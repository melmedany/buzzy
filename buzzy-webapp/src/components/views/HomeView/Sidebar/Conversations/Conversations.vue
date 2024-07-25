<script setup lang="ts">
import type {IContact, IConversation} from "@src/types";
import type {Ref} from "vue";
import {onMounted, ref, watch} from "vue";

import useStore from "@src/store/store";
import {getName} from "@src/utils";

import {PencilSquareIcon} from "@heroicons/vue/24/outline";
import ComposeModal from "@src/components/shared/modals/ComposeModal/ComposeModal.vue";
import NoConversation from "@src/components/states/empty-states/NoConversation.vue";
import Loading1 from "@src/components/states/loading-states/Loading1.vue";
import IconButton from "@src/components/ui/inputs/IconButton.vue";
import SearchInput from "@src/components/ui/inputs/SearchInput.vue";
import FadeTransition from "@src/components/ui/transitions/FadeTransition.vue";
import ArchivedButton from "@src/components/views/HomeView/Sidebar/Conversations/ArchivedButton.vue";
import ConversationsList from "@src/components/views/HomeView/Sidebar/Conversations/ConversationsList.vue";
import SidebarHeader from "@src/components/views/HomeView/Sidebar/SidebarHeader.vue";
import userProfileService from "@src/services/user-profile-service";
import conversationsService from "@src/services/conversations-service";

const store = useStore();

const keyword: Ref<string> = ref("");

const composeOpen = ref(false);

// determines whether the archive is open or not
const openArchive = ref(false);

// the filtered list of conversations.
const filteredConversations: Ref<IConversation[]> = ref(store.conversations);

// filter the list of conversation based on search text.
watch([keyword, openArchive], () => {
  if (openArchive.value) {
    // search conversations
    filteredConversations.value =
      store.archivedConversations?.filter((conversation) =>
        getName(conversation)
          ?.toLowerCase()
          .includes(keyword.value.toLowerCase())
      ) || [];
  } else {
    // search archived conversations
    filteredConversations.value =
      store.conversations?.filter((conversation) =>
        getName(conversation)
          ?.toLowerCase()
          .includes(keyword.value.toLowerCase())
      ) || [];
  }
});

// (event) close the compose modal.
const closeComposeModal = () => {
  composeOpen.value = false;
};

const searchKeywordChanged = async (keyword: string) => {
  if (keyword?.length >= 3) {
    await userProfileService.searchUserProfiles(keyword);
  }
};

const contactSelected = async (contact: IContact) => {
  if (contact) {
    const connectionAdded = await userProfileService.addConnection(contact.id);

    if (connectionAdded) {
      await updateConversations();
    }
  }
};

const updateConversations = async () => {
  await conversationsService.getConversationsSummary();
  filteredConversations.value = sort(store.conversations);
}

const sort = (conversations: IConversation[]): IConversation[] => {
  return conversations.sort((a, b) => {
    const latestMessageDateA = a.messages.length > 0 ? new Date(Math.max(...a.messages.map(m => new Date(m.date).getTime()))) : new Date(0);

    const latestMessageDateB = b.messages.length > 0 ? new Date(Math.max(...b.messages.map(m => new Date(m.date).getTime()))) : new Date(0);

    return latestMessageDateB.getTime() - latestMessageDateA.getTime();
  });
}

onMounted(async () => {
  await updateConversations();
  // if (!store.conversations || !store.conversations.length) {
  //   await updateConversations();
  // }
  store.$patch({
    status: "success",
    delayLoading: false
  });
});
</script>

<template>
  <div>
    <SidebarHeader>
      <!--title-->
      <template v-slot:title>{{ $t('side-bar.header.conversations.title') }}</template>

      <!--side actions-->
      <template v-slot:actions>
        <IconButton @click="composeOpen = true"
          :aria-label="$t('conversations.compose-conversation.button.title')"
          :title="$t('conversations.compose-conversation.button.title')" class="w-7 h-7">
          <PencilSquareIcon class="w-[1.25rem] h-[1.25rem] text-indigo-300 hover:text-indigo-400"/>
        </IconButton>
      </template>
    </SidebarHeader>

    <!--search bar-->
    <div class="px-5 xs:pb-6 md:pb-5">
      <SearchInput v-model="keyword" />
    </div>

    <!--conversations-->
    <div role="list"
     :aria-label="$t('conversations.compose-conversation.list.aria-label')"
      class="w-full h-full scroll-smooth scrollbar-hidden"
      style="overflow-x: visible; overflow-y: scroll">
      <Loading1 v-if="store.status === 'loading' || store.delayLoading" v-for="item in 6"/>

      <div v-else>
        <ArchivedButton v-if="store.archivedConversations.length > 0"
          :open="openArchive" @click="openArchive = !openArchive"/>

        <div v-if="store.status === 'success' && !store.delayLoading && filteredConversations.length > 0 ">
          <FadeTransition>
            <component :is="ConversationsList"
              :filtered-conversations="filteredConversations"
              :key="openArchive ? 'archive' : 'active'" />
          </FadeTransition>
        </div>

        <div v-else>
          <NoConversation v-if="store.archivedConversations.length === 0" />
        </div>
      </div>
    </div>

    <!--compose modal-->
    <ComposeModal :open="composeOpen" :close-modal="closeComposeModal"
                  :search-keyword-changed="(value) => searchKeywordChanged(value)"
                  :contact-selected="(value) => contactSelected(value)"/>
  </div>
</template>
