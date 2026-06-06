<template>
  <div class="sample-label">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="样品编号">
            <a-input v-model:value="queryParams.sampleCode" placeholder="请输入样品编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="样品名称">
            <a-input v-model:value="queryParams.sampleName" placeholder="请输入样品名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="标签类型">
            <a-select v-model:value="queryParams.labelType" placeholder="请选择" style="width: 140px" allow-clear>
              <a-select-option :value="1">二维码标签</a-select-option>
              <a-select-option :value="2">条形码标签</a-select-option>
              <a-select-option :value="3">混合标签</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="打印状态">
            <a-select v-model:value="queryParams.printStatus" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">未打印</a-select-option>
              <a-select-option :value="1">已打印</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="创建时间">
            <a-range-picker
              v-model:value="createTimeRange"
              style="width: 260px"
              value-format="YYYY-MM-DD HH:mm:ss"
              :placeholder="['开始时间', '结束时间']"
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
          <a-button type="primary" @click="handleGenerateLabel">
            <PlusOutlined /> 生成标签
          </a-button>
          <a-button type="primary" style="margin-left: 8px" :disabled="selectedRowKeys.length === 0" @click="handleBatchPrint">
            <PrinterOutlined /> 打印标签
          </a-button>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'index'">
            {{ (pagination.current - 1) * pagination.pageSize + record._index + 1 }}
          </template>
          <template v-else-if="column.key === 'labelType'">
            <a-tag :color="getLabelTypeColor(record.labelType)">
              {{ record.labelTypeName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'qrCode'">
            <div class="code-display">
              <QrcodeOutlined class="code-icon" />
              <img v-if="record.qrCodeUrl" :src="record.qrCodeUrl" class="code-thumb" @click="handlePreview(record)" />
              <span v-else class="code-empty">暂无</span>
            </div>
          </template>
          <template v-else-if="column.key === 'barcode'">
            <div class="code-display">
              <BarcodeOutlined class="code-icon" />
              <img v-if="record.barcodeUrl" :src="record.barcodeUrl" class="code-thumb barcode-thumb" @click="handlePreview(record)" />
              <span v-else class="code-empty">暂无</span>
            </div>
          </template>
          <template v-else-if="column.key === 'printStatus'">
            <a-tag :color="record.printStatus === 1 ? 'green' : 'orange'">
              {{ record.printStatusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'printCount'">
            {{ record.printCount ?? 0 }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handlePreview(record)">
              <EyeOutlined /> 预览
            </a-button>
            <a-button type="link" size="small" @click="handlePrint(record)">
              <PrinterOutlined /> 打印
            </a-button>
            <a-button type="link" size="small" :disabled="record.printStatus !== 1" @click="handleReprint(record)">
              重打
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="generateModalVisible"
      title="生成标签"
      width="700px"
      @ok="handleGenerateSubmit"
      @cancel="generateModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form :model="generateForm" layout="vertical">
        <a-form-item label="选择样品">
          <a-select
            v-model:value="generateForm.sampleIds"
            mode="multiple"
            placeholder="请选择要生成标签的样品"
            style="width: 100%"
            :maxTagCount="3"
          >
            <a-select-option v-for="sample in sampleOptions" :key="sample.id" :value="sample.id">
              {{ sample.sampleCode }} - {{ sample.sampleName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="标签类型">
              <a-select v-model:value="generateForm.labelType" placeholder="请选择标签类型" style="width: 100%">
                <a-select-option :value="1">二维码</a-select-option>
                <a-select-option :value="2">条形码</a-select-option>
                <a-select-option :value="3">两者</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="标签尺寸">
              <a-select v-model:value="generateForm.labelSize" placeholder="请选择标签尺寸" style="width: 100%">
                <a-select-option value="50x30">50mm × 30mm</a-select-option>
                <a-select-option value="60x40">60mm × 40mm</a-select-option>
                <a-select-option value="80x50">80mm × 50mm</a-select-option>
                <a-select-option value="100x60">100mm × 60mm</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="打印份数">
              <a-input-number v-model:value="generateForm.printCount" :min="1" :max="100" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="previewModalVisible"
      title="标签预览"
      width="800px"
      :footer="null"
    >
      <div class="preview-container" id="label-preview">
        <div v-if="previewLabel" class="label-preview-card" :class="`size-${previewLabel.labelSize || '60x40'}`">
          <div class="label-header">
            <span class="label-title">样品标签</span>
            <span class="label-code">{{ previewLabel.sampleCode }}</span>
          </div>
          <div class="label-body">
            <div class="label-info">
              <div class="info-item">
                <span class="label">样品名称：</span>
                <span class="value">{{ previewLabel.sampleName }}</span>
              </div>
              <div class="info-item">
                <span class="label">样品编号：</span>
                <span class="value">{{ previewLabel.sampleCode }}</span>
              </div>
              <div class="info-item" v-if="previewLabel.labelType === 1 || previewLabel.labelType === 3">
                <span class="label">二维码：</span>
                <span class="value">{{ previewLabel.qrCode }}</span>
              </div>
              <div class="info-item" v-if="previewLabel.labelType === 2 || previewLabel.labelType === 3">
                <span class="label">条形码：</span>
                <span class="value">{{ previewLabel.barcode }}</span>
              </div>
            </div>
            <div class="label-codes">
              <div v-if="previewLabel.labelType === 1 || previewLabel.labelType === 3" class="code-item">
                <div class="code-title">
                  <QrcodeOutlined /> 二维码
                </div>
                <img v-if="previewLabel.qrCodeUrl" :src="previewLabel.qrCodeUrl" class="qr-code-img" />
                <span v-else class="code-empty">暂无</span>
              </div>
              <div v-if="previewLabel.labelType === 2 || previewLabel.labelType === 3" class="code-item">
                <div class="code-title">
                  <BarcodeOutlined /> 条形码
                </div>
                <img v-if="previewLabel.barcodeUrl" :src="previewLabel.barcodeUrl" class="bar-code-img" />
                <span v-else class="code-empty">暂无</span>
              </div>
            </div>
          </div>
          <div class="label-footer">
            <span>生成时间：{{ previewLabel.createTime }}</span>
          </div>
        </div>
        <div class="preview-actions">
          <a-button type="primary" @click="handlePrintPreview">
            <PrinterOutlined /> 打印预览
          </a-button>
          <a-button @click="handlePrintFromPreview" style="margin-left: 8px">
            <PrinterOutlined /> 直接打印
          </a-button>
        </div>
      </div>
    </a-modal>

    <a-modal
      v-model:open="printModalVisible"
      title="打印设置"
      width="500px"
      @ok="handlePrintSubmit"
      @cancel="printModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form :model="printForm" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="标签尺寸">
              <a-select v-model:value="printForm.labelSize" style="width: 100%">
                <a-select-option value="50x30">50mm × 30mm</a-select-option>
                <a-select-option value="60x40">60mm × 40mm</a-select-option>
                <a-select-option value="80x50">80mm × 50mm</a-select-option>
                <a-select-option value="100x60">100mm × 60mm</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="打印份数">
              <a-input-number v-model:value="printForm.printCount" :min="1" :max="100" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="已选择样品">
          <a-tag v-for="item in printLabels" :key="item.id" color="blue" style="margin-bottom: 4px">
            {{ item.sampleCode }} - {{ item.sampleName }}
          </a-tag>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TableProps } from 'ant-design-vue'
import {
  QrcodeOutlined,
  BarcodeOutlined,
  PrinterOutlined,
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EyeOutlined
} from '@ant-design/icons-vue'
import type {
  SampleLabelQuery,
  SampleLabelPrintDTO,
  SampleLabelVO,
  SampleVO
} from '@/types'
import {
  getSampleLabelPage,
  generateLabel,
  printLabel,
  reprintLabel,
  getSampleList
} from '@/api/sample'

const loading = ref(false)
const submitting = ref(false)
const generateModalVisible = ref(false)
const previewModalVisible = ref(false)
const printModalVisible = ref(false)
const selectedRowKeys = ref<number[]>([])
const selectedRows = ref<SampleLabelVO[]>([])
const createTimeRange = ref<any[]>([])

const queryParams = reactive<SampleLabelQuery>({
  pageNum: 1,
  pageSize: 10,
  sampleCode: '',
  sampleName: '',
  labelType: undefined,
  printStatus: undefined,
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

const rowSelection: TableProps['rowSelection'] = {
  selectedRowKeys,
  onChange: (keys: number[], rows: SampleLabelVO[]) => {
    selectedRowKeys.value = keys
    selectedRows.value = rows
  },
  onSelectAll: (selected: boolean, selectedRows: SampleLabelVO[], changeRows: SampleLabelVO[]) => {
    if (selected) {
      selectedRowKeys.value = tableData.value.map(item => item.id)
    } else {
      selectedRowKeys.value = []
    }
  }
}

const tableData = ref<SampleLabelVO[]>([])
const sampleOptions = ref<SampleVO[]>([])
const previewLabel = ref<SampleLabelVO | null>(null)
const printLabels = ref<SampleLabelVO[]>([])

const generateForm = reactive<SampleLabelPrintDTO>({
  sampleIds: [],
  labelType: 3,
  labelSize: '60x40',
  printCount: 1
})

const printForm = reactive({
  labelSize: '60x40',
  printCount: 1
})

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70 },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 160, ellipsis: true },
  { title: '标签类型', dataIndex: 'labelType', key: 'labelType', width: 120 },
  { title: '二维码', dataIndex: 'qrCode', key: 'qrCode', width: 120 },
  { title: '条形码', dataIndex: 'barcode', key: 'barcode', width: 160 },
  { title: '打印状态', dataIndex: 'printStatus', key: 'printStatus', width: 100 },
  { title: '打印次数', dataIndex: 'printCount', key: 'printCount', width: 100 },
  { title: '打印时间', dataIndex: 'printTime', key: 'printTime', width: 170 },
  { title: '操作', key: 'action', width: 220, fixed: 'right' }
]

const getLabelTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'blue', 2: 'purple', 3: 'cyan' }
  return colors[type || 0] || 'default'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSampleLabelPage(queryParams)
    tableData.value = res.data.list.map((item, index) => ({ ...item, _index: index }))
    pagination.value.total = res.data.total
    pagination.value.current = res.data.pageNum
    pagination.value.pageSize = res.data.pageSize
  } catch (error) {
    console.error('Fetch label list error:', error)
    message.error('获取标签列表失败')
    tableData.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
}

const fetchSampleOptions = async () => {
  try {
    const res = await getSampleList()
    sampleOptions.value = res.data
  } catch (error) {
    console.error('Fetch sample list error:', error)
    message.error('获取样品列表失败')
    sampleOptions.value = []
  }
}

const handleQuery = () => {
  if (createTimeRange.value && createTimeRange.value.length === 2) {
    queryParams.createTimeStart = createTimeRange.value[0]
    queryParams.createTimeEnd = createTimeRange.value[1]
  } else {
    queryParams.createTimeStart = ''
    queryParams.createTimeEnd = ''
  }
  queryParams.pageNum = 1
  pagination.value.current = 1
  selectedRowKeys.value = []
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    sampleCode: '',
    sampleName: '',
    labelType: undefined,
    printStatus: undefined,
    createTimeStart: '',
    createTimeEnd: ''
  })
  createTimeRange.value = []
  selectedRowKeys.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleGenerateLabel = () => {
  generateForm.sampleIds = []
  generateForm.labelType = 3
  generateForm.labelSize = '60x40'
  generateForm.printCount = 1
  fetchSampleOptions()
  generateModalVisible.value = true
}

const handleGenerateSubmit = async () => {
  if (generateForm.sampleIds.length === 0) {
    message.warning('请选择至少一个样品')
    return
  }
  if (!generateForm.labelType) {
    message.warning('请选择标签类型')
    return
  }
  submitting.value = true
  try {
    await generateLabel(generateForm.sampleIds, generateForm.labelType)
    message.success('标签生成成功')
    generateModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Generate label error:', error)
    message.error('标签生成失败')
  } finally {
    submitting.value = false
  }
}

const handlePreview = (record: SampleLabelVO) => {
  previewLabel.value = { ...record, labelSize: '60x40' }
  previewModalVisible.value = true
}

const handlePrintPreview = () => {
  const printContent = document.getElementById('label-preview')?.innerHTML
  const printWindow = window.open('', '_blank', 'width=800,height=600')
  if (printWindow) {
    printWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>标签打印预览</title>
        <style>
          body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
          .label-preview-card { border: 1px solid #ddd; padding: 15px; margin-bottom: 20px; }
          .label-header { display: flex; justify-content: space-between; border-bottom: 1px solid #eee; padding-bottom: 8px; margin-bottom: 12px; }
          .label-title { font-weight: bold; font-size: 16px; }
          .label-code { color: #666; }
          .label-body { display: flex; gap: 20px; }
          .label-info { flex: 1; }
          .info-item { margin-bottom: 6px; font-size: 12px; }
          .info-item .label { color: #666; }
          .label-codes { display: flex; gap: 20px; align-items: center; }
          .code-item { text-align: center; }
          .code-title { font-size: 12px; color: #666; margin-bottom: 8px; }
          .qr-code-img { width: 80px; height: 80px; }
          .bar-code-img { width: 120px; height: 60px; }
          .label-footer { border-top: 1px solid #eee; padding-top: 8px; margin-top: 12px; color: #999; font-size: 12px; }
          @media print {
            .preview-actions { display: none; }
          }
        </style>
      </head>
      <body>
        ${printContent}
        <script>
          window.onload = function() {
            window.print();
          };
        </script>
      </body>
      </html>
    `)
    printWindow.document.close()
  }
}

const handlePrintFromPreview = () => {
  if (previewLabel.value) {
    printLabels.value = [previewLabel.value]
    printForm.labelSize = '60x40'
    printForm.printCount = 1
    previewModalVisible.value = false
    printModalVisible.value = true
  }
}

const handlePrint = (record: SampleLabelVO) => {
  printLabels.value = [record]
  printForm.labelSize = '60x40'
  printForm.printCount = 1
  printModalVisible.value = true
}

const handleBatchPrint = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要打印的标签')
    return
  }
  printLabels.value = selectedRows.value
  printForm.labelSize = '60x40'
  printForm.printCount = 1
  printModalVisible.value = true
}

const handlePrintSubmit = async () => {
  submitting.value = true
  try {
    const data: SampleLabelPrintDTO = {
      sampleIds: printLabels.value.map(item => item.sampleId),
      labelType: printLabels.value[0]?.labelType || 3,
      labelSize: printForm.labelSize,
      printCount: printForm.printCount
    }
    await printLabel(data)
    message.success('打印成功')
    printModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Print error:', error)
    message.error('打印失败')
  } finally {
    submitting.value = false
  }
}

const handleReprint = async (record: SampleLabelVO) => {
  try {
    await reprintLabel([record.id])
    message.success('重打成功')
    fetchData()
  } catch (error) {
    console.error('Reprint error:', error)
    message.error('重打失败')
  }
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

.code-display {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.code-icon {
  font-size: 16px;
  color: #1890ff;
}

.code-thumb {
  width: 40px;
  height: 40px;
  border: 1px solid #eee;
  border-radius: 4px;
  object-fit: contain;
}

.code-thumb.barcode-thumb {
  width: 60px;
  height: 40px;
}

.code-empty {
  color: #999;
  font-size: 12px;
}

.preview-container {
  text-align: center;
}

.label-preview-card {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
  margin: 0 auto 20px;
  max-width: 600px;
}

.label-preview-card.size-50x30 {
  max-width: 350px;
}

.label-preview-card.size-60x40 {
  max-width: 420px;
}

.label-preview-card.size-80x50 {
  max-width: 560px;
}

.label-preview-card.size-100x60 {
  max-width: 700px;
}

.label-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 12px;
  margin-bottom: 16px;
}

.label-title {
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

.label-code {
  font-size: 14px;
  color: #666;
  font-family: monospace;
}

.label-body {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.label-info {
  flex: 1;
  text-align: left;
}

.info-item {
  margin-bottom: 8px;
  font-size: 14px;
}

.info-item .label {
  color: #888;
  display: inline-block;
  min-width: 80px;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

.label-codes {
  display: flex;
  gap: 20px;
  align-items: center;
}

.code-item {
  text-align: center;
}

.code-title {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.qr-code-img {
  width: 100px;
  height: 100px;
  border: 1px solid #ddd;
  padding: 4px;
  background: #fff;
}

.bar-code-img {
  width: 150px;
  height: 60px;
  border: 1px solid #ddd;
  padding: 4px;
  background: #fff;
}

.label-footer {
  border-top: 1px solid #e8e8e8;
  padding-top: 12px;
  margin-top: 16px;
  color: #999;
  font-size: 12px;
  text-align: right;
}

.preview-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding-top: 16px;
}

@media print {
  .preview-actions {
    display: none;
  }
}
</style>
