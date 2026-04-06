<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
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
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 200px">
          <el-option 
            v-for="category in platformCategories" 
            :key="category.id" 
            :label="category.name" 
            :value="category.id"
          >
            <span v-if="category.icon" style="margin-right: 8px;">{{ category.icon }}</span>
            <span>{{ category.name }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="平台名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入平台名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="平台类型" prop="platformType">
        <el-select v-model="queryParams.platformType" placeholder="请选择平台类型" clearable style="width: 150px">
          <el-option label="OpenAI" value="openai" />
          <el-option label="Azure OpenAI" value="azure" />
          <el-option label="通义千问" value="qwen" />
          <el-option label="文心一言" value="wenxin" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
      <el-form-item v-if="viewMode === 'creator'">
        <el-button type="success" plain icon="Download" @click="handleFullExport" v-hasPermi="['gamebox:platform:export']">全站导出</el-button>
        <el-button type="warning" plain icon="Upload" @click="handleFullImport" v-hasPermi="['gamebox:platform:import']">全站导入</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:platform:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:platform:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:platform:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5" v-if="viewMode === 'creator' && isPersonalSiteCheck(queryParams.siteId)">
        <el-button type="danger" plain icon="CircleClose" :disabled="multiple" @click="handleBatchExclude" v-hasPermi="['gamebox:platform:edit']">批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5" v-if="viewMode === 'creator'">
        <el-button type="primary" plain icon="Link" :disabled="multiple" @click="handleBatchRelation" v-hasPermi="['gamebox:platform:edit']">批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Download" :disabled="multiple" @click="handleExport" v-hasPermi="['gamebox:platform:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleImport" v-hasPermi="['gamebox:platform:import']">导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="platformTableRef" v-loading="loading" :data="platformList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="平台名称" align="center" prop="name" :show-overflow-tooltip="true" />
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
          <!-- 默认配置显示排除数量 -->
          <template v-if="scope.row.relationSource === 'default' || isPersonalSiteCheck(scope.row.siteId)">
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
            v-else 
            type="info" 
            style="cursor: pointer" 
            @click="handleManageSites(scope.row)"
          >
            未配置
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="categoryName" width="150">
        <template #default="scope">
          <CategoryTag v-if="scope.row.categoryId" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'ai_platform', icon: scope.row.categoryIcon }" />
          <span v-else style="color: #909399">-</span>
        </template>
      </el-table-column>
      <el-table-column label="可见性" align="center" width="100" v-if="viewMode === 'related' && queryParams.siteId && !isPersonalSiteCheck(queryParams.siteId)">
        <template #header>
          <el-tooltip placement="top" effect="light">
            <template #content>
              <div style="max-width: 300px; line-height: 1.6;">
                <p style="margin: 0 0 8px 0; font-weight: bold;">可见性控制说明：</p>
                <p style="margin: 0 0 4px 0;">• <strong>默认配置</strong>：默认全局可见。通过"批量排除"设置不显示的网站；通过"管理网站关联"可添加到特定网站并控制可见性（关联优先于排除）</p>
                <p style="margin: 0 0 4px 0;">• <strong>自有数据</strong>：切换会修改启用/禁用状态。可通过"管理网站关联"分享到其他网站</p>
                <p style="margin: 0;">• <strong>共享数据</strong>：切换会修改关联可见性。可通过"移除关联"取消关联</p>
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
      <el-table-column label="平台类型" align="center" prop="platformType" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.platformType === 'openai'" type="success">OpenAI</el-tag>
          <el-tag v-else-if="scope.row.platformType === 'azure'" type="primary">Azure OpenAI</el-tag>
          <el-tag v-else-if="scope.row.platformType === 'qwen'" type="warning">通义千问</el-tag>
          <el-tag v-else-if="scope.row.platformType === 'wenxin'" type="danger">文心一言</el-tag>
          <el-tag v-else type="info">其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="API地址" align="center" prop="baseUrl" :show-overflow-tooltip="true" width="200" />
      <el-table-column label="默认模型" align="center" prop="defaultModel" width="150" :show-overflow-tooltip="true" />
      <el-table-column label="可用模型" align="center" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.availableModels" size="small">
            {{ parseModelCount(scope.row.availableModels) }} 个
          </el-tag>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="默认配置" align="center" prop="isDefault" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isDefault === '1' ? 'success' : 'info'" size="small">
            {{ scope.row.isDefault === '1' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="120" v-if="viewMode === 'creator'">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
            {{ scope.row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <!-- 关联模式下的操作 - 基于relationSource统一判断 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的配置 - 可修改、删除 -->
            <template v-if="scope.row.relationSource === 'own'">
              <el-tooltip content="测试连接" placement="top">
                <el-button 
                  link 
                  type="primary" 
                  icon="Connection" 
                  @click="handleTest(scope.row)" 
                  v-hasPermi="['gamebox:platform:edit']"
                />
              </el-tooltip>
              <el-tooltip content="修改" placement="top">
                <el-button 
                  link 
                  type="primary" 
                  icon="Edit" 
                  @click="handleUpdate(scope.row)" 
                  v-hasPermi="['gamebox:platform:edit']"
                />
              </el-tooltip>
              <el-tooltip content="网站关联" placement="top">
                <el-button 
                  link 
                  type="warning" 
                  icon="Link" 
                  @click="handleManageSites(scope.row)" 
                  v-hasPermi="['gamebox:platform:edit']"
                />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Delete" 
                  @click="handleDelete(scope.row)" 
                  v-hasPermi="['gamebox:platform:remove']"
                />
              </el-tooltip>
            </template>
            <!-- default: 默认配置 - 注意：排除/恢复功能已统一到"批量排除"按钮中管理 -->
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tooltip content="测试连接" placement="top">
                <el-button link type="primary" icon="Connection" @click="handleTest(scope.row)" v-hasPermi="['gamebox:platform:edit']" />
              </el-tooltip>
              <el-tag v-if="scope.row.isExcluded === 1" type="info" size="small">已排除</el-tag>
            </template>
            <!-- shared: 其他网站分享 - 可移除关联 -->
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-tooltip content="测试连接" placement="top">
                <el-button link type="primary" icon="Connection" @click="handleTest(scope.row)" v-hasPermi="['gamebox:platform:edit']" />
              </el-tooltip>
              <el-tooltip content="移除关联" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Close" 
                  @click="handleRemoveFromSite(scope.row)" 
                  v-hasPermi="['gamebox:platform:remove']"
                />
              </el-tooltip>
            </template>
          </template>
          <!-- 创建者模式下的操作 -->
          <template v-else>
            <el-tooltip content="测试连接" placement="top">
              <el-button link type="primary" icon="Connection" @click="handleTest(scope.row)" v-hasPermi="['gamebox:platform:edit']" />
            </el-tooltip>
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:platform:edit']" />
            </el-tooltip>
            <!-- 非默认配置：显示网站关联 -->
            <el-tooltip content="网站关联" placement="top" v-if="!isPersonalSiteCheck(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="Link" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:platform:edit']"
              />
            </el-tooltip>
            <!-- 默认配置：管理排除 -->
            <el-tooltip content="管理排除" placement="top" v-if="isPersonalSiteCheck(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="CircleClose" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:platform:edit']"
              />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:platform:remove']" />
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 网站关联管理对话框 -->
    <SiteRelationManager
      v-model="siteRelationDialogOpen"
      entity-type="aiPlatform"
      :entity-id="currentPlatformIdForSites"
      :entity-name="currentPlatformNameForSites"
      :creator-site-id="currentPlatformCreatorSiteId"
      @refresh="getList"
    />

    <!-- 添加或修改AI平台配置对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="platformRef" :model="form" :rules="rules" label-width="120px">
        <el-row v-if="!form.id">
          <el-col :span="24">
            <el-form-item label="所属网站" prop="siteId">
              <SiteSelect v-model="form.siteId" :site-list="siteList" show-default default-label="默认配置（全局）" width="100%" placeholder="请选择所属网站" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.id">
          <el-col :span="24">
            <el-form-item label="所属网站">
              <el-input :value="getSiteName(form.siteId)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入平台名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 100%">
                <el-option 
                  v-for="category in platformCategories" 
                  :key="category.id" 
                  :label="category.name" 
                  :value="category.id"
                >
                  <span v-if="category.icon" style="margin-right: 8px;">{{ category.icon }}</span>
                  <span>{{ category.name }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="平台类型" prop="platformType">
              <el-select v-model="form.platformType" placeholder="请选择平台类型" style="width: 100%">
                <el-option label="OpenAI" value="openai" />
                <el-option label="Azure OpenAI" value="azure" />
                <el-option label="通义千问" value="qwen" />
                <el-option label="文心一言" value="wenxin" />
                <el-option label="其他" value="other" />
              </el-select>
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
            <el-form-item label="API地址" prop="baseUrl">
              <el-input v-model="form.baseUrl" placeholder="请输入API地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="API密钥" prop="apiKey">
              <el-input v-model="form.apiKey" placeholder="请输入API密钥" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="可用模型">
              <el-button 
                type="primary" 
                :icon="loadingModels ? 'Loading' : 'Refresh'" 
                :loading="loadingModels"
                @click="handleLoadModels"
                size="small">
                {{ loadingModels ? '加载中...' : '从平台加载模型列表' }}
              </el-button>
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">
                已加载 {{ availableModels.length }} 个模型
              </span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="默认模型" prop="defaultModel">
              <el-select 
                v-model="form.defaultModel" 
                placeholder="请选择或输入模型名称" 
                filterable 
                allow-create
                default-first-option
                :loading="loadingModels"
                style="width: 100%">
                <el-option v-for="model in availableModels" :key="model" :label="model" :value="model" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大Token" prop="maxTokens">
              <el-input-number v-model="form.maxTokens" :min="100" :max="32000" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="Temperature" prop="temperature">
              <el-input-number v-model="form.temperature" :min="0" :max="2" :precision="2" :step="0.1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="速率限制/分钟" prop="rateLimitPerMinute">
              <el-input-number v-model="form.rateLimitPerMinute" :min="1" :max="1000" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="默认配置" prop="isDefault">
              <el-switch v-model="form.isDefault" active-value="1" inactive-value="0" />
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

    <!-- 排除管理对话框（已废弃，仅保留避免错误） -->
    <el-dialog
      v-model="exclusionDialogVisible"
      :title="`管理排除 - ${currentPlatformForExclusion?.name}`"
      width="600px"
      append-to-body
    >
      <el-alert
        title="提示：勾选的网站将无法看到此默认AI平台配置"
        type="warning"
        :closable="false"
        style="margin-bottom: 15px"
        show-icon
      />
      <el-checkbox-group v-model="selectedExcludedSites">
        <el-checkbox
          v-for="site in exclusionSiteList"
          :key="site.id"
          :label="site.id"
          style="display: block; margin-bottom: 10px"
        >
          {{ site.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="exclusionDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitExclusions">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 批量排除管理对话框 -->
    <el-dialog 
      title="批量排除管理" 
      v-model="batchExclusionDialogOpen" 
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选AI平台配置）</p>
          <p style="margin: 0;">• <strong>不勾选的网站</strong> = 移除 exclude 排除（全局可见）</p>
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
          <strong>已选AI平台配置：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedPlatformsForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="platform in selectedPlatformsForBatchExclude" 
            :key="platform.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removePlatformFromBatchExclude(platform.id)"
            size="small"
          >
            {{ platform.name }}
          </el-tag>
        </div>
      </div>
      
      <el-collapse v-if="platformExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各配置当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="platformExclusionDetails" size="small" stripe>
              <el-table-column label="平台名称" prop="platformName" width="150" show-overflow-tooltip />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选AI平台配置对该网站可见）</p>
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
          <strong>已选AI平台配置：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedPlatformsForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="platform in selectedPlatformsForBatchRelation" 
            :key="platform.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removePlatformFromBatchRelation(platform.id)"
            size="small"
          >
            {{ platform.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各平台的当前关联/排除状态 -->
      <el-collapse v-if="platformRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各配置当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="platformRelationDetails" size="small" stripe>
              <el-table-column label="平台名称" prop="platformName" width="150" show-overflow-tooltip />
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

    <!-- 全站导出对话框 -->
    <FullExportDialog
      v-model="fullExportDialogOpen"
      v-model:exportFormat="fullExportFormat"
      :loading="fullExportLoading"
      @confirm="confirmFullExport"
    >
      <template #exportTips>
        <p>• 全站导出将包含所有AI平台配置数据（默认配置 + 所有站点）</p>
        <p>• 包含所有网站关联关系</p>
        <p>• 包含默认配置及排除关系</p>
        <p style="margin-top: 10px; color: #F56C6C; font-weight: bold;">⚠️ 数据量可能较大，请耐心等待</p>
      </template>
    </FullExportDialog>

    <!-- 导出对话框 -->
    <ExportDialog
      v-model="exportDialogOpen"
      :selectedCount="ids.length"
      v-model:exportFormat="exportFormat"
      :loading="exportLoading"
      @confirm="confirmExport"
    >
      <template #exportTips>
        <p>• AI平台配置数据：包含平台配置和关联关系</p>
        <p>• AI平台配置不包含翻译数据</p>
        <p style="margin-top: 10px; color: #909399;">导入时将自动归属到当前选择的网站</p>
      </template>
    </ExportDialog>

    <!-- 导入对话框 -->
    <ImportDialog
      v-model="importDialogOpen"
      :loading="importLoading"
      :previewData="importPreviewData"
      :translationsData="[]"
      @confirm="confirmImport"
      @fileChange="handleFileChange"
      @fileRemove="handleFileRemove"
    >
      <template #importTips>
        <p>• 支持导入 Excel (.xlsx) 或 JSON (.json) 格式文件</p>
        <p>• 将根据导入数据重建AI平台配置和关联关系</p>
        <p>• 请确保文件格式与导出的数据格式一致</p>
        <p>• 必填字段：平台名称、平台类型、API密钥</p>
      </template>
      <template #previewColumns>
        <el-table-column prop="name" label="平台名称" width="150" />
        <el-table-column prop="platformType" label="平台类型" width="120" />
        <el-table-column prop="siteCode" label="网站" width="100" />
        <el-table-column prop="status" label="状态" width="80" />
      </template>
    </ImportDialog>

    <!-- 全站导入对话框 -->
    <FullImportDialog
      v-model="fullImportDialogOpen"
      :loading="fullImportLoading"
      :siteList="siteList"
      :importSites="fullImportSites"
      :importData="fullImportPlatforms"
      v-model:importTranslations="fullImportTranslations"
      v-model:importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      :dataLabel="'AI平台配置'"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #importTips>
        <p style="margin: 0;">从其他系统导入完整的AI平台配置数据，包括：</p>
        <ul style="margin: 5px 0; padding-left: 20px;">
          <li>AI平台配置基础数据</li>
          <li>网站关联关系</li>
          <li>默认配置排除关系</li>
        </ul>
      </template>
    </FullImportDialog>
  </div>
</template>

<script setup name="Platform">
import { listPlatform, getPlatform, delPlatform, addPlatform, updatePlatform, testPlatform, getAvailableModels } from "@/api/gamebox/platform"
import { batchSaveAiPlatformSiteRelations, updatePlatformVisibility, getPlatformSites, getBatchPlatformSites } from "@/api/gamebox/siteRelation"
import { useSiteSelection, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"
import SiteRelationManager from "@/components/SiteRelationManager/index.vue"
import CategoryTag from "@/components/CategoryTag/index.vue"
import { listVisibleCategory } from "@/api/gamebox/category"
import { ExportDialog, ImportDialog, FullExportDialog, FullImportDialog } from "@/components/ImportExportDialogs"
import { Warning } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()

// 使用网站选择组合式函数
const { siteList, currentSiteId, loadSiteList, getSiteName } = useSiteSelection()
const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

const platformList = ref([])
const platformTableRef = ref(null)
const platformCategories = ref([]) // AI平台分类列表
const includeDefaultConfig = ref(false) // 是否包含默认配置
const viewMode = ref('creator') // 查看模式：creator-创建者, related-关联网站
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const availableModels = ref([])
const loadingModels = ref(false)
const isLoadingEditData = ref(false)

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentPlatformIdForSites = ref(0)
const currentPlatformNameForSites = ref('')
const currentPlatformCreatorSiteId = ref(0)

// 旧的单个排除对话框变量（已废弃，仅保留用于避免报错）
const exclusionDialogVisible = ref(false)
const currentPlatformForExclusion = ref(null)
const selectedExcludedSites = ref([])
const exclusionSiteList = ref([])

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const selectedPlatformsForBatchExclude = ref([])
const platformExclusionDetails = ref([]) // 各平台的排除详情

// batchExclusionConflictSiteIds：所有平台中存在 include 关联的网站ID集合（用于排除对话框冲突标识）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  platformExclusionDetails.value.forEach(detail => {
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
const selectedPlatformsForBatchRelation = ref([])
const platformRelationDetails = ref([]) // 各平台的关联详情

// 是否处于默认配置的批量关联管理
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId, siteList.value))

// 冲突网站 ID 集合（存在 exclude 关系的网站）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  platformRelationDetails.value.forEach(detail => {
    if (detail.excludedSiteIds) {
      detail.excludedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

// 导出管理相关
const exportDialogOpen = ref(false)
const exportFormat = ref('excel')
const exportLoading = ref(false)

// 全站导出相关
const fullExportDialogOpen = ref(false)
const fullExportFormat = ref('excel')
const fullExportLoading = ref(false)

// 导入管理相关
const importDialogOpen = ref(false)
const importLoading = ref(false)
const importPreviewData = ref([])
const importFile = ref(null)

// 全站导入相关
const fullImportDialogOpen = ref(false)
const fullImportLoading = ref(false)
const fullImportSites = ref([]) // 导入文件中的网站列表
const fullImportPlatforms = ref([]) // 导入的AI平台配置数据
const fullImportRelations = ref([]) // 导入的关联关系
const fullImportTranslations = ref([]) // 导入的翻译数据（平台不使用）
const fullImportFile = ref(null)
const siteMapping = ref({}) // 网站ID映射 {源网站ID: 目标网站ID}
const hasDefaultConfig = ref(false) // 是否包含默认配置(siteid=0)
const createDefaultAsNewSite = ref(true) // 是否将默认配置导入为新网站的配置（并创建关联）

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: undefined,
    categoryId: undefined,
    name: undefined,
    platformType: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: "平台名称不能为空", trigger: "blur" }],
    platformType: [{ required: true, message: "平台类型不能为空", trigger: "change" }],
    baseUrl: [{ required: true, message: "API地址不能为空", trigger: "blur" }],
    apiKey: [{ required: true, message: "API密钥不能为空", trigger: "blur" }],
    defaultModel: [{ required: true, message: "模型名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 网站切换事件 */
function handleSiteChange() {
  // 切换网站时重置"含默认配置"选项
  if (!queryParams.value.siteId || isPersonalSite(queryParams.value.siteId, siteList.value)) {
    includeDefaultConfig.value = false
  }
  queryParams.value.pageNum = 1
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  // 重新加载分类列表
  loadPlatformCategories()
  getList()
}

/** 查看模式切换事件 */
function handleViewModeChange() {
  queryParams.value.siteId = resolveSiteIdByViewMode(viewMode.value, siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  // 重置"含默认配置"选项
  includeDefaultConfig.value = false
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  // 重新加载分类列表
  loadPlatformCategories()
  // 切换模式时重新查询
  queryParams.value.pageNum = 1
  getList()
}

function getList() {
  loading.value = true
  
  // 构建查询参数
  const params = { 
    ...queryParams.value,
    queryMode: viewMode.value,
    includeDefault: includeDefaultConfig.value
  }
  
  // 使用统一的查询接口
  if (params.siteId !== undefined) {
    listPlatform(params).then(response => {
      platformList.value = response.rows
      total.value = response.total
      loading.value = false
    }).catch(error => {
      console.error('查询AI平台配置失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  } else {
    // 未选择网站：默认显示默认配置
    listPlatform({ ...params, siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      platformList.value = response.rows
      total.value = response.total
      loading.value = false
    }).catch(error => {
      console.error('查询AI平台配置失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  }
}

// 解析模型数量
function parseModelCount(modelsJson) {
  if (!modelsJson) return 0
  try {
    const models = JSON.parse(modelsJson)
    return Array.isArray(models) ? models.length : 0
  } catch (e) {
    return 0
  }
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    siteId: undefined,
    categoryId: undefined,
    name: undefined,
    platformType: undefined,
    baseUrl: undefined,
    apiKey: undefined,
    organizationId: undefined,
    defaultModel: undefined,
    availableModels: undefined,
    maxTokens: 2000,
    temperature: 0.7,
    rateLimitPerMinute: 60,
    costPer1kTokens: 0,
    proxyConfig: undefined,
    isDefault: "0",
    status: "1",
    remark: undefined
  }
  availableModels.value = []
  if (proxy.$refs.platformRef) {
    proxy.$refs.platformRef.clearValidate()
  }
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
  // 默认使用当前查询的站点ID
  if (queryParams.value.siteId) {
    form.value.siteId = queryParams.value.siteId
  } else if (siteList.value.length > 0) {
    // 如果查询没有选择站点，默认选择第一个站点
    form.value.siteId = siteList.value[0].id
  }
  open.value = true
  title.value = "添加AI平台配置"
  availableModels.value = []
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  
  isLoadingEditData.value = true
  
  getPlatform(id).then(response => {
    const data = response.data
    
    // 解析模型列表
    let models = []
    if (data.availableModels) {
      try {
        models = JSON.parse(data.availableModels)
      } catch (e) {
        console.error('解析模型列表失败:', e)
        models = []
      }
    }
    
    // 更新模型列表
    availableModels.value = models
    
    // 填充表单数据
    form.value = {
      ...data,
      maxTokens: Number(data.maxTokens) || 2000,
      temperature: Number(data.temperature) || 0.7,
      rateLimitPerMinute: Number(data.rateLimitPerMinute) || 60,
      costPer1kTokens: Number(data.costPer1kTokens) || 0
    }
    
    nextTick(() => {
      isLoadingEditData.value = false
    })
    
    open.value = true
    title.value = "修改AI平台配置"
  }).catch(error => {
    isLoadingEditData.value = false
    console.error('获取平台配置失败:', error)
    proxy.$modal.msgError("获取平台配置失败")
  })
}

function handleLoadModels() {
  if (!form.value.platformType) {
    proxy.$modal.msgWarning("请先选择平台类型")
    return
  }
  if (!form.value.apiKey) {
    proxy.$modal.msgWarning("请先输入API密钥")
    return
  }
  if (!form.value.baseUrl) {
    proxy.$modal.msgWarning("请先输入API地址")
    return
  }
  
  loadingModels.value = true
  const apiKey = form.value.apiKey
  const baseUrl = form.value.baseUrl
  
  getAvailableModels(form.value.platformType, apiKey, baseUrl).then(response => {
    const models = response.data || []
    availableModels.value = models
    form.value.availableModels = JSON.stringify(models)
    loadingModels.value = false
    if (models.length > 0) {
      proxy.$modal.msgSuccess(`成功加载 ${models.length} 个模型`)
    } else {
      proxy.$modal.msgWarning("未获取到模型列表，请检查配置是否正确")
    }
  }).catch(() => {
    availableModels.value = []
    loadingModels.value = false
    proxy.$modal.msgError("加载模型失败，请检查API密钥和地址是否正确")
  })
}

watch(() => form.value.platformType, (newType, oldType) => {
  if (isLoadingEditData.value) {
    return
  }
  availableModels.value = []
  form.value.defaultModel = undefined
  form.value.availableModels = undefined
})

function handleTest(row) {
  proxy.$modal.loading("正在测试连接...")
  testPlatform(row.id).then(response => {
    proxy.$modal.closeLoading()
    if (response && (response.code === 200 || response.success)) {
      const data = response.data || {}
      let msg = "测试连接成功"
      const modelCount = data.modelCount != null ? data.modelCount : 0
      msg += `（获取到 ${modelCount} 个可用模型）`
      proxy.$modal.msgSuccess(msg)
    } else {
      proxy.$modal.msgError(response.msg || "测试失败")
    }
  }).catch(() => {
    // 请求拦截器已自动展示后端错误信息，此处仅关闭加载状态
    proxy.$modal.closeLoading()
  })
}

function submitForm() {
  proxy.$refs["platformRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updatePlatform(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPlatform(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const platformIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除平台编号为"' + platformIds + '"的数据项？').then(function() {
    return delPlatform(platformIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 管理网站关联 */
function handleManageSites(row) {
  currentPlatformIdForSites.value = row.id
  currentPlatformNameForSites.value = row.name
  currentPlatformCreatorSiteId.value = row.siteId || 0
  siteRelationDialogOpen.value = true
}

/** 排除默认平台配置 */
async function handleExclude(row) {
  try {
    await proxy.$modal.confirm(`确认要排除默认AI平台配置"${row.name}"吗？排除后该配置将不在当前网站显示。`)
    const siteId = queryParams.value.siteId
    const res = await getPlatformSites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excludeSiteIds.includes(siteId)) excludeSiteIds.push(siteId)
    await batchSaveAiPlatformSiteRelations({ aiPlatformIds: [row.id], includeSiteIds, excludeSiteIds })
    proxy.$modal.msgSuccess("排除成功")
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('排除失败:', error)
    }
  }
}

/** 恢复被排除的默认平台配置 */
async function handleRestore(row) {
  try {
    await proxy.$modal.confirm(`确认要恢复默认AI平台配置"${row.name}"吗？恢复后该配置将重新在当前网站显示。`)
    const siteId = queryParams.value.siteId
    const res = await getPlatformSites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== siteId)
    await batchSaveAiPlatformSiteRelations({ aiPlatformIds: [row.id], includeSiteIds, excludeSiteIds })
    proxy.$modal.msgSuccess("恢复成功")
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
    }
  }
}

/** 管理默认平台配置的排除网站 */
async function handleManageExclusions(row) {
  currentPlatformForExclusion.value = row
  exclusionSiteList.value = siteList.value.filter(s => s.id > 0)
  
  try {
    const response = await getExcludedSitesByPlatform(row.id)
    selectedExcludedSites.value = response.data || []
    exclusionDialogVisible.value = true
  } catch (error) {
    console.error('加载排除网站失败:', error)
    proxy.$modal.msgError('加载排除网站失败')
  }
}

async function submitExclusions() {
  try {
    await manageDefaultPlatformExclusions({
      aiPlatformId: currentPlatformForExclusion.value.id,
      excludedSiteIds: selectedExcludedSites.value
    })
    proxy.$modal.msgSuccess("管理成功")
    exclusionDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('管理排除失败:', error)
    proxy.$modal.msgError('管理失败')
  }
}

/** 移除网站关联 */
async function handleRemoveFromSite(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认从"${getSiteName(currentQuerySiteId)}"移除该AI平台配置的关联吗？`)
    const res = await getPlatformSites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId).filter(id => id !== currentQuerySiteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    await batchSaveAiPlatformSiteRelations({ aiPlatformIds: [row.id], includeSiteIds, excludeSiteIds })
    proxy.$modal.msgSuccess("移除关联成功")
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除关联失败:', error)
    }
  }
}

/** 获取可见性值 */
function getVisibilityValue(row) {
  if (row.relationSource === 'default') {
    // 默认配置：使用 isVisible 判断是否被排除
    // isVisible === '0' 表示被排除（不显示）
    return row.isVisible || '1'
  } else if (row.relationSource === 'own') {
    // 自有数据：使用 status 状态
    return row.status
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
      const relationResponse = await getPlatformSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段（忽略排除逻辑）
        await updatePlatformVisibility(currentQuerySiteId, row.id, newValue)
        const action = newValue === '1' ? '显示' : '隐藏'
        proxy.$modal.msgSuccess(`${action}成功`)
        // 重新加载列表以更新统计信息
        getList()
      } else {
        // 没有关联记录：使用排除逻辑
        const includeSiteIds = relations.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
        const excludeSiteIds = relations.filter(s => s.relationType === 'exclude').map(s => s.siteId)
        if (newValue === '0') {
          // 要隐藏 -> 添加排除关系
          if (!excludeSiteIds.includes(currentQuerySiteId)) excludeSiteIds.push(currentQuerySiteId)
          await batchSaveAiPlatformSiteRelations({ aiPlatformIds: [row.id], includeSiteIds, excludeSiteIds })
          proxy.$modal.msgSuccess('已排除该配置')
        } else {
          // 要显示 -> 移除排除关系
          const newExcludes = excludeSiteIds.filter(id => id !== currentQuerySiteId)
          await batchSaveAiPlatformSiteRelations({ aiPlatformIds: [row.id], includeSiteIds, excludeSiteIds: newExcludes })
          proxy.$modal.msgSuccess('已恢复该配置')
        }
        // 重新加载列表以更新排除网站数量
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用状态
      const text = newValue === '1' ? '启用' : '禁用'
      await updatePlatform({ id: row.id, status: newValue })
      row.status = newValue
      proxy.$modal.msgSuccess(`${text}成功`)
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updatePlatformVisibility(currentQuerySiteId, row.id, newValue)
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

/** 快速切换可见性（仅用于 shared 类型）*/
async function handleQuickToggleVisibility(row) {
  const currentQuerySiteId = queryParams.value.siteId
  // 切换状态
  const newVisibility = row.isVisible === '1' ? '0' : '1'
  const action = newVisibility === '1' ? '显示' : '隐藏'
  
  try {
    await updatePlatformVisibility(currentQuerySiteId, row.id, newVisibility)
    
    // 更新成功后才更新UI
    row.isVisible = newVisibility
    proxy.$modal.msgSuccess(`${action}成功`)
  } catch (error) {
    console.error('更新可见性失败:', error)
    proxy.$modal.msgError('更新失败')
  }
}

/** 加载AI平台分类列表 */
async function loadPlatformCategories() {
  try {
    // 根据当前查看模式和网站ID加载分类
    const params = {
      categoryType: 'ai_platform',
      siteId: queryParams.value.siteId || 0,
      queryMode: viewMode.value,
      status: '1' // 只加载启用的分类
    }
    
    const response = await listVisibleCategory(params)
    platformCategories.value = response.rows || []
  } catch (error) {
    console.error('加载AI平台分类失败:', error)
    platformCategories.value = []
  }
}

// === 排除/恢复默认配置相关函数 ===
/** 排除默认配置 */
async function handleExcludeDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认要排除默认AI平台配置"${row.name}"吗？排除后该配置将不在当前网站显示。`)
    const res = await getPlatformSites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excludeSiteIds.includes(currentQuerySiteId)) excludeSiteIds.push(currentQuerySiteId)
    await batchSaveAiPlatformSiteRelations({ aiPlatformIds: [row.id], includeSiteIds, excludeSiteIds })
    proxy.$modal.msgSuccess("排除成功")
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('排除失败:', error)
    }
  }
}

/** 恢复被排除的默认配置 */
async function handleRestoreDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认要恢复默认AI平台配置"${row.name}"吗？恢复后该配置将重新在当前网站显示。`)
    const res = await getPlatformSites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== currentQuerySiteId)
    await batchSaveAiPlatformSiteRelations({ aiPlatformIds: [row.id], includeSiteIds, excludeSiteIds })
    proxy.$modal.msgSuccess("恢复成功")
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
    }
  }
}

// 由于函数太多，我将分批添加。暂时让我们创建一个单独的composables或utils文件
// 来存放这些导入导出函数。但为了快速实现，我们先添加关键函数

/** 获取网站编码 */
function getSiteCode(siteId) {
  if (isPersonalSite(siteId, siteList.value)) return 'default'
  const site = siteList.value.find(s => s.id === siteId)
  return site ? site.code : ''
}

// === 批量排除管理 ===
async function handleBatchExclude() {
  const selectedRows = platformList.value.filter(platform => ids.value.includes(platform.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的AI平台配置')
    return
  }
  
  // 只允许默认配置的平台进行批量排除
  const invalidPlatforms = selectedRows.filter(platform => !isPersonalSite(platform.siteId, siteList.value))
  if (invalidPlatforms.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的AI平台进行批量排除管理')
    return
  }
  
  selectedPlatformsForBatchExclude.value = selectedRows.map(platform => ({
    id: platform.id,
    name: platform.name
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中平台的网站关系
    const batchRes = await getBatchPlatformSites(selectedPlatformsForBatchExclude.value.map(p => p.id))
    const batchMap = batchRes.data || {}
    const results = selectedPlatformsForBatchExclude.value.map(platform => {
      const sites = batchMap[platform.id] || []
      return {
        platformId: platform.id,
        platformName: platform.name,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    platformExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有平台共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedPlatformsForBatchExclude.value.length) {
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

function removePlatformFromBatchExclude(platformId) {
  selectedPlatformsForBatchExclude.value = selectedPlatformsForBatchExclude.value.filter(
    platform => platform.id !== platformId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== platformId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (platformTableRef.value) {
    const row = platformList.value.find(platform => platform.id === platformId)
    if (row) {
      platformTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  platformExclusionDetails.value = platformExclusionDetails.value.filter(
    detail => detail.platformId !== platformId
  )
  
  if (selectedPlatformsForBatchExclude.value.length === 0) {
    batchExclusionDialogOpen.value = false
  }
}

function selectAllSitesForBatchExclude() {
  batchExcludedSiteIds.value = siteList.value.filter(s => s.isPersonal !== 1).map(site => site.id)
}

function deselectAllSitesForBatchExclude() {
  batchExcludedSiteIds.value = []
}

function invertSiteSelectionForBatchExclude() {
  const allSiteIds = siteList.value.filter(s => s.isPersonal !== 1).map(site => site.id)
  const currentSelected = new Set(batchExcludedSiteIds.value)
  batchExcludedSiteIds.value = allSiteIds.filter(id => !currentSelected.has(id))
}

async function submitBatchExclusions() {
  if (selectedPlatformsForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何AI平台配置')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    const promises = []
    
    // 对每个AI平台配置进行处理
    for (const platform of selectedPlatformsForBatchExclude.value) {
        promises.push(
          batchSaveAiPlatformSiteRelations({
            aiPlatformIds: [platform.id],
            excludeSiteIds: batchExcludedSiteIds.value
          })
        )
    }
    await Promise.all(promises)
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedPlatformsForBatchExclude.value.length} 个配置排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedPlatformsForBatchExclude.value.length} 个配置的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchPlatformSites(selectedPlatformsForBatchExclude.value.map(p => p.id))
    const refreshMap = refreshRes.data || {}
    platformExclusionDetails.value = selectedPlatformsForBatchExclude.value.map(platform => {
      const sites = refreshMap[platform.id] || []
      return {
        platformId: platform.id,
        platformName: platform.name,
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
  const selectedRows = platformList.value.filter(platform => ids.value.includes(platform.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的AI平台配置')
    return
  }
  
  const isDefaultConfig = isPersonalSite(queryParams.value.siteId, siteList.value)
  
  selectedPlatformsForBatchRelation.value = selectedRows.map(platform => ({
    id: platform.id,
    name: platform.name,
    siteId: platform.siteId
  }))
  
  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchPlatformSites(selectedPlatformsForBatchRelation.value.map(p => p.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedPlatformsForBatchRelation.value.map(platform => {
      const sites = batchMap2[platform.id] || []
      return {
        platformId: platform.id,
        platformName: platform.name,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== platform.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    platformRelationDetails.value = results
    
    // 找出被所有平台共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedPlatformsForBatchRelation.value.length) {
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

/** 从批量关联中移除某个平台 */
function removePlatformFromBatchRelation(platformId) {
  selectedPlatformsForBatchRelation.value = selectedPlatformsForBatchRelation.value.filter(
    platform => platform.id !== platformId
  )
  
  ids.value = ids.value.filter(id => id !== platformId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  if (platformTableRef.value) {
    const row = platformList.value.find(platform => platform.id === platformId)
    if (row) {
      platformTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  platformRelationDetails.value = platformRelationDetails.value.filter(
    detail => detail.platformId !== platformId
  )
  
  if (selectedPlatformsForBatchRelation.value.length === 0) {
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
  if (selectedPlatformsForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何AI平台配置')
    return
  }
  
  batchRelationLoading.value = true
  
  try {
    // 一次请求处理所有选中平台的关联关系
    await batchSaveAiPlatformSiteRelations({
      aiPlatformIds: selectedPlatformsForBatchRelation.value.map(p => p.id),
      includeSiteIds: batchRelatedSiteIds.value
    })
    
    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedPlatformsForBatchRelation.value.length} 个配置关联 ${relateCount} 个网站`
      : `成功取消 ${selectedPlatformsForBatchRelation.value.length} 个配置的所有共享关联`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchPlatformSites(selectedPlatformsForBatchRelation.value.map(p => p.id))
    const refreshMap2 = refreshRes2.data || {}
    platformRelationDetails.value = selectedPlatformsForBatchRelation.value.map(platform => {
      const sites = refreshMap2[platform.id] || []
      return {
        platformId: platform.id,
        platformName: platform.name,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== platform.siteId).map(s => s.siteId),
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

// ============ 导入导出功能 ============

/** 导出数据 */
async function handleExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgError('请选择要导出的AI平台配置')
    return
  }
  
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgError('请选择要导出的AI平台配置')
    return
  }
  
  exportLoading.value = true
  
  try {
    // 获取选中的AI平台配置详细数据
    const platformPromises = selectedIds.map(id => getPlatform(id))
    const platformResponses = await Promise.all(platformPromises)
    const platformData = platformResponses.map(response => response.data)
    
    // 转换数据格式
    const formattedData = platformData.map(platform => ({
      平台名称: platform.name,
      平台类型: platform.platformType,
      分类名称: platform.categoryName || '',
      'API地址': platform.baseUrl || '',
      'API密钥': platform.apiKey || '',
      默认模型: platform.defaultModel || '',
      可用模型: platform.availableModels || '',
      最大Token: platform.maxTokens || '',
      温度: platform.temperature || '',
      默认配置: platform.isDefault || '0',
      优先级: platform.priority || 0,
      状态: platform.status || '1',
      配置描述: platform.description || ''
    }))
    
    if (exportFormat.value === 'excel') {
      await exportToExcel(formattedData)
    } else {
      exportToJSON(formattedData)
    }
    
    proxy.$modal.msgSuccess('数据导出成功')
    exportDialogOpen.value = false
    
  } catch (error) {
    console.error('导出数据失败:', error)
    proxy.$modal.msgError('导出数据失败: ' + (error.message || '未知错误'))
  } finally {
    exportLoading.value = false
  }
}

/** 导出为Excel */
async function exportToExcel(platformData) {
  const XLSX = await import('xlsx')
  
  const wb = XLSX.utils.book_new()
  
  // 创建AI平台配置数据工作表
  const platformWs = XLSX.utils.json_to_sheet(platformData)
  const platformColWidths = [
    { wch: 20 }, // 平台名称
    { wch: 15 }, // 平台类型
    { wch: 15 }, // 分类名称
    { wch: 30 }, // API地址
    { wch: 40 }, // API密钥
    { wch: 20 }, // 默认模型
    { wch: 30 }, // 可用模型
    { wch: 10 }, // 最大Token
    { wch: 10 }, // 温度
    { wch: 10 }, // 默认配置
    { wch: 8 },  // 优先级
    { wch: 8 },  // 状态
    { wch: 30 }  // 配置描述
  ]
  platformWs['!cols'] = platformColWidths
  XLSX.utils.book_append_sheet(wb, platformWs, "AI平台配置数据")
  
  const fileName = `AI平台配置数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出为JSON */
function exportToJSON(platformData) {
  const exportData = {
    platforms: platformData,
    exportTime: new Date().toISOString()
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `AI平台配置数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
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

/** 导入数据 */
function handleImport() {
  if (viewMode.value !== 'creator') {
    proxy.$modal.msgError('请切换到创建者模式进行导入')
    return
  }
  
  importDialogOpen.value = true
  importPreviewData.value = []
  importFile.value = null
}

/** 处理文件选择 */
async function handleFileChange(file) {
  importFile.value = file
  
  try {
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.raw.type.includes('json')) {
      parsedData = parseJSONData(fileData)
    } else {
      parsedData = await parseExcelData(fileData)
    }
    
    importPreviewData.value = validateAndTransformImportData(parsedData.platforms)
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + error.message)
    importPreviewData.value = []
  }
}

/** 处理文件移除 */
function handleFileRemove() {
  importFile.value = null
  importPreviewData.value = []
  return true
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

/** 解析Excel数据 */
async function parseExcelData(arrayBuffer) {
  const XLSX = await import('xlsx')
  const workbook = XLSX.read(arrayBuffer, { type: 'array' })
  
  const result = { platforms: [] }
  
  // 解析AI平台配置数据
  const platformSheetName = workbook.SheetNames.find(name => 
    name === 'AI平台配置数据' || name === 'platforms'
  ) || workbook.SheetNames[0]
  if (platformSheetName && workbook.Sheets[platformSheetName]) {
    result.platforms = XLSX.utils.sheet_to_json(workbook.Sheets[platformSheetName])
  }
  
  return result
}

/** 解析JSON数据 */
function parseJSONData(jsonString) {
  try {
    const data = JSON.parse(jsonString)
    return {
      platforms: data.platforms || (Array.isArray(data) ? data : [])
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
      const category = platformCategories.value.find(cat => 
        cat.name === item['分类名称'] && 
        (cat.siteId === siteId || isPersonalSite(cat.siteId, siteList.value))
      )
      if (category) {
        categoryId = category.id
      }
    }
    
    return {
      virtualId: item['AI平台虚拟ID'],
      name: item['平台名称'] || `导入平台${index + 1}`,
      platformType: item['平台类型'] || 'openai',
      siteId: siteId,
      categoryId: categoryId,
      baseUrl: item['API地址'] || '',
      apiKey: item['API密钥'] || '',
      defaultModel: item['默认模型'] || '',
      availableModels: item['可用模型'] || '',
      maxTokens: item['最大Token'] || '',
      temperature: item['温度'] || '',
      isDefault: normalizeStatus(item['默认配置']),
      priority: parseInt(item['优先级']) || 0,
      status: normalizeStatus(item['状态']),
      description: item['配置描述'] || '',
      // 显示用字段
      siteCode: getSiteCode(siteId)
    }
  })
}

/** 确认导入 */
async function confirmImport() {
  if (importPreviewData.value.length === 0) {
    proxy.$modal.msgError('没有可导入的数据')
    return
  }
  
  try {
    importLoading.value = true
    
    // 批量导入AI平台配置
    for (const platformData of importPreviewData.value) {
      const { virtualId, siteCode, ...cleanData } = platformData
      await addPlatform(cleanData)
    }
    
    proxy.$modal.msgSuccess(`成功导入 ${importPreviewData.value.length} 条AI平台配置`)
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

/** 全站导出 */
function handleFullExport() {
  fullExportDialogOpen.value = true
}

/** 确认全站导出 */
async function confirmFullExport() {
  try {
    fullExportLoading.value = true
    
    // 1. 获取所有AI平台配置数据
    const platformsResponse = await listPlatform({ 
      queryMode: 'creator', 
      pageNum: 1, 
      pageSize: 9999 
    })
    const platformData = platformsResponse.rows || []
    
    // 2. 先收集所有涉及的网站ID
    const siteIds = new Set()
    platformData.forEach(platform => {
      if (platform.siteId !== null && platform.siteId !== undefined) {
        siteIds.add(platform.siteId)
      }
    })
    
    // 3. 获取所有关联关系，同时收集网站ID
    const relationDataRaw = []
    for (let index = 0; index < platformData.length; index++) {
      const platform = platformData[index]
      const virtualId = index + 1
      
      try {
        const response = await getPlatformSites(platform.id)
        const relations = response.data || []
        if (relations.length > 0) {
          relations.forEach(rel => {
            siteIds.add(rel.siteId)
            
            const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
            
            const relationData = {
              'AI平台虚拟ID': virtualId,
              关联类型: relationType,
              '网站ID_原始': rel.siteId
            }
            
            if (rel.relationType !== 'exclude') {
              relationData.可见性 = rel.isVisible === '1' ? '显示' : '隐藏'
            }
            
            relationDataRaw.push(relationData)
          })
        }
      } catch (error) {
        console.warn('获取AI平台配置关联失败:', platform.id, error)
      }
    }
    
    // 4. 获取网站详细信息并建立ID映射
    const sitesData = []
    const siteIdToVirtualIdMap = new Map()
    let realSiteIndex = 0
    
    siteIdToVirtualIdMap.set(personalSiteId.value, 0)
    
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
          const virtualId = realSiteIndex
          siteIdToVirtualIdMap.set(siteId, virtualId)
          
          sitesData.push({
            网站虚拟ID: virtualId,
            网站名称: site.name,
            网站编码: site.code,
            网站域名: site.domain || '',
            状态: site.status === '1' ? '启用' : '禁用'
          })
        }
      }
    }
    
    // 5. 转换关联数据中的网站ID为虚拟ID
    const relationData = relationDataRaw.map(rel => {
      const virtualSiteId = siteIdToVirtualIdMap.get(rel['网站ID_原始']) || 0
      const { '网站ID_原始': _, ...rest } = rel
      return {
        ...rest,
        网站虚拟ID: virtualSiteId
      }
    })
    
    // 6. 格式化AI平台配置数据并使用虚拟ID
    const formattedPlatformData = platformData.map((platform, index) => {
      const virtualId = index + 1
      const virtualSiteId = siteIdToVirtualIdMap.get(platform.siteId) || 0
      
      // 处理JSON字段：确保导出的是有效的JSON字符串
      let availableModelsStr = '[]'
      if (platform.availableModels) {
        if (typeof platform.availableModels === 'string') {
          try {
            // 验证是否是有效JSON
            JSON.parse(platform.availableModels)
            availableModelsStr = platform.availableModels
          } catch (e) {
            availableModelsStr = '[]'
          }
        } else if (Array.isArray(platform.availableModels)) {
          availableModelsStr = JSON.stringify(platform.availableModels)
        }
      }
      
      return {
        'AI平台虚拟ID': virtualId,
        网站虚拟ID: virtualSiteId,
        平台名称: platform.name,
        平台类型: platform.platformType,
        分类名称: platform.categoryName || '',
        'API地址': platform.baseUrl || '',
        'API密钥': platform.apiKey || '',
        默认模型: platform.defaultModel || '',
        可用模型: availableModelsStr,
        最大Token: platform.maxTokens || '',
        温度: platform.temperature || '',
        默认配置: platform.isDefault || '0',
        优先级: platform.priority || 0,
        状态: platform.status || '1',
        配置描述: platform.description || ''
      }
    })
    
    // 7. 导出数据
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, formattedPlatformData, relationData)
    } else {
      exportFullDataToJSON(sitesData, formattedPlatformData, relationData)
    }
    
    proxy.$modal.msgSuccess('全站数据导出成功')
    fullExportDialogOpen.value = false
    
  } catch (error) {
    console.error('全站导出失败:', error)
    proxy.$modal.msgError('全站导出失败: ' + (error.message || '未知错误'))
  } finally {
    fullExportLoading.value = false
  }
}

/** 全站数据导出为Excel */
async function exportFullDataToExcel(sitesData, platformData, relationData) {
  const XLSX = await import('xlsx')
  const wb = XLSX.utils.book_new()
  
  // 网站列表
  const sitesWs = XLSX.utils.json_to_sheet(sitesData)
  sitesWs['!cols'] = [
    { wch: 12 }, { wch: 20 }, { wch: 15 }, { wch: 30 }, { wch: 8 }
  ]
  XLSX.utils.book_append_sheet(wb, sitesWs, '网站列表')
  
  // AI平台配置数据
  const platformWs = XLSX.utils.json_to_sheet(platformData)
  platformWs['!cols'] = [
    { wch: 15 }, { wch: 12 }, { wch: 20 }, { wch: 15 },
    { wch: 15 }, { wch: 30 }, { wch: 40 }, { wch: 20 },
    { wch: 30 }, { wch: 10 }, { wch: 10 }, { wch: 10 }, { wch: 8 },
    { wch: 8 }, { wch: 30 }
  ]
  XLSX.utils.book_append_sheet(wb, platformWs, 'AI平台配置数据')
  
  // 网站关联
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    relationWs['!cols'] = [
      { wch: 15 }, { wch: 10 }, { wch: 12 }, { wch: 10 }
    ]
    XLSX.utils.book_append_sheet(wb, relationWs, '网站关联')
  }
  
  const fileName = `AI平台配置全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 全站数据导出为JSON */
function exportFullDataToJSON(sitesData, platformData, relationData) {
  const exportData = {
    sites: sitesData,
    platforms: platformData,
    relations: relationData,
    exportTime: new Date().toISOString()
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `AI平台配置全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
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

/** 全站导入 */
function handleFullImport() {
  fullImportDialogOpen.value = true
  fullImportSites.value = []
  fullImportPlatforms.value = []
  fullImportRelations.value = []
  fullImportFile.value = null
  siteMapping.value = {}
  hasDefaultConfig.value = false
  createDefaultAsNewSite.value = true
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
    fullImportPlatforms.value = parsedData.platforms || []
    fullImportRelations.value = parsedData.relations || []
    
    hasDefaultConfig.value = fullImportPlatforms.value.some(p => p['网站虚拟ID'] === 0 || p['网站编码'] === 'default')
    
    initializeSiteMapping()
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + error.message)
  }
}

/** 初始化网站映射 */
function initializeSiteMapping() {
  const mapping = {}
  
  fullImportSites.value.forEach(importSite => {
    const siteVirtualId = importSite['网站虚拟ID'] || 0
    const siteCode = importSite['网站编码']
    
    if (siteVirtualId > 0) {
      const matchingSite = siteList.value.find(s => s.code === siteCode)
      if (matchingSite) {
        mapping[siteVirtualId] = matchingSite.id
      }
    }
  })
  
  siteMapping.value = mapping
}

/** 解析全站导入Excel数据 */
async function parseFullImportExcelData(fileData) {
  const XLSX = await import('xlsx')
  const workbook = XLSX.read(fileData, { type: 'array' })
  
  const result = {
    sites: [],
    platforms: [],
    relations: []
  }
  
  if (workbook.SheetNames.includes('网站列表')) {
    const siteSheet = workbook.Sheets['网站列表']
    result.sites = XLSX.utils.sheet_to_json(siteSheet)
  }
  
  if (workbook.SheetNames.includes('AI平台配置数据')) {
    const platformSheet = workbook.Sheets['AI平台配置数据']
    result.platforms = XLSX.utils.sheet_to_json(platformSheet)
  }
  
  if (workbook.SheetNames.includes('网站关联')) {
    const relationSheet = workbook.Sheets['网站关联']
    result.relations = XLSX.utils.sheet_to_json(relationSheet)
  }
  
  return result
}

/** 解析全站导入JSON数据 */
function parseFullImportJSONData(fileData) {
  const jsonData = JSON.parse(fileData)
  
  if (!jsonData.sites || !Array.isArray(jsonData.sites)) {
    throw new Error('缺少网站列表数据')
  }
  
  return {
    sites: jsonData.sites || [],
    platforms: jsonData.platforms || [],
    relations: jsonData.relations || []
  }
}

/** 处理全站导入文件移除 */
function handleFullImportFileRemove() {
  fullImportFile.value = null
  fullImportSites.value = []
  fullImportPlatforms.value = []
  fullImportRelations.value = []
  siteMapping.value = {}
  hasDefaultConfig.value = false
}

/** 验证全站导入映射是否完整 */
const isFullImportMappingValid = computed(() => {
  const realSites = fullImportSites.value.filter(s => s['网站虚拟ID'] > 0)
  
  if (hasDefaultConfig.value && createDefaultAsNewSite.value) {
    if (!siteMapping.value[0]) {
      return false
    }
  }
  
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
  const virtualIdToNewIdMap = {}
  
  Object.values(siteMapping.value).forEach(targetSiteId => {
    virtualIdToNewIdMap[targetSiteId] = {}
  })
  
  // 1. 按网站分组AI平台配置
  const platformsBySite = {}
  
  fullImportPlatforms.value.forEach(platform => {
    const siteVirtualId = platform['网站虚拟ID'] || 0
    const virtualId = platform['AI平台虚拟ID']
    
    const targetSiteId = siteMapping.value[siteVirtualId]
    
    if (targetSiteId !== undefined && virtualId) {
      if (!platformsBySite[targetSiteId]) {
        platformsBySite[targetSiteId] = []
      }
      platformsBySite[targetSiteId].push({
        ...platform,
        targetSiteId,
        virtualId
      })
    }
  })
  
  // 2. 按目标网站导入AI平台配置
  for (const [targetSiteId, platforms] of Object.entries(platformsBySite)) {
    await importPlatformsForSite(targetSiteId, platforms, virtualIdToNewIdMap[targetSiteId] || {})
  }
  
  // 3. 先处理默认配置关联（如果需要）
  if (hasDefaultConfig.value && createDefaultAsNewSite.value) {
    await createRelationsForConvertedDefault(virtualIdToNewIdMap)
  }
  
  // 4. 处理剩余的关联关系
  if (fullImportRelations.value.length > 0) {
    await importRelations(virtualIdToNewIdMap)
  }
}

/** 为指定网站导入AI平台配置 */
async function importPlatformsForSite(targetSiteId, platforms, virtualIdMap) {
  for (const platform of platforms) {
    try {
      const categoryId = platform['分类名称'] ? 
        platformCategories.value.find(c => c.name === platform['分类名称'])?.id : null
      
      // 处理JSON字段：确保导入的是有效的JSON字符串
      let availableModels = '[]'
      if (platform['可用模型']) {
        const modelsValue = platform['可用模型']
        if (typeof modelsValue === 'string') {
          // 如果是字符串，验证并使用
          const trimmed = modelsValue.trim()
          if (trimmed && trimmed !== '') {
            try {
              JSON.parse(trimmed)
              availableModels = trimmed
            } catch (e) {
              console.warn('无效的JSON格式，使用空数组:', trimmed)
              availableModels = '[]'
            }
          }
        } else if (Array.isArray(modelsValue)) {
          availableModels = JSON.stringify(modelsValue)
        }
      }
      
      const platformData = {
        name: platform['平台名称'],
        platformType: platform['平台类型'],
        siteId: parseInt(targetSiteId),
        categoryId: categoryId,
        baseUrl: platform['API地址'],
        apiKey: platform['API密钥'],
        defaultModel: platform['默认模型'],
        availableModels: availableModels,
        maxTokens: platform['最大Token'],
        temperature: platform['温度'],
        isDefault: normalizeStatus(platform['默认配置']),
        priority: parseInt(platform['优先级']) || 0,
        status: normalizeStatus(platform['状态']),
        description: platform['配置描述'] || ''
      }
      
      const response = await addPlatform(platformData)
      const newPlatformId = response.data
      
      if (!newPlatformId) {
        console.error('API返回的平台ID为空:', response)
        throw new Error('创建平台失败：未返回平台ID')
      }
      
      virtualIdMap[platform.virtualId] = newPlatformId
      console.log(`AI平台配置导入成功: ${platform['平台名称']} (虚拟ID: ${platform.virtualId}) -> ID: ${newPlatformId}`)
    } catch (error) {
      console.warn('导入AI平台配置失败:', platform, error)
      throw error
    }
  }
}

/** 为转换的默认配置创建关联关系 */
async function createRelationsForConvertedDefault(virtualIdToNewIdMap) {
  const defaultTargetSiteId = siteMapping.value[0]
  if (!defaultTargetSiteId) return
  
  const defaultVirtualIdMap = virtualIdToNewIdMap[defaultTargetSiteId]
  if (!defaultVirtualIdMap) return
  
  console.log('开始为转换的默认配置创建关联关系...')
  
  try {
    // 获取所有被映射导入的目标网站ID（排除默认配置映射的目标网站）
    const mappedTargetSiteIds = Object.entries(siteMapping.value)
      .filter(([key, value]) => key !== '0') // 排除默认配置
      .map(([key, value]) => parseInt(value))
      .filter(id => id > 0 && id !== parseInt(defaultTargetSiteId)) // 排除目标网站本身
    
    console.log('被映射导入的目标网站ID:', mappedTargetSiteIds)
    console.log(`找到 ${mappedTargetSiteIds.length} 个需要关联的网站:`, mappedTargetSiteIds)
    
    // 记录已处理的关联关系，用于后续从关联关系列表中移除
    const processedRelations = []
    
    // 为每个转换后的AI平台配置检查原来的关联和排除关系
    for (const [virtualId, newPlatformId] of Object.entries(defaultVirtualIdMap)) {
      // 查找原来的关联关系数据（包括排除和关联）
      const originalRelations = fullImportRelations.value.filter(rel => {
        const relVirtualId = rel['AI平台虚拟ID']
        
        // 从AI平台配置数据中获取创建者网站虚拟ID
        const platformConfig = fullImportPlatforms.value.find(p => 
          p['AI平台虚拟ID'] === relVirtualId
        )
        const creatorSiteVirtualId = platformConfig ? (platformConfig['网站虚拟ID'] || 0) : 0
        
        return relVirtualId === parseInt(virtualId) && creatorSiteVirtualId === 0
      })
      
      // 第一步：清理既有关联又有排除的数据，删除排除逻辑
      // 获取有关联关系的网站虚拟ID列表
      const includedSiteVirtualIds = new Set(
        originalRelations
          .filter(rel => (rel['关联类型'] || '关联') === '关联')
          .map(rel => rel['网站虚拟ID'] || 0)
          .filter(id => id > 0)
      )
      
      // 获取需要排除的网站虚拟ID列表（如果同时存在关联，则忽略排除）
      const excludedSiteVirtualIds = originalRelations
        .filter(rel => {
          const relationType = rel['关联类型'] || '关联'
          const siteVirtualId = rel['网站虚拟ID'] || 0
          // 只保留那些没有关联关系的排除记录
          return relationType === '排除' && !includedSiteVirtualIds.has(siteVirtualId)
        })
        .map(rel => rel['网站虚拟ID'] || 0)
        .filter(id => id > 0)
      
      console.log(`AI平台 ${newPlatformId} (虚拟ID: ${virtualId}): 有关联的网站虚拟ID:`, Array.from(includedSiteVirtualIds), '仅排除的网站虚拟ID:', excludedSiteVirtualIds)
      
      // 第二步 + 第三步：收集所有关联网站，一次性批量保存
      const includeSiteIdsForBatch = []
      
      // 处理显式关联关系
      for (const includedVId of includedSiteVirtualIds) {
        const targetSiteId = siteMapping.value[includedVId]
        if (!targetSiteId) {
          console.warn(`关联的网站虚拟ID ${includedVId} 未映射，跳过`)
          continue
        }
        includeSiteIdsForBatch.push(targetSiteId)
      }
      
      // 将剩余的排除逻辑逆向转换为关联逻辑
      for (const targetSiteId of mappedTargetSiteIds) {
        const isExcludedMapped = excludedSiteVirtualIds.some(excludedVId => {
          const mappedSiteId = siteMapping.value[excludedVId]
          return mappedSiteId && mappedSiteId === targetSiteId
        })
        const isAlreadyIncluded = Array.from(includedSiteVirtualIds).some(includedVId => {
          const mappedSiteId = siteMapping.value[includedVId]
          return mappedSiteId && mappedSiteId === targetSiteId
        })
        if (!isExcludedMapped && !isAlreadyIncluded) {
          includeSiteIdsForBatch.push(targetSiteId)
          console.log(`加入逆向关联: platform=${newPlatformId}, site=${targetSiteId}`)
        } else if (isExcludedMapped) {
          console.log(`跳过网站 ${targetSiteId}，因为它是被排除网站的映射目标`)
        } else {
          console.log(`跳过网站 ${targetSiteId}，因为已在显式关联中处理`)
        }
      }
      
      if (includeSiteIdsForBatch.length > 0) {
        try {
          await batchSaveAiPlatformSiteRelations({
            aiPlatformIds: [newPlatformId],
            includeSiteIds: includeSiteIdsForBatch
          })
          console.log(`关联关系批量保存成功: platform=${newPlatformId}, sites=`, includeSiteIdsForBatch)
        } catch (error) {
          console.warn(`关联关系批量保存失败: platform=${newPlatformId}`, error.message || '未知错误')
        }
      }
      
      // 记录已处理的关联关系（所有涉及该AI平台配置和默认配置的关联）
      originalRelations.forEach(rel => {
        processedRelations.push(rel)
      })
    }
    
    // 从关联关系列表中移除已处理的关联
    processedRelations.forEach(processedRel => {
      const index = fullImportRelations.value.findIndex(rel => 
        rel['AI平台虚拟ID'] === processedRel['AI平台虚拟ID'] && 
        rel['网站虚拟ID'] === processedRel['网站虚拟ID']
      )
      if (index !== -1) {
        fullImportRelations.value.splice(index, 1)
      }
    })
    
    console.log('默认配置关联关系处理完成')
  } catch (error) {
    console.error('处理默认配置关联关系失败:', error)
    throw error
  }
}

/** 处理普通关联关系 */
async function importRelations(virtualIdToNewIdMap) {
  console.log('开始处理剩余的关联关系...')
  
  // 按平台ID分组收集 includeSiteIds / excludeSiteIds
  const platformRelationsMap = new Map() // newPlatformId -> { includeSiteIds: [], excludeSiteIds: [] }
  
  for (const relation of fullImportRelations.value) {
    try {
      const platformVirtualId = relation['AI平台虚拟ID']
      const siteVirtualId = relation['网站虚拟ID']
      const relationType = relation['关联类型'] === '排除' ? 'exclude' : 'include'
      
      const targetSiteId = siteMapping.value[siteVirtualId]
      if (!targetSiteId) {
        console.warn(`跳过关联关系，网站虚拟ID ${siteVirtualId} 未映射:`, relation)
        continue
      }
      
      let newPlatformId = null
      let platformCreatorTargetSiteId = null
      for (const siteId in virtualIdToNewIdMap) {
        if (virtualIdToNewIdMap[siteId][platformVirtualId]) {
          newPlatformId = virtualIdToNewIdMap[siteId][platformVirtualId]
          platformCreatorTargetSiteId = parseInt(siteId)
          break
        }
      }
      
      if (!newPlatformId) {
        console.warn(`跳过关联关系，找不到虚拟ID ${platformVirtualId} 对应的AI平台:`, relation)
        continue
      }
      if (platformCreatorTargetSiteId === parseInt(targetSiteId)) {
        console.log(`跳过自关联: 平台所属网站 ${platformCreatorTargetSiteId} 等于目标网站 ${targetSiteId}`)
        continue
      }
      
      if (!platformRelationsMap.has(newPlatformId)) {
        platformRelationsMap.set(newPlatformId, { includeSiteIds: [], excludeSiteIds: [] })
      }
      const entry = platformRelationsMap.get(newPlatformId)
      const parsedTargetSiteId = parseInt(targetSiteId)
      if (relationType === 'exclude') {
        if (!entry.excludeSiteIds.includes(parsedTargetSiteId)) entry.excludeSiteIds.push(parsedTargetSiteId)
      } else {
        if (!entry.includeSiteIds.includes(parsedTargetSiteId)) entry.includeSiteIds.push(parsedTargetSiteId)
      }
    } catch (error) {
      console.warn('处理关联关系失败:', relation, error)
    }
  }
  
  // 按平台批量保存
  for (const [newPlatformId, { includeSiteIds, excludeSiteIds }] of platformRelationsMap) {
    try {
      await batchSaveAiPlatformSiteRelations({
        aiPlatformIds: [newPlatformId],
        includeSiteIds,
        excludeSiteIds
      })
      console.log(`关联关系导入成功: platform=${newPlatformId}`)
    } catch (error) {
      console.warn('导入关联关系失败:', newPlatformId, error)
    }
  }
  
  console.log('关联关系导入完成')
}

/** 标准化状态值 */
function normalizeStatus(value) {
  if (value === '启用' || value === '1' || value === 1 || value === true) return '1'
  if (value === '禁用' || value === '0' || value === 0 || value === false) return '0'
  return '1'
}

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  if (siteList.value.length > 0) {
    loadPlatformCategories()
    getList()
  }
})

// 初始化：先加载网站列表，然后由 loadSiteList 自动加载数据
loadSiteList(() => {
  const restored = restoreViewModeSiteSelection(siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  viewMode.value = restored.viewMode
  queryParams.value.siteId = restored.siteId
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  // 加载分类列表
  loadPlatformCategories()
  getList()
})
</script>
