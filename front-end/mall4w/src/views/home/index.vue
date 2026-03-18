<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="banner-section">
      <div class="container">
        <el-carousel
          height="400px"
          :interval="5000"
          arrow="hover"
          indicator-position="outside"
        >
          <el-carousel-item v-for="item in bannerList" :key="item.imgUrl">
            <div class="banner-item" @click="handleBannerClick(item)">
              <img :src="item.imgUrl" class="banner-img" />
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 分类入口 -->
    <section class="category-section">
      <div class="container">
        <div class="section-header">
          <h3>{{ t('home.categoryTitle') }}</h3>
          <router-link to="/category" class="view-all">{{ t('home.viewAllCategories') }}</router-link>
        </div>
        <div class="category-grid">
          <div
            v-for="cat in categoryList.slice(0, 10)"
            :key="cat.categoryId"
            class="category-item"
            @click="goCategory(cat.categoryId)"
          >
            <div class="category-icon">
              <img :src="cat.pic" class="category-img" />
            </div>
            <span class="category-name">{{ cat.categoryName }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品列表 -->
    <section v-for="tag in tagProdList" :key="tag.id" class="prod-section">
      <div class="container">
        <div class="section-header">
          <h3>{{ tag.title }}</h3>
          <router-link to="/category" class="view-all">{{ t('home.viewMore') }}</router-link>
        </div>
        <div class="prod-grid">
          <div
            v-for="prod in tag.prods"
            :key="prod.prodId"
            class="prod-card"
            @click="goProdDetail(prod.prodId)"
          >
            <div class="prod-img-wrapper">
              <img :src="prod.pic" class="prod-img" />
              <div class="prod-overlay">
                <span>{{ t('home.viewDetail') }}</span>
              </div>
            </div>
            <div class="prod-info">
              <p class="prod-name">{{ prod.prodName }}</p>
              <p class="prod-brief" v-if="prod.brief">{{ prod.brief }}</p>
              <div class="prod-price-row">
                <span class="prod-price">¥{{ prod.price }}</span>
                <span class="prod-sales" v-if="prod.sales">{{ t('product.sold') }} {{ prod.sales }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 空状态 -->
    <div v-if="!loading && tagProdList.length === 0" class="empty-state">
      <el-empty :description="t('home.emptyProducts')" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getIndexImgs, getTagProdList } from '@/api/index'
import { getCategoryInfo } from '@/api/prod'
import { useAppStore } from '@/stores/app'

const { t } = useI18n()
const router = useRouter()
const appStore = useAppStore()

const loading = ref(true)
const bannerList = ref([])
const categoryList = ref([])
const tagProdList = ref([])

onMounted(async () => {
  try {
    await Promise.all([
      fetchBanners(),
      fetchCategories(),
      fetchTagProds()
    ])
  } finally {
    loading.value = false
  }
})

const fetchBanners = async () => {
  try {
    const { data } = await getIndexImgs()
    bannerList.value = data || []
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

const fetchCategories = async () => {
  try {
    const { data } = await getCategoryInfo()
    categoryList.value = data || []
    appStore.categoryList = data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchTagProds = async () => {
  try {
    const { data } = await getTagProdList()
    tagProdList.value = data || []
  } catch (error) {
    console.error('获取商品列表失败:', error)
  }
}

const handleBannerClick = (item) => {
  if (item.relation) {
    router.push(`/prod/${item.relation}`)
  }
}

const goCategory = (categoryId) => {
  router.push({ path: '/category', query: { id: categoryId } })
}

const goProdDetail = (prodId) => {
  router.push(`/prod/${prodId}`)
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

.home-page {
  background: linear-gradient(180deg, $bg-page 0%, #F1F5F9 50%, $bg-page 100%);
  min-height: 100vh;
  padding-bottom: 48px;
  position: relative;

  // 装饰性背景
  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 380px;
    background: linear-gradient(180deg, rgba(74, 159, 212, 0.04) 0%, transparent 100%);
    pointer-events: none;
    z-index: -1;
  }
}

// 轮播图区域
.banner-section {
  padding: 20px 0;

  .banner-item {
    cursor: pointer;
    border-radius: 18px;
    overflow: hidden;
    height: 100%;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 18px;
      box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.08);
      pointer-events: none;
    }
  }

  .banner-img {
    width: 100%;
    height: 400px;
    object-fit: cover;
    transition: transform 0.5s $ease-out-expo;
  }

  .banner-item:hover .banner-img {
    transform: scale(1.02);
  }

  :deep(.el-carousel__indicators--outside) {
    margin-top: 16px;
  }

  :deep(.el-carousel__indicator--horizontal .el-carousel__button) {
    width: 28px;
    height: 3px;
    border-radius: 2px;
    background: #E2E8F0;
    transition: all 0.25s $ease-out-expo;
  }

  :deep(.el-carousel__indicator--horizontal.is-active .el-carousel__button) {
    width: 42px;
    background: linear-gradient(90deg, $primary, $primary-dark);
  }
}

// 通用标题 - 精致简洁
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h3 {
    font-size: 20px;
    font-weight: 600;
    color: $text-primary;
    position: relative;
    padding-left: 14px;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.01em;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 22px;
      background: linear-gradient(180deg, $primary, $primary-dark);
      border-radius: 2px;
    }
  }

  .view-all {
    color: $text-secondary;
    font-size: 13px;
    font-weight: 500;
    padding: 5px 14px;
    border-radius: 16px;
    transition: all 0.2s $ease-out-expo;

    &:hover {
      color: $primary;
      background: rgba(74, 159, 212, 0.08);
    }
  }
}

// 分类区域
.category-section {
  background: linear-gradient(135deg, #ffffff 0%, #FAFCFE 100%);
  padding: 32px 0;
  margin-bottom: 20px;
  border-radius: 20px;
  box-shadow: 0 1px 16px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 10px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 18px 6px;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s $ease-out-expo;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 14px;
    background: linear-gradient(135deg, $primary-bg 0%, $primary-surface 100%);
    opacity: 0;
    transition: opacity 0.3s $ease-out-expo;
  }

  &:hover {
    transform: translateY(-4px);

    &::before {
      opacity: 1;
    }

    .category-icon {
      transform: scale(1.1);
      box-shadow: 0 6px 20px rgba(74, 159, 212, 0.18);
    }

    .category-name {
      color: $primary;
    }
  }

  .category-icon {
    width: 62px;
    height: 62px;
    border-radius: 50%;
    background: linear-gradient(135deg, $bg-secondary 0%, #E8E8E8 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12px;
    transition: all 0.3s $ease-out-expo;
    box-shadow: 0 3px 12px rgba(0, 0, 0, 0.05);
    position: relative;
    z-index: 1;
  }

  .category-img {
    width: 38px;
    height: 38px;
    object-fit: contain;
  }

  .category-name {
    font-size: 12px;
    color: $text-regular;
    font-weight: 500;
    transition: color 0.2s;
    position: relative;
    z-index: 1;
  }
}

// 商品区域
.prod-section {
  background: #fff;
  padding: 32px 0;
  margin-bottom: 20px;
  border-radius: 20px;
  box-shadow: 0 1px 16px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.prod-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 18px;
}

.prod-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s $ease-out-expo;
  border: 1px solid rgba(0, 0, 0, 0.04);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 16px;
    box-shadow: 0 16px 32px rgba(74, 159, 212, 0.1);
    opacity: 0;
    transition: opacity 0.3s $ease-out-expo;
    pointer-events: none;
  }

  &:hover {
    transform: translateY(-6px);
    border-color: rgba(74, 159, 212, 0.12);

    &::before {
      opacity: 1;
    }

    .prod-overlay {
      opacity: 1;
    }

    .prod-img {
      transform: scale(1.05);
    }

    .prod-price {
      color: $danger-light;
      transform: scale(1.04);
    }
  }

  .prod-img-wrapper {
    position: relative;
    overflow: hidden;
  }

  .prod-img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    transition: transform 0.4s $ease-out-expo;
  }

  .prod-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(74, 159, 212, 0.75), rgba(58, 139, 196, 0.75));
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s $ease-out-expo;

    span {
      color: #fff;
      font-size: 13px;
      font-weight: 500;
      padding: 8px 20px;
      border: 1.5px solid #fff;
      border-radius: 20px;
      backdrop-filter: blur(4px);
      letter-spacing: 0.01em;
    }
  }

  .prod-info {
    padding: 16px;
    position: relative;
  }

  .prod-name {
    font-size: 13px;
    color: $text-regular;
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.5;
    height: 39px;
    margin-bottom: 10px;
    transition: color 0.2s;

    &:hover {
      color: $primary;
    }
  }

  .prod-brief {
    font-size: 11px;
    color: $text-tertiary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 8px;
  }

  .prod-price-row {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
  }

  .prod-price {
    font-size: 20px;
    color: $danger;
    font-weight: 700;
    font-family: 'Outfit', sans-serif;
    transition: all 0.25s $ease-out-expo;

    &::before {
      content: '¥';
      font-size: 13px;
      font-weight: 600;
    }
  }

  .prod-sales {
    font-size: 11px;
    color: $text-tertiary;
    font-weight: 450;
  }

  .prod-action {
    position: absolute;
    bottom: 16px;
    right: 16px;
    opacity: 0;
    transform: translateY(6px);
    transition: all 0.25s $ease-out-expo;
  }
}

.empty-state {
  padding: 72px 0;
  background: #fff;
  border-radius: 20px;
  margin: 20px 0;
  box-shadow: 0 1px 16px rgba(0, 0, 0, 0.03);
}

// 响应式
@media (max-width: 1400px) {
  .category-grid {
    grid-template-columns: repeat(8, 1fr);
  }
  .prod-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1200px) {
  .category-grid {
    grid-template-columns: repeat(6, 1fr);
  }
  .prod-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(5, 1fr);
  }
  .prod-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .prod-card {
    border-radius: 14px;

    .prod-img {
      height: 160px;
    }

    .prod-info {
      padding: 12px;
    }

    .prod-price {
      font-size: 16px;
    }
  }
}
</style>
