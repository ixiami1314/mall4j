import http from '@/utils/http'

// 登录
export function login(data) {
  return http({
    url: http.adornUrl('/login'),
    method: 'post',
    data
  })
}

// 注册
export function register(data) {
  return http({
    url: http.adornUrl('/user/register'),
    method: 'post',
    data
  })
}

// 用户信息
export function getUserInfo() {
  return http({
    url: http.adornUrl('/p/user/userInfo'),
    method: 'get'
  })
}

// 修改用户信息
export function setUserInfo(data) {
  return http({
    url: http.adornUrl('/p/user/setUserInfo'),
    method: 'put',
    data
  })
}

// 发送验证码
export function sendSms(mobile) {
  return http({
    url: http.adornUrl('/p/sms/send'),
    method: 'post',
    data: { mobile }
  })
}
