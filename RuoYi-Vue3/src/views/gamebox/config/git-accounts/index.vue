<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="账号名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入账号名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="Git平台" prop="provider">
        <el-select v-model="queryParams.provider" placeholder="全部" clearable style="width:110px">
          <el-option label="GitHub" value="github" />
          <el-option label="Gitee" value="gitee" />
          <el-option label="GitLab" value="gitlab" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:90px">
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:gitAccount:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:gitAccount:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:gitAccount:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="账号名称" align="center" prop="name" min-width="130" />
      <el-table-column label="Git平台" align="center" prop="provider" width="100">
        <template #default="scope">
          <el-tag :type="providerTagType(scope.row.provider)" size="small">{{ scope.row.provider }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="用户名/组织" align="center" prop="account" min-width="130" />
      <el-table-column label="Token" align="center" prop="token" width="160">
        <template #default="scope">
          <span class="token-masked">{{ scope.row.token }}</span>
        </template>
      </el-table-column>
      <el-table-column label="默认" align="center" prop="isDefault" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.isDefault === 1" type="warning" size="small">默认</el-tag>
          <span v-else style="color:#c0c4cc">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" size="small">
            {{ scope.row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160">
        <template #default="scope">
          <el-tooltip content="测试连接" placement="top">
            <el-button link type="primary" icon="Connection" @click="handleVerify(scope.row)" v-hasPermi="['gamebox:gitAccount:query']" />
          </el-tooltip>
          <el-tooltip content="修改" placement="top">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:gitAccount:edit']" />
          </el-tooltip>
          <el-tooltip content="设为默认" placement="top">
            <el-button link type="warning" icon="Star" @click="handleSetDefault(scope.row)" v-hasPermi="['gamebox:gitAccount:edit']" :disabled="scope.row.isDefault === 1" />
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:gitAccount:remove']" />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="520px" append-to-body>
      <el-form ref="accountRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="账号名称" prop="name">
          <el-input v-model="form.name" placeholder="如：公司主GitHub账号" />
        </el-form-item>
        <el-form-item label="Git平台" prop="provider">
          <el-select v-model="form.provider" style="width:100%">
            <el-option label="GitHub" value="github" />
            <el-option label="Gitee" value="gitee" />
            <el-option label="GitLab" value="gitlab" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名/组织" prop="account">
          <el-input v-model="form.account" placeholder="GitHub 用户名或组织名" />
        </el-form-item>
        <el-form-item label="Access Token" prop="token">
          <el-input
            v-model="form.token"
            type="password"
            show-password
            :placeholder="form.id ? '不填则保持原Token不变' : '请输入 Personal Access Token'"
          />
          <div v-if="tokenUrl" class="form-tip">
            <el-link type="primary" :href="tokenUrl" target="_blank" :underline="false">{{ tokenUrlLabel }}</el-link>
            <span v-if="form.provider === 'github'">（已预填 repo、workflow 权限）</span>
          </div>
        </el-form-item>
        <el-form-item label="设为默认" prop="isDefault">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
          <span class="ml8" style="color:#909399;font-size:12px">设为默认后向导将自动选择此账号</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="可选备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listGitAccounts, getGitAccount, addGitAccount, updateGitAccount,
  deleteGitAccounts, setDefaultGitAccount, verifyGitAccount
} from '@/api/gamebox/gitAccount'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const accountList = ref([])
const open = ref(false)
const title = ref('')
const single = ref(true)
const multiple = ref(true)
const ids = ref([])
const queryRef = ref(null)
const accountRef = ref(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, name: '', provider: '', status: '' })

const form = ref({})
const rules = reactive({
  name: [{ required: true, message: '账号名称不能为空', trigger: 'blur' }],
  provider: [{ required: true, message: '请选择Git平台', trigger: 'change' }],
  account: [{ required: true, message: '用户名/组织名不能为空', trigger: 'blur' }],
  token: [{ required: false, trigger: 'blur' }]
})

function providerTagType(provider) {
  return { github: '', gitee: 'danger', gitlab: 'warning' }[provider] || 'info'
}

const tokenUrl = computed(() => {
  const urls = {
    github: 'https://github.com/settings/tokens/new?scopes=repo,workflow&description=GameBox',
    gitee: 'https://gitee.com/profile/personal_access_tokens/new',
    gitlab: 'https://gitlab.com/-/profile/personal_access_tokens',
  }
  return urls[form.value.provider] || null
})

const tokenUrlLabel = computed(() => {
  const labels = {
    github: '前往 GitHub 创建 Personal Access Token →',
    gitee: '前往 Gitee 创建私人令牌 →',
    gitlab: '前往 GitLab 创建 Access Token →',
  }
  return labels[form.value.provider] || '前往创建 Token →'
})

function getList() {
  loading.value = true
  listGitAccounts(queryParams).then(res => {
    accountList.value = res.rows
    total.value = res.total
  }).finally(() => { loading.value = false })
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryRef.value?.resetFields(); handleQuery() }

function handleSelectionChange(selection) {
  ids.value = selection.map(s => s.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function resetForm() {
  form.value = { id: undefined, name: '', provider: 'github', account: '', token: '', isDefault: 0, status: '1', remark: '' }
  accountRef.value?.resetFields()
}

function handleAdd() {
  resetForm()
  title.value = '新增Git账号'
  open.value = true
}

function handleUpdate(row) {
  const id = row?.id || ids.value[0]
  getGitAccount(id).then(res => {
    form.value = { ...res.data, token: '' }
    title.value = '修改Git账号'
    open.value = true
  })
}

function cancel() { open.value = false; resetForm() }

function submitForm() {
  accountRef.value?.validate(valid => {
    if (!valid) return
    const data = { ...form.value }
    if (data.id) {
      updateGitAccount(data).then(() => { ElMessage.success('修改成功'); open.value = false; getList() })
    } else {
      if (!data.token) { ElMessage.warning('请填写 Access Token'); return }
      addGitAccount(data).then(() => { ElMessage.success('新增成功'); open.value = false; getList() })
    }
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm(`确认删除所选Git账号？`, '警告', { type: 'warning' }).then(() => {
    deleteGitAccounts(delIds.join(',')).then(() => { ElMessage.success('删除成功'); getList() })
  })
}

function handleSetDefault(row) {
  ElMessageBox.confirm(`将「${row.name}」设为默认Git账号？`, '确认', { type: 'info' }).then(() => {
    setDefaultGitAccount(row.id).then(() => { ElMessage.success('已设为默认'); getList() })
  })
}

function handleVerify(row) {
  ElMessage({ message: '验证中…', type: 'info', duration: 1500 })
  verifyGitAccount(row.id).then(res => {
    const d = res.data || {}
    ElMessageBox.alert(d.message || '无返回信息', d.success ? '✓ 验证通过' : '✗ 验证失败',
      { type: d.success ? 'success' : 'error', confirmButtonText: '知道了' })
  }).catch(e => {
    ElMessageBox.alert('请求异常：' + e.message, '验证失败', { type: 'error', confirmButtonText: '知道了' })
  })
}

getList()
</script>

<style scoped>
.token-masked { font-family: monospace; color: #909399; }
.ml8 { margin-left: 8px; }
.form-tip { margin-top: 4px; font-size: 12px; color: #909399; line-height: 1.4; }
</style>
