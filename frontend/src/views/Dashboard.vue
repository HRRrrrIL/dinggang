<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <div class="num">{{ stats.customerCount || 0 }}</div>
            <div class="label">客户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <div class="num">{{ stats.contractCount || 0 }}</div>
            <div class="label">生效合同</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <div class="num">{{ stats.orderCount || 0 }}</div>
            <div class="label">待处理工单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat">
            <div class="num">{{ stats.overdueNodes || 0 }}</div>
            <div class="label">逾期节点</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>客户层级分布</template>
          <div ref="pieChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>工单状态统计</template>
          <div ref="barChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import request from '@/api/request'

const stats = ref({})
const pieChart = ref(null)
const barChart = ref(null)
let pieInstance = null
let barInstance = null

const loadStats = async () => {
  try {
    const res = await request.get('/stats/overview')
    const data = res.data
    stats.value = {
      customerCount: data.customerCount || 0,
      contractCount: data.contractCount || 0,
      orderCount: data.orderCount || 0,
      overdueNodes: 0
    }

    // 更新饼图数据
    if (data.levelStats) {
      pieInstance?.setOption({
        series: [{
          data: [
            { value: data.levelStats['潜在'] || 0, name: '潜在客户' },
            { value: data.levelStats['意向'] || 0, name: '意向客户' },
            { value: data.levelStats['成交'] || 0, name: '成交客户' },
            { value: data.levelStats['复购'] || 0, name: '复购客户' }
          ]
        }]
      })
    }
  } catch (e) {}
}

const initCharts = () => {
  // 饼图：客户层级分布（静态示例，后续接真实数据）
  pieInstance = echarts.init(pieChart.value)
  pieInstance.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 10, name: '潜在客户' },
        { value: 8, name: '意向客户' },
        { value: 5, name: '成交客户' },
        { value: 3, name: '复购客户' }
      ]
    }]
  })

  // 柱状图：工单状态
  barInstance = echarts.init(barChart.value)
  barInstance.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['待分配', '处理中', '待确认', '已闭环', '已驳回']
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [2, 5, 1, 8, 1],
      itemStyle: { color: '#409EFF' }
    }]
  })
}

const handleResize = () => {
  pieInstance?.resize()
  barInstance?.resize()
}

onMounted(() => {
  loadStats()
  setTimeout(initCharts, 100)
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieInstance?.dispose()
  barInstance?.dispose()
})
</script>

<style scoped>
.stat { text-align: center; padding: 20px 0; }
.num { font-size: 32px; font-weight: bold; color: #409EFF; }
.label { margin-top: 8px; color: #999; font-size: 14px; }
</style>
