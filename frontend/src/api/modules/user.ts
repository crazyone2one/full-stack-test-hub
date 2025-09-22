import {get, post, put} from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {UserState} from "/@/store/modules/user/types.ts";
import type {
    DeleteUserParams,
    ICreateUserParams,
    ICreateUserResult,
    ISystemRole,
    IUpdateUserStatusParams,
    UpdateUserInfoParams,
    UserCreateInfo
} from "/@/api/types/user.ts";

export const userApis = {
    fetchUserPage: (data: ITableQueryParams) => post<IPageResponse<UserState>>("/system/user/page", data),
    updateUser: (data: UpdateUserInfoParams) => put("/system/user/update", data),
    createUser: (data: UserCreateInfo) => post("/system/user/save", data),
    batchCreateUser: (data: ICreateUserParams) => post<ICreateUserResult>("/system/user/add", data),
    deleteUserInfo: (data: DeleteUserParams) => post("/system/user/delete", data),
    toggleUserStatus: (data: IUpdateUserStatusParams) => post("/system/user/update/enable", data),
    getSystemRoles: () => get<Array<ISystemRole>>("/system/user/get/global/system/role"),
};