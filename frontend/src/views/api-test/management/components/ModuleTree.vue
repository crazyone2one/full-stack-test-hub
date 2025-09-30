<script setup lang="ts">
import TreeFolderAll from "/@/views/api-test/components/TreeFolderAll.vue";
import {ProtocolKeyEnum} from "/@/enums/api-enum.ts";
import {computed, h, onBeforeMount, ref} from "vue";
import ApiPopConfirm from "/@/views/api-test/components/ApiPopConfirm.vue";
import {apiManagementApis} from "/@/api/modules/api-test/management.ts";
import {useAppStore} from "/@/store";
import {mapTree} from "/@/utils";
import {NButton, NDropdown, type TreeOption} from 'naive-ui'

const props = withDefaults(
    defineProps<{
      isExpandAll?: boolean; // 是否展开所有节点
      activeModule?: string | number; // 选中的节点 key
      readOnly?: boolean; // 是否是只读模式
      activeNodeId?: string | number; // 当前选中节点 id
      isModal?: boolean; // 是否弹窗模式，只读且只可见模块树
      trash?: boolean; // 是否是回收站
      docShareId?: string; // 是否分享文档
    }>(),
    {
      activeModule: 'all',
      readOnly: false,
      isModal: false,
      trash: false,
    }
);
const emit = defineEmits([
  'init',
  'newApi',
  'import',
  'folderNodeSelect',
  'clickApiNode',
  'changeProtocol',
  'updateApiNode',
  'deleteNode',
  'execute',
  'openCurrentNode',
  'exportShare',
]);
const appStore = useAppStore();
const treeFolderAllRef = ref<InstanceType<typeof TreeFolderAll>>();
const selectedProtocols = computed<string[]>(() => treeFolderAllRef.value?.selectedProtocols ?? []);
const isExpandAll = ref(props.isExpandAll);
const rootModulesName = ref<string[]>([]); // 根模块名称列表
const handleSelect = (value: string) => {
  switch (value) {
    case 'newApi':
      emit('newApi');
      break;
    case 'import':
      emit('import');
      break;
    default:
      break;
  }
}
const selectedKeys = ref<Array<string | number>>([props.activeModule]);
const folderTree = ref<TreeOption[]>([]);
const initModules = async (isSetDefaultKey = false) => {
  const res = await apiManagementApis.getModuleTree({
    keyword: '',
    protocols: selectedProtocols.value,
    projectId: appStore.currentProjectId,
    moduleIds: [],
  });
  const nodePathObj: Record<string, any> = {};
  folderTree.value = mapTree<TreeOption>(res, (e, fullPath) => {
    // 拼接当前节点的完整路径
    nodePathObj[e.id] = {
      path: e.path,
      fullPath,
    };
    return {
      ...e,
      hideMoreAction: e.id === 'root',
      draggable: e.id !== 'root',
    };
  });
  if (isSetDefaultKey) {
    selectedKeys.value = [folderTree.value[0].id as string];
  }
  emit('init', folderTree.value, selectedProtocols.value, nodePathObj);
}
const handleAddFinish = () => {
  initModules();
}
const focusNodeKey = ref<string | number>('');
const renamePopVisible = ref(false);
const renameFolderTitle = ref(''); // 重命名的文件夹名称
const resetFocusNodeKey = () => {
  focusNodeKey.value = '';
  renamePopVisible.value = false;
  renameFolderTitle.value = '';
}
const deleteFolder = (option: TreeOption) => {
  window.$dialog.error({
    title: option.type === 'API' ? `是否删除 ${option.name}？` : `是否删除 ${option.name} 模块？`,
    content: option.type === 'API' ? '删除后无法恢复，请谨慎操作！' : '该操作会删除模块及其下所有资源，请谨慎操作！',
  })
}
const handleNodeMoreSelect = (key: string, option: TreeOption) => {
  switch (key) {
    case 'delete':
      deleteFolder(option);
      resetFocusNodeKey();
      break;
    case 'rename':
      renameFolderTitle.value = option.name as string || '';
      renamePopVisible.value = true;
      document.querySelector(`#renameSpan${option.id}`)?.dispatchEvent(new Event('click'));
      break;
    case 'execute':
      emit('execute', option.id);
      break;
    default:
      break;
  }
}
const folderMoreActions = [
  {
    label: '重命名',
    key: 'rename'
  },
  {
    label: '执行',
    key: 'execute',
    disabled: true
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: '删除',
    key: 'delete'
  }]
const renderSuffix = ({option}: { option: TreeOption }) => {
  let suffix = []
  if (option.id !== 'root' && option.type === 'MODULE') {
    suffix.push(
        h(ApiPopConfirm,
            {
              mode: 'add',
              allNames: (option.children || []).map((e: TreeOption) => e.name || '') as string[],
              parentId: option.id as string,
              addModuleApi: apiManagementApis.addModule,
              // 处理 emit 事件
              onAddFinish: () => {
                handleAddFinish();
              }
            }, {
              default: () => h(NButton, {text: true}, {default: () => h('div', {class: 'add-icon text-[14px]'})})
            }))
  }
  if (option.id !== 'root') {
    suffix.push(
        h(ApiPopConfirm,
            {
              mode: 'rename',
              allNames: folderTree.value.map((e: TreeOption) => e.name || '') as string[],
              parentId: option.id as string,
              nodeType: option.type as 'MODULE' | 'API',
              fieldConfig: {field: renameFolderTitle.value},
              updateModuleApi: apiManagementApis.updateModule,
              nodeId: option.id as string,
              onRenameFinish: () => {
                handleAddFinish();
              }
            }, {
              default: () => h("div", {class: 'relative h-full', id: `renameSpan${option.id}`})
            }));
  }
  suffix.push(
      h(NDropdown,
          {
            showArrow: true, options: folderMoreActions, onSelect: (v) => handleNodeMoreSelect(v, option)
          }, {
            default: () => h(NButton, {text: true, class: 'ml-1'},
                {default: () => h('div', {class: 'i-mdi:animation-outline text-[14px]'})})
          }));
  return suffix;
}
const renderLabel = ({option}: { option: TreeOption }) => {
  if (option.type === 'API') {
    return h('div', {class: 'inline-flex w-full cursor-pointer gap-[4px]'},
        {default: () => h('div', {class: 'one-line-text w-full'}, {default: () => option.name})});
  } else {
    return h('div', {class: 'inline-flex gap-[8px]'},
        {
          default: () => [
            h('div', {class: 'one-line-text w-full text-[#c1c4d0ff]'}, {default: () => option.name}),
            h('div', {class: 'ml-1 text-[#787b88]'}, 0)
          ]
        });
  }
};
const handleNodeSelect = (_selectedKeys: Array<string>, option: Array<TreeOption | null>) => {
  const node = option[0];
  if (node?.type === 'MODULE') {
    const offspringIds: string[] = [];
    mapTree(node?.children || [], (e) => {
      offspringIds.push(e.id);
      return e;
    });
    selectedKeys.value = _selectedKeys
    emit('folderNodeSelect', _selectedKeys, offspringIds)
  } else if (node?.type === 'API') {
    // todo
  }
}
onBeforeMount(() => {
  initModules(true)
})
</script>

<template>
  <div>
    <template v-if="!props.isModal">
      <div v-if="!props.readOnly && !props.trash && !props.docShareId" class="mb-[8px] flex items-center gap-[8px]">
        <n-button v-permission="['PROJECT_API_DEFINITION:READ+ADD']" type="primary"
                  @click="handleSelect('newApi')">新建接口
        </n-button>
        <n-button disabled>导入接口</n-button>
      </div>
      <n-input :placeholder="props.isModal ? '请输入模块名称搜索' : '请输入模块/接口名称'" class="mb-[8px]" clearable/>
      <tree-folder-all ref="treeFolderAllRef"
                       v-model:isExpandAll="isExpandAll"
                       folder-name="全部接口" :all-count="0" :protocol-key="ProtocolKeyEnum.API_MODULE_TREE_PROTOCOL">
        <template #expandRight>
          <api-pop-confirm mode="add" :all-names="rootModulesName" parent-id="NONE"
                           :add-module-api="apiManagementApis.addModule"
                           @add-finish="handleAddFinish">
            <n-button type="primary" text>
              <div class="add-icon text-[18px]"/>
            </n-button>
          </api-pop-confirm>
          <n-tooltip trigger="hover">
            <template #trigger>
              <n-button text disabled>
                <div class="i-mdi:format-vertical-align-top"/>
              </n-button>
            </template>
            导出
          </n-tooltip>
        </template>
      </tree-folder-all>
    </template>
    <n-input v-else :placeholder="props.isModal ? '请输入模块名称搜索' : '请输入模块/接口名称'" class="mb-[16px]"
             clearable/>
    <n-tree block-line :data="folderTree" label-field="name" key-field="id"
            :selected-keys="selectedKeys"
            :render-suffix="renderSuffix"
            :render-label="renderLabel"
            :default-expand-all="isExpandAll"
            @update-selected-keys="handleNodeSelect"/>
  </div>
</template>

<style scoped>

</style>