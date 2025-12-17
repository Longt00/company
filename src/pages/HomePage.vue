<template>
  <div class="home-page">
    <!-- 公司信息展示区域 -->
    <CompanyInfoSection />

    <!-- 头部导航 -->
    <Header />

    <!-- 主要内容区域 -->
    <main>
      <!-- 主要展示区域 -->
      <HeroSection />

      <!-- 公司优势 -->
      <AdvantagesSection />

      <!-- 公司简介 -->
      <CompanyProfile />

      <!-- 验证供应商部分 -->
      <VerifiedSupplierSection />

      <!-- 公司优势部分 -->
      <CompanyAdvantagesSection />

      <!-- 服务图片 -->
      <ServiceImagesSection />

      <!-- 服务轮播 -->
      <ServiceCarousel />

      <!-- 产品展示 -->
      <ProductDisplaySection />

      <!-- 主要产品部分（新） -->
      <MainProductsNew />

      <!-- 主要产品部分 -->
      <MainProductsSection />

      <!-- 新产品展示 -->
      <NewProducts />

      <!-- Virtual Reality Showroom -->
      <VirtualRealityShowroom />

    </main>

    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script>
import CompanyInfoSection from '../components/CompanyInfoSection.vue'
import Header from '../components/Header.vue'
import HeroSection from '../components/HeroSection.vue'
import AdvantagesSection from '../components/OurAdvantagesSection.vue'
import CompanyProfile from '../components/CompanyProfile.vue'
import VerifiedSupplierSection from '../components/VerifiedSupplierSection.vue'
import ServiceImagesSection from '../components/ServiceImagesSection.vue'
import ServiceCarousel from '../components/ServiceCarousel.vue'
import ProductDisplaySection from '../components/ProductDisplaySection.vue'
import CompanyAdvantagesSection from '../components/CompanyAdvantagesSection.vue'
import MainProductsSection from '../components/MainProductsSection.vue'
import NewProducts from '../components/NewProducts.vue'
import VirtualRealityShowroom from '../components/VirtualRealityShowroom.vue'
import Footer from '../components/Footer.vue'
import MainProductsNew from '../components/MainProductsNew.vue'

export default {
  name: 'HomePage',
  components: {
    CompanyInfoSection,
    Header,
    HeroSection,
    AdvantagesSection,
    CompanyProfile,
    VerifiedSupplierSection,
    ServiceImagesSection,
    ServiceCarousel,
    ProductDisplaySection,
    CompanyAdvantagesSection,
    MainProductsNew,
    MainProductsSection,
    NewProducts,
    VirtualRealityShowroom,
    Footer
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: transparent;
}
</style>