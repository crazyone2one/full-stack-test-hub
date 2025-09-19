<script setup lang="ts">

import {ref, watch} from "vue";
import type {DataTableColumns, DataTableRowKey, SelectOption} from "naive-ui";
import {memberApis} from "/@/api/modules/member.ts";
import {projectMemberApis} from "/@/api/modules/project-member.ts";
import {usePagination} from "alova/client";
import {orgProjectApis} from "/@/api/modules/org-project.ts";
import type {IUserItem} from "/@/api/types/user.ts";
import BasePagination from "/@/components/BasePagination.vue";

const props = defineProps<{
  isOrganization?: boolean; // 组织下的
  userGroupOptions?: Array<SelectOption>;
  organizationId?: string;
  projectId?: string;
}>();
const emit = defineEmits<{
  (e: 'submit'): void;
}>();
const showModal = defineModel<boolean>("showModal", {
  type: Boolean,
  default: false,
});
const keyword = ref<string>('');
const userGroupIds = ref<string[]>([]);
const currentUserGroupOptions = ref<Array<SelectOption>>([]);
const handleCancel = () => {
  showModal.value = false;
  keyword.value = '';
  userGroupIds.value = [];
};
const getUserGroupOptions = async () => {
  if (props.organizationId && !props.isOrganization) {
    currentUserGroupOptions.value = await memberApis.getGlobalUserGroup(props.organizationId);
  } else if (props.projectId) {
    currentUserGroupOptions.value = await projectMemberApis.getProjectUserGroup(props.projectId);
  }
};
const {page, pageSize, data, send: fetchData, total,} = usePagination(
    (page, pageSize) => {
      const param = {
        page,
        pageSize,
        keyword: keyword.value,
        sourceId: props.projectId ?? props.organizationId,
        projectId: props.projectId,
        organizationId: props.organizationId,
      };
      return !props.isOrganization ? orgProjectApis.fetchSystemMemberPage(param) : memberApis.fetchOrganizationMemberPage(param);
    },
    {
      initialData: {total: 0, data: []},
      immediate: false,
      data: (resp) => resp.records,
      total: (resp) => resp.totalRow,
      watchingStates: [keyword],
    }
);
const columns: DataTableColumns<IUserItem> = [
  {type: "selection",},
  {title: "姓名", key: "name", width: 200},
  {title: "邮箱", key: "email", width: 250,},
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const loading = ref(false);
const handleAddMember = async () => {
  try {
    loading.value = true;
    if (!props.isOrganization) {
      // 系统-组织与项目
      await orgProjectApis.addUserToOrgOrProject({
        userRoleIds: userGroupIds.value,
        userIds: checkedRowKeys.value as string[],
        projectId: props.projectId,
        organizationId: props.organizationId,
      });
    } else {
      // 组织-项目
      await orgProjectApis.addProjectMemberByOrg({
        userRoleIds: userGroupIds.value,
        userIds: checkedRowKeys.value as string[],
        projectId: props.projectId,
      });
    }
    window.$message.success('添加成功');
    emit('submit');
    handleCancel();
  } catch (error) {
    // eslint-disable-next-line no-console
    console.error(error);
  } finally {
    loading.value = false;
  }
};
watch(() => showModal.value, (value) => {
  if (value) {
    fetchData();
    if (!props.userGroupOptions) {
      getUserGroupOptions();
    } else {
      currentUserGroupOptions.value = props.userGroupOptions;
    }
    if (props.projectId) {
      userGroupIds.value = ['project_member'];
    } else if (props.organizationId) {
      userGroupIds.value = ['org_member'];
    }
  }
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog"
           style="width: 700px"
           :mask-closable="false"
           @close="handleCancel">
    <template #header>
      <div>添加成员</div>
    </template>
    <div class="mb-[16px] ">
      <div>
        <n-input v-model:value="keyword" placeholder="通过名称/邮箱搜索" class="w-[220px]" clearable/>
      </div>
      <n-data-table
          :columns="columns"
          :data="data"
          :row-key="(row: IUserItem) => row.id"
          size="small"
          @update:checked-row-keys="handleCheck"
      />
      <base-pagination
          v-model:page="page"
          v-model:page-size="pageSize"
          :count="total || 0"
          size="small"
      />
    </div>
    <template #action>
      <div class="flex justify-between">
        <div class="flex items-center gap-[8px]">
          <div class="text-nowrap">用户组</div>
          <n-select v-model:value="userGroupIds" :options="currentUserGroupOptions" multiple
                    size="small"
                    placeholder="请为以上成员选择用户组"
                    class="!w-[220px] text-start"/>
        </div>
        <div class="flex gap-[12px]">
          <n-button secondary :loading="loading" @click="handleCancel">取消</n-button>
          <n-button type="primary" :loading="loading" :disabled="!userGroupIds.length || !checkedRowKeys.length"
                    @click="handleAddMember">添加</n-button>
        </div>
      </div>
    </template>
  </n-modal>
</template>

<style scoped>

</style>