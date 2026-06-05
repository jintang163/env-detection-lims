<template>
  <div class="entrust">
    <a-card>
      <div class="toolbar">
        <a-form layout="inline" :model="queryParams">
          <a-form-item label="委托单号">
            <a-input v-model:value="queryParams.entrustNo" placeholder="请输入委托单号" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="客户名称">
            <a-input v-model:value="queryParams.customerName" placeholder="请输入客户名称" style="width: 160px" allow-clear />
          </a-form-item>
          <a-form-item label="委托类型">
            <a-select v-model:value="queryParams.entrustType" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="1">常规检测</a-select-option>
              <a-select-option :value="2">仲裁检测</a-select-option>
              <a-select-option :value="3">委托检测</a-select-option>
              <a-select-option :value="4">监督检测</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.status" placeholder="请选择" style="width: 120px" allow-clear>
              <a-select-option :value="0">草稿</a-select-option>
              <a-select-option :value="1">待受理</a-select-option>
              <a-select-option :value="2">待评审</a-select-option>
              <a-select-option :value="3">已受理</a-select-option>
              <a-select-option :value="4">采样中</a-select-option>
              <a-select-option :value="5">检测中</a-select-option>
              <a-select-option :value="6">报告编制</a-select-option>
              <a-select-option :value="7">报告审核</a-select-option>
              <a-select-option :value="8">已完成</a-select-option>
              <a-select-option :value="9">已驳回</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="是否加急">
            <a-select v-model:value="queryParams.isUrgent" placeholder="请选择" style="width: 100px" allow-clear>
              <a-select-option :value="0">否</a-select-option>
              <a-select-option :value="1">是</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="是否分包">
            <a-select v-model:value="queryParams.isSubcontract" placeholder="请选择" style="width: 100px" allow-clear>
              <a-select-option :value="0">否</a-select-option>
              <a-select-option :value="1">是</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="审批状态">
            <a-select v-model:value="queryParams.approvalStatus" placeholder="请选择" style="width: 100px" allow-clear>
              <a-select-option :value="0">待审批</a-select-option>
              <a-select-option :value="1">审批中</a-select-option>
              <a-select-option :value="2">审批通过</a-select-option>
              <a-select-option :value="3">审批驳回</a-select-option>
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
            <PlusOutlined /> 新增委托单
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
          <template v-if="column.key === 'entrustType'">
            <a-tag :color="getEntrustTypeColor(record.entrustType)">
              {{ record.entrustTypeName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.statusName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'isUrgent'">
            <a-tag :color="record.isUrgent === 1 ? 'red' : 'default'">
              {{ record.isUrgent === 1 ? '加急' : '正常' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'isSubcontract'">
            <a-tag :color="record.isSubcontract === 1 ? 'orange' : 'default'">
              {{ record.isSubcontract === 1 ? '是' : '否' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'approvalStatus'">
            <a-tag :color="getApprovalStatusColor(record.approvalStatus)">
              {{ record.approvalStatusName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'actualAmount'">
            ¥{{ formatMoney(record.actualAmount) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleView(record)">查看</a-button>
            <template v-if="record.status === 0">
              <a-button type="link" size="small" @click="handleEdit(record)">编辑</a-button>
              <a-button type="link" size="small" @click="handleSubmit(record)">提交受理</a-button>
              <a-popconfirm title="确定删除该委托单吗？" @confirm="handleDelete(record.id)">
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </template>
            <template v-else-if="record.status === 2">
              <a-button type="link" size="small" @click="handleReview(record, 1)">评审通过</a-button>
              <a-button type="link" danger size="small" @click="handleReview(record, 2)">评审驳回</a-button>
            </template>
            <template v-else-if="record.status >= 3 && record.status < 8">
              <a-dropdown>
                <template #overlay>
                  <a-menu @click="({ key }) => handleStatusChange(record, key)">
                    <a-menu-item key="4" v-if="record.status === 3">采样中</a-menu-item>
                    <a-menu-item key="5" v-if="record.status === 4">检测中</a-menu-item>
                    <a-menu-item key="6" v-if="record.status === 5">报告编制</a-menu-item>
                    <a-menu-item key="7" v-if="record.status === 6">报告审核</a-menu-item>
                    <a-menu-item key="8" v-if="record.status === 7">已完成</a-menu-item>
                  </a-menu>
                </template>
                <a-button type="link" size="small">
                  状态流转 <DownOutlined />
                </a-button>
              </a-dropdown>
              <a-button type="link" size="small" @click="handleUrgent(record)" v-if="record.isUrgent === 0">加急处理</a-button>
              <a-button type="link" size="small" @click="handleAdjust(record)">调账处理</a-button>
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
          <a-col :span="8">
            <a-form-item label="委托类型" name="entrustType">
              <a-select v-model:value="formData.entrustType" placeholder="请选择委托类型">
                <a-select-option :value="1">常规检测</a-select-option>
                <a-select-option :value="2">仲裁检测</a-select-option>
                <a-select-option :value="3">委托检测</a-select-option>
                <a-select-option :value="4">监督检测</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="16">
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
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="关联合同">
              <a-select
                v-model:value="formData.contractId"
                placeholder="请选择合同（可选）"
                allow-clear
                show-search
                :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
                @change="handleContractChange"
              >
                <a-select-option v-for="item in contractList" :key="item.id" :value="item.id">
                  {{ item.contractNo }} - {{ item.contractName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="合同编号">
              <a-input v-model:value="formData.contractNo" placeholder="自动填充" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="样品名称" name="sampleName">
              <a-input v-model:value="formData.sampleName" placeholder="请输入样品名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="样品类型" name="sampleType">
              <a-select v-model:value="formData.sampleType" placeholder="请选择样品类型" show-search allow-clear>
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
              <a-input-number v-model:value="formData.sampleQuantity" :min="1" style="width: 100%" placeholder="请输入样品数量" />
            </a-form-item>
          </a-col>
          <a-col :span="16">
            <a-form-item label="采样地址">
              <a-input v-model:value="formData.samplingAddress" placeholder="请输入采样地址" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="采样经度">
              <a-input-number v-model:value="formData.samplingLongitude" :precision="6" style="width: 100%" placeholder="经度" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="采样纬度">
              <a-input-number v-model:value="formData.samplingLatitude" :precision="6" style="width: 100%" placeholder="纬度" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="采样时间">
              <a-date-picker v-model:value="formData.samplingTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="送样时间">
              <a-date-picker v-model:value="formData.sampleSendTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="收样时间">
              <a-date-picker v-model:value="formData.sampleReceiveTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="期望报告时间" name="expectedReportTime">
              <a-date-picker v-model:value="formData.expectedReportTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="检测依据">
              <a-textarea v-model:value="formData.detectionBasis" :rows="2" placeholder="请输入检测依据" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="评价依据">
              <a-textarea v-model:value="formData.evaluationBasis" :rows="2" placeholder="请输入评价依据" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="折扣率（%）">
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
          <a-col :span="8">
            <a-form-item label="是否加急" name="isUrgent">
              <a-select v-model:value="formData.isUrgent" @change="calculateAmount">
                <a-select-option :value="0">否</a-select-option>
                <a-select-option :value="1">是</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="加急费（元）">
              <a-input-number
                v-model:value="formData.urgentFee"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="请输入加急费"
                @change="calculateAmount"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="是否分包" name="isSubcontract">
              <a-select v-model:value="formData.isSubcontract">
                <a-select-option :value="0">否</a-select-option>
                <a-select-option :value="1">是</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="分包金额（元）">
              <a-input-number
                v-model:value="formData.subcontractAmount"
                :min="0"
                :precision="2"
                style="width: 100%"
                placeholder="请输入分包金额"
                @change="calculateAmount"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-divider orientation="left">检测项目</a-divider>
        <div class="items-toolbar">
          <a-button type="primary" size="small" @click="addItem">
            <PlusOutlined /> 添加检测项目
          </a-button>
          <span class="amount-info">
            总金额：<span class="amount-text">¥{{ formatMoney(totalAmount) }}</span>
            折扣金额：<span class="amount-text discount">-¥{{ formatMoney(discountAmount) }}</span>
            加急费：<span class="amount-text urgent">+¥{{ formatMoney(formData.urgentFee || 0) }}</span>
            实际金额：<span class="amount-text total">¥{{ formatMoney(actualAmount) }}</span>
          </span>
        </div>
        <a-table
          :columns="itemColumns"
          :data-source="formData.items"
          :pagination="false"
          row-key="key"
          size="small"
          :scroll="{ x: 1100 }"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'itemId'">
              <a-select
                v-model:value="record.itemId"
                placeholder="请选择检测项目"
                show-search
                style="width: 180px"
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
                style="width: 200px"
                :disabled="!record.itemId"
                :filter-option="(input: string, option: any) => option.children.toLowerCase().includes(input.toLowerCase())"
                @change="(val: number) => handleStandardChange(index, val)"
              >
                <a-select-option v-for="item in getStandardList(record.itemId)" :key="item.id" :value="item.id">
                  {{ item.standardNo }} - {{ item.standardName }}
                </a-select-option>
              </a-select>
            </template>
            <template v-else-if="column.key === 'limitValue'">
              <a-input v-model:value="record.limitValue" placeholder="限值要求" style="width: 120px" />
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
            <template v-else-if="column.key === 'isSubcontract'">
              <a-switch v-model:checked="record.isSubcontract" :checked-value="1" :un-checked-value="0" />
            </template>
            <template v-else-if="column.key === 'subcontractor'">
              <a-input v-model:value="record.subcontractor" placeholder="分包单位" style="width: 120px" />
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
      title="委托单详情"
      width="1100px"
      :footer="null"
      :destroy-on-close="true"
    >
      <a-tabs v-model:activeKey="detailTab">
        <a-tab-pane key="basic" tab="基本信息">
          <a-descriptions :column="2" bordered size="small">
            <a-descriptions-item label="委托单号">{{ entrustDetail?.entrustNo }}</a-descriptions-item>
            <a-descriptions-item label="委托类型">
              <a-tag :color="getEntrustTypeColor(entrustDetail?.entrustType)">
                {{ entrustDetail?.entrustTypeName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="客户名称">{{ entrustDetail?.customerName }}</a-descriptions-item>
            <a-descriptions-item label="关联合同">{{ entrustDetail?.contractNo || '-' }}</a-descriptions-item>
            <a-descriptions-item label="样品名称">{{ entrustDetail?.sampleName || '-' }}</a-descriptions-item>
            <a-descriptions-item label="样品类型">{{ entrustDetail?.sampleType || '-' }}</a-descriptions-item>
            <a-descriptions-item label="样品数量">{{ entrustDetail?.sampleQuantity || '-' }}</a-descriptions-item>
            <a-descriptions-item label="采样地址">{{ entrustDetail?.samplingAddress || '-' }}</a-descriptions-item>
            <a-descriptions-item label="采样经纬度">
              {{ entrustDetail?.samplingLongitude || '-' }}, {{ entrustDetail?.samplingLatitude || '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="采样时间">{{ entrustDetail?.samplingTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="送样时间">{{ entrustDetail?.sampleSendTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="收样时间">{{ entrustDetail?.sampleReceiveTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="期望报告时间">{{ entrustDetail?.expectedReportTime || '-' }}</a-descriptions-item>
            <a-descriptions-item label="检测依据">{{ entrustDetail?.detectionBasis || '-' }}</a-descriptions-item>
            <a-descriptions-item label="评价依据">{{ entrustDetail?.evaluationBasis || '-' }}</a-descriptions-item>
            <a-descriptions-item label="折扣率">{{ entrustDetail?.discountRate ?? '-' }}%</a-descriptions-item>
            <a-descriptions-item label="总金额">¥{{ formatMoney(entrustDetail?.totalAmount || 0) }}</a-descriptions-item>
            <a-descriptions-item label="折扣金额">¥{{ formatMoney(entrustDetail?.discountAmount || 0) }}</a-descriptions-item>
            <a-descriptions-item label="加急费">¥{{ formatMoney(entrustDetail?.isUrgent === 1 ? (entrustDetail?.actualAmount || 0) * 0.2 : 0) }}</a-descriptions-item>
            <a-descriptions-item label="实际金额">¥{{ formatMoney(entrustDetail?.actualAmount || 0) }}</a-descriptions-item>
            <a-descriptions-item label="是否加急">
              <a-tag :color="entrustDetail?.isUrgent === 1 ? 'red' : 'default'">
                {{ entrustDetail?.isUrgent === 1 ? '是' : '否' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="是否分包">
              <a-tag :color="entrustDetail?.isSubcontract === 1 ? 'orange' : 'default'">
                {{ entrustDetail?.isSubcontract === 1 ? '是' : '否' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="状态">
              <a-tag :color="getStatusColor(entrustDetail?.status)">
                {{ entrustDetail?.statusName }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="审批状态">
              <a-tag :color="getApprovalStatusColor(entrustDetail?.approvalStatus)">
                {{ entrustDetail?.approvalStatusName || '-' }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="备注" :span="2">{{ entrustDetail?.remark || '-' }}</a-descriptions-item>
          </a-descriptions>
        </a-tab-pane>

        <a-tab-pane key="items" tab="检测项目">
          <a-table
            :columns="detailItemColumns"
            :data-source="entrustDetail?.items || []"
            :pagination="false"
            row-key="id"
            size="small"
            :scroll="{ x: 1000 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'subtotal'">
                ¥{{ formatMoney(record.subtotal) }}
              </template>
              <template v-else-if="column.key === 'unitPrice'">
                ¥{{ formatMoney(record.unitPrice) }}
              </template>
              <template v-else-if="column.key === 'isSubcontract'">
                {{ record.isSubcontract === 1 ? '是' : '否' }}
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="statusLogs" tab="状态流转日志">
          <a-timeline>
            <a-timeline-item v-for="log in entrustDetail?.statusLogs || []" :key="log.id">
              <template #dot>
                <a-badge status="processing" />
              </template>
              <a-card size="small" style="margin-bottom: 8px">
                <div class="log-header">
                  <a-tag :color="getStatusColor(log.afterStatus)">
                    {{ log.beforeStatusName }} → {{ log.afterStatusName }}
                  </a-tag>
                  <span class="log-time">{{ log.operateTime }}</span>
                  <span class="log-operator">操作人：{{ log.operatorName || '-' }}</span>
                </div>
                <div v-if="log.operateType" class="log-type">操作类型：{{ log.operateType }}</div>
                <div v-if="log.operateContent" class="log-content">{{ log.operateContent }}</div>
              </a-card>
            </a-timeline-item>
          </a-timeline>
        </a-tab-pane>

        <a-tab-pane key="reviewLogs" tab="评审记录">
          <a-table
            :columns="reviewColumns"
            :data-source="entrustDetail?.reviewLogs || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'reviewResult'">
                <a-tag :color="record.reviewResult === 1 ? 'green' : 'red'">
                  {{ record.reviewResultName }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="subcontracts" tab="分包信息">
          <a-table
            :columns="subcontractColumns"
            :data-source="entrustDetail?.subcontracts || []"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 1 ? 'blue' : record.status === 2 ? 'green' : 'default'">
                  {{ record.statusName }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'subcontractAmount'">
                ¥{{ formatMoney(record.subcontractAmount) }}
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <a-modal
      v-model:open="reviewModalVisible"
      :title="reviewModalTitle"
      width="500px"
      @ok="handleReviewSubmit"
      @cancel="reviewModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-descriptions :column="1" bordered size="small" style="margin-bottom: 16px">
        <a-descriptions-item label="委托单号">{{ currentEntrust?.entrustNo }}</a-descriptions-item>
        <a-descriptions-item label="客户名称">{{ currentEntrust?.customerName }}</a-descriptions-item>
      </a-descriptions>
      <a-form layout="vertical">
        <a-form-item label="评审意见">
          <a-textarea v-model:value="reviewOpinion" :rows="3" placeholder="请输入评审意见" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="adjustModalVisible"
      title="调账处理"
      width="500px"
      @ok="handleAdjustSubmit"
      @cancel="adjustModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-descriptions :column="1" bordered size="small" style="margin-bottom: 16px">
        <a-descriptions-item label="委托单号">{{ currentEntrust?.entrustNo }}</a-descriptions-item>
        <a-descriptions-item label="当前金额">¥{{ formatMoney(currentEntrust?.actualAmount || 0) }}</a-descriptions-item>
      </a-descriptions>
      <a-form layout="vertical">
        <a-form-item label="调整金额（元）">
          <a-input-number v-model:value="adjustAmount" :min="0" :precision="2" style="width: 100%" placeholder="请输入调整金额" />
        </a-form-item>
        <a-form-item label="调整原因">
          <a-textarea v-model:value="adjustReason" :rows="3" placeholder="请输入调整原因" />
        </a-form-item>
      </a-form>
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
  DownOutlined
} from '@ant-design/icons-vue'
import type {
  EntrustQuery,
  EntrustSaveDTO,
  EntrustItemSaveDTO,
  EntrustVO,
  EntrustDetailVO,
  EntrustReviewDTO,
  EntrustStatusChangeDTO,
  AdjustDTO,
  DictTestItem,
  DictTestStandard
} from '@/types'
import {
  getEntrustPage,
  getEntrustById,
  addEntrust,
  updateEntrust,
  deleteEntrust,
  submitEntrust,
  reviewEntrust,
  changeEntrustStatus,
  urgentEntrust,
  adjustEntrust
} from '@/api/entrust'
import { getCustomerSelectList } from '@/api/customer'
import { getContractSelectList } from '@/api/contract'
import { getTestItemList, getStandardByItemId } from '@/api/dict'

const loading = ref(false)
const submitting = ref(false)
const detailTab = ref('basic')
const reviewResult = ref(1)
const reviewModalTitle = ref('')

const formModalVisible = ref(false)
const formModalTitle = ref('')
const detailModalVisible = ref(false)
const reviewModalVisible = ref(false)
const adjustModalVisible = ref(false)

const formRef = ref()
const isEdit = ref(false)
const currentEntrustId = ref<number>()
const currentEntrust = ref<EntrustVO | null>(null)
const reviewOpinion = ref('')
const adjustAmount = ref(0)
const adjustReason = ref('')

const customerList = ref<any[]>([])
const contractList = ref<any[]>([])
const testItemList = ref<DictTestItem[]>([])
const standardMap = ref<Map<number, DictTestStandard[]>>(new Map())

const queryParams = reactive<EntrustQuery>({
  pageNum: 1,
  pageSize: 10,
  entrustNo: '',
  customerName: '',
  entrustType: undefined,
  status: undefined,
  isUrgent: undefined,
  isSubcontract: undefined,
  approvalStatus: undefined
})

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const tableData = ref<EntrustVO[]>([])
const entrustDetail = ref<EntrustDetailVO | null>(null)

const formData = reactive<EntrustSaveDTO>({
  customerId: 0,
  customerName: '',
  items: []
})

const totalAmount = computed(() => {
  return formData.items.reduce((sum, item) => sum + (item.subtotal || 0), 0)
})

const discountAmount = computed(() => {
  const rate = (formData.discountRate || 0) / 100
  return Number((totalAmount.value * rate).toFixed(2))
})

const actualAmount = computed(() => {
  return Number((totalAmount.value - discountAmount.value + (formData.urgentFee || 0) + (formData.subcontractAmount || 0)).toFixed(2))
})

const formRules = {
  entrustType: [{ required: true, message: '请选择委托类型', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  sampleName: [{ required: true, message: '请输入样品名称', trigger: 'blur' }],
  sampleType: [{ required: true, message: '请选择样品类型', trigger: 'change' }],
  sampleQuantity: [{ required: true, message: '请输入样品数量', trigger: 'blur' }],
  expectedReportTime: [{ required: true, message: '请选择期望报告时间', trigger: 'change' }],
  isUrgent: [{ required: true, message: '请选择是否加急', trigger: 'change' }],
  isSubcontract: [{ required: true, message: '请选择是否分包', trigger: 'change' }]
}

const columns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, fixed: 'left', customRender: ({ index }: { index: number }) => index + 1 },
  { title: '委托单号', dataIndex: 'entrustNo', key: 'entrustNo', width: 150, fixed: 'left' },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 160, ellipsis: true },
  { title: '委托类型', dataIndex: 'entrustType', key: 'entrustType', width: 100 },
  { title: '样品类型', dataIndex: 'sampleType', key: 'sampleType', width: 100 },
  { title: '项目数', dataIndex: 'itemCount', key: 'itemCount', width: 80 },
  { title: '实际金额', dataIndex: 'actualAmount', key: 'actualAmount', width: 120 },
  { title: '是否加急', dataIndex: 'isUrgent', key: 'isUrgent', width: 90 },
  { title: '是否分包', dataIndex: 'isSubcontract', key: 'isSubcontract', width: 90 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '审批状态', dataIndex: 'approvalStatus', key: 'approvalStatus', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 300, fixed: 'right' }
]

const itemColumns = [
  { title: '检测项目', dataIndex: 'itemId', key: 'itemId', width: 200 },
  { title: '检测标准', dataIndex: 'standardId', key: 'standardId', width: 220 },
  { title: '限值要求', dataIndex: 'limitValue', key: 'limitValue', width: 130 },
  { title: '单价（元）', dataIndex: 'unitPrice', key: 'unitPrice', width: 110 },
  { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 90 },
  { title: '小计（元）', dataIndex: 'subtotal', key: 'subtotal', width: 110 },
  { title: '是否分包', dataIndex: 'isSubcontract', key: 'isSubcontract', width: 100 },
  { title: '分包单位', dataIndex: 'subcontractor', key: 'subcontractor', width: 130 },
  { title: '操作', key: 'action', width: 80 }
]

const detailItemColumns = [
  { title: '项目编码', dataIndex: 'itemCode', key: 'itemCode', width: 120 },
  { title: '项目名称', dataIndex: 'itemName', key: 'itemName', width: 150 },
  { title: '标准编号', dataIndex: 'standardNo', key: 'standardNo', width: 150 },
  { title: '标准名称', dataIndex: 'standardName', key: 'standardName', width: 180 },
  { title: '单位', dataIndex: 'unit', key: 'unit', width: 80 },
  { title: '限值要求', dataIndex: 'limitValue', key: 'limitValue', width: 120 },
  { title: '单价', dataIndex: 'unitPrice', key: 'unitPrice', width: 100 },
  { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 80 },
  { title: '小计', dataIndex: 'subtotal', key: 'subtotal', width: 100 },
  { title: '是否分包', dataIndex: 'isSubcontract', key: 'isSubcontract', width: 90 },
  { title: '分包单位', dataIndex: 'subcontractor', key: 'subcontractor', width: 120 }
]

const reviewColumns = [
  { title: '评审结果', dataIndex: 'reviewResult', key: 'reviewResult', width: 100 },
  { title: '评审意见', dataIndex: 'reviewOpinion', key: 'reviewOpinion' },
  { title: '评审节点', dataIndex: 'reviewNode', key: 'reviewNode', width: 120 },
  { title: '评审人', dataIndex: 'reviewerName', key: 'reviewerName', width: 100 },
  { title: '评审时间', dataIndex: 'reviewTime', key: 'reviewTime', width: 170 }
]

const subcontractColumns = [
  { title: '分包单号', dataIndex: 'subcontractNo', key: 'subcontractNo', width: 150 },
  { title: '分包单位', dataIndex: 'subcontractorName', key: 'subcontractorName', width: 150 },
  { title: '分包项目', dataIndex: 'subcontractItems', key: 'subcontractItems' },
  { title: '分包金额', dataIndex: 'subcontractAmount', key: 'subcontractAmount', width: 120 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170 }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getEntrustPage(queryParams)
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

const handleQuery = () => {
  queryParams.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    entrustNo: '',
    customerName: '',
    entrustType: undefined,
    status: undefined,
    isUrgent: undefined,
    isSubcontract: undefined,
    approvalStatus: undefined
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

const handleCustomerChange = (customerId: number) => {
  const customer = customerList.value.find(item => item.id === customerId)
  if (customer) {
    formData.customerName = customer.customerName
  }
}

const handleContractChange = (contractId: number) => {
  const contract = contractList.value.find(item => item.id === contractId)
  if (contract) {
    formData.contractNo = contract.contractNo
  } else {
    formData.contractNo = ''
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
  item.limitValue = ''
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
  const newItem: EntrustItemSaveDTO & { key: string } = {
    key: Date.now().toString(),
    itemId: undefined as any,
    itemCode: '',
    itemName: '',
    standardId: undefined as any,
    standardNo: '',
    standardName: '',
    unit: '',
    limitValue: '',
    unitPrice: 0,
    quantity: 1,
    subtotal: 0,
    isSubcontract: 0,
    subcontractor: '',
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
  formModalTitle.value = '新增委托单'
  Object.assign(formData, {
    id: undefined,
    entrustType: undefined,
    customerId: 0,
    customerName: '',
    contractId: undefined,
    contractNo: '',
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
    discountRate: 0,
    isUrgent: 0,
    urgentFee: 0,
    isSubcontract: 0,
    subcontractAmount: 0,
    remark: '',
    items: []
  })
  addItem()
  formModalVisible.value = true
}

const handleEdit = async (record: EntrustVO) => {
  try {
    const res = await getEntrustById(record.id)
    const detail = res.data
    isEdit.value = true
    formModalTitle.value = '编辑委托单'
    Object.assign(formData, {
      id: detail.id,
      entrustType: detail.entrustType,
      customerId: detail.customerId,
      customerName: detail.customerName,
      contractId: detail.contractId,
      contractNo: detail.contractNo,
      sampleName: detail.sampleName,
      sampleType: detail.sampleType,
      sampleQuantity: detail.sampleQuantity,
      samplingAddress: detail.samplingAddress,
      samplingLongitude: detail.samplingLongitude,
      samplingLatitude: detail.samplingLatitude,
      samplingTime: detail.samplingTime,
      sampleSendTime: detail.sampleSendTime,
      sampleReceiveTime: detail.sampleReceiveTime,
      expectedReportTime: detail.expectedReportTime,
      detectionBasis: detail.detectionBasis,
      evaluationBasis: detail.evaluationBasis,
      discountRate: detail.discountRate,
      isUrgent: detail.isUrgent,
      urgentFee: detail.isUrgent === 1 ? (detail.actualAmount || 0) * 0.2 : 0,
      isSubcontract: detail.isSubcontract,
      subcontractAmount: detail.isSubcontract === 1 ? (detail.actualAmount || 0) * 0.3 : 0,
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
    console.error('Get entrust detail error:', error)
  }
}

const handleView = async (record: EntrustVO) => {
  try {
    const res = await getEntrustById(record.id)
    entrustDetail.value = res.data
    detailTab.value = 'basic'
    currentEntrustId.value = record.id
    detailModalVisible.value = true
  } catch (error) {
    console.error('Get entrust detail error:', error)
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
    submitting.value = true
    const submitData = {
      ...formData,
      items: formData.items.map(({ key, ...rest }) => rest)
    }
    if (isEdit.value) {
      await updateEntrust(submitData)
      message.success('更新成功')
    } else {
      await addEntrust(submitData)
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
    await deleteEntrust(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const handleSubmit = async (record: EntrustVO) => {
  try {
    await submitEntrust(record.id)
    message.success('提交受理成功')
    fetchData()
  } catch (error) {
    console.error('Submit error:', error)
  }
}

const handleReview = (record: EntrustVO, result: number) => {
  currentEntrustId.value = record.id
  currentEntrust.value = record
  reviewResult.value = result
  reviewOpinion.value = ''
  reviewModalTitle.value = result === 1 ? '评审通过' : '评审驳回'
  reviewModalVisible.value = true
}

const handleReviewSubmit = async () => {
  try {
    submitting.value = true
    const reviewData: EntrustReviewDTO = {
      entrustId: currentEntrustId.value!,
      reviewResult: reviewResult.value,
      reviewOpinion: reviewOpinion.value
    }
    await reviewEntrust(reviewData)
    message.success(reviewResult.value === 1 ? '评审通过' : '评审驳回成功')
    reviewModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Review error:', error)
  } finally {
    submitting.value = false
  }
}

const handleStatusChange = async (record: EntrustVO, targetStatus: string) => {
  try {
    const statusChangeData: EntrustStatusChangeDTO = {
      entrustId: record.id,
      targetStatus: parseInt(targetStatus)
    }
    await changeEntrustStatus(statusChangeData)
    message.success('状态变更成功')
    fetchData()
  } catch (error) {
    console.error('Status change error:', error)
  }
}

const handleUrgent = async (record: EntrustVO) => {
  try {
    await urgentEntrust(record.id)
    message.success('加急处理成功')
    fetchData()
  } catch (error) {
    console.error('Urgent error:', error)
  }
}

const handleAdjust = (record: EntrustVO) => {
  currentEntrustId.value = record.id
  currentEntrust.value = record
  adjustAmount.value = 0
  adjustReason.value = ''
  adjustModalVisible.value = true
}

const handleAdjustSubmit = async () => {
  if (adjustAmount.value <= 0) {
    message.warning('请输入调整金额')
    return
  }
  if (!adjustReason.value.trim()) {
    message.warning('请输入调整原因')
    return
  }
  try {
    submitting.value = true
    const adjustData: AdjustDTO = {
      entrustId: currentEntrustId.value!,
      adjustAmount: adjustAmount.value,
      adjustReason: adjustReason.value
    }
    await adjustEntrust(adjustData)
    message.success('调账成功')
    adjustModalVisible.value = false
    fetchData()
  } catch (error) {
    console.error('Adjust error:', error)
  } finally {
    submitting.value = false
  }
}

const formatMoney = (amount?: number) => {
  return amount?.toFixed(2) || '0.00'
}

const getEntrustTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'blue', 2: 'purple', 3: 'green', 4: 'orange' }
  return colors[type || 0] || 'default'
}

const getStatusColor = (status?: number) => {
  const colors: Record<number, string> = {
    0: 'default',
    1: 'orange',
    2: 'cyan',
    3: 'blue',
    4: 'purple',
    5: 'geekblue',
    6: 'gold',
    7: 'lime',
    8: 'green',
    9: 'red'
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
  gap: 16px;
  align-items: center;
  font-size: 14px;
}

.amount-text {
  font-weight: bold;
  color: #333;
}

.amount-text.discount {
  color: #52c41a;
}

.amount-text.urgent {
  color: #fa8c16;
}

.amount-text.total {
  color: #f5222d;
  font-size: 16px;
}

.log-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.log-time {
  color: #999;
  font-size: 12px;
}

.log-operator {
  color: #666;
  font-size: 12px;
  margin-left: auto;
}

.log-type {
  color: #666;
  font-size: 13px;
  margin-bottom: 4px;
}

.log-content {
  color: #333;
  line-height: 1.6;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>
