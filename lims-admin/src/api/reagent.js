import request from '@/utils/request'

export const reagentApi = {
  page: (params) => request.get('/reagent/reagent/page', { params }),
  get: (id) => request.get(`/reagent/reagent/${id}`),
  save: (data) => request.post('/reagent/reagent', data),
  update: (data) => request.put('/reagent/reagent', data),
  delete: (id) => request.delete(`/reagent/reagent/${id}`),
  stats: () => request.get('/reagent/reagent/stats'),
  getWarningList: (params) => request.get('/reagent/reagent/warning', { params }),
  getExpireList: (params) => request.get('/reagent/reagent/expire', { params }),
  stockIn: (data) => request.post('/reagent/reagent/stockIn', data),
  stockOut: (data) => request.post('/reagent/reagent/stockOut', data),
  getMsds: (id) => request.get(`/reagent/reagent/${id}/msds`),
  getStockRecords: (reagentId, params) => request.get(`/reagent/reagent/${reagentId}/stockRecords`, { params })
}

export const standardMaterialApi = {
  page: (params) => request.get('/reagent/standardMaterial/page', { params }),
  get: (id) => request.get(`/reagent/standardMaterial/${id}`),
  save: (data) => request.post('/reagent/standardMaterial', data),
  update: (data) => request.put('/reagent/standardMaterial', data),
  delete: (id) => request.delete(`/reagent/standardMaterial/${id}`),
  stats: () => request.get('/reagent/standardMaterial/stats'),
  stockIn: (data) => request.post('/reagent/standardMaterial/stockIn', data),
  getPrepareRecords: (materialId, params) => request.get(`/reagent/standardMaterial/${materialId}/prepareRecords`, { params }),
  savePrepareRecord: (data) => request.post('/reagent/standardMaterial/prepare', data),
  getCalibrateRecords: (solutionId, params) => request.get(`/reagent/standardMaterial/${solutionId}/calibrateRecords`, { params }),
  saveCalibrateRecord: (data) => request.post('/reagent/standardMaterial/calibrate', data),
  getValidMaterials: () => request.get('/reagent/standardMaterial/valid'),
  getTraceTasks: (materialId) => request.get(`/reagent/standardMaterial/${materialId}/traceTasks`),
  getExpireList: (params) => request.get('/reagent/standardMaterial/expire', { params })
}

export const consumableApi = {
  page: (params) => request.get('/reagent/consumable/page', { params }),
  get: (id) => request.get(`/reagent/consumable/${id}`),
  save: (data) => request.post('/reagent/consumable', data),
  update: (data) => request.put('/reagent/consumable', data),
  delete: (id) => request.delete(`/reagent/consumable/${id}`),
  stats: () => request.get('/reagent/consumable/stats'),
  getWarningList: (params) => request.get('/reagent/consumable/warning', { params }),
  purchase: (data) => request.post('/reagent/consumable/purchase', data),
  stockIn: (data) => request.post('/reagent/consumable/stockIn', data),
  stockOut: (data) => request.post('/reagent/consumable/stockOut', data),
  getPurchaseRecords: (consumableId, params) => request.get(`/reagent/consumable/${consumableId}/purchaseRecords`, { params }),
  getUsageRecords: (consumableId, params) => request.get(`/reagent/consumable/${consumableId}/usageRecords`, { params })
}
