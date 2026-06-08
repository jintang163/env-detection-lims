<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">环境监控</div>
        <div class="page-desc">管理实验室环境监控数据、预警信息和阈值配置</div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="4.8">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">监控点总数</div>
              <div class="stat-value">{{ stats.totalPoints || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card class="stat-card valid" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">正常监控点</div>
              <div class="stat-value">{{ stats.normalPoints || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card class="stat-card expired" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">预警监控点</div>
              <div class="stat-value">{{ stats.warningPoints || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日录入</div>
              <div class="stat-value">{{ stats.todayEntries || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4.8">
        <el-card class="stat-card danger" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待处理预警</div>
              <div class="stat-value">{{ stats.pendingWarnings || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeMainTab" class="main-tabs">
        <el-tab-pane label="数据录入" name="data">
          <div class="toolbar">
            <el-input
              v-model="dataSearchKeyword"
              placeholder="搜索监控点、房间..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchDataList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="dataSearchMonitorType" placeholder="监控类型" clearable style="width: 140px">
              <el-option label="温度" :value="1" />
              <el-option label="湿度" :value="2" />
              <el-option label="压差" :value="3" />
              <el-option label="噪声" :value="4" />
            </el-select>
            <el-select v-model="dataSearchCollectMethod" placeholder="采集方式" clearable style="width: 140px">
              <el-option label="人工" :value="1" />
              <el-option label="在线" :value="2" />
            </el-select>
            <el-date-picker
              v-model="dataSearchDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 180px"
            />
            <el-button type="primary" @click="handleDataAdd">
              <el-icon><Plus /></el-icon>
              新增数据
            </el-button>
            <el-button @click="fetchDataList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="dataTableData"
            v-loading="dataLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="monitorPoint" label="监控点" width="120" />
            <el-table-column prop="roomName" label="房间" width="120" />
            <el-table-column label="监控类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getMonitorTypeTag(row.monitorType)" effect="light" size="small">
                  {{ getMonitorTypeText(row.monitorType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="monitorValue" label="监测值" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.isWarning ? '#f56c6c' : '#303133', fontWeight: row.isWarning ? '600' : 'normal' }">
                  {{ row.monitorValue }}{{ getMonitorUnit(row.monitorType) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="采集方式" width="100">
              <template #default="{ row }">
                {{ row.collectMethod === 1 ? '人工' : '在线' }}
              </template>
            </el-table-column>
            <el-table-column label="是否预警" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.isWarning" type="danger" effect="light" size="small">是</el-tag>
                <el-tag v-else type="success" effect="light" size="small">否</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="collectTime" label="采集时间" width="160" />
            <el-table-column prop="collectorName" label="采集人" width="100" />
            <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleDataView(row)">详情</el-button>
                <el-button type="primary" link @click="handleDataEdit(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDataDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="dataPagination.pageNum"
            v-model:page-size="dataPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="dataPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchDataList"
            @current-change="fetchDataList"
          />
        </el-tab-pane>

        <el-tab-pane label="实时监控" name="realtime">
          <div class="toolbar">
            <el-select v-model="realtimeRoomId" placeholder="选择房间" clearable style="width: 180px" @change="fetchRealtimeData">
              <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomName" :value="room.id" />
            </el-select>
            <el-select v-model="realtimeMonitorType" placeholder="监控类型" clearable style="width: 140px" @change="fetchRealtimeData">
              <el-option label="温度" :value="1" />
              <el-option label="湿度" :value="2" />
              <el-option label="压差" :value="3" />
              <el-option label="噪声" :value="4" />
            </el-select>
            <el-button @click="fetchRealtimeData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-row :gutter="16" style="margin-top: 16px">
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <div style="display: flex; justify-content: space-between; align-items: center">
                    <span>监控点实时数据</span>
                    <el-tag type="info" size="small">共 {{ realtimeData.length }} 个监控点</el-tag>
                  </div>
                </template>
                <el-table :data="realtimeData" v-loading="realtimeLoading" border stripe>
                  <el-table-column prop="monitorPoint" label="监控点" width="120" />
                  <el-table-column prop="roomName" label="房间" width="120" />
                  <el-table-column label="监控类型" width="100">
                    <template #default="{ row }">
                      <el-tag :type="getMonitorTypeTag(row.monitorType)" effect="light" size="small">
                        {{ getMonitorTypeText(row.monitorType) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="当前值" width="120">
                    <template #default="{ row }">
                      <span :style="{ color: row.isWarning ? '#f56c6c' : '#67c23a', fontWeight: '600' }">
                        {{ row.currentValue }}{{ getMonitorUnit(row.monitorType) }}
                      </span>
                    </template>
                  </el-table-column>
                  <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag v-if="row.isWarning" type="danger" effect="light" size="small">异常</el-tag>
                      <el-tag v-else type="success" effect="light" size="small">正常</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="updateTime" label="更新时间" width="160" />
                  <el-table-column label="操作" width="100">
                    <template #default="{ row }">
                      <el-button type="primary" link size="small" @click="handleRealtimeDetail(row)">趋势</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="hover">
                <template #header>
                  <span>环境参数趋势图</span>
                </template>
                <div ref="chartRef" style="width: 100%; height: 400px"></div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>

        <el-tab-pane label="预警记录" name="warning">
          <div class="toolbar">
            <el-input
              v-model="warningSearchKeyword"
              placeholder="搜索监控点、房间..."
              style="width: 260px"
              clearable
              @keyup.enter="fetchWarningList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="warningSearchLevel" placeholder="预警级别" clearable style="width: 140px">
              <el-option label="一般" :value="1" />
              <el-option label="重要" :value="2" />
              <el-option label="紧急" :value="3" />
            </el-select>
            <el-select v-model="warningSearchStatus" placeholder="处理状态" clearable style="width: 140px">
              <el-option label="待处理" :value="0" />
              <el-option label="处理中" :value="1" />
              <el-option label="已处理" :value="2" />
              <el-option label="已忽略" :value="3" />
            </el-select>
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
            <el-table-column prop="monitorPoint" label="监控点" width="120" />
            <el-table-column prop="roomName" label="房间" width="120" />
            <el-table-column label="预警级别" width="100">
              <template #default="{ row }">
                <el-tag :type="getWarnLevelTag(row.warnLevel)" effect="light" size="small">
                  {{ getWarnLevelText(row.warnLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warnContent" label="预警内容" min-width="200" show-overflow-tooltip />
            <el-table-column label="监测值" width="120">
              <template #default="{ row }">
                {{ row.monitorValue }}{{ getMonitorUnit(row.monitorType) }}
              </template>
            </el-table-column>
            <el-table-column label="阈值范围" width="140">
              <template #default="{ row }">
                {{ row.thresholdMin }} - {{ row.thresholdMax }}{{ getMonitorUnit(row.monitorType) }}
              </template>
            </el-table-column>
            <el-table-column label="处理状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getWarningStatusTag(row.warningStatus)" effect="light" size="small">
                  {{ getWarningStatusText(row.warningStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="warnTime" label="预警时间" width="160" />
            <el-table-column prop="handlerName" label="处理人" width="100" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.warningStatus === 0" type="primary" link @click="handleWarningHandle(row)">处理</el-button>
                <el-button v-if="row.warningStatus === 0" type="info" link @click="handleWarningIgnore(row)">忽略</el-button>
                <el-button type="primary" link @click="handleWarningView(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="warningPagination.pageNum"
            v-model:page-size="warningPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="warningPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchWarningList"
            @current-change="fetchWarningList"
          />
        </el-tab-pane>

        <el-tab-pane label="阈值配置" name="threshold">
          <div class="toolbar">
            <el-select v-model="thresholdSearchMonitorType" placeholder="监控类型" clearable style="width: 140px" @change="fetchThresholdList">
              <el-option label="温度" :value="1" />
              <el-option label="湿度" :value="2" />
              <el-option label="压差" :value="3" />
              <el-option label="噪声" :value="4" />
            </el-select>
            <el-select v-model="thresholdSearchRoomId" placeholder="选择房间" clearable style="width: 180px" @change="fetchThresholdList">
              <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomName" :value="room.id" />
            </el-select>
            <el-button type="primary" @click="handleThresholdAdd">
              <el-icon><Plus /></el-icon>
              新增配置
            </el-button>
            <el-button @click="fetchThresholdList">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <el-table
            :data="thresholdTableData"
            v-loading="thresholdLoading"
            border
            stripe
            style="width: 100%; margin-top: 16px"
          >
            <el-table-column prop="monitorPoint" label="监控点" width="120" />
            <el-table-column prop="roomName" label="房间" width="120" />
            <el-table-column label="监控类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getMonitorTypeTag(row.monitorType)" effect="light" size="small">
                  {{ getMonitorTypeText(row.monitorType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="thresholdMin" label="最小值" width="100" />
            <el-table-column prop="thresholdMax" label="最大值" width="100" />
            <el-table-column label="单位" width="80">
              <template #default="{ row }">
                {{ getMonitorUnit(row.monitorType) }}
              </template>
            </el-table-column>
            <el-table-column prop="warnLevel" label="预警级别" width="100">
              <template #default="{ row }">
                <el-tag :type="getWarnLevelTag(row.warnLevel)" effect="light" size="small">
                  {{ getWarnLevelText(row.warnLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.isEnabled" type="success" effect="light" size="small">已启用</el-tag>
                <el-tag v-else type="info" effect="light" size="small">已停用</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleThresholdEdit(row)">编辑</el-button>
                <el-button :type="row.isEnabled ? 'warning' : 'success'" link @click="handleThresholdToggle(row)">
                  {{ row.isEnabled ? '停用' : '启用' }}
                </el-button>
                <el-button type="danger" link @click="handleThresholdDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="thresholdPagination.pageNum"
            v-model:page-size="thresholdPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="thresholdPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 16px; justify-content: flex-end"
            @size-change="fetchThresholdList"
            @current-change="fetchThresholdList"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dataDialogVisible" :title="dataDialogTitle" width="700px" top="10vh">
      <el-form
        :model="dataForm"
        :rules="dataFormRules"
        ref="dataFormRef"
        label-width="100px"
        v-loading="dataDetailLoading"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="监控点" prop="monitorPoint">
              <el-select v-model="dataForm.monitorPoint" placeholder="请选择监控点" style="width: 100%" :disabled="isDataView">
                <el-option label="温度-1" value="温度-1" />
                <el-option label="温度-2" value="温度-2" />
                <el-option label="湿度-1" value="湿度-1" />
                <el-option label="湿度-2" value="湿度-2" />
                <el-option label="压差-1" value="压差-1" />
                <el-option label="噪声-1" value="噪声-1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间" prop="roomId">
              <el-select v-model="dataForm.roomId" placeholder="请选择房间" style="width: 100%" :disabled="isDataView" @change="handleRoomChange">
                <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomName" :value="room.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="监控类型" prop="monitorType">
              <el-select v-model="dataForm.monitorType" placeholder="请选择监控类型" style="width: 100%" :disabled="isDataView">
                <el-option label="温度" :value="1" />
                <el-option label="湿度" :value="2" />
                <el-option label="压差" :value="3" />
                <el-option label="噪声" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="监测值" prop="monitorValue">
              <el-input-number v-model="dataForm.monitorValue" :precision="2" style="width: 100%" :disabled="isDataView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="采集方式" prop="collectMethod">
              <el-select v-model="dataForm.collectMethod" placeholder="请选择采集方式" style="width: 100%" :disabled="isDataView">
                <el-option label="人工" :value="1" />
                <el-option label="在线" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="采集时间" prop="collectTime">
              <el-date-picker
                v-model="dataForm.collectTime"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择时间"
                style="width: 100%"
                :disabled="isDataView"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采集人" prop="collectorName">
              <el-input v-model="dataForm.collectorName" placeholder="请输入采集人" :disabled="isDataView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="dataForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
            :disabled="isDataView"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">关闭</el-button>
        <el-button v-if="!isDataView" type="primary" @click="handleDataSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="warningHandleDialogVisible" title="处理预警" width="600px" top="10vh">
      <el-form :model="warningHandleForm" :rules="warningHandleFormRules" ref="warningHandleFormRef" label-width="100px">
        <el-form-item label="预警内容">
          <el-input v-model="currentWarning.warnContent" disabled />
        </el-form-item>
        <el-form-item label="监测值">
          <el-input :value="currentWarning.monitorValue + getMonitorUnit(currentWarning.monitorType)" disabled />
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-select v-model="warningHandleForm.handleResult" placeholder="请选择处理结果" style="width: 100%">
            <el-option label="已修复" value="已修复" />
            <el-option label="设备调整" value="设备调整" />
            <el-option label="环境改善" value="环境改善" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理说明" prop="handleRemark">
          <el-input
            v-model="warningHandleForm.handleRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="warningHandleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleWarningHandleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="thresholdDialogVisible" :title="thresholdDialogTitle" width="700px" top="10vh">
      <el-form
        :model="thresholdForm"
        :rules="thresholdFormRules"
        ref="thresholdFormRef"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="监控点" prop="monitorPoint">
              <el-input v-model="thresholdForm.monitorPoint" placeholder="请输入监控点" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间" prop="roomId">
              <el-select v-model="thresholdForm.roomId" placeholder="请选择房间" style="width: 100%" @change="handleRoomChange">
                <el-option v-for="room in roomOptions" :key="room.id" :label="room.roomName" :value="room.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="监控类型" prop="monitorType">
              <el-select v-model="thresholdForm.monitorType" placeholder="请选择监控类型" style="width: 100%">
                <el-option label="温度" :value="1" />
                <el-option label="湿度" :value="2" />
                <el-option label="压差" :value="3" />
                <el-option label="噪声" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最小值" prop="thresholdMin">
              <el-input-number v-model="thresholdForm.thresholdMin" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大值" prop="thresholdMax">
              <el-input-number v-model="thresholdForm.thresholdMax" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="预警级别" prop="warnLevel">
              <el-select v-model="thresholdForm.warnLevel" placeholder="请选择预警级别" style="width: 100%">
                <el-option label="一般" :value="1" />
                <el-option label="重要" :value="2" />
                <el-option label="紧急" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用" prop="isEnabled">
              <el-radio-group v-model="thresholdForm.isEnabled">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="thresholdForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="thresholdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleThresholdSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="realtimeDetailDialogVisible" title="趋势图详情" width="900px" top="5vh">
      <div style="margin-bottom: 16px">
        <span style="margin-right: 16px">监控点：{{ currentRealtimePoint.monitorPoint }}</span>
        <span style="margin-right: 16px">房间：{{ currentRealtimePoint.roomName }}</span>
        <span>类型：{{ getMonitorTypeText(currentRealtimePoint.monitorType) }}</span>
      </div>
      <div ref="detailChartRef" style="width: 100%; height: 400px"></div>
      <template #footer>
        <el-button @click="realtimeDetailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Monitor,
  CircleCheck,
  Warning,
  Edit,
  Bell,
  Search,
  Plus,
  Refresh,
  Check
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { monitorApi, roomApi, facilityApi } from '@/api/environment'

const activeMainTab = ref('data')
const loading = ref(false)
const dataLoading = ref(false)
const warningLoading = ref(false)
const thresholdLoading = ref(false)
const realtimeLoading = ref(false)
const dataDetailLoading = ref(false)

const stats = reactive({
  totalPoints: 0,
  normalPoints: 0,
  warningPoints: 0,
  todayEntries: 0,
  pendingWarnings: 0
})

const roomOptions = ref([])

const dataSearchKeyword = ref('')
const dataSearchMonitorType = ref('')
const dataSearchCollectMethod = ref('')
const dataSearchDate = ref('')
const dataTableData = ref([])
const dataPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const warningSearchKeyword = ref('')
const warningSearchLevel = ref('')
const warningSearchStatus = ref('')
const warningTableData = ref([])
const warningPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const thresholdSearchMonitorType = ref('')
const thresholdSearchRoomId = ref('')
const thresholdTableData = ref([])
const thresholdPagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const realtimeRoomId = ref('')
const realtimeMonitorType = ref('')
const realtimeData = ref([])

const chartRef = ref(null)
const detailChartRef = ref(null)
let chartInstance = null
let detailChartInstance = null

const dataDialogVisible = ref(false)
const isDataView = ref(false)
const dataDialogTitle = ref('')
const dataFormRef = ref(null)
const dataForm = ref({
  id: null,
  monitorPoint: '',
  roomId: null,
  roomName: '',
  monitorType: 1,
  monitorValue: null,
  collectMethod: 1,
  collectTime: '',
  collectorName: '',
  remark: ''
})

const dataFormRules = {
  monitorPoint: [{ required: true, message: '请选择监控点', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  monitorType: [{ required: true, message: '请选择监控类型', trigger: 'change' }],
  monitorValue: [{ required: true, message: '请输入监测值', trigger: 'blur' }],
  collectMethod: [{ required: true, message: '请选择采集方式', trigger: 'change' }],
  collectTime: [{ required: true, message: '请选择采集时间', trigger: 'change' }]
}

const warningHandleDialogVisible = ref(false)
const warningHandleFormRef = ref(null)
const currentWarning = ref({})
const warningHandleForm = ref({
  handleResult: '',
  handleRemark: ''
})
const warningHandleFormRules = {
  handleResult: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  handleRemark: [{ required: true, message: '请输入处理说明', trigger: 'blur' }]
}

const thresholdDialogVisible = ref(false)
const thresholdDialogTitle = ref('')
const thresholdFormRef = ref(null)
const thresholdForm = ref({
  id: null,
  monitorPoint: '',
  roomId: null,
  roomName: '',
  monitorType: 1,
  thresholdMin: null,
  thresholdMax: null,
  warnLevel: 1,
  isEnabled: 1,
  remark: ''
})

const thresholdFormRules = {
  monitorPoint: [{ required: true, message: '请输入监控点', trigger: 'blur' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  monitorType: [{ required: true, message: '请选择监控类型', trigger: 'change' }],
  thresholdMin: [{ required: true, message: '请输入最小值', trigger: 'blur' }],
  thresholdMax: [{ required: true, message: '请输入最大值', trigger: 'blur' }],
  warnLevel: [{ required: true, message: '请选择预警级别', trigger: 'change' }]
}

const realtimeDetailDialogVisible = ref(false)
const currentRealtimePoint = ref({})

const getMonitorTypeTag = (type) => {
  const tags = { 1: 'danger', 2: 'primary', 3: 'success', 4: 'warning' }
  return tags[type] || 'info'
}

const getMonitorTypeText = (type) => {
  const texts = { 1: '温度', 2: '湿度', 3: '压差', 4: '噪声' }
  return texts[type] || '未知'
}

const getMonitorUnit = (type) => {
  const units = { 1: '°C', 2: '%RH', 3: 'Pa', 4: 'dB' }
  return units[type] || ''
}

const getWarnLevelTag = (level) => {
  const tags = { 1: 'info', 2: 'warning', 3: 'danger' }
  return tags[level] || 'info'
}

const getWarnLevelText = (level) => {
  const texts = { 1: '一般', 2: '重要', 3: '紧急' }
  return texts[level] || '未知'
}

const getWarningStatusTag = (status) => {
  const tags = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return tags[status] || 'info'
}

const getWarningStatusText = (status) => {
  const texts = { 0: '待处理', 1: '处理中', 2: '已处理', 3: '已忽略' }
  return texts[status] || '未知'
}

const handleRoomChange = (roomId) => {
  const room = roomOptions.value.find(r => r.id === roomId)
  if (room) {
    if (dataForm.value) dataForm.value.roomName = room.roomName
    if (thresholdForm.value) thresholdForm.value.roomName = room.roomName
  }
}

const fetchStats = async () => {
  try {
    const res = await monitorApi.stats()
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

const fetchDataList = async () => {
  dataLoading.value = true
  try {
    const params = {
      pageNum: dataPagination.pageNum,
      pageSize: dataPagination.pageSize,
      monitorPoint: dataSearchKeyword.value,
      roomName: dataSearchKeyword.value,
      monitorType: dataSearchMonitorType.value,
      collectMethod: dataSearchCollectMethod.value,
      collectDate: dataSearchDate.value
    }
    const res = await monitorApi.dataPage(params)
    if (res.code === 200) {
      dataTableData.value = res.data.list
      dataPagination.total = res.data.total
    }
  } finally {
    dataLoading.value = false
  }
}

const fetchWarningList = async () => {
  warningLoading.value = true
  try {
    const params = {
      pageNum: warningPagination.pageNum,
      pageSize: warningPagination.pageSize,
      monitorPoint: warningSearchKeyword.value,
      roomName: warningSearchKeyword.value,
      warnLevel: warningSearchLevel.value,
      warningStatus: warningSearchStatus.value
    }
    const res = await monitorApi.warningPage(params)
    if (res.code === 200) {
      warningTableData.value = res.data.list
      warningPagination.total = res.data.total
    }
  } finally {
    warningLoading.value = false
  }
}

const fetchThresholdList = async () => {
  thresholdLoading.value = true
  try {
    const params = {
      pageNum: thresholdPagination.pageNum,
      pageSize: thresholdPagination.pageSize,
      monitorType: thresholdSearchMonitorType.value,
      roomId: thresholdSearchRoomId.value
    }
    const res = await monitorApi.thresholdPage(params)
    if (res.code === 200) {
      thresholdTableData.value = res.data.list
      thresholdPagination.total = res.data.total
    }
  } finally {
    thresholdLoading.value = false
  }
}

const fetchRealtimeData = async () => {
  realtimeLoading.value = true
  try {
    const params = {
      roomId: realtimeRoomId.value,
      monitorType: realtimeMonitorType.value
    }
    const res = await monitorApi.dataPage({ ...params, pageSize: 100 })
    if (res.code === 200) {
      realtimeData.value = (res.data.list || []).map(item => ({
        monitorPoint: item.monitorPoint,
        roomName: item.roomName,
        monitorType: item.monitorType,
        currentValue: item.monitorValue,
        isWarning: item.isWarning,
        updateTime: item.collectTime
      }))
      initChart()
    }
  } finally {
    realtimeLoading.value = false
  }
}

const initChart = () => {
  if (!chartRef.value) return
  nextTick(() => {
    if (!chartInstance) {
      chartInstance = echarts.init(chartRef.value)
    }
    const xData = []
    const tempData = []
    const humidityData = []
    for (let i = 23; i >= 0; i--) {
      const d = new Date()
      d.setHours(d.getHours() - i)
      xData.push(`${d.getHours()}:00`)
      tempData.push((20 + Math.random() * 5).toFixed(1))
      humidityData.push((45 + Math.random() * 15).toFixed(1))
    }
    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['温度(°C)', '湿度(%RH)'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: xData },
      yAxis: [
        { type: 'value', name: '温度(°C)' },
        { type: 'value', name: '湿度(%RH)' }
      ],
      series: [
        {
          name: '温度(°C)',
          type: 'line',
          smooth: true,
          data: tempData,
          itemStyle: { color: '#f56c6c' }
        },
        {
          name: '湿度(%RH)',
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          data: humidityData,
          itemStyle: { color: '#409eff' }
        }
      ]
    }
    chartInstance.setOption(option)
  })
}

const initDetailChart = () => {
  if (!detailChartRef.value) return
  nextTick(() => {
    if (!detailChartInstance) {
      detailChartInstance = echarts.init(detailChartRef.value)
    }
    const xData = []
    const yData = []
    for (let i = 6; i >= 0; i--) {
      const d = new Date()
      d.setDate(d.getDate() - i)
      xData.push(`${d.getMonth() + 1}/${d.getDate()}`)
      const base = currentRealtimePoint.value.monitorType === 1 ? 22 :
                   currentRealtimePoint.value.monitorType === 2 ? 50 :
                   currentRealtimePoint.value.monitorType === 3 ? 15 : 55
      yData.push((base + (Math.random() - 0.5) * 6).toFixed(1))
    }
    const unit = getMonitorUnit(currentRealtimePoint.value.monitorType)
    const option = {
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: xData },
      yAxis: { type: 'value', name: unit },
      series: [{
        name: '监测值',
        type: 'line',
        smooth: true,
        data: yData,
        areaStyle: { opacity: 0.3 },
        itemStyle: { color: '#409eff' }
      }]
    }
    detailChartInstance.setOption(option)
  })
}

const handleDataAdd = () => {
  isDataView.value = false
  dataDialogTitle.value = '新增监控数据'
  const now = new Date()
  dataForm.value = {
    id: null,
    monitorPoint: '',
    roomId: null,
    roomName: '',
    monitorType: 1,
    monitorValue: null,
    collectMethod: 1,
    collectTime: now.toISOString().slice(0, 19).replace('T', ' '),
    collectorName: '',
    remark: ''
  }
  dataDialogVisible.value = true
}

const handleDataEdit = (row) => {
  isDataView.value = false
  dataDialogTitle.value = '编辑监控数据'
  dataForm.value = { ...row }
  dataDialogVisible.value = true
}

const handleDataView = (row) => {
  isDataView.value = true
  dataDialogTitle.value = '监控数据详情'
  dataDetailLoading.value = true
  monitorApi.dataGet(row.id).then(res => {
    if (res.code === 200) {
      dataForm.value = { ...res.data }
    }
    dataDetailLoading.value = false
  }).catch(() => {
    dataDetailLoading.value = false
  })
  dataDialogVisible.value = true
}

const handleDataDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条监控数据吗？', '提示', { type: 'warning' })
    .then(async () => {
      const res = await monitorApi.dataDelete(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchDataList()
        fetchStats()
      }
    }).catch(() => {})
}

const handleDataSubmit = async () => {
  if (!dataFormRef.value) return
  try {
    await dataFormRef.value.validate()
    let res
    if (dataForm.value.id) {
      res = await monitorApi.dataUpdate(dataForm.value)
    } else {
      res = await monitorApi.dataSave(dataForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(dataForm.value.id ? '更新成功' : '新增成功')
      dataDialogVisible.value = false
      fetchDataList()
      fetchStats()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleWarningHandle = (row) => {
  currentWarning.value = { ...row }
  warningHandleForm.value = { handleResult: '', handleRemark: '' }
  warningHandleDialogVisible.value = true
}

const handleWarningIgnore = (row) => {
  ElMessageBox.confirm('确定要忽略这条预警吗？', '提示', { type: 'warning' })
    .then(async () => {
      const res = await monitorApi.warningIgnore(row.id)
      if (res.code === 200) {
        ElMessage.success('已忽略')
        fetchWarningList()
        fetchStats()
      }
    }).catch(() => {})
}

const handleWarningView = (row) => {
  currentWarning.value = { ...row }
  warningHandleForm.value = {
    handleResult: row.handleResult || '',
    handleRemark: row.handleRemark || ''
  }
  warningHandleDialogVisible.value = true
}

const handleWarningHandleSubmit = async () => {
  if (!warningHandleFormRef.value) return
  try {
    await warningHandleFormRef.value.validate()
    const data = {
      id: currentWarning.value.id,
      ...warningHandleForm.value
    }
    const res = await monitorApi.warningHandle(data)
    if (res.code === 200) {
      ElMessage.success('处理成功')
      warningHandleDialogVisible.value = false
      fetchWarningList()
      fetchStats()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleThresholdAdd = () => {
  thresholdDialogTitle.value = '新增阈值配置'
  thresholdForm.value = {
    id: null,
    monitorPoint: '',
    roomId: null,
    roomName: '',
    monitorType: 1,
    thresholdMin: null,
    thresholdMax: null,
    warnLevel: 1,
    isEnabled: 1,
    remark: ''
  }
  thresholdDialogVisible.value = true
}

const handleThresholdEdit = (row) => {
  thresholdDialogTitle.value = '编辑阈值配置'
  thresholdForm.value = { ...row }
  thresholdDialogVisible.value = true
}

const handleThresholdToggle = (row) => {
  const action = row.isEnabled ? '停用' : '启用'
  ElMessageBox.confirm(`确定要${action}这条阈值配置吗？`, '提示', { type: 'warning' })
    .then(async () => {
      const res = await monitorApi.thresholdToggle(row.id, row.isEnabled ? 0 : 1)
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        fetchThresholdList()
      }
    }).catch(() => {})
}

const handleThresholdDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条阈值配置吗？', '提示', { type: 'warning' })
    .then(async () => {
      const res = await monitorApi.thresholdDelete(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchThresholdList()
      }
    }).catch(() => {})
}

const handleThresholdSubmit = async () => {
  if (!thresholdFormRef.value) return
  try {
    await thresholdFormRef.value.validate()
    let res
    if (thresholdForm.value.id) {
      res = await monitorApi.thresholdUpdate(thresholdForm.value)
    } else {
      res = await monitorApi.thresholdSave(thresholdForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(thresholdForm.value.id ? '更新成功' : '新增成功')
      thresholdDialogVisible.value = false
      fetchThresholdList()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleRealtimeDetail = (row) => {
  currentRealtimePoint.value = { ...row }
  realtimeDetailDialogVisible.value = true
  nextTick(() => {
    initDetailChart()
  })
}

watch(activeMainTab, (val) => {
  if (val === 'data') {
    fetchDataList()
  } else if (val === 'realtime') {
    fetchRealtimeData()
  } else if (val === 'warning') {
    fetchWarningList()
  } else if (val === 'threshold') {
    fetchThresholdList()
  }
})

onMounted(() => {
  fetchStats()
  fetchRooms()
  fetchDataList()
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

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ff416c 0%, #ff4b2b 100%);
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
