export interface UserCreateInfo {
    id?: string;
    name: string;
    email: string;
    phone: string;
    userRoleIdList?: string[];
}

export interface ISystemRole {
    id: string;
    name: string;
    selected: boolean; // 是否可选
    closeable: boolean; // 是否可取消
}

export interface UserBatchCreate {
    userInfoList: UserCreateInfo[];
    userRoleIdList: string[];
}

// 用户所属组织模型
export interface IOrganizationListItem {
    id: string;
    num: number;
    name: string;
    description: string;
    createTime: number;
    updateTime: number;
    createUser: string;
    updateUser: string;
    deleted: boolean; // 是否删除
    deleteUser: string;
    deleteTime: number;
    enable: boolean; // 是否启用
}

// 用户所属用户组模型
export interface IUserRoleListItem {
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

export interface IUserItem {
    id: string;
    name: string;
    email: string;
    password: string;
    enable: boolean;
    createTime: number;
    updateTime: number;
    language: string;
    lastOrganizationId: string;
    phone: string;
    source: string;
    lastProjectId: string;
    createUser: string;
    updateUser: string;
    organizationList: IOrganizationListItem[]; // 用户所属组织
    userRoleList: IUserRoleListItem[]; // 用户所属用户组
    userRoles?: IUserRoleListItem[]; // 用户所属用户组
    selectUserGroupVisible?: boolean;
}
