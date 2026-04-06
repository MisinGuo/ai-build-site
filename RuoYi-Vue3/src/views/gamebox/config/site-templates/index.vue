<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入模板名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="站点类型" prop="siteFunctionType">
        <el-select v-model="queryParams.siteFunctionType" placeholder="全部" clearable>
          <el-option label="自动获客站" value="seo_site" />
          <el-option label="落地站" value="landing_site" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:siteTemplate:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:siteTemplate:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:siteTemplate:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="模板名称" align="center" prop="name" min-width="140" />
      <el-table-column label="站点类型" align="center" prop="siteFunctionType" width="110">
        <template #default="scope">
          <el-tag v-if="scope.row.siteFunctionType === 'seo_site'" type="primary">自动获客站</el-tag>
          <el-tag v-else-if="scope.row.siteFunctionType === 'landing_site'" type="success">落地站</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="行业" align="center" prop="industry" width="100" />
      <el-table-column label="框架" align="center" prop="framework" width="100" />
      <el-table-column label="Git 地址" align="center" prop="templateGitUrl" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="入口目录" align="center" prop="entryDir" width="100" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
            {{ scope.row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120">
        <template #default="scope">
          <el-tooltip content="修改" placement="top">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:siteTemplate:edit']" />
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:siteTemplate:remove']" />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-tabs v-model="dialogTab">
        <!-- Tab 1：基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form ref="templateRef" :model="form" :rules="rules" label-width="110px">
            <el-row>
              <el-col :span="12">
                <el-form-item label="模板名称" prop="name">
                  <el-input v-model="form.name" placeholder="请输入模板名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="站点类型" prop="siteFunctionType">
                  <el-select v-model="form.siteFunctionType" style="width:100%">
                    <el-option label="自动获客站" value="seo_site" />
                    <el-option label="落地站" value="landing_site" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="行业" prop="industry">
                  <el-input v-model="form.industry" placeholder="如：游戏、教育" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="框架" prop="framework">
                  <el-input v-model="form.framework" placeholder="如：Next.js" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="Git 地址" prop="templateGitUrl">
              <el-input v-model="form.templateGitUrl" placeholder="模板仓库 Git URL（公开仓库无需账号）" />
            </el-form-item>
            <el-form-item label="Git 账号" prop="gitAccountId">
              <el-select v-model="form.gitAccountId" placeholder="公开仓库可不选" clearable style="width:100%" v-loading="gitAccountsLoading">
                <el-option
                  v-for="a in gitAccounts"
                  :key="a.id"
                  :label="`${a.name}（${a.provider} / ${a.account}）`"
                  :value="a.id"
                />
              </el-select>
              <div style="font-size:12px;color:#909399;margin-top:4px">
                私有模板仓库需选择具有读取权限的 Git 账号，系统将以前称账号凭据克隆模板。公开仓库可不选。
              </div>
            </el-form-item>
            <el-row>
              <el-col :span="12">
                <el-form-item label="Git 分支" prop="templateGitBranch">
                  <el-input v-model="form.templateGitBranch" placeholder="main" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="入口目录" prop="entryDir">
                  <el-input v-model="form.entryDir" placeholder="留空表示仓库根目录" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="模板描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入模板描述" />
            </el-form-item>
            <el-form-item label="预览图 URL" prop="previewImage">
              <el-input v-model="form.previewImage" placeholder="模板预览图片 URL" />
            </el-form-item>
            <el-row>
              <el-col :span="12">
                <el-form-item label="排序" prop="sortOrder">
                  <el-input-number v-model="form.sortOrder" :min="0" style="width:100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="form.status">
                    <el-radio value="1">启用</el-radio>
                    <el-radio value="0">禁用</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- Tab 2：环境变量模板 -->
        <el-tab-pane label="环境变量" name="env">
          <div style="padding:4px 0 8px">
            <el-alert type="info" show-icon :closable="false" style="margin-bottom:12px">
              定义站点部署时所需的环境变量（.env 文件），每个变量对应一个用户填写项。
            </el-alert>
            <el-table :data="envTemplateRows" border size="small" style="margin-bottom:8px">
              <el-table-column label="变量名" min-width="180">
                <template #default="{ row }">
                  <el-input v-model="row.key" size="small" placeholder="NEXT_PUBLIC_XXX" />
                </template>
              </el-table-column>
              <el-table-column label="标签" width="120">
                <template #default="{ row }">
                  <el-input v-model="row.label" size="small" placeholder="显示名称" />
                </template>
              </el-table-column>
              <el-table-column label="默认值" min-width="140">
                <template #default="{ row }">
                  <el-input v-model="row.defaultValue" size="small" placeholder="默认值" />
                </template>
              </el-table-column>
              <el-table-column label="类型" width="100">
                <template #default="{ row }">
                  <el-select v-model="row.type" size="small" style="width:100%">
                    <el-option label="文本" value="text" />
                    <el-option label="选择" value="select" />
                    <el-option label="布尔" value="boolean" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="必填" width="60" align="center">
                <template #default="{ row }">
                  <el-checkbox v-model="row.required" />
                </template>
              </el-table-column>
              <el-table-column label="说明" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.description" size="small" placeholder="变量说明" />
                </template>
              </el-table-column>
              <el-table-column label="" width="50" align="center">
                <template #default="{ $index }">
                  <el-button link type="danger" icon="Delete" size="small" @click="envTemplateRows.splice($index, 1)" />
                </template>
              </el-table-column>
            </el-table>
            <el-button size="small" icon="Plus" @click="envTemplateRows.push({ key: '', label: '', defaultValue: '', type: 'text', required: false, description: '' })">添加变量</el-button>
          </div>
        </el-tab-pane>

        <!-- Tab 3：配置文件映射 -->
        <el-tab-pane label="配置文件映射" name="configMappings">
          <div style="padding:4px 0 8px">
            <el-alert type="info" show-icon :closable="false" style="margin-bottom:8px">
              <template #title>自动替换：wrangler.toml → <code>name</code> 字段</template>
              向导建站时，系统会将代码仓库中 <code>wrangler.toml</code> 的 <code>name</code> 字段自动替换为用户配置的 CF 项目名称（即 Worker 名）。<br />
              此替换在初次克隆模板时执行一次，用户克隆后可直接在编辑器中看到正确的名称，无需额外配置 GitHub Secret。
            </el-alert>
            <el-alert type="warning" show-icon :closable="false" style="margin-bottom:12px">
              <template #title>
                配置文件映射定义用户可通过「站点配置编辑器」修改的 TypeScript 配置变量。
                系统将根据此映射自动定位并替换代码仓库中对应文件的变量值。
              </template>
              每个映射项指定：文件路径（相对代码仓库根目录）及要定位和替换的变量属性键 <code>tsKey</code>。
              对于复杂嵌套结构，可提供自定义正则 <code>searchRegex</code> 和替换模板 <code>replaceTpl</code>。
            </el-alert>
            <el-table :data="configMappingRows" border size="small" style="margin-bottom:8px">
              <el-table-column label="键名(key)" width="110">
                <template #default="{ row }">
                  <el-input v-model="row.key" size="small" placeholder="唯一标识" />
                </template>
              </el-table-column>
              <el-table-column label="标签" width="100">
                <template #default="{ row }">
                  <el-input v-model="row.label" size="small" placeholder="显示名称" />
                </template>
              </el-table-column>
              <el-table-column label="类型" width="90">
                <template #default="{ row }">
                  <el-select v-model="row.type" size="small" style="width:100%">
                    <el-option label="文本" value="text" />
                    <el-option label="颜色" value="color" />
                    <el-option label="长文本" value="textarea" />
                    <el-option label="数字" value="number" />
                    <el-option label="开关" value="switch" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="分组" width="90">
                <template #default="{ row }">
                  <el-input v-model="row.group" size="small" placeholder="品牌信息" />
                </template>
              </el-table-column>
              <el-table-column label="文件路径" min-width="200">
                <template #default="{ row }">
                  <el-input v-model="row.filePath" size="small" placeholder="src/config/customize/site.ts" />
                </template>
              </el-table-column>
              <el-table-column label="属性键(tsKey)" width="130">
                <template #default="{ row }">
                  <el-tooltip content="简单属性名，如 siteName / primaryColor。系统自动生成正则" placement="top">
                    <el-input v-model="row.tsKey" size="small" placeholder="siteName" />
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column label="自定义正则" width="140">
                <template #default="{ row }">
                  <el-tooltip content="可选，复杂场景下覆盖自动正则。需包含3个捕获组：前缀/值/后缀" placement="top">
                    <el-input v-model="row.searchRegex" size="small" placeholder="自定义正则（可选）" />
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column label="替换模板" width="110">
                <template #default="{ row }">
                  <el-tooltip content="可选，默认 $1$VALUE$3。$VALUE 替换为新值" placement="top">
                    <el-input v-model="row.replaceTpl" size="small" placeholder="$1$VALUE$3" />
                  </el-tooltip>
                </template>
              </el-table-column>
              <el-table-column label="默认值" width="100">
                <template #default="{ row }">
                  <el-input v-model="row.defaultValue" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="必填" width="55" align="center">
                <template #default="{ row }">
                  <el-checkbox v-model="row.required" />
                </template>
              </el-table-column>
              <el-table-column label="说明" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.description" size="small" placeholder="字段用途说明" />
                </template>
              </el-table-column>
              <el-table-column label="" width="50" align="center">
                <template #default="{ $index }">
                  <el-button link type="danger" icon="Delete" size="small" @click="configMappingRows.splice($index, 1)" />
                </template>
              </el-table-column>
            </el-table>
            <el-button size="small" icon="Plus" @click="configMappingRows.push({ key: '', label: '', type: 'text', group: '', filePath: '', tsKey: '', searchRegex: '', replaceTpl: '', defaultValue: '', required: false, description: '' })">添加映射项</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="SiteTemplates">
import { listSiteTemplates, getSiteTemplate, addSiteTemplate, updateSiteTemplate, delSiteTemplate } from '@/api/gamebox/siteTemplate'
import { listEnabledGitAccounts } from '@/api/gamebox/gitAccount'

const { proxy } = getCurrentInstance()

const templateList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    siteFunctionType: undefined
  },
  rules: {
    name: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
    siteFunctionType: [{ required: true, message: '请选择站点类型', trigger: 'change' }],
    templateGitUrl: [{ required: true, message: '请输入 Git 地址', trigger: 'blur' }]
  }
})
const { queryParams, form, rules } = toRefs(data)

// 对话框当前 Tab
const dialogTab = ref('basic')

// Git 账号列表
const gitAccounts = ref([])
const gitAccountsLoading = ref(false)

function loadGitAccounts() {
  gitAccountsLoading.value = true
  listEnabledGitAccounts().then(res => {
    gitAccounts.value = res.data || []
  }).catch(() => { gitAccounts.value = [] }).finally(() => { gitAccountsLoading.value = false })
}

// 环境变量模板结构化编辑
const envTemplateRows = ref([])

// 配置文件映射结构化编辑
const configMappingRows = ref([])

// 解析 envTemplate JSON 字符串到结构化行
function parseEnvTemplate(json) {
  if (!json) return []
  try {
    const parsed = JSON.parse(json)
    if (Array.isArray(parsed)) return parsed.map(r => ({ key: r.key || '', label: r.label || '', defaultValue: r.defaultValue || '', type: r.type || 'text', required: !!r.required, description: r.description || '' }))
  } catch {}
  return []
}

// 将结构化行序列化为 JSON 字符串
function serializeEnvTemplate(rows) {
  const valid = rows.filter(r => r.key && r.key.trim())
  if (!valid.length) return ''
  return JSON.stringify(valid)
}

// 解析 configMappings JSON 字符串到结构化行
function parseConfigMappings(json) {
  if (!json) return []
  try {
    const parsed = JSON.parse(json)
    if (Array.isArray(parsed)) return parsed.map(r => ({
      key: r.key || '', label: r.label || '', type: r.type || 'text',
      group: r.group || '', filePath: r.filePath || '', tsKey: r.tsKey || '',
      searchRegex: r.searchRegex || '', replaceTpl: r.replaceTpl || '',
      defaultValue: r.defaultValue || '', required: !!r.required, description: r.description || ''
    }))
  } catch {}
  return []
}

// 将配置映射行序列化为 JSON 字符串
function serializeConfigMappings(rows) {
  const valid = rows.filter(r => r.key && r.key.trim() && r.filePath && r.filePath.trim())
  if (!valid.length) return ''
  return JSON.stringify(valid.map(r => {
    const item = { key: r.key, label: r.label, type: r.type, group: r.group, filePath: r.filePath }
    if (r.tsKey) item.tsKey = r.tsKey
    if (r.searchRegex) item.searchRegex = r.searchRegex
    if (r.replaceTpl) item.replaceTpl = r.replaceTpl
    if (r.defaultValue) item.defaultValue = r.defaultValue
    if (r.required) item.required = true
    if (r.description) item.description = r.description
    return item
  }))
}

function getList() {
  loading.value = true
  listSiteTemplates(queryParams.value).then(res => {
    templateList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    siteFunctionType: 'seo_site',
    industry: undefined,
    description: undefined,
    previewImage: undefined,
    templateGitUrl: undefined,
    templateGitBranch: 'main',
    gitAccountId: undefined,
    entryDir: undefined,
    framework: 'Next.js',
    envTemplate: undefined,
    sortOrder: 0,
    status: '1',
    remark: undefined
  }
  proxy.resetForm('templateRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  envTemplateRows.value = []
  configMappingRows.value = []
  dialogTab.value = 'basic'
  loadGitAccounts()
  open.value = true
  title.value = '添加站点模板'
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getSiteTemplate(id).then(res => {
    form.value = { ...res.data }
    envTemplateRows.value = parseEnvTemplate(res.data.envTemplate)
    configMappingRows.value = parseConfigMappings(res.data.configMappings)
    dialogTab.value = 'basic'
    loadGitAccounts()
    open.value = true
    title.value = '修改站点模板'
  })
}

function handleDelete(row) {
  const delIds = row.id ? [row.id] : ids.value
  proxy.$modal.confirm('是否确认删除所选模板？').then(() => {
    return delSiteTemplate(delIds.join(','))
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function submitForm() {
  proxy.$refs.templateRef.validate(valid => {
    if (!valid) return
    // 将结构化行序列化回 JSON
    form.value.envTemplate = serializeEnvTemplate(envTemplateRows.value) || null
    form.value.configMappings = serializeConfigMappings(configMappingRows.value) || null
    if (form.value.id) {
      updateSiteTemplate(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        open.value = false
        getList()
      })
    } else {
      addSiteTemplate(form.value).then(() => {
        proxy.$modal.msgSuccess('新增成功')
        open.value = false
        getList()
      })
    }
  })
}

getList()
</script>
