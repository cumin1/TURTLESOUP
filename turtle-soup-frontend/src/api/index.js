import axios from 'axios'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080',
  timeout: 10000,
  withCredentials: true
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从cookie获取token
    const token = Cookies.get('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是1，说明接口有问题
    if (res.code !== 1) {
      ElMessage({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        // 清除token并跳转登录页
        Cookies.remove('token')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    let message = '网络错误'
    if (error.response) {
      // 服务器返回了错误状态码
      const { status, data } = error.response
      if (status === 404) {
        message = '接口不存在'
      } else if (status === 500) {
        message = '服务器内部错误'
      } else if (data && data.msg) {
        message = data.msg
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      message = '无法连接到服务器'
    }
    
    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service 