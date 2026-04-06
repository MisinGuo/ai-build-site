import request from '@/utils/request'

// 查询主文章列表
export function listMasterArticle(query) {
  return request({
    url: '/gamebox/masterArticle/list',
    method: 'get',
    params: query
  })
}

// 查询主文章详细
export function getMasterArticle(id) {
  return request({
    url: '/gamebox/masterArticle/' + id,
    method: 'get'
  })
}

// 新增主文章
export function addMasterArticle(data) {
  return request({
    url: '/gamebox/masterArticle',
    method: 'post',
    data: data
  })
}

// 修改主文章
export function updateMasterArticle(data) {
  return request({
    url: '/gamebox/masterArticle',
    method: 'put',
    data: data
  })
}

// 删除主文章
export function delMasterArticle(ids) {
  return request({
    url: '/gamebox/masterArticle/' + ids,
    method: 'delete'
  })
}

// 检查内容键是否存在
export function checkContentKeyExists(contentKey, siteId, excludeId) {
  return request({
    url: '/gamebox/masterArticle/checkContentKey',
    method: 'get',
    params: {
      contentKey: contentKey,
      siteId: siteId,
      excludeId: excludeId
    }
  })
}

// 查询主文章的所有语言版本
export function getLanguageVersions(masterContentId) {
  return request({
    url: '/gamebox/masterArticle/' + masterContentId + '/versions',
    method: 'get'
  })
}

// 获取主文章编辑数据（包含默认语言版本内容）
export function getMasterArticleEditData(id) {
  return request({
    url: '/gamebox/masterArticle/editData/' + id,
    method: 'get'
  })
}

// 获取主文章的主页绑定信息
export function getMasterArticleHomepageBinding(masterArticleId) {
  return request({
    url: '/gamebox/masterArticle/homepageBinding/' + masterArticleId,
    method: 'get'
  })
}

// 绑定主文章到游戏主页
export function bindMasterArticleToGame(data) {
  return request({
    url: '/gamebox/masterArticle/bindToGame',
    method: 'post',
    data: data
  })
}

// 绑定主文章到游戏盒子主页
export function bindMasterArticleToGameBox(data) {
  return request({
    url: '/gamebox/masterArticle/bindToGameBox',
    method: 'post',
    data: data
  })
}

// 解绑游戏主页
export function unbindMasterArticleFromGame(data) {
  return request({
    url: '/gamebox/masterArticle/unbindFromGame',
    method: 'post',
    params: data
  })
}

// 解绑游戏盒子主页
export function unbindMasterArticleFromGameBox(data) {
  return request({
    url: '/gamebox/masterArticle/unbindFromGameBox',
    method: 'post',
    params: data
  })
}

// ==================== 游戏关联管理接口 ====================

// 查询主文章关联的游戏列表
export function getMasterArticleGames(masterArticleId) {
  return request({
    url: '/gamebox/masterArticle/games/' + masterArticleId,
    method: 'get'
  })
}

// 保存主文章的游戏关联
export function saveMasterArticleGames(data) {
  return request({
    url: '/gamebox/masterArticle/games/save',
    method: 'post',
    params: { masterArticleId: data.masterArticleId },
    data: data.gameIds
  })
}

// 删除游戏关联
export function removeMasterArticleGame(masterArticleId, gameId) {
  return request({
    url: '/gamebox/masterArticle/games/' + masterArticleId + '/' + gameId,
    method: 'delete'
  })
}

// ==================== 游戏盒子关联管理接口 ====================

// 查询主文章关联的游戏盒子列表
export function getMasterArticleGameBoxes(masterArticleId) {
  return request({
    url: '/gamebox/masterArticle/gameboxes/' + masterArticleId,
    method: 'get'
  })
}

// 保存主文章的游戏盒子关联
export function saveMasterArticleGameBoxes(data) {
  return request({
    url: '/gamebox/masterArticle/gameboxes/save',
    method: 'post',
    params: { masterArticleId: data.masterArticleId },
    data: data.gameBoxIds
  })
}

// 删除游戏盒子关联
export function removeMasterArticleGameBox(masterArticleId, gameBoxId) {
  return request({
    url: '/gamebox/masterArticle/gameboxes/' + masterArticleId + '/' + gameBoxId,
    method: 'delete'
  })
}

// ==================== 排除管理接口 ====================

// 排除默认主文章
export function excludeDefaultMasterArticle(data) {
  return request({
    url: '/gamebox/masterArticle/exclude',
    method: 'post',
    params: data
  })
}

// 恢复默认主文章（取消排除）
export function restoreDefaultMasterArticle(data) {
  return request({
    url: '/gamebox/masterArticle/restore',
    method: 'post',
    params: data
  })
}

// 获取主文章被哪些网站排除
export function getExcludedSitesByMasterArticle(masterArticleId) {
  return request({
    url: '/gamebox/masterArticle/excludedSites/' + masterArticleId,
    method: 'get'
  })
}

// 批量管理主文章的排除网站
export function manageDefaultMasterArticleExclusions(data) {
  return request({
    url: '/gamebox/masterArticle/manageExclusions',
    method: 'post',
    params: { masterArticleId: data.masterArticleId },
    data: data.excludedSiteIds
  })
}
