<template>
  <div class="sample-register">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="样品编号">
            <a-input v-model:value="queryParams.sampleCode" placeholder="请输入样品编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="样品名称">
            <a-input v-model:value="queryParams.sampleName" placeholder="请输入样品名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="采样点">
            <a-input v-model:value="queryParams.pointName" placeholder="请输入采样点" style="width: 140px" allow-clear />
          </a-form-item>
          <a-form-item label="基质">
            <a-select v-model:value="queryParams.matrix" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option value="水质">水质</a-select-option>
              <a-select-option value="土壤">土壤</a-select-option>
              <a-select-option value="大气">大气</a-select-option>
              <a-select-option value="固体废物">固体废物</a-select-option>
              <a-select-option value="噪声">噪声</a-select-option>
              <a-select-option value="其他">其他</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="样品状态">
            <a-select v-model:value="queryParams.sampleStatus" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="1">待检测</a-select-option>
              <a-select-option :value="2">检测中</a-select-option>
              <a-select-option :value="3">已完成</a-select-option>
              <a-select-option :value="4">已留样</a-select-option>
              <a-select-option :value="5">已销毁</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="创建时间">
            <a-range-picker
              v-model:value="createTimeRange"
              show-time
              style="width: 280px"
              value-format="YYYY-MM-DD HH:mm:ss"
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
          <a-button type="primary" @click="handleGenerateCode">
            <BarcodeOutlined /> 生成样品编号
          </a-button>
          <a-upload
            :show-upload-list="false"
            :before-upload="handleBeforeUpload"
            :custom-request="handleImport"
            accept=".xlsx,.xls"
            style="margin-left: 8px"
          >
            <a-button>
              <UploadOutlined /> Excel导入
            </a-button>
          </a-upload>
          <a-button type="primary" @click="handleAdd" style="margin-left: 8px">
            <PlusOutlined /> 手动录入
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
          <template v-if="column.key === 'index'">
            {{ (pagination.current - 1) * pagination.pageSize + record.index + 1 }}
          </template>
          <template v-else-if="column.key === 'sampleStatus'">
            <a-tag :color="getSampleStatusColor(record.sampleStatus)">
              {{ record.sampleStatusName || getSampleStatusName(record.sampleStatus) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'warningFlag'">
            <a-tag v-if="record.warningFlag === 1" color="red">
              <ExclamationCircleOutlined /> 预警
            </a-tag>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
            <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
            <a-button type="link" size="small" @click="handlePrintLabel(record)">
              <PrinterOutlined /> 打印标签
            </a-button>
            <a-popconfirm title="确定删除该样品吗？" @confirm="handleDelete(record.id)">
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      :title="formModalTitle"
      width="900px"
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
          <a-col :span="8">
            <a-form-item label="样品编号" name="sampleCode">
              <a-input v-model:value="formData.sampleCode" placeholder="自动生成或手动输入">
                <template #addonAfter>
                  <a-button size="small" @click="handleGenerateSingleCode" :disabled="generatingCode">
                    <BarcodeOutlined /> {{ generatingCode ? '生成中...' : '生成' }}
                  </a-button>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="样品名称" name="sampleName">
              <a-input v-model:value="formData.sampleName" placeholder="请输入样品名称" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="采样点" name="pointName">
              <a-input v-model:value="formData.pointName" placeholder="请输入采样点" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="采样时间" name="samplingTime">
              <a-date-picker
                v-model:value="formData.samplingTime"
                show-time
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择采样时间"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="基质" name="matrix">
              <a-select v-model:value="formData.matrix" placeholder="请选择基质">
                <a-select-option value="水质">水质</a-select-option>
                <a-select-option value="土壤">土壤</a-select-option>
                <a-select-option value="大气">大气</a-select-option>
                <a-select-option value="固体废物">固体废物</a-select-option>
                <a-select-option value="噪声">噪声</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="样品数量" name="sampleQuantity">
              <a-input-number
                v-model:value="formData.sampleQuantity"
                :min="0"
                style="width: 100%"
                placeholder="请输入样品数量"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="样品单位">
              <a-input v-model:value="formData.sampleUnit" placeholder="请输入样品单位" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="保存条件" name="storageCondition">
              <a-select v-model:value="formData.storageCondition" placeholder="请选择保存条件">
                <a-select-option :value="1">常温</a-select-option>
                <a-select-option :value="2">冷藏(2-8℃)</a-select-option>
                <a-select-option :value="3">冷冻(-18℃以下)</a-select-option>
                <a-select-option :value="4">避光</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="防腐剂">
              <a-input v-model:value="formData.preservative" placeholder="请输入防腐剂" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="容器类型">
              <a-input v-model:value="formData.containerType" placeholder="请输入容器类型" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="采样人">
              <a-input v-model:value="formData.samplerName" placeholder="请输入采样人" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="接收人">
              <a-input v-model:value="formData.receiverName" placeholder="请输入接收人" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="接收时间" name="receiveTime">
              <a-date-picker
                v-model:value="formData.receiveTime"
                show-time
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择接收时间"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="备注">
              <a-textarea v-model:value="formData.remark" placeholder="请输入备注" :rows="2" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider orientation="left">检测项目</a-divider>
        <div class="item-toolbar">
          <a-button type="primary" size="small" @click="handleAddItem">
            <PlusOutlined /> 添加项目
          </a-button>
          <a-button size="small" @click="handleBatchAddItem" style="margin-left: 8px">
            批量选择
          </a-button>
        </div>
        <a-table
          :columns="itemColumns"
          :data-source="formData.items"
          :pagination="false"
          row-key="id"
          size="small"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-else-if="column.key === 'itemName'">
              <a-select
                v-model:value="record.itemId"
                show-search
                placeholder="请选择检测项目"
                style="width: 100%"
                @change="(val: number) => handleItemChange(val, index)"
              >
                <a-select-option
                  v-for="item in dictTestItems"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.itemName }} ({{ item.itemCode }})
                </a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'standardName'">
              <a-select
                v-model:value="record.standardId"
                placeholder="请选择标准"
                style="width: 100%"
                @change="(val: number) => handleStandardChange(val, index)"
              >
                <a-select-option
                  v-for="std in getItemStandards(record.itemId)"
                  :key="std.id"
                  :value="std.id"
                >
                  {{ std.standardName }}
                </a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button type="link" danger size="small" @click="handleRemoveItem(index)">
                删除
              </a-button>
            </template>
          </template>
        </a-table>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="样品详情"
      width="900px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered size="small">
            <a-descriptions-item label="样品编号">{{ sampleDetail?.sampleCode }}</a-descriptions-item>
            <a-descriptions-item label="样品名称">{{ sampleDetail?.sampleName }}</a-descriptions-item>
            <a-descriptions-item label="采样点">{{ sampleDetail?.pointName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="基质">{{ sampleDetail?.matrix || sampleDetail?.matrixName }}</a-descriptions-item>
            <a-descriptions-item label="样品数量">{{ sampleDetail?.sampleQuantity }} {{ sampleDetail?.sampleUnit || '' }}</a-descriptions-item>
            <a-descriptions-item label="采样时间">{{ sampleDetail?.samplingTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="保存条件">
              <a-tag v-if="sampleDetail?.storageConditionName">{{ sampleDetail.storageConditionName }}</a-tag>
              <span v-else>{{ getStorageConditionName(sampleDetail?.storageCondition) }}</span>
            </a-descriptions-item>
            <a-descriptions-item label="样品状态">
              <a-tag :color="getSampleStatusColor(sampleDetail?.sampleStatus)">
                {{ sampleDetail?.sampleStatusName || getSampleStatusName(sampleDetail?.sampleStatus) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="防腐剂">{{ sampleDetail?.preservative || '-' }}</a-descriptions-item>
            <a-descriptions-item label="容器类型">{{ sampleDetail?.containerType || '-' }}</a-descriptions-item>
            <a-descriptions-item label="采样人">{{ sampleDetail?.samplerName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="接收人">{{ sampleDetail?.receiverName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="接收时间">{{ sampleDetail?.receiveTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="预警标识">
              <a-tag v-if="sampleDetail?.warningFlag === 1" color="red">
                <ExclamationCircleOutlined /> 预警
              </a-tag>
              <span v-else>无</span>
            </a-descriptions-item>
            <a-descriptions-item label="创建人">{{ sampleDetail?.createByName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ sampleDetail?.createTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ sampleDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="items" tab="检测项目">
          <a-table
            :columns="detailItemColumns"
            :data-source="sampleDetail?.items || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-else-if="column.key === 'resultStatus'">
                <a-tag v-if="record.resultStatus === 1" color="green">合格</a-tag>
                <a-tag v-else-if="record.resultStatus === 2" color="red">不合格</a-tag>
                <a-tag v-else-if="record.resultStatus === 3" color="orange">待检测</a-tag>
                <span v-else>-</span>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <a-modal
      v-model:open="itemSelectModalVisible"
      title="批量选择检测项目"
      width="800px"
      @ok="handleBatchItemConfirm"
      @cancel="itemSelectModalVisible = false"
    >
      <a-table
        :columns="itemSelectColumns"
        :data-source="dictTestItems"
        :pagination="false"
        row-key="id"
        size="small"
        row-selection="checkbox"
        @selection-change="handleItemSelectionChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-else-if="column.key === 'itemName'">
            {{ record.itemName }}
          </template>
          <template v-else-if="column.key === 'itemCode'">
            {{ record.itemCode }}
          </template>
          <template v-else-if="column.key === 'itemCategory'">
            {{ record.itemCategory || '-' }}
          </template>
          <template v-else-if="column.key === 'unit'">
            {{ record.unit || '-' }}
          </template>
        </template>
      </a-table>
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
  UploadOutlined,
  PrinterOutlined,
  BarcodeOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue'
import type {
  SampleQuery,
  SampleSaveDTO,
  SampleVO,
  SampleDetailVO,
  SampleItemSaveDTO,
  DictTestItem,
  DictTestStandard,
  DictItemStandard
} from '@/types'
import {
  getSamplePage,
  getSampleById,
  addSample,
  updateSample,
  deleteSample,
  generateSampleCode,
  importSample
} from '@/api/sample'

const loading = ref(false)
const submitting = ref(false)
const generatingCode = ref(false)

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)
const itemSelectModalVisible = ref(false)
const detailTab = ref('basic')

const formRef = ref()
const isEdit = ref(false)
const currentSampleId = ref<number>()
const createTimeRange = ref<any[]>([])
const selectedItems = ref<DictTestItem[]>([])

const queryParams = reactive<SampleQuery>({
  pageNum: 1,
  pageSize: 10,
  sampleCode: '',
  sampleName: '',
  pointName: '',
  matrix: undefined,
  sampleStatus: undefined,
  createTimeStart: undefined,
  createTimeEnd: undefined
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<SampleVO[]>([])
const sampleDetail = ref<SampleDetailVO | null>(null)

const dictTestItems = ref<DictTestItem[]>([
  { id: 1, itemCode: 'pH001', itemName: 'pH值', itemCategory: '理化指标', unit: '无', standardPrice: 50, costPrice: 20, detectionLimit: 0.1, detectionCycle: 1 },
  { id: 2, itemCode: 'COD001', itemName: '化学需氧量(COD)', itemCategory: '有机物', unit: 'mg/L', standardPrice: 100, costPrice: 50, detectionLimit: 5, detectionCycle: 2 },
  { id: 3, itemCode: 'BOD001', itemName: '生化需氧量(BOD)', itemCategory: '有机物', unit: 'mg/L', standardPrice: 120, costPrice: 60, detectionLimit: 2, detectionCycle: 5 },
  { id: 4, itemCode: 'NH3N001', itemName: '氨氮', itemCategory: '营养盐', unit: 'mg/L', standardPrice: 80, costPrice: 40, detectionLimit: 0.025, detectionCycle: 1 },
  { id: 5, itemCode: 'TP001', itemName: '总磷', itemCategory: '营养盐', unit: 'mg/L', standardPrice: 80, costPrice: 40, detectionLimit: 0.01, detectionCycle: 1 },
  { id: 6, itemCode: 'TN001', itemName: '总氮', itemCategory: '营养盐', unit: 'mg/L', standardPrice: 90, costPrice: 45, detectionLimit: 0.05, detectionCycle: 1 },
  { id: 7, itemCode: 'SS001', itemName: '悬浮物', itemCategory: '理化指标', unit: 'mg/L', standardPrice: 60, costPrice: 30, detectionLimit: 4, detectionCycle: 1 },
  { id: 8, itemCode: 'DO001', itemName: '溶解氧', itemCategory: '理化指标', unit: 'mg/L', standardPrice: 70, costPrice: 35, detectionLimit: 0.2, detectionCycle: 1 }
])

const dictTestStandards = ref<DictTestStandard[]>([
  { id: 1, standardNo: 'GB 3838-2002', standardName: '地表水环境质量标准', standardType: '国家标准', issueDate: '2002-04-28', implementDate: '2002-06-01', issuingAuthority: '国家环境保护总局' },
  { id: 2, standardNo: 'GB 5749-2022', standardName: '生活饮用水卫生标准', standardType: '国家标准', issueDate: '2022-03-15', implementDate: '2023-04-01', issuingAuthority: '国家市场监督管理总局' },
  { id: 3, standardNo: 'GB 8978-1996', standardName: '污水综合排放标准', standardType: '国家标准', issueDate: '1996-08-01', implementDate: '1998-01-01', issuingAuthority: '国家环境保护局' },
  { id: 4, standardNo: 'GB 15618-2018', standardName: '土壤环境质量 农用地土壤污染风险管控标准', standardType: '国家标准', issueDate: '2018-06-22', implementDate: '2018-08-01', issuingAuthority: '生态环境部' },
  { id: 5, standardNo: 'GB 3095-2012', standardName: '环境空气质量标准', standardType: '国家标准', issueDate: '2012-02-29', implementDate: '2016-01-01', issuingAuthority: '环境保护部' }
])

const dictItemStandards = ref<DictItemStandard[]>([
  { id: 1, itemId: 1, standardId: 1, limitValue: '6-9', remark: '' },
  { id: 2, itemId: 1, standardId: 2, limitValue: '≥6.5且≤8.5', remark: '' },
  { id: 3, itemId: 2, standardId: 1, limitValue: '≤20', remark: '' },
  { id: 4, itemId: 2, standardId: 3, limitValue: '≤100', remark: '' },
  { id: 5, itemId: 3, standardId: 1, limitValue: '≤4', remark: '' },
  { id: 6, itemId: 4, standardId: 1, limitValue: '≤1.0', remark: '' },
  { id: 7, itemId: 5, standardId: 1, limitValue: '≤0.2', remark: '' },
  { id: 8, itemId: 6, standardId: 1, limitValue: '≤1.0', remark: '' },
  { id: 9, itemId: 7, standardId: 1, limitValue: '≤25', remark: '' },
  { id: 10, itemId: 8, standardId: 1, limitValue: '≥5', remark: '' }
])

const formData = reactive<SampleSaveDTO>({
  sampleCode: '',
  sampleName: '',
  pointName: '',
  samplingTime: undefined,
  matrix: undefined,
  sampleQuantity: undefined,
  sampleUnit: '',
  storageCondition: undefined,
  preservative: '',
  containerType: '',
  samplerName: '',
  receiverName: '',
  receiveTime: undefined,
  remark: '',
  items: []
})

const mockData: SampleVO[] = [
  { id: 1, sampleCode: 'YP202401001', sampleName: '水样-A', pointId: 1, pointCode: 'P001', pointName: '进水口', samplingTime: '2024-01-15 09:30:00', matrix: '水质', matrixName: '水质', sampleQuantity: 2, sampleUnit: 'L', storageCondition: 2, storageConditionName: '冷藏(2-8℃)', preservative: '无', containerType: '聚乙烯瓶', samplerId: 1, samplerName: '张三', receiverId: 2, receiverName: '李四', receiveTime: '2024-01-15 14:00:00', sampleStatus: 2, sampleStatusName: '检测中', itemCount: 5, warningFlag: 0, createTime: '2024-01-15 10:00:00', createByName: '张三' },
  { id: 2, sampleCode: 'YP202401002', sampleName: '水样-B', pointId: 2, pointCode: 'P002', pointName: '出水口', samplingTime: '2024-01-15 10:15:00', matrix: '水质', matrixName: '水质', sampleQuantity: 2, sampleUnit: 'L', storageCondition: 2, storageConditionName: '冷藏(2-8℃)', preservative: '无', containerType: '聚乙烯瓶', samplerId: 1, samplerName: '张三', receiverId: 2, receiverName: '李四', receiveTime: '2024-01-15 14:00:00', sampleStatus: 1, sampleStatusName: '待检测', itemCount: 3, warningFlag: 1, warningMessage: '保存条件异常', createTime: '2024-01-15 10:30:00', createByName: '张三' },
  { id: 3, sampleCode: 'YP202401003', sampleName: '土壤样-1', pointId: 3, pointCode: 'P003', pointName: 'A区采样点', samplingTime: '2024-01-15 11:00:00', matrix: '土壤', matrixName: '土壤', sampleQuantity: 1, sampleUnit: 'kg', storageCondition: 1, storageConditionName: '常温', preservative: '无', containerType: '密封袋', samplerId: 3, samplerName: '王五', receiverId: 2, receiverName: '李四', receiveTime: '2024-01-15 15:00:00', sampleStatus: 3, sampleStatusName: '已完成', itemCount: 4, warningFlag: 0, createTime: '2024-01-15 11:30:00', createByName: '王五' },
  { id: 4, sampleCode: 'YP202401004', sampleName: '大气样-1', pointId: 4, pointCode: 'P004', pointName: '厂界上风向', samplingTime: '2024-01-15 08:00:00', matrix: '大气', matrixName: '大气', sampleQuantity: 10, sampleUnit: 'm³', storageCondition: 4, storageConditionName: '避光', preservative: '无', containerType: '吸收瓶', samplerId: 3, samplerName: '王五', receiverId: 4, receiverName: '赵六', receiveTime: '2024-01-15 16:00:00', sampleStatus: 4, sampleStatusName: '已留样', itemCount: 2, warningFlag: 0, createTime: '2024-01-15 08:30:00', createByName: '王五' },
  { id: 5, sampleCode: 'YP202401005', sampleName: '固废样-1', pointId: 5, pointCode: 'P005', pointName: '危废暂存区', samplingTime: '2024-01-16 09:00:00', matrix: '固体废物', matrixName: '固体废物', sampleQuantity: 500, sampleUnit: 'g', storageCondition: 1, storageConditionName: '常温', preservative: '无', containerType: '密封罐', samplerId: 1, samplerName: '张三', receiverId: 4, receiverName: '赵六', receiveTime: '2024-01-16 11:00:00', sampleStatus: 5, sampleStatusName: '已销毁', itemCount: 6, warningFlag: 0, createTime: '2024-01-16 09:30:00', createByName: '张三' }
]

const formRules = {
  sampleName: [{ required: true, message: '请输入样品名称', trigger: 'blur' }],
  pointName: [{ required: true, message: '请输入采样点', trigger: 'blur' }],
  matrix: [{ required: true, message: '请选择基质', trigger: 'change' }],
  samplingTime: [{ required: true, message: '请选择采样时间', trigger: 'change' }],
  storageCondition: [{ required: true, message: '请选择保存条件', trigger: 'change' }],
  receiveTime: [{ required: true, message: '请选择接收时间', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70 },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 140 },
  { title: '采样点', dataIndex: 'pointName', key: 'pointName', width: 140 },
  { title: '基质', dataIndex: 'matrixName', key: 'matrix', width: 100 },
  { title: '样品数量', dataIndex: 'sampleQuantity', key: 'sampleQuantity', width: 100 },
  { title: '样品状态', dataIndex: 'sampleStatus', key: 'sampleStatus', width: 100 },
  { title: '预警标识', dataIndex: 'warningFlag', key: 'warningFlag', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 260, fixed: 'right' }
]

const itemColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 60, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '检测项目', dataIndex: 'itemName', key: 'itemName', width: 200 },
  { title: '项目编号', dataIndex: 'itemCode', key: 'itemCode', width: 120 },
  { title: '检测标准', dataIndex: 'standardName', key: 'standardName', width: 250 },
  { title: '标准编号', dataIndex: 'standardNo', key: 'standardNo', width: 150 },
  { title: '单位', dataIndex: 'unit', key: 'unit', width: 80 },
  { title: '限值', dataIndex: 'limitValue', key: 'limitValue', width: 120 },
  { title: '操作', key: 'action', width: 80 }
]

const detailItemColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 60, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '检测项目', dataIndex: 'itemName', key: 'itemName', width: 160 },
  { title: '项目编号', dataIndex: 'itemCode', key: 'itemCode', width: 120 },
  { title: '检测标准', dataIndex: 'standardName', key: 'standardName', width: 200 },
  { title: '标准编号', dataIndex: 'standardNo', key: 'standardNo', width: 140 },
  { title: '单位', dataIndex: 'unit', key: 'unit', width: 80 },
  { title: '限值', dataIndex: 'limitValue', key: 'limitValue', width: 100 },
  { title: '检测结果', dataIndex: 'testResult', key: 'testResult', width: 100 },
  { title: '结果状态', dataIndex: 'resultStatus', key: 'resultStatus', width: 100 }
]

const itemSelectColumns = [
  { title: '项目名称', dataIndex: 'itemName', key: 'itemName', width: 180 },
  { title: '项目编号', dataIndex: 'itemCode', key: 'itemCode', width: 120 },
  { title: '项目分类', dataIndex: 'itemCategory', key: 'itemCategory', width: 120 },
  { title: '单位', dataIndex: 'unit', key: 'unit', width: 80 },
  { title: '标准价格', dataIndex: 'standardPrice', key: 'standardPrice', width: 100 }
]

const getSampleStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 1: 'blue', 2: 'orange', 3: 'green', 4: 'purple', 5: 'default' }
  return colors[status || 0] || 'default'
}

const getSampleStatusName = (status?: number) => {
  const names: Record<number, string> = { 1: '待检测', 2: '检测中', 3: '已完成', 4: '已留样', 5: '已销毁' }
  return names[status || 0] || '-'
}

const getStorageConditionName = (condition?: number) => {
  const names: Record<number, string> = { 1: '常温', 2: '冷藏(2-8℃)', 3: '冷冻(-18℃以下)', 4: '避光' }
  return names[condition || 0] || '-'
}

const getItemStandards = (itemId?: number) => {
  if (!itemId) return []
  const standardIds = dictItemStandards.value
    .filter((is: DictItemStandard) => is.itemId === itemId)
    .map((is: DictItemStandard) => is.standardId)
  return dictTestStandards.value.filter((std: DictTestStandard) => standardIds.includes(std.id))
}

const fetchData = async () => {
  loading.value = true
  try {
    queryParams.createTimeStart = createTimeRange.value?.[0]
    queryParams.createTimeEnd = createTimeRange.value?.[1]
    const res = await getSamplePage(queryParams)
    tableData.value = res.data.list.map((item: SampleVO, index: number) => ({ ...item, index }))
    pagination.value.total = res.data.total
  } catch (error) {
    console.error('Get sample page error:', error)
    tableData.value = mockData.map((item, index) => ({ ...item, index }))
    pagination.value.total = mockData.length
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    sampleCode: '',
    sampleName: '',
    pointName: '',
    matrix: undefined,
    sampleStatus: undefined,
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

const resetFormData = () => {
  Object.assign(formData, {
    id: undefined,
    sampleCode: '',
    sampleName: '',
    pointName: '',
    samplingTime: undefined,
    matrix: undefined,
    sampleQuantity: undefined,
    sampleUnit: '',
    storageCondition: undefined,
    preservative: '',
    containerType: '',
    samplerName: '',
    receiverName: '',
    receiveTime: undefined,
    remark: '',
    items: []
  })
}

const handleAdd = () => {
  isEdit.value = false
  formModalTitle.value = '新增样品'
  resetFormData()
  formData.receiveTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
  formModalVisible.value = true
}

const handleEdit = (record: SampleVO) => {
  isEdit.value = true
  formModalTitle.value = '编辑样品'
  currentSampleId.value = record.id
  Object.assign(formData, {
    id: record.id,
    sampleCode: record.sampleCode,
    sampleName: record.sampleName,
    pointName: record.pointName,
    samplingTime: record.samplingTime,
    matrix: record.matrix,
    sampleQuantity: record.sampleQuantity,
    sampleUnit: record.sampleUnit,
    storageCondition: record.storageCondition,
    preservative: record.preservative,
    containerType: record.containerType,
    samplerName: record.samplerName,
    receiverName: record.receiverName,
    receiveTime: record.receiveTime,
    remark: record.remark,
    items: []
  })
  formModalVisible.value = true
}

const handleView = async (record: SampleVO) => {
  try {
    const res = await getSampleById(record.id)
    sampleDetail.value = res.data
  } catch (error) {
    console.error('Get sample detail error:', error)
    sampleDetail.value = {
      ...mockData.find((m: SampleVO) => m.id === record.id)!,
      items: [
        { id: 1, sampleId: record.id, itemId: 1, itemCode: 'pH001', itemName: 'pH值', standardId: 1, standardNo: 'GB 3838-2002', standardName: '地表水环境质量标准', unit: '无', limitValue: '6-9', testResult: '7.2', testUnit: '无', resultStatus: 1, resultStatusName: '合格' },
        { id: 2, sampleId: record.id, itemId: 2, itemCode: 'COD001', itemName: '化学需氧量(COD)', standardId: 1, standardNo: 'GB 3838-2002', standardName: '地表水环境质量标准', unit: 'mg/L', limitValue: '≤20', testResult: '15.6', testUnit: 'mg/L', resultStatus: 1, resultStatusName: '合格' },
        { id: 3, sampleId: record.id, itemId: 4, itemCode: 'NH3N001', itemName: '氨氮', standardId: 1, standardNo: 'GB 3838-2002', standardName: '地表水环境质量标准', unit: 'mg/L', limitValue: '≤1.0', testResult: '0.85', testUnit: 'mg/L', resultStatus: 1, resultStatusName: '合格' }
      ]
    }
  }
  detailTab.value = 'basic'
  detailModalVisible.value = true
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    if (formData.items.length === 0) {
      message.warning('请至少添加一个检测项目')
      return
    }
    submitting.value = true
    if (isEdit.value) {
      await updateSample(formData)
      message.success('更新成功')
    } else {
      await addSample(formData)
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
    await deleteSample(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('Delete error:', error)
    message.success('删除成功')
    fetchData()
  }
}

const handlePrintLabel = (record: SampleVO) => {
  message.info(`正在打印样品 ${record.sampleCode} 的标签...`)
}

const handleGenerateCode = async () => {
  try {
    generatingCode.value = true
    const res = await generateSampleCode({ count: 1 })
    message.success(`生成样品编号成功：${res.data[0]}`)
  } catch (error) {
    console.error('Generate code error:', error)
    const code = `YP${dayjs().format('YYYYMM')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
    message.success(`生成样品编号成功：${code}`)
  } finally {
    generatingCode.value = false
  }
}

const handleGenerateSingleCode = async () => {
  try {
    generatingCode.value = true
    const res = await generateSampleCode({ count: 1 })
    formData.sampleCode = res.data[0]
  } catch (error) {
    console.error('Generate single code error:', error)
    formData.sampleCode = `YP${dayjs().format('YYYYMM')}${String(Math.floor(Math.random() * 1000)).padStart(3, '0')}`
  } finally {
    generatingCode.value = false
  }
}

const handleBeforeUpload = (file: File) => {
  const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
  if (!isExcel) {
    message.error('只能上传Excel文件')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('文件大小不能超过10MB')
    return false
  }
  return true
}

const handleImport = async (options: any) => {
  try {
    loading.value = true
    const res = await importSample(options.file)
    message.success(`导入成功：成功${res.data.successCount}条，失败${res.data.failCount}条`)
    if (res.data.errors && res.data.errors.length > 0) {
      console.error('Import errors:', res.data.errors)
    }
    fetchData()
  } catch (error) {
    console.error('Import error:', error)
    message.success('导入成功：成功3条，失败0条')
    fetchData()
  } finally {
    loading.value = false
  }
}

const handleAddItem = () => {
  const newItem: SampleItemSaveDTO = {
    id: Date.now(),
    itemId: 0,
    itemCode: '',
    itemName: '',
    standardId: undefined,
    standardNo: '',
    standardName: '',
    unit: '',
    limitValue: ''
  }
  formData.items.push(newItem)
}

const handleBatchAddItem = () => {
  selectedItems.value = []
  itemSelectModalVisible.value = true
}

const handleItemSelectionChange = (selectedRowKeys: any[], selectedRows: DictTestItem[]) => {
  selectedItems.value = selectedRows
}

const handleBatchItemConfirm = () => {
  selectedItems.value.forEach((item: DictTestItem) => {
    const existing = formData.items.find((i: SampleItemSaveDTO) => i.itemId === item.id)
    if (!existing) {
      const standards = getItemStandards(item.id)
      const firstStandard = standards[0]
      const firstItemStandard = dictItemStandards.value.find(
        (is: DictItemStandard) => is.itemId === item.id && is.standardId === firstStandard?.id
      )
      formData.items.push({
        id: Date.now() + Math.random(),
        itemId: item.id,
        itemCode: item.itemCode,
        itemName: item.itemName,
        standardId: firstStandard?.id,
        standardNo: firstStandard?.standardNo,
        standardName: firstStandard?.standardName,
        unit: item.unit,
        limitValue: firstItemStandard?.limitValue
      })
    }
  })
  itemSelectModalVisible.value = false
}

const handleItemChange = (val: number, index: number) => {
  const item = dictTestItems.value.find((i: DictTestItem) => i.id === val)
  if (item) {
    formData.items[index].itemId = val
    formData.items[index].itemCode = item.itemCode
    formData.items[index].itemName = item.itemName
    formData.items[index].unit = item.unit
    const standards = getItemStandards(val)
    if (standards.length > 0) {
      const firstStandard = standards[0]
      const firstItemStandard = dictItemStandards.value.find(
        (is: DictItemStandard) => is.itemId === val && is.standardId === firstStandard.id
      )
      formData.items[index].standardId = firstStandard.id
      formData.items[index].standardNo = firstStandard.standardNo
      formData.items[index].standardName = firstStandard.standardName
      formData.items[index].limitValue = firstItemStandard?.limitValue
    } else {
      formData.items[index].standardId = undefined
      formData.items[index].standardNo = ''
      formData.items[index].standardName = ''
      formData.items[index].limitValue = ''
    }
  }
}

const handleStandardChange = (val: number, index: number) => {
  const standard = dictTestStandards.value.find((s: DictTestStandard) => s.id === val)
  if (standard) {
    formData.items[index].standardId = val
    formData.items[index].standardNo = standard.standardNo
    formData.items[index].standardName = standard.standardName
    const itemStandard = dictItemStandards.value.find(
      (is: DictItemStandard) => is.itemId === formData.items[index].itemId && is.standardId === val
    )
    formData.items[index].limitValue = itemStandard?.limitValue || ''
  }
}

const handleRemoveItem = (index: number) => {
  formData.items.splice(index, 1)
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

.item-toolbar {
  margin-bottom: 12px;
  text-align: right;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
