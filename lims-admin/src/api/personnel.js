import request from '@/utils/request'

export const personnelApi = {
  page: (params) => request.get('/personnel/page', { params }),
  get: (id) => request.get(`/personnel/${id}`),
  save: (data) => request.post('/personnel', data),
  update: (data) => request.put('/personnel', data),
  delete: (id) => request.delete(`/personnel/${id}`),
  stats: () => request.get('/personnel/stats'),
  generateEmployeeNo: () => request.get('/personnel/generateEmployeeNo'),
  checkQualification: (personnelId, testItemIds) => request.get(`/personnel/${personnelId}/checkQualification`, {
    params: { testItemIds: testItemIds.join(',') }
  }),
  getQualified: (testItemIds) => request.get('/personnel/qualified', {
    params: { testItemIds: testItemIds.join(',') }
  }),

  educationList: (personnelId) => request.get(`/personnel/${personnelId}/education`),
  educationSave: (data) => request.post('/personnel/education', data),
  educationUpdate: (data) => request.put('/personnel/education', data),
  educationDelete: (id) => request.delete(`/personnel/education/${id}`),

  titleList: (personnelId) => request.get(`/personnel/${personnelId}/title`),
  titleSave: (data) => request.post('/personnel/title', data),
  titleUpdate: (data) => request.put('/personnel/title', data),
  titleDelete: (id) => request.delete(`/personnel/title/${id}`),

  authorizationList: (personnelId) => request.get(`/personnel/${personnelId}/authorization`),
  authorizationSave: (data) => request.post('/personnel/authorization', data),
  authorizationUpdate: (data) => request.put('/personnel/authorization', data),
  authorizationDelete: (id) => request.delete(`/personnel/authorization/${id}`),
  authorizationEnable: (id) => request.post(`/personnel/authorization/enable/${id}`),
  authorizationDisable: (id) => request.post(`/personnel/authorization/disable/${id}`),

  certificateList: (personnelId) => request.get(`/personnel/${personnelId}/certificate`),
  certificateSave: (data) => request.post('/personnel/certificate', data),
  certificateUpdate: (data) => request.put('/personnel/certificate', data),
  certificateDelete: (id) => request.delete(`/personnel/certificate/${id}`),

  assessmentList: (personnelId) => request.get(`/personnel/${personnelId}/assessment`),
  assessmentSave: (data) => request.post('/personnel/assessment', data),
  assessmentUpdate: (data) => request.put('/personnel/assessment', data),
  assessmentDelete: (id) => request.delete(`/personnel/assessment/${id}`),

  trainingRecordList: (personnelId) => request.get(`/personnel/${personnelId}/trainingRecord`),
  trainingRecordSave: (data) => request.post('/personnel/trainingRecord', data),
  trainingRecordUpdate: (data) => request.put('/personnel/trainingRecord', data),
  trainingRecordDelete: (id) => request.delete(`/personnel/trainingRecord/${id}`)
}

export const trainingApi = {
  planPage: (params) => request.get('/training/plan/page', { params }),
  planGet: (id) => request.get(`/training/plan/${id}`),
  planSave: (data) => request.post('/training/plan', data),
  planUpdate: (data) => request.put('/training/plan', data),
  planDelete: (id) => request.delete(`/training/plan/${id}`),
  planPublish: (id) => request.post(`/training/plan/publish/${id}`),
  planStart: (id) => request.post(`/training/plan/start/${id}`),
  planComplete: (id) => request.post(`/training/plan/complete/${id}`),

  participantPage: (params) => request.get('/training/participant/page', { params }),
  participantAdd: (data) => request.post('/training/participant', data),
  participantSignIn: (data) => request.post('/training/participant/signIn', data),
  participantUpdate: (data) => request.put('/training/participant', data),
  participantDelete: (id) => request.delete(`/training/participant/${id}`),

  evaluationAdd: (data) => request.post('/training/evaluation', data),
  evaluationByPlan: (planId) => request.get(`/training/evaluation/plan/${planId}`),

  statistics: () => request.get('/training/statistics'),
  checkCertificateExpiry: () => request.post('/training/certificate/checkExpiry'),
  warningPage: (params) => request.get('/training/warning/page', { params }),
  warningProcess: (data) => request.post('/training/warning/process', data)
}
