import {get, post} from "/@/api";
import type {
    ISaveGlobalUSettingData,
    ISystemUserGroupParams,
    IUserGroupAuthSetting,
    IUserGroupItem
} from "/@/api/types/user-group.ts";
import type {IPageResponse, ITableQueryParams, IUserSelectorOption} from "/@/api/types/commons.ts";
import type {IUserItem} from "/@/api/types/user.ts";

export const userGroupApis = {
    updateOrAddUserGroup: (data: ISystemUserGroupParams) => post<IUserGroupItem>(data.id ? "user/role/global/update" : "user/role/global/add", data),
    updateOrAddProjectUserGroup: (data: ISystemUserGroupParams) => post<IUserGroupItem>(data.id ? "user/role/project/update" : "user/role/project/add", data),
    updateOrAddOrgUserGroup: (data: ISystemUserGroupParams) => post<IUserGroupItem>(data.id ? "user/role/organization/update" : "user/role/global/add", data),

    // 系统-编辑用户组对应的权限配置
    saveGlobalUSetting: (data: ISaveGlobalUSettingData) => post<IUserGroupAuthSetting[]>("user/role/global/permission/update", data),
    // 组织-编辑用户组对应的权限配置
    saveOrgUSetting: (data: ISaveGlobalUSettingData) => post<IUserGroupAuthSetting[]>("user/role/organization/permission/update", data),
    // 项目-编辑用户组对应的权限配置
    saveProjectUGSetting: (data: ISaveGlobalUSettingData) => post<IUserGroupAuthSetting[]>("user/role/project/permission/update", data),
    getUserGroupList: () => get<IUserGroupItem[]>("user/role/global/list", {}, {cacheFor: 0}),
    getOrgUserGroupList: (organizationId: string) => get<IUserGroupItem[]>(`/user/role/organization/list/${organizationId}`, {}, {cacheFor: 0}),
    getProjectUserGroupList: (projectId: string) => get<IUserGroupItem[]>(`/user/role/organization/list/${projectId}`, {}, {cacheFor: 0}),
    // 系统-获取用户组对应的权限配置
    getGlobalUSetting: (id: string) => get<IUserGroupAuthSetting[]>(`/user/role/global/permission/setting/${id}`),
    getOrgUSetting: (id: string) => get<IUserGroupAuthSetting[]>(`/user/role/organization/permission/setting/${id}`),
    // 项目-获取用户组对应的权限
    getAuthByUserGroup: (id: string) => get<IUserGroupAuthSetting[]>(`/user/role/project/permission/setting/${id}`),
    // 项目-删除用户组
    deleteUserGroup: (id: string) => get(`/user/role/project/delete/${id}`),
    // 项目-获取用户组列表
    fetchUserGroupPage: (data: ITableQueryParams) => post<IPageResponse<IUserGroupItem>>("/user/role/project/page", data),
    // 系统-获取用户组对应的用户列表
    fetchUserByUserGroup: (data: ITableQueryParams) => post<IPageResponse<IUserItem>>("/user/role/relation/global/list", data),
    // 组织-获取用户组对应的用户列表
    fetchOrgUserByUserGroup: (data: ITableQueryParams) => post<IPageResponse<IUserItem>>("/user/role/organization/list-member", data),
    // 系统-获取需要关联的用户选项
    getSystemUserGroupOption: (id: string, keyword: string) => get<IUserSelectorOption[]>(`/user/role/relation/global/user/option/${id}`, {params: {keyword}}),
    getOrgUserGroupOption: (organizationId: string, roleId: string, keyword: string) =>
        get<IUserSelectorOption[]>(`/user/role/organization/get-member/option/${organizationId}/${roleId}`, {params: {keyword}}),
    // 系统-添加用户到用户组
    addUserToUserGroup: (data: { roleId: string; userIds: string[] }) => post("/user/role/relation/global/add", data),
    // 组织-添加用户到用户组
    addOrgUserToUserGroup: (data: { userRoleId: string; userIds: string[]; organizationId: string }) =>
        post("/user/role/organization/add-member", data),
}