<template>
  <div class="text-color-manage">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2>文字颜色管理</h2>
        <p>为网站内容设置独立的文字颜色</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog = true">
          <i class="el-icon-plus"></i> 新增段落
        </el-button>
        <el-button @click="refreshData">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
    </div>

    <!-- 段落列表 -->
    <div class="paragraphs-list">
      <el-table :data="paragraphs" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="段落内容" min-width="300">
          <template slot-scope="scope">
            <div class="content-preview">
              <div
                class="text-preview"
                :style="{ color: scope.row.color }"
              >
                {{ scope.row.content || '(空段落)' }}
              </div>
              <div class="content-meta">
                <span class="length-info">{{ scope.row.content.length }} 字符</span>
                <span class="color-info" :style="{ color: scope.row.color }">
                  {{ scope.row.color }}
                </span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="文字颜色" width="200">
          <template slot-scope="scope">
            <div class="color-display">
              <div
                class="color-box"
                :style="{ backgroundColor: scope.row.color }"
                @click="showColorPicker(scope.row)"
              ></div>
              <span class="color-text">{{ scope.row.color }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="editParagraph(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="toggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="deleteParagraph(scope.row)"
              style="color: #f56c6c"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑段落对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="showAddDialog"
      width="60%"
      @close="resetForm"
    >
      <el-form
        ref="paragraphForm"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="段落内容" prop="content">
          <el-input
            type="textarea"
            v-model="formData.content"
            :rows="6"
            placeholder="请输入段落内容"
          ></el-input>
        </el-form-item>

        <el-form-item label="文字颜色" prop="color">
          <div class="color-picker-container">
            <!-- 颜色预览 -->
            <div class="color-preview-wrapper">
              <span
                class="color-preview-text"
                :style="{ color: formData.color }"
              >
                预览文字效果
              </span>
              <div
                class="color-preview-box"
                :style="{ backgroundColor: formData.color }"
              ></div>
            </div>

            <!-- 颜色选择器 -->
            <div class="color-selector">
              <!-- 预设颜色 -->
              <div class="preset-colors">
                <div class="color-section-title">快速选择</div>
                <div class="color-grid">
                  <div
                    v-for="color in presetColors"
                    :key="color"
                    class="color-option"
                    :style="{ backgroundColor: color }"
                    :class="{ active: formData.color === color }"
                    @click="selectColor(color)"
                    :title="color"
                  ></div>
                </div>
              </div>

              <!-- 自定义颜色 -->
              <div class="custom-color">
                <div class="color-section-title">自定义颜色</div>
                <el-input
                  v-model="formData.color"
                  placeholder="#000000"
                  class="color-input"
                >
                  <el-color-picker
                    slot="append"
                    v-model="formData.color"
                    show-alpha
                    :predefine="presetColors"
                  ></el-color-picker>
                </el-input>
              </div>

              <!-- 常用文字颜色 -->
              <div class="common-colors">
                <div class="color-section-title">常用文字颜色</div>
                <div class="common-color-list">
                  <div
                    v-for="textColor in textPresets"
                    :key="textColor.name"
                    class="common-color-item"
                    :class="{ active: formData.color === textColor.color }"
                    @click="selectColor(textColor.color)"
                  >
                    <span
                      class="common-color-preview"
                      :style="{ color: textColor.color }"
                    >
                      {{ textColor.name }}
                    </span>
                    <span class="common-color-value">{{ textColor.color }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            placeholder="可选的备注信息"
          ></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveParagraph" :loading="saving">
          {{ formData.id ? '更新' : '新增' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 快速颜色选择对话框 -->
    <el-dialog
      title="快速颜色选择"
      :visible.sync="showQuickColorPicker"
      width="400px"
    >
      <div v-if="currentEditingParagraph">
        <div class="quick-color-content">
          <div class="current-info">
            <p>当前段落：</p>
            <div class="content-display">{{ currentEditingParagraph.content || '(空段落)' }}</div>
            <p>当前颜色：</p>
            <div class="color-display-large">
              <div
                class="color-box-large"
                :style="{ backgroundColor: currentEditingParagraph.color }"
              ></div>
              <span>{{ currentEditingParagraph.color }}</span>
            </div>
          </div>

          <div class="quick-color-grid">
            <div
              v-for="color in presetColors"
              :key="color"
              class="quick-color-item"
              :style="{ backgroundColor: color }"
              @click="quickChangeColor(color)"
              :title="color"
            ></div>
          </div>

          <div class="custom-color-quick">
            <el-input
              v-model="quickColorValue"
              placeholder="输入颜色值 (#000000)"
              class="color-input"
            >
              <el-color-picker
                slot="append"
                v-model="quickColorValue"
                show-alpha
              ></el-color-picker>
            </el-input>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="showQuickColorPicker = false">取消</el-button>
        <el-button type="primary" @click="applyQuickColor">应用颜色</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getTextColorList, saveTextColor, deleteTextColor, updateTextColorStatus } from '@/api/textColor'

export default {
  name: 'TextColorManage',
  data() {
    return {
      paragraphs: [],
      loading: false,
      saving: false,
      showAddDialog: false,
      showQuickColorPicker: false,
      currentEditingParagraph: null,
      quickColorValue: '',

      // 表单数据
      formData: {
        id: null,
        content: '',
        color: '#333333',
        status: 1,
        remark: ''
      },

      // 表单验证规则
      formRules: {
        content: [
          { required: true, message: '请输入段落内容', trigger: 'blur' },
          { max: 2000, message: '内容不能超过2000字符', trigger: 'blur' }
        ],
        color: [
          { required: true, message: '请选择文字颜色', trigger: 'blur' },
          { pattern: /^#[0-9A-Fa-f]{6}$/, message: '请输入有效的颜色值', trigger: 'blur' }
        ]
      },

      // 预设颜色
      presetColors: [
        '#000000', '#ffffff', '#ff0000', '#00ff00', '#0000ff',
        '#ffff00', '#ff00ff', '#00ffff', '#ff6b6b', '#4ecdc4',
        '#45b7d1', '#96ceb4', '#ffeaa7', '#dfe6e9', '#74b9ff',
        '#a29bfe', '#6c5ce7', '#fd79a8', '#fdcb6e', '#e17055',
        '#2d3436', '#636e72', '#b2bec3', '#00b894', '#00cec9',
        '#0984e3', '#6c5ce7', '#a29bfe', '#fd79a8', '#fdcb6e'
      ],

      // 常用文字颜色
      textPresets: [
        { name: '默认黑', color: '#333333' },
        { name: '深灰', color: '#666666' },
        { name: '浅灰', color: '#999999' },
        { name: '蓝色', color: '#1976d2' },
        { name: '红色', color: '#dc3545' },
        { name: '绿色', color: '#28a745' },
        { name: '橙色', color: '#ff6a00' },
        { name: '紫色', color: '#6c5ce7' }
      ]
    }
  },

  computed: {
    dialogTitle() {
      return this.formData.id ? '编辑段落' : '新增段落'
    }
  },

  mounted() {
    this.fetchData()
  },

  methods: {
    // 获取数据
    async fetchData() {
      this.loading = true
      try {
        const response = await getTextColorList()
        this.paragraphs = response.data || []
      } catch (error) {
        this.$message.error('获取数据失败：' + error.message)
      } finally {
        this.loading = false
      }
    },

    // 刷新数据
    refreshData() {
      this.fetchData()
    },

    // 重置表单
    resetForm() {
      this.formData = {
        id: null,
        content: '',
        color: '#333333',
        status: 1,
        remark: ''
      }
      if (this.$refs.paragraphForm) {
        this.$refs.paragraphForm.resetFields()
      }
    },

    // 编辑段落
    editParagraph(paragraph) {
      this.formData = {
        id: paragraph.id,
        content: paragraph.content,
        color: paragraph.color,
        status: paragraph.status,
        remark: paragraph.remark || ''
      }
      this.showAddDialog = true
    },

    // 保存段落
    async saveParagraph() {
      try {
        await this.$refs.paragraphForm.validate()
        this.saving = true

        await saveTextColor(this.formData)
        this.$message.success(this.formData.id ? '更新成功' : '新增成功')
        this.showAddDialog = false
        this.fetchData()
      } catch (error) {
        if (error.message) {
          this.$message.error('保存失败：' + error.message)
        }
      } finally {
        this.saving = false
      }
    },

    // 删除段落
    async deleteParagraph(paragraph) {
      try {
        await this.$confirm('确定要删除这个段落吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await deleteTextColor(paragraph.id)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败：' + error.message)
        }
      }
    },

    // 切换状态
    async toggleStatus(paragraph) {
      try {
        const newStatus = paragraph.status === 1 ? 0 : 1
        await updateTextColorStatus(paragraph.id, newStatus)
        paragraph.status = newStatus
        this.$message.success(newStatus === 1 ? '已启用' : '已禁用')
      } catch (error) {
        this.$message.error('操作失败：' + error.message)
      }
    },

    // 显示颜色选择器
    showColorPicker(paragraph) {
      this.currentEditingParagraph = paragraph
      this.quickColorValue = paragraph.color
      this.showQuickColorPicker = true
    },

    // 选择颜色
    selectColor(color) {
      this.formData.color = color
    },

    // 快速改变颜色
    quickChangeColor(color) {
      this.quickColorValue = color
    },

    // 应用快速颜色
    async applyQuickColor() {
      if (!this.currentEditingParagraph || !this.quickColorValue) {
        return
      }

      try {
        await saveTextColor({
          id: this.currentEditingParagraph.id,
          content: this.currentEditingParagraph.content,
          color: this.quickColorValue,
          status: this.currentEditingParagraph.status
        })

        this.currentEditingParagraph.color = this.quickColorValue
        this.showQuickColorPicker = false
        this.$message.success('颜色更新成功')
      } catch (error) {
        this.$message.error('颜色更新失败：' + error.message)
      }
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.text-color-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.header-content h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.header-content p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.content-preview {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.text-preview {
  font-size: 14px;
  line-height: 1.5;
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.content-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}

.length-info {
  color: #909399;
}

.color-info {
  font-family: monospace;
  font-weight: 600;
}

.color-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-box {
  width: 24px;
  height: 24px;
  border: 2px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-box:hover {
  border-color: #409eff;
  transform: scale(1.1);
}

.color-text {
  font-family: monospace;
  font-size: 12px;
  color: #606266;
}

/* 颜色选择器样式 */
.color-picker-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.color-preview-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.color-preview-text {
  font-size: 16px;
  font-weight: 500;
}

.color-preview-box {
  width: 40px;
  height: 40px;
  border: 2px solid #e4e7ed;
  border-radius: 6px;
}

.color-selector {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.color-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.preset-colors .color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(32px, 1fr));
  gap: 8px;
}

.color-option {
  width: 32px;
  height: 32px;
  border: 2px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-option:hover {
  transform: scale(1.1);
  border-color: #409eff;
}

.color-option.active {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3);
}

.custom-color .color-input {
  max-width: 300px;
}

.common-colors .common-color-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 8px;
}

.common-color-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: #fff;
  border: 2px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.common-color-item:hover {
  border-color: #409eff;
  transform: translateY(-1px);
}

.common-color-item.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.common-color-preview {
  font-size: 12px;
  font-weight: 600;
}

.common-color-value {
  font-size: 10px;
  color: #909399;
  font-family: monospace;
}

/* 快速颜色选择对话框 */
.quick-color-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.current-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.current-info p {
  margin: 0;
  font-weight: 600;
  color: #303133;
}

.content-display {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
}

.color-display-large {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-box-large {
  width: 48px;
  height: 48px;
  border: 3px solid #e4e7ed;
  border-radius: 8px;
}

.quick-color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
  gap: 10px;
}

.quick-color-item {
  width: 40px;
  height: 40px;
  border: 2px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-color-item:hover {
  transform: scale(1.1);
  border-color: #409eff;
}

.custom-color-quick .color-input {
  margin-top: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .header-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }

  .preset-colors .color-grid {
    grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
  }

  .color-option {
    width: 40px;
    height: 40px;
  }

  .common-colors .common-color-list {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .quick-color-grid {
    grid-template-columns: repeat(auto-fill, minmax(35px, 1fr));
  }

  .quick-color-item {
    width: 35px;
    height: 35px;
  }
}
</style>