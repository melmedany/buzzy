<script setup lang="ts">
import Typography from "@src/components/ui/data-display/Typography.vue";
import {useI18n} from "vue-i18n";
import {dayFormat, isSameDay} from "@src/utils";

const props = defineProps<{
  date: Date;
}>();

const i18n = useI18n();

const prettyDate = () => {
  const today = new Date();
  const yesterday = new Date()
  yesterday.setDate(today.getDate() - 1)

  if (isSameDay(props.date, today)) {
    return i18n.t('conversations.timeline.divider.today')
  } else if (isSameDay(props.date, yesterday)) {
    return i18n.t('conversations.timeline.divider.yesterday')
  } else {
    return dayFormat(props.date, 'l')
  }

}

</script>

<template>
  <div class="w-full my-7 flex items-center justify-center">
    <div class="w-full h-0 border-t border-dashed dark:border-gray-600 dark:bg-opacity-0"></div>

    <Typography variant="body-4" class="mx-5"> {{ prettyDate() }}</Typography>

    <div class="w-full h-0 border-t border-dashed dark:border-gray-600 dark:bg-opacity-0"></div>
  </div>
</template>
