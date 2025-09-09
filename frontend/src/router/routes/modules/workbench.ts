import type {RouteRecordRaw} from "vue-router";
import {WorkbenchRouteEnum} from "/@/enums/route-enum.ts";

const Dashboard: RouteRecordRaw = {
    path: '/workstation',
    name: WorkbenchRouteEnum.WORKBENCH,
    redirect: '/workstation/home',
    component: () => import('/src/layout/index.vue'),
    meta: {
        locale: '工作台',
        collapsedLocale: '首页',
        icon: 'i-mdi:monitor-dashboard',
        order: 0,
        hideChildrenInMenu: true,
    },
    children: [
        {
            path: 'home',
            name: WorkbenchRouteEnum.WORKBENCH_INDEX,
            component: () => import('/@/views/dashboard/index.vue'),
            meta: {
                locale: '首页',
                roles: ['*'],
                isTopMenu: true,
            },
        }
    ]
}
export default Dashboard