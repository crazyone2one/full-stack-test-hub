<script setup lang="ts">
import {ref} from "vue";
import {type MenuOption, NMenu} from "naive-ui";

const menuOptions: MenuOption[] = [
  {
    label: '1973年的弹珠玩具',
    key: 'pinball-1973',
  },
  {
    label: '寻羊冒险记',
    key: 'a-wild-sheep-chase',
  }
]
const activeKey = ref<string | null>(null)
</script>

<template>
  <n-menu
      v-model:value="activeKey"
      mode="horizontal"
      :options="menuOptions"
      responsive
  />
</template>

<style scoped>

</style>