import request from '@/utils/request'

export function listWorkflow(query) {
  return request({ url: '/aisite/workflows/list', method: 'get', params: query })
}

export function listAllWorkflow(query) {
  return request({ url: '/aisite/workflows/all', method: 'get', params: query })
}

export function getWorkflow(id) {
  return request({ url: '/aisite/workflows/' + id, method: 'get' })
}

export function getWorkflowByCode(code) {
  return request({ url: '/aisite/workflows/code/' + code, method: 'get' })
}

export function addWorkflow(data) {
  return request({ url: '/aisite/workflows', method: 'post', data })
}

export function updateWorkflow(data) {
  return request({ url: '/aisite/workflows', method: 'put', data })
}

export function delWorkflow(ids) {
  return request({ url: '/aisite/workflows/' + ids, method: 'delete' })
}
