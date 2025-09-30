<script setup lang="ts">
import {type TreeOption} from "naive-ui";

const data: TreeOption[] = [
  {
    label: '1',
    key: '1',
    children: [
      {
        label: '1-1',
        key: '1-1',
      },
      {
        label: '1-2',
        key: '1-2',
      },
      {
        label: '1-3',
        key: '1-3',
      },
    ]
  }
]
</script>

<template>
  <n-tree
      block-line
      :data="data"
      expand-on-click
  />
</template>

<style scoped>

</style>