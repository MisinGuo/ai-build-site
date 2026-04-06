<template>
  <div class="app-container proxy-config-page">
    <!-- 页头说明 -->
    <el-alert
      title="代理配置说明"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    >
      <template #default>
        以下列出了系统中所有功能模块及其对外部网络的依赖情况。
        对于<strong>需要代理</strong>的功能（标记为"支持配置"），当你发现该功能在当前网络环境下无法正常使用时，
        可为其单独配置代理服务器以实现加速访问。<br/>
        <span style="color: #f56c6c;">注意：本地操作类功能（标记为"无需代理"）无外部网络调用，
        请勿为其强行配置代理，否则可能导致功能异常。</span>
      </template>
    </el-alert>

    <!-- 筛选工具栏 -->
    <el-row :gutter="10" class="mb8" style="align-items: center">
      <el-col :span="1.5">
        <el-radio-group v-model="filterMode" size="small" @change="applyFilter">
          <el-radio-button value="all">全部功能</el-radio-button>
          <el-radio-button value="applicable">支持代理</el-radio-button>
          <el-radio-button value="enabled">已启用代理</el-radio-button>
        </el-radio-group>
      </el-col>
      <el-col :span="1.5" style="margin-left: 12px;">
        <el-button :loading="loading" icon="Refresh" @click="loadData">刷新</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="Check" :loading="saving" @click="handleBatchSave" v-hasPermi="['gamebox:proxy:edit']">
          保存所有修改
        </el-button>
      </el-col>
    </el-row>

    <!-- 功能分组列表 -->
    <div v-loading="loading">
      <div
        v-for="group in filteredGroups"
        :key="group.groupName"
        class="feature-group"
      >
        <div class="group-header">
          <el-icon style="margin-right: 6px; vertical-align: middle;"><Folder /></el-icon>
          <span>{{ group.groupName }}</span>
          <el-tag size="small" type="info" style="margin-left: 8px">{{ group.items.length }} 个功能</el-tag>
          <el-tag v-if="getGroupEnabledCount(group) > 0" size="small" type="success" style="margin-left: 6px">
            {{ getGroupEnabledCount(group) }} 个已开启代理
          </el-tag>
        </div>

        <el-table :data="group.items" size="default" style="width: 100%">
          <!-- 功能名称 -->
          <el-table-column label="功能名称" min-width="200" prop="featureName">
            <template #default="{ row }">
              <div style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap;">
                <span style="font-weight: 500;">{{ row.featureName }}</span>
                <el-tag v-if="row.proxyApplicable === 1" size="small" type="warning">支持配置</el-tag>
                <el-tag v-else size="small" type="info">无需代理</el-tag>
              </div>
              <div v-if="row.featureDesc" style="color: #909399; font-size: 12px; margin-top: 4px; line-height: 1.4;">
                {{ row.featureDesc }}
              </div>
            </template>
          </el-table-column>

          <!-- 代理状态 -->
          <el-table-column label="代理状态" width="130" align="center">
            <template #default="{ row }">
              <template v-if="row.proxyApplicable === 1">
                <el-switch
                  v-model="row.proxyEnabled"
                  :active-value="1"
                  :inactive-value="0"
                  active-text="已开启"
                  inactive-text="未开启"
                  @change="markDirty(row)"
                  v-hasPermi="['gamebox:proxy:edit']"
                />
              </template>
              <el-tag v-else size="small" type="info">-</el-tag>
            </template>
          </el-table-column>

          <!-- 代理服务器 -->
          <el-table-column label="代理服务器" min-width="220" align="center">
            <template #default="{ row }">
              <template v-if="row.proxyApplicable === 1">
                <div v-if="row.proxyEnabled === 1" style="display: flex; align-items: center; gap: 4px; flex-wrap: wrap; justify-content: center;">
                  <el-tag v-if="row.proxyHost" type="primary" size="small" style="max-width: 200px; overflow: hidden; text-overflow: ellipsis;">
                    {{ row.proxyType || 'http' }}://{{ row.proxyHost }}:{{ row.proxyPort || '-' }}
                  </el-tag>
                  <el-tag v-else type="danger" size="small">未配置</el-tag>
                </div>
                <span v-else style="color: #c0c4cc; font-size: 12px;">未启用</span>
              </template>
              <span v-else style="color: #c0c4cc; font-size: 12px;">不适用</span>
            </template>
          </el-table-column>

          <!-- 操作 -->
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <template v-if="row.proxyApplicable === 1">
                <el-button
                  type="primary"
                  size="small"
                  link
                  icon="Edit"
                  @click="openEditDialog(row)"
                  v-hasPermi="['gamebox:proxy:edit']"
                >
                  配置
                </el-button>
                <el-button
                  type="success"
                  size="small"
                  link
                  icon="Connection"
                  :loading="testingId === row.id"
                  @click="handleTestConnection(row)"
                  v-hasPermi="['gamebox:proxy:query']"
                >
                  测试
                </el-button>
              </template>
              <el-tooltip v-else content="本地操作无需代理" placement="top">
                <el-button size="small" link disabled icon="Lock">-</el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-empty v-if="filteredGroups.length === 0 && !loading" description="暂无匹配的功能" />
    </div>

    <!-- 编辑代理配置对话框 -->
    <el-dialog
      v-model="editDialog.visible"
      :title="'配置代理 — ' + (editDialog.row ? editDialog.row.featureName : '')"
      width="520px"
      :close-on-click-modal="false"
      @closed="resetEditForm"
    >
      <template v-if="editDialog.row">
        <!-- 功能说明 -->
        <el-descriptions :column="1" border size="small" style="margin-bottom: 16px;">
          <el-descriptions-item label="功能分组">{{ editDialog.row.featureGroup }}</el-descriptions-item>
          <el-descriptions-item label="功能说明">
            <span style="color: #606266; font-size: 13px;">{{ editDialog.row.featureDesc }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 代理注意事项 -->
        <el-alert
          v-if="editDialog.row.proxyWarning"
          :title="editDialog.row.proxyWarning"
          type="warning"
          show-icon
          :closable="false"
          style="margin-bottom: 16px; font-size: 13px;"
        />

        <el-form :model="editForm" ref="editFormRef" label-width="100px" :rules="editRules">
          <el-form-item label="启用代理">
            <el-switch
              v-model="editForm.proxyEnabled"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="关闭"
            />
          </el-form-item>

          <template v-if="editForm.proxyEnabled === 1">
            <el-form-item label="代理协议" prop="proxyType">
              <el-select v-model="editForm.proxyType" style="width: 100%">
                <el-option label="HTTP" value="http" />
                <el-option label="HTTPS" value="https" />
                <el-option label="SOCKS5" value="socks5" />
              </el-select>
            </el-form-item>

            <el-form-item label="代理地址" prop="proxyHost">
              <el-input
                v-model="editForm.proxyHost"
                placeholder="例如：127.0.0.1 或 proxy.example.com"
                clearable
              />
            </el-form-item>

            <el-form-item label="代理端口" prop="proxyPort">
              <el-input-number
                v-model="editForm.proxyPort"
                :min="1"
                :max="65535"
                placeholder="例如：7890"
                style="width: 100%"
                controls-position="right"
              />
            </el-form-item>

            <el-divider content-position="left">
              <span style="font-size: 12px; color: #909399;">认证信息（可选，不需要认证则留空）</span>
            </el-divider>

            <el-form-item label="用户名">
              <el-input
                v-model="editForm.proxyUsername"
                placeholder="代理认证用户名（可选）"
                clearable
              />
            </el-form-item>

            <el-form-item label="密码">
              <el-input
                v-model="editForm.proxyPassword"
                type="password"
                placeholder="代理认证密码（可选）"
                show-password
                clearable
              />
            </el-form-item>
          </template>

          <el-form-item label="备注">
            <el-input
              v-model="editForm.remark"
              type="textarea"
              :rows="2"
              placeholder="备注（可选）"
            />
          </el-form-item>
        </el-form>
      </template>

      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="editDialog.saving" @click="handleSaveOne">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Folder } from '@element-plus/icons-vue'
import { listProxyConfig, updateProxyConfig, batchUpdateProxyConfig, testProxyConnection } from '@/api/gamebox/proxy'

// ---- 数据 ----
const loading = ref(false)
const saving = ref(false)
const allConfigs = ref([])
const dirtyIds = ref(new Set())
const filterMode = ref('all')
const testingId = ref(null) // 当前正在测试中的记录 ID

// ---- 筛选后的分组数据 ----
const filteredGroups = computed(() => {
  let items = allConfigs.value
  if (filterMode.value === 'applicable') {
    items = items.filter(item => item.proxyApplicable === 1)
  } else if (filterMode.value === 'enabled') {
    items = items.filter(item => item.proxyEnabled === 1)
  }

  // 按分组聚合
  const groupMap = {}
  for (const item of items) {
    const g = item.featureGroup || '其他'
    if (!groupMap[g]) {
      groupMap[g] = { groupName: g, items: [] }
    }
    groupMap[g].items.push(item)
  }
  return Object.values(groupMap)
})

function getGroupEnabledCount(group) {
  return group.items.filter(item => item.proxyEnabled === 1).length
}

// ---- 加载数据 ----
async function loadData() {
  loading.value = true
  try {
    const res = await listProxyConfig({})
    allConfigs.value = (res.data || []).map(item => ({ ...item }))
    dirtyIds.value.clear()
  } catch (e) {
    ElMessage.error('加载代理配置失败：' + (e.message || e))
  } finally {
    loading.value = false
  }
}

function applyFilter() {
  // filteredGroups 是计算属性，自动响应 filterMode 变化
}

function markDirty(row) {
  dirtyIds.value.add(row.id)
}

// ---- 批量保存（保存所有被"switch"切换过的条目） ----
async function handleBatchSave() {
  if (dirtyIds.value.size === 0) {
    ElMessage.info('没有需要保存的修改')
    return
  }
  saving.value = true
  try {
    const dirtyList = allConfigs.value.filter(item => dirtyIds.value.has(item.id))
    await batchUpdateProxyConfig(dirtyList)
    ElMessage.success(`已保存 ${dirtyList.length} 条配置`)
    dirtyIds.value.clear()
  } catch (e) {
    ElMessage.error('保存失败：' + (e.message || e))
  } finally {
    saving.value = false
  }
}

// ---- 编辑对话框 ----
const editDialog = reactive({
  visible: false,
  row: null,
  saving: false
})

const editFormRef = ref(null)
const editForm = reactive({
  id: null,
  proxyEnabled: 0,
  proxyType: 'http',
  proxyHost: '',
  proxyPort: null,
  proxyUsername: '',
  proxyPassword: '',
  remark: ''
})

const editRules = {
  proxyType: [{ required: true, message: '请选择代理协议', trigger: 'change' }],
  proxyHost: [{ required: true, message: '请输入代理服务器地址', trigger: 'blur' }],
  proxyPort: [{ required: true, message: '请输入代理端口', trigger: 'blur' }]
}

function openEditDialog(row) {
  editDialog.row = row
  editForm.id = row.id
  editForm.proxyEnabled = row.proxyEnabled ?? 0
  editForm.proxyType = row.proxyType || 'http'
  editForm.proxyHost = row.proxyHost || ''
  editForm.proxyPort = row.proxyPort || null
  editForm.proxyUsername = row.proxyUsername || ''
  editForm.proxyPassword = row.proxyPassword || ''
  editForm.remark = row.remark || ''
  editDialog.visible = true
}

function resetEditForm() {
  editDialog.row = null
  editFormRef.value?.resetFields()
}

async function handleSaveOne() {
  // 只有启用代理时才校验地址/端口
  if (editForm.proxyEnabled === 1) {
    try {
      await editFormRef.value.validate()
    } catch {
      return
    }
  }

  editDialog.saving = true
  try {
    await updateProxyConfig({ ...editForm })
    // 同步到列表数据
    const target = allConfigs.value.find(item => item.id === editForm.id)
    if (target) {
      Object.assign(target, {
        proxyEnabled: editForm.proxyEnabled,
        proxyType: editForm.proxyType,
        proxyHost: editForm.proxyHost,
        proxyPort: editForm.proxyPort,
        proxyUsername: editForm.proxyUsername,
        proxyPassword: editForm.proxyPassword,
        remark: editForm.remark
      })
      dirtyIds.value.delete(editForm.id)
    }
    ElMessage.success('代理配置已保存')
    editDialog.visible = false
  } catch (e) {
    ElMessage.error('保存失败：' + (e.message || e))
  } finally {
    editDialog.saving = false
  }
}

// ---- 测试代理连通性 ----
async function handleTestConnection(row) {
  if (!row.proxyEnabled) {
    ElMessage.warning('该功能未启用代理，请先启用并配置代理地址')
    return
  }
  testingId.value = row.id
  try {
    const res = await testProxyConnection(row.id)
    const d = res.data || {}
    const icon = d.success ? '✅' : '❌'
    const proxyLine = d.proxyActive
      ? `<span style="color:#67c23a">代理已生效：${d.proxyDesc}</span>`
      : `<span style="color:#f56c6c">代理未生效（检查配置）</span>`
    const statusLine = d.success
      ? `HTTP ${d.httpStatus} &nbsp;|&nbsp; 耗时 ${d.elapsedMs}ms`
      : `错误：${d.errorMessage || '连接失败'}`
    ElMessageBox.alert(
      `<div style="line-height:1.9">
        <div>${proxyLine}</div>
        <div>目标地址：<code>${d.testUrl}</code></div>
        <div>${icon} ${statusLine}</div>
        <div style="margin-top:8px;color:#909399;font-size:12px">
          （后端日志中可查看 [PROXY ON] / [PROXY OFF] / [PROXY TEST] 标记）
        </div>
      </div>`,
      `代理测试结果 — ${row.featureName}`,
      { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' }
    ).catch(() => {})
  } catch (e) {
    ElMessage.error('测试请求失败：' + (e.message || e))
  } finally {
    testingId.value = null
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.proxy-config-page {
  padding: 20px;
}

.feature-group {
  margin-bottom: 24px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.group-header {
  padding: 12px 16px;
  background: #f5f7fa;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
}
</style>
