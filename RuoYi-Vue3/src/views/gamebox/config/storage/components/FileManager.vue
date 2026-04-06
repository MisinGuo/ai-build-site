<template>
  <div class="storage-file-manager">
    <!-- 顶部工具栏 -->
    <el-card class="toolbar-card" shadow="never" :body-style="{ padding: '16px' }">
      <el-row :gutter="16" align="middle">
        <el-col :span="12">
          <div class="breadcrumb-wrapper" @click="enablePathEdit" style="cursor: pointer;">
            <el-icon size="20" color="#909399" style="margin-right: 8px;">
              <Folder />
            </el-icon>
            <!-- 显示模式：面包屑导航 -->
            <el-breadcrumb separator="/" v-if="!isEditingPath">
              <el-breadcrumb-item>
                <el-link @click.stop="navigateTo('')" :underline="false">根目录</el-link>
              </el-breadcrumb-item>
              <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
                <el-link @click.stop="navigateTo(item.path)" :underline="false">{{ item.name }}</el-link>
              </el-breadcrumb-item>
            </el-breadcrumb>
            <!-- 编辑模式：路径输入框 -->
            <el-input
              v-else
              ref="pathInputRef"
              v-model="pathInput"
              placeholder="输入路径 (例如: folder1/folder2)"
              @keyup.enter="handlePathJump"
              @blur="handlePathInputBlur"
              @click.stop
              style="flex: 1;"
            >
              <template #append>
                <el-button :icon="Right" @click="handlePathJump">跳转</el-button>
              </template>
            </el-input>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="toolbar-actions">
            <el-button type="success" :icon="FolderAdd" @click="showCreateFolderDialog">
              新建文件夹
            </el-button>
            <el-upload
              ref="uploadRef"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :data="{ path: currentPath }"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :timeout="60000"
            >
              <el-button type="primary" :icon="Upload">上传文件</el-button>
            </el-upload>
            <el-button :icon="Refresh" @click="loadFiles" circle />
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 文件列表 -->
    <el-card class="file-list-card" shadow="never" :body-style="{ padding: '0' }">
      <el-table
        v-loading="loading"
        :data="fileList"
        style="width: 100%"
        @row-click="handleRowClick"
        :row-class-name="getRowClassName"
      >
        <el-table-column label="名称" min-width="350">
          <template #default="{ row }">
            <div class="file-name-cell">
              <el-icon v-if="row.type === 'directory'" color="#409EFF" size="20">
                <Folder />
              </el-icon>
              <el-icon v-else color="#67C23A" size="20">
                <Document />
              </el-icon>
              <span class="file-name" :class="{ 'is-directory': row.type === 'directory' }">
                {{ row.name }}
              </span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="大小" width="120" align="right">
          <template #default="{ row }">
            <span v-if="row.type === 'file'" class="file-size">{{ formatFileSize(row.size) }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        
        <el-table-column label="修改时间" width="180">
          <template #default="{ row }">
            <span v-if="row.lastModified" class="file-time">
              {{ parseTime(row.lastModified, '{y}-{m}-{d} {h}:{i}') }}
            </span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <template v-if="row.type === 'file'">
                <el-tooltip content="编辑" v-if="row.editable">
                  <el-button
                    type="primary"
                    link
                    :icon="Edit"
                    @click.stop="handleEdit(row)"
                  />
                </el-tooltip>
                <el-tooltip content="下载">
                  <el-button
                    type="success"
                    link
                    :icon="Download"
                    @click.stop="handleDownload(row)"
                  />
                </el-tooltip>
                <el-tooltip content="重命名">
                  <el-button
                    type="warning"
                    link
                    :icon="EditPen"
                    @click.stop="showRenameDialog(row)"
                  />
                </el-tooltip>
                <el-tooltip content="删除">
                  <el-button
                    type="danger"
                    link
                    :icon="Delete"
                    @click.stop="handleDelete(row)"
                  />
                </el-tooltip>
              </template>
              <template v-else>
                <el-tooltip content="打开文件夹">
                  <el-button
                    type="primary"
                    link
                    :icon="FolderOpened"
                    @click.stop="navigateTo(row.path)"
                  />
                </el-tooltip>
                <el-tooltip content="重命名文件夹">
                  <el-button
                    type="warning"
                    link
                    :icon="EditPen"
                    @click.stop="showRenameDialog(row)"
                  />
                </el-tooltip>
                <el-tooltip content="删除文件夹">
                  <el-button
                    type="danger"
                    link
                    :icon="Delete"
                    @click.stop="handleDelete(row)"
                  />
                </el-tooltip>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <div v-if="!loading && fileList.length === 0" class="empty-state">
        <el-empty description="此目录为空" />
      </div>
    </el-card>

    <!-- 文本编辑器对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="`编辑文件: ${editingFile?.name}`"
      width="80%"
      :close-on-click-modal="false"
    >
      <div v-loading="contentLoading" element-loading-text="加载文件内容中...">
        <el-input
          v-model="fileContent"
          type="textarea"
          :rows="25"
          placeholder="文件内容"
          :disabled="contentLoading"
        />
      </div>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 新建文件夹对话框 -->
    <el-dialog
      v-model="createFolderDialogVisible"
      title="新建文件夹"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="folderForm" label-width="100px">
        <el-form-item label="文件夹名称">
          <el-input
            v-model="folderForm.folderName"
            placeholder="请输入文件夹名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createFolderDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateFolder">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 重命名对话框 -->
    <el-dialog
      v-model="renameDialogVisible"
      :title="`重命名${renamingItem?.type === 'directory' ? '文件夹' : '文件'}`"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form>
        <el-form-item label="原名称">
          <el-input v-model="renamingItem.name" disabled />
        </el-form-item>
        <el-form-item label="新名称">
          <el-input
            v-model="renameForm.newName"
            placeholder="请输入新名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renameDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="renaming" @click="handleRename">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="StorageFileManager">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  Refresh,
  Folder,
  FolderOpened,
  FolderAdd,
  Document,
  Edit,
  Download,
  Delete,
  EditPen,
  Right
} from '@element-plus/icons-vue'
import { parseTime } from '@/utils/ruoyi'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { saveAs } from 'file-saver'
import {
  listFiles,
  uploadFile,
  deleteFile,
  readTextFile,
  updateTextFile,
  createFolder,
  getDownloadFileUrl,
  getPresignedDownloadUrl,
  renameFile,
  countDirectoryContents
} from '@/api/gamebox/storageFile'

const props = defineProps({
  configId: {
    type: Number,
    required: true
  },
  config: {
    type: Object,
    default: null
  }
})

// 数据
const loading = ref(false)
const fileList = ref([])
const currentPath = ref('')
const editDialogVisible = ref(false)
const editingFile = ref(null)
const fileContent = ref('')
const contentLoading = ref(false)
const saving = ref(false)
const uploadRef = ref(null)
const createFolderDialogVisible = ref(false)
const creating = ref(false)
const folderForm = ref({
  folderName: ''
})
const renameDialogVisible = ref(false)
const renaming = ref(false)
const renamingItem = ref(null)
const renameForm = ref({
  newName: ''
})
const isEditingPath = ref(false)
const pathInputRef = ref(null)
const pathInput = ref('')

// 计算面包屑导航
const breadcrumbs = computed(() => {
  if (!currentPath.value) return []
  
  const parts = currentPath.value.split('/').filter(p => p)
  return parts.map((name, index) => ({
    name,
    path: parts.slice(0, index + 1).join('/')
  }))
})

// 上传URL
const uploadUrl = computed(() => {
  const baseURL = import.meta.env.VITE_APP_BASE_API || '/dev-api'
  return `${baseURL}/gamebox/storage/files/upload/${props.configId}`
})

// 上传请求头
const uploadHeaders = computed(() => ({
  Authorization: 'Bearer ' + getToken()
}))

// 加载文件列表
const loadFiles = async () => {
  loading.value = true
  try {
    const response = await listFiles(props.configId, currentPath.value)
    fileList.value = response.data || []
  } catch (error) {
    ElMessage.error('加载文件列表失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 导航到指定路径
const navigateTo = (path) => {
  currentPath.value = path
  loadFiles()
}

// 处理行点击
const handleRowClick = (row) => {
  if (row.type === 'directory') {
    navigateTo(row.path)
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 上传前检查
const beforeUpload = (file) => {
  // 检查是否上传 .gitkeep 文件
  if (file.name === '.gitkeep') {
    ElMessage.error('不允许上传 .gitkeep 文件，此文件由系统自动管理')
    return false
  }
  
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isLt100M) {
    ElMessage.error('上传文件大小不能超过 100MB!')
    return false
  }
  
  return true
}

// 上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    loadFiles()
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 上传失败
const handleUploadError = (error, file, fileList) => {
  console.error('Upload error:', error)
  console.error('File:', file)
  let errorMsg = '上传失败'
  if (error.message) {
    errorMsg += ': ' + error.message
  } else if (error.status === 0) {
    errorMsg = '上传失败: 网络错误或请求超时，请检查后端服务是否正常运行'
  } else if (error.status) {
    errorMsg += ': HTTP ' + error.status
  }
  ElMessage.error(errorMsg)
}

// 编辑文件
const handleEdit = async (row) => {
  editingFile.value = row
  editDialogVisible.value = true
  contentLoading.value = true
  fileContent.value = ''
  
  try {
    const response = await readTextFile(props.configId, row.path)
    fileContent.value = response.data.content || ''
  } catch (error) {
    ElMessage.error('读取文件失败: ' + error.message)
    editDialogVisible.value = false
  } finally {
    contentLoading.value = false
  }
}

// 保存文件
const handleSave = async () => {
  if (!editingFile.value) return
  
  saving.value = true
  try {
    await updateTextFile(
      props.configId,
      editingFile.value.path,
      fileContent.value
    )
    ElMessage.success('保存成功')
    editDialogVisible.value = false
    loadFiles()
  } catch (error) {
    ElMessage.error('保存失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

// 显示新建文件夹对话框
const showCreateFolderDialog = () => {
  folderForm.value.folderName = ''
  createFolderDialogVisible.value = true
}

// 创建文件夹
const handleCreateFolder = async () => {
  if (!folderForm.value.folderName || !folderForm.value.folderName.trim()) {
    ElMessage.warning('请输入文件夹名称')
    return
  }

  // 验证文件夹名称（不能包含特殊字符）
  const invalidChars = /[<>:"|?*\\\/]/
  if (invalidChars.test(folderForm.value.folderName)) {
    ElMessage.warning('文件夹名称不能包含特殊字符: < > : " | ? * \\ /')
    return
  }

  creating.value = true
  try {
    await createFolder(props.configId, currentPath.value, folderForm.value.folderName)
    ElMessage.success('文件夹创建成功')
    createFolderDialogVisible.value = false
    loadFiles()
  } catch (error) {
    ElMessage.error('创建文件夹失败: ' + error.message)
  } finally {
    creating.value = false
  }
}

// 下载文件 - 使用预签名URL直接下载
const handleDownload = async (row) => {
  try {
    ElMessage.info('正在获取下载链接...')
    
    // 获取预签名URL
    const response = await getPresignedDownloadUrl(props.configId, row.path)
    // RuoYi框架的响应拦截器已经返回了data，所以直接访问url字段
    const downloadUrl = response.url || response.data?.url
    
    if (!downloadUrl) {
      throw new Error('未获取到下载链接')
    }
    
    // 后端已在预签名URL中设置了Content-Disposition，直接使用URL即可流式下载
    const link = document.createElement('a')
    link.href = downloadUrl
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    
    // 清理
    setTimeout(() => {
      document.body.removeChild(link)
    }, 100)
    
    ElMessage.success('开始下载')
    
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载失败:', error)
    ElMessage.error('下载失败: ' + (error.message || '未知错误'))
  }
}

// 删除文件
const handleDelete = async (row) => {
  const itemType = row.type === 'directory' ? '文件夹' : '文件'
  let warningMessage = ''
  
  if (row.type === 'directory') {
    // 调用后端统计接口
    try {
      ElMessage.info('正在统计目录内容...')
      const response = await countDirectoryContents(props.configId, row.path)
      const counts = response.data
      
      if (counts.fileCount === 0 && counts.dirCount === 0) {
        warningMessage = `确定要删除空文件夹"${row.name}"吗？`
      } else {
        const details = []
        if (counts.fileCount > 0) {
          details.push(`${counts.fileCount} 个文件`)
        }
        if (counts.dirCount > 0) {
          details.push(`${counts.dirCount} 个子目录`)
        }
        warningMessage = `确定要删除文件夹"${row.name}"吗？\n\n此操作将删除：${details.join('、')}`
      }
    } catch (error) {
      console.error('统计目录内容失败:', error)
      warningMessage = `确定要删除文件夹"${row.name}"吗？这将删除文件夹中的所有内容！`
    }
  } else {
    warningMessage = `确定要删除文件"${row.name}"吗？`
  }
    
  ElMessageBox.confirm(
    warningMessage,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: false
    }
  ).then(async () => {
    try {
      await deleteFile(props.configId, row.path)
      ElMessage.success(`${itemType}删除成功`)
      loadFiles()
    } catch (error) {
      ElMessage.error(`删除失败: ${error.message}`)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 启用路径编辑模式
const enablePathEdit = () => {
  pathInput.value = currentPath.value
  isEditingPath.value = true
  // 等待DOM更新后聚焦输入框
  setTimeout(() => {
    pathInputRef.value?.focus()
  }, 100)
}

// 路径输入框失去焦点
const handlePathInputBlur = () => {
  setTimeout(() => {
    isEditingPath.value = false
  }, 200)
}

// 路径跳转（支持自动向上查找）
const handlePathJump = async () => {
  let targetPath = pathInput.value.trim()
  
  if (!targetPath) {
    // 空路径，跳转到根目录
    currentPath.value = ''
    isEditingPath.value = false
    loadFiles()
    return
  }
  
  // 处理路径格式
  if (targetPath.startsWith('/')) {
    targetPath = targetPath.substring(1)
  }
  if (targetPath.endsWith('/') && targetPath.length > 0) {
    targetPath = targetPath.substring(0, targetPath.length - 1)
  }
  
  // 尝试访问目标路径
  let currentTryPath = targetPath
  let foundPath = false
  
  while (true) {
    try {
      loading.value = true
      // 尝试列出当前路径的文件
      await listFiles(props.configId, currentTryPath)
      // 成功，说明路径存在
      currentPath.value = currentTryPath
      foundPath = true
      if (currentTryPath !== targetPath) {
        ElMessage.warning(`路径 "${targetPath}" 不存在，已自动跳转到最近的父目录: ${currentTryPath || '根目录'}`)
      }
      break
    } catch (error) {
      // 路径不存在，向上查找
      if (!currentTryPath || currentTryPath === '') {
        // 已经到根目录了，尝试根目录
        try {
          await listFiles(props.configId, '')
          currentPath.value = ''
          foundPath = true
          ElMessage.warning(`路径 "${targetPath}" 不存在，已跳转到根目录`)
          break
        } catch (e) {
          break
        }
      }
      
      // 获取父路径
      const lastSlashIndex = currentTryPath.lastIndexOf('/')
      if (lastSlashIndex === -1) {
        // 没有父路径了，尝试根目录
        currentTryPath = ''
      } else {
        currentTryPath = currentTryPath.substring(0, lastSlashIndex)
      }
    } finally {
      loading.value = false
    }
  }
  
  if (foundPath) {
    isEditingPath.value = false
    loadFiles()
  } else {
    ElMessage.error('无法访问该路径')
  }
}

// 显示重命名对话框
const showRenameDialog = (row) => {
  renamingItem.value = { ...row }
  renameForm.value.newName = row.name
  renameDialogVisible.value = true
}

// 重命名文件或文件夹
const handleRename = async () => {
  if (!renameForm.value.newName || !renameForm.value.newName.trim()) {
    ElMessage.warning('请输入新名称')
    return
  }

  // 验证名称（不能包含特殊字符）
  const invalidChars = /[<>:"|?*\\\/]/
  if (invalidChars.test(renameForm.value.newName)) {
    ElMessage.warning('名称不能包含特殊字符: < > : " | ? * \\ /')
    return
  }

  // 检查名称是否改变
  if (renameForm.value.newName === renamingItem.value.name) {
    ElMessage.warning('新名称与原名称相同')
    return
  }

  renaming.value = true
  try {
    await renameFile(props.configId, renamingItem.value.path, renameForm.value.newName)
    ElMessage.success('重命名成功')
    renameDialogVisible.value = false
    loadFiles()
  } catch (error) {
    ElMessage.error('重命名失败: ' + error.message)
  } finally {
    renaming.value = false
  }
}

// 表格行类名
const getRowClassName = ({ row }) => {
  return row.type === 'directory' ? 'directory-row' : 'file-row'
}

// 初始化
onMounted(() => {
  loadFiles()
})
</script>

<style scoped lang="scss">
.storage-file-manager {
  .toolbar-card {
    margin-bottom: 16px;
    border-radius: 8px;
    
    .breadcrumb-wrapper {
      display: flex;
      align-items: center;
      padding: 8px 12px;
      background-color: #f5f7fa;
      border-radius: 4px;
      transition: all 0.2s;
      min-height: 40px;
      
      &:hover:not(:has(.el-input)) {
        background-color: #e8eaed;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      }
    }
    
    .toolbar-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }
    
    .path-input-wrapper {
      :deep(.el-input-group__prepend) {
        background-color: #f5f7fa;
      }
    }
  }

  .file-list-card {
    border-radius: 8px;
  }

  .file-name-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .file-name {
      font-size: 14px;
      color: #303133;
      
      &.is-directory {
        color: #409EFF;
        font-weight: 500;
      }
    }
  }

  .file-size {
    color: #606266;
    font-size: 13px;
  }

  .file-time {
    color: #909399;
    font-size: 13px;
  }

  .text-muted {
    color: #C0C4CC;
  }

  .action-buttons {
    display: flex;
    justify-content: center;
    gap: 4px;
  }

  .empty-state {
    padding: 60px 0;
  }

  :deep(.el-table) {
    .directory-row {
      background-color: #f0f9ff;
      
      &:hover {
        background-color: #e1f3ff !important;
      }
    }
    
    .file-row {
      &:hover {
        background-color: #f5f7fa !important;
      }
    }
    
    .el-table__row {
      cursor: pointer;
      transition: background-color 0.2s;
    }
  }
}
</style>
