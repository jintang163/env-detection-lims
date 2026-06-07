<template>
  <div class="oos-record-edit-container">
    <el-page-header @back="handleBack" content="OOS详情处理" class="page-header" />

    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Warning /></el-icon>
          <span>基本信息</span>
          <el-tag :type="getStatusTagType(oosData.status)" effect="dark" class="status-tag">
            {{ getStatusName(oosData.status) }}
          </el-tag>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="OOS编号">
          {{ oosData.oosNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="数据记录编号">
          {{ oosData.recordNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="检测项目">
          {{ oosData.itemName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="检测结果">
          <span class="highlight">{{ oosData.result }} {{ oosData.unit }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="标准限值">
          {{ oosData.standardLimit }} {{ oosData.unit }}
        </el-descriptions-item>
        <el-descriptions-item label="偏差率">
          <span class="deviation-rate" :class="{ blink: oosData.deviationRate > 50 }">
            {{ oosData.deviationRate }}%
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="OOS等级">
          <el-tag :type="getLevelTagType(oosData.oosLevel)" effect="dark">
            {{ getLevelName(oosData.oosLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ oosData.createTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建人">
          {{ oosData.createBy || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="timeline-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Clock /></el-icon>
          <span>处理流程</span>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="(log, index) in processLogs"
          :key="index"
          :timestamp="log.time"
          :type="getTimelineType(log.status)"
          :hollow="index === processLogs.length - 1"
        >
          <div class="timeline-content">
            <div class="timeline-header">
              <span class="timeline-title">{{ log.title }}</span>
              <el-tag :type="getStatusTagType(log.status)" size="small" effect="dark">
                {{ getStatusName(log.status) }}
              </el-tag>
            </div>
            <div class="timeline-body">
              <p v-if="log.content" class="log-content">{{ log.content }}</p>
              <div v-if="log.reasonAnalysis" class="log-section">
                <span class="log-label">原因分析：</span>
                <span>{{ log.reasonAnalysis }}</span>
              </div>
              <div v-if="log.handlingMeasures" class="log-section">
                <span class="log-label">处理措施：</span>
                <span>{{ log.handlingMeasures }}</span>
              </div>
              <div v-if="log.reviewOpinion" class="log-section">
                <span class="log-label">审核意见：</span>
                <span>{{ log.reviewOpinion }}</span>
              </div>
              <div v-if="log.finalConclusion" class="log-section">
                <span class="log-label">最终结论：</span>
                <span>{{ log.finalConclusion }}</span>
              </div>
              <div v-if="log.operator" class="log-meta">
                <span>操作人：{{ log.operator }}</span>
              </div>
              <div v-if="log.attachments && log.attachments.length" class="log-attachments">
                <span class="log-label">附件：</span>
                <el-link
                  v-for="(file, fileIndex) in log.attachments"
                  :key="fileIndex"
                  type="primary"
                  :underline="false"
                  class="attachment-link"
                >
                  <el-icon><Paperclip /></el-icon>
                  {{ file.name }}
                </el-link>
              </div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-card class="action-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Operation /></el-icon>
          <span>操作区域</span>
        </div>
      </template>

      <div v-if="oosData.status === 0" class="action-section">
        <h4>启动调查</h4>
        <el-form :model="startForm" label-width="100px" class="action-form">
          <el-form-item label="调查人员">
            <el-select v-model="startForm.investigator" placeholder="请选择调查人员" style="width: 300px">
              <el-option label="张三" value="张三" />
              <el-option label="李四" value="李四" />
              <el-option label="王五" value="王五" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="startForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="VideoPlay" @click="handleStartInvestigation" :loading="submitting">
              启动调查
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-else-if="oosData.status === 1" class="action-section">
        <h4>完成调查</h4>
        <el-form :model="completeForm" label-width="100px" class="action-form">
          <el-form-item label="原因分析" required>
            <el-input v-model="completeForm.reasonAnalysis" type="textarea" :rows="4" placeholder="请详细描述超标原因分析" />
          </el-form-item>
          <el-form-item label="处理措施" required>
            <el-input v-model="completeForm.handlingMeasures" type="textarea" :rows="4" placeholder="请描述采取的处理措施" />
          </el-form-item>
          <el-form-item label="附件">
            <el-upload
              v-model:file-list="completeForm.attachments"
              action="#"
              :auto-upload="false"
              multiple
            >
              <el-button type="primary" :icon="Plus">上传附件</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Check" @click="handleCompleteInvestigation" :loading="submitting">
              完成调查
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-else-if="oosData.status === 2" class="action-section">
        <h4>审核</h4>
        <el-alert type="info" :closable="false" class="alert-info">
          <p><strong>原因分析：</strong>{{ oosData.reasonAnalysis }}</p>
          <p><strong>处理措施：</strong>{{ oosData.handlingMeasures }}</p>
        </el-alert>
        <el-form :model="reviewForm" label-width="100px" class="action-form" style="margin-top: 16px">
          <el-form-item label="审核意见" required>
            <el-input v-model="reviewForm.reviewOpinion" type="textarea" :rows="3" placeholder="请输入审核意见" />
          </el-form-item>
          <el-form-item>
            <el-button type="success" :icon="Check" @click="handleReview(true)" :loading="submitting">
              审核通过
            </el-button>
            <el-button type="danger" :icon="Close" @click="handleReview(false)" :loading="submitting">
              审核驳回
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-else-if="oosData.status === 3" class="action-section">
        <h4>关闭OOS</h4>
        <el-form :model="closeForm" label-width="100px" class="action-form">
          <el-form-item label="最终结论" required>
            <el-input v-model="closeForm.finalConclusion" type="textarea" :rows="4" placeholder="请输入最终结论" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="SwitchButton" @click="handleClose" :loading="submitting">
              关闭OOS
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-else-if="oosData.status === 4" class="action-section">
        <el-result icon="success" title="OOS已关闭" sub-title="该OOS事件已处理完毕并关闭">
          <template #extra>
            <el-button type="primary" @click="handleBack">返回列表</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { oosRecordApi } from '@/api/detection'
import {
  Warning,
  Clock,
  Paperclip,
  Operation,
  VideoPlay,
  Check,
  Close,
  Plus,
  SwitchButton
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const id = route.params.id
const submitting = ref(false)

const oosData = ref({})
const processLogs = ref([])

const startForm = reactive({
  investigator: '',
  remark: ''
})

const completeForm = reactive({
  reasonAnalysis: '',
  handlingMeasures: '',
  attachments: []
})

const reviewForm = reactive({
  reviewOpinion: ''
})

const closeForm = reactive({
  finalConclusion: ''
})

const statusConfig = {
  0: { name: '待调查', type: 'warning', timelineType: 'warning' },
  1: { name: '调查中', type: 'primary', timelineType: 'primary' },
  2: { name: '调查完成', type: 'info', timelineType: '' },
  3: { name: '审核中', type: 'warning', timelineType: 'warning' },
  4: { name: '已关闭', type: 'success', timelineType: 'success' }
}

const levelConfig = {
  1: { name: '轻微', type: 'success' },
  2: { name: '一般', type: 'warning' },
  3: { name: '严重', type: 'danger' }
}

const getStatusName = (status) => {
  return statusConfig[status]?.name || status
}

const getStatusTagType = (status) => {
  return statusConfig[status]?.type || 'info'
}

const getTimelineType = (status) => {
  return statusConfig[status]?.timelineType || ''
}

const getLevelName = (level) => {
  return levelConfig[level]?.name || level
}

const getLevelTagType = (level) => {
  return levelConfig[level]?.type || 'info'
}

const fetchDetail = async () => {
  try {
    const res = await oosRecordApi.get(id)
    oosData.value = res.data || {}
    buildProcessLogs()
  } catch (error) {
    console.error('获取OOS详情失败:', error)
  }
}

const buildProcessLogs = () => {
  const logs = []
  const data = oosData.value

  logs.push({
    status: 0,
    title: 'OOS创建',
    content: '检测结果超出标准限值，系统自动生成OOS记录',
    time: data.createTime,
    operator: data.createBy
  })

  if (data.investigator) {
    logs.push({
      status: 1,
      title: '启动调查',
      content: `已指派 ${data.investigator} 进行调查${data.investigationRemark ? `，备注：${data.investigationRemark}` : ''}`,
      time: data.investigationStartTime,
      operator: data.investigationStartBy
    })
  }

  if (data.reasonAnalysis) {
    logs.push({
      status: 2,
      title: '完成调查',
      reasonAnalysis: data.reasonAnalysis,
      handlingMeasures: data.handlingMeasures,
      time: data.investigationCompleteTime,
      operator: data.investigationCompleteBy,
      attachments: data.investigationAttachments
    })
  }

  if (data.reviewOpinion) {
    logs.push({
      status: 3,
      title: `审核${data.reviewPassed ? '通过' : '驳回'}`,
      reviewOpinion: data.reviewOpinion,
      time: data.reviewTime,
      operator: data.reviewBy
    })
  }

  if (data.finalConclusion) {
    logs.push({
      status: 4,
      title: 'OOS关闭',
      finalConclusion: data.finalConclusion,
      time: data.closeTime,
      operator: data.closeBy
    })
  }

  processLogs.value = logs
}

const handleStartInvestigation = async () => {
  if (!startForm.investigator) {
    ElMessage.warning('请选择调查人员')
    return
  }
  try {
    await ElMessageBox.confirm('确定要启动调查吗？', '确认', { type: 'warning' })
    submitting.value = true
    await oosRecordApi.startInvestigation({
      id,
      investigator: startForm.investigator,
      remark: startForm.remark
    })
    ElMessage.success('调查已启动')
    fetchDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启动调查失败:', error)
    }
  } finally {
    submitting.value = false
  }
}

const handleCompleteInvestigation = async () => {
  if (!completeForm.reasonAnalysis || !completeForm.handlingMeasures) {
    ElMessage.warning('请填写原因分析和处理措施')
    return
  }
  try {
    await ElMessageBox.confirm('确定要完成调查吗？提交后将进入审核流程', '确认', { type: 'warning' })
    submitting.value = true
    await oosRecordApi.completeInvestigation({
      id,
      reasonAnalysis: completeForm.reasonAnalysis,
      handlingMeasures: completeForm.handlingMeasures
    })
    ElMessage.success('调查已完成')
    fetchDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成调查失败:', error)
    }
  } finally {
    submitting.value = false
  }
}

const handleReview = async (passed) => {
  if (!reviewForm.reviewOpinion) {
    ElMessage.warning('请输入审核意见')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要审核${passed ? '通过' : '驳回'}吗？`,
      '确认',
      { type: passed ? 'success' : 'warning' }
    )
    submitting.value = true
    await oosRecordApi.review({
      id,
      passed,
      reviewOpinion: reviewForm.reviewOpinion
    })
    ElMessage.success(`审核${passed ? '通过' : '驳回'}成功`)
    fetchDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
    }
  } finally {
    submitting.value = false
  }
}

const handleClose = async () => {
  if (!closeForm.finalConclusion) {
    ElMessage.warning('请输入最终结论')
    return
  }
  try {
    await ElMessageBox.confirm('确定要关闭该OOS吗？关闭后将无法修改', '确认', { type: 'warning' })
    submitting.value = true
    await oosRecordApi.close({
      id,
      finalConclusion: closeForm.finalConclusion
    })
    ElMessage.success('OOS已关闭')
    fetchDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('关闭OOS失败:', error)
    }
  } finally {
    submitting.value = false
  }
}

const handleBack = () => {
  router.push('/detection/oos')
}

onMounted(() => {
  fetchDetail()
})
</script>

<style lang="scss" scoped>
.oos-record-edit-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  margin-bottom: 0;
}

.info-card {
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;

    .status-tag {
      margin-left: auto;
    }
  }

  .highlight {
    color: #E6A23C;
    font-weight: 600;
  }

  .deviation-rate {
    color: #F56C6C;
    font-weight: 600;

    &.blink {
      animation: blink 1s infinite;
    }
  }
}

.timeline-card {
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }

  .timeline-content {
    .timeline-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;

      .timeline-title {
        font-weight: 600;
        font-size: 15px;
        color: #303133;
      }
    }

    .timeline-body {
      .log-content {
        margin: 0 0 8px 0;
        color: #606266;
      }

      .log-section {
        margin-bottom: 6px;
        color: #606266;

        .log-label {
          color: #909399;
        }
      }

      .log-meta {
        margin-top: 8px;
        color: #909399;
        font-size: 13px;
      }

      .log-attachments {
        margin-top: 8px;

        .log-label {
          color: #909399;
          margin-right: 8px;
        }

        .attachment-link {
          margin-right: 16px;
        }
      }
    }
  }
}

.action-card {
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }

  .action-section {
    h4 {
      margin: 0 0 16px 0;
      font-size: 16px;
      color: #303133;
    }

    .action-form {
      max-width: 600px;
    }

    .alert-info {
      p {
        margin: 4px 0;
        line-height: 1.6;
      }
    }
  }
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
}
</style>
