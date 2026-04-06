<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="所属网站" prop="siteId">
        <SiteSelect v-model="queryParams.siteId" :site-list="siteList" @change="handleSiteChange" />
      </el-form-item>
      <el-form-item label="供应商名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入供应商名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="供应商码" prop="vendorCode">
        <el-input v-model="queryParams.vendorCode" placeholder="请输入供应商编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:vendor:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:vendor:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:vendor:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="vendorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="Logo" align="center" prop="logoUrl" width="80">
        <template #default="scope">
          <el-image v-if="scope.row.logoUrl" :src="scope.row.logoUrl" style="width: 40px; height: 40px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="供应商名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="创建者网站" align="center" prop="siteId" width="150">
        <template #default="scope">
          <el-tag type="success">{{ getSiteName(scope.row.siteId) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="供应商编码" align="center" prop="vendorCode" width="120" />
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="分成比例" align="center" prop="shareRatio" width="100">
        <template #default="scope">
          {{ scope.row.shareRatio ? scope.row.shareRatio + '%' : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
            {{ scope.row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:vendor:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:vendor:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改供应商对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="vendorRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="创建者网站" prop="siteId">
              <SiteSelect v-model="form.siteId" :site-list="siteList" show-default default-label="全局" clearable :disabled="!!form.id" width="100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.id">
            <el-alert
              title="创建后不可修改创建者网站。如需在其他网站展示，请使用【网站关联】功能。"
              type="warning"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入供应商名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商编码" prop="vendorCode">
              <el-input v-model="form.vendorCode" placeholder="请输入供应商编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="供应商Logo" prop="logoUrl">
              <el-input v-model="form.logoUrl" placeholder="请输入Logo URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="API接口地址" prop="apiEndpoint">
              <el-input v-model="form.apiEndpoint" placeholder="请输入API接口地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="API密钥" prop="apiKey">
              <el-input v-model="form.apiKey" placeholder="请输入API密钥" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分成比例(%)" prop="shareRatio">
              <el-input-number v-model="form.shareRatio" :min="0" :max="100" :precision="2" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
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
  </div>
</template>

<script setup name="Vendor">
import { listVendor, getVendor, delVendor, addVendor, updateVendor } from "@/api/gamebox/vendor"
import { useSiteSelection } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"

const { proxy } = getCurrentInstance()

const { siteList, currentSiteId, loadSiteList: loadSites, getSiteName } = useSiteSelection()

const vendorList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: undefined,
    name: undefined,
    vendorCode: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: "供应商名称不能为空", trigger: "blur" }],
    vendorCode: [{ required: true, message: "供应商编码不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function handleSiteChange() {
  queryParams.value.pageNum = 1
  getList()
}

function getList() {
  loading.value = true
  listVendor(queryParams.value).then(response => {
    vendorList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    name: undefined,
    vendorCode: undefined,
    logoUrl: undefined,
    contactPerson: undefined,
    contactPhone: undefined,
    contactEmail: undefined,
    apiEndpoint: undefined,
    apiKey: undefined,
    shareRatio: 30,
    sortOrder: 0,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("vendorRef")
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

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加供应商"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getVendor(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改供应商"
  })
}

function submitForm() {
  proxy.$refs["vendorRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateVendor(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addVendor(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const vendorIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除供应商编号为"' + vendorIds + '"的数据项？').then(function() {
    return delVendor(vendorIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

loadSites(() => {
  queryParams.value.siteId = currentSiteId.value
  getList()
})
</script>
