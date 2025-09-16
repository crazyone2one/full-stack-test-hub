<script setup lang="ts">

import UserGroupLeft from "/@/views/setting/organization/user-group/components/UserGroupLeft.vue";
import {provide, ref, useTemplateRef, computed} from "vue";
import {AuthScopeEnum} from "/@/enums/common-enum.ts";
import type {ICurrentUserGroupItem} from "/@/api/types/user-group.ts";
import AuthTable from "/@/views/setting/organization/user-group/components/AuthTable.vue";

provide('systemType', AuthScopeEnum.ORGANIZATION);
const userGroupLeftRef = useTemplateRef<InstanceType<typeof UserGroupLeft>>('userGroupLeft');
const leftWidth = ref('300px');
const currentUserGroupItem = ref<ICurrentUserGroupItem>({
  id: '',
  name: '',
  type: AuthScopeEnum.ORGANIZATION,
  internal: true,
});
const handleSelect = (item: ICurrentUserGroupItem) => {
  currentUserGroupItem.value = item;
}
const currentTable = ref('user');
const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.ORGANIZATION);
const couldShowAuth = computed(() => currentUserGroupItem.value.id !== 'admin');
// onMounted(async () => {
//   await nextTick();
//   userGroupLeftRef.value?.initData(router.currentRoute.value.query.id as string, true);
// });
</script>

<template>
  <n-card>
    <n-split v-model:size="leftWidth">
      <template #1>
        <user-group-left ref="userGroupLeftRef"
                         :add-permission="['ORGANIZATION_USER_ROLE:READ+ADD']"
                         :update-permission="['ORGANIZATION_USER_ROLE:READ+UPDATE']"
                         :is-global-disable="true"
                         @handle-select="handleSelect"/>
      </template>
      <template #2>
        <div>
          <div class="flex flex-row items-center justify-between p-[16px]">
            <n-radio-group v-if="couldShowUser && couldShowAuth" v-model:value="currentTable">
              <n-radio v-if="couldShowAuth" value="auth">权限</n-radio>
              <n-radio v-if="couldShowUser" value="user">user</n-radio>
            </n-radio-group>
            <div class="flex items-center">
              <n-input v-if="currentTable === 'user'" class="w-[240px]" placeholder="通过姓名/邮箱/手机搜索"/>
            </div>
          </div>
          <div>
            <div v-if="currentTable === 'user' && couldShowUser">user</div>
            <div v-if="currentTable === 'auth' && couldShowAuth">
              <auth-table :current="currentUserGroupItem"/>
            </div>
          </div>
        </div>
      </template>
    </n-split>
  </n-card>
</template>

<style scoped>

</style>