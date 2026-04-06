import request from '@/utils/request'

export function listCategory(query) {
  return request({ url: '/aisite/categories/list', method: 'get', params: query })
}

export function getCategory(id) {
  return request({ url: '/aisite/categories/' + id, method: 'get' })
}

export function addCategory(data) {
  return request({ url: '/aisite/categories', method: 'post', data })
}

export function updateCategory(data) {
  return request({ url: '/aisite/categories', method: 'put', data })
}

export function delCategory(id) {
  return request({ url: '/aisite/categories/' + id, method: 'delete' })
}
