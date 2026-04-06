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
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入配置名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="存储类型" prop="storageType">
        <el-select v-model="queryParams.storageType" placeholder="请选择存储类型" clearable style="width: 200px">
          <el-option label="GitHub仓库" value="github" />
          <el-option label="AWS S3" value="s3" />
          <el-option label="阿里云OSS" value="aliyun_oss" />
        </el-select>
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 200px" filterable>
          <el-option
            v-for="cat in storageCategoryList"
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
        <el-button type="success" plain icon="Download" @click="handleFullExport" v-hasPermi="['gamebox:storage:export']">全站导出</el-button>
        <el-button type="warning" plain icon="Upload" @click="handleFullImport" v-hasPermi="['gamebox:storage:import']">全站导入</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:storage:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:storage:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:storage:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5" v-if="viewMode === 'creator' && isPersonalSiteCheck(queryParams.siteId)">
        <el-button type="danger" plain icon="CircleClose" :disabled="multiple" @click="handleBatchExclude" v-hasPermi="['gamebox:storage:edit']">批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5" v-if="viewMode === 'creator'">
        <el-button type="primary" plain icon="Link" :disabled="multiple" @click="handleBatchRelation" v-hasPermi="['gamebox:storage:edit']">批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Download" :disabled="multiple" @click="handleExport" v-hasPermi="['gamebox:storage:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleImport" v-hasPermi="['gamebox:storage:import']">导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="storageTableRef" v-loading="loading" :data="storageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="配置名称" align="center" prop="name" :show-overflow-tooltip="true" />
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
          <template v-if="isPersonalSiteCheck(scope.row.creatorSiteId) || isPersonalSiteCheck(scope.row.siteId)">
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
      <el-table-column label="存储类型" align="center" prop="storageType" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.storageType === 'github'" type="success">GitHub</el-tag>
          <el-tag v-else-if="scope.row.storageType === 's3'" type="primary">AWS S3</el-tag>
          <el-tag v-else-if="scope.row.storageType === 'aliyun_oss'" type="warning">阿里云OSS</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="存储标识" align="center" prop="code" width="120" />
      <el-table-column label="分类" align="center" prop="categoryId" width="120">
        <template #default="scope">
          <CategoryTag v-if="scope.row.categoryName" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'storage', icon: scope.row.categoryIcon }" size="small" />
          <CategoryTag v-else-if="getCategoryFromCache(scope.row.categoryId)" :category="{ id: scope.row.categoryId, name: getCategoryFromCache(scope.row.categoryId).name, categoryType: 'storage', icon: getCategoryFromCache(scope.row.categoryId).icon }" size="small" />
          <el-tag v-else-if="scope.row.categoryId" type="warning" size="small">ID: {{ scope.row.categoryId }}</el-tag>
          <el-tag v-else type="info" size="small">未分类</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="自定义域名" align="center" prop="customDomain" :show-overflow-tooltip="true" />
      <el-table-column label="默认配置" align="center" prop="isDefault" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isDefault === '1' ? 'success' : 'info'" size="small">
            {{ scope.row.isDefault === '1' ? '是' : '否' }}
          </el-tag>
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
      <el-table-column label="状态" align="center" prop="status" width="120" v-if="viewMode === 'creator'">
        <template #default="scope">
          <!-- 关联模式下的状态显示 - 基于relationSource统一判断 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的配置 - 显示启用/禁用 -->
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <!-- 关联模式下的操作 - 基于relationSource统一判断 -->
          <template v-if="viewMode === 'related'">
            <!-- own: 网站自己的配置 - 可修改、删除 -->
            <template v-if="scope.row.relationSource === 'own'">
              <el-tooltip content="文件管理" placement="top">
                <el-button link type="primary" icon="FolderOpened" @click="handleFileManage(scope.row)" v-hasPermi="['gamebox:storage:list']" />
              </el-tooltip>
              <el-tooltip content="修改" placement="top">
                <el-button 
                  link 
                  type="primary" 
                  icon="Edit" 
                  @click="handleUpdate(scope.row)" 
                  v-hasPermi="['gamebox:storage:edit']"
                />
              </el-tooltip>
              <el-tooltip content="网站关联" placement="top">
                <el-button 
                  link 
                  type="warning" 
                  icon="Link" 
                  @click="handleManageSites(scope.row)" 
                  v-hasPermi="['gamebox:storage:edit']"
                />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Delete" 
                  @click="handleDelete(scope.row)" 
                  v-hasPermi="['gamebox:storage:remove']"
                />
              </el-tooltip>
            </template>
            <!-- default: 默认配置 - 注意：排除/恢复功能已统一到"批量排除"按钮中管理 -->
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tooltip content="文件管理" placement="top">
                <el-button link type="primary" icon="FolderOpened" @click="handleFileManage(scope.row)" v-hasPermi="['gamebox:storage:list']" />
              </el-tooltip>
              <el-tag v-if="scope.row.isExcluded === 1" type="info" size="small">已排除</el-tag>
            </template>
            <!-- shared: 其他网站分享 - 可移除关联 -->
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-tooltip content="文件管理" placement="top">
                <el-button link type="primary" icon="FolderOpened" @click="handleFileManage(scope.row)" v-hasPermi="['gamebox:storage:list']" />
              </el-tooltip>
              <el-tooltip content="移除关联" placement="top">
                <el-button 
                  link 
                  type="danger" 
                  icon="Close" 
                  @click="handleRemoveFromSite(scope.row)" 
                  v-hasPermi="['gamebox:storage:remove']"
                />
              </el-tooltip>
            </template>
          </template>
          <!-- 创建者模式下的操作 -->
          <template v-else>
            <el-tooltip content="文件管理" placement="top">
              <el-button link type="primary" icon="FolderOpened" @click="handleFileManage(scope.row)" v-hasPermi="['gamebox:storage:list']" />
            </el-tooltip>
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:storage:edit']" />
            </el-tooltip>
            <!-- 非默认配置：显示网站关联 -->
            <el-tooltip content="网站关联" placement="top" v-if="!isPersonalSiteCheck(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="Link" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:storage:edit']"
              />
            </el-tooltip>
            <!-- 默认配置：管理排除 -->
            <el-tooltip content="管理排除" placement="top" v-if="isPersonalSiteCheck(scope.row.creatorSiteId) || isPersonalSiteCheck(scope.row.siteId)">
              <el-button 
                link 
                type="warning" 
                icon="CircleClose" 
                @click="handleManageSites(scope.row)" 
                v-hasPermi="['gamebox:storage:edit']"
              />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:storage:remove']" />
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 网站关联管理对话框 -->
    <SiteRelationManager
      v-model="siteRelationDialogOpen"
      entity-type="storageConfig"
      :entity-id="currentStorageIdForSites"
      :entity-name="currentStorageNameForSites"
      :creator-site-id="currentStorageCreatorSiteId"
      @refresh="getList"
    />

    <!-- 添加或修改存储配置对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="storageRef" :model="form" :rules="dynamicRules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="配置名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入配置名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配置代码" prop="code">
              <el-input v-model="form.code" placeholder="请输入配置代码（英文、数字、下划线）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="创建者网站" prop="siteId">
              <SiteSelect v-model="form.siteId" :site-list="siteList" show-default clearable :disabled="!!form.id" width="100%" />
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
        <el-row v-if="form.siteId && !isPersonalSiteCheck(form.siteId)">
          <el-col :span="24">
            <el-alert
              title="提示：配置可以选择本网站的分类或全局分类，全局分类适用于所有网站"
              type="info"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="存储类型" prop="storageType">
              <el-select v-model="form.storageType" placeholder="请选择存储类型" @change="handleStorageTypeChange">
                <el-option label="GitHub仓库" value="github" />
                <el-option label="AWS S3" value="s3" />
                <el-option label="阿里云OSS" value="aliyun_oss" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-tree-select
                v-model="form.categoryId"
                :data="storageCategoryTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择分类"
                check-strictly
                :render-after-expand="false"
                clearable
                filterable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- GitHub 配置 -->
        <el-row v-if="form.storageType === 'github'">
          <el-col :span="12">
            <el-form-item label="仓库所有者" prop="githubOwner">
              <el-input v-model="form.githubOwner" placeholder="GitHub用户名或组织名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="githubRepo">
              <el-input v-model="form.githubRepo" placeholder="仓库名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.storageType === 'github'">
          <el-col :span="12">
            <el-form-item label="分支名称" prop="githubBranch">
              <el-input v-model="form.githubBranch" placeholder="分支名称，如：main" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="GitHub Token" prop="githubToken">
              <el-input v-model="form.githubToken" placeholder="Personal Access Token" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- AWS S3 配置 -->
        <el-row v-if="form.storageType === 's3'">
          <el-col :span="12">
            <el-form-item label="Bucket名称" prop="r2Bucket">
              <el-input v-model="form.r2Bucket" placeholder="S3存储桶名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Region">
              <el-input v-model="form.r2AccountId" placeholder="可选，为空则自动判断（R2: auto, AWS: us-east-1）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.storageType === 's3'">
          <el-col :span="24">
            <el-form-item label="服务端点" prop="r2PublicUrl">
              <el-input v-model="form.r2PublicUrl" placeholder="必填，如：https://s3.tebi.io 或 https://your-account.r2.cloudflarestorage.com" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.storageType === 's3'">
          <el-col :span="12">
            <el-form-item label="Access Key" prop="r2AccessKey">
              <el-input v-model="form.r2AccessKey" placeholder="访问密钥ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Secret Key" prop="r2SecretKey">
              <el-input v-model="form.r2SecretKey" placeholder="秘密访问密钥" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.storageType === 's3'">
          <el-col :span="24">
            <el-alert
              title="提示：服务端点是必填项，用于指定S3兼容服务的API地址"
              type="info"
              :closable="false"
              show-icon>
              <template #default>
                <div>常见端点示例：</div>
                <div>• AWS S3: https://s3.amazonaws.com 或 https://s3.&lt;region&gt;.amazonaws.com</div>
                <div>• Tebi.io: https://s3.tebi.io</div>
                <div>• Cloudflare R2: https://&lt;account-id&gt;.r2.cloudflarestorage.com</div>
              </template>
            </el-alert>
          </el-col>
        </el-row>
        
        <!-- 阿里云 OSS 配置 -->
        <el-row v-if="form.storageType === 'aliyun_oss'">
          <el-col :span="12">
            <el-form-item label="Access Key" prop="ossAccessKey">
              <el-input v-model="form.ossAccessKey" placeholder="阿里云 AccessKey ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Secret Key" prop="ossSecretKey">
              <el-input v-model="form.ossSecretKey" placeholder="阿里云 AccessKey Secret" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.storageType === 'aliyun_oss'">
          <el-col :span="12">
            <el-form-item label="Bucket名称" prop="ossBucket">
              <el-input v-model="form.ossBucket" placeholder="OSS存储空间名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Endpoint" prop="ossEndpoint">
              <el-input v-model="form.ossEndpoint" placeholder="如：oss-cn-hangzhou.aliyuncs.com" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.storageType === 'aliyun_oss'">
          <el-col :span="24">
            <el-form-item label="Region" prop="ossRegion">
              <el-input v-model="form.ossRegion" placeholder="地域，如：cn-hangzhou" />
            </el-form-item>
          </el-col>
        </el-row>

        
        <!-- 公共配置 -->
        <el-row>
          <el-col :span="12">
            <el-form-item label="基础路径" prop="basePath">
              <el-input v-model="form.basePath" placeholder="请输入基础路径，如：/files" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="自定义域名" prop="customDomain">
              <el-input v-model="form.customDomain" placeholder="请输入自定义域名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.storageType === 'github'">
          <el-col :span="24">
            <el-form-item label="仓库路径" prop="githubPathPrefix">
              <el-input v-model="form.githubPathPrefix" placeholder="仓库内路径，如：docs/" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="默认配置" prop="isDefault">
              <el-switch v-model="form.isDefault" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先级" prop="priority">
              <el-input-number v-model="form.priority" :min="0" :max="999" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
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
            <el-form-item label="配置描述" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入配置描述" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="success" :loading="validating" @click="handleValidate">验证配置</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 文件管理对话框 -->
    <el-dialog
      v-model="fileManageDialogVisible"
      :title="`文件管理 - ${currentConfigName}`"
      width="90%"
      :close-on-click-modal="false"
      append-to-body
    >
      <FileManager v-if="fileManageDialogVisible" :config-id="currentConfigId" :config="currentConfig" />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 排除（不显示所选存储配置）</p>
          <p style="margin: 0;">• <strong>不勾选的网站</strong> = 显示（可以看到所选存储配置）</p>
        </div>
      </el-alert>

      <!-- 冲突规则提示 -->
      <el-alert
        type="warning"
        :closable="false"
        style="margin-bottom: 12px"
      >
        <template #title>
          <span>冲突规则：<strong>include 关联优先于 exclude 排除</strong></span>
        </template>
        <div style="line-height: 1.6; font-size: 13px;">
          <p style="margin: 0 0 4px 0;">当某网站同时存在 include 关联和 exclude 排除时，<strong>仅 include 关联生效</strong>，exclude 被自动忽略。</p>
          <p style="margin: 0;">如需彻底清除冲突，可先在"批量关联管理"中移除相应网站的关联设置。</p>
        </div>
      </el-alert>
      
      <div style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; margin-bottom: 8px;">
          <strong>已选存储配置：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedStoragesForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="storage in selectedStoragesForBatchExclude" 
            :key="storage.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeStorageFromBatchExclude(storage.id)"
            size="small"
          >
            {{ storage.name }}
          </el-tag>
        </div>
      </div>
      
      <el-collapse v-if="storageExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各配置当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="storageExclusionDetails" size="small" stripe>
              <el-table-column label="配置名称" prop="storageName" width="150" show-overflow-tooltip />
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
              content="该网站已存在 include 关联，exclude 将被忽略，include 优先生效"
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选存储配置对该网站可见）</p>
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
          <strong>已选存储配置：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedStoragesForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="storage in selectedStoragesForBatchRelation" 
            :key="storage.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeStorageFromBatchRelation(storage.id)"
            size="small"
          >
            {{ storage.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各配置的当前关联/排除状态 -->
      <el-collapse v-if="storageRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各配置当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="storageRelationDetails" size="small" stripe>
              <el-table-column label="配置名称" prop="storageName" width="150" show-overflow-tooltip />
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
        <p>• 全站导出将包含所有存储配置数据（默认配置 + 所有站点）</p>
        <p>• 包含所有网站关联关系</p>
        <p>• 适用于系统迁移、备份等场景</p>
        <p style="margin-top: 10px; color: #F56C6C; font-weight: bold;">⚠️ 数据量可能较大，请耐心等待</p>
      </template>
    </FullExportDialog>

    <!-- 数据导出对话框 -->
    <ExportDialog
      v-model="exportDialogOpen"
      :selectedCount="ids.length"
      v-model:exportFormat="exportFormat"
      :loading="exportLoading"
      @confirm="confirmExport"
    >
      <template #exportTips>
        <p>• 存储配置数据：包含配置信息和关联关系</p>
        <p>• 存储配置不包含翻译数据</p>
        <p style="margin-top: 10px; color: #909399;">导入时将自动归属到当前选择的网站</p>
      </template>
    </ExportDialog>

    <!-- 数据导入对话框 -->
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
        <p>• 将根据导入数据重建存储配置和关联关系</p>
        <p>• 请确保文件格式与导出的数据格式一致</p>
        <p>• 必填字段：配置名称、配置代码、存储类型</p>
      </template>
      <template #previewColumns>
        <el-table-column prop="name" label="配置名称" width="120" />
        <el-table-column prop="storageType" label="存储类型" width="100" />
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
      :importData="fullImportStorages"
      v-model:importTranslations="fullImportTranslations"
      v-model:importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      :dataLabel="'存储配置'"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #importTips>
        <p style="margin: 0;">从其他系统导入完整的存储配置数据，包括：</p>
        <ul style="margin: 5px 0; padding-left: 20px;">
          <li>存储配置基础数据</li>
          <li>网站关联关系</li>
          <li>默认配置排除关系</li>
        </ul>
      </template>
    </FullImportDialog>
  </div>
</template>

<script setup name="Storage">
import { listStorage, getStorage, delStorage, addStorage, updateStorage, validateStorage } from "@/api/gamebox/storage"
import { listCategory, listVisibleCategory } from "@/api/gamebox/category"
import { getStorageConfigSites, getBatchStorageConfigSites, batchSaveStorageConfigSiteRelations } from "@/api/gamebox/siteRelation"
import { useSiteSelection, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"
import CategoryTag from "@/components/CategoryTag/index.vue"
import SiteRelationManager from "@/components/SiteRelationManager/index.vue"
import { ExportDialog, ImportDialog, FullExportDialog, FullImportDialog } from "@/components/ImportExportDialogs"
import { CircleClose, Link, Warning } from '@element-plus/icons-vue'
import FileManager from './components/FileManager.vue'
import { handleTree } from "@/utils/ruoyi"

const { proxy } = getCurrentInstance()
const storageTableRef = ref(null)

// 使用网站选择组合式函数
const { siteList, currentSiteId, loadSiteList, getSiteName } = useSiteSelection()
const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

const storageList = ref([])
const storageCategoryList = ref([])
const storageCategoryTreeOptions = ref([]) // 树形结构，用于对话框
const categoryCache = ref(new Map()) // 分类缓存，用于表格显示
const includeDefaultConfig = ref(false) // 是否包含默认配置
const viewMode = ref('creator') // 查看模式：creator-创建者, related-关联网站
const open = ref(false)
const loading = ref(true)
const validating = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentStorageIdForSites = ref(0)
const currentStorageNameForSites = ref('')
const currentStorageCreatorSiteId = ref(0)

// 旧的单个排除对话框变量（已废弃，仅保留用于避免报错）
const exclusionDialogVisible = ref(false)
const currentStorageForExclusion = ref(null)
const selectedExcludedSites = ref([])
const exclusionSiteList = ref([])

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const selectedStoragesForBatchExclude = ref([])
const storageExclusionDetails = ref([]) // 各配置的排除详情

// 冲突网站 ID 集合（存在 include 关联的网站，exclude 对其无效）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  storageExclusionDetails.value.forEach(detail => {
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
const selectedStoragesForBatchRelation = ref([])
const storageRelationDetails = ref([]) // 各配置的关联详情

// 是否处于默认配置的批量关联管理
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId, siteList.value))

// 冲突网站 ID 集合（存在 exclude 关系的网站）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  storageRelationDetails.value.forEach(detail => {
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
const fullImportStorages = ref([]) // 导入的存储配置数据
const fullImportRelations = ref([]) // 导入的关联关系
const fullImportTranslations = ref([]) // 导入的翻译数据
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
    name: undefined,
    storageType: undefined,
    categoryId: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: "配置名称不能为空", trigger: "blur" }],
    code: [
      { required: true, message: "配置代码不能为空", trigger: "blur" },
      { pattern: /^[a-zA-Z0-9_]+$/, message: "配置代码只能包含字母、数字和下划线", trigger: "blur" }
    ],
    storageType: [{ required: true, message: "存储类型不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 根据存储类型动态计算验证规则
const dynamicRules = computed(() => {
  const baseRules = {
    name: [{ required: true, message: "配置名称不能为空", trigger: "blur" }],
    code: [
      { required: true, message: "配置代码不能为空", trigger: "blur" },
      { pattern: /^[a-zA-Z0-9_]+$/, message: "配置代码只能包含字母、数字和下划线", trigger: "blur" }
    ],
    storageType: [{ required: true, message: "存储类型不能为空", trigger: "change" }]
  }
  
  // 根据存储类型添加特定字段的验证
  if (form.value.storageType === 'github') {
    baseRules.githubOwner = [{ required: true, message: "GitHub仓库所有者不能为空", trigger: "blur" }]
    baseRules.githubRepo = [{ required: true, message: "GitHub仓库名称不能为空", trigger: "blur" }]
    baseRules.githubBranch = [{ required: true, message: "GitHub分支名称不能为空", trigger: "blur" }]
    baseRules.githubToken = [{ required: true, message: "GitHub Token不能为空", trigger: "blur" }]
  } else if (form.value.storageType === 's3') {
    baseRules.r2Bucket = [{ required: true, message: "Bucket名称不能为空", trigger: "blur" }]
    baseRules.r2PublicUrl = [{ required: true, message: "服务端点不能为空", trigger: "blur" }]
    baseRules.r2AccessKey = [{ required: true, message: "Access Key不能为空", trigger: "blur" }]
    baseRules.r2SecretKey = [{ required: true, message: "Secret Key不能为空", trigger: "blur" }]
    baseRules.r2PublicUrl = [{ required: true, message: "服务端点不能为空", trigger: "blur" }]
  } else if (form.value.storageType === 'aliyun_oss') {
    baseRules.ossAccessKey = [{ required: true, message: "Access Key不能为空", trigger: "blur" }]
    baseRules.ossSecretKey = [{ required: true, message: "Secret Key不能为空", trigger: "blur" }]
    baseRules.ossBucket = [{ required: true, message: "Bucket名称不能为空", trigger: "blur" }]
    baseRules.ossEndpoint = [{ required: true, message: "Endpoint不能为空", trigger: "blur" }]
  }
  
  return baseRules
})

/** 网站切换事件 */
function handleSiteChange() {
  // 切换网站时重置"含默认配置"选项
  if (!queryParams.value.siteId || isPersonalSite(queryParams.value.siteId, siteList.value)) {
    includeDefaultConfig.value = false
  }
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  queryParams.value.pageNum = 1
  loadStorageCategoriesForQuery(queryParams.value.siteId)
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
    listStorage(params).then(response => {
      storageList.value = response.rows || []
      total.value = response.total
      // 预加载所有分类信息到缓存
      preloadCategories(storageList.value)
      loading.value = false
    }).catch(error => {
      console.error('查询存储配置失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  } else {
    // 未选择网站：默认显示默认配置
    listStorage({ ...params, siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      storageList.value = response.rows || []
      total.value = response.total
      // 预加载所有分类信息到缓存
      preloadCategories(storageList.value)
      loading.value = false
    }).catch(error => {
      console.error('查询存储配置失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  }
}

// 预加载分类信息到缓存
function preloadCategories(storageRecords) {
  // 提取所有唯一的categoryId
  const categoryIds = [...new Set(storageRecords.map(s => s.categoryId).filter(id => id))]
  
  if (categoryIds.length === 0) return
  
  // 批量查询所有分类（不限制siteId，这样可以查到全局和各站点的分类）
  listCategory({ 
    categoryType: 'storage', 
    pageNum: 1, 
    pageSize: 9999,
    status: '1'
  }).then(response => {
    const categories = response.rows || []
    categories.forEach(cat => {
      categoryCache.value.set(cat.id, cat)
    })
  })
}

// 从缓存获取分类名称
function getCategoryFromCache(categoryId) {
  if (!categoryId) return null
  return categoryCache.value.get(categoryId)
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
    code: undefined,
    storageType: "github",
    categoryId: undefined,
    basePath: undefined,
    customDomain: undefined,
    githubToken: undefined,
    githubOwner: undefined,
    githubRepo: undefined,
    githubBranch: "main",
    githubPathPrefix: undefined,
    r2AccessKey: undefined,
    r2SecretKey: undefined,
    r2Bucket: undefined,
    r2AccountId: undefined,
    r2PublicUrl: undefined,
    ossAccessKey: undefined,
    ossSecretKey: undefined,
    ossBucket: undefined,
    ossEndpoint: undefined,
    ossRegion: undefined,
    isDefault: "0",
    priority: 100,
    status: "1",
    description: undefined
  }
  proxy.resetForm("storageRef")
}

function handleStorageTypeChange() {
  // 切换存储类型时清空其他存储类型的字段
  // 只在新增时清空，编辑时保留数据
  if (!form.value.id) {
    // 清空 GitHub 字段
    if (form.value.storageType !== 'github') {
      form.value.githubToken = undefined
      form.value.githubOwner = undefined
      form.value.githubRepo = undefined
      form.value.githubBranch = undefined
      form.value.githubPathPrefix = undefined
    }
    // 清空 S3 字段
    if (form.value.storageType !== 's3') {
      form.value.r2AccessKey = undefined
      form.value.r2SecretKey = undefined
      form.value.r2Bucket = undefined
      form.value.r2AccountId = undefined
      form.value.r2PublicUrl = undefined
    }
    // 清空 OSS 字段
    if (form.value.storageType !== 'aliyun_oss') {
      form.value.ossAccessKey = undefined
      form.value.ossSecretKey = undefined
      form.value.ossBucket = undefined
      form.value.ossEndpoint = undefined
      form.value.ossRegion = undefined
    }
  }
}

function handleValidate() {
  proxy.$refs["storageRef"].validate(valid => {
    if (valid) {
      validating.value = true
      validateStorage(form.value).then(response => {
        proxy.$modal.msgSuccess("配置验证成功")
        validating.value = false
      }).catch(error => {
        validating.value = false
      })
    }
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

function handleAdd() {
  reset()
  // 默认使用当前查询的站点ID
  if (queryParams.value.siteId) {
    form.value.siteId = queryParams.value.siteId
  } else if (siteList.value.length > 0) {
    // 如果查询没有选择站点，默认选择第一个站点
    form.value.siteId = siteList.value[0].id
  }
  loadStorageCategoriesForDialog(form.value.siteId)
  open.value = true
  title.value = "添加存储配置"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getStorage(id).then(response => {
    const data = response.data
    open.value = true
    title.value = "修改存储配置"
    
    // 先设置非分类字段
    form.value = { ...data, categoryId: undefined }
    
    // 异步加载分类列表，加载完成后再设置categoryId
    loadStorageCategoriesForDialog(data.siteId).then(() => {
      nextTick(() => {
        form.value.categoryId = data.categoryId
      })
    })
  })
}

function submitForm() {
  proxy.$refs["storageRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateStorage(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addStorage(form.value).then(response => {
          const newStorageId = response.data
          
          // 验证返回的ID是否有效
          if (!newStorageId || typeof newStorageId !== 'number') {
            proxy.$modal.msgWarning('存储配置创建成功，但无法获取新ID，请刷新页面')
            open.value = false
            getList()
            return
          }
          
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const storageIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除存储配置编号为"' + storageIds + '"的数据项？').then(function() {
    return delStorage(storageIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

// 文件管理相关
const fileManageDialogVisible = ref(false)
const currentConfigId = ref(null)
const currentConfigName = ref('')
const currentConfig = ref(null)

/** 管理网站关联 */
async function handleManageSites(row) {
  // 始终使用统一的 SiteRelationManager 组件
  currentStorageIdForSites.value = row.id
  currentStorageNameForSites.value = row.name
  currentStorageCreatorSiteId.value = row.siteId || 0
  siteRelationDialogOpen.value = true
}

function handleFileManage(row) {
  currentConfigId.value = row.id
  currentConfigName.value = row.name
  currentConfig.value = row
  fileManageDialogVisible.value = true
}

/** 排除默认配置 */
async function handleExcludeDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认排除"${row.name}"吗？排除后该存储配置将不在当前网站显示。`)
    
    const res = await getStorageConfigSites(row.id)
    const relations = res.data || []
    const newExcludes = relations.filter(r => r.relationType === 'exclude').map(r => r.siteId)
    if (!newExcludes.includes(currentQuerySiteId)) newExcludes.push(currentQuerySiteId)
    await batchSaveStorageConfigSiteRelations({
      storageConfigIds: [row.id],
      excludeSiteIds: newExcludes
    })
    
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
    await proxy.$modal.confirm(`确认恢复"${row.name}"吗？恢复后该存储配置将重新在当前网站显示。`)
    
    const res = await getStorageConfigSites(row.id)
    const relations = res.data || []
    const newExcludes = relations.filter(r => r.relationType === 'exclude' && r.siteId !== currentQuerySiteId).map(r => r.siteId)
    await batchSaveStorageConfigSiteRelations({
      storageConfigIds: [row.id],
      excludeSiteIds: newExcludes
    })
    
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
  const selectedRows = storageList.value.filter(storage => ids.value.includes(storage.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的存储配置')
    return
  }
  
  // 只允许默认配置的存储进行批量排除
  const invalidStorages = selectedRows.filter(storage => !isPersonalSite(storage.siteId, siteList.value))
  if (invalidStorages.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的存储进行批量排除管理')
    return
  }
  
  selectedStoragesForBatchExclude.value = selectedRows.map(storage => ({
    id: storage.id,
    name: storage.name
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中存储的网站关系
    const batchRes = await getBatchStorageConfigSites(selectedStoragesForBatchExclude.value.map(s => s.id))
    const batchMap = batchRes.data || {}
    const results = selectedStoragesForBatchExclude.value.map(storage => {
      const sites = batchMap[storage.id] || []
      return {
        storageId: storage.id,
        storageName: storage.name,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    storageExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有存储共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedStoragesForBatchExclude.value.length) {
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

/** 从批量排除中移除某个存储配置 */
function removeStorageFromBatchExclude(storageId) {
  selectedStoragesForBatchExclude.value = selectedStoragesForBatchExclude.value.filter(
    storage => storage.id !== storageId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== storageId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (storageTableRef.value) {
    const row = storageList.value.find(storage => storage.id === storageId)
    if (row) {
      storageTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  storageExclusionDetails.value = storageExclusionDetails.value.filter(
    detail => detail.storageId !== storageId
  )
  
  if (selectedStoragesForBatchExclude.value.length === 0) {
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
  if (selectedStoragesForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何存储配置')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一次性传入所有配置 ID，单次请求完成
    await batchSaveStorageConfigSiteRelations({
      storageConfigIds: selectedStoragesForBatchExclude.value.map(s => s.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedStoragesForBatchExclude.value.length} 个配置排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedStoragesForBatchExclude.value.length} 个配置的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchStorageConfigSites(selectedStoragesForBatchExclude.value.map(s => s.id))
    const refreshMap = refreshRes.data || {}
    storageExclusionDetails.value = selectedStoragesForBatchExclude.value.map(storage => {
      const sites = refreshMap[storage.id] || []
      return {
        storageId: storage.id,
        storageName: storage.name,
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
  const selectedRows = storageList.value.filter(storage => ids.value.includes(storage.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的存储配置')
    return
  }
  
  const isDefaultConfig = isPersonalSite(queryParams.value.siteId, siteList.value)
  
  selectedStoragesForBatchRelation.value = selectedRows.map(storage => ({
    id: storage.id,
    name: storage.name,
    siteId: storage.siteId
  }))
  
  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchStorageConfigSites(selectedStoragesForBatchRelation.value.map(s => s.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedStoragesForBatchRelation.value.map(storage => {
      const sites = batchMap2[storage.id] || []
      return {
        storageId: storage.id,
        storageName: storage.name,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== storage.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    storageRelationDetails.value = results
    
    // 找出被所有配置共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedStoragesForBatchRelation.value.length) {
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

/** 从批量关联中移除某个存储配置 */
function removeStorageFromBatchRelation(storageId) {
  selectedStoragesForBatchRelation.value = selectedStoragesForBatchRelation.value.filter(
    storage => storage.id !== storageId
  )
  
  ids.value = ids.value.filter(id => id !== storageId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  if (storageTableRef.value) {
    const row = storageList.value.find(storage => storage.id === storageId)
    if (row) {
      storageTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  storageRelationDetails.value = storageRelationDetails.value.filter(
    detail => detail.storageId !== storageId
  )
  
  if (selectedStoragesForBatchRelation.value.length === 0) {
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
  if (selectedStoragesForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何存储配置')
    return
  }
  
  batchRelationLoading.value = true
  
  try {
    // 一次性传入所有配置 ID，单次请求完成
    await batchSaveStorageConfigSiteRelations({
      storageConfigIds: selectedStoragesForBatchRelation.value.map(s => s.id),
      includeSiteIds: batchRelatedSiteIds.value
    })
    
    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedStoragesForBatchRelation.value.length} 个配置关联 ${relateCount} 个网站`
      : `成功取消 ${selectedStoragesForBatchRelation.value.length} 个配置的所有共享关联`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchStorageConfigSites(selectedStoragesForBatchRelation.value.map(s => s.id))
    const refreshMap2 = refreshRes2.data || {}
    storageRelationDetails.value = selectedStoragesForBatchRelation.value.map(storage => {
      const sites = refreshMap2[storage.id] || []
      return {
        storageId: storage.id,
        storageName: storage.name,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== storage.siteId).map(s => s.siteId),
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

/** 移除网站关联 */
async function handleRemoveFromSite(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认从"${getSiteName(currentQuerySiteId)}"移除该存储配置的关联吗？`)
    
    const res = await getStorageConfigSites(row.id)
    const relations = res.data || []
    const newIncludes = relations.filter(r => r.relationType !== 'exclude' && r.siteId !== currentQuerySiteId).map(r => r.siteId)
    const newExcludes = relations.filter(r => r.relationType === 'exclude' && r.siteId !== currentQuerySiteId).map(r => r.siteId)
    await batchSaveStorageConfigSiteRelations({
      storageConfigIds: [row.id],
      includeSiteIds: newIncludes,
      excludeSiteIds: newExcludes
    })
    
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
      const relationResponse = await getStorageConfigSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段（忽略排除逻辑）
        const { updateStorageVisibility } = await import('@/api/gamebox/siteRelation')
        await updateStorageVisibility(currentQuerySiteId, row.id, newValue)
        const action = newValue === '1' ? '显示' : '隐藏'
        proxy.$modal.msgSuccess(`${action}成功`)
        // 重新加载列表以更新统计信息
        getList()
      } else {
        // 没有关联记录：使用排除逻辑
        if (newValue === '0') {
          // 要隐藏 -> 添加排除关系
          const relRes = await getStorageConfigSites(row.id)
          const allRels = relRes.data || []
          const newExcludes = allRels.filter(r => r.relationType === 'exclude').map(r => r.siteId)
          if (!newExcludes.includes(currentQuerySiteId)) newExcludes.push(currentQuerySiteId)
          await batchSaveStorageConfigSiteRelations({
            storageConfigIds: [row.id],
            excludeSiteIds: newExcludes
          })
          proxy.$modal.msgSuccess('已排除该配置')
        } else {
          // 要显示 -> 移除排除关系
          const relRes = await getStorageConfigSites(row.id)
          const allRels = relRes.data || []
          const newExcludes = allRels.filter(r => r.relationType === 'exclude' && r.siteId !== currentQuerySiteId).map(r => r.siteId)
          await batchSaveStorageConfigSiteRelations({
            storageConfigIds: [row.id],
            excludeSiteIds: newExcludes
          })
          proxy.$modal.msgSuccess('已恢复该配置')
        }
        // 重新加载列表以更新排除网站数量
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用状态
      const text = newValue === '1' ? '启用' : '禁用'
      await updateStorage({ id: row.id, status: newValue })
      row.status = newValue
      proxy.$modal.msgSuccess(`${text}成功`)
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      const { updateStorageVisibility } = await import('@/api/gamebox/siteRelation')
      await updateStorageVisibility(currentQuerySiteId, row.id, newValue)
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

/** 快速切换可见性（关联模式下的开关） */
async function handleQuickToggleVisibility(row) {
  const currentQuerySiteId = queryParams.value.siteId
  // 切换状态
  const newVisibility = row.isVisible === '1' ? '0' : '1'
  const action = newVisibility === '1' ? '显示' : '隐藏'
  
  try {
    const { updateStorageVisibility } = await import('@/api/gamebox/siteRelation')
    await updateStorageVisibility(currentQuerySiteId, row.id, newVisibility)
    
    // 更新成功后才更新UI
    row.isVisible = newVisibility
    proxy.$modal.msgSuccess(`${action}成功`)
  } catch (error) {
    console.error('更新可见性失败:', error)
    proxy.$modal.msgError('更新失败')
  }
}

// 加载存储分类列表（对话框使用）
// 使用可见分类接口，只展示对该网站可见的分类（默认配置未排除 + 跨站共享可见 + 自有启用）
function loadStorageCategoriesForDialog(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点或选择了全局，只查询全局分类
    return listVisibleCategory({ 
      categoryType: 'storage', 
      siteId: personalSiteId.value,
      pageNum: 1, 
      pageSize: 1000 
    }).then(response => {
      let categories = response.rows || []
      // 为分类添加站点标识和图标
      categories = categories.map(cat => ({
        ...cat,
        name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [默认配置]`
      }))
      storageCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
    })
  }
  
  // 选择了具体站点：使用可见分类接口，只返回可见的分类
  // 包括：1.默认配置未排除 2.其他网站跨站共享可见 3.自有网站启用
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
      // 判断来源
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
    
    // 转换为树形结构
    storageCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
  })
}

// 加载存储分类列表（查询表单使用）
// 始终使用关联模式，显示所有分类并标记不可用的
function loadStorageCategoriesForQuery(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点，只查询默认配置分类（创建者模式）
    listCategory({ 
      categoryType: 'storage', 
      siteId: personalSiteId.value, 
      status: '1',
      queryMode: 'creator',
      pageNum: 1, 
      pageSize: 1000 
    }).then(response => {
      storageCategoryList.value = response.rows || []
    })
    return
  }
  
  // 选择了具体站点：始终使用关联模式（related）查询所有分类
  // 关联模式会返回：1.默认配置（包括被排除的） 2.跨站共享（包括不可见的） 3.自有分类
  // 并且会在 is_visible 字段中标记每个分类的可见性状态
  listCategory({ 
    categoryType: 'storage',
    siteId: siteId,
    status: '1',
    queryMode: 'related',  // 始终使用关联模式
    pageNum: 1,
    pageSize: 1000
  }).then(response => {
    storageCategoryList.value = response.rows || []
  })
}

// 监听表单中的siteId变化，自动重新加载分类（对话框）
watch(() => form.value.siteId, (newSiteId, oldSiteId) => {
  // 只有在对话框已打开且是用户主动切换站点时才处理
  if (!open.value) return
  
  loadStorageCategoriesForDialog(newSiteId)
  // 只有在用户主动切换站点时（不是初始化时）才清空分类
  if (oldSiteId !== undefined) {
    form.value.categoryId = undefined
  }
})

// 监听查询表单的includeDefaultConfig变化，重新加载分类
watch(() => includeDefaultConfig.value, () => {
  loadStorageCategoriesForQuery(queryParams.value.siteId)
})

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  if (siteList.value.length > 0) {
    loadStorageCategoriesForQuery(queryParams.value.siteId)
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
  loadStorageCategoriesForQuery(queryParams.value.siteId)
  getList()
})

// ============ 导入导出功能 ============

/** 导出数据 */
async function handleExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgError('请选择要导出的存储配置')
    return
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
    
    // 1. 获取所有存储配置数据
    const storagesResponse = await listStorage({ 
      queryMode: 'creator', 
      pageNum: 1, 
      pageSize: 9999 
    })
    const storageData = storagesResponse.rows || []
    
    // 2. 先收集所有涉及的网站ID
    const siteIds = new Set()
    storageData.forEach(storage => {
      if (storage.siteId !== null && storage.siteId !== undefined) {
        siteIds.add(storage.siteId)
      }
    })
    
    // 3. 获取所有关联关系（包括普通关联和默认配置排除），同时收集网站ID
    const relationDataRaw = [] // 临时存储，稍后转换虚拟ID
    for (let index = 0; index < storageData.length; index++) {
      const storage = storageData[index]
      const virtualId = index + 1 // 虚拟ID从1开始
      
      if (storage.siteId && !isPersonalSite(storage.siteId, siteList.value)) {
        // 非默认配置：获取所有关联关系
        try {
          const response = await getStorageConfigSites(storage.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId) // 收集网站ID
              
              // 根据 relationType 区分关联类型
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              
              const relationData = {
                存储配置虚拟ID: virtualId,
                配置代码: storage.code,
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
          console.warn('获取存储配置关联失败:', storage.id, error)
        }
      } else {
        // 默认配置：获取所有网站关系（包括关联和排除）
        try {
          const response = await getStorageConfigSites(storage.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId) // 收集网站ID
              
              // 根据 relationType 区分关联类型
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              
              const relationData = {
                存储配置虚拟ID: virtualId,
                配置代码: storage.code,
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
          console.warn('获取默认配置关系失败:', storage.id, error)
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
      存储配置虚拟ID: rel.存储配置虚拟ID,
      配置代码: rel.配置代码,
      关联类型: rel.关联类型,
      网站虚拟ID: siteIdToVirtualIdMap.get(rel.网站ID_原始) ?? 0,
      网站编码: rel.网站编码,
      可见性: rel.可见性
    }))
    
    // 6. 转换存储配置数据格式
    const formattedStorageData = storageData.map((storage, index) => ({
      存储配置虚拟ID: index + 1, // 添加虚拟ID
      配置名称: storage.name,
      配置代码: storage.code,
      存储类型: storage.storageType,
      网站虚拟ID: siteIdToVirtualIdMap.get(storage.siteId) ?? 0,
      网站编码: getSiteCode(storage.siteId),
      分类ID: storage.categoryId,
      分类名称: storage.categoryName || '',
      'GitHub所有者': storage.githubOwner || '',
      'GitHub仓库': storage.githubRepo || '',
      'GitHub分支': storage.githubBranch || '',
      'GitHub Token': storage.githubToken || '',
      'S3 Bucket': storage.r2Bucket || '',
      'S3端点': storage.r2PublicUrl || '',
      'S3 Access Key': storage.r2AccessKey || '',
      'S3 Secret Key': storage.r2SecretKey || '',
      'OSS Access Key': storage.ossAccessKey || '',
      'OSS Secret Key': storage.ossSecretKey || '',
      'OSS Bucket': storage.ossBucket || '',
      'OSS Endpoint': storage.ossEndpoint || '',
      'OSS Region': storage.ossRegion || '',
      基础路径: storage.basePath || '',
      自定义域名: storage.customDomain || '',
      'GitHub路径前缀': storage.githubPathPrefix || '',
      默认配置: storage.isDefault || '0',
      优先级: storage.priority || 0,
      状态: storage.status || '1',
      配置描述: storage.description || ''
    }))
    
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, formattedStorageData, relationData)
    } else {
      exportFullDataToJSON(sitesData, formattedStorageData, relationData)
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

/** 导出全站数据为Excel */
async function exportFullDataToExcel(sitesData, storageData, relationData) {
  const XLSX = await import('xlsx')
  
  const wb = XLSX.utils.book_new()
  
  // 网站列表sheet（第一个sheet）
  if (sitesData.length > 0) {
    const sitesWs = XLSX.utils.json_to_sheet(sitesData)
    XLSX.utils.book_append_sheet(wb, sitesWs, "网站列表")
  }
  
  // 存储配置数据sheet
  const storageWs = XLSX.utils.json_to_sheet(storageData)
  XLSX.utils.book_append_sheet(wb, storageWs, "存储配置数据")
  
  // 关联关系sheet
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    XLSX.utils.book_append_sheet(wb, relationWs, "网站关联")
  }
  
  const fileName = `存储配置全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出全站数据为JSON */
function exportFullDataToJSON(sitesData, storageData, relationData) {
  const exportData = {
    sites: sitesData,
    storages: storageData,
    relations: relationData,
    exportTime: new Date().toISOString()
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `存储配置全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
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
    proxy.$modal.msgError('请选择要导出的存储配置')
    return
  }
  
  exportLoading.value = true
  
  try {
    // 获取选中的存储配置详细数据
    const storagePromises = selectedIds.map(id => getStorage(id))
    const storageResponses = await Promise.all(storagePromises)
    const storageData = storageResponses.map(response => response.data)
    
    // 转换数据格式
    const formattedData = storageData.map(storage => ({
      配置名称: storage.name,
      配置代码: storage.code,
      存储类型: storage.storageType,
      网站编码: getSiteCode(storage.siteId),
      分类名称: storage.categoryName || getCategoryFromCache(storage.categoryId)?.name || '',
      'GitHub所有者': storage.githubOwner || '',
      'GitHub仓库': storage.githubRepo || '',
      'GitHub分支': storage.githubBranch || '',
      'GitHub Token': storage.githubToken || '',
      'S3 Bucket': storage.r2Bucket || '',
      'S3端点': storage.r2PublicUrl || '',
      'S3 Access Key': storage.r2AccessKey || '',
      'S3 Secret Key': storage.r2SecretKey || '',
      'OSS Access Key': storage.ossAccessKey || '',
      'OSS Secret Key': storage.ossSecretKey || '',
      'OSS Bucket': storage.ossBucket || '',
      'OSS Endpoint': storage.ossEndpoint || '',
      'OSS Region': storage.ossRegion || '',
      基础路径: storage.basePath || '',
      自定义域名: storage.customDomain || '',
      'GitHub路径前缀': storage.githubPathPrefix || '',
      默认配置: storage.isDefault || '0',
      优先级: storage.priority || 0,
      状态: storage.status || '1',
      配置描述: storage.description || ''
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
async function exportToExcel(storageData) {
  const XLSX = await import('xlsx')
  
  const wb = XLSX.utils.book_new()
  
  // 创建存储配置数据工作表
  const storageWs = XLSX.utils.json_to_sheet(storageData)
  const storageColWidths = [
    { wch: 20 }, // 配置名称
    { wch: 15 }, // 配置代码
    { wch: 12 }, // 存储类型
    { wch: 15 }, // 网站编码
    { wch: 15 }, // 分类名称
    { wch: 15 }, // GitHub所有者
    { wch: 20 }, // GitHub仓库
    { wch: 10 }, // GitHub分支
    { wch: 30 }, // GitHub Token
    { wch: 15 }, // S3 Bucket
    { wch: 25 }, // S3端点
    { wch: 20 }, // S3 Access Key
    { wch: 20 }, // S3 Secret Key
    { wch: 20 }, // OSS Access Key
    { wch: 20 }, // OSS Secret Key
    { wch: 15 }, // OSS Bucket
    { wch: 25 }, // OSS Endpoint
    { wch: 12 }, // OSS Region
    { wch: 15 }, // 基础路径
    { wch: 20 }, // 自定义域名
    { wch: 15 }, // GitHub路径前缀
    { wch: 10 }, // 默认配置
    { wch: 8 }, // 优先级
    { wch: 8 }, // 状态
    { wch: 30 }  // 配置描述
  ]
  storageWs['!cols'] = storageColWidths
  XLSX.utils.book_append_sheet(wb, storageWs, "存储配置数据")
  
  const fileName = `存储配置数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出为JSON */
function exportToJSON(storageData) {
  const exportData = {
    storages: storageData,
    exportTime: new Date().toISOString()
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `存储配置数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
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
    
    importPreviewData.value = validateAndTransformImportData(parsedData.storages)
    
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
  
  const result = { storages: [] }
  
  // 解析存储配置数据
  const storageSheetName = workbook.SheetNames.find(name => 
    name === '存储配置数据' || name === 'storages'
  ) || workbook.SheetNames[0]
  if (storageSheetName && workbook.Sheets[storageSheetName]) {
    result.storages = XLSX.utils.sheet_to_json(workbook.Sheets[storageSheetName])
  }
  
  return result
}

/** 解析JSON数据 */
function parseJSONData(jsonString) {
  try {
    const data = JSON.parse(jsonString)
    return {
      storages: data.storages || (Array.isArray(data) ? data : [])
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
      const category = storageCategoryList.value.find(cat => 
        cat.name === item['分类名称'] && 
        (cat.siteId === siteId || isPersonalSite(cat.siteId, siteList.value))
      )
      if (category) {
        categoryId = category.id
      }
    }
    
    const transformedItem = {
      virtualId: item['存储配置虚拟ID'],
      name: item['配置名称'] || `导入配置${index + 1}`,
      code: item['配置代码'] || `import_${Date.now()}_${index}`,
      storageType: item['存储类型'] || 'github',
      siteId: siteId,
      categoryId: categoryId,
      githubOwner: item['GitHub所有者'] || '',
      githubRepo: item['GitHub仓库'] || '',
      githubBranch: item['GitHub分支'] || 'main',
      githubToken: item['GitHub Token'] || '',
      r2Bucket: item['S3 Bucket'] || '',
      r2PublicUrl: item['S3端点'] || '',
      r2AccessKey: item['S3 Access Key'] || '',
      r2SecretKey: item['S3 Secret Key'] || '',
      ossAccessKey: item['OSS Access Key'] || '',
      ossSecretKey: item['OSS Secret Key'] || '',
      ossBucket: item['OSS Bucket'] || '',
      ossEndpoint: item['OSS Endpoint'] || '',
      ossRegion: item['OSS Region'] || '',
      basePath: item['基础路径'] || '',
      customDomain: item['自定义域名'] || '',
      githubPathPrefix: item['GitHub路径前缀'] || '',
      isDefault: normalizeStatus(item['默认配置']),
      priority: parseInt(item['优先级']) || 0,
      status: normalizeStatus(item['状态']),
      description: item['配置描述'] || '',
      // 显示用字段
      siteCode: getSiteCode(siteId)
    }
    
    return transformedItem
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
    
    // 批量导入存储配置
    for (const storageData of importPreviewData.value) {
      const { siteCode, ...cleanData } = storageData
      await addStorage(cleanData)
    }
    
    proxy.$modal.msgSuccess(`成功导入 ${importPreviewData.value.length} 条存储配置`)
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
  fullImportStorages.value = []
  fullImportRelations.value = []
  fullImportFile.value = null
  siteMapping.value = {}
  hasDefaultConfig.value = false
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
    fullImportStorages.value = parsedData.storages || []
    fullImportRelations.value = parsedData.relations || []
    
    // 检查是否包含默认配置
    hasDefaultConfig.value = fullImportStorages.value.some(s => s['网站ID'] === 0 || s['网站编码'] === 'default')
    
    // 初始化网站映射
    initializeSiteMapping()
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + error.message)
  }
}

/** 初始化网站映射（尝试根据网站编码自动匹配） */
function initializeSiteMapping() {
  const mapping = {}
  
  fullImportSites.value.forEach(importSite => {
    const siteVirtualId = importSite['网站虚拟ID'] || 0
    const siteCode = importSite['网站编码']
    
    if (siteVirtualId > 0) { // 只处理非默认配置
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
    storages: [],
    relations: []
  }
  
  // 解析网站列表
  if (workbook.SheetNames.includes('网站列表')) {
    const siteSheet = workbook.Sheets['网站列表']
    result.sites = XLSX.utils.sheet_to_json(siteSheet)
  } else {
    result.sites = []
  }
  
  // 解析存储配置数据
  if (workbook.SheetNames.includes('存储配置数据')) {
    const storageSheet = workbook.Sheets['存储配置数据']
    result.storages = XLSX.utils.sheet_to_json(storageSheet)
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
  
  if (!jsonData.sites || !Array.isArray(jsonData.sites)) {
    throw new Error('缺少网站列表数据')
  }
  
  return {
    sites: jsonData.sites || [],
    storages: jsonData.storages || [],
    relations: jsonData.relations || []
  }
}

/** 处理全站导入文件移除 */
function handleFullImportFileRemove() {
  fullImportFile.value = null
  fullImportSites.value = []
  fullImportStorages.value = []
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
  const virtualIdToNewIdMap = {} // 虚拟ID到新ID的映射（按目标网站分组） {siteId: {virtualId: newId}}
  
  // 初始化每个目标网站的虚拟ID映射
  Object.values(siteMapping.value).forEach(targetSiteId => {
    virtualIdToNewIdMap[targetSiteId] = {}
  })
  
  // 1. 处理存储配置数据导入（按网站）
  const storagesBySite = {}
  
  fullImportStorages.value.forEach(storage => {
    const siteVirtualId = storage['网站虚拟ID'] || 0
    const virtualId = storage['存储配置虚拟ID']
    
    // 获取目标网站ID（包括默认配置和真实网站）
    const targetSiteId = siteMapping.value[siteVirtualId]
    
    if (targetSiteId !== undefined && virtualId) {
      if (!storagesBySite[targetSiteId]) {
        storagesBySite[targetSiteId] = []
      }
      storagesBySite[targetSiteId].push({
        ...storage,
        targetSiteId,
        virtualId
      })
    }
  })
  
  // 2. 按目标网站分别导入存储配置
  for (const [targetSiteId, storages] of Object.entries(storagesBySite)) {
    await importStoragesForSite(targetSiteId, storages, virtualIdToNewIdMap[targetSiteId] || {})
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

/** 为指定网站导入存储配置 */
async function importStoragesForSite(targetSiteId, storages, virtualIdMap) {
  console.log(`开始为网站 ${targetSiteId} 导入存储配置...`)
  
  if (!storages || storages.length === 0) {
    console.log('没有存储配置需要导入')
    return
  }

  // 转换数据格式
  const transformedData = validateAndTransformImportData(storages)
  
  // 设置目标网站ID（虚拟ID已经在validateAndTransformImportData中保留）
  transformedData.forEach((item, index) => {
    item.siteId = parseInt(targetSiteId)
    // virtualId已经在transformedData中，不需要再次赋值
    delete item.targetSiteId
    delete item.siteCode
  })

  // 导入存储配置
  for (const storageItem of transformedData) {
    try {
      console.log('导入存储配置:', storageItem.name)
      
      // 保存虚拟ID，因为addStorage可能不会返回它
      const virtualId = storageItem.virtualId
      
      // 尝试导入
      let response
      let retryCount = 0
      const maxRetries = 5
      
      while (retryCount < maxRetries) {
        try {
          response = await addStorage(storageItem)
          break // 成功则跳出循环
        } catch (error) {
          // 检查是否是 code 重复错误
          if (error.message && error.message.includes('Duplicate entry') && error.message.includes('uk_code')) {
            retryCount++
            if (retryCount >= maxRetries) {
              throw new Error(`配置代码 "${storageItem.code}" 重复，尝试${maxRetries}次后仍失败`)
            }
            // 在 code 后添加时间戳后缀
            const timestamp = Date.now()
            storageItem.code = `${storageItem.code.split('_imported_')[0]}_imported_${timestamp}`
            console.warn(`检测到代码重复，重试第${retryCount}次，新代码: ${storageItem.code}`)
          } else {
            throw error // 其他错误直接抛出
          }
        }
      }
      
      const newStorageId = response.data
      
      // 记录虚拟ID到新ID的映射
      if (virtualId) {
        virtualIdMap[virtualId] = newStorageId
      }
      
      console.log(`存储配置导入成功: ${storageItem.name} (虚拟ID: ${virtualId}) -> ID: ${newStorageId}`)
    } catch (error) {
      console.error(`存储配置导入失败: ${storageItem.name}`, error)
      throw new Error(`存储配置 "${storageItem.name}" 导入失败: ${error.message}`)
    }
  }

  console.log(`网站 ${targetSiteId} 存储配置导入完成`)
}

/** 导入关联关系 */
async function importRelations(virtualIdToNewIdMap) {
  console.log(`开始导入 ${fullImportRelations.value.length} 条关联关系...`)
  const pendingRelations = new Map() // storageConfigId -> { includes: Set, excludes: Set }
  
  for (const relation of fullImportRelations.value) {
    try {
      // 根据虚拟ID找到新的存储配置ID
      const virtualId = relation['存储配置虚拟ID']
      const relationTypeFromData = relation['关联类型'] || '关联'
      const siteVirtualId = relation['网站虚拟ID'] || 0
      
      // 从存储配置数据中获取创建者网站虚拟ID
      const storageConfig = fullImportStorages.value.find(s => 
        s['存储配置虚拟ID'] === virtualId
      )
      const creatorSiteVirtualId = storageConfig ? (storageConfig['网站虚拟ID'] || 0) : 0
      
      // 如果默认配置被转换为新网站配置，跳过这些关联关系（已在createRelationsForConvertedDefault中处理）
      if (hasDefaultConfig.value && createDefaultAsNewSite.value && creatorSiteVirtualId === 0) {
        console.log(`跳过默认配置关联关系（已由createRelationsForConvertedDefault处理）: 虚拟ID=${virtualId}, 目标网站虚拟ID=${siteVirtualId}`)
        continue
      }
      
      if (!virtualId) {
        console.warn('跳过关联关系，缺少存储配置虚拟ID:', relation)
        continue
      }
      
      // 映射关联网站虚拟ID到目标网站ID
      const targetSiteId = siteMapping.value[siteVirtualId]
      if (!targetSiteId) {
        console.warn(`跳过关联关系，找不到网站虚拟ID ${siteVirtualId} 的映射目标:`, relation)
        continue
      }
      
      let newStorageId = null
      let storageCreatorTargetSiteId = null
      
      // 从所有网站的映射中查找该虚拟ID对应的新ID
      for (const siteId in virtualIdToNewIdMap) {
        if (virtualIdToNewIdMap[siteId][virtualId]) {
          newStorageId = virtualIdToNewIdMap[siteId][virtualId]
          storageCreatorTargetSiteId = parseInt(siteId)
          break
        }
      }
      
      if (!newStorageId) {
        console.warn(`跳过关联关系，找不到虚拟ID ${virtualId} 对应的存储配置:`, relation)
        continue
      }
      
      // 避免创建自己到自己的关联
      if (storageCreatorTargetSiteId === targetSiteId) {
        console.log(`跳过自关联: 存储配置所属网站 ${storageCreatorTargetSiteId} 等于目标网站 ${targetSiteId}`)
        continue
      }
      
      // 根据关联类型处理不同的关联逻辑
      if (!pendingRelations.has(newStorageId)) pendingRelations.set(newStorageId, { includes: new Set(), excludes: new Set() })
      if (relationTypeFromData === '排除') {
        console.log(`记录排除关系: 网站 ${targetSiteId} 排除存储配置 ${newStorageId} (虚拟ID: ${virtualId})`)
        pendingRelations.get(newStorageId).excludes.add(targetSiteId)
      } else {
        console.log(`记录关联关系: 存储配置 ${newStorageId} (创建者网站: ${storageCreatorTargetSiteId}) -> 网站 ${targetSiteId}`)
        pendingRelations.get(newStorageId).includes.add(targetSiteId)
      }
      
    } catch (error) {
      console.error('关联关系导入失败:', relation, error)
      // 继续导入其他关联关系
    }
  }
  
  // 批量提交所有积累的关联关系
  for (const [storageConfigId, { includes, excludes }] of pendingRelations.entries()) {
    const batchData = { storageConfigIds: [storageConfigId] }
    if (includes.size > 0) batchData.includeSiteIds = Array.from(includes)
    if (excludes.size > 0) batchData.excludeSiteIds = Array.from(excludes)
    await batchSaveStorageConfigSiteRelations(batchData)
    console.log(`批量关联关系导入成功: storageConfigId=${storageConfigId}`)
  }

  console.log('关联关系导入完成')
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
    
    // 为每个转换后的存储配置检查原来的关联和排除关系
    for (const [virtualId, newStorageId] of Object.entries(defaultVirtualIdMap)) {
      // 查找原来的关联关系数据（包括排除和关联）
      const originalRelations = fullImportRelations.value.filter(rel => {
        const relVirtualId = rel['存储配置虚拟ID']
        
        // 从存储配置数据中获取创建者网站虚拟ID
        const storageConfig = fullImportStorages.value.find(s => 
          s['存储配置虚拟ID'] === relVirtualId
        )
        const creatorSiteVirtualId = storageConfig ? (storageConfig['网站虚拟ID'] || 0) : 0
        
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
      
      console.log(`存储配置 ${newStorageId} (虚拟ID: ${virtualId}): 有关联的网站虚拟ID:`, Array.from(includedSiteVirtualIds), '仅排除的网站虚拟ID:', excludedSiteVirtualIds)
      
      // 收集该存储配置需要关联的所有网站ID
      const newIncludeSiteIds = []
      
      // 第二步：优先处理关联逻辑
      // 处理显式关联关系
      for (const includedVId of includedSiteVirtualIds) {
        const targetSiteId = siteMapping.value[includedVId]
        if (!targetSiteId) {
          console.warn(`关联的网站虚拟ID ${includedVId} 未映射，跳过`)
          continue
        }
        newIncludeSiteIds.push(targetSiteId)
        console.log(`记录关联: storageConfig=${newStorageId}, site=${targetSiteId}`)
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
        
        // 如果既不在排除列表中，也不在显式关联列表中，则记录关联
        if (!isExcludedMapped && !isAlreadyIncluded) {
          newIncludeSiteIds.push(targetSiteId)
          console.log(`记录逆向关联: storageConfig=${newStorageId}, site=${targetSiteId}`)        } else if (isExcludedMapped) {
          console.log(`跳过网站 ${targetSiteId}，因为它是被排除网站的映射目标`)
        } else {
          console.log(`跳过网站 ${targetSiteId}，因为已在显式关联中处理`)
        }
      }
      
      // 批量创建关联关系
      if (newIncludeSiteIds.length > 0) {
        try {
          await batchSaveStorageConfigSiteRelations({
            storageConfigIds: [newStorageId],
            includeSiteIds: newIncludeSiteIds
          })
          console.log(`批量创建关联成功: storageConfig=${newStorageId}, 网站数=${newIncludeSiteIds.length}`)
        } catch (error) {
          console.warn(`批量创建关联失败: storageConfig=${newStorageId}`, error.message || '未知错误')
        }
      }
      
      // 记录已处理的关联关系（所有涉及该存储配置和默认配置的关联）
      originalRelations.forEach(rel => {
        processedRelations.push(rel)
      })
    }
    
    // 从关联关系列表中移除已处理的关联
    processedRelations.forEach(processedRel => {
      const index = fullImportRelations.value.findIndex(rel => {
        return rel['存储配置虚拟ID'] === processedRel['存储配置虚拟ID'] && 
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

// 工具函数

/** 获取网站编码 */
function getSiteCode(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) return 'default'
  const site = siteList.value.find(s => s.id === siteId)
  return site ? site.code : `site_${siteId}`
}

/** 规范化状态值 */
function normalizeStatus(status) {
  if (!status) return '0'
  const str = String(status).toLowerCase()
  if (str === '1' || str === 'true' || str === '启用' || str === 'enabled' || str === 'active') {
    return '1'
  }
  return '0'
}

/** 获取指定网站的存储配置数量 */
function getStorageCountForSite(siteId) {
  return fullImportStorages.value.filter(s => s['网站ID'] === siteId).length
}

/** 获取存储配置关联关系 */
async function getStorageRelations(storageId) {
  try {
    const response = await getStorageConfigSites(storageId)
    return response.data || []
  } catch (error) {
    console.error('获取存储配置关联关系失败:', error)
    return []
  }
}

</script>
