import request from '@/utils/request'

/**
 * 灾情数据API
 */

// 创建灾情数据
export function createDisasterData(data) {
  return request({
    url: '/disaster/create',
    method: 'post',
    data
  })
}

// 更新灾情数据
export function updateDisasterData(id, data) {
  return request({
    url: `/disaster/update/${id}`,
    method: 'put',
    data
  })
}

// 查询灾情数据列表
export function getDisasterDataList() {
  return request({
    url: '/disaster/list',
    method: 'get'
  })
}

// 根据ID查询灾情数据
export function getDisasterDataById(id) {
  return request({
    url: `/disaster/${id}`,
    method: 'get'
  })
}

// 根据编码ID查询灾情数据
export function getDisasterDataByCode(encodedId) {
  return request({
    url: `/disaster/code/${encodedId}`,
    method: 'get'
  })
}

// 根据来源查询
export function getDisasterDataBySource(sourceCode) {
  return request({
    url: `/disaster/source/${sourceCode}`,
    method: 'get'
  })
}

// 根据载体类型查询
export function getDisasterDataByCarrier(carrierType) {
  return request({
    url: `/disaster/carrier/${carrierType}`,
    method: 'get'
  })
}

// 根据灾害类别查询
export function getDisasterDataByCategory(category) {
  return request({
    url: `/disaster/category/${category}`,
    method: 'get'
  })
}

// 根据时间范围查询
export function getDisasterDataByTimeRange(startTime, endTime) {
  return request({
    url: '/disaster/time-range',
    method: 'get',
    params: { startTime, endTime }
  })
}

// 根据地理位置范围查询
export function getDisasterDataByLocationRange(minLng, maxLng, minLat, maxLat) {
  return request({
    url: '/disaster/location-range',
    method: 'get',
    params: { minLng, maxLng, minLat, maxLat }
  })
}

// 统计各类型数据数量
export function getStatsByCategory() {
  return request({
    url: '/disaster/stats/category',
    method: 'get'
  })
}

// 统计各来源数据数量
export function getStatsBySource() {
  return request({
    url: '/disaster/stats/source',
    method: 'get'
  })
}

// 解析编码ID
export function parseEncodedId(encodedId) {
  return request({
    url: `/disaster/parse/${encodedId}`,
    method: 'get'
  })
}

// 删除过期数据
export function cleanupExpiredData(hours = 72) {
  return request({
    url: '/disaster/cleanup',
    method: 'delete',
    params: { hours }
  })
}

// 删除灾情数据
export function deleteDisasterData(id) {
  return request({
    url: `/disaster/${id}`,
    method: 'delete'
  })
}
