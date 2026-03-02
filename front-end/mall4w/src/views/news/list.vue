<template>
  <div class="news-list container">
    <h2>新闻公告</h2>
    <div v-if="newsList.length === 0" class="empty-news">
      <el-empty description="暂无新闻" />
    </div>
    <div v-else class="news-items">
      <div v-for="news in newsList" :key="news.id" class="news-item" @click="goDetail(news.id)">
        <span class="news-title">{{ news.title }}</span>
        <span class="news-time">{{ news.createTime }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticeList } from '@/api/index'

const router = useRouter()
const newsList = ref([])

onMounted(() => {
  fetchNews()
})

const fetchNews = async () => {
  const { data } = await getNoticeList({ current: 1, size: 20 })
  newsList.value = data?.records || []
}

const goDetail = (id) => {
  router.push(`/news/${id}`)
}
</script>

<style lang="scss" scoped>
.news-list {
  background: #fff;
  padding: 20px;
  min-height: 500px;
  h2 { margin-bottom: 20px; }
}
.news-item {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  &:hover .news-title { color: #3a86b9; }
  .news-time { color: #999; font-size: 12px; }
}
</style>
