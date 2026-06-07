import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/detection/data'
  },
  {
    path: '/detection/data',
    name: 'DataRecord',
    component: () => import('@/views/detection/DataRecord.vue'),
    meta: { title: '检测数据录入' }
  },
  {
    path: '/detection/data/edit/:id',
    name: 'DataRecordEdit',
    component: () => import('@/views/detection/DataRecordEdit.vue'),
    meta: { title: '检测数据录入-编辑' }
  },
  {
    path: '/detection/original',
    name: 'OriginalRecord',
    component: () => import('@/views/detection/OriginalRecord.vue'),
    meta: { title: '原始记录管理' }
  },
  {
    path: '/detection/original/edit/:id',
    name: 'OriginalRecordEdit',
    component: () => import('@/views/detection/OriginalRecordEdit.vue'),
    meta: { title: '原始记录-编辑' }
  },
  {
    path: '/detection/review',
    name: 'DataReview',
    component: () => import('@/views/detection/DataReview.vue'),
    meta: { title: '数据审核' }
  },
  {
    path: '/detection/trace',
    name: 'DataTrace',
    component: () => import('@/views/detection/DataTrace.vue'),
    meta: { title: '数据溯源' }
  },
  {
    path: '/detection/oos',
    name: 'OosRecord',
    component: () => import('@/views/detection/OosRecord.vue'),
    meta: { title: '超标预警(OOS)' }
  },
  {
    path: '/detection/oos/edit/:id',
    name: 'OosRecordEdit',
    component: () => import('@/views/detection/OosRecordEdit.vue'),
    meta: { title: 'OOS详情' }
  },
  {
    path: '/detection/quality/rule',
    name: 'QualityControlRule',
    component: () => import('@/views/detection/QualityControlRule.vue'),
    meta: { title: '质控规则配置' }
  },
  {
    path: '/detection/quality/sample',
    name: 'QualityControlSample',
    component: () => import('@/views/detection/QualityControlSample.vue'),
    meta: { title: '质控样品管理' }
  },
  {
    path: '/detection/quality/plan',
    name: 'QualityControlPlan',
    component: () => import('@/views/detection/QualityControlPlan.vue'),
    meta: { title: '质控计划' }
  },
  {
    path: '/detection/quality/chart',
    name: 'QualityControlChart',
    component: () => import('@/views/detection/QualityControlChart.vue'),
    meta: { title: '质控图分析' }
  },
  {
    path: '/detection/proficiency',
    name: 'ProficiencyTest',
    component: () => import('@/views/detection/ProficiencyTest.vue'),
    meta: { title: '能力验证与比对' }
  },
  {
    path: '/detection/standardCurve',
    name: 'StandardCurve',
    component: () => import('@/views/detection/StandardCurve.vue'),
    meta: { title: '标准曲线管理' }
  },
  {
    path: '/detection/stability',
    name: 'StabilityTest',
    component: () => import('@/views/detection/StabilityTest.vue'),
    meta: { title: '稳定性考察' }
  },
  {
    path: '/detection/capa',
    name: 'CapaManagement',
    component: () => import('@/views/detection/CapaManagement.vue'),
    meta: { title: 'CAPA管理' }
  },
  {
    path: '/reagent/ledger',
    name: 'ReagentLedger',
    component: () => import('@/views/reagent/ReagentLedger.vue'),
    meta: { title: '试剂台账' }
  },
  {
    path: '/reagent/standard',
    name: 'StandardMaterial',
    component: () => import('@/views/reagent/StandardMaterial.vue'),
    meta: { title: '标准物质/标准溶液' }
  },
  {
    path: '/reagent/consumable',
    name: 'ConsumableManagement',
    component: () => import('@/views/reagent/ConsumableManagement.vue'),
    meta: { title: '耗材管理' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - LIMS` : 'LIMS 环境检测实验室信息管理系统'
  next()
})

export default router
