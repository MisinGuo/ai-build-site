import request from '@/utils/request'

// 获取站点支持的语言列表
export function getSupportedLocales(siteId) {
  return request({
    url: `/gamebox/translation/supported-locales/${siteId}`,
    method: 'get'
  })
}

// 获取实体的所有翻译（所有语言）
export function getEntityTranslations(entityType, entityId) {
  return request({
    url: `/gamebox/translation/${entityType}/${entityId}/all`,
    method: 'get'
  })
}

// 获取实体指定语言的翻译
export function getEntityTranslationsByLocale(entityType, entityId, locale) {
  return request({
    url: `/gamebox/translation/${entityType}/${entityId}`,
    method: 'get',
    params: { locale }
  })
}

// 保存单个翻译
export function saveTranslation(data) {
  return request({
    url: '/gamebox/translation',
    method: 'post',
    data: data
  })
}

// 批量保存翻译
export function batchSaveTranslations(data) {
  return request({
    url: '/gamebox/translation/batch-save',
    method: 'post',
    data: data
  })
}

// 删除实体的所有翻译
export function deleteEntityTranslations(entityType, entityId) {
  return request({
    url: `/gamebox/translation/${entityType}/${entityId}`,
    method: 'delete'
  })
}

// 删除单个字段翻译
export function deleteFieldTranslation(entityType, entityId, locale, fieldName) {
  return request({
    url: `/gamebox/translation/${entityType}/${entityId}/${locale}/${fieldName}`,
    method: 'delete'
  })
}

// 自动翻译单个文本
export function autoTranslateText(text, targetLang) {
  return request({
    url: '/gamebox/translation/auto-translate-text',
    method: 'post',
    data: {
      text: text,
      targetLang: targetLang
    }
  })
}

// 自动翻译实体的所有字段
export function autoTranslateEntity(entityType, entityId, siteId, fields) {
  return request({
    url: '/gamebox/translation/auto-translate-entity',
    method: 'post',
    data: {
      entityType: entityType,
      entityId: entityId,
      siteId: siteId,
      fields: fields
    }
  })
}

// 批量自动翻译多个实体
export function batchAutoTranslate(entityType, siteId, entities) {
  return request({
    url: '/gamebox/translation/batch-auto-translate',
    method: 'post',
    timeout: 300000, // 批量翻译5分钟超时（支持更多网站）
    data: {
      entityType: entityType,
      siteId: siteId,
      entities: entities
    }
  })
}
