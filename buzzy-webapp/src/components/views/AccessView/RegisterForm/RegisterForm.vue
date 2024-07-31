<script setup lang="ts">
import {computed, ref} from "vue";
import {RouterLink} from "vue-router";

import SlideTransition from "@src/components/ui/transitions/SlideTransition.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import PasswordSection from "@src/components/views/AccessView/RegisterForm/PasswordSection.vue";
import PersonalSection from "@src/components/views/AccessView/RegisterForm/PersonalSection.vue";
import router from "@src/router";
import {SignupForm, SignupFormValidation} from "@src/types";
import {POSITION, useToast} from 'vue-toastification'
import authenticationService from "@src/services/authentication-service";
import {useI18n} from "vue-i18n";
import {getApiErrorMessageKey} from "@src/utils";

defineEmits(["activeSectionChange"]);

const toast = useToast();
const i18n = useI18n();

const signUpErrorToastId = "signup-error-toast";
const signUpSuccessToastId = "signup-success-toast";

const activeSectionName = ref("personal-section");

const animation = ref("slide-left");

const ActiveSection = computed((): any => {
  if (activeSectionName.value === "personal-section") {
    return PersonalSection;
  } else if (activeSectionName.value === "password-section") {
    return PasswordSection;
  }
});

const changeActiveSection = (event: {
  sectionName: string;
  animationName: string;
}) => {
  animation.value = event.animationName;
  activeSectionName.value = event.sectionName;
};

const signupForm: SignupForm = {
  username: "",
  firstname: "",
  lastname: "",
  password: "",
  confirmPassword: "",
  $reset(): void {
    this.username = "";
    this.firstname = "";
    this.lastname = "";
    this.password = "";
    this.confirmPassword = "";
  }
};

const signupFormValidation = ref<SignupFormValidation>({
  usernameErrorMessage: [],
  firstnameErrorMessage: [],
  lastnameErrorMessage: [],
  passwordErrorMessage: [],
  confirmPasswordErrorMessage: [],
  $reset(): void {
    this.usernameErrorMessage = [];
    this.firstnameErrorMessage = [];
    this.lastnameErrorMessage = [];
    this.passwordErrorMessage = [];
    this.confirmPasswordErrorMessage = [];
  }
});

const signupFormSubmitted = ref(false);

const handleSignUp = async () => {
  signupFormSubmitted.value = true;

  const signedUpResponse = await authenticationService.signup(signupForm);

  const successMessage = i18n.t('signup.successful.toast.text');

  if (typeof signedUpResponse === "boolean" && signedUpResponse) {
    signupFormSubmitted.value = false;
    signupForm.$reset();
    signupFormValidation.value.$reset()

    toast.success(successMessage, {
      id: signUpSuccessToastId,
      position: POSITION.TOP_CENTER,
      timeout: 2000,
      closeOnClick: false,
      pauseOnFocusLoss: false,
      pauseOnHover: false,
      draggable: false,
      showCloseButtonOnHover: true,
      hideProgressBar: false,
      closeButton: false,
      icon: "fa-duotone fa-solid fa-check",
      onClose() {
        router.push({path: "/access/sign-in/"});
      },
    });
    return;
  }

  handleErrorResponse(signedUpResponse);

}

const handleErrorResponse = (errorResponse: any) => {
  let messageKey
  if (typeof errorResponse === "string") {
    messageKey = getApiErrorMessageKey(errorResponse);

  } else {
    messageKey = 'errors.global.error';
  }

  const errorMessage = i18n.t(messageKey);

  signupFormSubmitted.value = false;

  toast.dismiss(signUpErrorToastId);
  toast.error(errorMessage, {
    id: signUpErrorToastId,
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
</script>

<template>
  <div
      class="p-5 md:basis-1/2 xs:basis-full flex flex-col justify-center items-center">
    <div class="w-full md:px-[26%] xs:px-[10%]">
      <!--header-->
      <div class="mb-6 flex flex-col">
        <img src="@src/assets/vectors/logo-gradient.svg"
             class="w-[1.375rem] h-[1.125rem] mb-5 opacity-70" alt="buzzy"/>
        <Typography variant="heading-2" class="mb-4">{{ $t("signup.opening.message") }}</Typography>
        <Typography variant="body-3" class="text-opacity-75 font-light">
          {{ $t("signup.login.message") }}
        </Typography>
      </div>

      <!--form section-->
      <SlideTransition :animation="animation">
        <component @active-section-change="changeActiveSection"
                   :is="ActiveSection"
                   :signup="signupForm"
                   :handleSignUp="handleSignUp"
                   :signupFormSubmitted="signupFormSubmitted"
                   :signupFormValidation="signupFormValidation"/>
      </SlideTransition>

      <!--bottom text-->
      <div class="flex justify-center">
        <Typography variant="body-2">{{ $t("signup.already.have.account.message") }}
          <RouterLink to="/access/sign-in/" class="text-indigo-400 opacity-100">
            {{ $t("signup.login.button.label") }}
          </RouterLink>
        </Typography>
      </div>
    </div>
  </div>
</template>
