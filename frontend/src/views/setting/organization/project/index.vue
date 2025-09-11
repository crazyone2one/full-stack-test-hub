<script setup lang="ts">
import {type DataTableColumns, type DataTableRowKey, NButton, NFlex, NSwitch} from "naive-ui";
import {computed, h, onMounted, ref, useTemplateRef} from "vue";
import {hasAnyPermission} from "/@/utils/permissions.ts";
import {useAppStore} from "/@/store";
import type {CreateOrUpdateOrgProjectParams, ProjectTableItem} from "/@/api/types/project.ts";
import {usePagination, useRequest} from "alova/client";
import {projectManagementApis} from "/@/api/modules/project-management.ts";
import AddProjectModal from "/@/views/setting/organization/project/components/AddProjectModal.vue";
import type {IUserItem} from "/@/api/types/user.ts";

const appStore = useAppStore();
const currentOrgId = computed(() => appStore.appState.currentOrgId);
const keyword = ref('');
const addProjectVisible = ref(false)
const addProjectModalRef = useTemplateRef<InstanceType<typeof AddProjectModal>>('addProjectModal')
const currentUpdateProject = ref<CreateOrUpdateOrgProjectParams>();
const operationWidth = computed(() => {
  if (hasOperationPermission.value) {
    return 250;
  }
  if (hasAnyPermission(['PROJECT_BASE_INFO:READ'])) {
    return 100;
  }
  return 50;
});
const showAddProjectModal = (record: ProjectTableItem) => {
  const {
    id,
    name,
    description,
    enable,
    adminList,
    organizationId,
    moduleIds,
    resourcePoolList,
    allResourcePool,
    projectCode
  } =
      record;
  currentUpdateProject.value = {
    id,
    name,
    description,
    enable,
    userIds: adminList.map((item: IUserItem) => item.id),
    organizationId,
    moduleIds,
    resourcePoolIds: resourcePoolList.map((item: { id: string }) => item.id),
    allResourcePool,
    projectCode
  };
  addProjectVisible.value = true;
};
const hasOperationPermission = computed(() =>
    hasAnyPermission([
      'ORGANIZATION_PROJECT:READ+RECOVER',
      'ORGANIZATION_PROJECT:READ+UPDATE',
      'ORGANIZATION_PROJECT:READ+DELETE',
    ])
);
const columns: DataTableColumns<ProjectTableItem> = [
  {
    type: 'selection',
  },
  {title: 'ID', key: 'num', width: 80},
  {title: '名称', key: 'name'},
  {title: '成员', key: 'memberCount'},
  {
    title: '状态', key: 'enable', render(record) {
      return h(NSwitch, {
        value: record.enable,
        size: 'small',
        onUpdateValue: (value) => handleChangeEnable(value, record)
      })
    }
  },
  {title: '描述', key: 'description'},
  {title: '所属组织', key: 'organizationName'},
  {
    title: hasOperationPermission.value ? '操作' : '',
    key: 'actions',
    fixed: 'right',
    width: operationWidth.value,
    render(record) {
      if (!record.enable) {
        return h(NButton, {text: true, size: 'medium'}, {default: () => '删除'});
      } else {
        return h(NFlex, {}, {
          default: () => [
            h(NButton, {
              text: true,
              size: 'medium',
              onClick: () => showAddProjectModal(record)
            }, {default: () => '编辑'}),
            h(NButton, {text: true, size: 'medium'}, {default: () => '添加成员'}),
            h(NButton, {text: true, size: 'medium'}, {default: () => '进入项目'}),
            h(NButton, {text: true, size: 'medium'}, {default: () => '删除'}),
          ]
        })
      }
    },
  },
]
const {
  page,
  pageSize,
  data,
  send: fetchData, total
} = usePagination((page, pageSize) => projectManagementApis.fetchProjectPageByOrg({
      page,
      pageSize,
      keyword: keyword.value,
      organizationId: currentOrgId.value
    }),
    {
      initialData: {total: 0, data: []},
      immediate: false,
      data: resp => resp.records,
      total: resp => resp.totalRow,
      watchingStates: [keyword]
    })
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const showAddProject = () => {
  addProjectVisible.value = true
  currentUpdateProject.value = undefined;
}
const handleAddProjectModalCancel = (shouldSearch: boolean) => {
  addProjectVisible.value = false;
  if (shouldSearch) {
    fetchData()
  }
}
const {send: handleEnableOrDisableProject} = useRequest((id, isEnable) => projectManagementApis.enableOrDisableProjectByOrg(id, isEnable), {immediate: false})
const handleChangeEnable = (isEnable: boolean, record: ProjectTableItem) => {
  window.$dialog.warning({
    title: isEnable ? '启用项目' : '关闭项目',
    content: isEnable ? '开启后的项目展示在项目切换列表' : '关闭后的项目不展示在项目切换列表',
    positiveText: isEnable ? '确认开启' : '确认关闭',
    negativeText: '取消',
    draggable: true,
    onPositiveClick: () => {
      handleEnableOrDisableProject(record.id, isEnable).then(() => {
        window.$message.success(isEnable ? '启用成功' : '关闭成功')
        fetchData()
      })
    },
  })
}
onMounted(() => {
  fetchData()
})
</script>

<template>
  <n-card>
    <template #header>
      <n-button type="primary" @click="showAddProject">创建项目</n-button>
    </template>
    <template #header-extra>
      <n-input v-model:value="keyword" placeholder="通过 ID/名称搜索" class="w-[240px]" clearable/>
    </template>
    <n-data-table :columns="columns"
                  :data="data"
                  :row-key="(row: ProjectTableItem) => row.id"
                  @update:checked-row-keys="handleCheck"/>
    <base-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
  </n-card>
  <add-project-modal ref="addProjectModalRef" v-model:show-modal="addProjectVisible"
                     :current-project="currentUpdateProject"
                     @cancel="handleAddProjectModalCancel"/>
</template>

<style scoped>

</style>