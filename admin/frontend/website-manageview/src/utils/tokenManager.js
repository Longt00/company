/**
 * Token管理工具
 * 负责token的存储、验证和过期检查
 */

const TOKEN_KEY = 'token'
const TOKEN_TIMESTAMP_KEY = 'token_timestamp'
const TOKEN_EXPIRATION = 24 * 60 * 60 * 1000 // 24小时（毫秒）

/**
 * 保存token和时间戳
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(TOKEN_TIMESTAMP_KEY, Date.now().toString())
}

/**
 * 获取token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 清除token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_TIMESTAMP_KEY)
  localStorage.removeItem('userInfo')
}

/**
 * 检查token是否过期
 * @returns {boolean} true表示已过期
 */
export function isTokenExpired() {
  const token = getToken()
  const timestamp = localStorage.getItem(TOKEN_TIMESTAMP_KEY)
  
  if (!token || !timestamp) {
    return true
  }
  
  const now = Date.now()
  const savedTime = parseInt(timestamp)
  
  // 检查是否超过24小时
  return (now - savedTime) >= TOKEN_EXPIRATION
}

/**
 * 获取token剩余有效时间（毫秒）
 */
export function getTokenRemainingTime() {
  const timestamp = localStorage.getItem(TOKEN_TIMESTAMP_KEY)
  
  if (!timestamp) {
    return 0
  }
  
  const now = Date.now()
  const savedTime = parseInt(timestamp)
  const elapsed = now - savedTime
  const remaining = TOKEN_EXPIRATION - elapsed
  
  return remaining > 0 ? remaining : 0
}

/**
 * 检查token是否即将过期（剩余时间少于2小时）
 */
export function isTokenExpiringSoon() {
  const remaining = getTokenRemainingTime()
  const twoHours = 2 * 60 * 60 * 1000
  
  return remaining > 0 && remaining < twoHours
}