<script setup lang="ts">

import {NA, NLayoutHeader} from "naive-ui";
import {RouterLink} from "vue-router";
</script>

<template>
  <n-layout-header >
    <router-link to="/" #="{ navigate, href }" custom>
      <n-a class="logo" :href="href" @click="navigate">
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
      </n-a>
    </router-link>
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