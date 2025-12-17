<template>
  <section class="product-display">
    <div class="container">
      <div class="display-content">
        <!-- 左侧视频区域 -->
        <div class="video-section">
          <div class="video-container">
            <video
              ref="productVideo"
              :src="videoData.url"
              :poster="videoData.poster"
              class="product-video"
              controls
              preload="metadata"
            >
              <source :src="videoData.url" type="video/mp4">
              您的浏览器不支持视频播放。
            </video>
            <div class="play-overlay" v-if="!videoData.url">
              <i class="fas fa-play"></i>
            </div>
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="content-section">
          <!-- 上部分：文字介绍 -->
          <div class="text-section">
            <div class="title-row">
              <div class="style-tag">Style Design</div>
              <h2 class="main-title">Product display</h2>
            </div>
            <p class="subtitle">High quality, good show</p>
          </div>

          <!-- 下部分：产品列表 -->
          <div class="product-list-container">
            <div class="product-list" ref="productList">
              <div
                v-for="(product, index) in products"
                :key="index"
                class="product-item"
                @click="goToProductDetail(product.id)"
              >
                <div class="product-image">
                  <img
                    :src="product.image"
                    :alt="product.name"
                    @error="handleImageError"
                  />
                </div>
                <div class="product-info">
                  <div class="product-name">{{ product.name }}</div>
                  <div class="product-description">{{ product.description }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { getProductVideos } from '../api/media'
import { getAllProducts } from '../api/products'

export default {
  name: 'ProductDisplaySection',
  data() {
    return {
      videoData: {
        url: '',
        poster: ''
      },
      products: []
    }
  },
  mounted() {
    this.fetchVideoData();
    this.fetchProducts();
    this.setupSmoothScroll();
  },
  methods: {
    // 获取视频数据 - 使用新的媒体API
    async fetchVideoData() {
      try {
        console.log('🔄 [ProductDisplaySection] 使用媒体API获取产品视频数据...');

        // 使用新的媒体API获取产品视频
        const response = await getProductVideos();
        console.log('✅ [ProductDisplaySection] 媒体API响应:', response);
        console.log('🔍 [ProductDisplaySection] API响应详情:', {
          response: response,
          hasData: !!response?.data,
          hasCode: !!response?.data?.code,
          code: response?.data?.code,
          hasFiles: !!response?.data?.data?.files,
          files: response?.data?.data?.files
        });

        // 检查响应格式并处理数据
        if (response && response.data && response.data.code === 200 && response.data.data && response.data.data.files) {
          const videoFiles = response.data.data.files;
          console.log('📹 [ProductDisplaySection] 找到视频文件数量:', videoFiles.length);

          if (videoFiles.length > 0) {
            // 使用第一个视频文件
            const firstVideo = videoFiles[0];
            this.videoData = {
              url: firstVideo.fileUrl,
              poster: '', // 可以后续添加缩略图支持
              title: firstVideo.description || '产品展示视频',
              description: firstVideo.description
            };

            console.log(`✅ [ProductDisplaySection] 成功获取产品视频:`, this.videoData);
            return;
          } else {
            console.log('⚠️ [ProductDisplaySection] 产品视频分类中没有找到视频文件');
          }
        } else {
          console.warn('⚠️ [ProductDisplaySection] 媒体API响应格式异常:', response);
        }

        // 媒体API无数据时直接使用默认数据
        console.log('🔄 [ProductDisplaySection] 媒体API无数据，使用默认视频数据...');

      } catch (error) {
        console.error('❌ [ProductDisplaySection] 获取产品视频数据失败:', error);
        console.error('🔍 错误详情:', {
          message: error.message,
          stack: error.stack
        });
      }

      // 最终使用默认视频数据
      console.log('📦 [ProductDisplaySection] 所有API都失败，使用本地默认视频数据');
      this.videoData = {
        url: 'src/images/vedio/Production video1.mp4',
        poster: '',
        title: '产品展示视频',
        description: '高质量牛仔服装产品展示'
      };

      console.log('🎬 [ProductDisplaySection] 设置默认视频数据:', this.videoData);
    },
    // 获取产品数据
    async fetchProducts() {
      try {
        console.log('🔄 [ProductDisplaySection] 开始加载产品数据...');

        // 使用公共API获取产品数据（无需认证）
        const response = await getAllProducts({ page: 1, size: 50 });
        console.log('✅ [ProductDisplaySection] API响应:', response);

        // 处理API响应格式
        if (response && response.data && response.data.data && response.data.data.content && Array.isArray(response.data.data.content)) {
          const apiProducts = response.data.data.content;
          console.log('✅ [ProductDisplaySection] 获取到API产品数据:', apiProducts.length, '个商品');

          // 筛选上架产品 (status: 1)
          const activeProducts = apiProducts.filter(product => product.status === 1);
          console.log('✅ [ProductDisplaySection] 筛选后上架产品:', activeProducts.length, '个商品');

          // 按创建时间排序（最新的在前）
          const sortedProducts = activeProducts.sort((a, b) => {
            const dateA = new Date(a.createdAt || a.updateTime || a.created_at || a.updated_at || 0);
            const dateB = new Date(b.createdAt || b.updateTime || b.created_at || b.updated_at || 0);
            return dateB - dateA;
          });

          // 取前10个最新产品
          const latestProducts = sortedProducts.slice(0, 10);

          this.products = latestProducts.map((product, index) => ({
            id: product.id || index + 1,
            name: product.name || product.productName || `Product ${index + 1}`,
            image: product.mainImage || product.images?.[0] || '/images/placeholder-400x400.svg',
            description: product.description || `High-quality product ${index + 1}`
          }));

          console.log('✅ [ProductDisplaySection] 产品数据处理完成:', this.products.length, '个产品');
        } else {
          console.warn('⚠️ [ProductDisplaySection] API响应格式异常或无数据，使用默认产品');
          this.products = this.getDefaultProducts();
        }
      } catch (error) {
        console.error('❌ [ProductDisplaySection] 获取产品数据失败:', error);
        this.products = this.getDefaultProducts();
      }
    },

    // 默认产品数据 - 使用导入的图片和描述信息
    getDefaultProducts() {
      return [
        { id: 1, name: 'Brown Ripped Jeans', image: product1, description: 'Stylish ripped jeans with comfortable fit and modern design' },
        { id: 2, name: 'Grey Cargo Jeans', image: product2, description: 'Functional cargo jeans with multiple pockets and durable fabric' },
        { id: 3, name: 'Dark Blue Ripped Skinny Jeans', image: product3, description: 'Fashionable skinny jeans with trendy ripped details' },
        { id: 4, name: 'Light Blue Skinny Jeans', image: product4, description: 'Classic light blue skinny jeans perfect for casual wear' },
        { id: 5, name: 'Blue Plaid Denim Jacket', image: product5, description: 'Stylish denim jacket with plaid pattern for unique look' },
        { id: 6, name: 'Dark Blue Denim Jacket', image: product6, description: 'Classic dark blue denim jacket with timeless appeal' },
        { id: 7, name: 'Distressed Blue Denim Jacket', image: product7, description: 'Edgy distressed denim jacket with vintage-inspired details' },
        { id: 8, name: 'Light Blue Denim Shorts', image: product8, description: 'Comfortable denim shorts ideal for summer activities' },
        { id: 9, name: 'Bleached Black Wide-Leg Jeans', image: product9, description: 'Trendy wide-leg jeans with unique bleached black finish' },
        { id: 10, name: 'Camouflage Wide-Leg Jeans', image: product10, description: 'Fashionable camo print wide-leg jeans for bold style' },
        { id: 11, name: 'Dark Grey Long-sleeve Top', image: product11, description: 'Elegant dark grey top perfect for layering and versatility' },
        { id: 12, name: 'Light Blue Denim Shirt Dress', image: product12, description: 'Chic denim shirt dress combining casual and elegant elements' },
        { id: 13, name: 'Dark Blue Denim Dress', image: product13, description: 'Sophisticated dark blue denim dress for various occasions' },
        { id: 14, name: 'Light Blue Denim Midi Dress', image: product14, description: 'Comfortable midi dress in light blue denim fabric' },
        { id: 15, name: 'Dark Blue Strapless Denim Mini-dress', image: product15, description: 'Trendy strapless mini dress in dark blue denim' },
        { id: 16, name: 'Light Blue Denim Jacket with Pearls', image: product16, description: 'Elegant denim jacket adorned with pearl details' },
        { id: 17, name: 'White Cropped Top with Denim Mini-skirt', image: product17, description: 'Stylish cropped top paired with matching denim skirt' },
        { id: 18, name: 'White Cropped Top with Denim Shorts', image: product18, description: 'Casual cropped top set with comfortable denim shorts' }
      ];
    },

    // 处理图片加载错误
    handleImageError(event) {
      event.target.src = '/images/placeholder-400x400.svg'; // 使用本地占位符图片
    },

    // 商品点击跳转到详情页面
    goToProductDetail(productId) {
      // 跳转到商品详情页面
      this.$router.push({
        name: 'ProductDetail',
        params: { id: productId }
      }).catch(err => {
        console.log('Navigation failed:', err)
        // 如果路由不存在，可以跳转到产品页面或显示提示
        this.$router.push('/products').catch(() => {
          console.log('Failed to navigate to products page')
        })
      })
    },

    // 设置平滑滚动
    setupSmoothScroll() {
      this.$nextTick(() => {
        const container = this.$refs.productList;
        if (container) {
          // 为产品列表容器添加平滑滚动
          container.style.scrollBehavior = 'smooth';

          // 添加鼠标滚轮事件监听，优化滚动体验
          container.addEventListener('wheel', (e) => {
            // 允许正常滚动，但可以在这里添加自定义逻辑
            if (e.deltaY !== 0) {
              // 垂直滚动
              container.scrollTop += e.deltaY * 0.8; // 调整滚动速度
              e.preventDefault();
            }
          }, { passive: false });
        }
      });
    }
  }
}
</script>

<style scoped>
.product-display {
  padding: 60px 0;
  background: #fff;
}

@media (max-width: 768px) {
  .product-display {
    padding: 16px 0 !important;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.display-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px; /* 减小间隔 */
  align-items: start; /* 从顶部开始对齐 */
  height: auto; /* 自动高度 */
}

/* 左侧视频区域 */
.video-section {
  position: relative;
}

.video-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.product-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: #f5f5f5;
}

.play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.play-overlay i {
  color: white;
  font-size: 24px;
  margin-left: 4px;
}

/* 右侧内容区域 */
.content-section {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 400px; /* 确保有最小高度 */
  padding-left: 10px; /* 减小左侧内边距 */
  min-width: 300px;
  gap: 20px; /* 使用gap代替margin，更好的空间控制 */
}

/* 上部分：文字介绍 */
.text-section {
  overflow: hidden;
  width: 100%;
  flex-shrink: 0; /* 防止文字区域被压缩 */
}

.title-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 15px;
  flex-wrap: nowrap;
  white-space: nowrap;
  min-width: 0;
}

.style-tag {
  display: inline-block;
  background: #fff3e0;
  border: 2px solid #ff9800;
  color: #666;
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  flex-shrink: 0;
  white-space: nowrap;
}

.main-title {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
  margin: 0;
  line-height: 1.2;
  white-space: nowrap;
}

.subtitle {
  font-size: 13px;
  color: #fff;
  margin: 0;
  line-height: 1.4;
}

/* 下部分：产品列表 */
.product-list-container {
  overflow-y: scroll; /* 垂直滚动，强制显示滚动条 */
  overflow-x: hidden; /* 隐藏水平滚动 */
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  height: 298px; /* 固定高度显示4个产品 (60px + 6px margin) * 4 + 16px padding */
  padding: 16px;
  scrollbar-width: thin; /* Firefox - 显示细滚动条 */
  scrollbar-gutter: stable; /* 为滚动条预留空间 */
}

.product-list {
  padding: 0;
  flex: 1; /* 让产品列表填充可用空间 */
}

.product-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.2s ease;
  min-height: 60px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 6px;
  margin-bottom: 6px;
  cursor: pointer;
}

.product-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(4px);
}

.product-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.product-image {
  width: 50px;
  height: 50px;
  margin-right: 12px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 3px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 3px;
  line-height: 1.3;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-description {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.3;
  font-weight: 400;
  display: -webkit-box;
  display: box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  box-orient: vertical;
  overflow: hidden;
}

/* 自定义滚动条 - 美化样式 */
.product-list-container::-webkit-scrollbar {
  width: 8px;
}

.product-list-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  margin: 0;
}

.product-list-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.4);
  border-radius: 4px;
  transition: all 0.2s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.product-list-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.6);
  border-color: rgba(255, 255, 255, 0.5);
}

.product-list-container::-webkit-scrollbar-thumb:active {
  background: rgba(255, 255, 255, 0.7);
}

/* 滚动条圆角优化 */
.product-list-container::-webkit-scrollbar-corner {
  background: transparent;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .display-content {
    grid-template-columns: 1.5fr 1fr;
    gap: 30px;
  }

  .main-title {
    font-size: 26px;
    white-space: nowrap;
  }

  .title-row {
    gap: 10px;
  }

  .content-section {
    padding-left: 15px;
  }
}

@media (max-width: 768px) {
  .product-display {
    padding: 15px 0;
  }

  .container {
    padding: 0 12px;
  }

  .display-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .video-container {
    aspect-ratio: 16/10;
  }

  .main-title {
    font-size: 22px;
    white-space: nowrap;
  }

  .title-row {
    align-items: baseline;
    gap: 8px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }

  .content-section {
    min-width: 250px;
  }

  .content-section {
    padding-left: 0;
    gap: 15px;
  }

  .product-list-container {
    min-height: 250px; /* 移动端设置最小高度 */
  }

  .product-item {
    padding: 10px 12px;
    min-height: 55px;
  }

  .product-image {
    width: 45px;
    height: 45px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 15px;
  }
  
  .main-title {
    font-size: 20px;
    white-space: nowrap;
  }
  
  .title-row {
    gap: 6px;
    flex-wrap: nowrap;
    white-space: nowrap;
  }

  .content-section {
    min-width: 200px;
    gap: 12px;
  }

  .product-list-container {
    min-height: 200px; /* 小屏幕设置最小高度 */
  }

  .product-item {
    padding: 8px 10px;
    min-height: 50px;
  }

  .product-image {
    width: 40px;
    height: 40px;
    margin-right: 10px;
  }

  .product-name {
    font-size: 14px;
  }

  .product-description {
    font-size: 12px;
  }
  
  }
</style>
