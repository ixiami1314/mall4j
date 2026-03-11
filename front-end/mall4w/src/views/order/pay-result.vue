<template>
  <div class="pay-result container">
    <div class="result-content">
      <el-result
        icon="success"
        :title="t('order.payResult.submitSuccess')"
        :sub-title="t('order.payResult.payHint')"
      >
        <template #extra>
          <el-button type="primary" @click="handlePay">{{ t('order.payResult.payNow') }}</el-button>
          <el-button @click="$router.push('/order/list')">{{ t('order.payResult.viewOrder') }}</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { pay } from '@/api/order'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const handlePay = async () => {
  const orderNumbers = route.query.orderNumbers
  if (!orderNumbers) {
    ElMessage.warning(t('order.payResult.orderNotFound'))
    return
  }
  await pay(orderNumbers)
  ElMessage.success(t('order.payResult.success'))
  router.push('/order/list')
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
</style>
