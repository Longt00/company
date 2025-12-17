<template>
  <router-view />
</template>

<script setup>
</script>

<style>
#app {
  height: 100vh;
  width: 100vw;
  background-color: #ffffff;
  overflow: hidden;
  position: relative;
}

/* 确保整个应用区域都是白色背景，避免滚动时显示其他背景 */
* {
  box-sizing: border-box;
}

body {
  background-color: #ffffff !important;
  overflow: hidden !important;
}

html {
  background-color: #ffffff !important;
}
</style>