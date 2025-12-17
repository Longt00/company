<template>
  <section class="company-profile">
    <div class="container">
      <div class="profile-content">
        <!-- 左侧文字内容 -->
        <div class="profile-text">
          <h2 class="section-title">| COMPANY PROFILE</h2>
          <p class="profile-description" v-html="profileContent.replace(/\n/g, '<br>')" style="white-space: pre-wrap;"></p>
        </div>

        <!-- 右侧轮播图 -->
        <div class="carousel-container">
          <div class="carousel-wrapper">

            <div class="carousel-slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
              <div
                v-for="(image, index) in carouselImages"
                :key="index"
                class="carousel-slide"
              >
                <img :src="image.url" :alt="image.alt" class="carousel-image">
              </div>
            </div>

            <!-- 轮播图导航箭头 -->
            <button
              class="carousel-nav carousel-prev"
              @click="prevSlide"
              :disabled="currentSlide === 0"
            >
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M12.5 15L7.5 10L12.5 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button
              class="carousel-nav carousel-next"
              @click="nextSlide"
              :disabled="currentSlide === carouselImages.length - 1"
            >
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M7.5 5L12.5 10L7.5 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 下方两张图片 - 只有有图片时才显示 -->
      <div class="bottom-images" v-if="hasValidBottomImages">
        <div
          v-for="(image, index) in bottomImages"
          :key="index"
          class="bottom-image-container"
          v-show="image.url"
        >
          <img
            :src="image.url"
            :alt="image.alt"
            class="bottom-image"
            @error="onImageError(index)"
            @load="onImageLoad(index)"
          >
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { getHomeProfile } from '../api/richContent'

export default {
  name: 'CompanyProfile',
  data() {
    return {
      currentSlide: 0,
      carouselImages: [], // 轮播图图片数据
      bottomImages: [], // 下方两张图片数据
      // 不设置默认内容，强制从API获取
      profileContent: ''
    }
  },
  computed: {
    // 检查是否有有效的底部图片
    hasValidBottomImages() {
      return this.bottomImages && this.bottomImages.length > 0 && this.bottomImages.some(img => img && img.url)
    }
  },
  mounted() {
    this.fetchCarouselImages(); // 获取轮播图数据
    this.fetchBottomImages(); // 获取下方图片数据
    this.fetchProfileContent(); // 获取公司简介内容
  },
  methods: {
    

    // 获取轮播图图片数据 - 使用about-carousel分类
    async fetchCarouselImages() {
      try {
        console.log('🔄 [CompanyProfile] 使用媒体API获取about-carousel轮播图数据...');
        
        // 使用媒体API获取about-carousel分类的图片
        const { getMediaFiles } = await import('../api/media');
        const response = await getMediaFiles('about-carousel');
        console.log('✅ [CompanyProfile] 媒体API响应:', response);

        // 检查响应格式并处理数据
        if (response && response.data && response.data.code === 200 && response.data.data && response.data.data.files) {
          const carouselFiles = response.data.data.files;

          if (carouselFiles.length > 0) {
            // 将媒体文件转换为轮播图格式
            this.carouselImages = carouselFiles.map((file, index) => ({
              url: file.fileUrl,
              alt: file.description || `关于我们轮播图 ${index + 1}`
            }));

            console.log(`✅ [CompanyProfile] 成功获取 ${carouselFiles.length} 张about-carousel轮播图`);
            console.log('🖼️ [CompanyProfile] 轮播图数据:', this.carouselImages);
            return;
          } else {
            console.log('⚠️ [CompanyProfile] about-carousel分类中没有找到图片文件');
            this.carouselImages = []; // 清空轮播图
          }
        } else {
          console.warn('⚠️ [CompanyProfile] 媒体API响应格式异常:', response);
          this.carouselImages = []; // 清空轮播图
        }
      } catch (error) {
        console.error('❌ [CompanyProfile] 获取about-carousel轮播图数据失败:', error);
        this.carouselImages = []; // 清空轮播图
      }
    },

    /**
     * 获取公司简介文本内容
     */
    async fetchProfileContent() {
      try {
        console.log('🔄 [CompanyProfile] 获取公司简介内容...')
        const response = await getHomeProfile()
        
        if (response && response.code === 200 && response.data && response.data.length > 0) {
          this.profileContent = response.data[0].content
          console.log('✅ [CompanyProfile] 成功加载公司简介内容')
        } else {
          console.warn('⚠️ [CompanyProfile] API响应格式异常，使用默认内容')
        }
      } catch (error) {
        console.warn('⚠️ [CompanyProfile] API连接失败，使用默认内容:', error.message)
        // 保持使用默认值，不做任何修改
      }
    },

    // 获取下方图片数据 - 使用about-bottom-fixed分类
    async fetchBottomImages() {
      try {
        console.log('🔄 [CompanyProfile] 使用媒体API获取about-bottom-fixed底部图片数据...');
        
        // 使用媒体API获取about-bottom-fixed分类的图片
        const { getMediaFiles } = await import('../api/media');
        const response = await getMediaFiles('about-bottom-fixed');
        console.log('✅ [CompanyProfile] 媒体API响应:', response);

        // 检查响应格式并处理数据
        if (response && response.data && response.data.code === 200 && response.data.data && response.data.data.files) {
          const bottomFiles = response.data.data.files;

          if (bottomFiles.length > 0) {
            // 将媒体文件转换为底部图片格式，只取前2张
            this.bottomImages = bottomFiles.slice(0, 2).map((file, index) => ({
              url: file.fileUrl,
              alt: file.description || `关于我们底部图片 ${index + 1}`,
              title: file.description ? file.description.split('，')[0] : `公司图片 ${index + 1}`,
              description: file.description || `公司简介底部展示图片 ${index + 1}`
            }));

            console.log(`✅ [CompanyProfile] 成功获取 ${bottomFiles.length} 张about-bottom-fixed底部图片`);
            console.log('🖼️ [CompanyProfile] 底部图片数据:', this.bottomImages);
            return;
          } else {
            console.log('⚠️ [CompanyProfile] about-bottom-fixed分类中没有找到图片文件');
            this.bottomImages = []; // 清空底部图片
          }
        } else {
          console.warn('⚠️ [CompanyProfile] 媒体API响应格式异常:', response);
          this.bottomImages = []; // 清空底部图片
        }
      } catch (error) {
        console.error('❌ [CompanyProfile] 获取about-bottom-fixed底部图片数据失败:', error);
        this.bottomImages = []; // 清空底部图片
      }
    },

    

    // 轮播图上一张
    prevSlide() {
      if (this.currentSlide > 0) {
        this.currentSlide--;
      }
    },

    // 轮播图下一张
    nextSlide() {
      if (this.currentSlide < this.carouselImages.length - 1) {
        this.currentSlide++;
      }
    },


    // 图片加载成功处理
    onImageLoad(index) {
      console.log(`图片 ${index} 加载成功:`, this.bottomImages[index].url);
    },

    // 图片加载错误处理
    onImageError(index) {
      console.error(`底部图片 ${index} 加载失败，移除该图片`);
      // 移除加载失败的图片，设置为空
      this.$set(this.bottomImages, index, {
        ...this.bottomImages[index],
        url: null
      });
    }
  }
}
</script>

<style scoped>
.company-profile {
  padding: 40px 0;
  /* 背景由全局 body 控制，从接口动态加载 */
  background: transparent;
}

@media (max-width: 768px) {
  .company-profile {
    padding: 16px 0 !important;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 0px;
}

.profile-content {
  display: flex;
  align-items: flex-start;
}

/* 左侧文字内容样式 */
.profile-text {
  flex: 1;
  max-width: 50%;
}

.section-title {
  font-size: 32px;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 30px;
  text-align: left;
}

.profile-description {
  font-size: 16px;
  line-height: 1.6;
  color: #ffffff;
  margin-bottom: 20px;
  text-align: left;
}

/* 右侧轮播图样式 */
.carousel-container {
  flex: 1;
  max-width: 50%;
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  height: 400px;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  background-image: url('../images/background/background-vedio.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.carousel-slides {
  display: flex;
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease-in-out;
}

.carousel-slide {
  min-width: 100%;
  height: 100%;
}

.carousel-image {
  width: 80%;
  height: 80%;
  object-fit: cover;
  margin: 10% auto;
  display: block;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.carousel-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.9);
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

.carousel-nav:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-50%) scale(1.1);
}

.carousel-nav:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.carousel-nav:disabled:hover {
  transform: translateY(-50%);
}

.carousel-prev {
  left: 15px;
}

.carousel-next {
  right: 15px;
}

/* 下方图片样式 */
.bottom-images {
  display: flex;
  gap: 30px;
  margin-top: 40px;
}

.bottom-image-container {
  flex: 1;
  position: relative;
  /* height: 350px; */
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.bottom-image-container:hover {
  transform: translateY(-5px);
}

.bottom-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}


/* 图片占位符样式 */
.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 2px dashed #dee2e6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
  transition: all 0.3s ease;
}

.image-placeholder:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  border-color: #adb5bd;
}

.placeholder-icon {
  color: #6c757d;
  margin-bottom: 15px;
  opacity: 0.7;
}

.placeholder-text h3 {
  font-size: 18px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
}

.placeholder-text p {
  font-size: 14px;
  color: #6c757d;
  line-height: 1.4;
}




/* 响应式设计 */
@media (max-width: 1024px) {
  .profile-content {
    flex-direction: column;
    gap: 40px;
  }

  .profile-text,
  .carousel-container {
    max-width: 100%;
  }

  .carousel-wrapper {
    height: 350px;
  }
}

@media (max-width: 768px) {
  .company-profile {
    padding: 15px 0;
  }

  .container {
    padding: 0 12px;
  }

  .profile-content {
    gap: 16px;
    margin-bottom: 12px;
  }

  .section-title {
    font-size: 22px;
    margin-bottom: 16px;
  }

  .profile-description {
    font-size: 14px;
    margin-bottom: 12px;
  }

  .carousel-wrapper {
    height: 260px;
  }

  .bottom-images {
    flex-direction: column;
    gap: 12px;
    margin-top: 16px;
  }

  /* .bottom-image-container {
    height: 120px;
  } */
}

@media (max-width: 480px) {
  .container {
    padding: 0 15px;
  }

  .section-title {
    font-size: 24px;
  }

  .profile-description {
    font-size: 14px;
  }

  .carousel-wrapper {
    height: 250px;
  }

  .carousel-nav {
    width: 35px;
    height: 35px;
  }
/* 
  .bottom-image-container {
    height: 100px;
  } */
}
</style>