import {post} from "/@/api";
import type {
    AddModuleParams,
    ApiDefinitionGetModuleParams,
    ApiDefinitionUpdateModuleParams
} from "/@/api/types/api-test/management.ts";
import type {TreeOption} from "naive-ui";
export const apiManagementApis = {
    // 添加模块
    addModule: (data: AddModuleParams) => post('/api/definition/module/save', data),
    // 更新模块
    updateModule: (data: ApiDefinitionUpdateModuleParams) => post('/api/definition/module/update', data),
    // 获取模块树
    getModuleTree: (data: ApiDefinitionGetModuleParams) => post<TreeOption[]>('/api/definition/module/tree', data),
};