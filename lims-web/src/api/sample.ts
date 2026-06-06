import { get, post, put, del } from '@/utils/request'
import type {
  SampleQuery,
  SampleSaveDTO,
  SampleVO,
  SampleDetailVO,
  SampleGenerateCodeDTO,
  SampleLabelQuery,
  SampleLabelPrintDTO,
  SampleLabelVO,
  SampleStorageQuery,
  SampleStorageSaveDTO,
  SampleStorageMoveDTO,
  SampleInOutDTO,
  SampleStorageVO,
  SampleStorageLogVO,
  StorageWarehouseSaveDTO,
  StorageRefrigeratorSaveDTO,
  StorageShelfSaveDTO,
  StorageWarehouseVO,
  StorageRefrigeratorVO,
  StorageShelfVO,
  SampleTransferQuery,
  SampleTransferSaveDTO,
  SampleTransferLogVO,
  SampleTransferTimelineVO,
  RetainSampleQuery,
  RetainSampleCreateDTO,
  RetainSampleOperateDTO,
  RetainSampleObservationDTO,
  RetainSampleVO,
  RetainSampleDetailVO,
  SampleDisposalQuery,
  SampleDisposalApplyDTO,
  SampleDisposalApprovalDTO,
  SampleDisposalExecuteDTO,
  SampleDisposalVO,
  SampleDisposalDetailVO,
  PageResult,
  Result
} from '@/types'

export function getSamplePage(params: SampleQuery) {
  return get<Result<PageResult<SampleVO>>>('/sample/page', params)
}

export function getSampleList(params?: any) {
  return get<Result<SampleVO[]>>('/sample/list', params)
}

export function getSampleById(id: number) {
  return get<Result<SampleDetailVO>>(`/sample/${id}`)
}

export function addSample(data: SampleSaveDTO) {
  return post<Result<number>>('/sample', data)
}

export function updateSample(data: SampleSaveDTO) {
  return put<Result<void>>('/sample', data)
}

export function deleteSample(id: number) {
  return del<Result<void>>(`/sample/${id}`)
}

export function generateSampleCode(data: SampleGenerateCodeDTO) {
  return post<Result<string[]>>('/sample/generate-code', data)
}

export function importSample(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<Result<{ successCount: number; failCount: number; errors: string[] }>>('/sample/import', formData)
}

export function getSampleLabelPage(params: SampleLabelQuery) {
  return get<Result<PageResult<SampleLabelVO>>>('/sample/label/page', params)
}

export function generateLabel(sampleIds: number[], labelType: number) {
  return post<Result<SampleLabelVO[]>>('/sample/label/generate', { sampleIds, labelType })
}

export function printLabel(data: SampleLabelPrintDTO) {
  return post<Result<void>>('/sample/label/print', data)
}

export function reprintLabel(ids: number[]) {
  return post<Result<void>>('/sample/label/reprint', { ids })
}

export function getSampleStoragePage(params: SampleStorageQuery) {
  return get<Result<PageResult<SampleStorageVO>>>('/sample/storage/page', params)
}

export function getSampleStorageList(params?: any) {
  return get<Result<SampleStorageVO[]>>('/sample/storage/list', params)
}

export function getSampleStorageById(id: number) {
  return get<Result<SampleStorageVO>>(`/sample/storage/${id}`)
}

export function saveSampleStorage(data: SampleStorageSaveDTO) {
  return post<Result<number>>('/sample/storage', data)
}

export function updateSampleStorage(data: SampleStorageSaveDTO) {
  return put<Result<void>>('/sample/storage', data)
}

export function moveSampleStorage(data: SampleStorageMoveDTO) {
  return post<Result<void>>('/sample/storage/move', data)
}

export function sampleInbound(data: SampleInOutDTO) {
  return post<Result<void>>('/sample/storage/inbound', data)
}

export function sampleOutbound(data: SampleInOutDTO) {
  return post<Result<void>>('/sample/storage/outbound', data)
}

export function getStorageLogBySampleId(sampleId: number) {
  return get<Result<SampleStorageLogVO[]>>(`/sample/storage/log/${sampleId}`)
}

export function getWarehouseList(params?: any) {
  return get<Result<StorageWarehouseVO[]>>('/sample/storage/warehouse/list', params)
}

export function getWarehouseById(id: number) {
  return get<Result<StorageWarehouseVO>>(`/sample/storage/warehouse/${id}`)
}

export function saveWarehouse(data: StorageWarehouseSaveDTO) {
  return post<Result<number>>('/sample/storage/warehouse', data)
}

export function updateWarehouse(data: StorageWarehouseSaveDTO) {
  return put<Result<void>>('/sample/storage/warehouse', data)
}

export function deleteWarehouse(id: number) {
  return del<Result<void>>(`/sample/storage/warehouse/${id}`)
}

export function getRefrigeratorList(warehouseId?: number) {
  return get<Result<StorageRefrigeratorVO[]>>('/sample/storage/refrigerator/list', { warehouseId })
}

export function saveRefrigerator(data: StorageRefrigeratorSaveDTO) {
  return post<Result<number>>('/sample/storage/refrigerator', data)
}

export function updateRefrigerator(data: StorageRefrigeratorSaveDTO) {
  return put<Result<void>>('/sample/storage/refrigerator', data)
}

export function deleteRefrigerator(id: number) {
  return del<Result<void>>(`/sample/storage/refrigerator/${id}`)
}

export function getShelfList(params?: any) {
  return get<Result<StorageShelfVO[]>>('/sample/storage/shelf/list', params)
}

export function saveShelf(data: StorageShelfSaveDTO) {
  return post<Result<number>>('/sample/storage/shelf', data)
}

export function updateShelf(data: StorageShelfSaveDTO) {
  return put<Result<void>>('/sample/storage/shelf', data)
}

export function deleteShelf(id: number) {
  return del<Result<void>>(`/sample/storage/shelf/${id}`)
}

export function getStorageTree() {
  return get<Result<any[]>>('/sample/storage/tree')
}

export function getSampleTransferPage(params: SampleTransferQuery) {
  return get<Result<PageResult<SampleTransferLogVO>>>('/sample/transfer/page', params)
}

export function getTransferTimeline(sampleId: number) {
  return get<Result<SampleTransferTimelineVO>>(`/sample/transfer/timeline/${sampleId}`)
}

export function transferSample(data: SampleTransferSaveDTO) {
  return post<Result<void>>('/sample/transfer', data)
}

export function getTransferLogBySampleId(sampleId: number) {
  return get<Result<SampleTransferLogVO[]>>(`/sample/transfer/log/${sampleId}`)
}

export function getRetainSamplePage(params: RetainSampleQuery) {
  return get<Result<PageResult<RetainSampleVO>>>('/sample/retain/page', params)
}

export function getRetainSampleById(id: number) {
  return get<Result<RetainSampleDetailVO>>(`/sample/retain/${id}`)
}

export function createRetainSample(data: RetainSampleCreateDTO) {
  return post<Result<number>>('/sample/retain', data)
}

export function autoCreateRetainSample(sampleIds: number[]) {
  return post<Result<number[]>>('/sample/retain/auto-create', { sampleIds })
}

export function operateRetainSample(data: RetainSampleOperateDTO) {
  return post<Result<void>>('/sample/retain/operate', data)
}

export function addObservationRecord(data: RetainSampleObservationDTO) {
  return post<Result<void>>('/sample/retain/observation', data)
}

export function getObservationRecords(retainSampleId: number) {
  return get<Result<any[]>>(`/sample/retain/observation/${retainSampleId}`)
}

export function getSampleDisposalPage(params: SampleDisposalQuery) {
  return get<Result<PageResult<SampleDisposalVO>>>('/sample/disposal/page', params)
}

export function getSampleDisposalById(id: number) {
  return get<Result<SampleDisposalDetailVO>>(`/sample/disposal/${id}`)
}

export function applyDisposal(data: SampleDisposalApplyDTO) {
  return post<Result<number>>('/sample/disposal/apply', data)
}

export function approvalDisposal(data: SampleDisposalApprovalDTO) {
  return post<Result<void>>('/sample/disposal/approval', data)
}

export function executeDisposal(data: SampleDisposalExecuteDTO) {
  return post<Result<void>>('/sample/disposal/execute', data)
}

export function cancelDisposal(id: number) {
  return post<Result<void>>(`/sample/disposal/cancel/${id}`)
}

export function getDisposalApprovalRecords(disposalId: number) {
  return get<Result<any[]>>(`/sample/disposal/approval/${disposalId}`)
}
