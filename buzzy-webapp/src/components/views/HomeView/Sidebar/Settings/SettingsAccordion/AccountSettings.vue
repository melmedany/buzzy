<script setup lang="ts">
import { ref } from "vue";
import type { Ref } from "vue";

import useStore from "@src/store/store";

import AccordionButton from "@src/components/ui/data-display/AccordionButton.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import Collapse from "@src/components/ui/utils/Collapse.vue";
import TextInput from "@src/components/ui/inputs/TextInput.vue";
import DropFileUpload from "@src/components/ui/inputs/DropFileUpload.vue";
import Button from "@src/components/ui/inputs/Button.vue";

// Types
interface AccountValues {
  username: string | undefined;
  firstname: string | undefined;
  lastname: string | undefined;
  avatar: File | undefined;
}

// Variables
const props = defineProps<{
  collapsed: boolean;
  handleToggle: () => void;
}>();

const store = useStore();

const accountValues: Ref<AccountValues> = ref({
  username: store.user?.username,
  firstname: store.user?.firstname,
  lastname: store.user?.lastname,
  avatar: undefined,
});

const loading = ref(false);

// (event) handle submitting the values of the form.
const handleSubmit = () => {
  loading.value = true;

  store.$patch({
    user: {
      ...store.user,
      username: accountValues.value.username,
      firstname: accountValues.value.firstname,
      lastname: accountValues.value.lastname,
    },
  });

  setTimeout(() => {
    loading.value = false;
  }, 2000);
};
</script>

<template>
  <!--account settings-->
  <AccordionButton
    id="account-settings-toggler"
    class="w-full flex px-5 py-6 mb-3 rounded focus:outline-none"
    :collapsed="props.collapsed"
    chevron
    aria-controls="account-settings-collapse"
    @click="handleToggle()">
    <Typography variant="heading-2" class="mb-4"> {{$t("account.settings.link.description")}} </Typography>
    <Typography variant="body-2"> {{ $t("account.settings.link.description") }}</Typography>
  </AccordionButton>

  <Collapse id="account-settings-collapse" :collapsed="props.collapsed">
    <TextInput :label="$t('account.settings.username.label')"
               class="mb-7"
               :value="accountValues?.username"
               :disabled="true"
    />
    <TextInput :label="$t('account.settings.firstname.label')"
      class="mb-7"
      :value="accountValues?.firstname"
      @value-changed="(value) => (accountValues.firstname = value)"/>
    <TextInput :label="$t('account.settings.lastname.label')"
      class="mb-7"
      :value="accountValues?.lastname"
      @value-changed="(value) => (accountValues.lastname = value)"
    />
    <DropFileUpload :label="$t('account.settings.avatar.label')"
      class="mb-7" accept="image/*"
      :value="accountValues.avatar"
      @value-changed="(value) => (accountValues.avatar = value)"
    />
    <Button class="w-full py-4" @click="handleSubmit" :loading="loading">
      {{ $t("account.settings.save.button.label") }}
    </Button>
  </Collapse>
</template>
