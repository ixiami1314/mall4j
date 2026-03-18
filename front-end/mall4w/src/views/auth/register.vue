<template>
  <div class="register-page">
    <div class="register-box">
      <h2>{{ t('auth.register.title') }}</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="userName">
          <el-input v-model="form.userName" :placeholder="t('auth.register.usernamePlaceholder')" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="mobile">
          <el-input v-model="form.mobile" :placeholder="t('auth.register.phonePlaceholder')" :prefix-icon="Phone" />
        </el-form-item>
        <el-form-item v-if="!skipVerify" prop="code">
          <div class="code-input">
            <el-input v-model="form.code" :placeholder="t('auth.register.codePlaceholder')" :prefix-icon="Key" />
            <el-button :disabled="countdown > 0" @click="handleSendCode">
              {{ countdown > 0 ? `${countdown}s` : t('auth.register.getCode') }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" :placeholder="t('auth.register.passwordPlaceholder')" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" :placeholder="t('auth.register.confirmPasswordPlaceholder')" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-btn" :loading="loading" @click="handleRegister">
            {{ t('auth.register.submit') }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <router-link to="/login">{{ t('auth.register.login') }}</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { User, Lock, Phone, Key } from '@element-plus/icons-vue'
import { register, sendSms } from '@/api/user'
import { encryptPassword } from '@/utils/crypto'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
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
    callback(new Error(t('auth.validation.passwordMismatch')))
  } else {
    callback()
  }
}

const rules = {
  userName: [{ required: true, message: t('auth.validation.usernameRequired'), trigger: 'blur' }],
  mobile: [
    { required: true, message: t('auth.validation.phoneRequired'), trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: t('auth.validation.phoneFormat'), trigger: 'blur' }
  ],
  code: [{ required: true, message: t('auth.validation.codeRequired'), trigger: 'blur' }],
  password: [
    { required: true, message: t('auth.validation.passwordRequired'), trigger: 'blur' },
    { min: 6, message: t('auth.validation.passwordMinLength'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: t('auth.validation.confirmPasswordRequired'), trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

onMounted(() => {
  // 检查 URL 参数， skip_mobile_code_verify
  skipVerify.value = route.query.skip_mobile_code_verify === 'true' || route.query.skip_mobile_code_verify === ''
})

const handleSendCode = async () => {
  if (!form.mobile || !/^1[3-9]\d{9}$/.test(form.mobile)) {
    ElMessage.warning(t('auth.validation.phoneFormat'))
    return
  }
  try {
    await sendSms(form.mobile)
    ElMessage.success(t('auth.register.codeSent'))
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
    ElMessage.success(t('auth.register.success'))
    router.push('/login')
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

.register-page {
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

  &::after {
    content: '';
    position: absolute;
    width: 500px;
    height: 500px;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.1);
    bottom: -180px;
    left: -180px;
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

.register-box {
  width: 400px;
  padding: 40px 36px;
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
    margin-bottom: 28px;
    color: $text-primary;
    font-weight: 600;
    font-size: 24px;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
    letter-spacing: -0.02em;
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
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

  .code-input {
    display: flex;
    gap: 10px;

    .el-input {
      flex: 1;
    }

    .el-button {
      width: 110px;
      border-radius: 10px;
      font-size: 13px;
      font-weight: 500;
      border-color: $primary;
      color: $primary;

      &:hover:not(:disabled) {
        background: $primary-bg;
      }

      &:disabled {
        border-color: $border-light;
        color: $text-tertiary;
      }
    }
  }

  .register-btn {
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

  .register-footer {
    text-align: center;
    margin-top: 22px;
    padding-top: 18px;
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
  .register-page {
    padding: 18px;
  }

  .register-box {
    width: 100%;
    padding: 28px 22px;
    border-radius: 16px;

    h2 {
      font-size: 20px;
      margin-bottom: 22px;
    }
  }
}
</style>
