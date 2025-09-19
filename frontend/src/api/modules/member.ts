import {get, post} from "/@/api";
import type {SelectOption} from "naive-ui";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {IUserItem} from "/@/api/types/user.ts";

export const memberApis = {
    // 获取用户组下拉
    getGlobalUserGroup: (organizationId: string) => get<Array<SelectOption>>(`/organization/user/role/list/${organizationId}`),
    // 系统设置-组织-项目-分页获取成员列表
    fetchOrganizationMemberPage: (param: ITableQueryParams) => post<IPageResponse<IUserItem>>("/organization/project/user-list", param),
};