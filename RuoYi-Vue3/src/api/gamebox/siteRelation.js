import request from '@/utils/request'

// ==================== 分类关联管理 ====================

// 查询分类关联的网站列表
export function getCategorySites(categoryId) {
  return request({
    url: `/gamebox/siterelation/category/sites/${categoryId}`,
    method: 'get'
  })
}

// 批量查询多个分类各自关联的网站（一次请求代替 N 次）
// ids: number[]  返回 data: { categoryId -> sites[] }
export function getBatchCategorySites(ids) {
  return request({
    url: '/gamebox/siterelation/category/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新分类在网站的可见性
export function updateCategoryVisibility(siteId, categoryId, isVisible) {
  return request({
    url: '/gamebox/siterelation/category/visibility',
    method: 'put',
    data: { siteId, categoryId, isVisible }
  })
}

/**
 * 批量保存分类的网站关联关系（统一接口）
 * @param {Object} data - { categoryIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveCategorySiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/category/batchSaveRelations',
    method: 'put',
    data: data
  })
}

// ==================== 游戏盒子关联管理 ====================

// 查询游戏盒子关联的网站列表
export function getBoxSites(boxId) {
  return request({
    url: `/gamebox/siterelation/box/sites/${boxId}`,
    method: 'get'
  })
}

// 批量查询多个游戏盒子各自关联的网站（一次请求代替 N 次）
export function getBatchBoxSites(ids) {
  return request({
    url: '/gamebox/siterelation/box/batchSites',
    method: 'post',
    data: ids
  })
}

// 复制游戏盒子到其他网站（含游戏和分类，无等价新接口）
export function copyBoxToSite(data) {
  return request({
    url: '/gamebox/siterelation/box/copy',
    method: 'post',
    data: data
  })
}

// 更新游戏盒子在网站的可见性
export function updateBoxVisibility(siteId, boxId, isVisible) {
  return request({
    url: '/gamebox/siterelation/box/visibility',
    method: 'put',
    data: { siteId, boxId, isVisible }
  })
}

// 更新游戏盒子在网站的完整配置（可见性、推荐、自定义名、排序等）
export function updateBoxSiteConfig(data) {
  return request({
    url: '/gamebox/siterelation/box/updateConfig',
    method: 'put',
    data: data
  })
}

// ==================== 游戏关联管理 ====================

// 查询游戏关联的网站列表
export function getGameSites(gameId) {
  return request({
    url: `/gamebox/siterelation/game/sites/${gameId}`,
    method: 'get'
  })
}

// 批量查询多个游戏各自关联的网站（一次请求代替 N 次）
export function getBatchGameSites(ids) {
  return request({
    url: '/gamebox/siterelation/game/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新游戏在网站的可见性
export function updateGameVisibility(siteId, gameId, isVisible) {
  return request({
    url: '/gamebox/siterelation/game/visibility',
    method: 'put',
    data: { siteId, gameId, isVisible }
  })
}

// 更新游戏在网站的完整配置（可见性、推荐、新游、自定义名、排序等）
export function updateGameSiteConfig(data) {
  return request({
    url: '/gamebox/siterelation/game/updateConfig',
    method: 'put',
    data: data
  })
}

// ==================== 文章关联管理 ====================

// 查询文章关联的网站列表
export function getArticleSites(articleId) {
  return request({
    url: `/gamebox/siterelation/article/sites/${articleId}`,
    method: 'get'
  })
}

// 批量查询多篇文章各自关联的网站（一次请求代替 N 次）
export function getBatchArticleSites(ids) {
  return request({
    url: '/gamebox/siterelation/article/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新文章在网站的配置（完整更新，需传入 id 或完整字段）
export function updateArticleSiteConfig(data) {
  return request({
    url: '/gamebox/siterelation/article/updateConfig',
    method: 'put',
    data: data
  })
}

// 更新文章在指定网站的可见性（仅更新 is_visible 字段）
export function updateArticleVisibility(siteId, articleId, isVisible) {
  return request({
    url: '/gamebox/siterelation/article/visibility',
    method: 'put',
    data: { siteId, articleId, isVisible }
  })
}

// ==================== 存储配置关联管理 ====================

// 查询存储配置关联的网站列表
export function getStorageConfigSites(storageConfigId) {
  return request({
    url: `/gamebox/siterelation/storageConfig/sites/${storageConfigId}`,
    method: 'get'
  })
}

// 批量查询多个存储配置各自关联的网站（一次请求代替 N 次）
export function getBatchStorageConfigSites(ids) {
  return request({
    url: '/gamebox/siterelation/storageConfig/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新存储配置在网站的可见性
export function updateStorageVisibility(siteId, storageConfigId, isVisible) {
  return request({
    url: '/gamebox/siterelation/storageConfig/visibility',
    method: 'put',
    data: { siteId, storageConfigId, isVisible }
  })
}

// ==================== AI平台配置关联管理 ====================

// 查询AI平台配置关联的网站列表
export function getPlatformSites(platformId) {
  return request({
    url: `/gamebox/siterelation/aiPlatform/sites/${platformId}`,
    method: 'get'
  })
}

// 批量查询多个AI平台各自关联的网站（一次请求代替 N 次）
export function getBatchPlatformSites(ids) {
  return request({
    url: '/gamebox/siterelation/aiPlatform/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新AI平台配置在网站的可见性
export function updatePlatformVisibility(siteId, platformId, isVisible) {
  return request({
    url: '/gamebox/siterelation/aiPlatform/visibility',
    method: 'put',
    data: { siteId, platformId, isVisible }
  })
}

/**
 * 批量保存 AI 平台的网站关联关系（统一接口）
 * @param {Object} data - { aiPlatformIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveAiPlatformSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/aiPlatform/batchSaveRelations',
    method: 'put',
    data: data
  })
}

// ==================== 内容工具配置关联管理 ====================

// ==================== 标题池批次关联管理 ====================

// 查询标题池批次关联的网站列表
export function getTitleBatchSites(batchId) {
  return request({
    url: `/gamebox/siterelation/titleBatch/sites/${batchId}`,
    method: 'get'
  })
}

// 批量查询多个批次各自关联的网站（一次请求代替 N 次）
export function getBatchTitleBatchSites(ids) {
  return request({
    url: '/gamebox/siterelation/titleBatch/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新标题池批次在网站的可见性
export function updateTitleBatchVisibility(siteId, batchId, isVisible) {
  return request({
    url: '/gamebox/siterelation/titleBatch/visibility',
    method: 'put',
    data: { siteId, batchId, isVisible }
  })
}

// 批量保存标题池批次的网站关联关系（统一接口，同时处理关联和排除）
// Body: { batchIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
export function batchSaveTitleBatchSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/titleBatch/batchSaveRelations',
    method: 'put',
    data: data
  })
}

// ==================== 原子工具关联管理 ====================

// 查询原子工具关联的网站列表
export function getAtomicToolSites(toolId) {
  return request({
    url: `/gamebox/siterelation/atomicTool/sites/${toolId}`,
    method: 'get'
  })
}

// 批量查询多个原子工具各自关联的网站（一次请求代替 N 次）
export function getBatchAtomicToolSites(ids) {
  return request({
    url: '/gamebox/siterelation/atomicTool/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新原子工具在网站的可见性
export function updateAtomicToolVisibility(siteId, toolId, isVisible) {
  return request({
    url: '/gamebox/siterelation/atomicTool/visibility',
    method: 'put',
    data: { siteId, atomicToolId: toolId, isVisible }
  })
}

// ==================== 工作流关联管理 ====================

// 查询工作流关联的网站列表
export function getWorkflowSites(workflowId) {
  return request({
    url: `/gamebox/siterelation/workflow/sites/${workflowId}`,
    method: 'get'
  })
}

// 批量查询多个工作流各自关联的网站（一次请求代替 N 次）
export function getBatchWorkflowSites(ids) {
  return request({
    url: '/gamebox/siterelation/workflow/batchSites',
    method: 'post',
    data: ids
  })
}

// 更新工作流在指定网站的可见性
export function updateWorkflowVisibility(siteId, workflowId, isVisible) {
  return request({
    url: '/gamebox/siterelation/workflow/visibility',
    method: 'put',
    data: { siteId, workflowId, isVisible }
  })
}

// 更新工作流在网站的配置（工作流关联表仅有 is_visible 字段，与 updateWorkflowVisibility 等价）
export function updateWorkflowSiteConfig(siteId, workflowId, isVisible) {
  return updateWorkflowVisibility(siteId, workflowId, isVisible)
}

// ==================== 统一保存关联关系接口（同时处理 include 和 exclude，一次发送完整状态） ====================
// 请求体格式：{ [contentIdField], includeSiteIds: Long[], excludeSiteIds: Long[] }
// 后端会做差量更新：新增缺失的关联/排除，删除多余的关联/排除

/**
 * 保存分类的网站关联关系
 * @param {Object} data - { categoryId, includeSiteIds: [], excludeSiteIds: [] }
 */
export function saveCategorySiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/category/saveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 保存游戏盒子的网站关联关系
 * @param {Object} data - { boxId, includeSiteIds: [], excludeSiteIds: [] }
 */
export function saveBoxSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/box/saveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 保存游戏的网站关联关系
 * @param {Object} data - { gameId, includeSiteIds: [], excludeSiteIds: [] }
 */
export function saveGameSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/game/saveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 批量保存存储配置的网站关联关系
 * @param {Object} data - { storageConfigIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveStorageConfigSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/storageConfig/batchSaveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 保存内容工具（promptTemplate）的网站关联关系
 * @param {Object} data - { promptTemplateId, includeSiteIds: [], excludeSiteIds: [] }
 */
export function saveTemplateSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/promptTemplate/saveRelations',
    method: 'put',
    data: data
  })
}

// ==================== 批量保存（多个内容ID + 目标网站，一次请求） ====================
// includeSiteIds / excludeSiteIds 任意一侧可省略，省略则该侧不变

/**
 * 批量保存工作流的网站关联关系（多个工作流一次请求）
 * @param {Object} data - { workflowIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveWorkflowSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/workflow/batchSaveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 批量保存游戏盒子的网站关联关系（多个盒子一次请求）
 * @param {Object} data - { boxIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveBoxSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/box/batchSaveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 批量保存游戏的网站关联关系（多个游戏一次请求）
 * @param {Object} data - { gameIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveGameSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/game/batchSaveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 批量保存文章的网站关联关系（多篇文章一次请求）
 * @param {Object} data - { articleIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveArticleSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/article/batchSaveRelations',
    method: 'put',
    data: data
  })
}

/**
 * 批量保存原子工具的网站关联关系（多个工具一次请求）
 * @param {Object} data - { atomicToolIds: [], includeSiteIds?: [], excludeSiteIds?: [] }
 */
export function batchSaveAtomicToolSiteRelations(data) {
  return request({
    url: '/gamebox/siterelation/atomicTool/batchSaveRelations',
    method: 'put',
    data: data
  })
}
