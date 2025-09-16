<script setup lang="ts">

import MenuPanel from "/@/views/project-management/components/MenuPanel.vue";
import {computed, onBeforeMount, provide, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {ProjectManagementRouteEnum} from "/@/enums/route-enum.ts";
import usePermission from "/@/hooks/use-permission.ts";

const router = useRouter();
const route = useRoute();
const permission = usePermission();
const currentKey = ref<string>('');
const memberPermissionShowCondition = () => {
  const routerList = router.getRoutes();
  return routerList.some((rou) => {
    return [
      ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_MEMBER,
      ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_USER_GROUP,
    ].includes(rou.name as any) && permission.accessRouter(rou);
  });
}
const sourceMenuList = ref([
  {
    key: 'project',
    title: '项目',
    level: 1,
    name: '',
  },
  {
    key: 'projectBasicInfo',
    title: '基本信息',
    level: 2,
    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_BASIC_INFO,
  },
  {
    key: 'projectMenuManage',
    title: '应用设置',
    level: 2,
    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_MENU_MANAGEMENT,
  },
  {
    key: 'memberPermission',
    title: '成员权限',
    level: 1,
    name: '',
    showCondition: memberPermissionShowCondition,
  },
  {
    key: 'projectMember',
    title: '成员',
    level: 2,
    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_MEMBER,
  },
  {
    key: 'projectUserGroup',
    title: '用户组',
    level: 2,
    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_USER_GROUP,
  },
])
const menuList = computed(() => {
  const routerList = router.getRoutes();
  return sourceMenuList.value.filter((item) => {
    if (item.name) {
      const routerItem = routerList.find((rou) => rou.name === item.name);
      if (!routerItem) return false;
      if (routerItem.name === ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_VERSION) {
        return permission.accessRouter(routerItem);
      }
      return permission.accessRouter(routerItem);
    }
    return true;
  });
});
const setInitRoute = () => {
  if (route?.name) currentKey.value = route.name as string;
};
const isLoading = ref(false);
const reload = (flag: boolean) => {
  isLoading.value = flag;
};
const toggleMenu = (itemName: string) => {
  if (itemName) {
    currentKey.value = itemName;
    router.push({ name: itemName });
  }
};
provide('reload', reload);
onBeforeMount(() => {
  setInitRoute();
});
</script>

<template>
  <div class="wrapper flex min-h-[500px]" >
    <menu-panel title="项目与权限"
                :menu-list="menuList"
                :default-key="currentKey"
                class="mr-[16px] w-[208px] min-w-[208px] bg-white p-[16px]"
                @toggle-menu="toggleMenu"/>
    <n-card>
      <router-view/>
    </n-card>
  </div>

</template>

<style scoped>

</style>