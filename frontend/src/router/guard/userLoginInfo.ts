import type {LocationQueryRaw, Router} from 'vue-router';
import {hasToken} from "/@/utils/auth.ts";
import useUser from "/@/hooks/use-user.ts";

export default function setupUserLoginInfoGuard(router: Router) {
    router.beforeEach(async (to, _from, next) => {
        const {isWhiteListPage} = useUser();
        if (to.name !== 'login' && hasToken(to.name as string)) {
            next();
        } else {
            if (to.name === 'login' || isWhiteListPage()) {
                next();
                return;
            }
            next({
                name: 'login',
                query: {
                    redirect: to.name,
                    ...to.query,
                } as LocationQueryRaw,
            });
            // await sleep(0);
        }
    });
};