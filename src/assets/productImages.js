// 产品图片统一导入文件
// Product Images Unified Import File

// 只导入存在的图片文件
import product1 from '@/images/website/products/1.png'
import product2 from '@/images/website/products/2.png'
import product3 from '@/images/website/products/3.png'
import product4 from '@/images/website/products/4.png'
import product5 from '@/images/website/products/5.png'
import product6 from '@/images/website/products/6.png'
import product7 from '@/images/website/products/7.png'
import product8 from '@/images/website/products/8.png'
import product9 from '@/images/website/products/9.png'
import product10 from '@/images/website/products/10.png'
import product11 from '@/images/website/products/11.png'
import product12 from '@/images/website/products/12.png'
import product13 from '@/images/website/products/13.jpg'
import product15 from '@/images/website/products/15.jpg'
import product16 from '@/images/website/products/16.jpg'
import product18 from '@/images/website/products/18.jpg'
import product19 from '@/images/website/products/19.jpg'
import product20 from '@/images/website/products/20.jpg'

// 导出所有产品图片
export const productImages = {
  1: product1,
  2: product2,
  3: product3,
  4: product4,
  5: product5,
  6: product6,
  7: product7,
  8: product8,
  9: product9,
  10: product10,
  11: product11,
  12: product12,
  13: product13,
  15: product15,
  16: product16,
 18: product18,
  19: product19,
  20: product20
}

// 按索引获取产品图片的辅助函数
export function getProductImage(index) {
  return productImages[index] || product1 // 默认返回第一张图片
}

// 批量导出单个图片（方便直接使用）
export {
  product1,
  product2,
  product3,
  product4,
  product5,
  product6,
  product7,
  product8,
  product9,
  product10,
  product11,
  product12,
  product13,
  product15,
  product16,
  product18,
  product19,
  product20
}