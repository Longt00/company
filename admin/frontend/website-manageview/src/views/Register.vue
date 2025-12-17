<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2 class="text-center">管理员注册</h2>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="mb-3">
          <label for="username" class="form-label">
            用户名 <span class="text-danger">*</span>
          </label>
          <input 
            type="text" 
            class="form-control" 
            id="username" 
            v-model="registerForm.username"
            @blur="checkUsernameAvailability"
            placeholder="请输入用户名"
            required
          >
          <div v-if="usernameError" class="form-text text-danger">
            {{ usernameError }}
          </div>
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">
            密码 <span class="text-danger">*</span>
          </label>
          <div class="input-group">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              class="form-control" 
              id="password" 
              v-model="registerForm.password"
              placeholder="请输入密码（至少6位）"
              minlength="6"
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

        <div class="mb-3">
          <label for="confirmPassword" class="form-label">
            确认密码 <span class="text-danger">*</span>
          </label>
          <div class="input-group">
            <input 
              :type="showConfirmPassword ? 'text' : 'password'" 
              class="form-control" 
              id="confirmPassword" 
              v-model="confirmPassword"
              placeholder="请再次输入密码"
              required
            >
            <button 
              class="btn btn-outline-secondary" 
              type="button"
              @click="showConfirmPassword = !showConfirmPassword"
            >
              <i :class="showConfirmPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
            </button>
          </div>
          <div v-if="passwordMismatch" class="form-text text-danger">
            两次输入的密码不一致
          </div>
        </div>

        <button 
          type="submit" 
          class="btn btn-primary w-100 mb-3" 
          :disabled="loading || usernameError || passwordMismatch"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? '注册中...' : '注册' }}
        </button>

        <div v-if="errorMessage" class="alert alert-danger" role="alert">
          <i class="bi bi-exclamation-triangle"></i> {{ errorMessage }}
        </div>

        <div class="text-center">
          <router-link to="/login" class="text-decoration-none">
            已有账号？立即登录
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'
import Swal from 'sweetalert2'

const router = useRouter()
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const errorMessage = ref('')
const usernameError = ref('')
const confirmPassword = ref('')

const registerForm = ref({
  username: '',
  password: ''
})

const passwordMismatch = computed(() => {
  return confirmPassword.value && registerForm.value.password !== confirmPassword.value
})

// 检查用户名是否可用
const checkUsernameAvailability = async () => {
  if (!registerForm.value.username) return
  
  try {
    const response = await authAPI.checkUsername(registerForm.value.username)
    if (response.data.exists) {
      usernameError.value = '该用户名已被使用'
    } else {
      usernameError.value = ''
    }
  } catch (error) {
    console.error('检查用户名失败:', error)
  }
}

// 处理注册
const handleRegister = async () => {
  // 验证密码一致性
  if (passwordMismatch.value) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }
  
  loading.value = true
  errorMessage.value = ''
  
  try {
    // 调用注册 API
    const response = await authAPI.register(registerForm.value)
    
    if (response.code === 200) {
      // 注册成功，跳转到登录页
      await Swal.fire({
        title: '注册成功',
        text: '请使用您的账号登录',
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
      router.push('/login')
    } else {
      errorMessage.value = response.message || '注册失败，请重试'
    }
  } catch (error) {
    console.error('注册失败:', error)
    errorMessage.value = error.response?.data?.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 50%, #90caf9 100%);
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

.register-container::before {
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

.register-box {
  background: white;
  padding: 40px 40px;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 1;
}

.register-header {
  margin-bottom: 25px;
}

.register-header h2 {
  color: #1976d2;
  font-weight: 600;
  font-size: 24px;
  margin-bottom: 0;
}

.register-form {
  margin-top: 20px;
}

.form-label {
  font-weight: 500;
  color: #666;
  margin-bottom: 6px;
  font-size: 14px;
}

.form-control {
  padding: 10px 15px;
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

.btn-primary {
  padding: 12px;
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
  .register-container {
    padding: 20px;
  }

  .register-box {
    padding: 30px 25px;
  }
}
</style>