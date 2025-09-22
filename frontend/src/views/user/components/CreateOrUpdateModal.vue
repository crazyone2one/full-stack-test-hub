<script setup lang="ts">
import {type FormInst, type FormItemRule, NButton, NForm, NFormItem, NInput, NScrollbar} from "naive-ui";
import {onBeforeMount, ref, unref, watchEffect} from "vue";
import type {FormItemModel} from "/@/components/batch-form/types.ts";
import {validateEmail, validatePhone} from "/@/utils/validate.ts";
import {VueDraggable} from "vue-draggable-plus";
import type {ISystemRole, UserCreateInfo} from "/@/api/types/user.ts";
import {userApis} from "/@/api/modules/user.ts";

interface IUserForm {
  list: UserCreateInfo[];
  userGroup: string[];
}

const props = withDefaults(defineProps<{ userFormMode?: 'create' | 'edit' }>(), {userFormMode: 'create'})
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const userForm = defineModel<IUserForm>('userForm', {default: {list: [] as UserCreateInfo[], userGroup: []}})
const emit = defineEmits(['cancel', 'submit'])
const userGroupOptions = ref<ISystemRole[]>([]);
const batchFormModels = ref<FormItemModel[]>([
  {
    field: 'name',
    type: 'input',
    label: '用户名',
    rules: [{required: true, message: '姓名不能为空', trigger: ['blur', 'change']},
      {
        validator(_rule: FormItemRule, value: string) {
          if (value && value.length > 255) {
            return new Error('姓名长度不能超过 50')
          }
          return true
        }
      }],
    placeholder: '请输入用户姓名',
  },
  {
    field: 'email',
    type: 'input',
    label: '邮箱',
    rules: [{required: true, message: '邮箱不能为空', trigger: ['blur', 'change']},
      {
        validator(_rule: FormItemRule, value: string) {
          if (value && !validateEmail(value)) {
            return new Error('请输入正确的邮箱')
          }
          return true;
        }, trigger: ['blur', 'change']
      },
      {notRepeat: true, message: '邮箱不能重复'}],
    placeholder: '请输入邮箱地址',
  },
  {
    field: 'phone',
    type: 'input',
    label: '手机',
    rules: [{
      validator(_rule: FormItemRule, value: string) {
        if (value !== null && value !== '' && value !== undefined && !validatePhone(value)) {
          return new Error('请输入 11 位手机号')
        }
        return true
      },
      trigger: ['blur', 'change'],
    }],
    placeholder: '请输入 11 位手机号',
  },
])
const formRef = ref<FormInst | null>(null);

const formItem: Record<string, any> = {};

const addField = () => {
  const item = [{...formItem}];
  item[0].type = [];
  formValidate(() => {
    userForm.value.list.push(item[0] as UserCreateInfo); // 序号自增，不会因为删除而重复
  }, false);
}
const formValidate = (cb: (res?: Record<string, any>[]) => void, isSubmit = true) => {
  formRef.value?.validate(e => {
    if (!e) {
      if (typeof cb === 'function') {
        if (isSubmit) {
          cb(getFormResult());
          return;
        }
        cb();
      }
    }
  })
}
const getFormResult = () => {
  return unref<Record<string, any>[]>(userForm.value.list);
}
const removeField = (i: number) => {
  userForm.value.list.splice(i, 1);
}

const handleSubmit = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      if (props.userFormMode === 'create') {
        const params = {
          userInfoList: userForm.value.list,
          userRoleIdList: userForm.value.userGroup,
        };
        const res = await userApis.batchCreateUser(params)
        if (res.errorEmails !== null) {
          const errData: Record<string, any> = {};
          Object.keys(res.errorEmails).forEach((key) => {
            const filedIndex = userForm.value.list.findIndex((e) => e.email === key);
            if (filedIndex > -1) {
              errData[`list[${filedIndex}].email`] = {
                status: 'error',
                message: '邮箱已存在',
              };
            }
          });
        } else {
          window.$message.success('添加成功')
          emit('submit')
          showModal.value = false
        }
      } else {
        const activeUser = userForm.value.list[0];
        const params = {
          id: activeUser.id as string,
          name: activeUser.name,
          email: activeUser.email,
          phone: activeUser.phone,
          userRoleIdList: userForm.value.userGroup,
        };
        await userApis.updateUser(params)
        window.$message.success('更新成功')
        emit('submit')
        showModal.value = false
      }
    }
  })
}
const handleBeforeClose = () => {
  showModal.value = false
  formRef.value?.restoreValidation()
}
const init = () => {
  userApis.getSystemRoles().then(res => {
    userGroupOptions.value = res
    if (userGroupOptions.value.length) {
      userForm.value.userGroup = userGroupOptions.value.filter(item => item.selected).map(item => item.id)
    }
  })
}
watchEffect(() => {
  batchFormModels.value.forEach((e) => {
    // 默认填充表单项
    let value: string | number | boolean | string[] | number[] | undefined;
    if (e.type === 'inputNumber') {
      value = undefined;
    } else if (e.type === 'tagInput') {
      value = [];
    } else {
      value = e.defaultValue;
    }
    formItem[e.field] = value;
    // 默认填充表单项的子项
    e.children?.forEach((child) => {
      formItem[child.field] = child.type === 'inputNumber' ? null : child.defaultValue;
    });
  });
  userForm.value.list = [{...formItem} as UserCreateInfo];
});
onBeforeMount(() => {
  init()
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" :style="{width: 800+'px'}">
    <n-form ref="formRef" :model="userForm" label-placement="left"
            require-mark-placement="right-hanging">
      <div class="mb-[16px] overflow-y-auto border p-[12px]">
        <n-scrollbar class="overflow-y-auto" :style="{ 'max-height': '250px' }">
          <VueDraggable v-model="userForm.list"
                        ghost-class="ghost"
                        drag-class="dragChosenClass"
                        :force-fallback="true"
                        :animation="150"
                        handle=".dragIcon"
                        id="VueDraggable">
            <div v-for="(element, index) in userForm.list" :key="`${element.field}${index}`"
                 class="flex w-full justify-between rounded gap-[8px] py-[6px] pr-[8px]">
              <div v-show="userFormMode==='create'" class="dragIcon ml-[8px] mr-[8px] pt-[8px]">
                <div class="i-mdi:drag block text-[16px]"/>
              </div>
              <n-form-item v-for="model of batchFormModels" :key="`${model.field}${index}`"
                           :path="`list[${index}].${model.field}`"
                           :class="index > 0 ? 'hidden-item' : 'mb-0 flex-1' "
                           :label="index === 0 && model.label ? model.label : ''"
                           :rule="model.rules"
              >
                <n-input v-model:value="element[model.field]"
                         class="flex-1" :placeholder="model.placeholder || ''"
                         :maxlength="model.maxLength || 255" :disabled="model.disabled"
                         clearable/>
              </n-form-item>
              <div
                  v-show="userForm.list.length > 1"
                  class="flex h-[32px] w-[32px] cursor-pointer items-center justify-center rounded"
                  :style="{ 'margin-top': index === 0 && userFormMode !=='create' ? '4px' : '' }"
                  @click="removeField(index)"
              >
                <div class="i-mdi:minus-circle-outline"/>
              </div>
            </div>
          </VueDraggable>
        </n-scrollbar>
        <div v-if="userFormMode === 'create' " class="w-full">
          <n-button type="info" class="px-0" text @click="addField">添加用户</n-button>
        </div>
      </div>
      <n-form-item path="userGroup" label="用户组">
        <n-select v-model:value="userForm.userGroup" :options="userGroupOptions" placeholder="请选择用户组" multiple
                  value-field="id" label-field="name"/>
      </n-form-item>
    </n-form>
    <template #action>
      <div class="flex flex-row justify-between">
        <div class="flex flex-row gap-[12px]">
          <n-button type="tertiary" size="small" @click="handleBeforeClose">取消</n-button>
          <n-button type="primary" size="small" @click="handleSubmit">
            {{ userFormMode === 'create' ? '创建' : '保存' }}
          </n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>
.ghost {
  border: 1px dashed rgba(var(#cd3bff));
  background-color: rgba(var(#cd3bff));
  @apply rounded;
}
</style>