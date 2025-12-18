<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>数据概览</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <div class="stat-label">总数据量</div>
          <div class="stat-number">{{ stats.total }}</div>
          <div class="stat-desc">条灾情数据</div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <div class="stat-label">今日新增</div>
          <div class="stat-number">{{ stats.today }}</div>
          <div class="stat-desc">条数据</div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <div class="stat-label">数据源</div>
          <div class="stat-number">{{ stats.sources }}</div>
          <div class="stat-desc">个接入源</div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <div class="stat-label">处理中</div>
          <div class="stat-number">{{ stats.processing }}</div>
          <div class="stat-desc">条数据</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 数据来源分布 -->
      <el-col :span="12">
        <div class="card-panel">
          <h3 class="chart-title">数据来源分布</h3>
          <div ref="sourceChartRef" class="chart-container"></div>
        </div>
      </el-col>

      <!-- 灾害类型分布 -->
      <el-col :span="12">
        <div class="card-panel">
          <h3 class="chart-title">灾害类型分布</h3>
          <div ref="categoryChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 数据趋势 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <div class="card-panel">
          <h3 class="chart-title">数据接入趋势</h3>
          <div ref="trendChartRef" class="chart-container" style="height: 300px"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 最新数据 -->
    <el-row>
      <el-col :span="24">
        <div class="card-panel">
          <h3 class="chart-title">最新灾情数据</h3>
          <el-table :data="recentData" stripe>
            <el-table-column prop="encodedId" label="编码ID" width="280" />
            <el-table-column prop="location" label="地理位置" width="200" />
            <el-table-column prop="carrierType" label="载体类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getCarrierTypeTag(row.carrierType)" size="small">
                  {{ row.carrierType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="createTime" label="创建时间" width="180" />
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDisasterDataList, getStatsByCategory, getStatsBySource } from '@/api/disaster'

const stats = ref({
  total: 0,
  today: 0,
  sources: 0,
  processing: 0
})

const recentData = ref([])
const sourceChartRef = ref(null)
const categoryChartRef = ref(null)
const trendChartRef = ref(null)

// 初始化图表
const initCharts = async (dataList) => {
  await initSourceChart()
  await initCategoryChart()
  initTrendChart(dataList)
}

// 数据来源分布图
const initSourceChart = async () => {
  const chart = echarts.init(sourceChartRef.value)

  // 从后端获取真实数据
  let chartData = []
  try {
    const res = await getStatsBySource()
    if (res.data) {
      chartData = Object.keys(res.data).map(key => ({
        value: res.data[key],
        name: key
      }))
      // 更新数据源数量
      stats.value.sources = chartData.length
    }
  } catch (error) {
    console.error('获取来源统计失败:', error)
    // 使用默认数据
    chartData = [
      { value: 0, name: '暂无数据' }
    ]
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '数据来源',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: chartData
      }
    ]
  }
  chart.setOption(option)
}

// 灾害类型分布图
const initCategoryChart = async () => {
  const chart = echarts.init(categoryChartRef.value)

  // 从后端获取真实数据
  let categories = []
  let values = []
  try {
    const res = await getStatsByCategory()
    if (res.data && Object.keys(res.data).length > 0) {
      categories = Object.keys(res.data)
      values = Object.values(res.data)
    } else {
      categories = ['暂无数据']
      values = [0]
    }
  } catch (error) {
    console.error('获取类别统计失败:', error)
    categories = ['暂无数据']
    values = [0]
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: '{b}: {c} 条'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        interval: 0,
        rotate: categories.length > 5 ? 30 : 0
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '数量',
        type: 'bar',
        barWidth: '40%',
        data: values,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  chart.setOption(option)
}

// 数据趋势图
const initTrendChart = (dataList = []) => {
  const chart = echarts.init(trendChartRef.value)

  // 生成最近7天的日期和数据
  const dates = []
  const counts = []
  const today = new Date()

  for (let i = 6; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    const dateStr = `${date.getMonth() + 1}/${date.getDate()}`
    dates.push(dateStr)

    // 统计该日期的数据数量
    const count = dataList.filter(item => {
      if (!item.createTime) return false
      const itemDate = new Date(item.createTime)
      return itemDate.toDateString() === date.toDateString()
    }).length
    counts.push(count)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c} 条'
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
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '数据量',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
            { offset: 1, color: 'rgba(24, 144, 255, 0)' }
          ])
        },
        lineStyle: {
          color: '#1890ff'
        },
        itemStyle: {
          color: '#1890ff'
        },
        data: counts
      }
    ]
  }
  chart.setOption(option)
}

// 加载数据
const loadData = async () => {
  try {
    const res = await getDisasterDataList()
    if (res.data && res.data.length > 0) {
      stats.value.total = res.data.length
      recentData.value = res.data.slice(0, 10)

      // 计算今日新增
      const today = new Date().toDateString()
      stats.value.today = res.data.filter(item => {
        return new Date(item.createTime).toDateString() === today
      }).length

      // 处理中的数据（示例：最近24小时的数据）
      const oneDayAgo = new Date()
      oneDayAgo.setHours(oneDayAgo.getHours() - 24)
      stats.value.processing = res.data.filter(item => {
        const createTime = new Date(item.createTime)
        return createTime >= oneDayAgo
      }).length

      return res.data
    }
    return []
  } catch (error) {
    console.error('加载数据失败:', error)
    return []
  }
}

// 获取载体类型标签颜色
const getCarrierTypeTag = (type) => {
  const map = {
    '文字': 'success',
    '图像': 'primary',
    '音频': 'warning',
    '视频': 'danger'
  }
  return map[type] || 'info'
}

onMounted(async () => {
  const dataList = await loadData()
  await nextTick()
  await initCharts(dataList)

  // 响应式调整图表大小
  window.addEventListener('resize', () => {
    echarts.getInstanceByDom(sourceChartRef.value)?.resize()
    echarts.getInstanceByDom(categoryChartRef.value)?.resize()
    echarts.getInstanceByDom(trendChartRef.value)?.resize()
  })
})
</script>

<style lang="scss" scoped>
.dashboard {
  .stats-row {
    margin-bottom: 20px;
  }

  .charts-row {
    margin-bottom: 20px;
  }

  .chart-title {
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 20px;
    color: #333;
  }

  .chart-container {
    height: 350px;
  }
}
</style>
