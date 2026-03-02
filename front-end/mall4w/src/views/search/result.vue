<template>
  <div class="search-result container">
    <div class="result-header">
      <span>搜索 "{{ keyword }}" 的结果，共 {{ total }} 件商品</span>
      <el-radio-group v-model="orderBy" @change="fetchProducts">
        <el-radio-button label="">综合</el-radio-button>
        <el-radio-button label="sales">销量</el-radio-button>
        <el-radio-button label="price">价格</el-radio-button>
      </el-radio-group>
    </div>
    <div v-if="prodList.length === 0" class="empty-result">
      <el-empty description="没有找到相关商品" />
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
    <el-pagination
      v-model:current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      layout="prev, pager, next"
      @current-change="fetchProducts"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProd } from '@/api/search'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const prodList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const orderBy = ref('')

onMounted(() => {
  keyword.value = route.query.keyword || ''
  fetchProducts()
})

watch(() => route.query.keyword, (newKeyword) => {
  keyword.value = newKeyword || ''
  pageNum.value = 1
  fetchProducts()
})

const fetchProducts = async () => {
  if (!keyword.value) return
  const { data } = await searchProd({
    keyword: keyword.value,
    current: pageNum.value,
    size: pageSize.value,
    orderBy: orderBy.value
  })
  prodList.value = data?.records || []
  total.value = data?.total || 0
}

const goProdDetail = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
.search-result {
  background: #fff;
  padding: 20px;
}
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.prod-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.prod-card {
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
