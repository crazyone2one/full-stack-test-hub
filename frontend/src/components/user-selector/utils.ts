import {projectManagementApis} from "/@/api/modules/project-management.ts";
import {userGroupApis} from "/@/api/modules/user-group.ts";

export const UserRequestTypeEnum = {
    SYSTEM_USER_GROUP: 'SYSTEM_USER_GROUP',
    SYSTEM_ORGANIZATION: 'SYSTEM_ORGANIZATION',
    SYSTEM_ORGANIZATION_ADMIN: 'SYSTEM_ORGANIZATION_ADMIN',
    SYSTEM_PROJECT: 'SYSTEM_PROJECT',
    SYSTEM_PROJECT_ADMIN: 'SYSTEM_PROJECT_ADMIN',
    ORGANIZATION_USER_GROUP: 'ORGANIZATION_USER_GROUP',
    ORGANIZATION_USER_GROUP_ADMIN: 'ORGANIZATION_USER_GROUP_ADMIN',
    ORGANIZATION_PROJECT: 'ORGANIZATION_PROJECT',
    ORGANIZATION_PROJECT_ADMIN: 'ORGANIZATION_PROJECT_ADMIN',
    SYSTEM_ORGANIZATION_PROJECT: 'SYSTEM_ORGANIZATION_PROJECT',
    SYSTEM_ORGANIZATION_MEMBER: 'SYSTEM_ORGANIZATION_MEMBER',
    PROJECT_PERMISSION_MEMBER: 'PROJECT_PERMISSION_MEMBER',
    PROJECT_USER_GROUP: 'PROJECT_USER_GROUP',
    SYSTEM_ORGANIZATION_LIST: 'SYSTEM_ORGANIZATION_LIST',
    SYSTEM_PROJECT_LIST: 'SYSTEM_PROJECT_LIST',
    EXECUTE_USER: 'EXECUTE_USER',
} as const
export type UserRequestTypeEnumType = typeof UserRequestTypeEnum[keyof typeof UserRequestTypeEnum];
export const initOptionsFunc = (type: string, params: Record<string, any>) => {
    if (type === UserRequestTypeEnum.SYSTEM_USER_GROUP) {
        return userGroupApis.getSystemUserGroupOption(params.roleId, params.keyword)
    }
    if (type === UserRequestTypeEnum.ORGANIZATION_USER_GROUP) {
        return userGroupApis.getOrgUserGroupOption(params.organizationId, params.roleId, params.keyword)
    }
    if (type === UserRequestTypeEnum.ORGANIZATION_PROJECT_ADMIN) {
        // 组织 - 项目-添加管理员-下拉选项
        return projectManagementApis.fetchAdminByProjectByOrg(params.organizationId, params.keyword);
    }
}