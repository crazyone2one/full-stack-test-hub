<script setup lang="ts">

import TestPlanTree from "/@/views/plan/components/TestPlanTree.vue";
import {ref} from "vue";

const planTreeRef = ref();
</script>

<template>
  <n-card>
    <n-split>
      <template #1>
        <div class="min-w-[300px] p-[16px]">
          <div class="mb-[6px] flex justify-between">
            <div>
              <n-input placeholder="请输入模块名称"/>
            </div>
            <div>
              <!--              <n-dropdown v-permission="['PROJECT_TEST_PLAN:READ+ADD']" class="ml-2" :options="[]">-->
              <n-dropdown class="ml-2" :options="[]">
                <n-button>新建</n-button>
              </n-dropdown>
            </div>
          </div>
          <div class="test-plan h-[100%]">
            <div class="case h-[38px]">
              <div class="flex items-center">
                <div class="i-mdi:folder"/>
                <div>全部测试计划</div>
              </div>
              <div class="ml-auto flex items-center">

              </div>
            </div>
            <test-plan-tree ref="planTreeRef"/>
          </div>
        </div>

      </template>
      <template #2>
        table data
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>

</style>