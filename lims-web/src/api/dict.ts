import { get } from '@/utils/request'
import type { DictTestItem, DictTestStandard, DictItemStandard, Result } from '@/types'

export function getTestItemList(params?: any) {
  return get<Result<DictTestItem[]>>('/dict/testItem/list', params)
}

export function getTestItemPage(params?: any) {
  return get<Result<any>>('/dict/testItem/page', params)
}

export function getTestStandardList(params?: any) {
  return get<Result<DictTestStandard[]>>('/dict/testStandard/list', params)
}

export function getStandardByItemId(itemId: number) {
  return get<Result<DictTestStandard[]>>(`/dict/testStandard/byItem/${itemId}`)
}

export function getItemStandardList(params?: any) {
  return get<Result<DictItemStandard[]>>('/dict/itemStandard/list', params)
}
