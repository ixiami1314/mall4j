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
