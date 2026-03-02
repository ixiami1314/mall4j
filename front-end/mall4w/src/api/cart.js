import http from '@/utils/http'

// 获取购物车信息
export function getCartInfo(data = {}) {
  return http({
    url: http.adornUrl('/p/shopCart/info'),
    method: 'post',
    data
  })
}

// 获取购物车数量
export function getCartCount() {
  return http({
    url: http.adornUrl('/p/shopCart/prodCount'),
    method: 'get'
  })
}

// 添加/修改购物车
export function changeCartItem(data) {
  return http({
    url: http.adornUrl('/p/shopCart/changeItem'),
    method: 'post',
    data
  })
}

// 删除购物车商品
export function deleteCartItem(basketIds) {
  return http({
    url: http.adornUrl('/p/shopCart/deleteItem'),
    method: 'delete',
    data: basketIds
  })
}

// 清空购物车
export function clearCart() {
  return http({
    url: http.adornUrl('/p/shopCart/deleteAll'),
    method: 'delete'
  })
}

// 获取选中商品总价
export function getTotalPay(basketIds) {
  return http({
    url: http.adornUrl('/p/shopCart/totalPay'),
    method: 'post',
    data: basketIds
  })
}
