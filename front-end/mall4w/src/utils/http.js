import axios from 'axios'
import qs from 'qs'
import cookie from 'vue-cookies'
import { ElMessage } from 'element-plus'
import router from '@/router'

const http = axios.create({
  timeout: 1000 * 30,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json; charset=utf-8'
  }
})

// 请求拦截
http.interceptors.request.use(
  config => {
    config.headers.Authorization = cookie.get('Authorization')
    if (config.method === 'get' || config.method === 'GET') {
      config.paramsSerializer = function (params) {
        return qs.stringify(params, { arrayFormat: 'repeat' })
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截
http.interceptors.response.use(
  response => {
    if (response.request.responseType === 'blob') {
      return response
    }
    const res = response.data
    if (res.code === '00000' || res.code === 'A00002') {
      return res
    }
    if (res.code === 'A00001') {
      ElMessage({
        message: res.msg || res.data || 'Error',
        type: 'error',
        duration: 1500
      })
      return Promise.reject(res)
    }
    if (res.code === 'A00004') {
      cookie.remove('Authorization')
      router.push('/login')
    }
    if (res.code === 'A00005') {
      ElMessage({
        message: '服务器出了点小差，请稍后再试',
        type: 'error',
        duration: 1500
      })
      return Promise.reject(res)
    }
    return res
  },
  error => {
    switch (error.response?.status) {
      case 400:
        ElMessage({ message: error.response.data, type: 'error', duration: 1500 })
        break
      case 401:
        cookie.remove('Authorization')
        router.push('/login')
        break
      case 500:
        ElMessage({ message: '服务器出了点小差，请稍后再试', type: 'error', duration: 1500 })
        break
    }
    return Promise.reject(error)
  }
)

// API 地址处理
http.adornUrl = actionName => {
  return import.meta.env.VITE_APP_BASE_API + actionName
}

// get 请求参数处理
http.adornParams = (params = {}, openDefaultParams = true) => {
  const defaults = { t: Date.now() }
  return openDefaultParams ? { ...defaults, ...params } : params
}

export default http
