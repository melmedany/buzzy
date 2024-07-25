<script setup lang="ts">
import {ref} from "vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import Button from "@src/components/ui/inputs/Button.vue";
import IconButton from "@src/components/ui/inputs/IconButton.vue";
import TextInput from "@src/components/ui/inputs/TextInput.vue";
import {EyeIcon, EyeSlashIcon} from "@heroicons/vue/24/outline";
import {RouterLink} from "vue-router";
import router from "@src/router";
import authenticationService from "@src/services/authentication-service";
import {LoginForm, LoginFormValidation} from "@src/types";
import userProfileService from "@src/services/user-profile-service";
import {POSITION, useToast} from "vue-toastification";
import {useI18n} from "vue-i18n";
import {getApiErrorMessageKey} from "@src/utils";

const toast = useToast();
const i18n = useI18n();

const showPassword = ref(false);
const loginErrorToastId = "login-error-toast";
const loginSuccessToastId = "login-success-toast";

const loginForm: LoginForm = {
  username: "",
  password: "",
  $reset(): void {
    this.username = "";
    this.password = "";
  }
}

const loginFormValidation = ref<LoginFormValidation>({
  usernameErrorMessage: [],
  passwordErrorMessage: [],
  $reset(): void {
    this.usernameErrorMessage = [];
    this.passwordErrorMessage = [];
  }
});

const loginFormSubmitted = ref(false);

const validateUsername = (username: string) => {
  if (!username) {
    return
  }

  if (username.length < 3 || username.length > 50) {
    loginFormValidation.value.usernameErrorMessage = [i18n.t("validation.login.username")];
  } else {
    loginFormValidation.value.usernameErrorMessage = [];
  }
}

const validatePassword = (password: string) => {
  if (!password) {
    return
  }

  if (password.length < 8 || password.length > 20) {
    loginFormValidation.value.passwordErrorMessage = [i18n.t("validation.login.password")];
  } else {
    loginFormValidation.value.passwordErrorMessage = [];
  }
}

const handleLogin = async () => {
  loginFormSubmitted.value = true;

  const loginResponse = await authenticationService.login(loginForm);

  if (typeof loginResponse === "boolean" && loginResponse) {
    const userProfileResponse = await userProfileService.getUserProfile();

    if (typeof userProfileResponse === "string") {
      handleErrorResponse(userProfileResponse);
      return;
    }

    loginFormSubmitted.value = false;
    loginForm.$reset();
    loginFormValidation.value.$reset();

    const successMessage = i18n.t('login.successful.toast.text');
    toast.dismiss(loginErrorToastId);
    toast.success(successMessage, {
      id: loginSuccessToastId,
      position: POSITION.TOP_CENTER,
      timeout: 2000,
      closeOnClick: false,
      pauseOnFocusLoss: false,
      pauseOnHover: false,
      draggable: false,
      showCloseButtonOnHover: true,
      hideProgressBar: false,
      closeButton: false,
      icon: "fa-duotone fa-solid fa-check fa-beat-fade",
      onClose() {
        router.push({path: "/chat/"}).then(() => location.reload());
      },
    });
    return;
  }

  handleErrorResponse(loginResponse);
}

const handleErrorResponse = (errorResponse: any) => {
  let messageKey
  if (typeof errorResponse === "string") {
    messageKey = getApiErrorMessageKey(errorResponse);

  } else {
    messageKey = 'errors.global.error';
  }

  const errorMessage = i18n.t(messageKey);

  loginFormSubmitted.value = false;

  toast.dismiss(loginErrorToastId);
  toast.error(errorMessage, {
    id: loginErrorToastId,
    position: POSITION.TOP_CENTER,
    timeout: false,
    closeOnClick: false,
    pauseOnFocusLoss: false,
    pauseOnHover: false,
    draggable: false,
    showCloseButtonOnHover: true,
    hideProgressBar: false,
    closeButton: "button",
    icon: "fa-duotone fa-solid fa-circle-xmark",
  });
  return;
}

const disabled = (): boolean => {
  if (loginFormSubmitted.value) {
    return true;
  }

  const hasValue = !!loginForm.username.trim() && !!loginForm.password.trim()
  const hasErrors = loginFormValidation.value.usernameErrorMessage.length > 0 ||
      loginFormValidation.value.passwordErrorMessage.length > 0;

  return !hasValue || hasErrors;
}

</script>

<template>
  <div class="p-5 md:basis-1/2 xs:basis-full flex flex-col justify-center items-center">
    <div class="w-full md:px-[26%] xs:px-[10%]">
      <!--header-->
      <div class="mb-6 flex flex-col">
        <img src="@src/assets/vectors/logo-gradient.svg"
            class="w-[1.375rem] h-[1.125rem] mb-4 opacity-70"
            alt="bird logo"/>
        <Typography variant="heading-2" class="mb-4">{{ $t("login.form.welcome.back") }}</Typography>
        <Typography variant="body-3" class="text-opacity-75 font-light">
          {{ $t("login.form.opening.message") }}
        </Typography>
      </div>

      <div class="mb-6">
        <TextInput :label="$t('login.username.label')"
                   :placeholder="$t('login.username.placeholder')" class="mb-5"
                   :value="loginForm.username"
                   @value-changed="(value) => (loginForm.username = value)"
                   :handle-blur="(event) => validateUsername(event.target.value)"
                   :error-messages="loginFormValidation.usernameErrorMessage" />
        <TextInput
            :label="$t('login.password.label')"
            :placeholder="$t('login.password.placeholder')"
            :type="showPassword ? 'text' : 'password'"
            class="pr-[2.5rem] mb-5"
            :value="loginForm.password"
            @value-changed="(value) => (loginForm.password = value)"
            :handle-blur="(event) => validatePassword(event.target.value)"
            :error-messages="loginFormValidation.passwordErrorMessage" >
          <template v-slot:endAdornment>
            <IconButton
                :title="$t('login.password.show.button.title')"
                aria-label="toggle password visibility"
                class="m-[.5rem] p-2"
                @click="showPassword = !showPassword">
              <EyeSlashIcon v-if="showPassword" class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60" />
              <EyeIcon v-else class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60" />
            </IconButton>
          </template>
        </TextInput>
      </div>

      <!--local controls-->
      <div class="mb-6">
        <Button class="w-full mb-4" @click="handleLogin" :disabled="disabled()" :loading="loginFormSubmitted"
                :loadingTypography="$t('login.loading.button.label')">{{ $t("login.button.label") }}</Button>
      </div>

      <div>
        <!--bottom text-->
        <div class="flex justify-center">
          <Typography variant="body-2">{{ $t("login.signup.message") }}
            <RouterLink
                to="/access/sign-up/"
                class="text-indigo-400 opacity-100">
              {{ $t("login.signup.button.label") }}
            </RouterLink>
          </Typography>
        </div>
      </div>
    </div>
  </div>
</template>
