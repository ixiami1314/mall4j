import { defineStore } from 'pinia'
import http from '@/utils/http'

export const useAppStore = defineStore('app', {
  state: () => ({
    categoryList: [],
    searchHistory: []
  }),
  actions: {
    async fetchCategoryList() {
      const { data } = await http({
        url: http.adornUrl('/category/categoryInfo'),
        method: 'get'
      })
      this.categoryList = data || []
    },
    addSearchHistory(keyword) {
      if (!keyword) return
      const index = this.searchHistory.indexOf(keyword)
      if (index > -1) {
        this.searchHistory.splice(index, 1)
      }
      this.searchHistory.unshift(keyword)
      if (this.searchHistory.length > 10) {
        this.searchHistory.pop()
      }
      localStorage.setItem('searchHistory', JSON.stringify(this.searchHistory))
    },
    loadSearchHistory() {
      const history = localStorage.getItem('searchHistory')
      if (history) {
        this.searchHistory = JSON.parse(history)
      }
    }
  }
})
