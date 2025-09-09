import type {RouteRecordRaw} from "vue-router";
import {ProjectManagementRouteEnum} from "/@/enums/route-enum.ts";

const ProjectManagement: RouteRecordRaw = {
    path: '/project-management',
    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT,
    redirect: '/project-management/permission',
    component: () => import('/src/layout/index.vue'),
    meta: {
        locale: '项目管理',
        collapsedLocale: '项目',
        icon: 'i-mdi:tools',
        order: 1,
        hideChildrenInMenu: true,
    },
    children: [
        {
            path: 'permission',
            name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION,
            component: () => import('/@/views/project-management/project-permission/index.vue'),
            redirect: '/project-management/permission/basicInfo',
            meta: {
                locale: '项目与权限',
                isTopMenu: true,
            },
            children: [
                {
                    path: 'basicInfo',
                    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_BASIC_INFO,
                    component: () => import('/@/views/project-management/project-permission/basic-info/index.vue'),
                    meta: {
                        locale: '基本信息',
                    },
                },
            ]
        }
    ]
}
export default ProjectManagement