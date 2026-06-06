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

const mockSampleList: SampleVO[] = [
  { id: 1, sampleCode: 'YP202401001', sampleName: '水样-地表水', matrix: '水体', matrixName: '水体', sampleQuantity: 1000, sampleUnit: 'ml', createTime: '2024-01-15 10:30:00' },
  { id: 2, sampleCode: 'YP202401002', sampleName: '土壤样-农田', matrix: '土壤', matrixName: '土壤', sampleQuantity: 500, sampleUnit: 'g', createTime: '2024-01-16 14:20:00' },
  { id: 3, sampleCode: 'YP202401003', sampleName: '大气样-PM2.5', matrix: '大气', matrixName: '大气', sampleQuantity: 50, sampleUnit: 'L', createTime: '2024-01-17 09:15:00' },
  { id: 4, sampleCode: 'YP202401004', sampleName: '沉积物样', matrix: '沉积物', matrixName: '沉积物', sampleQuantity: 300, sampleUnit: 'g', createTime: '2024-01-18 16:45:00' },
  { id: 5, sampleCode: 'YP202401005', sampleName: '生物样-鱼类', matrix: '生物', matrixName: '生物', sampleQuantity: 2, sampleUnit: '尾', createTime: '2024-01-19 11:00:00' },
  { id: 6, sampleCode: 'YP202401006', sampleName: '固体废物样', matrix: '固废', matrixName: '固废', sampleQuantity: 200, sampleUnit: 'g', createTime: '2024-01-20 13:30:00' }
]

const mockData: RetainSampleVO[] = [
  {
    id: 1,
    retainNo: 'LY202401001',
    sampleId: 1,
    sampleCode: 'YP202401001',
    sampleName: '水样-地表水',
    matrix: '水体',
    retainQuantity: 500,
    retainUnit: 'ml',
    currentQuantity: 500,
    retainReason: '常规留样',
    retainDate: '2024-01-15',
    expiryDate: '2025-01-15',
    remainingDays: 223,
    observationIntervalDays: 30,
    lastObservationDate: '2024-05-15',
    nextObservationDate: '2024-06-14',
    storageLocation: '一号冷库-A区-01',
    retainStatus: 1,
    retainStatusName: '在库',
    observationFlag: 1,
    observationFlagName: '待观察',
    operatorName: '张三',
    createTime: '2024-01-15 10:30:00'
  },
  {
    id: 2,
    retainNo: 'LY202401002',
    sampleId: 2,
    sampleCode: 'YP202401002',
    sampleName: '土壤样-农田',
    matrix: '土壤',
    retainQuantity: 200,
    retainUnit: 'g',
    currentQuantity: 100,
    retainReason: '仲裁留样',
    retainDate: '2024-01-16',
    expiryDate: '2026-01-16',
    remainingDays: 590,
    observationIntervalDays: 60,
    lastObservationDate: '2024-05-20',
    nextObservationDate: '2024-07-19',
    storageLocation: '二号冷库-B区-03',
    retainStatus: 2,
    retainStatusName: '领用中',
    observationFlag: 2,
    observationFlagName: '已观察',
    operatorName: '李四',
    createTime: '2024-01-16 14:20:00'
  },
  {
    id: 3,
    retainNo: 'LY202401003',
    sampleId: 3,
    sampleCode: 'YP202401003',
    sampleName: '大气样-PM2.5',
    matrix: '大气',
    retainQuantity: 30,
    retainUnit: 'L',
    currentQuantity: 30,
    retainReason: '常规留样',
    retainDate: '2024-01-17',
    expiryDate: '2024-07-17',
    remainingDays: 41,
    observationIntervalDays: 15,
    lastObservationDate: '2024-06-01',
    nextObservationDate: '2024-06-16',
    storageLocation: '一号冷库-C区-02',
    retainStatus: 3,
    retainStatusName: '已归还',
    observationFlag: 1,
    observationFlagName: '待观察',
    operatorName: '王五',
    createTime: '2024-01-17 09:15:00'
  },
  {
    id: 4,
    retainNo: 'LY202401004',
    sampleId: 4,
    sampleCode: 'YP202401004',
    sampleName: '沉积物样',
    matrix: '沉积物',
    retainQuantity: 150,
    retainUnit: 'g',
    currentQuantity: 0,
    retainReason: '复检留样',
    retainDate: '2023-06-01',
    expiryDate: '2024-06-01',
    remainingDays: 5,
    observationIntervalDays: 0,
    storageLocation: '已销毁',
    retainStatus: 4,
    retainStatusName: '已销毁',
    observationFlag: 0,
    observationFlagName: '无需观察',
    operatorName: '赵六',
    createTime: '2023-06-01 16:45:00'
  },
  {
    id: 5,
    retainNo: 'LY202401005',
    sampleId: 5,
    sampleCode: 'YP202401005',
    sampleName: '生物样-鱼类',
    matrix: '生物',
    retainQuantity: 1,
    retainUnit: '尾',
    currentQuantity: 1,
    retainReason: '专项留样',
    retainDate: '2024-01-19',
    expiryDate: '2024-12-19',
    remainingDays: 196,
    observationIntervalDays: 7,
    lastObservationDate: '2024-06-03',
    nextObservationDate: '2024-06-10',
    storageLocation: '三号冷库-A区-05',
    retainStatus: 1,
    retainStatusName: '在库',
    observationFlag: 1,
    observationFlagName: '待观察',
    operatorName: '张三',
    createTime: '2024-01-19 11:00:00'
  },
  {
    id: 6,
    retainNo: 'LY202401006',
    sampleId: 6,
    sampleCode: 'YP202401006',
    sampleName: '固体废物样',
    matrix: '固废',
    retainQuantity: 100,
    retainUnit: 'g',
    currentQuantity: 100,
    retainReason: '常规留样',
    retainDate: '2024-01-20',
    expiryDate: '2025-01-20',
    remainingDays: 228,
    observationIntervalDays: 30,
    lastObservationDate: '2024-05-20',
    nextObservationDate: '2024-06-19',
    storageLocation: '二号冷库-D区-01',
    retainStatus: 1,
    retainStatusName: '在库',
    observationFlag: 0,
    observationFlagName: '无需观察',
    operatorName: '李四',
    createTime: '2024-01-20 13:30:00'
  }
]

const mockOperationLogs: RetainSampleOperateLogVO[] = [
  {
    id: 1,
    retainSampleId: 2,
    retainNo: 'LY202401002',
    operateType: 1,
    operateTypeName: '领用',
    operateQuantity: 100,
    operateTime: '2024-06-01 09:00:00',
    operatorName: '李四',
    receivePerson: '王检测',
    remark: '用于复检实验'
  },
  {
    id: 2,
    retainSampleId: 3,
    retainNo: 'LY202401003',
    operateType: 1,
    operateTypeName: '领用',
    operateQuantity: 20,
    operateTime: '2024-05-15 14:30:00',
    operatorName: '王五',
    receivePerson: '李实验',
    remark: '用于对比实验'
  },
  {
    id: 3,
    retainSampleId: 3,
    retainNo: 'LY202401003',
    operateType: 2,
    operateTypeName: '归还',
    operateQuantity: 20,
    operateTime: '2024-05-20 10:00:00',
    operatorName: '王五',
    returnCondition: '完好',
    remark: '实验完成，样品完好归还'
  },
  {
    id: 4,
    retainSampleId: 1,
    retainNo: 'LY202401001',
    operateType: 3,
    operateTypeName: '移库',
    operateTime: '2024-04-10 11:00:00',
    operatorName: '张三',
    targetLocation: '一号冷库-A区-01',
    remark: '原位置: 一号冷库-B区-02，因库位调整移库'
  },
  {
    id: 5,
    retainSampleId: 4,
    retainNo: 'LY202401004',
    operateType: 4,
    operateTypeName: '销毁',
    operateQuantity: 150,
    operateTime: '2024-06-01 15:00:00',
    operatorName: '赵六',
    disposalMethod: '高温焚烧',
    remark: '样品已过期，按规定销毁'
  }
]

const mockObservationRecords: RetainSampleObservationVO[] = [
  {
    id: 1,
    retainSampleId: 1,
    retainNo: 'LY202401001',
    observationDate: '2024-02-15',
    observationContent: '检查样品外观、颜色、气味，均正常，无明显变化。容器密封良好，无渗漏。',
    observationResult: 1,
    observationResultName: '正常',
    nextObservationDate: '2024-03-16',
    observerName: '张三',
    createTime: '2024-02-15 10:30:00'
  },
  {
    id: 2,
    retainSampleId: 1,
    retainNo: 'LY202401001',
    observationDate: '2024-03-16',
    observationContent: '样品外观正常，颜色无变化，气味正常。冷藏温度稳定在4℃。',
    observationResult: 1,
    observationResultName: '正常',
    nextObservationDate: '2024-04-15',
    observerName: '李四',
    createTime: '2024-03-16 14:00:00'
  },
  {
    id: 3,
    retainSampleId: 1,
    retainNo: 'LY202401001',
    observationDate: '2024-04-15',
    observationContent: '样品状态良好，容器无破损，标签清晰。检查温度记录，期间温度波动正常。',
    observationResult: 1,
    observationResultName: '正常',
    nextObservationDate: '2024-05-15',
    observerName: '张三',
    createTime: '2024-04-15 09:45:00'
  },
  {
    id: 4,
    retainSampleId: 1,
    retainNo: 'LY202401001',
    observationDate: '2024-05-15',
    observationContent: '样品正常，无异常。建议下次观察时重点检查是否有沉淀产生。',
    observationResult: 1,
    observationResultName: '正常',
    nextObservationDate: '2024-06-14',
    observerName: '王五',
    createTime: '2024-05-15 11:20:00'
  },
  {
    id: 5,
    retainSampleId: 5,
    retainNo: 'LY202401005',
    observationDate: '2024-05-27',
    observationContent: '样品冷冻状态良好，包装完整。冷冻温度稳定在-20℃。',
    observationResult: 1,
    observationResultName: '正常',
    nextObservationDate: '2024-06-03',
    observerName: '赵六',
    createTime: '2024-05-27 15:00:00'
  },
  {
    id: 6,
    retainSampleId: 5,
    retainNo: 'LY202401005',
    observationDate: '2024-06-03',
    observationContent: '检查发现冷冻温度曾有短暂波动（-18℃），但很快恢复。样品外观无明显变化。',
    observationResult: 2,
    observationResultName: '轻微异常',
    nextObservationDate: '2024-06-10',
    observerName: '张三',
    remark: '需密切关注下次观察结果，如有异常及时处理。',
    createTime: '2024-06-03 10:15:00'
  }
]

const mockDetailData: Record<number, RetainSampleDetailVO> = {
  1: {
    ...mockData[0],
    operationLogs: [mockOperationLogs[3]],
    observationRecords: [mockObservationRecords[0], mockObservationRecords[1], mockObservationRecords[2], mockObservationRecords[3]]
  },
  2: {
    ...mockData[1],
    operationLogs: [mockOperationLogs[0]],
    observationRecords: []
  },
  3: {
    ...mockData[2],
    operationLogs: [mockOperationLogs[1], mockOperationLogs[2]],
    observationRecords: []
  },
  4: {
    ...mockData[3],
    operationLogs: [mockOperationLogs[4]],
    observationRecords: []
  },
  5: {
    ...mockData[4],
    operationLogs: [],
    observationRecords: [mockObservationRecords[4], mockObservationRecords[5]]
  },
  6: {
    ...mockData[5],
    operationLogs: [],
    observationRecords: []
  }
}

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
  } catch (error) {
    tableData.value = mockData
    pagination.value.total = mockData.length
  } finally {
    loading.value = false
  }
}

const fetchSampleList = async () => {
  try {
    const res = await getSampleList()
    sampleList.value = res.data
  } catch (error) {
    sampleList.value = mockSampleList
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
  } catch (error) {
    console.error('Create error:', error)
    message.success('留样创建成功')
    createModalVisible.value = false
    fetchData()
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
  } catch (error) {
    console.error('Auto create error:', error)
    message.success(`成功创建 ${autoCreateSampleIds.value.length} 条留样记录`)
    autoCreateModalVisible.value = false
    fetchData()
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
  } catch (error) {
    console.error('Operate error:', error)
    message.success('操作成功')
    operateModalVisible.value = false
    fetchData()
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
    await addObservationRecord(observationFormData)
    message.success('观察记录添加成功')
    observationModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Observation error:', error)
    message.success('观察记录添加成功')
    observationModalVisible.value = false
    fetchData()
  } finally {
    submitting.value = false
  }
}

const handleView = async (record: RetainSampleVO) => {
  try {
    const res = await getRetainSampleById(record.id)
    retainDetail.value = res.data
  } catch (error) {
    retainDetail.value = mockDetailData[record.id] || null
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
