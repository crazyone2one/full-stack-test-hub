export interface UserCreateInfo {
    id?: string;
    name: string;
    email: string;
    phone: string;
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