import type {RouteRecordRaw} from "vue-router";
import {TestPlanRouteEnum} from "/@/enums/route-enum.ts";

const TestPlan:RouteRecordRaw = {
    path: '/test-plan',
    name: TestPlanRouteEnum.TEST_PLAN,
    redirect: '/test-plan/testPlanIndex',
    component: () => import('/src/layout/index.vue'),
    meta: {
        locale: '测试计划',
        collapsedLocale: '计划',
        icon: 'icon-a-icon_test-tracking_filled1',
        order: 2,
        hideChildrenInMenu: true,
        roles: ['PROJECT_TEST_PLAN:READ', 'PROJECT_TEST_PLAN_REPORT:READ'],
    },
    children:[
        // 测试计划
        {
            path: 'testPlanIndex',
            name: TestPlanRouteEnum.TEST_PLAN_INDEX,
            component: () => import('/@/views/plan/index.vue'),
            meta: {
                locale: '计划',
                roles: ['PROJECT_TEST_PLAN:READ'],
                isTopMenu: true,
            },
        },
        {
            path: 'testPlanReport',
            name: TestPlanRouteEnum.TEST_PLAN_REPORT,
            component: () => import('/@/views/plan/report/index.vue'),
            meta: {
                locale: '报告',
                roles: ['PROJECT_TEST_PLAN_REPORT:READ'],
                isTopMenu: true,
            },
        },
    ]
};
export default TestPlan;