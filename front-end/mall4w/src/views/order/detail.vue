<template>
  <div class="order-detail container" v-loading="loading">
    <h2>{{ t('order.detail.title') }}</h2>

    <div class="section status-section">
      <h3>{{ t('order.detail.orderStatus') }}</h3>
      <p class="status">{{ getStatusText(orderInfo.status) }}</p>
    </div>

    <div class="section address-section">
      <h3>{{ t('order.detail.shippingInfo') }}</h3>
      <p>{{ orderInfo.receiver }} {{ orderInfo.mobile }}</p>
      <p>{{ orderInfo.addr }}</p>
    </div>

    <div class="section goods-section">
      <h3>{{ t('order.detail.productInfo') }}</h3>
      <div v-for="item in orderInfo.orderItemDtos" :key="item.orderItemId" class="goods-item">
        <img :src="item.pic" class="goods-img" />
        <div class="goods-info">
          <p class="goods-name">{{ item.prodName }}</p>
          <p class="goods-sku">{{ item.skuName }}</p>
        </div>
        <div class="goods-price">¥{{ item.price }}</div>
        <div class="goods-count">x{{ item.prodCount }}</div>
      </div>
    </div>

    <div class="section info-section">
      <h3>{{ t('order.detail.orderInfo') }}</h3>
      <p>{{ t('order.detail.orderNo') }}{{ orderInfo.orderNumber }}</p>
      <p>{{ t('order.detail.createTime') }}{{ orderInfo.createTime }}</p>
      <p>{{ t('order.detail.orderTotal') }}¥{{ orderInfo.total }}</p>
      <p>{{ t('order.detail.actualTotal') }}<em>¥{{ orderInfo.actualTotal }}</em></p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getOrderDetail } from '@/api/order'

const { t } = useI18n()
const route = useRoute()
const loading = ref(true)
const orderInfo = ref({})

const statusMap = {
  1: 'order.list.pending',
  2: 'order.list.paid',
  3: 'order.list.shipped',
  4: 'order.list.completed',
  5: 'order.list.completed',
  6: 'order.list.cancelled'
}

onMounted(() => {
  fetchOrderDetail()
})

const fetchOrderDetail = async () => {
  const { data } = await getOrderDetail(route.params.id)
  orderInfo.value = data || {}
  loading.value = false
}

const getStatusText = (status) => t(statusMap[status] || 'common.unknown')
</script>

<style lang="scss" scoped>
.order-detail {
  background: #fff;
  padding: 20px;
  min-height: 500px;
  h2 { margin-bottom: 20px; }
}
.section {
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #eee;
  h3 { margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px solid #eee; }
  p { margin-bottom: 8px; }
}
.status-section .status {
  font-size: 20px;
  color: #e4393c;
  font-weight: bold;
}
.goods-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  .goods-img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    margin-right: 15px;
  }
  .goods-info { flex: 1; }
  .goods-sku { color: #999; font-size: 12px; margin-top: 5px; }
  .goods-price { width: 100px; text-align: center; }
  .goods-count { width: 80px; text-align: center; }
}
.info-section em {
  font-style: normal;
  color: #e4393c;
  font-weight: bold;
}
</style>
