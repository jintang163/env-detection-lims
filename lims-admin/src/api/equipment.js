import request from '@/utils/request'

export const equipmentApi = {
  page: (params) => request.get('/equipment/page', { params }),
  get: (id) => request.get(`/equipment/${id}`),
  save: (data) => request.post('/equipment', data),
  update: (data) => request.put('/equipment', data),
  delete: (id) => request.delete(`/equipment/${id}`),
  available: () => request.get('/equipment/available'),
  updateStatus: (id, status) => request.put(`/equipment/${id}/status?status=${status}`),
  stats: () => request.get('/equipment/stats')
}

export const calibrationApi = {
  planPage: (params) => request.get('/equipment/calibration/plan/page', { params }),
  planGet: (id) => request.get(`/equipment/calibration/plan/${id}`),
  planSave: (data) => request.post('/equipment/calibration/plan', data),
  planUpdate: (data) => request.put('/equipment/calibration/plan', data),
  planDelete: (id) => request.delete(`/equipment/calibration/plan/${id}`),
  planUpcoming: (params) => request.get('/equipment/calibration/plan/upcoming', { params }),
  planCheckStatus: () => request.post('/equipment/calibration/plan/check-status'),

  recordPage: (params) => request.get('/equipment/calibration/record/page', { params }),
  recordGet: (id) => request.get(`/equipment/calibration/record/${id}`),
  recordSave: (data) => request.post('/equipment/calibration/record', data),
  recordUpdate: (data) => request.put('/equipment/calibration/record', data),
  recordDelete: (id) => request.delete(`/equipment/calibration/record/${id}`),
  recordByEquipment: (equipmentId) => request.get(`/equipment/calibration/record/equipment/${equipmentId}`)
}

export const equipmentUsageApi = {
  page: (params) => request.get('/equipment/usage/page', { params }),
  get: (id) => request.get(`/equipment/usage/${id}`),
  start: (data) => request.post('/equipment/usage/start', data),
  end: (id, data) => request.post(`/equipment/usage/end/${id}`, data),
  delete: (id) => request.delete(`/equipment/usage/${id}`),
  byEquipment: (equipmentId) => request.get(`/equipment/usage/equipment/${equipmentId}`)
}

export const maintenanceApi = {
  recordPage: (params) => request.get('/equipment/maintenance/record/page', { params }),
  recordGet: (id) => request.get(`/equipment/maintenance/record/${id}`),
  recordSave: (data) => request.post('/equipment/maintenance/record', data),
  recordUpdate: (data) => request.put('/equipment/maintenance/record', data),
  recordDelete: (id) => request.delete(`/equipment/maintenance/record/${id}`),
  recordByEquipment: (equipmentId) => request.get(`/equipment/maintenance/record/equipment/${equipmentId}`),

  repairPage: (params) => request.get('/equipment/maintenance/repair/page', { params }),
  repairGet: (id) => request.get(`/equipment/maintenance/repair/${id}`),
  repairSubmit: (data) => request.post('/equipment/maintenance/repair', data),
  repairUpdate: (data) => request.put('/equipment/maintenance/repair', data),
  repairDelete: (id) => request.delete(`/equipment/maintenance/repair/${id}`),
  repairHandle: (data) => request.post('/equipment/maintenance/repair/handle', data),
  repairConfirm: (data) => request.post('/equipment/maintenance/repair/confirm', data),
  repairReject: (id, reason) => request.post(`/equipment/maintenance/repair/reject/${id}?reason=${reason}`),
  repairByEquipment: (equipmentId) => request.get(`/equipment/maintenance/repair/equipment/${equipmentId}`)
}
