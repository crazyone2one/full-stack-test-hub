<script setup lang="ts">
import {Users, AlertCircle as fallback, Dashboard, Api, DragDrop as drag} from "@vicons/tabler";
import {NIcon} from 'naive-ui'

const icons = {Users, fallback, Dashboard, Api, drag}
type IconTypes = keyof typeof icons

const props = defineProps({
  ...NIcon.props,
  type: {
    type: String,
    default: 'fallback'
  }
})

const icon = icons[props.type as IconTypes]
</script>

<template>
  <n-icon v-bind="props">
    <icon/>
  </n-icon>
</template>

<style scoped>

</style>