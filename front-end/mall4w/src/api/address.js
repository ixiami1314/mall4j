import http from '@/utils/http'

// 地址列表
export function getAddressList() {
  return http({
    url: http.adornUrl('/p/address/list'),
    method: 'get'
  })
}

// 地址详情
export function getAddressDetail(addrId) {
  return http({
    url: http.adornUrl('/p/address/addrInfo'),
    method: 'get',
    params: { addrId }
  })
}

// 添加地址
export function addAddress(data) {
  return http({
    url: http.adornUrl('/p/address/addAddr'),
    method: 'post',
    data
  })
}

// 修改地址
export function updateAddress(data) {
  return http({
    url: http.adornUrl('/p/address/updateAddr'),
    method: 'put',
    data
  })
}

// 删除地址
export function deleteAddress(addrId) {
  return http({
    url: http.adornUrl('/p/address/deleteAddr'),
    method: 'delete',
    params: { addrId }
  })
}

// 设为默认
export function setDefaultAddress(addrId) {
  return http({
    url: http.adornUrl('/p/address/defaultAddr'),
    method: 'put',
    params: { addrId }
  })
}
