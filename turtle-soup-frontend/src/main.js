import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './styles/index.scss'

// 处理 ResizeObserver 错误
const resizeHandler = () => {
  const debounce = (func, wait) => {
    let timeout
    return function executedFunction(...args) {
      const later = () => {
        clearTimeout(timeout)
        func(...args)
      }
      clearTimeout(timeout)
      timeout = setTimeout(later, wait)
    }
  }

  const _ResizeObserver = window.ResizeObserver
  window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
    constructor(callback) {
      callback = debounce(callback, 16)
      super(callback)
    }
  }
}

resizeHandler()

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 全局修复 getComputedStyle 报错，防止 Element Plus el-select 销毁后访问 DOM 时出错
const originGetComputedStyle = window.getComputedStyle
window.getComputedStyle = function(element, ...args) {
  if (!(element instanceof Element)) return {}
  return originGetComputedStyle.call(window, element, ...args)
}

app.mount('#app')
