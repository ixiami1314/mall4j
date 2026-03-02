<template>
  <div class="order-confirm container" v-loading="loading">
    <h2>确认订单</h2>

    <!-- 收货地址 -->
    <div class="section address-section">
      <h3>收货地址</h3>
      <div v-if="!selectedAddr" class="no-address">
        <el-button type="primary" @click="showAddressDialog = true">选择收货地址</el-button>
      </div>
      <div v-else class="address-info">
        <p><strong>{{ selectedAddr.receiver }}</strong> {{ selectedAddr.mobile }}</p>
        <p>{{ selectedAddr.province }}{{ selectedAddr.city }}{{ selectedAddr.area }}{{ selectedAddr.addr }}</p>
        <el-button link @click="showAddressDialog = true">修改</el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="section goods-section">
      <h3>商品清单</h3>
      <div v-for="shop in orderInfo.shopCartOrders" :key="shop.shopId" class="shop-card">
        <div class="shop-header">{{ shop.shopName }}</div>
        <div v-for="discount in shop.shopCartItemDiscounts" :key="discount.discountId">
          <div v-for="item in discount.shopCartItems" :key="item.basketId" class="goods-item">
            <img :src="item.pic" class="goods-img" />
            <div class="goods-info">
              <p class="goods-name">{{ item.prodName }}</p>
              <p class="goods-sku">{{ item.skuName }}</p>
            </div>
            <div class="goods-price">¥{{ item.price }}</div>
            <div class="goods-count">x{{ item.prodCount }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 订单汇总 -->
    <div class="section summary-section">
      <div class="summary-row">
        <span>商品总价</span>
        <span>¥{{ orderInfo.total }}</span>
      </div>
      <div class="summary-row">
        <span>优惠</span>
        <span>-¥{{ orderInfo.orderReduce || 0 }}</span>
      </div>
      <div class="summary-row total">
        <span>应付金额</span>
        <span class="price">¥{{ orderInfo.actualTotal }}</span>
      </div>
    </div>

    <!-- 提交栏 -->
    <div class="submit-bar">
      <span>应付：<em>¥{{ orderInfo.actualTotal }}</em></span>
      <el-button type="danger" size="large" :loading="submitting" @click="handleSubmit">提交订单</el-button>
    </div>

    <!-- 地址选择弹窗 -->
    <el-dialog v-model="showAddressDialog" title="选择收货地址" width="600px">
      <div v-for="addr in addressList" :key="addr.addrId" class="address-item" @click="selectAddress(addr)">
        <p><strong>{{ addr.receiver }}</strong> {{ addr.mobile }}</p>
        <p>{{ addr.province }}{{ addr.city }}{{ addr.area }}{{ addr.addr }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { confirmOrder, submitOrder } from '@/api/order'
import { getAddressList } from '@/api/address'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const orderInfo = ref({})
const addressList = ref([])
const selectedAddr = ref(null)
const showAddressDialog = ref(false)

onMounted(async () => {
  await Promise.all([fetchOrderInfo(), fetchAddressList()])
})

const fetchOrderInfo = async () => {
  const basketIds = route.query.basketIds?.split(',').map(Number) || []
  const { data } = await confirmOrder({ basketIds, orderItem: [] })
  orderInfo.value = data || {}
  loading.value = false
}

const fetchAddressList = async () => {
  const { data } = await getAddressList()
  addressList.value = data || []
  if (data?.length > 0) {
    selectedAddr.value = data.find(a => a.commonAddr === 1) || data[0]
  }
}

const selectAddress = (addr) => {
  selectedAddr.value = addr
  showAddressDialog.value = false
}

const handleSubmit = async () => {
  if (!selectedAddr.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  submitting.value = true
  try {
    await confirmOrder({
      basketIds: route.query.basketIds?.split(',').map(Number) || [],
      orderItem: [],
      addrId: selectedAddr.value.addrId
    })
    const { data } = await submitOrder({ orderShopParam: [] })
    ElMessage.success('订单提交成功')
    router.push({ path: '/order/pay-result', query: { orderNumbers: data.orderNumbers } })
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.order-confirm {
  background: #fff;
  padding: 20px;
  h2 { margin-bottom: 20px; }
}
.section {
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #eee;
  h3 { margin-bottom: 15px; }
}
.address-info {
  p { margin-bottom: 5px; }
}
.shop-card {
  border: 1px solid #eee;
  margin-bottom: 15px;
}
.shop-header {
  padding: 10px 15px;
  background: #f5f5f5;
}
.goods-item {
  display: flex;
  align-items: center;
  padding: 15px;
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
.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  &.total {
    border-top: 1px solid #eee;
    font-weight: bold;
    .price { font-size: 20px; color: #e4393c; }
  }
}
.submit-bar {
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  padding: 15px 20px;
  background: #fff;
  border-top: 1px solid #eee;
  em { font-size: 24px; color: #e4393c; font-style: normal; }
}
.address-item {
  padding: 15px;
  border: 1px solid #eee;
  margin-bottom: 10px;
  cursor: pointer;
  &:hover { border-color: #3a86b9; }
}
</style>
