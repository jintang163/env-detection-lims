import { get, post, put, del } from '@/utils/request'
import type {
  EntrustQuery,
  EntrustSaveDTO,
  EntrustVO,
  EntrustDetailVO,
  EntrustReviewDTO,
  EntrustStatusChangeDTO,
  AdjustDTO,
  PageResult,
  Result
} from '@/types'

export function getEntrustPage(params: EntrustQuery) {
  return get<Result<PageResult<EntrustVO>>>('/entrust/page', params)
}

export function getEntrustList(params?: any) {
  return get<Result<EntrustVO[]>>('/entrust/list', params)
}

export function getEntrustById(id: number) {
  return get<Result<EntrustDetailVO>>(`/entrust/${id}`)
}

export function addEntrust(data: EntrustSaveDTO) {
  return post<Result<number>>('/entrust', data)
}

export function updateEntrust(data: EntrustSaveDTO) {
  return put<Result<void>>('/entrust', data)
}

export function deleteEntrust(id: number) {
  return del<Result<void>>(`/entrust/${id}`)
}

export function submitEntrust(id: number) {
  return post<Result<void>>(`/entrust/submit/${id}`)
}

export function reviewEntrust(data: EntrustReviewDTO) {
  return post<Result<void>>('/entrust/review', data)
}

export function changeEntrustStatus(data: EntrustStatusChangeDTO) {
  return post<Result<void>>('/entrust/changeStatus', data)
}

export function urgentEntrust(id: number) {
  return post<Result<void>>(`/entrust/urgent/${id}`)
}

export function adjustEntrust(data: AdjustDTO) {
  return post<Result<void>>('/entrust/adjust', data)
}
