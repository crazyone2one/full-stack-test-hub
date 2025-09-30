<script setup lang="ts">
const active = defineModel<boolean>('active', {type: Boolean, default: false});
</script>

<template>
  <n-drawer v-model:show="active" :width="502">
    <n-drawer-content>
      <template #header>
        Header
      </template>
      <template #footer>
        <n-button>Footer</n-button>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>