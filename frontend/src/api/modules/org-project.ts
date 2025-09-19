import type { SelectOption } from "naive-ui";
import type { IUserItem } from "../types/user";
import { get, post } from "/@/api";
import type { IPageResponse, ITableQueryParams } from "/@/api/types/commons.ts";
import type { ProjectTableItem } from "/@/api/types/project.ts";

export const orgProjectApis = {
  // 获取组织下拉选项
  getSystemOrgOption: () =>
    post<Array<SelectOption>>("system/organization/option/all"),
  // 系统-获取项目列表
  postProjectTable: (param: ITableQueryParams) =>
    post<IPageResponse<ProjectTableItem>>("system/project/page", param),
  // 根据 orgId 或 projectId 获取用户列表
  fetchUserPageByOrgIdOrProjectId: (param: ITableQueryParams) =>
    post<IPageResponse<IUserItem>>(
      param.organizationId
        ? "/system/organization/list-member"
        : "/system/project/member-list",
      param
    ),
  // 获取项目和组织的总数
  getOrgAndProjectCount: () =>
    get<{ organizationTotal: number; projectTotal: number }>(
      "system/organization/total"
    ),
};
