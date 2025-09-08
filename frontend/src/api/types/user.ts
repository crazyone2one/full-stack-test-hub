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