import type {IUserItem} from "/@/api/types/user.ts";
import type {IResourcePoolItem} from "/@/api/types/resource-pool.ts";

export interface ProjectListItem {
    id: string;
    num: number;
    organizationId: string;
    name: string;
    description: string;
}

export interface ProjectTableItem {
    id: string;
    num: number;
    organizationId: string;
    organizationName: string;
    name: string;
    description: string;
    memberCount: number;
    enable: boolean;
    adminList: IUserItem[];
    moduleIds: string[];
    resourcePoolList: IResourcePoolItem[];
    allResourcePool: boolean;
    projectCode: string;
}

export interface CreateOrUpdateOrgProjectParams {
    id?: string;
    name: string;
    projectCode: string;
    description?: string;
    enable?: boolean;
    userIds?: string[];
    moduleIds?: string[];
    organizationId?: string;
    resourcePoolIds?: string[];
    allResourcePool: boolean;
}
export interface AdminList {
    id: string;
    name: string;
    email: string;
    password: string;
    enable: boolean;
    createTime: string;
    updateTime: number;
    language: string;
    lastOrganizationId: string;
    phone: string;
    source: string;
    lastProjectId: string;
    createUser: string;
    updateUser: string;
    deleted: boolean; // 是否删除
    adminFlag: boolean; // 是否组织/项目管理员
    memberFlag: boolean; // 是否组织/项目成员
    checkRoleFlag: boolean; // 是否属于用户组
    sourceId: string; // 资源id
}
export interface IProjectBasicInfo {
    id: string;
    num: number;
    organizationId: string;
    name: string;
    description: string;
    createTime: string;
    updateTime: number;
    updateUser: string;
    createUser: string;
    deleteTime: number;
    deleted: boolean;
    deleteUser: string;
    enable: boolean;
    moduleSetting: string; // 模块设置
    memberCount: number; // 项目成员数量
    organizationName: string;
    adminList: AdminList[]; // 管理员
    projectCreateUserIsAdmin: boolean; // 创建人是否是管理员
    moduleIds: string[];
    resourcePoolList: { name: string; id: string }[]; // 资源池列表
}