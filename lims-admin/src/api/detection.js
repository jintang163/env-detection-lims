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
