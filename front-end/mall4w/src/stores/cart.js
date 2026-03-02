import { defineStore } from 'pinia'
import http from '@/utils/http'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    totalCount: 0
  }),
  actions: {
    async fetchCart() {
      const { data } = await http({
        url: http.adornUrl('/p/shopCart/info'),
        method: 'post',
        data: {}
      })
      this.items = data || []
      this.calculateTotal()
    },
    async fetchCartCount() {
      const { data } = await http({
        url: http.adornUrl('/p/shopCart/prodCount'),
        method: 'get'
      })
      this.totalCount = data || 0
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
