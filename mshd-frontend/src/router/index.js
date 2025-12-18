import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/layout',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据概览', requiresAuth: true }
      },
      {
        path: '/data-screen',
        name: 'DataScreen',
        component: () => import('@/views/DataScreen.vue'),
        meta: { title: '数据大屏', requiresAuth: true }
      },
      {
        path: '/data-list',
        name: 'DataList',
        component: () => import('@/views/DataList.vue'),
        meta: { title: '灾情数据列表', requiresAuth: true }
      },
      {
        path: '/data-source',
        name: 'DataSource',
        component: () => import('@/views/DataSource.vue'),
        meta: { title: '数据源管理', requiresAuth: true }
      },
      {
        path: '/encoding',
        name: 'Encoding',
        component: () => import('@/views/Encoding.vue'),
        meta: { title: '数据编码', requiresAuth: true }
      },
      {
        path: '/visualization',
        name: 'Visualization',
        component: () => import('@/views/Visualization.vue'),
        meta: { title: '数据可视化', requiresAuth: true }
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue'),
        meta: { title: '统计分析', requiresAuth: true }
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

  const userStore = useUserStore()

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    // 已登录用户访问登录页，跳转到首页
    if (to.path === '/login' && userStore.isLoggedIn) {
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router
