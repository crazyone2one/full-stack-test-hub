<script setup lang="ts">
import {computed, onMounted, ref, h} from "vue";
import {useAppStore} from "/@/store";
import {type IUserGroupItem} from "/@/api/types/user-group.ts";
import {type DataTableColumns, type DataTableRowKey, NButton, NDivider} from "naive-ui";
import {usePagination} from "alova/client";
import {userGroupApis} from "/@/api/modules/user-group.ts";
import {hasAnyPermission} from "/@/utils/permissions.ts";


const appStore = useAppStore();
const keyword = ref('')
const currentProjectId = computed(() => appStore.currentProjectId);
const columns: DataTableColumns<IUserGroupItem> = [
  {
    title: '用户组名称', key: 'name', render(record) {
      return h('div', {class: 'flex flex-row items-center gap-[4px]'}, {
        default: () => [
          h('div', {class: 'one-line-text'}, {default: () => record.name}),
          h('div', {class: 'ml-1'}, {
            default: () => `(${record.internal ? '系统内置' : record.scopeId === 'global' ? '系统自定义' : '自定义'})`
          })
        ]
      })
    }
  },
  {title: '成员数', key: 'memberCount'},
  {
    title: '操作', key: 'operation', width: 150, render(record) {
      return h('div', {class: 'flex flex-row flex-nowrap'}, {
        default: () => {
          const operations = []
          if (hasAnyPermission(['PROJECT_GROUP:READ'])) {
            operations.push(h(NButton, {text: true, class: '!mr-0'}, {default: () => '查看权限'}))
            if (record.scopeId !== 'global') {
              operations.push(h(NDivider, {}, {}));
            }
          }
          if (record.scopeId !== 'global') {
            if (hasAnyPermission(['PROJECT_GROUP:READ+UPDATE'])) {
              operations.push(h(NButton, {text: true, class: '!mr-0', type: 'error'}, {default: () => '删除'}));
            }
          }
          return operations;
        }
      })
    }
  },
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
const {
  page,
  pageSize,
  data,
  send: fetchData, total
} = usePagination((page, pageSize) => userGroupApis.fetchUserGroupPage({
      page,
      pageSize,
      keyword: keyword.value,
      projectId: currentProjectId.value
    }),
    {
      initialData: {total: 0, data: []},
      immediate: false,
      data: resp => resp.records,
      total: resp => resp.totalRow,
      watchingStates: [keyword]
    })
onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="flex flex-row items-center justify-between">
    <n-button type="primary">添加用户组</n-button>
    <div>
      <n-input placeholder="通过名称搜索" v-model:value="keyword" clearable class="w-[240px]"/>
    </div>
  </div>
  <n-data-table :columns="columns"
                :data="data"
                :row-key="(row: IUserGroupItem) => row.id"
                class="mt-[16px]"
                @update:checked-row-keys="handleCheck"/>
  <base-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
</template>

<style scoped>

</style>