<script setup lang="ts">
import {NLayout, NLayoutContent, NLayoutFooter, NLayoutHeader} from "naive-ui";
import BaseSidebar from "/@/layout/components/BaseSidebar.vue";
import BaseHeader from "/@/layout/components/BaseHeader.vue";
</script>

<template>
  <n-layout position="absolute" has-sider>
    <base-sidebar/>
    <n-layout>
      <base-header/>
      <n-layout-content content-style="padding: 24px;">
        <router-view/>
      </n-layout-content>
      <n-layout-footer>成府路</n-layout-footer>
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