import { defineStore } from 'pinia'
import { login, register, getUserInfo, logout } from '@/api/user'
import Cookies from 'js-cookie'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: Cookies.get('token') || '',
    userInfo: null,
    isGuest: false
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAuthenticated: (state) => !!state.token || state.isGuest
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
        this.isGuest = false
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
        // 如果没有头像，设置默认头像
        this.userInfo = {
          ...response.data,
          avatar: response.data.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${response.data.username}&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf`
        }
        return response
      } catch (error) {
        throw error
      }
    },

    // 用户登出
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        this.resetUserState()
      }
    },

    // 游客登录
    guestLogin() {
      this.isGuest = true
      this.token = ''
      this.userInfo = {
        id: 'guest',
        username: '游客',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=guest&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf',
        email: null
      }
    },

    // 重置用户状态
    resetUserState() {
      this.token = ''
      this.userInfo = null
      this.isGuest = false
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
          // 如果获取用户信息失败，设置默认头像
          this.userInfo = {
            id: 'default',
            username: '用户',
            avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=user&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf',
            email: null
          }
        }
      }
    }
  }
}) 