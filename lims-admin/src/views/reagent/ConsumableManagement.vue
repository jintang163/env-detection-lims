<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">耗材管理</div>
        <div class="page-desc">管理玻璃器皿、色谱柱、滤膜等耗材的采购、入库、领用、库存预警</div>
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
              <div class="stat-label">耗材总数</div>
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
              <div class="stat-label">库存充足</div>
              <div class="stat-value">{{ stats.valid || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">库存预警</div>
              <div class="stat-value">{{ stats.warning || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card purchase" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待采购</div>
              <div class="stat-value">{{ stats.purchase || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="耗材库存" name="stock">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索耗材名称、规格、型号..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="searchCategory" placeholder="耗材分类" clearable style="width: 140px">
              <el-option label="玻璃器皿" value="GLASSWARE" />
              <el-option label="色谱柱" value="CHROMATOGRAPHY" />
              <el-option label="滤膜/滤器" value="FILTER" />
              <el-option label="移液器/枪头" value="PIPETTE" />
              <el-option label="样品瓶/容器" value="CONTAINER" />
              <el-option label="其他耗材" value="OTHER" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增耗材
            </el-button>
            <el-button type="success" @click="handleStockIn">
              <el-icon><Download /></el-icon>
              入库
            </el-button>
            <el-button type="warning" @click="handleStockOut">
              <el-icon><Upload /></el-icon>
              领用
            </el-button>
            <el-button @click="fetchList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="tableData"
            v-loading="loading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="consumableNo" label="耗材编号" width="140" />
            <el-table-column prop="consumableName" label="耗材名称" width="180" />
            <el-table-column prop="category" label="分类" width="120">
              <template #default="{ row }">
                <el-tag :type="getCategoryTag(row.category)" effect="light" size="small">
                  {{ getCategoryText(row.category) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="specification" label="规格型号" width="160" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column prop="stockQuantity" label="库存数量" width="100">
              <template #default="{ row }">
                <span :class="{ 'text-warning': row.stockQuantity <= row.warningQuantity }">
                  {{ row.stockQuantity }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="warningQuantity" label="预警值" width="80" />
            <el-table-column prop="unitPrice" label="单价(元)" width="100">
              <template #default="{ row }">
                {{ row.unitPrice ? row.unitPrice.toFixed(2) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="manufacturer" label="生产厂家" width="160" show-overflow-tooltip />
            <el-table-column prop="supplier" label="供应商" width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                <el-button type="success" link @click="handleViewStockRecords(row)">出入库记录</el-button>
                <el-button type="info" link @click="handleQuickPurchase(row)" v-if="row.stockQuantity <= row.warningQuantity">申请采购</el-button>
                <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="pagination.pageNum"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchList"
            @current-change="fetchList"
          />
        </el-tab-pane>

        <el-tab-pane label="采购管理" name="purchase">
          <div class="toolbar">
            <el-input
              v-model="purchaseSearchKeyword"
              placeholder="搜索采购单..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchPurchaseList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="purchaseSearchStatus" placeholder="采购状态" clearable style="width: 140px">
              <el-option label="待审批" value="PENDING" />
              <el-option label="已批准" value="APPROVED" />
              <el-option label="采购中" value="PURCHASING" />
              <el-option label="已入库" value="RECEIVED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
            <el-button type="primary" @click="handleAddPurchase">
              <el-icon><Plus /></el-icon>
              新增采购单
            </el-button>
            <el-button @click="fetchPurchaseList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="purchaseTableData"
            v-loading="purchaseLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="purchaseNo" label="采购单号" width="160" />
            <el-table-column prop="consumableName" label="耗材名称" width="180" />
            <el-table-column prop="specification" label="规格型号" width="160" />
            <el-table-column prop="quantity" label="采购数量" width="100" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column prop="unitPrice" label="单价(元)" width="100">
              <template #default="{ row }">
                {{ row.unitPrice ? row.unitPrice.toFixed(2) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="总价(元)" width="110">
              <template #default="{ row }">
                {{ row.totalPrice ? row.totalPrice.toFixed(2) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="supplier" label="供应商" width="160" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getPurchaseStatusTag(row.status)" effect="light" size="small">
                  {{ getPurchaseStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applyBy" label="申请人" width="100" />
            <el-table-column prop="applyTime" label="申请时间" width="160" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleViewPurchase(row)">详情</el-button>
                <el-button type="success" link @click="handleApprovePurchase(row)" v-if="row.status === 'PENDING'">审批</el-button>
                <el-button type="success" link @click="handleReceivePurchase(row)" v-if="row.status === 'PURCHASING'">入库</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="purchasePagination.pageNum"
            v-model:page-size="purchasePagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="purchasePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchPurchaseList"
            @current-change="fetchPurchaseList"
          />
        </el-tab-pane>

        <el-tab-pane label="领用记录" name="usage">
          <div class="toolbar">
            <el-input
              v-model="usageSearchKeyword"
              placeholder="搜索领用记录..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchUsageList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="fetchUsageList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="usageTableData"
            v-loading="usageLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="usageNo" label="领用单号" width="160" />
            <el-table-column prop="consumableName" label="耗材名称" width="180" />
            <el-table-column prop="specification" label="规格型号" width="160" />
            <el-table-column prop="quantity" label="领用数量" width="100" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column prop="purpose" label="用途" width="160" show-overflow-tooltip />
            <el-table-column prop="receiver" label="领用人" width="100" />
            <el-table-column prop="usageTime" label="领用时间" width="160" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>

          <el-pagination
            v-model:current-page="usagePagination.pageNum"
            v-model:page-size="usagePagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="usagePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchUsageList"
            @current-change="fetchUsageList"
          />
        </el-tab-pane>

        <el-tab-pane label="库存预警" name="warning">
          <div class="toolbar">
            <el-input
              v-model="warningSearchKeyword"
              placeholder="搜索预警耗材..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchWarningList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="fetchWarningList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="warningTableData"
            v-loading="warningLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="consumableNo" label="耗材编号" width="140" />
            <el-table-column prop="consumableName" label="耗材名称" width="180" />
            <el-table-column prop="specification" label="规格型号" width="160" />
            <el-table-column label="库存状态" width="140">
              <template #default="{ row }">
                <el-tag type="warning" effect="light">
                  当前库存: {{ row.stockQuantity }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warningQuantity" label="预警值" width="80" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleQuickPurchase(row)">申请采购</el-button>
                <el-button type="success" link @click="handleQuickStockIn(row)">紧急入库</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="warningPagination.pageNum"
            v-model:page-size="warningPagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="warningPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchWarningList"
            @current-change="fetchWarningList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" top="5vh">
      <el-form
        :model="consumableForm"
        :rules="formRules"
        ref="consumableFormRef"
        label-width="100px"
        v-loading="detailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="耗材编号" prop="consumableNo">
              <el-input v-model="consumableForm.consumableNo" placeholder="自动生成或手动输入" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="耗材名称" prop="consumableName">
              <el-input v-model="consumableForm.consumableName" placeholder="请输入耗材名称" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="耗材分类" prop="category">
              <el-select v-model="consumableForm.category" placeholder="请选择分类" :disabled="isView" style="width: 100%">
                <el-option label="玻璃器皿" value="GLASSWARE" />
                <el-option label="色谱柱" value="CHROMATOGRAPHY" />
                <el-option label="滤膜/滤器" value="FILTER" />
                <el-option label="移液器/枪头" value="PIPETTE" />
                <el-option label="样品瓶/容器" value="CONTAINER" />
                <el-option label="其他耗材" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规格型号" prop="specification">
              <el-input v-model="consumableForm.specification" placeholder="如: 500mL 烧杯" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="consumableForm.unit" placeholder="请选择" :disabled="isView" style="width: 100%">
                <el-option label="个" value="个" />
                <el-option label="盒" value="盒" />
                <el-option label="包" value="包" />
                <el-option label="箱" value="箱" />
                <el-option label="支" value="支" />
                <el-option label="根" value="根" />
                <el-option label="卷" value="卷" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number
                v-model="consumableForm.stockQuantity"
                :min="0"
                :precision="0"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预警值" prop="warningQuantity">
              <el-input-number
                v-model="consumableForm.warningQuantity"
                :min="0"
                :precision="0"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价(元)" prop="unitPrice">
              <el-input-number
                v-model="consumableForm.unitPrice"
                :min="0"
                :precision="2"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="生产厂家" prop="manufacturer">
              <el-input v-model="consumableForm.manufacturer" placeholder="请输入生产厂家" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier">
              <el-input v-model="consumableForm.supplier" placeholder="请输入供应商" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
            v-model="consumableForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isView"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="stockInDialogVisible" title="耗材入库" width="600px" top="5vh">
      <el-form
        :model="stockInForm"
        :rules="stockInRules"
        ref="stockInFormRef"
        label-width="100px"
      >
        <el-form-item label="耗材名称" prop="consumableId">
          <el-select v-model="stockInForm.consumableId" placeholder="请选择耗材" style="width: 100%" filterable>
            <el-option
              v-for="item in tableData"
              :key="item.id"
              :label="item.consumableName + ' (' + item.specification + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="入库数量" prop="quantity">
              <el-input-number
                v-model="stockInForm.quantity"
                :min="1"
                :precision="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="stockInForm.unit" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="批次号" prop="batchNo">
              <el-input v-model="stockInForm.batchNo" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库日期" prop="stockInDate">
              <el-date-picker
                v-model="stockInForm.stockInDate"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="经办人" prop="operator">
          <el-input v-model="stockInForm.operator" placeholder="请输入经办人" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="stockInForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="stockInDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitStockIn">
          <el-icon><Check /></el-icon>
          确认入库
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="stockOutDialogVisible" title="耗材领用" width="600px" top="5vh">
      <el-form
        :model="stockOutForm"
        :rules="stockOutRules"
        ref="stockOutFormRef"
        label-width="100px"
      >
        <el-form-item label="耗材名称" prop="consumableId">
          <el-select v-model="stockOutForm.consumableId" placeholder="请选择耗材" style="width: 100%" filterable @change="handleConsumableChange">
            <el-option
              v-for="item in tableData"
              :key="item.id"
              :label="item.consumableName + ' (' + item.specification + ')' + ' 库存:' + item.stockQuantity"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="领用数量" prop="quantity">
              <el-input-number
                v-model="stockOutForm.quantity"
                :min="1"
                :precision="0"
                :max="maxStockOutQuantity"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="stockOutForm.unit" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="领用用途" prop="purpose">
              <el-select v-model="stockOutForm.purpose" placeholder="请选择" style="width: 100%">
                <el-option label="实验检测" value="TEST" />
                <el-option label="方法开发" value="METHOD_DEV" />
                <el-option label="质量控制" value="QC" />
                <el-option label="日常使用" value="DAILY" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="领用日期" prop="usageDate">
              <el-date-picker
                v-model="stockOutForm.usageDate"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="领用人" prop="receiver">
          <el-input v-model="stockOutForm.receiver" placeholder="请输入领用人" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="stockOutForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="stockOutDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitStockOut">
          <el-icon><Check /></el-icon>
          确认领用
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="purchaseDialogVisible" :title="purchaseDialogTitle" width="700px" top="5vh">
      <el-form
        :model="purchaseForm"
        :rules="purchaseRules"
        ref="purchaseFormRef"
        label-width="100px"
        v-loading="purchaseDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="采购单号" prop="purchaseNo">
              <el-input v-model="purchaseForm.purchaseNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="耗材名称" prop="consumableId">
              <el-select v-model="purchaseForm.consumableId" placeholder="请选择耗材" style="width: 100%" filterable :disabled="isPurchaseView">
                <el-option
                  v-for="item in tableData"
                  :key="item.id"
                  :label="item.consumableName + ' (' + item.specification + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="采购数量" prop="quantity">
              <el-input-number
                v-model="purchaseForm.quantity"
                :min="1"
                :precision="0"
                style="width: 100%"
                :disabled="isPurchaseView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="purchaseForm.unit" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预估单价" prop="unitPrice">
              <el-input-number
                v-model="purchaseForm.unitPrice"
                :min="0"
                :precision="2"
                style="width: 100%"
                :disabled="isPurchaseView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier">
              <el-input v-model="purchaseForm.supplier" placeholder="请输入供应商" :disabled="isPurchaseView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="要求到货日期" prop="expectedDate">
              <el-date-picker
                v-model="purchaseForm.expectedDate"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                :disabled="isPurchaseView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="申请人" prop="applyBy">
              <el-input v-model="purchaseForm.applyBy" :disabled="isPurchaseView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请日期" prop="applyDate">
              <el-date-picker
                v-model="purchaseForm.applyDate"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="采购原因" prop="reason">
          <el-input
            v-model="purchaseForm.reason"
            type="textarea"
            :rows="2"
            placeholder="请说明采购原因"
            :disabled="isPurchaseView"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="purchaseForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isPurchaseView"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="purchaseDialogVisible = false">关闭</el-button>
        <el-button v-if="!isPurchaseView" type="primary" @click="handleSubmitPurchase">
          <el-icon><Check /></el-icon>
          提交采购申请
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recordsDialogVisible" title="出入库记录" width="900px" top="5vh">
      <el-table :data="stockRecords" v-loading="recordsLoading" border stripe style="width: 100%">
        <el-table-column prop="recordNo" label="记录编号" width="160" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'IN' ? 'success' : 'warning'" effect="light" size="small">
              {{ row.type === 'IN' ? '入库' : '领用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="batchNo" label="批次号" width="140" />
        <el-table-column prop="operator" label="经办人/领用人" width="120" />
        <el-table-column prop="purpose" label="用途" width="120" />
        <el-table-column prop="recordTime" label="操作时间" width="180" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { consumableApi } from '@/api/reagent'
import dayjs from 'dayjs'

const loading = ref(false)
const purchaseLoading = ref(false)
const usageLoading = ref(false)
const warningLoading = ref(false)
const detailLoading = ref(false)
const purchaseDetailLoading = ref(false)
const recordsLoading = ref(false)

const searchKeyword = ref('')
const searchCategory = ref(null)
const purchaseSearchKeyword = ref('')
const purchaseSearchStatus = ref(null)
const usageSearchKeyword = ref('')
const warningSearchKeyword = ref('')
const activeTab = ref('stock')

const tableData = ref([])
const purchaseTableData = ref([])
const usageTableData = ref([])
const warningTableData = ref([])
const stockRecords = ref([])

const dialogVisible = ref(false)
const stockInDialogVisible = ref(false)
const stockOutDialogVisible = ref(false)
const purchaseDialogVisible = ref(false)
const recordsDialogVisible = ref(false)

const dialogTitle = ref('')
const purchaseDialogTitle = ref('')
const isView = ref(false)
const isPurchaseView = ref(false)
const maxStockOutQuantity = ref(9999)

const consumableFormRef = ref(null)
const stockInFormRef = ref(null)
const stockOutFormRef = ref(null)
const purchaseFormRef = ref(null)

const stats = reactive({
  total: 0,
  valid: 0,
  warning: 0,
  purchase: 0
})

const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const purchasePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const usagePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const warningPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const consumableForm = reactive({
  id: null, consumableNo: '', consumableName: '', category: '', specification: '',
  unit: '', stockQuantity: null, warningQuantity: null, unitPrice: null,
  manufacturer: '', supplier: '', remark: ''
})

const stockInForm = reactive({
  consumableId: null, quantity: null, unit: '', batchNo: '', stockInDate: '', operator: '', remark: ''
})

const stockOutForm = reactive({
  consumableId: null, quantity: null, unit: '', purpose: '', usageDate: '', receiver: '', remark: ''
})

const purchaseForm = reactive({
  id: null, purchaseNo: '', consumableId: null, consumableName: '', quantity: null,
  unit: '', unitPrice: null, totalPrice: null, supplier: '', expectedDate: '',
  applyBy: '', applyDate: '', reason: '', status: 'PENDING', remark: ''
})

const formRules = {
  consumableName: [{ required: true, message: '请输入耗材名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择耗材分类', trigger: 'change' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  stockQuantity: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  warningQuantity: [{ required: true, message: '请输入预警值', trigger: 'blur' }]
}

const stockInRules = {
  consumableId: [{ required: true, message: '请选择耗材', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  operator: [{ required: true, message: '请输入经办人', trigger: 'blur' }]
}

const stockOutRules = {
  consumableId: [{ required: true, message: '请选择耗材', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入领用数量', trigger: 'blur' }],
  purpose: [{ required: true, message: '请选择用途', trigger: 'change' }],
  receiver: [{ required: true, message: '请输入领用人', trigger: 'blur' }]
}

const purchaseRules = {
  consumableId: [{ required: true, message: '请选择耗材', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入采购数量', trigger: 'blur' }],
  applyBy: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入采购原因', trigger: 'blur' }]
}

const getCategoryText = (category) => {
  const map = {
    GLASSWARE: '玻璃器皿',
    CHROMATOGRAPHY: '色谱柱',
    FILTER: '滤膜/滤器',
    PIPETTE: '移液器/枪头',
    CONTAINER: '样品瓶/容器',
    OTHER: '其他耗材'
  }
  return map[category] || category
}

const getCategoryTag = (category) => {
  const map = {
    GLASSWARE: 'primary',
    CHROMATOGRAPHY: 'success',
    FILTER: 'warning',
    PIPETTE: 'info',
    CONTAINER: 'warning',
    OTHER: 'info'
  }
  return map[category] || 'info'
}

const getPurchaseStatusText = (status) => {
  const map = {
    PENDING: '待审批',
    APPROVED: '已批准',
    PURCHASING: '采购中',
    RECEIVED: '已入库',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

const getPurchaseStatusTag = (status) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'primary',
    PURCHASING: 'info',
    RECEIVED: 'success',
    CANCELLED: 'danger'
  }
  return map[status] || 'info'
}

const fetchStats = async () => {
  try {
    const res = await consumableApi.stats()
    if (res.data) Object.assign(stats, res.data)
  } catch (e) {
    stats.total = tableData.value.length
    stats.valid = tableData.value.filter(r => r.stockQuantity > r.warningQuantity).length
    stats.warning = tableData.value.filter(r => r.stockQuantity <= r.warningQuantity).length
    stats.purchase = purchaseTableData.value.filter(r => r.status !== 'RECEIVED' && r.status !== 'CANCELLED').length
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      category: searchCategory.value
    }
    const res = await consumableApi.page(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      tableData.value = generateMockData()
      pagination.total = tableData.value.length
    }
    await fetchStats()
  } catch (e) {
    console.error('获取耗材列表失败', e)
    ElMessage.error('获取列表失败')
    tableData.value = generateMockData()
    pagination.total = tableData.value.length
    await fetchStats()
  } finally { loading.value = false }
}

const fetchPurchaseList = async () => {
  purchaseLoading.value = true
  try {
    const params = {
      pageNum: purchasePagination.pageNum,
      pageSize: purchasePagination.pageSize,
      keyword: purchaseSearchKeyword.value,
      status: purchaseSearchStatus.value
    }
    const res = await consumableApi.getPurchaseRecords(null, params)
    if (res.data?.records) {
      purchaseTableData.value = res.data.records
      purchasePagination.total = res.data.total
    } else {
      purchaseTableData.value = generateMockPurchaseData()
      purchasePagination.total = purchaseTableData.value.length
    }
    await fetchStats()
  } catch (e) {
    purchaseTableData.value = generateMockPurchaseData()
    purchasePagination.total = purchaseTableData.value.length
    await fetchStats()
  } finally { purchaseLoading.value = false }
}

const fetchUsageList = async () => {
  usageLoading.value = true
  try {
    const params = {
      pageNum: usagePagination.pageNum,
      pageSize: usagePagination.pageSize,
      keyword: usageSearchKeyword.value
    }
    const res = await consumableApi.getUsageRecords(null, params)
    if (res.data?.records) {
      usageTableData.value = res.data.records
      usagePagination.total = res.data.total
    } else {
      usageTableData.value = generateMockUsageData()
      usagePagination.total = usageTableData.value.length
    }
  } catch (e) {
    usageTableData.value = generateMockUsageData()
    usagePagination.total = usageTableData.value.length
  } finally { usageLoading.value = false }
}

const fetchWarningList = async () => {
  warningLoading.value = true
  try {
    const params = {
      pageNum: warningPagination.pageNum,
      pageSize: warningPagination.pageSize,
      keyword: warningSearchKeyword.value
    }
    const res = await consumableApi.getWarningList(params)
    if (res.data?.records) {
      warningTableData.value = res.data.records
      warningPagination.total = res.data.total
    } else {
      warningTableData.value = tableData.value.filter(r => r.stockQuantity <= r.warningQuantity)
      warningPagination.total = warningTableData.value.length
    }
  } catch (e) {
    warningTableData.value = tableData.value.filter(r => r.stockQuantity <= r.warningQuantity)
    warningPagination.total = warningTableData.value.length
  } finally { warningLoading.value = false }
}

const generateMockData = () => {
  const categories = ['GLASSWARE', 'CHROMATOGRAPHY', 'FILTER', 'PIPETTE', 'CONTAINER']
  const names = [
    { name: '烧杯', spec: '500mL', cat: 'GLASSWARE', unit: '个' },
    { name: '容量瓶', spec: '1000mL', cat: 'GLASSWARE', unit: '个' },
    { name: '移液管', spec: '10mL', cat: 'GLASSWARE', unit: '支' },
    { name: 'C18色谱柱', spec: '4.6×250mm, 5μm', cat: 'CHROMATOGRAPHY', unit: '根' },
    { name: '滤膜', spec: '0.45μm, 50mm', cat: 'FILTER', unit: '盒' },
    { name: '一次性注射器', spec: '5mL', cat: 'FILTER', unit: '盒' },
    { name: '移液器枪头', spec: '1000μL', cat: 'PIPETTE', unit: '盒' },
    { name: '进样瓶', spec: '2mL, 棕色', cat: 'CONTAINER', unit: '盒' },
    { name: '样品管', spec: '50mL, 离心管', cat: 'CONTAINER', unit: '包' },
    { name: '玻璃棒', spec: '20cm', cat: 'GLASSWARE', unit: '根' },
    { name: '漏斗', spec: '10cm', cat: 'GLASSWARE', unit: '个' },
    { name: '提取柱', spec: 'C18, 500mg/6mL', cat: 'CHROMATOGRAPHY', unit: '盒' }
  ]
  
  return names.map((item, i) => ({
    id: i + 1,
    consumableNo: `CS${dayjs().format('YYYYMM')}${String(i + 1).padStart(4, '0')}`,
    consumableName: item.name,
    category: item.cat,
    specification: item.spec,
    unit: item.unit,
    stockQuantity: Math.random() > 0.3 ? (10 + i % 30) : (i % 5),
    warningQuantity: 10,
    unitPrice: 10 + i * 15.5,
    manufacturer: i % 2 === 0 ? '蜀玻集团' : '安捷伦科技',
    supplier: i % 2 === 0 ? '成都试剂公司' : '上海仪器公司',
    remark: ''
  }))
}

const generateMockPurchaseData = () => {
  const consumables = generateMockData()
  const statuses = ['PENDING', 'APPROVED', 'PURCHASING', 'RECEIVED']
  const data = []
  for (let i = 0; i < 8; i++) {
    const item = consumables[i % consumables.length]
    const status = statuses[i % 4]
    data.push({
      id: i + 1,
      purchaseNo: `PO${dayjs().format('YYYYMM')}${String(i + 1).padStart(4, '0')}`,
      consumableId: item.id,
      consumableName: item.consumableName,
      specification: item.specification,
      quantity: 20 + i * 5,
      unit: item.unit,
      unitPrice: item.unitPrice,
      totalPrice: (20 + i * 5) * item.unitPrice,
      supplier: item.supplier,
      expectedDate: dayjs().add(i + 5, 'day').format('YYYY-MM-DD'),
      applyBy: i % 2 === 0 ? '张三' : '李四',
      applyDate: dayjs().subtract(i, 'day').format('YYYY-MM-DD'),
      applyTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
      status: status,
      reason: '库存不足，需要补充',
      remark: ''
    })
  }
  return data
}

const generateMockUsageData = () => {
  const consumables = generateMockData()
  const purposes = ['实验检测', '方法开发', '质量控制', '日常使用']
  const data = []
  for (let i = 0; i < 10; i++) {
    const item = consumables[i % consumables.length]
    data.push({
      id: i + 1,
      usageNo: `UO${dayjs().format('YYYYMM')}${String(i + 1).padStart(4, '0')}`,
      consumableId: item.id,
      consumableName: item.consumableName,
      specification: item.specification,
      quantity: 1 + i % 5,
      unit: item.unit,
      purpose: purposes[i % 4],
      receiver: i % 2 === 0 ? '王五' : '赵六',
      usageTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
      remark: ''
    })
  }
  return data
}

const handleTabChange = () => {
  if (activeTab.value === 'stock') fetchList()
  else if (activeTab.value === 'purchase') fetchPurchaseList()
  else if (activeTab.value === 'usage') fetchUsageList()
  else fetchWarningList()
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增耗材'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑耗材'
  Object.assign(consumableForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除耗材"${row.consumableName}"吗？`, '提示', { type: 'warning' })
    await consumableApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e) { if (e !== 'cancel' && e !== false) ElMessage.error('删除失败') }
}

const handleSubmit = async () => {
  try {
    await consumableFormRef.value.validate()
    if (consumableForm.id) {
      await consumableApi.update(consumableForm)
      ElMessage.success('更新成功')
    } else {
      await consumableApi.save(consumableForm)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (e) { console.error(e) }
}

const handleStockIn = () => {
  Object.assign(stockInForm, {
    consumableId: null, quantity: null, unit: '', batchNo: '',
    stockInDate: dayjs().format('YYYY-MM-DD'), operator: '', remark: ''
  })
  stockInDialogVisible.value = true
}

const handleQuickStockIn = (row) => {
  Object.assign(stockInForm, {
    consumableId: row.id, quantity: null, unit: row.unit, batchNo: '',
    stockInDate: dayjs().format('YYYY-MM-DD'), operator: '', remark: ''
  })
  stockInDialogVisible.value = true
}

const handleSubmitStockIn = async () => {
  try {
    await stockInFormRef.value.validate()
    await consumableApi.stockIn(stockInForm)
    ElMessage.success('入库成功')
    stockInDialogVisible.value = false
    fetchList()
  } catch (e) { console.error(e) }
}

const handleStockOut = () => {
  maxStockOutQuantity.value = 9999
  Object.assign(stockOutForm, {
    consumableId: null, quantity: null, unit: '', purpose: '',
    usageDate: dayjs().format('YYYY-MM-DD'), receiver: '', remark: ''
  })
  stockOutDialogVisible.value = true
}

const handleConsumableChange = (id) => {
  const item = tableData.value.find(i => i.id === id)
  if (item) {
    stockOutForm.value.unit = item.unit
    maxStockOutQuantity.value = item.stockQuantity
  }
}

const handleSubmitStockOut = async () => {
  try {
    await stockOutFormRef.value.validate()
    await consumableApi.stockOut(stockOutForm)
    ElMessage.success('领用成功')
    stockOutDialogVisible.value = false
    fetchList()
    fetchUsageList()
  } catch (e) { console.error(e) }
}

const handleAddPurchase = () => {
  isPurchaseView.value = false
  purchaseDialogTitle.value = '新增采购申请'
  resetPurchaseForm()
  purchaseDialogVisible.value = true
}

const handleQuickPurchase = (row) => {
  isPurchaseView.value = false
  purchaseDialogTitle.value = '采购申请'
  Object.assign(purchaseForm, {
    id: null,
    purchaseNo: 'PO-' + dayjs().format('YYYYMMDDHHmmss'),
    consumableId: row.id,
    consumableName: row.consumableName,
    quantity: Math.max(row.warningQuantity * 2, 20),
    unit: row.unit,
    unitPrice: row.unitPrice,
    totalPrice: row.unitPrice ? Math.max(row.warningQuantity * 2, 20) * row.unitPrice : null,
    supplier: row.supplier,
    expectedDate: dayjs().add(7, 'day').format('YYYY-MM-DD'),
    applyBy: '',
    applyDate: dayjs().format('YYYY-MM-DD'),
    reason: '库存不足，当前库存' + row.stockQuantity + row.unit + '，低于预警值' + row.warningQuantity + row.unit,
    status: 'PENDING',
    remark: ''
  })
  purchaseDialogVisible.value = true
}

const handleViewPurchase = (row) => {
  isPurchaseView.value = true
  purchaseDialogTitle.value = '采购单详情'
  Object.assign(purchaseForm, { ...row })
  purchaseDialogVisible.value = true
}

const handleApprovePurchase = (row) => {
  ElMessageBox.confirm(`确定批准采购单"${row.purchaseNo}"吗？`, '审批', { type: 'info' })
    .then(() => {
      row.status = 'APPROVED'
      ElMessage.success('已批准')
      fetchPurchaseList()
    })
    .catch(() => {})
}

const handleReceivePurchase = (row) => {
  const item = tableData.value.find(i => i.id === row.consumableId)
  if (item) {
    Object.assign(stockInForm, {
      consumableId: row.consumableId,
      quantity: row.quantity,
      unit: row.unit,
      batchNo: '',
      stockInDate: dayjs().format('YYYY-MM-DD'),
      operator: '',
      remark: `采购入库: ${row.purchaseNo}`
    })
    stockInDialogVisible.value = true
    row.status = 'RECEIVED'
  }
}

const handleSubmitPurchase = async () => {
  try {
    await purchaseFormRef.value.validate()
    await consumableApi.purchase(purchaseForm)
    ElMessage.success('采购申请已提交')
    purchaseDialogVisible.value = false
    fetchPurchaseList()
  } catch (e) { console.error(e) }
}

const handleViewStockRecords = async (row) => {
  recordsLoading.value = true
  try {
    const res = await consumableApi.getPurchaseRecords(row.id, { pageNum: 1, pageSize: 100 })
    if (res.data?.records) {
      stockRecords.value = res.data.records
    } else {
      stockRecords.value = generateMockRecords(row)
    }
    recordsDialogVisible.value = true
  } catch (e) {
    stockRecords.value = generateMockRecords(row)
    recordsDialogVisible.value = true
  } finally { recordsLoading.value = false }
}

const generateMockRecords = (row) => {
  const records = []
  for (let i = 0; i < 6; i++) {
    records.push({
      id: i + 1,
      recordNo: `SR${dayjs().format('YYYYMM')}${String(i + 1).padStart(6, '0')}`,
      type: i % 2 === 0 ? 'IN' : 'OUT',
      quantity: 5 + i * 2,
      unit: row.unit,
      batchNo: `B${dayjs().format('YYYYMM')}${String(i + 1).padStart(3, '0')}`,
      operator: i % 2 === 0 ? '张三' : '李四',
      purpose: i % 2 === 0 ? '' : '实验检测',
      recordTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
      remark: ''
    })
  }
  return records
}

const resetForm = () => {
  Object.assign(consumableForm, {
    id: null, consumableNo: '', consumableName: '', category: '', specification: '',
    unit: '', stockQuantity: null, warningQuantity: null, unitPrice: null,
    manufacturer: '', supplier: '', remark: ''
  })
}

const resetPurchaseForm = () => {
  Object.assign(purchaseForm, {
    id: null, purchaseNo: 'PO-' + dayjs().format('YYYYMMDDHHmmss'),
    consumableId: null, consumableName: '', quantity: null,
    unit: '', unitPrice: null, totalPrice: null, supplier: '',
    expectedDate: dayjs().add(7, 'day').format('YYYY-MM-DD'),
    applyBy: '', applyDate: dayjs().format('YYYY-MM-DD'),
    reason: '', status: 'PENDING', remark: ''
  })
}

onMounted(() => { fetchList() })
</script>

<style lang="scss" scoped>
.page-container {
  .page-header {
    margin-bottom: 16px;
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
  }
  .stats-row {
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;
        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: #fff;
        }
        .stat-info {
          .stat-label {
            font-size: 14px;
            color: #606266;
            margin-bottom: 4px;
          }
          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
          }
        }
      }
      &.total .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
      &.valid .stat-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); }
      &.warning .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
      &.purchase .stat-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); }
    }
  }
  .toolbar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
  .text-warning {
    color: #e6a23c;
    font-weight: 600;
  }
}
</style>
