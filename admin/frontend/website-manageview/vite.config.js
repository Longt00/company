import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  base: "./",
  server: {
    port: 33301,
    open: true,
    // 强制刷新环境变量
    fs: {
      strict: false
    },
    // 添加代理配置解决CORS问题
    proxy: {
      '/api': {
        target: 'http://localhost:33380',
        changeOrigin: true,
        secure: false
      }
    }
  },
  // 添加缓存破坏
  define: {
    __APP_VERSION__: JSON.stringify(Date.now())
  }
})