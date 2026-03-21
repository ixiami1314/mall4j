<template>
  <header class="header">
    <div class="container header-content">
      <!-- Logo -->
      <div class="logo" @click="$router.push('/')">
        <div class="logo-icon">
          <el-icon :size="22"><Shop /></el-icon>
        </div>
        <span class="logo-text">mall4w</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <router-link to="/" class="nav-item" exact-active-class="active">{{ t('nav.home') }}</router-link>
        <router-link to="/category" class="nav-item" active-class="active">{{ t('nav.category') }}</router-link>
      </nav>

      <!-- 搜索框 -->
      <div class="search-box">
        <div class="search-wrapper">
          <el-icon class="search-icon"><Search /></el-icon>
          <input
            v-model="searchKeyword"
            type="text"
            :placeholder="t('header.searchPlaceholder')"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <el-icon :size="14"><Search /></el-icon>
          </button>
        </div>
      </div>

      <!-- 用户区域 -->
      <div class="user-area">
        <!-- 购物车 -->
        <router-link to="/cart" class="cart-link">
          <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
            <div class="cart-icon">
              <el-icon :size="18"><ShoppingCart /></el-icon>
            </div>
          </el-badge>
        </router-link>

        <template v-if="isLogin">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="30" :src="userStore.pic">
                {{ userStore.nickName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.nickName || t('nav.user') }}</span>
              <el-icon :size="12"><ArrowDown /></el-icon>
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
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.25s $ease-out-expo;
}

.header-content {
  display: flex;
  align-items: center;
  height: 56px;
  gap: 20px;
}

// Logo - 更紧凑
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.25s $ease-out-expo;

  &:hover {
    transform: scale(1.02);
  }
}

.logo-icon {
  width: 34px;
  height: 34px;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 2px 8px rgba(74, 159, 212, 0.2);
  transition: all 0.25s $ease-out-expo;

  &:hover {
    box-shadow: 0 4px 12px rgba(74, 159, 212, 0.3);
  }
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  font-family: 'Outfit', sans-serif;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

// 导航菜单 - 更精致
.nav-menu {
  display: flex;
  gap: 2px;
}

.nav-item {
  padding: 6px 14px;
  font-size: 13px;
  font-weight: 500;
  color: #334155;
  border-radius: 6px;
  transition: all 0.2s $ease-out-expo;

  &:hover {
    color: $primary;
    background: rgba(74, 159, 212, 0.06);
  }

  &.active {
    color: $primary;
    background: $primary-bg;
  }
}

// 搜索框 - 更简洁
.search-box {
  flex: 1;
  max-width: 400px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  height: 36px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #E2E8F0;
  background: #F8FAFC;
  transition: all 0.2s $ease-out-expo;
  padding: 0 4px 0 12px;
  gap: 8px;

  &:hover {
    border-color: #CBD5E1;
  }

  &:focus-within {
    border-color: $primary;
    background: #fff;
    box-shadow: 0 0 0 2px rgba(74, 159, 212, 0.1);

    .search-icon {
      color: $primary;
    }
  }

  .search-icon {
    font-size: 14px;
    color: #94A3B8;
    flex-shrink: 0;
    transition: color 0.2s;
  }

  input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 13px;
    color: #334155;
    background: transparent;
    height: 100%;

    &::placeholder {
      color: #94A3B8;
      font-size: 13px;
    }
  }

  .search-btn {
    width: 32px;
    height: 28px;
    border: none;
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    color: #fff;
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s $ease-out-expo;
    flex-shrink: 0;

    &:hover {
      background: linear-gradient(135deg, $primary-light 0%, $primary 100%);
    }
  }
}

// 用户区域 - 更紧凑
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 8px;
  transition: all 0.2s $ease-out-expo;

  &:hover {
    background: rgba(74, 159, 212, 0.06);
  }

  .user-name {
    max-width: 72px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 500;
    font-size: 13px;
    color: #334155;
  }
}

.auth-link {
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s $ease-out-expo;

  &.login {
    color: $primary;

    &:hover {
      background: rgba(74, 159, 212, 0.06);
    }
  }

  &.register {
    color: #fff;
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    box-shadow: 0 2px 8px rgba(74, 159, 212, 0.2);

    &:hover {
      box-shadow: 0 4px 12px rgba(74, 159, 212, 0.3);
    }
  }
}

// 购物车 - 更简洁
.cart-link {
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.2s $ease-out-expo;

  &:hover {
    background: rgba(74, 159, 212, 0.06);

    .cart-icon {
      transform: scale(1.05);
    }
  }
}

.cart-icon {
  width: 32px;
  height: 32px;
  background: $primary-bg;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $primary;
  transition: all 0.25s $ease-out-expo;
}

:deep(.el-badge__content) {
  background: linear-gradient(135deg, #DC2626 0%, #EF4444 100%);
  font-family: 'Outfit', sans-serif;
  font-weight: 600;
  font-size: 10px;
  height: 16px;
  line-height: 16px;
  padding: 0 4px;
  top: 2px;
  right: calc(50% - 18px);
}

// 响应式
@media (max-width: 1200px) {
  .nav-menu {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-content {
    height: 52px;
    gap: 12px;
  }

  .logo-text {
    display: none;
  }

  .search-box {
    max-width: none;
  }

  .auth-link {
    padding: 5px 10px;
    font-size: 12px;
  }
}
</style>
