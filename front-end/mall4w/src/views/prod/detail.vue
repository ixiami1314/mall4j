<template>
  <div class="prod-detail" v-loading="loading">
    <div class="prod-main">
      <!-- 商品图片区 -->
      <div class="prod-gallery">
        <div class="main-image-wrapper">
          <el-image
            :src="prodInfo.pic"
            :preview-src-list="[prodInfo.pic]"
            fit="contain"
            class="main-image"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
                <span>加载失败</span>
              </div>
            </template>
          </el-image>
        </div>
        <div class="image-thumbs" v-if="prodInfo.imgs?.length">
          <div
            v-for="(img, index) in prodInfo.imgs.slice(0, 5)"
            :key="index"
            :class="['thumb-item', { active: prodInfo.pic === img }]"
            @click="prodInfo.pic = img"
          >
            <img :src="img" />
          </div>
        </div>
      </div>

      <!-- 商品信息区 -->
      <div class="prod-info">
        <h1 class="prod-name">{{ prodInfo.prodName }}</h1>
        <p class="prod-brief">{{ prodInfo.brief }}</p>

        <div class="price-section">
          <div class="price-row">
            <span class="price-label">促销价</span>
            <span class="price-value">¥{{ selectedSku?.price || prodInfo.price }}</span>
            <span v-if="prodInfo.oriPrice" class="ori-price">¥{{ prodInfo.oriPrice }}</span>
          </div>
          <div class="price-tags" v-if="prodInfo.oriPrice">
            <el-tag size="small" type="danger" effect="plain">
              省 ¥{{ ((prodInfo.oriPrice - (selectedSku?.price || prodInfo.price)) || 0).toFixed(2) }}
            </el-tag>
          </div>
        </div>

        <!-- SKU 选择 -->
        <div v-if="prodInfo.skuList?.length" class="sku-section">
          <div v-for="sku in skuGroups" :key="sku.name" class="sku-row">
            <span class="sku-label">{{ sku.name }}</span>
            <div class="sku-options">
              <span
                v-for="val in sku.values"
                :key="val"
                :class="['sku-option', { active: selectedSkus[sku.name] === val, disabled: isSkuDisabled(sku.name, val) }]"
                @click="!isSkuDisabled(sku.name, val) && selectSku(sku.name, val)"
              >
                {{ val }}
              </span>
            </div>
          </div>
        </div>

        <div class="quantity-section">
          <span class="section-label">数量</span>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="selectedSku?.stocks || prodInfo.stocks || 99"
            size="large"
          />
          <span class="stocks-info">
            <el-icon><Box /></el-icon>
            库存 {{ selectedSku?.stocks || prodInfo.stocks || 0 }} 件
          </span>
        </div>

        <div class="action-section">
          <el-button type="primary" size="large" class="action-btn cart-btn" @click="handleAddToCart">
            <el-icon><ShoppingCart /></el-icon>
            加入购物车
          </el-button>
          <el-button type="danger" size="large" class="action-btn buy-btn" @click="handleBuyNow">
            <el-icon><Lightning /></el-icon>
            立即购买
          </el-button>
        </div>

        <!-- 服务保障 -->
        <div class="service-section">
          <div class="service-item">
            <el-icon><CircleCheck /></el-icon>
            <span>正品保证</span>
          </div>
          <div class="service-item">
            <el-icon><Van /></el-icon>
            <span>极速发货</span>
          </div>
          <div class="service-item">
            <el-icon><RefreshRight /></el-icon>
            <span>7天退换</span>
          </div>
          <div class="service-item">
            <el-icon><Service /></el-icon>
            <span>售后无忧</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情/评价 Tab -->
    <div class="prod-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品详情" name="detail">
          <div class="detail-content" v-html="prodInfo.content"></div>
        </el-tab-pane>
        <el-tab-pane :label="`商品评价 (${commentTotal})`" name="comment">
          <div v-if="comments.length === 0" class="empty-comment">
            <el-empty description="暂无评价" />
          </div>
          <div v-else class="comment-list">
            <div v-for="comm in comments" :key="comm.id" class="comment-item">
              <div class="comm-avatar">
                <el-avatar :size="40">{{ comm.nickName?.charAt(0) }}</el-avatar>
              </div>
              <div class="comm-content">
                <div class="comm-header">
                  <span class="user-name">{{ comm.nickName }}</span>
                  <el-rate v-model="comm.score" disabled size="small" v-if="comm.score" />
                  <span class="comm-time">{{ comm.createTime }}</span>
                </div>
                <p class="comm-text">{{ comm.content }}</p>
                <div class="comm-images" v-if="comm.pics?.length">
                  <el-image
                    v-for="(pic, idx) in comm.pics"
                    :key="idx"
                    :src="pic"
                    :preview-src-list="comm.pics"
                    fit="cover"
                    class="comm-img"
                  />
                </div>
              </div>
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
import { Picture, Box, ShoppingCart, Lightning, CircleCheck, Van, RefreshRight, Service } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const prodInfo = ref({})
const quantity = ref(1)
const activeTab = ref('detail')
const comments = ref([])
const commentTotal = ref(0)
const selectedSkus = ref({})

// 解析 SKU properties 字符串 (格式: "颜色:红色;尺码:XL")
const parseProperties = (propertiesStr) => {
  if (!propertiesStr) return []
  return propertiesStr.split(';').filter(Boolean).map(prop => {
    const [propertyName, propertyValueName] = prop.split(':')
    return { propertyName, propertyValueName }
  })
}

const skuGroups = computed(() => {
  if (!prodInfo.value.skuList?.length) return []
  const groups = {}
  prodInfo.value.skuList.forEach(sku => {
    const props = parseProperties(sku.properties)
    props.forEach(prop => {
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
    const props = parseProperties(sku.properties)
    return props.every(prop =>
      selectedSkus.value[prop.propertyName] === prop.propertyValueName
    )
  })
})

const isSkuDisabled = (skuName, value) => {
  // 检查选择该值后是否有可用库存
  const tempSelected = { ...selectedSkus.value, [skuName]: value }
  return !prodInfo.value.skuList?.some(sku => {
    const props = parseProperties(sku.properties)
    return props.every(prop =>
      tempSelected[prop.propertyName] === prop.propertyValueName
    ) && sku.stocks > 0
  })
}

onMounted(() => {
  fetchProdInfo()
})

watch(() => route.params.id, () => {
  fetchProdInfo()
})

const fetchProdInfo = async () => {
  loading.value = true
  const prodId = route.params.id
  try {
    const { data } = await getProdInfo(prodId)
    prodInfo.value = data || {}
    // 自动选择第一个 SKU
    if (prodInfo.value.skuList?.length) {
      const firstSku = prodInfo.value.skuList[0]
      const props = parseProperties(firstSku.properties)
      props.forEach(prop => {
        selectedSkus.value[prop.propertyName] = prop.propertyValueName
      })
    }
    fetchComments(prodId)
  } catch (error) {
    ElMessage.error('获取商品信息失败')
  } finally {
    loading.value = false
  }
}

const fetchComments = async (prodId) => {
  try {
    const { data } = await getProdCommPage(prodId, { current: 1, size: 10 })
    comments.value = data?.records || []
    commentTotal.value = data?.total || 0
  } catch (error) {
    console.error('获取评论失败:', error)
  }
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

  try {
    await changeCartItem({
      count: quantity.value,
      prodId: prodInfo.value.prodId,
      skuId: selectedSku.value?.skuId || prodInfo.value.skuList?.[0]?.skuId,
      shopId: prodInfo.value.shopId
    })
    cartStore.fetchCartCount()
    ElMessage.success('已添加到购物车')
  } catch (error) {
    // 错误已在 http 拦截器处理
  }
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

  try {
    await changeCartItem({
      count: quantity.value,
      prodId: prodInfo.value.prodId,
      skuId: selectedSku.value?.skuId || prodInfo.value.skuList?.[0]?.skuId,
      shopId: prodInfo.value.shopId
    })
    router.push('/cart')
  } catch (error) {
    // 错误已在 http 拦截器处理
  }
}
</script>

<style lang="scss" scoped>
.prod-detail {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin: 20px auto;
}

.prod-main {
  display: flex;
  gap: 40px;
  margin-bottom: 30px;
}

// 商品图片区
.prod-gallery {
  width: 480px;
  flex-shrink: 0;
  .main-image-wrapper {
    border-radius: 12px;
    overflow: hidden;
    background: #f8f9fa;
    border: 1px solid #eee;
  }
  .main-image {
    width: 100%;
    height: 480px;
  }
  .image-error {
    width: 100%;
    height: 480px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;
    .el-icon {
      font-size: 48px;
      margin-bottom: 10px;
    }
  }
  .image-thumbs {
    display: flex;
    gap: 10px;
    margin-top: 12px;
  }
  .thumb-item {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.2s;
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    &:hover, &.active {
      border-color: #3a86b9;
    }
  }
}

// 商品信息区
.prod-info {
  flex: 1;
  .prod-name {
    font-size: 24px;
    font-weight: 600;
    color: #1a1a1a;
    line-height: 1.4;
    margin-bottom: 12px;
  }
  .prod-brief {
    color: #666;
    font-size: 14px;
    margin-bottom: 20px;
    line-height: 1.6;
  }
}

// 价格区域
.price-section {
  background: linear-gradient(135deg, #fff5f5 0%, #fff8f8 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  border: 1px solid #ffe4e4;
}
.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.price-label {
  color: #999;
  font-size: 14px;
}
.price-value {
  font-size: 32px;
  color: #e4393c;
  font-weight: 700;
}
.ori-price {
  color: #999;
  font-size: 16px;
  text-decoration: line-through;
}
.price-tags {
  margin-top: 10px;
}

// SKU 选择
.sku-section {
  margin-bottom: 24px;
}
.sku-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}
.sku-label {
  width: 60px;
  color: #666;
  font-size: 14px;
  padding-top: 8px;
  flex-shrink: 0;
}
.sku-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.sku-option {
  padding: 8px 20px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  &:hover:not(.disabled) {
    border-color: #3a86b9;
    color: #3a86b9;
  }
  &.active {
    border-color: #3a86b9;
    background: #3a86b9;
    color: #fff;
  }
  &.disabled {
    background: #f5f5f5;
    color: #ccc;
    cursor: not-allowed;
    text-decoration: line-through;
  }
}

// 数量选择
.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  .section-label {
    width: 60px;
    color: #666;
    font-size: 14px;
  }
  .stocks-info {
    margin-left: 20px;
    color: #999;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

// 操作按钮
.action-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.action-btn {
  flex: 1;
  height: 52px;
  font-size: 16px;
  border-radius: 26px;
  font-weight: 500;
}
.cart-btn {
  background: linear-gradient(135deg, #ff9500 0%, #ff6b00 100%);
  border: none;
  &:hover {
    background: linear-gradient(135deg, #ff8c00 0%, #ff5f00 100%);
  }
}
.buy-btn {
  background: linear-gradient(135deg, #e4393c 0%, #c41e24 100%);
  border: none;
  &:hover {
    background: linear-gradient(135deg, #d63031 0%, #b71c1c 100%);
  }
}

// 服务保障
.service-section {
  display: flex;
  gap: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
.service-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  font-size: 13px;
  .el-icon {
    color: #3a86b9;
  }
}

// Tab 区域
.prod-tabs {
  margin-top: 30px;
  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
  }
  :deep(.el-tabs__item) {
    font-size: 16px;
    padding: 0 24px;
  }
  :deep(.el-tabs__item.is-active) {
    font-weight: 600;
  }
}
.detail-content {
  padding: 24px 0;
  min-height: 300px;
  line-height: 1.8;
  :deep(img) {
    max-width: 100%;
    display: block;
    margin: 16px auto;
    border-radius: 8px;
  }
}

// 评论区域
.comment-list {
  padding: 20px 0;
}
.comment-item {
  display: flex;
  gap: 16px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
  &:last-child {
    border-bottom: none;
  }
}
.comm-avatar {
  flex-shrink: 0;
}
.comm-content {
  flex: 1;
}
.comm-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.user-name {
  font-weight: 500;
  color: #333;
}
.comm-time {
  color: #999;
  font-size: 12px;
  margin-left: auto;
}
.comm-text {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}
.comm-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.comm-img {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  cursor: pointer;
}

.empty-comment {
  padding: 60px 0;
}
</style>
