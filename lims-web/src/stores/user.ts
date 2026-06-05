import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo, LoginDTO } from '@/types'
import { loginApi, logoutApi } from '@/api/auth'
import { setToken, removeToken, setUser, removeUser, getToken, getUser } from '@/utils/storage'
import { clearStorage } from '@/utils/storage'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getToken() || '')
  const userInfo = ref<UserInfo | null>(getUser())

  async function login(loginData: LoginDTO) {
    const res = await loginApi(loginData)
    const { token: tokenValue, userInfo: user } = res.data
    token.value = tokenValue
    userInfo.value = user
    setToken(tokenValue)
    setUser(user)
    return res
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = null
      clearStorage()
    }
  }

  function resetUser() {
    token.value = ''
    userInfo.value = null
    removeToken()
    removeUser()
  }

  return {
    token,
    userInfo,
    login,
    logout,
    resetUser
  }
})
