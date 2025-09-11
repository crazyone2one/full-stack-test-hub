import {get, post} from "/@/api";
import type {CreateOrUpdateOrgProjectParams, ProjectListItem, ProjectTableItem} from "/@/api/types/project.ts";
import type {IPageResponse, ITableQueryParams, IUserSelectorOption} from "/@/api/types/commons.ts";
import {type SelectOption} from "naive-ui";


export const projectManagementApis = {
    fetchProjectList: (organizationId: string) => get<Array<ProjectListItem>>(`/project/list/options/${organizationId}`, {}, {
        cacheFor: 0
    }),
    /**
     *
     * @param id
     * @param isEnable
     */
    enableOrDisableProjectByOrg: (id: string, isEnable = true) =>
        get(`${isEnable ? '/organization/project/enable/' : '/organization/project/disable/'}${id}`),
    fetchAdminByProjectByOrg: (organizationId: string, keyword: string) =>
        get<Array<IUserSelectorOption>>(`organization/project/user-admin-list/${organizationId}`, {keyword}),
    // 获取组织下拉选项
    fetchSystemOrgOption: () => post<Array<SelectOption>>('system/organization/option/all'),
    fetchProjectPageByOrg: (param: ITableQueryParams) => post<IPageResponse<ProjectTableItem>>('organization/project/page', param, {
        cacheFor: 0
    }),
    createOrUpdateProjectByOrg: (param: CreateOrUpdateOrgProjectParams) =>
        post(param.id ? '/organization/project/update' : '/organization/project/add', param)
}