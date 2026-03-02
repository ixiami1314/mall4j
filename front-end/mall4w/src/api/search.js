import http from '@/utils/http'

// 搜索商品
export function searchProd(params) {
  return http({
    url: http.adornUrl('/search/searchProdPage'),
    method: 'get',
    params: http.adornParams(params)
  })
}

// 热门搜索
export function getHotSearch() {
  return http({
    url: http.adornUrl('/search/hotSearch'),
    method: 'get'
  })
}
