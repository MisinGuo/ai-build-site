import request from '@/utils/request'

const LONG_RUNNING_TIMEOUT = 120000

// ── 批次接口 ──────────────────────────────────────────────

/** 查询导入批次列表（分页） */
export function listImportBatch(query) {
  return request({
    url: '/gamebox/import/batch/list',
    method: 'get',
    params: query
  })
}

/** 撤回整个批次 */
export function revertImportBatch(id) {
  return request({
    url: '/gamebox/import/batch/revert',
    method: 'post',
    params: { batchId: id },
    timeout: LONG_RUNNING_TIMEOUT
  })
}

/** 获取批次全部变更记录 */
export function getBatchChanges(batchId) {
  return request({
    url: '/gamebox/import/batch/changes',
    method: 'get',
    params: { batchId }
  })
}

/** 获取批次号的全部变更记录（用于手工修改批次） */
export function getBatchChangesByNo(batchNo) {
  return request({
    url: '/gamebox/import/batch/changes',
    method: 'get',
    params: { batchNo }
  })
}

// ── 游戏历史接口 ──────────────────────────────────────────

/** 查询指定游戏的历史变更 */
export function getGameHistory(gameId) {
  return request({
    url: '/gamebox/import/game/' + gameId + '/history',
    method: 'get'
  })
}

// ── 单条变更撤回 ──────────────────────────────────────────

/** 撤回单条变更 */
export function revertChange(id) {
  return request({
    url: '/gamebox/import/change/' + id + '/revert',
    method: 'post'
  })
}

/** 重新应用整个批次 */
export function reApplyImportBatch(id) {
  return request({
    url: '/gamebox/import/batch/reapply',
    method: 'post',
    params: { batchId: id },
    timeout: LONG_RUNNING_TIMEOUT
  })
}

/** 撤回指定批次号（用于手工修改批次） */
export function revertImportBatchByNo(batchNo) {
  return request({
    url: '/gamebox/import/batch/revert',
    method: 'post',
    params: { batchNo },
    timeout: LONG_RUNNING_TIMEOUT
  })
}

/** 重新应用指定批次号（用于手工修改批次） */
export function reApplyImportBatchByNo(batchNo) {
  return request({
    url: '/gamebox/import/batch/reapply',
    method: 'post',
    params: { batchNo },
    timeout: LONG_RUNNING_TIMEOUT
  })
}

// ── 批次 + 指定游戏的撤回 / 重新应用 ──────────────────────

/** 撤回指定批次中指定游戏的全部变更（主表+关联表视为一条数据） */
export function revertByBatchGame(batchId, gameId) {
  return request({
    url: '/gamebox/import/batch/game/revert',
    method: 'post',
    params: { batchId, gameId },
    timeout: LONG_RUNNING_TIMEOUT
  })
}

/** 重新应用指定批次中指定游戏的全部已撤回变更 */
export function reApplyByBatchGame(batchId, gameId) {
  return request({
    url: '/gamebox/import/batch/game/reapply',
    method: 'post',
    params: { batchId, gameId },
    timeout: LONG_RUNNING_TIMEOUT
  })
}

/** 撤回指定批次号中指定游戏的全部变更（用于手工修改批次） */
export function revertByBatchNoGame(batchNo, gameId) {
  return request({
    url: '/gamebox/import/batch/game/revert',
    method: 'post',
    params: { batchNo, gameId },
    timeout: LONG_RUNNING_TIMEOUT
  })
}

/** 重新应用指定批次号中指定游戏的全部变更（用于手工修改批次） */
export function reApplyByBatchNoGame(batchNo, gameId) {
  return request({
    url: '/gamebox/import/batch/game/reapply',
    method: 'post',
    params: { batchNo, gameId },
    timeout: LONG_RUNNING_TIMEOUT
  })
}
