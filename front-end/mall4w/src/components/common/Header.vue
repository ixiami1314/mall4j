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
