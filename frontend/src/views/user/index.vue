<script setup lang="ts">
import {usePagination, useRequest} from "alova/client";
import {userApis} from "/@/api/modules/user.ts";
import {h, onMounted, ref, useTemplateRef} from "vue";
import type {UserState} from "/@/store/modules/user/types.ts";
import {
  type DataTableColumns,
  type DataTableRowKey,
  type DropdownOption,
  NButton,
  NCard,
  NDataTable,
  NDropdown,
  NFlex,
  NInput,
  NSelect,
  NSwitch,
  type PaginationProps
} from "naive-ui";
import EditUser from "/@/views/user/components/EditUser.vue";
import {cloneDeep} from "es-toolkit";
import type {ISystemRole, IUserItem, UserCreateInfo} from "/@/api/types/user.ts";
import CreateOrUpdateModal from "/@/views/user/components/CreateOrUpdateModal.vue";
import type {IBatchActionQueryParams} from "/@/api/types/commons.ts";
import BatchModal from "/@/views/user/components/BatchModal.vue";
import TagGroup from "/@/components/TagGroup.vue";

const batchAction = ref('');
const showBatchModal = ref(false);
const keyword = ref('');
const {page, pageSize, data, send: loadList} = usePagination((page, pageSize) => userApis.fetchUserPage({
      page,
      pageSize,
      keyword: keyword.value
    }),
    {
      initialData: {total: 0, data: []},
      immediate: false,
      data: resp => resp.records,
      total: resp => resp.totalRow,
      watchingStates: [keyword]
    }
)
const userGroupOptions = ref<ISystemRole[]>([]);
const checkedRowKeys = ref<string[]>([])
const tableMoreAction: Array<DropdownOption> = [
  {
    label: '重置密码',
    key: 'resetPassword'
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: '删除',
    key: 'delete',
    props: {style: 'color: red;'}
  },
]
const columns: DataTableColumns<IUserItem> = [
  {
    type: 'selection',
    options: [
      {
        label: '添加至项目', key: 'batchAddProject',
        onSelect: (_pageData) => {
          // console.log(pageData)
          // checkedRowKeys.value = pageData.map(row => row.id)
          batchAction.value = 'batchAddProject'
          showBatchModal.value = true
        }
      },
      {
        label: '添加至用户组',
        key: 'batchAddUserGroup', onSelect: (_pageData) => {
          batchAction.value = 'batchAddUserGroup'
          showBatchModal.value = true
        }
      },
      {
        label: '添加至组织',
        key: 'batchAddOrganization', onSelect: (_pageData) => {
          batchAction.value = 'batchAddOrganization'
          showBatchModal.value = true
        }
      }
    ]
  },
  {title: '用户名', key: 'name',},
  {title: '邮箱', key: 'email', width: 200},
  {title: '手机', key: 'phone', width: 140},
  {
    title: '组织', key: 'organizationList', width: 300,
    render(record) {
      return h(TagGroup, {tagList: record.organizationList, showTable: true})
    }
  },
  {
    title: '用户组', key: 'userRoleList', width: 300,
    render(record) {
      if (!record.selectUserGroupVisible) {
        return h(TagGroup, {
          tagList: record.userRoleList,
          showTable: true,
        });
      } else {
        return h(NSelect, {
          options: userGroupOptions.value,
          labelField: 'name',
          valueField: 'id',
          disabled: record.selectUserGroupLoading,
          class: "w-full max-w-[300px]",
          multiple: true,
          placeholder: '请选择用户组',
          value: record.userRoleIds,
          size: 'small',
          onUpdateShow: (v) => handleUserGroupChange(v, record)
        })
      }
    }
  },
  {
    title: '状态', key: 'enable',
    render(record) {
      return h(NSwitch, {value: record.enable, size: 'small', onUpdateValue: (v) => handleChangeEnable(v, record)})
    }
  },
  {
    title: '操作',
    key: 'actions',
    fixed: 'right',
    width: 110,
    render(record) {
      if (record.enable) {
        return h(NFlex, {}, {
          default() {
            return [
              h(NButton, {
                size: 'small',
                type: 'primary',
                text: true,
                onClick: () => showUserModal('edit', record)
              }, {
                default: () => '编辑'
              }),
              h(NDropdown, {
                options: tableMoreAction,
                size: 'small',
                trigger: 'click',
                onSelect: (v) => handleSelect(v, record)
              }, {
                default: () => h("div", {class: 'i-mdi:dots-horizontal-circle-outline text-[16px]'})
              })
            ]
          }
        });
      } else {
        return h(NButton, {
          size: 'small',
          type: 'error',
          text: true,
          onClick: () => deleteUser(record)
        }, {
          default: () => '删除'
        })
      }
    }
  }
]

const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys as string[]
}
const pagination: PaginationProps = {
  page: page.value, pageSize: pageSize.value, size: 'small'
}
type UserModalMode = 'create' | 'edit';
const showAddModel = ref(false)
const visible = ref(false)
const userFormMode = ref<'create' | 'edit'>('create')
const createOrUpdateModalRef = useTemplateRef<InstanceType<typeof CreateOrUpdateModal>>('createOrUpdateModal')
const batchModalRef = useTemplateRef<InstanceType<typeof BatchModal>>('batchModal')
const editUserRef = useTemplateRef<InstanceType<typeof EditUser>>('editUser')
const userForm1 = ref<UserState>()

interface IUserForm {
  list: UserCreateInfo[];
  userGroup: string[];
}

const defaultUserForm = {
  list: [
    {
      name: '',
      email: '',
      phone: '',
    },
  ],
  userGroup: [],
};
const userForm = ref<IUserForm>(cloneDeep(defaultUserForm));
const showUserModal = (mode: UserModalMode, record?: IUserItem) => {
  visible.value = true;
  userFormMode.value = mode;
  if (mode === 'edit' && record) {
    userForm.value.list = [
      {
        id: record.id,
        name: record.name,
        email: record.email,
        phone: record.phone ? record.phone.replace(/\s/g, '') : record.phone,
      },
    ];
    userForm.value.userGroup = record.userRoleList.map(item => item.id);
  }
}
const deleteUser = (record?: IUserItem, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let selectIds = [record?.id || ''];
  let title = `确认删除 ${record?.name} 这个用户吗？`;
  if (isBatch) {
    selectIds = checkedRowKeys.value;
    title = `确认删除已选中的 ${params?.currentSelectCount || checkedRowKeys.value.length} 个用户吗？`;
  }
  window.$dialog.error({
    title: title,
    content: '仅删除用户信息，不处理该用户的系统数据',
    positiveText: '确认删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      await userApis.deleteUserInfo({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      })
      window.$message.success('删除成功');
      await loadList()
    },
  });
};
const handleChangeEnable = (value: boolean, record: IUserItem) => {
  if (value) {
    enableUser(record);
  } else {
    disabledUser(record);
  }
}
const {send: handleUserStatus} = useRequest((param) => userApis.toggleUserStatus(param), {immediate: false})
const enableUser = (record: IUserItem, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let title = `确认启用 ${record.name} 这个用户吗？`;
  let selectIds = [record.id || ''];
  if (isBatch) {
    title = `确认启用已选中的 ${params?.currentSelectCount || checkedRowKeys.value.length} 个用户吗？`;
    selectIds = checkedRowKeys.value;
  }
  window.$dialog.info({
    title: title,
    content: '启用后用户可以登录系统',
    positiveText: '确认启用',
    negativeText: '取消',
    onPositiveClick: () => {
      handleUserStatus({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
        enable: true,
      }).then(() => {
        window.$message.success('启用成功');
        loadList();
      })
    },
  });
}
const disabledUser = (record: IUserItem, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let title = `确认禁用 ${record.name} 这个用户吗？`;
  let selectIds = [record.id || ''];
  if (isBatch) {
    title = `确认禁用已选中的 ${params?.currentSelectCount || checkedRowKeys.value.length} 个用户吗？`;
    selectIds = checkedRowKeys.value;
  }
  window.$dialog.error({
    title: title,
    content: '禁用的用户无法登录系统',
    positiveText: '确认禁用',
    negativeText: '取消',
    onPositiveClick: () => {
      handleUserStatus({
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
        enable: false,
      }).then(() => {
        window.$message.success('禁用成功');
        loadList();
      })
    },
  })
};
const initUGOptions = async () => {
  userGroupOptions.value = await userApis.getSystemRoles();
  if (userGroupOptions.value.length) {
    userForm.value.userGroup = userGroupOptions.value.filter(e => e.selected).map(item => item.id) as string[];
  }
}
const handleUserGroupChange = (value: boolean, record: IUserItem) => {
  console.log(value)
  console.log(record)
}
const resetPassword = (record?: IUserItem, isBatch?: boolean, params?: IBatchActionQueryParams) => {
  let title = `是否将 ${record?.name} 的密码重置为初始密码？`;
  let selectIds = [record?.id || ''];
  if (isBatch) {
    title = `是否将选中的 ${params?.currentSelectCount || checkedRowKeys.value.length} 个用户的密码重置为初始密码？`
    selectIds = checkedRowKeys.value;
  }

  let content = '初始的密码为用户邮箱，下次登录时生效';
  if (record && record.name === 'admin') {
    content = '初始的密码为 Password，下次登录时生效';
  }
  window.$dialog.warning({
    title,
    content,
    positiveText: '确认重置',
    negativeText: '取消',
    onPositiveClick: async () => {
      const param = {
        selectIds,
        selectAll: !!params?.selectAll,
        excludeIds: params?.excludeIds || [],
        condition: {keyword: keyword.value},
      }
      await userApis.resetUserPassword(param);
      window.$message.success('重置成功');
    },
  })
}
const handleSelect = (key: string, record: IUserItem) => {
  switch (key) {
    case 'resetPassword':
      resetPassword(record);
      break
    case 'delete':
      deleteUser(record);
      break;
    default:
      break;
  }
}
onMounted(() => {
  loadList()
  initUGOptions()
})
</script>

<template>
  <n-card>
    <template #header>
      <!--      <n-button type="primary" size="small" @click="showAddModel = true"> 创建用户</n-button>-->
      <n-button type="primary" size="small" class="ml-2" @click="showUserModal('create')"> 创建用户</n-button>
      <n-button size="small" class="ml-2" disabled> 导入用户</n-button>
    </template>
    <template #header-extra>
      <n-input class="w-[240px]" clearable v-model:value="keyword" placeholder="通过 ID/名称搜索"/>
    </template>
    <n-data-table
        :columns="columns"
        :data="data"
        :row-key="(row: IUserItem) => row.id"
        :pagination="pagination"
        @update:checked-row-keys="handleCheck"
    />
  </n-card>
  <create-or-update-modal ref="createOrUpdateModalRef" v-model:show-modal="visible"
                          v-model:user-form="userForm"
                          :user-form-mode="userFormMode" @submit="loadList()"/>
  <edit-user ref="editUserRef" v-model:show-modal="showAddModel" v-model:user-form="userForm1"
             :user-form-mode="userFormMode" @submit="loadList()"/>
  <batch-modal ref="batchModalRef" v-model:show-modal="showBatchModal" :table-selected="checkedRowKeys"
               :action="batchAction" @finished="loadList"/>
</template>

<style scoped>

</style>