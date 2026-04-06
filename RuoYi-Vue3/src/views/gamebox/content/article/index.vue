<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <!-- 第一行：查看模式和网站选择 -->
      <el-row :gutter="10">
        <el-col :span="24">
          <el-form-item label="查看模式" prop="viewMode">
            <el-radio-group v-model="viewMode" @change="handleViewModeChange">
              <el-radio-button value="creator">
                <el-icon><User /></el-icon>
                <span style="margin-left: 4px;">创建者</span>
              </el-radio-button>
              <el-radio-button value="related">
                <el-icon><Link /></el-icon>
                <span style="margin-left: 4px;">关联网站</span>
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="viewMode === 'creator' ? '创建者网站' : '关联网站'" prop="siteId">
            <SiteSelect v-model="queryParams.siteId" :site-list="siteList" :show-default="viewMode === 'creator'" clearable @change="handleSiteChange" />
          </el-form-item>
          <el-form-item v-if="viewMode === 'creator' && queryParams.siteId && !isPersonalSiteCheck(queryParams.siteId)" label=" " style="margin-left: 0;">
            <el-checkbox v-model="includeDefaultConfig" @change="handleQuery">
              <el-icon style="vertical-align: middle;"><Plus /></el-icon>
              <span style="margin-left: 4px;">含默认配置</span>
            </el-checkbox>
          </el-form-item>
        </el-col>
      </el-row>
      
      <!-- 第二行：搜索条件 -->
      <el-row :gutter="10" style="margin-top: 8px;">
        <el-col :xl="6" :lg="8" :md="12" :sm="24">
          <el-form-item label="文章标题" prop="title">
            <el-input 
              v-model="queryParams.title" 
              placeholder="请输入文章标题" 
              clearable 
              @keyup.enter="handleQuery"
              prefix-icon="Search"
            />
          </el-form-item>
        </el-col>
        <el-col :xl="6" :lg="8" :md="12" :sm="24">
          <el-form-item label="板块" prop="sectionId">
            <el-select v-model="queryParams.sectionId" placeholder="全部板块" clearable @change="handleSectionChange">
              <el-option 
                v-for="section in sectionList" 
                :key="section.id" 
                :label="section.name" 
                :value="section.id"
              >
                <span v-if="section.icon" style="margin-right: 4px">{{ section.icon }}</span>
                {{ section.name }}
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xl="6" :lg="8" :md="12" :sm="24">
          <el-form-item label="分类" prop="categoryId">
            <el-tree-select
              v-model="queryParams.categoryId"
              :data="articleCategoryQueryOptions"
              placeholder="全部分类"
              clearable
              filterable
              node-key="id"
              :props="{ label: 'name', children: 'children', disabled: 'disabled' }"
            >
              <template #default="{ data }">
                <span :style="data.isVisible === '0' ? 'color: #ccc;' : ''">
                  <span v-if="data.icon" style="margin-right: 4px">{{ data.icon }}</span>
                  {{ data.displayName }}
                  <span v-if="!data.siteId || isPersonalSiteCheck(data.siteId)" style="margin-left: 4px; color: #67c23a; font-size: 12px;">[默认配置]</span>
                  <span v-else style="margin-left: 4px; color: #909399; font-size: 12px;">[{{ getSiteName(data.siteId) }}]</span>
                  <span v-if="data.isVisible === '0'" style="margin-left: 4px; color: #f56c6c; font-size: 12px;">[不可用]</span>
                </span>
              </template>
            </el-tree-select>
          </el-form-item>
        </el-col>
        <el-col :xl="6" :lg="8" :md="12" :sm="24">
          <el-form-item label="发布状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
              <el-option label="无发布版本" value="0">
                <el-icon style="color: #909399; margin-right: 8px;"><Edit /></el-icon>
                <span>无发布版本</span>
              </el-option>
              <el-option label="有发布版本" value="1">
                <el-icon style="color: #67C23A; margin-right: 8px;"><CircleCheck /></el-icon>
                <span>有发布版本</span>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xl="6" :lg="24" :md="24" :sm="24">
          <el-form-item label=" " style="margin-left: 0;">
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="viewMode === 'creator'" :gutter="10" style="margin-top: 8px;">
        <el-col :span="24">
          <el-form-item label=" ">
            <el-button type="success" plain icon="Download" @click="handleFullExport" v-hasPermi="['gamebox:article:export']">全站导出</el-button>
            <el-button type="warning" plain icon="Upload" @click="handleFullImport" v-hasPermi="['gamebox:article:import']">全站导入</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:article:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:article:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="CircleClose" :disabled="multiple" @click="handleBatchExclude" v-if="isPersonalSiteCheck(queryParams.siteId)" v-hasPermi="['gamebox:article:edit']">批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Link" :disabled="multiple" @click="handleBatchRelation" v-hasPermi="['gamebox:article:edit']">批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Download" :disabled="multiple" @click="handleExport" v-hasPermi="['gamebox:article:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleSystemImport" v-hasPermi="['gamebox:article:import']">导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="articleTableRef" v-loading="loading" :data="articleList" @selection-change="handleSelectionChange" row-key="id">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="封面图" align="center" prop="coverUrl" width="80">
        <template #default="scope">
          <el-image v-if="scope.row.coverUrl" :src="scope.row.coverUrl" style="width: 60px; height: 40px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="文章标题" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="分类" align="center" prop="categoryId" width="120">
        <template #default="scope">
          <CategoryTag v-if="scope.row.categoryName" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'article', icon: scope.row.categoryIcon }" size="small" />
          <el-tag v-else type="info" size="small">未分类</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="板块" align="center" prop="sectionId" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.sectionName" type="primary" size="small" effect="plain">
            {{ scope.row.sectionName }}
          </el-tag>
          <span v-else style="color: #909399; font-size: 12px;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="作者" align="center" prop="author" width="100" />
      <el-table-column label="浏览量" align="center" prop="viewCount" width="80" />
      <el-table-column label="AI生成" align="center" prop="isAiGenerated" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isAiGenerated === '1' ? 'warning' : 'info'" size="small">
            {{ scope.row.isAiGenerated === '1' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="120" v-if="viewMode === 'creator'">
        <template #default="scope">
          <!-- 创建者模式下显示发布状态（从后端计算的状态） -->
          <el-tag v-if="scope.row.publishStatus === '0'" type="info">未发布</el-tag>
          <el-tag v-else-if="scope.row.publishStatus === '1'" type="warning">部分发布</el-tag>
          <el-tag v-else-if="scope.row.publishStatus === '2'" type="success">已发布</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="publishTime" width="160" />
      <el-table-column label="关联网站" align="center" width="120">
        <template #default="scope">
          <!-- 默认配置显示排除数量或全局可见（可点击管理） -->
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
            v-else 
            type="info" 
            style="cursor: pointer" 
            @click="handleManageSites(scope.row)"
          >
            未配置
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="可见性" align="center" width="100" v-if="viewMode === 'related' && queryParams.siteId && !isPersonalSiteCheck(queryParams.siteId)">
        <template #header>
          <el-tooltip placement="top" effect="light">
            <template #content>
              <div style="max-width: 300px; line-height: 1.6;">
                <p style="margin: 0 0 8px 0; font-weight: bold;">可见性控制说明：</p>
                <p style="margin: 0 0 4px 0;">• <strong>默认配置</strong>：切换会调用排除管理，排除后不显示</p>
                <p style="margin: 0 0 4px 0;">• <strong>自有数据</strong>：批量设置所有语言版本为发布或下架状态</p>
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
            {{ getVisibilityText(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布管理" align="center" prop="defaultLocale" width="120">
        <template #default="scope">
          <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
            <el-tag type="primary" size="small">{{ scope.row.defaultLocale }}</el-tag>
            <el-button 
              link 
              type="primary" 
              size="small"
              @click="handlePublishManagement(scope.row)"
              icon="Promotion"
            >
              管理发布
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="绑定信息" align="center" prop="bindType" width="140">
        <template #default="scope">
          <el-tag 
            v-if="scope.row.bindType === 'game'" 
            type="success" 
            size="small"
            effect="plain"
          >
            游戏主页
          </el-tag>
          <el-tag 
            v-else-if="scope.row.bindType === 'gamebox'" 
            type="warning" 
            size="small"
            effect="plain"
          >
            盒子主页
          </el-tag>
          <span v-else style="color: #c0c4cc; font-size: 13px;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220" fixed="right">
        <template #default="scope">
          <!-- 关联模式下的操作 - 基于relationSource统一判断 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的文章 - 可修改、删除 -->
            <template v-if="scope.row.relationSource === 'own'">
              <el-tooltip :content="`编辑(${scope.row.defaultLocale})`" placement="top">
                <el-button 
                  link 
                  type="primary" 
                  icon="Edit" 
                  @click="handleUpdate(scope.row)" 
                  v-hasPermi="['gamebox:article:edit']" />
              </el-tooltip>
              <el-tooltip content="预览" placement="top">
                <el-button 
                  link 
                  :type="scope.row.storageUrl ? 'success' : 'info'" 
                  icon="View" 
                  :disabled="!scope.row.storageUrl"
                  @click="scope.row.storageUrl && openUrl(scope.row.storageUrl)" />
              </el-tooltip>
              <el-dropdown 
                @command="(command) => handleCommand(command, scope.row)" 
                v-hasPermi="['gamebox:article:edit', 'gamebox:article:remove']">
                <el-button link type="primary" icon="ArrowDown" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item 
                      v-if="checkPermi(['gamebox:article:edit'])"
                      command="bind" 
                      icon="Link">
                      主页绑定
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="checkPermi(['gamebox:article:edit'])"
                      command="sites" 
                      icon="Connection">
                      网站关联
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="scope.row.status === '1' && checkPermi(['gamebox:article:edit'])" 
                      command="unpublish" 
                      icon="RefreshLeft">
                      撤销发布
                    </el-dropdown-item>
                    <el-dropdown-item 
                      v-if="checkPermi(['gamebox:article:remove'])"
                      command="delete" 
                      icon="Delete" 
                      divided>
                      <span style="color: #f56c6c;">删除</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <!-- default: 默认配置 - 可预览、排除/恢复 -->
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tooltip content="预览" placement="top">
                <el-button 
                  link 
                  :type="scope.row.storageUrl ? 'success' : 'info'" 
                  icon="View" 
                  :disabled="!scope.row.storageUrl"
                  @click="scope.row.storageUrl && openUrl(scope.row.storageUrl)" />
              </el-tooltip>
              <el-tag v-if="scope.row.isExcluded === '1'" type="info" size="small">已排除</el-tag>
            </template>
            <!-- shared: 其他网站分享 - 可预览 -->
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-tooltip content="预览" placement="top">
                <el-button 
                  link 
                  :type="scope.row.storageUrl ? 'success' : 'info'" 
                  icon="View" 
                  :disabled="!scope.row.storageUrl"
                  @click="scope.row.storageUrl && openUrl(scope.row.storageUrl)" />
              </el-tooltip>
            </template>
          </template>
          <!-- 创建者模式下的操作 -->
          <template v-else>
            <el-tooltip :content="`编辑(${scope.row.defaultLocale})`" placement="top">
              <el-button 
                link 
                type="primary" 
                icon="Edit" 
                @click="handleUpdate(scope.row)" 
                v-hasPermi="['gamebox:article:edit']" />
            </el-tooltip>
            <el-tooltip content="预览" placement="top">
              <el-button 
                link 
                :type="scope.row.storageUrl ? 'success' : 'info'" 
                icon="View" 
                :disabled="!scope.row.storageUrl"
                @click="scope.row.storageUrl && openUrl(scope.row.storageUrl)" />
            </el-tooltip>
            
            <el-tooltip content="主页绑定" placement="top">
              <el-button 
                link 
                type="warning" 
                icon="Link" 
                @click="handleBindHomepage(scope.row)" 
                v-hasPermi="['gamebox:article:edit']" />
            </el-tooltip>
            <el-tooltip v-if="!isPersonalSiteCheck(scope.row.siteId)" content="网站关联" placement="top">
              <el-button 
                link 
                type="warning" 
                icon="Connection" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:article:edit']" />
            </el-tooltip>
            <el-tooltip v-if="isPersonalSiteCheck(scope.row.siteId)" content="排除管理" placement="top">
              <el-button 
                link 
                type="danger" 
                icon="CircleClose" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:article:edit']" />
            </el-tooltip>
            <el-tooltip v-if="scope.row.status === '1'" content="撤销发布" placement="top">
              <el-button 
                link 
                type="info" 
                icon="RefreshLeft" 
                @click="handleUnpublish(scope.row)" 
                v-hasPermi="['gamebox:article:edit']" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button 
                link 
                type="danger" 
                icon="Delete" 
                @click="handleDelete(scope.row)" 
                v-hasPermi="['gamebox:article:remove']" />
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 网站关联管理对话框 -->
    <SiteRelationManager
      v-model="siteRelationDialogOpen"
      entity-type="article"
      :entity-id="currentArticleIdForSites"
      :entity-name="currentArticleTitleForSites"
      :creator-site-id="currentArticleCreatorSiteId"
      @refresh="getList"
    />

    <!-- 添加或修改文章对话框 -->
    <el-dialog :title="title" v-model="open" width="1200px" append-to-body :close-on-click-modal="false">
      <el-form ref="articleRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="文章标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入文章标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Slug" prop="slug">
              <el-input v-model="form.slug" placeholder="文章路径标识，如：game-guide" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="创建者网站" prop="siteId">
              <SiteSelect
                v-model="form.siteId"
                :site-list="siteList"
                show-default
                default-label="默认配置（全局）"
                clearable
                :disabled="!!form.id"
                width="100%"
                @change="handleFormSiteChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.id">
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
        <el-row v-if="form.siteId !== undefined && form.siteId !== null">
          <el-col :span="24">
            <el-alert
              :title="isPersonalSiteCheck(form.siteId) ? '提示：全局文章只能使用全局分类，所有网站都可以接入使用这些全局分类' : '提示：可以使用未被排除的分类（包括本网站分类、未排除的全局分类、跨站共享分类）'"
              type="info"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="分类" prop="categoryId">
              <el-tree-select
                v-model="form.categoryId"
                :data="articleCategoryTreeOptions"
                placeholder="请选择分类"
                clearable
                filterable
                style="width: 100%"
                node-key="id"
                :props="{ label: 'name', children: 'children', disabled: 'disabled' }"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="游戏盒子" prop="gameBoxIds">
              <el-select v-model="form.gameBoxIds" placeholder="选择游戏盒子（可多选）" clearable multiple style="width: 100%" filterable>
                <el-option
                  v-for="box in gameBoxList"
                  :key="box.id"
                  :label="box.name"
                  :value="box.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="游戏" prop="gameIds">
              <el-select v-model="form.gameIds" placeholder="选择游戏（可多选）" clearable multiple style="width: 100%" filterable>
                <el-option
                  v-for="game in gameList"
                  :key="game.id"
                  :label="game.name"
                  :value="game.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="请输入作者" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="语言" prop="locale">
              <el-select v-model="form.locale" placeholder="选择语言" style="width: 100%" :disabled="true">
                <el-option
                  v-for="locale in formDefaultLocaleOnly"
                  :key="locale.value"
                  :label="locale.label"
                  :value="locale.value"
                />
              </el-select>
              <div style="margin-top: 4px; font-size: 12px; color: #909399;">
                <el-icon><info-filled /></el-icon> 新增文章只能选择网站的主语言（默认语言）
              </div>
              <div style="margin-top: 4px; font-size: 12px; color: #e6a23c;">
                <el-icon><warning-filled /></el-icon> 其他语言版本请通过"语言版本管理"功能添加
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="存储分类" prop="storageCategoryId">
              <el-select 
                v-model="form.storageCategoryId" 
                placeholder="请先选择存储分类" 
                clearable 
                style="width: 100%" 
                filterable
                @change="handleStorageCategoryChange"
              >
                <el-option
                  v-for="cat in storageCategoryList"
                  :key="cat.id"
                  :label="cat.name"
                  :value="cat.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存储配置" prop="storageConfigId">
              <el-select 
                v-model="form.storageConfigId" 
                placeholder="请选择存储配置" 
                clearable 
                style="width: 100%" 
                filterable
                :disabled="!form.storageCategoryId"
              >
                <el-option
                  v-for="storage in filteredStorageList"
                  :key="storage.id"
                  :label="storage.name"
                  :value="storage.id"
                >
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span>
                      <strong>{{ storage.name }}</strong>
                    </span>
                    <span style="color: #8492a6; font-size: 12px; margin-left: 12px;">
                      <el-tag size="small" :type="getStorageTypeTag(storage.storageType)">
                        {{ storage.storageType }}
                      </el-tag>
                      <span v-if="storage.storagePurpose" style="margin-left: 4px;">
                        | {{ getStoragePurposeLabel(storage.storagePurpose) }}
                      </span>
                    </span>
                  </div>
                  <div v-if="storage.description" style="font-size: 12px; color: #909399; margin-top: 2px;">
                    {{ storage.description }}
                  </div>
                </el-option>
                <template v-if="form.storageCategoryId && filteredStorageList.length === 0">
                  <el-option disabled value="">该分类下暂无可用的存储配置</el-option>
                </template>
              </el-select>
              <div v-if="!form.storageCategoryId" style="font-size: 12px; color: #909399; margin-top: 4px;">
                <el-icon><InfoFilled /></el-icon>
                请先选择存储分类
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="路径规则" prop="pathRule">
              <el-select v-model="form.pathRule" placeholder="选择路径规则" style="width: 100%">
                <el-option label="日期+标题 (YYYY-MM-DD-title)" value="date-title" />
                <el-option label="年月+标题 (YYYY-MM-title)" value="yearmonth-title" />
                <el-option label="时间戳 (timestamp-title)" value="timestamp-title" />
                <el-option label="仅标题 (title)" value="title-only" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预览路径">
              <el-input :value="previewPath" readonly placeholder="根据规则自动生成" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <!-- <el-alert
              title="模板变量使用说明"
              type="info"
              :closable="false"
              style="margin-bottom: 16px;"
            >
              <template #default>
                <div style="line-height: 1.6;">
                  在文章内容中可以使用以下模板变量，发布时会自动替换为实际数据：
                  <ul style="margin: 8px 0; padding-left: 20px;">
                    <li><code v-text="'{{siteConfig.name}}'"></code> - 网站名称</li>
                    <li><code v-text="'{{siteConfig.domain}}'"></code> - 网站域名</li>
                    <li><code v-text="'{{gameBox.name}}'"></code> 或 <code v-text="'{{gameBox1.name}}'"></code> - 第1个游戏盒子名称</li>
                    <li><code v-text="'{{gameBox2.name}}'"></code>, <code v-text="'{{gameBox3.name}}'"></code> - 第2、3个游戏盒子（依此类推）</li>
                    <li><code v-text="'{{game.name}}'"></code> 或 <code v-text="'{{game1.name}}'"></code> - 第1个游戏名称</li>
                    <li><code v-text="'{{game2.name}}'"></code>, <code v-text="'{{game3.name}}'"></code> - 第2、3个游戏（依此类推）</li>
                  </ul>
                  <span style="color: #909399; font-size: 12px;">提示：需要先选择对应的网站、游戏盒子或游戏才能使用相应的变量。支持多选，按选择顺序编号。</span>
                </div>
              </template>
            </el-alert> -->
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="文章内容" prop="content">
              <!-- 模板变量快速插入工具栏 -->
              <div style="margin-bottom: 8px; padding: 12px; background: #f5f7fa; border-radius: 4px; border: 1px solid #e4e7ed;">
                <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                  <span style="color: #606266; font-weight: 500; margin-right: 4px;">快速插入：</span>
                  
                  <!-- 原子工具按钮 -->
                  <el-button 
                    size="small" 
                    type="success" 
                    plain
                    @click="handleOpenAtomicTool"
                  >
                    <el-icon><Tools /></el-icon>
                    <span style="margin-left: 4px;">原子工具</span>
                  </el-button>
                  
                  <!-- 网站变量 -->
                  <el-dropdown v-if="form.siteId" trigger="click" @command="insertVariable">
                    <el-button size="small" type="primary" plain>
                      网站变量 <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="SITE.name">网站名称</el-dropdown-item>
                        <el-dropdown-item command="SITE.domain">网站域名</el-dropdown-item>
                        <el-dropdown-item command="SITE.code">网站代码</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  
                  <!-- 游戏盒子变量 -->
                  <el-dropdown v-if="form.gameBoxIds && form.gameBoxIds.length > 0" trigger="click" @command="insertVariable">
                    <el-button size="small" type="success" plain>
                      游戏盒子变量 <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <template v-for="(boxId, index) in form.gameBoxIds" :key="boxId">
                          <el-dropdown-item v-if="index > 0" divided disabled style="padding: 0; height: 0; min-height: 0;"></el-dropdown-item>
                          <el-dropdown-item disabled style="color: #909399; font-size: 12px; padding: 8px 16px;">
                            {{ getGameBoxName(boxId) }} (第{{ index + 1 }}个)
                          </el-dropdown-item>
                          <el-dropdown-item :command="`GAMEBOX-${index + 1}.name`">├─ 名称</el-dropdown-item>
                          <el-dropdown-item :command="`GAMEBOX-${index + 1}.downloadUrl`">├─ 下载链接</el-dropdown-item>
                          <el-dropdown-item :command="`GAMEBOX-${index + 1}.logoUrl`">├─ Logo URL</el-dropdown-item>
                          <el-dropdown-item :command="`GAMEBOX-${index + 1}.description`">└─ 描述</el-dropdown-item>
                        </template>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  
                  <!-- 游戏变量 -->
                  <el-dropdown v-if="form.gameIds && form.gameIds.length > 0" trigger="click" @command="insertVariable">
                    <el-button size="small" type="warning" plain>
                      游戏变量 <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <template v-for="(gameId, index) in form.gameIds" :key="gameId">
                          <el-dropdown-item v-if="index > 0" divided disabled style="padding: 0; height: 0; min-height: 0;"></el-dropdown-item>
                          <el-dropdown-item disabled style="color: #909399; font-size: 12px; padding: 8px 16px;">
                            {{ getGameName(gameId) }} (第{{ index + 1 }}个)
                          </el-dropdown-item>
                          <el-dropdown-item :command="`GAME-${index + 1}.name`">├─ 游戏名称</el-dropdown-item>
                          <el-dropdown-item :command="`GAME-${index + 1}.downloadUrl`">├─ 下载链接</el-dropdown-item>
                          <el-dropdown-item :command="`GAME-${index + 1}.androidUrl`">├─ 安卓下载</el-dropdown-item>
                          <el-dropdown-item :command="`GAME-${index + 1}.iosUrl`">├─ iOS下载</el-dropdown-item>
                          <el-dropdown-item :command="`GAME-${index + 1}.coverUrl`">├─ 封面图</el-dropdown-item>
                          <el-dropdown-item :command="`GAME-${index + 1}.description`">└─ 游戏描述</el-dropdown-item>
                        </template>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                  
                  <span v-if="!form.siteId && (!form.gameBoxIds || form.gameBoxIds.length === 0) && (!form.gameIds || form.gameIds.length === 0)" style="color: #909399; font-size: 13px;">
                    <el-icon><info-filled /></el-icon> 请先选择网站、游戏盒子或游戏以使用模板变量
                  </span>
                </div>
              </div>
              
              <MdEditor 
                ref="mdEditorRef"
                v-model="form.content" 
                language="zh-CN"
                :toolbars="toolbars"
                @onUploadImg="handleUploadImage"
                @on-html-changed="handleHtmlChanged"
                @on-get-catalog="handleGetCatalog"
                :preview-theme="previewTheme"
                :code-theme="codeTheme"
                :sanitize="sanitizeHtml"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="关键词" prop="keywords">
              <el-input v-model="form.keywords" placeholder="请输入关键词,多个用逗号分隔" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="文章摘要" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入文章摘要" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="封面图" prop="coverUrl">
              <el-input v-model="form.coverUrl" placeholder="请输入封面图URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="置顶" prop="isTop">
              <el-switch v-model="form.isTop" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="推荐" prop="isRecommend">
              <el-switch v-model="form.isRecommend" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
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
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="saveDraft" :loading="savingDraft">
            <el-icon v-if="!savingDraft"><Document /></el-icon>
            {{ savingDraft ? '保存中...' : '保存草稿' }}
          </el-button>
          <el-button type="success" @click="submitForm" :loading="publishing">
            <el-icon v-if="!publishing"><Upload /></el-icon>
            {{ publishing ? '发布中...' : '发布到存储' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 主页绑定对话框 -->
    <el-dialog :title="`主页绑定 - ${currentArticleForBinding.articleTitle || '文章'}`" v-model="bindDialogOpen" width="600px" append-to-body :close-on-click-modal="false">
      <!-- 提示信息 -->
      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      >
        <template #title>
          <div style="font-size: 14px; line-height: 1.6;">
            为该主文章绑定主页（游戏或游戏盒子）<br/>
            <span style="color: #909399; font-size: 12px;">注意：绑定关系独立于语言，所有语言版本共享同一个绑定</span>
          </div>
        </template>
      </el-alert>

      <!-- 文章信息卡片 -->
      <el-card shadow="never" style="margin-bottom: 20px; background-color: #f5f7fa;">
        <div style="display: flex; align-items: center; gap: 12px;">
          <el-icon :size="18" color="#409eff"><Document /></el-icon>
          <div style="flex: 1;">
            <div style="font-weight: 500; color: #303133;">{{ currentArticleForBinding.articleTitle }}</div>
            <div style="font-size: 12px; color: #909399; margin-top: 4px;">主文章 ID: {{ currentArticleForBinding.masterArticleId }}</div>
          </div>
        </div>
      </el-card>

      <!-- 绑定配置 -->
      <div style="border: 1px solid #ebeef5; border-radius: 4px; padding: 20px;">
        <el-form label-width="100px" size="default">
          <el-form-item label="绑定类型">
            <el-radio-group v-model="currentBindType" size="default" @change="currentBindTargetId = undefined">
              <el-radio-button label="none">
                <el-icon style="margin-right: 4px;"><CircleClose /></el-icon>
                不绑定
              </el-radio-button>
              <el-radio-button label="game">
                <el-icon style="margin-right: 4px;"><Trophy /></el-icon>
                游戏主页
              </el-radio-button>
              <el-radio-button label="gamebox">
                <el-icon style="margin-right: 4px;"><Box /></el-icon>
                游戏盒子主页
              </el-radio-button>
            </el-radio-group>
          </el-form-item>

          <!-- 选择游戏 -->
          <el-form-item v-if="currentBindType === 'game'" label="选择游戏">
            <el-select 
              v-model="currentBindTargetId" 
              placeholder="请选择要绑定的游戏" 
              clearable 
              filterable 
              style="width: 100%"
              :loading="loadingGames"
            >
              <el-option
                v-for="game in gameList"
                :key="game.id"
                :label="game.name"
                :value="game.id"
              />
            </el-select>
          </el-form-item>

          <!-- 选择游戏盒子 -->
          <el-form-item v-if="currentBindType === 'gamebox'" label="游戏盒子">
            <el-select 
              v-model="currentBindTargetId" 
              placeholder="请选择要绑定的游戏盒子" 
              clearable 
              filterable 
              style="width: 100%"
              :loading="loadingGameBoxes"
            >
              <el-option
                v-for="box in gameBoxList"
                :key="box.id"
                :label="box.name"
                :value="box.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmitMultiLanguageBinding" :loading="binding">保 存</el-button>
          <el-button @click="bindDialogOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 发布管理对话框 -->
    <el-dialog title="发布管理" v-model="languageVersionDialogOpen" width="1200px" append-to-body>
      <!-- 使用说明 -->
      <el-alert 
        title="使用说明" 
        type="info" 
        show-icon 
        :closable="false"
        style="margin-bottom: 20px;"
      >
        <template #default>
          <div style="font-size: 14px; line-height: 1.6;">
            <strong>文章管理规则：</strong><br>
            • 主文章列表中的「编辑」操作 → 编辑默认语言版本（{{ currentMasterArticle?.defaultLocale || 'zh-CN' }}）<br>
            • 默认语言版本的发布状态会影响主文章的发布状态<br>
            • 其他语言版本只能在此发布管理界面中编辑和发布<br>
            • 主文章发布状态：<el-tag size="small" type="info">无发布版本</el-tag> = 所有版本都是草稿，<el-tag size="small" type="success">有发布版本</el-tag> = 至少有一个版本已发布
          </div>
        </template>
      </el-alert>
      
      <!-- 主文章信息卡片 -->
      <el-card shadow="never" style="margin-bottom: 20px; background-color: #f8f9fa;">
        <div style="display: flex; align-items: center; gap: 12px;">
          <el-icon :size="20" color="#409eff"><Promotion /></el-icon>
          <div style="flex: 1;">
            <div style="font-weight: 500; color: #303133; margin-bottom: 4px;">
              文章：{{ currentMasterArticle.title }}
            </div>
            <div style="font-size: 13px; color: #909399;">
              共 {{ languageVersions.length }} 个语言版本，默认语言：{{ currentMasterArticle.defaultLocale }}
            </div>
          </div>
          <el-button type="success" icon="Promotion" @click="handleBatchPublish" :disabled="selectedLanguageVersions.length === 0">
            批量发布 ({{ selectedLanguageVersions.length }})
          </el-button>
          <el-button type="warning" icon="Refresh" @click="handleBatchCheckRemote" :disabled="selectedLanguageVersions.length === 0" :loading="batchCheckingRemote">
            批量检查远程 ({{ selectedLanguageVersions.length }})
          </el-button>
          <el-button type="primary" icon="Plus" @click="handleAddNewLanguageVersion">新增语言版本</el-button>
        </div>
      </el-card>

      <!-- 语言版本列表 -->
      <el-table :data="languageVersions" style="width: 100%" @selection-change="handleLanguageVersionSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="语言" prop="locale" width="120">
          <template #default="scope">
            <div style="display: flex; align-items: center; gap: 4px;">
              <el-tag type="info" size="small">{{ scope.row.locale }}</el-tag>
              <el-tag 
                v-if="scope.row.locale === currentMasterArticle?.defaultLocale" 
                type="warning" 
                size="small"
                effect="plain"
              >
                默认
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="标题" prop="title" :show-overflow-tooltip="true" min-width="200" />
        <el-table-column label="作者" prop="author" width="100" />
        <el-table-column label="状态" prop="status" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="info">草稿</el-tag>
            <el-tag v-else-if="scope.row.status === '1'" type="success">已发布</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="danger">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="远程文件" align="center" width="200">
          <template #header>
            <el-tooltip placement="top" effect="light">
              <template #content>
                <div style="max-width: 350px; line-height: 1.6;">
                  <p style="margin: 0 0 8px 0; font-weight: bold;">远程文件状态说明：</p>
                  <p style="margin: 0 0 4px 0;">• <strong style="color: #67C23A;">存在</strong>：远程文件存在且可访问</p>
                  <p style="margin: 0 0 4px 0;">• <strong style="color: #E6A23C;">不存在</strong>：远程文件不存在</p>
                  <p style="margin: 0 0 4px 0;">• <strong style="color: #F56C6C;">异常</strong>：检查时发生错误</p>
                  <p style="margin: 0 0 4px 0;">• <strong style="color: #909399;">未检查</strong>：尚未检查远程文件状态</p>
                  <p style="margin: 0 0 4px 0;">• <strong style="color: #409EFF;">无</strong>：未发布到远程（仅本地）</p>
                  <p style="margin: 6px 0 0 0; padding-top: 6px; border-top: 1px solid #EBEEF5;">
                    点击"检查"按钮可实时检测远程文件状态
                  </p>
                </div>
              </template>
              <span style="cursor: help;">
                远程文件
                <el-icon style="margin-left: 4px;"><QuestionFilled /></el-icon>
              </span>
            </el-tooltip>
          </template>
          <template #default="scope">
            <div style="display: flex; flex-direction: column; align-items: center; gap: 6px;">
              <!-- 远程文件状态 -->
              <div v-if="!scope.row.storageUrl">
                <el-tag type="info" size="small" effect="plain">
                  <el-icon style="margin-right: 4px;"><DocumentRemove /></el-icon>
                  无
                </el-tag>
              </div>
              <div v-else>
                <el-tag
                  v-if="scope.row.remoteFileStatus === '1'"
                  type="success"
                  size="small"
                >
                  <el-icon style="margin-right: 4px;"><CircleCheck /></el-icon>
                  存在
                </el-tag>
                <el-tag
                  v-else-if="scope.row.remoteFileStatus === '2'"
                  type="warning"
                  size="small"
                >
                  <el-icon style="margin-right: 4px;"><WarningFilled /></el-icon>
                  不存在
                </el-tag>
                <el-tag
                  v-else-if="scope.row.remoteFileStatus === '3'"
                  type="danger"
                  size="small"
                >
                  <el-icon style="margin-right: 4px;"><CircleClose /></el-icon>
                  异常
                </el-tag>
                <el-tag
                  v-else
                  type="info"
                  size="small"
                  effect="plain"
                >
                  <el-icon style="margin-right: 4px;"><QuestionFilled /></el-icon>
                  未检查
                </el-tag>
              </div>
              
              <!-- 远程文件信息 -->
              <div v-if="scope.row.storageUrl && (scope.row.remoteFileSize || scope.row.lastSyncCheckTime)" style="font-size: 12px; color: #909399; text-align: center;">
                <div v-if="scope.row.remoteFileSize && scope.row.remoteFileSize > 0">
                  大小: {{ formatFileSize(scope.row.remoteFileSize) }}
                </div>
                <div v-if="scope.row.lastSyncCheckTime" style="margin-top: 2px;">
                  检查: {{ formatTimeAgo(scope.row.lastSyncCheckTime) }}
                </div>
              </div>
              
              <!-- 操作按钮 -->
              <el-button
                v-if="scope.row.storageUrl"
                link
                type="primary"
                size="small"
                @click="handleCheckRemoteStatus(scope.row)"
                :loading="scope.row.checkingRemote"
                icon="Refresh"
              >
                检查
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="本地Content" align="center" width="150">
          <template #header>
            <el-tooltip placement="top" effect="light">
              <template #content>
                <div style="max-width: 350px; line-height: 1.6;">
                  <p style="margin: 0 0 8px 0; font-weight: bold;">本地Content说明：</p>
                  <p style="margin: 0 0 4px 0;">发布后，content字段会在设定天数后自动清理以节省数据库空间</p>
                  <p style="margin: 0 0 4px 0;">清理前会自动备份到远程存储</p>
                  <p style="margin: 6px 0 0 0; padding-top: 6px; border-top: 1px solid #EBEEF5;">
                    已清理的content可通过"恢复"功能从远程备份恢复
                  </p>
                </div>
              </template>
              <span style="cursor: help;">
                本地Content
                <el-icon style="margin-left: 4px;"><QuestionFilled /></el-icon>
              </span>
            </el-tooltip>
          </template>
          <template #default="scope">
            <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
              <!-- Content状态 -->
              <el-tag v-if="scope.row.contentClearedAt" type="warning" size="small">
                <el-icon style="margin-right: 4px;"><Delete /></el-icon>
                已清理
              </el-tag>
              <el-tag v-else type="success" size="small">
                <el-icon style="margin-right: 4px;"><Document /></el-icon>
                有效
              </el-tag>
              
              <!-- 清理信息 -->
              <div v-if="scope.row.contentClearedAt" style="font-size: 12px; color: #909399; text-align: center;">
                清理于: {{ parseTime(scope.row.contentClearedAt, '{y}-{m}-{d}') }}
              </div>
              <div v-else-if="scope.row.publishTime && scope.row.contentAutoClearDays" style="font-size: 12px; color: #909399; text-align: center;">
                {{ getAutoClearInfo(scope.row) }}
              </div>
              
              <!-- 恢复按钮 -->
              <el-button
                v-if="scope.row.contentClearedAt && scope.row.contentBackupKey"
                link
                type="primary"
                size="small"
                @click="handleRestoreContent(scope.row)"
                icon="RefreshRight"
              >
                恢复
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="160" />
        <el-table-column label="更新时间" prop="updateTime" width="160" />
        <el-table-column label="发布时间" prop="publishTime" width="160" />
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdateLanguageVersion(scope.row)" />
            </el-tooltip>
            <el-tooltip content="发布" placement="top">
              <el-button 
                link 
                type="success" 
                icon="Promotion"
                @click="handlePublishArticle(scope.row)"
                :disabled="scope.row.status === '1'" />
            </el-tooltip>
            <el-tooltip content="预览" placement="top">
              <el-button link type="success" icon="View" @click="scope.row.storageUrl && openUrl(scope.row.storageUrl)" :disabled="!scope.row.storageUrl" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button 
                link 
                type="danger" 
                icon="Delete"
                @click="handleDeleteSingleArticle(scope.row)"
                :disabled="scope.row.locale === currentMasterArticle?.defaultLocale" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="languageVersionDialogOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增语言版本对话框 -->
    <el-dialog title="新增语言版本" v-model="addLanguageVersionDialogOpen" width="600px" append-to-body>
      <el-form ref="addLanguageVersionFormRef" :model="addLanguageVersionForm" :rules="addLanguageVersionRules" label-width="100px">
        <el-form-item label="主文章" prop="masterArticleId">
          <el-input v-model="currentMasterArticle.title" disabled />
        </el-form-item>
        <el-form-item label="语言" prop="locale">
          <el-select v-model="addLanguageVersionForm.locale" placeholder="请选择语言" style="width: 100%;" @change="handleLocaleChange">
            <el-option
              v-for="locale in availableLocales"
              :key="locale.code"
              :label="`${locale.name} (${locale.code})`"
              :value="locale.code"
              :disabled="existingLocales.includes(locale.code)"
            >
              <span>{{ locale.name }} ({{ locale.code }})</span>
              <el-tag v-if="existingLocales.includes(locale.code)" type="info" size="small" style="margin-left: 8px;">已存在</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="addLanguageVersionForm.title" placeholder="请输入文章标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="Slug" prop="slug">
          <el-input v-model="addLanguageVersionForm.slug" placeholder="URL路径标识，留空自动生成" maxlength="200" />
        </el-form-item>
        <el-form-item label="自动翻译">
          <el-checkbox v-model="addLanguageVersionForm.autoTranslate">
            <span>创建后自动翻译文章内容和其他字段</span>
          </el-checkbox>
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            将自动翻译标题、描述、关键词、内容等字段到目标语言
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addLanguageVersionDialogOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitAddLanguageVersion" :loading="addingLanguageVersion">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导出对话框 -->
    <el-dialog title="数据导出" v-model="exportDialogOpen" width="500px" append-to-body>
      <el-alert
        title="导出说明"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      >
        <template #default>
          <p>• 导出选中文章的数据（包含分类关联）</p>
          <p>• 文章数据不包含翻译和网站关联</p>
          <p style="margin-top: 10px; color: #909399;">导入时将自动归属到当前选择的网站</p>
        </template>
      </el-alert>
      <el-form label-width="100px">
        <el-form-item label="导出格式">
          <el-radio-group v-model="exportFormat">
            <el-radio value="excel">Excel (.xlsx)</el-radio>
            <el-radio value="json">JSON (.json)</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选中数量">
          <el-tag type="info">{{ ids.length }} 个文章</el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmExport" :loading="exportLoading">确认导出</el-button>
          <el-button @click="exportDialogOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 全站导出对话框 -->
    <el-dialog title="全站数据导出" v-model="fullExportDialogOpen" width="550px" append-to-body>
      <el-alert
        title="全站导出说明"
        type="warning"
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      >
        <template #default>
          <p>• 全站导出将包含所有文章数据（默认配置 + 所有站点）</p>
          <p>• 包含所有网站关联关系和分类关联</p>
          <p>• 适用于系统迁移、备份等场景</p>
          <p style="margin-top: 10px; color: #F56C6C; font-weight: bold;">⚠️ 数据量可能较大，请耐心等待</p>
        </template>
      </el-alert>
      <el-form label-width="100px">
        <el-form-item label="导出格式">
          <el-radio-group v-model="fullExportFormat">
            <el-radio value="excel">Excel (.xlsx)</el-radio>
            <el-radio value="json">JSON (.json)</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmFullExport" :loading="fullExportLoading">确认导出</el-button>
          <el-button @click="fullExportDialogOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 系统导入对话框 -->
    <ImportDialog
      v-model="systemImportDialogOpen"
      :loading="systemImportLoading"
      :previewData="systemImportPreviewData"
      :translationsData="[]"
      @confirm="confirmSystemImport"
      @fileChange="handleSystemImportFileChange"
      @fileRemove="handleSystemImportFileRemove"
    >
      <template #importTips>
        <p>• 支持导入 Excel (.xlsx) 或 JSON (.json) 格式文件</p>
        <p>• 将文章数据导入到当前选择的创建者网站</p>
        <p>• 导入时会自动匹配分类</p>
        <p>• 必填字段：文章标题</p>
        <el-form :model="systemImportForm" label-width="100px" style="margin-top: 15px;">
          <el-form-item label="创建者网站" prop="siteId">
            <SiteSelect v-model="systemImportForm.siteId" :site-list="siteList" filterable width="100%" />
          </el-form-item>
        </el-form>
      </template>
      <template #previewColumns>
        <el-table-column prop="title" label="文章标题" width="200" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'info'" size="small">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
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
      :importData="fullImportArticles"
      v-model:importTranslations="fullImportTranslations"
      v-model:importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      :dataLabel="'文章'"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #importTips>
        <p style="margin: 0;">从其他系统导入完整的文章数据，包括：</p>
        <ul style="margin: 5px 0; padding-left: 20px;">
          <li>文章基础数据</li>
          <li>网站关联关系</li>
          <li>默认配置排除关系</li>
          <li>多语言翻译数据（如果有）</li>
        </ul>
      </template>
      <template #dataPreviewColumns>
        <el-table-column prop="文章标题" label="文章标题" width="200" show-overflow-tooltip />
        <el-table-column prop="作者" label="作者" width="100" />
        <el-table-column prop="状态" label="状态" width="80" />
      </template>
    </FullImportDialog>

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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选文章）</p>
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
          <strong>已选文章：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedArticlesForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="article in selectedArticlesForBatchExclude" 
            :key="article.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeArticleFromBatchExclude(article.id)"
            size="small"
          >
            {{ article.title }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各文章的当前排除/关联状态 -->
      <el-collapse v-if="articleExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各文章当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="articleExclusionDetails" size="small" stripe>
              <el-table-column label="文章标题" prop="articleTitle" width="150" show-overflow-tooltip />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选文章对该网站可见）</p>
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
          <strong>已选文章：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedArticlesForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="article in selectedArticlesForBatchRelation" 
            :key="article.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeArticleFromBatchRelation(article.id)"
            size="small"
          >
            {{ article.title }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各文章的当前关联/排除状态 -->
      <el-collapse v-if="articleRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各文章当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="articleRelationDetails" size="small" stripe>
              <el-table-column label="文章标题" prop="articleTitle" width="150" show-overflow-tooltip />
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

    <!-- 原子工具面板 -->
    <AtomicToolPanel 
      v-model="atomicToolDialogOpen"
      :selected-text="selectedText"
      @replace-text="handleReplaceText"
    />
  </div>
</template>

<script setup name="Article">
import { listArticle, getArticle, delArticle, addArticle, updateArticle, publishArticle, publishArticleForce, unpublishSingleArticle, saveMasterArticleDraft, updateMasterArticleDraft, batchPublishLanguageVersions, batchPublishLanguageVersionsForce, batchUnpublishLanguageVersions, batchUpdateArticleStatus, checkRemoteFileStatus, batchCheckRemoteFileStatus } from "@/api/gamebox/article"
import { delMasterArticle } from "@/api/gamebox/masterArticle"
import { listMasterArticle, getMasterArticleEditData, getMasterArticleHomepageBinding, bindMasterArticleToGame, bindMasterArticleToGameBox, unbindMasterArticleFromGame, unbindMasterArticleFromGameBox } from "@/api/gamebox/masterArticle"
import { listCategory, listVisibleCategory, listSections, listCategoriesBySection } from "@/api/gamebox/category"
import { listStorage } from "@/api/gamebox/storage"
import { listSite, getSite } from "@/api/gamebox/site"
import { listBox } from "@/api/gamebox/box"
import { listGame } from "@/api/gamebox/game"
import { autoTranslateText } from "@/api/gamebox/translation"
import CategoryTag from "@/components/CategoryTag/index.vue"
import SiteRelationManager from "@/components/SiteRelationManager/index.vue"
import ImportDialog from "@/components/ImportExportDialogs/ImportDialog.vue"
import FullImportDialog from "@/components/ImportExportDialogs/FullImportDialog.vue"
import AtomicToolPanel from "./components/AtomicToolPanel.vue"
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { handleTree, parseTime } from '@/utils/ruoyi'
import { checkPermi } from '@/utils/permission'
import { useSiteSelection, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"
import { getArticleSites, getBatchArticleSites, batchSaveArticleSiteRelations, updateArticleVisibility } from "@/api/gamebox/siteRelation"
import { User, Link, Star, Plus, Search, Edit, CircleCheck, CircleClose, MagicStick, Tools, DocumentAdd, Collection, Document, InfoFilled, WarningFilled, Promotion, Warning } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { nextTick, getCurrentInstance, h, computed } from 'vue'

const { proxy } = getCurrentInstance()

// 使用网站选择组合式函数
const { siteList, currentSiteId, loadSiteList, getSiteName } = useSiteSelection()
const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

const articleList = ref([])
const sectionList = ref([])
const articleCategoryList = ref([])
const articleCategoryTreeOptions = ref([])
const articleCategoryQueryOptions = ref([])
const categoryCache = new Map()
const viewMode = ref('creator') // 查看模式：creator-创建者, related-关联网站
const includeDefaultConfig = ref(false) // 是否包含默认配置
const includeGlobalInQuery = ref(false)
const storageCategoryList = ref([])
const storageList = ref([])
const gameBoxList = ref([])
const gameList = ref([])
const open = ref(false)
const loading = ref(true)
const publishing = ref(false)
const savingDraft = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const mdEditorRef = ref(null)
const previewTheme = ref('default')
const codeTheme = ref('atom')

// 导出相关
const exportDialogOpen = ref(false)
const exportFormat = ref('excel')
const exportLoading = ref(false)

// 全站导出相关
const fullExportDialogOpen = ref(false)
const fullExportFormat = ref('excel')
const fullExportLoading = ref(false)

// 系统导入相关（用于本系统导出的数据再导入）
const systemImportDialogOpen = ref(false)
const systemImportLoading = ref(false)
const systemImportPreviewData = ref([])
const systemImportFile = ref(null)
const systemImportForm = ref({
  siteId: undefined
})

// 全站导入相关
const fullImportDialogOpen = ref(false)
const fullImportLoading = ref(false)
const fullImportSites = ref([]) // 导入文件中的网站列表
const fullImportArticles = ref([]) // 导入的文章数据
const fullImportRelations = ref([]) // 导入的关联关系
const fullImportTranslations = ref([]) // 导入的翻译数据
const fullImportFile = ref(null)
const siteMapping = ref({}) // 网站ID映射 {源网站ID: 目标网站ID}
const hasDefaultConfig = ref(false) // 是否包含默认配置(siteid=0)
const createDefaultAsNewSite = ref(true) // 是否将默认配置导入为新网站的配置（并创建关联）

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const selectedArticlesForBatchExclude = ref([])
const articleExclusionDetails = ref([])
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const articleTableRef = ref(null)

// 批量排除：冲突检测（已勾选排除 & 同时存在 include 关联的网站集合）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  articleExclusionDetails.value.forEach(detail => {
    if (Array.isArray(detail.includedSiteIds)) {
      detail.includedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

// 批量关联管理相关
const batchRelationDialogOpen = ref(false)
const batchRelatedSiteIds = ref([])
const batchRelationLoading = ref(false)
const selectedArticlesForBatchRelation = ref([])
const articleRelationDetails = ref([])

// 批量关联：是否为默认配置模式（siteId=0）
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId, siteList.value))

// 批量关联：冲突检测（已勾选关联 & 同时存在 exclude 排除的网站集合）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  articleRelationDetails.value.forEach(detail => {
    if (Array.isArray(detail.excludedSiteIds)) {
      detail.excludedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

// 远程文件状态检查相关
const batchCheckingRemote = ref(false)

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentArticleIdForSites = ref(null)
const currentArticleTitleForSites = ref('')
const currentArticleCreatorSiteId = ref(0)

// 排除网站管理相关
const exclusionDialogOpen = ref(false)
const currentExclusionArticleId = ref(null)
const currentExclusionArticleTitle = ref('')
const selectedExcludedSiteIds = ref([])

// 主页绑定相关
const bindDialogOpen = ref(false)
const binding = ref(false)
const unbinding = ref(false)
const loadingGames = ref(false)
const loadingGameBoxes = ref(false)

// 语言版本管理相关
const languageVersionDialogOpen = ref(false)
const languageVersions = ref([])
const selectedLanguageVersions = ref([])  // 选中的语言版本
const currentMasterArticle = ref({})
const addLanguageVersionDialogOpen = ref(false)
const addLanguageVersionFormRef = ref(null)
const addLanguageVersionForm = ref({
  masterArticleId: undefined,
  locale: undefined,
  title: '',
  slug: ''
})
const addLanguageVersionRules = {
  locale: [{ required: true, message: '请选择语言', trigger: 'change' }],
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }]
}
const availableLocales = ref([]) // 可用的语言列表
const existingLocales = ref([]) // 已存在的语言列表
const addingLanguageVersion = ref(false)
const currentArticleForBinding = ref({
  masterArticleId: undefined,
  articleTitle: '',
  siteId: undefined
})
const currentBindType = ref('none') // 绑定类型：'none', 'game', 'gamebox'
const currentBindTargetId = ref(undefined) // 绑定目标ID

// 内容工具相关
const atomicToolDialogOpen = ref(false)
const selectedText = ref('')
const selectionStart = ref(0)
const selectionEnd = ref(0)

// markdown编辑器工具栏配置
const toolbars = [
  'bold',
  'underline',
  'italic',
  'strikeThrough',
  '-',
  'title',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  'mermaid',
  'katex',
  '-',
  'revoke',
  'next',
  '=',
  'pageFullscreen',
  'fullscreen',
  'preview',
  'htmlPreview',
  'catalog',
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: undefined,
    title: undefined,
    sectionId: undefined,
    categoryId: undefined,
    status: undefined
  },
  rules: {
    title: [{ required: true, message: "文章标题不能为空", trigger: "blur" }],
    slug: [{ required: true, message: "Slug不能为空", trigger: "blur" }],
    // 发布时才需要存储配置，草稿不需要
    // storageConfigId: [{ required: true, message: "请选择存储配置", trigger: "change" }],
    // pathRule: [{ required: true, message: "请选择路径规则", trigger: "change" }],
    content: [{ required: true, message: "文章内容不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 计算表单可用的语言列表（根据所选网站的supportedLocales）
const formAvailableLocales = computed(() => {
  if (!form.value.siteId) {
    // 未选择网站时，返回默认语言列表
    return [
      { label: '简体中文', value: 'zh-CN' },
      { label: '繁体中文', value: 'zh-TW' },
      { label: 'English', value: 'en-US' },
      { label: '日本語', value: 'ja-JP' }
    ]
  }
  
  if (isPersonalSite(form.value.siteId, siteList.value)) {
    // 全局文章，支持所有语言
    return [
      { label: '简体中文', value: 'zh-CN' },
      { label: '繁体中文', value: 'zh-TW' },
      { label: 'English', value: 'en-US' },
      { label: '日本語', value: 'ja-JP' }
    ]
  }
  
  // 查找所选网站
  const site = siteList.value.find(s => s.id === form.value.siteId)
  if (!site || !site.supportedLocales) {
    // 网站未配置supportedLocales，返回默认简体中文
    return [{ label: '简体中文', value: 'zh-CN' }]
  }
  
  try {
    const locales = typeof site.supportedLocales === 'string' 
      ? JSON.parse(site.supportedLocales) 
      : site.supportedLocales
      
    if (!Array.isArray(locales) || locales.length === 0) {
      return [{ label: '简体中文', value: 'zh-CN' }]
    }
    
    // 语言标签映射
    const localeLabels = {
      'zh-CN': '简体中文',
      'zh-TW': '繁体中文',
      'en-US': 'English',
      'ja-JP': '日本語'
    }
    
    return locales.map(locale => ({
      label: localeLabels[locale] || locale,
      value: locale
    }))
  } catch (e) {
    console.error('解析网站支持的语言失败:', e)
    return [{ label: '简体中文', value: 'zh-CN' }]
  }
})

// 计算表单只能选择默认语言（用于新增文章时限制只能选择主语言）
const formDefaultLocaleOnly = computed(() => {
  // 查找所选网站
  const site = siteList.value.find(s => s.id === form.value.siteId)
  const defaultLocale = site?.defaultLocale || 'zh-CN'
  
  // 语言标签映射
  const localeLabels = {
    'zh-CN': '简体中文',
    'zh-TW': '繁体中文',
    'en-US': 'English',
    'ja-JP': '日本語'
  }
  
  return [{
    label: localeLabels[defaultLocale] || defaultLocale,
    value: defaultLocale
  }]
})

/** 网站切换事件 */
function handleSiteChange() {
  // 切换网站时重置“含默认配置”选项
  if (!queryParams.value.siteId || isPersonalSite(queryParams.value.siteId, siteList.value)) {
    includeDefaultConfig.value = false
  }
  // 清空板块和分类筛选
  queryParams.value.sectionId = undefined
  queryParams.value.categoryId = undefined
  queryParams.value.pageNum = 1
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  // 加载板块列表
  loadSections(queryParams.value.siteId)
  // 加载分类列表
  loadArticleCategoriesForQuery(queryParams.value.siteId)
  getList()
}

/** 表单中的网站切换事件 */
function handleFormSiteChange() {
  // 检查当前选择的语言是否在新网站的支持语言列表中
  const availableLocales = formAvailableLocales.value
  const currentLocale = form.value.locale
  
  if (currentLocale && !availableLocales.find(l => l.value === currentLocale)) {
    // 当前语言不在支持列表中，重置为第一个可用语言
    if (availableLocales.length > 0) {
      form.value.locale = availableLocales[0].value
      proxy.$modal.msgWarning(`当前语言不在该网站支持的语言列表中，已自动调整为 ${availableLocales[0].label}`)
    } else {
      form.value.locale = 'zh-CN'
    }
  } else if (!currentLocale && availableLocales.length > 0) {
    // 如果未选择语言，设置为第一个可用语言
    form.value.locale = availableLocales[0].value
  }
  
  // 重新加载分类列表
  if (form.value.siteId !== undefined) {
    loadArticleCategoriesForDialog(form.value.siteId)
  }
}

/** 查看模式切换事件 */
function handleViewModeChange() {
  queryParams.value.siteId = resolveSiteIdByViewMode(viewMode.value, siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  // 重置“含默认配置”选项
  includeDefaultConfig.value = false
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  // 切换模式时重新查询
  queryParams.value.pageNum = 1
  loadArticleCategoriesForQuery(queryParams.value.siteId)
  getList()
}

// 计算预览路径
const previewPath = computed(() => {
  if (!form.value.slug || !form.value.pathRule) return ''
  
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const timestamp = now.getTime()
  const slug = form.value.slug
  
  let path = ''
  switch (form.value.pathRule) {
    case 'date-title':
      path = `${year}-${month}-${day}-${slug}`
      break
    case 'yearmonth-title':
      path = `${year}-${month}-${slug}`
      break
    case 'timestamp-title':
      path = `${timestamp}-${slug}`
      break
    case 'title-only':
      path = slug
      break
    default:
      path = slug
  }
  
  return `${path}/README.md`
})

// 为多语言版本生成存储key
function generateStorageKeyForLocale(slug, pathRule, locale) {
  if (!slug || !pathRule) return ''
  
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const timestamp = now.getTime()
  
  let path = ''
  switch (pathRule) {
    case 'date-title':
      path = `${year}-${month}-${day}-${slug}`
      break
    case 'yearmonth-title':
      path = `${year}-${month}-${slug}`
      break
    case 'timestamp-title':
      path = `${timestamp}-${slug}`
      break
    case 'title-only':
      path = slug
      break
    default:
      path = slug
  }
  
  // 对于非默认语言，在路径中添加语言标识
  // 例如: 2024-01-15-wangzherongyao/README.zh-TW.md
  if (locale && locale !== 'zh-CN') {
    return `${path}/README.${locale}.md`
  }
  
  return `${path}/README.md`
}

// 根据选择的分类过滤存储配置
const filteredStorageList = computed(() => {
  if (!form.value.storageCategoryId) {
    return []
  }
  
  const filtered = storageList.value.filter(storage => {
    // 必须是选中的分类
    const matchCategory = storage.categoryId === form.value.storageCategoryId
    // 必须支持文章存储
    const supportArticle = storage.storagePurpose === 'article' || storage.storagePurpose === 'mixed'
    
    // console.log('过滤存储配置:', storage.name, '分类匹配:', matchCategory, '(', storage.categoryId, 'vs', form.value.storageCategoryId, ')', '用途支持:', supportArticle, '(', storage.storagePurpose, ')')
    
    return matchCategory && supportArticle
  })
  
  // console.log('过滤后的存储配置:', filtered.length, '个')
  return filtered
})

// 获取存储类型标签颜色
function getStorageTypeTag(type) {
  const tagMap = {
    'github': 'info',
    'r2': 'warning',
    'minio': 'success',
    'oss': 'primary',
    'cos': 'primary',
    's3': 'warning'
  }
  return tagMap[type] || 'info'
}

// 获取存储用途标签
function getStoragePurposeLabel(purpose) {
  const labelMap = {
    'article': '文章',
    'resource': '资源',
    'mixed': '混合'
  }
  return labelMap[purpose] || purpose
}

function getList() {
  loading.value = true
  
  // 构建查询参数
  const params = { 
    ...queryParams.value,
    queryMode: viewMode.value,
    includeDefault: includeDefaultConfig.value
  }
  
  // 使用主文章查询接口（列表页应该显示主文章，而不是所有语言版本的文章）
  if (params.siteId !== undefined) {
    listMasterArticle(params).then(response => {
      articleList.value = response.rows
      total.value = response.total
      preloadCategories(response.rows)
      // 加载绑定信息
      loadBindingInfo(response.rows)
      loading.value = false
    }).catch(error => {
      console.error('查询主文章失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  } else {
    // 未选择网站：默认显示默认配置
    listMasterArticle({ ...params, siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      articleList.value = response.rows
      total.value = response.total
      preloadCategories(response.rows)
      // 加载绑定信息
      loadBindingInfo(response.rows)
      loading.value = false
    }).catch(error => {
      console.error('查询主文章失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  }
}

// 批量加载绑定信息
function loadBindingInfo(articles) {
  if (!articles || articles.length === 0) return
  
  // 为每篇文章查询绑定信息
  articles.forEach((article, index) => {
    getMasterArticleHomepageBinding(article.id).then(response => {
      const binding = response.data
      // 使用 Object.assign 或展开运算符来触发响应式更新
      if (binding && binding.gameId) {
        articleList.value[index] = { ...article, bindType: 'game', bindTargetId: binding.gameId }
      } else if (binding && binding.gameBoxId) {
        articleList.value[index] = { ...article, bindType: 'gamebox', bindTargetId: binding.gameBoxId }
      } else {
        articleList.value[index] = { ...article, bindType: null, bindTargetId: null }
      }
    }).catch(() => {
      // 查询失败时设置为null
      articleList.value[index] = { ...article, bindType: null, bindTargetId: null }
    })
  })
}

// 预加载分类信息到缓存
function preloadCategories(articles) {
  const categoryIds = [...new Set(articles.map(a => a.categoryId).filter(Boolean))]
  categoryIds.forEach(id => {
    const article = articles.find(a => a.categoryId === id)
    if (article && article.categoryName) {
      categoryCache.set(id, {
        id: article.categoryId,
        name: article.categoryName,
        icon: article.categoryIcon
      })
    }
  })
}

// 从缓存获取分类信息
function getCategoryFromCache(categoryId) {
  return categoryCache.get(categoryId)
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  // 根据所选网站获取默认语言
  const currentSite = siteList.value.find(s => s.id === (queryParams.value.siteId || 0))
  const defaultLocale = currentSite?.defaultLocale || 'zh-CN'
  
  form.value = {
    id: undefined,
    title: undefined,
    slug: undefined,
    locale: defaultLocale, // 使用网站的默认语言
    categoryId: undefined,
    siteId: queryParams.value.siteId || 0, // 默认使用当前查询的网站
    storageCategoryId: undefined,
    gameBoxIds: [],
    gameIds: [],
    author: undefined,
    coverUrl: undefined,
    description: undefined,
    content: undefined,
    keywords: undefined,
    storageConfigId: undefined,
    pathRule: 'date-title',
    isTop: "0",
    isRecommend: "0",
    sortOrder: 0,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("articleRef")
}

// 加载存储配置列表
function loadStorageConfigs() {
  // 查询支持文章存储的配置（article 或 mixed）
  listStorage({ 
    status: '1',  // 数据库中：1=启用 0=禁用
    pageNum: 1, 
    pageSize: 1000 
  }).then(response => {
    storageList.value = response.rows || []
  }).catch(error => {
    console.error('加载存储配置失败:', error)
    storageList.value = []
    proxy.$modal.msgError('加载存储配置失败')
  })
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

// 加载文章分类列表（编辑对话框用）
// 使用可见分类接口，只展示对该网站可见的分类（默认配置未排除 + 跨站共享可见 + 自有启用）
function loadArticleCategoriesForDialog() {
  const siteId = form.value.siteId || personalSiteId.value
  
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点或选择了全局，只查询全局分类
    return listVisibleCategory({ 
      categoryType: 'article', 
      siteId: personalSiteId.value,
      pageNum: 1, 
      pageSize: 1000 
    }).then(response => {
      let categories = response.rows || []
      categories = categories.map(cat => ({
        ...cat,
        name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [默认配置]`,
        disabled: cat.isSection === '1' || !cat.parentId || Number(cat.parentId) === 0
      }))
      articleCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
      return Promise.resolve()
    }).catch(error => {
      console.error('加载文章分类失败:', error)
      articleCategoryTreeOptions.value = []
      return Promise.resolve()
    })
  }
  
  // 选择了具体站点：使用可见分类接口，只返回可见的分类
  return listVisibleCategory({ 
    categoryType: 'article', 
    siteId: siteId,
    pageNum: 1, 
    pageSize: 1000 
  }).then(response => {
    let categories = response.rows || []
    
    // 为分类添加站点标识和图标，并标注来源
    const siteName = getSiteName(siteId)
    categories = categories.map(cat => {
      let source = ''
      if (isPersonalSite(cat.siteId, siteList.value)) {
        source = '[默认配置]'
      } else if (cat.siteId === siteId) {
        source = `[${siteName}]`
      } else {
        source = '[共享]'
      }
      
      return {
        ...cat,
        name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} ${source}`,
        disabled: cat.isSection === '1' || !cat.parentId || Number(cat.parentId) === 0
      }
    })
    
    articleCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
    return Promise.resolve()
  }).catch(error => {
    console.error('加载文章分类失败:', error)
    articleCategoryTreeOptions.value = []
    return Promise.resolve()
  })
}

// 加载板块列表
function loadSections(siteId) {
  listSections({ siteId: siteId || 0 }).then(response => {
    sectionList.value = response.data || []
  })
}

// 板块切换处理
function handleSectionChange(sectionId) {
  // 清空分类筛选
  queryParams.value.categoryId = undefined
  // 如果选择了板块，重新加载该板块下的分类
  if (sectionId) {
    listCategoriesBySection(sectionId).then(response => {
      const categories = response.data || []
      categories.forEach(cat => {
        cat.displayName = cat.name
      })
      articleCategoryList.value = categories
      articleCategoryQueryOptions.value = handleTree(categories, "id", "parentId")
    })
  } else {
    // 没有选择板块，恢复加载所有分类
    loadArticleCategoriesForQuery(queryParams.value.siteId)
  }
  handleQuery()
}

// 加载文章分类列表（查询表单使用）
// 始终使用关联模式，显示所有分类并标记不可用的
function loadArticleCategoriesForQuery(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点，只查询默认配置分类（创建者模式），排除板块
    listCategory({ 
      categoryType: 'article', 
      siteId: personalSiteId.value, 
      status: '1',
      isSection: '0',
      queryMode: 'creator',
      pageNum: 1, 
      pageSize: 1000 
    }).then(response => {
      const categories = response.rows || []
      // 为查询选项添加displayName字段
      categories.forEach(cat => {
        cat.displayName = cat.name
      })
      articleCategoryList.value = categories
      articleCategoryQueryOptions.value = handleTree(categories, "id", "parentId")
    })
    return
  }
  
  // 选择了具体站点：始终使用关联模式（related）查询所有分类，排除板块
  listCategory({ 
    categoryType: 'article',
    siteId: siteId,
    status: '1',
    isSection: '0',
    queryMode: 'related',  // 始终使用关联模式
    pageNum: 1,
    pageSize: 1000
  }).then(response => {
    const categories = response.rows || []
    // 为查询选项添加displayName字段（不包含来源和状态标签）
    categories.forEach(cat => {
      cat.displayName = cat.name
    })
    articleCategoryList.value = categories
    articleCategoryQueryOptions.value = handleTree(categories, "id", "parentId")
  })
}

// 加载存储分类列表（编辑对话框使用）
// 使用可见分类接口，只展示对该网站可见的分类（默认配置未排除 + 跨站共享可见 + 自有启用）
function loadStorageCategories(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点或选择了全局，只查询全局分类
    return listVisibleCategory({ 
      categoryType: 'storage', 
      siteId: personalSiteId.value,
      pageNum: 1, 
      pageSize: 1000 
    }).then(response => {
      let categories = response.rows || []
      categories = categories.map(cat => ({
        ...cat,
        name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [默认配置]`
      }))
      storageCategoryList.value = categories
      return Promise.resolve()
    }).catch(error => {
      console.error('加载存储分类失败:', error)
      storageCategoryList.value = []
      return Promise.resolve()
    })
  }
  
  // 选择了具体站点：使用可见分类接口，只返回可见的分类
  return listVisibleCategory({ 
    categoryType: 'storage', 
    siteId: siteId,
    pageNum: 1, 
    pageSize: 1000 
  }).then(response => {
    let categories = response.rows || []
    
    // 为分类添加站点标识和图标，并标注来源
    const siteName = getSiteName(siteId)
    categories = categories.map(cat => {
      let source = ''
      if (isPersonalSite(cat.siteId, siteList.value)) {
        source = '[默认配置]'
      } else if (cat.siteId === siteId) {
        source = `[${siteName}]`
      } else {
        source = '[共享]'
      }
      
      return {
        ...cat,
        name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} ${source}`
      }
    })
    
    storageCategoryList.value = categories
    return Promise.resolve()
  }).catch(error => {
    console.error('加载存储分类失败:', error)
    storageCategoryList.value = []
    return Promise.resolve()
  })
}

// 加载游戏盒子列表
function loadGameBoxes() {
  listBox({ status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
    gameBoxList.value = response.rows || []
  }).catch(error => {
    console.error('加载游戏盒子列表失败:', error)
    gameBoxList.value = []
  })
}

// 加载游戏列表
function loadGames() {
  listGame({ status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
    gameList.value = response.rows || []
  }).catch(error => {
    console.error('加载游戏列表失败:', error)
    gameList.value = []
  })
}

/** 存储分类改变时清空存储配置 */
function handleStorageCategoryChange() {
  form.value.storageConfigId = undefined
}

/** 插入模板变量到编辑器 */
function insertVariable(variablePath) {
  const variable = `{{${variablePath}}}`
  if (mdEditorRef.value) {
    // 获取当前编辑器内容和光标位置
    const content = form.value.content || ''
    // 简单地在内容末尾添加
    form.value.content = content + (content ? '\n' : '') + variable
  }
}

/** 插入内容回调 */
function handleInsertContent(content) {
  // 如果有选中的文本，覆盖选中的内容
  if (selectedText.value && selectionStart.value !== undefined && selectionEnd.value !== undefined) {
    const before = form.value.content.substring(0, selectionStart.value)
    const after = form.value.content.substring(selectionEnd.value)
    form.value.content = before + content + after
    proxy.$modal.msgSuccess('内容已替换选中文本')
  } else if (form.value.content) {
    // 如果没有选中文本但已有内容，追加到末尾
    form.value.content += '\n\n' + content
    proxy.$modal.msgSuccess('内容已插入到编辑器')
  } else {
    // 如果没有内容，直接设置
    form.value.content = content
    proxy.$modal.msgSuccess('内容已插入到编辑器')
  }
  
  // 清空选中状态
  selectedText.value = ''
  selectionStart.value = undefined
  selectionEnd.value = undefined
}

/** 替换内容回调 */
function handleReplaceContent(content) {
  if (!content) {
    return
  }
  
  // 用新内容替换选中的文本
  if (selectionStart.value !== undefined && selectionEnd.value !== undefined) {
    const before = form.value.content.substring(0, selectionStart.value)
    const after = form.value.content.substring(selectionEnd.value)
    form.value.content = before + content + after
  } else {
    // 如果没有选中范围，直接替换所有内容
    form.value.content = content
  }
  
  proxy.$modal.msgSuccess('内容已替换')
}

/** 打开原子工具面板 */
function handleOpenAtomicTool() {
  if (!open.value) {
    proxy.$modal.msgWarning('请先打开文章编辑对话框')
    return
  }
  
  // 尝试获取选中文本
  const selection = window.getSelection()
  let selected = selection ? selection.toString() : ''
  
  // 如果没有选中内容，尝试从textarea获取
  if (!selected || selected.trim() === '') {
    const editor = mdEditorRef.value
    if (editor) {
      // 尝试多种方式获取textarea
      let textarea = null
      
      if (editor.textareaRef) {
        textarea = editor.textareaRef
      } else if (editor.$el) {
        textarea = editor.$el.querySelector('.md-editor-input-wrapper textarea') ||
                   editor.$el.querySelector('textarea')
      }
      
      if (textarea) {
        const start = textarea.selectionStart
        const end = textarea.selectionEnd
        if (start !== end) {
          selected = form.value.content.substring(start, end)
          selectionStart.value = start
          selectionEnd.value = end
        }
      }
    }
  }
  
  // 如果通过window.getSelection获取到了文本，需要找到它在content中的位置
  if (selected && selected.trim() !== '' && (selectionStart.value === 0 && selectionEnd.value === 0)) {
    const index = form.value.content.indexOf(selected)
    if (index !== -1) {
      selectionStart.value = index
      selectionEnd.value = index + selected.length
    } else {
      // 如果找不到精确位置，设置为末尾
      selectionStart.value = form.value.content.length
      selectionEnd.value = form.value.content.length
    }
  }
  
  // 保存选中文本（可能为空）
  selectedText.value = selected
  
  // 打开原子工具面板
  atomicToolDialogOpen.value = true
}

/** 替换文本回调 */
function handleReplaceText(newText) {
  if (!newText) {
    proxy.$modal.msgWarning('没有生成的文本')
    return
  }
  
  // 用新内容替换选中的文本
  if (selectionStart.value !== undefined && selectionEnd.value !== undefined) {
    const before = form.value.content.substring(0, selectionStart.value)
    const after = form.value.content.substring(selectionEnd.value)
    form.value.content = before + newText + after
    proxy.$modal.msgSuccess('文本已替换')
  } else {
    // 如果没有选中范围，在末尾追加
    form.value.content = form.value.content + '\n' + newText
    proxy.$modal.msgSuccess('文本已追加到末尾')
  }
}

/** 获取游戏盒子名称 */
function getGameBoxName(boxId) {
  const box = gameBoxList.value.find(b => b.id === boxId)
  return box ? box.name : `盒子#${boxId}`
}

/** 获取游戏名称 */
function getGameName(gameId) {
  const game = gameList.value.find(g => g.id === gameId)
  return game ? game.name : `游戏#${gameId}`
}

/** markdown 编辑器 HTML 变化时的回调（用于实时预览替换） */
function handleHtmlChanged(html) {
  // 这里可以实现实时替换预览，但 md-editor-v3 的预览是内置的
  // 我们需要自定义渲染器来实现模板变量替换
}

/** 获取目录回调 */
function handleGetCatalog(list) {
  // console.log('目录:', list)
}

/** 在预览时替换模板变量 */
const sanitizeHtml = (html) => {
  // 先将被 md-editor 错误解析为链接的模板变量还原
  // 例如: {{<a href="http://GAMEBOX-1.name">GAMEBOX-1.name</a>}} -> {{GAMEBOX-1.name}}
  html = html.replace(/\{\{<a[^>]*>([a-zA-Z0-9_.-]+)<\/a>\}\}/g, '{{$1}}')
  
  // 构建变量上下文
  const context = buildTemplateContext()
  
  // 替换所有 {{XXX.yyy}} 或 {{XXX-n.yyy}} 格式的变量
  return html.replace(/\{\{([a-zA-Z0-9_.-]+)\}\}/g, (match, path) => {
    const value = resolveVariablePath(path, context)
    // 只有基本类型（字符串、数字、布尔值）才算有效值，对象类型视为未解析
    if (value !== undefined && value !== null && typeof value !== 'object') {
      return `<span class="template-var-replaced" style="background: #e6f7ff; padding: 2px 6px; border-radius: 3px; color: #1890ff;">${value}</span>`
    }
    // 未找到变量或者是对象类型，保留原样但标记为未替换
    return `<span class="template-var-unresolved" style="background: #fff1f0; padding: 2px 6px; border-radius: 3px; color: #ff4d4f;">${match}</span>`
  })
}

/** 构建模板变量上下文 */
function buildTemplateContext() {
  const context = {}
  
  // 添加网站配置
  if (form.value.siteId) {
    const site = siteList.value.find(s => s.id === form.value.siteId)
    if (site) {
      context.SITE = {
        name: site.name,
        domain: site.domain,
        code: site.code
      }
    }
  }
  
  // 添加游戏盒子数据
  if (form.value.gameBoxIds && form.value.gameBoxIds.length > 0) {
    form.value.gameBoxIds.forEach((boxId, index) => {
      const box = gameBoxList.value.find(b => b.id === boxId)
      if (box) {
        context[`GAMEBOX-${index + 1}`] = {
          name: box.name,
          logoUrl: box.logoUrl,
          downloadUrl: box.downloadUrl,
          description: box.description
        }
      }
    })
  }
  
  // 添加游戏数据
  if (form.value.gameIds && form.value.gameIds.length > 0) {
    form.value.gameIds.forEach((gameId, index) => {
      const game = gameList.value.find(g => g.id === gameId)
      if (game) {
        context[`GAME-${index + 1}`] = {
          name: game.name,
          downloadUrl: game.downloadUrl,
          androidUrl: game.androidUrl,
          iosUrl: game.iosUrl,
          coverUrl: game.coverUrl,
          description: game.description
        }
      }
    })
  }
  
  return context
}

/** 解析变量路径（仅用于预览显示）*/
function resolveVariablePath(path, context) {
  // 支持两种格式:
  // 1. SITE.name
  // 2. GAME-1.name 或 GAMEBOX-1.name
  
  // 普通点号分隔的路径
  const parts = path.split('.')
  let current = context
  
  for (const part of parts) {
    if (current && typeof current === 'object' && part in current) {
      current = current[part]
    } else {
      return undefined
    }
  }
  
  return current
}

function handleAdd() {
  reset()
  // 先加载基础数据
  loadArticleCategoriesForDialog()
  loadStorageConfigs()
  loadGameBoxes()
  loadGames()
  // 根据默认的siteId加载存储分类（通过watch监听器会自动处理）
  loadStorageCategories(form.value.siteId || 0)
  
  // 确保设置正确的默认语言
  nextTick(() => {
    const currentSite = siteList.value.find(s => s.id === form.value.siteId)
    const defaultLocale = currentSite?.defaultLocale || 'zh-CN'
    form.value.locale = defaultLocale
  })
  
  open.value = true
  title.value = "添加文章"
}

/** 编辑特定语言版本的文章（从发布管理对话框中调用） */
function handleUpdateLanguageVersion(languageVersion) {
  reset()
  loadStorageConfigs()
  loadGameBoxes()
  loadGames()
  
  const articleId = languageVersion.id
  const masterArticleId = languageVersion.masterArticleId
  
  if (!articleId || !masterArticleId) {
    proxy.$modal.msgError('无效的文章信息')
    return
  }
  
  // 获取特定语言版本的编辑数据
  getMasterArticleEditData(masterArticleId).then(response => {
    const { masterArticle, gameIds, gameBoxIds } = response.data
    
    // 获取这个特定语言版本的文章数据
    getArticle(articleId).then(articleResponse => {
      const article = articleResponse.data
      
      // 合并主文章和语言文章数据到表单
      form.value = {
        ...article,
        // 从主文章获取的字段
        categoryId: masterArticle.categoryId,
        siteId: masterArticle.siteId,
        isTop: masterArticle.isTop,
        isRecommend: masterArticle.isRecommend,
        sortOrder: masterArticle.sortOrder,
        remark: masterArticle.remark,
        // 用于提交时标识
        masterArticleId: masterArticleId,
        articleId: articleId,
        // 从主文章关联表获取的游戏和游戏盒子ID
        gameIds: gameIds || [],
        gameBoxIds: gameBoxIds || []
      }
      
      // 先加载分类列表，再设置分类值
      loadArticleCategoriesForDialog().then(() => {
        // 根据文章的siteId加载存储分类
        loadStorageCategories(masterArticle.siteId || 0).then(() => {
          if (article.storageConfigId) {
            const storage = storageList.value.find(s => s.id === article.storageConfigId)
            if (storage) {
              form.value.storageCategoryId = storage.categoryId
            }
          }
        })
        
        // 等待分类加载完成后再设置categoryId
        nextTick(() => {
          if (masterArticle.categoryId) {
            form.value.categoryId = masterArticle.categoryId
          }
        })
      })
      
      // 如果内容是从对象存储加载的，顶部橙色提示
      if (form.value.contentLoadedFromStorage) {
        // 动态引入 ElMessage，避免全局污染
        import('element-plus').then(({ ElMessage }) => {
          ElMessage.closeAll()
          ElMessage({
            dangerouslyUseHTMLString: true,
            message: `
              <div style="white-space:pre-line">
                <b>该文章内容已从对象存储加载（数据库已清理）。</b><br>
                <span style="color:#e6a23c;">⚠️ 重要提示：</span><br>
                - 模板变量已被替换为实际值，无法再修改旧的变量关联（网站、游戏盒子、游戏）。<br>
                - 您可以编辑文本内容，但再次发布时，旧的模板变量不会被重新替换。
              </div>
            `,
            type: 'warning',
            duration: 8000,
            showClose: true,
            offset: 60
          })
        })
      }
      
      // 关闭发布管理对话框
      languageVersionDialogOpen.value = false
      open.value = true
      title.value = "编辑文章 - " + languageVersion.locale
    }).catch(error => {
      proxy.$modal.msgError('加载语言版本失败：' + (error.msg || error.message || '文章不存在'))
    })
  }).catch(error => {
    proxy.$modal.msgError('加载文章信息失败：' + (error.msg || error.message || '文章不存在'))
  })
}

/** 编辑主文章（跳转到默认语言版本） */
function handleUpdate(row) {
  reset()
  loadStorageConfigs()
  loadGameBoxes()
  loadGames()
  
  let masterArticleId
  
  // 如果没有传入row，使用选中的项
  if (!row) {
    masterArticleId = ids.value[0] // 选中的项已经是主文章ID
  } else {
    // 如果传入的是主文章（列表页行数据），直接使用
    // 列表页中显示的是主文章数据，row.id 是主文章ID
    masterArticleId = row.id
  }
  
  if (!masterArticleId) {
    proxy.$modal.msgError('请选择要编辑的文章')
    return
  }
  
  // 使用主文章ID获取编辑数据（包含默认语言版本内容）
  getMasterArticleEditData(masterArticleId).then(response => {
    const { masterArticle, articleId, gameIds, gameBoxIds } = response.data
    
    // 检查是否找到默认语言版本
    if (!articleId) {
      proxy.$modal.msgError(`未找到该文章的默认语言版本（${masterArticle.defaultLocale}），请先创建`)
      return
    }
    
    // 获取特定语言版本的文章数据（包括从远程存储加载content的逻辑）
    getArticle(articleId).then(articleResponse => {
      const article = articleResponse.data
      
      // 合并主文章和语言文章数据到表单
      form.value = {
        ...article,
        // 从主文章获取的字段
        categoryId: masterArticle.categoryId,
        siteId: masterArticle.siteId,
        isTop: masterArticle.isTop,
        isRecommend: masterArticle.isRecommend,
        sortOrder: masterArticle.sortOrder,
        // status 由后端根据所有语言版本统计，前端不使用
        remark: masterArticle.remark,
        // 用于提交时标识
        masterArticleId: masterArticleId,
        articleId: articleId,
        isDefaultLanguage: true, // 标识这是默认语言版本
        // 从主文章关联表获取的游戏和游戏盒子ID
        gameIds: gameIds || [],
        gameBoxIds: gameBoxIds || []
      }
      
      // 设置默认值
      if (!form.value.locale) form.value.locale = masterArticle.defaultLocale || 'zh-CN'
      if (!form.value.pathRule) form.value.pathRule = 'date-title'
      
      // 根据文章的siteId加载存储分类（先设置storageCategoryId，以便后续过滤存储配置）
      loadStorageCategories(masterArticle.siteId || 0).then(() => {
        // 存储分类加载完成后，设置storageCategoryId
        if (article.storageConfigId) {
          // 如果有存储配置ID，需要找到对应的分类ID
          const storage = storageList.value.find(s => s.id === article.storageConfigId)
          if (storage) {
            form.value.storageCategoryId = storage.categoryId
          }
        }
      })
      
      // 等待分类加载完成后再设置categoryId（因为需要树结构）
      loadArticleCategoriesForDialog().then(() => {
        nextTick(() => {
          form.value.categoryId = masterArticle.categoryId
        })
      })
      
      // 如果内容是从对象存储加载的，顶部橙色提示
      if (form.value.contentLoadedFromStorage) {
        // 动态引入 ElMessage，避免全局污染
        import('element-plus').then(({ ElMessage }) => {
          ElMessage.closeAll()
          ElMessage({
            dangerouslyUseHTMLString: true,
            message: `
              <div style="white-space:pre-line">
                <b>该文章内容已从对象存储加载（数据库已清理）。</b><br>
                <span style="color:#e6a23c;">⚠️ 重要提示：</span><br>
                - 模板变量已被替换为实际值，无法再修改旧的变量关联（网站、游戏盒子、游戏）。<br>
                - 您可以编辑文本内容，但再次发布时，旧的模板变量不会被重新替换。
              </div>
            `,
            type: 'warning',
            duration: 8000,
            showClose: true,
            offset: 60
          })
        })
      }
      
      open.value = true
      title.value = `编辑文章 - ${masterArticle.defaultLocale}（主语言版本）`
    }).catch(error => {
      proxy.$modal.msgError('加载语言版本失败：' + (error.msg || error.message || '文章不存在'))
    })
  }).catch(error => {
    proxy.$modal.msgError('加载文章失败：' + (error.msg || error.message || '文章不存在'))
  })
}

// 保存草稿（status=0，不发布到存储）
function saveDraft() {
  proxy.$refs["articleRef"].validate(valid => {
    if (valid) {
      // 保存前确保 storageKey 使用计算出的预览路径
      if (previewPath.value) {
        form.value.storageKey = previewPath.value
      }
      
      savingDraft.value = true
      
      const isEdit = !!form.value.id
      
      // 准备文章数据
      const articleData = {
        id: form.value.id,
        masterArticleId: form.value.masterArticleId,
        locale: form.value.locale,
        title: form.value.title,
        slug: form.value.slug,
        subtitle: form.value.subtitle,
        description: form.value.description,
        keywords: form.value.keywords,
        coverUrl: form.value.coverUrl,
        content: form.value.content,
        contentType: 'markdown',
        author: form.value.author,
        pathRule: form.value.pathRule,
        storageConfigId: form.value.storageConfigId,
        storageKey: form.value.storageKey,
        status: '0' // 草稿状态
      }
      
      // 如果是新增，还需要传递主文章相关信息
      if (!isEdit) {
        // 新增时需要创建主文章
        const draftData = {
          // 主文章信息
          masterArticle: {
            siteId: form.value.siteId,
            categoryId: form.value.categoryId,
            defaultLocale: form.value.locale,
            isTop: form.value.isTop,
            isRecommend: form.value.isRecommend,
            sortOrder: form.value.sortOrder || 0,
            remark: form.value.remark
          },
          // 语言版本信息
          article: articleData,
          // 关联信息
          gameBoxIds: form.value.gameBoxIds || [],
          gameIds: form.value.gameIds || [],
          dramaIds: form.value.dramaIds || []
        }
        
        saveMasterArticleDraft(draftData).then(response => {
          proxy.$modal.msgSuccess("草稿保存成功！")
          savingDraft.value = false
          open.value = false
          reset()
          getList()
        }).catch(error => {
          proxy.$modal.msgError("保存草稿失败：" + (error.msg || error.message || "未知错误"))
          savingDraft.value = false
        })
      } else {
        // 编辑时更新文章和主文章关联信息
        const draftData = {
          masterArticleId: form.value.masterArticleId,
          articleId: form.value.id,
          // 主文章信息（包含关联）
          masterArticle: {
            siteId: form.value.siteId,
            categoryId: form.value.categoryId,
            isTop: form.value.isTop,
            isRecommend: form.value.isRecommend,
            sortOrder: form.value.sortOrder || 0,
            remark: form.value.remark
          },
          // 语言版本信息
          article: articleData,
          // 关联信息
          gameBoxIds: form.value.gameBoxIds || [],
          gameIds: form.value.gameIds || [],
          dramaIds: form.value.dramaIds || []
        }
        
        updateMasterArticleDraft(draftData).then(response => {
          proxy.$modal.msgSuccess("草稿更新成功！")
          savingDraft.value = false
          open.value = false
          reset()
          getList()
        }).catch(error => {
          proxy.$modal.msgError("更新草稿失败：" + (error.msg || error.message || "未知错误"))
          savingDraft.value = false
        })
      }
    }
  })
}

// 发布到存储（基于现有草稿发布）
function submitForm() {
  proxy.$refs["articleRef"].validate(valid => {
    if (valid) {
      // 发布时需要验证存储配置
      if (!form.value.storageConfigId) {
        proxy.$modal.msgError("请选择存储配置")
        return
      }
      if (!form.value.pathRule) {
        proxy.$modal.msgError("请选择路径规则")
        return
      }
      
      publishing.value = true
      
      const isEdit = !!form.value.id
      
      if (!isEdit) {
        // 新增模式：先保存草稿（原始内容），然后调用发布API
        const draftData = {
          // 主文章信息
          masterArticle: {
            siteId: form.value.siteId,
            categoryId: form.value.categoryId,
            defaultLocale: form.value.locale,
            isTop: form.value.isTop,
            isRecommend: form.value.isRecommend,
            sortOrder: form.value.sortOrder || 0,
            remark: form.value.remark
          },
          // 语言版本信息（保存原始内容，带模板变量）
          article: {
            locale: form.value.locale,
            title: form.value.title,
            slug: form.value.slug,
            subtitle: form.value.subtitle,
            description: form.value.description,
            keywords: form.value.keywords,
            coverUrl: form.value.coverUrl,
            content: form.value.content, // 保存原始内容
            contentType: 'markdown',
            author: form.value.author,
            pathRule: form.value.pathRule,
            storageConfigId: form.value.storageConfigId,
            storageKey: previewPath.value
          },
          // 关联信息
          gameBoxIds: form.value.gameBoxIds || [],
          gameIds: form.value.gameIds || [],
          dramaIds: form.value.dramaIds || []
        }
        
        // 先保存草稿
        saveMasterArticleDraft(draftData).then(response => {
          // 获取新创建的文章ID
          const articleId = response.data
          if (articleId) {
            // 调用发布API，后端会自动替换模板变量
            publishArticle(articleId).then(publishResponse => {
              handlePublishResponse(publishResponse, articleId)
            }).catch(publishError => {
              proxy.$modal.msgError("发布失败：" + (publishError.msg || publishError.message || "未知错误"))
              publishing.value = false
            })
          } else {
            proxy.$modal.msgError("保存成功但无法获取文章ID，请手动发布")
            publishing.value = false
            open.value = false
            getList()
          }
        }).catch(error => {
          proxy.$modal.msgError("保存失败：" + (error.msg || error.message || "未知错误"))
          publishing.value = false
        })
      } else {
        // 编辑模式：先更新草稿（原始内容），然后调用发布API
        const draftData = {
          masterArticleId: form.value.masterArticleId,
          articleId: form.value.id,
          // 主文章信息
          masterArticle: {
            siteId: form.value.siteId,
            categoryId: form.value.categoryId,
            isTop: form.value.isTop,
            isRecommend: form.value.isRecommend,
            sortOrder: form.value.sortOrder || 0,
            remark: form.value.remark
          },
          // 语言版本信息（保存原始内容，带模板变量）
          article: {
            id: form.value.id,
            locale: form.value.locale,
            title: form.value.title,
            slug: form.value.slug,
            subtitle: form.value.subtitle,
            description: form.value.description,
            keywords: form.value.keywords,
            coverUrl: form.value.coverUrl,
            content: form.value.content, // 保存原始内容
            contentType: 'markdown',
            author: form.value.author,
            pathRule: form.value.pathRule,
            storageConfigId: form.value.storageConfigId,
            storageKey: previewPath.value
          },
          // 关联信息
          gameBoxIds: form.value.gameBoxIds || [],
          gameIds: form.value.gameIds || [],
          dramaIds: form.value.dramaIds || []
        }
        
        // 先更新草稿
        updateMasterArticleDraft(draftData).then(response => {
          // 调用发布API，后端会自动替换模板变量
          publishArticle(form.value.id).then(publishResponse => {
            handlePublishResponse(publishResponse, form.value.id)
          }).catch(publishError => {
            proxy.$modal.msgError("发布失败：" + (publishError.msg || publishError.message || "未知错误"))
            publishing.value = false
          })
        }).catch(error => {
          proxy.$modal.msgError("更新失败：" + (error.msg || error.message || "未知错误"))
          publishing.value = false
        })
      }
    }
  })
}

// 处理发布响应的公共方法
// 处理发布响应的公共方法
function handlePublishResponse(response, articleId) {
  // 检查是否需要用户确认覆盖（需要检查 response.data.needConfirm）
  const responseData = response.data || {}
  if (responseData.needConfirm) {
    publishing.value = false
    
    // 判断是文件已存在还是删除旧文件失败
    let confirmMessage = ''
    if (responseData.deleteOldFileFailed) {
      confirmMessage = `删除旧文件 ${responseData.oldFile} 失败：${responseData.errorMessage}\n\n是否继续发布？`
    } else {
      confirmMessage = `文件 ${responseData.existingFile} 已存在，是否覆盖？`
    }
    
    proxy.$modal.confirm(confirmMessage).then(() => {
      // 用户确认，强制发布
      publishing.value = true
      
      // 调用强制发布API
      publishArticleForce(articleId).then(res => {
        proxy.$modal.msgSuccess("发布成功！")
        publishing.value = false
        
        // 关闭编辑对话框（如果是从编辑页面发布）
        if (open.value) {
          open.value = false
        }
        
        // 刷新主文章列表
        getList()
        
        // 如果发布管理对话框打开，刷新语言版本列表
        if (languageVersionDialogOpen.value && currentMasterArticle.value) {
          handlePublishManagement(currentMasterArticle.value)
        }
      }).catch(err => {
        proxy.$modal.msgError("发布失败：" + (err.msg || err.message || "未知错误"))
        publishing.value = false
      })
    }).catch(() => {
      proxy.$modal.msgInfo("已取消发布")
    })
    return
  }
  
  // 正常发布成功
  proxy.$modal.msgSuccess("发布成功！")
  publishing.value = false
  
  // 关闭编辑对话框（如果是从编辑页面发布）
  if (open.value) {
    open.value = false
  }
  
  // 刷新主文章列表
  getList()
  
  // 如果发布管理对话框打开，刷新语言版本列表
  if (languageVersionDialogOpen.value && currentMasterArticle.value) {
    handlePublishManagement(currentMasterArticle.value)
  }
}

// 处理图片上传
function handleUploadImage(files, callback) {
  // 这里可以实现图片上传到存储的逻辑
  // 暂时使用base64
  const promises = files.map(file => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = (e) => {
        resolve(e.target.result)
      }
      reader.onerror = reject
      reader.readAsDataURL(file)
    })
  })
  
  Promise.all(promises).then(urls => {
    callback(urls)
  }).catch(error => {
    proxy.$modal.msgError("图片处理失败")
    console.error(error)
  })
}

// 删除主文章（连带删除所有语言版本）
function handleDelete(row) {
  // 注意：列表中的每一行实际上是主文章，row.id 就是 masterArticleId
  const masterArticleIds = row.id || ids.value.join(',')
  const confirmMsg = row.id 
    ? `是否确认删除主文章"${row.title}"？\n\n删除后将同时删除：\n1. 该主文章的所有语言版本\n2. 远程存储中已发布的文件`
    : `是否确认删除选中的 ${ids.value.length} 篇主文章？\n\n删除后将同时删除：\n1. 这些主文章的所有语言版本\n2. 远程存储中已发布的文件`
  
  proxy.$modal.confirm(confirmMsg).then(function() {
    return delMasterArticle(masterArticleIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

// 删除单个文章（仅删除指定语言版本）
async function handleDeleteSingleArticle(article) {
  try {
    await proxy.$modal.confirm(`是否确认删除"${article.title}"(${article.locale})？\n\n删除后将同时删除远程存储中的文件。`)
    await delArticle(article.id)
    proxy.$modal.msgSuccess("删除成功")
    
    // 刷新语言版本列表
    const refreshResponse = await listArticle({
      masterArticleId: article.masterArticleId,
      queryMode: 'creator',
      pageNum: 1,
      pageSize: 100
    })
    if (refreshResponse.code === 200) {
      languageVersions.value = refreshResponse.rows || []
    }
    
    // 刷新主文章列表
    getList()
  } catch (error) {
    // 用户取消或删除失败
  }
}

// 检查单个文章的远程文件状态
async function handleCheckRemoteStatus(row) {
  // 为该行设置检查状态
  row.checkingRemote = true
  
  try {
    const response = await checkRemoteFileStatus(row.id)
    if (response.code === 200) {
      // 更新行数据
      row.remoteFileStatus = response.data.remoteFileStatus
      row.lastSyncCheckTime = response.data.lastSyncCheckTime
      row.remoteFileSize = response.data.remoteFileSize
      
      // 根据状态显示不同的消息
      const statusMap = {
        '1': { type: 'success', msg: '远程文件正常' },
        '2': { type: 'warning', msg: '远程文件不存在' },
        '3': { type: 'error', msg: '远程文件检查异常' }
      }
      const status = statusMap[response.data.remoteFileStatus]
      if (status) {
        proxy.$modal[`msg${status.type.charAt(0).toUpperCase() + status.type.slice(1)}`](status.msg)
      }
      
      // 如果在语言版本管理对话框中，刷新语言版本列表
      if (languageVersionDialogOpen.value && currentMasterArticle.value.id) {
        const refreshResponse = await listArticle({
          masterArticleId: currentMasterArticle.value.id,
          queryMode: 'creator',
          pageNum: 1,
          pageSize: 100
        })
        if (refreshResponse.code === 200) {
          languageVersions.value = refreshResponse.rows || []
        }
      }
    } else {
      proxy.$modal.msgError(response.msg || '检查失败')
    }
  } catch (error) {
    proxy.$modal.msgError('检查远程状态失败：' + (error.message || error))
  } finally {
    row.checkingRemote = false
  }
}

// 批量检查远程文件状态
async function handleBatchCheckRemote() {
  if (selectedLanguageVersions.value.length === 0) {
    proxy.$modal.msgWarning('请选择要检查的语言版本')
    return
  }
  
  // 提取选中语言版本的文章ID
  const articleIds = selectedLanguageVersions.value.map(v => v.id)
  
  batchCheckingRemote.value = true
  
  try {
    const response = await batchCheckRemoteFileStatus(articleIds)
    if (response.code === 200) {
      const result = response.data
      
      // 显示检查结果
      let message = `检查完成！\n总计: ${result.totalCount}\n成功: ${result.successCount}\n失败: ${result.failCount}`
      if (result.successCount > 0) {
        message += `\n\n状态统计：\n正常: ${result.normalCount}\n不存在: ${result.missingCount}\n异常: ${result.errorCount}\n仅本地: ${result.localOnlyCount}`
      }
      
      proxy.$modal.msgSuccess(message)
      
      // 刷新语言版本列表
      const refreshResponse = await listArticle({
        masterArticleId: currentMasterArticle.value.id,
        queryMode: 'creator',
        pageNum: 1,
        pageSize: 100
      })
      if (refreshResponse.code === 200) {
        languageVersions.value = refreshResponse.rows || []
      }
    } else {
      proxy.$modal.msgError(response.msg || '批量检查失败')
    }
  } catch (error) {
    proxy.$modal.msgError('批量检查失败：' + (error.message || error))
  } finally {
    batchCheckingRemote.value = false
  }
}

// 管理网站关联
function handleManageSites(row) {
  currentArticleIdForSites.value = row.id
  currentArticleTitleForSites.value = row.title
  currentArticleCreatorSiteId.value = row.siteId || 0
  siteRelationDialogOpen.value = true
}

// 撤销发布
function handleUnpublish(row) {
  proxy.$modal.confirm(`确认撤销发布文章"${row.title}"吗？撤销后文章将改为草稿状态，远程存储中的文件将被删除。`).then(function() {
    return unpublishArticle(row.id)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("撤销发布成功")
  }).catch(() => {})
}

/** 获取可见性值 */
function getVisibilityValue(row) {
  if (row.relationSource === 'default') {
    // 默认配置：使用 isVisible 判断（后端已处理关联优先逻辑）
    return row.isVisible || '1'
  } else if (row.relationSource === 'own') {
    // 自有数据：基于 publishStatus（0=未发布, 1=部分发布, 2=已发布）
    return row.publishStatus === '2' ? '1' : '0'
  } else if (row.relationSource === 'shared') {
    // 共享数据：使用 isVisible 状态
    return row.isVisible || '1'
  }
  return '0'
}

/** 获取可见性显示文本 */
function getVisibilityText(row) {
  if (row.relationSource === 'own') {
    // 自有数据：根据发布状态显示不同文本
    if (row.publishStatus === '2') {
      return '上架'
    } else if (row.publishStatus === '1') {
      return '部分发布'
    } else {
      return '下架'
    }
  }
  // 其他情况统一显示
  return getVisibilityValue(row) === '1' ? '显示' : '隐藏'
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
      const relationResponse = await getArticleSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段
        await updateArticleVisibility(currentQuerySiteId, row.id, newValue)
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
          proxy.$modal.msgSuccess('已排除该文章')
        } else {
          excluded = excluded.filter(id => id !== currentQuerySiteId)
          proxy.$modal.msgSuccess('已恢复该文章')
        }
        await batchSaveArticleSiteRelations({ articleIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
        // 重新加载列表以更新排除网站数量
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：批量设置所有语言版本的发布状态
      const action = newValue === '1' ? '上架' : '下架'
      const statusText = newValue === '1' ? '发布' : '下架'
      
      await proxy.$modal.confirm(
        `确认要${action}文章"${row.title}"吗？这将把该主文章的所有语言版本都设置为${statusText}状态。`,
        '批量操作确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      // 调用批量更新接口
      if (newValue === '1') {
        // 上架：批量更新状态为已发布
        // 使用文章列表API获取主文章的所有语言版本
        const versionsResponse = await listArticle({ masterArticleId: row.id, pageSize: 999 })
        const languageVersions = versionsResponse.rows || []
        
        // 过滤出未发布的语言版本
        const unpublishedVersions = languageVersions.filter(v => v.status === '0')
        
        if (unpublishedVersions.length === 0) {
          proxy.$modal.msgWarning('没有未发布的语言版本')
          return
        }
        
        // 调用批量更新状态接口
        const articleIds = unpublishedVersions.map(v => v.id)
        await batchUpdateArticleStatus(articleIds, '1')
        
        row.publishStatus = '2'
        proxy.$modal.msgSuccess('已上架，所有语言版本已设置为已发布')
      } else {
          // 下架：批量更新状态为草稿
          // 使用文章列表API获取主文章的所有语言版本
          const versionsResponse = await listArticle({ masterArticleId: row.id, pageSize: 999 })
          const languageVersions = versionsResponse.rows || []
          
          // 过滤出已发布的语言版本
          const publishedVersions = languageVersions.filter(v => v.status === '1')
          
          if (publishedVersions.length === 0) {
            proxy.$modal.msgWarning('没有已发布的语言版本')
            return
          }
          
          // 调用批量更新状态接口
          const articleIds = publishedVersions.map(v => v.id)
          await batchUpdateArticleStatus(articleIds, '0')
          
          row.publishStatus = '0'
          proxy.$modal.msgSuccess('已下架，所有语言版本已设置为草稿')
        }
      
      // 刷新列表以更新状态
      getList()
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updateArticleVisibility(currentQuerySiteId, row.id, newValue)
      const action = newValue === '1' ? '显示' : '隐藏'
      proxy.$modal.msgSuccess(`${action}成功`)
      // 刷新列表以获取最新的排除网站统计数据
      getList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换可见性失败:', error)
      proxy.$modal.msgError('操作失败: ' + (error.msg || error.message || '未知错误'))
    }
  }
}

/** 排除默认文章 */
function handleExcludeDefaultArticle(row) {
  proxy.$modal.confirm(`确认要排除默认文章"${row.title}"吗？排除后该文章将不在当前网站显示。`).then(async () => {
    const siteId = queryParams.value.siteId
    const res = await getArticleSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excluded.includes(siteId)) excluded.push(siteId)
    return batchSaveArticleSiteRelations({ articleIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess("排除成功")
    getList()
  }).catch(() => {})
}

/** 恢复被排除的默认文章 */
function handleRestoreDefaultArticle(row) {
  proxy.$modal.confirm(`确认要恢复默认文章"${row.title}"吗？恢复后该文章将重新在当前网站显示。`).then(async () => {
    const siteId = queryParams.value.siteId
    const res = await getArticleSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== siteId)
    return batchSaveArticleSiteRelations({ articleIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess("恢复成功")
    getList()
  }).catch(() => {})
}

// 打开主页绑定对话框
function handleBindHomepage(row) {
  // 使用主文章ID进行绑定（独立于语言）
  currentArticleForBinding.value = {
    masterArticleId: row.id,  // 主文章ID
    articleTitle: row.title || '未命名',
    siteId: row.siteId || 0
  }
  
  // 加载游戏和游戏盒子列表
  loadGamesForBinding()
  loadGameBoxesForBinding()
  
  // 查询主文章的主页绑定信息
  queryMasterArticleHomepageBinding(row.id)
  
  bindDialogOpen.value = true
}

// 查询主文章的主页绑定信息
function queryMasterArticleHomepageBinding(masterArticleId) {
  getMasterArticleHomepageBinding(masterArticleId).then(response => {
    const binding = response.data
    if (binding && binding.gameId) {
      currentBindType.value = 'game'
      currentBindTargetId.value = binding.gameId
    } else if (binding && binding.gameBoxId) {
      currentBindType.value = 'gamebox'
      currentBindTargetId.value = binding.gameBoxId
    } else {
      currentBindType.value = 'none'
      currentBindTargetId.value = undefined
    }
  }).catch(error => {
    console.error('查询主页绑定信息失败:', error)
    // 查询失败时设置默认值
    currentBindType.value = 'none'
    currentBindTargetId.value = undefined
  })
}

// 加载游戏列表（用于绑定对话框）
function loadGamesForBinding() {
  if (gameList.value.length > 0) return // 已加载则跳过
  
  loadingGames.value = true
  listGame({ status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
    gameList.value = response.rows || []
  }).catch(error => {
    console.error('加载游戏列表失败:', error)
    proxy.$modal.msgError('加载游戏列表失败')
  }).finally(() => {
    loadingGames.value = false
  })
}

// 加载游戏盒子列表（用于绑定对话框）
function loadGameBoxesForBinding() {
  if (gameBoxList.value.length > 0) return // 已加载则跳过
  
  loadingGameBoxes.value = true
  listBox({ status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
    gameBoxList.value = response.rows || []
  }).catch(error => {
    console.error('加载游戏盒子列表失败:', error)
    proxy.$modal.msgError('加载游戏盒子列表失败')
  }).finally(() => {
    loadingGameBoxes.value = false
  })
}

// 提交主页绑定
function handleSubmitMultiLanguageBinding() {
  const masterArticleId = currentArticleForBinding.value.masterArticleId
  const bindType = currentBindType.value
  const bindTargetId = currentBindTargetId.value
  
  // 如果选择解绑
  if (bindType === 'none') {
    proxy.$modal.confirm('确认要解除该主文章的主页绑定吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      binding.value = true
      
      // 先查询当前绑定信息，确定要解绑的类型
      getMasterArticleHomepageBinding(masterArticleId).then(response => {
        const currentBinding = response.data
        
        if (!currentBinding) {
          proxy.$modal.msgWarning("该主文章未绑定任何主页")
          binding.value = false
          return
        }
        
        // 根据当前绑定类型调用对应的解绑API
        let unbindApi
        let params = { masterArticleId }
        
        if (currentBinding.gameId) {
          unbindApi = unbindMasterArticleFromGame
          params.gameId = currentBinding.gameId
        } else if (currentBinding.gameBoxId) {
          unbindApi = unbindMasterArticleFromGameBox
          params.gameBoxId = currentBinding.gameBoxId
        } else {
          proxy.$modal.msgWarning("绑定数据异常")
          binding.value = false
          return
        }
        
        // 调用解绑API
        unbindApi(params).then(() => {
          proxy.$modal.msgSuccess("已解除绑定")
          
          // 重新查询绑定状态以更新UI显示
          queryMasterArticleHomepageBinding(masterArticleId)
          
          // 延迟关闭对话框，让用户看到解绑结果
          setTimeout(() => {
            bindDialogOpen.value = false
          }, 1000)
          
          getList()
        }).catch(error => {
          proxy.$modal.msgError("解绑失败：" + (error.msg || error.message || "未知错误"))
        }).finally(() => {
          binding.value = false
        })
      }).catch(error => {
        proxy.$modal.msgError("查询绑定信息失败：" + (error.msg || error.message || "未知错误"))
        binding.value = false
      })
    }).catch(() => {})
    return
  }
  
  // 检查是否选择了目标
  if (!bindTargetId) {
    proxy.$modal.msgWarning('请选择要绑定的游戏或游戏盒子')
    return
  }
  
  binding.value = true
  
  // 选择对应的API函数
  const bindApi = bindType === 'game' ? bindMasterArticleToGame : bindMasterArticleToGameBox
  const targetIdKey = bindType === 'game' ? 'gameId' : 'gameBoxId'
  
  const params = {
    masterArticleId,
    [targetIdKey]: bindTargetId,
    force: false // 默认不强制覆盖
  }
  
  bindApi(params).then(response => {
    // 检查是否需要确认
    if (response.needConfirm) {
      proxy.$modal.confirm(
        response.message || '该文章已绑定到其他主页，是否要强制覆盖？',
        '确认覆盖',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 用户确认后，使用force:true重新绑定
        const forceParams = {
          masterArticleId,
          [targetIdKey]: bindTargetId,
          force: true
        }
        bindApi(forceParams).then(() => {
          proxy.$modal.msgSuccess("绑定成功")
          queryMasterArticleHomepageBinding(masterArticleId)
          setTimeout(() => {
            bindDialogOpen.value = false
          }, 1000)
          getList()
        }).catch(err => {
          console.error('绑定失败:', err)
        }).finally(() => {
          binding.value = false
        })
      }).catch(() => {
        binding.value = false
      })
    } else {
      // 直接绑定成功
      proxy.$modal.msgSuccess("绑定成功")
      queryMasterArticleHomepageBinding(masterArticleId)
      setTimeout(() => {
        bindDialogOpen.value = false
      }, 1000)
      getList()
      binding.value = false
    }
  }).catch(error => {
    console.error('绑定失败:', error)
    proxy.$modal.msgError('绑定失败: ' + (error.msg || error.message || '未知错误'))
    binding.value = false
  })
}

// 获取语言标签
function getLocaleLabel(locale) {
  const labels = {
    'zh-CN': '简体中文',
    'zh-TW': '繁体中文',
    'en-US': 'English',
    'ja-JP': '日本語'
  }
  return labels[locale] || locale
}

// 打开URL
function openUrl(url) {
  window.open(url, '_blank')
}

// 监听表单中的siteId变化，自动重新加载分类
watch(() => form.value.siteId, (newSiteId, oldSiteId) => {
  // 只有在对话框已打开且是用户主动切换站点时才处理
  if (!open.value) return
  
  // 重新加载文章分类
  loadArticleCategoriesForDialog()
  // 重新加载存储分类
  loadStorageCategories(newSiteId || 0)
  
  // 只有在用户主动切换站点时（不是初始化时）才清空相关字段
  if (oldSiteId !== undefined) {
    form.value.categoryId = undefined  // 清空文章分类
    form.value.storageCategoryId = undefined  // 清空存储分类
    form.value.storageConfigId = undefined  // 清空存储配置
  }
})

// ===== 多语言版本管理相关函数 =====
/** 从站点 supportedLocales 字段解析语言列表 */
function parseSiteLocales(supportedLocales) {
  const languageNames = {
    'zh-CN': '简体中文',
    'zh-TW': '繁体中文',
    'en-US': 'English',
    'ja-JP': '日本語',
    'ko-KR': '한국어',
    'es-ES': 'Español',
    'fr-FR': 'Français',
    'de-DE': 'Deutsch',
    'ru-RU': 'Русский',
    'pt-BR': 'Português',
    'it-IT': 'Italiano',
    'th-TH': 'ไทย',
    'vi-VN': 'Tiếng Việt',
    'ar-SA': 'العربية'
  }
  try {
    const parsed = JSON.parse(supportedLocales)
    if (Array.isArray(parsed) && parsed.length > 0) {
      if (typeof parsed[0] === 'string') {
        return parsed.map(code => ({ code, name: languageNames[code] || code }))
      } else if (typeof parsed[0] === 'object') {
        return parsed
      }
    }
  } catch (e) {
    console.error('解析语言列表失败:', e)
  }
  return []
}

/** 打开新增语言版本对话框 */
async function handleAddNewLanguageVersion() {
  if (!currentMasterArticle.value.id) {
    proxy.$modal.msgError('无效的主文章')
    return
  }
  
  try {
    const siteId = currentMasterArticle.value.siteId
    let locales = []

    // 优先从 siteList 缓存中获取
    let siteData = siteList.value.find(s => s.id === siteId)

    // 如果缓存中没有或 supportedLocales 未配置，直接调用 API 获取最新数据
    if (!siteData || !siteData.supportedLocales) {
      try {
        const siteResp = await getSite(siteId)
        if (siteResp.data) {
          siteData = siteResp.data
        }
      } catch (e) {
        console.error('获取网站详情失败:', e)
      }
    }

    if (siteData && siteData.supportedLocales) {
      locales = parseSiteLocales(siteData.supportedLocales)
    }

    // 如果网站未配置支持的语言，仍使用通用备选列表（降级兜底）
    if (!locales || locales.length === 0) {
      locales = [
        { code: 'zh-CN', name: '简体中文' },
        { code: 'zh-TW', name: '繁体中文' },
        { code: 'en-US', name: 'English' },
        { code: 'ja-JP', name: '日本語' },
        { code: 'ko-KR', name: '한국어' },
        { code: 'es-ES', name: 'Español' },
        { code: 'fr-FR', name: 'Français' },
        { code: 'de-DE', name: 'Deutsch' },
        { code: 'ru-RU', name: 'Русский' }
      ]
    }
    
    // 获取已存在的语言版本
    existingLocales.value = languageVersions.value.map(v => v.locale)
    
    // 设置可用语言列表
    availableLocales.value = locales
    
    // 获取默认语言版本的slug
    const defaultVersionArticle = languageVersions.value.find(
      v => v.locale === currentMasterArticle.value.defaultLocale
    )
    const baseSlug = defaultVersionArticle?.slug || ''
    
    // 重置表单，默认使用主文章标题和slug
    addLanguageVersionForm.value = {
      masterArticleId: currentMasterArticle.value.id,
      locale: undefined,
      title: currentMasterArticle.value.title || '',
      slug: '',
      autoTranslate: false,  // 默认不自动翻译
      _baseSlug: baseSlug  // 保存基础slug，用于添加语言标识
    }
    
    // 使用 nextTick 确保数据更新后再显示对话框
    await proxy.$nextTick()
    addLanguageVersionDialogOpen.value = true
    
  } catch (error) {
    console.error('打开新增语言版本对话框失败:', error)
    proxy.$modal.msgError('打开对话框失败')
  }
}

/** 处理语言选择变化，自动生成带语言标识的slug */
function handleLocaleChange(locale) {
  if (!locale || !addLanguageVersionForm.value._baseSlug) return
  
  // 使用完整的语言代码，例如 zh-CN, en-US, zh-TW
  const baseSlug = addLanguageVersionForm.value._baseSlug
  if (baseSlug) {
    // 将语言代码转为小写，例如 zh-CN -> zh-cn
    const langCode = locale.toLowerCase()
    addLanguageVersionForm.value.slug = `${baseSlug}-${langCode}`
  }
}

/** 提交新增语言版本 */
async function submitAddLanguageVersion() {
  if (!addLanguageVersionFormRef.value) return
  
  await addLanguageVersionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        addingLanguageVersion.value = true
        
        // 获取默认语言版本的文章数据作为模板
        const defaultVersionArticle = languageVersions.value.find(
          v => v.locale === currentMasterArticle.value.defaultLocale
        )
        
        // 如果找到默认语言版本，获取其完整数据
        let templateData = {}
        if (defaultVersionArticle && defaultVersionArticle.id) {
          const detailResponse = await getArticle(defaultVersionArticle.id)
          if (detailResponse.code === 200 && detailResponse.data) {
            templateData = detailResponse.data
          }
        }
        
        // 构建新文章数据，复用默认语言版本的所有配置
        const articleData = {
          masterArticleId: addLanguageVersionForm.value.masterArticleId,
          locale: addLanguageVersionForm.value.locale,
          title: addLanguageVersionForm.value.title,
          slug: addLanguageVersionForm.value.slug || undefined,
          // 复用默认语言版本的内容和其他字段
          content: templateData.content || '',
          contentType: templateData.contentType || 'markdown',
          author: templateData.author || '',
          description: templateData.description || '',
          keywords: templateData.keywords || '',
          coverImage: templateData.coverImage || '',
          coverUrl: templateData.coverUrl || '',
          categoryId: templateData.categoryId || currentMasterArticle.value.categoryId,
          siteId: templateData.siteId || currentMasterArticle.value.siteId,
          // 复用存储配置和路径规则
          storageConfigId: templateData.storageConfigId,
          storageCategoryId: templateData.storageCategoryId,
          pathRule: templateData.pathRule || 'date-title',
          // 生成多语言的 storageKey
          storageKey: generateStorageKeyForLocale(
            addLanguageVersionForm.value.slug,
            templateData.pathRule || 'date-title',
            addLanguageVersionForm.value.locale
          ),
          // 复用其他设置
          isTop: templateData.isTop || '0',
          isRecommend: templateData.isRecommend || '0',
          status: '0',  // 新文章默认为草稿状态
          remark: templateData.remark || ''
        }
        
        const response = await addArticle(articleData)
        if (response.code === 200) {
          // 如果选择了自动翻译
          if (addLanguageVersionForm.value.autoTranslate && templateData.content) {
            try {
              // 准备要翻译的字段及其翻译结果
              const fieldsToTranslate = {}
              if (addLanguageVersionForm.value.title) fieldsToTranslate.title = addLanguageVersionForm.value.title
              if (templateData.description) fieldsToTranslate.description = templateData.description
              if (templateData.keywords) fieldsToTranslate.keywords = templateData.keywords
              if (templateData.content) fieldsToTranslate.content = templateData.content
              
              // 翻译每个字段
              const translatedFields = {}
              let successCount = 0
              let failCount = 0
              
              for (const [field, value] of Object.entries(fieldsToTranslate)) {
                if (value) {
                  try {
                    const translateResult = await autoTranslateText(value, addLanguageVersionForm.value.locale)
                    
                    if (translateResult.code === 200 && (translateResult.data || translateResult.msg)) {
                      // 翻译结果可能在 data 或 msg 字段中
                      translatedFields[field] = translateResult.data || translateResult.msg
                      successCount++
                    } else {
                      failCount++
                      console.warn(`字段 ${field} 翻译失败，响应码: ${translateResult.code}, 消息: ${translateResult.msg}`)
                    }
                  } catch (err) {
                    failCount++
                    console.error(`翻译字段 ${field} 异常:`, err)
                  }
                }
              }
              
              // 如果有翻译结果，需要查询新创建的文章ID再更新
              if (Object.keys(translatedFields).length > 0) {
                // 查询刚创建的文章（根据masterArticleId和locale）
                const articleListResponse = await listArticle({
                  masterArticleId: addLanguageVersionForm.value.masterArticleId,
                  locale: addLanguageVersionForm.value.locale,
                  pageNum: 1,
                  pageSize: 1
                })
                
                if (articleListResponse.code === 200 && articleListResponse.rows && articleListResponse.rows.length > 0) {
                  const newArticleId = articleListResponse.rows[0].id
                  
                  await updateArticle({
                    id: newArticleId,
                    ...translatedFields
                  })
                  proxy.$modal.msgSuccess(`新增语言版本并自动翻译成功（成功 ${successCount} 个字段${failCount > 0 ? '，失败 ' + failCount + ' 个字段' : ''}）`)
                } else {
                  proxy.$modal.msgWarning('新增成功，翻译完成，但无法更新文章，请手动刷新查看')
                }
              } else {
                proxy.$modal.msgWarning('新增语言版本成功，但所有字段翻译均失败，请手动翻译')
              }
            } catch (error) {
              console.error('自动翻译失败:', error)
              proxy.$modal.msgWarning('新增成功，但自动翻译失败，请手动翻译')
            }
          } else {
            proxy.$modal.msgSuccess('新增语言版本成功')
          }
          
          addLanguageVersionDialogOpen.value = false
          
          // 刷新语言版本列表
          const refreshResponse = await listArticle({
            masterArticleId: currentMasterArticle.value.id,
            queryMode: 'creator',  // 使用创建者模式，因为语言版本属于同一个主文章
            pageNum: 1,
            pageSize: 100
          })
          if (refreshResponse.code === 200) {
            languageVersions.value = refreshResponse.rows || []
          }
        } else {
          proxy.$modal.msgError('新增失败：' + response.msg)
        }
      } catch (error) {
        console.error('新增语言版本失败:', error)
        proxy.$modal.msgError('新增语言版本失败')
      } finally {
        addingLanguageVersion.value = false
      }
    }
  })
}

/** 发布管理 */
async function handlePublishManagement(article) {
  // 当前查询的是主文章列表，article.id 就是主文章ID
  if (!article.id) {
    proxy.$modal.msgError('无效的主文章ID')
    return
  }
  
  try {
    loading.value = true
    
    // 设置当前主文章信息
    currentMasterArticle.value = {
      id: article.id,
      siteId: article.siteId,
      categoryId: article.categoryId,
      defaultLocale: article.defaultLocale,
      title: article.title,
      // 保存原始文章对象，用于新增语言版本时复制数据
      _originalArticle: article
    }
    
    // 通过主文章ID查询所有语言版本
    const response = await listArticle({
      masterArticleId: article.id,
      pageNum: 1,
      pageSize: 100
    })
    
    if (response.code === 200) {
      languageVersions.value = response.rows || []
      if (languageVersions.value.length === 0) {
        proxy.$modal.msgWarning('未找到该主文章的其他语言版本')
        return
      }
      languageVersionDialogOpen.value = true
    } else {
      proxy.$modal.msgError('获取语言版本失败：' + response.msg)
    }
  } catch (error) {
    console.error('获取语言版本失败:', error)
    proxy.$modal.msgError('获取语言版本失败')
  } finally {
    loading.value = false
  }
}

/** 语言版本选择变化 */
function handleLanguageVersionSelectionChange(selection) {
  selectedLanguageVersions.value = selection
}

/** 发布单个文章 */
async function handlePublishArticle(article) {
  try {
    proxy.$modal.confirm(`确认发布文章 "${article.title}"？`).then(async () => {
      publishing.value = true
      
      try {
        // 调用发布API，后端会自动替换模板变量
        const result = await publishArticle(article.id)
        
        // 使用统一的 handlePublishResponse 处理响应
        handlePublishResponse(result, article.id)
      } catch (error) {
        proxy.$modal.msgError('发布失败：' + (error.msg || error.message || '未知错误'))
        publishing.value = false
      }
    }).catch(() => {})
  } catch (error) {
    console.error('发布文章失败:', error)
    proxy.$modal.msgError('发布文章失败')
  }
}

/** 批量发布文章 */
/** 批量发布文章 */
async function handleBatchPublish() {
  if (selectedLanguageVersions.value.length === 0) {
    proxy.$modal.msgWarning('请选择要发布的文章')
    return
  }

  // 过滤已发布的文章
  const unpublishedArticles = selectedLanguageVersions.value.filter(a => a.status !== '1')
  if (unpublishedArticles.length === 0) {
    proxy.$modal.msgWarning('所选文章均已发布')
    return
  }

  proxy.$modal.confirm(`确认发布所选的 ${unpublishedArticles.length} 篇文章？`).then(async () => {
    await performBatchPublish(unpublishedArticles, false)
  }).catch(() => {})
}

/** 执行批量发布 */
async function performBatchPublish(articles, forceOverride = false) {
  publishing.value = true
  
  try {
    // 提取文章ID列表
    const articleIds = articles.map(article => article.id)
    
    // 调用相应的批量发布接口
    let result
    
    try {
      result = forceOverride ? 
        await batchPublishLanguageVersionsForce(articleIds) : 
        await batchPublishLanguageVersions(articleIds)
    } catch (apiError) {
      // 网络错误或其他异常
      throw apiError
    }
    
    // 批量操作统一返回 code: 200，通过 data 判断成功/失败
    const resultData = result.data
    
    // 检查是否需要确认（文件已存在需要覆盖）
    if (resultData.needConfirm) {
        const existingCount = resultData.existingCount
        const totalCount = resultData.totalCount
        const message = resultData.message || `检测到 ${existingCount} 篇文章已发布，${totalCount - existingCount} 篇未发布。是否强制覆盖已发布的文章？`
        
        try {
          await proxy.$modal.confirm(
            message, 
            '发布冲突确认', 
            {
              confirmButtonText: '强制覆盖',
              cancelButtonText: '取消',
              type: 'warning'
            }
          )
          
          // 用户确认后，递归调用强制发布
          publishing.value = true
          await performBatchPublish(articles, true)
        } catch (cancelError) {
          // 用户取消或发布失败，不做处理
          // 如果是用户取消，不显示错误
          // 如果是发布失败，错误已在内层显示
        }
        return // 提前返回，避免执行后面的逻辑
      }
      
      // 检查是否有失败的文章
      if (resultData.failCount > 0) {
        // 有失败，显示详细错误信息
        const failureItems = []
        if (resultData.failMessages && resultData.failMessages.trim()) {
          const failures = resultData.failMessages
            .split('; ')
            .filter(msg => msg.trim())
          
          failures.forEach((msg, index) => {
            failureItems.push(h('div', { style: 'margin: 5px 0;' }, `${index + 1}. ${msg}`))
          })
        }
        
        // 使用 h 函数创建 VNode
        const message = h('div', { style: 'text-align: left; line-height: 1.8;' }, [
          h('p', { style: 'margin-bottom: 10px; font-weight: bold;' }, '批量发布失败！'),
          h('p', { style: 'margin-bottom: 10px;' }, `成功：${resultData.successCount || 0} 篇    失败：${resultData.failCount} 篇`),
          failureItems.length > 0 ? h('p', { style: 'margin-bottom: 8px; font-weight: bold;' }, '失败详情：') : null,
          failureItems.length > 0 ? h('div', { style: 'margin-left: 10px; max-height: 300px; overflow-y: auto;' }, failureItems) : null,
          h('p', { style: 'margin-top: 10px; color: #E6A23C;' }, '请修复以上问题后重试。')
        ])
        
        // 使用 ElMessageBox 直接调用
        await ElMessageBox.confirm(message, '批量发布失败', {
          confirmButtonText: '我知道了',
          showCancelButton: false,
          type: 'error'
        })
      } else {
        // 全部发布成功
        proxy.$modal.msgSuccess(`批量发布完成！成功 ${resultData.successCount} 篇`)
        
        // 刷新语言版本列表
        await handlePublishManagement(currentMasterArticle.value)
        
        // 刷新主文章列表以更新发布状态
        getList()
      }
    } catch (error) {
    proxy.$modal.msgError('批量发布失败：' + (error.msg || error.message || '未知错误'))
  } finally {
    publishing.value = false
  }
}

// 格式化文件大小
function formatFileSize(bytes) {
  if (!bytes || bytes === 0 || isNaN(bytes)) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  const size = bytes / Math.pow(k, i)
  return (Math.round(size * 100) / 100) + ' ' + sizes[i]
}

// 格式化相对时间（如：2小时前、3天前）
function formatTimeAgo(dateTime) {
  if (!dateTime) return ''
  
  const now = new Date()
  const past = new Date(dateTime)
  const diffMs = now - past
  const diffSecs = Math.floor(diffMs / 1000)
  const diffMins = Math.floor(diffSecs / 60)
  const diffHours = Math.floor(diffMins / 60)
  const diffDays = Math.floor(diffHours / 24)
  
  if (diffSecs < 60) return '刚刚'
  if (diffMins < 60) return diffMins + '分钟前'
  if (diffHours < 24) return diffHours + '小时前'
  if (diffDays < 30) return diffDays + '天前'
  
  return parseTime(dateTime, '{y}-{m}-{d}')
}

// 获取自动清理信息
function getAutoClearInfo(article) {
  if (!article.publishTime || !article.contentAutoClearDays) return ''
  
  const publishDate = new Date(article.publishTime)
  const clearDate = new Date(publishDate.getTime() + article.contentAutoClearDays * 24 * 60 * 60 * 1000)
  const now = new Date()
  const diffDays = Math.ceil((clearDate - now) / (24 * 60 * 60 * 1000))
  
  if (diffDays > 0) {
    return `${diffDays}天后清理`
  } else if (diffDays === 0) {
    return '今天清理'
  } else {
    return '待清理'
  }
}

// 恢复content
async function handleRestoreContent(article) {
  try {
    await proxy.$modal.confirm(`确认从远程备份恢复 "${article.title}" 的content内容？`)
    
    // TODO: 调用恢复content的API
    // const response = await restoreArticleContent(article.id)
    
    proxy.$modal.msgWarning('恢复content功能暂未实现，请稍后')
    
  } catch (error) {
    // 用户取消
  }
}

// ========== 导出和导入功能 ==========

/** 打开导出对话框 */
function handleExport() {
  if (ids.value.length === 0) {
    proxy.$modal.msgError('请先选择要导出的文章')
    return
  }
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  proxy.$modal.msgWarning('文章导出功能开发中，敬请期待')
  exportDialogOpen.value = false
}

/** 全站导出 */
function handleFullExport() {
  fullExportDialogOpen.value = true
}

/** 确认全站导出 */
async function confirmFullExport() {
  proxy.$modal.msgWarning('文章全站导出功能开发中，敬请期待')
  fullExportDialogOpen.value = false
}

/** 系统导入 */
function handleSystemImport() {
  if (viewMode.value !== 'creator') {
    proxy.$modal.msgError('请切换到创建者模式进行导入')
    return
  }
  systemImportDialogOpen.value = true
  systemImportPreviewData.value = []
  systemImportFile.value = null
  systemImportForm.value.siteId = queryParams.value.siteId
}

/** 处理系统导入文件选择 */
async function handleSystemImportFileChange(file) {
  proxy.$modal.msgWarning('文章导入功能开发中，敬请期待')
}

/** 处理文件移除 */
function handleSystemImportFileRemove() {
  systemImportFile.value = null
  systemImportPreviewData.value = []
  return true
}

/** 确认系统导入 */
async function confirmSystemImport() {
  proxy.$modal.msgWarning('文章导入功能开发中，敬请期待')
}

/** 全站导入 */
function handleFullImport() {
  fullImportDialogOpen.value = true
  fullImportSites.value = []
  fullImportArticles.value = []
  fullImportRelations.value = []
  fullImportFile.value = null
  siteMapping.value = {}
}

/** 处理全站导入文件选择 */
async function handleFullImportFileChange(file) {
  proxy.$modal.msgWarning('文章全站导入功能开发中，敬请期待')
}

/** 处理全站导入文件移除 */
function handleFullImportFileRemove() {
  fullImportFile.value = null
  fullImportSites.value = []
  fullImportArticles.value = []
  fullImportRelations.value = []
  fullImportTranslations.value = []
  hasDefaultConfig.value = false
  return true
}

/** 确认全站导入 */
async function confirmFullImport() {
  proxy.$modal.msgWarning('文章全站导入功能开发中，敬请期待')
}

// === 批量排除管理 ===
async function handleBatchExclude() {
  const selectedRows = articleList.value.filter(article => ids.value.includes(article.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的文章')
    return
  }
  
  // 只允许默认配置的文章进行批量排除
  const invalidArticles = selectedRows.filter(article => !isPersonalSite(article.siteId, siteList.value))
  if (invalidArticles.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的文章进行批量排除管理')
    return
  }
  
  selectedArticlesForBatchExclude.value = selectedRows.map(article => ({
    id: article.id,
    title: article.title
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中文章的网站关系
    const batchRes = await getBatchArticleSites(selectedArticlesForBatchExclude.value.map(article => article.id))
    const batchMap = batchRes.data || {}
    const results = selectedArticlesForBatchExclude.value.map(article => {
      const sites = batchMap[article.id] || []
      return {
        articleId: article.id,
        articleTitle: article.title,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    articleExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有文章共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedArticlesForBatchExclude.value.length) {
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

function removeArticleFromBatchExclude(articleId) {
  selectedArticlesForBatchExclude.value = selectedArticlesForBatchExclude.value.filter(
    article => article.id !== articleId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== articleId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (articleTableRef.value) {
    const row = articleList.value.find(article => article.id === articleId)
    if (row) {
      articleTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  articleExclusionDetails.value = articleExclusionDetails.value.filter(
    detail => detail.articleId !== articleId
  )
  
  if (selectedArticlesForBatchExclude.value.length === 0) {
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
  if (selectedArticlesForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何文章')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一条请求处理所有选中文章的排除关系
    await batchSaveArticleSiteRelations({
      articleIds: selectedArticlesForBatchExclude.value.map(a => a.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedArticlesForBatchExclude.value.length} 个文章排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedArticlesForBatchExclude.value.length} 个文章的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    batchExclusionDialogOpen.value = false
    getList()
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
  const selectedRows = articleList.value.filter(article => ids.value.includes(article.id))

  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的文章')
    return
  }

  const isDefaultConfig = isPersonalSite(queryParams.value.siteId, siteList.value)

  selectedArticlesForBatchRelation.value = selectedRows.map(article => ({
    id: article.id,
    title: article.title,
    siteId: article.siteId
  }))

  try {
    const batchRes2 = await getBatchArticleSites(selectedArticlesForBatchRelation.value.map(article => article.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedArticlesForBatchRelation.value.map(article => {
      const sites = batchMap2[article.id] || []
      return {
        articleId: article.id,
        articleTitle: article.title,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== article.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    articleRelationDetails.value = results

    // 找出被所有文章共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedArticlesForBatchRelation.value.length) {
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

/** 从批量关联中移除某个文章 */
function removeArticleFromBatchRelation(articleId) {
  selectedArticlesForBatchRelation.value = selectedArticlesForBatchRelation.value.filter(
    article => article.id !== articleId
  )

  ids.value = ids.value.filter(id => id !== articleId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length

  if (articleTableRef.value) {
    const row = articleList.value.find(article => article.id === articleId)
    if (row) {
      articleTableRef.value.toggleRowSelection(row, false)
    }
  }

  articleRelationDetails.value = articleRelationDetails.value.filter(
    detail => detail.articleId !== articleId
  )

  if (selectedArticlesForBatchRelation.value.length === 0) {
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
  if (selectedArticlesForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何文章')
    return
  }

  batchRelationLoading.value = true

  try {
    // 一条请求处理所有选中文章的关联关系
    await batchSaveArticleSiteRelations({
      articleIds: selectedArticlesForBatchRelation.value.map(a => a.id),
      includeSiteIds: batchRelatedSiteIds.value
    })

    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedArticlesForBatchRelation.value.length} 篇文章关联 ${relateCount} 个网站`
      : `成功取消 ${selectedArticlesForBatchRelation.value.length} 篇文章的所有共享关联`

    proxy.$modal.msgSuccess(msg)
    getList()
    // 刷新对话框内关联详情
    const refreshRes2 = await getBatchArticleSites(selectedArticlesForBatchRelation.value.map(article => article.id))
    const refreshMap2 = refreshRes2.data || {}
    articleRelationDetails.value = selectedArticlesForBatchRelation.value.map(article => {
      const sites = refreshMap2[article.id] || []
      return {
        articleId: article.id,
        articleTitle: article.title,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== article.siteId).map(s => s.siteId),
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

// 初始化：先加载网站列表，然后由 loadSiteList 自动加载数据
// 初始化：先加载网站列表，然后由 loadSiteList 自动加载数据

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  if (siteList.value.length > 0) {
    getList()
  }
})

loadSiteList(() => {
  const restored = restoreViewModeSiteSelection(siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  viewMode.value = restored.viewMode
  queryParams.value.siteId = restored.siteId
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  loadSections(queryParams.value.siteId) // 加载板块列表
  loadArticleCategoriesForQuery(queryParams.value.siteId)
  getList()
})
loadStorageConfigs()
</script>

<style scoped>
.app-container :deep(.md-editor) {
  height: 600px;
}

.app-container :deep(.md-editor-preview-wrapper),
.app-container :deep(.md-editor-input-wrapper) {
  padding: 12px;
}
</style>
