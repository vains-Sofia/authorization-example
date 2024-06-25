<script setup lang="ts">
import { watch } from "vue";
import { useImageVerify } from "./hooks";

defineOptions({
  name: "ReImageVerify"
});

interface Props {
  code?: string;
  captchaId?: string;
}

interface Emits {
  (e: "update:code", code: string): void;
  (e: "update:captchaId", captchaId: string): void;
}

const props = withDefaults(defineProps<Props>(), {
  code: "",
  captchaId: ""
});

const emit = defineEmits<Emits>();

const { domRef, imgCode, captchaId, setImgCode, getImgCode } = useImageVerify();

watch(
  () => props.code,
  newValue => {
    setImgCode(newValue);
  }
);
watch(imgCode, newValue => {
  emit("update:code", newValue);
});

watch(captchaId, newValue => {
  emit("update:captchaId", newValue);
});

defineExpose({ getImgCode });
</script>

<template>
  <canvas
    ref="domRef"
    width="120"
    height="40"
    class="cursor-pointer"
    @click="getImgCode"
  />
</template>
