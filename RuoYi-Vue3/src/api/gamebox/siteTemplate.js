import request from '@/utils/request'

// 查询模板列表（管理端）
export function listSiteTemplates(query) {
  return request({
    url: '/gamebox/site-templates/list',
    method: 'get',
    params: query
  })
}

// 获取已启用模板列表（向导用，不含敏感字段）
export function listEnabledTemplates() {
  return request({
    url: '/gamebox/site-templates/enabled',
    method: 'get'
  })
}

// 获取模板详情
export function getSiteTemplate(id) {
  return request({
    url: '/gamebox/site-templates/' + id,
    method: 'get'
  })
}

// 新增模板
export function addSiteTemplate(data) {
  return request({
    url: '/gamebox/site-templates',
    method: 'post',
    data: data
  })
}

// 修改模板
export function updateSiteTemplate(data) {
  return request({
    url: '/gamebox/site-templates',
    method: 'put',
    data: data
  })
}

// 删除模板
export function delSiteTemplate(ids) {
  return request({
    url: '/gamebox/site-templates/' + ids,
    method: 'delete'
  })
}
