import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { localeOptions, setStoredLocale } from '@/locales'

/**
 * 语言切换 composable
 * 提供语言切换、获取当前语言等功能
 */
export function useLocale() {
  const { locale, t } = useI18n()

  // 当前语言代码
  const currentLocale = computed(() => locale.value)

  // 当前语言标签
  const currentLocaleLabel = computed(() => {
    const option = localeOptions.find(item => item.value === locale.value)
    return option ? option.label : '简体中文'
  })

  // 切换语言
  const changeLocale = (newLocale) => {
    locale.value = newLocale
    setStoredLocale(newLocale)
    // 更新 HTML lang 属性
    document.documentElement.setAttribute('lang', newLocale)
  }

  return {
    locale,
    currentLocale,
    currentLocaleLabel,
    localeOptions,
    changeLocale,
    t
  }
}
