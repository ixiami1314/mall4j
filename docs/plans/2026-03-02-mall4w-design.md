# 商城 Web 端（mall4w）设计文档

## 概述

mall4w 是 mall4j 电商项目的 PC 端商城前台，为用户提供商品浏览、搜索、购物车、下单、订单管理等完整的购物体验。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 | 3.2.47 |
| 构建工具 | Vite | 4.3.9 |
| UI 框架 | Element Plus | 2.3.6 |
| 状态管理 | Pinia | 2.0.33 |
| 路由 | Vue Router | 4.1.6 |
| HTTP 客户端 | Axios | 1.3.4 |
| 样式 | Sass | 1.59.3 |

## 项目结构

```
front-end/mall4w/
├── public/                  # 静态资源
├── src/
│   ├── App.vue             # 根组件
│   ├── main.js             # 入口文件
│   ├── api/                # API 接口
│   │   ├── user.js         # 用户相关
│   │   ├── prod.js         # 商品相关
│   │   ├── cart.js         # 购物车
│   │   ├── order.js        # 订单
│   │   ├── address.js      # 地址
│   │   ├── search.js       # 搜索
│   │   ├── collection.js   # 收藏
│   │   ├── coupon.js       # 优惠券
│   │   ├── news.js         # 新闻
│   │   └── index.js        # 首页
│   ├── assets/             # 资源文件
│   │   ├── images/
│   │   └── styles/
│   ├── components/         # 公共组件
│   │   ├── common/
│   │   │   ├── Header.vue
│   │   │   ├── Footer.vue
│   │   │   ├── Sidebar.vue
│   │   │   └── Breadcrumb.vue
│   │   ├── product/
│   │   │   ├── ProdCard.vue
│   │   │   ├── ProdList.vue
│   │   │   ├── ProdFilter.vue
│   │   │   └── SkuSelector.vue
│   │   ├── cart/
│   │   │   ├── CartItem.vue
│   │   │   └── CartSummary.vue
│   │   ├── order/
│   │   │   ├── OrderItem.vue
│   │   │   ├── OrderStatus.vue
│   │   │   └── AddressSelector.vue
│   │   └── user/
│   │       ├── UserMenu.vue
│   │       └── LoginForm.vue
│   ├── composables/        # 组合式函数
│   │   └── useCart.js
│   ├── layout/             # 布局组件
│   │   └── MainLayout.vue
│   ├── router/             # 路由配置
│   │   └── index.js
│   ├── stores/             # Pinia 状态管理
│   │   ├── user.js
│   │   ├── cart.js
│   │   ├── app.js
│   │   └── address.js
│   ├── utils/              # 工具函数
│   │   ├── request.js
│   │   └── auth.js
│   └── views/              # 页面组件
│       ├── home/
│       ├── category/
│       ├── cart/
│       ├── user/
│       ├── prod/
│       ├── order/
│       ├── search/
│       ├── auth/
│       └── news/
├── index.html
├── vite.config.js
├── package.json
└── .eslintrc.cjs
```

## 页面路由

| 路由 | 页面 | 说明 |
|------|------|------|
| `/` | 首页 | 轮播图、商品分类入口、推荐商品、限时特惠 |
| `/category` | 分类页 | 左侧分类列表、右侧商品展示 |
| `/search` | 搜索页 | 搜索框、搜索历史、热门搜索 |
| `/search/result` | 搜索结果 | 商品列表、筛选排序 |
| `/prod/:id` | 商品详情 | 商品信息、SKU选择、加入购物车 |
| `/cart` | 购物车 | 商品列表、数量修改、结算 |
| `/order/confirm` | 确认订单 | 地址选择、支付方式、提交订单 |
| `/order/pay-result` | 支付结果 | 支付成功/失败状态 |
| `/order/list` | 订单列表 | 全部/待付款/待发货/待收货/已完成 |
| `/order/detail/:id` | 订单详情 | 订单信息、物流查询 |
| `/user` | 用户中心 | 个人信息、订单入口、收藏、地址 |
| `/user/address` | 地址管理 | 收货地址列表、新增/编辑 |
| `/user/collection` | 我的收藏 | 收藏的商品列表 |
| `/user/coupon` | 我的优惠券 | 优惠券列表 |
| `/login` | 登录 | 账号密码/手机验证码登录 |
| `/register` | 注册 | 用户注册 |
| `/news` | 新闻列表 | 公告/资讯列表 |
| `/news/:id` | 新闻详情 | 新闻内容 |

## 状态管理

### User Store
- `userInfo` - 用户信息
- `token` - 认证令牌
- `isLogin` - 登录状态

### Cart Store
- `cartItems` - 购物车商品列表
- `totalCount` - 商品总数量
- `totalPrice` - 商品总价格

### App Store
- `categoryList` - 商品分类列表
- `searchHistory` - 搜索历史

### Address Store
- `addressList` - 地址列表
- `defaultAddress` - 默认地址

## API 设计

后端服务地址：`http://localhost:8085`

### 请求封装
- 基于 axios 封装
- 请求拦截：添加 token
- 响应拦截：统一错误处理
- 与 mall4v 保持一致的封装方式

### API 模块
- `api/user.js` - 登录、注册、用户信息
- `api/prod.js` - 商品列表、详情、分类
- `api/cart.js` - 购物车增删改查
- `api/order.js` - 订单创建、查询、取消
- `api/address.js` - 地址管理
- `api/search.js` - 搜索、热门关键词
- `api/collection.js` - 收藏功能
- `api/coupon.js` - 优惠券
- `api/news.js` - 新闻公告

## 开发配置

- **端口**: 9528
- **API 代理**: http://localhost:8085
- **包管理器**: pnpm

## 依赖

```json
{
  "dependencies": {
    "vue": "3.2.47",
    "vue-router": "4.1.6",
    "pinia": "2.0.33",
    "element-plus": "2.3.6",
    "@element-plus/icons-vue": "2.1.0",
    "axios": "1.3.4",
    "crypto-js": "4.1.1",
    "big.js": "6.2.1",
    "js-base64": "3.7.5"
  },
  "devDependencies": {
    "vite": "4.3.9",
    "@vitejs/plugin-vue": "4.1.0",
    "sass": "1.59.3",
    "eslint": "8.38.0"
  }
}
```

## 布局设计

### 顶部导航
- Logo
- 搜索框
- 导航菜单（首页、分类、优惠券）
- 用户信息（登录/注册 或 用户名/退出）
- 购物车入口（带数量角标）

### 主内容区
- 路由视图
- 面包屑导航

### 底部信息
- 版权信息
- 友情链接
- 联系方式
