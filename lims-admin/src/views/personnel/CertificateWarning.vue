<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">证书到期预警</div>
        <div class="page-desc">管理和处理人员证书到期预警信息，确保证书及时更新</div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">预警总数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">即将到期</div>
              <div class="stat-value">{{ stats.upcoming || 0 }}</div>
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
              <div class="stat-label">已过期</div>
              <div class="stat-value">{{ stats.expired || 0 }}</div>
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
              <div class="stat-label">已处理</div>
              <div class="stat-value">{{ stats.processed || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索人员姓名、证书名称..."
          style="width: 260px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchWarningType" placeholder="预警类型" clearable style="width: 140px">
          <el-option label="即将到期" value="即将到期" />
          <el-option label="已过期" value="已过期" />
        </el-select>
        <el-select v-model="searchWarningStatus" placeholder="处理状态" clearable style="width: 140px">
          <el-option label="未处理" :value="0" />
          <el-option label="已处理" :value="1" />
          <el-option label="已忽略" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleCheckExpiry">
          <el-icon><RefreshRight /></el-icon>
          检查证书到期
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
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="personnelName" label="人员姓名" width="120" />
        <el-table-column prop="certificateType" label="证书类型" width="120" />
        <el-table-column prop="certificateName" label="证书名称" width="180" show-overflow-tooltip />
        <el-table-column prop="certificateNo" label="证书编号" width="140" />
        <el-table-column prop="expiryDate" label="有效期至" width="120" />
        <el-table-column label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.warningType === '已过期' ? 'danger' : 'warning'" effect="light" size="small">
              {{ row.warningType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="剩余天数" width="100" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.warningDays < 0, 'text-warning': row.warningDays >= 0 && row.warningDays <= 30 }">
              {{ row.warningDays < 0 ? '已过期' + Math.abs(row.warningDays) + '天' : row.warningDays + '天' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getWarningStatusTag(row.warningStatus)" effect="light" size="small">
              {{ getWarningStatusText(row.warningStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="processResult" label="处理结果" min-width="150" show-overflow-tooltip />
        <el-table-column prop="processUserName" label="处理人" width="100" />
        <el-table-column prop="processTime" label="处理时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewCertificate(row.certificateId)">查看证书</el-button>
            <el-button 
              v-if="row.warningStatus === 0" 
              type="success" 
              link 
              @click="handleProcess(row)"
            >处理</el-button>
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

    <el-dialog v-model="processDialogVisible" title="处理预警" width="600px" top="20vh">
      <el-form
        :model="processForm"
        :rules="processFormRules"
        ref="processFormRef"
        label-width="100px"
      >
        <el-form-item label="预警类型">
          <el-tag :type="processForm.warningType === '已过期' ? 'danger' : 'warning'" effect="light">
            {{ processForm.warningType }}
          </el-tag>
        </el-form-item>
        <el-form-item label="人员姓名">
          <span>{{ processForm.personnelName }}</span>
        </el-form-item>
        <el-form-item label="证书名称">
          <span>{{ processForm.certificateName }}</span>
        </el-form-item>
        <el-form-item label="有效期至">
          <span>{{ processForm.expiryDate }}</span>
        </el-form-item>
        <el-form-item label="处理结果" prop="processResult">
          <el-select v-model="processForm.processResult" placeholder="请选择处理结果" style="width: 100%">
            <el-option label="已更新证书" value="已更新证书" />
            <el-option label="已申请延期" value="已申请延期" />
            <el-option label="已离职，无需处理" value="已离职，无需处理" />
            <el-option label="忽略此预警" value="忽略此预警" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess">
          <el-icon><Check /></el-icon>
          确认处理
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Clock, Warning, CircleCheck, Search, RefreshRight, Refresh, Check } from '@element-plus/icons-vue'
import { trainingApi } from '@/api/personnel'

const loading = ref(false)
const processDialogVisible = ref(false)
const processFormRef = ref(null)

const searchKeyword = ref('')
const searchWarningType = ref('')
const searchWarningStatus = ref('')

const stats = reactive({
  total: 0,
  upcoming: 0,
  expired: 0,
  processed: 0
})

const tableData = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const processForm = reactive({
  id: null,
  warningType: '',
  personnelName: '',
  certificateName: '',
  expiryDate: '',
  processResult: ''
})

const processFormRules = {
  processResult: [{ required: true, message: '请选择处理结果', trigger: 'change' }]
}

const getWarningStatusTag = (status) => {
  const tags = { 0: 'warning', 1: 'success', 2: 'info' }
  return tags[status] || 'info'
}

const getWarningStatusText = (status) => {
  const texts = { 0: '未处理', 1: '已处理', 2: '已忽略' }
  return texts[status] || '未知'
}

const tableRowClassName = ({ row }) => {
  if (row.warningStatus === 1) return 'row-processed'
  if (row.warningType === '已过期') return 'row-expired'
  return ''
}

const fetchStats = async () => {
  try {
    const res = await trainingApi.warningPage({ pageNum: 1, pageSize: 1000 })
    if (res.code === 200) {
      const list = res.data.list || []
      stats.total = list.length
      stats.upcoming = list.filter(item => item.warningType === '即将到期').length
      stats.expired = list.filter(item => item.warningType === '已过期').length
      stats.processed = list.filter(item => item.warningStatus === 1).length
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
      warningStatus: searchWarningStatus.value !== '' ? searchWarningStatus.value : undefined,
      warningType: searchWarningType.value || undefined,
      keyword: searchKeyword.value || undefined
    }
    const res = await trainingApi.warningPage(params)
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleCheckExpiry = async () => {
  try {
    const res = await trainingApi.checkCertificateExpiry()
    if (res.code === 200) {
      ElMessage.success('证书到期检查完成，已生成最新预警信息')
      fetchStats()
      fetchList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleViewCertificate = (certificateId) => {
  ElMessage.info(`查看证书详情，证书ID: ${certificateId}`)
}

const handleProcess = (row) => {
  processForm.id = row.id
  processForm.warningType = row.warningType
  processForm.personnelName = row.personnelName
  processForm.certificateName = row.certificateName
  processForm.expiryDate = row.expiryDate
  processForm.processResult = ''
  processDialogVisible.value = true
}

const submitProcess = async () => {
  if (!processFormRef.value) return
  try {
    await processFormRef.value.validate()
    const res = await trainingApi.warningProcess({
      warningId: processForm.id,
      processResult: processForm.processResult
    })
    if (res.code === 200) {
      ElMessage.success('处理成功')
      processDialogVisible.value = false
      fetchStats()
      fetchList()
    }
  } catch (e) {
    console.error(e)
  }
}

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

.text-danger {
  color: #f56c6c;
  font-weight: 600;
}

.text-warning {
  color: #e6a23c;
  font-weight: 600;
}

:deep(.row-expired) {
  background-color: #fef0f0 !important;
}

:deep(.row-processed) {
  background-color: #f0f9eb !important;
}
</style>
