<template>
  <div class="sample-retain">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="留样编号">
            <a-input v-model:value="queryParams.retainNo" placeholder="请输入留样编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="样品编号">
            <a-input v-model:value="queryParams.sampleCode" placeholder="请输入样品编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="样品名称">
            <a-input v-model:value="queryParams.sampleName" placeholder="请输入样品名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="留样状态">
            <a-select v-model:value="queryParams.retainStatus" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="1">在库</a-select-option>
              <a-select-option :value="2">领用中</a-select-option>
              <a-select-option :value="3">已归还</a-select-option>
              <a-select-option :value="4">已销毁</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="观察标识">
            <a-select v-model:value="queryParams.observationFlag" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">无需观察</a-select-option>
              <a-select-option :value="1">待观察</a-select-option>
              <a-select-option :value="2">已观察</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="创建时间">
            <a-range-picker
              v-model:value="createTimeRange"
              style="width: 260px"
              value-format="YYYY-MM-DD HH:mm:ss"
              @change="handleCreateTimeChange"
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
          <a-button type="primary" @click="handleCreate">
            <PlusOutlined /> 创建留样
          </a-button>
          <a-button type="primary" style="margin-left: 8px" @click="handleAutoCreate">
            <PlayCircleOutlined /> 自动创建
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
          <template v-if="column.key === 'retainStatus'">
            <a-tag :color="getRetainStatusColor(record.retainStatus)">
              {{ record.retainStatusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'observationFlag'">
            <a-tag v-if="record.observationFlag === 0" color="default">
              {{ record.observationFlagName || '-' }}
            </a-tag>
            <a-tag v-else-if="record.observationFlag === 1" color="red">
              <WarningOutlined style="margin-right: 4px" />
              {{ record.observationFlagName || '-' }}
            </a-tag>
            <a-tag v-else-if="record.observationFlag === 2" color="green">
              {{ record.observationFlagName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'remainingDays'">
            <span :class="{ 'text-red': record.remainingDays !== undefined && record.remainingDays <= 7 }">
              {{ record.remainingDays !== undefined ? record.remainingDays + ' 天' : '-' }}
            </span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">
              <EyeOutlined /> 查看
            </a-button>
            <a-dropdown :trigger="['click']" v-if="record.retainStatus !== 4">
              <a-button type="link" size="small">
                <EditOutlined /> 操作 <a-down-outlined />
              </a-button>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleOperate(record, 1)" v-if="record.retainStatus === 1">
                    领用
                  </a-menu-item>
                  <a-menu-item @click="handleOperate(record, 2)" v-if="record.retainStatus === 2">
                    归还
                  </a-menu-item>
                  <a-menu-item @click="handleOperate(record, 3)" v-if="record.retainStatus === 1">
                    移库
                  </a-menu-item>
                  <a-menu-item @click="handleOperate(record, 4)" v-if="record.retainStatus === 1">
                    销毁复核
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
            <a-button type="link" size="small" @click="handleAddObservation(record)" v-if="record.observationFlag !== 0 && record.retainStatus !== 4">
              <CalendarOutlined /> 添加观察
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="createModalVisible"
      title="创建留样"
      width="700px"
      @ok="handleCreateSubmit"
      @cancel="createModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="createFormRef"
        :model="createFormData"
        :rules="createFormRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="选择样品" name="sampleId">
              <a-select
                v-model:value="createFormData.sampleId"
                placeholder="请选择样品"
                show-search
                :filter-option="(input: string, option: any) =>
                  (option.label as string)?.toLowerCase().includes(input.toLowerCase())
                "
                @change="handleSampleChange"
              >
                <a-select-option v-for="sample in sampleList" :key="sample.id" :value="sample.id" :label="sample.sampleCode + ' - ' + sample.sampleName">
                  {{ sample.sampleCode }} - {{ sample.sampleName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="留样数量" name="retainQuantity">
              <a-input-number v-model:value="createFormData.retainQuantity" :min="1" style="width: 100%" placeholder="请输入留样数量" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单位">
              <a-input v-model:value="createFormData.retainUnit" placeholder="请输入单位" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="留样原因" name="retainReason">
          <a-textarea v-model:value="createFormData.retainReason" :rows="2" placeholder="请输入留样原因" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="留样日期" name="retainDate">
              <a-date-picker v-model:value="createFormData.retainDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="到期日期" name="expiryDate">
              <a-date-picker v-model:value="createFormData.expiryDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="观察间隔(天)">
              <a-input-number v-model:value="createFormData.observationIntervalDays" :min="1" style="width: 100%" placeholder="请输入观察间隔天数" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="存储位置">
              <a-input v-model:value="createFormData.storageLocation" placeholder="请输入存储位置" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="createFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="autoCreateModalVisible"
      title="自动创建留样"
      width="700px"
      @ok="handleAutoCreateSubmit"
      @cancel="autoCreateModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-alert type="info" show-icon message="请选择需要自动创建留样记录的样品，系统将根据样品信息自动生成留样记录。" style="margin-bottom: 16px" />
      <a-form layout="vertical">
        <a-form-item label="选择样品">
          <a-select
            v-model:value="autoCreateSampleIds"
            mode="multiple"
            placeholder="请选择多个样品"
            show-search
            :filter-option="(input: string, option: any) =>
              (option.label as string)?.toLowerCase().includes(input.toLowerCase())
            "
            style="width: 100%"
          >
            <a-select-option v-for="sample in sampleList" :key="sample.id" :value="sample.id" :label="sample.sampleCode + ' - ' + sample.sampleName">
              {{ sample.sampleCode }} - {{ sample.sampleName }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="operateModalVisible"
      :title="operateModalTitle"
      width="600px"
      @ok="handleOperateSubmit"
      @cancel="operateModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-tabs v-model:activeKey="operateTabKey" @change="handleOperateTabChange">
        <a-tab-pane key="1" tab="领用" v-if="currentOperateType === 1">
          <a-form
            ref="operateFormRef"
            :model="operateFormData"
            :rules="operateFormRules"
            layout="vertical"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="领用数量" name="operateQuantity">
                  <a-input-number v-model:value="operateFormData.operateQuantity" :min="1" :max="maxOperateQuantity" style="width: 100%" placeholder="请输入领用数量" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="领用人" name="receivePerson">
                  <a-input v-model:value="operateFormData.receivePerson" placeholder="请输入领用人" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item label="领用用途" name="remark">
              <a-textarea v-model:value="operateFormData.remark" :rows="2" placeholder="请输入领用用途" />
            </a-form-item>
            <a-form-item label="领用日期" name="operateTime">
              <a-date-picker v-model:value="operateFormData.operateTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-form>
        </a-tab-pane>
        <a-tab-pane key="2" tab="归还" v-if="currentOperateType === 2">
          <a-form
            ref="operateFormRef"
            :model="operateFormData"
            :rules="operateFormRules"
            layout="vertical"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="归还数量" name="operateQuantity">
                  <a-input-number v-model:value="operateFormData.operateQuantity" :min="1" :max="maxOperateQuantity" style="width: 100%" placeholder="请输入归还数量" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="归还情况" name="returnCondition">
                  <a-select v-model:value="operateFormData.returnCondition" placeholder="请选择">
                    <a-select-option value="完好">完好</a-select-option>
                    <a-select-option value="轻微破损">轻微破损</a-select-option>
                    <a-select-option value="严重破损">严重破损</a-select-option>
                    <a-select-option value="部分丢失">部分丢失</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item label="备注" name="remark">
              <a-textarea v-model:value="operateFormData.remark" :rows="2" placeholder="请输入备注" />
            </a-form-item>
            <a-form-item label="归还日期" name="operateTime">
              <a-date-picker v-model:value="operateFormData.operateTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-form>
        </a-tab-pane>
        <a-tab-pane key="3" tab="移库" v-if="currentOperateType === 3">
          <a-form
            ref="operateFormRef"
            :model="operateFormData"
            :rules="operateFormRules"
            layout="vertical"
          >
            <a-form-item label="目标位置" name="targetLocation">
              <a-input v-model:value="operateFormData.targetLocation" placeholder="请输入目标存储位置" />
            </a-form-item>
            <a-form-item label="移库原因" name="remark">
              <a-textarea v-model:value="operateFormData.remark" :rows="2" placeholder="请输入移库原因" />
            </a-form-item>
            <a-form-item label="移库日期" name="operateTime">
              <a-date-picker v-model:value="operateFormData.operateTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-form>
        </a-tab-pane>
        <a-tab-pane key="4" tab="销毁复核" v-if="currentOperateType === 4">
          <a-form
            ref="operateFormRef"
            :model="operateFormData"
            :rules="operateFormRules"
            layout="vertical"
          >
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="销毁方式" name="disposalMethod">
                  <a-select v-model:value="operateFormData.disposalMethod" placeholder="请选择">
                    <a-select-option value="高温焚烧">高温焚烧</a-select-option>
                    <a-select-option value="化学处理">化学处理</a-select-option>
                    <a-select-option value="深埋处理">深埋处理</a-select-option>
                    <a-select-option value="其他">其他</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="见证人">
                  <a-input v-model:value="witnessName" placeholder="请输入见证人" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-form-item label="销毁原因" name="disposalReason">
              <a-textarea v-model:value="operateFormData.disposalReason" :rows="2" placeholder="请输入销毁原因" />
            </a-form-item>
            <a-form-item label="销毁日期" name="operateTime">
              <a-date-picker v-model:value="operateFormData.operateTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <a-modal
      v-model:open="observationModalVisible"
      title="添加观察记录"
      width="600px"
      @ok="handleObservationSubmit"
      @cancel="observationModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="observationFormRef"
        :model="observationFormData"
        :rules="observationFormRules"
        layout="vertical"
      >
        <a-form-item label="观察内容" name="observationContent">
          <a-textarea v-model:value="observationFormData.observationContent" :rows="3" placeholder="请输入观察内容，详细描述样品状态、外观、颜色等" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="观察结果" name="observationResult">
              <a-select v-model:value="observationFormData.observationResult" placeholder="请选择">
                <a-select-option :value="1">正常</a-select-option>
                <a-select-option :value="2">轻微异常</a-select-option>
                <a-select-option :value="3">严重异常</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="下次观察日期" name="nextObservationDate">
              <a-date-picker v-model:value="observationFormData.nextObservationDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="observationFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="留样详情"
      width="900px"
      :footer="null"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered size="small">
            <a-descriptions-item label="留样编号">{{ retainDetail?.retainNo }}</a-descriptions-item>
            <a-descriptions-item label="样品编号">{{ retainDetail?.sampleCode }}</a-descriptions-item>
            <a-descriptions-item label="样品名称">{{ retainDetail?.sampleName }}</a-descriptions-item>
            <a-descriptions-item label="基质">{{ retainDetail?.matrix || '-' }}</a-descriptions-item>
            <a-descriptions-item label="留样数量">{{ retainDetail?.retainQuantity }}{{ retainDetail?.retainUnit || '' }}</a-descriptions-item>
            <a-descriptions-item label="当前数量">{{ retainDetail?.currentQuantity }}{{ retainDetail?.retainUnit || '' }}</a-descriptions-item>
            <a-descriptions-item label="留样日期">{{ retainDetail?.retainDate || '-' }}</a-descriptions-item>
            <a-descriptions-item label="到期日期">{{ retainDetail?.expiryDate || '-' }}</a-descriptions-item>
            <a-descriptions-item label="剩余天数">
              <span :class="{ 'text-red': retainDetail?.remainingDays !== undefined && retainDetail?.remainingDays <= 7 }">
                {{ retainDetail?.remainingDays !== undefined ? retainDetail?.remainingDays + ' 天' : '-' }}
              </span>
            </a-descriptions-item>
            <a-descriptions-item label="观察间隔">{{ retainDetail?.observationIntervalDays ? retainDetail?.observationIntervalDays + ' 天' : '-' }}</a-descriptions-item>
            <a-descriptions-item label="存储位置">{{ retainDetail?.storageLocation || '-' }}</a-descriptions-item>
            <a-descriptions-item label="留样原因">{{ retainDetail?.retainReason || '-' }}</a-descriptions-item>
            <a-descriptions-item label="留样状态">
              <a-tag :color="getRetainStatusColor(retainDetail?.retainStatus)">
                {{ retainDetail?.retainStatusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="观察标识">
              <a-tag v-if="retainDetail?.observationFlag === 0" color="default">
                {{ retainDetail?.observationFlagName }}
              </a-tag>
              <a-tag v-else-if="retainDetail?.observationFlag === 1" color="red">
                <WarningOutlined style="margin-right: 4px" />
                {{ retainDetail?.observationFlagName }}
              </a-tag>
              <a-tag v-else-if="retainDetail?.observationFlag === 2" color="green">
                {{ retainDetail?.observationFlagName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="上次观察日期">{{ retainDetail?.lastObservationDate || '-' }}</a-descriptions-item>
            <a-descriptions-item label="下次观察日期">{{ retainDetail?.nextObservationDate || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建人">{{ retainDetail?.operatorName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ retainDetail?.createTime }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ retainDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="logs" tab="操作日志">
          <a-table
            :columns="operateLogColumns"
            :data-source="retainDetail?.operationLogs || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'operateType'">
                <a-tag :color="getOperateTypeColor(record.operateType)">
                  {{ record.operateTypeName }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'operateQuantity'">
                {{ record.operateQuantity !== undefined ? record.operateQuantity : '-' }}
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="observations" tab="观察记录">
          <a-timeline v-if="retainDetail?.observationRecords?.length">
            <a-timeline-item v-for="item in retainDetail?.observationRecords" :key="item.id">
              <template #dot>
                <a-badge :status="getObservationResultStatus(item.observationResult)" />
              </template>
              <a-card size="small" style="margin-bottom: 8px">
                <div class="observation-header">
                  <a-tag :color="getObservationResultColor(item.observationResult)">
                    {{ item.observationResultName || '未评估' }}
                  </a-tag>
                  <span class="observation-time">
                    <CalendarOutlined style="margin-right: 4px" />
                    {{ item.observationDate || item.createTime }}
                  </span>
                  <span class="observation-person">观察人：{{ item.observerName || '-' }}</span>
                </div>
                <div class="observation-content">{{ item.observationContent }}</div>
                <div class="observation-next" v-if="item.nextObservationDate">
                  下次观察日期：{{ item.nextObservationDate }}
                </div>
                <div class="observation-remark" v-if="item.remark">
                  备注：{{ item.remark }}
                </div>
              </a-card>
            </a-timeline-item>
          </a-timeline>
          <a-empty v-else description="暂无观察记录" />
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
  SaveOutlined,
  EyeOutlined,
  PlusOutlined,
  PlayCircleOutlined,
  EditOutlined,
  HistoryOutlined,
  CalendarOutlined,
  WarningOutlined,
  SearchOutlined,
  ReloadOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import type {
  RetainSampleQuery,
  RetainSampleCreateDTO,
  RetainSampleOperateDTO,
  RetainSampleObservationDTO,
  RetainSampleVO,
  RetainSampleDetailVO,
  RetainSampleOperateLogVO,
  RetainSampleObservationVO,
  SampleVO
} from '@/types'
import {
  getRetainSamplePage,
  getRetainSampleById,
  createRetainSample,
  autoCreateRetainSample,
  operateRetainSample,
  addObservationRecord,
  getSampleList
} from '@/api/sample'

const loading = ref(false)
const submitting = ref(false)
const detailTab = ref('basic')
const createTimeRange = ref<any[]>([])
const operateTabKey = ref('1')
const witnessName = ref('')

const createModalVisible = ref(false)
const autoCreateModalVisible = ref(false)
const operateModalVisible = ref(false)
const operateModalTitle = ref('')
const observationModalVisible = ref(false)
const detailModalVisible = ref(false)

const createFormRef = ref()
const operateFormRef = ref()
const observationFormRef = ref()

const currentRetainId = ref<number>()
const currentOperateType = ref<number>(1)
const maxOperateQuantity = ref<number>(0)
const autoCreateSampleIds = ref<number[]>([])

const sampleList = ref<SampleVO[]>([])

const queryParams = reactive<RetainSampleQuery>({
  pageNum: 1,
  pageSize: 10,
  retainNo: '',
  sampleCode: '',
  sampleName: '',
  retainStatus: undefined,
  observationFlag: undefined
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<RetainSampleVO[]>([])
const retainDetail = ref<RetainSampleDetailVO | null>(null)

const createFormData = reactive<RetainSampleCreateDTO>({
  sampleId: 0,
  sampleCode: '',
  sampleName: '',
  retainQuantity: 1,
  retainUnit: '',
  retainReason: '',
  retainDate: '',
  expiryDate: '',
  observationIntervalDays: 30,
  storageLocation: '',
  remark: ''
})

const operateFormData = reactive<RetainSampleOperateDTO>({
  id: 0,
  operateType: 1,
  operateQuantity: 1,
  operateTime: '',
  receivePerson: '',
  returnCondition: '',
  targetLocation: '',
  disposalMethod: '',
  disposalReason: '',
  remark: ''
})

const observationFormData = reactive<RetainSampleObservationDTO>({
  retainSampleId: 0,
  observationDate: '',
  observationContent: '',
  observationResult: 1,
  nextObservationDate: '',
  remark: ''
})

const createFormRules = {
  sampleId: [{ required: true, message: '请选择样品', trigger: 'change' }],
  retainQuantity: [{ required: true, message: '请输入留样数量', trigger: 'blur' }],
  retainReason: [{ required: true, message: '请输入留样原因', trigger: 'blur' }],
  retainDate: [{ required: true, message: '请选择留样日期', trigger: 'change' }],
  expiryDate: [{ required: true, message: '请选择到期日期', trigger: 'change' }]
}

const operateFormRules = {
  operateQuantity: [{ required: true, message: '请输入操作数量', trigger: 'blur' }],
  receivePerson: [{ required: true, message: '请输入领用人', trigger: 'blur' }],
  returnCondition: [{ required: true, message: '请选择归还情况', trigger: 'change' }],
  targetLocation: [{ required: true, message: '请输入目标位置', trigger: 'blur' }],
  disposalMethod: [{ required: true, message: '请选择销毁方式', trigger: 'change' }],
  disposalReason: [{ required: true, message: '请输入销毁原因', trigger: 'blur' }],
  operateTime: [{ required: true, message: '请选择操作日期', trigger: 'change' }]
}

const observationFormRules = {
  observationContent: [{ required: true, message: '请输入观察内容', trigger: 'blur' }],
  observationResult: [{ required: true, message: '请选择观察结果', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '留样编号', dataIndex: 'retainNo', key: 'retainNo', width: 130, ellipsis: true },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 130, ellipsis: true },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 150, ellipsis: true },
  { title: '基质', dataIndex: 'matrix', key: 'matrix', width: 80 },
  { title: '留样数量', dataIndex: 'retainQuantity', key: 'retainQuantity', width: 100, customRender: ({ record }: { record: RetainSampleVO }) => `${record.retainQuantity}${record.retainUnit || ''}` },
  { title: '当前数量', dataIndex: 'currentQuantity', key: 'currentQuantity', width: 100, customRender: ({ record }: { record: RetainSampleVO }) => `${record.currentQuantity}${record.retainUnit || ''}` },
  { title: '留样日期', dataIndex: 'retainDate', key: 'retainDate', width: 110 },
  { title: '到期日期', dataIndex: 'expiryDate', key: 'expiryDate', width: 110 },
  { title: '剩余天数', dataIndex: 'remainingDays', key: 'remainingDays', width: 100 },
  { title: '留样状态', dataIndex: 'retainStatus', key: 'retainStatus', width: 100 },
  { title: '观察标识', dataIndex: 'observationFlag', key: 'observationFlag', width: 100 },
  { title: '下次观察日期', dataIndex: 'nextObservationDate', key: 'nextObservationDate', width: 120 },
  { title: '操作', key: 'action', width: 240, fixed: 'right' }
]

const operateLogColumns = [
  { title: '操作类型', dataIndex: 'operateType', key: 'operateType', width: 100 },
  { title: '操作数量', dataIndex: 'operateQuantity', key: 'operateQuantity', width: 100 },
  { title: '操作时间', dataIndex: 'operateTime', key: 'operateTime', width: 170 },
  { title: '操作人', dataIndex: 'operatorName', key: 'operatorName', width: 100 },
  { title: '领用人/归还情况', key: 'detail', width: 150, customRender: ({ record }: { record: RetainSampleOperateLogVO }) => record.receivePerson || record.returnCondition || '-' },
  { title: '目标位置/销毁方式', key: 'target', width: 150, customRender: ({ record }: { record: RetainSampleOperateLogVO }) => record.targetLocation || record.disposalMethod || '-' },
  { title: '备注', dataIndex: 'remark', key: 'remark' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRetainSamplePage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } catch (error: any) {
    tableData.value = []
    pagination.value.total = 0
    message.error(error?.message || '获取留样列表失败')
  } finally {
    loading.value = false
  }
}

const fetchSampleList = async () => {
  try {
    const res = await getSampleList({ sampleStatus: 3 })
    sampleList.value = res.data
  } catch (error: any) {
    sampleList.value = []
    message.error(error?.message || '获取样品列表失败')
  }
}

const handleCreateTimeChange = (dates: any[]) => {
  if (dates && dates.length === 2) {
    queryParams.createTimeStart = dates[0]
    queryParams.createTimeEnd = dates[1]
  } else {
    queryParams.createTimeStart = undefined
    queryParams.createTimeEnd = undefined
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    retainNo: '',
    sampleCode: '',
    sampleName: '',
    retainStatus: undefined,
    observationFlag: undefined,
    createTimeStart: undefined,
    createTimeEnd: undefined
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

const handleSampleChange = (sampleId: number) => {
  const sample = sampleList.value.find(s => s.id === sampleId)
  if (sample) {
    createFormData.sampleCode = sample.sampleCode
    createFormData.sampleName = sample.sampleName
    createFormData.retainUnit = sample.sampleUnit || ''
  }
}

const handleCreate = () => {
  Object.assign(createFormData, {
    sampleId: 0,
    sampleCode: '',
    sampleName: '',
    retainQuantity: 1,
    retainUnit: '',
    retainReason: '',
    retainDate: dayjs().format('YYYY-MM-DD'),
    expiryDate: dayjs().add(1, 'year').format('YYYY-MM-DD'),
    observationIntervalDays: 30,
    storageLocation: '',
    remark: ''
  })
  createModalVisible.value = true
}

const handleCreateSubmit = async () => {
  try {
    await createFormRef.value.validate()
    submitting.value = true
    await createRetainSample(createFormData)
    message.success('留样创建成功')
    createModalVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('Create error:', error)
    message.error(error?.message || '留样创建失败')
  } finally {
    submitting.value = false
  }
}

const handleAutoCreate = () => {
  autoCreateSampleIds.value = []
  autoCreateModalVisible.value = true
}

const handleAutoCreateSubmit = async () => {
  if (autoCreateSampleIds.value.length === 0) {
    message.warning('请至少选择一个样品')
    return
  }
  try {
    submitting.value = true
    await autoCreateRetainSample(autoCreateSampleIds.value)
    message.success(`成功创建 ${autoCreateSampleIds.value.length} 条留样记录`)
    autoCreateModalVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('Auto create error:', error)
    message.error(error?.message || '自动创建留样失败')
  } finally {
    submitting.value = false
  }
}

const handleOperate = (record: RetainSampleVO, operateType: number) => {
  currentRetainId.value = record.id
  currentOperateType.value = operateType
  operateTabKey.value = String(operateType)
  maxOperateQuantity.value = record.currentQuantity || 0

  const titles: Record<number, string> = {
    1: '领用操作',
    2: '归还操作',
    3: '移库操作',
    4: '销毁复核'
  }
  operateModalTitle.value = titles[operateType]

  Object.assign(operateFormData, {
    id: record.id,
    operateType: operateType,
    operateQuantity: operateType === 3 ? undefined : 1,
    operateTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    receivePerson: '',
    returnCondition: '',
    targetLocation: '',
    disposalMethod: '',
    disposalReason: '',
    remark: ''
  })
  witnessName.value = ''
  operateModalVisible.value = true
}

const handleOperateTabChange = (key: string) => {
  currentOperateType.value = Number(key)
}

const handleOperateSubmit = async () => {
  try {
    await operateFormRef.value.validate()
    submitting.value = true
    await operateRetainSample(operateFormData)
    message.success('操作成功')
    operateModalVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('Operate error:', error)
    message.error(error?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleAddObservation = (record: RetainSampleVO) => {
  currentRetainId.value = record.id
  Object.assign(observationFormData, {
    retainSampleId: record.id,
    observationDate: dayjs().format('YYYY-MM-DD'),
    observationContent: '',
    observationResult: 1,
    nextObservationDate: record.observationIntervalDays ? dayjs().add(record.observationIntervalDays, 'day').format('YYYY-MM-DD') : '',
    remark: ''
  })
  observationModalVisible.value = true
}

const handleObservationSubmit = async () => {
  try {
    await observationFormRef.value.validate()
    submitting.value = true
    observationFormData.nextObservationDate = dayjs(observationFormData.observationDate).add(retainDetail?.observationIntervalDays || 30, 'day').format('YYYY-MM-DD')
    await addObservationRecord(observationFormData)
    message.success('观察记录添加成功')
    observationModalVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('Observation error:', error)
    message.error(error?.message || '添加观察记录失败')
  } finally {
    submitting.value = false
  }
}

const handleView = async (record: RetainSampleVO) => {
  try {
    const res = await getRetainSampleById(record.id)
    retainDetail.value = res.data
  } catch (error: any) {
    retainDetail.value = null
    message.error(error?.message || '获取留样详情失败')
  }
  detailTab.value = 'basic'
  currentRetainId.value = record.id
  detailModalVisible.value = true
}

const getRetainStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 1: 'green', 2: 'orange', 3: 'blue', 4: 'default' }
  return colors[status || 0] || 'default'
}

const getOperateTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'orange', 2: 'green', 3: 'blue', 4: 'red' }
  return colors[type || 0] || 'default'
}

const getObservationResultColor = (result?: number) => {
  const colors: Record<number, string> = { 1: 'green', 2: 'orange', 3: 'red' }
  return colors[result || 0] || 'default'
}

const getObservationResultStatus = (result?: number) => {
  const statuses: Record<number, 'success' | 'warning' | 'error' | 'default'> = {
    1: 'success',
    2: 'warning',
    3: 'error'
  }
  return statuses[result || 0] || 'default'
}

onMounted(() => {
  fetchData()
  fetchSampleList()
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

.text-red {
  color: #f5222d;
}

.observation-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.observation-time {
  color: #999;
  font-size: 12px;
}

.observation-person {
  color: #666;
  font-size: 12px;
  margin-left: auto;
}

.observation-content {
  color: #333;
  line-height: 1.6;
}

.observation-next {
  margin-top: 8px;
  color: #1890ff;
  font-size: 12px;
}

.observation-remark {
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
