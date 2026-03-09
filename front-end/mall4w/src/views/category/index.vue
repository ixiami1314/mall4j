<template>
  <div class="category-page">
    <!-- 左侧分类 -->
    <div class="category-aside">
      <div class="aside-header">
        <h3>商品分类</h3>
      </div>
      <div class="category-list">
        <div
          v-for="cat in categoryList"
          :key="cat.categoryId"
          :class="['category-item', { active: currentCategoryId === cat.categoryId }]"
          @click="selectCategory(cat.categoryId)"
        >
          <img :src="cat.pic" class="cat-icon" v-if="cat.pic" />
          <span class="cat-name">{{ cat.categoryName }}</span>
          <el-icon v-if="currentCategoryId === cat.categoryId"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <!-- 右侧商品列表 -->
    <div class="category-main">
      <!-- 顶部筛选栏 -->
      <div class="filter-bar">
        <div class="filter-left">
          <span class="current-category" v-if="currentCategory">
            {{ currentCategory.categoryName }}
            <small>{{ total }} 件商品</small>
          </span>
        </div>
        <div class="filter-right">
          <el-radio-group v-model="sortBy" size="small" @change="handleSort">
            <el-radio-button label="default">综合</el-radio-button>
            <el-radio-button label="sales">销量</el-radio-button>
            <el-radio-button label="price_asc">价格↑</el-radio-button>
            <el-radio-button label="price_desc">价格↓</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 商品列表 -->
      <div v-if="prodList.length === 0 && !loading" class="empty-prod">
        <el-empty description="该分类下暂无商品">
          <el-button type="primary" @click="$router.push('/')">去首页看看</el-button>
        </el-empty>
      </div>

      <div v-else class="prod-grid">
        <div
          v-for="prod in prodList"
          :key="prod.prodId"
          class="prod-card"
          @click="goProdDetail(prod.prodId)"
        >
          <div class="prod-img-wrapper">
            <img :src="prod.pic" class="prod-img" />
            <div class="prod-overlay">
              <span>查看详情</span>
            </div>
            <div class="prod-tag" v-if="prod.tag">
              <el-tag size="small" effect="dark">{{ prod.tag }}</el-tag>
            </div>
          </div>
          <div class="prod-info">
            <p class="prod-name">{{ prod.prodName }}</p>
            <p class="prod-brief" v-if="prod.brief">{{ prod.brief }}</p>
            <div class="prod-footer">
              <div class="price-row">
                <span class="prod-price">¥{{ prod.price }}</span>
                <span class="ori-price" v-if="prod.oriPrice">¥{{ prod.oriPrice }}</span>
              </div>
              <span class="prod-sales" v-if="prod.sales">已售{{ prod.sales }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, jumper"
          background
          @current-change="fetchProducts"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryInfo, getProdPage } from '@/api/prod'
import { ArrowRight } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const categoryList = ref([])
const currentCategoryId = ref(null)
const prodList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const sortBy = ref('default')

// 当前分类信息
const currentCategory = computed(() => {
  return categoryList.value.find(c => c.categoryId === currentCategoryId.value)
})

onMounted(async () => {
  await fetchCategories()
  if (route.query.id) {
    currentCategoryId.value = Number(route.query.id)
    fetchProducts()
  }
})

watch(() => route.query.id, (newId) => {
  if (newId) {
    currentCategoryId.value = Number(newId)
    pageNum.value = 1
    fetchProducts()
  }
})

const fetchCategories = async () => {
  try {
    const { data } = await getCategoryInfo()
    categoryList.value = data || []
    if (!currentCategoryId.value && data?.length > 0) {
      currentCategoryId.value = data[0].categoryId
      fetchProducts()
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchProducts = async () => {
  if (!currentCategoryId.value) return
  loading.value = true
  try {
    const { data } = await getProdPage(currentCategoryId.value, {
      current: pageNum.value,
      size: pageSize.value
    })
    prodList.value = data?.records || []
    total.value = data?.total || 0
  } catch (error) {
    console.error('获取商品失败:', error)
  } finally {
    loading.value = false
  }
}

const selectCategory = (categoryId) => {
  currentCategoryId.value = categoryId
  pageNum.value = 1
  router.push({ path: '/category', query: { id: categoryId } })
  fetchProducts()
}

const handleSort = () => {
  // 根据排序方式重新排序
  const list = [...prodList.value]
  switch (sortBy.value) {
    case 'sales':
      prodList.value = list.sort((a, b) => (b.sales || 0) - (a.sales || 0))
      break
    case 'price_asc':
      prodList.value = list.sort((a, b) => a.price - b.price)
      break
    case 'price_desc':
      prodList.value = list.sort((a, b) => b.price - a.price)
      break
    default:
      fetchProducts()
  }
}

const goProdDetail = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
.category-page {
  display: flex;
  gap: 24px;
  max-width: 1200px;
  margin: 20px auto;
  min-height: calc(100vh - 180px);
}

// 左侧分类
.category-aside {
  width: 240px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}
.aside-header {
  padding: 20px;
  background: linear-gradient(135deg, #3a86b9 0%, #5ba4d4 100%);
  h3 {
    color: #fff;
    font-size: 18px;
    font-weight: 600;
  }
}
.category-list {
  max-height: 600px;
  overflow-y: auto;
  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: #ddd;
    border-radius: 2px;
  }
}
.category-item {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
  &:hover {
    background: #f5f7fa;
  }
  &.active {
    background: linear-gradient(90deg, #e8f4fc 0%, #fff 100%);
    border-left-color: #3a86b9;
    color: #3a86b9;
    font-weight: 500;
  }
  .cat-icon {
    width: 24px;
    height: 24px;
    object-fit: contain;
    margin-right: 12px;
  }
  .cat-name {
    flex: 1;
    font-size: 14px;
  }
  .el-icon {
    color: #ccc;
  }
}

// 右侧主区域
.category-main {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

// 筛选栏
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}
.current-category {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  small {
    font-size: 12px;
    color: #999;
    font-weight: normal;
    margin-left: 8px;
  }
}

// 商品网格
.prod-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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
    height: 200px;
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
  .prod-tag {
    position: absolute;
    top: 10px;
    left: 10px;
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
    margin-bottom: 10px;
  }
  .prod-footer {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
  }
  .price-row {
    display: flex;
    align-items: baseline;
    gap: 6px;
  }
  .prod-price {
    font-size: 18px;
    color: #e4393c;
    font-weight: 600;
    transition: color 0.2s;
  }
  .ori-price {
    font-size: 12px;
    color: #999;
    text-decoration: line-through;
  }
  .prod-sales {
    font-size: 12px;
    color: #999;
  }
}

// 空状态
.empty-prod {
  padding: 80px 0;
  text-align: center;
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

// 响应式
@media (max-width: 1200px) {
  .prod-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .category-aside {
    width: 200px;
  }
  .prod-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .category-page {
    flex-direction: column;
  }
  .category-aside {
    width: 100%;
  }
  .category-list {
    display: flex;
    overflow-x: auto;
    max-height: none;
    padding: 10px;
    gap: 10px;
  }
  .category-item {
    flex-shrink: 0;
    border-left: none;
    border-bottom: 2px solid transparent;
    padding: 10px 16px;
    border-radius: 20px;
    background: #f5f5f5;
    &.active {
      background: #3a86b9;
      color: #fff;
      border-left-color: transparent;
    }
  }
}
</style>
