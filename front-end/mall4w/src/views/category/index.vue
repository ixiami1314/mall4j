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

.category-page {
  display: flex;
  gap: 20px;
  max-width: 1200px;
  margin: 16px auto;
  min-height: calc(100vh - 160px);
}

// 左侧分类
.category-aside {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.aside-header {
  padding: 16px;
  background: $primary;

  h3 {
    color: #fff;
    font-size: 14px;
    font-weight: 600;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
  }
}

.category-list {
  max-height: 500px;
  overflow-y: auto;
  padding: 4px;

  &::-webkit-scrollbar {
    width: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: $border-base;
    border-radius: 2px;
  }
}

.category-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  cursor: pointer;
  transition: all 0.15s $ease-out-expo;
  border-radius: 6px;
  margin: 2px 0;
  position: relative;

  &:hover {
    background: rgba(74, 159, 212, 0.04);
  }

  &.active {
    background: $primary-bg;
    color: $primary;
    font-weight: 500;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 2px;
      height: 16px;
      background: $primary;
      border-radius: 0 1px 1px 0;
    }
  }

  .cat-icon {
    width: 20px;
    height: 20px;
    object-fit: contain;
    margin-right: 10px;
  }

  .cat-name {
    flex: 1;
    font-size: 12px;
    font-weight: 400;
  }

  .el-icon {
    color: $text-tertiary;
    font-size: 12px;
  }
}

// 右侧主区域
.category-main {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  padding: 18px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

// 筛选栏
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid $border-light;
}

.current-category {
  font-size: 14px;
  font-weight: 600;
  color: $text-primary;
  font-family: 'Outfit', 'Noto Sans SC', sans-serif;
  display: flex;
  align-items: baseline;
  gap: 8px;

  small {
    font-size: 11px;
    color: $text-tertiary;
    font-weight: 400;
    padding: 2px 8px;
    background: $bg-page;
    border-radius: 4px;
  }
}

// 商品网格
.prod-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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
    height: 170px;
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

  .prod-tag {
    position: absolute;
    top: 8px;
    left: 8px;

    :deep(.el-tag) {
      border-radius: 4px;
      font-size: 10px;
    }
  }

  .prod-info {
    padding: 12px;
  }

  .prod-name {
    font-size: 12px;
    color: $text-regular;
    font-weight: 400;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.4;
    height: 34px;
    margin-bottom: 6px;

    &:hover {
      color: $primary;
    }
  }

  .prod-brief {
    font-size: 10px;
    color: $text-tertiary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 8px;
  }

  .prod-footer {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
  }

  .price-row {
    display: flex;
    align-items: baseline;
    gap: 4px;
  }

  .prod-price {
    font-size: 15px;
    color: $danger;
    font-weight: 700;
    font-family: 'Outfit', sans-serif;

    &::before {
      content: '¥';
      font-size: 10px;
      font-weight: 600;
    }
  }

  .ori-price {
    font-size: 10px;
    color: $text-tertiary;
    text-decoration: line-through;
    font-family: 'Outfit', sans-serif;
  }

  .prod-sales {
    font-size: 10px;
    color: $text-tertiary;
    font-weight: 400;
  }
}

// 空状态
.empty-prod {
  padding: 60px 0;
  text-align: center;
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 18px;
  border-top: 1px solid $border-light;

  :deep(.el-pagination) {
    gap: 4px;
  }

  :deep(.el-pager li) {
    border-radius: 6px;
    font-weight: 500;
    font-size: 12px;
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
    width: 180px;
  }

  .prod-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .category-page {
    flex-direction: column;
    gap: 12px;
  }

  .category-aside {
    width: 100%;
  }

  .category-list {
    display: flex;
    overflow-x: auto;
    max-height: none;
    padding: 8px;
    gap: 4px;
  }

  .category-item {
    flex-shrink: 0;
    padding: 6px 14px;
    border-radius: 16px;
    background: $bg-secondary;

    &.active {
      background: $primary;
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
    gap: 10px;
  }

  .prod-card {
    border-radius: 8px;

    .prod-img {
      height: 130px;
    }

    .prod-info {
      padding: 10px;
    }

    .prod-price {
      font-size: 13px;
    }
  }
}
</style>
