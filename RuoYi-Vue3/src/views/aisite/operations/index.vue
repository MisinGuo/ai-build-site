<template>
  <div class="app-container">
    <!-- 顶部统计 -->
    <el-row :gutter="16" class="mb16">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card class="stat-card" shadow="never" :body-style="{ padding: '16px 20px' }">
          <div class="stat-inner">
            <div>
              <div class="stat-num" :style="{color: stat.color}">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
            <el-icon class="stat-icon" :style="{color: stat.color}" :size="36"><component :is="stat.icon" /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="任务名称" prop="taskName">
        <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable style="width:180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="任务类型" prop="taskType">
        <el-select v-model="queryParams.taskType" placeholder="全部类型" clearable style="width:140px">
          <el-option label="内容生成" value="content_gen" />
          <el-option label="SEO 优化" value="seo_optimize" />
          <el-option label="内链建设" value="interlink" />
          <el-option label="站点部署" value="deploy" />
          <el-option label="死链扫描" value="dead_link_scan" />
        </el-select>
      </el-form-item>
      <el-form-item label="触发方式" prop="triggerType">
        <el-select v-model="queryParams.triggerType" placeholder="全部" clearable style="width:110px">
          <el-option label="手动" value="manual" />
          <el-option label="定时" value="scheduled" />
          <el-option label="事件" value="event" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width:110px">
          <el-option label="等待中" value="pending" />
          <el-option label="运行中" value="running" />
          <el-option label="已完成" value="success" />
          <el-option label="失败" value="failed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['aisite:operation:add']">新建任务</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['aisite:operation:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['aisite:operation:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="任务名称" align="center" prop="taskName" :show-overflow-tooltip="true" />
      <el-table-column label="任务类型" align="center" prop="taskType" width="110">
        <template #default="{ row }">
          <el-tag :type="taskTypeTag(row.taskType)" size="small">{{ taskTypeLabel(row.taskType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="触发方式" align="center" prop="triggerType" width="90">
        <template #default="{ row }">
          <el-tag type="info" size="small">{{ triggerTypeLabel(row.triggerType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Cron" align="center" prop="cronExpr" width="140" :show-overflow-tooltip="true" />
      <el-table-column label="优先级" align="center" prop="priority" width="80">
        <template #default="{ row }">
          <el-rate v-model="row.priority" :max="10" size="small" disabled />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="{ row }">
          <el-badge :is-dot="row.status === 'running'" :type="statusTag(row.status)">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </el-badge>
        </template>
      </el-table-column>
      <el-table-column label="上次执行" align="center" prop="lastRunAt" width="155">
        <template #default="{ row }">{{ row.lastRunAt ? parseTime(row.lastRunAt) : '-' }}</template>
      </el-table-column>
      <el-table-column label="下次执行" align="center" prop="nextRunAt" width="155">
        <template #default="{ row }">{{ row.nextRunAt ? parseTime(row.nextRunAt) : '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160">
        <template #default="{ row }">
          <el-button link type="primary" icon="VideoPlay" @click="handleRun(row)" v-hasPermi="['aisite:operation:edit']"
            :disabled="row.status === 'running'">执行</el-button>
          <el-button link type="warning" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['aisite:operation:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['aisite:operation:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="taskRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="任务类型" prop="taskType">
              <el-select v-model="form.taskType" placeholder="选择类型" style="width:100%">
                <el-option label="内容生成" value="content_gen" />
                <el-option label="SEO 优化" value="seo_optimize" />
                <el-option label="内链建设" value="interlink" />
                <el-option label="站点部署" value="deploy" />
                <el-option label="死链扫描" value="dead_link_scan" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="触发方式" prop="triggerType">
              <el-select v-model="form.triggerType" placeholder="选择触发方式" style="width:100%">
                <el-option label="手动" value="manual" />
                <el-option label="定时" value="scheduled" />
                <el-option label="事件" value="event" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Cron 表达式" prop="cronExpr" v-if="form.triggerType === 'scheduled'">
          <el-input v-model="form.cronExpr" placeholder="如 0 2 * * * 每天凌晨2点" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-slider v-model="form.priority" :min="1" :max="10" show-stops />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大重试" prop="maxRetries">
              <el-input-number v-model="form.maxRetries" :min="0" :max="10" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="任务参数 (JSON)">
          <el-input v-model="form.taskParams" type="textarea" :rows="3" placeholder='{"siteId": 1, "typeCode": "article"}' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Check, CircleCheck, Warning, Clock } from '@element-plus/icons-vue'
import { listOperationTask, getOperationTask, addOperationTask, updateOperationTask, delOperationTask } from '@/api/aisite/operationTask'
import { parseTime } from '@/utils/ruoyi'

const loading = ref(false)
const showSearch = ref(true)
const taskList = ref([])
const total = ref(0)
const open = ref(false)
const title = ref('')
const single = ref(true)
const multiple = ref(true)
const queryRef = ref(null)
const taskRef = ref(null)
const selectedIds = ref([])

const queryParams = reactive({
  pageNum: 1, pageSize: 10,
  taskName: null, taskType: null, triggerType: null, status: null
})

const form = ref({})
const rules = reactive({
  taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  taskType: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
  triggerType: [{ required: true, message: '请选择触发方式', trigger: 'change' }]
})

const stats = computed(() => {
  const all = taskList.value
  return [
    { label: '全部任务', value: total.value, color: '#409eff', icon: 'Calendar' },
    { label: '运行中', value: all.filter(t => t.status === 'running').length, color: '#e6a23c', icon: 'Loading' },
    { label: '已完成', value: all.filter(t => t.status === 'success').length, color: '#67c23a', icon: 'CircleCheck' },
    { label: '失败', value: all.filter(t => t.status === 'failed').length, color: '#f56c6c', icon: 'Warning' }
  ]
})

const taskTypeMap = { content_gen: '内容生成', seo_optimize: 'SEO优化', interlink: '内链建设', deploy: '站点部署', dead_link_scan: '死链扫描' }
const taskTypeTagMap = { content_gen: 'primary', seo_optimize: 'warning', interlink: 'success', deploy: 'info', dead_link_scan: 'danger' }
function taskTypeLabel(v) { return taskTypeMap[v] || v }
function taskTypeTag(v) { return taskTypeTagMap[v] || '' }

function triggerTypeLabel(v) { return { manual: '手动', scheduled: '定时', event: '事件' }[v] || v }

function statusLabel(v) { return { pending: '等待中', running: '运行中', success: '已完成', failed: '失败', cancelled: '已取消' }[v] || v }
function statusTag(v) { return { pending: 'info', running: 'warning', success: 'success', failed: 'danger', cancelled: 'info' }[v] || '' }

function resetForm() {
  form.value = { triggerType: 'manual', priority: 5, maxRetries: 3, status: 'pending' }
}

function getList() {
  loading.value = true
  listOperationTask(queryParams).then(res => {
    taskList.value = res.rows
    total.value = res.total
  }).finally(() => loading.value = false)
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryRef.value?.resetFields(); handleQuery() }

function handleSelectionChange(sel) {
  selectedIds.value = sel.map(r => r.id)
  single.value = sel.length !== 1
  multiple.value = !sel.length
}

function handleAdd() { resetForm(); open.value = true; title.value = '新建运营任务' }

function handleUpdate(row) {
  const id = row?.id || selectedIds.value[0]
  getOperationTask(id).then(res => {
    form.value = { ...res.data }
    open.value = true
    title.value = '编辑运营任务'
  })
}

function handleDelete(row) {
  const ids = row?.id ? [row.id] : selectedIds.value
  ElMessageBox.confirm('确认删除所选运营任务吗？', '提示', { type: 'warning' }).then(() => {
    delOperationTask(ids.join(',')).then(() => {
      getList()
      ElMessage.success('删除成功')
    })
  })
}

function handleRun(row) {
  ElMessageBox.confirm(`确认立即执行「${row.taskName}」？`, '确认', { type: 'info' }).then(() => {
    updateOperationTask({ id: row.id, status: 'running' }).then(() => {
      ElMessage.success('任务已提交执行')
      getList()
    })
  })
}

function cancel() { open.value = false; resetForm() }

function submitForm() {
  taskRef.value?.validate(valid => {
    if (!valid) return
    const fn = form.value.id ? updateOperationTask : addOperationTask
    fn(form.value).then(() => {
      ElMessage.success(form.value.id ? '修改成功' : '新建成功')
      open.value = false
      getList()
    })
  })
}

onMounted(getList)
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.stat-card { border-radius: 8px; }
.stat-inner { display: flex; justify-content: space-between; align-items: center; }
.stat-num { font-size: 30px; font-weight: 700; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.stat-icon { opacity: 0.2; }
</style>
