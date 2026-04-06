import request from '@/utils/request'

// 查询存储配置列表
export function listStorage(query) {
  return request({
    url: '/gamebox/storage/list',
    method: 'get',
    params: query
  })
}

// 查询存储配置详细
export function getStorage(id) {
  return request({
    url: '/gamebox/storage/' + id,
    method: 'get'
  })
}

// 新增存储配置
export function addStorage(data) {
  return request({
    url: '/gamebox/storage',
    method: 'post',
    data: data
  })
}

// 修改存储配置
export function updateStorage(data) {
  return request({
    url: '/gamebox/storage',
    method: 'put',
    data: data
  })
}

// 删除存储配置
export function delStorage(id) {
  return request({
    url: '/gamebox/storage/' + id,
    method: 'delete'
  })
}

// 验证存储配置
export function validateStorage(data) {
  return request({
    url: '/gamebox/storage/validate',
    method: 'post',
    data: data
  })
}

// ==================== 排除管理 ====================

// 排除默认存储配置
export function excludeDefaultStorage(data) {
  return request({
    url: '/gamebox/siterelation/storageConfig/exclude',
    method: 'post',
    data: data
  })
}

// 恢复被排除的默认存储配置
export function restoreDefaultStorage(siteId, storageId) {
  return request({
    url: `/gamebox/siterelation/storageConfig/exclude/${siteId}/${storageId}`,
    method: 'delete'
  })
}

