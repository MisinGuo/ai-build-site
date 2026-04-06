import request from '@/utils/request'

export function listSite(query) {
  return request({ url: '/aisite/sites/list', method: 'get', params: query })
}

export function getSite(id) {
  return request({ url: '/aisite/sites/' + id, method: 'get' })
}

export function addSite(data) {
  return request({ url: '/aisite/sites', method: 'post', data })
}

export function updateSite(data) {
  return request({ url: '/aisite/sites', method: 'put', data })
}

export function delSite(id) {
  return request({ url: '/aisite/sites/' + id, method: 'delete' })
}
