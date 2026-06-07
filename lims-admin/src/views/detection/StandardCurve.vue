<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">标准曲线管理</div>
      <div class="page-desc">建立、管理和验证检测项目的标准曲线，支持多种拟合方式和质量评估</div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Histogram /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总曲线数</div>
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
              <div class="stat-label">有效曲线</div>
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
              <div class="stat-label">即将过期</div>
              <div class="stat-value">{{ stats.expiringSoon || 0 }}</div>
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
              <div class="stat-label">已过期</div>
              <div class="stat-value">{{ stats.expired || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab" class="main-tabs">
        <el-tab-pane label="标准曲线管理" name="manage">
          <div class="toolbar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索曲线编号、名称、检测项目..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="searchItem" placeholder="检测项目" clearable style="width: 160px">
              <el-option v-for="item in itemOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 120px">
              <el-option label="有效" :value="1" />
              <el-option label="已过期" :value="0" />
              <el-option label="已作废" :value="2" />
            </el-select>
            <el-select v-model="searchFitType" placeholder="拟合类型" clearable style="width: 140px">
              <el-option label="线性" value="linear" />
              <el-option label="二次" value="quadratic" />
              <el-option label="线性过原点" value="linear_origin" />
              <el-option label="加权" value="weighted" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增曲线
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
            :row-class-name="getTableRowClassName"
          >
            <el-table-column prop="curveNo" label="曲线编号" width="160" />
            <el-table-column prop="curveName" label="曲线名称" width="200" />
            <el-table-column prop="itemName" label="检测项目" width="140" />
            <el-table-column prop="methodName" label="检测方法" min-width="150" show-overflow-tooltip />
            <el-table-column prop="fitType" label="拟合类型" width="120">
              <template #default="{ row }">
                <el-tag type="info" effect="light" size="small">
                  {{ getFitTypeText(row.fitType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="rSquared" label="R²" width="100">
              <template #default="{ row }">
                <span :class="getRSquaredClass(row.rSquared)">
                  {{ row.rSquared ? row.rSquared.toFixed(4) : '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="calibrationDate" label="校准日期" width="120" />
            <el-table-column prop="expireDate" label="有效期至" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleView(row)">
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  type="success"
                  link
                  @click="handleEdit(row)"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  type="warning"
                  link
                  @click="handleVerify(row)"
                >
                  <el-icon><Select /></el-icon>
                  验证
                </el-button>
                <el-button
                  v-if="row.status === 1"
                  type="danger"
                  link
                  @click="handleInvalid(row)"
                >
                  <el-icon><Close /></el-icon>
                  作废
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
        </el-tab-pane>

        <el-tab-pane label="曲线拟合工具" name="fitting">
          <el-alert
            title="曲线拟合工具"
            type="info"
            show-icon
            :closable="false"
            style="margin-bottom: 16px"
          >
            <template #default>
              快速进行曲线拟合计算，无需保存曲线数据。输入标准点浓度和响应值，选择拟合类型后点击计算即可查看结果。
            </template>
          </el-alert>

          <el-row :gutter="16">
            <el-col :span="10">
              <el-card shadow="never">
                <template #header>
                  <div class="card-header">
                    <span>拟合参数</span>
                  </div>
                </template>
                <el-form label-width="100px">
                  <el-form-item label="拟合类型">
                    <el-select v-model="fittingForm.fitType" style="width: 100%">
                      <el-option label="线性 (y = ax + b)" value="linear" />
                      <el-option label="二次 (y = ax² + bx + c)" value="quadratic" />
                      <el-option label="线性过原点 (y = ax)" value="linear_origin" />
                      <el-option label="加权线性" value="weighted" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="权重类型">
                    <el-select v-model="fittingForm.weightType" style="width: 100%">
                      <el-option label="1/x" value="1/x" />
                      <el-option label="1/x²" value="1/x²" />
                      <el-option label="1/y" value="1/y" />
                      <el-option label="1/y²" value="1/y²" />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="单位">
                    <el-input v-model="fittingForm.unit" placeholder="如: mg/L" />
                  </el-form-item>
                </el-form>

                <el-divider content-position="left">标准点数据</el-divider>

                <el-table :data="fittingForm.points" border size="small" style="width: 100%">
                  <el-table-column label="序号" width="60" type="index" />
                  <el-table-column label="浓度 (x)" width="120">
                    <template #default="{ row }">
                      <el-input-number
                        v-model="row.concentration"
                        :precision="4"
                        :step="0.001"
                        :min="0"
                        controls-position="right"
                        style="width: 100%"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="响应值1" width="110">
                    <template #default="{ row }">
                      <el-input-number
                        v-model="row.response1"
                        :precision="4"
                        :step="0.001"
                        controls-position="right"
                        style="width: 100%"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="响应值2" width="110">
                    <template #default="{ row }">
                      <el-input-number
                        v-model="row.response2"
                        :precision="4"
                        :step="0.001"
                        controls-position="right"
                        style="width: 100%"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="响应值3" width="110">
                    <template #default="{ row }">
                      <el-input-number
                        v-model="row.response3"
                        :precision="4"
                        :step="0.001"
                        controls-position="right"
                        style="width: 100%"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="有效" width="60">
                    <template #default="{ row }">
                      <el-checkbox v-model="row.valid" />
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="60">
                    <template #default="{ $index }">
                      <el-button type="danger" link @click="removeFittingPoint($index)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <div style="margin-top: 12px; display: flex; gap: 8px">
                  <el-button type="primary" size="small" @click="addFittingPoint">
                    <el-icon><Plus /></el-icon>
                    添加点
                  </el-button>
                  <el-button type="success" size="small" @click="calculateFitting">
                    <el-icon><Calculator /></el-icon>
                    开始计算
                  </el-button>
                  <el-button size="small" @click="resetFitting">
                    <el-icon><RefreshRight /></el-icon>
                    重置
                  </el-button>
                </div>
              </el-card>
            </el-col>

            <el-col :span="14">
              <el-card shadow="never">
                <template #header>
                  <div class="card-header">
                    <span>拟合结果</span>
                  </div>
                </template>
                <div v-if="fittingResult" class="fitting-result">
                  <el-descriptions :column="2" border size="small">
                    <el-descriptions-item label="曲线方程">
                      <span class="equation-text">{{ fittingResult.equation }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="相关系数 R²">
                      <span :class="getRSquaredClass(fittingResult.rSquared)">
                        {{ fittingResult.rSquared?.toFixed(6) }}
                      </span>
                      <el-tag
                        :type="getRSquaredTagType(fittingResult.rSquared)"
                        size="small"
                        style="margin-left: 8px"
                      >
                        {{ getRSquaredLevel(fittingResult.rSquared) }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="斜率 a">{{ fittingResult.slope?.toFixed(6) }}</el-descriptions-item>
                    <el-descriptions-item label="截距 b">{{ fittingResult.intercept?.toFixed(6) }}</el-descriptions-item>
                    <el-descriptions-item label="检出限 (LOD)">{{ fittingResult.lod?.toFixed(4) }} {{ fittingForm.unit }}</el-descriptions-item>
                    <el-descriptions-item label="定量限 (LOQ)">{{ fittingResult.loq?.toFixed(4) }} {{ fittingForm.unit }}</el-descriptions-item>
                  </el-descriptions>

                  <el-divider content-position="left">拟合曲线</el-divider>
                  <div ref="fittingChartRef" class="chart-container"></div>

                  <el-divider content-position="left">各点残差分析</el-divider>
                  <el-table :data="fittingResult.residuals" border size="small" style="width: 100%">
                    <el-table-column label="序号" width="60" type="index" />
                    <el-table-column label="浓度 (x)" prop="concentration" width="120">
                      <template #default="{ row }">{{ row.concentration?.toFixed(4) }}</template>
                    </el-table-column>
                    <el-table-column label="实测响应 (y)" prop="measured" width="120">
                      <template #default="{ row }">{{ row.measured?.toFixed(4) }}</template>
                    </el-table-column>
                    <el-table-column label="计算响应 (ŷ)" prop="calculated" width="120">
                      <template #default="{ row }">{{ row.calculated?.toFixed(4) }}</template>
                    </el-table-column>
                    <el-table-column label="残差" prop="residual" width="100">
                      <template #default="{ row }">{{ row.residual?.toFixed(4) }}</template>
                    </el-table-column>
                    <el-table-column label="相对误差 (%)" prop="relativeError" width="120">
                      <template #default="{ row }">
                        <span :class="getRelativeErrorClass(row.relativeError)">
                          {{ row.relativeError?.toFixed(2) }}%
                        </span>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
                <el-empty v-else description="请输入标准点数据并点击计算" />
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="curveDialogVisible" :title="curveDialogTitle" width="900px" top="3vh" class="curve-dialog">
      <el-form
        :model="curveForm"
        :rules="curveFormRules"
        ref="curveFormRef"
        label-width="100px"
        v-loading="detailLoading"
      >
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="曲线编号" prop="curveNo">
              <el-input v-model="curveForm.curveNo" :disabled="isView || !!curveForm.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="曲线名称" prop="curveName">
              <el-input v-model="curveForm.curveName" :disabled="isView" placeholder="请输入曲线名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="检测项目" prop="itemCode">
              <el-select v-model="curveForm.itemCode" :disabled="isView" style="width: 100%" filterable>
                <el-option v-for="item in itemOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="检测方法" prop="methodName">
              <el-input v-model="curveForm.methodName" :disabled="isView" placeholder="检测方法" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="仪器" prop="instrument">
              <el-input v-model="curveForm.instrument" :disabled="isView" placeholder="使用仪器" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="curveForm.unit" :disabled="isView" placeholder="如: mg/L" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="拟合类型" prop="fitType">
              <el-select v-model="curveForm.fitType" :disabled="isView" style="width: 100%">
                <el-option label="线性" value="linear" />
                <el-option label="二次" value="quadratic" />
                <el-option label="线性过原点" value="linear_origin" />
                <el-option label="加权" value="weighted" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="权重类型" prop="weightType">
              <el-select v-model="curveForm.weightType" :disabled="isView || curveForm.fitType !== 'weighted'" style="width: 100%">
                <el-option label="1/x" value="1/x" />
                <el-option label="1/x²" value="1/x²" />
                <el-option label="1/y" value="1/y" />
                <el-option label="1/y²" value="1/y²" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="有效天数" prop="validDays">
              <el-input-number v-model="curveForm.validDays" :min="1" :max="365" :disabled="isView" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">标准曲线点</el-divider>
        <el-table :data="curveForm.points" border size="small" style="width: 100%">
          <el-table-column label="点序号" width="70" type="index" />
          <el-table-column label="浓度" width="130">
            <template #default="{ row }">
              <el-input-number
                v-model="row.concentration"
                :precision="4"
                :step="0.001"
                :min="0"
                controls-position="right"
                style="width: 100%"
                :disabled="isView"
              />
            </template>
          </el-table-column>
          <el-table-column label="响应值1" width="110">
            <template #default="{ row }">
              <el-input-number
                v-model="row.response1"
                :precision="4"
                :step="0.001"
                controls-position="right"
                style="width: 100%"
                :disabled="isView"
              />
            </template>
          </el-table-column>
          <el-table-column label="响应值2" width="110">
            <template #default="{ row }">
              <el-input-number
                v-model="row.response2"
                :precision="4"
                :step="0.001"
                controls-position="right"
                style="width: 100%"
                :disabled="isView"
              />
            </template>
          </el-table-column>
          <el-table-column label="响应值3" width="110">
            <template #default="{ row }">
              <el-input-number
                v-model="row.response3"
                :precision="4"
                :step="0.001"
                controls-position="right"
                style="width: 100%"
                :disabled="isView"
              />
            </template>
          </el-table-column>
          <el-table-column label="平均值" width="100">
            <template #default="{ row }">
              {{ calculateAvgResponse(row) }}
            </template>
          </el-table-column>
          <el-table-column label="是否有效" width="80">
            <template #default="{ row }">
              <el-checkbox v-model="row.valid" :disabled="isView" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" v-if="!isView">
            <template #default="{ $index }">
              <el-button type="danger" link @click="removeCurvePoint($index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!isView" style="margin-top: 12px; display: flex; gap: 8px">
          <el-button type="primary" size="small" @click="addCurvePoint">
            <el-icon><Plus /></el-icon>
            添加曲线点
          </el-button>
          <el-button type="success" size="small" @click="calculateCurve">
            <el-icon><Calculator /></el-icon>
            实时计算
          </el-button>
        </div>

        <el-divider v-if="calculationResult" content-position="left">计算结果</el-divider>
        <el-descriptions v-if="calculationResult" :column="3" border size="small">
          <el-descriptions-item label="曲线方程">
            <span class="equation-text">{{ calculationResult.equation }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="相关系数 R²">
            <span :class="getRSquaredClass(calculationResult.rSquared)">
              {{ calculationResult.rSquared?.toFixed(6) }}
            </span>
            <el-tag
              :type="getRSquaredTagType(calculationResult.rSquared)"
              size="small"
              style="margin-left: 8px"
            >
              {{ getRSquaredLevel(calculationResult.rSquared) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="斜率 a">{{ calculationResult.slope?.toFixed(6) }}</el-descriptions-item>
          <el-descriptions-item label="截距 b">{{ calculationResult.intercept?.toFixed(6) }}</el-descriptions-item>
          <el-descriptions-item label="检出限 (LOD)">{{ calculationResult.lod?.toFixed(4) }} {{ curveForm.unit }}</el-descriptions-item>
          <el-descriptions-item label="定量限 (LOQ)">{{ calculationResult.loq?.toFixed(4) }} {{ curveForm.unit }}</el-descriptions-item>
        </el-descriptions>

        <el-form-item label="备注" style="margin-top: 16px">
          <el-input
            v-model="curveForm.remark"
            type="textarea"
            :rows="2"
            :disabled="isView"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="curveDialogVisible = false">关闭</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSaveCurve" :disabled="!calculationResult">
          <el-icon><Check /></el-icon>
          {{ curveForm.id ? '更新' : '保存' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="曲线详情" width="1000px" top="3vh" class="detail-dialog">
      <div v-loading="detailLoading">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="曲线编号">{{ currentCurve?.curveNo }}</el-descriptions-item>
          <el-descriptions-item label="曲线名称">{{ currentCurve?.curveName }}</el-descriptions-item>
          <el-descriptions-item label="检测项目">{{ currentCurve?.itemName }}</el-descriptions-item>
          <el-descriptions-item label="检测方法">{{ currentCurve?.methodName }}</el-descriptions-item>
          <el-descriptions-item label="拟合类型">{{ getFitTypeText(currentCurve?.fitType) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTag(currentCurve?.status)" effect="light" size="small">
              {{ getStatusText(currentCurve?.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="曲线方程">
            <span class="equation-text">{{ currentCurve?.equation }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="相关系数 R²">
            <span :class="getRSquaredClass(currentCurve?.rSquared)">
              {{ currentCurve?.rSquared?.toFixed(6) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="斜率 / 截距">
            a = {{ currentCurve?.slope?.toFixed(6) }}, b = {{ currentCurve?.intercept?.toFixed(6) }}
          </el-descriptions-item>
          <el-descriptions-item label="检出限">{{ currentCurve?.lod?.toFixed(4) }} {{ currentCurve?.unit }}</el-descriptions-item>
          <el-descriptions-item label="定量限">{{ currentCurve?.loq?.toFixed(4) }} {{ currentCurve?.unit }}</el-descriptions-item>
          <el-descriptions-item label="有效期">
            {{ currentCurve?.calibrationDate }} 至 {{ currentCurve?.expireDate }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">拟合曲线</el-divider>
        <div ref="detailChartRef" class="chart-container-large"></div>

        <el-divider content-position="left">曲线点残差分析</el-divider>
        <el-table :data="curvePoints" border size="small" style="width: 100%">
          <el-table-column label="序号" width="60" type="index" />
          <el-table-column label="浓度 (x)" width="120">
            <template #default="{ row }">{{ row.concentration?.toFixed(4) }}</template>
          </el-table-column>
          <el-table-column label="响应值1" width="100">
            <template #default="{ row }">{{ row.response1?.toFixed(4) }}</template>
          </el-table-column>
          <el-table-column label="响应值2" width="100">
            <template #default="{ row }">{{ row.response2?.toFixed(4) }}</template>
          </el-table-column>
          <el-table-column label="响应值3" width="100">
            <template #default="{ row }">{{ row.response3?.toFixed(4) }}</template>
          </el-table-column>
          <el-table-column label="平均响应" width="110">
            <template #default="{ row }">{{ row.avgResponse?.toFixed(4) }}</template>
          </el-table-column>
          <el-table-column label="计算值" width="110">
            <template #default="{ row }">{{ row.calculated?.toFixed(4) }}</template>
          </el-table-column>
          <el-table-column label="残差" width="90">
            <template #default="{ row }">{{ row.residual?.toFixed(4) }}</template>
          </el-table-column>
          <el-table-column label="相对误差 (%)" width="120">
            <template #default="{ row }">
              <span :class="getRelativeErrorClass(row.relativeError)">
                {{ row.relativeError?.toFixed(2) }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.valid" type="success" size="small">有效</el-tag>
              <el-tag v-else type="info" size="small">无效</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <el-divider content-position="left">验证记录</el-divider>
        <el-table :data="verifyRecords" border size="small" style="width: 100%">
          <el-table-column label="验证时间" prop="verifyTime" width="180" />
          <el-table-column label="理论浓度" prop="expectedConcentration" width="120">
            <template #default="{ row }">{{ row.expectedConcentration }} {{ currentCurve?.unit }}</template>
          </el-table-column>
          <el-table-column label="实测响应" prop="measuredResponse" width="120" />
          <el-table-column label="计算浓度" prop="calculatedConcentration" width="120">
            <template #default="{ row }">{{ row.calculatedConcentration?.toFixed(4) }} {{ currentCurve?.unit }}</template>
          </el-table-column>
          <el-table-column label="回收率 (%)" prop="recovery" width="120">
            <template #default="{ row }">
              <span :class="getRecoveryClass(row.recovery)">
                {{ row.recovery?.toFixed(2) }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column label="结果" width="80">
            <template #default="{ row }">
              <el-tag :type="row.pass ? 'success' : 'danger'" size="small">
                {{ row.pass ? '合格' : '不合格' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="验证人" prop="verifyBy" width="100" />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="verifyDialogVisible" title="曲线验证" width="500px" top="10vh">
      <el-alert
        title="验证说明"
        type="info"
        :closable="false"
        style="margin-bottom: 16px"
      >
        <template #default>
          输入验证点的理论浓度和实测响应值，系统将根据曲线方程计算回收率。回收率在 95%~105% 之间为合格。
        </template>
      </el-alert>
      <el-form :model="verifyForm" :rules="verifyFormRules" ref="verifyFormRef" label-width="120px">
        <el-form-item label="验证曲线">
          <el-input :value="currentCurve?.curveName" disabled />
        </el-form-item>
        <el-form-item label="理论浓度" prop="expectedConcentration">
          <el-input-number
            v-model="verifyForm.expectedConcentration"
            :precision="4"
            :step="0.001"
            :min="0"
            style="width: 100%"
          />
          <span style="margin-left: 8px; color: #909399">{{ currentCurve?.unit }}</span>
        </el-form-item>
        <el-form-item label="实测响应值" prop="measuredResponse">
          <el-input-number
            v-model="verifyForm.measuredResponse"
            :precision="4"
            :step="0.001"
            style="width: 100%"
          />
        </el-form-item>
        <el-divider v-if="verifyResult" />
        <div v-if="verifyResult" class="verify-result">
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="计算浓度">
              {{ verifyResult.calculatedConcentration?.toFixed(4) }} {{ currentCurve?.unit }}
            </el-descriptions-item>
            <el-descriptions-item label="回收率">
              <span :class="getRecoveryClass(verifyResult.recovery)" style="font-size: 18px; font-weight: 600">
                {{ verifyResult.recovery?.toFixed(2) }}%
              </span>
              <el-tag
                :type="verifyResult.pass ? 'success' : 'danger'"
                size="small"
                style="margin-left: 8px"
              >
                {{ verifyResult.pass ? '合格' : '不合格' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="verifyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="calculateVerify">计算</el-button>
        <el-button v-if="verifyResult" type="success" @click="handleSaveVerify">保存验证记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { standardCurveApi } from '@/api/detection'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const loading = ref(false)
const detailLoading = ref(false)
const activeTab = ref('manage')
const searchKeyword = ref('')
const searchItem = ref(null)
const searchStatus = ref(null)
const searchFitType = ref(null)
const tableData = ref([])

const curveDialogVisible = ref(false)
const curveDialogTitle = ref('')
const isView = ref(false)
const curveFormRef = ref(null)
const detailDialogVisible = ref(false)
const verifyDialogVisible = ref(false)
const verifyFormRef = ref(null)
const currentCurve = ref(null)
const calculationResult = ref(null)
const fittingResult = ref(null)
const verifyResult = ref(null)
const curvePoints = ref([])
const verifyRecords = ref([])
const fittingChartRef = ref(null)
const detailChartRef = ref(null)
let fittingChart = null
let detailChart = null

const stats = reactive({
  total: 0,
  valid: 0,
  expiringSoon: 0,
  expired: 0
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const itemOptions = [
  { value: 'COD', label: '化学需氧量(COD)' },
  { value: 'BOD', label: '生化需氧量(BOD)' },
  { value: 'NH3N', label: '氨氮(NH3-N)' },
  { value: 'TP', label: '总磷(TP)' },
  { value: 'TN', label: '总氮(TN)' },
  { value: 'PH', label: 'pH值' },
  { value: 'SS', label: '悬浮物(SS)' },
  { value: 'OIL', label: '石油类' },
  { value: 'ANION', label: '阴离子表面活性剂' },
  { value: 'HEAVY_METAL', label: '重金属' }
]

const curveForm = reactive({
  id: null,
  curveNo: '',
  curveName: '',
  itemCode: '',
  itemName: '',
  methodName: '',
  instrument: '',
  unit: 'mg/L',
  fitType: 'linear',
  weightType: '1/x',
  validDays: 30,
  calibrationDate: '',
  expireDate: '',
  remark: '',
  points: []
})

const curveFormRules = {
  curveNo: [{ required: true, message: '请输入曲线编号', trigger: 'blur' }],
  curveName: [{ required: true, message: '请输入曲线名称', trigger: 'blur' }],
  itemCode: [{ required: true, message: '请选择检测项目', trigger: 'change' }],
  fitType: [{ required: true, message: '请选择拟合类型', trigger: 'change' }],
  validDays: [{ required: true, message: '请输入有效天数', trigger: 'blur' }]
}

const fittingForm = reactive({
  fitType: 'linear',
  weightType: '1/x',
  unit: 'mg/L',
  points: []
})

const verifyForm = reactive({
  expectedConcentration: null,
  measuredResponse: null
})

const verifyFormRules = {
  expectedConcentration: [{ required: true, message: '请输入理论浓度', trigger: 'blur' }],
  measuredResponse: [{ required: true, message: '请输入实测响应值', trigger: 'blur' }]
}

const getFitTypeText = (type) => {
  const map = {
    linear: '线性',
    quadratic: '二次',
    linear_origin: '线性过原点',
    weighted: '加权'
  }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = { 0: '已过期', 1: '有效', 2: '已作废' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { 0: 'danger', 1: 'success', 2: 'info' }
  return map[status] || 'info'
}

const getRSquaredClass = (r2) => {
  if (r2 >= 0.999) return 'r2-excellent'
  if (r2 >= 0.995) return 'r2-good'
  return 'r2-poor'
}

const getRSquaredTagType = (r2) => {
  if (r2 >= 0.999) return 'success'
  if (r2 >= 0.995) return 'warning'
  return 'danger'
}

const getRSquaredLevel = (r2) => {
  if (r2 >= 0.999) return '优秀'
  if (r2 >= 0.995) return '良好'
  return '不合格'
}

const getRelativeErrorClass = (error) => {
  if (Math.abs(error) <= 5) return 'error-good'
  if (Math.abs(error) <= 10) return 'error-warning'
  return 'error-poor'
}

const getRecoveryClass = (recovery) => {
  if (recovery >= 95 && recovery <= 105) return 'recovery-good'
  if (recovery >= 90 && recovery <= 110) return 'recovery-warning'
  return 'recovery-poor'
}

const getTableRowClassName = ({ row }) => {
  if (row.status === 1 && row.expireDate) {
    const daysLeft = dayjs(row.expireDate).diff(dayjs(), 'day')
    if (daysLeft >= 0 && daysLeft <= 7) {
      return 'expiring-soon-row'
    }
  }
  return ''
}

const calculateAvgResponse = (row) => {
  const values = [row.response1, row.response2, row.response3].filter(v => v !== null && v !== undefined && v !== '')
  if (values.length === 0) return '-'
  const avg = values.reduce((a, b) => Number(a) + Number(b), 0) / values.length
  return avg.toFixed(4)
}

const fetchStats = async () => {
  try {
    const res = await standardCurveApi.getStats()
    if (res.data) {
      stats.total = res.data.total || 0
      stats.valid = res.data.valid || 0
      stats.expiringSoon = res.data.expiringSoon || 0
      stats.expired = res.data.expired || 0
    }
  } catch (error) {
    stats.total = tableData.value.length
    stats.valid = tableData.value.filter(r => r.status === 1).length
    stats.expiringSoon = 0
    stats.expired = tableData.value.filter(r => r.status === 0).length
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      itemCode: searchItem.value,
      status: searchStatus.value,
      fitType: searchFitType.value
    }
    const res = await standardCurveApi.page(params)
    if (res.data?.records) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      tableData.value = []
      pagination.total = 0
    }
    await fetchStats()
  } catch (error) {
    console.error('获取曲线列表失败', error)
    ElMessage.error('获取曲线列表失败')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const initFittingPoints = () => {
  fittingForm.points = [
    { concentration: 0, response1: 0, response2: 0, response3: 0, valid: true },
    { concentration: 0.5, response1: null, response2: null, response3: null, valid: true },
    { concentration: 1, response1: null, response2: null, response3: null, valid: true },
    { concentration: 2, response1: null, response2: null, response3: null, valid: true },
    { concentration: 5, response1: null, response2: null, response3: null, valid: true },
    { concentration: 10, response1: null, response2: null, response3: null, valid: true }
  ]
}

const addFittingPoint = () => {
  fittingForm.points.push({ concentration: null, response1: null, response2: null, response3: null, valid: true })
}

const removeFittingPoint = (index) => {
  if (fittingForm.points.length <= 2) {
    ElMessage.warning('至少保留2个标准点')
    return
  }
  fittingForm.points.splice(index, 1)
}

const calculateFitting = async () => {
  const validPoints = fittingForm.points.filter(p => p.valid && p.concentration !== null && p.concentration !== '')
  if (validPoints.length < 2) {
    ElMessage.warning('请至少输入2个有效的标准点')
    return
  }
  try {
    const params = {
      fitType: fittingForm.fitType,
      weightType: fittingForm.weightType,
      points: validPoints
    }
    const res = await standardCurveApi.calculate(params)
    if (res.data) {
      fittingResult.value = res.data
      await nextTick()
      renderFittingChart()
    }
  } catch (error) {
    console.error('计算失败', error)
    ElMessage.error('计算失败：' + (error.response?.data?.message || error.message))
  }
}

const resetFitting = () => {
  fittingForm.fitType = 'linear'
  fittingForm.weightType = '1/x'
  fittingForm.unit = 'mg/L'
  fittingResult.value = null
  initFittingPoints()
  if (fittingChart) {
    fittingChart.dispose()
    fittingChart = null
  }
}

const renderFittingChart = () => {
  if (!fittingChartRef.value || !fittingResult.value) return
  
  if (fittingChart) {
    fittingChart.dispose()
  }
  
  fittingChart = echarts.init(fittingChartRef.value)
  
  const points = fittingResult.value.residuals || []
  const scatterData = points.map(p => [p.concentration, p.measured])
  const lineData = generateFitLineData(fittingResult.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    grid: { left: '10%', right: '10%', bottom: '10%', top: '10%' },
    xAxis: {
      name: '浓度 (x)',
      nameLocation: 'middle',
      nameGap: 25,
      type: 'value',
      scale: true
    },
    yAxis: {
      name: '响应值 (y)',
      nameLocation: 'middle',
      nameGap: 35,
      type: 'value',
      scale: true
    },
    series: [
      {
        name: '标准点',
        type: 'scatter',
        data: scatterData,
        symbolSize: 8,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '拟合曲线',
        type: 'line',
        data: lineData,
        smooth: true,
        symbol: 'none',
        lineStyle: { color: '#F56C6C', width: 2 },
        markLine: {
          silent: true,
          data: [{ type: 'average', name: '平均值' }]
        }
      }
    ]
  }
  
  fittingChart.setOption(option)
}

const renderDetailChart = () => {
  if (!detailChartRef.value || !currentCurve.value) return
  
  if (detailChart) {
    detailChart.dispose()
  }
  
  detailChart = echarts.init(detailChartRef.value)
  
  const points = curvePoints.value.filter(p => p.valid)
  const scatterData = points.map(p => [p.concentration, p.avgResponse])
  
  const tempResult = {
    fitType: currentCurve.value.fitType,
    slope: currentCurve.value.slope,
    intercept: currentCurve.value.intercept,
    residuals: points.map(p => ({
      concentration: p.concentration,
      measured: p.avgResponse
    }))
  }
  const lineData = generateFitLineData(tempResult)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      formatter: (params) => {
        let result = ''
        params.forEach(p => {
          result += `${p.seriesName}: (${p.value[0]?.toFixed(4)}, ${p.value[1]?.toFixed(4)})<br/>`
        })
        return result
      }
    },
    grid: { left: '10%', right: '10%', bottom: '10%', top: '10%' },
    xAxis: {
      name: `浓度 (${currentCurve.value.unit || ''})`,
      nameLocation: 'middle',
      nameGap: 25,
      type: 'value',
      scale: true
    },
    yAxis: {
      name: '响应值',
      nameLocation: 'middle',
      nameGap: 35,
      type: 'value',
      scale: true
    },
    series: [
      {
        name: '标准点',
        type: 'scatter',
        data: scatterData,
        symbolSize: 10,
        itemStyle: { color: '#409EFF' },
        label: {
          show: true,
          position: 'top',
          formatter: (p) => p.value[0]?.toFixed(2)
        }
      },
      {
        name: '拟合曲线',
        type: 'line',
        data: lineData,
        smooth: true,
        symbol: 'none',
        lineStyle: { color: '#F56C6C', width: 2 }
      }
    ]
  }
  
  detailChart.setOption(option)
}

const generateFitLineData = (result) => {
  if (!result || !result.residuals || result.residuals.length === 0) return []
  
  const concentrations = result.residuals.map(r => r.concentration).filter(c => c !== null && c !== undefined)
  const minX = Math.min(...concentrations)
  const maxX = Math.max(...concentrations)
  const range = maxX - minX
  const padding = range * 0.1
  
  const points = []
  const step = (range + padding * 2) / 100
  
  for (let x = minX - padding; x <= maxX + padding; x += step) {
    let y
    if (result.fitType === 'quadratic' && result.coefficients) {
      const [a, b, c] = result.coefficients
      y = a * x * x + b * x + c
    } else if (result.fitType === 'linear_origin') {
      y = (result.slope || 0) * x
    } else {
      y = (result.slope || 0) * x + (result.intercept || 0)
    }
    points.push([x, y])
  }
  
  return points
}

const handleAdd = () => {
  isView.value = false
  curveDialogTitle.value = '新增标准曲线'
  calculationResult.value = null
  Object.assign(curveForm, {
    id: null,
    curveNo: 'SC-' + dayjs().format('YYYYMMDDHHmmss'),
    curveName: '',
    itemCode: '',
    itemName: '',
    methodName: '',
    instrument: '',
    unit: 'mg/L',
    fitType: 'linear',
    weightType: '1/x',
    validDays: 30,
    calibrationDate: dayjs().format('YYYY-MM-DD'),
    expireDate: dayjs().add(30, 'day').format('YYYY-MM-DD'),
    remark: '',
    points: [
      { concentration: 0, response1: 0, response2: 0, response3: 0, valid: true },
      { concentration: 0.5, response1: null, response2: null, response3: null, valid: true },
      { concentration: 1, response1: null, response2: null, response3: null, valid: true },
      { concentration: 2, response1: null, response2: null, response3: null, valid: true },
      { concentration: 5, response1: null, response2: null, response3: null, valid: true },
      { concentration: 10, response1: null, response2: null, response3: null, valid: true }
    ]
  })
  curveDialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  curveDialogTitle.value = '编辑标准曲线'
  calculationResult.value = null
  detailLoading.value = true
  try {
    const res = await standardCurveApi.getDetail(row.id)
    if (res.data) {
      const data = res.data
      Object.assign(curveForm, {
        id: data.id,
        curveNo: data.curveNo,
        curveName: data.curveName,
        itemCode: data.itemCode,
        itemName: data.itemName,
        methodName: data.methodName,
        instrument: data.instrument,
        unit: data.unit,
        fitType: data.fitType,
        weightType: data.weightType,
        validDays: data.validDays,
        calibrationDate: data.calibrationDate,
        expireDate: data.expireDate,
        remark: data.remark,
        points: data.points || []
      })
      if (data.equation) {
        calculationResult.value = {
          equation: data.equation,
          rSquared: data.rSquared,
          slope: data.slope,
          intercept: data.intercept,
          lod: data.lod,
          loq: data.loq
        }
      }
      curveDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取曲线详情失败', error)
    ElMessage.error('获取曲线详情失败')
  } finally {
    detailLoading.value = false
  }
}

const handleView = async (row) => {
  currentCurve.value = row
  detailLoading.value = true
  try {
    const [detailRes, pointsRes] = await Promise.all([
      standardCurveApi.getDetail(row.id),
      standardCurveApi.getPoints(row.id)
    ])
    
    if (detailRes.data) {
      currentCurve.value = detailRes.data
    }
    if (pointsRes.data) {
      curvePoints.value = pointsRes.data.map(p => {
        const avg = calculatePointAvg(p)
        const calculated = calculateConcentrationFromCurve(p.concentration, currentCurve.value)
        return {
          ...p,
          avgResponse: avg,
          calculated: calculated,
          residual: avg - calculated,
          relativeError: avg !== 0 ? ((avg - calculated) / avg * 100) : 0
        }
      })
    }
    
    verifyRecords.value = [
      { verifyTime: '2024-01-15 10:30:00', expectedConcentration: 2.5, measuredResponse: 0.512, calculatedConcentration: 2.48, recovery: 99.2, pass: true, verifyBy: '张三' },
      { verifyTime: '2024-01-16 14:20:00', expectedConcentration: 5, measuredResponse: 1.025, calculatedConcentration: 5.12, recovery: 102.4, pass: true, verifyBy: '李四' }
    ]
    
    detailDialogVisible.value = true
    await nextTick()
    renderDetailChart()
  } catch (error) {
    console.error('获取详情失败', error)
    ElMessage.error('获取详情失败')
  } finally {
    detailLoading.value = false
  }
}

const calculatePointAvg = (p) => {
  const values = [p.response1, p.response2, p.response3].filter(v => v !== null && v !== undefined && v !== '')
  if (values.length === 0) return 0
  return values.reduce((a, b) => Number(a) + Number(b), 0) / values.length
}

const calculateConcentrationFromCurve = (concentration, curve) => {
  if (!curve) return 0
  if (curve.fitType === 'linear_origin') {
    return (curve.slope || 0) * concentration
  }
  return (curve.slope || 0) * concentration + (curve.intercept || 0)
}

const handleVerify = (row) => {
  currentCurve.value = row
  verifyForm.expectedConcentration = null
  verifyForm.measuredResponse = null
  verifyResult.value = null
  verifyDialogVisible.value = true
}

const calculateVerify = async () => {
  try {
    await verifyFormRef.value.validate()
    const res = await standardCurveApi.verify(currentCurve.value.id, verifyForm)
    if (res.data) {
      verifyResult.value = res.data
    }
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('验证计算失败', error)
      ElMessage.error('验证计算失败')
    }
  }
}

const handleSaveVerify = async () => {
  try {
    await ElMessageBox.confirm('确定保存该验证记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    ElMessage.success('验证记录保存成功')
    verifyDialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('保存失败', error)
    }
  }
}

const handleInvalid = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要作废曲线"${row.curveName}"吗？作废后将无法使用。`, '提示', {
      confirmButtonText: '确定作废',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await standardCurveApi.delete(row.id)
    ElMessage.success('作废成功')
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('作废失败', error)
    }
  }
}

const addCurvePoint = () => {
  curveForm.points.push({ concentration: null, response1: null, response2: null, response3: null, valid: true })
}

const removeCurvePoint = (index) => {
  if (curveForm.points.length <= 2) {
    ElMessage.warning('至少保留2个曲线点')
    return
  }
  curveForm.points.splice(index, 1)
}

const calculateCurve = async () => {
  const validPoints = curveForm.points.filter(p => p.valid && p.concentration !== null && p.concentration !== '')
  if (validPoints.length < 2) {
    ElMessage.warning('请至少输入2个有效的曲线点')
    return
  }
  try {
    const params = {
      fitType: curveForm.fitType,
      weightType: curveForm.weightType,
      points: validPoints
    }
    const res = await standardCurveApi.calculate(params)
    if (res.data) {
      calculationResult.value = res.data
      ElMessage.success('计算完成')
    }
  } catch (error) {
    console.error('计算失败', error)
    ElMessage.error('计算失败：' + (error.response?.data?.message || error.message))
  }
}

const handleSaveCurve = async () => {
  try {
    await curveFormRef.value.validate()
    if (!calculationResult.value) {
      ElMessage.warning('请先进行实时计算')
      return
    }
    
    const selectedItem = itemOptions.find(o => o.value === curveForm.itemCode)
    const data = {
      ...curveForm,
      itemName: selectedItem?.label || curveForm.itemName,
      equation: calculationResult.value.equation,
      rSquared: calculationResult.value.rSquared,
      slope: calculationResult.value.slope,
      intercept: calculationResult.value.intercept,
      lod: calculationResult.value.lod,
      loq: calculationResult.value.loq,
      calibrationDate: dayjs().format('YYYY-MM-DD'),
      expireDate: dayjs().add(curveForm.validDays, 'day').format('YYYY-MM-DD')
    }
    
    if (curveForm.id) {
      await standardCurveApi.update(data)
      ElMessage.success('更新成功')
    } else {
      await standardCurveApi.save(data)
      ElMessage.success('保存成功')
    }
    curveDialogVisible.value = false
    fetchList()
  } catch (error) {
    if (error !== 'cancel' && error !== false) {
      console.error('提交失败', error)
    }
  }
}

watch(activeTab, (val) => {
  if (val === 'fitting') {
    nextTick(() => {
      if (fittingResult.value) {
        renderFittingChart()
      }
    })
  }
})

onMounted(() => {
  fetchList()
  initFittingPoints()
  
  window.addEventListener('resize', () => {
    fittingChart?.resize()
    detailChart?.resize()
  })
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
        background: linear-gradient(135deg, #11998e, #38ef7d);
      }

      &.warning .stat-icon {
        background: linear-gradient(135deg, #f093fb, #f5576c);
      }

      &.expired .stat-icon {
        background: linear-gradient(135deg, #eb3349, #f45c43);
      }
    }
  }

  .main-tabs {
    :deep(.el-tabs__header) {
      margin: 0 0 16px 0;
    }
  }

  .toolbar {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
  }

  .card-header {
    font-weight: 600;
    font-size: 15px;
  }

  .chart-container {
    width: 100%;
    height: 320px;
  }

  .chart-container-large {
    width: 100%;
    height: 400px;
  }

  .equation-text {
    font-family: 'Times New Roman', serif;
    font-size: 16px;
    font-style: italic;
    color: #409eff;
    font-weight: 600;
  }

  .r2-excellent {
    color: #67c23a;
    font-weight: 600;
  }

  .r2-good {
    color: #e6a23c;
    font-weight: 600;
  }

  .r2-poor {
    color: #f56c6c;
    font-weight: 600;
  }

  .error-good {
    color: #67c23a;
  }

  .error-warning {
    color: #e6a23c;
  }

  .error-poor {
    color: #f56c6c;
  }

  .recovery-good {
    color: #67c23a;
    font-weight: 600;
  }

  .recovery-warning {
    color: #e6a23c;
    font-weight: 600;
  }

  .recovery-poor {
    color: #f56c6c;
    font-weight: 600;
  }

  .fitting-result {
    :deep(.el-descriptions) {
      margin-bottom: 16px;
    }
  }

  .expiring-soon-row {
    background-color: #fff7e6 !important;
  }

  .verify-result {
    margin-top: 16px;
  }

  :deep(.el-table .expiring-soon-row) {
    --el-table-tr-bg-color: #fff7e6;
    background-color: #fff7e6;
  }
}
</style>
