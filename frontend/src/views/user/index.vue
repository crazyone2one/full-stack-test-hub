<script setup lang="ts">
import {usePagination} from "alova/client";
import {userApis} from "/@/api/modules/user.ts";
import {onMounted, ref, useTemplateRef, h} from "vue";
import type {UserState} from "/@/store/modules/user/types.ts";
import {
  type DataTableColumns,
  type DataTableRowKey,
  NButton,
  NCard,
  NDataTable,
  NInput, NFlex,
  type PaginationProps
} from "naive-ui";
import EditUser from "/@/views/user/components/EditUser.vue";

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
    }
)
const columns: DataTableColumns<UserState> = [
  {
    type: 'selection',
  },
  {
    title: '用户名',
    key: 'name',
  },
  {
    title: '邮箱',
    key: 'email',
  },
  {
    title: '手机',
    key: 'phone',
    width: 140
  },
  {
    title: '操作',
    key: 'actions',
    fixed: 'right',
    width: 110,
    render(row) {
      return h(NFlex, {}, {
        default() {
          return h(NButton, {
            size: 'small',
            type: 'primary',
            text: true,
            onClick: () => handleEditClick(row)
          }, {
            default: () => '编辑'
          })
        }
      })
    }
  }
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const pagination: PaginationProps = {
  page: page.value, pageSize: pageSize.value, size: 'small'
}

const showAddModel = ref(false)
const userFormMode = ref<'create' | 'edit'>('create')
// const createOrUpdateModalRef = useTemplateRef<InstanceType<typeof CreateOrUpdateModal>>('createOrUpdateModal')
const editUserRef = useTemplateRef<InstanceType<typeof EditUser>>('editUser')
const userForm = ref<UserState>()
const handleEditClick = (row: UserState) => {
  showAddModel.value = true
  userForm.value = row
  userFormMode.value = 'edit'
}
onMounted(() => {
  loadList()
})
</script>

<template>
  <n-card>
    <template #header>
      <n-button type="primary" size="small" @click="showAddModel = true"> 创建用户</n-button>
    </template>
    <template #header-extra>
      <n-input class="w-[240px]" clearable v-model:value="keyword" placeholder="通过 ID/名称搜索"/>
    </template>
    <n-data-table
        :columns="columns"
        :data="data"
        :row-key="(row: UserState) => row.id"
        :pagination="pagination"
        @update:checked-row-keys="handleCheck"
    />
  </n-card>
  <!--  <create-or-update-modal ref="createOrUpdateModalRef" v-model:show-modal="showAddModel"/>-->
  <edit-user ref="editUserRef" v-model:show-modal="showAddModel" v-model:user-form="userForm"
             :user-form-mode="userFormMode" @submit="loadList()"/>
</template>

<style scoped>

</style>