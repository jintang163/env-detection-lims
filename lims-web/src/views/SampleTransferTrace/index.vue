<template>
  <div class="sample-transfer-trace">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="样品编号">
            <a-input v-model:value="queryParams.sampleCode" placeholder="请输入样品编号" style="width: 150px" allow-clear />
          </a-form-item>
          <a-form-item label="样品名称">
            <a-input v-model:value="queryParams.sampleName" placeholder="请输入样品名称" style="width: 150px" allow-clear />
          </a-form-item>
          <a-form-item label="流转节点">
            <a-select v-model:value="queryParams.transferNode" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option v-for="node in transferNodes" :key="node.value" :value="node.value">
                {{ node.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="操作人">
            <a-select v-model:value="queryParams.operatorId" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option v-for="user in operatorList" :key="user.id" :value="user.id">
                {{ user.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="操作时间">
            <a-range-picker
              v-model:value="operateTimeRange"
              show-time
              style="width: 280px"
              value-format="YYYY-MM-DD HH:mm:ss"
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
        <div class="toolbar-right">
          <a-button type="primary" @click="handleBatchTransfer">
            <SwapOutlined /> 批量流转
          </a-button>
        </div>
      </div>

      <a-row :gutter="16">
        <a-col :span="10">
          <a-card title="样品列表" size="small" :bordered="false" class="left-panel">
            <a-table
              :columns="sampleColumns"
              :data-source="sampleList"
              :loading="sampleLoading"
              :pagination="samplePagination"
              row-key="id"
              :row-selection="rowSelection"
              :row-class-name="getSampleRowClassName"
              @change="handleSampleTableChange"
              @row-click="handleSampleClick"
              size="small"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'index'">
                  {{ (samplePagination.current - 1) * samplePagination.pageSize + record._index + 1 }}
                </template>
                <template v-else-if="column.key === 'currentNode'">
                  <a-tag :color="getNodeStatusColor(record.currentNodeStatus)">
                    {{ record.currentNodeName || '-' }}
                  </a-tag>
                </template>
                <template v-else-if="column.key === 'sampleStatus'">
                  <a-tag :color="getSampleStatusColor(record.sampleStatus)">
                    {{ record.sampleStatusName || '-' }}
                  </a-tag>
                </template>
              </template>
            </a-table>
          </a-card>
        </a-col>

        <a-col :span="14">
          <a-card size="small" :bordered="false" class="right-panel">
            <template #title>
              <span v-if="selectedSample">
                <FileTextOutlined /> {{ selectedSample.sampleCode }} - {{ selectedSample.sampleName }}
              </span>
              <span v-else>流转跟踪</span>
            </template>
            <a-tabs v-model:activeKey="detailTab" @change="handleDetailTabChange">
              <a-tab-pane key="timeline" tab="流转时间线">
                <div v-if="timelineData" class="timeline-container">
                  <a-timeline mode="left" class="custom-timeline">
                    <a-timeline-item
                      v-for="(item, index) in timelineData.timeline"
                      :key="item.nodeCode"
                      :color="getTimelineColor(item.status)"
                    >
                      <template #dot>
                        <div class="timeline-dot" :class="getDotClass(item.status)">
                          <CheckCircleOutlined v-if="item.status === 1" />
                          <PlayCircleOutlined v-else-if="item.status === 2" />
                          <ClockCircleOutlined v-else />
                        </div>
                      </template>
                      <template #label>
                        <div class="timeline-label">
                          <span class="node-name" :class="getLabelClass(item.status)">
                            {{ item.nodeName }}
                          </span>
                          <span v-if="item.duration" class="duration">
                            耗时: {{ item.duration }}
                          </span>
                        </div>
                      </template>
                      <div class="timeline-content" :class="getContentClass(item.status)">
                        <div v-if="item.status !== 3" class="content-card">
                          <div class="content-header">
                            <span v-if="item.operatorName" class="operator">
                              <UserOutlined /> {{ item.operatorName }}
                            </span>
                            <span v-if="item.operateTime" class="time">
                              {{ item.operateTime }}
                            </span>
                          </div>
                          <div v-if="item.remark" class="content-body">
                            <span class="remark-label">备注:</span>
                            <span class="remark-content">{{ item.remark }}</span>
                          </div>
                        </div>
                        <div v-else class="pending-content">
                          <span class="pending-text">待处理</span>
                        </div>
                      </div>
                    </a-timeline-item>
                  </a-timeline>
                </div>
                <div v-else class="empty-state">
                  <a-empty description="请选择左侧样品查看流转时间线" />
                </div>
              </a-tab-pane>

              <a-tab-pane key="log" tab="流转日志">
                <div v-if="selectedSample">
                  <a-table
                    :columns="logColumns"
                    :data-source="transferLogList"
                    :loading="logLoading"
                    :pagination="logPagination"
                    row-key="id"
                    size="small"
                  >
                    <template #bodyCell="{ column, record }">
                      <template v-if="column.key === 'index'">
                        {{ record._index + 1 }}
                      </template>
                      <template v-else-if="column.key === 'transferNode'">
                        <a-tag :color="getNodeStatusColor(record.nodeStatus)">
                          {{ record.transferNodeName }}
                        </a-tag>
                      </template>
                      <template v-else-if="column.key === 'nodeStatus'">
                        <a-tag :color="getNodeStatusColor(record.nodeStatus)">
                          {{ record.nodeStatusName || '-' }}
                        </a-tag>
                      </template>
                    </template>
                  </a-table>
                </div>
                <div v-else class="empty-state">
                  <a-empty description="请选择左侧样品查看流转日志" />
                </div>
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </a-col>
      </a-row>
    </a-card>

    <a-modal
      v-model:open="transferModalVisible"
      title="样品流转"
      width="600px"
      @ok="handleTransferSubmit"
      @cancel="transferModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="transferFormRef"
        :model="transferFormData"
        :rules="transferFormRules"
        layout="vertical"
      >
        <a-form-item label="已选样品">
          <a-tag v-for="sample in selectedSamplesForTransfer" :key="sample.id" color="blue" closable @close="removeSampleFromTransfer(sample.id)">
            {{ sample.sampleCode }}
          </a-tag>
          <span v-if="selectedSamplesForTransfer.length === 0" class="text-gray">未选择样品</span>
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="当前节点" name="transferNode">
              <a-select v-model:value="transferFormData.transferNode" placeholder="请选择当前节点">
                <a-select-option v-for="node in transferNodes" :key="node.value" :value="node.value">
                  {{ node.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="下一节点" name="nextNode">
              <a-select v-model:value="transferFormData.nextNode" placeholder="请选择下一节点">
                <a-select-option v-for="node in nextNodeOptions" :key="node.value" :value="node.value">
                  {{ node.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="操作人" name="operatorId">
              <a-select v-model:value="transferFormData.operatorId" placeholder="请选择操作人">
                <a-select-option v-for="user in operatorList" :key="user.id" :value="user.id">
                  {{ user.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="操作时间" name="operateTime">
              <a-date-picker
                v-model:value="transferFormData.operateTime"
                show-time
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注" name="remark">
          <a-textarea v-model:value="transferFormData.remark" :rows="3" placeholder="请输入备注信息" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import type { TableRowSelection } from 'ant-design-vue/es/table/interface'
import {
  SearchOutlined,
  ReloadOutlined,
  SwapOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  PlayCircleOutlined,
  PlusOutlined,
  UserOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue'
import type {
  SampleTransferQuery,
  SampleTransferSaveDTO,
  SampleTransferLogVO,
  SampleTransferTimelineVO,
  SampleTransferTimelineItemVO,
  SampleVO,
  UserInfo
} from '@/types'
import {
  getSamplePage,
  getTransferTimeline,
  transferSample,
  getTransferLogBySampleId,
  getUserList
} from '@/api/sample'

const sampleLoading = ref(false)
const logLoading = ref(false)
const submitting = ref(false)
const detailTab = ref('timeline')

const transferModalVisible = ref(false)
const transferFormRef = ref()

const operateTimeRange = ref<any[]>([])
const selectedSample = ref<SampleVO | null>(null)
const selectedSampleIds = ref<number[]>([])
const timelineData = ref<SampleTransferTimelineVO | null>(null)
const transferLogList = ref<SampleTransferLogVO[]>([])

const transferNodes = [
  { value: 1, label: '交接' },
  { value: 2, label: '制样' },
  { value: 3, label: '前处理' },
  { value: 4, label: '上机' },
  { value: 5, label: '审核' },
  { value: 6, label: '留样' },
  { value: 7, label: '销毁' }
]

const operatorList = ref<{ id: number; name: string }[]>([])

const queryParams = reactive<SampleTransferQuery>({
  pageNum: 1,
  pageSize: 10,
  sampleCode: '',
  sampleName: '',
  transferNode: undefined,
  operatorId: undefined,
  operateTimeStart: '',
  operateTimeEnd: ''
})

const sampleList = ref<SampleVO[]>([])

const samplePagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const logPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const sampleColumns = [
  { title: '序号', key: 'index', width: 60 },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 130 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 130, ellipsis: true },
  { title: '基质', dataIndex: 'matrixName', key: 'matrixName', width: 80 },
  { title: '当前节点', key: 'currentNode', width: 100 },
  { title: '样品状态', key: 'sampleStatus', width: 100 }
]

const logColumns = [
  { title: '序号', key: 'index', width: 60 },
  { title: '流转节点', key: 'transferNode', width: 100 },
  { title: '节点状态', key: 'nodeStatus', width: 100 },
  { title: '操作人', dataIndex: 'operatorName', key: 'operatorName', width: 100 },
  { title: '操作时间', dataIndex: 'operateTime', key: 'operateTime', width: 170 },
  { title: '耗时', dataIndex: 'duration', key: 'duration', width: 100 },
  { title: '备注', dataIndex: 'remark', key: 'remark', ellipsis: true }
]

const transferFormData = reactive<SampleTransferSaveDTO>({
  sampleIds: [],
  transferNode: undefined,
  operatorId: undefined,
  operateTime: '',
  remark: '',
  nextNode: undefined
})

const transferFormRules = {
  sampleIds: [{ required: true, message: '请选择样品', trigger: 'change' }],
  transferNode: [{ required: true, message: '请选择当前节点', trigger: 'change' }],
  operatorId: [{ required: true, message: '请选择操作人', trigger: 'change' }],
  operateTime: [{ required: true, message: '请选择操作时间', trigger: 'change' }]
}

const selectedSamplesForTransfer = computed(() => {
  return sampleList.value.filter(s => selectedSampleIds.value.includes(s.id))
})

const nextNodeOptions = computed(() => {
  if (!transferFormData.transferNode) return transferNodes
  const currentIndex = transferNodes.findIndex(n => n.value === transferFormData.transferNode)
  return transferNodes.filter((_, index) => index > currentIndex)
})

const rowSelection: TableRowSelection = {
  selectedRowKeys: selectedSampleIds,
  onChange: (keys: number[]) => {
    selectedSampleIds.value = keys
  }
}

const getNodeStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 1: 'green', 2: 'blue', 3: 'default' }
  return colors[status || 0] || 'default'
}

const getSampleStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 1: 'orange', 2: 'processing', 3: 'success', 4: 'default' }
  return colors[status || 0] || 'default'
}

const getTimelineColor = (status: number) => {
  const colors: Record<number, string> = { 1: 'green', 2: 'blue', 3: 'gray' }
  return colors[status] || 'gray'
}

const getDotClass = (status: number) => {
  const classes: Record<number, string> = { 1: 'dot-completed', 2: 'dot-current', 3: 'dot-pending' }
  return classes[status] || 'dot-pending'
}

const getLabelClass = (status: number) => {
  const classes: Record<number, string> = { 1: 'label-completed', 2: 'label-current', 3: 'label-pending' }
  return classes[status] || 'label-pending'
}

const getContentClass = (status: number) => {
  const classes: Record<number, string> = { 1: 'content-completed', 2: 'content-current', 3: 'content-pending' }
  return classes[status] || 'content-pending'
}

const getSampleRowClassName = (record: SampleVO) => {
  if (selectedSample.value?.id === record.id) {
    return 'row-selected'
  }
  return ''
}

const fetchSampleList = async () => {
  sampleLoading.value = true
  try {
    const res = await getSamplePage(queryParams)
    if (res.code === 200 && res.data) {
      sampleList.value = res.data.list.map((s, i) => ({ ...s, _index: i }))
      samplePagination.value.total = res.data.total
    } else {
      sampleList.value = []
      samplePagination.value.total = 0
      message.error(res.message || '获取样品列表失败')
    }
  } catch (error: any) {
    sampleList.value = []
    samplePagination.value.total = 0
    message.error(error.message || '获取样品列表失败')
  } finally {
    sampleLoading.value = false
  }
}

const fetchTimelineData = async (sampleId: number) => {
  try {
    const res = await getTransferTimeline(sampleId)
    if (res.code === 200 && res.data) {
      timelineData.value = res.data
    } else {
      timelineData.value = null
      message.error(res.message || '获取流转时间线失败')
    }
  } catch (error: any) {
    timelineData.value = null
    message.error(error.message || '获取流转时间线失败')
  }
}

const fetchTransferLog = async (sampleId: number) => {
  logLoading.value = true
  try {
    const res = await getTransferLogBySampleId(sampleId)
    if (res.code === 200 && res.data) {
      transferLogList.value = res.data.map((l, i) => ({ ...l, _index: i }))
      logPagination.value.total = res.data.length
    } else {
      transferLogList.value = []
      logPagination.value.total = 0
      message.error(res.message || '获取流转日志失败')
    }
  } catch (error: any) {
    transferLogList.value = []
    logPagination.value.total = 0
    message.error(error.message || '获取流转日志失败')
  } finally {
    logLoading.value = false
  }
}

const fetchOperators = async () => {
  try {
    const res = await getUserList()
    if (res.code === 200 && res.data) {
      operatorList.value = res.data.map((u: UserInfo) => ({ id: u.id, name: u.realName || u.username }))
    } else {
      operatorList.value = []
      message.error(res.message || '获取操作人列表失败')
    }
  } catch (error: any) {
    operatorList.value = []
    message.error(error.message || '获取操作人列表失败')
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  samplePagination.value.current = 1
  if (operateTimeRange.value && operateTimeRange.value.length === 2) {
    queryParams.operateTimeStart = operateTimeRange.value[0]
    queryParams.operateTimeEnd = operateTimeRange.value[1]
  }
  fetchSampleList()
}

const handleReset = () => {
  Object.assign(queryParams, {
    sampleCode: '',
    sampleName: '',
    transferNode: undefined,
    operatorId: undefined,
    operateTimeStart: '',
    operateTimeEnd: ''
  })
  operateTimeRange.value = []
  handleQuery()
}

const handleSampleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  samplePagination.value.current = pag.current
  samplePagination.value.pageSize = pag.pageSize
  fetchSampleList()
}

const handleSampleClick = (record: SampleVO) => {
  selectedSample.value = record
  if (detailTab.value === 'timeline') {
    fetchTimelineData(record.id)
  } else {
    fetchTransferLog(record.id)
  }
}

const handleDetailTabChange = (key: string) => {
  if (!selectedSample.value) return
  if (key === 'timeline') {
    fetchTimelineData(selectedSample.value.id)
  } else {
    fetchTransferLog(selectedSample.value.id)
  }
}

const handleBatchTransfer = () => {
  if (selectedSampleIds.value.length === 0) {
    message.warning('请先选择要流转的样品')
    return
  }
  Object.assign(transferFormData, {
    sampleIds: [...selectedSampleIds.value],
    transferNode: undefined,
    operatorId: undefined,
    operateTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    remark: '',
    nextNode: undefined
  })
  transferModalVisible.value = true
}

const removeSampleFromTransfer = (id: number) => {
  transferFormData.sampleIds = transferFormData.sampleIds.filter(sid => sid !== id)
  selectedSampleIds.value = selectedSampleIds.value.filter(sid => sid !== id)
}

const handleTransferSubmit = async () => {
  try {
    await transferFormRef.value.validate()
    submitting.value = true
    const res = await transferSample(transferFormData)
    if (res.code === 200) {
      message.success('流转操作成功')
      transferModalVisible.value = false
      fetchSampleList()
      if (selectedSample.value) {
        if (detailTab.value === 'timeline') {
          fetchTimelineData(selectedSample.value.id)
        } else {
          fetchTransferLog(selectedSample.value.id)
        }
      }
    } else {
      message.error(res.message || '流转操作失败')
    }
  } catch (error: any) {
    if (error.errorFields) {
      return
    }
    message.error(error.message || '流转操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchSampleList()
  fetchOperators()
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

.toolbar-right {
  flex-shrink: 0;
}

.left-panel,
.right-panel {
  height: calc(100vh - 280px);
  min-height: 500px;
}

.left-panel :deep(.ant-card-body),
.right-panel :deep(.ant-card-body) {
  height: calc(100% - 57px);
  overflow: auto;
  padding: 12px;
}

.row-selected {
  background-color: #e6f7ff !important;
}

.timeline-container {
  padding: 20px 10px;
  height: 100%;
  overflow: auto;
}

.custom-timeline {
  padding-left: 20px;
}

.timeline-dot {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 14px;
}

.dot-completed {
  background-color: #fff;
  color: #52c41a;
  border: 2px solid #52c41a;
}

.dot-current {
  background-color: #fff;
  color: #1890ff;
  border: 2px solid #1890ff;
  animation: pulse 2s infinite;
}

.dot-pending {
  background-color: #fff;
  color: #bfbfbf;
  border: 2px solid #bfbfbf;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(24, 144, 255, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(24, 144, 255, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(24, 144, 255, 0);
  }
}

.timeline-label {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-right: 16px;
}

.node-name {
  font-weight: 600;
  font-size: 14px;
}

.label-completed {
  color: #52c41a;
}

.label-current {
  color: #1890ff;
}

.label-pending {
  color: #bfbfbf;
}

.duration {
  font-size: 12px;
  color: #8c8c8c;
}

.timeline-content {
  margin-left: 16px;
}

.content-card {
  background: #fafafa;
  border-radius: 6px;
  padding: 12px 16px;
  border-left: 3px solid #e8e8e8;
}

.content-completed .content-card {
  border-left-color: #52c41a;
  background: #f6ffed;
}

.content-current .content-card {
  border-left-color: #1890ff;
  background: #e6f7ff;
}

.content-pending .pending-content {
  background: #f5f5f5;
  border-radius: 6px;
  padding: 12px 16px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  flex-wrap: wrap;
  gap: 8px;
}

.operator {
  font-size: 13px;
  color: #595959;
  display: flex;
  align-items: center;
  gap: 4px;
}

.time {
  font-size: 12px;
  color: #8c8c8c;
}

.content-body {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.remark-label {
  font-size: 12px;
  color: #8c8c8c;
  flex-shrink: 0;
}

.remark-content {
  font-size: 13px;
  color: #262626;
  line-height: 1.6;
}

.pending-text {
  font-size: 13px;
  color: #bfbfbf;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.text-gray {
  color: #bfbfbf;
}

:deep(.ant-timeline-item-last > .ant-timeline-item-tail) {
  display: none;
}

:deep(.ant-timeline-item-content) {
  min-height: 60px;
}
</style>
