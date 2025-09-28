<script setup lang="ts">
import {ref} from "vue";
import CreateAndEditCase from "/@/views/api-test/management/case/CreateAndEditCase.vue";

const props = defineProps<{
  // isApi: boolean; // 接口定义详情的case tab下
  // activeModule: string;
  // selectedProtocols: string[]; // 查看的协议类型
  apiDetail?: any;
  // offspringIds: string[];
  heightUsed?: number;
}>();
const showCaseVisible = ref(false);
const createAndEditCaseDrawerRef = ref<InstanceType<typeof CreateAndEditCase>>();
const createCase = () => {
  createAndEditCaseDrawerRef.value?.open(props.apiDetail?.id);
}
</script>

<template>
  <n-card>
    <template #header>
      <n-button v-permission="['PROJECT_API_DEFINITION_CASE:READ+ADD']" type="primary" @click="createCase">
        创建用例
      </n-button>
    </template>
    <template #header-extra>
      <n-input placeholder="输入 ID/名称/api路径搜索"/>
    </template>
    卡片内容
  </n-card>
  <create-and-edit-case ref="createAndEditCaseDrawerRef" v-model:active="showCaseVisible"/>
</template>

<style scoped>

</style>