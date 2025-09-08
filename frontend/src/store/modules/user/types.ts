export interface UserState {
    id: string;
    name: string;
    email: string;
    phone: string;
    lastOrganizationId?: string;
    lastProjectId?: string;
    avatar?: string;
}