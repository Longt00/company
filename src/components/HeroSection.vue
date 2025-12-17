<template>
  <section class="hero">
    <!-- 轮播组件 -->
    <ImageCarousel
      :slides="carouselSlides"
      :autoplay="true"
      :interval="5000"
      @view-products="handleViewProducts"
      @contact-us="handleContactUs"
    />

    </section>
</template>

<script>
import ImageCarousel from './ImageCarousel.vue'
import { getHomeCarouselImages } from '../api/media'

export default {
  name: 'HeroSection',
  components: {
    ImageCarousel
  },
  data() {
    return {
      carouselSlides: [] // 存储从后端获取的轮播图数据
    }
  },
  mounted() {
    console.log('🚀 [HeroSection] 组件已挂载，开始获取轮播图数据...');
    this.fetchCarouselSlides(); // 组件挂载后获取轮播图数据
  },
  methods: {
    // 获取轮播图数据 - 使用新的媒体API
    async fetchCarouselSlides() {
      try {
        console.log('🔄 [HeroSection] 使用媒体API获取首页轮播图数据...');

        // 使用新的媒体API获取首页轮播图
        const response = await getHomeCarouselImages();
        console.log('✅ [HeroSection] 媒体API响应:', response);

        // 检查响应格式并处理数据
        if (response && response.data && response.data.code === 200 && response.data.data && response.data.data.files) {
          const mediaFiles = response.data.data.files;

          if (mediaFiles.length > 0) {
            // 将媒体文件转换为轮播图格式
            this.carouselSlides = mediaFiles.map((file, index) => ({
              image: file.fileUrl,
              alt: file.description || `首页轮播图 ${index + 1}`,
              title: file.description || `专业牛仔服装展示 ${index + 1}`,
              subtitle: file.description || '我们与多家工厂合作，确保货物直接从源头交付，为您提供高质量的牛仔服装产品'
            }));

            console.log(`✅ [HeroSection] 成功获取 ${mediaFiles.length} 张轮播图`);
            console.log('🖼️ [HeroSection] 轮播图数据:', this.carouselSlides);
            return;
          } else {
            console.log('⚠️ [HeroSection] 首页轮播图分类中没有找到图片文件');
            this.carouselSlides = []; // 清空轮播图
          }
        } else {
          console.warn('⚠️ [HeroSection] 媒体API响应格式异常:', response);
          this.carouselSlides = []; // 清空轮播图
        }

      } catch (error) {
        console.error('❌ [HeroSection] 获取轮播图数据失败:', error);
        console.error('🔍 错误详情:', {
          message: error.message,
          stack: error.stack
        });
        this.carouselSlides = []; // 清空轮播图
      }
    },

    // 处理查看产品事件
    handleViewProducts() {
      // 处理查看产品事件
      this.$emit('view-products');
    },

    // 处理联系我们事件
    handleContactUs() {
      // 处理联系我们事件
      this.$emit('contact-us');
    }
  }
}
</script>

<style scoped>
.hero {
  padding: 0; /* 轮播组件已经有自己的样式 */
  position: relative;
}

</style>





