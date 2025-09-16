<script setup lang="ts">
import {inject, reactive, ref, watchEffect} from "vue";
import {AuthScopeEnum, type AuthScopeEnumType} from "/@/enums/common-enum.ts";
import type {IUserGroupItem} from "/@/api/types/user-group.ts";
import {type FormInst, type FormItemRule} from 'naive-ui'
import {useAppStore} from "/@/store";
import {userGroupApis} from "/@/api/modules/user-group.ts";
import {useRequest} from "alova/client";

const systemType = inject<AuthScopeEnumType>('systemType');
const props = defineProps<{
  id?: string;
  list: IUserGroupItem[];
  visible: boolean;
  defaultName?: string;
  // 权限范围
  authScope: AuthScopeEnumType;
}>();
const emit = defineEmits<{
  (e: 'cancel', value: boolean): void;
  (e: 'submit', currentId: string): void;
}>();
const appStore = useAppStore();
const currentVisible = ref(props.visible);
const formRef = ref<FormInst | null>(null)
const form = reactive({
  name: '',
});
const handleCancel = () => {
  form.name = '';
  emit('cancel', false);
};
const handleOutsideClick = () => {
  if (currentVisible.value) {
    handleCancel();
  }
};
const rule = {
  name: {
    validator: (_rule: FormItemRule, value: string) => {
      if (!value) {
        return new Error('用户组名称不能为空');
      } else {
        const isExist = props.list.some((item) => item.name === value);
        if (isExist) {
          return new Error(`已有 ${value} ，请更改`);
        }
      }
    }
  }
}
// const validateName =
const {send} = useRequest(() => {
  if (systemType === AuthScopeEnum.SYSTEM) {
    return userGroupApis.updateOrAddUserGroup({id: props.id, name: form.name, type: props.authScope});
  } else if (systemType === AuthScopeEnum.ORGANIZATION) {
    return userGroupApis.updateOrAddOrgUserGroup({
      id: props.id,
      name: form.name,
      type: props.authScope,
      scopeId: appStore.currentOrgId,
    });
  } else {
    return userGroupApis.updateOrAddProjectUserGroup({name: form.name});
  }
}, {immediate: false})
const handleSubmit = () => {
  formRef.value?.validate((errors) => {
    if (errors) {
      return false;
    }
    send().then(res => {
      if (res) {
        window.$message.success(props.id ? '更新用户组成功' : '添加用户组成功');
        emit('submit', res.id);
        handleCancel();
      }
    })
  })
}
watchEffect(() => {
  currentVisible.value = props.visible;
  form.name = props.defaultName || '';
});
</script>

<template>
  <n-popover :show="currentVisible" trigger="click" class="w-[350px]" :content-class="props.id ? 'move-left' : ''">
    <template #trigger>
      <slot></slot>
    </template>
    <div v-outer="handleOutsideClick">
      <div>
        <n-form ref="formRef" :model="form" :rules="rule" label-width="80px">
          <div class="mb-[8px] text-[14px] font-medium">
            {{ props.id ? '重命名' : '创建用户组' }}
          </div>
          <n-form-item path="name">
            <n-flex>
              <n-input v-model:value="form.name" placeholder="请输入用户组名称" clearable :maxlength="255"/>
              <span v-if="!props.id" class="mt-[8px] text-[13px] font-medium">该用户组将在整个系统范围内可用</span>
            </n-flex>
          </n-form-item>
        </n-form>
      </div>
      <div class="flex flex-row flex-nowrap justify-end gap-2">
        <n-button size="tiny" @click="handleCancel">取消</n-button>
        <n-button size="tiny" type="primary" :disabled="form.name.length === 0" @click="handleSubmit">
          {{ props.id ? '确认' : '创建' }}
        </n-button>
      </div>
    </div>
  </n-popover>
</template>

<style scoped>
.move-left {
  position: relative;
  right: 22px;
}
</style>