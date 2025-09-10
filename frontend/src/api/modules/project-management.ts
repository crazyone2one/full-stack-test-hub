import {get, post} from "/@/api";
import type {ProjectListItem, ProjectTableItem} from "/@/api/types/project.ts";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";

export const projectManagementApis = {
    fetchProjectList: (organizationId: string) => get<Array<ProjectListItem>>(`/project/list/options/${organizationId}`),
    fetchProjectPageByOrg: (param: ITableQueryParams) => post<IPageResponse<ProjectTableItem>>('organization/project/page', param)
}