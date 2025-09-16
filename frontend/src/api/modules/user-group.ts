import {get, post} from "/@/api";
import type {
    ISaveGlobalUSettingData,
    ISystemUserGroupParams,
    IUserGroupAuthSetting,
    IUserGroupItem
} from "/@/api/types/user-group.ts";

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
}