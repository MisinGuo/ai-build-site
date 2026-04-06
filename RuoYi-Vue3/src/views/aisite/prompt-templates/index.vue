<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入模板名称" clearable style="width:180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="模板编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入编码" clearable style="width:150px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="场景" prop="scene">
        <el-input v-model="queryParams.scene" placeholder="场景" clearable style="width:120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="平台" prop="platformType">
        <el-select v-model="queryParams.platformType" placeholder="请选择" clearable style="width:130px">
          <el-option label="OpenAI" value="openai" />
          <el-option label="Azure" value="azure" />
          <el-option label="Anthropic" value="anthropic" />
          <el-option label="通义千问" value="qwen" />
          <el-option label="DeepSeek" value="deepseek" />
          <el-option label="通用" value="any" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width:110px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:promptTemplate:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:promptTemplate:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:promptTemplate:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="模板名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="模板编码" align="center" prop="code" width="200" :show-overflow-tooltip="true" />
      <el-table-column label="场景" align="center" prop="scene" width="120" />
      <el-table-column label="适用平台" align="center" prop="platformType" width="120" />
      <el-table-column label="内置" align="center" prop="isBuiltin" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isBuiltin === '1' ? 'primary' : 'info'" size="small">
            {{ row.isBuiltin === '1' ? '内置' : '自定义' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '1' ? 'success' : 'danger'" size="small">
            {{ row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['aisite:promptTemplate:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['aisite:promptTemplate:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="templateRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="模板名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板编码" prop="code">
              <el-input v-model="form.code" placeholder="如 article_seo" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应用场景" prop="scene">
              <el-input v-model="form.scene" placeholder="如 article / seo / translation" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用平台">
              <el-select v-model="form.platformType" clearable style="width:100%" placeholder="通用（不限）">
                <el-option label="OpenAI" value="openai" />
                <el-option label="Azure OpenAI" value="azure" />
                <el-option label="Anthropic" value="anthropic" />
                <el-option label="通义千问" value="qwen" />
                <el-option label="DeepSeek" value="deepseek" />
                <el-option label="通用" value="any" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio value="1">启用</el-radio>
                <el-radio value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="System Prompt" prop="systemPrompt">
              <el-input v-model="form.systemPrompt" type="textarea" :rows="4"
                placeholder="你是一个专业的SEO内容创作者，请根据用户提供的关键词生成高质量的文章..." />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="User Prompt 模板" prop="userPrompt">
              <el-input v-model="form.userPrompt" type="textarea" :rows="5"
                placeholder="请为以下关键词生成一篇文章：\n关键词：{{keywords}}\n目标语言：{{language}}" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="变量定义 JSON">
              <el-input v-model="form.variables" type="textarea" :rows="3"
                placeholder='[{"name":"keywords","label":"关键词","type":"string","required":true}]' />
            </el-form-item>
          </el-col>
        </el-row>
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
import { listPromptTemplate, getPromptTemplate, addPromptTemplate, updatePromptTemplate, delPromptTemplate } from '@/api/aisite/promptTemplate'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const templateList = ref([])
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const title = ref('')
const queryRef = ref()
const templateRef = ref()

const queryParams = reactive({ pageNum: 1, pageSize: 10, name: undefined, code: undefined, scene: undefined, platformType: undefined, status: undefined })
const form = ref({})
const rules = {
  name: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listPromptTemplate(queryParams).then(res => {
    templateList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryRef.value?.resetFields(); handleQuery() }

function handleSelectionChange(sel) {
  ids.value = sel.map(s => s.id)
  single.value = sel.length !== 1
  multiple.value = !sel.length
}

function handleAdd() {
  form.value = { status: '1', isBuiltin: '0' }
  open.value = true
  title.value = '新增 Prompt 模板'
}

function handleUpdate(row) {
  const id = row?.id || ids.value[0]
  getPromptTemplate(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改 Prompt 模板'
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm('确认删除选中的 Prompt 模板吗？', '提示', { type: 'warning' }).then(() => {
    delPromptTemplate(delIds.join(',')).then(() => { ElMessage.success('删除成功'); getList() })
  })
}

function cancel() { open.value = false; form.value = {} }

function submitForm() {
  templateRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updatePromptTemplate : addPromptTemplate
    fn(form.value).then(() => {
      ElMessage.success('保存成功')
      open.value = false
      getList()
    })
  })
}

onMounted(getList)
</script>
