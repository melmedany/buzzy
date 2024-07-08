<script setup lang="ts">
import Typography from "@src/components/ui/data-display/Typography.vue";
import SelectOption from "@src/components/ui/inputs/SelectOption.vue";

const props = defineProps<{
  id: string;
  value: string;
  title: string;
  description: string;
  options: any;
  handleValueChanged: (value: string) => any;
}>();
</script>

<template>
  <div class="w-full flex flex-col">
    <div class="flex">
      <!--label-->
      <div class="grow">
        <label :for="props.id"
          class="inline-block outline-none mb-4"
          tabindex="0">
          <Typography variant="heading-2" class="w-13">
            {{ props.title }}
          </Typography>
        </label>
      </div>

      <!--select-->
      <SelectOption :id="props.id" :value="props.value" :options="options"
        @value-changed="(value) => props.handleValueChanged(value)" />
    </div>
    <!--description or help text-->
    <Typography variant="body-2" class="help-block outline-none" tabindex="0">
      {{ props.description }}
    </Typography>
  </div>
</template>
