import { get, post, put, del } from '@/utils/request'
import type {
  CustomerQuery,
  CustomerSaveDTO,
  CustomerVO,
  CustomerDetailVO,
  CustomerCreditSaveDTO,
  CustomerQualificationSaveDTO,
  CustomerFollowSaveDTO,
  PageResult,
  Result
} from '@/types'

export function getCustomerPage(params: CustomerQuery) {
  return get<Result<PageResult<CustomerVO>>>('/customer/page', params)
}

export function getCustomerList(params?: any) {
  return get<Result<CustomerVO[]>>('/customer/list', params)
}

export function getCustomerById(id: number) {
  return get<Result<CustomerDetailVO>>(`/customer/${id}`)
}

export function addCustomer(data: CustomerSaveDTO) {
  return post<Result<number>>('/customer', data)
}

export function updateCustomer(data: CustomerSaveDTO) {
  return put<Result<void>>('/customer', data)
}

export function deleteCustomer(id: number) {
  return del<Result<void>>(`/customer/${id}`)
}

export function getPublicCustomerPage(params: CustomerQuery) {
  return get<Result<PageResult<CustomerVO>>>('/customer/public/page', params)
}

export function claimCustomer(id: number) {
  return post<Result<void>>(`/customer/claim/${id}`)
}

export function assignCustomer(customerId: number, userId: number) {
  return post<Result<void>>(`/customer/assign/${customerId}/${userId}`)
}

export function addCredit(data: CustomerCreditSaveDTO) {
  return post<Result<void>>('/customer/credit', data)
}

export function addQualification(data: CustomerQualificationSaveDTO) {
  return post<Result<void>>('/customer/qualification', data)
}

export function deleteQualification(id: number) {
  return del<Result<void>>(`/customer/qualification/${id}`)
}

export function addFollow(data: CustomerFollowSaveDTO) {
  return post<Result<void>>('/customer/follow', data)
}

export function getCustomerSelectList() {
  return get<Result<any[]>>('/customer/select')
}
