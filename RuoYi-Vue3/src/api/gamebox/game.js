import request from '@/utils/request'

// 查询游戏列表
export function listGame(query) {
  return request({
    url: '/gamebox/game/list',
    method: 'get',
    params: query
  })
}

// 查询符合筛选条件的所有游戏ID（不分页，用于跨页全选）
export function listGameIds(query) {
  return request({
    url: '/gamebox/game/ids',
    method: 'get',
    params: query
  })
}

// 查询游戏详细
export function getGame(id) {
  return request({
    url: '/gamebox/game/' + id,
    method: 'get'
  })
}

// 新增游戏
export function addGame(data) {
  return request({
    url: '/gamebox/game',
    method: 'post',
    data: data
  })
}

// 批量导入游戏（设置更长的超时时间）
export function batchAddGames(games) {
  return request({
    url: '/gamebox/game/batch',
    method: 'post',
    timeout: 120000, // 2分钟超时，支持大量游戏导入
    data: games
  })
}

// 修改游戏
export function updateGame(data) {
  return request({
    url: '/gamebox/game',
    method: 'put',
    data: data
  })
}

// 更新游戏状态
export function updateGameStatus(data) {
  return request({
    url: '/gamebox/game/status',
    method: 'put',
    data: data
  })
}

// 删除游戏
export function delGame(id) {
  return request({
    url: '/gamebox/game/' + id,
    method: 'delete'
  })
}

// 获取游戏的主页绑定（主文章）
export function getGameHomepage(gameId) {
  return request({
    url: '/gamebox/game/' + gameId + '/homepage',
    method: 'get'
  })
}

// 绑定主文章到游戏主页
export function bindGameHomepage(gameId, data) {
  return request({
    url: '/gamebox/game/' + gameId + '/homepage',
    method: 'post',
    data: data
  })
}

// 解绑游戏主页
export function unbindGameHomepage(gameId, data) {
  return request({
    url: '/gamebox/game/' + gameId + '/homepage',
    method: 'delete',
    data: data
  })
}

// 获取游戏的分类列表
export function getGameCategories(gameId) {
  return request({
    url: '/gamebox/game/' + gameId + '/categories',
    method: 'get'
  })
}

// 保存游戏的分类关联
export function saveGameCategories(gameId, categories) {
  return request({
    url: '/gamebox/game/' + gameId + '/categories',
    method: 'post',
    data: categories
  })
}
// ==================== 排除管理 ====================

// 排除默认游戏
export function excludeDefaultGame(data) {
  return request({
    url: '/gamebox/siterelation/game/exclude',
    method: 'post',
    data: data
  })
}

// 恢复被排除的默认游戏
export function restoreDefaultGame(siteId, gameId) {
  return request({
    url: `/gamebox/siterelation/game/exclude/${siteId}/${gameId}`,
    method: 'delete'
  })
}

// 追加游戏的分类关联（合并而不是覆盖）
export function appendGameCategories(gameId, categories) {
  return request({
    url: '/gamebox/game/' + gameId + '/categories/append',
    method: 'post',
    data: categories
  })
}

// 批量保存多个游戏的分类关联
export function batchSaveGameCategories(gameCategoryMappings) {
  return request({
    url: '/gamebox/game/batch/categories',
    method: 'post',
    data: gameCategoryMappings
  })
}

// 批量追加多个游戏的分类关联
export function batchAppendGameCategories(gameCategoryMappings) {
  return request({
    url: '/gamebox/game/batch/categories/append',
    method: 'post',
    data: gameCategoryMappings
  })
}

// 多平台游戏数据导入（使用字段映射配置）
export function importFromPlatform(data) {
  return request({
    url: '/gamebox/game/import/platform',
    method: 'post',
    timeout: 120000, // 2分钟超时
    data: data
  })
}

// 获取支持的平台列表
export function getSupportedPlatforms() {
  return request({
    url: '/gamebox/game/import/platforms',
    method: 'get'
  })
}
