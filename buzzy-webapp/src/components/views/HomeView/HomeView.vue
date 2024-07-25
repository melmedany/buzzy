<script setup lang="ts">
import FadeTransition from "@src/components/ui/transitions/FadeTransition.vue";
import Navigation from "@src/components/views/HomeView/Navigation/Navigation.vue";
import Sidebar from "@src/components/views/HomeView/Sidebar/Sidebar.vue";
import {getActiveConversationId, getConversationIndex} from "@src/utils";
import {onMounted} from "vue";
import useStore from "@src/store/store";
import webSocketService from "@src/services/web-socket-service";
import conversationsService from "@src/services/conversations-service";

const store = useStore();

const handleMessage = async (message: any) => {
  console.debug("HomeView.handleMessage");
  const messageBody = JSON.parse(message.body);
  if (messageBody?.conversationId) {

    const index = getConversationIndex(messageBody?.conversationId);

    const conversation = await conversationsService.getConversation(messageBody?.conversationId);

    if (index !== undefined) {
      store.conversations[index] = conversation!!
    } else {
      store.conversations.push(conversation!!)
    }
  }
}

onMounted(async () => {
  await webSocketService.subscribeToUserUpdates((message: any) => handleMessage(message))
});

</script>

<template>
  <KeepAlive>
    <div class="xs:relative md:static h-full flex xs:flex-col md:flex-row overflow-hidden">
      <!--navigation-bar-->
      <Navigation class="xs:order-1 md:-order-none" />
      <!--sidebar-->
      <Sidebar class="xs:grow-1 md:grow-0 xs:overflow-y-scroll md:overflow-visible scrollbar-hidden"/>
      <!--chat-->
      <div id="mainContent"
        class="xs:absolute xs:z-10 md:static grow h-full xs:w-full md:w-fit scrollbar-hidden bg-white dark:bg-gray-800 transition-all duration-500"
        :class="getActiveConversationId()? ['xs:left-[0rem]', 'xs:static'] : ['xs:left-[62.5rem]']"
        role="region">
        <router-view v-slot="{ Component }">
          <FadeTransition name="fade" mode="out-in">
            <component :is="Component" :key="getActiveConversationId()" />
          </FadeTransition>
        </router-view>
      </div>
    </div>
  </KeepAlive>
</template>
