import { get, post, put, del } from '@/utils/request'
import type {
  SamplingPlanQuery,
  SamplingPlanSaveDTO,
  SamplingPlanVO,
  SamplingPlanDetailVO,
  TaskAssignDTO,
  SamplingTaskQuery,
  SamplingTaskVO,
  SamplingTaskDetailVO,
  FieldSamplingDTO,
  SampleTransferSaveDTO,
  SampleTransferQuery,
  SampleTransferVO,
  EquipmentSaveDTO,
  EquipmentQuery,
  EquipmentVO,
  EquipmentBorrowSaveDTO,
  EquipmentBorrowQuery,
  EquipmentBorrowVO,
  PageResult,
  Result
} from '@/types'

export function getSamplingPlanPage(params: SamplingPlanQuery) {
  return get<Result<PageResult<SamplingPlanVO>>>('/sampling/plan/page', params)
}

export function getSamplingPlanList(params?: any) {
  return get<Result<SamplingPlanVO[]>>('/sampling/plan/list', params)
}

export function getSamplingPlanById(id: number) {
  return get<Result<SamplingPlanDetailVO>>(`/sampling/plan/${id}`)
}

export function addSamplingPlan(data: SamplingPlanSaveDTO) {
  return post<Result<number>>('/sampling/plan', data)
}

export function updateSamplingPlan(data: SamplingPlanSaveDTO) {
  return put<Result<void>>('/sampling/plan', data)
}

export function deleteSamplingPlan(id: number) {
  return del<Result<void>>(`/sampling/plan/${id}`)
}

export function submitSamplingPlan(id: number) {
  return post<Result<void>>(`/sampling/plan/submit/${id}`)
}

export function assignSamplingTask(data: TaskAssignDTO) {
  return post<Result<void>>('/sampling/plan/assign', data)
}

export function cancelSamplingPlan(id: number) {
  return post<Result<void>>(`/sampling/plan/cancel/${id}`)
}

export function getSamplingTaskPage(params: SamplingTaskQuery) {
  return get<Result<PageResult<SamplingTaskVO>>>('/sampling/task/page', params)
}

export function getMySamplingTasks(samplerId: number) {
  return get<Result<SamplingTaskVO[]>>(`/sampling/task/my/${samplerId}`)
}

export function getSamplingTaskById(id: number) {
  return get<Result<SamplingTaskDetailVO>>(`/sampling/task/${id}`)
}

export function downloadSamplingTask(taskId: number) {
  return post<Result<SamplingTaskDetailVO>>(`/sampling/task/download/${taskId}`)
}

export function startSampling(taskId: number) {
  return post<Result<void>>(`/sampling/task/start/${taskId}`)
}

export function submitFieldSampling(data: FieldSamplingDTO) {
  return post<Result<void>>('/sampling/task/submit', data)
}

export function syncSamplingTask(taskId: number) {
  return post<Result<void>>(`/sampling/task/sync/${taskId}`)
}

export function getOfflineTasks(samplerId: number) {
  return get<Result<SamplingTaskVO[]>>(`/sampling/task/offline/${samplerId}`)
}

export function getSampleTransferPage(params: SampleTransferQuery) {
  return get<Result<PageResult<SampleTransferVO>>>('/sampling/transfer/page', params)
}

export function getSampleTransferById(id: number) {
  return get<Result<SampleTransferVO>>(`/sampling/transfer/${id}`)
}

export function createSampleTransfer(data: SampleTransferSaveDTO) {
  return post<Result<number>>('/sampling/transfer', data)
}

export function confirmSampleTransfer(id: number) {
  return post<Result<void>>(`/sampling/transfer/confirm/${id}`)
}

export function rejectSampleTransfer(id: number, reason: string) {
  return post<Result<void>>(`/sampling/transfer/reject/${id}`, { reason })
}

export function getEquipmentPage(params: EquipmentQuery) {
  return get<Result<PageResult<EquipmentVO>>>('/sampling/equipment/page', params)
}

export function getEquipmentList(params?: any) {
  return get<Result<EquipmentVO[]>>('/sampling/equipment/list', params)
}

export function getAvailableEquipments() {
  return get<Result<EquipmentVO[]>>('/sampling/equipment/available')
}

export function getEquipmentById(id: number) {
  return get<Result<EquipmentVO>>(`/sampling/equipment/${id}`)
}

export function addEquipment(data: EquipmentSaveDTO) {
  return post<Result<number>>('/sampling/equipment', data)
}

export function updateEquipment(data: EquipmentSaveDTO) {
  return put<Result<void>>('/sampling/equipment', data)
}

export function deleteEquipment(id: number) {
  return del<Result<void>>(`/sampling/equipment/${id}`)
}

export function getEquipmentBorrowPage(params: EquipmentBorrowQuery) {
  return get<Result<PageResult<EquipmentBorrowVO>>>('/sampling/equipment-borrow/page', params)
}

export function getEquipmentBorrowById(id: number) {
  return get<Result<EquipmentBorrowVO>>(`/sampling/equipment-borrow/${id}`)
}

export function borrowEquipment(data: EquipmentBorrowSaveDTO) {
  return post<Result<number>>('/sampling/equipment-borrow/borrow', data)
}

export function returnEquipment(id: number, data: EquipmentBorrowSaveDTO) {
  return post<Result<void>>(`/sampling/equipment-borrow/return/${id}`, data)
}
