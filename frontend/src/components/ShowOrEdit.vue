<script setup lang="ts">
import {ref} from 'vue'
import {type InputInst} from "naive-ui";

const props = defineProps<{
  value: string
}>()
const isEdit = ref(false)
const inputRef = ref<InputInst | null>(null)
const inputValue = ref(props.value)
</script>

<template>
  <div v-if="isEdit">
    <n-input ref="inputRef" v-model:value="inputValue"/>
  </div>
  <div v-else>
    {{ props.value }}
  </div>
</template>

<style scoped>

</style>