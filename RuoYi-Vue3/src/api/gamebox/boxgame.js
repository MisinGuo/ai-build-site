import request from '@/utils/request'

// 查询游戏盒子-游戏关联列表
export function listBoxGame(query) {
  return request({
    url: '/gamebox/boxgame/list',
    method: 'get',
    params: query
  })
}

// 根据盒子ID查询关联的游戏列表
export function getGamesByBoxId(boxId) {
  return request({
    url: '/gamebox/boxgame/box/' + boxId,
    method: 'get'
  })
}

// 根据游戏ID查询关联的盒子列表
export function getBoxesByGameId(gameId) {
  return request({
    url: '/gamebox/boxgame/game/' + gameId,
    method: 'get'
  })
}

// 查询游戏盒子-游戏关联详细
export function getBoxGame(id) {
  return request({
    url: '/gamebox/boxgame/' + id,
    method: 'get'
  })
}

// 新增游戏盒子-游戏关联
export function addBoxGame(data) {
  return request({
    url: '/gamebox/boxgame',
    method: 'post',
    data: data
  })
}

// 修改游戏盒子-游戏关联
export function updateBoxGame(data) {
  return request({
    url: '/gamebox/boxgame',
    method: 'put',
    data: data
  })
}

// 删除游戏盒子-游戏关联
export function delBoxGame(id) {
  return request({
    url: '/gamebox/boxgame/' + id,
    method: 'delete'
  })
}

// 批量添加游戏到盒子
export function batchAddGames(boxId, gameIds) {
  return request({
    url: '/gamebox/boxgame/batch/add/' + boxId,
    method: 'post',
    data: gameIds
  })
}

// 批量从盒子移除游戏
export function batchRemoveGames(boxId, gameIds) {
  return request({
    url: '/gamebox/boxgame/batch/remove/' + boxId,
    method: 'delete',
    data: gameIds
  })
}

// 批量添加游戏到盒子（带关联数据）
export function batchAddGamesWithRelations(boxId, gameRelations) {
  return request({
    url: '/gamebox/boxgame/batch/add-with-relations/' + boxId,
    method: 'post',
    data: gameRelations
  })
}

// 导出游戏盒子-游戏关联
export function exportBoxGame(query) {
  return request({
    url: '/gamebox/boxgame/export',
    method: 'post',
    params: query
  })
}
