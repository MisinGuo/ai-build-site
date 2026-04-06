import request from '@/utils/request'

// 查询短剧列表
export function listDrama(query) {
  return request({
    url: '/gamebox/drama/list',
    method: 'get',
    params: query
  })
}

// 查询短剧详细
export function getDrama(id) {
  return request({
    url: '/gamebox/drama/' + id,
    method: 'get'
  })
}

// 新增短剧
export function addDrama(data) {
  return request({
    url: '/gamebox/drama',
    method: 'post',
    data: data
  })
}

// 修改短剧
export function updateDrama(data) {
  return request({
    url: '/gamebox/drama',
    method: 'put',
    data: data
  })
}

// 删除短剧
export function delDrama(id) {
  return request({
    url: '/gamebox/drama/' + id,
    method: 'delete'
  })
}
