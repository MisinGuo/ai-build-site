import request from '@/utils/request'

/**
 * 列出文件
 * @param {number} configId - 存储配置ID
 * @param {string} path - 文件路径
 */
export function listFiles(configId, path) {
  return request({
    url: `/gamebox/storage/files/list/${configId}`,
    method: 'get',
    params: { path }
  })
}

/**
 * 上传文件
 * @param {number} configId - 存储配置ID
 * @param {string} path - 目标路径
 * @param {File} file - 文件对象
 */
export function uploadFile(configId, path, file) {
  const formData = new FormData()
  formData.append('path', path)
  formData.append('file', file)
  
  return request({
    url: `/gamebox/storage/files/upload/${configId}`,
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 删除文件
 * @param {number} configId - 存储配置ID
 * @param {string} path - 文件路径
 */
export function deleteFile(configId, path) {
  return request({
    url: `/gamebox/storage/files/${configId}`,
    method: 'delete',
    params: { path }
  })
}

/**
 * 读取文本文件
 * @param {number} configId - 存储配置ID
 * @param {string} path - 文件路径
 */
export function readTextFile(configId, path) {
  return request({
    url: `/gamebox/storage/files/read/${configId}`,
    method: 'get',
    params: { path }
  })
}

/**
 * 更新文本文件
 * @param {number} configId - 存储配置ID
 * @param {string} path - 文件路径
 * @param {string} content - 文件内容
 */
export function updateTextFile(configId, path, content) {
  return request({
    url: `/gamebox/storage/files/update/${configId}`,
    method: 'put',
    data: { path, content }
  })
}

/**
 * 创建文件夹
 * @param {number} configId - 存储配置ID
 * @param {string} parentPath - 父路径
 * @param {string} folderName - 文件夹名称
 */
export function createFolder(configId, parentPath, folderName) {
  return request({
    url: `/gamebox/storage/files/folder/${configId}`,
    method: 'post',
    data: { parentPath, folderName }
  })
}

/**
 * 获取下载URL
 * @param {number} configId - 存储配置ID
 * @param {string} path - 文件路径
 */
export function getDownloadUrl(configId, path) {
  return request({
    url: `/gamebox/storage/files/download-url/${configId}`,
    method: 'get',
    params: { path }
  })
}

/**
 * 下载文件（通过后端代理）
 * @deprecated 建议使用 getPresignedDownloadUrl 获取预签名URL,让客户端直接下载
 * @param {number} configId - 存储配置ID
 * @param {string} path - 文件路径
 * @returns {string} 下载URL
 */
export function getDownloadFileUrl(configId, path) {
  // 返回后端代理下载接口的URL，使用环境变量中的baseURL
  const baseURL = import.meta.env.VITE_APP_BASE_API || '/dev-api'
  return `${baseURL}/gamebox/storage/files/download/${configId}?path=${encodeURIComponent(path)}`
}

/**
 * 获取文件预签名下载URL(推荐方式)
 * 客户端可使用返回的URL直接从存储服务下载,无需通过服务器代理
 * @param {number} configId - 存储配置ID
 * @param {string} path - 文件路径
 * @param {number} expirationMinutes - URL有效期(分钟),默认60分钟
 * @returns {Promise} 响应结果包含 url 字段
 */
export function getPresignedDownloadUrl(configId, path, expirationMinutes = 60) {
  return request({
    url: `/gamebox/storage/files/presigned-url/${configId}`,
    method: 'get',
    params: { path, expirationMinutes }
  })
}

/**
 * 重命名文件或文件夹
 * @param {number} configId - 存储配置ID
 * @param {string} oldPath - 旧路径
 * @param {string} newName - 新名称
 */
export function renameFile(configId, oldPath, newName) {
  return request({
    url: `/gamebox/storage/files/rename/${configId}`,
    method: 'put',
    params: { oldPath, newName }
  })
}

/**
 * 统计目录内容
 * @param {number} configId - 存储配置ID
 * @param {string} path - 目录路径
 */
export function countDirectoryContents(configId, path) {
  return request({
    url: `/gamebox/storage/files/count/${configId}`,
    method: 'get',
    params: { path }
  })
}
