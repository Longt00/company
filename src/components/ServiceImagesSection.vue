<template>
  <section class="service-images">
    <div class="container">
      <div v-if="loading" class="loading-state">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">加载中...</span>
        </div>
        <p>正在加载服务图片...</p>
      </div>

      <div v-else-if="serviceImages.length === 0" class="empty-state">
        <div class="placeholder-images">
          <div class="image-item">
            <img
              src="@/images/website/home/Customization process.jpg"
              alt="Customization Process"
              class="service-image"
            />
          </div>
          <div class="image-item">
            <img
              src="@/images/website/home/Personalized customization services.jpg"
              alt="Personalized Customization Services"
              class="service-image"
            />
          </div>
          <div class="image-item">
            <img
              src="@/images/website/home/Wecan.jpg"
              alt="We Can"
              class="service-image"
            />
          </div>
          <div class="image-item">
            <img
              src="@/images/website/home/Reasons.jpg"
              alt="Reasons"
              class="service-image"
            />
          </div>
        </div>
      </div>

      <div v-else class="images-grid">
        <div v-for="(image, index) in sortedServiceImages" :key="image.id" class="image-item">
          <img
            :src="image.fileUrl"
            :alt="image.originalName || `Service Image ${index + 1}`"
            class="service-image"
            @error="handleImageError"
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

// 响应式数据
const serviceImages = ref([])
const loading = ref(true)

// 计算属性：按排序顺序排列的服务图片
const sortedServiceImages = computed(() => {
  // 按创建时间排序，最新的在前
  return [...serviceImages.value].sort((a, b) => {
    // 如果有排序字段，按排序字段排序，否则按创建时间排序
    if (a.sortOrder !== undefined && b.sortOrder !== undefined) {
      return a.sortOrder - b.sortOrder
    }
    return new Date(b.createTime) - new Date(a.createTime)
  }).slice(0, 4) // 只显示前4张图片
})

// 生命周期
onMounted(async () => {
  await loadServiceImages()
})

// 加载服务图片
const loadServiceImages = async () => {
  try {
    loading.value = true
    const response = await fetch('/api/admin/upload/files/category/service-images')

    if (response.ok) {
      const data = await response.json()
      if (data && data.code === 200 && data.data && data.data.list) {
        serviceImages.value = data.data.list
      } else {
        serviceImages.value = []
      }
    } else {
      console.warn('服务图片API响应失败:', response.status)
      serviceImages.value = []
    }
  } catch (error) {
    console.error('加载服务图片失败:', error)
    // 加载失败时使用默认图片
    serviceImages.value = []
  } finally {
    loading.value = false
  }
}

// 处理图片加载错误
const handleImageError = (event) => {
  // 如果图片加载失败，可以使用默认图片或隐藏
  console.warn('服务图片加载失败:', event.target.src)
  // 可以设置一个默认图片
  // event.target.src = '/default-service-image.jpg'
}
</script>

<style scoped>
.service-images {
  padding: 40px 0;
  background: #fff;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #6c757d;
}

.loading-state .spinner-border {
  margin-bottom: 20px;
}

.loading-state p {
  margin: 0;
  font-size: 1.1rem;
}

.empty-state {
  /* 空状态时使用默认图片 */
}

.placeholder-images {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0;
}

.images-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0;
}

.image-item {
  display: flex;
  justify-content: center;
}

.service-image {
  display: block;
  width: 70%;
  object-fit: cover;
  transition: all 0.3s ease;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .placeholder-images,
  .images-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .service-image {
    width: 75%;
  }
}

@media (max-width: 768px) {
  .service-images {
    padding: 30px 0;
  }

  .loading-state {
    padding: 40px 20px;
  }

  .placeholder-images,
  .images-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .service-image {
    width: 85%;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 15px;
  }

  .loading-state {
    padding: 30px 15px;
  }

  .loading-state .spinner-border {
    width: 2rem;
    height: 2rem;
  }

  .loading-state p {
    font-size: 1rem;
  }

  .service-image {
    width: 100%;
  }
}
</style>
