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
