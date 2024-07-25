<script setup lang="ts">
import {IConversation, IMessage, MessageUpdate} from "@src/types";
import {inject, onMounted, onUnmounted, Ref, ref, watch} from "vue";

import useStore from "@src/store/store";

import Message from "@src/components/views/HomeView/Chat/ChatMiddle/Message/Message.vue";
import TimelineDivider from "@src/components/views/HomeView/Chat/ChatMiddle/TimelineDivider.vue";
import {getConversationIndex, getMessageIndex, isSameDay} from "@src/utils";
import webSocketService from "@src/services/web-socket-service";
import conversationsService from "@src/services/conversations-service";

const props = defineProps<{
  handleSelectMessage: (messageId: string) => void;
  handleDeselectMessage: (messageId: string) => void;
  selectedMessages: string[];
}>();

const store = useStore();
const container: Ref<HTMLElement | null> = ref(null);

const activeConversation = <IConversation>inject("activeConversation");

watch([activeConversation.messages], () => {
  scrollTop()
});

// checks whether the previous message was sent by the same user.
const isFollowUp = (index: number, previousIndex: number): boolean => {
  if (previousIndex < 0) {
    return false;
  } else {
    let previousSender = activeConversation?.messages[previousIndex].sender.id;
    let currentSender = activeConversation?.messages[index].sender.id;
    return previousSender === currentSender;
  }
};

// checks whether the message is sent by the authenticated user.
const isSelf = (message: IMessage): boolean => {
  return Boolean(store.user && message.sender.id === store.user.id);
};

// checks wether the new message has been sent in a new day or not.
const renderDivider = (index: number): boolean => {
  const currentMessage = activeConversation?.messages[index];
  const nextMessage = activeConversation?.messages[index + 1];

  if (!nextMessage) {
    return false;
  }

  const currentDate = currentMessage?.date
  const nextDate = nextMessage.date

  return !isSameDay(currentDate, nextDate);
};

const timeDividerDate = (index: number): Date => {
  const nextMessage = activeConversation?.messages[index + 1];
  return nextMessage.date;
};

const messageReceived = async (messageEvent: any) => {
  const messageUpdate: MessageUpdate = JSON.parse(messageEvent.body);

  if (!messageUpdate || !messageUpdate.conversationId || !messageUpdate.conversationId) {
    return;
  }

  const conversationIndex = getConversationIndex(messageUpdate.conversationId);
  if (conversationIndex === undefined) {
    return;
  }

  const message = await conversationsService.getConversationMessage(messageUpdate.messageId, messageUpdate.conversationId);

  const messageIndex = getMessageIndex(messageUpdate.messageId, messageUpdate.conversationId);

  if (messageIndex === undefined) {
    // store.conversations[conversationIndex].messages.push(message!);
    activeConversation.messages.push(message!);
  } else {
    store.conversations[conversationIndex].messages[messageIndex] = message!;
    activeConversation.messages[messageIndex] = message!;
  }

  if (messageUpdate.state !== 'read') {
    webSocketService.messageStateUpdate(messageUpdate.messageId, messageUpdate.conversationId);
  }
};


const reloadActiveConversation = async () => {
  const conversationIndex = getConversationIndex(activeConversation.id);
  if (conversationIndex === undefined) {
    console.log(`Conversation ${activeConversation.id} index not found.`);
    return;
  }

  const conversation = await conversationsService.getConversation(activeConversation.id);

  store.conversations[conversationIndex] = conversation!;
  activeConversation.messages = conversation!.messages!;
}

const scrollTop = () => {
  (container.value as HTMLElement).scrollTop = (container.value as HTMLElement).scrollHeight;
}

onMounted(async () => {
  console.log("ChatMiddle.onMounted()");
  await webSocketService.subscribeToConversation(activeConversation.id,
      (message: any) => messageReceived(message).then(() => scrollTop()))
  await reloadActiveConversation().then(() => scrollTop())
      .then(() => {
        let minDate: Date = activeConversation.messages[0].date;
        let messageId = null;
        let sender: string | undefined = store.user?.username

        for (const conversationMessage of activeConversation.messages) {
          const unreadMessage = conversationMessage.state !== 'read' &&
              conversationMessage.sender.username === sender &&
              conversationMessage.date < minDate;
          if (unreadMessage) {
              minDate = conversationMessage.date;
              messageId = conversationMessage.id;
          }
        }

        if (messageId !== null) {
          webSocketService.bulkMessageStateUpdate(messageId, activeConversation.id);
        }
      });
});

onUnmounted(async () => {
  console.log("ChatMiddle.onUnmounted");
  await webSocketService.unSubscribeFromConversation(activeConversation.id)
});

</script>

<template>
  <div ref="container" class="grow px-5 py-5 flex flex-col overflow-y-scroll scrollbar-hidden">
    <div v-if="store.status !== 'loading'" v-for="(message, index) in activeConversation?.messages" :key="index">

      <TimelineDivider id="TimelineDivider" v-if="index === 0" :date="message?.date"/>

      <Message :message="message"
               :self="isSelf(message)"
               :follow-up="isFollowUp(index, index - 1)"
               :divider="renderDivider(index)"
               :selected="props.selectedMessages.includes(message.id)"
               :handle-select-message="handleSelectMessage"
               :handle-deselect-message="handleDeselectMessage"/>

      <TimelineDivider v-if="renderDivider(index)" :date="timeDividerDate(index)"/>

    </div>
  </div>
</template>
