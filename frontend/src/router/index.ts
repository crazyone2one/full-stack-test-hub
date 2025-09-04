import routes from './routes'
import {createRouter, createWebHashHistory} from "vue-router";
import createRouteGuard from "/@/router/guard";

const history = createWebHashHistory()

const router = createRouter({history, routes})
createRouteGuard(router);
router.afterEach(to => {
    const items = [import.meta.env.VITE_APP_TITLE]
    to.meta.title != null && items.unshift(to.meta.title as string)
    document.title = items.join(' | ')
})

export default router