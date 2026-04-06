import request from '@/utils/request'

export function listOperationTask(query) {
  return request({
    url: '/aisite/operations/list',
    method: 'get',
    params: query
  })
}

export function getOperationTask(id) {
  return request({
    url: '/aisite/operations/' + id,
    method: 'get'
  })
}

export function addOperationTask(data) {
  return request({
    url: '/aisite/operations',
    method: 'post',
    data: data
  })
}

export function updateOperationTask(data) {
  return request({
    url: '/aisite/operations',
    method: 'put',
    data: data
  })
}

export function delOperationTask(ids) {
  return request({
    url: '/aisite/operations/' + ids,
    method: 'delete'
  })
}
