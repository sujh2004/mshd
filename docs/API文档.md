# MSHD API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **响应格式**: JSON

## 通用响应格式

所有接口返回统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1702800000000
}
```

**响应字段说明**：
- `code`: 状态码，200表示成功，其他表示失败
- `message`: 提示信息
- `data`: 返回的数据
- `timestamp`: 时间戳

## 状态码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 1. 灾情数据管理

### 1.1 创建灾情数据

**接口**: `POST /disaster/create`

**请求参数**:

```json
{
  "geographicCode": "632626200206",
  "location": "青海省海东市民和回族土族自治县",
  "longitude": 102.8394,
  "latitude": 36.3295,
  "earthquakeTime": "2021-05-22T02:04:00",
  "sourceCode": "101",
  "sourceType": "业务报送",
  "carrierCode": "0",
  "carrierType": "text",
  "disasterCode": "302001",
  "disasterCategory": "3",
  "disasterSubcategory": "02",
  "indicatorCode": "001",
  "content": "砖木房屋一般损坏面积500平方米",
  "ttl": 168
}
```

**请求字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| geographicCode | String | 是 | 地理信息码（12位） |
| location | String | 否 | 地理位置描述 |
| longitude | Double | 否 | 经度 |
| latitude | Double | 否 | 纬度 |
| earthquakeTime | String | 是 | 地震发生时间（ISO 8601格式） |
| sourceCode | String | 是 | 来源码（3位） |
| sourceType | String | 否 | 数据来源类型 |
| carrierCode | String | 是 | 载体码（1位） |
| carrierType | String | 否 | 载体类型 |
| disasterCode | String | 是 | 灾情码（6位） |
| disasterCategory | String | 是 | 灾害大类代码（1位） |
| disasterSubcategory | String | 是 | 灾害子类代码（2位） |
| indicatorCode | String | 是 | 灾情指标代码（3位） |
| content | String | 否 | 灾情数据内容 |
| ttl | Integer | 否 | 数据有效期（小时） |

**响应示例**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "encodedId": "63262620020620210522020400101030200",
    "earthquakeCode": "63262620020620210522020400",
    "geographicCode": "632626200206",
    "location": "青海省海东市民和回族土族自治县",
    "longitude": 102.8394,
    "latitude": 36.3295,
    "timeCode": "20210522020400",
    "earthquakeTime": "2021-05-22T02:04:00",
    "sourceCode": "101",
    "sourceType": "业务报送",
    "carrierCode": "0",
    "carrierType": "text",
    "disasterCode": "302001",
    "disasterCategory": "3",
    "disasterSubcategory": "02",
    "indicatorCode": "001",
    "content": "砖木房屋一般损坏面积500平方米",
    "status": 1,
    "createTime": "2025-12-17T22:00:00",
    "updateTime": "2025-12-17T22:00:00"
  },
  "timestamp": 1702800000000
}
```

---

### 1.2 查询灾情数据列表

**接口**: `GET /disaster/list`

**响应示例**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "encodedId": "63262620020620210522020400101030200",
      "location": "青海省海东市民和回族土族自治县",
      "longitude": 102.8394,
      "latitude": 36.3295,
      "earthquakeTime": "2021-05-22T02:04:00",
      "sourceType": "业务报送",
      "carrierType": "text",
      "content": "砖木房屋一般损坏面积500平方米",
      "status": 1,
      "createTime": "2025-12-17T22:00:00"
    }
  ],
  "timestamp": 1702800000000
}
```

---

### 1.3 根据ID查询灾情数据

**接口**: `GET /disaster/{id}`

**路径参数**:
- `id`: 灾情数据ID

**响应示例**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "encodedId": "63262620020620210522020400101030200",
    "earthquakeCode": "63262620020620210522020400",
    "geographicCode": "632626200206",
    "location": "青海省海东市民和回族土族自治县",
    "content": "砖木房屋一般损坏面积500平方米"
  },
  "timestamp": 1702800000000
}
```

---

### 1.4 根据编码ID查询

**接口**: `GET /disaster/code/{encodedId}`

**路径参数**:
- `encodedId`: 一体化编码ID（36位）

**响应示例**: 同1.3

---

### 1.5 根据来源查询

**接口**: `GET /disaster/source/{sourceCode}`

**路径参数**:
- `sourceCode`: 来源码（如：101）

**响应示例**: 返回数据数组

---

### 1.6 根据载体类型查询

**接口**: `GET /disaster/carrier/{carrierType}`

**路径参数**:
- `carrierType`: 载体类型（text, image, audio, video）

**响应示例**: 返回数据数组

---

### 1.7 根据灾害类别查询

**接口**: `GET /disaster/category/{category}`

**路径参数**:
- `category`: 灾害大类代码（1-5）

**响应示例**: 返回数据数组

---

### 1.8 根据时间范围查询

**接口**: `GET /disaster/time-range`

**查询参数**:
- `startTime`: 开始时间（格式：yyyy-MM-dd HH:mm:ss）
- `endTime`: 结束时间（格式：yyyy-MM-dd HH:mm:ss）

**示例**:
```
GET /disaster/time-range?startTime=2021-05-01 00:00:00&endTime=2021-05-31 23:59:59
```

**响应示例**: 返回数据数组

---

### 1.9 根据地理位置范围查询

**接口**: `GET /disaster/location-range`

**查询参数**:
- `minLng`: 最小经度
- `maxLng`: 最大经度
- `minLat`: 最小纬度
- `maxLat`: 最大纬度

**示例**:
```
GET /disaster/location-range?minLng=100&maxLng=105&minLat=35&maxLat=40
```

**响应示例**: 返回数据数组

---

### 1.10 删除灾情数据

**接口**: `DELETE /disaster/{id}`

**路径参数**:
- `id`: 灾情数据ID

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1702800000000
}
```

---

## 2. 统计分析

### 2.1 按类别统计

**接口**: `GET /disaster/stats/category`

**响应示例**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "1": 120,
    "2": 200,
    "3": 150,
    "4": 80,
    "5": 70
  },
  "timestamp": 1702800000000
}
```

**数据说明**:
- 键：灾害大类代码
- 值：该类别的数据数量

---

### 2.2 按来源统计

**接口**: `GET /disaster/stats/source`

**响应示例**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "100": 335,
    "101": 310,
    "200": 234,
    "201": 135
  },
  "timestamp": 1702800000000
}
```

---

## 3. 编码解析

### 3.1 解析编码ID

**接口**: `GET /disaster/parse/{encodedId}`

**路径参数**:
- `encodedId`: 一体化编码ID（36位）

**响应示例**:

```json
{
  "code": 200,
  "message": "解析成功",
  "data": {
    "earthquakeCode": "63262620020620210522020400",
    "geographicCode": "632626200206",
    "timeCode": "20210522020400",
    "sourceCode": "101",
    "carrierCode": "0",
    "disasterCode": "302001",
    "disasterCategory": "3",
    "disasterSubcategory": "02",
    "indicatorCode": "001"
  },
  "timestamp": 1702800000000
}
```

---

## 4. 数据维护

### 4.1 清理过期数据

**接口**: `DELETE /disaster/cleanup`

**查询参数**:
- `hours`: 过期时间（小时），默认72

**示例**:
```
DELETE /disaster/cleanup?hours=168
```

**响应示例**:

```json
{
  "code": 200,
  "message": "清理完成",
  "data": 15,
  "timestamp": 1702800000000
}
```

**数据说明**: 返回清理的数据条数

---

## 5. 数据源管理

### 5.1 查询数据源列表

**接口**: `GET /datasource/list`

**响应示例**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "sourceName": "前方地震应急指挥部",
      "sourceCode": "100",
      "sourceType": "business",
      "sourceUrl": "http://localhost:8080/api/data/source1",
      "protocol": "HTTP",
      "dataFormat": "JSON",
      "enabled": true,
      "syncStatus": 2,
      "lastSyncTime": "2025-12-17T22:00:00"
    }
  ],
  "timestamp": 1702800000000
}
```

---

## 编码规范参考

### 地理信息码（12位）
- 省份代码（2位）
- 城市代码（2位）
- 区县代码（2位）
- 乡镇代码（3位）
- 村/社区代码（3位）

### 来源码（3位）
- 业务报送数据：1XX
  - 100: 前方地震应急指挥部
  - 101: 后方地震应急指挥部
  - 120: 应急指挥技术系统
- 泛在感知数据：2XX
  - 200: 互联网感知
  - 201: 通信网感知
  - 202: 舆情网感知
- 其他数据：3XX
  - 300: 其他来源

### 载体码（1位）
- 0: 文字
- 1: 图像
- 2: 音频
- 3: 视频
- 4: 其他

### 灾情码（6位）
**灾害大类（1位）**:
- 1: 震情
- 2: 人员伤亡及失踪
- 3: 房屋破坏
- 4: 生命线工程灾情
- 5: 次生灾害

**灾害子类（2位）**: 根据大类确定

**灾情指标（3位）**:
- 001: 受灾人数/设施数/损失
- 002: 受灾范围
- 003: 受灾程度

---

## 错误处理

### 常见错误码

| 错误码 | 错误信息 | 说明 |
|-------|---------|------|
| 400 | Invalid encoded ID | 编码ID格式错误 |
| 404 | Data not found | 数据不存在 |
| 500 | Database error | 数据库错误 |

### 错误响应示例

```json
{
  "code": 400,
  "message": "Invalid encoded ID: 格式必须为36位数字",
  "data": null,
  "timestamp": 1702800000000
}
```

---

## 使用示例

### JavaScript (Axios)

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 15000
})

// 创建灾情数据
async function createDisasterData() {
  const data = {
    geographicCode: '632626200206',
    location: '青海省海东市民和回族土族自治县',
    longitude: 102.8394,
    latitude: 36.3295,
    earthquakeTime: '2021-05-22T02:04:00',
    sourceCode: '101',
    carrierCode: '0',
    disasterCode: '302001',
    content: '砖木房屋一般损坏面积500平方米'
  }

  try {
    const response = await api.post('/disaster/create', data)
    console.log(response.data)
  } catch (error) {
    console.error(error)
  }
}

// 查询数据列表
async function getDisasterList() {
  try {
    const response = await api.get('/disaster/list')
    console.log(response.data)
  } catch (error) {
    console.error(error)
  }
}
```

### Python (Requests)

```python
import requests

BASE_URL = 'http://localhost:8080/api'

# 创建灾情数据
def create_disaster_data():
    data = {
        'geographicCode': '632626200206',
        'location': '青海省海东市民和回族土族自治县',
        'longitude': 102.8394,
        'latitude': 36.3295,
        'earthquakeTime': '2021-05-22T02:04:00',
        'sourceCode': '101',
        'carrierCode': '0',
        'disasterCode': '302001',
        'content': '砖木房屋一般损坏面积500平方米'
    }

    response = requests.post(f'{BASE_URL}/disaster/create', json=data)
    print(response.json())

# 查询数据列表
def get_disaster_list():
    response = requests.get(f'{BASE_URL}/disaster/list')
    print(response.json())
```

### cURL

```bash
# 创建灾情数据
curl -X POST http://localhost:8080/api/disaster/create \
  -H "Content-Type: application/json" \
  -d '{
    "geographicCode": "632626200206",
    "location": "青海省海东市民和回族土族自治县",
    "longitude": 102.8394,
    "latitude": 36.3295,
    "earthquakeTime": "2021-05-22T02:04:00",
    "sourceCode": "101",
    "carrierCode": "0",
    "disasterCode": "302001",
    "content": "砖木房屋一般损坏面积500平方米"
  }'

# 查询数据列表
curl http://localhost:8080/api/disaster/list

# 根据ID查询
curl http://localhost:8080/api/disaster/1

# 解析编码
curl http://localhost:8080/api/disaster/parse/63262620020620210522020400101030200
```

---

## 测试工具推荐

- **Postman**: 强大的API测试工具
- **Insomnia**: 轻量级REST客户端
- **cURL**: 命令行HTTP客户端
- **Swagger UI**: 在线API文档和测试（即将提供）

---

## 版本历史

### v1.0.0 (2025-12-17)
- 初始版本发布
- 实现基础CRUD接口
- 实现编码解析功能
- 实现统计分析接口

---

**更新时间**: 2025-12-17
**维护者**: MSHD团队
