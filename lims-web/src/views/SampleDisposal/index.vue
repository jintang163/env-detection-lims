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
import { ref, reactive, onMounted, computed } from 'vue'
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

const mockSamples: SampleVO[] = [
  { id: 1, sampleCode: 'S2024001001', sampleName: '废水样品A', sampleStatus: 5, sampleStatusName: '检测完成', matrixName: '废水', warningFlag: 1, warningMessage: '已过期', createTime: '2024-01-15 10:30:00' },
  { id: 2, sampleCode: 'S2024001002', sampleName: '废水样品B', sampleStatus: 5, sampleStatusName: '检测完成', matrixName: '废水', warningFlag: 0, createTime: '2024-01-15 10:35:00' },
  { id: 3, sampleCode: 'S2024001003', sampleName: '土壤样品A', sampleStatus: 6, sampleStatusName: '已过期', matrixName: '土壤', warningFlag: 1, warningMessage: '已过期', createTime: '2024-01-10 14:20:00' },
  { id: 4, sampleCode: 'S2024001004', sampleName: '大气样品A', sampleStatus: 5, sampleStatusName: '检测完成', matrixName: '大气', warningFlag: 0, createTime: '2024-01-12 09:15:00' },
  { id: 5, sampleCode: 'S2024001005', sampleName: '固体废物A', sampleStatus: 6, sampleStatusName: '已过期', matrixName: '固体废物', warningFlag: 1, warningMessage: '已过期', createTime: '2024-01-08 16:45:00' },
  { id: 6, sampleCode: 'S2024001006', sampleName: '废水样品C', sampleStatus: 5, sampleStatusName: '检测完成', matrixName: '废水', warningFlag: 0, createTime: '2024-01-18 11:00:00' },
  { id: 7, sampleCode: 'S2024001007', sampleName: '土壤样品B', sampleStatus: 5, sampleStatusName: '检测完成', matrixName: '土壤', warningFlag: 0, createTime: '2024-01-20 13:30:00' },
  { id: 8, sampleCode: 'S2024001008', sampleName: '大气样品B', sampleStatus: 6, sampleStatusName: '已过期', matrixName: '大气', warningFlag: 1, warningMessage: '已过期', createTime: '2024-01-05 08:00:00' }
]

const mockApprovalRecords: SampleDisposalApprovalVO[] = [
  { id: 1, disposalId: 1, disposalNo: 'DIS202406001', approvalNode: '部门经理审批', approverName: '张三', approvalResult: 1, approvalResultName: '同意', approvalOpinion: '同意处置，按规范执行', approvalTime: '2024-06-02 10:30:00' },
  { id: 2, disposalId: 1, disposalNo: 'DIS202406001', approvalNode: '质量负责人审批', approverName: '李四', approvalResult: 1, approvalResultName: '同意', approvalOpinion: '同意，注意环保要求', approvalTime: '2024-06-02 14:15:00' }
]

const mockData: SampleDisposalVO[] = [
  {
    id: 1,
    disposalNo: 'DIS202406001',
    sampleCount: 3,
    disposalReason: '样品已过期，需按规范处置',
    disposalMethod: 1,
    disposalMethodName: '高温焚烧',
    expectedDisposalDate: '2024-06-10',
    actualDisposalDate: '2024-06-10',
    applicantName: '王五',
    applyTime: '2024-06-01 09:00:00',
    disposalStatus: 5,
    disposalStatusName: '已执行',
    approvalStatus: 3,
    approvalStatusName: '已通过',
    disposalOperatorName: '赵六',
    witnessName: '钱七',
    disposalFile: '/files/disposal/DIS202406001.pdf',
    createTime: '2024-06-01 09:00:00'
  },
  {
    id: 2,
    disposalNo: 'DIS202406002',
    sampleCount: 2,
    disposalReason: '检测完成，留样期满',
    disposalMethod: 3,
    disposalMethodName: '专业回收',
    expectedDisposalDate: '2024-06-15',
    applicantName: '王五',
    applyTime: '2024-06-03 14:30:00',
    disposalStatus: 3,
    disposalStatusName: '已批准',
    approvalStatus: 3,
    approvalStatusName: '已通过',
    createTime: '2024-06-03 14:30:00'
  },
  {
    id: 3,
    disposalNo: 'DIS202406003',
    sampleCount: 5,
    disposalReason: '样品性质不稳定，需提前处置',
    disposalMethod: 2,
    disposalMethodName: '化学处理',
    expectedDisposalDate: '2024-06-12',
    applicantName: '孙八',
    applyTime: '2024-06-04 11:00:00',
    disposalStatus: 2,
    disposalStatusName: '审批中',
    approvalStatus: 2,
    approvalStatusName: '审批中',
    createTime: '2024-06-04 11:00:00'
  },
  {
    id: 4,
    disposalNo: 'DIS202406004',
    sampleCount: 2,
    disposalReason: '检测结果异常，需销毁重新采样',
    disposalMethod: 4,
    disposalMethodName: '深埋处理',
    expectedDisposalDate: '2024-06-08',
    applicantName: '周九',
    applyTime: '2024-06-02 16:20:00',
    disposalStatus: 4,
    disposalStatusName: '已驳回',
    approvalStatus: 4,
    approvalStatusName: '已驳回',
    createTime: '2024-06-02 16:20:00'
  },
  {
    id: 5,
    disposalNo: 'DIS202406005',
    sampleCount: 4,
    disposalReason: '留样期满，按规定处置',
    disposalMethod: 5,
    disposalMethodName: '其他',
    expectedDisposalDate: '2024-06-20',
    applicantName: '吴十',
    applyTime: '2024-06-05 08:45:00',
    disposalStatus: 1,
    disposalStatusName: '待审批',
    approvalStatus: 1,
    approvalStatusName: '待审批',
    createTime: '2024-06-05 08:45:00'
  }
]

const mockDetailMap: Record<number, SampleDisposalDetailVO> = {
  1: {
    ...mockData[0],
    samples: mockSamples.slice(0, 3),
    approvalRecords: mockApprovalRecords
  },
  2: {
    ...mockData[1],
    samples: mockSamples.slice(3, 5),
    approvalRecords: [
      { id: 3, disposalId: 2, disposalNo: 'DIS202406002', approvalNode: '部门经理审批', approverName: '张三', approvalResult: 1, approvalResultName: '同意', approvalOpinion: '同意回收', approvalTime: '2024-06-04 09:15:00' }
    ]
  },
  3: {
    ...mockData[2],
    samples: mockSamples.slice(2, 7),
    approvalRecords: [
      { id: 4, disposalId: 3, disposalNo: 'DIS202406003', approvalNode: '部门经理审批', approverName: '张三', approvalResult: 1, approvalResultName: '同意', approvalTime: '2024-06-04 15:00:00' }
    ]
  },
  4: {
    ...mockData[3],
    samples: mockSamples.slice(5, 7),
    approvalRecords: [
      { id: 5, disposalId: 4, disposalNo: 'DIS202406004', approvalNode: '部门经理审批', approverName: '张三', approvalResult: 2, approvalResultName: '驳回', approvalOpinion: '异常样品需先进行复检，确认后再处置', approvalTime: '2024-06-03 10:30:00' }
    ]
  },
  5: {
    ...mockData[4],
    samples: mockSamples.slice(1, 5),
    approvalRecords: []
  }
}

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
    await new Promise(resolve => setTimeout(resolve, 500))
    const filteredData = mockData.filter(item => {
      if (queryParams.disposalNo && !item.disposalNo.includes(queryParams.disposalNo)) return false
      if (queryParams.disposalStatus !== undefined && item.disposalStatus !== queryParams.disposalStatus) return false
      if (queryParams.approvalStatus !== undefined && item.approvalStatus !== queryParams.approvalStatus) return false
      return true
    })
    const start = (queryParams.pageNum! - 1) * queryParams.pageSize!
    const end = start + queryParams.pageSize!
    tableData.value = filteredData.slice(start, end)
    pagination.value.total = filteredData.length
  } finally {
    loading.value = false
  }
}

const fetchAvailableSamples = async () => {
  sampleLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 300))
    availableSamples.value = mockSamples.filter(s => s.sampleStatus === 5 || s.sampleStatus === 6)
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
    await new Promise(resolve => setTimeout(resolve, 800))
    message.success('处置申请提交成功')
    applyModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Apply error:', error)
  } finally {
    submitting.value = false
  }
}

const handleView = async (record: SampleDisposalVO) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 300))
    currentDetail.value = mockDetailMap[record.id]
    detailTab.value = 'basic'
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get detail error:', error)
  }
}

const handleApproval = (record: SampleDisposalVO) => {
  currentDisposalId.value = record.id
  currentDetail.value = mockDetailMap[record.id]
  Object.assign(approvalFormData, {
    id: record.id,
    approvalResult: 1,
    approvalOpinion: '',
    approvalNode: ''
  })
  approvalModalVisible.value = true
}

const handleApprovalSubmit = async () => {
  try {
    await approvalFormRef.value.validate()
    submitting.value = true
    await new Promise(resolve => setTimeout(resolve, 800))
    message.success(approvalFormData.approvalResult === 1 ? '审批通过' : '已驳回')
    approvalModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Approval error:', error)
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
    await new Promise(resolve => setTimeout(resolve, 800))
    message.success('处置执行成功')
    executeModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Execute error:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = async (id: number) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    message.success('取消成功')
    fetchData()
  } catch (error) {
    console.error('Cancel error:', error)
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
