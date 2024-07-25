<script setup lang="ts">
import useStore from "@src/store/store";

import AccordionButton from "@src/components/ui/data-display/AccordionButton.vue";
import Collapse from "@src/components/ui/utils/Collapse.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import {useI18n} from "vue-i18n";
import SettingsSelect from "@src/components/views/HomeView/Sidebar/Settings/SettingsAccordion/SettingsSelect.vue";
import {defaultSettings} from "@src/store/defaults";
import userProfileService from "@src/services/user-profile-service";

const i18n = useI18n();

const props = defineProps<{
  collapsed: boolean;
  handleToggle: () => void;
}>();

const store = useStore();
const preferredLanguage = store.settings.preferredLanguage || defaultSettings.preferredLanguage

const languages = [{name: i18n.t('languages.dutch'), value: "nl"}, {name: i18n.t('languages.english'), value: "en"}];

const handleLanguageChange = (value: string) => {
  if (value && value !== store.settings.preferredLanguage){
    const confirmed = confirm(i18n.t('localization.settings.select-language.confirmation.message'));

    if (confirmed) {
      store.settings.preferredLanguage = value;
      userProfileService.updateUserProfileSettings().then(() => location.reload());
    }
  }
}

</script>

<template>
  <!--appearance settings-->
  <AccordionButton
    id="localization-settings-toggler"
    class="w-full flex px-5 py-6 mb-3 rounded focus:outline-none"
    :collapsed="props.collapsed"
    chevron
    aria-controls="localization-settings-collapse"
    @click="props.handleToggle()">
    <Typography variant="heading-2" class="mb-4"> {{ $t("localization.settings.link.title") }} </Typography>
    <Typography variant="body-2"> {{ $t("localization.settings.link.description") }} </Typography>
  </AccordionButton>

  <Collapse id="localization-settings-collapse" :collapsed="props.collapsed">
    <SettingsSelect :title="$t('localization.settings.select-language.title')"
        :description="$t('localization.settings.select-language.description')"
        :options="languages"
        :id="'localization-settings-select'"
        :value="store.settings.preferredLanguage"
        :handle-value-changed="(value) => handleLanguageChange(value)"
        class="mb-7"/>

<!--    @change="handleLanguageSelect"-->
  </Collapse>
</template>
