<template>
  <div id="app">
    <!-- 主内容区域 -->
    <router-view />

    <!-- 全局 WhatsApp 浮窗：挂在根组件，避免被页面内部布局遮挡 -->
    <WhatsAppFloat />
  </div>
</template>

<script>
import WhatsAppFloat from '@/components/WhatsAppFloat.vue'

export default {
  name: 'App',
  components: {
    WhatsAppFloat
  }
}
</script>

<style>
#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 隐藏 sub-frame-error 错误框 - 更强力的选择器 */
#sub-frame-error,
iframe[style*="sub-frame-error"],
.sub-frame-error,
div[class*="sub-frame-error"],
div[id*="sub-frame-error"],
div[id*="sub-frame-error"] div,
div[class*="sub-frame-error"] div,
body > div[id*="sub-frame"],
html > div[id*="sub-frame"] {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  width: 0 !important;
  height: 0 !important;
  position: absolute !important;
  top: -9999px !important;
  left: -9999px !important;
  z-index: -9999 !important;
  pointer-events: none !important;
}

/* 隐藏可能的 iframe 错误相关元素 */
iframe[style*="error"],
iframe[src*="error"],
iframe[title*="error"],
iframe[aria-label*="error"] {
  display: none !important;
}
</style>
