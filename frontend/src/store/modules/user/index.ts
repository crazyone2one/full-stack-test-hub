import {defineStore} from "pinia";
import type {UserState} from "/@/store/modules/user/types.ts";

const useUserStore = defineStore('user', {
    state: (): UserState => ({
        id: undefined,
        name: undefined,
        lastProjectId: undefined,
        avatar: undefined,
        lastOrganizationId: undefined,
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
    },
    persist: true
},);
export default useUserStore;