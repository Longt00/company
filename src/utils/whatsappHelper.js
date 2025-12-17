// WhatsApp 连接工具函数
// WhatsApp Connection Helper Functions

import {
  buildWhatsAppUrl,
  buildWelcomeWhatsAppUrl,
  buildProductWhatsAppUrl,
  buildInquiryWhatsAppUrl,
  WHATSAPP_CONFIG
} from '@/config/whatsapp'

/**
 * 检测是否为移动设备
 * @returns {boolean} 是否为移动设备
 */
export function isMobileDevice() {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

/**
 * 直接打开WhatsApp聊天窗口
 * @param {string} message - 要发送的消息
 */
export function openWhatsAppChat(message = '') {
  const whatsappUrl = buildWhatsAppUrl(message)
  openWhatsAppUrl(whatsappUrl)
}

/**
 * 通用WhatsApp URL打开函数
 * @param {string|Object} whatsappUrlOrObj - WhatsApp链接或URL对象
 */
export function openWhatsAppUrl(whatsappUrlOrObj) {
  let urlToUse

  if (typeof whatsappUrlOrObj === 'object' && whatsappUrlOrObj !== null) {
    // 处理新的URL对象格式
    urlToUse = whatsappUrlOrObj.primaryUrl
  } else {
    // 处理旧的字符串格式（向后兼容）
    urlToUse = whatsappUrlOrObj
  }

  if (isMobileDevice()) {
    // 移动设备尝试多种跳转方式
    if (typeof whatsappUrlOrObj === 'object' && whatsappUrlOrObj.appUrl) {
      // 先尝试WhatsApp应用协议
      window.location.href = whatsappUrlOrObj.appUrl

      // 如果应用协议失败，延迟跳转到web版本
      setTimeout(() => {
        window.open(whatsappUrlOrObj.webUrl, '_blank')
      }, 1000)
    } else {
      // 回退到标准web方式
      window.location.href = urlToUse
    }
  } else {
    // 桌面设备使用新窗口
    window.open(urlToUse, '_blank', 'noopener,noreferrer')
  }
}

/**
 * 打开带欢迎消息的WhatsApp
 */
export function openWhatsAppWithWelcome() {
  const whatsappUrl = buildWelcomeWhatsAppUrl()
  openWhatsAppUrl(whatsappUrl)
}

/**
 * 打开产品相关的WhatsApp咨询
 * @param {Object} productInfo - 产品信息
 */
export function openProductInquiry(productInfo) {
  const whatsappUrl = buildProductWhatsAppUrl(productInfo)
  openWhatsAppUrl(whatsappUrl)
}

/**
 * 打开询价相关的WhatsApp
 * @param {Object} inquiryData - 询价信息
 */
export function openPriceInquiry(inquiryData) {
  const whatsappUrl = buildInquiryWhatsAppUrl(inquiryData)
  openWhatsAppUrl(whatsappUrl)
}

/**
 * 生成WhatsApp分享链接（不自动打开）
 * @param {string} message - 要分享的消息
 * @returns {string} WhatsApp URL
 */
export function generateWhatsAppLink(message) {
  const urlObj = buildWhatsAppUrl(message)
  return typeof urlObj === 'object' ? urlObj.primaryUrl : urlObj
}

/**
 * 从组件中调用WhatsApp连接
 * 通常在Vue组件的methods中使用
 *
 * 示例用法：
 * methods: {
 *   contactViaWhatsApp() {
 *     openWhatsAppChat('Hello, I would like to inquire about your products.')
 *   },
 *
 *   shareProduct(product) {
 *     openProductInquiry({
 *       productName: product.name,
 *       productPrice: product.price,
 *       customOption: 'customization available'
 *     })
 *   }
 * }
 */
export const whatsappMethods = {
  openWhatsAppChat,
  openWhatsAppWithWelcome,
  openProductInquiry,
  openPriceInquiry,
  generateWhatsAppLink
}

/**
 * Vue mixin，用于在组件中快速添加WhatsApp功能
 *
 * 使用方法：
 * import { whatsappMixin } from '@/utils/whatsappHelper'
 *
 * export default {
 *   mixins: [whatsappMixin],
 *   methods: {
 *     customWhatsAppAction() {
 *       this.openWhatsAppChat('Custom message from component')
 *     }
 *   }
 * }
 */
export const whatsappMixin = {
  methods: {
    openWhatsAppChat(message = '') {
      openWhatsAppChat(message)
    },

    openWhatsAppWithWelcome() {
      openWhatsAppWithWelcome()
    },

    openProductInquiry(productInfo = {}) {
      openProductInquiry(productInfo)
    },

    openPriceInquiry(inquiryData = {}) {
      openPriceInquiry(inquiryData)
    },

    generateWhatsAppLink(message) {
      return generateWhatsAppLink(message)
    }
  }
}

/**
 * 预设的常用消息模板
 */
export const messageTemplates = {
  // 通用问候
  greeting: "Hello! I'm interested in your products. Could you please provide more information?",

  // 产品询价
  productInquiry: "Hi! I'd like to inquire about your denim products. Could you send me your catalog and pricing?",

  // 样品申请
  sampleRequest: "Hello! I'd like to request a sample of your products before placing a bulk order. Is this possible?",

  // 合作洽谈
  partnership: "Hi! I represent a retail company and I'm interested in exploring partnership opportunities. Could we discuss further?",

  // 技术支持
  technicalSupport: "Hello! I have some technical questions about your customization process. Could you help me?",

  // 询价模板
  priceInquiry: "Could you please provide a quote for the following specifications?",

  // 紧急联系
  urgent: "Urgent inquiry! I need to place an order quickly. Please contact me as soon as possible."
}

/**
 * 验证WhatsApp号码格式
 * @param {string} phoneNumber - 要验证的电话号码
 * @returns {boolean} 是否为有效格式
 */
export function validateWhatsAppNumber(phoneNumber) {
  // 移除所有非数字字符，只保留国家代码和电话号码
  const cleanPhone = phoneNumber.replace(/[^\d+]/g, '')

  // 基本验证：至少8位数字（国家代码+电话号码）
  return cleanPhone.length >= 8 && cleanPhone.length <= 15
}

/**
 * 格式化电话号码显示
 * @param {string} phoneNumber - 原始电话号码
 * @returns {string} 格式化后的号码
 */
export function formatPhoneNumber(phoneNumber) {
  const cleanPhone = phoneNumber.replace(/[^\d+]/g, '')

  // 简单格式化：每3-4位添加分隔符
  if (cleanPhone.length <= 4) return cleanPhone

  let formatted = ''
  for (let i = 0; i < cleanPhone.length; i += 3) {
    const chunk = cleanPhone.slice(i, i + 3)
    formatted += (formatted ? ' ' : '') + chunk
  }

  return formatted
}

export default {
  WHATSAPP_CONFIG,
  messageTemplates,
  whatsappMixin,
  ...whatsappMethods
}