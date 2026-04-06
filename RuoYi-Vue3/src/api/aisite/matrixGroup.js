import request from '@/utils/request'

export function listMatrixGroup(query) {
  return request({
    url: '/aisite/matrix/list',
    method: 'get',
    params: query
  })
}

export function getMatrixGroup(id) {
  return request({
    url: '/aisite/matrix/' + id,
    method: 'get'
  })
}

export function addMatrixGroup(data) {
  return request({
    url: '/aisite/matrix',
    method: 'post',
    data: data
  })
}

export function updateMatrixGroup(data) {
  return request({
    url: '/aisite/matrix',
    method: 'put',
    data: data
  })
}

export function delMatrixGroup(ids) {
  return request({
    url: '/aisite/matrix/' + ids,
    method: 'delete'
  })
}
