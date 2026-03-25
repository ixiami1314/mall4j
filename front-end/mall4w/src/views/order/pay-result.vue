<template>
  <div class="pay-result container">
    <div class="result-content">
      <el-result
        icon="success"
        :title="t('order.payResult.submitSuccess')"
        :sub-title="t('order.payResult.payHint')"
      >
        <template #extra>
          <div class="pay-buttons">
            <el-button
              type="primary"
              size="large"
              class="paypal-btn"
              :loading="paypalLoading"
              @click="handlePayPalPay"
            >
              <svg class="paypal-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M7.076 21.337H2.47a.641.641 0 0 1-.633-.74L4.944.901C5.026.382 5.474 0 5.998 0h7.46c2.57 0 4.578.543 5.69 1.81 1.01 1.15 1.304 2.42 1.012 4.287-.023.143-.047.288-.077.437-.983 5.05-4.349 6.797-8.647 6.797h-2.19c-.524 0-.968.382-1.05.9l-1.12 7.106zm14.146-14.42a3.35 3.35 0 0 0-.607-.541c-.013.076-.026.175-.041.254-.93 4.778-4.005 7.201-9.138 7.201h-2.19a.563.563 0 0 0-.556.479l-1.187 7.527h-.506l-.24 1.516a.56.56 0 0 0 .554.647h3.882c.46 0 .85-.334.922-.788.06-.26.76-4.852.816-5.09a.932.932 0 0 1 .923-.788h.58c3.76 0 6.705-1.528 7.565-5.946.36-1.847.174-3.388-.777-4.471z"/>
              </svg>
              {{ t('order.payResult.payWithPayPal') }}
            </el-button>
            <el-button size="large" @click="$router.push('/order/list')">
              {{ t('order.payResult.viewOrder') }}
            </el-button>
          </div>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { createPayPalOrder } from '@/api/order'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const paypalLoading = ref(false)

const handlePayPalPay = async () => {
  const orderNumbers = route.query.orderNumbers
  if (!orderNumbers) {
    ElMessage.warning(t('order.payResult.orderNotFound'))
    return
  }

  paypalLoading.value = true

  try {
    // 调用后端创建 PayPal 订单
    const { data } = await createPayPalOrder(orderNumbers)

    if (data && data.approveUrl) {
      // 跳转到 PayPal 支付页面
      ElMessage.info(t('order.payResult.paypalProcessing'))
      window.location.href = data.approveUrl
    } else {
      ElMessage.error(t('order.payResult.paypalFailed'))
    }
  } catch (error) {
    console.error('PayPal order creation failed:', error)
    ElMessage.error(t('order.payResult.paypalFailed'))
  } finally {
    paypalLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.pay-result {
  background: #fff;
  padding: 60px 20px;
}

.result-content {
  max-width: 500px;
  margin: 0 auto;
}

.pay-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;

  .el-button {
    width: 200px;
  }
}

.paypal-btn {
  background: #0070ba !important;
  border-color: #0070ba !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &:hover {
    background: #003087 !important;
    border-color: #003087 !important;
  }

  .paypal-icon {
    flex-shrink: 0;
  }
}
</style>
