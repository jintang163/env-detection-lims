<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">质控图分析</div>
      <div class="page-desc">Levey-Jennings图、Z分数图、Cusum图，自动判定失控/警告，标记违反规则</div>
    </div>

    <el-card shadow="never" style="margin-bottom: 16px">
      <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
        <el-form-item label="检测项目">
          <el-select v-model="searchForm.project" placeholder="请选择检测项目" clearable style="width: 180px">
            <el-option label="铜(Cu)" value="Cu" />
            <el-option label="铅(Pb)" value="Pb" />
            <el-option label="镉(Cd)" value="Cd" />
            <el-option label="锌(Zn)" value="Zn" />
            <el-option label="铬(Cr)" value="Cr" />
          </el-select>
        </el-form-item>
        <el-form-item label="质控样">
          <el-select v-model="searchForm.sampleId" placeholder="请选择质控样" clearable style="width: 200px">
            <el-option
              v-for="sample in qcSampleList"
              :key="sample.id"
              :label="sample.sampleName"
              :value="sample.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出报告
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="4">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总测定次数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card in-control" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">在控</div>
              <div class="stat-value">{{ stats.inControl || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">警告</div>
              <div class="stat-value">{{ stats.warning || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card out-control" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">失控</div>
              <div class="stat-value">{{ stats.outControl || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card mean" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">均值</div>
              <div class="stat-value">{{ stats.mean ? stats.mean.toFixed(4) : '-' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card sd" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><PieChart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">标准差</div>
              <div class="stat-value">{{ stats.sd ? stats.sd.toFixed(4) : '-' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeChartType" @tab-change="handleChartTypeChange">
        <el-tab-pane label="Levey-Jennings图" name="levey">
          <div class="chart-toolbar">
            <el-checkbox v-model="showMeanLine">显示中心线</el-checkbox>
            <el-checkbox v-model="show1SdLine">显示±1SD线</el-checkbox>
            <el-checkbox v-model="show2SdLine">显示±2SD线</el-checkbox>
            <el-checkbox v-model="show3SdLine">显示±3SD线</el-checkbox>
            <el-checkbox v-model="showViolationMarks">显示规则违例标记</el-checkbox>
          </div>
          <div ref="leveyChartRef" class="chart-container"></div>
        </el-tab-pane>

        <el-tab-pane label="Z分数图" name="zscore">
          <div class="chart-toolbar">
            <el-checkbox v-model="showZMeanLine">显示中心线</el-checkbox>
            <el-checkbox v-model="showZ1SdLine">显示±1SD线</el-checkbox>
            <el-checkbox v-model="showZ2SdLine">显示±2SD线</el-checkbox>
            <el-checkbox v-model="showZ3SdLine">显示±3SD线</el-checkbox>
            <el-checkbox v-model="showZViolationMarks">显示规则违例标记</el-checkbox>
          </div>
          <div ref="zscoreChartRef" class="chart-container"></div>
        </el-tab-pane>

        <el-tab-pane label="Cusum图" name="cusum">
          <div class="chart-toolbar">
            <el-checkbox v-model="showCusumLimit">显示控制限</el-checkbox>
            <el-checkbox v-model="showCusumViolation">显示违例标记</el-checkbox>
            <el-form-item label="H值" style="margin-left: 20px; margin-bottom: 0">
              <el-input-number v-model="cusumH" :min="1" :max="10" :step="0.5" @change="renderCusumChart" />
            </el-form-item>
            <el-form-item label="K值" style="margin-left: 20px; margin-bottom: 0">
              <el-input-number v-model="cusumK" :min="0.1" :max="2" :step="0.1" :precision="1" @change="renderCusumChart" />
            </el-form-item>
          </div>
          <div ref="cusumChartRef" class="chart-container"></div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="card-header">
        <div class="card-title">规则判定结果</div>
        <el-tag :type="overallStatus === 'in_control' ? 'success' : overallStatus === 'warning' ? 'warning' : 'danger'" size="large">
          {{ overallStatus === 'in_control' ? '在控' : overallStatus === 'warning' ? '警告' : '失控' }}
        </el-tag>
      </div>

      <el-table :data="violationData" border stripe style="width: 100%; margin-top: 16px">
        <el-table-column prop="seqNo" label="序号" width="80" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="measureDate" label="测定日期" width="180" />
        <el-table-column prop="measuredValue" label="测定值" width="120" />
        <el-table-column prop="zScore" label="Z分数" width="100" />
        <el-table-column label="违反规则" min-width="150">
          <template #default="{ row }">
            <div class="violation-tags">
              <el-tag
                v-for="(rule, idx) in row.violatedRules"
                :key="idx"
                :type="rule.type === 1 ? 'warning' : 'danger'"
                size="small"
                style="margin-right: 4px; margin-bottom: 4px"
              >
                {{ rule.ruleCode }}
              </el-tag>
              <span v-if="!row.violatedRules || row.violatedRules.length === 0" style="color: #909399">-</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'in_control' ? 'success' : row.status === 'warning' ? 'warning' : 'danger'" effect="light" size="small">
              {{ row.status === 'in_control' ? '在控' : row.status === 'warning' ? '警告' : '失控' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="质控数据详情" width="700px" top="5vh">
      <div v-if="currentDetail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="序号">{{ currentDetail.seqNo }}</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ currentDetail.batchNo }}</el-descriptions-item>
          <el-descriptions-item label="测定日期">{{ currentDetail.measureDate }}</el-descriptions-item>
          <el-descriptions-item label="质控样">{{ currentDetail.sampleName }}</el-descriptions-item>
          <el-descriptions-item label="检测项目">{{ currentDetail.project }}</el-descriptions-item>
          <el-descriptions-item label="检测方法">{{ currentDetail.methodName }}</el-descriptions-item>
          <el-descriptions-item label="测定值">{{ currentDetail.measuredValue }} {{ currentDetail.unit }}</el-descriptions-item>
          <el-descriptions-item label="单位">{{ currentDetail.unit }}</el-descriptions-item>
          <el-descriptions-item label="均值(μ)">{{ currentDetail.mean ? currentDetail.mean.toFixed(4) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="标准差(SD)">{{ currentDetail.sd ? currentDetail.sd.toFixed(4) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="Z分数">{{ currentDetail.zScore ? currentDetail.zScore.toFixed(4) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="Cusum值">{{ currentDetail.cusum !== null ? currentDetail.cusum.toFixed(4) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentDetail.status === 'in_control' ? 'success' : currentDetail.status === 'warning' ? 'warning' : 'danger'" effect="light" size="small">
              {{ currentDetail.status === 'in_control' ? '在控' : currentDetail.status === 'warning' ? '警告' : '失控' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="执行人">{{ currentDetail.operator }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">违反规则详情</el-divider>
        <div v-if="currentDetail.violatedRules && currentDetail.violatedRules.length > 0">
          <el-alert
            v-for="(rule, idx) in currentDetail.violatedRules"
            :key="idx"
            :title="`${rule.ruleCode} - ${rule.ruleName}`"
            :type="rule.type === 1 ? 'warning' : 'error'"
            show-icon
            style="margin-bottom: 8px"
          >
            <template #default>
              <p>{{ rule.description }}</p>
              <p style="margin-top: 4px; color: #909399">判定条件: {{ rule.formula }}</p>
            </template>
          </el-alert>
        </div>
        <el-empty v-else description="未违反任何质控规则" />

        <el-divider content-position="left">处理建议</el-divider>
        <el-input
          v-model="currentDetail.suggestion"
          type="textarea"
          :rows="3"
          placeholder="请输入处理建议..."
          readonly
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { qualityControlApi } from '@/api/detection'
import dayjs from 'dayjs'

const leveyChartRef = ref(null)
const zscoreChartRef = ref(null)
const cusumChartRef = ref(null)
let leveyChart = null
let zscoreChart = null
let cusumChart = null

const searchForm = reactive({
  project: 'Cu',
  sampleId: 1,
  dateRange: [dayjs().subtract(30, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
})

const qcSampleList = ref([
  { id: 1, sampleName: '铜标准样(1.0mg/L)', sampleType: 'STANDARD', mean: 1.0, sd: 0.035, unit: 'mg/L' },
  { id: 2, sampleName: '铅标准样(20μg/L)', sampleType: 'STANDARD', mean: 20.0, sd: 0.85, unit: 'μg/L' },
  { id: 3, sampleName: '实验室空白', sampleType: 'BLANK', mean: 0.001, sd: 0.0005, unit: 'mg/L' }
])

const activeChartType = ref('levey')

const showMeanLine = ref(true)
const show1SdLine = ref(true)
const show2SdLine = ref(true)
const show3SdLine = ref(true)
const showViolationMarks = ref(true)

const showZMeanLine = ref(true)
const showZ1SdLine = ref(true)
const showZ2SdLine = ref(true)
const showZ3SdLine = ref(true)
const showZViolationMarks = ref(true)

const showCusumLimit = ref(true)
const showCusumViolation = ref(true)
const cusumH = ref(5)
const cusumK = ref(0.5)

const chartData = ref([])
const violationData = ref([])
const detailVisible = ref(false)
const currentDetail = ref(null)

const stats = reactive({
  total: 0,
  inControl: 0,
  warning: 0,
  outControl: 0,
  mean: 0,
  sd: 0
})

const overallStatus = ref('in_control')

const calculateMean = (arr) => {
  if (!arr || arr.length === 0) return 0
  return arr.reduce((sum, val) => sum + val, 0) / arr.length
}

const calculateSD = (arr, mean) => {
  if (!arr || arr.length === 0) return 0
  const m = mean !== undefined ? mean : calculateMean(arr)
  const squaredDiffs = arr.map(val => Math.pow(val - m, 2))
  return Math.sqrt(squaredDiffs.reduce((sum, val) => sum + val, 0) / (arr.length - 1))
}

const calculateZScore = (value, mean, sd) => {
  if (sd === 0) return 0
  return (value - mean) / sd
}

const calculateCusum = (values, mean, k = 0.5) => {
  const cusumPos = []
  const cusumNeg = []
  let cp = 0
  let cn = 0

  values.forEach((val, i) => {
    const deviation = val - mean
    cp = Math.max(0, cp + deviation - k * stats.sd)
    cn = Math.min(0, cn + deviation + k * stats.sd)
    cusumPos.push(cp)
    cusumNeg.push(cn)
  })

  return { cusumPos, cusumNeg }
}

const checkWestgardRules = (data, mean, sd) => {
  const results = []
  const values = data.map(d => d.measuredValue)

  for (let i = 0; i < data.length; i++) {
    const violations = []
    const z = calculateZScore(values[i], mean, sd)
    const absZ = Math.abs(z)

    if (absZ > 2) {
      violations.push({
        ruleCode: '1₂s',
        ruleName: '1-2s警告规则',
        type: 1,
        description: '单个质控测定值超过均值±2SD',
        formula: '|x - μ| > 2SD'
      })
    }

    if (absZ > 3) {
      violations.push({
        ruleCode: '1₃s',
        ruleName: '1-3s失控规则',
        type: 2,
        description: '单个质控测定值超过均值±3SD',
        formula: '|x - μ| > 3SD'
      })
    }

    if (i >= 1) {
      const z1 = calculateZScore(values[i - 1], mean, sd)
      if ((z > 2 && z1 > 2) || (z < -2 && z1 < -2)) {
        violations.push({
          ruleCode: '2₂s',
          ruleName: '2-2s失控规则',
          type: 2,
          description: '两个连续的质控测定值同时超过均值+2SD或均值-2SD',
          formula: '连续2个值 > μ+2SD 或 < μ-2SD'
        })
      }
    }

    if (i >= 3) {
      let allAbove1Sd = true
      let allBelow1Sd = true
      for (let j = 0; j < 4; j++) {
        const zj = calculateZScore(values[i - j], mean, sd)
        if (zj <= 1) allAbove1Sd = false
        if (zj >= -1) allBelow1Sd = false
      }
      if (allAbove1Sd || allBelow1Sd) {
        violations.push({
          ruleCode: '4₁s',
          ruleName: '4-1s失控规则',
          type: 2,
          description: '4个连续的质控测定值同时超过均值+1SD或均值-1SD',
          formula: '连续4个值 > μ+1SD 或 < μ-1SD'
        })
      }
    }

    if (i >= 9) {
      let allAboveMean = true
      let allBelowMean = true
      for (let j = 0; j < 10; j++) {
        const zj = calculateZScore(values[i - j], mean, sd)
        if (zj <= 0) allAboveMean = false
        if (zj >= 0) allBelowMean = false
      }
      if (allAboveMean || allBelowMean) {
        violations.push({
          ruleCode: '10ₓ',
          ruleName: '10-x失控规则',
          type: 2,
          description: '10个连续的质控测定值全部落在均值的同一侧',
          formula: '连续10个值同时 > μ 或 < μ'
        })
      }
    }

    let status = 'in_control'
    if (violations.length > 0) {
      const hasReject = violations.some(v => v.type === 2)
      status = hasReject ? 'out_control' : 'warning'
    }

    results.push({
      ...data[i],
      zScore: z,
      violatedRules: violations,
      status: status
    })
  }

  return results
}

const generateMockData = () => {
  const currentSample = qcSampleList.value.find(s => s.id === searchForm.sampleId) || qcSampleList.value[0]
  const mean = currentSample.mean
  const sd = currentSample.sd
  const unit = currentSample.unit
  const count = 30
  const data = []

  for (let i = 0; i < count; i++) {
    const random = Math.random()
    let measuredValue

    if (i === 8) {
      measuredValue = mean + 3.2 * sd
    } else if (i === 15) {
      measuredValue = mean + 2.5 * sd
    } else if (i === 16) {
      measuredValue = mean + 2.3 * sd
    } else if (i === 22) {
      measuredValue = mean - 2.8 * sd
    } else if (i >= 25 && i <= 28) {
      measuredValue = mean + 1.2 * sd + (Math.random() - 0.5) * 0.2 * sd
    } else {
      measuredValue = mean + (Math.random() - 0.5) * 2 * sd * 0.8
    }

    data.push({
      seqNo: i + 1,
      batchNo: `B${dayjs().subtract(count - i, 'day').format('YYYYMMDD')}${String(i + 1).padStart(3, '0')}`,
      measureDate: dayjs().subtract(count - i, 'day').format('YYYY-MM-DD HH:mm:ss'),
      measuredValue: parseFloat(measuredValue.toFixed(4)),
      unit: unit,
      sampleName: currentSample.sampleName,
      project: searchForm.project,
      methodName: '火焰原子吸收分光光度法',
      operator: ['张三', '李四', '王五'][i % 3],
      mean: mean,
      sd: sd
    })
  }

  return data
}

const fetchData = async () => {
  try {
    const params = {
      project: searchForm.project,
      sampleId: searchForm.sampleId,
      startDate: searchForm.dateRange[0],
      endDate: searchForm.dateRange[1]
    }

    const res = await qualityControlApi.chartData(params)
    if (res.data) {
      chartData.value = res.data
    } else {
      chartData.value = generateMockData()
    }
  } catch (error) {
    chartData.value = generateMockData()
  }

  const values = chartData.value.map(d => d.measuredValue)
  stats.mean = calculateMean(values)
  stats.sd = calculateSD(values, stats.mean)
  stats.total = chartData.value.length

  const currentSample = qcSampleList.value.find(s => s.id === searchForm.sampleId) || qcSampleList.value[0]
  violationData.value = checkWestgardRules(chartData.value, currentSample.mean, currentSample.sd)

  stats.inControl = violationData.value.filter(v => v.status === 'in_control').length
  stats.warning = violationData.value.filter(v => v.status === 'warning').length
  stats.outControl = violationData.value.filter(v => v.status === 'out_control').length

  if (stats.outControl > 0) {
    overallStatus.value = 'out_control'
  } else if (stats.warning > 0) {
    overallStatus.value = 'warning'
  } else {
    overallStatus.value = 'in_control'
  }

  const cusumResult = calculateCusum(values, stats.mean, cusumK.value)
  violationData.value.forEach((v, i) => {
    v.cusum = Math.max(Math.abs(cusumResult.cusumPos[i]), Math.abs(cusumResult.cusumNeg[i]))
    v.suggestion = v.status === 'in_control' 
      ? '数据在控，可正常报告结果。' 
      : v.status === 'warning'
      ? '数据警告，建议检查实验操作、试剂、仪器状态，必要时复测。'
      : '数据失控，请立即查找原因，采取纠正措施，经评估后决定是否复测或报废。'
  })

  renderAllCharts()
}

const renderLeveyChart = () => {
  if (!leveyChartRef.value) return

  if (!leveyChart) {
    leveyChart = echarts.init(leveyChartRef.value)
  }

  const currentSample = qcSampleList.value.find(s => s.id === searchForm.sampleId) || qcSampleList.value[0]
  const mean = currentSample.mean
  const sd = currentSample.sd

  const xData = violationData.value.map((d, i) => `#${i + 1}`)
  const yData = violationData.value.map(d => d.measuredValue)

  const markLines = []
  if (showMeanLine.value) {
    markLines.push({
      yAxis: mean,
      label: { formatter: 'μ', position: 'end' },
      lineStyle: { color: '#303133', type: 'solid', width: 2 }
    })
  }
  if (show1SdLine.value) {
    markLines.push({
      yAxis: mean + sd,
      label: { formatter: '+1SD', position: 'end' },
      lineStyle: { color: '#67C23A', type: 'dashed', width: 1 }
    })
    markLines.push({
      yAxis: mean - sd,
      label: { formatter: '-1SD', position: 'end' },
      lineStyle: { color: '#67C23A', type: 'dashed', width: 1 }
    })
  }
  if (show2SdLine.value) {
    markLines.push({
      yAxis: mean + 2 * sd,
      label: { formatter: '+2SD (警告限)', position: 'end' },
      lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 }
    })
    markLines.push({
      yAxis: mean - 2 * sd,
      label: { formatter: '-2SD (警告限)', position: 'end' },
      lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 }
    })
  }
  if (show3SdLine.value) {
    markLines.push({
      yAxis: mean + 3 * sd,
      label: { formatter: '+3SD (失控限)', position: 'end' },
      lineStyle: { color: '#F56C6C', type: 'dashed', width: 2 }
    })
    markLines.push({
      yAxis: mean - 3 * sd,
      label: { formatter: '-3SD (失控限)', position: 'end' },
      lineStyle: { color: '#F56C6C', type: 'dashed', width: 2 }
    })
  }

  const scatterData = []
  if (showViolationMarks.value) {
    violationData.value.forEach((d, i) => {
      if (d.status === 'warning') {
        scatterData.push({
          name: d.violatedRules.map(r => r.ruleCode).join(','),
          value: [xData[i], d.measuredValue],
          itemStyle: { color: '#E6A23C' },
          symbolSize: 12
        })
      } else if (d.status === 'out_control') {
        scatterData.push({
          name: d.violatedRules.map(r => r.ruleCode).join(','),
          value: [xData[i], d.measuredValue],
          itemStyle: { color: '#F56C6C' },
          symbolSize: 14
        })
      }
    })
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const idx = params[0].dataIndex
        const d = violationData.value[idx]
        let html = `<div style="font-weight: bold; margin-bottom: 8px">${d.batchNo}</div>`
        html += `<div>序号: ${d.seqNo}</div>`
        html += `<div>测定值: ${d.measuredValue} ${d.unit}</div>`
        html += `<div>Z分数: ${d.zScore.toFixed(4)}</div>`
        if (d.violatedRules.length > 0) {
          html += `<div style="margin-top: 8px; color: ${d.status === 'out_control' ? '#F56C6C' : '#E6A23C'}">违反规则: ${d.violatedRules.map(r => r.ruleCode).join(', ')}</div>`
        } else {
          html += `<div style="margin-top: 8px; color: #67C23A">在控</div>`
        }
        return html
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: { rotate: 45, fontSize: 10 },
      name: '测定顺序'
    },
    yAxis: {
      type: 'value',
      name: `测定值 (${currentSample.unit})`,
      scale: true
    },
    series: [
      {
        name: '测定值',
        type: 'line',
        data: yData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
        markLine: {
          silent: true,
          symbol: 'none',
          data: markLines
        }
      },
      {
        name: '违规点',
        type: 'scatter',
        data: scatterData,
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          fontSize: 10,
          color: '#F56C6C'
        },
        zlevel: 10
      }
    ]
  }

  leveyChart.setOption(option, true)
}

const renderZscoreChart = () => {
  if (!zscoreChartRef.value) return

  if (!zscoreChart) {
    zscoreChart = echarts.init(zscoreChartRef.value)
  }

  const xData = violationData.value.map((d, i) => `#${i + 1}`)
  const yData = violationData.value.map(d => d.zScore)

  const markLines = []
  if (showZMeanLine.value) {
    markLines.push({
      yAxis: 0,
      label: { formatter: '0', position: 'end' },
      lineStyle: { color: '#303133', type: 'solid', width: 2 }
    })
  }
  if (showZ1SdLine.value) {
    markLines.push({
      yAxis: 1,
      label: { formatter: '+1', position: 'end' },
      lineStyle: { color: '#67C23A', type: 'dashed', width: 1 }
    })
    markLines.push({
      yAxis: -1,
      label: { formatter: '-1', position: 'end' },
      lineStyle: { color: '#67C23A', type: 'dashed', width: 1 }
    })
  }
  if (showZ2SdLine.value) {
    markLines.push({
      yAxis: 2,
      label: { formatter: '+2 (警告限)', position: 'end' },
      lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 }
    })
    markLines.push({
      yAxis: -2,
      label: { formatter: '-2 (警告限)', position: 'end' },
      lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 }
    })
  }
  if (showZ3SdLine.value) {
    markLines.push({
      yAxis: 3,
      label: { formatter: '+3 (失控限)', position: 'end' },
      lineStyle: { color: '#F56C6C', type: 'dashed', width: 2 }
    })
    markLines.push({
      yAxis: -3,
      label: { formatter: '-3 (失控限)', position: 'end' },
      lineStyle: { color: '#F56C6C', type: 'dashed', width: 2 }
    })
  }

  const scatterData = []
  if (showZViolationMarks.value) {
    violationData.value.forEach((d, i) => {
      if (d.status === 'warning') {
        scatterData.push({
          name: d.violatedRules.map(r => r.ruleCode).join(','),
          value: [xData[i], d.zScore],
          itemStyle: { color: '#E6A23C' },
          symbolSize: 12
        })
      } else if (d.status === 'out_control') {
        scatterData.push({
          name: d.violatedRules.map(r => r.ruleCode).join(','),
          value: [xData[i], d.zScore],
          itemStyle: { color: '#F56C6C' },
          symbolSize: 14
        })
      }
    })
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const idx = params[0].dataIndex
        const d = violationData.value[idx]
        let html = `<div style="font-weight: bold; margin-bottom: 8px">${d.batchNo}</div>`
        html += `<div>序号: ${d.seqNo}</div>`
        html += `<div>Z分数: ${d.zScore.toFixed(4)}</div>`
        if (d.violatedRules.length > 0) {
          html += `<div style="margin-top: 8px; color: ${d.status === 'out_control' ? '#F56C6C' : '#E6A23C'}">违反规则: ${d.violatedRules.map(r => r.ruleCode).join(', ')}</div>`
        } else {
          html += `<div style="margin-top: 8px; color: #67C23A">在控</div>`
        }
        return html
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: { rotate: 45, fontSize: 10 },
      name: '测定顺序'
    },
    yAxis: {
      type: 'value',
      name: 'Z分数',
      scale: true
    },
    series: [
      {
        name: 'Z分数',
        type: 'line',
        data: yData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
        markLine: {
          silent: true,
          symbol: 'none',
          data: markLines
        }
      },
      {
        name: '违规点',
        type: 'scatter',
        data: scatterData,
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          fontSize: 10,
          color: '#F56C6C'
        },
        zlevel: 10
      }
    ]
  }

  zscoreChart.setOption(option, true)
}

const renderCusumChart = () => {
  if (!cusumChartRef.value) return

  if (!cusumChart) {
    cusumChart = echarts.init(cusumChartRef.value)
  }

  const values = chartData.value.map(d => d.measuredValue)
  const { cusumPos, cusumNeg } = calculateCusum(values, stats.mean, cusumK.value)

  const xData = violationData.value.map((d, i) => `#${i + 1}`)
  const hLimit = cusumH.value * stats.sd

  const scatterData = []
  if (showCusumViolation.value) {
    cusumPos.forEach((val, i) => {
      if (val > hLimit || Math.abs(cusumNeg[i]) > hLimit) {
        const maxVal = Math.max(val, Math.abs(cusumNeg[i]))
        scatterData.push({
          name: '失控',
          value: [xData[i], maxVal === val ? val : cusumNeg[i]],
          itemStyle: { color: '#F56C6C' },
          symbolSize: 12
        })
      }
    })
  }

  const markLines = []
  if (showCusumLimit.value) {
    markLines.push({
      yAxis: hLimit,
      label: { formatter: `+H(${cusumH.value}SD)`, position: 'end' },
      lineStyle: { color: '#F56C6C', type: 'dashed', width: 2 }
    })
    markLines.push({
      yAxis: -hLimit,
      label: { formatter: `-H(-${cusumH.value}SD)`, position: 'end' },
      lineStyle: { color: '#F56C6C', type: 'dashed', width: 2 }
    })
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const idx = params[0].dataIndex
        const d = violationData.value[idx]
        let html = `<div style="font-weight: bold; margin-bottom: 8px">${d.batchNo}</div>`
        html += `<div>序号: ${d.seqNo}</div>`
        html += `<div>Cusum (+): ${cusumPos[idx].toFixed(4)}</div>`
        html += `<div>Cusum (-): ${cusumNeg[idx].toFixed(4)}</div>`
        if (cusumPos[idx] > hLimit || Math.abs(cusumNeg[idx]) > hLimit) {
          html += `<div style="margin-top: 8px; color: #F56C6C">失控: 超出控制限</div>`
        }
        return html
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    legend: {
      data: ['Cusum (+)', 'Cusum (-)'],
      top: 10
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: { rotate: 45, fontSize: 10 },
      name: '测定顺序'
    },
    yAxis: {
      type: 'value',
      name: 'Cusum值',
      scale: true
    },
    series: [
      {
        name: 'Cusum (+)',
        type: 'line',
        data: cusumPos,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { color: '#67C23A', width: 2 },
        itemStyle: { color: '#67C23A' },
        markLine: {
          silent: true,
          symbol: 'none',
          data: markLines
        }
      },
      {
        name: 'Cusum (-)',
        type: 'line',
        data: cusumNeg,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { color: '#E6A23C', width: 2 },
        itemStyle: { color: '#E6A23C' }
      },
      {
        name: '违规点',
        type: 'scatter',
        data: scatterData,
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          fontSize: 10,
          color: '#F56C6C'
        },
        zlevel: 10
      }
    ]
  }

  cusumChart.setOption(option, true)
}

const renderAllCharts = () => {
  nextTick(() => {
    renderLeveyChart()
    renderZscoreChart()
    renderCusumChart()
  })
}

const handleChartTypeChange = () => {
  nextTick(() => {
    if (activeChartType.value === 'levey') {
      renderLeveyChart()
    } else if (activeChartType.value === 'zscore') {
      renderZscoreChart()
    } else {
      renderCusumChart()
    }
  })
}

const handleSearch = () => {
  fetchData()
}

const handleReset = () => {
  searchForm.project = 'Cu'
  searchForm.sampleId = 1
  searchForm.dateRange = [dayjs().subtract(30, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
  fetchData()
}

const handleExport = () => {
  ElMessage.success('导出功能开发中')
}

const handleViewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handleResize = () => {
  leveyChart?.resize()
  zscoreChart?.resize()
  cusumChart?.resize()
}

watch([showMeanLine, show1SdLine, show2SdLine, show3SdLine, showViolationMarks], () => {
  renderLeveyChart()
})

watch([showZMeanLine, showZ1SdLine, showZ2SdLine, showZ3SdLine, showZViolationMarks], () => {
  renderZscoreChart()
})

watch([showCusumLimit, showCusumViolation], () => {
  renderCusumChart()
})

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  leveyChart?.dispose()
  zscoreChart?.dispose()
  cusumChart?.dispose()
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

  .search-form {
    margin: 0;
  }

  .stats-row {
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 12px;

        .stat-icon {
          width: 44px;
          height: 44px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 22px;
          color: #fff;
        }

        .stat-info {
          .stat-label {
            font-size: 13px;
            color: #606266;
            margin-bottom: 2px;
          }

          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #303133;
          }
        }
      }

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
      }

      &.in-control .stat-icon {
        background: linear-gradient(135deg, #4facfe, #00f2fe);
      }

      &.warning .stat-icon {
        background: linear-gradient(135deg, #f093fb, #f5576c);
      }

      &.out-control .stat-icon {
        background: linear-gradient(135deg, #fa709a, #fee140);
      }

      &.mean .stat-icon {
        background: linear-gradient(135deg, #43e97b, #38f9d7);
      }

      &.sd .stat-icon {
        background: linear-gradient(135deg, #fa709a, #fee140);
      }
    }
  }

  .chart-toolbar {
    display: flex;
    gap: 20px;
    align-items: center;
    margin-bottom: 16px;
    flex-wrap: wrap;
  }

  .chart-container {
    width: 100%;
    height: 450px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .violation-tags {
    display: flex;
    flex-wrap: wrap;
  }
}
</style>
