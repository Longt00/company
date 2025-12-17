<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'collapsed': isCollapsed }">
      <div class="sidebar-header">
        <h4 v-if="!isCollapsed" class="mb-0">管理后台</h4>
        <h5 v-else class="mb-0">后台</h5>
      </div>
      
      <nav class="sidebar-nav">
        <ul class="nav flex-column">
          <li class="nav-item">
            <router-link to="/dashboard" class="nav-link" active-class="active">
              <i class="bi bi-speedometer2"></i>
              <span v-if="!isCollapsed">仪表盘</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/images" class="nav-link" active-class="active">
              <i class="bi bi-image"></i>
              <span v-if="!isCollapsed">图片管理</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/videos" class="nav-link" active-class="active">
              <i class="bi bi-file-earmark-play"></i>
              <span v-if="!isCollapsed">视频管理</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/products" class="nav-link" active-class="active">
              <i class="bi bi-box-seam"></i>
              <span v-if="!isCollapsed">产品管理</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/company" class="nav-link" active-class="active">
              <i class="bi bi-building"></i>
              <span v-if="!isCollapsed">公司信息</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/home-content" class="nav-link" active-class="active">
              <i class="bi bi-house-fill"></i>
              <span v-if="!isCollapsed">首页内容</span>
            </router-link>
          </li>
        </ul>
      </nav>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-navbar">
        <div class="d-flex align-items-center justify-content-between w-100">
          <button class="btn btn-link text-dark" @click="toggleSidebar">
            <i class="bi bi-list fs-4"></i>
          </button>
          
          <div class="d-flex align-items-center gap-3">
            <div class="dropdown">
              <button class="btn btn-link text-dark dropdown-toggle" type="button" data-bs-toggle="dropdown">
                <i class="bi bi-person-circle fs-5"></i>
                <span class="ms-2">管理员</span>
              </button>
              <ul class="dropdown-menu dropdown-menu-end">
                <li><a class="dropdown-item" href="#" @click="showChangePassword"><i class="bi bi-key"></i> 修改密码</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" @click="logout"><i class="bi bi-box-arrow-right"></i> 退出登录</a></li>
              </ul>
            </div>
          </div>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { removeToken } from '../utils/tokenManager'
import Swal from 'sweetalert2'

const router = useRouter()
const isCollapsed = ref(false)

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const showChangePassword = () => {
  router.push('/change-password')
}

const logout = async () => {
  const result = await Swal.fire({
    title: '温馨提示',
    text: '确定要退出登录吗？',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })
  
  if (result.isConfirmed) {
    removeToken() // 清除token和用户信息
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100%;
  background-color: #ffffff; /* 确保整个布局容器为白色背景 */
  overflow: hidden; /* 防止容器级别的滚动 */
}

/* 侧边栏样式 */
.sidebar {
  width: 250px;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: white;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 20px;
  background: rgba(0, 0, 0, 0.2);
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
}

.sidebar-nav .nav-item {
  margin: 5px 10px;
}

.sidebar-nav .nav-link {
  color: rgba(255, 255, 255, 0.8);
  padding: 12px 15px;
  border-radius: 8px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
}

.sidebar-nav .nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.sidebar-nav .nav-link.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 500;
}

.sidebar-nav .nav-link i {
  font-size: 18px;
}

.sidebar.collapsed .nav-link {
  justify-content: center;
  padding: 12px;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #ffffff; /* 确保主内容区为白色背景 */
  min-height: 0; /* 允许flex子项收缩 */
}

/* 顶部导航栏 */
.top-navbar {
  background: white;
  padding: 15px 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.top-navbar .btn-link {
  text-decoration: none;
  padding: 0;
}

.top-navbar .dropdown-toggle::after {
  margin-left: 8px;
}

.dropdown-item i {
  margin-right: 8px;
  width: 16px;
}

/* 内容区域 */
.content-area {
  flex: 1;
  padding: 25px;
  overflow-y: auto;
  background: #ffffff; /* 修改为纯白色背景，避免多余背景色 */
  min-height: 0; /* 允许flex子项收缩，确保滚动条正常工作 */

  /* 防止内容溢出导致的布局问题 */
  -webkit-overflow-scrolling: touch; /* 在移动设备上提供平滑滚动 */

  /* 确保滚动时不会显示底层背景 */
  position: relative;
  z-index: 1;
}
</style>