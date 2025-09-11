// 资源池配置信息对象
export interface INodesListItem {
    ip: string;
    port: string;
    concurrentNumber: number;
    singleTaskConcurrentNumber: number; // 单个任务最大并发数
}
// 应用组织id和name映射对象
export interface IOrgIdNameMap {
    id: string;
    name: string;
}
export interface TestResourceDTO {
    nodesList: INodesListItem[]; // node资源
    ip: string; // k8s ip
    token: string; // k8s token
    namespace: string; // k8s 命名空间
    concurrentNumber: number; // k8s 最大并发数
    singleTaskConcurrentNumber: number; // 单个任务最大并发数
    podThreads: number; // k8s 单pod最大线程数
    jobDefinition: string; // k8s job自定义模板
    deployName: string; // k8s api测试部署名称
    uiGrid: string; // ui测试selenium-grid
    girdConcurrentNumber: number; // ui测试selenium-grid最大并发数
    orgIds: string[]; // 应用范围选择指定组织时的id集合
    orgIdNameMap: IOrgIdNameMap[]; // 应用范围选择指定组织时的id和name映射
}
export interface IResourcePoolInfo {
    name: string; // 资源池名称
    description: string; // 资源池描述
    type: string; // 资源池类型
    enable: boolean; // 是否启用
    apiTest: boolean; // 是否支持api测试
    uiTest: boolean; // 是否支持ui测试
    serverUrl: string; // 资源池地址
    allOrg: boolean; // 是否应用范围选择全部组织
    testResourceDTO: TestResourceDTO; // 测试资源信息对象
}
// 资源池列表项对象
export interface IResourcePoolItem extends IResourcePoolInfo {
    id: string;
}