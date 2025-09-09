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
    },
    children: [
        {
            path: 'system',
            name: SettingRouteEnum.SETTING_SYSTEM,
            component: null,
            meta: {
                locale: '系统',
                hideChildrenInMenu: true,
            },
            children: [
                {
                    path: '/user',
                    name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
                    component: () => import('/src/views/user/index.vue'),
                    meta: {
                        title: '用户',
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
            },
            children: [
                {
                    path: 'member',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_MEMBER,
                    component: () => import('/src/views/setting/organization/member/index.vue'),
                    meta: {
                        title: '成员',
                        locale: '成员',
                        isTopMenu: true,
                    }
                },
                {
                    path: 'project',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
                    component: () => import('/src/views/setting/organization/project/index.vue'),
                    meta: {
                        title: '项目',
                        locale: '项目',
                        isTopMenu: true,
                    }
                }
            ]
        }
    ]
};
export default Setting;