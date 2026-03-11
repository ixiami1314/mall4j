<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="banner-section">
      <div class="container">
        <el-carousel
          height="420px"
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
        <div class="section-title">
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
        <div class="section-title">
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

.home-page {
  background: linear-gradient(180deg, #faf9f7 0%, #f5f3f0 50%, #faf9f7 100%);
  min-height: 100vh;
  padding-bottom: 50px;
  position: relative;

  // 装饰性背景
  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 400px;
    background: linear-gradient(180deg, rgba(255, 103, 0, 0.03) 0%, transparent 100%);
    pointer-events: none;
    z-index: -1;
  }
}

// 轮播图区域
.banner-section {
  padding: 24px 0;

  .banner-item {
    cursor: pointer;
    border-radius: 20px;
    overflow: hidden;
    height: 100%;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: 20px;
      box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.1);
      pointer-events: none;
    }
  }

  .banner-img {
    width: 100%;
    height: 420px;
    object-fit: cover;
    transition: transform 0.6s $ease-out-expo;
  }

  .banner-item:hover .banner-img {
    transform: scale(1.03);
  }

  :deep(.el-carousel__indicators--outside) {
    margin-top: 20px;
  }

  :deep(.el-carousel__indicator--horizontal .el-carousel__button) {
    width: 32px;
    height: 4px;
    border-radius: 2px;
    background: #e0ddd8;
    transition: all 0.3s $ease-out-expo;
  }

  :deep(.el-carousel__indicator--horizontal.is-active .el-carousel__button) {
    width: 48px;
    background: linear-gradient(90deg, #ff6700, #ff4500);
  }
}

// 通用标题 - 更精致
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;

  h3 {
    font-size: 24px;
    font-weight: 700;
    color: #1a1a1a;
    position: relative;
    padding-left: 20px;
    font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.01em;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 26px;
      background: linear-gradient(180deg, #ff6700, #ff4500);
      border-radius: 2px;
    }

    &::after {
      content: '';
      position: absolute;
      left: 10px;
      top: 50%;
      transform: translateY(-50%);
      width: 2px;
      height: 14px;
      background: #d4a574;
      border-radius: 1px;
      opacity: 0.5;
    }
  }

  .view-all {
    color: #666;
    font-size: 14px;
    font-weight: 500;
    padding: 6px 16px;
    border-radius: 20px;
    transition: all 0.25s $ease-out-expo;

    &:hover {
      color: #ff6700;
      background: rgba(255, 103, 0, 0.06);
    }
  }
}

// 分类区域
.category-section {
  background: linear-gradient(135deg, #ffffff 0%, #faf9f7 100%);
  padding: 36px 0;
  margin-bottom: 24px;
  border-radius: 24px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 8px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.35s $ease-out-expo;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 16px;
    background: linear-gradient(135deg, #fff8f5 0%, #ffefea 100%);
    opacity: 0;
    transition: opacity 0.35s $ease-out-expo;
  }

  &:hover {
    transform: translateY(-6px);

    &::before {
      opacity: 1;
    }

    .category-icon {
      transform: scale(1.15);
      box-shadow: 0 8px 24px rgba(255, 103, 0, 0.2);
    }

    .category-name {
      color: #ff6700;
    }
  }

  .category-icon {
    width: 68px;
    height: 68px;
    border-radius: 50%;
    background: linear-gradient(135deg, #f5f3f0 0%, #e8e4de 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 14px;
    transition: all 0.35s $ease-out-expo;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
    position: relative;
    z-index: 1;
  }

  .category-img {
    width: 42px;
    height: 42px;
    object-fit: contain;
  }

  .category-name {
    font-size: 13px;
    color: #333;
    font-weight: 500;
    transition: color 0.25s;
    position: relative;
    z-index: 1;
  }
}

// 商品区域
.prod-section {
  background: #fff;
  padding: 36px 0;
  margin-bottom: 24px;
  border-radius: 24px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.prod-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.prod-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s $ease-out-expo;
  border: 1px solid rgba(0, 0, 0, 0.04);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 20px;
    box-shadow: 0 20px 40px rgba(228, 57, 60, 0.12);
    opacity: 0;
    transition: opacity 0.4s $ease-out-expo;
    pointer-events: none;
  }

  &:hover {
    transform: translateY(-8px);
    border-color: rgba(255, 103, 0, 0.1);

    &::before {
      opacity: 1;
    }

    .prod-overlay {
      opacity: 1;
    }

    .prod-img {
      transform: scale(1.08);
    }

    .prod-price {
      color: #ff4757;
      transform: scale(1.08);
    }

    .prod-action {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .prod-img-wrapper {
    position: relative;
    overflow: hidden;
  }

  .prod-img {
    width: 100%;
    height: 220px;
    object-fit: cover;
    transition: transform 0.5s $ease-out-expo;
  }

  .prod-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(228, 57, 60, 0.75), rgba(255, 103, 0, 0.75));
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.35s $ease-out-expo;

    span {
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      padding: 10px 24px;
      border: 1.5px solid #fff;
      border-radius: 24px;
      backdrop-filter: blur(4px);
      letter-spacing: 0.02em;
    }
  }

  .prod-info {
    padding: 18px;
    position: relative;
  }

  .prod-name {
    font-size: 14px;
    color: #333;
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.5;
    height: 42px;
    margin-bottom: 12px;
    transition: color 0.25s;

    &:hover {
      color: #ff6700;
    }
  }

  .prod-brief {
    font-size: 12px;
    color: #999;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 10px;
  }

  .prod-price-row {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
  }

  .prod-price {
    font-size: 22px;
    color: #e4393c;
    font-weight: 700;
    font-family: 'Space Grotesk', sans-serif;
    transition: all 0.3s $ease-out-expo;

    &::before {
      content: '¥';
      font-size: 14px;
      font-weight: 600;
    }
  }

  .prod-sales {
    font-size: 12px;
    color: #999;
    font-weight: 450;
  }

  .prod-action {
    position: absolute;
    bottom: 18px;
    right: 18px;
    opacity: 0;
    transform: translateY(8px);
    transition: all 0.3s $ease-out-expo;
  }
}

.empty-state {
  padding: 80px 0;
  background: #fff;
  border-radius: 24px;
  margin: 24px 0;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
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
    gap: 12px;
  }

  .prod-card {
    border-radius: 16px;

    .prod-img {
      height: 180px;
    }

    .prod-info {
      padding: 14px;
    }

    .prod-price {
      font-size: 18px;
    }
  }
}
</style>
