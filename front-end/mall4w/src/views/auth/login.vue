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
    cookie.set('Authorization', 'bearer' + data.accessToken, 60 * 60 * 24 * 7, '/')
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
.login-page {
  min-height: calc(100vh - 100px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  h2 {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
  }
  .login-btn {
    width: 100%;
    height: 42px;
    font-size: 16px;
  }
  .login-footer {
    text-align: center;
    margin-top: 20px;
    a {
      color: #3a86b9;
    }
  }
}
</style>
