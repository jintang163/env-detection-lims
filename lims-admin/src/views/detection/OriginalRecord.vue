<template>
  <div class="original-record">
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="记录编号">
          <el-input v-model="searchForm.recordNo" placeholder="请输入记录编号" clearable />
        </el-form-item>
        <el-form-item label="关联数据记录">
          <el-input v-model="searchForm.dataRecordNo" placeholder="请输入数据记录编号" clearable />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="创建人">
          <el-input v-model="searchForm.createBy" placeholder="请输入创建人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 160px">
            <el-option label="草稿" :value="0" />
            <el-option label="已提交" :value="1" />
            <el-option label="已归档" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
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
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
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
        style="width: 100%"
      >
        <el-table-column prop="recordNo" label="记录编号" width="180" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="dataRecordNo" label="关联数据记录" width="180" />
        <el-table-column prop="version" label="版本号" width="100" />
        <el-table-column prop="createBy" label="创建人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)" :disabled="row.status === 2">
              编辑
            </el-button>
            <el-button link type="primary" @click="handleSubmit(row)" :disabled="row.status !== 0">
              提交
            </el-button>
            <el-button link type="primary" @click="handlePreview(row)">
              预览
            </el-button>
            <el-button link type="success" @click="handleArchive(row)" :disabled="row.status !== 1">
              归档
            </el-button>
            <el-button link type="warning" @click="handleVerify(row)">
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
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </el-card>

    <el-dialog v-model="previewVisible" title="预览" width="80%" top="5vh">
      <div class="preview-content" v-html="previewContent"></div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { originalRecordApi } from '@/api/detection'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const previewVisible = ref(false)
const previewContent = ref('')

const searchForm = reactive({
  recordNo: '',
  dataRecordNo: '',
  title: '',
  createBy: '',
  status: null,
  dateRange: []
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'primary',
    2: 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    0: '草稿',
    1: '已提交',
    2: '已归档'
  }
  return textMap[status] || '未知'
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    delete params.dateRange
    
    const res = await originalRecordApi.page(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchList()
}

const handleReset = () => {
  searchForm.recordNo = ''
  searchForm.dataRecordNo = ''
  searchForm.title = ''
  searchForm.createBy = ''
  searchForm.status = null
  searchForm.dateRange = []
  handleSearch()
}

const handleAdd = () => {
  router.push('/detection/original/edit/new')
}

const handleEdit = (row) => {
  router.push(`/detection/original/edit/${row.id}`)
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm('确定要提交该记录吗？提交后将进入审核流程。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await originalRecordApi.submit(row.id)
    ElMessage.success('提交成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
    }
  }
}

const handlePreview = async (row) => {
  try {
    const res = await originalRecordApi.preview(row.id)
    previewContent.value = res.data || ''
    previewVisible.value = true
  } catch (error) {
    console.error('预览失败:', error)
  }
}

const handleArchive = async (row) => {
  try {
    await ElMessageBox.confirm('确定要归档该记录吗？归档后将无法再编辑。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await originalRecordApi.archive(row.id)
    ElMessage.success('归档成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归档失败:', error)
    }
  }
}

const handleVerify = async (row) => {
  try {
    const res = await originalRecordApi.verify(row.id)
    if (res.data) {
      ElMessage.success('数据完整性验证通过')
    } else {
      ElMessage.error('数据完整性验证失败，数据可能被篡改')
    }
  } catch (error) {
    console.error('验证失败:', error)
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style lang="scss" scoped>
.original-record {
  .search-card {
    margin-bottom: 16px;

    :deep(.el-form-item) {
      margin-bottom: 0;
    }
  }

  .table-card {
    .toolbar {
      margin-bottom: 16px;
    }

    :deep(.el-pagination) {
      margin-top: 16px;
      justify-content: flex-end;
    }
  }

  .preview-content {
    padding: 20px;
    min-height: 400px;
    background: #fff;
    border: 1px solid #e4e7ed;
    border-radius: 4px;

    :deep(img) {
      max-width: 100%;
    }

    :deep(table) {
      border-collapse: collapse;
      width: 100%;
    }

    :deep(th), :deep(td) {
      border: 1px solid #e4e7ed;
      padding: 8px 12px;
    }
  }
}
</style>
