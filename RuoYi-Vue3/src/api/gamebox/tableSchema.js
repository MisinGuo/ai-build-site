import request from '@/utils/request'

// 获取数据库表结构(字段列表和注释)
export function getTableSchema(tableName) {
  return request({
    url: '/gamebox/table-schema/' + tableName,
    method: 'get'
  })
}

// 获取所有相关表的字段信息
export function getAllTableFields() {
  return request({
    url: '/gamebox/table-schema/all-fields',
    method: 'get'
  })
}
