<template>
  <div class="data-source">
    <div class="page-header">
      <h2>数据源管理</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <div class="stat-label">数据源总数</div>
          <div class="stat-number">{{ stats.total }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <div class="stat-label">已启用</div>
          <div class="stat-number">{{ stats.enabled }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <div class="stat-label">业务报送</div>
          <div class="stat-number">{{ stats.business }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <div class="stat-label">泛在感知</div>
          <div class="stat-number">{{ stats.ubiquitous }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 搜索和操作栏 -->
    <div class="card-panel search-panel">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="数据源名称">
          <el-input v-model="searchForm.sourceName" placeholder="请输入数据源名称" clearable />
        </el-form-item>

        <el-form-item label="数据源类型">
          <el-select v-model="searchForm.sourceType" placeholder="请选择" clearable>
            <el-option label="业务报送" value="business" />
            <el-option label="泛在感知" value="ubiquitous" />
            <el-option label="舆情感知" value="public_opinion" />
            <el-option label="承载体基础数据" value="infrastructure" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.enabled" placeholder="请选择" clearable>
            <el-option label="已启用" :value="true" />
            <el-option label="已禁用" :value="false" />
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
            新增数据源
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据源列表 -->
    <div class="card-panel">
      <el-table :data="filteredData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="sourceName" label="数据源名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sourceCode" label="编码" width="80" />
        <el-table-column prop="sourceType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.sourceType)" size="small">
              {{ getTypeText(row.sourceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sourceUrl" label="数据源URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="protocol" label="协议" width="100" />
        <el-table-column prop="dataFormat" label="数据格式" width="100" />
        <el-table-column prop="enabled" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              @change="handleToggleEnabled(row)"
              :loading="row.toggleLoading"
            />
          </template>
        </el-table-column>
        <el-table-column prop="syncStatus" label="同步状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getSyncStatusTag(row.syncStatus)" size="small">
              {{ getSyncStatusText(row.syncStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button link type="success" size="small" @click="handleTest(row)">
              <el-icon><Connection /></el-icon>
              测试
            </el-button>
            <el-button link type="warning" size="small" @click="handleSync(row)">
              <el-icon><Refresh /></el-icon>
              同步
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据源名称" prop="sourceName">
              <el-input v-model="formData.sourceName" placeholder="请输入数据源名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据源编码" prop="sourceCode">
              <el-input
                v-model="formData.sourceCode"
                placeholder="3位编码"
                maxlength="3"
                :disabled="isEditMode"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据源类型" prop="sourceType">
              <el-select v-model="formData.sourceType" placeholder="请选择" style="width: 100%">
                <el-option label="业务报送" value="business" />
                <el-option label="泛在感知" value="ubiquitous" />
                <el-option label="舆情感知" value="public_opinion" />
                <el-option label="承载体基础数据" value="infrastructure" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="接口协议" prop="protocol">
              <el-select v-model="formData.protocol" placeholder="请选择" style="width: 100%">
                <el-option label="HTTP" value="HTTP" />
                <el-option label="HTTPS" value="HTTPS" />
                <el-option label="MQTT" value="MQTT" />
                <el-option label="WebSocket" value="WebSocket" />
                <el-option label="FTP" value="FTP" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="数据源URL" prop="sourceUrl">
          <el-input v-model="formData.sourceUrl" placeholder="请输入数据源URL" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="认证方式" prop="authType">
              <el-select v-model="formData.authType" placeholder="请选择" style="width: 100%">
                <el-option label="无认证" value="none" />
                <el-option label="基础认证" value="basic" />
                <el-option label="Token认证" value="token" />
                <el-option label="OAuth2" value="oauth2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据格式" prop="dataFormat">
              <el-select v-model="formData.dataFormat" placeholder="请选择" style="width: 100%">
                <el-option label="JSON" value="JSON" />
                <el-option label="XML" value="XML" />
                <el-option label="CSV" value="CSV" />
                <el-option label="Binary" value="Binary" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="认证凭证" prop="credentials" v-if="formData.authType !== 'none'">
          <el-input
            v-model="formData.credentials"
            type="textarea"
            :rows="2"
            placeholder="请输入认证凭证（如Token、密钥等）"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="轮询间隔(秒)" prop="pollInterval">
              <el-input-number
                v-model="formData.pollInterval"
                :min="0"
                :max="86400"
                style="width: 100%"
                placeholder="0表示不自动轮询"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用" prop="enabled">
              <el-switch v-model="formData.enabled" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDataSourceList,
  createDataSource,
  updateDataSource,
  deleteDataSource,
  toggleDataSource,
  testConnection,
  syncDataSource,
  getDataSourceStats
} from '@/api/datasource'

const searchForm = ref({
  sourceName: '',
  sourceType: '',
  enabled: null
})

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增数据源')
const formRef = ref(null)
const submitLoading = ref(false)
const isEditMode = ref(false)
const editId = ref(null)

const stats = ref({
  total: 0,
  enabled: 0,
  business: 0,
  ubiquitous: 0
})

// 表单数据
const formData = ref({
  sourceName: '',
  sourceCode: '',
  sourceType: '',
  sourceUrl: '',
  protocol: '',
  authType: 'none',
  credentials: '',
  dataFormat: 'JSON',
  enabled: true,
  pollInterval: 0,
  remark: ''
})

// 表单验证规则
const formRules = {
  sourceName: [
    { required: true, message: '请输入数据源名称', trigger: 'blur' }
  ],
  sourceCode: [
    { required: true, message: '请输入数据源编码', trigger: 'blur' },
    { len: 3, message: '编码必须为3位', trigger: 'blur' }
  ],
  sourceType: [
    { required: true, message: '请选择数据源类型', trigger: 'change' }
  ],
  sourceUrl: [
    { required: true, message: '请输入数据源URL', trigger: 'blur' }
  ],
  protocol: [
    { required: true, message: '请选择接口协议', trigger: 'change' }
  ],
  dataFormat: [
    { required: true, message: '请选择数据格式', trigger: 'change' }
  ]
}

// 过滤后的数据
const filteredData = computed(() => {
  return tableData.value.filter(item => {
    let match = true
    if (searchForm.value.sourceName) {
      match = match && item.sourceName.includes(searchForm.value.sourceName)
    }
    if (searchForm.value.sourceType) {
      match = match && item.sourceType === searchForm.value.sourceType
    }
    if (searchForm.value.enabled !== null && searchForm.value.enabled !== '') {
      match = match && item.enabled === searchForm.value.enabled
    }
    return match
  })
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getDataSourceList()
    if (res.data) {
      tableData.value = res.data.map(item => ({
        ...item,
        toggleLoading: false
      }))
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getDataSourceStats()
    if (res.data) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  // 过滤逻辑在 computed 中处理
}

// 重置
const handleReset = () => {
  searchForm.value = {
    sourceName: '',
    sourceType: '',
    enabled: null
  }
}

// 显示新增对话框
const showAddDialog = () => {
  isEditMode.value = false
  editId.value = null
  dialogTitle.value = '新增数据源'
  formData.value = {
    sourceName: '',
    sourceCode: '',
    sourceType: '',
    sourceUrl: '',
    protocol: '',
    authType: 'none',
    credentials: '',
    dataFormat: 'JSON',
    enabled: true,
    pollInterval: 0,
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEditMode.value = true
  editId.value = row.id
  dialogTitle.value = '编辑数据源'
  formData.value = {
    sourceName: row.sourceName,
    sourceCode: row.sourceCode,
    sourceType: row.sourceType,
    sourceUrl: row.sourceUrl,
    protocol: row.protocol,
    authType: row.authType || 'none',
    credentials: row.credentials || '',
    dataFormat: row.dataFormat,
    enabled: row.enabled,
    pollInterval: row.pollInterval || 0,
    remark: row.remark || ''
  }
  dialogVisible.value = true
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  isEditMode.value = false
  editId.value = null
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEditMode.value && editId.value) {
          await updateDataSource(editId.value, formData.value)
          ElMessage.success('更新成功')
        } else {
          await createDataSource(formData.value)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadData()
        loadStats()
      } catch (error) {
        ElMessage.error((isEditMode.value ? '更新' : '新增') + '失败：' + (error.message || '未知错误'))
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 启用/禁用
const handleToggleEnabled = async (row) => {
  row.toggleLoading = true
  try {
    await toggleDataSource(row.id, row.enabled)
    ElMessage.success(row.enabled ? '已启用' : '已禁用')
    loadStats()
  } catch (error) {
    row.enabled = !row.enabled
    ElMessage.error('操作失败')
  } finally {
    row.toggleLoading = false
  }
}

// 测试连接
const handleTest = async (row) => {
  try {
    const res = await testConnection(row)
    if (res.code === 200) {
      ElMessage.success(res.message || '连接测试成功')
    } else {
      ElMessage.error(res.message || '连接测试失败')
    }
  } catch (error) {
    ElMessage.error('测试失败：' + (error.message || '未知错误'))
  }
}

// 同步数据源
const handleSync = async (row) => {
  try {
    const res = await syncDataSource(row.id)
    if (res.code === 200) {
      ElMessage.success('同步成功')
      loadData()
    } else {
      ElMessage.error(res.message || '同步失败')
    }
  } catch (error) {
    ElMessage.error('同步失败：' + (error.message || '未知错误'))
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这个数据源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDataSource(row.id)
      ElMessage.success('删除成功')
      loadData()
      loadStats()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 获取类型标签
const getTypeTag = (type) => {
  const map = {
    'business': 'primary',
    'ubiquitous': 'success',
    'public_opinion': 'warning',
    'infrastructure': 'info',
    'other': ''
  }
  return map[type] || ''
}

// 获取类型文本
const getTypeText = (type) => {
  const map = {
    'business': '业务报送',
    'ubiquitous': '泛在感知',
    'public_opinion': '舆情感知',
    'infrastructure': '承载体基础数据',
    'other': '其他'
  }
  return map[type] || type
}

// 获取同步状态标签
const getSyncStatusTag = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

// 获取同步状态文本
const getSyncStatusText = (status) => {
  const map = {
    0: '未同步',
    1: '同步中',
    2: '成功',
    3: '失败'
  }
  return map[status] || '未知'
}

onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style lang="scss" scoped>
.data-source {
  .stats-row {
    margin-bottom: 20px;
  }

  .search-panel {
    margin-bottom: 20px;
  }
}
</style>
