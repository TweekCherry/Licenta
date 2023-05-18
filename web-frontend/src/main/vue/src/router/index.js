import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '/profile',
        name: 'profile',
        component: () => import('@/pages/Profile.vue')
      },
      {
        path: '/support',
        name: 'Support',
        component: () => import('@/pages/Support.vue')
      },
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/pages/Home.vue')
      },
      {
        path: '/consultations',
        name: 'Consultations',
        component: () => import('@/pages/Consultations.vue')
      },
      {
        path: '/analysis',
        name: 'Analysis',
        component: () => import('@/pages/Analysis.vue')
      },
      {
        path: '/appointments',
        name: 'Appointments',
        component: () => import('@/pages/Appointments.vue')
      },
      {
        path: '/subscriptions',
        name: 'Subscriptions',
        component: () => import('@/pages/Subscriptions.vue')
      },
      {
        path: '/physicians',
        name: 'Physicians',
        component: () => import('@/pages/Physicians.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/Login.vue')
  },
  {
    path: '/forgot-password',
    name: 'forgot-password',
    component: () => import('@/pages/ForgotPassword.vue')
  },
  {
    path: '/:catchAll(.*)*',
    name: '404',
    component: () => import('@/pages/404.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  scrollBehavior: () => ({ left: 0, top: 0 }),
  routes
})

export default router
