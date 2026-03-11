<template>
  <header class="header">
    <div class="container header-content">
      <!-- Logo -->
      <div class="logo" @click="$router.push('/')">
        <div class="logo-icon">
          <el-icon :size="28"><Shop /></el-icon>
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
              <el-icon :size="22"><ShoppingCart /></el-icon>
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

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s $ease-out-expo;
}

.header-content {
  display: flex;
  align-items: center;
  height: 72px;
  gap: 32px;
}

// Logo
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.3s $ease-out-expo;

  &:hover {
    transform: scale(1.02);
  }
}

.logo-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 103, 0, 0.25);
  transition: all 0.3s $ease-out-expo;

  &:hover {
    box-shadow: 0 6px 20px rgba(255, 103, 0, 0.35);
    transform: translateY(-1px);
  }
}

.logo-text {
  font-size: 26px;
  font-weight: 700;
  font-family: 'Space Grotesk', sans-serif;
  background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

// 搜索框 - 统一整体设计
.search-box {
  flex: 1;
  max-width: 520px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  height: 48px;
  border-radius: 28px;
  overflow: hidden;
  border: 2px solid #e8e4de;
  background: #fff;
  transition: all 0.3s $ease-out-expo;

  &:hover {
    border-color: #d4cfc6;
    background: #faf9f7;
  }

  &:focus-within {
    border-color: #ff6700;
    background: #fff;
    box-shadow: 0 0 0 3px rgba(255, 103, 0, 0.1);

    .search-icon {
      color: #ff6700;
    }
  }
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 0 16px 0 20px;
  gap: 10px;
  height: 100%;

  .search-icon {
    font-size: 18px;
    color: #999;
    flex-shrink: 0;
    transition: color 0.3s;
  }

  input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 15px;
    font-weight: 450;
    color: #333;
    background: transparent;
    height: 100%;

    &::placeholder {
      color: #999;
      font-weight: 400;
    }
  }
}

.search-btn {
  height: 100%;
  padding: 0 32px;
  border: none;
  background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
  cursor: pointer;
  transition: all 0.3s $ease-out-expo;
  white-space: nowrap;

  &:hover {
    background: linear-gradient(135deg, #ff8c42 0%, #ff6700 100%);
  }

  &:active {
    background: linear-gradient(135deg, #e55d00 0%, #d44d00 100%);
  }
}

// 导航菜单
.nav-menu {
  display: flex;
  gap: 6px;
}

.nav-item {
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 500;
  color: #333;
  border-radius: 24px;
  transition: all 0.25s $ease-out-expo;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
    opacity: 0;
    transition: opacity 0.25s $ease-out-expo;
    z-index: -1;
  }

  &:hover {
    color: #ff6700;
    background: rgba(255, 103, 0, 0.06);
  }

  &.active {
    color: #fff;
    background: transparent;
    box-shadow: 0 4px 12px rgba(255, 103, 0, 0.3);

    &::before {
      opacity: 1;
    }
  }
}

// 用户区域
.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 14px;
  border-radius: 24px;
  transition: all 0.25s $ease-out-expo;

  &:hover {
    background: rgba(255, 103, 0, 0.06);
  }

  .user-name {
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 500;
  }
}

.auth-link {
  padding: 10px 22px;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s $ease-out-expo;
  position: relative;
  overflow: hidden;

  &.login {
    color: #ff6700;
    border: 1px solid transparent;

    &:hover {
      background: rgba(255, 103, 0, 0.06);
      border-color: rgba(255, 103, 0, 0.15);
    }
  }

  &.register {
    color: #fff;
    background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
    box-shadow: 0 4px 12px rgba(255, 103, 0, 0.25);

    &:hover {
      box-shadow: 0 6px 20px rgba(255, 103, 0, 0.35);
      transform: translateY(-1px);
    }
  }
}

// 购物车
.cart-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  padding: 6px 14px;
  border-radius: 12px;
  transition: all 0.25s $ease-out-expo;

  &:hover {
    background: rgba(255, 103, 0, 0.06);

    .cart-icon {
      transform: scale(1.1) translateY(-2px);
      box-shadow: 0 8px 20px rgba(255, 103, 0, 0.35);
    }
  }
}

.cart-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #ff9500 0%, #ff6b00 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: all 0.3s $ease-out-expo;
  box-shadow: 0 4px 12px rgba(255, 103, 0, 0.25);
}

.cart-text {
  font-size: 11px;
  color: #666;
  font-weight: 500;
}

:deep(.el-badge__content) {
  background: linear-gradient(135deg, #e4393c 0%, #c41e24 100%);
  font-family: 'Space Grotesk', sans-serif;
  font-weight: 600;
  font-size: 10px;
  height: 18px;
  line-height: 18px;
  padding: 0 6px;
}

// 响应式
@media (max-width: 1200px) {
  .nav-menu {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-content {
    height: 64px;
    gap: 16px;
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
    padding: 8px 16px;
    font-size: 13px;
  }
}
</style>
