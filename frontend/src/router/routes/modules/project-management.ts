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
        icon: 'i-mdi:application-cog',
        order: 1,
        hideChildrenInMenu: true,
        roles: [
            'PROJECT_BASE_INFO:READ',
            'PROJECT_TEMPLATE:READ',
            'PROJECT_FILE_MANAGEMENT:READ',
            'PROJECT_MESSAGE:READ',
            'PROJECT_CUSTOM_FUNCTION:READ',
            'PROJECT_LOG:READ',
            'PROJECT_ENVIRONMENT:READ',
            // 菜单管理
            'PROJECT_APPLICATION_WORKSTATION:READ',
            'PROJECT_APPLICATION_TEST_PLAN:READ',
            'PROJECT_APPLICATION_BUG:READ',
            'PROJECT_APPLICATION_CASE:READ',
            'PROJECT_APPLICATION_API:READ',
            'PROJECT_APPLICATION_UI:READ',
            'PROJECT_APPLICATION_PERFORMANCE_TEST:READ',
            // 菜单管理
            'PROJECT_USER:READ',
            'PROJECT_GROUP:READ',
        ],
    },
    children: [
        {
            path: 'permission',
            name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION,
            component: () => import('/@/views/project-management/project-permission/index.vue'),
            redirect: '/project-management/permission/basicInfo',
            meta: {
                locale: '项目与权限',
                roles: [
                    'PROJECT_BASE_INFO:READ',
                    // 菜单管理
                    'PROJECT_APPLICATION_WORKSTATION:READ',
                    'PROJECT_APPLICATION_TEST_PLAN:READ',
                    'PROJECT_APPLICATION_BUG:READ',
                    'PROJECT_APPLICATION_CASE:READ',
                    'PROJECT_APPLICATION_API:READ',
                    'PROJECT_APPLICATION_UI:READ',
                    'PROJECT_APPLICATION_PERFORMANCE_TEST:READ',
                    // 菜单管理
                    'PROJECT_USER:READ',
                    'PROJECT_GROUP:READ',
                ],
                isTopMenu: true,
            },
            children: [
                {
                    path: 'basicInfo',
                    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_BASIC_INFO,
                    component: () => import('/@/views/project-management/project-permission/basic-info/index.vue'),
                    meta: {
                        locale: '基本信息',
                        roles: ['PROJECT_BASE_INFO:READ'],
                    },
                },
            ]
        }
    ]
}
export default ProjectManagement