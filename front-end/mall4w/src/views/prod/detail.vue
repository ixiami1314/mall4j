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
                <span>{{ t('product.imageLoadFailed') }}</span>
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
            <span class="price-label">{{ t('product.promoPrice') }}</span>
            <span class="price-value">¥{{ selectedSku?.price || prodInfo.price }}</span>
            <span v-if="prodInfo.oriPrice" class="ori-price">¥{{ prodInfo.oriPrice }}</span>
          </div>
          <div class="price-tags" v-if="prodInfo.oriPrice">
            <el-tag size="small" type="danger" effect="plain">
              {{ t('product.save') }} ¥{{ ((prodInfo.oriPrice - (selectedSku?.price || prodInfo.price)) || 0).toFixed(2) }}
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
          <span class="section-label">{{ t('product.quantity') }}</span>
          <el-input-number
            v-model="quantity"
            :min="1"
            :max="selectedSku?.stocks || prodInfo.stocks || 99"
            size="large"
          />
          <span class="stocks-info">
            <el-icon><Box /></el-icon>
            {{ t('product.stock') }} {{ selectedSku?.stocks || prodInfo.stocks || 0 }} {{ t('product.items') }}
          </span>
        </div>

        <div class="action-section">
          <el-button type="primary" size="large" class="action-btn cart-btn" @click="handleAddToCart">
            <el-icon><ShoppingCart /></el-icon>
            {{ t('product.addToCart') }}
          </el-button>
          <el-button type="danger" size="large" class="action-btn buy-btn" @click="handleBuyNow">
            <el-icon><Lightning /></el-icon>
            {{ t('product.buyNow') }}
          </el-button>
        </div>

        <!-- 服务保障 -->
        <div class="service-section">
          <div class="service-item">
            <el-icon><CircleCheck /></el-icon>
            <span>{{ t('product.authenticGuarantee') }}</span>
          </div>
          <div class="service-item">
            <el-icon><Van /></el-icon>
            <span>{{ t('product.fastDelivery') }}</span>
          </div>
          <div class="service-item">
            <el-icon><RefreshRight /></el-icon>
            <span>{{ t('product.returnPolicy') }}</span>
          </div>
          <div class="service-item">
            <el-icon><Service /></el-icon>
            <span>{{ t('product.afterSales') }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情/评价 Tab -->
    <div class="prod-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="t('product.description')" name="detail">
          <div class="detail-content" v-html="prodInfo.content"></div>
        </el-tab-pane>
        <el-tab-pane :label="t('product.reviewsTab', { count: commentTotal })" name="comment">
          <div v-if="comments.length === 0" class="empty-comment">
            <el-empty :description="t('product.noReviews')" />
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
import { useI18n } from 'vue-i18n'
import { getProdInfo, getProdCommPage } from '@/api/prod'
import { changeCartItem } from '@/api/cart'
import { useCartStore } from '@/stores/cart'
import { isLogin } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, Box, ShoppingCart, Lightning, CircleCheck, Van, RefreshRight, Service } from '@element-plus/icons-vue'

const { t } = useI18n()
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
    ElMessage.error(t('product.getInfoFailed'))
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
    ElMessageBox.confirm(t('product.pleaseLogin'), t('common.tip'), {
      confirmButtonText: t('product.goLogin'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    }).then(() => router.push('/login'))
    return
  }

  if (prodInfo.value.skuList?.length && !selectedSku.value) {
    ElMessage.warning(t('product.selectSpec'))
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
    ElMessage.success(t('product.addedToCart'))
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
    ElMessage.warning(t('product.selectSpec'))
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
// 缓动函数
$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

.prod-detail {
  background: #fff;
  border-radius: 24px;
  padding: 32px;
  margin: 24px auto;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
  animation: fadeIn 0.5s $ease-out-expo;
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

.prod-main {
  display: flex;
  gap: 48px;
  margin-bottom: 40px;
}

// 商品图片区
.prod-gallery {
  width: 500px;
  flex-shrink: 0;

  .main-image-wrapper {
    border-radius: 20px;
    overflow: hidden;
    background: linear-gradient(135deg, #faf9f7 0%, #f5f3f0 100%);
    border: 1px solid #f0ede8;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  }

  .main-image {
    width: 100%;
    height: 500px;
  }

  .image-error {
    width: 100%;
    height: 500px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;

    .el-icon {
      font-size: 56px;
      margin-bottom: 12px;
      opacity: 0.5;
    }
  }

  .image-thumbs {
    display: flex;
    gap: 12px;
    margin-top: 16px;
  }

  .thumb-item {
    width: 80px;
    height: 80px;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.3s $ease-out-expo;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    &:hover, &.active {
      border-color: #ff6700;
      box-shadow: 0 4px 16px rgba(255, 103, 0, 0.2);
      transform: translateY(-2px);
    }
  }
}

// 商品信息区
.prod-info {
  flex: 1;

  .prod-name {
    font-size: 26px;
    font-weight: 700;
    color: #1a1a1a;
    line-height: 1.4;
    margin-bottom: 14px;
    font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.01em;
  }

  .prod-brief {
    color: #666;
    font-size: 15px;
    margin-bottom: 24px;
    line-height: 1.7;
    font-weight: 450;
  }
}

// 价格区域
.price-section {
  background: linear-gradient(135deg, #fff8f5 0%, #fff5f0 100%);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 28px;
  border: 1px solid rgba(255, 103, 0, 0.1);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #ff6700, #ff4500, #e4393c);
  }
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 14px;
}

.price-label {
  color: #999;
  font-size: 14px;
  font-weight: 450;
}

.price-value {
  font-size: 36px;
  color: #e4393c;
  font-weight: 700;
  font-family: 'Space Grotesk', sans-serif;

  &::before {
    content: '¥';
    font-size: 20px;
    font-weight: 600;
    margin-right: 2px;
  }
}

.ori-price {
  color: #999;
  font-size: 18px;
  text-decoration: line-through;
  font-family: 'Space Grotesk', sans-serif;
}

.price-tags {
  margin-top: 12px;

  .el-tag {
    font-weight: 500;
    border-radius: 6px;
  }
}

// SKU 选择
.sku-section {
  margin-bottom: 28px;
}

.sku-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 18px;
}

.sku-label {
  width: 70px;
  color: #666;
  font-size: 14px;
  padding-top: 10px;
  flex-shrink: 0;
  font-weight: 450;
}

.sku-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.sku-option {
  padding: 10px 24px;
  border: 1.5px solid #e8e4de;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 450;
  transition: all 0.3s $ease-out-expo;
  background: #fff;

  &:hover:not(.disabled) {
    border-color: #ff6700;
    color: #ff6700;
    background: rgba(255, 103, 0, 0.04);
  }

  &.active {
    border-color: #ff6700;
    background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(255, 103, 0, 0.25);
    font-weight: 500;
  }

  &.disabled {
    background: #f5f3f0;
    color: #ccc;
    cursor: not-allowed;
    text-decoration: line-through;
    border-color: #e8e4de;
  }
}

// 数量选择
.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 28px;

  .section-label {
    width: 70px;
    color: #666;
    font-size: 14px;
    font-weight: 450;
  }

  :deep(.el-input-number) {
    width: 140px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    border-color: #e8e4de;
    padding: 4px 12px;

    &:hover {
      border-color: #d4cfc6;
    }
  }

  .stocks-info {
    margin-left: 24px;
    color: #999;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 450;

    .el-icon {
      color: #ff6700;
    }
  }
}

// 操作按钮
.action-section {
  display: flex;
  gap: 18px;
  margin-bottom: 28px;
}

.action-btn {
  flex: 1;
  height: 56px;
  font-size: 17px;
  border-radius: 14px;
  font-weight: 600;
  letter-spacing: 0.02em;
  transition: all 0.35s $ease-out-expo;
}

.cart-btn {
  background: linear-gradient(135deg, #ff9500 0%, #ff6b00 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(255, 107, 0, 0.3);

  &:hover {
    background: linear-gradient(135deg, #ff8c00 0%, #ff5f00 100%);
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(255, 107, 0, 0.4);
  }
}

.buy-btn {
  background: linear-gradient(135deg, #e4393c 0%, #c41e24 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(228, 57, 60, 0.3);

  &:hover {
    background: linear-gradient(135deg, #d63031 0%, #b71c1c 100%);
    transform: translateY(-2px);
    box-shadow: 0 8px 28px rgba(228, 57, 60, 0.4);
  }
}

// 服务保障
.service-section {
  display: flex;
  gap: 28px;
  padding-top: 24px;
  border-top: 1px solid #f0ede8;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 13px;
  font-weight: 450;
  padding: 8px 16px;
  background: rgba(255, 103, 0, 0.04);
  border-radius: 8px;
  transition: all 0.25s $ease-out-expo;

  &:hover {
    background: rgba(255, 103, 0, 0.08);
    color: #ff6700;
  }

  .el-icon {
    color: #ff6700;
    font-size: 16px;
  }
}

// Tab 区域
.prod-tabs {
  margin-top: 40px;

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background: #f0ede8;
  }

  :deep(.el-tabs__item) {
    font-size: 16px;
    padding: 0 28px;
    font-weight: 450;
    height: 52px;
    line-height: 52px;

    &.is-active {
      font-weight: 600;
    }
  }

  :deep(.el-tabs__active-bar) {
    height: 3px;
    border-radius: 2px;
  }
}

.detail-content {
  padding: 28px 0;
  min-height: 300px;
  line-height: 1.9;
  font-size: 15px;
  color: #333;

  :deep(img) {
    max-width: 100%;
    display: block;
    margin: 20px auto;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  }

  :deep(p) {
    margin-bottom: 16px;
  }
}

// 评论区域
.comment-list {
  padding: 24px 0;
}

.comment-item {
  display: flex;
  gap: 18px;
  padding: 24px 0;
  border-bottom: 1px solid #f5f3f0;
  transition: background 0.25s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: rgba(255, 103, 0, 0.02);
    margin: 0 -16px;
    padding-left: 16px;
    padding-right: 16px;
  }
}

.comm-avatar {
  flex-shrink: 0;

  :deep(.el-avatar) {
    background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
    font-weight: 500;
  }
}

.comm-content {
  flex: 1;
}

.comm-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 12px;
}

.user-name {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.comm-time {
  color: #999;
  font-size: 12px;
  margin-left: auto;
  font-weight: 450;
}

.comm-text {
  color: #555;
  line-height: 1.7;
  margin-bottom: 14px;
  font-size: 14px;
}

.comm-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.comm-img {
  width: 80px;
  height: 80px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s $ease-out-expo;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  }
}

.empty-comment {
  padding: 80px 0;
}

// 响应式
@media (max-width: 1024px) {
  .prod-main {
    flex-direction: column;
    gap: 32px;
  }

  .prod-gallery {
    width: 100%;
    max-width: 500px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .prod-detail {
    padding: 20px;
    border-radius: 20px;
  }

  .prod-info .prod-name {
    font-size: 20px;
  }

  .price-value {
    font-size: 28px;
  }

  .action-section {
    flex-direction: column;
  }

  .service-section {
    flex-wrap: wrap;
    gap: 12px;
  }

  .service-item {
    font-size: 12px;
    padding: 6px 12px;
  }
}
</style>
