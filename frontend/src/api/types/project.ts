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