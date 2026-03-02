<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="banner-section">
      <div class="container">
        <el-carousel height="400px" :interval="5000">
          <el-carousel-item v-for="item in bannerList" :key="item.imgUrl">
            <img :src="item.imgUrl" class="banner-img" @click="handleBannerClick(item)" />
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 分类入口 -->
    <section class="category-section">
      <div class="container">
        <div class="category-list">
          <div v-for="cat in categoryList.slice(0, 8)" :key="cat.categoryId" class="category-item" @click="goCategory(cat.categoryId)">
            <img :src="cat.pic" class="category-img" />
            <span>{{ cat.categoryName }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品列表 -->
    <section v-for="tag in tagProdList" :key="tag.id" class="prod-section">
      <div class="container">
        <div class="section-header">
          <h2>{{ tag.title }}</h2>
          <router-link to="/category" class="more-link">查看更多 ></router-link>
        </div>
        <div class="prod-grid">
          <div v-for="prod in tag.prods" :key="prod.prodId" class="prod-card" @click="goProdDetail(prod.prodId)">
            <img :src="prod.pic" class="prod-img" />
            <div class="prod-info">
              <p class="prod-name">{{ prod.prodName }}</p>
              <p class="prod-price">¥{{ prod.price }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
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

const bannerList = ref([])
const categoryList = ref([])
const tagProdList = ref([])

onMounted(async () => {
  await Promise.all([
    fetchBanners(),
    fetchCategories(),
    fetchTagProds()
  ])
})

const fetchBanners = async () => {
  const { data } = await getIndexImgs()
  bannerList.value = data || []
}

const fetchCategories = async () => {
  const { data } = await getCategoryInfo()
  categoryList.value = data || []
  appStore.categoryList = data || []
}

const fetchTagProds = async () => {
  const { data } = await getTagProdList()
  tagProdList.value = data || []
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
  background: #f5f5f5;
}
.banner-section {
  margin-bottom: 20px;
  .banner-img {
    width: 100%;
    height: 400px;
    object-fit: cover;
    cursor: pointer;
  }
}
.category-section {
  background: #fff;
  padding: 30px 0;
  margin-bottom: 20px;
}
.category-list {
  display: flex;
  justify-content: space-between;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  &:hover {
    color: #3a86b9;
  }
  .category-img {
    width: 60px;
    height: 60px;
    margin-bottom: 10px;
  }
}
.prod-section {
  background: #fff;
  padding: 30px 0;
  margin-bottom: 20px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 {
    font-size: 20px;
    color: #333;
  }
  .more-link {
    color: #999;
    &:hover {
      color: #3a86b9;
    }
  }
}
.prod-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.prod-card {
  background: #fff;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  .prod-img {
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  .prod-info {
    padding: 10px;
  }
  .prod-name {
    font-size: 14px;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .prod-price {
    font-size: 18px;
    color: #e4393c;
    margin-top: 8px;
  }
}
</style>
