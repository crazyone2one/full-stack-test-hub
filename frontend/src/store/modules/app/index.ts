import {defineStore} from "pinia";
import {computed, ref} from "vue";
import type {AppState} from "/@/store/modules/app/types.ts";
import {projectManagementApis} from "/@/api/modules/project-management.ts";
import {featureRouteMap} from "/@/router/constants.ts";
import type {RouteRecordRaw} from "vue-router";
import {cloneDeep} from "es-toolkit";

const useAppStore = defineStore('app', () => {
    const appState = ref<AppState>({
        currentOrgId: '',
        currentProjectId: '',
        innerHeight: 0,
        menuCollapse: false,
        loading: false,
        loadingTip: '你不知道你有多幸运...',
        projectList: [],
        currentMenuConfig: Object.keys(featureRouteMap),
        topMenus: [],
        currentTopMenu: {} as RouteRecordRaw
    })
    const currentOrgId = computed(() => {
        return appState.value.currentOrgId
    })
    const currentProjectId = computed(() => {
        return appState.value.currentProjectId
    })
    const loadingStatus = computed(() => {
        return appState.value.loading
    })
    const getCurrentTopMenu = computed(() => {
        return appState.value.currentTopMenu
    })
    const getTopMenus = computed(() => {
        return appState.value.topMenus
    })
    const setCurrentOrgId = (id: string) => {
        appState.value = {
            ...appState.value,
            currentOrgId: id
        };
    }
    const setCurrentProjectId = (id: string) => {
        appState.value = {
            ...appState.value,
            currentProjectId: id
        };
    }
    const showLoading = (tip = '') => {
        appState.value = {
            ...appState.value,
            loading: true,
            loadingTip: tip || '加载中...'
        };
    }
    const hideLoading = () => {
        appState.value = {
            ...appState.value,
            loading: false
        };
    }
    const initProjectList = async () => {
        // 检查 currentOrgId 是否为空
        if (!appState.value.currentOrgId) {
            console.warn('currentOrgId is empty, skip project list initialization');
            return;
        }
        try {
            showLoading('加载项目列表中...');
            const projects = await projectManagementApis.fetchProjectList(appState.value.currentOrgId);

            appState.value = {
                ...appState.value,
                projectList: projects
            };
        } catch (error) {
            console.error('Failed to fetch project list:', error);
            appState.value = {
                ...appState.value,
                projectList: []
            };
            // 可以添加错误提示
            if (window.$message) {
                window.$message.error('加载项目列表失败');
            }
        } finally {
            hideLoading();
        }
    };
    const setCurrentMenuConfig = (menuConfig: string[]) => {
        appState.value = {
            ...appState.value,
            currentMenuConfig: menuConfig
        };
    }
    const setTopMenus = (menus: RouteRecordRaw[] | undefined) => {
        appState.value = {
            ...appState.value,
            topMenus: menus ? [...menus] : []
        };
    }
    const setCurrentTopMenu = (menu: RouteRecordRaw) => {
        appState.value = {
            ...appState.value,
            currentTopMenu: cloneDeep(menu)
        };
    }
    return {
        appState,
        currentOrgId,
        currentProjectId,
        loadingStatus, getCurrentTopMenu, getTopMenus,
        setCurrentOrgId,
        setCurrentProjectId,
        showLoading,
        hideLoading, initProjectList, setCurrentMenuConfig, setTopMenus, setCurrentTopMenu
    }
}, {
    persist: {
        pick: ['appState.currentOrgId', 'appState.currentProjectId', 'appState.menuCollapse']
    }
})
export default useAppStore