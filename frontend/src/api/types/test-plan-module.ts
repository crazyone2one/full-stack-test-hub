export interface CreateOrUpdateModule {
    projectId: string;
    name: string;
    parentId: string;
}
// 更新模块
export interface UpdateModule {
    id: string;
    name: string;
}