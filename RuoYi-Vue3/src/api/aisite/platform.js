import request from '@/utils/request'

export function listPlatform(query) {
  return request({ url: '/aisite/platforms/list', method: 'get', params: query })
}

export function listAllPlatform(query) {
  return request({ url: '/aisite/platforms/all', method: 'get', params: query })
}

export function getPlatform(id) {
  return request({ url: '/aisite/platforms/' + id, method: 'get' })
}

export function addPlatform(data) {
  return request({ url: '/aisite/platforms', method: 'post', data })
}

export function updatePlatform(data) {
  return request({ url: '/aisite/platforms', method: 'put', data })
}

export function delPlatform(ids) {
  return request({ url: '/aisite/platforms/' + ids, method: 'delete' })
}
