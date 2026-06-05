<template>
  <div class="sampling-plan">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="计划编号">
            <a-input v-model:value="queryParams.planNo" placeholder="请输入计划编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="计划名称">
            <a-input v-model:value="queryParams.planName" placeholder="请输入计划名称" style="width: 180px" allow-clear />
          </a-form-item>
          <a-form-item label="采样类型">
            <a-select v-model:value="queryParams.samplingType" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="1">环境空气</a-select-option>
              <a-select-option :value="2">地表水</a-select-option>
              <a-select-option :value="3">地下水</a-select-option>
              <a-select-option :value="4">土壤</a-select-option>
              <a-select-option :value="5">噪声</a-select-option>
              <a-select-option :value="6">其他</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.status" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">草稿</a-select-option>
              <a-select-option :value="1">待分配</a-select-option>
              <a-select-option :value="2">已分配</a-select-option>
              <a-select-option :value="3">采样中</a-select-option>
              <a-select-option :value="4">已完成</a-select-option>
              <a-select-option :value="5">已取消</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="计划日期">
            <a-range-picker
              v-model:value="planDateRange"
              style="width: 260px"
              value-format="YYYY-MM-DD"
              @change="handlePlanDateChange"
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
          <a-button type="primary" @click="handleAdd">
            <PlusOutlined /> 新增计划
          </a-button>
        </div>
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
          <template v-if="column.key === 'samplingType'">
            <a-tag :color="getSamplingTypeColor(record.samplingType)">
              {{ record.samplingTypeName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
            <template v-if="record.status === 0">
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-button type="link" size="small" @click="handleSubmit(record)">提交</a-button>
              <a-popconfirm title="确定删除该计划吗？" @confirm="handleDelete(record.id)">
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </template>
            <template v-if="record.status === 1">
              <a-button type="link" size="small" @click="handleAssign(record)">任务分配</a-button>
            </template>
            <template v-if="record.status !== 4 && record.status !== 5">
              <a-button type="link" danger size="small" @click="handleCancel(record)">取消</a-button>
            </template>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      :title="formModalTitle"
      width="1000px"
      @ok="handleFormSubmit"
      @cancel="formModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="计划名称" name="planName">
              <a-input v-model:value="formData.planName" placeholder="请输入计划名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="采样类型" name="samplingType">
              <a-select v-model:value="formData.samplingType" placeholder="请选择采样类型">
                <a-select-option :value="1">环境空气</a-select-option>
                <a-select-option :value="2">地表水</a-select-option>
                <a-select-option :value="3">地下水</a-select-option>
                <a-select-option :value="4">土壤</a-select-option>
                <a-select-option :value="5">噪声</a-select-option>
                <a-select-option :value="6">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="计划日期" name="planDate">
              <a-date-picker v-model:value="formData.planDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="关联委托">
              <a-select
                v-model:value="formData.entrustId"
                placeholder="请选择委托"
                show-search
                allow-clear
                @change="handleEntrustChange"
              >
                <a-select-option v-for="item in entrustList" :key="item.id" :value="item.id">
                  {{ item.entrustNo }} - {{ item.customerName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="采样人员">
              <a-select
                v-model:value="selectedSamplerIds"
                mode="multiple"
                placeholder="请选择采样人员"
                @change="handleSamplerChange"
              >
                <a-select-option v-for="item in userList" :key="item.id" :value="item.id">
                  {{ item.realName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="采样设备">
              <a-select
                v-model:value="selectedEquipmentIds"
                mode="multiple"
                placeholder="请选择采样设备"
                @change="handleEquipmentChange"
              >
                <a-select-option v-for="item in equipmentList" :key="item.id" :value="item.id">
                  {{ item.equipmentName }} ({{ item.equipmentNo }})
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="样品容器">
              <a-input v-model:value="formData.containerNames" placeholder="请输入样品容器" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="点位清单">
          <a-table
            :columns="pointColumns"
            :data-source="formData.points"
            row-key="id"
            size="small"
            :pagination="false"
          >
            <template #bodyCell="{ column, record, index }">
              <template v-if="column.key === 'index'">
                {{ index + 1 }}
              </template>
              <template v-else-if="column.key === 'pointCode'">
                <a-input v-model:value="record.pointCode" size="small" placeholder="点位编号" />
              </template>
              <template v-else-if="column.key === 'pointName'">
                <a-input v-model:value="record.pointName" size="small" placeholder="点位名称" />
              </template>
              <template v-else-if="column.key === 'longitude'">
                <a-input-number v-model:value="record.longitude" size="small" placeholder="经度" style="width: 100%" />
              </template>
              <template v-else-if="column.key === 'latitude'">
                <a-input-number v-model:value="record.latitude" size="small" placeholder="纬度" style="width: 100%" />
              </template>
              <template v-else-if="column.key === 'samplingDepth'">
                <a-input v-model:value="record.samplingDepth" size="small" placeholder="采样深度" />
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button type="link" danger size="small" @click="handleRemovePoint(index)">删除</a-button>
              </template>
            </template>
          </a-table>
          <a-button type="dashed" style="width: 100%; margin-top: 8px" @click="handleAddPoint">
            <PlusOutlined /> 添加点位
          </a-button>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="采样计划详情"
      width="1100px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="计划编号">{{ planDetail?.planNo }}</a-descriptions-item>
            <a-descriptions-item label="计划名称">{{ planDetail?.planName }}</a-descriptions-item>
            <a-descriptions-item label="采样类型">
              <a-tag :color="getSamplingTypeColor(planDetail?.samplingType)">
                {{ planDetail?.samplingTypeName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="计划日期">{{ planDetail?.planDate }}</a-descriptions-item>
            <a-descriptions-item label="关联委托">{{ planDetail?.entrustNo || '-' }}</a-descriptions-item>
            <a-descriptions-item label="点位数量">{{ planDetail?.pointCount || 0 }}</a-descriptions-item>
            <a-descriptions-item label="任务数量">{{ planDetail?.taskCount || 0 }}</a-descriptions-item>
            <a-descriptions-item label="状态">
              <a-tag :color="getStatusColor(planDetail?.status)">
                {{ planDetail?.statusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="采样人员">{{ planDetail?.samplerNames || '-' }}</a-descriptions-item>
            <a-descriptions-item label="采样设备">{{ planDetail?.equipmentNames || '-' }}</a-descriptions-item>
            <a-descriptions-item label="样品容器">{{ planDetail?.containerNames || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建人">{{ planDetail?.createByName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ planDetail?.createTime }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ planDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>
        <a-tab-pane key="points" tab="点位清单">
          <a-table
            :columns="pointDetailColumns"
            :data-source="planDetail?.points || []"
            row-key="id"
            size="small"
            :pagination="false"
          />
        </a-tab-pane>
        <a-tab-pane key="tasks" tab="采样任务">
          <a-table
            :columns="taskDetailColumns"
            :data-source="planDetail?.tasks || []"
            row-key="id"
            size="small"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="getTaskStatusColor(record.status)">
                  {{ record.statusName }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <a-modal
      v-model:open="assignModalVisible"
      title="任务分配"
      width="800px"
      @ok="handleAssignSubmit"
      @cancel="assignModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
    >
      <a-descriptions :column="2" bordered size="small" style="margin-bottom: 16px">
        <a-descriptions-item label="计划编号">{{ currentPlan?.planNo }}</a-descriptions-item>
        <a-descriptions-item label="计划名称">{{ currentPlan?.planName }}</a-descriptions-item>
      </a-descriptions>
      <a-table
        :columns="assignColumns"
        :data-source="assignTaskList"
        row-key="pointId"
        size="small"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'index'">
            {{ record.index }}
          </template>
          <template v-else-if="column.key === 'samplerId'">
            <a-select
              v-model:value="record.samplerId"
              placeholder="请选择采样员"
              @change="(val: number) => handleSamplerSelect(val, record)"
              style="width: 100%"
            >
              <a-select-option v-for="item in userList" :key="item.id" :value="item.id">
                {{ item.realName }}
              </a-select-option>
            </a-select>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import type {
  SamplingPlanQuery,
  SamplingPlanSaveDTO,
  SamplingPlanVO,
  SamplingPlanDetailVO,
  SamplingPointSaveDTO,
  TaskAssignDTO,
  TaskAssignItem
} from '@/types'
import {
  getSamplingPlanPage,
  getSamplingPlanById,
  addSamplingPlan,
  updateSamplingPlan,
  deleteSamplingPlan,
  submitSamplingPlan,
  assignSamplingTask,
  cancelSamplingPlan
} from '@/api/sampling'
import { getEntrustSelectList } from '@/api/entrust'
import { getAvailableEquipments } from '@/api/sampling'

const loading = ref(false)
const submitting = ref(false)
const detailTab = ref('basic')

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)
const assignModalVisible = ref(false)

const formRef = ref()
const isEdit = ref(false)
const currentPlan = ref<SamplingPlanVO | null>(null)
const planDateRange = ref<any[]>([])

const entrustList = ref<any[]>([])
const userList = ref<any[]>([])
const equipmentList = ref<any[]>([])
const selectedSamplerIds = ref<number[]>([])
const selectedEquipmentIds = ref<number[]>([])

const queryParams = reactive<SamplingPlanQuery>({
  pageNum: 1,
  pageSize: 10,
  planNo: '',
  planName: '',
  samplingType: undefined,
  status: undefined,
  planDateStart: '',
  planDateEnd: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<SamplingPlanVO[]>([])
const planDetail = ref<SamplingPlanDetailVO | null>(null)
const assignTaskList = ref<TaskAssignItem[]>([])

const formData = reactive<SamplingPlanSaveDTO>({
  planName: '',
  planDate: '',
  points: []
})

const formRules = {
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  planDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }],
  samplingType: [{ required: true, message: '请选择采样类型', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '计划编号', dataIndex: 'planNo', key: 'planNo', width: 140 },
  { title: '计划名称', dataIndex: 'planName', key: 'planName', width: 200, ellipsis: true },
  { title: '委托编号', dataIndex: 'entrustNo', key: 'entrustNo', width: 140 },
  { title: '采样类型', dataIndex: 'samplingType', key: 'samplingType', width: 100 },
  { title: '计划日期', dataIndex: 'planDate', key: 'planDate', width: 120 },
  { title: '点位数', dataIndex: 'pointCount', key: 'pointCount', width: 80 },
  { title: '任务数', dataIndex: 'taskCount', key: 'taskCount', width: 80 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '采样人员', dataIndex: 'samplerNames', key: 'samplerNames', width: 150, ellipsis: true },
  { title: '创建人', dataIndex: 'createByName', key: 'createByName', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 320, fixed: 'right' }
]

const pointColumns = [
  { title: '序号', key: 'index', width: 60 },
  { title: '点位编号', dataIndex: 'pointCode', key: 'pointCode', width: 120 },
  { title: '点位名称', dataIndex: 'pointName', key: 'pointName', width: 150 },
  { title: '经度', dataIndex: 'longitude', key: 'longitude', width: 120 },
  { title: '纬度', dataIndex: 'latitude', key: 'latitude', width: 120 },
  { title: '采样深度', dataIndex: 'samplingDepth', key: 'samplingDepth', width: 100 },
  { title: '操作', key: 'action', width: 80 }
]

const pointDetailColumns = [
  { title: '序号', key: 'index', width: 60, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '点位编号', dataIndex: 'pointCode', key: 'pointCode', width: 120 },
  { title: '点位名称', dataIndex: 'pointName', key: 'pointName', width: 150 },
  { title: '经度', dataIndex: 'longitude', key: 'longitude', width: 120 },
  { title: '纬度', dataIndex: 'latitude', key: 'latitude', width: 120 },
  { title: '采样深度', dataIndex: 'samplingDepth', key: 'samplingDepth', width: 100 },
  { title: '采样频次', dataIndex: 'samplingFrequency', key: 'samplingFrequency', width: 100 },
  { title: '检测项目', dataIndex: 'samplingItems', key: 'samplingItems', width: 150 },
  { title: '备注', dataIndex: 'remark', key: 'remark' }
]

const taskDetailColumns = [
  { title: '序号', key: 'index', width: 60, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '任务编号', dataIndex: 'taskNo', key: 'taskNo', width: 140 },
  { title: '点位名称', dataIndex: 'pointName', key: 'pointName', width: 150 },
  { title: '采样员', dataIndex: 'samplerName', key: 'samplerName', width: 100 },
  { title: '分配时间', dataIndex: 'assignTime', key: 'assignTime', width: 170 },
  { title: '样品数', dataIndex: 'sampleCount', key: 'sampleCount', width: 80 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 }
]

const assignColumns = [
  { title: '序号', key: 'index', width: 60 },
  { title: '点位编号', dataIndex: 'pointCode', key: 'pointCode', width: 120 },
  { title: '点位名称', dataIndex: 'pointName', key: 'pointName', width: 200 },
  { title: '采样员', dataIndex: 'samplerId', key: 'samplerId', width: 200 },
  { title: '采样员姓名', dataIndex: 'samplerName', key: 'samplerName', width: 150 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSamplingPlanPage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchEntrustList = async () => {
  try {
    const res = await getEntrustSelectList()
    entrustList.value = res.data
  } catch (error) {
    console.error('Get entrust list error:', error)
  }
}

const fetchEquipmentList = async () => {
  try {
    const res = await getAvailableEquipments()
    equipmentList.value = res.data
  } catch (error) {
    console.error('Get equipment list error:', error)
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

const handlePlanDateChange = (dates: any[]) => {
  if (dates && dates.length === 2) {
    queryParams.planDateStart = dates[0]
    queryParams.planDateEnd = dates[1]
  } else {
    queryParams.planDateStart = ''
    queryParams.planDateEnd = ''
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    planNo: '',
    planName: '',
    samplingType: undefined,
    status: undefined,
    planDateStart: '',
    planDateEnd: ''
  })
  planDateRange.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleEntrustChange = (entrustId: number) => {
  const entrust = entrustList.value.find(item => item.id === entrustId)
  if (entrust) {
    formData.entrustNo = entrust.entrustNo
  }
}

const handleSamplerChange = (ids: number[]) => {
  const names = ids.map(id => {
    const user = userList.value.find(u => u.id === id)
    return user?.realName || ''
  }).filter(Boolean).join(',')
  formData.samplerIds = ids.join(',')
  formData.samplerNames = names
}

const handleEquipmentChange = (ids: number[]) => {
  const names = ids.map(id => {
    const eq = equipmentList.value.find(e => e.id === id)
    return eq?.equipmentName || ''
  }).filter(Boolean).join(',')
  formData.equipmentIds = ids.join(',')
  formData.equipmentNames = names
}

const handleAddPoint = () => {
  const newPoint: SamplingPointSaveDTO = {
    pointCode: `P${String(formData.points.length + 1).padStart(3, '0')}`,
    pointName: '',
    longitude: undefined,
    latitude: undefined,
    samplingDepth: '',
    samplingFrequency: '',
    samplingItems: '',
    remark: ''
  }
  formData.points.push(newPoint)
}

const handleRemovePoint = (index: number) => {
  formData.points.splice(index, 1)
}

const handleAdd = () => {
  isEdit.value = false
  formModalTitle.value = '新增采样计划'
  Object.assign(formData, {
    id: undefined,
    planName: '',
    planDate: '',
    samplingType: undefined,
    entrustId: undefined,
    entrustNo: '',
    samplerIds: '',
    samplerNames: '',
    equipmentIds: '',
    equipmentNames: '',
    containerNames: '',
    remark: '',
    points: []
  })
  selectedSamplerIds.value = []
  selectedEquipmentIds.value = []
  handleAddPoint()
  formModalVisible.value = true
}

const handleEdit = (record: SamplingPlanVO) => {
  isEdit.value = true
  formModalTitle.value = '编辑采样计划'
  getSamplingPlanById(record.id).then(res => {
    const detail = res.data
    Object.assign(formData, {
      id: detail.id,
      planName: detail.planName,
      planDate: detail.planDate,
      samplingType: detail.samplingType,
      entrustId: detail.entrustId,
      entrustNo: detail.entrustNo,
      samplerIds: detail.samplerIds,
      samplerNames: detail.samplerNames,
      equipmentIds: detail.equipmentIds,
      equipmentNames: detail.equipmentNames,
      containerNames: detail.containerNames,
      remark: detail.remark,
      points: detail.points || []
    })
    selectedSamplerIds.value = detail.samplerIds ? detail.samplerIds.split(',').map(Number) : []
    selectedEquipmentIds.value = detail.equipmentIds ? detail.equipmentIds.split(',').map(Number) : []
    formModalVisible.value = true
  })
}

const handleView = async (record: SamplingPlanVO) => {
  try {
    const res = await getSamplingPlanById(record.id)
    planDetail.value = res.data
    detailTab.value = 'basic'
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get plan detail error:', error)
  }
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    if (formData.points.length === 0) {
      message.warning('请至少添加一个点位')
      return
    }
    submitting.value = true
    if (isEdit.value) {
      await updateSamplingPlan(formData)
      message.success('更新成功')
    } else {
      await addSamplingPlan(formData)
      message.success('新增成功')
    }
    formModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteSamplingPlan(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const handleSubmit = async (record: SamplingPlanVO) => {
  try {
    await submitSamplingPlan(record.id)
    message.success('提交成功')
    fetchData()
  } catch (error) {
    console.error('Submit error:', error)
  }
}

const handleCancel = async (record: SamplingPlanVO) => {
  try {
    await cancelSamplingPlan(record.id)
    message.success('取消成功')
    fetchData()
  } catch (error) {
    console.error('Cancel error:', error)
  }
}

const handleAssign = async (record: SamplingPlanVO) => {
  currentPlan.value = record
  const res = await getSamplingPlanById(record.id)
  const detail = res.data
  assignTaskList.value = (detail.points || []).map((point, index) => ({
    pointId: point.id!,
    pointCode: point.pointCode,
    pointName: point.pointName,
    samplerId: 0,
    samplerName: '',
    index: index + 1
  } as any))
  assignModalVisible.value = true
}

const handleSamplerSelect = (val: number, record: any) => {
  const user = userList.value.find(u => u.id === val)
  if (user) {
    record.samplerName = user.realName
  }
}

const handleAssignSubmit = async () => {
  const invalid = assignTaskList.value.some(item => !item.samplerId)
  if (invalid) {
    message.warning('请为所有点位分配采样员')
    return
  }
  try {
    submitting.value = true
    const dto: TaskAssignDTO = {
      planId: currentPlan.value!.id,
      taskList: assignTaskList.value.map(item => ({
        pointId: item.pointId,
        samplerId: item.samplerId,
        samplerName: item.samplerName
      }))
    }
    await assignSamplingTask(dto)
    message.success('任务分配成功')
    assignModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Assign error:', error)
  } finally {
    submitting.value = false
  }
}

const getSamplingTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'blue', 2: 'cyan', 3: 'green', 4: 'orange', 5: 'purple', 6: 'default' }
  return colors[type || 0] || 'default'
}

const getStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'default',
    1: 'orange',
    2: 'blue',
    3: 'processing',
    4: 'green',
    5: 'red'
  }
  return colors[status || 0] || 'default'
}

const getTaskStatusColor = (status?: number) => {
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

onMounted(() => {
  fetchData()
  fetchEntrustList()
  fetchEquipmentList()
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

.toolbar-right {
  flex-shrink: 0;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
