<script setup lang="ts">
import {ref} from "vue";

import useStore from "@src/store/store";

import {
  BellIcon,
  ChatBubbleOvalLeftIcon,
  Cog6ToothIcon,
  MoonIcon,
  PhoneIcon,
  SunIcon,
  UserIcon,
} from "@heroicons/vue/24/solid";
import AccountDropdown from "@src/components/views/HomeView/Navigation/AccountDropdown.vue";
import Logo from "@src/components/views/HomeView/Navigation/Logo.vue";
import NavLink from "@src/components/views/HomeView/Navigation/NavLink.vue";
import {defaultSettings} from "@src/store/defaults";
import userProfileService from "@src/services/user-profile-service";

const store = useStore();
const preferredLanguage = store.settings.preferredLanguage || defaultSettings.preferredLanguage

const showDropdown = ref(false);

// (event) change the active sidebar component when clicking on a NavLink
const handleActiveSidebarComponentChange = (value: string) => {
  store.activeSidebarComponent = value;
};

const darkMode = () => {
  store.settings.darkMode = !store.settings.darkMode
  userProfileService.updateUserProfileSettings()
}
</script>

<template>
  <div class="xs:w-full md:w-11 md:h-full md:py-7 xs:py-5 px-5 flex xs:flex-row md:flex-col items-center transition-all duration-500">
    <!--logo-->
    <Logo />

    <!--main navigation-->
    <div class="grow">
      <nav :aria-label="$t('navigation.main.label')">
        <ul class="xs:flex md:block xs:justify-between xs:items-center">
          <!--message button-->
          <li>
            <NavLink :icon="ChatBubbleOvalLeftIcon"
              :title="$t('navigation.conversations.title')"
              @click="() => handleActiveSidebarComponentChange('messages')"
              :active="store.activeSidebarComponent === 'messages'"/>
          </li>

          <!--contacts list button-->
          <li>
            <NavLink :icon="UserIcon"
             :title="$t('navigation.contacts.title')"
              @click="() => handleActiveSidebarComponentChange('contacts')"
              :active="store.activeSidebarComponent === 'contacts'" />
          </li>

          <!--dropdown button small screen-->
          <li>
            <AccountDropdown
              id="small-profile-menu"
              class="xs:block md:hidden"
              aria-labelledby="small-profile-menu-button"
              :show-dropdown="showDropdown"
              :handle-show-dropdown="() => (showDropdown = true)"
              :handle-close-dropdown="() => (showDropdown = false)" />
          </li>

          <!--notifications button-->
          <li class="xs:hidden md:inline">
            <NavLink :icon="BellIcon"
              :title="$t('navigation.notifications.title')"
              :notifications="3"
              @click="() => handleActiveSidebarComponentChange('notifications')"
              :active="store.activeSidebarComponent === 'notifications'"
            />
          </li>

          <!--voice call button-->
          <li>
            <NavLink :icon="PhoneIcon"
              :title="$t('navigation.call.log.title')"
              @click="() => handleActiveSidebarComponentChange('phone')"
              :active="store.activeSidebarComponent === 'phone'"
            />
          </li>

          <!--settings button small screen-->
          <li class="xs:inline md:hidden">
            <NavLink :icon="Cog6ToothIcon"
              :title="$t('navigation.settings.title')"
              @click="() => handleActiveSidebarComponentChange('settings')"
              :active="store.activeSidebarComponent === 'settings'"
            />
          </li>
        </ul>
      </nav>
    </div>

    <!--secondary navigation-->
    <div>
      <nav :aria-label="$t('secondary.navigation.aria-label')" class="xs:hidden md:block">
        <ul>
          <!--toggle dark mode button-->
          <li>
            <NavLink :icon="store.settings.darkMode ? SunIcon : MoonIcon"
              :title="$t('secondary.dark-mode.button.title')"
              @click="darkMode"/>
          </li>
          <!--settings button-->
          <li>
            <NavLink :icon="Cog6ToothIcon"
              :title="$t('secondary.settings.button.title')"
              @click="() => handleActiveSidebarComponentChange('settings')"
              :active="store.activeSidebarComponent === 'settings'"
            />
          </li>
        </ul>
      </nav>

      <!--separator-->
      <hr class="xs:hidden md:block mb-6 border-gray-100 dark:border-gray-600" />

      <!--user avatar-->
      <AccountDropdown id="profile-menu" class="xs:hidden md:block" aria-labelledby="profile-menu-button"
        :show-dropdown="showDropdown"
        :handle-show-dropdown="() => (showDropdown = true)"
        :handle-close-dropdown="() => (showDropdown = false)" />
    </div>
  </div>
</template>
