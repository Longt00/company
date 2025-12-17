import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import ProductsPage from '../pages/ProductsPage.vue'
import ProductDetailPage from '../pages/ProductDetailPage.vue'
import CompanyProfilePage from '../pages/CompanyProfilePage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage // 恢复使用原始首页
  },
  {
    path: '/products',
    name: 'Products',
    component: ProductsPage
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetailPage
  },
  {
    path: '/company-profile',
    name: 'CompanyProfile',
    component: CompanyProfilePage
  }
]

const router = createRouter({
  history: createWebHistory('/frontend/'),
  routes
})

export default router