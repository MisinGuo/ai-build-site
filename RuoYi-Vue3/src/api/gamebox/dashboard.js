import request from '@/utils/request'

// 获取首页仪表盘统计数据
export function getDashboardStatistics() {
  return request({
    url: '/gamebox/dashboard/statistics',
    method: 'get'
  })
}
