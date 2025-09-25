<script setup lang="ts">
import {AuthScopeEnum, type AuthScopeEnumType} from "/@/enums/common-enum.ts";
import {computed, inject, onMounted, ref} from "vue";
import CreateOrUpdateUserGroup from "/@/views/setting/organization/user-group/components/CreateOrUpdateUserGroup.vue";
import type {ICurrentUserGroupItem, IUserGroupItem, PopVisible} from "/@/api/types/user-group.ts";
import {hasAnyPermission} from "/@/utils/permissions.ts";
import {useAppStore} from "/@/store";
import {userGroupApis} from "/@/api/modules/user-group.ts";
import {useRouter} from "vue-router";
import AddUserModal from "/@/components/user-group-comp/AddUserModal.vue";

const emit = defineEmits<{
  (e: 'handleSelect', element: IUserGroupItem): void;
  (e: 'addUserSuccess', id: string): void;
}>();

const props = defineProps<{
  addPermission: string[];
  updatePermission: string[];
  isGlobalDisable: boolean;
}>();
const router = useRouter();
const systemType = inject<AuthScopeEnumType>('systemType');
const showSystem = computed(() => systemType === AuthScopeEnum.SYSTEM);
const showOrg = computed(() => systemType === AuthScopeEnum.SYSTEM || systemType === AuthScopeEnum.ORGANIZATION);
const showProject = computed(() => systemType === AuthScopeEnum.SYSTEM || systemType === AuthScopeEnum.PROJECT);
// 用户组列表
const userGroupList = ref<IUserGroupItem[]>([]);
// 系统用户组Toggle
const systemToggle = ref(true);
// 组织用户组Toggle
const orgToggle = ref(true);
// 项目用户组Toggle
const projectToggle = ref(true);
// 系统用户创建用户组visible
const systemUserGroupVisible = ref(false);
// 组织用户创建用户组visible
const orgUserGroupVisible = ref(false);
// 项目用户创建用户组visible
const projectUserGroupVisible = ref(false);
const userModalVisible = ref(false);
// 系统用户组列表
const systemUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.SYSTEM);
});
// 组织用户组列表
const orgUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.ORGANIZATION);
});
// 项目用户组列表
const projectUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.PROJECT);
});
const handleCreateUG = (scoped: AuthScopeEnumType) => {
  if (scoped === AuthScopeEnum.SYSTEM) {
    systemUserGroupVisible.value = true;
  } else if (scoped === AuthScopeEnum.ORGANIZATION) {
    orgUserGroupVisible.value = true;
  } else if (scoped === AuthScopeEnum.PROJECT) {
    projectUserGroupVisible.value = true;
  }
}
const isSystemShowAll = computed(() => {
  return hasAnyPermission([...props.updatePermission, 'SYSTEM_USER_ROLE:READ+DELETE']);
});
const isOrdShowAll = computed(() => {
  return hasAnyPermission([...props.updatePermission, 'ORGANIZATION_USER_ROLE:READ+DELETE']);
});
const isProjectShowAll = computed(() => {
  return hasAnyPermission([...props.updatePermission, 'PROJECT_GROUP:READ+DELETE']);
});
// 气泡弹窗
const popVisible = ref<PopVisible>({});
const currentId = ref('');
const currentItem = ref<ICurrentUserGroupItem>({id: '', name: '', internal: false, type: AuthScopeEnum.SYSTEM});
const systemMoreAction = [
  {
    label: 'system.userGroup.rename',
    danger: false,
    eventTag: 'rename',
    permission: props.updatePermission,
  },
  {
    isDivider: true,
  },
  {
    label: 'system.userGroup.delete',
    danger: true,
    eventTag: 'delete',
    permission: ['SYSTEM_USER_ROLE:READ+DELETE'],
  },
];
const handleAddMember = () => {
  userModalVisible.value = true;
}
const handleListItemClick = (element: IUserGroupItem) => {
  const {id, name, type, internal} = element;
  currentItem.value = {id, name, type, internal};
  currentId.value = id;
  emit('handleSelect', element);
}
const appStore = useAppStore();

const initData = async (id?: string, isSelect = true) => {
  let res: IUserGroupItem[] = [];
  if (systemType === AuthScopeEnum.SYSTEM && hasAnyPermission(['SYSTEM_USER_ROLE:READ'])) {
    res = await userGroupApis.getUserGroupList();
  } else if (systemType === AuthScopeEnum.ORGANIZATION && hasAnyPermission(['ORGANIZATION_USER_ROLE:READ'])) {
    res = await userGroupApis.getOrgUserGroupList(appStore.currentOrgId);
  } else if (systemType === AuthScopeEnum.PROJECT && hasAnyPermission(['PROJECT_GROUP:READ'])) {
    res = await userGroupApis.getProjectUserGroupList(appStore.currentProjectId);
  }
  if (res.length > 0) {
    userGroupList.value = res;
    if (isSelect) {
      // leftCollapse 切换时不重复数据请求
      if (id) {
        const item = res.find((i) => i.id === id);
        if (item) {
          handleListItemClick(item);
        } else {
          window.$message.warning("资源已被删除")
          handleListItemClick(res[0]);
        }
      } else {
        handleListItemClick(res[0]);
      }
    }
    // 弹窗赋值
    const tmpObj: PopVisible = {};
    res.forEach((element) => {
      tmpObj[element.id] = {visible: false, authScope: element.type, defaultName: '', id: element.id};
    });
    popVisible.value = tmpObj;
  }
}
const handleCreateUserGroup = (id: string) => {
  initData(id);
}
const handleAddUserCancel = (shouldSearch: boolean) => {
  userModalVisible.value = false;
  if (shouldSearch) {
    emit('addUserSuccess', currentId.value);
  }
};
onMounted(() => {
  initData(router.currentRoute.value.query.id as string, true)
})
defineExpose({
  initData,
});
</script>

<template>
  <div class="flex flex-col px-[16px] pb-[16px]">
    <div class="sticky top-0 z-[999]  pb-[8px] pt-[16px]">
      <n-input placeholder="请输入用户组名称"/>
    </div>
    <div v-if="showSystem" class="mt-2">
      <div class="flex items-center justify-between px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1 text-[#9597a4]">
          <div v-if="systemToggle" class="i-mdi:chevron-down-circle cursor-pointer text-[16px]"
               @click="systemToggle = false"/>
          <div v-else class="i-mdi:chevron-right-circle cursor-pointer text-[16px]" @click="systemToggle = true"/>
          <div class="text-[14px]">系统用户组</div>
        </div>
        <create-or-update-user-group :list="systemUserGroupList" :visible="systemUserGroupVisible"
                                     :auth-scope="AuthScopeEnum.SYSTEM"
                                     @cancel="systemUserGroupVisible = false"
                                     @submit="handleCreateUserGroup">
          <n-tooltip>
            <template #trigger>
              <div class="add-icon text-[#36ad6a]  text-[20px]" @click="handleCreateUG(AuthScopeEnum.SYSTEM)"/>
            </template>
            创建系统用户组
          </n-tooltip>
        </create-or-update-user-group>
      </div>
      <Transition>
        <div v-if="systemToggle">
          <div v-for="item in systemUserGroupList" :key="item.id" class="list-item"
               :class="{ '!bg-[#accb9f]': item.id === currentId }"
               @click="handleListItemClick(item)">
            <div class="flex items-center justify-between px-[4px] py-[7px]">
              <create-or-update-user-group :list="systemUserGroupList"
                                           :auth-scope="popVisible[item.id].authScope"
                                           :visible="popVisible[item.id].visible"
                                           :default-name="popVisible[item.id].defaultName">
                <div class="flex max-w-[100%] grow flex-row items-center justify-between">
                  <div
                      class="list-item-name one-line-text"
                      :class="{ 'text-[#18a058]': item.id === currentId }"
                  >
                    {{ item.name }}
                  </div>
                  <div v-if="item.type === systemType ||(isSystemShowAll &&!item.internal &&(item.scopeId !== 'global' || !isGlobalDisable) && systemMoreAction.length > 0)
                  "
                       class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                       :class="{ '!opacity-100': item.id === currentId }">
                    <div v-if="item.type === systemType">
                      <div class="add-icon text-[16px]" @click="handleAddMember"/>
                    </div>
                  </div>
                </div>
              </create-or-update-user-group>
            </div>
          </div>
          <n-divider class="my-[0px] mt-[6px]"/>
        </div>
      </Transition>
    </div>
    <div v-if="showOrg" class="mt-2">
      <div class="flex items-center justify-between px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1 text-[#9597a4]">
          <div v-if="orgToggle" class="i-mdi:chevron-down-circle cursor-pointer text-[16px]"
               @click="orgToggle = false"/>
          <div v-else class="i-mdi:chevron-right-circle cursor-pointer text-[16px]" @click="orgToggle = true"/>
          <div class="text-[14px]">组织用户组</div>
        </div>
        <create-or-update-user-group :list="orgUserGroupList" :visible="orgUserGroupVisible"
                                     :auth-scope="AuthScopeEnum.ORGANIZATION"
                                     @cancel="orgUserGroupVisible = false"
                                     @submit="handleCreateUserGroup">
          <n-tooltip>
            <template #trigger>
              <div class="add-icon text-[#36ad6a]  text-[20px]" @click="orgUserGroupVisible = true"/>
            </template>
            创建组织用户组
          </n-tooltip>
        </create-or-update-user-group>
      </div>
      <Transition>
        <div v-if="orgToggle">
          <div v-for="item in orgUserGroupList" :key="item.id"
               class="list-item"
               :class="{ '!bg-[#accb9f]': item.id === currentId }"
               @click="handleListItemClick(item)">
            <create-or-update-user-group :list="orgUserGroupList"
                                         :auth-scope="popVisible[item.id].authScope"
                                         :visible="popVisible[item.id].visible">
              <div class="flex w-full grow flex-row items-center justify-between">
                <div class="flex w-[calc(100%-56px)] items-center gap-[4px]"
                >
                  <div class="list-item-action one-line-text"
                       :class="`${systemType === AuthScopeEnum.ORGANIZATION ? 'max-w-[calc(100%-86px)]' : 'w-full'} ${item.id === currentId ? 'text-[#18a058]' : ''}`"
                  >
                    {{ item.name }}
                  </div>
                  <div v-if="systemType === AuthScopeEnum.ORGANIZATION">
                    {{ `(${item.internal ? '系统内置' : item.scopeId === 'global' ? '系统自定义' : '自定义'})` }}
                  </div>
                </div>
                <div v-if="
                    item.type === systemType ||
                    (isOrdShowAll && !item.internal && (item.scopeId !== 'global' || !isGlobalDisable) && systemMoreAction.length > 0)
                  "
                     class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                     :class="{ '!opacity-100': item.id === currentId }">
                  <div v-if="item.type === systemType">
                    <div class="add-icon" @click="handleAddMember"/>
                  </div>
                  <div v-if="
                      isOrdShowAll &&
                      !item.internal &&
                      (item.scopeId !== 'global' || !isGlobalDisable)
                    ">
                    <n-split>
                      <n-button text>重命名</n-button>
                      <n-button text>删除</n-button>
                    </n-split>
                  </div>

                </div>
              </div>
            </create-or-update-user-group>
          </div>
          <n-divider v-if="showSystem" class="my-[0px] mt-[6px]"/>
        </div>
      </Transition>
    </div>
    <div v-if="showProject" class="mt-2">
      <div class="flex items-center justify-between px-[4px] py-[7px]">
        <div class="flex flex-row items-center gap-1 text-[#9597a4]">
          <div v-if="projectToggle" class="i-mdi:chevron-down-circle" @click="projectToggle = false"/>
          <div v-else class="i-mdi:chevron-right-circle" @click="projectToggle = true"/>
          <div class="text-[14px]">项目用户组</div>
        </div>
        <create-or-update-user-group :list="projectUserGroupList"
                                     :visible="projectUserGroupVisible"
                                     :auth-scope="AuthScopeEnum.PROJECT"
                                     @cancel="projectUserGroupVisible = false"
                                     @submit="handleCreateUserGroup">
          <n-tooltip>
            <template #trigger>
              <div class="add-icon text-[#36ad6a]  text-[20px]" @click="projectUserGroupVisible = true"/>
            </template>
            创建项目用户组
          </n-tooltip>
        </create-or-update-user-group>
      </div>
      <Transition>
        <div v-if="projectToggle">
          <div v-for="item in projectUserGroupList" :key="item.id" class="list-item"
               :class="{ '!bg-[#accb9f]': item.id === currentId }"
               @click="handleListItemClick(item)">
            <div class="flex items-center justify-between px-[4px] py-[7px]">
              <create-or-update-user-group :list="projectUserGroupList" :auth-scope="popVisible[item.id].authScope"
                                           :visible="popVisible[item.id].visible">
                <div class="flex max-w-[100%] grow flex-row items-center justify-between">
                  <div
                      class="list-item-name one-line-text text-[var(--color-text-1)]"
                      :class="{ '!text-[rgb(var(--primary-5))]': item.id === currentId }"
                  >
                    {{ item.name }}
                  </div>
                  <div v-if="
                    item.type === systemType ||
                    (isProjectShowAll && !item.internal && (item.scopeId !== 'global' || !isGlobalDisable) && systemMoreAction.length > 0)
                  "
                       class="list-item-action flex flex-row items-center gap-[8px] opacity-0"
                       :class="{ '!opacity-100': item.id === currentId }">
                    <div v-if="item.type === systemType">
                      <div class="add-icon" @click="handleAddMember"/>
                    </div>
                  </div>
                </div>
              </create-or-update-user-group>
            </div>
          </div>
          <n-divider v-if="showSystem" class="my-[0px] mt-[6px]"/>
        </div>
      </Transition>
    </div>
  </div>
  <add-user-modal :show-modal="userModalVisible" :current-id="currentItem.id" @cancel="handleAddUserCancel"/>
</template>

<style scoped>
.list-item {
  border-radius: 2px;

  &:hover .list-item-action {
    opacity: 1;
  }
}
</style>