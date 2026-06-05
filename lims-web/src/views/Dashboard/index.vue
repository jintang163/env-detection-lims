<template>
  <div class="dashboard">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #1890ff">
              <UserOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.customerCount }}</div>
              <div class="stat-label">客户总数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #52c41a">
              <FileTextOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.contractCount }}</div>
              <div class="stat-label">合同总数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #faad14">
              <FormOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.entrustCount }}</div>
              <div class="stat-label">委托单总数</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #eb2f96">
              <DollarOutlined />
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.totalAmount }}</div>
              <div class="stat-label">总金额</div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="12">
        <a-card title="业务趋势">
          <v-chart class="chart" :option="lineOption" autoresize />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="委托单状态分布">
          <v-chart class="chart" :option="pieOption" autoresize />
        </a-card>
      </a-col>
    </a-row>

    <a-card title="最近委托单" style="margin-top: 16px">
      <a-table :columns="columns" :data-source="recentEntrusts" :pagination="false" row-key="id" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { UserOutlined, FileTextOutlined, FormOutlined, DollarOutlined } from '@ant-design/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const stats = ref({
  customerCount: 0,
  contractCount: 0,
  entrustCount: 0,
  totalAmount: 0
})

const recentEntrusts = ref<any[]>([])

const columns = [
  { title: '委托单号', dataIndex: 'entrustNo', key: 'entrustNo' },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName' },
  { title: '样品类型', dataIndex: 'sampleType', key: 'sampleType' },
  { title: '状态', dataIndex: 'statusName', key: 'statusName' },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime' }
]

const lineOption = ref({
  tooltip: { trigger: 'axis' },
  legend: { data: ['合同', '委托单', '报价单'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['1月', '2月', '3月', '4月', '5月', '6月']
  },
  yAxis: { type: 'value' },
  series: [
    { name: '合同', type: 'line', data: [12, 19, 15, 25, 22, 30] },
    { name: '委托单', type: 'line', data: [20, 25, 30, 35, 40, 45] },
    { name: '报价单', type: 'line', data: [15, 20, 25, 28, 32, 38] }
  ]
})

const pieOption = ref({
  tooltip: { trigger: 'item' },
  legend: { orient: 'vertical', left: 'left' },
  series: [
    {
      name: '状态分布',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: [
        { value: 30, name: '待审核' },
        { value: 50, name: '检测中' },
        { value: 80, name: '已完成' },
        { value: 10, name: '已驳回' }
      ]
    }
  ]
})

onMounted(() => {
  stats.value = {
    customerCount: 156,
    contractCount: 89,
    entrustCount: 170,
    totalAmount: '285,600'
  }
  recentEntrusts.value = [
    { id: 1, entrustNo: 'WT202401001', customerName: '某环保公司', sampleType: '水质', statusName: '检测中', createTime: '2024-01-15 10:30:00' },
    { id: 2, entrustNo: 'WT202401002', customerName: '某化工企业', sampleType: '土壤', statusName: '待审核', createTime: '2024-01-15 09:20:00' },
    { id: 3, entrustNo: 'WT202401003', customerName: '某制药厂', sampleType: '废气', statusName: '已完成', createTime: '2024-01-14 16:45:00' },
    { id: 4, entrustNo: 'WT202401004', customerName: '某食品厂', sampleType: '水质', statusName: '检测中', createTime: '2024-01-14 14:10:00' },
    { id: 5, entrustNo: 'WT202401005', customerName: '某电子厂', sampleType: '噪声', statusName: '待审核', createTime: '2024-01-14 11:30:00' }
  ]
})
</script>

<style scoped>
.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.chart {
  height: 300px;
}
</style>
