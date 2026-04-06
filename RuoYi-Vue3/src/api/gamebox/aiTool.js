import request from '@/utils/request'

// AI工具专用API - 为文章编辑器的AI辅助功能提供轻量级查询接口
// 注意：这里的接口不需要复杂的权限检查和分页，直接返回可用数据

/**
 * 获取可用的AI平台列表（仅启用的）
 * @returns {Promise}
 */
export function getAvailablePlatforms() {
  return request({
    url: '/gamebox/ai/platforms',
    method: 'get'
  })
}

/**
 * 获取可用的内容工具列表（仅启用的）
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function getAvailableTemplates(query) {
  return request({
    url: '/gamebox/ai/templates',
    method: 'get',
    params: query
  })
}

/**
 * 获取模板的变量定义
 * @param {Number} templateId 模板ID
 * @returns {Promise}
 */
export function getTemplateVariables(templateId) {
  return request({
    url: '/gamebox/ai/template/' + templateId + '/variables',
    method: 'get'
  })
}

/**
 * AI内容处理（统一接口）
 * @param {Object} data 处理参数
 * @returns {Promise}
 */
export function processContent(data) {
  return request({
    url: '/gamebox/ai/process',
    method: 'post',
    data: data,
    timeout: 120000 // 2分钟超时
  })
}

/**
 * 渲染模板预览
 * @param {Object} data 包含templateId和variables
 * @returns {Promise}
 */
export function renderTemplate(data) {
  return request({
    url: '/gamebox/ai/template/render',
    method: 'post',
    data: data
  })
}

/**
 * 执行AI工具（通用执行接口）
 * @param {Object} data AI执行参数
 * @param {Number} data.aiPlatformId - AI平台ID
 * @param {String} data.model - 模型名称
 * @param {String} data.systemPrompt - 系统提示词
 * @param {String} data.userPrompt - 用户提示词
 * @param {Number} data.temperature - 温度参数
 * @param {Number} data.maxTokens - 最大token数
 * @returns {Promise}
 */
export function executeAiTool(data) {
  return request({
    url: '/gamebox/ai/execute',
    method: 'post',
    data: data,
    timeout: 120000 // 2分钟超时
  })
}
