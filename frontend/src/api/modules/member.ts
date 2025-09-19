import {get} from "/@/api";
import type {SelectOption} from "naive-ui";

export const memberApis = {
    // 获取用户组下拉
    getGlobalUserGroup: (organizationId: string) => get<Array<SelectOption>>(`/organization/user/role/list/${organizationId}`),
};