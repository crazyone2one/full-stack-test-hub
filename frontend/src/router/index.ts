import routes from './routes'
import {createRouter, createWebHistory} from "vue-router";

const history = createWebHistory()

const router = createRouter({history, routes})

router.afterEach(to => {
    const items = [import.meta.env.VITE_APP_TITLE]
    to.meta.title != null && items.unshift(to.meta.title as string)
    document.title = items.join(' | ')
})

export default router