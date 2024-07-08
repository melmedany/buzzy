<script setup lang="ts">
import type {IConversation, IMessage, MessageBody, Messages} from "@src/types";
import {inject, onBeforeMount, onMounted, Ref, ref} from "vue";

import useStore from "@src/store/store";

import Message from "@src/components/views/HomeView/Chat/ChatMiddle/Message/Message.vue";
import TimelineDivider from "@src/components/views/HomeView/Chat/ChatMiddle/TimelineDivider.vue";
import {getConversationIndex, isSameDay} from "@src/utils";
import webSocketClient from "@src/clients/web-socket-client";
import {connectToSocketSever} from "@src/app";
import {defaultSettings} from "@src/store/defaults";
import conversationsClient from "@src/clients/conversations-client";

const props = defineProps<{
  handleSelectMessage: (messageId: string) => void;
  handleDeselectMessage: (messageId: string) => void;
  selectedMessages: string[];
}>();

const store = useStore();
const socket = webSocketClient.getInstance();

const container: Ref<HTMLElement | null> = ref(null);

const activeConversation = <IConversation>inject("activeConversation");

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

const conversationDestination = "/conversation/{conversationId}/user/{username}/";

if (store.tokens) {
  const destination = conversationDestination.replace("{conversationId}", activeConversation.id).replace("{username}", store.user!!.username)
  connectToSocketSever().then(() => {
    socket.subscribe(destination, (message: any) => {
          console.log(message)
          // const messageBody: MessageBody = JSON.parse(message.body);
          // updateMessages((prev: Messages) => {
          //   const friendsMessages = prev[activeConversation.id] || [];
          //   const newMessages = [...friendsMessages, messageBody];
          //   const newObj = { ...prev, [activeConversation.id]: newMessages };
          //   return newObj;
          // });
        }
    );

    const updateMessages = (messages: (prev: Messages) => { [p: string]: MessageBody[] }) => {
      console.log(messages)
    }
  })
}

onBeforeMount(async () => {
  const index = getConversationIndex(activeConversation.id);
  if (index !== undefined) {

    if (store.tokens) {
      const preferredLanguage = store.settings?.preferredLanguage || defaultSettings.preferredLanguage;

      const response = await conversationsClient.getConversation(activeConversation?.id, store.tokens.accessToken, preferredLanguage);

      if (response?.errors) {
        // todo handel error
        console.log(response?.errors);
      } else {
        store.conversations[index] = response?.data!!
        activeConversation.messages = response?.data!!.messages!!;
      }

    } else {
      // todo // handel error
    }

  } else {
    // todo // handle error
  }
})

const updateActiveConversation = async () => {


}

// scroll messages to bottom.
onMounted(async () => {
  (container.value as HTMLElement).scrollTop = (
      container.value as HTMLElement
  ).scrollHeight;
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
