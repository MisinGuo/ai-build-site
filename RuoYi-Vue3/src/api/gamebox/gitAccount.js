import request from '@/utils/request'

export function listGitAccounts(query) {
  return request({ url: '/gamebox/git-accounts/list', method: 'get', params: query })
}

export function getGitAccount(id) {
  return request({ url: '/gamebox/git-accounts/' + id, method: 'get' })
}

/** 查询启用账号（供向导下拉使用） */
export function listEnabledGitAccounts() {
  return request({ url: '/gamebox/git-accounts/enabled', method: 'get' })
}

export function addGitAccount(data) {
  return request({ url: '/gamebox/git-accounts', method: 'post', data })
}

export function updateGitAccount(data) {
  return request({ url: '/gamebox/git-accounts', method: 'put', data })
}

export function deleteGitAccounts(ids) {
  return request({ url: '/gamebox/git-accounts/' + ids, method: 'delete' })
}

export function setDefaultGitAccount(id) {
  return request({ url: '/gamebox/git-accounts/' + id + '/default', method: 'put' })
}

export function verifyGitAccount(id) {
  return request({ url: '/gamebox/git-accounts/' + id + '/verify', method: 'get' })
}
