<script setup lang="ts">
import {usePagination} from "alova/client";
import {userApis} from "/@/api/modules/user.ts";
import {onMounted, ref} from "vue";
import type {UserState} from "/@/store/modules/user/types.ts";
import {type DataTableColumns, type DataTableRowKey, NDataTable, type PaginationProps} from "naive-ui";

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
  }
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const pagination: PaginationProps = {
  page: page.value, pageSize: pageSize.value, size: 'small'
}
onMounted(() => {
  loadList()
})
</script>

<template>
  <n-data-table
      :columns="columns"
      :data="data"
      :row-key="(row: UserState) => row.id"
      :pagination="pagination"
      @update:checked-row-keys="handleCheck"
  />
</template>

<style scoped>

</style>