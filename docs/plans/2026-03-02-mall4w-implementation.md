# mall4w 商城 Web 端实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 创建一个完整的 PC 端商城前台，提供商品浏览、搜索、购物车、下单、订单管理等完整的购物体验

**Architecture:** 采用 Vue 3 + Vite + Element Plus + Pinia 技术栈，与 mall4v 后台管理端保持一致。项目结构按功能模块划分，API 层与后端服务对接，状态管理使用 Pinia 管理用户、购物车、应用全局状态。

**Tech Stack:** Vue 3.2.47, Vite 4.3.9, Element Plus 2.3.6, Pinia 2.0.33, Vue Router 4.1.6, Axios 1.3.4, Sass 1.59.3

---

## Phase 1: 项目初始化

### Task 1: 创建项目基础结构

**Files:**
- Create: `front-end/mall4w/package.json`
- Create: `front-end/mall4w/vite.config.js`
- Create: `front-end/mall4w/index.html`
- Create: `front-end/mall4w/.eslintrc.cjs`

**Step 1: 创建 package.json**

```json
{
  "name": "mall4w",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "preinstall": "npx only-allow pnpm",
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint --ext .js,.vue src",
    "lint:fix": "eslint --fix --ext .js,.vue src"
  },
  "engines": {
    "node": ">=16",
    "pnpm": ">=6"
  },
  "dependencies": {
    "@element-plus/icons-vue": "2.1.0",
    "axios": "1.3.4",
    "big.js": "6.2.1",
    "crypto-js": "4.1.1",
    "element-plus": "2.3.6",
    "js-base64": "3.7.5",
    "lodash": "4.17.21",
    "pinia": "2.0.33",
    "qs": "6.11.1",
    "vue": "3.2.47",
    "vue-cookies": "1.8.3",
    "vue-router": "4.1.6"
  },
  "devDependencies": {
    "@babel/eslint-parser": "7.21.3",
    "@vitejs/plugin-vue": "4.1.0",
    "eslint": "8.38.0",
    "eslint-config-standard": "17.0.0",
    "eslint-plugin-import": "2.27.5",
    "eslint-plugin-n": "15.7.0",
    "eslint-plugin-promise": "6.1.1",
    "eslint-plugin-vue": "9.10.0",
    "sass": "1.59.3",
    "vite": "4.3.9"
  }
}
```

**Step 2: 创建 vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: true,
    port: 9528,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  build: {
    base: './',
    rollupOptions: {
      output: {
        chunkFileNames: 'static/js/[name]-[hash].js',
        entryFileNames: 'static/js/[name]-[hash].js',
        assetFileNames: 'static/[ext]/[name]-[hash].[ext]'
      }
    },
    sourcemap: false,
    target: 'es2015'
  }
})
```

**Step 3: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8">
    <link rel="icon" href="/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>mall4w 商城</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

**Step 4: 创建 .eslintrc.cjs**

```javascript
module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-essential',
    'standard'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    parser: '@babel/eslint-parser'
  },
  plugins: ['vue'],
  rules: {
    'vue/multi-word-component-names': 'off',
    'no-unused-vars': 'warn'
  }
}
```

**Step 5: 安装依赖**

Run: `cd front-end/mall4w && pnpm install`
Expected: 依赖安装成功

**Step 6: Commit**

```bash
git add front-end/mall4w/package.json front-end/mall4w/vite.config.js front-end/mall4w/index.html front-end/mall4w/.eslintrc.cjs
git commit -m "feat(mall4w): 初始化项目配置

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

### Task 2: 创建入口文件和基础组件

**Files:**
- Create: `front-end/mall4w/src/main.js`
- Create: `front-end/mall4w/src/App.vue`

**Step 1: 创建 main.js**

```javascript
import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from '@/router'

import '@/assets/styles/index.scss'

const app = createApp(App)

app.use(router)
const pinia = createPinia()
app.use(pinia)

app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
```

**Step 2: 创建 App.vue**

```vue
<template>
  <el-config-provider namespace="el">
    <router-view />
  </el-config-provider>
</template>

<script setup>
</script>

<style lang="scss">
@use '@/assets/styles/index.scss';
</style>
```

**Step 3: 创建样式文件**

Create: `front-end/mall4w/src/assets/styles/index.scss`

```scss
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  color: #333;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

a {
  color: #333;
  text-decoration: none;

  &:hover {
    color: #3a86b9;
  }
}

.container {
  width: 1200px;
  margin: 0 auto;
}

.clearfix::after {
  content: '';
  display: table;
  clear: both;
}
```

**Step 4: Commit**

```bash
git add front-end/mall4w/src/
git commit -m "feat(mall4w): 添加入口文件和基础样式

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

### Task 3: 创建 HTTP 请求封装

**Files:**
- Create: `front-end/mall4w/src/utils/http.js`
- Create: `front-end/mall4w/src/utils/index.js`

**Step 1: 创建 http.js**

```javascript
import axios from 'axios'
import qs from 'qs'
import cookie from 'vue-cookies'
import { ElMessage } from 'element-plus'
import router from '@/router'

const http = axios.create({
  timeout: 1000 * 30,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json; charset=utf-8'
  }
})

// 请求拦截
http.interceptors.request.use(
  config => {
    config.headers.Authorization = cookie.get('Authorization')
    if (config.method === 'get' || config.method === 'GET') {
      config.paramsSerializer = function (params) {
        return qs.stringify(params, { arrayFormat: 'repeat' })
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截
http.interceptors.response.use(
  response => {
    if (response.request.responseType === 'blob') {
      return response
    }
    const res = response.data
    if (res.code === '00000' || res.code === 'A00002') {
      return res
    }
    if (res.code === 'A00001') {
      ElMessage({
        message: res.msg || res.data || 'Error',
        type: 'error',
        duration: 1500
      })
      return Promise.reject(res)
    }
    if (res.code === 'A00004') {
      cookie.remove('Authorization')
      router.push('/login')
    }
    if (res.code === 'A00005') {
      ElMessage({
        message: '服务器出了点小差，请稍后再试',
        type: 'error',
        duration: 1500
      })
      return Promise.reject(res)
    }
    return res
  },
  error => {
    switch (error.response?.status) {
      case 400:
        ElMessage({ message: error.response.data, type: 'error', duration: 1500 })
        break
      case 401:
        cookie.remove('Authorization')
        router.push('/login')
        break
      case 500:
        ElMessage({ message: '服务器出了点小差，请稍后再试', type: 'error', duration: 1500 })
        break
    }
    return Promise.reject(error)
  }
)

// API 地址处理
http.adornUrl = actionName => {
  return '/api' + actionName
}

// get 请求参数处理
http.adornParams = (params = {}, openDefaultParams = true) => {
  const defaults = { t: Date.now() }
  return openDefaultParams ? { ...defaults, ...params } : params
}

export default http
```

**Step 2: 创建 utils/index.js**

```javascript
import cookie from 'vue-cookies'

export const clearLoginInfo = () => {
  cookie.remove('Authorization')
  sessionStorage.clear()
}

export const isLogin = () => {
  return !!cookie.get('Authorization')
}
```

**Step 3: Commit**

```bash
git add front-end/mall4w/src/utils/
git commit -m "feat(mall4w): 添加 HTTP 请求封装

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## Phase 2: 路由和布局

### Task 4: 创建路由配置

**Files:**
- Create: `front-end/mall4w/src/router/index.js`

**Step 1: 创建路由配置**

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import cookie from 'vue-cookies'
import MainLayout from '@/layout/MainLayout.vue'

// 不需要登录的路由
const publicRoutes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', name: 'home', component: () => import('@/views/home/index.vue'), meta: { title: '首页' } },
      { path: 'category', name: 'category', component: () => import('@/views/category/index.vue'), meta: { title: '分类' } },
      { path: 'prod/:id', name: 'prod-detail', component: () => import('@/views/prod/detail.vue'), meta: { title: '商品详情' } },
      { path: 'search', name: 'search', component: () => import('@/views/search/index.vue'), meta: { title: '搜索' } },
      { path: 'search/result', name: 'search-result', component: () => import('@/views/search/result.vue'), meta: { title: '搜索结果' } },
      { path: 'news', name: 'news', component: () => import('@/views/news/list.vue'), meta: { title: '新闻' } },
      { path: 'news/:id', name: 'news-detail', component: () => import('@/views/news/detail.vue'), meta: { title: '新闻详情' } }
    ]
  },
  { path: '/login', name: 'login', component: () => import('@/views/auth/login.vue'), meta: { title: '登录' } },
  { path: '/register', name: 'register', component: () => import('@/views/auth/register.vue'), meta: { title: '注册' } }
]

// 需要登录的路由
const authRoutes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: 'cart', name: 'cart', component: () => import('@/views/cart/index.vue'), meta: { title: '购物车', requiresAuth: true } },
      { path: 'order/confirm', name: 'order-confirm', component: () => import('@/views/order/confirm.vue'), meta: { title: '确认订单', requiresAuth: true } },
      { path: 'order/list', name: 'order-list', component: () => import('@/views/order/list.vue'), meta: { title: '订单列表', requiresAuth: true } },
      { path: 'order/detail/:id', name: 'order-detail', component: () => import('@/views/order/detail.vue'), meta: { title: '订单详情', requiresAuth: true } },
      { path: 'order/pay-result', name: 'pay-result', component: () => import('@/views/order/pay-result.vue'), meta: { title: '支付结果', requiresAuth: true } },
      { path: 'user', name: 'user', component: () => import('@/views/user/index.vue'), meta: { title: '用户中心', requiresAuth: true } },
      { path: 'user/address', name: 'address', component: () => import('@/views/user/address.vue'), meta: { title: '地址管理', requiresAuth: true } },
      { path: 'user/collection', name: 'collection', component: () => import('@/views/user/collection.vue'), meta: { title: '我的收藏', requiresAuth: true } },
      { path: 'user/coupon', name: 'coupon', component: () => import('@/views/user/coupon.vue'), meta: { title: '我的优惠券', requiresAuth: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...publicRoutes, ...authRoutes]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - mall4w` : 'mall4w'
  if (to.meta.requiresAuth && !cookie.get('Authorization')) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/router/
git commit -m "feat(mall4w): 添加路由配置

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

### Task 5: 创建布局组件

**Files:**
- Create: `front-end/mall4w/src/layout/MainLayout.vue`
- Create: `front-end/mall4w/src/components/common/Header.vue`
- Create: `front-end/mall4w/src/components/common/Footer.vue`

**Step 1: 创建 MainLayout.vue**

```vue
<template>
  <div class="main-layout">
    <Header />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/common/Header.vue'
import Footer from '@/components/common/Footer.vue'
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  padding: 20px 0;
}
</style>
```

**Step 2: 创建 Header.vue**

```vue
<template>
  <header class="header">
    <div class="container header-content">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-text">mall4w</span>
      </div>

      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <nav class="nav-menu">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/category" class="nav-item">分类</router-link>
      </nav>

      <div class="user-area">
        <template v-if="isLogin">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.nickName || '用户' }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="user">用户中心</el-dropdown-item>
                <el-dropdown-item command="order">我的订单</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="auth-link">登录</router-link>
          <router-link to="/register" class="auth-link">注册</router-link>
        </template>

        <router-link to="/cart" class="cart-link">
          <el-badge :value="cartCount" :hidden="cartCount === 0">
            <el-icon :size="20"><ShoppingCart /></el-icon>
          </el-badge>
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search, User, ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { isLogin, clearLoginInfo } from '@/utils'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchKeyword = ref('')

const cartCount = computed(() => cartStore.totalCount)

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search/result', query: { keyword: searchKeyword.value } })
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    clearLoginInfo()
    userStore.reset()
    cartStore.reset()
    router.push('/')
  } else if (command === 'user') {
    router.push('/user')
  } else if (command === 'order') {
    router.push('/order/list')
  }
}
</script>

<style lang="scss" scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-content {
  display: flex;
  align-items: center;
  height: 60px;
  gap: 20px;
}
.logo {
  cursor: pointer;
  .logo-text {
    font-size: 24px;
    font-weight: bold;
    color: #3a86b9;
  }
}
.search-box {
  flex: 1;
  max-width: 400px;
}
.nav-menu {
  display: flex;
  gap: 20px;
  .nav-item {
    padding: 8px 16px;
    &:hover {
      color: #3a86b9;
    }
  }
}
.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
  .user-info {
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 4px;
  }
  .auth-link {
    padding: 4px 12px;
    &:hover {
      color: #3a86b9;
    }
  }
  .cart-link {
    display: flex;
    align-items: center;
  }
}
</style>
```

**Step 3: 创建 Footer.vue**

```vue
<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <p class="copyright">© 2024 mall4w 商城. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.footer {
  background: #333;
  color: #fff;
  padding: 20px 0;
  margin-top: auto;
}
.footer-content {
  text-align: center;
}
.copyright {
  font-size: 12px;
  color: #999;
}
</style>
```

**Step 4: Commit**

```bash
git add front-end/mall4w/src/layout/ front-end/mall4w/src/components/common/
git commit -m "feat(mall4w): 添加布局组件

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## Phase 3: 状态管理

### Task 6: 创建 Pinia Stores

**Files:**
- Create: `front-end/mall4w/src/stores/user.js`
- Create: `front-end/mall4w/src/stores/cart.js`
- Create: `front-end/mall4w/src/stores/app.js`

**Step 1: 创建 user.js**

```javascript
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: '',
    nickName: '',
    pic: '',
    mobile: ''
  }),
  getters: {
    isLogin: (state) => !!state.userId
  },
  actions: {
    setUser(user) {
      this.userId = user.userId
      this.nickName = user.nickName
      this.pic = user.pic
      this.mobile = user.mobile
    },
    reset() {
      this.userId = ''
      this.nickName = ''
      this.pic = ''
      this.mobile = ''
    }
  }
})
```

**Step 2: 创建 cart.js**

```javascript
import { defineStore } from 'pinia'
import http from '@/utils/http'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    totalCount: 0
  }),
  actions: {
    async fetchCart() {
      const { data } = await http({
        url: http.adornUrl('/p/shopCart/info'),
        method: 'post',
        data: {}
      })
      this.items = data || []
      this.calculateTotal()
    },
    async fetchCartCount() {
      const { data } = await http({
        url: http.adornUrl('/p/shopCart/prodCount'),
        method: 'get'
      })
      this.totalCount = data || 0
    },
    calculateTotal() {
      let count = 0
      this.items.forEach(shop => {
        shop.shopCartItemDiscounts?.forEach(discount => {
          discount.shopCartItems?.forEach(item => {
            count += item.prodCount
          })
        })
      })
      this.totalCount = count
    },
    reset() {
      this.items = []
      this.totalCount = 0
    }
  }
})
```

**Step 3: 创建 app.js**

```javascript
import { defineStore } from 'pinia'
import http from '@/utils/http'

export const useAppStore = defineStore('app', {
  state: () => ({
    categoryList: [],
    searchHistory: []
  }),
  actions: {
    async fetchCategoryList() {
      const { data } = await http({
        url: http.adornUrl('/category/categoryInfo'),
        method: 'get'
      })
      this.categoryList = data || []
    },
    addSearchHistory(keyword) {
      if (!keyword) return
      const index = this.searchHistory.indexOf(keyword)
      if (index > -1) {
        this.searchHistory.splice(index, 1)
      }
      this.searchHistory.unshift(keyword)
      if (this.searchHistory.length > 10) {
        this.searchHistory.pop()
      }
      localStorage.setItem('searchHistory', JSON.stringify(this.searchHistory))
    },
    loadSearchHistory() {
      const history = localStorage.getItem('searchHistory')
      if (history) {
        this.searchHistory = JSON.parse(history)
      }
    }
  }
})
```

**Step 4: Commit**

```bash
git add front-end/mall4w/src/stores/
git commit -m "feat(mall4w): 添加状态管理

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## Phase 4: API 接口

### Task 7: 创建 API 接口模块

**Files:**
- Create: `front-end/mall4w/src/api/index.js` (首页)
- Create: `front-end/mall4w/src/api/prod.js` (商品)
- Create: `front-end/mall4w/src/api/user.js` (用户)
- Create: `front-end/mall4w/src/api/cart.js` (购物车)
- Create: `front-end/mall4w/src/api/order.js` (订单)
- Create: `front-end/mall4w/src/api/address.js` (地址)
- Create: `front-end/mall4w/src/api/search.js` (搜索)

**Step 1: 创建 api/index.js (首页)**

```javascript
import http from '@/utils/http'

// 首页轮播图
export function getIndexImgs() {
  return http({
    url: http.adornUrl('/indexImgs'),
    method: 'get'
  })
}

// 首页标签商品
export function getTagProdList() {
  return http({
    url: http.adornUrl('/prod/tagProdList'),
    method: 'get'
  })
}

// 新闻列表
export function getNoticeList(page) {
  return http({
    url: http.adornUrl('/shop/notice/noticeList'),
    method: 'get',
    params: http.adornParams(page)
  })
}
```

**Step 2: 创建 api/prod.js**

```javascript
import http from '@/utils/http'

// 商品详情
export function getProdInfo(prodId) {
  return http({
    url: http.adornUrl('/prod/prodInfo'),
    method: 'get',
    params: { prodId }
  })
}

// 分类商品列表
export function getProdPage(categoryId, page = {}) {
  return http({
    url: http.adornUrl('/prod/pageProd'),
    method: 'get',
    params: http.adornParams({ categoryId, ...page })
  })
}

// 标签商品列表
export function getProdListByTagId(tagId, page = {}) {
  return http({
    url: http.adornUrl('/prod/prodListByTagId'),
    method: 'get',
    params: http.adornParams({ tagId, ...page })
  })
}

// 新品推荐
export function getLastedProdPage(page = {}) {
  return http({
    url: http.adornUrl('/prod/lastedProdPage'),
    method: 'get',
    params: http.adornParams(page)
  })
}

// 每日疯抢
export function getMoreBuyProdList(page = {}) {
  return http({
    url: http.adornUrl('/prod/moreBuyProdList'),
    method: 'get',
    params: http.adornParams(page)
  })
}

// 分类信息
export function getCategoryInfo() {
  return http({
    url: http.adornUrl('/category/categoryInfo'),
    method: 'get'
  })
}

// 商品评论
export function getProdCommPage(prodId, page = {}) {
  return http({
    url: http.adornUrl('/prodComm/prodCommPage'),
    method: 'get',
    params: http.adornParams({ prodId, ...page })
  })
}
```

**Step 3: 创建 api/user.js**

```javascript
import http from '@/utils/http'

// 登录
export function login(data) {
  return http({
    url: http.adornUrl('/login'),
    method: 'post',
    data
  })
}

// 注册
export function register(data) {
  return http({
    url: http.adornUrl('/userInfo/registerOrBindUser'),
    method: 'post',
    data
  })
}

// 用户信息
export function getUserInfo() {
  return http({
    url: http.adornUrl('/p/user/userInfo'),
    method: 'get'
  })
}

// 修改用户信息
export function setUserInfo(data) {
  return http({
    url: http.adornUrl('/p/user/setUserInfo'),
    method: 'put',
    data
  })
}

// 发送验证码
export function sendSms(mobile) {
  return http({
    url: http.adornUrl('/p/sms/send'),
    method: 'post',
    data: { mobile }
  })
}
```

**Step 4: 创建 api/cart.js**

```javascript
import http from '@/utils/http'

// 获取购物车信息
export function getCartInfo(data = {}) {
  return http({
    url: http.adornUrl('/p/shopCart/info'),
    method: 'post',
    data
  })
}

// 获取购物车数量
export function getCartCount() {
  return http({
    url: http.adornUrl('/p/shopCart/prodCount'),
    method: 'get'
  })
}

// 添加/修改购物车
export function changeCartItem(data) {
  return http({
    url: http.adornUrl('/p/shopCart/changeItem'),
    method: 'post',
    data
  })
}

// 删除购物车商品
export function deleteCartItem(basketIds) {
  return http({
    url: http.adornUrl('/p/shopCart/deleteItem'),
    method: 'delete',
    data: basketIds
  })
}

// 清空购物车
export function clearCart() {
  return http({
    url: http.adornUrl('/p/shopCart/deleteAll'),
    method: 'delete'
  })
}

// 获取选中商品总价
export function getTotalPay(basketIds) {
  return http({
    url: http.adornUrl('/p/shopCart/totalPay'),
    method: 'post',
    data: basketIds
  })
}
```

**Step 5: 创建 api/order.js**

```javascript
import http from '@/utils/http'

// 订单确认
export function confirmOrder(data) {
  return http({
    url: http.adornUrl('/p/order/confirm'),
    method: 'post',
    data
  })
}

// 提交订单
export function submitOrder(data) {
  return http({
    url: http.adornUrl('/p/order/submit'),
    method: 'post',
    data
  })
}

// 订单列表
export function getOrderList(params) {
  return http({
    url: http.adornUrl('/p/myOrder/myOrder'),
    method: 'get',
    params: http.adornParams(params)
  })
}

// 订单详情
export function getOrderDetail(orderNumber) {
  return http({
    url: http.adornUrl('/p/myOrder/orderDetail'),
    method: 'get',
    params: { orderNumber }
  })
}

// 取消订单
export function cancelOrder(orderNumber) {
  return http({
    url: http.adornUrl('/p/myOrder/cancelOrder'),
    method: 'put',
    params: { orderNumber }
  })
}

// 确认收货
export function receiptOrder(orderNumber) {
  return http({
    url: http.adornUrl('/p/myOrder/receiptOrder'),
    method: 'put',
    params: { orderNumber }
  })
}

// 支付
export function pay(orderNumbers) {
  return http({
    url: http.adornUrl('/p/order/pay'),
    method: 'post',
    params: { orderNumbers }
  })
}
```

**Step 6: 创建 api/address.js**

```javascript
import http from '@/utils/http'

// 地址列表
export function getAddressList() {
  return http({
    url: http.adornUrl('/p/address/list'),
    method: 'get'
  })
}

// 地址详情
export function getAddressDetail(addrId) {
  return http({
    url: http.adornUrl('/p/address/addrInfo'),
    method: 'get',
    params: { addrId }
  })
}

// 添加地址
export function addAddress(data) {
  return http({
    url: http.adornUrl('/p/address/addAddr'),
    method: 'post',
    data
  })
}

// 修改地址
export function updateAddress(data) {
  return http({
    url: http.adornUrl('/p/address/updateAddr'),
    method: 'put',
    data
  })
}

// 删除地址
export function deleteAddress(addrId) {
  return http({
    url: http.adornUrl('/p/address/deleteAddr'),
    method: 'delete',
    params: { addrId }
  })
}

// 设为默认
export function setDefaultAddress(addrId) {
  return http({
    url: http.adornUrl('/p/address/defaultAddr'),
    method: 'put',
    params: { addrId }
  })
}
```

**Step 7: 创建 api/search.js**

```javascript
import http from '@/utils/http'

// 搜索商品
export function searchProd(params) {
  return http({
    url: http.adornUrl('/search/searchProdPage'),
    method: 'get',
    params: http.adornParams(params)
  })
}

// 热门搜索
export function getHotSearch() {
  return http({
    url: http.adornUrl('/search/hotSearch'),
    method: 'get'
  })
}
```

**Step 8: Commit**

```bash
git add front-end/mall4w/src/api/
git commit -m "feat(mall4w): 添加 API 接口模块

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## Phase 5: 页面开发

### Task 8: 创建首页

**Files:**
- Create: `front-end/mall4w/src/views/home/index.vue`

**Step 1: 创建首页组件**

```vue
<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="banner-section">
      <div class="container">
        <el-carousel height="400px" :interval="5000">
          <el-carousel-item v-for="item in bannerList" :key="item.imgUrl">
            <img :src="item.imgUrl" class="banner-img" @click="handleBannerClick(item)" />
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 分类入口 -->
    <section class="category-section">
      <div class="container">
        <div class="category-list">
          <div v-for="cat in categoryList.slice(0, 8)" :key="cat.categoryId" class="category-item" @click="goCategory(cat.categoryId)">
            <img :src="cat.pic" class="category-img" />
            <span>{{ cat.categoryName }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品列表 -->
    <section v-for="tag in tagProdList" :key="tag.id" class="prod-section">
      <div class="container">
        <div class="section-header">
          <h2>{{ tag.title }}</h2>
          <router-link to="/category" class="more-link">查看更多 ></router-link>
        </div>
        <div class="prod-grid">
          <div v-for="prod in tag.prods" :key="prod.prodId" class="prod-card" @click="goProdDetail(prod.prodId)">
            <img :src="prod.pic" class="prod-img" />
            <div class="prod-info">
              <p class="prod-name">{{ prod.prodName }}</p>
              <p class="prod-price">¥{{ prod.price }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getIndexImgs, getTagProdList } from '@/api/index'
import { getCategoryInfo } from '@/api/prod'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const appStore = useAppStore()

const bannerList = ref([])
const categoryList = ref([])
const tagProdList = ref([])

onMounted(async () => {
  await Promise.all([
    fetchBanners(),
    fetchCategories(),
    fetchTagProds()
  ])
})

const fetchBanners = async () => {
  const { data } = await getIndexImgs()
  bannerList.value = data || []
}

const fetchCategories = async () => {
  const { data } = await getCategoryInfo()
  categoryList.value = data || []
  appStore.categoryList = data || []
}

const fetchTagProds = async () => {
  const { data } = await getTagProdList()
  tagProdList.value = data || []
}

const handleBannerClick = (item) => {
  if (item.relation) {
    router.push(`/prod/${item.relation}`)
  }
}

const goCategory = (categoryId) => {
  router.push({ path: '/category', query: { id: categoryId } })
}

const goProdDetail = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
.home-page {
  background: #f5f5f5;
}
.banner-section {
  margin-bottom: 20px;
  .banner-img {
    width: 100%;
    height: 400px;
    object-fit: cover;
    cursor: pointer;
  }
}
.category-section {
  background: #fff;
  padding: 30px 0;
  margin-bottom: 20px;
}
.category-list {
  display: flex;
  justify-content: space-between;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  &:hover {
    color: #3a86b9;
  }
  .category-img {
    width: 60px;
    height: 60px;
    margin-bottom: 10px;
  }
}
.prod-section {
  background: #fff;
  padding: 30px 0;
  margin-bottom: 20px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 {
    font-size: 20px;
    color: #333;
  }
  .more-link {
    color: #999;
    &:hover {
      color: #3a86b9;
    }
  }
}
.prod-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.prod-card {
  background: #fff;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  .prod-img {
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  .prod-info {
    padding: 10px;
  }
  .prod-name {
    font-size: 14px;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .prod-price {
    font-size: 18px;
    color: #e4393c;
    margin-top: 8px;
  }
}
</style>
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/views/home/
git commit -m "feat(mall4w): 添加首页

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

### Task 9: 创建登录页面

**Files:**
- Create: `front-end/mall4w/src/views/auth/login.vue`

**Step 1: 创建登录页面**

```vue
<template>
  <div class="login-page">
    <div class="login-box">
      <h2>用户登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="principal">
          <el-input v-model="form.principal" placeholder="请输入手机号/用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="credentials">
          <el-input
            v-model="form.credentials"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <router-link to="/register">还没有账号？立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import cookie from 'vue-cookies'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  principal: '',
  credentials: ''
})

const rules = {
  principal: [{ required: true, message: '请输入手机号/用户名', trigger: 'blur' }],
  credentials: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const { data } = await login(form)
    cookie.set('Authorization', data.accessToken, '7d')
    userStore.setUser(data)
    await cartStore.fetchCartCount()
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: calc(100vh - 100px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  h2 {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
  }
  .login-btn {
    width: 100%;
    height: 42px;
    font-size: 16px;
  }
  .login-footer {
    text-align: center;
    margin-top: 20px;
    a {
      color: #3a86b9;
    }
  }
}
</style>
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/views/auth/
git commit -m "feat(mall4w): 添加登录页面

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

### Task 10: 创建购物车页面

**Files:**
- Create: `front-end/mall4w/src/views/cart/index.vue`

**Step 1: 创建购物车页面**

```vue
<template>
  <div class="cart-page container">
    <h2>我的购物车</h2>
    <div v-if="cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="$router.push('/')">去购物</el-button>
      </el-empty>
    </div>
    <div v-else class="cart-content">
      <div v-for="shop in cartItems" :key="shop.shopId" class="shop-card">
        <div class="shop-header">
          <el-checkbox v-model="shop.checked" @change="handleShopCheck(shop)" />
          <span class="shop-name">{{ shop.shopName }}</span>
        </div>
        <div v-for="discount in shop.shopCartItemDiscounts" :key="discount.discountId">
          <div v-for="item in discount.shopCartItems" :key="item.basketId" class="cart-item">
            <el-checkbox v-model="item.checked" @change="handleItemCheck" />
            <img :src="item.pic" class="item-img" @click="goProd(item.prodId)" />
            <div class="item-info">
              <p class="item-name" @click="goProd(item.prodId)">{{ item.prodName }}</p>
              <p class="item-sku">{{ item.skuName }}</p>
            </div>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-count">
              <el-input-number v-model="item.prodCount" :min="1" :max="item.stocks" @change="handleCountChange(item)" />
            </div>
            <div class="item-total">¥{{ (item.price * item.prodCount).toFixed(2) }}</div>
            <el-button type="danger" link @click="handleDelete(item.basketId)">删除</el-button>
          </div>
        </div>
      </div>

      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="allChecked" @change="handleAllCheck">全选</el-checkbox>
          <el-button type="danger" link @click="handleDeleteSelected">删除选中</el-button>
        </div>
        <div class="footer-right">
          <span>已选 {{ selectedCount }} 件商品</span>
          <span class="total-price">合计: <em>¥{{ totalPrice }}</em></span>
          <el-button type="primary" :disabled="selectedCount === 0" @click="handleCheckout">
            去结算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartInfo, changeCartItem, deleteCartItem } from '@/api/cart'
import { ElMessageBox } from 'element-plus'

const router = useRouter()

const cartItems = ref([])
const allChecked = ref(false)

const selectedCount = computed(() => {
  let count = 0
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) count += item.prodCount
      })
    })
  })
  return count
})

const totalPrice = computed(() => {
  let total = 0
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) total += item.price * item.prodCount
      })
    })
  })
  return total.toFixed(2)
})

onMounted(() => {
  fetchCart()
})

const fetchCart = async () => {
  const { data } = await getCartInfo()
  cartItems.value = (data || []).map(shop => ({
    ...shop,
    checked: false,
    shopCartItemDiscounts: shop.shopCartItemDiscounts?.map(discount => ({
      ...discount,
      shopCartItems: discount.shopCartItems?.map(item => ({ ...item, checked: false }))
    }))
  }))
}

const handleShopCheck = (shop) => {
  shop.shopCartItemDiscounts?.forEach(discount => {
    discount.shopCartItems?.forEach(item => {
      item.checked = shop.checked
    })
  })
  updateAllChecked()
}

const handleItemCheck = () => {
  updateAllChecked()
}

const updateAllChecked = () => {
  let allItems = []
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      allItems = allItems.concat(discount.shopCartItems || [])
    })
  })
  allChecked.value = allItems.length > 0 && allItems.every(item => item.checked)
}

const handleAllCheck = (val) => {
  cartItems.value.forEach(shop => {
    shop.checked = val
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        item.checked = val
      })
    })
  })
}

const handleCountChange = async (item) => {
  await changeCartItem({
    count: item.prodCount,
    prodId: item.prodId,
    skuId: item.skuId,
    shopId: item.shopId
  })
}

const handleDelete = async (basketId) => {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示')
  await deleteCartItem([basketId])
  fetchCart()
}

const handleDeleteSelected = async () => {
  const ids = []
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) ids.push(item.basketId)
      })
    })
  })
  if (ids.length === 0) return
  await ElMessageBox.confirm('确定删除选中商品吗？', '提示')
  await deleteCartItem(ids)
  fetchCart()
}

const handleCheckout = () => {
  const ids = []
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) ids.push(item.basketId)
      })
    })
  })
  router.push({ path: '/order/confirm', query: { basketIds: ids.join(',') } })
}

const goProd = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
.cart-page {
  background: #fff;
  padding: 20px;
  min-height: 500px;
  h2 {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}
.shop-card {
  margin-bottom: 20px;
  border: 1px solid #eee;
}
.shop-header {
  padding: 10px 15px;
  background: #f5f5f5;
  .shop-name {
    margin-left: 10px;
    font-weight: bold;
  }
}
.cart-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  .item-img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    margin: 0 15px;
    cursor: pointer;
  }
  .item-info {
    flex: 1;
    .item-name {
      cursor: pointer;
      &:hover {
        color: #3a86b9;
      }
    }
    .item-sku {
      color: #999;
      font-size: 12px;
      margin-top: 5px;
    }
  }
  .item-price {
    width: 100px;
    text-align: center;
  }
  .item-count {
    width: 150px;
    text-align: center;
  }
  .item-total {
    width: 100px;
    text-align: center;
    color: #e4393c;
    font-weight: bold;
  }
}
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f5f5;
  .footer-right {
    display: flex;
    align-items: center;
    gap: 20px;
    .total-price em {
      font-size: 24px;
      color: #e4393c;
      font-style: normal;
    }
  }
}
</style>
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/views/cart/
git commit -m "feat(mall4w): 添加购物车页面

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

### Task 11-15: 创建其他核心页面

由于篇幅限制，以下页面遵循相同模式创建：

- **Task 11**: 商品详情页 `views/prod/detail.vue`
- **Task 12**: 分类页 `views/category/index.vue`
- **Task 13**: 订单确认页 `views/order/confirm.vue`
- **Task 14**: 订单列表页 `views/order/list.vue`
- **Task 15**: 用户中心页 `views/user/index.vue`

每个页面遵循相同的开发流程：
1. 创建 Vue 组件
2. 实现数据获取逻辑
3. 实现交互逻辑
4. 编写样式
5. 提交代码

---

## Phase 6: 测试和优化

### Task 16: 验证项目可运行

**Step 1: 启动开发服务器**

Run: `cd front-end/mall4w && pnpm dev`
Expected: 服务启动在 http://localhost:9528

**Step 2: 验证页面功能**
- 访问首页，检查轮播图和商品列表
- 测试登录功能
- 测试购物车功能
- 测试订单流程

**Step 3: Commit**

```bash
git add .
git commit -m "feat(mall4w): 完成商城 Web 端开发

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 任务汇总

| Phase | Task | 描述 | 预计时间 |
|-------|------|------|----------|
| 1 | 1 | 项目基础结构 | 15min |
| 1 | 2 | 入口文件和基础组件 | 10min |
| 1 | 3 | HTTP 请求封装 | 10min |
| 2 | 4 | 路由配置 | 10min |
| 2 | 5 | 布局组件 | 15min |
| 3 | 6 | 状态管理 | 10min |
| 4 | 7 | API 接口模块 | 15min |
| 5 | 8 | 首页 | 20min |
| 5 | 9 | 登录页面 | 10min |
| 5 | 10 | 购物车页面 | 20min |
| 5 | 11 | 商品详情页 | 20min |
| 5 | 12 | 分类页 | 15min |
| 5 | 13 | 订单确认页 | 15min |
| 5 | 14 | 订单列表页 | 15min |
| 5 | 15 | 用户中心页 | 15min |
| 6 | 16 | 测试和优化 | 20min |

**总计: 约 4-5 小时**
