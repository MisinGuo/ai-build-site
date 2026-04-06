<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- ========== 内容条目 Tab ========== -->
      <el-tab-pane label="内容条目" name="items">
        <el-form :model="itemQuery" ref="itemQueryRef" :inline="true" v-show="showSearch">
          <el-form-item label="站点ID" prop="siteId">
            <el-input v-model="itemQuery.siteId" placeholder="站点ID" clearable style="width: 100px" @keyup.enter="loadItems" />
          </el-form-item>
          <el-form-item label="内容类型" prop="typeCode">
            <el-input v-model="itemQuery.typeCode" placeholder="类型标识" clearable style="width: 120px" @keyup.enter="loadItems" />
          </el-form-item>
          <el-form-item label="标题" prop="title">
            <el-input v-model="itemQuery.title" placeholder="请输入标题" clearable @keyup.enter="loadItems" />
          </el-form-item>
          <el-form-item label="发布状态" prop="publishStatus">
            <el-select v-model="itemQuery.publishStatus" placeholder="请选择" clearable style="width: 120px">
              <el-option label="草稿" value="draft" />
              <el-option label="已发布" value="published" />
              <el-option label="已归档" value="archived" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="loadItems">搜索</el-button>
            <el-button icon="Refresh" @click="resetItemQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAddItem" v-hasPermi="['aisite:contentItem:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="!itemIds.length" @click="handleDeleteItems" v-hasPermi="['aisite:contentItem:remove']">删除</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="loadItems"></right-toolbar>
        </el-row>

        <el-table v-loading="itemLoading" :data="itemList" @selection-change="sel => itemIds = sel.map(s => s.id)">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="ID" align="center" prop="id" width="70" />
          <el-table-column label="站点" align="center" prop="siteId" width="70" />
          <el-table-column label="类型" align="center" prop="typeCode" width="100">
            <template #default="scope"><el-tag type="info" size="small">{{ scope.row.typeCode }}</el-tag></template>
          </el-table-column>
          <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" />
          <el-table-column label="发布状态" align="center" prop="publishStatus" width="100">
            <template #default="scope">
              <el-tag :type="{ published: 'success', archived: 'info', draft: 'warning' }[scope.row.publishStatus]" size="small">
                {{ { draft: '草稿', published: '已发布', archived: '已归档' }[scope.row.publishStatus] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="浏览量" align="center" prop="viewCount" width="80" />
          <el-table-column label="操作" align="center" width="130">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="handleEditItem(scope.row)" v-hasPermi="['aisite:contentItem:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click="handleDeleteItems(scope.row)" v-hasPermi="['aisite:contentItem:remove']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="itemTotal > 0" :total="itemTotal" v-model:page="itemQuery.pageNum" v-model:limit="itemQuery.pageSize" @pagination="loadItems" />
      </el-tab-pane>

      <!-- ========== 内容类型 Tab ========== -->
      <el-tab-pane label="内容类型" name="types">
        <el-form :model="typeQuery" ref="typeQueryRef" :inline="true">
          <el-form-item label="站点ID" prop="siteId">
            <el-input v-model="typeQuery.siteId" placeholder="站点ID" clearable style="width: 100px" @keyup.enter="loadTypes" />
          </el-form-item>
          <el-form-item label="类型标识" prop="code">
            <el-input v-model="typeQuery.code" placeholder="类型标识" clearable @keyup.enter="loadTypes" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="loadTypes">搜索</el-button>
            <el-button icon="Refresh" @click="resetTypeQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAddType" v-hasPermi="['aisite:contentType:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="!typeIds.length" @click="handleDeleteTypes" v-hasPermi="['aisite:contentType:remove']">删除</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="typeLoading" :data="typeList" @selection-change="sel => typeIds = sel.map(s => s.id)">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="ID" align="center" prop="id" width="70" />
          <el-table-column label="站点" align="center" prop="siteId" width="70" />
          <el-table-column label="类型标识" align="center" prop="code" width="130">
            <template #default="scope"><el-tag>{{ scope.row.code }}</el-tag></template>
          </el-table-column>
          <el-table-column label="类型名称" align="center" prop="name" />
          <el-table-column label="内容数" align="center" prop="itemCount" width="80" />
          <el-table-column label="状态" align="center" prop="status" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" size="small">
                {{ scope.row.status === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="130">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="handleEditType(scope.row)" v-hasPermi="['aisite:contentType:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click="handleDeleteTypes(scope.row)" v-hasPermi="['aisite:contentType:remove']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="typeTotal > 0" :total="typeTotal" v-model:page="typeQuery.pageNum" v-model:limit="typeQuery.pageSize" @pagination="loadTypes" />
      </el-tab-pane>

      <!-- ========== 分类 Tab ========== -->
      <el-tab-pane label="分类管理" name="categories">
        <el-form :model="catQuery" ref="catQueryRef" :inline="true">
          <el-form-item label="站点ID" prop="siteId">
            <el-input v-model="catQuery.siteId" placeholder="站点ID" clearable style="width: 100px" @keyup.enter="loadCategories" />
          </el-form-item>
          <el-form-item label="内容类型" prop="typeCode">
            <el-input v-model="catQuery.typeCode" placeholder="类型标识" clearable style="width: 120px" @keyup.enter="loadCategories" />
          </el-form-item>
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="catQuery.name" placeholder="请输入分类名称" clearable @keyup.enter="loadCategories" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="loadCategories">搜索</el-button>
            <el-button icon="Refresh" @click="resetCatQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAddCat" v-hasPermi="['aisite:category:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="!catIds.length" @click="handleDeleteCats" v-hasPermi="['aisite:category:remove']">删除</el-button>
          </el-col>
        </el-row>

        <el-table v-loading="catLoading" :data="catList" @selection-change="sel => catIds = sel.map(s => s.id)">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="ID" align="center" prop="id" width="70" />
          <el-table-column label="站点" align="center" prop="siteId" width="70" />
          <el-table-column label="类型" align="center" prop="typeCode" width="100">
            <template #default="scope"><el-tag type="info" size="small">{{ scope.row.typeCode }}</el-tag></template>
          </el-table-column>
          <el-table-column label="分类名称" align="center" prop="name" />
          <el-table-column label="Slug" align="center" prop="slug" width="150" :show-overflow-tooltip="true" />
          <el-table-column label="图标" align="center" prop="icon" width="60" />
          <el-table-column label="内容数" align="center" prop="itemCount" width="80" />
          <el-table-column label="操作" align="center" width="130">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="handleEditCat(scope.row)" v-hasPermi="['aisite:category:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click="handleDeleteCats(scope.row)" v-hasPermi="['aisite:category:remove']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="catTotal > 0" :total="catTotal" v-model:page="catQuery.pageNum" v-model:limit="catQuery.pageSize" @pagination="loadCategories" />
      </el-tab-pane>
    </el-tabs>

    <!-- ===== 内容条目 编辑对话框 ===== -->
    <el-dialog :title="itemDialogTitle" v-model="itemOpen" width="780px" append-to-body>
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="站点ID" prop="siteId"><el-input-number v-model="itemForm.siteId" :min="0" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内容类型" prop="typeCode"><el-input v-model="itemForm.typeCode" placeholder="例：house / car" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标题" prop="title"><el-input v-model="itemForm.title" placeholder="请输入标题" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Slug" prop="slug"><el-input v-model="itemForm.slug" placeholder="留空自动生成" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面图" prop="coverImage"><el-input v-model="itemForm.coverImage" placeholder="封面图片 URL" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="摘要" prop="summary"><el-input v-model="itemForm.summary" type="textarea" :rows="2" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="正文" prop="content"><el-input v-model="itemForm.content" type="textarea" :rows="5" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="扩展字段JSON" prop="fieldsJson"><el-input v-model="itemForm.fieldsJson" type="textarea" :rows="3" placeholder='{"price":500000,"city":"北京"}' /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发布状态" prop="publishStatus">
              <el-select v-model="itemForm.publishStatus" style="width:100%">
                <el-option label="草稿" value="draft" />
                <el-option label="已发布" value="published" />
                <el-option label="已归档" value="archived" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="精选" prop="isFeatured">
              <el-radio-group v-model="itemForm.isFeatured">
                <el-radio value="1">是</el-radio><el-radio value="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sortOrder"><el-input-number v-model="itemForm.sortOrder" :min="0" style="width:100%" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="itemOpen = false">取消</el-button>
        <el-button type="primary" @click="submitItem">确定</el-button>
      </template>
    </el-dialog>

    <!-- ===== 内容类型 编辑对话框 ===== -->
    <el-dialog :title="typeDialogTitle" v-model="typeOpen" width="700px" append-to-body>
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="站点ID" prop="siteId"><el-input-number v-model="typeForm.siteId" :min="0" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型标识" prop="code"><el-input v-model="typeForm.code" placeholder="例：house / car" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型名称" prop="name"><el-input v-model="typeForm.name" placeholder="请输入类型名称" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder"><el-input-number v-model="typeForm.sortOrder" :min="0" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="description"><el-input v-model="typeForm.description" placeholder="请输入描述" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="Schema JSON" prop="schemaJson">
              <el-input v-model="typeForm.schemaJson" type="textarea" :rows="5"
                placeholder='[{"key":"title","label":"标题","type":"text","required":true}]' />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="列表字段" prop="listFields"><el-input v-model="typeForm.listFields" placeholder="逗号分隔" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="筛选字段" prop="filterFields"><el-input v-model="typeForm.filterFields" placeholder="逗号分隔" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="系统内置">
              <el-radio-group v-model="typeForm.isSystem">
                <el-radio value="1">是</el-radio><el-radio value="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-radio-group v-model="typeForm.status">
                <el-radio value="1">启用</el-radio><el-radio value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="typeOpen = false">取消</el-button>
        <el-button type="primary" @click="submitType">确定</el-button>
      </template>
    </el-dialog>

    <!-- ===== 分类 编辑对话框 ===== -->
    <el-dialog :title="catDialogTitle" v-model="catOpen" width="560px" append-to-body>
      <el-form ref="catFormRef" :model="catForm" :rules="catRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="站点ID" prop="siteId"><el-input-number v-model="catForm.siteId" :min="0" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内容类型" prop="typeCode"><el-input v-model="catForm.typeCode" placeholder="例：house" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="name"><el-input v-model="catForm.name" placeholder="请输入分类名称" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Slug" prop="slug"><el-input v-model="catForm.slug" placeholder="路径标识" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="父级ID"><el-input-number v-model="catForm.parentId" :min="0" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图标"><el-input v-model="catForm.icon" placeholder="Emoji 或图标" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述"><el-input v-model="catForm.description" type="textarea" :rows="2" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序"><el-input-number v-model="catForm.sortOrder" :min="0" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="catForm.status">
                <el-radio value="1">启用</el-radio><el-radio value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="catOpen = false">取消</el-button>
        <el-button type="primary" @click="submitCat">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listContentItem, getContentItem, addContentItem, updateContentItem, delContentItem } from '@/api/aisite/contentItem'
import { listContentType, getContentType, addContentType, updateContentType, delContentType } from '@/api/aisite/contentType'
import { listCategory, getCategory, addCategory, updateCategory, delCategory } from '@/api/aisite/category'

const activeTab = ref('items')
const showSearch = ref(true)

// ===== 内容条目 =====
const itemLoading = ref(false)
const itemTotal = ref(0)
const itemList = ref([])
const itemIds = ref([])
const itemOpen = ref(false)
const itemDialogTitle = ref('')
const itemQueryRef = ref()
const itemFormRef = ref()
const itemQuery = reactive({ pageNum: 1, pageSize: 10, siteId: undefined, typeCode: undefined, title: undefined, publishStatus: undefined })
const itemForm = ref({})
const itemRules = {
  siteId: [{ required: true, message: '站点ID不能为空', trigger: 'blur' }],
  typeCode: [{ required: true, message: '内容类型不能为空', trigger: 'blur' }],
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }]
}

function loadItems() {
  itemLoading.value = true
  listContentItem(itemQuery).then(res => { itemList.value = res.rows; itemTotal.value = res.total; itemLoading.value = false })
}
function resetItemQuery() { itemQueryRef.value?.resetFields(); itemQuery.pageNum = 1; loadItems() }
function handleAddItem() { itemForm.value = { publishStatus: 'draft', sortOrder: 0, isFeatured: '0' }; itemFormRef.value?.resetFields(); itemOpen.value = true; itemDialogTitle.value = '新增内容条目' }
function handleEditItem(row) {
  getContentItem(row.id).then(res => { itemForm.value = res.data; itemFormRef.value?.resetFields(); itemOpen.value = true; itemDialogTitle.value = '修改内容条目' })
}
function handleDeleteItems(row) {
  const ids = row?.id ? [row.id] : itemIds.value
  ElMessageBox.confirm('确认删除吗？', '提示', { type: 'warning' }).then(() => {
    delContentItem(ids.join(',')).then(() => { ElMessage.success('删除成功'); loadItems() })
  })
}
function submitItem() {
  itemFormRef.value?.validate(valid => {
    if (!valid) return
    const fn = itemForm.value.id ? updateContentItem : addContentItem
    fn(itemForm.value).then(() => { ElMessage.success('保存成功'); itemOpen.value = false; loadItems() })
  })
}

// ===== 内容类型 =====
const typeLoading = ref(false)
const typeTotal = ref(0)
const typeList = ref([])
const typeIds = ref([])
const typeOpen = ref(false)
const typeDialogTitle = ref('')
const typeQueryRef = ref()
const typeFormRef = ref()
const typeQuery = reactive({ pageNum: 1, pageSize: 10, siteId: undefined, code: undefined })
const typeForm = ref({})
const typeRules = {
  name: [{ required: true, message: '类型名称不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '类型标识不能为空', trigger: 'blur' }]
}

function loadTypes() {
  typeLoading.value = true
  listContentType(typeQuery).then(res => { typeList.value = res.rows; typeTotal.value = res.total; typeLoading.value = false })
}
function resetTypeQuery() { typeQueryRef.value?.resetFields(); typeQuery.pageNum = 1; loadTypes() }
function handleAddType() { typeForm.value = { status: '1', sortOrder: 0, isSystem: '0' }; typeFormRef.value?.resetFields(); typeOpen.value = true; typeDialogTitle.value = '新增内容类型' }
function handleEditType(row) {
  getContentType(row.id).then(res => { typeForm.value = res.data; typeFormRef.value?.resetFields(); typeOpen.value = true; typeDialogTitle.value = '修改内容类型' })
}
function handleDeleteTypes(row) {
  const ids = row?.id ? [row.id] : typeIds.value
  ElMessageBox.confirm('确认删除吗？', '提示', { type: 'warning' }).then(() => {
    delContentType(ids.join(',')).then(() => { ElMessage.success('删除成功'); loadTypes() })
  })
}
function submitType() {
  typeFormRef.value?.validate(valid => {
    if (!valid) return
    const fn = typeForm.value.id ? updateContentType : addContentType
    fn(typeForm.value).then(() => { ElMessage.success('保存成功'); typeOpen.value = false; loadTypes() })
  })
}

// ===== 分类 =====
const catLoading = ref(false)
const catTotal = ref(0)
const catList = ref([])
const catIds = ref([])
const catOpen = ref(false)
const catDialogTitle = ref('')
const catQueryRef = ref()
const catFormRef = ref()
const catQuery = reactive({ pageNum: 1, pageSize: 10, siteId: undefined, typeCode: undefined, name: undefined })
const catForm = ref({})
const catRules = {
  siteId: [{ required: true, message: '站点ID不能为空', trigger: 'blur' }],
  typeCode: [{ required: true, message: '内容类型不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }],
  slug: [{ required: true, message: 'Slug不能为空', trigger: 'blur' }]
}

function loadCategories() {
  catLoading.value = true
  listCategory(catQuery).then(res => { catList.value = res.rows; catTotal.value = res.total; catLoading.value = false })
}
function resetCatQuery() { catQueryRef.value?.resetFields(); catQuery.pageNum = 1; loadCategories() }
function handleAddCat() { catForm.value = { status: '1', sortOrder: 0, parentId: 0 }; catFormRef.value?.resetFields(); catOpen.value = true; catDialogTitle.value = '新增分类' }
function handleEditCat(row) {
  getCategory(row.id).then(res => { catForm.value = res.data; catFormRef.value?.resetFields(); catOpen.value = true; catDialogTitle.value = '修改分类' })
}
function handleDeleteCats(row) {
  const ids = row?.id ? [row.id] : catIds.value
  ElMessageBox.confirm('确认删除吗？', '提示', { type: 'warning' }).then(() => {
    delCategory(ids.join(',')).then(() => { ElMessage.success('删除成功'); loadCategories() })
  })
}
function submitCat() {
  catFormRef.value?.validate(valid => {
    if (!valid) return
    const fn = catForm.value.id ? updateCategory : addCategory
    fn(catForm.value).then(() => { ElMessage.success('保存成功'); catOpen.value = false; loadCategories() })
  })
}

onMounted(() => { loadItems(); loadTypes(); loadCategories() })
</script>
