import type {RouteRecordRaw} from "vue-router";

const mainRoutes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'dashboard',
        component: () => import('/@/views/dashboard/index.vue'),
        meta: {
            title: 'Dashboard'
        }
    },
    {
        path: '/user',
        name: 'users',
        component: () => import('/@/views/user/index.vue'),
        meta: {
            title: 'Users'
        }
    },
    {
        path: '/cases',
        name: 'cases',
        component: () => import('/@/views/case-management/index.vue'),
        meta: {
            title: 'Cases'
        }
    },
    {
        path: '/plan',
        name: 'plan',
        component: () => import('/@/views/plan/index.vue'),
        meta: {
            title: 'Plan'
        }
    }
]
const routes: RouteRecordRaw[] = [
    {
        name: 'login',
        path: '/login',
        component: () => import('/@/views/login/index.vue'),
        meta: {
            title: 'Sign In'
        }
    },
    {
        name: 'layout',
        path: '/',
        component: () => import('/@/layout/index.vue'),
        children: [...mainRoutes]
    },
    {
        name: 'not-found',
        path: '/:path*',
        component: () => import('/@/views/error/NotFound.vue'),
        meta: {
            title: 'Oh no!'
        }
    }
]
export default routes