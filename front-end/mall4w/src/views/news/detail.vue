<template>
  <div class="news-detail container" v-loading="loading">
    <h2>{{ newsInfo.title }}</h2>
    <p class="news-time">{{ newsInfo.createTime }}</p>
    <div class="news-content" v-html="newsInfo.content"></div>
    <div class="back-btn">
      <el-button @click="$router.back()">{{ t('common.back') }}</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import http from '@/utils/http'

const { t } = useI18n()
const route = useRoute()
const loading = ref(true)
const newsInfo = ref({})

onMounted(() => {
  fetchNewsDetail()
})

const fetchNewsDetail = async () => {
  try {
    const { data } = await http({
      url: http.adornUrl('/shop/notice/info/' + route.params.id),
      method: 'get'
    })
    newsInfo.value = data || {}
  } catch (error) {
    console.error('获取新闻详情失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.news-detail {
  background: #fff;
  padding: 20px;
  min-height: 500px;
  h2 { margin-bottom: 10px; }
  .news-time {
    color: #999;
    font-size: 12px;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;
  }
  .news-content {
    line-height: 1.8;
  }
  .back-btn {
    margin-top: 30px;
  }
}
</style>
