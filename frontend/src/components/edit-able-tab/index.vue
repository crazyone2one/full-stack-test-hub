<script setup lang="ts">
import type {TabItem} from "/@/components/edit-able-tab/types.ts";
import {ref} from "vue";
import {useScroll} from "@vueuse/core";

const tabs = defineModel<TabItem[]>('tabs', {
  required: true,
});
const activeTab = defineModel<TabItem >('activeTab', {
  default: undefined,
});
const tabNav = ref<HTMLElement>();
const {arrivedState} = useScroll(tabNav);
</script>

<template>
  <div ref="tabNav">
    <n-tabs v-model:value="activeTab.id" type="card" animated>
      <template #prefix>
        <n-tooltip trigger="hover" :disabled="!arrivedState.left">
          <template #trigger>
            <n-button text :disabled="arrivedState.left">
              <div class="i-mdi:arrow-left-bold"/>
            </n-button>
          </template>
          到最左侧啦～
        </n-tooltip>
      </template>

      <n-tab-pane v-for="tab in tabs" :name="tab.id" :tab="tab.label">
        <slot name="label" :tab="tab">
          <div class="one-line-text flex max-w-[144px] items-center">
            {{ tab.label }}
          </div>
        </slot>
<!--        {{ activeTab }}-->
      </n-tab-pane>
      <template #suffix>
        <n-tooltip trigger="hover" :disabled="!arrivedState.right">
          <template #trigger>
            <n-button text :disabled="arrivedState.right">
              <div class="i-mdi:arrow-right-bold"/>
            </n-button>
          </template>
          到最右侧啦～
        </n-tooltip>
      </template>
    </n-tabs>
  </div>
</template>

<style scoped>

</style>