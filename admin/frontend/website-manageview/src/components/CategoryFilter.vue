<template>
  <div class="category-filter">
    <div class="filter-header">
      <h4>
        <i class="bi bi-funnel"></i>
        分类筛选
      </h4>
      <button
        v-if="selectedCategory"
        class="btn-clear-filter"
        @click="clearFilter"
        title="清除筛选"
      >
        <i class="bi bi-x-circle"></i>
      </button>
    </div>

    <div class="category-tree">
      <!-- 一级分类 -->
      <div
        v-for="level1Category in level1Categories"
        :key="level1Category"
        class="category-node"
      >
        <div
          class="category-item level1"
          :class="{
            active: selectedCategory === level1Category,
            expanded: expandedCategories.includes(level1Category)
          }"
          @click="toggleCategory(level1Category)"
        >
          <i class="bi bi-chevron-right expand-icon" v-if="hasSubcategories(level1Category)"></i>
          <span class="category-name">{{ level1Category }}</span>
          <span class="product-count">{{ getProductCount(level1Category) }}</span>
        </div>

        <!-- 二级分类 -->
        <div
          v-if="expandedCategories.includes(level1Category)"
          class="subcategory-container"
        >
          <div
            v-for="level2Category in getLevel2Categories(level1Category)"
            :key="`${level1Category}-${level2Category}`"
            class="category-node"
          >
            <div
              class="category-item level2"
              :class="{
                active: selectedCategory === `${level1Category} > ${level2Category}`,
                expanded: expandedCategories.includes(`${level1Category} > ${level2Category}`)
              }"
              @click="toggleCategory(`${level1Category} > ${level2Category}`)"
            >
              <i class="bi bi-chevron-right expand-icon" v-if="hasSubcategories(level1Category, level2Category)"></i>
              <span class="category-name">{{ level2Category }}</span>
              <span class="product-count">{{ getProductCount(level1Category, level2Category) }}</span>
            </div>

            <!-- 三级分类 -->
            <div
              v-if="expandedCategories.includes(`${level1Category} > ${level2Category}`)"
              class="subcategory-container"
            >
              <div
                v-for="level3Category in getLevel3Categories(level1Category, level2Category)"
                :key="`${level1Category}-${level2Category}-${level3Category}`"
                class="category-item level3"
                :class="{ active: selectedCategory === `${level1Category} > ${level2Category} > ${level3Category}` }"
                @click="selectCategory(`${level1Category} > ${level2Category} > ${level3Category}`)"
              >
                <i class="bi bi-circle-fill category-icon"></i>
                <span class="category-name">{{ level3Category }}</span>
                <span class="product-count">{{ getProductCount(level1Category, level2Category, level3Category) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 当前筛选状态 -->
    <div v-if="selectedCategory" class="filter-status">
      <div class="current-filter">
        <i class="bi bi-funnel-fill"></i>
        <span class="filter-text">当前筛选:</span>
        <span class="filter-category">{{ selectedCategory }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, toRefs, watch } from 'vue'
import { productAPI } from '@/api'

const props = defineProps({
  products: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['filter-change', 'category-select'])

// 预设分类数据 - 与 CategorySelectorAdvanced 保持一致
const presetCategories = {
  "Men": {
    "Bottoms": ["Trousers", "Shorts"],
    "Tops": ["Hoodie", "T-shirt", "Vest"],
    "Coat/Shirt": ["Suit", "Dress Shirt", "Casual Shirt"]
  },
  "Women": {
    "Bottoms": ["Trousers", "Shorts", "Skirts"],
    "Tops": ["Hoodie", "T-shirt", "Vest"],
    "Coat/Shirt": ["Suit", "Blouse", "Casual Shirt"],
    "Maternity clothing": ["Maternity Tops", "Maternity Bottoms", "Maternity Dresses"]
  },
  "Children": {
    "Girls": ["Trousers", "Shorts", "Skirts", "Tops", "Hoodie", "T-shirt", "Vest", "Coat/Shirt", "Suit"],
    "Boys": ["Trousers", "Shorts", "Tops", "Hoodie", "T-shirt", "Vest", "Coat/Shirt", "Suit"]
  },
  "Denim hat/Denim bag": {
    "Denim Products": ["Denim Baseball Cap", "Denim Bucket Hat", "Denim Backpack", "Denim Shoulder Bag"]
  },
  "Customization of denim processing accessories": {
    "Customization Services": ["Custom Embroidery", "Custom Printing", "Custom Patches"]
  },
  "Ungrouped": {
    "Other Products": ["Uncategorized", "Special Items", "Miscellaneous"]
  }
}

// 状态管理
const selectedCategory = ref('')
const expandedCategories = ref([])
const categoryProductCounts = ref({})

// 计算属性：收集产品中实际出现的分类层级信息
const visibleCategoryInfo = computed(() => {
  const level1Set = new Set()
  const level2Map = {}
  const level3Map = {}

  ;(props.products || []).forEach(product => {
    const categoryPath = product.categoryPath || product.category || ''
    if (!categoryPath) return

    const parts = categoryPath
      .split(' > ')
      .map(part => part.trim())
      .filter(part => part)

    if (!parts.length) return

    const level1 = parts[0]
    const level2 = parts[1]
    const level3 = parts[2]

    if (level1) {
      level1Set.add(level1)
    }

    if (level1 && level2) {
      if (!level2Map[level1]) {
        level2Map[level1] = new Set()
      }
      level2Map[level1].add(level2)
    }

    if (level1 && level2 && level3) {
      const key = `${level1} > ${level2}`
      if (!level3Map[key]) {
        level3Map[key] = new Set()
      }
      level3Map[key].add(level3)
    }
  })

  return {
    level1: level1Set,
    level2: level2Map,
    level3: level3Map
  }
})

// 新增：解析产品中的分类路径为分类树结构
const parseProductCategoriesToTree = (products) => {
  const tree = {};
  products.forEach(product => {
    const categoryPath = product.categoryPath || product.category || '';
    if (!categoryPath) return;
    const parts = categoryPath.split(' > ').map(part => part.trim()).filter(part => part);
    if (parts.length === 0) return;

    // 构建层级结构：对象表示有子分类，数组表示叶子节点
    let currentLevel = tree;
    parts.forEach((part, index) => {
      // 如果节点不存在，初始化（最后一个节点为数组，其余为对象）
      if (!currentLevel[part]) {
        currentLevel[part] = index === parts.length - 1 ? [] : {};
      }
      // 处理节点类型转换：如果原本是叶子节点（数组）但需要成为父节点，转为对象
      if (index < parts.length - 1 && Array.isArray(currentLevel[part])) {
        currentLevel[part] = {};
      }
      // 进入下一级
      currentLevel = currentLevel[part];
    });
  });

  // 归一化树结构（数组去重）
  const normalizeTree = (node) => {
    if (Array.isArray(node)) {
      return [...new Set(node)];
    }
    const normalized = {};
    for (const key in node) {
      normalized[key] = normalizeTree(node[key]);
    }
    return normalized;
  };

  return normalizeTree(tree);
};

// 新增：合并两个分类树（预设树 + 产品动态树）
const mergeCategoryTrees = (presetTree, dynamicTree) => {
  const merged = { ...presetTree };

  // 递归合并节点
  const mergeNodes = (target, source) => {
    for (const key in source) {
      if (target[key] === undefined) {
        // 源节点不存在，直接赋值
        target[key] = source[key];
      } else {
        // 两者都存在，递归合并
        if (typeof target[key] === 'object' && typeof source[key] === 'object' && !Array.isArray(target[key]) && !Array.isArray(source[key])) {
          // 两个都是对象，递归合并子节点
          mergeNodes(target[key], source[key]);
        } else if (Array.isArray(target[key]) && Array.isArray(source[key])) {
          // 两个都是数组，合并并去重
          target[key] = [...new Set([...target[key], ...source[key]])];
        }
        // 其他情况（对象+数组）：保留预设的节点类型（优先预设分类结构）
      }
    }
  };

  mergeNodes(merged, dynamicTree);
  return merged;
};

// 新增：合并后的分类树（预设分类 + 产品动态分类）
const combinedCategoryTree = computed(() => {
  const dynamicTree = parseProductCategoriesToTree(props.products);
  return mergeCategoryTrees(presetCategories, dynamicTree);
});

// 修改：一级分类从合并后的分类树获取（不再仅依赖预设分类）
const level1Categories = computed(() => {
  const allLevel1 = Object.keys(combinedCategoryTree.value);

  // 当没有产品时，展示全部合并后的一级分类
  if (!props.products || !props.products.length) {
    return allLevel1;
  }

  const visibleSet = visibleCategoryInfo.value.level1;
  const filtered = allLevel1.filter(name => visibleSet.has(name));

  // 过滤后为空则显示全部一级分类
  return filtered.length ? filtered : allLevel1;
});

// 修改：判断是否有子分类（基于合并后的分类树）
const hasSubcategories = (level1, level2 = null) => {
  if (level2) {
    const level2Node = combinedCategoryTree.value[level1]?.[level2];
    // 对象且有子节点表示有子分类
    return typeof level2Node === 'object' && !Array.isArray(level2Node) && Object.keys(level2Node).length > 0;
  }
  const level1Node = combinedCategoryTree.value[level1];
  return typeof level1Node === 'object' && !Array.isArray(level1Node) && Object.keys(level1Node).length > 0;
};

// 修改：获取二级分类（基于合并后的分类树）
const getLevel2Categories = (level1) => {
  const level1Node = combinedCategoryTree.value[level1];
  // 对象取key作为二级分类，数组则无二级分类
  const allLevel2 = typeof level1Node === 'object' && !Array.isArray(level1Node) ? Object.keys(level1Node) : [];

  if (!props.products || !props.products.length) {
    return allLevel2;
  }

  const visibleMap = visibleCategoryInfo.value.level2;
  const visibleSet = visibleMap[level1];

  if (!visibleSet) {
    return allLevel2;
  }

  const filtered = allLevel2.filter(name => visibleSet.has(name));

  return filtered.length ? filtered : allLevel2;
};

// 修改：获取三级分类（基于合并后的分类树）
const getLevel3Categories = (level1, level2) => {
  const level2Node = combinedCategoryTree.value[level1]?.[level2];
  let allLevel3 = [];
  // 对象取key，数组直接用数组项，其余为空
  if (typeof level2Node === 'object' && !Array.isArray(level2Node)) {
    allLevel3 = Object.keys(level2Node);
  } else if (Array.isArray(level2Node)) {
    allLevel3 = level2Node;
  }

  if (!props.products || !props.products.length) {
    return allLevel3;
  }

  const key = `${level1} > ${level2}`;
  const visibleMap = visibleCategoryInfo.value.level3;
  const visibleSet = visibleMap[key];

  if (!visibleSet) {
    return allLevel3;
  }

  const filtered = allLevel3.filter(name => visibleSet.has(name));

  return filtered.length ? filtered : allLevel3;
};

// 修改：优化分类切换逻辑（无子女分类时不执行展开/收起）
const toggleCategory = (categoryPath) => {
  const parts = categoryPath.split(' > ');

  // 判断当前分类是否有子分类
  const hasSub = () => {
    if (parts.length === 1) {
      return hasSubcategories(parts[0]);
    } else if (parts.length === 2) {
      return hasSubcategories(parts[0], parts[1]);
    }
    return false;
  };

  // 先选中分类
  selectCategory(categoryPath);

  // 只有有子分类时才切换展开/收起状态
  if (hasSub()) {
    if (expandedCategories.value.includes(categoryPath)) {
      expandedCategories.value = expandedCategories.value.filter(cat => cat !== categoryPath);
    } else {
      expandedCategories.value.push(categoryPath);
    }
  }
};

const selectCategory = (categoryPath) => {
  selectedCategory.value = categoryPath;
  emit('filter-change', categoryPath);
  emit('category-select', categoryPath);
};

const clearFilter = () => {
  selectedCategory.value = '';
  emit('filter-change', '');
};

const getProductCount = (level1, level2 = null, level3 = null) => {
  let categoryPath = level1;
  if (level2) categoryPath += ` > ${level2}`;
  if (level3) categoryPath += ` > ${level3}`;

  return categoryProductCounts.value[categoryPath] || 0;
};

// 计算各分类的产品数量
const calculateCategoryCounts = () => {
  const counts = {};

  props.products.forEach(product => {
    const categoryPath = product.categoryPath || product.category || '';
    if (!categoryPath) return;

    // 为完整路径计数
    counts[categoryPath] = (counts[categoryPath] || 0) + 1;

    // 为各级父分类计数
    const parts = categoryPath.split(' > ');
    let currentPath = '';

    parts.forEach((part, index) => {
      currentPath = index === 0 ? part : `${currentPath} > ${part}`;
      counts[currentPath] = (counts[currentPath] || 0) + 1;
    });
  });

  categoryProductCounts.value = counts;
};

// 初始化时计算产品数量
onMounted(() => {
  calculateCategoryCounts();
});

// 监听产品列表变化
const { products } = toRefs(props);
watch(products, () => {
  calculateCategoryCounts();
}, { deep: true });
</script>

<style scoped>
.category-filter {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 20px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.filter-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.filter-header h4 i {
  margin-right: 8px;
  color: #667eea;
}

.btn-clear-filter {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 16px;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.btn-clear-filter:hover {
  background: #f8f9fa;
  color: #dc3545;
}

.category-tree {
  max-height: 600px;
  overflow-y: auto;
}

.category-node {
  margin-bottom: 2px;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s ease;
  font-size: 14px;
  line-height: 1.4;
}

.category-item:hover {
  background: #f8f9fa;
}

.category-item.active {
  background: #e3f2fd;
  color: #1976d2;
  font-weight: 500;
}

.expand-icon {
  margin-right: 8px;
  font-size: 12px;
  transition: transform 0.3s ease;
  color: #999;
}

.category-item.expanded .expand-icon {
  transform: rotate(90deg);
}

.category-icon {
  margin-right: 8px;
  font-size: 8px;
  color: #999;
}

.category-name {
  flex: 1;
  margin-right: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-count {
  display: none; /* 隐藏产品数量统计 */
}

.category-item.active .product-count {
  display: none; /* 隐藏产品数量统计 */
}

.subcategory-container {
  margin-left: 20px;
  border-left: 1px solid #e0e0e0;
  padding-left: 10px;
}

.level1 {
  font-weight: 600;
}

.level2 {
  font-weight: 500;
  font-size: 13px;
}

.level3 {
  font-weight: 400;
  font-size: 12px;
}

.filter-status {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.current-filter {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #e8f5e8;
  border-radius: 6px;
  font-size: 13px;
}

.current-filter i {
  margin-right: 8px;
  color: #28a745;
}

.filter-text {
  margin-right: 5px;
  color: #666;
}

.filter-category {
  font-weight: 600;
  color: #28a745;
}

/* 滚动条样式 */
.category-tree::-webkit-scrollbar {
  width: 6px;
}

.category-tree::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.category-tree::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.category-tree::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>