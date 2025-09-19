<script setup lang="ts">
import {computed, ref} from "vue";

const props = withDefaults(defineProps<{
  title: string; // 文本提示标题
  subTitleTip?: string; // 子内容提示
  okText?: string; // 确定按钮文本
  cancelText?: string;
  isDelete?: boolean; // 当前使用是否是移除
}>(), {
  isDelete: true, // 默认移除pop
  okText: '移除',
})
const emit = defineEmits<{ (e: 'ok'): void; }>();
const titleClass = computed(() => {
  return props.isDelete
      ? 'ml-2 font-medium text-[var(--color-text-1)] text-[14px]'
      : 'mb-[8px] font-medium text-[var(--color-text-1)] text-[14px] leading-[22px]';
});
const currentVisible = ref(false);
</script>

<template>
  <n-popconfirm v-model:show="currentVisible"
                :class="props.isDelete ? 'w-[352px]' : ''"
                :positive-text="props.okText || '确认'"
                :negative-text="props.cancelText || '取消'"
                @positive-click="emit('ok')"
                @negative-click="currentVisible = false">
    <template #trigger>
      <n-button text type="error" @click="currentVisible = true">移除</n-button>
    </template>
    <div class="flex-row flex-nowrap">
      <div :class="[titleClass]">
        {{ props.title || '' }}
      </div>
      <div v-if="props.subTitleTip" class="ml-8 mt-2 text-sm text-[var(--color-text-2)]">
        {{ props.subTitleTip }}
      </div>
      <n-form v-else>
        <n-input/>
      </n-form>
    </div>

  </n-popconfirm>
</template>

<style scoped>

</style>