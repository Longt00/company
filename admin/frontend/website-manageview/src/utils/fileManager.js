/**
 * 文件管理器 - 按照修复指南实现的缓存管理机制
 * 用于管理文件列表缓存，优化删除后的界面刷新
 */
class FileManager {
  constructor() {
    this.cache = new Map()
    this.lastUpdateTime = new Map()
    this.cacheExpiry = 5 * 60 * 1000 // 5分钟缓存
  }

  /**
   * 获取文件列表（带缓存）
   * @param {string} category - 文件分类
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise<Array>} 文件列表
   */
  async getFiles(category, forceRefresh = false) {
    const now = Date.now()
    const cacheKey = `files_${category}`

    // 检查缓存
    if (!forceRefresh &&
        this.cache.has(cacheKey) &&
        (now - this.lastUpdateTime.get(cacheKey)) < this.cacheExpiry) {
      console.log(`从缓存获取 ${category} 文件列表`)
      return this.cache.get(cacheKey)
    }

    try {
      // 动态导入避免循环依赖
      const { uploadAPI } = await import('../api/upload.js')

      // 获取最新数据
      const response = await uploadAPI.getFilesByCategory(category)

      if (response && response.success && response.data) {
        const files = Array.isArray(response.data) ? response.data : response.data.files || []

        // 更新缓存
        this.cache.set(cacheKey, files)
        this.lastUpdateTime.set(cacheKey, now)

        console.log(`获取 ${category} 文件列表成功，共 ${files.length} 个文件`)
        return files
      }
    } catch (error) {
      console.error(`获取 ${category} 文件列表失败:`, error)
    }

    return []
  }

  /**
   * 强制刷新缓存
   * @param {string} category - 文件分类
   * @returns {Promise<Array>} 最新的文件列表
   */
  async forceRefresh(category) {
    console.log(`强制刷新 ${category} 文件列表`)
    return await this.getFiles(category, true)
  }

  /**
   * 刷新文件列表（使用专门的刷新接口）
   * @param {string} category - 文件分类
   * @returns {Promise<Array>} 最新的文件列表
   */
  async refreshFileList(category = null) {
    try {
      // 动态导入避免循环依赖
      const { uploadAPI } = await import('../api/upload.js')

      const response = await uploadAPI.refreshFileList(category)

      if (response && response.success && response.data) {
        const files = response.data.files || []
        const now = Date.now()

        if (category) {
          // 更新特定分类的缓存
          const cacheKey = `files_${category}`
          this.cache.set(cacheKey, files)
          this.lastUpdateTime.set(cacheKey, now)
          console.log(`刷新 ${category} 文件列表成功，共 ${files.length} 个文件`)
        } else {
          // 如果没有指定分类，清除所有缓存
          this.clearAllCache()
          console.log('刷新所有文件列表成功，已清除所有缓存')
        }

        return files
      }
    } catch (error) {
      console.error('刷新文件列表失败:', error)
    }

    return []
  }

  /**
   * 清除缓存
   * @param {string} category - 文件分类（可选）
   */
  clearCache(category) {
    if (category) {
      const cacheKey = `files_${category}`
      this.cache.delete(cacheKey)
      this.lastUpdateTime.delete(cacheKey)
      console.log(`清除 ${category} 文件列表缓存`)
    } else {
      this.clearAllCache()
    }
  }

  /**
   * 清除所有缓存
   */
  clearAllCache() {
    this.cache.clear()
    this.lastUpdateTime.clear()
    console.log('🗑️ 已清除所有文件列表缓存')
  }

  /**
   * 检查缓存是否有效
   * @param {string} category - 文件分类
   * @returns {boolean} 缓存是否有效
   */
  isCacheValid(category) {
    const cacheKey = `files_${category}`
    const now = Date.now()

    return this.cache.has(cacheKey) &&
           (now - this.lastUpdateTime.get(cacheKey)) < this.cacheExpiry
  }

  /**
   * 获取缓存状态信息
   * @returns {Object} 缓存状态
   */
  getCacheStatus() {
    const status = {}
    const now = Date.now()

    for (const [key, timestamp] of this.lastUpdateTime.entries()) {
      const category = key.replace('files_', '')
      const isValid = (now - timestamp) < this.cacheExpiry
      const age = Math.floor((now - timestamp) / 1000)

      status[category] = {
        isValid,
        age,
        ageText: this.formatAge(age),
        fileCount: this.cache.get(key)?.length || 0
      }
    }

    return status
  }

  /**
   * 格式化缓存年龄
   * @param {number} age - 年龄（秒）
   * @returns {string} 格式化的年龄文本
   */
  formatAge(age) {
    if (age < 60) return `${age}秒前`
    if (age < 3600) return `${Math.floor(age / 60)}分钟前`
    if (age < 86400) return `${Math.floor(age / 3600)}小时前`
    return `${Math.floor(age / 86400)}天前`
  }

  /**
   * 设置缓存过期时间
   * @param {number} expiry - 过期时间（毫秒）
   */
  setCacheExpiry(expiry) {
    this.cacheExpiry = expiry
    console.log(`设置缓存过期时间为 ${expiry}ms`)
  }
}

// 创建单例实例
const fileManager = new FileManager()

export default fileManager
export { FileManager }