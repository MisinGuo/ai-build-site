<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="站点ID" prop="siteId">
        <el-input v-model="queryParams.siteId" placeholder="请输入站点ID" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型标识" prop="code">
        <el-input v-model="queryParams.code" placeholder="例：house / car / hotel" clearable @keyup.enter="handleQuery" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:contentType:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:contentType:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:contentType:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="站点ID" align="center" prop="siteId" width="90" />
      <el-table-column label="类型标识" align="center" prop="code" width="130">
        <template #default="scope">
          <el-tag>{{ scope.row.code }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="类型名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="内容数量" align="center" prop="itemCount" width="90" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="70" />
      <el-table-column label="系统内置" align="center" prop="isSystem" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.isSystem === '1' ? 'warning' : 'info'">
            {{ scope.row.isSystem === '1' ? '是' : '否' }}
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
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['aisite:contentType:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['aisite:contentType:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="站点ID" prop="siteId">
              <el-input-number v-model="form.siteId" :min="0" placeholder="请输入站点ID" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型标识" prop="code">
              <el-input v-model="form.code" placeholder="例：house / car / hotel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入类型名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" placeholder="请输入描述" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">字段 Schema（JSON）</el-divider>
        <el-form-item label="Schema JSON" prop="schemaJson">
          <el-input v-model="form.schemaJson" type="textarea" :rows="6"
            placeholder='[{"key":"title","label":"标题","type":"text","required":true},{"key":"price","label":"价格","type":"number"}]' />
        </el-form-item>

        <el-divider content-position="left">显示配置</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="列表展示字段" prop="listFields">
              <el-input v-model="form.listFields" placeholder="逗号分隔，例：title,price,area" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="筛选字段" prop="filterFields">
              <el-input v-model="form.filterFields" placeholder="逗号分隔，例：price,city" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="SEO 模板" prop="seoTemplate">
              <el-input v-model="form.seoTemplate" placeholder="例：{title} - {city} 房源信息" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="系统内置" prop="isSystem">
              <el-radio-group v-model="form.isSystem">
                <el-radio value="1">是</el-radio>
                <el-radio value="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="1">启用</el-radio>
                <el-radio value="0">禁用</el-radio>
              </el-radio-group>
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
import { listContentType, getContentType, addContentType, updateContentType, delContentType } from '@/api/aisite/contentType'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const typeList = ref([])
const open = ref(false)
const title = ref('')
const single = ref(true)
const multiple = ref(true)
const ids = ref([])

const queryRef = ref()
const formRef = ref()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  siteId: undefined,
  name: undefined,
  code: undefined,
  status: undefined
})

const form = ref({})

const rules = {
  name: [{ required: true, message: '类型名称不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '类型标识不能为空', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listContentType(queryParams).then(res => {
    typeList.value = res.rows
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
  form.value = { status: '1', sortOrder: 0, isSystem: '0' }
  formRef.value?.resetFields()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增内容类型'
}

function handleUpdate(row) {
  reset()
  const id = row?.id || ids.value[0]
  getContentType(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改内容类型'
  })
}

function cancel() {
  open.value = false
  reset()
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updateContentType : addContentType
    fn(form.value).then(() => {
      ElMessage.success(form.value.id ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm('确认删除所选内容类型吗？', '提示', { type: 'warning' }).then(() => {
    delContentType(delIds.join(',')).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

onMounted(getList)
</script>
