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
    deleted: boolean;
}