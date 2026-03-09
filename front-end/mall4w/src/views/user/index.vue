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
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item index="/order/list">
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
.user-center {
  display: flex;
  gap: 24px;
  max-width: 1200px;
  margin: 20px auto;
  min-height: calc(100vh - 180px);
}

// 侧边栏
.user-aside {
  width: 260px;
  flex-shrink: 0;
}

// 用户卡片
.user-card {
  background: linear-gradient(135deg, #3a86b9 0%, #5ba4d4 100%);
  border-radius: 12px;
  padding: 30px 20px;
  text-align: center;
  color: #fff;
  margin-bottom: 20px;
}
.user-avatar {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
  :deep(.el-avatar) {
    border: 3px solid rgba(255, 255, 255, 0.3);
  }
}
.user-level {
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
}
.user-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}
.user-phone {
  font-size: 13px;
  opacity: 0.8;
}

// 菜单区域
.menu-section {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}
.user-menu {
  border-right: none;
  :deep(.el-menu-item) {
    height: 56px;
    line-height: 56px;
    margin: 4px 8px;
    border-radius: 8px;
    &:hover {
      background: #f5f7fa;
    }
    &.is-active {
      background: linear-gradient(135deg, #e8f4fc 0%, #d6ebf7 100%);
      color: #3a86b9;
      font-weight: 500;
    }
    .el-icon {
      font-size: 18px;
      margin-right: 12px;
    }
  }
}

// 退出登录
.logout-section {
  margin-top: 20px;
  padding: 0 10px;
  .el-button {
    width: 100%;
    height: 48px;
    font-size: 15px;
  }
}

// 主内容区
.user-main {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  min-height: 600px;
}
</style>
