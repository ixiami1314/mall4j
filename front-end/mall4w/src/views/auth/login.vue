<template>
  <div class="login-page">
    <div class="login-box">
      <h2>{{ t('auth.login.title') }}</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="userName">
          <el-input v-model="form.userName" :placeholder="t('auth.login.usernamePlaceholder')" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="passWord">
          <el-input
            v-model="form.passWord"
            type="password"
            :placeholder="t('auth.login.passwordPlaceholder')"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            {{ t('auth.login.submit') }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <router-link to="/register">{{ t('auth.login.register') }}</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/user'
import { encryptPassword } from '@/utils/crypto'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import cookie from 'vue-cookies'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  userName: '',
  passWord: ''
})

const rules = {
  userName: [{ required: true, message: t('auth.validation.usernameRequired'), trigger: 'blur' }],
  passWord: [{ required: true, message: t('auth.validation.passwordRequired'), trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const { data } = await login({
      userName: form.userName,
      passWord: encryptPassword(form.passWord)
    })
    // 登录成功后保存 token，格式为 "bearer + token"
    // 设置 cookie：key, value, expire(path), path
    cookie.set('Authorization', data.accessToken, 60 * 60 * 24 * 7, '/')
    await cartStore.fetchCartCount()
    ElMessage.success(t('auth.login.success'))
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
// 缓动函数
$ease-out-expo: cubic-bezier(0.16, 1, 0.3, 1);

// 颜色变量 - 淡蓝色系
$primary: #4A9FD4;
$primary-light: #6BB8E8;
$primary-dark: #3A8BC4;
$primary-bg: #F0F7FC;

$text-primary: #1E293B;
$text-regular: #334155;
$text-secondary: #64748B;
$text-tertiary: #94A3B8;

$border-light: #E2E8F0;

.login-page {
  min-height: calc(100vh - 120px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
  position: relative;
  overflow: hidden;

  // 装饰性背景
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background:
      radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.08) 0%, transparent 40%),
      radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.06) 0%, transparent 40%);
    animation: float 20s ease-in-out infinite;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(10px, -10px) rotate(2deg);
  }
}

.login-box {
  width: 360px;
  padding: 36px 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.4s $ease-out-expo;

  h2 {
    text-align: center;
    margin-bottom: 28px;
    color: $text-primary;
    font-weight: 600;
    font-size: 20px;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
  }

  :deep(.el-form-item) {
    margin-bottom: 18px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    padding: 2px 12px;
    box-shadow: none;
    border: 1px solid $border-light;
    background: #F8FAFC;
    transition: all 0.2s $ease-out-expo;

    &:hover {
      border-color: #CBD5E1;
    }

    &.is-focus {
      border-color: $primary;
      background: #fff;
      box-shadow: 0 0 0 2px rgba(74, 159, 212, 0.1);
    }
  }

  :deep(.el-input__inner) {
    height: 40px;
    font-size: 13px;

    &::placeholder {
      color: $text-tertiary;
    }
  }

  :deep(.el-input__prefix) {
    font-size: 14px;
    color: $text-tertiary;
  }

  .login-btn {
    width: 100%;
    height: 42px;
    font-size: 14px;
    font-weight: 600;
    background: $primary;
    border: none;
    border-radius: 8px;
    transition: all 0.2s $ease-out-expo;
    margin-top: 4px;

    &:hover {
      background: $primary-dark;
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 20px;
    padding-top: 16px;
    border-top: 1px solid $border-light;

    a {
      color: $text-secondary;
      font-size: 12px;
      transition: all 0.2s $ease-out-expo;

      &:hover {
        color: $primary;
      }
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式
@media (max-width: 480px) {
  .login-page {
    padding: 16px;
  }

  .login-box {
    width: 100%;
    padding: 28px 20px;
    border-radius: 10px;

    h2 {
      font-size: 18px;
      margin-bottom: 24px;
    }
  }
}
</style>
