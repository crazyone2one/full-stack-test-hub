import {get, post} from "/@/api";
import type {ITableQueryParams, ModuleTreeNode} from "/@/api/types/commons.ts";
import type {CreateOrUpdateModule, UpdateModule} from "/@/api/types/test-plan-module.ts";

export const testPlanApi = {
    getTestPlanModule: (params: ITableQueryParams) => get<ModuleTreeNode[]>(`/test-plan/module/tree/${params.projectId}`),
    // 删除模块
    deletePlanModuleTree: (id: string) => get(`/test-plan/module/remove/${id}`),
    // 创建模块树
    createPlanModuleTree: (data: CreateOrUpdateModule) => post('/test-plan/module/save', data),
    // 更新模块树
    updatePlanModuleTree: (data: UpdateModule) => post('/test-plan/module/update', data),
}