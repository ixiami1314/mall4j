import http from '@/utils/http'

// 首页轮播图
export function getIndexImgs() {
  return http({
    url: http.adornUrl('/indexImgs'),
    method: 'get'
  })
}

// 首页标签商品
export function getTagProdList() {
  return http({
    url: http.adornUrl('/prod/tagProdList'),
    method: 'get'
  })
}

// 新闻列表
export function getNoticeList(page) {
  return http({
    url: http.adornUrl('/shop/notice/noticeList'),
    method: 'get',
    params: http.adornParams(page)
  })
}
