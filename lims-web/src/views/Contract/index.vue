<template>
  <div class="contract">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="合同编号">
            <a-input v-model:value="queryParams.contractNo" placeholder="请输入合同编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="合同名称">
            <a-input v-model:value="queryParams.contractName" placeholder="请输入合同名称" style="width: 180px" allow-clear />
          </a-form-item>
          <a-form-item label="客户名称">
            <a-input v-model:value="queryParams.customerName" placeholder="请输入客户名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="合同类型">
            <a-select v-model:value="queryParams.contractType" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="1">检测合同</a-select-option>
              <a-select-option :value="2">服务合同</a-select-option>
              <a-select-option :value="3">框架合同</a-select-option>
              <a-select-option :value="4">其他</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.status" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">草稿</a-select-option>
              <a-select-option :value="1">待审批</a-select-option>
              <a-select-option :value="2">审批通过</a-select-option>
              <a-select-option :value="3">审批驳回</a-select-option>
              <a-select-option :value="4">履约中</a-select-option>
              <a-select-option :value="5">已完成</a-select-option>
              <a-select-option :value="6">已终止</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="签订日期">
            <a-range-picker
              v-model:value="signDateRange"
              style="width: 260px"
              value-format="YYYY-MM-DD"
              @change="handleSignDateChange"
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
            <PlusOutlined /> 新增合同
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
          <template v-if="column.key === 'contractType'">
            <a-tag :color="getContractTypeColor(record.contractType)">
              {{ record.contractTypeName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'approvalStatus'">
            <a-tag :color="getApprovalStatusColor(record.approvalStatus)">
              {{ record.approvalStatusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'contractAmount'">
            ¥{{ formatMoney(record.contractAmount) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
            <template v-if="record.status === 0">
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-button type="link" size="small" @click="handleSubmitApproval(record)">提交审批</a-button>
              <a-popconfirm title="确定删除该合同吗？" @confirm="handleDelete(record.id)">
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </template>
            <template v-else-if="record.approvalStatus === 1">
              <a-button type="link" size="small" @click="handleApproval(record)">审批</a-button>
            </template>
            <template v-else-if="record.status === 2 || record.status === 4">
              <a-button type="link" size="small" @click="handleChange(record)">变更登记</a-button>
              <a-button type="link" size="small" @click="handlePerformance(record)">履约登记</a-button>
              <a-button type="link" size="small" @click="handleTerminate(record)">合同终止</a-button>
              <a-button type="link" size="small" @click="handleComplete(record)">合同完成</a-button>
            </template>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      :title="formModalTitle"
      width="800px"
      @ok="handleFormSubmit"
      @cancel="formModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="16">
            <a-form-item label="合同名称" name="contractName">
              <a-input v-model:value="formData.contractName" placeholder="请输入合同名称" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="合同类型" name="contractType">
              <a-select v-model:value="formData.contractType" placeholder="请选择合同类型">
                <a-select-option :value="1">检测合同</a-select-option>
                <a-select-option :value="2">服务合同</a-select-option>
                <a-select-option :value="3">框架合同</a-select-option>
                <a-select-option :value="4">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="客户" name="customerId">
          <a-select
            v-model:value="formData.customerId"
            placeholder="请选择客户"
            show-search
            :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
            @change="handleCustomerChange"
          >
            <a-select-option v-for="item in customerList" :key="item.id" :value="item.id">
              {{ item.customerName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="签订日期" name="signDate">
              <a-date-picker v-model:value="formData.signDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="生效日期" name="effectiveDate">
              <a-date-picker v-model:value="formData.effectiveDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="到期日期" name="expireDate">
              <a-date-picker v-model:value="formData.expireDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="合同金额（元）" name="contractAmount">
          <a-input-number
            v-model:value="formData.contractAmount"
            :min="0"
            :precision="2"
            :step="100"
            style="width: 100%"
            placeholder="请输入合同金额"
          />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="甲方签字人">
              <a-input v-model:value="formData.signingPartyA" placeholder="请输入甲方签字人" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="乙方签字人">
              <a-input v-model:value="formData.signingPartyB" placeholder="请输入乙方签字人" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="付款条款">
          <a-textarea v-model:value="formData.paymentTerms" :rows="2" placeholder="请输入付款条款" />
        </a-form-item>
        <a-form-item label="履约要求">
          <a-textarea v-model:value="formData.performanceRequirements" :rows="2" placeholder="请输入履约要求" />
        </a-form-item>
        <a-form-item label="违约责任">
          <a-textarea v-model:value="formData.liabilityForBreach" :rows="2" placeholder="请输入违约责任" />
        </a-form-item>
        <a-form-item label="争议解决">
          <a-textarea v-model:value="formData.disputeResolution" :rows="2" placeholder="请输入争议解决方式" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="合同详情"
      width="1000px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="合同编号">{{ contractDetail?.contractNo }}</a-descriptions-item>
            <a-descriptions-item label="合同名称">{{ contractDetail?.contractName }}</a-descriptions-item>
            <a-descriptions-item label="合同类型">
              <a-tag :color="getContractTypeColor(contractDetail?.contractType)">
                {{ contractDetail?.contractTypeName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="客户名称">{{ contractDetail?.customerName }}</a-descriptions-item>
            <a-descriptions-item label="签订日期">{{ contractDetail?.signDate }}</a-descriptions-item>
            <a-descriptions-item label="生效日期">{{ contractDetail?.effectiveDate }}</a-descriptions-item>
            <a-descriptions-item label="到期日期">{{ contractDetail?.expireDate }}</a-descriptions-item>
            <a-descriptions-item label="合同金额">¥{{ formatMoney(contractDetail?.contractAmount || 0) }}</a-descriptions-item>
            <a-descriptions-item label="状态">
              <a-tag :color="getStatusColor(contractDetail?.status)">
                {{ contractDetail?.statusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="审批状态">
              <a-tag :color="getApprovalStatusColor(contractDetail?.approvalStatus)">
                {{ contractDetail?.approvalStatusName || '-' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="甲方签字人">{{ contractDetail?.signingPartyA || '-' }}</a-descriptions-item>
            <a-descriptions-item label="乙方签字人">{{ contractDetail?.signingPartyB || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建人">{{ contractDetail?.createByName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ contractDetail?.createTime }}</a-descriptions-item>
            <a-descriptions-item label="付款条款" :span="2">{{ contractDetail?.paymentTerms || '-' }}</a-descriptions-item>
            <a-descriptions-item label="履约要求" :span="2">{{ contractDetail?.performanceRequirements || '-' }}</a-descriptions-item>
            <a-descriptions-item label="违约责任" :span="2">{{ contractDetail?.liabilityForBreach || '-' }}</a-descriptions-item>
            <a-descriptions-item label="争议解决" :span="2">{{ contractDetail?.disputeResolution || '-' }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ contractDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="approval" tab="审批记录">
          <a-table
            :columns="approvalColumns"
            :data-source="contractDetail?.approvals || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'approvalResult'">
                <a-tag :color="record.approvalResult === 1 ? 'green' : record.approvalResult === 2 ? 'red' : 'default'">
                  {{ record.approvalResultName || '-' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="change" tab="变更记录">
          <a-table
            :columns="changeColumns"
            :data-source="contractDetail?.changes || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'changeType'">
                <a-tag :color="getChangeTypeColor(record.changeType)">
                  {{ record.changeTypeName }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="performance" tab="履约跟踪">
          <div class="detail-toolbar">
            <a-button type="primary" size="small" @click="handlePerformanceAdd">
              <PlusOutlined /> 新增履约
            </a-button>
          </div>
          <a-table
            :columns="performanceColumns"
            :data-source="contractDetail?.performances || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'performanceStatus'">
                <a-tag :color="getPerformanceStatusColor(record.performanceStatus)">
                  {{ record.performanceStatusName }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <a-modal
      v-model:open="approvalModalVisible"
      title="合同审批"
      width="500px"
      @ok="handleApprovalSubmit"
      @cancel="approvalModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-descriptions :column="1" bordered size="small" style="margin-bottom: 16px">
        <a-descriptions-item label="合同编号">{{ currentContract?.contractNo }}</a-descriptions-item>
        <a-descriptions-item label="合同名称">{{ currentContract?.contractName }}</a-descriptions-item>
        <a-descriptions-item label="客户名称">{{ currentContract?.customerName }}</a-descriptions-item>
      </a-descriptions>
      <a-form
        ref="approvalFormRef"
        :model="approvalFormData"
        :rules="approvalFormRules"
        layout="vertical"
      >
        <a-form-item label="审批结果" name="approvalResult">
          <a-radio-group v-model:value="approvalFormData.approvalResult">
            <a-radio :value="1">通过</a-radio>
            <a-radio :value="2">驳回</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="审批意见" name="approvalOpinion">
          <a-textarea v-model:value="approvalFormData.approvalOpinion" :rows="3" placeholder="请输入审批意见" />
        </a-form-item>
        <a-form-item label="审批节点">
          <a-input v-model:value="approvalFormData.approvalNode" placeholder="请输入审批节点" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="changeModalVisible"
      title="合同变更登记"
      width="600px"
      @ok="handleChangeSubmit"
      @cancel="changeModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="changeFormRef"
        :model="changeFormData"
        :rules="changeFormRules"
        layout="vertical"
      >
        <a-form-item label="变更类型" name="changeType">
          <a-select v-model:value="changeFormData.changeType" placeholder="请选择变更类型">
            <a-select-option :value="1">金额变更</a-select-option>
            <a-select-option :value="2">时间变更</a-select-option>
            <a-select-option :value="3">内容变更</a-select-option>
            <a-select-option :value="4">条款变更</a-select-option>
            <a-select-option :value="5">其他</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="变更内容" name="changeContent">
          <a-textarea v-model:value="changeFormData.changeContent" :rows="3" placeholder="请输入变更内容" />
        </a-form-item>
        <a-form-item label="变更原因" name="changeReason">
          <a-textarea v-model:value="changeFormData.changeReason" :rows="3" placeholder="请输入变更原因" />
        </a-form-item>
        <a-form-item label="变更时间">
          <a-date-picker v-model:value="changeFormData.changeTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="performanceModalVisible"
      title="履约进度登记"
      width="600px"
      @ok="handlePerformanceSubmit"
      @cancel="performanceModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="performanceFormRef"
        :model="performanceFormData"
        :rules="performanceFormRules"
        layout="vertical"
      >
        <a-form-item label="履约节点" name="performanceNode">
          <a-input v-model:value="performanceFormData.performanceNode" placeholder="请输入履约节点，如：首付款、检测完成、报告交付等" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="计划日期" name="planDate">
              <a-date-picker v-model:value="performanceFormData.planDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="实际日期">
              <a-date-picker v-model:value="performanceFormData.actualDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="履约状态" name="performanceStatus">
          <a-select v-model:value="performanceFormData.performanceStatus" placeholder="请选择履约状态">
            <a-select-option :value="0">未开始</a-select-option>
            <a-select-option :value="1">进行中</a-select-option>
            <a-select-option :value="2">已完成</a-select-option>
            <a-select-option :value="3">已逾期</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="履约说明">
          <a-textarea v-model:value="performanceFormData.performanceDesc" :rows="2" placeholder="请输入履约说明" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="performanceFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="terminateModalVisible"
      title="合同终止"
      width="500px"
      @ok="handleTerminateSubmit"
      @cancel="terminateModalVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="终止原因">
          <a-textarea v-model:value="terminateReason" :rows="4" placeholder="请输入终止原因" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import type {
  ContractQuery,
  ContractSaveDTO,
  ContractVO,
  ContractDetailVO,
  ContractApprovalDTO,
  ContractChangeSaveDTO,
  ContractPerformanceSaveDTO
} from '@/types'
import {
  getContractPage,
  getContractById,
  addContract,
  updateContract,
  deleteContract,
  submitApproval,
  approval,
  addChange,
  addPerformance,
  terminateContract,
  completeContract,
  getContractSelectList
} from '@/api/contract'
import { getCustomerSelectList } from '@/api/customer'

const loading = ref(false)
const submitting = ref(false)
const detailTab = ref('basic')

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)
const approvalModalVisible = ref(false)
const changeModalVisible = ref(false)
const performanceModalVisible = ref(false)
const terminateModalVisible = ref(false)

const formRef = ref()
const approvalFormRef = ref()
const changeFormRef = ref()
const performanceFormRef = ref()
const isEdit = ref(false)
const currentContractId = ref<number>()
const currentContract = ref<ContractVO | null>(null)
const signDateRange = ref<any[]>([])
const terminateReason = ref('')

const customerList = ref<any[]>([])
const contractSelectList = ref<any[]>([])

const queryParams = reactive<ContractQuery>({
  pageNum: 1,
  pageSize: 10,
  contractNo: '',
  contractName: '',
  customerName: '',
  contractType: undefined,
  status: undefined,
  signDateStart: '',
  signDateEnd: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<ContractVO[]>([])
const contractDetail = ref<ContractDetailVO | null>(null)

const formData = reactive<ContractSaveDTO>({
  contractName: '',
  customerId: 0,
  customerName: '',
  signDate: '',
  effectiveDate: '',
  expireDate: '',
  contractAmount: 0
})

const approvalFormData = reactive<ContractApprovalDTO>({
  id: 0,
  approvalResult: 1,
  approvalOpinion: '',
  approvalNode: ''
})

const changeFormData = reactive<ContractChangeSaveDTO>({
  contractId: 0,
  changeType: 1,
  changeContent: '',
  changeReason: ''
})

const performanceFormData = reactive<ContractPerformanceSaveDTO>({
  contractId: 0,
  performanceNode: '',
  planDate: '',
  performanceStatus: 1
})

const formRules = {
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  contractType: [{ required: true, message: '请选择合同类型', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  signDate: [{ required: true, message: '请选择签订日期', trigger: 'change' }],
  effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }],
  expireDate: [{ required: true, message: '请选择到期日期', trigger: 'change' }],
  contractAmount: [{ required: true, message: '请输入合同金额', trigger: 'blur' }]
}

const approvalFormRules = {
  approvalResult: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
  approvalOpinion: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

const changeFormRules = {
  changeType: [{ required: true, message: '请选择变更类型', trigger: 'change' }],
  changeContent: [{ required: true, message: '请输入变更内容', trigger: 'blur' }],
  changeReason: [{ required: true, message: '请输入变更原因', trigger: 'blur' }]
}

const performanceFormRules = {
  performanceNode: [{ required: true, message: '请输入履约节点', trigger: 'blur' }],
  planDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }],
  performanceStatus: [{ required: true, message: '请选择履约状态', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '合同编号', dataIndex: 'contractNo', key: 'contractNo', width: 160 },
  { title: '合同名称', dataIndex: 'contractName', key: 'contractName', width: 200, ellipsis: true },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 180, ellipsis: true },
  { title: '合同类型', dataIndex: 'contractType', key: 'contractType', width: 100 },
  { title: '合同金额', dataIndex: 'contractAmount', key: 'contractAmount', width: 120 },
  { title: '签订日期', dataIndex: 'signDate', key: 'signDate', width: 120 },
  { title: '到期日期', dataIndex: 'expireDate', key: 'expireDate', width: 120 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '审批状态', dataIndex: 'approvalStatus', key: 'approvalStatus', width: 100 },
  { title: '操作', key: 'action', width: 320, fixed: 'right' }
]

const approvalColumns = [
  { title: '审批节点', dataIndex: 'approvalNode', key: 'approvalNode', width: 120 },
  { title: '审批人', dataIndex: 'approverName', key: 'approverName', width: 100 },
  { title: '审批结果', dataIndex: 'approvalResult', key: 'approvalResult', width: 100 },
  { title: '审批意见', dataIndex: 'approvalOpinion', key: 'approvalOpinion' },
  { title: '审批时间', dataIndex: 'approvalTime', key: 'approvalTime', width: 170 }
]

const changeColumns = [
  { title: '变更类型', dataIndex: 'changeType', key: 'changeType', width: 100 },
  { title: '变更内容', dataIndex: 'changeContent', key: 'changeContent' },
  { title: '变更原因', dataIndex: 'changeReason', key: 'changeReason' },
  { title: '变更时间', dataIndex: 'changeTime', key: 'changeTime', width: 170 },
  { title: '操作人', dataIndex: 'operatorName', key: 'operatorName', width: 100 }
]

const performanceColumns = [
  { title: '履约节点', dataIndex: 'performanceNode', key: 'performanceNode', width: 150 },
  { title: '计划日期', dataIndex: 'planDate', key: 'planDate', width: 120 },
  { title: '实际日期', dataIndex: 'actualDate', key: 'actualDate', width: 120 },
  { title: '履约状态', dataIndex: 'performanceStatus', key: 'performanceStatus', width: 100 },
  { title: '履约说明', dataIndex: 'performanceDesc', key: 'performanceDesc' },
  { title: '备注', dataIndex: 'remark', key: 'remark' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getContractPage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchCustomerList = async () => {
  try {
    const res = await getCustomerSelectList()
    customerList.value = res.data
  } catch (error) {
    console.error('Get customer list error:', error)
  }
}

const handleSignDateChange = (dates: any[]) => {
  if (dates && dates.length === 2) {
    queryParams.signDateStart = dates[0]
    queryParams.signDateEnd = dates[1]
  } else {
    queryParams.signDateStart = ''
    queryParams.signDateEnd = ''
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    contractNo: '',
    contractName: '',
    customerName: '',
    contractType: undefined,
    status: undefined,
    signDateStart: '',
    signDateEnd: ''
  })
  signDateRange.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleCustomerChange = (customerId: number) => {
  const customer = customerList.value.find(item => item.id === customerId)
  if (customer) {
    formData.customerName = customer.customerName
  }
}

const handleAdd = () => {
  isEdit.value = false
  formModalTitle.value = '新增合同'
  Object.assign(formData, {
    id: undefined,
    contractName: '',
    contractType: undefined,
    customerId: 0,
    customerName: '',
    signDate: '',
    effectiveDate: '',
    expireDate: '',
    contractAmount: 0,
    paymentTerms: '',
    performanceRequirements: '',
    liabilityForBreach: '',
    disputeResolution: '',
    signingPartyA: '',
    signingPartyB: '',
    remark: ''
  })
  formModalVisible.value = true
}

const handleEdit = (record: ContractVO) => {
  isEdit.value = true
  formModalTitle.value = '编辑合同'
  Object.assign(formData, {
    id: record.id,
    contractName: record.contractName,
    contractType: record.contractType,
    customerId: record.customerId,
    customerName: record.customerName,
    signDate: record.signDate,
    effectiveDate: record.effectiveDate,
    expireDate: record.expireDate,
    contractAmount: record.contractAmount,
    paymentTerms: '',
    performanceRequirements: '',
    liabilityForBreach: '',
    disputeResolution: '',
    signingPartyA: '',
    signingPartyB: '',
    remark: record.remark
  })
  formModalVisible.value = true
}

const handleView = async (record: ContractVO) => {
  try {
    const res = await getContractById(record.id)
    contractDetail.value = res.data
    detailTab.value = 'basic'
    currentContractId.value = record.id
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get contract detail error:', error)
  }
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    if (formData.effectiveDate && formData.expireDate && dayjs(formData.effectiveDate).isAfter(formData.expireDate)) {
      message.error('生效日期不能晚于到期日期')
      return
    }
    submitting.value = true
    if (isEdit.value) {
      await updateContract(formData)
      message.success('更新成功')
    } else {
      await addContract(formData)
      message.success('新增成功')
    }
    formModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteContract(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const handleSubmitApproval = async (record: ContractVO) => {
  try {
    await submitApproval(record.id)
    message.success('提交审批成功')
    fetchData()
  } catch (error) {
    console.error('Submit approval error:', error)
  }
}

const handleApproval = (record: ContractVO) => {
  currentContractId.value = record.id
  currentContract.value = record
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
    await approval(approvalFormData)
    message.success('审批成功')
    approvalModalVisible.value = false
    fetchData()
    if (currentContractId.value) {
      const res = await getContractById(currentContractId.value)
      contractDetail.value = res.data
    }
  } catch (error) {
    console.error('Approval submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleChange = (record: ContractVO) => {
  currentContractId.value = record.id
  Object.assign(changeFormData, {
    contractId: record.id,
    changeType: 1,
    changeContent: '',
    changeReason: '',
    changeTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
  })
  changeModalVisible.value = true
}

const handleChangeSubmit = async () => {
  try {
    await changeFormRef.value.validate()
    submitting.value = true
    await addChange(changeFormData)
    message.success('变更登记成功')
    changeModalVisible.value = false
    fetchData()
    if (currentContractId.value) {
      const res = await getContractById(currentContractId.value)
      contractDetail.value = res.data
    }
  } catch (error) {
    console.error('Change submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handlePerformance = (record: ContractVO) => {
  currentContractId.value = record.id
  detailTab.value = 'performance'
  handleView(record)
}

const handlePerformanceAdd = () => {
  Object.assign(performanceFormData, {
    contractId: currentContractId.value,
    performanceNode: '',
    planDate: '',
    actualDate: '',
    performanceStatus: 1,
    performanceDesc: '',
    remark: ''
  })
  performanceModalVisible.value = true
}

const handlePerformanceSubmit = async () => {
  try {
    await performanceFormRef.value.validate()
    submitting.value = true
    await addPerformance(performanceFormData)
    message.success('履约登记成功')
    performanceModalVisible.value = false
    if (currentContractId.value) {
      const res = await getContractById(currentContractId.value)
      contractDetail.value = res.data
    }
  } catch (error) {
    console.error('Performance submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleTerminate = (record: ContractVO) => {
  currentContractId.value = record.id
  terminateReason.value = ''
  terminateModalVisible.value = true
}

const handleTerminateSubmit = async () => {
  if (!terminateReason.value.trim()) {
    message.warning('请输入终止原因')
    return
  }
  try {
    await terminateContract(currentContractId.value!, terminateReason.value)
    message.success('合同已终止')
    terminateModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Terminate error:', error)
  }
}

const handleComplete = async (record: ContractVO) => {
  try {
    await completeContract(record.id)
    message.success('合同已完成')
    fetchData()
  } catch (error) {
    console.error('Complete error:', error)
  }
}

const formatMoney = (amount: number) => {
  return amount?.toFixed(2) || '0.00'
}

const getContractTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'blue', 2: 'green', 3: 'purple', 4: 'default' }
  return colors[type || 0] || 'default'
}

const getStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'default',
    1: 'orange',
    2: 'green',
    3: 'red',
    4: 'blue',
    5: 'cyan',
    6: 'default'
  }
  return colors[status || 0] || 'default'
}

const getApprovalStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 0: 'default', 1: 'orange', 2: 'green', 3: 'red' }
  return colors[status || 0] || 'default'
}

const getChangeTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'red', 2: 'orange', 3: 'blue', 4: 'purple', 5: 'default' }
  return colors[type || 0] || 'default'
}

const getPerformanceStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 0: 'default', 1: 'blue', 2: 'green', 3: 'red' }
  return colors[status || 0] || 'default'
}

onMounted(() => {
  fetchData()
  fetchCustomerList()
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

.detail-toolbar {
  margin-bottom: 12px;
  text-align: right;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
