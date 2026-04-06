<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="站点ID" prop="siteId">
        <el-input v-model="queryParams.siteId" placeholder="站点ID" clearable style="width: 100px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="内容类型" prop="typeCode">
        <el-input v-model="queryParams.typeCode" placeholder="类型标识" clearable style="width: 120px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="发布状态" prop="publishStatus">
        <el-select v-model="queryParams.publishStatus" placeholder="请选择" clearable style="width: 120px">
          <el-option label="草稿" value="draft" />
          <el-option label="已发布" value="published" />
          <el-option label="已归档" value="archived" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:contentItem:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:contentItem:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:contentItem:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="站点" align="center" prop="siteId" width="80" />
      <el-table-column label="类型" align="center" prop="typeCode" width="100">
        <template #default="scope">
          <el-tag type="info">{{ scope.row.typeCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="Slug" align="center" prop="slug" width="150" :show-overflow-tooltip="true" />
      <el-table-column label="发布状态" align="center" prop="publishStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.publishStatus === 'published' ? 'success' : scope.row.publishStatus === 'archived' ? 'info' : 'warning'">
            {{ { draft: '草稿', published: '已发布', archived: '已归档' }[scope.row.publishStatus] || scope.row.publishStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="浏览量" align="center" prop="viewCount" width="80" />
      <el-table-column label="精选" align="center" prop="isFeatured" width="70">
        <template #default="scope">
          <el-icon v-if="scope.row.isFeatured === '1'" color="#e6a23c"><Star /></el-icon>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['aisite:contentItem:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['aisite:contentItem:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="站点ID" prop="siteId">
              <el-input-number v-model="form.siteId" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内容类型" prop="typeCode">
              <el-input v-model="form.typeCode" placeholder="例：house / car" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Slug" prop="slug">
              <el-input v-model="form.slug" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面图" prop="coverImage">
              <el-input v-model="form.coverImage" placeholder="封面图片 URL" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="摘要" prop="summary">
              <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入摘要" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="正文" prop="content">
              <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入正文内容" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">扩展字段（JSON）</el-divider>
        <el-form-item label="Fields JSON" prop="fieldsJson">
          <el-input v-model="form.fieldsJson" type="textarea" :rows="4"
            placeholder='{"price":500000,"city":"北京","area":120}' />
        </el-form-item>

        <el-divider content-position="left">SEO 信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="SEO 标题" prop="seoTitle">
              <el-input v-model="form.seoTitle" placeholder="SEO 标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SEO 关键词" prop="seoKeywords">
              <el-input v-model="form.seoKeywords" placeholder="关键词，逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="SEO 描述" prop="seoDescription">
              <el-input v-model="form.seoDescription" type="textarea" :rows="2" placeholder="SEO 描述" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="发布状态" prop="publishStatus">
              <el-select v-model="form.publishStatus" style="width: 100%">
                <el-option label="草稿" value="draft" />
                <el-option label="已发布" value="published" />
                <el-option label="已归档" value="archived" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="精选" prop="isFeatured">
              <el-radio-group v-model="form.isFeatured">
                <el-radio value="1">是</el-radio>
                <el-radio value="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
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
import { listContentItem, getContentItem, addContentItem, updateContentItem, delContentItem } from '@/api/aisite/contentItem'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const itemList = ref([])
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
  typeCode: undefined,
  title: undefined,
  publishStatus: undefined
})

const form = ref({})

const rules = {
  siteId: [{ required: true, message: '站点ID不能为空', trigger: 'blur' }],
  typeCode: [{ required: true, message: '内容类型不能为空', trigger: 'blur' }],
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listContentItem(queryParams).then(res => {
    itemList.value = res.rows
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
  form.value = { publishStatus: 'draft', sortOrder: 0, isFeatured: '0' }
  formRef.value?.resetFields()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '新增内容条目'
}

function handleUpdate(row) {
  reset()
  const id = row?.id || ids.value[0]
  getContentItem(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改内容条目'
  })
}

function cancel() {
  open.value = false
  reset()
}

function submitForm() {
  formRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updateContentItem : addContentItem
    fn(form.value).then(() => {
      ElMessage.success(form.value.id ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm('确认删除所选内容条目吗？', '提示', { type: 'warning' }).then(() => {
    delContentItem(delIds.join(',')).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

onMounted(getList)
</script>
