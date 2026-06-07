<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">质控规则配置</div>
      <div class="page-desc">配置Westgard多规则及参数，用于质控数据的自动判定</div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">规则总数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card enabled" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已启用</div>
              <div class="stat-value">{{ stats.enabled || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">警告规则</div>
              <div class="stat-value">{{ stats.warning || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card reject" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">失控规则</div>
              <div class="stat-value">{{ stats.reject || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索规则名称、规则代码..."
          style="width: 300px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchType" placeholder="规则类型" clearable style="width: 150px">
          <el-option label="警告规则" :value="1" />
          <el-option label="失控规则" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增规则
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
        <el-table-column prop="ruleCode" label="规则代码" width="120" />
        <el-table-column prop="ruleName" label="规则名称" width="180" />
        <el-table-column prop="ruleType" label="规则类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.ruleType === 1 ? 'warning' : 'danger'" effect="light">
              {{ row.ruleType === 1 ? '警告' : '失控' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="规则描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="参数配置" min-width="300">
          <template #default="{ row }">
            <div class="param-tags">
              <el-tag v-if="row.sdMultiple" size="small" type="info">
                SD倍数: {{ row.sdMultiple }}
              </el-tag>
              <el-tag v-if="row.consecutivePoints" size="small" type="info">
                连续点数: {{ row.consecutivePoints }}
              </el-tag>
              <el-tag v-if="row.withinRun" size="small" type="success">
                批内判断
              </el-tag>
              <el-tag v-if="row.acrossRun" size="small" type="primary">
                跨批判断
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              :active-value="1"
              :inactive-value="0"
              @change="handleToggle(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="success" link @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
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
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" top="5vh">
      <el-form
        :model="ruleForm"
        :rules="formRules"
        ref="ruleFormRef"
        label-width="120px"
        v-loading="detailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="规则代码" prop="ruleCode">
              <el-input v-model="ruleForm.ruleCode" placeholder="如: 1_3s" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规则名称" prop="ruleName">
              <el-input v-model="ruleForm.ruleName" placeholder="如: 1-3s规则" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="规则类型" prop="ruleType">
              <el-select v-model="ruleForm.ruleType" placeholder="请选择规则类型" :disabled="isView">
                <el-option label="警告规则" :value="1" />
                <el-option label="失控规则" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用" prop="enabled">
              <el-switch v-model="ruleForm.enabled" :active-value="1" :inactive-value="0" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="规则描述" prop="description">
          <el-input
            v-model="ruleForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入规则描述"
            :disabled="isView"
          />
        </el-form-item>

        <el-divider content-position="left">参数配置</el-divider>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="SD倍数" prop="sdMultiple">
              <el-input-number
                v-model="ruleForm.sdMultiple"
                :min="1"
                :max="10"
                :step="0.5"
                placeholder="标准差倍数"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="连续点数" prop="consecutivePoints">
              <el-input-number
                v-model="ruleForm.consecutivePoints"
                :min="1"
                :max="20"
                placeholder="连续检测点数"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="批内判断" prop="withinRun">
              <el-switch v-model="ruleForm.withinRun" :disabled="isView" />
              <span style="margin-left: 8px; color: #909399">同一批次内判断</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="跨批判断" prop="acrossRun">
              <el-switch v-model="ruleForm.acrossRun" :disabled="isView" />
              <span style="margin-left: 8px; color: #909399">多个批次间判断</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="计算公式" prop="formula">
          <el-input
            v-model="ruleForm.formula"
            type="textarea"
            :rows="3"
            placeholder="如: |x - mean| > k * SD"
            :disabled="isView"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="ruleForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isView"
          />
        </el-form-item>
      </el-form>

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
import { qualityControlApi } from '@/api/detection'

const loading = ref(false)
const detailLoading = ref(false)
const searchKeyword = ref('')
const searchType = ref(null)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isView = ref(false)
const ruleFormRef = ref(null)

const stats = reactive({
  total: 0,
  enabled: 0,
  warning: 0,
  reject: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const ruleForm = reactive({
  id: null,
  ruleCode: '',
  ruleName: '',
  ruleType: 1,
  description: '',
  sdMultiple: null,
  consecutivePoints: null,
  withinRun: false,
  acrossRun: false,
  formula: '',
  enabled: 1,
  remark: ''
})

const formRules = {
  ruleCode: [{ required: true, message: '请输入规则代码', trigger: 'blur' }],
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入规则描述', trigger: 'blur' }]
}

const fetchStats = async () => {
  try {
    const res = await qualityControlApi.ruleStats()
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      ruleType: searchType.value
    }
    const res = await qualityControlApi.rulePage(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else if (res.data?.list) {
      tableData.value = res.data.list
      pagination.total = res.data.total || res.data.list.length
    } else {
      tableData.value = []
      pagination.total = 0
    }
    await fetchStats()
  } catch (error) {
    console.error('获取规则列表失败', error)
    ElMessage.error('获取规则列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增质控规则'
  Object.assign(ruleForm, {
    id: null,
    ruleCode: '',
    ruleName: '',
    ruleType: 1,
    description: '',
    sdMultiple: null,
    consecutivePoints: null,
    withinRun: false,
    acrossRun: false,
    formula: '',
    enabled: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑质控规则'
  Object.assign(ruleForm, { ...row })
  dialogVisible.value = true
}

const handleView = (row) => {
  isView.value = true
  dialogTitle.value = '查看质控规则'
  Object.assign(ruleForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除规则"${row.ruleName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await qualityControlApi.deleteRule(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('删除失败', error)
    }
  }
}

const handleToggle = async (row) => {
  try {
    await qualityControlApi.toggleRule(row.id, row.enabled)
    ElMessage.success(row.enabled === 1 ? '已启用' : '已禁用')
    fetchStats()
  } catch (error) {
    row.enabled = row.enabled === 1 ? 0 : 1
    console.error('切换状态失败', error)
    ElMessage.error('切换状态失败')
  }
}

const handleSubmit = async () => {
  try {
    await ruleFormRef.value.validate()
    if (ruleForm.id) {
      await qualityControlApi.updateRule(ruleForm)
      ElMessage.success('更新成功')
    } else {
      await qualityControlApi.saveRule(ruleForm)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('提交失败', error)
    }
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style lang="scss" scoped>
.page-container {
  .page-header {
    margin-bottom: 16px;

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
  }

  .stats-row {
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: #fff;
        }

        .stat-info {
          .stat-label {
            font-size: 14px;
            color: #606266;
            margin-bottom: 4px;
          }

          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
          }
        }
      }

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
      }

      &.enabled .stat-icon {
        background: linear-gradient(135deg, #4facfe, #00f2fe);
      }

      &.warning .stat-icon {
        background: linear-gradient(135deg, #f093fb, #f5576c);
      }

      &.reject .stat-icon {
        background: linear-gradient(135deg, #fa709a, #fee140);
      }
    }
  }

  .toolbar {
    display: flex;
    gap: 12px;
  }

  .param-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }
}
</style>
