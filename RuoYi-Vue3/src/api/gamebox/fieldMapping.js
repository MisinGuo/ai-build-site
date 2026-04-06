import request from '@/utils/request'

// 查询字段映射配置列表
export function listFieldMapping(query) {
  return request({
    url: '/gamebox/box-field-mapping/list',
    method: 'get',
    params: query
  })
}

// 根据平台和资源类型查询字段映射配置
export function listByPlatform(platform, resourceType) {
  return request({
    url: '/gamebox/box-field-mapping/listByPlatform',
    method: 'get',
    params: { platform, resourceType }
  })
}

// 查询字段映射配置详细
export function getFieldMapping(id) {
  return request({
    url: '/gamebox/box-field-mapping/' + id,
    method: 'get'
  })
}

// 新增字段映射配置
export function addFieldMapping(data) {
  return request({
    url: '/gamebox/box-field-mapping',
    method: 'post',
    data: data
  })
}

// 修改字段映射配置
export function updateFieldMapping(data) {
  return request({
    url: '/gamebox/box-field-mapping',
    method: 'put',
    data: data
  })
}

// 删除字段映射配置
export function delFieldMapping(ids) {
  return request({
    url: '/gamebox/box-field-mapping/' + ids,
    method: 'delete'
  })
}

// 导出字段映射配置为Excel
export function exportFieldMapping(query) {
  return request({
    url: '/gamebox/box-field-mapping/export',
    method: 'post',
    data: query
  })
}

// 导出字段映射配置为JSON
export function exportFieldMappingJson(platform, resourceType) {
  return request({
    url: '/gamebox/box-field-mapping/exportJson',
    method: 'get',
    params: { platform, resourceType }
  })
}

// 批量导入字段映射配置
export function importFieldMappings(mappingList, updateSupport) {
  return request({
    url: '/gamebox/box-field-mapping/importData',
    method: 'post',
    params: { updateSupport },
    data: mappingList
  })
}

// 根据盒子ID查询字段映射配置
export function listFieldMappingByBoxId(boxId) {
  return request({
    url: '/gamebox/box-field-mapping/box/' + boxId,
    method: 'get'
  })
}

// 保存盒子的字段映射配置（批量）
export function saveFieldMappingsForBox(boxId, mappings) {
  return request({
    url: '/gamebox/box-field-mapping/box/' + boxId,
    method: 'post',
    data: mappings
  })
}

// 获取所有表字段
export function getAllTableFields() {
  return request({
    url: '/gamebox/box-field-mapping/tableFields',
    method: 'get'
  })
}

// 获取指定表字段的所有不同值
export function getFieldDistinctValues(tableName, fieldName) {
  return request({
    url: '/gamebox/box-field-mapping/fieldValues',
    method: 'get',
    params: { tableName, fieldName }
  })
}

// 获取字段schema定义（字段类型等元数据）
export function getFieldSchemas() {
  return request({
    url: '/gamebox/box-field-mapping/fieldSchemas',
    method: 'get'
  })
}

// 批量保存或更新字段映射配置（增量更新）
export function batchSaveOrUpdateFieldMappings(mappings) {
  return request({
    url: '/gamebox/box-field-mapping/batchSaveOrUpdate',
    method: 'post',
    data: mappings
  })
}
