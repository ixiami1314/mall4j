import { createRouter, createWebHistory } from 'vue-router'
import cookie from 'vue-cookies'
import MainLayout from '@/layout/MainLayout.vue'

// 不需要登录的路由
const publicRoutes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', name: 'home', component: () => import('@/views/home/index.vue'), meta: { title: '首页' } },
      { path: 'category', name: 'category', component: () => import('@/views/category/index.vue'), meta: { title: '分类' } },
      { path: 'prod/:id', name: 'prod-detail', component: () => import('@/views/prod/detail.vue'), meta: { title: '商品详情' } },
      { path: 'search', name: 'search', component: () => import('@/views/search/index.vue'), meta: { title: '搜索' } },
      { path: 'search/result', name: 'search-result', component: () => import('@/views/search/result.vue'), meta: { title: '搜索结果' } },
      { path: 'news', name: 'news', component: () => import('@/views/news/list.vue'), meta: { title: '新闻' } },
      { path: 'news/:id', name: 'news-detail', component: () => import('@/views/news/detail.vue'), meta: { title: '新闻详情' } }
    ]
  },
  { path: '/login', name: 'login', component: () => import('@/views/auth/login.vue'), meta: { title: '登录' } },
  { path: '/register', name: 'register', component: () => import('@/views/auth/register.vue'), meta: { title: '注册' } }
]

// 需要登录的路由
const authRoutes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: 'cart', name: 'cart', component: () => import('@/views/cart/index.vue'), meta: { title: '购物车', requiresAuth: true } },
      { path: 'order/confirm', name: 'order-confirm', component: () => import('@/views/order/confirm.vue'), meta: { title: '确认订单', requiresAuth: true } },
      { path: 'order/list', name: 'order-list', component: () => import('@/views/order/list.vue'), meta: { title: '订单列表', requiresAuth: true } },
      { path: 'order/detail/:id', name: 'order-detail', component: () => import('@/views/order/detail.vue'), meta: { title: '订单详情', requiresAuth: true } },
      { path: 'order/pay-result', name: 'pay-result', component: () => import('@/views/order/pay-result.vue'), meta: { title: '支付结果', requiresAuth: true } },
      {
        path: 'user',
        name: 'user',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户中心', requiresAuth: true },
        redirect: '/user/dashboard',
        children: [
          { path: 'dashboard', name: 'user-dashboard', component: () => import('@/views/user/dashboard.vue'), meta: { title: '个人中心' } },
          { path: 'order', name: 'user-order', component: () => import('@/views/order/list.vue'), meta: { title: '我的订单' } },
          { path: 'address', name: 'user-address', component: () => import('@/views/user/address.vue'), meta: { title: '收货地址' } },
          { path: 'collection', name: 'user-collection', component: () => import('@/views/user/collection.vue'), meta: { title: '我的收藏' } },
          { path: 'coupon', name: 'user-coupon', component: () => import('@/views/user/coupon.vue'), meta: { title: '我的优惠券' } }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...publicRoutes, ...authRoutes]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - mall4w` : 'mall4w'
  if (to.meta.requiresAuth && !cookie.get('Authorization')) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
