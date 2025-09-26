import type {ITableQueryParams} from "/@/api/types/commons.ts";
import type {
    ExecuteResultEnumType,
    ExecuteStatusEnumType,
    ExecuteTaskTypeType,
    ExecuteTriggerModeType
} from "/@/enums/task-center-enum.ts";

export interface ICreateTaskItem {
    projectId: string; // 项目ID
    name: string;
    value: string;
    job: string;
    enable: boolean;
    id?: string;
    [key: string]: any;
}

export interface ITaskCenterSystemTaskItem {
    organizationName: string; // 所属组织名称
    projectName: string; // 所属项目名称
    projectId: string; // 项目ID
    organizationId: string; // 组织ID
    id: string;
    reportId: string;
    taskName: string;
    resourceId: string; // 资源ID
    num: number;
    resourceType: string; // 资源类型
    resourceNum: number; // 资源num
    value: string;
    nextTime: number;
    enable: boolean;
    createUserId: string;
    createUserName: string;
    createTime: number;

    [key: string]: any;
}

export interface ITaskCenterBatchParams extends ITableQueryParams {
    taskId?: string;
    batchType?: string;
    resourcePoolIds?: string[];
    resourcePoolNodes?: string[];
}

export interface ITaskCenterTaskItem {
    id: string;
    reportId: string;
    num: number;
    taskName: string;
    status: string; // 执行状态
    caseCount: number;
    result: ExecuteResultEnumType; // 执行结果
    taskType: ExecuteTaskTypeType; // 任务类型
    resourceId: string;
    triggerMode: ExecuteTriggerModeType; // 执行方式
    projectId: string;
    organizationId: string;
    createTime: number;
    createUser: string;
    startTime: number;
    endTime: number;
    organizationName: string; // 所属组织名称
    projectName: string; // 所属项目名称
    createUserName: string; // 创建人
    [key: string]: any;
}

export interface ITaskCenterTaskDetailItem {
    id: string;
    reportId: string;
    taskId: string; // 任务ID
    resourceId: string;
    resourceName: string;
    taskOrigin: string; // 任务来源
    status: ExecuteStatusEnumType; // 执行状态
    result: ExecuteResultEnumType; // 执行结果
    resourcePoolId: string; // 资源池ID
    resourcePoolNode: string; // 资源池节点
    resourceType: string; // 资源类型
    projectId: string;
    organizationId: string;
    threadId: string; // 线程ID
    startTime: number;
    endTime: number;
    executor: string;
    taskName: string;
    userName: string;
    resourcePoolName: string;
    triggerMode: string; // 触发方式
    lineNum: number | string;
}

export interface TTaskCenterStatisticsItem {
    id: string;
    executeRate: number; // 执行率
    successCount: number; // 成功数
    errorCount: number; // 失败数
    fakeErrorCount: number; // 误报数
    pendingCount: number; // 待执行数
    caseTotal: number; // 用例总数
}

export interface TTaskCenterResourcePoolStatus {
    id: string;
    status: boolean; // 状态, true: 正常, false: 异常
}

export interface TTaskCenterResourcePoolItem {
    id: string;
    name: string;
    children: TTaskCenterResourcePoolItem[];
}

export interface TTaskCenterBatchTaskReportItem {
    id: string;
    source: string;
    integrated: boolean; // 是否集合报告
    name: string;
    status: string;
    execResult: string;
    triggerMode: ExecuteTriggerModeType;
    createUser: string;
    createTime: number;
}
export interface IRunConfig {
    runMode: 'SERIAL' | 'PARALLEL'
    [key: string]: any;
}
export interface ICreateTask {
    resourceId: string;
    enable: boolean;
    cron: string;
    runConfig: IRunConfig;
}