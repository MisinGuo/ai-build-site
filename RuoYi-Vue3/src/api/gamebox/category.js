import request from '@/utils/request'

// 查询分类列表
export function listCategory(query) {
  return request({
    url: '/gamebox/category/list',
    method: 'get',
    params: query
  })
}

// 查询可见的分类列表（用于编辑器选择）
export function listVisibleCategory(query) {
  return request({
    url: '/gamebox/category/visibleList',
    method: 'get',
    params: query
  })
}

// 查询分类详细
export function getCategory(id) {
  return request({
    url: '/gamebox/category/' + id,
    method: 'get'
  })
}

// 新增分类
export function addCategory(data) {
  return request({
    url: '/gamebox/category',
    method: 'post',
    data: data
  })
}

// 修改分类
export function updateCategory(data) {
  return request({
    url: '/gamebox/category',
    method: 'put',
    data: data
  })
}

// 删除分类
export function delCategory(id) {
  return request({
    url: '/gamebox/category/' + id,
    method: 'delete'
  })
}

// 获取分类类型选项列表
export function getCategoryTypes() {
  return request({
    url: '/gamebox/category/categoryTypes',
    method: 'get'
  })
}

// 查询文章板块列表
export function listSections(query) {
  return request({
    url: '/gamebox/category/sections',
    method: 'get',
    params: query
  })
}

// 通过板块ID查询分类列表
export function listCategoriesBySection(sectionId) {
  return request({
    url: '/gamebox/category/sections/' + sectionId + '/categories',
    method: 'get'
  })
}
