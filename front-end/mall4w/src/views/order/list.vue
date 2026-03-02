<template>
  <div class="order-list container">
    <h2>我的订单</h2>

    <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待付款" name="1" />
      <el-tab-pane label="待发货" name="2" />
      <el-tab-pane label="待收货" name="3" />
      <el-tab-pane label="已完成" name="5" />
    </el-tabs>

    <div v-if="orderList.length === 0" class="empty-order">
      <el-empty description="暂无订单" />
    </div>
    <div v-else class="order-cards">
      <div v-for="order in orderList" :key="order.orderNumber" class="order-card">
        <div class="order-header">
          <span>订单号: {{ order.orderNumber }}</span>
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
          <span>共 {{ order.totalCount }} 件商品，实付 <em>¥{{ order.actualTotal }}</em></span>
          <div class="order-actions">
            <el-button v-if="order.status === 1" type="primary" @click="handlePay(order.orderNumber)">去支付</el-button>
            <el-button v-if="order.status === 1" @click="handleCancel(order.orderNumber)">取消订单</el-button>
            <el-button v-if="order.status === 3" type="primary" @click="handleReceipt(order.orderNumber)">确认收货</el-button>
            <el-button link @click="goDetail(order.orderNumber)">查看详情</el-button>
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
import { getOrderList, cancelOrder, receiptOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const activeStatus = ref('')
const orderList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusMap = {
  1: '待付款',
  2: '待发货',
  3: '待收货',
  4: '已完成',
  5: '已完成',
  6: '已取消'
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

const getStatusText = (status) => statusMap[status] || '未知'

const handlePay = (orderNumber) => {
  router.push({ path: '/order/pay-result', query: { orderNumbers: orderNumber } })
}

const handleCancel = async (orderNumber) => {
  await ElMessageBox.confirm('确定取消该订单吗？', '提示')
  await cancelOrder(orderNumber)
  ElMessage.success('订单已取消')
  fetchOrders()
}

const handleReceipt = async (orderNumber) => {
  await ElMessageBox.confirm('确定已收到货物吗？', '提示')
  await receiptOrder(orderNumber)
  ElMessage.success('已确认收货')
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
