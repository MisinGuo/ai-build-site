<template>
  <div class="app-container">
    <!-- 批次列表视图 -->
    <div v-if="!currentBatch">
      <el-form :model="batchQueryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="查看模式" prop="viewMode">
          <el-radio-group v-model="viewMode" @change="handleViewModeChange">
            <el-radio-button value="creator">创建者</el-radio-button>
            <el-radio-button value="related">关联网站</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="viewMode === 'creator' ? '创建者网站' : '关联网站'" prop="siteId">
          <SiteSelect v-model="batchQueryParams.siteId" :site-list="siteList" :show-default="viewMode === 'creator'" @change="handleSiteChange" />
        </el-form-item>
        <el-form-item v-if="viewMode === 'creator' && batchQueryParams.siteId && !isPersonalSiteCheck(batchQueryParams.siteId)" label=" ">
          <el-checkbox v-model="includeDefaultConfig" @change="handleQuery">含默认配置</el-checkbox>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="batchQueryParams.categoryId" placeholder="请选择分类" clearable style="width: 200px">
            <el-option 
              v-for="category in titleCategories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id"
            >
              <span v-if="category.icon" style="margin-right: 8px;">{{ category.icon }}</span>
              <span>{{ category.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="批次名称" prop="batchName">
          <el-input v-model="batchQueryParams.batchName" placeholder="请输入批次名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
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

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAddBatch">创建批次</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-if="isPersonalSiteCheck(batchQueryParams.siteId)" type="danger" plain icon="CircleClose" :disabled="batchMultiple" @click="handleBatchExclude">批量排除管理</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-if="viewMode === 'creator'" type="primary" plain icon="Connection" :disabled="batchMultiple" @click="handleBatchRelation">批量关联管理</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="Download" :disabled="batchMultiple" @click="handleExport">导出</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Upload" @click="handleSystemImport">导入</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="Upload" @click="showImportDialog = true">Excel导入</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="batchMultiple" @click="handleDeleteBatch">删除批次</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getBatchList"></right-toolbar>
      </el-row>

      <el-table ref="batchTableRef" v-loading="batchLoading" :data="batchList" @selection-change="handleBatchSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="批次名称" align="center" prop="batchName" min-width="200" :show-overflow-tooltip="true" />
        <el-table-column label="批次编号" align="center" prop="batchCode" width="180" />
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
            <CategoryTag v-if="scope.row.categoryId" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'title_pool', icon: scope.row.categoryIcon }" />
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column label="标题数量" align="center" prop="titleCount" width="100">
          <template #default="scope">
            <el-tag type="primary">{{ scope.row.titleCount || 0 }}条</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源说明" align="center" prop="importSource" width="120" />
        <el-table-column label="可见性" align="center" width="100" v-if="viewMode === 'related' && batchQueryParams.siteId && !isPersonalSiteCheck(batchQueryParams.siteId)">
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
        <el-table-column label="状态" align="center" prop="status" width="120" v-if="viewMode === 'creator'">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220" fixed="right">
          <template #default="scope">
            <!-- 关联模式下的操作 - 基于relationSource统一判断 -->
            <template v-if="viewMode === 'related'">
              <!-- own: 网站自己的配置 - 可修改、删除 -->
              <template v-if="scope.row.relationSource === 'own'">
                <el-tooltip content="查看标题" placement="top">
                  <el-button link type="primary" icon="List" @click="handleViewTitles(scope.row)" />
                </el-tooltip>
                <el-tooltip content="修改" placement="top">
                  <el-button link type="primary" icon="Edit" @click="handleUpdateBatch(scope.row)" />
                </el-tooltip>
                <el-tooltip content="删除" placement="top">
                  <el-button link type="danger" icon="Delete" @click="handleDeleteBatch(scope.row)" />
                </el-tooltip>
              </template>
              <!-- default: 默认配置 - 可排除/恢复 -->
              <template v-else-if="scope.row.relationSource === 'default'">
                <el-tooltip content="查看标题" placement="top">
                  <el-button link type="primary" icon="List" @click="handleViewTitles(scope.row)" />
                </el-tooltip>
                <el-tag v-if="scope.row.isExcluded === '1'" type="info" size="small">已排除</el-tag>
              </template>
              <!-- shared: 其他网站分享 - 可移除关联 -->
              <template v-else-if="scope.row.relationSource === 'shared'">
                <el-tooltip content="查看标题" placement="top">
                  <el-button link type="primary" icon="List" @click="handleViewTitles(scope.row)" />
                </el-tooltip>
              </template>
            </template>
            <!-- 创建者模式下的操作 -->
            <template v-else>
              <el-tooltip content="查看标题" placement="top">
                <el-button link type="primary" icon="List" @click="handleViewTitles(scope.row)" />
              </el-tooltip>
              <el-tooltip content="修改" placement="top">
                <el-button link type="primary" icon="Edit" @click="handleUpdateBatch(scope.row)" />
              </el-tooltip>
              <!-- 默认配置：管理排除网站 -->
              <el-tooltip v-if="isPersonalSiteCheck(scope.row.siteId)" content="排除管理" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="CircleClose" 
                  @click="handleManageSites(scope.row)" />
              </el-tooltip>
              <!-- 非默认配置：网站关联 -->
              <el-tooltip v-else content="网站关联" placement="top">
                <el-button 
                  link 
                  type="warning" 
                  icon="Link" 
                  @click="handleManageSites(scope.row)" />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link type="danger" icon="Delete" @click="handleDeleteBatch(scope.row)" />
              </el-tooltip>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="batchTotal > 0" :total="batchTotal" v-model:page="batchQueryParams.pageNum" v-model:limit="batchQueryParams.pageSize" @pagination="getBatchList" />
    
      <!-- 网站关联管理对话框 -->
      <SiteRelationManager
        v-model="siteRelationDialogOpen"
        entity-type="titleBatch"
        :entity-id="currentBatchIdForSites"
        :entity-name="currentBatchNameForSites"
        :creator-site-id="currentBatchCreatorSiteId"
        @refresh="getBatchList"
      />
    </div>

    <!-- 标题列表视图（在批次内） -->
    <div v-else>
      <el-page-header @back="handleBackToBatchList" style="margin-bottom: 20px">
        <template #content>
          <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 16px; font-weight: bold;">{{ currentBatch.batchName }}</span>
            <el-tag type="info">{{ currentBatch.batchCode }}</el-tag>
            <CategoryTag v-if="currentBatch.categoryId" :category="{ id: currentBatch.categoryId, name: currentBatch.categoryName, categoryType: 'title_pool', icon: currentBatch.categoryIcon }" />
          </div>
        </template>
      </el-page-header>

      <el-form :model="titleQueryParams" ref="titleQueryRef" :inline="true" v-show="showSearch">
        <el-form-item label="标题" prop="title">
          <el-input v-model="titleQueryParams.title" placeholder="请输入标题" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="使用状态" prop="usageStatus">
          <el-select v-model="titleQueryParams.usageStatus" placeholder="请选择使用状态" clearable style="width: 150px">
            <el-option label="未使用" value="0" />
            <el-option label="已使用" value="1" />
            <el-option label="已废弃" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd">添加标题</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getTitleList"></right-toolbar>
      </el-row>

      <el-table v-loading="titleLoading" :data="titleList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="80" />
        <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" min-width="250" />
        <el-table-column label="关键词" align="center" prop="keywords" :show-overflow-tooltip="true" width="180" />
        <el-table-column label="来源" align="center" prop="sourceName" width="120" />
        <el-table-column label="优先级" align="center" prop="priority" width="80" />
        <el-table-column label="使用状态" align="center" prop="usageStatus" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.usageStatus === '0'" type="success">未使用</el-tag>
            <el-tag v-else-if="scope.row.usageStatus === '1'" type="info">已使用</el-tag>
            <el-tag v-else-if="scope.row.usageStatus === '2'" type="danger">已废弃</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="使用次数" align="center" prop="usedCount" width="100" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
          <template #default="scope">
            <el-tooltip content="查看" placement="top">
              <el-button link type="primary" icon="View" @click="handleView(scope.row)" />
            </el-tooltip>
            <el-tooltip content="编辑" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="titleTotal > 0" :total="titleTotal" v-model:page="titleQueryParams.pageNum" v-model:limit="titleQueryParams.pageSize" @pagination="getTitleList" />
    </div>

    <!-- 批次编辑对话框 -->
    <el-dialog :title="batchTitle" v-model="batchOpen" width="600px" append-to-body>
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="所属网站" prop="siteId" v-if="!batchForm.id">
          <SiteSelect v-model="batchForm.siteId" :site-list="siteList" show-default default-label="默认配置（全局）" width="100%" placeholder="请选择所属网站" />
        </el-form-item>
        <el-form-item label="所属网站" v-else>
          <el-input :value="getSiteName(batchForm.siteId)" disabled />
        </el-form-item>
        <el-form-item label="批次名称" prop="batchName">
          <el-input v-model="batchForm.batchName" placeholder="请输入批次名称" />
        </el-form-item>
        <el-form-item label="批次分类" prop="categoryId">
          <el-select v-model="batchForm.categoryId" placeholder="请选择批次分类" clearable style="width: 100%">
            <el-option 
              v-for="category in titleCategories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id"
            >
              <span v-if="category.icon" style="margin-right: 8px;">{{ category.icon }}</span>
              <span>{{ category.name }}</span>
            </el-option>
          </el-select>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            整个批次的所有标题将使用此分类
          </div>
        </el-form-item>
        <el-form-item label="来源名称" prop="importSource">
          <el-input v-model="batchForm.importSource" placeholder="请输入来源名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="batchForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitBatchForm">确 定</el-button>
          <el-button @click="cancelBatch">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加/编辑标题对话框 -->
    <el-dialog :title="titleDialogTitle" v-model="titleOpen" width="700px" append-to-body>
      <el-alert
        title="提示：标题的分类、网站等信息由所属批次统一管理"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 15px">
      </el-alert>
      <el-form ref="titleFormRef" :model="titleForm" :rules="titleRules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="标题" prop="title">
              <el-input v-model="titleForm.title" placeholder="请输入标题" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="关键词" prop="keywords">
              <el-input v-model="titleForm.keywords" placeholder="多个关键词用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源名称" prop="sourceName">
              <el-input v-model="titleForm.sourceName" placeholder="请输入来源名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-input-number v-model="titleForm.priority" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用状态" prop="usageStatus">
              <el-select v-model="titleForm.usageStatus" placeholder="请选择使用状态" style="width: 100%">
                <el-option label="未使用" value="0" />
                <el-option label="已使用" value="1" />
                <el-option label="已废弃" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="参考内容" prop="referenceContent">
              <el-input v-model="titleForm.referenceContent" type="textarea" :rows="4" placeholder="请输入参考内容" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="titleForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitTitleForm">确 定</el-button>
          <el-button @click="cancelTitle">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导出对话框 -->
    <el-dialog title="批次数据导出" v-model="exportDialogOpen" width="500px" append-to-body>
      <el-alert
        title="导出说明"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      >
        <template #default>
          <p>• 导出选中批次的数据（包含所有标题）</p>
          <p>• 数据分为两个表：批次信息和标题列表</p>
          <p>• 使用虚拟ID关联批次和标题</p>
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
          <el-tag type="info">{{ batchIds.length }} 个批次</el-tag>
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
          <p>• 全站导出将包含所有标题池批次数据（默认配置 + 所有站点）</p>
          <p>• 包含所有批次的标题和网站关联关系</p>
          <p>• 导出格式：网站列表、批次数据、标题数据、网站关联</p>
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

    <!-- 全站导入对话框 -->
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
        <p>• 将批次和标题数据导入到当前选择的创建者网站</p>
        <p>• 导入时会自动创建批次和关联标题</p>
        <p>• 必填字段：批次名称、标题内容</p>
        <el-form :model="systemImportForm" label-width="100px" style="margin-top: 15px;">
          <el-form-item label="创建者网站" prop="siteId">
            <SiteSelect v-model="systemImportForm.siteId" :site-list="siteList" show-default default-label="默认配置（全局）" filterable width="100%" />
          </el-form-item>
        </el-form>
      </template>
      <template #previewColumns>
        <el-table-column prop="batchName" label="批次名称" width="200" show-overflow-tooltip />
        <el-table-column prop="titleCount" label="标题数量" width="100" align="center" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'info'" size="small">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </template>
    </ImportDialog>

    <!-- 导入数据对话框 -->
    <el-dialog title="导入标题数据" v-model="showImportDialog" width="700px" append-to-body>
      <el-form label-width="120px">
        <el-form-item label="所属网站" required>
          <SiteSelect v-model="importForm.siteId" :site-list="siteList" show-default default-label="默认配置（全局）" width="100%" placeholder="请选择所属网站" />
        </el-form-item>
        <el-form-item label="批次分类" required>
          <el-select v-model="importForm.categoryId" placeholder="请选择批次分类" clearable style="width: 100%">
            <el-option 
              v-for="category in titleCategories" 
              :key="category.id" 
              :label="category.name" 
              :value="category.id"
            >
              <span v-if="category.icon" style="margin-right: 8px;">{{ category.icon }}</span>
              <span>{{ category.name }}</span>
            </el-option>
          </el-select>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            提示：整个批次的标题将统一使用此分类
          </div>
        </el-form-item>
        <el-form-item label="批次名称">
          <el-input v-model="importForm.batchName" placeholder="可选，留空将自动生成" style="width: 100%" />
        </el-form-item>
      </el-form>
      <el-divider />
      <el-tabs v-model="importType">
        <el-tab-pane label="JSON导入" name="json">
          <el-form label-width="100px">
            <el-form-item label="JSON数据">
              <el-input v-model="jsonData" type="textarea" :rows="10" placeholder='请输入JSON格式数据，例如：
[
  {
    "title": "游戏攻略标题",
    "keywords": "游戏,攻略",
    "referenceContent": "参考内容",
    "sourceName": "来源名称",
    "priority": 5
  }
]' />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="Excel导入" name="excel">
          <el-form label-width="100px">
            <el-form-item label="Excel文件">
              <el-upload
                ref="uploadRef"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :data="importForm"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :before-upload="beforeUpload"
                :limit="1"
                :file-list="fileList"
                accept=".xlsx,.xls"
              >
                <el-button type="primary">选择文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">只能上传xlsx/xls文件，且不超过10MB</div>
                </template>
              </el-upload>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleImport" v-if="importType === 'json'">确认导入</el-button>
          <el-button @click="showImportDialog = false">取 消</el-button>
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选批次）</p>
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
          <strong>已选批次：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedBatchesForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="batch in selectedBatchesForBatchExclude" 
            :key="batch.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeBatchFromBatchExclude(batch.id)"
            size="small"
          >
            {{ batch.batchName }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各批次的当前排除/关联状态 -->
      <el-collapse v-if="batchExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各批次当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="batchExclusionDetails" size="small" stripe>
              <el-table-column label="批次名称" prop="batchName" width="150" show-overflow-tooltip />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选批次对该网站可见）</p>
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
          <strong>已选批次：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedBatchesForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="batch in selectedBatchesForBatchRelation" 
            :key="batch.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeBatchFromBatchRelation(batch.id)"
            size="small"
          >
            {{ batch.batchName }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各批次的当前关联/排除状态 -->
      <el-collapse v-if="batchRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各批次当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="batchRelationDetails" size="small" stripe>
              <el-table-column label="批次名称" prop="batchName" width="150" show-overflow-tooltip />
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
            v-for="site in siteList.filter(s => s.id !== batchQueryParams.siteId && s.isPersonal !== 1)"
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
  </div>
</template>

<script setup name="TitlePool">
import { listTitlePool, getTitlePool, addTitlePool, updateTitlePool, delTitlePool, importFromJson } from "@/api/gamebox/titlePool"
import { listBatch, getBatch, addBatch, updateBatch, delBatch } from "@/api/gamebox/titleBatch"
import { getTitleBatchSites, getBatchTitleBatchSites, batchSaveTitleBatchSiteRelations, updateTitleBatchVisibility } from "@/api/gamebox/siteRelation"
import { useSiteSelection, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"
import CategoryTag from "@/components/CategoryTag/index.vue"
import SiteRelationManager from '@/components/SiteRelationManager/index.vue'
import ImportDialog from "@/components/ImportExportDialogs/ImportDialog.vue"
import FullImportDialog from "@/components/ImportExportDialogs/FullImportDialog.vue"
import { listVisibleCategory } from "@/api/gamebox/category"
import { getToken } from '@/utils/auth'

const { proxy } = getCurrentInstance()

// 使用网站选择组合式函数
const { siteList, currentSiteId, loadSiteList, getSiteName } = useSiteSelection()
const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

// ========== 批次相关 ==========
const currentBatch = ref(null) // 当前查看的批次
const batchList = ref([])
const batchLoading = ref(false)
const batchTotal = ref(0)
const batchOpen = ref(false)
const batchTitle = ref("")
const batchIds = ref([])
const batchMultiple = ref(true)
const batchQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  siteId: undefined,
  categoryId: undefined,
  batchName: undefined
})
const batchForm = ref({})
const batchRules = ref({
  batchName: [{ required: true, message: "批次名称不能为空", trigger: "blur" }],
  siteId: [{ required: true, message: "请选择所属网站", trigger: "change" }]
})

// ========== 标题相关 ==========
const titleList = ref([])
const titleLoading = ref(false)
const titleTotal = ref(0)
const titleOpen = ref(false)
const titleDialogTitle = ref("")
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const titleQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  title: undefined,
  usageStatus: undefined
})
const titleForm = ref({})
const titleRules = ref({
  title: [{ required: true, message: "标题不能为空", trigger: "blur" }]
})

// ========== 公共相关 ==========
const titleCategories = ref([]) // 标题池分类列表
const includeDefaultConfig = ref(false) // 是否包含默认配置
const viewMode = ref('creator') // 查看模式：creator-创建者, related-关联网站
const showSearch = ref(true)
const showImportDialog = ref(false)

// ========== 网站关联管理相关 ==========
const siteRelationDialogOpen = ref(false)
const currentBatchIdForSites = ref(0)
const currentBatchNameForSites = ref('')
const currentBatchCreatorSiteId = ref(null)
const importType = ref("json")
const jsonData = ref("")
const fileList = ref([])
const importForm = ref({
  siteId: undefined,
  categoryId: undefined,
  batchName: undefined
})
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/gamebox/title/import/excel")
const uploadHeaders = ref({ Authorization: "Bearer " + getToken() })

// 导出相关
const exportDialogOpen = ref(false)
const exportFormat = ref('excel')
const exportLoading = ref(false)

// 全站导出相关
const fullExportDialogOpen = ref(false)
const fullExportFormat = ref('excel')
const fullExportLoading = ref(false)

// 系统导入相关
const systemImportDialogOpen = ref(false)
const systemImportLoading = ref(false)
const systemImportPreviewData = ref([])
const systemImportFile = ref(null)
const systemImportForm = ref({
  siteId: undefined
})
const fullImportBatches = ref([])
const fullImportRelations = ref([])
const fullImportFile = ref(null)
const siteMapping = ref({})

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const selectedBatchesForBatchExclude = ref([])
const batchExclusionDetails = ref([])
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const batchTableRef = ref(null)

// 冲突网站 ID 集合（存在 include 关联的网站，exclude 对其无效）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  batchExclusionDetails.value.forEach(detail => {
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
const selectedBatchesForBatchRelation = ref([])
const batchRelationDetails = ref([])

// 是否处于默认配置的批量关联管理
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(batchQueryParams.value.siteId, siteList.value))

// 冲突网站 ID 集合（存在 exclude 关系的网站）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  batchRelationDetails.value.forEach(detail => {
    if (detail.excludedSiteIds) {
      detail.excludedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

/** ========== 批次管理 ========== */

/** 获取批次列表 */
function getBatchList() {
  // 关联模式下必须有有效的siteId
  if (viewMode.value === 'related' && (!batchQueryParams.value.siteId || isPersonalSite(batchQueryParams.value.siteId, siteList.value))) {
    console.warn('关联模式需要有效的siteId')
    return
  }
  
  batchLoading.value = true
  const params = { 
    ...batchQueryParams.value,
    queryMode: viewMode.value,
    includeDefault: includeDefaultConfig.value
  }
  
  listBatch(params).then(response => {
    batchList.value = response.rows
    batchTotal.value = response.total
    batchLoading.value = false
  }).catch(() => {
    batchLoading.value = false
  })
}

/** 批次选择变化 */
function handleBatchSelectionChange(selection) {
  batchIds.value = selection.map(item => item.id)
  batchMultiple.value = !selection.length
}

/** 添加批次 */
function handleAddBatch() {
  resetBatch()
  batchForm.value.siteId = batchQueryParams.value.siteId || (siteList.value.length > 0 ? siteList.value[0].id : 0)
  loadTitleCategoriesForDialog(batchForm.value.siteId)
  batchOpen.value = true
  batchTitle.value = "创建批次"
}

/** 编辑批次 */
function handleUpdateBatch(row) {
  resetBatch()
  const id = row.id || batchIds.value[0]
  getBatch(id).then(response => {
    batchForm.value = response.data
    loadTitleCategoriesForDialog(batchForm.value.siteId)
    batchOpen.value = true
    batchTitle.value = "编辑批次"
  })
}

/** 查看批次下的标题 */
function handleViewTitles(row) {
  currentBatch.value = row
  titleQueryParams.value.importBatch = row.batchCode
  getTitleList()
}

/** 返回批次列表 */
function handleBackToBatchList() {
  currentBatch.value = null
  titleQueryParams.value.importBatch = undefined
  titleList.value = []
  getBatchList()
}

/** 删除批次 */
function handleDeleteBatch(row) {
  const batchIdList = row.id ? [row.id] : batchIds.value
  proxy.$modal.confirm('删除批次将同时删除批次下的所有标题，是否确认删除？').then(() => {
    return delBatch(batchIdList)
  }).then(() => {
    getBatchList()
    proxy.$modal.msgSuccess("删除成功")
  })
}

/** 提交批次表单 */
function submitBatchForm() {
  proxy.$refs.batchFormRef.validate(valid => {
    if (valid) {
      if (batchForm.value.id) {
        updateBatch(batchForm.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          batchOpen.value = false
          getBatchList()
        })
      } else {
        addBatch(batchForm.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          batchOpen.value = false
          getBatchList()
        })
      }
    }
  })
}

/** 取消批次表单 */
function cancelBatch() {
  batchOpen.value = false
  resetBatch()
}

/** 重置批次表单 */
function resetBatch() {
  batchForm.value = {
    id: undefined,
    siteId: undefined,
    categoryId: undefined,
    batchName: undefined,
    importSource: undefined,
    remark: undefined
  }
  proxy.resetForm("batchFormRef")
}

/** ========== 标题管理 ========== */

/** 获取标题列表 */
function getTitleList() {
  if (!currentBatch.value) return
  
  titleLoading.value = true
  const params = { 
    ...titleQueryParams.value,
    importBatch: currentBatch.value.batchCode
  }
  
  listTitlePool(params).then(response => {
    titleList.value = response.rows
    titleTotal.value = response.total
    titleLoading.value = false
  }).catch(() => {
    titleLoading.value = false
  })
}

/** 标题选择变化 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 添加标题 */
function handleAdd() {
  resetTitle()
  titleForm.value.importBatch = currentBatch.value.batchCode
  titleOpen.value = true
  titleDialogTitle.value = "添加标题"
}

/** 编辑标题 */
function handleUpdate(row) {
  resetTitle()
  const id = row.id || ids.value[0]
  getTitlePool(id).then(response => {
    titleForm.value = response.data
    titleOpen.value = true
    titleDialogTitle.value = "修改标题"
  })
}

/** 查看标题 */
function handleView(row) {
  resetTitle()
  getTitlePool(row.id).then(response => {
    titleForm.value = response.data
    titleOpen.value = true
    titleDialogTitle.value = "查看标题详情"
  })
}

/** 删除标题 */
function handleDelete(row) {
  const titleIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除标题编号为"' + titleIds + '"的数据项？').then(() => {
    return delTitlePool(titleIds)
  }).then(() => {
    getTitleList()
    // 刷新批次列表以更新标题数量
    getBatchList()
    proxy.$modal.msgSuccess("删除成功")
  })
}

/** 提交标题表单 */
function submitTitleForm() {
  proxy.$refs.titleFormRef.validate(valid => {
    if (valid) {
      if (titleForm.value.id) {
        updateTitlePool(titleForm.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          titleOpen.value = false
          getTitleList()
        })
      } else {
        addTitlePool(titleForm.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          titleOpen.value = false
          getTitleList()
          // 刷新批次列表以更新标题数量
          getBatchList()
        })
      }
    }
  })
}

/** 取消标题表单 */
function cancelTitle() {
  titleOpen.value = false
  resetTitle()
}

/** 重置标题表单 */
function resetTitle() {
  titleForm.value = {
    id: undefined,
    title: undefined,
    keywords: undefined,
    referenceContent: undefined,
    sourceName: undefined,
    priority: 0,
    usageStatus: "0",
    remark: undefined,
    importBatch: currentBatch.value?.batchCode
  }
  proxy.resetForm("titleFormRef")
}

/** ========== 分类管理 ========== */

/** 加载标题池分类列表 */
async function loadTitleCategories() {
  try {
    const response = await listVisibleCategory({
      categoryType: 'title_pool',
      siteId: batchQueryParams.value.siteId || personalSiteId.value,
      status: '1'
    })
    titleCategories.value = response.rows || []
  } catch (error) {
    console.error('加载标题池分类失败:', error)
    titleCategories.value = []
  }
}

/** 加载对话框中指定站点的分类列表 */
async function loadTitleCategoriesForDialog(siteId) {
  if (siteId === undefined) return
  try {
    const response = await listVisibleCategory({
      categoryType: 'title_pool',
      siteId: siteId || personalSiteId.value,
      status: '1'
    })
    titleCategories.value = response.rows || []
  } catch (error) {
    console.error('加载标题池分类失败:', error)
    titleCategories.value = []
  }
}

/** ========== 公共操作 ========== */

/** 网站切换事件 */
function handleSiteChange() {
  // 切换网站时重置"含默认配置"选项
  if (!batchQueryParams.value.siteId || isPersonalSite(batchQueryParams.value.siteId, siteList.value)) {
    includeDefaultConfig.value = false
  }
  batchQueryParams.value.pageNum = 1
  saveViewModeSiteSelection(viewMode.value, batchQueryParams.value.siteId)
  // 重新加载分类列表
  loadTitleCategories()
  getBatchList()
}

/** 查看模式切换事件 */
function handleViewModeChange() {
  const resolvedSiteId = resolveSiteIdByViewMode(viewMode.value, siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  if (viewMode.value === 'related' && !resolvedSiteId) {
    proxy.$modal.msgWarning("关联模式需要至少一个真实网站，请先创建网站")
    viewMode.value = 'creator'
    batchQueryParams.value.siteId = resolveSiteIdByViewMode('creator', siteList.value, {
      creatorFallbackSiteId: personalSiteId.value
    })
  } else {
    batchQueryParams.value.siteId = resolvedSiteId
  }
  // 重置"含默认配置"选项
  includeDefaultConfig.value = false
  saveViewModeSiteSelection(viewMode.value, batchQueryParams.value.siteId)
  // 重新加载分类列表
  loadTitleCategories()
  // 切换模式时重新查询
  batchQueryParams.value.pageNum = 1
  getBatchList()
}

/** 查询 */
function handleQuery() {
  if (currentBatch.value) {
    titleQueryParams.value.pageNum = 1
    getTitleList()
  } else {
    batchQueryParams.value.pageNum = 1
    getBatchList()
  }
}

/** 重置查询 */
function resetQuery() {
  if (currentBatch.value) {
    proxy.resetForm("titleQueryRef")
    getTitleList()
  } else {
    proxy.resetForm("queryRef")
    getBatchList()
  }
}

/** ========== 导入功能 ========== */

/** 导入JSON */
function handleImport() {
  if (importForm.value.siteId === undefined) {
    proxy.$modal.msgError("请选择所属网站")
    return
  }
  if (!importForm.value.categoryId) {
    proxy.$modal.msgError("请选择批次分类")
    return
  }
  if (!jsonData.value) {
    proxy.$modal.msgError("请输入JSON数据")
    return
  }
  
  try {
    JSON.parse(jsonData.value)
    
    const params = {
      jsonData: jsonData.value,
      siteId: importForm.value.siteId,
      categoryId: importForm.value.categoryId,
      batchName: importForm.value.batchName || undefined,
      importSource: "手动导入"
    }
    
    importFromJson(params).then(response => {
      proxy.$modal.msgSuccess(response.msg || "导入成功")
      showImportDialog.value = false
      jsonData.value = ""
      importForm.value = { siteId: undefined, categoryId: undefined, batchName: undefined }
      getBatchList()
    })
  } catch (e) {
    proxy.$modal.msgError("JSON格式错误：" + e.message)
  }
}

/** 上传前验证 */
function beforeUpload(file) {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || file.type === 'application/vnd.ms-excel'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    proxy.$modal.msgError('只能上传Excel文件!')
    return false
  }
  if (!isLt10M) {
    proxy.$modal.msgError('文件大小不能超过 10MB!')
    return false
  }
  
  // 验证必填参数
  if (importForm.value.siteId === undefined) {
    proxy.$modal.msgError("请先选择所属网站")
    return false
  }
  if (!importForm.value.categoryId) {
    proxy.$modal.msgError("请先选择批次分类")
    return false
  }
  
  return true
}

/** 上传成功 */
function handleUploadSuccess(response) {
  proxy.$modal.msgSuccess(response.msg || "导入成功")
  showImportDialog.value = false
  fileList.value = []
  importForm.value = { siteId: undefined, categoryId: undefined, batchName: undefined }
  getBatchList()
}

/** 上传失败 */
function handleUploadError() {
  proxy.$modal.msgError("导入失败")
}

/** ========== 排除管理 ========== */

/** 管理关联网站 */
function handleManageSites(row) {
  currentBatchIdForSites.value = row.id
  currentBatchNameForSites.value = row.batchName || '标题池批次'
  currentBatchCreatorSiteId.value = row.siteId || personalSiteId.value
  siteRelationDialogOpen.value = true
}

/** 获取可见性值 */
function getVisibilityValue(row) {
  if (row.relationSource === 'default') {
    // 默认配置：使用 isVisible 判断是否被排除
    return row.isVisible || '1'
  } else if (row.relationSource === 'own') {
    // 自有数据：使用 status 字段（标题池批次使用status）
    return row.status || '1'
  } else if (row.relationSource === 'shared') {
    // 共享数据：使用 isVisible 状态
    return row.isVisible || '1'
  }
  return '0'
}

/** 切换可见性（统一入口）*/
async function handleToggleVisibility(row) {
  const currentQuerySiteId = batchQueryParams.value.siteId
  const currentValue = getVisibilityValue(row)
  const newValue = currentValue === '1' ? '0' : '1'
  
  try {
    if (row.relationSource === 'default') {
      // 默认配置：根据是否有关联记录决定操作方式
      const relationResponse = await getTitleBatchSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段
        await updateTitleBatchVisibility(currentQuerySiteId, row.id, newValue)
        const action = newValue === '1' ? '显示' : '隐藏'
        proxy.$modal.msgSuccess(`${action}成功`)
        getBatchList()
      } else {
        // 没有关联记录：使用排除逻辑
        const included = relations.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
        let excluded = relations.filter(s => s.relationType === 'exclude').map(s => s.siteId)
        if (newValue === '0') {
          if (!excluded.includes(currentQuerySiteId)) excluded.push(currentQuerySiteId)
          proxy.$modal.msgSuccess('已排除该批次')
        } else {
          excluded = excluded.filter(id => id !== currentQuerySiteId)
          proxy.$modal.msgSuccess('已恢复该批次')
        }
        await batchSaveTitleBatchSiteRelations({ batchIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
        getBatchList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用/禁用状态
      const text = newValue === '1' ? '启用' : '禁用'
      await updateBatch({ id: row.id, status: newValue })
      row.status = newValue
      row.isVisible = newValue
      proxy.$modal.msgSuccess(`${text}成功`)
      getBatchList()
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updateTitleBatchVisibility(currentQuerySiteId, row.id, newValue)
      const action = newValue === '1' ? '显示' : '隐藏'
      proxy.$modal.msgSuccess(`${action}成功`)
      getBatchList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换可见性失败:', error)
      proxy.$modal.msgError('操作失败')
    }
  }
}

/** 快速切换可见性（关联模式下的开关） */
async function handleQuickToggleVisibility(row) {
  const currentQuerySiteId = batchQueryParams.value.siteId
  const newVisibility = row.isVisible === '1' ? '0' : '1'
  const action = newVisibility === '1' ? '显示' : '隐藏'
  
  updateTitleBatchVisibility(currentQuerySiteId, row.id, newVisibility).then(() => {
    row.isVisible = newVisibility
    proxy.$modal.msgSuccess(`${action}成功`)
  }).catch(() => {
    proxy.$modal.msgError('更新失败')
  })
}

/** 排除默认配置（关联模式下） */
function handleExclude(row) {
  const currentQuerySiteId = batchQueryParams.value.siteId
  proxy.$modal.confirm(`确认要排除批次"${row.batchName}"吗？排除后该批次将不会在当前网站显示。`).then(async () => {
    const res = await getTitleBatchSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excluded.includes(currentQuerySiteId)) excluded.push(currentQuerySiteId)
    return batchSaveTitleBatchSiteRelations({ batchIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess("排除成功")
    getBatchList()
  })
}

/** 恢复默认配置（关联模式下） */
function handleRestore(row) {
  const currentQuerySiteId = batchQueryParams.value.siteId
  proxy.$modal.confirm(`确认要恢复批次"${row.batchName}"吗？恢复后该批次将重新在当前网站显示。`).then(async () => {
    const res = await getTitleBatchSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== currentQuerySiteId)
    return batchSaveTitleBatchSiteRelations({ batchIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess("恢复成功")
    getBatchList()
  })
}

/** 移除关联（关联模式下，移除共享批次） */
function handleRemoveFromSite(row) {
  const currentQuerySiteId = batchQueryParams.value.siteId
  const actionLabel = row.relationType === 'exclude' ? '取消排除' : '移除关联'
  const actionMessage = row.relationType === 'exclude'
    ? `确认要取消排除批次"${row.batchName}"吗？取消后该批次将对当前网站可见。`
    : `确认要移除批次"${row.batchName}"的关联吗？移除后该批次将不再在当前网站显示。`

  proxy.$modal.confirm(actionMessage).then(async () => {
    const res = await getTitleBatchSites(row.id)
    const sites = res.data || []
    let included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    let excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (row.relationType === 'exclude') {
      excluded = excluded.filter(id => id !== currentQuerySiteId)
    } else {
      included = included.filter(id => id !== currentQuerySiteId)
    }
    return batchSaveTitleBatchSiteRelations({ batchIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
  }).then(() => {
    proxy.$modal.msgSuccess(actionLabel + '成功')
    getBatchList()
  })
}

// ========== 导出和导入功能 ==========

/** 打开导出对话框 */
function handleExport() {
  if (batchIds.value.length === 0) {
    proxy.$modal.msgError('请先选择要导出的批次')
    return
  }
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  const selectedIds = batchIds.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgError('请选择要导出的批次')
    return
  }
  
  exportLoading.value = true
  
  try {
    // 获取选中的批次详细数据
    const batchPromises = selectedIds.map(id => getBatch(id))
    const batchResponses = await Promise.all(batchPromises)
    const batchData = batchResponses.map(response => response.data)
    
    // 获取所有批次的标题数据
    const titlesPromises = selectedIds.map(batchId => 
      listTitlePool({ batchId, pageSize: 10000 })
    )
    const titlesResponses = await Promise.all(titlesPromises)
    
    // 构建批次数据（使用虚拟ID）
    const batches = []
    const titles = []
    let batchVirtualId = 1
    
    for (let i = 0; i < batchData.length; i++) {
      const batch = batchData[i]
      const batchTitles = titlesResponses[i].rows || []
      
      batches.push({
        '批次虚拟ID': batchVirtualId,
        '批次编号': batch.batchCode,
        '批次名称': batch.batchName,
        '网站编码': getSiteCode(batch.siteId),
        '导入来源': batch.importSource || '',
        '导入日期': batch.importDate || '',
        '分类名称': batch.categoryName || '',
        '标题数量': batch.titleCount || 0,
        '状态': batch.status === '1' ? '启用' : '禁用',
        '备注': batch.remark || ''
      })
      
      // 添加该批次的所有标题
      batchTitles.forEach(title => {
        titles.push({
          '批次虚拟ID': batchVirtualId,
          '标题内容': title.title,
          '标题语言': title.locale || 'zh-CN',
          '标题类型': title.titleType || 'standard',
          '排序': title.sortOrder || 0
        })
      })
      
      batchVirtualId++
    }
    
    if (exportFormat.value === 'excel') {
      await exportBatchesToExcel(batches, titles)
    } else {
      exportBatchesToJSON(batches, titles)
    }
    
    proxy.$modal.msgSuccess('数据导出成功')
    exportDialogOpen.value = false
  } catch (error) {
    console.error('导出失败:', error)
    proxy.$modal.msgError('导出失败: ' + (error.message || '未知错误'))
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
    
    // 1. 获取所有批次数据
    const batchResponse = await listBatch({ 
      pageNum: 1, 
      pageSize: 9999 
    })
    const batchData = batchResponse.rows || []
    
    // 2. 收集所有网站ID
    const siteIds = new Set()
    batchData.forEach(batch => {
      if (batch.siteId !== null && batch.siteId !== undefined) {
        siteIds.add(batch.siteId)
      }
    })
    
    // 3. 获取所有关联关系和批次标题
    const { getTitleBatchSites } = await import('@/api/gamebox/siteRelation')
    const relationDataRaw = []
    const allTitles = []
    
    for (let index = 0; index < batchData.length; index++) {
      const batch = batchData[index]
      const virtualId = index + 1
      
      // 获取批次的所有标题
      try {
        const titlesResponse = await listTitlePool({ batchId: batch.id, pageSize: 10000 })
        const titles = titlesResponse.rows || []
        titles.forEach(title => {
          allTitles.push({
            batchVirtualId: virtualId,
            batchName: batch.batchName,
            title: title.title,
            locale: title.locale || 'zh-CN',
            titleType: title.titleType || 'standard',
            sortOrder: title.sortOrder || 0
          })
        })
      } catch (error) {
        console.warn('获取批次标题失败:', batch.id, error)
      }
      
      // 获取批次的所有网站关联
      try {
        const response = await getTitleBatchSites(batch.id)
        const relations = response.data || []
        if (relations.length > 0) {
          relations.forEach(rel => {
            siteIds.add(rel.siteId)
            const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
            const relationData = {
              批次虚拟ID: virtualId,
              批次名称: batch.batchName,
              关联类型: relationType,
              网站ID_原始: rel.siteId,
              网站编码: getSiteCode(rel.siteId)
            }
            if (rel.relationType !== 'exclude') {
              relationData.可见性 = rel.isVisible === '1' ? '显示' : '隐藏'
            }
            relationDataRaw.push(relationData)
          })
        }
      } catch (error) {
        console.warn('获取批次关联失败:', batch.id, error)
      }
    }
    
    // 4. 获取网站详细信息并建立ID映射
    const sitesData = []
    const siteIdToVirtualIdMap = new Map()
    let realSiteIndex = 0
    
    const _personalSiteId = getPersonalSiteId(siteList.value)
    if (_personalSiteId) siteIdToVirtualIdMap.set(_personalSiteId, 0)
    
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
    
    // 5. 转换关联关系
    const relationData = relationDataRaw.map(rel => ({
      批次虚拟ID: rel.批次虚拟ID,
      批次名称: rel.批次名称,
      关联类型: rel.关联类型,
      网站虚拟ID: siteIdToVirtualIdMap.get(rel.网站ID_原始) ?? 0,
      网站编码: rel.网站编码,
      可见性: rel.可见性
    }))
    
    // 6. 转换批次数据格式
    const formattedBatchData = batchData.map((batch, index) => ({
      批次虚拟ID: index + 1,
      批次编号: batch.batchCode,
      批次名称: batch.batchName,
      网站虚拟ID: siteIdToVirtualIdMap.get(batch.siteId) ?? 0,
      网站编码: getSiteCode(batch.siteId),
      导入来源: batch.importSource || '',
      导入日期: batch.importDate || '',
      分类名称: batch.categoryName || '',
      标题数量: batch.titleCount || 0,
      状态: batch.status === '1' ? '启用' : '禁用',
      备注: batch.remark || ''
    }))
    
    // 7. 转换标题数据格式
    const formattedTitles = allTitles.map(title => ({
      批次虚拟ID: title.batchVirtualId,
      标题内容: title.title,
      标题语言: title.locale,
      标题类型: title.titleType,
      排序: title.sortOrder
    }))
    
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, formattedBatchData, formattedTitles, relationData)
    } else {
      exportFullDataToJSON(sitesData, formattedBatchData, formattedTitles, relationData)
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

/** 系统导入 */
function handleSystemImport() {
  systemImportDialogOpen.value = true
  systemImportPreviewData.value = []
  systemImportFile.value = null
  systemImportForm.value.siteId = batchQueryParams.value.siteId !== undefined ? batchQueryParams.value.siteId : personalSiteId.value
}

/** 处理系统导入文件选择 */
async function handleSystemImportFileChange(file) {
  systemImportFile.value = file
  
  try {
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.raw.type.includes('json')) {
      parsedData = parseSystemImportJSONData(fileData)
    } else {
      parsedData = await parseSystemImportExcelData(fileData)
    }
    
    systemImportPreviewData.value = validateAndTransformSystemImportData(parsedData.batches || [])
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + error.message)
    systemImportPreviewData.value = []
  }
}

/** 确认系统导入 */
async function confirmSystemImport() {
  if (systemImportPreviewData.value.length === 0) {
    proxy.$modal.msgError('没有可导入的数据')
    return
  }
  
  if (systemImportForm.value.siteId === undefined || systemImportForm.value.siteId === null) {
    proxy.$modal.msgError('请选择创建者网站')
    return
  }
  
  try {
    systemImportLoading.value = true
    
    // 解析导入的批次和标题数据
    const fileData = await readFileData(systemImportFile.value.raw)
    let parsedData
    
    if (systemImportFile.value.raw.type.includes('json')) {
      parsedData = parseSystemImportJSONData(fileData)
    } else {
      parsedData = await parseSystemImportExcelData(fileData)
    }
    
    const batchesData = parsedData.batches || []
    const titlesData = parsedData.titles || []
    
    // 批量导入批次和标题
    let successCount = 0
    for (const batchItem of batchesData) {
      try {
        // 创建批次
        const batchData = {
          batchName: batchItem['批次名称'] || batchItem['batchName'],
          batchCode: batchItem['批次编号'] || batchItem['batchCode'] || '',
          siteId: systemImportForm.value.siteId,
          importSource: batchItem['导入来源'] || batchItem['importSource'] || '',
          importDate: batchItem['导入日期'] || batchItem['importDate'] || '',
          categoryName: batchItem['分类名称'] || batchItem['categoryName'] || '',
          status: normalizeStatus(batchItem['状态'] || batchItem['status']),
          remark: batchItem['备注'] || batchItem['remark'] || ''
        }
        
        const batchResponse = await addBatch(batchData)
        const newBatchId = batchResponse.data
        
        // 获取该批次的虚拟ID
        const batchVirtualId = batchItem['批次虚拟ID'] || batchItem['batchVirtualId']
        
        // 找到属于这个批次的所有标题
        const batchTitles = titlesData.filter(t => 
          (t['批次虚拟ID'] || t['batchVirtualId']) === batchVirtualId
        )
        
        // 批量导入标题
        for (const titleItem of batchTitles) {
          try {
            await addTitlePool({
              batchId: newBatchId,
              title: titleItem['标题内容'] || titleItem['title'],
              locale: titleItem['标题语言'] || titleItem['locale'] || 'zh-CN',
              titleType: titleItem['标题类型'] || titleItem['titleType'] || 'standard',
              sortOrder: parseInt(titleItem['排序'] || titleItem['sortOrder']) || 0
            })
          } catch (error) {
            console.warn('导入标题失败:', error)
          }
        }
        
        successCount++
      } catch (error) {
        console.error('导入批次失败:', error)
      }
    }
    
    proxy.$modal.msgSuccess(`成功导入 ${successCount} 个批次`)
    systemImportDialogOpen.value = false
    systemImportPreviewData.value = []
    systemImportFile.value = null
    getBatchList()
    
  } catch (error) {
    console.error('全站导入失败:', error)
    proxy.$modal.msgError('全站导入失败: ' + (error.message || '未知错误'))
  } finally {
    systemImportLoading.value = false
  }
}

/** 处理文件移除 */
function handleSystemImportFileRemove() {
  systemImportFile.value = null
  systemImportPreviewData.value = []
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

/** 解析全站导入Excel数据 */
async function parseSystemImportExcelData(arrayBuffer) {
  const XLSX = await import('xlsx')
  const workbook = XLSX.read(arrayBuffer, { type: 'array' })
  
  const result = { batches: [], titles: [] }
  
  const batchSheetName = workbook.SheetNames.find(name => 
    name === '批次数据' || name === 'batches'
  )
  if (batchSheetName && workbook.Sheets[batchSheetName]) {
    result.batches = XLSX.utils.sheet_to_json(workbook.Sheets[batchSheetName])
  }
  
  const titleSheetName = workbook.SheetNames.find(name => 
    name === '标题数据' || name === 'titles'
  )
  if (titleSheetName && workbook.Sheets[titleSheetName]) {
    result.titles = XLSX.utils.sheet_to_json(workbook.Sheets[titleSheetName])
  }
  
  return result
}

/** 解析全站导入JSON数据 */
function parseSystemImportJSONData(jsonString) {
  try {
    const data = JSON.parse(jsonString)
    return {
      batches: data.batches || (Array.isArray(data) ? data : []),
      titles: data.titles || []
    }
  } catch (error) {
    throw new Error('JSON格式错误')
  }
}

/** 验证和转换全站导入数据 */
function validateAndTransformSystemImportData(rawData) {
  if (!Array.isArray(rawData) || rawData.length === 0) {
    return []
  }
  
  return rawData.map((item, index) => {
    const transformedItem = {
      batchName: item['批次名称'] || item['batchName'] || `导入批次${index + 1}`,
      batchCode: item['批次编号'] || item['batchCode'] || '',
      importSource: item['导入来源'] || item['importSource'] || '',
      importDate: item['导入日期'] || item['importDate'] || '',
      categoryName: item['分类名称'] || item['categoryName'] || '',
      titleCount: parseInt(item['标题数量'] || item['titleCount']) || 0,
      status: normalizeStatus(item['状态'] || item['status']),
      remark: item['备注'] || item['remark'] || ''
    }
    
    return transformedItem
  })
}

/** 标准化状态值 */
function normalizeStatus(value) {
  if (value === '启用' || value === '1' || value === 1 || value === true) {
    return '1'
  }
  return '0'
}

/** 全站导入 */
function handleFullImport() {
  fullImportDialogOpen.value = true
  fullImportSites.value = []
  fullImportBatches.value = []
  fullImportRelations.value = []
  fullImportFile.value = null
  siteMapping.value = {}
}

/** 处理全站导入文件选择 */
async function handleFullImportFileChange(file) {
  proxy.$modal.msgWarning('标题池全站导入功能开发中，敬请期待')
}

/** 确认全站导入 */
async function confirmFullImport() {
  proxy.$modal.msgWarning('标题池全站导入功能开发中，敬请期待')
}

// === 批量排除管理 ===
async function handleBatchExclude() {
  const selectedRows = batchList.value.filter(batch => batchIds.value.includes(batch.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的批次')
    return
  }
  
  // 只允许默认配置的批次进行批量排除
  const invalidBatches = selectedRows.filter(batch => !isPersonalSite(batch.siteId, siteList.value))
  if (invalidBatches.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的批次进行批量排除管理')
    return
  }
  
  selectedBatchesForBatchExclude.value = selectedRows.map(batch => ({
    id: batch.id,
    batchName: batch.batchName
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中批次的网站关系
    const batchRes = await getBatchTitleBatchSites(selectedBatchesForBatchExclude.value.map(b => b.id))
    const batchMap = batchRes.data || {}
    const results = selectedBatchesForBatchExclude.value.map(batch => {
      const sites = batchMap[batch.id] || []
      return {
        batchId: batch.id,
        batchName: batch.batchName,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    batchExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有批次共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedBatchesForBatchExclude.value.length) {
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

function removeBatchFromBatchExclude(batchId) {
  selectedBatchesForBatchExclude.value = selectedBatchesForBatchExclude.value.filter(
    batch => batch.id !== batchId
  )
  
  batchIds.value = batchIds.value.filter(id => id !== batchId)
  batchMultiple.value = !batchIds.value.length
  
  if (batchTableRef.value) {
    const row = batchList.value.find(batch => batch.id === batchId)
    if (row) {
      batchTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  batchExclusionDetails.value = batchExclusionDetails.value.filter(
    detail => detail.batchId !== batchId
  )
  
  if (selectedBatchesForBatchExclude.value.length === 0) {
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
  if (selectedBatchesForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何批次')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一条请求处理所有选中批次的排除关系
    await batchSaveTitleBatchSiteRelations({
      batchIds: selectedBatchesForBatchExclude.value.map(b => b.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedBatchesForBatchExclude.value.length} 个批次排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedBatchesForBatchExclude.value.length} 个批次的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getBatchList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchTitleBatchSites(selectedBatchesForBatchExclude.value.map(b => b.id))
    const refreshMap = refreshRes.data || {}
    batchExclusionDetails.value = selectedBatchesForBatchExclude.value.map(batch => {
      const sites = refreshMap[batch.id] || []
      return {
        batchId: batch.id,
        batchName: batch.batchName,
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
  const selectedRows = batchList.value.filter(batch => batchIds.value.includes(batch.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的批次')
    return
  }
  
  const isDefaultConfig = isPersonalSite(batchQueryParams.value.siteId, siteList.value)
  
  selectedBatchesForBatchRelation.value = selectedRows.map(batch => ({
    id: batch.id,
    batchName: batch.batchName,
    siteId: batch.siteId
  }))
  
  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchTitleBatchSites(selectedBatchesForBatchRelation.value.map(b => b.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedBatchesForBatchRelation.value.map(batch => {
      const sites = batchMap2[batch.id] || []
      return {
        batchId: batch.id,
        batchName: batch.batchName,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== batch.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    batchRelationDetails.value = results
    
    // 找出被所有批次共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedBatchesForBatchRelation.value.length) {
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

/** 从批量关联中移除某个批次 */
function removeBatchFromBatchRelation(batchId) {
  selectedBatchesForBatchRelation.value = selectedBatchesForBatchRelation.value.filter(
    batch => batch.id !== batchId
  )
  
  batchIds.value = batchIds.value.filter(id => id !== batchId)
  batchMultiple.value = !batchIds.value.length
  
  if (batchTableRef.value) {
    const row = batchList.value.find(batch => batch.id === batchId)
    if (row) {
      batchTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  batchRelationDetails.value = batchRelationDetails.value.filter(
    detail => detail.batchId !== batchId
  )
  
  if (selectedBatchesForBatchRelation.value.length === 0) {
    batchRelationDialogOpen.value = false
  }
}

/** 全选网站（批量关联） */
function selectAllSitesForBatchRelation() {
  batchRelatedSiteIds.value = siteList.value
    .filter(s => s.id !== batchQueryParams.value.siteId && s.isPersonal !== 1)
    .map(site => site.id)
}

/** 取消全选网站（批量关联） */
function deselectAllSitesForBatchRelation() {
  batchRelatedSiteIds.value = []
}

/** 反选网站（批量关联） */
function invertSiteSelectionForBatchRelation() {
  const allSiteIds = siteList.value
    .filter(s => s.id !== batchQueryParams.value.siteId && s.isPersonal !== 1)
    .map(site => site.id)
  const currentSelected = new Set(batchRelatedSiteIds.value)
  batchRelatedSiteIds.value = allSiteIds.filter(id => !currentSelected.has(id))
}

/** 提交批量关联配置 */
async function submitBatchRelations() {
  if (selectedBatchesForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何批次')
    return
  }
  
  batchRelationLoading.value = true
  
  try {
    // 一条请求处理所有选中批次的关联关系
    await batchSaveTitleBatchSiteRelations({
      batchIds: selectedBatchesForBatchRelation.value.map(b => b.id),
      includeSiteIds: batchRelatedSiteIds.value
    })
    
    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedBatchesForBatchRelation.value.length} 个批次关联 ${relateCount} 个网站`
      : `成功取消 ${selectedBatchesForBatchRelation.value.length} 个批次的所有共享关联`
    
    proxy.$modal.msgSuccess(msg)
    getBatchList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchTitleBatchSites(selectedBatchesForBatchRelation.value.map(b => b.id))
    const refreshMap2 = refreshRes2.data || {}
    batchRelationDetails.value = selectedBatchesForBatchRelation.value.map(batch => {
      const sites = refreshMap2[batch.id] || []
      return {
        batchId: batch.id,
        batchName: batch.batchName,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== batch.siteId).map(s => s.siteId),
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

// ========== 导出辅助函数 ==========

/** 获取网站编码 */
function getSiteCode(siteId) {
  if (isPersonalSite(siteId, siteList.value)) return 'default'
  const site = siteList.value.find(s => s.id === siteId)
  return site ? site.code : ''
}

/** 导出到Excel（分两个sheet） */
async function exportBatchesToExcel(batches, titles) {
  const XLSX = await import('xlsx')
  
  // 创建工作簿
  const wb = XLSX.utils.book_new()
  
  // 创建批次工作表
  const batchWs = XLSX.utils.json_to_sheet(batches)
  XLSX.utils.book_append_sheet(wb, batchWs, '批次')
  
  // 创建标题工作表
  const titleWs = XLSX.utils.json_to_sheet(titles)
  XLSX.utils.book_append_sheet(wb, titleWs, '标题')
  
  // 导出文件
  const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-')
  XLSX.writeFile(wb, `标题池批次_${timestamp}.xlsx`)
}

/** 导出到JSON */
function exportBatchesToJSON(batches, titles) {
  const data = {
    batches: batches,
    titles: titles,
    exportTime: new Date().toISOString()
  }
  
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-')
  a.href = url
  a.download = `标题池批次_${timestamp}.json`
  a.click()
  URL.revokeObjectURL(url)
}

/** 导出全站数据为Excel */
async function exportFullDataToExcel(sitesData, batchData, titlesData, relationData) {
  const XLSX = await import('xlsx')
  const wb = XLSX.utils.book_new()
  
  if (sitesData.length > 0) {
    const sitesWs = XLSX.utils.json_to_sheet(sitesData)
    XLSX.utils.book_append_sheet(wb, sitesWs, "网站列表")
  }
  
  const batchWs = XLSX.utils.json_to_sheet(batchData)
  XLSX.utils.book_append_sheet(wb, batchWs, "批次数据")
  
  if (titlesData.length > 0) {
    const titlesWs = XLSX.utils.json_to_sheet(titlesData)
    XLSX.utils.book_append_sheet(wb, titlesWs, "标题数据")
  }
  
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    XLSX.utils.book_append_sheet(wb, relationWs, "网站关联")
  }
  
  const fileName = `标题池全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出全站数据为JSON */
function exportFullDataToJSON(sitesData, batchData, titlesData, relationData) {
  const exportData = {
    sites: sitesData,
    batches: batchData,
    titles: titlesData,
    relations: relationData,
    exportTime: new Date().toISOString()
  }
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  const fileName = `标题池全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
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

/** ========== 监听器 ========== */

// 监听导入对话框打开，初始化表单
watch(showImportDialog, (val) => {
  if (val) {
    // 默认选择当前查询的站点
    importForm.value.siteId = batchQueryParams.value.siteId !== undefined ? batchQueryParams.value.siteId : (siteList.value.length > 0 ? siteList.value[0].id : 0)
    // 加载对应站点的分类
    loadTitleCategoriesForDialog(importForm.value.siteId)
  }
})

// 监听导入表单中的站点切换
watch(() => importForm.value.siteId, (newSiteId) => {
  if (showImportDialog.value && newSiteId !== undefined) {
    loadTitleCategoriesForDialog(newSiteId)
    // 清空已选分类
    importForm.value.categoryId = undefined
  }
})

// 监听批次表单中的站点切换
watch(() => batchForm.value.siteId, (newSiteId) => {
  if (batchOpen.value && newSiteId !== undefined) {
    loadTitleCategoriesForDialog(newSiteId)
  }
})

/** ========== 初始化 ========== */

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  if (siteList.value.length > 0) {
    getBatchList()
  }
})

onMounted(async () => {
  await loadSiteList()
  const restored = restoreViewModeSiteSelection(siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  viewMode.value = restored.viewMode
  batchQueryParams.value.siteId = restored.siteId
  saveViewModeSiteSelection(viewMode.value, batchQueryParams.value.siteId)
  loadTitleCategories()
  getBatchList()
})
</script>
