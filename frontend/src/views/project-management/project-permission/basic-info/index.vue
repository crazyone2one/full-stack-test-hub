<script setup lang="ts">
import type {IProjectBasicInfo} from "/@/api/types/project.ts";
import {computed, onBeforeMount, ref, watch} from "vue";
import {useAppStore} from "/@/store";
import {useRequest} from "alova/client";
import {projectManagementApis} from "/@/api/modules/project-management.ts";

const appStore = useAppStore();

const currentProjectId = computed(() => appStore.appState.currentProjectId);
const projectDetail = ref<IProjectBasicInfo>();
const {send: fetchProjectDetail} = useRequest((id) => projectManagementApis.getProjectInfo(id), {immediate: false});
watch(() => currentProjectId, () => {
  fetchProjectDetail(currentProjectId.value).then(res => projectDetail.value = res)
})
onBeforeMount(() => {
  fetchProjectDetail(currentProjectId.value).then(res => projectDetail.value = res)
})
</script>

<template>
  <div class="wrapper mb-[16px] flex justify-between border-b pb-[16px]">
    <span class="font-medium">基本信息</span>
    <n-button v-show="!projectDetail?.deleted" secondary>编辑</n-button>
  </div>
  <div class="detail-info mb-[16px] flex items-center gap-[12px]">
    <div class="flex items-center rounded-[2px]  p-[9px]">
      <div class="i-mdi:application-cog "/>
    </div>
    <div class="flex flex-col">
      <div class="flex items-center">
        <div class="one-line-text mr-1 max-w-[300px] font-medium">
          {{ projectDetail?.name }}
        </div>
        <div class="button mr-1" :class="[projectDetail?.deleted ? 'delete-button' : 'enable-button']">
          {{ projectDetail?.deleted ? '已删除' : '启用' }}
        </div>
        <div class="one-line-text text-[12px] text-[#9597a4]">{{ projectDetail?.description }}</div>
      </div>
    </div>
  </div>
  <div class="grid grid-cols-2 gap-[16px] rounded-[2px] p-[16px]">
    <div class="label-item">
      <span class="label">创建人：</span>
      <span class="one-line-text" style="max-width: 300px">{{ projectDetail?.createUser }}</span>
    </div>
    <div class="label-item">
      <span class="label">所属组织：</span>
      <n-tag :bordered="false">{{ projectDetail?.organizationName }}</n-tag>
    </div>
    <div class="label-item">
      <span class="label">资源池：</span>
      <n-tag v-for="pool of projectDetail?.resourcePoolList" :key="pool.id" :bordered="false">{{ pool.name }}</n-tag>
    </div>
  </div>
</template>

<style scoped>
.detail-info {
  .button {
    padding: 0 4px;
    font-size: 12px;
    border-radius: 2px;
    line-height: 20px;
    @apply inline-block;
  }

  .enable-button {
    color: #11b671;
  }

  .disabled-button {
    color: #9597a4;
    background: #434552;
  }

  .delete-button {
    color: #e2324f;
    background: #e2324f;
  }
}

.label-item {
  height: 22px;
  line-height: 22px;

  .label {
    width: 120px;
    color: #adb0bc;
  }
}
</style>