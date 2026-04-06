import request from '@/utils/request'

/**
 * 查询功能代理配置列表（全量）
 * @param {Object} query - { featureGroup?, proxyApplicable? }
 */
export function listProxyConfig(query) {
  return request({
    url: '/gamebox/proxy/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取单条代理配置详情
 * @param {number} id
 */
export function getProxyConfig(id) {
  return request({
    url: '/gamebox/proxy/' + id,
    method: 'get'
  })
}

/**
 * 更新单条代理配置
 * @param {Object} data - GbProxyConfig
 */
export function updateProxyConfig(data) {
  return request({
    url: '/gamebox/proxy',
    method: 'put',
    data: data
  })
}

/**
 * 批量更新代理配置
 * @param {Array} configs - GbProxyConfig[]
 */
export function batchUpdateProxyConfig(configs) {
  return request({
    url: '/gamebox/proxy/batch',
    method: 'put',
    data: configs
  })
}

/**
 * 测试指定功能代理的连通性
 * 仅在开发/调试阶段使用，用于验证代理配置确实生效
 * @param {number} id - 代理配置 ID
 * @param {string} [testUrl] - 测试目标地址（默认 https://www.google.com）
 */
export function testProxyConnection(id, testUrl) {
  return request({
    url: '/gamebox/proxy/' + id + '/test-connection',
    method: 'get',
    params: testUrl ? { testUrl } : {}
  })
}
