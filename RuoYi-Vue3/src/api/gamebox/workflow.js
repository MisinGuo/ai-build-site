import request from '@/utils/request'

// 查询工作流列表
export function listWorkflow(query) {
  return request({
    url: '/gamebox/workflow/list',
    method: 'get',
    params: query
  })
}

// 查询工作流详细
export function getWorkflow(id) {
  return request({
    url: '/gamebox/workflow/' + id,
    method: 'get'
  })
}

// 根据代码查询工作流
export function getWorkflowByCode(workflowCode) {
  return request({
    url: '/gamebox/workflow/code/' + workflowCode,
    method: 'get'
  })
}

// 新增工作流
export function addWorkflow(data) {
  return request({
    url: '/gamebox/workflow',
    method: 'post',
    data: data
  })
}

// 修改工作流
export function updateWorkflow(data) {
  return request({
    url: '/gamebox/workflow',
    method: 'put',
    data: data
  })
}

// 删除工作流
export function delWorkflow(id) {
  return request({
    url: '/gamebox/workflow/' + id,
    method: 'delete'
  })
}

// 执行工作流
export function executeWorkflow(workflowCode, inputs) {
  return request({
    url: '/gamebox/workflow/execute/' + workflowCode,
    method: 'post',
    data: inputs
  })
}

// 异步执行工作流
export function executeWorkflowAsync(workflowCode, inputs) {
  return request({
    url: '/gamebox/workflow/execute/async/' + workflowCode,
    method: 'post',
    data: inputs
  })
}

// 批量执行工作流
export function batchExecuteWorkflow(workflowCode, inputsList) {
  return request({
    url: '/gamebox/workflow/execute/batch/' + workflowCode,
    method: 'post',
    data: inputsList
  })
}

// 获取工作流执行状态
export function getExecutionStatus(executionId) {
  return request({
    url: '/gamebox/workflow/execution/' + executionId,
    method: 'get'
  })
}

// 获取工作流执行进度
export function getExecutionProgress(executionId) {
  return request({
    url: '/gamebox/workflow/execution/' + executionId + '/progress',
    method: 'get'
  })
}

// 取消工作流执行
export function cancelExecution(executionId) {
  return request({
    url: '/gamebox/workflow/execution/' + executionId + '/cancel',
    method: 'post'
  })
}

// 查询工作流执行历史
export function listExecutionHistory(query) {
  return request({
    url: '/gamebox/workflow/execution/history',
    method: 'get',
    params: query
  })
}

// 查询步骤执行记录
export function listStepExecutions(executionId) {
  return request({
    url: '/gamebox/workflow/execution/' + executionId + '/steps',
    method: 'get'
  })
}

// 测试工作流（不保存实际数据）
export function testWorkflow(workflowCode, inputs) {
  return request({
    url: '/gamebox/workflow/test/' + workflowCode,
    method: 'post',
    data: inputs
  })
}

// 验证工作流定义
export function validateWorkflow(definition) {
  return request({
    url: '/gamebox/workflow/validate',
    method: 'post',
    data: definition
  })
}

// 复制工作流
export function copyWorkflow(id) {
  return request({
    url: '/gamebox/workflow/copy/' + id,
    method: 'post'
  })
}

// 获取所有可用的原子工具列表
export function getAvailableTools(siteId) {
  return request({
    url: '/gamebox/workflow/tools/available',
    method: 'get',
    params: { siteId }
  })
}

// 验证步骤参数映射
export function validateStepMapping(data) {
  return request({
    url: '/gamebox/workflow/step/validate-mapping',
    method: 'post',
    data: data
  })
}

// 模拟执行工作流（测试模式）
export function simulateWorkflow(data) {
  return request({
    url: '/gamebox/workflow/simulate',
    method: 'post',
    data: data
  })
}

// 导入工作流（系统级）
export function importWorkflows(data) {
  return request({
    url: '/gamebox/workflow/import',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导入全站工作流
export function importAllWorkflows(data) {
  return request({
    url: '/gamebox/workflow/importAll',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取工作流执行统计（总次数、成功率、平均耗时等）
export function getExecutionStats(workflowCode) {
  return request({
    url: `/gamebox/workflow/execution/stats/${workflowCode}`,
    method: 'get'
  })
}

// 批量获取多个工作流的执行统计
export function batchGetExecutionStats(workflowCodes) {
  return request({
    url: '/gamebox/workflow/execution/stats/batch',
    method: 'post',
    data: workflowCodes
  })
}

// ====================================================================
// 工作流模板
// ====================================================================

// 查询模板列表
export function listWorkflowTemplates(query) {
  return request({
    url: '/gamebox/workflow/template/list',
    method: 'get',
    params: query
  })
}

// 获取模板详情（含 definition）
export function getWorkflowTemplate(id) {
  return request({
    url: `/gamebox/workflow/template/${id}`,
    method: 'get'
  })
}

// 从模板创建工作流
export function createWorkflowFromTemplate(templateId, data) {
  return request({
    url: `/gamebox/workflow/template/${templateId}/create-workflow`,
    method: 'post',
    data: data
  })
}

// 新增模板
export function addWorkflowTemplate(data) {
  return request({
    url: '/gamebox/workflow/template',
    method: 'post',
    data: data
  })
}

// 更新模板
export function updateWorkflowTemplate(data) {
  return request({
    url: '/gamebox/workflow/template',
    method: 'put',
    data: data
  })
}

// 删除模板
export function deleteWorkflowTemplate(id) {
  return request({
    url: `/gamebox/workflow/template/${id}`,
    method: 'delete'
  })
}

