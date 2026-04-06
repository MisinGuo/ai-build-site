import request from '@/utils/request'

export function listAtomicTool(query) {
  return request({ url: '/aisite/atomicTools/list', method: 'get', params: query })
}

export function listAllAtomicTool(query) {
  return request({ url: '/aisite/atomicTools/all', method: 'get', params: query })
}

export function getAtomicTool(id) {
  return request({ url: '/aisite/atomicTools/' + id, method: 'get' })
}

export function addAtomicTool(data) {
  return request({ url: '/aisite/atomicTools', method: 'post', data })
}

export function updateAtomicTool(data) {
  return request({ url: '/aisite/atomicTools', method: 'put', data })
}

export function delAtomicTool(ids) {
  return request({ url: '/aisite/atomicTools/' + ids, method: 'delete' })
}
