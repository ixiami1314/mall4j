<template>
  <div class="user-center">
    <!-- 侧边栏 -->
    <div class="user-aside">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="user-avatar">
          <el-avatar :size="76" :src="userInfo.pic">
            {{ userInfo.nickName?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="user-level" v-if="userInfo.level">
            <el-tag size="small" type="warning">LV.{{ userInfo.level }}</el-tag>
          </div>
        </div>
        <h3 class="user-name">{{ userInfo.nickName || t('user.center.user') }}</h3>
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
            <span>{{ t('user.center.title') }}</span>
          </el-menu-item>
          <el-menu-item index="/user/order">
            <el-icon><List /></el-icon>
            <span>{{ t('user.center.myOrders') }}</span>
          </el-menu-item>
          <el-menu-item index="/user/address">
            <el-icon><Location /></el-icon>
            <span>{{ t('user.center.myAddress') }}</span>
          </el-menu-item>
          <el-menu-item index="/user/collection">
            <el-icon><Star /></el-icon>
            <span>{{ t('user.center.myCollection') }}</span>
          </el-menu-item>
          <el-menu-item index="/user/coupon">
            <el-icon><Ticket /></el-icon>
            <span>{{ t('user.center.myCoupon') }}</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 退出登录 -->
      <div class="logout-section">
        <el-button type="danger" plain @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          {{ t('user.center.logout') }}
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
import { useI18n } from 'vue-i18n'
import { User, List, Location, Star, Ticket, SwitchButton } from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { clearLoginInfo } from '@/utils'
import { ElMessageBox } from 'element-plus'

const { t } = useI18n()

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
    console.error(t('user.dashboard.getUserInfoError'), ':', error)
  }
}

const formatPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(t('user.center.logoutConfirm'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
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

// 颜色变量 - 淡蓝色系
$primary: #4A9FD4;
$primary-light: #6BB8E8;
$primary-dark: #3A8BC4;
$primary-bg: #F0F7FC;

$danger: #DC2626;

$text-primary: #1E293B;
$text-regular: #334155;
$text-secondary: #64748B;

$bg-page: #F8FAFC;
$bg-secondary: #F1F5F9;

$border-light: #E2E8F0;

.user-center {
  display: flex;
  gap: 20px;
  max-width: 1200px;
  margin: 16px auto;
  min-height: calc(100vh - 160px);
}

// 侧边栏
.user-aside {
  width: 220px;
  flex-shrink: 0;
}

// 用户卡片
.user-card {
  background: $primary;
  border-radius: 10px;
  padding: 24px 18px;
  text-align: center;
  color: #fff;
  margin-bottom: 16px;
}

.user-avatar {
  position: relative;
  display: inline-block;
  margin-bottom: 14px;

  :deep(.el-avatar) {
    border: 2px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.15);
    font-weight: 600;
  }
}

.user-level {
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);

  :deep(.el-tag) {
    font-weight: 600;
    border: none;
    font-size: 10px;
  }
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
}

.user-phone {
  font-size: 12px;
  opacity: 0.85;
  font-weight: 400;
}

// 菜单区域
.menu-section {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.user-menu {
  border-right: none;
  padding: 4px;

  :deep(.el-menu-item) {
    height: 44px;
    line-height: 44px;
    margin: 2px 0;
    border-radius: 6px;
    transition: all 0.15s $ease-out-expo;
    font-weight: 400;
    font-size: 13px;

    &:hover {
      background: rgba(74, 159, 212, 0.04);
    }

    &.is-active {
      background: $primary-bg;
      color: $primary;
      font-weight: 500;
      box-shadow: inset 2px 0 0 $primary;
    }

    .el-icon {
      font-size: 15px;
      margin-right: 10px;
    }
  }
}

// 退出登录
.logout-section {
  margin-top: 16px;
  padding: 0 4px;

  .el-button {
    width: 100%;
    height: 42px;
    font-size: 13px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.2s $ease-out-expo;
  }
}

// 主内容区
.user-main {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  min-height: 500px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

// 响应式
@media (max-width: 992px) {
  .user-center {
    flex-direction: column;
    gap: 16px;
  }

  .user-aside {
    width: 100%;
  }

  .user-card {
    display: flex;
    align-items: center;
    text-align: left;
    padding: 16px;
    gap: 14px;

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
      gap: 4px;
      padding: 8px;
    }

    :deep(.el-menu-item) {
      flex: 0 0 auto;
      height: 36px;
      line-height: 36px;
      padding: 0 14px !important;
    }
  }

  .logout-section {
    display: none;
  }
}

@media (max-width: 576px) {
  .user-main {
    padding: 16px;
    border-radius: 8px;
  }
}
</style>
