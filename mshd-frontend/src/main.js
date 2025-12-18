import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import * as echarts from 'echarts'

import App from './App.vue'
import router from './router'
import './assets/styles/global.scss'

// 加载中国地图数据
fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
  .then(response => response.json())
  .then(chinaJson => {
    echarts.registerMap('china', chinaJson)
    console.log('中国地图数据加载成功')
  })
  .catch(error => {
    console.warn('中国地图数据加载失败，地图功能可能无法使用:', error)
  })

const app = createApp(App)

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

app.mount('#app')

console.log('MSHD多源异构灾情数据管理服务系统已启动')
