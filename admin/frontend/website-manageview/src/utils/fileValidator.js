/**
 * 文件验证工具
 * 用于验证文件是否真实存在，避免显示不存在的文件链接
 */

/**
 * 验证文件是否存在
 * @param {string} fileUrl - 文件URL
 * @returns {Promise<boolean>} 文件是否存在
 */
export const verifyFileExists = async (fileUrl) => {
  if (!fileUrl || typeof fileUrl !== 'string') {
    console.warn('无效的文件URL:', fileUrl)
    return false
  }

  // 多层端口修正
  let correctedUrl = fileUrl
    .replace(/:8081/g, ':8080')  // 修正所有8081端口
    .replace(/https?:\/\/localhost:8080/g, 'http://localhost:8080') // 确保使用http协议

  if (fileUrl !== correctedUrl) {
    console.warn('🔧 文件URL修正:', { original: fileUrl, corrected: correctedUrl })
  }

  try {
    const response = await fetch(correctedUrl, {
      method: 'HEAD',
      cache: 'no-cache', // 避免缓存影响验证结果
      mode: 'cors', // 明确指定CORS模式
      credentials: 'omit' // 不发送凭证，避免认证问题
    })

    if (response.ok) {
      return true
    } else {
      console.warn('文件访问失败:', correctedUrl, `HTTP ${response.status} ${response.statusText}`)
      return false
    }
  } catch (error) {
    console.warn('文件验证失败:', correctedUrl, error.message)

    // 如果是网络错误，尝试使用GET方法获取更多信息
    if (error.message.includes('Failed to fetch') || error.message.includes('ERR_FAILED')) {
      try {
        const getResponse = await fetch(correctedUrl, {
          method: 'GET',
          cache: 'no-cache',
          mode: 'cors',
          credentials: 'omit'
        })
        return getResponse.ok
      } catch (getError) {
        console.warn('GET方法也失败:', correctedUrl, getError.message)
        return false
      }
    }

    return false
  }
}

/**
 * 过滤存在的文件列表
 * @param {Array} files - 文件对象数组
 * @param {Function} getUrlFn - 获取文件URL的函数
 * @returns {Promise<Array>} 过滤后的存在的文件列表
 */
export const filterExistingFiles = async (files, getUrlFn = (file) => file.fileUrl) => {
  if (!Array.isArray(files) || files.length === 0) {
    return []
  }

  const validFiles = []

  // 并行验证所有文件
  const verificationPromises = files.map(async (file) => {
    const fileUrl = getUrlFn(file)
    const exists = await verifyFileExists(fileUrl)
    return { file, exists }
  })

  const results = await Promise.allSettled(verificationPromises)

  results.forEach((result) => {
    if (result.status === 'fulfilled' && result.value.exists) {
      validFiles.push(result.value.file)
    } else if (result.status === 'fulfilled' && !result.value.exists) {
      console.warn('文件不存在，已过滤:', getUrlFn(result.value.file))
    } else if (result.status === 'rejected') {
      console.error('文件验证过程中出错:', result.reason)
    }
  })

  return validFiles
}

/**
 * 验证单个文件并返回验证结果
 * @param {Object} file - 文件对象
 * @param {Function} getUrlFn - 获取文件URL的函数
 * @returns {Promise<{file: Object, exists: boolean}>} 验证结果
 */
export const validateFile = async (file, getUrlFn = (file) => file.fileUrl) => {
  const fileUrl = getUrlFn(file)
  const exists = await verifyFileExists(fileUrl)
  return { file, exists }
}

export default {
  verifyFileExists,
  filterExistingFiles,
  validateFile
}