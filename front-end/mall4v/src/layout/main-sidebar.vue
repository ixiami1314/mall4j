<template>
  <aside
    class="site-sidebar"
    :class="{ 'is-collapsed': sidebarFold }"
  >
    <el-scrollbar class="sidebar-scrollbar">
      <!-- Home Menu Item -->
      <div
        class="menu-item"
        :class="{ 'is-active': selectLeftId === '' }"
        @click="toHome()"
      >
        <el-tooltip
          :content="sidebarFold ? $t('home') : ''"
          placement="right"
          :disabled="!sidebarFold"
        >
          <div class="menu-item-content">
            <svg-icon
              icon-class="icon-shouye"
              class="menu-icon"
            />
            <span
              v-show="!sidebarFold"
              class="menu-text"
            >{{ $t('home') }}</span>
          </div>
        </el-tooltip>
      </div>

      <!-- Menu List -->
      <template
        v-for="menu in menuList"
        :key="menu.menuId"
      >
        <div
          v-if="!menu.hidden"
          class="menu-group"
        >
          <!-- Menu with submenu -->
          <template v-if="menu.list && menu.list.length">
            <el-tooltip
              :content="sidebarFold ? menu.name : ''"
              placement="right"
              :disabled="!sidebarFold"
            >
              <div
                class="menu-item"
                :class="{
                  'is-active': selectLeftId === menu.menuId,
                  'is-expanded': expandedMenuId === menu.menuId
                }"
                @click="toggleSubmenu(menu)"
              >
                <div class="menu-item-content">
                  <svg-icon
                    v-if="menu.icon"
                    :icon-class="`icon-${menu.icon}`"
                    class="menu-icon"
                  />
                  <span
                    v-show="!sidebarFold"
                    class="menu-text"
                  >{{ menu.name }}</span>
                  <svg-icon
                    v-show="!sidebarFold"
                    icon-class="icon-arrow-down"
                    class="expand-arrow"
                    :class="{ 'is-rotated': expandedMenuId === menu.menuId }"
                  />
                </div>
              </div>
            </el-tooltip>

            <!-- Inline Submenu -->
            <transition name="submenu">
              <div
                v-show="!sidebarFold && expandedMenuId === menu.menuId"
                class="submenu"
              >
                <div
                  v-for="subItem in menu.list"
                  :key="subItem.menuId"
                  class="submenu-item"
                  :class="{ 'is-active': isSubmenuActive(subItem) }"
                  @click="gotoRouteHandle(subItem)"
                >
                  <span class="submenu-dot" />
                  <span class="submenu-text">{{ subItem.name }}</span>
                </div>
              </div>
            </transition>
          </template>

          <!-- Menu without submenu -->
          <template v-else>
            <el-tooltip
              :content="sidebarFold ? menu.name : ''"
              placement="right"
              :disabled="!sidebarFold"
            >
              <div
                class="menu-item"
                :class="{ 'is-active': selectLeftId === menu.menuId }"
                @click="gotoRouteHandle(menu)"
              >
                <div class="menu-item-content">
                  <svg-icon
                    v-if="menu.icon"
                    :icon-class="menu.icon || ''"
                    class="menu-icon"
                  />
                  <span
                    v-show="!sidebarFold"
                    class="menu-text"
                  >{{ menu.name }}</span>
                </div>
              </div>
            </el-tooltip>
          </template>
        </div>
      </template>
    </el-scrollbar>
  </aside>
</template>

<script setup>
const route = useRoute()
const router = useRouter()
const commonStore = useCommonStore()
const dynamicMenuRoutes = ref([])
const expandedMenuId = ref(null)

const sidebarFold = computed(() => commonStore.sidebarFold)
const menuList = computed({
  get: () => {
    return commonStore.menuList
  },
  set: val => {
    commonStore.updateMenuList(val)
  }
})

const selectLeftId = computed({
  get: () => {
    return commonStore.selectLeftId || ''
  }
})

onBeforeMount(() => {
  menuList.value = JSON.parse(sessionStorage.getItem('menuList') || '[]')
  dynamicMenuRoutes.value = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
  routeHandle(route)
  // Auto-expand submenu based on current route
  autoExpandMenu()
})

// Auto expand menu based on current route
const autoExpandMenu = () => {
  const currentPath = route.name
  menuList.value.forEach(menu => {
    if (menu.list) {
      const found = menu.list.some(sub => sub.url === currentPath || selectLeftId.value === menu.menuId)
      if (found) {
        expandedMenuId.value = menu.menuId
      }
    }
  })
}

// Check if submenu item is active
const isSubmenuActive = (subItem) => {
  return route.name === subItem.url || commonStore.selectRightId === subItem.menuId
}

const toHome = () => {
  router.push({ name: 'home' })
  expandedMenuId.value = null
  commonStore.updateSidebarFold(true)
  commonStore.updateSelectLeftId('')
  commonStore.updateSelectRightId('')
}

const routeHandle = route => {
  if (route.name === 'home') {
    expandedMenuId.value = null
    commonStore.updateSelectLeftId('')
    commonStore.updateSelectRightId('')
  }
}

const gotoRouteHandle = menu => {
  // Toggle submenu if clicking same menu
  if (menu.list) {
    toggleSubmenu(menu)
    return
  }

  // Handle message menu specially
  if (menu.name === '消息' || menu.name === 'Message') {
    window.open(location.href.split('#')[0] + '#/imBox', '_blank', 'noopener,noreferrer')
    return
  }

  commonStore.updateSelectLeftId(menu.menuId || '')
  commonStore.updateSelectRightId('')
  router.push({ name: menu.url })
}

const toggleSubmenu = menu => {
  if (sidebarFold.value) {
    // When collapsed, expand sidebar first
    commonStore.updateSidebarFold(false)
    expandedMenuId.value = menu.menuId
  } else {
    // Toggle submenu
    if (expandedMenuId.value === menu.menuId) {
      expandedMenuId.value = null
    } else {
      expandedMenuId.value = menu.menuId
    }
  }
  commonStore.updateSelectLeftId(menu.menuId || '')
}

// Watch route changes to update active states
watch(() => route.name, () => {
  autoExpandMenu()
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.site-sidebar {
  width: $sidebar-width-expanded;
  height: calc(100vh - $navbar--height);
  background-color: $sidebar--background-color;
  transition: width $transition-base;
  overflow: hidden;
  flex-shrink: 0;

  &.is-collapsed {
    width: $sidebar-width-collapsed;

    .menu-item-content {
      justify-content: center;
      padding: 0;
    }

    .menu-icon {
      margin-right: 0;
    }
  }
}

.sidebar-scrollbar {
  height: 100%;
}

.menu-group {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  color: $sidebar--color-text;
  cursor: pointer;
  transition: all $transition-fast;
  position: relative;
  border-left: 3px solid transparent;

  &:hover {
    background: rgba(255, 255, 255, 0.05);
    color: $sidebar--color-text-active;
  }

  &.is-active {
    background: rgba($--color-primary, 0.15);
    color: $--color-primary;
    border-left-color: $--color-primary;

    .menu-icon {
      color: $--color-primary;
    }
  }

  &.is-expanded {
    .menu-text {
      color: $sidebar--color-text-active;
    }
  }
}

.menu-item-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.menu-icon {
  font-size: 18px;
  margin-right: 12px;
  color: inherit;
  transition: color $transition-fast;
  flex-shrink: 0;
}

.menu-text {
  font-size: $font-size-base;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: opacity $transition-fast;
}

.expand-arrow {
  margin-left: auto;
  font-size: 12px;
  transition: transform $transition-fast;

  &.is-rotated {
    transform: rotate(180deg);
  }
}

// Submenu styles
.submenu {
  background-color: $sidebar--background-color-dark;
  overflow: hidden;
}

.submenu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px 12px 44px;
  color: $sidebar--color-text;
  cursor: pointer;
  transition: all $transition-fast;
  font-size: $font-size-sm;

  &:hover {
    background: rgba(255, 255, 255, 0.05);
    color: $sidebar--color-text-active;
  }

  &.is-active {
    color: $--color-primary;

    .submenu-dot {
      background-color: $--color-primary;
    }
  }
}

.submenu-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: $sidebar--color-text;
  margin-right: 12px;
  flex-shrink: 0;
  transition: background-color $transition-fast;
}

.submenu-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

// Submenu transition
.submenu-enter-active,
.submenu-leave-active {
  transition: all $transition-base;
  transform-origin: top;
}

.submenu-enter-from,
.submenu-leave-to {
  opacity: 0;
  transform: scaleY(0);
}

// Deep selector for el-scrollbar
:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

:deep(.el-scrollbar__bar.is-horizontal) {
  display: none;
}

// Tooltip styling for collapsed state
:deep(.el-tooltip__popper) {
  background-color: $--color-text-primary !important;
  border: none;
  font-size: $font-size-sm;
}
</style>
