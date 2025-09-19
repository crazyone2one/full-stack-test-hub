<script setup lang="ts">
import {usePagination} from "alova/client";
import {type DataTableColumns, NButton, NSelect, type SelectOption} from "naive-ui";
import {computed, h, ref, watch} from "vue";
import {orgProjectApis} from "/@/api/modules/org-project";
import type {IUserItem} from "/@/api/types/user";
import BasePagination from "/@/components/BasePagination.vue";
import {hasAnyPermission} from "/@/utils/permissions";
import RemoveButton from "/@/components/RemoveButton.vue";
import TagGroup from "/@/components/TagGroup.vue";
import {memberApis} from "/@/api/modules/member.ts";
import {projectMemberApis} from "/@/api/modules/project-member.ts";
import AddUserModal from "/@/views/setting/system/org-project/components/AddUserModal.vue";

export interface projectDrawerProps {
  visible: boolean;
  organizationId?: string;
  projectId?: string;
  currentName: string;
}

const props = defineProps<projectDrawerProps>();
const emit = defineEmits<{
  (e: "cancel", refresh: boolean): void;
  (e: "requestFetchData"): void;
}>();
const keyword = ref("");
const userVisible = ref(false);
const userGroupOptions = ref<SelectOption[]>([]);
const active = defineModel<boolean>("active", {
  type: Boolean,
  default: false,
});
const handleLeave = (refresh: false) => {
  emit("cancel", refresh);
};
const hasOperationPermission = computed(() =>
    hasAnyPermission([
      "SYSTEM_ORGANIZATION_PROJECT:READ+RECOVER",
      "SYSTEM_ORGANIZATION_PROJECT:READ+UPDATE",
      "SYSTEM_ORGANIZATION_PROJECT:READ+DELETE",
    ])
);
const columns: DataTableColumns<IUserItem> = [
  {
    type: "selection",
  },
  {title: "姓名", key: "name", width: 150},
  {
    title: "用户组",
    key: "userRoleList",
    width: 200,
    render(record) {
      if (!record.selectUserGroupVisible) {
        return h(TagGroup, {
          tagList: record.userRoleList, showTable: true, allowEdit: true,
          onClick: () => handleTagClick(record)
        }, {});
      }
      return h(NSelect, {
        value: record.userRoleList.map(item => item.id),
        options: userGroupOptions.value, multiple: true,
        placeholder: '请选择用户组', class: 'w-full max-w-[300px]',
        onUpdateValue: (value) => handleUserGroupChange(value, record),
      }, {})
    },
  },
  {title: "邮箱", key: "email", width: 180,},
  {title: "手机", key: "phone"},
  {
    title: hasOperationPermission.value ? "操作" : "",
    key: "actions",
    fixed: "right",
    width: 60,
    render(record) {
      if (!record.enable) {
        return h(
            RemoveButton,
            {
              title: `确认移除 ${record.name} 这个用户吗？`,
              subTitleTip: props.organizationId ? '移除后，将失去组织权限' : '移除后，将失去项目权限',
              onOk: () => handleRemove(record)
            },
            {}
        );
      }
    },
  },
];
const {
  page,
  pageSize,
  data,
  send: fetchData,
  total,
} = usePagination(
    (page, pageSize) => {
      const param = {
        page,
        pageSize,
        keyword: keyword.value,
        projectId: "",
        organizationId: "",
      };
      if (props.organizationId) {
        param.organizationId = props.organizationId;
      }
      if (props.projectId) {
        param.projectId = props.projectId;
      }
      return orgProjectApis.fetchUserPageByOrgIdOrProjectId(param);
    },
    {
      initialData: {total: 0, data: []},
      immediate: false,
      data: (resp) => resp.records,
      total: (resp) => resp.totalRow,
      watchingStates: [keyword],
    }
);
const getUserGroupOptions = async () => {
  try {
    if (props.organizationId) {
      userGroupOptions.value = await memberApis.getGlobalUserGroup(props.organizationId);
    } else if (props.projectId) {
      userGroupOptions.value = await projectMemberApis.getProjectUserGroup(props.projectId);
    }
  } catch (error) {
    console.log(error);
  }
};
const handleTagClick = (record: IUserItem & Record<string, any>) => {
  if (hasAnyPermission(['SYSTEM_ORGANIZATION_PROJECT:READ+UPDATE_MEMBER'])) {
    record.selectUserGroupVisible = true;
  }
}
const handleUserGroupChange = (val: boolean, record: IUserItem & Record<string, any>) => {
  if (!val) {
    console.log(record)
  }
}
const handleAddMember = () => {
  userVisible.value = true;
};
const handleAddMemberSubmit = () => {
  fetchData();
  emit('requestFetchData');
}
const handleRemove = async (record: IUserItem) => {
  if (props.organizationId) {
    await orgProjectApis.deleteUserFromOrgOrProject(props.organizationId, record.id);
  }
  if (props.projectId) {
    await orgProjectApis.deleteUserFromOrgOrProject(props.projectId, record.id, false);
  }
  window.$message.success('移除成功');
  await fetchData();
  emit('requestFetchData');
}
watch(
    [() => props.projectId, () => props.organizationId, () => props.visible],
    () => {
      if (props.visible) {
        fetchData();
        getUserGroupOptions();
      }
    }
);
</script>

<template>
  <n-drawer
      v-model:show="active"
      :width="800"
      :auto-focus="false"
      @after-leave="handleLeave(false)"
  >
    <n-drawer-content :native-scrollbar="false" closable>
      <template #header>
        <div class="flex flex-1 items-center justify-between overflow-hidden">
          <div class="flex flex-1 items-center overflow-hidden">
            <div class="one-line-text one-line-text max-w-[300px]">
              成员列表
            </div>
            <div class="">
              {{ props.currentName }}
            </div>
          </div>
        </div>
      </template>
      <div>
        <div class="flex flex-row justify-between">
          <n-button type="info" secondary class="mr-3" @click="handleAddMember">添加成员</n-button>
          <div>
            <n-input
                v-model:value="keyword"
                placeholder="通过名称/邮箱/手机搜索"
                class="w-[230px]"
                clearable
            />
          </div>
        </div>
        <div class="mt-4">
          <n-data-table
              :columns="columns"
              :data="data"
              :row-key="(row: IUserItem) => row.id"
          />
          <base-pagination
              v-model:page="page"
              v-model:page-size="pageSize"
              :count="total || 0"
          />
        </div>
      </div>
    </n-drawer-content>
  </n-drawer>
  <add-user-modal v-model:show-modal="userVisible" :user-group-options="userGroupOptions"
                  :project-id="props.projectId" :organization-id="props.organizationId"
                  @submit="handleAddMemberSubmit"/>
</template>

<style scoped></style>
