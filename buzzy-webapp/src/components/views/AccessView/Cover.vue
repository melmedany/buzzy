<script setup lang="ts">
import Typography from "@src/components/ui/data-display/Typography.vue";
import cloudPatternPath from "@src/assets/images/clouds.png";
import blurPath from "@src/assets/images/blur.png";
</script>

<template>
  <div class="py-[3.125rem] px-[3.125rem] h-full basis-1/2 md:flex xs:hidden items-center justify-center">
    <div class="w-full h-full px-7 flex items-center bg-no-repeat bg-center bg-cover rounded-[.75rem]"
      :style="{ 'background-image': `url(${cloudPatternPath})` }">
      <div class="px-9 py-[3.75rem] rounded-[.3125rem] bg-cover bg-no-repeat"
        :style="{ 'background-image': `url(${blurPath})` }">
        <Typography variant="display" class="mb-5 text-white" no-color>{{ $t("cover.opening.message") }}
          <span class="text-gradient">{{ $t("cover.opening.message.span") }}</span>
        </Typography>
        <Typography variant="body-3" class="text-white" no-color>
          {{ $t("cover.body.text") }}
        </Typography>
      </div>
    </div>
  </div>
</template>

<!-- Add this custom CSS to your project -->
<style scoped>
.text-gradient {
  background: -webkit-linear-gradient(left top, #de6dce, #e7ddee);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>
