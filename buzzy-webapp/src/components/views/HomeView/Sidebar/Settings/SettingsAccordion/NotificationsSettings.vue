<script setup lang="ts">
import useStore from "@src/store/store";

import AccordionButton from "@src/components/ui/data-display/AccordionButton.vue";
import Collapse from "@src/components/ui/utils/Collapse.vue";
import SettingsSwitch from "@src/components/views/HomeView/Sidebar/Settings/SettingsAccordion/SettingsSwitch.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import {defaultSettings} from "@src/store/defaults";
import userProfileService from "@src/services/user-profile-service";

const props = defineProps<{
  collapsed: boolean;
  handleToggle: () => void;
}>();

const store = useStore();
const preferredLanguage = store.settings.preferredLanguage || defaultSettings.preferredLanguage

const allowNotifications = () => {
  store.settings.allowNotifications = !store.settings.allowNotifications
  userProfileService.updateUserProfileSettings()
}
const keepNotifications = () => {
  store.settings.keepNotifications = !store.settings.keepNotifications
  userProfileService.updateUserProfileSettings()
}
</script>

<template>
  <!--notifications settings-->
  <AccordionButton
    id="notifications-settings-toggler"
    class="w-full flex px-5 py-6 mb-3 rounded focus:outline-none"
    :collapsed="props.collapsed"
    chevron
    aria-controls="notifications-settings-collapse"
    @click="props.handleToggle()">
    <Typography variant="heading-2" class="mb-4"> {{ $t('notifications.settings.link.title') }} </Typography>
    <Typography variant="body-2"> {{ $t('notifications.settings.link.description') }} </Typography>
  </AccordionButton>

  <Collapse id="notifications-settings-collapse" :collapsed="props.collapsed">
    <SettingsSwitch :title="$t('notifications.settings.allow-notifications.title')"
      :description="$t('notifications.settings.allow-notifications.description')"
      :id="'notifications-settings-allow-notifications'" @click="allowNotifications"
      :value="store.settings.allowNotifications"
      :handle-toggle-switch="
        (value) => (store.settings.allowNotifications = value)
      "
      class="mb-7"/>
    <SettingsSwitch :title="$t('notifications.settings.keep-notifications.title')"
      :description="$t('notifications.settings.keep-notifications.description')"
      :id="'notifications-settings-keep-notifications'" @click="keepNotifications"
      :value="store.settings.keepNotifications"
      :handle-toggle-switch="
        (value) => (store.settings.keepNotifications = value)
      "
      class="mb-7"
    />
  </Collapse>
</template>
