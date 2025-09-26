<script setup lang="ts">
import {h, onMounted, ref} from "vue";
import {type DataTableColumns, type DataTableRowKey, NButton, NDataTable, NText,NSwitch} from "naive-ui";
import type {ITaskCenterSystemTaskItem} from "/@/api/types/task-center.ts";
import {usePagination} from "alova/client";
import type {ITableQueryParams} from "/@/api/types/commons.ts";
import {systemApis} from "/@/api/modules/task-center/system.ts";
import {organizationApi} from "/@/api/modules/task-center/organization.ts";
import {projectApis} from "/@/api/modules/task-center/project.ts";
import {scheduleTaskTypeMap} from "/@/views/task-center/config.ts";
import EditSchedule from "/@/views/task-center/components/EditSchedule.vue";
import BaseCronSelect from "/@/components/BaseCronSelect.vue";
import {hasAnyPermission} from "/@/utils/permissions.ts";

const props = defineProps<{
  type: 'system' | 'project' | 'org';
}>();

const keyword = ref('');
const editScheduleShow = ref(false);
const getCurrentPermission = (action: 'DELETE' | 'EDIT') => {
  return {
    system: {
      DELETE: 'SYSTEM_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'SYSTEM_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
    org: {
      DELETE: 'ORGANIZATION_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'ORGANIZATION_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
    project: {
      DELETE: 'PROJECT_SCHEDULE_TASK_CENTER:READ+DELETE',
      EDIT: 'PROJECT_SCHEDULE_TASK_CENTER:READ+UPDATE',
    },
  }[props.type][action];
}
const columns: DataTableColumns<ITaskCenterSystemTaskItem> = [
  {
    type: 'selection', fixed: 'left'
  },
  {title: '任务 ID', key: 'num', width: 150, ellipsis: {tooltip: true}},
  {title: '任务名称', key: 'name', width: 160},
  {
    title: '状态', key: 'enable', width: 80,
    render(record) {
      return h(NSwitch, {value: record.enable,size:'small'}, {})
    }
  },
  {
    title: '类型', key: 'resourceType', width: 120, render: (record) => {
      return h(NText, {}, {default: () => scheduleTaskTypeMap[record.resourceType]})
    }
  },
  {
    title: '运行规则', key: 'value', width: 180,
    render(record) {
      if (hasAnyPermission([getCurrentPermission('EDIT')])) {
        return h(BaseCronSelect, {modelValue: record.value, size: 'small'});
      }
      return h(NText, {}, {default: () => record.value})
    }
  },
  {title: '操作人', key: 'createUser', width: 100},
  {title: '操作时间', key: 'createTime', width: 150, ellipsis: {tooltip: true}},
  {title: '上次完成时间', key: 'lastTime', width: 150},
  {title: '下次执行时间', key: 'nextTime', width: 150},
  {
    title: '操作', key: 'actions', fixed: 'right', width: 200,
    render: (record) => {
      const actions = [
        h(NButton, {size: 'small', text: true, type: 'primary', class: '!mr-[12px]'}, {default: () => '详情'}),
        h(NButton, {size: 'small', text: true, type: 'primary', class: '!mr-[12px]'}, {default: () => '修改配置'}),
      ]
      if (['API_IMPORT', 'TEST_PLAN', 'TEST_PLAN_GROUP', 'API_SCENARIO', 'LOAD_TEST'].includes(record.resourceType)) {
        if (hasAnyPermission([getCurrentPermission('DELETE')])) {
          actions.push(h(NButton, {size: 'small', text: true, type: 'error'}, {default: () => '删除'}))
        }
      }
      return actions;
    }
  },
]
const initProjectAndOrgs = async () => {
  if (props.type === 'system') {
    // const projects = await systemApis.systemProjectOptions();
    // const orgs = await systemApis.systemOrgOptions();
    columns.splice(2, 0, {title: '所属项目', key: 'projectName', width: 150},
        {title: '所属组织', key: 'organizationName', width: 150})
  } else if (props.type === 'org') {
    // const projects = await organizationApi.organizationProjectOptions();
    columns.splice(2, 0, {title: '所属项目', key: 'projectName', width: 150})
  }
}
await initProjectAndOrgs();
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const {page, pageSize, total, data, send: fetchData} = usePagination((page, pageSize) => {
  const param: ITableQueryParams = {page, pageSize, keyword: keyword.value}
  if (props.type === 'system') {
    return systemApis.getSystemScheduleList(param);
  } else if (props.type === 'org') {
    return organizationApi.getOrganizationScheduleList(param);
  } else {
    return projectApis.getProjectScheduleList(param);
  }
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  watchingStates: [keyword]
})
onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="flex items-center justify-between">
    <n-button secondary type="primary" size="small" @click="editScheduleShow = true">
      <template #icon>
        <div class="i-mdi:plus"/>
      </template>
    </n-button>
    <div class="my-[10px] flex items-center justify-end">
      <div>
        <n-input v-model:value="keyword" placeholder="通过 ID/名称搜索" class="mr-[12px] w-[240px]" clearable/>
      </div>
      <n-button type="info" text>
        <div class="i-mdi:reload text-[20px] ml-3"/>
      </n-button>
    </div>
  </div>

  <n-data-table :columns="columns"
                :data="data"
                :row-key="(row: ITaskCenterSystemTaskItem) => row.id"
                @update:checked-row-keys="handleCheck"/>
  <base-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
  <edit-schedule v-model:show-modal="editScheduleShow" :type="props.type" @refresh="fetchData"/>
</template>

<style scoped>

</style>