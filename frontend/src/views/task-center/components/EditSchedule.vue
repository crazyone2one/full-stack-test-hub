<script setup lang="ts">
import {ref} from "vue";
import type {FormInst} from "naive-ui";
import {useForm} from "alova/client";
import {systemApis} from "/@/api/modules/task-center/system.ts";
import {useAppStore} from "/@/store";
import BaseCronSelect from "/@/components/BaseCronSelect.vue";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
// const props = defineProps<{
//   type: 'system' | 'project' | 'org';
// }>();
const emit = defineEmits<{ (e: 'refresh'): void; }>();
const appStore = useAppStore();
const formRef = ref<FormInst | null>(null)
const {form,send}=useForm(form=>systemApis.saveSystemSchedule(form),{
  immediate: false,
  initialForm: {
    name: '',
    job: '',
    value: '',
    projectId: appStore.currentProjectId,
    enable: true,
  }
})
const rules = {
  value: {
    required: true,
    trigger: ['input'],
    message: '请输入 cron表达式'
  },
  name: {
    required: true,
    trigger: ['blur', 'input'],
    message: '请输入 name'
  },
  job: {
    required: true,
    trigger: ['blur', 'input'],
    message: '请输入 job'
  },
}
const handleSubmit = () => {
  send().then(() => {
    window.$message.success('任务添加成功')
    handleCancel()
    emit('refresh')
  })
  console.log(form.value)
}
const handleCancel = () => {
  showModal.value = false
  formRef.value?.restoreValidation()
}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <div>创建定时任务</div>
    </template>
    <div>
      <n-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-placement="left"
          label-width="auto"
          require-mark-placement="right-hanging"
      >
        <n-form-item label="taskName" path="name">
          <n-input v-model:value="form.name" placeholder="录入任务名称"/>
        </n-form-item>
        <n-form-item label="job" path="job">
          <n-input v-model:value="form.job" clearable placeholder="录入任务对应的类名"/>
          <n-tooltip trigger="hover">
            <template #trigger>
              <base-icon type="fallback"/>
            </template>
            录入类似cn.master.job.DemoJob
          </n-tooltip>
        </n-form-item>
        <n-form-item label="cron表达式" path="value">
          <base-cron-select v-model:model-value="form.value" size="medium"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <div class="flex flex-row justify-between">
        <div class="flex flex-row items-center gap-[4px]">
          <div class="mr-3">
            <n-switch size="small" v-model:value="form.enable"/>
            <span>状态</span>
          </div>
        </div>
        <div class="flex flex-row gap-[12px]">
          <n-button @click="handleCancel">取消</n-button>
          <n-button type="primary" @click="handleSubmit">确定</n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>