<template>
  <div class="sampling-task">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="任务编号">
            <a-input v-model:value="queryParams.taskNo" placeholder="请输入任务编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="计划编号">
            <a-input v-model:value="planNo" placeholder="请输入计划编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="点位名称">
            <a-input v-model:value="pointName" placeholder="请输入点位名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="采样员">
            <a-select v-model:value="queryParams.samplerId" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option v-for="item in userList" :key="item.id" :value="item.id">
                {{ item.realName }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.status" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">待分配</a-select-option>
              <a-select-option :value="1">已分配</a-select-option>
              <a-select-option :value="2">采样中</a-select-option>
              <a-select-option :value="3">已提交</a-select-option>
              <a-select-option :value="4">已完成</a-select-option>
              <a-select-option :value="5">已取消</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="分配时间">
            <a-range-picker
              v-model:value="assignTimeRange"
              style="width: 260px"
              value-format="YYYY-MM-DD"
              @change="handleAssignTimeChange"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery">
              <SearchOutlined /> 查询
            </a-button>
            <a-button style="margin-left: 8px" @click="handleReset">
              <ReloadOutlined /> 重置
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看详情</a-button>
            <a-button v-if="record.status === 1" type="link" size="small" @click="handleDownload(record)">下载</a-button>
            <a-button v-if="record.status === 1" type="link" size="small" @click="handleStart(record)">开始采样</a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="detailModalVisible"
      title="采样任务详情"
      width="1100px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="任务编号">{{ taskDetail?.taskNo }}</a-descriptions-item>
            <a-descriptions-item label="计划编号">{{ taskDetail?.planNo || '-' }}</a-descriptions-item>
            <a-descriptions-item label="计划名称">{{ taskDetail?.planName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="点位名称">{{ taskDetail?.pointName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="采样员">{{ taskDetail?.samplerName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="状态">
              <a-tag :color="getStatusColor(taskDetail?.status)">
                {{ taskDetail?.statusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="分配时间">{{ taskDetail?.assignTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="实际采样日期">{{ taskDetail?.actualSamplingDate || '-' }}</a-descriptions-item>
            <a-descriptions-item label="采样时间">{{ taskDetail?.samplingTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="采样人员">{{ taskDetail?.samplingPerson || '-' }}</a-descriptions-item>
            <a-descriptions-item label="质控样标记">
              <a-tag :color="taskDetail?.qcSampleFlag === 1 ? 'orange' : 'default'">
                {{ taskDetail?.qcSampleFlagName || '-' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="离线标记">
              <a-tag :color="taskDetail?.offlineFlag === 1 ? 'blue' : 'default'">
                {{ taskDetail?.offlineFlagName || '-' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="样品数量">{{ taskDetail?.sampleCount || 0 }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ taskDetail?.createTime }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ taskDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>
        <a-tab-pane key="gps" tab="GPS信息">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="经度">{{ taskDetail?.longitude || '-' }}</a-descriptions-item>
            <a-descriptions-item label="纬度">{{ taskDetail?.latitude || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>
        <a-tab-pane key="field" tab="现场参数">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="温度(°C)">{{ taskDetail?.temperature !== null && taskDetail?.temperature !== undefined ? taskDetail.temperature : '-' }}</a-descriptions-item>
            <a-descriptions-item label="pH">{{ taskDetail?.ph !== null && taskDetail?.ph !== undefined ? taskDetail.ph : '-' }}</a-descriptions-item>
            <a-descriptions-item label="天气">{{ taskDetail?.weather || '-' }}</a-descriptions-item>
            <a-descriptions-item label="风速">{{ taskDetail?.windSpeed || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>
        <a-tab-pane key="photos" tab="照片">
          <div v-if="taskDetail?.photos" class="photo-list">
            <a-image
              v-for="(photo, index) in photoList"
              :key="index"
              :width="200"
              :height="200"
              :src="photo"
              style="margin: 8px"
            />
          </div>
          <a-empty v-else description="暂无照片" />
        </a-tab-pane>
        <a-tab-pane key="samples" tab="样品列表">
          <a-table
            :columns="sampleColumns"
            :data-source="taskDetail?.samples || []"
            row-key="id"
            size="small"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'sampleStatus'">
                <a-tag :color="getSampleStatusColor(record.sampleStatus)">
                  {{ record.sampleStatusName || '-' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'qcFlag'">
                <a-tag :color="record.qcFlag === 1 ? 'orange' : 'default'">
                  {{ record.qcFlagName || '-' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'storageCondition'">
                {{ record.storageConditionName || '-' }}
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import type {
  SamplingTaskQuery,
  SamplingTaskVO,
  SamplingTaskDetailVO
} from '@/types'
import {
  getSamplingTaskPage,
  getSamplingTaskById,
  downloadSamplingTask,
  startSampling
} from '@/api/sampling'

const loading = ref(false)
const detailTab = ref('basic')
const detailModalVisible = ref(false)

const planNo = ref('')
const pointName = ref('')
const assignTimeRange = ref<any[]>([])

const userList = ref<any[]>([])

const queryParams = reactive<SamplingTaskQuery>({
  pageNum: 1,
  pageSize: 10,
  taskNo: '',
  planId: undefined,
  samplerId: undefined,
  status: undefined,
  qcSampleFlag: undefined,
  offlineFlag: undefined,
  assignTimeStart: '',
  assignTimeEnd: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<SamplingTaskVO[]>([])
const taskDetail = ref<SamplingTaskDetailVO | null>(null)

const photoList = computed(() => {
  if (!taskDetail.value?.photos) return []
  return taskDetail.value.photos.split(',').filter(Boolean)
})

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '任务编号', dataIndex: 'taskNo', key: 'taskNo', width: 140 },
  { title: '计划编号', dataIndex: 'planNo', key: 'planNo', width: 140 },
  { title: '点位名称', dataIndex: 'pointName', key: 'pointName', width: 180, ellipsis: true },
  { title: '采样员', dataIndex: 'samplerName', key: 'samplerName', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '分配时间', dataIndex: 'assignTime', key: 'assignTime', width: 170 },
  { title: '实际采样日期', dataIndex: 'actualSamplingDate', key: 'actualSamplingDate', width: 140 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' }
]

const sampleColumns = [
  { title: '序号', key: 'index', width: 60, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '样品编号', dataIndex: 'sampleNo', key: 'sampleNo', width: 140 },
  { title: '二维码', dataIndex: 'qrCode', key: 'qrCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 150 },
  { title: '样品状态', dataIndex: 'sampleStatus', key: 'sampleStatus', width: 100 },
  { title: '保存条件', dataIndex: 'storageCondition', key: 'storageCondition', width: 100 },
  { title: '质控样标记', dataIndex: 'qcFlag', key: 'qcFlag', width: 100 },
  { title: '采样深度', dataIndex: 'samplingDepth', key: 'samplingDepth', width: 100 },
  { title: '采样时间', dataIndex: 'samplingTime', key: 'samplingTime', width: 170 },
  { title: '容器类型', dataIndex: 'containerType', key: 'containerType', width: 100 },
  { title: '防腐剂', dataIndex: 'preservative', key: 'preservative', width: 100 },
  { title: '备注', dataIndex: 'remark', key: 'remark', width: 150 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSamplingTaskPage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchUserList = async () => {
  userList.value = [
    { id: 1, realName: '张三' },
    { id: 2, realName: '李四' },
    { id: 3, realName: '王五' },
    { id: 4, realName: '赵六' }
  ]
}

const handleAssignTimeChange = (dates: any[]) => {
  if (dates && dates.length === 2) {
    queryParams.assignTimeStart = dates[0]
    queryParams.assignTimeEnd = dates[1]
  } else {
    queryParams.assignTimeStart = ''
    queryParams.assignTimeEnd = ''
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    taskNo: '',
    planId: undefined,
    samplerId: undefined,
    status: undefined,
    qcSampleFlag: undefined,
    offlineFlag: undefined,
    assignTimeStart: '',
    assignTimeEnd: ''
  })
  planNo.value = ''
  pointName.value = ''
  assignTimeRange.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleView = async (record: SamplingTaskVO) => {
  try {
    const res = await getSamplingTaskById(record.id)
    taskDetail.value = res.data
    detailTab.value = 'basic'
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get task detail error:', error)
  }
}

const handleDownload = async (record: SamplingTaskVO) => {
  try {
    await downloadSamplingTask(record.id)
    message.success('下载任务成功')
  } catch (error) {
    console.error('Download task error:', error)
  }
}

const handleStart = async (record: SamplingTaskVO) => {
  try {
    await startSampling(record.id)
    message.success('开始采样成功')
    fetchData()
  } catch (error) {
    console.error('Start sampling error:', error)
  }
}

const getStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'default',
    1: 'blue',
    2: 'processing',
    3: 'orange',
    4: 'green',
    5: 'red'
  }
  return colors[status || 0] || 'default'
}

const getSampleStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'default',
    1: 'blue',
    2: 'processing',
    3: 'green',
    4: 'red'
  }
  return colors[status || 0] || 'default'
}

onMounted(() => {
  fetchData()
  fetchUserList()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.photo-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
