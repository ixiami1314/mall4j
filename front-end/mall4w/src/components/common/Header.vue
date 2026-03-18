<template>
  <header class="header">
    <div class="container header-content">
      <!-- Logo -->
      <div class="logo" @click="$router.push('/')">
        <div class="logo-icon">
          <el-icon :size="26"><Shop /></el-icon>
        </div>
        <span class="logo-text">mall4w</span>
      </div>

      <!-- 搜索框 -->
      <div class="search-box">
        <div class="search-wrapper">
          <div class="search-input">
            <el-icon class="search-icon"><Search /></el-icon>
            <input
              v-model="searchKeyword"
              type="text"
              :placeholder="t('header.searchPlaceholder')"
              @keyup.enter="handleSearch"
            />
          </div>
          <button class="search-btn" @click="handleSearch">{{ t('common.search') }}</button>
        </div>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <router-link to="/" class="nav-item" exact-active-class="active">{{ t('nav.home') }}</router-link>
        <router-link to="/category" class="nav-item" active-class="active">{{ t('nav.category') }}</router-link>
      </nav>

      <!-- 用户区域 -->
      <div class="user-area">
        <template v-if="isLogin">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.pic">
                {{ userStore.nickName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.nickName || t('nav.user') }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="user">
                  <el-icon><User /></el-icon>
                  {{ t('header.userCenter') }}
                </el-dropdown-item>
                <el-dropdown-item command="order">
                  <el-icon><List /></el-icon>
                  {{ t('header.myOrders') }}
                </el-dropdown-item>
                <el-dropdown-item command="collection">
                  <el-icon><Star /></el-icon>
                  {{ t('header.myCollection') }}
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  {{ t('header.logout') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="auth-link login">{{ t('header.login') }}</router-link>
          <router-link to="/register" class="auth-link register">{{ t('header.register') }}</router-link>
        </template>

        <!-- 语言切换 -->
        <LocaleSwitch />

        <!-- 购物车 -->
        <router-link to="/cart" class="cart-link">
          <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
            <div class="cart-icon">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </div>
          </el-badge>
          <span class="cart-text">{{ t('nav.cart') }}</span>
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Search, User, ShoppingCart, Shop, ArrowDown, List, Star, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { isLogin, clearLoginInfo } from '@/utils'
import LocaleSwitch from './LocaleSwitch.vue'

const { t } = useI18n()

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchKeyword = ref('')

const cartCount = computed(() => cartStore.totalCount)

onMounted(() => {
  // 如果已登录，获取购物车数量
  if (isLogin()) {
    cartStore.fetchCartCount()
  }
})

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search/result', query: { keyword: searchKeyword.value.trim() } })
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
  } else if (command === 'collection') {
    router.push('/user/collection')
  }
}
</script>

<style lang="scss" scoped>
// 缓动函数
$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

// 颜色变量 - 淡蓝色系
$primary: #4A9FD4;
$primary-light: #6BB8E8;
$primary-dark: #3A8BC4;
$primary-bg: #F0F7FC;

.header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.25s $ease-out-expo;
}

.header-content {
  display: flex;
  align-items: center;
  height: 68px;
  gap: 28px;
}

// Logo
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.25s $ease-out-expo;

  &:hover {
    transform: scale(1.02);
  }
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 3px 10px rgba(74, 159, 212, 0.25);
  transition: all 0.25s $ease-out-expo;

  &:hover {
    box-shadow: 0 5px 16px rgba(74, 159, 212, 0.35);
    transform: translateY(-1px);
  }
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  font-family: 'Outfit', sans-serif;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

// 搜索框
.search-box {
  flex: 1;
  max-width: 480px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  height: 44px;
  border-radius: 24px;
  overflow: hidden;
  border: 1.5px solid #E2E8F0;
  background: #fff;
  transition: all 0.25s $ease-out-expo;

  &:hover {
    border-color: #CBD5E1;
    background: #FAFCFE;
  }

  &:focus-within {
    border-color: $primary;
    background: #fff;
    box-shadow: 0 0 0 3px rgba(74, 159, 212, 0.12);

    .search-icon {
      color: $primary;
    }
  }
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 0 14px 0 18px;
  gap: 10px;
  height: 100%;

  .search-icon {
    font-size: 16px;
    color: #94A3B8;
    flex-shrink: 0;
    transition: color 0.2s;
  }

  input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 14px;
    font-weight: 450;
    color: #334155;
    background: transparent;
    height: 100%;

    &::placeholder {
      color: #94A3B8;
      font-weight: 400;
    }
  }
}

.search-btn {
  height: 100%;
  padding: 0 28px;
  border: none;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.01em;
  cursor: pointer;
  transition: all 0.25s $ease-out-expo;
  white-space: nowrap;

  &:hover {
    background: linear-gradient(135deg, $primary-light 0%, $primary 100%);
  }

  &:active {
    background: linear-gradient(135deg, $primary-dark 0%, #2A7AB4 100%);
  }
}

// 导航菜单
.nav-menu {
  display: flex;
  gap: 4px;
}

.nav-item {
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  border-radius: 20px;
  transition: all 0.2s $ease-out-expo;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    opacity: 0;
    transition: opacity 0.2s $ease-out-expo;
    z-index: -1;
  }

  &:hover {
    color: $primary;
    background: rgba(74, 159, 212, 0.06);
  }

  &.active {
    color: #fff;
    background: transparent;
    box-shadow: 0 3px 10px rgba(74, 159, 212, 0.25);

    &::before {
      opacity: 1;
    }
  }
}

// 用户区域
.user-area {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 12px;
  border-radius: 20px;
  transition: all 0.2s $ease-out-expo;

  &:hover {
    background: rgba(74, 159, 212, 0.06);
  }

  .user-name {
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 500;
    font-size: 14px;
  }
}

.auth-link {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s $ease-out-expo;
  position: relative;
  overflow: hidden;

  &.login {
    color: $primary;
    border: 1px solid transparent;

    &:hover {
      background: rgba(74, 159, 212, 0.06);
      border-color: rgba(74, 159, 212, 0.15);
    }
  }

  &.register {
    color: #fff;
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    box-shadow: 0 3px 10px rgba(74, 159, 212, 0.25);

    &:hover {
      box-shadow: 0 5px 16px rgba(74, 159, 212, 0.35);
      transform: translateY(-1px);
    }
  }
}

// 购物车
.cart-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 5px 12px;
  border-radius: 10px;
  transition: all 0.2s $ease-out-expo;

  &:hover {
    background: rgba(74, 159, 212, 0.06);

    .cart-icon {
      transform: scale(1.08) translateY(-2px);
      box-shadow: 0 6px 16px rgba(74, 159, 212, 0.3);
    }
  }
}

.cart-icon {
  width: 38px;
  height: 38px;
  background: linear-gradient(135deg, $primary-light 0%, $primary 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: all 0.25s $ease-out-expo;
  box-shadow: 0 3px 10px rgba(74, 159, 212, 0.25);
}

.cart-text {
  font-size: 10px;
  color: #64748B;
  font-weight: 500;
}

:deep(.el-badge__content) {
  background: linear-gradient(135deg, #DC2626 0%, #EF4444 100%);
  font-family: 'Outfit', sans-serif;
  font-weight: 600;
  font-size: 10px;
  height: 17px;
  line-height: 17px;
  padding: 0 5px;
}

// 响应式
@media (max-width: 1200px) {
  .nav-menu {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-content {
    height: 60px;
    gap: 14px;
  }

  .logo-text {
    display: none;
  }

  .search-box {
    max-width: none;
  }

  .cart-text {
    display: none;
  }

  .auth-link {
    padding: 7px 14px;
    font-size: 12px;
  }
}
</style>
