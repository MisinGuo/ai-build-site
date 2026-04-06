<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="矩阵组名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入矩阵组名称" clearable style="width:200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="行业" prop="industry">
        <el-select v-model="queryParams.industry" placeholder="全部行业" clearable style="width:130px">
          <el-option label="游戏" value="game" />
          <el-option label="短剧" value="drama" />
          <el-option label="电商" value="product" />
          <el-option label="房产" value="property" />
          <el-option label="通用" value="general" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width:120px">
          <el-option label="草稿" value="draft" />
          <el-option label="建站中" value="building" />
          <el-option label="已上线" value="live" />
          <el-option label="已暂停" value="paused" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:matrix:add']">新增矩阵组</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:matrix:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:matrix:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 矩阵组卡片统计 -->
    <el-row :gutter="16" class="mb16" v-if="total > 0">
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-value">{{ total }}</div>
          <div class="stat-label">矩阵组总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-blue" shadow="never">
          <div class="stat-value">{{ totalSites }}</div>
          <div class="stat-label">站点总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-green" shadow="never">
          <div class="stat-value">{{ liveSites }}</div>
          <div class="stat-label">已上线站点</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card stat-orange" shadow="never">
          <div class="stat-value">{{ buildingSites }}</div>
          <div class="stat-label">建站进行中</div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="matrixList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="矩阵组名称" align="center" prop="name" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="行业" align="center" prop="industry" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="industryTagType(row.industry)">{{ industryLabel(row.industry) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="域名模式" align="center" prop="domainPattern" :show-overflow-tooltip="true" />
      <el-table-column label="关键词数" align="center" width="90">
        <template #default="{ row }">
          {{ keywordCount(row.keywordList) }}
        </template>
      </el-table-column>
      <el-table-column label="站点数" align="center" width="80">
        <template #default="{ row }">
          <span class="count-built">{{ row.builtCount || 0 }}</span>
          <span class="count-sep">/</span>
          <span class="count-total">{{ row.totalCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已上线" align="center" prop="liveCount" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['aisite:matrix:edit']">修改</el-button>
          <el-button link type="success" icon="VideoPlay" @click="handleBuild(row)" v-hasPermi="['aisite:matrix:edit']">建站</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['aisite:matrix:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="matrixRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="矩阵组名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入矩阵组名称" />
        </el-form-item>
        <el-form-item label="行业" prop="industry">
          <el-select v-model="form.industry" placeholder="请选择行业" style="width:100%">
            <el-option label="游戏" value="game" />
            <el-option label="短剧" value="drama" />
            <el-option label="电商" value="product" />
            <el-option label="房产" value="property" />
            <el-option label="通用" value="general" />
          </el-select>
        </el-form-item>
        <el-form-item label="域名模式" prop="domainPattern">
          <el-input v-model="form.domainPattern" placeholder="如 {keyword}.example.com" />
        </el-form-item>
        <el-form-item label="关键词列表" prop="keywordList">
          <el-input v-model="keywordInput" type="textarea" :rows="5"
            placeholder="每行一个关键词，如：&#10;手游推荐&#10;免费游戏&#10;好玩手游"
            @change="syncKeywords" />
          <div class="form-tip">已匹配 {{ keywordCount(form.keywordList) }} 个关键词</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="draft">草稿</el-radio>
            <el-radio value="live">已上线</el-radio>
            <el-radio value="paused">已暂停</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="可选描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMatrixGroup, getMatrixGroup, addMatrixGroup, updateMatrixGroup, delMatrixGroup } from '@/api/aisite/matrixGroup'
import { parseTime } from '@/utils/ruoyi'

const loading = ref(false)
const showSearch = ref(true)
const matrixList = ref([])
const total = ref(0)
const open = ref(false)
const title = ref('')
const single = ref(true)
const multiple = ref(true)
const queryRef = ref(null)
const matrixRef = ref(null)
const keywordInput = ref('')
const selectedIds = ref([])

const queryParams = reactive({
  pageNum: 1, pageSize: 10,
  name: null, industry: null, status: null
})

const form = ref({})
const rules = reactive({
  name: [{ required: true, message: '请输入矩阵组名称', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业', trigger: 'change' }]
})

const totalSites = computed(() => matrixList.value.reduce((s, r) => s + (r.totalCount || 0), 0))
const liveSites = computed(() => matrixList.value.reduce((s, r) => s + (r.liveCount || 0), 0))
const buildingSites = computed(() => matrixList.value.filter(r => r.status === 'building').length)

function industryLabel(v) {
  const map = { game: '游戏', drama: '短剧', product: '电商', property: '房产', general: '通用' }
  return map[v] || v
}
function industryTagType(v) {
  const map = { game: 'primary', drama: 'warning', product: 'success', property: 'info', general: '' }
  return map[v] || ''
}
function statusLabel(v) {
  const map = { draft: '草稿', building: '建站中', live: '已上线', paused: '已暂停' }
  return map[v] || v
}
function statusTagType(v) {
  const map = { draft: 'info', building: 'warning', live: 'success', paused: 'danger' }
  return map[v] || ''
}
function keywordCount(json) {
  if (!json) return 0
  try {
    const arr = typeof json === 'string' ? JSON.parse(json) : json
    return Array.isArray(arr) ? arr.length : 0
  } catch { return 0 }
}

function syncKeywords() {
  const lines = keywordInput.value.split('\n').map(s => s.trim()).filter(Boolean)
  form.value.keywordList = JSON.stringify(lines)
}

function resetForm() {
  form.value = { industry: 'general', status: 'draft', totalCount: 0, builtCount: 0, liveCount: 0 }
  keywordInput.value = ''
}

function getList() {
  loading.value = true
  listMatrixGroup(queryParams).then(res => {
    matrixList.value = res.rows
    total.value = res.total
  }).finally(() => loading.value = false)
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryRef.value?.resetFields(); handleQuery() }

function handleSelectionChange(sel) {
  selectedIds.value = sel.map(r => r.id)
  single.value = sel.length !== 1
  multiple.value = !sel.length
}

function handleAdd() {
  resetForm()
  open.value = true
  title.value = '新增矩阵站组'
}

function handleUpdate(row) {
  const id = row?.id || selectedIds.value[0]
  getMatrixGroup(id).then(res => {
    form.value = { ...res.data }
    const arr = keywordCount(form.value.keywordList) > 0
      ? JSON.parse(form.value.keywordList) : []
    keywordInput.value = arr.join('\n')
    open.value = true
    title.value = '编辑矩阵站组'
  })
}

function handleDelete(row) {
  const ids = row?.id ? [row.id] : selectedIds.value
  ElMessageBox.confirm(`确认删除所选矩阵站组吗？`, '提示', { type: 'warning' }).then(() => {
    delMatrixGroup(ids.join(',')).then(() => {
      getList()
      ElMessage.success('删除成功')
    })
  })
}

function handleBuild(row) {
  ElMessageBox.confirm(`确认对「${row.name}」发起批量建站任务？`, '提示', { type: 'info' }).then(() => {
    updateMatrixGroup({ id: row.id, status: 'building', updateBy: '' }).then(() => {
      ElMessage.success('建站任务已提交')
      getList()
    })
  })
}

function cancel() { open.value = false; resetForm() }

function submitForm() {
  matrixRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updateMatrixGroup : addMatrixGroup
    fn(form.value).then(() => {
      ElMessage.success(form.value.id ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

onMounted(getList)
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.stat-card { text-align: center; padding: 8px 0; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.stat-blue .stat-value { color: #409eff; }
.stat-green .stat-value { color: #67c23a; }
.stat-orange .stat-value { color: #e6a23c; }
.count-built { color: #67c23a; font-weight: 600; }
.count-sep { color: #c0c4cc; margin: 0 2px; }
.count-total { color: #909399; }
.form-tip { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
