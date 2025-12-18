<template>
  <div class="encoding">
    <div class="page-header">
      <h2>数据编码工具</h2>
      <p class="subtitle">灾情数据36位一体化编码生成与解析</p>
    </div>

    <el-tabs v-model="activeTab" class="encoding-tabs">
      <!-- 编码生成器 -->
      <el-tab-pane label="编码生成器" name="generator">
        <div class="card-panel">
          <h3 class="section-title">
            <el-icon><Setting /></el-icon>
            编码参数配置
          </h3>

          <el-form :model="generatorForm" label-width="140px" class="generator-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="经度">
                  <el-input-number
                    v-model="generatorForm.longitude"
                    :precision="6"
                    :min="-180"
                    :max="180"
                    style="width: 100%"
                    placeholder="例如：101.123456"
                    @change="updateGeneratedCode"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="纬度">
                  <el-input-number
                    v-model="generatorForm.latitude"
                    :precision="6"
                    :min="-90"
                    :max="90"
                    style="width: 100%"
                    placeholder="例如：37.654321"
                    @change="updateGeneratedCode"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="地震时间">
              <el-date-picker
                v-model="generatorForm.earthquakeTime"
                type="datetime"
                placeholder="选择地震发生时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
                @change="updateGeneratedCode"
              />
            </el-form-item>

            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="数据来源">
                  <el-select
                    v-model="generatorForm.sourceType"
                    placeholder="请选择数据来源"
                    style="width: 100%"
                    @change="updateGeneratedCode"
                  >
                    <el-option label="后方指挥部" value="后方指挥部" />
                    <el-option label="现场指挥部" value="现场指挥部" />
                    <el-option label="业务报送" value="业务报送" />
                    <el-option label="泛在感知" value="泛在感知" />
                    <el-option label="舆情感知" value="舆情感知" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="载体类型">
                  <el-select
                    v-model="generatorForm.carrierType"
                    placeholder="请选择载体类型"
                    style="width: 100%"
                    @change="updateGeneratedCode"
                  >
                    <el-option label="文字" value="文字" />
                    <el-option label="图像" value="图像" />
                    <el-option label="音频" value="音频" />
                    <el-option label="视频" value="视频" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="灾害类别">
                  <el-select
                    v-model="generatorForm.disasterCategory"
                    placeholder="请选择灾害类别"
                    style="width: 100%"
                    @change="updateGeneratedCode"
                  >
                    <el-option label="震情" value="震情" />
                    <el-option label="人员伤亡" value="人员伤亡" />
                    <el-option label="房屋破坏" value="房屋破坏" />
                    <el-option label="生命线工程" value="生命线工程" />
                    <el-option label="次生灾害" value="次生灾害" />
                    <el-option label="其他" value="其他" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>

          <el-divider />

          <h3 class="section-title">
            <el-icon><View /></el-icon>
            生成的编码
          </h3>

          <div class="code-display">
            <div class="code-structure">
              <div class="code-part geo">
                <div class="code-value">{{ codeStructure.geo }}</div>
                <div class="code-label">地理信息码(12位)</div>
              </div>
              <div class="code-part time">
                <div class="code-value">{{ codeStructure.time }}</div>
                <div class="code-label">时间码(14位)</div>
              </div>
              <div class="code-part source">
                <div class="code-value">{{ codeStructure.source }}</div>
                <div class="code-label">来源码(3位)</div>
              </div>
              <div class="code-part carrier">
                <div class="code-value">{{ codeStructure.carrier }}</div>
                <div class="code-label">载体码(1位)</div>
              </div>
              <div class="code-part disaster">
                <div class="code-value">{{ codeStructure.disaster }}</div>
                <div class="code-label">灾情码(6位)</div>
              </div>
            </div>

            <div class="full-code">
              <el-input
                :value="generatedCode"
                readonly
                size="large"
              >
                <template #prepend>
                  <span style="font-weight: 600;">完整编码</span>
                </template>
                <template #append>
                  <el-button @click="copyCode" :icon="DocumentCopy">
                    复制
                  </el-button>
                </template>
              </el-input>
            </div>

            <div class="code-info" v-if="generatedCode">
              <el-descriptions :column="2" border size="small">
                <el-descriptions-item label="经度信息">{{ generatorForm.longitude }}</el-descriptions-item>
                <el-descriptions-item label="纬度信息">{{ generatorForm.latitude }}</el-descriptions-item>
                <el-descriptions-item label="时间信息">{{ generatorForm.earthquakeTime }}</el-descriptions-item>
                <el-descriptions-item label="来源信息">{{ generatorForm.sourceType }} ({{ codeStructure.source }})</el-descriptions-item>
                <el-descriptions-item label="载体信息">{{ generatorForm.carrierType }} ({{ codeStructure.carrier }})</el-descriptions-item>
                <el-descriptions-item label="灾情信息">{{ generatorForm.disasterCategory }} ({{ codeStructure.disaster }})</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 编码解析器 -->
      <el-tab-pane label="编码解析器" name="parser">
        <div class="card-panel">
          <h3 class="section-title">
            <el-icon><Search /></el-icon>
            输入编码进行解析
          </h3>

          <el-input
            v-model="parserInput"
            placeholder="请输入36位编码"
            size="large"
            maxlength="36"
            clearable
            @input="parseCode"
          >
            <template #prepend>
              <span style="font-weight: 600;">编码输入</span>
            </template>
          </el-input>

          <div class="parser-result" v-if="parsedData">
            <el-divider />

            <h3 class="section-title">
              <el-icon><Document /></el-icon>
              解析结果
            </h3>

            <div class="code-structure">
              <div class="code-part geo">
                <div class="code-value">{{ parsedData.geoCode }}</div>
                <div class="code-label">地理信息码</div>
              </div>
              <div class="code-part time">
                <div class="code-value">{{ parsedData.timeCode }}</div>
                <div class="code-label">时间码</div>
              </div>
              <div class="code-part source">
                <div class="code-value">{{ parsedData.sourceCode }}</div>
                <div class="code-label">来源码</div>
              </div>
              <div class="code-part carrier">
                <div class="code-value">{{ parsedData.carrierCode }}</div>
                <div class="code-label">载体码</div>
              </div>
              <div class="code-part disaster">
                <div class="code-value">{{ parsedData.disasterCode }}</div>
                <div class="code-label">灾情码</div>
              </div>
            </div>

            <el-descriptions :column="2" border class="parsed-details">
              <el-descriptions-item label="地理位置">
                经度: {{ parsedData.longitude }}°, 纬度: {{ parsedData.latitude }}°
              </el-descriptions-item>
              <el-descriptions-item label="发生时间">
                {{ parsedData.timeStr }}
              </el-descriptions-item>
              <el-descriptions-item label="数据来源">
                {{ parsedData.sourceType }}
              </el-descriptions-item>
              <el-descriptions-item label="载体类型">
                {{ parsedData.carrierType }}
              </el-descriptions-item>
              <el-descriptions-item label="灾害类别" :span="2">
                {{ parsedData.disasterCategory }}
              </el-descriptions-item>
            </el-descriptions>

            <el-alert
              v-if="parsedData.warning"
              :title="parsedData.warning"
              type="warning"
              :closable="false"
              show-icon
              style="margin-top: 15px;"
            />
          </div>

          <el-empty v-else description="请输入36位编码进行解析" />
        </div>
      </el-tab-pane>

      <!-- 编码规则 -->
      <el-tab-pane label="编码规则" name="rules">
        <div class="card-panel">
          <h3 class="section-title">
            <el-icon><Memo /></el-icon>
            36位一体化编码规范
          </h3>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="编码总长度">
              36位数字
            </el-descriptions-item>
            <el-descriptions-item label="编码结构">
              地理信息码(12位) + 时间码(14位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
            </el-descriptions-item>
          </el-descriptions>

          <el-divider />

          <div class="rules-content">
            <el-collapse v-model="activeRules" accordion>
              <el-collapse-item title="1. 地理信息码 (12位)" name="1">
                <div class="rule-detail">
                  <p><strong>结构：</strong>经度(6位) + 纬度(6位)</p>
                  <p><strong>编码方式：</strong>取经纬度整数部分和小数点后2位，不足补0</p>
                  <p><strong>示例：</strong></p>
                  <ul>
                    <li>经度 101.12 → 101120</li>
                    <li>纬度 37.65 → 037650</li>
                    <li>地理信息码 → 101120037650</li>
                  </ul>
                </div>
              </el-collapse-item>

              <el-collapse-item title="2. 时间码 (14位)" name="2">
                <div class="rule-detail">
                  <p><strong>结构：</strong>YYYYMMDDHHmmss</p>
                  <p><strong>示例：</strong></p>
                  <ul>
                    <li>2021年5月22日 02:04:00 → 20210522020400</li>
                  </ul>
                </div>
              </el-collapse-item>

              <el-collapse-item title="3. 来源码 (3位)" name="3">
                <div class="rule-detail">
                  <p><strong>编码对照表：</strong></p>
                  <el-table :data="sourceCodeTable" border size="small">
                    <el-table-column prop="code" label="编码" width="100" />
                    <el-table-column prop="name" label="来源名称" />
                  </el-table>
                </div>
              </el-collapse-item>

              <el-collapse-item title="4. 载体码 (1位)" name="4">
                <div class="rule-detail">
                  <p><strong>编码对照表：</strong></p>
                  <el-table :data="carrierCodeTable" border size="small">
                    <el-table-column prop="code" label="编码" width="100" />
                    <el-table-column prop="name" label="载体类型" />
                  </el-table>
                </div>
              </el-collapse-item>

              <el-collapse-item title="5. 灾情码 (6位)" name="5">
                <div class="rule-detail">
                  <p><strong>编码对照表：</strong></p>
                  <el-table :data="disasterCodeTable" border size="small">
                    <el-table-column prop="code" label="编码" width="100" />
                    <el-table-column prop="name" label="灾情类别" />
                  </el-table>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>

          <el-divider />

          <div class="example-section">
            <h4>完整示例</h4>
            <div class="example-code">
              <code class="full-example">632626200206 20210522020400 101 0 302001</code>
            </div>
            <el-descriptions :column="1" border size="small" class="example-desc">
              <el-descriptions-item label="地理信息码">632626200206 (经度63.26, 纬度26.20)</el-descriptions-item>
              <el-descriptions-item label="时间码">20210522020400 (2021-05-22 02:04:00)</el-descriptions-item>
              <el-descriptions-item label="来源码">101 (后方指挥部)</el-descriptions-item>
              <el-descriptions-item label="载体码">0 (文字)</el-descriptions-item>
              <el-descriptions-item label="灾情码">302001 (人员伤亡)</el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'

const activeTab = ref('generator')
const activeRules = ref('1')

// 生成器表单
const generatorForm = ref({
  longitude: null,
  latitude: null,
  earthquakeTime: '',
  sourceType: '',
  carrierType: '',
  disasterCategory: ''
})

// 解析器输入
const parserInput = ref('')

// 编码映射表
const sourceCodeMap = {
  '后方指挥部': '101',
  '现场指挥部': '102',
  '业务报送': '201',
  '泛在感知': '202',
  '舆情感知': '203',
  '其他': '999'
}

const carrierCodeMap = {
  '文字': '0',
  '图像': '1',
  '音频': '2',
  '视频': '3'
}

const disasterCodeMap = {
  '震情': '301001',
  '人员伤亡': '302001',
  '房屋破坏': '303001',
  '生命线工程': '304001',
  '次生灾害': '305001',
  '其他': '399999'
}

// 反向映射
const sourceCodeReverseMap = Object.fromEntries(
  Object.entries(sourceCodeMap).map(([k, v]) => [v, k])
)

const carrierCodeReverseMap = Object.fromEntries(
  Object.entries(carrierCodeMap).map(([k, v]) => [v, k])
)

const disasterCodeReverseMap = Object.fromEntries(
  Object.entries(disasterCodeMap).map(([k, v]) => [v, k])
)

// 规则表格数据
const sourceCodeTable = Object.entries(sourceCodeMap).map(([name, code]) => ({
  code,
  name
}))

const carrierCodeTable = Object.entries(carrierCodeMap).map(([name, code]) => ({
  code,
  name
}))

const disasterCodeTable = Object.entries(disasterCodeMap).map(([name, code]) => ({
  code,
  name
}))

// 生成编码结构
const codeStructure = computed(() => {
  const form = generatorForm.value

  // 地理信息码
  let geoCode = '------------'
  if (form.longitude !== null && form.latitude !== null) {
    const lng = String(Math.abs(form.longitude).toFixed(2)).replace('.', '').padStart(6, '0').substring(0, 6)
    const lat = String(Math.abs(form.latitude).toFixed(2)).replace('.', '').padStart(6, '0').substring(0, 6)
    geoCode = lng + lat
  }

  // 时间码
  let timeCode = '--------------'
  if (form.earthquakeTime) {
    timeCode = form.earthquakeTime.replace(/[-:\s]/g, '').substring(0, 14)
  }

  // 来源码
  const sourceCode = form.sourceType ? sourceCodeMap[form.sourceType] : '---'

  // 载体码
  const carrierCode = form.carrierType ? carrierCodeMap[form.carrierType] : '-'

  // 灾情码
  const disasterCode = form.disasterCategory ? disasterCodeMap[form.disasterCategory] : '------'

  return {
    geo: geoCode,
    time: timeCode,
    source: sourceCode,
    carrier: carrierCode,
    disaster: disasterCode
  }
})

// 生成的完整编码
const generatedCode = computed(() => {
  const { geo, time, source, carrier, disaster } = codeStructure.value

  // 检查是否所有部分都已填充
  if (geo.includes('-') || time.includes('-') ||
      source.includes('-') || carrier === '-' || disaster.includes('-')) {
    return ''
  }

  return geo + time + source + carrier + disaster
})

// 更新生成的编码
const updateGeneratedCode = () => {
  // 触发计算属性更新
}

// 复制编码
const copyCode = () => {
  if (!generatedCode.value) {
    ElMessage.warning('请先配置完整的编码参数')
    return
  }

  navigator.clipboard.writeText(generatedCode.value).then(() => {
    ElMessage.success('编码已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 解析的数据
const parsedData = ref(null)

// 解析编码
const parseCode = () => {
  const code = parserInput.value.trim()

  if (!code) {
    parsedData.value = null
    return
  }

  if (code.length !== 36) {
    parsedData.value = {
      warning: '编码长度必须为36位'
    }
    return
  }

  try {
    // 解析各部分
    const geoCode = code.substring(0, 12)
    const timeCode = code.substring(12, 26)
    const sourceCode = code.substring(26, 29)
    const carrierCode = code.substring(29, 30)
    const disasterCode = code.substring(30, 36)

    // 解析地理信息
    const lngStr = geoCode.substring(0, 6)
    const latStr = geoCode.substring(6, 12)
    const longitude = (parseInt(lngStr.substring(0, 3)) + parseInt(lngStr.substring(3, 6)) / 100).toFixed(2)
    const latitude = (parseInt(latStr.substring(0, 2)) + parseInt(latStr.substring(2, 6)) / 100).toFixed(2)

    // 解析时间
    const year = timeCode.substring(0, 4)
    const month = timeCode.substring(4, 6)
    const day = timeCode.substring(6, 8)
    const hour = timeCode.substring(8, 10)
    const minute = timeCode.substring(10, 12)
    const second = timeCode.substring(12, 14)
    const timeStr = `${year}-${month}-${day} ${hour}:${minute}:${second}`

    // 解析来源、载体、灾情
    const sourceType = sourceCodeReverseMap[sourceCode] || '未知'
    const carrierType = carrierCodeReverseMap[carrierCode] || '未知'
    const disasterCategory = disasterCodeReverseMap[disasterCode] || '未知'

    parsedData.value = {
      geoCode,
      timeCode,
      sourceCode,
      carrierCode,
      disasterCode,
      longitude,
      latitude,
      timeStr,
      sourceType,
      carrierType,
      disasterCategory
    }
  } catch (error) {
    parsedData.value = {
      warning: '编码格式错误，无法解析'
    }
  }
}
</script>

<style lang="scss" scoped>
.encoding {
  .subtitle {
    color: #909399;
    margin-top: 5px;
  }

  .encoding-tabs {
    margin-top: 20px;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #333;
  }

  .generator-form {
    margin-bottom: 20px;
  }

  .code-display {
    .code-structure {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
      flex-wrap: wrap;

      .code-part {
        flex: 1;
        min-width: 120px;
        padding: 15px;
        border-radius: 8px;
        text-align: center;

        .code-value {
          font-family: 'Courier New', monospace;
          font-size: 18px;
          font-weight: 600;
          margin-bottom: 8px;
          color: #fff;
          letter-spacing: 2px;
        }

        .code-label {
          font-size: 12px;
          color: rgba(255, 255, 255, 0.9);
        }

        &.geo {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        &.time {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        &.source {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &.carrier {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }

        &.disaster {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }
      }
    }

    .full-code {
      margin-bottom: 20px;
    }

    .code-info {
      margin-top: 20px;
    }
  }

  .parser-result {
    margin-top: 20px;

    .parsed-details {
      margin-top: 20px;
    }
  }

  .rules-content {
    margin: 20px 0;

    .rule-detail {
      padding: 10px;

      p {
        margin: 10px 0;
      }

      ul {
        margin: 10px 0;
        padding-left: 20px;

        li {
          margin: 5px 0;
        }
      }
    }
  }

  .example-section {
    margin-top: 20px;

    h4 {
      margin-bottom: 15px;
      color: #333;
    }

    .example-code {
      background: #f5f7fa;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 15px;

      .full-example {
        font-family: 'Courier New', monospace;
        font-size: 18px;
        font-weight: 600;
        color: #409eff;
      }
    }

    .example-desc {
      margin-top: 15px;
    }
  }
}
</style>
