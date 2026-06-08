<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">设施维护管理</div>
        <div class="page-desc">管理设施的维护记录和维护计划</div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待维护计划</div>
              <div class="stat-value">{{ stats.pendingPlans || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card valid" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日维护</div>
              <div class="stat-value">{{ stats.todayMaintenance || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已完成维护</div>
              <div class="stat-value">{{ stats.completed || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card expired" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">逾期维护</div>
              <div class="stat-value">{{ stats.overdue || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeMainTab" class="main-tabs">
        <el-tab-pane label="维护记录" name="record">
          <div class="toolbar">
            <el-input
              v-model="recordSearchKeyword"
              placeholder="搜索设施编号、名称..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchRecordList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="recordSearchType" placeholder="维护类型" clearable style="width: 180px">
              <el-option label="日常保养" :value="1" />
              <el-option label="定期维护" :value="2" />
              <el-option label="预防性维护" :value="3" />
              <el-option label="故障维修" :value="4" />
            </el-select>
            <el-select v-model="recordSearchResult" placeholder="维护结果" clearable style="width: 140px">
              <el-option label="良好" :value="1" />
              <el-option label="一般" :value="2" />
              <el-option label="需维修" :value="3" />
            </el-select>
            <el-date-picker
              v-model="recordSearchDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 180px"
            />
            <el-button type="primary" @click="handleRecordAdd">
              <el-icon><Plus /></el-icon>
              新增记录
            </el-button>
            <el-button @click="fetchRecordList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="recordTableData"
            v-loading="recordLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="facilityNo" label="设施编号" width="140" />
            <el-table-column prop="facilityName" label="设施名称" width="180" />
            <el-table-column label="维护类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getMaintenanceTypeTag(row.maintenanceType)" effect="light" size="small">
                  {{ getMaintenanceTypeText(row.maintenanceType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="maintenanceDate" label="维护日期" width="120" />
            <el-table-column prop="maintenanceContent" label="维护内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="maintainerName" label="维护人" width="100" />
            <el-table-column label="维护结果" width="100">
              <template #default="{ row }">
                <el-tag :type="getResultTag(row.result)" effect="light" size="small">
                  {{ getResultText(row.result) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="nextMaintenanceDate" label="下次维护日期" width="140" />
            <el-table-column prop="cost" label="费用(元)" width="110">
              <template #default="{ row }">
                {{ row.cost ? row.cost.toFixed(2) : '0.00' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleRecordView(row)">详情</el-button>
                <el-button type="primary" link @click="handleRecordEdit(row)">编辑</el-button>
                <el-button type="danger" link @click="handleRecordDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="recordPagination.pageNum"
            v-model:page-size="recordPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="recordPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchRecordList"
            @current-change="fetchRecordList"
          />
        </el-tab-pane>

        <el-tab-pane label="维护计划" name="plan">
          <div class="toolbar">
            <el-input
              v-model="planSearchKeyword"
              placeholder="搜索设施编号、名称..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchPlanList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="planSearchStatus" placeholder="计划状态" clearable style="width: 140px">
              <el-option label="待执行" :value="0" />
              <el-option label="已完成" :value="1" />
              <el-option label="已过期" :value="2" />
            </el-select>
            <el-date-picker
              v-model="planSearchDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 300px"
            />
            <el-button type="primary" @click="handlePlanAdd">
              <el-icon><Plus /></el-icon>
              新增计划
            </el-button>
            <el-button @click="fetchPlanList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="planTableData"
            v-loading="planLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="facilityNo" label="设施编号" width="140" />
            <el-table-column prop="facilityName" label="设施名称" width="180" />
            <el-table-column prop="planName" label="计划名称" width="160" />
            <el-table-column label="维护类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getMaintenanceTypeTag(row.maintenanceType)" effect="light" size="small">
                  {{ getMaintenanceTypeText(row.maintenanceType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="planDate" label="计划日期" width="120" />
            <el-table-column prop="planContent" label="计划内容" min-width="200" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getPlanStatusTag(row.planStatus)" effect="light" size="small">
                  {{ getPlanStatusText(row.planStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="executorName" label="执行人" width="100" />
            <el-table-column label="剩余天数" width="100">
              <template #default="{ row }">
                <span :style="{ color: getPlanDaysColor(row) }">
                  {{ getPlanDays(row) }}天
                </span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.planStatus === 0" type="success" link @click="handlePlanComplete(row)">完成</el-button>
                <el-button type="primary" link @click="handlePlanView(row)">详情</el-button>
                <el-button v-if="row.planStatus === 0" type="primary" link @click="handlePlanEdit(row)">编辑</el-button>
                <el-button type="danger" link @click="handlePlanDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="planPagination.pageNum"
            v-model:page-size="planPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="planPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchPlanList"
            @current-change="fetchPlanList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="recordDialogVisible" :title="recordDialogTitle" width="800px" top="10vh">
      <el-form
        :model="recordForm"
        :rules="recordFormRules"
        ref="recordFormRef"
        label-width="100px"
        v-loading="recordDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设施" prop="facilityId">
              <el-select v-model="recordForm.facilityId" placeholder="请选择设施" style="width: 100%" :disabled="isRecordView" @change="handleRecordFacilityChange">
                <el-option v-for="item in facilityOptions" :key="item.id" :label="item.facilityName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护类型" prop="maintenanceType">
              <el-select v-model="recordForm.maintenanceType" placeholder="请选择维护类型" style="width: 100%" :disabled="isRecordView">
                <el-option label="日常保养" :value="1" />
                <el-option label="定期维护" :value="2" />
                <el-option label="预防性维护" :value="3" />
                <el-option label="故障维修" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="维护日期" prop="maintenanceDate">
              <el-date-picker
                v-model="recordForm.maintenanceDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isRecordView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维护人" prop="maintainerName">
              <el-input v-model="recordForm.maintainerName" placeholder="请输入维护人" :disabled="isRecordView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维护时长(小时)" prop="maintenanceHours">
              <el-input-number v-model="recordForm.maintenanceHours" :min="0" :precision="1" style="width: 100%" :disabled="isRecordView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="维护内容" prop="maintenanceContent">
          <el-input
            v-model="recordForm.maintenanceContent"
            type="textarea"
            :rows="3"
            placeholder="请输入维护内容"
            :disabled="isRecordView"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="维护结果" prop="result">
              <el-select v-model="recordForm.result" placeholder="请选择维护结果" style="width: 100%" :disabled="isRecordView">
                <el-option label="良好" :value="1" />
                <el-option label="一般" :value="2" />
                <el-option label="需维修" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下次维护日期" prop="nextMaintenanceDate">
              <el-date-picker
                v-model="recordForm.nextMaintenanceDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isRecordView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="费用(元)" prop="cost">
              <el-input-number v-model="recordForm.cost" :min="0" :precision="2" style="width: 100%" :disabled="isRecordView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="recordForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isRecordView"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialogVisible = false">关闭</el-button>
        <el-button v-if="!isRecordView" type="primary" @click="handleRecordSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="planDialogVisible" :title="planDialogTitle" width="800px" top="10vh">
      <el-form
        :model="planForm"
        :rules="planFormRules"
        ref="planFormRef"
        label-width="100px"
        v-loading="planDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设施" prop="facilityId">
              <el-select v-model="planForm.facilityId" placeholder="请选择设施" style="width: 100%" :disabled="isPlanView" @change="handlePlanFacilityChange">
                <el-option v-for="item in facilityOptions" :key="item.id" :label="item.facilityName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="planForm.planName" placeholder="请输入计划名称" :disabled="isPlanView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="维护类型" prop="maintenanceType">
              <el-select v-model="planForm.maintenanceType" placeholder="请选择维护类型" style="width: 100%" :disabled="isPlanView">
                <el-option label="日常保养" :value="1" />
                <el-option label="定期维护" :value="2" />
                <el-option label="预防性维护" :value="3" />
                <el-option label="故障维修" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计划日期" prop="planDate">
              <el-date-picker
                v-model="planForm.planDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isPlanView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="执行人" prop="executorName">
              <el-input v-model="planForm.executorName" placeholder="请输入执行人" :disabled="isPlanView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="计划内容" prop="planContent">
          <el-input
            v-model="planForm.planContent"
            type="textarea"
            :rows="3"
            placeholder="请输入计划内容"
            :disabled="isPlanView"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="周期(天)" prop="cycleDays">
              <el-input-number v-model="planForm.cycleDays" :min="0" style="width: 100%" :disabled="isPlanView" />
            </el-col>
          <el-col :span="8">
            <el-form-item label="预估费用(元)" prop="estimatedCost">
              <el-input-number v-model="planForm.estimatedCost" :min="0" :precision="2" style="width: 100%" :disabled="isPlanView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="planForm.priority" placeholder="请选择优先级" style="width: 100%" :disabled="isPlanView">
                <el-option label="低" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="高" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="planForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isPlanView"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">关闭</el-button>
        <el-button v-if="!isPlanView" type="primary" @click="handlePlanSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="planCompleteDialogVisible" title="完成维护计划" width="700px" top="10vh">
      <div style="margin-bottom: 16px">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="设施名称">{{ currentPlan.facilityName }}</el-descriptions-item>
          <el-descriptions-item label="计划名称">{{ currentPlan.planName }}</el-descriptions-item>
          <el-descriptions-item label="维护类型">{{ getMaintenanceTypeText(currentPlan.maintenanceType) }}</el-descriptions-item>
          <el-descriptions-item label="计划日期">{{ currentPlan.planDate }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-form :model="planCompleteForm" :rules="planCompleteFormRules" ref="planCompleteFormRef" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="维护日期" prop="maintenanceDate">
              <el-date-picker
                v-model="planCompleteForm.maintenanceDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维护人" prop="maintainerName">
              <el-input v-model="planCompleteForm.maintainerName" placeholder="请输入维护人" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维护结果" prop="result">
              <el-select v-model="planCompleteForm.result" placeholder="请选择维护结果" style="width: 100%">
                <el-option label="良好" :value="1" />
                <el-option label="一般" :value="2" />
                <el-option label="需维修" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="维护内容" prop="maintenanceContent">
          <el-input
            v-model="planCompleteForm.maintenanceContent"
            type="textarea"
            :rows="3"
            placeholder="请输入维护内容"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="维护时长(小时)" prop="maintenanceHours">
              <el-input-number v-model="planCompleteForm.maintenanceHours" :min="0" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实际费用(元)" prop="cost">
              <el-input-number v-model="planCompleteForm.cost" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下次维护日期" prop="nextMaintenanceDate">
              <el-date-picker
                v-model="planCompleteForm.nextMaintenanceDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="planCompleteForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planCompleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePlanCompleteSubmit">
          <el-icon><Check /></el-icon>
          确定完成
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar,
  Clock,
  CircleCheck,
  Warning,
  Search,
  Plus,
  Refresh,
  Check
} from '@element-plus/icons-vue'
import { facilityMaintenanceApi, facilityApi } from '@/api/environment'

const activeMainTab = ref('record')
const recordLoading = ref(false)
const planLoading = ref(false)
const recordDetailLoading = ref(false)
const planDetailLoading = ref(false)

const stats = reactive({
  pendingPlans: 0,
  todayMaintenance: 0,
  completed: 0,
  overdue: 0
})

const facilityOptions = ref([])

const recordSearchKeyword = ref('')
const recordSearchType = ref('')
const recordSearchResult = ref('')
const recordSearchDate = ref('')
const recordTableData = ref([])
const recordPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const planSearchKeyword = ref('')
const planSearchStatus = ref('')
const planSearchDateRange = ref([])
const planTableData = ref([])
const planPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const recordDialogVisible = ref(false)
const isRecordView = ref(false)
const recordDialogTitle = ref('')
const recordFormRef = ref(null)
const recordForm = ref({
  id: null,
  facilityId: null,
  facilityNo: '',
  facilityName: '',
  maintenanceType: 1,
  maintenanceDate: '',
  maintainerName: '',
  maintenanceHours: null,
  maintenanceContent: '',
  result: 1,
  nextMaintenanceDate: '',
  cost: null,
  remark: ''
})

const recordFormRules = {
  facilityId: [{ required: true, message: '请选择设施', trigger: 'change' }],
  maintenanceType: [{ required: true, message: '请选择维护类型', trigger: 'change' }],
  maintenanceDate: [{ required: true, message: '请选择维护日期', trigger: 'change' }],
  maintainerName: [{ required: true, message: '请输入维护人', trigger: 'blur' }],
  maintenanceContent: [{ required: true, message: '请输入维护内容', trigger: 'blur' }],
  result: [{ required: true, message: '请选择维护结果', trigger: 'change' }]
}

const planDialogVisible = ref(false)
const isPlanView = ref(false)
const planDialogTitle = ref('')
const planFormRef = ref(null)
const planForm = ref({
  id: null,
  facilityId: null,
  facilityNo: '',
  facilityName: '',
  planName: '',
  maintenanceType: 1,
  planDate: '',
  executorName: '',
  planContent: '',
  cycleDays: null,
  estimatedCost: null,
  priority: 2,
  planStatus: 0,
  remark: ''
})

const planFormRules = {
  facilityId: [{ required: true, message: '请选择设施', trigger: 'change' }],
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  maintenanceType: [{ required: true, message: '请选择维护类型', trigger: 'change' }],
  planDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }],
  planContent: [{ required: true, message: '请输入计划内容', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
}

const planCompleteDialogVisible = ref(false)
const currentPlan = ref({})
const planCompleteFormRef = ref(null)
const planCompleteForm = ref({
  maintenanceDate: '',
  maintainerName: '',
  maintenanceContent: '',
  result: 1,
  maintenanceHours: null,
  cost: null,
  nextMaintenanceDate: '',
  remark: ''
})

const planCompleteFormRules = {
  maintenanceDate: [{ required: true, message: '请选择维护日期', trigger: 'change' }],
  maintainerName: [{ required: true, message: '请输入维护人', trigger: 'blur' }],
  maintenanceContent: [{ required: true, message: '请输入维护内容', trigger: 'blur' }],
  result: [{ required: true, message: '请选择维护结果', trigger: 'change' }]
}

const getMaintenanceTypeTag = (type) => {
  const tags = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger' }
  return tags[type] || 'info'
}

const getMaintenanceTypeText = (type) => {
  const texts = { 1: '日常保养', 2: '定期维护', 3: '预防性维护', 4: '故障维修' }
  return texts[type] || '未知'
}

const getResultTag = (result) => {
  const tags = { 1: 'success', 2: 'warning', 3: 'danger' }
  return tags[result] || 'info'
}

const getResultText = (result) => {
  const texts = { 1: '良好', 2: '一般', 3: '需维修' }
  return texts[result] || '未知'
}

const getPlanStatusTag = (status) => {
  const tags = { 0: 'warning', 1: 'success', 2: 'danger' }
  return tags[status] || 'info'
}

const getPlanStatusText = (status) => {
  const texts = { 0: '待执行', 1: '已完成', 2: '已过期' }
  return texts[status] || '未知'
}

const getPlanDays = (row) => {
  if (!row.planDate) return 0
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  const planDate = new Date(row.planDate)
  planDate.setHours(0, 0, 0, 0)
  const diffDays = Math.ceil((planDate - now) / (1000 * 60 * 60 * 24))
  return diffDays
}

const getPlanDaysColor = (row) => {
  const days = getPlanDays(row)
  if (days < 0) return '#f56c6c'
  if (days <= 3) return '#e6a23c'
  return '#67c23a'
}

const fetchFacilities = async () => {
  try {
    const res = await facilityApi.list()
    if (res.code === 200) {
      facilityOptions.value = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchRecordList = async () => {
  recordLoading.value = true
  try {
    const params = {
      pageNum: recordPagination.pageNum,
      pageSize: recordPagination.pageSize,
      facilityNo: recordSearchKeyword.value,
      facilityName: recordSearchKeyword.value,
      maintenanceType: recordSearchType.value,
      result: recordSearchResult.value,
      maintenanceDate: recordSearchDate.value
    }
    const res = await facilityMaintenanceApi.recordPage(params)
    if (res.code === 200) {
      recordTableData.value = res.data.list
      recordPagination.total = res.data.total
    }
  } finally {
    recordLoading.value = false
  }
}

const fetchPlanList = async () => {
  planLoading.value = true
  try {
    const params = {
      pageNum: planPagination.pageNum,
      pageSize: planPagination.pageSize,
      facilityNo: planSearchKeyword.value,
      facilityName: planSearchKeyword.value,
      planStatus: planSearchStatus.value,
      startDate: planSearchDateRange.value ? planSearchDateRange.value[0] : '',
      endDate: planSearchDateRange.value ? planSearchDateRange.value[1] : ''
    }
    const res = await facilityMaintenanceApi.planPage(params)
    if (res.code === 200) {
      planTableData.value = res.data.list
      planPagination.total = res.data.total
    }
  } finally {
    planLoading.value = false
  }
}

const handleRecordFacilityChange = (facilityId) => {
  const facility = facilityOptions.value.find(f => f.id === facilityId)
  if (facility) {
    recordForm.value.facilityName = facility.facilityName
    recordForm.value.facilityNo = facility.facilityNo
  }
}

const handlePlanFacilityChange = (facilityId) => {
  const facility = facilityOptions.value.find(f => f.id === facilityId)
  if (facility) {
    planForm.value.facilityName = facility.facilityName
    planForm.value.facilityNo = facility.facilityNo
  }
}

const handleRecordAdd = () => {
  isRecordView.value = false
  recordDialogTitle.value = '新增维护记录'
  const now = new Date().toISOString().slice(0, 10)
  recordForm.value = {
    id: null,
    facilityId: null,
    facilityNo: '',
    facilityName: '',
    maintenanceType: 1,
    maintenanceDate: now,
    maintainerName: '',
    maintenanceHours: null,
    maintenanceContent: '',
    result: 1,
    nextMaintenanceDate: '',
    cost: null,
    remark: ''
  }
  recordDialogVisible.value = true
}

const handleRecordEdit = (row) => {
  isRecordView.value = false
  recordDialogTitle.value = '编辑维护记录'
  recordForm.value = { ...row }
  recordDialogVisible.value = true
}

const handleRecordView = (row) => {
  isRecordView.value = true
  recordDialogTitle.value = '维护记录详情'
  recordDetailLoading.value = true
  facilityMaintenanceApi.recordGet(row.id).then(res => {
    if (res.code === 200) {
      recordForm.value = { ...res.data }
    }
    recordDetailLoading.value = false
  }).catch(() => {
    recordDetailLoading.value = false
  })
  recordDialogVisible.value = true
}

const handleRecordDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条维护记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      const res = await facilityMaintenanceApi.recordDelete(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchRecordList()
      }
    }).catch(() => {})
}

const handleRecordSubmit = async () => {
  if (!recordFormRef.value) return
  try {
    await recordFormRef.value.validate()
    let res
    if (recordForm.value.id) {
      res = await facilityMaintenanceApi.recordUpdate(recordForm.value)
    } else {
      res = await facilityMaintenanceApi.recordSave(recordForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(recordForm.value.id ? '更新成功' : '新增成功')
      recordDialogVisible.value = false
      fetchRecordList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handlePlanAdd = () => {
  isPlanView.value = false
  planDialogTitle.value = '新增维护计划'
  const now = new Date().toISOString().slice(0, 10)
  planForm.value = {
    id: null,
    facilityId: null,
    facilityNo: '',
    facilityName: '',
    planName: '',
    maintenanceType: 1,
    planDate: now,
    executorName: '',
    planContent: '',
    cycleDays: null,
    estimatedCost: null,
    priority: 2,
    planStatus: 0,
    remark: ''
  }
  planDialogVisible.value = true
}

const handlePlanEdit = (row) => {
  isPlanView.value = false
  planDialogTitle.value = '编辑维护计划'
  planForm.value = { ...row }
  planDialogVisible.value = true
}

const handlePlanView = (row) => {
  isPlanView.value = true
  planDialogTitle.value = '维护计划详情'
  planDetailLoading.value = true
  planForm.value = { ...row }
  planDetailLoading.value = false
  planDialogVisible.value = true
}

const handlePlanDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条维护计划吗？', '提示', { type: 'warning' })
    .then(async () => {
      const res = await facilityMaintenanceApi.planDelete(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchPlanList()
      }
    }).catch(() => {})
}

const handlePlanSubmit = async () => {
  if (!planFormRef.value) return
  try {
    await planFormRef.value.validate()
    let res
    if (planForm.value.id) {
      res = await facilityMaintenanceApi.planUpdate(planForm.value)
    } else {
      res = await facilityMaintenanceApi.planSave(planForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(planForm.value.id ? '更新成功' : '新增成功')
      planDialogVisible.value = false
      fetchPlanList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handlePlanComplete = (row) => {
  currentPlan.value = { ...row }
  const now = new Date().toISOString().slice(0, 10)
  planCompleteForm.value = {
    maintenanceDate: now,
    maintainerName: '',
    maintenanceContent: row.planContent || '',
    result: 1,
    maintenanceHours: null,
    cost: row.estimatedCost || null,
    nextMaintenanceDate: '',
    remark: ''
  }
  planCompleteDialogVisible.value = true
}

const handlePlanCompleteSubmit = async () => {
  if (!planCompleteFormRef.value) return
  try {
    await planCompleteFormRef.value.validate()
    const data = {
      ...planCompleteForm.value,
      facilityId: currentPlan.value.facilityId,
      facilityNo: currentPlan.value.facilityNo,
      facilityName: currentPlan.value.facilityName,
      maintenanceType: currentPlan.value.maintenanceType
    }
    const res = await facilityMaintenanceApi.planComplete(currentPlan.value.id, data)
    if (res.code === 200) {
      ElMessage.success('完成成功，已自动创建维护记录')
      planCompleteDialogVisible.value = false
      fetchPlanList()
      fetchRecordList()
    }
  } catch (e) {
    console.error(e)
  }
}

watch(activeMainTab, (val) => {
  if (val === 'record') {
    fetchRecordList()
  } else if (val === 'plan') {
    fetchPlanList()
  }
})

onMounted(() => {
  fetchFacilities()
  fetchRecordList()
})
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.page-header {
  margin-bottom: 16px;
}

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

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.total .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-card.valid .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-card.expired .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.main-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}
</style>
