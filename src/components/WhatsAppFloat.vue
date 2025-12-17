<template>
  <div class="whatsapp-container">
    <!-- WhatsApp 聊天按钮 -->
    <div
      class="whatsapp-button"
      @click="toggleChat"
      :class="{ 'active': isChatOpen }"
    >
      <div class="whatsapp-icon">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M12 2C6.48 2 2 6.48 2 12C2 13.54 2.46 14.88 3.24 16H4.66C3.78 14.82 3.26 13.42 3.26 11.9C3.26 7.48 6.76 4 11.22 4C15.68 4 19.18 7.48 19.18 11.9C19.18 16.32 15.68 19.8 11.22 19.8C10.3 19.8 9.42 19.64 8.6 19.36L7.74 20.68C8.86 21.22 10.12 21.52 11.44 21.52C16.96 21.52 21.48 17 21.48 11.48C21.48 5.96 16.96 1.44 11.44 1.44C5.92 1.44 1.4 5.96 1.4 11.48C1.4 14.36 2.62 16.94 4.54 18.76" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M17.5 7.5L11 14L8.5 11.5" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="whatsapp-text">WhatsApp</span>
    </div>

    <!-- 聊天对话框 -->
    <transition name="chat-fade">
      <div v-if="isChatOpen" class="whatsapp-chat">
        <div class="chat-header">
          <div class="chat-info">
            <div class="chat-details">
              <h4>Guangzhou Kaicheng Garment Co., Ltd.</h4>
              <p>Typically replies within minutes</p>
            </div>
          </div>
          <button class="close-btn" @click="toggleChat">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M15 5L5 15M5 5L15 15" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
        <div class="chat-messages">
          <div class="message received">
            <p>👋 Hello! Welcome to Guangzhou Kaicheng Garment Co., Ltd.How can we help you?</p>
          </div>
        </div>
        <div class="chat-input">
          <textarea
            v-model="message"
            placeholder="Type your message..."
            rows="1"
            @keydown.enter.exact.prevent="sendMessage"
            @keydown.enter.shift.exact="newLine"
            ref="messageInput"
          ></textarea>
          <button
            class="send-btn"
            @click="sendMessage"
            :disabled="!message.trim()"
          >
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M18 2L2 10L9 11L11 18L18 2Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </transition>

    <div v-if="!isChatOpen && showTooltip" class="tooltip-bubble">
      <p>Need help? Contact us via WhatsApp  </p>
      <button class="tooltip-close" @click="hideTooltip">×</button>
    </div>
  </div>
</template>

<script>
import { WHATSAPP_CONFIG, buildWhatsAppUrl, buildWelcomeWhatsAppUrl } from '@/config/whatsapp'
import { getCompanyInfo } from '../api/company'

export default {
  name: 'WhatsAppFloat',
  data() {
    return {
      isChatOpen: false,
      message: '',
      showTooltip: true,
      whatsappNumber: WHATSAPP_CONFIG.phoneNumber,
      companyName: WHATSAPP_CONFIG.companyInfo.name,
      companyDescription: WHATSAPP_CONFIG.companyInfo.description,
      responseTime: WHATSAPP_CONFIG.companyInfo.responseTime,
      dynamicWhatsAppNumber: null
    }
  },
  mounted() {
    setTimeout(() => {
      this.showTooltip = false
    }, 5000)
    document.addEventListener('keydown', this.handleEscKey)
    this.fetchWhatsAppNumber()
  },
  beforeUnmount() {
    document.removeEventListener('keydown', this.handleEscKey)
  },
  methods: {
    toggleChat() {
      this.isChatOpen = !this.isChatOpen
      if (this.isChatOpen) {
        this.$nextTick(() => {
          this.$refs.messageInput?.focus()
        })
      }
    },
    sendMessage() {
      if (!this.message.trim()) return
      const whatsappUrlObj = buildWhatsAppUrl(this.message, this.dynamicWhatsAppNumber)
      if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
        window.location.href = whatsappUrlObj.appUrl
        setTimeout(() => {
          window.open(whatsappUrlObj.webUrl, '_blank')
        }, 1000)
      } else {
        window.open(whatsappUrlObj.webUrl, '_blank', 'noopener,noreferrer')
      }
      this.message = ''
      this.isChatOpen = false
    },
    newLine() {
      this.message += '\n'
    },
    hideTooltip() {
      this.showTooltip = false
    },
    async fetchWhatsAppNumber() {
      try {
        const response = await getCompanyInfo()
        const resData = response?.data
        if (!resData) return
        let rawNumber = null
        if (resData.code === 200 && resData.data) {
          rawNumber = resData.data.companyPhone
        }
        if (rawNumber) {
          let clean = String(rawNumber).replace(/[^\d+]/g, '')
          if (clean.startsWith('00')) {
            clean = '+' + clean.slice(2)
          }
          if (!clean.startsWith('+') && clean.length >= 10) {
            if (clean.length === 11 && clean.startsWith('1')) {
              clean = '+86' + clean
            }
          }
          this.dynamicWhatsAppNumber = clean
          return
        }
      } catch (error) {
        console.log('ℹ️ [WhatsAppFloat] 公司信息接口暂不可用，使用默认配置:', error.message)
      }
    },
    handleEscKey(e) {
      if (e.key === 'Escape' && this.isChatOpen) {
        this.isChatOpen = false
      }
    },
    handleLogoError(event) {
      event.target.src = '/images/placeholder-40x40.svg'
    }
  }
}
</script>

<style scoped>
/* 父容器：默认PC端布局，手机端调整为紧靠右侧 */
.whatsapp-container {
  position: fixed;
  z-index: 999999;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

/* WhatsApp按钮 */
.whatsapp-button {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #25D366;
  color: white;
  border: none;
  border-radius: 50px;
  padding: 12px 20px;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(37, 211, 102, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-width: 120px;
  justify-content: center;
}

.whatsapp-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(37, 211, 102, 0.4);
}

.whatsapp-button.active {
  background: #128C7E;
}

.whatsapp-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.whatsapp-text {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

/* 聊天对话框：紧贴按钮正上方，水平右对齐 */
.whatsapp-chat {
  position: absolute;
  bottom: calc(100% + 10px);
  right: 0; /* 弹窗与按钮水平右对齐 */
  width: 280px;
  height: 320px;
  transform: scale(0.85);
  transform-origin: right bottom; /* 缩放中心在弹窗右下角（与按钮对齐） */
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #e0e0e0;
  z-index: 999999;
}

/* 聊天头部 */
.chat-header {
  background: #075E54;
  color: white;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chat-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  width: 40px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-details h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.2;
}

.chat-details p {
  margin: 2px 0 0;
  font-size: 13px;
  opacity: 0.8;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: background 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 聊天消息区域 */
.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #E5DDD5;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message {
  max-width: 80%;
  padding: 10px 15px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.4;
}

.message.received {
  background: white;
  align-self: flex-start;
  border-bottom-left-radius: 4px;
}

.message.sent {
  background: #DCF8C6;
  align-self: flex-end;
  border-bottom-right-radius: 4px;
}

.message p {
  margin: 0;
}

/* 聊天输入区域 */
.chat-input {
  padding: 15px 20px;
  background: white;
  border-top: 1px solid #e0e0e0;
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.chat-input textarea {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 10px 15px;
  font-size: 14px;
  line-height: 1.4;
  resize: none;
  outline: none;
  min-height: 40px;
  max-height: 100px;
  font-family: inherit;
}

.chat-input textarea:focus {
  border-color: #25D366;
}

.send-btn {
  width: 40px;
  height: 40px;
  background: #25D366;
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  background: #128C7E;
}

.send-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 提示气泡 */
.tooltip-bubble {
  position: absolute;
  bottom: calc(100% + 10px);
  right: 0;
  background: #333;
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13px;
  min-width: 280px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  animation: tooltipSlide 0.3s ease;
  z-index: 999999;
}

.tooltip-bubble::after {
  content: '';
  position: absolute;
  bottom: -6px;
  right: 20px;
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid #333;
}

.tooltip-bubble p {
  margin: 0;
  padding: 0;
  font-weight: 500;
  line-height: 1.4;
  padding-right: 30px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tooltip-close {
  position: absolute;
  top: 4px;
  right: 8px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  cursor: pointer;
  padding: 2px;
  transition: all 0.2s ease;
}

.tooltip-close:hover {
  color: white;
}

/* 动画 */
.chat-fade-enter-active,
.chat-fade-leave-active {
  transition: all 0.3s ease;
}

.chat-fade-enter-from {
  opacity: 0;
  transform: scale(0.85) translateY(20px);
}

.chat-fade-leave-to {
  opacity: 0;
  transform: scale(0.85) translateY(20px);
}

@keyframes tooltipSlide {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 手机端：紧靠屏幕右侧 + 向上移动100px */
@media (max-width: 768px) {
  .whatsapp-container {
    top: calc(45% - 100px);
    right: 0;
    bottom: auto;
    left: auto;
  }
  .whatsapp-chat {
    width: 260px;
    height: 300px;
  }
  .whatsapp-button {
    padding: 10px 16px;
    gap: 8px;
    min-width: 100px;
  }
  .whatsapp-icon {
    width: 20px;
    height: 20px;
  }
  .whatsapp-icon svg {
    width: 20px;
    height: 20px;
  }
  .whatsapp-text {
    font-size: 12px;
  }
}

/* PC端：原布局 + 弹窗向上移动20px（核心修改） */
@media (min-width: 769px) {
  .whatsapp-container {
    top: calc(20% - 100px);
    bottom: auto;
    right: 30px;
    left: auto;
  }
  .whatsapp-chat {
    position: absolute;
    bottom: auto;
    top: -40px; /* 再向上移动20px，总计上移40px */
    right: calc(100% + 20px);
    width: 345px;
    height: 285px;
    transform-origin: right top;
  }
  .chat-fade-enter-from {
    transform: scale(0.85) translateX(20px);
  }
  .chat-fade-leave-to {
    transform: scale(0.85) translateX(20px);
  }
}
</style>