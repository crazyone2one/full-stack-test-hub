<script setup lang="ts">
import {NCard} from "naive-ui";
import {computed} from "vue";
import {hasAnyPermission} from "/@/utils/permissions.ts";
const hasAddPermission = computed(() => hasAnyPermission(['ORGANIZATION_PROJECT:READ+ADD']));

</script>

<template>
  <n-card>
    {{ hasAddPermission ? 1 : 2}}
  </n-card>
</template>

<style scoped>

</style>