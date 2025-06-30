import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/soup',
    name: 'soup-list',
    component: () => import('@/views/soup/SoupListView.vue'),
    meta: { title: '题目列表' }
  },
  {
    path: '/soup/:id',
    name: 'SoupDetail',
    component: () => import('@/views/soup/SoupDetailView.vue'),
    meta: { title: '题目详情' }
  },
  {
    path: '/game/:id',
    name: 'game',
    component: () => import('@/views/game/GameView.vue'),
    meta: { title: '游戏', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/user/ProfileView.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/AboutView.vue'),
    meta: { title: '关于' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 海龟汤` : '海龟汤'
  
  const userStore = useUserStore()
  
  // 初始化用户状态
  if (!userStore.isAuthenticated) {
    await userStore.initUserState()
  }
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    // 如果需要认证但未登录，跳转到登录页
    next('/login')
  } else {
    next()
  }
})

export default router
