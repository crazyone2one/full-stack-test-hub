import router from "/@/router";
import {WHITE_LIST} from "/@/router/constants.ts";
import {useUserStore} from "/@/store";

export default function useUser() {
    const isWhiteListPage = () => {
        const currentRoute = router.currentRoute.value;
        return WHITE_LIST.some((e) => e.path.includes(currentRoute.path));
    };
    const logout = async (logoutTo?: string, noRedirect?: boolean) => {
        const userStore = useUserStore();
        await userStore.logout();
        const currentRoute = router.currentRoute.value;
        await router.push({
            name: logoutTo && typeof logoutTo === 'string' ? logoutTo : 'login',
            query: noRedirect ? {} : {
                ...router.currentRoute.value.query,
                redirect: currentRoute.name as string,
            }
        })
    }
    return {isWhiteListPage, logout};
};