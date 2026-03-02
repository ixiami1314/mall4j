import http from '@/utils/http'

// 商品详情
export function getProdInfo(prodId) {
  return http({
    url: http.adornUrl('/prod/prodInfo'),
    method: 'get',
    params: { prodId }
  })
}

// 分类商品列表
export function getProdPage(categoryId, page = {}) {
  return http({
    url: http.adornUrl('/prod/pageProd'),
    method: 'get',
    params: http.adornParams({ categoryId, ...page })
  })
}

// 标签商品列表
export function getProdListByTagId(tagId, page = {}) {
  return http({
    url: http.adornUrl('/prod/prodListByTagId'),
    method: 'get',
    params: http.adornParams({ tagId, ...page })
  })
}

// 新品推荐
export function getLastedProdPage(page = {}) {
  return http({
    url: http.adornUrl('/prod/lastedProdPage'),
    method: 'get',
    params: http.adornParams(page)
  })
}

// 每日疯抢
export function getMoreBuyProdList(page = {}) {
  return http({
    url: http.adornUrl('/prod/moreBuyProdList'),
    method: 'get',
    params: http.adornParams(page)
  })
}

// 分类信息
export function getCategoryInfo() {
  return http({
    url: http.adornUrl('/category/categoryInfo'),
    method: 'get'
  })
}

// 商品评论
export function getProdCommPage(prodId, page = {}) {
  return http({
    url: http.adornUrl('/prodComm/prodCommPage'),
    method: 'get',
    params: http.adornParams({ prodId, ...page })
  })
}
