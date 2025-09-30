<script setup lang="ts">
import type {FormInst, FormItemRule, FormRules} from "naive-ui";
import {ref, watch} from "vue";
import {useAppStore} from "/@/store";

interface FieldConfig {
  field?: string;
  rules?: FormRules[];
  placeholder?: string;
  maxLength?: number;
  isTextArea?: boolean;
}

const props = defineProps<{
  mode: 'add' | 'rename' | 'tabRename';
  nodeType?: 'MODULE' | 'API';
  visible?: boolean;
  title?: string;
  allNames: string[];
  popupContainer?: string;
  fieldConfig?: FieldConfig;
  parentId?: string; // 父节点 id
  nodeId?: string; // 节点 id
  popupOffset?: number;
  addModuleApi?: (params: { projectId: string; parentId: string; name: string }) => Promise<any>;
  updateModuleApi?: (params: { id: string; name: string }) => Promise<any>;
  updateApiNodeApi?: (params: { id: string; name: string }) => Promise<any>;
  repeatMessage?: string;
}>();
const appStore = useAppStore();
const innerVisible = ref(props.visible || false);
const emit = defineEmits(['update:visible', 'close', 'addFinish', 'renameFinish']);
const formRef = ref<FormInst | null>(null)
const form = ref({
  field: props.fieldConfig?.field || '',
});
const rules = {
  field: [
    {
      required: true,
      message: '请输入名称',
      trigger: 'blur',
    },
    {
      validator: (_rule: FormItemRule, value: string) => {
        if (props.allNames.includes(value)) {
          return new Error(props.repeatMessage || '该层级已有此模块名称')
        }
      }
    }
  ]
}
const handleSubmit = (done?: (closed: boolean) => void) => {
  formRef.value?.validate(async (error) => {
    if (!error) {
      if (props.mode === 'add' && props.addModuleApi) {
        await props.addModuleApi({
          projectId: appStore.currentProjectId,
          parentId: props.parentId || '',
          name: form.value.field,
        });
        window.$message.success('添加成功');
        emit('addFinish', form.value.field);
      } else if (props.mode === 'rename' && props.updateModuleApi) {
        // 模块重命名
        await props.updateModuleApi({
          id: props.nodeId || '',
          name: form.value.field,
        });
        window.$message.success('更新成功');
        emit('renameFinish', form.value.field, props.nodeId);
      }
      if (done) {
        done(true);
      } else {
        innerVisible.value = false;
      }
    } else if (done) {
      done(false);
    }
  })
}
watch(
    () => props.fieldConfig?.field,
    (val) => {
      form.value.field = val || '';
    },
    {
      deep: true,
    }
);
</script>

<template>
  <n-popconfirm v-model:show="innerVisible"
                @positive-click="handleSubmit(undefined)"
  >
    <template #trigger>
      <slot></slot>
    </template>
    <div class="mb-[8px] font-medium">
      {{ props.title || (props.mode === 'add' ? '添加子模块' : '重命名') }}
    </div>
    <n-form ref="formRef" :model="form" :rules="rules">
      <n-form-item path="field">
        <n-input v-if="props.fieldConfig?.isTextArea" type="textarea" v-model:value="form.field"
                 placeholder="请输入名称"/>
        <n-input v-else v-model:value="form.field" placeholder="请输入名称"/>
      </n-form-item>
    </n-form>
  </n-popconfirm>
</template>

<style scoped>

</style>