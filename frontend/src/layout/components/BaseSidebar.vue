<script setup lang="ts">

import {type MenuOption, NLayoutSider, NMenu} from "naive-ui";
import {computed, h, ref} from "vue";
import {type RouteRecordRaw, RouterLink} from "vue-router";
import useMenuTree from "/@/hooks/use-menu-tree.ts";
import {listenerRouteChange} from "/@/utils/route-listener.ts";
import {getFirstRouterNameByCurrentRoute} from "/@/utils/permissions.ts";

const expandedKeys = ref<string[]>([])
const collapsed = ref(false)
const {menuTree} = useMenuTree();
const menuIcon = (name: string) => {
  if ("workstation" === name) {
    return 'i-mdi:monitor-dashboard'
  }
  if ("projectManagement" === name) {
    return 'i-mdi:application-cog'
  }
  if ("apiTest" === name) {
    return 'i-mdi:swap-horizontal-circle'
  }
  return "i-mdi:tools";
}
const menuOptions = computed(() => {
  const travel = (_route: (RouteRecordRaw | null)[] | null, nodes: Array<MenuOption> = []) => {
    if (_route) {
      _route.forEach(route => {
        const node: MenuOption = {
          label: () => h(RouterLink, {
            to: {
              name: route?.meta?.hideChildrenInMenu ? getFirstRouterNameByCurrentRoute(route.name as string) : route?.name
            }
          }, {default: () => route?.meta?.locale}),
          key: route?.name as string,
          icon: route?.meta?.icon ? () => h('div', {class: menuIcon(route.name as string)}, {}) : undefined,
        }
        if (route?.children && route?.children?.length) {
          node.children = travel(route?.children as RouteRecordRaw[])
        }
        nodes.push(node);
      })
    }
    return nodes;
  }
  return travel(menuTree.value);
})
const findMenuOpenKeys = (target: string) => {
  const result: string[] = [];
  let isFind = false;
  const backtrack = (item: RouteRecordRaw | null, keys: string[]) => {
    if (target.includes(item?.name as string)) {
      result.push(...keys);
      if (result.length >= 2) {
        // 由于目前存在三级子路由，所以至少会匹配到三层才算结束
        isFind = true;
        return;
      }
    }
    if (item?.children?.length) {
      item.children.forEach((el) => {
        backtrack(el, [...keys, el.name as string]);
      });
    }
  };

  menuTree.value?.forEach((el: RouteRecordRaw | null) => {
    if (isFind) return; // 节省性能
    backtrack(el, [el?.name as string]);
  });
  return result;
}
const selectedKey = ref<string>('');
/**
 * 监听路由变化，存储打开及选中的菜单
 */
listenerRouteChange((newRoute) => {
  const {requiresAuth, activeMenu, hideInMenu} = newRoute.meta;
  if (requiresAuth !== false && (!hideInMenu || activeMenu)) {
    const menuOpenKeys = findMenuOpenKeys((activeMenu || newRoute.name) as string);
    const keySet = new Set([...menuOpenKeys, ...expandedKeys.value]);
    expandedKeys.value = [...keySet];
    selectedKey.value = menuOpenKeys[menuOpenKeys.length - 1];
  }
}, true);
</script>

<template>
  <n-layout-sider
      bordered
      show-trigger
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :native-scrollbar="false"
      @collapse="collapsed = true"
      @expand="collapsed = false"
  >

    <n-menu
        v-model:value="selectedKey"
        :default-expanded-keys="expandedKeys"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :root-indent="18"
        @update:value="(k: string) => selectedKey=k"
    />
  </n-layout-sider>
</template>

<style scoped>

</style>