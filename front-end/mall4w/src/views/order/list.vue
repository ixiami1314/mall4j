<template>
  <div class="order-list container">
    <h2>{{ t('order.list.title') }}</h2>

    <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
      <el-tab-pane :label="t('order.list.all')" name="" />
      <el-tab-pane :label="t('order.list.pending')" name="1" />
      <el-tab-pane :label="t('order.list.paid')" name="2" />
      <el-tab-pane :label="t('order.list.shipped')" name="3" />
      <el-tab-pane :label="t('order.list.completed')" name="5" />
    </el-tabs>

    <div v-if="orderList.length === 0" class="empty-order">
      <el-empty :description="t('order.list.empty')" />
    </div>
    <div v-else class="order-cards">
      <div v-for="order in orderList" :key="order.orderNumber" class="order-card">
        <div class="order-header">
          <span>{{ t('order.list.orderNo') }}{{ order.orderNumber }}</span>
          <span class="status">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-items">
          <div v-for="item in order.orderItemDtos" :key="item.orderItemId" class="order-item">
            <img :src="item.pic" class="item-img" />
            <div class="item-info">
              <p class="item-name">{{ item.prodName }}</p>
              <p class="item-sku">{{ item.skuName }}</p>
            </div>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-count">x{{ item.prodCount }}</div>
          </div>
        </div>
        <div class="order-footer">
          <span>{{ t('order.list.totalCount', { count: order.totalCount }) }}<em>¥{{ order.actualTotal }}</em></span>
          <div class="order-actions">
            <el-button v-if="order.status === 1" type="primary" @click="handlePay(order.orderNumber)">{{ t('order.list.pay') }}</el-button>
            <el-button v-if="order.status === 1" @click="handleCancel(order.orderNumber)">{{ t('order.list.cancel') }}</el-button>
            <el-button v-if="order.status === 3" type="primary" @click="handleReceipt(order.orderNumber)">{{ t('order.list.confirm') }}</el-button>
            <el-button link @click="goDetail(order.orderNumber)">{{ t('order.list.viewDetail') }}</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      @current-change="fetchOrders"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getOrderList, cancelOrder, receiptOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const { t } = useI18n()
const router = useRouter()

const activeStatus = ref('')
const orderList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusMap = {
  1: 'order.list.pending',
  2: 'order.list.paid',
  3: 'order.list.shipped',
  4: 'order.list.completed',
  5: 'order.list.completed',
  6: 'order.list.cancelled'
}

onMounted(() => {
  fetchOrders()
})

const fetchOrders = async () => {
  const { data } = await getOrderList({
    current: pageNum.value,
    size: pageSize.value,
    status: activeStatus.value
  })
  orderList.value = data?.records || []
  total.value = data?.total || 0
}

const handleTabChange = () => {
  pageNum.value = 1
  fetchOrders()
}

const getStatusText = (status) => t(statusMap[status] || 'common.unknown')

const handlePay = (orderNumber) => {
  router.push({ path: '/order/pay-result', query: { orderNumbers: orderNumber } })
}

const handleCancel = async (orderNumber) => {
  await ElMessageBox.confirm(t('order.list.cancelConfirm'), t('common.tip'))
  await cancelOrder(orderNumber)
  ElMessage.success(t('order.list.cancelSuccess'))
  fetchOrders()
}

const handleReceipt = async (orderNumber) => {
  await ElMessageBox.confirm(t('order.list.receiptConfirm'), t('common.tip'))
  await receiptOrder(orderNumber)
  ElMessage.success(t('order.list.receiptSuccess'))
  fetchOrders()
}

const goDetail = (orderNumber) => {
  router.push(`/order/detail/${orderNumber}`)
}
</script>

<style lang="scss" scoped>
.order-list {
  background: #fff;
  padding: 20px;
  min-height: 500px;
  h2 { margin-bottom: 20px; }
}
.order-card {
  border: 1px solid #eee;
  margin-bottom: 15px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  padding: 10px 15px;
  background: #f5f5f5;
  .status { color: #e4393c; }
}
.order-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  .item-img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    margin-right: 15px;
  }
  .item-info { flex: 1; }
  .item-sku { color: #999; font-size: 12px; margin-top: 5px; }
  .item-price { width: 100px; text-align: center; }
  .item-count { width: 80px; text-align: center; }
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  em { font-style: normal; color: #e4393c; font-weight: bold; }
}
</style>
