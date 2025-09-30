<script setup lang="ts">
import type {ProtocolKeyEnumType} from "/@/enums/api-enum.ts";
import {ref} from "vue";

const props = defineProps<{
  activeFolder?: string; // 选中的节点
  folderName: string; // 名称
  allCount: number; // 总数
  showExpandApi?: boolean; // 展示 展开请求的开关
  notShowOperation?: boolean; // 是否展示操作按钮
  protocolKey: ProtocolKeyEnumType;
}>();
const emit = defineEmits<{
  (e: 'setActiveFolder', val: string): void;
  (e: 'changeApiExpand'): void;
  (e: 'selectedProtocolsChange'): void;
}>();
const isExpandAll = defineModel<boolean | undefined>('isExpandAll', {
  required: false,
  default: undefined,
});
const isExpandApi = defineModel<boolean>('isExpandApi', {
  required: false,
  default: undefined,
});
const selectedProtocols = ref<string[]>([]);
const changeApiExpand = () => {
  isExpandApi.value = !isExpandApi.value;
}
defineExpose({selectedProtocols})
</script>

<template>
  <folder-all v-model:is-expand-all="isExpandAll" :active-folder="props.activeFolder" :folder-name="props.folderName"
              :all-count="props.allCount" @set-active-folder="(val: string) => emit('setActiveFolder', val)">
    <template #expandLeft>
      <!-- 显示请求icon -->
      <n-tooltip trigger="hover">
        <template #trigger>
          <n-button v-show="!props.notShowOperation && showExpandApi" text @click="changeApiExpand">
            <div :class="!isExpandApi?'i-mdi:eye-outline':'i-mdi:eye-off-outline'"/>
          </n-button>
        </template>
        {{ !isExpandApi ? '显示全部请求' : '隐藏全部请求' }}
      </n-tooltip>
      <!-- 协议icon -->
<!--      <n-popconfirm negative-text="知道了">-->
<!--        <template #trigger>-->
<!--          <n-tooltip trigger="hover">-->
<!--            <template #trigger>-->
<!--              <n-button text>-->
<!--                <div class="i-mdi:file-document"/>-->
<!--              </n-button>-->
<!--            </template>-->
<!--            协议-->
<!--          </n-tooltip>-->
<!--        </template>-->
<!--        <div class="flex flex-col gap-[8px]">-->
<!--          <div class="font-medium">接口协议为空</div>-->
<!--          <div class="text-[var(&#45;&#45;color-text-2)]">列表展示数据为空，请选择协议</div>-->
<!--        </div>-->
<!--      </n-popconfirm>-->
    </template>
    <template #expandRight>
      <slot name="expandRight"></slot>
    </template>
  </folder-all>
</template>

<style scoped>

</style>