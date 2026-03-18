<template>
  <div class="user-dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-text">
        <h2>{{ t('user.dashboard.welcomeTitle', { name: userInfo.nickName || t('user.center.user') }) }}</h2>
        <p>{{ t('user.dashboard.welcomeSubtitle') }}</p>
      </div>
      <div class="welcome-img">
        <el-icon :size="72" color="#4A9FD4"><User /></el-icon>
      </div>
    </div>

    <!-- 订单快捷入口 -->
    <div class="order-section">
      <div class="section-header">
        <h3>{{ t('user.center.myOrders') }}</h3>
        <router-link to="/user/order" class="view-all">{{ t('user.dashboard.viewAllOrders') }}</router-link>
      </div>
      <div class="order-shortcuts">
        <div class="shortcut-item" @click="goOrder(1)">
          <div class="shortcut-icon">
            <el-badge :value="orderCount.unPay" :hidden="!orderCount.unPay" :max="99">
              <el-icon :size="26"><Wallet /></el-icon>
            </el-badge>
          </div>
          <span>{{ t('user.dashboard.pendingPayment') }}</span>
        </div>
        <div class="shortcut-item" @click="goOrder(2)">
          <div class="shortcut-icon">
            <el-badge :value="orderCount.payed" :hidden="!orderCount.payed" :max="99">
              <el-icon :size="26"><Box /></el-icon>
            </el-badge>
          </div>
          <span>{{ t('user.dashboard.pendingShipment') }}</span>
        </div>
        <div class="shortcut-item" @click="goOrder(3)">
          <div class="shortcut-icon">
            <el-badge :value="orderCount.consignment" :hidden="!orderCount.consignment" :max="99">
              <el-icon :size="26"><Van /></el-icon>
            </el-badge>
          </div>
          <span>{{ t('user.dashboard.pendingReceipt') }}</span>
        </div>
        <div class="shortcut-item" @click="goOrder(5)">
          <div class="shortcut-icon">
            <el-icon :size="26"><CircleCheck /></el-icon>
          </div>
          <span>{{ t('user.dashboard.completed') }}</span>
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-section">
      <div class="stat-card" @click="$router.push('/user/collection')">
        <div class="stat-num">{{ collectionCount }}</div>
        <div class="stat-label">{{ t('user.dashboard.myCollection') }}</div>
      </div>
      <div class="stat-card" @click="$router.push('/user/coupon')">
        <div class="stat-num">{{ couponCount }}</div>
        <div class="stat-label">{{ t('user.dashboard.myCoupon') }}</div>
      </div>
      <div class="stat-card" @click="$router.push('/user/address')">
        <div class="stat-num">{{ addressCount }}</div>
        <div class="stat-label">{{ t('user.dashboard.myAddress') }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">0</div>
        <div class="stat-label">{{ t('user.dashboard.myFootprint') }}</div>
      </div>
    </div>

    <!-- 快捷功能 -->
    <div class="quick-menu-section">
      <div class="section-header">
        <h3>{{ t('user.dashboard.commonFunctions') }}</h3>
      </div>
      <div class="quick-menu-grid">
        <div class="menu-item" @click="$router.push('/user/address')">
          <el-icon :size="22"><Location /></el-icon>
          <span>{{ t('user.center.myAddress') }}</span>
        </div>
        <div class="menu-item" @click="$router.push('/user/collection')">
          <el-icon :size="22"><Star /></el-icon>
          <span>{{ t('user.center.myCollection') }}</span>
        </div>
        <div class="menu-item" @click="$router.push('/user/coupon')">
          <el-icon :size="22"><Ticket /></el-icon>
          <span>{{ t('user.dashboard.couponShort') }}</span>
        </div>
        <div class="menu-item" @click="$router.push('/user/order')">
          <el-icon :size="22"><List /></el-icon>
          <span>{{ t('user.dashboard.allOrders') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { User, Wallet, Box, Van, CircleCheck, Location, Star, Ticket, List } from '@element-plus/icons-vue'
import { getOrderCount } from '@/api/order'
import { getCollectionCount } from '@/api/collection'
import { getAddressList } from '@/api/address'

const router = useRouter()
const { t } = useI18n()

// 从父组件注入用户信息
const userInfo = inject('userInfo', ref({}))

const orderCount = ref({
  unPay: 0,
  payed: 0,
  consignment: 0
})
const collectionCount = ref(0)
const couponCount = ref(0)
const addressCount = ref(0)

onMounted(() => {
  fetchOrderCount()
  fetchCollectionCount()
  fetchAddressCount()
})

const fetchOrderCount = async () => {
  try {
    const { data } = await getOrderCount()
    if (data) {
      orderCount.value = {
        unPay: data.unPay || 0,
        payed: data.payed || 0,
        consignment: data.consignment || 0
      }
    }
  } catch (error) {
    console.error(t('user.dashboard.getOrderCountError'), ':', error)
  }
}

const fetchCollectionCount = async () => {
  try {
    const { data } = await getCollectionCount()
    collectionCount.value = data || 0
  } catch (error) {
    console.error(t('user.dashboard.getCollectionCountError'), ':', error)
  }
}

const fetchAddressCount = async () => {
  try {
    const { data } = await getAddressList()
    addressCount.value = (data || []).length
  } catch (error) {
    console.error(t('user.dashboard.getAddressCountError'), ':', error)
  }
}

const goOrder = (status) => {
  router.push({ path: '/user/order', query: { status } })
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
$primary-surface: #E8F2FA;

$danger: #DC2626;

$text-primary: #1E293B;
$text-regular: #334155;
$text-secondary: #64748B;
$text-tertiary: #94A3B8;

$bg-page: #F8FAFC;
$bg-secondary: #F1F5F9;

$border-light: #E2E8F0;

.user-dashboard {
  padding: 10px;
}

// 欢迎区域
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, $primary-bg 0%, $primary-surface 100%);
  border-radius: 16px;
  padding: 24px 28px;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(74, 159, 212, 0.1);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, $primary, $primary-dark);
  }

  // 装饰性背景
  &::after {
    content: '';
    position: absolute;
    right: -40px;
    top: -40px;
    width: 120px;
    height: 120px;
    background: radial-gradient(circle, rgba(74, 159, 212, 0.12) 0%, transparent 70%);
    border-radius: 50%;
  }

  .welcome-text {
    position: relative;
    z-index: 1;

    h2 {
      font-size: 24px;
      color: $text-primary;
      margin-bottom: 8px;
      font-weight: 600;
      font-family: 'Outfit', 'Noto Sans SC', sans-serif;
      letter-spacing: -0.01em;
    }

    p {
      color: $text-secondary;
      font-size: 14px;
      font-weight: 400;
    }
  }

  .welcome-img {
    opacity: 0.9;
    position: relative;
    z-index: 1;
    animation: float 3s ease-in-out infinite;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-6px);
  }
}

// 通用标题
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: $text-primary;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
    display: flex;
    align-items: center;
    gap: 8px;

    &::before {
      content: '';
      width: 4px;
      height: 4px;
      background: $primary;
      border-radius: 50%;
    }
  }

  .view-all {
    color: $text-secondary;
    font-size: 13px;
    font-weight: 400;
    padding: 5px 12px;
    border-radius: 14px;
    transition: all 0.2s $ease-out-expo;

    &:hover {
      color: $primary;
      background: rgba(74, 159, 212, 0.08);
    }
  }
}

// 订单快捷入口
.order-section {
  background: #fff;
  border-radius: 16px;
  padding: 22px;
  margin-bottom: 24px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 12px rgba(0, 0, 0, 0.03);
}

.order-shortcuts {
  display: flex;
  justify-content: space-around;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 18px 32px;
  cursor: pointer;
  border-radius: 14px;
  transition: all 0.3s $ease-out-expo;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 14px;
    background: linear-gradient(135deg, $primary-bg 0%, $primary-surface 100%);
    opacity: 0;
    transition: opacity 0.3s $ease-out-expo;
  }

  &:hover {
    &::before {
      opacity: 1;
    }

    .shortcut-icon {
      color: $primary;
      transform: scale(1.1);
    }
  }

  .shortcut-icon {
    color: $text-secondary;
    margin-bottom: 10px;
    transition: all 0.3s $ease-out-expo;
    position: relative;
    z-index: 1;
  }

  span {
    font-size: 13px;
    color: $text-regular;
    font-weight: 400;
    position: relative;
    z-index: 1;
  }
}

// 数据统计
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px 18px;
  text-align: center;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s $ease-out-expo;
  position: relative;
  overflow: hidden;
  box-shadow: 0 1px 10px rgba(0, 0, 0, 0.03);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: transparent;
    transition: background 0.3s $ease-out-expo;
  }

  &:hover {
    border-color: rgba(74, 159, 212, 0.15);
    box-shadow: 0 6px 20px rgba(74, 159, 212, 0.1);
    transform: translateY(-3px);

    &::before {
      background: linear-gradient(90deg, $primary, $primary-dark);
    }

    .stat-num {
      color: $primary;
      transform: scale(1.04);
    }
  }

  .stat-num {
    font-size: 32px;
    font-weight: 700;
    color: $danger;
    margin-bottom: 8px;
    font-family: 'Outfit', sans-serif;
    transition: all 0.25s $ease-out-expo;
  }

  .stat-label {
    font-size: 13px;
    color: $text-secondary;
    font-weight: 400;
  }
}

// 快捷功能
.quick-menu-section {
  background: #fff;
  border-radius: 16px;
  padding: 22px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 12px rgba(0, 0, 0, 0.03);
}

.quick-menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 14px;
  cursor: pointer;
  border-radius: 14px;
  transition: all 0.3s $ease-out-expo;
  color: $text-secondary;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 14px;
    background: linear-gradient(135deg, $primary-bg 0%, $primary-surface 100%);
    opacity: 0;
    transition: opacity 0.3s $ease-out-expo;
  }

  &:hover {
    color: $primary;

    &::before {
      opacity: 1;
    }

    .el-icon {
      transform: scale(1.1) translateY(-3px);
    }
  }

  .el-icon {
    margin-bottom: 10px;
    transition: all 0.3s $ease-out-expo;
    position: relative;
    z-index: 1;
  }

  span {
    font-size: 13px;
    font-weight: 400;
    position: relative;
    z-index: 1;
  }
}

// 响应式
@media (max-width: 992px) {
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .welcome-section {
    padding: 18px 20px;

    .welcome-text h2 {
      font-size: 20px;
    }

    .welcome-img {
      display: none;
    }
  }

  .order-shortcuts {
    flex-wrap: wrap;
    gap: 6px;
  }

  .shortcut-item {
    padding: 14px 20px;
  }

  .stat-card {
    padding: 20px 14px;

    .stat-num {
      font-size: 26px;
    }
  }
}
</style>
