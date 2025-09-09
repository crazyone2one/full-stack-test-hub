import type {ProjectListItem} from "/@/api/types/project.ts";
import type {RouteRecordRaw} from "vue-router";

export interface AppState {
    currentOrgId: string;
    currentProjectId: string;
    innerHeight: number;
    menuCollapse: boolean; // 菜单是否折叠
    loading: boolean
    loadingTip: string
    projectList: ProjectListItem[]
    currentMenuConfig:string[]
    topMenus: RouteRecordRaw[];
    currentTopMenu: RouteRecordRaw
}