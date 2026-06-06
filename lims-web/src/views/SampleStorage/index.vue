<template>
  <div class="sample-storage">
    <a-layout style="background: #fff; min-height: calc(100vh - 120px)">
      <a-layout-sider width="280" style="background: #fff; border-right: 1px solid #f0f0f0">
        <div class="sider-header">
          <span class="sider-title">存储结构</span>
          <div class="sider-actions">
            <a-dropdown>
              <a-button size="small" type="primary">
                <PlusOutlined /> 新增
              </a-button>
              <template #overlay>
                <a-menu @click="handleAddStorage">
                  <a-menu-item key="warehouse">
                    <DatabaseOutlined /> 新增库房
                  </a-menu-item>
                  <a-menu-item key="refrigerator">
                    <CloudServerOutlined /> 新增冰箱
                  </a-menu-item>
                  <a-menu-item key="shelf">
                    <AppstoreOutlined /> 新增货架
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>
        <a-tree
          :tree-data="storageTree"
          :selected-keys="selectedTreeKeys"
          :expanded-keys="expandedTreeKeys"
          @select="handleTreeSelect"
          @expand="handleTreeExpand"
          show-icon
          :field-names="{ title: 'name', key: 'id', children: 'children' }"
        >
          <template #icon="{ type }">
            <HomeOutlined v-if="type === 'warehouse'" />
            <CloudServerOutlined v-else-if="type === 'refrigerator'" />
            <AppstoreOutlined v-else-if="type === 'shelf'" />
          </template>
          <template #title="{ name, type, warningCount }">
            <span class="tree-node-title">{{ name }}</span>
            <a-badge v-if="warningCount && warningCount > 0" :count="warningCount" size="small" />
          </template>
        </a-tree>
      </a-layout-sider>

      <a-layout-content style="padding: 16px">
        <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
          <a-tab-pane key="storage" tab="存储视图">
            <div class="toolbar">
              <a-form layout="inline" :model="queryParams">
                <a-form-item label="样品编号">
                  <a-input v-model:value="queryParams.sampleCode" placeholder="请输入样品编号" style="width: 160px" allow-clear />
                </a-form-item>
                <a-form-item label="样品名称">
                  <a-input v-model:value="queryParams.sampleName" placeholder="请输入样品名称" style="width: 160px" allow-clear />
                </a-form-item>
                <a-form-item label="存储状态">
                  <a-select v-model:value="queryParams.storageStatus" placeholder="请选择" style="width: 120px" allow-clear>
                    <a-select-option :value="1">在库</a-select-option>
                    <a-select-option :value="2">出库中</a-select-option>
                    <a-select-option :value="3">已出库</a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="预警状态">
                  <a-select v-model:value="queryParams.warningFlag" placeholder="请选择" style="width: 120px" allow-clear>
                    <a-select-option :value="1">有预警</a-select-option>
                    <a-select-option :value="0">无预警</a-select-option>
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
                <a-button type="primary" @click="handleInbound">
                  <InboxOutlined /> 样品入库
                </a-button>
                <a-button style="margin-left: 8px" @click="handleOutbound">
                  <ExportOutlined /> 样品出库
                </a-button>
              </div>
            </div>

            <div class="storage-grid">
              <div
                v-for="cell in storageCells"
                :key="cell.locationCode"
                class="storage-cell"
                :class="{
                  'cell-warning': cell.warningCount > 0,
                  'cell-drag-over': dragOverLocation === cell.locationCode
                }"
                draggable="true"
                @dragstart="(e) => handleDragStart(e, cell)"
                @dragover="(e) => handleDragOver(e, cell.locationCode)"
                @dragleave="handleDragLeave"
                @drop="(e) => handleDrop(e, cell.locationCode)"
                @click="handleCellClick(cell)"
              >
                <div class="cell-header">
                  <span class="cell-location">{{ cell.locationCode }}</span>
                  <a-tag v-if="cell.warningCount > 0" color="red" size="small">
                    <WarningOutlined /> {{ cell.warningCount }}
                  </a-tag>
                </div>
                <div class="cell-body">
                  <div class="cell-count">{{ cell.sampleCount || 0 }}</div>
                  <div class="cell-label">样品数</div>
                </div>
                <div class="cell-footer" v-if="cell.samples && cell.samples.length > 0">
                  <div class="cell-sample" v-for="sample in cell.samples.slice(0, 3)" :key="sample.id">
                    <span :class="{ 'text-warning': sample.warningFlag === 1 }">{{ sample.sampleCode }}</span>
                  </div>
                  <div v-if="cell.samples.length > 3" class="cell-more">
                    等 {{ cell.samples.length }} 个样品
                  </div>
                </div>
              </div>
            </div>

            <a-table
              :columns="storageColumns"
              :data-source="storageTableData"
              :loading="storageLoading"
              :pagination="storagePagination"
              row-key="id"
              @change="handleStorageTableChange"
              style="margin-top: 16px"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'storageStatus'">
                  <a-tag :color="getStorageStatusColor(record.storageStatus)">
                    {{ record.storageStatusName || '-' }}
                  </a-tag>
                </template>
                <template v-else-if="column.key === 'warningFlag'">
                  <a-tag v-if="record.warningFlag === 1" color="red">
                    <WarningOutlined /> {{ record.warningFlagName }}
                  </a-tag>
                  <span v-else>-</span>
                </template>
                <template v-else-if="column.key === 'location'">
                  <span>{{ [record.warehouseName, record.refrigeratorName, record.shelfName, record.locationCode].filter(Boolean).join(' / ') }}</span>
                </template>
                <template v-else-if="column.key === 'remainingDays'">
                  <span :class="{ 'text-red': record.remainingDays !== undefined && record.remainingDays <= 7 }">
                    {{ record.remainingDays !== undefined ? record.remainingDays + ' 天' : '-' }}
                  </span>
                </template>
                <template v-else-if="column.key === 'action'">
                  <a-button type="link" size="small" @click="handleViewLog(record)">记录</a-button>
                  <a-button type="link" size="small" @click="handleMove(record)">移动</a-button>
                  <a-button type="link" size="small" @click="handleOutboundSingle(record)">出库</a-button>
                </template>
              </template>
            </a-table>
          </a-tab-pane>

          <a-tab-pane key="warning" tab="库存预警">
            <div class="toolbar">
              <a-form layout="inline" :model="warningQueryParams">
                <a-form-item label="样品编号">
                  <a-input v-model:value="warningQueryParams.sampleCode" placeholder="请输入样品编号" style="width: 160px" allow-clear />
                </a-form-item>
                <a-form-item label="样品名称">
                  <a-input v-model:value="warningQueryParams.sampleName" placeholder="请输入样品名称" style="width: 160px" allow-clear />
                </a-form-item>
                <a-form-item label="预警类型">
                  <a-select v-model:value="warningTypeFilter" placeholder="请选择" style="width: 140px" allow-clear>
                    <a-select-option value="expire">过期预警</a-select-option>
                    <a-select-option value="low">库存不足</a-select-option>
                    <a-select-option value="temp">温度异常</a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item>
                  <a-button type="primary" @click="handleWarningQuery">
                    <SearchOutlined /> 查询
                  </a-button>
                  <a-button style="margin-left: 8px" @click="handleWarningReset">
                    <ReloadOutlined /> 重置
                  </a-button>
                </a-form-item>
              </a-form>
            </div>

            <a-table
              :columns="warningColumns"
              :data-source="warningTableData"
              :loading="warningLoading"
              :pagination="warningPagination"
              row-key="id"
              @change="handleWarningTableChange"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'warningType'">
                  <a-tag :color="getWarningTypeColor(record.warningMessage)">
                    <WarningOutlined /> {{ record.warningFlagName || record.warningMessage || '-' }}
                  </a-tag>
                </template>
                <template v-else-if="column.key === 'location'">
                  <span>{{ [record.warehouseName, record.refrigeratorName, record.shelfName, record.locationCode].filter(Boolean).join(' / ') }}</span>
                </template>
                <template v-else-if="column.key === 'remainingDays'">
                  <span :class="{ 'text-red': record.remainingDays !== undefined && record.remainingDays <= 7 }">
                    {{ record.remainingDays !== undefined ? record.remainingDays + ' 天' : '-' }}
                  </span>
                </template>
                <template v-else-if="column.key === 'action'">
                  <a-button type="link" size="small" @click="handleViewLog(record)">查看记录</a-button>
                  <a-button type="link" size="small" @click="handleMove(record)">处理</a-button>
                </template>
              </template>
            </a-table>
          </a-tab-pane>

          <a-tab-pane key="log" tab="出入库记录">
            <div class="toolbar">
              <a-form layout="inline" :model="logQueryParams">
                <a-form-item label="样品编号">
                  <a-input v-model:value="logQueryParams.sampleCode" placeholder="请输入样品编号" style="width: 160px" allow-clear />
                </a-form-item>
                <a-form-item label="样品名称">
                  <a-input v-model:value="logQueryParams.sampleName" placeholder="请输入样品名称" style="width: 160px" allow-clear />
                </a-form-item>
                <a-form-item label="操作类型">
                  <a-select v-model:value="logQueryParams.operateType" placeholder="请选择" style="width: 120px" allow-clear>
                    <a-select-option :value="1">入库</a-select-option>
                    <a-select-option :value="2">出库</a-select-option>
                    <a-select-option :value="3">移动</a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="操作时间">
                  <a-range-picker v-model:value="logTimeRange" value-format="YYYY-MM-DD HH:mm:ss" style="width: 260px" />
                </a-form-item>
                <a-form-item>
                  <a-button type="primary" @click="handleLogQuery">
                    <SearchOutlined /> 查询
                  </a-button>
                  <a-button style="margin-left: 8px" @click="handleLogReset">
                    <ReloadOutlined /> 重置
                  </a-button>
                </a-form-item>
              </a-form>
            </div>

            <a-table
              :columns="logColumns"
              :data-source="logTableData"
              :loading="logLoading"
              :pagination="logPagination"
              row-key="id"
              @change="handleLogTableChange"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'operateType'">
                  <a-tag :color="getOperateTypeColor(record.operateType)">
                    <InboxOutlined v-if="record.operateType === 1" />
                    <ExportOutlined v-else-if="record.operateType === 2" />
                    <span v-else>↔</span>
                    {{ record.operateTypeName || '-' }}
                  </a-tag>
                </template>
                <template v-else-if="column.key === 'location'">
                  <span>{{ [record.warehouseName, record.refrigeratorName, record.shelfName, record.locationCode].filter(Boolean).join(' / ') || '-' }}</span>
                </template>
              </template>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </a-layout-content>
    </a-layout>

    <a-modal
      v-model:open="warehouseModalVisible"
      :title="warehouseModalTitle"
      width="600px"
      @ok="handleWarehouseSubmit"
      @cancel="warehouseModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="warehouseFormRef" :model="warehouseFormData" :rules="warehouseFormRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="库房编码" name="warehouseCode">
              <a-input v-model:value="warehouseFormData.warehouseCode" placeholder="请输入库房编码" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="库房名称" name="warehouseName">
              <a-input v-model:value="warehouseFormData.warehouseName" placeholder="请输入库房名称" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="库房类型" name="warehouseType">
              <a-select v-model:value="warehouseFormData.warehouseType" placeholder="请选择">
                <a-select-option :value="1">普通库房</a-select-option>
                <a-select-option :value="2">冷藏库房</a-select-option>
                <a-select-option :value="3">冷冻库房</a-select-option>
                <a-select-option :value="4">恒温恒湿库房</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="warehouseFormData.status" placeholder="请选择">
                <a-select-option :value="1">启用</a-select-option>
                <a-select-option :value="0">停用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="温度范围(℃)">
              <a-input-group compact>
                <a-input-number v-model:value="warehouseFormData.temperatureMin" placeholder="最低" style="width: 45%" />
                <a-input placeholder="~" disabled style="width: 10%" />
                <a-input-number v-model:value="warehouseFormData.temperatureMax" placeholder="最高" style="width: 45%" />
              </a-input-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="湿度范围(%)">
              <a-input-group compact>
                <a-input-number v-model:value="warehouseFormData.humidityMin" placeholder="最低" style="width: 45%" />
                <a-input placeholder="~" disabled style="width: 10%" />
                <a-input-number v-model:value="warehouseFormData.humidityMax" placeholder="最高" style="width: 45%" />
              </a-input-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="位置">
          <a-input v-model:value="warehouseFormData.location" placeholder="请输入库房位置" />
        </a-form-item>
        <a-form-item label="负责人">
          <a-select v-model:value="warehouseFormData.managerId" placeholder="请选择负责人" allow-clear>
            <a-select-option :value="1">管理员</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="warehouseFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="refrigeratorModalVisible"
      :title="refrigeratorModalTitle"
      width="600px"
      @ok="handleRefrigeratorSubmit"
      @cancel="refrigeratorModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="refrigeratorFormRef" :model="refrigeratorFormData" :rules="refrigeratorFormRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="所属库房" name="warehouseId">
              <a-select v-model:value="refrigeratorFormData.warehouseId" placeholder="请选择库房">
                <a-select-option v-for="item in warehouseList" :key="item.id" :value="item.id">
                  {{ item.warehouseName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="冰箱编码" name="refrigeratorCode">
              <a-input v-model:value="refrigeratorFormData.refrigeratorCode" placeholder="请输入冰箱编码" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="冰箱名称" name="refrigeratorName">
              <a-input v-model:value="refrigeratorFormData.refrigeratorName" placeholder="请输入冰箱名称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="refrigeratorFormData.status" placeholder="请选择">
                <a-select-option :value="1">启用</a-select-option>
                <a-select-option :value="0">停用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="温度范围(℃)">
              <a-input-group compact>
                <a-input-number v-model:value="refrigeratorFormData.temperatureMin" placeholder="最低" style="width: 45%" />
                <a-input placeholder="~" disabled style="width: 10%" />
                <a-input-number v-model:value="refrigeratorFormData.temperatureMax" placeholder="最高" style="width: 45%" />
              </a-input-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="当前温度(℃)">
              <a-input-number v-model:value="refrigeratorFormData.currentTemperature" placeholder="请输入当前温度" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="refrigeratorFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="shelfModalVisible"
      :title="shelfModalTitle"
      width="600px"
      @ok="handleShelfSubmit"
      @cancel="shelfModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="shelfFormRef" :model="shelfFormData" :rules="shelfFormRules" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="所属库房" name="warehouseId">
              <a-select v-model:value="shelfFormData.warehouseId" placeholder="请选择库房" @change="handleWarehouseChange">
                <a-select-option v-for="item in warehouseList" :key="item.id" :value="item.id">
                  {{ item.warehouseName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="所属冰箱" name="refrigeratorId">
              <a-select v-model:value="shelfFormData.refrigeratorId" placeholder="请选择冰箱" allow-clear>
                <a-select-option v-for="item in refrigeratorList" :key="item.id" :value="item.id">
                  {{ item.refrigeratorName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="货架编码" name="shelfCode">
              <a-input v-model:value="shelfFormData.shelfCode" placeholder="请输入货架编码" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="货架名称" name="shelfName">
              <a-input v-model:value="shelfFormData.shelfName" placeholder="请输入货架名称" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="层数">
              <a-input-number v-model:value="shelfFormData.layerCount" :min="1" placeholder="层数" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="容量">
              <a-input-number v-model:value="shelfFormData.capacity" :min="0" placeholder="容量" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="shelfFormData.status" placeholder="请选择">
                <a-select-option :value="1">启用</a-select-option>
                <a-select-option :value="0">停用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="shelfFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="inboundModalVisible"
      title="样品入库"
      width="700px"
      @ok="handleInboundSubmit"
      @cancel="inboundModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="inboundFormRef" :model="inboundFormData" :rules="inboundFormRules" layout="vertical">
        <a-form-item label="选择样品" name="sampleIds">
          <a-select
            v-model:value="inboundFormData.sampleIds"
            mode="multiple"
            placeholder="请选择要入库的样品"
            :filter-option="(input, option) => (option?.label ?? '').includes(input)"
            :options="availableSampleOptions"
            style="width: 100%"
          />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="库房" name="warehouseId">
              <a-select v-model:value="inboundFormData.warehouseId" placeholder="请选择库房" @change="handleInboundWarehouseChange">
                <a-select-option v-for="item in warehouseList" :key="item.id" :value="item.id">
                  {{ item.warehouseName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="冰箱" name="refrigeratorId">
              <a-select v-model:value="inboundFormData.refrigeratorId" placeholder="请选择冰箱" allow-clear :disabled="!inboundFormData.warehouseId">
                <a-select-option v-for="item in inboundRefrigeratorList" :key="item.id" :value="item.id">
                  {{ item.refrigeratorName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="货架" name="shelfId">
              <a-select v-model:value="inboundFormData.shelfId" placeholder="请选择货架" allow-clear>
                <a-select-option v-for="item in inboundShelfList" :key="item.id" :value="item.id">
                  {{ item.shelfName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="库位编码" name="locationCode">
              <a-input v-model:value="inboundFormData.locationCode" placeholder="请输入库位编码" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="入库时间" name="operateTime">
              <a-date-picker v-model:value="inboundFormData.operateTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="inboundFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="outboundModalVisible"
      title="样品出库"
      width="600px"
      @ok="handleOutboundSubmit"
      @cancel="outboundModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="outboundFormRef" :model="outboundFormData" :rules="outboundFormRules" layout="vertical">
        <a-form-item label="选择样品" name="sampleIds">
          <a-select
            v-model:value="outboundFormData.sampleIds"
            mode="multiple"
            placeholder="请选择要出库的样品"
            :options="storageSampleOptions"
            style="width: 100%"
          />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="出库时间" name="operateTime">
              <a-date-picker v-model:value="outboundFormData.operateTime" show-time style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="操作人">
              <a-input v-model:value="outboundFormData.operatorName" placeholder="请输入操作人" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="备注">
          <a-textarea v-model:value="outboundFormData.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="moveModalVisible"
      title="移动样品"
      width="600px"
      @ok="handleMoveSubmit"
      @cancel="moveModalVisible = false"
      :confirm-loading="submitting"
    >
      <a-form ref="moveFormRef" :model="moveFormData" :rules="moveFormRules" layout="vertical">
        <a-form-item label="当前样品">
          <a-tag color="blue">{{ moveSampleInfo?.sampleCode }} - {{ moveSampleInfo?.sampleName }}</a-tag>
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="目标库房" name="targetWarehouseId">
              <a-select v-model:value="moveFormData.targetWarehouseId" placeholder="请选择库房" @change="handleMoveWarehouseChange">
                <a-select-option v-for="item in warehouseList" :key="item.id" :value="item.id">
                  {{ item.warehouseName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="目标冰箱" name="targetRefrigeratorId">
              <a-select v-model:value="moveFormData.targetRefrigeratorId" placeholder="请选择冰箱" allow-clear :disabled="!moveFormData.targetWarehouseId">
                <a-select-option v-for="item in moveRefrigeratorList" :key="item.id" :value="item.id">
                  {{ item.refrigeratorName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="目标货架" name="targetShelfId">
              <a-select v-model:value="moveFormData.targetShelfId" placeholder="请选择货架" allow-clear>
                <a-select-option v-for="item in moveShelfList" :key="item.id" :value="item.id">
                  {{ item.shelfName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="目标库位" name="targetLocationCode">
          <a-input v-model:value="moveFormData.targetLocationCode" placeholder="请输入目标库位编码" />
        </a-form-item>
        <a-form-item label="移动原因" name="moveReason">
          <a-textarea v-model:value="moveFormData.moveReason" :rows="2" placeholder="请输入移动原因" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="logModalVisible"
      title="存储操作记录"
      width="800px"
      :footer="null"
    >
      <a-table
        :columns="sampleLogColumns"
        :data-source="sampleLogData"
        :loading="sampleLogLoading"
        :pagination="false"
        row-key="id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'operateType'">
            <a-tag :color="getOperateTypeColor(record.operateType)">
              {{ record.operateTypeName || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'location'">
            <span>{{ [record.warehouseName, record.refrigeratorName, record.shelfName, record.locationCode].filter(Boolean).join(' / ') || '-' }}</span>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  DatabaseOutlined,
  HomeOutlined,
  CloudServerOutlined,
  AppstoreOutlined,
  WarningOutlined,
  InboxOutlined,
  ExportOutlined,
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import type {
  SampleStorageQuery,
  SampleStorageSaveDTO,
  SampleStorageMoveDTO,
  SampleInOutDTO,
  SampleStorageVO,
  SampleStorageLogVO,
  StorageWarehouseSaveDTO,
  StorageRefrigeratorSaveDTO,
  StorageShelfSaveDTO,
  StorageWarehouseVO,
  StorageRefrigeratorVO,
  StorageShelfVO,
  SampleVO
} from '@/types'
import {
  getSampleStoragePage,
  getStorageTree,
  getWarehouseList,
  getRefrigeratorList,
  getShelfList,
  saveWarehouse,
  updateWarehouse,
  deleteWarehouse,
  saveRefrigerator,
  updateRefrigerator,
  deleteRefrigerator,
  saveShelf,
  updateShelf,
  deleteShelf,
  sampleInbound,
  sampleOutbound,
  moveSampleStorage,
  getStorageLogBySampleId,
  getSampleList
} from '@/api/sample'

const loading = ref(false)
const storageLoading = ref(false)
const warningLoading = ref(false)
const logLoading = ref(false)
const sampleLogLoading = ref(false)
const submitting = ref(false)

const activeTab = ref('storage')
const selectedTreeKeys = ref<string[]>([])
const expandedTreeKeys = ref<string[]>([])
const currentTreeNode = ref<any>(null)
const warningTypeFilter = ref<string | undefined>()
const logTimeRange = ref<string[]>([])

const storageTree = ref<any[]>([])
const warehouseList = ref<StorageWarehouseVO[]>([])
const refrigeratorList = ref<StorageRefrigeratorVO[]>([])
const shelfList = ref<StorageShelfVO[]>([])
const availableSampleList = ref<SampleVO[]>([])
const dragOverLocation = ref<string | null>(null)
const dragSample = ref<SampleStorageVO | null>(null)

const warehouseModalVisible = ref(false)
const warehouseModalTitle = ref('')
const refrigeratorModalVisible = ref(false)
const refrigeratorModalTitle = ref('')
const shelfModalVisible = ref(false)
const shelfModalTitle = ref('')
const inboundModalVisible = ref(false)
const outboundModalVisible = ref(false)
const moveModalVisible = ref(false)
const logModalVisible = ref(false)

const warehouseFormRef = ref()
const refrigeratorFormRef = ref()
const shelfFormRef = ref()
const inboundFormRef = ref()
const outboundFormRef = ref()
const moveFormRef = ref()

const isWarehouseEdit = ref(false)
const isRefrigeratorEdit = ref(false)
const isShelfEdit = ref(false)
const currentWarehouseId = ref<number>()
const currentRefrigeratorId = ref<number>()
const currentShelfId = ref<number>()
const moveSampleInfo = ref<SampleStorageVO | null>(null)
const outboundSingle = ref(false)

const queryParams = reactive<SampleStorageQuery>({
  pageNum: 1,
  pageSize: 10,
  sampleCode: '',
  sampleName: '',
  warehouseId: undefined,
  refrigeratorId: undefined,
  shelfId: undefined,
  storageStatus: undefined,
  warningFlag: undefined
})

const warningQueryParams = reactive<SampleStorageQuery>({
  pageNum: 1,
  pageSize: 10,
  sampleCode: '',
  sampleName: '',
  warehouseId: undefined,
  refrigeratorId: undefined,
  shelfId: undefined,
  warningFlag: 1
})

const logQueryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  sampleCode: '',
  sampleName: '',
  operateType: undefined,
  operateTimeStart: '',
  operateTimeEnd: ''
})

const storagePagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const warningPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const logPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

const storageTableData = ref<SampleStorageVO[]>([])
const warningTableData = ref<SampleStorageVO[]>([])
const logTableData = ref<SampleStorageLogVO[]>([])
const sampleLogData = ref<SampleStorageLogVO[]>([])

const warehouseFormData = reactive<StorageWarehouseSaveDTO>({
  warehouseCode: '',
  warehouseName: '',
  warehouseType: 1,
  status: 1
})

const refrigeratorFormData = reactive<StorageRefrigeratorSaveDTO>({
  warehouseId: 0,
  refrigeratorCode: '',
  refrigeratorName: '',
  status: 1
})

const shelfFormData = reactive<StorageShelfSaveDTO>({
  warehouseId: 0,
  shelfCode: '',
  shelfName: '',
  layerCount: 1,
  capacity: 0,
  status: 1
})

const inboundFormData = reactive<SampleInOutDTO>({
  sampleIds: [],
  operateType: 1,
  warehouseId: undefined,
  refrigeratorId: undefined,
  shelfId: undefined,
  locationCode: '',
  operateTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
  remark: ''
})

const outboundFormData = reactive<SampleInOutDTO>({
  sampleIds: [],
  operateType: 2,
  operateTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
  remark: ''
})

const moveFormData = reactive<SampleStorageMoveDTO>({
  id: 0,
  targetWarehouseId: 0,
  targetRefrigeratorId: undefined,
  targetShelfId: undefined,
  targetLocationCode: '',
  moveReason: ''
})

const warehouseFormRules = {
  warehouseCode: [{ required: true, message: '请输入库房编码', trigger: 'blur' }],
  warehouseName: [{ required: true, message: '请输入库房名称', trigger: 'blur' }],
  warehouseType: [{ required: true, message: '请选择库房类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const refrigeratorFormRules = {
  warehouseId: [{ required: true, message: '请选择所属库房', trigger: 'change' }],
  refrigeratorCode: [{ required: true, message: '请输入冰箱编码', trigger: 'blur' }],
  refrigeratorName: [{ required: true, message: '请输入冰箱名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const shelfFormRules = {
  warehouseId: [{ required: true, message: '请选择所属库房', trigger: 'change' }],
  shelfCode: [{ required: true, message: '请输入货架编码', trigger: 'blur' }],
  shelfName: [{ required: true, message: '请输入货架名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const inboundFormRules = {
  sampleIds: [{ required: true, message: '请选择样品', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择库房', trigger: 'change' }],
  operateTime: [{ required: true, message: '请选择入库时间', trigger: 'change' }]
}

const outboundFormRules = {
  sampleIds: [{ required: true, message: '请选择样品', trigger: 'change' }],
  operateTime: [{ required: true, message: '请选择出库时间', trigger: 'change' }]
}

const moveFormRules = {
  targetWarehouseId: [{ required: true, message: '请选择目标库房', trigger: 'change' }],
  targetLocationCode: [{ required: true, message: '请输入目标库位编码', trigger: 'blur' }]
}

const storageColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 160, ellipsis: true },
  { title: '存储位置', key: 'location', width: 200, ellipsis: true },
  { title: '库位', dataIndex: 'locationCode', key: 'locationCode', width: 100 },
  { title: '存储状态', dataIndex: 'storageStatus', key: 'storageStatus', width: 100 },
  { title: '预警状态', dataIndex: 'warningFlag', key: 'warningFlag', width: 120 },
  { title: '剩余天数', dataIndex: 'remainingDays', key: 'remainingDays', width: 100 },
  { title: '有效期', dataIndex: 'expiryDate', key: 'expiryDate', width: 120 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' }
]

const warningColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 160, ellipsis: true },
  { title: '存储位置', key: 'location', width: 200, ellipsis: true },
  { title: '预警类型', key: 'warningType', width: 140 },
  { title: '剩余天数', dataIndex: 'remainingDays', key: 'remainingDays', width: 100 },
  { title: '有效期', dataIndex: 'expiryDate', key: 'expiryDate', width: 120 },
  { title: '操作', key: 'action', width: 160, fixed: 'right' }
]

const logColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '样品编号', dataIndex: 'sampleCode', key: 'sampleCode', width: 140 },
  { title: '样品名称', dataIndex: 'sampleName', key: 'sampleName', width: 160, ellipsis: true },
  { title: '操作类型', dataIndex: 'operateType', key: 'operateType', width: 100 },
  { title: '存储位置', key: 'location', width: 200, ellipsis: true },
  { title: '操作人', dataIndex: 'operatorName', key: 'operatorName', width: 100 },
  { title: '操作时间', dataIndex: 'operateTime', key: 'operateTime', width: 160 },
  { title: '备注', dataIndex: 'remark', key: 'remark', width: 150, ellipsis: true }
]

const sampleLogColumns = [
  { title: '序号', dataIndex: 'index', key: 'index', width: 70, customRender: ({ index }: { index: number }) => index + 1 },
  { title: '操作类型', dataIndex: 'operateType', key: 'operateType', width: 100 },
  { title: '存储位置', key: 'location', width: 200, ellipsis: true },
  { title: '操作人', dataIndex: 'operatorName', key: 'operatorName', width: 100 },
  { title: '操作时间', dataIndex: 'operateTime', key: 'operateTime', width: 160 },
  { title: '备注', dataIndex: 'remark', key: 'remark', ellipsis: true }
]

const storageCells = computed(() => {
  const cells: any[] = []
  const locationMap = new Map<string, any>()

  storageTableData.value.forEach(item => {
    const locationCode = item.locationCode || '未分配'
    if (!locationMap.has(locationCode)) {
      locationMap.set(locationCode, {
        locationCode,
        sampleCount: 0,
        warningCount: 0,
        samples: []
      })
    }
    const cell = locationMap.get(locationCode)
    cell.sampleCount++
    if (item.warningFlag === 1) cell.warningCount++
    cell.samples.push(item)
  })

  locationMap.forEach(cell => cells.push(cell))
  if (cells.length === 0) {
    for (let i = 1; i <= 12; i++) {
      cells.push({
        locationCode: `A-${String(i).padStart(2, '0')}`,
        sampleCount: 0,
        warningCount: 0,
        samples: []
      })
    }
  }
  return cells
})

const availableSampleOptions = computed(() => {
  return availableSampleList.value.map(item => ({
    label: `${item.sampleCode} - ${item.sampleName}`,
    value: item.id
  }))
})

const storageSampleOptions = computed(() => {
  return storageTableData.value
    .filter(item => item.storageStatus === 1)
    .map(item => ({
      label: `${item.sampleCode} - ${item.sampleName}`,
      value: item.sampleId
    }))
})

const inboundRefrigeratorList = computed(() => {
  if (!inboundFormData.warehouseId) return []
  return refrigeratorList.value.filter(item => item.warehouseId === inboundFormData.warehouseId)
})

const inboundShelfList = computed(() => {
  if (!inboundFormData.warehouseId) return []
  return shelfList.value.filter(item =>
    item.warehouseId === inboundFormData.warehouseId &&
    (!inboundFormData.refrigeratorId || item.refrigeratorId === inboundFormData.refrigeratorId)
  )
})

const moveRefrigeratorList = computed(() => {
  if (!moveFormData.targetWarehouseId) return []
  return refrigeratorList.value.filter(item => item.warehouseId === moveFormData.targetWarehouseId)
})

const moveShelfList = computed(() => {
  if (!moveFormData.targetWarehouseId) return []
  return shelfList.value.filter(item =>
    item.warehouseId === moveFormData.targetWarehouseId &&
    (!moveFormData.targetRefrigeratorId || item.refrigeratorId === moveFormData.targetRefrigeratorId)
  )
})

const fetchStorageTree = async () => {
  try {
    const res = await getStorageTree()
    storageTree.value = res.data || []
  } catch (error) {
    console.error('Get storage tree error:', error)
  }
}

const fetchWarehouseList = async () => {
  try {
    const res = await getWarehouseList({ status: 1 })
    warehouseList.value = res.data || []
  } catch (error) {
    console.error('Get warehouse list error:', error)
  }
}

const fetchRefrigeratorList = async () => {
  try {
    const res = await getRefrigeratorList()
    refrigeratorList.value = res.data || []
  } catch (error) {
    console.error('Get refrigerator list error:', error)
  }
}

const fetchShelfList = async () => {
  try {
    const res = await getShelfList({ status: 1 })
    shelfList.value = res.data || []
  } catch (error) {
    console.error('Get shelf list error:', error)
  }
}

const fetchAvailableSamples = async () => {
  try {
    const res = await getSampleList({ sampleStatus: 1 })
    availableSampleList.value = res.data || []
  } catch (error) {
    console.error('Get available samples error:', error)
  }
}

const fetchStorageData = async () => {
  storageLoading.value = true
  try {
    const params = { ...queryParams }
    if (currentTreeNode.value) {
      if (currentTreeNode.value.type === 'warehouse') {
        params.warehouseId = currentTreeNode.value.id
      } else if (currentTreeNode.value.type === 'refrigerator') {
        params.refrigeratorId = currentTreeNode.value.id
      } else if (currentTreeNode.value.type === 'shelf') {
        params.shelfId = currentTreeNode.value.id
      }
    }
    const res = await getSampleStoragePage(params)
    storageTableData.value = res.data.list
    storagePagination.value.total = res.data.total
  } finally {
    storageLoading.value = false
  }
}

const fetchWarningData = async () => {
  warningLoading.value = true
  try {
    const params = { ...warningQueryParams, warningFlag: 1 }
    if (currentTreeNode.value) {
      if (currentTreeNode.value.type === 'warehouse') {
        params.warehouseId = currentTreeNode.value.id
      } else if (currentTreeNode.value.type === 'refrigerator') {
        params.refrigeratorId = currentTreeNode.value.id
      } else if (currentTreeNode.value.type === 'shelf') {
        params.shelfId = currentTreeNode.value.id
      }
    }
    const res = await getSampleStoragePage(params)
    let data = res.data.list
    if (warningTypeFilter.value) {
      data = data.filter(item => {
        const msg = item.warningMessage || ''
        if (warningTypeFilter.value === 'expire') return msg.includes('过期') || msg.includes('效期')
        if (warningTypeFilter.value === 'low') return msg.includes('库存') || msg.includes('不足')
        if (warningTypeFilter.value === 'temp') return msg.includes('温度')
        return true
      })
    }
    warningTableData.value = data
    warningPagination.value.total = res.data.total
  } finally {
    warningLoading.value = false
  }
}

const fetchLogData = async () => {
  logLoading.value = true
  try {
    const params: any = { pageNum: logQueryParams.pageNum, pageSize: logQueryParams.pageSize }
    if (logQueryParams.sampleCode) params.sampleCode = logQueryParams.sampleCode
    if (logQueryParams.sampleName) params.sampleName = logQueryParams.sampleName
    if (logQueryParams.operateType) params.operateType = logQueryParams.operateType
    if (logTimeRange.value && logTimeRange.value.length === 2) {
      params.operateTimeStart = logTimeRange.value[0]
      params.operateTimeEnd = logTimeRange.value[1]
    }
    const res = await getSampleStoragePage({
      ...queryParams,
      pageNum: 1,
      pageSize: 1000
    })
    const allLogs: SampleStorageLogVO[] = []
    res.data.list.forEach(item => {
      if (item.id) {
        allLogs.push({
          id: item.id,
          sampleId: item.sampleId,
          sampleCode: item.sampleCode,
          sampleName: item.sampleName,
          operateType: item.storageStatus === 1 ? 1 : item.storageStatus === 3 ? 2 : 3,
          operateTypeName: item.storageStatus === 1 ? '入库' : item.storageStatus === 3 ? '出库' : '移动',
          warehouseId: item.warehouseId,
          warehouseName: item.warehouseName,
          refrigeratorId: item.refrigeratorId,
          refrigeratorName: item.refrigeratorName,
          shelfId: item.shelfId,
          shelfName: item.shelfName,
          locationCode: item.locationCode,
          operateTime: item.storageTime,
          operatorName: item.storageOperatorName,
          remark: item.warningMessage
        })
      }
    })
    logTableData.value = allLogs
    logPagination.value.total = allLogs.length
  } finally {
    logLoading.value = false
  }
}

const fetchSampleLog = async (sampleId: number) => {
  sampleLogLoading.value = true
  try {
    const res = await getStorageLogBySampleId(sampleId)
    sampleLogData.value = res.data || []
  } finally {
    sampleLogLoading.value = false
  }
}

const handleTabChange = (key: string) => {
  if (key === 'storage') {
    fetchStorageData()
  } else if (key === 'warning') {
    fetchWarningData()
  } else if (key === 'log') {
    fetchLogData()
  }
}

const handleTreeSelect = (selectedKeys: string[], info: any) => {
  selectedTreeKeys.value = selectedKeys
  currentTreeNode.value = info.node.dataRef
  queryParams.warehouseId = undefined
  queryParams.refrigeratorId = undefined
  queryParams.shelfId = undefined
  warningQueryParams.warehouseId = undefined
  warningQueryParams.refrigeratorId = undefined
  warningQueryParams.shelfId = undefined

  if (currentTreeNode.value) {
    if (currentTreeNode.value.type === 'warehouse') {
      queryParams.warehouseId = currentTreeNode.value.id
      warningQueryParams.warehouseId = currentTreeNode.value.id
    } else if (currentTreeNode.value.type === 'refrigerator') {
      queryParams.refrigeratorId = currentTreeNode.value.id
      warningQueryParams.refrigeratorId = currentTreeNode.value.id
    } else if (currentTreeNode.value.type === 'shelf') {
      queryParams.shelfId = currentTreeNode.value.id
      warningQueryParams.shelfId = currentTreeNode.value.id
    }
  }

  if (activeTab.value === 'storage') {
    fetchStorageData()
  } else if (activeTab.value === 'warning') {
    fetchWarningData()
  }
}

const handleTreeExpand = (expandedKeys: string[]) => {
  expandedTreeKeys.value = expandedKeys
}

const handleQuery = () => {
  queryParams.pageNum = 1
  storagePagination.value.current = 1
  fetchStorageData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    sampleCode: '',
    sampleName: '',
    storageStatus: undefined,
    warningFlag: undefined
  })
  handleQuery()
}

const handleStorageTableChange = (pag: any) => {
  queryParams.pageNum = pag.current
  queryParams.pageSize = pag.pageSize
  storagePagination.value.current = pag.current
  storagePagination.value.pageSize = pag.pageSize
  fetchStorageData()
}

const handleWarningQuery = () => {
  warningQueryParams.pageNum = 1
  warningPagination.value.current = 1
  fetchWarningData()
}

const handleWarningReset = () => {
  Object.assign(warningQueryParams, {
    sampleCode: '',
    sampleName: ''
  })
  warningTypeFilter.value = undefined
  handleWarningQuery()
}

const handleWarningTableChange = (pag: any) => {
  warningQueryParams.pageNum = pag.current
  warningQueryParams.pageSize = pag.pageSize
  warningPagination.value.current = pag.current
  warningPagination.value.pageSize = pag.pageSize
  fetchWarningData()
}

const handleLogQuery = () => {
  logQueryParams.pageNum = 1
  logPagination.value.current = 1
  fetchLogData()
}

const handleLogReset = () => {
  Object.assign(logQueryParams, {
    sampleCode: '',
    sampleName: '',
    operateType: undefined
  })
  logTimeRange.value = []
  handleLogQuery()
}

const handleLogTableChange = (pag: any) => {
  logQueryParams.pageNum = pag.current
  logQueryParams.pageSize = pag.pageSize
  logPagination.value.current = pag.current
  logPagination.value.pageSize = pag.pageSize
  fetchLogData()
}

const handleAddStorage = ({ key }: { key: string }) => {
  if (key === 'warehouse') {
    handleAddWarehouse()
  } else if (key === 'refrigerator') {
    handleAddRefrigerator()
  } else if (key === 'shelf') {
    handleAddShelf()
  }
}

const handleAddWarehouse = () => {
  isWarehouseEdit.value = false
  warehouseModalTitle.value = '新增库房'
  Object.assign(warehouseFormData, {
    warehouseCode: '',
    warehouseName: '',
    warehouseType: 1,
    temperatureMin: undefined,
    temperatureMax: undefined,
    humidityMin: undefined,
    humidityMax: undefined,
    location: '',
    managerId: undefined,
    status: 1,
    remark: ''
  })
  warehouseModalVisible.value = true
}

const handleEditWarehouse = (record: StorageWarehouseVO) => {
  isWarehouseEdit.value = true
  warehouseModalTitle.value = '编辑库房'
  currentWarehouseId.value = record.id
  Object.assign(warehouseFormData, {
    id: record.id,
    warehouseCode: record.warehouseCode,
    warehouseName: record.warehouseName,
    warehouseType: record.warehouseType,
    temperatureMin: record.temperatureMin,
    temperatureMax: record.temperatureMax,
    humidityMin: record.humidityMin,
    humidityMax: record.humidityMax,
    location: record.location,
    managerId: record.managerId,
    status: record.status,
    remark: record.remark
  })
  warehouseModalVisible.value = true
}

const handleWarehouseSubmit = async () => {
  try {
    await warehouseFormRef.value.validate()
    submitting.value = true
    if (isWarehouseEdit.value) {
      await updateWarehouse(warehouseFormData)
      message.success('更新成功')
    } else {
      await saveWarehouse(warehouseFormData)
      message.success('新增成功')
    }
    warehouseModalVisible.value = false
    fetchStorageTree()
    fetchWarehouseList()
  } catch (error) {
    console.error('Warehouse submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDeleteWarehouse = async (id: number) => {
  try {
    await deleteWarehouse(id)
    message.success('删除成功')
    fetchStorageTree()
    fetchWarehouseList()
  } catch (error) {
    console.error('Delete warehouse error:', error)
  }
}

const handleAddRefrigerator = () => {
  isRefrigeratorEdit.value = false
  refrigeratorModalTitle.value = '新增冰箱'
  Object.assign(refrigeratorFormData, {
    warehouseId: currentTreeNode.value?.type === 'warehouse' ? currentTreeNode.value.id : (warehouseList.value[0]?.id || 0),
    refrigeratorCode: '',
    refrigeratorName: '',
    temperatureMin: undefined,
    temperatureMax: undefined,
    currentTemperature: undefined,
    status: 1,
    remark: ''
  })
  refrigeratorModalVisible.value = true
}

const handleEditRefrigerator = (record: StorageRefrigeratorVO) => {
  isRefrigeratorEdit.value = true
  refrigeratorModalTitle.value = '编辑冰箱'
  currentRefrigeratorId.value = record.id
  Object.assign(refrigeratorFormData, {
    id: record.id,
    warehouseId: record.warehouseId,
    refrigeratorCode: record.refrigeratorCode,
    refrigeratorName: record.refrigeratorName,
    temperatureMin: record.temperatureMin,
    temperatureMax: record.temperatureMax,
    currentTemperature: record.currentTemperature,
    status: record.status,
    remark: record.remark
  })
  refrigeratorModalVisible.value = true
}

const handleRefrigeratorSubmit = async () => {
  try {
    await refrigeratorFormRef.value.validate()
    submitting.value = true
    if (isRefrigeratorEdit.value) {
      await updateRefrigerator(refrigeratorFormData)
      message.success('更新成功')
    } else {
      await saveRefrigerator(refrigeratorFormData)
      message.success('新增成功')
    }
    refrigeratorModalVisible.value = false
    fetchStorageTree()
    fetchRefrigeratorList()
  } catch (error) {
    console.error('Refrigerator submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDeleteRefrigerator = async (id: number) => {
  try {
    await deleteRefrigerator(id)
    message.success('删除成功')
    fetchStorageTree()
    fetchRefrigeratorList()
  } catch (error) {
    console.error('Delete refrigerator error:', error)
  }
}

const handleAddShelf = () => {
  isShelfEdit.value = false
  shelfModalTitle.value = '新增货架'
  const defaultWarehouseId = currentTreeNode.value?.type === 'warehouse'
    ? currentTreeNode.value.id
    : currentTreeNode.value?.type === 'refrigerator'
      ? currentTreeNode.value.warehouseId
      : currentTreeNode.value?.type === 'shelf'
        ? currentTreeNode.value.warehouseId
        : warehouseList.value[0]?.id || 0
  const defaultRefrigeratorId = currentTreeNode.value?.type === 'refrigerator'
    ? currentTreeNode.value.id
    : currentTreeNode.value?.type === 'shelf'
      ? currentTreeNode.value.refrigeratorId
      : undefined
  Object.assign(shelfFormData, {
    warehouseId: defaultWarehouseId,
    refrigeratorId: defaultRefrigeratorId,
    shelfCode: '',
    shelfName: '',
    layerCount: 1,
    capacity: 0,
    status: 1,
    remark: ''
  })
  shelfModalVisible.value = true
}

const handleEditShelf = (record: StorageShelfVO) => {
  isShelfEdit.value = true
  shelfModalTitle.value = '编辑货架'
  currentShelfId.value = record.id
  Object.assign(shelfFormData, {
    id: record.id,
    warehouseId: record.warehouseId,
    refrigeratorId: record.refrigeratorId,
    shelfCode: record.shelfCode,
    shelfName: record.shelfName,
    layerCount: record.layerCount,
    capacity: record.capacity,
    status: record.status,
    remark: record.remark
  })
  shelfModalVisible.value = true
}

const handleWarehouseChange = async () => {
  if (shelfFormData.warehouseId) {
    shelfFormData.refrigeratorId = undefined
  }
}

const handleShelfSubmit = async () => {
  try {
    await shelfFormRef.value.validate()
    submitting.value = true
    if (isShelfEdit.value) {
      await updateShelf(shelfFormData)
      message.success('更新成功')
    } else {
      await saveShelf(shelfFormData)
      message.success('新增成功')
    }
    shelfModalVisible.value = false
    fetchStorageTree()
    fetchShelfList()
  } catch (error) {
    console.error('Shelf submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleDeleteShelf = async (id: number) => {
  try {
    await deleteShelf(id)
    message.success('删除成功')
    fetchStorageTree()
    fetchShelfList()
  } catch (error) {
    console.error('Delete shelf error:', error)
  }
}

const handleInbound = () => {
  fetchAvailableSamples()
  Object.assign(inboundFormData, {
    sampleIds: [],
    operateType: 1,
    warehouseId: currentTreeNode.value?.type === 'warehouse' ? currentTreeNode.value.id : undefined,
    refrigeratorId: currentTreeNode.value?.type === 'refrigerator' ? currentTreeNode.value.id : undefined,
    shelfId: currentTreeNode.value?.type === 'shelf' ? currentTreeNode.value.id : undefined,
    locationCode: '',
    operateTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    remark: ''
  })
  inboundModalVisible.value = true
}

const handleInboundWarehouseChange = () => {
  inboundFormData.refrigeratorId = undefined
  inboundFormData.shelfId = undefined
}

const handleInboundSubmit = async () => {
  try {
    await inboundFormRef.value.validate()
    submitting.value = true
    await sampleInbound(inboundFormData)
    message.success('入库成功')
    inboundModalVisible.value = false
    fetchStorageData()
    fetchStorageTree()
  } catch (error) {
    console.error('Inbound submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleOutbound = () => {
  outboundSingle.value = false
  Object.assign(outboundFormData, {
    sampleIds: [],
    operateType: 2,
    operateTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    operatorName: '',
    remark: ''
  })
  outboundModalVisible.value = true
}

const handleOutboundSingle = (record: SampleStorageVO) => {
  outboundSingle.value = true
  Object.assign(outboundFormData, {
    sampleIds: [record.sampleId],
    operateType: 2,
    operateTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    operatorName: '',
    remark: ''
  })
  outboundModalVisible.value = true
}

const handleOutboundSubmit = async () => {
  try {
    await outboundFormRef.value.validate()
    submitting.value = true
    await sampleOutbound(outboundFormData)
    message.success('出库成功')
    outboundModalVisible.value = false
    fetchStorageData()
    fetchStorageTree()
  } catch (error) {
    console.error('Outbound submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleMove = (record: SampleStorageVO) => {
  moveSampleInfo.value = record
  Object.assign(moveFormData, {
    id: record.id,
    targetWarehouseId: record.warehouseId,
    targetRefrigeratorId: record.refrigeratorId,
    targetShelfId: record.shelfId,
    targetLocationCode: record.locationCode || '',
    moveReason: ''
  })
  moveModalVisible.value = true
}

const handleMoveWarehouseChange = () => {
  moveFormData.targetRefrigeratorId = undefined
  moveFormData.targetShelfId = undefined
}

const handleMoveSubmit = async () => {
  try {
    await moveFormRef.value.validate()
    submitting.value = true
    await moveSampleStorage(moveFormData)
    message.success('移动成功')
    moveModalVisible.value = false
    fetchStorageData()
    fetchStorageTree()
  } catch (error) {
    console.error('Move submit error:', error)
  } finally {
    submitting.value = false
  }
}

const handleViewLog = (record: SampleStorageVO) => {
  fetchSampleLog(record.sampleId)
  logModalVisible.value = true
}

const handleCellClick = (cell: any) => {
  if (cell.samples && cell.samples.length > 0) {
    queryParams.locationCode = cell.locationCode
    handleQuery()
  }
}

const handleDragStart = (e: DragEvent, cell: any) => {
  if (cell.samples && cell.samples.length > 0) {
    dragSample.value = cell.samples[0]
    e.dataTransfer?.setData('text/plain', cell.locationCode)
  }
}

const handleDragOver = (e: DragEvent, locationCode: string) => {
  e.preventDefault()
  dragOverLocation.value = locationCode
}

const handleDragLeave = () => {
  dragOverLocation.value = null
}

const handleDrop = async (e: DragEvent, targetLocation: string) => {
  e.preventDefault()
  dragOverLocation.value = null

  if (dragSample.value && dragSample.value.locationCode !== targetLocation) {
    try {
      await moveSampleStorage({
        id: dragSample.value.id,
        targetWarehouseId: dragSample.value.warehouseId,
        targetRefrigeratorId: dragSample.value.refrigeratorId,
        targetShelfId: dragSample.value.shelfId,
        targetLocationCode: targetLocation,
        moveReason: '拖拽移动'
      })
      message.success('移动成功')
      fetchStorageData()
    } catch (error) {
      console.error('Drag move error:', error)
    }
  }
  dragSample.value = null
}

const getStorageStatusColor = (status?: number) => {
  const colors: Record<number, string> = { 1: 'green', 2: 'orange', 3: 'default' }
  return colors[status || 0] || 'default'
}

const getWarningTypeColor = (message?: string) => {
  if (!message) return 'default'
  if (message.includes('过期') || message.includes('效期')) return 'red'
  if (message.includes('库存') || message.includes('不足')) return 'orange'
  if (message.includes('温度')) return 'blue'
  return 'default'
}

const getOperateTypeColor = (type?: number) => {
  const colors: Record<number, string> = { 1: 'green', 2: 'red', 3: 'blue' }
  return colors[type || 0] || 'default'
}

onMounted(() => {
  fetchStorageTree()
  fetchWarehouseList()
  fetchRefrigeratorList()
  fetchShelfList()
  fetchStorageData()
})
</script>

<style scoped>
.sample-storage {
  height: 100%;
}

.sider-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 8px;
}

.sider-title {
  font-weight: 600;
  font-size: 14px;
}

.tree-node-title {
  font-size: 13px;
}

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

.storage-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.storage-cell {
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.storage-cell:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
}

.storage-cell.cell-warning {
  border-color: #ff4d4f;
  background: #fff1f0;
}

.storage-cell.cell-drag-over {
  border-color: #52c41a;
  background: #f6ffed;
}

.cell-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.cell-location {
  font-weight: 600;
  font-size: 14px;
  color: #262626;
}

.cell-body {
  text-align: center;
  padding: 8px 0;
}

.cell-count {
  font-size: 28px;
  font-weight: bold;
  color: #1890ff;
  line-height: 1.2;
}

.cell-label {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 4px;
}

.cell-footer {
  border-top: 1px dashed #f0f0f0;
  padding-top: 8px;
  margin-top: 8px;
}

.cell-sample {
  font-size: 12px;
  color: #595959;
  padding: 2px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cell-more {
  font-size: 12px;
  color: #8c8c8c;
  text-align: center;
  padding-top: 4px;
}

.text-warning {
  color: #faad14;
}

.text-red {
  color: #f5222d;
}

.text-green {
  color: #52c41a;
}

:deep(.ant-tree-node-content-wrapper) {
  padding: 4px 0;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style>