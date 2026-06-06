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

export interface SampleQuery extends PageQuery {
  sampleCode?: string
  sampleName?: string
  pointName?: string
  matrix?: string
  sampleStatus?: number
  entrustId?: number
  createTimeStart?: string
  createTimeEnd?: string
}

export interface SampleItemSaveDTO {
  id?: number
  itemId: number
  itemCode?: string
  itemName?: string
  standardId?: number
  standardNo?: string
  standardName?: string
  unit?: string
  limitValue?: string
}

export interface SampleSaveDTO {
  id?: number
  sampleCode?: string
  sampleName: string
  pointId?: number
  pointCode?: string
  pointName?: string
  samplingTime?: string
  matrix?: string
  entrustId?: number
  entrustNo?: string
  sampleQuantity?: number
  sampleUnit?: string
  storageCondition?: number
  preservative?: string
  containerType?: string
  samplerId?: number
  samplerName?: string
  receiverId?: number
  receiverName?: string
  receiveTime?: string
  remark?: string
  items: SampleItemSaveDTO[]
}

export interface SampleImportDTO {
  file: File
}

export interface SampleGenerateCodeDTO {
  prefix?: string
  count: number
}

export interface SampleVO {
  id: number
  sampleCode: string
  sampleName: string
  pointId?: number
  pointCode?: string
  pointName?: string
  samplingTime?: string
  matrix?: string
  matrixName?: string
  entrustId?: number
  entrustNo?: string
  sampleQuantity?: number
  sampleUnit?: string
  storageCondition?: number
  storageConditionName?: string
  preservative?: string
  containerType?: string
  samplerId?: number
  samplerName?: string
  receiverId?: number
  receiverName?: string
  receiveTime?: string
  sampleStatus?: number
  sampleStatusName?: string
  qrCode?: string
  barcode?: string
  itemCount?: number
  currentLocation?: string
  warningFlag?: number
  warningMessage?: string
  createTime?: string
  createByName?: string
}

export interface SampleDetailVO extends SampleVO {
  items?: SampleItemVO[]
  transferLogs?: SampleTransferLogVO[]
  storageLogs?: SampleStorageLogVO[]
}

export interface SampleItemVO {
  id: number
  sampleId: number
  itemId: number
  itemCode: string
  itemName: string
  standardId?: number
  standardNo?: string
  standardName?: string
  unit?: string
  limitValue?: string
  testResult?: string
  testUnit?: string
  resultStatus?: number
  resultStatusName?: string
}

export interface SampleLabelQuery extends PageQuery {
  sampleCode?: string
  sampleName?: string
  labelType?: number
  printStatus?: number
  createTimeStart?: string
  createTimeEnd?: string
}

export interface SampleLabelPrintDTO {
  sampleIds: number[]
  labelType: number
  labelSize?: string
  printCount?: number
}

export interface SampleLabelVO {
  id: number
  sampleId: number
  sampleCode: string
  sampleName: string
  labelType?: number
  labelTypeName?: string
  qrCode?: string
  barcode?: string
  qrCodeUrl?: string
  barcodeUrl?: string
  printStatus?: number
  printStatusName?: string
  printTime?: string
  printCount?: number
  createTime?: string
}

export interface SampleStorageQuery extends PageQuery {
  sampleCode?: string
  sampleName?: string
  warehouseId?: number
  refrigeratorId?: number
  shelfId?: number
  storageStatus?: number
  warningFlag?: number
}

export interface SampleStorageSaveDTO {
  id?: number
  sampleId: number
  sampleCode?: string
  warehouseId: number
  warehouseName?: string
  refrigeratorId?: number
  refrigeratorName?: string
  shelfId?: number
  shelfName?: string
  locationCode?: string
  storageTime?: string
  storageOperatorId?: number
  storageOperatorName?: string
  remark?: string
}

export interface SampleStorageMoveDTO {
  id: number
  targetWarehouseId: number
  targetRefrigeratorId?: number
  targetShelfId?: number
  targetLocationCode?: string
  moveReason?: string
  operatorId?: number
  operatorName?: string
}

export interface SampleInOutDTO {
  sampleIds: number[]
  operateType: number
  warehouseId?: number
  refrigeratorId?: number
  shelfId?: number
  locationCode?: string
  operateTime?: string
  operatorId?: number
  operatorName?: string
  remark?: string
}

export interface SampleStorageVO {
  id: number
  sampleId: number
  sampleCode: string
  sampleName: string
  sampleQuantity?: number
  sampleUnit?: string
  matrix?: string
  warehouseId: number
  warehouseName: string
  refrigeratorId?: number
  refrigeratorName?: string
  shelfId?: number
  shelfName?: string
  locationCode?: string
  storageTime?: string
  storageOperatorName?: string
  storageStatus?: number
  storageStatusName?: string
  warningFlag?: number
  warningFlagName?: string
  warningMessage?: string
  expiryDate?: string
  remainingDays?: number
  currentQuantity?: number
  createTime?: string
}

export interface SampleStorageLogVO {
  id: number
  sampleId: number
  sampleCode: string
  sampleName: string
  operateType: number
  operateTypeName: string
  warehouseId?: number
  warehouseName?: string
  refrigeratorId?: number
  refrigeratorName?: string
  shelfId?: number
  shelfName?: string
  locationCode?: string
  operateTime?: string
  operatorName?: string
  remark?: string
}

export interface StorageWarehouseSaveDTO {
  id?: number
  warehouseCode: string
  warehouseName: string
  warehouseType?: number
  temperatureMin?: number
  temperatureMax?: number
  humidityMin?: number
  humidityMax?: number
  location?: string
  managerId?: number
  managerName?: string
  status?: number
  remark?: string
}

export interface StorageRefrigeratorSaveDTO {
  id?: number
  warehouseId: number
  refrigeratorCode: string
  refrigeratorName: string
  temperatureMin?: number
  temperatureMax?: number
  currentTemperature?: number
  status?: number
  remark?: string
}

export interface StorageShelfSaveDTO {
  id?: number
  refrigeratorId?: number
  warehouseId?: number
  shelfCode: string
  shelfName: string
  layerCount?: number
  capacity?: number
  usedCapacity?: number
  status?: number
  remark?: string
}

export interface StorageWarehouseVO {
  id: number
  warehouseCode: string
  warehouseName: string
  warehouseType?: number
  warehouseTypeName?: string
  temperatureMin?: number
  temperatureMax?: number
  humidityMin?: number
  humidityMax?: number
  location?: string
  managerId?: number
  managerName?: string
  refrigeratorCount?: number
  shelfCount?: number
  sampleCount?: number
  warningCount?: number
  status?: number
  statusName?: string
  createTime?: string
}

export interface StorageRefrigeratorVO {
  id: number
  warehouseId: number
  warehouseName: string
  refrigeratorCode: string
  refrigeratorName: string
  temperatureMin?: number
  temperatureMax?: number
  currentTemperature?: number
  shelfCount?: number
  sampleCount?: number
  warningCount?: number
  status?: number
  statusName?: string
  createTime?: string
}

export interface StorageShelfVO {
  id: number
  warehouseId?: number
  refrigeratorId?: number
  warehouseName?: string
  refrigeratorName?: string
  shelfCode: string
  shelfName: string
  layerCount?: number
  capacity?: number
  usedCapacity?: number
  sampleCount?: number
  warningCount?: number
  status?: number
  statusName?: string
  createTime?: string
}

export interface SampleTransferQuery extends PageQuery {
  sampleCode?: string
  sampleName?: string
  transferNode?: number
  operatorId?: number
  operateTimeStart?: string
  operateTimeEnd?: string
}

export interface SampleTransferSaveDTO {
  id?: number
  sampleIds: number[]
  transferNode: number
  operatorId?: number
  operatorName?: string
  operateTime?: string
  remark?: string
  nextNode?: number
  nextOperatorId?: number
  nextOperatorName?: string
}

export interface SampleTransferLogVO {
  id: number
  sampleId: number
  sampleCode: string
  sampleName: string
  transferNode: number
  transferNodeName: string
  operatorId?: number
  operatorName?: string
  operateTime?: string
  remark?: string
  duration?: string
  nodeStatus?: number
  nodeStatusName?: string
}

export interface SampleTransferTimelineVO {
  sampleId: number
  sampleCode: string
  sampleName: string
  currentNode: number
  currentNodeName: string
  timeline: SampleTransferTimelineItemVO[]
}

export interface SampleTransferTimelineItemVO {
  nodeCode: string
  nodeName: string
  nodeOrder: number
  status: number
  statusName: string
  operatorName?: string
  operateTime?: string
  remark?: string
  duration?: string
}

export interface RetainSampleQuery extends PageQuery {
  retainNo?: string
  sampleCode?: string
  sampleName?: string
  retainStatus?: number
  observationFlag?: number
  createTimeStart?: string
  createTimeEnd?: string
}

export interface RetainSampleCreateDTO {
  id?: number
  sampleId: number
  sampleCode?: string
  sampleName?: string
  retainQuantity?: number
  retainUnit?: string
  retainReason?: string
  retainDate?: string
  expiryDate?: string
  observationIntervalDays?: number
  nextObservationDate?: string
  storageLocation?: string
  operatorId?: number
  operatorName?: string
  remark?: string
}

export interface RetainSampleOperateDTO {
  id: number
  operateType: number
  operateQuantity?: number
  operatorId?: number
  operatorName?: string
  operateTime?: string
  receivePerson?: string
  returnCondition?: string
  targetLocation?: string
  disposalMethod?: string
  disposalReason?: string
  remark?: string
}

export interface RetainSampleObservationDTO {
  retainSampleId: number
  observationDate?: string
  observationContent: string
  observationResult?: number
  nextObservationDate?: string
  observerId?: number
  observerName?: string
  remark?: string
}

export interface RetainSampleVO {
  id: number
  retainNo: string
  sampleId: number
  sampleCode: string
  sampleName: string
  matrix?: string
  retainQuantity?: number
  retainUnit?: string
  currentQuantity?: number
  retainReason?: string
  retainDate?: string
  expiryDate?: string
  remainingDays?: number
  observationIntervalDays?: number
  lastObservationDate?: string
  nextObservationDate?: string
  storageLocation?: string
  retainStatus?: number
  retainStatusName?: string
  observationFlag?: number
  observationFlagName?: string
  operatorName?: string
  createTime?: string
}

export interface RetainSampleDetailVO extends RetainSampleVO {
  operationLogs?: RetainSampleOperateLogVO[]
  observationRecords?: RetainSampleObservationVO[]
}

export interface RetainSampleOperateLogVO {
  id: number
  retainSampleId: number
  retainNo: string
  operateType: number
  operateTypeName: string
  operateQuantity?: number
  operateTime?: string
  operatorName?: string
  receivePerson?: string
  returnCondition?: string
  targetLocation?: string
  disposalMethod?: string
  remark?: string
}

export interface RetainSampleObservationVO {
  id: number
  retainSampleId: number
  retainNo: string
  observationDate?: string
  observationContent: string
  observationResult?: number
  observationResultName?: string
  nextObservationDate?: string
  observerName?: string
  remark?: string
  createTime?: string
}

export interface SampleDisposalQuery extends PageQuery {
  disposalNo?: string
  sampleCode?: string
  sampleName?: string
  disposalStatus?: number
  applicantId?: number
  approvalStatus?: number
  createTimeStart?: string
  createTimeEnd?: string
}

export interface SampleDisposalApplyDTO {
  id?: number
  sampleIds: number[]
  disposalReason: string
  disposalMethod: number
  expectedDisposalDate?: string
  applicantId?: number
  applicantName?: string
  applyTime?: string
  remark?: string
}

export interface SampleDisposalApprovalDTO {
  id: number
  approvalResult: number
  approvalOpinion?: string
  approverId?: number
  approverName?: string
  approvalTime?: string
  approvalNode?: string
}

export interface SampleDisposalExecuteDTO {
  id: number
  actualDisposalDate?: string
  disposalOperatorId?: number
  disposalOperatorName?: string
  disposalProcess?: string
  witnessId?: number
  witnessName?: string
  disposalFile?: string
  remark?: string
}

export interface SampleDisposalVO {
  id: number
  disposalNo: string
  sampleCount?: number
  disposalReason: string
  disposalMethod: number
  disposalMethodName?: string
  expectedDisposalDate?: string
  actualDisposalDate?: string
  applicantId?: number
  applicantName?: string
  applyTime?: string
  disposalStatus?: number
  disposalStatusName?: string
  approvalStatus?: number
  approvalStatusName?: string
  disposalOperatorName?: string
  witnessName?: string
  disposalFile?: string
  remark?: string
  createTime?: string
}

export interface SampleDisposalDetailVO extends SampleDisposalVO {
  samples?: SampleVO[]
  approvalRecords?: SampleDisposalApprovalVO[]
}

export interface SampleDisposalApprovalVO {
  id: number
  disposalId: number
  disposalNo: string
  approvalNode: string
  approverId?: number
  approverName?: string
  approvalResult?: number
  approvalResultName?: string
  approvalOpinion?: string
  approvalTime?: string
}
