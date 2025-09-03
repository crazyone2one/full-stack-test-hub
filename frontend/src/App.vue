<script setup lang="ts">
import {
  darkTheme,
  dateZhCN,
  NConfigProvider,
  NDialogProvider,
  NGlobalStyle,
  NLoadingBarProvider,
  NMessageProvider,
  useOsTheme,
  zhCN
} from "naive-ui";
import {computed, onBeforeMount} from "vue";
import {useEventListener, useWindowSize} from '@vueuse/core';
import useAppStore from "/@/store/modules/app";

const osTheme = useOsTheme()
const appStore = useAppStore()
const theme = computed(() => (osTheme.value === 'dark' ? darkTheme : null))
onBeforeMount(() => {
  const {height} = useWindowSize();
  appStore.appState.innerHeight = height.value
})
useEventListener(window, 'resize', () => {
  const {height} = useWindowSize();
  appStore.appState.innerHeight = height.value
})
</script>

<template>
  <n-config-provider :theme="theme" :locale="zhCN" :date-locale="dateZhCN">
    <n-global-style/>
    <n-loading-bar-provider>
      <n-message-provider>
        <n-dialog-provider>
          <router-view/>
          <slot/>
        </n-dialog-provider>
      </n-message-provider>
    </n-loading-bar-provider>
  </n-config-provider>

</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
