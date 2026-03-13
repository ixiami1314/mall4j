<template>
  <div class="login-container">
    <!-- Left Brand Section -->
    <div class="login-brand">
      <!-- Decorative Circles -->
      <div class="decorative-circle circle-1"></div>
      <div class="decorative-circle circle-2"></div>
      <div class="decorative-circle circle-3"></div>

      <!-- Brand Content -->
      <div class="brand-content">
        <div class="brand-logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 7H4C2.89543 7 2 7.89543 2 9V19C2 20.1046 2.89543 21 4 21H20C21.1046 21 22 20.1046 22 19V9C22 7.89543 21.1046 7 20 7Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 7V5C16 3.89543 15.1046 3 14 3H10C8.89543 3 8 3.89543 8 5V7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 12V16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <path d="M10 14H14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="brand-title">Mall4j</h1>
        <p class="brand-subtitle">企业级电商管理平台</p>

        <ul class="brand-features">
          <li>
            <span class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </span>
            <span>高效便捷的商品管理</span>
          </li>
          <li>
            <span class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </span>
            <span>安全稳定的订单系统</span>
          </li>
          <li>
            <span class="feature-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </span>
            <span>智能化的数据分析</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Right Form Section -->
    <div class="login-form-section">
      <div class="login-card">
        <div class="card-header">
          <h2 class="card-title">欢迎登录</h2>
          <p class="card-subtitle">请输入您的账号信息</p>
        </div>

        <el-form
          ref="dataFormRef"
          :model="dataForm"
          :rules="dataRule"
          status-icon
          @keyup.enter="dataFormSubmit()"
        >
          <el-form-item prop="userName">
            <el-input
              v-model="dataForm.userName"
              placeholder="请输入账号"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="dataForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="isSubmitting"
              class="login-btn"
              @click="dataFormSubmit()"
            >
              {{ isSubmitting ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="card-footer">
          Copyright © 2019 广州市蓝海创新科技有限公司
        </div>
      </div>
    </div>

    <Verify
      ref="verifyRef"
      :captcha-type="'blockPuzzle'"
      :img-size="{width:'400px',height:'200px'}"
      @success="login"
    />
  </div>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { encrypt } from '@/utils/crypto'
import { getUUID } from '@/utils'
import Verify from '@/components/verifition/Verify.vue'
import cookie from 'vue-cookies'

const dataForm = ref({
  userName: '',
  password: '',
  uuid: '',
  captcha: ''
})
const dataRule = {
  userName: [
    { required: true, message: '帐号不能为空', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '验证码不能为空', trigger: 'blur' }
  ]
}

onBeforeUnmount(() => {
  document.removeEventListener('keyup', handerKeyup)
})
onMounted(() => {
  getCaptcha()
  document.addEventListener('keyup', handerKeyup)
})
const handerKeyup = (e) => {
  const keycode = document.all ? event.keyCode : e.which
  if (keycode === 13) {
    this.dataFormSubmit()
  }
}

const verifyRef = ref(null)
const dataFormRef = ref(null)
const isSubmitting = ref(false)
let isSubmit = false
/**
 * 提交表单
 */
const dataFormSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      // TODO: 调试时临时跳过验证码，上线前需要恢复
      // verifyRef.value?.show()
      login({ captchaVerification: 'skip' })
    }
  })
}

const router = useRouter()
const login = (verifyResult) => {
  if (isSubmit) {
    return
  }
  isSubmit = true
  isSubmitting.value = true
  http({
    url: http.adornUrl('/adminLogin'),
    method: 'post',
    data: http.adornData({
      userName: dataForm.value.userName,
      passWord: encrypt(dataForm.value.password),
      captchaVerification: verifyResult.captchaVerification
    })
  }).then(({ data }) => {
    // Remove any existing Authorization cookies first
    cookie.remove('Authorization')
    cookie.remove('Authorization', '/')
    cookie.remove('Authorization', '/', window.location.hostname)
    // Extract root domain for cross-subdomain cookie (e.g., lumistarlearning.com)
    const hostname = window.location.hostname
    const rootDomain = hostname.includes('.') ? hostname.split('.').slice(-2).join('.') : hostname
    cookie.remove('Authorization', '/', rootDomain)
    // Set new cookie with explicit domain
    cookie.set('Authorization', data.accessToken, '30d', '/', window.location.hostname)
    router.replace({ name: 'home' })
  }).catch(() => {
    isSubmit = false
    isSubmitting.value = false
  })
}

/**
 * 获取验证码
 */
const getCaptcha = () => {
  dataForm.value.uuid = getUUID()
}

</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.login-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

// Left Brand Section
.login-brand {
  width: 45%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, $--color-primary 0%, $--color-primary-dark 100%);
  overflow: hidden;

  // Decorative Circles
  .decorative-circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
  }

  .circle-1 {
    width: 300px;
    height: 300px;
    top: -100px;
    left: -100px;
  }

  .circle-2 {
    width: 200px;
    height: 200px;
    bottom: -50px;
    right: -50px;
  }

  .circle-3 {
    width: 150px;
    height: 150px;
    top: 50%;
    right: 10%;
    background: rgba(255, 255, 255, 0.05);
  }
}

.brand-content {
  text-align: center;
  color: #fff;
  z-index: 1;
  padding: $spacing-xl;
}

.brand-logo {
  width: 80px;
  height: 80px;
  margin: 0 auto $spacing-lg;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 40px;
    height: 40px;
    color: #fff;
  }
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 $spacing-sm;
  color: #fff;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 $spacing-xl * 2;
}

.brand-features {
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: left;

  li {
    display: flex;
    align-items: center;
    margin-bottom: $spacing-md;
    font-size: $font-size-md;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .feature-icon {
    width: 24px;
    height: 24px;
    margin-right: $spacing-md;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    svg {
      width: 14px;
      height: 14px;
      color: #fff;
    }
  }
}

// Right Form Section
.login-form-section {
  width: 55%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $--color-background;
  padding: $spacing-xl;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: $border-radius-xl;
  box-shadow: $shadow-xl;
  padding: 48px 40px;
}

.card-header {
  text-align: center;
  margin-bottom: $spacing-xl * 1.5;
}

.card-title {
  font-size: $font-size-xl;
  font-weight: 600;
  color: $--color-text-primary;
  margin: 0 0 $spacing-sm;
}

.card-subtitle {
  font-size: $font-size-sm;
  color: $--color-text-secondary;
  margin: 0;
}

// Form Styles
:deep(.el-form-item) {
  margin-bottom: $spacing-lg;
}

:deep(.el-input) {
  --el-input-border-radius: #{$border-radius-md};

  .el-input__wrapper {
    padding: 0 15px;
    box-shadow: 0 0 0 1px $--color-border inset;
    transition: box-shadow $transition-fast;

    &:hover {
      box-shadow: 0 0 0 1px $--color-primary inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px rgba($--color-primary, 0.2), 0 0 0 1px $--color-primary inset;
    }
  }

  .el-input__prefix {
    color: $--color-text-placeholder;
  }
}

:deep(.el-input__inner) {
  height: 48px;
  line-height: 48px;

  &::placeholder {
    color: $--color-text-placeholder;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: $font-size-md;
  font-weight: 500;
  border-radius: $border-radius-md;
  background: linear-gradient(135deg, $--color-primary 0%, $--color-primary-dark 100%);
  border: none;
  transition: all $transition-fast;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba($--color-primary, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

.card-footer {
  text-align: center;
  margin-top: $spacing-xl;
  padding-top: $spacing-lg;
  border-top: 1px solid $--color-border;
  color: $--color-text-placeholder;
  font-size: $font-size-xs;
}

// Responsive Design
@media screen and (max-width: 992px) {
  .login-brand {
    display: none;
  }

  .login-form-section {
    width: 100%;
  }
}

@media screen and (max-width: 480px) {
  .login-form-section {
    padding: $spacing-md;
  }

  .login-card {
    padding: 32px 24px;
  }
}
</style>
