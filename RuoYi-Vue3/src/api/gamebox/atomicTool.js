import request from '@/utils/request'

// 查询原子工具列表
export function listAtomicTool(query) {
  return request({
    url: '/gamebox/atomicTool/list',
    method: 'get',
    params: query
  })
}

// 查询原子工具详细
export function getAtomicTool(id) {
  return request({
    url: '/gamebox/atomicTool/' + id,
    method: 'get'
  })
}

// 新增原子工具
export function addAtomicTool(data) {
  return request({
    url: '/gamebox/atomicTool',
    method: 'post',
    data: data
  })
}

// 修改原子工具
export function updateAtomicTool(data) {
  return request({
    url: '/gamebox/atomicTool',
    method: 'put',
    data: data
  })
}

// 删除原子工具
export function delAtomicTool(id) {
  return request({
    url: '/gamebox/atomicTool/' + id,
    method: 'delete'
  })
}

// 获取所有可用的系统工具执行器列表
export function getExecutors() {
  return request({
    url: '/gamebox/atomicTool/executors',
    method: 'get'
  })
}

// 获取工具定义（包含输入输出参数）
export function getToolDefinition(toolCode) {
  return request({
    url: '/gamebox/atomicTool/definition/' + toolCode,
    method: 'get'
  })
}

// 执行工具（通过ID，支持所有工具）
export function executeAtomicToolById(id, params) {
  return request({
    url: '/gamebox/atomicTool/executeById/' + id,
    method: 'post',
    data: params
  })
}

