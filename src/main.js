import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/variables.css'  // 全局CSS变量和响应式断点
import './style.css'
import './styles/backdrop-filter-fix.css'

const app = createApp(App)
app.use(router)

app.mount('#app')

// 设置背景图片（在挂载后执行）
// 优先从接口获取，失败则使用本地默认背景
async function loadBackgroundImage() {
  // 设置背景样式的通用函数
  const setBackgroundStyle = (imageUrl) => {
    document.body.style.backgroundImage = `url(${imageUrl})`
    document.body.style.backgroundSize = 'cover'
    document.body.style.backgroundPosition = 'center'
    document.body.style.backgroundRepeat = 'no-repeat'
    document.body.style.backgroundAttachment = 'fixed'
    document.body.style.minHeight = '100vh'
  }

  try {
    // 尝试从接口获取背景图片
    const response = await fetch('/api/public/media/category/background-image')
    const result = await response.json()
    
    console.log('🖼️ [main.js] 背景图片接口响应:', result)
    
    if (result.code === 200 && result.data?.files?.length > 0) {
      const bgFile = result.data.files[0]
      if (bgFile.fileUrl) {
        setBackgroundStyle(bgFile.fileUrl)
        console.log('✅ [main.js] 成功加载接口背景图片:', bgFile.fileUrl)
        return
      }
    }
  } catch (error) {
    console.log('ℹ️ [main.js] 接口获取背景失败，使用本地默认背景:', error.message)
  }

  // 回退到本地默认背景
  try {
    const module = await import('./images/background/background.jpg')
    setBackgroundStyle(module.default)
    console.log('ℹ️ [main.js] 使用本地默认背景图片')
  } catch (error) {
    console.warn('背景图片加载失败:', error)
  }
}

setTimeout(loadBackgroundImage, 100)







