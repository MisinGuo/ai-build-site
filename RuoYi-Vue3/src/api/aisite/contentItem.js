import request from '@/utils/request'

export function listContentItem(query) {
  return request({ url: '/aisite/content-items/list', method: 'get', params: query })
}

export function getContentItem(id) {
  return request({ url: '/aisite/content-items/' + id, method: 'get' })
}

export function addContentItem(data) {
  return request({ url: '/aisite/content-items', method: 'post', data })
}

export function updateContentItem(data) {
  return request({ url: '/aisite/content-items', method: 'put', data })
}

export function delContentItem(id) {
  return request({ url: '/aisite/content-items/' + id, method: 'delete' })
}
