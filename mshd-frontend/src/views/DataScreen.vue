<template>
  <div class="data-screen">
    <!-- 顶部标题栏 -->
    <div class="screen-header">
      <div class="header-left">
        <div class="logo">MSHD 2.0</div>
      </div>
      <div class="header-center">
        <h1 class="main-title">多源异构灾情数据管理平台</h1>
        <p class="subtitle">Multi-Source Heterogeneous Data Disaster Management Platform</p>
      </div>
      <div class="header-right">
        <div class="datetime">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
        </div>
        <el-button
          circle
          :icon="isFullscreen ? 'compress' : 'full-screen'"
          @click="toggleFullscreen"
          class="fullscreen-btn"
        />
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="screen-body">
      <!-- 左侧列 -->
      <div class="left-column">
        <!-- 数据统计 -->
        <div class="panel-box">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>实时数据统计</span>
          </div>
          <div class="stats-grid">
            <div class="stat-item" v-for="(stat, index) in stats" :key="index">
              <div class="stat-icon" :style="{ background: stat.color }">
                <i :class="stat.icon"></i>
              </div>
              <div class="stat-info">
                <div class="stat-label">{{ stat.label }}</div>
                <div class="stat-value">{{ stat.value }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 灾害类别分布 -->
        <div class="panel-box">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>灾害类别分布</span>
          </div>
          <div ref="categoryChartRef" class="chart-container"></div>
        </div>

        <!-- 数据来源分布 -->
        <div class="panel-box">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>数据来源分布</span>
          </div>
          <div ref="sourceChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 中间列 -->
      <div class="center-column">
        <!-- 地图分布 -->
        <div class="panel-box map-panel">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>地理分布态势</span>
          </div>
          <div ref="mapChartRef" class="map-container"></div>
        </div>

        <!-- 时间趋势 -->
        <div class="panel-box">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>数据接入趋势</span>
          </div>
          <div ref="trendChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 右侧列 -->
      <div class="right-column">
        <!-- 载体类型分布 -->
        <div class="panel-box">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>载体类型分布</span>
          </div>
          <div ref="carrierChartRef" class="chart-container"></div>
        </div>

        <!-- 实时数据流 -->
        <div class="panel-box">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>实时数据流</span>
          </div>
          <div class="data-stream">
            <div
              v-for="item in recentData"
              :key="item.id"
              class="stream-item"
            >
              <div class="stream-time">{{ formatTime(item.createTime) }}</div>
              <div class="stream-content">
                <span class="stream-tag" :style="{ background: getCategoryColor(item.disasterCategory) }">
                  {{ item.disasterCategory }}
                </span>
                <span class="stream-location">{{ item.location }}</span>
              </div>
              <div class="stream-source">{{ item.sourceType }}</div>
            </div>
          </div>
        </div>

        <!-- 数据源状态 -->
        <div class="panel-box">
          <div class="panel-title">
            <span class="title-icon"></span>
            <span>数据源状态</span>
          </div>
          <div class="datasource-status">
            <div
              v-for="ds in dataSources"
              :key="ds.id"
              class="datasource-item"
            >
              <div class="ds-name">{{ ds.sourceName }}</div>
              <div class="ds-status">
                <span
                  class="status-dot"
                  :class="{ active: ds.enabled }"
                ></span>
                <span>{{ ds.enabled ? '在线' : '离线' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDisasterDataList, getStatsByCategory, getStatsBySource } from '@/api/disaster'
import { getDataSourceList } from '@/api/datasource'

const currentDate = ref('')
const currentTime = ref('')
const isFullscreen = ref(false)

// 统计数据
const stats = ref([
  { label: '总数据量', value: 0, icon: 'el-icon-data-line', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '今日新增', value: 0, icon: 'el-icon-trend-charts', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '接入数据源', value: 0, icon: 'el-icon-connection', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '覆盖地区', value: 0, icon: 'el-icon-location', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

const recentData = ref([])
const dataSources = ref([])

// 图表引用
const categoryChartRef = ref(null)
const sourceChartRef = ref(null)
const carrierChartRef = ref(null)
const trendChartRef = ref(null)
const mapChartRef = ref(null)

let categoryChart = null
let sourceChart = null
let carrierChart = null
let trendChart = null
let mapChart = null
let refreshTimer = null
let clockTimer = null

// 更新时钟
const updateClock = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long'
  })
  currentTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// 加载数据
const loadData = async () => {
  try {
    const res = await getDisasterDataList()
    if (res.data && res.data.length > 0) {
      const data = res.data

      // 统计数据
      stats.value[0].value = data.length

      // 今日新增
      const today = new Date().toISOString().substring(0, 10)
      const todayData = data.filter(item =>
        item.createTime && item.createTime.substring(0, 10) === today
      )
      stats.value[1].value = todayData.length

      // 覆盖地区
      const regions = [...new Set(data.map(item => item.location))].filter(l => l)
      stats.value[3].value = regions.length

      // 最近数据
      recentData.value = data.slice(0, 15)

      // 更新图表
      await nextTick()
      updateCharts(data)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 加载数据源
const loadDataSources = async () => {
  try {
    const res = await getDataSourceList()
    if (res.data) {
      dataSources.value = res.data.slice(0, 8)
      stats.value[2].value = res.data.filter(ds => ds.enabled).length
    }
  } catch (error) {
    console.error('加载数据源失败:', error)
  }
}

// 更新图表
const updateCharts = (data) => {
  // 灾害类别分布 - 玫瑰图
  const categoryStats = {}
  data.forEach(item => {
    const category = item.disasterCategory || '未分类'
    categoryStats[category] = (categoryStats[category] || 0) + 1
  })

  categoryChart = echarts.init(categoryChartRef.value)
  categoryChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [{
      name: '灾害类别',
      type: 'pie',
      radius: ['30%', '70%'],
      roseType: 'area',
      itemStyle: {
        borderRadius: 8
      },
      label: {
        color: '#fff',
        fontSize: 12
      },
      data: Object.entries(categoryStats).map(([name, value]) => ({
        name,
        value,
        itemStyle: {
          color: getCategoryColor(name)
        }
      }))
    }]
  })

  // 数据来源分布 - 雷达图
  const sourceStats = {}
  data.forEach(item => {
    const source = item.sourceType || '未知来源'
    sourceStats[source] = (sourceStats[source] || 0) + 1
  })

  const sourceNames = Object.keys(sourceStats)
  const maxValue = Math.max(...Object.values(sourceStats))

  sourceChart = echarts.init(sourceChartRef.value)
  sourceChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item'
    },
    radar: {
      indicator: sourceNames.map(name => ({
        name,
        max: maxValue
      })),
      splitArea: {
        areaStyle: {
          color: ['rgba(255, 255, 255, 0.05)', 'rgba(255, 255, 255, 0.1)']
        }
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.2)'
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.2)'
        }
      },
      name: {
        textStyle: {
          color: '#fff'
        }
      }
    },
    series: [{
      name: '数据来源',
      type: 'radar',
      data: [{
        value: Object.values(sourceStats),
        name: '数据量',
        areaStyle: {
          color: 'rgba(100, 200, 255, 0.3)'
        },
        lineStyle: {
          color: '#64c8ff'
        }
      }]
    }]
  })

  // 载体类型分布 - 环形图
  const carrierStats = {}
  data.forEach(item => {
    const carrier = item.carrierType || '未知'
    carrierStats[carrier] = (carrierStats[carrier] || 0) + 1
  })

  carrierChart = echarts.init(carrierChartRef.value)
  carrierChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: {
        color: '#fff'
      }
    },
    series: [{
      name: '载体类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      label: {
        show: true,
        color: '#fff'
      },
      data: Object.entries(carrierStats).map(([name, value]) => ({
        name,
        value
      })),
      itemStyle: {
        borderRadius: 10,
        borderColor: '#0a0e27',
        borderWidth: 2
      }
    }]
  })

  // 时间趋势 - 面积图
  const dateStats = {}
  data.forEach(item => {
    const date = item.createTime ? item.createTime.substring(0, 10) : ''
    if (date) {
      dateStats[date] = (dateStats[date] || 0) + 1
    }
  })

  const sortedDates = Object.keys(dateStats).sort().slice(-30)

  trendChart = echarts.init(trendChartRef.value)
  trendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '5%',
      right: '5%',
      bottom: '5%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: sortedDates,
      axisLabel: {
        color: '#fff',
        rotate: 45
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.3)'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#fff'
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      }
    },
    series: [{
      name: '数据量',
      type: 'line',
      smooth: true,
      data: sortedDates.map(date => dateStats[date]),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24, 144, 255, 0.8)' },
          { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
        ])
      },
      lineStyle: {
        color: '#1890ff',
        width: 3
      },
      itemStyle: {
        color: '#1890ff'
      }
    }]
  })

  // 地图分布 - 散点图
  const geoData = data
    .filter(item => item.longitude && item.latitude)
    .map(item => ({
      name: item.location || '未知',
      value: [item.longitude, item.latitude, 1],
      category: item.disasterCategory
    }))

  mapChart = echarts.init(mapChartRef.value)

  // 检查中国地图是否已加载
  try {
    mapChart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          return `${params.data.name}<br/>经度: ${params.data.value[0]}<br/>纬度: ${params.data.value[1]}`
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
            areaColor: 'rgba(0, 50, 100, 0.3)',
            borderColor: 'rgba(100, 200, 255, 0.5)',
            borderWidth: 1
          },
          emphasis: {
            areaColor: 'rgba(0, 100, 200, 0.5)'
          }
        }
      },
      series: [{
        name: '灾情分布',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: geoData,
        symbolSize: 15,
        label: {
          show: false
        },
        itemStyle: {
          color: (params) => getCategoryColor(params.data.category),
          shadowBlur: 10,
          shadowColor: 'rgba(255, 255, 255, 0.5)'
        },
        emphasis: {
          scale: true,
          label: {
            show: true,
            formatter: '{b}',
            position: 'top',
            color: '#fff'
          }
        }
      }]
    })
  } catch (error) {
    console.warn('地图加载中，请稍后刷新页面', error)
    // 如果地图未加载，显示简单的散点图
    mapChart.setOption({
      backgroundColor: 'transparent',
      title: {
        text: '地图加载中...',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#fff',
          fontSize: 16
        }
      },
      xAxis: { show: false },
      yAxis: { show: false }
    })
  }

  // 响应式
  window.addEventListener('resize', () => {
    categoryChart?.resize()
    sourceChart?.resize()
    carrierChart?.resize()
    trendChart?.resize()
    mapChart?.resize()
  })
}

// 获取类别颜色
const getCategoryColor = (category) => {
  const colorMap = {
    '震情': '#ff6b6b',
    '人员伤亡': '#ee5a6f',
    '房屋破坏': '#feca57',
    '生命线工程': '#48dbfb',
    '次生灾害': '#ff9ff3',
    '其他': '#95afc0'
  }
  return colorMap[category] || '#95afc0'
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.substring(5, 16).replace(' ', ' ')
}

onMounted(async () => {
  // 更新时钟
  updateClock()
  clockTimer = setInterval(updateClock, 1000)

  // 加载数据
  await loadData()
  await loadDataSources()

  // 定时刷新
  refreshTimer = setInterval(() => {
    loadData()
    loadDataSources()
  }, 30000) // 30秒刷新一次
})

onUnmounted(() => {
  if (clockTimer) clearInterval(clockTimer)
  if (refreshTimer) clearInterval(refreshTimer)

  categoryChart?.dispose()
  sourceChart?.dispose()
  carrierChart?.dispose()
  trendChart?.dispose()
  mapChart?.dispose()
})
</script>

<style lang="scss" scoped>
.data-screen {
  background: #0a0e27;
  min-height: calc(100vh - 120px);
  padding: 20px;
  color: #fff;
  position: relative;
  overflow-x: hidden;

  // 背景装饰
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image:
      linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px);
    background-size: 50px 50px;
    pointer-events: none;
  }

  .screen-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: linear-gradient(135deg, rgba(100, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
    border-radius: 10px;
    backdrop-filter: blur(10px);
    margin-bottom: 20px;
    border: 1px solid rgba(255, 255, 255, 0.1);

    .header-left {
      .logo {
        font-size: 28px;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .header-center {
      text-align: center;

      .main-title {
        font-size: 36px;
        font-weight: 700;
        margin: 0;
        background: linear-gradient(135deg, #fff 0%, #64c8ff 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        letter-spacing: 4px;
      }

      .subtitle {
        margin: 5px 0 0;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.6);
        letter-spacing: 2px;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 15px;

      .datetime {
        text-align: right;

        .date {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.8);
        }

        .time {
          font-size: 24px;
          font-weight: 600;
          color: #64c8ff;
          font-family: 'Courier New', monospace;
          letter-spacing: 2px;
        }
      }

      .fullscreen-btn {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 255, 255, 0.3);
        color: #fff;

        &:hover {
          background: rgba(255, 255, 255, 0.2);
        }
      }
    }
  }

  .screen-body {
    display: grid;
    grid-template-columns: 1fr 2fr 1fr;
    gap: 20px;
    position: relative;
    z-index: 1;
  }

  .left-column,
  .right-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .center-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .panel-box {
    background: rgba(10, 14, 39, 0.6);
    border-radius: 10px;
    padding: 20px;
    border: 1px solid rgba(100, 200, 255, 0.2);
    backdrop-filter: blur(10px);
    transition: all 0.3s;

    &:hover {
      border-color: rgba(100, 200, 255, 0.5);
      box-shadow: 0 0 20px rgba(100, 200, 255, 0.3);
    }

    &.map-panel {
      flex: 1;
    }

    .panel-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 15px;
      padding-bottom: 10px;
      border-bottom: 2px solid rgba(100, 200, 255, 0.3);

      .title-icon {
        width: 4px;
        height: 18px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 2px;
      }
    }
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 15px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        transform: translateY(-2px);
      }

      .stat-icon {
        width: 50px;
        height: 50px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #fff;
      }

      .stat-info {
        flex: 1;

        .stat-label {
          font-size: 13px;
          color: rgba(255, 255, 255, 0.7);
          margin-bottom: 5px;
        }

        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #fff;
        }
      }
    }
  }

  .chart-container {
    height: 250px;
  }

  .map-container {
    height: 100%;
    min-height: 400px;
  }

  .data-stream {
    max-height: 350px;
    overflow-y: auto;
    padding-right: 5px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: rgba(100, 200, 255, 0.3);
      border-radius: 3px;

      &:hover {
        background: rgba(100, 200, 255, 0.5);
      }
    }

    .stream-item {
      padding: 10px;
      background: rgba(255, 255, 255, 0.05);
      border-left: 3px solid #64c8ff;
      border-radius: 4px;
      margin-bottom: 10px;
      transition: all 0.3s;
      animation: slideIn 0.5s ease-out;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        transform: translateX(5px);
      }

      .stream-time {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.5);
        margin-bottom: 5px;
      }

      .stream-content {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 5px;

        .stream-tag {
          padding: 2px 8px;
          border-radius: 4px;
          font-size: 12px;
          color: #fff;
        }

        .stream-location {
          font-size: 14px;
          color: #fff;
        }
      }

      .stream-source {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.6);
      }
    }
  }

  .datasource-status {
    .datasource-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 6px;
      margin-bottom: 10px;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }

      .ds-name {
        font-size: 14px;
        color: #fff;
      }

      .ds-status {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: rgba(255, 255, 255, 0.7);

        .status-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: #666;
          transition: all 0.3s;

          &.active {
            background: #52c41a;
            box-shadow: 0 0 10px rgba(82, 196, 26, 0.6);
            animation: pulse 2s infinite;
          }
        }
      }
    }
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
