import {createApp} from 'vue'
// import './style.css'
import 'virtual:uno.css'
import App from './App.vue'
import router from './router'
import store from './store'
import directive from './directive';

const app = createApp(App)
app.use(router)
app.use(store)
app.use(directive);
app.mount('#app')
