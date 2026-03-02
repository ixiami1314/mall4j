<template>
  <div class="collection-page">
    <h3>我的收藏</h3>
    <div v-if="prodList.length === 0" class="empty-collection">
      <el-empty description="暂无收藏商品" />
    </div>
    <div v-else class="prod-grid">
      <div v-for="prod in prodList" :key="prod.prodId" class="prod-card" @click="goProd(prod.prodId)">
        <img :src="prod.pic" class="prod-img" />
        <div class="prod-info">
          <p class="prod-name">{{ prod.prodName }}</p>
          <p class="prod-price">¥{{ prod.price }}</p>
        </div>
        <el-button type="danger" link class="remove-btn" @click.stop="handleRemove(prod.prodId)">取消收藏</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/utils/http'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const prodList = ref([])

onMounted(() => {
  fetchCollection()
})

const fetchCollection = async () => {
  const { data } = await http({
    url: http.adornUrl('/p/user/collection/prods'),
    method: 'get'
  })
  prodList.value = data || []
}

const handleRemove = async (prodId) => {
  await ElMessageBox.confirm('确定取消收藏吗？')
  await http({
    url: http.adornUrl('/p/user/collection/addOrDelete'),
    method: 'post',
    data: { prodId }
  })
  fetchCollection()
}

const goProd = (prodId) => {
  router.push(`/prod/${prodId}`)
}
</script>

<style lang="scss" scoped>
h3 {
  margin-bottom: 20px;
  padding-bottom: 15px;
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
  position: relative;
  .prod-img {
    width: 100%;
    height: 180px;
    object-fit: cover;
  }
  .prod-info { padding: 10px; }
  .prod-name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .prod-price { color: #e4393c; margin-top: 8px; }
  .remove-btn {
    position: absolute;
    top: 10px;
    right: 10px;
  }
}
</style>
