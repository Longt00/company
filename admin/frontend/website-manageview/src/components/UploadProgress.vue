<template>
  <!-- 上传进度条弹窗 -->
  <div v-if="show" class="upload-progress-overlay">
    <div class="upload-progress-modal">
      <div class="progress-header">
        <i class="bi bi-cloud-upload"></i>
        <span>上传并压缩中...</span>
      </div>
      <div class="progress-file-name">{{ fileName }}</div>
      <div class="progress-bar-wrapper">
        <div class="progress-bar">
          <div
            class="progress-bar-fill"
            :style="{ width: percent + '%' }"
          >
            <span class="progress-text">{{ percent }}%</span>
          </div>
        </div>
      </div>
      <div class="progress-tip">
        <i class="bi bi-info-circle"></i>
        正在上传图片并进行智能压缩优化，请不要关闭页面...
      </div>

      <!-- 可选的取消按钮 -->
      <div class="progress-actions" v-if="cancellable">
        <button
          class="btn btn-outline-secondary btn-sm"
          @click="handleCancel"
          :disabled="cancelling"
        >
          <i class="bi bi-x-circle"></i>
          {{ cancelling ? '取消中...' : '取消上传' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UploadProgress',
  props: {
    // 是否显示进度条
    show: {
      type: Boolean,
      default: false
    },
    // 进度百分比
    percent: {
      type: Number,
      default: 0,
      validator: value => value >= 0 && value <= 100
    },
    // 文件名
    fileName: {
      type: String,
      default: ''
    },
    // 是否可取消
    cancellable: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      cancelling: false
    }
  },
  methods: {
    handleCancel() {
      if (!this.cancelling) {
        this.cancelling = true
        this.$emit('cancel')
      }
    }
  }
}
</script>

<style scoped>
/* 上传进度条弹窗 */
.upload-progress-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);           /* 半透明背景遮罩 */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;                            /* 最高层级 */
  backdrop-filter: blur(4px);               /* 背景模糊效果 */
}

.upload-progress-modal {
  background: white;
  border-radius: 16px;
  padding: 40px;
  min-width: 500px;
  max-width: 600px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

/* 弹窗滑入动画 */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.progress-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 20px;
}

.progress-header i {
  font-size: 24px;
  animation: pulse 1.5s ease-in-out infinite;
}

/* 图标脉冲动画 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.progress-file-name {
  color: #666;
  font-size: 14px;
  margin-bottom: 25px;
  word-break: break-all;                 /* 文件名过长时换行 */
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 3px solid #4a90e2;
}

.progress-bar-wrapper {
  margin-bottom: 20px;
}

.progress-bar {
  width: 100%;
  height: 40px;
  background: #e3f2fd;                   /* 进度条背景色 */
  border-radius: 20px;
  overflow: hidden;
  position: relative;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #1976d2 0%, #2196f3 100%); /* 渐变色 */
  border-radius: 20px;
  transition: width 0.3s ease;          /* 平滑过渡 */
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 进度条闪光动画效果 */
.progress-bar-fill::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.progress-text {
  color: white;
  font-weight: 600;
  font-size: 16px;
  z-index: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.progress-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 13px;
  padding: 12px;
  background: #fff3cd;                   /* 黄色提示背景 */
  border-radius: 8px;
  border-left: 4px solid #ffc107;         /* 左侧彩色边框 */
  margin-bottom: 20px;
}

.progress-tip i {
  color: #ffc107;
  font-size: 16px;
}

.progress-actions {
  display: flex;
  justify-content: flex-end;
}

.progress-actions .btn {
  min-width: 120px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .upload-progress-modal {
    min-width: 90%;
    padding: 30px 20px;
  }

  .progress-bar {
    height: 32px;                        /* 移动端进度条更小 */
  }

  .progress-text {
    font-size: 14px;
  }

  .progress-header {
    font-size: 18px;
  }

  .progress-header i {
    font-size: 20px;
  }
}

/* 暗色模式支持 */
@media (prefers-color-scheme: dark) {
  .upload-progress-modal {
    background: #2d3748;
    color: #e2e8f0;
  }

  .progress-file-name {
    background: #4a5568;
    color: #e2e8f0;
  }

  .progress-tip {
    background: #2c5282;
    color: #e2e8f0;
  }

  .progress-tip i {
    color: #fbbf24;
  }
}
</style>