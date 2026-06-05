<template>
  <div class="quotation">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="报价单号">
            <a-input v-model:value="queryParams.quotationNo" placeholder="请输入报价单号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="报价单名称">
            <a-input v-model:value="queryParams.quotationName" placeholder="请输入报价单名称" style="width: 180px" allow-clear />
          </a-form-item>
          <a-form-item label="客户名称">
            <a-input v-model:value="queryParams.customerName" placeholder="请输入客户名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.status" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">草稿</a-select-option>
              <a-select-option :value="1">待审批</a-select-option>
              <a-select-option :value="2">审批通过</a-select-option>
              <a-select-option :value="3">审批驳回</a-select-option>
              <a-select-option :value="4">待客户确认</a-select-option>
              <a-select-option :value="5">客户已确认</a-select-option>
              <a-select-option :value="6">客户已拒绝</a-select-option>
              <a-select-option :value="7">已作废</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="报价日期">
            <a-range-picker
              v-model:value="quotationDateRange"
              style="width: 260px"
              value-format="YYYY-MM-DD"
              @change="handleQuotationDateChange"
            />
          </a-form-item>
          <a-form-item label="是否已转委托">
            <a-select v-model:value="queryParams.isConverted" placeholder="请选择" style="width: 120px" allow-clear>
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
            <PlusOutlined /> 新增报价单
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
        :scroll="{ x: 1400 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'approvalStatus'">
            <a-tag :color="getApprovalStatusColor(record.approvalStatus)">
              {{ record.approvalStatus === 0 ? '-' : (record.approvalStatus === 1 ? '审批中' : record.approvalStatus === 2 ? '通过' : '驳回') }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'isConverted'">
            <a-tag :color="record.isConverted === 1 ? 'green' : 'default'">
              {{ record.isConverted === 1 ? '是' : '否' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'totalAmount'">
            ¥{{ formatMoney(record.totalAmount) }}
          </template>
          <template v-else-if="column.key === 'actualAmount'">
            ¥{{ formatMoney(record.actualAmount) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
            <template v-if="record.status === 0">
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-button type="link" size="small" @click="handleSubmitApproval(record)">提交审批</a-button>
              <a-popconfirm title="确定删除该报价单吗？" @confirm="handleDelete(record.id)">
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </template>
            <template v-else-if="record.approvalStatus === 1">
              <a-button type="link" size="small" @click="handleApproval(record)">审批</a-button>
            </template>
            <template v-else-if="record.status === 2">
              <a-button type="link" size="small" @click="handleCustomerConfirm(record)">客户确认</a-button>
              <a-button type="link" size="small" @click="handleConvert(record)">转委托单</a-button>
              <a-button type="link" size="small" @click="handlePrint(record)">打印预览</a-button>
              <a-button type="link" danger size="small" @click="handleCancel(record)">报价作废</a-button>
            </template>
            <template v-else-if="record.status === 5">
              <a-button type="link" size="small" @click="handleConvert(record)">转委托单</a-button>
              <a-button type="link" size="small" @click="handlePrint(record)">打印预览</a-button>
            </template>
            <template v-else>
              <a-button type="link" size="small" @click="handlePrint(record)">打印预览</a-button>
            </template>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formModalVisible"
      :title="formModalTitle"
      width="1100px"
      @ok="handleFormSubmit"
      @cancel="formModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
      :body-style="{ maxHeight: '65vh', overflowY: 'auto' }"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="16">
            <a-form-item label="报价单名称" name="quotationName">
              <a-input v-model:value="formData.quotationName" placeholder="请输入报价单名称" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="折扣率（%）" name="discountRate">
              <a-input-number
                v-model:value="formData.discountRate"
                :min="0"
                :max="100"
                :precision="2"
                style="width: 100%"
                placeholder="0-100"
                @change="calculateAmount"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="客户" name="customerId">
              <a-select
                v-model:value="formData.customerId"
                placeholder="请选择客户"
                show-search
                :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
                @change="handleCustomerChange"
              >
                <a-select-option v-for="item in customerList" :key="item.id" :value="item.id">
                  {{ item.customerName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="关联合同">
              <a-select
                v-model:value="formData.contractId"
                placeholder="请选择合同（可选）"
                allow-clear
                show-search
                :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
              >
                <a-select-option v-for="item in contractList" :key="item.id" :value="item.id">
                  {{ item.contractNo }} - {{ item.contractName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="报价日期" name="quotationDate">
              <a-date-picker v-model:value="formData.quotationDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="报价有效期" name="validDate">
              <a-date-picker v-model:value="formData.validDate" style="width: 100%" value-format="YYYY-MM-DD" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="报价说明">
          <a-textarea v-model:value="formData.quotationContent" :rows="2" placeholder="请输入报价说明" />
        </a-form-item>

        <a-divider orientation="left">报价明细</a-divider>
        <div class="items-toolbar">
          <a-button type="primary" size="small" @click="addItem">
            <PlusOutlined /> 添加检测项目
          </a-button>
          <span class="amount-info">
            总金额：<span class="amount-text">¥{{ formatMoney(totalAmount) }}</span>
            折扣率：<span class="amount-text">{{ formData.discountRate || 0 }}%</span>
            实际金额：<span class="amount-text total">¥{{ formatMoney(actualAmount) }}</span>
          </span>
        </div>
        <a-table
          :columns="itemColumns"
          :data-source="formData.items"
          :pagination="false"
          row-key="key"
          size="small"
          :scroll="{ x: 900 }"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'itemId'">
              <a-select
                v-model:value="record.itemId"
                placeholder="请选择检测项目"
                show-search
                style="width: 200px"
                :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
                @change="(val: number) => handleItemChange(index, val)"
              >
                <a-select-option v-for="item in testItemList" :key="item.id" :value="item.id">
                  {{ item.itemCode }} - {{ item.itemName }}
                </a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'standardId'">
              <a-select
                v-model:value="record.standardId"
                placeholder="请选择检测标准"
                show-search
                style="width: 220px"
                :disabled="!record.itemId"
                :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
                @change="(val: number) => handleStandardChange(index, val)"
              >
                <a-select-option v-for="item in getStandardList(record.itemId)" :key="item.id" :value="item.id">
                  {{ item.standardNo }} - {{ item.standardName }}
                </a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'unitPrice'">
              <a-input-number
                v-model:value="record.unitPrice"
                :min="0"
                :precision="2"
                style="width: 100px"
                @change="calculateItemAmount(index)"
              />
            </template>
            <template v-else-if="column.key === 'quantity'">
              <a-input-number
                v-model:value="record.quantity"
                :min="1"
                style="width: 80px"
                @change="calculateItemAmount(index)"
              />
            </template>
            <template v-else-if="column.key === 'subtotal'">
              <span>¥{{ formatMoney(record.subtotal) }}</span>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button type="link" danger size="small" @click="removeItem(index)">删除</a-button>
            </template>
          </template>
        </a-table>

        <a-form-item label="备注">
          <a-textarea v-model:value="formData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="detailModalVisible"
      title="报价单详情"
      width="1100px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered size="small">
            <a-descriptions-item label="报价单号">{{ quotationDetail?.quotationNo }}</a-descriptions-item>
            <a-descriptions-item label="报价单名称">{{ quotationDetail?.quotationName }}</a-descriptions-item>
            <a-descriptions-item label="客户名称">{{ quotationDetail?.customerName }}</a-descriptions-item>
            <a-descriptions-item label="关联合同">{{ quotationDetail?.contractId ? '已关联' : '-' }}</a-descriptions-item>
            <a-descriptions-item label="报价日期">{{ quotationDetail?.quotationDate }}</a-descriptions-item>
            <a-descriptions-item label="报价有效期">{{ quotationDetail?.validDate }}</a-descriptions-item>
            <a-descriptions-item label="总金额">¥{{ formatMoney(quotationDetail?.totalAmount || 0) }}</a-descriptions-item>
            <a-descriptions-item label="折扣率">{{ quotationDetail?.discountRate }}%</a-descriptions-item>
            <a-descriptions-item label="实际金额">¥{{ formatMoney(quotationDetail?.actualAmount || 0) }}</a-descriptions-item>
            <a-descriptions-item label="状态">
              <a-tag :color="getStatusColor(quotationDetail?.status)">
                {{ quotationDetail?.statusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="是否已转委托">
              <a-tag :color="quotationDetail?.isConverted === 1 ? 'green' : 'default'">
                {{ quotationDetail?.isConverted === 1 ? '是' : '否' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="创建人">{{ quotationDetail?.createByName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="创建时间">{{ quotationDetail?.createTime }}</a-descriptions-item>
            <a-descriptions-item label="客户确认时间">{{ quotationDetail?.confirmTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="报价说明" :span="2">{{ quotationDetail?.quotationContent || '-' }}</a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ quotationDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="items" tab="报价明细">
          <a-table
            :columns="detailItemColumns"
            :data-source="quotationDetail?.items || []"
            :pagination="false"
            row-key="id"
            size="small"
            :scroll="{ x: 800 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'subtotal'">
                ¥{{ formatMoney(record.subtotal) }}
              </template>
              <template v-else-if="column.key === 'unitPrice'">
                ¥{{ formatMoney(record.unitPrice) }}
              </template>
            </template>
          </a-table>
          <div class="total-row">
            <span>总金额：¥{{ formatMoney(quotationDetail?.totalAmount || 0) }}</span>
            <span>折扣率：{{ quotationDetail?.discountRate }}%</span>
            <span class="total-amount">实际金额：¥{{ formatMoney(quotationDetail?.actualAmount || 0) }}</span>
          </div>
        </a-tab-pane>

        <a-tab-pane key="approval" tab="审批记录">
          <a-table
            :columns="approvalColumns"
            :data-source="quotationDetail?.approvalRecords || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'approvalResult'">
                <a-tag :color="record.approvalResult === 1 ? 'green' : record.approvalResult === 2 ? 'red' : 'default'">
                  {{ record.approvalResultName || '-' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <a-modal
      v-model:open="approvalModalVisible"
      title="报价单审批"
      width="500px"
      @ok="handleApprovalSubmit"
      @cancel="approvalModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-descriptions :column="1" bordered size="small" style="margin-bottom: 16px">
        <a-descriptions-item label="报价单号">{{ currentQuotation?.quotationNo }}</a-descriptions-item>
        <a-descriptions-item label="报价单名称">{{ currentQuotation?.quotationName }}</a-descriptions-item>
        <a-descriptions-item label="客户名称">{{ currentQuotation?.customerName }}</a-descriptions-item>
        <a-descriptions-item label="报价金额">¥{{ formatMoney(currentQuotation?.actualAmount || 0) }}</a-descriptions-item>
      </a-descriptions>
      <a-form
        ref="approvalFormRef"
        :model="approvalFormData"
        :rules="approvalFormRules"
        layout="vertical"
      >
        <a-form-item label="审批结果" name="approvalResult">
          <a-radio-group v-model:value="approvalFormData.approvalResult">
            <a-radio :value="1">通过</a-radio>
            <a-radio :value="2">驳回</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="审批意见" name="approvalRemark">
          <a-textarea v-model:value="approvalFormData.approvalRemark" :rows="3" placeholder="请输入审批意见" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="confirmModalVisible"
      title="客户确认"
      width="400px"
      @ok="handleCustomerConfirmSubmit"
      @cancel="confirmModalVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="确认结果">
          <a-radio-group v-model:value="confirmResult">
            <a-radio :value="1">确认</a-radio>
            <a-radio :value="2">拒绝</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="确认人">
          <a-input v-model:value="confirmPerson" placeholder="请输入客户确认人" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="convertModalVisible"
      title="转委托单"
      width="1000px"
      @ok="handleConvertSubmit"
      @cancel="convertModalVisible = false"
      :confirm-loading="submitting"
      :destroy-on-close="true"
      :body-style="{ maxHeight: '60vh', overflowY: 'auto' }"
    >
      <a-alert message="系统将自动复制报价单中的检测项目信息，请补充委托单必要信息。" type="info" style="margin-bottom: 16px" />
      <a-form
        ref="convertFormRef"
        :model="convertFormData"
        :rules="convertFormRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="委托类型" name="entrustType">
              <a-select v-model:value="convertFormData.entrustType" placeholder="请选择委托类型">
                <a-select-option :value="1">常规检测</a-select-option>
                <a-select-option :value="2">仲裁检测</a-select-option>
                <a-select-option :value="3">委托检测</a-select-option>
                <a-select-option :value="4">监督检测</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="样品名称" name="sampleName">
              <a-input v-model:value="convertFormData.sampleName" placeholder="请输入样品名称" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="样品类型" name="sampleType">
              <a-select v-model:value="convertFormData.sampleType" placeholder="请选择样品类型" show-search allow-clear>
                <a-select-option value="水质">水质</a-select-option>
                <a-select-option value="土壤">土壤</a-select-option>
                <a-select-option value="大气">大气</a-select-option>
                <a-select-option value="废气">废气</a-select-option>
                <a-select-option value="噪声">噪声</a-select-option>
                <a-select-option value="固废">固废</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="样品数量" name="sampleQuantity">
              <a-input-number v-model:value="convertFormData.sampleQuantity" :min="1" style="width: 100%" placeholder="请输入样品数量" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="是否加急" name="isUrgent">
              <a-select v-model:value="convertFormData.isUrgent">
                <a-select-option :value="0">否</a-select-option>
                <a-select-option :value="1">是</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="是否分包" name="isSubcontract">
              <a-select v-model:value="convertFormData.isSubcontract">
                <a-select-option :value="0">否</a-select-option>
                <a-select-option :value="1">是</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="16">
            <a-form-item label="采样地址">
              <a-input v-model:value="convertFormData.samplingAddress" placeholder="请输入采样地址" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="期望报告时间" name="expectedReportTime">
              <a-date-picker v-model:value="convertFormData.expectedReportTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="检测依据">
              <a-textarea v-model:value="convertFormData.detectionBasis" :rows="2" placeholder="请输入检测依据" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="评价依据">
              <a-textarea v-model:value="convertFormData.evaluationBasis" :rows="2" placeholder="请输入评价依据" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="关联合同">
              <a-select
                v-model:value="convertFormData.contractId"
                placeholder="请选择合同（可选）"
                allow-clear
                show-search
                :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
                @change="handleConvertContractChange"
              >
                <a-select-option v-for="item in contractList" :key="item.id" :value="item.id">
                  {{ item.contractNo }} - {{ item.contractName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="合同编号">
              <a-input v-model:value="convertFormData.contractNo" placeholder="自动填充" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="convertFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="printModalVisible"
      title="打印预览"
      width="900px"
      :footer="null"
      :destroy-on-close="true"
    >
      <div class="print-container" ref="printContainer">
        <div class="print-header">
          <h2>检测报价单</h2>
          <p class="print-no">报价单号：{{ printData?.quotationNo }}</p>
        </div>
        <div class="print-info">
          <div class="info-row">
            <span class="label">客户名称：</span>
            <span class="value">{{ printData?.customerName }}</span>
          </div>
          <div class="info-row">
            <span class="label">报价日期：</span>
            <span class="value">{{ printData?.quotationDate }}</span>
          </div>
          <div class="info-row">
            <span class="label">有效期至：</span>
            <span class="value">{{ printData?.validDate }}</span>
          </div>
        </div>
        <table class="print-table">
          <thead>
            <tr>
              <th style="width: 60px">序号</th>
              <th style="width: 120px">项目编码</th>
              <th>项目名称</th>
              <th style="width: 150px">检测标准</th>
              <th style="width: 80px">单位</th>
              <th style="width: 100px">单价（元）</th>
              <th style="width: 80px">数量</th>
              <th style="width: 100px">小计（元）</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in printData?.items || []" :key="item.id">
              <td>{{ index + 1 }}</td>
              <td>{{ item.itemCode }}</td>
              <td>{{ item.itemName }}</td>
              <td>{{ item.standardNo }}</td>
              <td>{{ item.unit }}</td>
              <td class="text-right">{{ formatMoney(item.unitPrice) }}</td>
              <td class="text-center">{{ item.quantity }}</td>
              <td class="text-right">{{ formatMoney(item.subtotal) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="print-summary">
          <div class="summary-row">
            <span>总金额：</span>
            <span>¥{{ formatMoney(printData?.totalAmount || 0) }}</span>
          </div>
          <div class="summary-row">
            <span>大写金额：</span>
            <span>{{ printData?.totalAmountCn }}</span>
          </div>
          <div class="summary-row">
            <span>折扣率：</span>
            <span>{{ printData?.discountRate }}%</span>
          </div>
          <div class="summary-row total">
            <span>实际金额：</span>
            <span>¥{{ formatMoney(printData?.actualAmount || 0) }}</span>
          </div>
          <div class="summary-row total-cn">
            <span>大写金额：</span>
            <span>{{ printData?.actualAmountCn }}</span>
          </div>
        </div>
        <div class="print-footer">
          <p v-if="printData?.quotationContent">报价说明：{{ printData.quotationContent }}</p>
          <p v-if="printData?.remark">备注：{{ printData.remark }}</p>
        </div>
      </div>
      <div class="print-actions">
        <a-button type="primary" @click="doPrint">
          <PrinterOutlined /> 打印
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  PrinterOutlined
} from '@ant-design/icons-vue'
import type {
  QuotationQuery,
  QuotationSaveDTO,
  QuotationItemSaveDTO,
  QuotationVO,
  QuotationDetailVO,
  QuotationApprovalDTO,
  ConvertToEntrustDTO,
  QuotationPrintVO,
  DictTestItem,
  DictTestStandard
} from '@/types'
import {
  getQuotationPage,
  getQuotationById,
  getQuotationPrint,
  addQuotation,
  updateQuotation,
  deleteQuotation,
  submitQuotationApproval,
  approveQuotation,
  customerConfirmQuotation,
  cancelQuotation,
  convertToEntrust
} from '@/api/quotation'
import { getCustomerSelectList } from '@/api/customer'
import { getContractSelectList } from '@/api/contract'
import { getTestItemList, getStandardByItemId } from '@/api/dict'

const loading = ref(false)
const submitting = ref(false)
const detailTab = ref('basic')

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)
const approvalModalVisible = ref(false)
const confirmModalVisible = ref(false)
const convertModalVisible = ref(false)
const printModalVisible = ref(false)

const formRef = ref()
const approvalFormRef = ref()
const convertFormRef = ref()
const printContainer = ref()
const isEdit = ref(false)
const currentQuotationId = ref<number>()
const currentQuotation = ref<QuotationVO | null>(null)
const quotationDateRange = ref<any[]>([])
const confirmResult = ref(1)
const confirmPerson = ref('')

const customerList = ref<any[]>([])
const contractList = ref<any[]>([])
const testItemList = ref<DictTestItem[]>([])
const standardMap = ref<Map<number, DictTestStandard[]>>(new Map())

const queryParams = reactive<QuotationQuery>({
  pageNum: 1,
  pageSize: 10,
  quotationNo: '',
  quotationName: '',
  customerName: '',
  status: undefined,
  isConverted: undefined,
  quotationDateStart: '',
  quotationDateEnd: ''
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<QuotationVO[]>([])
const quotationDetail = ref<QuotationDetailVO | null>(null)
const printData = ref<QuotationPrintVO | null>(null)

const formData = reactive<QuotationSaveDTO>({
  quotationName: '',
  customerId: 0,
  customerName: '',
  validDate: '',
  quotationDate: '',
  discountRate: 0,
  items: []
})

const approvalFormData = reactive<QuotationApprovalDTO>({
  id: 0,
  approvalResult: 1,
  approvalRemark: ''
})

const convertFormData = reactive<ConvertToEntrustDTO>({
  quotationId: 0,
  entrustType: 1,
  isUrgent: 0,
  isSubcontract: 0
})

const totalAmount = computed(() => {
  return formData.items.reduce((sum, item) => sum + (item.subtotal || 0), 0)
})

const actualAmount = computed(() => {
  const discount = (formData.discountRate || 0) / 100
  return Number((totalAmount.value * (1 - discount)).toFixed(2))
})

const formRules = {
  quotationName: [{ required: true, message: '请输入报价单名称', trigger: 'blur' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  quotationDate: [{ required: true, message: '请选择报价日期', trigger: 'change' }],
  validDate: [{ required: true, message: '请选择报价有效期', trigger: 'change' }],
  discountRate: [{ required: true, message: '请输入折扣率', trigger: 'blur' }],
  entrustType: [{ required: true, message: '请选择委托类型', trigger: 'change' }],
  sampleName: [{ required: true, message: '请输入样品名称', trigger: 'blur' }],
  sampleType: [{ required: true, message: '请选择样品类型', trigger: 'change' }],
  sampleQuantity: [{ required: true, message: '请输入样品数量', trigger: 'blur' }],
  expectedReportTime: [{ required: true, message: '请选择期望报告时间', trigger: 'change' }],
  isUrgent: [{ required: true, message: '请选择是否加急', trigger: 'change' }],
  isSubcontract: [{ required: true, message: '请选择是否分包', trigger: 'change' }]
}

const approvalFormRules = {
  approvalResult: [{ required: true, message: '请选择审批结果', trigger: 'change' }],
  approvalRemark: [{ required: true, message: '请输入审批意见', trigger: 'blur' }]
}

const convertFormRules = {
  entrustType: [{ required: true, message: '请选择委托类型', trigger: 'change' }],
  sampleName: [{ required: true, message: '请输入样品名称', trigger: 'blur' }],
  sampleType: [{ required: true, message: '请选择样品类型', trigger: 'change' }],
  sampleQuantity: [{ required: true, message: '请输入样品数量', trigger: 'blur' }],
  expectedReportTime: [{ required: true, message: '请选择期望报告时间', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, fixed: 'left', customRender: ({ index }: { index: number }) => index + 1 },
  { title: '报价单号', dataIndex: 'quotationNo', key: 'quotationNo', width: 150, fixed: 'left' },
  { title: '报价单名称', dataIndex: 'quotationName', key: 'quotationName', width: 180, ellipsis: true },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 160, ellipsis: true },
  { title: '总金额', dataIndex: 'totalAmount', key: 'totalAmount', width: 120 },
  { title: '折扣率', dataIndex: 'discountRate', key: 'discountRate', width: 100, customRender: ({ text }: { text: number }) => `${text || 0}%` },
  { title: '实际金额', dataIndex: 'actualAmount', key: 'actualAmount', width: 120 },
  { title: '报价日期', dataIndex: 'quotationDate', key: 'quotationDate', width: 120 },
  { title: '有效期', dataIndex: 'validDate', key: 'validDate', width: 120 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 110 },
  { title: '审批状态', dataIndex: 'approvalStatus', key: 'approvalStatus', width: 100 },
  { title: '已转委托', dataIndex: 'isConverted', key: 'isConverted', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 350, fixed: 'right' }
]

const itemColumns = [
  { title: '检测项目', dataIndex: 'itemId', key: 'itemId', width: 220 },
  { title: '检测标准', dataIndex: 'standardId', key: 'standardId', width: 240 },
  { title: '单价（元）', dataIndex: 'unitPrice', key: 'unitPrice', width: 120 },
  { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 100 },
  { title: '小计（元）', dataIndex: 'subtotal', key: 'subtotal', width: 120 },
  { title: '操作', key: 'action', width: 80 }
]

const detailItemColumns = [
  { title: '项目编码', dataIndex: 'itemCode', key: 'itemCode', width: 120 },
  { title: '项目名称', dataIndex: 'itemName', key: 'itemName', width: 180 },
  { title: '标准编号', dataIndex: 'standardNo', key: 'standardNo', width: 150 },
  { title: '标准名称', dataIndex: 'standardName', key: 'standardName', width: 200 },
  { title: '单位', dataIndex: 'unit', key: 'unit', width: 80 },
  { title: '单价', dataIndex: 'unitPrice', key: 'unitPrice', width: 100 },
  { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 80 },
  { title: '小计', dataIndex: 'subtotal', key: 'subtotal', width: 100 }
]

const approvalColumns = [
  { title: '审批节点', dataIndex: 'approvalNode', key: 'approvalNode', width: 120 },
  { title: '审批人', dataIndex: 'approverName', key: 'approverName', width: 100 },
  { title: '审批结果', dataIndex: 'approvalResult', key: 'approvalResult', width: 100 },
  { title: '审批意见', dataIndex: 'approvalOpinion', key: 'approvalOpinion' },
  { title: '审批时间', dataIndex: 'approvalTime', key: 'approvalTime', width: 170 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getQuotationPage(queryParams)
    tableData.value = res.data.list
    pagination.value.total = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchCustomerList = async () => {
  try {
    const res = await getCustomerSelectList()
    customerList.value = res.data
  } catch (error) {
    console.error('Get customer list error:', error)
  }
}

const fetchContractList = async () => {
  try {
    const res = await getContractSelectList()
    contractList.value = res.data
  } catch (error) {
    console.error('Get contract list error:', error)
  }
}

const fetchTestItemList = async () => {
  try {
    const res = await getTestItemList()
    testItemList.value = res.data
  } catch (error) {
    console.error('Get test item list error:', error)
  }
}

const getStandardList = (itemId?: number) => {
  if (!itemId) return []
  const standards = standardMap.value.get(itemId)
  return standards || []
}

const loadStandards = async (itemId: number) => {
  if (!standardMap.value.has(itemId)) {
    try {
      const res = await getStandardByItemId(itemId)
      standardMap.value.set(itemId, res.data)
    } catch (error) {
      console.error('Get standards error:', error)
    }
  }
}

const handleQuotationDateChange = (dates: any[]) => {
  if (dates && dates.length === 2) {
    queryParams.quotationDateStart = dates[0]
    queryParams.quotationDateEnd = dates[1]
  } else {
    queryParams.quotationDateStart = ''
    queryParams.quotationDateEnd = ''
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    quotationNo: '',
    quotationName: '',
    customerName: '',
    status: undefined,
    isConverted: undefined,
    quotationDateStart: '',
    quotationDateEnd: ''
  })
  quotationDateRange.value = []
  handleQuery()
}

const handleTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

const handleCustomerChange = (customerId: number) => {
  const customer = customerList.value.find(item => item.id === customerId)
  if (customer) {
    formData.customerName = customer.customerName
  }
}

const handleConvertContractChange = (contractId: number) => {
  const contract = contractList.value.find(item => item.id === contractId)
  if (contract) {
    convertFormData.contractNo = contract.contractNo
  } else {
    convertFormData.contractNo = ''
  }
}

const handleItemChange = async (index: number, itemId: number) => {
  await loadStandards(itemId)
  const item = formData.items[index]
  const testItem = testItemList.value.find(t => t.id === itemId)
  if (testItem) {
    item.itemCode = testItem.itemCode
    item.itemName = testItem.itemName
    item.unit = testItem.unit
    item.unitPrice = testItem.standardPrice
  }
  item.standardId = undefined as any
  item.standardNo = ''
  item.standardName = ''
  calculateItemAmount(index)
}

const handleStandardChange = (index: number, standardId: number) => {
  const item = formData.items[index]
  const standards = getStandardList(item.itemId)
  const standard = standards.find(s => s.id === standardId)
  if (standard) {
    item.standardNo = standard.standardNo
    item.standardName = standard.standardName
  }
}

const addItem = () => {
  const newItem: QuotationItemSaveDTO & { key: string } = {
    key: Date.now().toString(),
    itemId: undefined as any,
    itemCode: '',
    itemName: '',
    standardId: undefined as any,
    standardNo: '',
    standardName: '',
    unit: '',
    unitPrice: 0,
    quantity: 1,
    subtotal: 0,
    sortOrder: formData.items.length
  }
  formData.items.push(newItem)
}

const removeItem = (index: number) => {
  formData.items.splice(index, 1)
  calculateAmount()
}

const calculateItemAmount = (index: number) => {
  const item = formData.items[index]
  item.subtotal = Number(((item.unitPrice || 0) * (item.quantity || 0)).toFixed(2))
  calculateAmount()
}

const calculateAmount = () => {
}

const handleAdd = () => {
  isEdit.value = false
  formModalTitle.value = '新增报价单'
  Object.assign(formData, {
    id: undefined,
    quotationName: '',
    customerId: 0,
    customerName: '',
    contractId: undefined,
    validDate: '',
    quotationDate: dayjs().format('YYYY-MM-DD'),
    discountRate: 0,
    quotationContent: '',
    remark: '',
    items: []
  })
  addItem()
  formModalVisible.value = true
}

const handleEdit = async (record: QuotationVO) => {
  try {
    const res = await getQuotationById(record.id)
    const detail = res.data
    isEdit.value = true
    formModalTitle.value = '编辑报价单'
    Object.assign(formData, {
      id: detail.id,
      quotationName: detail.quotationName,
      customerId: detail.customerId,
      customerName: detail.customerName,
      contractId: detail.contractId,
      validDate: detail.validDate,
      quotationDate: detail.quotationDate,
      discountRate: detail.discountRate,
      quotationContent: detail.quotationContent,
      remark: detail.remark,
      items: (detail.items || []).map((item, index) => ({
        ...item,
        key: `${item.id || Date.now()}_${index}`
      }))
    })
    for (const item of formData.items) {
      if (item.itemId) {
        await loadStandards(item.itemId)
      }
    }
    formModalVisible.value = true
  } catch (error) {
    console.error('Get quotation detail error:', error)
  }
}

const handleView = async (record: QuotationVO) => {
  try {
    const res = await getQuotationById(record.id)
    quotationDetail.value = res.data
    detailTab.value = 'basic'
    currentQuotationId.value = record.id
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get quotation detail error:', error)
  }
}

const handleFormSubmit = async () => {
  try {
    await formRef.value.validate()
    if (formData.items.length === 0) {
      message.warning('请至少添加一个检测项目')
      return
    }
    for (let i = 0; i < formData.items.length; i++) {
      const item = formData.items[i]
      if (!item.itemId || !item.standardId) {
        message.warning(`第${i + 1}行请选择检测项目和检测标准`)
        return
      }
    }
    if (formData.validDate && formData.quotationDate && dayjs(formData.validDate).isBefore(formData.quotationDate)) {
      message.error('报价有效期不能早于报价日期')
      return
    }
    submitting.value = true
    const submitData = {
      ...formData,
      items: formData.items.map(({ key, ...rest }) => rest)
    }
    if (isEdit.value) {
      await updateQuotation(submitData)
      message.success('更新成功')
    } else {
      await addQuotation(submitData)
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
    await deleteQuotation(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const handleSubmitApproval = async (record: QuotationVO) => {
  try {
    await submitQuotationApproval(record.id)
    message.success('提交审批成功')
    fetchData()
  } catch (error) {
    console.error('Submit approval error:', error)
  }
}

const handleApproval = (record: QuotationVO) => {
  currentQuotationId.value = record.id
  currentQuotation.value = record
  Object.assign(approvalFormData, {
    id: record.id,
    approvalResult: 1,
    approvalRemark: ''
  })
  approvalModalVisible.value = true
}

const handleApprovalSubmit = async () => {
  try {
    await approvalFormRef.value.validate()
    submitting.value = true
    await approveQuotation(approvalFormData)
    message.success('审批成功')
    approvalModalVisible.value = false
    fetchData()
    if (currentQuotationId.value) {
      const res = await getQuotationById(currentQuotationId.value)
      quotationDetail.value = res.data
    }
  } catch (error) {
    console.error('Approval error:', error)
  } finally {
    submitting.value = false
  }
}

const handleCustomerConfirm = (record: QuotationVO) => {
  currentQuotationId.value = record.id
  currentQuotation.value = record
  confirmResult.value = 1
  confirmPerson.value = ''
  confirmModalVisible.value = true
}

const handleCustomerConfirmSubmit = async () => {
  if (!confirmPerson.value.trim()) {
    message.warning('请输入客户确认人')
    return
  }
  try {
    if (confirmResult.value === 1) {
      await customerConfirmQuotation(currentQuotationId.value!, confirmPerson.value)
      message.success('客户已确认')
    } else {
      message.info('客户已拒绝功能待实现')
    }
    confirmModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Customer confirm error:', error)
  }
}

const handleConvert = (record: QuotationVO) => {
  currentQuotationId.value = record.id
  currentQuotation.value = record
  Object.assign(convertFormData, {
    quotationId: record.id,
    entrustType: 1,
    sampleName: '',
    sampleType: '',
    sampleQuantity: 1,
    samplingAddress: '',
    samplingLongitude: undefined,
    samplingLatitude: undefined,
    samplingTime: '',
    sampleSendTime: '',
    sampleReceiveTime: '',
    expectedReportTime: '',
    detectionBasis: '',
    evaluationBasis: '',
    contractId: undefined,
    contractNo: '',
    discountRate: 0,
    isUrgent: 0,
    isSubcontract: 0,
    remark: ''
  })
  convertModalVisible.value = true
}

const handleConvertSubmit = async () => {
  try {
    await convertFormRef.value.validate()
    submitting.value = true
    const result = await convertToEntrust(convertFormData)
    message.success(`转委托单成功，委托单ID：${result.data}`)
    convertModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Convert error:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = async (record: QuotationVO) => {
  try {
    await cancelQuotation(record.id)
    message.success('报价单已作废')
    fetchData()
  } catch (error) {
    console.error('Cancel error:', error)
  }
}

const handlePrint = async (record: QuotationVO) => {
  try {
    const res = await getQuotationPrint(record.id)
    printData.value = res.data
    printModalVisible.value = true
  } catch (error) {
    console.error('Get print data error:', error)
  }
}

const doPrint = () => {
  window.print()
}

const formatMoney = (amount?: number) => {
  return amount?.toFixed(2) || '0.00'
}

const getStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'default',
    1: 'orange',
    2: 'green',
    3: 'red',
    4: 'cyan',
    5: 'blue',
    6: 'red',
    7: 'default'
  }
  return colors[status || 0] || 'default'
}

const getApprovalStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 0: 'default', 1: 'orange', 2: 'green', 3: 'red' }
  return colors[status || 0] || 'default'
}

onMounted(() => {
  fetchData()
  fetchCustomerList()
  fetchContractList()
  fetchTestItemList()
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

.items-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 12px;
}

.amount-info {
  display: flex;
  gap: 20px;
  align-items: center;
  font-size: 14px;
}

.amount-text {
  font-weight: bold;
  color: #333;
}

.amount-text.total {
  color: #f5222d;
  font-size: 16px;
}

.total-row {
  display: flex;
  justify-content: flex-end;
  gap: 30px;
  margin-top: 16px;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 4px;
  font-size: 14px;
}

.total-amount {
  color: #f5222d;
  font-weight: bold;
  font-size: 16px;
}

.print-container {
  padding: 20px;
  background: #fff;
}

.print-header {
  text-align: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #333;
  padding-bottom: 15px;
}

.print-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.print-no {
  margin: 0;
  color: #666;
}

.print-info {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
}

.info-row .label {
  width: 100px;
  color: #666;
}

.info-row .value {
  flex: 1;
}

.print-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.print-table th,
.print-table td {
  border: 1px solid #ddd;
  padding: 8px 10px;
  text-align: left;
}

.print-table th {
  background: #f5f5f5;
  font-weight: bold;
}

.text-right {
  text-align: right !important;
}

.text-center {
  text-align: center !important;
}

.print-summary {
  margin-bottom: 20px;
  padding: 15px;
  background: #fafafa;
  border-radius: 4px;
}

.summary-row {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-bottom: 8px;
}

.summary-row span:first-child {
  width: 100px;
  color: #666;
}

.summary-row.total {
  font-weight: bold;
  color: #f5222d;
  font-size: 16px;
  border-top: 1px dashed #ddd;
  padding-top: 8px;
  margin-top: 8px;
}

.summary-row.total-cn {
  color: #f5222d;
}

.print-footer {
  border-top: 1px dashed #ddd;
  padding-top: 15px;
  color: #666;
}

.print-footer p {
  margin: 5px 0;
}

.print-actions {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}

@media print {
  .print-actions,
  .ant-modal-header,
  .ant-modal-footer {
    display: none !important;
  }
}
</style>
