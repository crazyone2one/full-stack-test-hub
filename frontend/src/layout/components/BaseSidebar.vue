<script setup lang="ts">

import {type MenuOption, NLayoutSider, NMenu} from "naive-ui";
import {computed, h, ref, watchEffect} from "vue";
import BaseIcon from "/@/components/BaseIcon.vue";
import {RouterLink, useRoute} from "vue-router";
import {SettingRouteEnum} from "/@/enums/route-enum.ts";

const route = useRoute()
const currentKey = ref<string>('')
const expandedKeys = ref<string[]>([])
const collapsed = ref(false)

const menuOptions = computed(() => {
  return [
    {
      label: () => h(RouterLink, {to: {name: "workstation"}}, {default: () => 'Dashboard'}),
      key: 'workstation',
      icon: () => h(BaseIcon, {type: 'Dashboard'}, {})
    },
    // {
    //   label: () => h(RouterLink, {to: {name: "cases"}}, {default: () => 'Cases'}),
    //   key: 'cases',
    //   icon: () => h(BaseIcon, {type: 'Api'}, {})
    // },
    {
      label: () => h(RouterLink, {to: {name: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT}}, {default: () => '项目'}),
      key: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
      icon: () => h('div', {class: 'i-local-icon_test-tracking_filled'}, {})
    },
    {
      label: () => h(RouterLink, {to: {name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE}}, {default: () => 'Users'}),
      key: 'users',
      icon: () => h(BaseIcon, {type: 'Users'}, {})
    }
  ]
})
const routeMatched = (menu: MenuOption): boolean => {
  return route.name === menu.key && (menu.params == null || JSON.stringify(route.params) === JSON.stringify(menu.params))
}
const matchExpanded = (items: MenuOption[]): boolean => {
  let matched = false
  for (const item of items) {
    if (item.children != null) {
      matchExpanded(item.children) && expandedKeys.value.push(item.key as string)
    }
    if (routeMatched(item)) {
      currentKey.value = item.key as string
      matched = true
    }
  }
  return matched
}
watchEffect(() => matchExpanded(menuOptions.value))
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
        v-model:value="currentKey"
        :default-expanded-keys="expandedKeys"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :root-indent="18"
        @update:value="(k: string) => currentKey=k"
    />
  </n-layout-sider>
</template>

<style scoped>

</style>