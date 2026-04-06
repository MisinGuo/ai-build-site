<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="所属网站" prop="siteId">
        <SiteSelect v-model="queryParams.siteId" :site-list="siteList" @change="handleSiteChange" />
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="includeGlobalInQuery" @change="handleQuery">含全局</el-checkbox>
      </el-form-item>
      <el-form-item label="短剧名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入短剧名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="短剧编码" prop="dramaCode">
        <el-input v-model="queryParams.dramaCode" placeholder="请输入短剧编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 200px" filterable>
          <el-option
            v-for="cat in dramaCategoryList"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          >
            <span v-if="cat.icon" style="margin-right: 4px">{{ cat.icon }}</span>
            <span>{{ cat.name }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:drama:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:drama:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:drama:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="MagicStick" :disabled="multiple" @click="handleBatchTranslate" v-hasPermi="['gamebox:drama:edit']">批量翻译</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dramaList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="封面图" align="center" prop="coverUrl" width="80">
        <template #default="scope">
          <el-image v-if="scope.row.coverUrl" :src="scope.row.coverUrl" style="width: 40px; height: 60px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="短剧名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="所属网站" align="center" prop="siteId" width="150">
        <template #default="scope">
          <el-tag type="info">{{ getSiteName(scope.row.siteId) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="短剧编码" align="center" prop="dramaCode" width="120" />
      <el-table-column label="分类" align="center" prop="categoryId" width="120">
        <template #default="scope">
          <CategoryTag v-if="scope.row.categoryName" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'drama', icon: scope.row.categoryIcon }" size="small" />
          <CategoryTag v-else-if="getCategoryFromCache(scope.row.categoryId)" :category="{ id: scope.row.categoryId, name: getCategoryFromCache(scope.row.categoryId).name, categoryType: 'drama', icon: getCategoryFromCache(scope.row.categoryId).icon }" size="small" />
          <el-tag v-else-if="scope.row.categoryId" type="warning" size="small">ID: {{ scope.row.categoryId }}</el-tag>
          <el-tag v-else type="info" size="small">未分类</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总集数" align="center" prop="totalEpisodes" width="80" />
      <el-table-column label="播放量" align="center" prop="playCount" width="100" />
      <el-table-column label="评分" align="center" prop="rating" width="80" />
      <el-table-column label="推荐" align="center" prop="isRecommended" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isRecommended === '1' ? 'success' : 'info'" size="small">
            {{ scope.row.isRecommended === '1' ? '是' : '否' }}
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:drama:edit']">修改</el-button>
          <el-button link type="warning" icon="Document" @click="handleManageTranslations(scope.row)" v-hasPermi="['gamebox:drama:edit']">翻译</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:drama:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改短剧对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="dramaRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="短剧名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入短剧名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="短剧编码" prop="dramaCode">
              <el-input v-model="form.dramaCode" placeholder="请输入短剧编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="所属网站" prop="siteId">
              <SiteSelect v-model="form.siteId" :site-list="siteList" show-default default-label="全局" clearable width="100%" placeholder="请选择所属网站" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.siteId && !isPersonalSiteCheck(form.siteId)">
          <el-col :span="24">
            <el-alert
              title="提示：短剧可以选择本网站的分类或全局分类，全局分类适用于所有网站"
              type="info"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-tree-select
                v-model="form.categoryId"
                :data="dramaCategoryTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择分类"
                check-strictly
                :render-after-expand="false"
                clearable
                filterable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商ID" prop="vendorId">
              <el-input-number v-model="form.vendorId" :min="0" placeholder="请输入供应商ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总集数" prop="totalEpisodes">
              <el-input-number v-model="form.totalEpisodes" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="封面图" prop="coverUrl">
              <el-input v-model="form.coverUrl" placeholder="请输入封面图URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="短剧简介" prop="summary">
              <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入短剧简介" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="导演" prop="director">
              <el-input v-model="form.director" placeholder="请输入导演" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主演" prop="actors">
              <el-input v-model="form.actors" placeholder="请输入主演" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="评分" prop="rating">
              <el-input-number v-model="form.rating" :min="0" :max="10" :precision="1" :step="0.1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否推荐" prop="isRecommended">
              <el-switch v-model="form.isRecommended" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否完结" prop="isFinished">
              <el-switch v-model="form.isFinished" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
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
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 翻译管理对话框 -->
    <TranslationManager
      v-model="translationDialogOpen"
      entity-type="drama"
      :entity-id="currentTranslationDramaId"
      :entity-name="currentTranslationDramaName"
      :site-id="queryParams.siteId || 0"
      :translation-fields="dramaTranslationFields"
      :original-data="currentTranslationDramaData"
      @refresh="getList"
    />
  </div>
</template>

<script setup name="Drama">
import { listDrama, getDrama, delDrama, addDrama, updateDrama } from "@/api/gamebox/drama"
import { listCategory } from "@/api/gamebox/category"
import { batchAutoTranslate } from "@/api/gamebox/translation"
import { useSiteSelection, getPersonalSiteId, isPersonalSite } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"
import CategoryTag from "@/components/CategoryTag/index.vue"
import TranslationManager from "@/components/TranslationManager/index.vue"
import { handleTree } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()

const { siteList, currentSiteId, loadSiteList: loadSites, getSiteName } = useSiteSelection()

const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

const dramaList = ref([])
const dramaCategoryList = ref([])
const dramaCategoryTreeOptions = ref([]) // 树形结构，用于对话框
const categoryCache = ref(new Map()) // 分类缓存，用于表格显示
const includeGlobalCategories = ref(false)
const includeGlobalInQuery = ref(false)
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 翻译管理相关
const translationDialogOpen = ref(false)
const currentTranslationDramaId = ref(0)
const currentTranslationDramaName = ref('')
const currentTranslationDramaData = ref({})
const dramaTranslationFields = [
  { name: 'name', label: '短剧名称', type: 'text' },
  { name: 'subtitle', label: '副标题', type: 'text' },
  { name: 'description', label: '短剧简介', type: 'textarea' },
  { name: 'content', label: '详细介绍', type: 'textarea' },
  { name: 'director', label: '导演', type: 'text' },
  { name: 'actors', label: '演员列表', type: 'text' },
  { name: 'producer', label: '出品方', type: 'text' }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: undefined,
    name: undefined,
    dramaCode: undefined,
    categoryId: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: "短剧名称不能为空", trigger: "blur" }],
    dramaCode: [{ required: true, message: "短剧编码不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function handleSiteChange() {
  queryParams.value.pageNum = 1
  loadDramaCategoriesForQuery(queryParams.value.siteId)
  getList()
}

// 预加载分类信息到缓存
function preloadCategories(dramaRecords) {
  const categoryIds = [...new Set(dramaRecords.map(d => d.categoryId).filter(id => id))]
  
  if (categoryIds.length === 0) return
  
  listCategory({ 
    categoryType: 'drama', 
    pageNum: 1, 
    pageSize: 9999,
    status: '1'
  }).then(response => {
    const categories = response.rows || []
    categories.forEach(cat => {
      categoryCache.value.set(cat.id, cat)
    })
  })
}

// 从缓存获取分类
function getCategoryFromCache(categoryId) {
  if (!categoryId) return null
  return categoryCache.value.get(categoryId)
}

function getList() {
  loading.value = true
  
  const params = { ...queryParams.value }
  
  if (params.siteId && !isPersonalSite(params.siteId, siteList.value)) {
    if (includeGlobalInQuery.value) {
      const promises = [
        listDrama({ ...params, siteId: params.siteId }),
        listDrama({ ...params, siteId: personalSiteId.value })
      ]
      Promise.all(promises).then(responses => {
        const siteDramas = responses[0].rows || []
        const globalDramas = responses[1].rows || []
        dramaList.value = [...siteDramas, ...globalDramas]
        total.value = siteDramas.length + globalDramas.length
        preloadCategories(dramaList.value)
        loading.value = false
      })
    } else {
      listDrama(params).then(response => {
        dramaList.value = response.rows
        total.value = response.total
        preloadCategories(dramaList.value)
        loading.value = false
      })
    }
  } else {
    listDrama({ ...params, siteId: personalSiteId.value }).then(response => {
      dramaList.value = response.rows
      total.value = response.total
      preloadCategories(dramaList.value)
      loading.value = false
    })
  }
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    dramaCode: undefined,
    categoryId: undefined,
    vendorId: undefined,
    coverUrl: undefined,
    summary: undefined,
    totalEpisodes: 1,
    director: undefined,
    actors: undefined,
    rating: 8.0,
    isRecommended: "0",
    isFinished: "0",
    sortOrder: 0,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("dramaRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

// 加载短剧分类列表（对话框使用）
function loadDramaCategoriesForDialog(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    listCategory({ categoryType: 'drama', siteId: personalSiteId.value, status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
      let categories = response.rows || []
      categories = categories.map(cat => ({
        ...cat,
        name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [全局]`
      }))
      dramaCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
    })
    return
  }
  
  const promises = [
    listCategory({ categoryType: 'drama', siteId: siteId, status: '1', pageNum: 1, pageSize: 1000 }),
    listCategory({ categoryType: 'drama', siteId: personalSiteId.value, status: '1', pageNum: 1, pageSize: 1000 })
  ]
  Promise.all(promises).then(responses => {
    let siteCategories = responses[0].rows || []
    let globalCategories = responses[1].rows || []
    
    const siteName = getSiteName(siteId)
    siteCategories = siteCategories.map(cat => ({
      ...cat,
      name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [${siteName}]`
    }))
    globalCategories = globalCategories.map(cat => ({
      ...cat,
      name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [全局]`
    }))
    
    const allCategories = [...siteCategories, ...globalCategories]
    dramaCategoryTreeOptions.value = handleTree(allCategories, "id", "parentId")
  })
}

// 加载短剧分类列表（查询表单使用）
function loadDramaCategoriesForQuery(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    listCategory({ categoryType: 'drama', siteId: personalSiteId.value, status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
      dramaCategoryList.value = response.rows || []
    })
    return
  }
  
  if (includeGlobalInQuery.value) {
    const promises = [
      listCategory({ categoryType: 'drama', siteId: siteId, status: '1', pageNum: 1, pageSize: 1000 }),
      listCategory({ categoryType: 'drama', siteId: personalSiteId.value, status: '1', pageNum: 1, pageSize: 1000 })
    ]
    Promise.all(promises).then(responses => {
      const siteCategories = responses[0].rows || []
      const globalCategories = responses[1].rows || []
      dramaCategoryList.value = [...siteCategories, ...globalCategories]
    })
  } else {
    listCategory({ categoryType: 'drama', siteId: siteId, status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
      dramaCategoryList.value = response.rows || []
    })
  }
}

function handleAdd() {
  reset()
  if (queryParams.value.siteId) {
    form.value.siteId = queryParams.value.siteId
  } else if (siteList.value.length > 0) {
    form.value.siteId = siteList.value[0].id
  }
  loadDramaCategoriesForDialog(form.value.siteId)
  open.value = true
  title.value = "添加短剧"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getDrama(id).then(response => {
    const data = response.data
    open.value = true
    title.value = "修改短剧"
    
    loadDramaCategoriesForDialog(data.siteId)
    
    nextTick(() => {
      form.value = data
    })
  })
}

function submitForm() {
  proxy.$refs["dramaRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateDrama(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addDrama(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const dramaIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除短剧编号为"' + dramaIds + '"的数据项？').then(function() {
    return delDrama(dramaIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 管理翻译 */
function handleManageTranslations(row) {
  currentTranslationDramaId.value = row.id
  currentTranslationDramaName.value = row.name
  currentTranslationDramaData.value = {
    name: row.name,
    subtitle: row.subtitle,
    description: row.description,
    content: row.content,
    director: row.director,
    actors: row.actors,
    producer: row.producer
  }
  translationDialogOpen.value = true
}

/** 批量翻译 */
async function handleBatchTranslate() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要翻译的短剧')
    return
  }
  
  try {
    await proxy.$modal.confirm(`确认要为选中的 ${selectedIds.length} 个短剧生成翻译吗？`)
    
    // 准备要翻译的数据
    const allEntities = dramaList.value
      .filter(drama => selectedIds.includes(drama.id))
      .map(drama => ({
        entityId: drama.id,
        fields: {
          name: drama.name,
          subtitle: drama.subtitle || '',
          description: drama.description || '',
          content: drama.content || '',
          director: drama.director || '',
          actors: drama.actors || '',
          producer: drama.producer || ''
        }
      }))
    
    // 分批处理，每批5条
    const batchSize = 5
    const batches = []
    for (let i = 0; i < allEntities.length; i += batchSize) {
      batches.push(allEntities.slice(i, i + batchSize))
    }
    
    let successCount = 0
    let failCount = 0
    
    const loading = proxy.$loading({
      lock: true,
      text: `正在批量生成翻译... 0/${allEntities.length}`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 逐批处理
    for (let i = 0; i < batches.length; i++) {
      try {
        await batchAutoTranslate('drama', queryParams.siteId || 0, batches[i])
        successCount += batches[i].length
      } catch (error) {
        console.error(`第${i + 1}批翻译失败:`, error)
        failCount += batches[i].length
      }
      
      // 更新进度
      loading.setText(`正在批量生成翻译... ${successCount + failCount}/${allEntities.length}`)
    }
    
    loading.close()
    
    if (failCount > 0) {
      proxy.$modal.msgWarning(`批量翻译完成：成功${successCount}个，失败${failCount}个`)
    } else {
      proxy.$modal.msgSuccess(`批量翻译完成：共${successCount}个`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量翻译失败:', error)
      proxy.$modal.msgError('批量翻译失败: ' + error.message)
    }
  }
}

// 监听表单中的siteId变化，自动重新加载分类
watch(() => form.value.siteId, (newSiteId, oldSiteId) => {
  if (!open.value) return
  
  loadDramaCategoriesForDialog(newSiteId)
  if (oldSiteId !== undefined) {
    form.value.categoryId = undefined
  }
})

// 监听查询表单的includeGlobalInQuery变化
watch(() => includeGlobalInQuery.value, () => {
  loadDramaCategoriesForQuery(queryParams.value.siteId)
})

loadSites(() => {
  queryParams.value.siteId = currentSiteId.value
  loadDramaCategoriesForQuery(queryParams.value.siteId)
  getList()
})
</script>
