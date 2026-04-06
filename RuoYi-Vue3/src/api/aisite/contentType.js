import request from '@/utils/request'

export function listContentType(query) {
  return request({ url: '/aisite/content-types/list', method: 'get', params: query })
}

export function getContentType(id) {
  return request({ url: '/aisite/content-types/' + id, method: 'get' })
}

export function addContentType(data) {
  return request({ url: '/aisite/content-types', method: 'post', data })
}

export function updateContentType(data) {
  return request({ url: '/aisite/content-types', method: 'put', data })
}

export function delContentType(id) {
  return request({ url: '/aisite/content-types/' + id, method: 'delete' })
}
