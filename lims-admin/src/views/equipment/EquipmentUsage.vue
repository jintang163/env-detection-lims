<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">设备使用记录</div>
        <div class="page-desc">管理设备的使用记录，追踪设备运行状态和使用情况</div>
      </div>
    </div>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索设备编号、名称、使用人..."
          style="width: 260px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchStatus" placeholder="使用状态" clearable style="width: 140px">
          <el-option label="使用中" :value="0" />
          <el-option label="已结束" :value="1" />
          <el-option label="异常" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleStart">
          <el-icon><VideoPlay /></el-icon>
          开始使用
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
        <el-table-column prop="userName" label="使用人" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="durationMinutes" label="使用时长(分钟)" width="140">
          <template #default="{ row }">
            <span v-if="row.durationMinutes !== null">{{ row.durationMinutes }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sampleNo" label="样品编号" width="140" show-overflow-tooltip />
        <el-table-column prop="taskNo" label="任务编号" width="140" show-overflow-tooltip />
        <el-table-column label="运行状况" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.runningStatus === 1" type="success" effect="light" size="small">正常</el-tag>
            <el-tag v-else-if="row.runningStatus === 2" type="warning" effect="light" size="small">异常</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="使用状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              @click="handleEnd(row)"
            >
              结束使用
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </el-card>

    <el-dialog v-model="startDialogVisible" title="开始使用设备" width="600px" top="5vh">
      <el-form
        :model="startForm"
        :rules="startFormRules"
        ref="startFormRef"
        label-width="100px"
      >
        <el-form-item label="设备" prop="equipmentId">
          <el-select
            v-model="startForm.equipmentId"
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
        <el-form-item label="使用人" prop="userName">
          <el-input v-model="startForm.userName" placeholder="请输入使用人" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="样品编号" prop="sampleNo">
              <el-input v-model="startForm.sampleNo" placeholder="请输入样品编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务编号" prop="taskNo">
              <el-input v-model="startForm.taskNo" placeholder="请输入任务编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="startForm.startTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="startForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="startDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitStart">
          <el-icon><Check /></el-icon>
          开始使用
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="endDialogVisible" title="结束使用设备" width="600px" top="5vh">
      <el-form
        :model="endForm"
        :rules="endFormRules"
        ref="endFormRef"
        label-width="100px"
      >
        <el-form-item label="设备">
          <el-input :value="endForm.equipmentName + '(' + endForm.equipmentNo + ')'" disabled />
        </el-form-item>
        <el-form-item label="使用人">
          <el-input :value="endForm.userName" disabled />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="endForm.endTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="运行状况" prop="runningStatus">
          <el-radio-group v-model="endForm.runningStatus">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="异常说明" prop="exceptionRemark">
          <el-input
            v-model="endForm.exceptionRemark"
            type="textarea"
            :rows="3"
            placeholder="如有异常，请描述异常情况"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="endForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="endDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEnd">
          <el-icon><Check /></el-icon>
          确认结束
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, VideoPlay } from '@element-plus/icons-vue'
import { equipmentUsageApi, equipmentApi } from '@/api/equipment'

const loading = ref(false)
const searchKeyword = ref('')
const searchStatus = ref('')
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const startDialogVisible = ref(false)
const startFormRef = ref(null)
const startForm = ref({
  equipmentId: null,
  equipmentNo: '',
  equipmentName: '',
  userName: '',
  sampleNo: '',
  taskNo: '',
  startTime: '',
  remark: ''
})

const endDialogVisible = ref(false)
const endFormRef = ref(null)
const currentUsageId = ref(null)
const endForm = ref({
  equipmentNo: '',
  equipmentName: '',
  userName: '',
  endTime: '',
  runningStatus: 1,
  exceptionRemark: '',
  remark: ''
})

const equipmentList = ref([])

const startFormRules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  userName: [{ required: true, message: '请输入使用人', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }]
}

const endFormRules = {
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  runningStatus: [{ required: true, message: '请选择运行状况', trigger: 'change' }]
}

const getStatusTag = (status) => {
  const tags = { 0: 'warning', 1: 'success', 2: 'danger' }
  return tags[status] || 'info'
}

const getStatusName = (status) => {
  const names = { 0: '使用中', 1: '已结束', 2: '异常' }
  return names[status] || '未知'
}

const fetchEquipmentList = async () => {
  const res = await equipmentApi.available()
  if (res.code === 200) {
    equipmentList.value = res.data
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
      userName: searchKeyword.value,
      status: searchStatus.value
    }
    const res = await equipmentUsageApi.page(params)
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleEquipmentChange = (equipmentId) => {
  const equipment = equipmentList.value.find(e => e.id === equipmentId)
  if (equipment) {
    startForm.value.equipmentNo = equipment.equipmentNo
    startForm.value.equipmentName = equipment.equipmentName
  }
}

const handleStart = () => {
  startForm.value = {
    equipmentId: null,
    equipmentNo: '',
    equipmentName: '',
    userName: '',
    sampleNo: '',
    taskNo: '',
    startTime: '',
    remark: ''
  }
  startDialogVisible.value = true
}

const handleSubmitStart = async () => {
  if (!startFormRef.value) return
  try {
    await startFormRef.value.validate()
    const res = await equipmentUsageApi.start(startForm.value)
    if (res.code === 200) {
      ElMessage.success('开始使用成功')
      startDialogVisible.value = false
      fetchList()
      fetchEquipmentList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleEnd = (row) => {
  currentUsageId.value = row.id
  endForm.value = {
    equipmentNo: row.equipmentNo,
    equipmentName: row.equipmentName,
    userName: row.userName,
    endTime: '',
    runningStatus: 1,
    exceptionRemark: '',
    remark: ''
  }
  endDialogVisible.value = true
}

const handleSubmitEnd = async () => {
  if (!endFormRef.value) return
  try {
    await endFormRef.value.validate()
    const res = await equipmentUsageApi.end(currentUsageId.value, endForm.value)
    if (res.code === 200) {
      ElMessage.success('结束使用成功')
      endDialogVisible.value = false
      currentUsageId.value = null
      fetchList()
      fetchEquipmentList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该使用记录吗？', '提示', { type: 'warning' }).then(async () => {
    const res = await equipmentUsageApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
      fetchEquipmentList()
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchEquipmentList()
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

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}
</style>
