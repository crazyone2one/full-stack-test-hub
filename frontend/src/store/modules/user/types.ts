export interface UserRoleListItem {
    id: string;
    name: string;
    description: string;
    internal: boolean; // 是否内置用户组
    type: string; // 所属类型 SYSTEM ORGANIZATION PROJECT
    createTime: number;
    updateTime: number;
    createUser: string;
    scopeId: string; // 应用范围
}

export interface UserState {
    id: string;
    name: string;
    email: string;
    phone: string;
    lastOrganizationId?: string;
    lastProjectId?: string;
    avatar?: string;
    userRolePermissions: UserRolePermissions[];
    userRoles?: UserRole[];
    userRoleRelations?: UserRoleRelation[];
    userRoleList: UserRoleListItem[]; // 用户所属用户组
    userGroup?: string[]
}

export type RoleType = '' | '*' | 'admin' | 'user';
export type SystemScopeType = 'PROJECT' | 'ORGANIZATION' | 'SYSTEM';

export interface UserRole {
    createTime: number;
    updateTime: number;
    createUser: string;
    description?: string;
    id: string;
    name: string;
    scopeId: string; // 项目/组织/系统 id
    type: SystemScopeType;
}

export interface permissionsItem {
    id: string;
    permissionId: string;
    roleId: string;
}

export interface UserRoleRelation {
    id: string;
    userId: string;
    roleId: string;
    sourceId: string;
    organizationId: string;
    createTime: number;
    createUser: string;
    userRolePermissions: permissionsItem[];
    userRole: UserRole;
}

export interface UserRolePermissions {
    userRole: UserRole;
    userRolePermissions: permissionsItem[];
}