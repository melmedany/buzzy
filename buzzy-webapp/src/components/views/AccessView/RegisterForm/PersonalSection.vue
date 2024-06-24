<script setup lang="ts">
import Button from "@src/components/ui/inputs/Button.vue";
import TextInput from "@src/components/ui/inputs/TextInput.vue";

const props = defineProps({
  signup: {
    type: Object,
    required: true,
  }
});

defineEmits(['active-section-change']);
</script>

<template>
  <div>
    <!--form-->
    <div class="mb-5">
      <TextInput label="Username" placeholder="Enter your username" class="mb-5"
                 :value="props.signup?.username"
                 @value-changed="(value) => (props.signup.username = value)"/>
      <TextInput label="First Name" placeholder="Enter your first name" class="mb-5"
                 :value="props.signup?.firstname"
                 @value-changed="(value) => (props.signup.firstname = value)"/>
      <TextInput label="Last Name" placeholder="Enter your last name" class="mb-5"
                 :value="props.signup?.lastname"
                 @value-changed="(value) => (props.signup.lastname = value)"/>
    </div>

    <!--local controls-->
    <div class="mb-6">
      <Button
        class="w-full mb-4"
        @click="
          $emit('active-section-change', {
            sectionName: 'password-section',
            animationName: 'slide-left',
          })
        "
        >Next</Button
      >
    </div>
  </div>
</template>
