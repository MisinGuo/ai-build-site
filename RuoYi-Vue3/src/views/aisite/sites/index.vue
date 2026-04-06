<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="站点名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入站点名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="域名" prop="domain">
        <el-input v-model="queryParams.domain" placeholder="请输入域名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="行业" prop="industry">
        <el-input v-model="queryParams.industry" placeholder="请输入行业标识" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:site:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:site:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:site:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="siteList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="站点名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="域名" align="center" prop="domain" :show-overflow-tooltip="true" />
      <el-table-column label="行业" align="center" prop="industry" width="120" />
      <el-table-column label="默认语言" align="center" prop="defaultLocale" width="100" />
      <el-table-column label="部署状态" align="center" prop="deployStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.deployStatus === 'published' ? 'success' : scope.row.deployStatus === 'deploying' ? 'warning' : 'info'">
            {{ scope.row.deployStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
            {{ scope.row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['aisite:site:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['aisite:site:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="siteRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="站点名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入站点名称" />
        </el-form-item>
        <el-form-item label="域名" prop="domain">
          <el-input v-model="form.domain" placeholder="例：example.com" />
        </el-form-item>
        <el-form-item label="行业标识" prop="industry">
          <el-input v-model="form.industry" placeholder="例：game / house / car / hotel" />
        </el-form-item>
        <el-form-item label="Logo URL" prop="logoUrl">
          <el-input v-model="form.logoUrl" placeholder="请输入 Logo 图片 URL" />
        </el-form-item>
        <el-form-item label="站点描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入站点描述" />
        </el-form-item>
        <el-form-item label="SEO 标题" prop="seoTitle">
          <el-input v-model="form.seoTitle" placeholder="请输入 SEO 标题" />
        </el-form-item>
        <el-form-item label="SEO 关键词" prop="seoKeywords">
          <el-input v-model="form.seoKeywords" placeholder="请输入 SEO 关键词，逗号分隔" />
        </el-form-item>
        <el-form-item label="SEO 描述" prop="seoDescription">
          <el-input v-model="form.seoDescription" type="textarea" :rows="2" placeholder="请输入 SEO 描述" />
        </el-form-item>
        <el-form-item label="默认语言" prop="defaultLocale">
          <el-select v-model="form.defaultLocale" placeholder="请选择默认语言">
            <el-option label="中文 (zh-CN)" value="zh-CN" />
            <el-option label="English (en)" value="en" />
            <el-option label="日本語 (ja)" value="ja" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listSite, getSite, addSite, updateSite, delSite } from '@/api/aisite/site'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const siteList = ref([])
const open = ref(false)
const title = ref('')
const single = ref(true)
const multiple = ref(true)
const ids = ref([])

const queryRef = ref()
const siteRef = ref()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  domain: undefined,
  industry: undefined,
  status: undefined
})

const form = ref({})

const rules = {
  name: [{ required: true, message: '站点名称不能为空', trigger: 'blur' }],
  domain: [{ required: true, message: '域名不能为空', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listSite(queryParams).then(res => {
    siteList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  queryRef.value?.resetFields()
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(s => s.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function reset() {
  form.value = { status: '1', industry: 'general', defaultLocale: 'zh-CN' }
  siteRef.value?.resetFields()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增站点'
}

function handleUpdate(row) {
  reset()
  const id = row?.id || ids.value[0]
  getSite(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改站点'
  })
}

function cancel() {
  open.value = false
  reset()
}

function submitForm() {
  siteRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updateSite : addSite
    fn(form.value).then(() => {
      ElMessage.success(form.value.id ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm('确认删除所选站点吗？', '提示', { type: 'warning' }).then(() => {
    delSite(delIds.join(',')).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

onMounted(getList)
</script>
