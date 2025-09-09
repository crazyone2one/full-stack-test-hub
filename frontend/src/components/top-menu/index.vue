<script setup lang="ts">
import {computed, h, ref} from "vue";
import {type MenuOption, NMenu} from "naive-ui";
import {listenerRouteChange} from "/@/utils/route-listener.ts";
import appClientMenus from "/@/router/app-menus";
import {type RouteRecordRaw, RouterLink} from "vue-router";
import {cloneDeep} from "es-toolkit";
import usePermission from "/@/hooks/use-permission.ts";
import {useAppStore} from "/@/store";

const menuOptions = computed(() => {
  const menus: Array<MenuOption> = []
  appStore.appState.topMenus.forEach((route: RouteRecordRaw) => {
    menus.push({
      label: () => h(RouterLink, {to: {name: route?.name as string}}, {default: () => route.meta?.locale}),
      key: route.name as string,
    })
  })
  return menus
});
const activeKey = ref<string | null>(null)
const copyRouters = cloneDeep(appClientMenus) as RouteRecordRaw[];
const permission = usePermission();
const appStore = useAppStore()
const setCurrentTopMenu = (key: string) => {
  const secParentFullSame = appStore.appState.topMenus.find((route: RouteRecordRaw) => {
    return key === route?.name;
  });

  // 非全等的情况下，一定是父子路由包含关系
  const secParentLike = appStore.appState.topMenus.find((route: RouteRecordRaw) => {
    return key.includes(route?.name as string);
  });

  if (secParentFullSame) {
    appStore.setCurrentTopMenu(secParentFullSame);
  } else if (secParentLike) {
    appStore.setCurrentTopMenu(secParentLike);
  }
}
listenerRouteChange((newRoute) => {
  const {name} = newRoute;
  for (let i = 0; i < copyRouters.length; i++) {
    const firstRoute = copyRouters[i];
    // 权限校验通过
    if (permission.accessRouter(firstRoute)) {
      if (name && firstRoute?.name && (name as string).includes(firstRoute.name as string)) {
        // 先判断二级菜单是否顶部菜单
        let currentParent = firstRoute?.children?.some((item) => item.meta?.isTopMenu)
            ? (firstRoute as RouteRecordRaw)
            : undefined;

        if (!currentParent) {
          // 二级菜单非顶部菜单，则判断三级菜单是否有顶部菜单
          currentParent = firstRoute?.children?.find(
              (item) => name && item?.name && (name as string).includes(item.name as string)
          );
        }

        let filterMenuTopRouter =
            currentParent?.children?.filter((item: any) => permission.accessRouter(item) && item.meta?.isTopMenu) || [];
        appStore.setTopMenus(filterMenuTopRouter);
        setCurrentTopMenu(name as string);
        activeKey.value = name as string;
        return;
      }
    }
  }
  // 切换到没有顶部菜单的路由时，清空顶部菜单
  appStore.setTopMenus([]);
  setCurrentTopMenu('');
  activeKey.value = null;
}, true);
</script>

<template>
  <div v-show="appStore.appState.topMenus.length > 0">
    <n-menu
        v-model:value="activeKey"
        mode="horizontal"
        :options="menuOptions"
        responsive
    />
  </div>

</template>

<style scoped>

</style>