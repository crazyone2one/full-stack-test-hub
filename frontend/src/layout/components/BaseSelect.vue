<script setup lang="ts">

import {NSelect, type SelectOption} from "naive-ui";
import {CaretDown, Search} from "@vicons/tabler";
import {ref} from "vue";

const show = ref(false)
defineProps<{ options: SelectOption[] }>()
</script>

<template>
  <n-select v-model:show="show" :options="options" >
    <template #arrow>
      <caret-down v-if="!show" class="color-purple"/>
      <search v-else/>
    </template>
  </n-select>
</template>

<style scoped>

</style>