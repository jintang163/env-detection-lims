import { get, post, put, del } from '@/utils/request'
import type {
  QuotationQuery,
  QuotationSaveDTO,
  QuotationVO,
  QuotationDetailVO,
  QuotationApprovalDTO,
  ConvertToEntrustDTO,
  QuotationPrintVO,
  PageResult,
  Result
} from '@/types'

export function getQuotationPage(params: QuotationQuery) {
  return get<Result<PageResult<QuotationVO>>>('/quotation/page', params)
}

export function getQuotationList(params?: any) {
  return get<Result<QuotationVO[]>>('/quotation/list', params)
}

export function getQuotationById(id: number) {
  return get<Result<QuotationDetailVO>>(`/quotation/${id}`)
}

export function getQuotationPrint(id: number) {
  return get<Result<QuotationPrintVO>>(`/quotation/print/${id}`)
}

export function addQuotation(data: QuotationSaveDTO) {
  return post<Result<number>>('/quotation', data)
}

export function updateQuotation(data: QuotationSaveDTO) {
  return put<Result<void>>('/quotation', data)
}

export function deleteQuotation(id: number) {
  return del<Result<void>>(`/quotation/${id}`)
}

export function submitQuotationApproval(id: number) {
  return post<Result<void>>(`/quotation/submit/${id}`)
}

export function approveQuotation(data: QuotationApprovalDTO) {
  return post<Result<void>>('/quotation/approval', data)
}

export function customerConfirmQuotation(id: number, confirmPerson: string) {
  return post<Result<void>>(`/quotation/confirm/${id}?confirmPerson=${confirmPerson}`)
}

export function cancelQuotation(id: number) {
  return post<Result<void>>(`/quotation/cancel/${id}`)
}

export function convertToEntrust(data: ConvertToEntrustDTO) {
  return post<Result<number>>('/quotation/convert', data)
}
