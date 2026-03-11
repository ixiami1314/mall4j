<template>
  <div class="category-page">
    <!-- 左侧分类 -->
    <div class="category-aside">
      <div class="aside-header">
        <h3>{{ t('category.title') }}</h3>
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
            <small>{{ t('category.itemCount', { count: total }) }}</small>
          </span>
        </div>
        <div class="filter-right">
          <el-radio-group v-model="sortBy" size="small" @change="handleSort">
            <el-radio-button label="default">{{ t('category.sortDefault') }}</el-radio-button>
            <el-radio-button label="sales">{{ t('category.sortSales') }}</el-radio-button>
            <el-radio-button label="price_asc">{{ t('category.sortPriceAsc') }}</el-radio-button>
            <el-radio-button label="price_desc">{{ t('category.sortPriceDesc') }}</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 商品列表 -->
      <div v-if="prodList.length === 0 && !loading" class="empty-prod">
        <el-empty :description="t('category.emptyProducts')">
          <el-button type="primary" @click="$router.push('/')">{{ t('category.goHome') }}</el-button>
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
              <span>{{ t('home.viewDetail') }}</span>
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
              <span class="prod-sales" v-if="prod.sales">{{ t('product.sold') }}{{ prod.sales }}</span>
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
import { useI18n } from 'vue-i18n'
import { getCategoryInfo, getProdPage } from '@/api/prod'
import { ArrowRight } from '@element-plus/icons-vue'

const { t } = useI18n()
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
// 缓动函数
$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

.category-page {
  display: flex;
  gap: 28px;
  max-width: 1200px;
  margin: 24px auto;
  min-height: calc(100vh - 180px);
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

// 左侧分类
.category-aside {
  width: 260px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.aside-header {
  padding: 24px;
  background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -20px;
    right: -20px;
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }

  h3 {
    color: #fff;
    font-size: 20px;
    font-weight: 700;
    font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.01em;
  }
}

.category-list {
  max-height: 600px;
  overflow-y: auto;
  padding: 8px;

  &::-webkit-scrollbar {
    width: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #e8e4de;
    border-radius: 2px;

    &:hover {
      background: #ff6700;
    }
  }
}

.category-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.25s $ease-out-expo;
  border-radius: 12px;
  margin: 2px 0;
  position: relative;

  &:hover {
    background: rgba(255, 103, 0, 0.06);
  }

  &.active {
    background: linear-gradient(135deg, #fff8f5 0%, #ffefea 100%);
    color: #ff6700;
    font-weight: 500;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 24px;
      background: linear-gradient(180deg, #ff6700, #ff4500);
      border-radius: 0 2px 2px 0;
    }

    .el-icon {
      color: #ff6700;
    }
  }

  .cat-icon {
    width: 26px;
    height: 26px;
    object-fit: contain;
    margin-right: 14px;
  }

  .cat-name {
    flex: 1;
    font-size: 14px;
    font-weight: 450;
  }

  .el-icon {
    color: #ccc;
    transition: color 0.25s;
  }
}

// 右侧主区域
.category-main {
  flex: 1;
  background: #fff;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

// 筛选栏
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0ede8;
}

.current-category {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
  display: flex;
  align-items: baseline;
  gap: 12px;

  small {
    font-size: 13px;
    color: #999;
    font-weight: 450;
    padding: 4px 12px;
    background: rgba(255, 103, 0, 0.06);
    border-radius: 12px;
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
    box-shadow: 0 16px 32px rgba(228, 57, 60, 0.1);
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
      transform: scale(1.05);
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
    }
  }

  .prod-tag {
    position: absolute;
    top: 12px;
    left: 12px;

    :deep(.el-tag) {
      border-radius: 6px;
      font-weight: 500;
    }
  }

  .prod-info {
    padding: 18px;
  }

  .prod-name {
    font-size: 14px;
    color: #333;
    font-weight: 450;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.5;
    height: 42px;
    margin-bottom: 10px;
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
    margin-bottom: 12px;
  }

  .prod-footer {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
  }

  .price-row {
    display: flex;
    align-items: baseline;
    gap: 8px;
  }

  .prod-price {
    font-size: 20px;
    color: #e4393c;
    font-weight: 700;
    font-family: 'Space Grotesk', sans-serif;
    transition: all 0.3s $ease-out-expo;

    &::before {
      content: '¥';
      font-size: 13px;
      font-weight: 600;
    }
  }

  .ori-price {
    font-size: 12px;
    color: #999;
    text-decoration: line-through;
    font-family: 'Space Grotesk', sans-serif;
  }

  .prod-sales {
    font-size: 12px;
    color: #999;
    font-weight: 450;
  }
}

// 空状态
.empty-prod {
  padding: 100px 0;
  text-align: center;
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 36px;
  padding-top: 24px;
  border-top: 1px solid #f0ede8;

  :deep(.el-pagination) {
    gap: 8px;
  }

  :deep(.el-pager li) {
    border-radius: 10px;
    font-weight: 500;
  }
}

// 响应式
@media (max-width: 1200px) {
  .prod-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .category-aside {
    width: 220px;
  }

  .prod-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .category-page {
    flex-direction: column;
    gap: 20px;
  }

  .category-aside {
    width: 100%;
  }

  .category-list {
    display: flex;
    overflow-x: auto;
    max-height: none;
    padding: 12px;
    gap: 8px;
  }

  .category-item {
    flex-shrink: 0;
    padding: 10px 18px;
    border-radius: 20px;
    background: #f5f3f0;

    &.active {
      background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
      color: #fff;

      &::before {
        display: none;
      }
    }

    .cat-icon {
      display: none;
    }
  }

  .prod-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .prod-card {
    border-radius: 16px;

    .prod-img {
      height: 160px;
    }

    .prod-info {
      padding: 14px;
    }

    .prod-price {
      font-size: 16px;
    }
  }
}
</style>
