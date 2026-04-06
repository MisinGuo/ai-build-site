<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="查看模式" prop="viewMode">
        <el-radio-group v-model="viewMode" @change="handleViewModeChange">
          <el-radio-button value="creator">创建者</el-radio-button>
          <el-radio-button value="related">关联网站</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="viewMode === 'creator' ? '创建者网站' : '关联网站'" prop="siteId">
        <SiteSelect v-model="queryParams.siteId" :site-list="siteList" :show-default="viewMode === 'creator'" @change="handleSiteChange" />
      </el-form-item>
      <el-form-item v-if="viewMode === 'creator' && queryParams.siteId && !isPersonalSiteCheck(queryParams.siteId)" label=" ">
        <el-checkbox v-model="includeDefaultConfig" @change="handleQuery">含默认配置</el-checkbox>
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 200px" filterable>
          <el-option
            v-for="cat in workflowCategoryList"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          >
            <span v-if="cat.icon" style="margin-right: 4px">{{ cat.icon }}</span>
            <span :style="cat.isVisible === '0' ? 'color: #ccc;' : ''">{{ cat.name }}</span>
            <span v-if="!cat.siteId || isPersonalSiteCheck(cat.siteId)" style="margin-left: 4px; color: #67c23a; font-size: 12px;">[默认配置]</span>
            <span v-else style="margin-left: 4px; color: #909399; font-size: 12px;">[{{ getSiteName(cat.siteId) }}]</span>
            <span v-if="cat.isVisible === '0'" style="margin-left: 4px; color: #f56c6c; font-size: 12px;">[不可用]</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="工作流编码" prop="workflowCode">
        <el-input
          v-model="queryParams.workflowCode"
          placeholder="请输入工作流编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工作流名称" prop="workflowName">
        <el-input
          v-model="queryParams.workflowName"
          placeholder="请输入工作流名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="触发类型" prop="triggerType">
        <el-select v-model="queryParams.triggerType" placeholder="请选择触发类型" clearable style="width: 200px">
          <el-option label="手动触发" value="manual" />
          <el-option label="定时触发" value="scheduled" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable style="width: 200px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row v-if="viewMode === 'creator'" :gutter="10" style="margin-top: 8px;">
      <el-col :span="24">
        <el-form-item label=" ">
          <el-button type="success" plain icon="Download" @click="handleFullExport">全站导出</el-button>
          <el-button type="warning" plain icon="Upload" @click="handleFullImport">全站导入</el-button>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['content:workflow:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="CopyDocument"
          @click="handleOpenTemplates"
          v-hasPermi="['content:workflow:add']"
        >从模板创建</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-if="isPersonalSiteCheck(queryParams.siteId)"
          type="danger"
          plain
          icon="CircleClose"
          :disabled="multiple"
          @click="handleBatchExclude"
          v-hasPermi="['content:workflow:edit']"
        >批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-if="viewMode === 'creator'"
          type="primary"
          plain
          icon="Connection"
          :disabled="multiple"
          @click="handleBatchRelation"
          v-hasPermi="['content:workflow:edit']"
        >批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Download"
          :disabled="multiple"
          @click="handleExport"
          v-hasPermi="['content:workflow:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Upload"
          @click="handleSystemImport"
          v-hasPermi="['content:workflow:add']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['content:workflow:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 工作流列表 -->
    <el-table ref="workflowTableRef" v-loading="loading" :data="workflowList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="工作流编码" align="center" prop="workflowCode" width="180" />
      <el-table-column label="工作流名称" align="center" prop="workflowName" width="200" show-overflow-tooltip />
      <el-table-column label="创建者网站" align="center" prop="siteId" width="150">
        <template #default="scope">
          <el-tag type="success">{{ getSiteName(scope.row.siteId) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="relationSource" width="100" v-if="viewMode === 'related'">
        <template #default="scope">
          <dict-tag :options="[{label:'自有',value:'own',elTagType:'success'},{label:'默认配置',value:'default',elTagType:'info'},{label:'跨站共享',value:'shared',elTagType:'warning'}]" :value="scope.row.relationSource" v-if="scope.row.relationSource" />
        </template>
      </el-table-column>
      <el-table-column label="关联网站" align="center" width="120">
        <template #default="scope">
          <!-- 默认配置显示排除数量或全局可见 -->
          <template v-if="isPersonalSiteCheck(scope.row.siteId)">
            <el-tag 
              v-if="(scope.row.excludedSitesCount || 0) > 0"
              type="warning"
              style="cursor: pointer" 
              @click="handleManageSites(scope.row)"
            >
              排除 {{ scope.row.excludedSitesCount }} 个
            </el-tag>
            <el-tag 
              v-else
              type="success"
              style="cursor: pointer" 
              @click="handleManageSites(scope.row)"
            >
              全局可见
            </el-tag>
          </template>
          <!-- 其他配置显示关联数量 -->
          <el-tag 
            v-else-if="scope.row.relatedSitesCount > 0" 
            type="primary" 
            style="cursor: pointer" 
            @click="handleManageSites(scope.row)"
          >
            {{ scope.row.relatedSitesCount }} 个
          </el-tag>
          <el-tag 
            v-else-if="!isPersonalSiteCheck(scope.row.siteId)"
            type="info" 
            style="cursor: pointer" 
            @click="handleManageSites(scope.row)"
          >
            未配置
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="categoryId" width="140">
        <template #default="scope">
          <CategoryTag v-if="scope.row.categoryName" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'workflow', icon: scope.row.categoryIcon }" size="small" />
          <el-tag v-else type="info" size="small">未分类</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" prop="description" show-overflow-tooltip />
      <el-table-column label="触发类型" align="center" prop="triggerType" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.triggerType === 'manual'" type="primary">手动触发</el-tag>
          <el-tag v-else-if="scope.row.triggerType === 'scheduled'" type="warning">定时触发</el-tag>
          <el-tag v-else-if="scope.row.triggerType === 'event'" type="success">事件触发</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="步骤数" align="center" prop="stepCount" width="100">
        <template #default="scope">
          <el-tooltip v-if="statsMap[scope.row.workflowCode] && statsMap[scope.row.workflowCode].totalCount" placement="top" effect="light">
            <template #content>
              <div style="line-height:1.8; min-width:160px">
                <div>总执行：<strong>{{ statsMap[scope.row.workflowCode].totalCount }}</strong> 次</div>
                <div>成功：<span style="color:#67c23a">{{ statsMap[scope.row.workflowCode].successCount || 0 }}</span> &nbsp;失败：<span style="color:#f56c6c">{{ statsMap[scope.row.workflowCode].failedCount || 0 }}</span></div>
                <div v-if="statsMap[scope.row.workflowCode].avgDuration">平均耗时：{{ Math.round(statsMap[scope.row.workflowCode].avgDuration / 1000) }}s</div>
                <div v-if="statsMap[scope.row.workflowCode].lastRunTime">上次执行：{{ parseTime(statsMap[scope.row.workflowCode].lastRunTime) }}</div>
              </div>
            </template>
            <div style="display:inline-flex; align-items:center; gap:4px; cursor:pointer" @click="handleHistory(scope.row)">
              <el-tag size="small" type="primary">{{ scope.row.stepCount || 0 }}步</el-tag>
              <el-tag size="small" type="info" round style="min-width:22px; padding:0 5px">{{ statsMap[scope.row.workflowCode].totalCount }}</el-tag>
            </div>
          </el-tooltip>
          <el-tag v-else size="small" type="info">{{ scope.row.stepCount || 0 }}步</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="可见性" align="center" width="100" v-if="viewMode === 'related' && queryParams.siteId && !isPersonalSiteCheck(queryParams.siteId)">
        <template #header>
          <el-tooltip placement="top" effect="light">
            <template #content>
              <div style="max-width: 300px; line-height: 1.6;">
                <p style="margin: 0 0 8px 0; font-weight: bold;">可见性控制说明：</p>
                <p style="margin: 0 0 4px 0;">• <strong>默认配置</strong>：切换会调用排除管理，排除后不显示</p>
                <p style="margin: 0 0 4px 0;">• <strong>自有数据</strong>：切换启用/禁用状态（enabled: 1=启用, 0=禁用）</p>
                <p style="margin: 0;">• <strong>共享数据</strong>：切换会修改关联可见性</p>
              </div>
            </template>
            <span style="cursor: help;">
              可见性
              <el-icon style="margin-left: 4px;"><QuestionFilled /></el-icon>
            </span>
          </el-tooltip>
        </template>
        <template #default="scope">
          <el-tag
            :type="getVisibilityValue(scope.row) === '1' ? 'success' : 'info'"
            @click="handleToggleVisibility(scope.row)"
            style="cursor: pointer; user-select: none;"
            size="small"
          >
            <el-icon style="margin-right: 4px;">
              <View v-if="getVisibilityValue(scope.row) === '1'" />
              <Hide v-else />
            </el-icon>
            {{ getVisibilityValue(scope.row) === '1' ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="120" v-if="viewMode === 'creator'">
        <template #default="scope">
          <el-switch
            :model-value="scope.row.enabled"
            :active-value="1"
            :inactive-value="0"
            @click="handleStatusChange(scope.row)"
            v-hasPermi="['content:workflow:edit']"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <!-- 关联模式下的操作 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的配置 - 可编辑、执行、删除 -->
            <template v-if="scope.row.relationSource === 'own'">
              <el-tooltip content="编辑" placement="top">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['content:workflow:edit']" />
              </el-tooltip>
              <el-tooltip content="执行" placement="top">
                <el-button link type="primary" icon="VideoPlay" @click="handleExecute(scope.row)" v-hasPermi="['content:workflow:execute']" />
              </el-tooltip>
              <el-tooltip content="网站关联" placement="top">
                <el-button link type="warning" icon="Link" @click="handleManageSites(scope.row)" v-hasPermi="['content:workflow:edit']" />
              </el-tooltip>
              <el-tooltip content="历史" placement="top">
                <el-button link type="primary" icon="Document" @click="handleHistory(scope.row)" v-hasPermi="['content:workflow:query']" />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['content:workflow:remove']" />
              </el-tooltip>
            </template>
            <!-- default: 默认配置 - 可排除/恢复 -->
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tooltip content="执行" placement="top">
                <el-button link type="primary" icon="VideoPlay" @click="handleExecute(scope.row)" v-hasPermi="['content:workflow:execute']" />
              </el-tooltip>
              <el-tooltip content="历史" placement="top">
                <el-button link type="primary" icon="Document" @click="handleHistory(scope.row)" v-hasPermi="['content:workflow:query']" />
              </el-tooltip>
              <el-tag v-if="scope.row.isExcluded" type="info" size="small">已排除</el-tag>
            </template>
            <!-- shared: 其他网站分享 - 可执行、移除关联 -->
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-tooltip content="执行" placement="top">
                <el-button link type="primary" icon="VideoPlay" @click="handleExecute(scope.row)" v-hasPermi="['content:workflow:execute']" />
              </el-tooltip>
              <el-tooltip content="历史" placement="top">
                <el-button link type="primary" icon="Document" @click="handleHistory(scope.row)" v-hasPermi="['content:workflow:query']" />
              </el-tooltip>
            </template>
          </template>
          <!-- 创建者模式下的操作 -->
          <template v-else>
            <el-tooltip content="编辑" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['content:workflow:edit']" />
            </el-tooltip>
            <el-tooltip content="执行" placement="top">
              <el-button link type="primary" icon="VideoPlay" @click="handleExecute(scope.row)" v-hasPermi="['content:workflow:execute']" />
            </el-tooltip>
            <el-tooltip v-if="!isPersonalSiteCheck(scope.row.siteId)" content="网站关联" placement="top">
              <el-button link type="warning" icon="Link" @click="handleManageSites(scope.row)" v-hasPermi="['content:workflow:edit']" />
            </el-tooltip>
            <el-tooltip v-if="isPersonalSiteCheck(scope.row.siteId)" content="排除管理" placement="top">
              <el-button link type="danger" icon="CircleClose" @click="handleManageExclusions(scope.row)" v-hasPermi="['content:workflow:edit']" />
            </el-tooltip>
            <el-tooltip content="历史" placement="top">
              <el-button link type="primary" icon="Document" @click="handleHistory(scope.row)" v-hasPermi="['content:workflow:query']" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['content:workflow:remove']" />
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 网站关联管理对话框 -->
    <SiteRelationManager
      v-model="siteRelationDialogOpen"
      entity-type="workflow"
      :entity-id="currentWorkflowIdForSites"
      :entity-name="currentWorkflowNameForSites"
      :creator-site-id="currentWorkflowCreatorSiteId"
      @refresh="getList"
    />

    <!-- 工作流可视化编辑器 -->
    <WorkflowVisualEditor ref="workflowEditorRef" @success="getList" />

    <!-- 执行工作流对话框 -->
    <el-dialog v-model="executeOpen" width="560px" append-to-body :close-on-click-modal="!executing" :show-close="!executingProgress">
      <template #header>
        <div style="display:flex; align-items:center; gap:10px;">
          <el-icon style="color:#409EFF; font-size:18px;"><VideoPlay /></el-icon>
          <span style="font-size:16px; font-weight:600;">执行工作流</span>
        </div>
      </template>

      <!-- 执行模式选择 -->
      <div style="display:flex; gap:12px; margin-bottom:20px;">
        <div
          v-for="item in [{label:'sync', title:'前台执行', desc:'页面等待，完成后自动展示结果', icon:'Monitor'}, {label:'async', title:'后台执行', desc:'立即返回，在历史中查看结果', icon:'Timer'}]"
          :key="item.label"
          @click="!executing && (executeForm.mode = item.label)"
          :style="{
            flex:1, padding:'12px 16px', borderRadius:'8px', cursor: executing ? 'not-allowed' : 'pointer',
            border: executeForm.mode === item.label ? '2px solid #409EFF' : '2px solid #e4e7ed',
            background: executeForm.mode === item.label ? '#ecf5ff' : '#fafafa',
            transition: 'all .2s'
          }"
        >
          <div style="display:flex; align-items:center; gap:8px; margin-bottom:4px;">
            <el-icon :style="{color: executeForm.mode === item.label ? '#409EFF' : '#909399'}">
              <component :is="item.icon" />
            </el-icon>
            <span :style="{fontWeight:600, color: executeForm.mode === item.label ? '#409EFF' : '#303133'}">{{ item.title }}</span>
            <el-tag v-if="executeForm.mode === item.label" type="primary" size="small" effect="light">当前</el-tag>
          </div>
          <div style="font-size:12px; color:#909399;">{{ item.desc }}</div>
        </div>
      </div>

      <!-- 输入参数 -->
      <div style="margin-bottom:6px; font-size:13px; font-weight:600; color:#606266; display:flex; align-items:center; gap:6px;">
        <el-icon style="color:#409EFF;"><Setting /></el-icon>输入参数
      </div>
      <div style="background:#f7f8fa; border-radius:8px; padding:16px 16px 4px; margin-bottom:12px;">
        <el-form label-width="90px" size="default">
          <template v-if="workflowInputParams && workflowInputParams.length > 0">
            <el-form-item
              v-for="param in workflowInputParams"
              :key="param.name"
              :label="param.label || param.name"
              :required="param.required"
              style="margin-bottom:14px;"
            >
              <el-input
                v-if="param.type === 'string' || !param.type"
                v-model="executeForm.inputs[param.name]"
                :placeholder="param.description || '请输入' + (param.label || param.name)"
                clearable
              />
              <el-input-number
                v-else-if="param.type === 'number'"
                v-model="executeForm.inputs[param.name]"
                style="width:100%"
              />
              <el-switch
                v-else-if="param.type === 'boolean'"
                v-model="executeForm.inputs[param.name]"
              />
              <el-input
                v-else
                v-model="executeForm.inputs[param.name]"
                :type="param.type === 'array' || param.type === 'object' ? 'textarea' : 'text'"
                :rows="3"
                :placeholder="param.description || '请输入' + (param.label || param.name)"
              />
            </el-form-item>
          </template>
          <el-empty v-else description="该工作流没有定义输入参数" :image-size="60" style="padding:8px 0;" />
        </el-form>
      </div>

      <!-- 高级模式 -->
      <el-collapse-transition>
        <div v-if="showJsonInput" style="margin-bottom:12px;">
          <el-input
            v-model="executeForm.inputDataJson"
            type="textarea"
            :rows="6"
            placeholder="请输入JSON格式的输入数据"
            style="font-family:monospace; font-size:12px;"
          />
        </div>
      </el-collapse-transition>
      <div style="margin-bottom:12px;">
        <el-checkbox v-model="showJsonInput" size="small">高级模式（直接编辑 JSON）</el-checkbox>
      </div>

      <!-- 执行进度 -->
      <el-collapse-transition>
        <el-alert v-if="executingProgress" :title="executingStatus" type="info" :closable="false" show-icon style="margin-bottom:4px;">
          <template #icon><el-icon class="is-loading"><Loading /></el-icon></template>
        </el-alert>
      </el-collapse-transition>

      <template #footer>
        <div style="display:flex; justify-content:flex-end; gap:10px;">
          <el-button @click="executeOpen = false" :disabled="executingProgress">取消</el-button>
          <el-button type="primary" @click="submitExecute" :loading="executing" :disabled="executing" style="min-width:80px;">
            <el-icon v-if="!executing"><VideoPlay /></el-icon>
            {{ executing ? '执行中...' : '立即执行' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 执行历史对话框 -->
    <el-dialog :title="'执行历史 - ' + historyWorkflowName" v-model="historyOpen" width="1000px" append-to-body>
      <!-- 统计卡片 -->
      <div v-if="historyStats && historyStats.totalCount" style="display:flex; gap:12px; margin-bottom:16px; flex-wrap:wrap">
        <el-card shadow="never" style="flex:1; min-width:110px; background:#f0f9eb; border-color:#b3e19d">
          <div style="text-align:center">
            <div style="font-size:22px; font-weight:bold; color:#67c23a">{{ historyStats.totalCount || 0 }}</div>
            <div style="font-size:12px; color:#909399; margin-top:4px">总执行次数</div>
          </div>
        </el-card>
        <el-card shadow="never" style="flex:1; min-width:110px; background:#f0f9eb; border-color:#b3e19d">
          <div style="text-align:center">
            <div style="font-size:22px; font-weight:bold; color:#67c23a">{{ historyStats.successCount || 0 }}</div>
            <div style="font-size:12px; color:#909399; margin-top:4px">成功次数</div>
          </div>
        </el-card>
        <el-card shadow="never" style="flex:1; min-width:110px; background:#fef0f0; border-color:#fbc4c4">
          <div style="text-align:center">
            <div style="font-size:22px; font-weight:bold; color:#f56c6c">{{ historyStats.failedCount || 0 }}</div>
            <div style="font-size:12px; color:#909399; margin-top:4px">失败次数</div>
          </div>
        </el-card>
        <el-card shadow="never" style="flex:1; min-width:110px; background:#ecf5ff; border-color:#b3d8ff">
          <div style="text-align:center">
            <div style="font-size:22px; font-weight:bold; color:#409eff">
              {{ historyStats.totalCount > 0 ? Math.round((historyStats.successCount || 0) / historyStats.totalCount * 100) : 0 }}%
            </div>
            <div style="font-size:12px; color:#909399; margin-top:4px">成功率</div>
          </div>
        </el-card>
        <el-card shadow="never" style="flex:1; min-width:130px; background:#fdf6ec; border-color:#f5dab1">
          <div style="text-align:center">
            <div style="font-size:22px; font-weight:bold; color:#e6a23c">
              {{ historyStats.avgDuration ? Math.round(historyStats.avgDuration / 1000) + 's' : '-' }}
            </div>
            <div style="font-size:12px; color:#909399; margin-top:4px">平均耗时</div>
          </div>
        </el-card>
      </div>

      <!-- 状态过滤 -->
      <div style="display:flex; align-items:center; margin-bottom:12px; gap:8px">
        <span style="font-size:13px; color:#606266">状态过滤：</span>
        <el-radio-group v-model="historyFilterStatus" size="small" @change="() => { historyQueryParams.pageNum = 1; getHistoryList() }">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="success">成功</el-radio-button>
          <el-radio-button value="failed">失败</el-radio-button>
          <el-radio-button value="running">运行中</el-radio-button>
        </el-radio-group>
        <el-button icon="Refresh" size="small" @click="getHistoryList" style="margin-left:auto">刷新</el-button>
      </div>

      <el-table v-loading="historyLoading" :data="historyList" border stripe size="small">
        <el-table-column label="执行 ID" align="center" prop="executionId" width="200" show-overflow-tooltip />
        <el-table-column label="执行模式" align="center" prop="mode" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.mode === 'sync'"   type="primary" size="small">前台</el-tag>
            <el-tag v-else-if="scope.row.mode === 'async'"  type="warning" size="small">后台</el-tag>
            <el-tag v-else-if="scope.row.mode === 'scheduled'" type="info" size="small">定时</el-tag>
            <span v-else>{{ scope.row.mode }}</span>
          </template>
        </el-table-column>
        <el-table-column label="执行状态" align="center" prop="status" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'success'" type="success" size="small">成功</el-tag>
            <el-tag v-else-if="scope.row.status === 'failed'"  type="danger"  size="small">失败</el-tag>
            <el-tag v-else-if="scope.row.status === 'running'" type="warning" size="small">运行中</el-tag>
            <el-tag v-else-if="scope.row.status === 'pending'" type="info"    size="small">等待中</el-tag>
            <el-tag v-else type="info" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" align="center" prop="startTime" width="160">
          <template #default="scope"><span>{{ parseTime(scope.row.startTime) }}</span></template>
        </el-table-column>
        <el-table-column label="耗时" align="center" prop="duration" width="90">
          <template #default="scope">
            <span v-if="scope.row.duration != null">
              {{ scope.row.duration >= 60000 ? Math.floor(scope.row.duration/60000) + '分' + Math.round((scope.row.duration%60000)/1000) + '秒'
                : scope.row.duration >= 1000 ? (scope.row.duration/1000).toFixed(1) + 's'
                : scope.row.duration + 'ms' }}
            </span>
            <span v-else style="color:#c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="错误信息" align="center" prop="error" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.error" style="color:#f56c6c; font-size:12px">{{ scope.row.error }}</span>
            <span v-else style="color:#c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column label="记录时间" align="center" prop="createTime" width="160">
          <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="70" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleViewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="historyTotal > 0"
        :total="historyTotal"
        v-model:page="historyQueryParams.pageNum"
        v-model:limit="historyQueryParams.pageSize"
        @pagination="getHistoryList"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="historyOpen = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 执行详情对话框 -->
    <el-dialog title="执行详情" v-model="detailOpen" width="80%" append-to-body>
      <el-tabs v-model="detailActiveTab" type="border-card">
        <!-- 概览 Tab -->
        <el-tab-pane label="概览" name="overview">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="执行 ID" :span="2">{{ executionDetail.executionId }}</el-descriptions-item>
            <el-descriptions-item label="工作流编码">{{ executionDetail.workflowCode }}</el-descriptions-item>
            <el-descriptions-item label="工作流名称">{{ executionDetail.workflowName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="执行模式">
              <el-tag v-if="executionDetail.mode === 'sync'" type="primary" size="small">前台</el-tag>
              <el-tag v-else-if="executionDetail.mode === 'async'" type="warning" size="small">后台</el-tag>
              <el-tag v-else-if="executionDetail.mode === 'scheduled'" type="info" size="small">定时</el-tag>
              <span v-else>{{ executionDetail.mode }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="执行状态">
              <el-tag v-if="executionDetail.status === 'success'" type="success">成功</el-tag>
              <el-tag v-else-if="executionDetail.status === 'failed'" type="danger">失败</el-tag>
              <el-tag v-else-if="executionDetail.status === 'running'" type="warning">运行中</el-tag>
              <el-tag v-else type="info">{{ executionDetail.status }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ parseTime(executionDetail.startTime) }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ parseTime(executionDetail.endTime) }}</el-descriptions-item>
            <el-descriptions-item label="执行耗时" :span="2">
              <span v-if="executionDetail.duration != null">
                {{ executionDetail.duration >= 60000 ? Math.floor(executionDetail.duration/60000) + '分' + Math.round((executionDetail.duration%60000)/1000) + '秒'
                  : executionDetail.duration >= 1000 ? (executionDetail.duration/1000).toFixed(2) + 's'
                  : executionDetail.duration + 'ms' }}
              </span>
              <span v-else>-</span>
            </el-descriptions-item>
          </el-descriptions>

          <template v-if="executionDetail.error">
            <el-divider>错误信息</el-divider>
            <el-alert type="error" :closable="false" show-icon style="white-space:pre-wrap; word-break:break-all">
              {{ executionDetail.error }}
            </el-alert>
          </template>

          <el-divider>输入数据</el-divider>
          <textarea readonly class="json-textarea" :value="formattedDetailInput"></textarea>

          <el-divider>输出数据</el-divider>
          <textarea readonly class="json-textarea" :value="formattedDetailOutput"></textarea>
        </el-tab-pane>

        <!-- 步骤记录 Tab -->
        <el-tab-pane name="steps">
          <template #label>
            步骤记录
            <el-badge v-if="stepExecutionList.length" :value="stepExecutionList.length" :max="99"
              style="margin-left:4px; vertical-align:middle" />
          </template>
          <div v-loading="stepExecutionLoading" style="min-height:120px">
            <el-empty v-if="!stepExecutionLoading && !stepExecutionList.length" description="暂无步骤记录" />
            <el-timeline v-else>
              <el-timeline-item
                v-for="(step, idx) in stepExecutionList"
                :key="step.id"
                :type="step.status === 'success' ? 'success' : step.status === 'failed' ? 'danger' : step.status === 'running' ? 'warning' : 'info'"
                :timestamp="parseTime(step.startTime)"
                placement="top"
              >
                <el-card shadow="never" :style="step.status === 'failed' ? 'border-color:#f56c6c' : ''">
                  <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px">
                    <span style="font-weight:bold; font-size:14px">
                      {{ idx + 1 }}. {{ step.stepName || step.stepId || ('步骤 ' + (idx+1)) }}
                    </span>
                    <div style="display:flex; align-items:center; gap:8px">
                      <el-tag v-if="step.toolCode" size="small" type="info">{{ step.toolCode }}</el-tag>
                      <el-tag
                        :type="step.status === 'success' ? 'success' : step.status === 'failed' ? 'danger' : step.status === 'running' ? 'warning' : 'info'"
                        size="small"
                      >
                        {{ step.status === 'success' ? '成功' : step.status === 'failed' ? '失败' : step.status === 'running' ? '运行中' : step.status }}
                      </el-tag>
                      <span v-if="step.duration != null" style="font-size:12px; color:#909399">
                        {{ step.duration >= 1000 ? (step.duration/1000).toFixed(2) + 's' : step.duration + 'ms' }}
                      </span>
                    </div>
                  </div>
                  <!-- 错误信息 -->
                  <el-alert v-if="step.error" type="error" :closable="false" size="small"
                    style="margin-bottom:8px; white-space:pre-wrap; word-break:break-all; font-size:12px">
                    {{ step.error }}
                  </el-alert>
                  <!-- 输入/输出折叠（用普通按钮切换，避免 el-collapse 高度动画引发大文本 layout 卡顿） -->
                  <div style="margin-top:4px">
                    <el-button link size="small" style="font-size:12px; color:#606266; padding:0"
                      @click="toggleStepIO(step)">
                      {{ step._ioOpen ? '▲ 收起输入/输出数据' : '▶ 查看输入/输出数据' }}
                    </el-button>
                    <div v-if="step._ioOpen">
                      <div v-if="step.inputData" style="margin-top:6px">
                        <div style="font-size:12px; color:#909399; margin-bottom:4px">输入：</div>
                        <textarea readonly class="json-textarea" :value="step._ioReady ? step._formattedInput : ''"></textarea>
                      </div>
                      <div v-if="step.outputData" style="margin-top:8px">
                        <div style="font-size:12px; color:#909399; margin-bottom:4px">输出：</div>
                        <textarea readonly class="json-textarea" :value="step._ioReady ? step._formattedOutput : ''"></textarea>
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 从模板创建工作流对话框 -->
    <el-dialog title="从模板创建工作流" v-model="templateDialogOpen" width="820px" append-to-body>
      <div v-loading="templateLoading" style="min-height:120px">
        <el-empty v-if="!templateLoading && !templateList.length" description="暂无可用模板" />
        <el-row :gutter="16">
          <el-col :span="8" v-for="tpl in templateList" :key="tpl.id" style="margin-bottom:16px">
            <el-card shadow="hover" style="cursor:pointer; height:100%">
              <div style="text-align:center; font-size:36px; margin-bottom:8px; line-height:1.2">{{ tpl.icon || '📋' }}</div>
              <div style="font-weight:bold; text-align:center; margin-bottom:8px; font-size:14px">{{ tpl.templateName }}</div>
              <div style="color:#909399; font-size:12px; margin-bottom:12px; min-height:36px; line-height:1.5">{{ tpl.description || '暂无描述' }}</div>
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px">
                <el-tag size="small" type="info">{{ tpl.category || '通用' }}</el-tag>
                <span style="font-size:12px; color:#c0c4cc">使用 {{ tpl.usageCount || 0 }} 次</span>
              </div>
              <div style="text-align:center">
                <el-button type="primary" size="small" @click="handleUseTemplate(tpl)">使用此模板</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <el-button @click="templateDialogOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 批量排除管理对话框 -->
    <el-dialog 
      v-model="batchExclusionDialogOpen" 
      title="批量排除管理" 
      width="650px"
      append-to-body
    >
      <el-alert
        title="操作说明"
        type="info"
        :closable="false"
        style="margin-bottom: 12px"
      >
        <div style="line-height: 1.6;">
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选工作流）</p>
          <p style="margin: 0;">• <strong>不勾选的网站</strong> = 移除 exclude 排除（默认全部可见）</p>
        </div>
      </el-alert>

      <!-- 冲突提示 -->
      <el-alert
        type="warning"
        :closable="false"
        style="margin-bottom: 12px"
      >
        <template #title>
          <span>冲突规则：<strong>include 关联优先于 exclude 排除</strong></span>
        </template>
        <div style="line-height: 1.6; font-size: 13px;">
          <p style="margin: 0 0 4px 0;">当某网站同时存在 include 关联和 exclude 排除时，即便勾选了 exclude 排除，<strong>include 仍优先生效</strong>，exclude 被自动忽略。</p>
          <p style="margin: 0;">如需彻底生效排除，请先在"批量关联管理"中移除相应网站的 include 关联。</p>
        </div>
      </el-alert>
      
      <div style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; margin-bottom: 8px;">
          <strong>已选工作流：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedWorkflowsForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="workflow in selectedWorkflowsForBatchExclude" 
            :key="workflow.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeWorkflowFromBatchExclude(workflow.id)"
            size="small"
          >
            {{ workflow.workflowName }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各工作流的当前排除/关联状态 -->
      <el-collapse v-if="workflowExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各工作流当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="workflowExclusionDetails" size="small" stripe>
              <el-table-column label="工作流名称" prop="workflowName" width="150" show-overflow-tooltip />
              <el-table-column label="include 关联">
                <template #default="scope">
                  <span v-if="!scope.row.includedSiteIds || scope.row.includedSiteIds.length === 0" style="color: #909399; font-size: 13px;">无关联</span>
                  <el-tag 
                    v-else
                    v-for="siteId in scope.row.includedSiteIds"
                    :key="siteId"
                    type="primary"
                    size="small"
                    style="margin: 2px 4px 2px 0;"
                  >
                    {{ getSiteName(siteId) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="exclude 排除">
                <template #default="scope">
                  <span v-if="scope.row.excludedSiteIds.length === 0" style="color: #909399; font-size: 13px;">未排除</span>
                  <template v-else>
                    <el-tag 
                      v-for="siteId in scope.row.excludedSiteIds" 
                      :key="siteId"
                      :type="scope.row.includedSiteIds && scope.row.includedSiteIds.includes(siteId) ? 'warning' : 'danger'"
                      size="small" 
                      style="margin: 2px 4px 2px 0;"
                    >
                      <el-icon v-if="scope.row.includedSiteIds && scope.row.includedSiteIds.includes(siteId)" style="margin-right:2px;"><Warning /></el-icon>
                      {{ getSiteName(siteId) }}
                    </el-tag>
                  </template>
                </template>
              </el-table-column>
            </el-table>
            <div style="font-size: 12px; color: #E6A23C; margin-top: 8px; padding: 0 4px;">
              <el-icon><Warning /></el-icon> 标色标识表示冲突：include 和 exclude 同时存在，此时仅 include 关联生效
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
      
      <el-divider />
      
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
        <div>
          <strong>选择要排除的网站：</strong>
          <el-tag type="warning" size="small" style="margin-left: 8px;">已勾选 {{ batchExcludedSiteIds.length }} 个</el-tag>
        </div>
        <div>
          <el-button size="small" @click="selectAllSitesForBatchExclude">全选</el-button>
          <el-button size="small" @click="deselectAllSitesForBatchExclude">清空</el-button>
          <el-button size="small" @click="invertSiteSelectionForBatchExclude">反选</el-button>
        </div>
      </div>
      
      <div style="max-height: 300px; overflow-y: auto; padding: 12px; border: 1px solid #DCDFE6; border-radius: 4px; background: #fff;">
        <el-checkbox-group v-model="batchExcludedSiteIds">
          <div
            v-for="site in siteList.filter(s => s.isPersonal !== 1)"
            :key="site.id"
            style="display: flex; align-items: center; margin: 8px 0;"
          >
            <el-checkbox :label="site.id">{{ site.name }}</el-checkbox>
            <!-- 冲突标识：该网站已勾选且存在 include 关联 -->
            <el-tooltip
              v-if="batchExcludedSiteIds.includes(site.id) && batchExclusionConflictSiteIds.has(site.id)"
              content="该网站已存在 include 关联， exclude 将被忽略，include 优先生效"
              placement="right"
            >
              <el-tag type="warning" size="small" style="margin-left: 8px;">冲突</el-tag>
            </el-tooltip>
          </div>
        </el-checkbox-group>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitBatchExclusions" :loading="batchExclusionLoading">确 定</el-button>
          <el-button @click="batchExclusionDialogOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量关联管理对话框 -->
    <el-dialog 
      title="批量关联管理" 
      v-model="batchRelationDialogOpen" 
      width="650px" 
      append-to-body
    >
      <el-alert
        title="操作说明"
        type="info"
        :closable="false"
        style="margin-bottom: 12px"
      >
        <div style="line-height: 1.6;">
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选工作流对该网站可见）</p>
          <p style="margin: 0;">• <strong>不勾选的网站</strong> = 移除 include 关联（共享取消）</p>
        </div>
      </el-alert>

      <!-- 默认配置冲突提示 -->
      <el-alert
        v-if="batchRelationIsDefaultConfig"
        type="warning"
        :closable="false"
        style="margin-bottom: 12px"
      >
        <template #title>
          <span>冲突规则：<strong>include 关联优先于 exclude 排除</strong></span>
        </template>
        <div style="line-height: 1.6; font-size: 13px;">
          <p style="margin: 0 0 4px 0;">当某网站同时存在 include 关联和 exclude 排除时，<strong>仅 include 关联生效</strong>，exclude 被自动忽略。</p>
          <p style="margin: 0;">如需彻底清除冲突，可先在"批量排除管理"中移除相应网站的排除设置。</p>
        </div>
      </el-alert>
      
      <div style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; margin-bottom: 8px;">
          <strong>已选工作流：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedWorkflowsForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="workflow in selectedWorkflowsForBatchRelation" 
            :key="workflow.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeWorkflowFromBatchRelation(workflow.id)"
            size="small"
          >
            {{ workflow.workflowName }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各工作流的当前关联/排除状态 -->
      <el-collapse v-if="workflowRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各工作流当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="workflowRelationDetails" size="small" stripe>
              <el-table-column label="工作流名称" prop="workflowName" width="150" show-overflow-tooltip />
              <el-table-column label="include 关联">
                <template #default="scope">
                  <span v-if="scope.row.relatedSiteIds.length === 0" style="color: #909399; font-size: 13px;">未关联</span>
                  <el-tag 
                    v-else 
                    v-for="siteId in scope.row.relatedSiteIds" 
                    :key="siteId" 
                    type="primary"
                    size="small" 
                    style="margin: 2px 4px 2px 0;"
                  >
                    {{ getSiteName(siteId) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column v-if="batchRelationIsDefaultConfig" label="exclude 排除">
                <template #default="scope">
                  <span v-if="!scope.row.excludedSiteIds || scope.row.excludedSiteIds.length === 0" style="color: #909399; font-size: 13px;">无排除</span>
                  <template v-else>
                    <el-tag
                      v-for="siteId in scope.row.excludedSiteIds"
                      :key="siteId"
                      :type="scope.row.relatedSiteIds.includes(siteId) ? 'warning' : 'danger'"
                      size="small"
                      style="margin: 2px 4px 2px 0;"
                    >
                      <template v-if="scope.row.relatedSiteIds.includes(siteId)">
                        <el-icon style="margin-right:2px;"><Warning /></el-icon>
                      </template>
                      {{ getSiteName(siteId) }}
                    </el-tag>
                  </template>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="batchRelationIsDefaultConfig" style="font-size: 12px; color: #E6A23C; margin-top: 8px; padding: 0 4px;">
              <el-icon><Warning /></el-icon> 标色标识表示冲突：include 和 exclude 同时存在，此时仅 include 关联生效
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
      
      <el-divider />
      
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
        <div>
          <strong>选择要关联的网站：</strong>
          <el-tag type="primary" size="small" style="margin-left: 8px;">已勾选 {{ batchRelatedSiteIds.length }} 个</el-tag>
        </div>
        <div>
          <el-button size="small" @click="selectAllSitesForBatchRelation">全选</el-button>
          <el-button size="small" @click="deselectAllSitesForBatchRelation">清空</el-button>
          <el-button size="small" @click="invertSiteSelectionForBatchRelation">反选</el-button>
        </div>
      </div>
      
      <div style="max-height: 300px; overflow-y: auto; padding: 12px; border: 1px solid #DCDFE6; border-radius: 4px; background: #fff;">
        <el-checkbox-group v-model="batchRelatedSiteIds">
          <div
            v-for="site in siteList.filter(s => s.id !== queryParams.siteId && s.isPersonal !== 1)"
            :key="site.id"
            style="display: flex; align-items: center; margin: 8px 0;"
          >
            <el-checkbox :label="site.id">{{ site.name }}</el-checkbox>
            <!-- 冲突标识：该网站已勾选且存在排除关系 -->
            <el-tooltip
              v-if="batchRelationIsDefaultConfig && batchRelatedSiteIds.includes(site.id) && batchRelationConflictSiteIds.has(site.id)"
              content="该网站同时存在 exclude 排除关系，但 include 将优先生效"
              placement="right"
            >
              <el-tag type="warning" size="small" style="margin-left: 8px;">冲突</el-tag>
            </el-tooltip>
          </div>
        </el-checkbox-group>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitBatchRelations" :loading="batchRelationLoading">确 定</el-button>
          <el-button @click="batchRelationDialogOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导出对话框 -->
    <ExportDialog
      v-model="exportDialogOpen"
      :selectedCount="ids.length"
      v-model:exportFormat="exportFormat"
      :loading="exportLoading"
      @confirm="confirmExport"
    >
      <template #exportTips>
        <p>• 工作流数据：包含工作流定义、步骤配置和关联关系</p>
        <p>• 导出格式支持 Excel 和 JSON</p>
        <p style="margin-top: 10px; color: #909399;">导入时将自动归属到当前选择的网站</p>
      </template>
    </ExportDialog>

    <!-- 全站导出对话框 -->
    <FullExportDialog
      v-model="fullExportDialogOpen"
      v-model:exportFormat="fullExportFormat"
      :loading="fullExportLoading"
      @confirm="confirmFullExport"
    >
      <template #exportTips>
        <p>• 全站导出将包含所有工作流数据（默认配置 + 所有站点）</p>
        <p>• 包含所有网站关联关系</p>
        <p>• 包含默认配置排除关系</p>
        <p style="margin-top: 10px; color: #F56C6C; font-weight: bold;">⚠️ 数据量可能较大，请耐心等待</p>
      </template>
    </FullExportDialog>

    <!-- 系统导入对话框 -->
    <ImportDialog
      v-model="importDialogOpen"
      :loading="importLoading"
      :previewData="importPreviewData"
      :translationsData="[]"
      @confirm="confirmSystemImport"
      @fileChange="handleSystemImportFileChange"
      @fileRemove="handleSystemImportFileRemove"
    >
      <template #importTips>
        <p>• 支持导入 Excel (.xlsx) 或 JSON (.json) 格式文件</p>
        <p>• 将根据导入数据重建工作流配置和关联关系</p>
        <p>• 请确保文件格式与导出的数据格式一致</p>
        <p>• 必填字段：工作流编码、工作流名称</p>
      </template>
      <template #previewColumns>
        <el-table-column prop="workflowCode" label="工作流编码" width="150" />
        <el-table-column prop="workflowName" label="工作流名称" width="150" />
        <el-table-column prop="triggerType" label="触发类型" width="100" />
        <el-table-column prop="enabled" label="状态" width="80" />
      </template>
    </ImportDialog>

    <!-- 全站导入对话框 -->
    <FullImportDialog
      v-model="fullImportDialogOpen"
      :loading="fullImportLoading"
      :siteList="siteList"
      :importSites="fullImportSites"
      :importData="fullImportWorkflows"
      v-model:importTranslations="fullImportTranslations"
      v-model:importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      :dataLabel="'工作流'"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #importTips>
        <p style="margin: 0;">从其他系统导入完整的工作流数据，包括：</p>
        <ul style="margin: 5px 0; padding-left: 20px;">
          <li>工作流配置基础数据</li>
          <li>网站关联关系</li>
          <li>默认配置排除关系</li>
        </ul>
      </template>
    </FullImportDialog>
  </div>
</template>

<script setup name="Workflow">
import { listWorkflow, getWorkflow, getWorkflowByCode, addWorkflow, updateWorkflow, delWorkflow, executeWorkflow, getExecutionStatus, listExecutionHistory, getExecutionStats, listStepExecutions, listWorkflowTemplates, createWorkflowFromTemplate } from '@/api/gamebox/workflow'
import { getWorkflowSites, getBatchWorkflowSites, batchSaveWorkflowSiteRelations, updateWorkflowVisibility } from '@/api/gamebox/siteRelation'
import { listSite } from '@/api/gamebox/site'
import { enrichSiteList, getSiteDisplayName, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from '@/composables/useSiteSelection'
import { listCategory } from '@/api/gamebox/category'
import SiteSelect from '@/components/SiteSelect/index.vue'
import CategoryTag from '@/components/CategoryTag/index.vue'
import SiteRelationManager from '@/components/SiteRelationManager/index.vue'
import { ExportDialog, ImportDialog, FullExportDialog, FullImportDialog } from "@/components/ImportExportDialogs"
import WorkflowVisualEditor from './components/WorkflowVisualEditor.vue'

const { proxy } = getCurrentInstance()
const workflowEditorRef = ref(null)

const workflowList = ref([])
const siteList = ref([])
const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)
const workflowCategoryList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const activeTab = ref('basic')

// 查看模式和关联相关
const viewMode = ref('creator')
const includeDefaultConfig = ref(false)

// 网站ID缓存
const siteNameCache = ref({})

// 执行相关
const executeOpen = ref(false)
const executing = ref(false)
const executingProgress = ref(false)  // 同步模式正在轮询中
const executingStatus = ref('')       // 轮询到的状态文本
let executingTimer = null             // 轮询定时器
const showJsonInput = ref(false)
const workflowInputParams = ref([])
const executeForm = ref({
  workflowCode: '',
  mode: 'sync',
  inputs: {},
  inputDataJson: ''
})

// 历史记录相关
const historyOpen = ref(false)
const historyLoading = ref(false)
const historyList = ref([])
const historyTotal = ref(0)
const historyWorkflowName = ref('')
const historyFilterStatus = ref('')   // 历史弹窗的状态过滤
const historyQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  workflowCode: '',
  status: ''
})

// 每个工作流的执行统计（键：workflowCode）
const statsMap = ref({})
// 当前历史弹窗的汇总统计
const historyStats = ref({})

// 详情相关
const detailOpen = ref(false)
const executionDetail = ref({})
const detailActiveTab = ref('overview')
const stepExecutionList = ref([])
const stepExecutionLoading = ref(false)
const formattedDetailInput = ref('-')
const formattedDetailOutput = ref('-')

// 收藏相关（当前用户收藏的工作流 ID 集合）

// 模板相关
const templateDialogOpen = ref(false)
const templateList = ref([])
const templateLoading = ref(false)

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentWorkflowIdForSites = ref(0)
const currentWorkflowNameForSites = ref('')
const currentWorkflowCreatorSiteId = ref(0)

// 导出相关
const exportDialogOpen = ref(false)
const exportFormat = ref('excel')
const exportLoading = ref(false)

// 全站导出相关
const fullExportDialogOpen = ref(false)
const fullExportFormat = ref('excel')
const fullExportLoading = ref(false)

// 系统导入相关
const importDialogOpen = ref(false)
const importLoading = ref(false)
const importPreviewData = ref([])
const importFile = ref(null)

// 全站导入相关
const fullImportDialogOpen = ref(false)
const fullImportLoading = ref(false)
const fullImportSites = ref([])
const fullImportWorkflows = ref([])
const fullImportRelations = ref([])
const fullImportTranslations = ref([])
const fullImportFile = ref(null)
const siteMapping = ref({})
const createDefaultAsNewSite = ref(false)
const hasDefaultConfig = ref(false)

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const selectedWorkflowsForBatchExclude = ref([])
const workflowExclusionDetails = ref([])
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const workflowTableRef = ref(null)

// 冲突网站 ID 集合（存在 include 关联的网站，exclude 对其无效）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  workflowExclusionDetails.value.forEach(detail => {
    if (detail.includedSiteIds) {
      detail.includedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

// 批量关联管理相关
const batchRelationDialogOpen = ref(false)
const batchRelatedSiteIds = ref([])
const batchRelationLoading = ref(false)
const selectedWorkflowsForBatchRelation = ref([])
const workflowRelationDetails = ref([])

// 是否处于默认配置的批量关联管理
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId, siteList.value))

// 冲突网站 ID 集合（存在 exclude 关系的网站）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  workflowRelationDetails.value.forEach(detail => {
    if (detail.excludedSiteIds) {
      detail.excludedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: null,
    workflowCode: undefined,
    workflowName: undefined,
    triggerType: undefined,
    enabled: undefined,
    categoryId: undefined,
    queryMode: 'creator',
    includeDefault: false
  },
  rules: {
    workflowCode: [{ required: true, message: '工作流编码不能为空', trigger: 'blur' }],
    workflowName: [{ required: true, message: '工作流名称不能为空', trigger: 'blur' }],
    triggerType: [{ required: true, message: '触发类型不能为空', trigger: 'change' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// JSON 预览
const jsonPreview = computed(() => {
  try {
    const workflowDefinition = {
      workflowCode: form.value.workflowCode,
      workflowName: form.value.workflowName,
      description: form.value.description,
      triggerType: form.value.triggerType,
      inputs: form.value.inputs || [],
      steps: (form.value.steps || []).map(step => {
        const stepData = {
          stepId: step.stepId,
          stepName: step.stepName,
          stepType: step.stepType
        }
        if (step.toolCode) {
          stepData.toolCode = step.toolCode
        }
        if (step.toolParamsText) {
          try {
            stepData.toolParams = JSON.parse(step.toolParamsText)
          } catch (e) {
            stepData.toolParams = step.toolParamsText
          }
        }
        if (step.outputMappingText) {
          try {
            stepData.outputMapping = JSON.parse(step.outputMappingText)
          } catch (e) {
            stepData.outputMapping = step.outputMappingText
          }
        }
        return stepData
      })
    }
    return JSON.stringify(workflowDefinition, null, 2)
  } catch (e) {
    return '生成预览失败: ' + e.message
  }
})

/** 初始化数据 */
function init() {
  // 获取网站列表
  listSite({ pageNum: 1, pageSize: 9999, status: '1' }).then(response => {
    siteList.value = enrichSiteList(response.rows || [])
    
    // 初始化site ID缓存
    siteNameCache.value[0] = '默认配置'
    siteList.value.forEach(site => {
      siteNameCache.value[site.id] = site.name
    })
    
    const restored = restoreViewModeSiteSelection(siteList.value, {
      creatorFallbackSiteId: personalSiteId.value
    })
    viewMode.value = restored.viewMode
    queryParams.value.siteId = restored.siteId
    saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
    
    // 在网站列表加载完成后再加载工作流列表
    getList()
  }).catch(error => {
    console.error('加载网站列表失败:', error)
    loading.value = false
  })
  
  // 获取工作流分类列表
  listCategory({ categoryType: 'workflow' }).then(response => {
    workflowCategoryList.value = response.rows || []
  })

}

/** 获取网站名称 */
function getSiteName(siteId) {
  if (isPersonalSite(siteId, siteList.value) || siteId === null || siteId === undefined) {
    return getSiteDisplayName(siteId, siteList.value)
  }
  return siteNameCache.value[siteId] || `网站${siteId}`
}

/** 获取网站编码 */
function getSiteCode(siteId) {
  if (isPersonalSite(siteId, siteList.value) || siteId === null || siteId === undefined) {
    return 'default'
  }
  const site = siteList.value.find(s => s.id === siteId)
  return site ? site.code : `site_${siteId}`
}

/** 查看模式切换 */
function handleViewModeChange() {
  queryParams.value.queryMode = viewMode.value
  queryParams.value.pageNum = 1

  queryParams.value.siteId = resolveSiteIdByViewMode(viewMode.value, siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  
  getList()
}

/** 网站切换 */
function handleSiteChange() {
  queryParams.value.pageNum = 1
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  getList()
}

/** 查询工作流列表 */
function getList() {
  loading.value = true
  // 更新查询参数
  queryParams.value.queryMode = viewMode.value
  queryParams.value.includeDefault = includeDefaultConfig.value
  
  listWorkflow(queryParams.value).then(response => {
    workflowList.value = response.rows
    total.value = response.total
    loading.value = false
    // 异步加载当前页工作流的执行统计
    loadPageStats(response.rows)
  }).catch(error => {
    console.error('查询工作流失败:', error)
    loading.value = false
  })
}

/** 异步加载当前页所有工作流的执行统计 */
function loadPageStats(rows) {
  if (!rows || rows.length === 0) return
  rows.forEach(row => {
    if (row.workflowCode && !statsMap.value[row.workflowCode]) {
      getExecutionStats(row.workflowCode).then(res => {
        const data = res.data || {}
        statsMap.value = { ...statsMap.value, [row.workflowCode]: data }
      }).catch(() => {})
    }
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    id: null,
    workflowCode: null,
    workflowName: null,
    description: null,
    triggerType: 'manual',
    enabled: 1,
    inputs: [],
    steps: [],
    remark: null
  }
  activeTab.value = 'basic'
  proxy.resetForm('workflowFormRef')
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  workflowEditorRef.value.open(null, { defaultSiteId: queryParams.value.siteId })
}

/** 修改按钮操作 */
function handleUpdate(row) {
  const workflowId = row.id || ids.value
  getWorkflow(workflowId).then(response => {
    workflowEditorRef.value.open(response.data)
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['workflowFormRef'].validate(valid => {
    if (valid) {
      // 构建 definition
      const definition = {
        inputs: form.value.inputs || [],
        steps: (form.value.steps || []).map(step => {
          const stepData = {
            stepId: step.stepId,
            stepName: step.stepName,
            stepType: step.stepType
          }
          if (step.toolCode) {
            stepData.toolCode = step.toolCode
          }
          if (step.toolParamsText) {
            try {
              stepData.toolParams = JSON.parse(step.toolParamsText)
            } catch (e) {
              proxy.$modal.msgError('步骤 ' + step.stepName + ' 的工具参数格式不正确')
              return
            }
          }
          if (step.outputMappingText) {
            try {
              stepData.outputMapping = JSON.parse(step.outputMappingText)
            } catch (e) {
              proxy.$modal.msgError('步骤 ' + step.stepName + ' 的输出映射格式不正确')
              return
            }
          }
          return stepData
        })
      }

      const submitData = {
        ...form.value,
        definition: JSON.stringify(definition),
        stepCount: definition.steps.length
      }

      if (submitData.id != null) {
        updateWorkflow(submitData).then(response => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addWorkflow(submitData).then(response => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const workflowIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除工作流编号为"' + workflowIds + '"的数据项？').then(function () {
    return delWorkflow(workflowIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

/** 状态修改 */
function handleStatusChange(row) {
  // 切换状态（因为使用了 :model-value 而不是 v-model，需要手动切换）
  const newStatus = row.enabled === 1 ? 0 : 1
  const text = newStatus === 1 ? '启用' : '禁用'
  const workflowName = row.workflowName || row.workflow_name || '工作流'
  
  proxy.$modal.confirm('确认要"' + text + '""' + workflowName + '"工作流吗？').then(function () {
    // 先更新本地状态
    const oldStatus = row.enabled
    row.enabled = newStatus
    // 调用接口更新
    return updateWorkflow(row).catch(() => {
      // 失败时恢复原状态
      row.enabled = oldStatus
      throw new Error('更新失败')
    })
  }).then(() => {
    proxy.$modal.msgSuccess(text + '成功')
    // 刷新列表以确保数据同步
    getList()
  }).catch(() => {
    // 用户取消或更新失败，不需要做任何事（状态已在上面处理）
  })
}

/** 添加输入参数 */
function handleAddInput() {
  if (!form.value.inputs) {
    form.value.inputs = []
  }
  form.value.inputs.push({
    name: '',
    type: 'string',
    required: false,
    defaultValue: '',
    description: ''
  })
}

/** 删除输入参数 */
function handleDeleteInput(index) {
  form.value.inputs.splice(index, 1)
}

/** 添加步骤 */
function handleAddStep() {
  if (!form.value.steps) {
    form.value.steps = []
  }
  form.value.steps.push({
    stepId: 'step_' + (form.value.steps.length + 1),
    stepName: '',
    stepType: 'tool',
    toolCode: '',
    toolParamsText: '{}',
    outputMappingText: '{}'
  })
}

/** 删除步骤 */
function handleDeleteStep(index) {
  form.value.steps.splice(index, 1)
}

/** 执行工作流 */
function handleExecute(row) {
  // 解析工作流定义，获取输入参数
  workflowInputParams.value = []
  const defaultInputs = {}
  
  try {
    const definition = typeof row.definition === 'string' 
      ? JSON.parse(row.definition) 
      : row.definition
    
    // 兼容 inputs 和 inputParams 两种字段名
    const params = definition.inputs || definition.inputParams
    
    if (params && Array.isArray(params)) {
      workflowInputParams.value = params
      
      // 根据inputs生成默认值
      params.forEach(param => {
        if (param.default !== undefined && param.default !== null) {
          defaultInputs[param.name] = param.default
        } else if (param.type === 'number') {
          defaultInputs[param.name] = 0
        } else if (param.type === 'boolean') {
          defaultInputs[param.name] = false
        } else {
          defaultInputs[param.name] = ''
        }
      })
    }
  } catch (e) {
    console.error('解析工作流定义失败:', e)
  }

  executeForm.value = {
    workflowCode: row.workflowCode,
    mode: 'sync',
    inputs: { ...defaultInputs },
    inputDataJson: JSON.stringify(defaultInputs, null, 2)
  }
  showJsonInput.value = false
  executeOpen.value = true
}

/** 提交执行
 *  两种模式后端都在后台线程执行，区别仅在于前端行为：
 *  - sync（前台执行）：提交后轮询状态，完成后自动弹出结果详情
 *  - async（后台执行）：提交后立即关闭对话框，由用户自行查看执行历史
 */
function submitExecute() {
  executing.value = true
  executingProgress.value = false
  executingStatus.value = ''
  let inputData

  try {
    if (showJsonInput.value) {
      inputData = JSON.parse(executeForm.value.inputDataJson)
    } else {
      inputData = executeForm.value.inputs
    }
  } catch (e) {
    proxy.$modal.msgError('输入数据格式不正确，请输入有效的JSON')
    executing.value = false
    return
  }

  // 后端请求携带用户选择的模式（sync/async），用于历史记录展示
  const isForeground = executeForm.value.mode === 'sync'
  const params = { mode: executeForm.value.mode, inputData: inputData }

  executeWorkflow(executeForm.value.workflowCode, params).then(response => {
    if (!isForeground) {
      // 后台模式：立即关闭，用户自行查看历史
      executing.value = false
      executeOpen.value = false
      proxy.$modal.msgSuccess('已提交后台执行，请在执行历史中查看结果')
      return
    }

    // 前台模式：轮询直到完成
    const executionId = response.data && response.data.executionId
    if (!executionId) {
      executing.value = false
      executeOpen.value = false
      proxy.$modal.msgSuccess('已提交执行，请在执行历史中查看结果')
      return
    }
    executingProgress.value = true
    executingStatus.value = '正在执行中...'
    let retries = 0
    const maxRetries = 120  // 最多 120 次 × 2s = 4 分钟
    executingTimer = setInterval(() => {
      retries++
      if (retries > maxRetries) {
        clearInterval(executingTimer)
        executing.value = false
        executingProgress.value = false
        executeOpen.value = false
        proxy.$modal.msgWarning('执行超时，请在执行历史中查看结果')
        return
      }
      getExecutionStatus(executionId).then(res => {
        const status = res.data && res.data.status
        if (status === 'running') {
          executingStatus.value = `执行中...（已等待 ${retries * 2}s）`
          return
        }
        clearInterval(executingTimer)
        executing.value = false
        executingProgress.value = false
        executeOpen.value = false
        if (status === 'success') {
          proxy.$modal.msgSuccess('执行成功')
          handleViewDetail(res.data)
        } else {
          proxy.$modal.msgError('执行失败：' + (res.data && res.data.error || '未知错误'))
        }
      }).catch(() => {})
    }, 2000)
  }).catch(() => {
    executing.value = false
    executingProgress.value = false
  })
}

/** 查看执行历史 */
function handleHistory(row) {
  historyQueryParams.value.workflowCode = row.workflowCode
  historyQueryParams.value.pageNum = 1
  historyQueryParams.value.status = ''
  historyFilterStatus.value = ''
  historyWorkflowName.value = row.workflowName || row.workflowCode
  historyStats.value = statsMap.value[row.workflowCode] || {}
  // 加载最新统计
  getExecutionStats(row.workflowCode).then(res => {
    historyStats.value = res.data || {}
    // 同步到 statsMap
    statsMap.value = { ...statsMap.value, [row.workflowCode]: res.data || {} }
  }).catch(() => {})
  getHistoryList()
  historyOpen.value = true
}

/** 获取执行历史列表 */
function getHistoryList() {
  historyLoading.value = true
  historyQueryParams.value.status = historyFilterStatus.value
  listExecutionHistory(historyQueryParams.value).then(response => {
    historyList.value = response.rows
    historyTotal.value = response.total
    historyLoading.value = false
  })
}

/** 查看执行详情 */
function handleViewDetail(row) {
  executionDetail.value = row
  detailActiveTab.value = 'overview'
  stepExecutionList.value = []
  // 预计算概览数据的格式化 JSON，避免模板重渲染时反复执行
  formattedDetailInput.value = formatJson(row.inputData)
  formattedDetailOutput.value = formatJson(row.outputData)
  detailOpen.value = true
  // 异步加载步骤执行记录
  if (row.executionId) {
    stepExecutionLoading.value = true
    listStepExecutions(row.executionId).then(res => {
      // 预计算每个步骤的格式化 JSON，避免折叠展开时重复解析大数据
      stepExecutionList.value = (res.data || []).map(step => ({
        ...step,
        _ioOpen: false,
        _ioReady: false,
        _formattedInput: formatJson(step.inputData),
        _formattedOutput: formatJson(step.outputData)
      }))
    }).catch(() => {}).finally(() => {
      stepExecutionLoading.value = false
    })
  }
}

/** 打开模板选择对话框 */
function handleOpenTemplates() {
  templateList.value = []
  templateLoading.value = true
  templateDialogOpen.value = true
  listWorkflowTemplates({ isPublic: 1 }).then(res => {
    templateList.value = res.data || []
  }).catch(() => {}).finally(() => {
    templateLoading.value = false
  })
}

/** 使用模板创建工作流 */
function handleUseTemplate(tpl) {
  proxy.$modal.confirm(`确认从模板「${tpl.templateName}」创建工作流？`).then(() => {
    createWorkflowFromTemplate(tpl.id, { siteId: queryParams.value.siteId ?? null }).then(res => {
      proxy.$modal.msgSuccess('创建成功')
      templateDialogOpen.value = false
      getList()
    }).catch(() => {})
  }).catch(() => {})
}

/** 切换步骤输入/输出显示（两阶段渲染，先挂载空 DOM，再填充大文本） */
function toggleStepIO(step) {
  if (step._ioOpen) {
    step._ioOpen = false
    step._ioReady = false
    return
  }
  // 第一阶段：先打开容器（textarea 为空，无 layout 开销），让浏览器先绘制展开动作
  step._ioOpen = true
  step._ioReady = false
  // 第二阶段：在浏览器完成绘制后再填入大文本，将 freeze 时机延到用户已看到展开状态之后
  setTimeout(() => { step._ioReady = true }, 30)
}

/** 格式化 JSON 显示（将字符串或对象都处理） */
function formatJson(data) {
  if (data === null || data === undefined) return '-'
  if (typeof data === 'string') {
    try { return JSON.stringify(JSON.parse(data), null, 2) } catch { return data }
  }
  try { return JSON.stringify(data, null, 2) } catch { return String(data) }
}

/** 获取可见性值 */
function getVisibilityValue(row) {
  if (row.relationSource === 'default') {
    // 默认配置：使用 isVisible 判断是否被排除
    return row.isVisible || '1'
  } else if (row.relationSource === 'own') {
    // 自有数据：使用 enabled 状态
    return row.enabled === 1 ? '1' : '0'
  } else if (row.relationSource === 'shared') {
    // 共享数据：使用 isVisible 状态
    return row.isVisible || '1'
  }
  return '0'
}

/** 切换可见性（统一入口）*/
async function handleToggleVisibility(row) {
  const currentQuerySiteId = queryParams.value.siteId
  const currentValue = getVisibilityValue(row)
  const newValue = currentValue === '1' ? '0' : '1'
  
  try {
    if (row.relationSource === 'default') {
      // 默认配置：根据是否有关联记录决定操作方式
      // 首先检查是否有关联记录（include）
      const relationResponse = await getWorkflowSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段
        await updateWorkflowVisibility(currentQuerySiteId, row.id, newValue)
        const action = newValue === '1' ? '显示' : '隐藏'
        proxy.$modal.msgSuccess(`${action}成功`)
        // 重新加载列表以更新统计信息
        getList()
      } else {
        // 没有关联记录：使用排除逻辑
        // 复用已加载的 relations 计算新的排除状态
        const included = relations.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
        let excluded = relations.filter(s => s.relationType === 'exclude').map(s => s.siteId)
        if (newValue === '0') {
          if (!excluded.includes(currentQuerySiteId)) excluded.push(currentQuerySiteId)
          proxy.$modal.msgSuccess('已排除该工作流')
        } else {
          excluded = excluded.filter(id => id !== currentQuerySiteId)
          proxy.$modal.msgSuccess('已恢复该工作流')
        }
        await batchSaveWorkflowSiteRelations({ workflowIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用状态
      const newEnabled = newValue === '1' ? 1 : 0
      const text = newValue === '1' ? '启用' : '禁用'
      await updateWorkflow({ ...row, enabled: newEnabled })
      row.enabled = newEnabled
      // 同步更新 isVisible 以保持显示一致
      row.isVisible = newValue
      proxy.$modal.msgSuccess(`${text}成功`)
      // 刷新列表以确保数据同步
      getList()
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updateWorkflowVisibility(currentQuerySiteId, row.id, newValue)
      const action = newValue === '1' ? '显示' : '隐藏'
      proxy.$modal.msgSuccess(`${action}成功`)
      // 重新加载列表以更新统计信息
      getList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换可见性失败:', error)
      proxy.$modal.msgError('操作失败')
    }
  }
}

/** 排除默认配置 */
function handleExclude(row) {
  const workflowName = row.workflowName || row.workflow_name || '工作流'
  proxy.$modal.confirm('确认要排除默认工作流"' + workflowName + '"吗？排除后该工作流在当前网站将不可见。').then(async () => {
    const siteId = queryParams.value.siteId
    const res = await getWorkflowSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excluded.includes(siteId)) excluded.push(siteId)
    return batchSaveWorkflowSiteRelations({ workflowIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess('排除成功')
    getList()
  }).catch(() => {})
}

/** 恢复默认配置 */
function handleRestore(row) {
  const workflowName = row.workflowName || row.workflow_name || '工作流'
  proxy.$modal.confirm('确认要恢复默认工作流"' + workflowName + '"吗？恢复后该工作流将重新在当前网站显示。').then(async () => {
    const siteId = queryParams.value.siteId
    const res = await getWorkflowSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== siteId)
    return batchSaveWorkflowSiteRelations({ workflowIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess('恢复成功')
    getList()
  }).catch(() => {})
}

/** 移除跨站关联 */
function handleRemoveFromSite(row) {
  const workflowName = row.workflowName || row.workflow_name || '工作流'
  const actionLabel = row.relationType === 'exclude' ? '取消排除' : '移除关联'
  const actionMessage = row.relationType === 'exclude' 
    ? '确认要取消排除工作流"' + workflowName + '"吗？取消后该工作流将对当前网站可见。'
    : '确认要移除工作流"' + workflowName + '"的关联吗？'
  
  proxy.$modal.confirm(actionMessage).then(async () => {
    const siteId = queryParams.value.siteId
    const res = await getWorkflowSites(row.id)
    const sites = res.data || []
    let included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    let excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (row.relationType === 'exclude') {
      excluded = excluded.filter(id => id !== siteId)
    } else {
      included = included.filter(id => id !== siteId)
    }
    return batchSaveWorkflowSiteRelations({ workflowIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess(actionLabel + '成功')
    getList()
  }).catch(() => {})
}

/** 快速切换可见性（用于跨站共享） */
function handleQuickToggleVisibility(row) {
  const newVisible = row.isVisible === '1' ? '0' : '1'
  const text = newVisible === '1' ? '显示' : '隐藏'
  
  updateWorkflowVisibility(queryParams.value.siteId, row.id, newVisible).then(() => {
    proxy.$modal.msgSuccess(text + '成功')
    row.isVisible = newVisible
  }).catch(() => {
    // 恢复原状态
  })
}

/** 管理关联网站 */
function handleManageSites(row) {
  currentWorkflowIdForSites.value = row.id
  currentWorkflowNameForSites.value = row.workflowName || row.workflow_name || '工作流'
  currentWorkflowCreatorSiteId.value = row.siteId || 0
  siteRelationDialogOpen.value = true
}

/** 管理排除网站（默认配置）- 复用网站关联管理对话框 */
function handleManageExclusions(row) {
  handleManageSites(row)
}

/** 导出工作流 */
function handleExport() {
  const selectedRows = workflowTableRef.value.getSelectionRows()
  if (!selectedRows || selectedRows.length === 0) {
    proxy.$modal.msgWarning('请至少选择一个工作流')
    return
  }
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgError('请选择要导出的工作流')
    return
  }
  
  exportLoading.value = true
  exportDialogOpen.value = false
  
  try {
    // 获取选中的工作流详细数据
    const workflowPromises = selectedIds.map(id => getWorkflow(id))
    const workflowResponses = await Promise.all(workflowPromises)
    const workflowData = workflowResponses.map(response => response.data)
    
    // 转换数据格式
    const formattedData = workflowData.map(workflow => ({
      工作流编码: workflow.workflowCode,
      工作流名称: workflow.workflowName,
      网站编码: getSiteCode(workflow.siteId),
      分类名称: workflow.categoryName || '',
      触发类型: workflow.triggerType === 'manual' ? '手动触发' : workflow.triggerType === 'scheduled' ? '定时触发' : '事件触发',
      步骤数: workflow.stepCount || 0,
      状态: workflow.enabled === 1 ? '启用' : '禁用',
      描述: workflow.description || '',
      定义: workflow.definition || ''
    }))
    
    if (exportFormat.value === 'excel') {
      await exportToExcel(formattedData, '工作流数据')
    } else {
      exportToJSON(formattedData, '工作流数据')
    }
    
    proxy.$modal.msgSuccess('数据导出成功')
    
  } catch (error) {
    console.error('导出数据失败:', error)
    proxy.$modal.msgError('导出数据失败: ' + (error.message || '未知错误'))
  } finally {
    exportLoading.value = false
  }
}

/** 全站导出 */
function handleFullExport() {
  fullExportDialogOpen.value = true
}

/** 确认全站导出 */
async function confirmFullExport() {
  try {
    fullExportLoading.value = true
    fullExportDialogOpen.value = false
    
    // 1. 获取所有工作流数据
    const workflowsResponse = await listWorkflow({
      queryMode: 'creator',
      pageNum: 1,
      pageSize: 9999
    })
    const workflowData = workflowsResponse.rows || []
    
    // 2. 收集所有涉及的网站ID
    const siteIds = new Set()
    workflowData.forEach(workflow => {
      if (workflow.siteId !== null && workflow.siteId !== undefined) {
        siteIds.add(workflow.siteId)
      }
    })
    
    // 3. 获取所有关联关系（包括普通关联和默认配置排除），同时收集网站ID
    const relationDataRaw = [] // 临时存储，稍后转换虚拟ID
    for (let index = 0; index < workflowData.length; index++) {
      const workflow = workflowData[index]
      const virtualId = index + 1 // 虚拟ID从1开始
      
      if (workflow.siteId && !isPersonalSite(workflow.siteId, siteList.value)) {
        // 非默认配置：获取所有关联关系
        try {
          const response = await getWorkflowSites(workflow.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId) // 收集网站ID
              
              // 根据 relationType 区分关联类型
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              
              const relationData = {
                工作流虚拟ID: virtualId,
                工作流编码: workflow.workflowCode,
                关联类型: relationType,
                网站ID_原始: rel.siteId, // 暂存原始ID
                网站编码: getSiteCode(rel.siteId)
              }
              
              // 只有关联类型才有可见性字段
              if (rel.relationType !== 'exclude') {
                relationData.可见性 = rel.isVisible === '1' ? '显示' : '隐藏'
              }
              
              relationDataRaw.push(relationData)
            })
          }
        } catch (error) {
          console.warn('获取工作流关联失败:', workflow.id, error)
        }
      } else {
        // 默认配置：获取所有网站关系（包括关联和排除）
        try {
          const response = await getWorkflowSites(workflow.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId) // 收集网站ID
              
              // 根据 relationType 区分关联类型
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              
              const relationData = {
                工作流虚拟ID: virtualId,
                工作流编码: workflow.workflowCode,
                关联类型: relationType,
                网站ID_原始: rel.siteId, // 暂存原始ID
                网站编码: getSiteCode(rel.siteId)
              }
              
              // 只有关联类型才有可见性字段
              if (rel.relationType !== 'exclude') {
                relationData.可见性 = rel.isVisible === '1' ? '显示' : '隐藏'
              }
              
              relationDataRaw.push(relationData)
            })
          }
        } catch (error) {
          console.warn('获取默认配置关系失败:', workflow.id, error)
        }
      }
    }
    
    // 4. 获取网站详细信息并建立ID映射
    const sitesData = []
    const siteIdToVirtualIdMap = new Map()
    let realSiteIndex = 0
    
    // 默认配置的虚拟ID固定为0
    siteIdToVirtualIdMap.set(0, 0)
    
    for (const siteId of siteIds) {
      if (isPersonalSite(siteId, siteList.value)) {
        sitesData.push({
          网站虚拟ID: 0,
          网站名称: '默认配置',
          网站编码: 'default',
          网站域名: '',
          状态: '启用'
        })
      } else {
        const site = siteList.value.find(s => s.id === siteId)
        if (site) {
          realSiteIndex++
          siteIdToVirtualIdMap.set(siteId, realSiteIndex)
          sitesData.push({
            网站虚拟ID: realSiteIndex,
            网站名称: site.name,
            网站编码: site.code,
            网站域名: site.domain || '',
            状态: site.status === '1' ? '启用' : '禁用'
          })
        }
      }
    }
    
    // 5. 转换关联关系，使用网站虚拟ID
    const relationData = relationDataRaw.map(rel => ({
      工作流虚拟ID: rel.工作流虚拟ID,
      工作流编码: rel.工作流编码,
      关联类型: rel.关联类型,
      网站虚拟ID: siteIdToVirtualIdMap.get(rel.网站ID_原始) ?? 0,
      网站编码: rel.网站编码,
      可见性: rel.可见性
    }))
    
    // 6. 转换工作流数据格式
    const formattedWorkflowData = workflowData.map((workflow, index) => ({
      工作流虚拟ID: index + 1,
      工作流编码: workflow.workflowCode,
      工作流名称: workflow.workflowName,
      网站虚拟ID: siteIdToVirtualIdMap.get(workflow.siteId) ?? 0,
      网站编码: getSiteCode(workflow.siteId),
      分类名称: workflow.categoryName || '',
      触发类型: workflow.triggerType === 'manual' ? '手动触发' : workflow.triggerType === 'scheduled' ? '定时触发' : '事件触发',
      步骤数: workflow.stepCount || 0,
      状态: workflow.enabled === 1 ? '启用' : '禁用',
      描述: workflow.description || '',
      定义: workflow.definition || ''
    }))
    
    // 7. 根据格式导出
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, formattedWorkflowData, relationData)
    } else {
      exportFullDataToJSON(sitesData, formattedWorkflowData, relationData)
    }
    
    proxy.$modal.msgSuccess('全站数据导出成功')
    
  } catch (error) {
    console.error('全站导出失败:', error)
    proxy.$modal.msgError('全站导出失败: ' + (error.message || '未知错误'))
  } finally {
    fullExportLoading.value = false
  }
}

/** 系统导入 */
function handleSystemImport() {
  importDialogOpen.value = true
  importPreviewData.value = []
  importFile.value = null
}

/** 处理系统导入文件选择 */
async function handleSystemImportFileChange(file) {
  importFile.value = file
  
  try {
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.raw.type.includes('json')) {
      parsedData = parseJSONDataForImport(fileData)
    } else {
      parsedData = await parseExcelDataForImport(fileData)
    }
    
    importPreviewData.value = parsedData.workflows || []
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + (error.message || '未知错误'))
    handleSystemImportFileRemove()
  }
}

/** 解析Excel数据（普通导入） */
async function parseExcelDataForImport(arrayBuffer) {
  const XLSX = await import('xlsx')
  const workbook = XLSX.read(arrayBuffer, { type: 'array' })
  
  const result = { workflows: [] }
  
  // 解析工作流数据
  const workflowSheetName = workbook.SheetNames.find(name => 
    name === '工作流数据' || name === 'workflows'
  ) || workbook.SheetNames[0]
  
  if (workflowSheetName && workbook.Sheets[workflowSheetName]) {
    const rawData = XLSX.utils.sheet_to_json(workbook.Sheets[workflowSheetName])
    result.workflows = validateAndTransformImportData(rawData)
  }
  
  return result
}

/** 解析JSON数据（普通导入） */
function parseJSONDataForImport(jsonString) {
  try {
    const data = JSON.parse(jsonString)
    const rawWorkflows = data.workflows || (Array.isArray(data) ? data : [])
    return {
      workflows: validateAndTransformImportData(rawWorkflows)
    }
  } catch (error) {
    throw new Error('JSON格式错误')
  }
}

/** 验证和转换导入数据 */
function validateAndTransformImportData(rawData) {
  if (!Array.isArray(rawData) || rawData.length === 0) {
    return []
  }
  
  return rawData.map((item, index) => {
    // 获取网站ID
    let siteId = personalSiteId.value // 默认为默认配置
    if (item['网站编码'] && item['网站编码'] !== 'default') {
      const site = siteList.value.find(s => s.code === item['网站编码'])
      if (site) {
        siteId = site.id
      }
    }
    
    // 处理分类ID
    let categoryId = null
    if (item['分类名称']) {
      const category = workflowCategoryList.value.find(cat => 
        cat.name === item['分类名称'] && 
        (cat.siteId === siteId || isPersonalSite(cat.siteId, siteList.value))
      )
      if (category) {
        categoryId = category.id
      }
    }
    
    // 处理触发类型
    let triggerType = 'manual'
    if (item['触发类型'] === '手动触发') {
      triggerType = 'manual'
    } else if (item['触发类型'] === '定时触发') {
      triggerType = 'scheduled'
    } else if (item['触发类型'] === '事件触发') {
      triggerType = 'event'
    }
    
    // 处理状态
    let enabled = 1
    if (item['状态'] === '禁用' || item['状态'] === '0') {
      enabled = 0
    }
    
    const transformedItem = {
      workflowCode: item['工作流编码'] || `import_${Date.now()}_${Math.floor(Math.random() * 10000)}_${index}`,
      workflowName: item['工作流名称'] || `导入工作流${index + 1}`,
      siteId: siteId,
      categoryId: categoryId,
      description: item['描述'] || '',
      triggerType: triggerType,
      definition: item['定义'] || '',
      stepCount: parseInt(item['步骤数']) || 0,
      enabled: enabled,
      // 显示用字段
      siteCode: getSiteCode(siteId)
    }
    
    return transformedItem
  })
}

/** 处理系统导入文件移除 */
function handleSystemImportFileRemove() {
  importFile.value = null
  importPreviewData.value = []
}

/** 确认系统导入 */
async function confirmSystemImport() {
  if (importPreviewData.value.length === 0) {
    proxy.$modal.msgError('没有可导入的数据')
    return
  }
  
  // 检查导入数据中是否有重复的workflowCode
  const codeMap = new Map()
  const duplicateCodes = []
  
  for (const item of importPreviewData.value) {
    const workflowCode = item.workflowCode
    if (!workflowCode) continue
    
    if (codeMap.has(workflowCode)) {
      if (!duplicateCodes.includes(workflowCode)) {
        duplicateCodes.push(workflowCode)
      }
    } else {
      codeMap.set(workflowCode, true)
    }
  }
  
  // 如果发现重复编码，阻止导入
  if (duplicateCodes.length > 0) {
    const codeList = duplicateCodes.join('、')
    proxy.$modal.msgError(`导入数据中存在重复的工作流编码，请修正后重新导入：${codeList}`)
    return
  }
  
  // 检查数据库中是否已存在这些编码
  const existingCodes = []
  const allCodes = importPreviewData.value.map(item => item.workflowCode).filter(code => code)
  
  for (const code of allCodes) {
    try {
      const response = await getWorkflowByCode(code)
      if (response && response.code === 200 && response.data) {
        existingCodes.push(code)
      }
    } catch (error) {
      // 如果返回404或其他错误，说明不存在
      // 继续检查下一个
    }
  }
  
  // 如果有已存在的编码，阻止导入
  if (existingCodes.length > 0) {
    const codeList = existingCodes.join('、')
    proxy.$modal.msgError(`以下工作流编码已存在于数据库中，请修改后重新导入：${codeList}`)
    return
  }
  
  try {
    importLoading.value = true
    
    // 批量导入工作流
    for (const workflowData of importPreviewData.value) {
      const { siteCode, ...cleanData } = workflowData
      await addWorkflow(cleanData)
    }
    
    proxy.$modal.msgSuccess(`成功导入 ${importPreviewData.value.length} 条工作流`)
    importDialogOpen.value = false
    importPreviewData.value = []
    importFile.value = null
    getList()
    
  } catch (error) {
    console.error('导入失败:', error)
    proxy.$modal.msgError('导入失败: ' + (error.message || '未知错误'))
  } finally {
    importLoading.value = false
  }
}

/** 全站导入 */
function handleFullImport() {
  fullImportDialogOpen.value = true
  fullImportSites.value = []
  fullImportWorkflows.value = []
  fullImportRelations.value = []
  fullImportFile.value = null
  siteMapping.value = {}
  hasDefaultConfig.value = false
}

/** 读取文件数据 */
function readFileData(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = e => resolve(e.target.result)
    reader.onerror = () => reject(new Error('文件读取失败'))
    
    if (file.type.includes('json')) {
      reader.readAsText(file)
    } else {
      reader.readAsArrayBuffer(file)
    }
  })
}

/** 解析全站导入Excel数据 */
async function parseFullImportExcelData(fileData) {
  const XLSX = await import('xlsx')
  const workbook = XLSX.read(fileData, { type: 'array' })
  
  const result = {
    sites: [],
    workflows: [],
    relations: []
  }
  
  // 解析网站列表
  if (workbook.SheetNames.includes('网站列表')) {
    const siteSheet = workbook.Sheets['网站列表']
    result.sites = XLSX.utils.sheet_to_json(siteSheet)
  }
  
  // 解析工作流数据
  if (workbook.SheetNames.includes('工作流数据')) {
    const workflowSheet = workbook.Sheets['工作流数据']
    result.workflows = XLSX.utils.sheet_to_json(workflowSheet)
  }
  
  // 解析关联关系
  if (workbook.SheetNames.includes('网站关联')) {
    const relationSheet = workbook.Sheets['网站关联']
    result.relations = XLSX.utils.sheet_to_json(relationSheet)
  }
  
  return result
}

/** 解析全站导入JSON数据 */
function parseFullImportJSONData(fileData) {
  const jsonData = JSON.parse(fileData)
  
  return {
    sites: jsonData.sites || [],
    workflows: jsonData.workflows || [],
    relations: jsonData.relations || []
  }
}

/** 初始化网站映射 */
function initializeSiteMapping() {
  siteMapping.value = {}
  
  // 为默认配置创建映射（如果存在）
  if (hasDefaultConfig.value) {
    siteMapping.value[0] = 0 // 默认映射到默认配置
  }
  
  // 为其他网站创建映射
  fullImportSites.value.forEach(site => {
    const siteVirtualId = site['网站虚拟ID']
    if (siteVirtualId > 0) {
      // 尝试自动匹配网站编码
      const siteCode = site['网站编码']
      const matchedSite = siteList.value.find(s => s.code === siteCode)
      if (matchedSite) {
        siteMapping.value[siteVirtualId] = matchedSite.id
      }
    }
  })
}

/** 处理全站导入文件选择 */
async function handleFullImportFileChange(file) {
  fullImportFile.value = file
  
  try {
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.raw.type.includes('json')) {
      parsedData = parseFullImportJSONData(fileData)
    } else {
      parsedData = await parseFullImportExcelData(fileData)
    }
    
    fullImportSites.value = parsedData.sites || []
    fullImportWorkflows.value = parsedData.workflows || []
    fullImportRelations.value = parsedData.relations || []
    
    // 检查是否包含默认配置
    hasDefaultConfig.value = fullImportWorkflows.value.some(w => w['网站虚拟ID'] === 0 || w['网站编码'] === 'default')
    
    // 初始化网站映射
    initializeSiteMapping()
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + (error.message || '未知错误'))
    handleFullImportFileRemove()
  }
}

/** 处理全站导入文件移除 */
function handleFullImportFileRemove() {
  fullImportFile.value = null
  fullImportSites.value = []
  fullImportWorkflows.value = []
  fullImportRelations.value = []
  siteMapping.value = {}
  hasDefaultConfig.value = false
}

/** 验证全站导入映射是否完整 */
const isFullImportMappingValid = computed(() => {
  // 检查是否有需要映射的网站
  const realSites = fullImportSites.value.filter(s => s['网站虚拟ID'] > 0)
  
  // 如果有默认配置且勾选了转换为新网站配置，需要映射到目标网站
  if (hasDefaultConfig.value && createDefaultAsNewSite.value) {
    if (!siteMapping.value[0]) {
      return false
    }
  }
  
  // 检查所有真实网站是否都已映射
  for (const site of realSites) {
    const siteVirtualId = site['网站虚拟ID']
    if (!siteMapping.value[siteVirtualId]) {
      return false
    }
  }
  
  return true
})

/** 确认全站导入 */
async function confirmFullImport() {
  if (!isFullImportMappingValid.value) {
    proxy.$modal.msgError('请完成所有网站映射配置')
    return
  }
  
  try {
    fullImportLoading.value = true
    
    await processFullImport()
    
    proxy.$modal.msgSuccess('全站数据导入成功')
    fullImportDialogOpen.value = false
    handleFullImportFileRemove()
    getList()
    
  } catch (error) {
    console.error('全站导入失败:', error)
    proxy.$modal.msgError('全站导入失败: ' + (error.message || '未知错误'))
  } finally {
    fullImportLoading.value = false
  }
}

/** 处理全站导入 */
async function processFullImport() {
  const virtualIdToNewIdMap = {} // 虚拟ID到新ID的映射
  
  // 初始化每个目标网站的虚拟ID映射
  Object.values(siteMapping.value).forEach(targetSiteId => {
    virtualIdToNewIdMap[targetSiteId] = {}
  })
  
  // 0. 检查导入数据中是否有重复的workflowCode
  const codeMap = new Map()
  const duplicateCodes = []
  
  for (const workflow of fullImportWorkflows.value) {
    const workflowCode = workflow['工作流编码']
    if (!workflowCode) continue
    
    if (codeMap.has(workflowCode)) {
      if (!duplicateCodes.includes(workflowCode)) {
        duplicateCodes.push(workflowCode)
      }
    } else {
      codeMap.set(workflowCode, true)
    }
  }
  
  // 如果发现重复编码，阻止导入
  if (duplicateCodes.length > 0) {
    const codeList = duplicateCodes.join('、')
    throw new Error(`导入数据中存在重复的工作流编码，请修正后重新导入：${codeList}`)
  }
  
  // 0.5 检查数据库中是否已存在这些编码
  const existingCodes = []
  const allCodes = fullImportWorkflows.value.map(w => w['工作流编码']).filter(code => code)
  
  for (const code of allCodes) {
    try {
      const response = await getWorkflowByCode(code)
      if (response && response.code === 200 && response.data) {
        existingCodes.push(code)
      }
    } catch (error) {
      // 如果返回404或其他错误，说明不存在，继续
      console.log(`编码 ${code} 不存在`)
    }
  }
  
  // 如果有已存在的编码，阻止导入
  if (existingCodes.length > 0) {
    const codeList = existingCodes.join('、')
    throw new Error(`以下工作流编码已存在于数据库中，请修改后重新导入：${codeList}`)
  }
  
  // 1. 处理工作流数据导入（按网站）
  const workflowsBySite = {}
  
  fullImportWorkflows.value.forEach(workflow => {
    const siteVirtualId = workflow['网站虚拟ID'] || 0
    const virtualId = workflow['工作流虚拟ID']
    const targetSiteId = siteMapping.value[siteVirtualId]
    
    if (targetSiteId !== undefined && virtualId) {
      if (!workflowsBySite[targetSiteId]) {
        workflowsBySite[targetSiteId] = []
      }
      workflowsBySite[targetSiteId].push({
        ...workflow,
        targetSiteId,
        virtualId
      })
    }
  })
  
  // 2. 按目标网站分别导入工作流
  for (const [targetSiteId, workflows] of Object.entries(workflowsBySite)) {
    await importWorkflowsForSite(targetSiteId, workflows, virtualIdToNewIdMap[targetSiteId] || {})
  }
  
  // 3. 处理关联关系
  if (fullImportRelations.value.length > 0) {
    await importRelations(virtualIdToNewIdMap)
  }
}

/** 为指定网站导入工作流 */
async function importWorkflowsForSite(targetSiteId, workflows, virtualIdMap) {
  console.log(`开始为网站 ${targetSiteId} 导入工作流...`)
  
  if (!workflows || workflows.length === 0) {
    console.log('没有工作流需要导入')
    return
  }
  
  for (const workflowItem of workflows) {
    try {
      const virtualId = workflowItem.virtualId
      
      // 转换工作流数据
      const timestamp = Date.now()
      const random = Math.floor(Math.random() * 10000)
      const workflowData = {
        workflowCode: workflowItem['工作流编码'] || `import_${timestamp}_${random}_${virtualId}`,
        workflowName: workflowItem['工作流名称'] || `导入工作流${virtualId}`,
        siteId: parseInt(targetSiteId),
        description: workflowItem['描述'] || '',
        triggerType: workflowItem['触发类型'] === '手动触发' ? 'manual' : workflowItem['触发类型'] === '定时触发' ? 'scheduled' : 'event',
        definition: workflowItem['定义'] || '',
        stepCount: parseInt(workflowItem['步骤数']) || 0,
        enabled: workflowItem['状态'] === '启用' ? 1 : 0
      }
      
      // 处理分类
      if (workflowItem['分类名称']) {
        const category = workflowCategoryList.value.find(cat => 
          cat.name === workflowItem['分类名称'] && 
          (cat.siteId === parseInt(targetSiteId) || isPersonalSite(cat.siteId, siteList.value))
        )
        if (category) {
          workflowData.categoryId = category.id
        }
      }
      
      // 导入工作流
      const response = await addWorkflow(workflowData)
      const newWorkflowId = response.data
      
      // 记录虚拟ID到新ID的映射
      if (virtualId) {
        virtualIdMap[virtualId] = newWorkflowId
      }
      
      console.log(`工作流导入成功: ${workflowData.workflowName} (虚拟ID: ${virtualId}) -> ID: ${newWorkflowId}`)
    } catch (error) {
      console.error(`工作流导入失败: ${workflowItem['工作流名称']}`, error)
      throw new Error(`工作流 "${workflowItem['工作流名称']}" 导入失败: ${error.message}`)
    }
  }
  
  console.log(`网站 ${targetSiteId} 工作流导入完成`)
}

/** 导入关联关系 */
async function importRelations(virtualIdToNewIdMap) {
  console.log(`开始导入 ${fullImportRelations.value.length} 条关联关系...`)
  
  for (const relation of fullImportRelations.value) {
    try {
      const virtualId = relation['工作流虚拟ID']
      const relationTypeFromData = relation['关联类型'] || '关联'
      const siteVirtualId = relation['网站虚拟ID'] || 0
      
      // 从工作流数据中获取创建者网站虚拟ID
      const workflowConfig = fullImportWorkflows.value.find(w => 
        w['工作流虚拟ID'] === virtualId
      )
      const creatorSiteVirtualId = workflowConfig ? (workflowConfig['网站虚拟ID'] || 0) : 0
      
      // 如果默认配置被转换为新网站配置，跳过默认配置的关联
      if (hasDefaultConfig.value && createDefaultAsNewSite.value && creatorSiteVirtualId === 0) {
        console.log(`跳过默认配置关联关系: 虚拟ID=${virtualId}, 目标网站虚拟ID=${siteVirtualId}`)
        continue
      }
      
      // 获取目标网站ID
      const targetSiteId = siteMapping.value[siteVirtualId]
      const creatorTargetSiteId = siteMapping.value[creatorSiteVirtualId]
      
      // 获取新的工作流ID
      const newWorkflowId = virtualIdToNewIdMap[creatorTargetSiteId]?.[virtualId]
      
      if (!newWorkflowId || !targetSiteId) {
        console.warn(`跳过关联关系: 工作流虚拟ID=${virtualId}, 网站虚拟ID=${siteVirtualId} - 未找到对应的映射`)
        continue
      }
      
      // 跳过自身关联
      if (creatorTargetSiteId === targetSiteId) {
        console.log(`跳过自身关联: 工作流ID=${newWorkflowId}, 网站ID=${targetSiteId}`)
        continue
      }
      
      // 创建关联关系
      const relationType = relationTypeFromData === '排除' ? 'exclude' : 'include'
      
      if (relationType === 'include') {
        await batchSaveWorkflowSiteRelations({ workflowIds: [newWorkflowId], includeSiteIds: [targetSiteId] })
      } else {
        await batchSaveWorkflowSiteRelations({ workflowIds: [newWorkflowId], excludeSiteIds: [targetSiteId] })
      }
      
      console.log(`关联关系导入成功: 工作流ID=${newWorkflowId}, 网站ID=${targetSiteId}, 类型=${relationType}`)
    } catch (error) {
      console.error(`关联关系导入失败:`, relation, error)
      // 继续处理其他关联关系
    }
  }
  
  console.log('关联关系导入完成')
}

/** 导出为Excel */
async function exportToExcel(workflowData, sheetName = '工作流数据') {
  const XLSX = await import('xlsx')
  
  const wb = XLSX.utils.book_new()
  
  // 创建工作流数据工作表
  const ws = XLSX.utils.json_to_sheet(workflowData)
  const colWidths = [
    { wch: 20 }, // 工作流编码
    { wch: 20 }, // 工作流名称
    { wch: 15 }, // 网站编码
    { wch: 15 }, // 分类名称
    { wch: 12 }, // 触发类型
    { wch: 10 }, // 步骤数
    { wch: 8 },  // 状态
    { wch: 30 }, // 描述
    { wch: 50 }  // 定义
  ]
  ws['!cols'] = colWidths
  XLSX.utils.book_append_sheet(wb, ws, sheetName)
  
  const fileName = `${sheetName}_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出为JSON */
function exportToJSON(workflowData, dataType = '工作流数据') {
  const exportData = {
    workflows: workflowData,
    exportTime: new Date().toISOString()
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `${dataType}_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.display = 'none'
  
  document.body.appendChild(link)
  link.click()
  
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/** 导出全站数据为Excel */
async function exportFullDataToExcel(sitesData, workflowData, relationData) {
  const XLSX = await import('xlsx')
  
  const wb = XLSX.utils.book_new()
  
  // 网站列表sheet（第一个sheet）
  if (sitesData.length > 0) {
    const sitesWs = XLSX.utils.json_to_sheet(sitesData)
    XLSX.utils.book_append_sheet(wb, sitesWs, "网站列表")
  }
  
  // 工作流数据sheet
  const workflowWs = XLSX.utils.json_to_sheet(workflowData)
  XLSX.utils.book_append_sheet(wb, workflowWs, "工作流数据")
  
  // 关联关系sheet
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    XLSX.utils.book_append_sheet(wb, relationWs, "网站关联")
  }
  
  const fileName = `工作流全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出全站数据为JSON */
function exportFullDataToJSON(sitesData, workflowData, relationData) {
  const exportData = {
    sites: sitesData,
    workflows: workflowData,
    relations: relationData,
    exportTime: new Date().toISOString()
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `工作流全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.display = 'none'
  
  document.body.appendChild(link)
  link.click()
  
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/** 批量排除管理 */
async function handleBatchExclude() {
  const selectedRows = workflowTableRef.value.getSelectionRows()
  if (!selectedRows || selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的工作流')
    return
  }
  
  // 只允许默认配置的工作流进行批量排除
  const invalidWorkflows = selectedRows.filter(workflow => !isPersonalSite(workflow.siteId, siteList.value))
  if (invalidWorkflows.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的工作流进行批量排除管理')
    return
  }
  
  selectedWorkflowsForBatchExclude.value = selectedRows.map(row => ({
    id: row.id,
    workflowName: row.workflowName || row.workflow_name,
    siteId: row.siteId
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中工作流的网站关系
    const batchRes = await getBatchWorkflowSites(selectedWorkflowsForBatchExclude.value.map(w => w.id))
    const batchMap = batchRes.data || {}
    const results = selectedWorkflowsForBatchExclude.value.map(workflow => {
      const sites = batchMap[workflow.id] || []
      return {
        workflowId: workflow.id,
        workflowName: workflow.workflowName,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    workflowExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有工作流共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedWorkflowsForBatchExclude.value.length) {
        commonExcludedSites.push(siteId)
      }
    })
    
    batchExcludedSiteIds.value = commonExcludedSites
    batchExclusionDialogOpen.value = true
  } catch (error) {
    console.error('加载排除状态失败:', error)
    proxy.$modal.msgError('加载排除状态失败')
  }
}

/** 从批量排除中移除工作流 */
function removeWorkflowFromBatchExclude(workflowId) {
  selectedWorkflowsForBatchExclude.value = selectedWorkflowsForBatchExclude.value.filter(
    workflow => workflow.id !== workflowId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== workflowId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (workflowTableRef.value) {
    const row = workflowList.value.find(workflow => workflow.id === workflowId)
    if (row) {
      workflowTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  workflowExclusionDetails.value = workflowExclusionDetails.value.filter(
    detail => detail.workflowId !== workflowId
  )
  
  // 如果没有工作流了，关闭对话框
  if (selectedWorkflowsForBatchExclude.value.length === 0) {
    batchExclusionDialogOpen.value = false
  }
}

/** 全选网站 */
function selectAllSitesForBatchExclude() {
  batchExcludedSiteIds.value = siteList.value.filter(s => s.isPersonal !== 1).map(s => s.id)
}

/** 取消全选 */
function deselectAllSitesForBatchExclude() {
  batchExcludedSiteIds.value = []
}

/** 反选 */
function invertSiteSelectionForBatchExclude() {
  const allSiteIds = siteList.value.filter(s => s.isPersonal !== 1).map(s => s.id)
  const currentSelected = new Set(batchExcludedSiteIds.value)
  batchExcludedSiteIds.value = allSiteIds.filter(id => !currentSelected.has(id))
}

/** 提交批量排除 */
async function submitBatchExclusions() {
  if (selectedWorkflowsForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('请至少选择一个工作流')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一条请求处理所有选中工作流的排除关系
    await batchSaveWorkflowSiteRelations({
      workflowIds: selectedWorkflowsForBatchExclude.value.map(w => w.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedWorkflowsForBatchExclude.value.length} 个工作流排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedWorkflowsForBatchExclude.value.length} 个工作流的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchWorkflowSites(selectedWorkflowsForBatchExclude.value.map(w => w.id))
    const refreshMap = refreshRes.data || {}
    workflowExclusionDetails.value = selectedWorkflowsForBatchExclude.value.map(workflow => {
      const sites = refreshMap[workflow.id] || []
      return {
        workflowId: workflow.id,
        workflowName: workflow.workflowName,
        excludedSiteIds: sites.filter(s => s.relationType === 'exclude').map(s => s.siteId),
        includedSiteIds: sites.filter(s => s.relationType === 'include').map(s => s.siteId)
      }
    })
  } catch (error) {
    console.error('批量排除失败:', error)
    proxy.$modal.msgError('批量排除失败')
  } finally {
    batchExclusionLoading.value = false
  }
}

// ============ 批量关联管理 ============

/** 批量关联管理 */
async function handleBatchRelation() {
  const selectedRows = workflowTableRef.value.getSelectionRows()
  if (!selectedRows || selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的工作流')
    return
  }
  
  const isDefaultConfig = isPersonalSite(queryParams.value.siteId, siteList.value)

  selectedWorkflowsForBatchRelation.value = selectedRows.map(row => ({
    id: row.id,
    workflowName: row.workflowName || row.workflow_name,
    siteId: row.siteId
  }))
  
  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchWorkflowSites(selectedWorkflowsForBatchRelation.value.map(w => w.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedWorkflowsForBatchRelation.value.map(workflow => {
      const sites = batchMap2[workflow.id] || []
      return {
        workflowId: workflow.id,
        workflowName: workflow.workflowName,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== workflow.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    workflowRelationDetails.value = results
    
    // 找出被所有工作流共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedWorkflowsForBatchRelation.value.length) {
        commonRelatedSites.push(siteId)
      }
    })
    
    batchRelatedSiteIds.value = commonRelatedSites
    batchRelationDialogOpen.value = true
  } catch (error) {
    console.error('加载关联状态失败:', error)
    proxy.$modal.msgError('加载关联状态失败')
  }
}

/** 从批量关联中移除某个工作流 */
function removeWorkflowFromBatchRelation(workflowId) {
  selectedWorkflowsForBatchRelation.value = selectedWorkflowsForBatchRelation.value.filter(
    workflow => workflow.id !== workflowId
  )
  
  ids.value = ids.value.filter(id => id !== workflowId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  if (workflowTableRef.value) {
    const row = workflowList.value.find(workflow => workflow.id === workflowId)
    if (row) {
      workflowTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  workflowRelationDetails.value = workflowRelationDetails.value.filter(
    detail => detail.workflowId !== workflowId
  )
  
  if (selectedWorkflowsForBatchRelation.value.length === 0) {
    batchRelationDialogOpen.value = false
  }
}

/** 全选网站（批量关联） */
function selectAllSitesForBatchRelation() {
  batchRelatedSiteIds.value = siteList.value
    .filter(s => s.id !== queryParams.value.siteId && s.isPersonal !== 1)
    .map(site => site.id)
}

/** 取消全选网站（批量关联） */
function deselectAllSitesForBatchRelation() {
  batchRelatedSiteIds.value = []
}

/** 反选网站（批量关联） */
function invertSiteSelectionForBatchRelation() {
  const allSiteIds = siteList.value
    .filter(s => s.id !== queryParams.value.siteId && s.isPersonal !== 1)
    .map(site => site.id)
  const currentSelected = new Set(batchRelatedSiteIds.value)
  batchRelatedSiteIds.value = allSiteIds.filter(id => !currentSelected.has(id))
}

/** 提交批量关联配置 */
async function submitBatchRelations() {
  if (selectedWorkflowsForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何工作流')
    return
  }
  
  batchRelationLoading.value = true
  
  try {
    // 一条请求处理所有选中工作流的关联关系
    await batchSaveWorkflowSiteRelations({
      workflowIds: selectedWorkflowsForBatchRelation.value.map(w => w.id),
      includeSiteIds: batchRelatedSiteIds.value
    })
    
    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedWorkflowsForBatchRelation.value.length} 个工作流关联 ${relateCount} 个网站`
      : `成功取消 ${selectedWorkflowsForBatchRelation.value.length} 个工作流的所有共享关联`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchWorkflowSites(selectedWorkflowsForBatchRelation.value.map(w => w.id))
    const refreshMap2 = refreshRes2.data || {}
    workflowRelationDetails.value = selectedWorkflowsForBatchRelation.value.map(workflow => {
      const sites = refreshMap2[workflow.id] || []
      return {
        workflowId: workflow.id,
        workflowName: workflow.workflowName,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== workflow.siteId).map(s => s.siteId),
        excludedSiteIds: sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
      }
    })
  } catch (error) {
    console.error('批量关联失败:', error)
    proxy.$modal.msgError('批量关联失败')
  } finally {
    batchRelationLoading.value = false
  }
}

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  init()
})

init()
</script>

<style scoped>
.step-card {
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
}

.param-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.5;
}

.json-block {
  background: #f8f8f8;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  line-height: 1.6;
  max-height: 300px;
  overflow-y: auto;
  color: #303133;
}

.json-textarea {
  width: 100%;
  min-height: 80px;
  max-height: 400px;
  resize: vertical;
  background: #f8f8f8;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  line-height: 1.6;
  color: #303133;
  box-sizing: border-box;
  outline: none;
  cursor: default;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: #303133;
}
</style>
