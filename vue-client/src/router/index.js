import { createRouter, createWebHistory } from 'vue-router'

// Import page components
const HomePage = () => import('@/pages/HomePage.vue')
const LoginPage = () => import('@/pages/LoginPage.vue')
const GamePage = () => import('@/pages/GamePage.vue')
const RoomListPage = () => import('@/pages/RoomListPage.vue')

// Create router
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
      meta: { requiresAuth: false }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
      meta: { requiresAuth: false }
    },
    {
      path: '/rooms',
      name: 'rooms',
      component: RoomListPage,
      meta: { requiresAuth: true }
    },
    {
      path: '/game/:roomId',
      name: 'game',
      component: GamePage,
      meta: { requiresAuth: true }
    },
    {
      path: '/:catchAll(.*)',
      redirect: '/'
    }
  ]
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token') !== null
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router