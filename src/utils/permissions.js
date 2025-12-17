/**
 * 权限管理工具
 * 用于基于角色的访问控制
 */

export const permissions = {
  /**
   * 管理员权限检测
   */
  isAdmin() {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}')
    return user.role === 'admin' || user.permissions?.includes('product_management')
  },

  /**
   * 图片管理权限
   */
  canManageImages() {
    return this.isAdmin() || this.hasPermission('image_management')
  },

  /**
   * 产品管理权限
   */
  canManageProducts() {
    return this.isAdmin() || this.hasPermission('product_management')
  },

  /**
   * 视频管理权限
   */
  canManageVideos() {
    return this.isAdmin() || this.hasPermission('video_management')
  },

  /**
   * 公司信息管理权限
   */
  canManageCompany() {
    return this.isAdmin() || this.hasPermission('company_management')
  },

  /**
   * 检查特定权限
   */
  hasPermission(permission) {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}')
    return user.permissions?.includes(permission) || false
  },

  /**
   * 设置管理员模式（开发环境测试用）
   */
  setAdminMode(enabled = true) {
    if (process.env.NODE_ENV === 'development') {
      const testUser = {
        id: 'test-admin',
        role: 'admin',
        name: 'Test Admin',
        permissions: ['product_management', 'image_management', 'video_management', 'company_management']
      }
      localStorage.setItem('currentUser', JSON.stringify(testUser))
      return enabled
    }
    return false
  },

  /**
   * 清除权限信息
   */
  clearPermissions() {
    localStorage.removeItem('currentUser')
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser() {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}')
    return user
  },

  /**
   * 检查是否已登录
   */
  isLoggedIn() {
    const token = localStorage.getItem('authToken') || sessionStorage.getItem('authToken')
    return !!token
  },

  /**
   * 检查Token是否过期
   */
  isTokenExpired() {
    const token = localStorage.getItem('authToken') || sessionStorage.getItem('authToken')
    if (!token) return true

    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.exp * 1000 < Date.now()
    } catch (error) {
      console.error('Token解析失败:', error)
      return true
    }
  }
}

export default {
  permissions,
  isAdmin: permissions.isAdmin,
  canManageImages: permissions.canManageImages,
  canManageProducts: permissions.canManageProducts,
  canManageVideos: permissions.canManageVideos,
  canManageCompany: permissions.canManageCompany,
  hasPermission: permissions.hasPermission,
  setAdminMode: permissions.setAdminMode,
  clearPermissions: permissions.clearPermissions,
  getCurrentUser: permissions.getCurrentUser,
  isLoggedIn: permissions.isLoggedIn,
  isTokenExpired: permissions.isTokenExpired
}