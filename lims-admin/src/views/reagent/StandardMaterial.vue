<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">标准物质/标准溶液</div>
        <div class="page-desc">管理有证标准物质入库、配制记录、标定记录、有效期管理及溯源关联检测任务</div>
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
              <div class="stat-label">标准物质总数</div>
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
              <div class="stat-label">在有效期内</div>
              <div class="stat-value">{{ stats.valid || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card solution" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">标准溶液数</div>
              <div class="stat-value">{{ stats.solution || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card expired" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">即将/已过期</div>
              <div class="stat-value">{{ stats.expired || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="有证标准物质" name="material">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索标准物质名称、编号..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="searchType" placeholder="物质类型" clearable style="width: 140px">
              <el-option label="纯物质" value="PURE" />
              <el-option label="溶液标准物质" value="SOLUTION" />
              <el-option label="气体标准物质" value="GAS" />
              <el-option label="固体标准物质" value="SOLID" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增标准物质
            </el-button>
            <el-button type="success" @click="handleStockIn">
              <el-icon><Download /></el-icon>
              入库
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
            <el-table-column prop="materialNo" label="物质编号" width="160" />
            <el-table-column prop="materialName" label="物质名称" width="180" />
            <el-table-column prop="materialType" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getMaterialTypeTag(row.materialType)" effect="light" size="small">
                  {{ getMaterialTypeText(row.materialType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="certificateNo" label="证书编号" width="140" />
            <el-table-column prop="batchNo" label="批号" width="120" />
            <el-table-column prop="concentration" label="标准值" width="140">
              <template #default="{ row }">
                {{ row.concentration }} {{ row.unit || '' }}
              </template>
            </el-table-column>
            <el-table-column prop="stockQuantity" label="库存数量" width="100" />
            <el-table-column prop="validDate" label="有效期至" width="110" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getExpireStatusTag(row)" effect="light" size="small">
                  {{ getExpireStatusText(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                <el-button type="success" link @click="handlePrepare(row)">配制溶液</el-button>
                <el-button type="info" link @click="handleTrace(row)">溯源检测</el-button>
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

        <el-tab-pane label="配制记录" name="prepare">
          <div class="toolbar">
            <el-input
              v-model="prepareSearchKeyword"
              placeholder="搜索配制记录..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchPrepareList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAddPrepare">
              <el-icon><Plus /></el-icon>
              新增配制
            </el-button>
            <el-button @click="fetchPrepareList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="prepareTableData"
            v-loading="prepareLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="prepareNo" label="配制单号" width="160" />
            <el-table-column prop="solutionName" label="溶液名称" width="180" />
            <el-table-column prop="materialName" label="使用标准物质" width="160" show-overflow-tooltip />
            <el-table-column prop="targetConcentration" label="目标浓度" width="140">
              <template #default="{ row }">
                {{ row.targetConcentration }} {{ row.unit || '' }}
              </template>
            </el-table-column>
            <el-table-column prop="prepareVolume" label="配制体积" width="120">
              <template #default="{ row }">
                {{ row.prepareVolume }} {{ row.volumeUnit || 'mL' }}
              </template>
            </el-table-column>
            <el-table-column prop="prepareBy" label="配制人" width="100" />
            <el-table-column prop="validDate" label="有效期至" width="110" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getPrepareStatusTag(row.status)" effect="light" size="small">
                  {{ getPrepareStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="success" link @click="handleCalibrate(row)" v-if="row.status === 'PENDING'">标定</el-button>
                <el-button type="primary" link @click="handleViewPrepare(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="preparePagination.pageNum"
            v-model:page-size="preparePagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="preparePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchPrepareList"
            @current-change="fetchPrepareList"
          />
        </el-tab-pane>

        <el-tab-pane label="标定记录" name="calibrate">
          <div class="toolbar">
            <el-input
              v-model="calibrateSearchKeyword"
              placeholder="搜索标定记录..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchCalibrateList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="fetchCalibrateList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="calibrateTableData"
            v-loading="calibrateLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="calibrateNo" label="标定单号" width="160" />
            <el-table-column prop="solutionName" label="标准溶液" width="180" />
            <el-table-column prop="calibrateConcentration" label="标定浓度" width="140">
              <template #default="{ row }">
                {{ row.calibrateConcentration }} {{ row.unit || '' }}
              </template>
            </el-table-column>
            <el-table-column prop="deviation" label="相对偏差" width="110">
              <template #default="{ row }">
                <span :class="{ 'text-danger': Math.abs(row.deviation) > 0.5 }">
                  {{ row.deviation }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="calibrateBy" label="标定人" width="100" />
            <el-table-column prop="calibrateTime" label="标定时间" width="180" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleViewCalibrate(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="calibratePagination.pageNum"
            v-model:page-size="calibratePagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="calibratePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchCalibrateList"
            @current-change="fetchCalibrateList"
          />
        </el-tab-pane>

        <el-tab-pane label="过期提醒" name="expire">
          <div class="toolbar">
            <el-input
              v-model="expireSearchKeyword"
              placeholder="搜索..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchExpireList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="fetchExpireList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="expireTableData"
            v-loading="expireLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.type === 'MATERIAL' ? 'primary' : 'success'" effect="light" size="small">
                  {{ row.type === 'MATERIAL' ? '标准物质' : '标准溶液' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="no" label="编号" width="160" />
            <el-table-column prop="name" label="名称" width="180" />
            <el-table-column prop="concentration" label="浓度/含量" width="140" />
            <el-table-column prop="validDate" label="有效期至" width="110" />
            <el-table-column label="剩余天数" width="100">
              <template #default="{ row }">
                <span :class="getExpireDaysClass(row)">
                  {{ getExpireDays(row) }}天
                </span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getExpireStatusTag(row)" effect="light" size="small">
                  {{ getExpireStatusText(row) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="expirePagination.pageNum"
            v-model:page-size="expirePagination.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="expirePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchExpireList"
            @current-change="fetchExpireList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" top="5vh">
      <el-form
        :model="materialForm"
        :rules="formRules"
        ref="materialFormRef"
        label-width="100px"
        v-loading="detailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="物质编号" prop="materialNo">
              <el-input v-model="materialForm.materialNo" placeholder="自动生成或手动输入" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物质名称" prop="materialName">
              <el-input v-model="materialForm.materialName" placeholder="请输入物质名称" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="物质类型" prop="materialType">
              <el-select v-model="materialForm.materialType" placeholder="请选择类型" :disabled="isView" style="width: 100%">
                <el-option label="纯物质" value="PURE" />
                <el-option label="溶液标准物质" value="SOLUTION" />
                <el-option label="气体标准物质" value="GAS" />
                <el-option label="固体标准物质" value="SOLID" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="证书编号" prop="certificateNo">
              <el-input v-model="materialForm.certificateNo" placeholder="请输入证书编号" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="批号" prop="batchNo">
              <el-input v-model="materialForm.batchNo" placeholder="请输入批号" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="标准值" prop="concentration">
              <el-input-number
                v-model="materialForm.concentration"
                :precision="6"
                :step="0.001"
                :min="0"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="materialForm.unit" placeholder="请选择单位" :disabled="isView" style="width: 100%">
                <el-option label="mg/L" value="mg/L" />
                <el-option label="μg/L" value="μg/L" />
                <el-option label="mg/kg" value="mg/kg" />
                <el-option label="g" value="g" />
                <el-option label="mg" value="mg" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="不确定度" prop="uncertainty">
              <el-input v-model="materialForm.uncertainty" placeholder="如: ±0.05 mg/L" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="有效期至" prop="validDate">
              <el-date-picker
                v-model="materialForm.validDate"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number
                v-model="materialForm.stockQuantity"
                :min="0"
                :precision="2"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单位" prop="stockUnit">
              <el-select v-model="materialForm.stockUnit" placeholder="请选择" :disabled="isView" style="width: 100%">
                <el-option label="瓶" value="瓶" />
                <el-option label="支" value="支" />
                <el-option label="盒" value="盒" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产厂家" prop="manufacturer">
              <el-input v-model="materialForm.manufacturer" placeholder="请输入生产厂家" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="materialForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isView"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="prepareDialogVisible" :title="prepareDialogTitle" width="800px" top="5vh">
      <el-form
        :model="prepareForm"
        :rules="prepareRules"
        ref="prepareFormRef"
        label-width="120px"
        v-loading="prepareDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配制单号" prop="prepareNo">
              <el-input v-model="prepareForm.prepareNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="溶液名称" prop="solutionName">
              <el-input v-model="prepareForm.solutionName" :disabled="isPrepareView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="使用标准物质" prop="materialId">
              <el-select v-model="prepareForm.materialId" placeholder="请选择" :disabled="isPrepareView" style="width: 100%" filterable>
                <el-option
                  v-for="item in materialOptions"
                  :key="item.id"
                  :label="item.materialName + ' (' + item.materialNo + ')'"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准物质批号" prop="materialBatchNo">
              <el-input v-model="prepareForm.materialBatchNo" :disabled="isPrepareView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="目标浓度" prop="targetConcentration">
              <el-input-number
                v-model="prepareForm.targetConcentration"
                :precision="6"
                :step="0.001"
                :min="0"
                style="width: 100%"
                :disabled="isPrepareView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="浓度单位" prop="unit">
              <el-select v-model="prepareForm.unit" :disabled="isPrepareView" style="width: 100%">
                <el-option label="mg/L" value="mg/L" />
                <el-option label="μg/L" value="μg/L" />
                <el-option label="mol/L" value="mol/L" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配制体积" prop="prepareVolume">
              <el-input-number
                v-model="prepareForm.prepareVolume"
                :precision="2"
                :min="0"
                style="width: 100%"
                :disabled="isPrepareView"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="配制方法" prop="prepareMethod">
          <el-input
            v-model="prepareForm.prepareMethod"
            type="textarea"
            :rows="3"
            placeholder="请详细描述配制过程"
            :disabled="isPrepareView"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="配制人" prop="prepareBy">
              <el-input v-model="prepareForm.prepareBy" :disabled="isPrepareView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="校核人" prop="verifyBy">
              <el-input v-model="prepareForm.verifyBy" :disabled="isPrepareView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="配制日期" prop="prepareDate">
              <el-date-picker
                v-model="prepareForm.prepareDate"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                :disabled="isPrepareView"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="prepareForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isPrepareView"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="prepareDialogVisible = false">关闭</el-button>
        <el-button v-if="!isPrepareView" type="primary" @click="handleSubmitPrepare">保存配制记录</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="calibrateDialogVisible" title="标准溶液标定" width="800px" top="5vh">
      <el-form
        :model="calibrateForm"
        :rules="calibrateRules"
        ref="calibrateFormRef"
        label-width="120px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="标定单号" prop="calibrateNo">
              <el-input v-model="calibrateForm.calibrateNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准溶液">
              <el-input :value="calibrateForm.solutionName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="标定方法" prop="calibrateMethod">
              <el-select v-model="calibrateForm.calibrateMethod" placeholder="请选择" style="width: 100%">
                <el-option label="基准物质法" value="PRIMARY" />
                <el-option label="比较法" value="COMPARISON" />
                <el-option label="电位滴定法" value="POTENTIOMETRIC" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标定浓度" prop="calibrateConcentration">
              <el-input-number
                v-model="calibrateForm.calibrateConcentration"
                :precision="6"
                :step="0.001"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="calibrateForm.unit" style="width: 100%">
                <el-option label="mg/L" value="mg/L" />
                <el-option label="μg/L" value="μg/L" />
                <el-option label="mol/L" value="mol/L" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="相对偏差(%)" prop="deviation">
              <el-input v-model="calibrateForm.deviation" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="扩展不确定度" prop="uncertainty">
              <el-input v-model="calibrateForm.uncertainty" placeholder="如: ±0.02 mg/L, k=2" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标定日期" prop="calibrateDate">
              <el-date-picker
                v-model="calibrateForm.calibrateDate"
                type="date"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标定过程" prop="calibrateProcess">
          <el-input
            v-model="calibrateForm.calibrateProcess"
            type="textarea"
            :rows="3"
            placeholder="请详细描述标定过程"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="标定人" prop="calibrateBy">
              <el-input v-model="calibrateForm.calibrateBy" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="复核人" prop="verifyBy">
              <el-input v-model="calibrateForm.verifyBy" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="calibrateForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="calibrateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitCalibrate">保存标定记录</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="traceDialogVisible" title="溯源检测任务" width="900px" top="5vh">
      <div v-if="currentMaterial">
        <div class="trace-header">
          <div class="trace-title">标准物质: {{ currentMaterial.materialName }} ({{ currentMaterial.materialNo }})</div>
          <div class="trace-desc">以下检测任务使用了该标准物质，可追溯检测结果</div>
        </div>
        <el-table :data="traceTasks" v-loading="traceLoading" border stripe style="width: 100%; margin-top: 16px">
          <el-table-column prop="taskNo" label="任务编号" width="160" />
          <el-table-column prop="sampleName" label="样品名称" width="180" />
          <el-table-column prop="itemName" label="检测项目" width="160" />
          <el-table-column prop="testResult" label="检测结果" width="140" />
          <el-table-column prop="testTime" label="检测时间" width="180" />
          <el-table-column prop="operator" label="检测人" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'PASSED' ? 'success' : 'warning'" effect="light" size="small">
                {{ row.status === 'PASSED' ? '已完成' : '进行中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { standardMaterialApi } from '@/api/reagent'
import dayjs from 'dayjs'

const loading = ref(false)
const prepareLoading = ref(false)
const calibrateLoading = ref(false)
const expireLoading = ref(false)
const detailLoading = ref(false)
const prepareDetailLoading = ref(false)
const traceLoading = ref(false)

const searchKeyword = ref('')
const searchType = ref(null)
const prepareSearchKeyword = ref('')
const calibrateSearchKeyword = ref('')
const expireSearchKeyword = ref('')
const activeTab = ref('material')

const tableData = ref([])
const prepareTableData = ref([])
const calibrateTableData = ref([])
const expireTableData = ref([])
const traceTasks = ref([])
const materialOptions = ref([])

const dialogVisible = ref(false)
const stockInDialogVisible = ref(false)
const prepareDialogVisible = ref(false)
const calibrateDialogVisible = ref(false)
const traceDialogVisible = ref(false)

const dialogTitle = ref('')
const prepareDialogTitle = ref('')
const isView = ref(false)
const isPrepareView = ref(false)
const currentMaterial = ref(null)

const materialFormRef = ref(null)
const prepareFormRef = ref(null)
const calibrateFormRef = ref(null)

const stats = reactive({
  total: 0,
  valid: 0,
  solution: 0,
  expired: 0
})

const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const preparePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const calibratePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const expirePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const materialForm = reactive({
  id: null, materialNo: '', materialName: '', materialType: '', certificateNo: '',
  batchNo: '', concentration: null, unit: '', uncertainty: '', stockQuantity: null,
  stockUnit: '', validDate: '', manufacturer: '', remark: ''
})

const stockInForm = reactive({
  materialId: null, quantity: null, unit: '', batchNo: '', stockInDate: '', operator: '', remark: ''
})

const prepareForm = reactive({
  id: null, prepareNo: '', solutionName: '', materialId: null, materialName: '',
  materialBatchNo: '', targetConcentration: null, unit: '', prepareVolume: null,
  volumeUnit: 'mL', validDays: 30, prepareMethod: '', prepareBy: '', verifyBy: '',
  prepareDate: '', status: 'PENDING', remark: ''
})

const calibrateForm = reactive({
  id: null, calibrateNo: '', solutionId: null, solutionName: '', calibrateMethod: '',
  calibrateConcentration: null, unit: '', deviation: null, uncertainty: '',
  calibrateProcess: '', calibrateBy: '', verifyBy: '', calibrateDate: '', remark: ''
})

const formRules = {
  materialName: [{ required: true, message: '请输入物质名称', trigger: 'blur' }],
  materialType: [{ required: true, message: '请选择物质类型', trigger: 'change' }],
  certificateNo: [{ required: true, message: '请输入证书编号', trigger: 'blur' }],
  concentration: [{ required: true, message: '请输入标准值', trigger: 'blur' }],
  validDate: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const prepareRules = {
  solutionName: [{ required: true, message: '请输入溶液名称', trigger: 'blur' }],
  materialId: [{ required: true, message: '请选择标准物质', trigger: 'change' }],
  targetConcentration: [{ required: true, message: '请输入目标浓度', trigger: 'blur' }],
  prepareMethod: [{ required: true, message: '请输入配制方法', trigger: 'blur' }],
  prepareBy: [{ required: true, message: '请输入配制人', trigger: 'blur' }]
}

const calibrateRules = {
  calibrateMethod: [{ required: true, message: '请选择标定方法', trigger: 'change' }],
  calibrateConcentration: [{ required: true, message: '请输入标定浓度', trigger: 'blur' }],
  calibrateBy: [{ required: true, message: '请输入标定人', trigger: 'blur' }],
  verifyBy: [{ required: true, message: '请输入复核人', trigger: 'blur' }]
}

const getMaterialTypeText = (type) => {
  const map = { PURE: '纯物质', SOLUTION: '溶液标准物质', GAS: '气体标准物质', SOLID: '固体标准物质' }
  return map[type] || type
}

const getMaterialTypeTag = (type) => {
  const map = { PURE: 'primary', SOLUTION: 'success', GAS: 'warning', SOLID: 'info' }
  return map[type] || 'info'
}

const getPrepareStatusText = (status) => {
  const map = { PENDING: '待标定', CALIBRATED: '已标定', EXPIRED: '已过期' }
  return map[status] || status
}

const getPrepareStatusTag = (status) => {
  const map = { PENDING: 'warning', CALIBRATED: 'success', EXPIRED: 'danger' }
  return map[status] || 'info'
}

const getExpireStatus = (row) => {
  const today = dayjs()
  const validDate = dayjs(row.validDate)
  const diffDays = validDate.diff(today, 'day')
  if (diffDays < 0) return 3
  if (diffDays <= 30) return 2
  return 1
}

const getExpireStatusText = (row) => {
  const status = getExpireStatus(row)
  const map = { 1: '在有效期内', 2: '即将过期', 3: '已过期' }
  return map[status]
}

const getExpireStatusTag = (row) => {
  const status = getExpireStatus(row)
  const map = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[status]
}

const getExpireDays = (row) => dayjs(row.validDate).diff(dayjs(), 'day')

const getExpireDaysClass = (row) => {
  const days = getExpireDays(row)
  if (days < 0) return 'text-danger'
  if (days <= 7) return 'text-danger'
  if (days <= 30) return 'text-warning'
  return ''
}

const fetchStats = async () => {
  try {
    const res = await standardMaterialApi.stats()
    if (res.data) Object.assign(stats, res.data)
  } catch (e) {
    stats.total = tableData.value.length
    stats.valid = tableData.value.filter(r => getExpireStatus(r) === 1).length
    stats.solution = prepareTableData.value.filter(r => r.status === 'CALIBRATED').length
    stats.expired = tableData.value.filter(r => getExpireStatus(r) !== 1).length
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.pageNum, pageSize: pagination.pageSize, keyword: searchKeyword.value, materialType: searchType.value }
    const res = await standardMaterialApi.page(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      tableData.value = generateMockData()
      pagination.total = tableData.value.length
    }
    materialOptions.value = tableData.value.filter(r => getExpireStatus(r) === 1)
    await fetchStats()
  } catch (e) {
    tableData.value = generateMockData()
    pagination.total = tableData.value.length
    materialOptions.value = tableData.value.filter(r => getExpireStatus(r) === 1)
    await fetchStats()
  } finally { loading.value = false }
}

const fetchPrepareList = async () => {
  prepareLoading.value = true
  try {
    const params = { pageNum: preparePagination.pageNum, pageSize: preparePagination.pageSize, keyword: prepareSearchKeyword.value }
    const res = await standardMaterialApi.getPrepareRecords(null, params)
    if (res.data?.records) {
      prepareTableData.value = res.data.records
      preparePagination.total = res.data.total
    } else {
      prepareTableData.value = generateMockPrepareData()
      preparePagination.total = prepareTableData.value.length
    }
    await fetchStats()
  } catch (e) {
    prepareTableData.value = generateMockPrepareData()
    preparePagination.total = prepareTableData.value.length
    await fetchStats()
  } finally { prepareLoading.value = false }
}

const fetchCalibrateList = async () => {
  calibrateLoading.value = true
  try {
    const params = { pageNum: calibratePagination.pageNum, pageSize: calibratePagination.pageSize, keyword: calibrateSearchKeyword.value }
    const res = await standardMaterialApi.getCalibrateRecords(null, params)
    if (res.data?.records) {
      calibrateTableData.value = res.data.records
      calibratePagination.total = res.data.total
    } else {
      calibrateTableData.value = generateMockCalibrateData()
      calibratePagination.total = calibrateTableData.value.length
    }
  } catch (e) {
    calibrateTableData.value = generateMockCalibrateData()
    calibratePagination.total = calibrateTableData.value.length
  } finally { calibrateLoading.value = false }
}

const fetchExpireList = async () => {
  expireLoading.value = true
  try {
    const params = { pageNum: expirePagination.pageNum, pageSize: expirePagination.pageSize, keyword: expireSearchKeyword.value }
    const res = await standardMaterialApi.getExpireList(params)
    if (res.data?.records) {
      expireTableData.value = res.data.records
      expirePagination.total = res.data.total
    } else {
      const materialExpire = tableData.value.filter(r => getExpireStatus(r) !== 1).map(r => ({
        type: 'MATERIAL', no: r.materialNo, name: r.materialName, batchNo: r.batchNo,
        concentration: `${r.concentration} ${r.unit}`, validDate: r.validDate
      }))
      const solutionExpire = prepareTableData.value.filter(r => getExpireStatus(r) !== 1).map(r => ({
        type: 'SOLUTION', no: r.prepareNo, name: r.solutionName, batchNo: r.materialBatchNo,
        concentration: `${r.targetConcentration} ${r.unit}`, validDate: r.validDate
      }))
      expireTableData.value = [...materialExpire, ...solutionExpire]
      expirePagination.total = expireTableData.value.length
    }
  } catch (e) {
    expireTableData.value = tableData.value.filter(r => getExpireStatus(r) !== 1).map(r => ({
      type: 'MATERIAL', no: r.materialNo, name: r.materialName, concentration: `${r.concentration} ${r.unit}`, validDate: r.validDate
    }))
    expirePagination.total = expireTableData.value.length
  } finally { expireLoading.value = false }
}

const generateMockData = () => {
  const types = ['PURE', 'SOLUTION', 'SOLID']
  const names = ['镉标准溶液', '铅标准溶液', '砷标准溶液', '汞标准溶液', '铜标准溶液', '锌标准溶液', 'pH标准物质', '电导率标准物质']
  return names.map((name, i) => ({
    id: i + 1,
    materialNo: `GBW${String(i + 1).padStart(6, '0')}`,
    materialName: name,
    materialType: types[i % types.length],
    certificateNo: `GBW(E)08${String(i + 1).padStart(4, '0')}`,
    batchNo: `B${dayjs().format('YYYY')}${String(i + 1).padStart(4, '0')}`,
    concentration: 100 + i * 10,
    unit: 'mg/L',
    uncertainty: `±${0.5 + i * 0.1} mg/L`,
    stockQuantity: Math.random() > 0.3 ? (5 + i % 10) : (i % 3),
    stockUnit: i % 2 === 0 ? '瓶' : '支',
    validDate: dayjs().add(12 - i % 24, 'month').format('YYYY-MM-DD'),
    manufacturer: '国家标准物质研究中心',
    remark: ''
  }))
}

const generateMockPrepareData = () => {
  const materials = generateMockData()
  const data = []
  for (let i = 0; i < 8; i++) {
    const material = materials[i % materials.length]
    const status = i % 3 === 0 ? 'PENDING' : (i % 3 === 1 ? 'CALIBRATED' : 'EXPIRED')
    data.push({
      id: i + 1,
      prepareNo: `PRE${dayjs().format('YYYYMM')}${String(i + 1).padStart(4, '0')}`,
      solutionName: `${material.materialName}工作液`,
      materialId: material.id,
      materialName: material.materialName,
      materialBatchNo: material.batchNo,
      targetConcentration: 10 + i,
      unit: 'mg/L',
      prepareVolume: 500,
      volumeUnit: 'mL',
      validDays: 30,
      prepareMethod: '移取适量标准储备液，用去离子水定容至500mL容量瓶中',
      prepareBy: '张三',
      verifyBy: '李四',
      prepareDate: dayjs().subtract(i, 'day').format('YYYY-MM-DD'),
      validDate: dayjs().add(30 - i, 'day').format('YYYY-MM-DD'),
      status: status,
      remark: ''
    })
  }
  return data
}

const generateMockCalibrateData = () => {
  const solutions = generateMockPrepareData().filter(s => s.status === 'CALIBRATED')
  return solutions.map((sol, i) => ({
    id: i + 1,
    calibrateNo: `CAL${dayjs().format('YYYYMM')}${String(i + 1).padStart(4, '0')}`,
    solutionId: sol.id,
    solutionName: sol.solutionName,
    calibrateMethod: i % 2 === 0 ? 'PRIMARY' : 'COMPARISON',
    calibrateConcentration: sol.targetConcentration + (Math.random() - 0.5) * 0.5,
    unit: sol.unit,
    deviation: (Math.random() * 0.5).toFixed(2),
    uncertainty: `±${0.02 + i * 0.01} mg/L, k=2`,
    calibrateProcess: '采用基准物质法进行标定，平行测定三次',
    calibrateBy: '王五',
    verifyBy: '赵六',
    calibrateDate: dayjs().subtract(i, 'day').format('YYYY-MM-DD'),
    calibrateTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
    remark: ''
  }))
}

const handleTabChange = () => {
  if (activeTab.value === 'material') fetchList()
  else if (activeTab.value === 'prepare') fetchPrepareList()
  else if (activeTab.value === 'calibrate') fetchCalibrateList()
  else fetchExpireList()
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增标准物质'
  resetMaterialForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑标准物质'
  Object.assign(materialForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除标准物质"${row.materialName}"吗？`, '提示', { type: 'warning' })
    await standardMaterialApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e) { if (e !== 'cancel' && e !== false) ElMessage.error('删除失败') }
}

const handleSubmit = async () => {
  try {
    await materialFormRef.value.validate()
    if (materialForm.id) {
      await standardMaterialApi.update(materialForm)
      ElMessage.success('更新成功')
    } else {
      await standardMaterialApi.save(materialForm)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (e) { console.error(e) }
}

const handleStockIn = () => {
  Object.assign(stockInForm, { materialId: null, quantity: null, unit: '', batchNo: '', stockInDate: dayjs().format('YYYY-MM-DD'), operator: '', remark: '' })
  stockInDialogVisible.value = true
}

const handlePrepare = (row) => {
  isPrepareView.value = false
  prepareDialogTitle.value = '配制标准溶液'
  Object.assign(prepareForm, {
    id: null,
    prepareNo: 'PRE-' + dayjs().format('YYYYMMDDHHmmss'),
    solutionName: '',
    materialId: row.id,
    materialName: row.materialName,
    materialBatchNo: row.batchNo,
    targetConcentration: null,
    unit: row.unit,
    prepareVolume: null,
    volumeUnit: 'mL',
    validDays: 30,
    prepareMethod: '',
    prepareBy: '',
    verifyBy: '',
    prepareDate: dayjs().format('YYYY-MM-DD'),
    status: 'PENDING',
    remark: ''
  })
  prepareDialogVisible.value = true
}

const handleAddPrepare = () => {
  isPrepareView.value = false
  prepareDialogTitle.value = '新增配制记录'
  Object.assign(prepareForm, {
    id: null,
    prepareNo: 'PRE-' + dayjs().format('YYYYMMDDHHmmss'),
    solutionName: '',
    materialId: null,
    materialName: '',
    materialBatchNo: '',
    targetConcentration: null,
    unit: 'mg/L',
    prepareVolume: null,
    volumeUnit: 'mL',
    validDays: 30,
    prepareMethod: '',
    prepareBy: '',
    verifyBy: '',
    prepareDate: dayjs().format('YYYY-MM-DD'),
    status: 'PENDING',
    remark: ''
  })
  prepareDialogVisible.value = true
}

const handleViewPrepare = (row) => {
  isPrepareView.value = true
  prepareDialogTitle.value = '配制记录详情'
  Object.assign(prepareForm, { ...row })
  prepareDialogVisible.value = true
}

const handleSubmitPrepare = async () => {
  try {
    await prepareFormRef.value.validate()
    await standardMaterialApi.savePrepareRecord(prepareForm)
    ElMessage.success('保存成功')
    prepareDialogVisible.value = false
    fetchPrepareList()
  } catch (e) { console.error(e) }
}

const handleCalibrate = (row) => {
  Object.assign(calibrateForm, {
    id: null,
    calibrateNo: 'CAL-' + dayjs().format('YYYYMMDDHHmmss'),
    solutionId: row.id,
    solutionName: row.solutionName,
    calibrateMethod: '',
    calibrateConcentration: null,
    unit: row.unit,
    deviation: null,
    uncertainty: '',
    calibrateProcess: '',
    calibrateBy: '',
    verifyBy: '',
    calibrateDate: dayjs().format('YYYY-MM-DD'),
    remark: ''
  })
  calibrateDialogVisible.value = true
}

const handleViewCalibrate = (row) => {
  Object.assign(calibrateForm, { ...row })
  calibrateDialogVisible.value = true
}

const handleSubmitCalibrate = async () => {
  try {
    await calibrateFormRef.value.validate()
    await standardMaterialApi.saveCalibrateRecord(calibrateForm)
    ElMessage.success('标定完成')
    calibrateDialogVisible.value = false
    fetchPrepareList()
    fetchCalibrateList()
  } catch (e) { console.error(e) }
}

const handleTrace = async (row) => {
  currentMaterial.value = row
  traceLoading.value = true
  try {
    const res = await standardMaterialApi.getTraceTasks(row.id)
    if (res.data) {
      traceTasks.value = res.data
    } else {
      traceTasks.value = [
        { taskNo: 'T20250601001', sampleName: '水样-001', itemName: row.materialName.replace('标准溶液', ''), testResult: (Math.random() * 10).toFixed(2) + ' mg/L', testTime: '2025-06-01 10:30:00', operator: '张三', status: 'PASSED' },
        { taskNo: 'T20250602002', sampleName: '土样-005', itemName: row.materialName.replace('标准溶液', ''), testResult: (Math.random() * 100).toFixed(2) + ' mg/kg', testTime: '2025-06-02 14:20:00', operator: '李四', status: 'PENDING' }
      ]
    }
    traceDialogVisible.value = true
  } catch (e) {
    traceTasks.value = [
      { taskNo: 'T20250601001', sampleName: '水样-001', itemName: row.materialName.replace('标准溶液', ''), testResult: (Math.random() * 10).toFixed(2) + ' mg/L', testTime: '2025-06-01 10:30:00', operator: '张三', status: 'PASSED' }
    ]
    traceDialogVisible.value = true
  } finally { traceLoading.value = false }
}

const resetMaterialForm = () => {
  Object.assign(materialForm, {
    id: null, materialNo: '', materialName: '', materialType: '', certificateNo: '',
    batchNo: '', concentration: null, unit: '', uncertainty: '', stockQuantity: null,
    stockUnit: '', validDate: '', manufacturer: '', remark: ''
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
      &.solution .stat-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); }
      &.expired .stat-icon { background: linear-gradient(135deg, #fa709a, #fee140); }
    }
  }
  .toolbar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }
  .text-danger {
    color: #f56c6c;
    font-weight: 600;
  }
  .trace-header {
    padding: 12px;
    background: #f5f7fa;
    border-radius: 4px;
    margin-bottom: 16px;
    .trace-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 4px;
    }
    .trace-desc {
      font-size: 13px;
      color: #909399;
    }
  }
}
</style>
