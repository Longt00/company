<template>
  <div class="carousel-container">
    <div class="carousel">
      <!-- 轮播轨道 -->
      <div 
        class="carousel-track" 
        :style="{ 
          transform: `translateX(-${currentSlide * 100}%)`,
          '--slide-count': slides.length 
        }"
      >
        <div
          v-for="(slide, index) in slides"
          :key="index"
          class="carousel-slide"
        >
          <img 
            :src="slide.image" 
            :alt="slide.alt || `轮播图${index + 1}`" 
            class="slide-image"
            loading="lazy" 
          >
        </div>
      </div>

      <!-- 指示器 -->
      <div class="carousel-indicators" v-if="slides.length > 1">
        <button
          v-for="(_, index) in slides"
          :key="index"
          :class="['indicator', { active: currentSlide === index }]"
          @click="goToSlide(index)"
          :aria-label="`切换到第${index + 1}张轮播图`"
        ></button>
      </div>
    </div>

    <!-- 左右导航按钮 -->
    <button 
      class="carousel-nav-left" 
      @click="prevSlide"
      v-if="slides.length > 1"
      :aria-label="`上一张轮播图`"
    >
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
        <path d="M15 18l-6-6 6-6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
    <button 
      class="carousel-nav-right" 
      @click="nextSlide"
      v-if="slides.length > 1"
      :aria-label="`下一张轮播图`"
    >
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
        <path d="M9 18l6-6-6-6" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
  </div>
</template>

<script>
export default {
  name: 'ImageCarousel',
  data() {
    return {
      currentSlide: 0,
      autoplayInterval: null,
      isPaused: false,
      touchStartX: 0 // 初始化触摸坐标
    }
  },
  props: {
    slides: {
      type: Array,
      required: true,
      validator: (val) => val.every(item => item && typeof item.image === 'string')
    },
    autoplay: {
      type: Boolean,
      default: true
    },
    interval: {
      type: Number,
      default: 5000,
      validator: (val) => val >= 2000
    }
  },
  mounted() {
    this.$nextTick(() => {
      if (this.autoplay && this.slides.length > 1) {
        this.startAutoplay();
      }
      this.addEventListeners();
    });
  },
  beforeUnmount() {
    this.stopAutoplay();
    this.removeEventListeners();
  },
  methods: {
    nextSlide() {
      if (this.slides.length <= 1) return;
      this.currentSlide = (this.currentSlide + 1) % this.slides.length;
    },
    prevSlide() {
      if (this.slides.length <= 1) return;
      this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
    },
    goToSlide(index) {
      if (index < 0 || index >= this.slides.length) return;
      this.currentSlide = index;
      if (this.autoplay) {
        this.stopAutoplay();
        this.startAutoplay();
      }
    },
    startAutoplay() {
      this.stopAutoplay();
      this.autoplayInterval = setInterval(() => this.nextSlide(), this.interval);
    },
    stopAutoplay() {
      if (this.autoplayInterval) clearInterval(this.autoplayInterval);
    },
    pauseAutoplay() {
      if (this.autoplay) {
        this.stopAutoplay();
        this.isPaused = true;
      }
    },
    resumeAutoplay() {
      if (this.autoplay && this.isPaused && this.slides.length > 1) {
        this.startAutoplay();
        this.isPaused = false;
      }
    },
    addEventListeners() {
      const container = this.$el;
      container.addEventListener('mouseenter', this.pauseAutoplay);
      container.addEventListener('mouseleave', this.resumeAutoplay);
      container.addEventListener('touchstart', this.handleTouchStart);
      container.addEventListener('touchend', this.handleTouchEnd);
    },
    removeEventListeners() {
      const container = this.$el;
      container.removeEventListener('mouseenter', this.pauseAutoplay);
      container.removeEventListener('mouseleave', this.resumeAutoplay);
      container.removeEventListener('touchstart', this.handleTouchStart);
      container.removeEventListener('touchend', this.handleTouchEnd);
    },
    handleTouchStart(e) {
      this.touchStartX = e.touches[0].clientX;
    },
    handleTouchEnd(e) {
      if (!this.touchStartX) return;
      const touchEndX = e.changedTouches[0].clientX;
      const diff = this.touchStartX - touchEndX;
      if (diff > 30) this.nextSlide();
      else if (diff < -30) this.prevSlide();
      this.touchStartX = null;
    }
  }
}
</script>

<style scoped>
/* 核心修改：给轮播容器设置固定高度 + 最大宽度 */
 .carousel-container {
  position: relative;
  width: 100%;
  /* 让容器宽度跟随页面，避免左右留空；高度由图片自然撑开 */
  max-width: 100%;
  margin: 0;
  height: auto;
  overflow: hidden;
  background: white;
  border-radius: 0;
  box-sizing: border-box;
}

.carousel {
  position: relative;
  width: 100%;
  /* 高度由内部图片自然撑开 */
  height: auto;
  overflow: hidden;
  box-sizing: border-box;
}

/* 轮播轨道 */
.carousel-track {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease-in-out;
  /* 关键：轨道宽度与容器一致，让 translateX(-currentSlide * 100%) 每次刚好移动一屏 */
  width: 100%;
  will-change: transform;
  box-sizing: border-box;
}

/* 轮播项 */
.carousel-slide {
  flex: 0 0 100%;
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

/* 图片样式：宽度铺满容器，高度自适应，避免左右空白 */
.slide-image {
  width: 100%;
  height: auto;
  display: block;
  object-fit: cover;
  line-height: 0;
}

/* 导航按钮 */
.carousel-nav-left, .carousel-nav-right {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.6);
  border: none;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  pointer-events: auto;
}

.carousel-nav-left { left: 20px; }
.carousel-nav-right { right: 20px; }
.carousel-nav-left:hover, .carousel-nav-right:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: translateY(-50%) scale(1.1);
}

/* 指示器 */
.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 3;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s;
  flex-shrink: 0;
  padding: 0;
  margin: 0;
  min-width: 0;
  min-height: 0;
  box-sizing: border-box;
}

.indicator.active {
  background: white;
  transform: scale(1.2);
}
.indicator:hover { background: rgba(255,255,255,0.6); }

/* 响应式调整：仅缩放控件尺寸，不再固定高度，保持图片自适应 */
@media (max-width: 1200px) {
  .carousel-container {
    max-width: 100%;
    height: auto;
  }
}

@media (max-width: 768px) {
  .carousel-nav-left, .carousel-nav-right {
    width: 36px;
    height: 36px;
  }
  .carousel-nav-left svg, .carousel-nav-right svg {
    width: 18px;
    height: 18px;
  }
}

@media (max-width: 480px) {
  .carousel-nav-left, .carousel-nav-right {
    width: 32px;
    height: 32px;
  }
  .carousel-nav-left svg, .carousel-nav-right svg {
    width: 16px;
    height: 16px;
  }
}

:deep(.carousel-container *) {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>