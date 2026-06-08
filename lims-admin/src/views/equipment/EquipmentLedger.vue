<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">设备台账</div>
        <div class="page-desc">管理仪器设备的基础信息、状态、校准、使用和维护记录</div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Cpu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">设备总数</div>
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
              <div class="stat-label">正常在用</div>
              <div class="stat-value">{{ stats.inUse || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">维修中</div>
              <div class="stat-value">{{ stats.repairing || 0 }}</div>
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
              <div class="stat-label">校准即将到期</div>
              <div class="stat-value">{{ stats.needCalibration || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索设备编号、名称、型号..."
          style="width: 260px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchType" placeholder="设备类型" clearable style="width: 140px">
          <el-option label="采样设备" value="1" />
          <el-option label="监测设备" value="2" />
          <el-option label="样品容器" value="3" />
          <el-option label="其他设备" value="4" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="设备状态" clearable style="width: 140px">
          <el-option label="闲置" :value="0" />
          <el-option label="在用" :value="1" />
          <el-option label="维修中" :value="2" />
          <el-option label="停用" :value="3" />
          <el-option label="报废" :value="4" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增设备
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
        <el-table-column prop="equipmentNo" label="设备编号" width="140" />
        <el-table-column prop="equipmentName" label="设备名称" width="180" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="specification" label="规格" width="140" />
        <el-table-column label="设备类型" width="110">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.equipmentType)" effect="light" size="small">
              {{ getTypeText(row.equipmentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="领用状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.borrowStatus === 1 ? 'warning' : 'info'" effect="light" size="small">
              {{ row.borrowStatus === 1 ? '已领用' : '未领用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="manufacturer" label="生产厂家" width="140" show-overflow-tooltip />
        <el-table-column prop="managerName" label="管理员" width="100" />
        <el-table-column prop="deptName" label="所属部门" width="120" />
        <el-table-column prop="nextCalibrationDate" label="下次校准日期" width="130" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="900px" top="5vh">
      <el-tabs v-model="activeTab" v-if="isView">
        <el-tab-pane label="基本信息" name="basic" />
        <el-tab-pane label="校准计划" name="calibration" />
        <el-tab-pane label="校准记录" name="calibrationRecord" />
        <el-tab-pane label="使用记录" name="usage" />
        <el-tab-pane label="维护记录" name="maintenance" />
        <el-tab-pane label="维修申请" name="repair" />
      </el-tabs>

      <div v-if="activeTab === 'basic' || !isView">
        <el-form
          :model="equipmentForm"
          :rules="formRules"
          ref="equipmentFormRef"
          label-width="100px"
          v-loading="detailLoading"
        >
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="设备编号" prop="equipmentNo">
                <el-input v-model="equipmentForm.equipmentNo" placeholder="请输入设备编号" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="设备名称" prop="equipmentName">
                <el-input v-model="equipmentForm.equipmentName" placeholder="请输入设备名称" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="设备型号" prop="model">
                <el-input v-model="equipmentForm.model" placeholder="请输入型号" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="规格" prop="specification">
                <el-input v-model="equipmentForm.specification" placeholder="请输入规格" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="设备类型" prop="equipmentType">
                <el-select v-model="equipmentForm.equipmentType" placeholder="请选择类型" :disabled="isView" style="width: 100%">
                  <el-option label="采样设备" value="1" />
                  <el-option label="监测设备" value="2" />
                  <el-option label="样品容器" value="3" />
                  <el-option label="其他设备" value="4" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="生产厂家" prop="manufacturer">
                <el-input v-model="equipmentForm.manufacturer" placeholder="请输入生产厂家" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="出厂编号" prop="factoryNo">
                <el-input v-model="equipmentForm.factoryNo" placeholder="请输入出厂编号" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="设备状态" prop="status">
                <el-select v-model="equipmentForm.status" placeholder="请选择状态" :disabled="isView" style="width: 100%">
                  <el-option label="闲置" :value="0" />
                  <el-option label="在用" :value="1" />
                  <el-option label="维修中" :value="2" />
                  <el-option label="停用" :value="3" />
                  <el-option label="报废" :value="4" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="所属部门" prop="deptName">
                <el-input v-model="equipmentForm.deptName" placeholder="请输入所属部门" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="管理员" prop="managerName">
                <el-input v-model="equipmentForm.managerName" placeholder="请输入管理员" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="存放位置" prop="storageLocation">
                <el-input v-model="equipmentForm.storageLocation" placeholder="请输入存放位置" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="供应商" prop="supplierName">
                <el-input v-model="equipmentForm.supplierName" placeholder="请输入供应商" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="购置日期" prop="purchaseDate">
                <el-date-picker
                  v-model="equipmentForm.purchaseDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="购置金额" prop="purchaseAmount">
                <el-input-number
                  v-model="equipmentForm.purchaseAmount"
                  :min="0"
                  :precision="2"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="校准周期(天)" prop="calibrationCycle">
                <el-input-number
                  v-model="equipmentForm.calibrationCycle"
                  :min="0"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="上次校准日期" prop="lastCalibrationDate">
                <el-date-picker
                  v-model="equipmentForm.lastCalibrationDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="下次校准日期" prop="nextCalibrationDate">
                <el-date-picker
                  v-model="equipmentForm.nextCalibrationDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="领用状态" prop="borrowStatus">
                <el-select v-model="equipmentForm.borrowStatus" placeholder="请选择领用状态" :disabled="isView" style="width: 100%">
                  <el-option label="未领用" :value="0" />
                  <el-option label="已领用" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="当前借用人ID" prop="currentBorrowerId">
                <el-input-number v-model="equipmentForm.currentBorrowerId" :min="1" style="width: 100%" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="当前借用人姓名" prop="currentBorrowerName">
                <el-input v-model="equipmentForm.currentBorrowerName" placeholder="请输入借用人姓名" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="技术参数" prop="technicalParams">
            <el-input
              v-model="equipmentForm.technicalParams"
              type="textarea"
              :rows="2"
              placeholder="请输入技术参数"
              :disabled="isView"
            />
          </el-form-item>

          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="equipmentForm.remark"
              type="textarea"
              :rows="2"
              placeholder="请输入备注"
              :disabled="isView"
            />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="isView && activeTab === 'calibration'">
        <el-table :data="detailData.calibrationPlans || []" border stripe style="width: 100%">
          <el-table-column prop="calibrationTypeName" label="校准类型" width="100" />
          <el-table-column prop="cycleMonths" label="周期(月)" width="100" />
          <el-table-column prop="nextCalibrationDate" label="下次校准日期" width="140" />
          <el-table-column label="是否即将到期" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.upcoming" type="warning" size="small">是</el-tag>
              <el-tag v-else type="info" size="small">否</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="daysUntilDue" label="剩余天数" width="100" />
          <el-table-column prop="statusName" label="状态" width="100" />
          <el-table-column prop="managerName" label="负责人" width="100" />
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'calibrationRecord'">
        <el-table :data="detailData.calibrationRecords || []" border stripe style="width: 100%">
          <el-table-column prop="calibrationTypeName" label="校准类型" width="100" />
          <el-table-column prop="calibrationDate" label="校准日期" width="120" />
          <el-table-column prop="calibrationUnit" label="校准单位" width="140" />
          <el-table-column prop="certificateNo" label="证书编号" width="140" />
          <el-table-column prop="resultName" label="结果" width="100" />
          <el-table-column prop="validUntil" label="有效期至" width="120" />
          <el-table-column prop="cost" label="费用(元)" width="100" />
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'usage'">
        <el-table :data="detailData.usageRecords || []" border stripe style="width: 100%">
          <el-table-column prop="userName" label="使用人" width="100" />
          <el-table-column prop="startTime" label="开始时间" width="160" />
          <el-table-column prop="endTime" label="结束时间" width="160" />
          <el-table-column prop="usageMinutes" label="使用时长(分钟)" width="130" />
          <el-table-column prop="sampleNo" label="样品编号" width="140" />
          <el-table-column prop="taskNo" label="任务编号" width="140" />
          <el-table-column prop="runningStatusName" label="运行状况" width="100" />
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'maintenance'">
        <el-table :data="detailData.maintenanceRecords || []" border stripe style="width: 100%">
          <el-table-column prop="maintenanceTypeName" label="维护类型" width="120" />
          <el-table-column prop="maintenanceDate" label="维护日期" width="120" />
          <el-table-column prop="maintenanceContent" label="维护内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="maintainerName" label="维护人" width="100" />
          <el-table-column prop="resultName" label="结果" width="100" />
          <el-table-column prop="nextMaintenanceDate" label="下次维护日期" width="140" />
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'repair'">
        <el-table :data="detailData.repairRequests || []" border stripe style="width: 100%">
          <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="urgencyName" label="紧急程度" width="100" />
          <el-table-column prop="statusName" label="状态" width="100" />
          <el-table-column prop="applicantName" label="申请人" width="100" />
          <el-table-column prop="applyTime" label="申请时间" width="160" />
          <el-table-column prop="repairResultName" label="维修结果" width="100" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Cpu, CircleCheck, Tools, Warning, Search, Plus, Refresh, Check } from '@element-plus/icons-vue'
import { equipmentApi } from '@/api/equipment'

const loading = ref(false)
const detailLoading = ref(false)
const dialogVisible = ref(false)
const isView = ref(false)
const dialogTitle = ref('')
const activeTab = ref('basic')

const searchKeyword = ref('')
const searchType = ref('')
const searchStatus = ref('')

const stats = reactive({
  total: 0,
  inUse: 0,
  repairing: 0,
  needCalibration: 0
})

const tableData = ref([])
const detailData = ref({})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const equipmentForm = ref({
  id: null,
  equipmentNo: '',
  equipmentName: '',
  model: '',
  specification: '',
  equipmentType: '',
  manufacturer: '',
  factoryNo: '',
  status: 0,
  deptId: null,
  deptName: '',
  managerId: null,
  managerName: '',
  storageLocation: '',
  supplierId: null,
  supplierName: '',
  purchaseDate: '',
  purchaseAmount: null,
  calibrationCycle: null,
  lastCalibrationDate: '',
  nextCalibrationDate: '',
  borrowStatus: 0,
  currentBorrowerId: null,
  currentBorrowerName: '',
  technicalParams: '',
  remark: ''
})

const formRules = {
  equipmentNo: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }]
}

const equipmentFormRef = ref(null)

const getTypeTag = (type) => {
  const tags = { '1': 'primary', '2': 'success', '3': 'warning', '4': 'info' }
  return tags[type] || 'info'
}

const getTypeText = (type) => {
  const texts = { '1': '采样设备', '2': '监测设备', '3': '样品容器', '4': '其他设备' }
  return texts[type] || '未知'
}

const getStatusTag = (status) => {
  const tags = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger', 4: 'info' }
  return tags[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '闲置', 1: '在用', 2: '维修中', 3: '停用', 4: '报废' }
  return texts[status] || '未知'
}

const fetchStats = async () => {
  try {
    const res = await equipmentApi.stats()
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
      equipmentName: searchKeyword.value,
      equipmentNo: searchKeyword.value,
      model: searchKeyword.value,
      equipmentType: searchType.value,
      status: searchStatus.value
    }
    const res = await equipmentApi.page(params)
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增设备'
  equipmentForm.value = {
    id: null,
    equipmentNo: '',
    equipmentName: '',
    model: '',
    specification: '',
    equipmentType: '',
    manufacturer: '',
    factoryNo: '',
    status: 0,
    deptId: null,
    deptName: '',
    managerId: null,
    managerName: '',
    storageLocation: '',
    supplierId: null,
    supplierName: '',
    purchaseDate: '',
    purchaseAmount: null,
    calibrationCycle: null,
    lastCalibrationDate: '',
    nextCalibrationDate: '',
    technicalParams: '',
    remark: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑设备'
  equipmentForm.value = { ...row }
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '设备详情'
  activeTab.value = 'basic'
  detailLoading.value = true
  try {
    const res = await equipmentApi.get(row.id)
    if (res.code === 200) {
      equipmentForm.value = { ...res.data }
      detailData.value = res.data
    }
  } finally {
    detailLoading.value = false
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除设备"${row.equipmentName}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await equipmentApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
      fetchStats()
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!equipmentFormRef.value) return
  try {
    await equipmentFormRef.value.validate()
    let res
    if (equipmentForm.value.id) {
      res = await equipmentApi.update(equipmentForm.value)
    } else {
      res = await equipmentApi.save(equipmentForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(equipmentForm.value.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      fetchList()
      fetchStats()
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
</style>
