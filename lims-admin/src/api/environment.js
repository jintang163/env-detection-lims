import request from '@/utils/request'

export const roomApi = {
  page: (params) => request.get('/environment/room/page', { params }),
  list: () => request.get('/environment/room/list'),
  get: (id) => request.get(`/environment/room/${id}`),
  save: (data) => request.post('/environment/room', data),
  update: (data) => request.put('/environment/room', data),
  delete: (id) => request.delete(`/environment/room/${id}`)
}

export const facilityApi = {
  page: (params) => request.get('/environment/facility/page', { params }),
  list: () => request.get('/environment/facility/list'),
  get: (id) => request.get(`/environment/facility/${id}`),
  save: (data) => request.post('/environment/facility', data),
  update: (data) => request.put('/environment/facility', data),
  delete: (id) => request.delete(`/environment/facility/${id}`),
  updateStatus: (id, status) => request.put(`/environment/facility/${id}/status?status=${status}`),
  stats: () => request.get('/environment/facility/stats')
}

export const monitorApi = {
  dataPage: (params) => request.get('/environment/monitor/data/page', { params }),
  dataGet: (id) => request.get(`/environment/monitor/data/${id}`),
  dataSave: (data) => request.post('/environment/monitor/data', data),
  dataUpdate: (data) => request.put('/environment/monitor/data', data),
  dataDelete: (id) => request.delete(`/environment/monitor/data/${id}`),
  dataRealtime: (monitorPoint) => request.get(`/environment/monitor/data/realtime/${monitorPoint}`),
  dataHistory: (params) => request.get('/environment/monitor/data/history', { params }),

  thresholdPage: (params) => request.get('/environment/monitor/threshold/page', { params }),
  thresholdSave: (data) => request.post('/environment/monitor/threshold', data),
  thresholdUpdate: (data) => request.put('/environment/monitor/threshold', data),
  thresholdDelete: (id) => request.delete(`/environment/monitor/threshold/${id}`),
  thresholdToggle: (id, isEnabled) => request.put(`/environment/monitor/threshold/${id}/toggle?isEnabled=${isEnabled}`),

  warningPage: (params) => request.get('/environment/monitor/warning/page', { params }),
  warningHandle: (data) => request.post('/environment/monitor/warning/handle', data),
  warningIgnore: (id) => request.put(`/environment/monitor/warning/${id}/ignore`),

  stats: () => request.get('/environment/monitor/stats')
}

export const facilityMaintenanceApi = {
  recordPage: (params) => request.get('/environment/facility/maintenance/record/page', { params }),
  recordGet: (id) => request.get(`/environment/facility/maintenance/record/${id}`),
  recordByFacility: (facilityId) => request.get(`/environment/facility/maintenance/record/facility/${facilityId}`),
  recordSave: (data) => request.post('/environment/facility/maintenance/record', data),
  recordUpdate: (data) => request.put('/environment/facility/maintenance/record', data),
  recordDelete: (id) => request.delete(`/environment/facility/maintenance/record/${id}`),

  planPage: (params) => request.get('/environment/facility/maintenance/plan/page', { params }),
  planByFacility: (facilityId) => request.get(`/environment/facility/maintenance/plan/facility/${facilityId}`),
  planSave: (data) => request.post('/environment/facility/maintenance/plan', data),
  planUpdate: (data) => request.put('/environment/facility/maintenance/plan', data),
  planDelete: (id) => request.delete(`/environment/facility/maintenance/plan/${id}`),
  planComplete: (planId, data) => request.post(`/environment/facility/maintenance/plan/${planId}/complete`, data)
}
