<script setup lang="ts">
import {onBeforeMount, ref, useTemplateRef, watch} from "vue";
import SystemOrganization from "/@/views/setting/system/org-project/components/SystemOrganization.vue";
import SystemProject from "/@/views/setting/system/org-project/components/SystemProject.vue";
import AddProjectModal from "/@/views/setting/organization/project/components/AddProjectModal.vue";
import {useRequest} from "alova/client";
import {orgProjectApis} from "/@/api/modules/org-project.ts";

const currentTable = ref('project');
const keyword = ref('');
const organizationCount = ref(0);
const projectCount = ref(0);
const orgTableRef = useTemplateRef<InstanceType<typeof SystemOrganization>>('orgTable');
const projectTableRef = useTemplateRef<InstanceType<typeof SystemProject>>('projectTable');
const addProjectRef = useTemplateRef<InstanceType<typeof AddProjectModal>>('addProject');
const projectVisible = ref(false);
const organizationVisible = ref(false);

const {send: fetchOrgAndProjectCount} = useRequest(() => orgProjectApis.getOrgAndProjectCount(), {immediate: false})
const initOrgAndProjectCount = () => {
  fetchOrgAndProjectCount().then(res => {
    organizationCount.value = res.organizationTotal;
    projectCount.value = res.projectTotal;
  })
}
const handleAdd = () => {
  if (currentTable.value === 'organization') {
    organizationVisible.value = true;
  } else {
    projectVisible.value = true;
  }
}
const handleAddProjectCancel = (shouldSearch: boolean) => {
  projectVisible.value = false;
  if (shouldSearch) {
    tableSearch();
  }
};
const tableSearch = () => {
  initOrgAndProjectCount();
}
watch(
    () => currentTable.value,
    () => {
      // currentKeyword.value = '';
      keyword.value = '';
    }
);
onBeforeMount(() => {
  initOrgAndProjectCount()
})
</script>

<template>
  <n-card>
    <template #header>
      <n-flex>
        <n-radio-group v-model:value="currentTable">
          <n-radio value="organization">{{ `组织(${organizationCount})` }}</n-radio>
          <n-radio value="project">{{ `项目(${projectCount})` }}</n-radio>
        </n-radio-group>
        <n-button v-if="currentTable !== 'organization'" type="primary" @click="handleAdd">
          {{ currentTable === 'organization' ? '创建组织' : '创建项目' }}
        </n-button>
      </n-flex>
    </template>
    <template #header-extra>
      <n-input v-model:value="keyword" placeholder="通过 ID/名称搜索" clearable class="w-[240px]"/>
    </template>
    <div>
      <system-organization v-if="currentTable === 'organization'" ref="orgTableRef"/>
      <system-project v-if="currentTable === 'project'" ref="projectTableRef" v-model:keyword="keyword"/>
    </div>
  </n-card>
  <add-project-modal ref="addProjectRef" v-model:show-modal="projectVisible"
                     @cancel="handleAddProjectCancel"
                     @submit="tableSearch"/>
</template>

<style scoped>

</style>