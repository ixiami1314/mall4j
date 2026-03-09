import { defineStore } from 'pinia'
import http from '@/utils/http'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    totalCount: 0
  }),
  actions: {
    async fetchCart() {
      const res = await http({
        url: http.adornUrl('/p/shopCart/info'),
        method: 'post',
        data: {}
      })
      this.items = res.data || []
      this.calculateTotal()
    },
    async fetchCartCount() {
      try {
        const res = await http({
          url: http.adornUrl('/p/shopCart/prodCount'),
          method: 'get',
          params: { t: Date.now() }
        })
        this.totalCount = res.data || 0
      } catch (error) {
        console.error('获取购物车数量失败:', error)
        this.totalCount = 0
      }
    },
    calculateTotal() {
      let count = 0
      this.items.forEach(shop => {
        shop.shopCartItemDiscounts?.forEach(discount => {
          discount.shopCartItems?.forEach(item => {
            count += item.prodCount
          })
        })
      })
      this.totalCount = count
    },
    reset() {
      this.items = []
      this.totalCount = 0
    }
  }
})
