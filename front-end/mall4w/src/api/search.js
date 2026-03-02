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
export function getHotSearch(number = 10, sort = 0) {
  return http({
    url: http.adornUrl('/search/hotSearch'),
    method: 'get',
    params: http.adornParams({ number, sort })
  })
}
