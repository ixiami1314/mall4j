<template>
  <div class="prod-detail container" v-loading="loading">
    <div class="prod-main">
      <div class="prod-gallery">
        <el-image :src="prodInfo.pic" fit="contain" class="main-image" />
      </div>
      <div class="prod-info">
        <h1 class="prod-name">{{ prodInfo.prodName }}</h1>
        <p class="prod-brief">{{ prodInfo.brief }}</p>
        <div class="prod-price-box">
          <span class="label">价格</span>
          <span class="price">¥{{ selectedSku?.price || prodInfo.price }}</span>
          <span v-if="prodInfo.oriPrice" class="ori-price">¥{{ prodInfo.oriPrice }}</span>
        </div>

        <!-- SKU 选择 -->
        <div v-if="prodInfo.skuList?.length" class="sku-section">
          <div v-for="sku in skuGroups" :key="sku.name" class="sku-row">
            <span class="sku-label">{{ sku.name }}</span>
            <div class="sku-options">
              <span
                v-for="val in sku.values"
                :key="val"
                :class="['sku-option', { active: selectedSkus[sku.name] === val }]"
                @click="selectSku(sku.name, val)"
              >
                {{ val }}
              </span>
            </div>
          </div>
        </div>

        <div class="quantity-row">
          <span class="label">数量</span>
          <el-input-number v-model="quantity" :min="1" :max="selectedSku?.stocks || 99" />
          <span class="stocks">库存: {{ selectedSku?.stocks || prodInfo.stocks || 0 }}</span>
        </div>

        <div class="action-row">
          <el-button type="primary" size="large" @click="handleAddToCart">加入购物车</el-button>
          <el-button type="danger" size="large" @click="handleBuyNow">立即购买</el-button>
        </div>
      </div>
    </div>

    <div class="prod-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品详情" name="detail">
          <div class="detail-content" v-html="prodInfo.content"></div>
        </el-tab-pane>
        <el-tab-pane label="商品评价" name="comment">
          <div v-if="comments.length === 0" class="empty-comment">
            <el-empty description="暂无评价" />
          </div>
          <div v-else class="comment-list">
            <div v-for="comm in comments" :key="comm.id" class="comment-item">
              <div class="comm-header">
                <span class="user">{{ comm.nickName }}</span>
                <span class="time">{{ comm.createTime }}</span>
              </div>
              <p class="comm-content">{{ comm.content }}</p>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProdInfo, getProdCommPage } from '@/api/prod'
import { changeCartItem } from '@/api/cart'
import { useCartStore } from '@/stores/cart'
import { isLogin } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const prodInfo = ref({})
const quantity = ref(1)
const activeTab = ref('detail')
const comments = ref([])
const selectedSkus = ref({})

const skuGroups = computed(() => {
  if (!prodInfo.value.skuList?.length) return []
  const groups = {}
  prodInfo.value.skuList.forEach(sku => {
    sku.properties?.forEach(prop => {
      if (!groups[prop.propertyName]) {
        groups[prop.propertyName] = new Set()
      }
      groups[prop.propertyName].add(prop.propertyValueName)
    })
  })
  return Object.entries(groups).map(([name, values]) => ({
    name,
    values: Array.from(values)
  }))
})

const selectedSku = computed(() => {
  if (!prodInfo.value.skuList?.length) return null
  const selectedCount = Object.keys(selectedSkus.value).length
  if (selectedCount !== skuGroups.value.length) return null

  return prodInfo.value.skuList.find(sku => {
    return sku.properties?.every(prop =>
      selectedSkus.value[prop.propertyName] === prop.propertyValueName
    )
  })
})

onMounted(() => {
  fetchProdInfo()
})

watch(() => route.params.id, () => {
  fetchProdInfo()
})

const fetchProdInfo = async () => {
  loading.value = true
  const prodId = route.params.id
  const { data } = await getProdInfo(prodId)
  prodInfo.value = data || {}
  fetchComments(prodId)
  loading.value = false
}

const fetchComments = async (prodId) => {
  const { data } = await getProdCommPage(prodId, { current: 1, size: 10 })
  comments.value = data?.records || []
}

const selectSku = (name, value) => {
  selectedSkus.value[name] = value
}

const handleAddToCart = async () => {
  if (!isLogin()) {
    ElMessageBox.confirm('请先登录', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => router.push('/login'))
    return
  }

  if (prodInfo.value.skuList?.length && !selectedSku.value) {
    ElMessage.warning('请选择商品规格')
    return
  }

  await changeCartItem({
    count: quantity.value,
    prodId: prodInfo.value.prodId,
    skuId: selectedSku.value?.skuId || prodInfo.value.skuList?.[0]?.skuId,
    shopId: prodInfo.value.shopId
  })

  cartStore.fetchCartCount()
  ElMessage.success('已添加到购物车')
}

const handleBuyNow = async () => {
  if (!isLogin()) {
    router.push('/login')
    return
  }

  if (prodInfo.value.skuList?.length && !selectedSku.value) {
    ElMessage.warning('请选择商品规格')
    return
  }

  await changeCartItem({
    count: quantity.value,
    prodId: prodInfo.value.prodId,
    skuId: selectedSku.value?.skuId || prodInfo.value.skuList?.[0]?.skuId,
    shopId: prodInfo.value.shopId
  })

  router.push('/cart')
}
</script>

<style lang="scss" scoped>
.prod-detail {
  background: #fff;
  padding: 20px;
}
.prod-main {
  display: flex;
  gap: 40px;
  margin-bottom: 30px;
}
.prod-gallery {
  width: 450px;
  .main-image {
    width: 100%;
    height: 450px;
    border: 1px solid #eee;
  }
}
.prod-info {
  flex: 1;
  .prod-name {
    font-size: 20px;
    font-weight: normal;
    margin-bottom: 10px;
  }
  .prod-brief {
    color: #999;
    margin-bottom: 20px;
  }
  .prod-price-box {
    background: #f5f5f5;
    padding: 15px;
    margin-bottom: 20px;
    .label { color: #999; margin-right: 20px; }
    .price {
      font-size: 28px;
      color: #e4393c;
      font-weight: bold;
    }
    .ori-price {
      color: #999;
      text-decoration: line-through;
      margin-left: 10px;
    }
  }
}
.sku-section {
  margin-bottom: 20px;
}
.sku-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  .sku-label {
    width: 60px;
    color: #999;
  }
  .sku-options {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  .sku-option {
    padding: 5px 15px;
    border: 1px solid #ddd;
    cursor: pointer;
    &:hover { border-color: #3a86b9; }
    &.active {
      border-color: #3a86b9;
      color: #3a86b9;
    }
  }
}
.quantity-row {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  .label {
    width: 60px;
    color: #999;
  }
  .stocks {
    margin-left: 15px;
    color: #999;
    font-size: 12px;
  }
}
.action-row {
  display: flex;
  gap: 15px;
}
.prod-tabs {
  margin-top: 30px;
  .detail-content {
    padding: 20px;
    min-height: 300px;
  }
}
.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  .comm-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    .user { font-weight: bold; }
    .time { color: #999; font-size: 12px; }
  }
  .comm-content { color: #666; }
}
</style>
