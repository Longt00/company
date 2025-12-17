/**
 * 产品管理 API
 */
import http from './http'

export const productAPI = {
  /**
   * 创建产品
   */
  createProduct(data) {
    return http.post('/api/products', data)
  },

  /**
   * 更新产品
   */
  updateProduct(id, data) {
    return http.put(`/api/products/${id}`, data)
  },

  /**
   * 获取产品详情
   */
  getProductById(id) {
    return http.get(`/api/products/${id}`)
  },

  /**
   * 分页查询产品列表
   */
  getProducts(params) {
    return http.get('/api/products', { params })
  },

  /**
   * 获取上架产品列表
   */
  getActiveProducts() {
    return http.get('/api/products/active')
  },

  /**
   * 获取推荐产品列表
   */
  getFeaturedProducts() {
    return http.get('/api/products/featured')
  },

  /**
   * 获取草稿产品列表
   */
  getDraftProducts() {
    return http.get('/api/products/status/drafts')
  },

  /**
   * 搜索产品
   */
  searchProducts(keyword) {
    return http.get('/api/products/search', { params: { keyword } })
  },

  /**
   * 高级搜索产品
   */
  searchProductsAdvanced(params) {
    return http.get('/api/products/search/advanced', { params })
  },

  /**
   * 根据分类获取产品
   */
  getProductsByCategory(category) {
    return http.get(`/api/products/category/${category}`)
  },

  /**
   * 上架产品
   */
  activateProduct(id) {
    return http.put(`/api/products/${id}/activate`)
  },

  /**
   * 下架产品
   */
  deactivateProduct(id) {
    return http.put(`/api/products/${id}/deactivate`)
  },

  /**
   * 保存为草稿
   */
  saveAsDraft(id) {
    return http.put(`/api/products/${id}/save-draft`)
  },

  /**
   * 从草稿发布
   */
  publishFromDraft(id) {
    return http.put(`/api/products/${id}/publish`)
  },

  /**
   * 删除产品
   */
  deleteProduct(id) {
    return http.delete(`/api/products/${id}`)
  },

  /**
   * 批量删除产品
   */
  batchDeleteProducts(ids) {
    return http.delete('/api/products/batch', { data: ids })
  },

  /**
   * 批量上架产品
   */
  batchActivateProducts(ids) {
    return http.put('/api/products/batch/activate', ids)
  },

  /**
   * 批量下架产品
   */
  batchDeactivateProducts(ids) {
    return http.put('/api/products/batch/deactivate', ids)
  },

  /**
   * 批量保存为草稿
   */
  batchSaveAsDraft(ids) {
    return http.put('/api/products/batch/save-draft', ids)
  },

  /**
   * 批量从草稿发布
   */
  batchPublishFromDraft(ids) {
    return http.put('/api/products/batch/publish', ids)
  },

  /**
   * 更新库存
   */
  updateStock(id, quantity) {
    return http.put(`/api/products/${id}/stock`, null, { params: { quantity } })
  },

  /**
   * 设置为推荐
   */
  setAsFeatured(id) {
    return http.put(`/api/products/${id}/featured`)
  },

  /**
   * 取消推荐
   */
  unsetAsFeatured(id) {
    return http.delete(`/api/products/${id}/featured`)
  },

  /**
   * 更新产品推荐状态
   * @param {number} id 产品ID
   * @param {boolean} isFeatured 是否推荐
   */
  updateProductFeatured(id, isFeatured) {
    if (isFeatured) {
      return this.setAsFeatured(id)
    } else {
      return this.unsetAsFeatured(id)
    }
  },

  /**
<<<<<<< HEAD
   * 批量设置产品为推荐
   * @param {Array} ids 产品ID列表
   */
  async batchSetAsFeatured(ids) {
    const results = { successCount: 0, failureCount: 0, errors: [] }

    for (const id of ids) {
      try {
        await this.setAsFeatured(id)
        results.successCount++
      } catch (error) {
        results.failureCount++
        const errorMsg = error.response?.data?.message || error.message
        results.errors.push(`产品ID ${id} 设置推荐失败: ${errorMsg}`)
      }
    }

    return results
  },

  /**
   * 批量取消产品推荐
   * @param {Array} ids 产品ID列表
   */
  async batchUnsetAsFeatured(ids) {
    const results = { successCount: 0, failureCount: 0, errors: [] }

    for (const id of ids) {
      try {
        await this.unsetAsFeatured(id)
        results.successCount++
      } catch (error) {
        results.failureCount++
        const errorMsg = error.response?.data?.message || error.message
        results.errors.push(`产品ID ${id} 取消推荐失败: ${errorMsg}`)
      }
    }

    return results
  },

  /**
=======
>>>>>>> c32817d68ce552510e979397ce6d60a161ea3dc0
   * 检查产品编码是否存在
   */
  checkProductCodeExists(productCode) {
    return http.get(`/api/products/check-code/${productCode}`)
  },

  /**
   * 获取产品统计信息
   */
  getProductStatistics() {
    return http.get('/api/products/statistics')
  },

  /**
   * 检查API健康状态
   */
  checkHealth() {
    return http.get('/api/health')
  },

  /**
   * 上传产品视频
   */
  uploadProductVideo(formData) {
    console.log('🎬 API调用: uploadProductVideo (使用通用视频接口)')
    console.log('📁 FormData内容:')
    for (let [key, value] of formData.entries()) {
      console.log(`  ${key}:`, value instanceof File ? `File(${value.name}, ${value.size} bytes, ${value.type})` : value)
    }

    // 使用通用视频上传接口 /api/admin/upload/video，它使用"video"分类
    // 而不是 /api/admin/upload/product/video，它使用"product/videos"分类但验证失败
    return http.post('/api/admin/upload/video', formData, {
      timeout: 120000 // 增加超时时间到2分钟，用于大文件上传
    })
  },

  /**
   * 关联视频到产品
   */
  attachVideoToProduct(productId, videoUrl, videoTitle, videoDescription) {
    return http.post(`/api/admin/products/${productId}/video`, {
      videoUrl,
      videoTitle,
      videoDescription
    })
  },

  /**
   * 删除产品视频
   */
  deleteProductVideo(productId) {
    return http.delete(`/api/admin/products/${productId}/video`)
  },

  /**
   * 获取产品视频信息
   */
  getProductVideo(productId) {
    return http.get(`/api/admin/products/${productId}/video`)
  },

  /**
   * 增强视频上传
   */
  uploadEnhancedProductVideo(formData, description) {
    return http.post('/api/admin/upload/product/video/enhanced', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      params: {
        description: description || '产品视频'
      }
    })
  },

  /**
   * 获取新产品列表
   */
  getNewProducts() {
    return http.get('/api/products/new')
  },

  /**
   * 获取主产品列表
   */
  getMainProducts() {
    return http.get('/api/products/main')
  },

  /**
   * 获取上架新产品列表
   */
  getActiveNewProducts() {
    return http.get('/api/products/active/new')
  },

  /**
   * 获取上架主产品列表
   */
  getActiveMainProducts() {
    return http.get('/api/products/active/main')
  },

  /**
   * 设置产品为主产品
   */
  setAsMain(id) {
    return http.put(`/api/products/${id}/main`)
  },

  /**
   * 取消产品主产品标识
   */
  unsetAsMain(id) {
    return http.delete(`/api/products/${id}/main`)
  },

  /**
   * 设置产品为新产品
   */
  setAsNew(id) {
    return http.put(`/api/products/${id}/new`)
  },

  /**
   * 取消产品新产品标识
   */
  unsetAsNew(id) {
    return http.delete(`/api/products/${id}/new`)
  },

  /**
   * 批量设置产品为主产品
   */
  batchSetAsMain(ids) {
    return http.put('/api/products/batch/main', ids)
  },

  /**
   * 批量取消产品主产品标识
   */
  batchUnsetAsMain(ids) {
    return http.delete('/api/products/batch/main', { data: ids })
  },

  /**
   * 批量设置产品为新产品
   */
  batchSetAsNew(ids) {
    return http.put('/api/products/batch/new', ids)
  },

  /**
   * 批量取消产品新产品标识
   */
  batchUnsetAsNew(ids) {
    return http.delete('/api/products/batch/new', { data: ids })
  },

  /**
   * 更新产品主产品和新产品状态
   * @param {number} id 产品ID
   * @param {Object} status 状态对象 {isMain: boolean, isNew: boolean}
   */
  updateProductMainAndNewStatus(id, status) {
    const promises = []

    if (status.isMain !== undefined) {
      promises.push(status.isMain ? this.setAsMain(id) : this.unsetAsMain(id))
    }

    if (status.isNew !== undefined) {
      promises.push(status.isNew ? this.setAsNew(id) : this.unsetAsNew(id))
    }

    return Promise.all(promises)
  }
}

export default productAPI