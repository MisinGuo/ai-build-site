import request from '@/utils/request'

// 查询批次列表
export function listBatch(query) {
  return request({
    url: '/gamebox/title/batch/list',
    method: 'get',
    params: query
  })
}

// 查询批次详细
export function getBatch(id) {
  return request({
    url: '/gamebox/title/batch/' + id,
    method: 'get'
  })
}

// 根据批次号查询批次
export function getBatchByCode(batchCode) {
  return request({
    url: `/gamebox/title/batch/code/${batchCode}`,
    method: 'get'
  })
}

// 新增批次
export function addBatch(data) {
  return request({
    url: '/gamebox/title/batch',
    method: 'post',
    data: data
  })
}

// 修改批次
export function updateBatch(data) {
  return request({
    url: '/gamebox/title/batch',
    method: 'put',
    data: data
  })
}

// 删除批次
export function delBatch(id) {
  return request({
    url: '/gamebox/title/batch/' + id,
    method: 'delete'
  })
}

// 更新批次标题数量
export function updateBatchTitleCount(batchCode) {
  return request({
    url: `/gamebox/title/batch/updateCount/${batchCode}`,
    method: 'put'
  })
}
