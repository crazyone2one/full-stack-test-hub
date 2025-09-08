<script setup lang="ts">
import BaseModal from "/@/components/BaseModal.vue";
import {ref, watch} from "vue";
import {type FormInst, type FormItemRule, type FormRules, NAlert, NButton, NForm, NFormItem, NInput} from "naive-ui";
import {useForm} from "alova/client";
import {userApis} from "/@/api/modules/user.ts";
import {validateEmail, validatePhone} from "/@/utils/validate.ts";
import type {UserState} from "/@/store/modules/user/types.ts";

defineProps<{
  userFormMode: 'create' | 'edit'
}>()
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const userForm = defineModel<UserState>('userForm', {default: {}})
const formRef = ref<FormInst | null>(null);
const emit = defineEmits(['cancel', 'submit'])
const rules: FormRules = {
  name: [{required: true, message: '姓名不能为空', trigger: ['blur', 'change']},
    {
      validator(_rule: FormItemRule, value: string) {
        if (value && value.length > 255) {
          return new Error('姓名长度不能超过 50')
        }
        return true
      }
    }],
  email: [{required: true, message: '邮箱不能为空', trigger: ['blur', 'change']},
    {
      validator(_rule: FormItemRule, value: string) {
        if (value && !validateEmail(value)) {
          return new Error('请输入正确的邮箱')
        }
        return true;
      }, trigger: ['blur', 'change']
    },],
  phone: {
    validator(_rule: FormItemRule, value: string) {
      if (value !== null && value !== '' && value !== undefined && !validatePhone(value)) {
        return new Error('请输入 11 位手机号')
      }
      return true
    },
    trigger: ['blur', 'change'],
  }
}
const {form, send, loading,reset} = useForm(formData => {
  // 数据预处理
  const processedData = {
    ...formData,
  };
  // 根据是否有 ID 判断是创建还是更新
  return processedData.id
      ? userApis.updateUser(processedData)
      : userApis.createUser(processedData);
}, {
  initialForm: {
    id: undefined,
    name: '',
    email: '',
    phone: ''
  },
  resetAfterSubmiting: true,
  immediate: false
})
const handleValidateClick = (e: MouseEvent) => {
  e?.preventDefault()
  formRef.value?.validate(errors => {
    if (!errors) {
      send().then(() => {
        window.$message.success(form.value.id ? '更新成功' : '添加成功')
        showModal.value = false
        emit('submit')
      })
    }
  })
}

const handleBeforeClose = () => {
  showModal.value = false
  formRef.value?.restoreValidation()
  reset()
}
watch(() => userForm.value, (newValue) => {
  if (newValue) {
    form.value = newValue
  }
}, {deep: true})
</script>

<template>
  <base-modal v-model:show-modal="showModal" :is-edit="userFormMode !== 'create'"
              :show-footer="false">
    <template #header>
      {{ userFormMode === 'create' ? '创建用户' : '编辑用户' }}
    </template>
    <n-alert type="info" :bordered="false" class="mb-[16px]">
      初始密码为邮箱地址
    </n-alert>
    <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" require-mark-placement="right-hanging"
            label-width="auto">
      <n-form-item path="name" label="用户名">
        <n-input v-model:value="form.name" placeholder="请输入姓名"></n-input>
      </n-form-item>
      <n-form-item path="email" label="邮箱">
        <n-input v-model:value="form.email" placeholder="请输入邮箱"></n-input>
      </n-form-item>
      <n-form-item path="phone" label="手机号">
        <n-input v-model:value="form.phone" placeholder="请输入手机号"></n-input>
      </n-form-item>
    </n-form>
    <template #footer>
      <div class="flex flex-row gap-[12px]">
        <n-button type="tertiary" size="small" :disabled="loading" @click="handleBeforeClose">取消</n-button>
<!--        <n-button v-if="userFormMode === 'create'" type="tertiary" size="small" :disabled="loading"-->
<!--                  @click="saveAndContinue">-->
<!--          保存并继续创建-->
<!--        </n-button>-->
        <n-button type="primary" size="small" @click="handleValidateClick">
          {{ userFormMode === 'create' ? '创建' : '保存' }}
        </n-button>
      </div>
    </template>
  </base-modal>
</template>

<style scoped>

</style>