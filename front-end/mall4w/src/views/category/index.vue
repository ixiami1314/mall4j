<template>
  <div class="category-page container">
    <div class="category-aside">
      <h3>商品分类</h3>
      <ul class="category-list">
        <li
          v-for="cat in categoryList"
          :key="cat.categoryId"
          :class="{ active: currentCategoryId === cat.categoryId }"
          @click="selectCategory(cat.categoryId)"
        >
          {{ cat.categoryName }}
        </li>
      </ul>
    </div>
    <div class="category-main">
      <div class="prod-header">
        <span>商品列表</span>
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchProducts"
        />
      </div>
      <div v-if="prodList.length === 0" class="empty-prod">
        <el-empty description="暂无商品" />
      </div>
      <div v-else class="prod-grid">
        <div v-for="prod in prodList" :key="prod.prodId" class="prod-card" @click="goProdDetail(prod.prodId)">
          <img :src="prod.pic" class="prod-img" />
          <div class="prod-info">
            <p class="prod-name">{{ prod.prodName }}</p>
            <p class="prod-price">¥{{ prod.price }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryInfo, getProdPage } from '@/api/prod'

const route = useRoute()
const router = useRouter()

const categoryList = ref([])
const currentCategoryId = ref(null)
const prodList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

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
  const { data } = await getCategoryInfo()
  categoryList.value = data || []
  if (!currentCategoryId.value && data?.length > 0) {
    currentCategoryId.value = data[0].categoryId
    fetchProducts()
  }
}

const fetchProducts = async () => {
  if (!currentCategoryId.value) return
  const { data } = await getProdPage(currentCategoryId.value, {
    current: pageNum.value,
    size: pageSize.value
  })
  prodList.value = data?.records || []
  total.value = data?.total || 0
}

const selectCategory = (categoryId) => {
  currentCategoryId.value = categoryId
  pageNum.value = 1
  router.push({ path: '/category', query: { id: categoryId } })
  fetchProducts()
}

const goProdDetail = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
.category-page {
  display: flex;
  gap: 20px;
  min-height: 600px;
}
.category-aside {
  width: 200px;
  background: #fff;
  padding: 20px;
  h3 {
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
  .category-list {
    list-style: none;
    li {
      padding: 10px 15px;
      cursor: pointer;
      border-radius: 4px;
      &:hover { background: #f5f5f5; }
      &.active {
        background: #3a86b9;
        color: #fff;
      }
    }
  }
}
.category-main {
  flex: 1;
  background: #fff;
  padding: 20px;
}
.prod-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.prod-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.prod-card {
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  .prod-img {
    width: 100%;
    height: 180px;
    object-fit: cover;
  }
  .prod-info { padding: 10px; }
  .prod-name {
    font-size: 14px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .prod-price {
    font-size: 16px;
    color: #e4393c;
    margin-top: 8px;
  }
}
</style>
