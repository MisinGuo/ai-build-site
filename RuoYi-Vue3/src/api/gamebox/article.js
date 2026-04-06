import request from '@/utils/request'

// 查询文章列表
export function listArticle(query) {
  return request({
    url: '/gamebox/article/list',
    method: 'get',
    params: query
  })
}

// 查询文章详细
export function getArticle(id) {
  return request({
    url: '/gamebox/article/' + id,
    method: 'get'
  })
}

// 新增文章
export function addArticle(data) {
  return request({
    url: '/gamebox/article',
    method: 'post',
    data: data
  })
}

// 修改文章
export function updateArticle(data) {
  return request({
    url: '/gamebox/article',
    method: 'put',
    data: data
  })
}

// 删除文章
export function delArticle(id) {
  return request({
    url: '/gamebox/article/' + id,
    method: 'delete'
  })
}

// 发布单个语言版本文章
export function publishArticle(id) {
  return request({
    url: '/gamebox/article/publish/' + id,
    method: 'post',
    timeout: 60000  // 发布涉及远程存储上传，超时设为 60s
  })
}

// 强制发布单个语言版本文章
export function publishArticleForce(id) {
  return request({
    url: '/gamebox/article/publishForce/' + id,
    method: 'post',
    timeout: 60000  // 发布涉及远程存储上传，超时设为 60s
  })
}

// 取消发布单个语言版本文章
export function unpublishSingleArticle(id) {
  return request({
    url: '/gamebox/article/unpublish/' + id,
    method: 'post',
    timeout: 60000
  })
}

// 撤销发布，将文章改为草稿
export function unpublishArticle(id) {
  return request({
    url: '/gamebox/article/unpublish/' + id,
    method: 'put',
    timeout: 60000
  })
}

// ==================== 排除管理 ====================

// 排除默认文章
export function excludeDefaultArticle(data) {
  return request({
    url: '/gamebox/siterelation/article/exclude',
    method: 'post',
    data: data
  })
}

// 恢复被排除的默认文章
export function restoreDefaultArticle(siteId, articleId) {
  return request({
    url: `/gamebox/siterelation/article/exclude/${siteId}/${articleId}`,
    method: 'delete'
  })
}

// 批量管理默认文章的排除网站
export function manageDefaultArticleExclusions(data) {
  return request({
    url: '/gamebox/siterelation/article/manage-exclusions',
    method: 'post',
    data: data
  })
}

// 查询默认文章被哪些网站排除
export function getExcludedSitesByArticle(articleId) {
  return request({
    url: `/gamebox/siterelation/article/excluded-sites/${articleId}`,
    method: 'get'
  })
}

// 保存主文章为草稿（创建默认语言版本草稿）
export function saveMasterArticleDraft(data) {
  return request({
    url: '/gamebox/masterArticle/saveDraft',
    method: 'post',
    data: data
  })
}

// 更新主文章草稿（更新默认语言版本草稿）
export function updateMasterArticleDraft(data) {
  return request({
    url: '/gamebox/masterArticle/updateDraft',
    method: 'post',
    data: data
  })
}

// 批量发布语言版本文章（新API）
export function batchPublishLanguageVersions(articleIds) {
  return request({
    url: '/gamebox/article/batchPublish',
    method: 'post',
    data: { articleIds: articleIds },
    timeout: 120000  // 批量发布可能较慢，超时设为 120s
  })
}

// 强制批量发布语言版本文章
export function batchPublishLanguageVersionsForce(articleIds) {
  return request({
    url: '/gamebox/article/batchPublishForce',
    method: 'post',
    data: { articleIds: articleIds },
    timeout: 120000  // 批量发布可能较慢，超时设为 120s
  })
}

// 批量撤销发布语言版本文章
export function batchUnpublishLanguageVersions(articleIds) {
  return request({
    url: '/gamebox/article/batchUnpublish',
    method: 'post',
    data: { articleIds: articleIds }
  })
}

// 批量更新文章状态（只修改数据库状态）
export function batchUpdateArticleStatus(articleIds, status) {
  return request({
    url: '/gamebox/article/batchUpdateStatus',
    method: 'post',
    data: { articleIds: articleIds, status: status }
  })
}

// 检查单个文章远程文件状态
export function checkRemoteFileStatus(articleId) {
  return request({
    url: '/gamebox/article/checkRemoteStatus/' + articleId,
    method: 'post'
  })
}

// 批量检查文章远程文件状态
export function batchCheckRemoteFileStatus(articleIds) {
  return request({
    url: '/gamebox/article/batchCheckRemoteStatus',
    method: 'post',
    data: { articleIds: articleIds }
  })
}
