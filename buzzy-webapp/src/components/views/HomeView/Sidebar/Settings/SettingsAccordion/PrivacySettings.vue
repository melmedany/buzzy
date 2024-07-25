<script setup lang="ts">
import useStore from "@src/store/store";

import AccordionButton from "@src/components/ui/data-display/AccordionButton.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import Collapse from "@src/components/ui/utils/Collapse.vue";
import SettingsSwitch from "@src/components/views/HomeView/Sidebar/Settings/SettingsAccordion/SettingsSwitch.vue";
import {defaultSettings} from "@src/store/defaults";
import userProfileService from "@src/services/user-profile-service";

// Variables
const props = defineProps<{
  collapsed: boolean;
  handleToggle: () => void;
}>();

const store = useStore();
const preferredLanguage = store.settings.preferredLanguage || defaultSettings.preferredLanguage
const lastSeen = () => {
  store.settings.lastSeen = !store.settings.lastSeen
  userProfileService.updateUserProfileSettings()
}
const readReceipt = () => {
  store.settings.readReceipt = !store.settings.readReceipt
  userProfileService.updateUserProfileSettings()
}
const joiningGroups = () => {
  store.settings.joiningGroups = !store.settings.joiningGroups
  userProfileService.updateUserProfileSettings()
}
const privateMessages = () => {
  store.settings.privateMessages = !store.settings.privateMessages
  userProfileService.updateUserProfileSettings()
}
</script>

<template>
  <!--privacy settings-->
  <AccordionButton
    id="privacy-settings-toggler"
    class="w-full flex px-5 py-6 mb-3 rounded focus:outline-none"
    :collapsed="props.collapsed"
    chevron
    aria-controls="privacy-settings-collapse"
    @click="props.handleToggle()">
    <Typography variant="heading-2" class="mb-4"> {{ $t('privacy.settings.link.title') }} </Typography>
    <Typography variant="body-2"> {{ $t('privacy.settings.link.description') }}</Typography>
  </AccordionButton>

  <Collapse id="privacy-settings-collapse" :collapsed="props.collapsed">
    <SettingsSwitch :title="$t('privacy.settings.last-seen.title')"
      :description="$t('privacy.settings.last-seen.description')"
      :id="'privacy-settings-allow-last-seen'" @click="lastSeen"
      :value="store.settings.lastSeen"
      :handle-toggle-switch="(value:boolean) => (store.settings.lastSeen = value)"
      class="mb-7"/>
    <SettingsSwitch :title="$t('privacy.settings.show-read-receipt.title')"
      :description="$t('privacy.settings.show-read-receipt.description')"
      :id="'privacy-settings-show-read-receipt'" @click="readReceipt"
      :value="store.settings.readReceipt"
      :handle-toggle-switch="
        (value:boolean) => (store.settings.readReceipt = value)
      "
      class="mb-7"/>
    <SettingsSwitch :title="$t('privacy.settings.joining-groups.title')"
      :description="$t('privacy.settings.joining-groups.description')"
      :id="'privacy-settings-joining-groups'" @click="joiningGroups"
      :value="store.settings.joiningGroups"
      :handle-toggle-switch="
        (value:boolean) => (store.settings.joiningGroups = value)
      "
      class="mb-7"/>
    <SettingsSwitch :title="$t('privacy.settings.private-messages.title')"
      :description="$t('privacy.settings.private-messages.description')"
      :id="'privacy-settings-private-messages'" @click="privateMessages"
      :value="store.settings.privateMessages"
      :handle-toggle-switch="
        (value:boolean) => (store.settings.privateMessages = value)
      "
      class="mb-7"/>
  </Collapse>
</template>
