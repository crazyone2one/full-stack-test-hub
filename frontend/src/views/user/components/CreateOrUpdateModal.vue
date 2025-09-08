<script setup lang="ts">
import {type FormInst, type FormItemRule, NButton, NForm, NFormItem, NInput, NScrollbar} from "naive-ui";
import BaseModal from "/@/components/BaseModal.vue";
import {ref, unref, watchEffect} from "vue";
import type {FormItemModel} from "/@/components/batch-form/types.ts";
import {validateEmail, validatePhone} from "/@/utils/validate.ts";
import {VueDraggable} from "vue-draggable-plus";

const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false})
const emit = defineEmits(['cancel', 'submit'])
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
const defaultForm = {
  list: [] as Record<string, any>[],
};
const formItem: Record<string, any> = {};
const form = ref<Record<string, any>>({list: [...defaultForm.list]});
const userFormMode = ref<'create' | 'edit'>('create');
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
  form.value.list = [{...formItem}];
});
const addField = () => {
  const item = [{...formItem}];
  item[0].type = [];
  formValidate(() => {
    form.value.list.push(item[0]); // 序号自增，不会因为删除而重复
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
  return unref<Record<string, any>[]>(form.value.list);
}
const removeField = (i: number) => {
  form.value.list.splice(i, 1);
}
const handleSubmit = () => {
  console.log(form.value.list)
}
</script>

<template>
  <base-modal v-model:show-modal="showModal" :modal-width="800" @submit="handleSubmit">
    <n-form ref="formRef" :model="form" label-placement="left"
            require-mark-placement="right-hanging">
      <div class="mb-[16px] overflow-y-auto border p-[12px]">
        <n-scrollbar class="overflow-y-auto" :style="{ 'max-height': '250px' }">
          <VueDraggable v-model="form.list"
                        ghost-class="ghost"
                        drag-class="dragChosenClass"
                        :force-fallback="true"
                        :animation="150"
                        handle=".dragIcon"
                        id="VueDraggable">
            <div v-for="(element, index) in form.list" :key="`${element.field}${index}`"
                 class="flex w-full justify-between rounded gap-[8px] py-[6px] pr-[8px]">
              <div class="dragIcon ml-[8px] mr-[8px] pt-[8px]">
                <div class="i-mdi:drag block text-[16px]"/>
              </div>
              <n-form-item v-for="model of batchFormModels" :key="`${model.field}${index}`"
                           :path="`list[${index}].${model.field}`"
                           :class="index > 0 ? 'hidden-item' : 'mb-0 flex-1' "
                           :rule="model.rules?.map(e=>{
                             if (e.notRepeat === true) {
                                console.log('11111')
                                return {
                                  // validator: (val, callback) => fieldNotRepeat(val, callback, index, model.field, e.message),
                                  validator(_rule: FormItemRule, value: string) {
                                     // 添加对空值的处理
                                    if (!value || value.trim() === '') {
                                      return true;
                                    }
                                    for (let i = 0; i < form.list.length; i++) {
                                      if (i !== index && form.list[i][model.field] && form.list[i][model.field].trim() === value) {

                                        return new Error(e.message as string)
                                      }
                                    }
                                      return true
                                    },
                                    trigger: e.trigger || ['blur', 'change']
                                };
                             }
                             return e;
                           })"
                           :label="index === 0 && model.label ? model.label : ''"
              >
                <n-input v-model:value="element[model.field]"
                         class="flex-1" :placeholder="model.placeholder || ''"
                         :maxlength="model.maxLength || 255" :disabled="model.disabled"
                         clearable/>
              </n-form-item>
              <div
                  v-show="form.list.length > 1"
                  class="flex h-[32px] w-[32px] cursor-pointer items-center justify-center rounded"
                  :style="{ 'margin-top': index === 0 ? '4px' : '' }"
                  @click="removeField(index)"
              >
                <div class="i-mdi:minus-circle-outline"/>
              </div>
            </div>
          </VueDraggable>
        </n-scrollbar>
        <div v-if="userFormMode === 'create' " class="w-full">
          <n-button class="px-0" text @click="addField">添加用户</n-button>
        </div>
      </div>
    </n-form>
  </base-modal>
</template>

<style scoped>

</style>