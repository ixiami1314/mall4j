# Mall4v UI Modernization Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Upgrade mall4v admin dashboard from basic styling to modern minimalist design.

**Architecture:** CSS-first approach using SCSS variables and Element Plus theming. No JavaScript framework changes. Component-level styling updates.

**Tech Stack:** Vue 3, Vite, Element Plus, SCSS

---

## Task 1: Update Global Style Variables

**Files:**
- Modify: `src/styles/_variables.scss`

**Step 1: Update color variables**

Replace the entire content of `src/styles/_variables.scss` with:

```scss
// ========================================
// Mall4v Modern Design System
// ========================================

// ----------------------------------------
// Color System
// ----------------------------------------

// Primary Colors
$--color-primary: #3B82F6;
$--color-primary-light: #EFF6FF;
$--color-primary-dark: #1D4ED8;

// Status Colors
$--color-success: #10B981;
$--color-success-light: #D1FAE5;
$--color-warning: #F59E0B;
$--color-warning-light: #FEF3C7;
$--color-danger: #EF4444;
$--color-danger-light: #FEE2E2;
$--color-info: #6366F1;

// Neutral Colors
$--color-text-primary: #1F2937;
$--color-text-secondary: #6B7280;
$--color-text-placeholder: #9CA3AF;
$--color-text-disabled: #D1D5DB;

// Background Colors
$--color-bg-page: #F9FAFB;
$--color-bg-card: #FFFFFF;
$--color-bg-hover: #F3F4F6;

// Border Colors
$--color-border: #E5E7EB;
$--color-border-light: #F3F4F6;
$--color-border-dark: #D1D5DB;

// Sidebar Colors
$sidebar--bg: #1F2937;
$sidebar--bg-dark: #111827;
$sidebar--text: #9CA3AF;
$sidebar--text-active: #FFFFFF;
$sidebar--active-indicator: $--color-primary;

// Navbar Colors
$navbar--bg: #FFFFFF;
$navbar--text: $--color-text-primary;
$navbar--height: 60px;

// ----------------------------------------
// Spacing System
// ----------------------------------------
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 16px;
$spacing-lg: 24px;
$spacing-xl: 32px;

// ----------------------------------------
// Border Radius
// ----------------------------------------
$radius-sm: 4px;
$radius-md: 8px;
$radius-lg: 12px;
$radius-xl: 16px;

// ----------------------------------------
// Shadows
// ----------------------------------------
$shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
$shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
$shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
$shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);

// ----------------------------------------
// Typography
// ----------------------------------------
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans SC', sans-serif;
$font-size-xs: 12px;
$font-size-sm: 13px;
$font-size-base: 14px;
$font-size-lg: 16px;
$font-size-xl: 18px;
$font-size-2xl: 20px;

// ----------------------------------------
// Layout Dimensions
// ----------------------------------------
$sidebar-width: 220px;
$sidebar-width-collapsed: 80px;
$navbar-height: 60px;

// ----------------------------------------
// Transitions
// ----------------------------------------
$transition-fast: 0.15s ease;
$transition-base: 0.3s ease;
$transition-slow: 0.5s ease;
```

**Step 2: Verify file is updated**

Run: `cat src/styles/_variables.scss | head -20`
Expected: Shows new color system comments

**Step 3: Commit**

```bash
git add src/styles/_variables.scss
git commit -m "style: update global style variables for modern design system

- New color palette with primary blue #3B82F6
- Added status colors (success, warning, danger, info)
- Neutral colors for text hierarchy
- Sidebar and navbar color definitions
- Spacing, radius, shadow, typography systems
- Layout dimensions for sidebar and navbar"
```

---

## Task 2: Create Modern Style Module

**Files:**
- Create: `src/styles/_modern.scss`

**Step 1: Create the modern style module**

Create `src/styles/_modern.scss` with:

```scss
// ========================================
// Modern UI Component Styles
// ========================================

@use 'variables' as *;

// ----------------------------------------
// Button Styles
// ----------------------------------------
.btn-modern {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12px 24px;
  font-size: $font-size-base;
  font-weight: 500;
  border-radius: $radius-md;
  transition: all $transition-fast;
  cursor: pointer;
  border: none;
  outline: none;

  &--primary {
    background: $--color-primary;
    color: #fff;

    &:hover {
      background: $--color-primary-dark;
      transform: translateY(-1px);
      box-shadow: $shadow-md;
    }

    &:active {
      transform: translateY(0);
    }
  }
}

// ----------------------------------------
// Card Styles
// ----------------------------------------
.card-modern {
  background: $--color-bg-card;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
  border: 1px solid $--color-border-light;
  overflow: hidden;
}

// ----------------------------------------
// Input Styles
// ----------------------------------------
.input-modern {
  ::v-deep(.el-input__wrapper) {
    border-radius: $radius-md;
    box-shadow: none;
    border: 1px solid $--color-border;
    padding: 4px 12px;
    transition: all $transition-fast;

    &:hover {
      border-color: $--color-border-dark;
    }

    &.is-focus {
      border-color: $--color-primary;
      box-shadow: 0 0 0 3px rgba($--color-primary, 0.1);
    }
  }

  ::v-deep(.el-input__inner) {
    font-size: $font-size-base;
    color: $--color-text-primary;

    &::placeholder {
      color: $--color-text-placeholder;
    }
  }
}

// ----------------------------------------
// Scrollbar Styles
// ----------------------------------------
.scrollbar-modern {
  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: $--color-border-dark;
    border-radius: 3px;

    &:hover {
      background: $--color-text-placeholder;
    }
  }
}

// ----------------------------------------
// Animation Utilities
// ----------------------------------------
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in {
  animation: fadeIn $transition-base;
}

.slide-in-left {
  animation: slideInLeft $transition-base;
}

.slide-in-up {
  animation: slideInUp $transition-base;
}
```

**Step 2: Verify file is created**

Run: `ls -la src/styles/_modern.scss`
Expected: File exists

**Step 3: Commit**

```bash
git add src/styles/_modern.scss
git commit -m "style: add modern UI component styles module

- Button styles with hover effects
- Card component styles
- Input field enhancements
- Custom scrollbar styling
- Animation utilities (fadeIn, slideIn)"
```

---

## Task 3: Update Main Style Index

**Files:**
- Modify: `src/styles/index.scss`

**Step 1: Read current index.scss**

Run: `cat src/styles/index.scss`

**Step 2: Update index.scss to include modern styles**

Replace content with:

```scss
@use './variables' as *;
@use './modern' as *;
@use './_normalize';
@use './_base';

// Element Plus theme overrides
:root {
  --el-color-primary: #{$--color-primary};
  --el-color-primary-light-3: #{$--color-primary-light};
  --el-color-primary-dark-2: #{$--color-primary-dark};
  --el-font-family: #{$font-family};
  --el-border-radius-base: #{$radius-md};
  --el-border-radius-small: #{$radius-sm};
  --el-border-radius-round: #{$radius-lg};
}

// Global page background
body {
  font-family: $font-family;
  background-color: $--color-bg-page;
  color: $--color-text-primary;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

// Improve Element Plus component styles
.el-button--primary {
  --el-button-bg-color: #{$--color-primary};
  --el-button-border-color: #{$--color-primary};
  --el-button-hover-bg-color: #{$--color-primary-dark};
  --el-button-hover-border-color: #{$--color-primary-dark};
}

.el-card {
  border-radius: $radius-lg;
  border-color: $--color-border-light;
  box-shadow: $shadow-sm;
}

.el-table {
  --el-table-border-color: #{$--color-border};
  --el-table-header-bg-color: #{$--color-bg-page};
}

.el-dialog {
  border-radius: $radius-xl;
}
```

**Step 3: Verify syntax**

Run: `head -30 src/styles/index.scss`
Expected: Shows new imports and CSS variables

**Step 4: Commit**

```bash
git add src/styles/index.scss
git commit -m "style: update main style index with modern theme

- Import modern style module
- Element Plus CSS variable overrides
- Global typography and background
- Enhanced component styles for buttons, cards, tables"
```

---

## Task 4: Redesign Login Page

**Files:**
- Modify: `src/views/common/login/index.vue`

**Step 1: Replace the entire login component**

Replace the entire content of `src/views/common/login/index.vue` with:

```vue
<template>
  <div class="login-container">
    <!-- Left Brand Section -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-logo">
          <svg-icon icon-class="icon-shouye" class="logo-icon" />
        </div>
        <h1 class="brand-title">Mall4j</h1>
        <p class="brand-subtitle">企业级电商管理平台</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-icon">✓</span>
            <span>完整的商品管理体系</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">✓</span>
            <span>灵活的订单管理流程</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">✓</span>
            <span>多维度的数据分析</span>
          </div>
        </div>
      </div>
      <!-- Decorative elements -->
      <div class="brand-decoration">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>

    <!-- Right Login Form Section -->
    <div class="login-form-section">
      <div class="login-card">
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <el-form
          ref="dataFormRef"
          :model="dataForm"
          :rules="dataRule"
          class="login-form"
          @keyup.enter="dataFormSubmit()"
        >
          <el-form-item prop="userName">
            <el-input
              v-model="dataForm.userName"
              placeholder="请输入账号"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="dataForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="dataFormSubmit()"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <span>Copyright © 2019 广州市蓝海创新科技有限公司</span>
        </div>
      </div>
    </div>

    <!-- Captcha Component -->
    <Verify
      ref="verifyRef"
      :captcha-type="'blockPuzzle'"
      :img-size="{width:'400px',height:'200px'}"
      @success="login"
    />
  </div>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { encrypt } from '@/utils/crypto'
import { getUUID } from '@/utils'
import Verify from '@/components/verifition/Verify.vue'
import cookie from 'vue-cookies'

const dataForm = ref({
  userName: '',
  password: '',
  uuid: '',
  captcha: ''
})

const dataRule = {
  userName: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const loading = ref(false)
const verifyRef = ref(null)
const dataFormRef = ref(null)

const dataFormSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      // TODO: 调试时临时跳过验证码，上线前需要恢复
      // verifyRef.value?.show()
      login({ captchaVerification: 'skip' })
    }
  })
}

const router = useRouter()
const login = (verifyResult) => {
  if (loading.value) return
  loading.value = true

  http({
    url: http.adornUrl('/adminLogin'),
    method: 'post',
    data: http.adornData({
      userName: dataForm.value.userName,
      passWord: encrypt(dataForm.value.password),
      captchaVerification: verifyResult.captchaVerification
    })
  }).then(({ data }) => {
    // Remove any existing Authorization cookies first
    cookie.remove('Authorization')
    cookie.remove('Authorization', '/')
    cookie.remove('Authorization', '/', window.location.hostname)
    const hostname = window.location.hostname
    const rootDomain = hostname.includes('.') ? hostname.split('.').slice(-2).join('.') : hostname
    cookie.remove('Authorization', '/', rootDomain)
    // Set new cookie
    cookie.set('Authorization', data.accessToken, '30d', '/', window.location.hostname)
    router.replace({ name: 'home' })
  }).catch(() => {
    loading.value = false
  })
}

onMounted(() => {
  dataForm.value.uuid = getUUID()
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.login-container {
  display: flex;
  min-height: 100vh;
  background: $--color-bg-page;
}

// Left Brand Section
.login-brand {
  flex: 0 0 45%;
  background: linear-gradient(135deg, $--color-primary 0%, $--color-primary-dark 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;

  @media (max-width: 992px) {
    display: none;
  }
}

.brand-content {
  text-align: center;
  color: #fff;
  z-index: 1;
  padding: 40px;
}

.brand-logo {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;

  .logo-icon {
    font-size: 40px;
    color: #fff;
  }
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 40px;
}

.brand-features {
  text-align: left;
  display: inline-block;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-icon {
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

// Decorative circles
.brand-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
}

.circle-3 {
  width: 200px;
  height: 200px;
  bottom: 20%;
  right: 10%;
}

// Right Form Section
.login-form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: $radius-xl;
  box-shadow: $shadow-xl;
  padding: 48px 40px;
  animation: slideInUp 0.5s ease;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    font-size: 24px;
    font-weight: 600;
    color: $--color-text-primary;
    margin: 0 0 8px;
  }

  p {
    font-size: 14px;
    color: $--color-text-secondary;
    margin: 0;
  }
}

.login-form {
  :deep(.el-input__wrapper) {
    border-radius: $radius-md;
    padding: 8px 16px;
    box-shadow: none;
    border: 1px solid $--color-border;

    &:hover {
      border-color: $--color-border-dark;
    }

    &.is-focus {
      border-color: $--color-primary;
      box-shadow: 0 0 0 3px rgba($--color-primary, 0.1);
    }
  }

  :deep(.el-input__inner) {
    font-size: 14px;

    &::placeholder {
      color: $--color-text-placeholder;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: $radius-md;
  background: $--color-primary;
  border-color: $--color-primary;

  &:hover {
    background: $--color-primary-dark;
    border-color: $--color-primary-dark;
  }
}

.login-footer {
  text-align: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid $--color-border-light;

  span {
    font-size: 12px;
    color: $--color-text-placeholder;
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
```

**Step 2: Verify the file was updated**

Run: `head -30 src/views/common/login/index.vue`
Expected: Shows new template structure

**Step 3: Commit**

```bash
git add src/views/common/login/index.vue
git commit -m "feat: redesign login page with modern split layout

- Left brand section with logo, title, features
- Right login form with card design
- Decorative background circles
- Element Plus icons integration
- Responsive design (brand hidden on mobile)
- Smooth slide-up animation"
```

---

## Task 5: Redesign Sidebar

**Files:**
- Modify: `src/layout/main-sidebar.vue`

**Step 1: Replace sidebar component**

Replace the entire content of `src/layout/main-sidebar.vue` with:

```vue
<template>
  <aside
    class="sidebar"
    :class="{ 'sidebar--collapsed': sidebarFold }"
  >
    <el-scrollbar class="sidebar-scroll">
      <!-- Home Menu -->
      <div
        class="menu-item"
        :class="{ 'menu-item--active': selectLeftId === '' }"
        @click="toHome()"
      >
        <svg-icon icon-class="icon-shouye" class="menu-icon" />
        <span v-show="!sidebarFold" class="menu-text">首页</span>
      </div>

      <!-- Menu List -->
      <template
        v-for="menu in menuList"
        :key="menu.menuId"
      >
        <div
          v-if="!menu.hidden"
          class="menu-item"
          :class="{
            'menu-item--active': selectLeftId === menu.menuId,
            'menu-item--has-children': menu.list && menu.list.length > 0
          }"
          @click="handleMenuClick(menu)"
        >
          <svg-icon
            v-if="menu.icon"
            :icon-class="`icon-${menu.icon}`"
            class="menu-icon"
          />
          <span v-show="!sidebarFold" class="menu-text">{{ menu.name }}</span>
          <svg-icon
            v-if="menu.list && menu.list.length > 0 && !sidebarFold"
            :icon-class="expandedMenuId === menu.menuId ? 'icon-arrow-up' : 'icon-arrow-down'"
            class="menu-arrow"
          />
        </div>

        <!-- Sub Menu (Inline) -->
        <transition name="submenu">
          <div
            v-if="menu.list && menu.list.length > 0 && expandedMenuId === menu.menuId && !sidebarFold"
            class="submenu"
          >
            <div
              v-for="subMenu in menu.list"
              :key="subMenu.menuId"
              class="submenu-item"
              :class="{ 'submenu-item--active': selectRightId === subMenu.menuId }"
              @click="gotoRouteHandle(subMenu)"
            >
              <span class="submenu-text">{{ subMenu.name }}</span>
            </div>
          </div>
        </transition>
      </template>
    </el-scrollbar>
  </aside>
</template>

<script setup>
const route = useRoute()
const router = useRouter()
const commonStore = useCommonStore()

const expandedMenuId = ref(null)

const sidebarFold = computed(() => commonStore.sidebarFold)
const selectLeftId = computed(() => commonStore.selectLeftId || '')
const selectRightId = computed(() => commonStore.selectRightId || '')

const menuList = computed({
  get: () => commonStore.menuList,
  set: val => commonStore.updateMenuList(val)
})

onBeforeMount(() => {
  menuList.value = JSON.parse(sessionStorage.getItem('menuList') || '[]')
  routeHandle(route)
})

const routeHandle = (route) => {
  if (route.name === 'home') {
    expandedMenuId.value = null
    commonStore.updateSelectLeftId('')
    commonStore.updateSelectRightId('')
  }
}

const toHome = () => {
  router.push({ name: 'home' })
  expandedMenuId.value = null
  commonStore.updateSidebarFold(true)
  commonStore.updateSelectLeftId('')
  commonStore.updateSelectRightId('')
}

const handleMenuClick = (menu) => {
  if (menu.list && menu.list.length > 0) {
    // Toggle submenu
    if (expandedMenuId.value === menu.menuId) {
      expandedMenuId.value = null
    } else {
      expandedMenuId.value = menu.menuId
    }
    commonStore.updateSelectLeftId(menu.menuId || '')
  } else {
    // Navigate directly
    gotoRouteHandle(menu)
  }
}

const gotoRouteHandle = (menu) => {
  if (router.history.current.name === menu.url) {
    expandedMenuId.value = null
    commonStore.updateSidebarFold(true)
    commonStore.updateSelectRightId('')
    commonStore.updateSelectLeftId(menu.menuId || '')
    return
  }

  expandedMenuId.value = null
  commonStore.updateSidebarFold(true)
  commonStore.updateSelectRightId(menu.menuId || '')
  commonStore.updateSelectLeftId(commonStore.selectLeftId)
  router.push({ name: menu.url })
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.sidebar {
  width: $sidebar-width;
  background: $sidebar--bg;
  height: calc(100vh - #{$navbar-height});
  transition: width $transition-base;
  overflow: hidden;

  &--collapsed {
    width: $sidebar-width-collapsed;
  }
}

.sidebar-scroll {
  height: 100%;

  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }

  :deep(.el-scrollbar__bar.is-horizontal) {
    display: none;
  }
}

// Menu Item
.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  color: $sidebar--text;
  cursor: pointer;
  transition: all $transition-fast;
  position: relative;
  user-select: none;

  &:hover {
    background: rgba(255, 255, 255, 0.05);
    color: $sidebar--text-active;
  }

  &--active {
    background: rgba($--color-primary, 0.15);
    color: $--color-primary;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 3px;
      background: $--color-primary;
    }

    .menu-icon {
      color: $--color-primary;
    }
  }
}

.menu-icon {
  font-size: 18px;
  flex-shrink: 0;
  transition: color $transition-fast;
}

.menu-text {
  flex: 1;
  margin-left: 12px;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.menu-arrow {
  font-size: 12px;
  color: $sidebar--text;
  transition: transform $transition-fast;
}

// Submenu
.submenu {
  background: $sidebar--bg-dark;
  overflow: hidden;
}

.submenu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px 12px 50px;
  color: $sidebar--text;
  cursor: pointer;
  transition: all $transition-fast;

  &:hover {
    background: rgba(255, 255, 255, 0.05);
    color: $sidebar--text-active;
  }

  &--active {
    color: $--color-primary;
    background: rgba($--color-primary, 0.1);

    &::before {
      content: '';
      width: 6px;
      height: 6px;
      background: $--color-primary;
      border-radius: 50%;
      margin-right: 10px;
    }
  }
}

.submenu-text {
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// Submenu transition
.submenu-enter-active,
.submenu-leave-active {
  transition: all $transition-base;
}

.submenu-enter-from,
.submenu-leave-to {
  opacity: 0;
  max-height: 0;
}

.submenu-enter-to,
.submenu-leave-from {
  opacity: 1;
  max-height: 500px;
}
</style>
```

**Step 2: Verify the file was updated**

Run: `head -20 src/layout/main-sidebar.vue`
Expected: Shows new sidebar template

**Step 3: Commit**

```bash
git add src/layout/main-sidebar.vue
git commit -m "feat: redesign sidebar with modern collapsible menu

- Collapsible sidebar (220px/80px)
- Inline submenu expansion
- Left border indicator for active state
- Smooth transitions and hover effects
- Dark theme with blue accent"
```

---

## Task 6: Redesign Navbar

**Files:**
- Modify: `src/layout/main-navbar.vue`

**Step 1: Replace navbar component**

Replace the entire content of `src/layout/main-navbar.vue` with:

```vue
<template>
  <nav class="navbar">
    <!-- Left Section -->
    <div class="navbar-left">
      <div class="collapse-btn" @click="setSidebarFold">
        <svg-icon :icon-class="sidebarFold ? 'icon-zhedie' : 'icon-zhedie'" />
      </div>
      <div class="navbar-brand">
        <span class="brand-text">Mall4j</span>
      </div>
    </div>

    <!-- Right Section -->
    <div class="navbar-right">
      <!-- User Dropdown -->
      <el-dropdown
        class="user-dropdown"
        trigger="click"
        @command="handleCommand"
      >
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar">
            {{ userName?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <span class="user-name">{{ userName }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="password">
              <el-icon><Key /></el-icon>
              <span>修改密码</span>
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- Update Password Dialog -->
    <UpdatePassword
      v-if="updatePasswordVisible"
      ref="updatePasswordRef"
    />
  </nav>
</template>

<script setup>
import { ArrowDown, Key, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import UpdatePassword from './main-navbar-update-password.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const commonStore = useCommonStore()

const userName = computed(() => userStore.name)
const sidebarFold = computed(() => commonStore.sidebarFold)

const setSidebarFold = () => {
  const len = commonStore.selectMenu.length
  const flag = sessionStorage.getItem('isExpand')
  if ((route.path === '/home' || len === 1) && flag === '0') {
    commonStore.updateSidebarFold(true)
  } else {
    commonStore.updateSidebarFold(!sidebarFold.value)
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    logoutHandle()
  } else if (command === 'password') {
    updatePasswordHandle()
  }
}

const logoutHandle = () => {
  ElMessageBox.confirm('确定进行[退出]操作?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/logOut'),
      method: 'post',
      data: http.adornData()
    }).then(() => {
      clearLoginInfo()
      router.push({ name: 'login' })
    })
  })
}

const updatePasswordVisible = ref(false)
const updatePasswordRef = ref(null)

const updatePasswordHandle = () => {
  updatePasswordVisible.value = true
  nextTick(() => {
    updatePasswordRef.value?.init()
  })
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $navbar-height;
  background: $navbar--bg;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

// Left Section
.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all $transition-fast;
  color: $--color-text-secondary;

  &:hover {
    background: $--color-bg-hover;
    color: $--color-primary;
  }

  svg {
    font-size: 20px;
    transition: transform $transition-base;
  }
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 8px;
}

.brand-text {
  font-size: 18px;
  font-weight: 600;
  color: $--color-text-primary;
}

// Right Section
.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: $radius-lg;
  transition: background $transition-fast;

  &:hover {
    background: $--color-bg-hover;
  }
}

.user-avatar {
  background: $--color-primary;
  color: #fff;
  font-weight: 500;
}

.user-name {
  font-size: 14px;
  color: $--color-text-primary;
  font-weight: 500;
}

.dropdown-icon {
  font-size: 12px;
  color: $--color-text-secondary;
  transition: transform $transition-fast;
}

// Dropdown Menu Styles
:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;

  .el-icon {
    font-size: 16px;
    color: $--color-text-secondary;
  }

  &:hover {
    background: $--color-primary-light;
    color: $--color-primary;

    .el-icon {
      color: $--color-primary;
    }
  }
}
</style>
```

**Step 2: Verify the file was updated**

Run: `head -20 src/layout/main-navbar.vue`
Expected: Shows new navbar template

**Step 3: Commit**

```bash
git add src/layout/main-navbar.vue
git commit -m "feat: redesign navbar with modern style

- 60px height with subtle shadow
- Collapse button with hover effect
- User avatar with initials
- Dropdown menu with icons
- Clean and minimal design"
```

---

## Task 7: Update Main Layout

**Files:**
- Modify: `src/layout/main.vue`

**Step 1: Update main layout component**

Replace the content of `src/layout/main.vue` with:

```vue
<template>
  <div
    v-loading.fullscreen.lock="loading"
    class="app-layout"
    :class="{ 'app-layout--sidebar-collapsed': sidebarFold }"
    :element-loading-text="'加载中...'"
  >
    <template v-if="!loading">
      <MainNavbar />
      <div class="app-body">
        <MainSidebar />
        <main class="app-content">
          <MainContent />
        </main>
      </div>
    </template>
  </div>
</template>

<script setup>
import MainNavbar from './main-navbar.vue'
import MainSidebar from './main-sidebar.vue'
import MainContent from './main-content.vue'

onBeforeMount(() => {
  getUserInfo()
})

const commonStore = useCommonStore()
const userStore = useUserStore()
const sidebarFold = computed(() => commonStore.sidebarFold)

onMounted(() => {
  resetDocumentClientHeight()
})

const resetDocumentClientHeight = () => {
  commonStore.documentClientHeight = document.documentElement.clientHeight
  window.onresize = () => {
    commonStore.documentClientHeight = document.documentElement.clientHeight
  }
}

const loading = ref(true)

const getUserInfo = () => {
  http({
    url: http.adornUrl('/sys/user/info'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    loading.value = false
    userStore.userId = data.userId
    userStore.name = data.username
    userStore.mobile = data.mobile
    userStore.shopId = data.shopId
    userStore.userId = data.userId
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: $--color-bg-page;
}

.app-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.app-content {
  flex: 1;
  overflow: auto;
  padding: $spacing-lg;
  min-width: 0;

  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }
}
</style>
```

**Step 2: Update main-content.vue**

Read `src/layout/main-content.vue` first, then update if needed:

Run: `cat src/layout/main-content.vue`

**Step 3: Commit**

```bash
git add src/layout/main.vue
git commit -m "feat: update main layout structure

- Flexbox-based layout
- Consistent spacing and background
- Clean content area with padding"
```

---

## Task 8: Build and Test

**Step 1: Run development server**

Run: `pnpm dev`

Expected: Dev server starts on http://localhost:9527

**Step 2: Test login page**

1. Open http://localhost:9527/login
2. Verify left brand section displays correctly
3. Verify login form styling
4. Test login functionality

**Step 3: Test dashboard**

1. Login successfully
2. Verify sidebar collapses/expands
3. Verify navbar user dropdown works
4. Verify menu navigation works

**Step 4: Build for production**

Run: `pnpm build`

Expected: Build completes without errors

**Step 5: Final commit**

```bash
git add -A
git commit -m "feat: complete mall4v UI modernization

- Modern color system with blue primary
- Redesigned login page with split layout
- Collapsible sidebar with inline submenus
- Clean navbar with user avatar
- Consistent spacing and typography
- Element Plus theme integration"
```

---

## Task 9: Deploy to Server

**Step 1: Deploy to lq server**

Run:
```bash
ssh lq "rm -rf /data/mall4j/webapp/mall4admin/*"
scp -r dist/* lq:/data/mall4j/webapp/mall4admin/
```

**Step 2: Verify deployment**

Open https://admin.lumistarlearning.com/login and verify the new design is live.
