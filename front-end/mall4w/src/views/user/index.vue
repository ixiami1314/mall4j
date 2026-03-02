<template>
  <div class="user-center container">
    <div class="user-aside">
      <div class="user-info">
        <el-avatar :size="80" :src="userInfo.pic">{{ userInfo.nickName?.charAt(0) }}</el-avatar>
        <p class="nick-name">{{ userInfo.nickName || '用户' }}</p>
      </div>
      <el-menu :default-active="activeMenu" router>
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
    <div class="user-main">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { User, List, Location, Star, Ticket } from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const userInfo = ref({})
const activeMenu = ref('/user')

onMounted(async () => {
  const { data } = await getUserInfo()
  userInfo.value = data || {}
  userStore.setUser(data)
})

const refreshUser = () => {
  userInfo.value = { ...userStore.$state }
}
</script>

<style lang="scss" scoped>
.user-center {
  display: flex;
  gap: 20px;
  min-height: 600px;
}
.user-aside {
  width: 200px;
  background: #fff;
  padding: 20px;
}
.user-info {
  text-align: center;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  .nick-name {
    margin-top: 10px;
    font-weight: bold;
  }
}
.user-main {
  flex: 1;
  background: #fff;
  padding: 20px;
}
</style>
