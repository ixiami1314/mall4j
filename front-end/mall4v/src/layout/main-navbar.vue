<template>
  <nav class="site-navbar">
    <!-- Left Section -->
    <div class="navbar-left">
      <button
        class="collapse-btn"
        @click="setSidebarFold"
      >
        <svg-icon icon-class="icon-zhedie" />
      </button>
      <span class="brand-text">Mall4j</span>
    </div>

    <!-- Right Section -->
    <div class="navbar-right">
      <el-dropdown
        placement="bottom-end"
        :show-timeout="0"
      >
        <div class="user-dropdown-trigger">
          <div class="user-avatar">
            {{ userInitials }}
          </div>
          <span class="user-name">{{ userName }}</span>
          <el-icon class="dropdown-icon">
            <ArrowDown />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="user-dropdown-menu">
            <el-dropdown-item @click="updatePasswordHandle">
              <el-icon><Key /></el-icon>
              <span>修改密码</span>
            </el-dropdown-item>
            <el-dropdown-item
              divided
              @click="logoutHandle"
            >
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 弹窗, 修改密码 -->
    <UpdatePassword
      v-if="updatePassowrdVisible"
      ref="updatePassowrdRef"
    />
  </nav>
</template>

<script setup>
import { ArrowDown, Key, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import UpdatePassword from './main-navbar-update-password.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userName = computed(() => userStore.name)
const userInitials = computed(() => {
  const name = userName.value || ''
  return name.charAt(0).toUpperCase()
})

const commonStore = useCommonStore()
const sidebarFold = computed(() => commonStore.sidebarFold)

const setSidebarFold = () => {
  const len = commonStore.selectMenu.length
  const flag = sessionStorage.getItem('isExpand')
  if ((route.path === '/home' || len === 1) && flag === '0') {
    commonStore.updateSidebarFold(true)
  } else {
    const foldFlag = sidebarFold.value
    commonStore.updateSidebarFold(!foldFlag)
  }
}

const logoutHandle = () => {
  ElMessageBox.confirm('确定进行[退出]操作?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/logOut'),
      method: 'post',
      data: http.adornData()
    }).then(() => {
      clearLoginInfo()
      router.push({ name: 'login' })
    })
  })
}

const updatePassowrdVisible = ref(false)
const updatePassowrdRef = ref(null)
/**
 * 修改密码
 */
const updatePasswordHandle = () => {
  updatePassowrdVisible.value = true
  nextTick(() => {
    updatePassowrdRef.value?.init()
  })
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.site-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $navbar--height;
  background: $navbar--background-color;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 20px;
  z-index: 10;
}

// Left Section
.navbar-left {
  display: flex;
  align-items: center;
  gap: 12px;

  .collapse-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border: none;
    background: transparent;
    border-radius: $border-radius-md;
    cursor: pointer;
    transition: background-color $transition-fast;

    &:hover {
      background-color: $--color-primary-light;
    }

    :deep(.svg-icon) {
      width: 20px;
      height: 20px;
      color: $--color-text-secondary;
      transition: color $transition-fast;
    }

    &:hover :deep(.svg-icon) {
      color: $--color-primary;
    }
  }

  .brand-text {
    font-size: $font-size-lg;
    font-weight: 600;
    color: $--color-text-primary;
  }
}

// Right Section
.navbar-right {
  display: flex;
  align-items: center;
}

.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: $border-radius-md;
  transition: background-color $transition-fast;

  &:hover {
    background-color: $--color-background;
  }
}

.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: $--color-primary;
  color: #fff;
  font-size: $font-size-sm;
  font-weight: 600;
  text-transform: uppercase;
}

.user-name {
  font-size: $font-size-base;
  font-weight: 500;
  color: $--color-text-primary;
}

.dropdown-icon {
  font-size: 12px;
  color: $--color-text-secondary;
  transition: transform $transition-fast;
}

// Dropdown Menu Styles
:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-size: $font-size-base;
  color: $--color-text-primary;
  transition: all $transition-fast;

  .el-icon {
    font-size: 16px;
    color: $--color-text-secondary;
    transition: color $transition-fast;
  }

  &:hover {
    background-color: $--color-primary-light;
    color: $--color-primary;

    .el-icon {
      color: $--color-primary;
    }
  }
}
</style>
