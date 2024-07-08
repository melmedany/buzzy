<script setup lang="ts">
import {ref} from "vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import Button from "@src/components/ui/inputs/Button.vue";
import IconButton from "@src/components/ui/inputs/IconButton.vue";
import TextInput from "@src/components/ui/inputs/TextInput.vue";
import {EyeIcon, EyeSlashIcon} from "@heroicons/vue/24/outline";
import {RouterLink} from "vue-router";
import router from "@src/router";
import {addSeconds} from "date-fns";
import authServerClient from "@src/clients/auth-server-client";
import useStore from "@src/store/store";
import {SigninForm} from "@src/types";
import {defaultSettings} from "@src/store/defaults";
import userProfileClient from "@src/clients/user-profile-client";

const store = useStore();

const showPassword = ref(false);

const signinForm: SigninForm = {
  username: "",
  password: "",
  grant_type: "grant_api"
};

const handleSignIn = async () => {
  const response = await authServerClient.tokens(signinForm);

  if (response?.data) {
    store.tokens = {
      accessToken: response?.data!!.access_token,
      refreshToken: response?.data!!.refresh_token,
      expiresAt: addSeconds(new Date(), response?.data!!.expires_in)
    };
    await postSignInSuccess();
  } else {
    // todo handel error
  }
};

const postSignInSuccess = async () => {
  if (!store.user && store.tokens) {
    const preferredLanguage = store.settings?.preferredLanguage || defaultSettings.preferredLanguage;
    const response = await userProfileClient.getUserProfile(store.tokens.accessToken, preferredLanguage);

    if (response?.errors) {
      // todo handel error
      console.log(response?.errors);
    } else {
      store.$patch({
        status: "success",
        user: response?.data || undefined,
        settings: response?.data?.settings || defaultSettings,
        notifications: [],
        archivedConversations: [],
        delayLoading: false
      });

      await router.push({path: "/chat/"})
          .then(() => location.reload());
    }
  } else {
    // todo // handle missing tokens
  }
}
</script>

<template>
  <div
      class="p-5 md:basis-1/2 xs:basis-full flex flex-col justify-center items-center"
  >
    <div class="w-full md:px-[26%] xs:px-[10%]">
      <!--header-->
      <div class="mb-6 flex flex-col">
        <img
            src="@src/assets/vectors/logo-gradient.svg"
            class="w-[1.375rem] h-[1.125rem] mb-4 opacity-70"
            alt="bird logo"
        />
        <Typography variant="heading-2" class="mb-4">{{ $t("login.form.welcome.back") }}</Typography>
        <Typography variant="body-3" class="text-opacity-75 font-light">
          {{ $t("login.form.opening.message") }}
        </Typography>
      </div>

      <!--form-->
      <div class="mb-6">
        <TextInput :label="$t('login.username.label')"
                   :placeholder="$t('login.username.placeholder')" class="mb-5"
                   :value="signinForm.username"
                   @value-changed="(value) => (signinForm.username = value)"/>
        <TextInput
            :label="$t('login.password.label')"
            :placeholder="$t('login.password.placeholder')"
            :type="showPassword ? 'text' : 'password'"
            class="pr-[2.5rem]"
            :value="signinForm.password"
            @value-changed="(value) => (signinForm.password = value)"
        >
          <template v-slot:endAdornment>
            <IconButton
                :title="$t('login.password.show.button.title')"
                aria-label="toggle password visibility"
                class="m-[.5rem] p-2"
                @click="showPassword = !showPassword"
            >
              <EyeSlashIcon
                  v-if="showPassword"
                  class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60"
              />
              <EyeIcon
                  v-else
                  class="w-5 h-5 text-black opacity-50 dark:text-white dark:opacity-60"
              />
            </IconButton>
          </template>
        </TextInput>
      </div>

      <!--local controls-->
      <div class="mb-6">
        <Button class="w-full mb-4" @click="handleSignIn">{{ $t("login.button.label") }}</Button>
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
