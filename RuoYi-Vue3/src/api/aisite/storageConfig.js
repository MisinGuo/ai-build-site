import request from '@/utils/request'

// 查询存储配置列表
export function listStorageConfig(query) {
  return request({ url: '/aisite/storageConfigs/list', method: 'get', params: query })
}

// 查询全部存储配置（不分页）
export function listAllStorageConfig(query) {
  return request({ url: '/aisite/storageConfigs/all', method: 'get', params: query })
}

// 获取存储配置详情
export function getStorageConfig(id) {
  return request({ url: `/aisite/storageConfigs/${id}`, method: 'get' })
}

// 新增存储配置
export function addStorageConfig(data) {
  return request({ url: '/aisite/storageConfigs', method: 'post', data })
}

// 修改存储配置
export function updateStorageConfig(data) {
  return request({ url: '/aisite/storageConfigs', method: 'put', data })
}

// 删除存储配置
export function delStorageConfig(ids) {
  return request({ url: `/aisite/storageConfigs/${ids}`, method: 'delete' })
}
