<template>
  <div class="data-list">
    <div class="page-header">
      <h2>灾情数据列表</h2>
    </div>

    <!-- 搜索栏 -->
    <div class="card-panel search-panel">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="编码ID">
          <el-input v-model="searchForm.encodedId" placeholder="请输入编码ID" clearable />
        </el-form-item>

        <el-form-item label="数据来源">
          <el-select v-model="searchForm.sourceCode" placeholder="请选择" clearable>
            <el-option label="业务报送" value="1" />
            <el-option label="泛在感知" value="2" />
            <el-option label="其他" value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="载体类型">
          <el-select v-model="searchForm.carrierType" placeholder="请选择" clearable>
            <el-option label="文字" value="text" />
            <el-option label="图像" value="image" />
            <el-option label="音频" value="audio" />
            <el-option label="视频" value="video" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="card-panel">
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="encodedId" label="编码ID" width="280" show-overflow-tooltip />
        <el-table-column prop="location" label="地理位置" width="180" show-overflow-tooltip />
        <el-table-column prop="sourceType" label="数据来源" width="120" />
        <el-table-column prop="carrierType" label="载体类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getCarrierTypeTag(row.carrierType)" size="small">
              {{ row.carrierType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip min-width="200" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="warning" size="small" @click="handleParse(row)">
              <el-icon><Key /></el-icon>
              解析
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="灾情数据详情" width="800px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="编码ID">{{ currentRow.encodedId }}</el-descriptions-item>
        <el-descriptions-item label="震情码">{{ currentRow.earthquakeCode }}</el-descriptions-item>
        <el-descriptions-item label="地理位置">{{ currentRow.location }}</el-descriptions-item>
        <el-descriptions-item label="经纬度">
          {{ currentRow.longitude }}, {{ currentRow.latitude }}
        </el-descriptions-item>
        <el-descriptions-item label="地震时间">{{ currentRow.earthquakeTime }}</el-descriptions-item>
        <el-descriptions-item label="数据来源">{{ currentRow.sourceType }}</el-descriptions-item>
        <el-descriptions-item label="载体类型">{{ currentRow.carrierType }}</el-descriptions-item>
        <el-descriptions-item label="灾害类别">{{ currentRow.disasterCategory }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentRow.content }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentRow.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDisasterDataList, deleteDisasterData, parseEncodedId } from '@/api/disaster'

const searchForm = ref({
  encodedId: '',
  sourceCode: '',
  carrierType: ''
})

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentRow = ref(null)

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getDisasterDataList()
    if (res.data) {
      tableData.value = res.data
      total.value = res.data.length
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.value = {
    encodedId: '',
    sourceCode: '',
    carrierType: ''
  }
  loadData()
}

// 查看详情
const handleView = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

// 解析编码
const handleParse = async (row) => {
  try {
    const res = await parseEncodedId(row.encodedId)
    ElMessageBox.alert(JSON.stringify(res.data, null, 2), '编码解析结果', {
      confirmButtonText: '确定'
    })
  } catch (error) {
    ElMessage.error('解析失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条数据吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDisasterData(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 新增
const showAddDialog = () => {
  ElMessage.info('新增功能开发中...')
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadData()
}

// 获取载体类型标签
const getCarrierTypeTag = (type) => {
  const map = {
    'text': 'success',
    'image': 'primary',
    'audio': 'warning',
    'video': 'danger'
  }
  return map[type] || 'info'
}

// 获取状态标签
const getStatusTag = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    0: '待处理',
    1: '已编码',
    2: '已存储',
    3: '已过期'
  }
  return map[status] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.data-list {
  .search-panel {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
