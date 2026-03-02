import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: '',
    nickName: '',
    pic: '',
    mobile: ''
  }),
  getters: {
    isLogin: (state) => !!state.userId
  },
  actions: {
    setUser(user) {
      this.userId = user.userId
      this.nickName = user.nickName
      this.pic = user.pic
      this.mobile = user.mobile
    },
    reset() {
      this.userId = ''
      this.nickName = ''
      this.pic = ''
      this.mobile = ''
    }
  }
})
