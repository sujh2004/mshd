import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    isLoggedIn: !!localStorage.getItem('token')
  }),

  getters: {
    // 获取用户名
    username: (state) => state.userInfo.username || '',

    // 获取真实姓名
    realName: (state) => state.userInfo.realName || '',

    // 获取角色
    role: (state) => state.userInfo.role || '',

    // 获取部门
    department: (state) => state.userInfo.department || '',

    // 是否是管理员
    isAdmin: (state) => state.userInfo.role === 'admin'
  },

  actions: {
    // 登录
    async login(loginData) {
      try {
        const res = await login(loginData)
        if (res.code === 200 && res.data) {
          this.token = res.data.token
          this.userInfo = res.data
          this.isLoggedIn = true

          // 保存到localStorage
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('userInfo', JSON.stringify(res.data))

          return { success: true, message: res.message }
        } else {
          return { success: false, message: res.message || '登录失败' }
        }
      } catch (error) {
        return { success: false, message: error.message || '登录失败' }
      }
    },

    // 登出
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('登出接口调用失败:', error)
      } finally {
        // 清除状态
        this.token = ''
        this.userInfo = {}
        this.isLoggedIn = false

        // 清除localStorage
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
    },

    // 获取用户信息
    async fetchUserInfo(userId) {
      try {
        const res = await getUserInfo(userId)
        if (res.code === 200 && res.data) {
          this.userInfo = { ...this.userInfo, ...res.data }
          localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
          return { success: true, data: res.data }
        }
        return { success: false, message: res.message }
      } catch (error) {
        return { success: false, message: error.message }
      }
    }
  }
})
