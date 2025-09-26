import type {RouteRecordRaw} from "vue-router";
import {ApiTestRouteEnum} from "/@/enums/route-enum.ts";

const ApiTest: RouteRecordRaw = {
    path: '/api-test',
    name: ApiTestRouteEnum.API_TEST,
    redirect: '/api-test/management',
    component: () => import('/src/layout/index.vue'),
    meta: {
        locale: '接口测试',
        collapsedLocale: '接口',
        icon: 'icon-icon_api-test-filled2',
        order: 4,
        hideChildrenInMenu: true,
        roles: [
            'PROJECT_API_DEBUG:READ',
            'PROJECT_API_DEFINITION:READ',
            'PROJECT_API_DEFINITION_CASE:READ',
            'PROJECT_API_DEFINITION_MOCK:READ',
            'PROJECT_API_SCENARIO:READ',
            'PROJECT_API_REPORT:READ',
        ],
    },
    children: [
        {
            path: 'debug',
            name: ApiTestRouteEnum.API_TEST_DEBUG_MANAGEMENT,
            component: () => import('/@/views/api-test/debug/index.vue'),
            meta: {
                locale: '调试',
                roles: ['PROJECT_API_DEBUG:READ'],
                isTopMenu: true,
            },
        },
        {
            path: 'management',
            name: ApiTestRouteEnum.API_TEST_MANAGEMENT,
            component: () => import('/@/views/api-test/management/index.vue'),
            meta: {
                locale: '定义',
                roles: ['PROJECT_API_DEFINITION:READ'],
                isTopMenu: true,
            },
        },
        {
            path: 'report',
            name: ApiTestRouteEnum.API_TEST_REPORT,
            component: () => import('/@/views/api-test/report/index.vue'),
            meta: {
                locale: '报告',
                roles: ['PROJECT_API_REPORT:READ'],
                isTopMenu: true,
            },
        },
    ]
}
export default ApiTest;