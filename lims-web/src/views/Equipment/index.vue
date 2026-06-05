<template>
  <div class="equipment">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="设备编号">
            <a-input v-model:value="queryParams.equipmentNo" placeholder="请输入设备编号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="设备名称">
            <a-input v-model:value="queryParams.equipmentName" placeholder="请输入设备名称" style="width: 180px" allow-clear />
          </a-form-item>
          <a-form-item label="设备类型">
            <a-select v-model:value="queryParams.equipmentType" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="1">采样设备</a-select-option>
              <a-select-option :value="2">辅助设备</a-select-option>
              <a-select-option :value="3">容器</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="借用状态">
            <a-select v-model:value="queryParams.borrowStatus" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">未借用</a-select-option>
              <a-select-option :value="1">已借用</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.status" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">正常</a-select-option>
              <a-select-option :value="1">维修中</a-select-option>
              <a-select-option :value="2">已报废</a-select-option>
            </a-select>
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
            <PlusOutlined /> 新增设备
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
          <template v-if="column.key === 'equipmentType'">
            <a-tag :color="getEquipmentTypeColor(record.equipmentType)">
              {{ record.equipmentTypeName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'borrowStatus'">
            <a-tag :color="getBorrowStatusColor(record.borrowStatus)">
              {{ record.borrowStatusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
            <template v-if="record.status === 0">
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
            </template>
            <template v-if="record.status === 0 && record.borrowStatus === 0">
              <a-popconfirm title="确定删除该设备吗？" @confirm="handleDelete(record.id)">
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      :title="formModalTitle"
      width="800px"
      @ok="handleFormSubmit"
      @cancel="formModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="设备编号" name="equipmentNo">
              <a-input v-model:value="formData.equipmentNo" placeholder="请输入设备编号" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="设备名称" name="equipmentName">
              <a-input v-model:value="formData.equipmentName" placeholder="请输入设备名称" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="设备类型" name="equipmentType">
              <a-select v-model:value="formData.equipmentType" placeholder="请选择设备类型">
                <a-select-option :value="1">采样设备</a-select-option>
                <a-select-option :value="2">辅助设备</a-select-option>
                <a-select-option :value="3">容器</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="规格型号">
              <a-input v-model:value="formData.specification" placeholder="请输入规格型号" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="生产厂家">
              <a-input v-model:value="formData.manufacturer" placeholder="请输入生产厂家" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="购置日期" name="purchaseDate">
              <a-date-picker v-model:value="formData.purchaseDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="上次校准日期">
              <a-date-picker v-model:value="formData.lastCalibrationDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="下次校准日期">
              <a-date-picker v-model:value="formData.nextCalibrationDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="状态" name="status">
          <a-select v-model:value="formData.status" placeholder="请选择状态">
            <a-select-option :value="0">正常</a-select-option>
            <a-select-option :value="1">维修中</a-select-option>
            <a-select-option :value="2">已报废</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="设备详情"
      width="800px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-descriptions :column="2" bordered>
        <a-descriptions-item label="设备编号">{{ equipmentDetail?.equipmentNo }}</a-descriptions-item>
        <a-descriptions-item label="设备名称">{{ equipmentDetail?.equipmentName }}</a-descriptions-item>
        <a-descriptions-item label="设备类型">
          <a-tag :color="getEquipmentTypeColor(equipmentDetail?.equipmentType)">
            {{ equipmentDetail?.equipmentTypeName }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="规格型号">{{ equipmentDetail?.specification || '-' }}</a-descriptions-item>
        <a-descriptions-item label="生产厂家">{{ equipmentDetail?.manufacturer || '-' }}</a-descriptions-item>
        <a-descriptions-item label="购置日期">{{ equipmentDetail?.purchaseDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="上次校准日期">{{ equipmentDetail?.lastCalibrationDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="下次校准日期">{{ equipmentDetail?.nextCalibrationDate || '-' }}</a-descriptions-item>
        <a-descriptions-item label="借用状态">
          <a-tag :color="getBorrowStatusColor(equipmentDetail?.borrowStatus)">
            {{ equipmentDetail?.borrowStatusName }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getStatusColor(equipmentDetail?.status)">
            {{ equipmentDetail?.statusName }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ equipmentDetail?.createTime }}</a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">{{ equipmentDetail?.remark || '-' }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import type {
  EquipmentQuery,
  EquipmentSaveDTO,
  EquipmentVO
} from '@/types'
import {
  getEquipmentPage,
  getEquipmentById,
  addEquipment,
  updateEquipment,
  deleteEquipment
} from '@/api/sampling'

const loading = ref(false)
const submitting = ref(false)

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)

const formRef = ref()
const isEdit = ref(false)

const queryParams = reactive<EquipmentQuery>({
  pageNum: 1,
  pageSize: 10,
  equipmentNo: '',
  equipmentName: '',
  equipmentType: undefined,
  borrowStatus: undefined,
  status: undefined
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<EquipmentVO[]>([])
const equipmentDetail = ref<EquipmentVO | null>(null)

const formData = reactive<EquipmentSaveDTO>({
  equipmentName: ''
})

const formRules = {
  equipmentNo: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  equipmentType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '设备编号', dataIndex: 'equipmentNo', key: 'equipmentNo', width: 140 },
  { title: '设备名称', dataIndex: 'equipmentName', key: 'equipmentName', width: 180, ellipsis: true },
  { title: '设备类型', dataIndex: 'equipmentType', key: 'equipmentType', width: 100 },
  { title: '规格型号', dataIndex: 'specification', key: 'specification', width: 140, ellipsis: true },
  { title: '生产厂家', dataIndex: 'manufacturer', key: 'manufacturer', width: 160, ellipsis: true },
  { title: '购置日期', dataIndex: 'purchaseDate', key: 'purchaseDate', width: 120 },
  { title: '上次校准日期', dataIndex: 'lastCalibrationDate', key: 'lastCalibrationDate', width: 140 },
  { title: '下次校准日期', dataIndex: 'nextCalibrationDate', key: 'nextCalibrationDate', width: 140 },
  { title: '借用状态', dataIndex: 'borrowStatus', key: 'borrowStatus', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 220, fixed: 'right' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getEquipmentPage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    equipmentNo: '',
    equipmentName: '',
    equipmentType: undefined,
    borrowStatus: undefined,
    status: undefined
  })
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleAdd = () => {
  isEdit.value = false
  formModalTitle.value = '新增设备'
  Object.assign(formData, {
    id: undefined,
    equipmentNo: '',
    equipmentName: '',
    equipmentType: undefined,
    specification: '',
    model: '',
    manufacturer: '',
    purchaseDate: '',
    lastCalibrationDate: '',
    nextCalibrationDate: '',
    borrowStatus: 0,
    status: 0,
    remark: ''
  })
  formModalVisible.value = true
}

const handleEdit = (record: EquipmentVO) => {
  isEdit.value = true
  formModalTitle.value = '编辑设备'
  getEquipmentById(record.id).then(res => {
    const detail = res.data
    Object.assign(formData, {
      id: detail.id,
      equipmentNo: detail.equipmentNo,
      equipmentName: detail.equipmentName,
      equipmentType: detail.equipmentType,
      specification: detail.specification,
      model: detail.model,
      manufacturer: detail.manufacturer,
      purchaseDate: detail.purchaseDate,
      lastCalibrationDate: detail.lastCalibrationDate,
      nextCalibrationDate: detail.nextCalibrationDate,
      borrowStatus: detail.borrowStatus,
      status: detail.status,
      remark: detail.remark
    })
    formModalVisible.value = true
  })
}

const handleView = async (record: EquipmentVO) => {
  try {
    const res = await getEquipmentById(record.id)
    equipmentDetail.value = res.data
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get equipment detail error:', error)
  }
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await updateEquipment(formData)
      message.success('更新成功')
    } else {
      await addEquipment(formData)
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
    await deleteEquipment(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const getEquipmentTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'blue', 2: 'green', 3: 'orange' }
  return colors[type || 0] || 'default'
}

const getBorrowStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 0: 'green', 1: 'orange' }
  return colors[status || 0] || 'default'
}

const getStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 0: 'green', 1: 'orange', 2: 'red' }
  return colors[status || 0] || 'default'
}

onMounted(() => {
  fetchData()
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
