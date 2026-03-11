# mall4w 前端 i18n 国际化实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 mall4w Vue 3 前端项目实现多语言国际化支持（简体中文、英文、日语、繁体中文）

**Architecture:** 采用 vue-i18n 作为国际化解决方案，通过 Composition API 模式集成，语言包按模块组织为扁平化 JSON 文件，支持浏览器语言自动检测和手动切换。

**Tech Stack:** Vue 3, vue-i18n, Element Plus, Pinia, Vue Router

---

## Task 1: 安装依赖并创建基础结构

**Files:**
- Modify: `front-end/mall4w/package.json`

**Step 1: 安装 vue-i18n 依赖**

Run:
```bash
cd /Users/junwei/workspace-fisher/mall4j/front-end/mall4w && pnpm add vue-i18n
```

Expected: 依赖安装成功

**Step 2: 创建 locales 目录结构**

Run:
```bash
mkdir -p /Users/junwei/workspace-fisher/mall4j/front-end/mall4w/src/locales
mkdir -p /Users/junwei/workspace-fisher/mall4j/front-end/mall4w/src/composables
```

Expected: 目录创建成功

**Step 3: Commit**

```bash
git add front-end/mall4w/package.json
git commit -m "chore(i18n): add vue-i18n dependency"
```

---

## Task 2: 创建语言包文件

**Files:**
- Create: `front-end/mall4w/src/locales/zh-CN.json`
- Create: `front-end/mall4w/src/locales/en-US.json`
- Create: `front-end/mall4w/src/locales/ja-JP.json`
- Create: `front-end/mall4w/src/locales/zh-TW.json`

**Step 1: 创建简体中文语言包**

Create `front-end/mall4w/src/locales/zh-CN.json`:

```json
{
  "common": {
    "submit": "提交",
    "cancel": "取消",
    "confirm": "确定",
    "delete": "删除",
    "edit": "编辑",
    "save": "保存",
    "loading": "加载中...",
    "search": "搜索",
    "all": "全部",
    "success": "操作成功",
    "error": "操作失败",
    "back": "返回"
  },
  "nav": {
    "home": "首页",
    "category": "全部分类",
    "cart": "购物车",
    "user": "我的"
  },
  "auth": {
    "login": {
      "title": "用户登录",
      "usernamePlaceholder": "请输入手机号/用户名",
      "passwordPlaceholder": "请输入密码",
      "submit": "登录",
      "register": "还没有账号？立即注册",
      "success": "登录成功"
    },
    "register": {
      "title": "用户注册",
      "usernamePlaceholder": "请输入用户名",
      "passwordPlaceholder": "请输入密码",
      "confirmPasswordPlaceholder": "请确认密码",
      "phonePlaceholder": "请输入手机号",
      "submit": "注册",
      "login": "已有账号？立即登录",
      "success": "注册成功"
    },
    "validation": {
      "usernameRequired": "请输入手机号/用户名",
      "passwordRequired": "请输入密码",
      "confirmPasswordRequired": "请确认密码",
      "passwordMismatch": "两次密码输入不一致",
      "phoneRequired": "请输入手机号",
      "phoneFormat": "手机号格式不正确"
    }
  },
  "header": {
    "searchPlaceholder": "搜索商品、品牌、店铺",
    "userCenter": "个人中心",
    "myOrders": "我的订单",
    "myCollection": "我的收藏",
    "logout": "退出登录",
    "login": "登录",
    "register": "注册"
  },
  "footer": {
    "shoppingGuide": "购物指南",
    "shoppingProcess": "购物流程",
    "memberIntro": "会员介绍",
    "pointsRule": "积分规则",
    "faq": "常见问题",
    "delivery": "配送方式",
    "deliveryRange": "配送范围",
    "deliveryFee": "配送费用",
    "deliveryTime": "配送时效",
    "payment": "支付方式",
    "onlinePayment": "在线支付",
    "balancePayment": "余额支付",
    "afterSale": "售后服务",
    "returnPolicy": "退换货政策",
    "returnProcess": "退换货流程",
    "refundNote": "退款说明",
    "aboutUs": "关于我们",
    "companyIntro": "公司介绍",
    "contactUs": "联系我们",
    "joinUs": "加入我们",
    "serviceHotline": "客服热线：{phone}",
    "serviceTime": "服务时间：{time}",
    "email": "邮箱：{email}",
    "copyright": "© 2024 mall4w 商城. All rights reserved.",
    "privacyPolicy": "隐私政策",
    "userAgreement": "用户协议"
  },
  "cart": {
    "title": "我的购物车",
    "itemCount": "共 {count} 件商品",
    "empty": "购物车还是空的，快去挑选心仪的商品吧~",
    "goShopping": "去购物",
    "selectAll": "全选",
    "deleteSelected": "删除选中",
    "clearCart": "清空购物车",
    "selected": "已选 {count} 件",
    "total": "合计：",
    "checkout": "去结算",
    "recommend": "猜你喜欢",
    "confirmDelete": "确定要删除该商品吗？",
    "confirmDeleteTitle": "删除确认",
    "confirmDeleteSelected": "确定要删除选中的 {count} 件商品吗？",
    "confirmClear": "确定要清空购物车吗？此操作不可恢复。",
    "confirmClearTitle": "清空购物车",
    "deleteSuccess": "删除成功",
    "clearSuccess": "购物车已清空",
    "selectItems": "请选择要结算的商品"
  },
  "order": {
    "confirm": {
      "title": "确认订单",
      "shippingInfo": "收货信息",
      "addAddress": "添加收货地址",
      "productInfo": "商品信息",
      "orderNote": "订单备注",
      "notePlaceholder": "选填，建议先和商家协商一致",
      "submit": "提交订单"
    },
    "list": {
      "title": "我的订单",
      "all": "全部",
      "pending": "待付款",
      "paid": "待发货",
      "shipped": "待收货",
      "completed": "已完成",
      "cancelled": "已取消",
      "pay": "去支付",
      "cancel": "取消订单",
      "confirm": "确认收货",
      "review": "去评价",
      "buyAgain": "再次购买"
    },
    "detail": {
      "title": "订单详情",
      "orderNo": "订单编号",
      "createTime": "创建时间",
      "payTime": "付款时间",
      "deliveryTime": "发货时间",
      "completeTime": "完成时间"
    },
    "payResult": {
      "success": "支付成功",
      "failed": "支付失败",
      "viewOrder": "查看订单",
      "continueShopping": "继续购物"
    }
  },
  "user": {
    "center": {
      "title": "个人中心",
      "welcome": "你好，{name}",
      "myOrders": "我的订单",
      "myCollection": "我的收藏",
      "myCoupon": "我的优惠券",
      "myAddress": "收货地址",
      "accountSettings": "账号设置"
    },
    "address": {
      "title": "收货地址",
      "add": "新增地址",
      "edit": "编辑地址",
      "setDefault": "设为默认",
      "default": "默认",
      "delete": "删除",
      "consignee": "收货人",
      "phone": "手机号",
      "area": "所在地区",
      "detail": "详细地址",
      "consigneePlaceholder": "请输入收货人姓名",
      "phonePlaceholder": "请输入手机号",
      "detailPlaceholder": "请输入详细地址"
    },
    "collection": {
      "title": "我的收藏",
      "empty": "暂无收藏商品",
      "addToCart": "加入购物车",
      "remove": "取消收藏"
    },
    "coupon": {
      "title": "我的优惠券",
      "unused": "未使用",
      "used": "已使用",
      "expired": "已过期",
      "empty": "暂无优惠券",
      "validity": "有效期：{start} - {end}",
      "useNow": "立即使用"
    }
  },
  "product": {
    "addToCart": "加入购物车",
    "buyNow": "立即购买",
    "spec": "规格",
    "quantity": "数量",
    "stock": "库存",
    "sold": "已售",
    "reviews": "评价",
    "description": "商品详情",
    "params": "规格参数"
  },
  "search": {
    "placeholder": "搜索商品",
    "empty": "未找到相关商品",
    "result": "搜索结果"
  },
  "category": {
    "title": "全部分类"
  },
  "news": {
    "title": "新闻资讯",
    "detail": "新闻详情"
  }
}
```

**Step 2: 创建英文语言包**

Create `front-end/mall4w/src/locales/en-US.json`:

```json
{
  "common": {
    "submit": "Submit",
    "cancel": "Cancel",
    "confirm": "Confirm",
    "delete": "Delete",
    "edit": "Edit",
    "save": "Save",
    "loading": "Loading...",
    "search": "Search",
    "all": "All",
    "success": "Success",
    "error": "Failed",
    "back": "Back"
  },
  "nav": {
    "home": "Home",
    "category": "Categories",
    "cart": "Cart",
    "user": "My"
  },
  "auth": {
    "login": {
      "title": "Login",
      "usernamePlaceholder": "Phone / Username",
      "passwordPlaceholder": "Password",
      "submit": "Login",
      "register": "No account? Register now",
      "success": "Login successful"
    },
    "register": {
      "title": "Register",
      "usernamePlaceholder": "Username",
      "passwordPlaceholder": "Password",
      "confirmPasswordPlaceholder": "Confirm Password",
      "phonePlaceholder": "Phone Number",
      "submit": "Register",
      "login": "Have an account? Login now",
      "success": "Registration successful"
    },
    "validation": {
      "usernameRequired": "Please enter phone/username",
      "passwordRequired": "Please enter password",
      "confirmPasswordRequired": "Please confirm password",
      "passwordMismatch": "Passwords do not match",
      "phoneRequired": "Please enter phone number",
      "phoneFormat": "Invalid phone format"
    }
  },
  "header": {
    "searchPlaceholder": "Search products, brands, stores",
    "userCenter": "My Account",
    "myOrders": "My Orders",
    "myCollection": "My Wishlist",
    "logout": "Logout",
    "login": "Login",
    "register": "Register"
  },
  "footer": {
    "shoppingGuide": "Shopping Guide",
    "shoppingProcess": "Shopping Process",
    "memberIntro": "Membership",
    "pointsRule": "Points Rules",
    "faq": "FAQ",
    "delivery": "Delivery",
    "deliveryRange": "Delivery Range",
    "deliveryFee": "Delivery Fee",
    "deliveryTime": "Delivery Time",
    "payment": "Payment",
    "onlinePayment": "Online Payment",
    "balancePayment": "Balance Payment",
    "afterSale": "After-sales",
    "returnPolicy": "Return Policy",
    "returnProcess": "Return Process",
    "refundNote": "Refund Info",
    "aboutUs": "About Us",
    "companyIntro": "Company",
    "contactUs": "Contact Us",
    "joinUs": "Join Us",
    "serviceHotline": "Hotline: {phone}",
    "serviceTime": "Service Time: {time}",
    "email": "Email: {email}",
    "copyright": "© 2024 mall4w. All rights reserved.",
    "privacyPolicy": "Privacy Policy",
    "userAgreement": "User Agreement"
  },
  "cart": {
    "title": "My Cart",
    "itemCount": "{count} items",
    "empty": "Your cart is empty. Start shopping now!",
    "goShopping": "Start Shopping",
    "selectAll": "Select All",
    "deleteSelected": "Delete Selected",
    "clearCart": "Clear Cart",
    "selected": "{count} selected",
    "total": "Total:",
    "checkout": "Checkout",
    "recommend": "You May Also Like",
    "confirmDelete": "Delete this item?",
    "confirmDeleteTitle": "Confirm Delete",
    "confirmDeleteSelected": "Delete {count} selected items?",
    "confirmClear": "Clear cart? This cannot be undone.",
    "confirmClearTitle": "Clear Cart",
    "deleteSuccess": "Deleted",
    "clearSuccess": "Cart cleared",
    "selectItems": "Please select items to checkout"
  },
  "order": {
    "confirm": {
      "title": "Confirm Order",
      "shippingInfo": "Shipping Info",
      "addAddress": "Add Address",
      "productInfo": "Product Info",
      "orderNote": "Order Note",
      "notePlaceholder": "Optional",
      "submit": "Submit Order"
    },
    "list": {
      "title": "My Orders",
      "all": "All",
      "pending": "Pending",
      "paid": "To Ship",
      "shipped": "To Receive",
      "completed": "Completed",
      "cancelled": "Cancelled",
      "pay": "Pay",
      "cancel": "Cancel",
      "confirm": "Confirm Receipt",
      "review": "Review",
      "buyAgain": "Buy Again"
    },
    "detail": {
      "title": "Order Detail",
      "orderNo": "Order No.",
      "createTime": "Created",
      "payTime": "Paid",
      "deliveryTime": "Shipped",
      "completeTime": "Completed"
    },
    "payResult": {
      "success": "Payment Successful",
      "failed": "Payment Failed",
      "viewOrder": "View Order",
      "continueShopping": "Continue Shopping"
    }
  },
  "user": {
    "center": {
      "title": "My Account",
      "welcome": "Hello, {name}",
      "myOrders": "My Orders",
      "myCollection": "My Wishlist",
      "myCoupon": "My Coupons",
      "myAddress": "Addresses",
      "accountSettings": "Settings"
    },
    "address": {
      "title": "Addresses",
      "add": "Add Address",
      "edit": "Edit Address",
      "setDefault": "Set Default",
      "default": "Default",
      "delete": "Delete",
      "consignee": "Name",
      "phone": "Phone",
      "area": "Region",
      "detail": "Address",
      "consigneePlaceholder": "Recipient name",
      "phonePlaceholder": "Phone number",
      "detailPlaceholder": "Detailed address"
    },
    "collection": {
      "title": "My Wishlist",
      "empty": "No items in wishlist",
      "addToCart": "Add to Cart",
      "remove": "Remove"
    },
    "coupon": {
      "title": "My Coupons",
      "unused": "Unused",
      "used": "Used",
      "expired": "Expired",
      "empty": "No coupons",
      "validity": "Valid: {start} - {end}",
      "useNow": "Use Now"
    }
  },
  "product": {
    "addToCart": "Add to Cart",
    "buyNow": "Buy Now",
    "spec": "Specification",
    "quantity": "Quantity",
    "stock": "Stock",
    "sold": "Sold",
    "reviews": "Reviews",
    "description": "Details",
    "params": "Specifications"
  },
  "search": {
    "placeholder": "Search products",
    "empty": "No products found",
    "result": "Search Results"
  },
  "category": {
    "title": "All Categories"
  },
  "news": {
    "title": "News",
    "detail": "News Detail"
  }
}
```

**Step 3: 创建日语语言包**

Create `front-end/mall4w/src/locales/ja-JP.json`:

```json
{
  "common": {
    "submit": "送信",
    "cancel": "キャンセル",
    "confirm": "確認",
    "delete": "削除",
    "edit": "編集",
    "save": "保存",
    "loading": "読み込み中...",
    "search": "検索",
    "all": "すべて",
    "success": "成功",
    "error": "失敗",
    "back": "戻る"
  },
  "nav": {
    "home": "ホーム",
    "category": "カテゴリ",
    "cart": "カート",
    "user": "マイページ"
  },
  "auth": {
    "login": {
      "title": "ログイン",
      "usernamePlaceholder": "電話番号/ユーザー名",
      "passwordPlaceholder": "パスワード",
      "submit": "ログイン",
      "register": "アカウントをお持ちでない方は新規登録",
      "success": "ログイン成功"
    },
    "register": {
      "title": "新規登録",
      "usernamePlaceholder": "ユーザー名",
      "passwordPlaceholder": "パスワード",
      "confirmPasswordPlaceholder": "パスワード確認",
      "phonePlaceholder": "電話番号",
      "submit": "登録",
      "login": "アカウントをお持ちの方はログイン",
      "success": "登録成功"
    },
    "validation": {
      "usernameRequired": "電話番号/ユーザー名を入力してください",
      "passwordRequired": "パスワードを入力してください",
      "confirmPasswordRequired": "パスワードを確認してください",
      "passwordMismatch": "パスワードが一致しません",
      "phoneRequired": "電話番号を入力してください",
      "phoneFormat": "電話番号の形式が正しくありません"
    }
  },
  "header": {
    "searchPlaceholder": "商品、ブランド、店舗を検索",
    "userCenter": "マイアカウント",
    "myOrders": "注文履歴",
    "myCollection": "お気に入り",
    "logout": "ログアウト",
    "login": "ログイン",
    "register": "新規登録"
  },
  "footer": {
    "shoppingGuide": "ショッピングガイド",
    "shoppingProcess": "ご購入の流れ",
    "memberIntro": "会員紹介",
    "pointsRule": "ポイントルール",
    "faq": "よくある質問",
    "delivery": "配送について",
    "deliveryRange": "配送エリア",
    "deliveryFee": "配送料",
    "deliveryTime": "配送時間",
    "payment": "支払い方法",
    "onlinePayment": "オンライン決済",
    "balancePayment": "残高決済",
    "afterSale": "アフターサービス",
    "returnPolicy": "返品ポリシー",
    "returnProcess": "返品手順",
    "refundNote": "返金について",
    "aboutUs": "会社概要",
    "companyIntro": "会社紹介",
    "contactUs": "お問い合わせ",
    "joinUs": "採用情報",
    "serviceHotline": "カスタマーサービス：{phone}",
    "serviceTime": "営業時間：{time}",
    "email": "メール：{email}",
    "copyright": "© 2024 mall4w. All rights reserved.",
    "privacyPolicy": "プライバシーポリシー",
    "userAgreement": "利用規約"
  },
  "cart": {
    "title": "ショッピングカート",
    "itemCount": "{count}点の商品",
    "empty": "カートは空です。お買い物を始めましょう！",
    "goShopping": "ショッピングへ",
    "selectAll": "すべて選択",
    "deleteSelected": "選択を削除",
    "clearCart": "カートを空にする",
    "selected": "{count}点選択中",
    "total": "合計：",
    "checkout": "レジへ進む",
    "recommend": "おすすめ商品",
    "confirmDelete": "この商品を削除しますか？",
    "confirmDeleteTitle": "削除確認",
    "confirmDeleteSelected": "選択した{count}点を削除しますか？",
    "confirmClear": "カートを空にしますか？この操作は取り消せません。",
    "confirmClearTitle": "カートを空にする",
    "deleteSuccess": "削除しました",
    "clearSuccess": "カートを空にしました",
    "selectItems": "決済する商品を選択してください"
  },
  "order": {
    "confirm": {
      "title": "注文確認",
      "shippingInfo": "配送先情報",
      "addAddress": "住所を追加",
      "productInfo": "商品情報",
      "orderNote": "注文メモ",
      "notePlaceholder": "任意",
      "submit": "注文を確定"
    },
    "list": {
      "title": "注文履歴",
      "all": "すべて",
      "pending": "未払い",
      "paid": "発送待ち",
      "shipped": "到着待ち",
      "completed": "完了",
      "cancelled": "キャンセル",
      "pay": "支払う",
      "cancel": "キャンセル",
      "confirm": "受け取り確認",
      "review": "レビュー",
      "buyAgain": "再購入"
    },
    "detail": {
      "title": "注文詳細",
      "orderNo": "注文番号",
      "createTime": "注文日時",
      "payTime": "支払日時",
      "deliveryTime": "発送日時",
      "completeTime": "完了日時"
    },
    "payResult": {
      "success": "支払い完了",
      "failed": "支払い失敗",
      "viewOrder": "注文を見る",
      "continueShopping": "ショッピングを続ける"
    }
  },
  "user": {
    "center": {
      "title": "マイページ",
      "welcome": "こんにちは、{name}さん",
      "myOrders": "注文履歴",
      "myCollection": "お気に入り",
      "myCoupon": "クーポン",
      "myAddress": "住所",
      "accountSettings": "設定"
    },
    "address": {
      "title": "住所",
      "add": "住所を追加",
      "edit": "住所を編集",
      "setDefault": "デフォルトに設定",
      "default": "デフォルト",
      "delete": "削除",
      "consignee": "宛名",
      "phone": "電話番号",
      "area": "地域",
      "detail": "住所",
      "consigneePlaceholder": "宛名を入力",
      "phonePlaceholder": "電話番号を入力",
      "detailPlaceholder": "詳細住所を入力"
    },
    "collection": {
      "title": "お気に入り",
      "empty": "お気に入りに商品がありません",
      "addToCart": "カートに入れる",
      "remove": "削除"
    },
    "coupon": {
      "title": "クーポン",
      "unused": "未使用",
      "used": "使用済み",
      "expired": "期限切れ",
      "empty": "クーポンがありません",
      "validity": "有効期限：{start} - {end}",
      "useNow": "今すぐ使う"
    }
  },
  "product": {
    "addToCart": "カートに入れる",
    "buyNow": "今すぐ購入",
    "spec": "仕様",
    "quantity": "数量",
    "stock": "在庫",
    "sold": "販売数",
    "reviews": "レビュー",
    "description": "商品詳細",
    "params": "仕様"
  },
  "search": {
    "placeholder": "商品を検索",
    "empty": "商品が見つかりません",
    "result": "検索結果"
  },
  "category": {
    "title": "すべてのカテゴリ"
  },
  "news": {
    "title": "ニュース",
    "detail": "ニュース詳細"
  }
}
```

**Step 4: 创建繁体中文语言包**

Create `front-end/mall4w/src/locales/zh-TW.json`:

```json
{
  "common": {
    "submit": "提交",
    "cancel": "取消",
    "confirm": "確定",
    "delete": "刪除",
    "edit": "編輯",
    "save": "儲存",
    "loading": "載入中...",
    "search": "搜尋",
    "all": "全部",
    "success": "操作成功",
    "error": "操作失敗",
    "back": "返回"
  },
  "nav": {
    "home": "首頁",
    "category": "全部分類",
    "cart": "購物車",
    "user": "我的"
  },
  "auth": {
    "login": {
      "title": "使用者登入",
      "usernamePlaceholder": "請輸入手機號/使用者名稱",
      "passwordPlaceholder": "請輸入密碼",
      "submit": "登入",
      "register": "還沒有帳號？立即註冊",
      "success": "登入成功"
    },
    "register": {
      "title": "使用者註冊",
      "usernamePlaceholder": "請輸入使用者名稱",
      "passwordPlaceholder": "請輸入密碼",
      "confirmPasswordPlaceholder": "請確認密碼",
      "phonePlaceholder": "請輸入手機號",
      "submit": "註冊",
      "login": "已有帳號？立即登入",
      "success": "註冊成功"
    },
    "validation": {
      "usernameRequired": "請輸入手機號/使用者名稱",
      "passwordRequired": "請輸入密碼",
      "confirmPasswordRequired": "請確認密碼",
      "passwordMismatch": "兩次密碼輸入不一致",
      "phoneRequired": "請輸入手機號",
      "phoneFormat": "手機號格式不正確"
    }
  },
  "header": {
    "searchPlaceholder": "搜尋商品、品牌、店鋪",
    "userCenter": "個人中心",
    "myOrders": "我的訂單",
    "myCollection": "我的收藏",
    "logout": "登出",
    "login": "登入",
    "register": "註冊"
  },
  "footer": {
    "shoppingGuide": "購物指南",
    "shoppingProcess": "購物流程",
    "memberIntro": "會員介紹",
    "pointsRule": "積分規則",
    "faq": "常見問題",
    "delivery": "配送方式",
    "deliveryRange": "配送範圍",
    "deliveryFee": "配送費用",
    "deliveryTime": "配送時效",
    "payment": "支付方式",
    "onlinePayment": "線上支付",
    "balancePayment": "餘額支付",
    "afterSale": "售後服務",
    "returnPolicy": "退換貨政策",
    "returnProcess": "退換貨流程",
    "refundNote": "退款說明",
    "aboutUs": "關於我們",
    "companyIntro": "公司介紹",
    "contactUs": "聯絡我們",
    "joinUs": "加入我們",
    "serviceHotline": "客服專線：{phone}",
    "serviceTime": "服務時間：{time}",
    "email": "信箱：{email}",
    "copyright": "© 2024 mall4w 商城. All rights reserved.",
    "privacyPolicy": "隱私權政策",
    "userAgreement": "使用者協議"
  },
  "cart": {
    "title": "我的購物車",
    "itemCount": "共 {count} 件商品",
    "empty": "購物車還是空的，快去挑選心儀的商品吧~",
    "goShopping": "去購物",
    "selectAll": "全選",
    "deleteSelected": "刪除選中",
    "clearCart": "清空購物車",
    "selected": "已選 {count} 件",
    "total": "合計：",
    "checkout": "去結帳",
    "recommend": "猜你喜歡",
    "confirmDelete": "確定要刪除該商品嗎？",
    "confirmDeleteTitle": "刪除確認",
    "confirmDeleteSelected": "確定要刪除選中的 {count} 件商品嗎？",
    "confirmClear": "確定要清空購物車嗎？此操作不可恢復。",
    "confirmClearTitle": "清空購物車",
    "deleteSuccess": "刪除成功",
    "clearSuccess": "購物車已清空",
    "selectItems": "請選擇要結帳的商品"
  },
  "order": {
    "confirm": {
      "title": "確認訂單",
      "shippingInfo": "收貨資訊",
      "addAddress": "新增收貨地址",
      "productInfo": "商品資訊",
      "orderNote": "訂單備註",
      "notePlaceholder": "選填，建議先和商家協商一致",
      "submit": "提交訂單"
    },
    "list": {
      "title": "我的訂單",
      "all": "全部",
      "pending": "待付款",
      "paid": "待出貨",
      "shipped": "待收貨",
      "completed": "已完成",
      "cancelled": "已取消",
      "pay": "去付款",
      "cancel": "取消訂單",
      "confirm": "確認收貨",
      "review": "去評價",
      "buyAgain": "再次購買"
    },
    "detail": {
      "title": "訂單詳情",
      "orderNo": "訂單編號",
      "createTime": "建立時間",
      "payTime": "付款時間",
      "deliveryTime": "出貨時間",
      "completeTime": "完成時間"
    },
    "payResult": {
      "success": "付款成功",
      "failed": "付款失敗",
      "viewOrder": "查看訂單",
      "continueShopping": "繼續購物"
    }
  },
  "user": {
    "center": {
      "title": "個人中心",
      "welcome": "你好，{name}",
      "myOrders": "我的訂單",
      "myCollection": "我的收藏",
      "myCoupon": "我的優惠券",
      "myAddress": "收貨地址",
      "accountSettings": "帳號設定"
    },
    "address": {
      "title": "收貨地址",
      "add": "新增地址",
      "edit": "編輯地址",
      "setDefault": "設為預設",
      "default": "預設",
      "delete": "刪除",
      "consignee": "收貨人",
      "phone": "手機號",
      "area": "所在地區",
      "detail": "詳細地址",
      "consigneePlaceholder": "請輸入收貨人姓名",
      "phonePlaceholder": "請輸入手機號",
      "detailPlaceholder": "請輸入詳細地址"
    },
    "collection": {
      "title": "我的收藏",
      "empty": "暫無收藏商品",
      "addToCart": "加入購物車",
      "remove": "取消收藏"
    },
    "coupon": {
      "title": "我的優惠券",
      "unused": "未使用",
      "used": "已使用",
      "expired": "已過期",
      "empty": "暫無優惠券",
      "validity": "有效期：{start} - {end}",
      "useNow": "立即使用"
    }
  },
  "product": {
    "addToCart": "加入購物車",
    "buyNow": "立即購買",
    "spec": "規格",
    "quantity": "數量",
    "stock": "庫存",
    "sold": "已售",
    "reviews": "評價",
    "description": "商品詳情",
    "params": "規格參數"
  },
  "search": {
    "placeholder": "搜尋商品",
    "empty": "未找到相關商品",
    "result": "搜尋結果"
  },
  "category": {
    "title": "全部分類"
  },
  "news": {
    "title": "新聞資訊",
    "detail": "新聞詳情"
  }
}
```

**Step 5: Commit**

```bash
git add front-end/mall4w/src/locales/*.json
git commit -m "feat(i18n): add language packs (zh-CN, en-US, ja-JP, zh-TW)"
```

---

## Task 3: 创建 i18n 实例配置

**Files:**
- Create: `front-end/mall4w/src/locales/index.js`

**Step 1: 创建 i18n 配置文件**

Create `front-end/mall4w/src/locales/index.js`:

```js
import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN.json'
import enUS from './en-US.json'
import jaJP from './ja-JP.json'
import zhTW from './zh-TW.json'

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS,
  'ja-JP': jaJP,
  'zh-TW': zhTW
}

// 语言选项
export const localeOptions = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en-US', label: 'English' },
  { value: 'ja-JP', label: '日本語' },
  { value: 'zh-TW', label: '繁體中文' }
]

// 浏览器语言映射
const browserLocaleMap = {
  zh: 'zh-CN',
  'zh-cn': 'zh-CN',
  'zh-tw': 'zh-TW',
  'zh-hk': 'zh-TW',
  en: 'en-US',
  'en-us': 'en-US',
  ja: 'ja-JP',
  'ja-jp': 'ja-JP'
}

// 检测浏览器语言
function detectBrowserLocale() {
  const lang = navigator.language.toLowerCase()
  return browserLocaleMap[lang] || browserLocaleMap[lang.split('-')[0]] || null
}

// 获取存储的语言
function getStoredLocale() {
  return localStorage.getItem('locale')
}

// 存储语言设置
export function setStoredLocale(locale) {
  localStorage.setItem('locale', locale)
}

// 获取初始语言
function getInitialLocale() {
  const stored = getStoredLocale()
  if (stored && messages[stored]) {
    return stored
  }
  const detected = detectBrowserLocale()
  if (detected) {
    return detected
  }
  return 'zh-CN'
}

const i18n = createI18n({
  legacy: false,
  locale: getInitialLocale(),
  fallbackLocale: 'zh-CN',
  messages
})

export default i18n
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/locales/index.js
git commit -m "feat(i18n): create i18n instance with auto-detect locale"
```

---

## Task 4: 创建 useLocale Composable

**Files:**
- Create: `front-end/mall4w/src/composables/useLocale.js`

**Step 1: 创建 useLocale composable**

Create `front-end/mall4w/src/composables/useLocale.js`:

```js
import { computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { localeOptions, setStoredLocale } from '@/locales'

export function useLocale() {
  const { locale, t } = useI18n()

  const currentLocale = computed(() => locale.value)

  const currentLocaleLabel = computed(() => {
    const option = localeOptions.find(item => item.value === locale.value)
    return option ? option.label : '简体中文'
  })

  const changeLocale = (newLocale) => {
    locale.value = newLocale
    setStoredLocale(newLocale)
    document.documentElement.setAttribute('lang', newLocale)
  }

  // 返回 locale 选项列表
  const getLocaleOptions = () => localeOptions

  return {
    locale,
    currentLocale,
    currentLocaleLabel,
    localeOptions,
    getLocaleOptions,
    changeLocale,
    t
  }
}
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/composables/useLocale.js
git commit -m "feat(i18n): add useLocale composable"
```

---

## Task 5: 创建 LocaleSwitch 组件

**Files:**
- Create: `front-end/mall4w/src/components/common/LocaleSwitch.vue`

**Step 1: 创建语言切换组件**

Create `front-end/mall4w/src/components/common/LocaleSwitch.vue`:

```vue
<template>
  <el-dropdown @command="handleChangeLocale" trigger="click">
    <span class="locale-switch">
      <el-icon><Globe /></el-icon>
      <span class="locale-label">{{ currentLocaleLabel }}</span>
      <el-icon class="arrow"><ArrowDown /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in localeOptions"
          :key="item.value"
          :command="item.value"
          :class="{ 'is-active': locale === item.value }"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { Globe, ArrowDown } from '@element-plus/icons-vue'
import { useLocale } from '@/composables/useLocale'

const { locale, currentLocaleLabel, localeOptions, changeLocale } = useLocale()

const handleChangeLocale = (localeValue) => {
  changeLocale(localeValue)
}
</script>

<style lang="scss" scoped>
.locale-switch {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 8px 14px;
  border-radius: 24px;
  transition: all 0.25s ease;

  &:hover {
    background: rgba(255, 103, 0, 0.06);
  }

  .locale-label {
    font-size: 14px;
    font-weight: 500;
    color: #333;
  }

  .arrow {
    font-size: 12px;
    color: #999;
  }
}

:deep(.el-dropdown-menu__item.is-active) {
  color: #ff6700;
  background: rgba(255, 103, 0, 0.06);
}
</style>
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/components/common/LocaleSwitch.vue
git commit -m "feat(i18n): add LocaleSwitch component"
```

---

## Task 6: 集成 i18n 到 main.js

**Files:**
- Modify: `front-end/mall4w/src/main.js`

**Step 1: 修改 main.js 添加 i18n 注册**

修改 `front-end/mall4w/src/main.js`:

```js
import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from '@/router'
import VueCookies from 'vue-cookies'
import i18n from '@/locales'

import '@/assets/styles/index.scss'

const app = createApp(App)

app.use(router)
const pinia = createPinia()
app.use(pinia)
app.use(i18n)

app.use(ElementPlus)
app.use(VueCookies, { expireTimes: '7d' })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
```

**Step 2: Commit**

```bash
git add front-end/mall4w/src/main.js
git commit -m "feat(i18n): integrate i18n into main.js"
```

---

## Task 7: 改造 Header 组件

**Files:**
- Modify: `front-end/mall4w/src/components/common/Header.vue`

**Step 1: 在 Header 中添加 i18n 支持**

在 `<script setup>` 部分添加：

```js
import { useI18n } from 'vue-i18n'
import LocaleSwitch from './LocaleSwitch.vue'

const { t } = useI18n()
```

**Step 2: 替换模板中的硬编码文本**

将以下文本替换为 i18n 调用：
- `搜索商品、品牌、店铺` → `:placeholder="t('header.searchPlaceholder')"`
- `搜索` → `{{ t('common.search') }}`
- `首页` → `{{ t('nav.home') }}`
- `全部分类` → `{{ t('nav.category') }}`
- `个人中心` → `{{ t('header.userCenter') }}`
- `我的订单` → `{{ t('header.myOrders') }}`
- `我的收藏` → `{{ t('header.myCollection') }}`
- `退出登录` → `{{ t('header.logout') }}`
- `登录` → `{{ t('header.login') }}`
- `注册` → `{{ t('header.register') }}`
- `购物车` → `{{ t('nav.cart') }}`

**Step 3: 在用户区域添加 LocaleSwitch 组件**

在导航菜单和用户区域之间添加：

```vue
<!-- 语言切换 -->
<LocaleSwitch />
```

**Step 4: Commit**

```bash
git add front-end/mall4w/src/components/common/Header.vue
git commit -m "feat(i18n): add i18n support to Header component"
```

---

## Task 8: 改造 Footer 组件

**Files:**
- Modify: `front-end/mall4w/src/components/common/Footer.vue`

**Step 1: 添加 i18n 支持**

```js
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
```

**Step 2: 替换所有硬编码文本为 i18n 调用**

**Step 3: Commit**

```bash
git add front-end/mall4w/src/components/common/Footer.vue
git commit -m "feat(i18n): add i18n support to Footer component"
```

---

## Task 9: 改造登录页面

**Files:**
- Modify: `front-end/mall4w/src/views/auth/login.vue`

**Step 1: 添加 i18n 支持**

```js
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
```

**Step 2: 替换文本和验证规则**

- `用户登录` → `{{ t('auth.login.title') }}`
- 验证消息也使用 i18n

**Step 3: Commit**

```bash
git add front-end/mall4w/src/views/auth/login.vue
git commit -m "feat(i18n): add i18n support to login page"
```

---

## Task 10: 改造注册页面

**Files:**
- Modify: `front-end/mall4w/src/views/auth/register.vue`

**Step 1-3: 同 Task 9 流程**

```bash
git add front-end/mall4w/src/views/auth/register.vue
git commit -m "feat(i18n): add i18n support to register page"
```

---

## Task 11: 改造购物车页面

**Files:**
- Modify: `front-end/mall4w/src/views/cart/index.vue`

**Step 1-3: 添加 i18n 支持并替换所有硬编码文本**

```bash
git add front-end/mall4w/src/views/cart/index.vue
git commit -m "feat(i18n): add i18n support to cart page"
```

---

## Task 12: 改造订单相关页面

**Files:**
- Modify: `front-end/mall4w/src/views/order/confirm.vue`
- Modify: `front-end/mall4w/src/views/order/detail.vue`
- Modify: `front-end/mall4w/src/views/order/list.vue`
- Modify: `front-end/mall4w/src/views/order/pay-result.vue`

**Step 1-3: 逐个添加 i18n 支持**

```bash
git add front-end/mall4w/src/views/order/
git commit -m "feat(i18n): add i18n support to order pages"
```

---

## Task 13: 改造用户中心页面

**Files:**
- Modify: `front-end/mall4w/src/views/user/index.vue`
- Modify: `front-end/mall4w/src/views/user/address.vue`
- Modify: `front-end/mall4w/src/views/user/collection.vue`
- Modify: `front-end/mall4w/src/views/user/coupon.vue`
- Modify: `front-end/mall4w/src/views/user/dashboard.vue`

**Step 1-3: 逐个添加 i18n 支持**

```bash
git add front-end/mall4w/src/views/user/
git commit -m "feat(i18n): add i18n support to user center pages"
```

---

## Task 14: 改造其他页面

**Files:**
- Modify: `front-end/mall4w/src/views/home/index.vue`
- Modify: `front-end/mall4w/src/views/category/index.vue`
- Modify: `front-end/mall4w/src/views/prod/detail.vue`
- Modify: `front-end/mall4w/src/views/search/index.vue`
- Modify: `front-end/mall4w/src/views/search/result.vue`
- Modify: `front-end/mall4w/src/views/news/list.vue`
- Modify: `front-end/mall4w/src/views/news/detail.vue`

**Step 1-3: 逐个添加 i18n 支持**

```bash
git add front-end/mall4w/src/views/
git commit -m "feat(i18n): add i18n support to remaining pages"
```

---

## Task 15: 测试与验证

**Step 1: 启动开发服务器**

```bash
cd /Users/junwei/workspace-fisher/mall4j/front-end/mall4w && pnpm dev
```

**Step 2: 验证功能**

- [ ] 语言切换组件显示正确
- [ ] 点击切换语言后页面文本更新
- [ ] 刷新页面后语言保持
- [ ] 清除 localStorage 后自动检测浏览器语言
- [ ] 各页面文本显示正确

**Step 3: 最终提交**

```bash
git add .
git commit -m "feat(i18n): complete i18n implementation for mall4w"
```

---

## 执行说明

执行此计划时，请使用 `superpowers:executing-plans` 或 `superpowers:subagent-driven-development` skill。
