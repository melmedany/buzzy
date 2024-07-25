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

const darkMode = () => {
  store.settings.darkMode = !store.settings.darkMode
  userProfileService.updateUserProfileSettings()
}

const borderedTheme = () => {
  store.settings.borderedTheme = !store.settings.borderedTheme
  userProfileService.updateUserProfileSettings()
}

</script>

<template>
  <!--appearance settings-->
  <AccordionButton
    id="appearance-settings-toggler"
    class="w-full flex px-5 py-6 mb-3 rounded focus:outline-none"
    :collapsed="props.collapsed"
    chevron
    aria-controls="appearance-settings-collapse"
    @click="props.handleToggle()">
    <Typography variant="heading-2" class="mb-4"> {{ $t("appearance.settings.link.title") }} </Typography>
    <Typography variant="body-2"> {{ $t("appearance.settings.link.description") }} </Typography>
  </AccordionButton>

  <Collapse id="appearance-settings-collapse" :collapsed="props.collapsed">
    <SettingsSwitch :title="$t('appearance.settings.dark-mode.title')"
      :description="$t('appearance.settings.dark-mode.description')"
      @click="darkMode" :id="'appearance-settings-dark-mode'"
      :value="store.settings.darkMode"
      :handle-toggle-switch="(value) => (store.settings.darkMode = value)"
      class="mb-7"/>
    <SettingsSwitch :title="$t('appearance.settings.bordered-theme.title')"
      :description="$t('appearance.bordered-theme.description')"
      @click="borderedTheme" :id="'appearance-settings-bordered-theme'"
      :value="store.settings.borderedTheme"
      :handle-toggle-switch="(value) => (store.settings.borderedTheme = value)"
      class="mb-7"/>
  </Collapse>
</template>
