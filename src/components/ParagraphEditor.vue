<template>
  <div class="paragraph-editor">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-section">
        <button @click="addParagraph" class="tool-btn add-btn">
          <span class="icon">+</span>
          添加段落
        </button>
        <button @click="clearAll" class="tool-btn clear-btn">
          <span class="icon">🗑</span>
          清空所有
        </button>
      </div>

      <div class="toolbar-section">
        <button @click="exportData" class="tool-btn export-btn">
          <span class="icon">💾</span>
          导出数据
        </button>
        <button @click="importData" class="tool-btn import-btn">
          <span class="icon">📁</span>
          导入数据
        </button>
        <input
          type="file"
          ref="fileInput"
          @change="handleFileImport"
          accept=".json"
          style="display: none"
        >
      </div>
    </div>

    <!-- 段落列表 -->
    <div class="paragraphs-container">
      <div
        v-for="(paragraph, index) in paragraphs"
        :key="paragraph.id"
        class="paragraph-wrapper"
      >
        <!-- 段落操作栏 -->
        <div class="paragraph-header">
          <span class="paragraph-number">段落 {{ index + 1 }}</span>
          <div class="paragraph-actions">
            <button @click="moveUp(index)" :disabled="index === 0" class="action-btn move-btn">
              ↑
            </button>
            <button @click="moveDown(index)" :disabled="index === paragraphs.length - 1" class="action-btn move-btn">
              ↓
            </button>
            <button @click="duplicateParagraph(index)" class="action-btn duplicate-btn">
              📋
            </button>
            <button @click="deleteParagraph(index)" class="action-btn delete-btn">
              ✕
            </button>
          </div>
        </div>

        <!-- 段落颜色选择器 -->
        <ParagraphColorPicker
          :initial-content="paragraph.content"
          :initial-color="paragraph.color"
          :ref="`picker-${paragraph.id}`"
          @content-change="updateParagraph(paragraph.id, $event)"
          @color-change="updateParagraphColor(paragraph.id, $event)"
          @apply-to-all="applyColorToAll($event)"
        />
      </div>

      <!-- 空状态 -->
      <div v-if="paragraphs.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>还没有任何段落</h3>
        <p>点击"添加段落"开始创建你的内容</p>
        <button @click="addParagraph" class="empty-action-btn">
          添加第一个段落
        </button>
      </div>
    </div>

    <!-- 预览区域 -->
    <div class="preview-section" v-if="paragraphs.length > 0">
      <div class="preview-header">
        <h3>预览效果</h3>
        <button @click="togglePreview" class="preview-toggle">
          {{ showPreview ? '隐藏预览' : '显示预览' }}
        </button>
      </div>
      <div v-if="showPreview" class="preview-content">
        <div
          v-for="paragraph in paragraphs"
          :key="`preview-${paragraph.id}`"
          class="preview-paragraph"
          :style="{ color: paragraph.color }"
        >
          {{ paragraph.content || '（空段落）' }}
        </div>
      </div>
    </div>

    <!-- 导入/导出确认对话框 -->
    <div v-if="showImportDialog" class="dialog-overlay" @click="closeImportDialog">
      <div class="dialog" @click.stop>
        <h3>导入数据</h3>
        <p>将覆盖当前所有段落，确定继续吗？</p>
        <div class="dialog-actions">
          <button @click="closeImportDialog" class="dialog-btn cancel">取消</button>
          <button @click="confirmImport" class="dialog-btn confirm">确定导入</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ParagraphColorPicker from './ParagraphColorPicker.vue';

export default {
  name: 'ParagraphEditor',
  components: {
    ParagraphColorPicker
  },

  data() {
    return {
      paragraphs: [],
      showPreview: true,
      showImportDialog: false,
      importData: null,
      nextId: 1
    };
  },

  mounted() {
    // 初始化时添加一个默认段落
    this.addParagraph();
    this.loadFromLocalStorage();
  },

  methods: {
    addParagraph() {
      const newParagraph = {
        id: this.nextId++,
        content: '',
        color: '#333333'
      };
      this.paragraphs.push(newParagraph);
      this.saveToLocalStorage();
    },

    deleteParagraph(index) {
      if (this.paragraphs.length > 1) {
        this.paragraphs.splice(index, 1);
        this.saveToLocalStorage();
      } else {
        // 至少保留一个段落
        this.paragraphs[index] = {
          id: this.nextId++,
          content: '',
          color: '#333333'
        };
        this.saveToLocalStorage();
      }
    },

    duplicateParagraph(index) {
      const original = this.paragraphs[index];
      const duplicate = {
        id: this.nextId++,
        content: original.content,
        color: original.color
      };
      this.paragraphs.splice(index + 1, 0, duplicate);
      this.saveToLocalStorage();
    },

    moveUp(index) {
      if (index > 0) {
        const temp = this.paragraphs[index];
        this.paragraphs[index] = this.paragraphs[index - 1];
        this.paragraphs[index - 1] = temp;
        this.saveToLocalStorage();
      }
    },

    moveDown(index) {
      if (index < this.paragraphs.length - 1) {
        const temp = this.paragraphs[index];
        this.paragraphs[index] = this.paragraphs[index + 1];
        this.paragraphs[index + 1] = temp;
        this.saveToLocalStorage();
      }
    },

    updateParagraph(id, data) {
      const paragraph = this.paragraphs.find(p => p.id === id);
      if (paragraph) {
        paragraph.content = data.content;
        paragraph.color = data.color;
        this.saveToLocalStorage();
      }
    },

    updateParagraphColor(id, color) {
      const paragraph = this.paragraphs.find(p => p.id === id);
      if (paragraph) {
        paragraph.color = color;
        this.saveToLocalStorage();
      }
    },

    applyColorToAll(color) {
      this.paragraphs.forEach(paragraph => {
        paragraph.color = color;
      });
      this.saveToLocalStorage();
    },

    clearAll() {
      if (confirm('确定要清空所有段落吗？')) {
        this.paragraphs = [{
          id: this.nextId++,
          content: '',
          color: '#333333'
        }];
        this.saveToLocalStorage();
      }
    },

    togglePreview() {
      this.showPreview = !this.showPreview;
    },

    exportData() {
      const data = {
        paragraphs: this.paragraphs,
        timestamp: new Date().toISOString(),
        version: '1.0'
      };

      const json = JSON.stringify(data, null, 2);
      const blob = new Blob([json], { type: 'application/json' });
      const url = URL.createObjectURL(blob);

      const a = document.createElement('a');
      a.href = url;
      a.download = `paragraphs_${new Date().getTime()}.json`;
      a.click();

      URL.revokeObjectURL(url);
    },

    importData() {
      this.$refs.fileInput.click();
    },

    handleFileImport(event) {
      const file = event.target.files[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const data = JSON.parse(e.target.result);
          if (data.paragraphs && Array.isArray(data.paragraphs)) {
            this.importData = data;
            this.showImportDialog = true;
          } else {
            alert('无效的数据格式');
          }
        } catch (error) {
          alert('文件解析失败：' + error.message);
        }
      };
      reader.readAsText(file);

      // 清空文件输入
      event.target.value = '';
    },

    confirmImport() {
      if (this.importData) {
        this.paragraphs = this.importData.paragraphs.map(p => ({
          ...p,
          id: this.nextId++
        }));
        this.saveToLocalStorage();
        this.closeImportDialog();
      }
    },

    closeImportDialog() {
      this.showImportDialog = false;
      this.importData = null;
    },

    saveToLocalStorage() {
      try {
        localStorage.setItem('paragraphEditorData', JSON.stringify(this.paragraphs));
      } catch (error) {
        console.error('保存数据失败:', error);
      }
    },

    loadFromLocalStorage() {
      try {
        const saved = localStorage.getItem('paragraphEditorData');
        if (saved) {
          const data = JSON.parse(saved);
          if (Array.isArray(data) && data.length > 0) {
            this.paragraphs = data.map(p => ({
              ...p,
              id: this.nextId++
            }));
          }
        }
      } catch (error) {
        console.error('加载数据失败:', error);
      }
    },

    getAllParagraphs() {
      return [...this.paragraphs];
    },

    setParagraphs(paragraphs) {
      this.paragraphs = paragraphs.map(p => ({
        ...p,
        id: this.nextId++
      }));
      this.saveToLocalStorage();
    }
  }
};
</script>

<style scoped>
.paragraph-editor {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.toolbar-section {
  display: flex;
  gap: 12px;
}

.tool-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.tool-btn:hover {
  transform: translateY(-1px);
}

.add-btn {
  background: #28a745;
  color: white;
}

.add-btn:hover {
  background: #218838;
}

.clear-btn {
  background: #dc3545;
  color: white;
}

.clear-btn:hover {
  background: #c82333;
}

.export-btn {
  background: #007bff;
  color: white;
}

.export-btn:hover {
  background: #0056b3;
}

.import-btn {
  background: #6c757d;
  color: white;
}

.import-btn:hover {
  background: #545b62;
}

.icon {
  font-size: 16px;
}

.paragraphs-container {
  padding: 20px;
}

.paragraph-wrapper {
  margin-bottom: 24px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.paragraph-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.paragraph-number {
  font-weight: 600;
  color: #333;
}

.paragraph-actions {
  display: flex;
  gap: 6px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.move-btn {
  background: #e9ecef;
  color: #495057;
}

.move-btn:hover:not(:disabled) {
  background: #dee2e6;
}

.duplicate-btn {
  background: #17a2b8;
  color: white;
}

.duplicate-btn:hover {
  background: #138496;
}

.delete-btn {
  background: #dc3545;
  color: white;
}

.delete-btn:hover {
  background: #c82333;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  color: #495057;
}

.empty-state p {
  margin: 0 0 24px 0;
}

.empty-action-btn {
  padding: 12px 24px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: background 0.2s ease;
}

.empty-action-btn:hover {
  background: #0056b3;
}

.preview-section {
  border-top: 1px solid #e0e0e0;
  background: #f8f9fa;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
}

.preview-header h3 {
  margin: 0;
  color: #333;
}

.preview-toggle {
  padding: 6px 12px;
  background: #e9ecef;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s ease;
}

.preview-toggle:hover {
  background: #dee2e6;
}

.preview-content {
  padding: 0 20px 20px;
}

.preview-paragraph {
  padding: 16px;
  margin-bottom: 12px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  line-height: 1.6;
  min-height: 20px;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 24px;
  border-radius: 8px;
  max-width: 400px;
  width: 90%;
}

.dialog h3 {
  margin: 0 0 12px 0;
  color: #333;
}

.dialog p {
  margin: 0 0 20px 0;
  color: #6c757d;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.dialog-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.dialog-btn.cancel {
  background: #e9ecef;
  color: #495057;
}

.dialog-btn.cancel:hover {
  background: #dee2e6;
}

.dialog-btn.confirm {
  background: #007bff;
  color: white;
}

.dialog-btn.confirm:hover {
  background: #0056b3;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .toolbar-section {
    justify-content: center;
  }

  .paragraph-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .paragraph-actions {
    justify-content: center;
  }

  .preview-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
}
</style>