<script setup lang="ts">
import {usePagination} from "alova/client";
import {
  type DataTableColumns,
  type DataTableRowKey,
  NButton,
  NFlex,
  NSwitch,
} from "naive-ui";
import {computed, h, onMounted, reactive, ref} from "vue";
import {orgProjectApis} from "/@/api/modules/org-project.ts";
import type {ProjectTableItem} from "/@/api/types/project.ts";
import {hasAnyPermission} from "/@/utils/permissions.ts";
import UserGrawer from "/@/views/setting/system/org-project/components/UserGrawer.vue";

const keyword = defineModel("keyword", {type: String});
const hasOperationPermission = computed(() =>
    hasAnyPermission([
      "SYSTEM_ORGANIZATION_PROJECT:READ+RECOVER",
      "SYSTEM_ORGANIZATION_PROJECT:READ+UPDATE",
      "SYSTEM_ORGANIZATION_PROJECT:READ+DELETE",
    ])
);
const operationWidth = computed(() => {
  if (hasOperationPermission.value) {
    return 250;
  }
  if (hasAnyPermission(["PROJECT_BASE_INFO:READ"])) {
    return 100;
  }
  return 50;
});
const columns: DataTableColumns<ProjectTableItem> = [
  {
    type: "selection",
  },
  {title: "ID", key: "num", width: 80},
  {title: "名称", key: "name"},
  {
    title: "成员",
    key: "memberCount",
    render(record) {
      if (
          hasAnyPermission([
            "SYSTEM_ORGANIZATION_PROJECT:READ+ADD_MEMBER",
            "SYSTEM_ORGANIZATION_PROJECT:READ",
          ])
      ) {
        return h(
            NButton,
            {
              text: true,
              size: "small",
              onClick: () => showUserDrawer(record),
            },
            {default: () => record.memberCount}
        );
      }
      return h("div", {}, {default: () => record.memberCount});
    },
  },
  {
    title: "状态",
    key: "enable",
    render(record) {
      return h(NSwitch, {
        value: record.enable,
        size: "small",
        // onUpdateValue: (value) => handleChangeEnable(value, record)
      });
    },
  },
  {title: "描述", key: "description"},
  {title: "所属组织", key: "organizationName"},
  {
    title: hasOperationPermission.value ? "操作" : "",
    key: "actions",
    fixed: "right",
    width: operationWidth.value,
    render(record) {
      if (!record.enable) {
        return h(
            NButton,
            {text: true, type: "error"},
            {default: () => "删除"}
        );
      } else {
        return h(
            NFlex,
            {},
            {
              default: () => [
                // h(NButton, {
                //   text: true,
                //   size: 'medium',
                //   onClick: () => showAddProjectModal(record)
                // }, {default: () => '编辑'}),
                h(
                    NButton,
                    {text: true, size: "medium"},
                    {default: () => "添加成员"}
                ),
                h(
                    NButton,
                    {text: true, size: "medium"},
                    {default: () => "进入项目"}
                ),
                h(
                    NButton,
                    {text: true, type: "error"},
                    {default: () => "删除"}
                ),
              ],
            }
        );
      }
    },
  },
];
const {
  page,
  pageSize,
  data,
  send: fetchData,
  total,
} = usePagination(
    (page, pageSize) =>
        orgProjectApis.postProjectTable({
          page,
          pageSize,
          keyword: keyword.value,
        }),
    {
      initialData: {total: 0, data: []},
      immediate: false,
      data: (resp) => resp.records,
      total: (resp) => resp.totalRow,
      watchingStates: [keyword],
    }
);
const checkedRowKeys = ref<DataTableRowKey[]>([]);
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys;
};
const currentUserDrawer = reactive({
  visible: false,
  projectId: "",
  currentName: "",
});
const getRowClass = (record: ProjectTableItem) => {
  return record.id === currentUserDrawer.projectId ? 'selected-row-class' : ''
}
const showUserDrawer = (record: ProjectTableItem) => {
  currentUserDrawer.visible = true;
  currentUserDrawer.projectId = record.id;
  currentUserDrawer.currentName = record.name;
};
const handleUserDrawerCancel = (refresh: false) => {
  currentUserDrawer.visible = false;
  if (refresh) {
    fetchData();
  }
};
defineExpose({
  fetchData,
});
onMounted(() => {
  fetchData();
});
</script>

<template>
  <n-data-table
      :columns="columns"
      :data="data"
      :row-key="(row: ProjectTableItem) => row.id"
      :row-class-name="getRowClass"
      @update:checked-row-keys="handleCheck"
  />
  <base-pagination
      v-model:page="page"
      v-model:page-size="pageSize"
      :count="total || 0"
  />
  <user-grawer
      v-model:active="currentUserDrawer.visible"
      v-bind="currentUserDrawer"
      @cancel="handleUserDrawerCancel(false)"
      @request-fetch-data="fetchData"
  />
</template>

<style scoped>
:deep(.selected-row-class td) {
  background: rgba(197, 159, 227, 75) !important;
}
</style>
