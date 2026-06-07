<template>
  <div class="data-trace-container">
    <el-card class="search-card" shadow="never">
      <div class="search-form">
        <el-form :inline="true" :model="searchForm" class="search-inline">
          <el-form-item label="溯源类型">
            <el-select v-model="searchForm.traceType" placeholder="请选择溯源类型" style="width: 150px">
              <el-option label="样品" value="sample" />
              <el-option label="任务" value="task" />
              <el-option label="仪器" value="equipment" />
            </el-select>
          </el-form-item>
          <el-form-item label="源ID">
            <el-input v-model="searchForm.sourceId" placeholder="请输入源ID" style="width: 200px" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch" :loading="loading">查询</el-button>
            <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <div class="content-wrapper">
      <el-card class="tree-card" shadow="never">
        <template #header>
          <div class="card-header">
            <el-icon><Connection /></el-icon>
            <span>溯源关系树</span>
          </div>
        </template>
        <div class="tree-container">
          <el-empty v-if="!treeData.length" description="暂无数据，请先查询" />
          <el-tree
            v-else
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            :expand-on-click-node="false"
            @node-click="handleNodeClick"
            default-expand-all
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <el-icon :color="getNodeIconColor(data.nodeType)" :size="16">
                  <component :is="getNodeIcon(data.nodeType)" />
                </el-icon>
                <span class="node-label">{{ data.label }}</span>
                <el-tag :type="getNodeTagType(data.nodeType)" size="small" class="node-tag">
                  {{ getNodeTypeName(data.nodeType) }}
                </el-tag>
              </div>
            </template>
          </el-tree>
        </div>
      </el-card>

      <el-card class="detail-card" shadow="never">
        <template #header>
          <div class="card-header">
            <el-icon><Document /></el-icon>
            <span>节点详情</span>
          </div>
        </template>
        <div class="detail-container">
          <el-empty v-if="!selectedNode" description="请点击左侧节点查看详情" />
          <div v-else class="detail-content">
            <div class="detail-header">
              <el-icon :color="getNodeIconColor(selectedNode.nodeType)" :size="32">
                <component :is="getNodeIcon(selectedNode.nodeType)" />
              </el-icon>
              <div class="detail-title">
                <h3>{{ selectedNode.label }}</h3>
                <el-tag :type="getNodeTagType(selectedNode.nodeType)">
                  {{ getNodeTypeName(selectedNode.nodeType) }}
                </el-tag>
              </div>
            </div>
            <el-divider />
            <el-descriptions :column="2" border>
              <el-descriptions-item label="ID">
                {{ selectedNode.id }}
              </el-descriptions-item>
              <el-descriptions-item label="类型">
                {{ getNodeTypeName(selectedNode.nodeType) }}
              </el-descriptions-item>
              <el-descriptions-item v-for="(value, key) in selectedNode.extra" :key="key" :label="formatLabel(key)">
                {{ value || '-' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { dataTraceApi } from '@/api/detection'
import {
  Search,
  Refresh,
  Connection,
  Document,
  Box,
  List,
  DataLine,
  Files,
  Report,
  Cpu
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const treeRef = ref(null)
const treeData = ref([])
const selectedNode = ref(null)

const searchForm = reactive({
  traceType: 'sample',
  sourceId: ''
})

const treeProps = {
  children: 'children',
  label: 'label'
}

const nodeTypeConfig = {
  sample: { icon: Box, color: '#409EFF', tagType: 'primary', name: '样品' },
  task: { icon: List, color: '#67C23A', tagType: 'success', name: '任务' },
  data_record: { icon: DataLine, color: '#E6A23C', tagType: 'warning', name: '检测数据' },
  original_record: { icon: Files, color: '#909399', tagType: 'info', name: '原始记录' },
  report: { icon: Report, color: '#F56C6C', tagType: 'danger', name: '报告' },
  equipment: { icon: Cpu, color: '#13CE66', tagType: 'success', name: '仪器' }
}

const getNodeIcon = (nodeType) => {
  return nodeTypeConfig[nodeType]?.icon || Document
}

const getNodeIconColor = (nodeType) => {
  return nodeTypeConfig[nodeType]?.color || '#909399'
}

const getNodeTagType = (nodeType) => {
  return nodeTypeConfig[nodeType]?.tagType || 'info'
}

const getNodeTypeName = (nodeType) => {
  return nodeTypeConfig[nodeType]?.name || nodeType
}

const formatLabel = (key) => {
  const labelMap = {
    sampleNo: '样品编号',
    sampleName: '样品名称',
    taskNo: '任务编号',
    taskName: '任务名称',
    itemName: '检测项目',
    result: '检测结果',
    unit: '单位',
    standardLimit: '标准限值',
    recordNo: '记录编号',
    equipmentNo: '仪器编号',
    equipmentName: '仪器名称',
    reportNo: '报告编号',
    createTime: '创建时间',
    createBy: '创建人',
    status: '状态'
  }
  return labelMap[key] || key
}

const handleSearch = async () => {
  if (!searchForm.sourceId) {
    ElMessage.warning('请输入源ID')
    return
  }

  loading.value = true
  try {
    let res
    if (searchForm.traceType === 'sample') {
      res = await dataTraceApi.getTreeFromSample(searchForm.sourceId)
    } else if (searchForm.traceType === 'task') {
      res = await dataTraceApi.getTreeFromTask(searchForm.sourceId)
    } else if (searchForm.traceType === 'equipment') {
      res = await dataTraceApi.getTreeFromEquipment(searchForm.sourceId)
    }
    treeData.value = res.data ? [res.data] : []
    selectedNode.value = null
    if (!treeData.value.length) {
      ElMessage.info('未找到相关溯源数据')
    }
  } catch (error) {
    console.error('查询溯源数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.traceType = 'sample'
  searchForm.sourceId = ''
  treeData.value = []
  selectedNode.value = null
}

const handleNodeClick = (data) => {
  selectedNode.value = data
}
</script>

<style lang="scss" scoped>
.data-trace-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

.search-card {
  .search-form {
    .search-inline {
      margin-bottom: 0;
    }
  }
}

.content-wrapper {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.tree-card {
  width: 380px;
  display: flex;
  flex-direction: column;

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }

  .tree-container {
    flex: 1;
    overflow: auto;

    .tree-node {
      display: flex;
      align-items: center;
      gap: 8px;
      width: 100%;

      .node-label {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .node-tag {
        flex-shrink: 0;
      }
    }
  }
}

.detail-card {
  flex: 1;
  display: flex;
  flex-direction: column;

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
  }

  .detail-container {
    flex: 1;
    overflow: auto;

    .detail-content {
      .detail-header {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 16px;

        .detail-title {
          h3 {
            margin: 0 0 8px 0;
            font-size: 20px;
            color: #303133;
          }
        }
      }
    }
  }
}

:deep(.el-tree-node__content) {
  height: 40px;
}
</style>
