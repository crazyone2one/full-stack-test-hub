import {get, post} from "/@/api";
import type {ProjectListItem} from "/@/api/types/project.ts";
import type {IOrganizationListItem} from "/@/api/types/user.ts";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {ICreateTaskItem, ITaskCenterSystemTaskItem} from "/@/api/types/task-center.ts";

export const systemApis = {
    // 系统任务-项目列表
    systemProjectOptions: () => get<Array<ProjectListItem>>("/system/task-center/project/options"),
    // 系统任务-组织列表
    systemOrgOptions: () => get<Array<IOrganizationListItem>>("/system/task-center/organization/options"),
    // 系统任务-系统后台任务列表
    getSystemScheduleList: (data: ITableQueryParams) => post<IPageResponse<ITaskCenterSystemTaskItem>>("/system/task-center/schedule/page", data),
    // 系统任务-添加任务
    saveSystemSchedule: (data: ICreateTaskItem) => post("/system/task-center/schedule/save", data),
}