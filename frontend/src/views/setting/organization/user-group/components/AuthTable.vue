<script setup lang="ts">
import {AuthScopeEnum, type AuthScopeEnumType} from "/@/enums/common-enum.ts";
import {computed, h, inject, ref, type VNode, watchEffect} from "vue";
import {type DataTableColumns, NCheckbox, NCheckboxGroup} from "naive-ui";
import {useRequest} from "alova/client";
import {userGroupApis} from "/@/api/modules/user-group.ts";
import type {
  IAuthTableItem,
  ICurrentUserGroupItem,
  ISavePermissions,
  IUserGroupAuthSetting
} from "/@/api/types/user-group.ts";

const props = withDefaults(defineProps<{
  current: ICurrentUserGroupItem;
  disabled?: boolean;
  showBottom?: boolean;
}>(), {
  disabled: false,
  showBottom: true
})
const systemType = inject<AuthScopeEnumType>('systemType');
const allChecked = ref(false);
const allIndeterminate = ref(false);
const canSave = ref(false);
const tableData = ref<IAuthTableItem[]>();
const {send: fetchData} = useRequest((id) => {
  if (systemType === AuthScopeEnum.SYSTEM) {
    return userGroupApis.getGlobalUSetting(id)
  } else if (systemType === AuthScopeEnum.ORGANIZATION) {
    return userGroupApis.getOrgUSetting(id);
  } else {
    return userGroupApis.getAuthByUserGroup(id);
  }
}, {immediate: false, force: true})
const initData = (id: string) => {
  tableData.value = []; // 重置数据，可以使表格滚动条重新计算
  fetchData(id).then(res => {
    tableData.value = transformData(res);
    handleAllChange(true);
  })
};
const handleAllChange = (isInit = false) => {
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const {length: allLength} = tmpArr;
  const {length} = tmpArr.filter((item) => item.enable);
  if (length === allLength) {
    allChecked.value = true;
    allIndeterminate.value = false;
  } else if (length === 0) {
    allChecked.value = false;
    allIndeterminate.value = false;
  } else {
    allChecked.value = false;
    allIndeterminate.value = true;
  }
  if (!isInit && !canSave.value) canSave.value = true;
}
const transformData = (data: IUserGroupAuthSetting[]) => {
  const result: IAuthTableItem[] = [];
  data.forEach((item) => {
    result.push(...makeData(item));
  });
  return result;
};
const makeData = (item: IUserGroupAuthSetting) => {
  const result: IAuthTableItem[] = [];
  item.children?.forEach((child, index) => {
    if (!child.license) {
      const perChecked =
          child?.permissions?.reduce((acc: string[], cur) => {
            if (cur.enable) {
              acc.push(cur.id);
            }
            return acc;
          }, []) || [];
      const perCheckedLength = perChecked.length;
      let indeterminate = false;
      if (child?.permissions) {
        indeterminate = perCheckedLength > 0 && perCheckedLength < child?.permissions?.length;
      }
      result.push({
        id: child?.id,
        license: child?.license,
        enable: child?.enable,
        permissions: child?.permissions,
        indeterminate,
        perChecked,
        ability: index === 0 ? item.name : undefined,
        operationObject: child.name,
        rowSpan: index === 0 ? item.children?.length || 1 : undefined,
      });
    }
  });
  return result;
}
const systemAdminDisabled = computed(() => {
  const adminArr = ['admin', 'org_admin', 'project_admin'];
  const {id} = props.current;
  if (adminArr.includes(id)) {
    // 系统管理员,组织管理员，项目管理员都不可编辑
    return true;
  }

  return props.disabled;
});
const columns: DataTableColumns<IAuthTableItem> = [
  {
    title: '功能',
    key: 'ability',
    width: 100
  },
  {
    title: '操作对象',
    key: 'operationObject'
  },
  {
    title: () => {
      return h('div', {class: 'flex w-full flex-row justify-between'}, {
        default: () => {
          return [
            h('div', {}, {default: () => '权限'}),
            h(NCheckbox, {checked: allChecked.value, indeterminate: allIndeterminate.value, class: 'mr-[7px]'}, {})
          ];
        }
      })
    },
    key: 'permissions',
    render(record, rowIndex) {
      return h('div', {class: 'flex flex-row items-center justify-between'}, {
        default: () => {
          return [
            h(NCheckboxGroup, {
              value: record.perChecked,
              onUpdateValue: (value, meta) => handleCellAuthChange(value, meta, rowIndex, record)
            }, {
              default: () => {
                let result: VNode[] = []
                record.permissions?.forEach(item => {
                  result.push(h(NCheckbox, {
                    value: item.id,
                    disabled: systemAdminDisabled.value || props.disabled,
                  }, {default: () => item.name}))
                })
                return result
              }
            }),
            h(NCheckbox, {
              class: 'mr-[7px]',
              checked: record.enable,
              indeterminate: record.indeterminate,
              onUpdateChecked: (value) => handleRowAuthChange(value, rowIndex)
            }, {})
          ];
        }
      })
    }
  }
]
const handleRowAuthChange = (value: boolean, rowIndex: number) => {
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  tmpArr[rowIndex].indeterminate = false;
  if (value) {
    tmpArr[rowIndex].enable = true;
    tmpArr[rowIndex].perChecked = tmpArr[rowIndex].permissions?.map((item) => item.id);
  } else {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].perChecked = [];
  }
  tableData.value = [...tmpArr];
  handleAllChange();
  if (!canSave.value) canSave.value = true;
}
const handleCellAuthChange = (_value: (string | number)[],
                              meta: { actionType: 'check' | 'uncheck', value: string | number },
                              rowIndex: number,
                              record: IAuthTableItem
) => {
  setAutoRead(record, meta.value as string);
  if (!tableData.value) return;
  const tmpArr = tableData.value;
  const length = tmpArr[rowIndex].permissions?.length || 0;
  if (record.perChecked?.length === length) {
    tmpArr[rowIndex].enable = true;
    tmpArr[rowIndex].indeterminate = false;
  } else if (record.perChecked?.length === 0) {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].indeterminate = false;
  } else {
    tmpArr[rowIndex].enable = false;
    tmpArr[rowIndex].indeterminate = true;
  }
  handleAllChange();
}
const setAutoRead = (record: IAuthTableItem, currentValue: string) => {
  if (!record.perChecked?.includes(currentValue)) {
    // 如果当前没有选中则执行自动添加查询权限逻辑
    // 添加权限值
    record.perChecked?.push(currentValue);
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    const lastEditStr = currentValue.split('+')[1]; // 编辑类权限通过+号拼接
    const existRead = record.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'READ'
    );
    const existCreate = record.perChecked?.some(
        (item: string) => item.split(':')[0] === preStr && item.split(':')[1] === 'ADD'
    );
    if (!existRead && postStr !== 'READ') {
      record.perChecked?.push(`${preStr}:READ`);
    }
    if (!existCreate && lastEditStr === 'IMPORT') {
      // 勾选导入时自动勾选新增和查询
      record.perChecked?.push(`${preStr}:ADD`);
      record.perChecked?.push(`${preStr}:READ+UPDATE`);
    }
  } else {
    // 删除权限值
    const preStr = currentValue.split(':')[0];
    const postStr = currentValue.split(':')[1];
    if (postStr === 'READ') {
      // 当前是查询 那 移除所有相关的
      record.perChecked = record.perChecked.filter((item: string) => !item.includes(preStr));
    } else {
      record.perChecked.splice(record.perChecked.indexOf(currentValue), 1);
    }
  }
}
const handleReset = () => {
  if (props.current.id) {
    initData(props.current.id);
  }
};
const {send: handleSavePermissions} = useRequest((param) => {
  if (systemType === AuthScopeEnum.SYSTEM) {
    return userGroupApis.saveGlobalUSetting(param);
  } else if (systemType === AuthScopeEnum.ORGANIZATION) {
    return userGroupApis.saveOrgUSetting(param);
  } else {
    return userGroupApis.saveProjectUGSetting(param);
  }
}, {immediate: false})
const handleSave = () => {
  if (!tableData.value) return;
  const permissions: ISavePermissions[] = [];

  const tmpArr = tableData.value;
  tmpArr.forEach((item) => {
    item.permissions?.forEach((ele) => {
      ele.enable = item.perChecked?.includes(ele.id) || false;
      permissions.push({id: ele.id, enable: ele.enable,});
    });
  });
  handleSavePermissions({userRoleId: props.current.id, permissions}).then(() => {
    canSave.value = false;
    initData(props.current.id);
    window.$message.success('保存成功')
  })
}
watchEffect(() => {
  if (props.current.id) {
    initData(props.current.id);
  }
});
defineExpose({
  canSave,
  handleSave,
  handleReset,
});
</script>

<template>
  <div class="flex h-full flex-col gap-[16px] overflow-hidden">
    <div>
      <n-data-table :columns="columns" :data="tableData"/>
    </div>
    <div v-if="props.showBottom && props.current.id !== 'admin' && !systemAdminDisabled" class="footer">
      <n-button :disabled="!canSave" @click="handleReset">重置</n-button>
      <n-button type="primary" :disabled="!canSave" @click="handleSave">保存</n-button>
    </div>
  </div>
</template>

<style scoped>
.footer {
  display: flex;
  justify-content: flex-end;
  padding: 24px;
  background-color: #fff;
  box-shadow: 0 -1px 4px rgb(2 2 2 / 10%);
  gap: 16px;
}
</style>