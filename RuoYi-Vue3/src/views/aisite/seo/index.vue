<template>
  <div class="app-container">
    <!-- 站点选择器 -->
    <el-card shadow="never" class="mb16">
      <el-row :gutter="16" align="middle">
        <el-col :span="6">
          <el-select v-model="selectedSiteId" placeholder="选择站点查看 SEO 数据" style="width:100%" clearable @change="onSiteChange">
            <el-option v-for="s in siteOptions" :key="s.id" :label="s.siteName" :value="s.id" />
          </el-select>
        </el-col>
        <el-col :span="10">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期"
            value-format="YYYY-MM-DD" style="width:100%" @change="handleQuery" />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-col>
        <el-col :span="4" style="text-align:right">
          <el-button type="success" plain icon="Plus" @click="handleRecord" v-hasPermi="['aisite:seo:add']">记录快照</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- KPI 卡片 -->
    <el-row :gutter="16" class="mb16">
      <el-col :span="6" v-for="kpi in kpis" :key="kpi.label">
        <el-card shadow="never" class="kpi-card" :body-style="{ padding:'16px 20px' }">
          <div class="kpi-inner">
            <div>
              <div class="kpi-num" :style="{ color: kpi.color }">{{ kpi.value }}</div>
              <div class="kpi-change" :class="kpi.trend >= 0 ? 'up' : 'down'" v-if="kpi.trend !== null">
                {{ kpi.trend >= 0 ? '▲' : '▼' }} {{ Math.abs(kpi.trend) }}{{ kpi.unit }}
              </div>
              <div class="kpi-label">{{ kpi.label }}</div>
            </div>
            <el-icon class="kpi-bg-icon" :style="{ color: kpi.color }" :size="40">
              <component :is="kpi.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图区域 -->
    <el-row :gutter="16" class="mb16">
      <el-col :span="16">
        <el-card shadow="never" header="点击量 & 展现量趋势">
          <div v-if="!selectedSiteId" class="no-site-tip">
            <el-icon size="48" color="#c0c4cc"><TrendCharts /></el-icon>
            <p>请先选择站点</p>
          </div>
          <div v-else-if="metricsLoading" style="text-align:center;padding:40px">
            <el-icon class="is-loading" size="32"><Loading /></el-icon>
          </div>
          <div v-else-if="chartData.dates.length === 0" class="no-site-tip">
            <p>暂无数据，请先记录 SEO 快照</p>
          </div>
          <div v-else class="chart-container">
            <div class="chart-legend">
              <span class="legend-item clicks">● 点击量</span>
              <span class="legend-item impressions">● 展现量</span>
            </div>
            <div class="chart-bars">
              <div v-for="(d, i) in chartData.dates" :key="i" class="chart-col">
                <div class="bar-group">
                  <div class="bar clicks-bar" :style="{ height: barHeight(chartData.clicks[i], maxClicks) + 'px' }"
                    :title="`点击: ${chartData.clicks[i]}`"></div>
                  <div class="bar impr-bar" :style="{ height: barHeight(chartData.impressions[i], maxImpressions) + 'px' }"
                    :title="`展现: ${chartData.impressions[i]}`"></div>
                </div>
                <div class="bar-label">{{ shortDate(d) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" header="平均排名 & CTR">
          <div v-if="!selectedSiteId || metricsData.length === 0" class="no-site-tip">
            <el-icon size="48" color="#c0c4cc"><DataAnalysis /></el-icon>
            <p>{{ selectedSiteId ? '暂无数据' : '请先选择站点' }}</p>
          </div>
          <div v-else>
            <div class="mini-stat" v-for="item in latestMetrics" :key="item.label">
              <span class="mini-label">{{ item.label }}</span>
              <el-progress :percentage="item.pct" :stroke-width="14" :color="item.color"
                :format="() => item.display" style="flex:1; margin: 0 8px" />
              <span class="mini-val" :style="{ color: item.color }">{{ item.display }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快照列表 -->
    <el-card shadow="never" header="历史快照">
      <el-table v-loading="metricsLoading" :data="metricsData" stripe>
        <el-table-column label="快照日期" prop="snapshotDate" width="120" align="center" />
        <el-table-column label="收录页数" prop="indexedPages" width="100" align="center" />
        <el-table-column label="平均排名" prop="avgPosition" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="positionTag(row.avgPosition)" size="small">{{ row.avgPosition }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="点击量" prop="clicks" width="100" align="center" />
        <el-table-column label="展现量" prop="impressions" width="100" align="center" />
        <el-table-column label="点击率 (CTR)" prop="ctr" width="120" align="center">
          <template #default="{ row }">{{ row.ctr ? (row.ctr * 100).toFixed(2) + '%' : '-' }}</template>
        </el-table-column>
        <el-table-column label="备注数据" align="center" :show-overflow-tooltip="true">
          <template #default="{ row }">{{ row.metricsJson || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button link type="danger" icon="Delete" @click="handleDeleteMetric(row)" v-hasPermi="['aisite:seo:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="fetchMetrics" />
    </el-card>

    <!-- 记录快照对话框 -->
    <el-dialog title="记录 SEO 快照" v-model="openRecord" width="500px" append-to-body>
      <el-form ref="recordRef" :model="recordForm" :rules="recordRules" label-width="110px">
        <el-form-item label="站点" prop="siteId">
          <el-select v-model="recordForm.siteId" placeholder="选择站点" style="width:100%">
            <el-option v-for="s in siteOptions" :key="s.id" :label="s.siteName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="快照日期" prop="snapshotDate">
          <el-date-picker v-model="recordForm.snapshotDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="收录页数" prop="indexedPages">
              <el-input-number v-model="recordForm.indexedPages" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="平均排名">
              <el-input-number v-model="recordForm.avgPosition" :precision="1" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="点击量">
              <el-input-number v-model="recordForm.clicks" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="展现量">
              <el-input-number v-model="recordForm.impressions" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="点击率 (CTR)">
          <el-input-number v-model="recordForm.ctr" :precision="4" :min="0" :max="1" :step="0.001" style="width:100%" />
          <div class="el-form-item__detail">0~1 之间小数，如 0.0526 = 5.26%</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="openRecord = false">取 消</el-button>
        <el-button type="primary" @click="submitRecord">保 存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { TrendCharts, DataAnalysis, Loading } from '@element-plus/icons-vue'
import { listSeoMetrics, getSeoMetricsBySite, addSeoMetrics, delSeoMetrics } from '@/api/aisite/seoMetrics'
import { listSite } from '@/api/aisite/site'

const selectedSiteId = ref(null)
const dateRange = ref([])
const metricsLoading = ref(false)
const metricsData = ref([])
const total = ref(0)
const siteOptions = ref([])
const openRecord = ref(false)
const recordRef = ref(null)

const queryParams = reactive({ pageNum: 1, pageSize: 20, siteId: null, startDate: null, endDate: null })
const recordForm = ref({ siteId: null, snapshotDate: null, indexedPages: 0, avgPosition: null, clicks: 0, impressions: 0, ctr: null })
const recordRules = reactive({
  siteId: [{ required: true, message: '请选择站点', trigger: 'change' }],
  snapshotDate: [{ required: true, message: '请选择日期', trigger: 'change' }]
})

/* KPI 计算 */
const latest = computed(() => metricsData.value[0] || {})
const prev = computed(() => metricsData.value[1] || {})

const kpis = computed(() => [
  { label: '收录页数', value: latest.value.indexedPages ?? '-', trend: diff(latest.value.indexedPages, prev.value.indexedPages), unit: '', color: '#409eff', icon: 'Document' },
  { label: '平均排名', value: latest.value.avgPosition ?? '-', trend: diff(prev.value.avgPosition, latest.value.avgPosition), unit: '', color: '#e6a23c', icon: 'TrendCharts' },
  { label: '点击量', value: latest.value.clicks ?? '-', trend: diff(latest.value.clicks, prev.value.clicks), unit: '', color: '#67c23a', icon: 'Cursor' },
  { label: '展现量', value: latest.value.impressions ?? '-', trend: diff(latest.value.impressions, prev.value.impressions), unit: '', color: '#9b59b6', icon: 'View' }
])

function diff(a, b) {
  if (a == null || b == null) return null
  return a - b
}

/* 趋势图数据 */
const chartData = computed(() => {
  const sorted = [...metricsData.value].reverse()
  return {
    dates: sorted.map(r => r.snapshotDate),
    clicks: sorted.map(r => r.clicks || 0),
    impressions: sorted.map(r => r.impressions || 0)
  }
})
const maxClicks = computed(() => Math.max(1, ...chartData.value.clicks))
const maxImpressions = computed(() => Math.max(1, ...chartData.value.impressions))
function barHeight(val, max) { return Math.round((val / max) * 80) }
function shortDate(d) { return d ? d.slice(5) : '' }

/* 右侧小统计 */
const latestMetrics = computed(() => {
  if (!latest.value.avgPosition && !latest.value.ctr) return []
  return [
    { label: '平均排名', display: latest.value.avgPosition || '-',
      pct: latest.value.avgPosition ? Math.max(0, 100 - latest.value.avgPosition * 2) : 0, color: '#e6a23c' },
    { label: '点击率 CTR', display: latest.value.ctr ? (latest.value.ctr * 100).toFixed(2) + '%' : '-',
      pct: latest.value.ctr ? +(latest.value.ctr * 100).toFixed(1) : 0, color: '#409eff' }
  ]
})

function positionTag(v) {
  if (!v) return ''
  if (v <= 5) return 'success'
  if (v <= 20) return ''
  return 'danger'
}

async function loadSites() {
  const res = await listSite({ pageNum: 1, pageSize: 200 })
  siteOptions.value = res.rows || []
}

function onSiteChange(id) { queryParams.siteId = id; fetchMetrics() }

function fetchMetrics() {
  if (!selectedSiteId.value && !queryParams.siteId) return
  metricsLoading.value = true
  const params = { ...queryParams, siteId: selectedSiteId.value || queryParams.siteId }
  if (dateRange.value?.length === 2) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }
  listSeoMetrics(params).then(res => {
    metricsData.value = res.rows || []
    total.value = res.total || 0
  }).finally(() => metricsLoading.value = false)
}

function handleQuery() { queryParams.pageNum = 1; fetchMetrics() }
function resetQuery() { selectedSiteId.value = null; dateRange.value = []; metricsData.value = []; total.value = 0 }

function handleRecord() {
  recordForm.value = { siteId: selectedSiteId.value, snapshotDate: new Date().toISOString().slice(0, 10), indexedPages: 0, avgPosition: null, clicks: 0, impressions: 0, ctr: null }
  openRecord.value = true
}

function submitRecord() {
  recordRef.value?.validate(valid => {
    if (!valid) return
    addSeoMetrics(recordForm.value).then(() => {
      ElMessage.success('快照已记录')
      openRecord.value = false
      if (selectedSiteId.value) fetchMetrics()
    })
  })
}

function handleDeleteMetric(row) {
  ElMessageBox.confirm(`确认删除 ${row.snapshotDate} 的快照？`, '提示', { type: 'warning' }).then(() => {
    delSeoMetrics(row.id).then(() => {
      ElMessage.success('删除成功')
      fetchMetrics()
    })
  })
}

onMounted(loadSites)
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.kpi-card { border-radius: 8px; }
.kpi-inner { display: flex; justify-content: space-between; align-items: center; }
.kpi-num { font-size: 28px; font-weight: 700; }
.kpi-label { font-size: 13px; color: #909399; margin-top: 4px; }
.kpi-change { font-size: 12px; margin-top: 2px; }
.kpi-change.up { color: #67c23a; }
.kpi-change.down { color: #f56c6c; }
.kpi-bg-icon { opacity: 0.18; }
.no-site-tip { text-align: center; padding: 40px 0; color: #c0c4cc; }
.chart-container { padding: 8px 0; }
.chart-legend { margin-bottom: 8px; font-size: 12px; }
.legend-item { margin-right: 16px; }
.legend-item.clicks { color: #409eff; }
.legend-item.impressions { color: #e6a23c; }
.chart-bars { display: flex; align-items: flex-end; gap: 6px; height: 100px; overflow-x: auto; }
.chart-col { display: flex; flex-direction: column; align-items: center; min-width: 28px; }
.bar-group { display: flex; align-items: flex-end; gap: 2px; }
.bar { width: 10px; border-radius: 3px 3px 0 0; transition: height 0.3s; }
.clicks-bar { background: #409eff; }
.impr-bar { background: #e6a23c; }
.bar-label { font-size: 10px; color: #909399; margin-top: 3px; }
.mini-stat { display: flex; align-items: center; margin-bottom: 16px; }
.mini-label { width: 70px; font-size: 12px; color: #606266; flex-shrink: 0; }
.mini-val { font-size: 13px; font-weight: 600; width: 55px; text-align: right; }
</style>
