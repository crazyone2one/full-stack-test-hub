<script setup lang="ts">
import type {FormInst, FormRules, SelectOption} from "naive-ui";
import type {CreateOrUpdateOrgProjectParams} from "/@/api/types/project.ts";
import {computed, ref} from "vue";
import {useForm} from "alova/client";
import {projectManagementApis} from "/@/api/modules/project-management.ts";
import {useAppStore} from "/@/store";

const props = defineProps<{
  currentProject?: CreateOrUpdateOrgProjectParams;
}>();
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void; }>();
const isEdit = computed(() => !!(props.currentProject && props.currentProject.id));
// const allModuleIds = ['bugManagement', 'caseManagement', 'apiTest', 'testPlan'];
const allModuleIds = ['apiTest', 'testPlan'];
const appStore = useAppStore();
const currentOrgId = computed(() => appStore.currentOrgId);
const showPoolModuleIds = ['apiTest', 'testPlan'];

const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
  name: [
    {required: true, message: '项目名称不能为空', trigger: ['blur', 'input'],},
    {max: 255, message: '名称不能超过 255 个字符'}
  ],
  // allResourcePool: {required: showPoolModuleIds.some((item) => form.moduleIds?.includes(item)), message: '资源池不能为空', trigger: ['blur', 'input'],}
}
const moduleOption = [
  // { label: 'menu.workbench', value: 'workstation' },
  {label: '测试计划', value: 'testPlan'},
  // { label: 'menu.bugManagement', value: 'bugManagement' },
  // { label: 'menu.caseManagement', value: 'caseManagement' },
  {label: '接口测试', value: 'apiTest'},
  // { label: 'menu.uiTest', value: 'uiTest' },
  // { label: 'menu.performanceTest', value: 'loadTest' },
];
const affiliatedOrgOption = ref<SelectOption[]>([]);
const {form, reset, loading} = useForm(formData => projectManagementApis.createOrUpdateProjectByOrg(formData), {
  immediate: false,
  initialForm: {
    name: '',
    userIds: [],
    organizationId: currentOrgId.value,
    description: '',
    resourcePoolIds: [],
    enable: true,
    moduleIds: allModuleIds,
    allResourcePool: true,
  }
})

const handleCancel = () => {
  emit('cancel', false)
  reset()
  formRef.value?.restoreValidation()
}
const handleSubmit = () => {
  formRef.value?.validate(errors => {
    if (!errors) {
      console.log(form.value)
    }
  })
}
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog" @close="handleCancel">
    <template #header>
      <div v-if="isEdit" class="flex">
        更新项目
        <div class="ml-[4px] flex max-w-[300px]">
          {{ props.currentProject?.name }}
        </div>
      </div>
      <div v-else>创建项目</div>
    </template>
    <div class="form">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="auto"
              require-mark-placement="right-hanging"
      >
        <n-form-item label="项目名称" path="name">
          <n-input v-model:value="form.name" clearable placeholder="请输入项目名称，不可与其他项目名称重复"/>
        </n-form-item>
        <n-form-item label="所属组织" path="organizationId">
          <n-select v-model:value="form.organizationId" disabled :options="affiliatedOrgOption"
                    placeholder="请选择所属组织"/>
        </n-form-item>
        <n-form-item label="启用模块" path="module">
          <n-checkbox-group v-model:value="form.moduleIds">
            <n-space item-style="display: flex;">
              <n-checkbox v-for="item in moduleOption" :key="item.value" :value="item.value" :label="item.label"/>
            </n-space>
          </n-checkbox-group>
        </n-form-item>
        <n-form-item v-if="showPoolModuleIds.some((item) => form.moduleIds?.includes(item))" label="资源池"
                     path="allResourcePool" class="!mb-0">
          <n-radio-group v-model:value="form.allResourcePool" name="allResourcePool" class="mb-[16px]">
            <n-space>
              <n-radio :value="true">全部资源池</n-radio>
              <n-radio :value="false">指定资源池</n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>
        <n-form-item v-if="!form.allResourcePool" path="resourcePoolIds">

        </n-form-item>
        <n-form-item label="描述" path="description">
          <n-input v-model:value="form.description" clearable type="textarea" placeholder="请对该项目进行描述"
                   :autosize="{minRows:1}" :maxlength="1000"/>
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <div class="flex flex-row justify-between">
        <div class="flex flex-row items-center gap-[4px]">
          <n-switch size="small"/>
          <span>状态</span>
          <n-tooltip>
            <template #trigger>
              <div class="i-mdi:progress-question"/>
            </template>
            项目开启后，将展示在项目切换列表
          </n-tooltip>
        </div>
        <div class="flex flex-row gap-[14px]">
          <n-button secondary :disabled="loading" @click="handleCancel">取消</n-button>
          <n-button type="primary" @click="handleSubmit">
            {{ isEdit ? '确认' : '创建' }}
          </n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>