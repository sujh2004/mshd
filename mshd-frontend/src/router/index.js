import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/layout',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: '/data-screen',
        name: 'DataScreen',
        component: () => import('@/views/DataScreen.vue'),
        meta: { title: '数据大屏' }
      },
      {
        path: '/data-list',
        name: 'DataList',
        component: () => import('@/views/DataList.vue'),
        meta: { title: '灾情数据列表' }
      },
      {
        path: '/data-source',
        name: 'DataSource',
        component: () => import('@/views/DataSource.vue'),
        meta: { title: '数据源管理' }
      },
      {
        path: '/encoding',
        name: 'Encoding',
        component: () => import('@/views/Encoding.vue'),
        meta: { title: '数据编码' }
      },
      {
        path: '/visualization',
        name: 'Visualization',
        component: () => import('@/views/Visualization.vue'),
        meta: { title: '数据可视化' }
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue'),
        meta: { title: '统计分析' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - MSHD`
  }
  next()
})

export default router
