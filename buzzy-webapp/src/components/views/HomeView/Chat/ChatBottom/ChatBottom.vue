<script setup lang="ts">
import type {Ref} from "vue";
import {inject, onMounted, ref} from "vue";
import {IConversation, MessageType, PostMessage} from "@src/types";
import useStore from "@src/store/store";
import {getConversationIndex, textMessage} from "@src/utils";

import {
  CheckIcon,
  FaceSmileIcon,
  MicrophoneIcon,
  PaperAirplaneIcon,
  PaperClipIcon,
  XCircleIcon,
} from "@heroicons/vue/24/outline";
import AttachmentsModal from "@src/components/shared/modals/AttachmentsModal/AttachmentsModal.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import Button from "@src/components/ui/inputs/Button.vue";
import IconButton from "@src/components/ui/inputs/IconButton.vue";
import ScaleTransition from "@src/components/ui/transitions/ScaleTransition.vue";
import ReplyMessage from "@src/components/views/HomeView/Chat/ChatBottom/ReplyMessage.vue";
import EmojiPicker from "@src/components/ui/inputs/EmojiPicker/EmojiPicker.vue";
import Textarea from "@src/components/ui/inputs/Textarea.vue";
import conversationsService from "@src/services/conversations-service";

const store = useStore();

const activeConversation = <IConversation>inject("activeConversation");

// the content of the message.
const value: Ref<string> = ref("");

// determines whether the app is recording or not.
const recording = ref(false);

// open emoji picker.
const showPicker = ref(false);

// open modal used to send multiple attachments attachments.
const openAttachmentsModal = ref(false);

// start and stop recording.
const handleToggleRecording = () => {
  recording.value = !recording.value;
};

// stop recording without sending.
const handleCancelRecording = () => {
  recording.value = false;
};

// close picker when you click outside.
const handleClickOutside = (event: Event) => {
  let target = event.target as HTMLElement;
  let parent = target.parentElement as HTMLElement;

  if (
    target &&
    !target.classList.contains("toggle-picker-button") &&
    parent &&
    !parent.classList.contains("toggle-picker-button")
  ) {
    showPicker.value = false;
  }
};

// (event) set the draft message equals the content of the text area
const handleSetDraft = () => {
  const index = getConversationIndex(activeConversation?.id);
  if (index !== undefined) {
    store.conversations[index].draftMessage = value.value;
  }
};

const handleSendMessage = async () => {
  const index = getConversationIndex(activeConversation?.id);
  if (index !== undefined) {
    const draftMessage = store.conversations[index].draftMessage

    if (draftMessage && draftMessage.trim()) {
      activeConversation.messages.push(textMessage(draftMessage, store.user!!));

      const postMessage: PostMessage = { type: MessageType.text, message: draftMessage };
      await conversationsService.postMessage(postMessage, activeConversation.id);
      await updateActiveConversation();
      store.conversations[index].draftMessage = value.value = '';
      handleSetDraft();
    }
  }
}

const updateActiveConversation = async () => {
  const conversation = await conversationsService.getConversation(activeConversation?.id);

  let index = getConversationIndex(activeConversation?.id)

  if (index !== undefined) {
    store.conversations[index] = conversation!!;
    activeConversation.messages = conversation!!.messages!!;
  }
}

onMounted(() => {
  value.value = activeConversation?.draftMessage;
});
</script>

<template>
  <div class="w-full">
    <!--selected reply display-->
    <div
      class="relative transition-all duration-200"
      :class="{ 'pt-[3.75rem]': activeConversation?.replyMessage }">
      <ReplyMessage />
    </div>

    <div
      class="h-auto min-h-[5.25rem] p-5 flex items-end"
      v-if="store.status !== 'loading'"
      :class="recording ? ['justify-between'] : []">
      <div class="min-h-[2.75rem]">
        <!--select attachments button-->
        <IconButton :title="$t('conversations.chat.open.attachments.modal.title')"
          :aria-label="$t('conversations.chat.open.attachments.modal.aria-label')"
          @click="openAttachmentsModal = true"
          v-if="!recording"
          class="group w-7 h-7 md:mr-5 xs:mr-4">
          <PaperClipIcon class="w-[1.25rem] h-[1.25rem] text-gray-400 group-hover:text-indigo-300" />
        </IconButton>

        <!--recording timer-->
        <Typography v-if="recording" variant="body-1" no-color class="text-indigo-300">00:11</Typography>
      </div>

      <!--message textarea-->
      <div class="grow md:mr-5 xs:mr-4 self-end" v-if="!recording">
        <div class="relative">
          <Textarea v-model="value" @input="handleSetDraft" :value="value"
            class="max-h-[5rem] pr-[3.125rem] resize-none scrollbar-hidden"
            auto-resize cols="30" rows="1"
            :placeholder="$t('conversations.chat.textarea.title')"
            :aria-label="$t('conversations.chat.textarea.aria-label')" />

          <!--emojis-->
          <div class="absolute bottom-[.8125rem] right-0">
            <!--emoji button-->
            <IconButton :title="$t('conversations.chat.emoji.picker.title')"
              :aria-label="$t('conversations.chat.emoji.picker.aria-label')"
              @click="showPicker = !showPicker"
              class="toggle-picker-button group w-7 h-7 md:mr-5 xs:mr-4" >
              <XCircleIcon v-if="showPicker" class="w-[1.25rem] h-[1.25rem] text-gray-400 group-hover:text-indigo-300" />
              <FaceSmileIcon v-else class="w-[1.25rem] h-[1.25rem] text-gray-400 group-hover:text-indigo-300" />
            </IconButton>

            <!--emoji picker-->
            <ScaleTransition>
              <div v-click-outside="handleClickOutside" v-show="showPicker"
                class="absolute z-10 bottom-[3.4375rem] md:right-0 xs:right-[-5rem] mt-2">
                <div role="none">
                  <EmojiPicker :show="showPicker" />
                </div>
              </div>
            </ScaleTransition>
          </div>
        </div>
      </div>

      <div class="min-h-[2.75rem]">
        <!--cancel recording button-->
        <div v-if="recording" @click="handleCancelRecording">
          <Button variant="ghost" color="danger"> Cancel </Button>
        </div>
      </div>

      <div class="min-h-[2.75rem] flex">
        <!--finish recording button-->
        <IconButton :title="$t('conversations.chat.finish.recording.title')"
          :aria-label="$t('conversations.chat.finish.recording.aria-label')"
          v-if="recording"
          @click="handleToggleRecording"
          class="relative group w-7 h-7 flex justify-center items-center outline-none rounded-full bg-indigo-300 hover:bg-green-300 dark:hover:bg-green-400 dark:focus:bg-green-400 focus:outline-none transition-all duration-200">
          <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-indigo-300 group-hover:bg-green-300 opacity-40" />

          <MicrophoneIcon class="w-[1.25rem] h-[1.25rem] text-white group-hover:hidden" />
          <CheckIcon class="w-[1.25rem] h-[1.25rem] hidden text-white group-hover:block" />
        </IconButton>

        <!--start recording button-->
        <IconButton v-else @click="handleToggleRecording"
          :title="$t('conversations.chat.start.recording.title')"
          :aria-label="$t('conversations.chat.start.recording.aria-label')"
          class="group w-7 h-7 md:mr-5 xs:mr-4" >
          <MicrophoneIcon class="w-[1.25rem] h-[1.25rem] text-gray-400 group-hover:text-indigo-300" />
        </IconButton>

        <!--send message button-->
        <IconButton v-if="!recording"
          @click="handleSendMessage"
          class="group w-7 h-7 bg-indigo-300 hover:bg-indigo-400 focus:bg-indigo-400 dark:focus:bg-indigo-300 dark:bg-indigo-400 dark:hover:bg-indigo-400 active:scale-110"
          variant="ghost"
          :title="$t('conversations.chat.send.message.title')"
          :aria-label="$t('conversations.chat.send.message.aria-label')" >
          <PaperAirplaneIcon class="w-[1.0625rem] h-[1.0625rem] text-white" />
        </IconButton>
      </div>
    </div>

    <AttachmentsModal :open="openAttachmentsModal" :close-modal="() => (openAttachmentsModal = false)" />
  </div>
</template>

<style>
input[placeholder="Search emoji"] {
  background: rgba(0, 0, 0, 0);
}

.v3-emoji-picker .v3-header {
  border-bottom: 0;
}

.v3-emoji-picker .v3-footer {
  border-top: 0;
}
</style>
