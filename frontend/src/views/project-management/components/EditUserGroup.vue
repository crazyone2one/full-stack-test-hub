<script setup lang="ts">

import {ref} from "vue";
import type {FormInst} from "naive-ui";
import {useForm} from "alova/client";
import {userGroupApis} from "/@/api/modules/user-group.ts";
import {useAppStore} from "/@/store";

const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void; }>();
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const formRef = ref<FormInst | null>(null)
const appStore = useAppStore()
const {form, reset} = useForm((FormData) => userGroupApis.updateOrAddProjectUserGroup(FormData), {
  initialForm: {
    name: '',
    scopeId: appStore.appState.currentProjectId
  },
  immediate: false
})
const rules = {
  name: [
    {required: true, message: '用户组名称不能为空', trigger: 'blur'},
    {max: 255, message: '名称不能超过 255 个字符', trigger: 'blur'}
  ]
}
const handleCancel = (shouldSearch: boolean) => {
  emit('cancel', shouldSearch)
  reset()
  formRef.value?.restoreValidation()
}
const handleCreateUserGroup = () => {
  formRef.value?.validate(errors => {
    if (!errors) {
      userGroupApis.updateOrAddProjectUserGroup(form.value).then(() => {
        handleCancel(true)
        window.$message.success('创建成功')
      })
    }
  })
}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" @close="handleCancel(false)">
    <template #header>
      <div>添加用户组</div>
    </template>
    <div class="form">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="auto"
              require-mark-placement="right-hanging"
      >
        <n-form-item label="用户组名称" path="name">
          <n-input v-model:value="form.name" clearable/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <div class="flex flex-row justify-between">
        <div class="flex flex-row gap-[14px]">
          <n-button secondary @click="handleCancel(false)">取消</n-button>
          <n-button type="primary" @click="handleCreateUserGroup">
            添加
          </n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>