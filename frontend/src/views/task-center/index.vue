<script setup lang="ts">
import {hasAnyPermission} from "/@/utils/permissions.ts";
import {TaskCenterEnum, type TaskCenterEnumType} from "/@/enums/task-center-enum.ts";
import {ref} from "vue";
import {useRoute} from "vue-router";
import SystemTaskTable from "/@/views/task-center/components/SystemTaskTable.vue";

const props = defineProps<{
  type: 'system' | 'org' | 'project';
  mode?: 'modal' | 'normal';
}>();
const route = useRoute();
const activeTab = ref<TaskCenterEnumType>((route.query.type as TaskCenterEnumType) || TaskCenterEnum.BACKEND);
const tabList = ref<Record<string, any>[]>([]);
const initTabList = () => {
  if (
      (props.type === 'project' && hasAnyPermission(['PROJECT_CASE_TASK_CENTER:READ'])) ||
      (props.type === 'org' && hasAnyPermission(['ORGANIZATION_CASE_TASK_CENTER:READ'])) ||
      (props.type === 'system' && hasAnyPermission(['SYSTEM_CASE_TASK_CENTER:READ']))
  ) {
    tabList.value.push(
        {
          value: TaskCenterEnum.CASE,
          label: '系统即时任务',
        },
        {
          value: TaskCenterEnum.DETAIL,
          label: '任务执行详情',
        }
    );
  }
  if (
      (props.type === 'project' && hasAnyPermission(['PROJECT_SCHEDULE_TASK_CENTER:READ'])) ||
      (props.type === 'org' && hasAnyPermission(['ORGANIZATION_SCHEDULE_TASK_CENTER:READ'])) ||
      (props.type === 'system' && hasAnyPermission(['SYSTEM_SCHEDULE_TASK_CENTER:READ']))
  ) {
    tabList.value.push({
      value: TaskCenterEnum.BACKEND,
      label: '系统后台任务',
    });
  }
}
initTabList()
</script>

<template>
  <div>
    <n-tabs v-model:value="activeTab" type="line">
      <n-tab-pane v-for="item of tabList" :name="item.value" :tab="item.label"/>
    </n-tabs>
    <div class="task-center-content">
      <Suspense>
        <div v-if="activeTab === TaskCenterEnum.CASE">caseTaskTable</div>
        <div v-else-if="activeTab === TaskCenterEnum.DETAIL">caseTaskDetailTable</div>
        <system-task-table v-else-if="activeTab === TaskCenterEnum.BACKEND"
                           :type="props.type"/>
      </Suspense>
    </div>
  </div>
</template>

<style scoped>

</style>