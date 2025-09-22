<script setup lang="ts">
import {computed} from "vue";
import {characterLimit} from "/@/utils";

const props = withDefaults(defineProps<{
  tagList: Array<any>;
  showTable?: boolean;
  nameKey?: string;
  showNum?: number;
  isStringTag?: boolean; // 是否是字符串数组的标签
  allowEdit?: boolean;
}>(), {nameKey: 'name', showNum: 2,})
const filterTagList = computed(() => {
  return (props.tagList || []).filter((item: any) => item) || [];
});
const showTagList = computed(() => {
  // 在表格展示则全部展示，按照自适应去展示标签个数
  if (props.showTable) {
    return filterTagList.value;
  }
  return filterTagList.value.slice(0, props.showNum);
});
const getTagContent = (tag: { [x: string]: any }) => {
  const tagContent = (props.isStringTag ? tag : tag[props.nameKey]) || '';
  if (props.showTable) {
    return tagContent.length > 16 ? characterLimit(tagContent, 9) : tagContent;
  }
  return tagContent;
}
const tagsTooltip = computed(() => {
  return filterTagList.value.map((e: any) => (props.isStringTag ? e : e[props.nameKey])).join('，');
})
const emit = defineEmits<{
  (e: 'click'): void;
}>();
</script>

<template>
<div v-if="showTagList.length" :class="`tag-group-class flex w-full flex-row ${props.allowEdit ? 'cursor-pointer' : ''}`" @click="emit('click')">
  <n-tag v-for="(tag) in showTagList" :key="tag.id" :class="`${props.showTable ? 'ms-tag-group' : ''}`"
  size="small">
    {{ getTagContent(tag) }}
  </n-tag>
  <n-tooltip :disabled="!(props.tagList.length > props.showNum)">
    <template #trigger>
      <n-tag  v-show="props.tagList.length > props.showNum" size="small"
              class="ms-tag-num">
        {{ props.tagList.length - props.showNum }}
      </n-tag>
    </template>
    {{tagsTooltip}}
  </n-tooltip>
</div>
  <div v-else :class="`tag-group-class ${props.allowEdit ? 'min-h-[24px] cursor-pointer' : ''}`" @click="emit('click')">
    -
  </div>
</template>

<style scoped>
.tag-group-class {
  overflow: hidden;
  max-width: 440px;
  white-space: nowrap;
}
.ms-tag-group {
  min-width: min-content !important;
  max-width: 144px !important;
}
</style>