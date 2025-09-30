// 创建定义参数
import type {ExecuteRequestParams, IResponseDefinition} from "/@/api/types/api.ts";
import {type RequestCaseStatusType, type RequestDefinitionStatus, type RequestMethods} from "/@/enums/api-enum.ts";

// 定义-自定义字段
export interface ApiDefinitionCustomField {
    apiId: string;
    fieldId: string;
    value: string;
}

export interface ApiDefinitionCreateParams extends ExecuteRequestParams {
    tags: string[];
    response: IResponseDefinition[];
    description: string;
    status: RequestDefinitionStatus;
    customFields: ApiDefinitionCustomField[];
    moduleId: string;
    versionId: string;

    [key: string]: any; // 其他前端定义的参数
}

// 定义-自定义字段详情
export interface ApiDefinitionCustomFieldDetail {
    id: string;
    name: string;
    scene: string;
    type: string;
    remark: string;
    internal: boolean;
    scopeType: string;
    createTime: number;
    updateTime: number;
    createUser: string;
    refId: string;
    enableOptionKey: boolean;
    scopeId: string;
    value: string;
    apiId: string;
    fieldId: string;
}

export interface ApiDefinitionDetail extends ApiDefinitionCreateParams {
    id: string;
    name: string;
    protocol: string;
    method: RequestMethods | string;
    path: string;
    num: number;
    pos: number;
    projectId: string;
    moduleId: string;
    latest: boolean;
    versionId: string;
    refId: string;
    createTime: number;
    createUser: string;
    updateTime: number;
    updateUser: string;
    deleteUser: string;
    deleteTime: number;
    deleted: boolean;
    createUserName: string;
    updateUserName: string;
    deleteUserName: string;
    versionName: string;
    caseTotal: number;
    casePassRate: string;
    caseStatus: string;
    follow: boolean;
    customFields: ApiDefinitionCustomFieldDetail[];
}
// 用例列表和用例详情
export interface ApiCaseDetail extends ExecuteRequestParams {
    id: string;
    name: string;
    priority: string;
    num: number;
    status: RequestCaseStatusType;
    protocol: string;
    lastReportStatus: string;
    lastReportId: string;
    projectId: string;
    apiDefinitionId: string;
    environmentId: string;
    environmentName: string;
    follow: boolean;
    method: RequestMethods | string;
    path: string;
    tags: string[];
    passRate: string;
    modulePath: string;
    moduleId: string;
    createTime: number;
    createUser: string;
    createName: string;
    updateTime: number;
    updateUser: string;
    updateName: string;
    deleteTime: number;
    deleteUser: string;
    deleteName: string;
    apiChange: boolean; // 接口定义参数变更标识
    inconsistentWithApi: boolean; // 与接口定义不一致
    aiCreate: boolean; // 是否AI创建
}

// 添加模块参数
export interface AddModuleParams {
    projectId: string;
    name: string;
    parentId: string;
}
export interface ApiDefinitionUpdateModuleParams {
    id: string;
    name: string;
}
// 定义-获取模块树参数
export interface ApiDefinitionGetModuleParams {
    keyword: string;
    searchMode?: 'AND' | 'OR';
    filter?: Record<string, any>;
    combine?: Record<string, any>;
    moduleIds: string[];
    protocols: string[];
    projectId: string;
    versionId?: string;
    refId?: string;
    shareId?: string;
    orgId?: string; // 组织id
}