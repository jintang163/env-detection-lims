<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">设施台账</div>
        <div class="page-desc">管理实验室房间信息和设施设备的基础信息、状态</div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">设施总数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card valid" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">正常设施</div>
              <div class="stat-value">{{ stats.normal || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">维护中</div>
              <div class="stat-value">{{ stats.maintaining || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card expired" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">故障设施</div>
              <div class="stat-value">{{ stats.fault || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeMainTab" class="main-tabs">
        <el-tab-pane label="房间管理" name="room">
          <div class="toolbar">
            <el-input
              v-model="roomSearchKeyword"
              placeholder="搜索房间编号、名称..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchRoomList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="roomSearchType" placeholder="房间类型" clearable style="width: 180px">
              <el-option label="理化实验室" :value="1" />
              <el-option label="微生物实验室" :value="2" />
              <el-option label="仪器室" :value="3" />
              <el-option label="试剂室" :value="4" />
              <el-option label="样品室" :value="5" />
              <el-option label="气瓶室" :value="6" />
              <el-option label="办公室" :value="7" />
            </el-select>
            <el-button type="primary" @click="handleRoomAdd">
              <el-icon><Plus /></el-icon>
              新增房间
            </el-button>
            <el-button @click="fetchRoomList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="roomTableData"
            v-loading="roomLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="roomNo" label="房间编号" width="120" />
            <el-table-column prop="roomName" label="房间名称" width="160" />
            <el-table-column label="房间类型" width="140">
              <template #default="{ row }">
                <el-tag :type="getRoomTypeTag(row.roomType)" effect="light" size="small">
                  {{ getRoomTypeText(row.roomType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="floor" label="楼层" width="80" />
            <el-table-column prop="area" label="面积(㎡)" width="100" />
            <el-table-column prop="managerName" label="负责人" width="100" />
            <el-table-column prop="phone" label="联系电话" width="130" />
            <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleRoomView(row)">详情</el-button>
                <el-button type="primary" link @click="handleRoomEdit(row)">编辑</el-button>
                <el-button type="danger" link @click="handleRoomDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="roomPagination.pageNum"
            v-model:page-size="roomPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="roomPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchRoomList"
            @current-change="fetchRoomList"
          />
        </el-tab-pane>

        <el-tab-pane label="设施管理" name="facility">
          <div class="toolbar">
            <el-input
              v-model="facilitySearchKeyword"
              placeholder="搜索设施编号、名称..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchFacilityList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="facilitySearchType" placeholder="设施类型" clearable style="width: 180px">
              <el-option label="通风橱" :value="1" />
              <el-option label="超净台" :value="2" />
              <el-option label="生物安全柜" :value="3" />
              <el-option label="空调系统" :value="4" />
              <el-option label="纯水系统" :value="5" />
              <el-option label="供气系统" :value="6" />
              <el-option label="排风系统" :value="7" />
              <el-option label="其他" :value="8" />
            </el-select>
            <el-select v-model="facilitySearchStatus" placeholder="状态" clearable style="width: 140px">
              <el-option label="停用" :value="0" />
              <el-option label="正常" :value="1" />
              <el-option label="维护中" :value="2" />
              <el-option label="故障" :value="3" />
            </el-select>
            <el-button type="primary" @click="handleFacilityAdd">
              <el-icon><Plus /></el-icon>
              新增设施
            </el-button>
            <el-button @click="fetchFacilityList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="facilityTableData"
            v-loading="facilityLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="facilityNo" label="设施编号" width="140" />
            <el-table-column prop="facilityName" label="设施名称" width="180" />
            <el-table-column label="设施类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getFacilityTypeTag(row.facilityType)" effect="light" size="small">
                  {{ getFacilityTypeText(row.facilityType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="model" label="型号" width="120" />
            <el-table-column prop="roomName" label="所在房间" width="140" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="manufacturer" label="生产厂家" width="140" show-overflow-tooltip />
            <el-table-column prop="installDate" label="安装日期" width="120" />
            <el-table-column prop="managerName" label="负责人" width="100" />
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleFacilityView(row)">详情</el-button>
                <el-button type="primary" link @click="handleFacilityEdit(row)">编辑</el-button>
                <el-button :type="row.status === 1 ? 'warning' : 'success'" link @click="handleFacilityStatusToggle(row)">
                  {{ row.status === 1 ? '停用' : '启用' }}
                </el-button>
                <el-button type="danger" link @click="handleFacilityDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="facilityPagination.pageNum"
            v-model:page-size="facilityPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="facilityPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchFacilityList"
            @current-change="fetchFacilityList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="roomDialogVisible" :title="roomDialogTitle" width="700px" top="10vh">
      <el-form
        :model="roomForm"
        :rules="roomFormRules"
        ref="roomFormRef"
        label-width="100px"
        v-loading="roomDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="房间编号" prop="roomNo">
              <el-input v-model="roomForm.roomNo" placeholder="请输入房间编号" :disabled="isRoomView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间名称" prop="roomName">
              <el-input v-model="roomForm.roomName" placeholder="请输入房间名称" :disabled="isRoomView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="roomForm.roomType" placeholder="请选择房间类型" style="width: 100%" :disabled="isRoomView">
                <el-option label="理化实验室" :value="1" />
                <el-option label="微生物实验室" :value="2" />
                <el-option label="仪器室" :value="3" />
                <el-option label="试剂室" :value="4" />
                <el-option label="样品室" :value="5" />
                <el-option label="气瓶室" :value="6" />
                <el-option label="办公室" :value="7" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="roomForm.floor" :min="1" style="width: 100%" :disabled="isRoomView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number v-model="roomForm.area" :min="0" :precision="2" style="width: 100%" :disabled="isRoomView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="负责人" prop="managerName">
              <el-input v-model="roomForm.managerName" placeholder="请输入负责人" :disabled="isRoomView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="roomForm.phone" placeholder="请输入联系电话" :disabled="isRoomView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="房间描述" prop="description">
          <el-input
            v-model="roomForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入房间描述"
            :disabled="isRoomView"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="roomForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isRoomView"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">关闭</el-button>
        <el-button v-if="!isRoomView" type="primary" @click="handleRoomSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="facilityDialogVisible" :title="facilityDialogTitle" width="800px" top="10vh">
      <el-form
        :model="facilityForm"
        :rules="facilityFormRules"
        ref="facilityFormRef"
        label-width="100px"
        v-loading="facilityDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设施编号" prop="facilityNo">
              <el-input v-model="facilityForm.facilityNo" placeholder="请输入设施编号" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设施名称" prop="facilityName">
              <el-input v-model="facilityForm.facilityName" placeholder="请输入设施名称" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="设施类型" prop="facilityType">
              <el-select v-model="facilityForm.facilityType" placeholder="请选择设施类型" style="width: 100%" :disabled="isFacilityView">
                <el-option label="通风橱" :value="1" />
                <el-option label="超净台" :value="2" />
                <el-option label="生物安全柜" :value="3" />
                <el-option label="空调系统" :value="4" />
                <el-option label="纯水系统" :value="5" />
                <el-option label="供气系统" :value="6" />
                <el-option label="排风系统" :value="7" />
                <el-option label="其他" :value="8" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="型号" prop="model">
              <el-input v-model="facilityForm.model" placeholder="请输入型号" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="facilityForm.specification" placeholder="请输入规格" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所在房间" prop="roomId">
              <el-select v-model="facilityForm.roomId" placeholder="请选择房间" style="width: 100%" :disabled="isFacilityView" @change="handleFacilityRoomChange">
                <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomName" :value="room.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="facilityForm.status" placeholder="请选择状态" style="width: 100%" :disabled="isFacilityView">
                <el-option label="停用" :value="0" />
                <el-option label="正常" :value="1" />
                <el-option label="维护中" :value="2" />
                <el-option label="故障" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="生产厂家" prop="manufacturer">
              <el-input v-model="facilityForm.manufacturer" placeholder="请输入生产厂家" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出厂编号" prop="factoryNo">
              <el-input v-model="facilityForm.factoryNo" placeholder="请输入出厂编号" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="安装日期" prop="installDate">
              <el-date-picker
                v-model="facilityForm.installDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isFacilityView"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="负责人" prop="managerName">
              <el-input v-model="facilityForm.managerName" placeholder="请输入负责人" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="facilityForm.phone" placeholder="请输入联系电话" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="购置金额(元)" prop="purchaseAmount">
              <el-input-number v-model="facilityForm.purchaseAmount" :min="0" :precision="2" style="width: 100%" :disabled="isFacilityView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="技术参数" prop="technicalParams">
          <el-input
            v-model="facilityForm.technicalParams"
            type="textarea"
            :rows="2"
            placeholder="请输入技术参数"
            :disabled="isFacilityView"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="facilityForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isFacilityView"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="facilityDialogVisible = false">关闭</el-button>
        <el-button v-if="!isFacilityView" type="primary" @click="handleFacilitySubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Box,
  CircleCheck,
  Tools,
  Warning,
  Search,
  Plus,
  Refresh,
  Check
} from '@element-plus/icons-vue'
import { facilityApi, roomApi, facilityMaintenanceApi } from '@/api/environment'

const activeMainTab = ref('room')
const roomLoading = ref(false)
const facilityLoading = ref(false)
const roomDetailLoading = ref(false)
const facilityDetailLoading = ref(false)

const stats = reactive({
  total: 0,
  normal: 0,
  maintaining: 0,
  fault: 0,
  pendingMaintenance: 0
})

const roomOptions = ref([])

const roomSearchKeyword = ref('')
const roomSearchType = ref('')
const roomTableData = ref([])
const roomPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const facilitySearchKeyword = ref('')
const facilitySearchType = ref('')
const facilitySearchStatus = ref('')
const facilityTableData = ref([])
const facilityPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const roomDialogVisible = ref(false)
const isRoomView = ref(false)
const roomDialogTitle = ref('')
const roomFormRef = ref(null)
const roomForm = ref({
  id: null,
  roomNo: '',
  roomName: '',
  roomType: 1,
  floor: 1,
  area: null,
  managerName: '',
  phone: '',
  description: '',
  remark: ''
})

const roomFormRules = {
  roomNo: [{ required: true, message: '请输入房间编号', trigger: 'blur' }],
  roomName: [{ required: true, message: '请输入房间名称', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房间类型', trigger: 'change' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }]
}

const facilityDialogVisible = ref(false)
const isFacilityView = ref(false)
const facilityDialogTitle = ref('')
const facilityFormRef = ref(null)
const facilityForm = ref({
  id: null,
  facilityNo: '',
  facilityName: '',
  facilityType: 1,
  model: '',
  specification: '',
  roomId: null,
  roomName: '',
  status: 1,
  manufacturer: '',
  factoryNo: '',
  installDate: '',
  managerName: '',
  phone: '',
  purchaseAmount: null,
  technicalParams: '',
  remark: ''
})

const facilityFormRules = {
  facilityNo: [{ required: true, message: '请输入设施编号', trigger: 'blur' }],
  facilityName: [{ required: true, message: '请输入设施名称', trigger: 'blur' }],
  facilityType: [{ required: true, message: '请选择设施类型', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const getRoomTypeTag = (type) => {
  const tags = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger', 5: 'info', 6: 'primary', 7: 'success' }
  return tags[type] || 'info'
}

const getRoomTypeText = (type) => {
  const texts = { 1: '理化实验室', 2: '微生物实验室', 3: '仪器室', 4: '试剂室', 5: '样品室', 6: '气瓶室', 7: '办公室' }
  return texts[type] || '未知'
}

const getFacilityTypeTag = (type) => {
  const tags = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'danger', 5: 'info', 6: 'primary', 7: 'success', 8: 'info' }
  return tags[type] || 'info'
}

const getFacilityTypeText = (type) => {
  const texts = { 1: '通风橱', 2: '超净台', 3: '生物安全柜', 4: '空调系统', 5: '纯水系统', 6: '供气系统', 7: '排风系统', 8: '其他' }
  return texts[type] || '未知'
}

const getStatusTag = (status) => {
  const tags = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
  return tags[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '停用', 1: '正常', 2: '维护中', 3: '故障' }
  return texts[status] || '未知'
}

const fetchStats = async () => {
  try {
    const res = await facilityApi.stats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchRooms = async () => {
  try {
    const res = await roomApi.list()
    if (res.code === 200) {
      roomOptions.value = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchRoomList = async () => {
  roomLoading.value = true
  try {
    const params = {
      pageNum: roomPagination.pageNum,
      pageSize: roomPagination.pageSize,
      roomNo: roomSearchKeyword.value,
      roomName: roomSearchKeyword.value,
      roomType: roomSearchType.value
    }
    const res = await roomApi.page(params)
    if (res.code === 200) {
      roomTableData.value = res.data.list
      roomPagination.total = res.data.total
    }
  } finally {
    roomLoading.value = false
  }
}

const fetchFacilityList = async () => {
  facilityLoading.value = true
  try {
    const params = {
      pageNum: facilityPagination.pageNum,
      pageSize: facilityPagination.pageSize,
      facilityNo: facilitySearchKeyword.value,
      facilityName: facilitySearchKeyword.value,
      facilityType: facilitySearchType.value,
      status: facilitySearchStatus.value
    }
    const res = await facilityApi.page(params)
    if (res.code === 200) {
      facilityTableData.value = res.data.list
      facilityPagination.total = res.data.total
    }
  } finally {
    facilityLoading.value = false
  }
}

const handleFacilityRoomChange = (roomId) => {
  const room = roomOptions.value.find(r => r.id === roomId)
  if (room) {
    facilityForm.value.roomName = room.roomName
  }
}

const handleRoomAdd = () => {
  isRoomView.value = false
  roomDialogTitle.value = '新增房间'
  roomForm.value = {
    id: null,
    roomNo: '',
    roomName: '',
    roomType: 1,
    floor: 1,
    area: null,
    managerName: '',
    phone: '',
    description: '',
    remark: ''
  }
  roomDialogVisible.value = true
}

const handleRoomEdit = (row) => {
  isRoomView.value = false
  roomDialogTitle.value = '编辑房间'
  roomForm.value = { ...row }
  roomDialogVisible.value = true
}

const handleRoomView = (row) => {
  isRoomView.value = true
  roomDialogTitle.value = '房间详情'
  roomDetailLoading.value = true
  roomApi.get(row.id).then(res => {
    if (res.code === 200) {
      roomForm.value = { ...res.data }
    }
    roomDetailLoading.value = false
  }).catch(() => {
    roomDetailLoading.value = false
  })
  roomDialogVisible.value = true
}

const handleRoomDelete = (row) => {
  ElMessageBox.confirm(`确定要删除房间"${row.roomName}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      const res = await roomApi.delete(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchRoomList()
        fetchRooms()
      }
    }).catch(() => {})
}

const handleRoomSubmit = async () => {
  if (!roomFormRef.value) return
  try {
    await roomFormRef.value.validate()
    let res
    if (roomForm.value.id) {
      res = await roomApi.update(roomForm.value)
    } else {
      res = await roomApi.save(roomForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(roomForm.value.id ? '更新成功' : '新增成功')
      roomDialogVisible.value = false
      fetchRoomList()
      fetchRooms()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleFacilityAdd = () => {
  isFacilityView.value = false
  facilityDialogTitle.value = '新增设施'
  facilityForm.value = {
    id: null,
    facilityNo: '',
    facilityName: '',
    facilityType: 1,
    model: '',
    specification: '',
    roomId: null,
    roomName: '',
    status: 1,
    manufacturer: '',
    factoryNo: '',
    installDate: '',
    managerName: '',
    phone: '',
    purchaseAmount: null,
    technicalParams: '',
    remark: ''
  }
  facilityDialogVisible.value = true
}

const handleFacilityEdit = (row) => {
  isFacilityView.value = false
  facilityDialogTitle.value = '编辑设施'
  facilityForm.value = { ...row }
  facilityDialogVisible.value = true
}

const handleFacilityView = (row) => {
  isFacilityView.value = true
  facilityDialogTitle.value = '设施详情'
  facilityDetailLoading.value = true
  facilityApi.get(row.id).then(res => {
    if (res.code === 200) {
      facilityForm.value = { ...res.data }
    }
    facilityDetailLoading.value = false
  }).catch(() => {
    facilityDetailLoading.value = false
  })
  facilityDialogVisible.value = true
}

const handleFacilityStatusToggle = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = row.status === 1 ? '停用' : '启用'
  ElMessageBox.confirm(`确定要${action}设施"${row.facilityName}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      const res = await facilityApi.updateStatus(row.id, newStatus)
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        fetchFacilityList()
        fetchStats()
      }
    }).catch(() => {})
}

const handleFacilityDelete = (row) => {
  ElMessageBox.confirm(`确定要删除设施"${row.facilityName}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      const res = await facilityApi.delete(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchFacilityList()
        fetchStats()
      }
    }).catch(() => {})
}

const handleFacilitySubmit = async () => {
  if (!facilityFormRef.value) return
  try {
    await facilityFormRef.value.validate()
    let res
    if (facilityForm.value.id) {
      res = await facilityApi.update(facilityForm.value)
    } else {
      res = await facilityApi.save(facilityForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(facilityForm.value.id ? '更新成功' : '新增成功')
      facilityDialogVisible.value = false
      fetchFacilityList()
      fetchStats()
    }
  } catch (e) {
    console.error(e)
  }
}

watch(activeMainTab, (val) => {
  if (val === 'room') {
    fetchRoomList()
  } else if (val === 'facility') {
    fetchFacilityList()
  }
})

onMounted(() => {
  fetchStats()
  fetchRooms()
  fetchRoomList()
})
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.page-header {
  margin-bottom: 16px;
}

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

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.total .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-card.valid .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-card.expired .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.main-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}
</style>
