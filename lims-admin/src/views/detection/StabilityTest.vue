<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">稳定性考察管理</div>
      <div class="page-desc">长期稳定性和加速稳定性考察方案管理，支持趋势分析和保质期预估</div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总方案数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card active" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">进行中</div>
              <div class="stat-value">{{ stats.inProgress || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card completed" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已完成</div>
              <div class="stat-value">{{ stats.completed || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card pending" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><AlarmClock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待检测</div>
              <div class="stat-value">{{ stats.pending || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="长期稳定性" name="long_term" />
        <el-tab-pane label="加速稳定性" name="accelerated" />
      </el-tabs>

      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索方案编号、名称、样品名称..."
          style="width: 260px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchType" placeholder="方案类型" clearable style="width: 140px">
          <el-option label="长期稳定性" value="long_term" />
          <el-option label="加速稳定性" value="accelerated" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 140px">
          <el-option label="未开始" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增方案
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
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="schemeNo" label="方案编号" width="160" />
        <el-table-column prop="schemeName" label="方案名称" width="200" />
        <el-table-column prop="sampleName" label="样品名称" width="150" />
        <el-table-column prop="itemName" label="检测项目" width="150" />
        <el-table-column prop="schemeType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.schemeType === 'long_term' ? 'primary' : 'warning'" effect="light" size="small">
              {{ row.schemeType === 'long_term' ? '长期稳定性' : '加速稳定性' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="durationDays" label="总考察天数" width="120">
          <template #default="{ row }">
            {{ row.durationDays }} 天
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handleStart(row)">
              <el-icon><VideoPlay /></el-icon>
              启动
            </el-button>
            <el-button v-if="row.status === 1" type="warning" link @click="handleRecordResult(row)">
              <el-icon><EditPen /></el-icon>
              录入结果
            </el-button>
            <el-button type="info" link @click="handleViewTrend(row)">
              <el-icon><TrendCharts /></el-icon>
              查看趋势
            </el-button>
            <el-button v-if="row.status === 1" type="success" link @click="handleEstimate(row)">
              <el-icon><MagicStick /></el-icon>
              预估保质期
            </el-button>
            <el-button type="primary" link @click="handleGenerateReport(row)">
              <el-icon><Document /></el-icon>
              生成报告
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
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
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="900px" top="3vh">
      <el-form
        :model="schemeForm"
        :rules="formRules"
        ref="schemeFormRef"
        label-width="120px"
        v-loading="detailLoading"
      >
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="方案名称" prop="schemeName">
              <el-input v-model="schemeForm.schemeName" placeholder="请输入方案名称" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="样品名称" prop="sampleName">
              <el-input v-model="schemeForm.sampleName" placeholder="请输入样品名称" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="样品类型" prop="sampleType">
              <el-input v-model="schemeForm.sampleType" placeholder="请输入样品类型" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测项目" prop="itemName">
              <el-input v-model="schemeForm.itemName" placeholder="请输入检测项目" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="schemeForm.unit" placeholder="请输入单位" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="初始值" prop="initialValue">
              <el-input-number
                v-model="schemeForm.initialValue"
                :precision="4"
                :step="0.01"
                :min="0"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="可接受偏差(%)" prop="acceptableDeviation">
              <el-input-number
                v-model="schemeForm.acceptableDeviation"
                :precision="2"
                :step="0.1"
                :min="0"
                :max="100"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">考察设置</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="考察类型" prop="schemeType">
              <el-select v-model="schemeForm.schemeType" placeholder="请选择类型" :disabled="isView" style="width: 100%">
                <el-option label="长期稳定性" value="long_term" />
                <el-option label="加速稳定性" value="accelerated" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="储存条件" prop="storageCondition">
              <el-input v-model="schemeForm.storageCondition" placeholder="请输入储存条件" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="温度(°C)" prop="temperature">
              <el-input-number
                v-model="schemeForm.temperature"
                :precision="1"
                :step="0.5"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="湿度(%RH)" prop="humidity">
              <el-input-number
                v-model="schemeForm.humidity"
                :precision="1"
                :step="1"
                :min="0"
                :max="100"
                style="width: 100%"
                :disabled="isView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总考察天数" prop="durationDays">
              <el-input-number
                v-model="schemeForm.durationDays"
                :step="1"
                :min="1"
                style="width: 100%"
                :disabled="isView"
                @change="autoGenerateTimePoints"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">检测时间点</el-divider>
        <el-form-item label="时间点设置">
          <div class="time-point-config">
            <el-table
              :data="schemeForm.timePoints"
              border
              size="small"
              style="width: 100%"
            >
              <el-table-column label="点序号" width="100">
                <template #default="{ $index }">
                  {{ $index + 1 }}
                </template>
              </el-table-column>
              <el-table-column label="考察天数" width="150">
                <template #default="{ row }">
                  <el-input-number
                    v-model="row.day"
                    :step="1"
                    :min="0"
                    style="width: 100%"
                    :disabled="isView"
                    @change="updatePlanDate(row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="计划日期">
                <template #default="{ row }">
                  <el-date-picker
                    v-model="row.planDate"
                    type="date"
                    value-format="YYYY-MM-DD"
                    placeholder="选择日期"
                    style="width: 100%"
                    :disabled="isView"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" v-if="!isView">
                <template #default="{ $index }">
                  <el-button type="danger" link @click="removeTimePoint($index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button
              v-if="!isView"
              type="primary"
              size="small"
              style="margin-top: 8px"
              @click="addTimePoint"
            >
              <el-icon><Plus /></el-icon>
              添加时间点
            </el-button>
            <el-button
              v-if="!isView"
              type="warning"
              size="small"
              style="margin-top: 8px; margin-left: 8px"
              @click="autoGenerateTimePoints"
            >
              <el-icon><MagicStick /></el-icon>
              自动生成
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="schemeForm.remark"
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

    <el-dialog v-model="recordDialogVisible" title="录入检测结果" width="700px" top="5vh">
      <div v-loading="recordLoading">
        <el-alert
          v-if="currentScheme"
          :title="`方案: ${currentScheme.schemeName} - ${currentScheme.sampleName}`"
          type="info"
          show-icon
          style="margin-bottom: 16px"
        />
        <el-alert
          v-if="currentScheme"
          :title="`初始值: ${currentScheme.initialValue} ${currentScheme.unit}, 可接受偏差: ±${currentScheme.acceptableDeviation}%`"
          type="warning"
          show-icon
          style="margin-bottom: 16px"
        />

        <el-table
          :data="timePointsWithResult"
          border
          stripe
          style="width: 100%; margin-bottom: 16px"
          @row-click="handleSelectTimePoint"
          highlight-current-row
          :row-class-name="timePointRowClassName"
        >
          <el-table-column label="点序号" width="80">
            <template #default="{ $index }">
              {{ $index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="day" label="考察天数" width="100" />
          <el-table-column prop="planDate" label="计划日期" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'" effect="light" size="small">
                {{ row.status === 1 ? '已检测' : '待检测' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="value1" label="平行样1" width="100">
            <template #default="{ row }">
              {{ row.value1 || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="value2" label="平行样2" width="100">
            <template #default="{ row }">
              {{ row.value2 || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="avgValue" label="平均值" width="100">
            <template #default="{ row }">
              {{ row.avgValue ? row.avgValue.toFixed(4) : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="rsd" label="RSD(%)" width="100">
            <template #default="{ row }">
              <span :style="{ color: row.rsd && row.rsd > 2 ? '#F56C6C' : '#606266' }">
                {{ row.rsd ? row.rsd.toFixed(2) : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="deviationRate" label="偏差率(%)" width="110">
            <template #default="{ row }">
              <span :style="{ color: isDeviationQualified(row) ? '#67C23A' : '#F56C6C' }">
                {{ row.deviationRate !== null && row.deviationRate !== undefined ? row.deviationRate.toFixed(2) : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="qualified" label="是否合格" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.qualified !== null && row.qualified !== undefined" :type="row.qualified ? 'success' : 'danger'" effect="light" size="small">
                {{ row.qualified ? '合格' : '不合格' }}
              </el-tag>
              <span v-else style="color: #909399">-</span>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">录入结果</el-divider>
        <el-form :model="recordForm" :rules="recordRules" ref="recordFormRef" label-width="100px">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="平行样1" prop="value1">
                <el-input-number
                  v-model="recordForm.value1"
                  :precision="4"
                  :step="0.001"
                  :min="0"
                  style="width: 100%"
                  @change="calculateRecord"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="平行样2" prop="value2">
                <el-input-number
                  v-model="recordForm.value2"
                  :precision="4"
                  :step="0.001"
                  :min="0"
                  style="width: 100%"
                  @change="calculateRecord"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="检测人" prop="operator">
                <el-input v-model="recordForm.operator" placeholder="请输入检测人" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="6">
              <el-form-item label="平均值">
                <el-input :value="recordForm.avgValue ? recordForm.avgValue.toFixed(4) : '-'" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="RSD(%)">
                <el-input
                  :value="recordForm.rsd !== null && recordForm.rsd !== undefined ? recordForm.rsd.toFixed(2) : '-'"
                  :style="{ color: recordForm.rsd && recordForm.rsd > 2 ? '#F56C6C' : '#606266' }"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="偏差率(%)">
                <el-input
                  :value="recordForm.deviationRate !== null && recordForm.deviationRate !== undefined ? recordForm.deviationRate.toFixed(2) : '-'"
                  :style="{ color: isRecordDeviationQualified() ? '#67C23A' : '#F56C6C' }"
                  disabled
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="是否合格">
                <el-tag :type="isRecordDeviationQualified() ? 'success' : 'danger'" effect="light">
                  {{ isRecordDeviationQualified() ? '合格' : '不合格' }}
                </el-tag>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注">
            <el-input
              v-model="recordForm.remark"
              type="textarea"
              :rows="2"
              placeholder="请输入备注"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="recordDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSubmitRecord">
          <el-icon><Check /></el-icon>
          保存结果
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="trendDialogVisible" title="趋势分析" width="1000px" top="3vh">
      <div v-loading="trendLoading">
        <el-alert
          v-if="currentScheme"
          :title="`方案: ${currentScheme.schemeName} - ${currentScheme.sampleName} - ${currentScheme.itemName}`"
          type="info"
          show-icon
          style="margin-bottom: 16px"
        />
        <el-alert
          v-if="currentScheme"
          :title="`初始值: ${currentScheme.initialValue} ${currentScheme.unit}, 可接受偏差: ±${currentScheme.acceptableDeviation}%`"
          type="warning"
          show-icon
          style="margin-bottom: 16px"
        />

        <div ref="trendChartRef" class="chart-container"></div>

        <el-divider content-position="left">检测点详情</el-divider>
        <el-table
          :data="trendTimePoints"
          border
          stripe
          style="width: 100%; margin-top: 16px"
          :row-class-name="timePointRowClassName"
        >
          <el-table-column label="点序号" width="80">
            <template #default="{ $index }">
              {{ $index + 1 }}
            </template>
          </el-table-column>
          <el-table-column prop="day" label="考察天数" width="100" />
          <el-table-column prop="planDate" label="计划日期" width="120" />
          <el-table-column prop="testDate" label="检测日期" width="120">
            <template #default="{ row }">
              {{ row.testDate || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'" effect="light" size="small">
                {{ row.status === 1 ? '已检测' : '待检测' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="value1" label="平行样1" width="100">
            <template #default="{ row }">
              {{ row.value1 || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="value2" label="平行样2" width="100">
            <template #default="{ row }">
              {{ row.value2 || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="avgValue" label="平均值" width="110">
            <template #default="{ row }">
              {{ row.avgValue ? row.avgValue.toFixed(4) : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="rsd" label="RSD(%)" width="100">
            <template #default="{ row }">
              <span :style="{ color: row.rsd && row.rsd > 2 ? '#F56C6C' : '#606266' }">
                {{ row.rsd ? row.rsd.toFixed(2) : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="deviationRate" label="偏差率(%)" width="110">
            <template #default="{ row }">
              <span :style="{ color: isDeviationQualified(row) ? '#67C23A' : '#F56C6C' }">
                {{ row.deviationRate !== null && row.deviationRate !== undefined ? row.deviationRate.toFixed(2) : '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="qualified" label="是否合格" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.qualified !== null && row.qualified !== undefined" :type="row.qualified ? 'success' : 'danger'" effect="light" size="small">
                {{ row.qualified ? '合格' : '不合格' }}
              </el-tag>
              <span v-else style="color: #909399">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="operator" label="检测人" width="100">
            <template #default="{ row }">
              {{ row.operator || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.remark || '-' }}
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="trendDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="estimateDialogVisible" title="保质期预估" width="500px" top="5vh">
      <div v-loading="estimateLoading">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预估保质期">
            <span style="font-size: 24px; font-weight: bold; color: #409EFF">
              {{ estimateResult.days || '-' }}
            </span>
            <span style="margin-left: 8px; color: #606266">天</span>
          </el-descriptions-item>
          <el-descriptions-item label="预估日期">
            {{ estimateResult.estimateDate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="置信度">
            <el-tag :type="estimateResult.confidence && estimateResult.confidence >= 90 ? 'success' : 'warning'" effect="light">
              {{ estimateResult.confidence ? estimateResult.confidence + '%' : '-' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">建议</el-divider>
        <el-alert
          :title="estimateResult.suggestion || '正在计算预估结果...'"
          type="info"
          show-icon
          :closable="false"
        />

        <el-divider content-position="left">计算依据</el-divider>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="初始值">
            {{ currentScheme?.initialValue }} {{ currentScheme?.unit }}
          </el-descriptions-item>
          <el-descriptions-item label="可接受偏差">
            ±{{ currentScheme?.acceptableDeviation }}%
          </el-descriptions-item>
          <el-descriptions-item label="合格下限">
            {{ estimateResult.lowerLimit ? estimateResult.lowerLimit.toFixed(4) : '-' }} {{ currentScheme?.unit }}
          </el-descriptions-item>
          <el-descriptions-item label="合格上限">
            {{ estimateResult.upperLimit ? estimateResult.upperLimit.toFixed(4) : '-' }} {{ currentScheme?.unit }}
          </el-descriptions-item>
          <el-descriptions-item label="已检测点数">
            {{ estimateResult.testedPoints || 0 }}
          </el-descriptions-item>
          <el-descriptions-item label="回归方程">
            {{ estimateResult.regressionEquation || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <el-button @click="estimateDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { stabilityApi } from '@/api/detection'
import dayjs from 'dayjs'

const loading = ref(false)
const detailLoading = ref(false)
const recordLoading = ref(false)
const trendLoading = ref(false)
const estimateLoading = ref(false)

const searchKeyword = ref('')
const searchType = ref(null)
const searchStatus = ref(null)
const activeTab = ref('long_term')

const tableData = ref([])
const dialogVisible = ref(false)
const recordDialogVisible = ref(false)
const trendDialogVisible = ref(false)
const estimateDialogVisible = ref(false)

const dialogTitle = ref('')
const isView = ref(false)
const schemeFormRef = ref(null)
const recordFormRef = ref(null)
const currentScheme = ref(null)
const selectedTimePoint = ref(null)
const trendChartRef = ref(null)
let trendChart = null

const stats = reactive({
  total: 0,
  inProgress: 0,
  completed: 0,
  pending: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const schemeForm = reactive({
  id: null,
  schemeNo: '',
  schemeName: '',
  sampleName: '',
  sampleType: '',
  itemName: '',
  unit: '',
  initialValue: null,
  acceptableDeviation: 5,
  schemeType: 'long_term',
  storageCondition: '',
  temperature: null,
  humidity: null,
  durationDays: 365,
  timePoints: [],
  remark: ''
})

const formRules = {
  schemeName: [{ required: true, message: '请输入方案名称', trigger: 'blur' }],
  sampleName: [{ required: true, message: '请输入样品名称', trigger: 'blur' }],
  itemName: [{ required: true, message: '请输入检测项目', trigger: 'blur' }],
  initialValue: [{ required: true, message: '请输入初始值', trigger: 'blur' }],
  acceptableDeviation: [{ required: true, message: '请输入可接受偏差', trigger: 'blur' }],
  schemeType: [{ required: true, message: '请选择考察类型', trigger: 'change' }],
  durationDays: [{ required: true, message: '请输入总考察天数', trigger: 'blur' }]
}

const recordForm = reactive({
  pointId: null,
  value1: null,
  value2: null,
  operator: '',
  remark: '',
  avgValue: null,
  rsd: null,
  deviationRate: null,
  qualified: null
})

const recordRules = {
  value1: [{ required: true, message: '请输入平行样1检测值', trigger: 'blur' }],
  value2: [{ required: true, message: '请输入平行样2检测值', trigger: 'blur' }],
  operator: [{ required: true, message: '请输入检测人', trigger: 'blur' }]
}

const timePointsWithResult = ref([])
const trendTimePoints = ref([])

const estimateResult = reactive({
  days: null,
  estimateDate: '',
  confidence: null,
  suggestion: '',
  lowerLimit: null,
  upperLimit: null,
  testedPoints: 0,
  regressionEquation: ''
})

const getStatusText = (status) => {
  const map = { 0: '未开始', 1: '进行中', 2: '已完成', 3: '已取消' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { 0: 'info', 1: 'primary', 2: 'success', 3: 'info' }
  return map[status] || 'info'
}

const isOverdue = (planDate) => {
  if (!planDate) return false
  return dayjs().isAfter(dayjs(planDate), 'day')
}

const tableRowClassName = ({ row }) => {
  if (row.status === 1) {
    const today = dayjs()
    const hasOverduePoint = row.timePoints?.some(p => 
      p.status !== 1 && p.planDate && dayjs(p.planDate).isBefore(today, 'day')
    )
    if (hasOverduePoint) {
      return 'overdue-row'
    }
  }
  return ''
}

const timePointRowClassName = ({ row }) => {
  if (row.status !== 1 && isOverdue(row.planDate)) {
    return 'overdue-row'
  }
  return ''
}

const isDeviationQualified = (row) => {
  if (row.deviationRate === null || row.deviationRate === undefined) return true
  const absDeviation = Math.abs(row.deviationRate)
  const acceptable = currentScheme.value?.acceptableDeviation || schemeForm.acceptableDeviation
  return absDeviation <= acceptable
}

const isRecordDeviationQualified = () => {
  if (recordForm.deviationRate === null || recordForm.deviationRate === undefined) return true
  const absDeviation = Math.abs(recordForm.deviationRate)
  const acceptable = currentScheme.value?.acceptableDeviation || 5
  return absDeviation <= acceptable
}

const calculateRecord = () => {
  if (recordForm.value1 !== null && recordForm.value2 !== null) {
    recordForm.avgValue = (recordForm.value1 + recordForm.value2) / 2
    
    if (recordForm.avgValue !== 0) {
      recordForm.rsd = Math.abs(recordForm.value1 - recordForm.value2) / recordForm.avgValue * 100
    } else {
      recordForm.rsd = 0
    }
    
    const initialValue = currentScheme.value?.initialValue || 0
    if (initialValue !== 0) {
      recordForm.deviationRate = (recordForm.avgValue - initialValue) / initialValue * 100
    } else {
      recordForm.deviationRate = 0
    }
    
    recordForm.qualified = isRecordDeviationQualified()
  } else {
    recordForm.avgValue = null
    recordForm.rsd = null
    recordForm.deviationRate = null
    recordForm.qualified = null
  }
}

const fetchStats = async () => {
  try {
    const res = await stabilityApi.getStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.inProgress = res.data.inProgress || 0
      stats.completed = res.data.completed || 0
      stats.pending = res.data.pending || 0
    }
  } catch (error) {
    stats.total = tableData.value.length
    stats.inProgress = tableData.value.filter(r => r.status === 1).length
    stats.completed = tableData.value.filter(r => r.status === 2).length
    stats.pending = tableData.value.filter(r => {
      if (r.status !== 1) return false
      const today = dayjs()
      return r.timePoints?.some(p => 
        p.status !== 1 && p.planDate && dayjs(p.planDate).isBefore(today, 'day')
      )
    }).length
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      schemeType: searchType.value || activeTab.value,
      status: searchStatus.value
    }
    const res = await stabilityApi.page(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      tableData.value = []
      pagination.total = 0
    }
    await fetchStats()
  } catch (error) {
    console.error('获取方案列表失败', error)
    ElMessage.error('获取方案列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pagination.pageNum = 1
  fetchList()
}

const initSchemeForm = () => {
  Object.assign(schemeForm, {
    id: null,
    schemeNo: 'STS-' + dayjs().format('YYYYMMDDHHmmss'),
    schemeName: '',
    sampleName: '',
    sampleType: '',
    itemName: '',
    unit: '',
    initialValue: null,
    acceptableDeviation: 5,
    schemeType: activeTab.value,
    storageCondition: '',
    temperature: 25,
    humidity: 60,
    durationDays: 365,
    timePoints: [],
    remark: ''
  })
  autoGenerateTimePoints()
}

const autoGenerateTimePoints = () => {
  if (!schemeForm.durationDays) return
  
  const defaultPoints = [0, 30, 90, 180, 365]
  if (schemeForm.durationDays >= 730) {
    defaultPoints.push(730)
  }
  if (schemeForm.durationDays >= 1095) {
    defaultPoints.push(1095)
  }
  
  const startDate = dayjs()
  schemeForm.timePoints = defaultPoints
    .filter(d => d <= schemeForm.durationDays)
    .map(day => ({
      day: day,
      planDate: startDate.add(day, 'day').format('YYYY-MM-DD'),
      status: 0
    }))
}

const addTimePoint = () => {
  const maxDay = schemeForm.timePoints.length > 0 
    ? Math.max(...schemeForm.timePoints.map(p => p.day)) 
    : 0
  const nextDay = maxDay + 30
  schemeForm.timePoints.push({
    day: nextDay,
    planDate: dayjs().add(nextDay, 'day').format('YYYY-MM-DD'),
    status: 0
  })
}

const removeTimePoint = (index) => {
  schemeForm.timePoints.splice(index, 1)
}

const updatePlanDate = (row) => {
  if (row.day !== null && row.day !== undefined) {
    row.planDate = dayjs().add(row.day, 'day').format('YYYY-MM-DD')
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增稳定性考察方案'
  initSchemeForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑稳定性考察方案'
  detailLoading.value = true
  try {
    const res = await stabilityApi.getDetail(row.id)
    if (res.data) {
      Object.assign(schemeForm, {
        ...res.data,
        timePoints: res.data.timePoints ? [...res.data.timePoints] : []
      })
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取方案详情失败', error)
    ElMessage.error('获取方案详情失败')
  } finally {
    detailLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除方案"${row.schemeName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await stabilityApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('删除失败', error)
    }
  }
}

const handleStart = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要启动方案"${row.schemeName}"吗？启动后将开始计时。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await stabilityApi.start(row.id)
    row.status = 1
    ElMessage.success('方案已启动')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('启动失败', error)
    }
  }
}

const handleRecordResult = async (row) => {
  recordLoading.value = true
  currentScheme.value = row
  selectedTimePoint.value = null
  try {
    const res = await stabilityApi.getDetail(row.id)
    if (res.data) {
      currentScheme.value = res.data
      timePointsWithResult.value = res.data.timePoints || []
      
      const pendingPoints = timePointsWithResult.value.filter(p => p.status !== 1)
      if (pendingPoints.length > 0) {
        selectedTimePoint.value = pendingPoints[0]
        handleSelectTimePoint(selectedTimePoint.value)
      }
      
      recordDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取方案详情失败', error)
    ElMessage.error('获取方案详情失败')
  } finally {
    recordLoading.value = false
  }
}

const handleSelectTimePoint = (row) => {
  selectedTimePoint.value = row
  Object.assign(recordForm, {
    pointId: row.id,
    value1: row.value1 || null,
    value2: row.value2 || null,
    operator: row.operator || '',
    remark: row.remark || '',
    avgValue: row.avgValue || null,
    rsd: row.rsd || null,
    deviationRate: row.deviationRate || null,
    qualified: row.qualified || null
  })
}

const handleSubmitRecord = async () => {
  try {
    await recordFormRef.value.validate()
    if (!selectedTimePoint.value) {
      ElMessage.warning('请先选择要录入的时间点')
      return
    }
    
    const data = {
      value1: recordForm.value1,
      value2: recordForm.value2,
      operator: recordForm.operator,
      remark: recordForm.remark,
      avgValue: recordForm.avgValue,
      rsd: recordForm.rsd,
      deviationRate: recordForm.deviationRate,
      qualified: recordForm.qualified
    }
    
    await stabilityApi.recordResult(selectedTimePoint.value.id, data)
    ElMessage.success('结果录入成功')
    
    selectedTimePoint.value.status = 1
    selectedTimePoint.value.value1 = recordForm.value1
    selectedTimePoint.value.value2 = recordForm.value2
    selectedTimePoint.value.avgValue = recordForm.avgValue
    selectedTimePoint.value.rsd = recordForm.rsd
    selectedTimePoint.value.deviationRate = recordForm.deviationRate
    selectedTimePoint.value.qualified = recordForm.qualified
    selectedTimePoint.value.operator = recordForm.operator
    selectedTimePoint.value.remark = recordForm.remark
    selectedTimePoint.value.testDate = dayjs().format('YYYY-MM-DD')
    
    const nextPending = timePointsWithResult.value.find(p => p.status !== 1)
    if (nextPending) {
      handleSelectTimePoint(nextPending)
    } else {
      recordDialogVisible.value = false
      fetchList()
    }
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('保存结果失败', error)
    }
  }
}

const handleViewTrend = async (row) => {
  trendLoading.value = true
  currentScheme.value = row
  try {
    const res = await stabilityApi.getTrend(row.id)
    if (res.data) {
      trendTimePoints.value = res.data.timePoints || []
      trendDialogVisible.value = true
      nextTick(() => {
        renderTrendChart(res.data)
      })
    }
  } catch (error) {
    console.error('获取趋势数据失败', error)
    ElMessage.error('获取趋势数据失败')
  } finally {
    trendLoading.value = false
  }
}

const renderTrendChart = (data) => {
  if (!trendChartRef.value) return
  
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  
  const initialValue = currentScheme.value?.initialValue || 0
  const acceptableDeviation = currentScheme.value?.acceptableDeviation || 5
  const unit = currentScheme.value?.unit || ''
  
  const upperLimit = initialValue * (1 + acceptableDeviation / 100)
  const lowerLimit = initialValue * (1 - acceptableDeviation / 100)
  
  const testedPoints = (data.timePoints || []).filter(p => p.status === 1 && p.avgValue !== null && p.avgValue !== undefined)
  const xData = testedPoints.map(p => p.day + '天')
  const yData = testedPoints.map(p => p.avgValue)
  
  const markAreaData = [
    [{ name: '合格范围', yAxis: upperLimit, itemStyle: { color: 'rgba(67, 233, 123, 0.1)' } }, { yAxis: lowerLimit }]
  ]
  
  const markLines = [
    { yAxis: initialValue, label: { formatter: `初始值: ${initialValue}`, position: 'end' }, lineStyle: { color: '#303133', type: 'solid', width: 2 } },
    { yAxis: upperLimit, label: { formatter: `上限: ${upperLimit.toFixed(4)} (+${acceptableDeviation}%)`, position: 'end' }, lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 } },
    { yAxis: lowerLimit, label: { formatter: `下限: ${lowerLimit.toFixed(4)} (-${acceptableDeviation}%)`, position: 'end' }, lineStyle: { color: '#E6A23C', type: 'dashed', width: 1 } }
  ]
  
  const scatterData = []
  testedPoints.forEach((p, i) => {
    if (p.qualified === false) {
      scatterData.push({
        name: '不合格',
        value: [xData[i], p.avgValue],
        itemStyle: { color: '#F56C6C' },
        symbolSize: 12
      })
    }
  })
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const idx = params[0].dataIndex
        const p = testedPoints[idx]
        let html = `<div style="font-weight: bold; margin-bottom: 8px">第${p.day}天</div>`
        html += `<div>检测日期: ${p.testDate || '-'}</div>`
        html += `<div>平行样1: ${p.value1 || '-'}</div>`
        html += `<div>平行样2: ${p.value2 || '-'}</div>`
        html += `<div>平均值: ${p.avgValue ? p.avgValue.toFixed(4) : '-'} ${unit}</div>`
        html += `<div>RSD: ${p.rsd ? p.rsd.toFixed(2) : '-'}%</div>`
        html += `<div>偏差率: ${p.deviationRate !== null && p.deviationRate !== undefined ? p.deviationRate.toFixed(2) : '-'}%</div>`
        html += `<div style="margin-top: 8px; color: ${p.qualified ? '#67C23A' : '#F56C6C'}">状态: ${p.qualified ? '合格' : '不合格'}</div>`
        return html
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xData,
      name: '考察天数',
      axisLabel: { rotate: 45, fontSize: 10 }
    },
    yAxis: {
      type: 'value',
      name: `检测值 (${unit})`,
      scale: true
    },
    series: [
      {
        name: '检测值',
        type: 'line',
        data: yData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { color: '#409EFF', width: 2 },
        itemStyle: { color: '#409EFF' },
        markArea: {
          silent: true,
          data: markAreaData
        },
        markLine: {
          silent: true,
          symbol: 'none',
          data: markLines
        }
      },
      {
        name: '不合格点',
        type: 'scatter',
        data: scatterData,
        label: {
          show: true,
          position: 'top',
          formatter: '{b}',
          fontSize: 10,
          color: '#F56C6C'
        },
        zlevel: 10
      }
    ]
  }
  
  trendChart.setOption(option, true)
}

const handleEstimate = async (row) => {
  estimateLoading.value = true
  currentScheme.value = row
  try {
    const res = await stabilityApi.estimate(row.id)
    if (res.data) {
      Object.assign(estimateResult, res.data)
      estimateDialogVisible.value = true
    }
  } catch (error) {
    console.error('保质期预估失败', error)
    ElMessage.error('保质期预估失败')
  } finally {
    estimateLoading.value = false
  }
}

const handleGenerateReport = async (row) => {
  try {
    const res = await stabilityApi.generateReport(row.id)
    const blob = new Blob([res], { type: 'application/pdf' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `稳定性考察报告_${row.schemeNo}_${dayjs().format('YYYYMMDDHHmmss')}.pdf`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('报告生成成功')
  } catch (error) {
    console.error('生成报告失败', error)
    ElMessage.error('生成报告失败')
  }
}

const handleSubmit = async () => {
  try {
    await schemeFormRef.value.validate()
    
    if (schemeForm.timePoints.length === 0) {
      ElMessage.warning('请至少添加一个检测时间点')
      return
    }
    
    if (schemeForm.id) {
      await stabilityApi.update(schemeForm)
      ElMessage.success('更新成功')
    } else {
      await stabilityApi.save(schemeForm)
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

const handleResize = () => {
  trendChart?.resize()
}

onMounted(() => {
  fetchList()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
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

      &.active .stat-icon {
        background: linear-gradient(135deg, #4facfe, #00f2fe);
      }

      &.completed .stat-icon {
        background: linear-gradient(135deg, #43e97b, #38f9d7);
      }

      &.pending .stat-icon {
        background: linear-gradient(135deg, #f093fb, #f5576c);
      }
    }
  }

  .toolbar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .time-point-config {
    width: 100%;
  }

  .chart-container {
    width: 100%;
    height: 400px;
  }

  :deep(.el-table .overdue-row) {
    --el-table-tr-bg-color: rgba(245, 108, 108, 0.1);
    
    &:hover > td {
      background-color: rgba(245, 108, 108, 0.2) !important;
    }
  }

  :deep(.el-table__row.current-row) {
    background-color: rgba(64, 158, 255, 0.1) !important;
  }
}
</style>
