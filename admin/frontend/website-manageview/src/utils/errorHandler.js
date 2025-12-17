/**
 * 错误处理工具 - 按照修复指南实现的错误处理机制
 * 用于处理文件操作相关的错误，提供统一的错误处理和用户反馈
 */

/**
 * 处理API错误
 * @param {Error} error - 错误对象
 * @param {string} defaultMessage - 默认错误消息
 * @param {Function} showMessage - 显示消息的函数（可选）
 * @param {Function} redirectToLogin - 重定向到登录页的函数（可选）
 */
export function handleAPIError(error, defaultMessage = '操作失败', showMessage = null, redirectToLogin = null) {
  let message = defaultMessage

  if (error.response) {
    // 服务器返回了错误状态码
    const status = error.response.status
    const data = error.response.data

    switch (status) {
      case 400:
        message = data?.message || '请求参数错误'
        break
      case 401:
        message = '登录已过期，请重新登录'
        if (showMessage) {
          showMessage(message, 'warning')
          // 延迟2秒后跳转到登录页
          setTimeout(() => {
            if (redirectToLogin) {
              redirectToLogin()
            } else {
              window.location.href = '/login'
            }
          }, 2000)
        }
        return { handled: true, shouldRedirect: true, message }
      case 403:
        message = '权限不足，请联系管理员'
        break
      case 404:
        message = '文件不存在或已被删除'
        break
      case 500:
        message = '服务器错误，请稍后重试'
        break
      case 502:
      case 503:
      case 504:
        message = '服务器暂时不可用，请稍后重试'
        break
      default:
        message = data?.message || defaultMessage
    }
  } else if (error.request) {
    // 请求已发出但没有收到响应
    message = '网络错误，请检查网络连接'
  } else {
    // 其他错误
    message = error.message || defaultMessage
  }

  if (showMessage) {
    showMessage(message, 'error')
  }

  return { handled: true, shouldRedirect: false, message }
}

/**
 * 文件操作错误处理
 * @param {Error} error - 错误对象
 * @param {string} operation - 操作类型（upload/delete/refresh）
 * @param {Object} fileInfo - 文件信息（可选）
 * @param {Function} showMessage - 显示消息的函数（可选）
 */
export function handleFileOperationError(error, operation, fileInfo = {}, showMessage = null) {
  const fileName = fileInfo.originalName || fileInfo.fileName || '文件'
  let message = ''

  switch (operation) {
    case 'upload':
      if (error.response?.status === 413) {
        message = `${fileName} 上传失败：文件过大`
      } else if (error.response?.status === 415) {
        message = `${fileName} 上传失败：不支持的文件格式`
      } else if (error.response?.status === 507) {
        message = `${fileName} 上传失败：存储空间不足`
      } else {
        message = `${fileName} 上传失败：${error.response?.data?.message || error.message || '未知错误'}`
      }
      break

    case 'delete':
      if (error.response?.status === 404) {
        message = `${fileName} 删除失败：文件不存在`
      } else if (error.response?.status === 403) {
        message = `${fileName} 删除失败：没有删除权限`
      } else {
        message = `${fileName} 删除失败：${error.response?.data?.message || error.message || '未知错误'}`
      }
      break

    case 'refresh':
      message = `刷新文件列表失败：${error.response?.data?.message || error.message || '网络错误'}`
      break

    default:
      message = `文件操作失败：${error.response?.data?.message || error.message || '未知错误'}`
  }

  if (showMessage) {
    showMessage(message, 'error')
  }

  console.error(`${operation} 操作失败:`, error)
  return message
}

/**
 * 文件操作成功处理
 * @param {string} operation - 操作类型
 * @param {Object} fileInfo - 文件信息
 * @param {Function} showMessage - 显示消息的函数（可选）
 */
export function handleFileOperationSuccess(operation, fileInfo, showMessage = null) {
  const fileName = fileInfo.originalName || fileInfo.fileName || '文件'
  let message = ''

  switch (operation) {
    case 'upload':
      message = `${fileName} 上传成功`
      break
    case 'delete':
      message = `${fileName} 删除成功`
      break
    case 'refresh':
      message = '文件列表刷新成功'
      break
    default:
      message = '操作成功'
  }

  if (showMessage) {
    showMessage(message, 'success')
  }

  console.log(`${operation} 操作成功:`, fileName)
  return message
}

/**
 * 创建带重试机制的API调用
 * @param {Function} apiCall - API调用函数
 * @param {number} maxRetries - 最大重试次数
 * @param {number} retryDelay - 重试延迟（毫秒）
 * @returns {Promise} API调用结果
 */
export async function withRetry(apiCall, maxRetries = 3, retryDelay = 1000) {
  let lastError = null

  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      const result = await apiCall()
      if (attempt > 1) {
        console.log(`API调用在第 ${attempt} 次尝试后成功`)
      }
      return result
    } catch (error) {
      lastError = error

      if (attempt === maxRetries) {
        console.error(`API调用失败，已达到最大重试次数 ${maxRetries}:`, error)
        break
      }

      console.warn(`API调用失败，第 ${attempt} 次尝试，${retryDelay}ms 后重试:`, error.message)

      // 等待后重试
      await new Promise(resolve => setTimeout(resolve, retryDelay))

      // 指数退避
      retryDelay *= 2
    }
  }

  throw lastError
}

/**
 * 简单的消息显示函数（默认实现）
 * @param {string} message - 消息内容
 * @param {string} type - 消息类型（success/error/warning/info）
 */
export function defaultShowMessage(message, type = 'info') {
  // 如果项目中有 SweetAlert2 或其他 UI 库，可以在这里集成
  if (typeof Swal !== 'undefined') {
    const config = {
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true
    }

    switch (type) {
      case 'success':
        Swal.fire({
          ...config,
          icon: 'success',
          title: message
        })
        break
      case 'error':
        Swal.fire({
          ...config,
          icon: 'error',
          title: message
        })
        break
      case 'warning':
        Swal.fire({
          ...config,
          icon: 'warning',
          title: message
        })
        break
      default:
        Swal.fire({
          ...config,
          icon: 'info',
          title: message
        })
    }
  } else {
    // 简单的浏览器原生实现
    const className = `alert-${type === 'error' ? 'danger' : type}`
    const alertDiv = document.createElement('div')
    alertDiv.className = `alert ${className} alert-dismissible fade show position-fixed top-0 end-0 m-3`
    alertDiv.style.zIndex = '9999'
    alertDiv.innerHTML = `
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `
    document.body.appendChild(alertDiv)

    // 自动移除
    setTimeout(() => {
      if (alertDiv.parentNode) {
        alertDiv.parentNode.removeChild(alertDiv)
      }
    }, 3000)
  }
}

export default {
  handleAPIError,
  handleFileOperationError,
  handleFileOperationSuccess,
  withRetry,
  defaultShowMessage
}