<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">质控计划管理</div>
      <div class="page-desc">制定日常质控计划，包括每批次/周期运行的质控样，支持自动提醒</div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">计划总数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card active" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">执行中</div>
              <div class="stat-value">{{ stats.active || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card today" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><AlarmClock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日待执行</div>
              <div class="stat-value">{{ stats.today || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card missed" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">逾期未执行</div>
              <div class="stat-value">{{ stats.missed || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索计划名称、检测项目..."
          style="width: 260px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchCycle" placeholder="执行周期" clearable style="width: 140px">
          <el-option label="每批次" value="BATCH" />
          <el-option label="每日" value="DAILY" />
          <el-option label="每周" value="WEEKLY" />
          <el-option label="每月" value="MONTHLY" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="计划状态" clearable style="width: 140px">
          <el-option label="未开始" :value="0" />
          <el-option label="执行中" :value="1" />
          <el-option label="已暂停" :value="2" />
          <el-option label="已结束" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增计划
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
        <el-table-column prop="planNo" label="计划编号" width="160" />
        <el-table-column prop="planName" label="计划名称" width="200" />
        <el-table-column prop="projectName" label="检测项目" width="150" />
        <el-table-column prop="methodName" label="检测方法" min-width="150" show-overflow-tooltip />
        <el-table-column label="质控样配置" min-width="200">
          <template #default="{ row }">
            <div class="qc-sample-tags">
              <el-tag
                v-for="(sample, idx) in row.qcSamples"
                :key="idx"
                size="small"
                :type="getSampleTypeTag(sample.type)"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ getSampleTypeText(sample.type) }}
                <span v-if="sample.frequency"> ({{ sample.frequency }})</span>
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="cycleType" label="执行周期" width="100">
          <template #default="{ row }">
            <el-tag type="info" effect="light">
              {{ getCycleTypeText(row.cycleType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reminderEnabled" label="自动提醒" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.reminderEnabled" type="success" effect="light">已开启</el-tag>
            <el-tag v-else type="info" effect="light">未开启</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              link
              @click="handlePause(row)"
            >
              <el-icon><VideoPause /></el-icon>
              暂停
            </el-button>
            <el-button
              v-if="row.status === 2"
              type="success"
              link
              @click="handleResume(row)"
            >
              <el-icon><VideoPlay /></el-icon>
              恢复
            </el-button>
            <el-button type="info" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              执行记录
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
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" top="5vh">
      <el-form
        :model="planForm"
        :rules="formRules"
        ref="planFormRef"
        label-width="120px"
        v-loading="detailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计划编号" prop="planNo">
              <el-input v-model="planForm.planNo" placeholder="自动生成或手动输入" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="planForm.planName" placeholder="请输入计划名称" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检测项目" prop="projectName">
              <el-input v-model="planForm.projectName" placeholder="请输入检测项目" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测方法" prop="methodName">
              <el-input v-model="planForm.methodName" placeholder="请输入检测方法" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="执行周期" prop="cycleType">
              <el-select v-model="planForm.cycleType" placeholder="请选择执行周期" :disabled="isView" style="width: 100%">
                <el-option label="每批次" value="BATCH" />
                <el-option label="每日" value="DAILY" />
                <el-option label="每周" value="WEEKLY" />
                <el-option label="每月" value="MONTHLY" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划状态" prop="status">
              <el-select v-model="planForm.status" placeholder="请选择状态" :disabled="isView" style="width: 100%">
                <el-option label="未开始" :value="0" />
                <el-option label="执行中" :value="1" />
                <el-option label="已暂停" :value="2" />
                <el-option label="已结束" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="planForm.startDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="planForm.endDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">质控样配置</el-divider>

        <el-form-item label="质控样">
          <div class="qc-sample-config">
            <el-table
              :data="planForm.qcSamples"
              border
              size="small"
              style="width: 100%"
            >
              <el-table-column label="样品类型" width="140">
                <template #default="{ row, $index }">
                  <el-select v-model="row.type" placeholder="请选择" :disabled="isView" style="width: 100%">
                    <el-option label="标准样" value="STANDARD" />
                    <el-option label="加标样" value="SPIKE" />
                    <el-option label="平行样" value="PARALLEL" />
                    <el-option label="空白样" value="BLANK" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="质控样名称" width="180">
                <template #default="{ row }">
                  <el-input v-model="row.sampleName" placeholder="请输入" :disabled="isView" />
                </template>
              </el-table-column>
              <el-table-column label="浓度值" width="140">
                <template #default="{ row }">
                  <el-input-number
                    v-model="row.concentration"
                    :precision="4"
                    :step="0.001"
                    :min="0"
                    style="width: 100%"
                    :disabled="isView"
                  />
                </template>
              </el-table-column>
              <el-table-column label="单位" width="100">
                <template #default="{ row }">
                  <el-select v-model="row.unit" placeholder="单位" :disabled="isView" style="width: 100%">
                    <el-option label="mg/L" value="mg/L" />
                    <el-option label="μg/L" value="μg/L" />
                    <el-option label="ng/L" value="ng/L" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="执行频率">
                <template #default="{ row }">
                  <el-input v-model="row.frequency" placeholder="如: 每批次1个" :disabled="isView" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" v-if="!isView">
                <template #default="{ $index }">
                  <el-button type="danger" link @click="removeQcSample($index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button
              v-if="!isView"
              type="primary"
              size="small"
              style="margin-top: 8px"
              @click="addQcSample"
            >
              <el-icon><Plus /></el-icon>
              添加质控样
            </el-button>
          </div>
        </el-form-item>

        <el-divider content-position="left">提醒配置</el-divider>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开启自动提醒" prop="reminderEnabled">
              <el-switch v-model="planForm.reminderEnabled" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提醒方式" prop="reminderType">
              <el-checkbox-group v-model="planForm.reminderType" :disabled="isView || !planForm.reminderEnabled">
                <el-checkbox label="SYSTEM">系统消息</el-checkbox>
                <el-checkbox label="EMAIL">邮件</el-checkbox>
                <el-checkbox label="SMS">短信</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="提前提醒时间" prop="reminderAdvance">
              <el-input-number
                v-model="planForm.reminderAdvance"
                :min="0"
                :max="120"
                placeholder="分钟"
                style="width: 100%"
                :disabled="isView || !planForm.reminderEnabled"
              />
              <span style="margin-left: 8px; color: #909399">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提醒对象" prop="reminderUsers">
              <el-input
                v-model="planForm.reminderUsers"
                placeholder="多个用户用逗号分隔"
                :disabled="isView || !planForm.reminderEnabled"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="计划描述">
          <el-input
            v-model="planForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入计划描述"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="planForm.remark"
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

    <el-dialog v-model="recordDialogVisible" title="执行记录" width="900px" top="5vh">
      <div v-loading="recordLoading">
        <el-alert
          v-if="currentPlan"
          :title="`计划: ${currentPlan.planName}`"
          type="info"
          show-icon
          style="margin-bottom: 16px"
        />

        <div class="toolbar">
          <el-input
            v-model="recordSearchKeyword"
            placeholder="搜索执行记录..."
            style="width: 260px"
            clearable
            @keyup.enter="fetchRecordList"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="recordSearchStatus" placeholder="执行状态" clearable style="width: 140px">
            <el-option label="待执行" :value="0" />
            <el-option label="执行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已逾期" :value="3" />
          </el-select>
          <el-button @click="fetchRecordList">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>

        <el-table
          :data="recordTableData"
          border
          stripe
          style="width: 100%; margin-top: 16px"
        >
          <el-table-column prop="recordNo" label="执行编号" width="160" />
          <el-table-column prop="executeDate" label="执行日期" width="180" />
          <el-table-column prop="batchNo" label="批次号" width="140" />
          <el-table-column prop="qcSampleType" label="质控样类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getSampleTypeTag(row.qcSampleType)" effect="light" size="small">
                {{ getSampleTypeText(row.qcSampleType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="qcSampleName" label="质控样名称" width="160" />
          <el-table-column prop="measuredValue" label="测定值" width="120" />
          <el-table-column prop="executeBy" label="执行人" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getExecuteStatusTag(row.status)" effect="light" size="small">
                {{ getExecuteStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleViewChart(row)">
                <el-icon><TrendCharts /></el-icon>
                查看图表
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="recordPagination.pageNum"
          v-model:page-size="recordPagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="recordPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          style="margin-top: 16px; justify-content: flex-end"
          @size-change="fetchRecordList"
          @current-change="fetchRecordList"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { qualityControlApi } from '@/api/detection'
import dayjs from 'dayjs'

const loading = ref(false)
const recordLoading = ref(false)
const detailLoading = ref(false)
const searchKeyword = ref('')
const searchCycle = ref(null)
const searchStatus = ref(null)
const recordSearchKeyword = ref('')
const recordSearchStatus = ref(null)
const tableData = ref([])
const recordTableData = ref([])
const dialogVisible = ref(false)
const recordDialogVisible = ref(false)
const dialogTitle = ref('')
const isView = ref(false)
const planFormRef = ref(null)
const currentPlan = ref(null)

const stats = reactive({
  total: 0,
  active: 0,
  today: 0,
  missed: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const recordPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const planForm = reactive({
  id: null,
  planNo: '',
  planName: '',
  projectName: '',
  methodName: '',
  cycleType: '',
  status: 0,
  startDate: '',
  endDate: '',
  qcSamples: [],
  reminderEnabled: false,
  reminderType: [],
  reminderAdvance: 30,
  reminderUsers: '',
  description: '',
  remark: ''
})

const formRules = {
  planNo: [{ required: true, message: '请输入计划编号', trigger: 'blur' }],
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  projectName: [{ required: true, message: '请输入检测项目', trigger: 'blur' }],
  cycleType: [{ required: true, message: '请选择执行周期', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }]
}

const mockPlans = [
  {
    id: 1,
    planNo: 'QCP-2024-001',
    planName: '水质铜检测日常质控',
    projectName: '铜',
    methodName: '火焰原子吸收分光光度法',
    cycleType: 'BATCH',
    status: 1,
    startDate: '2024-01-01',
    endDate: '2024-12-31',
    reminderEnabled: true,
    reminderType: ['SYSTEM', 'EMAIL'],
    reminderAdvance: 30,
    reminderUsers: '张三,李四',
    description: '每批次检测前需运行质控样',
    qcSamples: [
      { type: 'BLANK', sampleName: '实验室空白', concentration: 0, unit: 'mg/L', frequency: '每批次1个' },
      { type: 'STANDARD', sampleName: '铜标准样', concentration: 1.0, unit: 'mg/L', frequency: '每批次1个' },
      { type: 'PARALLEL', sampleName: '平行样', concentration: '', unit: 'mg/L', frequency: '每10个样品1对' }
    ],
    createTime: '2024-01-01 10:00:00'
  },
  {
    id: 2,
    planNo: 'QCP-2024-002',
    planName: '水质铅检测日常质控',
    projectName: '铅',
    methodName: '石墨炉原子吸收分光光度法',
    cycleType: 'DAILY',
    status: 1,
    startDate: '2024-01-01',
    endDate: '2024-12-31',
    reminderEnabled: true,
    reminderType: ['SYSTEM'],
    reminderAdvance: 60,
    reminderUsers: '王五',
    description: '每日首次检测前运行质控样',
    qcSamples: [
      { type: 'BLANK', sampleName: '试剂空白', concentration: 0, unit: 'μg/L', frequency: '每日1个' },
      { type: 'STANDARD', sampleName: '铅标准样', concentration: 20.0, unit: 'μg/L', frequency: '每日1个' },
      { type: 'SPIKE', sampleName: '加标样', concentration: 20.0, unit: 'μg/L', frequency: '每周1个' }
    ],
    createTime: '2024-01-02 14:30:00'
  },
  {
    id: 3,
    planNo: 'QCP-2024-003',
    planName: '土壤镉检测周质控',
    projectName: '镉',
    methodName: '电感耦合等离子体质谱法',
    cycleType: 'WEEKLY',
    status: 0,
    startDate: '2024-03-01',
    endDate: '2024-12-31',
    reminderEnabled: false,
    reminderType: [],
    reminderAdvance: 0,
    reminderUsers: '',
    description: '每周运行一次质控样',
    qcSamples: [
      { type: 'STANDARD', sampleName: '土壤标准物质', concentration: 0.5, unit: 'mg/kg', frequency: '每周1个' }
    ],
    createTime: '2024-02-20 09:00:00'
  }
]

const mockRecords = [
  {
    id: 1,
    recordNo: 'QCR-2024-0220-001',
    executeDate: '2024-02-20 09:30:00',
    batchNo: 'B20240220001',
    qcSampleType: 'BLANK',
    qcSampleName: '实验室空白',
    measuredValue: 0.002,
    unit: 'mg/L',
    executeBy: '张三',
    status: 2
  },
  {
    id: 2,
    recordNo: 'QCR-2024-0220-002',
    executeDate: '2024-02-20 09:35:00',
    batchNo: 'B20240220001',
    qcSampleType: 'STANDARD',
    qcSampleName: '铜标准样',
    measuredValue: 0.985,
    unit: 'mg/L',
    executeBy: '张三',
    status: 2
  },
  {
    id: 3,
    recordNo: 'QCR-2024-0221-001',
    executeDate: '2024-02-21 10:00:00',
    batchNo: 'B20240221001',
    qcSampleType: 'BLANK',
    qcSampleName: '实验室空白',
    measuredValue: 0.001,
    unit: 'mg/L',
    executeBy: '李四',
    status: 2
  },
  {
    id: 4,
    recordNo: 'QCR-2024-0221-002',
    executeDate: '2024-02-21 10:05:00',
    batchNo: 'B20240221001',
    qcSampleType: 'STANDARD',
    qcSampleName: '铜标准样',
    measuredValue: 1.023,
    unit: 'mg/L',
    executeBy: '李四',
    status: 0
  }
]

const getCycleTypeText = (type) => {
  const map = {
    BATCH: '每批次',
    DAILY: '每日',
    WEEKLY: '每周',
    MONTHLY: '每月'
  }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = { 0: '未开始', 1: '执行中', 2: '已暂停', 3: '已结束' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status] || 'info'
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

const getExecuteStatusText = (status) => {
  const map = { 0: '待执行', 1: '执行中', 2: '已完成', 3: '已逾期' }
  return map[status] || status
}

const getExecuteStatusTag = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return map[status] || 'info'
}

const fetchStats = () => {
  stats.total = tableData.value.length
  stats.active = tableData.value.filter(r => r.status === 1).length
  stats.today = 5
  stats.missed = 1
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      cycleType: searchCycle.value,
      status: searchStatus.value
    }
    const res = await qualityControlApi.planPage(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      let data = [...mockPlans]
      if (searchKeyword.value) {
        data = data.filter(item =>
          item.planName.includes(searchKeyword.value) ||
          item.planNo.includes(searchKeyword.value) ||
          item.projectName.includes(searchKeyword.value)
        )
      }
      if (searchCycle.value) {
        data = data.filter(item => item.cycleType === searchCycle.value)
      }
      if (searchStatus.value !== null) {
        data = data.filter(item => item.status === searchStatus.value)
      }
      pagination.total = data.length
      const start = (pagination.pageNum - 1) * pagination.pageSize
      const end = start + pagination.pageSize
      tableData.value = data.slice(start, end)
    }
    fetchStats()
  } catch (error) {
    let data = [...mockPlans]
    if (searchKeyword.value) {
      data = data.filter(item =>
        item.planName.includes(searchKeyword.value) ||
        item.planNo.includes(searchKeyword.value) ||
        item.projectName.includes(searchKeyword.value)
      )
    }
    if (searchCycle.value) {
      data = data.filter(item => item.cycleType === searchCycle.value)
    }
    if (searchStatus.value !== null) {
      data = data.filter(item => item.status === searchStatus.value)
    }
    pagination.total = data.length
    const start = (pagination.pageNum - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    tableData.value = data.slice(start, end)
    fetchStats()
  } finally {
    loading.value = false
  }
}

const fetchRecordList = async () => {
  recordLoading.value = true
  try {
    const params = {
      pageNum: recordPagination.pageNum,
      pageSize: recordPagination.pageSize,
      keyword: recordSearchKeyword.value,
      status: recordSearchStatus.value,
      planId: currentPlan.value?.id
    }
    const res = await qualityControlApi.recordPage(params)
    if (res.data?.records) {
      recordTableData.value = res.data.records
      recordPagination.total = res.data.total
    } else {
      let data = [...mockRecords]
      if (recordSearchKeyword.value) {
        data = data.filter(item =>
          item.recordNo.includes(recordSearchKeyword.value) ||
          item.qcSampleName.includes(recordSearchKeyword.value)
        )
      }
      if (recordSearchStatus.value !== null) {
        data = data.filter(item => item.status === recordSearchStatus.value)
      }
      recordPagination.total = data.length
      const start = (recordPagination.pageNum - 1) * recordPagination.pageSize
      const end = start + recordPagination.pageSize
      recordTableData.value = data.slice(start, end)
    }
  } catch (error) {
    let data = [...mockRecords]
    if (recordSearchKeyword.value) {
      data = data.filter(item =>
        item.recordNo.includes(recordSearchKeyword.value) ||
        item.qcSampleName.includes(recordSearchKeyword.value)
      )
    }
    if (recordSearchStatus.value !== null) {
      data = data.filter(item => item.status === recordSearchStatus.value)
    }
    recordPagination.total = data.length
    const start = (recordPagination.pageNum - 1) * recordPagination.pageSize
    const end = start + recordPagination.pageSize
    recordTableData.value = data.slice(start, end)
  } finally {
    recordLoading.value = false
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增质控计划'
  Object.assign(planForm, {
    id: null,
    planNo: 'QCP-' + dayjs().format('YYYYMMDDHHmmss'),
    planName: '',
    projectName: '',
    methodName: '',
    cycleType: '',
    status: 0,
    startDate: '',
    endDate: '',
    qcSamples: [],
    reminderEnabled: false,
    reminderType: [],
    reminderAdvance: 30,
    reminderUsers: '',
    description: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑质控计划'
  Object.assign(planForm, { ...row, qcSamples: [...row.qcSamples] })
  dialogVisible.value = true
}

const handleView = (row) => {
  currentPlan.value = row
  recordPagination.pageNum = 1
  recordPagination.pageSize = 10
  recordSearchKeyword.value = ''
  recordSearchStatus.value = null
  fetchRecordList()
  recordDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除计划"${row.planName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await qualityControlApi.deletePlan(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      tableData.value = tableData.value.filter(item => item.id !== row.id)
      ElMessage.success('删除成功')
      fetchStats()
    }
  }
}

const handlePause = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要暂停计划"${row.planName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await qualityControlApi.pausePlan(row.id)
    row.status = 2
    ElMessage.success('已暂停')
    fetchStats()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      row.status = 2
      ElMessage.success('已暂停')
      fetchStats()
    }
  }
}

const handleResume = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要恢复计划"${row.planName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await qualityControlApi.resumePlan(row.id)
    row.status = 1
    ElMessage.success('已恢复')
    fetchStats()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      row.status = 1
      ElMessage.success('已恢复')
      fetchStats()
    }
  }
}

const addQcSample = () => {
  planForm.qcSamples.push({
    type: '',
    sampleName: '',
    concentration: null,
    unit: 'mg/L',
    frequency: ''
  })
}

const removeQcSample = (index) => {
  planForm.qcSamples.splice(index, 1)
}

const handleSubmit = async () => {
  try {
    await planFormRef.value.validate()
    if (planForm.id) {
      await qualityControlApi.updatePlan(planForm)
      ElMessage.success('更新成功')
    } else {
      await qualityControlApi.savePlan(planForm)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      if (planForm.id) {
        const idx = tableData.value.findIndex(item => item.id === planForm.id)
        if (idx > -1) {
          tableData.value[idx] = { ...planForm }
        }
        ElMessage.success('更新成功')
      } else {
        tableData.value.unshift({
          ...planForm,
          id: Date.now(),
          createTime: new Date().toLocaleString()
        })
        ElMessage.success('保存成功')
      }
      dialogVisible.value = false
      fetchStats()
    }
  }
}

const handleViewChart = (row) => {
  ElMessage.info('图表功能请前往质控图页面查看')
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

      &.active .stat-icon {
        background: linear-gradient(135deg, #4facfe, #00f2fe);
      }

      &.today .stat-icon {
        background: linear-gradient(135deg, #f093fb, #f5576c);
      }

      &.missed .stat-icon {
        background: linear-gradient(135deg, #fa709a, #fee140);
      }
    }
  }

  .toolbar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .qc-sample-tags {
    display: flex;
    flex-wrap: wrap;
  }

  .qc-sample-config {
    width: 100%;
  }
}
</style>
