<template>
  <div class="search-page container">
    <div class="search-box">
      <el-input
        v-model="keyword"
        :placeholder="t('search.placeholder')"
        size="large"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button type="primary" @click="handleSearch">{{ t('common.search') }}</el-button>
        </template>
      </el-input>
    </div>

    <div class="search-history" v-if="searchHistory.length">
      <h4>{{ t('search.history') }}</h4>
      <div class="history-list">
        <el-tag
          v-for="(item, index) in searchHistory"
          :key="index"
          closable
          @click="quickSearch(item)"
          @close="removeHistory(index)"
        >
          {{ item }}
        </el-tag>
      </div>
    </div>

    <div class="hot-search" v-if="hotKeywords.length">
      <h4>{{ t('search.hot') }}</h4>
      <div class="hot-list">
        <el-tag
          v-for="(item, index) in hotKeywords"
          :key="index"
          type="warning"
          @click="quickSearch(item)"
        >
          {{ item }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { getHotSearch } from '@/api/search'
import { useAppStore } from '@/stores/app'

const { t } = useI18n()
const router = useRouter()
const appStore = useAppStore()

const keyword = ref('')
const searchHistory = ref([])
const hotKeywords = ref([])

onMounted(async () => {
  appStore.loadSearchHistory()
  searchHistory.value = [...appStore.searchHistory]
  const { data } = await getHotSearch()
  hotKeywords.value = data || []
})

const handleSearch = () => {
  if (!keyword.value.trim()) return
  appStore.addSearchHistory(keyword.value.trim())
  router.push({ path: '/search/result', query: { keyword: keyword.value } })
}

const quickSearch = (kw) => {
  keyword.value = kw
  handleSearch()
}

const removeHistory = (index) => {
  searchHistory.value.splice(index, 1)
  appStore.searchHistory = [...searchHistory.value]
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
}
</script>

<style lang="scss" scoped>
.search-page {
  background: #fff;
  padding: 40px;
}
.search-box {
  max-width: 600px;
  margin: 0 auto 40px;
}
.search-history, .hot-search {
  margin-bottom: 30px;
  h4 {
    margin-bottom: 15px;
    color: #666;
  }
  .history-list, .hot-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  .el-tag {
    cursor: pointer;
  }
}
</style>
