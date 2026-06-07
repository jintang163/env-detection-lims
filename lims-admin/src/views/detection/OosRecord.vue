<template>
  <div class="oos-record-container">
    <div class="stats-cards">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon orange">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingInvestigationCount }}</div>
            <div class="stat-label">待调查</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon blue">
            <el-icon><Search /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.investigatingCount }}</div>
            <div class="stat-label">调查中</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon yellow">
            <el-icon><DocumentChecked /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingReviewCount }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon red">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.openCount }}</div>
            <div class="stat-label">未关闭总数</div>
          </div>
        </div>
      </el-card>
    </div>

    <el-card class="table-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="OOS编号">
          <el-input v-model="searchForm.oosNo" placeholder="请输入OOS编号" clearable />
        </el-form-item>
        <el-form-item label="数据记录编号">
          <el-input v-model="searchForm.recordNo" placeholder="请输入记录编号" clearable />
        </el-form-item>
        <el-form-item label="检测项目">
          <el-input v-model="searchForm.itemName" placeholder="请输入检测项目" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待调查" :value="0" />
            <el-option label="调查中" :value="1" />
            <el-option label="调查完成" :value="2" />
            <el-option label="审核中" :value="3" />
            <el-option label="已关闭" :value="4" />
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
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="oosNo" label="OOS编号" width="160" fixed="left" />
        <el-table-column prop="recordNo" label="数据记录编号" width="160" />
        <el-table-column prop="itemName" label="检测项目" width="140" />
        <el-table-column prop="result" label="检测结果" width="120" align="right">
          <template #default="{ row }">
            {{ row.result }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="standardLimit" label="标准限值" width="120" align="right">
          <template #default="{ row }">
            {{ row.standardLimit }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="deviationRate" label="偏差率" width="120" align="right">
          <template #default="{ row }">
            <span
              class="deviation-rate"
              :class="{
                'blink': row.deviationRate > 50,
                'high-light': row.deviationRate > 50
              }"
            >
              {{ row.deviationRate }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="oosLevel" label="OOS等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.oosLevel)" effect="dark">
              {{ getLevelName(row.oosLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" effect="dark">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { oosRecordApi } from '@/api/detection'
import {
  Search,
  Refresh,
  Clock,
  DocumentChecked,
  Warning
} from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const stats = reactive({
  pendingInvestigationCount: 0,
  investigatingCount: 0,
  pendingReviewCount: 0,
  openCount: 0
})

const searchForm = reactive({
  oosNo: '',
  recordNo: '',
  itemName: '',
  status: null,
  dateRange: []
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const statusConfig = {
  0: { name: '待调查', type: 'warning', color: '#E6A23C' },
  1: { name: '调查中', type: 'primary', color: '#409EFF' },
  2: { name: '调查完成', type: 'info', color: '#909399' },
  3: { name: '审核中', type: 'warning', color: '#F56C6C' },
  4: { name: '已关闭', type: 'success', color: '#67C23A' }
}

const levelConfig = {
  1: { name: '轻微', type: 'success' },
  2: { name: '一般', type: 'warning' },
  3: { name: '严重', type: 'danger' }
}

const getStatusName = (status) => {
  return statusConfig[status]?.name || status
}

const getStatusTagType = (status) => {
  return statusConfig[status]?.type || 'info'
}

const getLevelName = (level) => {
  return levelConfig[level]?.name || level
}

const getLevelTagType = (level) => {
  return levelConfig[level]?.type || 'info'
}

const fetchStats = async () => {
  try {
    const [pendingRes, reviewRes, openRes] = await Promise.all([
      oosRecordApi.pendingInvestigationCount(),
      oosRecordApi.pendingReviewCount(),
      oosRecordApi.openCount()
    ])
    stats.pendingInvestigationCount = pendingRes.data || 0
    stats.pendingReviewCount = reviewRes.data || 0
    stats.openCount = openRes.data || 0
    stats.investigatingCount = Math.max(0, stats.openCount - stats.pendingInvestigationCount - stats.pendingReviewCount)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      oosNo: searchForm.oosNo || undefined,
      recordNo: searchForm.recordNo || undefined,
      itemName: searchForm.itemName || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startTime = searchForm.dateRange[0]
      params.endTime = searchForm.dateRange[1]
    }
    const res = await oosRecordApi.page(params)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取OOS数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.oosNo = ''
  searchForm.recordNo = ''
  searchForm.itemName = ''
  searchForm.status = null
  searchForm.dateRange = []
  pagination.current = 1
  fetchData()
}

const handleView = (row) => {
  router.push(`/detection/oos/edit/${row.id}`)
}

onMounted(() => {
  fetchStats()
  fetchData()
})
</script>

<style lang="scss" scoped>
.oos-record-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 16px;

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 28px;

        &.orange {
          background: linear-gradient(135deg, #E6A23C, #F56C6C);
        }
        &.blue {
          background: linear-gradient(135deg, #409EFF, #667EEA);
        }
        &.yellow {
          background: linear-gradient(135deg, #F56C6C, #E6A23C);
        }
        &.red {
          background: linear-gradient(135deg, #F56C6C, #C45655);
        }
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
        }
        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
  }
}

.table-card {
  .search-form {
    margin-bottom: 16px;
  }

  .pagination {
    margin-top: 16px;
    justify-content: flex-end;
  }
}

.deviation-rate {
  color: #F56C6C;
  font-weight: 600;

  &.high-light {
    color: #F56C6C;
  }

  &.blink {
    animation: blink 1s infinite;
  }
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}
</style>
