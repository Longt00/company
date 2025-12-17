/**
 * 端口修正工具
 * 用于临时解决8081端口问题，确保所有API调用都使用8080端口
 */

/**
 * 修正URL中的端口
 * @param {string} url - 原始URL
 * @returns {string} 修正后的URL
 */
export const fixPort = (url) => {
  if (!url || typeof url !== 'string') {
    return url
  }

  // 将所有8081端口替换为8080
  const fixedUrl = url.replace(/:8081/g, ':8080')

  if (url !== fixedUrl) {
    console.warn('🔧 端口修正: 8081 -> 8080', { original: url, fixed: fixedUrl })
  }

  return fixedUrl
}

/**
 * 修正文件URL中的端口
 * @param {string} fileUrl - 文件URL
 * @returns {string} 修正后的文件URL
 */
export const fixFilePort = (fileUrl) => {
  return fixPort(fileUrl)
}

/**
 * 确保API基础URL使用正确的端口
 * @param {string} baseUrl - API基础URL
 * @returns {string} 修正后的API基础URL
 */
export const fixApiBaseUrl = (baseUrl) => {
  return fixPort(baseUrl)
}

// 在模块加载时应用全局修正
if (typeof window !== 'undefined') {
  // 重写fetch方法以自动修正端口
  const originalFetch = window.fetch
  window.fetch = function(url, options) {
    const fixedUrl = fixPort(url)
    return originalFetch.call(this, fixedUrl, options)
  }

  console.log('✅ 全局端口修正已启用，所有fetch请求将自动修正端口')
}

export default {
  fixPort,
  fixFilePort,
  fixApiBaseUrl
}