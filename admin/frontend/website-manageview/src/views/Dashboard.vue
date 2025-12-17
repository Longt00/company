<template>
  <div class="dashboard">
    <h2 class="mb-4">仪表盘</h2>
    
    <div class="row">
      <div class="col-md-4 mb-4">
        <div class="stat-card">
          <div class="stat-icon bg-primary">
            <i class="bi bi-image"></i>
          </div>
          <div class="stat-content">
            <h6 class="text-muted mb-1">图片总数</h6>
            <h3 class="mb-0">{{ statistics.imageCount }}</h3>
          </div>
        </div>
      </div>
      
      <div class="col-md-4 mb-4">
        <div class="stat-card">
          <div class="stat-icon bg-success">
            <i class="bi bi-file-earmark-play"></i>
          </div>
          <div class="stat-content">
            <h6 class="text-muted mb-1">视频总数</h6>
            <h3 class="mb-0">{{ statistics.videoCount }}</h3>
          </div>
        </div>
      </div>
      
      <div class="col-md-4 mb-4">
        <div class="stat-card">
          <div class="stat-icon bg-warning">
            <i class="bi bi-box-seam"></i>
          </div>
          <div class="stat-content">
            <h6 class="text-muted mb-1">产品总数</h6>
            <h3 class="mb-0">{{ statistics.productCount }}</h3>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-header">
            <h5 class="mb-0">最近活动</h5>
          </div>
          <div class="card-body">
            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">加载中...</span>
              </div>
            </div>
            <div v-else-if="activities.length === 0" class="text-center text-muted py-4">
              <i class="bi bi-inbox" style="font-size: 48px; opacity: 0.3;"></i>
              <p class="mt-3">暂无最近活动</p>
            </div>
            <div v-else>
              <div 
                v-for="activity in activities" 
                :key="activity.id" 
                class="activity-item"
              >
                <i :class="getActivityIcon(activity.operationType)"></i>
                <span v-html="formatActivityText(activity)"></span>
                <small class="text-muted">{{ formatTime(activity.createTime) }}</small>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { logsAPI, dashboardAPI } from '@/api'

// 状态管理
const loading = ref(false)
const activities = ref([])
const statistics = ref({
  imageCount: 0,
  videoCount: 0,
  productCount: 0
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await dashboardAPI.getStatistics()
    if (response.success && response.data) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载最近活动
const loadRecentActivities = async () => {
  loading.value = true
  try {
    const response = await logsAPI.getRecentLogs(10)
    if (response.success && response.data) {
      activities.value = response.data
    }
  } catch (error) {
    console.error('加载最近活动失败:', error)
  } finally {
    loading.value = false
  }
}

// 根据操作类型返回图标样式
const getActivityIcon = (operationType) => {
  const iconMap = {
    'UPLOAD': 'bi bi-upload text-success',
    'CREATE': 'bi bi-plus-circle text-success',
    'UPDATE': 'bi bi-pencil text-primary',
    'DELETE': 'bi bi-trash text-danger',
    'VIEW': 'bi bi-eye text-info',
    'LOGIN': 'bi bi-box-arrow-in-right text-success',
    'LOGOUT': 'bi bi-box-arrow-left text-secondary'
  }
  return iconMap[operationType] || 'bi bi-circle text-secondary'
}

// 格式化活动文本
const formatActivityText = (activity) => {
  const target = activity.targetName ? `<strong>${activity.targetName}</strong>` : ''
  const operationType = activity.operationType
  const operationModule = activity.operationModule
  
  // 根据操作类型生成更具体的描述
  let actionText = ''
  
  switch (operationType) {
    case 'UPLOAD':
      actionText = '上传了'
      break
    case 'CREATE':
      actionText = '创建了'
      break
    case 'UPDATE':
      actionText = '更新了'
      break
    case 'DELETE':
      actionText = '删除了'
      break
    case 'VIEW':
      actionText = '查看了'
      break
    case 'LOGIN':
      actionText = '登录系统'
      return actionText
    case 'LOGOUT':
      actionText = '退出登录'
      return actionText
    default:
      actionText = '操作了'
  }
  
  // 根据模块类型添加语境
  let moduleText = ''
  switch (operationModule) {
    case 'IMAGE':
      moduleText = '图片'
      break
    case 'VIDEO':
      moduleText = '视频'
      break
    case 'FILE':
      moduleText = '文件'
      break
    case 'PRODUCT':
      moduleText = '产品'
      break
    case 'COMPANY':
      moduleText = '公司信息'
      break
    default:
      moduleText = ''
  }
  
  // 组合文本
  if (target) {
    return `${actionText}${moduleText} ${target}`
  } else if (moduleText) {
    return `${actionText}${moduleText}`
  } else {
    return activity.operationDescription || `${actionText}内容`
  }
}

// 格式化时间（相对时间）
const formatTime = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) return `${days}天前`
  if (hours > 0) return `${hours}小时前`
  if (minutes > 0) return `${minutes}分钟前`
  return '刚刚'
}

// 组件加载时获取数据
onMounted(() => {
  loadStatistics()
  loadRecentActivities()
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-content h6 {
  font-size: 13px;
  margin-bottom: 5px;
}

.stat-content h3 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
}

.card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.card-header {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  padding: 20px;
}

.card-body {
  padding: 20px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item i {
  font-size: 18px;
}

.activity-item span {
  flex: 1;
}

.activity-item small {
  font-size: 12px;
}
</style>