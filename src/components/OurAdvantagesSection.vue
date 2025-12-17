<template>
  <section class="advantages">
    <div class="container">
      <!-- 左右分栏容器 -->
      <div class="advantage-row">
        <!-- 左侧视频区域 -->
        <div class="video-col">
          <!-- 视频容器（后端渲染视频内容） -->
          <div class="video-container">
            <!-- 视频内容 -->
            <div v-if="videoUrl" class="video-wrapper">
              <video
                ref="videoPlayer"
                :src="videoUrl"
                class="advantage-video"
                controls
                preload="metadata"
              >
                您的浏览器不支持视频播放
              </video>
            </div>
            <!-- 加载中或错误时的占位内容 -->
            <div v-else class="video-placeholder" :class="{ error: error }">
              <div class="placeholder-icon">
                <svg width="60" height="60" viewBox="0 0 60 60" fill="none">
                  <circle cx="30" cy="30" r="30" fill="#333" opacity="0.8"/>
                  <path v-if="!error" d="M22 18L42 30L22 42V18Z" fill="white"/>
                  <path v-else d="M30 15L30 30M30 38L30 40" stroke="white" stroke-width="3" stroke-linecap="round"/>
                </svg>
              </div>
              <p class="placeholder-text">
                {{ isLoading ? '视频加载中...' : (error ? '视频加载失败' : '暂无企业宣传视频') }}
              </p>
              
            </div>
          </div>
        </div>

        <!-- 右侧文字区域 -->
        <div class="text-col">
          <h2 class="section-title">OUR ADVANTAGE</h2>
          <div class="section-content">
            <div v-html="advantageContent.replace(/\n/g, '<br>')" style="white-space: pre-wrap;"></div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { videoApi } from '../api/video'
import { getMediaFiles } from '../api/media'
import { getHomeAdvantage } from '../api/richContent'

export default {
  name: 'AdvantagesSection',
  data() {
    return {
      videoUrl: null,
      isLoading: true,
      error: null,
      // 不设置默认内容，强制从API获取
      advantageContent: ''
    }
  },
  async mounted() {
    await this.fetchAdvantageVideo()
    await this.fetchAdvantageContent()
  },
  methods: {
    /**
     * 获取企业优势视频 - 使用company-advantage分类
     */
    async fetchAdvantageVideo() {
      try {
        this.isLoading = true
        this.error = null

        console.log('🔄 [OurAdvantagesSection] 使用媒体API获取company-advantage企业优势视频...')

        // 使用新的媒体API获取企业优势视频
        const response = await getMediaFiles('company-advantage')
        console.log('✅ [OurAdvantagesSection] 媒体API响应:', response)

        // 检查响应格式并处理数据
        if (response && response.data && response.data.code === 200 && response.data.data && response.data.data.files) {
          const videoFiles = response.data.data.files

          if (videoFiles.length > 0) {
            // 使用第一个视频文件
            const firstVideo = videoFiles[0]
            this.videoUrl = firstVideo.fileUrl
            console.log(`✅ [OurAdvantagesSection] 成功获取企业优势视频:`, this.videoUrl)
            console.log('📝 视频描述:', firstVideo.description)
            return
          } else {
            console.log('⚠️ [OurAdvantagesSection] company-advantage分类中没有找到视频文件')
            this.videoUrl = null // 清空视频URL
          }
        } else {
          console.warn('⚠️ [OurAdvantagesSection] 媒体API响应格式异常:', response)
          this.videoUrl = null // 清空视频URL
        }
      } catch (error) {
        console.error('❌ [OurAdvantagesSection] 获取company-advantage企业优势视频失败:', error)
        console.error('🔍 错误详情:', {
          message: error.message,
          stack: error.stack
        })
        this.videoUrl = null // 清空视频URL
      } finally {
        this.isLoading = false
      }
    },

    /**
     * 获取企业优势文本内容
     */
    async fetchAdvantageContent() {
      try {
        console.log('🔄 [OurAdvantagesSection] 获取企业优势内容...')
        const response = await getHomeAdvantage()
        
        if (response && response.code === 200 && response.data && response.data.length > 0) {
          this.advantageContent = response.data[0].content
          console.log('✅ [OurAdvantagesSection] 成功加载企业优势内容')
        } else {
          console.warn('⚠️ [OurAdvantagesSection] API响应格式异常，使用默认内容')
        }
      } catch (error) {
        console.warn('⚠️ [OurAdvantagesSection] API连接失败，使用默认内容:', error.message)
        // 保持使用默认值，不做任何修改
      }
    },

  
    

    /**
     * 上传新的企业视频
     * @param {File} videoFile - 视频文件
     * @param {Object} options - 上传选项
     */
    async uploadAdvantageVideo(videoFile, options = {}) {
      try {
        console.log('📤 开始上传企业优势视频...')

        const response = await videoApi.uploadVideo(videoFile, {
          title: options.title || 'Company Advantage Video',
          description: options.description || '展示公司优势和企业文化的宣传视频',
          category: options.category || 'company-advantage',
          ...options
        })

        if (response && response.success && response.data) {
          const videoData = response.data
          console.log('✅ 企业优势视频上传成功:', videoData)

          // 更新当前视频URL
          this.videoUrl = videoData.accessUrl
          this.error = null

          // 重新加载视频以确保新视频生效
          if (this.$refs.videoPlayer) {
            this.$refs.videoPlayer.load()
          }

          return { success: true, data: videoData }
        } else {
          throw new Error('上传失败：服务器响应异常')
        }

      } catch (error) {
        console.error('❌ 企业优势视频上传失败:', error)
        return { success: false, error: error.message }
      }
    },

    /**
     * 选择并上传企业优势视频文件
     */
    async selectAndUploadAdvantageVideo() {
      try {
        // 创建文件选择器
        const input = document.createElement('input')
        input.type = 'file'
        input.accept = 'video/*'

        return new Promise((resolve) => {
          input.onchange = async (event) => {
            const file = event.target.files[0]
            if (!file) {
              resolve({ success: false, message: '未选择文件' })
              return
            }

            // 验证文件大小（最大200MB）
            if (file.size > 200 * 1024 * 1024) {
              resolve({ success: false, message: '视频文件大小不能超过200MB' })
              return
            }

            // 开始上传
            this.isLoading = true
            const result = await this.uploadAdvantageVideo(file, {
              title: 'Company Advantage Video - ' + new Date().toLocaleDateString(),
              description: '展示公司核心优势和制造实力的企业宣传视频'
            })
            this.isLoading = false

            resolve(result)
          }

          input.oncancel = () => {
            resolve({ success: false, message: '用户取消上传' })
          }

          input.click()
        })

      } catch (error) {
        console.error('❌ 选择视频文件失败:', error)
        return { success: false, error: error.message }
      }
    },

  }
}
</script>

<style scoped>
.advantages {
  padding: 70px 0;
  background: #fff;
}

@media (max-width: 768px) {
  .advantages {
    padding: 16px 0 !important;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 左右分栏行 */
.advantage-row {
  display: flex;
  flex-wrap: wrap; /* 响应式换行 */
  align-items: center;
  gap: 40px;
}

/* 视频列 */
.video-col {
  flex: 1 1 400px; /* 弹性布局，最小宽度400px */
}

.video-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 8px;
  overflow: hidden;
  background: #000;
}

/* 视频包装器 */
.video-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background: #000;
}

/* 视频样式 */
.advantage-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 视频占位符样式 */
.video-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  color: white;
}

.placeholder-icon {
  margin-bottom: 15px;
  opacity: 0.9;
}

.placeholder-text {
  font-size: 16px;
  margin: 0;
  opacity: 0.8;
}

/* 错误状态样式 */
.video-placeholder.error .placeholder-text {
  color: #ff6b6b;
}

/* 文字列 */
.text-col {
  flex: 1 1 300px; /* 弹性布局，最小宽度300px */
}

.section-title {
  font-size: 36px;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 20px;
}

.section-content {
  font-size: 18px;
  color: #ffffff;
  line-height: 1.8;
}

.section-content ol {
  padding-left: 20px;
  margin-top: 10px;
}

/* 响应式适配（移动端堆叠） */
@media (max-width: 768px) {
  .advantages {
    padding: 12px 0 !important;
  }

  .container {
    padding: 0 12px;
  }
  
  .advantage-row {
    flex-direction: column;
    gap: 8px;
  }

  .video-col {
    flex: 1 1 auto;
    min-width: 0;
    width: 100%;
  }

  .text-col {
    flex: 1 1 auto;
    min-width: 0;
  }
  
  .section-title {
    font-size: 20px;
    margin-bottom: 8px;
  }

  .section-content {
    font-size: 13px;
    line-height: 1.5;
  }
}

@media (max-width: 480px) {
  .advantages {
    padding: 12px 0;
  }

  .advantage-row {
    gap: 10px;
  }

  .section-title {
    font-size: 20px;
  }

  .section-content {
    font-size: 13px;
  }
}
</style>







