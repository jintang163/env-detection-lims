import { post, get } from '@/utils/request'
import type { LoginDTO, LoginVO, Result } from '@/types'

export function login(data: LoginDTO) {
  return post<LoginVO>('/auth/login', data)
}

export function logout() {
  return post<void>('/auth/logout')
}

export function getUserInfo() {
  return get<Result<any>>('/auth/userInfo')
}

export function getCaptcha() {
  return get<Result<any>>('/auth/captcha')
}
