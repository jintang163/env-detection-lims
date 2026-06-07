<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">质控样品管理</div>
      <div class="page-desc">管理质控样品（标准样、加标样、平行样、空白样）的入库、配制、有效期等信息</div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">样品总数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card valid" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">在有效期内</div>
              <div class="stat-value">{{ stats.valid || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">即将过期</div>
              <div class="stat-value">{{ stats.warning || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card expired" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已过期</div>
              <div class="stat-value">{{ stats.expired || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="样品库存" name="stock">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索样品名称、批号..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="searchType" placeholder="样品类型" clearable style="width: 140px">
              <el-option label="标准样" value="STANDARD" />
              <el-option label="加标样" value="SPIKE" />
              <el-option label="平行样" value="PARALLEL" />
              <el-option label="空白样" value="BLANK" />
            </el-select>
            <el-select v-model="searchStatus" placeholder="有效期状态" clearable style="width: 140px">
              <el-option label="在有效期内" :value="1" />
              <el-option label="即将过期" :value="2" />
              <el-option label="已过期" :value="3" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              入库登记
            </el-button>
            <el-button type="success" @click="handleBatchImport">
              <el-icon><Upload /></el-icon>
              批量入库
            </el-button>
            <el-button @click="fetchList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="tableData"
            v-loading="loading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="sampleNo" label="样品编号" width="160" />
            <el-table-column prop="sampleName" label="样品名称" width="180" />
            <el-table-column prop="sampleType" label="样品类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getSampleTypeTag(row.sampleType)" effect="light">
                  {{ getSampleTypeText(row.sampleType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="batchNo" label="批号" width="140" />
            <el-table-column prop="concentration" label="浓度值" width="140">
              <template #default="{ row }">
                {{ row.concentration }} {{ row.unit || '' }}
              </template>
            </el-table-column>
            <el-table-column prop="uncertainty" label="不确定度" width="120" />
            <el-table-column prop="stockQuantity" label="库存数量" width="100" />
            <el-table-column prop="prepareDate" label="配制日期" width="120" />
            <el-table-column prop="expireDate" label="有效期至" width="120" />
            <el-table-column label="有效期状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getExpireStatusTag(row)" effect="light">
                  {{ getExpireStatusText(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="storageCondition" label="储存条件" width="120" show-overflow-tooltip />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="success" link @click="handlePrepare(row)">
                  <el-icon><Tools /></el-icon>
                  配制记录
                </el-button>
                <el-button type="info" link @click="handleView(row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button type="danger" link @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchList"
            @current-change="fetchList"
          />
        </el-tab-pane>

        <el-tab-pane label="配制记录" name="prepare">
          <div class="toolbar">
            <el-input
              v-model="prepareSearchKeyword"
              placeholder="搜索配制记录..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchPrepareList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="fetchPrepareList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="prepareTableData"
            v-loading="prepareLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="prepareNo" label="配制单号" width="160" />
            <el-table-column prop="sampleName" label="样品名称" width="180" />
            <el-table-column prop="sampleType" label="样品类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getSampleTypeTag(row.sampleType)" effect="light">
                  {{ getSampleTypeText(row.sampleType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="batchNo" label="批号" width="140" />
            <el-table-column prop="prepareVolume" label="配制体积" width="120">
              <template #default="{ row }">
                {{ row.prepareVolume }} {{ row.volumeUnit || 'mL' }}
              </template>
            </el-table-column>
            <el-table-column prop="concentration" label="目标浓度" width="140">
              <template #default="{ row }">
                {{ row.concentration }} {{ row.unit || '' }}
              </template>
            </el-table-column>
            <el-table-column prop="prepareMethod" label="配制方法" min-width="150" show-overflow-tooltip />
            <el-table-column prop="prepareBy" label="配制人" width="100" />
            <el-table-column prop="prepareTime" label="配制时间" width="180" />
            <el-table-column prop="verifyBy" label="校核人" width="100" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleViewPrepare(row)">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
                <el-button type="success" link @click="handlePrintPrepare(row)">
                  <el-icon><Printer /></el-icon>
                  打印
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="preparePagination.pageNum"
            v-model:page-size="preparePagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="preparePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchPrepareList"
            @current-change="fetchPrepareList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" top="5vh">
      <el-form
        :model="sampleForm"
        :rules="formRules"
        ref="sampleFormRef"
        label-width="100px"
        v-loading="detailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="样品编号" prop="sampleNo">
              <el-input v-model="sampleForm.sampleNo" placeholder="自动生成或手动输入" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="样品名称" prop="sampleName">
              <el-input v-model="sampleForm.sampleName" placeholder="请输入样品名称" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="样品类型" prop="sampleType">
              <el-select v-model="sampleForm.sampleType" placeholder="请选择样品类型" :disabled="isView" style="width: 100%">
                <el-option label="标准样" value="STANDARD" />
                <el-option label="加标样" value="SPIKE" />
                <el-option label="平行样" value="PARALLEL" />
                <el-option label="空白样" value="BLANK" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批号" prop="batchNo">
              <el-input v-model="sampleForm.batchNo" placeholder="请输入批号" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="浓度值" prop="concentration">
              <el-input-number
                v-model="sampleForm.concentration"
                :precision="4"
                :step="0.001"
                :min="0"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="sampleForm.unit" placeholder="请选择单位" :disabled="isView" style="width: 100%">
                <el-option label="mg/L" value="mg/L" />
                <el-option label="μg/L" value="μg/L" />
                <el-option label="ng/L" value="ng/L" />
                <el-option label="mg/kg" value="mg/kg" />
                <el-option label="μg/kg" value="μg/kg" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="不确定度" prop="uncertainty">
              <el-input v-model="sampleForm.uncertainty" placeholder="如: ±5%" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number
                v-model="sampleForm.stockQuantity"
                :min="0"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配制日期" prop="prepareDate">
              <el-date-picker
                v-model="sampleForm.prepareDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期至" prop="expireDate">
              <el-date-picker
                v-model="sampleForm.expireDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="储存条件" prop="storageCondition">
          <el-input
            v-model="sampleForm.storageCondition"
            placeholder="如: 2-8℃冷藏、避光保存"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item label="溯源信息" prop="traceability">
          <el-input
            v-model="sampleForm.traceability"
            type="textarea"
            :rows="2"
            placeholder="标准物质来源、溯源链等信息"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="sampleForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isView"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="prepareDialogVisible" title="配制记录" width="800px" top="5vh">
      <el-form
        :model="prepareForm"
        :rules="prepareRules"
        ref="prepareFormRef"
        label-width="100px"
        v-loading="prepareDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配制单号" prop="prepareNo">
              <el-input v-model="prepareForm.prepareNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="样品名称" prop="sampleName">
              <el-input v-model="prepareForm.sampleName" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="样品类型" prop="sampleType">
              <el-select v-model="prepareForm.sampleType" style="width: 100%">
                <el-option label="标准样" value="STANDARD" />
                <el-option label="加标样" value="SPIKE" />
                <el-option label="平行样" value="PARALLEL" />
                <el-option label="空白样" value="BLANK" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="批号" prop="batchNo">
              <el-input v-model="prepareForm.batchNo" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配制体积" prop="prepareVolume">
              <el-input-number
                v-model="prepareForm.prepareVolume"
                :precision="2"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="目标浓度" prop="concentration">
              <el-input-number
                v-model="prepareForm.concentration"
                :precision="4"
                :step="0.001"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="浓度单位" prop="unit">
              <el-select v-model="prepareForm.unit" style="width: 100%">
                <el-option label="mg/L" value="mg/L" />
                <el-option label="μg/L" value="μg/L" />
                <el-option label="ng/L" value="ng/L" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体积单位" prop="volumeUnit">
              <el-select v-model="prepareForm.volumeUnit" style="width: 100%">
                <el-option label="mL" value="mL" />
                <el-option label="L" value="L" />
                <el-option label="μL" value="μL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="配制方法" prop="prepareMethod">
          <el-input
            v-model="prepareForm.prepareMethod"
            type="textarea"
            :rows="3"
            placeholder="请详细描述配制过程"
          />
        </el-form-item>

        <el-form-item label="使用试剂">
          <el-input
            v-model="prepareForm.reagents"
            type="textarea"
            :rows="2"
            placeholder="使用的标准物质、试剂等"
          />
        </el-form-item>

        <el-form-item label="使用仪器">
          <el-input
            v-model="prepareForm.instruments"
            type="textarea"
            :rows="2"
            placeholder="使用的天平、移液枪等仪器"
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配制人" prop="prepareBy">
              <el-input v-model="prepareForm.prepareBy" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="校核人" prop="verifyBy">
              <el-input v-model="prepareForm.verifyBy" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="环境条件">
          <el-input
            v-model="prepareForm.environment"
            placeholder="如: 温度20℃, 湿度50%"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="prepareForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="prepareDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPrepare">
          <el-icon><Check /></el-icon>
          保存配制记录
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { qualityControlApi } from '@/api/detection'
import dayjs from 'dayjs'

const loading = ref(false)
const prepareLoading = ref(false)
const detailLoading = ref(false)
const prepareDetailLoading = ref(false)
const searchKeyword = ref('')
const searchType = ref(null)
const searchStatus = ref(null)
const prepareSearchKeyword = ref('')
const activeTab = ref('stock')
const tableData = ref([])
const prepareTableData = ref([])
const dialogVisible = ref(false)
const prepareDialogVisible = ref(false)
const dialogTitle = ref('')
const isView = ref(false)
const sampleFormRef = ref(null)
const prepareFormRef = ref(null)

const stats = reactive({
  total: 0,
  valid: 0,
  warning: 0,
  expired: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const preparePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const sampleForm = reactive({
  id: null,
  sampleNo: '',
  sampleName: '',
  sampleType: '',
  batchNo: '',
  concentration: null,
  unit: '',
  uncertainty: '',
  stockQuantity: null,
  prepareDate: '',
  expireDate: '',
  storageCondition: '',
  traceability: '',
  remark: ''
})

const prepareForm = reactive({
  id: null,
  prepareNo: '',
  sampleName: '',
  sampleType: '',
  batchNo: '',
  prepareVolume: null,
  volumeUnit: 'mL',
  concentration: null,
  unit: 'mg/L',
  prepareMethod: '',
  reagents: '',
  instruments: '',
  prepareBy: '',
  verifyBy: '',
  environment: '',
  remark: ''
})

const formRules = {
  sampleNo: [{ required: true, message: '请输入样品编号', trigger: 'blur' }],
  sampleName: [{ required: true, message: '请输入样品名称', trigger: 'blur' }],
  sampleType: [{ required: true, message: '请选择样品类型', trigger: 'change' }],
  batchNo: [{ required: true, message: '请输入批号', trigger: 'blur' }],
  concentration: [{ required: true, message: '请输入浓度值', trigger: 'blur' }],
  expireDate: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const prepareRules = {
  sampleName: [{ required: true, message: '请输入样品名称', trigger: 'blur' }],
  sampleType: [{ required: true, message: '请选择样品类型', trigger: 'change' }],
  prepareVolume: [{ required: true, message: '请输入配制体积', trigger: 'blur' }],
  concentration: [{ required: true, message: '请输入目标浓度', trigger: 'blur' }],
  prepareMethod: [{ required: true, message: '请输入配制方法', trigger: 'blur' }],
  prepareBy: [{ required: true, message: '请输入配制人', trigger: 'blur' }]
}

const getSampleTypeText = (type) => {
  const map = {
    STANDARD: '标准样',
    SPIKE: '加标样',
    PARALLEL: '平行样',
    BLANK: '空白样'
  }
  return map[type] || type
}

const getSampleTypeTag = (type) => {
  const map = {
    STANDARD: 'primary',
    SPIKE: 'success',
    PARALLEL: 'warning',
    BLANK: 'info'
  }
  return map[type] || 'info'
}

const getExpireStatus = (row) => {
  const today = dayjs()
  const expireDate = dayjs(row.expireDate)
  const diffDays = expireDate.diff(today, 'day')
  if (diffDays < 0) return 3
  if (diffDays <= 7) return 2
  return 1
}

const getExpireStatusText = (row) => {
  const status = getExpireStatus(row)
  const map = { 1: '在有效期内', 2: '即将过期', 3: '已过期' }
  return map[status]
}

const getExpireStatusTag = (row) => {
  const status = getExpireStatus(row)
  const map = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status]
}

const fetchStats = async () => {
  try {
    const res = await qualityControlApi.sampleStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.valid = res.data.valid || 0
      stats.warning = res.data.warning || 0
      stats.expired = res.data.expired || 0
    }
  } catch (error) {
      stats.total = tableData.value.length
      stats.valid = tableData.value.filter(r => getExpireStatus(r) === 1).length
      stats.warning = tableData.value.filter(r => getExpireStatus(r) === 2).length
      stats.expired = tableData.value.filter(r => getExpireStatus(r) === 3).length
    }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      sampleType: searchType.value,
      expireStatus: searchStatus.value
    }
    const res = await qualityControlApi.samplePage(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      tableData.value = []
      pagination.total = 0
    }
    await fetchStats()
  } catch (error) {
    console.error('获取样品列表失败', error)
    ElMessage.error('获取样品列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const fetchPrepareList = async () => {
  prepareLoading.value = true
  try {
    const params = {
      pageNum: preparePagination.pageNum,
      pageSize: preparePagination.pageSize,
      keyword: prepareSearchKeyword.value
    }
    const res = await qualityControlApi.preparePage(params)
    if (res.data?.records) {
      prepareTableData.value = res.data.records
      preparePagination.total = res.data.total
    } else {
      prepareTableData.value = []
      preparePagination.total = 0
    }
  } catch (error) {
    console.error('获取配制记录失败', error)
    ElMessage.error('获取配制记录失败')
    prepareTableData.value = []
    preparePagination.total = 0
  } finally {
    prepareLoading.value = false
  }
}

const handleTabChange = () => {
  if (activeTab.value === 'stock') {
    fetchList()
  } else {
    fetchPrepareList()
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '入库登记'
  Object.assign(sampleForm, {
    id: null,
    sampleNo: '',
    sampleName: '',
    sampleType: '',
    batchNo: '',
    concentration: null,
    unit: '',
    uncertainty: '',
    stockQuantity: null,
    prepareDate: '',
    expireDate: '',
    storageCondition: '',
    traceability: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑样品'
  Object.assign(sampleForm, { ...row })
  dialogVisible.value = true
}

const handleView = (row) => {
  isView.value = true
  dialogTitle.value = '样品详情'
  Object.assign(sampleForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除样品"${row.sampleName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await qualityControlApi.deleteSample(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('删除失败', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    await sampleFormRef.value.validate()
    if (sampleForm.id) {
      await qualityControlApi.updateSample(sampleForm)
      ElMessage.success('更新成功')
    } else {
      await qualityControlApi.saveSample(sampleForm)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('提交失败', error)
    }
  }
}

const handlePrepare = (row) => {
  Object.assign(prepareForm, {
    id: null,
    prepareNo: 'PRE-' + dayjs().format('YYYYMMDDHHmmss'),
    sampleName: row.sampleName,
    sampleType: row.sampleType,
    batchNo: row.batchNo,
    prepareVolume: null,
    volumeUnit: 'mL',
    concentration: row.concentration,
    unit: row.unit,
    prepareMethod: '',
    reagents: '',
    instruments: '',
    prepareBy: '',
    verifyBy: '',
    environment: '',
    remark: ''
  })
  prepareDialogVisible.value = true
}

const handleViewPrepare = (row) => {
  Object.assign(prepareForm, { ...row })
  prepareDialogVisible.value = true
}

const handlePrintPrepare = (row) => {
  ElMessage.info('打印功能开发中')
}

const handleBatchImport = () => {
  ElMessage.info('批量入库功能开发中')
}

const handleSubmitPrepare = async () => {
  try {
    await prepareFormRef.value.validate()
    await qualityControlApi.savePrepare(prepareForm)
    ElMessage.success('保存成功')
    prepareDialogVisible.value = false
    fetchPrepareList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('保存失败', error)
    }
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style lang="scss" scoped>
.page-container {
  .page-header {
    margin-bottom: 16px;

    .page-title {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }

    .page-desc {
      font-size: 13px;
      color: #909399;
    }
  }

  .stats-row {
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: #fff;
        }

        .stat-info {
          .stat-label {
            font-size: 14px;
            color: #606266;
            margin-bottom: 4px;
          }

          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
          }
        }
      }

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
      }

      &.valid .stat-icon {
        background: linear-gradient(135deg, #4facfe, #00f2fe);
      }

      &.warning .stat-icon {
        background: linear-gradient(135deg, #f093fb, #f5576c);
      }

      &.expired .stat-icon {
        background: linear-gradient(135deg, #fa709a, #fee140);
      }
    }
  }

  .toolbar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
}
</style>
