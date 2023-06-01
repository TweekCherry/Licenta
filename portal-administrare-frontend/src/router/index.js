import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: {
      authenticationRequired: true
    },
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/pages/Home.vue'),
        meta: {
          authenticationRequired: true
        }
      },
      {
        path: '/investigation-types',
        name: 'InvestigationTypes',
        component: () => import('@/pages/InvestigationsTypes.vue'),
        meta: {
          authenticationRequired: true
        }
      },
      {
        path: '/investigations',
        name: 'Investigations',
        component: () => import('@/pages/Investigations.vue'),
        meta: {
          authenticationRequired: true
        }
      },
      {
        path: '/subscriptions',
        name: 'Subscriptions',
        component: () => import('@/pages/Subscriptions.vue'),
        meta: {
          authenticationRequired: true
        }
      },
      {
        path: '/clinics',
        name: 'Clinics',
        component: () => import('@/pages/Clinics.vue'),
        meta: {
          authenticationRequired: true
        }
      },
      {
        path: '/departments',
        name: 'Departments',
        component: () => import('@/pages/Departments.vue'),
        meta: {
          authenticationRequired: true
        }
      },
      {
        path: '/physicians',
        name: 'Physicians',
        component: () => import('@/pages/Physicians.vue'),
        meta: {
          authenticationRequired: true
        }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/Login.vue')
  },
  {
    path: '/logout',
    name: 'Logout'
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/pages/ForgotPassword.vue')
  },
  {
    path: '/forgot-password-complete',
    name: 'ForgotPasswordComplete',
    component: () => import('@/pages/ForgotPasswordComplete.vue')
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
router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.authenticationRequired) && store.state.apiToken === null) { // if we're not logged in, go to login page
    return next({ name: 'Login' })
  }

  if (to.name === 'Login' && store.state.apiToken !== null) {
    return next({ name: 'Home' })
  }

  if (to.name === 'Logout') {
    store.commit('logout')
    return next({ name: 'Login' })
  }
  if (to.name === 'Layout') {
    return next({ name: 'Home' })
  }
  return next()
})

export default router
