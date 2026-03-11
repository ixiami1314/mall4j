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
