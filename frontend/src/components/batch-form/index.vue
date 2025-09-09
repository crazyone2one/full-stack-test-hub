<script setup lang="ts">
import {type FormInst, type FormRules, NButton, NForm, NFormItem, NInput, NScrollbar} from "naive-ui";
import type {FormItemModel, FormMode} from "/@/components/batch-form/types.ts";
import {ref, unref, watchEffect} from "vue";
import {VueDraggable} from "vue-draggable-plus";

export interface BatchFormProps {
  models: FormItemModel[];
  formMode: FormMode;
  addText?: string;
  maxHeight?: string;
  defaultVals?: any[]; // 当外层是编辑状态时，可传入已填充的数据
  isShowDrag?: boolean; // 是否可以拖拽
  formWidth?: string; // 自定义表单区域宽度
  showEnable?: boolean; // 是否显示启用禁用switch状态
  hideAdd?: boolean; // 是否隐藏添加按钮
  addToolTip?: string;
  enableType?: 'circle' | 'round' | 'line';
}

const props = withDefaults(defineProps<BatchFormProps>(), {
  maxHeight: '30vh',
  isShowDrag: true,
  hideAdd: false,
  enableType: 'line',
});
const formRef = ref<FormInst | null>(null);
const defaultForm = {
  list: [] as Record<string, any>[],
};
const form = ref<Record<string, any>>({list: [...defaultForm.list]});
const formItem: Record<string, any> = {};
const addField = () => {
  const item = [{...formItem}];
  item[0].type = [];
  formValidate(() => {
    form.value.list.push(item[0]); // 序号自增，不会因为删除而重复
  }, false);
}
const formValidate = (cb: (res?: Record<string, any>[]) => void, isSubmit = true) => {
  formRef.value?.validate(e => {
    if (e) {
      return;
    }
    if (typeof cb === 'function') {
      if (isSubmit) {
        cb(getFormResult());
        return;
      }
      cb();
    }
  })
}
const getFormResult = () => {
  return unref<Record<string, any>[]>(form.value.list);
}
const removeField = (i: number) => {
  form.value.list.splice(i, 1);
}

watchEffect(() => {
  props.models.forEach((e) => {
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
    if (props.showEnable) {
      // 如果有开启关闭状态，将默认禁用
      formItem.enable = false;
    }
    // 默认填充表单项的子项
    e.children?.forEach((child) => {
      formItem[child.field] = child.type === 'inputNumber' ? null : child.defaultValue;
    });
  });
  form.value.list = [{...formItem}];
  if (props.defaultVals?.length) {
    // 取出defaultVals的表单 field
    form.value.list = props.defaultVals.map((e) => e);
  }
});
</script>

<template>
  <n-form ref="formRef" :model="form" >
    <div class="mb-[16px] overflow-y-auto border p-[12px]"
         :style="{ width: props.formWidth || '100%' }">
      <n-scrollbar class="overflow-y-auto" :style="{ 'max-height': props.maxHeight }">
        <VueDraggable v-model="form.list"
                      ghost-class="ghost"
                      drag-class="dragChosenClass"
                      :disabled="!props.isShowDrag"
                      :force-fallback="true"
                      :animation="150"
                      handle=".dragIcon"
                      id="VueDraggable">
          <div v-for="(element, index) in form.list"
               :key="`${element.field}${index}`"
               class="flex w-full justify-between rounded gap-[8px] py-[6px] pr-[8px]"
               :class="[props.isShowDrag ? 'cursor-move' : '']">
            <div v-if="props.isShowDrag" class="dragIcon ml-[8px] mr-[8px] pt-[36px]">
              <div class="i-mdi:drag block text-[16px]"/>
            </div>
            <n-form-item v-for="model of props.models" :key="`${model.field}${index}`"
                         :path="model.field"
                         :class="index > 0 ? 'hidden-item' : 'mb-0 flex-1' "
                         :rules="model.rules">
              <n-input v-if="model.type === 'input'" v-model:value="element[model.field]"
                       class="flex-1" :placeholder="model.placeholder || ''"
                       :maxlength="model.maxLength || 255" :disabled="model.disabled"
                       clearable/>
            </n-form-item>
            <div v-if="showEnable">

            </div>
            <div v-if="!props.hideAdd"
                 v-show="form.list.length > 1"
                 class="flex h-[32px] w-[32px] cursor-pointer items-center justify-center rounded"
                 :style="{ 'margin-top': '30px' }"
                 @click="removeField(index)">
              <div class="i-mdi:minus-circle-outline"/>
            </div>
          </div>
        </VueDraggable>
      </n-scrollbar>
      <div v-if="props.formMode === 'create' && !props.hideAdd" class="w-full">
        <n-button class="px-0" text @click="addField">{{ props.addText || '添加' }}</n-button>
      </div>
    </div>
  </n-form>
</template>

<style scoped>
.dragChosenClass {
  opacity: 1 !important;
  border-radius: 0.25rem;

  .minus {
    margin: 0 !important;
  }
}
</style>