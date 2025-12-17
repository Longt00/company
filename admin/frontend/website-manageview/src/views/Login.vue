<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2 class="text-center">管理后台登录</h2>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="mb-3">
          <label for="username" class="form-label">
            用户名
          </label>
          <input 
            type="text" 
            class="form-control" 
            id="username" 
            v-model="loginForm.username"
            placeholder="请输入用户名"
            required
          >
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">
            登录密码
          </label>
          <div class="input-group">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              class="form-control" 
              id="password" 
              v-model="loginForm.password"
              placeholder="请输入密码"
              required
            >
            <button 
              class="btn btn-outline-secondary" 
              type="button"
              @click="showPassword = !showPassword"
            >
              <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
            </button>
          </div>
        </div>


        <button type="submit" class="btn btn-primary w-100 mb-3" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? '登录中...' : '登录' }}
        </button>

        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          <i class="bi bi-exclamation-triangle"></i> {{ errorMessage }}
        </div>

        <div class="text-center mt-3">
          <router-link to="/register" class="text-decoration-none">
            还没有账号？立即注册
          </router-link>
        </div>
      </form>


    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'
import Swal from 'sweetalert2'
import { setToken } from '../utils/tokenManager'

const router = useRouter()
const showPassword = ref(false)
const loading = ref(false)
const errorMessage = ref('')

const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''
  
  try {
    // 调用登录 API
    const response = await authAPI.login(loginForm.value)
    
    // 保存 token 和用户信息
    if (response.data && response.data.token) {
      setToken(response.data.token) // 使用tokenManager保存token和时间戳
      localStorage.setItem('userInfo', JSON.stringify(response.data.user))
      
      // 显示登录成功提示
      await Swal.fire({
        icon: 'success',
        title: '登录成功！',
        text: '欢迎回来，' + (response.data.user?.username || '管理员'),
        showConfirmButton: false,
        timer: 1500
      })
      
      // 跳转到首页
      router.push('/home')
    } else {
      errorMessage.value = '登录失败，请重试'
    }
  } catch (error) {
    console.error('登录失败:', error)
    errorMessage.value = error.response?.data?.message || '用户名或密码错误'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 50%, #90caf9 100%);
  padding: 80px;
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: -10%;
  left: -20%;
  width: 60%;
  height: 120%;
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 50%, #64b5f6 100%);
  transform: skewX(-15deg);
  opacity: 0.9;
}

.login-box {
  background: white;
  padding: 60px 50px;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 460px;
  position: relative;
  z-index: 1;
}

.login-header {
  margin-bottom: 35px;
}

.login-header h2 {
  color: #1976d2;
  font-weight: 600;
  font-size: 24px;
  margin-bottom: 0;
}

.login-form {
  margin-top: 30px;
}

.form-label {
  font-weight: 400;
  color: #666;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-control {
  padding: 12px 15px;
  border-radius: 4px;
  border: 1px solid #ddd;
  transition: all 0.3s;
  background: #f8f9fa;
}

.form-control:focus {
  border-color: #1976d2;
  box-shadow: 0 0 0 0.15rem rgba(25, 118, 210, 0.15);
  background: white;
}

.input-group .btn {
  border-left: none;
}

.btn-primary {
  padding: 13px;
  border-radius: 4px;
  background: #1976d2;
  border: none;
  font-weight: 500;
  font-size: 15px;
  transition: all 0.3s;
}

.btn-primary:hover:not(:disabled) {
  background: #1565c0;
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.alert {
  border-radius: 8px;
  padding: 12px 15px;
}

@media (max-width: 768px) {
  .login-container {
    padding: 20px;
    justify-content: center;
  }

  .login-container::before {
    width: 100%;
    left: 0;
    top: 0;
    height: 40%;
    transform: skewY(-5deg);
  }
}
</style>