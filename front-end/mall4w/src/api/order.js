import http from '@/utils/http'

// 订单确认
export function confirmOrder(data) {
  return http({
    url: http.adornUrl('/p/order/confirm'),
    method: 'post',
    data
  })
}

// 提交订单
export function submitOrder(data) {
  return http({
    url: http.adornUrl('/p/order/submit'),
    method: 'post',
    data
  })
}

// 订单列表
export function getOrderList(params) {
  return http({
    url: http.adornUrl('/p/myOrder/myOrder'),
    method: 'get',
    params: http.adornParams(params)
  })
}

// 订单详情
export function getOrderDetail(orderNumber) {
  return http({
    url: http.adornUrl('/p/myOrder/orderDetail'),
    method: 'get',
    params: { orderNumber }
  })
}

// 取消订单
export function cancelOrder(orderNumber) {
  return http({
    url: http.adornUrl('/p/myOrder/cancelOrder'),
    method: 'put',
    params: { orderNumber }
  })
}

// 确认收货
export function receiptOrder(orderNumber) {
  return http({
    url: http.adornUrl('/p/myOrder/receiptOrder'),
    method: 'put',
    params: { orderNumber }
  })
}

// 支付
export function pay(orderNumbers) {
  return http({
    url: http.adornUrl('/p/order/pay'),
    method: 'post',
    params: { orderNumbers }
  })
}
