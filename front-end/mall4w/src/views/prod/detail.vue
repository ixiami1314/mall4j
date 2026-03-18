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

// 颜色变量 - 淡蓝色系
$primary: #4A9FD4;
$primary-light: #6BB8E8;
$primary-dark: #3A8BC4;
$primary-bg: #F0F7FC;
$primary-surface: #E8F2FA;

$danger: #DC2626;
$danger-light: #EF4444;

$text-primary: #1E293B;
$text-regular: #334155;
$text-secondary: #64748B;
$text-tertiary: #94A3B8;

$bg-page: #F8FAFC;
$bg-secondary: #F1F5F9;

$border-light: #E2E8F0;
$border-base: #CBD5E1;

.prod-detail {
  background: #fff;
  border-radius: 20px;
  padding: 28px;
  margin: 20px auto;
  box-shadow: 0 1px 16px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
  animation: fadeIn 0.4s $ease-out-expo;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.prod-main {
  display: flex;
  gap: 40px;
  margin-bottom: 36px;
}

// 商品图片区
.prod-gallery {
  width: 480px;
  flex-shrink: 0;

  .main-image-wrapper {
    border-radius: 16px;
    overflow: hidden;
    background: linear-gradient(135deg, $bg-page 0%, $bg-secondary 100%);
    border: 1px solid $border-light;
    box-shadow: 0 3px 16px rgba(0, 0, 0, 0.03);
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
    color: $text-tertiary;

    .el-icon {
      font-size: 48px;
      margin-bottom: 10px;
      opacity: 0.5;
    }
  }

  .image-thumbs {
    display: flex;
    gap: 10px;
    margin-top: 14px;
  }

  .thumb-item {
    width: 72px;
    height: 72px;
    border-radius: 10px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.25s $ease-out-expo;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    &:hover, &.active {
      border-color: $primary;
      box-shadow: 0 3px 12px rgba(74, 159, 212, 0.2);
      transform: translateY(-2px);
    }
  }
}

// 商品信息区
.prod-info {
  flex: 1;

  .prod-name {
    font-size: 24px;
    font-weight: 600;
    color: $text-primary;
    line-height: 1.4;
    margin-bottom: 12px;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.01em;
  }

  .prod-brief {
    color: $text-secondary;
    font-size: 14px;
    margin-bottom: 22px;
    line-height: 1.7;
    font-weight: 400;
  }
}

// 价格区域
.price-section {
  background: linear-gradient(135deg, $primary-bg 0%, $primary-surface 100%);
  border-radius: 14px;
  padding: 20px;
  margin-bottom: 24px;
  border: 1px solid rgba(74, 159, 212, 0.12);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, $primary, $primary-dark);
  }
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price-label {
  color: $text-tertiary;
  font-size: 13px;
  font-weight: 400;
}

.price-value {
  font-size: 32px;
  color: $danger;
  font-weight: 700;
  font-family: 'Outfit', sans-serif;

  &::before {
    content: '¥';
    font-size: 18px;
    font-weight: 600;
    margin-right: 2px;
  }
}

.ori-price {
  color: $text-tertiary;
  font-size: 16px;
  text-decoration: line-through;
  font-family: 'Outfit', sans-serif;
}

.price-tags {
  margin-top: 10px;

  .el-tag {
    font-weight: 500;
    border-radius: 5px;
  }
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
  width: 65px;
  color: $text-secondary;
  font-size: 13px;
  padding-top: 9px;
  flex-shrink: 0;
  font-weight: 400;
}

.sku-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.sku-option {
  padding: 8px 20px;
  border: 1.5px solid $border-light;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 450;
  transition: all 0.25s $ease-out-expo;
  background: #fff;

  &:hover:not(.disabled) {
    border-color: $primary;
    color: $primary;
    background: rgba(74, 159, 212, 0.04);
  }

  &.active {
    border-color: $primary;
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    color: #fff;
    box-shadow: 0 3px 10px rgba(74, 159, 212, 0.25);
    font-weight: 500;
  }

  &.disabled {
    background: $bg-secondary;
    color: $text-tertiary;
    cursor: not-allowed;
    text-decoration: line-through;
    border-color: $border-light;
  }
}

// 数量选择
.quantity-section {
  display: flex;
  align-items: center;
  margin-bottom: 24px;

  .section-label {
    width: 65px;
    color: $text-secondary;
    font-size: 13px;
    font-weight: 400;
  }

  :deep(.el-input-number) {
    width: 130px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    border-color: $border-light;
    padding: 3px 10px;

    &:hover {
      border-color: $border-base;
    }
  }

  .stocks-info {
    margin-left: 20px;
    color: $text-tertiary;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 5px;
    font-weight: 400;

    .el-icon {
      color: $primary;
    }
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
  font-size: 15px;
  border-radius: 12px;
  font-weight: 600;
  letter-spacing: 0.01em;
  transition: all 0.3s $ease-out-expo;
}

.cart-btn {
  background: linear-gradient(135deg, $primary-light 0%, $primary 100%);
  border: none;
  box-shadow: 0 5px 16px rgba(74, 159, 212, 0.25);

  &:hover {
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    transform: translateY(-2px);
    box-shadow: 0 7px 22px rgba(74, 159, 212, 0.32);
  }
}

.buy-btn {
  background: linear-gradient(135deg, $danger 0%, #B91C1C 100%);
  border: none;
  box-shadow: 0 5px 16px rgba(220, 38, 38, 0.25);

  &:hover {
    background: linear-gradient(135deg, $danger-light 0%, $danger 100%);
    transform: translateY(-2px);
    box-shadow: 0 7px 22px rgba(220, 38, 38, 0.32);
  }
}

// 服务保障
.service-section {
  display: flex;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid $border-light;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: $text-secondary;
  font-size: 12px;
  font-weight: 400;
  padding: 7px 14px;
  background: $bg-page;
  border-radius: 6px;
  transition: all 0.2s $ease-out-expo;

  &:hover {
    background: $primary-bg;
    color: $primary;
  }

  .el-icon {
    color: $primary;
    font-size: 14px;
  }
}

// Tab 区域
.prod-tabs {
  margin-top: 36px;

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background: $border-light;
  }

  :deep(.el-tabs__item) {
    font-size: 15px;
    padding: 0 24px;
    font-weight: 450;
    height: 48px;
    line-height: 48px;

    &.is-active {
      font-weight: 600;
    }
  }

  :deep(.el-tabs__active-bar) {
    height: 2px;
    border-radius: 1px;
  }
}

.detail-content {
  padding: 24px 0;
  min-height: 280px;
  line-height: 1.85;
  font-size: 14px;
  color: $text-regular;

  :deep(img) {
    max-width: 100%;
    display: block;
    margin: 16px auto;
    border-radius: 10px;
    box-shadow: 0 3px 16px rgba(0, 0, 0, 0.05);
  }

  :deep(p) {
    margin-bottom: 14px;
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
  border-bottom: 1px solid $border-light;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: rgba(74, 159, 212, 0.02);
    margin: 0 -14px;
    padding-left: 14px;
    padding-right: 14px;
  }
}

.comm-avatar {
  flex-shrink: 0;

  :deep(.el-avatar) {
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    font-weight: 500;
  }
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
  color: $text-regular;
  font-size: 13px;
}

.comm-time {
  color: $text-tertiary;
  font-size: 11px;
  margin-left: auto;
  font-weight: 400;
}

.comm-text {
  color: $text-secondary;
  line-height: 1.7;
  margin-bottom: 12px;
  font-size: 13px;
}

.comm-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.comm-img {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s $ease-out-expo;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);

  &:hover {
    transform: scale(1.04);
    box-shadow: 0 3px 12px rgba(0, 0, 0, 0.08);
  }
}

.empty-comment {
  padding: 72px 0;
}

// 响应式
@media (max-width: 1024px) {
  .prod-main {
    flex-direction: column;
    gap: 28px;
  }

  .prod-gallery {
    width: 100%;
    max-width: 480px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .prod-detail {
    padding: 18px;
    border-radius: 16px;
  }

  .prod-info .prod-name {
    font-size: 18px;
  }

  .price-value {
    font-size: 26px;
  }

  .action-section {
    flex-direction: column;
  }

  .service-section {
    flex-wrap: wrap;
    gap: 10px;
  }

  .service-item {
    font-size: 11px;
    padding: 5px 10px;
  }
}
</style>
