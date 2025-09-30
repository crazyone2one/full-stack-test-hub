<script setup lang="ts">
import type {RequestParam} from "/@/views/api-test/components/request-composition/index.vue";
import ExecuteButton from "/@/views/api-test/components/ExecuteButton.vue";
import {computed, defineAsyncComponent, ref} from "vue";
import {hasAnyPermission} from "/@/utils/permissions.ts";
import type {TabItem} from "/@/components/edit-able-tab/types.ts";
import type {ModuleTreeNode} from "/@/api/types/commons.ts";
import type {ApiCaseDetail, ApiDefinitionDetail} from "/@/api/types/api-test/management.ts";
import {cloneDeep} from "es-toolkit";
import {
  ProtocolKeyEnum, RequestAuthTypeEnum,
  RequestComposition,
  RequestDefinitionStatusEnum,
  RequestMethodsEnum, ResponseComposition
} from "/@/enums/api-enum.ts";
import {defaultBodyParams, defaultResponse, defaultResponseItem} from "/@/views/api-test/components/config.ts";

const requestComposition = defineAsyncComponent(
    () => import('/@/views/api-test/components/request-composition/index.vue')
);
const props = defineProps<{
  activeModule: string;
  offspringIds: string[];
  moduleTree: ModuleTreeNode[]; // 模块树
  selectedProtocols: string[];
  currentTab: string;
}>();

const emit = defineEmits<{
  (e: 'deleteApi', id: string): void;
  (e: 'import'): void;
  (e: 'handleAdvSearch', isStartAdvance: boolean): void;
  (e: 'openCaseTab', apiCaseDetail: ApiCaseDetail): void;
}>();
const requestCompositionRef = ref<InstanceType<typeof requestComposition>>();

const activeApiTab = defineModel<RequestParam>('activeApiTab', {
  required: true,
});
const apiTabs = defineModel<RequestParam[]>('apiTabs', {
  required: true,
});
const initDefaultId = `definition-${Date.now()}`;
const localProtocol = localStorage.getItem(ProtocolKeyEnum.API_NEW_PROTOCOL);
const defaultDefinitionParams: RequestParam = {
  type: 'api',
  definitionActiveKey: 'definition',
  id: initDefaultId,
  moduleId: props.activeModule === 'all' ? 'root' : props.activeModule,
  protocol: localProtocol || 'HTTP',
  tags: [],
  status: RequestDefinitionStatusEnum.PROCESSING,
  description: '',
  url: '',
  activeTab: RequestComposition.HEADER,
  label: '新建请求',
  closable: true,
  method: RequestMethodsEnum.GET,
  unSaved: false,
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
  mode: 'definition',
  executeLoading: false,
  preDependency: [], // 前置依赖
  postDependency: [], // 后置依赖
  errorMessageInfo: {},
};
const addApiTab = (defaultProps?: Partial<TabItem>) => {
  const id = `definition-${Date.now()}`;
  const protocol = localStorage.getItem(ProtocolKeyEnum.API_NEW_PROTOCOL);
  apiTabs.value.push({
    ...cloneDeep(defaultDefinitionParams),
    moduleId: props.activeModule === 'all' ? 'root' : props.activeModule,
    label: '新建接口',
    value: id,
    isNew: !defaultProps?.id, // 新开的tab标记为前端新增的调试，因为此时都已经有id了；但是如果是查看打开的会有携带id
    definitionActiveKey: !defaultProps ? 'definition' : 'preview',
    protocol: protocol || activeApiTab.value.protocol || defaultDefinitionParams.protocol, // 新开的tab默认使用当前激活的tab的协议
    ...defaultProps,
  });
  activeApiTab.value = apiTabs.value[apiTabs.value.length - 1];
};
const openApiTab = (options: {
  apiInfo: ModuleTreeNode | ApiDefinitionDetail | string;
  isCopy?: boolean;
  isExecute?: boolean;
  isEdit?: boolean;
  isDebugMock?: boolean;
}) => {
  console.log(options)
}
const contentTabList = computed(() => {
  const {isNew, protocol} = activeApiTab.value;
  const tabs = [
    {condition: !isNew, value: 'preview', label: '预览'},
    {
      condition: hasAnyPermission(['PROJECT_API_DEFINITION:READ+UPDATE', 'PROJECT_API_DEFINITION:READ+ADD']),
      value: 'definition',
      label: '定义',
    },
    {
      condition: !isNew && hasAnyPermission(['PROJECT_API_DEFINITION_CASE:READ']),
      value: 'case',
      label: '用例',
    },
    {condition: !isNew && protocol === 'HTTP', value: 'mock', label: 'MOCK'},
  ];
  return tabs.filter((tab) => tab.condition).map((tab) => ({value: tab.value, label: tab.label}));
});
defineExpose({addApiTab, openApiTab})
</script>

<template>
  <div class="flex flex-1 flex-col overflow-hidden">
    <div v-if="activeApiTab.id !== 'all'" class="flex-1 overflow-hidden">
      <div class="mt-[8px] flex items-center justify-between px-[16px]">
        <n-tabs v-model:value="activeApiTab.definitionActiveKey" type="line" animated>
          <n-tab-pane v-for="item of contentTabList" :name="item.value" :tab="item.label">
            {{ item.label }}
          </n-tab-pane>
        </n-tabs>
        <div v-if="activeApiTab.definitionActiveKey === 'preview'" class="flex gap-[12px]">
          <n-button v-permission="['PROJECT_API_DEFINITION:READ+DELETE']" size="small">删除</n-button>
          <n-button v-permission="['PROJECT_API_DEFINITION:READ+UPDATE']" size="small">编辑</n-button>
          <execute-button/>
        </div>
      </div>
      <div class="h-[calc(100%-32px)]">
        <request-composition v-if="activeApiTab.definitionActiveKey === 'definition'"
                             ref="requestCompositionRef"/>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>