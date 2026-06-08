<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">培训管理</div>
        <div class="page-desc">管理培训计划、培训人员签到、考核结果和效果评估</div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">计划总数</div>
              <div class="stat-value">{{ stats.totalPlans || 0 }}</div>
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
              <div class="stat-label">已完成计划</div>
              <div class="stat-value">{{ stats.completedPlans || 0 }}</div>
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
              <div class="stat-label">进行中计划</div>
              <div class="stat-value">{{ stats.ongoingPlans || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card hours" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总培训时长</div>
              <div class="stat-value">{{ stats.totalTrainingHours || 0 }}<span style="font-size: 14px; margin-left: 4px">小时</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索计划名称..."
          style="width: 260px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchType" placeholder="培训类型" clearable style="width: 140px">
          <el-option label="岗前培训" value="1" />
          <el-option label="岗位技能培训" value="2" />
          <el-option label="安全培训" value="3" />
          <el-option label="质量体系培训" value="4" />
          <el-option label="其他培训" value="5" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 140px">
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="进行中" :value="2" />
          <el-option label="已完成" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增计划
        </el-button>
        <el-button @click="fetchList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="warning" @click="handleCheckCertificate">
          <el-icon><Warning /></el-icon>
          检查证书到期
        </el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%; margin-top: 16px"
      >
        <el-table-column prop="planNo" label="计划编号" width="140" />
        <el-table-column prop="planName" label="计划名称" width="200" show-overflow-tooltip />
        <el-table-column label="培训类型" width="130">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.trainingType)" effect="light" size="small">
              {{ getTypeText(row.trainingType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="trainingHours" label="培训时长(小时)" width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)" :disabled="row.status === 3">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)" :disabled="row.status !== 0">删除</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 1" type="warning" link @click="handleStart(row)">开始</el-button>
            <el-button v-if="row.status === 2" type="success" link @click="handleComplete(row)">完成</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="1100px" top="3vh">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic" />
        <el-tab-pane label="培训人员" name="participant" />
        <el-tab-pane label="效果评估" name="evaluation" />
      </el-tabs>

      <div v-if="activeTab === 'basic'">
        <el-form
          :model="trainingForm"
          :rules="formRules"
          ref="trainingFormRef"
          label-width="100px"
          v-loading="detailLoading"
        >
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="计划编号" prop="planNo">
                <el-input v-model="trainingForm.planNo" placeholder="请输入计划编号" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="计划名称" prop="planName">
                <el-input v-model="trainingForm.planName" placeholder="请输入计划名称" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="培训类型" prop="trainingType">
                <el-select v-model="trainingForm.trainingType" placeholder="请选择类型" :disabled="isView" style="width: 100%">
                  <el-option label="岗前培训" value="1" />
                  <el-option label="岗位技能培训" value="2" />
                  <el-option label="安全培训" value="3" />
                  <el-option label="质量体系培训" value="4" />
                  <el-option label="其他培训" value="5" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="培训方式" prop="trainingMethod">
                <el-select v-model="trainingForm.trainingMethod" placeholder="请选择方式" :disabled="isView" style="width: 100%">
                  <el-option label="现场培训" value="1" />
                  <el-option label="线上培训" value="2" />
                  <el-option label="混合培训" value="3" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="培训讲师" prop="trainer">
                <el-input v-model="trainingForm.trainer" placeholder="请输入讲师姓名" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="开始日期" prop="startDate">
                <el-date-picker
                  v-model="trainingForm.startDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="结束日期" prop="endDate">
                <el-date-picker
                  v-model="trainingForm.endDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="培训时长(小时)" prop="trainingHours">
                <el-input-number
                  v-model="trainingForm.trainingHours"
                  :min="0"
                  :precision="1"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="培训地点" prop="location">
                <el-input v-model="trainingForm.location" placeholder="请输入培训地点" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="状态" prop="status">
                <el-select v-model="trainingForm.status" placeholder="请选择状态" :disabled="isView || trainingForm.id" style="width: 100%">
                  <el-option label="草稿" :value="0" />
                  <el-option label="已发布" :value="1" />
                  <el-option label="进行中" :value="2" />
                  <el-option label="已完成" :value="3" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="负责人" prop="manager">
                <el-input v-model="trainingForm.manager" placeholder="请输入负责人" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="培训内容" prop="content">
            <el-input
              v-model="trainingForm.content"
              type="textarea"
              :rows="3"
              placeholder="请输入培训内容"
              :disabled="isView"
            />
          </el-form-item>

          <el-form-item label="培训目标" prop="objective">
            <el-input
              v-model="trainingForm.objective"
              type="textarea"
              :rows="2"
              placeholder="请输入培训目标"
              :disabled="isView"
            />
          </el-form-item>

          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="trainingForm.remark"
              type="textarea"
              :rows="2"
              placeholder="请输入备注"
              :disabled="isView"
            />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="activeTab === 'participant'">
        <div class="toolbar" style="margin-bottom: 16px">
          <el-button type="primary" @click="handleAddParticipant" :disabled="isView || !trainingForm.id">
            <el-icon><Plus /></el-icon>
            添加培训人员
          </el-button>
        </div>

        <el-table :data="participantData" border stripe style="width: 100%" v-loading="participantLoading">
          <el-table-column prop="personnelNo" label="人员编号" width="120" />
          <el-table-column prop="personnelName" label="姓名" width="100" />
          <el-table-column prop="deptName" label="部门" width="120" />
          <el-table-column label="签到状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getSignInTag(row.signInStatus)" effect="light" size="small">
                {{ getSignInText(row.signInStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="signInTime" label="签到时间" width="160" />
          <el-table-column prop="assessmentScore" label="考核分数" width="100" />
          <el-table-column label="考核结果" width="100">
            <template #default="{ row }">
              <el-tag :type="getAssessmentTag(row.assessmentResult)" effect="light" size="small" v-if="row.assessmentResult">
                {{ getAssessmentText(row.assessmentResult) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="是否发证" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.isCertified === 1" type="success" effect="light" size="small">是</el-tag>
              <el-tag v-else-if="row.isCertified === 0" type="info" effect="light" size="small">否</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="certificateNo" label="证书编号" width="140" />
          <el-table-column prop="certificateValidUntil" label="证书有效期" width="120" />
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-dropdown @command="(cmd) => handleSignIn(row, cmd)" :disabled="isView || trainingForm.status !== 2">
                <el-button type="primary" link size="small">签到</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="1">已签到</el-dropdown-item>
                    <el-dropdown-item command="2">迟到</el-dropdown-item>
                    <el-dropdown-item command="3">早退</el-dropdown-item>
                    <el-dropdown-item command="4">缺勤</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button type="primary" link @click="handleUpdateAssessment(row)" :disabled="isView || trainingForm.status !== 3">
                考核结果
              </el-button>
              <el-button type="danger" link @click="handleDeleteParticipant(row)" :disabled="isView">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="activeTab === 'evaluation'">
        <el-row :gutter="16" class="stats-row" v-if="evaluationStats">
          <el-col :span="6">
            <el-card class="stat-card total" shadow="hover">
              <div class="stat-content">
                <div class="stat-info">
                  <div class="stat-label">培训内容评分</div>
                  <div class="stat-value">{{ evaluationStats.avgContentScore || 0 }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card completed" shadow="hover">
              <div class="stat-content">
                <div class="stat-info">
                  <div class="stat-label">讲师评分</div>
                  <div class="stat-value">{{ evaluationStats.avgTrainerScore || 0 }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card ongoing" shadow="hover">
              <div class="stat-content">
                <div class="stat-info">
                  <div class="stat-label">组织安排评分</div>
                  <div class="stat-value">{{ evaluationStats.avgOrganizationScore || 0 }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card hours" shadow="hover">
              <div class="stat-content">
                <div class="stat-info">
                  <div class="stat-label">实用性评分</div>
                  <div class="stat-value">{{ evaluationStats.avgPracticalityScore || 0 }}</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-table :data="evaluationData" border stripe style="width: 100%; margin-top: 16px" v-loading="evaluationLoading">
          <el-table-column prop="evaluatorName" label="评价人" width="100" />
          <el-table-column prop="contentScore" label="培训内容" width="100" />
          <el-table-column prop="trainerScore" label="讲师" width="80" />
          <el-table-column prop="organizationScore" label="组织安排" width="100" />
          <el-table-column prop="practicalityScore" label="实用性" width="80" />
          <el-table-column prop="overallScore" label="综合评分" width="100" />
          <el-table-column prop="suggestion" label="建议" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createTime" label="评价时间" width="160" />
        </el-table>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addParticipantVisible" title="添加培训人员" width="800px" top="5vh">
      <el-input
        v-model="personnelSearchKeyword"
        placeholder="搜索人员姓名、编号..."
        style="margin-bottom: 16px"
        clearable
        @keyup.enter="fetchPersonnelList"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-table
        :data="personnelList"
        border
        stripe
        style="width: 100%"
        @selection-change="handlePersonnelSelectionChange"
        v-loading="personnelLoading"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="personnelNo" label="人员编号" width="120" />
        <el-table-column prop="personnelName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="140" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="phone" label="电话" width="130" />
      </el-table>
      <el-pagination
        v-model:current-page="personnelPagination.pageNum"
        v-model:page-size="personnelPagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="personnelPagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="fetchPersonnelList"
        @current-change="fetchPersonnelList"
      />
      <template #footer>
        <el-button @click="addParticipantVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddParticipant" :disabled="selectedPersonnels.length === 0">
          确定添加
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assessmentDialogVisible" title="更新考核结果" width="600px" top="10vh">
      <el-form
        :model="assessmentForm"
        :rules="assessmentRules"
        ref="assessmentFormRef"
        label-width="100px"
      >
        <el-form-item label="考核分数" prop="assessmentScore">
          <el-input-number
            v-model="assessmentForm.assessmentScore"
            :min="0"
            :max="100"
            :precision="1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="考核结果" prop="assessmentResult">
          <el-select v-model="assessmentForm.assessmentResult" placeholder="请选择考核结果" style="width: 100%">
            <el-option label="优秀" value="1" />
            <el-option label="良好" value="2" />
            <el-option label="合格" value="3" />
            <el-option label="不合格" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否发证" prop="isCertified">
          <el-select v-model="assessmentForm.isCertified" placeholder="请选择" style="width: 100%">
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="证书编号" prop="certificateNo">
          <el-input v-model="assessmentForm.certificateNo" placeholder="请输入证书编号" />
        </el-form-item>
        <el-form-item label="证书有效期" prop="certificateValidUntil">
          <el-date-picker
            v-model="assessmentForm.certificateValidUntil"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="assessmentForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assessmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssessment">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, CircleCheck, Clock, Timer, Search, Plus, Refresh, Warning, Check } from '@element-plus/icons-vue'
import { trainingApi, personnelApi } from '@/api/personnel'

const loading = ref(false)
const detailLoading = ref(false)
const dialogVisible = ref(false)
const isView = ref(false)
const dialogTitle = ref('')
const activeTab = ref('basic')

const addParticipantVisible = ref(false)
const participantLoading = ref(false)
const personnelLoading = ref(false)
const evaluationLoading = ref(false)

const assessmentDialogVisible = ref(false)
const assessmentFormRef = ref(null)

const searchKeyword = ref('')
const searchType = ref('')
const searchStatus = ref('')
const personnelSearchKeyword = ref('')

const stats = reactive({
  totalPlans: 0,
  completedPlans: 0,
  ongoingPlans: 0,
  totalTrainingHours: 0
})

const tableData = ref([])
const participantData = ref([])
const evaluationData = ref([])
const evaluationStats = ref(null)
const personnelList = ref([])
const selectedPersonnels = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const personnelPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const trainingForm = ref({
  id: null,
  planNo: '',
  planName: '',
  trainingType: '',
  trainingMethod: '',
  trainer: '',
  startDate: '',
  endDate: '',
  trainingHours: null,
  location: '',
  status: 0,
  manager: '',
  content: '',
  objective: '',
  remark: ''
})

const assessmentForm = ref({
  id: null,
  assessmentScore: null,
  assessmentResult: '',
  isCertified: null,
  certificateNo: '',
  certificateValidUntil: '',
  remark: ''
})

const formRules = {
  planNo: [{ required: true, message: '请输入计划编号', trigger: 'blur' }],
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }]
}

const assessmentRules = {
  assessmentScore: [{ required: true, message: '请输入考核分数', trigger: 'blur' }],
  assessmentResult: [{ required: true, message: '请选择考核结果', trigger: 'change' }]
}

const trainingFormRef = ref(null)
const currentParticipant = ref(null)

const getTypeTag = (type) => {
  const tags = { '1': 'primary', '2': 'success', '3': 'warning', '4': 'danger', '5': 'info' }
  return tags[type] || 'info'
}

const getTypeText = (type) => {
  const texts = { '1': '岗前培训', '2': '岗位技能培训', '3': '安全培训', '4': '质量体系培训', '5': '其他培训' }
  return texts[type] || '未知'
}

const getStatusTag = (status) => {
  const tags = { 0: 'info', 1: 'primary', 2: 'warning', 3: 'success' }
  return tags[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '草稿', 1: '已发布', 2: '进行中', 3: '已完成' }
  return texts[status] || '未知'
}

const getSignInTag = (status) => {
  const tags = { 0: 'info', 1: 'success', 2: 'warning', 3: 'warning', 4: 'danger' }
  return tags[status] || 'info'
}

const getSignInText = (status) => {
  const texts = { 0: '未签到', 1: '已签到', 2: '迟到', 3: '早退', 4: '缺勤' }
  return texts[status] || '未知'
}

const getAssessmentTag = (result) => {
  const tags = { '1': 'success', '2': 'primary', '3': 'warning', '4': 'danger' }
  return tags[result] || 'info'
}

const getAssessmentText = (result) => {
  const texts = { '1': '优秀', '2': '良好', '3': '合格', '4': '不合格' }
  return texts[result] || '未知'
}

const fetchStats = async () => {
  try {
    const res = await trainingApi.statistics()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      planName: searchKeyword.value,
      trainingType: searchType.value,
      status: searchStatus.value
    }
    const res = await trainingApi.planPage(params)
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const fetchParticipants = async () => {
  if (!trainingForm.value.id) return
  participantLoading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 1000,
      planId: trainingForm.value.id
    }
    const res = await trainingApi.participantPage(params)
    if (res.code === 200) {
      participantData.value = res.data.list
    }
  } finally {
    participantLoading.value = false
  }
}

const fetchEvaluations = async () => {
  if (!trainingForm.value.id) return
  evaluationLoading.value = true
  try {
    const res = await trainingApi.evaluationByPlan(trainingForm.value.id)
    if (res.code === 200) {
      evaluationData.value = res.data.list || []
      evaluationStats.value = res.data.statistics || null
    }
  } finally {
    evaluationLoading.value = false
  }
}

const fetchPersonnelList = async () => {
  personnelLoading.value = true
  try {
    const params = {
      pageNum: personnelPagination.pageNum,
      pageSize: personnelPagination.pageSize,
      personnelName: personnelSearchKeyword.value,
      personnelNo: personnelSearchKeyword.value
    }
    const res = await personnelApi.page(params)
    if (res.code === 200) {
      personnelList.value = res.data.list
      personnelPagination.total = res.data.total
    }
  } finally {
    personnelLoading.value = false
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增培训计划'
  activeTab.value = 'basic'
  trainingForm.value = {
    id: null,
    planNo: '',
    planName: '',
    trainingType: '',
    trainingMethod: '',
    trainer: '',
    startDate: '',
    endDate: '',
    trainingHours: null,
    location: '',
    status: 0,
    manager: '',
    content: '',
    objective: '',
    remark: ''
  }
  participantData.value = []
  evaluationData.value = []
  evaluationStats.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑培训计划'
  activeTab.value = 'basic'
  trainingForm.value = { ...row }
  fetchParticipants()
  fetchEvaluations()
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '培训详情'
  activeTab.value = 'basic'
  detailLoading.value = true
  try {
    const res = await trainingApi.planGet(row.id)
    if (res.code === 200) {
      trainingForm.value = { ...res.data }
      fetchParticipants()
      fetchEvaluations()
    }
  } finally {
    detailLoading.value = false
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除培训计划"${row.planName}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await trainingApi.planDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
      fetchStats()
    }
  }).catch(() => {})
}

const handlePublish = (row) => {
  ElMessageBox.confirm(`确定要发布培训计划"${row.planName}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await trainingApi.planPublish(row.id)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      fetchList()
      fetchStats()
    }
  }).catch(() => {})
}

const handleStart = (row) => {
  ElMessageBox.confirm(`确定要开始培训计划"${row.planName}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await trainingApi.planStart(row.id)
    if (res.code === 200) {
      ElMessage.success('培训已开始')
      fetchList()
      fetchStats()
    }
  }).catch(() => {})
}

const handleComplete = (row) => {
  ElMessageBox.confirm(`确定要完成培训计划"${row.planName}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await trainingApi.planComplete(row.id)
    if (res.code === 200) {
      ElMessage.success('培训已完成')
      fetchList()
      fetchStats()
    }
  }).catch(() => {})
}

const handleCheckCertificate = async () => {
  try {
    const res = await trainingApi.checkCertificateExpiry()
    if (res.code === 200) {
      const count = res.data?.count || 0
      if (count > 0) {
        ElMessage.warning(`发现 ${count} 个证书即将到期，请及时处理`)
      } else {
        ElMessage.success('暂无即将到期的证书')
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const handleSubmit = async () => {
  if (!trainingFormRef.value) return
  try {
    await trainingFormRef.value.validate()
    let res
    if (trainingForm.value.id) {
      res = await trainingApi.planUpdate(trainingForm.value)
    } else {
      res = await trainingApi.planSave(trainingForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(trainingForm.value.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      fetchList()
      fetchStats()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleAddParticipant = () => {
  personnelSearchKeyword.value = ''
  personnelPagination.pageNum = 1
  selectedPersonnels.value = []
  fetchPersonnelList()
  addParticipantVisible.value = true
}

const handlePersonnelSelectionChange = (selection) => {
  selectedPersonnels.value = selection
}

const submitAddParticipant = async () => {
  if (selectedPersonnels.value.length === 0) {
    ElMessage.warning('请选择要添加的人员')
    return
  }
  try {
    const participants = selectedPersonnels.value.map(p => ({
      planId: trainingForm.value.id,
      personnelId: p.id,
      personnelNo: p.personnelNo,
      personnelName: p.personnelName,
      deptName: p.deptName,
      signInStatus: 0
    }))
    for (const participant of participants) {
      await trainingApi.participantAdd(participant)
    }
    ElMessage.success('添加成功')
    addParticipantVisible.value = false
    fetchParticipants()
  } catch (e) {
    console.error(e)
  }
}

const handleSignIn = async (row, status) => {
  try {
    const res = await trainingApi.participantSignIn({
      id: row.id,
      signInStatus: status,
      signInTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    })
    if (res.code === 200) {
      ElMessage.success('签到成功')
      fetchParticipants()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleUpdateAssessment = (row) => {
  currentParticipant.value = row
  assessmentForm.value = {
    id: row.id,
    assessmentScore: row.assessmentScore,
    assessmentResult: row.assessmentResult || '',
    isCertified: row.isCertified,
    certificateNo: row.certificateNo || '',
    certificateValidUntil: row.certificateValidUntil || '',
    remark: row.remark || ''
  }
  assessmentDialogVisible.value = true
}

const submitAssessment = async () => {
  if (!assessmentFormRef.value) return
  try {
    await assessmentFormRef.value.validate()
    const res = await trainingApi.participantUpdate(assessmentForm.value)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      assessmentDialogVisible.value = false
      fetchParticipants()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleDeleteParticipant = (row) => {
  ElMessageBox.confirm(`确定要删除人员"${row.personnelName}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await trainingApi.participantDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchParticipants()
    }
  }).catch(() => {})
}

watch(activeTab, (newVal) => {
  if (newVal === 'participant' && trainingForm.value.id) {
    fetchParticipants()
  } else if (newVal === 'evaluation' && trainingForm.value.id) {
    fetchEvaluations()
  }
})

onMounted(() => {
  fetchStats()
  fetchList()
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

.stat-card.completed .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.stat-card.ongoing .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-card.hours .stat-icon {
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
</style>
