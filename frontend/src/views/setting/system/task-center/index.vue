<script setup lang="ts">
import TaskCenter from '/@/views/task-center/index.vue'
</script>

<template>
  <n-card>
    <task-center type="system"/>
  </n-card>
</template>

<style scoped>
:deep(.task-center-content) {
  padding: 0 16px;
}
</style>