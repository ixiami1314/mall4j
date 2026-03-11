import http from '@/utils/http'

// 收藏/取消收藏商品
export function addOrCancelCollection(prodId) {
  return http({
    url: http.adornUrl('/p/user/collection/addOrCancel'),
    method: 'post',
    data: prodId
  })
}

// 检查是否收藏
export function isCollection(prodId) {
  return http({
    url: http.adornUrl('/p/user/collection/isCollection'),
    method: 'get',
    params: { prodId }
  })
}

// 获取收藏数量
export function getCollectionCount() {
  return http({
    url: http.adornUrl('/p/user/collection/count'),
    method: 'get'
  })
}

// 获取收藏列表
export function getCollectionList(params) {
  return http({
    url: http.adornUrl('/p/user/collection/prods'),
    method: 'get',
    params: http.adornParams(params)
  })
}

// 获取收藏分页数据
export function getCollectionPage(params) {
  return http({
    url: http.adornUrl('/p/user/collection/page'),
    method: 'get',
    params: http.adornParams(params)
  })
}
