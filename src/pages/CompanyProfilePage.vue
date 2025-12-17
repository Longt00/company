<template>
  <div class="company-profile-page">
    <!-- 公司信息展示区域 -->
    <CompanyInfoSection />

    <!-- 头部导航 -->
    <Header />

    <!-- B2B供应商能力与多媒体展示 -->
    <SupplierCapabilityShowcase />

    <!-- 页脚 -->
    <Footer />

    </div>
</template>

<script>
import CompanyInfoSection from '../components/CompanyInfoSection.vue'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import CompanyProfile from '../components/CompanyProfile.vue'
import VirtualRealityShowroom from '../components/VirtualRealityShowroom.vue'
import SupplierCapabilityShowcase from '../components/SupplierCapabilityShowcase.vue'
export default {
  name: 'CompanyProfilePage',
  components: {
    CompanyInfoSection,
    Header,
    Footer,
    CompanyProfile,
    VirtualRealityShowroom,
    SupplierCapabilityShowcase
  },

}
</script>

<style scoped>
/* 通用容器 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 公司简介页面整体样式 */
.company-profile-page {
  min-height: 100vh;
  /* 背景由全局 body 控制，从接口动态加载 */
  background: transparent;
}

.profile-content {
  background: transparent;
  padding: 0;
  min-height: calc(100vh - 200px);
}


</style>