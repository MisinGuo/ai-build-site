import request from '@/utils/request'

// 查询游戏盒子列表
export function listBox(query) {
  return request({
    url: '/gamebox/box/list',
    method: 'get',
    params: query
  })
}

// 查询游戏盒子详细
export function getBox(id) {
  return request({
    url: '/gamebox/box/' + id,
    method: 'get'
  })
}

// 新增游戏盒子
export function addBox(data) {
  return request({
    url: '/gamebox/box',
    method: 'post',
    data: data
  })
}

// 修改游戏盒子
export function updateBox(data) {
  return request({
    url: '/gamebox/box',
    method: 'put',
    data: data
  })
}

// 更新游戏盒子状态
export function updateBoxStatus(data) {
  return request({
    url: '/gamebox/box/status',
    method: 'put',
    data: data
  })
}

// 删除游戏盒子
export function delBox(id) {
  return request({
    url: '/gamebox/box/' + id,
    method: 'delete'
  })
}

// 获取游戏盒子的主页绑定（主文章）
export function getBoxHomepage(boxId) {
  return request({
    url: '/gamebox/box/' + boxId + '/homepage',
    method: 'get'
  })
}

// 绑定主文章到游戏盒子主页
export function bindBoxHomepage(boxId, data) {
  return request({
    url: '/gamebox/box/' + boxId + '/homepage',
    method: 'post',
    data: data
  })
}

// 解绑游戏盒子主页
export function unbindBoxHomepage(boxId, data) {
  return request({
    url: '/gamebox/box/' + boxId + '/homepage',
    method: 'delete',
    data: data
  })
}

// 获取游戏盒子的分类列表
export function getBoxCategories(boxId) {
  return request({
    url: '/gamebox/box/' + boxId + '/categories',
    method: 'get'
  })
}

// 保存游戏盒子的分类列表
export function saveBoxCategories(boxId, categories) {
  return request({
    url: '/gamebox/box/' + boxId + '/categories',
    method: 'post',
    data: categories
  })
}

// 验证盒子的siteId变更后分类是否有效
export function validateBoxCategories(boxId, newSiteId) {
  return request({
    url: '/gamebox/box/' + boxId + '/validate-categories',
    method: 'get',
    params: { newSiteId }
  })
}

