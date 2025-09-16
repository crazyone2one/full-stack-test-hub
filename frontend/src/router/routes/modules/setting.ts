import type {RouteRecordRaw} from "vue-router";
import {SettingRouteEnum} from "/@/enums/route-enum.ts";

const Setting: RouteRecordRaw = {
    path: '/setting',
    name: SettingRouteEnum.SETTING,
    component: () => import('/src/layout/index.vue'),
    meta: {
        locale: '系统设置',
        collapsedLocale: '系统',
        icon: 'i-mdi:tools',
        order: 8,
        roles: [
            'SYSTEM_USER:READ',
            'SYSTEM_USER_ROLE:READ',
            'SYSTEM_ORGANIZATION_PROJECT:READ',
            'SYSTEM_PARAMETER_SETTING_BASE:READ',
            'SYSTEM_PARAMETER_SETTING_DISPLAY:READ',
            'SYSTEM_PARAMETER_SETTING_AUTH:READ',
            'SYSTEM_PARAMETER_SETTING_MEMORY_CLEAN:READ',
            'SYSTEM_PARAMETER_SETTING_QRCODE:READ',
            'SYSTEM_TEST_RESOURCE_POOL:READ',
            'SYSTEM_AUTH:READ',
            'SYSTEM_PLUGIN:READ',
            'SYSTEM_LOG:READ',
            'SYSTEM_CASE_TASK_CENTER:READ',
            'SYSTEM_SCHEDULE_TASK_CENTER:READ',
            'ORGANIZATION_MEMBER:READ',
            'ORGANIZATION_USER_ROLE:READ',
            'ORGANIZATION_PROJECT:READ',
            'SYSTEM_SERVICE_INTEGRATION:READ',
            'ORGANIZATION_TEMPLATE:READ',
            'ORGANIZATION_LOG:READ',
            'ORGANIZATION_CASE_TASK_CENTER:READ',
            'ORGANIZATION_SCHEDULE_TASK_CENTER:READ',
        ],
    },
    children: [
        {
            path: 'system',
            name: SettingRouteEnum.SETTING_SYSTEM,
            component: null,
            meta: {
                locale: '系统',
                hideChildrenInMenu: true,
                roles: [
                    'SYSTEM_USER:READ',
                    'SYSTEM_USER_ROLE:READ',
                    'SYSTEM_ORGANIZATION_PROJECT:READ',
                    'SYSTEM_PARAMETER_SETTING_BASE:READ',
                    'SYSTEM_PARAMETER_SETTING_DISPLAY:READ',
                    'SYSTEM_PARAMETER_SETTING_AUTH:READ',
                    'SYSTEM_PARAMETER_SETTING_MEMORY_CLEAN:READ',
                    'SYSTEM_PARAMETER_SETTING_QRCODE:READ',
                    'SYSTEM_TEST_RESOURCE_POOL:READ',
                    'SYSTEM_AUTH:READ',
                    'SYSTEM_PLUGIN:READ',
                    'SYSTEM_LOG:READ',
                    'SYSTEM_CASE_TASK_CENTER:READ',
                    'SYSTEM_SCHEDULE_TASK_CENTER:READ',
                ],
            },
            children: [
                {
                    path: 'user',
                    name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
                    component: () => import('/src/views/user/index.vue'),
                    meta: {
                        roles: ['SYSTEM_USER:READ'],
                        locale: '用户',
                        isTopMenu: true,
                    }
                }
            ]
        },
        {
            path: 'organization',
            name: SettingRouteEnum.SETTING_ORGANIZATION,
            component: null,
            meta: {
                locale: '组织',
                hideChildrenInMenu: true,
                roles: [
                    'ORGANIZATION_MEMBER:READ',
                    'ORGANIZATION_USER_ROLE:READ',
                    'ORGANIZATION_PROJECT:READ',
                    'SYSTEM_SERVICE_INTEGRATION:READ',
                    'ORGANIZATION_TEMPLATE:READ',
                    'ORGANIZATION_LOG:READ',
                    'ORGANIZATION_CASE_TASK_CENTER:READ',
                    'ORGANIZATION_SCHEDULE_TASK_CENTER:READ',
                ],
            },
            children: [
                {
                    path: 'member',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_MEMBER,
                    component: () => import('/src/views/setting/organization/member/index.vue'),
                    meta: {
                        roles: ['ORGANIZATION_MEMBER:READ'],
                        locale: '成员',
                        isTopMenu: true,
                    }
                },
                {
                    path: 'usergroup',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_USER_GROUP,
                    component: () => import('/@/views/setting/organization/user-group/index.vue'),
                    meta: {
                        locale: '用户组',
                        roles: ['ORGANIZATION_USER_ROLE:READ'],
                        isTopMenu: true,
                    },
                },
                {
                    path: 'project',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
                    component: () => import('/src/views/setting/organization/project/index.vue'),
                    meta: {
                        roles: ['ORGANIZATION_PROJECT:READ'],
                        locale: '项目',
                        isTopMenu: true,
                    }
                }
            ]
        }
    ]
};
export default Setting;