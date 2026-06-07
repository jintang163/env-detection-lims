<template>
  <div class="original-record-edit">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="title">{{ isEdit ? '编辑原始记录' : '新增原始记录' }}</span>
          <div class="toolbar">
            <el-button type="primary" @click="handleSave">
              <el-icon><Save /></el-icon>
              保存
            </el-button>
            <el-button type="success" @click="handleSubmit" :disabled="!form.id">
              <el-icon><Check /></el-icon>
              提交
            </el-button>
            <el-button type="warning" @click="handlePreview" :disabled="!form.id">
              <el-icon><View /></el-icon>
              预览
            </el-button>
            <el-button @click="handleCancel">
              <el-icon><Close /></el-icon>
              取消
            </el-button>
          </div>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入标题" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联数据记录" prop="dataRecordId">
              <el-select
                v-model="form.dataRecordId"
                placeholder="请选择关联数据记录"
                filterable
                remote
                :remote-method="fetchDataRecords"
                :loading="dataRecordLoading"
                style="width: 100%"
              >
                <el-option
                  v-for="item in dataRecordOptions"
                  :key="item.id"
                  :label="`${item.recordNo} - ${item.sampleName || ''}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="记录类型" prop="recordType">
              <el-select v-model="form.recordType" placeholder="请选择记录类型" style="width: 100%">
                <el-option label="检测原始记录" value="DETECTION" />
                <el-option label="环境监测记录" value="MONITORING" />
                <el-option label="仪器使用记录" value="INSTRUMENT" />
                <el-option label="质量控制记录" value="QUALITY_CONTROL" />
                <el-option label="其他记录" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.id">
            <el-form-item label="当前版本">
              <el-tag type="primary">v{{ form.version || 1 }}</el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="记录内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="12"
            placeholder="请输入原始记录内容，支持HTML格式..."
            maxlength="10000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px" v-if="form.id">
      <template #header>
        <span class="title">版本历史</span>
      </template>

      <el-table :data="versionHistory" border stripe style="width: 100%">
        <el-table-column prop="version" label="版本号" width="100">
          <template #default="{ row }">
            <el-tag type="primary">v{{ row.version }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column prop="operateType" label="操作类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getOperateType(row.operateType)" effect="light">
              {{ getOperateText(row.operateType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="操作说明" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewVersion(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="previewVisible" title="预览" width="80%" top="5vh">
      <div class="preview-content" v-html="previewContent"></div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="versionVisible" title="历史版本详情" width="70%" top="5vh">
      <div class="version-detail">
        <div class="version-info">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="版本号">
              <el-tag type="primary">v{{ currentVersion.version }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="操作人">{{ currentVersion.operator }}</el-descriptions-item>
            <el-descriptions-item label="操作时间">{{ currentVersion.operateTime }}</el-descriptions-item>
            <el-descriptions-item label="操作类型">
              <el-tag :type="getOperateType(currentVersion.operateType)">
                {{ getOperateText(currentVersion.operateType) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="操作说明" :span="2">{{ currentVersion.remark }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="version-content" v-html="currentVersion.content"></div>
      </div>
      <template #footer>
        <el-button @click="versionVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { originalRecordApi, dataRecordApi } from '@/api/detection'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const id = computed(() => route.params.id)
const isEdit = computed(() => id.value && id.value !== 'new')

const form = reactive({
  id: null,
  title: '',
  dataRecordId: null,
  recordType: '',
  content: '',
  remark: '',
  version: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  dataRecordId: [{ required: true, message: '请选择关联数据记录', trigger: 'change' }],
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入记录内容', trigger: 'blur' }]
}

const dataRecordOptions = ref([])
const dataRecordLoading = ref(false)
const versionHistory = ref([])
const previewVisible = ref(false)
const previewContent = ref('')
const versionVisible = ref(false)
const currentVersion = ref({})

const fetchDataRecords = async (query) => {
  dataRecordLoading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 50,
      recordNo: query || ''
    }
    const res = await dataRecordApi.page(params)
    dataRecordOptions.value = res.data?.records || []
  } catch (error) {
    console.error('获取数据记录列表失败:', error)
  } finally {
    dataRecordLoading.value = false
  }
}

const fetchDetail = async () => {
  try {
    const res = await originalRecordApi.get(id.value)
    Object.assign(form, res.data)
    fetchVersionHistory()
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const fetchVersionHistory = async () => {
  try {
    const res = await originalRecordApi.get(id.value)
    versionHistory.value = res.data?.versionHistory || []
  } catch (error) {
    console.error('获取版本历史失败:', error)
  }
}

const getOperateType = (type) => {
  const typeMap = {
    CREATE: 'primary',
    UPDATE: 'warning',
    SUBMIT: 'success',
    ARCHIVE: 'info'
  }
  return typeMap[type] || 'info'
}

const getOperateText = (type) => {
  const textMap = {
    CREATE: '创建',
    UPDATE: '更新',
    SUBMIT: '提交',
    ARCHIVE: '归档'
  }
  return textMap[type] || type
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await originalRecordApi.update(form)
      ElMessage.success('保存成功')
    } else {
      const res = await originalRecordApi.save(form)
      form.id = res.data?.id
      form.version = res.data?.version || 1
      ElMessage.success('保存成功')
    }
    fetchVersionHistory()
  } catch (error) {
    if (error !== false) {
      console.error('保存失败:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    await ElMessageBox.confirm('确定要提交该记录吗？提交后将进入审核流程。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await originalRecordApi.submit(form.id)
    ElMessage.success('提交成功')
    router.push('/detection/original')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
    }
  }
}

const handlePreview = async () => {
  try {
    const res = await originalRecordApi.preview(form.id)
    previewContent.value = res.data || ''
    previewVisible.value = true
  } catch (error) {
    console.error('预览失败:', error)
  }
}

const handleCancel = () => {
  router.push('/detection/original')
}

const handleViewVersion = (row) => {
  currentVersion.value = row
  versionVisible.value = true
}

onMounted(() => {
  fetchDataRecords('')
  if (isEdit.value) {
    fetchDetail()
  }
})
</script>

<style lang="scss" scoped>
.original-record-edit {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: 600;
    }

    .toolbar {
      display: flex;
      gap: 8px;
    }
  }

  .preview-content,
  .version-content {
    padding: 20px;
    min-height: 400px;
    max-height: 600px;
    overflow-y: auto;
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

  .version-detail {
    .version-info {
      margin-bottom: 16px;
    }
  }
}
</style>
