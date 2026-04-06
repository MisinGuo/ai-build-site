<template>
  <div class="app-container builder-container">
    <el-row :gutter="16" style="height:100%">
      <!-- 左侧：对话面板 -->
      <el-col :span="14">
        <el-card class="chat-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><MagicStick /></el-icon>
              <span class="ml8">AI 建站助手</span>
              <el-tag type="success" size="small" class="ml8">智能配置</el-tag>
              <div class="header-right">
                <el-select v-model="selectedSiteId" placeholder="选择站点（可选）" clearable style="width:180px" size="small">
                  <el-option v-for="s in siteList" :key="s.id" :label="s.name" :value="s.id" />
                </el-select>
              </div>
            </div>
          </template>

          <!-- 对话历史 -->
          <div class="chat-body" ref="chatBodyRef">
            <!-- 引导卡片 -->
            <div v-if="messages.length === 0" class="guide-section">
              <div class="guide-title">你好！我可以帮你：</div>
              <el-row :gutter="12" class="guide-cards">
                <el-col :span="12" v-for="g in guides" :key="g.text">
                  <div class="guide-card" @click="fillInput(g.text)">
                    <el-icon class="guide-icon" :color="g.color"><component :is="g.icon" /></el-icon>
                    <div class="guide-label">{{ g.text }}</div>
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 消息列表 -->
            <div v-for="(msg, i) in messages" :key="i" :class="['msg-row', msg.role]">
              <el-avatar v-if="msg.role === 'assistant'" :size="32" class="msg-avatar" style="background:#409eff">AI</el-avatar>
              <div :class="['msg-bubble', msg.role]">
                <div v-if="msg.role === 'assistant'" class="msg-content" v-html="renderMarkdown(msg.content)"></div>
                <div v-else class="msg-content">{{ msg.content }}</div>
                <div class="msg-time">{{ msg.time }}</div>
              </div>
              <el-avatar v-if="msg.role === 'user'" :size="32" class="msg-avatar" style="background:#67c23a">我</el-avatar>
            </div>

            <!-- 加载中 -->
            <div v-if="thinking" class="msg-row assistant">
              <el-avatar :size="32" class="msg-avatar" style="background:#409eff">AI</el-avatar>
              <div class="msg-bubble assistant">
                <span class="thinking-dots"><span></span><span></span><span></span></span>
              </div>
            </div>
          </div>

          <!-- 输入区 -->
          <div class="chat-input">
            <el-input
              v-model="inputText"
              type="textarea"
              :rows="3"
              placeholder="描述你的站点需求…&#10;例如：帮我创建一个游戏推广站，主题深色科技风，需要游戏列表和详情页"
              resize="none"
              @keydown.ctrl.enter="sendMessage"
            />
            <div class="input-footer">
              <span class="tip">Ctrl+Enter 发送</span>
              <div>
                <el-button size="small" @click="clearChat" :disabled="messages.length === 0">清空对话</el-button>
                <el-button type="primary" size="small" :loading="thinking" @click="sendMessage" :disabled="!inputText.trim()">
                  <el-icon><Promotion /></el-icon> 发送
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：配置预览 -->
      <el-col :span="10">
        <el-card class="config-card" shadow="never">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span class="ml8">site_config 预览</span>
              <div class="header-right" v-if="configJson">
                <el-button size="small" icon="CopyDocument" @click="copyConfig">复制</el-button>
                <el-button size="small" type="primary" icon="Check" @click="saveConfig">应用到站点</el-button>
              </div>
            </div>
          </template>

          <div v-if="!configJson" class="config-empty">
            <el-icon style="font-size:48px;color:#c0c4cc"><Document /></el-icon>
            <p>AI 生成的 site_config 会在此展示</p>
          </div>
          <div v-else class="config-preview">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="JSON" name="json">
                <div class="json-viewer">
                  <pre class="json-code">{{ formattedConfig }}</pre>
                </div>
              </el-tab-pane>
              <el-tab-pane label="页面结构" name="pages">
                <div class="pages-tree">
                  <div v-for="(p, i) in configPages" :key="i" class="page-item">
                    <el-icon><Link /></el-icon>
                    <span class="page-path">{{ p.path }}</span>
                    <el-tag size="small" class="ml8">
                      {{ p.sections?.length || 0 }} 个区块
                    </el-tag>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="主题" name="theme">
                <div v-if="configTheme" class="theme-preview">
                  <div class="theme-row">
                    <span>主色调</span>
                    <div class="color-box" :style="{background: configTheme.colorPrimary}"></div>
                    <code>{{ configTheme.colorPrimary }}</code>
                  </div>
                  <div class="theme-row">
                    <span>预设风格</span>
                    <el-tag>{{ configTheme.preset || '默认' }}</el-tag>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MagicStick, Promotion, Setting, Document, Link, CopyDocument, Check } from '@element-plus/icons-vue'
import { listSite, updateSite } from '@/api/aisite/site'

const siteList = ref([])
const selectedSiteId = ref(null)
const messages = ref([])
const inputText = ref('')
const thinking = ref(false)
const configJson = ref('')
const activeTab = ref('json')
const chatBodyRef = ref(null)

const guides = [
  { text: '帮我创建一个游戏推广站，深色科技风', icon: 'Monitor', color: '#409eff' },
  { text: '生成一个房产中介站配置，专业简洁风', icon: 'House', color: '#67c23a' },
  { text: '为当前站点添加文章列表和详情页', icon: 'Document', color: '#e6a23c' },
  { text: '修改站点主题色为暗紫色', icon: 'Brush', color: '#9b59b6' }
]

const formattedConfig = computed(() => {
  try { return JSON.stringify(JSON.parse(configJson.value), null, 2) }
  catch { return configJson.value }
})

const configPages = computed(() => {
  try {
    const cfg = JSON.parse(configJson.value)
    return cfg.pages || []
  } catch { return [] }
})

const configTheme = computed(() => {
  try {
    const cfg = JSON.parse(configJson.value)
    return cfg.theme || null
  } catch { return null }
})

function fillInput(text) { inputText.value = text }

function renderMarkdown(text) {
  return text
    .replace(/```json([\s\S]*?)```/g, '<pre class="code-block">$1</pre>')
    .replace(/```([\s\S]*?)```/g, '<pre class="code-block">$1</pre>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br/>')
}

function clearChat() {
  messages.value = []
  configJson.value = ''
}

function scrollToBottom() {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
  })
}

function now() {
  return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || thinking.value) return
  messages.value.push({ role: 'user', content: text, time: now() })
  inputText.value = ''
  thinking.value = true
  scrollToBottom()

  try {
    // 模拟 AI 响应（真实环境对接后端 /aisite/builder/chat 接口）
    await new Promise(r => setTimeout(r, 1200))
    const reply = generateMockReply(text)
    messages.value.push({ role: 'assistant', content: reply.text, time: now() })
    if (reply.config) configJson.value = JSON.stringify(reply.config)
  } catch (e) {
    ElMessage.error('请求失败：' + e.message)
  } finally {
    thinking.value = false
    scrollToBottom()
  }
}

function generateMockReply(input) {
  const isGame = input.includes('游戏')
  const isHouse = input.includes('房产') || input.includes('房屋')
  const isDark = input.includes('深色') || input.includes('暗')

  const config = {
    siteId: selectedSiteId.value || 1,
    name: isGame ? '游戏推广站' : isHouse ? '房产中介站' : 'AI 建站',
    defaultLocale: 'zh-CN',
    supportedLocales: ['zh-CN', 'en'],
    theme: {
      colorPrimary: isDark ? '#6366f1' : isGame ? '#3b82f6' : '#10b981',
      preset: isDark ? 'dark-tech' : isHouse ? 'clean-corporate' : 'light-minimal'
    },
    pages: [
      {
        path: '/',
        sections: [
          { component: 'HeroBanner', props: { title: isGame ? '发现精彩游戏' : '优质房源推荐', subtitle: 'AI 驱动的高效建站平台' } },
          { component: 'CardGrid', props: { typeCode: isGame ? 'game' : isHouse ? 'property' : 'article', columns: 3 } }
        ]
      },
      {
        path: isGame ? '/games/:slug' : isHouse ? '/listings/:slug' : '/items/:slug',
        sections: [
          { component: 'ContentDetail', props: {} }
        ]
      }
    ],
    contentTypes: [
      { code: isGame ? 'game' : isHouse ? 'property' : 'article', listPath: '/', detailPath: isGame ? '/games/:slug' : '/listings/:slug' }
    ]
  }

  return {
    text: `好的！我已根据你的需求生成了 **site_config** 配置：\n\n- **主题风格**：${config.theme.preset}\n- **主色调**：${config.theme.colorPrimary}\n- **页面数量**：${config.pages.length} 个路由\n\n配置已显示在右侧面板，你可以点击「应用到站点」将其保存。\n\n如需调整，直接告诉我，例如：「把首页改为3列网格」。`,
    config
  }
}

function copyConfig() {
  navigator.clipboard.writeText(formattedConfig.value).then(() => {
    ElMessage.success('已复制到剪贴板')
  })
}

function saveConfig() {
  if (!selectedSiteId.value) {
    ElMessage.warning('请先在顶部选择要应用的站点')
    return
  }
  ElMessageBox.confirm('确认将此配置应用到所选站点？', '确认', { type: 'info' }).then(() => {
    updateSite({ id: selectedSiteId.value, siteConfig: configJson.value }).then(() => {
      ElMessage.success('site_config 已保存')
    })
  })
}

onMounted(() => {
  listSite({ pageSize: 100 }).then(res => {
    siteList.value = res.rows || []
  })
})
</script>

<style scoped>
.builder-container { height: calc(100vh - 120px); }
.chat-card, .config-card { height: 100%; display: flex; flex-direction: column; }
.chat-card :deep(.el-card__body), .config-card :deep(.el-card__body) {
  flex: 1; overflow: hidden; display: flex; flex-direction: column; padding: 12px;
}
.card-header { display: flex; align-items: center; }
.ml8 { margin-left: 8px; }
.header-right { margin-left: auto; display: flex; gap: 8px; align-items: center; }

/* 对话区 */
.chat-body { flex: 1; overflow-y: auto; padding: 8px 4px; }
.guide-section { padding: 24px 8px; }
.guide-title { font-size: 15px; font-weight: 500; color: #606266; margin-bottom: 16px; }
.guide-cards { gap: 12px; }
.guide-card {
  border: 1px solid #e4e7ed; border-radius: 8px; padding: 16px 12px;
  cursor: pointer; transition: all .2s; display: flex; align-items: center; gap: 10px;
}
.guide-card:hover { border-color: #409eff; background: #f0f7ff; }
.guide-icon { font-size: 20px; }
.guide-label { font-size: 13px; color: #606266; }

.msg-row { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 16px; }
.msg-row.user { flex-direction: row-reverse; }
.msg-avatar { flex-shrink: 0; font-size: 12px; }
.msg-bubble { max-width: 78%; padding: 10px 14px; border-radius: 12px; }
.msg-bubble.assistant { background: #f2f6fc; border-radius: 0 12px 12px 12px; }
.msg-bubble.user { background: #ecf5ff; border-radius: 12px 0 12px 12px; }
.msg-content { font-size: 14px; line-height: 1.6; color: #303133; }
.msg-content :deep(.code-block) {
  background: #1e1e1e; color: #d4d4d4; padding: 12px; border-radius: 6px;
  font-size: 12px; overflow-x: auto; margin: 8px 0;
}
.msg-time { font-size: 11px; color: #c0c4cc; margin-top: 6px; }

.thinking-dots span {
  display: inline-block; width: 7px; height: 7px; border-radius: 50%;
  background: #409eff; margin: 0 2px; animation: blink 1.2s infinite;
}
.thinking-dots span:nth-child(2) { animation-delay: .2s; }
.thinking-dots span:nth-child(3) { animation-delay: .4s; }
@keyframes blink { 0%,80%,100%{opacity:.3} 40%{opacity:1} }

/* 输入区 */
.chat-input { border-top: 1px solid #ebeef5; padding-top: 12px; }
.input-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
.tip { font-size: 12px; color: #c0c4cc; }

/* 配置预览 */
.config-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 300px; color: #909399; }
.config-preview { flex: 1; overflow: hidden; }
.json-viewer { overflow: auto; max-height: 520px; }
.json-code { font-size: 12px; line-height: 1.6; color: #303133; background: #f8f9fa; padding: 12px; border-radius: 6px; margin: 0; }
.pages-tree { padding: 8px 0; }
.page-item { display: flex; align-items: center; gap: 8px; padding: 8px 12px; border-bottom: 1px solid #f0f0f0; font-size: 13px; }
.page-path { color: #409eff; font-family: monospace; }
.theme-preview { padding: 8px 0; }
.theme-row { display: flex; align-items: center; gap: 12px; padding: 10px 4px; border-bottom: 1px solid #f0f0f0; font-size: 14px; }
.color-box { width: 24px; height: 24px; border-radius: 4px; border: 1px solid #ddd; }
</style>
