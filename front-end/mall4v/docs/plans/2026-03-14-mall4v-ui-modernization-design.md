# Mall4v 管理后台 UI 现代化设计

## 概述

将 mall4v 管理后台从当前的基础样式升级为现代简约风格，提升专业感和用户体验。

## 设计决策

| 项目 | 选择 |
|------|------|
| 设计风格 | 现代简约 |
| 美化范围 | 登录页、侧边栏、顶部栏、全局样式 |
| 改造程度 | 中度重构 |
| 实现方案 | CSS 变量 + 组件优化 |

## 全局设计规范

### 配色系统

| 用途 | 颜色 | CSS 变量 |
|------|------|----------|
| 主色 | `#3B82F6` | `--color-primary` |
| 主色浅 | `#EFF6FF` | `--color-primary-light` |
| 成功 | `#10B981` | `--color-success` |
| 警告 | `#F59E0B` | `--color-warning` |
| 错误 | `#EF4444` | `--color-danger` |
| 文字主色 | `#1F2937` | `--color-text-primary` |
| 文字次色 | `#6B7280` | `--color-text-secondary` |
| 边框色 | `#E5E7EB` | `--color-border` |
| 背景色 | `#F9FAFB` | `--color-bg` |

### 圆角

```scss
$radius-sm: 4px;   // 小组件
$radius-md: 8px;   // 卡片、按钮
$radius-lg: 12px;  // 大容器
```

### 阴影

```scss
$shadow-sm: 0 1px 2px rgba(0,0,0,0.05);
$shadow-md: 0 4px 6px -1px rgba(0,0,0,0.1);
$shadow-lg: 0 10px 15px -3px rgba(0,0,0,0.1);
```

### 字体

```scss
$font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
$font-size-sm: 12px;
$font-size-base: 14px;
$font-size-lg: 16px;
$font-size-xl: 18px;
```

## 登录页设计

### 布局

- 左右分栏布局
- 左侧：品牌区域（Logo、名称、特点介绍）
- 右侧：登录表单

### 样式要点

- 背景：纯色渐变 + 几何装饰
- 登录卡片：白色背景，16px 圆角，阴影
- 输入框：带图标，10px 圆角
- 按钮：主色，10px 圆角，48px 高度

### 左侧品牌区

```scss
.brand-section {
  background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
  border-radius: 0 24px 24px 0;
}
```

## 侧边栏设计

### 布局

- 宽度：220px（展开）/ 80px（折叠）
- 可折叠
- 内联子菜单

### 样式要点

- 背景：深灰 `#1F2937`
- 图标：左侧对齐
- 文字：左对齐
- 选中态：左侧蓝色边条 + 浅色背景
- 子菜单：深色背景 `#111827`，左侧缩进

### 选中态样式

```scss
.menu-item.active {
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
  border-left: 3px solid #3B82F6;
}
```

## 顶部栏设计

### 布局

- 高度：60px
- 从左到右：折叠按钮 | 品牌 | 面包屑 | 通知 | 用户菜单

### 样式要点

- 背景：白色
- 底部阴影：`0 1px 4px rgba(0, 0, 0, 0.08)`
- 用户头像：36px 圆形

### 用户下拉菜单

- 修改密码
- 退出登录

## 文件改动清单

```
src/
├── styles/
│   ├── _variables.scss      # 更新配色变量
│   ├── index.scss           # 引入新样式
│   └── _modern.scss         # 新增：现代样式模块
├── layout/
│   ├── main.vue             # 布局结构调整
│   ├── main-navbar.vue      # 顶部栏重构
│   ├── main-sidebar.vue     # 侧边栏重构
│   └── main-content.vue     # 内容区调整
└── views/common/login/
    └── index.vue            # 登录页重构
```

## 预计改动量

- 新增代码：约 300-400 行 SCSS
- 修改文件：6-8 个 Vue 组件
- 风险评估：低（仅样式改动，不涉及业务逻辑）

## 实施顺序

1. 更新全局样式变量
2. 重构登录页
3. 重构侧边栏
4. 重构顶部栏
5. 调整内容区布局
6. 测试并部署
