<script setup lang="ts">
import {computed, ref} from "vue";
import {RouterLink} from "vue-router";

import SlideTransition from "@src/components/ui/transitions/SlideTransition.vue";
import Typography from "@src/components/ui/data-display/Typography.vue";
import PasswordSection from "@src/components/views/AccessView/RegisterForm/PasswordSection.vue";
import PersonalSection from "@src/components/views/AccessView/RegisterForm/PersonalSection.vue";
import APICalls from "@src/APICalls";

defineEmits(["activeSectionChange"]);

// determines what form section to use.
const activeSectionName = ref("personal-section");

// determines what direction the slide animation should use.
const animation = ref("slide-left");

// get the active section component from the section name
const ActiveSection = computed((): any => {
  if (activeSectionName.value === "personal-section") {
    return PersonalSection;
  } else if (activeSectionName.value === "password-section") {
    return PasswordSection;
  }
});

// (event) to move between modal pages
const changeActiveSection = (event: {
  sectionName: string;
  animationName: string;
}) => {
  animation.value = event.animationName;
  activeSectionName.value = event.sectionName;
};

const backend = new APICalls('http://login.buzzy.io');
const signup = {username: "", firstname: "", lastname: "", password: "", confirmPassword: "", fullname: ""};

const handleSignUp = async () => {
  try {
    const headers = {
      Authorization: 'Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=',
    };

    // TODO // verify signup data
    const responseData = await backend.postData("/v1/signup", headers, signup);
    console.log('Response from post:', responseData);
  } catch (error) {
    console.error('Error posting data:', error);
  }
};
</script>

<template>
  <div
    class="p-5 md:basis-1/2 xs:basis-full flex flex-col justify-center items-center">
    <div class="w-full md:px-[26%] xs:px-[10%]">
      <!--header-->
      <div class="mb-6 flex flex-col">
        <img src="@src/assets/vectors/logo-gradient.svg"
          class="w-[1.375rem] h-[1.125rem] mb-5 opacity-70"/>
        <Typography variant="heading-2" class="mb-4">Get started with Avian</Typography>
        <Typography variant="body-3" class="text-opacity-75 font-light">
          Sign in to start using messaging!
        </Typography>
      </div>

      <!--form section-->
      <SlideTransition :animation="animation">
        <component
          @active-section-change="changeActiveSection"
          :is="ActiveSection"
          :signup="signup"
          :handleSignUp="handleSignUp"
        />
      </SlideTransition>

      <!--bottom text-->
      <div class="flex justify-center">
        <Typography variant="body-2"
          >Already have an account ?
          <RouterLink to="/access/sign-in/" class="text-indigo-400 opacity-100">
            Sign in
          </RouterLink>
        </Typography>
      </div>
    </div>
  </div>
</template>
