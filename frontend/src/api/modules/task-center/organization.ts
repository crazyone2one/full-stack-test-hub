import {get, post} from "/@/api";
import type {ProjectListItem} from "/@/api/types/project.ts";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {ITaskCenterSystemTaskItem} from "/@/api/types/task-center.ts";

export const organizationApi = {
    // 组织任务-项目列表
    organizationProjectOptions: () => get<Array<ProjectListItem>>("/organization/task-center/project/options"),
    getOrganizationScheduleList: (data: ITableQueryParams) => post<IPageResponse<ITaskCenterSystemTaskItem>>("/organization/task-center/schedule/page", data),
}