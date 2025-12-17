<template>
  <section class="company-advantages">
    <div class="container">
      <div class="advantages-grid">
        <!-- COMPANY 块 -->
        <div class="advantage-block" @mouseenter="onHover" @mouseleave="onLeave">
          <div class="icon-container">
            <img
              v-if="shouldShowImage(1)"
              :src="getIconSrc(1)"
              alt="COMPANY icon"
              class="advantage-icon-image"
              @error="(e) => handleIconError(e, 1)"
            >
            <svg v-else width="60" height="60" viewBox="0 0 60 60" fill="none">
              <circle cx="30" cy="30" r="30" fill="#333"/>
              <circle cx="22" cy="22" r="4" stroke="white" stroke-width="2" fill="none"/>
              <circle cx="38" cy="38" r="4" stroke="white" stroke-width="2" fill="none"/>
              <path d="M24 24C26 26 28 28 30 30" stroke="white" stroke-width="2" stroke-linecap="round"/>
              <path d="M36 36C34 34 32 32 30 30" stroke="white" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3 class="block-title">COMPANY</h3>
          <p class="block-description">
            we are professioal factory to make various denim & jeans professional factory.
          </p>
        </div>

        <!-- TEAM 块 -->
        <div class="advantage-block" @mouseenter="onHover" @mouseleave="onLeave">
          <div class="icon-container">
            <img
              v-if="shouldShowImage(2)"
              :src="getIconSrc(2)"
              alt="TEAM icon"
              class="advantage-icon-image"
              @error="(e) => handleIconError(e, 2)"
            >
            <svg v-else width="60" height="60" viewBox="0 0 60 60" fill="none">
              <circle cx="30" cy="30" r="30" fill="#333"/>
              <!-- 人物图标 -->
              <circle cx="20" cy="25" r="3" stroke="white" stroke-width="2" fill="none"/>
              <path d="M20 28C20 30 22 32 24 32" stroke="white" stroke-width="2" stroke-linecap="round"/>
              <circle cx="30" cy="25" r="3" stroke="white" stroke-width="2" fill="none"/>
              <path d="M30 28C30 30 28 32 26 32" stroke="white" stroke-width="2" stroke-linecap="round"/>
              <circle cx="40" cy="25" r="3" stroke="white" stroke-width="2" fill="none"/>
              <path d="M40 28C40 30 38 32 36 32" stroke="white" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </div>
          <h3 class="block-title">TEAM</h3>
          <p class="block-description">
            We have a professional clothing production team with numerous workers and professional services.
          </p>
        </div>

        <!-- BEST PRICES 块 -->
        <div class="advantage-block" @mouseenter="onHover" @mouseleave="onLeave">
          <div class="icon-container">
            <img
              v-if="shouldShowImage(3)"
              :src="getIconSrc(3)"
              alt="BEST PRICES icon"
              class="advantage-icon-image"
              @error="(e) => handleIconError(e, 3)"
            >
            <svg v-else width="60" height="60" viewBox="0 0 60 60" fill="none">
              <circle cx="30" cy="30" r="30" fill="#333"/>
              <!-- 货币符号 -->
              <circle cx="30" cy="30" r="8" stroke="white" stroke-width="2" fill="none"/>
              <path d="M22 30h16" stroke="white" stroke-width="2" stroke-linecap="round"/>
              <path d="M30 22v16" stroke="white" stroke-width="2" stroke-linecap="round"/>
              <text x="30" y="35" text-anchor="middle" fill="white" font-size="8" font-weight="bold">¥</text>
            </svg>
          </div>
          <h3 class="block-title">BEST PRICES</h3>
          <p class="block-description">
            Bulk production, supply efficiency, direct savings, cost control.
          </p>
        </div>

        <!-- QUALITY 块 -->
        <div class="advantage-block" @mouseenter="onHover" @mouseleave="onLeave">
          <div class="icon-container">
            <img
              v-if="shouldShowImage(4)"
              :src="getIconSrc(4)"
              alt="QUALITY icon"
              class="advantage-icon-image"
              @error="(e) => handleIconError(e, 4)"
            >
            <svg v-else width="60" height="60" viewBox="0 0 60 60" fill="none">
              <circle cx="30" cy="30" r="30" fill="#333"/>
              <!-- 点赞图标 -->
              <path d="M20 35C20 30 24 26 30 26C36 26 40 30 40 35C40 40 30 50 30 50C30 50 20 40 20 35Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
              <path d="M26 32C26 28 28 26 30 26C32 26 34 28 34 32" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" fill="none"/>
            </svg>
          </div>
          <h3 class="block-title">QUALITY</h3>
          <p class="block-description">
            Quality is our core guarantee, and the quality of clothing will definitely make you feel at ease.
          </p>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import homeIconsAPI from '@/api/homeIcons'
import { getMediaFiles } from '@/api/media'

// 图标位置与分类的映射
const ICON_CATEGORY_MAP = {
  1: 'icon-company',
  2: 'icon-team',
  3: 'icon-best-prices',
  4: 'icon-quality'
}

export default {
  name: 'CompanyAdvantagesSection',
  data() {
    return {
      homeIcons: {},
      dynamicIcons: {}, // 从媒体API获取的动态图标
      failedImages: new Set() // 跟踪加载失败的图标
    }
  },
  created() {
    this.loadHomeIcons()
    this.loadDynamicIcons()
  },
  methods: {
    async loadHomeIcons() {
      try {
        const config = await homeIconsAPI.getHomeIcons()
        console.log('📦 [CompanyAdvantagesSection] 接口返回的原始配置:', config)
        
        const iconsByPosition = {}
        if (config && Array.isArray(config.icons)) {
          config.icons.forEach(icon => {
            if (icon && typeof icon.position === 'number') {
              iconsByPosition[icon.position] = icon
              console.log(`  - 位置 ${icon.position}: customIcon="${icon.customIcon || ''}", defaultIcon="${icon.defaultIcon || ''}"`)
            }
          })
        } else {
          console.warn('⚠️ [CompanyAdvantagesSection] config.icons 不是数组:', config?.icons)
        }
        
        this.homeIcons = iconsByPosition
        console.log('✅ [CompanyAdvantagesSection] 最终 homeIcons:', this.homeIcons)
      } catch (error) {
        console.error('加载首页图标配置失败:', error)
      }
    },
    
    // 从媒体API加载动态图标（后台上传的最新图标）
    async loadDynamicIcons() {
      for (const [position, category] of Object.entries(ICON_CATEGORY_MAP)) {
        try {
          const response = await getMediaFiles(category)
          const resData = response?.data
          
          if (resData && resData.code === 200 && resData.data?.files?.length > 0) {
            // 获取最新上传的图标（按id降序，取最大的）
            const files = resData.data.files
            const latestFile = files.reduce((latest, file) => 
              file.id > latest.id ? file : latest
            , files[0])
            
            const storageName = latestFile.storageName || latestFile.filePath
            if (storageName) {
              this.dynamicIcons[position] = `/api/files/${storageName}`
              console.log(`✅ [CompanyAdvantagesSection] 位置${position}获取动态图标:`, this.dynamicIcons[position])
            }
          }
        } catch (error) {
          console.log(`ℹ️ [CompanyAdvantagesSection] 位置${position}无动态图标:`, error.message)
        }
      }
    },
    getIconSrc(position) {
      // 优先使用动态图标（后台上传的最新图标）
      if (this.dynamicIcons[position]) {
        console.log(`🔍 [getIconSrc] position=${position}, 使用动态图标:`, this.dynamicIcons[position])
        return this.dynamicIcons[position]
      }
      
      const icon = this.homeIcons[position]
      if (!icon || icon.enabled === false || this.failedImages.has(position)) {
        return ''
      }
      const iconSrc = icon.customIcon || icon.defaultIcon || ''
      
      console.log(`🔍 [getIconSrc] position=${position}, 原始iconSrc="${iconSrc}"`)

      // 处理 /uploads/ 开头的路径，转换为 /api/files/ 路径
      if (iconSrc && iconSrc.startsWith('/uploads/')) {
        // 根据文档，正确的访问路径是 /api/files/icons/xxx.png
        const apiPath = iconSrc.replace('/uploads/', '/api/files/')
        console.log(`🔍 [getIconSrc] 转换后URL="${apiPath}"`)
        return apiPath
      }
      
      // 如果已经是完整URL或其他格式
      console.log(`🔍 [getIconSrc] 直接使用="${iconSrc}"`)
      return iconSrc
    },
    shouldShowImage(position) {
      // 如果有动态图标，优先显示
      if (this.dynamicIcons[position] && !this.failedImages.has(position)) {
        return true
      }
      const icon = this.homeIcons[position]
      return icon && icon.enabled !== false && !this.failedImages.has(position) && (icon.customIcon || icon.defaultIcon)
    },
    onHover(event) {
      const block = event.currentTarget;
      block.style.transform = 'translateY(-8px)';
      block.style.transition = 'transform 0.3s ease';
    },
    onLeave(event) {
      const block = event.currentTarget;
      block.style.transform = 'translateY(0)';
      block.style.transition = 'transform 0.3s ease';
    },
    handleIconError(event, position) {
      // 标记该位置的图标加载失败
      this.failedImages.add(position);
      console.warn('图标加载失败，使用默认SVG图标:', event.target.src);
    }
  }
}
</script>

<style scoped>
.company-advantages {
  padding: 80px 0;
  background: #fff;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.advantages-grid {
  display: flex;
  justify-content: space-between;
  flex-wrap: nowrap;
  gap: 24px;
  align-items: start;
}

.advantage-block {
  flex: 1 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.advantage-block:hover {
  transform: translateY(-8px);
}

.icon-container {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-container svg {
  width: clamp(48px, 5vw, 60px);
  height: clamp(48px, 5vw, 60px);
}

.advantage-icon-image {
  width: clamp(48px, 5vw, 60px);
  height: clamp(48px, 5vw, 60px);
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.block-title {
  font-size: clamp(14px, 1.5vw, 20px);
  font-weight: bold;
  color: #fff;
  margin-bottom: 15px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.block-description {
  font-size: clamp(12px, 1.2vw, 16px);
  color: #fff;
  line-height: 1.5;
  text-align: left;
  margin: 0;
  max-width: 220px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .company-advantages {
    padding: 40px 0;
  }

  .container {
    padding: 0 16px;
  }

  .advantages-grid {
    gap: 16px;
  }

  .advantage-block {
    padding: 12px;
  }

  .block-description {
    max-width: 100%;
  }
}

@media (max-width: 480px) {
  .company-advantages {
    padding: 24px 0;
  }

  .container {
    padding: 0 12px;
  }

  .advantages-grid {
    gap: 12px;
  }

  .advantage-block {
    padding: 8px;
  }

  .icon-container {
    margin-bottom: 10px;
  }

  .block-title {
    margin-bottom: 8px;
  }
}
</style>

