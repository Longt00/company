import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../components/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import ImageManagement from '../views/ImageManagement.vue'
import VideoManagement from '../views/VideoManagement.vue'
import ProductManagement from '../views/ProductManagement.vue'
import ChangePassword from '../views/ChangePassword.vue'
import CompanyInfo from '../views/CompanyInfo.vue'
import HomeContentManagement from '../views/HomeContentManagement.vue'
import { getToken, isTokenExpired, removeToken } from '../utils/tokenManager'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: '/images',
        name: 'ImageManagement',
        component: ImageManagement
      },
      {
        path: '/videos',
        name: 'VideoManagement',
        component: VideoManagement
      },
      {
        path: '/products',
        name: 'ProductManagement',
        component: ProductManagement
      },
      {
        path: '/change-password',
        name: 'ChangePassword',
        component: ChangePassword
      },
      {
        path: '/company',
        name: 'CompanyInfo',
        component: CompanyInfo
      },
      {
        path: '/home-content',
        name: 'HomeContentManagement',
        component: HomeContentManagement
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  const publicPages = ['/login', '/register']
  const authRequired = !publicPages.includes(to.path)
  
  // 检查token是否存在且未过期
  const hasValidToken = token && !isTokenExpired()
  
  if (authRequired && !hasValidToken) {
    // 需要认证但token不存在或已过期
    if (token) {
      // token存在但已过期，清除它
      removeToken()
      console.log('Token已过期，请重新登录')
    }
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && hasValidToken) {
    // 已登录用户访问登录/注册页，跳转到首页
    next('/home')
  } else {
    next()
  }
})

export default router