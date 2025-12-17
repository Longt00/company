// WhatsApp 配置文件
// WhatsApp Configuration

// WhatsApp连接配置
export const WHATSAPP_CONFIG = {
  // 设置您的WhatsApp手机号（包含国家代码）
  // 中国手机号示例：+8615920419867
  // 其他国家示例：
  // 美国：+15551234567
  // 英国：+447911123456
  // 印度：+919876543210
  phoneNumber: '+8615920419867',

  // 公司信息
  companyInfo: {
    name: 'Guangzhou Kaicheng Garment Co., Ltd.',
    description: 'Professional denim clothing manufacturer',
    responseTime: 'Typically replies within minutes'
  },

  // 快速回复选项
  quickReplies: [
    "I'm interested in your denim products",
    "What's your pricing for custom orders?",
    "Do you offer sample service?",
    "What's your minimum order quantity?",
    "I need bulk pricing information"
  ]
}

// 获取纯数字的手机号（用于wa.me链接）
export function getCleanPhoneNumber(phoneOverride) {
  const raw = phoneOverride || WHATSAPP_CONFIG.phoneNumber
  return raw ? raw.replace(/[^\d+]/g, '') : ''
}

// 构建WhatsApp消息URL
// 可选传入覆盖用的手机号（phoneOverride），否则使用配置中的默认号码
export function buildWhatsAppUrl(message = '', phoneOverride) {
  const cleanPhone = getCleanPhoneNumber(phoneOverride)

  const encodedMessage = message ? encodeURIComponent(message) : ''

  // 尝试多种跳转方式，优先使用WhatsApp应用协议
  const whatsappAppUrl = `whatsapp://send/?phone=${cleanPhone}${encodedMessage ? '&text=' + encodedMessage : ''}`
  const waMeUrl = `https://wa.me/${cleanPhone}${encodedMessage ? '?text=' + encodedMessage : ''}`

  // 返回两个URL，让工具函数选择最佳方式
  return { appUrl: whatsappAppUrl, webUrl: waMeUrl, primaryUrl: waMeUrl }
}

// 构建带欢迎消息的WhatsApp URL（使用默认配置中的号码）
export function buildWelcomeWhatsAppUrl() {

  const welcomeMessage = `👋 Hello! Welcome to ${WHATSAPP_CONFIG.companyInfo.name}

We provide professional denim clothing customization services. How can we help you?

Feel free to ask about our products, pricing, or customization options.`

  return buildWhatsAppUrl(welcomeMessage)
}

// 构建产品相关的WhatsApp消息（使用默认配置中的号码）
export function buildProductWhatsAppUrl(productInfo = {}) {

  const {
    productName = 'your products',
    productPrice = 'competitive pricing',
    customOption = 'customization options'
  } = productInfo

  const message = `Hi! I'm interested in ${productName}. Could you please provide more information about:\n\n• ${productPrice}\n• ${customOption}\n• Minimum order quantity\n• Production timeline\n\nThank you!`

  return buildWhatsAppUrl(message)
}

// 构建询价相关的WhatsApp消息（使用默认配置中的号码）
export function buildInquiryWhatsAppUrl(inquiryData = {}) {

  const {
    productType = 'denim clothing',
    quantity = '1000 pieces',
    customRequirements = 'custom branding'
  } = inquiryData

  const message = `Hi! I'd like to inquire about:\n\n• Product: ${productType}\n• Quantity: ${quantity}\n• Requirements: ${customRequirements}\n\nCould you please send me a quote? Thank you!`

  return buildWhatsAppUrl(message)
}

// 默认导出配置
export default WHATSAPP_CONFIG