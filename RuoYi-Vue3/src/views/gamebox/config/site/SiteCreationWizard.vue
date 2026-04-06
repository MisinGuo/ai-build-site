<template>
  <el-dialog
    v-model="visible"
    title="新建站点向导"
    width="780px"
    :close-on-click-modal="false"
    @closed="handleClosed"
    append-to-body
  >
    <!-- 步骤条 -->
    <el-steps :active="currentStep" align-center finish-status="success" style="margin-bottom:24px">
      <el-step title="选择模板" />
      <el-step title="业务配置" />
      <el-step title="选择账号" />
      <el-step title="确认创建" />
    </el-steps>

    <!-- ==================== Step 0: 选择模板 ==================== -->
    <div v-if="currentStep === 0" class="wizard-step">
      <div class="step-header">
        <span class="step-title">选择站点模板</span>
        <span class="step-hint">模板由管理员统一维护，选定模板后站点类型自动确定</span>
      </div>
      <div v-if="templatesLoading" style="text-align:center;padding:40px">
        <el-icon class="is-loading" size="32"><Loading /></el-icon>
        <div style="margin-top:8px;color:#909399">正在加载模板...</div>
      </div>
      <template v-else>
        <div v-if="templates.length === 0" style="margin-bottom:16px">
          <el-empty description="暂无可用模板，请联系管理员添加" :image-size="80" />
        </div>
        <div v-else class="template-cards">
          <div
            v-for="t in templates"
            :key="t.id"
            class="template-card"
            :class="{ active: form.templateId === t.id }"
            @click="selectTemplate(t)"
          >
            <div class="tmpl-preview">
              <img v-if="t.previewImage" :src="t.previewImage" alt="预览" style="width:100%;height:100%;object-fit:cover" />
              <el-icon v-else size="40" color="#c0c4cc"><Picture /></el-icon>
            </div>
            <div class="tmpl-info">
              <div class="tmpl-name-row">
                <span class="tmpl-name">{{ t.name }}</span>
                <el-tag v-if="t.siteFunctionType === 'seo_site'" type="primary" size="small">获客站</el-tag>
                <el-tag v-else-if="t.siteFunctionType === 'landing_site'" type="success" size="small">落地站</el-tag>
              </div>
              <div class="tmpl-desc">{{ t.description }}</div>
              <el-tag v-if="t.framework" size="small" type="info">{{ t.framework }}</el-tag>
            </div>
          </div>
        </div>

        <el-divider content-position="center" style="margin:12px 0 8px">或</el-divider>
        <div
          class="template-card free-card"
          :class="{ active: form.setupMode === 'free' }"
          @click="selectFree"
        >
          <div style="display:flex;align-items:center;gap:14px;padding:14px 16px">
            <el-icon size="28" color="#909399"><Edit /></el-icon>
            <div>
              <div style="font-weight:600;color:#606266;font-size:14px">不使用模板（自由创建）</div>
              <div style="font-size:12px;color:#c0c4cc;margin-top:3px">仅创建站点数据库记录，代码仓库可后续在代码管理中配置</div>
            </div>
            <el-tag v-if="form.setupMode === 'free'" type="info" size="small" style="margin-left:auto">已选</el-tag>
          </div>
        </div>

        <div v-if="form.setupMode === 'free'" style="margin-top:12px;padding:0 4px">
          <el-form :model="form" label-width="90px" size="small">
            <el-form-item label="站点类型">
              <el-select v-model="form.siteFunctionType" placeholder="选填：获客站 / 落地站" clearable style="width:220px">
                <el-option label="获客站 (SEO)" value="seo_site" />
                <el-option label="落地站 (广告落地页)" value="landing_site" />
              </el-select>
              <span style="margin-left:8px;font-size:12px;color:#c0c4cc">可后续在站点设置中修改</span>
            </el-form-item>
          </el-form>
        </div>
      </template>
    </div>

    <!-- ==================== Step 1: 业务配置 ==================== -->
    <div v-if="currentStep === 1" class="wizard-step">
      <div class="step-header">
        <span class="step-title">填写站点业务信息</span>
        <el-tag v-if="selectedTemplate" :type="selectedTemplate.siteFunctionType === 'seo_site' ? 'primary' : 'success'" size="small" style="margin-left:8px">
          {{ selectedTemplate.siteFunctionType === 'seo_site' ? '获客站' : '落地站' }}
        </el-tag>
        <el-tag v-else-if="form.siteFunctionType" :type="form.siteFunctionType === 'seo_site' ? 'primary' : 'success'" size="small" style="margin-left:8px">
          {{ form.siteFunctionType === 'seo_site' ? '获客站' : '落地站' }}
        </el-tag>
      </div>
      <el-form ref="infoFormRef" :model="form" :rules="infoRules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="站点名称" prop="name">
              <el-input v-model="form.name" placeholder="如：游戏盒子官网" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="站点编码" prop="code">
              <el-input v-model="form.code" placeholder="小写字母+数字，如：gamebox" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="站点域名" prop="domain">
              <el-input v-model="form.domain" placeholder="如：www.example.com" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="默认语言" prop="defaultLocale">
              <el-select v-model="form.defaultLocale" style="width:100%">
                <el-option label="中文 (zh-CN)" value="zh-CN" />
                <el-option label="English (en)" value="en" />
                <el-option label="繁體中文 (zh-TW)" value="zh-TW" />
                <el-option label="日本語 (ja)" value="ja" />
                <el-option label="한국어 (ko)" value="ko" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="API 地址">
              <el-input v-model="form.apiUrl" placeholder="后端 API baseURL（可后续配置）" />
            </el-form-item>
          </el-col>
        </el-row>
        <template v-if="form.siteFunctionType === 'landing_site'">
          <el-row>
            <el-col :span="12">
              <el-form-item label="主站地址">
                <el-input v-model="form.mainSiteUrl" placeholder="主站 URL（跳转用）" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="跳转域名">
                <el-input v-model="form.jumpDomain" placeholder="跳转目标域名" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-form>
    </div>

    <!-- ==================== Step 2: 选择账号 ==================== -->
    <div v-if="currentStep === 2" class="wizard-step">
      <div class="step-header">
        <span class="step-title">选择部署账号</span>
        <span class="step-hint">系统将使用所选账号自动建仓、推代码</span>
      </div>

      <el-form ref="accountFormRef" :model="form" :rules="computedAccountRules" label-width="120px">
        <el-divider content-position="left">
          <el-icon><Connection /></el-icon> Git 仓库
        </el-divider>

        <el-form-item label="Git 账号" prop="gitAccountId">
          <el-select
            v-model="form.gitAccountId"
            placeholder="请选择 Git 账号"
            style="width:100%"
            v-loading="gitAccountsLoading"
            @change="onGitAccountChange"
          >
            <el-option
              v-for="a in gitAccounts"
              :key="a.id"
              :label="`${a.name}（${a.provider} / ${a.account}）`"
              :value="a.id"
            >
              <span>{{ a.name }}</span>
              <span style="float:right;color:#909399;font-size:12px;margin-left:16px">{{ a.provider }} / {{ a.account }}</span>
            </el-option>
          </el-select>
          <div v-if="gitAccounts.length === 0 && !gitAccountsLoading" style="font-size:12px;color:#e6a23c;margin-top:4px">
            暂无可用 Git 账号，请先在「基础配置 → Git账号管理」中添加
          </div>
        </el-form-item>

        <el-form-item label="仓库名" prop="repoName">
          <el-input v-model="form.repoName" :placeholder="form.code || 'my-site'" />
          <div style="font-size:12px;color:#909399;margin-top:4px">
            系统将在所选账号下自动创建该仓库，仅填写名称（不含 .git 后缀）
          </div>
        </el-form-item>

        <el-form-item label="仓库可见性">
          <el-radio-group v-model="form.repoPrivate">
            <el-radio :value="true">私有（推荐）</el-radio>
            <el-radio :value="false">公开</el-radio>
          </el-radio-group>
        </el-form-item>

        <template v-if="form.setupMode === 'template'">
          <el-divider content-position="left">
            <el-icon><Upload /></el-icon> Cloudflare 部署（可选）
          </el-divider>

          <el-form-item label="CF 账号">
            <el-select
              v-model="form.cfAccountId"
              placeholder="不选则跳过 CF 部署"
              clearable
              style="width:100%"
              v-loading="cfAccountsLoading"
            >
              <el-option
                v-for="a in cfAccounts"
                :key="a.id"
                :label="`${a.name}（${a.accountId}）`"
                :value="a.id"
              >
                <span>{{ a.name }}</span>
                <span style="float:right;color:#909399;font-size:12px;margin-left:16px">{{ a.accountId }}</span>
              </el-option>
            </el-select>
            <div v-if="cfAccounts.length === 0 && !cfAccountsLoading" style="font-size:12px;color:#909399;margin-top:4px">
              暂无可用 CF 账号（可在「基础配置 → CF账号管理」中添加）
            </div>
          </el-form-item>

          <el-form-item v-if="form.cfAccountId" label="CF 项目名">
            <el-input v-model="form.cfProjectName" :placeholder="form.code || 'my-site'" />
            <div style="font-size:12px;color:#909399;margin-top:4px">Cloudflare Pages 项目名</div>
            <div style="font-size:12px;margin-top:5px;padding:5px 8px;border-radius:4px;background:#0d2a1a;color:#67c23a;border:1px solid #1a4a2a;line-height:1.7">
              ℹ️ 此名称会在初次推送模板代码时自动写入 <code>wrangler.toml</code> 的 <code>name</code> 字段。该字段决定 CI 部署到 Cloudflare Workers 时的项目名，推送分支后 GitHub Actions 将自动使用此名称部署。
            </div>
          </el-form-item>
        </template>

        <el-alert
          v-if="form.setupMode === 'free'"
          type="info"
          :closable="false"
          style="margin-top:8px"
          title="自由创建模式：选择 Git 账号后系统将创建空仓库。CF 部署可后续在代码管理中配置。"
        />
      </el-form>
    </div>

    <!-- ==================== Step 3: 确认创建 ==================== -->
    <div v-if="currentStep === 3" class="wizard-step">
      <div class="step-header">
        <span class="step-title">确认创建信息</span>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="站点类型">
          <el-tag v-if="form.siteFunctionType === 'seo_site'" type="primary">获客站</el-tag>
          <el-tag v-else-if="form.siteFunctionType === 'landing_site'" type="success">落地站</el-tag>
          <span v-else style="color:#c0c4cc">未设置</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建方式">
          <span v-if="form.setupMode === 'template'">使用模板：<strong>{{ selectedTemplateName }}</strong></span>
          <span v-else>自由创建</span>
        </el-descriptions-item>
        <el-descriptions-item label="站点名称">{{ form.name }}</el-descriptions-item>
        <el-descriptions-item label="站点编码">{{ form.code }}</el-descriptions-item>
        <el-descriptions-item label="站点域名" :span="2">{{ form.domain }}</el-descriptions-item>
        <el-descriptions-item label="默认语言">{{ form.defaultLocale }}</el-descriptions-item>
        <el-descriptions-item label="Git 账号">
          <span v-if="selectedGitAccount">
            {{ selectedGitAccount.name }}
            <span style="color:#909399;font-size:12px">（{{ selectedGitAccount.provider }} / {{ selectedGitAccount.account }}）</span>
          </span>
          <span v-else style="color:#c0c4cc">未选择</span>
        </el-descriptions-item>
        <el-descriptions-item label="Git 仓库">
          <el-tag type="success" size="small" style="margin-right:6px">自动创建</el-tag>
          {{ selectedGitAccount ? selectedGitAccount.account : '?' }} / {{ form.repoName || form.code || 'my-site' }}
        </el-descriptions-item>
        <el-descriptions-item label="仓库可见性">{{ form.repoPrivate ? '私有' : '公开' }}</el-descriptions-item>
        <el-descriptions-item v-if="selectedCfAccount" label="CF 账号">{{ selectedCfAccount.name }}</el-descriptions-item>
        <el-descriptions-item v-if="selectedCfAccount" label="CF 项目">{{ form.cfProjectName || form.code || 'my-site' }}</el-descriptions-item>
      </el-descriptions>

      <el-alert
        v-if="form.setupMode === 'template'"
        type="warning"
        :closable="false"
        style="margin-top:16px"
        title="点击「开始创建」后，系统将自动：① 创建 GitHub 仓库 → ② 设置 GH Actions 将密鑰 (CF_API_TOKEN / CF_ACCOUNT_ID) → ③ 推送模板代码（含 GitHub Actions 工作流），并将 wrangler.toml 的 name 字段替换为您配置的 CF 项目名 → ④（如已选 CF 账号）创建 Cloudflare Worker 占位项目、保存部署配置。完成后每次 git push 将自动部署。" />
      <el-alert
        v-else
        type="info"
        :closable="false"
        style="margin-top:16px"
        title="自由创建模式：系统将创建站点记录和空 Git 仓库，后续可在「代码管理」中手动推送代码。"
      />

      <div v-if="creating" style="margin-top:16px;text-align:center">
        <el-icon class="is-loading" size="24"><Loading /></el-icon>
        <span style="margin-left:8px;color:#909399">{{ creatingMsg }}</span>
      </div>
      <!-- 创建中：实时日志常驻展示 -->
      <div v-if="creating && setupLog" style="margin-top:12px">
        <div style="font-size:12px;color:#606266;margin-bottom:4px">操作日志（实时）：</div>
        <pre ref="setupLogPreRef" style="font-size:12px;max-height:200px;overflow:auto;white-space:pre-wrap;background:#f5f7fa;padding:8px;border-radius:4px;border:1px solid #ebeef5">{{ setupLog }}</pre>
      </div>
      <!-- 创建完成后折叠展示 -->
      <el-collapse v-if="!creating && setupLog" style="margin-top:12px">
        <el-collapse-item title="查看操作日志">
          <pre style="font-size:12px;max-height:200px;overflow:auto;white-space:pre-wrap">{{ setupLog }}</pre>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- ==================== 底部按钮 ==================== -->
    <template #footer>
      <el-button @click="visible = false" :disabled="creating">取 消</el-button>
      <el-button v-if="currentStep > 0" @click="prevStep" :disabled="creating">上一步</el-button>
      <el-button v-if="currentStep < 3" type="primary" @click="nextStep" :disabled="creating">下一步</el-button>
      <el-button v-if="currentStep === 3" type="primary" :loading="creating" @click="handleCreate">开始创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, Picture, Edit, Connection, Upload } from '@element-plus/icons-vue'
import { addSite } from '@/api/gamebox/site'
import { setupFromTemplate } from '@/api/gamebox/codeManage'
import { listEnabledTemplates } from '@/api/gamebox/siteTemplate'
import { listEnabledGitAccounts } from '@/api/gamebox/gitAccount'
import { listEnabledCfAccounts } from '@/api/gamebox/cfAccount'
import useUserStore from '@/store/modules/user'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})
const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const currentStep = ref(0)
const creating = ref(false)
const creatingMsg = ref('')
const setupLog = ref('')
const setupLogPreRef = ref(null)

// 日志有新内容时自动滚到底部
watch(setupLog, () => {
  nextTick(() => {
    if (setupLogPreRef.value) {
      setupLogPreRef.value.scrollTop = setupLogPreRef.value.scrollHeight
    }
  })
})

const templates = ref([])
const templatesLoading = ref(false)
const gitAccounts = ref([])
const gitAccountsLoading = ref(false)
const cfAccounts = ref([])
const cfAccountsLoading = ref(false)

const infoFormRef = ref()
const accountFormRef = ref()

const userStore = useUserStore()

const defaultForm = () => ({
  siteFunctionType: null,
  setupMode: 'template',
  templateId: null,
  name: '',
  code: '',
  domain: '',
  defaultLocale: 'zh-CN',
  apiUrl: '',
  mainSiteUrl: '',
  jumpDomain: '',
  gitAccountId: null,
  repoName: '',
  repoPrivate: true,
  cfAccountId: null,
  cfProjectName: ''
})

const form = ref(defaultForm())

watch(visible, (v) => {
  if (v) {
    loadTemplates()
    loadGitAccounts()
    loadCfAccounts()
  }
})

function loadTemplates() {
  templatesLoading.value = true
  listEnabledTemplates().then(res => {
    templates.value = res.data || []
  }).catch(() => { templates.value = [] }).finally(() => { templatesLoading.value = false })
}

function loadGitAccounts() {
  gitAccountsLoading.value = true
  listEnabledGitAccounts().then(res => {
    gitAccounts.value = res.data || []
    const def = gitAccounts.value.find(a => a.isDefault === 1)
    if (def && !form.value.gitAccountId) form.value.gitAccountId = def.id
  }).catch(() => { gitAccounts.value = [] }).finally(() => { gitAccountsLoading.value = false })
}

function loadCfAccounts() {
  cfAccountsLoading.value = true
  listEnabledCfAccounts().then(res => {
    cfAccounts.value = res.data || []
    const def = cfAccounts.value.find(a => a.isDefault === 1)
    if (def && !form.value.cfAccountId) form.value.cfAccountId = def.id
  }).catch(() => { cfAccounts.value = [] }).finally(() => { cfAccountsLoading.value = false })
}

function selectTemplate(t) {
  form.value.templateId = t.id
  form.value.siteFunctionType = t.siteFunctionType
  form.value.setupMode = 'template'
}

function selectFree() {
  form.value.templateId = null
  form.value.setupMode = 'free'
  form.value.siteFunctionType = null
}

function onGitAccountChange() {
  if (!form.value.repoName && form.value.code) {
    form.value.repoName = form.value.code
  }
}

const selectedTemplate = computed(() => templates.value.find(t => t.id === form.value.templateId) || null)
const selectedTemplateName = computed(() => selectedTemplate.value ? selectedTemplate.value.name : '未选择')
const selectedGitAccount = computed(() => gitAccounts.value.find(a => a.id === form.value.gitAccountId) || null)
const selectedCfAccount = computed(() => cfAccounts.value.find(a => a.id === form.value.cfAccountId) || null)

const infoRules = {
  name: [{ required: true, message: '请输入站点名称', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入站点编码', trigger: 'blur' },
    { pattern: /^[a-z0-9_-]+$/, message: '只允许小写字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  domain: [{ required: true, message: '请输入站点域名', trigger: 'blur' }],
  defaultLocale: [{ required: true, message: '请选择默认语言', trigger: 'change' }]
}

const computedAccountRules = computed(() => ({
  gitAccountId: [{ required: true, message: '请选择 Git 账号', trigger: 'change' }],
  repoName: [
    { required: true, message: '请输入仓库名', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value && value.endsWith('.git')) {
          callback(new Error('仓库名无需以 .git 结尾'))
        } else if (value && /[/\\\s]/.test(value)) {
          callback(new Error('仓库名不能含斜杠或空格'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}))

async function nextStep() {
  if (currentStep.value === 0) {
    if (form.value.setupMode === 'template' && !form.value.templateId) {
      ElMessage.warning('请选择一个站点模板')
      return
    }
  }
  if (currentStep.value === 1) {
    const valid = await infoFormRef.value?.validate().catch(() => false)
    if (!valid) return
    if (!form.value.repoName) form.value.repoName = form.value.code
    if (!form.value.cfProjectName) form.value.cfProjectName = form.value.code
  }
  if (currentStep.value === 2) {
    const valid = await accountFormRef.value?.validate().catch(() => false)
    if (!valid) return
  }
  currentStep.value++
}

function prevStep() { currentStep.value-- }

async function handleCreate() {
  creating.value = true
  setupLog.value = ''
  try {
    creatingMsg.value = '正在创建站点记录...'
    const siteData = {
      userId: userStore.id,
      name: form.value.name,
      code: form.value.code,
      domain: form.value.domain,
      defaultLocale: form.value.defaultLocale,
      siteFunctionType: form.value.siteFunctionType,
      setupStatus: 'created',
      templateId: form.value.setupMode === 'template' ? form.value.templateId : null,
      status: '1'
    }
    const siteRes = await addSite(siteData)
    if (siteRes.code !== 200) {
      ElMessage.error('创建站点失败：' + siteRes.msg)
      return
    }
    const siteId = siteRes.data
    const repoName = form.value.repoName || form.value.code || 'my-site'

    if (form.value.setupMode === 'template') {
      creatingMsg.value = '正在初始化代码仓库（创建仓库、设置 Secrets、推送模板代码' + (form.value.cfAccountId ? '、创建 CF Worker' : '') + '）...'
      const startRes = await setupFromTemplate(siteId, {
        templateId: form.value.templateId,
        apiUrl: form.value.apiUrl,
        domain: form.value.domain,
        mainSiteUrl: form.value.mainSiteUrl,
        jumpDomain: form.value.jumpDomain,
        defaultLocale: form.value.defaultLocale,
        gitAccountId: form.value.gitAccountId,
        repoName,
        repoPrivate: form.value.repoPrivate,
        cfAccountId: form.value.cfAccountId || null,
        cfProjectName: form.value.cfProjectName || repoName
      })
      const taskId = startRes.data?.taskId
      if (!taskId) {
        ElMessage.warning('站点已创建，但代码初始化任务启动失败，请在「代码管理」中重试。')
      } else {
        // 轮询直到任务完成
        const finalData = await new Promise((resolve) => {
          const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
          const ws = new WebSocket(`${wsProtocol}//${window.location.host}/ws/rebuild-log/${taskId}`)
          ws.onmessage = (event) => {
            try {
              const msg = JSON.parse(event.data)
              if (msg.type === 'log') {
                setupLog.value += msg.data
              } else if (msg.type === 'done') {
                ws.close()
                resolve({ success: !!msg.success, message: msg.message })
              }
            } catch (_) { /* 忽略 */ }
          }
          ws.onerror = () => {
            setupLog.value += '\n[WebSocket 连接异常]\n'
            ws.close()
            resolve({ success: false, message: 'WebSocket 连接异常' })
          }
          ws.onclose = () => { /* close 由 done 帧触发，此处不做处理 */ }
        })
        if (!finalData.success) {
          ElMessage.warning('站点已创建，但代码初始化未完全成功，请查看日志后在「代码管理」中处理。')
        } else if (form.value.cfAccountId) {
          ElMessage.success('站点创建成功！代码已推送，Cloudflare Worker 已就绪。每次 git push 将自动部署。')
        } else {
          ElMessage.success('站点创建成功，代码已推送到 Git 仓库！')
        }
      }
    } else {
      creatingMsg.value = '正在创建 Git 空仓库...'
      try {
        const freeStart = await setupFromTemplate(siteId, {
          gitAccountId: form.value.gitAccountId,
          repoName,
          repoPrivate: form.value.repoPrivate,
          freeMode: true
        })
        const freeTaskId = freeStart.data?.taskId
        if (freeTaskId) {
          // 空仓库创建较快，等待 WebSocket 完成信号
          await new Promise((resolve) => {
            const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
            const ws = new WebSocket(`${wsProtocol}//${window.location.host}/ws/rebuild-log/${freeTaskId}`)
            ws.onmessage = (event) => {
              try {
                const msg = JSON.parse(event.data)
                if (msg.type === 'done') { ws.close(); resolve() }
              } catch (_) { /* 忽略 */ }
            }
            ws.onerror = () => { resolve() }
            ws.onclose = () => { resolve() }
          })
        }
      } catch (_) { /* 仓库创建失败不阻断 */ }
      ElMessage.success('站点创建成功！')
    }

    emit('success')
    visible.value = false
  } catch (e) {
    ElMessage.error('创建过程中发生异常：' + (e.message || String(e)))
  } finally {
    creating.value = false
    creatingMsg.value = ''
  }
}

function handleClosed() {
  currentStep.value = 0
  setupLog.value = ''
  creating.value = false
  form.value = defaultForm()
}
</script>

<style scoped>
.wizard-step { min-height: 260px; }
.step-header { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; }
.step-title { font-size: 15px; font-weight: 600; color: #303133; }
.step-hint { font-size: 12px; color: #c0c4cc; }
.template-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  gap: 12px;
  margin-bottom: 4px;
}
.template-card {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.template-card:hover,
.template-card.active {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
}
.free-card { border-style: dashed; }
.tmpl-preview {
  height: 90px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tmpl-info { padding: 10px 12px; }
.tmpl-name-row { display: flex; align-items: center; gap: 6px; margin-bottom: 4px; }
.tmpl-name { font-size: 14px; font-weight: 600; color: #303133; }
.tmpl-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
