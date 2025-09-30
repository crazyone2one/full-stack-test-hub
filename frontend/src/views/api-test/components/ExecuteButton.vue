<script setup lang="ts">
const options = [
  {
    label: '本地执行',
    key: 'localExec',

  },
  {
    label: '服务端执行',
    key: 'serverExec',
    disabled: true
  },
]
const handleSelect = () => {

}
</script>

<template>
  <n-dropdown trigger="click" :options="options" @select="handleSelect">
    <n-button>本地执行</n-button>
  </n-dropdown>
  <n-button v-permission="['PROJECT_API_DEFINITION:READ+UPDATE']" size="small">停止</n-button>
</template>

<style scoped>

</style>