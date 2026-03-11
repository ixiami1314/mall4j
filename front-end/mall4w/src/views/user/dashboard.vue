<template>
  <div class="user-dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-text">
        <h2>Hi, {{ userInfo.nickName || '用户' }}</h2>
        <p>欢迎来到个人中心</p>
      </div>
      <div class="welcome-img">
        <el-icon :size="80" color="#ff6700"><User /></el-icon>
      </div>
    </div>

    <!-- 订单快捷入口 -->
    <div class="order-section">
      <div class="section-header">
        <h3>我的订单</h3>
        <router-link to="/user/order" class="view-all">查看全部订单 ></router-link>
      </div>
      <div class="order-shortcuts">
        <div class="shortcut-item" @click="goOrder(1)">
          <div class="shortcut-icon">
            <el-badge :value="orderCount.unPay" :hidden="!orderCount.unPay" :max="99">
              <el-icon :size="28"><Wallet /></el-icon>
            </el-badge>
          </div>
          <span>待支付</span>
        </div>
        <div class="shortcut-item" @click="goOrder(2)">
          <div class="shortcut-icon">
            <el-badge :value="orderCount.payed" :hidden="!orderCount.payed" :max="99">
              <el-icon :size="28"><Box /></el-icon>
            </el-badge>
          </div>
          <span>待发货</span>
        </div>
        <div class="shortcut-item" @click="goOrder(3)">
          <div class="shortcut-icon">
            <el-badge :value="orderCount.consignment" :hidden="!orderCount.consignment" :max="99">
              <el-icon :size="28"><Van /></el-icon>
            </el-badge>
          </div>
          <span>待收货</span>
        </div>
        <div class="shortcut-item" @click="goOrder(5)">
          <div class="shortcut-icon">
            <el-icon :size="28"><CircleCheck /></el-icon>
          </div>
          <span>已完成</span>
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-section">
      <div class="stat-card" @click="$router.push('/user/collection')">
        <div class="stat-num">{{ collectionCount }}</div>
        <div class="stat-label">我的收藏</div>
      </div>
      <div class="stat-card" @click="$router.push('/user/coupon')">
        <div class="stat-num">{{ couponCount }}</div>
        <div class="stat-label">我的优惠券</div>
      </div>
      <div class="stat-card" @click="$router.push('/user/address')">
        <div class="stat-num">{{ addressCount }}</div>
        <div class="stat-label">收货地址</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">0</div>
        <div class="stat-label">我的足迹</div>
      </div>
    </div>

    <!-- 快捷功能 -->
    <div class="quick-menu-section">
      <div class="section-header">
        <h3>常用功能</h3>
      </div>
      <div class="quick-menu-grid">
        <div class="menu-item" @click="$router.push('/user/address')">
          <el-icon :size="24"><Location /></el-icon>
          <span>收货地址</span>
        </div>
        <div class="menu-item" @click="$router.push('/user/collection')">
          <el-icon :size="24"><Star /></el-icon>
          <span>我的收藏</span>
        </div>
        <div class="menu-item" @click="$router.push('/user/coupon')">
          <el-icon :size="24"><Ticket /></el-icon>
          <span>优惠券</span>
        </div>
        <div class="menu-item" @click="$router.push('/user/order')">
          <el-icon :size="24"><List /></el-icon>
          <span>全部订单</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { User, Wallet, Box, Van, CircleCheck, Location, Star, Ticket, List } from '@element-plus/icons-vue'
import { getOrderCount } from '@/api/order'
import { getCollectionCount } from '@/api/collection'
import { getAddressList } from '@/api/address'
import http from '@/utils/http'

const router = useRouter()

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
    console.error('获取订单数量失败:', error)
  }
}

const fetchCollectionCount = async () => {
  try {
    const { data } = await getCollectionCount()
    collectionCount.value = data || 0
  } catch (error) {
    console.error('获取收藏数量失败:', error)
  }
}

const fetchAddressCount = async () => {
  try {
    const { data } = await getAddressList()
    addressCount.value = (data || []).length
  } catch (error) {
    console.error('获取地址数量失败:', error)
  }
}

const goOrder = (status) => {
  router.push({ path: '/user/order', query: { status } })
}
</script>

<style lang="scss" scoped>
// 缓动函数
$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

.user-dashboard {
  padding: 12px;
}

// 欢迎区域
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #fff8f5 0%, #fff5f0 100%);
  border-radius: 20px;
  padding: 28px 32px;
  margin-bottom: 28px;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 103, 0, 0.08);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #ff6700, #ff4500);
  }

  // 装饰性背景
  &::after {
    content: '';
    position: absolute;
    right: -50px;
    top: -50px;
    width: 150px;
    height: 150px;
    background: radial-gradient(circle, rgba(255, 103, 0, 0.1) 0%, transparent 70%);
    border-radius: 50%;
  }

  .welcome-text {
    position: relative;
    z-index: 1;

    h2 {
      font-size: 26px;
      color: #1a1a1a;
      margin-bottom: 10px;
      font-weight: 700;
      font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
      letter-spacing: -0.01em;
    }

    p {
      color: #666;
      font-size: 15px;
      font-weight: 450;
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
    transform: translateY(-8px);
  }
}

// 通用标题
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  h3 {
    font-size: 20px;
    font-weight: 700;
    color: #1a1a1a;
    font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
    display: flex;
    align-items: center;
    gap: 10px;

    &::before {
      content: '';
      width: 5px;
      height: 5px;
      background: #ff6700;
      border-radius: 50%;
    }
  }

  .view-all {
    color: #666;
    font-size: 14px;
    font-weight: 450;
    padding: 6px 14px;
    border-radius: 16px;
    transition: all 0.25s $ease-out-expo;

    &:hover {
      color: #ff6700;
      background: rgba(255, 103, 0, 0.06);
    }
  }
}

// 订单快捷入口
.order-section {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 28px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.03);
}

.order-shortcuts {
  display: flex;
  justify-content: space-around;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 36px;
  cursor: pointer;
  border-radius: 16px;
  transition: all 0.35s $ease-out-expo;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 16px;
    background: linear-gradient(135deg, #fff8f5 0%, #ffefea 100%);
    opacity: 0;
    transition: opacity 0.35s $ease-out-expo;
  }

  &:hover {
    &::before {
      opacity: 1;
    }

    .shortcut-icon {
      color: #ff6700;
      transform: scale(1.15);
    }
  }

  .shortcut-icon {
    color: #666;
    margin-bottom: 12px;
    transition: all 0.35s $ease-out-expo;
    position: relative;
    z-index: 1;
  }

  span {
    font-size: 14px;
    color: #333;
    font-weight: 450;
    position: relative;
    z-index: 1;
  }
}

// 数据统计
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 28px;
}

.stat-card {
  background: #fff;
  border-radius: 20px;
  padding: 28px 20px;
  text-align: center;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.35s $ease-out-expo;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: transparent;
    transition: background 0.35s $ease-out-expo;
  }

  &:hover {
    border-color: rgba(255, 103, 0, 0.15);
    box-shadow: 0 8px 24px rgba(255, 103, 0, 0.1);
    transform: translateY(-4px);

    &::before {
      background: linear-gradient(90deg, #ff6700, #ff4500);
    }

    .stat-num {
      color: #ff6700;
      transform: scale(1.05);
    }
  }

  .stat-num {
    font-size: 36px;
    font-weight: 700;
    color: #e4393c;
    margin-bottom: 10px;
    font-family: 'Space Grotesk', sans-serif;
    transition: all 0.3s $ease-out-expo;
  }

  .stat-label {
    font-size: 14px;
    color: #666;
    font-weight: 450;
  }
}

// 快捷功能
.quick-menu-section {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.03);
}

.quick-menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  cursor: pointer;
  border-radius: 16px;
  transition: all 0.35s $ease-out-expo;
  color: #666;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 16px;
    background: linear-gradient(135deg, #fff8f5 0%, #ffefea 100%);
    opacity: 0;
    transition: opacity 0.35s $ease-out-expo;
  }

  &:hover {
    color: #ff6700;

    &::before {
      opacity: 1;
    }

    .el-icon {
      transform: scale(1.15) translateY(-4px);
    }
  }

  .el-icon {
    margin-bottom: 12px;
    transition: all 0.35s $ease-out-expo;
    position: relative;
    z-index: 1;
  }

  span {
    font-size: 14px;
    font-weight: 450;
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
    padding: 20px 24px;

    .welcome-text h2 {
      font-size: 22px;
    }

    .welcome-img {
      display: none;
    }
  }

  .order-shortcuts {
    flex-wrap: wrap;
    gap: 8px;
  }

  .shortcut-item {
    padding: 16px 24px;
  }

  .stat-card {
    padding: 24px 16px;

    .stat-num {
      font-size: 28px;
    }
  }
}
</style>
