<template>
  <section class="service-carousel">
    <div class="container">
      <div class="carousel-wrapper">
        
        <!-- 轮播内容 -->
        <div class="carousel-container">
          <button 
            class="nav-btn nav-left" 
            @click="previousSlide"
            :disabled="currentSlide === 0"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          
          <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * cardWidth}%)` }">
            <div 
              v-for="(service, index) in services" 
              :key="index"
              class="service-card"
            >
              <div class="service-icon">
                <i :class="service.icon"></i>
              </div>
              <h3 class="service-title">{{ service.title }}</h3>
              <p class="service-description">{{ service.description }}</p>
            </div>
          </div>
          
          <button 
            class="nav-btn nav-right" 
            @click="nextSlide"
            :disabled="currentSlide >= maxSlide"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'ServiceCarousel',
  data() {
    return {
      currentSlide: 0,
      isMobile: false,
      services: [
        {
          icon: 'fas fa-certificate',
          title: 'Full customization',
          description: 'The data is from past contract of the latest inspection report as assessed by independent third parties.'
        },
        {
          icon: 'fas fa-users',
          title: 'Total trading staff (3)',
          description: 'The data is from onsite check of the latest inspection report assessed by independent third parties'
        },
        {
          icon: 'fas fa-tools',
          title: 'Testing instruments (2)',
          description: '5+ certified engineers, each with 5+ years expertise'
        },
        {
          icon: 'fas fa-box',
          title: 'ODM services available',
          description: 'The data is from past contract of the latest inspection report as assessed by independent third parties.'
        },
        {
          icon: 'fas fa-check-circle',
          title: 'Quality assurance',
          description: 'Comprehensive quality control processes ensuring highest standards'
        },
        {
          icon: 'fas fa-shipping-fast',
          title: 'Fast delivery',
          description: 'Efficient logistics network for timely product delivery worldwide'
        }
      ]
    }
  },
  computed: {
    // 根据屏幕宽度计算每次显示的卡片数
    visibleCards() {
      return this.isMobile ? 2 : 4
    },
    // 每张卡片占用的百分比
    cardWidth() {
      return this.isMobile ? 50 : 25
    },
    maxSlide() {
      return Math.max(0, this.services.length - this.visibleCards)
    }
  },
  mounted() {
    this.checkMobile()
    window.addEventListener('resize', this.checkMobile)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkMobile)
  },
  methods: {
    checkMobile() {
      this.isMobile = window.innerWidth <= 768
      // 切换时重置到有效范围
      if (this.currentSlide > this.maxSlide) {
        this.currentSlide = this.maxSlide
      }
    },
    nextSlide() {
      if (this.currentSlide < this.maxSlide) {
        this.currentSlide++
      }
    },
    previousSlide() {
      if (this.currentSlide > 0) {
        this.currentSlide--
      }
    }
  }
}
</script>

<style scoped>
.service-carousel {
  padding: 60px 0;
  background: #f8f9fa;
}

@media (max-width: 768px) {
  .service-carousel {
    padding: 16px 0;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.carousel-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 40px;
  position: relative;
}

.verification-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  background: #007bff;
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
}

.verification-badge i.fa-check {
  font-size: 12px;
}

.verified-text {
  font-weight: bold;
}

.sgs-text {
  font-size: 12px;
  opacity: 0.9;
}

.info-icon {
  font-size: 12px;
  opacity: 0.8;
}

.carousel-container {
  position: relative;
  overflow: hidden;
  margin-top: 20px;
  padding: 0 50px;
}

.carousel-track {
  display: flex;
  transition: transform 0.3s ease;
  gap: 0;
}

.service-card {
  flex: 0 0 25%;
  padding: 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 200px;
}

.service-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  position: relative;
}

.service-icon::before {
  content: '';
  position: absolute;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 50%;
  z-index: -1;
  opacity: 0.3;
}

.service-icon i {
  font-size: 24px;
  color: #0d47a1;
  font-weight: bold;
}

.service-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.3;
}

.service-description {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  margin: 0;
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: #f5f5f5;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
}

.nav-btn:hover:not(:disabled) {
  background: #e0e0e0;
  transform: translateY(-50%) scale(1.1);
}

.nav-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.nav-left {
  left: 10px;
}

.nav-right {
  right: 10px;
}

.nav-btn i {
  font-size: 16px;
  color: #333;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .service-card {
    flex: 0 0 33.333%;
  }
  .carousel-track {
    transform: translateX(-33.333%);
  }
}
@media (max-width: 768px) {
  .carousel-wrapper {
    padding: 12px 8px;
    border-radius: 8px;
  }
  
  .carousel-container {
    padding: 0 28px;
    margin-top: 8px;
  }
  
  /* 移动端显示2个卡片 */
  .service-card {
    flex: 0 0 50%;
    padding: 8px 6px;
    min-height: auto;
  }

  .service-icon {
    width: 36px;
    height: 36px;
    margin-bottom: 8px;
  }

  .service-icon::before {
    width: 48px;
    height: 48px;
  }

  .service-icon i {
    font-size: 14px;
  }

  .service-title {
    font-size: 12px;
    margin-bottom: 4px;
  }

  .service-description {
    font-size: 10px;
    line-height: 1.3;
  }
  
  .nav-btn {
    width: 28px;
    height: 28px;
  }

  .nav-left {
    left: 2px;
  }

  .nav-right {
    right: 2px;
  }
  
  .verification-badge {
    position: static;
    margin-bottom: 8px;
    align-self: flex-end;
    padding: 4px 10px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .carousel-wrapper {
    padding: 10px 6px;
  }

  .carousel-container {
    padding: 0 24px;
  }
  
  /* 480px仍然显示2个卡片 */
  .service-card {
    flex: 0 0 50%;
    padding: 6px 4px;
  }

  .service-icon {
    width: 32px;
    height: 32px;
    margin-bottom: 6px;
  }

  .service-icon::before {
    width: 42px;
    height: 42px;
  }

  .service-icon i {
    font-size: 12px;
  }

  .service-title {
    font-size: 11px;
    margin-bottom: 3px;
  }

  .service-description {
    font-size: 9px;
    line-height: 1.2;
  }
  
  .nav-btn {
    width: 24px;
    height: 24px;
  }
  
  .nav-left {
    left: 0;
  }
  
  .nav-right {
    right: 0;
  }
}
</style>
