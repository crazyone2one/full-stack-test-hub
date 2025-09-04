<script setup lang="ts">
import {NAvatar, NBadge, NButton, NDropdown, NIcon, NTooltip} from "naive-ui";
import {BellRinging, CalendarTime} from "@vicons/tabler";
import useUser from "/@/hooks/use-user.ts";
import {computed, ref} from "vue";

const unReadCount = ref(1)
const handleSelect = (key: string) => {
  switch (key) {
    case 'profile':
      break;
    case 'logout':
      useUser().logout(undefined, true)
      break;
  }
}
const options = computed(() => {
  return [
    {
      label: '用户资料',
      key: 'profile',
    },
    {
      label: '退出登录',
      key: 'logout',
    }
  ]
})
</script>

<template>
  <n-tooltip trigger="hover">
    <template #trigger>
      <n-badge v-if="unReadCount > 0" :value="unReadCount" dot :max="99" class="mr-[8px]">
        <n-button text>
          <template #icon>
            <n-icon :size="26">
              <BellRinging/>
            </n-icon>
          </template>
        </n-button>
      </n-badge>
      <n-button v-else text class="mr-[8px]">
        <template #icon>
          <n-icon :size="26">
            <BellRinging/>
          </n-icon>
        </template>
      </n-button>
    </template>
    消息通知
  </n-tooltip>
  <n-tooltip trigger="hover">
    <template #trigger>
      <n-button text class="m-x-[8px]">
        <template #icon>
          <n-icon :size="26" color-purple>
            <CalendarTime/>
          </n-icon>
        </template>
      </n-button>
    </template>
    任务中心
  </n-tooltip>
  <n-dropdown trigger="click" :options="options" @select="handleSelect">
    <n-avatar round :size="26" class="cursor-pointer ml-[5px] color-purple">
      hub
    </n-avatar>
  </n-dropdown>
</template>

<style scoped>

</style>