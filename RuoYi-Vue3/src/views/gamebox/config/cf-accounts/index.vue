<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="账号名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入账号名称" clearable @keyup.enter="handleQuery" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:cfAccount:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:cfAccount:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:cfAccount:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="账号名称" align="center" prop="name" min-width="140" />
      <el-table-column label="Account ID" align="center" prop="accountId" min-width="200" :show-overflow-tooltip="true" />
      <el-table-column label="API Token" align="center" prop="apiToken" width="160">
        <template #default="scope">
          <span class="token-masked">{{ scope.row.apiToken }}</span>
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
      <el-table-column label="备注" align="center" prop="remark" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="160">
        <template #default="scope">
          <el-tooltip content="测试连接" placement="top">
            <el-button link type="primary" icon="Connection" @click="handleVerify(scope.row)" v-hasPermi="['gamebox:cfAccount:query']" />
          </el-tooltip>
          <el-tooltip content="修改" placement="top">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:cfAccount:edit']" />
          </el-tooltip>
          <el-tooltip content="设为默认" placement="top">
            <el-button link type="warning" icon="Star" @click="handleSetDefault(scope.row)" v-hasPermi="['gamebox:cfAccount:edit']" :disabled="scope.row.isDefault === 1" />
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:cfAccount:remove']" />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="520px" append-to-body>
      <el-form ref="accountRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="账号名称" prop="name">
          <el-input v-model="form.name" placeholder="如：主CF账号" />
        </el-form-item>
        <el-form-item label="Account ID" prop="accountId">
          <el-input v-model="form.accountId" placeholder="Cloudflare Account ID" />
          <div class="form-tip">
            在 <el-link type="primary" href="https://dash.cloudflare.com" target="_blank">Cloudflare 控制台</el-link> 登录后，右侧边栏可找到 Account ID
          </div>
        </el-form-item>
        <el-form-item label="API Token" prop="apiToken">
          <el-input
            v-model="form.apiToken"
            type="password"
            show-password
            :placeholder="form.id ? '不填则保持原Token不变' : '请输入 Cloudflare API Token'"
          />
          <div class="form-tip">
            <el-link type="primary" href="https://dash.cloudflare.com/profile/api-tokens" target="_blank" :underline="false">前往 Cloudflare 创建 API Token →</el-link>
            &nbsp;（推荐选 Edit Cloudflare Workers 模板）
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
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCfAccounts, getCfAccount, addCfAccount, updateCfAccount,
  deleteCfAccounts, setDefaultCfAccount, verifyCfAccount
} from '@/api/gamebox/cfAccount'

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

const queryParams = reactive({ pageNum: 1, pageSize: 10, name: '', status: '' })

const form = ref({})
const rules = reactive({
  name: [{ required: true, message: '账号名称不能为空', trigger: 'blur' }],
  accountId: [{ required: true, message: 'Account ID不能为空', trigger: 'blur' }],
})

function getList() {
  loading.value = true
  listCfAccounts(queryParams).then(res => {
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
  form.value = { id: undefined, name: '', accountId: '', apiToken: '', isDefault: 0, status: '1', remark: '' }
  accountRef.value?.resetFields()
}

function handleAdd() {
  resetForm()
  title.value = '新增CF账号'
  open.value = true
}

function handleUpdate(row) {
  const id = row?.id || ids.value[0]
  getCfAccount(id).then(res => {
    form.value = { ...res.data, apiToken: '' }
    title.value = '修改CF账号'
    open.value = true
  })
}

function cancel() { open.value = false; resetForm() }

function submitForm() {
  accountRef.value?.validate(valid => {
    if (!valid) return
    const data = { ...form.value }
    if (data.id) {
      updateCfAccount(data).then(() => { ElMessage.success('修改成功'); open.value = false; getList() })
    } else {
      if (!data.apiToken) { ElMessage.warning('请填写 API Token'); return }
      addCfAccount(data).then(() => { ElMessage.success('新增成功'); open.value = false; getList() })
    }
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm(`确认删除所选CF账号？`, '警告', { type: 'warning' }).then(() => {
    deleteCfAccounts(delIds.join(',')).then(() => { ElMessage.success('删除成功'); getList() })
  })
}

function handleSetDefault(row) {
  ElMessageBox.confirm(`将「${row.name}」设为默认CF账号？`, '确认', { type: 'info' }).then(() => {
    setDefaultCfAccount(row.id).then(() => { ElMessage.success('已设为默认'); getList() })
  })
}

function handleVerify(row) {
  ElMessage({ message: '验证中…', type: 'info', duration: 1500 })
  verifyCfAccount(row.id).then(res => {
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
