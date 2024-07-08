<script setup lang="ts">
import useStore from "@src/store/store";

import {ArrowLeftOnRectangleIcon, ArrowPathIcon, InformationCircleIcon,} from "@heroicons/vue/24/outline";
import Dropdown from "@src/components/ui/navigation/Dropdown/Dropdown.vue";
import DropdownLink from "@src/components/ui/navigation/Dropdown/DropdownLink.vue";
import {RouterLink} from "vue-router";
import router from "@src/router";
import authServerClient from "@src/clients/auth-server-client";
import {getAvatar, getName} from "@src/utils";
import defaultGroupAvatar from "@src/assets/vectors/avatar-group-default.png";
import defaultUserAvatar from "@src/assets/vectors/avatar-user-default.png";

const props = defineProps<{
  showDropdown: boolean;
  handleCloseDropdown: () => void;
  handleShowDropdown: () => void;
  id: string;
}>();

const store = useStore();

// (event) close dropdown menu when clicking outside
const handleCloseOnClickOutside = (event: Event) => {
  if (
    !["user-avatar", "profile-menu-button"].includes(
      (event.target as HTMLButtonElement).id
    )
  ) {
    props.handleCloseDropdown();
  }
};

const handleLogout = async () => {
  authServerClient.logout(store.tokens!!.accessToken).then(() => {
    store.$reset();
    router.push({path: "/access/sign-in/"})
        .then(() => location.reload());
  });
};
</script>

<template>
  <div class="relative">
    <!--toggle dropdown button-->
    <button :id="props.id + '-button'"
      @click="handleShowDropdown"
      class="bg-white rounded-full active:scale-110 focus:outline-none focus:scale-110 transition duration-200 ease-out"
      :style="{
        'box-shadow': !store.settings.darkMode
          ? '0 .125rem .3125rem rgba(193, 202, 255, 0.5),.125rem 0 .3125rem rgba(193, 202, 255, 0.5),-0.125rem 0 .3125rem rgba(193, 202, 255, 0.5),0 -0.125rem .3125rem rgba(193, 202, 255, 0.5)'
          : '0 .125rem .3125rem rgba(0, 70, 128, 0.5),.125rem 0 .3125rem rgba(0, 70, 128, 0.5),-0.125rem 0 .3125rem rgba(0, 70, 128, 0.5),0 -0.125rem .3125rem rgba(0, 70, 128, 0.5)',
      }"
      :aria-expanded="showDropdown"
      aria-controls="profile-menu"
      aria-label="toggle profile menu">
      <div id="user-avatar" :style="{ backgroundImage: `url(${store.user?.avatar})` }"
        class="w-7 h-7 rounded-full bg-cover bg-center" v-if="store.user?.avatar"></div>
      <div id="user-avatar" :style="{ backgroundImage: `url(${defaultGroupAvatar})` }"
        class="w-7 h-7 rounded-full bg-cover bg-center" v-else></div>
    </button>

    <!--dropdown menu-->
    <Dropdown :id="props.id + '-dropdown'"
      :aria-labelledby="props.id + '-button'"
      :show="props.showDropdown"
      :position="[
        'md:bottom-0',
        'md:left-[2.5rem]',
        'md:top-[auto]',
        'bottom-[3.125rem]',
        'left-[-4.8125rem]',
      ]"
      :handle-click-outside="handleCloseOnClickOutside"
      :close-dropdown="props.handleCloseDropdown">
      <DropdownLink
        label="Profile Information"
        :handle-click="props.handleCloseDropdown"
        tabindex="0">
        <InformationCircleIcon
          class="h-5 w-5 mr-3 text-black opacity-60 dark:text-white dark:opacity-70"/>
        {{ $t('dropdown.profile.information.label') }}
      </DropdownLink>

      <DropdownLink label="Password Change" :handle-click="props.handleCloseDropdown">
        <RouterLink to="/reset/" class="w-full flex items-center justify-start">
          <ArrowPathIcon class="h-5 w-5 mr-3 text-black opacity-60 dark:text-white dark:opacity-70"/>
          {{ $t("dropdown.password.change.label") }}
        </RouterLink>
      </DropdownLink>

      <DropdownLink :label="$t('dropdown.logout.button.label')"
        :handle-click="props.handleCloseDropdown"
        @click="handleLogout" color="danger">
        <RouterLink to="/access/sign-in/" class="w-full flex items-center justify-start">
          <ArrowLeftOnRectangleIcon class="h-5 w-5 mr-3" />
          {{ $t('dropdown.logout.button.label') }}
        </RouterLink>
      </DropdownLink>
    </Dropdown>
  </div>
</template>
