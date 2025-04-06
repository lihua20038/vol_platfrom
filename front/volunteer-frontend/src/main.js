import {
    createApp
} from 'vue'
import {
    createPinia
} from 'pinia'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import locale from 'element-plus/dist/locale/zh-cn.min.mjs'
import 'element-plus/dist/index.css'
import './assets/css/global.css'
import {
    createPersistedState
} from 'pinia-persistedstate-plugin'

const app = createApp(App)
const pinia = createPinia()
const persist = createPersistedState()
pinia.use(persist)

app.use(router)
app.use(ElementPlus, {
    locale
})
app.use(pinia)
app.mount('#app')

