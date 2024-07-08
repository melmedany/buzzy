<script setup lang="ts">
import FadeTransition from "@src/components/ui/transitions/FadeTransition.vue";
import Navigation from "@src/components/views/HomeView/Navigation/Navigation.vue";
import Sidebar from "@src/components/views/HomeView/Sidebar/Sidebar.vue";
import {getActiveConversationId, getConversationIndex} from "@src/utils";
import {onMounted} from "vue";
import {connectToSocketSever} from "@src/app";
import useStore from "@src/store/store";
import webSocketClient from "@src/clients/web-socket-client";
import {defaultSettings} from "@src/store/defaults";
import conversationsClient from "@src/clients/conversations-client";

const store = useStore();
const socket = webSocketClient.getInstance();

const userDestination = "/user/{username}/";

const handleMessage = async (message: any) => {
  const messageBody = JSON.parse(message.body);
  if (messageBody?.conversationId) {

    const index = getConversationIndex(messageBody?.conversationId);

    if (store.tokens) {
      const preferredLanguage = store.settings?.preferredLanguage || defaultSettings.preferredLanguage;

      const response = await conversationsClient.getConversation(messageBody?.conversationId, store.tokens.accessToken, preferredLanguage);

      if (response?.errors) {
        // todo handel error
        console.log(response?.errors);
      }

      if (index !== undefined) {
        store.conversations[index] = response?.data!!
      } else {
        store.conversations.push(response?.data!!)
      }

      console.log(response?.data!!);

    } else {
      // todo // handel error
    }

  }
}

onMounted(async () => {
  if (store.tokens) {
    const destination = userDestination.replace("{username}", store.user!!.username)
    connectToSocketSever().then(() => {
      socket.subscribe(destination, (message: any) => handleMessage(message));
    })
  }
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
