<template>
  <div class="config-editor-page">
    <!-- 顶部工具栏 -->
    <div class="editor-header">
      <div class="header-left">
        <el-button text icon="ArrowLeft" @click="goBack">返回</el-button>
        <el-divider direction="vertical" />
        <span class="site-name">{{ siteName }}</span>
        <el-tag size="small" type="info" style="margin-left:8px">配置编辑器</el-tag>
      </div>
      <div class="header-center">
        <el-alert
          v-if="configMappings.length === 0 && !loading"
          type="warning"
          :closable="false"
          show-icon
          style="padding:4px 12px;border-radius:6px"
        >
          该站点模板尚未定义配置映射，请联系管理员在模板中配置「配置文件映射」。
        </el-alert>
      </div>
      <div class="header-right">
        <el-tooltip content="预览站点">
          <el-button icon="Monitor" @click="togglePreview">{{ showPreview ? '隐藏预览' : '显示预览' }}</el-button>
        </el-tooltip>
        <el-button icon="RefreshRight" @click="loadCurrentValues" :loading="valuesLoading">刷新</el-button>
        <el-button type="primary" icon="Upload" @click="handleSaveAndPush" :loading="pushing">保存并推送</el-button>
      </div>
    </div>

    <!-- 配置文件路径警告横幅 -->
    <el-alert
      type="warning"
      show-icon
      :closable="false"
      style="margin:0;border-radius:0;border-left:none;border-right:none"
    >
      <template #title>
        ⚠️ 请勿手动修改代码仓库中 <code>src/config/customize/</code> 目录的文件结构、格式及路径，否则系统将无法解析并写入配置。请始终通过本编辑器修改配置项。
      </template>
    </el-alert>

    <!-- 主体区域 -->
    <div class="editor-body" :class="{ 'with-preview': showPreview }">
      <!-- 左侧：配置表单 -->
      <div class="editor-form-panel">
        <div v-if="loading" class="loading-placeholder">
          <el-skeleton :rows="10" animated />
        </div>
        <template v-else>
          <div v-if="configMappings.length === 0" class="empty-mappings">
            <el-empty description="暂未配置可编辑项" :image-size="80" />
          </div>
          <el-form v-else ref="configFormRef" :model="formValues" label-position="top" class="config-form">
            <!-- 按 group 分组展示 -->
            <div
              v-for="group in groupedMappings"
              :key="group.name"
              class="config-group"
            >
              <div class="group-title">{{ group.name }}</div>
              <el-row :gutter="16">
                <el-col
                  v-for="item in group.items"
                  :key="item.key"
                  :span="item.type === 'textarea' ? 24 : 12"
                >
                  <el-form-item
                    :label="item.label"
                    :prop="item.key"
                    :rules="item.required ? [{ required: true, message: item.label + ' 不能为空', trigger: 'blur' }] : []"
                  >
                    <!-- 文本 -->
                    <el-input
                      v-if="item.type === 'text'"
                      v-model="formValues[item.key]"
                      :placeholder="item.placeholder || item.defaultValue || ''"
                      clearable
                    />
                    <!-- 长文本 -->
                    <el-input
                      v-else-if="item.type === 'textarea'"
                      v-model="formValues[item.key]"
                      type="textarea"
                      :rows="3"
                      :placeholder="item.placeholder || item.defaultValue || ''"
                    />
                    <!-- 颜色 -->
                    <div v-else-if="item.type === 'color'" class="color-input-wrapper">
                      <el-color-picker v-model="formValues[item.key]" show-alpha />
                      <el-input
                        v-model="formValues[item.key]"
                        placeholder="#000000"
                        style="flex:1;margin-left:8px"
                      />
                    </div>
                    <!-- 数字 -->
                    <el-input-number
                      v-else-if="item.type === 'number'"
                      v-model="formValues[item.key]"
                      style="width:100%"
                    />
                    <!-- 开关 -->
                    <el-switch
                      v-else-if="item.type === 'switch'"
                      v-model="formValues[item.key]"
                      active-value="true"
                      inactive-value="false"
                    />
                    <!-- 默认文本 -->
                    <el-input v-else v-model="formValues[item.key]" clearable />
                    <!-- 说明 -->
                    <div v-if="item.description" class="field-desc">{{ item.description }}</div>
                    <!-- 文件路径提示 -->
                    <div class="field-path">
                      <el-icon size="10"><Document /></el-icon>
                      {{ item.filePath }}
                      <span v-if="item.tsKey" class="field-ts-key">· {{ item.tsKey }}</span>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-form>

          <!-- 未保存变更提示 -->
          <el-divider v-if="hasChanges && configMappings.length > 0" />
          <div v-if="hasChanges && configMappings.length > 0" class="unsaved-hint">
            <el-icon color="#e6a23c"><WarningFilled /></el-icon>
            有未保存的变更，点击「保存并推送」提交到代码仓库。
          </div>
        </template>
      </div>

      <!-- 右侧：预览面板 -->
      <div v-if="showPreview" class="editor-preview-panel">
        <div class="preview-toolbar">
          <span class="preview-title">实时预览</span>
          <el-button-group size="small">
            <el-button @click="startPreviewServer" :loading="previewStarting" icon="VideoPlay">启动预览</el-button>
            <el-button @click="refreshPreview" icon="Refresh">刷新</el-button>
            <el-button @click="stopPreviewServer" icon="VideoPause">停止</el-button>
          </el-button-group>
          <el-tag v-if="previewStatus === 'running'" type="success" size="small">运行中</el-tag>
          <el-tag v-else-if="previewStatus === 'starting'" type="warning" size="small">启动中...</el-tag>
          <el-tag v-else size="small" type="info">未启动</el-tag>
        </div>

        <div class="preview-notice">
          <el-alert type="info" :closable="false" show-icon style="border-radius:0;border-left:none;border-right:none">
            注意：预览为独立进程，保存并推送后需重新构建项目才能看到最新效果。
          </el-alert>
        </div>

        <div class="preview-frame-wrapper">
          <div v-if="previewStatus !== 'running'" class="preview-placeholder">
            <el-icon size="48" color="#c0c4cc"><Monitor /></el-icon>
            <p>点击「启动预览」运行本地预览服务</p>
            <el-button type="primary" @click="startPreviewServer" :loading="previewStarting">启动预览</el-button>
          </div>
          <iframe
            v-else
            ref="previewFrame"
            :src="previewUrl"
            class="preview-frame"
            sandbox="allow-scripts allow-same-origin allow-forms allow-popups"
          />
        </div>
      </div>
    </div>

    <!-- 保存并推送确认对话框 -->
    <el-dialog v-model="pushDialogOpen" title="保存并推送" width="460px" append-to-body>
      <div>
        <p>将把以下修改写入代码仓库并推送到远程：</p>
        <el-scrollbar max-height="200px">
          <ul class="change-list">
            <li v-for="(change, idx) in changeList" :key="idx">
              <el-tag size="small" type="warning" style="margin-right:6px">{{ change.filePath }}</el-tag>
              <span>{{ change.label }}: <code>{{ change.newValue }}</code></span>
            </li>
          </ul>
        </el-scrollbar>
        <el-form style="margin-top:16px">
          <el-form-item label="提交信息">
            <el-input v-model="commitMessage" placeholder="feat: update site config" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="pushDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="confirmPush" :loading="pushing">确认推送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSite } from '@/api/gamebox/site'
import { getSiteTemplate } from '@/api/gamebox/siteTemplate'
import { readFile, saveFile, pushCode, startPreview, stopPreview, getPreviewStatus } from '@/api/gamebox/codeManage'

const router = useRouter()
const route = useRoute()

const siteId = computed(() => Number(route.query.siteId))

// ─── 状态 ───────────────────────────────────────────────
const siteName = ref('')
const configMappings = ref([])   // 来自模板的配置映射定义
const formValues = ref({})       // 当前编辑中的值
const originalValues = ref({})   // 从文件读取的原始值（用于对比变更）
const loading = ref(true)
const valuesLoading = ref(false)
const pushing = ref(false)
const pushDialogOpen = ref(false)
const commitMessage = ref('')
const changeList = ref([])

// 预览相关
const showPreview = ref(false)
const previewStatus = ref('stopped') // stopped | starting | running
const previewStarting = ref(false)
const previewUrl = ref('')
const previewFrame = ref(null)
let previewPollTimer = null

const configFormRef = ref(null)

// ─── 计算属性 ────────────────────────────────────────────
const groupedMappings = computed(() => {
  const groups = {}
  for (const item of configMappings.value) {
    const gName = item.group || '基本配置'
    if (!groups[gName]) groups[gName] = { name: gName, items: [] }
    groups[gName].items.push(item)
  }
  return Object.values(groups)
})

const hasChanges = computed(() => {
  return configMappings.value.some(item => {
    return String(formValues.value[item.key] ?? '') !== String(originalValues.value[item.key] ?? '')
  })
})

// ─── 初始化 ─────────────────────────────────────────────
onMounted(async () => {
  await loadSiteAndTemplate()
  await loadCurrentValues()
  pollPreviewStatus()
})

onUnmounted(() => {
  if (previewPollTimer) clearInterval(previewPollTimer)
})

async function loadSiteAndTemplate() {
  loading.value = true
  try {
    const siteRes = await getSite(siteId.value)
    const siteData = siteRes.data
    siteName.value = siteData.name || '未知站点'

    if (!siteData.templateId) {
      loading.value = false
      return
    }

    const tmplRes = await getSiteTemplate(siteData.templateId)
    const tmpl = tmplRes.data
    if (tmpl.configMappings) {
      try {
        const parsed = JSON.parse(tmpl.configMappings)
        configMappings.value = Array.isArray(parsed) ? parsed : []
      } catch {
        configMappings.value = []
      }
    }

    // 初始化 formValues 为默认值
    const defaults = {}
    for (const item of configMappings.value) {
      defaults[item.key] = item.defaultValue || ''
    }
    formValues.value = { ...defaults }
    originalValues.value = { ...defaults }
  } catch (e) {
    ElMessage.error('加载站点信息失败：' + (e.message || String(e)))
  } finally {
    loading.value = false
  }
}

// ─── 读取当前配置文件值 ────────────────────────────────────
async function loadCurrentValues() {
  if (configMappings.value.length === 0) return
  valuesLoading.value = true

  // 按文件分组，减少请求次数
  const fileCache = {}
  const newValues = { ...formValues.value }

  for (const item of configMappings.value) {
    if (!item.filePath) continue
    if (!fileCache[item.filePath]) {
      try {
        const res = await readFile(siteId.value, item.filePath)
        fileCache[item.filePath] = res.data?.content || ''
      } catch {
        fileCache[item.filePath] = null
      }
    }

    const content = fileCache[item.filePath]
    if (content == null) continue

    const extracted = extractValueFromTs(content, item)
    if (extracted !== null) {
      newValues[item.key] = extracted
    }
  }

  formValues.value = { ...newValues }
  originalValues.value = { ...newValues }
  valuesLoading.value = false
  ElMessage.success('已加载最新配置值')
}

// ─── 从 TypeScript 文件内容提取值 ─────────────────────────
function extractValueFromTs(content, item) {
  const pattern = buildSearchRegex(item)
  if (!pattern) return null
  try {
    const regex = new RegExp(pattern, 's')
    const match = content.match(regex)
    if (match && match[2] !== undefined) return match[2]
  } catch {}
  return null
}

// ─── 构建搜索正则（统一入口）───────────────────────────────
function buildSearchRegex(item) {
  if (item.searchRegex) return item.searchRegex
  if (item.tsKey) {
    // 自动生成：匹配 key: 'value' 或 key: "value"
    const escaped = item.tsKey.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    return `(${escaped}\\s*:\\s*['"` + '`' + `])([^'"` + '`' + `]*?)(['"` + '`' + `])`
  }
  return null
}

// ─── 保存并推送 ──────────────────────────────────────────
async function handleSaveAndPush() {
  if (!hasChanges.value) {
    ElMessage.info('没有检测到变更')
    return
  }

  // 收集变更列表
  changeList.value = configMappings.value
    .filter(item => String(formValues.value[item.key] ?? '') !== String(originalValues.value[item.key] ?? ''))
    .map(item => ({
      key: item.key,
      label: item.label,
      filePath: item.filePath,
      newValue: formValues.value[item.key],
      oldValue: originalValues.value[item.key],
      item
    }))

  commitMessage.value = 'feat: update site config via editor'
  pushDialogOpen.value = true
}

async function confirmPush() {
  pushing.value = true
  try {
    // 按文件分组写入
    const fileCache = {}
    for (const change of changeList.value) {
      const { item, newValue } = change
      if (!item.filePath) continue

      if (!fileCache[item.filePath]) {
        const res = await readFile(siteId.value, item.filePath)
        fileCache[item.filePath] = res.data?.content || ''
      }

      const newContent = replaceValueInTs(fileCache[item.filePath], item, newValue)
      if (newContent === null) {
        ElMessage.warning(`字段 ${item.label} 未能匹配文件中的值，已跳过。请检查 tsKey 或 searchRegex 配置。`)
        continue
      }
      fileCache[item.filePath] = newContent
    }

    // 写回所有修改的文件
    for (const [filePath, content] of Object.entries(fileCache)) {
      await saveFile(siteId.value, filePath, content)
    }

    // 提交并推送
    await pushCode(siteId.value, commitMessage.value || 'feat: update site config via editor')

    // 更新 originalValues
    originalValues.value = { ...formValues.value }
    pushDialogOpen.value = false
    ElMessage.success('配置已保存并推送到代码仓库！')
  } catch (e) {
    ElMessage.error('推送失败：' + (e.message || String(e)))
  } finally {
    pushing.value = false
  }
}

// ─── 在 TypeScript 文件内容中替换值 ──────────────────────
function replaceValueInTs(content, item, newValue) {
  const pattern = buildSearchRegex(item)
  if (!pattern) return null
  const tpl = item.replaceTpl || '$1$VALUE$3'
  try {
    const regex = new RegExp(pattern, 's')
    if (!regex.test(content)) return null
    return content.replace(regex, (match, g1, g2, g3) => {
      return tpl.replace('$1', g1).replace('$VALUE', escapeForTs(newValue)).replace('$3', g3)
    })
  } catch {
    return null
  }
}

// 对新值做基本转义（防止破坏 TS 文件语法）
function escapeForTs(value) {
  return String(value).replace(/\\/g, '\\\\').replace(/'/g, "\\'").replace(/`/g, '\\`')
}

// ─── 预览服务器 ────────────────────────────────────────
function togglePreview() {
  showPreview.value = !showPreview.value
}

async function startPreviewServer() {
  previewStarting.value = true
  previewStatus.value = 'starting'
  try {
    await startPreview(siteId.value)
    ElMessage.info('预览服务启动中，请稍等...')
  } catch (e) {
    ElMessage.error('启动预览失败：' + (e.message || String(e)))
    previewStatus.value = 'stopped'
  } finally {
    previewStarting.value = false
  }
}

async function stopPreviewServer() {
  try {
    await stopPreview(siteId.value)
    previewStatus.value = 'stopped'
    previewUrl.value = ''
    ElMessage.success('预览服务已停止')
  } catch (e) {
    ElMessage.error('停止预览失败：' + (e.message || String(e)))
  }
}

function refreshPreview() {
  if (previewFrame.value) {
    const src = previewFrame.value.src
    previewFrame.value.src = ''
    setTimeout(() => { previewFrame.value.src = src }, 100)
  }
}

function pollPreviewStatus() {
  previewPollTimer = setInterval(async () => {
    try {
      const res = await getPreviewStatus(siteId.value)
      const data = res.data || {}
      if (data.running) {
        previewStatus.value = 'running'
        if (data.previewUrl && !previewUrl.value) {
          previewUrl.value = data.previewUrl
        }
      } else {
        if (previewStatus.value !== 'stopped') previewStatus.value = 'stopped'
      }
    } catch {}
  }, 5000)
}

function goBack() {
  router.back()
}
</script>

<style scoped>
.config-editor-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 84px);
  background: #f5f7fa;
}

.editor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  gap: 4px;
}

.header-center {
  flex: 1;
  min-width: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.site-name {
  font-weight: 600;
  font-size: 15px;
}

.editor-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.editor-body.with-preview .editor-form-panel {
  width: 50%;
  border-right: 1px solid #e4e7ed;
}

.editor-form-panel {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background: #fff;
}

.config-form {
  max-width: 900px;
}

.config-group {
  margin-bottom: 24px;
}

.group-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding: 6px 12px;
  background: #f0f2f5;
  border-left: 3px solid var(--el-color-primary);
  border-radius: 0 4px 4px 0;
  margin-bottom: 16px;
}

.color-input-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
}

.field-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

.field-path {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 3px;
}

.field-ts-key {
  color: #b8b8b8;
}

.unsaved-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #e6a23c;
  font-size: 13px;
  padding: 8px 0;
}

.loading-placeholder {
  padding: 20px;
}

.empty-mappings {
  padding: 60px 0;
}

/* ─── 预览面板 ─── */
.editor-preview-panel {
  width: 50%;
  display: flex;
  flex-direction: column;
  background: #1a1a2e;
}

.preview-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #16213e;
  border-bottom: 1px solid #0f3460;
}

.preview-title {
  color: #a0aec0;
  font-size: 13px;
  font-weight: 500;
  flex-shrink: 0;
}

.preview-notice {
  font-size: 12px;
}

.preview-frame-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #718096;
  gap: 12px;
}

.preview-placeholder p {
  margin: 0;
  font-size: 14px;
}

.preview-frame {
  width: 100%;
  height: 100%;
  border: none;
  background: #fff;
}

/* 推送变更列表 */
.change-list {
  padding-left: 16px;
  margin: 8px 0;
}

.change-list li {
  margin-bottom: 8px;
  font-size: 13px;
  line-height: 1.6;
}

code {
  background: #f0f2f5;
  padding: 1px 5px;
  border-radius: 3px;
  font-size: 12px;
}
</style>
