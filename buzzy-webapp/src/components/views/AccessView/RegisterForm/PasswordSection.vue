<script setup lang="ts">
import {ref} from "vue";

import {EyeIcon, EyeSlashIcon} from "@heroicons/vue/24/outline";
import IconButton from "@src/components/ui/inputs/IconButton.vue";
import TextInput from "@src/components/ui/inputs/TextInput.vue";
import Button from "@src/components/ui/inputs/Button.vue";
import {SignupForm, SignupFormValidation} from "@src/types";
import {useI18n} from "vue-i18n";

const props = defineProps<{
  signup: SignupForm,
  handleSignUp: () => void,
  signupFormValidation: SignupFormValidation,
  signupFormSubmitted: boolean
}>();

const i18n = useI18n();

const passwordConstraintsPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?":{}|<>])(?=.{8,20})/;

const validatePassword = (password: string) => {
  if (!password) {
    return
  }

  if (password && !passwordConstraintsPattern.test(password)) {
    props.signupFormValidation.passwordErrorMessage = [i18n.t("validation.signup.password")];
  } else {
    props.signupFormValidation.passwordErrorMessage = [];
  }

  validateConfirmPasswordPassword(props.signup.confirmPassword);
}

const validateConfirmPasswordPassword = (confirmPassword: string) => {
  if (!confirmPassword || !props.signup.password) {
    return
  }

  if (!passwordConstraintsPattern.test(confirmPassword)) {
    props.signupFormValidation.confirmPasswordErrorMessage = [i18n.t("validation.signup.password")];
  } else {
    props.signupFormValidation.confirmPasswordErrorMessage = [];
  }

  if (props.signup.password !== confirmPassword) {
    if (props.signupFormValidation.confirmPasswordErrorMessage.length > 0) {
      props.signupFormValidation.confirmPasswordErrorMessage.push(i18n.t("validation.signup.passwords.not.matching"));
    } else {
      props.signupFormValidation.confirmPasswordErrorMessage= [i18n.t("validation.signup.passwords.not.matching")];
    }
  } else {
    props.signupFormValidation.confirmPasswordErrorMessage = [];
  }
}

const disabled = (): boolean => {
  if (props.signupFormSubmitted) {
    return true;
  }

  const hasValue = !!props.signup.username.trim() &&
      !!props.signup.firstname.trim() &&
      !!props.signup.lastname.trim() &&
      !!props.signup.password.trim() &&
      !!props.signup.confirmPassword.trim();

  const hasErrors = props.signupFormValidation.usernameErrorMessage.length > 0 ||
      props.signupFormValidation.firstnameErrorMessage.length > 0 ||
      props.signupFormValidation.lastnameErrorMessage.length > 0 ||
      props.signupFormValidation.passwordErrorMessage.length > 0 ||
      props.signupFormValidation.confirmPasswordErrorMessage.length > 0;

  return !hasValue || hasErrors;
}

defineEmits(['active-section-change']);

const showPassword = ref(false);
const showPasswordConfirm = ref(false);
</script>

<template>
  <div>
    <div class="mb-5">
      <!--form-->
      <TextInput :label="$t('signup.password.label')"
        :placeholder="$t('signup.password.placeholder')"
        :type="showPassword ? 'text' : 'password'"
        class="pr-[2.5rem] mb-5"
        :value="props.signup?.password"
        :handle-blur="(event) => validatePassword(event.target.value)"
        :error-messages="props.signupFormValidation.passwordErrorMessage"
        @value-changed="(value) => (props.signup.password = value)">
        <template v-slot:endAdornment>
          <IconButton :title="$t('signup.password.show.button.title')"
            :aria-label="$t('signup.password.show.button.title')"
            class="m-[.5rem] p-2"
            @click="showPassword = !showPassword">
            <EyeSlashIcon v-if="showPassword" class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60"/>
            <EyeIcon v-else class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60"/>
          </IconButton>
        </template>
      </TextInput>

      <TextInput :label="$t('signup.confirm.password.label')"
        :placeholder="$t('signup.confirm.password.placeholder')"
        :type="showPasswordConfirm ? 'text' : 'password'"
        :value="props.signup?.confirmPassword"
        :handle-blur="(event) => validateConfirmPasswordPassword(event.target.value)"
        :error-messages="props.signupFormValidation.confirmPasswordErrorMessage"
        @value-changed="(value) => (props.signup.confirmPassword = value)">
        <template v-slot:endAdornment>
          <IconButton :title="$t('signup.password.show.button.title')"
            :placeholder="$t('signup.confirm.password.placeholder')"
            class="m-[.5rem] p-2"
            @click="showPasswordConfirm = !showPasswordConfirm">
            <EyeSlashIcon v-if="showPasswordConfirm" class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60"/>
            <EyeIcon v-else class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60"/>
          </IconButton>
        </template>
      </TextInput>
    </div>

    <!--controls-->
    <div class="mb-5">
      <Button class="w-full mb-4" @click="props.handleSignUp" :loading="props.signupFormSubmitted"
      :loadingTypography="$t('signup.loading.button.label')" :disabled="disabled()">
        {{ $t("signup.submit.button.label") }}
      </Button>
      <Button variant="outlined" class="w-full"
        @click="$emit('active-section-change', {sectionName: 'personal-section', animationName: 'slide-right'})">
        {{ $t("signup.back.button.label") }}
      </Button>
    </div>
  </div>
</template>
