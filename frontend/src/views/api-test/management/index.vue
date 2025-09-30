<script setup lang="ts">
import ModuleTree from "/@/views/api-test/management/components/ModuleTree.vue";
import {ref} from "vue";
import ImportDrawer from "/@/views/api-test/management/components/ImportDrawer.vue";
import ManagementIndex from "/src/views/api-test/management/components/management/index.vue";
import type {ModuleTreeNode} from "/@/api/types/commons.ts";

const importDrawerVisible = ref(false);
const moduleTreeRef = ref<InstanceType<typeof ModuleTree>>();
const managementRef = ref<InstanceType<typeof ManagementIndex>>();
const activeNodeId = ref<string | number>('all');
const folderTree = ref<ModuleTreeNode[]>([]);
const activeModule = ref<string>('all');
const offspringIds = ref<string[]>([]);
const selectedProtocols = ref<string[]>([]);
const folderTreePathMap = ref<Record<string, any>>({});
const newApi = () => {
  managementRef.value?.newTab();
}
const handleModuleInit = (tree: ModuleTreeNode[], _protocols: string[], pathMap: Record<string, any>) => {
  folderTree.value = tree;
  selectedProtocols.value = _protocols;
  folderTreePathMap.value = pathMap;
}
const handleNodeSelect = (keys: string[], _offspringIds: string[]) => {
  offspringIds.value = _offspringIds;
  [activeModule.value] = keys;
}
</script>

<template>
  <n-card>
    <n-split :max="0.8" :min="0.2" :default-size="0.2">
      <template #1>
        <div class="flex flex-col">
          <div class="p-[16px]">
            <module-tree ref="moduleTreeRef" :active-node-id="activeNodeId" @new-api="newApi"
                         @init="handleModuleInit"
                         @folder-node-select="handleNodeSelect"/>
          </div>
        </div>
      </template>
      <template #2>
        <div class="relative flex h-full flex-col">
          <div
              id="managementContainer"
              :class="['absolute z-[102] h-full w-full', importDrawerVisible ? '' : 'invisible']"
              style="transition: all 0.3s"
          >
            <import-drawer v-model:active="importDrawerVisible"/>
          </div>
          <management-index ref="managementRef" :module-tree="folderTree"
                            :active-module="activeModule"
                            :offspring-ids="offspringIds"
                            :selected-protocols="selectedProtocols"/>
        </div>
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>

</style>