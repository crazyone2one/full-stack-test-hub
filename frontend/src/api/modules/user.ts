import {get, post, put} from "/@/api";
import type {IPageResponse, ITableQueryParams, ITransferOption} from "/@/api/types/commons.ts";
import type {
    BatchAddParams,
    DeleteUserParams,
    ICreateUserParams,
    ICreateUserResult,
    ISystemRole,
    IUpdateUserStatusParams,
    IUserItem,
    UpdateUserInfoParams,
    UserCreateInfo
} from "/@/api/types/user.ts";
import {formatPhoneNumber} from "/@/utils";

export const userApis = {
    fetchUserPage: (data: ITableQueryParams) => post<IPageResponse<IUserItem>>("/system/user/page", data, {
        transform(rawData: IPageResponse<IUserItem>) {
            return rawData.records.map((item: IUserItem) => ({
                ...item,
                selectUserGroupVisible: false,
                selectUserGroupLoading: false,
                phone: formatPhoneNumber(item.phone || ''),
            }));
        },
    }),
    updateUser: (data: UpdateUserInfoParams) => put("/system/user/update", data),
    createUser: (data: UserCreateInfo) => post("/system/user/save", data),
    batchCreateUser: (data: ICreateUserParams) => post<ICreateUserResult>("/system/user/add", data),
    // 删除用户
    deleteUserInfo: (data: DeleteUserParams) => post("/system/user/delete", data),
    toggleUserStatus: (data: IUpdateUserStatusParams) => post("/system/user/update/enable", data),
    // 批量添加用户到多个用户组
    batchAddUserGroup: (data: BatchAddParams) => post("/system/user/add/batch/user-role", data),
    // 批量添加用户到多个项目
    batchAddProject: (data: BatchAddParams) => post("/system/user/add-project-member", data),
    // 批量添加用户到多个组织
    batchAddOrg: (data: BatchAddParams) => post("/system/user/add-org-member", data),
    getSystemRoles: () => get<Array<ISystemRole>>("/system/user/get/global/system/role"),
    // 获取系统项目
    getSystemProjects: () => get<Array<ITransferOption>>("/system/user/get/project"),
    // 获取系统组织组
    getSystemOrgs: () => get<Array<ITransferOption>>("/system/user/get/organization"),
    // 重置用户密码
    resetUserPassword: (data: DeleteUserParams) => post("/system/user/reset/password", data),
};