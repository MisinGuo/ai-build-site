import request from '@/utils/request'

export function listCfAccounts(query) {
  return request({ url: '/gamebox/cf-accounts/list', method: 'get', params: query })
}

export function getCfAccount(id) {
  return request({ url: '/gamebox/cf-accounts/' + id, method: 'get' })
}

/** 查询启用账号（供向导下拉使用） */
export function listEnabledCfAccounts() {
  return request({ url: '/gamebox/cf-accounts/enabled', method: 'get' })
}

export function addCfAccount(data) {
  return request({ url: '/gamebox/cf-accounts', method: 'post', data })
}

export function updateCfAccount(data) {
  return request({ url: '/gamebox/cf-accounts', method: 'put', data })
}

export function deleteCfAccounts(ids) {
  return request({ url: '/gamebox/cf-accounts/' + ids, method: 'delete' })
}

export function setDefaultCfAccount(id) {
  return request({ url: '/gamebox/cf-accounts/' + id + '/default', method: 'put' })
}

export function verifyCfAccount(id) {
  return request({ url: '/gamebox/cf-accounts/' + id + '/verify', method: 'get' })
}
