import request from '@/utils/request'

// 查询标题池列表
export function listTitlePool(query) {
  return request({
    url: '/gamebox/title/pool/list',
    method: 'get',
    params: query
  })
}

// 查询标题池详细
export function getTitlePool(id) {
  return request({
    url: '/gamebox/title/pool/' + id,
    method: 'get'
  })
}

// 新增标题
export function addTitlePool(data) {
  return request({
    url: '/gamebox/title/pool',
    method: 'post',
    data: data
  })
}

// 修改标题
export function updateTitlePool(data) {
  return request({
    url: '/gamebox/title/pool',
    method: 'put',
    data: data
  })
}

// 删除标题
export function delTitlePool(id) {
  return request({
    url: '/gamebox/title/pool/' + id,
    method: 'delete'
  })
}

// JSON导入
export function importFromJson(data) {
  return request({
    url: '/gamebox/title/import/json',
    method: 'post',
    data: data
  })
}

// Excel导入
export function importFromExcel(data) {
  return request({
    url: '/gamebox/title/import/excel',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}

// 查询导入历史
export function getImportHistory(query) {
  return request({
    url: '/gamebox/title/import/history',
    method: 'get',
    params: query
  })
}

// 查询导入统计
export function getImportStatistics() {
  return request({
    url: '/gamebox/title/import/statistics',
    method: 'get'
  })
}

// 批量更新使用状态
export function batchUpdateStatus(data) {
  return request({
    url: '/gamebox/title/pool/batch/status',
    method: 'put',
    data: data
  })
}

// 查询批次列表
// 用于列表页面：返回所有数据（包括被排除的，会有 isExcluded 标记）
// 支持参数：siteId, queryMode, includeDefault
export function listBatch(query) {
  return request({
    url: '/gamebox/title/batch/list',
    method: 'get',
    params: query
  })
}

// 查询可见批次列表
// 用于表单下拉框：只返回可见数据（不包括被排除的）
// 支持参数：siteId
export function listVisibleBatch(query) {
  return request({
    url: '/gamebox/title/batch/visibleList',
    method: 'get',
    params: query
  })
}

// 查询批次详细
export function getBatch(id) {
  return request({
    url: '/gamebox/title/batch/' + id,
    method: 'get'
  })
}

// 排除默认批次
export function excludeBatch(data) {
  return request({
    url: '/gamebox/siterelation/batch/exclude',
    method: 'post',
    data: data
  })
}

// 恢复默认批次
export function restoreBatch(siteId, batchId) {
  return request({
    url: `/gamebox/siterelation/batch/exclude/${siteId}/${batchId}`,
    method: 'delete'
  })
}

// 批量管理默认批次的排除网站
export function manageBatchExclusions(data) {
  return request({
    url: '/gamebox/siterelation/batch/manage-exclusions',
    method: 'post',
    data: data
  })
}

// 查询默认批次被哪些网站排除
export function getExcludedSitesByBatch(batchId) {
  return request({
    url: `/gamebox/siterelation/batch/excluded-sites/${batchId}`,
    method: 'get'
  })
}
