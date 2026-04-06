<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入配置名称" clearable style="width:180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="存储类型" prop="storageType">
        <el-select v-model="queryParams.storageType" placeholder="请选择" clearable style="width:140px">
          <el-option label="AWS S3" value="s3" />
          <el-option label="Cloudflare R2" value="r2" />
          <el-option label="阿里云 OSS" value="oss" />
          <el-option label="腾讯云 COS" value="cos" />
          <el-option label="MinIO" value="minio" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:storageConfig:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:storageConfig:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:storageConfig:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="配置名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="配置编码" align="center" prop="code" width="160" :show-overflow-tooltip="true" />
      <el-table-column label="存储类型" align="center" prop="storageType" width="130">
        <template #default="{ row }">
          <el-tag :type="typeTagMap[row.storageType] || 'info'" size="small">
            {{ typeNameMap[row.storageType] || row.storageType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="CDN 地址" align="center" prop="cdnUrl" :show-overflow-tooltip="true" />
      <el-table-column label="默认" align="center" prop="isDefault" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isDefault === '1' ? 'success' : 'info'" size="small">
            {{ row.isDefault === '1' ? '是' : '否' }}
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
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['aisite:storageConfig:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['aisite:storageConfig:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" v-model="open" width="660px" append-to-body>
      <el-form ref="configRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配置名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入配置名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配置编码" prop="code">
              <el-input v-model="form.code" placeholder="如 r2_main" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存储类型" prop="storageType">
              <el-select v-model="form.storageType" style="width:100%">
                <el-option label="AWS S3" value="s3" />
                <el-option label="Cloudflare R2" value="r2" />
                <el-option label="阿里云 OSS" value="oss" />
                <el-option label="腾讯云 COS" value="cos" />
                <el-option label="MinIO" value="minio" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="CDN 地址">
              <el-input v-model="form.cdnUrl" placeholder="https://cdn.example.com" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设为默认">
              <el-radio-group v-model="form.isDefault">
                <el-radio value="1">是</el-radio>
                <el-radio value="0">否</el-radio>
              </el-radio-group>
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
            <el-form-item label="存储配置 JSON">
              <el-input v-model="form.configJson" type="textarea" :rows="6"
                placeholder='{"bucket":"my-bucket","region":"auto","endpoint":"https://...","accessKeyId":"...","secretAccessKey":"..."}' />
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
import { listStorageConfig, getStorageConfig, addStorageConfig, updateStorageConfig, delStorageConfig } from '@/api/aisite/storageConfig'

const typeTagMap = { s3: 'warning', r2: 'primary', oss: 'success', cos: 'danger', minio: 'info' }
const typeNameMap = { s3: 'AWS S3', r2: 'Cloudflare R2', oss: '阿里云 OSS', cos: '腾讯云 COS', minio: 'MinIO' }

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const configList = ref([])
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const title = ref('')
const queryRef = ref()
const configRef = ref()

const queryParams = reactive({ pageNum: 1, pageSize: 10, name: undefined, storageType: undefined, status: undefined })
const form = ref({})
const rules = {
  name: [{ required: true, message: '配置名称不能为空', trigger: 'blur' }],
  code: [{ required: true, message: '配置编码不能为空', trigger: 'blur' }],
  storageType: [{ required: true, message: '请选择存储类型', trigger: 'change' }]
}

function getList() {
  loading.value = true
  listStorageConfig(queryParams).then(res => {
    configList.value = res.rows
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
  form.value = { isDefault: '0', status: '1' }
  open.value = true
  title.value = '新增存储配置'
}

function handleUpdate(row) {
  const id = row?.id || ids.value[0]
  getStorageConfig(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改存储配置'
  })
}

function handleDelete(row) {
  const delIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm('确认删除选中的存储配置吗？', '提示', { type: 'warning' }).then(() => {
    delStorageConfig(delIds.join(',')).then(() => { ElMessage.success('删除成功'); getList() })
  })
}

function cancel() { open.value = false; form.value = {} }

function submitForm() {
  configRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updateStorageConfig : addStorageConfig
    fn(form.value).then(() => {
      ElMessage.success('保存成功')
      open.value = false
      getList()
    })
  })
}

onMounted(getList)
</script>
