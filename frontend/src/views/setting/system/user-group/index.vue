<script setup lang="ts">
import {AuthScopeEnum} from "/@/enums/common-enum.ts";
import {computed, provide, ref} from "vue";
import UserGroupLeft from "/@/views/setting/organization/user-group/components/UserGroupLeft.vue";
import type {ICurrentUserGroupItem} from "/@/api/types/user-group.ts";
import UserTable from "/@/views/setting/organization/user-group/components/UserTable.vue";
import AuthTable from "/@/views/setting/organization/user-group/components/AuthTable.vue";

provide('systemType', AuthScopeEnum.SYSTEM);
const ugLeftRef = ref<InstanceType<typeof UserGroupLeft>>();
const userRef = ref<InstanceType<typeof UserTable>>();
const currentTable = ref('auth');
const currentUserGroupItem = ref<ICurrentUserGroupItem>({
  id: '',
  name: '',
  type: AuthScopeEnum.SYSTEM,
  internal: true,
});
const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.SYSTEM);
const handleSelect = (item: ICurrentUserGroupItem) => {
  currentUserGroupItem.value = item;
}
</script>

<template>
  <n-card>
    <n-split default-size="200px">
      <template #1>
        <user-group-left ref="ugLeftRef"
                         :add-permission="['SYSTEM_USER_ROLE:READ+ADD']"
                         :update-permission="['SYSTEM_USER_ROLE:READ+UPDATE']"
                         :is-global-disable="false"
                         @handle-select="handleSelect"/>
      </template>
      <template #2>
        <div class="flex h-full flex-col overflow-hidden pt-[16px]">
          <div class="flex flex-row items-center justify-between px-[16px]">
            <n-radio-group v-if="couldShowUser" v-model:value="currentTable" class="mb-[16px]">
              <n-radio value="auth" class="show-type-icon p-[2px]">auth</n-radio>
              <n-radio value="user" class="show-type-icon p-[2px]">user</n-radio>
            </n-radio-group>
            <div class="flex items-center">
              <n-input v-if="currentTable === 'user'" placeholder="通过姓名/邮箱/手机搜索" class="w-[240px]"/>
            </div>
          </div>
          <div class="flex-1 overflow-hidden">
            <user-table v-if="currentTable === 'user'" ref="userRef"/>
            <auth-table v-if="currentTable === 'auth'" :current="currentUserGroupItem"/>
          </div>
        </div>
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>

</style>