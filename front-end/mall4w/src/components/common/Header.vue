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
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品、品牌、店铺"
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <router-link to="/" class="nav-item" exact-active-class="active">首页</router-link>
        <router-link to="/category" class="nav-item" active-class="active">全部分类</router-link>
      </nav>

      <!-- 用户区域 -->
      <div class="user-area">
        <template v-if="isLogin">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.pic">
                {{ userStore.nickName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.nickName || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="user">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="order">
                  <el-icon><List /></el-icon>
                  我的订单
                </el-dropdown-item>
                <el-dropdown-item command="collection">
                  <el-icon><Star /></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="auth-link login">登录</router-link>
          <router-link to="/register" class="auth-link register">注册</router-link>
        </template>

        <!-- 购物车 -->
        <router-link to="/cart" class="cart-link">
          <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
            <div class="cart-icon">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </div>
          </el-badge>
          <span class="cart-text">购物车</span>
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, User, ShoppingCart, Shop, ArrowDown, List, Star, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { isLogin, clearLoginInfo } from '@/utils'

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
.header {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-content {
  display: flex;
  align-items: center;
  height: 70px;
  gap: 30px;
}

// Logo
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #3a86b9 0%, #5ba4d4 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}
.logo-text {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #3a86b9 0%, #5ba4d4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

// 搜索框
.search-box {
  flex: 1;
  max-width: 500px;
  :deep(.el-input__wrapper) {
    border-radius: 24px 0 0 24px;
    padding-left: 20px;
  }
  :deep(.el-input-group__append) {
    border-radius: 0 24px 24px 0;
    padding: 0;
    .el-button {
      margin: 0;
      border-radius: 0 24px 24px 0;
      padding: 0 24px;
      background: linear-gradient(135deg, #3a86b9 0%, #5ba4d4 100%);
      border: none;
      &:hover {
        background: linear-gradient(135deg, #2d6d94 0%, #4a8cb8 100%);
      }
    }
  }
}

// 导航菜单
.nav-menu {
  display: flex;
  gap: 8px;
}
.nav-item {
  padding: 10px 18px;
  font-size: 15px;
  font-weight: 500;
  color: #333;
  border-radius: 20px;
  transition: all 0.2s;
  &:hover {
    color: #3a86b9;
    background: #f5f7fa;
  }
  &.active {
    color: #fff;
    background: linear-gradient(135deg, #3a86b9 0%, #5ba4d4 100%);
  }
}

// 用户区域
.user-area {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: background 0.2s;
  &:hover {
    background: #f5f7fa;
  }
  .user-name {
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
.auth-link {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  &.login {
    color: #3a86b9;
    &:hover {
      background: #e8f4fc;
    }
  }
  &.register {
    color: #fff;
    background: linear-gradient(135deg, #3a86b9 0%, #5ba4d4 100%);
    &:hover {
      background: linear-gradient(135deg, #2d6d94 0%, #4a8cb8 100%);
    }
  }
}

// 购物车
.cart-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 4px 12px;
  border-radius: 8px;
  transition: background 0.2s;
  &:hover {
    background: #f5f7fa;
  }
}
.cart-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #ff9500 0%, #ff6b00 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  transition: transform 0.2s;
}
.cart-link:hover .cart-icon {
  transform: scale(1.1);
}
.cart-text {
  font-size: 12px;
  color: #666;
}
:deep(.el-badge__content) {
  background: #e4393c;
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
}
</style>
