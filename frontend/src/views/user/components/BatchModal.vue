<script setup lang="ts">
import {ref, watch} from "vue";
import type {IBatchActionQueryParams, ITransferOption} from "/@/api/types/commons.ts";
import {userApis} from "/@/api/modules/user.ts";

const props = withDefaults(
    defineProps<{
      tableSelected: (string | number)[];
      action: string;
      batchParams?: IBatchActionQueryParams;
      keyword?: string;
    }>(),
    {}
);
const showModal = defineModel<boolean>('showModal', {type: Boolean, default: false});
const batchTitle = ref('');
const emit = defineEmits([ 'finished']);
const batchModalMode = ref<'project' | 'userGroup' | 'organization'>('project');
const innerTarget = ref<string[]>([]);
const options = ref<ITransferOption[]>([])
const handleTableBatch = async (action: string) => {
  let resTree: any[] = [];
  switch (action) {
    case 'batchAddProject':
      batchModalMode.value = 'project';
      batchTitle.value = '批量添加至项目';
      resTree = await userApis.getSystemProjects();
      options.value = resTree[0]?.children.map(item => {
        return {label: item.name, value: item.id, selected: item.selected};
      })
      break
    case 'batchAddUserGroup':
      batchModalMode.value = 'userGroup';
      batchTitle.value = '批量添加至用户组';
      resTree = await userApis.getSystemRoles();
      options.value = resTree.map(item => {
        const option: ITransferOption = {label: item.name, value: item.id, selected: item.selected};
        return option;
      })
      break
    case 'batchAddOrganization':
      batchModalMode.value = 'organization';
      batchTitle.value = '批量添加至组织';
      options.value = await userApis.getSystemOrgs();
      break
    default:
      break;
  }
};
const cancelBatch = () => {
  showModal.value = false;
  innerTarget.value = [];
}
const batchLoading = ref(false);
const handleBatchConfirm = async () => {
  batchLoading.value = true;
  try {
    const params = {
      selectIds: props.tableSelected as string[],
      selectAll: !!props.batchParams?.selectAll,
      excludeIds: props.batchParams?.excludeIds,
      condition: {
        keyword: props.keyword,
      },
      roleIds: innerTarget.value,
    };
    switch (batchModalMode.value
        ) {
      case 'project':
        await userApis.batchAddProject(params);
        break;
      case 'userGroup':
        await userApis.batchAddUserGroup(params);
        break;
      case 'organization':
        await userApis.batchAddOrg(params);
        break;
      default:
        break;
    }
    window.$message.success('添加成功');
    showModal.value = false;
    emit('finished');
  } catch (error) {
    console.log(error);
  } finally {
    batchLoading.value = false;
  }
};

watch(() => showModal.value, (show) => {
  if (show) {
    handleTableBatch(props.action)
  }
}, {
  immediate: true,
})
</script>

<template>
  <n-modal v-model:show="showModal" preset="dialog" title="Dialog">
    <template #header>
      <div>{{ batchTitle }}</div>
      <div class="text-[#9597a4]">
        {{ `（已选 ${props.batchParams?.currentSelectCount || props.tableSelected.length} 个用户）` }}
      </div>
    </template>
    <n-spin :show="false" class="w-full">
      <n-alert v-if="batchModalMode==='project'" class="mb-[16px]">
        默认为成员添加项目成员用户组
      </n-alert>
      <n-transfer
          v-model:value="innerTarget"
          virtual-scroll
          :options="options"
          source-filterable
      />
    </n-spin>
    <template #action>
      <n-button secondary :disabled="batchLoading" @click="cancelBatch">取消</n-button>
      <n-button type="primary" :disabled="innerTarget.length === 0" :loading="batchLoading" @click="handleBatchConfirm">
        添加
      </n-button>
    </template>
  </n-modal>
</template>

<style scoped>

</style>