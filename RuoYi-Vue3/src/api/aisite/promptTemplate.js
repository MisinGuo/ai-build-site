import request from '@/utils/request'

// 查询 Prompt 模板列表
export function listPromptTemplate(query) {
  return request({ url: '/aisite/promptTemplates/list', method: 'get', params: query })
}

// 查询全部 Prompt 模板（不分页）
export function listAllPromptTemplate(query) {
  return request({ url: '/aisite/promptTemplates/all', method: 'get', params: query })
}

// 获取 Prompt 模板详情
export function getPromptTemplate(id) {
  return request({ url: `/aisite/promptTemplates/${id}`, method: 'get' })
}

// 根据编码获取 Prompt 模板
export function getPromptTemplateByCode(code) {
  return request({ url: `/aisite/promptTemplates/code/${code}`, method: 'get' })
}

// 新增 Prompt 模板
export function addPromptTemplate(data) {
  return request({ url: '/aisite/promptTemplates', method: 'post', data })
}

// 修改 Prompt 模板
export function updatePromptTemplate(data) {
  return request({ url: '/aisite/promptTemplates', method: 'put', data })
}

// 删除 Prompt 模板
export function delPromptTemplate(ids) {
  return request({ url: `/aisite/promptTemplates/${ids}`, method: 'delete' })
}
