<script setup lang="ts">
import Button from "@src/components/ui/inputs/Button.vue";
import TextInput from "@src/components/ui/inputs/TextInput.vue";
import {SignupForm, SignupFormValidation} from "@src/types";
import {useI18n} from "vue-i18n";

const props = defineProps<{
  signup: SignupForm,
  signupFormValidation: SignupFormValidation
}>();

const i18n = useI18n();

const validateUsername = (username: string) => {
  if (!username) {
    return
  }

  if (username.length < 3 || username.length > 50) {
    props.signupFormValidation.usernameErrorMessage = [i18n.t("validation.signup.username")];
  } else {
    props.signupFormValidation.usernameErrorMessage = [];
  }
}

const validateFirstname = (firstname: string) => {
  if (!firstname) {
    return
  }

  if (firstname.length < 3 || firstname.length > 100) {
    props.signupFormValidation.firstnameErrorMessage = [i18n.t("validation.signup.lastname")];
  } else {
    props.signupFormValidation.firstnameErrorMessage = [];
  }
}

const validateLastname = (lastname: string) => {
  if (!lastname) {
    return
  }

  if (lastname && lastname.length < 3 || lastname.length > 100) {
    props.signupFormValidation.lastnameErrorMessage = [i18n.t("validation.signup.lastname")];
  } else {
    props.signupFormValidation.lastnameErrorMessage = [];
  }
}

defineEmits(['active-section-change']);
</script>

<template>
  <div>
    <!--form-->
    <div class="mb-5">
      <TextInput :label="$t('signup.username.label')"
                 :placeholder="$t('signup.username.placeholder')" class="mb-5"
                 :handle-blur="(event) => validateUsername(event.target.value)"
                 :error-messages="props.signupFormValidation.usernameErrorMessage"
                 :value="props.signup?.username"
                 @value-changed="(value) => (props.signup.username = value)"/>
      <TextInput :label="$t('signup.firstname.label')"
                 :placeholder="$t('signup.firstname.placeholder')" class="mb-5"
                 :handle-blur="(event) => validateFirstname(event.target.value)"
                 :error-messages="props.signupFormValidation.firstnameErrorMessage"
                 :value="props.signup?.firstname"
                 @value-changed="(value) => (props.signup.firstname = value)"/>
      <TextInput :label="$t('signup.lastname.label')"
                 :placeholder="$t('signup.lastname.placeholder')" class="mb-5"
                 :handle-blur="(event) => validateLastname(event.target.value)"
                 :error-messages="props.signupFormValidation.lastnameErrorMessage"
                 :value="props.signup?.lastname"
                 @value-changed="(value) => (props.signup.lastname = value)"/>
    </div>

    <!--local controls-->
    <div class="mb-6">
      <Button class="w-full mb-4"
        @click="$emit('active-section-change', {sectionName: 'password-section',animationName: 'slide-left'})">
        {{ $t("signup.next.button.label") }}
      </Button>
    </div>
  </div>
</template>
