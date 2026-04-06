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
            v-for="category in toolCategories" 
            :key="category.id" 
            :label="category.name" 
            :value="category.id"
          >
            <span v-if="category.icon" style="margin-right: 8px;">{{ category.icon }}</span>
            <span>{{ category.name }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="工具名称" prop="toolName">
        <el-input
          v-model="queryParams.toolName"
          placeholder="请输入工具名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工具类型" prop="toolType">
        <el-select v-model="queryParams.toolType" placeholder="请选择工具类型" clearable style="width: 150px">
          <el-option label="AI工具" value="ai" />
          <el-option label="API工具" value="api" />
          <el-option label="集成工具" value="integration" />
          <el-option label="系统工具" value="system" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
      <el-form-item v-if="viewMode === 'creator'">
        <el-button type="success" plain icon="Download" @click="handleFullExport" v-hasPermi="['gamebox:atomicTool:export']">全站导出</el-button>
        <el-button type="warning" plain icon="Upload" @click="handleFullImport" v-hasPermi="['gamebox:atomicTool:import']">全站导入</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['gamebox:atomicTool:add']"
        >新增工具</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-dropdown @command="handleCreateFromTemplate" v-hasPermi="['gamebox:atomicTool:add']">
          <el-button type="success" plain icon="DocumentCopy">
            从模板创建<el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="ai_article_generator">
                <el-icon><MagicStick /></el-icon> AI文章生成器
              </el-dropdown-item>
              <el-dropdown-item command="text_optimizer">
                <el-icon><EditPen /></el-icon> 文本优化器
              </el-dropdown-item>
              <el-dropdown-item command="seo_optimizer">
                <el-icon><Search /></el-icon> SEO优化器
              </el-dropdown-item>
              <el-dropdown-item command="image_searcher">
                <el-icon><Picture /></el-icon> 图片搜索器
              </el-dropdown-item>
              <el-dropdown-item command="json_field_filter">
                <el-icon><Filter /></el-icon> JSON字段筛选器
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['gamebox:atomicTool:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gamebox:atomicTool:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="CircleClose"
          :disabled="multiple"
          @click="handleBatchExclude"
          v-hasPermi="['gamebox:atomicTool:edit']"
        >批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Link"
          :disabled="multiple"
          @click="handleBatchRelation"
          v-hasPermi="['gamebox:atomicTool:edit']"
        >批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Download"
          :disabled="multiple"
          @click="handleExport"
          v-hasPermi="['gamebox:atomicTool:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Upload"
          @click="handleImport"
          v-hasPermi="['gamebox:atomicTool:import']"
        >导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-alert
      title="💡 原子工具说明"
      type="info"
      :closable="false"
      style="margin-bottom: 15px"
    >
      <template #default>
        <div style="line-height: 1.8;">
          <strong>原子工具</strong>是独立的、可复用的功能单元，完成单一明确的任务（如AI生成、翻译、图片搜索）<br/>
          这些工具可以在<strong>工作流</strong>中组合使用，也可以在<strong>编辑器</strong>中直接调用<br/>
          <strong>🤖 AI工具</strong>：完全自定义，配置AI平台/模型/提示词，调用AI服务生成内容<br/>
          <strong>🔌 API工具</strong>：完全自定义，配置任意HTTP接口，灵活对接外部REST API<br/>
          <strong>🔗 集成工具</strong>：系统预置的外部服务集成（如Git文件读取），逻辑固定，每网站限一个实例<br/>
          <strong>⚡ 系统工具</strong>：系统内置操作（如保存文章、导入游戏），无外部网络调用，每网站限一个实例
        </div>
      </template>
    </el-alert>

    <el-table ref="atomicToolTableRef" v-loading="loading" :data="toolList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="工具名称" align="center" prop="toolName" :show-overflow-tooltip="true" />
      <el-table-column label="创建者网站" align="center" prop="siteId" width="120">
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
          <CategoryTag v-if="scope.row.categoryId" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'atomic_tool', icon: scope.row.categoryIcon }" />
          <span v-else style="color: #909399">-</span>
        </template>
      </el-table-column>
      <el-table-column label="工具类型" align="center" prop="toolType" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.toolType === 'ai'" type="primary">🤖 AI工具</el-tag>
          <el-tag v-else-if="scope.row.toolType === 'api'" type="success">🔌 API工具</el-tag>
          <el-tag v-else-if="scope.row.toolType === 'integration'" type="">🔗 集成工具</el-tag>
          <el-tag v-else-if="scope.row.toolType === 'system'" type="warning">⚡ 系统工具</el-tag>
          <el-tag v-else-if="scope.row.toolType === 'builtin'" type="warning">⚡ 系统工具</el-tag>
          <el-tag v-else type="warning">{{ scope.row.toolType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="可见性" align="center" width="100" v-if="viewMode === 'related' && queryParams.siteId && !isPersonalSiteCheck(queryParams.siteId)">
        <template #header>
          <el-tooltip placement="top" effect="light">
            <template #content>
              <div style="max-width: 300px; line-height: 1.6;">
                <p style="margin: 0 0 8px 0; font-weight: bold;">可见性控制说明：</p>
                <p style="margin: 0 0 4px 0;">• <strong>默认配置</strong>：切换会调用排除管理，排除后不显示</p>
                <p style="margin: 0 0 4px 0;">• <strong>自有数据</strong>：切换会修改启用/禁用状态</p>
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
      <el-table-column label="状态" align="center" prop="enabled" width="120" v-if="viewMode === 'creator'">
        <template #default="scope">
          <el-tag :type="scope.row.enabled === '1' ? 'success' : 'danger'">
            {{ scope.row.enabled === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200" fixed="right">
        <template #default="scope">
          <!-- 关联模式下的操作 - 基于relationSource统一判断 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的配置 - 可修改、删除 -->
            <template v-if="scope.row.relationSource === 'own'">
              <el-tooltip content="测试工具" placement="top">
                <el-button 
                  link 
                  type="primary" 
                  icon="VideoPlay" 
                  @click="handleTest(scope.row)" 
                  v-hasPermi="['gamebox:atomicTool:edit']"
                />
              </el-tooltip>
              <el-tooltip content="修改" placement="top">
                <el-button 
                  link 
                  type="primary" 
                  icon="Edit" 
                  @click="handleUpdate(scope.row)" 
                  v-hasPermi="['gamebox:atomicTool:edit']"
                />
              </el-tooltip>
              <el-tooltip content="网站关联" placement="top">
                <el-button 
                  link 
                  type="warning" 
                  icon="Link" 
                  @click="handleManageSites(scope.row)" 
                  v-hasPermi="['gamebox:atomicTool:edit']"
                />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Delete" 
                  @click="handleDelete(scope.row)" 
                  v-hasPermi="['gamebox:atomicTool:remove']"
                />
              </el-tooltip>
            </template>
            <!-- default: 默认配置 - 注意：排除/恢复功能已统一到"批量排除"按钮中管理 -->
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tooltip content="测试工具" placement="top">
                <el-button link type="primary" icon="VideoPlay" @click="handleTest(scope.row)" v-hasPermi="['gamebox:atomicTool:edit']" />
              </el-tooltip>
              <el-tag v-if="scope.row.isExcluded" type="info" size="small">已排除</el-tag>
            </template>
            <!-- shared: 其他网站分享 - 可移除关联 -->
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-tooltip content="测试工具" placement="top">
                <el-button link type="primary" icon="VideoPlay" @click="handleTest(scope.row)" v-hasPermi="['gamebox:atomicTool:edit']" />
              </el-tooltip>
              <el-tooltip content="移除关联" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Close" 
                  @click="handleRemoveFromSite(scope.row)" 
                  v-hasPermi="['gamebox:atomicTool:remove']"
                />
              </el-tooltip>
            </template>
          </template>
          <!-- 创建者模式下的操作 -->
          <template v-else>
            <el-tooltip content="测试工具" placement="top">
              <el-button link type="primary" icon="VideoPlay" @click="handleTest(scope.row)" v-hasPermi="['gamebox:atomicTool:edit']" />
            </el-tooltip>
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:atomicTool:edit']" />
            </el-tooltip>
            <!-- 非默认配置：显示网站关联 -->
            <el-tooltip content="网站关联" placement="top" v-if="!isPersonalSiteCheck(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="Link" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:atomicTool:edit']"
              />
            </el-tooltip>
            <!-- 默认配置：管理排除 -->
            <el-tooltip content="管理排除" placement="top" v-if="isPersonalSiteCheck(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="CircleClose" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:atomicTool:edit']"
              />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:atomicTool:remove']" />
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>

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
      entity-type="atomicTool"
      :entity-id="currentToolIdForSites"
      :entity-name="currentToolNameForSites"
      :creator-site-id="currentToolCreatorSiteId"
      @refresh="getList"
    />

    <!-- 排除网站管理对话框 -->
    <el-dialog
      title="管理排除网站"
      v-model="exclusionDialogVisible"
      width="600px"
      append-to-body
    >
      <el-alert
        :title="'选择要排除原子工具「' + (currentToolForExclusion?.toolName || '') + '」的网站'"
        type="info"
        :closable="false"
        style="margin-bottom: 15px"
      >
        <template #default>
          <div>被排除的网站将无法看到此默认配置的原子工具</div>
        </template>
      </el-alert>
      
      <el-checkbox-group v-model="selectedExcludedSites" style="display: flex; flex-direction: column;">
        <el-checkbox 
          v-for="site in exclusionSiteList" 
          :key="site.id" 
          :label="site.id"
          style="margin: 8px 0;"
        >
          {{ site.name }}
        </el-checkbox>
      </el-checkbox-group>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="exclusionDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitExclusions">确 定</el-button>
        </div>
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选原子工具）</p>
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
          <strong>已选原子工具：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedToolsForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="tool in selectedToolsForBatchExclude" 
            :key="tool.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeToolFromBatchExclude(tool.id)"
            size="small"
          >
            {{ tool.toolName }}
          </el-tag>
        </div>
      </div>
      
      <el-collapse v-if="toolExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各工具当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="toolExclusionDetails" size="small" stripe>
              <el-table-column label="工具名称" prop="toolName" width="150" show-overflow-tooltip />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选原子工具对该网站可见）</p>
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
          <strong>已选原子工具：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedToolsForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="tool in selectedToolsForBatchRelation" 
            :key="tool.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeToolFromBatchRelation(tool.id)"
            size="small"
          >
            {{ tool.toolName }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各工具的当前关联/排除状态 -->
      <el-collapse v-if="toolRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各工具当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="toolRelationDetails" size="small" stripe>
              <el-table-column label="工具名称" prop="toolName" width="150" show-overflow-tooltip />
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

    <!-- 添加或修改原子工具对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body :close-on-click-modal="false">
      <el-form ref="toolRef" :model="form" :rules="rules" label-width="120px">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-form-item label="工具代码" prop="toolCode">
              <!-- ai/api 类型：用户自定义 code -->
              <el-input
                v-if="form.toolType !== 'system' && form.toolType !== 'integration'"
                v-model="form.toolCode"
                placeholder="唯一标识，如: ai_generator, translator"
                :disabled="form.id !== undefined"
              />
              <!-- system/integration 类型：由执行器选择自动赋值，不可手动输入 -->
              <el-input
                v-else
                v-model="form.toolCode"
                :disabled="true"
                placeholder="请在『工具配置』选项卡中选择执行器自动填充"
              />
            </el-form-item>
            <el-form-item label="工具名称" prop="toolName">
              <el-input v-model="form.toolName" placeholder="工具的显示名称" />
            </el-form-item>
            <el-form-item label="工具类型" prop="toolType">
              <el-select v-model="form.toolType" placeholder="请选择工具类型" style="width: 100%" :disabled="form.id !== undefined" @change="handleToolTypeChange">
                <el-option label="AI工具" value="ai">
                  <span>🤖 AI工具</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">- 调用AI平台生成内容</span>
                </el-option>
                <el-option label="API工具" value="api">
                  <span>🔌 API工具</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">- 调用外部REST API</span>
                </el-option>
                <el-option label="集成工具" value="integration">
                  <span>🔗 集成工具</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">- 系统预置外部服务集成</span>
                </el-option>
                <el-option label="系统工具" value="system">
                  <span>⚡ 系统工具</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">- 基于后端执行器的功能</span>
                </el-option>
              </el-select>
              <!-- 类型说明提示 -->
              <div style="margin-top: 8px; padding: 8px; background: #f4f4f5; border-radius: 4px; font-size: 12px; line-height: 1.6;">
                <div v-if="form.toolType === 'ai'" style="color: #606266;">
                  <strong>🤖 AI工具：</strong>需在"工具配置"标签页中配置AI平台、模型、提示词等参数。输入输出需手动定义。
                </div>
                <div v-else-if="form.toolType === 'api'" style="color: #606266;">
                  <strong>🔌 API工具：</strong>需配置API端点、请求方法、请求头、响应映射等。输入输出需手动定义。
                </div>
                <div v-else-if="form.toolType === 'integration'" style="color: #606266;">
                  <strong>🔗 集成工具：</strong>系统预置的外部服务集成，逻辑固定不可修改。选择执行器后参数自动填充，每网站同一执行器限一个实例。
                  <span v-if="!form.id" style="margin-left:4px;">👉 请切换到「工具配置」选项卡选择执行器</span>
                </div>
                <div v-else-if="form.toolType === 'system'" style="color: #e6a23c;">
                  <strong>⚡ 系统工具：</strong>系统内置操作工具，无外部网络调用，逻辑固定不可修改。选择执行器后参数自动填充，每网站同一执行器限一个实例。
                  <span v-if="!form.id" style="margin-left:4px;">👉 请切换到「工具配置」选项卡选择执行器</span>
                </div>
                <div v-else style="color: #909399;">
                  请先选择工具类型
                </div>
              </div>
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="3"
                placeholder="工具的功能描述"
              />
            </el-form-item>
            <el-form-item label="所属网站" prop="siteId" required>
              <SiteSelect
                v-model="form.siteId"
                :site-list="siteList"
                show-default
                default-label="默认配置（全局可用）"
                :disabled="form.id !== undefined"
                width="100%"
                placeholder="请选择所属网站"
              />
              <div v-if="form.id !== undefined" style="color: #909399; font-size: 12px; margin-top: 4px;">
                提示：所属网站创建后不可修改
              </div>
            </el-form-item>
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 100%">
                <el-option
                  v-for="category in toolCategories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                >
                  <span v-if="category.icon" style="margin-right: 8px;">{{ category.icon }}</span>
                  <span>{{ category.name }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="是否启用">
              <el-switch v-model="form.enabled" active-value="1" inactive-value="0"></el-switch>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="工具配置" name="config">
            <el-alert
              title="根据工具类型配置相应的参数"
              type="info"
              :closable="false"
              style="margin-bottom: 15px"
            />
            
            <!-- AI工具配置 -->
            <template v-if="form.toolType === 'ai'">
              <el-form-item label="AI平台">
                <el-select v-model="form.config.aiPlatformId" placeholder="选择AI平台" style="width: 100%" @change="handleAiPlatformChange">
                  <el-option
                    v-for="platform in platformList"
                    :key="platform.id"
                    :label="platform.name"
                    :value="platform.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="模型名称">
                <el-select 
                  v-if="availableModels.length > 0" 
                  v-model="form.config.modelName" 
                  placeholder="请选择模型" 
                  style="width: 100%"
                  filterable
                  allow-create
                >
                  <el-option
                    v-for="model in availableModels"
                    :key="model"
                    :label="model"
                    :value="model"
                  />
                </el-select>
                <el-input v-else v-model="form.config.modelName" placeholder="如: gpt-4, claude-3" />
              </el-form-item>
              <el-form-item label="系统提示词">
                <el-input
                  ref="systemPromptInput"
                  v-model="form.config.systemPrompt"
                  type="textarea"
                  :rows="4"
                  placeholder="定义AI的角色和行为，可使用 {{site.name}}、{{game.name}}、{{gamebox.name}} 等变量"
                />
                <el-button 
                  type="primary" 
                  link 
                  size="small" 
                  @click="showVariableSelector('systemPrompt')"
                  style="margin-top: 5px"
                >
                  <el-icon><Plus /></el-icon>
                  插入变量
                </el-button>
              </el-form-item>
              <el-form-item label="用户提示词模板">
                <el-input
                  ref="userPromptInput"
                  v-model="form.config.userPromptTemplate"
                  type="textarea"
                  :rows="4"
                  placeholder="使用 ${变量名} 引用输入参数，使用 {{site.name}}、{{game.name}} 等引用数据变量"
                />
                <el-button 
                  type="primary" 
                  link 
                  size="small" 
                  @click="showVariableSelector('userPromptTemplate')"
                  style="margin-top: 5px"
                >
                  <el-icon><Plus /></el-icon>
                  插入变量
                </el-button>
              </el-form-item>
              <el-form-item label="Temperature">
                <el-slider v-model="form.config.temperature" :min="0" :max="2" :step="0.1" show-input />
              </el-form-item>
              <el-form-item label="最大Token数">
                <el-input-number v-model="form.config.maxTokens" :min="100" :max="32000" style="width: 100%" />
              </el-form-item>
            </template>

            <!-- API工具配置 -->
            <template v-if="form.toolType === 'api'">
              <el-alert 
                title="提示：API响应数据将作为 response 字段返回，您可以在输出参数中定义期望的字段名称" 
                type="info" 
                :closable="false" 
                style="margin-bottom: 16px"
              />
              <el-form-item label="API端点" prop="config.apiUrl">
                <el-input v-model="form.config.apiUrl" placeholder="https://api.example.com/endpoint" />
              </el-form-item>
              <el-form-item label="请求方法">
                <el-select v-model="form.config.method" placeholder="请选择" style="width: 100%">
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                  <el-option label="PUT" value="PUT" />
                  <el-option label="DELETE" value="DELETE" />
                  <el-option label="PATCH" value="PATCH" />
                </el-select>
              </el-form-item>
              <el-form-item label="Content-Type">
                <el-input v-model="form.config.contentType" placeholder="application/json（可选）" />
              </el-form-item>
              <el-form-item label="请求头（可选）">
                <el-input
                  v-model="form.config.headers"
                  type="textarea"
                  :rows="4"
                  placeholder='JSON格式，如: {"Authorization": "Bearer token123"}'
                />
                <div style="color: #909399; font-size: 12px; margin-top: 4px;">
                  支持变量：使用 ${变量名} 引用输入参数
                </div>
              </el-form-item>
              <el-form-item label="超时时间(ms)">
                <el-input-number v-model="form.config.timeout" :min="1000" :max="300000" :step="1000" style="width: 100%" />
              </el-form-item>
            </template>

            <!-- 系统工具 / 集成工具配置 -->
            <template v-if="form.toolType === 'system' || form.toolType === 'integration'">
              <el-form-item label="选择执行器" prop="toolCode">
                <el-select 
                  v-model="form.toolCode" 
                  placeholder="请选择系统执行器" 
                  style="width: 100%"
                  :disabled="form.id !== undefined"
                  @change="handleExecutorChange"
                  filterable
                >
                  <el-option
                    v-for="executor in filteredExecutors"
                    :key="executor.code"
                    :label="executor.name"
                    :value="executor.code"
                  >
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                      <span>{{ executor.name }}</span>
                      <span style="color: #909399; font-size: 12px;">{{ executor.code }}</span>
                    </div>
                  </el-option>
                </el-select>
                <div v-if="form.id !== undefined" style="color: #909399; font-size: 12px; margin-top: 4px;">
                  提示：执行器选择后不可修改
                </div>
              </el-form-item>
              
              <el-alert 
                v-if="selectedExecutor"
                :title="'执行器说明：' + selectedExecutor.description"
                type="info"
                :closable="false"
                style="margin-bottom: 15px"
              />

              <el-form-item label="其他配置">
                <el-input
                  v-model="form.config.otherConfig"
                  type="textarea"
                  :rows="4"
                  placeholder="JSON格式的其他配置（可选）"
                />
              </el-form-item>
            </template>
          </el-tab-pane>

          <el-tab-pane label="输入参数" name="inputs">
            <el-button @click="addInputParam" type="primary" size="small" icon="Plus">添加输入参数</el-button>
            <el-table :data="form.inputs" style="margin-top: 10px" max-height="400">
              <el-table-column label="参数名" width="120">
                <template #default="{ row, $index }">
                  <el-input v-model="row.name" size="small" placeholder="参数名" />
                </template>
              </el-table-column>
              <el-table-column label="显示名称" width="120">
                <template #default="{ row }">
                  <el-input v-model="row.label" size="small" placeholder="显示名称" />
                </template>
              </el-table-column>
              <el-table-column label="类型" width="120">
                <template #default="{ row }">
                  <el-select v-model="row.type" size="small">
                    <el-option label="字符串" value="string" />
                    <el-option label="文本" value="text" />
                    <el-option label="文本域" value="textarea" />
                    <el-option label="数字" value="number" />
                    <el-option label="布尔值" value="boolean" />
                    <el-option label="数组" value="array" />
                    <el-option label="对象" value="object" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="必需" width="80">
                <template #default="{ row }">
                  <el-checkbox v-model="row.required" />
                </template>
              </el-table-column>
              <el-table-column label="描述">
                <template #default="{ row }">
                  <el-input v-model="row.description" size="small" placeholder="参数说明" />
                </template>
              </el-table-column>
              <el-table-column label="默认值" width="100">
                <template #default="{ row }">
                  <el-input v-model="row.defaultValue" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" link @click="removeInputParam($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="输出参数" name="outputs">
            <el-button @click="addOutputParam" type="primary" size="small" icon="Plus">添加输出参数</el-button>
            <el-table :data="form.outputs" style="margin-top: 10px" max-height="400">
              <el-table-column label="参数名" width="120">
                <template #default="{ row }">
                  <el-input v-model="row.name" size="small" placeholder="参数名" />
                </template>
              </el-table-column>
              <el-table-column label="显示名称" width="120">
                <template #default="{ row }">
                  <el-input v-model="row.label" size="small" placeholder="显示名称" />
                </template>
              </el-table-column>
              <el-table-column label="类型" width="120">
                <template #default="{ row }">
                  <el-select v-model="row.type" size="small">
                    <el-option label="字符串" value="string" />
                    <el-option label="文本" value="text" />
                    <el-option label="文本域" value="textarea" />
                    <el-option label="数字" value="number" />
                    <el-option label="布尔值" value="boolean" />
                    <el-option label="数组" value="array" />
                    <el-option label="对象" value="object" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="描述">
                <template #default="{ row }">
                  <el-input v-model="row.description" size="small" placeholder="参数说明" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" link @click="removeOutputParam($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 测试工具对话框 -->
    <el-dialog 
      :title="`测试工具: ${currentTool?.toolName}`" 
      v-model="testDialogVisible" 
      width="700px"
      top="5vh"
    >
      <el-descriptions :column="1" border style="margin-bottom: 20px">
        <el-descriptions-item label="工具编码">{{ currentTool?.toolCode }}</el-descriptions-item>
        <el-descriptions-item label="工具类型">
          <el-tag v-if="currentTool?.toolType === 'ai'" type="primary">🤖 AI工具</el-tag>
          <el-tag v-else-if="currentTool?.toolType === 'api'" type="success">🔌 API工具</el-tag>
          <el-tag v-else-if="currentTool?.toolType === 'integration'" type="">🔗 集成工具</el-tag>
          <el-tag v-else-if="currentTool?.toolType === 'system'" type="warning">⚡ 系统工具</el-tag>
          <el-tag v-else type="warning">{{ currentTool?.toolType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工具描述">{{ currentTool?.description }}</el-descriptions-item>
      </el-descriptions>

      <!-- 参数输入区域（可滚动） -->
      <div v-if="currentTool?.inputs && currentTool.inputs.length > 0" style="max-height: 40vh; overflow-y: auto; padding-right: 10px; margin-bottom: 15px;">
        <el-form :model="testForm" label-width="120px">
          <el-form-item
            v-for="input in currentTool?.inputs"
            :key="input.name"
            :label="input.label || input.name"
            :required="input.required"
          >
            <el-input
              v-if="input.type === 'textarea'"
              v-model="testForm.params[input.name]"
              type="textarea"
              :rows="input.rows || 5"
              :placeholder="input.description || input.label"
            />
            <el-input
              v-else-if="input.type === 'text' || input.type === 'string'"
              v-model="testForm.params[input.name]"
              :placeholder="input.description || input.label"
            />
            <el-input-number
              v-else-if="input.type === 'number'"
              v-model="testForm.params[input.name]"
              :min="input.min"
              :max="input.max"
              style="width: 100%"
            />
            <el-switch
              v-else-if="input.type === 'boolean'"
              v-model="testForm.params[input.name]"
            />
            <span v-if="input.description" style="color: #909399; font-size: 12px; display: block; margin-top: 5px;">
              {{ input.description }}
            </span>
          </el-form-item>
        </el-form>
      </div>

      <!-- 测试按钮（固定位置） -->
      <el-button type="primary" @click="runTest" :loading="testing" style="width: 100%; margin-bottom: 15px">
        <el-icon><VideoPlay /></el-icon>
        运行测试
      </el-button>

      <!-- 测试结果区域（可滚动） -->
<!-- 测试结果区域（可滚动） -->
      <div v-if="testResult" style="margin-top: 20px;">
        <el-divider content-position="left">
          <span style="font-weight: 600; font-size: 14px;">测试结果</span>
        </el-divider>
        
        <!-- 成功结果 -->
        <div v-if="testResult.status === 'success'" style="background: #f0f9ff; border: 1px solid #91d5ff; border-radius: 8px; padding: 20px;">
          <!-- 工具栏 -->
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #d9ecff;">
            <div style="display: flex; align-items: center; gap: 12px;">
              <span style="color: #52c41a; font-size: 20px;">✓</span>
              <span style="font-weight: 600; color: #52c41a; font-size: 15px;">执行成功</span>
              <el-divider direction="vertical" style="height: 20px;" />
              <el-button-group size="small">
                <el-button 
                  :type="outputMode === 'fields' ? 'primary' : ''" 
                  @click="outputMode = 'fields'"
                  size="small"
                >
                  <el-icon style="margin-right: 4px;"><List /></el-icon>
                  字段模式
                </el-button>
                <el-button 
                  :type="outputMode === 'json' ? 'primary' : ''" 
                  @click="outputMode = 'json'"
                  size="small"
                >
                  <el-icon style="margin-right: 4px;"><Document /></el-icon>
                  JSON模式
                </el-button>
              </el-button-group>
            </div>
            <el-button size="small" @click="copyTestResult" type="primary" plain>
              <el-icon style="margin-right: 4px;"><DocumentCopy /></el-icon>
              复制结果
            </el-button>
          </div>
              
          <!-- 结果显示区域 -->
          <div style="max-height: 400px; overflow-y: auto; background: white; border-radius: 6px; padding: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.05);">
            <!-- 字段模式显示 -->
            <div v-if="outputMode === 'fields'">
              <div
                v-for="(output, index) in currentTool?.outputs"
                :key="output.name"
                :style="{
                  marginBottom: index < currentTool.outputs.length - 1 ? '20px' : '0',
                  paddingBottom: index < currentTool.outputs.length - 1 ? '20px' : '0',
                  borderBottom: index < currentTool.outputs.length - 1 ? '1px solid #f0f0f0' : 'none'
                }"
              >
                <div style="display: flex; align-items: flex-start; gap: 16px;">
                  <div style="min-width: 120px; padding-top: 8px;">
                    <div style="font-weight: 600; color: #262626; font-size: 14px; margin-bottom: 4px;">
                      {{ output.label || output.name }}
                    </div>
                    <el-tag size="small" type="info" effect="plain" style="font-size: 11px;">
                      {{ output.type }}
                    </el-tag>
                  </div>
                  <div style="flex: 1; min-width: 0;">
                    <el-input
                      v-if="output.type === 'text' || output.type === 'string'"
                      :value="getOutputValue(testResult.output, output.name)"
                      readonly
                      style="width: 100%;"
                    />
                    <el-input
                      v-else-if="output.type === 'textarea' || output.type === 'array' || output.type === 'object'"
                      type="textarea"
                      :value="formatOutputValue(testResult.output, output)"
                      readonly
                      :rows="output.rows || 6"
                      style="width: 100%;"
                    />
                    <el-input
                      v-else-if="output.type === 'number'"
                      :value="getOutputValue(testResult.output, output.name)"
                      readonly
                      style="width: 100%;"
                    />
                    <el-tag 
                      v-else-if="output.type === 'boolean'" 
                      :type="getOutputValue(testResult.output, output.name) ? 'success' : 'info'"
                      size="large"
                    >
                      {{ getOutputValue(testResult.output, output.name) ? '是' : '否' }}
                    </el-tag>
                    <el-input
                      v-else
                      :value="getOutputValue(testResult.output, output.name)"
                      readonly
                      style="width: 100%;"
                    />
                    <div v-if="output.description" style="color: #8c8c8c; font-size: 12px; margin-top: 6px; line-height: 1.5;">
                      {{ output.description }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
                
            <!-- JSON模式显示 -->
            <div v-else>
              <div style="margin-bottom: 12px;">
                <el-button-group size="small">
                  <el-button 
                    :type="jsonFormatted ? 'primary' : ''" 
                    @click="jsonFormatted = true" 
                    size="small"
                  >
                    格式化
                  </el-button>
                  <el-button 
                    :type="!jsonFormatted ? 'primary' : ''" 
                    @click="jsonFormatted = false" 
                    size="small"
                  >
                    紧凑
                  </el-button>
                </el-button-group>
              </div>
              <el-input
                type="textarea"
                :value="jsonFormatted ? JSON.stringify(testResult.output, null, 2) : JSON.stringify(testResult.output)"
                readonly
                :rows="18"
                style="font-family: 'Courier New', Consolas, monospace; font-size: 13px; line-height: 1.6;"
              />
            </div>
          </div>
        </div>

        <!-- 失败结果 -->
        <div v-else style="background: #fff2f0; border: 1px solid #ffccc7; border-radius: 8px; padding: 20px;">
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #ffccc7;">
            <span style="color: #ff4d4f; font-size: 20px;">✗</span>
            <span style="font-weight: 600; color: #ff4d4f; font-size: 15px;">执行失败</span>
          </div>
          <p style="color: #cf1322; margin: 0 0 16px 0; font-weight: 500; font-size: 14px;">
            {{ testResult.message }}
          </p>
          <div v-if="testResult.error" style="position: relative;">
            <el-button 
              size="small" 
              @click="copyErrorMessage" 
              type="danger" 
              plain
              style="position: absolute; right: 12px; top: 12px; z-index: 1;"
            >
              <el-icon style="margin-right: 4px;"><DocumentCopy /></el-icon>
              复制错误
            </el-button>
            <el-input
              type="textarea"
              :value="testResult.error"
              readonly
              :rows="10"
              style="font-family: 'Courier New', Consolas, monospace; font-size: 13px; line-height: 1.6; padding-right: 100px;"
            />
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 变量选择器对话框 -->
    <el-dialog
      v-model="variableSelectorVisible"
      title="选择变量"
      width="600px"
      append-to-body
    >
      <el-tabs v-model="variableCategory">
        <el-tab-pane label="输入参数" name="input">
          <el-space wrap v-if="form.inputs && form.inputs.length > 0">
            <el-button
              v-for="input in form.inputs"
              :key="input.name"
              size="small"
              @click="selectVariable('input', input.name)"
            >
              {{ input.label || input.name }}
              <el-tag size="small" type="info" style="margin-left: 5px;">{{ input.type }}</el-tag>
            </el-button>
          </el-space>
          <el-empty v-else description="暂无输入参数，请先在输入参数配置中添加" :image-size="100" />
        </el-tab-pane>
        <el-tab-pane label="网站变量" name="site">
          <el-space wrap>
            <el-button
              v-for="field in siteFields"
              :key="field.key"
              size="small"
              @click="selectVariable('site', field.key)"
            >
              {{ field.label }}
            </el-button>
          </el-space>
        </el-tab-pane>
        <el-tab-pane label="游戏变量" name="game">
          <el-space wrap>
            <el-button
              v-for="field in gameFields"
              :key="field.key"
              size="small"
              @click="selectVariable('game', field.key)"
            >
              {{ field.label }}
            </el-button>
          </el-space>
        </el-tab-pane>
        <el-tab-pane label="盒子变量" name="gamebox">
          <el-space wrap>
            <el-button
              v-for="field in gameboxFields"
              :key="field.key"
              size="small"
              @click="selectVariable('gamebox', field.key)"
            >
              {{ field.label }}
            </el-button>
          </el-space>
        </el-tab-pane>
      </el-tabs>
      
      <el-alert
        type="info"
        :closable="false"
        style="margin-top: 10px"
      >
        <template #title>
          <div>💡 变量使用说明</div>
        </template>
        <div style="font-size: 13px; line-height: 1.6;">
          • <strong>输入参数：</strong>使用 <code v-text="'{{paramName}}'"></code> 格式，从工具调用时传入<br/>
          • <strong>系统变量：</strong>使用 <code v-text="'{{category.field}}'"></code> 格式，从系统上下文获取<br/>
          • 点击按钮即可在光标位置插入变量，变量会在工具执行时自动替换为实际数据
        </div>
      </el-alert>
    </el-dialog>

    <!-- 全站导出对话框 -->
    <FullExportDialog
      v-model="fullExportDialogOpen"
      v-model:exportFormat="fullExportFormat"
      :loading="fullExportLoading"
      @confirm="confirmFullExport"
    >
      <template #exportTips>
        <p>• 全站导出将包含所有原子工具配置数据（默认配置 + 所有站点）</p>
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
        <p>• 原子工具配置数据：包含工具配置和关联关系</p>
        <p>• 原子工具配置不包含翻译数据</p>
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
        <p>• 将根据导入数据重建原子工具配置和关联关系</p>
        <p>• 请确保文件格式与导出的数据格式一致</p>
        <p>• 必填字段：工具名称、工具代码、工具类型</p>
      </template>
      <template #previewColumns>
        <el-table-column prop="toolName" label="工具名称" width="150" />
        <el-table-column prop="toolType" label="工具类型" width="120" />
        <el-table-column prop="siteCode" label="网站" width="100" />
        <el-table-column prop="enabled" label="状态" width="80" />
      </template>
    </ImportDialog>

    <!-- 全站导入对话框 -->
    <FullImportDialog
      v-model="fullImportDialogOpen"
      :loading="fullImportLoading"
      :siteList="siteList"
      :importSites="fullImportSites"
      :importData="fullImportTools"
      v-model:importTranslations="fullImportTranslations"
      v-model:importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      :dataLabel="'原子工具'"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #importTips>
        <p style="margin: 0;">从其他系统导入完整的原子工具配置数据，包括：</p>
        <ul style="margin: 5px 0; padding-left: 20px;">
          <li>原子工具配置基础数据</li>
          <li>网站关联关系</li>
          <li>默认配置排除关系</li>
        </ul>
      </template>
    </FullImportDialog>
  </div>
</template>

<script setup name="AtomicTool">
import { ref, computed, onMounted, nextTick, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, VideoPlay, DocumentCopy, QuestionFilled, View, Hide, Warning, Filter } from '@element-plus/icons-vue'
import { listAtomicTool, getAtomicTool, addAtomicTool, updateAtomicTool, delAtomicTool, executeAtomicToolById, getExecutors } from '@/api/gamebox/atomicTool'
import { listVisibleCategory } from '@/api/gamebox/category'
import { listPlatform, listVisiblePlatform } from '@/api/gamebox/platform'
import { getAtomicToolSites, getBatchAtomicToolSites, batchSaveAtomicToolSiteRelations, updateAtomicToolVisibility } from '@/api/gamebox/siteRelation'
import { useSiteSelection, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from '@/composables/useSiteSelection'
import SiteSelect from '@/components/SiteSelect/index.vue'
import SiteRelationManager from '@/components/SiteRelationManager'
import CategoryTag from '@/components/CategoryTag'
import { ExportDialog, ImportDialog, FullExportDialog, FullImportDialog } from "@/components/ImportExportDialogs"
import * as XLSX from 'xlsx'

const { proxy } = getCurrentInstance()

// 使用网站选择组合式函数
const { siteList, currentSiteId, loadSiteList, getSiteName } = useSiteSelection()
const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

const toolList = ref([])
const toolCategories = ref([]) // 原子工具分类列表
const availableExecutors = ref([]) // 可用的系统执行器列表
const selectedExecutor = computed(() => {
  return availableExecutors.value.find(e => e.code === form.value.toolCode)
})
// 按当前选择的工具类型过滤执行器（system 只展示 system 执行器，integration 只展示 integration 执行器）
const filteredExecutors = computed(() => {
  return availableExecutors.value.filter(e => !e.toolType || e.toolType === form.value.toolType)
})
const includeDefaultConfig = ref(false) // 是否包含默认配置
const viewMode = ref('creator') // 查看模式：creator-创建者, related-关联网站
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const activeTab = ref('basic')
const platformList = ref([])
const availableModels = ref([]) // 可用模型列表

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentToolIdForSites = ref(0)
const currentToolNameForSites = ref('')
const currentToolCreatorSiteId = ref(0)

// 排除管理相关
const exclusionDialogVisible = ref(false)
const currentToolForExclusion = ref(null)
const selectedExcludedSites = ref([])
const exclusionSiteList = ref([])

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const selectedToolsForBatchExclude = ref([])
const toolExclusionDetails = ref([]) // 各工具的排除详情
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const atomicToolTableRef = ref(null)

// batchExclusionConflictSiteIds：所有工具中存在 include 关联的网站ID集合（用于排除对话框冲突标识）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  toolExclusionDetails.value.forEach(detail => {
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
const selectedToolsForBatchRelation = ref([])
const toolRelationDetails = ref([]) // 各工具的关联详情

// 是否处于默认配置的批量关联管理
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId, siteList.value))

// 冲突网站 ID 集合（存在 exclude 关系的网站）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  toolRelationDetails.value.forEach(detail => {
    if (detail.excludedSiteIds) {
      detail.excludedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

// 测试相关
const testDialogVisible = ref(false)
const currentTool = ref(null)
const testing = ref(false)
const testResult = ref(null)
const jsonFormatted = ref(true) // JSON格式化显示
const outputMode = ref('fields') // 输出显示模式：fields-字段模式, json-JSON模式

// 变量选择器相关
const variableSelectorVisible = ref(false)
const variableCategory = ref('site')
const currentEditingField = ref('') // 当前正在编辑的字段：systemPrompt 或 userPromptTemplate

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
const fullImportTools = ref([]) // 导入的原子工具配置数据
const fullImportRelations = ref([]) // 导入的关联关系
const fullImportTranslations = ref([]) // 导入的翻译数据（工具不使用）
const fullImportFile = ref(null)
const siteMapping = ref({}) // 网站ID映射 {源网站ID: 目标网站ID}
const hasDefaultConfig = ref(false) // 是否包含默认配置(siteid=0)
const createDefaultAsNewSite = ref(true) // 是否将默认配置导入为新网站的配置（并创建关联）

// 可用变量字段定义
const siteFields = ref([
  { key: 'id', label: '网站ID' },
  { key: 'name', label: '网站名称' },
  { key: 'code', label: '站点编码' },
  { key: 'description', label: '网站描述' },
  { key: 'domain', label: '网站域名' },
  { key: 'seoTitle', label: 'SEO标题' },
  { key: 'seoKeywords', label: 'SEO关键词' },
  { key: 'seoDescription', label: 'SEO描述' }
])

const gameFields = ref([
  { key: 'id', label: '游戏ID' },
  { key: 'name', label: '游戏名称' },
  { key: 'subtitle', label: '游戏副标题' },
  { key: 'shortName', label: '游戏简称' },
  { key: 'description', label: '游戏描述' },
  { key: 'promotionDesc', label: '推广描述' },
  { key: 'category', label: '游戏分类' },
  { key: 'gameType', label: '游戏类型' },
  { key: 'developer', label: '开发者' },
  { key: 'tags', label: '游戏标签' }
])

const gameboxFields = ref([
  { key: 'id', label: '盒子ID' },
  { key: 'name', label: '盒子名称' },
  { key: 'description', label: '盒子描述' },
  { key: 'officialUrl', label: '官方链接' },
  { key: 'downloadUrl', label: '下载链接' },
  { key: 'features', label: '盒子特色' }
])

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  siteId: undefined,
  categoryId: undefined,
  toolCode: undefined,
  toolName: undefined,
  toolType: undefined,
  enabled: undefined
})

const form = ref({
  config: {},
  inputs: [],
  outputs: []
})

const testForm = ref({
  params: {}
})

const rules = {
  toolCode: [{ required: true, message: '工具代码不能为空', trigger: 'blur' }],
  toolName: [{ required: true, message: '工具名称不能为空', trigger: 'blur' }],
  toolType: [{ required: true, message: '工具类型不能为空', trigger: 'change' }],
  siteId: [{ required: true, message: '请选择所属网站', trigger: 'change' }]
}

// 工具模板定义
const toolTemplates = {
  ai_article_generator: {
    toolCode: 'ai_article_generator',
    toolName: 'AI文章生成器',
    toolType: 'ai',
    description: '根据标题使用AI生成完整文章内容，支持自定义字数和风格',
    enabled: '1',
    siteId: null,
    config: {
      aiPlatformId: null,
      modelName: 'gpt-4',
      systemPrompt: '你是一个专业的内容创作助手，擅长撰写高质量的文章。请根据用户提供的标题和要求，生成结构清晰、内容丰富的文章。',
      userPromptTemplate: '请根据以下标题生成一篇文章：\n\n标题：{{title}}\n\n要求：\n- 字数：{{wordCount}}字左右\n- 风格：{{style}}\n\n请确保文章结构完整，包含引言、正文和总结。',
      temperature: 0.7,
      maxTokens: 4000
    },
    inputs: [
      { name: 'title', type: 'string', required: true, description: '文章标题', defaultValue: '' },
      { name: 'wordCount', type: 'number', required: false, description: '目标字数', defaultValue: '1000' },
      { name: 'style', type: 'string', required: false, description: '写作风格（如：专业、轻松、幽默）', defaultValue: '专业' }
    ],
    outputs: [
      { name: 'content', type: 'text', description: '生成的文章内容' },
      { name: 'summary', type: 'string', description: '文章摘要（自动提取前200字）' },
      { name: 'wordCount', type: 'number', description: '实际生成的字数' }
    ]
  },
  text_optimizer: {
    toolCode: 'text_optimizer',
    toolName: '文本优化器',
    toolType: 'ai',
    description: '使用AI优化文本，改善语言表达、修正错误、提升可读性',
    enabled: '1',
    siteId: null,
    config: {
      aiPlatformId: null,
      modelName: 'gpt-4',
      systemPrompt: '你是一个专业的文本编辑助手，擅长优化文本质量。请保持原文的核心意思，改善语言表达，修正错误，提升可读性。',
      userPromptTemplate: '请优化以下文本：\n\n{{text}}\n\n优化方向：{{direction}}\n\n请返回优化后的文本。',
      temperature: 0.5,
      maxTokens: 4000
    },
    inputs: [
      { name: 'text', type: 'text', required: true, description: '需要优化的文本', defaultValue: '' },
      { name: 'direction', type: 'string', required: false, description: '优化方向（如：更简洁、更专业、更生动）', defaultValue: '提升整体质量' }
    ],
    outputs: [
      { name: 'optimizedText', type: 'text', description: '优化后的文本' },
      { name: 'changes', type: 'string', description: '主要改进说明' }
    ]
  },
  seo_optimizer: {
    toolCode: 'seo_optimizer',
    toolName: 'SEO优化器',
    toolType: 'ai',
    description: '分析并优化文章的SEO元素，包括标题、描述、关键词等',
    enabled: '1',
    siteId: null,
    config: {
      aiPlatformId: null,
      modelName: 'gpt-4',
      systemPrompt: '你是一个SEO专家，擅长优化网页内容以提高搜索引擎排名。请分析内容并提供SEO优化建议。',
      userPromptTemplate: '请为以下内容生成SEO优化建议：\n\n标题：{{title}}\n内容：{{content}}\n\n请提供：\n1. 优化后的标题\n2. Meta描述\n3. 关键词建议\n4. 优化建议',
      temperature: 0.6,
      maxTokens: 2000
    },
    inputs: [
      { name: 'title', type: 'string', required: true, description: '原标题', defaultValue: '' },
      { name: 'content', type: 'text', required: true, description: '文章内容', defaultValue: '' }
    ],
    outputs: [
      { name: 'optimizedTitle', type: 'string', description: '优化后的标题' },
      { name: 'metaDescription', type: 'string', description: 'Meta描述' },
      { name: 'keywords', type: 'string', description: '推荐关键词（逗号分隔）' },
      { name: 'suggestions', type: 'text', description: 'SEO优化建议' }
    ]
  },
  json_field_filter: {
    toolCode: 'json_field_filter',
    toolName: 'JSON字段筛选器',
    toolType: 'system',
    description: '从 JSON 数据中按指定字段和匹配条件筛选数据项，支持精确、包含、前后缀和正则匹配，兼容数组字段及逗号/竖线/点号分隔的字符串字段',
    enabled: '1',
    siteId: null,
    config: {
      otherConfig: ''
    },
    inputs: [
      { name: 'jsonData', type: 'textarea', required: true, label: 'JSON数据', description: '要筛选的 JSON 字符串' },
      { name: 'filterField', type: 'text', required: true, label: '筛选字段', description: '字段名，如 gametype / game_genre / 游戏分类 / genre_name' },
      { name: 'filterValue', type: 'text', required: true, label: '匹配值', description: '如 卡牌' },
      { name: 'matchMode', type: 'text', required: false, label: '匹配模式', description: 'contains（默认）/ equals / startsWith / endsWith / regex', defaultValue: 'contains' },
      { name: 'dataPath', type: 'text', required: false, label: '数据路径', description: 'JSON中数组的路径，如 items（为空时自动探测）' },
      { name: 'preserveWrapper', type: 'text', required: false, label: '保留外层结构', description: 'true（默认，保留 platform 等元数据）/ false（仅返回数组）', defaultValue: 'true' },
      { name: 'caseSensitive', type: 'text', required: false, label: '大小写敏感', description: 'true / false（默认 false）', defaultValue: 'false' }
    ],
    outputs: [
      { name: 'filteredData', type: 'textarea', label: '筛选结果(JSON)', description: '符合条件的数据，JSON 格式' },
      { name: 'filteredCount', type: 'number', label: '筛选数量', description: '符合条件的数据条数' },
      { name: 'totalCount', type: 'number', label: '总数量', description: '原始数据的总条目数' },
      { name: 'message', type: 'text', label: '结果消息', description: '操作结果描述' },
      { name: 'success', type: 'boolean', label: '是否成功', description: '是否成功完成筛选' }
    ]
  },
  image_searcher: {
    toolCode: 'image_searcher',
    toolName: '图片搜索器',
    toolType: 'api',
    description: '从图片API搜索相关图片，支持关键词和风格筛选',
    enabled: '1',
    siteId: null,
    config: {
      apiUrl: 'https://api.unsplash.com/search/photos',
      method: 'GET',
      contentType: 'application/json',
      headers: JSON.stringify({
        'Authorization': 'Client-ID ${unsplash_access_key}'
      }, null, 2),
      timeout: 10000
    },
    inputs: [
      { name: 'query', type: 'string', required: true, description: '搜索关键词', defaultValue: '' },
      { name: 'perPage', type: 'number', required: false, description: '每页返回数量', defaultValue: '10' },
      { name: 'orientation', type: 'string', required: false, description: '图片方向：landscape, portrait, squarish', defaultValue: 'landscape' }
    ],
    outputs: [
      { name: 'images', type: 'array', description: '图片URL列表' },
      { name: 'thumbnails', type: 'array', description: '缩略图URL列表' },
      { name: 'total', type: 'number', description: '搜索结果总数' }
    ]
  }
}

/** 从模板创建 */
function handleCreateFromTemplate(templateKey) {
  const defaultSiteId = queryParams.value.siteId !== undefined ? queryParams.value.siteId : personalSiteId.value
  reset(defaultSiteId)
  
  const template = toolTemplates[templateKey]
  if (!template) {
    ElMessage.error('模板不存在')
    return
  }
  
  // 深拷贝模板数据到表单
  form.value = {
    ...template,
    config: JSON.parse(JSON.stringify(template.config)),
    inputs: JSON.parse(JSON.stringify(template.inputs)),
    outputs: JSON.parse(JSON.stringify(template.outputs)),
    siteId: defaultSiteId
  }
  
  open.value = true
  title.value = `从模板创建：${template.toolName}`
  activeTab.value = 'basic'
  
  ElMessage.success({
    message: '已加载模板，请根据需要修改配置后保存',
    duration: 3000
  })
}

/** 加载分类列表 */
async function loadToolCategories() {
  try {
    const response = await listVisibleCategory({
      categoryType: 'atomic_tool',
      siteId: queryParams.value.siteId || personalSiteId.value
    })
    toolCategories.value = response.rows || response.data || []
  } catch (error) {
    console.error('加载原子工具分类失败:', error)
  }
}

/** 加载系统执行器列表 */
async function loadExecutors() {
  try {
    const response = await getExecutors()
    availableExecutors.value = response.data || []
    console.log('加载系统执行器列表:', availableExecutors.value)
  } catch (error) {
    console.error('加载执行器列表失败:', error)
  }
}

/** 获取网站编码 */
function getSiteCode(siteId) {
  if (isPersonalSite(siteId, siteList.value)) return 'default'
  const site = siteList.value.find(s => s.id === siteId)
  return site ? site.code : ''
}

/** 网站切换事件 */
function handleSiteChange() {
  if (!queryParams.value.siteId || isPersonalSite(queryParams.value.siteId, siteList.value)) {
    includeDefaultConfig.value = false
  }
  queryParams.value.pageNum = 1
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  loadToolCategories()
  getList()
}

/** 查看模式切换事件 */
function handleViewModeChange() {
  queryParams.value.siteId = resolveSiteIdByViewMode(viewMode.value, siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  includeDefaultConfig.value = false
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  loadToolCategories()
  queryParams.value.pageNum = 1
  getList()
}

/** 查询原子工具列表 */
function getList() {
  loading.value = true
  
  const params = { 
    ...queryParams.value,
    queryMode: viewMode.value,
    includeDefault: includeDefaultConfig.value
  }
  
  if (params.siteId !== undefined) {
    listAtomicTool(params).then(response => {
      toolList.value = response.rows.map(row => {
        const mappedRow = {
          ...row,
          enabled: String(row.enabled),
          isVisible: row.isVisible !== undefined ? String(row.isVisible) : '1',
          relationType: row.relationType || 'include' // 确保 relationType 字段存在
        }
        // 调试：打印关联类型和可见性相关字段
        console.log('Row data:', {
          id: row.id,
          toolName: row.toolName,
          relationSource: row.relationSource,
          relationType: row.relationType,
          isVisible: row.isVisible,
          enabled: row.enabled
        })
        return mappedRow
      })
      total.value = response.total
      loading.value = false
    }).catch(error => {
      console.error('查询原子工具失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  } else {
    listAtomicTool({ ...params, siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      toolList.value = response.rows.map(row => ({
        ...row,
        enabled: String(row.enabled),
        isVisible: row.isVisible !== undefined ? String(row.isVisible) : '1'
      }))
      total.value = response.total
      loading.value = false
    }).catch(error => {
      console.error('查询原子工具失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  }
}

/** 加载AI平台列表 */
function loadPlatforms() {
  const siteId = form.value?.siteId || queryParams.value.siteId
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    listPlatform({ siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      platformList.value = response.rows || []
      if (form.value.config?.aiPlatformId) {
        handleAiPlatformChange()
      }
    })
  } else {
    listVisiblePlatform({ siteId: siteId }).then(response => {
      platformList.value = response.data || []
      if (form.value.config?.aiPlatformId) {
        handleAiPlatformChange()
      }
    })
  }
}

/** AI平台变化时加载模型列表 */
function handleAiPlatformChange() {
  const platform = platformList.value.find(p => p.id === form.value.config.aiPlatformId)
  if (platform && platform.availableModels) {
    try {
      const models = typeof platform.availableModels === 'string'
        ? JSON.parse(platform.availableModels)
        : platform.availableModels
      availableModels.value = Array.isArray(models) ? models : []
      
      // 如果当前模型不在列表中，设置为第一个模型
      if (availableModels.value.length > 0 && !form.value.config.modelName) {
        form.value.config.modelName = availableModels.value[0]
      }
    } catch (e) {
      console.error('解析模型列表失败:', e)
      availableModels.value = []
    }
  } else {
    availableModels.value = []
  }
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
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  // 根据当前查询条件设置默认的 siteId
  const defaultSiteId = queryParams.value.siteId !== undefined ? queryParams.value.siteId : personalSiteId.value
  reset(defaultSiteId)
  // 加载AI平台列表和执行器列表
  loadPlatforms()
  loadExecutors()
  open.value = true
  title.value = '添加原子工具'
  activeTab.value = 'basic'
}

/** 工具类型变更处理 */
function handleToolTypeChange(newType) {
  // 切换类型时全量清空，避免不同类型之间的状态污染
  form.value.config = {}
  form.value.inputs = []
  form.value.outputs = []
  form.value.toolCode = ''
  form.value.toolName = ''
  form.value.description = ''
  // system/integration 工具需在「工具配置」选项卡中选择执行器，自动跳转引导
  if (newType === 'system' || newType === 'integration') {
    nextTick(() => { activeTab.value = 'config' })
  }
}

/** 执行器选择变更处理 */
function handleExecutorChange(executorCode) {
  const executor = availableExecutors.value.find(e => e.code === executorCode)
  if (executor) {
    // 同步设置工具代码、名称、描述（工具代码由执行器 code 自动绑定）
    form.value.toolCode = executor.code
    form.value.toolName = executor.name
    form.value.description = executor.description
    // 解析并填充输入输出参数
    try {
      form.value.inputs = JSON.parse(executor.inputs || '[]')
      form.value.outputs = JSON.parse(executor.outputs || '[]')
    } catch (error) {
      console.error('解析执行器参数失败:', error)
    }
  }
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const toolId = row.id || ids.value[0]
  getAtomicTool(toolId).then(response => {
    form.value = response.data
    // 将 enabled 转换为字符串
    form.value.enabled = String(form.value.enabled)
    // 解析JSON字段
    if (typeof form.value.config === 'string') {
      form.value.config = JSON.parse(form.value.config || '{}')
    }
    if (typeof form.value.inputs === 'string') {
      form.value.inputs = JSON.parse(form.value.inputs || '[]')
    }
    if (typeof form.value.outputs === 'string') {
      form.value.outputs = JSON.parse(form.value.outputs || '[]')
    }
    
    // 对于API工具，格式化特殊字段为字符串以便编辑
    if (form.value.toolType === 'api' && form.value.config) {
      if (form.value.config.headers && typeof form.value.config.headers === 'object') {
        form.value.config.headers = JSON.stringify(form.value.config.headers, null, 2)
      }
    }
    
    // 加载AI平台列表和执行器列表（system/integration 工具需要执行器数据回显）
    loadPlatforms()
    loadExecutors()
    open.value = true
    title.value = '修改原子工具'
    activeTab.value = 'basic'
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['toolRef'].validate(valid => {
    if (valid) {
      // 深拷贝form数据
      const submitData = JSON.parse(JSON.stringify(form.value))
      
      // 对于API工具，将字符串字段转回对象
      if (submitData.toolType === 'api' && submitData.config) {
        try {
          if (typeof submitData.config.headers === 'string' && submitData.config.headers.trim()) {
            submitData.config.headers = JSON.parse(submitData.config.headers)
          }
        } catch (e) {
          ElMessage.error('请求头格式错误，请输入有效的JSON')
          return
        }
      }
      
      // 序列化JSON字段
      submitData.config = JSON.stringify(submitData.config)
      submitData.inputs = JSON.stringify(submitData.inputs)
      submitData.outputs = JSON.stringify(submitData.outputs)

      if (form.value.id) {
        updateAtomicTool(submitData).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addAtomicTool(submitData).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 管理网站关联 */
function handleManageSites(row) {
  currentToolIdForSites.value = row.id
  currentToolNameForSites.value = row.toolName
  currentToolCreatorSiteId.value = row.siteId || personalSiteId.value
  siteRelationDialogOpen.value = true
}

/** 排除默认配置 */
function handleExclude(row) {
  proxy.$modal.confirm(`确认要排除默认原子工具"${row.toolName}"吗？排除后该配置将不在当前网站显示。`).then(async () => {
    const siteId = queryParams.value.siteId
    const res = await getAtomicToolSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excluded.includes(siteId)) excluded.push(siteId)
    return batchSaveAtomicToolSiteRelations({ atomicToolIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess('排除成功')
    getList()
  }).catch(() => {})
}

/** 恢复默认配置 */
function handleRestore(row) {
  proxy.$modal.confirm(`确认要恢复默认原子工具"${row.toolName}"吗？恢复后该配置将重新在当前网站显示。`).then(async () => {
    const siteId = queryParams.value.siteId
    const res = await getAtomicToolSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== siteId)
    return batchSaveAtomicToolSiteRelations({ atomicToolIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess('恢复成功')
    getList()
  }).catch(() => {})
}

/** 提交排除设置 */
async function submitExclusions() {
  try {
    const toolId = currentToolForExclusion.value.id
    const res = await getAtomicToolSites(toolId)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    await batchSaveAtomicToolSiteRelations({
      atomicToolIds: [toolId],
      includeSiteIds: included,
      excludeSiteIds: selectedExcludedSites.value
    })
    proxy.$modal.msgSuccess('保存成功')
    exclusionDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('更新排除设置失败:', error)
    proxy.$modal.msgError('更新失败')
  }
}

// === 批量排除管理 ===
async function handleBatchExclude() {
  const selectedRows = toolList.value.filter(tool => ids.value.includes(tool.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的原子工具')
    return
  }
  
  // 只允许默认配置的工具进行批量排除
  const invalidTools = selectedRows.filter(tool => !isPersonalSite(tool.siteId, siteList.value))
  if (invalidTools.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的原子工具进行批量排除管理')
    return
  }
  
  selectedToolsForBatchExclude.value = selectedRows.map(tool => ({
    id: tool.id,
    toolName: tool.toolName
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中工具的网站关系
    const batchRes = await getBatchAtomicToolSites(selectedToolsForBatchExclude.value.map(t => t.id))
    const batchMap = batchRes.data || {}
    const results = selectedToolsForBatchExclude.value.map(tool => {
      const sites = batchMap[tool.id] || []
      return {
        toolId: tool.id,
        toolName: tool.toolName,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    toolExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有工具共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedToolsForBatchExclude.value.length) {
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

function removeToolFromBatchExclude(toolId) {
  selectedToolsForBatchExclude.value = selectedToolsForBatchExclude.value.filter(
    tool => tool.id !== toolId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== toolId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (atomicToolTableRef.value) {
    const row = toolList.value.find(tool => tool.id === toolId)
    if (row) {
      atomicToolTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  toolExclusionDetails.value = toolExclusionDetails.value.filter(
    detail => detail.toolId !== toolId
  )
  
  if (selectedToolsForBatchExclude.value.length === 0) {
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
  if (selectedToolsForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何原子工具')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一条请求处理所有选中工具的排除关系
    await batchSaveAtomicToolSiteRelations({
      atomicToolIds: selectedToolsForBatchExclude.value.map(t => t.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedToolsForBatchExclude.value.length} 个工具排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedToolsForBatchExclude.value.length} 个工具的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchAtomicToolSites(selectedToolsForBatchExclude.value.map(t => t.id))
    const refreshMap = refreshRes.data || {}
    toolExclusionDetails.value = selectedToolsForBatchExclude.value.map(tool => {
      const sites = refreshMap[tool.id] || []
      return {
        toolId: tool.id,
        toolName: tool.toolName,
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
  const selectedRows = toolList.value.filter(tool => ids.value.includes(tool.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的原子工具')
    return
  }
  
  const isDefaultConfig = isPersonalSite(queryParams.value.siteId, siteList.value)
  
  selectedToolsForBatchRelation.value = selectedRows.map(tool => ({
    id: tool.id,
    toolName: tool.toolName,
    siteId: tool.siteId
  }))
  
  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchAtomicToolSites(selectedToolsForBatchRelation.value.map(t => t.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedToolsForBatchRelation.value.map(tool => {
      const sites = batchMap2[tool.id] || []
      return {
        toolId: tool.id,
        toolName: tool.toolName,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== tool.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    toolRelationDetails.value = results
    
    // 找出被所有工具共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedToolsForBatchRelation.value.length) {
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

/** 从批量关联中移除某个工具 */
function removeToolFromBatchRelation(toolId) {
  selectedToolsForBatchRelation.value = selectedToolsForBatchRelation.value.filter(
    tool => tool.id !== toolId
  )
  
  ids.value = ids.value.filter(id => id !== toolId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  if (atomicToolTableRef.value) {
    const row = toolList.value.find(tool => tool.id === toolId)
    if (row) {
      atomicToolTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  toolRelationDetails.value = toolRelationDetails.value.filter(
    detail => detail.toolId !== toolId
  )
  
  if (selectedToolsForBatchRelation.value.length === 0) {
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
  if (selectedToolsForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何原子工具')
    return
  }
  
  batchRelationLoading.value = true
  
  try {
    // 一条请求处理所有选中工具的关联关系
    await batchSaveAtomicToolSiteRelations({
      atomicToolIds: selectedToolsForBatchRelation.value.map(t => t.id),
      includeSiteIds: batchRelatedSiteIds.value
    })
    
    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedToolsForBatchRelation.value.length} 个工具关联 ${relateCount} 个网站`
      : `成功取消 ${selectedToolsForBatchRelation.value.length} 个工具的所有共享关联`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchAtomicToolSites(selectedToolsForBatchRelation.value.map(t => t.id))
    const refreshMap2 = refreshRes2.data || {}
    toolRelationDetails.value = selectedToolsForBatchRelation.value.map(tool => {
      const sites = refreshMap2[tool.id] || []
      return {
        toolId: tool.id,
        toolName: tool.toolName,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== tool.siteId).map(s => s.siteId),
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

/** 从网站移除 */
function handleRemoveFromSite(row) {
  const currentQuerySiteId = queryParams.value.siteId
  const actionLabel = row.relationType === 'exclude' ? '取消排除' : '移除关联'
  const actionMessage = row.relationType === 'exclude'
    ? `确认要取消排除原子工具"${row.toolName}"吗？取消后该工具将对当前网站可见。`
    : `确认从"${getSiteName(currentQuerySiteId)}"移除该原子工具的关联吗？`
  
  proxy.$modal.confirm(actionMessage).then(async () => {
    const res = await getAtomicToolSites(row.id)
    const sites = res.data || []
    let included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    let excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (row.relationType === 'exclude') {
      excluded = excluded.filter(id => id !== currentQuerySiteId)
    } else {
      included = included.filter(id => id !== currentQuerySiteId)
    }
    return batchSaveAtomicToolSiteRelations({ atomicToolIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess(actionLabel + '成功')
    getList()
  }).catch(() => {})
}

/** 获取可见性值 */
function getVisibilityValue(row) {
  if (row.relationSource === 'default') {
    // 默认配置：使用 isVisible 判断是否被排除
    // isVisible === '0' 表示被排除（不显示）
    return row.isVisible || '1'
  } else if (row.relationSource === 'own') {
    // 自有数据：使用 enabled 状态
    return row.enabled
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
      const relationResponse = await getAtomicToolSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段（忽略排除逻辑）
        await updateAtomicToolVisibility(currentQuerySiteId, row.id, newValue)
        const action = newValue === '1' ? '显示' : '隐藏'
        proxy.$modal.msgSuccess(`${action}成功`)
        // 重新加载列表以更新统计信息
        getList()
      } else {
        // 没有关联记录：使用排除逻辑
        const included = relations.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
        let excluded = relations.filter(s => s.relationType === 'exclude').map(s => s.siteId)
        if (newValue === '0') {
          if (!excluded.includes(currentQuerySiteId)) excluded.push(currentQuerySiteId)
          proxy.$modal.msgSuccess('已排除该配置')
        } else {
          excluded = excluded.filter(id => id !== currentQuerySiteId)
          proxy.$modal.msgSuccess('已恢复该配置')
        }
        await batchSaveAtomicToolSiteRelations({ atomicToolIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
        // 重新加载列表以更新排除网站数量
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用状态
      const text = newValue === '1' ? '启用' : '禁用'
      await updateAtomicTool({ id: row.id, enabled: Number(newValue) })
      proxy.$modal.msgSuccess(`${text}成功`)
      // 重新加载列表以更新数据
      getList()
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updateAtomicToolVisibility(currentQuerySiteId, row.id, newValue)
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
function handleQuickToggleVisibility(row) {
  const currentQuerySiteId = queryParams.value.siteId
  const newVisibility = row.isVisible === '1' ? '0' : '1'
  const action = newVisibility === '1' ? '显示' : '隐藏'
  
  updateAtomicToolVisibility(currentQuerySiteId, row.id, newVisibility).then(() => {
    row.isVisible = newVisibility
    proxy.$modal.msgSuccess(`${action}成功`)
  }).catch(error => {
    console.error('更新可见性失败:', error)
    proxy.$modal.msgError('更新失败')
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const toolIds = row.id || ids.value
  ElMessageBox.confirm('是否确认删除选中的原子工具？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delAtomicTool(toolIds)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

/** 全站导出按钮操作 */
function handleFullExport() {
  fullExportDialogOpen.value = true
}

/** 全站导入按钮操作 */
function handleFullImport() {
  fullImportDialogOpen.value = true
}

/** 导出按钮操作 */
function handleExport() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning('请选择要导出的原子工具')
    return
  }
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  try {
    exportLoading.value = true
    
    // 获取选中的工具详细数据
    const selectedTools = toolList.value.filter(tool => ids.value.includes(tool.id))
    
    // 准备导出数据
    const exportData = selectedTools.map(tool => ({
      工具代码: tool.toolCode,
      工具名称: tool.toolName,
      工具类型: tool.toolType,
      分类名称: tool.categoryName || '',
      描述: tool.description,
      配置: typeof tool.config === 'string' ? tool.config : JSON.stringify(tool.config),
      输入参数: typeof tool.inputs === 'string' ? tool.inputs : JSON.stringify(tool.inputs),
      输出参数: typeof tool.outputs === 'string' ? tool.outputs : JSON.stringify(tool.outputs),
      是否启用: tool.enabled,
      排序号: tool.sortOrder,
      创建时间: tool.createTime
    }))
    
    // 根据格式导出
    if (exportFormat.value === 'excel') {
      const ws = XLSX.utils.json_to_sheet(exportData)
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, '原子工具')
      XLSX.writeFile(wb, `原子工具导出_${new Date().getTime()}.xlsx`)
    } else {
      const dataStr = JSON.stringify(exportData, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = `原子工具导出_${new Date().getTime()}.json`
      link.click()
      URL.revokeObjectURL(url)
    }
    
    proxy.$modal.msgSuccess('导出成功')
    exportDialogOpen.value = false
  } catch (error) {
    console.error('导出失败:', error)
    proxy.$modal.msgError('导出失败: ' + error.message)
  } finally {
    exportLoading.value = false
  }
}

/** 全站导出 */
async function confirmFullExport() {
  try {
    fullExportLoading.value = true
    
    // 1. 获取所有原子工具配置数据
    const toolsResponse = await listAtomicTool({ 
      queryMode: 'creator', 
      pageNum: 1, 
      pageSize: 9999 
    })
    const toolData = toolsResponse.rows || []
    
    // 2. 先收集所有涉及的网站ID
    const siteIds = new Set()
    toolData.forEach(tool => {
      if (tool.siteId !== null && tool.siteId !== undefined) {
        siteIds.add(tool.siteId)
      }
    })
    
    // 3. 获取所有关联关系，同时收集网站ID
    const relationDataRaw = []
    for (let index = 0; index < toolData.length; index++) {
      const tool = toolData[index]
      const virtualId = index + 1
      
      try {
        const response = await getAtomicToolSites(tool.id)
        const relations = response.data || []
        if (relations.length > 0) {
          relations.forEach(rel => {
            siteIds.add(rel.siteId)
            
            const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
            
            const relationData = {
              '原子工具虚拟ID': virtualId,
              工具代码: tool.toolCode,
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
        console.warn('获取原子工具关联失败:', tool.id, error)
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
    
    // 6. 格式化原子工具配置数据并使用虚拟ID
    const formattedToolData = toolData.map((tool, index) => {
      const virtualId = index + 1
      const virtualSiteId = siteIdToVirtualIdMap.get(tool.siteId) || 0
      
      return {
        '原子工具虚拟ID': virtualId,
        网站虚拟ID: virtualSiteId,
        工具代码: tool.toolCode,
        工具名称: tool.toolName,
        工具类型: tool.toolType,
        分类名称: tool.categoryName || '',
        描述: tool.description || '',
        配置: typeof tool.config === 'string' ? tool.config : JSON.stringify(tool.config || {}),
        输入参数: typeof tool.inputs === 'string' ? tool.inputs : JSON.stringify(tool.inputs || []),
        输出参数: typeof tool.outputs === 'string' ? tool.outputs : JSON.stringify(tool.outputs || []),
        是否启用: tool.enabled || '1',
        排序号: tool.sortOrder || 0,
        创建时间: tool.createTime || ''
      }
    })
    
    // 7. 导出数据
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, formattedToolData, relationData)
    } else {
      exportFullDataToJSON(sitesData, formattedToolData, relationData)
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
async function exportFullDataToExcel(sitesData, toolData, relationData) {
  const wb = XLSX.utils.book_new()
  
  // 网站列表
  const sitesWs = XLSX.utils.json_to_sheet(sitesData)
  sitesWs['!cols'] = [
    { wch: 12 }, { wch: 20 }, { wch: 15 }, { wch: 30 }, { wch: 8 }
  ]
  XLSX.utils.book_append_sheet(wb, sitesWs, '网站列表')
  
  // 原子工具配置数据
  const toolWs = XLSX.utils.json_to_sheet(toolData)
  toolWs['!cols'] = [
    { wch: 15 }, { wch: 12 }, { wch: 15 }, { wch: 20 }, { wch: 15 },
    { wch: 10 }, { wch: 30 }, { wch: 40 }, { wch: 40 },
    { wch: 40 }, { wch: 10 }, { wch: 8 }, { wch: 20 }
  ]
  XLSX.utils.book_append_sheet(wb, toolWs, '原子工具配置数据')
  
  // 网站关联
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    relationWs['!cols'] = [
      { wch: 15 }, { wch: 15 }, { wch: 10 }, { wch: 12 }, { wch: 10 }
    ]
    XLSX.utils.book_append_sheet(wb, relationWs, '网站关联')
  }
  
  const fileName = `原子工具全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 全站数据导出为JSON */
function exportFullDataToJSON(sitesData, toolData, relationData) {
  const exportData = {
    sites: sitesData,
    tools: toolData,
    relations: relationData,
    exportTime: new Date().toISOString()
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `原子工具全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
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

/** 导入按钮操作 */
function handleImport() {
  importDialogOpen.value = true
}

/** 处理文件变化 */
function handleFileChange(file) {
  importFile.value = file.raw
  const reader = new FileReader()
  
  reader.onload = (e) => {
    try {
      let data = []
      
      if (file.name.endsWith('.xlsx') || file.name.endsWith('.xls')) {
        const workbook = XLSX.read(e.target.result, { type: 'binary' })
        const firstSheet = workbook.Sheets[workbook.SheetNames[0]]
        data = XLSX.utils.sheet_to_json(firstSheet)
      } else if (file.name.endsWith('.json')) {
        data = JSON.parse(e.target.result)
      }
      
      // 转换数据格式用于预览
      importPreviewData.value = data.map(item => ({
        toolCode: item['工具代码'] || item.toolCode,
        toolName: item['工具名称'] || item.toolName,
        toolType: item['工具类型'] || item.toolType,
        categoryName: item['分类名称'] || item.categoryName || '',
        description: item['描述'] || item.description,
        config: item['配置'] || item.config,
        inputs: item['输入参数'] || item.inputs,
        outputs: item['输出参数'] || item.outputs,
        enabled: item['是否启用'] || item.enabled,
        sortOrder: item['排序号'] || item.sortOrder,
        siteCode: item['网站编码'] || item.siteCode
      }))
    } catch (error) {
      console.error('文件解析失败:', error)
      proxy.$modal.msgError('文件解析失败: ' + error.message)
      importPreviewData.value = []
    }
  }
  
  if (file.name.endsWith('.json')) {
    reader.readAsText(file.raw)
  } else {
    reader.readAsBinaryString(file.raw)
  }
}

/** 移除文件 */
function handleFileRemove() {
  importFile.value = null
  importPreviewData.value = []
}

/** 确认导入 */
async function confirmImport() {
  if (importPreviewData.value.length === 0) {
    proxy.$modal.msgError('没有可导入的数据')
    return
  }
  
  try {
    importLoading.value = true
    
    // 转换数据格式
    const importTools = importPreviewData.value.map(item => {
      // 根据分类名称查找分类ID
      let categoryId = null
      if (item.categoryName) {
        const category = toolCategories.value.find(cat => cat.name === item.categoryName)
        if (category) {
          categoryId = category.id
        }
      }
      return {
      toolCode: item.toolCode,
      toolName: item.toolName,
      toolType: item.toolType,
      categoryId: categoryId,
      description: item.description,
      config: typeof item.config === 'string' ? item.config : JSON.stringify(item.config),
      inputs: typeof item.inputs === 'string' ? item.inputs : JSON.stringify(item.inputs),
      outputs: typeof item.outputs === 'string' ? item.outputs : JSON.stringify(item.outputs),
      enabled: item.enabled,
      sortOrder: item.sortOrder,
      siteId: queryParams.value.siteId || personalSiteId.value
    }
    })
    
    // 批量添加
    for (const tool of importTools) {
      await addAtomicTool(tool)
    }
    
    proxy.$modal.msgSuccess(`成功导入 ${importTools.length} 个原子工具`)
    importDialogOpen.value = false
    handleFileRemove()
    getList()
  } catch (error) {
    console.error('导入失败:', error)
    proxy.$modal.msgError('导入失败: ' + error.message)
  } finally {
    importLoading.value = false
  }
}

/** 状态切换 */
function handleStatusChange(row) {
  const newStatus = row.enabled
  const oldStatus = newStatus === '1' ? '0' : '1'
  const text = newStatus === '1' ? '启用' : '停用'
  
  ElMessageBox.confirm('确认要"' + text + '""' + row.toolName + '"工具吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 将字符串转换为数字发送给后端
    return updateAtomicTool({ id: row.id, enabled: Number(newStatus) })
  }).then(() => {
    ElMessage.success(text + '成功')
  }).catch(() => {
    // 恢复开关状态
    row.enabled = oldStatus
  })
}

/** 测试工具 */
function handleTest(row) {
  currentTool.value = row
  
  // 解析inputs字段（如果是字符串）
  let inputs = row.inputs
  if (typeof inputs === 'string') {
    try {
      inputs = JSON.parse(inputs || '[]')
    } catch (e) {
      console.error('解析inputs失败:', e)
      inputs = []
    }
  }
  
  // 解析outputs字段（如果是字符串）
  let outputs = row.outputs
  if (typeof outputs === 'string') {
    try {
      outputs = JSON.parse(outputs || '[]')
    } catch (e) {
      console.error('解析outputs失败:', e)
      outputs = []
    }
  }
  
  // 将解析后的inputs和outputs存到currentTool
  currentTool.value = {
    ...row,
    inputs: inputs,
    outputs: outputs
  }
  
  // 初始化测试参数，填充默认值
  const params = {}
  if (inputs && Array.isArray(inputs)) {
    inputs.forEach(input => {
      if (input.default !== undefined) {
        params[input.name] = input.default
      } else if (input.type === 'number') {
        params[input.name] = 0
      } else if (input.type === 'boolean') {
        params[input.name] = false
      } else {
        params[input.name] = ''
      }
    })
  }
  
  testForm.value.params = params
  testResult.value = null
  testDialogVisible.value = true
}

/** 运行测试 */
function runTest() {
  testing.value = true
  testResult.value = null
  
  executeAtomicToolById(currentTool.value.id, testForm.value.params).then(response => {
    testing.value = false
    
    // 后端返回格式: { status, message, output }
    if (response.data.status === 'success') {
      testResult.value = {
        status: 'success',
        message: response.data.message,
        output: response.data.output
      }
      ElMessage.success('测试成功')
    } else {
      testResult.value = {
        status: 'error',
        message: response.data.message,
        error: response.data.error
      }
      ElMessage.error('测试失败: ' + response.data.message)
    }
  }).catch(error => {
    testing.value = false
    testResult.value = {
      status: 'error',
      message: '测试请求失败',
      error: error.message || String(error)
    }
    ElMessage.error('测试失败: ' + (error.message || '未知错误'))
  })
}

/** 添加输入参数 */
function addInputParam() {
  form.value.inputs.push({
    name: '',
    label: '',
    type: 'string',
    required: false,
    description: '',
    defaultValue: ''
  })
}

/** 移除输入参数 */
function removeInputParam(index) {
  form.value.inputs.splice(index, 1)
}

/** 添加输出参数 */
function addOutputParam() {
  form.value.outputs.push({
    name: '',
    label: '',
    type: 'string',
    description: ''
  })
}

/** 移除输出参数 */
function removeOutputParam(index) {
  form.value.outputs.splice(index, 1)
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset(defaultSiteId) {
  form.value = {
    id: undefined,
    toolCode: undefined,
    toolName: undefined,
    toolType: 'ai',
    description: undefined,
    config: {},
    inputs: [],
    outputs: [],
    enabled: '1',
    siteId: defaultSiteId !== undefined ? defaultSiteId : personalSiteId.value,
    categoryId: undefined
  }
  proxy.resetForm('toolRef')
}

/** 获取输入参数数量 */
function getInputCount(row) {
  try {
    const inputs = typeof row.inputs === 'string' ? JSON.parse(row.inputs) : row.inputs
    return Array.isArray(inputs) ? inputs.length : 0
  } catch {
    return 0
  }
}

/** 获取输出参数数量 */
function getOutputCount(row) {
  try {
    const outputs = typeof row.outputs === 'string' ? JSON.parse(row.outputs) : row.outputs
    return Array.isArray(outputs) ? outputs.length : 0
  } catch {
    return 0
  }
}

/** 复制测试结果 */
function copyTestResult() {
  const text = jsonFormatted.value 
    ? JSON.stringify(testResult.value.output, null, 2) 
    : JSON.stringify(testResult.value.output)
  
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

/** 复制错误信息 */
function copyErrorMessage() {
  navigator.clipboard.writeText(testResult.value.error).then(() => {
    ElMessage.success('复制成功')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

/** 获取输出字段值 */
function getOutputValue(output, fieldName) {
  if (!output || typeof output !== 'object') {
    return ''
  }
  const value = output[fieldName]
  if (value === null || value === undefined) {
    return ''
  }
  return value
}

/** 格式化输出字段值（用于数组和对象） */
function formatOutputValue(output, fieldDef) {
  const value = getOutputValue(output, fieldDef.name)
  if (!value) {
    return ''
  }
  
  // 如果是数组或对象，格式化显示
  if (typeof value === 'object') {
    return JSON.stringify(value, null, 2)
  }
  
  return String(value)
}

/** 显示变量选择器 */
function showVariableSelector(fieldName) {
  currentEditingField.value = fieldName
  // 默认显示输入参数选项卡
  variableCategory.value = 'input'
  variableSelectorVisible.value = true
}

/** 选择变量并插入 */
function selectVariable(category, fieldKey) {
  // 输入参数使用 {{name}} 格式，系统变量使用 {{category.field}} 格式
  const variable = category === 'input' 
    ? '{{' + fieldKey + '}}' 
    : '{{' + category + '.' + fieldKey + '}}'
  insertVariableText(variable)
  variableSelectorVisible.value = false
}

/** 插入变量文本到编辑器 */
function insertVariableText(variable) {
  const fieldName = currentEditingField.value
  
  // 获取对应的输入框引用
  const inputRef = fieldName === 'systemPrompt' 
    ? proxy.$refs.systemPromptInput 
    : proxy.$refs.userPromptInput
  
  if (inputRef && inputRef.textarea) {
    const textarea = inputRef.textarea
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const text = form.value.config[fieldName] || ''
    
    // 在光标位置插入变量
    const newText = text.substring(0, start) + variable + text.substring(end)
    form.value.config[fieldName] = newText
    
    // 更新光标位置
    nextTick(() => {
      const newCursorPos = start + variable.length
      textarea.focus()
      textarea.setSelectionRange(newCursorPos, newCursorPos)
    })
  } else {
    // 如果没有ref，则在末尾添加
    if (form.value.config[fieldName]) {
      form.value.config[fieldName] += ' ' + variable
    } else {
      form.value.config[fieldName] = variable
    }
  }
}

/** 处理全站导入文件变化 */
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
    fullImportTools.value = parsedData.tools || []
    fullImportRelations.value = parsedData.relations || []
    
    hasDefaultConfig.value = fullImportTools.value.some(t => t['网站虚拟ID'] === 0 || t['网站编码'] === 'default')
    
    initializeSiteMapping()
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + error.message)
  }
}

/** 读取文件数据 */
function readFileData(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (e) => {
      resolve(e.target.result)
    }
    
    reader.onerror = (error) => {
      reject(error)
    }
    
    if (file.type.includes('json')) {
      reader.readAsText(file)
    } else {
      reader.readAsArrayBuffer(file)
    }
  })
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
  const workbook = XLSX.read(fileData, { type: 'array' })
  
  const result = {
    sites: [],
    tools: [],
    relations: []
  }
  
  if (workbook.SheetNames.includes('网站列表')) {
    const siteSheet = workbook.Sheets['网站列表']
    result.sites = XLSX.utils.sheet_to_json(siteSheet)
  }
  
  if (workbook.SheetNames.includes('原子工具配置数据')) {
    const toolSheet = workbook.Sheets['原子工具配置数据']
    result.tools = XLSX.utils.sheet_to_json(toolSheet)
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
    tools: jsonData.tools || [],
    relations: jsonData.relations || []
  }
}

/** 移除全站导入文件 */
function handleFullImportFileRemove() {
  fullImportFile.value = null
  fullImportSites.value = []
  fullImportTools.value = []
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
  
  // 1. 按网站分组原子工具配置
  const toolsBySite = {}
  
  fullImportTools.value.forEach(tool => {
    const siteVirtualId = tool['网站虚拟ID'] || 0
    const virtualId = tool['原子工具虚拟ID']
    
    const targetSiteId = siteMapping.value[siteVirtualId]
    
    if (targetSiteId !== undefined && virtualId) {
      if (!toolsBySite[targetSiteId]) {
        toolsBySite[targetSiteId] = []
      }
      toolsBySite[targetSiteId].push({
        ...tool,
        targetSiteId,
        virtualId
      })
    }
  })
  
  // 2. 按网站导入原子工具配置
  for (const [targetSiteId, tools] of Object.entries(toolsBySite)) {
    for (const tool of tools) {
      const toolData = {
        toolCode: tool['工具代码'],
        toolName: tool['工具名称'],
        toolType: tool['工具类型'],
        categoryId: tool['分类ID'] || null,
        description: tool['描述'] || '',
        config: tool['配置'] || '{}',
        inputs: tool['输入参数'] || '[]',
        outputs: tool['输出参数'] || '[]',
        enabled: tool['是否启用'] || '1',
        sortOrder: tool['排序号'] || 0,
        siteId: parseInt(targetSiteId)
      }
      
      try {
        const response = await addAtomicTool(toolData)
        const newToolId = response.data || response.id
        
        if (newToolId) {
          if (!virtualIdToNewIdMap[targetSiteId]) {
            virtualIdToNewIdMap[targetSiteId] = {}
          }
          virtualIdToNewIdMap[targetSiteId][tool.virtualId] = newToolId
        }
      } catch (error) {
        console.error('导入原子工具失败:', tool['工具名称'], error)
        throw new Error(`导入原子工具失败: ${tool['工具名称']} - ${error.message}`)
      }
    }
  }
  
  // 3. 处理关联关系
  if (fullImportRelations.value && fullImportRelations.value.length > 0) {
    for (const relation of fullImportRelations.value) {
      const toolVirtualId = relation['原子工具虚拟ID']
      const siteVirtualId = relation['网站虚拟ID']
      const relationType = relation['关联类型'] === '排除' ? 'exclude' : 'associate'
      
      // 查找对应的工具实际ID
      let newToolId = null
      for (const [targetSiteId, mapping] of Object.entries(virtualIdToNewIdMap)) {
        if (mapping[toolVirtualId]) {
          newToolId = mapping[toolVirtualId]
          break
        }
      }
      
      if (!newToolId) {
        console.warn('未找到原子工具虚拟ID对应的新ID:', toolVirtualId)
        continue
      }
      
      // 查找对应的网站实际ID
      const targetSiteId = siteMapping.value[siteVirtualId]
      if (!targetSiteId) {
        console.warn('未找到网站虚拟ID对应的新ID:', siteVirtualId)
        continue
      }
      
      try {
        if (relationType === 'exclude') {
          // 排除关系
          await batchSaveAtomicToolSiteRelations({ atomicToolIds: [newToolId], excludeSiteIds: [targetSiteId] })
        } else {
          // 关联关系
          await batchSaveAtomicToolSiteRelations({ atomicToolIds: [newToolId], includeSiteIds: [targetSiteId] })
        }
      } catch (error) {
        console.error('处理关联关系失败:', relation, error)
      }
    }
  }
}

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  if (siteList.value.length > 0) {
    getList()
  }
})

// 页面加载初始化
onMounted(async () => {
  await loadSiteList()
  const restored = restoreViewModeSiteSelection(siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  viewMode.value = restored.viewMode
  queryParams.value.siteId = restored.siteId
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  await loadToolCategories()
  await loadExecutors()
  loadPlatforms()
  getList()
})
</script>
