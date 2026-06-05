<template>
  <div class="customer">
    <a-card>
      <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
        <a-tab-pane key="all" tab="全部客户">
          <div class="toolbar">
            <a-form layout="inline" :model="queryParams">
              <a-form-item label="客户名称">
                <a-input v-model:value="queryParams.customerName" placeholder="请输入客户名称" style="width: 180px" allow-clear />
              </a-form-item>
              <a-form-item label="联系人">
                <a-input v-model:value="queryParams.contactPerson" placeholder="请输入联系人" style="width: 120px" allow-clear />
              </a-form-item>
              <a-form-item label="电话">
                <a-input v-model:value="queryParams.contactPhone" placeholder="请输入电话" style="width: 120px" allow-clear />
              </a-form-item>
              <a-form-item label="客户类型">
                <a-select v-model:value="queryParams.customerType" placeholder="请选择" style="width: 120px" allow-clear>
                  <a-select-option :value="1">企业客户</a-select-option>
                  <a-select-option :value="2">政府机构</a-select-option>
                  <a-select-option :value="3">个人客户</a-select-option>
                  <a-select-option :value="4">事业单位</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="客户等级">
                <a-select v-model:value="queryParams.level" placeholder="请选择" style="width: 120px" allow-clear>
                  <a-select-option :value="1">A级</a-select-option>
                  <a-select-option :value="2">B级</a-select-option>
                  <a-select-option :value="3">C级</a-select-option>
                  <a-select-option :value="4">D级</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="信用等级">
                <a-select v-model:value="queryParams.creditLevel" placeholder="请选择" style="width: 120px" allow-clear>
                  <a-select-option :value="1">优秀</a-select-option>
                  <a-select-option :value="2">良好</a-select-option>
                  <a-select-option :value="3">一般</a-select-option>
                  <a-select-option :value="4">较差</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="是否公海">
                <a-select v-model:value="queryParams.isPublic" placeholder="请选择" style="width: 100px" allow-clear>
                  <a-select-option :value="0">否</a-select-option>
                  <a-select-option :value="1">是</a-select-option>
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
                <PlusOutlined /> 新增客户
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
              <template v-if="column.key === 'level'">
                <a-tag :color="getLevelColor(record.level)">
                  {{ record.levelName || '-' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'creditLevel'">
                <a-tag :color="getCreditLevelColor(record.creditLevel)">
                  {{ record.creditLevelName || '-' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'isPublic'">
                <a-tag :color="record.isPublic === 1 ? 'orange' : 'green'">
                  {{ record.isPublicName }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'creditScore'">
                <span :class="{ 'text-red': record.creditScore !== undefined && record.creditScore < 60 }">
                  {{ record.creditScore ?? '-' }}
                </span>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
                <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
                <a-button type="link" size="small" @click="handleClaim(record)" v-if="record.isPublic === 1">认领</a-button>
                <a-button type="link" size="small" @click="handleAssign(record)" v-if="record.isPublic === 0">分配</a-button>
                <a-button type="link" size="small" @click="handleCreditAdjust(record)">信用调整</a-button>
                <a-popconfirm title="确定删除该客户吗？" @confirm="handleDelete(record.id)">
                  <a-button type="link" danger size="small">删除</a-button>
                </a-popconfirm>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="public" tab="公海客户">
          <div class="toolbar">
            <a-form layout="inline" :model="publicQueryParams">
              <a-form-item label="客户名称">
                <a-input v-model:value="publicQueryParams.customerName" placeholder="请输入客户名称" style="width: 180px" allow-clear />
              </a-form-item>
              <a-form-item label="联系人">
                <a-input v-model:value="publicQueryParams.contactPerson" placeholder="请输入联系人" style="width: 120px" allow-clear />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="handlePublicQuery">
                  <SearchOutlined /> 查询
                </a-button>
                <a-button style="margin-left: 8px" @click="handlePublicReset">
                  <ReloadOutlined /> 重置
                </a-button>
              </a-form-item>
            </a-form>
          </div>

          <a-table
            :columns="publicColumns"
            :data-source="publicTableData"
            :loading="publicLoading"
            :pagination="publicPagination"
            row-key="id"
            @change="handlePublicTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'level'">
                <a-tag :color="getLevelColor(record.level)">
                  {{ record.levelName || '-' }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
                <a-button type="primary" size="small" @click="handleClaim(record)">
                  <UserOutlined /> 认领
                </a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      :title="formModalTitle"
      width="700px"
      @ok="handleFormSubmit"
      @cancel="formModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="客户名称" name="customerName">
              <a-input v-model:value="formData.customerName" placeholder="请输入客户名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="客户类型" name="customerType">
              <a-select v-model:value="formData.customerType" placeholder="请选择客户类型">
                <a-select-option :value="1">企业客户</a-select-option>
                <a-select-option :value="2">政府机构</a-select-option>
                <a-select-option :value="3">个人客户</a-select-option>
                <a-select-option :value="4">事业单位</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="联系人" name="contactPerson">
              <a-input v-model:value="formData.contactPerson" placeholder="请输入联系人" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="联系电话" name="contactPhone">
              <a-input v-model:value="formData.contactPhone" placeholder="请输入联系电话" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="邮箱" name="contactEmail">
              <a-input v-model:value="formData.contactEmail" placeholder="请输入邮箱" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="法人">
              <a-input v-model:value="formData.legalPerson" placeholder="请输入法人" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="地址">
          <a-input v-model:value="formData.address" placeholder="请输入地址" />
        </a-form-item>
        <a-form-item label="统一社会信用代码">
          <a-input v-model:value="formData.creditCode" placeholder="请输入统一社会信用代码" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="客户等级" name="level">
              <a-select v-model:value="formData.level" placeholder="请选择">
                <a-select-option :value="1">A级</a-select-option>
                <a-select-option :value="2">B级</a-select-option>
                <a-select-option :value="3">C级</a-select-option>
                <a-select-option :value="4">D级</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="信用等级" name="creditLevel">
              <a-select v-model:value="formData.creditLevel" placeholder="请选择">
                <a-select-option :value="1">优秀</a-select-option>
                <a-select-option :value="2">良好</a-select-option>
                <a-select-option :value="3">一般</a-select-option>
                <a-select-option :value="4">较差</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="是否公海" name="isPublic">
              <a-select v-model:value="formData.isPublic" placeholder="请选择">
                <a-select-option :value="0">否</a-select-option>
                <a-select-option :value="1">是</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" placeholder="请输入备注" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="客户详情"
      width="900px"
      :footer="null"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered>
            <a-descriptions-item label="客户名称">{{ customerDetail?.customerName }}</a-descriptions-item>
            <a-descriptions-item label="客户类型">{{ customerDetail?.customerTypeName }}</a-descriptions-item>
            <a-descriptions-item label="联系人">{{ customerDetail?.contactPerson }}</a-descriptions-item>
            <a-descriptions-item label="联系电话">{{ customerDetail?.contactPhone }}</a-descriptions-item>
            <a-descriptions-item label="邮箱">{{ customerDetail?.contactEmail || '-' }}</a-descriptions-item>
            <a-descriptions-item label="法人">{{ customerDetail?.legalPerson || '-' }}</a-descriptions-item>
            <a-descriptions-item label="地址" :span="2">{{ customerDetail?.address || '-' }}</a-descriptions-item>
            <a-descriptions-item label="统一社会信用代码">{{ customerDetail?.creditCode || '-' }}</a-descriptions-item>
            <a-descriptions-item label="信用积分">{{ customerDetail?.creditScore ?? '-' }}</a-descriptions-item>
            <a-descriptions-item label="客户等级">
              <a-tag :color="getLevelColor(customerDetail?.level)">{{ customerDetail?.levelName }}</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="信用等级">
              <a-tag :color="getCreditLevelColor(customerDetail?.creditLevel)">{{ customerDetail?.creditLevelName }}</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="是否公海">
              <a-tag :color="customerDetail?.isPublic === 1 ? 'orange' : 'green'">
                {{ customerDetail?.isPublicName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="创建人">{{ customerDetail?.createByName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ customerDetail?.createTime }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ customerDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="qualification" tab="资质文件">
          <div class="detail-toolbar">
            <a-button type="primary" size="small" @click="handleAddQualification">
              <PlusOutlined /> 新增资质
            </a-button>
          </div>
          <a-table
            :columns="qualificationColumns"
            :data-source="customerDetail?.qualifications || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'fileUrl'">
                <a v-if="record.fileUrl" :href="record.fileUrl" target="_blank">查看文件</a>
                <span v-else>-</span>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-popconfirm title="确定删除该资质吗？" @confirm="handleDeleteQualification(record.id)">
                  <a-button type="link" danger size="small">删除</a-button>
                </a-popconfirm>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="credit" tab="信用记录">
          <a-table
            :columns="creditColumns"
            :data-source="customerDetail?.credits || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'changeValue'">
                <span :class="record.changeType === 1 ? 'text-green' : 'text-red'">
                  {{ record.changeType === 1 ? '+' : '-' }}{{ record.changeValue }}
                </span>
              </template>
              <template v-else-if="column.key === 'changeType'">
                <a-tag :color="record.changeType === 1 ? 'green' : 'red'">
                  {{ record.changeTypeName }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="follow" tab="跟进记录">
          <div class="detail-toolbar">
            <a-button type="primary" size="small" @click="handleAddFollow">
              <PlusOutlined /> 新增跟进
            </a-button>
          </div>
          <a-timeline>
            <a-timeline-item v-for="item in customerDetail?.follows || []" :key="item.id">
              <template #dot>
                <a-badge status="processing" />
              </template>
              <a-card size="small" style="margin-bottom: 8px">
                <div class="follow-header">
                  <a-tag :color="getFollowTypeColor(item.followType)">
                    {{ item.followTypeName }}
                  </a-tag>
                  <span class="follow-time">{{ item.followTime }}</span>
                  <span class="follow-person">跟进人：{{ item.followPerson || '-' }}</span>
                </div>
                <div class="follow-content">{{ item.followContent }}</div>
                <div class="follow-next" v-if="item.nextFollowTime">
                  下次跟进时间：{{ item.nextFollowTime }}
                </div>
              </a-card>
            </a-timeline-item>
          </a-timeline>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <a-modal
      v-model:open="creditModalVisible"
      title="信用调整"
      width="500px"
      @ok="handleCreditSubmit"
      @cancel="creditModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="creditFormRef"
        :model="creditFormData"
        :rules="creditFormRules"
        layout="vertical"
      >
        <a-form-item label="调整类型" name="changeType">
          <a-select v-model:value="creditFormData.changeType" placeholder="请选择调整类型">
            <a-select-option :value="1">加分</a-select-option>
            <a-select-option :value="2">扣分</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="调整分值" name="changeValue">
          <a-input-number v-model:value="creditFormData.changeValue" :min="1" :max="100" style="width: 100%" placeholder="请输入分值" />
        </a-form-item>
        <a-form-item label="调整原因" name="changeReason">
          <a-textarea v-model:value="creditFormData.changeReason" :rows="3" placeholder="请输入调整原因" />
        </a-form-item>
        <a-form-item label="调整时间">
          <a-date-picker v-model:value="creditFormData.changeTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="qualificationModalVisible"
      title="新增资质"
      width="600px"
      @ok="handleQualificationSubmit"
      @cancel="qualificationModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="qualificationFormRef"
        :model="qualificationFormData"
        :rules="qualificationFormRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="资质名称" name="qualificationName">
              <a-input v-model:value="qualificationFormData.qualificationName" placeholder="请输入资质名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="资质类型">
              <a-select v-model:value="qualificationFormData.qualificationType" placeholder="请选择">
                <a-select-option value="营业执照">营业执照</a-select-option>
                <a-select-option value="资质证书">资质证书</a-select-option>
                <a-select-option value="ISO认证">ISO认证</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="证书编号">
              <a-input v-model:value="qualificationFormData.certificateNo" placeholder="请输入证书编号" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发证机关">
              <a-input v-model:value="qualificationFormData.issuingAuthority" placeholder="请输入发证机关" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="发证日期">
              <a-date-picker v-model:value="qualificationFormData.issueDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="有效期至">
              <a-date-picker v-model:value="qualificationFormData.expiryDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="文件上传">
          <a-upload
            v-model:file-list="fileList"
            action="/api/upload"
            :before-upload="beforeUpload"
            @change="handleUploadChange"
            :max-count="1"
          >
            <a-button>
              <UploadOutlined /> 点击上传
            </a-button>
          </a-upload>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="qualificationFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="followModalVisible"
      title="新增跟进"
      width="500px"
      @ok="handleFollowSubmit"
      @cancel="followModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form
        ref="followFormRef"
        :model="followFormData"
        :rules="followFormRules"
        layout="vertical"
      >
        <a-form-item label="跟进类型" name="followType">
          <a-select v-model:value="followFormData.followType" placeholder="请选择跟进类型">
            <a-select-option :value="1">电话沟通</a-select-option>
            <a-select-option :value="2">上门拜访</a-select-option>
            <a-select-option :value="3">邮件往来</a-select-option>
            <a-select-option :value="4">微信沟通</a-select-option>
            <a-select-option :value="5">客户来访</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="跟进内容" name="followContent">
          <a-textarea v-model:value="followFormData.followContent" :rows="4" placeholder="请输入跟进内容" />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="跟进时间" name="followTime">
              <a-date-picker v-model:value="followFormData.followTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="下次跟进时间">
              <a-date-picker v-model:value="followFormData.nextFollowTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="跟进人">
          <a-input v-model:value="followFormData.followPerson" placeholder="请输入跟进人" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="assignModalVisible"
      title="分配客户"
      width="400px"
      @ok="handleAssignSubmit"
      @cancel="assignModalVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="分配给">
          <a-select v-model:value="assignUserId" placeholder="请选择人员">
            <a-select-option :value="1">张三</a-select-option>
            <a-select-option :value="2">李四</a-select-option>
            <a-select-option :value="3">王五</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
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
  PlusOutlined,
  UserOutlined,
  UploadOutlined
} from '@ant-design/icons-vue'
import type {
  CustomerQuery,
  CustomerSaveDTO,
  CustomerVO,
  CustomerDetailVO,
  CustomerCreditSaveDTO,
  CustomerQualificationSaveDTO,
  CustomerFollowSaveDTO
} from '@/types'
import {
  getCustomerPage,
  getPublicCustomerPage,
  getCustomerById,
  addCustomer,
  updateCustomer,
  deleteCustomer,
  claimCustomer,
  assignCustomer,
  addCredit,
  addQualification,
  deleteQualification,
  addFollow
} from '@/api/customer'

const loading = ref(false)
const publicLoading = ref(false)
const submitting = ref(false)
const activeTab = ref('all')
const detailTab = ref('basic')

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)
const creditModalVisible = ref(false)
const qualificationModalVisible = ref(false)
const followModalVisible = ref(false)
const assignModalVisible = ref(false)

const formRef = ref()
const creditFormRef = ref()
const qualificationFormRef = ref()
const followFormRef = ref()
const isEdit = ref(false)
const currentCustomerId = ref<number>()
const assignUserId = ref<number>()
const fileList = ref<any[]>([])

const queryParams = reactive<CustomerQuery>({
  pageNum: 1,
  pageSize: 10,
  customerName: '',
  contactPerson: '',
  contactPhone: '',
  customerType: undefined,
  level: undefined,
  creditLevel: undefined,
  isPublic: undefined
})

const publicQueryParams = reactive<CustomerQuery>({
  pageNum: 1,
  pageSize: 10,
  customerName: '',
  contactPerson: '',
  isPublic: 1
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const publicPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<CustomerVO[]>([])
const publicTableData = ref<CustomerVO[]>([])
const customerDetail = ref<CustomerDetailVO | null>(null)

const formData = reactive<CustomerSaveDTO>({
  customerName: '',
  contactPerson: '',
  contactPhone: ''
})

const creditFormData = reactive<CustomerCreditSaveDTO>({
  customerId: 0,
  changeType: 1,
  changeValue: 0,
  changeReason: ''
})

const qualificationFormData = reactive<CustomerQualificationSaveDTO>({
  customerId: 0,
  qualificationName: ''
})

const followFormData = reactive<CustomerFollowSaveDTO>({
  customerId: 0,
  followType: 1,
  followContent: ''
})

const formRules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  customerType: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  level: [{ required: true, message: '请选择客户等级', trigger: 'change' }],
  creditLevel: [{ required: true, message: '请选择信用等级', trigger: 'change' }],
  isPublic: [{ required: true, message: '请选择是否公海', trigger: 'change' }]
}

const creditFormRules = {
  changeType: [{ required: true, message: '请选择调整类型', trigger: 'change' }],
  changeValue: [{ required: true, message: '请输入调整分值', trigger: 'blur' }],
  changeReason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

const qualificationFormRules = {
  qualificationName: [{ required: true, message: '请输入资质名称', trigger: 'blur' }]
}

const followFormRules = {
  followType: [{ required: true, message: '请选择跟进类型', trigger: 'change' }],
  followContent: [{ required: true, message: '请输入跟进内容', trigger: 'blur' }],
  followTime: [{ required: true, message: '请选择跟进时间', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 180, ellipsis: true },
  { title: '客户类型', dataIndex: 'customerTypeName', key: 'customerType', width: 100 },
  { title: '联系人', dataIndex: 'contactPerson', key: 'contactPerson', width: 100 },
  { title: '联系电话', dataIndex: 'contactPhone', key: 'contactPhone', width: 130 },
  { title: '客户等级', dataIndex: 'level', key: 'level', width: 100 },
  { title: '信用等级', dataIndex: 'creditLevel', key: 'creditLevel', width: 100 },
  { title: '信用积分', dataIndex: 'creditScore', key: 'creditScore', width: 100 },
  { title: '是否公海', dataIndex: 'isPublic', key: 'isPublic', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 280, fixed: 'right' }
]

const publicColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 200 },
  { title: '客户类型', dataIndex: 'customerTypeName', key: 'customerType', width: 120 },
  { title: '联系人', dataIndex: 'contactPerson', key: 'contactPerson', width: 120 },
  { title: '联系电话', dataIndex: 'contactPhone', key: 'contactPhone', width: 140 },
  { title: '客户等级', dataIndex: 'level', key: 'level', width: 120 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' }
]

const qualificationColumns = [
  { title: '资质名称', dataIndex: 'qualificationName', key: 'qualificationName', width: 150 },
  { title: '资质类型', dataIndex: 'qualificationType', key: 'qualificationType', width: 100 },
  { title: '证书编号', dataIndex: 'certificateNo', key: 'certificateNo', width: 150 },
  { title: '发证机关', dataIndex: 'issuingAuthority', key: 'issuingAuthority', width: 120 },
  { title: '发证日期', dataIndex: 'issueDate', key: 'issueDate', width: 110 },
  { title: '有效期至', dataIndex: 'expiryDate', key: 'expiryDate', width: 110 },
  { title: '文件', dataIndex: 'fileUrl', key: 'fileUrl', width: 100 },
  { title: '操作', key: 'action', width: 80 }
]

const creditColumns = [
  { title: '调整类型', dataIndex: 'changeType', key: 'changeType', width: 100 },
  { title: '调整分值', dataIndex: 'changeValue', key: 'changeValue', width: 100 },
  { title: '调整原因', dataIndex: 'changeReason', key: 'changeReason' },
  { title: '调整时间', dataIndex: 'changeTime', key: 'changeTime', width: 170 },
  { title: '操作人', dataIndex: 'operator', key: 'operator', width: 100 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCustomerPage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchPublicData = async () => {
  publicLoading.value = true
  try {
    const res = await getPublicCustomerPage(publicQueryParams)
    publicTableData.value = res.data.list
    publicPagination.value.total = res.data.total
  } finally {
    publicLoading.value = false
  }
}

const handleTabChange = (key: string) => {
  if (key === 'public') {
    fetchPublicData()
  } else {
    fetchData()
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    customerName: '',
    contactPerson: '',
    contactPhone: '',
    customerType: undefined,
    level: undefined,
    creditLevel: undefined,
    isPublic: undefined
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

const handlePublicQuery = () => {
  publicQueryParams.pageNum = 1
  publicPagination.value.current = 1
  fetchPublicData()
}

const handlePublicReset = () => {
  publicQueryParams.customerName = ''
  publicQueryParams.contactPerson = ''
  handlePublicQuery()
}

const handlePublicTableChange = (pag: any) => {
  publicQueryParams.pageNum = pag.current
  publicQueryParams.pageSize = pag.pageSize
  publicPagination.value.current = pag.current
  publicPagination.value.pageSize = pag.pageSize
  fetchPublicData()
}

const handleAdd = () => {
  isEdit.value = false
  formModalTitle.value = '新增客户'
  Object.assign(formData, {
    customerName: '',
    customerType: undefined,
    contactPerson: '',
    contactPhone: '',
    contactEmail: '',
    address: '',
    legalPerson: '',
    creditCode: '',
    level: undefined,
    creditLevel: undefined,
    isPublic: undefined,
    remark: ''
  })
  formModalVisible.value = true
}

const handleEdit = (record: CustomerVO) => {
  isEdit.value = true
  formModalTitle.value = '编辑客户'
  Object.assign(formData, {
    id: record.id,
    customerName: record.customerName,
    customerType: record.customerType,
    contactPerson: record.contactPerson,
    contactPhone: record.contactPhone,
    contactEmail: record.contactEmail,
    address: record.address,
    legalPerson: record.legalPerson,
    creditCode: record.creditCode,
    level: record.level,
    creditLevel: record.creditLevel,
    isPublic: record.isPublic,
    remark: record.remark
  })
  formModalVisible.value = true
}

const handleView = async (record: CustomerVO) => {
  try {
    const res = await getCustomerById(record.id)
    customerDetail.value = res.data
    detailTab.value = 'basic'
    currentCustomerId.value = record.id
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get customer detail error:', error)
  }
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await updateCustomer(formData)
      message.success('更新成功')
    } else {
      await addCustomer(formData)
      message.success('新增成功')
    }
    formModalVisible.value = false
    fetchData()
    if (activeTab.value === 'public') fetchPublicData()
  } catch (error) {
    console.error('Submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteCustomer(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const handleClaim = async (record: CustomerVO) => {
  try {
    await claimCustomer(record.id)
    message.success('认领成功')
    fetchData()
    fetchPublicData()
  } catch (error) {
    console.error('Claim error:', error)
  }
}

const handleAssign = (record: CustomerVO) => {
  currentCustomerId.value = record.id
  assignUserId.value = undefined
  assignModalVisible.value = true
}

const handleAssignSubmit = async () => {
  if (!currentCustomerId.value || !assignUserId.value) {
    message.warning('请选择分配人员')
    return
  }
  try {
    await assignCustomer(currentCustomerId.value, assignUserId.value)
    message.success('分配成功')
    assignModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Assign error:', error)
  }
}

const handleCreditAdjust = (record: CustomerVO) => {
  currentCustomerId.value = record.id
  Object.assign(creditFormData, {
    customerId: record.id,
    changeType: 1,
    changeValue: 0,
    changeReason: '',
    changeTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
  })
  creditModalVisible.value = true
}

const handleCreditSubmit = async () => {
  try {
    await creditFormRef.value.validate()
    submitting.value = true
    await addCredit(creditFormData)
    message.success('信用调整成功')
    creditModalVisible.value = false
    fetchData()
    if (currentCustomerId.value) {
      const res = await getCustomerById(currentCustomerId.value)
      customerDetail.value = res.data
    }
  } catch (error) {
    console.error('Credit submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleAddQualification = () => {
  Object.assign(qualificationFormData, {
    customerId: currentCustomerId.value,
    qualificationName: '',
    qualificationType: '',
    certificateNo: '',
    issuingAuthority: '',
    issueDate: '',
    expiryDate: '',
    fileUrl: '',
    remark: ''
  })
  fileList.value = []
  qualificationModalVisible.value = true
}

const handleQualificationSubmit = async () => {
  try {
    await qualificationFormRef.value.validate()
    submitting.value = true
    await addQualification(qualificationFormData)
    message.success('资质添加成功')
    qualificationModalVisible.value = false
    if (currentCustomerId.value) {
      const res = await getCustomerById(currentCustomerId.value)
      customerDetail.value = res.data
    }
  } catch (error) {
    console.error('Qualification submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDeleteQualification = async (id: number) => {
  try {
    await deleteQualification(id)
    message.success('删除成功')
    if (currentCustomerId.value) {
      const res = await getCustomerById(currentCustomerId.value)
      customerDetail.value = res.data
    }
  } catch (error) {
    console.error('Delete qualification error:', error)
  }
}

const handleAddFollow = () => {
  Object.assign(followFormData, {
    customerId: currentCustomerId.value,
    followType: 1,
    followContent: '',
    followTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    nextFollowTime: '',
    followPerson: ''
  })
  followModalVisible.value = true
}

const handleFollowSubmit = async () => {
  try {
    await followFormRef.value.validate()
    submitting.value = true
    await addFollow(followFormData)
    message.success('跟进记录添加成功')
    followModalVisible.value = false
    if (currentCustomerId.value) {
      const res = await getCustomerById(currentCustomerId.value)
      customerDetail.value = res.data
    }
  } catch (error) {
    console.error('Follow submit error:', error)
  } finally {
    submitting.value = false
  }
}

const beforeUpload = (file: File) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error('文件大小不能超过10MB')
    return false
  }
  return true
}

const handleUploadChange = (info: any) => {
  if (info.file.status === 'done') {
    qualificationFormData.fileUrl = info.file.response?.data?.url
  }
}

const getLevelColor = (level?: number) => {
  const colors: Record<number, string> = { 1: 'gold', 2: 'blue', 3: 'green', 4: 'default' }
  return colors[level || 0] || 'default'
}

const getCreditLevelColor = (level?: number) => {
  const colors: Record<number, string> = { 1: 'green', 2: 'blue', 3: 'orange', 4: 'red' }
  return colors[level || 0] || 'default'
}

const getFollowTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'blue', 2: 'purple', 3: 'cyan', 4: 'green', 5: 'orange' }
  return colors[type || 0] || 'default'
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

.detail-toolbar {
  margin-bottom: 12px;
  text-align: right;
}

.follow-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.follow-time {
  color: #999;
  font-size: 12px;
}

.follow-person {
  color: #666;
  font-size: 12px;
  margin-left: auto;
}

.follow-content {
  color: #333;
  line-height: 1.6;
}

.follow-next {
  margin-top: 8px;
  color: #fa8c16;
  font-size: 12px;
}

.text-red {
  color: #f5222d;
}

.text-green {
  color: #52c41a;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
