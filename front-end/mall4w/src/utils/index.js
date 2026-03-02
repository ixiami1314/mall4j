import cookie from 'vue-cookies'

export const clearLoginInfo = () => {
  cookie.remove('Authorization')
  sessionStorage.clear()
}

export const isLogin = () => {
  return !!cookie.get('Authorization')
}
