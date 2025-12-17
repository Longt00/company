import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 引入Bootstrap CSS和JS
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'

// 引入全局样式
import './assets/main.css'

// 引入端口修正工具（必须在其他导入之后）
import './utils/portFixer'

const app = createApp(App)
app.use(router)
app.mount('#app')