<template>
  <div class="cart-page">
    <div class="page-header">
      <h2>
        <el-icon><ShoppingCart /></el-icon>
        {{ t('cart.title') }}
      </h2>
      <span class="cart-count" v-if="cartItems.length">{{ t('cart.itemCount', { count: totalCount }) }}</span>
    </div>

    <div v-if="cartItems.length === 0" class="empty-cart">
      <el-empty :description="t('cart.empty')">
        <template #image>
          <el-icon :size="100" color="#ddd"><ShoppingCart /></el-icon>
        </template>
        <el-button type="primary" size="large" @click="$router.push('/')">{{ t('cart.goShopping') }}</el-button>
      </el-empty>
    </div>

    <div v-else class="cart-content">
      <!-- 购物车列表 -->
      <div class="cart-list">
        <div v-for="shop in cartItems" :key="shop.shopId" class="shop-card">
          <!-- 店铺头部 -->
          <div class="shop-header">
            <el-checkbox v-model="shop.checked" @change="handleShopCheck(shop)" />
            <div class="shop-info">
              <el-icon><Shop /></el-icon>
              <span class="shop-name">{{ shop.shopName }}</span>
            </div>
          </div>

          <!-- 商品列表 -->
          <div v-for="discount in shop.shopCartItemDiscounts" :key="discount.discountId" class="discount-section">
            <div v-for="item in discount.shopCartItems" :key="item.basketId" class="cart-item">
              <el-checkbox v-model="item.checked" @change="handleItemCheck" />

              <div class="item-image" @click="goProd(item.prodId)">
                <img :src="item.pic" :alt="item.prodName" />
              </div>

              <div class="item-info">
                <p class="item-name" @click="goProd(item.prodId)">{{ item.prodName }}</p>
                <p class="item-sku" v-if="item.skuName">
                  <el-tag size="small" type="info">{{ item.skuName }}</el-tag>
                </p>
              </div>

              <div class="item-price">
                <span class="price">¥{{ item.price }}</span>
              </div>

              <div class="item-count">
                <el-input-number
                  v-model="item.prodCount"
                  :min="1"
                  :max="item.stocks || 99"
                  size="small"
                  @change="handleCountChange(item)"
                />
              </div>

              <div class="item-total">
                <span class="total">¥{{ (item.price * item.prodCount).toFixed(2) }}</span>
              </div>

              <div class="item-actions">
                <el-button type="danger" link @click="handleDelete(item.basketId)">
                  <el-icon><Delete /></el-icon>
                  {{ t('common.delete') }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 结算区域 -->
      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="allChecked" @change="handleAllCheck">{{ t('cart.selectAll') }}</el-checkbox>
          <el-button type="danger" link @click="handleDeleteSelected" :disabled="!hasSelected">
            <el-icon><Delete /></el-icon>
            {{ t('cart.deleteSelected') }}
          </el-button>
          <el-button type="warning" link @click="handleClearCart">
            <el-icon><DeleteFilled /></el-icon>
            {{ t('cart.clearCart') }}
          </el-button>
        </div>

        <div class="footer-right">
          <div class="price-summary">
            <span class="selected-count">{{ t('cart.selected', { count: selectedCount }) }}</span>
            <span class="total-label">{{ t('cart.total') }}</span>
            <span class="total-price">¥<em>{{ totalPrice }}</em></span>
          </div>
          <el-button
            type="danger"
            size="large"
            class="checkout-btn"
            :disabled="selectedCount === 0"
            @click="handleCheckout"
          >
            {{ t('cart.checkout') }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 推荐商品 -->
    <div class="recommend-section" v-if="recommendProds.length">
      <h3>{{ t('cart.recommend') }}</h3>
      <div class="recommend-grid">
        <div
          v-for="prod in recommendProds"
          :key="prod.prodId"
          class="recommend-item"
          @click="goProd(prod.prodId)"
        >
          <img :src="prod.pic" />
          <p class="rec-name">{{ prod.prodName }}</p>
          <p class="rec-price">¥{{ prod.price }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
import { getCartInfo, changeCartItem, deleteCartItem, clearCart } from '@/api/cart'
import { ElMessageBox, ElMessage } from 'element-plus'
import { ShoppingCart, Shop, Delete, DeleteFilled } from '@element-plus/icons-vue'

const router = useRouter()
const cartItems = ref([])
const allChecked = ref(false)
const recommendProds = ref([])

// 计算总商品数量
const totalCount = computed(() => {
  let count = 0
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      count += discount.shopCartItems?.length || 0
    })
  })
  return count
})

// 计算选中商品数量
const selectedCount = computed(() => {
  let count = 0
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) count += item.prodCount
      })
    })
  })
  return count
})

// 计算选中商品总价
const totalPrice = computed(() => {
  let total = 0
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) total += item.price * item.prodCount
      })
    })
  })
  return total.toFixed(2)
})

// 是否有选中商品
const hasSelected = computed(() => selectedCount.value > 0)

onMounted(() => {
  fetchCart()
})

const fetchCart = async () => {
  try {
    const { data } = await getCartInfo()
    cartItems.value = (data || []).map(shop => ({
      ...shop,
      checked: false,
      shopCartItemDiscounts: shop.shopCartItemDiscounts?.map(discount => ({
        ...discount,
        shopCartItems: discount.shopCartItems?.map(item => ({ ...item, checked: false }))
      }))
    }))
  } catch (error) {
    console.error('获取购物车失败:', error)
  }
}

const handleShopCheck = (shop) => {
  shop.shopCartItemDiscounts?.forEach(discount => {
    discount.shopCartItems?.forEach(item => {
      item.checked = shop.checked
    })
  })
  updateAllChecked()
}

const handleItemCheck = () => {
  updateAllChecked()
}

const updateAllChecked = () => {
  let allItems = []
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      allItems = allItems.concat(discount.shopCartItems || [])
    })
  })
  allChecked.value = allItems.length > 0 && allItems.every(item => item.checked)
}

const handleAllCheck = (val) => {
  cartItems.value.forEach(shop => {
    shop.checked = val
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        item.checked = val
      })
    })
  })
}

const handleCountChange = async (item) => {
  try {
    await changeCartItem({
      count: item.prodCount,
      prodId: item.prodId,
      skuId: item.skuId,
      shopId: item.shopId
    })
  } catch (error) {
    // 重新获取购物车数据
    fetchCart()
  }
}

const handleDelete = async (basketId) => {
  try {
    await ElMessageBox.confirm(t('cart.confirmDelete'), t('cart.confirmDeleteTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    await deleteCartItem([basketId])
    ElMessage.success(t('cart.deleteSuccess'))
    fetchCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleDeleteSelected = async () => {
  const ids = []
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) ids.push(item.basketId)
      })
    })
  })
  if (ids.length === 0) return

  try {
    await ElMessageBox.confirm(t('cart.confirmDeleteSelected', { count: ids.length }), t('cart.confirmDeleteTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    await deleteCartItem(ids)
    ElMessage.success(t('cart.deleteSuccess'))
    fetchCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm(t('cart.confirmClear'), t('cart.confirmClearTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    await clearCart()
    ElMessage.success(t('cart.clearSuccess'))
    fetchCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
    }
  }
}

const handleCheckout = () => {
  const ids = []
  cartItems.value.forEach(shop => {
    shop.shopCartItemDiscounts?.forEach(discount => {
      discount.shopCartItems?.forEach(item => {
        if (item.checked) ids.push(item.basketId)
      })
    })
  })
  if (ids.length === 0) {
    ElMessage.warning(t('cart.selectItems'))
    return
  }
  router.push({ path: '/order/confirm', query: { basketIds: ids.join(',') } })
}

const goProd = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
// 缓动函数
$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

.cart-page {
  max-width: 1200px;
  margin: 24px auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  animation: fadeIn 0.5s $ease-out-expo;

  h2 {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 24px;
    font-weight: 700;
    color: #1a1a1a;
    font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;

    .el-icon {
      color: #ff6700;
    }
  }

  .cart-count {
    color: #666;
    font-size: 14px;
    font-weight: 450;
    padding: 6px 16px;
    background: rgba(255, 103, 0, 0.06);
    border-radius: 20px;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.empty-cart {
  background: #fff;
  border-radius: 24px;
  padding: 100px 0;
  text-align: center;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.cart-content {
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

// 店铺卡片
.shop-card {
  margin-bottom: 0;

  &:not(:last-child) {
    border-bottom: 1px solid #f0ede8;
  }
}

.shop-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #faf9f7 0%, #f5f3f0 100%);
  border-bottom: 1px solid #f0ede8;
}

.shop-info {
  display: flex;
  align-items: center;
  gap: 10px;

  .el-icon {
    color: #ff6700;
    font-size: 18px;
  }
}

.shop-name {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 15px;
}

// 商品项
.discount-section {
  padding: 0 24px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 24px 0;
  border-bottom: 1px solid #f5f3f0;
  gap: 20px;
  transition: background 0.25s $ease-out-expo;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: rgba(255, 103, 0, 0.02);
    margin: 0 -24px;
    padding-left: 24px;
    padding-right: 24px;
  }
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s $ease-out-expo;
  }

  &:hover img {
    transform: scale(1.08);
  }
}

.item-info {
  flex: 1;
  min-width: 0;

  .item-name {
    font-size: 15px;
    color: #333;
    cursor: pointer;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    line-height: 1.5;
    font-weight: 450;
    transition: color 0.25s;

    &:hover {
      color: #ff6700;
    }
  }

  .item-sku {
    margin-top: 10px;
  }
}

.item-price {
  width: 100px;
  text-align: center;

  .price {
    font-size: 16px;
    color: #666;
    font-weight: 450;
  }
}

.item-count {
  width: 140px;
  text-align: center;

  :deep(.el-input-number) {
    width: 120px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    border-color: #e8e4de;

    &:hover {
      border-color: #d4cfc6;
    }
  }
}

.item-total {
  width: 120px;
  text-align: center;

  .total {
    font-size: 20px;
    color: #e4393c;
    font-weight: 700;
    font-family: 'Space Grotesk', sans-serif;

    &::before {
      content: '¥';
      font-size: 14px;
    }
  }
}

.item-actions {
  width: 80px;
  text-align: center;

  .el-button {
    transition: all 0.25s $ease-out-expo;

    &:hover {
      color: #ff4757;
    }
  }
}

// 底部结算区
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  background: linear-gradient(135deg, #faf9f7 0%, #f5f3f0 100%);
  border-top: 1px solid #f0ede8;
  position: sticky;
  bottom: 0;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 28px;

  .el-button {
    font-weight: 450;
  }
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 28px;
}

.price-summary {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.selected-count {
  color: #666;
  font-size: 14px;
  font-weight: 450;

  em {
    color: #ff6700;
    font-weight: 600;
    font-style: normal;
    font-family: 'Space Grotesk', sans-serif;
  }
}

.total-label {
  color: #333;
  font-size: 14px;
  font-weight: 450;
}

.total-price {
  color: #e4393c;
  font-size: 14px;
  font-weight: 450;

  em {
    font-size: 32px;
    font-weight: 700;
    font-style: normal;
    font-family: 'Space Grotesk', sans-serif;
  }
}

.checkout-btn {
  width: 180px;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.02em;
  border-radius: 14px;
  background: linear-gradient(135deg, #e4393c 0%, #c41e24 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(228, 57, 60, 0.35);
  transition: all 0.35s $ease-out-expo;

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #ff4757 0%, #e4393c 100%);
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(228, 57, 60, 0.45);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

// 推荐商品
.recommend-section {
  margin-top: 32px;
  background: #fff;
  border-radius: 24px;
  padding: 28px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
  animation: fadeIn 0.6s $ease-out-expo 0.1s both;

  h3 {
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 24px;
    padding-left: 16px;
    border-left: 4px solid #ff6700;
    font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
    color: #1a1a1a;
  }
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

.recommend-item {
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.35s $ease-out-expo;
  border: 1px solid rgba(0, 0, 0, 0.04);

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.1);
    border-color: rgba(255, 103, 0, 0.15);

    img {
      transform: scale(1.05);
    }
  }

  img {
    width: 100%;
    height: 160px;
    object-fit: cover;
    transition: transform 0.4s $ease-out-expo;
  }

  .rec-name {
    padding: 12px;
    font-size: 13px;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 450;
  }

  .rec-price {
    padding: 0 12px 12px;
    font-size: 18px;
    color: #e4393c;
    font-weight: 700;
    font-family: 'Space Grotesk', sans-serif;

    &::before {
      content: '¥';
      font-size: 12px;
    }
  }
}

// 响应式
@media (max-width: 1200px) {
  .recommend-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .cart-page {
    margin: 16px auto;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .cart-item {
    flex-wrap: wrap;
    gap: 12px;
  }

  .item-info {
    width: calc(100% - 120px);
  }

  .item-price, .item-count, .item-total, .item-actions {
    width: auto;
    flex: 1;
    text-align: center;
  }

  .recommend-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
