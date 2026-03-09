<template>
  <div class="cart-page">
    <div class="page-header">
      <h2>
        <el-icon><ShoppingCart /></el-icon>
        我的购物车
      </h2>
      <span class="cart-count" v-if="cartItems.length">共 {{ totalCount }} 件商品</span>
    </div>

    <div v-if="cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车还是空的，快去挑选心仪的商品吧~">
        <template #image>
          <el-icon :size="100" color="#ddd"><ShoppingCart /></el-icon>
        </template>
        <el-button type="primary" size="large" @click="$router.push('/')">去购物</el-button>
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
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 结算区域 -->
      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="allChecked" @change="handleAllCheck">全选</el-checkbox>
          <el-button type="danger" link @click="handleDeleteSelected" :disabled="!hasSelected">
            <el-icon><Delete /></el-icon>
            删除选中
          </el-button>
          <el-button type="warning" link @click="handleClearCart">
            <el-icon><DeleteFilled /></el-icon>
            清空购物车
          </el-button>
        </div>

        <div class="footer-right">
          <div class="price-summary">
            <span class="selected-count">已选 <em>{{ selectedCount }}</em> 件</span>
            <span class="total-label">合计：</span>
            <span class="total-price">¥<em>{{ totalPrice }}</em></span>
          </div>
          <el-button
            type="danger"
            size="large"
            class="checkout-btn"
            :disabled="selectedCount === 0"
            @click="handleCheckout"
          >
            去结算
          </el-button>
        </div>
      </div>
    </div>

    <!-- 推荐商品 -->
    <div class="recommend-section" v-if="recommendProds.length">
      <h3>猜你喜欢</h3>
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
    await ElMessageBox.confirm('确定要删除该商品吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCartItem([basketId])
    ElMessage.success('删除成功')
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
    await ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 件商品吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCartItem(ids)
    ElMessage.success('删除成功')
    fetchCart()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？此操作不可恢复。', '清空购物车', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearCart()
    ElMessage.success('购物车已清空')
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
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push({ path: '/order/confirm', query: { basketIds: ids.join(',') } })
}

const goProd = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
.cart-page {
  max-width: 1200px;
  margin: 20px auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 22px;
    font-weight: 600;
    color: #1a1a1a;
    .el-icon {
      color: #3a86b9;
    }
  }
  .cart-count {
    color: #666;
    font-size: 14px;
  }
}

.empty-cart {
  background: #fff;
  border-radius: 12px;
  padding: 80px 0;
  text-align: center;
}

.cart-content {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

// 店铺卡片
.shop-card {
  margin-bottom: 20px;
  &:last-child {
    margin-bottom: 0;
  }
}
.shop-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #f0f2f5 100%);
  border-bottom: 1px solid #eee;
}
.shop-info {
  display: flex;
  align-items: center;
  gap: 8px;
  .el-icon {
    color: #3a86b9;
  }
}
.shop-name {
  font-weight: 600;
  color: #333;
}

// 商品项
.discount-section {
  padding: 0 20px;
}
.cart-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 16px;
  &:last-child {
    border-bottom: none;
  }
}
.item-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
  }
  &:hover img {
    transform: scale(1.05);
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
    &:hover {
      color: #3a86b9;
    }
  }
  .item-sku {
    margin-top: 8px;
  }
}
.item-price {
  width: 100px;
  text-align: center;
  .price {
    font-size: 16px;
    color: #666;
  }
}
.item-count {
  width: 140px;
  text-align: center;
}
.item-total {
  width: 120px;
  text-align: center;
  .total {
    font-size: 18px;
    color: #e4393c;
    font-weight: 600;
  }
}
.item-actions {
  width: 80px;
  text-align: center;
}

// 底部结算区
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8f9fa 0%, #f0f2f5 100%);
  border-top: 1px solid #eee;
  position: sticky;
  bottom: 0;
}
.footer-left {
  display: flex;
  align-items: center;
  gap: 24px;
}
.footer-right {
  display: flex;
  align-items: center;
  gap: 24px;
}
.price-summary {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.selected-count {
  color: #666;
  font-size: 14px;
  em {
    color: #e4393c;
    font-weight: 600;
    font-style: normal;
  }
}
.total-label {
  color: #333;
  font-size: 14px;
}
.total-price {
  color: #e4393c;
  font-size: 14px;
  em {
    font-size: 28px;
    font-weight: 700;
    font-style: normal;
  }
}
.checkout-btn {
  width: 160px;
  height: 48px;
  font-size: 16px;
  border-radius: 24px;
  background: linear-gradient(135deg, #e4393c 0%, #c41e24 100%);
  border: none;
  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #d63031 0%, #b71c1c 100%);
  }
}

// 推荐商品
.recommend-section {
  margin-top: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  h3 {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 20px;
    padding-left: 12px;
    border-left: 4px solid #3a86b9;
  }
}
.recommend-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}
.recommend-item {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  }
  img {
    width: 100%;
    height: 160px;
    object-fit: cover;
  }
  .rec-name {
    padding: 10px;
    font-size: 13px;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .rec-price {
    padding: 0 10px 10px;
    font-size: 16px;
    color: #e4393c;
    font-weight: 600;
  }
}
</style>
