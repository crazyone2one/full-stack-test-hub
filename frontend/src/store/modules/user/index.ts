import {defineStore} from "pinia";
import type {UserState} from "/@/store/modules/user/types.ts";
import {useAppStore} from "/@/store";
import {authApi} from "/@/api/modules/auth.ts";
import {clearToken} from "/@/utils/auth.ts";
import {removeRouteListener} from "/@/utils/route-listener.ts";

const useUserStore = defineStore('user', {
    state: (): UserState => ({
        id: '',
        name: '',
        lastProjectId: '',
        avatar: undefined,
        lastOrganizationId: '',
        email: ''
    }),
    getters: {
        userInfo(state: UserState): UserState {
            return {...state};
        },
    },
    actions: {
        // 设置用户信息
        setInfo(partial: Partial<UserState>) {
            this.$patch(partial);
        },
        // 重置用户信息
        resetInfo() {
            this.$reset();
        },
        async logout(silence = false) {
            const appStore = useAppStore();
            if (!silence) {
                appStore.showLoading('正在退出登录...');
            }
            await authApi.logout()
            this.resetInfo();
            clearToken()
            appStore.hideLoading()
            removeRouteListener()
        }
    },
    persist: true
},);
export default useUserStore;