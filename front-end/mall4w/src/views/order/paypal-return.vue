<template>
  <div class="paypal-return container">
    <div class="result-content" v-loading="loading">
      <el-result
        v-if="paymentStatus === 'success'"
        icon="success"
        :title="t('order.payResult.paypalReturnSuccess')"
      >
        <template #extra>
          <el-button type="primary" @click="$router.push('/order/list')">
            {{ t('order.payResult.viewOrder') }}
          </el-button>
          <el-button @click="$router.push('/')">
            {{ t('order.payResult.continueShopping') }}
          </el-button>
        </template>
      </el-result>

      <el-result
        v-else-if="paymentStatus === 'failed'"
        icon="error"
        :title="t('order.payResult.paypalReturnFailed')"
      >
        <template #extra>
          <el-button type="primary" @click="$router.push('/order/list')">
            {{ t('order.payResult.viewOrder') }}
          </el-button>
          <el-button @click="$router.push('/')">
            {{ t('order.payResult.continueShopping') }}
          </el-button>
        </template>
      </el-result>

      <el-result
        v-else
        icon="info"
        :title="t('order.payResult.paypalReturnProcessing')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { capturePayPalOrder } from '@/api/order'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const loading = ref(true)
const paymentStatus = ref('processing') // processing, success, failed

onMounted(async () => {
  const token = route.query.token || route.query.order_id

  if (!token) {
    paymentStatus.value = 'failed'
    loading.value = false
    return
  }

  try {
    // 调用后端捕获 PayPal 订单
    const { data } = await capturePayPalOrder(token)

    if (data && data.success) {
      paymentStatus.value = 'success'
    } else {
      paymentStatus.value = 'failed'
    }
  } catch (error) {
    console.error('PayPal capture failed:', error)
    paymentStatus.value = 'failed'
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.paypal-return {
  background: #fff;
  padding: 60px 20px;
  min-height: 400px;
}

.result-content {
  max-width: 500px;
  margin: 0 auto;
}
</style>
