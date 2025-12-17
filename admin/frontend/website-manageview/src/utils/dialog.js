import Swal from 'sweetalert2'

/**
 * 统一的弹窗工具
 * 用于替换原生alert和confirm，避免暴露端口号
 */

/**
 * 显示提示消息
 * @param {string} message - 消息内容
 * @param {string} type - 类型：success/error/warning/info
 */
export function showMessage(message, type = 'info') {
  const iconMap = {
    success: 'success',
    error: 'error',
    warning: 'warning',
    info: 'info'
  }

  return Swal.fire({
    title: type === 'success' ? '操作成功' : type === 'error' ? '操作失败' : '温馨提示',
    text: message,
    icon: iconMap[type] || 'info',
    confirmButtonText: '确定',
    confirmButtonColor: '#1976d2'
  })
}

/**
 * 显示确认对话框
 * @param {string} message - 消息内容
 * @param {string} title - 标题
 * @returns {Promise<boolean>} 用户是否确认
 */
export async function showConfirm(message, title = '温馨提示') {
  const result = await Swal.fire({
    title: title,
    text: message,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })
  
  return result.isConfirmed
}

/**
 * 显示成功消息
 * @param {string} message - 消息内容
 */
export function showSuccess(message) {
  return showMessage(message, 'success')
}

/**
 * 显示错误消息
 * @param {string} message - 消息内容
 */
export function showError(message) {
  return showMessage(message, 'error')
}

/**
 * 显示警告消息
 * @param {string} message - 消息内容
 */
export function showWarning(message) {
  return showMessage(message, 'warning')
}

/**
 * 显示信息消息
 * @param {string} message - 消息内容
 */
export function showInfo(message) {
  return showMessage(message, 'info')
}

export default {
  showMessage,
  showConfirm,
  showSuccess,
  showError,
  showWarning,
  showInfo
}
