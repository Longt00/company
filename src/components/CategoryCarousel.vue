<template>
  <div class="category-carousel-container">
    <!-- 轮播组件 -->
    <div v-if="slides.length > 0" class="carousel-wrapper">
      <div class="carousel">
        <!-- 轮播轨道 -->
        <div class="carousel-track" :style="trackStyle">
          <div
            v-for="(slide, index) in slides"
            :key="index"
            class="carousel-slide"
          >
            <!-- 视频展示 -->
            <video
              v-if="isVideo(slide.fileUrl)"
              :src="slide.fileUrl"
              class="slide-video"
              controls
              controlslist="nodownload"
              @error="handleMediaError"
            >
              您的浏览器不支持视频播放
            </video>
            <!-- 图片展示 -->
            <img
              v-else
              :src="slide.fileUrl"
              :alt="slide.description || `${categoryName}图片 ${index + 1}`"
              class="slide-image"
              @error="handleMediaError"
            >
          </div>
        </div>

        <!-- 左右箭头 - 白色圆形按钮 -->
        <button
          class="carousel-btn carousel-btn-prev"
          :class="{ disabled: slides.length <= 1 }"
          @click="prevSlide"
          :disabled="slides.length <= 1"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button
          class="carousel-btn carousel-btn-next"
          :class="{ disabled: slides.length <= 1 }"
          @click="nextSlide"
          :disabled="slides.length <= 1"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M9 18l6-6-6-6" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <!-- 指示器 -->
        <div v-if="slides.length > 1" class="carousel-indicators">
          <button
            v-for="(_, index) in slides"
            :key="index"
            :class="['indicator', { active: currentSlide === index }]"
            @click="goToSlide(index)"
          ></button>
        </div>
      </div>
    </div>

    <!-- 无图片提示 -->
    <div v-else class="no-images-placeholder">
      <div class="placeholder-content">
        <svg width="48" height="48" viewBox="0 0 48 48" fill="none" class="placeholder-icon">
          <rect x="6" y="6" width="36" height="28" rx="2" stroke="currentColor" stroke-width="2"/>
          <circle cx="14" cy="14" r="2" fill="currentColor"/>
          <path d="M6 26l8-8 4 4 10-10 8 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <p class="placeholder-text">暂无{{ categoryName }}图片</p>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script>
import { getImagesByCategory } from '../api/media'

export default {
  name: 'CategoryCarousel',
  data() {
    return {
      currentSlide: 0,
      autoplayInterval: null,
      isPaused: false,
      loading: false,
      slides: []
    }
  },
  props: {
    category: {
      type: String,
      required: true
    },
    categoryName: {
      type: String,
      required: true
    },
    autoplay: {
      type: Boolean,
      default: true
    },
    interval: {
      type: Number,
      default: 4000
    }
  },
  computed: {
    trackStyle() {
      return {
        transform: `translateX(-${this.currentSlide * 100}%)`
      }
    }
  },
  watch: {
    category: {
      immediate: true,
      handler(newCategory) {
        if (newCategory) {
          this.loadCategoryImages()
        }
      }
    }
  },
  mounted() {
    if (this.autoplay && this.slides.length > 1) {
      this.startAutoplay()
    }
    // 鼠标悬停时暂停自动轮播
    this.$el.addEventListener('mouseenter', this.pauseAutoplay)
    this.$el.addEventListener('mouseleave', this.resumeAutoplay)
  },
  beforeDestroy() {
    this.stopAutoplay()
    this.$el.removeEventListener('mouseenter', this.pauseAutoplay)
    this.$el.removeEventListener('mouseleave', this.resumeAutoplay)
  },
  methods: {
    // 加载分类图片
    async loadCategoryImages() {
      if (!this.category) return

      this.loading = true
      try {
        console.log(`🔄 [CategoryCarousel] 正在加载 ${this.category} 分类图片...`)

        const response = await getImagesByCategory(this.category)
        console.log(`✅ [CategoryCarousel] ${this.category} 分类图片响应:`, response)

        if (response && response.data && response.data.data && response.data.data.files) {
          this.slides = response.data.data.files
          console.log(`✅ [CategoryCarousel] 成功加载 ${this.slides.length} 张 ${this.category} 图片`)
        } else {
          console.warn(`⚠️ [CategoryCarousel] ${this.category} 分类没有找到图片`)
          this.slides = []
        }
      } catch (error) {
        console.error(`❌ [CategoryCarousel] 加载 ${this.category} 分类图片失败:`, error)
        this.slides = []
      } finally {
        this.loading = false
        this.resetAutoplay()
      }
    },

    // 重置自动轮播
    resetAutoplay() {
      this.stopAutoplay()
      this.currentSlide = 0
      if (this.autoplay && this.slides.length > 1) {
        this.startAutoplay()
      }
    },

    // 轮播控制方法
    nextSlide() {
      if (this.slides.length > 1) {
        this.currentSlide = (this.currentSlide + 1) % this.slides.length
      }
    },
    prevSlide() {
      if (this.slides.length > 1) {
        this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length
      }
    },
    goToSlide(index) {
      this.currentSlide = index
    },
    startAutoplay() {
      this.autoplayInterval = setInterval(() => {
        this.nextSlide()
      }, this.interval)
    },
    stopAutoplay() {
      if (this.autoplayInterval) {
        clearInterval(this.autoplayInterval)
        this.autoplayInterval = null
      }
    },
    pauseAutoplay() {
      this.stopAutoplay()
      this.isPaused = true
    },
    resumeAutoplay() {
      if (this.autoplay && this.isPaused && this.slides.length > 1) {
        this.startAutoplay()
        this.isPaused = false
      }
    },
    // 判断是否为视频文件
    isVideo(url) {
      if (!url) return false
      const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov', '.avi', '.wmv']
      return videoExtensions.some(ext => url.toLowerCase().endsWith(ext))
    },
    handleMediaError(event) {
      const mediaType = event.target.tagName === 'VIDEO' ? '视频' : '图片'
      console.warn(`⚠️ [CategoryCarousel] ${mediaType}加载失败:`, event.target.src)
      // 可以在这里设置一个默认图片
    }
  }
}
</script>

<style scoped>
/* 轮播容器 */
.category-carousel-container {
  position: relative;
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.carousel-wrapper {
  width: 100%;
  height: 100%;
}

.carousel {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

/* 轮播轨道 */
.carousel-track {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease-in-out;
}

/* 轮播项 */
.carousel-slide {
  flex: 0 0 100%;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
}

.slide-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.slide-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

/* 轮播控制按钮 - 白色圆形按钮带边框 */
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.carousel-btn:hover:not(:disabled) {
  background: #f5f5f5;
  border-color: #ccc;
  transform: translateY(-50%) scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.carousel-btn:disabled,
.carousel-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.carousel-btn-prev {
  left: 16px;
}

.carousel-btn-next {
  right: 16px;
}

/* 指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 6px;
  z-index: 3;
}

.indicator {
  /* 重置 button 默认样式 */
  padding: 0;
  margin: 0;
  min-width: 0;
  min-height: 0;
  box-sizing: border-box;
  
  /* 设置圆形指示器 */
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: 2px solid white;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s;
  
  /* 确保在移动端也是正圆 */
  flex-shrink: 0;
}

.indicator.active {
  background: white;
  transform: scale(1.2);
}

.indicator:hover {
  background: rgba(255,255,255,0.6);
}

/* 无图片占位符 */
.no-images-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
}

.placeholder-content {
  text-align: center;
  color: #6c757d;
}

.placeholder-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  opacity: 0.5;
}

.placeholder-text {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

/* 加载覆盖层 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255,255,255,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #ff6a00;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel-btn {
    width: 32px;
    height: 32px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.15);
  }

  .carousel-btn svg {
    width: 18px;
    height: 18px;
  }

  .carousel-btn-prev {
    left: 6px;
  }

  .carousel-btn-next {
    right: 6px;
  }

  .carousel-indicators {
    bottom: 8px;
    gap: 4px;
  }

  .indicator {
    width: 6px;
    height: 6px;
    border-width: 1.5px;
  }

  .placeholder-text {
    font-size: 14px;
  }

  .carousel-slide {
    padding: 4px;
  }
}

@media (max-width: 480px) {
  .carousel-btn {
    width: 28px;
    height: 28px;
  }

  .carousel-btn svg {
    width: 16px;
    height: 16px;
  }

  .carousel-btn-prev {
    left: 4px;
  }

  .carousel-btn-next {
    right: 4px;
  }

  .carousel-indicators {
    bottom: 6px;
    gap: 3px;
  }

  .indicator {
    width: 5px;
    height: 5px;
    border-width: 1.5px;
  }

  .placeholder-icon {
    width: 32px;
    height: 32px;
    margin: 0 auto 10px;
  }

  .placeholder-text {
    font-size: 12px;
  }
}
</style>