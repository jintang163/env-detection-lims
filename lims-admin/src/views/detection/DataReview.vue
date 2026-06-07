<template>
  <div class="data-review">
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card pending-first" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待一级审核</div>
              <div class="stat-value">{{ stats.pendingFirst || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card pending-second" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待二级审核</div>
              <div class="stat-value">{{ stats.pendingSecond || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card passed" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已通过</div>
              <div class="stat-value">{{ stats.passed || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card rejected" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已驳回</div>
              <div class="stat-value">{{ stats.rejected || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="一级审核" name="first">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索记录编号、任务编号、样品名称..."
              style="width: 300px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="fetchList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="firstReviewList"
            v-loading="loading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="recordNo" label="记录编号" width="180" />
            <el-table-column prop="taskNo" label="任务编号" width="180" />
            <el-table-column prop="sampleName" label="样品名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="methodName" label="检测方法" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createBy" label="录入人" width="120" />
            <el-table-column prop="submitTime" label="提交时间" width="180" />
            <el-table-column prop="reviewLevel" label="审核级别" width="100">
              <template #default>
                <el-tag type="primary" effect="light">一级审核</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleReview(row, 1)">
                  审核
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="二级审核" name="second">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索记录编号、任务编号、样品名称..."
              style="width: 300px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="fetchList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="secondReviewList"
            v-loading="loading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="recordNo" label="记录编号" width="180" />
            <el-table-column prop="taskNo" label="任务编号" width="180" />
            <el-table-column prop="sampleName" label="样品名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="methodName" label="检测方法" min-width="150" show-overflow-tooltip />
            <el-table-column prop="createBy" label="录入人" width="120" />
            <el-table-column prop="submitTime" label="提交时间" width="180" />
            <el-table-column prop="reviewLevel" label="审核级别" width="100">
              <template #default>
                <el-tag type="warning" effect="light">二级审核</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleReview(row, 2)">
                  审核
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="reviewVisible" :title="`${reviewLevel === 1 ? '一级' : '二级'}审核`" width="70%" top="5vh">
      <div v-loading="detailLoading" class="review-content">
        <div class="detail-section" v-if="currentRecord">
          <h4>基本信息</h4>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="记录编号">{{ currentRecord.recordNo }}</el-descriptions-item>
            <el-descriptions-item label="任务编号">{{ currentRecord.taskNo }}</el-descriptions-item>
            <el-descriptions-item label="样品名称">{{ currentRecord.sampleName }}</el-descriptions-item>
            <el-descriptions-item label="检测方法">{{ currentRecord.methodName }}</el-descriptions-item>
            <el-descriptions-item label="录入人">{{ currentRecord.createBy }}</el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ currentRecord.submitTime }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section" v-if="currentRecord">
          <h4>检测数据</h4>
          <div class="data-preview" v-html="currentRecord.previewContent"></div>
        </div>

        <div class="detail-section">
          <h4>审核历史</h4>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in reviewHistory"
              :key="index"
              :type="item.result === 'PASS' ? 'success' : 'danger'"
              :timestamp="item.reviewTime"
            >
              <el-tag :type="item.result === 'PASS' ? 'success' : 'danger'" size="small">
                {{ item.result === 'PASS' ? '通过' : '驳回' }}
              </el-tag>
              <span style="margin-left: 8px">
                {{ item.reviewer }} - {{ item.reviewLevel === 1 ? '一级审核' : '二级审核' }}
              </span>
              <div style="margin-top: 8px; color: #606266">
                审核意见：{{ item.opinion || '无' }}
              </div>
            </el-timeline-item>
            <el-timeline-item v-if="reviewHistory.length === 0" type="primary">
              暂无审核记录
            </el-timeline-item>
          </el-timeline>
        </div>

        <div class="detail-section">
          <h4>审核意见</h4>
          <el-form :model="reviewForm" :rules="reviewRules" ref="reviewFormRef" label-width="80px">
            <el-form-item label="审核结果" prop="result">
              <el-radio-group v-model="reviewForm.result">
                <el-radio value="PASS">
                  <el-icon color="#67c23a"><CircleCheck /></el-icon>
                  通过
                </el-radio>
                <el-radio value="REJECT">
                  <el-icon color="#f56c6c"><CircleClose /></el-icon>
                  驳回
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审核意见" prop="opinion">
              <el-input
                v-model="reviewForm.opinion"
                type="textarea"
                :rows="4"
                placeholder="请输入审核意见..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview">
          <el-icon><Check /></el-icon>
          提交审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dataReviewApi, dataRecordApi } from '@/api/detection'

const activeTab = ref('first')
const loading = ref(false)
const detailLoading = ref(false)
const searchKeyword = ref('')
const firstReviewList = ref([])
const secondReviewList = ref([])
const reviewVisible = ref(false)
const reviewFormRef = ref(null)
const currentRecord = ref(null)
const reviewLevel = ref(1)
const reviewHistory = ref([])

const stats = reactive({
  pendingFirst: 0,
  pendingSecond: 0,
  passed: 0,
  rejected: 0
})

const reviewForm = reactive({
  dataRecordId: null,
  result: 'PASS',
  opinion: ''
})

const reviewRules = {
  result: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  opinion: [
    { required: true, message: '请输入审核意见', trigger: 'blur' },
    { min: 2, message: '审核意见至少2个字符', trigger: 'blur' }
  ]
}

const fetchStats = async () => {
  try {
    const res = await dataReviewApi.pendingCount()
    Object.assign(stats, res.data || {})
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 100,
      keyword: searchKeyword.value
    }

    const firstParams = { ...params, reviewLevel: 1 }
    const secondParams = { ...params, reviewLevel: 2 }

    const [firstRes, secondRes] = await Promise.all([
      dataReviewApi.myTasks(firstParams),
      dataReviewApi.myTasks(secondParams)
    ])

    firstReviewList.value = firstRes.data?.records || []
    secondReviewList.value = secondRes.data?.records || []
  } catch (error) {
    console.error('获取审核列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  searchKeyword.value = ''
  fetchList()
}

const handleReview = async (row, level) => {
  reviewLevel.value = level
  reviewForm.dataRecordId = row.dataRecordId || row.id
  
  detailLoading.value = true
  reviewVisible.value = true
  
  try {
    const [detailRes, historyRes] = await Promise.all([
      dataRecordApi.get(row.dataRecordId || row.id),
      dataReviewApi.list(row.dataRecordId || row.id)
    ])
    
    currentRecord.value = {
      ...row,
      ...detailRes.data,
      previewContent: detailRes.data?.content || ''
    }
    reviewHistory.value = historyRes.data || []
  } catch (error) {
    console.error('获取审核详情失败:', error)
  } finally {
    detailLoading.value = false
  }
}

const handleSubmitReview = async () => {
  try {
    await reviewFormRef.value.validate()
    
    const confirmText = reviewForm.result === 'PASS' 
      ? `确定要通过该${reviewLevel.value === 1 ? '一级' : '二级'}审核吗？`
      : `确定要驳回该${reviewLevel.value === 1 ? '一级' : '二级'}审核吗？`
    
    await ElMessageBox.confirm(confirmText, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: reviewForm.result === 'PASS' ? 'success' : 'warning'
    })

    const params = {
      dataRecordId: reviewForm.dataRecordId,
      opinion: reviewForm.opinion
    }

    if (reviewForm.result === 'PASS') {
      if (reviewLevel.value === 1) {
        await dataReviewApi.firstReview(params)
      } else {
        await dataReviewApi.secondReview(params)
      }
      ElMessage.success('审核通过')
    } else {
      await dataReviewApi.reject(params)
      ElMessage.success('已驳回')
    }

    reviewVisible.value = false
    reviewForm.result = 'PASS'
    reviewForm.opinion = ''
    fetchStats()
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('提交审核失败:', error)
    }
  }
}

onMounted(() => {
  fetchStats()
  fetchList()
})
</script>

<style lang="scss" scoped>
.data-review {
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

      &.pending-first {
        .stat-icon {
          background: linear-gradient(135deg, #667eea, #764ba2);
        }
      }

      &.pending-second {
        .stat-icon {
          background: linear-gradient(135deg, #f093fb, #f5576c);
        }
      }

      &.passed {
        .stat-icon {
          background: linear-gradient(135deg, #4facfe, #00f2fe);
        }
      }

      &.rejected {
        .stat-icon {
          background: linear-gradient(135deg, #fa709a, #fee140);
        }
      }
    }
  }

  .toolbar {
    display: flex;
    gap: 12px;
  }

  .review-content {
    .detail-section {
      margin-bottom: 20px;

      h4 {
        margin: 0 0 12px 0;
        font-size: 14px;
        font-weight: 600;
        color: #303133;
        padding-left: 8px;
        border-left: 3px solid #409eff;
      }
    }

    .data-preview {
      padding: 16px;
      background: #fafafa;
      border: 1px solid #e4e7ed;
      border-radius: 4px;
      max-height: 300px;
      overflow-y: auto;

      :deep(table) {
        border-collapse: collapse;
        width: 100%;
      }

      :deep(th), :deep(td) {
        border: 1px solid #e4e7ed;
        padding: 6px 10px;
        font-size: 13px;
      }

      :deep(th) {
        background: #f0f2f5;
        font-weight: 600;
      }
    }
  }
}
</style>
