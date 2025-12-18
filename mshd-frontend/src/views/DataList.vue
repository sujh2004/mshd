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
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button link type="success" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="addDialogVisible"
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
            <el-form-item label="地理位置" prop="location">
              <el-input v-model="formData.location" placeholder="例如：青海省海北州门源县" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地震时间" prop="earthquakeTime">
              <el-date-picker
                v-model="formData.earthquakeTime"
                type="datetime"
                placeholder="选择地震发生时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input-number
                v-model="formData.longitude"
                :precision="6"
                :min="-180"
                :max="180"
                style="width: 100%"
                placeholder="例如：101.123456"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input-number
                v-model="formData.latitude"
                :precision="6"
                :min="-90"
                :max="90"
                style="width: 100%"
                placeholder="例如：37.654321"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据来源" prop="sourceType">
              <el-select v-model="formData.sourceType" placeholder="请选择数据来源" style="width: 100%">
                <el-option label="后方指挥部" value="后方指挥部" />
                <el-option label="现场指挥部" value="现场指挥部" />
                <el-option label="业务报送" value="业务报送" />
                <el-option label="泛在感知" value="泛在感知" />
                <el-option label="舆情感知" value="舆情感知" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="载体类型" prop="carrierType">
              <el-select v-model="formData.carrierType" placeholder="请选择载体类型" style="width: 100%">
                <el-option label="文字" value="文字" />
                <el-option label="图像" value="图像" />
                <el-option label="音频" value="音频" />
                <el-option label="视频" value="视频" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="灾害类别" prop="disasterCategory">
              <el-select v-model="formData.disasterCategory" placeholder="请选择灾害类别" style="width: 100%">
                <el-option label="震情" value="震情" />
                <el-option label="人员伤亡" value="人员伤亡" />
                <el-option label="房屋破坏" value="房屋破坏" />
                <el-option label="生命线工程" value="生命线工程" />
                <el-option label="次生灾害" value="次生灾害" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="待处理" :value="0" />
                <el-option label="已编码" :value="1" />
                <el-option label="已存储" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请输入灾情数据内容"
          />
        </el-form-item>

        <el-form-item label="编码预览">
          <el-input
            :value="generateEncodedId()"
            readonly
            placeholder="系统将自动生成36位编码"
          >
            <template #prepend>
              <el-tag type="info">自动生成</el-tag>
            </template>
          </el-input>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            格式：地理信息码(12位) + 时间码(14位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

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
import { getDisasterDataList, deleteDisasterData, parseEncodedId, createDisasterData, updateDisasterData } from '@/api/disaster'

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
const addDialogVisible = ref(false)
const dialogTitle = ref('新增灾情数据')
const currentRow = ref(null)
const formRef = ref(null)
const submitLoading = ref(false)
const isEditMode = ref(false)
const editId = ref(null)

// 表单数据
const formData = ref({
  location: '',
  longitude: null,
  latitude: null,
  earthquakeTime: '',
  sourceType: '',
  carrierType: '',
  disasterCategory: '',
  content: '',
  status: 1
})

// 表单验证规则
const formRules = {
  location: [
    { required: true, message: '请输入地理位置', trigger: 'blur' }
  ],
  longitude: [
    { required: true, message: '请输入经度', trigger: 'blur' }
  ],
  latitude: [
    { required: true, message: '请输入纬度', trigger: 'blur' }
  ],
  earthquakeTime: [
    { required: true, message: '请选择地震时间', trigger: 'change' }
  ],
  sourceType: [
    { required: true, message: '请选择数据来源', trigger: 'change' }
  ],
  carrierType: [
    { required: true, message: '请选择载体类型', trigger: 'change' }
  ],
  disasterCategory: [
    { required: true, message: '请选择灾害类别', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ]
}

// 生成36位一体化编码
const generateEncodedId = () => {
  if (!formData.value.longitude || !formData.value.latitude ||
      !formData.value.earthquakeTime || !formData.value.sourceType ||
      !formData.value.carrierType || !formData.value.disasterCategory) {
    return ''
  }

  // 1. 地理信息码(12位)：经度6位 + 纬度6位
  const lng = String(Math.abs(formData.value.longitude).toFixed(2)).replace('.', '').padStart(6, '0').substring(0, 6)
  const lat = String(Math.abs(formData.value.latitude).toFixed(2)).replace('.', '').padStart(6, '0').substring(0, 6)
  const geoCode = lng + lat

  // 2. 时间码(14位)：YYYYMMDDHHmmss
  const timeStr = formData.value.earthquakeTime.replace(/[-:\s]/g, '')
  const timeCode = timeStr.substring(0, 14)

  // 3. 来源码(3位)
  const sourceMap = {
    '后方指挥部': '101',
    '现场指挥部': '102',
    '业务报送': '201',
    '泛在感知': '202',
    '舆情感知': '203',
    '其他': '999'
  }
  const sourceCode = sourceMap[formData.value.sourceType] || '999'

  // 4. 载体码(1位)：0=文字 1=图像 2=音频 3=视频
  const carrierMap = {
    '文字': '0',
    '图像': '1',
    '音频': '2',
    '视频': '3'
  }
  const carrierCode = carrierMap[formData.value.carrierType] || '0'

  // 5. 灾情码(6位)
  const categoryMap = {
    '震情': '301001',
    '人员伤亡': '302001',
    '房屋破坏': '303001',
    '生命线工程': '304001',
    '次生灾害': '305001',
    '其他': '399999'
  }
  const disasterCode = categoryMap[formData.value.disasterCategory] || '399999'

  return geoCode + timeCode + sourceCode + carrierCode + disasterCode
}

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

// 显示新增对话框
const showAddDialog = () => {
  isEditMode.value = false
  editId.value = null
  dialogTitle.value = '新增灾情数据'
  formData.value = {
    location: '',
    longitude: null,
    latitude: null,
    earthquakeTime: '',
    sourceType: '',
    carrierType: '',
    disasterCategory: '',
    content: '',
    status: 1
  }
  addDialogVisible.value = true
}

// 显示编辑对话框
const handleEdit = (row) => {
  isEditMode.value = true
  editId.value = row.id
  dialogTitle.value = '编辑灾情数据'
  formData.value = {
    location: row.location,
    longitude: row.longitude,
    latitude: row.latitude,
    earthquakeTime: row.earthquakeTime,
    sourceType: row.sourceType,
    carrierType: row.carrierType,
    disasterCategory: row.disasterCategory,
    content: row.content,
    status: row.status
  }
  addDialogVisible.value = true
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
        const encodedId = generateEncodedId()
        const submitData = {
          ...formData.value,
          encodedId
        }

        if (isEditMode.value && editId.value) {
          // 编辑模式
          await updateDisasterData(editId.value, submitData)
          ElMessage.success('更新成功')
        } else {
          // 新增模式
          await createDisasterData(submitData)
          ElMessage.success('新增成功')
        }

        addDialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error((isEditMode.value ? '更新' : '新增') + '失败：' + (error.message || '未知错误'))
      } finally {
        submitLoading.value = false
      }
    }
  })
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
    'video': 'danger',
    '文字': 'success',
    '图像': 'primary',
    '音频': 'warning',
    '视频': 'danger'
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
