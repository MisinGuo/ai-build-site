import request from '@/utils/request'

// 查询短剧供应商列表
export function listVendor(query) {
  return request({
    url: '/gamebox/vendor/list',
    method: 'get',
    params: query
  })
}

// 查询短剧供应商详细
export function getVendor(id) {
  return request({
    url: '/gamebox/vendor/' + id,
    method: 'get'
  })
}

// 新增短剧供应商
export function addVendor(data) {
  return request({
    url: '/gamebox/vendor',
    method: 'post',
    data: data
  })
}

// 修改短剧供应商
export function updateVendor(data) {
  return request({
    url: '/gamebox/vendor',
    method: 'put',
    data: data
  })
}

// 删除短剧供应商
export function delVendor(id) {
  return request({
    url: '/gamebox/vendor/' + id,
    method: 'delete'
  })
}
