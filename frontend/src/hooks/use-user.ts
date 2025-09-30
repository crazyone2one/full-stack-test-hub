import router from "/@/router";
import {useUserStore} from "/@/store";

const whiteRoutes: string[] = ['/login', '/404', '/403', '/500']
export default function useUser() {
    const isWhiteListPage = () => {
        const currentRoute = router.currentRoute.value;
        console.log(whiteRoutes.includes(currentRoute.path))
        return whiteRoutes.includes(currentRoute.path);
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