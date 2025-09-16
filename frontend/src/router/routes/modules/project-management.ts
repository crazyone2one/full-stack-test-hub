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
                {
                    path: 'menuManagement',
                    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_MENU_MANAGEMENT,
                    component: () => import('/@/views/project-management/project-permission/menu-management/index.vue'),
                    meta: {
                        locale: '应用设置',
                        roles: [
                            'PROJECT_APPLICATION_WORKSTATION:READ',
                            'PROJECT_APPLICATION_TEST_PLAN:READ',
                            'PROJECT_APPLICATION_BUG:READ',
                            'PROJECT_APPLICATION_CASE:READ',
                            'PROJECT_APPLICATION_API:READ',
                            'PROJECT_APPLICATION_UI:READ',
                            'PROJECT_APPLICATION_PERFORMANCE_TEST:READ',
                        ],
                    },
                },
                {
                    path: 'projectVersion',
                    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_VERSION,
                    component: () => import('/@/views/project-management/project-permission/project-version/index.vue'),
                    meta: {
                        locale: '项目版本',
                        roles: ['PROJECT_VERSION:READ'],
                    },
                },
                {
                    path: 'member',
                    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_MEMBER,
                    component: () => import('/@/views/project-management/project-permission/member/index.vue'),
                    meta: {
                        locale: '成员',
                        roles: ['PROJECT_USER:READ'],
                    },
                },
                {
                    path: 'projectUserGroup',
                    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_USER_GROUP,
                    component: () => import('/@/views/project-management/project-permission/user-group/index.vue'),
                    meta: {
                        locale: '用户组',
                        roles: ['PROJECT_GROUP:READ'],
                    },
                },
            ]
        },
        {
            path: 'fileManagement',
            name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_FILE_MANAGEMENT,
            component: () => import('/@/views/project-management/file-management/index.vue'),
            meta: {
                locale: '文件管理',
                roles: ['PROJECT_FILE_MANAGEMENT:READ'],
                isTopMenu: true,
            },
        },
        {
            path: 'log',
            name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_LOG,
            component: () => import('/@/views/project-management/log/index.vue'),
            meta: {
                locale: '日志',
                roles: ['PROJECT_LOG:READ'],
                isTopMenu: true,
            },
        },
    ]
}
export default ProjectManagement