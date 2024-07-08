<script setup lang="ts">
import {ArrowUturnLeftIcon} from "@heroicons/vue/24/solid";
import {computed, ref} from "vue";

import {IContact, IConversation} from "@src/types";
import {getAvatar, getFullName, getName, getOddContact, timeAgo} from "@src/utils";

import {
  ArrowLeftOnRectangleIcon,
  AtSymbolIcon,
  BellIcon,
  NoSymbolIcon,
  PencilIcon,
  ShareIcon,
  TrashIcon,
  UserIcon,
} from "@heroicons/vue/24/outline";
import InfoItem from "@src/components/shared/blocks/InfoItem.vue";
import ImageViewer from "@src/components/shared/modals/ConversationInfoModal/ImageViewer.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import Button from "@src/components/ui/inputs/Button.vue";
import IconButton from "@src/components/ui/inputs/IconButton.vue";

const props = defineProps<{
  conversation: IConversation;
  contact?: IContact;
  closeModal: () => void;
}>();

const openImageViewer = ref(false);

const imageUrl = computed(() => {
  if (props.contact) {
    return props.contact.avatar;
  } else {
    return getAvatar(props.conversation);
  }
});

</script>

<template>
  <div>
    <div class="mb-6 px-5 flex justify-between items-center">
      <!--title-->
      <Typography variant="heading-1"
                  id="modal-title"
                  class="default-outline"
                  tabindex="0">
        <span v-if="conversation?.type === 'direct_message' || props.contact">
          {{ $t('conversation.info.modal.contacts.title') }}
        </span>
        <span v-else-if="conversation?.type === 'group'">{{ $t('conversation.info.modal.group.title') }}</span>
        <span v-else-if="conversation?.type === 'broadcast'">{{ $t('conversation.info.modal.broadcast.title') }}</span>
        {{ $t('conversation.info.modal.title') }}
      </Typography>

      <!--close button-->
      <Button v-if="!props.contact" @click="props.closeModal" variant="outlined"
              color="danger" typography="body-4">
        {{ $t('conversation.info.modal.close.button.title') }}
      </Button>

      <!--return button-->
      <button v-else @click="
          $emit('active-page-change', {
            tabName: 'members',
            animationName: 'slide-right',
          })
        "
              class="group p-2 border rounded-full border-gray-200 dark:border-white dark:border-opacity-70 focus:outline-none focus:border-indigo-100 focus:bg-indigo-100 hover:bg-indigo-100 hover:border-indigo-100 dark:hover:border-indigo-400 dark:hover:bg-indigo-400 dark:focus:bg-reindigod-400 dark:focus:border-indigo-400 transition-all duration-200 outline-none">
        <ArrowUturnLeftIcon
            class="w-5 h-5 text-black opacity-100 dark:text-white dark:opacity-70 group-hover:text-indigo-500 group-hover:opacity-100 dark:group-hover:text-white"/>
      </button>
    </div>

    <!--top-->
    <div class="w-full p-5 pb-6">
      <div class="flex">
        <!--avatar-->
        <div class="mr-5">
          <button v-if="props.contact" @click="openImageViewer = true" class="outline-none"
                  :aria-label="$t('conversation.info.image.viewer.aria-label')">
            <div :style="{ backgroundImage: `url(${props.contact.avatar})` }"
                 class="w-[2.375rem] h-[2.375rem] rounded-full bg-cover bg-center"></div>
          </button>

          <button v-else @click="openImageViewer = true" class="outline-none"
                  :aria-label="$t('conversation.info.image.viewer.aria-label')">
            <div :style="{ backgroundImage: `url(${getAvatar(props.conversation)})`}"
                 class="w-[2.375rem] h-[2.375rem] rounded-full bg-cover bg-center"></div>
          </button>
        </div>

        <!--name-->
        <div class="w-full flex justify-between">
          <div>
            <Typography variant="heading-2" class="mb-3 mr-5 text-start">
              <span v-if="props.contact">
                {{ getFullName(props.contact) }}
              </span>

              <span v-else>
                {{ getName(props.conversation) }}
              </span>
            </Typography>

            <Typography variant="body-2" class="font-extralight text-start">
              <!--last seen-->
              <span v-if="conversation?.type === 'direct_message' || props.contact">
                {{ timeAgo(props.contact?.lastSeen) }}
              </span>

              <!--or number of group members-->
              <span v-else-if="['group', 'broadcast'].includes(conversation?.type)">
                {{ conversation.contacts.length }}
                {{ $t('conversation.info.modal.contacts.title') }}
              </span>
            </Typography>
          </div>

          <IconButton $title="$t('conversation.info.modal.edit.group.button.title')"
                      v-if="['group', 'broadcast'].includes(conversation?.type)"
                      class="group w-7 h-7">
            <PencilIcon class="w-5 h-5 text-gray-400 group-hover:text-indigo-300"
                        @click="
                $emit('active-page-change', {
                  tabName: 'edit-group',
                  animationName: 'slide-left',
                }) "/>
          </IconButton>
        </div>
      </div>
    </div>

    <!--middle-->
    <div class="w-full py-5 border-t border-gray-100 dark:border-gray-700">
      <!--(contact) email-->
      <div v-if="conversation?.type === 'direct_message' || props.contact"
           class="flex px-5 pb-5 items-center">
        <InfoItem :icon="AtSymbolIcon" :title="getOddContact(props.conversation)?.username"/>
      </div>

      <!--(group) members-->
      <div v-if="['group', 'broadcast'].includes(conversation?.type) && !props.contact"
           class="px-5 flex items-center pb-5">
        <InfoItem :icon="UserIcon" :title="$t('conversation.info.modal.group.user.icon.title')" link chevron
                  @click="$emit('active-page-change', { tabName: 'members', animationName: 'slide-left'})"/>
      </div>

      <!--(both) notifications-->
      <div class="px-5 flex items-center">
        <InfoItem :icon="BellIcon" :title="$t('conversation.info.modal.notification.title')" switch/>
      </div>

      <!--media-->
      <div class="px-5 pt-5 flex items-center">
        <InfoItem :icon="ShareIcon" :title="$t('conversation.info.modal.shared.media.title')" link chevron
                  @click="$emit('active-page-change', { tabName: 'shared-media', animationName: 'slide-left' })"/>
      </div>
    </div>

    <!--bottom-->
    <div class="w-full border-t border-gray-100 dark:border-gray-700">
      <!--block contact-->
      <div v-if="conversation?.type === 'direct_message' || props.contact" class="px-5 pt-5 group">
        <InfoItem :icon="NoSymbolIcon" :title="$t('conversation.info.modal.block.contact.title')" link color="danger"/>
      </div>

      <!--delete contact-->
      <div v-if="conversation?.type === 'direct_message' || props.contact" class="px-5 pt-5 group">
        <InfoItem :icon="TrashIcon" :title="$t('conversation.info.modal.delete.contact.title')" link color="danger"/>
      </div>

      <!--exit group-->
      <div v-if="['group', 'broadcast'].includes(conversation?.type) && !props.contact"
           class="px-5 pt-5 flex items-center group">
        <InfoItem :icon="ArrowLeftOnRectangleIcon" :title="$t('conversation.info.modal.exit.group.title')" link
                  color="danger"/>
      </div>
    </div>

    <!--image viewer-->
    <ImageViewer :image-url="imageUrl" :open="openImageViewer" :close-image="() => (openImageViewer = false)"/>
  </div>
</template>
