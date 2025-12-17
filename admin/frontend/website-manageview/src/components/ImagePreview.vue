<template>
  <div class="image-preview-overlay" @click="$emit('close')">
    <div class="preview-content" @click.stop>
      <img v-if="image" :src="image" alt="预览">
      <div v-else class="no-image">
        <i class="bi bi-image"></i>
        <p>无图片</p>
      </div>
      <button class="close-btn" @click="$emit('close')">
        <i class="bi bi-x-lg"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  image: {
    type: String,
    default: ''
  }
})

defineEmits(['close'])
</script>

<style scoped>
.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s;
}

.preview-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.preview-content img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: white;
}

.no-image i {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.no-image p {
  margin: 0;
  font-size: 20px;
  opacity: 0.7;
}

.close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
