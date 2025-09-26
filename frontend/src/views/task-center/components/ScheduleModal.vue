<script setup lang="ts">
import type {ICreateTask} from "/@/api/types/task-center.ts";
import {ref} from "vue";
import type {FormInst} from "naive-ui";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const props = defineProps<{
  taskConfig?: ICreateTask;
  sourceId?: string;
  isBatch?: boolean;
}>();
const formRef = ref<FormInst | null>(null)
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'handleSuccess'): void;
}>();
const initForm: ICreateTask = {
  resourceId: '',
  cron: '',
  enable: true,
  runConfig: {runMode: 'SERIAL'},
}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <div>{{props.taskConfig ? props.isBatch?'批量编辑定时任务':'更新定时任务' : '创建定时任务'}}</div>
    </template>
    <div>内容</div>
    <template #action>
      <div>操作</div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>