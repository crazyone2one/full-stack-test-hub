import {createRouter, createWebHashHistory} from "vue-router";
import createRouteGuard from "/@/router/guard";
import appRoutes from "/@/router/routes";

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            redirect: 'workstation',
        },
        {
            name: 'login',
            path: '/login',
            component: () => import('/@/views/login/index.vue'),
            meta: {
                locale: 'Sign In',
                requiresAuth: false,
            }
        },
        ...appRoutes,
        {
            name: 'not-found',
            path: '/:path*',
            component: () => import('/@/views/error/NotFound.vue'),
            meta: {
                locale: 'Oh no!'
            }
        }
    ],
})
createRouteGuard(router);
router.afterEach(to => {
    const items = [import.meta.env.VITE_APP_TITLE]
    to.meta.title != null && items.unshift(to.meta.locale as string)
    document.title = items.join(' | ')
})

export default router