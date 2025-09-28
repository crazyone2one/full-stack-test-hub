<script setup lang="ts">
import {ref} from "vue";

const active = defineModel<boolean>('active', {type: Boolean, default: false});
const isEdit = ref(false);
const open = (apiId: string) => {
  active.value = true;
  console.log(apiId)
}
defineExpose({
  open
})
</script>

<template>
  <n-drawer v-model:show="active" :width="894">
    <n-drawer-content>
      <template #header>
        {{ isEdit ? '更新用例' : '创建用例' }}
      </template>
      <template #footer>
        <n-button>{{ isEdit ? '更新' : '创建' }}</n-button>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped>

</style>