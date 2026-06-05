<template>
  <div class="sample-transfer">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="交接单号">
            <a-input v-model:value="queryParams.transferNo" placeholder="请输入交接单号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="计划编号">
            <a-input v-model:value="queryParams.planNo" placeholder="请输入计划编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="交接类型">
            <a-select v-model:value="queryParams.transferType" placeholder="请选择" style="width: 140px" allow-clear>
              <a-select-option :value="1">采样员送样</a-select-option>
              <a-select-option :value="2">其他</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.transferStatus" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">待确认</a-select-option>
              <a-select-option :value="1">已确认</a-select-option>
              <a-select-option :value="2">已驳回</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="交接时间">
            <a-range-picker
              v-model:value="transferTimeRange"
              style="width: 260px"
              value-format="YYYY-MM-DD"
              @change="handleTransferTimeChange"
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
            <PlusOutlined /> 创建交接单
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
          <template v-if="column.key === 'transferType'">
            {{ record.transferTypeName || '-' }}
          </template>
          <template v-else-if="column.key === 'transferStatus'">
            <a-tag :color="getStatusColor(record.transferStatus)">
              {{ record.transferStatusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看详情</a-button>
            <template v-if="record.transferStatus === 0">
              <a-popconfirm title="确定确认接收该交接单吗？" @confirm="handleConfirm(record.id)">
                <a-button type="link" size="small" style="color: #52c41a">确认接收</a-button>
              </a-popconfirm>
              <a-button type="link" danger size="small" @click="handleReject(record)">驳回</a-button>
            </template>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      title="创建交接单"
      width="800px"
      @ok="handleFormSubmit"
      @cancel="formModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="交接类型" name="transferType">
              <a-select v-model:value="formData.transferType" placeholder="请选择交接类型">
                <a-select-option :value="1">采样员送样</a-select-option>
                <a-select-option :value="2">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="采样计划" name="planId">
              <a-select
                v-model:value="formData.planId"
                placeholder="请选择采样计划"
                show-search
                allow-clear
                @change="handlePlanChange"
              >
                <a-select-option v-for="item in planList" :key="item.id" :value="item.id">
                  {{ item.planNo }} - {{ item.planName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="采样员" name="samplerId">
              <a-select
                v-model:value="formData.samplerId"
                placeholder="请选择采样员"
                show-search
                allow-clear
                @change="handleSamplerChange"
              >
                <a-select-option v-for="item in userList" :key="item.id" :value="item.id">
                  {{ item.realName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="交接时间" name="transferTime">
              <a-date-picker
                v-model:value="formData.transferTime"
                style="width: 100%"
                show-time
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="选择样品">
          <a-table
            :columns="sampleColumns"
            :data-source="sampleList"
            row-key="id"
            size="small"
            :pagination="false"
            :row-selection="rowSelection"
            :loading="sampleLoading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'sampleStatus'">
                <a-tag :color="getSampleStatusColor(record.sampleStatus)">
                  {{ record.sampleStatusName }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-form-item>
        <a-form-item label="样品数量">
          <a-input :value="selectedSampleIds.length" disabled />
        </a-form-item>
        <a-form-item label="交接备注">
          <a-textarea v-model:value="formData.transferRemark" :rows="3" placeholder="请输入交接备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="交接单详情"
      width="800px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="交接单号">{{ transferDetail?.transferNo }}</a-descriptions-item>
        <a-descriptions-item label="计划编号">{{ transferDetail?.planNo || '-' }}</a-descriptions-item>
        <a-descriptions-item label="交接类型">{{ transferDetail?.transferTypeName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="样品数量">{{ transferDetail?.sampleQuantity || 0 }}</a-descriptions-item>
        <a-descriptions-item label="采样员">{{ transferDetail?.samplerName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="接收员">{{ transferDetail?.receiverName || '-' }}</a-descriptions-item>
        <a-descriptions-item label="交接时间">{{ transferDetail?.transferTime || '-' }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getStatusColor(transferDetail?.transferStatus)">
            {{ transferDetail?.transferStatusName }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ transferDetail?.createTime }}</a-descriptions-item>
        <a-descriptions-item label="交接备注" :span="2">{{ transferDetail?.transferRemark || '-' }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>

    <a-modal
      v-model:open="rejectModalVisible"
      title="驳回交接单"
      width="500px"
      @ok="handleRejectSubmit"
      @cancel="rejectModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
    >
      <a-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" layout="vertical">
        <a-descriptions :column="2" bordered size="small" style="margin-bottom: 16px">
          <a-descriptions-item label="交接单号">{{ currentTransfer?.transferNo }}</a-descriptions-item>
          <a-descriptions-item label="计划编号">{{ currentTransfer?.planNo || '-' }}</a-descriptions-item>
        </a-descriptions>
        <a-form-item label="驳回原因" name="rejectReason">
          <a-textarea v-model:value="rejectForm.rejectReason" :rows="4" placeholder="请输入驳回原因" />
        </a-form-item>
      </a-form>
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
  SampleTransferQuery,
  SampleTransferSaveDTO,
  SampleTransferVO,
  SamplingPlanVO,
  SampleRecordVO
} from '@/types'
import {
  getSampleTransferPage,
  getSampleTransferById,
  createSampleTransfer,
  confirmSampleTransfer,
  rejectSampleTransfer
} from '@/api/sampling'
import { getSamplingPlanList } from '@/api/sampling'

const loading = ref(false)
const submitting = ref(false)
const sampleLoading = ref(false)

const formModalVisible = ref(false)
const detailModalVisible = ref(false)
const rejectModalVisible = ref(false)

const formRef = ref()
const rejectFormRef = ref()
const transferTimeRange = ref<any[]>([])
const currentTransfer = ref<SampleTransferVO | null>(null)
const transferDetail = ref<SampleTransferVO | null>(null)

const planList = ref<SamplingPlanVO[]>([])
const userList = ref<any[]>([])
const sampleList = ref<SampleRecordVO[]>([])
const selectedSampleIds = ref<number[]>([])

const rowSelection = {
  selectedRowKeys: selectedSampleIds,
  onChange: (selectedKeys: number[]) => {
    selectedSampleIds.value = selectedKeys
    formData.sampleQuantity = selectedKeys.length
  },
  getCheckboxProps: (record: SampleRecordVO) => ({
    disabled: record.sampleStatus !== 1
  })
}

const queryParams = reactive<SampleTransferQuery>({
  pageNum: 1,
  pageSize: 10,
  transferNo: '',
  planId: undefined,
  transferType: undefined,
  transferStatus: undefined,
  samplerId: undefined,
  receiverId: undefined,
  transferTimeStart: '',
  transferTimeEnd: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<SampleTransferVO[]>([])

const formData = reactive<SampleTransferSaveDTO>({
  transferType: 1,
  planId: undefined,
  planNo: '',
  samplerId: undefined,
  samplerName: '',
  transferTime: '',
  sampleQuantity: 0,
  transferRemark: '',
  sampleIds: ''
})

const formRules = {
  transferType: [{ required: true, message: '请选择交接类型', trigger: 'change' }],
  planId: [{ required: true, message: '请选择采样计划', trigger: 'change' }],
  samplerId: [{ required: true, message: '请选择采样员', trigger: 'change' }],
  transferTime: [{ required: true, message: '请选择交接时间', trigger: 'change' }]
}

const rejectForm = reactive({
  rejectReason: ''
})

const rejectRules = {
  rejectReason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '交接单号', dataIndex: 'transferNo', key: 'transferNo', width: 160 },
  { title: '计划编号', dataIndex: 'planNo', key: 'planNo', width: 140 },
  { title: '交接类型', dataIndex: 'transferType', key: 'transferType', width: 120 },
  { title: '采样员', dataIndex: 'samplerName', key: 'samplerName', width: 100 },
  { title: '接收员', dataIndex: 'receiverName', key: 'receiverName', width: 100 },
  { title: '交接时间', dataIndex: 'transferTime', key: 'transferTime', width: 170 },
  { title: '样品数量', dataIndex: 'sampleQuantity', key: 'sampleQuantity', width: 100 },
  { title: '状态', dataIndex: 'transferStatus', key: 'transferStatus', width: 100 },
  { title: '操作', key: 'action', width: 260, fixed: 'right' }
]

const sampleColumns = [
  { title: '序号', key: 'index', width: 60, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '样品编号', dataIndex: 'sampleNo', key: 'sampleNo', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 150 },
  { title: '样品类型', dataIndex: 'sampleType', key: 'sampleType', width: 100 },
  { title: '采样时间', dataIndex: 'samplingTime', key: 'samplingTime', width: 170 },
  { title: '状态', dataIndex: 'sampleStatus', key: 'sampleStatus', width: 100 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSampleTransferPage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchPlanList = async () => {
  try {
    const res = await getSamplingPlanList({ status: 4 })
    planList.value = res.data
  } catch (error) {
    console.error('Get plan list error:', error)
  }
}

const fetchSampleList = async (planId?: number) => {
  if (!planId) {
    sampleList.value = []
    selectedSampleIds.value = []
    return
  }
  sampleLoading.value = true
  try {
    const mockSamples: SampleRecordVO[] = [
      { id: 1, sampleNo: 'S202401001', sampleName: '环境空气样品001', sampleType: '环境空气', sampleStatus: 1, sampleStatusName: '已采样', samplingTime: '2024-01-15 10:30:00', createTime: '2024-01-15 10:30:00' },
      { id: 2, sampleNo: 'S202401002', sampleName: '环境空气样品002', sampleType: '环境空气', sampleStatus: 1, sampleStatusName: '已采样', samplingTime: '2024-01-15 11:00:00', createTime: '2024-01-15 11:00:00' },
      { id: 3, sampleNo: 'S202401003', sampleName: '地表水样品001', sampleType: '地表水', sampleStatus: 2, sampleStatusName: '已交接', samplingTime: '2024-01-15 14:00:00', createTime: '2024-01-15 14:00:00' },
      { id: 4, sampleNo: 'S202401004', sampleName: '土壤样品001', sampleType: '土壤', sampleStatus: 1, sampleStatusName: '已采样', samplingTime: '2024-01-15 15:30:00', createTime: '2024-01-15 15:30:00' }
    ]
    sampleList.value = mockSamples
  } finally {
    sampleLoading.value = false
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

const handleTransferTimeChange = (dates: any[]) => {
  if (dates && dates.length === 2) {
    queryParams.transferTimeStart = dates[0]
    queryParams.transferTimeEnd = dates[1]
  } else {
    queryParams.transferTimeStart = ''
    queryParams.transferTimeEnd = ''
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    transferNo: '',
    planId: undefined,
    transferType: undefined,
    transferStatus: undefined,
    samplerId: undefined,
    receiverId: undefined,
    transferTimeStart: '',
    transferTimeEnd: ''
  })
  transferTimeRange.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handlePlanChange = (planId: number) => {
  const plan = planList.value.find(item => item.id === planId)
  if (plan) {
    formData.planNo = plan.planNo
  }
  fetchSampleList(planId)
}

const handleSamplerChange = (samplerId: number) => {
  const user = userList.value.find(item => item.id === samplerId)
  if (user) {
    formData.samplerName = user.realName
  }
}

const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    transferNo: '',
    transferType: 1,
    planId: undefined,
    planNo: '',
    samplerId: undefined,
    samplerName: '',
    receiverId: undefined,
    receiverName: '',
    transferTime: '',
    sampleQuantity: 0,
    transferStatus: undefined,
    transferRemark: '',
    sampleIds: ''
  })
  selectedSampleIds.value = []
  sampleList.value = []
  formModalVisible.value = true
}

const handleView = async (record: SampleTransferVO) => {
  try {
    const res = await getSampleTransferById(record.id)
    transferDetail.value = res.data
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get transfer detail error:', error)
  }
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    if (selectedSampleIds.value.length === 0) {
      message.warning('请至少选择一个样品')
      return
    }
    submitting.value = true
    formData.sampleIds = selectedSampleIds.value.join(',')
    formData.sampleQuantity = selectedSampleIds.value.length
    await createSampleTransfer(formData)
    message.success('创建成功')
    formModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleConfirm = async (id: number) => {
  try {
    await confirmSampleTransfer(id)
    message.success('确认接收成功')
    fetchData()
  } catch (error) {
    console.error('Confirm error:', error)
  }
}

const handleReject = (record: SampleTransferVO) => {
  currentTransfer.value = record
  rejectForm.rejectReason = ''
  rejectModalVisible.value = true
}

const handleRejectSubmit = async () => {
  try {
    await rejectFormRef.value.validate()
    submitting.value = true
    await rejectSampleTransfer(currentTransfer.value!.id, rejectForm.rejectReason)
    message.success('驳回成功')
    rejectModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Reject error:', error)
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
  return colors[status ?? 0] || 'default'
}

const getSampleStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'default',
    1: 'blue',
    2: 'green',
    3: 'orange',
    4: 'red'
  }
  return colors[status ?? 0] || 'default'
}

onMounted(() => {
  fetchData()
  fetchPlanList()
  fetchUserList()
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
</style>
