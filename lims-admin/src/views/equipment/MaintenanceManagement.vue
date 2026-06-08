<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">设备维护/维修管理</div>
        <div class="page-desc">管理设备的日常维护记录和维修申请流程</div>
      </div>
    </div>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="维护记录" name="record">
          <div class="toolbar">
            <el-input
              v-model="recordSearchKeyword"
              placeholder="搜索设备编号、名称..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchRecordList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="recordSearchType" placeholder="维护类型" clearable style="width: 140px">
              <el-option label="日常维护" :value="1" />
              <el-option label="定期保养" :value="2" />
              <el-option label="故障排查" :value="3" />
              <el-option label="清洁消毒" :value="4" />
            </el-select>
            <el-select v-model="recordSearchResult" placeholder="维护结果" clearable style="width: 140px">
              <el-option label="良好" :value="1" />
              <el-option label="一般" :value="2" />
              <el-option label="需维修" :value="3" />
            </el-select>
            <el-button type="primary" @click="handleAddRecord">
              <el-icon><Plus /></el-icon>
              新增维护记录
            </el-button>
            <el-button @click="fetchRecordList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="recordTableData"
            v-loading="recordLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="equipmentNo" label="设备编号" width="140" />
            <el-table-column prop="equipmentName" label="设备名称" width="180" />
            <el-table-column label="维护类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getMaintenanceTypeTag(row.maintenanceType)" effect="light" size="small">
                  {{ row.maintenanceTypeName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="maintenanceDate" label="维护日期" width="120" />
            <el-table-column prop="maintenanceContent" label="维护内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="maintainerName" label="维护人" width="100" />
            <el-table-column label="结果" width="100">
              <template #default="{ row }">
                <el-tag :type="getResultTag(row.result)" effect="light" size="small">
                  {{ row.resultName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="nextMaintenanceDate" label="下次维护日期" width="140" />
            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditRecord(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteRecord(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="recordPagination.pageNum"
            v-model:page-size="recordPagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="recordPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchRecordList"
            @current-change="fetchRecordList"
          />
        </el-tab-pane>

        <el-tab-pane label="维修申请" name="repair">
          <div class="toolbar">
            <el-input
              v-model="repairSearchKeyword"
              placeholder="搜索设备编号、名称、申请人..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchRepairList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="repairSearchUrgency" placeholder="紧急程度" clearable style="width: 140px">
              <el-option label="低" :value="1" />
              <el-option label="中" :value="2" />
              <el-option label="高" :value="3" />
              <el-option label="紧急" :value="4" />
            </el-select>
            <el-select v-model="repairSearchStatus" placeholder="申请状态" clearable style="width: 140px">
              <el-option label="待受理" :value="0" />
              <el-option label="已受理" :value="1" />
              <el-option label="维修中" :value="2" />
              <el-option label="待确认" :value="3" />
              <el-option label="已完成" :value="4" />
              <el-option label="已驳回" :value="5" />
            </el-select>
            <el-button type="primary" @click="handleSubmitRepair">
              <el-icon><Plus /></el-icon>
              提交维修申请
            </el-button>
            <el-button @click="fetchRepairList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="repairTableData"
            v-loading="repairLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="equipmentNo" label="设备编号" width="140" />
            <el-table-column prop="equipmentName" label="设备名称" width="180" />
            <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
            <el-table-column label="紧急程度" width="100">
              <template #default="{ row }">
                <el-tag :type="getUrgencyTag(row.urgency)" effect="light" size="small">
                  {{ row.urgencyName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getRepairStatusTag(row.status)" effect="light" size="small">
                  {{ row.statusName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applicantName" label="申请人" width="100" />
            <el-table-column prop="applyTime" label="申请时间" width="160" />
            <el-table-column prop="repairResult" label="维修结果" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 0"
                  type="success"
                  link
                  @click="handleAcceptRepair(row)"
                >受理</el-button>
                <el-button
                  v-if="row.status === 1"
                  type="primary"
                  link
                  @click="handleProcessRepair(row)"
                >处理</el-button>
                <el-button
                  v-if="row.status === 2"
                  type="warning"
                  link
                  @click="handleConfirmRepair(row)"
                >确认结果</el-button>
                <el-button
                  v-if="row.status === 0"
                  type="danger"
                  link
                  @click="handleRejectRepair(row)"
                >驳回</el-button>
                <el-button
                  v-if="row.status === 0"
                  type="danger"
                  link
                  @click="handleDeleteRepair(row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="repairPagination.pageNum"
            v-model:page-size="repairPagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="repairPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchRepairList"
            @current-change="fetchRepairList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="recordDialogVisible" :title="recordDialogTitle" width="700px" top="5vh">
      <el-form
        :model="recordForm"
        :rules="recordFormRules"
        ref="recordFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备" prop="equipmentId">
              <el-select
                v-model="recordForm.equipmentId"
                placeholder="请选择设备"
                style="width: 100%"
                @change="handleEquipmentChange"
              >
                <el-option
                  v-for="item in equipmentList"
                  :key="item.id"
                  :label="item.equipmentName + '(' + item.equipmentNo + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护类型" prop="maintenanceType">
              <el-select v-model="recordForm.maintenanceType" placeholder="请选择" style="width: 100%">
                <el-option label="日常维护" :value="1" />
                <el-option label="定期保养" :value="2" />
                <el-option label="故障排查" :value="3" />
                <el-option label="清洁消毒" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="维护日期" prop="maintenanceDate">
              <el-date-picker
                v-model="recordForm.maintenanceDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维护人" prop="maintainerName">
              <el-input v-model="recordForm.maintainerName" placeholder="请输入维护人" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维护结果" prop="result">
              <el-select v-model="recordForm.result" placeholder="请选择" style="width: 100%">
                <el-option label="良好" :value="1" />
                <el-option label="一般" :value="2" />
                <el-option label="需维修" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="下次维护日期" prop="nextMaintenanceDate">
              <el-date-picker
                v-model="recordForm.nextMaintenanceDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护时长(分钟)" prop="duration">
              <el-input-number v-model="recordForm.duration" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="维护内容" prop="maintenanceContent">
          <el-input
            v-model="recordForm.maintenanceContent"
            type="textarea"
            :rows="2"
            placeholder="请输入维护内容"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="recordForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="recordDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSubmitRecord">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="repairSubmitDialogVisible" title="提交维修申请" width="700px" top="5vh">
      <el-form
        :model="repairSubmitForm"
        :rules="repairSubmitFormRules"
        ref="repairSubmitFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备" prop="equipmentId">
              <el-select
                v-model="repairSubmitForm.equipmentId"
                placeholder="请选择设备"
                style="width: 100%"
                @change="handleRepairEquipmentChange"
              >
                <el-option
                  v-for="item in equipmentList"
                  :key="item.id"
                  :label="item.equipmentName + '(' + item.equipmentNo + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急程度" prop="urgency">
              <el-select v-model="repairSubmitForm.urgency" placeholder="请选择" style="width: 100%">
                <el-option label="低" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="高" :value="3" />
                <el-option label="紧急" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicantName">
              <el-input v-model="repairSubmitForm.applicantName" placeholder="请输入申请人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="repairSubmitForm.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="故障描述" prop="faultDescription">
          <el-input
            v-model="repairSubmitForm.faultDescription"
            type="textarea"
            :rows="3"
            placeholder="请详细描述故障现象"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="repairSubmitForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="repairSubmitDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSubmitRepairForm">
          <el-icon><Check /></el-icon>
          提交
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="repairAcceptDialogVisible" title="受理维修申请" width="600px" top="5vh">
      <el-form
        :model="repairAcceptForm"
        :rules="repairAcceptFormRules"
        ref="repairAcceptFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备编号">
              <el-input v-model="repairAcceptForm.equipmentNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称">
              <el-input v-model="repairAcceptForm.equipmentName" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="处理人" prop="handlerName">
              <el-input v-model="repairAcceptForm.handlerName" placeholder="请输入处理人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计维修时间" prop="expectedRepairTime">
              <el-date-picker
                v-model="repairAcceptForm.expectedRepairTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择日期时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="受理备注" prop="acceptRemark">
          <el-input
            v-model="repairAcceptForm.acceptRemark"
            type="textarea"
            :rows="2"
            placeholder="请输入受理备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="repairAcceptDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleAcceptRepairForm">
          <el-icon><Check /></el-icon>
          确认受理
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="repairProcessDialogVisible" title="处理维修" width="700px" top="5vh">
      <el-form
        :model="repairProcessForm"
        :rules="repairProcessFormRules"
        ref="repairProcessFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备编号">
              <el-input v-model="repairProcessForm.equipmentNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称">
              <el-input v-model="repairProcessForm.equipmentName" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="维修结果" prop="repairResult">
              <el-select v-model="repairProcessForm.repairResult" placeholder="请选择" style="width: 100%">
                <el-option label="已修复" :value="1" />
                <el-option label="部分修复" :value="2" />
                <el-option label="无法修复" :value="3" />
                <el-option label="需更换部件" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维修费用(元)" prop="repairCost">
              <el-input-number v-model="repairProcessForm.repairCost" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="维修时长(小时)" prop="repairDuration">
              <el-input-number v-model="repairProcessForm.repairDuration" :min="0" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="维修内容" prop="repairContent">
          <el-input
            v-model="repairProcessForm.repairContent"
            type="textarea"
            :rows="3"
            placeholder="请详细描述维修过程"
          />
        </el-form-item>

        <el-form-item label="更换部件" prop="replacedParts">
          <el-input
            v-model="repairProcessForm.replacedParts"
            type="textarea"
            :rows="2"
            placeholder="请输入更换的部件名称和数量，多个用逗号分隔"
          />
        </el-form-item>

        <el-form-item label="维修备注" prop="repairRemark">
          <el-input
            v-model="repairProcessForm.repairRemark"
            type="textarea"
            :rows="2"
            placeholder="请输入维修备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="repairProcessDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleProcessRepairForm">
          <el-icon><Check /></el-icon>
          完成处理
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="repairConfirmDialogVisible" title="确认维修结果" width="600px" top="5vh">
      <el-form
        :model="repairConfirmForm"
        :rules="repairConfirmFormRules"
        ref="repairConfirmFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备编号">
              <el-input v-model="repairConfirmForm.equipmentNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称">
              <el-input v-model="repairConfirmForm.equipmentName" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="维修内容">
          <el-input v-model="repairConfirmForm.repairContent" type="textarea" :rows="2" disabled />
        </el-form-item>

        <el-form-item label="维修结果">
          <el-tag :type="getRepairResultTag(repairConfirmForm.repairResult)" effect="light">
            {{ getRepairResultName(repairConfirmForm.repairResult) }}
          </el-tag>
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="是否通过" prop="confirmed">
              <el-radio-group v-model="repairConfirmForm.confirmed">
                <el-radio :value="1">通过</el-radio>
                <el-radio :value="0">不通过</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认人" prop="confirmerName">
              <el-input v-model="repairConfirmForm.confirmerName" placeholder="请输入确认人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="确认意见" prop="confirmOpinion">
          <el-input
            v-model="repairConfirmForm.confirmOpinion"
            type="textarea"
            :rows="3"
            placeholder="请输入确认意见"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="repairConfirmDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleConfirmRepairForm">
          <el-icon><Check /></el-icon>
          确认
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="repairRejectDialogVisible" title="驳回维修申请" width="500px" top="5vh">
      <el-form
        :model="repairRejectForm"
        :rules="repairRejectFormRules"
        ref="repairRejectFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备编号">
              <el-input v-model="repairRejectForm.equipmentNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称">
              <el-input v-model="repairRejectForm.equipmentName" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="驳回原因" prop="rejectReason">
          <el-input
            v-model="repairRejectForm.rejectReason"
            type="textarea"
            :rows="3"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="repairRejectDialogVisible = false">关闭</el-button>
        <el-button type="danger" @click="handleRejectRepairForm">
          <el-icon><Close /></el-icon>
          确认驳回
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh, Check, Close } from '@element-plus/icons-vue'
import { maintenanceApi, equipmentApi } from '@/api/equipment'

const activeTab = ref('record')

const recordLoading = ref(false)
const recordSearchKeyword = ref('')
const recordSearchType = ref('')
const recordSearchResult = ref('')
const recordTableData = ref([])
const recordPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const repairLoading = ref(false)
const repairSearchKeyword = ref('')
const repairSearchUrgency = ref('')
const repairSearchStatus = ref('')
const repairTableData = ref([])
const repairPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const equipmentList = ref([])

const recordDialogVisible = ref(false)
const recordDialogTitle = ref('')
const recordFormRef = ref(null)
const recordForm = ref({
  id: null,
  equipmentId: null,
  equipmentNo: '',
  equipmentName: '',
  maintenanceType: 1,
  maintenanceDate: '',
  maintenanceContent: '',
  maintainerId: null,
  maintainerName: '',
  result: 1,
  nextMaintenanceDate: '',
  duration: null,
  remark: ''
})

const repairSubmitDialogVisible = ref(false)
const repairSubmitFormRef = ref(null)
const repairSubmitForm = ref({
  id: null,
  equipmentId: null,
  equipmentNo: '',
  equipmentName: '',
  faultDescription: '',
  urgency: 2,
  applicantId: null,
  applicantName: '',
  contactPhone: '',
  remark: ''
})

const repairAcceptDialogVisible = ref(false)
const repairAcceptFormRef = ref(null)
const repairAcceptForm = ref({
  id: null,
  equipmentNo: '',
  equipmentName: '',
  handlerId: null,
  handlerName: '',
  expectedRepairTime: '',
  acceptRemark: ''
})

const repairProcessDialogVisible = ref(false)
const repairProcessFormRef = ref(null)
const repairProcessForm = ref({
  id: null,
  equipmentNo: '',
  equipmentName: '',
  repairContent: '',
  replacedParts: '',
  repairCost: null,
  repairDuration: null,
  repairResult: 1,
  repairRemark: ''
})

const repairConfirmDialogVisible = ref(false)
const repairConfirmFormRef = ref(null)
const repairConfirmForm = ref({
  id: null,
  equipmentNo: '',
  equipmentName: '',
  repairContent: '',
  repairResult: null,
  confirmed: 1,
  confirmerId: null,
  confirmerName: '',
  confirmOpinion: ''
})

const repairRejectDialogVisible = ref(false)
const repairRejectFormRef = ref(null)
const repairRejectForm = ref({
  id: null,
  equipmentNo: '',
  equipmentName: '',
  rejectReason: ''
})

const recordFormRules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  maintenanceType: [{ required: true, message: '请选择维护类型', trigger: 'change' }],
  maintenanceDate: [{ required: true, message: '请选择维护日期', trigger: 'change' }],
  maintenanceContent: [{ required: true, message: '请输入维护内容', trigger: 'blur' }],
  maintainerName: [{ required: true, message: '请输入维护人', trigger: 'blur' }],
  result: [{ required: true, message: '请选择维护结果', trigger: 'change' }]
}

const repairSubmitFormRules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  faultDescription: [{ required: true, message: '请输入故障描述', trigger: 'blur' }],
  urgency: [{ required: true, message: '请选择紧急程度', trigger: 'change' }],
  applicantName: [{ required: true, message: '请输入申请人', trigger: 'blur' }]
}

const repairAcceptFormRules = {
  handlerName: [{ required: true, message: '请输入处理人', trigger: 'blur' }],
  expectedRepairTime: [{ required: true, message: '请选择预计维修时间', trigger: 'change' }]
}

const repairProcessFormRules = {
  repairContent: [{ required: true, message: '请输入维修内容', trigger: 'blur' }],
  repairResult: [{ required: true, message: '请选择维修结果', trigger: 'change' }]
}

const repairConfirmFormRules = {
  confirmed: [{ required: true, message: '请选择是否通过', trigger: 'change' }],
  confirmerName: [{ required: true, message: '请输入确认人', trigger: 'blur' }],
  confirmOpinion: [{ required: true, message: '请输入确认意见', trigger: 'blur' }]
}

const repairRejectFormRules = {
  rejectReason: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
}

const getMaintenanceTypeTag = (type) => {
  const tags = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info' }
  return tags[type] || 'info'
}

const getResultTag = (result) => {
  const tags = { 1: 'success', 2: 'warning', 3: 'danger' }
  return tags[result] || 'info'
}

const getUrgencyTag = (urgency) => {
  const tags = { 1: 'info', 2: 'primary', 3: 'warning', 4: 'danger' }
  return tags[urgency] || 'info'
}

const getRepairStatusTag = (status) => {
  const tags = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'warning', 4: 'success', 5: 'danger' }
  return tags[status] || 'info'
}

const getRepairResultTag = (result) => {
  const tags = { 1: 'success', 2: 'warning', 3: 'danger', 4: 'primary' }
  return tags[result] || 'info'
}

const getRepairResultName = (result) => {
  const names = { 1: '已修复', 2: '部分修复', 3: '无法修复', 4: '需更换部件' }
  return names[result] || '未知'
}

const fetchEquipmentList = async () => {
  const res = await equipmentApi.available()
  if (res.code === 200) {
    equipmentList.value = res.data
  }
}

const fetchRecordList = async () => {
  recordLoading.value = true
  try {
    const params = {
      pageNum: recordPagination.pageNum,
      pageSize: recordPagination.pageSize,
      equipmentName: recordSearchKeyword.value,
      equipmentNo: recordSearchKeyword.value,
      maintenanceType: recordSearchType.value,
      result: recordSearchResult.value
    }
    const res = await maintenanceApi.recordPage(params)
    if (res.code === 200) {
      recordTableData.value = res.data.list
      recordPagination.total = res.data.total
    }
  } finally {
    recordLoading.value = false
  }
}

const fetchRepairList = async () => {
  repairLoading.value = true
  try {
    const params = {
      pageNum: repairPagination.pageNum,
      pageSize: repairPagination.pageSize,
      equipmentName: repairSearchKeyword.value,
      equipmentNo: repairSearchKeyword.value,
      applicantName: repairSearchKeyword.value,
      urgency: repairSearchUrgency.value,
      status: repairSearchStatus.value
    }
    const res = await maintenanceApi.repairPage(params)
    if (res.code === 200) {
      repairTableData.value = res.data.list
      repairPagination.total = res.data.total
    }
  } finally {
    repairLoading.value = false
  }
}

const handleEquipmentChange = (equipmentId) => {
  const equipment = equipmentList.value.find(e => e.id === equipmentId)
  if (equipment) {
    recordForm.value.equipmentNo = equipment.equipmentNo
    recordForm.value.equipmentName = equipment.equipmentName
  }
}

const handleRepairEquipmentChange = (equipmentId) => {
  const equipment = equipmentList.value.find(e => e.id === equipmentId)
  if (equipment) {
    repairSubmitForm.value.equipmentNo = equipment.equipmentNo
    repairSubmitForm.value.equipmentName = equipment.equipmentName
  }
}

const handleAddRecord = () => {
  recordDialogTitle.value = '新增维护记录'
  recordForm.value = {
    id: null,
    equipmentId: null,
    equipmentNo: '',
    equipmentName: '',
    maintenanceType: 1,
    maintenanceDate: '',
    maintenanceContent: '',
    maintainerId: null,
    maintainerName: '',
    result: 1,
    nextMaintenanceDate: '',
    duration: null,
    remark: ''
  }
  recordDialogVisible.value = true
}

const handleEditRecord = (row) => {
  recordDialogTitle.value = '编辑维护记录'
  recordForm.value = { ...row }
  recordDialogVisible.value = true
}

const handleDeleteRecord = (row) => {
  ElMessageBox.confirm('确定要删除该维护记录吗？', '提示', { type: 'warning' }).then(async () => {
    const res = await maintenanceApi.recordDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchRecordList()
    }
  }).catch(() => {})
}

const handleSubmitRecord = async () => {
  if (!recordFormRef.value) return
  try {
    await recordFormRef.value.validate()
    let res
    if (recordForm.value.id) {
      res = await maintenanceApi.recordUpdate(recordForm.value)
    } else {
      res = await maintenanceApi.recordSave(recordForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(recordForm.value.id ? '更新成功' : '新增成功')
      recordDialogVisible.value = false
      fetchRecordList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleSubmitRepair = () => {
  repairSubmitForm.value = {
    id: null,
    equipmentId: null,
    equipmentNo: '',
    equipmentName: '',
    faultDescription: '',
    urgency: 2,
    applicantId: null,
    applicantName: '',
    contactPhone: '',
    remark: ''
  }
  repairSubmitDialogVisible.value = true
}

const handleSubmitRepairForm = async () => {
  if (!repairSubmitFormRef.value) return
  try {
    await repairSubmitFormRef.value.validate()
    const res = await maintenanceApi.repairSubmit(repairSubmitForm.value)
    if (res.code === 200) {
      ElMessage.success('提交成功')
      repairSubmitDialogVisible.value = false
      fetchRepairList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleAcceptRepair = (row) => {
  repairAcceptForm.value = {
    id: row.id,
    equipmentNo: row.equipmentNo,
    equipmentName: row.equipmentName,
    handlerId: null,
    handlerName: '',
    expectedRepairTime: '',
    acceptRemark: ''
  }
  repairAcceptDialogVisible.value = true
}

const handleAcceptRepairForm = async () => {
  if (!repairAcceptFormRef.value) return
  try {
    await repairAcceptFormRef.value.validate()
    const data = {
      id: repairAcceptForm.value.id,
      status: 1,
      ...repairAcceptForm.value
    }
    const res = await maintenanceApi.repairHandle(data)
    if (res.code === 200) {
      ElMessage.success('受理成功')
      repairAcceptDialogVisible.value = false
      fetchRepairList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleProcessRepair = (row) => {
  repairProcessForm.value = {
    id: row.id,
    equipmentNo: row.equipmentNo,
    equipmentName: row.equipmentName,
    repairContent: '',
    replacedParts: '',
    repairCost: null,
    repairDuration: null,
    repairResult: 1,
    repairRemark: ''
  }
  repairProcessDialogVisible.value = true
}

const handleProcessRepairForm = async () => {
  if (!repairProcessFormRef.value) return
  try {
    await repairProcessFormRef.value.validate()
    const data = {
      id: repairProcessForm.value.id,
      status: 2,
      ...repairProcessForm.value
    }
    const res = await maintenanceApi.repairHandle(data)
    if (res.code === 200) {
      ElMessage.success('处理完成')
      repairProcessDialogVisible.value = false
      fetchRepairList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleConfirmRepair = (row) => {
  repairConfirmForm.value = {
    id: row.id,
    equipmentNo: row.equipmentNo,
    equipmentName: row.equipmentName,
    repairContent: row.repairContent || '',
    repairResult: row.repairResult,
    confirmed: 1,
    confirmerId: null,
    confirmerName: '',
    confirmOpinion: ''
  }
  repairConfirmDialogVisible.value = true
}

const handleConfirmRepairForm = async () => {
  if (!repairConfirmFormRef.value) return
  try {
    await repairConfirmFormRef.value.validate()
    const data = {
      id: repairConfirmForm.value.id,
      ...repairConfirmForm.value
    }
    const res = await maintenanceApi.repairConfirm(data)
    if (res.code === 200) {
      ElMessage.success('确认成功')
      repairConfirmDialogVisible.value = false
      fetchRepairList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleRejectRepair = (row) => {
  repairRejectForm.value = {
    id: row.id,
    equipmentNo: row.equipmentNo,
    equipmentName: row.equipmentName,
    rejectReason: ''
  }
  repairRejectDialogVisible.value = true
}

const handleRejectRepairForm = async () => {
  if (!repairRejectFormRef.value) return
  try {
    await repairRejectFormRef.value.validate()
    const res = await maintenanceApi.repairReject(repairRejectForm.value.id, repairRejectForm.value.rejectReason)
    if (res.code === 200) {
      ElMessage.success('驳回成功')
      repairRejectDialogVisible.value = false
      fetchRepairList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleDeleteRepair = (row) => {
  ElMessageBox.confirm('确定要删除该维修申请吗？', '提示', { type: 'warning' }).then(async () => {
    const res = await maintenanceApi.repairDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchRepairList()
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchEquipmentList()
  fetchRecordList()
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

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}
</style>
