import request from '@/utils/request'

export function listSeoMetrics(query) {
  return request({
    url: '/aisite/seo/list',
    method: 'get',
    params: query
  })
}

export function getSeoMetricsBySite(siteId) {
  return request({
    url: '/aisite/seo/site/' + siteId,
    method: 'get'
  })
}

export function addSeoMetrics(data) {
  return request({
    url: '/aisite/seo',
    method: 'post',
    data
  })
}

export function delSeoMetrics(id) {
  return request({
    url: '/aisite/seo/' + id,
    method: 'delete'
  })
}
