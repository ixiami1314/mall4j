<template>
  <div class="login-page">
    <div class="login-box">
      <h2>用户登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="userName">
          <el-input v-model="form.userName" placeholder="请输入手机号/用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="passWord">
          <el-input
            v-model="form.passWord"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <router-link to="/register">还没有账号？立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/user'
import { encryptPassword } from '@/utils/crypto'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import cookie from 'vue-cookies'
import { ElMessage } from 'element-plus'

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
  userName: [{ required: true, message: '请输入手机号/用户名', trigger: 'blur' }],
  passWord: [{ required: true, message: '请输入密码', trigger: 'blur' }]
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
    ElMessage.success('登录成功')
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

.login-page {
  min-height: calc(100vh - 100px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e4393c 0%, #ff6700 50%, #ff4500 100%);
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
  width: 420px;
  padding: 48px 44px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow:
    0 25px 80px rgba(0, 0, 0, 0.25),
    0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  animation: fadeInUp 0.6s $ease-out-expo;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #ff6700, #ff4500, #e4393c);
    border-radius: 24px 24px 0 0;
  }

  h2 {
    text-align: center;
    margin-bottom: 36px;
    color: #1a1a1a;
    font-weight: 700;
    font-size: 26px;
    font-family: 'Space Grotesk', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.02em;
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    padding: 4px 16px;
    box-shadow: none;
    border: 2px solid #e8e4de;
    background: #faf9f7;
    transition: all 0.3s $ease-out-expo;

    &:hover {
      border-color: #d4cfc6;
    }

    &.is-focus {
      border-color: #ff6700;
      background: #fff;
      box-shadow: 0 0 0 4px rgba(255, 103, 0, 0.1);
    }
  }

  :deep(.el-input__inner) {
    height: 48px;
    font-size: 15px;
    font-weight: 450;

    &::placeholder {
      color: #999;
      font-weight: 400;
    }
  }

  :deep(.el-input__prefix) {
    font-size: 18px;
    color: #999;
  }

  .login-btn {
    width: 100%;
    height: 52px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 0.05em;
    background: linear-gradient(135deg, #ff6700 0%, #ff4500 100%);
    border: none;
    border-radius: 14px;
    box-shadow: 0 6px 20px rgba(255, 103, 0, 0.35);
    transition: all 0.35s $ease-out-expo;
    margin-top: 8px;

    &:hover {
      background: linear-gradient(135deg, #ff8c42 0%, #ff6700 100%);
      transform: translateY(-2px);
      box-shadow: 0 8px 28px rgba(255, 103, 0, 0.45);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 28px;
    padding-top: 24px;
    border-top: 1px solid #f0ede8;

    a {
      color: #666;
      font-size: 14px;
      font-weight: 450;
      transition: all 0.25s $ease-out-expo;
      padding: 6px 12px;
      border-radius: 8px;

      &:hover {
        color: #ff6700;
        background: rgba(255, 103, 0, 0.06);
      }
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式
@media (max-width: 480px) {
  .login-page {
    padding: 20px;
  }

  .login-box {
    width: 100%;
    padding: 36px 28px;
    border-radius: 20px;

    h2 {
      font-size: 22px;
      margin-bottom: 28px;
    }
  }
}
</style>
