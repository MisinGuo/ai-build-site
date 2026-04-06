<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="工具名称" prop="toolName">
        <el-input v-model="queryParams.toolName" placeholder="请输入工具名称" clearable style="width:180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="工具编码" prop="toolCode">
        <el-input v-model="queryParams.toolCode" placeholder="请输入工具编码" clearable style="width:160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input v-model="queryParams.category" placeholder="分类" clearable style="width:130px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="isEnabled">
        <el-select v-model="queryParams.isEnabled" placeholder="请选择" clearable style="width:110px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:atomicTool:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:atomicTool:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:atomicTool:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="toolList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="工具名称" align="center" prop="toolName" :show-overflow-tooltip="true" />
      <el-table-column label="工具编码" align="center" prop="toolCode" width="200" :show-overflow-tooltip="true" />
      <el-table-column label="分类" align="center" prop="category" width="130" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="内置" align="center" prop="isBuiltin" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isBuiltin === '1' ? 'primary' : 'info'" size="small">
            {{ row.isBuiltin === '1' ? '内置' : '自定义' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="isEnabled" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.isEnabled" active-value="1" inactive-value="0" @change="handleStatusChange(row)" />
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['aisite:atomicTool:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['aisite:atomicTool:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="toolRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工具名称" prop="toolName">
              <el-input v-model="form.toolName" placeholder="请输入工具名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工具编码" prop="toolCode">
              <el-input v-model="form.toolCode" placeholder="如 fetch_url" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-input v-model="form.category" placeholder="如 http / llm / text" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-switch v-model="form.isEnabled" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入工具描述" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="输入 Schema" prop="inputSchema">
              <el-input v-model="form.inputSchema" type="textarea" :rows="5" placeholder='{"type":"object","properties":{"url":{"type":"string"}}}' />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="输出 Schema" prop="outputSchema">
              <el-input v-model="form.outputSchema" type="textarea" :rows="5" placeholder='{"type":"object","properties":{"content":{"type":"string"}}}' />
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
import { listAtomicTool, getAtomicTool, addAtomicTool, updateAtomicTool, delAtomicTool } from '@/api/aisite/atomicTool'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const toolList = ref([])
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const title = ref('')
const queryRef = ref()
const toolRef = ref()

const queryParams = reactive({ pageNum: 1, pageSize: 10, toolName: undefined, toolCode: undefined, category: undefined, isEnabled: undefined })
const form = ref({})
const rules = {
  toolName: [{ required: true, message: '工具名称不能为空', trigger: 'blur' }],
  toolCode: [{ required: true, message: '工具编码不能为空', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listAtomicTool(queryParams).then(res => {
    toolList.value = res.rows
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

function handleStatusChange(row) {
  const label = row.isEnabled === '1' ? '启用' : '禁用'
  ElMessageBox.confirm(`确认要 ${label} 原子工具「${row.toolName}」吗？`, '提示', { type: 'warning' })
    .then(() => updateAtomicTool({ id: row.id, isEnabled: row.isEnabled }).then(() => ElMessage.success('操作成功')))
    .catch(() => { row.isEnabled = row.isEnabled === '1' ? '0' : '1' })
}

function handleAdd() {
  form.value = { isEnabled: '1', sortOrder: 0 }
  open.value = true
  title.value = '新增原子工具'
}

function handleUpdate(row) {
  const id = row?.id || ids.value[0]
  getAtomicTool(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改原子工具'
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm('确认删除选中的原子工具吗？', '提示', { type: 'warning' }).then(() => {
    delAtomicTool(delIds.join(',')).then(() => { ElMessage.success('删除成功'); getList() })
  })
}

function cancel() { open.value = false; form.value = {} }

function submitForm() {
  toolRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updateAtomicTool : addAtomicTool
    fn(form.value).then(() => {
      ElMessage.success('保存成功')
      open.value = false
      getList()
    })
  })
}

onMounted(getList)
</script>
