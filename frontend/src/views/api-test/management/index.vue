<script setup lang="ts">
import {ref} from "vue";
import apiCase from './case/index.vue';

const caseRef = ref<InstanceType<typeof apiCase>>();
// const currentTab = ref('case');
// const tabOptions = [
//   {label: 'API', value: 'api'},
//   ...(hasAnyPermission(['PROJECT_API_DEFINITION_CASE:READ']) ? [{label: 'CASE', value: 'case'}] : []),
//   ...(hasAnyPermission(['PROJECT_API_DEFINITION_MOCK:READ']) ? [{label: 'MOCK', value: 'mock'}] : []),
// ];
</script>

<template>
  <!--  <div class="flex gap-[8px] px-[16px] pt-[16px]">-->
  <!--    <n-select v-model:value="currentTab" :options="tabOptions" class="w-[80px]"/>-->
  <!--  </div>-->
  <api-case ref="caseRef"/>
</template>

<style scoped>

</style>