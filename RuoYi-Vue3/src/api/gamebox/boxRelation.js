import request from '@/utils/request'

/**
 * 获取游戏关联的游戏盒子列表
 * @param {number} gameId
 * @param {number|null} siteId 可选，传入则按站点三源可见性过滤
 */
export function getGameBoxes(gameId, siteId) {
  return request({
    url: `/gamebox/boxgame/game/${gameId}/boxes`,
    method: 'get',
    params: siteId ? { siteId } : undefined
  })
}

/**
 * 批量添加游戏到游戏盒子
 */
export function addGameToBox(data) {
  return request({
    url: '/gamebox/boxgame/batch-add',
    method: 'post',
    data: data
  })
}

/**
 * 移除游戏与游戏盒子的关联
 */
export function removeGameFromBox(data) {
  return request({
    url: '/gamebox/boxgame/remove',
    method: 'post',
    data: data
  })
}

/**
 * 更新游戏在游戏盒子中的配置
 */
export function updateGameBoxConfig(data) {
  return request({
    url: '/gamebox/boxgame/update-config',
    method: 'post',
    data: data
  })
}
