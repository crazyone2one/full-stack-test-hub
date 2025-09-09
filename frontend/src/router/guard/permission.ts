import {featureRouteMap, NO_RESOURCE_ROUTE_NAME, WHITE_LIST} from "/@/router/constants.ts";
import {useAppStore} from "/@/store";
import type {Router} from "vue-router";
import usePermission from "/@/hooks/use-permission.ts";

export default function setupPermissionGuard(router: Router) {
    router.beforeEach(async (to, _from, next) => {
        const appStore = useAppStore();
        const Permission = usePermission();
        const permissionsAllow = Permission.accessRouter(to);
        // 如果是隐藏的模块，则跳转到无权限页面
        const moduleId = Object.keys(featureRouteMap).find((key) => (to.name as string)?.includes(key));
        if (moduleId && featureRouteMap[moduleId] && !appStore.appState.currentMenuConfig.includes(featureRouteMap[moduleId])) {
            next({
                name: NO_RESOURCE_ROUTE_NAME,
            });
        }
        const exist = WHITE_LIST.find((el) => el.name === to.name);
        if (exist || permissionsAllow) {
            next();
        } else {
            next({
                name: NO_RESOURCE_ROUTE_NAME,
            });
        }
    });
}