import {post} from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {ITaskCenterSystemTaskItem} from "/@/api/types/task-center.ts";

export const projectApis = {
    // 项目任务-系统后台任务列表
    getProjectScheduleList: (data: ITableQueryParams) => post<IPageResponse<ITaskCenterSystemTaskItem>>("/project/task-center/schedule/page", data),
}