import { get, post, put, del } from '@/utils/request'
import type {
  ContractQuery,
  ContractSaveDTO,
  ContractVO,
  ContractDetailVO,
  ContractApprovalDTO,
  ContractChangeSaveDTO,
  ContractPerformanceSaveDTO,
  PageResult,
  Result
} from '@/types'

export function getContractPage(params: ContractQuery) {
  return get<Result<PageResult<ContractVO>>>('/contract/page', params)
}

export function getContractList(params?: any) {
  return get<Result<ContractVO[]>>('/contract/list', params)
}

export function getContractById(id: number) {
  return get<Result<ContractDetailVO>>(`/contract/${id}`)
}

export function addContract(data: ContractSaveDTO) {
  return post<Result<number>>('/contract', data)
}

export function updateContract(data: ContractSaveDTO) {
  return put<Result<void>>('/contract', data)
}

export function deleteContract(id: number) {
  return del<Result<void>>(`/contract/${id}`)
}

export function submitApproval(id: number) {
  return post<Result<void>>(`/contract/submit/${id}`)
}

export function approval(data: ContractApprovalDTO) {
  return post<Result<void>>('/contract/approval', data)
}

export function addChange(data: ContractChangeSaveDTO) {
  return post<Result<void>>('/contract/change', data)
}

export function addPerformance(data: ContractPerformanceSaveDTO) {
  return post<Result<void>>('/contract/performance', data)
}

export function terminateContract(id: number, reason: string) {
  return post<Result<void>>(`/contract/terminate/${id}`, { reason })
}

export function completeContract(id: number) {
  return post<Result<void>>(`/contract/complete/${id}`)
}

export function getContractSelectList() {
  return get<Result<any[]>>('/contract/select')
}
