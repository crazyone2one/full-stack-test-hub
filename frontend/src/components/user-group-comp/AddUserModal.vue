<script setup lang="ts">
import {computed, inject, reactive, ref} from "vue";
import {AuthScopeEnum, type AuthScopeEnumType} from "/@/enums/common-enum.ts";
import {useAppStore} from "/@/store";
import {UserRequestTypeEnum} from "/@/components/user-selector/utils.ts";
import type {FormInst} from "naive-ui";
import {userGroupApis} from "/@/api/modules/user-group.ts";

const systemType = inject<AuthScopeEnumType>('systemType');
const props = defineProps<{ currentId: string; }>();
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void; }>();
const appStore = useAppStore();
const currentOrgId = computed(() => appStore.currentOrgId);
const formRef = ref<FormInst | null>(null);
const form = reactive({name: [],});
const userSelectorProps = computed(() => {
  if (systemType === AuthScopeEnum.SYSTEM) {
    return {
      type: UserRequestTypeEnum.SYSTEM_USER_GROUP,
      loadOptionParams: {
        roleId: props.currentId,
      },
      disabledKey: 'exclude',
    };
  }
  return {
    type: UserRequestTypeEnum.ORGANIZATION_USER_GROUP,
    loadOptionParams: {
      roleId: props.currentId,
      organizationId: currentOrgId.value,
    },
    disabledKey: 'checkRoleFlag',
  };
})
const handleCancel = (shouldSearch = false) => {
  form.name = [];
  emit('cancel', shouldSearch);
};
const handleConfirm = () => {
  formRef.value?.validate(async err => {
    if (!err) {
      if (systemType === AuthScopeEnum.SYSTEM) {
        await userGroupApis.addUserToUserGroup({roleId: props.currentId, userIds: form.name})
      }
      if (systemType === AuthScopeEnum.ORGANIZATION) {
        await userGroupApis.addOrgUserToUserGroup({
          userRoleId: props.currentId,
          userIds: form.name,
          organizationId: currentOrgId.value,
        })
      }
      handleCancel(true);
      window.$message.success('添加成功');
    }
  })

}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" @close="handleCancel(false)">
    <template #header>
      <div>添加成员</div>
    </template>
    <div>
      <n-form ref="formRef" :model="form" class="rounded-[4px]">
        <n-form-item path="name" label="成员" :rule="{required: true, message: '请选择成员'}">
          <user-selector v-model:model-value="form.name" v-bind="userSelectorProps"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <n-button secondary @click="handleCancel(false)">取消</n-button>
      <n-button type="primary" :disabled="form.name.length === 0" @click="handleConfirm">添加</n-button>
    </template>
  </n-modal>
</template>

<style scoped>

</style>