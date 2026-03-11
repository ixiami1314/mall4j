<template>
  <div class="coupon-page">
    <h3>{{ t('user.coupon.title') }}</h3>
    <div v-if="couponList.length === 0" class="empty-coupon">
      <el-empty :description="t('user.coupon.empty')" />
    </div>
    <div v-else class="coupon-list">
      <div v-for="coupon in couponList" :key="coupon.id" class="coupon-item">
        <div class="coupon-value">
          <span class="unit">¥</span>
          <span class="amount">{{ coupon.reduceAmount }}</span>
        </div>
        <div class="coupon-info">
          <p class="coupon-name">{{ t('user.coupon.discountRule', { full: coupon.fullAmount, reduce: coupon.reduceAmount }) }}</p>
          <p class="coupon-time">{{ coupon.startTime }} - {{ coupon.endTime }}</p>
        </div>
        <el-button type="primary" size="small" @click="$router.push('/')">{{ t('user.coupon.goUse') }}</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import http from '@/utils/http'

const { t } = useI18n()

const couponList = ref([])

onMounted(() => {
  fetchCoupons()
})

const fetchCoupons = async () => {
  const { data } = await http({
    url: http.adornUrl('/p/user/coupon/list'),
    method: 'get'
  })
  couponList.value = data || []
}
</script>

<style lang="scss" scoped>
h3 {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.coupon-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #eee;
  margin-bottom: 10px;
  .coupon-value {
    width: 100px;
    color: #e4393c;
    .unit { font-size: 14px; }
    .amount { font-size: 32px; font-weight: bold; }
  }
  .coupon-info {
    flex: 1;
    .coupon-name { font-weight: bold; margin-bottom: 5px; }
    .coupon-time { color: #999; font-size: 12px; }
  }
}
</style>
