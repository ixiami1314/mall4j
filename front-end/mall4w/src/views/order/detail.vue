<template>
  <div class="order-detail container" v-loading="loading">
    <h2>订单详情</h2>

    <div class="section status-section">
      <h3>订单状态</h3>
      <p class="status">{{ getStatusText(orderInfo.status) }}</p>
    </div>

    <div class="section address-section">
      <h3>收货信息</h3>
      <p>{{ orderInfo.receiver }} {{ orderInfo.mobile }}</p>
      <p>{{ orderInfo.addr }}</p>
    </div>

    <div class="section goods-section">
      <h3>商品信息</h3>
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
      <h3>订单信息</h3>
      <p>订单编号：{{ orderInfo.orderNumber }}</p>
      <p>创建时间：{{ orderInfo.createTime }}</p>
      <p>订单总价：¥{{ orderInfo.total }}</p>
      <p>实付金额：<em>¥{{ orderInfo.actualTotal }}</em></p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail } from '@/api/order'

const route = useRoute()
const loading = ref(true)
const orderInfo = ref({})

const statusMap = {
  1: '待付款',
  2: '待发货',
  3: '待收货',
  4: '已完成',
  5: '已完成',
  6: '已取消'
}

onMounted(() => {
  fetchOrderDetail()
})

const fetchOrderDetail = async () => {
  const { data } = await getOrderDetail(route.params.id)
  orderInfo.value = data || {}
  loading.value = false
}

const getStatusText = (status) => statusMap[status] || '未知'
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
