<template>
  <div class="visualization">
    <div class="page-header">
      <h2>数据可视化分析</h2>
      <p class="subtitle">多维度灾情数据可视化展示</p>
    </div>

    <!-- 工具栏 -->
    <div class="card-panel toolbar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadData"
          />
        </el-form-item>

        <el-form-item label="灾害类别">
          <el-select
            v-model="filterForm.disasterCategory"
            placeholder="全部"
            clearable
            style="width: 150px"
            @change="loadData"
          >
            <el-option label="震情" value="震情" />
            <el-option label="人员伤亡" value="人员伤亡" />
            <el-option label="房屋破坏" value="房屋破坏" />
            <el-option label="生命线工程" value="生命线工程" />
            <el-option label="次生灾害" value="次生灾害" />
          </el-select>
        </el-form-item>

        <el-form-item label="数据来源">
          <el-select
            v-model="filterForm.sourceType"
            placeholder="全部"
            clearable
            style="width: 150px"
            @change="loadData"
          >
            <el-option label="后方指挥部" value="后方指挥部" />
            <el-option label="现场指挥部" value="现场指挥部" />
            <el-option label="业务报送" value="业务报送" />
            <el-option label="泛在感知" value="泛在感知" />
            <el-option label="舆情感知" value="舆情感知" />
          </el-select>
        </el-form-item>

        <el-form-item label="可视化类型">
          <el-radio-group v-model="visualType" @change="updateCharts">
            <el-radio-button value="overview">概览</el-radio-button>
            <el-radio-button value="category">类别分析</el-radio-button>
            <el-radio-button value="location">地域分布</el-radio-button>
            <el-radio-button value="timeline">时间轴</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="exportCharts">
            <el-icon><Download /></el-icon>
            导出图表
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 可视化内容区 -->
    <div v-if="visualType === 'overview'" class="overview-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="card-panel">
            <h3 class="chart-title">灾害类别占比</h3>
            <div ref="categoryPieRef" class="chart-container"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="card-panel">
            <h3 class="chart-title">数据来源分布</h3>
            <div ref="sourcePieRef" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mt-20">
        <el-col :span="24">
          <div class="card-panel">
            <h3 class="chart-title">数据量趋势分析</h3>
            <div ref="trendLineRef" class="chart-container-lg"></div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mt-20">
        <el-col :span="12">
          <div class="card-panel">
            <h3 class="chart-title">载体类型分布</h3>
            <div ref="carrierBarRef" class="chart-container"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="card-panel">
            <h3 class="chart-title">数据完整度分析</h3>
            <div ref="completenessRef" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div v-if="visualType === 'category'" class="category-section">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="card-panel">
            <h3 class="chart-title">各类别数据量对比</h3>
            <div ref="categoryCompareRef" class="chart-container-lg"></div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="card-panel">
            <h3 class="chart-title">类别统计详情</h3>
            <div class="category-stats">
              <div
                v-for="(stat, index) in categoryStats"
                :key="index"
                class="stat-row"
              >
                <div class="stat-name">{{ stat.name }}</div>
                <div class="stat-bar">
                  <div
                    class="stat-bar-fill"
                    :style="{
                      width: stat.percentage + '%',
                      background: stat.color
                    }"
                  ></div>
                </div>
                <div class="stat-value">{{ stat.count }}</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mt-20">
        <el-col :span="24">
          <div class="card-panel">
            <h3 class="chart-title">类别时间分布热力图</h3>
            <div ref="categoryHeatmapRef" class="chart-container-lg"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div v-if="visualType === 'location'" class="location-section">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="card-panel">
            <h3 class="chart-title">地理分布地图</h3>
            <div ref="geoMapRef" class="chart-container-xl"></div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="card-panel">
            <h3 class="chart-title">地区数据排名</h3>
            <div class="location-ranking">
              <div
                v-for="(loc, index) in locationRanking"
                :key="index"
                class="rank-item"
              >
                <div class="rank-number" :class="'rank-' + (index + 1)">
                  {{ index + 1 }}
                </div>
                <div class="rank-info">
                  <div class="rank-name">{{ loc.name }}</div>
                  <div class="rank-count">{{ loc.count }} 条数据</div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mt-20">
        <el-col :span="24">
          <div class="card-panel">
            <h3 class="chart-title">地区数据密度分布</h3>
            <div ref="locationDensityRef" class="chart-container-lg"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div v-if="visualType === 'timeline'" class="timeline-section">
      <div class="card-panel">
        <h3 class="chart-title">时间轴可视化</h3>
        <div class="timeline-controls">
          <el-button-group>
            <el-button
              :type="timeGranularity === 'hour' ? 'primary' : ''"
              @click="changeTimeGranularity('hour')"
            >
              按小时
            </el-button>
            <el-button
              :type="timeGranularity === 'day' ? 'primary' : ''"
              @click="changeTimeGranularity('day')"
            >
              按天
            </el-button>
            <el-button
              :type="timeGranularity === 'week' ? 'primary' : ''"
              @click="changeTimeGranularity('week')"
            >
              按周
            </el-button>
            <el-button
              :type="timeGranularity === 'month' ? 'primary' : ''"
              @click="changeTimeGranularity('month')"
            >
              按月
            </el-button>
          </el-button-group>
        </div>
        <div ref="timelineRef" class="chart-container-xl"></div>
      </div>

      <el-row :gutter="20" class="mt-20">
        <el-col :span="12">
          <div class="card-panel">
            <h3 class="chart-title">时段分布分析</h3>
            <div ref="hourDistRef" class="chart-container"></div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="card-panel">
            <h3 class="chart-title">星期分布分析</h3>
            <div ref="weekDistRef" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getDisasterDataList } from '@/api/disaster'

const dateRange = ref([])
const filterForm = ref({
  disasterCategory: '',
  sourceType: ''
})

const visualType = ref('overview')
const timeGranularity = ref('day')
const allData = ref([])
const filteredData = ref([])

// 图表引用
const categoryPieRef = ref(null)
const sourcePieRef = ref(null)
const trendLineRef = ref(null)
const carrierBarRef = ref(null)
const completenessRef = ref(null)
const categoryCompareRef = ref(null)
const categoryHeatmapRef = ref(null)
const geoMapRef = ref(null)
const locationDensityRef = ref(null)
const timelineRef = ref(null)
const hourDistRef = ref(null)
const weekDistRef = ref(null)

const categoryStats = ref([])
const locationRanking = ref([])

// 颜色配置
const categoryColors = {
  '震情': '#ff6b6b',
  '人员伤亡': '#ee5a6f',
  '房屋破坏': '#feca57',
  '生命线工程': '#48dbfb',
  '次生灾害': '#ff9ff3',
  '其他': '#95afc0'
}

// 加载数据
const loadData = async () => {
  try {
    const res = await getDisasterDataList()
    if (res.data && res.data.length > 0) {
      allData.value = res.data

      // 筛选数据
      let filtered = allData.value

      if (dateRange.value && dateRange.value.length === 2) {
        const [startDate, endDate] = dateRange.value
        filtered = filtered.filter(item => {
          const itemDate = item.createTime ? item.createTime.substring(0, 10) : ''
          return itemDate >= startDate && itemDate <= endDate
        })
      }

      if (filterForm.value.disasterCategory) {
        filtered = filtered.filter(
          item => item.disasterCategory === filterForm.value.disasterCategory
        )
      }

      if (filterForm.value.sourceType) {
        filtered = filtered.filter(
          item => item.sourceType === filterForm.value.sourceType
        )
      }

      filteredData.value = filtered

      // 更新图表
      await nextTick()
      updateCharts()
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

// 更新图表
const updateCharts = () => {
  if (visualType.value === 'overview') {
    renderOverviewCharts()
  } else if (visualType.value === 'category') {
    renderCategoryCharts()
  } else if (visualType.value === 'location') {
    renderLocationCharts()
  } else if (visualType.value === 'timeline') {
    renderTimelineCharts()
  }
}

// 概览图表
const renderOverviewCharts = () => {
  const data = filteredData.value

  // 灾害类别占比
  const categoryStats = {}
  data.forEach(item => {
    const cat = item.disasterCategory || '未分类'
    categoryStats[cat] = (categoryStats[cat] || 0) + 1
  })

  const categoryChart = echarts.init(categoryPieRef.value)
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
      data: Object.entries(categoryStats).map(([name, value]) => ({
        name,
        value,
        itemStyle: { color: categoryColors[name] || '#95afc0' }
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })

  // 数据来源分布
  const sourceStats = {}
  data.forEach(item => {
    const source = item.sourceType || '未知'
    sourceStats[source] = (sourceStats[source] || 0) + 1
  })

  const sourceChart = echarts.init(sourcePieRef.value)
  sourceChart.setOption({
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
      name: '数据来源',
      type: 'pie',
      radius: '60%',
      data: Object.entries(sourceStats).map(([name, value]) => ({ name, value }))
    }]
  })

  // 趋势分析
  const dateStats = {}
  data.forEach(item => {
    const date = item.createTime ? item.createTime.substring(0, 10) : ''
    if (date) {
      dateStats[date] = (dateStats[date] || 0) + 1
    }
  })

  const sortedDates = Object.keys(dateStats).sort()
  const trendChart = echarts.init(trendLineRef.value)
  trendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: sortedDates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '数据量',
      type: 'line',
      smooth: true,
      data: sortedDates.map(date => dateStats[date]),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24, 144, 255, 0.3)' },
          { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
        ])
      },
      lineStyle: { color: '#1890ff' },
      itemStyle: { color: '#1890ff' }
    }]
  })

  // 载体类型分布
  const carrierStats = {}
  data.forEach(item => {
    const carrier = item.carrierType || '未知'
    carrierStats[carrier] = (carrierStats[carrier] || 0) + 1
  })

  const carrierChart = echarts.init(carrierBarRef.value)
  carrierChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: Object.keys(carrierStats)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      type: 'bar',
      data: Object.values(carrierStats),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  })

  // 数据完整度分析
  const completenessData = [
    { name: '完整数据', value: data.filter(d => d.location && d.description).length },
    { name: '缺少位置', value: data.filter(d => !d.location && d.description).length },
    { name: '缺少描述', value: data.filter(d => d.location && !d.description).length },
    { name: '数据不完整', value: data.filter(d => !d.location && !d.description).length }
  ]

  const completenessChart = echarts.init(completenessRef.value)
  completenessChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: 10
    },
    series: [{
      name: '数据完整度',
      type: 'pie',
      radius: '60%',
      data: completenessData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })

  // 响应式
  window.addEventListener('resize', () => {
    categoryChart.resize()
    sourceChart.resize()
    trendChart.resize()
    carrierChart.resize()
    completenessChart.resize()
  })
}

// 类别分析图表
const renderCategoryCharts = () => {
  const data = filteredData.value

  // 统计类别数据
  const categoryData = {}
  data.forEach(item => {
    const cat = item.disasterCategory || '未分类'
    categoryData[cat] = (categoryData[cat] || 0) + 1
  })

  const total = data.length
  categoryStats.value = Object.entries(categoryData).map(([name, count]) => ({
    name,
    count,
    percentage: ((count / total) * 100).toFixed(1),
    color: categoryColors[name] || '#95afc0'
  }))

  // 类别对比图
  const compareChart = echarts.init(categoryCompareRef.value)
  compareChart.setOption({
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
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: categoryStats.value.map(s => s.name)
    },
    series: [{
      type: 'bar',
      data: categoryStats.value.map(s => ({
        value: s.count,
        itemStyle: { color: s.color }
      })),
      label: {
        show: true,
        position: 'right'
      }
    }]
  })

  // 热力图 - 类别与日期的关系
  const dates = [...new Set(data.map(d => d.createTime?.substring(0, 10)).filter(Boolean))].sort()
  const categories = Object.keys(categoryData)

  const heatmapData = []
  categories.forEach((cat, catIdx) => {
    dates.forEach((date, dateIdx) => {
      const count = data.filter(
        d => d.disasterCategory === cat && d.createTime?.substring(0, 10) === date
      ).length
      if (count > 0) {
        heatmapData.push([dateIdx, catIdx, count])
      }
    })
  })

  const maxCount = Math.max(...heatmapData.map(d => d[2]))

  const heatmapChart = echarts.init(categoryHeatmapRef.value)
  heatmapChart.setOption({
    tooltip: {
      position: 'top',
      formatter: (params) => {
        return `${categories[params.value[1]]}<br/>${dates[params.value[0]]}<br/>数量: ${params.value[2]}`
      }
    },
    grid: {
      height: '60%',
      top: '10%'
    },
    xAxis: {
      type: 'category',
      data: dates,
      splitArea: {
        show: true
      },
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'category',
      data: categories,
      splitArea: {
        show: true
      }
    },
    visualMap: {
      min: 0,
      max: maxCount,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '5%',
      inRange: {
        color: ['#e0f3f8', '#abd9e9', '#74add1', '#4575b4', '#313695']
      }
    },
    series: [{
      name: '数据量',
      type: 'heatmap',
      data: heatmapData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  })

  window.addEventListener('resize', () => {
    compareChart.resize()
    heatmapChart.resize()
  })
}

// 地域分析图表
const renderLocationCharts = () => {
  const data = filteredData.value

  // 统计地区数据
  const locationData = {}
  data.forEach(item => {
    const loc = item.location || '未知地区'
    locationData[loc] = (locationData[loc] || 0) + 1
  })

  // 排序
  locationRanking.value = Object.entries(locationData)
    .map(([name, count]) => ({ name, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 10)

  // 地图可视化
  const geoData = data
    .filter(item => item.longitude && item.latitude)
    .map(item => ({
      name: item.location || '未知',
      value: [item.longitude, item.latitude, 1],
      category: item.disasterCategory
    }))

  const mapChart = echarts.init(geoMapRef.value)

  // 检查中国地图是否已加载
  try {
    mapChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          if (params.componentSubType === 'scatter') {
            return `${params.data.name}<br/>经度: ${params.data.value[0]}<br/>纬度: ${params.data.value[1]}`
          }
          return params.name
        }
      },
      geo: {
        map: 'china',
        roam: true,
        label: {
          emphasis: {
            show: false
          }
        },
        itemStyle: {
          normal: {
            areaColor: '#f3f3f3',
            borderColor: '#999'
          },
          emphasis: {
            areaColor: '#e0e0e0'
          }
        }
      },
      series: [{
        name: '灾情分布',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: geoData,
        symbolSize: 12,
        label: {
          show: false
        },
        itemStyle: {
          color: (params) => categoryColors[params.data.category] || '#95afc0',
          shadowBlur: 5,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        },
        emphasis: {
          scale: true,
          label: {
            show: true,
            formatter: '{b}',
            position: 'top'
          }
        }
      }]
    })
  } catch (error) {
    console.warn('地图加载中，请刷新页面', error)
    // 如果地图未加载，显示提示
    mapChart.setOption({
      title: {
        text: '地图加载中，请刷新页面',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#999',
          fontSize: 16
        }
      },
      xAxis: { show: false },
      yAxis: { show: false }
    })
  }

  // 地区密度分布
  const densityChart = echarts.init(locationDensityRef.value)
  densityChart.setOption({
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
      data: locationRanking.value.map(l => l.name),
      axisLabel: {
        rotate: 30
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      type: 'bar',
      data: locationRanking.value.map(l => l.count),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      }
    }]
  })

  window.addEventListener('resize', () => {
    mapChart.resize()
    densityChart.resize()
  })
}

// 时间轴图表
const renderTimelineCharts = () => {
  const data = filteredData.value

  // 按粒度统计
  const timeData = {}
  data.forEach(item => {
    if (!item.createTime) return

    let key
    const date = new Date(item.createTime)

    switch (timeGranularity.value) {
      case 'hour':
        key = item.createTime.substring(0, 13) + ':00'
        break
      case 'day':
        key = item.createTime.substring(0, 10)
        break
      case 'week':
        const weekNum = Math.floor((date - new Date(date.getFullYear(), 0, 1)) / 604800000)
        key = `${date.getFullYear()}-W${weekNum}`
        break
      case 'month':
        key = item.createTime.substring(0, 7)
        break
      default:
        key = item.createTime.substring(0, 10)
    }

    timeData[key] = (timeData[key] || 0) + 1
  })

  const sortedTimes = Object.keys(timeData).sort()

  const timelineChart = echarts.init(timelineRef.value)
  timelineChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: sortedTimes,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '数据量',
      type: 'line',
      smooth: true,
      data: sortedTimes.map(t => timeData[t]),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(102, 126, 234, 0.5)' },
          { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
        ])
      },
      lineStyle: { color: '#667eea', width: 3 },
      itemStyle: { color: '#667eea' }
    }]
  })

  // 时段分布
  const hourStats = {}
  data.forEach(item => {
    if (!item.createTime) return
    const hour = parseInt(item.createTime.substring(11, 13))
    hourStats[hour] = (hourStats[hour] || 0) + 1
  })

  const hourChart = echarts.init(hourDistRef.value)
  hourChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: Array.from({ length: 24 }, (_, i) => i + '时')
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      type: 'bar',
      data: Array.from({ length: 24 }, (_, i) => hourStats[i] || 0),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#4facfe' },
          { offset: 1, color: '#00f2fe' }
        ])
      }
    }]
  })

  // 星期分布
  const weekStats = {}
  const weekNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  data.forEach(item => {
    if (!item.createTime) return
    const day = new Date(item.createTime).getDay()
    weekStats[day] = (weekStats[day] || 0) + 1
  })

  const weekChart = echarts.init(weekDistRef.value)
  weekChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      bottom: 10
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: weekNames.map((name, idx) => ({
        name,
        value: weekStats[idx] || 0
      }))
    }]
  })

  window.addEventListener('resize', () => {
    timelineChart.resize()
    hourChart.resize()
    weekChart.resize()
  })
}

// 改变时间粒度
const changeTimeGranularity = (granularity) => {
  timeGranularity.value = granularity
  renderTimelineCharts()
}

// 导出图表
const exportCharts = () => {
  ElMessage.success('图表导出功能开发中，敬请期待')
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.visualization {
  .subtitle {
    color: #909399;
    margin-top: 5px;
  }

  .toolbar {
    margin-top: 20px;
    margin-bottom: 20px;
  }

  .mt-20 {
    margin-top: 20px;
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

  .chart-container-lg {
    height: 400px;
  }

  .chart-container-xl {
    height: 500px;
  }

  // 类别统计
  .category-stats {
    .stat-row {
      display: flex;
      align-items: center;
      gap: 15px;
      margin-bottom: 15px;

      .stat-name {
        width: 100px;
        font-size: 14px;
        color: #333;
      }

      .stat-bar {
        flex: 1;
        height: 24px;
        background: #f0f0f0;
        border-radius: 12px;
        overflow: hidden;

        .stat-bar-fill {
          height: 100%;
          border-radius: 12px;
          transition: width 0.5s ease;
        }
      }

      .stat-value {
        width: 50px;
        text-align: right;
        font-weight: 600;
        color: #333;
      }
    }
  }

  // 地区排名
  .location-ranking {
    max-height: 480px;
    overflow-y: auto;
    padding-right: 10px;

    .rank-item {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 12px;
      background: #f9f9f9;
      border-radius: 8px;
      margin-bottom: 10px;
      transition: all 0.3s;

      &:hover {
        background: #f0f0f0;
        transform: translateX(5px);
      }

      .rank-number {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        font-weight: 700;
        color: #fff;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

        &.rank-1 {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.rank-2 {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.rank-3 {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }

      .rank-info {
        flex: 1;

        .rank-name {
          font-size: 14px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .rank-count {
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }

  // 时间轴控制
  .timeline-controls {
    margin-bottom: 20px;
  }
}
</style>
