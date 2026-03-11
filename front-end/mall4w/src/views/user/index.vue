<template>
  <div class="user-center">
    <!-- 侧边栏 -->
    <div class="user-aside">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="user-avatar">
          <el-avatar :size="80" :src="userInfo.pic">
            {{ userInfo.nickName?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="user-level" v-if="userInfo.level">
            <el-tag size="small" type="warning">LV.{{ userInfo.level }}</el-tag>
          </div>
        </div>
        <h3 class="user-name">{{ userInfo.nickName || '用户' }}</h3>
        <p class="user-phone" v-if="userInfo.userMobile">{{ formatPhone(userInfo.userMobile) }}</p>
      </div>

      <!-- 菜单列表 -->
      <div class="menu-section">
        <el-menu
          :default-active="activeMenu"
          router
          class="user-menu"
        >
          <el-menu-item index="/user/dashboard">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/user/order">
            <el-icon><List /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/user/address">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>
          <el-menu-item index="/user/collection">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="/user/coupon">
            <el-icon><Ticket /></el-icon>
            <span>我的优惠券</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 退出登录 -->
      <div class="logout-section">
        <el-button type="danger" plain @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="user-main">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, List, Location, Star, Ticket, SwitchButton } from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { clearLoginInfo } from '@/utils'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const userInfo = ref({})

const activeMenu = computed(() => route.path)

onMounted(async () => {
  await fetchUserInfo()
})

const fetchUserInfo = async () => {
  try {
    const { data } = await getUserInfo()
    userInfo.value = data || {}
    userStore.setUser(data)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const formatPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    clearLoginInfo()
    userStore.reset()
    cartStore.reset()
    router.push('/')
  } catch (error) {
    // 取消退出
  }
}
</script>

<style lang="scss" scoped>
// 缓动函数
$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

.user-center {
  display: flex;
  gap: 28px;
  max-width: 1200px;
  margin: 24px auto;
  min-height: calc(100vh - 180px);
  animation: fadeIn 0.5s $ease-out-expo;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 侧边栏
.user-aside {
  width: 280px;
  flex-shrink: 0;
}

// 用户卡片
.user-card {
  background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
  border-radius: 24px;
  padding: 36px 24px;
  text-align: center;
  color: #fff;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(255, 103, 0, 0.25);

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -50%;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 60%);
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: -30%;
    width: 80%;
    height: 80%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 60%);
    pointer-events: none;
  }
}

.user-avatar {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;

  :deep(.el-avatar) {
    border: 4px solid rgba(255, 255, 255, 0.35);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    background: rgba(255, 255, 255, 0.2);
    font-weight: 600;
  }
}

.user-level {
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);

  :deep(.el-tag) {
    font-weight: 600;
    border: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 10px;
  letter-spacing: 0.01em;
}

.user-phone {
  font-size: 14px;
  opacity: 0.85;
  font-weight: 450;
}

// 菜单区域
.menu-section {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.user-menu {
  border-right: none;
  padding: 8px;

  :deep(.el-menu-item) {
    height: 56px;
    line-height: 56px;
    margin: 4px 0;
    border-radius: 12px;
    transition: all 0.3s $ease-out-expo;
    font-weight: 450;

    &:hover {
      background: rgba(255, 103, 0, 0.04);
    }

    &.is-active {
      background: linear-gradient(135deg, #fff8f5 0%, #fff3ed 100%);
      color: #ff6700;
      font-weight: 600;
      box-shadow: inset 3px 0 0 #ff6700;
    }

    .el-icon {
      font-size: 18px;
      margin-right: 14px;
    }
  }
}

// 退出登录
.logout-section {
  margin-top: 24px;
  padding: 0 8px;

  .el-button {
    width: 100%;
    height: 52px;
    font-size: 15px;
    font-weight: 500;
    border-radius: 14px;
    transition: all 0.3s $ease-out-expo;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(228, 57, 60, 0.25);
    }
  }
}

// 主内容区
.user-main {
  flex: 1;
  background: #fff;
  border-radius: 24px;
  padding: 28px;
  min-height: 600px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

// 响应式
@media (max-width: 992px) {
  .user-center {
    flex-direction: column;
    gap: 24px;
  }

  .user-aside {
    width: 100%;
  }

  .user-card {
    display: flex;
    align-items: center;
    text-align: left;
    padding: 24px;
    gap: 20px;

    .user-avatar {
      margin-bottom: 0;
    }

    .user-info {
      flex: 1;
    }

    .user-name {
      margin-bottom: 4px;
    }
  }

  .menu-section {
    :deep(.el-menu) {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      gap: 8px;
      padding: 12px;
    }

    :deep(.el-menu-item) {
      flex: 0 0 auto;
      height: 44px;
      line-height: 44px;
      padding: 0 20px !important;
    }
  }

  .logout-section {
    display: none;
  }
}

@media (max-width: 576px) {
  .user-main {
    padding: 20px;
    border-radius: 20px;
  }
}
</style>
