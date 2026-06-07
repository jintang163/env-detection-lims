<template>
  <div class="capa-management-container">
    <div class="stats-cards">
      <el-card class="stat-card" shadow="hover" @click="handleStatClick('all')">
        <div class="stat-content">
          <div class="stat-icon blue">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalCount }}</div>
            <div class="stat-label">总CAPA数</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover" @click="handleStatClick(1)">
        <div class="stat-content">
          <div class="stat-icon orange">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingApprovalCount }}</div>
            <div class="stat-label">待审批</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover" @click="handleStatClick(2)">
        <div class="stat-content">
          <div class="stat-icon cyan">
            <el-icon><Setting /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.executingCount }}</div>
            <div class="stat-label">执行中</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover" @click="handleStatClick(3)">
        <div class="stat-content">
          <div class="stat-icon purple">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingVerifyCount }}</div>
            <div class="stat-label">待验证</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover" @click="handleStatClick(5)">
        <div class="stat-content">
          <div class="stat-icon green">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.closedCount }}</div>
            <div class="stat-label">已关闭</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover" @click="handleOverdueClick">
        <div class="stat-content">
          <div class="stat-icon red">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.overdueCount }}</div>
            <div class="stat-label">超期</div>
          </div>
        </div>
      </el-card>
    </div>

    <el-card class="main-card" shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部CAPA" name="all" />
        <el-tab-pane label="我发起的" name="myInitiated" />
        <el-tab-pane label="我执行的" name="myExecuted" />
        <el-tab-pane label="我验证的" name="myVerified" />
      </el-tabs>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="CAPA编号/标题" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.capaType" placeholder="请选择类型" clearable>
            <el-option label="纠正措施(CA)" value="CA" />
            <el-option label="预防措施(PA)" value="PA" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源类型">
          <el-select v-model="searchForm.sourceType" placeholder="请选择来源" clearable>
            <el-option label="OOS" :value="1" />
            <el-option label="客户投诉" :value="2" />
            <el-option label="内审" :value="3" />
            <el-option label="外审" :value="4" />
            <el-option label="能力验证" :value="5" />
            <el-option label="偏差" :value="6" />
            <el-option label="其他" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="待审批" :value="1" />
            <el-option label="执行中" :value="2" />
            <el-option label="待验证" :value="3" />
            <el-option label="已验证" :value="4" />
            <el-option label="已关闭" :value="5" />
            <el-option label="已驳回" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="严重等级">
          <el-select v-model="searchForm.severityLevel" placeholder="请选择等级" clearable>
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="searchForm.priority" placeholder="请选择优先级" clearable>
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete" :disabled="selectedIds.length === 0">批量删除</el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        @selection-change="handleSelectionChange"
        :row-class-name="tableRowClassName"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="capaNo" label="CAPA编号" width="160" fixed="left" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="capaType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.capaType === 'CA' ? 'danger' : 'warning'">
              {{ row.capaType === 'CA' ? '纠正措施' : '预防措施' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sourceType" label="来源类型" width="120">
          <template #default="{ row }">
            {{ getSourceTypeName(row.sourceType) }}
          </template>
        </el-table-column>
        <el-table-column prop="severityLevel" label="严重等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.severityLevel)" effect="dark">
              {{ getLevelName(row.severityLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.priority)" effect="dark">
              {{ getLevelName(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" :class="{ 'tag-purple': row.status === 3 }" effect="dark">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="implementorName" label="执行人" width="100" />
        <el-table-column prop="planCompleteDate" label="计划完成日期" width="140" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button
              v-if="row.status === 0 || row.status === 6"
              type="primary"
              link
              @click="handleEdit(row)"
            >编辑</el-button>
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              @click="handleSubmit(row)"
            >提交审批</el-button>
            <el-button
              v-if="row.status === 1"
              type="success"
              link
              @click="handleApprove(row)"
            >审批通过</el-button>
            <el-button
              v-if="row.status === 1"
              type="danger"
              link
              @click="handleReject(row)"
            >审批驳回</el-button>
            <el-button
              v-if="row.status === 2"
              type="success"
              link
              @click="handleExecuteComplete(row)"
            >执行完成</el-button>
            <el-button
              v-if="row.status === 3"
              type="success"
              link
              @click="handleVerifyPass(row)"
            >验证通过</el-button>
            <el-button
              v-if="row.status === 3"
              type="danger"
              link
              @click="handleVerifyFail(row)"
            >验证不通过</el-button>
            <el-button
              v-if="row.status === 4"
              type="success"
              link
              @click="handleClose(row)"
            >关闭</el-button>
            <el-button
              v-if="row.status === 0 || row.status === 6"
              type="danger"
              link
              @click="handleDelete(row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑CAPA' : '新增CAPA'"
      width="900px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="capaFormRef"
        :model="capaForm"
        :rules="capaFormRules"
        label-width="100px"
      >
        <el-tabs v-model="capaFormTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="类型" prop="capaType">
                  <el-radio-group v-model="capaForm.capaType">
                    <el-radio value="CA">纠正措施(CA)</el-radio>
                    <el-radio value="PA">预防措施(PA)</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="来源类型" prop="sourceType">
                  <el-select v-model="capaForm.sourceType" placeholder="请选择来源类型" style="width: 100%">
                    <el-option label="OOS" :value="1" />
                    <el-option label="客户投诉" :value="2" />
                    <el-option label="内审" :value="3" />
                    <el-option label="外审" :value="4" />
                    <el-option label="能力验证" :value="5" />
                    <el-option label="偏差" :value="6" />
                    <el-option label="其他" :value="7" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="来源编号">
                  <el-input v-model="capaForm.sourceNo" placeholder="请输入来源编号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="标题" prop="title">
                  <el-input v-model="capaForm.title" placeholder="请输入标题" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="问题描述" prop="problemDescription">
                  <el-input
                    v-model="capaForm.problemDescription"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入问题描述"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="原因分析" name="reason">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="分析方法">
                  <el-radio-group v-model="capaForm.analysisMethod">
                    <el-radio value="5Why">5Why</el-radio>
                    <el-radio value="鱼骨图">鱼骨图</el-radio>
                    <el-radio value="FMEA">FMEA</el-radio>
                    <el-radio value="其他">其他</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="根本原因" prop="rootCause">
                  <el-input
                    v-model="capaForm.rootCause"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入根本原因分析"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="风险评估" name="risk">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="严重等级" prop="severityLevel">
                  <el-radio-group v-model="capaForm.severityLevel">
                    <el-radio :value="1">低</el-radio>
                    <el-radio :value="2">中</el-radio>
                    <el-radio :value="3">高</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="优先级" prop="priority">
                  <el-radio-group v-model="capaForm.priority">
                    <el-radio :value="1">低</el-radio>
                    <el-radio :value="2">中</el-radio>
                    <el-radio :value="3">高</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="风险评估内容">
                  <el-input
                    v-model="capaForm.riskAssessment"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入风险评估内容"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="措施计划" name="plan">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="执行人" prop="implementorId">
                  <el-input v-model="capaForm.implementorName" placeholder="请选择执行人" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="验证人" prop="verifierId">
                  <el-input v-model="capaForm.verifierName" placeholder="请选择验证人" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="计划完成日期" prop="planCompleteDate">
                  <el-date-picker
                    v-model="capaForm.planCompleteDate"
                    type="date"
                    placeholder="请选择日期"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="拟采取措施" prop="measures">
                  <el-input
                    v-model="capaForm.measures"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入拟采取的措施"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailVisible" title="CAPA详情" width="1000px" :close-on-click-modal="false">
      <div v-if="currentDetail" class="detail-content">
        <el-steps :active="getStepActive(currentDetail.status)" align-center>
          <el-step title="草稿" />
          <el-step title="待审批" />
          <el-step title="执行中" />
          <el-step title="待验证" />
          <el-step title="已验证" />
          <el-step title="已关闭" />
        </el-steps>

        <el-tabs v-model="detailTab" class="detail-tabs">
          <el-tab-pane label="基本信息" name="basic">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="CAPA编号">{{ currentDetail.capaNo }}</el-descriptions-item>
              <el-descriptions-item label="类型">
                <el-tag :type="currentDetail.capaType === 'CA' ? 'danger' : 'warning'">
                  {{ currentDetail.capaType === 'CA' ? '纠正措施' : '预防措施' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="来源类型">{{ getSourceTypeName(currentDetail.sourceType) }}</el-descriptions-item>
              <el-descriptions-item label="来源编号">{{ currentDetail.sourceNo || '-' }}</el-descriptions-item>
              <el-descriptions-item label="标题" :span="2">{{ currentDetail.title }}</el-descriptions-item>
              <el-descriptions-item label="严重等级">
                <el-tag :type="getLevelTagType(currentDetail.severityLevel)" effect="dark">
                  {{ getLevelName(currentDetail.severityLevel) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="优先级">
                <el-tag :type="getLevelTagType(currentDetail.priority)" effect="dark">
                  {{ getLevelName(currentDetail.priority) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="getStatusTagType(currentDetail.status)" :class="{ 'tag-purple': currentDetail.status === 3 }" effect="dark">
                  {{ getStatusName(currentDetail.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="问题描述" :span="2">
                <pre class="pre-wrap">{{ currentDetail.problemDescription || '-' }}</pre>
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
          <el-tab-pane label="原因分析" name="reason">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="分析方法">{{ currentDetail.analysisMethod || '-' }}</el-descriptions-item>
              <el-descriptions-item label="根本原因">
                <pre class="pre-wrap">{{ currentDetail.rootCause || '-' }}</pre>
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
          <el-tab-pane label="措施计划" name="plan">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="执行人">{{ currentDetail.implementorName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="验证人">{{ currentDetail.verifierName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="计划完成日期">{{ currentDetail.planCompleteDate || '-' }}</el-descriptions-item>
              <el-descriptions-item label="风险评估内容" :span="2">
                <pre class="pre-wrap">{{ currentDetail.riskAssessment || '-' }}</pre>
              </el-descriptions-item>
              <el-descriptions-item label="拟采取措施" :span="2">
                <pre class="pre-wrap">{{ currentDetail.measures || '-' }}</pre>
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>
          <el-tab-pane label="处理日志" name="logs">
            <el-timeline>
              <el-timeline-item
                v-for="log in logs"
                :key="log.id"
                :timestamp="log.operateTime"
                :type="getLogType(log.operateType)"
              >
                <el-card>
                  <h4>{{ log.operateType }}</h4>
                  <p>操作人：{{ log.operatorName }}</p>
                  <p v-if="log.operateContent">操作内容：{{ log.operateContent }}</p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <div class="detail-footer">
          <el-button
            v-if="currentDetail?.status === 0"
            type="success"
            @click="handleSubmit(currentDetail)"
          >提交审批</el-button>
          <el-button
            v-if="currentDetail?.status === 1"
            type="success"
            @click="handleApprove(currentDetail)"
          >审批通过</el-button>
          <el-button
            v-if="currentDetail?.status === 1"
            type="danger"
            @click="handleReject(currentDetail)"
          >审批驳回</el-button>
          <el-button
            v-if="currentDetail?.status === 2"
            type="success"
            @click="handleExecuteComplete(currentDetail)"
          >执行完成</el-button>
          <el-button
            v-if="currentDetail?.status === 3"
            type="success"
            @click="handleVerifyPass(currentDetail)"
          >验证通过</el-button>
          <el-button
            v-if="currentDetail?.status === 3"
            type="danger"
            @click="handleVerifyFail(currentDetail)"
          >验证不通过</el-button>
          <el-button
            v-if="currentDetail?.status === 4"
            type="success"
            @click="handleClose(currentDetail)"
          >关闭</el-button>
          <el-button @click="detailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="approveVisible" :title="approveType === 'approve' ? '审批通过' : '审批驳回'" width="500px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审批意见" prop="opinion">
          <el-input
            v-model="approveForm.opinion"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见"
          />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            action="#"
            multiple
            :auto-upload="false"
          >
            <el-button :icon="Upload">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApprove">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="executeVisible" title="执行完成" width="500px">
      <el-form :model="executeForm" label-width="100px">
        <el-form-item label="执行结果说明" prop="result">
          <el-input
            v-model="executeForm.result"
            type="textarea"
            :rows="4"
            placeholder="请输入执行结果说明"
          />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            action="#"
            multiple
            :auto-upload="false"
          >
            <el-button :icon="Upload">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="executeVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmExecuteComplete">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="verifyVisible" title="验证" width="500px">
      <el-form :model="verifyForm" label-width="100px">
        <el-form-item label="验证结果" prop="result">
          <el-input
            v-model="verifyForm.result"
            type="textarea"
            :rows="4"
            placeholder="请输入验证结果"
          />
        </el-form-item>
        <el-form-item label="验证证据附件">
          <el-upload
            action="#"
            multiple
            :auto-upload="false"
          >
            <el-button :icon="Upload">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="verifyVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmVerify">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { capaApi } from '@/api/detection'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  Document,
  Clock,
  Setting,
  View,
  CircleCheck,
  Warning,
  Upload
} from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const selectedIds = ref([])
const activeTab = ref('all')
const detailTab = ref('basic')
const capaFormTab = ref('basic')

const stats = reactive({
  totalCount: 0,
  pendingApprovalCount: 0,
  executingCount: 0,
  pendingVerifyCount: 0,
  closedCount: 0,
  overdueCount: 0
})

const searchForm = reactive({
  keyword: '',
  capaType: '',
  sourceType: null,
  status: null,
  severityLevel: null,
  priority: null,
  overdueOnly: false
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const capaFormRef = ref()
const capaForm = reactive({
  id: null,
  capaType: 'CA',
  sourceType: null,
  sourceNo: '',
  title: '',
  problemDescription: '',
  rootCause: '',
  analysisMethod: '5Why',
  severityLevel: 2,
  priority: 2,
  riskAssessment: '',
  measures: '',
  implementorId: null,
  implementorName: '',
  verifierId: null,
  verifierName: '',
  planCompleteDate: ''
})

const capaFormRules = {
  capaType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  sourceType: [{ required: true, message: '请选择来源类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  problemDescription: [{ required: true, message: '请输入问题描述', trigger: 'blur' }],
  rootCause: [{ required: true, message: '请输入根本原因', trigger: 'blur' }],
  severityLevel: [{ required: true, message: '请选择严重等级', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  measures: [{ required: true, message: '请输入拟采取措施', trigger: 'blur' }],
  implementorId: [{ required: true, message: '请选择执行人', trigger: 'change' }],
  verifierId: [{ required: true, message: '请选择验证人', trigger: 'change' }],
  planCompleteDate: [{ required: true, message: '请选择计划完成日期', trigger: 'change' }]
}

const detailVisible = ref(false)
const currentDetail = ref(null)
const logs = ref([])

const approveVisible = ref(false)
const currentRow = ref(null)
const approveType = ref('approve')
const approveForm = reactive({
  opinion: ''
})

const executeVisible = ref(false)
const executeForm = reactive({
  result: ''
})

const verifyVisible = ref(false)
const verifyForm = reactive({
  result: ''
})
const verifyType = ref('pass')

const statusConfig = {
  0: { name: '草稿', type: 'info', color: '#909399' },
  1: { name: '待审批', type: 'warning', color: '#E6A23C' },
  2: { name: '执行中', type: 'primary', color: '#409EFF' },
  3: { name: '待验证', type: '', color: '#9C27B0' },
  4: { name: '已验证', type: 'success', color: '#8BC34A' },
  5: { name: '已关闭', type: 'success', color: '#67C23A' },
  6: { name: '已驳回', type: 'danger', color: '#F56C6C' }
}

const levelConfig = {
  1: { name: '低', type: 'success' },
  2: { name: '中', type: 'primary' },
  3: { name: '高', type: 'danger' }
}

const sourceTypeConfig = {
  1: 'OOS',
  2: '客户投诉',
  3: '内审',
  4: '外审',
  5: '能力验证',
  6: '偏差',
  7: '其他'
}

const getStatusName = (status) => {
  return statusConfig[status]?.name || status
}

const getStatusTagType = (status) => {
  if (status === 3) return ''
  return statusConfig[status]?.type || 'info'
}

const getLevelName = (level) => {
  return levelConfig[level]?.name || level
}

const getLevelTagType = (level) => {
  return levelConfig[level]?.type || 'info'
}

const getSourceTypeName = (type) => {
  return sourceTypeConfig[type] || type
}

const getStepActive = (status) => {
  if (status >= 5) return 5
  if (status >= 4) return 4
  if (status >= 3) return 3
  if (status >= 2) return 2
  if (status >= 1) return 1
  return 0
}

const getLogType = (type) => {
  if (type.includes('驳回') || type.includes('不通过')) return 'danger'
  if (type.includes('通过') || type.includes('完成') || type.includes('关闭')) return 'success'
  return 'primary'
}

const isOverdue = (row) => {
  if (!row.planCompleteDate || row.status === 5) return false
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const planDate = new Date(row.planCompleteDate)
  planDate.setHours(0, 0, 0, 0)
  return planDate < today
}

const tableRowClassName = ({ row }) => {
  if (isOverdue(row)) {
    return 'overdue-row'
  }
  return ''
}

const fetchStats = async () => {
  try {
    const res = await capaApi.getStats()
    const data = res.data || {}
    stats.totalCount = data.totalCount || 0
    stats.pendingApprovalCount = data.pendingApprovalCount || 0
    stats.executingCount = data.executingCount || 0
    stats.pendingVerifyCount = data.pendingVerifyCount || 0
    stats.closedCount = data.closedCount || 0
    stats.overdueCount = data.overdueCount || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      capaType: searchForm.capaType || undefined,
      sourceType: searchForm.sourceType !== null ? searchForm.sourceType : undefined,
      status: searchForm.status !== null ? searchForm.status : undefined,
      severityLevel: searchForm.severityLevel !== null ? searchForm.severityLevel : undefined,
      priority: searchForm.priority !== null ? searchForm.priority : undefined,
      tabType: activeTab.value === 'all' ? undefined : activeTab.value,
      overdueOnly: searchForm.overdueOnly || undefined
    }
    const res = await capaApi.page(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取CAPA数据失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchLogs = async (id) => {
  try {
    const res = await capaApi.getLogs(id)
    logs.value = (res.data || []).sort((a, b) => new Date(b.operateTime) - new Date(a.operateTime))
  } catch (error) {
    console.error('获取处理日志失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.capaType = ''
  searchForm.sourceType = null
  searchForm.status = null
  searchForm.severityLevel = null
  searchForm.priority = null
  searchForm.overdueOnly = false
  pagination.current = 1
  fetchData()
}

const handleTabChange = () => {
  pagination.current = 1
  fetchData()
}

const handleStatClick = (status) => {
  if (status === 'all') {
    searchForm.status = null
  } else {
    searchForm.status = status
  }
  searchForm.overdueOnly = false
  pagination.current = 1
  fetchData()
}

const handleOverdueClick = () => {
  searchForm.status = null
  searchForm.overdueOnly = true
  pagination.current = 1
  fetchData()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  resetCapaForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(capaForm, {
    id: row.id,
    capaType: row.capaType,
    sourceType: row.sourceType,
    sourceNo: row.sourceNo || '',
    title: row.title,
    problemDescription: row.problemDescription || '',
    rootCause: row.rootCause || '',
    analysisMethod: row.analysisMethod || '5Why',
    severityLevel: row.severityLevel,
    priority: row.priority,
    riskAssessment: row.riskAssessment || '',
    measures: row.measures || '',
    implementorId: row.implementorId,
    implementorName: row.implementorName || '',
    verifierId: row.verifierId,
    verifierName: row.verifierName || '',
    planCompleteDate: row.planCompleteDate || ''
  })
  dialogVisible.value = true
}

const resetCapaForm = () => {
  Object.assign(capaForm, {
    id: null,
    capaType: 'CA',
    sourceType: null,
    sourceNo: '',
    title: '',
    problemDescription: '',
    rootCause: '',
    analysisMethod: '5Why',
    severityLevel: 2,
    priority: 2,
    riskAssessment: '',
    measures: '',
    implementorId: null,
    implementorName: '',
    verifierId: null,
    verifierName: '',
    planCompleteDate: ''
  })
  capaFormRef.value?.clearValidate()
}

const handleSave = async () => {
  try {
    await capaFormRef.value.validate()
    if (isEdit.value) {
      await capaApi.update(capaForm)
      ElMessage.success('更新成功')
    } else {
      await capaApi.save(capaForm)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    fetchStats()
    fetchData()
  } catch (error) {
    if (error !== false) {
      console.error('保存失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该CAPA吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await capaApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchStats()
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的CAPA吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const id of selectedIds.value) {
      await capaApi.delete(id)
    }
    ElMessage.success('批量删除成功')
    fetchStats()
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交审批吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await capaApi.submit(row.id)
    ElMessage.success('提交成功')
    fetchStats()
    fetchData()
    if (detailVisible.value) {
      fetchDetail(row.id)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
    }
  }
}

const handleApprove = (row) => {
  currentRow.value = row
  approveType.value = 'approve'
  approveForm.opinion = ''
  approveVisible.value = true
}

const handleReject = (row) => {
  currentRow.value = row
  approveType.value = 'reject'
  approveForm.opinion = ''
  approveVisible.value = true
}

const confirmApprove = async () => {
  try {
    if (approveType.value === 'approve') {
      await capaApi.approve(currentRow.value.id, { opinion: approveForm.opinion })
      ElMessage.success('审批通过')
    } else {
      await capaApi.reject(currentRow.value.id, { opinion: approveForm.opinion })
      ElMessage.success('审批驳回')
    }
    approveVisible.value = false
    fetchStats()
    fetchData()
    if (detailVisible.value) {
      fetchDetail(currentRow.value.id)
    }
  } catch (error) {
    console.error('审批失败:', error)
  }
}

const handleExecuteComplete = (row) => {
  currentRow.value = row
  executeForm.result = ''
  executeVisible.value = true
}

const confirmExecuteComplete = async () => {
  try {
    await capaApi.executeComplete(currentRow.value.id, { result: executeForm.result })
    ElMessage.success('执行完成')
    executeVisible.value = false
    fetchStats()
    fetchData()
    if (detailVisible.value) {
      fetchDetail(currentRow.value.id)
    }
  } catch (error) {
    console.error('执行完成失败:', error)
  }
}

const handleVerifyPass = (row) => {
  currentRow.value = row
  verifyType.value = 'pass'
  verifyForm.result = ''
  verifyVisible.value = true
}

const handleVerifyFail = (row) => {
  currentRow.value = row
  verifyType.value = 'fail'
  verifyForm.result = ''
  verifyVisible.value = true
}

const confirmVerify = async () => {
  try {
    if (verifyType.value === 'pass') {
      await capaApi.verifyPass(currentRow.value.id, { result: verifyForm.result })
      ElMessage.success('验证通过')
    } else {
      await capaApi.verifyFail(currentRow.value.id, { result: verifyForm.result })
      ElMessage.success('验证不通过')
    }
    verifyVisible.value = false
    fetchStats()
    fetchData()
    if (detailVisible.value) {
      fetchDetail(currentRow.value.id)
    }
  } catch (error) {
    console.error('验证失败:', error)
  }
}

const handleClose = async (row) => {
  try {
    await ElMessageBox.confirm('确定要关闭该CAPA吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await capaApi.close(row.id, {})
    ElMessage.success('关闭成功')
    fetchStats()
    fetchData()
    if (detailVisible.value) {
      fetchDetail(row.id)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('关闭失败:', error)
    }
  }
}

const handleView = async (row) => {
  await fetchDetail(row.id)
  detailVisible.value = true
}

const fetchDetail = async (id) => {
  return new Promise(async (resolve, reject) => {
    try {
      const res = await capaApi.getDetail(id)
      currentDetail.value = res.data
      await fetchLogs(id)
      resolve()
    } catch (error) {
      console.error('获取详情失败:', error)
      reject(error)
    }
  })
}

onMounted(() => {
  fetchStats()
  fetchData()
})
</script>

<style lang="scss" scoped>
.capa-management-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;

  .stat-card {
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 50px;
        height: 50px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 24px;

        &.blue {
          background: linear-gradient(135deg, #409EFF, #667EEA);
        }
        &.orange {
          background: linear-gradient(135deg, #E6A23C, #F56C6C);
        }
        &.cyan {
          background: linear-gradient(135deg, #13C2C2, #1890FF);
        }
        &.purple {
          background: linear-gradient(135deg, #9C27B0, #673AB7);
        }
        &.green {
          background: linear-gradient(135deg, #67C23A, #8BC34A);
        }
        &.red {
          background: linear-gradient(135deg, #F56C6C, #C45655);
        }
      }

      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
        }
        .stat-label {
          font-size: 13px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
}

.main-card {
  .search-form {
    margin-bottom: 16px;
  }

  .table-toolbar {
    margin-bottom: 16px;
  }

  .pagination {
    margin-top: 16px;
    justify-content: flex-end;
  }
}

:deep(.el-table .el-table__row.overdue-row) {
  background-color: #fef0f0 !important;

  &:hover > td {
    background-color: #fde2e2 !important;
  }
}

.detail-content {
  .detail-tabs {
    margin-top: 24px;
  }

  .pre-wrap {
    white-space: pre-wrap;
    word-wrap: break-word;
    margin: 0;
    font-family: inherit;
    font-size: inherit;
    color: inherit;
  }
}

.detail-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

:deep(.el-step.is-vertical .el-step__icon-inner) {
  background-color: #fff;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
}

.tag-purple {
  background-color: #9C27B0 !important;
  border-color: #9C27B0 !important;
}
</style>
