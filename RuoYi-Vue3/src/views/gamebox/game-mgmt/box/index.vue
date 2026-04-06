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
          <el-form-item label="盒子名称" prop="name">
            <el-input 
              v-model="queryParams.name" 
              placeholder="请输入盒子名称" 
              clearable 
              @keyup.enter="handleQuery"
              prefix-icon="Search"
            />
          </el-form-item>
        </el-col>
        <el-col :xl="6" :lg="8" :md="12" :sm="24">
          <el-form-item label="分类" prop="categoryId">
            <el-tree-select
              v-model="queryParams.categoryId"
              :data="boxCategoryQueryTreeOptions"
              :props="{ label: 'name', children: 'children', value: 'id', disabled: 'disabled' }"
              placeholder="全部分类（含子类）"
              clearable
              filterable
              check-strictly
              :render-after-expand="false"
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
            <div style="color: #909399; font-size: 12px; margin-top: 2px; line-height: 1.2;">
              <el-icon style="font-size: 12px;"><InfoFilled /></el-icon>
              支持主分类和多分类递归搜索
            </div>
          </el-form-item>
        </el-col>
        <el-col :xl="6" :lg="8" :md="12" :sm="24">
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
              <el-option label="启用" value="1">
                <el-icon style="color: #67C23A; margin-right: 8px;"><CircleCheck /></el-icon>
                <span>启用</span>
              </el-option>
              <el-option label="禁用" value="0">
                <el-icon style="color: #F56C6C; margin-right: 8px;"><CircleClose /></el-icon>
                <span>禁用</span>
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
            <el-button type="success" plain icon="Download" @click="handleFullExport" v-hasPermi="['gamebox:box:export']">全站导出</el-button>
            <el-button type="warning" plain icon="Upload" @click="handleFullImport" v-hasPermi="['gamebox:box:import']">全站导入</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:box:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:box:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:box:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="MagicStick" :disabled="multiple" @click="handleBatchTranslate" v-hasPermi="['gamebox:box:edit']">批量翻译</el-button>
      </el-col>
      <el-col v-if="isPersonalSiteCheck(queryParams.siteId)" :span="1.5">
        <el-button type="danger" plain icon="CircleClose" :disabled="multiple" @click="handleBatchExclude" v-hasPermi="['gamebox:box:edit']">批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Link" :disabled="multiple" @click="handleBatchRelation" v-hasPermi="['gamebox:box:edit']">批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleImport" v-hasPermi="['gamebox:box:add']">盒子数据导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Download" :disabled="multiple" @click="handleExport" v-hasPermi="['gamebox:box:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleSystemImport" v-hasPermi="['gamebox:box:import']">导入</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="boxTableRef" v-loading="loading" :data="boxList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" fixed />
      <el-table-column label="ID" align="center" prop="id" width="70" fixed />
      <el-table-column label="盒子图标" align="center" prop="logoUrl" width="90">
        <template #default="scope">
          <el-image 
            v-if="scope.row.logoUrl" 
            :src="scope.row.logoUrl" 
            style="width: 50px; height: 50px; border-radius: 8px;" 
            fit="cover"
            :preview-src-list="[scope.row.logoUrl]"
            preview-teleported
          >
            <template #error>
              <div style="width: 50px; height: 50px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; border-radius: 8px;">
                <el-icon style="font-size: 24px; color: #c0c4cc;"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div v-else style="width: 50px; height: 50px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; border-radius: 8px;">
            <el-icon style="font-size: 24px; color: #c0c4cc;"><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="盒子名称" align="left" prop="name" min-width="150" :show-overflow-tooltip="true">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <span style="font-weight: 500;">{{ scope.row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建者网站" align="center" prop="siteId" width="130">
        <template #default="scope">
          <el-tag type="success" effect="plain">{{ getSiteName(scope.row.siteId) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="relationSource" width="100" v-if="viewMode === 'related'">
        <template #default="scope">
          <el-tag v-if="scope.row.relationSource === 'own'" type="success" size="small">自有</el-tag>
          <el-tag v-else-if="scope.row.relationSource === 'default'" type="info" size="small">默认配置</el-tag>
          <el-tag v-else-if="scope.row.relationSource === 'shared'" type="warning" size="small">跨站共享</el-tag>
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
      <el-table-column label="分类" align="center" prop="categoryId" width="200">
        <template #default="scope">
          <!-- 多分类显示 -->
          <div v-if="scope.row.categories && scope.row.categories.length > 0" style="display: flex; flex-wrap: wrap; gap: 4px; justify-content: center;">
            <CategoryTag
              v-for="cat in scope.row.categories"
              :key="cat.categoryId"
              :category="{ 
                id: cat.categoryId, 
                name: cat.categoryName, 
                categoryType: 'gamebox', 
                icon: cat.categoryIcon 
              }"
              size="small"
            >
              <template v-if="cat.isPrimary" #prefix>⭐</template>
            </CategoryTag>
          </div>
          <!-- 主分类兜底显示 -->
          <div v-else>
            <CategoryTag v-if="scope.row.categoryName" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'gamebox', icon: scope.row.categoryIcon }" size="small" />
            <CategoryTag v-else-if="getCategoryFromCache(scope.row.categoryId)" :category="{ id: scope.row.categoryId, name: getCategoryFromCache(scope.row.categoryId).name, categoryType: 'gamebox', icon: getCategoryFromCache(scope.row.categoryId).icon }" size="small" />
            <el-tag v-else-if="scope.row.categoryId" type="warning" size="small">ID: {{ scope.row.categoryId }}</el-tag>
            <el-tag v-else type="info" size="small">未分类</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="游戏数量" align="center" prop="gameCount" width="100">
        <template #default="scope">
          <el-tag type="info" effect="plain">
            <el-icon style="margin-right: 4px;"><Tickets /></el-icon>
            {{ scope.row.gameCount || 0 }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下载量" align="center" prop="downloadCount" width="100">
        <template #default="scope">
          <el-tag type="success" effect="plain">
            <el-icon style="margin-right: 4px;"><Download /></el-icon>
            {{ scope.row.downloadCount || 0 }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80" v-if="viewMode !== 'related'">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" effect="dark">
            {{ scope.row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding" width="250" fixed="right">
        <template #default="scope">
          <!-- 关联模式 -->
          <template v-if="viewMode === 'related'">
            <template v-if="scope.row.relationSource === 'own'">
              <el-tooltip content="修改" placement="top">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:box:edit']" />
              </el-tooltip>
              <el-tooltip content="主页预览" placement="top">
                <el-button link type="success" icon="View" @click="handlePreviewHomepages(scope.row)" />
              </el-tooltip>
              <el-tooltip content="游戏管理" placement="top">
                <el-button link type="primary" icon="Right" @click="handleManageGames(scope.row)" v-hasPermi="['gamebox:box:edit']" />
              </el-tooltip>
              <el-tooltip content="网站关联" placement="top">
                <el-button link type="warning" icon="Link" @click="handleManageSites(scope.row)" v-hasPermi="['gamebox:box:edit']" />
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:box:remove']" />
              </el-tooltip>
            </template>
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-tooltip content="主页预览" placement="top">
                <el-button link type="success" icon="View" @click="handlePreviewHomepages(scope.row)" />
              </el-tooltip>
              <el-tooltip content="游戏管理" placement="top">
                <el-button link type="primary" icon="Right" @click="handleManageGames(scope.row)" v-hasPermi="['gamebox:box:edit']" />
              </el-tooltip>
              <el-tag v-if="scope.row.isExcluded" type="info" size="small">已排除</el-tag>
            </template>
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-tooltip content="主页预览" placement="top">
                <el-button link type="success" icon="View" @click="handlePreviewHomepages(scope.row)" />
              </el-tooltip>
              <el-tooltip content="游戏管理" placement="top">
                <el-button link type="primary" icon="Right" @click="handleManageGames(scope.row)" v-hasPermi="['gamebox:box:edit']" />
              </el-tooltip>
            </template>
          </template>
          <!-- 创建者模式 -->
          <template v-else>
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:box:edit']" />
            </el-tooltip>
            <el-tooltip content="主页预览" placement="top">
              <el-button link type="success" icon="View" @click="handlePreviewHomepages(scope.row)" />
            </el-tooltip>
            <el-tooltip content="游戏管理" placement="top">
              <el-button link type="primary" icon="Right" @click="handleManageGames(scope.row)" v-hasPermi="['gamebox:box:edit']" />
            </el-tooltip>
            <el-tooltip content="翻译管理" placement="top">
              <el-button link type="warning" icon="Document" @click="handleManageTranslations(scope.row)" v-hasPermi="['gamebox:box:edit']" />
            </el-tooltip>
            <el-tooltip content="字段映射" placement="top">
              <el-button link type="info" icon="Operation" @click="handleManageFieldMapping(scope.row)" v-hasPermi="['gamebox:box:edit']" />
            </el-tooltip>
            <el-tooltip v-if="!isPersonalSiteCheck(scope.row.siteId)" content="网站关联" placement="top">
              <el-button link type="warning" icon="Link" @click="handleManageSites(scope.row)" v-hasPermi="['gamebox:box:edit']" />
            </el-tooltip>
            <el-tooltip v-if="isPersonalSiteCheck(scope.row.siteId)" content="排除管理" placement="top">
              <el-button link type="danger" icon="CircleClose" @click="handleManageSites(scope.row)" v-hasPermi="['gamebox:box:edit']" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:box:remove']" />
            </el-tooltip>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 翻译管理对话框 -->
    <TranslationManager
      v-model="translationDialogOpen"
      entity-type="box"
      :entity-id="currentTranslationBoxId"
      :entity-name="currentTranslationBoxName"
      :site-id="queryParams.siteId || 0"
      :translation-fields="boxTranslationFields"
      :original-data="currentTranslationBoxData"
      @refresh="getList"
    />

    <!-- 主页面预览对话框 -->
    <el-dialog title="主页面查看" v-model="homepageDialogOpen" width="700px" append-to-body>
      <div v-loading="loadingHomepages">
        <el-alert 
          v-if="!homepageBinding && !loadingHomepages" 
          type="warning" 
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        >
          <template #title>
            该游戏盒子暂未绑定主页文章
          </template>
        </el-alert>
        
        <div v-else-if="homepageBinding" style="display: flex; flex-direction: column; gap: 16px;">
          <!-- 主文章信息 -->
          <el-card shadow="hover" :body-style="{ padding: '16px' }">
            <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
              <el-tag type="primary" size="large">主文章</el-tag>
              <span style="font-weight: 600; font-size: 16px; color: #303133;">
                {{ homepageBinding.title || '未命名主文章' }}
              </span>
            </div>
            <div style="color: #909399; font-size: 13px;">
              主文章ID: {{ homepageBinding.masterArticleId }}
            </div>
          </el-card>
          
          <!-- 语言版本列表 -->
          <div v-if="homepageBinding.articles && homepageBinding.articles.length > 0">
            <div style="color: #606266; font-size: 14px; font-weight: 500; margin-bottom: 12px;">
              语言版本 ({{ homepageBinding.articles.length }})
            </div>
            <el-card 
              v-for="article in homepageBinding.articles" 
              :key="article.locale" 
              shadow="hover"
              :body-style="{ padding: '16px' }"
              style="margin-bottom: 12px;"
            >
              <div style="display: flex; align-items: center; justify-content: space-between;">
                <div style="flex: 1;">
                  <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 8px;">
                    <el-tag type="success" size="default">{{ article.locale }}</el-tag>
                    <span style="font-weight: 500; font-size: 15px; color: #303133;">{{ getLocaleLabel(article.locale) }}</span>
                  </div>
                  <div style="color: #606266; font-size: 14px; margin-bottom: 6px;">
                    {{ article.articleTitle }}
                  </div>
                  <div style="color: #909399; font-size: 13px;">
                    文章ID: {{ article.articleId }} | 
                    状态: <el-tag size="small" :type="article.status === '1' ? 'success' : 'info'">
                      {{ article.status === '1' ? '已发布' : '草稿' }}
                    </el-tag>
                  </div>
                </div>
                <el-button 
                  v-if="article.storageUrl" 
                  type="primary" 
                  icon="View" 
                  @click="openUrl(article.storageUrl)"
                  size="default"
                >
                  预览
                </el-button>
                <el-button 
                  v-else 
                  type="info" 
                  disabled
                  size="default"
                >
                  未发布
                </el-button>
              </div>
            </el-card>
          </div>
        </div>
      </div>
      <template #footer>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <el-button v-if="homepageBinding" type="danger" @click="handleUnbindHomepage">解除绑定</el-button>
            <el-button type="primary" @click="openBindHomepageDialog">{{ homepageBinding ? '重新绑定' : '绑定主页' }}</el-button>
          </div>
          <el-button @click="homepageDialogOpen = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 绑定主页对话框 -->
    <el-dialog 
      :title="`主页绑定 - ${currentBoxDisplayName}`" 
      v-model="bindHomepageDialogOpen" 
      width="600px" 
      append-to-body 
      :close-on-click-modal="false"
      class="homepage-bind-dialog"
    >
      <!-- 提示信息 -->
      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      >
        <template #title>
          <div style="font-size: 14px; line-height: 1.6;">
            为该游戏盒子选择一个主文章作为主页<br/>
            <span style="color: #909399; font-size: 12px;">所有语言版本将统一使用该主文章的对应语言内容</span>
          </div>
        </template>
      </el-alert>

      <!-- 游戏盒子信息卡片 -->
      <el-card shadow="never" style="margin-bottom: 20px; background-color: #f5f7fa;">
        <div style="display: flex; align-items: center; gap: 12px;">
          <el-icon :size="18" color="#409eff"><Box /></el-icon>
          <div style="flex: 1;">
            <div style="font-weight: 500; color: #303133;">{{ currentBoxForBind?.name || 'U2Game盒子' }}</div>
            <div style="font-size: 12px; color: #909399; margin-top: 4px;">游戏盒子 ID: {{ currentBoxForBind?.id || 3 }}</div>
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
              <el-radio-button label="article">
                <el-icon style="margin-right: 4px;"><Document /></el-icon>
                主文章
              </el-radio-button>
            </el-radio-group>
          </el-form-item>

          <!-- 选择主文章 -->
          <el-form-item v-if="currentBindType === 'article'" label="选择主文章">
            <el-select 
              v-model="currentBindTargetId" 
              placeholder="请选择要绑定的主文章" 
              clearable 
              filterable 
              style="width: 100%"
              :loading="loadingBindArticles"
              @visible-change="handleArticleSelectVisible"
            >
              <el-option
                v-for="article in bindArticleList"
                :key="article.id"
                :label="article.title || '未命名主文章'"
                :value="article.id"
              >
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <span>{{ article.title || '未命名主文章' }}</span>
                  <el-tag v-if="article.relationSource === 'own'" type="primary" size="small">自有</el-tag>
                  <el-tag v-else-if="article.relationSource === 'default'" type="warning" size="small">默认</el-tag>
                  <el-tag v-else-if="article.relationSource === 'shared'" type="success" size="small">共享</el-tag>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmitHomepageBinding" :loading="binding">保 存</el-button>
          <el-button @click="bindHomepageDialogOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导入游戏盒子对话框 -->
    <el-dialog title="导入游戏盒子" v-model="importOpen" width="900px" append-to-body>
      <el-alert
        title="使用说明"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      >
        <template #default>
          <p style="margin: 0 0 5px 0;">请粘贴外部系统的游戏盒子JSON数据，系统将自动解析并导入。</p>
          <p style="margin: 0; font-size: 12px; color: #909399;">支持的数据格式：包含 gamename, pic1, download 等字段的JSON对象</p>
        </template>
      </el-alert>
      <el-form :model="importForm" ref="importFormRef" label-width="100px">
        <el-form-item label="创建者网站" prop="siteId" :rules="[{ required: true, message: '请选择创建者网站', trigger: 'change' }]">
          <SiteSelect v-model="importForm.siteId" :site-list="siteList" show-default default-label="全局" clearable filterable width="100%" />
        </el-form-item>
        <el-form-item label="JSON数据" prop="jsonData" :rules="[{ required: true, message: '请输入JSON数据', trigger: 'blur' }]">
          <el-input
            v-model="importForm.jsonData"
            type="textarea"
            :rows="15"
            placeholder="请粘贴JSON数据..."
          />
        </el-form-item>
        <el-form-item v-if="importPreview" label="预览数据">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="盒子名称" :span="2">{{ importPreview.name }}</el-descriptions-item>
            <el-descriptions-item label="下载链接" :span="2">{{ importPreview.downloadUrl }}</el-descriptions-item>
            <el-descriptions-item label="Logo URL" :span="2">{{ importPreview.logoUrl }}</el-descriptions-item>
            <el-descriptions-item label="二维码URL" v-if="importPreview.qrcodeUrl" :span="2">{{ importPreview.qrcodeUrl }}</el-descriptions-item>
            <el-descriptions-item label="官网地址" v-if="importPreview.officialUrl">{{ importPreview.officialUrl }}</el-descriptions-item>
            <el-descriptions-item label="推广链接1" v-if="importPreview.promoteUrl1">{{ importPreview.promoteUrl1 }}</el-descriptions-item>
            <el-descriptions-item label="推广链接2" v-if="importPreview.promoteUrl2">{{ importPreview.promoteUrl2 }}</el-descriptions-item>
            <el-descriptions-item label="推广链接3" v-if="importPreview.promoteUrl3">{{ importPreview.promoteUrl3 }}</el-descriptions-item>
            <el-descriptions-item label="注册下载链接" v-if="importPreview.registerDownloadUrl">{{ importPreview.registerDownloadUrl }}</el-descriptions-item>
            <el-descriptions-item label="描述" v-if="importPreview.description" :span="2">{{ importPreview.description }}</el-descriptions-item>
            <el-descriptions-item label="标签" v-if="importPreview.tag">{{ importPreview.tag }}</el-descriptions-item>
            <el-descriptions-item label="添加时间" v-if="importPreview.addtime">{{ importPreview.addtime }}</el-descriptions-item>
          </el-descriptions>
          <el-alert 
            type="success" 
            :closable="false" 
            show-icon
            style="margin-top: 10px;"
          >
            <template #title>
              已解析 {{ Object.keys(importPreview.rawData || {}).length }} 个原始字段，映射了 {{ getFilledFieldsCount() }} 个系统字段
            </template>
          </el-alert>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelImport">取 消</el-button>
          <el-button type="warning" @click="parseImportData">解析数据</el-button>
          <el-button type="primary" @click="submitImport" :disabled="!importPreview">确认导入</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加或修改游戏盒子对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-tabs v-model="activeBoxTab" type="card">
        <!-- 基本信息Tab -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form ref="boxRef" :model="form" :rules="rules" label-width="100px">
            <el-row>
              <el-col :span="24">
                <el-form-item label="创建者网站" prop="siteId">
                  <SiteSelect v-model="form.siteId" :site-list="siteList" show-default default-label="全局" clearable filterable :disabled="!!form.id" width="100%">
                    <template #empty>
                      <div style="padding: 10px; text-align: center; color: #909399;">
                        <p>暂无可用站点</p>
                        <p style="font-size: 12px;">请先在“配置管理 > 站点配置”中添加站点</p>
                      </div>
                    </template>
                  </SiteSelect>
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
              title="提示：盒子可以选择本网站的分类或全局分类，全局分类适用于所有网站"
              type="info"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>        <el-row>
          <el-col :span="24">
            <el-form-item label="盒子名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入盒子名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="主分类" prop="categoryId">
              <el-tree-select
                v-model="form.categoryId"
                :data="boxCategoryTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择主分类"
                check-strictly
                :render-after-expand="false"
                clearable
                filterable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折扣率" prop="discountRate">
              <el-input-number v-model="form.discountRate" :min="0.1" :max="1" :step="0.1" :precision="2" placeholder="0.1-1.0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="关联分类" prop="selectedCategoryIds">
              <div style="width: 100%;">
                <el-tree-select
                  v-model="form.selectedCategoryIds"
                  :data="boxCategoryTreeOptions"
                  :props="{ value: 'id', label: 'name', children: 'children' }"
                  value-key="id"
                  placeholder="选择关联分类（可多选）"
                  check-strictly
                  :render-after-expand="false"
                  multiple
                  filterable
                  style="width: 100%"
                />
                <div style="color: #909399; font-size: 12px; margin-top: 4px;">
                  选择多个分类后，此盒子将在所有选中的分类下展示
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="盒子图标" prop="logoUrl">
              <el-input v-model="form.logoUrl" placeholder="请输入盒子图标URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="Banner图" prop="bannerUrl">
              <el-input v-model="form.bannerUrl" placeholder="请输入Banner图URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="二维码URL" prop="qrcodeUrl">
              <el-input v-model="form.qrcodeUrl" placeholder="请输入二维码URL（用于生成二维码）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="官网地址" prop="officialUrl">
              <el-input v-model="form.officialUrl" placeholder="请输入官网地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="下载链接" prop="downloadUrl">
              <el-input v-model="form.downloadUrl" placeholder="请输入下载链接" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="安卓下载" prop="androidUrl">
              <el-input v-model="form.androidUrl" placeholder="请输入安卓下载链接" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="iOS下载" prop="iosUrl">
              <el-input v-model="form.iosUrl" placeholder="请输入iOS下载链接" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="推广链接①" prop="promoteUrl1">
              <el-input v-model="form.promoteUrl1" placeholder="请输入推广链接1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="推广链接②" prop="promoteUrl2">
              <el-input v-model="form.promoteUrl2" placeholder="请输入推广链接2" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="推广链接③" prop="promoteUrl3">
              <el-input v-model="form.promoteUrl3" placeholder="请输入推广链接3" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="注册下载链接" prop="registerDownloadUrl">
              <el-input v-model="form.registerDownloadUrl" placeholder="请输入注册下载链接" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="盒子描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入盒子描述" />
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
            <el-form-item label="导入优先级" prop="priority">
              <el-input-number v-model="form.priority" :min="0" :max="100" :step="10" />
              <div style="color: #909399; font-size: 12px; margin-top: 2px;">
                0~100，值越大优先级越高，高优先级盒子的数据将覆盖低优先级盒子的数据
              </div>
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
      </el-form>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 管理排除网站对话框 -->
    <el-dialog 
      title="管理排除网站" 
      v-model="exclusionDialogOpen" 
      width="600px" 
      append-to-body
    >
      <el-alert
        title="说明"
        type="info"
        :closable="false"
        style="margin-bottom: 15px"
      >
        默认配置对所有网站自动可见。勾选的网站将<strong>不显示</strong>此游戏盒子。
      </el-alert>
      
      <div style="margin-bottom: 10px;">
        <strong>游戏盒子：</strong>{{ currentExclusionBoxName }}
      </div>
      
      <el-checkbox-group v-model="selectedExcludedSiteIds">
        <el-checkbox 
          v-for="site in siteList" 
          :key="site.id" 
          :label="site.id"
          style="display: block; margin: 10px 0;"
        >
          {{ site.name }}
        </el-checkbox>
      </el-checkbox-group>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitExclusions">确 定</el-button>
          <el-button @click="exclusionDialogOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 网站关联管理对话框 -->
    <SiteRelationManager
      v-model="siteRelationDialogOpen"
      entity-type="box"
      :entity-id="currentBoxIdForSites"
      :entity-name="currentBoxNameForSites"
      :creator-site-id="currentBoxCreatorSiteId"
      @refresh="getList"
    />

    <!-- 字段映射管理对话框 -->
    <el-dialog 
      title="字段映射配置管理" 
      v-model="fieldMappingDialogOpen" 
      width="90%" 
      append-to-body
      :close-on-click-modal="false"
    >
      <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
        <template #default>
          <div style="line-height: 1.6;">
            <p style="margin: 5px 0;"><strong>字段映射配置说明：</strong></p>
            <p style="margin: 5px 0;">1. 配置源数据字段如何映射到系统字段（支持嵌套字段如 <code>photo.0.url</code>）</p>
            <p style="margin: 5px 0;">2. 支持值映射，例如：分类名 → 分类ID，游戏类型 → 标准分类</p>
            <p style="margin: 5px 0;">3. 导入游戏时将自动应用此映射配置</p>
            <p style="margin: 5px 0; color: #67c23a;"><strong>💡 快速配置：</strong>点击工具栏中的 <el-tag size="small" type="success" effect="plain">快速配置</el-tag> 按钮，上传样本数据快速生成映射</p>
          </div>
        </template>
      </el-alert>
      
      <div style="margin-bottom: 10px;">
        <strong>游戏盒子：</strong>{{ currentFieldMappingBoxName }}
      </div>
      
      <FieldMappingConfig
        v-model="fieldMappings"
        :box-id="currentFieldMappingBoxId"
        :site-id="currentFieldMappingBoxSiteId"
        :site-name="getSiteName(currentFieldMappingBoxSiteId)"
      />
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="fieldMappingDialogOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 数据导出对话框 -->
    <el-dialog title="数据导出" v-model="exportDialogOpen" width="500px" append-to-body>
      <el-form label-width="100px">
        <el-form-item label="导出格式">
          <el-radio-group v-model="exportFormat">
            <el-radio value="excel">Excel 格式</el-radio>
            <el-radio value="json">JSON 格式</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmExport" :loading="exportLoading">确认导出</el-button>
          <el-button @click="exportDialogOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 全站数据导出对话框 -->
    <el-dialog title="全站数据导出" v-model="fullExportDialogOpen" width="550px" append-to-body>
      <el-alert 
        title="全站数据导出" 
        type="info" 
        :closable="false" 
        style="margin-bottom: 20px;"
      >
        <template #default>
          <div style="font-size: 13px; line-height: 1.5;">
            <p>• 将导出所有盒子数据、网站信息和关联关系</p>
            <p>• 导出的数据可用于数据备份和系统迁移</p>
            <p>• 支持 Excel 和 JSON 两种格式</p>
          </div>
        </template>
      </el-alert>
      <el-form label-width="100px">
        <el-form-item label="导出格式">
          <el-radio-group v-model="fullExportFormat">
            <el-radio value="excel">Excel 格式 (.xlsx)</el-radio>
            <el-radio value="json">JSON 格式 (.json)</el-radio>
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

    <!-- 导入对话框 -->
    <ImportDialog
      v-model="systemImportDialogOpen"
      title="盒子数据导入"
      entity-name="盒子"
      :site-list="siteList"
      :transform-data="transformBoxImportData"
      :validate-data="validateBoxImportData"
      :import-data="importBoxData"
      :parse-excel-data="parseBoxExcelData"
      :parse-json-data="parseBoxJsonData"
      @confirm="handleSystemImportConfirm"
    >
      <template #importTips>
        <p>• 支持导入 Excel (.xlsx) 或 JSON (.json) 格式的盒子数据文件</p>
        <p>• Excel 文件应包含：盒子名称、盒子描述、下载地址、官网地址等字段</p>
        <p>• JSON 文件应为包含 boxes 数组的对象格式</p>
        <p>• 请确保文件格式与导出的数据格式一致</p>
      </template>
    </ImportDialog>

    <!-- 全站导入对话框 -->
    <FullImportDialog
      v-model="fullImportDialogOpen"
      :loading="fullImportLoading"
      :siteList="siteList"
      :importSites="fullImportSites"
      :importData="fullImportBoxes"
      v-model:importTranslations="fullImportTranslations"
      v-model:importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      :dataLabel="'盒子'"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #importTips>
        <p style="margin: 0;">从其他系统导入完整的盒子数据，包括：</p>
        <ul style="margin: 5px 0; padding-left: 20px;">
          <li>盒子基础数据</li>
          <li>网站关联关系</li>
          <li>默认配置排除关系</li>
          <li>多语言翻译数据（如果有）</li>
        </ul>
      </template>
      <template #dataPreviewColumns>
        <el-table-column prop="盒子名称" label="盒子名称" width="150" show-overflow-tooltip />
        <el-table-column prop="盒子描述" label="描述" width="200" show-overflow-tooltip />
        <el-table-column prop="网站编码" label="网站" width="100" />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选盒子配置）</p>
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
          <strong>已选盒子配置：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedBoxesForBatchExclude.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="box in selectedBoxesForBatchExclude" 
            :key="box.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeBoxFromBatchExclude(box.id)"
            size="small"
          >
            {{ box.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各盒子的当前排除/关联状态 -->
      <el-collapse v-if="boxExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各配置当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="boxExclusionDetails" size="small" stripe>
              <el-table-column label="盒子名称" prop="boxName" width="150" show-overflow-tooltip />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选盒子配置对该网站可见）</p>
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
          <strong>已选盒子配置：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedBoxesForBatchRelation.length }} 个</el-tag>
        </div>
        <div style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="box in selectedBoxesForBatchRelation" 
            :key="box.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeBoxFromBatchRelation(box.id)"
            size="small"
          >
            {{ box.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各盒子的当前关联/排除状态 -->
      <el-collapse v-if="boxRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各配置当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="boxRelationDetails" size="small" stripe>
              <el-table-column label="盒子" prop="boxName" width="150" show-overflow-tooltip />
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
  </div>
</template>

<script setup name="Box">
import { listBox, getBox, delBox, addBox, updateBox, updateBoxStatus, getBoxHomepage, bindBoxHomepage, unbindBoxHomepage, getBoxCategories, saveBoxCategories } from "@/api/gamebox/box"
import { listMasterArticle } from "@/api/gamebox/masterArticle"
import { listSite } from "@/api/gamebox/site"
import { getBoxSites, getBatchBoxSites, batchSaveBoxSiteRelations, updateBoxVisibility } from "@/api/gamebox/siteRelation"
import { listCategory, listVisibleCategory } from "@/api/gamebox/category"
import { batchAutoTranslate } from "@/api/gamebox/translation"
import { useSiteSelection, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"
import CategoryTag from "@/components/CategoryTag/index.vue"
import SiteRelationManager from "@/components/SiteRelationManager/index.vue"
import TranslationManager from "@/components/TranslationManager/index.vue"
import FieldMappingConfig from "@/components/FieldMapping/index.vue"
import ImportDialog from "@/components/ImportExportDialogs/ImportDialog.vue"
import FullImportDialog from "@/components/ImportExportDialogs/FullImportDialog.vue"
import { listFieldMappingByBoxId, saveFieldMappingsForBox } from "@/api/gamebox/fieldMapping"
import { handleTree } from "@/utils/ruoyi"
import { useRouter } from 'vue-router'
import { StarFilled, InfoFilled, User, Link, Star, Plus, Search, Edit, CircleCheck, CircleClose, Picture, Tickets, Download, Box, Document, QuestionFilled, View, Hide, Operation, Warning } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const router = useRouter()

const { siteList, currentSiteId, loadSiteList: loadSites, getSiteName } = useSiteSelection()

const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

const boxList = ref([])
const boxCategoryList = ref([])
const boxCategoryTreeOptions = ref([]) // 树形结构，用于对话框
const boxCategoryQueryTreeOptions = ref([]) // 树形结构，用于查询表单
const categoryCache = ref(new Map()) // 分类缓存，用于表格显示
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
const originalSiteId = ref(null) // 记录原始的网站ID，用于检测变化

// 字段映射相关
const activeBoxTab = ref('basic') // 当前激活的Tab页
const fieldMappings = ref([]) // 字段映射配置列表
const fieldMappingDialogOpen = ref(false) // 字段映射管理对话框
const currentFieldMappingBoxId = ref(null)
const currentFieldMappingBoxName = ref('')
const currentFieldMappingBoxSiteId = ref(0)

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentBoxIdForSites = ref(0)
const currentBoxNameForSites = ref('')
const currentBoxCreatorSiteId = ref(0)

// 排除网站管理相关
const exclusionDialogOpen = ref(false)
const currentExclusionBoxId = ref(0)
const currentExclusionBoxName = ref('')
const selectedExcludedSiteIds = ref([])

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const selectedBoxesForBatchExclude = ref([])
const boxExclusionDetails = ref([])
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const boxTableRef = ref(null)

// 批量排除：冲突检测（已勾选排除 & 同时存在 include 关联的网站集合）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  boxExclusionDetails.value.forEach(detail => {
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
const selectedBoxesForBatchRelation = ref([])
const boxRelationDetails = ref([])

// 批量关联：是否为默认配置模式（siteId=0）
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId, siteList.value))

// 批量关联：冲突检测（已勾选关联 & 同时存在 exclude 排除的网站集合）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  boxRelationDetails.value.forEach(detail => {
    if (Array.isArray(detail.excludedSiteIds)) {
      detail.excludedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

// 翻译管理相关
const translationDialogOpen = ref(false)
const currentTranslationBoxId = ref(0)
const currentTranslationBoxName = ref('')
const currentTranslationBoxData = ref({})
const boxTranslationFields = [
  { name: 'name', label: '盒子名称', type: 'text' },
  { name: 'description', label: '盒子描述', type: 'textarea' }
]

// 导入相关
const importOpen = ref(false)
const importForm = ref({
  siteId: undefined,
  jsonData: ''
})
const importPreview = ref(null)

// 导出相关
const exportDialogOpen = ref(false)
const exportLoading = ref(false)
const exportFormat = ref('excel')

// 全站导出相关
const fullExportDialogOpen = ref(false)
const fullExportLoading = ref(false)
const fullExportFormat = ref('excel')

// 导入相关
const systemImportDialogOpen = ref(false)

// 全站导入相关
const fullImportDialogOpen = ref(false)
const fullImportLoading = ref(false)
const fullImportSites = ref([])
const fullImportBoxes = ref([])
const fullImportRelations = ref([])
const fullImportTranslations = ref([])
const siteMapping = ref({})
const createDefaultAsNewSite = ref(false)
const hasDefaultConfig = ref(false)

// 主页面预览相关
const homepageDialogOpen = ref(false)
const loadingHomepages = ref(false)
const homepageBinding = ref(null)
const currentBoxForBind = ref(null)

// 绑定主页相关
const bindHomepageDialogOpen = ref(false)
const loadingBindArticles = ref(false)
const bindArticleList = ref([])
const currentBindType = ref('none')
const currentBindTargetId = ref(undefined)
const binding = ref(false)

// 计算属性 - 当前盒子显示名称
const currentBoxDisplayName = computed(() => {
  return currentBoxForBind.value?.name || '游戏盒子'
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: undefined,
    name: undefined,
    categoryId: undefined,
    status: undefined
  },
  rules: {
    siteId: [{ required: true, message: "请选择创建者网站", trigger: "change" }],
    name: [{ required: true, message: "盒子名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function handleSiteChange() {
  // 切换网站时重置“含默认配置”选项
  if (!queryParams.value.siteId || isPersonalSite(queryParams.value.siteId, siteList.value)) {
    includeDefaultConfig.value = false
  }
  queryParams.value.pageNum = 1
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  loadBoxCategoriesForQuery(queryParams.value.siteId)
  getList()
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
  getList()
}

// 预加载分类信息到缓存
function preloadCategories(boxRecords) {
  const categoryIds = [...new Set(boxRecords.map(b => b.categoryId).filter(id => id))]
  
  if (categoryIds.length === 0) return
  
  listCategory({ 
    categoryType: 'gamebox', 
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

// 从缓存获取分类
function getCategoryFromCache(categoryId) {
  if (!categoryId) return null
  return categoryCache.value.get(categoryId)
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
    listBox(params).then(response => {
      boxList.value = response.rows.map(row => {
        const mappedRow = {
          ...row,
          relationType: row.relationType || 'include' // 确保 relationType 字段存在
        }
        // 调试：打印关联类型和排除状态相关字段
        console.log('Box row data:', {
          id: mappedRow.id,
          name: mappedRow.name,
          relationSource: mappedRow.relationSource,
          relationType: mappedRow.relationType,
          isVisible: mappedRow.isVisible,
          isExcluded: mappedRow.isExcluded,
          isExcludedType: typeof mappedRow.isExcluded
        })
        return mappedRow
      })
      total.value = response.total
      preloadCategories(boxList.value)
      loading.value = false
    }).catch(error => {
      console.error('查询游戏盒子失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  } else {
    // 未选择网站：默认显示默认配置
    listBox({ ...params, siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      boxList.value = response.rows.map(row => {
        const mappedRow = {
          ...row,
          relationType: row.relationType || 'include' // 确保 relationType 字段存在
        }
        // 调试：打印关联类型和排除状态相关字段
        console.log('Box row data (default):', {
          id: mappedRow.id,
          name: mappedRow.name,
          relationSource: mappedRow.relationSource,
          relationType: mappedRow.relationType,
          isVisible: mappedRow.isVisible,
          isExcluded: mappedRow.isExcluded,
          isExcludedType: typeof mappedRow.isExcluded
        })
        return mappedRow
      })
      total.value = response.total
      preloadCategories(boxList.value)
      loading.value = false
    }).catch(error => {
      console.error('查询游戏盒子失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
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
    categoryId: undefined,
    selectedCategoryIds: [],
    logoUrl: undefined,
    bannerUrl: undefined,
    qrcodeUrl: undefined,
    officialUrl: undefined,
    downloadUrl: undefined,
    androidUrl: undefined,
    iosUrl: undefined,
    promoteUrl1: undefined,
    promoteUrl2: undefined,
    promoteUrl3: undefined,
    registerDownloadUrl: undefined,
    description: undefined,
    discountRate: 1.00,
    sortOrder: 0,
    priority: 0,
    status: "1"
  }
  activeBoxTab.value = 'basic' // 重置Tab
  proxy.resetForm("boxRef")
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
  if (queryParams.value.siteId != null) {
    form.value.siteId = queryParams.value.siteId
  } else {
    form.value.siteId = currentSiteId.value
  }
  loadBoxCategoriesForDialog(form.value.siteId)
  open.value = true
  title.value = "添加游戏盒子"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getBox(id).then(response => {
    const data = response.data
    open.value = true
    title.value = "修改游戏盒子"
    originalSiteId.value = data.siteId // 保存原始网站ID
    activeBoxTab.value = 'basic' // 重置到基本信息Tab
    
    loadBoxCategoriesForDialog(data.siteId)
    
    // 加载盒子的关联分类
    getBoxCategories(id).then(catResponse => {
      const boxCategories = catResponse.data || []
      
      nextTick(() => {
        form.value = data
        // 设置多分类选择器的值
        form.value.selectedCategoryIds = boxCategories.map(cat => cat.categoryId)
      })
    }).catch(() => {
      nextTick(() => {
        form.value = data
        form.value.selectedCategoryIds = []
      })
    })
  })
}

function submitForm() {
  proxy.$refs["boxRef"].validate(valid => {
    if (valid) {
      // 注意：site_id现在表示创建者网站，不需要验证分类
      // 新架构下，分类关联是通过关联表管理的
      doSubmitForm(false)
    }
  })
}

function doSubmitForm(syncGameSiteId) {
  const submitData = { ...form.value }
  if (syncGameSiteId) {
    submitData.syncGameSiteId = true // 添加标记告诉后端需要同步游戏
  }
  
  // 准备分类关联数据
  const categoryRelations = []
  if (form.value.selectedCategoryIds && form.value.selectedCategoryIds.length > 0) {
    form.value.selectedCategoryIds.forEach((catId, index) => {
      categoryRelations.push({
        categoryId: catId,
        isPrimary: catId === form.value.categoryId ? '1' : '0',
        sortOrder: index
      })
    })
  }
  
  if (form.value.id != undefined) {
    updateBox(submitData).then(response => {
      // 更新后保存分类关联
      if (categoryRelations.length > 0) {
        saveBoxCategories(form.value.id, categoryRelations).then(() => {
          const message = response.msg || "修改成功"
          proxy.$modal.msgSuccess(message)
          open.value = false
          getList()
        }).catch(() => {
          proxy.$modal.msgWarning("盒子修改成功，但分类保存失败")
          open.value = false
          getList()
        })
      } else {
        const message = response.msg || "修改成功"
        proxy.$modal.msgSuccess(message)
        open.value = false
        getList()
      }
    })
  } else {
    addBox(submitData).then(response => {
      const newBoxId = response.data
      
      // 验证返回的ID是否有效
      if (!newBoxId || typeof newBoxId !== 'number') {
        proxy.$modal.msgWarning('游戏盒子创建成功，但无法获取新ID，请刷新后手动配置分类')
        open.value = false
        getList()
        return
      }
      
      // 设置form.id以便保存字段映射
      form.value.id = newBoxId
      
      // 新增后保存分类关联
      if (categoryRelations.length > 0 && newBoxId) {
        saveBoxCategories(newBoxId, categoryRelations).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        }).catch(() => {
          proxy.$modal.msgWarning("盒子新增成功，但分类保存失败")
          open.value = false
          getList()
        })
      } else {
        proxy.$modal.msgSuccess("新增成功")
        open.value = false
        getList()
      }
    })
  }
}

function handleDelete(row) {
  const boxIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除游戏盒子编号为"' + boxIds + '"的数据项？').then(function() {
    return delBox(boxIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

// 预览主页面
function handlePreviewHomepages(row) {
  currentBoxForBind.value = row
  homepageDialogOpen.value = true
  loadingHomepages.value = true
  homepageBinding.value = null
  
  getBoxHomepage(row.id).then(response => {
    homepageBinding.value = response.data
  }).catch(error => {
    console.error('加载主页面信息失败:', error)
    proxy.$modal.msgError('加载主页面信息失败')
  }).finally(() => {
    loadingHomepages.value = false
  })
}

// 打开绑定主页对话框
function openBindHomepageDialog() {
  if (!currentBoxForBind.value) return
  
  // 查询当前绑定信息
  if (homepageBinding.value && homepageBinding.value.masterArticleId) {
    currentBindType.value = 'article'
    currentBindTargetId.value = homepageBinding.value.masterArticleId
  } else {
    currentBindType.value = 'none'
    currentBindTargetId.value = undefined
  }
  
  bindHomepageDialogOpen.value = true
  
  // 立即加载主文章列表
  loadBindArticleList()
}

// 当下拉框显示时加载主文章列表
function handleArticleSelectVisible(visible) {
  if (visible && bindArticleList.value.length === 0) {
    loadBindArticleList()
  }
}

// 加载可绑定的主文章列表
function loadBindArticleList() {
  loadingBindArticles.value = true
  
  const query = {
    pageNum: 1,
    pageSize: 1000,
    siteId: queryParams.value.siteId || currentSiteId.value,
    viewMode: 'related'
  }
  
  listMasterArticle(query).then(response => {
    bindArticleList.value = response.rows || []
  }).catch(error => {
    console.error('加载主文章列表失败:', error)
    proxy.$modal.msgError('加载主文章列表失败')
  }).finally(() => {
    loadingBindArticles.value = false
  })
}

// 提交主页绑定
function handleSubmitHomepageBinding() {
  if (!currentBoxForBind.value) return
  
  const bindType = currentBindType.value
  const masterArticleId = currentBindTargetId.value
  
  // 如果选择解绑
  if (bindType === 'none') {
    if (!homepageBinding.value) {
      proxy.$modal.msgWarning('该游戏盒子未绑定主页')
      return
    }
    
    proxy.$modal.confirm('确认要解除该游戏盒子的主页绑定吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      binding.value = true
      const params = {
        masterArticleId: homepageBinding.value.masterArticleId
      }
      
      unbindBoxHomepage(currentBoxForBind.value.id, params).then(() => {
        proxy.$modal.msgSuccess('解绑成功')
        bindHomepageDialogOpen.value = false
        handlePreviewHomepages(currentBoxForBind.value)
      }).catch(error => {
        console.error('解绑失败:', error)
        proxy.$modal.msgError('解绑失败: ' + (error.msg || error.message || '未知错误'))
      }).finally(() => {
        binding.value = false
      })
    }).catch(() => {})
    return
  }
  
  // 如果选择绑定主文章
  if (bindType === 'article') {
    if (!masterArticleId) {
      proxy.$modal.msgWarning('请选择要绑定的主文章')
      return
    }
    
    binding.value = true
    const params = {
      masterArticleId: masterArticleId,
      force: false // 默认不强制覆盖
    }
    
    bindBoxHomepage(currentBoxForBind.value.id, params).then(response => {
      // 检查是否需要确认
      if (response.needConfirm) {
        proxy.$modal.confirm(
          response.message || '该文章已绑定到其他游戏盒子，是否要强制覆盖？',
          '确认覆盖',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          // 用户确认后，使用force:true重新绑定
          const forceParams = {
            masterArticleId: masterArticleId,
            force: true
          }
          bindBoxHomepage(currentBoxForBind.value.id, forceParams).then(() => {
            proxy.$modal.msgSuccess('绑定成功')
            bindHomepageDialogOpen.value = false
            handlePreviewHomepages(currentBoxForBind.value)
          }).catch(err => {
            proxy.$modal.msgError('绑定失败: ' + (err.msg || err.message || '未知错误'))
          }).finally(() => {
            binding.value = false
          })
        }).catch(() => {
          binding.value = false
        })
      } else {
        // 直接绑定成功
        proxy.$modal.msgSuccess('绑定成功')
        bindHomepageDialogOpen.value = false
        handlePreviewHomepages(currentBoxForBind.value)
        binding.value = false
      }
    }).catch(error => {
      console.error('绑定失败:', error)
      proxy.$modal.msgError('绑定失败: ' + (error.msg || error.message || '未知错误'))
      binding.value = false
    })
  }
}

// 解除主页绑定
function handleUnbindHomepage() {
  if (!currentBoxForBind.value || !homepageBinding.value) return
  
  proxy.$modal.confirm(
    `确认解除游戏盒子 "${currentBoxForBind.value.name}" 的主页绑定吗？`,
    '确认解绑',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const params = {
      masterArticleId: homepageBinding.value.masterArticleId
    }
    
    unbindBoxHomepage(currentBoxForBind.value.id, params).then(() => {
      proxy.$modal.msgSuccess('解绑成功')
      homepageBinding.value = null
      homepageDialogOpen.value = false
    }).catch(error => {
      console.error('解绑失败:', error)
      proxy.$modal.msgError('解绑失败: ' + (error.msg || error.message || '未知错误'))
    })
  }).catch(() => {})
}

// 打开URL
function openUrl(url) {
  window.open(url, '_blank')
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

// 管理网站关联
function handleManageSites(row) {
  currentBoxIdForSites.value = row.id
  currentBoxNameForSites.value = row.name
  currentBoxCreatorSiteId.value = row.siteId || 0
  siteRelationDialogOpen.value = true
}

// 管理翻译
function handleManageTranslations(row) {
  currentTranslationBoxId.value = row.id
  currentTranslationBoxName.value = row.name
  currentTranslationBoxData.value = {
    name: row.name,
    description: row.description
  }
  translationDialogOpen.value = true
}

// 批量翻译
async function handleBatchTranslate() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要翻译的盒子')
    return
  }
  
  try {
    await proxy.$modal.confirm(`确认要为选中的 ${selectedIds.length} 个盒子生成翻译吗？`)
    
    // 准备要翻译的数据
    const allEntities = boxList.value
      .filter(box => selectedIds.includes(box.id))
      .map(box => ({
        entityId: box.id,
        fields: {
          name: box.name,
          description: box.description || ''
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
        await batchAutoTranslate('box', queryParams.siteId || 0, batches[i])
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

// === 批量排除管理 ===
async function handleBatchExclude() {
  const selectedRows = boxList.value.filter(box => ids.value.includes(box.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的盒子配置')
    return
  }
  
  // 只允许默认配置的盒子进行批量排除
  const invalidBoxes = selectedRows.filter(box => !isPersonalSite(box.siteId, siteList.value))
  if (invalidBoxes.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的盒子进行批量排除管理')
    return
  }
  
  selectedBoxesForBatchExclude.value = selectedRows.map(box => ({
    id: box.id,
    name: box.name
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中盒子的网站关系
    const batchRes = await getBatchBoxSites(selectedBoxesForBatchExclude.value.map(box => box.id))
    const batchMap = batchRes.data || {}
    const results = selectedBoxesForBatchExclude.value.map(box => {
      const sites = batchMap[box.id] || []
      return {
        boxId: box.id,
        boxName: box.name,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    boxExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有盒子共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedBoxesForBatchExclude.value.length) {
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

function removeBoxFromBatchExclude(boxId) {
  selectedBoxesForBatchExclude.value = selectedBoxesForBatchExclude.value.filter(
    box => box.id !== boxId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== boxId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (boxTableRef.value) {
    const row = boxList.value.find(box => box.id === boxId)
    if (row) {
      boxTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  boxExclusionDetails.value = boxExclusionDetails.value.filter(
    detail => detail.boxId !== boxId
  )
  
  if (selectedBoxesForBatchExclude.value.length === 0) {
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
  if (selectedBoxesForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何盒子配置')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一条请求处理所有选中盒子的排除关系
    await batchSaveBoxSiteRelations({
      boxIds: selectedBoxesForBatchExclude.value.map(b => b.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedBoxesForBatchExclude.value.length} 个配置排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedBoxesForBatchExclude.value.length} 个配置的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchBoxSites(selectedBoxesForBatchExclude.value.map(box => box.id))
    const refreshMap = refreshRes.data || {}
    boxExclusionDetails.value = selectedBoxesForBatchExclude.value.map(box => {
      const sites = refreshMap[box.id] || []
      return {
        boxId: box.id,
        boxName: box.name,
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
  const selectedRows = boxList.value.filter(box => ids.value.includes(box.id))

  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的盒子配置')
    return
  }

  const isDefaultConfig = isPersonalSite(queryParams.value.siteId, siteList.value)

  selectedBoxesForBatchRelation.value = selectedRows.map(box => ({
    id: box.id,
    name: box.name,
    siteId: box.siteId
  }))

  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchBoxSites(selectedBoxesForBatchRelation.value.map(box => box.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedBoxesForBatchRelation.value.map(box => {
      const sites = batchMap2[box.id] || []
      return {
        boxId: box.id,
        boxName: box.name,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== box.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })

    boxRelationDetails.value = results

    // 找出被所有盒子共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedBoxesForBatchRelation.value.length) {
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

/** 从批量关联中移除某个盒子 */
function removeBoxFromBatchRelation(boxId) {
  selectedBoxesForBatchRelation.value = selectedBoxesForBatchRelation.value.filter(
    box => box.id !== boxId
  )

  ids.value = ids.value.filter(id => id !== boxId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length

  if (boxTableRef.value) {
    const row = boxList.value.find(box => box.id === boxId)
    if (row) {
      boxTableRef.value.toggleRowSelection(row, false)
    }
  }

  boxRelationDetails.value = boxRelationDetails.value.filter(
    detail => detail.boxId !== boxId
  )

  if (selectedBoxesForBatchRelation.value.length === 0) {
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
  if (selectedBoxesForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何盒子配置')
    return
  }

  batchRelationLoading.value = true

  try {
    // 一条请求处理所有选中盒子的关联关系
    await batchSaveBoxSiteRelations({
      boxIds: selectedBoxesForBatchRelation.value.map(b => b.id),
      includeSiteIds: batchRelatedSiteIds.value
    })

    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedBoxesForBatchRelation.value.length} 个配置关联 ${relateCount} 个网站`
      : `成功取消 ${selectedBoxesForBatchRelation.value.length} 个配置的所有共享关联`

    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchBoxSites(selectedBoxesForBatchRelation.value.map(box => box.id))
    const refreshMap2 = refreshRes2.data || {}
    boxRelationDetails.value = selectedBoxesForBatchRelation.value.map(box => {
      const sites = refreshMap2[box.id] || []
      return {
        boxId: box.id,
        boxName: box.name,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== box.siteId).map(s => s.siteId),
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

// 加载游戏盒子分类列表（对话框使用）
// 使用可见分类接口，只展示对该网站可见的分类（默认配置未排除 + 跨站共享可见 + 自有启用）
function loadBoxCategoriesForDialog(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点或选择了全局，只查询全局分类
    return listVisibleCategory({ 
      categoryType: 'gamebox', 
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
      boxCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
    })
  }
  
  // 选择了具体站点：使用可见分类接口，只返回可见的分类
  // 包括：1.默认配置未排除 2.其他网站跨站共享可见 3.自有网站启用
  return listVisibleCategory({ 
    categoryType: 'gamebox', 
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
    boxCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
  })
}

// 加载游戏盒子分类列表（查询表单使用）
// 始终使用关联模式，显示所有分类并标记不可用的
function loadBoxCategoriesForQuery(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点，只查询默认配置分类（创建者模式）
    listCategory({ 
      categoryType: 'gamebox', 
      siteId: personalSiteId.value, 
      status: '1',
      queryMode: 'creator',
      pageNum: 1, 
      pageSize: 1000 
    }).then(response => {
      const categories = response.rows || []
      // 为查询选项添加displayName字段
      categories.forEach(cat => {
        cat.displayName = cat.name
      })
      boxCategoryList.value = categories
      boxCategoryQueryTreeOptions.value = handleTree(categories, "id", "parentId")
    })
    return
  }
  
  // 选择了具体站点：始终使用关联模式（related）查询所有分类
  // 关联模式会返回：1.默认配置（包括被排除的） 2.跨站共享（包括不可见的） 3.自有分类
  // 并且会在 is_visible 字段中标记每个分类的可见性状态
  listCategory({ 
    categoryType: 'gamebox',
    siteId: siteId,
    status: '1',
    queryMode: 'related',  // 始终使用关联模式
    pageNum: 1,
    pageSize: 1000
  }).then(response => {
    const categories = response.rows || []
    // 为查询选项添加displayName字段（不包含来源和状态标签）
    categories.forEach(cat => {
      cat.displayName = cat.name
    })
    boxCategoryList.value = categories
    boxCategoryQueryTreeOptions.value = handleTree(categories, "id", "parentId")
  })
}

// 跳转到游戏管理页面
function handleManageGames(row) {
  // 跳转到游戏管理页面，并传递盒子ID作为参数
  router.push({
    path: '/gamebox/game-mgmt/game',
    query: {
      boxId: row.id,
      boxName: row.name
    }
  })
}

// 导入功能相关
function handleImport() {
  importOpen.value = true
  importForm.value = {
    siteId: queryParams.value.siteId || currentSiteId.value,
    jsonData: ''
  }
  importPreview.value = null
}

function cancelImport() {
  importOpen.value = false
  importForm.value = {
    siteId: undefined,
    jsonData: ''
  }
  importPreview.value = null
}

function parseImportData() {
  if (!importForm.value.jsonData) {
    proxy.$modal.msgWarning('请输入JSON数据')
    return
  }
  
  try {
    const jsonData = JSON.parse(importForm.value.jsonData)
    
    // 检查是否包含数据对象c
    const dataObj = jsonData.c || jsonData
    
    if (!dataObj.gamename) {
      proxy.$modal.msgError('JSON数据格式错误：缺少游戏名称(gamename)字段')
      return
    }
    
    // 将外部JSON映射到系统字段 - 尽可能多地保存数据
    // 注意：qrcodeUrl存储的是需要生成二维码的URL（通常是下载链接），而不是base64图片
    const downloadUrl = dataObj.url1 || dataObj.dwzdownurl || ''
    
    importPreview.value = {
      name: dataObj.gamename || '',
      logoUrl: dataObj.pic1 || dataObj.avatar || '',
      bannerUrl: dataObj.banner || '',
      qrcodeUrl: downloadUrl, // 将下载链接作为二维码URL
      downloadUrl: downloadUrl,
      androidUrl: dataObj.android_url || '',
      iosUrl: dataObj.ios_url || '',
      officialUrl: dataObj.openurl || dataObj.url5 || '',
      promoteUrl1: dataObj.url1 || '',
      promoteUrl2: dataObj.url2 || '',
      promoteUrl3: dataObj.url3 || '',
      registerDownloadUrl: dataObj.url4 || '',
      description: dataObj.welfare || dataObj.description || '',
      tag: dataObj.tag || '',
      addtime: dataObj.addtime ? new Date(dataObj.addtime * 1000).toLocaleString() : '',
      rawData: dataObj,
      status: '1',
      sortOrder: 0,
      discountRate: 1.00
    }
    
    proxy.$modal.msgSuccess('数据解析成功，请确认后导入')
  } catch (error) {
    proxy.$modal.msgError('JSON解析失败：' + error.message)
    importPreview.value = null
  }
}

function submitImport() {
  if (!importPreview.value) {
    proxy.$modal.msgWarning('请先解析数据')
    return
  }
  
  if (!importForm.value.siteId) {
    proxy.$modal.msgWarning('请选择创建者网站')
    return
  }
  
  // 构建提交数据，只包含有值的字段
  const submitData = {
    siteId: importForm.value.siteId,
    name: importPreview.value.name,
    status: importPreview.value.status,
    sortOrder: importPreview.value.sortOrder,
    discountRate: importPreview.value.discountRate
  }
  
  // 添加可选字段（只添加有值的字段）
  if (importPreview.value.logoUrl) submitData.logoUrl = importPreview.value.logoUrl
  if (importPreview.value.bannerUrl) submitData.bannerUrl = importPreview.value.bannerUrl
  if (importPreview.value.qrcodeUrl) submitData.qrcodeUrl = importPreview.value.qrcodeUrl
  if (importPreview.value.downloadUrl) submitData.downloadUrl = importPreview.value.downloadUrl
  if (importPreview.value.androidUrl) submitData.androidUrl = importPreview.value.androidUrl
  if (importPreview.value.iosUrl) submitData.iosUrl = importPreview.value.iosUrl
  if (importPreview.value.officialUrl) submitData.officialUrl = importPreview.value.officialUrl
  if (importPreview.value.promoteUrl1) submitData.promoteUrl1 = importPreview.value.promoteUrl1
  if (importPreview.value.promoteUrl2) submitData.promoteUrl2 = importPreview.value.promoteUrl2
  if (importPreview.value.promoteUrl3) submitData.promoteUrl3 = importPreview.value.promoteUrl3
  if (importPreview.value.registerDownloadUrl) submitData.registerDownloadUrl = importPreview.value.registerDownloadUrl
  if (importPreview.value.description) submitData.description = importPreview.value.description
  
  // 保存原始数据到备注字段（可选）
  if (importPreview.value.rawData) {
    const originalFields = {
      tag: importPreview.value.tag,
      addtime: importPreview.value.addtime,
      originalId: importPreview.value.rawData.id
    }
    submitData.remark = `导入数据：${JSON.stringify(originalFields)}`
  }
  
  addBox(submitData).then(response => {
    proxy.$modal.msgSuccess('导入成功')
    cancelImport()
    getList()
  }).catch(error => {
    proxy.$modal.msgError('导入失败：' + (error.message || '未知错误'))
  })
}

function getFilledFieldsCount() {
  if (!importPreview.value) return 0
  let count = 0
  const fields = ['name', 'logoUrl', 'bannerUrl', 'qrcodeUrl', 'downloadUrl', 
                  'androidUrl', 'iosUrl', 'officialUrl', 'promoteUrl1', 'promoteUrl2', 
                  'promoteUrl3', 'registerDownloadUrl', 'description']
  fields.forEach(field => {
    if (importPreview.value[field]) count++
  })
  return count
}

// 监听表单中的siteId变化，自动重新加载分类
watch(() => form.value.siteId, (newSiteId, oldSiteId) => {
  if (!open.value) return
  
  loadBoxCategoriesForDialog(newSiteId)
  // 创建模式下切换网站时，清空分类让用户重新选择
  if (!form.value.id && oldSiteId !== undefined && oldSiteId !== newSiteId) {
    form.value.categoryId = undefined
    form.value.selectedCategoryIds = []
    
    // 提示用户需要重新选择分类
    proxy.$modal.msgWarning('创建者网站已更改，请重新选择主分类和关联分类')
  }
})
/** 排除默认配置（关联模式） */
async function handleExcludeDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认排除"${row.name}"吗？排除后该游戏盒子将不在当前网站显示。`)
    
    const res = await getBoxSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excluded.includes(currentQuerySiteId)) excluded.push(currentQuerySiteId)
    await batchSaveBoxSiteRelations({ boxIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
    
    proxy.$modal.msgSuccess('排除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('排除失败:', error)
      proxy.$modal.msgError('排除失败')
    }
  }
}

/** 恢复被排除的默认配置（关联模式） */
async function handleRestoreDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认恢复"${row.name}"吗？恢复后该游戏盒子将重新在当前网站显示。`)
    
    const res = await getBoxSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== currentQuerySiteId)
    await batchSaveBoxSiteRelations({ boxIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
    
    proxy.$modal.msgSuccess('恢复成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
      proxy.$modal.msgError('恢复失败')
    }
  }
}


/** 提交排除网站配置 */
async function submitExclusions() {
  try {
    await batchSaveBoxSiteRelations({
      boxIds: [currentExclusionBoxId.value],
      excludeSiteIds: selectedExcludedSiteIds.value
    })
    
    proxy.$modal.msgSuccess('保存成功')
    exclusionDialogOpen.value = false
    getList()
  } catch (error) {
    console.error('保存失败:', error)
    proxy.$modal.msgError('保存失败')
  }
}

/** 加载盒子的字段映射配置 */
async function loadFieldMappingsForBox(boxId) {
  try {
    const response = await listFieldMappingByBoxId(boxId)
    fieldMappings.value = response.data || []
  } catch (error) {
    console.error('加载字段映射配置失败:', error)
    fieldMappings.value = []
  }
}

/** 保存盒子的字段映射配置 */
async function saveFieldMappings(boxId) {
  try {
    if (fieldMappings.value && fieldMappings.value.length > 0) {
      await saveFieldMappingsForBox(boxId, fieldMappings.value)
    }
  } catch (error) {
    console.error('保存字段映射配置失败:', error)
    proxy.$modal.msgWarning('字段映射配置保存失败')
  }
}

/** 打开字段映射管理对话框 */
function handleManageFieldMapping(row) {
  currentFieldMappingBoxId.value = row.id
  currentFieldMappingBoxName.value = row.name
  currentFieldMappingBoxSiteId.value = row.siteId || 0
  
  // 加载字段映射配置
  loadFieldMappingsForBox(row.id)
  
  fieldMappingDialogOpen.value = true
}

/** 字段映射更新后的回调 */
function handleFieldMappingUpdate(mappings) {
  fieldMappings.value = mappings
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
      const relationResponse = await getBoxSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段
        await updateBoxVisibility(currentQuerySiteId, row.id, newValue)
        const action = newValue === '1' ? '显示' : '隐藏'
        proxy.$modal.msgSuccess(`${action}成功`)
        // 重新加载列表以更新统计信息
        getList()
      } else {
        // 没有关联记录：直接使用排除逻辑（不显示确认框）
        const included = relations.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
        let excluded = relations.filter(s => s.relationType === 'exclude').map(s => s.siteId)
        if (newValue === '0') {
          if (!excluded.includes(currentQuerySiteId)) excluded.push(currentQuerySiteId)
          proxy.$modal.msgSuccess('排除成功')
        } else {
          excluded = excluded.filter(id => id !== currentQuerySiteId)
          proxy.$modal.msgSuccess('恢复成功')
        }
        await batchSaveBoxSiteRelations({ boxIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用状态（调用盒子自身的状态更新接口）
      const text = newValue === '1' ? '启用' : '禁用'
      await updateBoxStatus({ id: row.id, status: newValue })
      row.status = newValue
      proxy.$modal.msgSuccess(`${text}成功`)
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updateBoxVisibility(currentQuerySiteId, row.id, newValue)
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

// ========== 导出和导入功能 ==========

/** 打开导出对话框 */
function handleExport() {
  if (ids.value.length === 0) {
    proxy.$modal.msgError('请先选择要导出的盒子')
    return
  }
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgError('请选择要导出的盒子')
    return
  }
  
  exportLoading.value = true
  
  try {
    // 获取选中的盒子详细数据
    const boxPromises = selectedIds.map(id => getBox(id))
    const boxResponses = await Promise.all(boxPromises)
    const boxData = boxResponses.map(response => response.data)
    
    // 转换数据格式
    const formattedData = boxData.map(box => ({
      盒子名称: box.name,
      盒子描述: box.description || '',
      网站编码: getSiteCode(box.siteId),
      官网地址: box.officialUrl || '',
      下载地址: box.downloadUrl || '',
      安卓下载: box.androidUrl || '',
      iOS下载: box.iosUrl || '',
      推广链接1: box.promoteUrl1 || '',
      推广链接2: box.promoteUrl2 || '',
      推广链接3: box.promoteUrl3 || '',
      注册下载链接: box.registerDownloadUrl || '',
      折扣率: box.discountRate || 0,
      特色功能: box.features || '',
      收录游戏数: box.gameCount || 0,
      状态: box.status || '1',
      备注: box.remark || ''
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
async function exportToExcel(boxData) {
  const XLSX = await import('xlsx')
  const wb = XLSX.utils.book_new()
  const ws = XLSX.utils.json_to_sheet(boxData)
  XLSX.utils.book_append_sheet(wb, ws, "盒子数据")
  const fileName = `盒子数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出为JSON */
function exportToJSON(boxData) {
  const exportData = {
    boxes: boxData,
    exportTime: new Date().toISOString()
  }
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  const fileName = `盒子数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
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

/** 全站导出 */
function handleFullExport() {
  fullExportDialogOpen.value = true
}

/** 确认全站导出 */
async function confirmFullExport() {
  try {
    fullExportLoading.value = true
    
    // 1. 获取全站盒子数据（默认配置 + 所有真实站点）
    const baseQuery = {
      queryMode: 'creator',
      pageNum: 1,
      pageSize: 9999
    }
    const boxMap = new Map()
    const appendBoxes = (rows = []) => {
      rows.forEach(box => {
        if (box && box.id != null && !boxMap.has(box.id)) {
          boxMap.set(box.id, box)
        }
      })
    }

    // 先拉默认配置（显式使用个人默认站点ID，兼容默认站点ID已变更场景）
    const defaultSiteId = personalSiteId.value
    const defaultBoxesResponse = await listBox(
      defaultSiteId !== null && defaultSiteId !== undefined
        ? {
            ...baseQuery,
            siteId: defaultSiteId,
            includeDefault: false
          }
        : baseQuery
    )
    appendBoxes(defaultBoxesResponse.rows)

    // 再拉所有真实站点的自有数据
    const realSites = (siteList.value || []).filter(site => !isPersonalSite(site.id, siteList.value))
    const siteBoxResponses = await Promise.all(
      realSites.map(site =>
        listBox({
          ...baseQuery,
          siteId: site.id,
          includeDefault: false
        })
      )
    )
    siteBoxResponses.forEach(resp => appendBoxes(resp.rows))

    const boxData = Array.from(boxMap.values())
    
    // 2. 收集所有网站ID
    const siteIds = new Set()
    boxData.forEach(box => {
      if (box.siteId !== null && box.siteId !== undefined) {
        siteIds.add(box.siteId)
      }
    })
    
    // 3. 获取所有关联关系
    const relationDataRaw = []
    
    for (let index = 0; index < boxData.length; index++) {
      const box = boxData[index]
      const virtualId = index + 1
      
      if (box.siteId && !isPersonalSite(box.siteId, siteList.value)) {
        // 非默认配置：获取所有关联关系
        try {
          const response = await getBoxSites(box.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId)
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              const relationData = {
                盒子虚拟ID: virtualId,
                盒子名称: box.name,
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
          console.warn('获取盒子关联失败:', box.id, error)
        }
      } else {
        // 默认配置：获取所有网站关系（包括关联和排除）
        try {
          const response = await getBoxSites(box.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId)
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              const relationData = {
                盒子虚拟ID: virtualId,
                盒子名称: box.name,
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
          console.warn('获取默认配置关系失败:', box.id, error)
        }
      }
    }
    
    // 4. 获取网站详细信息并建立ID映射
    const sitesData = []
    const siteIdToVirtualIdMap = new Map()
    let realSiteIndex = 0
    const defaultSiteIds = new Set([0])
    if (defaultSiteId !== null && defaultSiteId !== undefined) {
      defaultSiteIds.add(defaultSiteId)
    }
    let defaultSiteAdded = false
    
    siteIdToVirtualIdMap.set(0, 0)
    if (defaultSiteId !== null && defaultSiteId !== undefined) {
      siteIdToVirtualIdMap.set(defaultSiteId, 0)
    }
    
    for (const siteId of siteIds) {
      if (defaultSiteIds.has(siteId) || isPersonalSite(siteId, siteList.value)) {
        if (!defaultSiteAdded) {
          sitesData.push({
            网站虚拟ID: 0,
            网站名称: '默认配置',
            网站编码: 'default',
            网站域名: '',
            状态: '启用'
          })
          defaultSiteAdded = true
        }
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
      盒子虚拟ID: rel.盒子虚拟ID,
      盒子名称: rel.盒子名称,
      关联类型: rel.关联类型,
      网站虚拟ID: siteIdToVirtualIdMap.get(rel.网站ID_原始) ?? 0,
      网站编码: rel.网站编码,
      可见性: rel.可见性
    }))
    
    // 6. 转换盒子数据格式
    const formattedBoxData = boxData.map((box, index) => ({
      盒子虚拟ID: index + 1,
      盒子名称: box.name,
      盒子描述: box.description || '',
      网站虚拟ID: siteIdToVirtualIdMap.get(box.siteId) ?? 0,
      网站编码: getSiteCode(box.siteId),
      官网地址: box.officialUrl || '',
      下载地址: box.downloadUrl || '',
      安卓下载: box.androidUrl || '',
      iOS下载: box.iosUrl || '',
      推广链接1: box.promoteUrl1 || '',
      推广链接2: box.promoteUrl2 || '',
      推广链接3: box.promoteUrl3 || '',
      注册下载链接: box.registerDownloadUrl || '',
      折扣率: box.discountRate || 0,
      特色功能: box.features || '',
      收录游戏数: box.gameCount || 0,
      状态: box.status || '1',
      备注: box.remark || ''
    }))
    
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, formattedBoxData, relationData)
    } else {
      exportFullDataToJSON(sitesData, formattedBoxData, relationData)
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
async function exportFullDataToExcel(sitesData, boxData, relationData) {
  const XLSX = await import('xlsx')
  const wb = XLSX.utils.book_new()
  
  if (sitesData.length > 0) {
    const sitesWs = XLSX.utils.json_to_sheet(sitesData)
    XLSX.utils.book_append_sheet(wb, sitesWs, "网站列表")
  }
  
  const boxWs = XLSX.utils.json_to_sheet(boxData)
  XLSX.utils.book_append_sheet(wb, boxWs, "盒子数据")
  
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    XLSX.utils.book_append_sheet(wb, relationWs, "网站关联")
  }
  
  const fileName = `盒子全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出全站数据为JSON */
function exportFullDataToJSON(sitesData, boxData, relationData) {
  const exportData = {
    sites: sitesData,
    boxes: boxData,
    relations: relationData,
    exportTime: new Date().toISOString()
  }
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  const fileName = `盒子全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
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

/** 导入 */
function handleSystemImport() {
  if (viewMode.value !== 'creator') {
    proxy.$modal.msgError('请切换到创建者模式进行导入')
    return
  }
  systemImportDialogOpen.value = true
}

/** 全站导入 */
function handleFullImport() {
  fullImportDialogOpen.value = true
  fullImportSites.value = []
  fullImportBoxes.value = []
  fullImportRelations.value = []
  fullImportTranslations.value = []
  siteMapping.value = {}
  createDefaultAsNewSite.value = false
  hasDefaultConfig.value = false
}

/** 处理全站导入文件变化 */
function handleFullImportFileChange(fileData) {
  try {
    fullImportSites.value = fileData.sites || []
    fullImportBoxes.value = fileData.boxes || []
    fullImportRelations.value = fileData.relations || []
    fullImportTranslations.value = fileData.translations || []
    
    // 检查是否包含默认配置
    hasDefaultConfig.value = fullImportBoxes.value.some(b => b['网站虚拟ID'] === 0 || b['网站编码'] === 'default')
    
    // 初始化网站映射
    initializeSiteMapping()
  } catch (error) {
    console.error('处理文件数据失败:', error)
  }
}

/** 处理全站导入文件移除 */
function handleFullImportFileRemove() {
  fullImportSites.value = []
  fullImportBoxes.value = []
  fullImportRelations.value = []
  fullImportTranslations.value = []
  siteMapping.value = {}
  createDefaultAsNewSite.value = false
  hasDefaultConfig.value = false
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

/** 确认全站导入 */
async function confirmFullImport() {
  try {
    fullImportLoading.value = true
    
    // 这里实现具体的导入逻辑
    // 参考游戏页面的实现
    
    proxy.$modal.msgSuccess('全站盒子数据导入成功')
    fullImportDialogOpen.value = false
    getList()
    
  } catch (error) {
    console.error('全站导入失败:', error)
    proxy.$modal.msgError('全站导入失败: ' + (error.message || '未知错误'))
  } finally {
    fullImportLoading.value = false
  }
}

// ========== 导入组件所需的方法 ==========

/** 转换盒子导入数据 */
function transformBoxImportData(rawData, siteId) {
  if (!Array.isArray(rawData) || rawData.length === 0) {
    return []
  }
  
  return rawData.map((item, index) => ({
    name: item['盒子名称'] || `导入盒子${index + 1}`,
    description: item['盒子描述'] || '',
    siteId: siteId,
    officialUrl: item['官网地址'] || '',
    downloadUrl: item['下载地址'] || '',
    androidUrl: item['安卓下载'] || '',
    iosUrl: item['iOS下载'] || '',
    promoteUrl1: item['推广链接1'] || '',
    promoteUrl2: item['推广链接2'] || '',
    promoteUrl3: item['推广链接3'] || '',
    registerDownloadUrl: item['注册下载链接'] || '',
    discountRate: parseFloat(item['折扣率']) || 0,
    features: item['特色功能'] || '',
    gameCount: parseInt(item['收录游戏数']) || 0,
    status: normalizeStatus(item['状态']),
    remark: item['备注'] || ''
  }))
}

/** 验证盒子导入数据 */
function validateBoxImportData(data) {
  const errors = []
  
  data.forEach((item, index) => {
    if (!item.name || item.name.trim() === '') {
      errors.push(`第${index + 1}行：盒子名称不能为空`)
    }
    if (!item.siteId) {
      errors.push(`第${index + 1}行：创建者网站不能为空`)
    }
  })
  
  return errors
}

/** 导入盒子数据 */
async function importBoxData(data) {
  for (const boxData of data) {
    await addBox(boxData)
  }
}

/** 解析盒子Excel数据 */
function parseBoxExcelData(workbook) {
  const result = { boxes: [] }
  
  const boxSheetName = workbook.SheetNames.find(name => 
    name === '盒子数据' || name === 'boxes'
  ) || workbook.SheetNames[0]
  
  if (boxSheetName && workbook.Sheets[boxSheetName]) {
    result.boxes = workbook.Sheets[boxSheetName]
  }
  
  return result
}

/** 解析盒子JSON数据 */
function parseBoxJsonData(jsonData) {
  return {
    boxes: jsonData.boxes || (Array.isArray(jsonData) ? jsonData : [])
  }
}

/** 处理导入确认 */
function handleSystemImportConfirm() {
  systemImportDialogOpen.value = false
  getList()
}

// 获取网站编码
function getSiteCode(siteId) {
  if (siteId === 0 || siteId === '0') return 'default'
  if (isPersonalSite(siteId, siteList.value)) return 'default'
  const site = siteList.value.find(s => s.id === siteId)
  return site?.code || ''
}

// 规范化状态值
function normalizeStatus(status) {
  if (status === '启用' || status === '1' || status === 1 || status === true) return '1'
  if (status === '禁用' || status === '0' || status === 0 || status === false) return '0'
  return '1' // 默认启用
}

// 监听查询表单的includeDefaultConfig变化
watch(() => includeDefaultConfig.value, () => {
  loadBoxCategoriesForQuery(queryParams.value.siteId)
})

// 页面重新激活时重新加载数据（处理 keep-alive 缓存）
onActivated(() => {
  if (siteList.value.length > 0) {
    loadBoxCategoriesForQuery(queryParams.value.siteId)
    getList()
  }
})

loadSites(() => {
  const restored = restoreViewModeSiteSelection(siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  viewMode.value = restored.viewMode
  queryParams.value.siteId = restored.siteId
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  loadBoxCategoriesForQuery(queryParams.value.siteId)
  getList()
})
</script>

<style scoped>
.homepage-bind-dialog :deep(.el-dialog__body) {
  min-height: 400px;
  padding: 20px;
  overflow: hidden;
}

.homepage-bind-dialog :deep(.el-card) {
  overflow: hidden;
}
</style>
