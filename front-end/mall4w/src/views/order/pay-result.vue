<template>
  <div class="pay-result container">
    <div class="result-content">
      <el-result
        icon="success"
        title="订单提交成功"
        sub-title="请尽快完成支付"
      >
        <template #extra>
          <el-button type="primary" @click="handlePay">立即支付</el-button>
          <el-button @click="$router.push('/order/list')">查看订单</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { pay } from '@/api/order'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const handlePay = async () => {
  const orderNumbers = route.query.orderNumbers
  if (!orderNumbers) {
    ElMessage.warning('订单号不存在')
    return
  }
  await pay(orderNumbers)
  ElMessage.success('支付成功')
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
