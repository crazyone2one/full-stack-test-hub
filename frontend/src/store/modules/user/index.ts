import {defineStore} from "pinia";
import type {UserState} from "/@/store/modules/user/types.ts";
import {useAppStore} from "/@/store";
import {authApi} from "/@/api/modules/auth.ts";
import {clearToken} from "/@/utils/auth.ts";
import {removeRouteListener} from "/@/utils/route-listener.ts";
import {composePermissions} from "/@/utils/permissions.ts";

const useUserStore = defineStore('user', {
    state: (): UserState => ({
        id: '',
        name: '',
        lastProjectId: '',
        avatar: undefined,
        lastOrganizationId: '',
        email: '',
        phone: '',
        userRolePermissions: []
    }),
    getters: {
        userInfo(state: UserState): UserState {
            return {...state};
        },
        isAdmin(state: UserState): boolean {
            if (!state.userRolePermissions) return false;
            return state.userRolePermissions.findIndex((ur) => ur.userRole.id === 'admin') > -1;
        },
        currentRole(state: UserState): {
            projectPermissions: string[];
            orgPermissions: string[];
            systemPermissions: string[];
        } {
            const appStore = useAppStore();

            state.userRoleRelations?.forEach((ug) => {
                state.userRolePermissions?.forEach((gp) => {
                    if (gp.userRole.id === ug.roleId) {
                        ug.userRolePermissions = gp.userRolePermissions;
                        ug.userRole = gp.userRole;
                    }
                });
            });

            return {
                projectPermissions: composePermissions(state.userRoleRelations || [], 'PROJECT', appStore.currentProjectId),
                orgPermissions: composePermissions(state.userRoleRelations || [], 'ORGANIZATION', appStore.currentOrgId),
                systemPermissions: composePermissions(state.userRoleRelations || [], 'SYSTEM', 'global'),
            };
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