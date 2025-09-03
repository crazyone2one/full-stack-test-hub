<script setup lang="ts">
import {NLayoutHeader, NFlex} from "naive-ui";
import BaseSelect from "/@/layout/components/BaseSelect.vue";
import TopMenu from '/@/components/top-menu/index.vue'
import NavEnd from "/@/layout/components/NavEnd.vue";
</script>

<template>
  <n-layout-header bordered class="items-center">
    <n-flex>
      <!--      左侧-->
      <div class="flex w-[200px] items-center px-[16px] logo">
        <svg class="small" width="100" height="100" viewBox="0 0 100 100"
             xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="myGradient" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" stop-color="#41D1FF"></stop>
              <stop offset="100%" stop-color="#BD34FE"></stop>
            </linearGradient>
          </defs>
          <path d="M20,20 v60 M80,20 v60 M20,50 h60"
                fill="none"
                stroke="url(#myGradient)"
                stroke-width="12"
                stroke-linecap="round"/>
        </svg>
      </div>
      <div class="flex flex-1 items-center">
        <!--        项目选择器-->
        <div class="w-[216px] mr-[12px] pl-[36px]">
          <base-select :options="[]"/>
        </div>
        <!--        顶部菜单-->
        <div class="grow-0 shrink-1 overflow-hidden ml-[24px]">
          <top-menu/>
        </div>
      </div>
      <!--      右侧-->
      <div class="items-center flex mr-[20px]">
        <nav-end/>
      </div>
    </n-flex>
  </n-layout-header>
</template>

<style scoped>
.logo {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  padding: 12px 20px;
  text-decoration: none;
}

.n-layout-sider--collapsed .logo {
  padding: 9px;
}

.logo svg {
  height: 32px;
}
</style>