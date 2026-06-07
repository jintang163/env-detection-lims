import request from '@/utils/request'

export const dataRecordApi = {
  page: (params) => request.get('/detection/dataRecord/page', { params }),
  get: (id) => request.get(`/detection/dataRecord/${id}`),
  getForm: (methodId) => request.get(`/detection/dataRecord/form/${methodId}`),
  validate: (data) => request.post('/detection/dataRecord/validate', data),
  save: (data) => request.post('/detection/dataRecord', data),
  update: (data) => request.put('/detection/dataRecord', data),
  submit: (id) => request.post(`/detection/dataRecord/submit/${id}`),
  delete: (id) => request.delete(`/detection/dataRecord/${id}`),
  import: (data) => request.post('/detection/dataRecord/import', null, { params: data }),
  verify: (id) => request.get(`/detection/dataRecord/verify/${id}`),
  getHash: (id) => request.get(`/detection/dataRecord/hash/${id}`)
}

export const originalRecordApi = {
  page: (params) => request.get('/detection/originalRecord/page', { params }),
  get: (id) => request.get(`/detection/originalRecord/${id}`),
  save: (data) => request.post('/detection/originalRecord', data),
  update: (data) => request.put('/detection/originalRecord', data),
  submit: (id) => request.post(`/detection/originalRecord/submit/${id}`),
  archive: (id) => request.post(`/detection/originalRecord/archive/${id}`),
  delete: (id) => request.delete(`/detection/originalRecord/${id}`),
  preview: (id) => request.get(`/detection/originalRecord/preview/${id}`),
  checkPermission: (id, params) => request.get(`/detection/originalRecord/permission/${id}`, { params }),
  verify: (id) => request.get(`/detection/originalRecord/verify/${id}`)
}

export const dataReviewApi = {
  list: (dataRecordId) => request.get(`/detection/dataReview/list/${dataRecordId}`),
  firstReview: (data) => request.post('/detection/dataReview/firstReview', data),
  secondReview: (data) => request.post('/detection/dataReview/secondReview', data),
  reject: (params) => request.post('/detection/dataReview/reject', null, { params }),
  myTasks: (params) => request.get('/detection/dataReview/myTasks', { params }),
  pendingCount: (params) => request.get('/detection/dataReview/pendingCount', { params })
}

export const oosRecordApi = {
  page: (params) => request.get('/detection/oosRecord/page', { params }),
  get: (id) => request.get(`/detection/oosRecord/${id}`),
  save: (data) => request.post('/detection/oosRecord', data),
  autoCreate: (params) => request.post('/detection/oosRecord/autoCreate', null, { params }),
  startInvestigation: (data) => request.post('/detection/oosRecord/startInvestigation', data),
  completeInvestigation: (data) => request.post('/detection/oosRecord/completeInvestigation', data),
  review: (data) => request.post('/detection/oosRecord/review', data),
  close: (data) => request.post('/detection/oosRecord/close', data),
  assign: (params) => request.post('/detection/oosRecord/assign', null, { params }),
  pendingInvestigation: () => request.get('/detection/oosRecord/pendingInvestigation'),
  pendingReview: () => request.get('/detection/oosRecord/pendingReview'),
  pendingInvestigationCount: () => request.get('/detection/oosRecord/stats/pendingInvestigationCount'),
  pendingReviewCount: () => request.get('/detection/oosRecord/stats/pendingReviewCount'),
  openCount: () => request.get('/detection/oosRecord/stats/openCount')
}

export const dataTraceApi = {
  getTree: (params) => request.get('/detection/dataTrace/tree', { params }),
  getTreeFromSample: (sampleId) => request.get(`/detection/dataTrace/tree/sample/${sampleId}`),
  getTreeFromTask: (taskId) => request.get(`/detection/dataTrace/tree/task/${taskId}`),
  getTreeFromEquipment: (equipmentId) => request.get(`/detection/dataTrace/tree/equipment/${equipmentId}`),
  build: (dataRecordId) => request.post(`/detection/dataTrace/build/${dataRecordId}`),
  getRelations: (params) => request.get('/detection/dataTrace/relations', { params }),
  getReverseRelations: (params) => request.get('/detection/dataTrace/reverseRelations', { params })
}

export const formFieldApi = {
  getByMethodId: (methodId) => request.get(`/detection/formField/method/${methodId}`),
  getByMethodCode: (methodCode) => request.get(`/detection/formField/methodCode/${methodCode}`),
  save: (data) => request.post('/detection/formField', data),
  update: (data) => request.put('/detection/formField', data),
  delete: (id) => request.delete(`/detection/formField/${id}`)
}

export const qualityControlApi = {
  rulePage: (params) => request.get('/detection/qualityControl/rule/page', { params }),
  ruleStats: () => request.get('/detection/qualityControl/rule/stats'),
  getRule: (id) => request.get(`/detection/qualityControl/rule/${id}`),
  getEnabledRules: () => request.get('/detection/qualityControl/rule/enabled'),
  saveRule: (data) => request.post('/detection/qualityControl/rule', data),
  updateRule: (data) => request.put('/detection/qualityControl/rule', data),
  deleteRule: (id) => request.delete(`/detection/qualityControl/rule/${id}`),
  toggleRule: (id, enabled) => request.put(`/detection/qualityControl/rule/${id}/toggle`, null, { params: { enabled } }),

  samplePage: (params) => request.get('/detection/qualityControl/sample/page', { params }),
  sampleStats: () => request.get('/detection/qualityControl/sample/stats'),
  getSample: (id) => request.get(`/detection/qualityControl/sample/${id}`),
  getValidSamples: () => request.get('/detection/qualityControl/sample/valid'),
  saveSample: (data) => request.post('/detection/qualityControl/sample', data),
  updateSample: (data) => request.put('/detection/qualityControl/sample', data),
  deleteSample: (id) => request.delete(`/detection/qualityControl/sample/${id}`),

  preparePage: (params) => request.get('/detection/qualityControl/prepare/page', { params }),
  getPrepare: (id) => request.get(`/detection/qualityControl/prepare/${id}`),
  savePrepare: (data) => request.post('/detection/qualityControl/prepare', data),

  planPage: (params) => request.get('/detection/qualityControl/plan/page', { params }),
  planStats: () => request.get('/detection/qualityControl/plan/stats'),
  getPlan: (id) => request.get(`/detection/qualityControl/plan/${id}`),
  savePlan: (data) => request.post('/detection/qualityControl/plan', data),
  updatePlan: (data) => request.put('/detection/qualityControl/plan', data),
  deletePlan: (id) => request.delete(`/detection/qualityControl/plan/${id}`),
  pausePlan: (id) => request.put(`/detection/qualityControl/plan/${id}/pause`),
  resumePlan: (id) => request.put(`/detection/qualityControl/plan/${id}/resume`),

  recordPage: (params) => request.get('/detection/qualityControl/record/page', { params }),
  executeRecord: (id, data) => request.post(`/detection/qualityControl/record/${id}/execute`, data),

  chartData: (params) => request.get('/detection/qualityControl/chart/data', { params }),
  analyze: (params) => request.post('/detection/qualityControl/analyze', params),
  exportReport: (params) => request.get('/detection/qualityControl/report/export', { params, responseType: 'blob' })
}

// =============================================
// 能力验证与实验室间比对 API
// =============================================
export const proficiencyTestApi = {
  planPage: (params) => request.get('/detection/proficiencyTest/page', { params }),
  getPlan: (id) => request.get(`/detection/proficiencyTest/${id}`),
  savePlan: (data) => request.post('/detection/proficiencyTest', data),
  updatePlan: (data) => request.put('/detection/proficiencyTest', data),
  deletePlan: (id) => request.delete(`/detection/proficiencyTest/${id}`),
  register: (id) => request.post(`/detection/proficiencyTest/${id}/register`),
  receiveSample: (id) => request.post(`/detection/proficiencyTest/${id}/receiveSample`),
  saveResult: (data) => request.post('/detection/proficiencyTest/result/save', data),
  reportResult: (data) => request.post('/detection/proficiencyTest/result/report', data),
  getZScore: (id) => request.get(`/detection/proficiencyTest/${id}/zscore`),
  getYoudenData: (id) => request.get(`/detection/proficiencyTest/${id}/youden`),
  getStats: () => request.get('/detection/proficiencyTest/stats')
}

// =============================================
// 标准曲线管理 API
// =============================================
export const standardCurveApi = {
  page: (params) => request.get('/detection/standardCurve/page', { params }),
  getDetail: (id) => request.get(`/detection/standardCurve/${id}`),
  save: (data) => request.post('/detection/standardCurve', data),
  update: (data) => request.put('/detection/standardCurve', data),
  delete: (id) => request.delete(`/detection/standardCurve/${id}`),
  calculate: (data) => request.post('/detection/standardCurve/calculate', data),
  getPoints: (id) => request.get(`/detection/standardCurve/${id}/points`),
  getValidCurves: (itemCode) => request.get(`/detection/standardCurve/valid/${itemCode}`),
  getStats: () => request.get('/detection/standardCurve/stats'),
  verify: (id, data) => request.post(`/detection/standardCurve/${id}/verify`, data)
}

// =============================================
// 稳定性考察 API
// =============================================
export const stabilityApi = {
  page: (params) => request.get('/detection/stability/page', { params }),
  getDetail: (id) => request.get(`/detection/stability/${id}`),
  save: (data) => request.post('/detection/stability', data),
  update: (data) => request.put('/detection/stability', data),
  delete: (id) => request.delete(`/detection/stability/${id}`),
  start: (id) => request.post(`/detection/stability/${id}/start`),
  recordResult: (pointId, data) => request.post(`/detection/stability/point/${pointId}/record`, data),
  getTrend: (id) => request.get(`/detection/stability/${id}/trend`),
  estimate: (id) => request.get(`/detection/stability/${id}/estimate`),
  generateReport: (id) => request.get(`/detection/stability/${id}/generateReport`, { responseType: 'blob' }),
  getStats: () => request.get('/detection/stability/stats')
}

// =============================================
// CAPA纠正预防措施 API
// =============================================
export const capaApi = {
  page: (params) => request.get('/detection/capa/page', { params }),
  getDetail: (id) => request.get(`/detection/capa/${id}`),
  save: (data) => request.post('/detection/capa', data),
  update: (data) => request.put('/detection/capa', data),
  delete: (id) => request.delete(`/detection/capa/${id}`),
  submit: (id) => request.post(`/detection/capa/${id}/submit`),
  approve: (id, data) => request.post(`/detection/capa/${id}/approve`, data),
  reject: (id, data) => request.post(`/detection/capa/${id}/reject`, data),
  executeComplete: (id, data) => request.post(`/detection/capa/${id}/executeComplete`, data),
  verifyPass: (id, data) => request.post(`/detection/capa/${id}/verifyPass`, data),
  verifyFail: (id, data) => request.post(`/detection/capa/${id}/verifyFail`, data),
  close: (id, data) => request.post(`/detection/capa/${id}/close`, data),
  getLogs: (id) => request.get(`/detection/capa/${id}/logs`),
  getStats: () => request.get('/detection/capa/stats'),
  getOverdue: () => request.get('/detection/capa/overdue')
}
