<script setup lang="ts">
const props = defineProps<{
  id?: string;
  label?: string;
  value: string;
  options: any;
}>();

</script>

<template>
  <div role="list" class="relative flex select-none outline-none transition-all duration-200 ease-in" tabindex="0">
    <select :id="props.id"  :name="props.label"
            class="w-full h-full p-2 border outline-none rounded-sm text-black dark:text-white dark:opacity-70 placeholder:text-black placeholder:opacity-40 dark:placeholder:text-white dark:placeholder:opacity-70 focus:outline-none focus:ring focus:ring-indigo-100 duration-200 transition ease-out text-opacity-70 border-none bg-gray-50 dark:bg-gray-700 focus:bg-white dark:focus:bg-gray-800"
        @change="$emit('valueChanged', ($event.target as HTMLInputElement).value)">
      <option disabled>{{ $t('localization.settings.select-language.select.language') }}</option>
      <option v-for="option in props.options" :value="option.value" :selected="option.value === props.value"
        class="block w-7 h-5 rounded-full outline-none cursor-pointer"
      >
        {{ option.name }}
      </option>
    </select>
  </div>
</template>
