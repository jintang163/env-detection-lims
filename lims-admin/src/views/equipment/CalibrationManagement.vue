<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">校准管理</div>
        <div class="page-desc">管理设备的校准计划和校准记录，实现到期自动提醒</div>
      </div>
    </div>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="校准计划" name="plan">
          <div class="toolbar">
            <el-input
              v-model="planSearchKeyword"
              placeholder="搜索设备编号、名称..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchPlanList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="planSearchType" placeholder="校准类型" clearable style="width: 140px">
              <el-option label="校准" :value="1" />
              <el-option label="检定" :value="2" />
            </el-select>
            <el-select v-model="planSearchStatus" placeholder="计划状态" clearable style="width: 140px">
              <el-option label="待执行" :value="0" />
              <el-option label="已完成" :value="1" />
              <el-option label="已过期" :value="2" />
            </el-select>
            <el-switch
              v-model="planSearchUpcoming"
              active-text="即将到期"
              inactive-text="全部"
              style="margin-left: 8px"
            />
            <el-button type="primary" @click="handleAddPlan">
              <el-icon><Plus /></el-icon>
              新增计划
            </el-button>
            <el-button type="warning" @click="handleCheckStatus">
              <el-icon><RefreshRight /></el-icon>
              检查过期
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
            <el-table-column prop="equipmentNo" label="设备编号" width="140" />
            <el-table-column prop="equipmentName" label="设备名称" width="180" />
            <el-table-column label="校准类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.calibrationType === 1 ? 'primary' : 'success'" effect="light" size="small">
                  {{ row.calibrationTypeName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="cycleMonths" label="周期(月)" width="100" />
            <el-table-column prop="lastCalibrationDate" label="上次校准" width="120" />
            <el-table-column prop="nextCalibrationDate" label="下次校准" width="120">
              <template #default="{ row }">
                <span :class="{ 'text-warning': row.upcoming, 'text-danger': row.daysUntilDue < 0 }">
                  {{ row.nextCalibrationDate }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="剩余天数" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.daysUntilDue < 0" type="danger" size="small">已过期{{ Math.abs(row.daysUntilDue) }}天</el-tag>
                <el-tag v-else-if="row.upcoming" type="warning" size="small">{{ row.daysUntilDue }}天</el-tag>
                <el-tag v-else type="info" size="small">{{ row.daysUntilDue }}天</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remindDays" label="提醒天数" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getPlanStatusTag(row.status)" effect="light" size="small">
                  {{ row.statusName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="managerName" label="负责人" width="100" />
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button type="success" link @click="handleAddRecord(row)">添加记录</el-button>
                <el-button type="primary" link @click="handleEditPlan(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeletePlan(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="planPagination.pageNum"
            v-model:page-size="planPagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="planPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchPlanList"
            @current-change="fetchPlanList"
          />
        </el-tab-pane>

        <el-tab-pane label="校准记录" name="record">
          <div class="toolbar">
            <el-input
              v-model="recordSearchKeyword"
              placeholder="搜索设备编号、名称、证书编号..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchRecordList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="recordSearchType" placeholder="校准类型" clearable style="width: 140px">
              <el-option label="校准" :value="1" />
              <el-option label="检定" :value="2" />
            </el-select>
            <el-select v-model="recordSearchResult" placeholder="校准结果" clearable style="width: 140px">
              <el-option label="合格" :value="1" />
              <el-option label="不合格" :value="2" />
              <el-option label="部分合格" :value="3" />
            </el-select>
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
            <el-table-column prop="equipmentNo" label="设备编号" width="140" />
            <el-table-column prop="equipmentName" label="设备名称" width="180" />
            <el-table-column label="校准类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.calibrationType === 1 ? 'primary' : 'success'" effect="light" size="small">
                  {{ row.calibrationTypeName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="calibrationDate" label="校准日期" width="120" />
            <el-table-column prop="calibrationUnit" label="校准单位" width="160" show-overflow-tooltip />
            <el-table-column prop="certificateNo" label="证书编号" width="160" />
            <el-table-column label="结果" width="100">
              <template #default="{ row }">
                <el-tag :type="getResultTag(row.result)" effect="light" size="small">
                  {{ row.resultName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="validUntil" label="有效期至" width="120" />
            <el-table-column prop="cost" label="费用(元)" width="100" />
            <el-table-column prop="calibratorName" label="校准人" width="100" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleViewRecord(row)">详情</el-button>
                <el-button type="danger" link @click="handleDeleteRecord(row)">删除</el-button>
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
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="planDialogVisible" :title="planDialogTitle" width="700px" top="5vh">
      <el-form
        :model="planForm"
        :rules="planFormRules"
        ref="planFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备" prop="equipmentId">
              <el-select
                v-model="planForm.equipmentId"
                placeholder="请选择设备"
                style="width: 100%"
                @change="handleEquipmentChange"
              >
                <el-option
                  v-for="item in equipmentList"
                  :key="item.id"
                  :label="item.equipmentName + '(' + item.equipmentNo + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="校准类型" prop="calibrationType">
              <el-select v-model="planForm.calibrationType" placeholder="请选择" style="width: 100%">
                <el-option label="校准" :value="1" />
                <el-option label="检定" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="周期(月)" prop="cycleMonths">
              <el-input-number v-model="planForm.cycleMonths" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下次校准日期" prop="nextCalibrationDate">
              <el-date-picker
                v-model="planForm.nextCalibrationDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="提醒天数" prop="remindDays">
              <el-input-number v-model="planForm.remindDays" :min="1" :max="365" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="上次校准日期" prop="lastCalibrationDate">
              <el-date-picker
                v-model="planForm.lastCalibrationDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="managerName">
              <el-input v-model="planForm.managerName" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="planForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="planDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSubmitPlan">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recordDialogVisible" title="添加校准记录" width="700px" top="5vh">
      <el-form
        :model="recordForm"
        :rules="recordFormRules"
        ref="recordFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备" prop="equipmentId">
              <el-select
                v-model="recordForm.equipmentId"
                placeholder="请选择设备"
                style="width: 100%"
                :disabled="!!selectedPlan"
              >
                <el-option
                  v-for="item in equipmentList"
                  :key="item.id"
                  :label="item.equipmentName + '(' + item.equipmentNo + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="校准类型" prop="calibrationType">
              <el-select v-model="recordForm.calibrationType" placeholder="请选择" style="width: 100%">
                <el-option label="校准" :value="1" />
                <el-option label="检定" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="校准日期" prop="calibrationDate">
              <el-date-picker
                v-model="recordForm.calibrationDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期至" prop="validUntil">
              <el-date-picker
                v-model="recordForm.validUntil"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="校准结果" prop="result">
              <el-select v-model="recordForm.result" placeholder="请选择" style="width: 100%">
                <el-option label="合格" :value="1" />
                <el-option label="不合格" :value="2" />
                <el-option label="部分合格" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="校准单位" prop="calibrationUnit">
              <el-input v-model="recordForm.calibrationUnit" placeholder="请输入校准单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证书编号" prop="certificateNo">
              <el-input v-model="recordForm.certificateNo" placeholder="请输入证书编号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="校准人" prop="calibratorName">
              <el-input v-model="recordForm.calibratorName" placeholder="请输入校准人" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="费用(元)" prop="cost">
              <el-input-number v-model="recordForm.cost" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="关联计划" prop="planId">
              <el-select v-model="recordForm.planId" placeholder="可关联校准计划" style="width: 100%" clearable>
                <el-option
                  v-for="item in planList"
                  :key="item.id"
                  :label="'计划-' + item.id"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="校准内容" prop="calibrationContent">
          <el-input
            v-model="recordForm.calibrationContent"
            type="textarea"
            :rows="2"
            placeholder="请输入校准内容"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="recordForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="recordDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSubmitRecord">
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
import { Search, Plus, Refresh, Check, RefreshRight } from '@element-plus/icons-vue'
import { calibrationApi, equipmentApi } from '@/api/equipment'

const activeTab = ref('plan')

const planLoading = ref(false)
const planSearchKeyword = ref('')
const planSearchType = ref('')
const planSearchStatus = ref('')
const planSearchUpcoming = ref(false)
const planTableData = ref([])
const planPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const recordLoading = ref(false)
const recordSearchKeyword = ref('')
const recordSearchType = ref('')
const recordSearchResult = ref('')
const recordTableData = ref([])
const recordPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const planDialogVisible = ref(false)
const planDialogTitle = ref('')
const planFormRef = ref(null)
const planForm = ref({
  id: null,
  equipmentId: null,
  equipmentNo: '',
  equipmentName: '',
  calibrationType: 1,
  cycleMonths: 12,
  lastCalibrationDate: '',
  nextCalibrationDate: '',
  remindDays: 30,
  managerId: null,
  managerName: '',
  remark: ''
})

const recordDialogVisible = ref(false)
const recordFormRef = ref(null)
const selectedPlan = ref(null)
const recordForm = ref({
  id: null,
  equipmentId: null,
  equipmentNo: '',
  equipmentName: '',
  planId: null,
  calibrationType: 1,
  calibrationDate: '',
  calibrationUnit: '',
  certificateNo: '',
  result: 1,
  validUntil: '',
  calibratorId: null,
  calibratorName: '',
  cost: null,
  calibrationContent: '',
  remark: ''
})

const equipmentList = ref([])
const planList = ref([])

const planFormRules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  calibrationType: [{ required: true, message: '请选择校准类型', trigger: 'change' }],
  cycleMonths: [{ required: true, message: '请输入校准周期', trigger: 'blur' }],
  nextCalibrationDate: [{ required: true, message: '请选择下次校准日期', trigger: 'change' }]
}

const recordFormRules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  calibrationType: [{ required: true, message: '请选择校准类型', trigger: 'change' }],
  calibrationDate: [{ required: true, message: '请选择校准日期', trigger: 'change' }],
  calibrationUnit: [{ required: true, message: '请输入校准单位', trigger: 'blur' }],
  result: [{ required: true, message: '请选择校准结果', trigger: 'change' }]
}

const getPlanStatusTag = (status) => {
  const tags = { 0: 'warning', 1: 'success', 2: 'danger' }
  return tags[status] || 'info'
}

const getResultTag = (result) => {
  const tags = { 1: 'success', 2: 'danger', 3: 'warning' }
  return tags[result] || 'info'
}

const fetchEquipmentList = async () => {
  const res = await equipmentApi.available()
  if (res.code === 200) {
    equipmentList.value = res.data
  }
}

const fetchPlanList = async () => {
  planLoading.value = true
  try {
    const params = {
      pageNum: planPagination.pageNum,
      pageSize: planPagination.pageSize,
      equipmentName: planSearchKeyword.value,
      equipmentNo: planSearchKeyword.value,
      calibrationType: planSearchType.value,
      status: planSearchStatus.value,
      upcoming: planSearchUpcoming.value
    }
    const res = await calibrationApi.planPage(params)
    if (res.code === 200) {
      planTableData.value = res.data.list
      planPagination.total = res.data.total
    }
  } finally {
    planLoading.value = false
  }
}

const fetchRecordList = async () => {
  recordLoading.value = true
  try {
    const params = {
      pageNum: recordPagination.pageNum,
      pageSize: recordPagination.pageSize,
      equipmentName: recordSearchKeyword.value,
      equipmentNo: recordSearchKeyword.value,
      certificateNo: recordSearchKeyword.value,
      calibrationType: recordSearchType.value,
      result: recordSearchResult.value
    }
    const res = await calibrationApi.recordPage(params)
    if (res.code === 200) {
      recordTableData.value = res.data.list
      recordPagination.total = res.data.total
    }
  } finally {
    recordLoading.value = false
  }
}

const handleEquipmentChange = (equipmentId) => {
  const equipment = equipmentList.value.find(e => e.id === equipmentId)
  if (equipment) {
    planForm.value.equipmentNo = equipment.equipmentNo
    planForm.value.equipmentName = equipment.equipmentName
    recordForm.value.equipmentNo = equipment.equipmentNo
    recordForm.value.equipmentName = equipment.equipmentName
  }
}

const handleAddPlan = () => {
  planDialogTitle.value = '新增校准计划'
  planForm.value = {
    id: null,
    equipmentId: null,
    equipmentNo: '',
    equipmentName: '',
    calibrationType: 1,
    cycleMonths: 12,
    lastCalibrationDate: '',
    nextCalibrationDate: '',
    remindDays: 30,
    managerId: null,
    managerName: '',
    remark: ''
  }
  planDialogVisible.value = true
}

const handleEditPlan = (row) => {
  planDialogTitle.value = '编辑校准计划'
  planForm.value = { ...row }
  planDialogVisible.value = true
}

const handleDeletePlan = (row) => {
  ElMessageBox.confirm('确定要删除该校准计划吗？', '提示', { type: 'warning' }).then(async () => {
    const res = await calibrationApi.planDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchPlanList()
    }
  }).catch(() => {})
}

const handleSubmitPlan = async () => {
  if (!planFormRef.value) return
  try {
    await planFormRef.value.validate()
    let res
    if (planForm.value.id) {
      res = await calibrationApi.planUpdate(planForm.value)
    } else {
      res = await calibrationApi.planSave(planForm.value)
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

const handleAddRecord = (row) => {
  selectedPlan.value = row
  recordForm.value = {
    id: null,
    equipmentId: row.equipmentId,
    equipmentNo: row.equipmentNo,
    equipmentName: row.equipmentName,
    planId: row.id,
    calibrationType: row.calibrationType,
    calibrationDate: '',
    calibrationUnit: '',
    certificateNo: '',
    result: 1,
    validUntil: '',
    calibratorId: null,
    calibratorName: '',
    cost: null,
    calibrationContent: '',
    remark: ''
  }
  recordDialogVisible.value = true
}

const handleViewRecord = (row) => {
  ElMessage.info('查看详情功能开发中...')
}

const handleDeleteRecord = (row) => {
  ElMessageBox.confirm('确定要删除该校准记录吗？', '提示', { type: 'warning' }).then(async () => {
    const res = await calibrationApi.recordDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchRecordList()
    }
  }).catch(() => {})
}

const handleSubmitRecord = async () => {
  if (!recordFormRef.value) return
  try {
    await recordFormRef.value.validate()
    const res = await calibrationApi.recordSave(recordForm.value)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      recordDialogVisible.value = false
      selectedPlan.value = null
      fetchPlanList()
      fetchRecordList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleCheckStatus = async () => {
  const res = await calibrationApi.planCheckStatus()
  if (res.code === 200) {
    ElMessage.success('状态检查完成，已更新过期计划')
    fetchPlanList()
  }
}

onMounted(() => {
  fetchEquipmentList()
  fetchPlanList()
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

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.text-warning {
  color: #e6a23c;
}

.text-danger {
  color: #f56c6c;
}
</style>
