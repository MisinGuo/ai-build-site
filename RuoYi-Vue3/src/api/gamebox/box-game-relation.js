import request from '@/utils/request'

// 查询游戏的所有盒子关联配置
export function getGameBoxRelations(gameId) {
  return request({
    url: '/gamebox/boxgame/game/' + gameId,
    method: 'get'
  })
}

// 更新盒子-游戏关联配置
export function updateBoxGameRelation(data) {
  return request({
    url: '/gamebox/boxgame',
    method: 'put',
    data: data
  })
}
