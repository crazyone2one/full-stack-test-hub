<script setup lang="ts">
import type {ExecuteApiRequestFullParams, IRequestTaskResult, IResponseItem, ITabItem} from "/@/api/types/api.ts";
import type {RequestCompositionType} from "/@/enums/api-enum.ts";

export interface IRequestCustomAttr {
  type: 'api' | 'case' | 'mock' | 'doc'; // 展示的请求 tab 类型；api包含了接口调试和接口定义
  isNew: boolean;
  protocol: string;
  activeTab: RequestCompositionType;
  mode?: 'definition' | 'debug'; // 接口定义时，展示的定义模式/调试模式（显示的 tab 不同）
  executeLoading: boolean; // 执行中loading
  isCopy?: boolean; // 是否是复制
  isExecute?: boolean; // 是否是执行
  errorMessageInfo?: {
    [key: string]: Record<string, any>;
  };
}

export type RequestParam = ExecuteApiRequestFullParams & {
  responseDefinition?: IResponseItem[];
  response?: IRequestTaskResult;
} & IRequestCustomAttr & ITabItem;
</script>

<template>

</template>

<style scoped>

</style>