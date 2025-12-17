<template>
  <div class="change-password">
    <div class="page-header">
      <h2><i class="bi bi-key"></i> 修改密码</h2>
      <button class="btn btn-outline-primary" @click="$router.push('/')">
        <i class="bi bi-house"></i> 返回首页
      </button>
    </div>

    <section class="section">
      <div class="password-form-container">
        <div class="form-card">
          <div class="form-header">
            <h3>修改登录密码</h3>
          </div>

          <form @submit.prevent="handleSubmit">
            <!-- 旧密码 -->
            <div class="form-group">
              <label class="form-label">
                <i class="bi bi-lock"></i>
                当前密码
              </label>
              <div class="password-input-group">
                <input 
                  :type="showOldPassword ? 'text' : 'password'"
                  class="form-control"
                  v-model="form.oldPassword"
                  placeholder="请输入当前密码"
                  required
                >
                <button 
                  type="button"
                  class="btn-toggle-password"
                  @click="showOldPassword = !showOldPassword"
                >
                  <i :class="showOldPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
            </div>

            <!-- 新密码 -->
            <div class="form-group">
              <label class="form-label">
                <i class="bi bi-key"></i>
                新密码
              </label>
              <div class="password-input-group">
                <input 
                  :type="showNewPassword ? 'text' : 'password'"
                  class="form-control"
                  v-model="form.newPassword"
                  placeholder="请输入新密码（至少6位）"
                  minlength="6"
                  required
                >
                <button 
                  type="button"
                  class="btn-toggle-password"
                  @click="showNewPassword = !showNewPassword"
                >
                  <i :class="showNewPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
              <small class="form-text">密码长度至少6位，建议包含字母和数字</small>
            </div>

            <!-- 确认新密码 -->
            <div class="form-group">
              <label class="form-label">
                <i class="bi bi-check-circle"></i>
                确认新密码
              </label>
              <div class="password-input-group">
                <input 
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-control"
                  v-model="form.confirmPassword"
                  placeholder="请再次输入新密码"
                  minlength="6"
                  required
                >
                <button 
                  type="button"
                  class="btn-toggle-password"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  <i :class="showConfirmPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                </button>
              </div>
              <div v-if="form.confirmPassword && form.newPassword !== form.confirmPassword" class="error-text">
                <i class="bi bi-exclamation-circle"></i>
                两次输入的密码不一致
              </div>
            </div>


            <!-- 按钮组 -->
            <div class="button-group">
              <button 
                type="button" 
                class="btn btn-secondary"
                @click="$router.push('/')"
              >
                <i class="bi bi-x-circle"></i>
                取消
              </button>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="!canSubmit || submitting"
              >
                <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-check-circle"></i>
                {{ submitting ? '提交中...' : '确认修改' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { removeToken } from '../utils/tokenManager'
import Swal from 'sweetalert2'

const router = useRouter()

const showOldPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)
const submitting = ref(false)

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const canSubmit = computed(() => {
  return form.value.oldPassword && 
         form.value.newPassword.length >= 6 && 
         form.value.confirmPassword && 
         form.value.newPassword === form.value.confirmPassword
})

const handleSubmit = async () => {
  if (!canSubmit.value) return

  submitting.value = true

  try {
    // TODO: 调用API修改密码
    // const response = await axios.post('/api/auth/change-password', {
    //   oldPassword: form.value.oldPassword,
    //   newPassword: form.value.newPassword
    // })

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500))

    await Swal.fire({
      title: '修改成功',
      text: '密码修改成功，请重新登录',
      icon: 'success',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    
    // 清除token
    removeToken()
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('修改密码失败:', error)
    Swal.fire({
      title: '修改失败',
      text: '请检查当前密码是否正确',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.change-password {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e0e0e0;
}

.page-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 28px;
}

.page-header h2 i {
  margin-right: 10px;
  color: #1976d2;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.password-form-container {
  display: flex;
  justify-content: center;
}

.form-card {
  width: 100%;
  max-width: 600px;
}

.form-header {
  text-align: center;
  margin-bottom: 35px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e8f4f8;
}

.form-header h3 {
  margin: 0;
  color: #1976d2;
  font-size: 22px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  font-weight: 500;
  color: #495057;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
}

.form-label i {
  color: #1976d2;
}

.password-input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.form-control {
  width: 100%;
  padding: 12px 45px 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 0.2rem rgba(25, 118, 210, 0.1);
}

.btn-toggle-password {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 8px;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s;
}

.btn-toggle-password:hover {
  color: #1976d2;
}

.form-text {
  font-size: 12px;
  color: #6c757d;
  margin-top: 6px;
  display: block;
}

.error-text {
  font-size: 13px;
  color: #dc3545;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 5px;
}


.button-group {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn {
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 140px;
  justify-content: center;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
  transform: translateY(-2px);
}

.btn-primary {
  background: #1976d2;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #1565c0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .section {
    padding: 25px 20px;
  }

  .button-group {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>
