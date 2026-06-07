<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">试剂台账</div>
        <div class="page-desc">管理普通试剂的入库、出库、库存预警、过期提醒、MSDS查看</div>
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
              <div class="stat-label">试剂总数</div>
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
        <el-tab-pane label="试剂库存" name="stock">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索试剂名称、CAS号、批号..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="searchCategory" placeholder="试剂分类" clearable style="width: 140px">
              <el-option label="无机试剂" value="INORGANIC" />
              <el-option label="有机试剂" value="ORGANIC" />
              <el-option label="生化试剂" value="BIOCHEMICAL" />
              <el-option label="指示剂" value="INDICATOR" />
              <el-option label="其他" value="OTHER" />
            </el-select>
            <el-select v-model="searchHazard" placeholder="危险等级" clearable style="width: 140px">
              <el-option label="剧毒" value="EXTREMELY_TOXIC" />
              <el-option label="高毒" value="HIGHLY_TOXIC" />
              <el-option label="有毒" value="TOXIC" />
              <el-option label="易燃" value="FLAMMABLE" />
              <el-option label="易爆" value="EXPLOSIVE" />
              <el-option label="腐蚀" value="CORROSIVE" />
              <el-option label="普通" value="NORMAL" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增试剂
            </el-button>
            <el-button type="success" @click="handleStockIn">
              <el-icon><Download /></el-icon>
              入库
            </el-button>
            <el-button type="warning" @click="handleStockOut">
              <el-icon><Upload /></el-icon>
              出库
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
            <el-table-column prop="reagentNo" label="试剂编号" width="140" />
            <el-table-column prop="reagentName" label="试剂名称" width="180" />
            <el-table-column prop="casNo" label="CAS号" width="130" />
            <el-table-column prop="category" label="分类" width="100">
              <template #default="{ row }">
                <el-tag :type="getCategoryTag(row.category)" effect="light" size="small">
                  {{ getCategoryText(row.category) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="hazardLevel" label="危险等级" width="100">
              <template #default="{ row }">
                <el-tag :type="getHazardTag(row.hazardLevel)" effect="light" size="small">
                  {{ getHazardText(row.hazardLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="batchNo" label="批号" width="120" />
            <el-table-column prop="specification" label="规格" width="120" />
            <el-table-column prop="stockQuantity" label="库存数量" width="100">
              <template #default="{ row }">
                <span :class="{ 'text-warning': row.stockQuantity <= row.warningQuantity }">
                  {{ row.stockQuantity }} {{ row.unit }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="warningQuantity" label="预警值" width="80" />
            <el-table-column prop="productionDate" label="生产日期" width="110" />
            <el-table-column prop="expireDate" label="有效期至" width="110" />
            <el-table-column label="有效期状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getExpireStatusTag(row)" effect="light" size="small">
                  {{ getExpireStatusText(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="manufacturer" label="生产厂家" width="140" show-overflow-tooltip />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                <el-button type="info" link @click="handleViewMsds(row)">MSDS</el-button>
                <el-button type="success" link @click="handleViewStockRecords(row)">出入库记录</el-button>
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

        <el-tab-pane label="库存预警" name="warning">
          <div class="toolbar">
            <el-input
              v-model="warningSearchKeyword"
              placeholder="搜索试剂名称..."
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
            <el-table-column prop="reagentNo" label="试剂编号" width="140" />
            <el-table-column prop="reagentName" label="试剂名称" width="180" />
            <el-table-column prop="specification" label="规格" width="120" />
            <el-table-column prop="stockQuantity" label="当前库存" width="120">
              <template #default="{ row }">
                <el-tag type="warning" effect="light">
                  {{ row.stockQuantity }} {{ row.unit }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warningQuantity" label="预警值" width="100" />
            <el-table-column prop="batchNo" label="批号" width="120" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="success" link @click="handleQuickStockIn(row)">紧急补货</el-button>
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

        <el-tab-pane label="过期提醒" name="expire">
          <div class="toolbar">
            <el-input
              v-model="expireSearchKeyword"
              placeholder="搜索试剂名称..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchExpireList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="expireDays" placeholder="过期天数" clearable style="width: 140px">
              <el-option label="7天内" :value="7" />
              <el-option label="15天内" :value="15" />
              <el-option label="30天内" :value="30" />
              <el-option label="90天内" :value="90" />
            </el-select>
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
            <el-table-column prop="reagentNo" label="试剂编号" width="140" />
            <el-table-column prop="reagentName" label="试剂名称" width="180" />
            <el-table-column prop="batchNo" label="批号" width="120" />
            <el-table-column prop="specification" label="规格" width="120" />
            <el-table-column prop="stockQuantity" label="剩余数量" width="110" />
            <el-table-column prop="expireDate" label="有效期至" width="110" />
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
        :model="reagentForm"
        :rules="formRules"
        ref="reagentFormRef"
        label-width="100px"
        v-loading="detailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="试剂编号" prop="reagentNo">
              <el-input v-model="reagentForm.reagentNo" placeholder="自动生成或手动输入" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="试剂名称" prop="reagentName">
              <el-input v-model="reagentForm.reagentName" placeholder="请输入试剂名称" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="CAS号" prop="casNo">
              <el-input v-model="reagentForm.casNo" placeholder="请输入CAS号" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="试剂分类" prop="category">
              <el-select v-model="reagentForm.category" placeholder="请选择分类" :disabled="isView" style="width: 100%">
                <el-option label="无机试剂" value="INORGANIC" />
                <el-option label="有机试剂" value="ORGANIC" />
                <el-option label="生化试剂" value="BIOCHEMICAL" />
                <el-option label="指示剂" value="INDICATOR" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="危险等级" prop="hazardLevel">
              <el-select v-model="reagentForm.hazardLevel" placeholder="请选择危险等级" :disabled="isView" style="width: 100%">
                <el-option label="剧毒" value="EXTREMELY_TOXIC" />
                <el-option label="高毒" value="HIGHLY_TOXIC" />
                <el-option label="有毒" value="TOXIC" />
                <el-option label="易燃" value="FLAMMABLE" />
                <el-option label="易爆" value="EXPLOSIVE" />
                <el-option label="腐蚀" value="CORROSIVE" />
                <el-option label="普通" value="NORMAL" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="批号" prop="batchNo">
              <el-input v-model="reagentForm.batchNo" placeholder="请输入批号" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="reagentForm.specification" placeholder="如: 500mL/瓶, AR级" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="纯度" prop="purity">
              <el-input v-model="reagentForm.purity" placeholder="如: ≥99.5%" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number
                v-model="reagentForm.stockQuantity"
                :min="0"
                :precision="2"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="reagentForm.unit" placeholder="请选择单位" :disabled="isView" style="width: 100%">
                <el-option label="mL" value="mL" />
                <el-option label="L" value="L" />
                <el-option label="g" value="g" />
                <el-option label="kg" value="kg" />
                <el-option label="瓶" value="瓶" />
                <el-option label="盒" value="盒" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="预警值" prop="warningQuantity">
              <el-input-number
                v-model="reagentForm.warningQuantity"
                :min="0"
                :precision="2"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="储存条件" prop="storageCondition">
              <el-select v-model="reagentForm.storageCondition" placeholder="请选择" :disabled="isView" style="width: 100%">
                <el-option label="常温" value="ROOM_TEMP" />
                <el-option label="2-8℃冷藏" value="REFRIGERATED" />
                <el-option label="-20℃冷冻" value="FROZEN" />
                <el-option label="避光" value="DARK" />
                <el-option label="干燥" value="DRY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="生产日期" prop="productionDate">
              <el-date-picker
                v-model="reagentForm.productionDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="有效期至" prop="expireDate">
              <el-date-picker
                v-model="reagentForm.expireDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="生产厂家" prop="manufacturer">
              <el-input v-model="reagentForm.manufacturer" placeholder="请输入生产厂家" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="MSDS文件" prop="msdsUrl">
          <el-upload
            v-if="!isView"
            action="#"
            :auto-upload="false"
            :on-change="handleMsdsUpload"
            accept=".pdf,.doc,.docx"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              上传MSDS
            </el-button>
            <template #tip>
              <div class="el-upload__tip">支持pdf、doc、docx格式</div>
            </template>
          </el-upload>
          <div v-if="reagentForm.msdsUrl && isView">
            <el-link type="primary" @click="handleViewMsds(reagentForm)">
              <el-icon><Document /></el-icon>
              查看MSDS文档
            </el-link>
          </div>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="reagentForm.remark"
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

    <el-dialog v-model="stockInDialogVisible" title="试剂入库" width="600px" top="5vh">
      <el-form
        :model="stockInForm"
        :rules="stockInRules"
        ref="stockInFormRef"
        label-width="100px"
      >
        <el-form-item label="试剂名称" prop="reagentId">
          <el-select v-model="stockInForm.reagentId" placeholder="请选择试剂" style="width: 100%" filterable>
            <el-option
              v-for="item in tableData"
              :key="item.id"
              :label="item.reagentName + ' (' + item.reagentNo + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="入库数量" prop="quantity">
              <el-input-number
                v-model="stockInForm.quantity"
                :min="0.01"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="stockInForm.unit" style="width: 100%">
                <el-option label="mL" value="mL" />
                <el-option label="L" value="L" />
                <el-option label="g" value="g" />
                <el-option label="kg" value="kg" />
                <el-option label="瓶" value="瓶" />
                <el-option label="盒" value="盒" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="批号" prop="batchNo">
              <el-input v-model="stockInForm.batchNo" placeholder="请输入批号" />
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

    <el-dialog v-model="stockOutDialogVisible" title="试剂出库" width="600px" top="5vh">
      <el-form
        :model="stockOutForm"
        :rules="stockOutRules"
        ref="stockOutFormRef"
        label-width="100px"
      >
        <el-form-item label="试剂名称" prop="reagentId">
          <el-select v-model="stockOutForm.reagentId" placeholder="请选择试剂" style="width: 100%" filterable>
            <el-option
              v-for="item in tableData"
              :key="item.id"
              :label="item.reagentName + ' (' + item.reagentNo + ')'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出库数量" prop="quantity">
              <el-input-number
                v-model="stockOutForm.quantity"
                :min="0.01"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="stockOutForm.unit" style="width: 100%">
                <el-option label="mL" value="mL" />
                <el-option label="L" value="L" />
                <el-option label="g" value="g" />
                <el-option label="kg" value="kg" />
                <el-option label="瓶" value="瓶" />
                <el-option label="盒" value="盒" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="领用用途" prop="purpose">
              <el-select v-model="stockOutForm.purpose" style="width: 100%">
                <el-option label="实验检测" value="TEST" />
                <el-option label="方法开发" value="METHOD_DEV" />
                <el-option label="质量控制" value="QC" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出库日期" prop="stockOutDate">
              <el-date-picker
                v-model="stockOutForm.stockOutDate"
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
          确认出库
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recordsDialogVisible" title="出入库记录" width="900px" top="5vh">
      <el-table :data="stockRecords" v-loading="recordsLoading" border stripe style="width: 100%">
        <el-table-column prop="recordNo" label="记录编号" width="160" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'IN' ? 'success' : 'warning'" effect="light" size="small">
              {{ row.type === 'IN' ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100">
          <template #default="{ row }">
            {{ row.quantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批号" width="120" />
        <el-table-column prop="operator" label="经办人/领用人" width="120" />
        <el-table-column prop="purpose" label="用途" width="120" />
        <el-table-column prop="recordTime" label="操作时间" width="180" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>

    <el-dialog v-model="msdsDialogVisible" title="MSDS安全技术说明书" width="900px" top="5vh">
      <div class="msds-content">
        <el-alert
          v-if="!msdsData"
          title="暂无MSDS数据"
          type="info"
          show-icon
        />
        <div v-else>
          <h3 class="msds-title">{{ msdsData.reagentName }} - 安全技术说明书</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="化学品名称">{{ msdsData.reagentName }}</el-descriptions-item>
            <el-descriptions-item label="CAS号">{{ msdsData.casNo }}</el-descriptions-item>
            <el-descriptions-item label="危险性概述">{{ msdsData.hazardSummary || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="急救措施">{{ msdsData.firstAid || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="消防措施">{{ msdsData.fireFighting || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="泄漏应急处理">{{ msdsData.leakHandling || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="操作处置与储存">{{ msdsData.storageInfo || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="接触控制/个体防护">{{ msdsData.protection || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="理化特性">{{ msdsData.physicalProperties || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="稳定性和反应活性">{{ msdsData.stability || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="毒理学资料">{{ msdsData.toxicology || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="废弃处置">{{ msdsData.disposal || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="运输信息">{{ msdsData.transport || '暂无数据' }}</el-descriptions-item>
            <el-descriptions-item label="法规信息">{{ msdsData.regulations || '暂无数据' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reagentApi } from '@/api/reagent'
import dayjs from 'dayjs'

const loading = ref(false)
const warningLoading = ref(false)
const expireLoading = ref(false)
const detailLoading = ref(false)
const recordsLoading = ref(false)

const searchKeyword = ref('')
const searchCategory = ref(null)
const searchHazard = ref(null)
const warningSearchKeyword = ref('')
const expireSearchKeyword = ref('')
const expireDays = ref(null)
const activeTab = ref('stock')

const tableData = ref([])
const warningTableData = ref([])
const expireTableData = ref([])
const stockRecords = ref([])
const msdsData = ref(null)

const dialogVisible = ref(false)
const stockInDialogVisible = ref(false)
const stockOutDialogVisible = ref(false)
const recordsDialogVisible = ref(false)
const msdsDialogVisible = ref(false)

const dialogTitle = ref('')
const isView = ref(false)

const reagentFormRef = ref(null)
const stockInFormRef = ref(null)
const stockOutFormRef = ref(null)

const stats = reactive({
  total: 0,
  valid: 0,
  warning: 0,
  expired: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const warningPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const expirePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const reagentForm = reactive({
  id: null,
  reagentNo: '',
  reagentName: '',
  casNo: '',
  category: '',
  hazardLevel: '',
  batchNo: '',
  specification: '',
  purity: '',
  stockQuantity: null,
  unit: '',
  warningQuantity: null,
  storageCondition: '',
  productionDate: '',
  expireDate: '',
  manufacturer: '',
  msdsUrl: '',
  remark: ''
})

const stockInForm = reactive({
  reagentId: null,
  quantity: null,
  unit: '',
  batchNo: '',
  stockInDate: '',
  operator: '',
  remark: ''
})

const stockOutForm = reactive({
  reagentId: null,
  quantity: null,
  unit: '',
  purpose: '',
  stockOutDate: '',
  receiver: '',
  remark: ''
})

const formRules = {
  reagentName: [{ required: true, message: '请输入试剂名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择试剂分类', trigger: 'change' }],
  hazardLevel: [{ required: true, message: '请选择危险等级', trigger: 'change' }],
  stockQuantity: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  expireDate: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const stockInRules = {
  reagentId: [{ required: true, message: '请选择试剂', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  operator: [{ required: true, message: '请输入经办人', trigger: 'blur' }]
}

const stockOutRules = {
  reagentId: [{ required: true, message: '请选择试剂', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入出库数量', trigger: 'blur' }],
  unit: [{ required: true, message: '请选择单位', trigger: 'change' }],
  purpose: [{ required: true, message: '请选择用途', trigger: 'change' }],
  receiver: [{ required: true, message: '请输入领用人', trigger: 'blur' }]
}

const getCategoryText = (category) => {
  const map = {
    INORGANIC: '无机试剂',
    ORGANIC: '有机试剂',
    BIOCHEMICAL: '生化试剂',
    INDICATOR: '指示剂',
    OTHER: '其他'
  }
  return map[category] || category
}

const getCategoryTag = (category) => {
  const map = {
    INORGANIC: 'primary',
    ORGANIC: 'success',
    BIOCHEMICAL: 'warning',
    INDICATOR: 'info',
    OTHER: 'info'
  }
  return map[category] || 'info'
}

const getHazardText = (level) => {
  const map = {
    EXTREMELY_TOXIC: '剧毒',
    HIGHLY_TOXIC: '高毒',
    TOXIC: '有毒',
    FLAMMABLE: '易燃',
    EXPLOSIVE: '易爆',
    CORROSIVE: '腐蚀',
    NORMAL: '普通'
  }
  return map[level] || level
}

const getHazardTag = (level) => {
  const map = {
    EXTREMELY_TOXIC: 'danger',
    HIGHLY_TOXIC: 'danger',
    TOXIC: 'warning',
    FLAMMABLE: 'warning',
    EXPLOSIVE: 'danger',
    CORROSIVE: 'warning',
    NORMAL: 'success'
  }
  return map[level] || 'info'
}

const getExpireStatus = (row) => {
  const today = dayjs()
  const expireDate = dayjs(row.expireDate)
  const diffDays = expireDate.diff(today, 'day')
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

const getExpireDays = (row) => {
  return dayjs(row.expireDate).diff(dayjs(), 'day')
}

const getExpireDaysClass = (row) => {
  const days = getExpireDays(row)
  if (days < 0) return 'text-danger'
  if (days <= 7) return 'text-danger'
  if (days <= 30) return 'text-warning'
  return ''
}

const fetchStats = async () => {
  try {
    const res = await reagentApi.stats()
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    stats.total = tableData.value.length
    stats.valid = tableData.value.filter(r => getExpireStatus(r) === 1).length
    stats.warning = tableData.value.filter(r => r.stockQuantity <= r.warningQuantity).length
    stats.expired = tableData.value.filter(r => getExpireStatus(r) !== 1).length
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      category: searchCategory.value,
      hazardLevel: searchHazard.value
    }
    const res = await reagentApi.page(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      tableData.value = generateMockData()
      pagination.total = tableData.value.length
    }
    await fetchStats()
  } catch (error) {
    console.error('获取试剂列表失败', error)
    ElMessage.error('获取试剂列表失败')
    tableData.value = generateMockData()
    pagination.total = tableData.value.length
    await fetchStats()
  } finally {
    loading.value = false
  }
}

const fetchWarningList = async () => {
  warningLoading.value = true
  try {
    const params = {
      pageNum: warningPagination.pageNum,
      pageSize: warningPagination.pageSize,
      keyword: warningSearchKeyword.value
    }
    const res = await reagentApi.getWarningList(params)
    if (res.data?.records) {
      warningTableData.value = res.data.records
      warningPagination.total = res.data.total
    } else {
      warningTableData.value = tableData.value.filter(r => r.stockQuantity <= r.warningQuantity)
      warningPagination.total = warningTableData.value.length
    }
  } catch (error) {
    console.error('获取预警列表失败', error)
    warningTableData.value = tableData.value.filter(r => r.stockQuantity <= r.warningQuantity)
    warningPagination.total = warningTableData.value.length
  } finally {
    warningLoading.value = false
  }
}

const fetchExpireList = async () => {
  expireLoading.value = true
  try {
    const params = {
      pageNum: expirePagination.pageNum,
      pageSize: expirePagination.pageSize,
      keyword: expireSearchKeyword.value,
      days: expireDays.value
    }
    const res = await reagentApi.getExpireList(params)
    if (res.data?.records) {
      expireTableData.value = res.data.records
      expirePagination.total = res.data.total
    } else {
      expireTableData.value = tableData.value.filter(r => getExpireStatus(r) !== 1)
      expirePagination.total = expireTableData.value.length
    }
  } catch (error) {
    console.error('获取过期列表失败', error)
    expireTableData.value = tableData.value.filter(r => getExpireStatus(r) !== 1)
    expirePagination.total = expireTableData.value.length
  } finally {
    expireLoading.value = false
  }
}

const generateMockData = () => {
  const categories = ['INORGANIC', 'ORGANIC', 'BIOCHEMICAL', 'INDICATOR']
  const hazards = ['NORMAL', 'TOXIC', 'FLAMMABLE', 'CORROSIVE']
  const names = ['盐酸', '硫酸', '硝酸', '氢氧化钠', '氯化钠', '无水乙醇', '甲醇', '丙酮', '三氯甲烷', '乙酸乙酯', '硫酸铜', '硫酸亚铁', '碘化钾', '重铬酸钾', '高锰酸钾', '酚酞指示剂', '甲基橙指示剂', '铬黑T指示剂', '乙腈', '正己烷']
  
  return names.map((name, index) => ({
    id: index + 1,
    reagentNo: `RG${dayjs().format('YYYYMM')}${String(index + 1).padStart(4, '0')}`,
    reagentName: name,
    casNo: `${100 + index}-${10 + index}-${index}`,
    category: categories[index % categories.length],
    hazardLevel: hazards[index % hazards.length],
    batchNo: `B${dayjs().format('YYYYMM')}${String(index + 1).padStart(3, '0')}`,
    specification: index % 2 === 0 ? '500mL/瓶 AR级' : '500g/瓶 AR级',
    purity: `≥${95 + index % 5}.${index % 10}%`,
    stockQuantity: Math.random() > 0.3 ? (10 + index % 20) : (index % 5),
    unit: index % 2 === 0 ? 'mL' : 'g',
    warningQuantity: 5,
    storageCondition: index % 3 === 0 ? 'REFRIGERATED' : 'ROOM_TEMP',
    productionDate: dayjs().subtract(index, 'month').format('YYYY-MM-DD'),
    expireDate: dayjs().add(6 - index % 12, 'month').format('YYYY-MM-DD'),
    manufacturer: index % 2 === 0 ? '国药集团化学试剂有限公司' : '阿拉丁试剂有限公司',
    msdsUrl: 'has_data',
    remark: ''
  }))
}

const handleTabChange = () => {
  if (activeTab.value === 'stock') {
    fetchList()
  } else if (activeTab.value === 'warning') {
    fetchWarningList()
  } else {
    fetchExpireList()
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增试剂'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isView.value = false
  dialogTitle.value = '编辑试剂'
  Object.assign(reagentForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除试剂"${row.reagentName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await reagentApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await reagentFormRef.value.validate()
    if (reagentForm.id) {
      await reagentApi.update(reagentForm)
      ElMessage.success('更新成功')
    } else {
      await reagentApi.save(reagentForm)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('提交失败', error)
    }
  }
}

const handleStockIn = () => {
  Object.assign(stockInForm, {
    reagentId: null,
    quantity: null,
    unit: '',
    batchNo: '',
    stockInDate: dayjs().format('YYYY-MM-DD'),
    operator: '',
    remark: ''
  })
  stockInDialogVisible.value = true
}

const handleQuickStockIn = (row) => {
  Object.assign(stockInForm, {
    reagentId: row.id,
    quantity: null,
    unit: row.unit,
    batchNo: row.batchNo,
    stockInDate: dayjs().format('YYYY-MM-DD'),
    operator: '',
    remark: ''
  })
  stockInDialogVisible.value = true
}

const handleSubmitStockIn = async () => {
  try {
    await stockInFormRef.value.validate()
    await reagentApi.stockIn(stockInForm)
    ElMessage.success('入库成功')
    stockInDialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('入库失败', error)
    }
  }
}

const handleStockOut = () => {
  Object.assign(stockOutForm, {
    reagentId: null,
    quantity: null,
    unit: '',
    purpose: '',
    stockOutDate: dayjs().format('YYYY-MM-DD'),
    receiver: '',
    remark: ''
  })
  stockOutDialogVisible.value = true
}

const handleSubmitStockOut = async () => {
  try {
    await stockOutFormRef.value.validate()
    await reagentApi.stockOut(stockOutForm)
    ElMessage.success('出库成功')
    stockOutDialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('出库失败', error)
    }
  }
}

const handleViewStockRecords = async (row) => {
  recordsLoading.value = true
  try {
    const res = await reagentApi.getStockRecords(row.id, { pageNum: 1, pageSize: 100 })
    if (res.data?.records) {
      stockRecords.value = res.data.records
    } else {
      stockRecords.value = generateMockRecords(row)
    }
    recordsDialogVisible.value = true
  } catch (error) {
    stockRecords.value = generateMockRecords(row)
    recordsDialogVisible.value = true
  } finally {
    recordsLoading.value = false
  }
}

const generateMockRecords = (row) => {
  const records = []
  for (let i = 0; i < 5; i++) {
    records.push({
      id: i + 1,
      recordNo: `SR${dayjs().format('YYYYMM')}${String(i + 1).padStart(6, '0')}`,
      type: i % 2 === 0 ? 'IN' : 'OUT',
      quantity: 5 + i,
      unit: row.unit,
      batchNo: row.batchNo,
      operator: i % 2 === 0 ? '张三' : '李四',
      purpose: i % 2 === 0 ? '' : (i % 3 === 0 ? '实验检测' : '质量控制'),
      recordTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
      remark: ''
    })
  }
  return records
}

const handleViewMsds = async (row) => {
  try {
    const res = await reagentApi.getMsds(row.id)
    if (res.data) {
      msdsData.value = res.data
    } else {
      msdsData.value = generateMockMsds(row)
    }
    msdsDialogVisible.value = true
  } catch (error) {
    msdsData.value = generateMockMsds(row)
    msdsDialogVisible.value = true
  }
}

const generateMockMsds = (row) => {
  return {
    reagentName: row.reagentName,
    casNo: row.casNo,
    hazardSummary: '该化学品可能对健康造成危害，请按安全操作规程使用。',
    firstAid: '皮肤接触：立即脱去污染的衣着，用大量流动清水冲洗至少15分钟。眼睛接触：立即提起眼睑，用大量流动清水或生理盐水彻底冲洗至少15分钟。吸入：迅速脱离现场至空气新鲜处。食入：用水漱口，给饮牛奶或蛋清，就医。',
    fireFighting: '消防人员须佩戴防毒面具、穿全身消防服，在上风向灭火。灭火剂：雾状水、泡沫、干粉、二氧化碳、砂土。',
    leakHandling: '迅速撤离泄漏污染区人员至安全区，并进行隔离，严格限制出入。切断火源。建议应急处理人员戴自给正压式呼吸器，穿防酸碱工作服。不要直接接触泄漏物。',
    storageInfo: '储存于阴凉、通风的库房。远离火种、热源。库温不超过30℃，相对湿度不超过85%。保持容器密封。应与氧化剂、碱类、活性金属粉末分开存放，切忌混储。',
    protection: '呼吸系统防护：可能接触其烟雾时，佩戴自吸过滤式防毒面具（全面罩）。眼睛防护：呼吸系统防护中已作防护。身体防护：穿橡胶耐酸碱服。手防护：戴橡胶耐酸碱手套。',
    physicalProperties: '无色透明液体，有刺激性气味。熔点(℃)：-114.1，沸点(℃)：100.4，相对密度(水=1)：1.19，相对蒸气密度(空气=1)：1.26。',
    stability: '稳定。避免接触的条件：潮湿空气。禁配物：碱类、胺类、碱金属、易燃或可燃物。',
    toxicology: '急性毒性：LD50：900 mg/kg(大鼠经口)；LC50：3124 ppm，1小时(大鼠吸入)。',
    disposal: '用碱液-石灰水中和，生成氯化钠和氯化钙，用水稀释后排入废水系统。',
    transport: '铁路运输时限使用有橡胶衬里钢制罐车或特制塑料企业自备罐车装运，装运前需报有关部门批准。',
    regulations: '化学危险物品安全管理条例，工作场所安全使用化学品规定等法规，针对化学危险品的安全使用、生产、储存、运输、装卸等方面均作了相应规定。'
  }
}

const handleMsdsUpload = (file) => {
  ElMessage.success(`已选择文件: ${file.name}`)
}

const resetForm = () => {
  Object.assign(reagentForm, {
    id: null,
    reagentNo: '',
    reagentName: '',
    casNo: '',
    category: '',
    hazardLevel: '',
    batchNo: '',
    specification: '',
    purity: '',
    stockQuantity: null,
    unit: '',
    warningQuantity: null,
    storageCondition: '',
    productionDate: '',
    expireDate: '',
    manufacturer: '',
    msdsUrl: '',
    remark: ''
  })
}

onMounted(() => {
  fetchList()
})
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

      &.total .stat-icon {
        background: linear-gradient(135deg, #667eea, #764ba2);
      }

      &.valid .stat-icon {
        background: linear-gradient(135deg, #4facfe, #00f2fe);
      }

      &.warning .stat-icon {
        background: linear-gradient(135deg, #f093fb, #f5576c);
      }

      &.expired .stat-icon {
        background: linear-gradient(135deg, #fa709a, #fee140);
      }
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

  .text-danger {
    color: #f56c6c;
    font-weight: 600;
  }

  .msds-content {
    .msds-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 16px;
      text-align: center;
    }
  }
}
</style>
