/**
 * 富内容相关API
 * 用于获取首页内容块（公司基本信息、优势、简介等）
 */

import apiClient from './config'

/**
 * 根据内容类型获取富内容
 * @param {string} contentType - 内容类型：home-company-info, home-advantage, home-profile
 * @returns {Promise} API响应
 */
export const getRichContentByType = async (contentType) => {
  try {
    console.log(`🔄 [RichContent API] 获取${contentType}内容...`)
    const response = await apiClient.get(`/api/rich-content/type/${contentType}`)
    console.log(`✅ [RichContent API] ${contentType}响应:`, response.data)
    return response.data
  } catch (error) {
    console.error(`❌ [RichContent API] 获取${contentType}失败:`, error)
    throw error
  }
}

/**
 * 获取首页公司基本信息
 */
export const getHomeCompanyInfo = () => {
  return getRichContentByType('home-company-info')
}

/**
 * 获取首页公司优势
 */
export const getHomeAdvantage = () => {
  return getRichContentByType('home-advantage')
}

/**
 * 获取首页公司简介
 */
export const getHomeProfile = () => {
  return getRichContentByType('home-profile')
}
