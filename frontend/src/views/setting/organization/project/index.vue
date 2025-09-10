<script setup lang="ts">
import {
  type DataTableColumns,
  type DataTableRowKey,
  NButton,
  NCard,
  NDataTable,
  NInput,
  type PaginationProps
} from "naive-ui";
import {computed, onMounted, ref} from "vue";
import {hasAnyPermission} from "/@/utils/permissions.ts";
import {useAppStore} from "/@/store";
import type {ProjectTableItem} from "/@/api/types/project.ts";
import {usePagination} from "alova/client";
import {projectManagementApis} from "/@/api/modules/project-management.ts";

const appStore = useAppStore();
const currentOrgId = computed(() => appStore.appState.currentOrgId);
const keyword = ref('');
const operationWidth = computed(() => {
  if (hasOperationPermission.value) {
    return 250;
  }
  if (hasAnyPermission(['PROJECT_BASE_INFO:READ'])) {
    return 100;
  }
  return 50;
});
const hasAddPermission = computed(() => hasAnyPermission(['ORGANIZATION_PROJECT:READ+ADD']));
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
  {title: '状态', key: 'enable'},
  {title: '描述', key: 'description'},
  {title: '所属组织', key: 'organizationName'},
  {
    title: hasOperationPermission.value ? '操作' : '',
    key: 'actions',
    fixed: 'right',
    width: operationWidth.value
  },
]
const {
  page,
  pageSize,
  data,
  send: fetchData
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
    })
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const pagination: PaginationProps = {
  page: page.value, pageSize: pageSize.value, size: 'small'
}
onMounted(() => {
  fetchData()
})
</script>

<template>
  <n-card>
    <template #header>
      <n-button type="primary">创建项目</n-button>
    </template>
    <template #header-extra>
      <n-input v-model:value="keyword" placeholder="通过 ID/名称搜索" class="w-[240px]" clearable/>
    </template>
    <n-data-table :columns="columns"
                  :data="data"
                  :pagination="pagination"
                  :row-key="(row: ProjectTableItem) => row.id"
                  @update:checked-row-keys="handleCheck"/>
  </n-card>
</template>

<style scoped>

</style>