<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="banner-section" v-if="bannerList.length > 0">
      <div class="container">
        <el-carousel
          height="340px"
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
    <section class="category-section" v-if="categoryList.length > 0">
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

    <!-- 空状态 - 更精致的设计 -->
    <section v-if="!loading && tagProdList.length === 0 && bannerList.length === 0" class="empty-state">
      <div class="container">
        <div class="empty-content">
          <div class="empty-icon">
            <el-icon :size="48"><Shop /></el-icon>
          </div>
          <h3>{{ t('home.emptyProducts') }}</h3>
          <p>商品正在上架中，敬请期待</p>
          <router-link to="/category" class="browse-btn">
            浏览分类
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Shop, ArrowRight } from '@element-plus/icons-vue'
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
  background: $bg-page;
  min-height: 100vh;
  padding-bottom: 32px;
}

// 轮播图区域
.banner-section {
  padding: 16px 0 8px;

  .banner-item {
    cursor: pointer;
    border-radius: 12px;
    overflow: hidden;
    height: 100%;
    position: relative;
  }

  .banner-img {
    width: 100%;
    height: 340px;
    object-fit: cover;
    transition: transform 0.5s $ease-out-expo;
  }

  .banner-item:hover .banner-img {
    transform: scale(1.02);
  }

  :deep(.el-carousel__indicators--outside) {
    margin-top: 12px;
  }

  :deep(.el-carousel__indicator--horizontal .el-carousel__button) {
    width: 24px;
    height: 3px;
    border-radius: 2px;
    background: #E2E8F0;
    transition: all 0.25s $ease-out-expo;
  }

  :deep(.el-carousel__indicator--horizontal.is-active .el-carousel__button) {
    width: 36px;
    background: $primary;
  }
}

// 通用标题 - 更精致
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    position: relative;
    padding-left: 12px;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 18px;
      background: $primary;
      border-radius: 2px;
    }
  }

  .view-all {
    color: $text-tertiary;
    font-size: 12px;
    font-weight: 500;
    padding: 4px 10px;
    border-radius: 4px;
    transition: all 0.2s $ease-out-expo;

    &:hover {
      color: $primary;
      background: $primary-bg;
    }
  }
}

// 分类区域
.category-section {
  background: #fff;
  padding: 24px 0;
  margin-bottom: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 8px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s $ease-out-expo;

  &:hover {
    background: $primary-bg;

    .category-icon {
      transform: scale(1.08);
    }

    .category-name {
      color: $primary;
    }
  }

  .category-icon {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: $bg-secondary;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 8px;
    transition: all 0.2s $ease-out-expo;
  }

  .category-img {
    width: 30px;
    height: 30px;
    object-fit: contain;
  }

  .category-name {
    font-size: 11px;
    color: $text-regular;
    font-weight: 500;
    transition: color 0.2s;
    text-align: center;
  }
}

// 商品区域
.prod-section {
  background: #fff;
  padding: 24px 0;
  margin-bottom: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.prod-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
}

.prod-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s $ease-out-expo;
  border: 1px solid rgba(0, 0, 0, 0.04);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 20px rgba(74, 159, 212, 0.1);
    border-color: rgba(74, 159, 212, 0.1);

    .prod-overlay {
      opacity: 1;
    }

    .prod-img {
      transform: scale(1.04);
    }
  }

  .prod-img-wrapper {
    position: relative;
    overflow: hidden;
  }

  .prod-img {
    width: 100%;
    height: 180px;
    object-fit: cover;
    transition: transform 0.3s $ease-out-expo;
  }

  .prod-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(74, 159, 212, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.2s $ease-out-expo;

    span {
      color: #fff;
      font-size: 12px;
      font-weight: 500;
      padding: 6px 14px;
      border: 1.5px solid #fff;
      border-radius: 16px;
    }
  }

  .prod-info {
    padding: 12px;
  }

  .prod-name {
    font-size: 12px;
    color: $text-regular;
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.4;
    height: 34px;
    margin-bottom: 8px;

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
    margin-bottom: 6px;
  }

  .prod-price-row {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
  }

  .prod-price {
    font-size: 16px;
    color: $danger;
    font-weight: 700;
    font-family: 'Outfit', sans-serif;

    &::before {
      content: '¥';
      font-size: 11px;
      font-weight: 600;
    }
  }

  .prod-sales {
    font-size: 10px;
    color: $text-tertiary;
    font-weight: 400;
  }
}

// 空状态 - 更精致
.empty-state {
  padding: 60px 0;
  background: #fff;
  border-radius: 12px;
  margin: 16px 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.empty-content {
  text-align: center;
  max-width: 320px;
  margin: 0 auto;

  .empty-icon {
    width: 80px;
    height: 80px;
    background: $primary-bg;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    color: $primary;
  }

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 8px;
  }

  p {
    font-size: 13px;
    color: $text-tertiary;
    margin-bottom: 20px;
  }

  .browse-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 10px 20px;
    background: $primary;
    color: #fff;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 500;
    transition: all 0.2s $ease-out-expo;

    &:hover {
      background: $primary-dark;
      color: #fff;
    }
  }
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
    border-radius: 8px;

    .prod-img {
      height: 140px;
    }

    .prod-info {
      padding: 10px;
    }

    .prod-price {
      font-size: 14px;
    }
  }

  .banner-section .banner-img {
    height: 200px;
  }
}
</style>
