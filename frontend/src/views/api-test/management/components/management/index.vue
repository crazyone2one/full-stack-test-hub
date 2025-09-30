<script setup lang="ts">
import {provide, readonly, ref} from "vue";
import {hasAnyPermission} from "/@/utils/permissions.ts";
import EditAbleTab from '/@/components/edit-able-tab/index.vue'
import type {RequestParam} from "/@/views/api-test/components/request-composition/index.vue";
import type {ModuleTreeNode} from "/@/api/types/commons.ts";
import {
  RequestAuthTypeEnum,
  RequestComposition,
  RequestDefinitionStatusEnum,
  RequestMethodsEnum,
  ResponseComposition
} from "/@/enums/api-enum.ts";
import {cloneDeep} from "es-toolkit";
import {defaultBodyParams, defaultResponse, defaultResponseItem} from "/@/views/api-test/components/config.ts";
import api from './api/index.vue';

const props = defineProps<{
  activeModule: string;
  offspringIds: string[];
  selectedProtocols: string[];
  moduleTree: ModuleTreeNode[]; // 模块树
}>();
const currentTab = ref('api');
const apiRef = ref<InstanceType<typeof api>>();
const tabOptions = [
  {label: 'API', value: 'api'},
  ...(hasAnyPermission(['PROJECT_API_DEFINITION_CASE:READ']) ? [{label: 'CASE', value: 'case'}] : []),
  ...(hasAnyPermission(['PROJECT_API_DEFINITION_MOCK:READ']) ? [{label: 'MOCK', value: 'mock'}] : []),
];
const apiTabs = ref<RequestParam[]>([
  {
    id: 'all',
    label: '全部接口',
    closable: false,
    moduleId: 'root',
  } as unknown as RequestParam,
]);
const activeApiTab = ref<RequestParam>(apiTabs.value[0] as RequestParam);
const newTab = (apiInfo?: ModuleTreeNode | string, isCopy?: boolean, isExecute?: boolean) => {
  if (apiInfo) {
    apiRef.value?.openApiTab({
      apiInfo,
      isCopy,
      isExecute,
    });
  } else {
    apiRef.value?.addApiTab();
  }
}
const initDefaultId = `case-${Date.now()}`;

const defaultCaseParams: RequestParam = {
  id: initDefaultId,
  type: 'case',
  moduleId: props.activeModule === 'all' ? 'root' : props.activeModule,
  protocol: 'HTTP',
  tags: [],
  description: '',
  priority: 'P0',
  status: RequestDefinitionStatusEnum.PROCESSING,
  url: '',
  activeTab: RequestComposition.HEADER,
  closable: true,
  method: RequestMethodsEnum.GET,
  headers: [],
  body: cloneDeep(defaultBodyParams),
  query: [],
  rest: [],
  polymorphicName: '',
  name: '',
  path: '',
  projectId: '',
  uploadFileIds: [],
  linkFileIds: [],
  authConfig: {
    authType: RequestAuthTypeEnum.NONE,
    basicAuth: {
      userName: '',
      password: '',
    },
    digestAuth: {
      userName: '',
      password: '',
    },
  },
  children: [
    {
      polymorphicName: 'MsCommonElement', // 协议多态名称，写死MsCommonElement
      assertionConfig: {
        enableGlobal: true,
        assertions: [],
      },
      postProcessorConfig: {
        enableGlobal: true,
        processors: [],
      },
      preProcessorConfig: {
        enableGlobal: true,
        processors: [],
      },
    },
  ],
  otherConfig: {
    connectTimeout: 60000,
    responseTimeout: 60000,
    certificateAlias: '',
    followRedirects: true,
    autoRedirects: false,
  },
  responseActiveTab: ResponseComposition.BODY,
  response: cloneDeep(defaultResponse),
  responseDefinition: [cloneDeep(defaultResponseItem)],
  isNew: true,
  unSaved: false,
  executeLoading: false,
  preDependency: [], // 前置依赖
  postDependency: [], // 后置依赖
  errorMessageInfo: {},
};

/** 向孙组件提供属性 */
provide('defaultCaseParams', readonly(defaultCaseParams));
defineExpose({newTab})
</script>

<template>
  <div class="flex gap-[8px] px-[16px] pt-[16px]">
    <n-select v-model:value="currentTab" :options="tabOptions" class="w-[80px]"/>
    <EditAbleTab v-model:tabs="apiTabs" v-model:active-tab="activeApiTab">
      <template #label="{tab}">
        {{tab.protocol === 'HTTP' ? tab.method : tab.protocol}}
        {{tab.name || tab.label}}
      </template>
    </EditAbleTab>
  </div>
  {{props.activeModule}}
  <api v-show="(activeApiTab.id === 'all' && currentTab === 'api') || activeApiTab.type === 'api'" ref="apiRef"
       v-model:active-api-tab="activeApiTab" v-model:api-tabs="apiTabs" :active-module="props.activeModule"
       :offspring-ids="props.offspringIds"
       :selected-protocols="props.selectedProtocols"
       :module-tree="props.moduleTree"
       :current-tab="currentTab"/>
  <div v-show="(activeApiTab.id === 'all' && currentTab === 'case') || activeApiTab.type === 'case'">apiCase</div>
</template>

<style scoped>

</style>