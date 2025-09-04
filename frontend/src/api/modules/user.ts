import {post} from "/@/api";
import type {IPageResponse, ITableQueryParams} from "/@/api/types/commons.ts";
import type {UserState} from "/@/store/modules/user/types.ts";

export const userApis = {
    fetchUserPage: (data: ITableQueryParams) => post<IPageResponse<UserState>>("/system/user/page", data),
    createUser: (data: UserState) => post("/system/user/save", data),
};