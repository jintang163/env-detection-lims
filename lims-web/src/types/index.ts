export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface LoginDTO {
  username: string
  password: string
  captcha?: string
  uuid?: string
}

export interface LoginVO {
  token: string
  userInfo: UserInfo
}

export interface UserInfo {
  id: number
  username: string
  realName: string
  phone?: string
  email?: string
  avatar?: string
  deptId?: number
  status?: number
  roles?: string[]
}

export interface PageQuery {
  pageNum?: number
  pageSize?: number
  orderBy?: string
  orderType?: string
}

export interface CustomerQuery extends PageQuery {
  customerName?: string
  contactPerson?: string
  contactPhone?: string
  customerType?: number
  level?: number
  creditLevel?: number
  isPublic?: number
  createTimeStart?: string
  createTimeEnd?: string
}

export interface CustomerSaveDTO {
  id?: number
  customerName: string
  customerType?: number
  contactPerson: string
  contactPhone: string
  contactEmail?: string
  address?: string
  legalPerson?: string
  creditCode?: string
  level?: number
  creditLevel?: number
  isPublic?: number
  remark?: string
}

export interface CustomerCreditSaveDTO {
  id?: number
  customerId: number
  changeType: number
  changeValue: number
  changeReason: string
  changeTime?: string
}

export interface CustomerQualificationSaveDTO {
  id?: number
  customerId: number
  qualificationName: string
  qualificationType?: string
  certificateNo?: string
  issuingAuthority?: string
  issueDate?: string
  expiryDate?: string
  fileUrl?: string
  remark?: string
}

export interface CustomerFollowSaveDTO {
  id?: number
  customerId: number
  followType: number
  followContent: string
  followTime?: string
  nextFollowTime?: string
  followPerson?: string
}

export interface CustomerVO {
  id: number
  customerName: string
  customerType?: number
  customerTypeName?: string
  contactPerson: string
  contactPhone: string
  contactEmail?: string
  address?: string
  legalPerson?: string
  creditCode?: string
  level?: number
  levelName?: string
  creditLevel?: number
  creditLevelName?: string
  creditScore?: number
  isPublic?: number
  isPublicName?: string
  remark?: string
  createBy?: number
  createByName?: string
  createTime: string
  updateTime?: string
}

export interface CustomerDetailVO extends CustomerVO {
  qualifications?: CustomerQualificationVO[]
  credits?: CustomerCreditVO[]
  follows?: CustomerFollowVO[]
}

export interface CustomerQualificationVO {
  id: number
  customerId: number
  qualificationName: string
  qualificationType?: string
  certificateNo?: string
  issuingAuthority?: string
  issueDate?: string
  expiryDate?: string
  fileUrl?: string
  remark?: string
  createTime: string
}

export interface CustomerCreditVO {
  id: number
  customerId: number
  changeType: number
  changeTypeName?: string
  changeValue: number
  changeReason: string
  changeTime?: string
  operator?: string
}

export interface CustomerFollowVO {
  id: number
  customerId: number
  followType: number
  followTypeName?: string
  followContent: string
  followTime?: string
  nextFollowTime?: string
  followPerson?: string
}

export interface ContractQuery extends PageQuery {
  contractNo?: string
  contractName?: string
  customerId?: number
  customerName?: string
  status?: number
  contractType?: number
  signDateStart?: string
  signDateEnd?: string
  effectiveDateStart?: string
  effectiveDateEnd?: string
}

export interface ContractSaveDTO {
  id?: number
  contractNo?: string
  contractName: string
  contractType?: number
  customerId: number
  customerName: string
  signDate: string
  effectiveDate: string
  expireDate: string
  contractAmount: number
  paymentTerms?: string
  performanceRequirements?: string
  liabilityForBreach?: string
  disputeResolution?: string
  signingPartyA?: string
  signingPartyB?: string
  remark?: string
}

export interface ContractApprovalDTO {
  id: number
  approvalResult: number
  approvalOpinion: string
  approvalNode?: string
}

export interface ContractChangeSaveDTO {
  id?: number
  contractId: number
  changeType: number
  changeContent: string
  changeReason: string
  changeTime?: string
  operator?: string
}

export interface ContractPerformanceSaveDTO {
  id?: number
  contractId: number
  performanceNode: string
  planDate: string
  actualDate?: string
  performanceStatus: number
  performanceDesc?: string
  remark?: string
}

export interface ContractVO {
  id: number
  contractNo: string
  contractName: string
  contractType?: number
  contractTypeName?: string
  customerId: number
  customerName: string
  signDate: string
  effectiveDate: string
  expireDate: string
  contractAmount: number
  status: number
  statusName: string
  approvalStatus?: number
  approvalStatusName?: string
  remark?: string
  createTime: string
  createByName?: string
}

export interface ContractDetailVO extends ContractVO {
  approvals?: ContractApprovalVO[]
  changes?: ContractChangeVO[]
  performances?: ContractPerformanceVO[]
}

export interface ContractApprovalVO {
  id: number
  contractId: number
  approvalNode: string
  approverName?: string
  approvalResult?: number
  approvalResultName?: string
  approvalOpinion?: string
  approvalTime?: string
}

export interface ContractChangeVO {
  id: number
  contractId: number
  changeType: number
  changeTypeName?: string
  changeContent: string
  changeReason: string
  changeTime?: string
  operatorName?: string
}

export interface ContractPerformanceVO {
  id: number
  contractId: number
  performanceNode: string
  planDate: string
  actualDate?: string
  performanceStatus: number
  performanceStatusName?: string
  performanceDesc?: string
  remark?: string
}

export interface EntrustQuery extends PageQuery {
  entrustNo?: string
  customerId?: number
  customerName?: string
  entrustType?: number
  status?: number
  isUrgent?: number
  isSubcontract?: number
  approvalStatus?: number
  contractId?: number
  createTimeStart?: string
  createTimeEnd?: string
  expectedReportStart?: string
  expectedReportEnd?: string
}

export interface EntrustSaveDTO {
  id?: number
  entrustType?: number
  customerId: number
  customerName: string
  contractId?: number
  contractNo?: string
  sampleName?: string
  sampleType?: string
  sampleQuantity?: number
  samplingAddress?: string
  samplingLongitude?: number
  samplingLatitude?: number
  samplingTime?: string
  sampleSendTime?: string
  sampleReceiveTime?: string
  expectedReportTime?: string
  detectionBasis?: string
  evaluationBasis?: string
  discountRate?: number
  isUrgent?: number
  urgentFee?: number
  isSubcontract?: number
  subcontractAmount?: number
  isAdjust?: number
  adjustAmount?: number
  adjustReason?: string
  remark?: string
  items: EntrustItemSaveDTO[]
}

export interface EntrustItemSaveDTO {
  id?: number
  itemId: number
  itemCode?: string
  itemName?: string
  standardId: number
  standardNo?: string
  standardName?: string
  unit?: string
  limitValue?: string
  unitPrice?: number
  quantity: number
  subtotal?: number
  isSubcontract?: number
  subcontractor?: string
  sortOrder?: number
  remark?: string
}

export interface EntrustReviewDTO {
  entrustId: number
  reviewResult: number
  reviewOpinion?: string
  reviewNode?: string
}

export interface EntrustStatusChangeDTO {
  entrustId: number
  targetStatus: number
  operateType?: string
  operateContent?: string
}

export interface AdjustDTO {
  entrustId: number
  adjustAmount: number
  adjustReason: string
}

export interface EntrustVO {
  id: number
  entrustNo: string
  entrustType?: number
  entrustTypeName?: string
  customerId: number
  customerName: string
  contractId?: number
  contractNo?: string
  sampleName?: string
  sampleType?: string
  sampleQuantity?: number
  samplingAddress?: string
  expectedReportTime?: string
  totalAmount?: number
  discountAmount?: number
  actualAmount?: number
  isUrgent?: number
  isSubcontract?: number
  status: number
  statusName: string
  approvalStatus?: number
  approvalStatusName?: string
  itemCount?: number
  createTime: string
}

export interface EntrustDetailVO extends EntrustVO {
  items?: EntrustItemVO[]
  statusLogs?: StatusLogVO[]
  reviewLogs?: ReviewLogVO[]
  subcontracts?: SubcontractVO[]
}

export interface EntrustItemVO {
  id: number
  entrustId: number
  itemId: number
  itemCode: string
  itemName: string
  standardId: number
  standardNo: string
  standardName: string
  unit: string
  limitValue?: string
  unitPrice: number
  quantity: number
  subtotal: number
  isSubcontract?: number
  subcontractor?: string
  sortOrder?: number
  remark?: string
}

export interface StatusLogVO {
  id: number
  entrustId: number
  beforeStatus?: number
  beforeStatusName?: string
  afterStatus: number
  afterStatusName: string
  operateType?: string
  operateContent?: string
  operatorName?: string
  operateTime?: string
}

export interface ReviewLogVO {
  id: number
  entrustId: number
  reviewResult: number
  reviewResultName?: string
  reviewOpinion?: string
  reviewerName?: string
  reviewTime?: string
  reviewNode?: string
}

export interface SubcontractVO {
  id: number
  subcontractNo: string
  entrustId: number
  subcontractorName?: string
  subcontractItems?: string
  subcontractAmount?: number
  status?: number
  statusName?: string
  createTime?: string
}

export interface QuotationQuery extends PageQuery {
  quotationNo?: string
  quotationName?: string
  customerName?: string
  status?: number
  quotationDateStart?: string
  quotationDateEnd?: string
  isConverted?: number
}

export interface QuotationSaveDTO {
  id?: number
  quotationName: string
  customerId: number
  customerName: string
  contractId?: number
  validDate: string
  quotationDate: string
  discountRate: number
  quotationContent?: string
  remark?: string
  items: QuotationItemSaveDTO[]
}

export interface QuotationItemSaveDTO {
  id?: number
  itemId: number
  itemCode?: string
  itemName: string
  itemCategory?: string
  standardId: number
  standardNo?: string
  standardName?: string
  unit?: string
  unitPrice: number
  quantity: number
  sortOrder?: number
  remark?: string
}

export interface QuotationApprovalDTO {
  id: number
  approvalResult: number
  approvalRemark?: string
}

export interface ConvertToEntrustDTO {
  quotationId: number
  entrustType: number
  sampleName?: string
  sampleType?: string
  sampleQuantity?: number
  samplingAddress?: string
  samplingLongitude?: number
  samplingLatitude?: number
  samplingTime?: string
  sampleSendTime?: string
  sampleReceiveTime?: string
  expectedReportTime?: string
  detectionBasis?: string
  evaluationBasis?: string
  contractId?: number
  contractNo?: string
  discountRate?: number
  isUrgent?: number
  isSubcontract?: number
  remark?: string
}

export interface QuotationVO {
  id: number
  quotationNo: string
  quotationName: string
  customerId: number
  customerName: string
  contractId?: number
  validDate: string
  quotationDate: string
  totalAmount: number
  discountRate: number
  actualAmount: number
  status: number
  statusName: string
  approvalStatus?: number
  isConverted?: number
  convertEntrustId?: number
  createTime: string
  createByName?: string
}

export interface QuotationDetailVO extends QuotationVO {
  quotationContent?: string
  confirmUserId?: number
  confirmTime?: string
  items?: QuotationItemVO[]
  approvalRecords?: QuotationApprovalRecordVO[]
}

export interface QuotationItemVO {
  id: number
  quotationId: number
  itemId: number
  itemCode: string
  itemName: string
  itemCategory?: string
  standardId: number
  standardNo: string
  standardName: string
  unit?: string
  unitPrice: number
  quantity: number
  subtotal: number
  sortOrder?: number
  remark?: string
}

export interface QuotationApprovalRecordVO {
  id: number
  quotationId: number
  approvalNode: string
  approverName?: string
  approvalResult?: number
  approvalResultName?: string
  approvalOpinion?: string
  approvalTime?: string
}

export interface QuotationPrintVO {
  quotationNo: string
  quotationName: string
  customerId: number
  customerName: string
  contractId?: number
  validDate: string
  quotationDate: string
  totalAmount: number
  discountRate: number
  actualAmount: number
  quotationContent?: string
  totalAmountCn?: string
  actualAmountCn?: string
  remark?: string
  items?: QuotationItemVO[]
}

export interface DictTestItem {
  id: number
  itemCode: string
  itemName: string
  itemCategory?: string
  unit?: string
  standardPrice: number
  costPrice?: number
  detectionLimit?: number
  detectionCycle?: number
  description?: string
  status?: number
}

export interface DictTestStandard {
  id: number
  standardNo: string
  standardName: string
  standardType?: string
  issueDate?: string
  implementDate?: string
  issuingAuthority?: string
  description?: string
  status?: number
}

export interface DictItemStandard {
  id: number
  itemId: number
  standardId: number
  limitValue?: string
  remark?: string
}

export interface SamplingPlanQuery extends PageQuery {
  planNo?: string
  planName?: string
  entrustId?: number
  status?: number
  samplingType?: number
  planDateStart?: string
  planDateEnd?: string
}

export interface SamplingPointSaveDTO {
  id?: number
  planId?: number
  pointCode: string
  pointName: string
  longitude?: number
  latitude?: number
  samplingDepth?: string
  samplingFrequency?: string
  samplingItems?: string
  remark?: string
}

export interface SamplingPlanSaveDTO {
  id?: number
  planNo?: string
  planName: string
  entrustId?: number
  entrustNo?: string
  planDate: string
  samplingType?: number
  samplingTypeText?: string
  samplerIds?: string
  samplerNames?: string
  equipmentIds?: string
  equipmentNames?: string
  containerIds?: string
  containerNames?: string
  remark?: string
  points: SamplingPointSaveDTO[]
}

export interface TaskAssignItem {
  pointId: number
  samplerId: number
  samplerName: string
}

export interface TaskAssignDTO {
  planId: number
  taskList: TaskAssignItem[]
}

export interface FieldSamplingDTO {
  taskId: number
  longitude?: number
  latitude?: number
  temperature?: number
  ph?: number
  weather?: string
  windSpeed?: string
  photos?: string
  samplingTime?: string
  samplingPerson?: string
  qcSampleFlag?: number
  offlineFlag?: number
  remark?: string
  samples: SampleRecordSaveDTO[]
}

export interface SampleRecordSaveDTO {
  id?: number
  taskId?: number
  planId?: number
  pointId?: number
  sampleNo?: string
  qrCode?: string
  sampleName?: string
  sampleType?: string
  sampleStatus?: number
  temperature?: number
  ph?: number
  storageCondition?: number
  qcFlag?: number
  qcType?: number
  samplingDepth?: string
  samplingTime?: string
  containerType?: string
  preservative?: string
  remark?: string
}

export interface SamplingTaskQuery extends PageQuery {
  taskNo?: string
  planId?: number
  samplerId?: number
  status?: number
  qcSampleFlag?: number
  offlineFlag?: number
  assignTimeStart?: string
  assignTimeEnd?: string
}

export interface SampleTransferSaveDTO {
  id?: number
  transferNo?: string
  transferType?: number
  planId?: number
  planNo?: string
  samplerId?: number
  samplerName?: string
  receiverId?: number
  receiverName?: string
  transferTime?: string
  sampleQuantity?: number
  transferStatus?: number
  transferRemark?: string
  sampleIds?: string
}

export interface SampleTransferQuery extends PageQuery {
  transferNo?: string
  planId?: number
  transferType?: number
  transferStatus?: number
  samplerId?: number
  receiverId?: number
  transferTimeStart?: string
  transferTimeEnd?: string
}

export interface EquipmentSaveDTO {
  id?: number
  equipmentNo?: string
  equipmentName: string
  equipmentType?: number
  specification?: string
  model?: string
  manufacturer?: string
  purchaseDate?: string
  lastCalibrationDate?: string
  nextCalibrationDate?: string
  borrowStatus?: number
  status?: number
  remark?: string
}

export interface EquipmentQuery extends PageQuery {
  equipmentNo?: string
  equipmentName?: string
  equipmentType?: number
  borrowStatus?: number
  status?: number
}

export interface EquipmentBorrowSaveDTO {
  id?: number
  borrowNo?: string
  equipmentId: number
  equipmentName?: string
  borrowerId?: number
  borrowerName?: string
  borrowDate?: string
  expectedReturnDate?: string
  actualReturnDate?: string
  returnStatus?: number
  borrowPurpose?: string
  checkResult?: string
  damageDescription?: string
  remark?: string
}

export interface EquipmentBorrowQuery extends PageQuery {
  borrowNo?: string
  equipmentId?: number
  borrowerId?: number
  returnStatus?: number
  borrowDateStart?: string
  borrowDateEnd?: string
}

export interface SamplingPlanVO {
  id: number
  planNo: string
  planName: string
  entrustId?: number
  entrustNo?: string
  planDate: string
  samplingType?: number
  samplingTypeName?: string
  samplerNames?: string
  equipmentNames?: string
  containerNames?: string
  pointCount?: number
  taskCount?: number
  status: number
  statusName: string
  remark?: string
  createBy?: number
  createByName?: string
  createTime: string
  updateTime?: string
}

export interface SamplingPlanDetailVO extends SamplingPlanVO {
  points?: SamplingPointVO[]
  tasks?: SamplingTaskVO[]
}

export interface SamplingPointVO {
  id: number
  planId: number
  pointCode: string
  pointName: string
  longitude?: number
  latitude?: number
  samplingDepth?: string
  samplingFrequency?: string
  samplingItems?: string
  remark?: string
  createTime?: string
}

export interface SamplingTaskVO {
  id: number
  taskNo: string
  planId: number
  planNo?: string
  planName?: string
  pointId?: number
  pointCode?: string
  pointName?: string
  samplerId?: number
  samplerName?: string
  assignTime?: string
  actualSamplingDate?: string
  temperature?: number
  ph?: number
  weather?: string
  qcSampleFlag?: number
  qcSampleFlagName?: string
  offlineFlag?: number
  offlineFlagName?: string
  sampleCount?: number
  status: number
  statusName: string
  remark?: string
  createTime?: string
}

export interface SamplingTaskDetailVO extends SamplingTaskVO {
  longitude?: number
  latitude?: number
  windSpeed?: string
  photos?: string
  samplingTime?: string
  samplingPerson?: string
  samples?: SampleRecordVO[]
}

export interface SampleRecordVO {
  id: number
  sampleNo: string
  qrCode?: string
  sampleName?: string
  sampleType?: string
  sampleStatus?: number
  sampleStatusName?: string
  temperature?: number
  ph?: number
  storageCondition?: number
  storageConditionName?: string
  qcFlag?: number
  qcFlagName?: string
  qcType?: number
  qcTypeName?: string
  samplingDepth?: string
  samplingTime?: string
  containerType?: string
  preservative?: string
  remark?: string
  createTime?: string
}

export interface SampleTransferVO {
  id: number
  transferNo: string
  transferType?: number
  transferTypeName?: string
  planId?: number
  planNo?: string
  samplerId?: number
  samplerName?: string
  receiverId?: number
  receiverName?: string
  transferTime?: string
  sampleQuantity?: number
  transferStatus?: number
  transferStatusName?: string
  transferRemark?: string
  createTime?: string
}

export interface EquipmentVO {
  id: number
  equipmentNo: string
  equipmentName: string
  equipmentType?: number
  equipmentTypeName?: string
  specification?: string
  model?: string
  manufacturer?: string
  purchaseDate?: string
  lastCalibrationDate?: string
  nextCalibrationDate?: string
  borrowStatus?: number
  borrowStatusName?: string
  status?: number
  statusName?: string
  remark?: string
  createTime?: string
}

export interface EquipmentBorrowVO {
  id: number
  borrowNo: string
  equipmentId: number
  equipmentName?: string
  equipmentNo?: string
  borrowerId?: number
  borrowerName?: string
  borrowDate?: string
  expectedReturnDate?: string
  actualReturnDate?: string
  returnStatus?: number
  returnStatusName?: string
  borrowPurpose?: string
  checkResult?: string
  damageDescription?: string
  remark?: string
  createTime?: string
}
