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