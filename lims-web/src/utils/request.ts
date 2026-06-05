import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'
import { getToken, clearStorage } from './storage'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
  timeout: 15000
})

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    if (res.code && res.code !== 200) {
      message.error(res.message || '请求失败')
      if (res.code === 401 || res.code === 403) {
        clearStorage()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    console.error('Response error:', error)
    if (error.response) {
      const { status } = error.response
      if (status === 401 || status === 403) {
        message.error('登录已过期，请重新登录')
        clearStorage()
        router.push('/login')
      } else if (status === 404) {
        message.error('请求的资源不存在')
      } else if (status === 500) {
        message.error('服务器内部错误')
      } else {
        message.error(error.response.data?.message || error.message || '请求失败')
      }
    } else if (error.request) {
      message.error('网络错误，请检查网络连接')
    } else {
      message.error(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export interface Result<T = any> {
  code: number
  message: string
  data: T
}

export interface PageResult<T = any> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export function request<T = any>(config: AxiosRequestConfig): Promise<Result<T>> {
  return service(config)
}

export function get<T = any>(url: string, params?: any): Promise<Result<T>> {
  return service({ url, method: 'get', params })
}

export function post<T = any>(url: string, data?: any): Promise<Result<T>> {
  return service({ url, method: 'post', data })
}

export function put<T = any>(url: string, data?: any): Promise<Result<T>> {
  return service({ url, method: 'put', data })
}

export function del<T = any>(url: string, params?: any): Promise<Result<T>> {
  return service({ url, method: 'delete', params })
}

export default service
