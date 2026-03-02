<template>
  <div class="cart-page container">
    <h2>我的购物车</h2>
    <div v-if="cartItems.length === 0" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="$router.push('/')">去购物</el-button>
      </el-empty>
    </div>
    <div v-else class="cart-content">
      <div v-for="shop in cartItems" :key="shop.shopId" class="shop-card">
        <div class="shop-header">
          <el-checkbox v-model="shop.checked" @change="handleShopCheck(shop)" />
          <span class="shop-name">{{ shop.shopName }}</span>
        </div>
        <div v-for="discount in shop.shopCartItemDiscounts" :key="discount.discountId">
          <div v-for="item in discount.shopCartItems" :key="item.basketId" class="cart-item">
            <el-checkbox v-model="item.checked" @change="handleItemCheck" />
            <img :src="item.pic" class="item-img" @click="goProd(item.prodId)" />
            <div class="item-info">
              <p class="item-name" @click="goProd(item.prodId)">{{ item.prodName }}</p>
              <p class="item-sku">{{ item.skuName }}</p>
            </div>
            <div class="item-price">¥{{ item.price }}</div>
            <div class="item-count">
              <el-input-number v-model="item.prodCount" :min="1" :max="item.stocks" size="small" @change="handleCountChange(item)" />
            </div>
            <div class="item-total">¥{{ (item.price * item.prodCount).toFixed(2) }}</div>
            <el-button type="danger" link @click="handleDelete(item.basketId)">删除</el-button>
          </div>
        </div>
      </div>

      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="allChecked" @change="handleAllCheck">全选</el-checkbox>
          <el-button type="danger" link @click="handleDeleteSelected">删除选中</el-button>
        </div>
        <div class="footer-right">
          <span>已选 {{ selectedCount }} 件商品</span>
          <span class="total-price">合计: <em>¥{{ totalPrice }}</em></span>
          <el-button type="primary" :disabled="selectedCount === 0" @click="handleCheckout">去结算</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartInfo, changeCartItem, deleteCartItem } from '@/api/cart'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const cartItems = ref([])
const allChecked = ref(false)

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

onMounted(() => {
  fetchCart()
})

const fetchCart = async () => {
  const { data } = await getCartInfo()
  cartItems.value = (data || []).map(shop => ({
    ...shop,
    checked: false,
    shopCartItemDiscounts: shop.shopCartItemDiscounts?.map(discount => ({
      ...discount,
      shopCartItems: discount.shopCartItems?.map(item => ({ ...item, checked: false }))
    }))
  }))
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
  await changeCartItem({
    count: item.prodCount,
    prodId: item.prodId,
    skuId: item.skuId,
    shopId: item.shopId
  })
}

const handleDelete = async (basketId) => {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示')
  await deleteCartItem([basketId])
  fetchCart()
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
  await ElMessageBox.confirm('确定删除选中商品吗？', '提示')
  await deleteCartItem(ids)
  fetchCart()
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
  router.push({ path: '/order/confirm', query: { basketIds: ids.join(',') } })
}

const goProd = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
.cart-page {
  background: #fff;
  padding: 20px;
  min-height: 500px;
  h2 {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}
.shop-card {
  margin-bottom: 20px;
  border: 1px solid #eee;
}
.shop-header {
  padding: 10px 15px;
  background: #f5f5f5;
  .shop-name {
    margin-left: 10px;
    font-weight: bold;
  }
}
.cart-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  .item-img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    margin: 0 15px;
    cursor: pointer;
  }
  .item-info {
    flex: 1;
    .item-name {
      cursor: pointer;
      &:hover { color: #3a86b9; }
    }
    .item-sku {
      color: #999;
      font-size: 12px;
      margin-top: 5px;
    }
  }
  .item-price { width: 100px; text-align: center; }
  .item-count { width: 150px; text-align: center; }
  .item-total {
    width: 100px;
    text-align: center;
    color: #e4393c;
    font-weight: bold;
  }
}
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f5f5;
  .footer-right {
    display: flex;
    align-items: center;
    gap: 20px;
    .total-price em {
      font-size: 24px;
      color: #e4393c;
      font-style: normal;
    }
  }
}
</style>
