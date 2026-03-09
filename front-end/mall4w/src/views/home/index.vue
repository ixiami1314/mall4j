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
          <h3>商品分类</h3>
          <router-link to="/category" class="view-all">全部分类 ></router-link>
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
          <router-link to="/category" class="view-all">查看更多 ></router-link>
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
                <span>查看详情</span>
              </div>
            </div>
            <div class="prod-info">
              <p class="prod-name">{{ prod.prodName }}</p>
              <p class="prod-brief" v-if="prod.brief">{{ prod.brief }}</p>
              <div class="prod-price-row">
                <span class="prod-price">¥{{ prod.price }}</span>
                <span class="prod-sales" v-if="prod.sales">已售 {{ prod.sales }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 空状态 -->
    <div v-if="!loading && tagProdList.length === 0" class="empty-state">
      <el-empty description="暂无商品数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getIndexImgs, getTagProdList } from '@/api/index'
import { getCategoryInfo } from '@/api/prod'
import { useAppStore } from '@/stores/app'

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
.home-page {
  background: linear-gradient(180deg, #f8f9fa 0%, #f0f2f5 100%);
  min-height: 100vh;
  padding-bottom: 40px;
}

// 轮播图区域
.banner-section {
  padding: 20px 0;
  .banner-item {
    cursor: pointer;
    border-radius: 12px;
    overflow: hidden;
    height: 100%;
  }
  .banner-img {
    width: 100%;
    height: 420px;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  .banner-item:hover .banner-img {
    transform: scale(1.02);
  }
  :deep(.el-carousel__indicators--outside) {
    margin-top: 15px;
  }
  :deep(.el-carousel__indicator--horizontal .el-carousel__button) {
    width: 30px;
    height: 4px;
    border-radius: 2px;
    background: #dcdfe6;
  }
  :deep(.el-carousel__indicator--horizontal.is-active .el-carousel__button) {
    background: linear-gradient(90deg, #3a86b9, #5ba4d4);
  }
}

// 通用标题
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  h3 {
    font-size: 22px;
    font-weight: 600;
    color: #1a1a1a;
    position: relative;
    padding-left: 16px;
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 22px;
      background: linear-gradient(180deg, #3a86b9, #5ba4d4);
      border-radius: 2px;
    }
  }
  .view-all {
    color: #666;
    font-size: 14px;
    transition: color 0.2s;
    &:hover {
      color: #3a86b9;
    }
  }
}

// 分类区域
.category-section {
  background: #fff;
  padding: 30px 0;
  margin-bottom: 20px;
  border-radius: 0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 16px;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  &:hover {
    background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf0 100%);
    transform: translateY(-4px);
    .category-icon {
      transform: scale(1.1);
    }
    .category-name {
      color: #3a86b9;
    }
  }
  .category-icon {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: linear-gradient(135deg, #f0f4f8 0%, #e2e8f0 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12px;
    transition: transform 0.3s ease;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
  .category-img {
    width: 40px;
    height: 40px;
    object-fit: contain;
  }
  .category-name {
    font-size: 13px;
    color: #333;
    font-weight: 500;
    transition: color 0.2s;
  }
}

// 商品区域
.prod-section {
  background: #fff;
  padding: 30px 0;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.prod-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.prod-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  &:hover {
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    transform: translateY(-6px);
    .prod-overlay {
      opacity: 1;
    }
    .prod-price {
      color: #ff4757;
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
    transition: transform 0.3s ease;
  }
  .prod-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;
    span {
      color: #fff;
      font-size: 14px;
      padding: 8px 20px;
      border: 1px solid #fff;
      border-radius: 20px;
    }
  }
  .prod-info {
    padding: 16px;
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
    line-height: 1.4;
    height: 40px;
    margin-bottom: 8px;
  }
  .prod-brief {
    font-size: 12px;
    color: #999;
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
    color: #e4393c;
    font-weight: 600;
    transition: color 0.2s;
  }
  .prod-sales {
    font-size: 12px;
    color: #999;
  }
}

.empty-state {
  padding: 60px 0;
  background: #fff;
  border-radius: 12px;
  margin: 20px 0;
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
  }
}
</style>
