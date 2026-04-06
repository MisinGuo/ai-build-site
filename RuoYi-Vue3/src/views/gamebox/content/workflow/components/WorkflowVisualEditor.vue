<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="isEdit ? '编辑工作流' : '新建工作流'"
    width="95%"
    top="3vh"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div class="workflow-visual-editor">
      <!-- 工具面板 -->
    <div class="tools-panel">
      <div class="panel-header">
        <span class="title">可用节点</span>
        <el-text type="info" size="small">按右侧所属网站过滤：{{ currentSiteName }}</el-text>
        <el-input
          v-model="toolSearch"
          placeholder="搜索工具/工作流..."
          size="small"
          clearable
          class="search-input"
          :disabled="!canSearchTools"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="tools-list">
        <div v-if="!canSearchTools" class="empty-hint" style="padding: 12px;">
          <el-text type="info" size="small">请先选择所属网站，再搜索并选择工具</el-text>
        </div>
        <div
          v-for="tool in filteredTools" 
          :key="tool.toolCode"
          class="tool-item"
          draggable="true"
          @dragstart="handleDragStart(tool, 'tool', $event)"
        >
          <div class="tool-icon">{{ tool.icon || '🔧' }}</div>
          <div class="tool-info">
            <div class="tool-name">{{ tool.toolName }}</div>
            <div class="tool-desc">{{ tool.description }}</div>
            <div class="tool-meta">
              <el-tag size="small" type="info">{{ tool.toolType }}</el-tag>
              <span class="tool-io">
                输入: {{ (tool.inputs || []).length }} | 
                输出: {{ (tool.outputs || []).length }}
              </span>
            </div>
          </div>
        </div>

        <el-divider content-position="left">可插入工作流</el-divider>
        <div
          v-for="wf in filteredWorkflows"
          :key="wf.workflowCode"
          class="tool-item workflow-item"
          draggable="true"
          @dragstart="handleDragStart(wf, 'workflow', $event)"
        >
          <div class="tool-icon">🔁</div>
          <div class="tool-info">
            <div class="tool-name">{{ wf.workflowName }}</div>
            <div class="tool-desc">编码：{{ wf.workflowCode }}</div>
            <div class="tool-meta">
              <el-tag size="small" type="success">workflow</el-tag>
              <span class="tool-io">
                输入: {{ (wf.inputs || []).length }} |
                输出: {{ (wf.outputs || []).length }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 画布区域 -->
    <div class="canvas-area">
      <div class="canvas-header">
        <el-button-group>
          <el-button size="small" @click="addStep">
            <el-icon><Plus /></el-icon> 添加步骤
          </el-button>
          <el-button size="small" @click="validateWorkflow">
            <el-icon><Check /></el-icon> 验证
          </el-button>
          <el-button size="small" @click="simulateWorkflow">
            <el-icon><VideoPlay /></el-icon> 模拟执行
          </el-button>
        </el-button-group>
        
        <el-switch
          v-model="showGrid"
          active-text="显示网格"
          size="small"
        />
      </div>

      <div 
        class="canvas"
        :class="{ 'show-grid': showGrid }"
        @drop="handleDrop"
        @dragover.prevent
      >
        <!-- 工作流输入 -->
        <div class="workflow-input-node" @click="selectedStepIndex = null">
          <div class="node-header">
            <el-icon><Download /></el-icon>
            <span>工作流输入</span>
            <el-badge 
              :value="workflowInputs.length" 
              :hidden="workflowInputs.length === 0"
              class="input-badge"
            />
          </div>
          <div class="node-content">
            <div v-if="workflowInputs.length === 0" class="empty-hint">
              <el-text type="info" size="small">还未配置输入参数</el-text>
            </div>
            <div v-for="input in workflowInputs" :key="input.name" class="param-item">
              <el-tag size="small">{{ input.label || input.name }}</el-tag>
              <span class="param-type">{{ input.type }}</span>
              <el-tag v-if="input.required" size="small" type="danger">必填</el-tag>
            </div>
            <el-button type="primary" plain size="small" @click.stop="editWorkflowInputs" style="width: 100%; margin-top: 8px;">
              <el-icon><{{ workflowInputs.length > 0 ? 'Edit' : 'Plus' }}</el-icon> 
              {{ workflowInputs.length > 0 ? '编辑参数' : '添加输入参数' }}
            </el-button>
          </div>
        </div>

        <!-- 步骤节点 -->
        <draggable 
          v-model="steps" 
          item-key="stepId"
          class="steps-container"
          handle=".step-drag-handle"
        >
          <template #item="{element: step, index}">
            <div 
              class="step-node"
              :class="{ 'selected': selectedStepIndex === index }"
              @click="selectStep(index)"
            >
              <!-- 连接线 -->
              <div class="step-connector"></div>

              <div class="step-card">
                <div class="step-header">
                  <span class="step-drag-handle">
                    <el-icon><Rank /></el-icon>
                  </span>
                  <span class="step-number">步骤 {{ index + 1 }}</span>
                  <span class="step-name">{{ step.stepName || '未命名步骤' }}</span>
                  <div class="step-actions">
                    <el-button text size="small" @click.stop="editStep(index)">
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button text size="small" type="danger" @click.stop="removeStep(index)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>

                <div class="step-content">
                  <div class="tool-info-compact">
                    <span class="tool-icon">{{ getStepMeta(step).icon }}</span>
                    <span class="tool-name">{{ getStepMeta(step).name }}</span>
                  </div>

                  <div v-if="step.description" class="step-desc">
                    {{ step.description }}
                  </div>

                  <!-- 输入映射预览 -->
                  <div v-if="step.inputMapping" class="mapping-preview">
                    <div class="mapping-title">输入映射:</div>
                    <div class="mapping-list">
                      <div v-for="(mapping, paramName) in step.inputMapping" :key="paramName" class="mapping-item">
                        <span class="param-name">{{ paramName }}</span>
                        <el-icon><Right /></el-icon>
                        <el-tag size="small" :type="getMappingSourceType(mapping.source)">
                          {{ formatMappingValue(mapping) }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </draggable>

        <!-- 工作流输出 -->
        <div v-if="steps.length > 0" class="workflow-output-node" @click="selectedStepIndex = null">
          <div class="node-header">
            <el-icon><Upload /></el-icon>
            <span>工作流输出</span>
            <el-badge 
              :value="workflowOutputs.length" 
              :hidden="workflowOutputs.length === 0"
              class="output-badge"
            />
          </div>
          <div class="node-content">
            <div v-if="workflowOutputs.length === 0" class="empty-hint">
              <el-text type="info" size="small">还未配置输出参数</el-text>
            </div>
            <div v-for="output in workflowOutputs" :key="output.name" class="param-item">
              <el-tag size="small" type="success">{{ output.name }}</el-tag>
              <span class="param-source">{{ output.source }}</span>
            </div>
            <el-button type="primary" plain size="small" @click.stop="editWorkflowOutputs" style="width: 100%; margin-top: 8px;">
              <el-icon><{{ workflowOutputs.length > 0 ? 'Edit' : 'Plus' }}</el-icon> 
              {{ workflowOutputs.length > 0 ? '编辑输出' : '配置输出参数' }}
            </el-button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="steps.length === 0" class="empty-canvas">
          <el-empty description="从左侧拖拽工具到此处开始创建工作流">
            <el-icon class="empty-icon"><Box /></el-icon>
          </el-empty>
        </div>
      </div>
    </div>

    <!-- 属性面板 -->
    <div class="properties-panel">
      <div class="panel-header">
        <span class="title">{{ selectedStepIndex !== null ? '步骤配置' : '工作流属性' }}</span>
      </div>

      <div v-if="selectedStepIndex === null" class="panel-content">
        <el-form label-width="80px" size="small">
          <el-form-item label="工作流名">
            <el-input v-model="workflowName" placeholder="请输入工作流名称" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="workflowDescription" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="所属网站">
            <SiteSelect
              v-model="workflowSiteId"
              :site-list="siteList"
              :show-default="true"
              :default-label="defaultSiteDisplayName"
              :disabled="isEdit"
            />
            <div v-if="isEdit" style="font-size: 12px; color: #909399; margin-top: 4px;">
              注意：工作流创建后不允许修改所属网站
            </div>
          </el-form-item>
          <el-form-item label="工作流分类">
            <el-select v-model="workflowCategoryId" placeholder="请选择分类" clearable>
              <el-option 
                v-for="category in categoryList" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="触发类型">
            <el-select v-model="workflowTriggerType" placeholder="请选择触发类型" style="width: 100%">
              <el-option label="手动触发" value="manual" />
              <el-option label="定时触发" value="scheduled" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="workflowTriggerType === 'scheduled'" label="Cron表达式">
            <el-input v-model="workflowScheduleExpression" placeholder="例: 0 0 8 * * ?" clearable />
            <div style="font-size: 11px; color: #909399; margin-top: 4px; line-height: 1.6;">
              格式：秒 分 时 日 月 周<br/>
              每天早表8点：<code>0 0 8 * * ?</code>　
              每小时：<code>0 0 * * * ?</code>　
              每5分钟：<code>0 */5 * * * ?</code>
            </div>
          </el-form-item>
          <el-form-item label="启用状态">
            <el-switch v-model="workflowEnabled" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
          </el-form-item>
        </el-form>
      </div>

      <div v-else class="panel-content">
        <StepConfigPanel
          v-if="steps[selectedStepIndex]"
          :step="steps[selectedStepIndex]"
          :tool-info="getStepToolInfo(steps[selectedStepIndex])"
          :available-sources="getAvailableDataSources(selectedStepIndex)"
          @update="updateStep"
        />
        <el-empty v-else description="步骤数据加载失败" />
      </div>
    </div>
    </div>

    <!-- 步骤编辑对话框 -->
    <el-dialog
      v-model="stepDialogVisible"
      :title="editingStepIndex === null ? '添加步骤' : '编辑步骤基本信息'"
      width="700px"
      append-to-body
    >
      <StepEditor
        v-if="stepDialogVisible"
        :step="editingStep"
        :available-tools="availableTools"
        :available-workflows="availableWorkflows"
        @save="saveStep"
        @cancel="stepDialogVisible = false"
      />
    </el-dialog>

    <!-- 工作流输入配置对话框 -->
    <el-dialog v-model="inputsDialogVisible" title="配置工作流输入" width="600px" append-to-body>
      <WorkflowInputsEditor
        v-if="inputsDialogVisible"
        v-model="workflowInputs"
        @confirm="inputsDialogVisible = false"
        @cancel="inputsDialogVisible = false"
      />
    </el-dialog>

    <!-- 工作流输出配置对话框 -->
    <el-dialog v-model="outputsDialogVisible" title="配置工作流输出" width="600px" append-to-body>
      <WorkflowOutputsEditor
        v-if="outputsDialogVisible"
        v-model="workflowOutputs"
        :available-sources="getAllOutputSources()"
        @confirm="outputsDialogVisible = false"
        @cancel="outputsDialogVisible = false"
      />
    </el-dialog>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable'
import { validateStepMapping, simulateWorkflow as apiSimulateWorkflow, addWorkflow, updateWorkflow, listWorkflow } from '@/api/gamebox/workflow'
import { listAtomicTool } from '@/api/gamebox/atomicTool'
import SiteSelect from '@/components/SiteSelect/index.vue'
import StepConfigPanel from './StepConfigPanel.vue'
import StepEditor from './StepEditor.vue'
import WorkflowInputsEditor from './WorkflowInputsEditor.vue'
import WorkflowOutputsEditor from './WorkflowOutputsEditor.vue'

const emit = defineEmits(['success'])

// 对话框控制
const dialogVisible = ref(false)
const isEdit = ref(false)
const workflowId = ref(null)
const saving = ref(false)

// 工具列表
const availableTools = ref([])
const availableWorkflows = ref([])
const toolSearch = ref('')
const toolsMap = ref({})
const workflowsMap = ref({})

// 工作流数据
const workflowCode = ref('')
const workflowName = ref('')
const workflowDescription = ref('')
const workflowCategory = ref('custom')
const workflowSiteId = ref(null)
const workflowCategoryId = ref(null)
const workflowTriggerType = ref('manual')
const workflowScheduleExpression = ref('')
const workflowEnabled = ref(1)
const workflowInputs = ref([])
const workflowOutputs = ref([])
const steps = ref([])

// 网站和分类列表
const siteList = ref([])
const categoryList = ref([])

// UI 状态
const showGrid = ref(true)
const selectedStepIndex = ref(null)
const stepDialogVisible = ref(false)
const editingStepIndex = ref(null)
const editingStep = ref({})
const inputsDialogVisible = ref(false)
const outputsDialogVisible = ref(false)

const canSearchTools = computed(() => workflowSiteId.value !== null && workflowSiteId.value !== undefined)
const defaultSiteDisplayName = computed(() => {
  const personalSite = siteList.value.find(site => site.isPersonal === 1)
  return personalSite?.name || '默认站点'
})
const currentSiteName = computed(() => {
  const current = siteList.value.find(site => site.id === workflowSiteId.value)
  return current ? current.name : '未选择'
})

// 过滤后的工具列表
const filteredTools = computed(() => {
  if (!canSearchTools.value) return []
  if (!toolSearch.value) return availableTools.value
  const search = toolSearch.value.toLowerCase()
  return availableTools.value.filter(tool => 
    tool.toolName.toLowerCase().includes(search) ||
    tool.toolCode.toLowerCase().includes(search) ||
    (tool.description && tool.description.toLowerCase().includes(search))
  )
})

const filteredWorkflows = computed(() => {
  if (!canSearchTools.value) return []
  const editableWorkflows = availableWorkflows.value.filter(wf => wf.workflowCode !== workflowCode.value)
  if (!toolSearch.value) return editableWorkflows
  const search = toolSearch.value.toLowerCase()
  return editableWorkflows.filter(wf =>
    wf.workflowName.toLowerCase().includes(search) ||
    wf.workflowCode.toLowerCase().includes(search)
  )
})

// 初始化
onMounted(() => {
  loadSiteList()
  loadCategoryList()
})

watch(workflowSiteId, async (newSiteId, oldSiteId) => {
  if (newSiteId === oldSiteId) return
  toolSearch.value = ''
  await loadAvailableTools(newSiteId)
  await loadAvailableWorkflows(newSiteId)
})

// 加载可用工具
async function loadAvailableTools(siteId = workflowSiteId.value) {
  if (siteId === null || siteId === undefined) {
    availableTools.value = []
    toolsMap.value = {}
    return
  }
  try {
    const response = await listAtomicTool({
      pageNum: 1,
      pageSize: 9999,
      siteId,
      queryMode: 'related',
      enabled: 1
    })

    const rows = response.rows || []
    const visibleRows = rows.filter(tool => {
      const relationSource = tool.relationSource
      if (relationSource === 'default' || relationSource === 'shared') {
        return String(tool.isVisible ?? '1') === '1'
      }
      if (relationSource === 'own') {
        return String(tool.enabled ?? 1) === '1'
      }
      // 兜底：优先按 isVisible 判断，再按 enabled 判断
      if (tool.isVisible !== undefined && tool.isVisible !== null) {
        return String(tool.isVisible) === '1'
      }
      return String(tool.enabled ?? 1) === '1'
    })

    availableTools.value = visibleRows.map(tool => {
      let inputs = tool.inputs
      let outputs = tool.outputs
      try {
        if (typeof inputs === 'string') inputs = JSON.parse(inputs || '[]')
      } catch {
        inputs = []
      }
      try {
        if (typeof outputs === 'string') outputs = JSON.parse(outputs || '[]')
      } catch {
        outputs = []
      }
      return {
        ...tool,
        inputs: Array.isArray(inputs) ? inputs : [],
        outputs: Array.isArray(outputs) ? outputs : []
      }
    })
    
    // 构建工具映射表
    toolsMap.value = {}
    availableTools.value.forEach(tool => {
      toolsMap.value[tool.toolCode] = tool
    })
  } catch (error) {
    ElMessage.error('加载工具列表失败')
  }
}

async function loadAvailableWorkflows(siteId = workflowSiteId.value) {
  if (siteId === null || siteId === undefined) {
    availableWorkflows.value = []
    workflowsMap.value = {}
    return
  }

  try {
    const response = await listWorkflow({
      pageNum: 1,
      pageSize: 9999,
      siteId,
      queryMode: 'related',
      enabled: 1
    })

    const rows = (response.rows || []).filter(wf => {
      if (!wf.workflowCode || !wf.workflowName) return false
      if (wf.relationSource === 'default' || wf.relationSource === 'shared') {
        return String(wf.isVisible ?? '1') === '1'
      }
      if (wf.relationSource === 'own') {
        return String(wf.enabled ?? 1) === '1'
      }
      return true
    })

    availableWorkflows.value = rows.map(wf => {
      let inputs = []
      let outputs = []
      try {
        const def = typeof wf.definition === 'string' ? JSON.parse(wf.definition || '{}') : (wf.definition || {})
        inputs = Array.isArray(def.inputs) ? def.inputs : []
        outputs = Array.isArray(def.outputs) ? def.outputs : []
      } catch {
        inputs = []
        outputs = []
      }
      return {
        workflowCode: wf.workflowCode,
        workflowName: wf.workflowName,
        inputs,
        outputs
      }
    })

    workflowsMap.value = {}
    availableWorkflows.value.forEach(wf => {
      workflowsMap.value[wf.workflowCode] = wf
    })
  } catch (error) {
    ElMessage.error('加载可插入工作流失败')
  }
}

// 加载网站列表
async function loadSiteList() {
  try {
    const { listSite } = await import('@/api/gamebox/site')
    const response = await listSite()
    siteList.value = response.rows || []
  } catch (error) {
    console.error('加载网站列表失败:', error)
  }
}

// 加载分类列表
async function loadCategoryList() {
  try {
    const { listCategory } = await import('@/api/gamebox/category')
    const response = await listCategory({ categoryType: 'workflow' })
    categoryList.value = response.rows || []
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

// 打开对话框
function open(data = null, options = {}) {
  dialogVisible.value = true
  resetForm()
  
  if (data) {
    // 编辑模式
    console.log('编辑模式，接收的数据:', data)
    isEdit.value = true
    workflowId.value = data.id
    workflowCode.value = data.workflowCode || ''
    workflowName.value = data.workflowName || ''
    workflowDescription.value = data.description || ''
    workflowCategory.value = data.category || 'custom'
    workflowSiteId.value = data.siteId !== undefined ? data.siteId : null
    workflowCategoryId.value = data.categoryId || null
    workflowTriggerType.value = data.triggerType || 'manual'
    workflowScheduleExpression.value = data.scheduleExpression || ''
    workflowEnabled.value = data.enabled !== undefined ? data.enabled : 1

    if (data.definition) {
      try {
        const definition = typeof data.definition === 'string' 
          ? JSON.parse(data.definition) 
          : data.definition

        console.log('解析后的definition:', definition)
        // 兼容 inputs/inputParams 和 outputs/outputParams
        workflowInputs.value = definition.inputs || definition.inputParams || []
        workflowOutputs.value = definition.outputs || definition.outputParams || []
        
        // 规范化 steps 数据，确保 inputMapping 格式正确
        const loadedSteps = definition.steps || []
        steps.value = loadedSteps.map(step => {
          console.log('规范化步骤:', step.stepName, '原始inputMapping:', step.inputMapping)
          const normalizedStep = {
            ...step,
            stepType: step.stepType || (step.workflowCode ? 'workflow' : 'tool'),
            workflowCode: step.workflowCode || ''
          }
          
          // 规范化 inputMapping
          if (step.inputMapping) {
            const normalizedMapping = {}
            Object.keys(step.inputMapping).forEach(paramName => {
              const mapping = step.inputMapping[paramName]
              
              console.log(`  参数 ${paramName}:`, mapping, typeof mapping)
              
              // 如果 mapping 不是对象（是直接值），转换为对象格式
              if (typeof mapping !== 'object' || mapping === null) {
                normalizedMapping[paramName] = {
                  source: 'constant',
                  value: mapping
                }
                console.log(`    -> 转换为常量:`, normalizedMapping[paramName])
              } else if (!mapping.source) {
                // 如果对象没有 source 字段，设置默认值
                normalizedMapping[paramName] = {
                  source: 'constant',
                  value: mapping.value || ''
                }
                console.log(`    -> 补充source:`, normalizedMapping[paramName])
              } else if (mapping.field && !mapping.value) {
                // 后端格式转前端格式: {source: 'step_1', field: 'titles'} -> {source: 'step_1', value: 'step_1.output.titles'}
                if (mapping.source === 'workflow') {
                  normalizedMapping[paramName] = {
                    source: 'input',
                    value: `input.${mapping.field}`
                  }
                } else if (mapping.source.startsWith('step')) {
                  normalizedMapping[paramName] = {
                    source: mapping.source,
                    value: `${mapping.source}.output.${mapping.field}`
                  }
                } else {
                  normalizedMapping[paramName] = mapping
                }
                console.log(`    -> 后端格式转前端:`, normalizedMapping[paramName])
              } else {
                // 格式正确，直接使用
                normalizedMapping[paramName] = mapping
                console.log(`    -> 格式正确:`, normalizedMapping[paramName])
              }
            })
            normalizedStep.inputMapping = normalizedMapping
          } else {
            normalizedStep.inputMapping = {}
          }
          
          console.log('规范化后的inputMapping:', normalizedStep.inputMapping)
          return normalizedStep
        })
        
        console.log('加载的inputs:', workflowInputs.value)
        console.log('加载并规范化后的steps:', steps.value)
      } catch (e) {
        console.error('解析工作流定义失败', e)
      }
    }
  } else {
    // 新增模式
    isEdit.value = false
    workflowId.value = null
    if (options.defaultSiteId !== undefined && options.defaultSiteId !== null) {
      workflowSiteId.value = options.defaultSiteId
    }
  }

  loadAvailableTools(workflowSiteId.value)
  loadAvailableWorkflows(workflowSiteId.value)
}

// 重置表单
function resetForm() {
  workflowCode.value = ''
  workflowName.value = ''
  workflowDescription.value = ''
  workflowCategory.value = 'custom'
  workflowSiteId.value = null
  workflowCategoryId.value = null
  workflowTriggerType.value = 'manual'
  workflowScheduleExpression.value = ''
  workflowEnabled.value = 1
  workflowInputs.value = []
  workflowOutputs.value = []
  steps.value = []
  selectedStepIndex.value = null
}

// 取消
function handleCancel() {
  dialogVisible.value = false
}

// 保存
async function handleSave() {
  // 验证
  if (!workflowName.value) {
    ElMessage.warning('请输入工作流名称')
    return
  }

  // 定时触发时校验 cron 表达式
  if (workflowTriggerType.value === 'scheduled') {
    const cron = (workflowScheduleExpression.value || '').trim()
    if (!cron) {
      ElMessage.warning('请输入 Cron 表达式')
      return
    }
    const parts = cron.split(/\s+/)
    if (parts.length < 6 || parts.length > 7) {
      ElMessage.error(`Cron 表达式必须为 6 个字段（秒 分 时 日 月 周），当前为 ${parts.length} 个字段\n示例：0 0 8 * * ?`)
      return
    }
  }
  
  if (steps.value.length === 0) {
    ElMessage.warning('请至少添加一个步骤')
    return
  }

  const unavailableTools = steps.value
    .filter(step => (step.stepType || 'tool') === 'tool')
    .map(step => step.toolCode)
    .filter(toolCode => toolCode && !toolsMap.value[toolCode])
  if (unavailableTools.length > 0) {
    ElMessage.error(`当前网站下存在不可用工具: ${[...new Set(unavailableTools)].join(', ')}`)
    return
  }

  const unavailableWorkflows = steps.value
    .filter(step => (step.stepType || 'tool') === 'workflow')
    .map(step => step.workflowCode)
    .filter(code => code && !workflowsMap.value[code])
  if (unavailableWorkflows.length > 0) {
    ElMessage.error(`当前网站下存在不可用工作流: ${[...new Set(unavailableWorkflows)].join(', ')}`)
    return
  }

  // 转换步骤数据格式以匹配后端期望
  const convertedSteps = steps.value.map(step => {
    const convertedMapping = {}
    
    // 获取工具定义以便进行类型转换
    const stepInputs = getStepInputDefinitions(step)
    const inputTypes = {}
    stepInputs.forEach(input => {
      inputTypes[input.name] = input.type
    })
    
    if (step.inputMapping) {
      Object.keys(step.inputMapping).forEach(paramName => {
        const mapping = step.inputMapping[paramName]
        
        if (mapping.source === 'constant') {
          // 常量值：根据参数类型进行转换
          let value = mapping.value
          const paramType = inputTypes[paramName]
          
          if (paramType === 'number') {
            // 转换为数字
            value = value === '' || value === null ? null : Number(value)
          } else if (paramType === 'boolean') {
            // 转换为布尔值
            value = value === 'true' || value === true
          }
          // text 类型保持字符串
          
          convertedMapping[paramName] = value
        } else if (mapping.source === 'input') {
          // 工作流输入：转换为 {source: "workflow", field: "fieldName"}
          const fieldName = mapping.value.replace('input.', '')
          convertedMapping[paramName] = {
            source: 'workflow',
            field: fieldName
          }
        } else if (mapping.source && mapping.source.startsWith('step_')) {
          // 步骤输出：转换为 {source: "stepX", field: "fieldName"}
          // mapping.value 格式: "step_1.output.titles"
          const parts = mapping.value.split('.')
          if (parts.length >= 3) {
            convertedMapping[paramName] = {
              source: parts[0],  // step_1
              field: parts[2]    // titles
            }
          }
        }
      })
    }
    
    return {
      ...step,
      stepType: step.stepType || 'tool',
      inputMapping: convertedMapping
    }
  })

  const definition = {
    inputs: workflowInputs.value,
    steps: convertedSteps,
    outputs: workflowOutputs.value
  }

  const workflowData = {
    workflowName: workflowName.value,
    description: workflowDescription.value,
    triggerType: workflowTriggerType.value,
    scheduleExpression: workflowTriggerType.value === 'scheduled' ? workflowScheduleExpression.value : null,
    definition: JSON.stringify(definition),
    enabled: workflowEnabled.value,
    siteId: workflowSiteId.value,
    categoryId: workflowCategoryId.value
  }

  if (isEdit.value) {
    workflowData.id = workflowId.value
    workflowData.workflowCode = workflowCode.value
  } else {
    // 新增时自动生成工作流编码
    workflowData.workflowCode = 'wf_' + Date.now()
  }

  try {
    saving.value = true
    if (isEdit.value) {
      await updateWorkflow(workflowData)
      ElMessage.success('更新成功')
    } else {
      await addWorkflow(workflowData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    emit('success')
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// 暴露方法给父组件
defineExpose({
  open
})

// 获取工具信息
function getToolInfo(toolCode) {
  const tool = toolsMap.value[toolCode]
  console.log('getToolInfo 被调用:', toolCode, '返回:', tool)
  return tool
}

function getWorkflowInfo(targetWorkflowCode) {
  return workflowsMap.value[targetWorkflowCode]
}

function getStepMeta(step) {
  const stepType = step.stepType || 'tool'
  if (stepType === 'workflow') {
    const wf = getWorkflowInfo(step.workflowCode)
    return {
      icon: '🔁',
      name: wf?.workflowName || step.workflowCode || '未选择工作流'
    }
  }
  const tool = getToolInfo(step.toolCode)
  return {
    icon: tool?.icon || '🔧',
    name: tool?.toolName || step.toolCode || '未选择工具'
  }
}

function getStepInputDefinitions(step) {
  const stepType = step.stepType || 'tool'
  if (stepType === 'workflow') {
    return getWorkflowInfo(step.workflowCode)?.inputs || []
  }
  return getToolInfo(step.toolCode)?.inputs || []
}

function getStepOutputDefinitions(step) {
  const stepType = step.stepType || 'tool'
  if (stepType === 'workflow') {
    const outputs = getWorkflowInfo(step.workflowCode)?.outputs || []
    return outputs.map(item => ({
      name: item.name,
      label: item.label || item.name,
      type: item.type || 'text'
    }))
  }
  return getToolInfo(step.toolCode)?.outputs || []
}

function getStepToolInfo(step) {
  const stepType = step.stepType || 'tool'
  if (stepType === 'workflow') {
    const wf = getWorkflowInfo(step.workflowCode)
    return {
      icon: '🔁',
      toolType: 'workflow',
      description: wf ? `子工作流：${wf.workflowCode}` : '子工作流',
      inputs: wf?.inputs || [],
      outputs: (wf?.outputs || []).map(item => ({
        name: item.name,
        label: item.label || item.name,
        type: item.type || 'text'
      }))
    }
  }
  return getToolInfo(step.toolCode)
}

// 拖拽开始
function handleDragStart(item, type, event) {
  event.dataTransfer.effectAllowed = 'copy'
  event.dataTransfer.setData('node', JSON.stringify({ type, item }))
}

// 拖放处理
function handleDrop(event) {
  event.preventDefault()
  
  try {
    const payload = JSON.parse(event.dataTransfer.getData('node'))
    if (payload.type === 'workflow') {
      addStepWithWorkflow(payload.item)
      return
    }
    addStepWithTool(payload.item)
  } catch (e) {
    console.error('拖放处理失败', e)
  }
}

// 添加步骤
function addStep() {
  editingStepIndex.value = null
  editingStep.value = {
    stepId: `step_${Date.now()}`,
    stepType: 'tool',
    stepName: '',
    toolCode: '',
    workflowCode: '',
    description: '',
    inputMapping: {},
    enabled: true,
    continueOnError: false
  }
  stepDialogVisible.value = true
}

// 使用工具添加步骤
function addStepWithTool(tool) {
  const newStep = {
    stepId: `step_${Date.now()}`,
    stepType: 'tool',
    stepName: tool.toolName,
    toolCode: tool.toolCode,
    workflowCode: '',
    description: tool.description,
    inputMapping: {},
    enabled: true,
    continueOnError: false
  }

  // 自动初始化输入映射
  if (tool.inputs && tool.inputs.length > 0) {
    tool.inputs.forEach(input => {
      newStep.inputMapping[input.name] = {
        source: 'constant',
        value: input.default || ''
      }
    })
  }

  steps.value.push(newStep)
  selectStep(steps.value.length - 1)
}

function addStepWithWorkflow(wf) {
  const newStep = {
    stepId: `step_${Date.now()}`,
    stepType: 'workflow',
    stepName: `执行${wf.workflowName}`,
    toolCode: '',
    workflowCode: wf.workflowCode,
    description: `执行子工作流：${wf.workflowCode}`,
    inputMapping: {},
    enabled: true,
    continueOnError: false
  }

  if (wf.inputs && wf.inputs.length > 0) {
    wf.inputs.forEach(input => {
      newStep.inputMapping[input.name] = {
        source: 'constant',
        value: input.default || ''
      }
    })
  }

  steps.value.push(newStep)
  selectStep(steps.value.length - 1)
}

// 编辑步骤
function editStep(index) {
  editingStepIndex.value = index
  editingStep.value = JSON.parse(JSON.stringify(steps.value[index]))
  stepDialogVisible.value = true
}

// 保存步骤
function saveStep(stepData) {
  if (editingStepIndex.value === null) {
    steps.value.push(stepData)
  } else {
    steps.value[editingStepIndex.value] = stepData
  }
  stepDialogVisible.value = false
}

// 删除步骤
function removeStep(index) {
  ElMessageBox.confirm('确定要删除这个步骤吗？', '提示', {
    type: 'warning'
  }).then(() => {
    steps.value.splice(index, 1)
    if (selectedStepIndex.value === index) {
      selectedStepIndex.value = null
    }
  }).catch(() => {})
}

// 选择步骤
function selectStep(index) {
  selectedStepIndex.value = index
}

// 更新步骤
function updateStep(stepData) {
  if (selectedStepIndex.value !== null) {
    steps.value[selectedStepIndex.value] = { ...steps.value[selectedStepIndex.value], ...stepData }
  }
}

// 编辑工作流输入
function editWorkflowInputs() {
  console.log('打开工作流输入编辑器，当前输入:', workflowInputs.value)
  inputsDialogVisible.value = true
}

// 编辑工作流输出
function editWorkflowOutputs() {
  outputsDialogVisible.value = true
}

// 获取可用的数据源（用于参数映射）
function getAvailableDataSources(stepIndex) {
  console.log('获取数据源，stepIndex:', stepIndex, '工作流输入:', workflowInputs.value)
  
  const sources = [
    { type: 'constant', label: '常量值', value: 'constant' }
  ]

  // 添加工作流输入
  if (workflowInputs.value && workflowInputs.value.length > 0) {
    workflowInputs.value.forEach(input => {
      sources.push({
        type: 'input',
        label: `输入: ${input.label || input.name}`,
        value: `input.${input.name}`,
        dataType: input.type
      })
    })
  }

  // 添加前置步骤的输出
  if (stepIndex !== null && stepIndex > 0) {
    for (let i = 0; i < stepIndex; i++) {
      const step = steps.value[i]
      const outputs = getStepOutputDefinitions(step)
      
      console.log(`处理步骤${i}(${step.stepName}), 步骤信息:`, step)
      
      if (outputs && outputs.length > 0) {
        console.log(`步骤${i}的输出参数:`, outputs)
        outputs.forEach(output => {
          sources.push({
            type: `step_${i + 1}`,
            label: `步骤${i + 1}(${step.stepName}): ${output.label || output.name}`,
            value: `step_${i + 1}.output.${output.name}`,
            dataType: output.type
          })
        })
      } else {
        console.warn(`步骤${i}没有输出参数或工具信息为空`)
      }
    }
  }

  console.log('返回的数据源:', sources)
  return sources
}

// 获取所有输出源（用于配置工作流输出）
function getAllOutputSources() {
  const sources = []

  steps.value.forEach((step, index) => {
    const outputs = getStepOutputDefinitions(step)
    if (outputs && outputs.length > 0) {
      outputs.forEach(output => {
        sources.push({
          stepIndex: index,
          stepName: step.stepName,
          outputName: output.name,
          outputLabel: output.label || output.name,
          value: `step_${index + 1}.output.${output.name}`
        })
      })
    }
  })

  return sources
}

// 获取映射源类型
function getMappingSourceType(source) {
  if (!source) return ''
  if (source === 'constant') return 'info'
  if (source === 'input') return 'primary'
  if (source.startsWith('step_')) return 'success'
  return ''
}

// 格式化映射值显示
function formatMappingValue(mapping) {
  if (mapping.source === 'constant') {
    return mapping.value || '(空)'
  }
  return mapping.value
}

// 验证工作流
async function validateWorkflow() {
  const errors = []

  // 基本验证
  if (!workflowName.value) {
    errors.push('工作流名称不能为空')
  }

  if (steps.value.length === 0) {
    errors.push('至少需要添加一个步骤')
  }

  // 验证每个步骤
  for (let i = 0; i < steps.value.length; i++) {
    const step = steps.value[i]
    const stepType = step.stepType || 'tool'
    
    if (stepType === 'workflow') {
      if (!step.workflowCode) {
        errors.push(`步骤${i + 1}: 未选择子工作流`)
        continue
      }
      const wf = getWorkflowInfo(step.workflowCode)
      if (!wf) {
        errors.push(`步骤${i + 1}: 子工作流不存在或不可见 (${step.workflowCode})`)
      }
      continue
    }

    if (!step.toolCode) {
      errors.push(`步骤${i + 1}: 未选择工具`)
      continue
    }

    const tool = getToolInfo(step.toolCode)
    if (!tool) {
      errors.push(`步骤${i + 1}: 工具不存在 (${step.toolCode})`)
      continue
    }

    try {
      const result = await validateStepMapping({ toolCode: step.toolCode, inputMapping: step.inputMapping })
      if (result.data && !result.data.valid) {
        errors.push(`步骤${i + 1}: ${result.data.errors.join(', ')}`)
      }
    } catch (e) {
      errors.push(`步骤${i + 1}: 验证失败`)
    }
  }

  if (errors.length > 0) {
    ElMessageBox.alert(errors.join('<br>'), '验证失败', {
      dangerouslyUseHTMLString: true,
      type: 'error'
    })
  } else {
    ElMessage.success('工作流验证通过')
  }
}

// 模拟执行
async function simulateWorkflow() {
  if (steps.value.length === 0) {
    ElMessage.warning('请先添加步骤')
    return
  }

  // 准备输入数据
  const inputData = {}
  workflowInputs.value.forEach(input => {
    inputData[input.name] = input.default || ''
  })

  try {
    const workflow = {
      workflowName: workflowName.value,
      workflowCode: 'temp_' + Date.now(),
      siteId: workflowSiteId.value,
      definition: JSON.stringify({
        inputs: workflowInputs.value,
        steps: steps.value,
        outputs: workflowOutputs.value
      })
    }

    const response = await apiSimulateWorkflow({ workflow, inputData })
    
    if (response.data.status === 'success') {
      ElMessage.success('模拟执行成功')
      // TODO: 显示执行结果
      console.log('执行结果:', response.data)
    } else {
      ElMessage.error('模拟执行失败: ' + response.data.message)
    }
  } catch (error) {
    ElMessage.error('模拟执行出错: ' + (error.message || '未知错误'))
  }
}
</script>

<style scoped lang="scss">
.workflow-visual-editor {
  display: flex;
  height: 75vh;
  min-height: 600px;
  background: #f5f7fa;
  
  .tools-panel {
    width: 280px;
    background: white;
    border-right: 1px solid #e4e7ed;
    display: flex;
    flex-direction: column;
    
    .panel-header {
      padding: 16px;
      border-bottom: 1px solid #e4e7ed;
      
      .title {
        font-weight: 600;
        font-size: 14px;
        display: block;
        margin-bottom: 12px;
      }
      
      .search-input {
        width: 100%;
      }
    }
    
    .tools-list {
      flex: 1;
      overflow-y: auto;
      padding: 8px;
      
      .tool-item {
        display: flex;
        padding: 12px;
        margin-bottom: 8px;
        background: #f9fafc;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        cursor: move;
        transition: all 0.3s;
        
        &:hover {
          background: #ecf5ff;
          border-color: #409eff;
          transform: translateX(2px);
        }
        
        .tool-icon {
          font-size: 24px;
          margin-right: 12px;
        }
        
        .tool-info {
          flex: 1;
          min-width: 0;
          
          .tool-name {
            font-weight: 500;
            font-size: 14px;
            margin-bottom: 4px;
          }
          
          .tool-desc {
            font-size: 12px;
            color: #909399;
            margin-bottom: 6px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
          
          .tool-meta {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 11px;
            
            .tool-io {
              color: #909399;
            }
          }
        }
      }
    }
  }
  
  .canvas-area {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    .canvas-header {
      padding: 12px 16px;
      background: white;
      border-bottom: 1px solid #e4e7ed;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .canvas {
      flex: 1;
      overflow-y: auto;
      padding: 24px;
      
      &.show-grid {
        background-image: 
          linear-gradient(rgba(0, 0, 0, 0.05) 1px, transparent 1px),
          linear-gradient(90deg, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
        background-size: 20px 20px;
      }
      
      .workflow-input-node,
      .workflow-output-node {
        max-width: 500px;
        margin: 0 auto 32px;
        background: white;
        border: 2px solid #67c23a;
        border-radius: 8px;
        padding: 16px;
        box-shadow: 0 2px 12px rgba(103, 194, 58, 0.15);
        transition: all 0.3s;
        
        &:hover {
          box-shadow: 0 4px 20px rgba(103, 194, 58, 0.25);
          transform: translateY(-2px);
        }
        
        .node-header {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 600;
          margin-bottom: 12px;
          color: #67c23a;
          position: relative;
          
          .input-badge,
          .output-badge {
            margin-left: auto;
          }
        }
        
        .node-content {
          .empty-hint {
            text-align: center;
            padding: 16px;
            background: #f5f7fa;
            border-radius: 4px;
            margin-bottom: 8px;
          }
          
          .param-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            margin-bottom: 8px;
            background: #f0f9ff;
            border-radius: 4px;
            
            .param-type,
            .param-source {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }
      
      .workflow-output-node {
        border-color: #409eff;
        box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
        
        &:hover {
          box-shadow: 0 4px 20px rgba(64, 158, 255, 0.25);
        }
        
        .node-header {
          color: #409eff;
        }
        
        .node-content .param-item {
          background: #ecf5ff;
        }
      }
      
      .steps-container {
        max-width: 600px;
        margin: 0 auto;
        
        .step-node {
          position: relative;
          margin-bottom: 24px;
          
          .step-connector {
            position: absolute;
            left: 50%;
            top: -24px;
            width: 2px;
            height: 24px;
            background: linear-gradient(to bottom, #e4e7ed, #409eff);
            transform: translateX(-1px);
          }
          
          .step-card {
            background: white;
            border: 2px solid #e4e7ed;
            border-radius: 8px;
            transition: all 0.3s;
            
            &:hover {
              border-color: #409eff;
              box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
            }
            
            .step-header {
              display: flex;
              align-items: center;
              gap: 8px;
              padding: 12px 16px;
              background: #f5f7fa;
              border-bottom: 1px solid #e4e7ed;
              border-radius: 6px 6px 0 0;
              
              .step-drag-handle {
                cursor: move;
                color: #909399;
              }
              
              .step-number {
                font-weight: 600;
                color: #409eff;
              }
              
              .step-name {
                flex: 1;
                font-weight: 500;
              }
              
              .step-actions {
                display: flex;
                gap: 4px;
              }
            }
            
            .step-content {
              padding: 16px;
              
              .tool-info-compact {
                display: flex;
                align-items: center;
                gap: 8px;
                margin-bottom: 12px;
                
                .tool-icon {
                  font-size: 20px;
                }
                
                .tool-name {
                  font-weight: 500;
                  color: #303133;
                }
              }
              
              .step-desc {
                font-size: 13px;
                color: #606266;
                margin-bottom: 12px;
              }
              
              .mapping-preview {
                .mapping-title {
                  font-size: 12px;
                  font-weight: 600;
                  color: #909399;
                  margin-bottom: 8px;
                }
                
                .mapping-list {
                  .mapping-item {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    padding: 6px;
                    margin-bottom: 6px;
                    background: #f9fafc;
                    border-radius: 4px;
                    font-size: 12px;
                    
                    .param-name {
                      font-weight: 500;
                      color: #303133;
                    }
                  }
                }
              }
            }
          }
          
          &.selected .step-card {
            border-color: #409eff;
            box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
          }
        }
      }
      
      .empty-canvas {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .empty-icon {
          font-size: 64px;
          color: #dcdfe6;
          margin-bottom: 16px;
        }
      }
    }
  }
  
  .properties-panel {
    width: 320px;
    background: white;
    border-left: 1px solid #e4e7ed;
    display: flex;
    flex-direction: column;
    
    .panel-header {
      padding: 16px;
      border-bottom: 1px solid #e4e7ed;
      
      .title {
        font-weight: 600;
        font-size: 14px;
      }
    }
    
    .panel-content {
      flex: 1;
      overflow-y: auto;
      padding: 16px;
    }
  }
}
</style>
