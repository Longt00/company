<template>
  <div class="pagination-container">
    <!-- 页码导航区 -->
    <div class="pagination-nav">
      <!-- 左翻页按钮 -->
      <button
        class="pagination-btn prev-btn"
        :disabled="currentPage === 1"
        @click="goToPage(currentPage - 1)"
      >
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <path d="M10 4L6 8L10 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>

      <!-- 页码按钮 -->
      <div class="page-numbers">
        <button
          v-for="page in visiblePages"
          :key="page"
          class="page-btn"
          :class="{ active: page === currentPage }"
          @click="goToPage(page)"
        >
          <span v-if="page === '...'">...</span>
          <span v-else>{{ page }}</span>
        </button>
      </div>

      <!-- 右翻页按钮 -->
      <button
        class="pagination-btn next-btn"
        :disabled="currentPage === totalPages"
        @click="goToPage(currentPage + 1)"
      >
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
          <path d="M6 4L10 8L6 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>

    <!-- 页码跳转区 -->
    <div class="pagination-jump">
      <span class="jump-text">Go to</span>
      <input
        type="number"
        v-model.number="jumpPage"
        class="jump-input"
        :min="1"
        :max="totalPages"
        @keyup.enter="handleJump"
      />
      <button class="jump-btn" @click="handleJump">Go</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Pagination',
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      jumpPage: null
    }
  },
  computed: {
    visiblePages() {
      const pages = []
      const current = Math.max(1, this.currentPage) // 确保当前页至少为1
      const total = Math.max(1, this.totalPages) // 确保总页数至少为1

      if (total <= 7) {
        // 如果总页数不超过7页，显示所有页码
        for (let i = 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        // 总页数超过7页时的智能显示逻辑
        if (current <= 4) {
          // 当前页在前面，显示 1,2,3,4,5,...,total
          for (let i = 1; i <= 5; i++) {
            pages.push(i)
          }
          pages.push('...')
          pages.push(total)
        } else if (current >= total - 3) {
          // 当前页在后面，显示 1,...,total-4,total-3,total-2,total-1,total
          pages.push(1)
          pages.push('...')
          for (let i = total - 4; i <= total; i++) {
            pages.push(i)
          }
        } else {
          // 当前页在中间，显示 1,...,current-1,current,current+1,...,total
          pages.push(1)
          pages.push('...')
          for (let i = current - 1; i <= current + 1; i++) {
            pages.push(i)
          }
          pages.push('...')
          pages.push(total)
        }
      }

      return pages
    }
  },
  methods: {
    goToPage(page) {
      const totalPages = Math.max(1, this.totalPages)
      if (page >= 1 && page <= totalPages && page !== '...') {
        this.$emit('page-change', page)
      }
    },
    handleJump() {
      const totalPages = Math.max(1, this.totalPages)
      if (this.jumpPage && this.jumpPage >= 1 && this.jumpPage <= totalPages) {
        this.goToPage(this.jumpPage)
        this.jumpPage = null
      }
    }
  }
}
</script>

<style scoped>
/* 分页导航容器 */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 40px;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
}

/* 页码导航区 */
.pagination-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 分页按钮基础样式 */
.pagination-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #f5f5f5;
  border: 1px solid #8e8d8d;
  border-radius: 4px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #e9e9e9;
  border-color: #999;
}

.pagination-btn:disabled {
  background: #f9f9f9;
  color: #333;
  cursor: not-allowed;
  border-color: #eee;
}

/* 页码按钮容器 */
.page-numbers {
  display: flex;
  gap: 4px;
}

/* 页码按钮 */
.page-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  height: 40px;
  padding: 0 12px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover {
  background: #f8f9fa;
  border-color: #999;
}

.page-btn.active {
  background: #333;
  color: white;
  border-color: #333;
}

.page-btn span {
  color: inherit;
}

/* 省略号样式 */
.page-btn:has(span:not(.active)) {
  background: transparent;
  border: none;
  cursor: default;
  color: #999;
}

.page-btn:has(span:not(.active)):hover {
  background: transparent;
  border: none;
}

/* 页码跳转区 */
.pagination-jump {
  display: flex;
  align-items: center;
  gap: 8px;
}

.jump-text {
  font-size: 14px;
  color: #666;
}

.jump-input {
  width: 60px;
  height: 40px;
  padding: 0 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
  transition: border-color 0.3s ease;
}

.jump-input:focus {
  outline: none;
  border-color: #333;
}

.jump-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  height: 40px;
  background: white;
  border: 1px solid #333;
  border-radius: 4px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jump-btn:hover {
  background: #333;
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pagination-container {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
  }

  .pagination-nav {
    flex-wrap: nowrap;
    justify-content: center;
  }

  .page-numbers {
    flex-wrap: nowrap;
  }

  .pagination-btn,
  .page-btn {
    width: 32px;
    height: 32px;
    font-size: 12px;
    padding: 0 6px;
    min-width: 32px;
  }

  .pagination-jump {
    gap: 6px;
  }

  .jump-input {
    width: 40px;
    height: 32px;
    font-size: 12px;
  }

  .jump-btn {
    height: 32px;
    padding: 0 10px;
    font-size: 12px;
  }

  .jump-text {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .pagination-container {
    padding: 15px 0;
    gap: 8px;
  }

  .pagination-btn,
  .page-btn {
    width: 28px;
    height: 28px;
    font-size: 11px;
    min-width: 28px;
    padding: 0 4px;
  }

  .jump-input {
    width: 36px;
    height: 28px;
    font-size: 11px;
  }

  .jump-btn {
    height: 28px;
    padding: 0 8px;
    font-size: 11px;
  }

  .jump-text {
    font-size: 11px;
  }
}
</style>