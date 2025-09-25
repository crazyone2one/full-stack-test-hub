<script setup lang="ts">
import {computed, inject, ref, watchEffect} from "vue";
import {AuthScopeEnum, type AuthScopeEnumType} from "/@/enums/common-enum.ts";
import {useAppStore} from "/@/store";
import type {ICurrentUserGroupItem} from "/@/api/types/user-group.ts";
import {usePagination} from "alova/client";
import {userGroupApis} from "/@/api/modules/user-group.ts";
import {type DataTableColumns, type DataTableRowKey, NDataTable} from "naive-ui";
import type {IUserItem} from "/@/api/types/user.ts";
import type {ProjectTableItem} from "/@/api/types/project.ts";
import type {ITableQueryParams} from "/@/api/types/commons.ts";

const systemType = inject<AuthScopeEnumType>('systemType');
const props = defineProps<{
  keyword: string;
  current: ICurrentUserGroupItem;
}>();
const appStore = useAppStore();
const currentOrgId = computed(() => appStore.currentOrgId);
const {page, pageSize, total, data, send: fetchData} = usePagination((page, pageSize) => {
  const param: ITableQueryParams = {page, pageSize, keyword: props.keyword}
  if (systemType === AuthScopeEnum.SYSTEM) {
    if (props.current.id && currentOrgId.value) {
      param.roleId = props.current.id;
    }
    return userGroupApis.fetchUserByUserGroup(param);
  } else {
    if (props.current.id && currentOrgId.value) {
      param.userRoleId = props.current.id;
      param.organizationId = currentOrgId.value;
    }
    return userGroupApis.fetchOrgUserByUserGroup(param)
  }
}, {
  initialData: {total: 0, data: []},
  immediate: false,
  data: resp => resp.records,
  total: resp => resp.totalRow,
  // watchingStates: [props.keyword]
})
const columns: DataTableColumns<IUserItem> = [
  {
    type: 'selection',
  },
  {title: '姓名', key: 'name'},
  {title: '邮箱', key: 'email'},
  {title: '手机', key: 'phone'},
  {title: '操作', key: 'actions', fixed: 'right', width: 100},
]
const checkedRowKeys = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => {
  checkedRowKeys.value = rowKeys
}
watchEffect(() => {
  if (props.current.id && currentOrgId.value) {
    fetchData();
  }
});
defineExpose({
  fetchData,
});
</script>

<template>
  <n-data-table :columns="columns"
                :data="data"
                :row-key="(row: ProjectTableItem) => row.id"
                @update:checked-row-keys="handleCheck"/>
  <base-pagination v-model:page="page" v-model:page-size="pageSize" :count="total||0"/>
</template>

<style scoped>

</style>