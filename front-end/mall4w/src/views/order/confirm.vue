<template>
  <div class="order-confirm container" v-loading="loading">
    <h2>{{ t('order.confirm.title') }}</h2>

    <!-- 收货地址 -->
    <div class="section address-section">
      <h3>{{ t('order.confirm.shippingInfo') }}</h3>
      <div v-if="!selectedAddr" class="no-address">
        <el-button type="primary" @click="showAddressDialog = true">{{ t('order.confirm.selectAddress') }}</el-button>
      </div>
      <div v-else class="address-info">
        <p><strong>{{ selectedAddr.receiver }}</strong> {{ selectedAddr.mobile }}</p>
        <p>{{ selectedAddr.province }}{{ selectedAddr.city }}{{ selectedAddr.area }}{{ selectedAddr.addr }}</p>
        <el-button link @click="showAddressDialog = true">{{ t('common.modify') }}</el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="section goods-section">
      <h3>{{ t('order.confirm.productList') }}</h3>
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
        <span>{{ t('order.confirm.productTotal') }}</span>
        <span>¥{{ orderInfo.total }}</span>
      </div>
      <div class="summary-row">
        <span>{{ t('order.confirm.discount') }}</span>
        <span>-¥{{ orderInfo.orderReduce || 0 }}</span>
      </div>
      <div class="summary-row total">
        <span>{{ t('order.confirm.payAmount') }}</span>
        <span class="price">¥{{ orderInfo.actualTotal }}</span>
      </div>
    </div>

    <!-- 提交栏 -->
    <div class="submit-bar">
      <span>{{ t('order.confirm.payAmountLabel') }}<em>¥{{ orderInfo.actualTotal }}</em></span>
      <el-button type="danger" size="large" :loading="submitting" @click="handleSubmit">{{ t('order.confirm.submit') }}</el-button>
    </div>

    <!-- 地址选择弹窗 -->
    <el-dialog v-model="showAddressDialog" :title="t('order.confirm.selectAddress')" width="600px">
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
import { useI18n } from 'vue-i18n'
import { confirmOrder, submitOrder } from '@/api/order'
import { getAddressList } from '@/api/address'
import { ElMessage } from 'element-plus'

const { t } = useI18n()

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
    ElMessage.warning(t('order.confirm.selectAddressHint'))
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
    ElMessage.success(t('order.confirm.submitSuccess'))
    router.push({ path: '/order/pay-result', query: { orderNumbers: data.orderNumbers } })
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
// 颜色变量 - 淡蓝色系
$primary: #4A9FD4;
$primary-light: #6BB8E8;
$primary-dark: #3A8BC4;
$primary-bg: #F0F7FC;

$danger: #DC2626;

$text-primary: #1E293B;
$text-regular: #334155;
$text-secondary: #64748B;
$text-tertiary: #94A3B8;

$bg-page: #F8FAFC;
$bg-secondary: #F1F5F9;

$border-light: #E2E8F0;

$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

.order-confirm {
  background: #fff;
  padding: 24px;
  border-radius: 20px;
  margin: 20px auto;
  box-shadow: 0 1px 16px rgba(0, 0, 0, 0.03);

  h2 {
    margin-bottom: 24px;
    font-size: 22px;
    font-weight: 600;
    color: $text-primary;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
  }
}

.section {
  margin-bottom: 24px;
  padding: 22px;
  border: 1px solid $border-light;
  border-radius: 16px;

  h3 {
    margin-bottom: 18px;
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
  }
}

.address-info {
  p {
    margin-bottom: 6px;
    color: $text-regular;
    font-size: 14px;
  }

  strong {
    font-weight: 600;
  }
}

.no-address {
  text-align: center;
  padding: 20px;
}

.shop-card {
  border: 1px solid $border-light;
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
}

.shop-header {
  padding: 12px 18px;
  background: $bg-page;
  font-weight: 500;
  color: $text-primary;
  font-size: 14px;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 16px 18px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);

  &:last-child {
    border-bottom: none;
  }

  .goods-img {
    width: 72px;
    height: 72px;
    object-fit: cover;
    margin-right: 16px;
    border-radius: 10px;
  }

  .goods-info {
    flex: 1;
  }

  .goods-name {
    font-size: 14px;
    color: $text-regular;
    font-weight: 400;
  }

  .goods-sku {
    color: $text-tertiary;
    font-size: 12px;
    margin-top: 6px;
  }

  .goods-price {
    width: 90px;
    text-align: center;
    font-size: 14px;
    color: $text-regular;
  }

  .goods-count {
    width: 70px;
    text-align: center;
    font-size: 13px;
    color: $text-secondary;
  }
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  font-size: 14px;
  color: $text-secondary;

  &.total {
    border-top: 1px solid $border-light;
    margin-top: 8px;
    padding-top: 16px;
    font-weight: 600;
    color: $text-primary;

    .price {
      font-size: 22px;
      color: $danger;
      font-family: 'Outfit', sans-serif;
    }
  }
}

.submit-bar {
  position: sticky;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  padding: 20px 24px;
  background: #fff;
  border-top: 1px solid $border-light;
  border-radius: 0 0 20px 20px;
  margin: 0 -24px -24px;

  span {
    font-size: 14px;
    color: $text-secondary;
  }

  em {
    font-size: 26px;
    color: $danger;
    font-style: normal;
    font-family: 'Outfit', sans-serif;
    font-weight: 700;
    margin-left: 8px;
  }

  :deep(.el-button) {
    width: 160px;
    height: 48px;
    font-size: 15px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, $danger 0%, #B91C1C 100%);
    border: none;
    box-shadow: 0 4px 14px rgba(220, 38, 38, 0.28);
    transition: all 0.25s $ease-out-expo;

    &:hover {
      box-shadow: 0 6px 20px rgba(220, 38, 38, 0.35);
      transform: translateY(-1px);
    }
  }
}

.address-item {
  padding: 16px;
  border: 1px solid $border-light;
  margin-bottom: 12px;
  cursor: pointer;
  border-radius: 10px;
  transition: all 0.2s;

  &:hover {
    border-color: $primary;
    background: $primary-bg;
  }

  p {
    margin-bottom: 4px;
    font-size: 14px;
    color: $text-regular;
  }

  strong {
    font-weight: 600;
  }
}
</style>
