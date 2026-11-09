import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { LoginPayload, LoginResult, UserInfo } from '@/api/auth'
import * as authApi from '@/api/auth'
import { REFRESH_TOKEN_KEY, TOKEN_KEY } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const accessToken = ref(localStorage.getItem(TOKEN_KEY) ?? '')
  const refreshToken = ref(localStorage.getItem(REFRESH_TOKEN_KEY) ?? '')
  const user = ref<UserInfo | null>(null)

  function applySession(result: LoginResult) {
    accessToken.value = result.accessToken
    refreshToken.value = result.refreshToken
    user.value = result.user
    localStorage.setItem(TOKEN_KEY, result.accessToken)
    localStorage.setItem(REFRESH_TOKEN_KEY, result.refreshToken)
  }

  async function login(payload: LoginPayload) {
    applySession(await authApi.login(payload))
  }

  async function register(payload: LoginPayload) {
    applySession(await authApi.register(payload))
  }

  async function loadProfile() {
    user.value = await authApi.fetchMe()
  }

  function clear() {
    accessToken.value = ''
    refreshToken.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
  }

  return { accessToken, refreshToken, user, login, register, loadProfile, clear }
})
