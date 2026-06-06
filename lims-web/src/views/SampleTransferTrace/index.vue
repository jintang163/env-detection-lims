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
  SampleVO
} from '@/types'
import {
  getSamplePage,
  getTransferTimeline,
  transferSample,
  getTransferLogBySampleId
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

const operatorList = [
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' },
  { id: 4, name: '赵六' },
  { id: 5, name: '钱七' }
]

const mockSamples: SampleVO[] = [
  {
    id: 1,
    sampleCode: 'YP202401001',
    sampleName: '水质样品-001',
    pointName: '采样点A',
    matrix: 'water',
    matrixName: '水质',
    sampleStatus: 2,
    sampleStatusName: '检测中',
    currentNode: 3,
    currentNodeName: '前处理',
    currentNodeStatus: 2,
    entrustNo: 'WT202401001',
    samplingTime: '2024-01-15 09:30:00',
    sampleQuantity: 1000,
    sampleUnit: 'ml',
    createTime: '2024-01-15 10:00:00',
    _index: 0
  },
  {
    id: 2,
    sampleCode: 'YP202401002',
    sampleName: '土壤样品-002',
    pointName: '采样点B',
    matrix: 'soil',
    matrixName: '土壤',
    sampleStatus: 1,
    sampleStatusName: '待检测',
    currentNode: 1,
    currentNodeName: '交接',
    currentNodeStatus: 1,
    entrustNo: 'WT202401001',
    samplingTime: '2024-01-15 10:00:00',
    sampleQuantity: 500,
    sampleUnit: 'g',
    createTime: '2024-01-15 10:30:00',
    _index: 1
  },
  {
    id: 3,
    sampleCode: 'YP202401003',
    sampleName: '大气样品-003',
    pointName: '采样点C',
    matrix: 'air',
    matrixName: '大气',
    sampleStatus: 3,
    sampleStatusName: '已完成',
    currentNode: 6,
    currentNodeName: '留样',
    currentNodeStatus: 1,
    entrustNo: 'WT202401002',
    samplingTime: '2024-01-14 14:00:00',
    sampleQuantity: 10,
    sampleUnit: 'L',
    createTime: '2024-01-14 15:00:00',
    _index: 2
  },
  {
    id: 4,
    sampleCode: 'YP202401004',
    sampleName: '水质样品-004',
    pointName: '采样点D',
    matrix: 'water',
    matrixName: '水质',
    sampleStatus: 2,
    sampleStatusName: '检测中',
    currentNode: 4,
    currentNodeName: '上机',
    currentNodeStatus: 2,
    entrustNo: 'WT202401002',
    samplingTime: '2024-01-14 15:30:00',
    sampleQuantity: 800,
    sampleUnit: 'ml',
    createTime: '2024-01-14 16:00:00',
    _index: 3
  },
  {
    id: 5,
    sampleCode: 'YP202401005',
    sampleName: '土壤样品-005',
    pointName: '采样点E',
    matrix: 'soil',
    matrixName: '土壤',
    sampleStatus: 2,
    sampleStatusName: '检测中',
    currentNode: 5,
    currentNodeName: '审核',
    currentNodeStatus: 2,
    entrustNo: 'WT202401003',
    samplingTime: '2024-01-13 09:00:00',
    sampleQuantity: 600,
    sampleUnit: 'g',
    createTime: '2024-01-13 10:00:00',
    _index: 4
  },
  {
    id: 6,
    sampleCode: 'YP202401006',
    sampleName: '固废样品-006',
    pointName: '采样点F',
    matrix: 'solid',
    matrixName: '固废',
    sampleStatus: 1,
    sampleStatusName: '待检测',
    currentNode: 2,
    currentNodeName: '制样',
    currentNodeStatus: 2,
    entrustNo: 'WT202401003',
    samplingTime: '2024-01-16 08:30:00',
    sampleQuantity: 2000,
    sampleUnit: 'g',
    createTime: '2024-01-16 09:00:00',
    _index: 5
  },
  {
    id: 7,
    sampleCode: 'YP202401007',
    sampleName: '水质样品-007',
    pointName: '采样点G',
    matrix: 'water',
    matrixName: '水质',
    sampleStatus: 4,
    sampleStatusName: '已销毁',
    currentNode: 7,
    currentNodeName: '销毁',
    currentNodeStatus: 1,
    entrustNo: 'WT202401004',
    samplingTime: '2024-01-10 11:00:00',
    sampleQuantity: 500,
    sampleUnit: 'ml',
    createTime: '2024-01-10 12:00:00',
    _index: 6
  },
  {
    id: 8,
    sampleCode: 'YP202401008',
    sampleName: '大气样品-008',
    pointName: '采样点H',
    matrix: 'air',
    matrixName: '大气',
    sampleStatus: 1,
    sampleStatusName: '待检测',
    currentNode: 1,
    currentNodeName: '交接',
    currentNodeStatus: 2,
    entrustNo: 'WT202401004',
    samplingTime: '2024-01-16 14:00:00',
    sampleQuantity: 20,
    sampleUnit: 'L',
    createTime: '2024-01-16 15:00:00',
    _index: 7
  }
]

const mockTimelines: Record<number, SampleTransferTimelineItemVO[]> = {
  1: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 1, statusName: '已完成', operatorName: '张三', operateTime: '2024-01-15 10:00:00', remark: '样品接收完好，数量核对无误', duration: '30分钟' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 1, statusName: '已完成', operatorName: '李四', operateTime: '2024-01-15 14:30:00', remark: '制样完成，分装为2份', duration: '2小时' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 2, statusName: '进行中', operatorName: '王五', operateTime: '2024-01-16 09:00:00', remark: '正在进行消解处理', duration: '-' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 3, statusName: '待处理', duration: '-' }
  ],
  2: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 1, statusName: '已完成', operatorName: '张三', operateTime: '2024-01-15 10:30:00', remark: '土壤样品，外观正常', duration: '20分钟' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 3, statusName: '待处理', duration: '-' }
  ],
  3: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 1, statusName: '已完成', operatorName: '李四', operateTime: '2024-01-14 15:00:00', remark: '气袋样品，密封良好', duration: '15分钟' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 1, statusName: '已完成', operatorName: '王五', operateTime: '2024-01-14 16:00:00', remark: '无需制样，直接送检', duration: '10分钟' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 1, statusName: '已完成', operatorName: '赵六', operateTime: '2024-01-15 08:30:00', remark: '浓缩处理完成', duration: '3小时' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 1, statusName: '已完成', operatorName: '钱七', operateTime: '2024-01-15 14:00:00', remark: 'GC-MS检测完成', duration: '4小时' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 1, statusName: '已完成', operatorName: '张三', operateTime: '2024-01-16 09:00:00', remark: '数据审核通过，结果正常', duration: '1小时' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 1, statusName: '已完成', operatorName: '李四', operateTime: '2024-01-16 11:00:00', remark: '留样于冷藏柜A-01', duration: '30分钟' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 3, statusName: '待处理', duration: '-' }
  ],
  4: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 1, statusName: '已完成', operatorName: '张三', operateTime: '2024-01-14 16:00:00', remark: '水质样品，冷藏保存', duration: '25分钟' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 1, statusName: '已完成', operatorName: '李四', operateTime: '2024-01-15 08:00:00', remark: '过滤、酸化处理', duration: '1.5小时' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 1, statusName: '已完成', operatorName: '王五', operateTime: '2024-01-15 11:00:00', remark: '萃取完成', duration: '2.5小时' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 2, statusName: '进行中', operatorName: '赵六', operateTime: '2024-01-16 08:30:00', remark: 'HPLC检测中', duration: '-' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 3, statusName: '待处理', duration: '-' }
  ],
  5: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 1, statusName: '已完成', operatorName: '李四', operateTime: '2024-01-13 10:00:00', remark: '土壤样品，编号清晰', duration: '20分钟' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 1, statusName: '已完成', operatorName: '王五', operateTime: '2024-01-13 14:00:00', remark: '研磨、过筛完成', duration: '3小时' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 1, statusName: '已完成', operatorName: '赵六', operateTime: '2024-01-14 09:00:00', remark: '微波消解完成', duration: '4小时' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 1, statusName: '已完成', operatorName: '钱七', operateTime: '2024-01-15 08:00:00', remark: 'ICP检测完成', duration: '5小时' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 2, statusName: '进行中', operatorName: '张三', operateTime: '2024-01-16 09:30:00', remark: '数据审核中', duration: '-' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 3, statusName: '待处理', duration: '-' }
  ],
  6: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 1, statusName: '已完成', operatorName: '张三', operateTime: '2024-01-16 09:00:00', remark: '固废样品，需特殊处理', duration: '30分钟' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 2, statusName: '进行中', operatorName: '李四', operateTime: '2024-01-16 13:00:00', remark: '正在进行粉碎处理', duration: '-' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 3, statusName: '待处理', duration: '-' }
  ],
  7: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 1, statusName: '已完成', operatorName: '王五', operateTime: '2024-01-10 12:00:00', remark: '水质样品，已过保质期', duration: '15分钟' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 1, statusName: '已完成', operatorName: '赵六', operateTime: '2024-01-10 13:00:00', remark: '无需制样', duration: '5分钟' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 1, statusName: '已完成', operatorName: '钱七', operateTime: '2024-01-10 14:00:00', remark: '快速处理', duration: '1小时' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 1, statusName: '已完成', operatorName: '张三', operateTime: '2024-01-11 08:00:00', remark: '检测完成，数据异常', duration: '3小时' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 1, statusName: '已完成', operatorName: '李四', operateTime: '2024-01-11 14:00:00', remark: '审核不通过，样品过期', duration: '30分钟' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 1, statusName: '已完成', operatorName: '王五', operateTime: '2024-01-12 09:00:00', remark: '留样备查', duration: '20分钟' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 1, statusName: '已完成', operatorName: '赵六', operateTime: '2024-01-15 10:00:00', remark: '按危废处理流程销毁', duration: '2小时' }
  ],
  8: [
    { nodeCode: '1', nodeName: '交接', nodeOrder: 1, status: 2, statusName: '进行中', operatorName: '张三', operateTime: '2024-01-16 15:00:00', remark: '正在核对样品信息', duration: '-' },
    { nodeCode: '2', nodeName: '制样', nodeOrder: 2, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '3', nodeName: '前处理', nodeOrder: 3, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '4', nodeName: '上机', nodeOrder: 4, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '5', nodeName: '审核', nodeOrder: 5, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '6', nodeName: '留样', nodeOrder: 6, status: 3, statusName: '待处理', duration: '-' },
    { nodeCode: '7', nodeName: '销毁', nodeOrder: 7, status: 3, statusName: '待处理', duration: '-' }
  ]
}

const mockLogs: Record<number, SampleTransferLogVO[]> = {
  1: [
    { id: 1, sampleId: 1, sampleCode: 'YP202401001', sampleName: '水质样品-001', transferNode: 1, transferNodeName: '交接', operatorId: 1, operatorName: '张三', operateTime: '2024-01-15 10:00:00', remark: '样品接收完好，数量核对无误', duration: '30分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 0 },
    { id: 2, sampleId: 1, sampleCode: 'YP202401001', sampleName: '水质样品-001', transferNode: 2, transferNodeName: '制样', operatorId: 2, operatorName: '李四', operateTime: '2024-01-15 14:30:00', remark: '制样完成，分装为2份', duration: '2小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 1 },
    { id: 3, sampleId: 1, sampleCode: 'YP202401001', sampleName: '水质样品-001', transferNode: 3, transferNodeName: '前处理', operatorId: 3, operatorName: '王五', operateTime: '2024-01-16 09:00:00', remark: '正在进行消解处理', duration: '-', nodeStatus: 2, nodeStatusName: '进行中', _index: 2 }
  ],
  2: [
    { id: 4, sampleId: 2, sampleCode: 'YP202401002', sampleName: '土壤样品-002', transferNode: 1, transferNodeName: '交接', operatorId: 1, operatorName: '张三', operateTime: '2024-01-15 10:30:00', remark: '土壤样品，外观正常', duration: '20分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 0 }
  ],
  3: [
    { id: 5, sampleId: 3, sampleCode: 'YP202401003', sampleName: '大气样品-003', transferNode: 1, transferNodeName: '交接', operatorId: 2, operatorName: '李四', operateTime: '2024-01-14 15:00:00', remark: '气袋样品，密封良好', duration: '15分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 0 },
    { id: 6, sampleId: 3, sampleCode: 'YP202401003', sampleName: '大气样品-003', transferNode: 2, transferNodeName: '制样', operatorId: 3, operatorName: '王五', operateTime: '2024-01-14 16:00:00', remark: '无需制样，直接送检', duration: '10分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 1 },
    { id: 7, sampleId: 3, sampleCode: 'YP202401003', sampleName: '大气样品-003', transferNode: 3, transferNodeName: '前处理', operatorId: 4, operatorName: '赵六', operateTime: '2024-01-15 08:30:00', remark: '浓缩处理完成', duration: '3小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 2 },
    { id: 8, sampleId: 3, sampleCode: 'YP202401003', sampleName: '大气样品-003', transferNode: 4, transferNodeName: '上机', operatorId: 5, operatorName: '钱七', operateTime: '2024-01-15 14:00:00', remark: 'GC-MS检测完成', duration: '4小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 3 },
    { id: 9, sampleId: 3, sampleCode: 'YP202401003', sampleName: '大气样品-003', transferNode: 5, transferNodeName: '审核', operatorId: 1, operatorName: '张三', operateTime: '2024-01-16 09:00:00', remark: '数据审核通过，结果正常', duration: '1小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 4 },
    { id: 10, sampleId: 3, sampleCode: 'YP202401003', sampleName: '大气样品-003', transferNode: 6, transferNodeName: '留样', operatorId: 2, operatorName: '李四', operateTime: '2024-01-16 11:00:00', remark: '留样于冷藏柜A-01', duration: '30分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 5 }
  ],
  4: [
    { id: 11, sampleId: 4, sampleCode: 'YP202401004', sampleName: '水质样品-004', transferNode: 1, transferNodeName: '交接', operatorId: 1, operatorName: '张三', operateTime: '2024-01-14 16:00:00', remark: '水质样品，冷藏保存', duration: '25分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 0 },
    { id: 12, sampleId: 4, sampleCode: 'YP202401004', sampleName: '水质样品-004', transferNode: 2, transferNodeName: '制样', operatorId: 2, operatorName: '李四', operateTime: '2024-01-15 08:00:00', remark: '过滤、酸化处理', duration: '1.5小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 1 },
    { id: 13, sampleId: 4, sampleCode: 'YP202401004', sampleName: '水质样品-004', transferNode: 3, transferNodeName: '前处理', operatorId: 3, operatorName: '王五', operateTime: '2024-01-15 11:00:00', remark: '萃取完成', duration: '2.5小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 2 },
    { id: 14, sampleId: 4, sampleCode: 'YP202401004', sampleName: '水质样品-004', transferNode: 4, transferNodeName: '上机', operatorId: 4, operatorName: '赵六', operateTime: '2024-01-16 08:30:00', remark: 'HPLC检测中', duration: '-', nodeStatus: 2, nodeStatusName: '进行中', _index: 3 }
  ],
  5: [
    { id: 15, sampleId: 5, sampleCode: 'YP202401005', sampleName: '土壤样品-005', transferNode: 1, transferNodeName: '交接', operatorId: 2, operatorName: '李四', operateTime: '2024-01-13 10:00:00', remark: '土壤样品，编号清晰', duration: '20分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 0 },
    { id: 16, sampleId: 5, sampleCode: 'YP202401005', sampleName: '土壤样品-005', transferNode: 2, transferNodeName: '制样', operatorId: 3, operatorName: '王五', operateTime: '2024-01-13 14:00:00', remark: '研磨、过筛完成', duration: '3小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 1 },
    { id: 17, sampleId: 5, sampleCode: 'YP202401005', sampleName: '土壤样品-005', transferNode: 3, transferNodeName: '前处理', operatorId: 4, operatorName: '赵六', operateTime: '2024-01-14 09:00:00', remark: '微波消解完成', duration: '4小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 2 },
    { id: 18, sampleId: 5, sampleCode: 'YP202401005', sampleName: '土壤样品-005', transferNode: 4, transferNodeName: '上机', operatorId: 5, operatorName: '钱七', operateTime: '2024-01-15 08:00:00', remark: 'ICP检测完成', duration: '5小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 3 },
    { id: 19, sampleId: 5, sampleCode: 'YP202401005', sampleName: '土壤样品-005', transferNode: 5, transferNodeName: '审核', operatorId: 1, operatorName: '张三', operateTime: '2024-01-16 09:30:00', remark: '数据审核中', duration: '-', nodeStatus: 2, nodeStatusName: '进行中', _index: 4 }
  ],
  6: [
    { id: 20, sampleId: 6, sampleCode: 'YP202401006', sampleName: '固废样品-006', transferNode: 1, transferNodeName: '交接', operatorId: 1, operatorName: '张三', operateTime: '2024-01-16 09:00:00', remark: '固废样品，需特殊处理', duration: '30分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 0 },
    { id: 21, sampleId: 6, sampleCode: 'YP202401006', sampleName: '固废样品-006', transferNode: 2, transferNodeName: '制样', operatorId: 2, operatorName: '李四', operateTime: '2024-01-16 13:00:00', remark: '正在进行粉碎处理', duration: '-', nodeStatus: 2, nodeStatusName: '进行中', _index: 1 }
  ],
  7: [
    { id: 22, sampleId: 7, sampleCode: 'YP202401007', sampleName: '水质样品-007', transferNode: 1, transferNodeName: '交接', operatorId: 3, operatorName: '王五', operateTime: '2024-01-10 12:00:00', remark: '水质样品，已过保质期', duration: '15分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 0 },
    { id: 23, sampleId: 7, sampleCode: 'YP202401007', sampleName: '水质样品-007', transferNode: 2, transferNodeName: '制样', operatorId: 4, operatorName: '赵六', operateTime: '2024-01-10 13:00:00', remark: '无需制样', duration: '5分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 1 },
    { id: 24, sampleId: 7, sampleCode: 'YP202401007', sampleName: '水质样品-007', transferNode: 3, transferNodeName: '前处理', operatorId: 5, operatorName: '钱七', operateTime: '2024-01-10 14:00:00', remark: '快速处理', duration: '1小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 2 },
    { id: 25, sampleId: 7, sampleCode: 'YP202401007', sampleName: '水质样品-007', transferNode: 4, transferNodeName: '上机', operatorId: 1, operatorName: '张三', operateTime: '2024-01-11 08:00:00', remark: '检测完成，数据异常', duration: '3小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 3 },
    { id: 26, sampleId: 7, sampleCode: 'YP202401007', sampleName: '水质样品-007', transferNode: 5, transferNodeName: '审核', operatorId: 2, operatorName: '李四', operateTime: '2024-01-11 14:00:00', remark: '审核不通过，样品过期', duration: '30分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 4 },
    { id: 27, sampleId: 7, sampleCode: 'YP202401007', sampleName: '水质样品-007', transferNode: 6, transferNodeName: '留样', operatorId: 3, operatorName: '王五', operateTime: '2024-01-12 09:00:00', remark: '留样备查', duration: '20分钟', nodeStatus: 1, nodeStatusName: '已完成', _index: 5 },
    { id: 28, sampleId: 7, sampleCode: 'YP202401007', sampleName: '水质样品-007', transferNode: 7, transferNodeName: '销毁', operatorId: 4, operatorName: '赵六', operateTime: '2024-01-15 10:00:00', remark: '按危废处理流程销毁', duration: '2小时', nodeStatus: 1, nodeStatusName: '已完成', _index: 6 }
  ],
  8: [
    { id: 29, sampleId: 8, sampleCode: 'YP202401008', sampleName: '大气样品-008', transferNode: 1, transferNodeName: '交接', operatorId: 1, operatorName: '张三', operateTime: '2024-01-16 15:00:00', remark: '正在核对样品信息', duration: '-', nodeStatus: 2, nodeStatusName: '进行中', _index: 0 }
  ]
}

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
  total: 8,
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

const fetchSampleData = async () => {
  sampleLoading.value = true
  try {
    let filteredData = [...mockSamples]
    
    if (queryParams.sampleCode) {
      filteredData = filteredData.filter(s => s.sampleCode.includes(queryParams.sampleCode!))
    }
    if (queryParams.sampleName) {
      filteredData = filteredData.filter(s => s.sampleName.includes(queryParams.sampleName!))
    }
    if (queryParams.transferNode) {
      filteredData = filteredData.filter(s => s.currentNode === queryParams.transferNode)
    }
    
    const start = (samplePagination.value.current - 1) * samplePagination.value.pageSize
    const end = start + samplePagination.value.pageSize
    sampleList.value = filteredData.slice(start, end).map((s, i) => ({ ...s, _index: i }))
    samplePagination.value.total = filteredData.length
  } finally {
    sampleLoading.value = false
  }
}

const fetchTimelineData = async (sampleId: number) => {
  try {
    const timeline = mockTimelines[sampleId] || []
    timelineData.value = {
      sampleId,
      sampleCode: selectedSample.value?.sampleCode || '',
      sampleName: selectedSample.value?.sampleName || '',
      currentNode: selectedSample.value?.currentNode || 1,
      currentNodeName: selectedSample.value?.currentNodeName || '',
      timeline
    }
  } catch (error) {
    console.error('Get timeline error:', error)
  }
}

const fetchTransferLogData = async (sampleId: number) => {
  logLoading.value = true
  try {
    const logs = mockLogs[sampleId] || []
    transferLogList.value = logs
    logPagination.value.total = logs.length
  } finally {
    logLoading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  samplePagination.value.current = 1
  if (operateTimeRange.value && operateTimeRange.value.length === 2) {
    queryParams.operateTimeStart = operateTimeRange.value[0]
    queryParams.operateTimeEnd = operateTimeRange.value[1]
  }
  fetchSampleData()
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
  fetchSampleData()
}

const handleSampleClick = (record: SampleVO) => {
  selectedSample.value = record
  if (detailTab.value === 'timeline') {
    fetchTimelineData(record.id)
  } else {
    fetchTransferLogData(record.id)
  }
}

const handleDetailTabChange = (key: string) => {
  if (!selectedSample.value) return
  if (key === 'timeline') {
    fetchTimelineData(selectedSample.value.id)
  } else {
    fetchTransferLogData(selectedSample.value.id)
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
    message.success('流转操作成功')
    transferModalVisible.value = false
    fetchSampleData()
    if (selectedSample.value) {
      if (detailTab.value === 'timeline') {
        fetchTimelineData(selectedSample.value.id)
      } else {
        fetchTransferLogData(selectedSample.value.id)
      }
    }
  } catch (error) {
    console.error('Transfer submit error:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchSampleData()
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
