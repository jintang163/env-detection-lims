<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">检测数据录入</div>
    </div>

    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
      <el-form-item label="记录编号">
        <el-input v-model="searchForm.recordNo" placeholder="请输入记录编号" clearable />
      </el-form-item>
      <el-form-item label="任务编号">
        <el-input v-model="searchForm.taskNo" placeholder="请输入任务编号" clearable />
      </el-form-item>
      <el-form-item label="样品名称">
        <el-input v-model="searchForm.sampleName" placeholder="请输入样品名称" clearable />
      </el-form-item>
      <el-form-item label="方法名称">
        <el-input v-model="searchForm.methodName" placeholder="请输入方法名称" clearable />
      </el-form-item>
      <el-form-item label="录入人">
        <el-input v-model="searchForm.createBy" placeholder="请输入录入人" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option label="草稿" :value="0" />
          <el-option label="待一级审核" :value="1" />
          <el-option label="一级审核通过" :value="2" />
          <el-option label="待二级审核" :value="3" />
          <el-option label="二级审核通过" :value="4" />
          <el-option label="已驳回" :value="5" />
          <el-option label="已完成" :value="6" />
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
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
        <el-button type="success" @click="handleImport">
          <el-icon><Upload /></el-icon>
          仪器导入
        </el-button>
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="recordNo" label="记录编号" width="160" />
      <el-table-column prop="taskNo" label="任务编号" width="160" />
      <el-table-column prop="sampleName" label="样品名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="methodName" label="检测方法" min-width="180" show-overflow-tooltip />
      <el-table-column prop="createBy" label="录入人" width="100" />
      <el-table-column prop="createTime" label="录入时间" width="180" />
      <el-table-column prop="status" label="状态" width="140">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" :effect="getStatusEffect(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="oos" label="是否OOS" width="100">
        <template #default="{ row }">
          <span v-if="row.oos" class="oos-badge">OOS</span>
          <span v-else style="color: #909399">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button
            type="success"
            link
            :disabled="row.status !== 0 && row.status !== 5"
            @click="handleSubmit(row)"
          >
            <el-icon><Check /></el-icon>
            提交审核
          </el-button>
          <el-button
            type="danger"
            link
            :disabled="row.status !== 0 && row.status !== 5"
            @click="handleDelete(row)"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
          <el-button type="info" link @click="handleVerify(row)">
            <el-icon><Key /></el-icon>
            验证完整性
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; justify-content: flex-end; display: flex"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Upload, Edit, Check, Delete, Key } from '@element-plus/icons-vue'
import { dataRecordApi } from '@/api/detection'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  recordNo: '',
  taskNo: '',
  sampleName: '',
  methodName: '',
  createBy: '',
  status: null,
  dateRange: []
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const statusMap = {
  0: { text: '草稿', type: 'info', effect: 'plain' },
  1: { text: '待一级审核', type: 'primary', effect: 'light' },
  2: { text: '一级审核通过', type: 'success', effect: 'light' },
  3: { text: '待二级审核', type: 'warning', effect: 'light' },
  4: { text: '二级审核通过', type: 'success', effect: 'dark' },
  5: { text: '已驳回', type: 'danger', effect: 'light' },
  6: { text: '已完成', type: 'success', effect: 'dark' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'
const getStatusEffect = (status) => statusMap[status]?.effect || 'plain'

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      recordNo: searchForm.recordNo || undefined,
      taskNo: searchForm.taskNo || undefined,
      sampleName: searchForm.sampleName || undefined,
      methodName: searchForm.methodName || undefined,
      createBy: searchForm.createBy || undefined,
      status: searchForm.status,
      startTime: searchForm.dateRange?.[0] || undefined,
      endTime: searchForm.dateRange?.[1] || undefined
    }
    const res = await dataRecordApi.page(params)
    tableData.value = res.data?.records || res.data?.list || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.recordNo = ''
  searchForm.taskNo = ''
  searchForm.sampleName = ''
  searchForm.methodName = ''
  searchForm.createBy = ''
  searchForm.status = null
  searchForm.dateRange = []
  handleSearch()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handleCurrentChange = (page) => {
  pagination.pageNum = page
  loadData()
}

const handleAdd = () => {
  router.push('/detection/data/edit/0')
}

const handleEdit = (row) => {
  router.push(`/detection/data/edit/${row.id}`)
}

const handleImport = () => {
  ElMessageBox.prompt('请输入仪器设备ID', '仪器导入', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入设备ID'
  }).then(async ({ value }) => {
    if (!value) return
    try {
      await dataRecordApi.import({ equipmentId: value })
      ElMessage.success('导入成功')
      loadData()
    } catch (error) {
      console.error('导入失败:', error)
    }
  }).catch(() => {})
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定提交记录【${row.recordNo}】进行审核吗？`,
      '提交审核',
      { type: 'warning' }
    )
    await dataRecordApi.submit(row.id)
    ElMessage.success('提交成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定删除记录【${row.recordNo}】吗？此操作不可恢复！`,
      '删除确认',
      { type: 'danger' }
    )
    await dataRecordApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleVerify = async (row) => {
  try {
    const res = await dataRecordApi.verify(row.id)
    if (res.data) {
      ElMessage.success(`数据完整，哈希值校验通过\n${res.data}`)
    } else {
      ElMessage.warning('数据完整性验证失败')
    }
  } catch (error) {
    console.error('验证失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.toolbar-left {
  display: flex;
  gap: 8px;
}

.oos-badge {
  background: #f56c6c;
  color: #fff;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}
</style>
