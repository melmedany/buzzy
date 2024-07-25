<script setup lang="ts">
import {onBeforeMount, onMounted, onUnmounted, ref} from "vue";

import useStore from "@src/store/store";
import moment from 'moment'

import FadeTransition from "@src/components/ui/transitions/FadeTransition.vue";
import {setLocale} from "@src/utils";

const store = useStore();

// update localStorage with state changes
store.$subscribe((_mutation, state) => {
  localStorage.setItem("chat", JSON.stringify(state));
});

onBeforeMount(async () => {
  setLocale(store.settings!!.preferredLanguage)
  moment.locale(store.settings!!.preferredLanguage)
})

// here we load the data from the server.
onMounted(async () => {
  store.status = "loading";
});

// the app height
const height = ref(`${window.innerHeight}px`);

// change the app height to the window hight.
const resizeWindow = () => {
  height.value = `${window.innerHeight}px`;
}

// and add the resize event when the component mounts.
onMounted(() => {
  window.addEventListener("resize", resizeWindow);
});

// remove the event when un-mounting the component.
onUnmounted(() => {
  window.removeEventListener("resize", resizeWindow);
});
</script>

<template>
  <div :class="{ dark: store.settings.darkMode }">
    <div
      class="bg-white dark:bg-gray-800 transition-colors duration-500"
      :style="{ height: height }">
      <router-view v-slot="{ Component }">
        <FadeTransition>
          <component :is="Component" />
        </FadeTransition>
      </router-view>
    </div>
  </div>
</template>
