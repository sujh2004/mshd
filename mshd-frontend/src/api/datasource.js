import request from '@/utils/request'

/**
 * 数据源管理API
 */

// 创建数据源
export function createDataSource(data) {
  return request({
    url: '/datasource/create',
    method: 'post',
    data
  })
}

// 更新数据源
export function updateDataSource(id, data) {
  return request({
    url: `/datasource/update/${id}`,
    method: 'put',
    data
  })
}

// 根据ID查询数据源
export function getDataSourceById(id) {
  return request({
    url: `/datasource/${id}`,
    method: 'get'
  })
}

// 查询数据源列表
export function getDataSourceList() {
  return request({
    url: '/datasource/list',
    method: 'get'
  })
}

// 根据类型查询
export function getDataSourceByType(sourceType) {
  return request({
    url: `/datasource/type/${sourceType}`,
    method: 'get'
  })
}

// 查询启用的数据源
export function getEnabledDataSources() {
  return request({
    url: '/datasource/enabled',
    method: 'get'
  })
}

// 启用/禁用数据源
export function toggleDataSource(id, enabled) {
  return request({
    url: `/datasource/toggle/${id}`,
    method: 'put',
    params: { enabled }
  })
}

// 删除数据源
export function deleteDataSource(id) {
  return request({
    url: `/datasource/${id}`,
    method: 'delete'
  })
}

// 测试数据源连接
export function testConnection(data) {
  return request({
    url: '/datasource/test',
    method: 'post',
    data
  })
}

// 手动同步数据源
export function syncDataSource(id) {
  return request({
    url: `/datasource/sync/${id}`,
    method: 'post'
  })
}

// 获取数据源统计
export function getDataSourceStats() {
  return request({
    url: '/datasource/stats',
    method: 'get'
  })
}
