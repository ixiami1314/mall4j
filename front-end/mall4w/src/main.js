import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from '@/router'
import VueCookies from 'vue-cookies'
import i18n from '@/locales'

import '@/assets/styles/index.scss'

const app = createApp(App)

app.use(router)
const pinia = createPinia()
app.use(pinia)
app.use(i18n)

app.use(ElementPlus)
app.use(VueCookies, { expireTimes: '7d' })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
