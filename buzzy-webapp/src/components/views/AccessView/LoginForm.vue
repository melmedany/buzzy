<script setup lang="ts">
import {ref} from "vue";

import useStore from "@src/store/store";
import Typography from "@src/components/ui/data-display/Typography.vue";
import Button from "@src/components/ui/inputs/Button.vue";
import IconButton from "@src/components/ui/inputs/IconButton.vue";
import TextInput from "@src/components/ui/inputs/TextInput.vue";
import {EyeIcon, EyeSlashIcon} from "@heroicons/vue/24/outline";
import {RouterLink} from "vue-router";
import APICalls from "@src/APICalls";
import router from "@src/router";
import {addSeconds} from 'date-fns';


const showPassword = ref(false);

const backend = new APICalls('http://login.buzzy.io');

const store = useStore();
const signin = {username: "", password: "", grant_type: "grant_api"};

const handleSignIn = async () => {
  try {
    const headers = {
      Authorization: "Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
      "Content-Type": 'application/x-www-form-urlencoded'
    };
    const responseData = await backend.postData("/oauth2/token", headers, signin);
    console.log('Response type: {} value: {}', typeof(responseData), JSON.stringify(responseData));
    store.user!!.token = {accessToken: responseData.access_token, refreshToken: responseData.refresh_token, expiresAt: addSeconds(new Date(), responseData.expires_in)};
    router.push({path: "/chat/no-chat/"});
  } catch (error) {
    console.error('Error posting data:', error);
  }
};
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
        <Typography variant="heading-2" class="mb-4">Welcome back</Typography>
        <Typography variant="body-3" class="text-opacity-75 font-light">
          Create an account a start messaging now!
        </Typography>
      </div>

      <!--form-->
      <div class="mb-6">
        <TextInput label="Username" placeholder="Enter your username" class="mb-5"
                   :value="signin.username"
                   @value-changed="(value) => (signin.username = value)" />
        <TextInput
          label="Password"
          placeholder="Enter your password"
          :type="showPassword ? 'text' : 'password'"
          class="pr-[2.5rem]"
          :value="signin.password"
          @value-changed="(value) => (signin.password = value)"
        >
          <template v-slot:endAdornment>
            <IconButton
              title="toggle password visibility"
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
        <Button class="w-full mb-4" @click="handleSignIn">Sign in</Button>
      </div>

      <!--divider-->
<!--      <div class="mb-6 flex items-center">-->
<!--        <span-->
<!--          class="w-full border border-dashed border-gray-100 dark:border-gray-600 rounded-[.0625rem]"-->
<!--        ></span>-->
<!--        <Typography variant="body-3" class="px-4 text-opacity-75 font-light"-->
<!--          >or</Typography-->
<!--        >-->
<!--        <span-->
<!--          class="w-full border border-dashed border-gray-100 dark:border-gray-600 rounded-[.0625rem]"-->
<!--        ></span>-->
<!--      </div>-->

      <!--oauth controls-->
      <div>
<!--        <Button variant="outlined" class="w-full mb-5">-->
<!--          <span class="flex">-->
<!--            <img-->
<!--              src="@src/assets/vectors/google-logo.svg"-->
<!--              class="mr-3"-->
<!--              alt="google logo"-->
<!--            />-->
<!--            Sign in with google-->
<!--          </span>-->
<!--        </Button>-->

        <!--bottom text-->
        <div class="flex justify-center">
          <Typography variant="body-2"
            >Don’t have an account ?
            <RouterLink
              to="/access/sign-up/"
              class="text-indigo-400 opacity-100"
            >
              Sign up
            </RouterLink>
          </Typography>
        </div>
      </div>
    </div>
  </div>
</template>
