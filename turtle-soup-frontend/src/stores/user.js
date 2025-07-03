import { defineStore } from 'pinia'
import { login, register, getUserInfo, logout } from '@/api/user'
import Cookies from 'js-cookie'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: Cookies.get('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAuthenticated: (state) => !!state.token
  },

  actions: {
    // 用户登录
    async login(loginForm) {
      try {
        const response = await login(loginForm)
        // 兼容后端直接返回id/username/avatar/token结构
        const { token, id, username, avatar } = response.data
        this.token = token
        this.userInfo = {
          id,
          username,
          avatar: avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf`
        }
        Cookies.set('token', token, { expires: 7 })
        return response
      } catch (error) {
        throw error
      }
    },

    // 用户注册
    async register(registerForm) {
      try {
        const response = await register(registerForm)
        return response
      } catch (error) {
        throw error
      }
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        const response = await getUserInfo()
        // 兼容后端返回的 avatar 字段
        const { id, username, avatar, avatarUrl, email } = response.data
        this.userInfo = {
          id,
          username,
          avatar: avatar || avatarUrl || `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf`,
          email: email || null
        }
        return response
      } catch (error) {
        throw error
      }
    },

    // 用户登出
    logout() {
      this.resetUserState()
    },

    // 重置用户状态
    resetUserState() {
      this.token = ''
      this.userInfo = null
      Cookies.remove('token')
    },

    // 初始化用户状态
    async initUserState() {
      const token = Cookies.get('token')
      if (token) {
        this.token = token
        try {
          await this.getUserInfo()
        } catch (error) {
          this.resetUserState()
        }
      } else {
        this.resetUserState()
      }
    }
  }
}) 