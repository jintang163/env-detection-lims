import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/storage'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard/index.vue'),
        meta: { title: '首页', requiresAuth: true, icon: 'HomeOutlined' }
      },
      {
        path: 'customer',
        name: 'Customer',
        component: () => import('@/views/Customer/index.vue'),
        meta: { title: '客户管理', requiresAuth: true, icon: 'UserOutlined' }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: () => import('@/views/Contract/index.vue'),
        meta: { title: '合同管理', requiresAuth: true, icon: 'FileTextOutlined' }
      },
      {
        path: 'entrust',
        name: 'Entrust',
        component: () => import('@/views/Entrust/index.vue'),
        meta: { title: '委托单管理', requiresAuth: true, icon: 'FormOutlined' }
      },
      {
        path: 'quotation',
        name: 'Quotation',
        component: () => import('@/views/Quotation/index.vue'),
        meta: { title: '报价单管理', requiresAuth: true, icon: 'DollarOutlined' }
      },
      {
        path: 'sample-register',
        name: 'SampleRegister',
        component: () => import('@/views/SampleRegister/index.vue'),
        meta: { title: '样品登记', requiresAuth: true, icon: 'FormOutlined' }
      },
      {
        path: 'sample-label',
        name: 'SampleLabel',
        component: () => import('@/views/SampleLabel/index.vue'),
        meta: { title: '样品标识', requiresAuth: true, icon: 'QrcodeOutlined' }
      },
      {
        path: 'sample-storage',
        name: 'SampleStorage',
        component: () => import('@/views/SampleStorage/index.vue'),
        meta: { title: '样品存储', requiresAuth: true, icon: 'DatabaseOutlined' }
      },
      {
        path: 'sample-transfer',
        name: 'SampleTransferTrace',
        component: () => import('@/views/SampleTransferTrace/index.vue'),
        meta: { title: '样品流转跟踪', requiresAuth: true, icon: 'SwapOutlined' }
      },
      {
        path: 'sample-retain',
        name: 'SampleRetain',
        component: () => import('@/views/SampleRetain/index.vue'),
        meta: { title: '留样管理', requiresAuth: true, icon: 'SaveOutlined' }
      },
      {
        path: 'sample-disposal',
        name: 'SampleDisposal',
        component: () => import('@/views/SampleDisposal/index.vue'),
        meta: { title: '样品处置', requiresAuth: true, icon: 'DeleteOutlined' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound/index.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const whiteList = ['/login']

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title as string) || 'LIMS系统'
  const token = getToken()
  
  if (token) {
    if (to.path === '/login') {
      next('/')
    } else {
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`)
    }
  }
})

export default router
