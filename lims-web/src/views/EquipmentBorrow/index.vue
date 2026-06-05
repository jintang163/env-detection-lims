<template>
  <div class="equipment-borrow">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="领用单号">
            <a-input v-model:value="queryParams.borrowNo" placeholder="请输入领用单号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="设备名称">
            <a-input v-model:value="equipmentName" placeholder="请输入设备名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="领用人">
            <a-input v-model:value="borrowerName" placeholder="请输入领用人" style="width: 120px" allow-clear />
          </a-form-item>
          <a-form-item label="归还状态">
            <a-select v-model:value="queryParams.returnStatus" placeholder="请选择" style="width: 140px" allow-clear>
              <a-select-option :value="0">未归还</a-select-option>
              <a-select-option :value="1">已归还</a-select-option>
              <a-select-option :value="2">逾期未还</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="领用日期">
            <a-range-picker
              v-model:value="borrowDateRange"
              style="width: 260px"
              value-format="YYYY-MM-DD"
              @change="handleBorrowDateChange"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery">
              <SearchOutlined /> 查询
            </a-button>
            <a-button style="margin-left: 8px" @click="handleReset">
              <ReloadOutlined /> 重置
            </a-button>
          </a-form-item>
        </a-form>
        <div class="toolbar-right">
          <a-button type="primary" @click="handleAdd">
            <PlusOutlined /> 领用登记
          </a-button>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'returnStatus'">
            <a-tag :color="getStatusColor(record.returnStatus)">
              {{ record.returnStatusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看详情</a-button>
            <template v-if="record.returnStatus === 0 || record.returnStatus === 2">
              <a-button type="link" size="small" @click="handleReturn(record)">归还登记</a-button>
            </template>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      :title="formModalTitle"
      width="600px"
      @ok="handleFormSubmit"
      @cancel="formModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <template v-if="!isReturn">
          <a-form-item label="选择设备" name="equipmentId">
            <a-select
              v-model:value="formData.equipmentId"
              placeholder="请选择设备"
              show-search
              allow-clear
              @change="handleEquipmentChange"
            >
              <a-select-option v-for="item in equipmentList" :key="item.id" :value="item.id">
                {{ item.equipmentName }} ({{ item.equipmentNo }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="选择领用人" name="borrowerId">
            <a-select
              v-model:value="formData.borrowerId"
              placeholder="请选择领用人"
              show-search
              allow-clear
              @change="handleBorrowerChange"
            >
              <a-select-option v-for="item in userList" :key="item.id" :value="item.id">
                {{ item.realName }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="领用日期" name="borrowDate">
                <a-date-picker v-model:value="formData.borrowDate" style="width: 100%" value-format="YYYY-MM-DD" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="预计归还日期" name="expectedReturnDate">
                <a-date-picker v-model:value="formData.expectedReturnDate" style="width: 100%" value-format="YYYY-MM-DD" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item label="领用用途" name="borrowPurpose">
            <a-textarea v-model:value="formData.borrowPurpose" :rows="3" placeholder="请输入领用用途" />
          </a-form-item>
        </template>
        <template v-else>
          <a-descriptions :column="1" bordered size="small" style="margin-bottom: 16px">
            <a-descriptions-item label="领用单号">{{ currentRecord?.borrowNo }}</a-descriptions-item>
            <a-descriptions-item label="设备名称">{{ currentRecord?.equipmentName }}</a-descriptions-item>
            <a-descriptions-item label="领用人">{{ currentRecord?.borrowerName }}</a-descriptions-item>
            <a-descriptions-item label="领用日期">{{ currentRecord?.borrowDate }}</a-descriptions-item>
            <a-descriptions-item label="预计归还日期">{{ currentRecord?.expectedReturnDate }}</a-descriptions-item>
          </a-descriptions>
          <a-form-item label="实际归还日期" name="actualReturnDate">
            <a-date-picker v-model:value="formData.actualReturnDate" style="width: 100%" value-format="YYYY-MM-DD" />
          </a-form-item>
          <a-form-item label="检查结果" name="checkResult">
            <a-select v-model:value="formData.checkResult" placeholder="请选择检查结果">
              <a-select-option value="完好">完好</a-select-option>
              <a-select-option value="轻微损坏">轻微损坏</a-select-option>
              <a-select-option value="严重损坏">严重损坏</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="损坏说明" name="damageDescription">
            <a-textarea v-model:value="formData.damageDescription" :rows="3" placeholder="如有损坏，请详细说明" />
          </a-form-item>
        </template>
        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="领用归还详情"
      width="700px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="领用单号">{{ borrowDetail?.borrowNo }}</a-descriptions-item>
        <a-descriptions-item label="设备编号">{{ borrowDetail?.equipmentNo }}</a-descriptions-item>
        <a-descriptions-item label="设备名称">{{ borrowDetail?.equipmentName }}</a-descriptions-item>
        <a-descriptions-item label="领用人">{{ borrowDetail?.borrowerName }}</a-descriptions-item>
        <a-descriptions-item label="领用日期">{{ borrowDetail?.borrowDate }}</a-descriptions-item>
        <a-descriptions-item label="预计归还日期">{{ borrowDetail?.expectedReturnDate }}</a-descriptions-item>
        <a-descriptions-item label="实际归还日期">{{ borrowDetail?.actualReturnDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="归还状态">
          <a-tag :color="getStatusColor(borrowDetail?.returnStatus)">
            {{ borrowDetail?.returnStatusName }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="检查结果" :span="2">{{ borrowDetail?.checkResult || '-' }}</a-descriptions-item>
        <a-descriptions-item label="损坏说明" :span="2">{{ borrowDetail?.damageDescription || '-' }}</a-descriptions-item>
        <a-descriptions-item label="领用用途" :span="2">{{ borrowDetail?.borrowPurpose || '-' }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">{{ borrowDetail?.remark || '-' }}</a-descriptions-item>
        <a-descriptions-item label="创建时间" :span="2">{{ borrowDetail?.createTime }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import type {
  EquipmentBorrowQuery,
  EquipmentBorrowSaveDTO,
  EquipmentBorrowVO
} from '@/types'
import {
  getEquipmentBorrowPage,
  getEquipmentBorrowById,
  borrowEquipment,
  returnEquipment,
  getAvailableEquipments
} from '@/api/sampling'

const loading = ref(false)
const submitting = ref(false)

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)

const formRef = ref()
const isReturn = ref(false)
const currentRecord = ref<EquipmentBorrowVO | null>(null)
const borrowDateRange = ref<any[]>([])
const equipmentName = ref('')
const borrowerName = ref('')

const equipmentList = ref<any[]>([])
const userList = ref<any[]>([])

const queryParams = reactive<EquipmentBorrowQuery>({
  pageNum: 1,
  pageSize: 10,
  borrowNo: '',
  returnStatus: undefined,
  borrowDateStart: '',
  borrowDateEnd: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<EquipmentBorrowVO[]>([])
const borrowDetail = ref<EquipmentBorrowVO | null>(null)

const formData = reactive<EquipmentBorrowSaveDTO>({
  equipmentId: 0,
  equipmentName: '',
  borrowerId: 0,
  borrowerName: '',
  borrowDate: '',
  expectedReturnDate: '',
  actualReturnDate: '',
  borrowPurpose: '',
  checkResult: '',
  damageDescription: '',
  remark: ''
})

const formRules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  borrowerId: [{ required: true, message: '请选择领用人', trigger: 'change' }],
  borrowDate: [{ required: true, message: '请选择领用日期', trigger: 'change' }],
  expectedReturnDate: [{ required: true, message: '请选择预计归还日期', trigger: 'change' }],
  borrowPurpose: [{ required: true, message: '请输入领用用途', trigger: 'blur' }],
  actualReturnDate: [{ required: true, message: '请选择实际归还日期', trigger: 'change' }],
  checkResult: [{ required: true, message: '请选择检查结果', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '领用单号', dataIndex: 'borrowNo', key: 'borrowNo', width: 140 },
  { title: '设备编号', dataIndex: 'equipmentNo', key: 'equipmentNo', width: 140 },
  { title: '设备名称', dataIndex: 'equipmentName', key: 'equipmentName', width: 180, ellipsis: true },
  { title: '领用人', dataIndex: 'borrowerName', key: 'borrowerName', width: 100 },
  { title: '领用日期', dataIndex: 'borrowDate', key: 'borrowDate', width: 120 },
  { title: '预计归还日期', dataIndex: 'expectedReturnDate', key: 'expectedReturnDate', width: 130 },
  { title: '实际归还日期', dataIndex: 'actualReturnDate', key: 'actualReturnDate', width: 130, customRender: ({ text }: { text: string }) => text || '-' },
  { title: '归还状态', dataIndex: 'returnStatus', key: 'returnStatus', width: 110 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (equipmentName.value) {
      (params as any).equipmentName = equipmentName.value
    }
    if (borrowerName.value) {
      (params as any).borrowerName = borrowerName.value
    }
    const res = await getEquipmentBorrowPage(params)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchEquipmentList = async () => {
  try {
    const res = await getAvailableEquipments()
    equipmentList.value = res.data
  } catch (error) {
    console.error('Get equipment list error:', error)
  }
}

const fetchUserList = async () => {
  userList.value = [
    { id: 1, realName: '张三' },
    { id: 2, realName: '李四' },
    { id: 3, realName: '王五' },
    { id: 4, realName: '赵六' }
  ]
}

const handleBorrowDateChange = (dates: any[]) => {
  if (dates && dates.length === 2) {
    queryParams.borrowDateStart = dates[0]
    queryParams.borrowDateEnd = dates[1]
  } else {
    queryParams.borrowDateStart = ''
    queryParams.borrowDateEnd = ''
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    borrowNo: '',
    returnStatus: undefined,
    borrowDateStart: '',
    borrowDateEnd: ''
  })
  equipmentName.value = ''
  borrowerName.value = ''
  borrowDateRange.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleEquipmentChange = (equipmentId: number) => {
  const equipment = equipmentList.value.find(item => item.id === equipmentId)
  if (equipment) {
    formData.equipmentId = equipmentId
    formData.equipmentName = equipment.equipmentName
  }
}

const handleBorrowerChange = (borrowerId: number) => {
  const user = userList.value.find(item => item.id === borrowerId)
  if (user) {
    formData.borrowerId = borrowerId
    formData.borrowerName = user.realName
  }
}

const handleAdd = () => {
  isReturn.value = false
  formModalTitle.value = '领用登记'
  Object.assign(formData, {
    id: undefined,
    equipmentId: 0,
    equipmentName: '',
    borrowerId: 0,
    borrowerName: '',
    borrowDate: '',
    expectedReturnDate: '',
    actualReturnDate: '',
    borrowPurpose: '',
    checkResult: '',
    damageDescription: '',
    remark: ''
  })
  fetchEquipmentList()
  fetchUserList()
  formModalVisible.value = true
}

const handleReturn = (record: EquipmentBorrowVO) => {
  isReturn.value = true
  formModalTitle.value = '归还登记'
  currentRecord.value = record
  Object.assign(formData, {
    id: record.id,
    equipmentId: record.equipmentId,
    equipmentName: record.equipmentName,
    borrowerId: record.borrowerId,
    borrowerName: record.borrowerName,
    borrowDate: record.borrowDate,
    expectedReturnDate: record.expectedReturnDate,
    actualReturnDate: '',
    borrowPurpose: record.borrowPurpose,
    checkResult: '',
    damageDescription: '',
    remark: ''
  })
  formModalVisible.value = true
}

const handleView = async (record: EquipmentBorrowVO) => {
  try {
    const res = await getEquipmentBorrowById(record.id)
    borrowDetail.value = res.data
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get borrow detail error:', error)
  }
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isReturn.value) {
      await returnEquipment(currentRecord.value!.id!, formData)
      message.success('归还登记成功')
    } else {
      await borrowEquipment(formData)
      message.success('领用登记成功')
    }
    formModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Submit error:', error)
  } finally {
    submitting.value = false
  }
}

const getStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'orange',
    1: 'green',
    2: 'red'
  }
  return colors[status || 0] || 'default'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-right {
  flex-shrink: 0;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
