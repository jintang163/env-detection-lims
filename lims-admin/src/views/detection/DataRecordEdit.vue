<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">
        {{ isEdit ? '编辑检测数据' : '新增检测数据' }}
      </div>
    </div>

    <div class="detail-card" v-if="baseInfo">
      <div class="card-title">基本信息</div>
      <el-descriptions :column="4" border size="default">
        <el-descriptions-item label="记录编号">
          <span v-if="baseInfo.recordNo">{{ baseInfo.recordNo }}</span>
          <el-tag v-else type="info">自动生成</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="任务编号">
          {{ baseInfo.taskNo || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="样品名称">
          {{ baseInfo.sampleName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="检测方法">
          {{ baseInfo.methodName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="录入人">
          {{ baseInfo.createBy || '当前用户' }}
        </el-descriptions-item>
        <el-descriptions-item label="录入时间">
          {{ baseInfo.createTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(baseInfo.status)" :effect="getStatusEffect(baseInfo.status)">
            {{ getStatusText(baseInfo.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否OOS">
          <span v-if="baseInfo.oos" class="oos-badge">OOS</span>
          <span v-else style="color: #909399">否</span>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="detail-card" v-loading="loading">
      <div class="card-title">
        <span>检测数据</span>
        <div style="float: right">
          <el-button size="small" type="warning" @click="handleValidateAll">
            <el-icon><Warning /></el-icon>
            实时校验
          </el-button>
        </div>
      </div>

      <el-form ref="formRef" :model="formData" label-width="140px" v-if="formFields.length > 0">
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" v-for="field in formFields" :key="field.fieldCode">
            <el-form-item
              :label="field.fieldName"
              :prop="field.fieldCode"
              :required="field.required"
            >
              <div class="field-wrapper">
                <div class="field-input">
                  <el-input
                    v-if="field.fieldType === 'input'"
                    v-model="formData[field.fieldCode]"
                    :placeholder="field.placeholder || '请输入' + field.fieldName"
                    :disabled="isReadOnly"
                    @blur="handleFieldBlur(field)"
                  />
                  <el-input-number
                    v-else-if="field.fieldType === 'number'"
                    v-model="formData[field.fieldCode]"
                    :placeholder="field.placeholder || '请输入' + field.fieldName"
                    :disabled="isReadOnly"
                    :precision="field.precision"
                    :step="field.step || 0.01"
                    :min="field.min"
                    :max="field.max"
                    style="width: 100%"
                    @blur="handleFieldBlur(field)"
                  />
                  <el-select
                    v-else-if="field.fieldType === 'select'"
                    v-model="formData[field.fieldCode]"
                    :placeholder="field.placeholder || '请选择' + field.fieldName"
                    :disabled="isReadOnly"
                    style="width: 100%"
                    @change="handleFieldBlur(field)"
                  >
                    <el-option
                      v-for="opt in field.options"
                      :key="opt.value"
                      :label="opt.label"
                      :value="opt.value"
                    />
                  </el-select>
                  <el-date-picker
                    v-else-if="field.fieldType === 'date'"
                    v-model="formData[field.fieldCode]"
                    type="date"
                    :placeholder="field.placeholder || '请选择' + field.fieldName"
                    :disabled="isReadOnly"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                    @blur="handleFieldBlur(field)"
                  />
                  <el-input
                    v-else-if="field.fieldType === 'textarea'"
                    v-model="formData[field.fieldCode]"
                    type="textarea"
                    :rows="3"
                    :placeholder="field.placeholder || '请输入' + field.fieldName"
                    :disabled="isReadOnly"
                    @blur="handleFieldBlur(field)"
                  />
                </div>
                <div class="field-meta">
                  <span v-if="field.unit" class="meta-item">
                    <el-icon><Scale /></el-icon>
                    {{ field.unit }}
                  </span>
                  <span v-if="field.detectionLimit !== null && field.detectionLimit !== undefined" class="meta-item">
                    <el-icon><Monitor /></el-icon>
                    检出限: {{ field.detectionLimit }}
                  </span>
                  <span v-if="field.quantLimit !== null && field.quantLimit !== undefined" class="meta-item">
                    <el-icon><Aim /></el-icon>
                    定量限: {{ field.quantLimit }}
                  </span>
                  <span v-if="field.rangeHint" class="meta-item range-hint">
                    <el-icon><InfoFilled /></el-icon>
                    {{ field.rangeHint }}
                  </span>
                </div>
                <div class="field-validation" v-if="validationResults[field.fieldCode]">
                  <div
                    v-for="(result, index) in validationResults[field.fieldCode]"
                    :key="index"
                    :class="['validation-item', `validation-${result.level}`]"
                  >
                    <el-icon v-if="result.level === 'error'"><CircleCloseFilled /></el-icon>
                    <el-icon v-else-if="result.level === 'warning'"><WarningFilled /></el-icon>
                    <el-icon v-else-if="result.level === 'oos'" class="oos-icon"><Warning /></el-icon>
                    <span>{{ result.message }}</span>
                    <el-tag v-if="result.level === 'oos'" class="oos-tag">OOS</el-tag>
                  </div>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-empty v-else description="暂无表单字段，请先选择检测方法" v-if="!loading" />
    </div>

    <div class="form-toolbar">
      <el-button type="warning" @click="handleValidateAll" :disabled="isReadOnly">
        <el-icon><Warning /></el-icon>
        实时校验
      </el-button>
      <el-button type="primary" @click="handleSave" :disabled="isReadOnly">
        <el-icon><Save /></el-icon>
        保存
      </el-button>
      <el-button
        type="success"
        @click="handleSubmit"
        :disabled="isReadOnly || hasErrors"
        :loading="submitting"
      >
        <el-icon><Check /></el-icon>
        提交审核
      </el-button>
      <el-button @click="handleCancel">
        <el-icon><Close /></el-icon>
        取消
      </el-button>
    </div>

    <el-dialog v-model="showMethodSelect" title="选择检测方法" width="500px">
      <el-form label-width="100px">
        <el-form-item label="任务编号" required>
          <el-input v-model="methodForm.taskNo" placeholder="请输入任务编号" />
        </el-form-item>
        <el-form-item label="样品名称" required>
          <el-input v-model="methodForm.sampleName" placeholder="请输入样品名称" />
        </el-form-item>
        <el-form-item label="检测方法" required>
          <el-select v-model="methodForm.methodId" placeholder="请选择检测方法" style="width: 100%">
            <el-option
              v-for="method in methodList"
              :key="method.id"
              :label="method.methodName"
              :value="method.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showMethodSelect = false">取消</el-button>
        <el-button type="primary" @click="confirmMethodSelect">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Warning, Save, Check, Close, Scale, Monitor, Aim,
  InfoFilled, CircleCloseFilled, WarningFilled
} from '@element-plus/icons-vue'
import { dataRecordApi, formFieldApi } from '@/api/detection'

const route = useRoute()
const router = useRouter()

const id = route.params.id
const isEdit = id && id !== '0'

const loading = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const showMethodSelect = ref(false)

const baseInfo = ref(null)
const formFields = ref([])
const formData = reactive({})
const validationResults = reactive({})

const methodForm = reactive({
  taskNo: '',
  sampleName: '',
  methodId: null
})

const methodList = ref([])

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

const isReadOnly = computed(() => {
  const status = baseInfo.value?.status
  return status !== 0 && status !== 5 && status !== undefined
})

const hasErrors = computed(() => {
  for (const field in validationResults) {
    const results = validationResults[field]
    if (results && results.some(r => r.level === 'error')) {
      return true
    }
  }
  return false
})

const loadData = async () => {
  if (!isEdit) {
    showMethodSelect.value = true
    loadMethodList()
    return
  }

  loading.value = true
  try {
    const res = await dataRecordApi.get(id)
    const record = res.data
    baseInfo.value = record

    if (record.formData) {
      Object.assign(formData, record.formData)
    }

    if (record.methodId) {
      await loadFormFields(record.methodId)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMethodList = async () => {
  try {
    const res = await formFieldApi.getByMethodId('list')
    methodList.value = res.data || []
  } catch (error) {
    console.error('加载方法列表失败:', error)
  }
}

const loadFormFields = async (methodId) => {
  try {
    const res = await formFieldApi.getByMethodId(methodId)
    formFields.value = res.data || []

    formFields.value.forEach(field => {
      if (formData[field.fieldCode] === undefined) {
        formData[field.fieldCode] = field.defaultValue ?? ''
      }
    })
  } catch (error) {
    console.error('加载表单字段失败:', error)
  }
}

const confirmMethodSelect = async () => {
  if (!methodForm.taskNo || !methodForm.sampleName || !methodForm.methodId) {
    ElMessage.warning('请填写完整信息')
    return
  }

  baseInfo.value = {
    taskNo: methodForm.taskNo,
    sampleName: methodForm.sampleName,
    methodId: methodForm.methodId,
    status: 0,
    oos: false
  }

  showMethodSelect.value = false
  await loadFormFields(methodForm.methodId)
}

const handleFieldBlur = async (field) => {
  if (!field.fieldCode || formData[field.fieldCode] === undefined || formData[field.fieldCode] === '') {
    delete validationResults[field.fieldCode]
    return
  }

  try {
    const res = await dataRecordApi.validate({
      fieldCode: field.fieldCode,
      value: formData[field.fieldCode],
      methodId: baseInfo.value?.methodId || methodForm.methodId
    })

    if (res.data && res.data.length > 0) {
      validationResults[field.fieldCode] = res.data
    } else {
      delete validationResults[field.fieldCode]
    }
  } catch (error) {
    console.error('字段校验失败:', error)
  }
}

const handleValidateAll = async () => {
  loading.value = true
  const errors = []

  try {
    for (const field of formFields.value) {
      if (field.required && (formData[field.fieldCode] === undefined || formData[field.fieldCode] === '')) {
        errors.push({
          fieldCode: field.fieldCode,
          level: 'error',
          message: `${field.fieldName}为必填项`
        })
        continue
      }

      if (formData[field.fieldCode] !== undefined && formData[field.fieldCode] !== '') {
        try {
          const res = await dataRecordApi.validate({
            fieldCode: field.fieldCode,
            value: formData[field.fieldCode],
            methodId: baseInfo.value?.methodId || methodForm.methodId
          })

          if (res.data && res.data.length > 0) {
            validationResults[field.fieldCode] = res.data
            errors.push(...res.data)
          } else {
            delete validationResults[field.fieldCode]
          }
        } catch (e) {
          console.error(`字段 ${field.fieldCode} 校验失败:`, e)
        }
      }
    }

    const errorCount = errors.filter(e => e.level === 'error').length
    const warningCount = errors.filter(e => e.level === 'warning').length
    const oosCount = errors.filter(e => e.level === 'oos').length

    if (errorCount > 0 || warningCount > 0 || oosCount > 0) {
      ElMessage.warning(
        `校验完成：${errorCount}个错误，${warningCount}个警告，${oosCount}个OOS`
      )
    } else {
      ElMessage.success('校验通过，所有字段符合要求')
    }
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (hasErrors.value) {
    ElMessage.error('存在校验错误，请先修正后再保存')
    return
  }

  loading.value = true
  try {
    const data = {
      ...baseInfo.value,
      id: isEdit ? id : undefined,
      methodId: baseInfo.value?.methodId || methodForm.methodId,
      taskNo: baseInfo.value?.taskNo || methodForm.taskNo,
      sampleName: baseInfo.value?.sampleName || methodForm.sampleName,
      formData: { ...formData }
    }

    if (isEdit) {
      await dataRecordApi.update(data)
      ElMessage.success('保存成功')
    } else {
      const res = await dataRecordApi.save(data)
      ElMessage.success('保存成功')
      if (res.data?.id) {
        router.replace(`/detection/data/edit/${res.data.id}`)
        return
      }
    }

    loadData()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (hasErrors.value) {
    ElMessage.error('存在校验错误，请先修正后再提交')
    return
  }

  try {
    await ElMessageBox.confirm(
      '确定提交当前检测数据进行审核吗？提交后将无法修改。',
      '提交审核',
      { type: 'warning' }
    )

    submitting.value = true
    loading.value = true

    if (!isEdit) {
      const data = {
        ...baseInfo.value,
        methodId: baseInfo.value?.methodId || methodForm.methodId,
        taskNo: baseInfo.value?.taskNo || methodForm.taskNo,
        sampleName: baseInfo.value?.sampleName || methodForm.sampleName,
        formData: { ...formData }
      }
      const res = await dataRecordApi.save(data)
      if (res.data?.id) {
        await dataRecordApi.submit(res.data.id)
      }
    } else {
      await dataRecordApi.submit(id)
    }

    ElMessage.success('提交成功')
    router.push('/detection/data')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
    }
  } finally {
    submitting.value = false
    loading.value = false
  }
}

const handleCancel = () => {
  router.push('/detection/data')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.field-wrapper {
  width: 100%;
}

.field-input {
  margin-bottom: 6px;
}

.field-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.range-hint {
  color: #409EFF;
}

.field-validation {
  margin-top: 4px;
}

.validation-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  margin-bottom: 4px;
}

.validation-error {
  background: #fef0f0;
  color: #f56c6c;
}

.validation-warning {
  background: #fdf6ec;
  color: #e6a23c;
}

.validation-oos {
  background: #fef0f0;
  color: #f56c6c;
  animation: oos-pulse 1.5s infinite;
}

.oos-icon {
  color: #f56c6c;
  animation: oos-pulse 1.5s infinite;
}

.oos-tag {
  background: #f56c6c !important;
  color: #fff !important;
  border: none !important;
  margin-left: 8px;
  animation: oos-pulse 1.5s infinite;
}

@keyframes oos-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
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

.form-toolbar {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 24px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
</style>
