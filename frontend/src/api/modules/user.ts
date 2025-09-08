import {post, put} from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {UserState} from "/@/store/modules/user/types.ts";
import type {UserCreateInfo} from "/@/api/types/user.ts";

export const userApis = {
    fetchUserPage: (data: ITableQueryParams) => post<IPageResponse<UserState>>("/system/user/page", data),
    updateUser: (data: UserCreateInfo) => put("/system/user/update", data),
    createUser: (data: UserCreateInfo) => post("/system/user/save", data),
};