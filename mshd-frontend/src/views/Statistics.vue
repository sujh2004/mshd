<template>
  <div class="statistics">
    <div class="page-header">
      <h2>统计分析</h2>
    </div>

    <!-- 筛选条件 -->
    <div class="card-panel filter-panel">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>

        <el-form-item label="数据来源">
          <el-select v-model="filterForm.sourceType" placeholder="全部" clearable style="width: 150px">
            <el-option label="后方指挥部" value="后方指挥部" />
            <el-option label="现场指挥部" value="现场指挥部" />
            <el-option label="业务报送" value="业务报送" />
            <el-option label="泛在感知" value="泛在感知" />
            <el-option label="舆情感知" value="舆情感知" />
          </el-select>
        </el-form-item>

        <el-form-item label="灾害类别">
          <el-select v-model="filterForm.disasterCategory" placeholder="全部" clearable style="width: 150px">
            <el-option label="震情" value="震情" />
            <el-option label="人员伤亡" value="人员伤亡" />
            <el-option label="房屋破坏" value="房屋破坏" />
            <el-option label="生命线工程" value="生命线工程" />
            <el-option label="次生灾害" value="次生灾害" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadStatistics">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="exportData">
            <el-icon><Download /></el-icon>
            导出报表
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <div class="stat-icon">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">总数据量</div>
            <div class="stat-number">{{ statistics.total }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <div class="stat-icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">日均新增</div>
            <div class="stat-number">{{ statistics.dailyAverage }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <div class="stat-icon">
            <el-icon><Location /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">覆盖地区</div>
            <div class="stat-number">{{ statistics.regions }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <div class="stat-icon">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">接入数据源</div>
            <div class="stat-number">{{ statistics.sources }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 按类别统计 -->
      <el-col :span="12">
        <div class="card-panel">
          <h3 class="chart-title">灾害类别分布</h3>
          <div ref="categoryChartRef" class="chart-container"></div>
        </div>
      </el-col>

      <!-- 按来源统计 -->
      <el-col :span="12">
        <div class="card-panel">
          <h3 class="chart-title">数据来源分布</h3>
          <div ref="sourceChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 时间趋势 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="card-panel">
          <h3 class="chart-title">数据接入趋势分析</h3>
          <div ref="trendChartRef" class="chart-container" style="height: 350px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 统计表格 -->
    <div class="card-panel">
      <h3 class="chart-title">详细统计数据</h3>
      <el-table :data="tableData" stripe border>
        <el-table-column prop="dimension" label="统计维度" width="150" />
        <el-table-column prop="category" label="类别" width="150" />
        <el-table-column prop="count" label="数量" width="120" align="right" />
        <el-table-column prop="percentage" label="占比" width="120" align="right">
          <template #default="{ row }">
            {{ row.percentage }}%
          </template>
        </el-table-column>
        <el-table-column prop="trend" label="趋势" width="120">
          <template #default="{ row }">
            <el-tag :type="getTrendType(row.trend)" size="small">
              {{ row.trend }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getDisasterDataList, getStatsByCategory, getStatsBySource } from '@/api/disaster'

const dateRange = ref([])
const filterForm = ref({
  sourceType: '',
  disasterCategory: ''
})

const statistics = ref({
  total: 0,
  dailyAverage: 0,
  regions: 0,
  sources: 0
})

const tableData = ref([])
const categoryChartRef = ref(null)
const sourceChartRef = ref(null)
const trendChartRef = ref(null)

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getDisasterDataList()

    if (res.data && res.data.length > 0) {
      let filteredData = res.data

      // 按时间范围筛选
      if (dateRange.value && dateRange.value.length === 2) {
        const [startDate, endDate] = dateRange.value
        filteredData = filteredData.filter(item => {
          const itemDate = item.createTime ? item.createTime.substring(0, 10) : ''
          return itemDate >= startDate && itemDate <= endDate
        })
      }

      // 按来源筛选
      if (filterForm.value.sourceType) {
        filteredData = filteredData.filter(item =>
          item.sourceType === filterForm.value.sourceType
        )
      }

      // 按类别筛选
      if (filterForm.value.disasterCategory) {
        filteredData = filteredData.filter(item =>
          item.disasterCategory === filterForm.value.disasterCategory
        )
      }

      // 计算统计数据
      statistics.value.total = filteredData.length

      // 计算日均新增
      if (filteredData.length > 0) {
        const dates = [...new Set(filteredData.map(item =>
          item.createTime ? item.createTime.substring(0, 10) : ''
        ))].filter(d => d)
        statistics.value.dailyAverage = dates.length > 0
          ? Math.round(filteredData.length / dates.length)
          : 0
      }

      // 计算覆盖地区（简化处理）
      const locations = [...new Set(filteredData.map(item => item.location))].filter(l => l)
      statistics.value.regions = locations.length

      // 计算数据源数量
      const sources = [...new Set(filteredData.map(item => item.sourceType))].filter(s => s)
      statistics.value.sources = sources.length

      // 生成表格数据
      generateTableData(filteredData)

      // 更新图表
      await nextTick()
      updateCharts(filteredData)
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

// 生成表格数据
const generateTableData = (data) => {
  const table = []

  // 按类别统计
  const categoryStats = {}
  data.forEach(item => {
    const category = item.disasterCategory || '未分类'
    categoryStats[category] = (categoryStats[category] || 0) + 1
  })

  Object.entries(categoryStats).forEach(([category, count]) => {
    table.push({
      dimension: '灾害类别',
      category,
      count,
      percentage: ((count / data.length) * 100).toFixed(2),
      trend: count > 10 ? '上升' : count > 5 ? '平稳' : '下降',
      remark: `占总数的${((count / data.length) * 100).toFixed(2)}%`
    })
  })

  // 按来源统计
  const sourceStats = {}
  data.forEach(item => {
    const source = item.sourceType || '未知来源'
    sourceStats[source] = (sourceStats[source] || 0) + 1
  })

  Object.entries(sourceStats).forEach(([category, count]) => {
    table.push({
      dimension: '数据来源',
      category,
      count,
      percentage: ((count / data.length) * 100).toFixed(2),
      trend: count > 8 ? '上升' : count > 4 ? '平稳' : '下降',
      remark: `占总数的${((count / data.length) * 100).toFixed(2)}%`
    })
  })

  tableData.value = table
}

// 更新图表
const updateCharts = (data) => {
  // 类别分布饼图
  const categoryStats = {}
  data.forEach(item => {
    const category = item.disasterCategory || '未分类'
    categoryStats[category] = (categoryStats[category] || 0) + 1
  })

  const categoryChart = echarts.init(categoryChartRef.value)
  categoryChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [{
      name: '灾害类别',
      type: 'pie',
      radius: ['40%', '70%'],
      data: Object.entries(categoryStats).map(([name, value]) => ({ name, value })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })

  // 来源分布柱状图
  const sourceStats = {}
  data.forEach(item => {
    const source = item.sourceType || '未知来源'
    sourceStats[source] = (sourceStats[source] || 0) + 1
  })

  const sourceChart = echarts.init(sourceChartRef.value)
  sourceChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Object.keys(sourceStats),
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: { type: 'value' },
    series: [{
      name: '数量',
      type: 'bar',
      data: Object.values(sourceStats),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  })

  // 时间趋势折线图
  const dateStats = {}
  data.forEach(item => {
    const date = item.createTime ? item.createTime.substring(0, 10) : ''
    if (date) {
      dateStats[date] = (dateStats[date] || 0) + 1
    }
  })

  const sortedDates = Object.keys(dateStats).sort()
  const trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: sortedDates
    },
    yAxis: { type: 'value' },
    series: [{
      name: '数据量',
      type: 'line',
      smooth: true,
      data: sortedDates.map(date => dateStats[date]),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
          { offset: 1, color: 'rgba(24, 144, 255, 0)' }
        ])
      },
      lineStyle: { color: '#1890ff' },
      itemStyle: { color: '#1890ff' }
    }]
  })

  // 响应式
  window.addEventListener('resize', () => {
    categoryChart.resize()
    sourceChart.resize()
    trendChart.resize()
  })
}

// 时间范围变化
const handleDateChange = () => {
  loadStatistics()
}

// 重置
const handleReset = () => {
  dateRange.value = []
  filterForm.value = {
    sourceType: '',
    disasterCategory: ''
  }
  loadStatistics()
}

// 导出数据
const exportData = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  // 生成CSV内容
  const headers = ['统计维度', '类别', '数量', '占比(%)', '趋势', '备注']
  const csvContent = [
    headers.join(','),
    ...tableData.value.map(row => [
      row.dimension,
      row.category,
      row.count,
      row.percentage,
      row.trend,
      row.remark
    ].join(','))
  ].join('\n')

  // 创建Blob并下载
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `统计分析报表_${new Date().toLocaleDateString()}.csv`
  link.click()

  ElMessage.success('报表导出成功')
}

// 获取趋势类型
const getTrendType = (trend) => {
  const map = {
    '上升': 'success',
    '平稳': 'warning',
    '下降': 'danger'
  }
  return map[trend] || 'info'
}

onMounted(() => {
  loadStatistics()
})
</script>

<style lang="scss" scoped>
.statistics {
  .filter-panel {
    margin-bottom: 20px;
  }

  .stats-row {
    margin-bottom: 20px;
  }

  .stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    border-radius: 12px;
    color: #fff;

    .stat-icon {
      font-size: 48px;
      margin-right: 20px;
      opacity: 0.8;
    }

    .stat-content {
      flex: 1;

      .stat-label {
        font-size: 14px;
        opacity: 0.9;
        margin-bottom: 8px;
      }

      .stat-number {
        font-size: 32px;
        font-weight: 700;
      }
    }
  }

  .charts-row {
    margin-bottom: 20px;
  }

  .chart-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #333;
  }

  .chart-container {
    height: 350px;
  }
}
</style>
