import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')

  async function login(data: { username: string; password: string }) {
    const res: any = await loginApi(data)
    token.value = res.data.token
    userId.value = res.data.userId
    username.value = res.data.username
    nickname.value = res.data.nickname
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', String(res.data.userId))
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('nickname', res.data.nickname)
  }

  async function register(data: { username: string; password: string; nickname?: string }) {
    const res: any = await registerApi(data)
    token.value = res.data.token
    userId.value = res.data.userId
    username.value = res.data.username
    nickname.value = res.data.nickname
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', String(res.data.userId))
    localStorage.setItem('username', res.data.username)
    localStorage.setItem('nickname', res.data.nickname)
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    nickname.value = ''
    localStorage.clear()
  }

  return { token, userId, username, nickname, login, register, logout }
})
