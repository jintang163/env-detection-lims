<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">能力验证与实验室间比对管理</div>
      <div class="page-desc">管理能力验证计划和实验室间比对，包括报名、样品接收、结果上报和统计分析</div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总计划数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card ongoing" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">进行中</div>
              <div class="stat-value">{{ stats.ongoing || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card completed" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已完成</div>
              <div class="stat-value">{{ stats.completed || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card pending" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待上报</div>
              <div class="stat-value">{{ stats.pending || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="能力验证计划" name="PT">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索计划编号、名称、组织者..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="searchStatus" placeholder="计划状态" clearable style="width: 140px">
              <el-option label="草稿" :value="0" />
              <el-option label="报名中" :value="1" />
              <el-option label="待检测" :value="2" />
              <el-option label="已上报" :value="3" />
              <el-option label="已评价" :value="4" />
              <el-option label="已完成" :value="5" />
              <el-option label="已取消" :value="6" />
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
            <el-table-column prop="ptNo" label="计划编号" width="160" />
            <el-table-column prop="ptName" label="计划名称" width="200" show-overflow-tooltip />
            <el-table-column prop="organizer" label="组织者" width="150" show-overflow-tooltip />
            <el-table-column prop="ptType" label="类型" width="120">
              <template #default="{ row }">
                <el-tag :type="row.ptType === 'PT' ? 'primary' : 'success'" effect="light" size="small">
                  {{ getPtTypeText(row.ptType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="projectName" label="检测项目" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="resultDeadline" label="截止日期" width="160">
              <template #default="{ row }">
                {{ formatDate(row.resultDeadline) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="320" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button
                  v-if="row.status === 0 || row.status === 1"
                  type="success"
                  link
                  @click="handleRegister(row)"
                >
                  <el-icon><UserFilled /></el-icon>
                  报名
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  type="warning"
                  link
                  @click="handleReceiveSample(row)"
                >
                  <el-icon><Box /></el-icon>
                  样品接收
                </el-button>
                <el-button
                  v-if="row.status === 2"
                  type="info"
                  link
                  @click="handleReportResult(row)"
                >
                  <el-icon><Upload /></el-icon>
                  结果上报
                </el-button>
                <el-button
                  v-if="row.status === 4 || row.status === 5"
                  type="success"
                  link
                  @click="handleAnalysis(row)"
                >
                  <el-icon><TrendCharts /></el-icon>
                  分析
                </el-button>
                <el-button type="danger" link @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="实验室间比对" name="ILC">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索计划编号、名称、组织者..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="searchStatus" placeholder="计划状态" clearable style="width: 140px">
              <el-option label="草稿" :value="0" />
              <el-option label="报名中" :value="1" />
              <el-option label="待检测" :value="2" />
              <el-option label="已上报" :value="3" />
              <el-option label="已评价" :value="4" />
              <el-option label="已完成" :value="5" />
              <el-option label="已取消" :value="6" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增比对
            </el-button>
            <el-button @click="fetchList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="ilcTableData"
            v-loading="ilcLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="ptNo" label="计划编号" width="160" />
            <el-table-column prop="ptName" label="计划名称" width="200" show-overflow-tooltip />
            <el-table-column prop="organizer" label="组织者" width="150" show-overflow-tooltip />
            <el-table-column prop="ptType" label="类型" width="120">
              <template #default="{ row }">
                <el-tag :type="row.ptType === 'PT' ? 'primary' : 'success'" effect="light" size="small">
                  {{ getPtTypeText(row.ptType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="projectName" label="检测项目" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="resultDeadline" label="截止日期" width="160">
              <template #default="{ row }">
                {{ formatDate(row.resultDeadline) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="320" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button
                  v-if="row.status === 0 || row.status === 1"
                  type="success"
                  link
                  @click="handleRegister(row)"
                >
                  <el-icon><UserFilled /></el-icon>
                  报名
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  type="warning"
                  link
                  @click="handleReceiveSample(row)"
                >
                  <el-icon><Box /></el-icon>
                  样品接收
                </el-button>
                <el-button
                  v-if="row.status === 2"
                  type="info"
                  link
                  @click="handleReportResult(row)"
                >
                  <el-icon><Upload /></el-icon>
                  结果上报
                </el-button>
                <el-button
                  v-if="row.status === 4 || row.status === 5"
                  type="success"
                  link
                  @click="handleAnalysis(row)"
                >
                  <el-icon><TrendCharts /></el-icon>
                  分析
                </el-button>
                <el-button type="danger" link @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

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
            <el-form-item label="计划编号" prop="ptNo">
              <el-input v-model="planForm.ptNo" placeholder="自动生成或手动输入" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划类型" prop="ptType">
              <el-select v-model="planForm.ptType" placeholder="请选择计划类型" :disabled="isView" style="width: 100%">
                <el-option label="能力验证(PT)" value="PT" />
                <el-option label="实验室间比对(ILC)" value="ILC" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计划名称" prop="ptName">
              <el-input v-model="planForm.ptName" placeholder="请输入计划名称" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组织者" prop="organizer">
              <el-input v-model="planForm.organizer" placeholder="请输入组织者" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="planForm.projectName" placeholder="请输入检测项目名称" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目编码" prop="itemCode">
              <el-input v-model="planForm.itemCode" placeholder="请输入项目编码" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="基质" prop="matrix">
              <el-input v-model="planForm.matrix" placeholder="请输入基质类型" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名费" prop="registrationFee">
              <el-input-number
                v-model="planForm.registrationFee"
                :precision="2"
                :step="100"
                :min="0"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="报名截止日期" prop="registerDeadline">
              <el-date-picker
                v-model="planForm.registerDeadline"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择日期时间"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="样品发放日期" prop="sampleDispatchDate">
              <el-date-picker
                v-model="planForm.sampleDispatchDate"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择日期时间"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="结果上报截止日期" prop="resultDeadline">
              <el-date-picker
                v-model="planForm.resultDeadline"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择日期时间"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结果反馈日期" prop="feedbackDate">
              <el-date-picker
                v-model="planForm.feedbackDate"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择日期时间"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="planForm.contactPerson" placeholder="请输入联系人" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="planForm.contactPhone" placeholder="请输入联系电话" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计划状态" prop="status">
              <el-select v-model="planForm.status" placeholder="请选择状态" :disabled="isView" style="width: 100%">
                <el-option label="草稿" :value="0" />
                <el-option label="报名中" :value="1" />
                <el-option label="待检测" :value="2" />
                <el-option label="已上报" :value="3" />
                <el-option label="已评价" :value="4" />
                <el-option label="已完成" :value="5" />
                <el-option label="已取消" :value="6" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="计划描述" prop="description">
          <el-input
            v-model="planForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入计划描述"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
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

    <el-dialog v-model="resultDialogVisible" :title="resultDialogTitle" width="700px" top="5vh">
      <el-form
        :model="resultForm"
        :rules="resultFormRules"
        ref="resultFormRef"
        label-width="120px"
        v-loading="resultLoading"
      >
        <el-alert
          v-if="currentPlan"
          :title="`计划: ${currentPlan.ptName} - ${currentPlan.projectName}`"
          type="info"
          show-icon
          style="margin-bottom: 16px"
        />

        <el-table
          v-if="currentPlan?.samples && currentPlan.samples.length > 0"
          :data="currentPlan.samples"
          border
          size="small"
          style="width: 100%; margin-bottom: 16px"
        >
          <el-table-column label="选择" width="60">
            <template #default="{ row }">
              <el-radio v-model="resultForm.ptSampleId" :label="row.id" />
            </template>
          </el-table-column>
          <el-table-column prop="sampleCode" label="样品编号" width="120" />
          <el-table-column prop="sampleName" label="样品名称" width="150" />
          <el-table-column prop="matrix" label="基质" width="100" />
          <el-table-column prop="expectedValue" label="指定值" width="120">
            <template #default="{ row }">
              {{ row.expectedValue }} {{ row.unit }}
            </template>
          </el-table-column>
          <el-table-column prop="uncertainty" label="不确定度" width="120" />
        </el-table>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检测值" prop="detectedValue">
              <el-input-number
                v-model="resultForm.detectedValue"
                :precision="4"
                :step="0.001"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="resultForm.unit" placeholder="如: mg/L" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检测日期" prop="measureDate">
              <el-date-picker
                v-model="resultForm.measureDate"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择日期时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测人" prop="operator">
              <el-input v-model="resultForm.operator" placeholder="请输入检测人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检测仪器" prop="instrument">
              <el-input v-model="resultForm.instrument" placeholder="请输入检测仪器" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测方法" prop="methodName">
              <el-input v-model="resultForm.methodName" placeholder="请输入检测方法" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="不确定度" prop="uncertainty">
              <el-input-number
                v-model="resultForm.uncertainty"
                :precision="4"
                :step="0.001"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="resultForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="resultDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveResult">
          <el-icon><Check /></el-icon>
          保存
        </el-button>
        <el-button type="success" @click="handleReportResultSubmit">
          <el-icon><Upload /></el-icon>
          上报
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="analysisDialogVisible" title="能力验证结果分析" width="1000px" top="5vh">
      <el-alert
        v-if="currentPlan"
        :title="`计划: ${currentPlan.ptName} - ${currentPlan.projectName}`"
        type="info"
        show-icon
        style="margin-bottom: 16px"
      />

      <el-tabs v-model="analysisTab">
        <el-tab-pane label="Youden图" name="youden">
          <div class="chart-toolbar">
            <el-checkbox v-model="show2SdEllipse">显示±2SD椭圆</el-checkbox>
            <el-checkbox v-model="show3SdEllipse">显示±3SD椭圆</el-checkbox>
            <el-checkbox v-model="showEvaluationMarks">显示评价标记</el-checkbox>
          </div>
          <div ref="youdenChartRef" class="chart-container"></div>
        </el-tab-pane>

        <el-tab-pane label="Z比分数分析" name="zscore">
          <div class="chart-toolbar">
            <el-button @click="fetchZScoreData">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
          <el-table
            :data="zScoreData"
            v-loading="zScoreLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="sampleCode" label="样品编号" width="120" />
            <el-table-column prop="sampleName" label="样品名称" width="150" />
            <el-table-column prop="detectedValue" label="检测值" width="120">
              <template #default="{ row }">
                {{ row.detectedValue }} {{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column prop="assignedValue" label="指定值" width="120">
              <template #default="{ row }">
                {{ row.assignedValue }} {{ row.unit }}
              </template>
            </el-table-column>
            <el-table-column prop="zScore" label="Z分数" width="120">
              <template #default="{ row }">
                <span :style="{ color: getZScoreColor(row.zScore) }">
                  {{ row.zScore !== null ? Number(row.zScore).toFixed(4) : '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="zScoreType" label="Z分数类型" width="120">
              <template #default="{ row }">
                <el-tag type="info" effect="light" size="small">
                  {{ row.zScoreType === 'LAB' ? '实验室间' : '实验室内' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="evaluation" label="评价" width="120">
              <template #default="{ row }">
                <el-tag :type="getEvaluationTag(row.evaluation)" effect="light" size="small">
                  {{ getEvaluationText(row.evaluation) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="evaluationText" label="评价说明" min-width="200" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { proficiencyTestApi } from '@/api/detection'
import dayjs from 'dayjs'

const youdenChartRef = ref(null)
let youdenChart = null

const loading = ref(false)
const ilcLoading = ref(false)
const detailLoading = ref(false)
const resultLoading = ref(false)
const zScoreLoading = ref(false)
const searchKeyword = ref('')
const searchStatus = ref(null)
const tableData = ref([])
const ilcTableData = ref([])
const dialogVisible = ref(false)
const resultDialogVisible = ref(false)
const analysisDialogVisible = ref(false)
const dialogTitle = ref('')
const resultDialogTitle = ref('')
const isView = ref(false)
const planFormRef = ref(null)
const resultFormRef = ref(null)
const currentPlan = ref(null)
const activeTab = ref('PT')
const analysisTab = ref('youden')

const show2SdEllipse = ref(true)
const show3SdEllipse = ref(true)
const showEvaluationMarks = ref(true)

const zScoreData = ref([])
const youdenData = ref([])

const stats = reactive({
  total: 0,
  ongoing: 0,
  completed: 0,
  pending: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const planForm = reactive({
  id: null,
  ptNo: '',
  ptName: '',
  organizer: '',
  ptType: 'PT',
  projectName: '',
  itemCode: '',
  matrix: '',
  registerDeadline: '',
  sampleDispatchDate: '',
  resultDeadline: '',
  feedbackDate: '',
  status: 0,
  registrationFee: 0,
  contactPerson: '',
  contactPhone: '',
  description: '',
  remark: ''
})

const resultForm = reactive({
  id: null,
  ptPlanId: null,
  ptSampleId: null,
  detectedValue: null,
  unit: '',
  measureDate: '',
  operator: '',
  instrument: '',
  methodName: '',
  uncertainty: null,
  remark: ''
})

const formRules = {
  ptName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  organizer: [{ required: true, message: '请输入组织者', trigger: 'blur' }],
  ptType: [{ required: true, message: '请选择计划类型', trigger: 'change' }],
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  registerDeadline: [{ required: true, message: '请选择报名截止日期', trigger: 'change' }],
  resultDeadline: [{ required: true, message: '请选择结果上报截止日期', trigger: 'change' }]
}

const resultFormRules = {
  ptSampleId: [{ required: true, message: '请选择样品', trigger: 'change' }],
  detectedValue: [{ required: true, message: '请输入检测值', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  measureDate: [{ required: true, message: '请选择检测日期', trigger: 'change' }],
  operator: [{ required: true, message: '请输入检测人', trigger: 'blur' }]
}

const getPtTypeText = (type) => {
  const map = { PT: '能力验证', ILC: '实验室间比对' }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = {
    0: '草稿',
    1: '报名中',
    2: '待检测',
    3: '已上报',
    4: '已评价',
    5: '已完成',
    6: '已取消'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    0: 'info',
    1: 'primary',
    2: 'warning',
    3: 'success',
    4: 'purple',
    5: 'success',
    6: 'info'
  }
  return map[status] || 'info'
}

const getEvaluationText = (evaluation) => {
  const map = {
    satisfactory: '满意',
    questionable: '可疑',
    unsatisfactory: '不满意'
  }
  return map[evaluation] || evaluation
}

const getEvaluationTag = (evaluation) => {
  const map = {
    satisfactory: 'success',
    questionable: 'warning',
    unsatisfactory: 'danger'
  }
  return map[evaluation] || 'info'
}

const getZScoreColor = (zScore) => {
  if (zScore === null || zScore === undefined) return '#909399'
  const abs = Math.abs(Number(zScore))
  if (abs <= 2) return '#67C23A'
  if (abs <= 3) return '#E6A23C'
  return '#F56C6C'
}

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

const fetchStats = async () => {
  try {
    const res = await proficiencyTestApi.getStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.ongoing = res.data.ongoing || 0
      stats.completed = res.data.completed || 0
      stats.pending = res.data.pending || 0
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

const fetchList = async () => {
  loading.value = true
  ilcLoading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      status: searchStatus.value,
      ptType: activeTab.value
    }
    const res = await proficiencyTestApi.planPage(params)
    if (res.data?.records) {
      if (activeTab.value === 'PT') {
        tableData.value = res.data.records
      } else {
        ilcTableData.value = res.data.records
      }
      pagination.total = res.data.total
    } else {
      if (activeTab.value === 'PT') {
        tableData.value = []
      } else {
        ilcTableData.value = []
      }
      pagination.total = 0
    }
    await fetchStats()
  } catch (error) {
    console.error('获取计划列表失败', error)
    ElMessage.error('获取计划列表失败')
    if (activeTab.value === 'PT') {
      tableData.value = []
    } else {
      ilcTableData.value = []
    }
    pagination.total = 0
  } finally {
    loading.value = false
    ilcLoading.value = false
  }
}

const handleTabChange = () => {
  pagination.pageNum = 1
  fetchList()
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = activeTab.value === 'PT' ? '新增能力验证计划' : '新增实验室间比对'
  Object.assign(planForm, {
    id: null,
    ptNo: (activeTab.value === 'PT' ? 'PT-' : 'ILC-') + dayjs().format('YYYYMMDDHHmmss'),
    ptName: '',
    organizer: '',
    ptType: activeTab.value,
    projectName: '',
    itemCode: '',
    matrix: '',
    registerDeadline: '',
    sampleDispatchDate: '',
    resultDeadline: '',
    feedbackDate: '',
    status: 0,
    registrationFee: 0,
    contactPerson: '',
    contactPhone: '',
    description: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑' + getPtTypeText(row.ptType) + '计划'
  Object.assign(planForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除"${row.ptName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await proficiencyTestApi.deletePlan(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('删除失败', error)
    }
  }
}

const handleRegister = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要报名参加"${row.ptName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await proficiencyTestApi.register(row.id)
    ElMessage.success('报名成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('报名失败', error)
    }
  }
}

const handleReceiveSample = async (row) => {
  try {
    await ElMessageBox.confirm(`确定已接收"${row.ptName}"的样品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await proficiencyTestApi.receiveSample(row.id)
    ElMessage.success('样品已接收')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('样品接收失败', error)
    }
  }
}

const handleReportResult = async (row) => {
  currentPlan.value = row
  resultDialogTitle.value = '结果上报 - ' + row.ptName
  Object.assign(resultForm, {
    id: null,
    ptPlanId: row.id,
    ptSampleId: row.samples && row.samples.length > 0 ? row.samples[0].id : null,
    detectedValue: null,
    unit: row.samples && row.samples.length > 0 ? row.samples[0].unit : '',
    measureDate: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    operator: '',
    instrument: '',
    methodName: '',
    uncertainty: null,
    remark: ''
  })
  resultDialogVisible.value = true
}

const handleSaveResult = async () => {
  try {
    await resultFormRef.value.validate()
    await proficiencyTestApi.saveResult(resultForm)
    ElMessage.success('保存成功')
    resultDialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('保存结果失败', error)
    }
  }
}

const handleReportResultSubmit = async () => {
  try {
    await resultFormRef.value.validate()
    await proficiencyTestApi.reportResult(resultForm)
    ElMessage.success('上报成功')
    resultDialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('上报结果失败', error)
    }
  }
}

const handleAnalysis = async (row) => {
  currentPlan.value = row
  analysisDialogVisible.value = true
  analysisTab.value = 'youden'
  nextTick(() => {
    fetchYoudenData()
    fetchZScoreData()
  })
}

const fetchYoudenData = async () => {
  try {
    const res = await proficiencyTestApi.getYoudenData(currentPlan.value.id)
    if (res.data) {
      youdenData.value = res.data
      renderYoudenChart()
    }
  } catch (error) {
    console.error('获取Youden图数据失败', error)
    youdenData.value = []
  }
}

const fetchZScoreData = async () => {
  zScoreLoading.value = true
  try {
    const res = await proficiencyTestApi.getZScore(currentPlan.value.id)
    if (res.data) {
      zScoreData.value = res.data
    } else {
      zScoreData.value = []
    }
  } catch (error) {
    console.error('获取Z分数数据失败', error)
    ElMessage.error('获取Z分数数据失败')
    zScoreData.value = []
  } finally {
    zScoreLoading.value = false
  }
}

const renderYoudenChart = () => {
  if (!youdenChartRef.value) return

  if (!youdenChart) {
    youdenChart = echarts.init(youdenChartRef.value)
  }

  if (youdenData.value.length === 0) {
    youdenChart.setOption({
      title: { text: '暂无数据', left: 'center', top: 'center' }
    }, true)
    return
  }

  const xData = youdenData.value.map(d => Number(d.xValue))
  const yData = youdenData.value.map(d => Number(d.yValue))

  const xMean = xData.reduce((a, b) => a + b, 0) / xData.length
  const yMean = yData.reduce((a, b) => a + b, 0) / yData.length
  const xSd = Math.sqrt(xData.reduce((sum, val) => sum + Math.pow(val - xMean, 2), 0) / (xData.length - 1))
  const ySd = Math.sqrt(yData.reduce((sum, val) => sum + Math.pow(val - yMean, 2), 0) / (yData.length - 1))

  const generateEllipse = (cx, cy, rx, ry, points = 100) => {
    const data = []
    for (let i = 0; i <= points; i++) {
      const angle = (2 * Math.PI * i) / points
      data.push([cx + rx * Math.cos(angle), cy + ry * Math.sin(angle)])
    }
    return data
  }

  const series = []

  if (show3SdEllipse.value) {
    series.push({
      name: '±3SD',
      type: 'line',
      data: generateEllipse(xMean, yMean, 3 * xSd, 3 * ySd),
      lineStyle: { color: '#F56C6C', type: 'dashed', width: 1 },
      symbol: 'none',
      areaStyle: {
        color: 'rgba(245, 108, 108, 0.1)'
      },
      silent: true
    })
  }

  if (show2SdEllipse.value) {
    series.push({
      name: '±2SD',
      type: 'line',
      data: generateEllipse(xMean, yMean, 2 * xSd, 2 * ySd),
      lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 },
      symbol: 'none',
      areaStyle: {
        color: 'rgba(230, 162, 60, 0.1)'
      },
      silent: true
    })
  }

  const scatterData = youdenData.value.map(d => ({
    name: d.laboratoryCode,
    value: [Number(d.xValue), Number(d.yValue)],
    evaluation: d.evaluation,
    itemStyle: {
      color: d.evaluation === 'satisfactory' ? '#67C23A' :
             d.evaluation === 'questionable' ? '#E6A23C' : '#F56C6C'
    }
  }))

  series.push({
    name: '检测结果',
    type: 'scatter',
    data: scatterData,
    symbolSize: 10,
    label: {
      show: showEvaluationMarks.value,
      position: 'top',
      formatter: (params) => params.name,
      fontSize: 10
    }
  })

  series.push({
    name: '中心线',
    type: 'line',
    markLine: {
      silent: true,
      symbol: 'none',
      data: [
        { xAxis: xMean, lineStyle: { color: '#909399', type: 'solid' } },
        { yAxis: yMean, lineStyle: { color: '#909399', type: 'solid' } }
      ]
    }
  })

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        if (params.seriesName === '检测结果') {
          return `
            <div style="font-weight: bold; margin-bottom: 4px">${params.name}</div>
            <div>样品A结果: ${params.value[0]}</div>
            <div>样品B结果: ${params.value[1]}</div>
            <div style="margin-top: 4px">评价: <span style="color: ${params.color}">${getEvaluationText(params.data.evaluation)}</span></div>
          `
        }
        return params.seriesName
      }
    },
    legend: {
      data: ['检测结果', '±2SD', '±3SD'],
      top: 10
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '样品A结果',
      scale: true
    },
    yAxis: {
      type: 'value',
      name: '样品B结果',
      scale: true
    },
    series: series
  }

  youdenChart.setOption(option, true)
}

const handleSubmit = async () => {
  try {
    await planFormRef.value.validate()
    if (planForm.id) {
      await proficiencyTestApi.updatePlan(planForm)
      ElMessage.success('更新成功')
    } else {
      await proficiencyTestApi.savePlan(planForm)
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

const handleResize = () => {
  youdenChart?.resize()
}

onMounted(() => {
  fetchList()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  youdenChart?.dispose()
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

      &.ongoing .stat-icon {
        background: linear-gradient(135deg, #4facfe, #00f2fe);
      }

      &.completed .stat-icon {
        background: linear-gradient(135deg, #43e97b, #38f9d7);
      }

      &.pending .stat-icon {
        background: linear-gradient(135deg, #fa709a, #fee140);
      }
    }
  }

  .toolbar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
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

  :deep(.el-tag--purple) {
    background-color: #f0e6ff;
    border-color: #d9b3ff;
    color: #722ed1;
  }
}
</style>
