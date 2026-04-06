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
      <el-form-item v-if="viewMode === 'creator' && queryParams.siteId && !isPersonalSite(queryParams.siteId)" label=" ">
        <el-checkbox v-model="includeDefaultConfig" @change="handleQuery">含默认配置</el-checkbox>
      </el-form-item>
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入分类名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类类型" prop="categoryType">
        <el-select v-model="queryParams.categoryType" placeholder="请选择分类类型" clearable style="width: 200px" @change="handleCategoryTypeChange">
          <el-option 
            v-for="type in categoryTypeOptions" 
            :key="type.value" 
            :label="type.label" 
            :value="type.value" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="板块筛选" prop="isSection" v-if="queryParams.categoryType === 'article'">
        <el-select v-model="queryParams.isSection" placeholder="全部" clearable style="width: 150px">
          <el-option label="仅板块" value="1" />
          <el-option label="仅分类" value="0" />
        </el-select>
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
      <el-form-item v-if="viewMode === 'creator'">
        <el-button type="success" plain icon="Download" @click="handleFullExport" v-hasPermi="['gamebox:category:export']">全站导出</el-button>
        <el-button type="warning" plain icon="Upload" @click="handleFullImport" v-hasPermi="['gamebox:category:add']">全站导入</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:category:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:category:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:category:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="MagicStick" :disabled="multiple" @click="handleBatchTranslate" v-hasPermi="['gamebox:category:edit']">批量翻译</el-button>
      </el-col>
      <el-col :span="1.5" v-if="viewMode === 'creator' && isPersonalSite(queryParams.siteId)">
        <el-button type="danger" plain icon="CircleClose" :disabled="multiple" @click="handleBatchExclude" v-hasPermi="['gamebox:category:edit']">批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5" v-if="viewMode === 'creator'">
        <el-button type="primary" plain icon="Link" :disabled="multiple" @click="handleBatchRelation" v-hasPermi="['gamebox:category:edit']">批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Download" :disabled="multiple" @click="handleExport" v-hasPermi="['gamebox:category:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleImport" v-hasPermi="['gamebox:category:add']">导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table 
      ref="categoryTableRef"
      v-loading="loading" 
      :data="categoryList" 
      row-key="id"
      :key="`table-${viewMode}-${queryParams.siteId}`"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分类名称" prop="name" :show-overflow-tooltip="true" min-width="200">
        <template #default="scope">
          <span v-if="scope.row.icon" style="margin-right: 5px;">{{ scope.row.icon }}</span>
          <span>{{ scope.row.name }}</span>
          <el-tag v-if="scope.row.isSection === '1'" type="warning" size="small" style="margin-left: 8px;">板块</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类标识" align="center" prop="slug" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="创建者网站" align="center" prop="siteId" width="150">
        <template #default="scope">
          <el-tag type="success">{{ getSiteName(scope.row.siteId) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联网站" align="center" width="120">
        <template #default="scope">
          <!-- 默认配置显示排除数量 -->
          <template v-if="isPersonalSite(scope.row.creatorSiteId) || isPersonalSite(scope.row.siteId)">
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
          <!-- 其他分类显示关联数量 -->
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
      <el-table-column label="分类类型" align="center" prop="categoryType" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.categoryType" :type="getCategoryTypeTagType(scope.row.categoryType)">
            {{ getCategoryTypeLabel(scope.row.categoryType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="应用页面" align="center" width="150">
        <template #default="scope">
          <span style="color: #409eff;">{{ getPageName(scope.row.categoryType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="关联数据" align="center" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.relatedDataCount > 0" type="info">
            {{ scope.row.relatedDataCount }}
          </el-tag>
          <el-tag v-else type="success" effect="plain">
            0
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="可见性" align="center" width="100" v-if="viewMode === 'related' && queryParams.siteId && !isPersonalSite(queryParams.siteId)">
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
      <el-table-column label="状态" align="center" prop="status" width="120" v-if="viewMode === 'creator'">
        <template #default="scope">
          <!-- 关联模式下的状态显示 - 基于relationSource统一判断 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的分类 - 显示启用/禁用 -->
            <template v-if="scope.row.relationSource === 'own'">
              <el-tag :type="scope.row.isVisible === '1' ? 'success' : 'danger'">
                {{ scope.row.isVisible === '1' ? '启用' : '禁用' }}
              </el-tag>
            </template>
            <!-- default: 默认配置 - 显示全局可见/已排除 -->
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tag v-if="scope.row.isVisible === '0'" type="info" effect="dark">
                已排除
              </el-tag>
              <el-tag v-else type="success" effect="plain">
                全局可见
              </el-tag>
            </template>
            <!-- shared: 其他网站分享 - 显示可见性开关 -->
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-switch
                :model-value="scope.row.isVisible"
                active-value="1"
                inactive-value="0"
                active-text="可见"
                inactive-text="隐藏"
                @click="handleQuickToggleVisibility(scope.row)"
              />
            </template>
          </template>
          <!-- 创建者模式下显示status -->
          <template v-else>
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template #default="scope">
          <!-- 关联模式下的操作 - 基于relationSource统一判断 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的分类 - 可修改、删除 -->
            <template v-if="scope.row.relationSource === 'own'">
              <el-tooltip content="修改" placement="top">
                <el-button 
                  link 
                  type="primary" 
                  icon="Edit" 
                  @click="handleUpdate(scope.row)" 
                  v-hasPermi="['gamebox:category:edit']"
                />
              </el-tooltip>
              <el-tooltip content="网站关联" placement="top" v-if="scope.row.categoryType !== 'website'">
                <el-button 
                  link 
                  type="warning" 
                  icon="Link" 
                  @click="handleManageSites(scope.row)" 
                  v-hasPermi="['gamebox:category:edit']"
                />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Delete" 
                  @click="handleDelete(scope.row)" 
                  v-hasPermi="['gamebox:category:remove']"
                />
              </el-tooltip>
            </template>
            <!-- default: 默认配置 - 注意：排除/恢复功能已统一到"批量排除"按钮中管理 -->
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tooltip content="网站关联" placement="top" v-if="scope.row.categoryType !== 'website'">
                <el-button 
                  link 
                  type="warning" 
                  icon="Link" 
                  @click="handleManageSites(scope.row)" 
                  v-hasPermi="['gamebox:category:edit']"
                />
              </el-tooltip>
              <el-tag v-if="scope.row.isExcluded === 1" type="info" size="small">已排除</el-tag>
            </template>
            <!-- shared: 其他网站分享 - 可移除关联 -->
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-tooltip content="网站关联" placement="top" v-if="scope.row.categoryType !== 'website'">
                <el-button 
                  link 
                  type="warning" 
                  icon="Link" 
                  @click="handleManageSites(scope.row)" 
                  v-hasPermi="['gamebox:category:edit']"
                />
              </el-tooltip>
              <el-tooltip content="移除关联" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Close" 
                  @click="handleRemoveFromSite(scope.row)" 
                  v-hasPermi="['gamebox:category:remove']"
                />
              </el-tooltip>
            </template>
          </template>
          <!-- 创建者模式下的操作 -->
          <template v-else>
            <el-tooltip content="添加子分类" placement="top">
              <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)" v-hasPermi="['gamebox:category:add']" />
            </el-tooltip>
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:category:edit']" />
            </el-tooltip>
            <!-- 显示翻译按钮（排除website类型） -->
            <el-tooltip content="翻译" placement="top" v-if="scope.row.categoryType !== 'website'">
              <el-button 
                link 
                type="warning" 
                icon="Document"
                @click="handleManageTranslations(scope.row)" 
                v-hasPermi="['gamebox:category:edit']"
              />
            </el-tooltip>
            <!-- 非默认配置的分类：显示网站关联按钮 -->
            <el-tooltip content="网站关联" placement="top" v-if="scope.row.categoryType !== 'website' && !isPersonalSite(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="Link" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:category:edit']"
              />
            </el-tooltip>
            <!-- 默认配置：管理排除 -->
            <el-tooltip content="管理排除" placement="top" v-if="isPersonalSite(scope.row.creatorSiteId) || isPersonalSite(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="CircleClose" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:category:edit']"
              />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:category:remove']" />
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 网站关联管理对话框 -->
    <SiteRelationManager
      v-model="siteRelationDialogOpen"
      entity-type="category"
      :entity-id="currentCategoryIdForSites"
      :entity-name="currentCategoryNameForSites"
      :creator-site-id="currentCategoryCreatorSiteId"
      :is-personal-creator="isPersonalSite(currentCategoryCreatorSiteId)"
      @refresh="getList"
    />

    <!-- 翻译管理对话框 -->
    <TranslationManager
      v-model="translationDialogOpen"
      :key="`translation-${queryParams.siteId || 0}`"
      entity-type="category"
      :entity-id="currentTranslationCategoryId"
      :entity-name="currentTranslationCategoryName"
      :site-id="queryParams.siteId || 0"
      :translation-fields="categoryTranslationFields"
      :original-data="currentTranslationCategoryData"
      @refresh="getList"
    />

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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选分类）</p>
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
          <p style="margin: 0;">如需彻底生效排除，请先在“批量关联管理”中移除相应网站的 include 关联。</p>
        </div>
      </el-alert>
      
      <div style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; margin-bottom: 8px;">
          <strong>已选分类：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedCategoriesForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="cat in selectedCategoriesForBatchExclude" 
            :key="cat.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeCategoryFromBatchExclude(cat.id)"
            size="small"
          >
            {{ cat.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各分类的当前排除/关联状态 -->
      <el-collapse v-if="categoryExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各分类当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="categoryExclusionDetails" size="small" stripe>
              <el-table-column label="分类" prop="categoryName" width="150" show-overflow-tooltip />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选分类对该网站可见）</p>
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
          <p style="margin: 0;">如需彻底清除冲突，可先在“批量排除管理”中移除相应网站的排除设置。</p>
        </div>
      </el-alert>
      
      <div style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; margin-bottom: 8px;">
          <strong>已选分类：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedCategoriesForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="cat in selectedCategoriesForBatchRelation" 
            :key="cat.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeCategoryFromBatchRelation(cat.id)"
            size="small"
          >
            {{ cat.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各分类的当前关联/排除状态 -->
      <el-collapse v-if="categoryRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各分类当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="categoryRelationDetails" size="small" stripe>
              <el-table-column label="分类" prop="categoryName" width="150" show-overflow-tooltip />
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
        <p>• 全站导出将包含所有分类数据（默认配置 + 所有站点）</p>
        <p>• 包含所有翻译数据和网站关联关系</p>
        <p>• 适用于系统迁移、备份等场景</p>
        <p style="margin-top: 10px; color: #F56C6C; font-weight: bold;">⚠️ 数据量可能较大，请耐心等待</p>
      </template>
    </FullExportDialog>

    <!-- 数据导出对话框 -->
    <ExportDialog
      v-model="exportDialogOpen"
      :selectedCount="ids.length"
      :availableLanguages="exportAvailableLanguages"
      v-model:exportFormat="exportFormat"
      v-model:includeTranslations="exportIncludeTranslations"
      v-model:selectedLanguages="exportSelectedLanguages"
      :loading="exportLoading"
      @confirm="confirmExport"
    >
      <template #exportTips>
        <p>• 分类数据：包含分类信息和层级关系（通过父级分类标识关联）</p>
        <p>• 翻译数据：可选择需要导出的语言版本</p>
        <p style="margin-top: 10px; color: #909399;">导入时将自动归属到当前选择的网站</p>
      </template>
    </ExportDialog>

    <!-- 数据导入对话框 -->
    <ImportDialog
      v-model="importDialogOpen"
      :loading="importLoading"
      :previewData="importPreviewData"
      :translationsData="importTranslationsData"
      @confirm="confirmImport"
      @fileChange="handleFileChange"
      @fileRemove="handleFileRemove"
    >
      <template #importTips>
        <p>• 支持导入 Excel (.xlsx) 或 JSON (.json) 格式文件</p>
        <p>• 将根据导入数据重建分类结构和关联关系</p>
        <p>• 请确保文件格式与导出的数据格式一致</p>
        <p>• 必填字段：分类名称、分类标识、分类类型</p>
        <p>• 会自动重建父子关系和网站关联关系</p>
      </template>
      <template #previewColumns>
        <el-table-column prop="name" label="分类名称" width="150" show-overflow-tooltip />
        <el-table-column prop="slug" label="分类标识" width="120" show-overflow-tooltip />
        <el-table-column prop="categoryType" label="分类类型" width="100" show-overflow-tooltip />
        <el-table-column prop="siteName" label="创建者网站" width="120" show-overflow-tooltip />
        <el-table-column label="其他字段" width="100">
          <template #default>
            <el-tag size="small" type="info">{{ Object.keys(importPreviewData[0] || {}).length }} 个字段</el-tag>
          </template>
        </el-table-column>
      </template>
    </ImportDialog>

    <!-- 全站导入对话框 -->
    <FullImportDialog
      v-model="fullImportDialogOpen"
      :loading="fullImportLoading"
      :siteList="siteList"
      :importSites="fullImportSites"
      :importData="fullImportCategories"
      :importTranslations="fullImportTranslations"
      :importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      dataLabel="分类数量"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #defaultConfigTips>
        <p>选择目标网站后，默认配置将：</p>
        <p>1. 创建为该网站的分类数据</p>
        <p>2. 自动为其他真实网站创建关联关系</p>
        <p style="margin-top: 5px; color: #F56C6C;">⚠️ 请确保已在系统中创建好目标网站</p>
      </template>
    </FullImportDialog>
        

    <!-- 添加或修改分类对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="categoryRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入分类名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类标识" prop="slug">
              <el-input v-model="form.slug" placeholder="请输入分类标识（URL友好）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.categoryType !== 'website'">
          <el-col :span="24">
            <el-form-item label="创建者网站" prop="siteId">
              <SiteSelect v-model="form.siteId" :site-list="siteList" show-default clearable width="100%" placeholder="请选择创建者网站" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.id && form.categoryType !== 'website'">
          <el-col :span="24">
            <el-alert
              title="创建后不可修改创建者网站。如需在其他网站展示，请使用【网站关联】功能。"
              type="warning"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <el-row v-if="form.categoryType !== 'website' && form.siteId && !isPersonalSite(form.siteId) && !form.id">
          <el-col :span="24">
            <el-alert
              title="提示：分类创建后对创建者网站可见。您可以选择将其共享到其他网站。"
              type="info"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <!-- 排除/关联网站选择 -->
        <el-row v-if="form.categoryType !== 'website' && !form.id">
          <el-col :span="24">
            <el-form-item label="排除的网站" v-if="isPersonalSite(form.siteId)">
              <el-select 
                v-model="form.relatedSiteIds" 
                multiple 
                collapse-tags
                collapse-tags-tooltip
                placeholder="选择要排除的网站（默认对所有网站可见）" 
                style="width: 100%"
              >
                <el-option 
                  v-for="site in siteList.filter(s => s.isPersonal !== 1)" 
                  :key="site.id" 
                  :label="site.displayLabel" 
                  :value="site.id" 
                />
              </el-select>
              <div style="color: #909399; font-size: 12px; margin-top: 4px;">
                默认配置对所有网站可见，这里可选择要排除的网站
              </div>
            </el-form-item>
            <el-form-item label="额外关联" v-else-if="form.siteId && !isPersonalSite(form.siteId)">
              <el-select 
                v-model="form.relatedSiteIds" 
                multiple 
                collapse-tags
                collapse-tags-tooltip
                placeholder="选择要额外关联的其他网站（可选）" 
                style="width: 100%"
              >
                <el-option 
                  v-for="site in siteList.filter(s => s.id !== form.siteId && s.isPersonal !== 1)" 
                  :key="site.id" 
                  :label="site.displayLabel" 
                  :value="site.id" 
                />
              </el-select>
              <div style="color: #909399; font-size: 12px; margin-top: 4px;">
                分类对创建者网站自动可见，这里可选择共享到其他网站
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-else>
          <el-col :span="24">
            <el-alert
              title="网站管理分类为全局配置，所有站点共用"
              type="info"
              :closable="false"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类类型" prop="categoryType">
              <el-select v-model="form.categoryType" placeholder="请选择分类类型" @change="handleCategoryTypeChangeInForm">
                <el-option 
                  v-for="type in categoryTypeOptions" 
                  :key="type.value" 
                  :label="type.label" 
                  :value="type.value" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.categoryType === 'article'">
            <el-form-item label="分类层级" prop="isSection">
              <el-radio-group v-model="form.isSection" @change="handleSectionTypeChange">
                <el-radio value="1">板块（顶级）</el-radio>
                <el-radio value="0">分类（需选板块）</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-else>
            <el-form-item label="父级分类" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="categoryTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择父级分类"
                check-strictly
                :render-after-expand="false"
                clearable
              >
                <template #default="{ data }">
                  <span style="display: flex; align-items: center; width: 100%;">
                    <span v-if="data.icon" style="margin-right: 6px;">{{ data.icon }}</span>
                    <span style="flex: 1;">{{ data.name }}</span>
                    <el-tag v-if="data.siteLabel" :type="data.siteLabel === '默认配置' ? 'success' : 'info'" size="small" style="margin-left: 8px;">
                      {{ data.siteLabel }}
                    </el-tag>
                  </span>
                </template>
              </el-tree-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.categoryType === 'article' && form.isSection === '0'">
          <el-col :span="24">
            <el-form-item label="所属板块" prop="parentId">
              <el-select v-model="form.parentId" placeholder="请选择所属板块" style="width: 100%">
                <el-option 
                  v-for="section in articleSectionOptions" 
                  :key="section.id" 
                  :label="section.name" 
                  :value="section.id"
                >
                  <span v-if="section.icon">{{ section.icon }} </span>
                  <span>{{ section.name }}</span>
                  <el-tag 
                    v-if="isPersonalSite(section.siteId)" 
                    type="success" 
                    size="small" 
                    style="margin-left: 8px;"
                  >
                    默认配置
                  </el-tag>
                  <el-tag 
                    v-else-if="section.siteId === form.siteId" 
                    type="primary" 
                    size="small" 
                    style="margin-left: 8px;"
                  >
                    {{ getSiteName(section.siteId) }}
                  </el-tag>
                  <el-tag 
                    v-else 
                    type="info" 
                    size="small" 
                    style="margin-left: 8px;"
                  >
                    {{ getSiteName(section.siteId) }}
                  </el-tag>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="分类图标" prop="icon">
              <el-input v-model="form.icon" placeholder="请输入分类图标（emoji或icon类名）" />
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

<script setup name="Category">
import { listCategory, getCategory, delCategory, addCategory, updateCategory } from "@/api/gamebox/category"
import { listSite } from "@/api/gamebox/site"
import { enrichSiteList, getSiteDisplayName, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from "@/composables/useSiteSelection"
import { batchSaveCategorySiteRelations, getCategorySites, getBatchCategorySites, updateCategoryVisibility } from "@/api/gamebox/siteRelation"
import { batchAutoTranslate, getEntityTranslations, batchSaveTranslations } from "@/api/gamebox/translation"
import { useCategoryTypes } from "@/composables/useCategoryTypes"
import SiteSelect from "@/components/SiteSelect/index.vue"
import SiteRelationManager from "@/components/SiteRelationManager/index.vue"
import TranslationManager from "@/components/TranslationManager/index.vue"
import { ExportDialog, ImportDialog, FullExportDialog, FullImportDialog } from "@/components/ImportExportDialogs"
import { handleTree } from "@/utils/ruoyi"
import { CircleClose, Link, Warning } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const categoryTableRef = ref(null)

// 网站列表
const siteList = ref([])

// 使用分类类型组合式函数
const { 
  categoryTypes,
  loadCategoryTypes, 
  getCategoryTypeLabel, 
  getCategoryTypeTagType,
  getCategoryTypeDescription,
  getCategoryTypeOptions
} = useCategoryTypes()

const categoryTypeOptions = computed(() => getCategoryTypeOptions())

// 获取分类类型对应的页面名称（从数据库description字段提取）
function getPageName(categoryType) {
  const description = getCategoryTypeDescription(categoryType)
  if (!description) return '未知页面'
  
  // 从 "用于XXX页面的分类" 或 "用于XXX管理页面的分类" 中提取页面名称
  // 例如: "用于存储配置页面的分类" -> "存储配置页面"
  const match = description.match(/用于(.+?)的分类/)
  if (match && match[1]) {
    return match[1]
  }
  
  return description
}

// 获取网站名称
function getSiteName(siteId) {
  return getSiteDisplayName(siteId, siteList.value)
}

// 个人默认站点 ID（is_personal=1 的站点，即迁移前的 site_id=0 默认配置）
const personalSiteId = computed(() => {
  const site = siteList.value.find(s => s.isPersonal === 1)
  return site ? site.id : null
})

// 判断某个 siteId 是否是个人默认站点
function isPersonalSite(siteId) {
  if (!siteId || !personalSiteId.value) return false
  return siteId === personalSiteId.value
}

const categoryList = ref([])
const categoryTreeOptions = ref([])
const articleSectionOptions = ref([])
const includeDefaultConfig = ref(false) // 是否包含默认分类
const viewMode = ref('creator') // 查看模式：creator-创建者, related-关联网站

const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentCategoryIdForSites = ref(0)
const currentCategoryNameForSites = ref('')
const currentCategoryCreatorSiteId = ref(0)

// 旧的单个排除对话框变量（已废弃，仅保留用于避免报错）
const exclusionDialogOpen = ref(false)
const selectedExcludedSiteIds = ref([])

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const selectedCategoriesForBatchExclude = ref([])
const categoryExclusionDetails = ref([]) // 各分类的排除详情

// 冲突网站 ID 集合（存在 include 关联的网站，exclude 对其无效）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  categoryExclusionDetails.value.forEach(detail => {
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
const selectedCategoriesForBatchRelation = ref([])
const categoryRelationDetails = ref([]) // 各分类的关联详情

// 是否处于默认配置的批量关联管理
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId))

// 冲突网站 ID 集合（存在 exclude 关系的网站）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  categoryRelationDetails.value.forEach(detail => {
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
const exportIncludeTranslations = ref(false)
const exportSelectedLanguages = ref([])
const exportAvailableLanguages = ref([])
const exportDefaultLanguage = ref('') // 默认语言代码

// 全站导出相关
const fullExportDialogOpen = ref(false)
const fullExportFormat = ref('excel')
const fullExportLoading = ref(false)

// 导入管理相关
const importDialogOpen = ref(false)
const importLoading = ref(false)
const importPreviewData = ref([])
const importTranslationsData = ref([])
const importFile = ref(null)

// 全站导入相关
const fullImportDialogOpen = ref(false)
const fullImportLoading = ref(false)
const fullImportSites = ref([]) // 导入文件中的网站列表
const fullImportCategories = ref([]) // 导入的分类数据
const fullImportTranslations = ref([]) // 导入的翻译数据
const fullImportRelations = ref([]) // 导入的关联关系
const fullImportFile = ref(null)
const siteMapping = ref({}) // 网站ID映射 {源网站ID: 目标网站ID}
const createDefaultAsNewSite = ref(false) // 是否将默认配置创建为新网站
const hasDefaultConfig = ref(false) // 是否包含默认配置(个人站点)

// 翻译管理相关
const translationDialogOpen = ref(false)
const currentTranslationCategoryId = ref(0)
const currentTranslationCategoryName = ref('')
const currentTranslationCategoryData = ref({})
const categoryTranslationFields = [
  { name: 'name', label: '分类名称', type: 'text' },
  { name: 'description', label: '分类描述', type: 'textarea' }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: undefined,
    name: undefined,
    categoryType: undefined,
    isSection: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: "分类名称不能为空", trigger: "blur" }],
    slug: [{ required: true, message: "分类标识不能为空", trigger: "blur" }],
    categoryType: [{ required: true, message: "分类类型不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 监听分类类型变化，website类型强制为全局
watch(() => form.value.categoryType, (newType) => {
  if (newType === 'website') {
    form.value.siteId = personalSiteId.value  // 使用个人站点ID表示全局
    form.value.parentId = 0
  }
  // 重新加载父级分类选项
  getCategoryTreeOptions()
})

// 监听siteId变化，重新加载父级分类选项
watch(() => form.value.siteId, () => {
  if (form.value.categoryType !== 'website') {
    form.value.parentId = 0
    getCategoryTreeOptions()
  }
})

/** 加载网站列表 */
function loadSiteList() {
  listSite({ pageNum: 1, pageSize: 9999, status: '1' }).then(response => {
    siteList.value = enrichSiteList(response.rows || [])
    const restored = restoreViewModeSiteSelection(siteList.value, {
      creatorFallbackSiteId: personalSiteId.value
    })
    viewMode.value = restored.viewMode
    queryParams.value.siteId = restored.siteId

    saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)

    // 加载分类数据
    getList()
  })
}

/** 分类类型切换事件 */
function handleCategoryTypeChange() {
  // 切换分类类型时，清空板块筛选
  queryParams.value.isSection = undefined
  handleQuery()
}

/** 表单中分类类型切换 */
function handleCategoryTypeChangeInForm() {
  // 切换分类类型时，重置isSection和parentId
  if (form.value.categoryType === 'article') {
    form.value.isSection = '0'
    form.value.parentId = undefined
    loadArticleSections()
  } else {
    form.value.isSection = undefined
    form.value.parentId = 0
  }
  getCategoryTreeOptions()
}

/** 板块/分类类型切换 */
function handleSectionTypeChange() {
  if (form.value.isSection === '1') {
    // 选择板块：parent_id必须为0
    form.value.parentId = 0
  } else {
    // 选择分类：需要选择板块
    form.value.parentId = undefined
    loadArticleSections()
  }
}

/** 加载文章板块列表 */
function loadArticleSections() {
  const siteId = form.value.siteId
  const currentId = form.value.id // 当前编辑的分类ID
  
  // 如果是默认配置（个人站），只加载默认板块
  if (isPersonalSite(siteId)) {
    return listCategory({
      categoryType: 'article',
      isSection: '1',
      siteId: personalSiteId.value,
      status: '1',
      pageNum: 1,
      pageSize: 9999
    }).then(response => {
      let sections = response.rows || []
      // 如果是编辑模式，过滤掉当前分类（避免选择自己作为父级）
      if (currentId) {
        sections = sections.filter(section => section.id !== currentId)
      }
      articleSectionOptions.value = sections
      return response
    })
  }
  
  // 如果是特定网站，加载该网站的板块 + 默认配置的板块
  const promises = [
    listCategory({
      categoryType: 'article',
      isSection: '1',
      siteId: siteId,
      status: '1',
      pageNum: 1,
      pageSize: 9999
    }),
    listCategory({
      categoryType: 'article',
      isSection: '1',
      siteId: personalSiteId.value,
      status: '1',
      pageNum: 1,
      pageSize: 9999
    })
  ]
  
  return Promise.all(promises).then(responses => {
    const siteCategories = responses[0].rows || []
    const globalCategories = responses[1].rows || []
    // 合并两个列表
    let allSections = [...siteCategories, ...globalCategories]
    // 如果是编辑模式，过滤掉当前分类（避免选择自己作为父级）
    if (currentId) {
      allSections = allSections.filter(section => section.id !== currentId)
    }
    articleSectionOptions.value = allSections
    return responses[0]
  })
}

/** 网站切换事件 */
function handleSiteChange() {
  // 切换网站时重置"含默认分类"选项
  if (!queryParams.value.siteId || isPersonalSite(queryParams.value.siteId)) {
    includeDefaultConfig.value = false
  }

  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)

  // 重置到第一页并重新查询
  queryParams.value.pageNum = 1
  getList()
}

/** 查看模式切换事件 */
function handleViewModeChange() {
  // 重置"含默认分类"选项
  includeDefaultConfig.value = false
  // 重置到第一页
  queryParams.value.pageNum = 1
  
  queryParams.value.siteId = resolveSiteIdByViewMode(viewMode.value, siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })

  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  
  // 使用 nextTick 确保 queryParams 更新后再查询
  nextTick(() => {
    getList()
  })
}

/** 查询分类列表 */
function getList() {
  loading.value = true
  
  // 构建查询参数
  const params = { 
    ...queryParams.value, 
    pageNum: 1, 
    pageSize: 9999,
    queryMode: viewMode.value,
    includeDefault: includeDefaultConfig.value
  }
  
  // 使用统一的查询接口
    if (params.siteId) {
    listCategory(params).then(response => {
      const rows = (response.rows || []).map(r => ({
        ...r,
        // 规范 isVisible 字段为字符串
        isVisible: String(r.isVisible != null ? r.isVisible : (r.is_visible != null ? r.is_visible : '1'))
      }))
      const treeData = handleTree(rows, "id", "parentId")
      total.value = treeData.length
      const start = (queryParams.value.pageNum - 1) * queryParams.value.pageSize
      const end = start + queryParams.value.pageSize
      categoryList.value = treeData.slice(start, end)
      loading.value = false
    }).catch(error => {
      console.error('查询分类失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
    } else {
    // 未选择网站：默认显示默认配置
    listCategory({ ...params, siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      const rows = (response.rows || []).map(r => ({
        ...r,
        // 规范 isVisible 字段为字符串
        isVisible: String(r.isVisible != null ? r.isVisible : (r.is_visible != null ? r.is_visible : '1'))
      }))
      const treeData = handleTree(rows, "id", "parentId")
      total.value = treeData.length
      const start = (queryParams.value.pageNum - 1) * queryParams.value.pageSize
      const end = start + queryParams.value.pageSize
      categoryList.value = treeData.slice(start, end)
      loading.value = false
    }).catch(error => {
      console.error('查询分类失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  }
}

/** 递归获取分类的所有子分类ID */
function getChildrenIds(categoryId, categories) {
  const ids = [categoryId]
  const children = categories.filter(cat => cat.parentId === categoryId)
  children.forEach(child => {
    ids.push(...getChildrenIds(child.id, categories))
  })
  return ids
}

/** 转换分类数据结构为树形选项 */
function getCategoryTreeOptions() {
  const categoryType = form.value.categoryType
  const siteId = form.value.siteId
  const currentId = form.value.id // 当前编辑的分类ID
  
  // website类型只能选择全局的website分类作为父级
  if (categoryType === 'website') {
    listCategory({ 
      pageNum: 1, 
      pageSize: 9999, 
      categoryType: 'website',
      siteId: personalSiteId.value // 加载全局的website分类
    }).then(response => {
      let categories = response.rows
      // 如果是编辑模式，过滤掉当前分类及其子分类
      if (currentId) {
        const excludeIds = getChildrenIds(currentId, categories)
        categories = categories.filter(cat => !excludeIds.includes(cat.id))
      }
      // 为每个分类添加站点标识，保留icon字段
      categories = categories.map(cat => ({
        ...cat,
        displayName: `${cat.name} [默认配置]`,
        siteLabel: '默认配置'
      }))
      const tree = handleTree(categories, "id", "parentId")
      categoryTreeOptions.value = [
        {
          id: 0,
          name: "顶级分类",
          icon: '',
          children: tree
        }
      ]
    })
  } else if (siteId && !isPersonalSite(siteId)) {
    // 非website类型，且选择了站点，加载该站点 + 全局分类（允许引用全局分类作为父级）
    const promises = [
      listCategory({ 
        pageNum: 1, 
        pageSize: 9999, 
        categoryType: categoryType,
        siteId: siteId 
      }),
      listCategory({ 
        pageNum: 1, 
        pageSize: 9999, 
        categoryType: categoryType,
        siteId: personalSiteId.value 
      })
    ]
    Promise.all(promises).then(responses => {
      let siteCategories = responses[0].rows || []
      let globalCategories = responses[1].rows || []
      
      // 如果是编辑模式，过滤掉当前分类及其子分类
      if (currentId) {
        const allCategories = [...siteCategories, ...globalCategories]
        const excludeIds = getChildrenIds(currentId, allCategories)
        siteCategories = siteCategories.filter(cat => !excludeIds.includes(cat.id))
        globalCategories = globalCategories.filter(cat => !excludeIds.includes(cat.id))
      }
      
      // 为分类添加站点标识，保留icon字段
      const siteName = getSiteName(siteId)
      siteCategories = siteCategories.map(cat => ({
        ...cat,
        displayName: `${cat.name} [${siteName}]`,
        siteLabel: siteName
      }))
      globalCategories = globalCategories.map(cat => ({
        ...cat,
        displayName: `${cat.name} [默认配置]`,
        siteLabel: '默认配置'
      }))
      
      const allCategories = [...siteCategories, ...globalCategories]
      const tree = handleTree(allCategories, "id", "parentId")
      categoryTreeOptions.value = [
        {
          id: 0,
          name: "顶级分类",
          icon: '',
          children: tree
        }
      ]
    })
  } else {
    // 选择了全局，加载全局的分类
    listCategory({ 
      pageNum: 1, 
      pageSize: 9999, 
      categoryType: categoryType,
      siteId: personalSiteId.value // 加载全局分类
    }).then(response => {
      let categories = response.rows
      // 如果是编辑模式，过滤掉当前分类及其子分类
      if (currentId) {
        const excludeIds = getChildrenIds(currentId, categories)
        categories = categories.filter(cat => !excludeIds.includes(cat.id))
      }
      // 为每个分类添加站点标识，保留icon字段
      categories = categories.map(cat => ({
        ...cat,
        displayName: `${cat.name} [默认配置]`,
        siteLabel: '默认配置'
      }))
      const tree = handleTree(categories, "id", "parentId")
      categoryTreeOptions.value = [
        {
          id: 0,
          name: "顶级分类",
          icon: '',
          children: tree
        }
      ]
    })
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
    name: undefined,
    slug: undefined,
    categoryType: "game",
    parentId: 0,
    isSection: "0",
    icon: undefined,
    sortOrder: 0,
    status: "1",
    remark: undefined,
    relatedSiteIds: [] // 关联或排除的网站IDs（根据siteId判断：0=排除，>0=关联）
  }
  proxy.resetForm("categoryRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  includeDefaultConfig.value = false
  proxy.resetForm("queryRef")
  // 重置后恢复默认网站选择
  if (siteList.value.length > 0) {
    queryParams.value.siteId = siteList.value[0].id
  } else {
    queryParams.value.siteId = personalSiteId.value
  }
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
async function handleAdd(row) {
  reset()
  
  if (row != null && row.id) {
    // 如果点击的是表格行数据，先获取完整的分类信息（确保所有字段准确）
    let parentCategory = row
    
    // 如果是文章类型，需要重新获取完整数据以确保isSection和parentId准确
    if (row.categoryType === 'article') {
      try {
        const response = await getCategory(row.id)
        parentCategory = response.data
      } catch (error) {
        console.error('获取父分类详情失败:', error)
      }
    }
    
    // 添加子分类：继承父分类的类型和站点ID
    form.value.categoryType = parentCategory.categoryType
    form.value.siteId = parentCategory.siteId
    
    // 处理文章类型的父子关系
    if (parentCategory.categoryType === 'article') {
      // 先加载板块列表
      await loadArticleSections()
      
      if (parentCategory.isSection === '1') {
        // 父分类是板块，则子分类必须是普通分类，且归属于该板块
        form.value.isSection = '0'
        form.value.parentId = parentCategory.id
      } else {
        // 父分类是普通分类，则子分类也是普通分类，且归属于同一个板块
        form.value.isSection = '0'
        // 继承父分类的板块ID（父分类的parentId就是板块ID）
        form.value.parentId = parentCategory.parentId
        
        // 如果父分类的parentId为0或undefined，说明数据有问题，给出提示
        if (!parentCategory.parentId || parentCategory.parentId === 0) {
          proxy.$modal.msgError('错误：父分类数据异常，未设置所属板块！')
          open.value = false
          return
        }
      }
    } else {
      // 非文章类型，直接使用父分类ID
      form.value.parentId = parentCategory.id
    }
  } else {
    // 添加顶级分类：使用当前查询条件的站点ID和分类类型
    form.value.parentId = 0
    
    // 自动使用用户搜索的网站条件
    if (queryParams.value.siteId !== undefined) {
      form.value.siteId = queryParams.value.siteId
    }
    
    // 自动使用用户搜索的分类类型条件
    if (queryParams.value.categoryType) {
      form.value.categoryType = queryParams.value.categoryType
      
      // 如果是文章类型，根据板块筛选条件设置isSection
      if (queryParams.value.categoryType === 'article') {
        if (queryParams.value.isSection === '1') {
          // 筛选的是板块，新增时也是板块
          form.value.isSection = '1'
        } else if (queryParams.value.isSection === '0') {
          // 筛选的是分类，新增时也是分类
          form.value.isSection = '0'
          // 加载板块列表
          await loadArticleSections()
        } else {
          // 没有板块筛选，默认为板块
          form.value.isSection = '1'
        }
      }
    }
  }
  
  getCategoryTreeOptions()
  open.value = true
  title.value = row != null && row.id ? "添加子分类" : "添加顶级分类"
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  try {
    const response = await getCategory(id)
    const data = response.data
    // 先设置除了parentId外的所有字段
    form.value = { ...data, parentId: undefined, relatedSiteIds: [] }
    // 保存原始的siteId，用于检测是否改变
    form.value.originalSiteId = data.siteId
    
    // 如果不是全局分类，加载已关联的网站
    if (data.siteId && !isPersonalSite(data.siteId)) {
      try {
        const relationResponse = await getCategorySites(id)
        const relatedSites = relationResponse.data || []
        // 过滤掉创建者网站（创建者网站是自动关联的，不需要显示在这里）
        form.value.relatedSiteIds = relatedSites
          .filter(site => site.siteId !== data.siteId)
          .map(site => site.siteId)
      } catch (error) {
        console.error('加载关联网站失败:', error)
      }
    }
    
    // 如果是文章类型的分类（非板块），先加载板块列表
    if (data.categoryType === 'article' && data.isSection === '0' && data.parentId && data.parentId > 0) {
      await loadArticleSections()
    }
    
    // 等待分类树选项加载完成后再设置parentId
    getCategoryTreeOptions()
    nextTick(() => {
      form.value.parentId = data.parentId || 0
    })
    
    open.value = true
    title.value = "修改分类"
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

/** 管理网站关联 */
async function handleManageSites(row) {
  // 始终使用统一的 SiteRelationManager 组件
  currentCategoryIdForSites.value = row.id
  currentCategoryNameForSites.value = row.name
  currentCategoryCreatorSiteId.value = row.siteId || 0
  siteRelationDialogOpen.value = true
}

/** 管理翻译 */
function handleManageTranslations(row) {
  currentTranslationCategoryId.value = row.id
  currentTranslationCategoryName.value = row.name
  currentTranslationCategoryData.value = {
    name: row.name,
    description: row.description
  }
  translationDialogOpen.value = true
}

/** 批量翻译 */
async function handleBatchTranslate() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要翻译的分类')
    return
  }
  
  try {
    await proxy.$modal.confirm(`确认要为选中的 ${selectedIds.length} 个分类生成翻译吗？`)
    
    // 准备要翻译的数据
    const allEntities = categoryList.value
      .filter(category => selectedIds.includes(category.id))
      .map(category => ({
        entityId: category.id,
        fields: {
          name: category.name,
          description: category.description || ''
        }
      }))
    
    // 分批处理，每批5条
    const batchSize = 5
    const batches = []
    for (let i = 0; i < allEntities.length; i += batchSize) {
      batches.push(allEntities.slice(i, i + batchSize))
    }
    
    let successCount = 0
    let failCount = 0
    
    const loading = proxy.$loading({
      lock: true,
      text: `正在批量生成翻译... 0/${allEntities.length}`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    // 逐批处理
    for (let i = 0; i < batches.length; i++) {
      try {
        await batchAutoTranslate('category', queryParams.siteId || 0, batches[i])
        successCount += batches[i].length
      } catch (error) {
        console.error(`第${i + 1}批翻译失败:`, error)
        failCount += batches[i].length
      }
      
      // 更新进度
      loading.setText(`正在批量生成翻译... ${successCount + failCount}/${allEntities.length}`)
    }
    
    loading.close()
    
    if (failCount > 0) {
      proxy.$modal.msgWarning(`批量翻译完成：成功${successCount}个，失败${failCount}个`)
    } else {
      proxy.$modal.msgSuccess(`批量翻译完成：共${successCount}个`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量翻译失败:', error)
      proxy.$modal.msgError('批量翻译失败: ' + error.message)
    }
  }
}

function submitForm() {
  proxy.$refs["categoryRef"].validate(async valid => {
    if (valid) {
      try {
        // 如果是文章类型的板块，确保parent_id为0
        if (form.value.categoryType === 'article' && form.value.isSection === '1') {
          form.value.parentId = 0
        }
        
        if (form.value.id != undefined) {
          // 更新模式：更新分类本身
          await updateCategory(form.value)
          
          // 如果不是全局分类，同步关联网站
          if (form.value.siteId && !isPersonalSite(form.value.siteId) && form.value.relatedSiteIds) {
            try {
              await batchSaveCategorySiteRelations({
                categoryIds: [form.value.id],
                includeSiteIds: form.value.relatedSiteIds
              })
            } catch (error) {
              console.error('更新关联失败:', error)
            }
          }
          
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        } else {
          // 新增模式：创建分类（创建者网站通过 site_id 字段自动可见）
          const response = await addCategory(form.value)
          const newCategoryId = response.data
          
          // 验证返回的ID是否有效
          if (!newCategoryId || typeof newCategoryId !== 'number') {
            proxy.$modal.msgWarning('分类创建成功，但无法获取新分类ID，请刷新后手动配置网站关联')
            open.value = false
            getList()
            return
          }
          
          // 如果选择了网站，根据类型添加关联或排除
          if (form.value.relatedSiteIds && form.value.relatedSiteIds.length > 0) {
            try {
              if (isPersonalSite(form.value.siteId)) {
                // 默认配置：添加排除关联
                await batchSaveCategorySiteRelations({
                  categoryIds: [newCategoryId],
                  excludeSiteIds: form.value.relatedSiteIds
                })
                proxy.$modal.msgSuccess(`新增成功，默认配置已创建并排除了 ${form.value.relatedSiteIds.length} 个网站`)
              } else {
                // 普通分类：添加include关联
                await batchSaveCategorySiteRelations({
                  categoryIds: [newCategoryId],
                  includeSiteIds: form.value.relatedSiteIds
                })
                proxy.$modal.msgSuccess(`新增成功，已共享到 ${form.value.relatedSiteIds.length} 个其他网站`)
              }
            } catch (error) {
              console.error('操作关联失败:', error)
              if (isPersonalSite(form.value.siteId)) {
                proxy.$modal.msgWarning('分类创建成功，但部分网站排除失败')
              } else {
                proxy.$modal.msgWarning('分类创建成功，但部分网站共享失败')
              }
            }
          } else {
            // 根据创建者类型显示不同的消息
            if (isPersonalSite(form.value.siteId)) {
              proxy.$modal.msgSuccess("新增成功，默认配置已创建（对所有网站可见）")
            } else {
              proxy.$modal.msgSuccess("新增成功")
            }
          }
          
          open.value = false
          getList()
        }
      } catch (error) {
        console.error('提交失败:', error)
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const categoryIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除分类编号为"' + categoryIds + '"的数据项？').then(function() {
    return delCategory(categoryIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 从网站移除关联 */
async function handleRemoveFromSite(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认从"${getSiteName(currentQuerySiteId)}"移除该分类的关联吗？`)
    const res = await getCategorySites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId).filter(id => id !== currentQuerySiteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    await batchSaveCategorySiteRelations({ categoryIds: [row.id], includeSiteIds, excludeSiteIds })
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
      const relationResponse = await getCategorySites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段（忽略排除逻辑）
        await updateCategoryVisibility(currentQuerySiteId, row.id, newValue)
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
          await batchSaveCategorySiteRelations({ categoryIds: [row.id], includeSiteIds, excludeSiteIds })
          proxy.$modal.msgSuccess('已排除该配置')
        } else {
          // 要显示 -> 移除排除关系
          const newExcludes = excludeSiteIds.filter(id => id !== currentQuerySiteId)
          await batchSaveCategorySiteRelations({ categoryIds: [row.id], includeSiteIds, excludeSiteIds: newExcludes })
          proxy.$modal.msgSuccess('已恢复该配置')
        }
        // 重新加载列表以更新排除网站数量
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用状态
      const text = newValue === '1' ? '启用' : '禁用'
      await updateCategory({ id: row.id, status: newValue })
      row.status = newValue
      proxy.$modal.msgSuccess(`${text}成功`)
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updateCategoryVisibility(currentQuerySiteId, row.id, newValue)
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
async function handleExcludeDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认排除"${row.name}"吗？排除后该分类将不在当前网站显示。`)
    const res = await getCategorySites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excludeSiteIds.includes(currentQuerySiteId)) excludeSiteIds.push(currentQuerySiteId)
    await batchSaveCategorySiteRelations({ categoryIds: [row.id], includeSiteIds, excludeSiteIds })
    proxy.$modal.msgSuccess('排除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('排除失败:', error)
      proxy.$modal.msgError('排除失败')
    }
  }
}

/** 恢复被排除的默认配置 */
async function handleRestoreDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认恢复"${row.name}"吗？恢复后该分类将重新在当前网站显示。`)
    const res = await getCategorySites(row.id)
    const sites = res.data || []
    const includeSiteIds = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excludeSiteIds = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== currentQuerySiteId)
    await batchSaveCategorySiteRelations({ categoryIds: [row.id], includeSiteIds, excludeSiteIds })
    proxy.$modal.msgSuccess('恢复成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
      proxy.$modal.msgError('恢复失败')
    }
  }
}

/** 批量排除管理 */
async function handleBatchExclude() {
  const selectedRows = categoryList.value.filter(cat => ids.value.includes(cat.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的分类')
    return
  }
  
  // 只允许默认配置的分类进行批量排除
  const invalidCategories = selectedRows.filter(cat => !isPersonalSite(cat.siteId))
  if (invalidCategories.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的分类进行批量排除管理')
    return
  }
  
  selectedCategoriesForBatchExclude.value = selectedRows.map(cat => ({
    id: cat.id,
    name: cat.name
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中分类的网站关系
    const batchRes = await getBatchCategorySites(selectedCategoriesForBatchExclude.value.map(cat => cat.id))
    const batchMap = batchRes.data || {}
    const results = selectedCategoriesForBatchExclude.value.map(cat => {
      const sites = batchMap[cat.id] || []
      return {
        categoryId: cat.id,
        categoryName: cat.name,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    categoryExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有分类共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedCategoriesForBatchExclude.value.length) {
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

/** 从批量排除中移除某个分类 */
function removeCategoryFromBatchExclude(categoryId) {
  selectedCategoriesForBatchExclude.value = selectedCategoriesForBatchExclude.value.filter(
    cat => cat.id !== categoryId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== categoryId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (categoryTableRef.value) {
    const row = categoryList.value.find(cat => cat.id === categoryId)
    if (row) {
      categoryTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  categoryExclusionDetails.value = categoryExclusionDetails.value.filter(
    detail => detail.categoryId !== categoryId
  )
  
  if (selectedCategoriesForBatchExclude.value.length === 0) {
    batchExclusionDialogOpen.value = false
  }
}

/** 全选网站 */
function selectAllSitesForBatchExclude() {
  batchExcludedSiteIds.value = siteList.value.filter(s => s.isPersonal !== 1).map(site => site.id)
}

/** 取消全选网站 */
function deselectAllSitesForBatchExclude() {
  batchExcludedSiteIds.value = []
}

/** 反选网站 */
function invertSiteSelectionForBatchExclude() {
  const allSiteIds = siteList.value.filter(s => s.isPersonal !== 1).map(site => site.id)
  const currentSelected = new Set(batchExcludedSiteIds.value)
  batchExcludedSiteIds.value = allSiteIds.filter(id => !currentSelected.has(id))
}

/** 提交批量排除配置 */
async function submitBatchExclusions() {
  if (selectedCategoriesForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何分类')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一次性传入所有分类 ID，单次请求完成
    await batchSaveCategorySiteRelations({
      categoryIds: selectedCategoriesForBatchExclude.value.map(cat => cat.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedCategoriesForBatchExclude.value.length} 个分类排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedCategoriesForBatchExclude.value.length} 个分类的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchCategorySites(selectedCategoriesForBatchExclude.value.map(cat => cat.id))
    const refreshMap = refreshRes.data || {}
    categoryExclusionDetails.value = selectedCategoriesForBatchExclude.value.map(cat => {
      const sites = refreshMap[cat.id] || []
      return {
        categoryId: cat.id,
        categoryName: cat.name,
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

/** 批量关联管理 */
async function handleBatchRelation() {
  const selectedRows = categoryList.value.filter(cat => ids.value.includes(cat.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的分类')
    return
  }
  
  const isDefaultConfig = isPersonalSite(queryParams.value.siteId)
  
  selectedCategoriesForBatchRelation.value = selectedRows.map(cat => ({
    id: cat.id,
    name: cat.name,
    siteId: cat.siteId
  }))
  
  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchCategorySites(selectedCategoriesForBatchRelation.value.map(cat => cat.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedCategoriesForBatchRelation.value.map(cat => {
      const sites = batchMap2[cat.id] || []
      return {
        categoryId: cat.id,
        categoryName: cat.name,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== cat.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    categoryRelationDetails.value = results
    
    // 找出被所有分类共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedCategoriesForBatchRelation.value.length) {
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

/** 从批量关联中移除某个分类 */
function removeCategoryFromBatchRelation(categoryId) {
  selectedCategoriesForBatchRelation.value = selectedCategoriesForBatchRelation.value.filter(
    cat => cat.id !== categoryId
  )
  
  ids.value = ids.value.filter(id => id !== categoryId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  if (categoryTableRef.value) {
    const row = categoryList.value.find(cat => cat.id === categoryId)
    if (row) {
      categoryTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  categoryRelationDetails.value = categoryRelationDetails.value.filter(
    detail => detail.categoryId !== categoryId
  )
  
  if (selectedCategoriesForBatchRelation.value.length === 0) {
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
  if (selectedCategoriesForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何分类')
    return
  }
  
  batchRelationLoading.value = true
  
  try {
    // 一次性传入所有分类 ID，单次请求完成
    await batchSaveCategorySiteRelations({
      categoryIds: selectedCategoriesForBatchRelation.value.map(cat => cat.id),
      includeSiteIds: batchRelatedSiteIds.value
    })
    
    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedCategoriesForBatchRelation.value.length} 个分类关联 ${relateCount} 个网站`
      : `成功取消 ${selectedCategoriesForBatchRelation.value.length} 个分类的所有共享关联`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchCategorySites(selectedCategoriesForBatchRelation.value.map(cat => cat.id))
    const refreshMap2 = refreshRes2.data || {}
    categoryRelationDetails.value = selectedCategoriesForBatchRelation.value.map(cat => {
      const sites = refreshMap2[cat.id] || []
      return {
        categoryId: cat.id,
        categoryName: cat.name,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== cat.siteId).map(s => s.siteId),
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

// 工具函数：字段名转换
/** 获取字段显示名称（英文转中文） */
function getFieldDisplayName(fieldName) {
  const fieldNameMap = {
    'name': '分类名称',
    'description': '分类描述'
  }
  return fieldNameMap[fieldName] || fieldName
}

/** 从显示名称获取字段名（中文转英文） */
function getFieldNameFromDisplay(displayName) {
  const displayNameMap = {
    '分类名称': 'name',
    '分类描述': 'description'
  }
  return displayNameMap[displayName] || displayName
}

/** 导出数据 */
async function handleExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要导出的分类')
    return
  }
  
  // 重置选项
  exportIncludeTranslations.value = false
  exportSelectedLanguages.value = []
  
  // 加载可用语言列表
  try {
    const currentSiteId = queryParams.value.siteId
    if (currentSiteId && !isPersonalSite(currentSiteId)) {
      const { getSupportedLocales } = await import('@/api/gamebox/translation')
      const response = await getSupportedLocales(currentSiteId)
      if (response && response.data) {
        // API 返回的是 [{value: "zh-CN", label: "简体中文", isDefault: "true"}, ...]
        // 找到默认语言
        const defaultLang = response.data.find(lang => lang.isDefault === 'true')
        exportDefaultLanguage.value = defaultLang ? defaultLang.value : ''
        
        // 只显示非默认语言供用户选择，默认语言会自动包含在导出中
        exportAvailableLanguages.value = response.data
          .filter(lang => lang && lang.value && lang.isDefault !== 'true') // 过滤掉默认语言
          .map(lang => ({
            code: lang.value,
            name: lang.label
          }))
      }
    } else {
      exportAvailableLanguages.value = []
      exportDefaultLanguage.value = ''
    }
  } catch (error) {
    console.warn('加载可用语言失败:', error)
    exportAvailableLanguages.value = []
    exportDefaultLanguage.value = ''
  }
  
  exportDialogOpen.value = true
}

/** 全站导出 */
function handleFullExport() {
  fullExportDialogOpen.value = true
}

/** 确认全站导出 */
async function confirmFullExport() {
  try {
    fullExportLoading.value = true
    
    // 获取所有分类数据（包括默认配置和所有站点）
    const response = await listCategory({
      pageNum: 1,
      pageSize: 100000  // 使用一个很大的数字获取所有数据
    })
    
    const allCategories = response.rows || []
    
    if (allCategories.length === 0) {
      proxy.$modal.msgWarning('没有可导出的分类数据')
      fullExportLoading.value = false
      return
    }
    
    // 获取所有分类的翻译数据
    const categoriesWithTranslations = await Promise.all(
      allCategories.map(async (category) => {
        let translations = {}
        try {
          const translationResponse = await getEntityTranslations('category', category.id)
          if (translationResponse && translationResponse.data) {
            translations = translationResponse.data
          }
        } catch (error) {
          console.warn(`获取分类 ${category.id} 的翻译数据失败:`, error)
        }
        return { ...category, translations }
      })
    )
    
    // 处理分类数据
    const processedCategories = categoriesWithTranslations.map((category, index) => {
      const row = {
        分类虚拟ID: index + 1, // 添加虚拟ID（基于行号）
        分类名称: category.name,
        分类标识: category.slug,
        分类类型: category.categoryType,
        网站虚拟ID: category.siteId || 0,
        网站编码: getSiteCode(category.siteId),
        父级分类标识: getParentCategorySlug(category.parentId),
        图标: category.icon,
        排序: category.sortOrder,
        状态: category.status === '1' ? '启用' : '禁用',
        备注: category.remark
      }
      
      // 只有文章类型才有isSection字段
      if (category.categoryType === 'article') {
        // 修复板块判断逻辑：处理字符串和数字类型的parentId
        const parentId = category.parentId
        const isSection = !parentId || parentId === 0 || parentId === '0' || parentId === null
        row.是否板块 = isSection ? '是' : '否'
      }
      
      return row
    })
    
    // 创建虚拟ID到分类标识的映射
    const virtualIdToSlugMap = {}
    categoriesWithTranslations.forEach((category, index) => {
      virtualIdToSlugMap[index + 1] = category.slug
    })
    
    // 准备翻译数据 - 添加虚拟ID关联
    const translationData = []
    categoriesWithTranslations.forEach((category, index) => {
      const virtualId = index + 1 // 使用虚拟ID
      if (category.translations && Array.isArray(category.translations)) {
        category.translations.forEach(translationItem => {
          if (translationItem.fieldName && translationItem.locale && translationItem.fieldValue) {
            translationData.push({
              分类虚拟ID: virtualId,
              分类标识: category.slug,
              网站虚拟ID: category.siteId || 0,
              字段名: getFieldDisplayName(translationItem.fieldName),
              语言代码: translationItem.locale,
              翻译值: translationItem.fieldValue
            })
          }
        })
      } else if (category.translations && typeof category.translations === 'object') {
        Object.entries(category.translations).forEach(([locale, fields]) => {
          if (fields && typeof fields === 'object') {
            Object.entries(fields).forEach(([fieldName, value]) => {
              if (value !== null && value !== undefined && value !== '') {
                translationData.push({
                  分类虚拟ID: virtualId,
                  分类标识: category.slug,
                  网站虚拟ID: category.siteId || 0,
                  字段名: getFieldDisplayName(fieldName),
                  语言代码: locale,
                  翻译值: value
                })
              }
            })
          }
        })
      }
    })
    
    // 收集所有网站ID，用于获取网站信息
    const siteIds = new Set()
    allCategories.forEach(cat => {
      if (cat.siteId) siteIds.add(cat.siteId)
    })
    siteIds.add(0) // 始终包含默认配置
    
    // 获取网站关联关系数据
    const relationDataRaw = []
    for (const category of allCategories) {
      try {
        // 查找对应的虚拟ID
        const categoryIndex = categoriesWithTranslations.findIndex(c => c.slug === category.slug)
        const virtualId = categoryIndex >= 0 ? categoryIndex + 1 : null
        
        // 获取所有网站关系（包括关联和排除）
        const relationResponse = await getCategorySites(category.id)
        if (relationResponse && relationResponse.data) {
          relationResponse.data.forEach(rel => {
            siteIds.add(rel.siteId) // 收集网站ID
            
            // 根据 relationType 区分关联类型
            const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
            
            const relationData = {
              分类虚拟ID: virtualId,
              分类标识: category.slug,
              关联类型: relationType,
              创建者网站虚拟ID: category.siteId || 0,
              网站虚拟ID: rel.siteId
            }
            
            // 只有关联类型才有可见性字段
            if (rel.relationType !== 'exclude') {
              relationData.可见性 = rel.isVisible === '1' ? '显示' : '隐藏'
            }
            
            relationDataRaw.push(relationData)
          })
        }
      } catch (error) {
        console.warn(`获取分类 ${category.id} 的关联关系失败:`, error)
      }
    }
    
    // 获取所有网站详细信息并建立虚拟ID映射
    const sitesData = []
    const siteIdToVirtualIdMap = new Map()
    let realSiteIndex = 0
    
    // 默认配置/个人站的虚拟ID固定为0，方便导入时保持兼容
    if (personalSiteId.value) {
      siteIdToVirtualIdMap.set(personalSiteId.value, 0)
      const _personalSite = siteList.value.find(s => s.id === personalSiteId.value)
      sitesData.push({
        网站虚拟ID: 0,
        网站名称: _personalSite ? _personalSite.name : '默认配置',
        网站编码: 'default',
        网站域名: _personalSite ? (_personalSite.domain || '') : '',
        默认语言: _personalSite ? (_personalSite.defaultLocale || '') : '',
        状态: '正常'
      })
    }
    
    // 为真实网站分配虚拟ID
    for (const siteId of siteIds) {
      if (isPersonalSite(siteId)) continue // 跳过默认配置/个人站
      
      const site = siteList.value.find(s => s.id === siteId)
      if (site) {
        realSiteIndex++
        const virtualId = realSiteIndex
        siteIdToVirtualIdMap.set(siteId, virtualId)
        
        sitesData.push({
          网站虚拟ID: virtualId,
          网站名称: site.name,
          网站编码: site.code,
          网站域名: site.domain,
          默认语言: site.defaultLocale,
          状态: site.status === '1' ? '正常' : '停用'
        })
      }
    }
    
    // 将关联关系中的真实ID替换为虚拟ID
    const relationData = relationDataRaw.map(rel => {
      const result = {
        分类虚拟ID: rel.分类虚拟ID,
        分类标识: rel.分类标识,
        关联类型: rel.关联类型,
        创建者网站虚拟ID: siteIdToVirtualIdMap.get(rel.创建者网站虚拟ID) || 0,
        网站虚拟ID: siteIdToVirtualIdMap.get(rel.网站虚拟ID) || 0
      }
      
      // 只有关联类型才有可见性字段
      if (rel.关联类型 === '关联' && rel.可见性) {
        result.可见性 = rel.可见性
      }
      
      return result
    })
    
    // 检查是否包含网站分类并提示用户
    const websiteCategories = allCategories.filter(cat => cat.categoryType === 'website')
    
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, processedCategories, translationData, relationData)
    } else {
      await exportFullDataToJSON(sitesData, processedCategories, translationData, relationData)
    }
    
    let successMessage = `成功导出 ${sitesData.length} 个网站的 ${allCategories.length} 条分类数据（包含 ${translationData.length} 条翻译、${relationData.length} 条关联关系）`
    
    if (websiteCategories.length > 0) {
      successMessage += `\n\n注意：已导出 ${websiteCategories.length} 个网站分类，请在导入时注意网站分类的特殊处理。`
    }
    
    proxy.$modal.msgSuccess(successMessage)
    fullExportDialogOpen.value = false
    
  } catch (error) {
    console.error('全站导出失败:', error)
    proxy.$modal.msgError('全站导出失败: ' + (error.message || '未知错误'))
  } finally {
    fullExportLoading.value = false
  }
}

/** 导出全站数据为Excel */
async function exportFullDataToExcel(sitesData, categoryData, translationData, relationData) {
  const XLSX = await import('xlsx')
  
  const wb = XLSX.utils.book_new()
  
  // 网站列表sheet（第一个sheet）
  if (sitesData.length > 0) {
    const sitesWs = XLSX.utils.json_to_sheet(sitesData)
    XLSX.utils.book_append_sheet(wb, sitesWs, "网站列表")
  }
  
  // 分类数据sheet
  const categoryWs = XLSX.utils.json_to_sheet(categoryData)
  XLSX.utils.book_append_sheet(wb, categoryWs, "分类数据")
  
  // 翻译数据sheet
  if (translationData.length > 0) {
    const translationWs = XLSX.utils.json_to_sheet(translationData)
    XLSX.utils.book_append_sheet(wb, translationWs, "翻译数据")
  }
  
  // 关联关系sheet
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    XLSX.utils.book_append_sheet(wb, relationWs, "网站关联")
  }
  
  const fileName = `分类全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出全站数据为JSON */
function exportFullDataToJSON(sitesData, categoryData, translationData, relationData) {
  const exportData = {
    sites: sitesData,
    categories: categoryData,
    translations: translationData,
    relations: relationData,
    exportTime: new Date().toISOString(),
    version: '1.0'
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `分类全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
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

/** 确认导出 */
async function confirmExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要导出的分类')
    return
  }
  
  exportLoading.value = true
  
  try {
    // 获取选中的分类数据
    const exportData = categoryList.value.filter(category => selectedIds.includes(category.id))
    
    // 获取所有相关的分类数据（包括父级和子级）
    const allCategoryData = await getAllRelatedCategories(selectedIds)
    
    // 获取翻译数据
    const exportDataWithTranslations = await Promise.all(
      allCategoryData.map(async (category) => {
        let translations = {}
        try {
          const translationResponse = await getEntityTranslations('category', category.id)
          if (translationResponse && translationResponse.data) {
            translations = translationResponse.data
          }
        } catch (error) {
          console.warn(`获取分类 ${category.id} 的翻译数据失败:`, error)
        }
        return { ...category, translations }
      })
    )

    // 处理分类数据（供其他网站复用，不携带网站信息）
    const processedData = exportDataWithTranslations.map(category => {
      const row = {
        分类名称: category.name,
        分类标识: category.slug,
        分类类型: category.categoryType,
        父级分类标识: getParentCategorySlug(category.parentId),
        图标: category.icon,
        排序: category.sortOrder,
        备注: category.remark,
        // 状态：始终导出分类本身的状态（启用/禁用），而不是关联模式下的可见性
        状态: category.status === '1' ? '启用' : '禁用'
      }
      
      // 只有文章类型才有isSection字段
      if (category.categoryType === 'article') {
        // 修复板块判断逻辑：处理字符串和数字类型的parentId
        const parentId = category.parentId
        const isSection = !parentId || parentId === 0 || parentId === '0' || parentId === null
        row.是否板块 = isSection ? '是' : '否'
      }
      
      return row
    })

    // 准备翻译数据（使用分类标识）
    const translationData = []
    
    // 只有当用户选择包含翻译时才导出翻译数据
    if (exportIncludeTranslations.value) {
      exportDataWithTranslations.forEach(category => {
        if (category.translations && Array.isArray(category.translations)) {
          // 翻译数据是数组格式：[{fieldName, locale, fieldValue}, ...]
          category.translations.forEach(translationItem => {
            if (translationItem.fieldName && translationItem.locale && translationItem.fieldValue) {
              // 默认语言总是包含，或者用户选择的语言
              const isDefaultLanguage = exportDefaultLanguage.value && translationItem.locale === exportDefaultLanguage.value
              const isSelectedLanguage = exportSelectedLanguages.value.includes(translationItem.locale)
              
              if (isDefaultLanguage || isSelectedLanguage) {
                translationData.push({
                  分类标识: category.slug,
                  字段名: getFieldDisplayName(translationItem.fieldName),
                  语言代码: translationItem.locale,
                  翻译值: translationItem.fieldValue
                })
              }
            }
          })
        } else if (category.translations && typeof category.translations === 'object') {
          // 兼容嵌套对象格式：{locale: {fieldName: value}}
          Object.entries(category.translations).forEach(([locale, fields]) => {
            // 默认语言总是包含，或者用户选择的语言
            const isDefaultLanguage = exportDefaultLanguage.value && locale === exportDefaultLanguage.value
            const isSelectedLanguage = exportSelectedLanguages.value.includes(locale)
            
            if (isDefaultLanguage || isSelectedLanguage) {
              if (fields && typeof fields === 'object') {
                Object.entries(fields).forEach(([fieldName, value]) => {
                  if (value !== null && value !== undefined && value !== '') {
                    translationData.push({
                      分类标识: category.slug,
                      字段名: getFieldDisplayName(fieldName),
                      语言代码: locale,
                      翻译值: value
                    })
                  }
                })
              }
            }
          })
        }
      })
    }

    if (exportFormat.value === 'excel') {
      await exportToExcel(processedData, translationData)
    } else {
      await exportToJSON(processedData, translationData)
    }
    
    let successMsg = `成功导出 ${allCategoryData.length} 条分类数据`
    if (exportIncludeTranslations.value && translationData.length > 0) {
      successMsg += `（包含 ${translationData.length} 条翻译数据）`
    }
    proxy.$modal.msgSuccess(successMsg)
    exportDialogOpen.value = false
    
  } catch (error) {
    console.error('导出数据失败:', error)
    proxy.$modal.msgError('导出数据失败: ' + (error.message || '未知错误'))
  } finally {
    exportLoading.value = false
  }
}

/** 获取所有相关的分类数据（包括父级和子级） */
async function getAllRelatedCategories(selectedIds) {
  try {
    // 根据当前视图模式获取分类数据
    const response = await listCategory({
      pageNum: 1,
      pageSize: 10000,
      siteId: queryParams.value.siteId || 0,
      queryMode: viewMode.value  // 使用当前的查看模式
    })
    
    const allCategories = response.rows || []
    const relatedIds = new Set(selectedIds)
    
    // 递归添加父级分类
    function addParents(categoryId) {
      const category = allCategories.find(cat => cat.id === categoryId)
      if (category && category.parentId && category.parentId > 0) {
        if (!relatedIds.has(category.parentId)) {
          relatedIds.add(category.parentId)
          addParents(category.parentId)
        }
      }
    }
    
    // 递归添加子级分类
    function addChildren(categoryId) {
      const children = allCategories.filter(cat => cat.parentId === categoryId)
      children.forEach(child => {
        if (!relatedIds.has(child.id)) {
          relatedIds.add(child.id)
          addChildren(child.id)
        }
      })
    }
    
    // 为每个选中的分类添加其父级和子级
    selectedIds.forEach(id => {
      addParents(id)
      addChildren(id)
    })
    
    // 返回所有相关的分类数据
    return allCategories.filter(category => relatedIds.has(category.id))
  } catch (error) {
    console.error('获取相关分类数据失败:', error)
    // 如果获取失败，仅返回选中的分类
    return categoryList.value.filter(category => selectedIds.includes(category.id))
  }
}

/** 获取父级分类名称 */
function getParentCategoryName(parentId) {
  if (!parentId || parentId === 0) return '顶级分类'
  const parent = categoryList.value.find(cat => cat.id === parentId)
  return parent ? parent.name : `分类ID: ${parentId}`
}

/** 获取父级分类标识 */
function getParentCategorySlug(parentId) {
  if (!parentId || parentId === 0) return ''
  const parent = categoryList.value.find(cat => cat.id === parentId)
  return parent ? parent.slug : ''
}

/** 获取网站编码 */
function getSiteCode(siteId) {
  if (!siteId || isPersonalSite(siteId)) return 'default'
  const site = siteList.value.find(s => s.id === siteId)
  return site ? site.code : `site_${siteId}`
}

/** 生成分类唯一键（网站编码_slug） */
function getCategoryUniqueKey(siteId, slug) {
  const siteCode = getSiteCode(siteId)
  return `${siteCode}_${slug}`
}

/** 获取父级分类唯一键 */
function getParentCategoryUniqueKey(parentId) {
  if (!parentId || parentId === 0) return ''
  const parent = categoryList.value.find(cat => cat.id === parentId)
  if (!parent) return ''
  return getCategoryUniqueKey(parent.siteId, parent.slug)
}

/** 导出为Excel */
async function exportToExcel(categoryData, translationData, sitesData) {
  const XLSX = await import('xlsx')
  
  const wb = XLSX.utils.book_new()
  
  // 创建分类数据工作表
  const categoryWs = XLSX.utils.json_to_sheet(categoryData)
  const categoryColWidths = [
    { wch: 20 },  // 分类名称
    { wch: 15 },  // 分类标识
    { wch: 15 },  // 分类类型
    { wch: 15 },  // 创建者网站
    { wch: 12 },  // 创建者网站ID
    { wch: 12 },  // 父级分类ID
    { wch: 20 },  // 父级分类名称
    { wch: 10 },  // 是否板块
    { wch: 10 },  // 图标
    { wch: 8 },   // 排序
    { wch: 10 },  // 状态
    { wch: 20 },  // 创建时间
    { wch: 20 },  // 更新时间
    { wch: 30 },  // 备注
    { wch: 15 }   // 临时标识
  ]
  categoryWs['!cols'] = categoryColWidths
  XLSX.utils.book_append_sheet(wb, categoryWs, "分类数据")
  
  // 添加翻译数据sheet
  if (translationData.length > 0) {
    const translationWs = XLSX.utils.json_to_sheet(translationData)
    const translationColWidths = [
      { wch: 15 },  // 分类标识
      { wch: 15 },  // 字段名
      { wch: 10 },  // 语言代码
      { wch: 50 }   // 翻译值
    ]
    translationWs['!cols'] = translationColWidths
    XLSX.utils.book_append_sheet(wb, translationWs, "翻译数据")
  }
  
  const fileName = `分类数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出为JSON */
function exportToJSON(categoryData, translationData) {
  const exportData = {
    categories: categoryData,
    translations: translationData
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `分类数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
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
    proxy.$modal.msgWarning('仅在创建者模式下支持导入功能')
    return
  }
  
  importDialogOpen.value = true
  importPreviewData.value = []
  importTranslationsData.value = []
  importFile.value = null
}

/** 处理文件选择 */
async function handleFileChange(file) {
  importFile.value = file
  
  try {
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.name.endsWith('.xlsx')) {
      parsedData = await parseExcelData(fileData)
    } else {
      parsedData = parseJSONData(fileData)
    }
    
    // 解析各种数据
    importPreviewData.value = validateAndTransformImportData(parsedData.categories || [])
    importTranslationsData.value = parsedData.translations || []
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + (error.message || '未知错误'))
    importPreviewData.value = []
    importTranslationsData.value = []
  }
}

/** 处理文件移除 */
function handleFileRemove() {
  importFile.value = null
  importPreviewData.value = []
  importTranslationsData.value = []
  return true
}

/** 读取文件数据 */
function readFileData(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = (e) => {
      resolve(e.target.result)
    }
    
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    
    if (file.name.endsWith('.xlsx')) {
      reader.readAsArrayBuffer(file)
    } else {
      reader.readAsText(file, 'utf-8')
    }
  })
}

/** 解析Excel数据 */
async function parseExcelData(arrayBuffer) {
  const XLSX = await import('xlsx')
  const workbook = XLSX.read(arrayBuffer, { type: 'array' })
  
  const result = { categories: [], translations: [] }
  
  // 解析分类数据
  const categorySheetName = workbook.SheetNames.find(name => 
    name === '分类数据' || name === 'categories'
  ) || workbook.SheetNames[0]
  if (categorySheetName && workbook.Sheets[categorySheetName]) {
    result.categories = XLSX.utils.sheet_to_json(workbook.Sheets[categorySheetName])
  }
  
  // 解析翻译数据
  const translationSheetName = workbook.SheetNames.find(name => 
    name === '翻译数据' || name === 'translations'
  )
  if (translationSheetName && workbook.Sheets[translationSheetName]) {
    result.translations = XLSX.utils.sheet_to_json(workbook.Sheets[translationSheetName])
  }
  
  return result
}

/** 解析JSON数据 */
function parseJSONData(jsonString) {
  try {
    const data = JSON.parse(jsonString)
    return {
      categories: data.categories || [],
      translations: data.translations || []
    }
  } catch (error) {
    throw new Error('JSON格式不正确')
  }
}

/** 验证和转换导入数据 */
function validateAndTransformImportData(rawData) {
  if (!Array.isArray(rawData) || rawData.length === 0) {
    return []
  }
  
  return rawData.map((item, index) => {
    // 支持中英文字段名映射
    const fieldMapping = {
      '虚拟ID': 'virtualId',
      'virtualId': 'virtualId',
      '分类名称': 'name',
      'name': 'name',
      '分类标识': 'slug',
      'slug': 'slug',
      '分类类型': 'categoryType',
      'categoryType': 'categoryType',
      '父级分类标识': 'parentSlug',
      'parentSlug': 'parentSlug',
      '是否板块': 'isSection',
      'isSection': 'isSection',
      '图标': 'icon',
      'icon': 'icon',
      '排序': 'sortOrder',
      'sortOrder': 'sortOrder',
      '状态': 'status',
      'status': 'status',
      '备注': 'remark',
      'remark': 'remark'
    }
    
    const transformedItem = {}
    
    Object.entries(item).forEach(([key, value]) => {
      const mappedKey = fieldMapping[key] || key
      if (mappedKey && value !== null && value !== undefined && value !== '') {
        transformedItem[mappedKey] = value
      }
    })
    
    // 处理状态字段（兼容"启用/禁用"和"显示/隐藏"两种格式）
    if (transformedItem.status) {
      if (transformedItem.status === '启用' || transformedItem.status === '显示' || transformedItem.status === '1') {
        transformedItem.status = '1'
      } else {
        transformedItem.status = '0'
      }
    } else {
      transformedItem.status = '1'  // 默认启用
    }
    
    // 处理是否板块字段（确保总是有值）
    if (transformedItem.isSection === '是' || transformedItem.isSection === '1' || transformedItem.isSection === 1) {
      transformedItem.isSection = '1'
    } else {
      transformedItem.isSection = '0'  // 默认为普通分类
    }
    
    // 导入时使用当前选择的网站
    transformedItem.siteId = queryParams.value.siteId || 0
    
    return transformedItem
  })
}

/** 取消导入 */
/** 确认导入 */
async function confirmImport() {
  if (importPreviewData.value.length === 0) {
    proxy.$modal.msgWarning('没有可导入的分类数据')
    return
  }
  
  try {
    await proxy.$modal.confirm(`确认要导入 ${importPreviewData.value.length} 条分类数据吗？\n注意：这将重建分类层级结构和关联关系。`)
    
    importLoading.value = true
    
    // 按层级顺序导入分类数据
    await importCategoriesWithHierarchy()
    
    proxy.$modal.msgSuccess('导入成功！')
    importDialogOpen.value = false
    getList()
    
  } catch (error) {
    importLoading.value = false
    if (error !== 'cancel') {
      console.error('导入数据失败:', error)
      proxy.$modal.msgError('导入数据失败: ' + (error.message || '未知错误'))
    }
  }
}

/** 全站导入 */
function handleFullImport() {
  fullImportDialogOpen.value = true
  fullImportSites.value = []
  fullImportCategories.value = []
  fullImportTranslations.value = []
  fullImportRelations.value = []
  fullImportFile.value = null
  siteMapping.value = {}
  createDefaultAsNewSite.value = false
  hasDefaultConfig.value = false
}

/** 处理全站导入文件选择 */
async function handleFullImportFileChange(file) {
  fullImportFile.value = file
  
  try {
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.name.endsWith('.xlsx')) {
      parsedData = await parseFullImportExcelData(fileData)
    } else {
      parsedData = parseFullImportJSONData(fileData)
    }
    
    // 设置解析后的数据
    fullImportSites.value = parsedData.sites || []
    fullImportCategories.value = parsedData.categories || []
    fullImportTranslations.value = parsedData.translations || []
    fullImportRelations.value = parsedData.relations || []
    
    // 检查是否包含默认配置
    hasDefaultConfig.value = fullImportSites.value.some(site => site['网站虚拟ID'] === 0)
    
    // 初始化网站映射（尝试自动匹配）
    initializeSiteMapping()
    
  } catch (error) {
    console.error('解析导入文件失败:', error)
    proxy.$modal.msgError('解析导入文件失败: ' + (error.message || '文件格式不正确'))
    fullImportFile.value = null
  }
}

/** 初始化网站映射（尝试根据网站编码自动匹配） */
function initializeSiteMapping() {
  const mapping = {}
  
  fullImportSites.value.forEach(importSite => {
    const sourceId = importSite['网站虚拟ID']
    const sourceCode = importSite['网站编码']
    
    // 跳过默认配置的自动映射
    if (sourceId === 0) {
      return
    }
    
    // 尝试根据网站编码匹配
    const matchedSite = siteList.value.find(site => site.code === sourceCode)
    if (matchedSite) {
      mapping[sourceId] = matchedSite.id
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
    categories: [],
    translations: [],
    relations: []
  }
  
  // 解析网站列表
  if (workbook.SheetNames.includes('网站列表')) {
    const siteSheet = workbook.Sheets['网站列表']
    result.sites = XLSX.utils.sheet_to_json(siteSheet)
  } else {
    throw new Error('导入文件缺少"网站列表"工作表，请确保使用全站导出的文件')
  }
  
  // 解析分类数据
  if (workbook.SheetNames.includes('分类数据')) {
    const categorySheet = workbook.Sheets['分类数据']
    result.categories = XLSX.utils.sheet_to_json(categorySheet)
  }
  
  // 解析翻译数据
  if (workbook.SheetNames.includes('翻译数据')) {
    const translationSheet = workbook.Sheets['翻译数据']
    result.translations = XLSX.utils.sheet_to_json(translationSheet)
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
  const jsonData = JSON.parse(new TextDecoder().decode(fileData))
  
  if (!jsonData.sites || !Array.isArray(jsonData.sites)) {
    throw new Error('导入文件缺少网站列表数据，请确保使用全站导出的文件')
  }
  
  return {
    sites: jsonData.sites || [],
    categories: jsonData.categories || [],
    translations: jsonData.translations || [],
    relations: jsonData.relations || []
  }
}

/** 处理全站导入文件移除 */
function handleFullImportFileRemove() {
  fullImportFile.value = null
  fullImportSites.value = []
  fullImportCategories.value = []
  fullImportTranslations.value = []
  fullImportRelations.value = []
  siteMapping.value = {}
  hasDefaultConfig.value = false
  createDefaultAsNewSite.value = false
}

/** 取消全站导入 */
/** 确认全站导入 */
async function confirmFullImport() {
  try {
    // 构建导入摘要信息
    let summary = `即将导入：\n`
    summary += `• ${fullImportCategories.value.length} 条分类数据\n`
    if (fullImportTranslations.value.length > 0) {
      summary += `• ${fullImportTranslations.value.length} 条翻译数据\n`
    }
    if (fullImportRelations.value.length > 0) {
      summary += `• ${fullImportRelations.value.length} 条关联关系\n`
    }
    
    // 检查是否包含网站分类并提示用户
    const websiteCategories = fullImportCategories.value.filter(cat => cat['分类类型'] === 'website')
    const defaultWebsiteCategories = websiteCategories.filter(cat => (cat['网站虚拟ID'] || 0) === 0)
    const mappedDefaultWebsite = defaultWebsiteCategories.filter(cat => {
      const targetSiteId = siteMapping.value[0]
      return targetSiteId && !isPersonalSite(targetSiteId)
    })
    
    if (websiteCategories.length > 0) {
      summary += `\n⚠️ 网站分类提示：\n`
      summary += `• 数据中包含 ${websiteCategories.length} 个网站分类\n`
      if (defaultWebsiteCategories.length > 0) {
        summary += `• 其中 ${defaultWebsiteCategories.length} 个来自默认配置\n`
        if (mappedDefaultWebsite.length > 0) {
          summary += `• ${mappedDefaultWebsite.length} 个默认配置的网站分类将被跳过（不允许映射到其他网站）\n`
        }
      }
    }
    
    summary += `\n网站映射：\n`
    
    if (hasDefaultConfig.value && createDefaultAsNewSite.value) {
      const targetSite = siteList.value.find(s => s.id === siteMapping.value[0])
      summary += `• 默认配置 → ${targetSite?.name || '未知'}\n`
    }
    
    const realSites = fullImportSites.value.filter(s => s['网站ID'] > 0)
    realSites.forEach(site => {
      const targetSite = siteList.value.find(s => s.id === siteMapping.value[site['网站虚拟ID']])
      summary += `• ${site['网站名称']} → ${targetSite?.name || '未知'}\n`
    })
    
    await proxy.$modal.confirm(summary + '\n确认开始导入吗？', '全站数据导入确认')
    
    fullImportLoading.value = true
    
    // 执行导入
    await processFullImport()
    
    proxy.$modal.msgSuccess('全站数据导入成功！')
    fullImportDialogOpen.value = false
    getList()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('全站导入失败:', error)
      proxy.$modal.msgError('全站导入失败: ' + (error.message || '未知错误'))
    }
  } finally {
    fullImportLoading.value = false
  }
}

/** 处理全站导入 */
async function processFullImport() {
  const slugToNewIdMap = new Map() // 旧slug到新ID的映射（按目标网站分组）
  const siteSlugMap = {} // 按网站分组的slug映射 {siteId: {slug: newId}}
  
  // 初始化每个目标网站的slug映射
  Object.values(siteMapping.value).forEach(targetSiteId => {
    if (!siteSlugMap[targetSiteId]) {
      siteSlugMap[targetSiteId] = {}
    }
  })
  
  // 1. 处理分类数据导入（按网站和层级）
  const categoriesBySite = {}
  
  fullImportCategories.value.forEach(cat => {
    const sourceSiteId = cat['网站虚拟ID'] || 0
    const targetSiteId = siteMapping.value[sourceSiteId]
    
    if (!targetSiteId) {
      console.warn(`分类 ${cat['分类标识']} 的源网站 ${sourceSiteId} 未映射，跳过`)
      return
    }
    
    // 如果是默认配置（个人站点）映射到其他网站，排除网站分类
    if (isPersonalSite(sourceSiteId) && !isPersonalSite(targetSiteId) && cat['分类类型'] === 'website') {
      console.warn(`跳过从默认配置映射到网站 ${targetSiteId} 的网站分类: ${cat['分类标识']}`)
      return
    }
    
    if (!categoriesBySite[targetSiteId]) {
      categoriesBySite[targetSiteId] = []
    }
    
    categoriesBySite[targetSiteId].push({
      ...cat,
      targetSiteId
    })
  })
  
  // 2. 按目标网站分别导入分类
  for (const [targetSiteId, categories] of Object.entries(categoriesBySite)) {
    await importCategoriesForSite(parseInt(targetSiteId), categories, siteSlugMap[targetSiteId])
  }
  
  // 3. 导入翻译数据
  if (fullImportTranslations.value.length > 0) {
    await importTranslations(siteSlugMap)
  }
  
  // 4. 先处理默认配置关联（如果需要）
  if (hasDefaultConfig.value && createDefaultAsNewSite.value) {
    await createRelationsForConvertedDefault(siteSlugMap)
  }
  
  // 5. 处理剩余的关联关系
  if (fullImportRelations.value.length > 0) {
    await importRelations(siteSlugMap)
  }
}

/** 为指定网站导入分类 */
async function importCategoriesForSite(targetSiteId, categories, slugMap) {
  console.log(`开始为网站 ${targetSiteId} 导入分类...`)
  
  if (!categories || categories.length === 0) {
    console.log(`网站 ${targetSiteId} 没有分类数据`)
    return
  }

  // 完全复制单独导入的数据转换逻辑
  const transformedData = validateAndTransformImportData(categories)
  
  // 设置目标网站ID，覆盖validateAndTransformImportData中设置的siteId
  transformedData.forEach(item => {
    item.siteId = targetSiteId
    // 清理可能的中文字段
    delete item['网站ID']
    delete item['网站编码']
    delete item['网站名称']
    delete item['创建时间']
    delete item['更新时间']
    delete item.targetSiteId  // 清理可能的重复字段
  })

  // 创建本地映射来跟踪已创建的分类
  const localSlugToIdMapping = {}
  
  // 分离板块和普通分类
  const sections = []
  const regularCategories = []
  
  transformedData.forEach(category => {
    // 使用与单独导入相同的板块判断逻辑：只根据isSection字段判断
    const isSection = category.isSection === '1' || category.isSection === 1
    
    if (isSection) {
      category.isSection = '1'
      sections.push(category)
    } else {
      category.isSection = '0'
      regularCategories.push(category)
    }
  })
  
  console.log(`检测到 ${sections.length} 个板块，${regularCategories.length} 个普通分类`)

  // 1. 先创建板块（完全复制单独导入的逻辑）
  for (const sectionItem of sections) {
    try {
      const { parentSlug, ...sectionInfo } = sectionItem
      sectionInfo.isSection = '1'
      
      console.log('准备创建板块：', sectionInfo)
      const sectionResponse = await addCategory(sectionInfo)
      
      if (sectionResponse.code === 200 && sectionResponse.data) {
        localSlugToIdMapping[sectionItem.slug] = sectionResponse.data
        slugMap[sectionItem.slug] = sectionResponse.data  // 同步更新传入的映射
        console.log(`板块创建成功: ${sectionItem.name}, ID: ${sectionResponse.data}`)
      }
    } catch (error) {
      console.error(`创建板块失败: ${sectionItem.name}`, error)
    }
  }

  // 2. 再创建普通分类（完全复制单独导入的逻辑）
  for (const categoryItem of regularCategories) {
    try {
      const { parentSlug, ...categoryInfo } = categoryItem
      
      // 查找父级分类ID
      if (parentSlug) {
        const parentId = localSlugToIdMapping[parentSlug] || slugMap[parentSlug]
        if (parentId) {
          categoryInfo.parentId = parentId
        } else {
          console.warn(`未找到父级分类: ${parentSlug}`)
        }
      }
      
      categoryInfo.isSection = '0'
      
      console.log('准备创建分类：', categoryInfo)
      const categoryResponse = await addCategory(categoryInfo)
      
      if (categoryResponse.code === 200 && categoryResponse.data) {
        localSlugToIdMapping[categoryItem.slug] = categoryResponse.data
        slugMap[categoryItem.slug] = categoryResponse.data  // 同步更新传入的映射
        console.log(`分类创建成功: ${categoryItem.name}, ID: ${categoryResponse.data}`)
      }
    } catch (error) {
      console.error(`创建分类失败: ${categoryItem.name}`, error)
    }
  }

  console.log(`网站 ${targetSiteId} 分类导入完成`)
}

// 工具函数：规范化状态值
function normalizeStatus(status) {
  if (!status) return '0'
  const str = String(status).toLowerCase()
  if (str === '1' || str === 'true' || str === '启用' || str === 'enabled' || str === 'active') {
    return '1'
  }
  return '0'
}

/** 导入翻译数据 */
async function importTranslations(siteSlugMap) {
  // 按虚拟ID分组翻译数据
  const translationsByVirtualId = {}
  
  fullImportTranslations.value.forEach(trans => {
    const virtualId = trans['虚拟ID']
    const slug = trans['分类标识']
    
    if (!virtualId) {
      console.warn('翻译数据缺少虚拟ID，跳过:', trans)
      return
    }
    
    // 检查对应的分类是否是网站分类，如果是则跳过
    const correspondingCategory = fullImportCategories.value.find(cat => 
      cat['分类标识'] === slug && cat['虚拟ID'] === virtualId
    )
    if (correspondingCategory && correspondingCategory['分类类型'] === 'website') {
      console.warn(`跳过网站分类的翻译数据: ${slug}`)
      return
    }
    
    if (!translationsByVirtualId[virtualId]) {
      translationsByVirtualId[virtualId] = {
        slug: slug,
        translations: []
      }
    }
    translationsByVirtualId[virtualId].translations.push(trans)
  })
  
  // 为每个分类导入翻译
  for (const [virtualId, data] of Object.entries(translationsByVirtualId)) {
    const slug = data.slug
    const translations = data.translations
    
    // 查找该slug在各个目标网站的新ID
    for (const [siteId, slugMap] of Object.entries(siteSlugMap)) {
      const newCategoryId = slugMap[slug]
      if (newCategoryId) {
        // 构建翻译数据
        const translationsData = {}
        translations.forEach(trans => {
          const locale = trans['语言代码']
          const fieldName = getFieldNameFromDisplay(trans['字段名'])
          const value = trans['翻译值']
          
          if (!translationsData[locale]) {
            translationsData[locale] = {}
          }
          translationsData[locale][fieldName] = value
        })
        
        // 保存翻译
        try {
          for (const [locale, fields] of Object.entries(translationsData)) {
            await batchSaveTranslations({
              entityType: 'category',
              entityId: parseInt(newCategoryId),
              locale: locale,
              translations: fields
            })
          }
        } catch (error) {
          console.error(`保存分类 ${slug} 的翻译失败:`, error)
        }
      }
    }
  }
}

/** 导入关联关系 */
async function importRelations(siteSlugMap) {
  console.log(`开始导入剩余的 ${fullImportRelations.value.length} 条关联关系...`)
  
  for (const relation of fullImportRelations.value) {
    const virtualId = relation['分类虚拟ID']
    const slug = relation['分类标识']
    const creatorSiteVirtualId = relation['创建者网站虚拟ID'] || 0
    const relatedSiteVirtualId = relation['网站虚拟ID']
    const relationType = relation['关联类型'] || '关联'
    const visibility = relation['可见性']
    
    if (!slug) {
      console.warn('关联关系数据缺少分类标识，跳过:', relation)
      continue
    }
    
    // 跳过默认配置的关联关系（已由createRelationsForConvertedDefault处理）
    if (creatorSiteVirtualId === 0 && relationType === '排除') {
      console.log(`跳过默认配置排除关系（已由createRelationsForConvertedDefault处理）: 分类=${slug}, 目标网站虚拟ID=${relatedSiteVirtualId}`)
      continue
    }
    
    // 检查对应的分类是否是网站分类，如果是则跳过
    const correspondingCategory = fullImportCategories.value.find(cat => 
      cat['分类标识'] === slug && cat['分类虚拟ID'] === virtualId
    )
    if (correspondingCategory && correspondingCategory['分类类型'] === 'website') {
      console.warn(`跳过网站分类的关联关系: ${slug}`)
      continue
    }
    
    // 映射创建者网站ID
    const targetCreatorSiteId = siteMapping.value[creatorSiteVirtualId]
    if (!targetCreatorSiteId && creatorSiteVirtualId !== 0) {
      console.warn(`关联关系中的创建者网站 ${creatorSiteVirtualId} 未映射，跳过`)
      continue
    }
    
    // 映射关联网站ID
    const targetRelatedSiteId = siteMapping.value[relatedSiteVirtualId]
    if (!targetRelatedSiteId) {
      console.warn(`关联关系中的关联网站 ${relatedSiteVirtualId} 未映射，跳过`)
      continue
    }
    
    // 查找分类ID（使用创建者网站和分类标识）
    const creatorSlugMap = siteSlugMap[targetCreatorSiteId || siteMapping.value[0]]
    if (!creatorSlugMap) {
      console.warn(`未找到创建者网站 ${targetCreatorSiteId} 的分类映射`)
      continue
    }
    
    const categoryId = creatorSlugMap[slug]
    if (!categoryId) {
      console.warn(`未找到分类 ${slug} 在网站 ${targetCreatorSiteId} 的ID`)
      continue
    }
    
    // 创建关联关系
    try {
      const requestData = {
        categoryId: categoryId,
        siteId: targetRelatedSiteId,
        relationType: relationType === '排除' ? 'exclude' : 'include'
      }
      
      // 只有关联类型才设置可见性
      if (relationType === '关联' && visibility) {
        requestData.isVisible = visibility === '显示' ? '1' : '0'
      }
      
      await addCategoryToSite(requestData)
      console.log(`关联关系创建成功: category=${categoryId}, site=${targetRelatedSiteId}, type=${relationType}`)
    } catch (error) {
      console.error(`创建关联关系失败: category=${categoryId}, site=${targetRelatedSiteId}`, error)
    }
  }
  
  console.log('剩余关联关系导入完成')
}


/** 为转换的默认配置创建关联关系 */
async function createRelationsForConvertedDefault(siteSlugMap) {
  const defaultTargetSiteId = siteMapping.value[0]
  if (!defaultTargetSiteId) return
  
  const defaultSlugMap = siteSlugMap[defaultTargetSiteId]
  if (!defaultSlugMap) return
  
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
    
    // 为每个转换后的分类检查原来的关联和排除关系
    for (const slug of Object.keys(defaultSlugMap)) {
      const categoryId = defaultSlugMap[slug]
      
      // 检查是否是网站分类，如果是则跳过
      const correspondingCategory = fullImportCategories.value.find(cat => 
        cat['分类标识'] === slug && (cat['网站虚拟ID'] || 0) === 0
      )
      if (correspondingCategory && correspondingCategory['分类类型'] === 'website') {
        console.warn(`跳过网站分类的关联关系创建: ${slug}`)
        continue
      }
      
      // 查找原来的关联关系数据（包括排除和关联）
      const originalRelations = fullImportRelations.value.filter(rel => {
        const relSlug = rel['分类标识']
        const creatorSiteVirtualId = rel['创建者网站虚拟ID'] || 0
        
        return relSlug === slug && creatorSiteVirtualId === 0
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
      
      console.log(`分类 ${slug}: 有关联的网站虚拟ID:`, Array.from(includedSiteVirtualIds), '仅排除的网站虚拟ID:', excludedSiteVirtualIds)
      
      // 第二步：优先处理关联逻辑
      // 处理显式关联关系
      for (const includedVId of includedSiteVirtualIds) {
        const targetSiteId = siteMapping.value[includedVId]
        if (!targetSiteId) {
          console.warn(`关联的网站虚拟ID ${includedVId} 未映射，跳过`)
          continue
        }
        
        // 获取原始关联关系中的可见性设置
        const originalIncludeRel = originalRelations.find(rel => 
          (rel['关联类型'] || '关联') === '关联' && rel['网站虚拟ID'] === includedVId
        )
        const visibility = originalIncludeRel?.['可见性']
        
        try {
          const requestData = {
            categoryId: categoryId,
            siteId: targetSiteId,
            relationType: 'include'
          }
          
          // 设置可见性
          if (visibility) {
            requestData.isVisible = visibility === '显示' ? '1' : '0'
          }
          
          await addCategoryToSite(requestData)
          console.log(`创建关联成功: category=${categoryId}, site=${targetSiteId}, visible=${visibility}`)
        } catch (error) {
          console.warn(`创建关联失败: category=${categoryId}, site=${targetSiteId}`, error.message || '未知错误')
        }
      }
      
      // 第三步：将剩余的排除逻辑逆向转换为关联逻辑
      // 对于每个被映射导入的网站，如果不在排除列表中，则创建关联
      for (const targetSiteId of mappedTargetSiteIds) {
        // 检查该目标网站是否对应被排除的原始网站
        const isExcludedMapped = excludedSiteVirtualIds.some(excludedVId => {
          const mappedSiteId = siteMapping.value[excludedVId]
          return mappedSiteId && mappedSiteId === targetSiteId
        })
        
        // 检查该目标网站是否已经在显式关联列表中
        const isAlreadyIncluded = Array.from(includedSiteVirtualIds).some(includedVId => {
          const mappedSiteId = siteMapping.value[includedVId]
          return mappedSiteId && mappedSiteId === targetSiteId
        })
        
        // 如果既不在排除列表中，也不在显式关联列表中，则创建关联
        if (!isExcludedMapped && !isAlreadyIncluded) {
          try {
            await addCategoryToSite({
              categoryId: categoryId,
              siteId: targetSiteId,
              relationType: 'include'
            })
            console.log(`创建逆向关联成功: category=${categoryId}, site=${targetSiteId}`)
          } catch (error) {
            console.warn(`创建逆向关联失败: category=${categoryId}, site=${targetSiteId}`, error.message || '未知错误')
          }
        } else if (isExcludedMapped) {
          console.log(`跳过网站 ${targetSiteId}，因为它是被排除网站的映射目标`)
        } else {
          console.log(`跳过网站 ${targetSiteId}，因为已在显式关联中处理`)
        }
      }
      
      // 记录已处理的关联关系（所有涉及该分类和默认配置的关联）
      originalRelations.forEach(rel => {
        processedRelations.push(rel)
      })
    }
    
    // 从关联关系列表中移除已处理的关联
    processedRelations.forEach(processedRel => {
      const index = fullImportRelations.value.findIndex(rel => {
        return rel['分类标识'] === processedRel['分类标识'] && 
               (rel['创建者网站虚拟ID'] || 0) === (processedRel['创建者网站虚拟ID'] || 0) &&
               rel['网站虚拟ID'] === processedRel['网站虚拟ID'] &&
               (rel['关联类型'] || '关联') === (processedRel['关联类型'] || '关联')
      })
      if (index !== -1) {
        fullImportRelations.value.splice(index, 1)
      }
    })
    
    console.log(`默认配置关联创建完成，已移除 ${processedRelations.length} 条已处理的关联关系`)
  } catch (error) {
    console.error('为转换的默认配置创建关联关系失败:', error)
    throw error
  }
}

/** 按层级顺序导入分类数据 */
async function importCategoriesWithHierarchy() {
  const categoryData = importPreviewData.value
  const slugToNewIdMap = new Map() // slug到新ID的映射
  
  // 1. 分离板块和普通分类
  const sections = categoryData.filter(cat => cat.isSection === '1' || cat.isSection === 1)
  const categories = categoryData.filter(cat => cat.isSection !== '1' && cat.isSection !== 1)
  
  // 2. 先导入所有板块（板块的parentId必须为0）
  for (const sectionItem of sections) {
    try {
      const { parentSlug, ...sectionInfo } = sectionItem
      
      // 板块的parentId必须为0
      sectionInfo.parentId = 0
      sectionInfo.isSection = '1'
      
      // 设置默认值
      sectionInfo.sortOrder = parseInt(sectionInfo.sortOrder) || 0
      if (!sectionInfo.categoryType) sectionInfo.categoryType = 'game'
      if (!sectionInfo.slug) sectionInfo.slug = sectionInfo.name
      
      const response = await addCategory(sectionInfo)
      const newSectionId = response.data
      
      // 保存slug到新ID的映射
      if (sectionInfo.slug && newSectionId) {
        slugToNewIdMap.set(sectionInfo.slug, newSectionId)
      }
      
    } catch (error) {
      console.error(`创建板块 ${sectionItem.name} 失败:`, error)
    }
  }
  
  // 3. 按层级排序普通分类（父级优先）
  const sortedCategories = []
  const remaining = [...categories]
  
  while (remaining.length > 0) {
    const canProcess = remaining.filter(cat => 
      !cat.parentSlug || cat.parentSlug === '' || slugToNewIdMap.has(cat.parentSlug)
    )
    
    if (canProcess.length === 0) {
      console.warn('检测到循环引用或无效的父级关系，将剩余分类设为顶级')
      remaining.forEach(cat => cat.parentSlug = '')
      continue
    }
    
    sortedCategories.push(...canProcess)
    canProcess.forEach(cat => {
      const index = remaining.indexOf(cat)
      remaining.splice(index, 1)
    })
  }
  
  // 4. 按顺序导入普通分类
  for (const categoryItem of sortedCategories) {
    try {
      const { parentSlug, ...categoryInfo } = categoryItem
      
      // 设置正确的parentId
      if (parentSlug && parentSlug !== '') {
        const newParentId = slugToNewIdMap.get(parentSlug)
        categoryInfo.parentId = newParentId || 0
      } else {
        categoryInfo.parentId = 0
      }
      
      // 设置默认值
      categoryInfo.sortOrder = parseInt(categoryInfo.sortOrder) || 0
      if (!categoryInfo.categoryType) categoryInfo.categoryType = 'game'
      if (!categoryInfo.slug) categoryInfo.slug = categoryInfo.name
      
      // 规范化状态值
      categoryInfo.status = normalizeStatus(categoryInfo.status)
      
      const response = await addCategory(categoryInfo)
      const newCategoryId = response.data
      
      // 保存slug到新ID的映射
      if (categoryInfo.slug && newCategoryId) {
        slugToNewIdMap.set(categoryInfo.slug, newCategoryId)
      }
      
    } catch (error) {
      console.error(`创建分类 ${categoryItem.name} 失败:`, error)
    }
  }
  
  // 5. 处理翻译数据
  if (importTranslationsData.value.length > 0) {
    await importNormalTranslations(slugToNewIdMap)
  }
  
  importLoading.value = false
}

/** 导入翻译数据（普通导入） */
async function importNormalTranslations(slugToNewIdMap) {
  const translationsByCategory = {}
  
  // 按分类slug分组翻译数据
  importTranslationsData.value.forEach(trans => {
    const slug = trans['分类标识'] || trans.categorySlug
    const newId = slugToNewIdMap.get(slug)
    if (!newId) return
    
    const fieldDisplayName = trans['字段名'] || trans.fieldName
    const locale = trans['语言代码'] || trans.locale
    const value = trans['翻译值'] || trans.value
    
    const fieldName = getFieldNameFromDisplay(fieldDisplayName)
    
    if (!translationsByCategory[newId]) {
      translationsByCategory[newId] = {}
    }
    if (!translationsByCategory[newId][locale]) {
      translationsByCategory[newId][locale] = {}
    }
    translationsByCategory[newId][locale][fieldName] = value
  })
  
  // 批量保存翻译数据
  for (const [categoryId, translations] of Object.entries(translationsByCategory)) {
    try {
      for (const [locale, fields] of Object.entries(translations)) {
        await batchSaveTranslations({
          entityType: 'category',
          entityId: parseInt(categoryId),
          locale: locale,
          translations: fields
        })
      }
    } catch (error) {
      console.error(`分类 ${categoryId} 翻译数据导入失败:`, error)
    }
  }
}

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  if (siteList.value.length > 0) {
    loadSiteList()
  }
})

// 初始化：先加载网站列表和分类类型，然后由 loadSiteList 自动加载分类数据
loadSiteList()
loadCategoryTypes()
</script>
