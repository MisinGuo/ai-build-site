import request from '@/utils/request'

// 查询AI平台配置列表
// 用于列表页面：返回所有数据（包括被排除的，会有 isExcluded 标记）
// 支持参数：siteId, queryMode, includeDefault
export function listPlatform(query) {
  return request({
    url: '/gamebox/platform/list',
    method: 'get',
    params: query
  })
}

// 查询可见AI平台列表
// 用于表单下拉框：只返回可见数据（不包括被排除的）
// 支持参数：siteId
export function listVisiblePlatform(query) {
  return request({
    url: '/gamebox/platform/visibleList',
    method: 'get',
    params: query
  })
}

// 查询AI平台配置详细
export function getPlatform(id) {
  return request({
    url: '/gamebox/platform/' + id,
    method: 'get'
  })
}

// 新增AI平台配置
export function addPlatform(data) {
  return request({
    url: '/gamebox/platform',
    method: 'post',
    data: data
  })
}

// 修改AI平台配置
export function updatePlatform(data) {
  return request({
    url: '/gamebox/platform',
    method: 'put',
    data: data
  })
}

// 删除AI平台配置
export function delPlatform(id) {
  return request({
    url: '/gamebox/platform/' + id,
    method: 'delete'
  })
}

// 测试AI平台连接
export function testPlatform(id) {
  return request({
    url: '/gamebox/platform/test/' + id,
    method: 'post'
  })
}

// 设置默认AI平台
export function setDefaultPlatform(id) {
  return request({
    url: '/gamebox/platform/default/' + id,
    method: 'put'
  })
}

// 根据平台类型获取可用模型列表
export function getAvailableModels(platformType, apiKey, baseUrl) {
  return request({
    url: '/gamebox/platform/models/' + platformType,
    method: 'get',
    params: {
      apiKey: apiKey,
      baseUrl: baseUrl
    }
  })
}
