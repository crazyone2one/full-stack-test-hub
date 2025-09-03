<script setup lang="ts">
import {NLayout, NLayoutContent, NSpin} from "naive-ui";
import BaseSidebar from "/@/layout/components/BaseSidebar.vue";
import BaseHeader from "/@/layout/components/BaseHeader.vue";
import useAppStore from "/@/store/modules/app";

const appStore = useAppStore();

</script>

<template>
  <n-layout :style="{ height: appStore.appState.innerHeight + 'px' }">
    <base-header/>
    <n-layout has-sider>
      <base-sidebar/>
      <n-layout-content content-style="padding: 24px;">
<!--        <router-view/>-->
        <n-spin :show="appStore.appState.loading" size="large">
          <router-view/>
          <template #description>
            {{ appStore.appState.loadingTip }}
          </template>
        </n-spin>
      </n-layout-content>
      <!--        <n-layout-footer>成府路</n-layout-footer>-->
    </n-layout>
  </n-layout>
</template>

<style scoped>
.n-layout-footer {
  opacity: 0.6;
  padding: 30px;
  text-align: center;
}
</style>