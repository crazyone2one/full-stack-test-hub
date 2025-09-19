import type {SelectOption} from "naive-ui";
import type {IUserItem} from "../types/user";
import {get, post} from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {IAddUserToOrgOrProjectParams, ProjectTableItem} from "/@/api/types/project.ts";

export const orgProjectApis = {
    // 获取组织下拉选项
    getSystemOrgOption: () => post<Array<SelectOption>>("system/organization/option/all"),
    // 系统-获取项目列表
    postProjectTable: (param: ITableQueryParams) => post<IPageResponse<ProjectTableItem>>("system/project/page", param),
    // 系统设置-系统-组织与项目-获取添加成员列表
    fetchSystemMemberPage: (param: ITableQueryParams) => post<IPageResponse<IUserItem>>("/system/organization/member-list", param),
    // 根据 orgId 或 projectId 获取用户列表
    fetchUserPageByOrgIdOrProjectId: (param: ITableQueryParams) =>
        post<IPageResponse<IUserItem>>(
            param.organizationId
                ? "/system/organization/list-member"
                : "/system/project/member-list",
            param
        ),
    // 获取项目和组织的总数
    getOrgAndProjectCount: () => get<{ organizationTotal: number; projectTotal: number }>("system/organization/total"),
    // 给组织或项目添加成员
    addUserToOrgOrProject: (data: IAddUserToOrgOrProjectParams) => post(data.projectId ? "/system/project/add-member" : '/system/organization/add-member', data),
    addProjectMemberByOrg: (data: IAddUserToOrgOrProjectParams) => post('/organization/project/add-members', data),
    // 删除组织或项目成员
    deleteUserFromOrgOrProject: (sourceId: string, userId: string, isOrg = true) =>
        get(`${isOrg ? '/system/organization/remove-member/' : '/system/project/remove-member/'}${sourceId}/${userId}`),
};
