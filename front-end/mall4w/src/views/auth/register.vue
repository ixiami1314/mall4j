<template>
  <div class="register-page">
    <div class="register-box">
      <h2>用户注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入手机号" :prefix-icon="Phone" />
        </el-form-item>
        <el-form-item v-if="!skipVerify" prop="code">
          <div class="code-input">
            <el-input v-model="form.code" placeholder="请输入验证码" :prefix-icon="Key" />
            <el-button :disabled="countdown > 0" @click="handleSendCode">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <router-link to="/login">已有账号？立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock, Phone, Key } from '@element-plus/icons-vue'
import { register, sendSms } from '@/api/user'
import { encryptPassword } from '@/utils/crypto'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)
const countdown = ref(0)
const skipVerify = ref(false)

const form = reactive({
  userName: '',
  mobile: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

onMounted(() => {
  // 检查 URL 参数， skip_mobile_code_verify
  skipVerify.value = route.query.skip_mobile_code_verify === 'true' || route.query.skip_mobile_code_verify === ''
})

const handleSendCode = async () => {
  if (!form.mobile || !/^1[3-9]\d{9}$/.test(form.mobile)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  try {
    await sendSms(form.mobile)
    ElMessage.success('验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (e) {
    console.error(e)
  }
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await register({
      userName: form.userName,
      mobile: form.mobile,
      passWord: encryptPassword(form.password),
      nickName: form.userName,
      checkRegisterSmsFlag: skipVerify.value ? '' : form.code,
      skipVerify: skipVerify.value
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: calc(100vh - 100px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-box {
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
  .code-input {
    display: flex;
    gap: 10px;
    .el-input {
      flex: 1;
    }
  }
  .register-btn {
    width: 100%;
    height: 42px;
    font-size: 16px;
  }
  .register-footer {
    text-align: center;
    margin-top: 20px;
    a {
      color: #3a86b9;
    }
  }
}
</style>
