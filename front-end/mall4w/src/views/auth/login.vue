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
  min-height: calc(100vh - 100px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, $primary 0%, $primary-light 50%, $primary-dark 100%);
  position: relative;
  overflow: hidden;

  // 装饰性背景元素
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background:
      radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.1) 0%, transparent 40%),
      radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.08) 0%, transparent 40%),
      radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.05) 0%, transparent 60%);
    animation: float 20s ease-in-out infinite;
  }

  // 装饰性圆形
  &::after {
    content: '';
    position: absolute;
    width: 600px;
    height: 600px;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.1);
    top: -200px;
    right: -200px;
    pointer-events: none;
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(20px, -20px) rotate(5deg);
  }
  66% {
    transform: translate(-20px, 20px) rotate(-5deg);
  }
}

.login-box {
  width: 400px;
  padding: 44px 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  box-shadow:
    0 22px 70px rgba(0, 0, 0, 0.2),
    0 8px 25px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  animation: fadeInUp 0.5s $ease-out-expo;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, $primary, $primary-light, $primary-dark);
    border-radius: 20px 20px 0 0;
  }

  h2 {
    text-align: center;
    margin-bottom: 32px;
    color: $text-primary;
    font-weight: 600;
    font-size: 24px;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.02em;
  }

  :deep(.el-form-item) {
    margin-bottom: 22px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    padding: 3px 14px;
    box-shadow: none;
    border: 1.5px solid $border-light;
    background: $primary-bg;
    transition: all 0.25s $ease-out-expo;

    &:hover {
      border-color: #CBD5E1;
    }

    &.is-focus {
      border-color: $primary;
      background: #fff;
      box-shadow: 0 0 0 3px rgba(74, 159, 212, 0.12);
    }
  }

  :deep(.el-input__inner) {
    height: 44px;
    font-size: 14px;
    font-weight: 400;

    &::placeholder {
      color: $text-tertiary;
      font-weight: 400;
    }
  }

  :deep(.el-input__prefix) {
    font-size: 16px;
    color: $text-tertiary;
  }

  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 0.03em;
    background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
    border: none;
    border-radius: 12px;
    box-shadow: 0 5px 18px rgba(74, 159, 212, 0.32);
    transition: all 0.3s $ease-out-expo;
    margin-top: 6px;

    &:hover {
      background: linear-gradient(135deg, $primary-light 0%, $primary 100%);
      transform: translateY(-2px);
      box-shadow: 0 7px 24px rgba(74, 159, 212, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid $border-light;

    a {
      color: $text-secondary;
      font-size: 13px;
      font-weight: 400;
      transition: all 0.2s $ease-out-expo;
      padding: 5px 12px;
      border-radius: 6px;

      &:hover {
        color: $primary;
        background: $primary-bg;
      }
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式
@media (max-width: 480px) {
  .login-page {
    padding: 18px;
  }

  .login-box {
    width: 100%;
    padding: 32px 24px;
    border-radius: 16px;

    h2 {
      font-size: 20px;
      margin-bottom: 26px;
    }
  }
}
</style>
