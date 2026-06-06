<template>
  <div class="sample-disposal">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="处置单号">
            <a-input v-model:value="queryParams.disposalNo" placeholder="请输入处置单号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="样品编号">
            <a-input v-model:value="queryParams.sampleCode" placeholder="请输入样品编号" style="width: 140px" allow-clear />
          </a-form-item>
          <a-form-item label="样品名称">
            <a-input v-model:value="queryParams.sampleName" placeholder="请输入样品名称" style="width: 140px" allow-clear />
          </a-form-item>
          <a-form-item label="处置状态">
            <a-select v-model:value="queryParams.disposalStatus" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="1">待审批</a-select-option>
              <a-select-option :value="2">审批中</a-select-option>
              <a-select-option :value="3">已批准</a-select-option>
              <a-select-option :value="4">已驳回</a-select-option>
              <a-select-option :value="5">已执行</a-select-option>
              <a-select-option :value="6">已取消</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="审批状态">
            <a-select v-model:value="queryParams.approvalStatus" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">未提交</a-select-option>
              <a-select-option :value="1">待审批</a-select-option>
              <a-select-option :value="2">审批中</a-select-option>
              <a-select-option :value="3">已通过</a-select-option>
              <a-select-option :value="4">已驳回</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="创建时间">
            <a-range-picker
              v-model:value="createTimeRange"
              style="width: 260px"
              value-format="YYYY-MM-DD"
              placeholder="开始日期,结束日期"
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
          <a-button type="primary" @click="handleApplyDisposal">
            <PlusOutlined /> 申请处置
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
          <template v-if="column.key === 'disposalStatus'">
            <a-tag :color="getDisposalStatusColor(record.disposalStatus)">
              {{ record.disposalStatusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'approvalStatus'">
            <a-tag :color="getApprovalStatusColor(record.approvalStatus)">
              {{ record.approvalStatusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">
              <EyeOutlined /> 查看
            </a-button>
            <a-button type="link" size="small" @click="handleApproval(record)" v-if="record.approvalStatus === 1 || record.approvalStatus === 2">
              <CheckCircleOutlined /> 审批
            </a-button>
            <a-button type="link" size="small" @click="handleExecute(record)" v-if="record.disposalStatus === 3">
              <PlayCircleOutlined /> 执行
            </a-button>
            <a-popconfirm title="确定取消该处置申请吗？" @confirm="handleCancel(record.id)" v-if="record.disposalStatus === 1 || record.disposalStatus === 2">
              <a-button type="link" danger size="small">
                <StopOutlined /> 取消
              </a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="applyModalVisible"
      title="申请样品处置"
      width="800px"
      @ok="handleApplySubmit"
      @cancel="applyModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="applyFormRef" :model="applyFormData" :rules="applyFormRules" layout="vertical">
        <a-form-item label="选择样品" name="sampleIds">
          <a-table
            :columns="sampleColumns"
            :data-source="availableSamples"
            :loading="sampleLoading"
            :pagination="false"
            row-key="id"
            row-selection="checkbox"
            v-model:selectedRowKeys="selectedSampleIds"
            size="small"
            :scroll="{ y: 220 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'sampleStatus'">
                <a-tag :color="record.warningFlag === 1 ? 'red' : 'green'">
                  {{ record.sampleStatusName || '-' }}
                </a-tag>
              </template>
            </template>
          </a-table>
          <div style="margin-top: 8px; color: #999; font-size: 12px">
            <FileTextOutlined /> 仅显示已过期或检测完成的样品
          </div>
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="处置原因" name="disposalReason">
              <a-textarea v-model:value="applyFormData.disposalReason" placeholder="请输入处置原因" :rows="2" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="处置方式" name="disposalMethod">
              <a-select v-model:value="applyFormData.disposalMethod" placeholder="请选择处置方式">
                <a-select-option :value="1">高温焚烧</a-select-option>
                <a-select-option :value="2">化学处理</a-select-option>
                <a-select-option :value="3">专业回收</a-select-option>
                <a-select-option :value="4">深埋处理</a-select-option>
                <a-select-option :value="5">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="预计处置日期" name="expectedDisposalDate">
              <a-date-picker v-model:value="applyFormData.expectedDisposalDate" style="width: 100%" value-format="YYYY-MM-DD">
                <template #suffixIcon><CalendarOutlined /></template>
              </a-date-picker>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="申请人">
              <a-input v-model:value="applyFormData.applicantName" placeholder="请输入申请人" disabled>
                <template #prefix><UserOutlined /></template>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="applyFormData.remark" placeholder="请输入备注" :rows="2" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="approvalModalVisible"
      title="审批处置申请"
      width="600px"
      @ok="handleApprovalSubmit"
      @cancel="approvalModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="approvalFormRef" :model="approvalFormData" :rules="approvalFormRules" layout="vertical">
        <a-descriptions :column="1" bordered size="small" style="margin-bottom: 16px">
          <a-descriptions-item label="处置单号">{{ currentDetail?.disposalNo }}</a-descriptions-item>
          <a-descriptions-item label="处置原因">{{ currentDetail?.disposalReason }}</a-descriptions-item>
          <a-descriptions-item label="样品数量">{{ currentDetail?.sampleCount }} 个</a-descriptions-item>
        </a-descriptions>
        <a-form-item label="审批结果" name="approvalResult">
          <a-radio-group v-model:value="approvalFormData.approvalResult">
            <a-radio :value="1">同意</a-radio>
            <a-radio :value="2">驳回</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="审批意见" name="approvalOpinion">
          <a-textarea v-model:value="approvalFormData.approvalOpinion" placeholder="请输入审批意见" :rows="3" />
        </a-form-item>
        <a-form-item label="审批节点" name="approvalNode">
          <a-input v-model:value="approvalFormData.approvalNode" placeholder="请输入审批节点" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="executeModalVisible"
      title="执行样品处置"
      width="700px"
      @ok="handleExecuteSubmit"
      @cancel="executeModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="executeFormRef" :model="executeFormData" :rules="executeFormRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="实际处置日期" name="actualDisposalDate">
              <a-date-picker v-model:value="executeFormData.actualDisposalDate" style="width: 100%" value-format="YYYY-MM-DD">
                <template #suffixIcon><CalendarOutlined /></template>
              </a-date-picker>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="处置人" name="disposalOperatorName">
              <a-input v-model:value="executeFormData.disposalOperatorName" placeholder="请输入处置人">
                <template #prefix><UserOutlined /></template>
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="处置过程" name="disposalProcess">
          <a-textarea v-model:value="executeFormData.disposalProcess" placeholder="请详细描述处置过程" :rows="4" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="见证人" name="witnessName">
              <a-input v-model:value="executeFormData.witnessName" placeholder="请输入见证人">
                <template #prefix><UserOutlined /></template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="处置文件">
              <a-upload
                v-model:file-list="disposalFileList"
                action="/api/upload"
                :before-upload="beforeUpload"
                @change="handleFileUploadChange"
                :max-count="3"
              >
                <a-button>
                  <UploadOutlined /> 上传文件
                </a-button>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="executeFormData.remark" placeholder="请输入备注" :rows="2" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="处置详情"
      width="900px"
      :footer="null"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="处置单号">{{ currentDetail?.disposalNo }}</a-descriptions-item>
            <a-descriptions-item label="样品数量">{{ currentDetail?.sampleCount }} 个</a-descriptions-item>
            <a-descriptions-item label="处置原因" :span="2">{{ currentDetail?.disposalReason }}</a-descriptions-item>
            <a-descriptions-item label="处置方式">
              <a-tag color="blue">{{ currentDetail?.disposalMethodName }}</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="申请人">{{ currentDetail?.applicantName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="申请时间">{{ currentDetail?.applyTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="预计处置日期">{{ currentDetail?.expectedDisposalDate || '-' }}</a-descriptions-item>
            <a-descriptions-item label="实际处置日期">{{ currentDetail?.actualDisposalDate || '-' }}</a-descriptions-item>
            <a-descriptions-item label="处置状态">
              <a-tag :color="getDisposalStatusColor(currentDetail?.disposalStatus)">
                {{ currentDetail?.disposalStatusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="审批状态">
              <a-tag :color="getApprovalStatusColor(currentDetail?.approvalStatus)">
                {{ currentDetail?.approvalStatusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="处置人">{{ currentDetail?.disposalOperatorName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="见证人">{{ currentDetail?.witnessName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="处置文件" v-if="currentDetail?.disposalFile">
              <a :href="currentDetail.disposalFile" target="_blank">查看文件</a>
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ currentDetail?.createTime }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ currentDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="samples" tab="样品列表">
          <a-table
            :columns="sampleDetailColumns"
            :data-source="currentDetail?.samples || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'sampleStatus'">
                <a-tag :color="record.warningFlag === 1 ? 'red' : 'green'">
                  {{ record.sampleStatusName || '-' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="approvals" tab="审批记录">
          <a-timeline v-if="currentDetail?.approvalRecords?.length">
            <a-timeline-item v-for="item in currentDetail.approvalRecords" :key="item.id">
              <template #dot>
                <a-badge :status="item.approvalResult === 1 ? 'success' : 'error'" />
              </template>
              <a-card size="small" style="margin-bottom: 8px">
                <div class="approval-header">
                  <a-tag :color="item.approvalResult === 1 ? 'green' : 'red'">
                    {{ item.approvalResultName }}
                  </a-tag>
                  <span class="approval-node">{{ item.approvalNode }}</span>
                  <span class="approval-time">{{ item.approvalTime }}</span>
                  <span class="approval-person">审批人：{{ item.approverName || '-' }}</span>
                </div>
                <div class="approval-opinion">
                  <span v-if="item.approvalOpinion">{{ item.approvalOpinion }}</span>
                  <span v-else style="color: #999">无审批意见</span>
                </div>
              </a-card>
            </a-timeline-item>
          </a-timeline>
          <a-empty v-else description="暂无审批记录" />
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EyeOutlined,
  CheckCircleOutlined,
  PlayCircleOutlined,
  StopOutlined,
  FileTextOutlined,
  UploadOutlined,
  UserOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import type {
  SampleDisposalQuery,
  SampleDisposalApplyDTO,
  SampleDisposalApprovalDTO,
  SampleDisposalExecuteDTO,
  SampleDisposalVO,
  SampleDisposalDetailVO,
  SampleDisposalApprovalVO,
  SampleVO
} from '@/types'
import {
  getSampleDisposalPage,
  getSampleDisposalById,
  applyDisposal,
  approvalDisposal,
  executeDisposal,
  cancelDisposal,
  getSampleList
} from '@/api/sample'

const loading = ref(false)
const sampleLoading = ref(false)
const submitting = ref(false)
const detailTab = ref('basic')
const createTimeRange = ref<any[]>([])
const selectedSampleIds = ref<number[]>([])
const disposalFileList = ref<any[]>([])

const applyModalVisible = ref(false)
const approvalModalVisible = ref(false)
const executeModalVisible = ref(false)
const detailModalVisible = ref(false)

const applyFormRef = ref()
const approvalFormRef = ref()
const executeFormRef = ref()
const currentDisposalId = ref<number>()
const currentDetail = ref<SampleDisposalDetailVO | null>(null)

const queryParams = reactive<SampleDisposalQuery>({
  pageNum: 1,
  pageSize: 10,
  disposalNo: '',
  sampleCode: '',
  sampleName: '',
  disposalStatus: undefined,
  applicantId: undefined,
  approvalStatus: undefined,
  createTimeStart: '',
  createTimeEnd: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<SampleDisposalVO[]>([])
const availableSamples = ref<SampleVO[]>([])

const applyFormData = reactive<SampleDisposalApplyDTO>({
  sampleIds: [],
  disposalReason: '',
  disposalMethod: 1,
  expectedDisposalDate: '',
  applicantName: '当前用户',
  applyTime: '',
  remark: ''
})

const approvalFormData = reactive<SampleDisposalApprovalDTO>({
  id: 0,
  approvalResult: 1,
  approvalOpinion: '',
  approvalNode: ''
})

const executeFormData = reactive<SampleDisposalExecuteDTO>({
  id: 0,
  actualDisposalDate: '',
  disposalOperatorName: '',
  disposalProcess: '',
  witnessName: '',
  disposalFile: '',
  remark: ''
})

const applyFormRules = {
  sampleIds: [{ required: true, message: '请至少选择一个样品', trigger: 'change' }],
  disposalReason: [{ required: true, message: '请输入处置原因', trigger: 'blur' }],
  disposalMethod: [{ required: true, message: '请选择处置方式', trigger: 'change' }],
  expectedDisposalDate: [{ required: true, message: '请选择预计处置日期', trigger: 'change' }]
}

const approvalFormRules = {
  approvalResult: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
  approvalOpinion: [{ required: true, message: '请输入审批意见', trigger: 'blur' }],
  approvalNode: [{ required: true, message: '请输入审批节点', trigger: 'blur' }]
}

const executeFormRules = {
  actualDisposalDate: [{ required: true, message: '请选择实际处置日期', trigger: 'change' }],
  disposalOperatorName: [{ required: true, message: '请输入处置人', trigger: 'blur' }],
  disposalProcess: [{ required: true, message: '请输入处置过程', trigger: 'blur' }],
  witnessName: [{ required: true, message: '请输入见证人', trigger: 'blur' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '处置单号', dataIndex: 'disposalNo', key: 'disposalNo', width: 140 },
  { title: '样品数量', dataIndex: 'sampleCount', key: 'sampleCount', width: 100, customRender: ({ text }: { text: number }) => `${text} 个` },
  { title: '处置原因', dataIndex: 'disposalReason', key: 'disposalReason', width: 200, ellipsis: true },
  { title: '处置方式', dataIndex: 'disposalMethodName', key: 'disposalMethod', width: 100 },
  { title: '申请人', dataIndex: 'applicantName', key: 'applicantName', width: 100 },
  { title: '申请时间', dataIndex: 'applyTime', key: 'applyTime', width: 170 },
  { title: '处置状态', dataIndex: 'disposalStatus', key: 'disposalStatus', width: 100 },
  { title: '审批状态', dataIndex: 'approvalStatus', key: 'approvalStatus', width: 100 },
  { title: '预计处置日期', dataIndex: 'expectedDisposalDate', key: 'expectedDisposalDate', width: 120 },
  { title: '操作', key: 'action', width: 240, fixed: 'right' }
]

const sampleColumns = [
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 150 },
  { title: '样品类型', dataIndex: 'matrixName', key: 'matrixName', width: 100 },
  { title: '样品状态', dataIndex: 'sampleStatus', key: 'sampleStatus', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '预警信息', dataIndex: 'warningMessage', key: 'warningMessage', width: 120 }
]

const sampleDetailColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 60, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 150 },
  { title: '样品类型', dataIndex: 'matrixName', key: 'matrixName', width: 100 },
  { title: '样品状态', dataIndex: 'sampleStatus', key: 'sampleStatus', width: 100 },
  { title: '采样时间', dataIndex: 'samplingTime', key: 'samplingTime', width: 170 },
  { title: '预警信息', dataIndex: 'warningMessage', key: 'warningMessage', width: 120 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSampleDisposalPage(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    } else {
      message.error(res.msg || '获取数据失败')
      tableData.value = []
      pagination.value.total = 0
    }
  } catch (error: any) {
    message.error(error.message || '获取数据失败')
    tableData.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
}

const fetchAvailableSamples = async () => {
  sampleLoading.value = true
  try {
    const res = await getSampleList({ disposalAvailable: true })
    if (res.code === 200) {
      availableSamples.value = res.data
    } else {
      message.error(res.msg || '获取可处置样品列表失败')
      availableSamples.value = []
    }
  } catch (error: any) {
    message.error(error.message || '获取可处置样品列表失败')
    availableSamples.value = []
  } finally {
    sampleLoading.value = false
  }
}

const handleQuery = () => {
  if (createTimeRange.value?.length === 2) {
    queryParams.createTimeStart = createTimeRange.value[0]
    queryParams.createTimeEnd = createTimeRange.value[1]
  } else {
    queryParams.createTimeStart = ''
    queryParams.createTimeEnd = ''
  }
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    disposalNo: '',
    sampleCode: '',
    sampleName: '',
    disposalStatus: undefined,
    applicantId: undefined,
    approvalStatus: undefined,
    createTimeStart: '',
    createTimeEnd: ''
  })
  createTimeRange.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleApplyDisposal = () => {
  selectedSampleIds.value = []
  disposalFileList.value = []
  Object.assign(applyFormData, {
    sampleIds: [],
    disposalReason: '',
    disposalMethod: 1,
    expectedDisposalDate: '',
    applicantName: '当前用户',
    applyTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    remark: ''
  })
  fetchAvailableSamples()
  applyModalVisible.value = true
}

const handleApplySubmit = async () => {
  try {
    applyFormData.sampleIds = selectedSampleIds.value
    await applyFormRef.value.validate()
    submitting.value = true
    const res = await applyDisposal(applyFormData)
    if (res.code === 200) {
      message.success('处置申请提交成功')
      applyModalVisible.value = false
      fetchData()
    } else {
      message.error(res.msg || '提交失败')
    }
  } catch (error: any) {
    message.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

const handleView = async (record: SampleDisposalVO) => {
  try {
    const res = await getSampleDisposalById(record.id)
    if (res.code === 200) {
      currentDetail.value = res.data
      detailTab.value = 'basic'
      detailModalVisible.value = true
    } else {
      message.error(res.msg || '获取详情失败')
    }
  } catch (error: any) {
    message.error(error.message || '获取详情失败')
  }
}

const handleApproval = async (record: SampleDisposalVO) => {
  try {
    currentDisposalId.value = record.id
    const res = await getSampleDisposalById(record.id)
    if (res.code === 200) {
      currentDetail.value = res.data
      Object.assign(approvalFormData, {
        id: record.id,
        approvalResult: 1,
        approvalOpinion: '',
        approvalNode: ''
      })
      approvalModalVisible.value = true
    } else {
      message.error(res.msg || '获取详情失败')
    }
  } catch (error: any) {
    message.error(error.message || '获取详情失败')
  }
}

const handleApprovalSubmit = async () => {
  try {
    await approvalFormRef.value.validate()
    submitting.value = true
    const res = await approvalDisposal(approvalFormData)
    if (res.code === 200) {
      message.success(approvalFormData.approvalResult === 1 ? '审批通过' : '已驳回')
      approvalModalVisible.value = false
      fetchData()
    } else {
      message.error(res.msg || '审批失败')
    }
  } catch (error: any) {
    message.error(error.message || '审批失败')
  } finally {
    submitting.value = false
  }
}

const handleExecute = (record: SampleDisposalVO) => {
  currentDisposalId.value = record.id
  disposalFileList.value = []
  Object.assign(executeFormData, {
    id: record.id,
    actualDisposalDate: dayjs().format('YYYY-MM-DD'),
    disposalOperatorName: '',
    disposalProcess: '',
    witnessName: '',
    disposalFile: '',
    remark: ''
  })
  executeModalVisible.value = true
}

const handleExecuteSubmit = async () => {
  try {
    await executeFormRef.value.validate()
    submitting.value = true
    const res = await executeDisposal(executeFormData)
    if (res.code === 200) {
      message.success('处置执行成功')
      executeModalVisible.value = false
      fetchData()
    } else {
      message.error(res.msg || '执行失败')
    }
  } catch (error: any) {
    message.error(error.message || '执行失败')
  } finally {
    submitting.value = false
  }
}

const handleCancel = async (id: number) => {
  try {
    const res = await cancelDisposal(id)
    if (res.code === 200) {
      message.success('取消成功')
      fetchData()
    } else {
      message.error(res.msg || '取消失败')
    }
  } catch (error: any) {
    message.error(error.message || '取消失败')
  }
}

const beforeUpload = (file: File) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('文件大小不能超过10MB')
    return false
  }
  return true
}

const handleFileUploadChange = (info: any) => {
  if (info.file.status === 'done') {
    executeFormData.disposalFile = info.file.response?.data?.url
  }
}

const getDisposalStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 1: 'orange', 2: 'blue', 3: 'green', 4: 'red', 5: 'gray', 6: 'default' }
  return colors[status || 0] || 'default'
}

const getApprovalStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 0: 'default', 1: 'orange', 2: 'blue', 3: 'green', 4: 'red' }
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

.approval-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.approval-node {
  font-weight: 500;
  color: #333;
}

.approval-time {
  color: #999;
  font-size: 12px;
}

.approval-person {
  color: #666;
  font-size: 12px;
  margin-left: auto;
}

.approval-opinion {
  color: #333;
  line-height: 1.6;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
