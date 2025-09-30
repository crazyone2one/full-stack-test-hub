<script setup lang="ts">
import {addCommasToNumber} from "/@/utils";

const props = defineProps<{
  activeFolder?: string; // 选中的节点
  folderName: string; // 名称
  allCount: number; // 总数
}>();
const emit = defineEmits<{ (e: 'setActiveFolder', val: string): void; }>();
const isExpandAll = defineModel<boolean>('isExpandAll', {
  required: false,
  default: undefined,
});
</script>

<template>
  <div class="folder ">
    <div :class="props.activeFolder === 'all' ? 'folder-text folder-text--active' : 'folder-text'"
         @click="emit('setActiveFolder', 'all')">
      <div class="folder-icon i-mdi:folder"/>
      <div class="folder-name">{{ props.folderName }}</div>
      <div class="folder-count">({{ addCommasToNumber(props.allCount) }})</div>
    </div>
    <div class="ml-auto flex items-center">
      <n-flex>
        <slot name="expandLeft"></slot>
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-button v-if="typeof isExpandAll === 'boolean'"  text
                      @click="isExpandAll = !isExpandAll">
              <div :class="isExpandAll ? 'i-mdi:folder' : 'i-mdi:folder-open'"/>
            </n-button>
          </template>
          {{ isExpandAll ? '收起全部' : '展开全部' }}
        </n-tooltip>
        <slot name="expandRight"></slot>
      </n-flex>
    </div>
  </div>
</template>

<style scoped>
.folder {
  @apply flex cursor-pointer items-center justify-between;
  padding: 4px;

  .folder-text {
    @apply flex cursor-pointer items-center;

    height: 26px;

    .folder-icon {
      margin-right: 4px;
    }

    .folder-count {
      margin-left: 4px;
    }
  }

}
</style>