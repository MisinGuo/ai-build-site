<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="站点名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入站点名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="站点编码" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入站点编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-tree-select
          v-model="queryParams.categoryId"
          :data="websiteCategoryQueryOptions"
          placeholder="请选择分类"
          clearable
          filterable
          style="width: 200px"
          node-key="id"
          :props="{ label: 'name', children: 'children' }"
        />
      </el-form-item>
      <el-form-item label="站点类型" prop="siteFunctionType">
        <el-select v-model="queryParams.siteFunctionType" placeholder="请选择站点类型" clearable style="width: 160px">
          <el-option label="获客站" value="seo_site" />
          <el-option label="落地站" value="landing_site" />
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
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:site:add']">新建站点</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:site:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['gamebox:site:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="MagicStick" :disabled="multiple" @click="handleBatchTranslate" v-hasPermi="['gamebox:site:edit']">批量翻译</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Download" :disabled="multiple" @click="handleExport" v-hasPermi="['gamebox:site:export']">导出数据</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Upload" @click="handleImport" v-hasPermi="['gamebox:site:add']">导入数据</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="siteList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="站点名称" align="center" prop="name" min-width="180" :show-overflow-tooltip="false">
        <template #default="scope">
          <div style="display:flex;align-items:center;justify-content:center;gap:4px;white-space:nowrap;overflow:hidden">
            <span style="overflow:hidden;text-overflow:ellipsis;flex-shrink:1;min-width:0">{{ scope.row.name }}</span>
            <el-tag v-if="scope.row.isDefault === 1" type="warning" size="small" style="flex-shrink:0">默认</el-tag>
            <el-tag v-if="scope.row.isPersonal === 1" type="info" size="small" style="flex-shrink:0">个人</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="站点编码" align="center" prop="code" width="120" />
      <el-table-column label="站点类型" align="center" prop="siteFunctionType" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.siteFunctionType === 'seo_site'" type="primary" size="small">获客站</el-tag>
          <el-tag v-else-if="scope.row.siteFunctionType === 'landing_site'" type="success" size="small">落地站</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="建站状态" align="center" prop="setupStatus" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.setupStatus === 'created'" type="info" size="small">⏳ 初始化中</el-tag>
          <el-tag v-else-if="scope.row.setupStatus === 'template_cloned'" type="warning" size="small">⏳ 代码准备中</el-tag>
          <el-tag v-else-if="scope.row.setupStatus === 'code_pulled'" type="primary" size="small">🚀 部署中</el-tag>
          <el-tag v-else-if="scope.row.setupStatus === 'configured'" type="primary" size="small">🚀 部署中</el-tag>
          <el-tag v-else-if="scope.row.setupStatus === 'deployed'" type="success" size="small">✅ 已上线</el-tag>
          <el-tag v-else-if="scope.row.setupStatus === 'setup_failed'" type="danger" size="small">❌ 初始化失败</el-tag>
          <span v-else style="color:#c0c4cc">-</span>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="categoryId" width="120">
        <template #default="scope">
          <CategoryTag v-if="scope.row.categoryName" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'website', icon: scope.row.categoryIcon }" size="small" />
          <el-tag v-else type="info" size="small">未分类</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="站点域名" align="center" prop="domain" min-width="160" :show-overflow-tooltip="true" />
      <el-table-column label="网站描述" align="center" prop="description" :show-overflow-tooltip="true" width="160" v-if="false" />
      <el-table-column label="SEO标题" align="center" prop="seoTitle" :show-overflow-tooltip="true" width="160" v-if="false" />
      <el-table-column label="SEO描述" align="center" prop="seoDescription" :show-overflow-tooltip="true" width="160" v-if="false" />
      <el-table-column label="默认语言" align="center" prop="defaultLocale" width="90" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
            {{ scope.row.status === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <el-tooltip content="修改" placement="top">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:site:edit']" />
          </el-tooltip>
          <el-tooltip :content="scope.row.isDefault === 1 ? '默认站点不支持代码管理' : '代码管理'" placement="top">
            <el-button link type="success" icon="Monitor" @click="handleCodeManage(scope.row)" v-hasPermi="['gamebox:codeManage:query']" :disabled="scope.row.isDefault === 1" />
          </el-tooltip>
          <el-tooltip :content="scope.row.isDefault === 1 ? '默认站点不支持Git配置' : 'Git 配置'" placement="top">
            <el-button link type="primary" icon="Connection" @click="handleOpenGitConfig(scope.row)" v-hasPermi="['gamebox:codeManage:edit']" :disabled="scope.row.isDefault === 1" />
          </el-tooltip>
          <el-tooltip v-if="scope.row.templateId" :content="scope.row.isDefault === 1 ? '默认站点不支持从模板重建代码' : '从模板重建代码'" placement="top">
            <el-button link type="warning" icon="RefreshLeft" @click="handleRebuildFromTemplate(scope.row)" v-hasPermi="['gamebox:codeManage:edit']" :disabled="scope.row.isDefault === 1" />
          </el-tooltip>
          <el-tooltip v-if="scope.row.setupStatus === 'template_cloned'" :content="scope.row.isDefault === 1 ? '默认站点不支持推送代码' : '推送代码到远程仓库'" placement="top">
            <el-button link type="warning" icon="Upload" :loading="pushingIds.has(scope.row.id)" @click="handleRetryPush(scope.row)" v-hasPermi="['gamebox:codeManage:edit']" :disabled="scope.row.isDefault === 1" />
          </el-tooltip>
          <el-tooltip content="翻译" placement="top">
            <el-button link type="warning" icon="ChatDotRound" @click="handleManageTranslations(scope.row)" v-hasPermi="['gamebox:site:edit']" />
          </el-tooltip>
          <el-tooltip :content="scope.row.isPersonal === 1 ? '个人默认站点不可删除' : '删除'" placement="top">
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:site:remove']" :disabled="scope.row.isPersonal === 1" />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新建站点向导 -->
    <SiteCreationWizard v-model="wizardOpen" @success="handleWizardSuccess" />

    <!-- 翻译管理对话框 -->
    <TranslationManager
      v-model="translationDialogOpen"
      entity-type="site"
      :entity-id="currentTranslationSiteId"
      :entity-name="currentTranslationSiteName"
      :site-id="currentTranslationSiteId"
      :translation-fields="siteTranslationFields"
      :original-data="currentTranslationSiteData"
      @refresh="getList"
    />

    <!-- 添加或修改站点对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="siteRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="站点名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入站点名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="站点编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入站点编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="站点类型" prop="siteFunctionType">
              <el-select v-model="form.siteFunctionType" placeholder="请选择站点类型" style="width:100%" clearable>
                <el-option label="获客站 (SEO自动获客)" value="seo_site" />
                <el-option label="落地站 (广告落地页)" value="landing_site" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建站状态">
              <el-tag v-if="form.setupStatus === 'created'" type="info" size="small">⏳ 初始化中</el-tag>
              <el-tag v-else-if="form.setupStatus === 'template_cloned'" type="warning" size="small">⏳ 代码准备中</el-tag>
              <el-tag v-else-if="form.setupStatus === 'code_pulled'" type="primary" size="small">🚀 部署中</el-tag>
              <el-tag v-else-if="form.setupStatus === 'configured'" type="primary" size="small">🚀 部署中</el-tag>
              <el-tag v-else-if="form.setupStatus === 'deployed'" type="success" size="small">✅ 已上线</el-tag>
              <el-tag v-else-if="form.setupStatus === 'setup_failed'" type="danger" size="small">❌ 初始化失败</el-tag>
              <span v-else style="color:#c0c4cc;font-size:13px">未初始化</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="站点域名" prop="domain">
              <el-input v-model="form.domain" placeholder="请输入站点域名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="网站描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入网站描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="SEO标题" prop="seoTitle">
              <el-input v-model="form.seoTitle" placeholder="请输入SEO标题" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="SEO关键词" prop="seoKeywords">
              <el-input v-model="form.seoKeywords" placeholder="请输入SEO关键词，多个关键词用逗号分隔" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="SEO描述" prop="seoDescription">
              <el-input v-model="form.seoDescription" type="textarea" :rows="3" placeholder="请输入SEO描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="分类" prop="categoryId">
              <el-tree-select
                v-model="form.categoryId"
                :data="websiteCategoryTreeOptions"
                placeholder="请选择分类"
                clearable
                filterable
                style="width: 100%"
                node-key="id"
                :props="{ label: 'name', children: 'children' }"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="默认语言" prop="defaultLocale">
              <el-select v-model="form.defaultLocale" placeholder="请选择默认语言">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="繁体中文" value="zh-TW" />
                <el-option label="英语" value="en-US" />
                <el-option label="日语" value="ja-JP" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="支持的语言" prop="supportedLocales">
              <el-select v-model="form.supportedLocales" placeholder="请选择支持的语言（可多选）" multiple style="width: 100%">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="繁体中文" value="zh-TW" />
                <el-option label="英语" value="en-US" />
                <el-option label="日语" value="ja-JP" />
              </el-select>
              <span style="color: #909399; font-size: 12px;">提示：支持的语言列表将用于多语言文章主页绑定</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="1">启用</el-radio>
                <el-radio value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="站点模板" prop="templateId">
              <el-select v-model="form.templateId" placeholder="请选择模板" clearable style="width:100%">
                <el-option v-for="t in templateList" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
              <div style="font-size:12px;color:#909399;margin-top:2px">决定环境配置面板展示的变量列表</div>
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

    <!-- 数据导出对话框 -->
    <el-dialog title="导出数据" v-model="exportDialogOpen" width="400px" append-to-body>
      <div style="text-align: center; margin: 20px 0;">
        <p style="margin-bottom: 20px; color: #606266;">
          已选择 <strong style="color: #409EFF;">{{ ids.length }}</strong> 个网站数据
        </p>
        
        <el-radio-group v-model="exportFormat" style="display: flex; flex-direction: column; align-items: flex-start;">
          <el-radio value="excel" style="margin-bottom: 15px;">
            <el-icon style="margin-right: 8px;"><Document /></el-icon>
            导出为 Excel 文件 (.xlsx)
          </el-radio>
          <el-radio value="json">
            <el-icon style="margin-right: 8px;"><Files /></el-icon>
            导出为 JSON 文件 (.json)
          </el-radio>
        </el-radio-group>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="exportDialogOpen = false">取 消</el-button>
          <el-button type="primary" @click="confirmExport" :loading="exportLoading">
            <el-icon style="margin-right: 5px;"><Download /></el-icon>
            开始导出
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 数据导入对话框 -->
    <el-dialog title="导入数据" v-model="importDialogOpen" width="500px" append-to-body>
      <div style="margin: 20px 0;">
        <el-alert 
          title="导入说明" 
          type="info" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #default>
            <div style="font-size: 13px; line-height: 1.5;">
              <p>• 支持导入 Excel (.xlsx) 或 JSON (.json) 格式文件</p>
              <p>• 导入的数据将创建为新的网站记录</p>
              <p>• 请确保文件格式与导出的数据格式一致</p>
              <p>• 必填字段：站点名称、站点编码</p>
            </div>
          </template>
        </el-alert>
        
        <el-upload
          ref="importUpload"
          :auto-upload="false"
          :show-file-list="true"
          :limit="1"
          accept=".xlsx,.json"
          :on-change="handleFileChange"
          :before-remove="handleFileRemove"
          drag
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 .xlsx 和 .json 格式文件，文件大小不超过10MB
            </div>
          </template>
        </el-upload>
        
        <div v-if="importPreviewData.length > 0" style="margin-top: 20px;">
          <el-divider content-position="left">数据预览（前5条）</el-divider>
          <el-table :data="importPreviewData.slice(0, 5)" border size="small" max-height="300">
            <el-table-column prop="name" label="站点名称" width="150" show-overflow-tooltip />
            <el-table-column prop="code" label="站点编码" width="120" show-overflow-tooltip />
            <el-table-column prop="domain" label="站点域名" width="200" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80" />
            <el-table-column label="其他字段" width="100">
              <template #default>
                <el-tag size="small" type="info">{{ Object.keys(importPreviewData[0] || {}).length }} 个字段</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <p style="margin-top: 10px; color: #909399; font-size: 13px;">
            共 {{ importPreviewData.length }} 条数据待导入
          </p>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelImport">取 消</el-button>
          <el-button type="primary" @click="confirmImport" :loading="importLoading" :disabled="importPreviewData.length === 0">
            <el-icon style="margin-right: 5px;"><Upload /></el-icon>
            确认导入 ({{ importPreviewData.length }})
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Git 配置对话框 -->
    <el-dialog v-model="gitConfigOpen" title="Git 仓库配置" width="520px" append-to-body :close-on-click-modal="false">
      <el-form :model="gitConfigForm" ref="gitConfigFormRef" :rules="gitConfigRules" label-width="90px">
        <el-form-item label="Git 账号" prop="gitAccountId">
          <el-select
            v-model="gitConfigForm.gitAccountId"
            placeholder="请选择 Git 账号"
            style="width:100%"
            v-loading="gitAccountsLoading"
          >
            <el-option
              v-for="a in gitAccounts"
              :key="a.id"
              :label="`${a.name}（${a.provider} / ${a.account}）`"
              :value="a.id"
            />
          </el-select>
          <div v-if="gitAccounts.length === 0 && !gitAccountsLoading" style="font-size:12px;color:#e6a23c;margin-top:4px">
            暂无可用账号，请先在「基础配置 → Git账号管理」中添加
          </div>
        </el-form-item>
        <el-form-item label="仓库方式">
          <el-radio-group v-model="gitConfigForm.autoCreate">
            <el-radio :value="false">填写已有仓库 URL</el-radio>
            <el-radio :value="true">自动新建仓库</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="!gitConfigForm.autoCreate">
          <el-form-item label="仓库地址" prop="gitRepoUrl">
            <el-input v-model="gitConfigForm.gitRepoUrl" placeholder="https://github.com/yourname/repo.git" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="仓库名" prop="repoName">
            <el-input v-model="gitConfigForm.repoName" placeholder="由账号所有者自动创建" />
            <div style="font-size:12px;color:#909399;margin-top:4px">留空将使用站点编码作为仓库名</div>
          </el-form-item>
          <el-form-item label="可见性">
            <el-radio-group v-model="gitConfigForm.repoPrivate">
              <el-radio :value="true">私有（推荐）</el-radio>
              <el-radio :value="false">公开</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        <el-form-item label="分支">
          <el-input v-model="gitConfigForm.gitBranch" placeholder="main" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gitConfigOpen = false">取消</el-button>
        <el-button type="primary" :loading="savingGitConfig" @click="handleSaveGitConfig">保存配置</el-button>
      </template>
    </el-dialog>

    <!-- 从模板重建代码对话框 -->
    <el-dialog v-model="rebuildOpen" title="从模板重建站点代码" width="520px" append-to-body :close-on-click-modal="false">
      <el-alert type="error" :closable="false" style="margin-bottom:16px">
        <template #default>
          <div style="font-size:13px;line-height:1.7">
            <b>危险操作：</b>此操作将重新从模板克隆代码并强制推送到远程仓库，<b>仓库中现有的代码修改将全部覆盖</b>，请务必确认代码已有备份再继续。
          </div>
        </template>
      </el-alert>
      <el-descriptions :column="2" border size="small" style="margin-bottom:16px">
        <el-descriptions-item label="站点">{{ rebuildSiteRow?.name }}</el-descriptions-item>
        <el-descriptions-item label="使用模板">
          {{ templateList.find(t => t.id === rebuildSiteRow?.templateId)?.name || '模板 #' + rebuildSiteRow?.templateId }}
        </el-descriptions-item>
      </el-descriptions>
      <el-form :model="rebuildForm" label-width="90px">
        <el-form-item label="Git 账号" required>
          <el-select
            v-model="rebuildForm.gitAccountId"
            placeholder="请选择 Git 账号"
            style="width:100%"
            v-loading="gitAccountsLoading"
          >
            <el-option
              v-for="a in gitAccounts"
              :key="a.id"
              :label="`${a.name}（${a.provider} / ${a.account}）`"
              :value="a.id"
            />
          </el-select>
          <div v-if="gitAccounts.length === 0 && !gitAccountsLoading" style="font-size:12px;color:#e6a23c;margin-top:4px">
            暂无可用账号，请先在「基础配置 → Git账号管理」中添加
          </div>
        </el-form-item>
        <el-form-item label="仓库方式">
          <el-radio-group v-model="rebuildForm.autoCreate">
            <el-radio :value="false">推送到已有仓库</el-radio>
            <el-radio :value="true">自动新建仓库</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="!rebuildForm.autoCreate">
          <el-form-item label="仓库地址">
            <div v-if="rebuildForm.gitRepoUrl" style="font-size:13px;color:#303133;word-break:break-all">
              {{ rebuildForm.gitRepoUrl }}
            </div>
            <div v-else style="font-size:13px;color:#e6a23c">
              未配置仓库地址，请先在站点配置中绑定 Git 仓库
            </div>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="仓库名">
            <el-input v-model="rebuildForm.repoName" :placeholder="rebuildSiteRow?.code || 'my-site'" />
            <div style="font-size:12px;color:#909399;margin-top:4px">留空将使用站点编码作为仓库名</div>
          </el-form-item>
          <el-form-item label="可见性">
            <el-radio-group v-model="rebuildForm.repoPrivate">
              <el-radio :value="true">私有（推荐）</el-radio>
              <el-radio :value="false">公开</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        <el-form-item label="分支">
          <el-input v-model="rebuildForm.gitBranch" placeholder="main" />
        </el-form-item>
      </el-form>
      <el-alert v-if="rebuildFinished" :type="rebuildSuccess ? 'success' : 'warning'" :closable="false" style="margin-top:12px">
        {{ rebuildSuccess ? '重建成功！请展开日志查看详情。' : '重建未全部完成，请查看日志。' }}
      </el-alert>
      <!-- 日志区（有日志时常驻展示，自动滚底） -->
      <div v-if="rebuildLog" style="margin-top:12px">
        <div style="font-size:12px;color:#606266;margin-bottom:4px">
          操作日志{{ rebuildLoading ? '（实时）' : '' }}：
        </div>
        <pre ref="rebuildLogPreRef" style="font-size:12px;max-height:220px;overflow:auto;white-space:pre-wrap;background:#f5f7fa;padding:8px;border-radius:4px;border:1px solid #ebeef5">{{ rebuildLog }}</pre>
      </div>
      <template #footer>
        <el-button v-if="!rebuildFinished" @click="rebuildOpen = false" :disabled="rebuildLoading">取消</el-button>
        <el-button v-if="rebuildFinished" @click="rebuildOpen = false">关闭</el-button>
        <el-button v-if="!rebuildFinished" type="danger" :loading="rebuildLoading" @click="handleConfirmRebuild">确认重建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Site">
import { useRouter } from 'vue-router'
import SiteCreationWizard from './SiteCreationWizard.vue'
import { listSite, getSite, delSite, addSite, updateSite } from "@/api/gamebox/site"
import { listSiteTemplates } from "@/api/gamebox/siteTemplate"
import { listCategory } from "@/api/gamebox/category"
import { batchAutoTranslate, getEntityTranslations, batchSaveTranslations } from "@/api/gamebox/translation"
import { getCodeManageInfo, saveGitConfig, saveDeployConfig, gitPushOnly, setupFromTemplate } from "@/api/gamebox/codeManage"
import { listEnabledGitAccounts } from "@/api/gamebox/gitAccount"
import CategoryTag from "@/components/CategoryTag/index.vue"
import TranslationManager from "@/components/TranslationManager/index.vue"
import { handleTree } from '@/utils/ruoyi'
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const router = useRouter()

// 工具函数：字段名转换
/** 获取字段显示名称（英文转中文） */
function getFieldDisplayName(fieldName) {
  const fieldNameMap = {
    'name': '站点名称',
    'description': '网站描述', 
    'seoTitle': 'SEO标题',
    'seoKeywords': 'SEO关键词',
    'seoDescription': 'SEO描述',
    'remark': '备注'
  }
  return fieldNameMap[fieldName] || fieldName
}

/** 从显示名称获取字段名（中文转英文） */
function getFieldNameFromDisplay(displayName) {
  const displayNameMap = {
    '站点名称': 'name',
    '网站描述': 'description',
    'SEO标题': 'seoTitle',
    'SEO关键词': 'seoKeywords', 
    'SEO描述': 'seoDescription',
    '备注': 'remark'
  }
  return displayNameMap[displayName] || displayName
}

const siteList = ref([])
const templateList = ref([])
const websiteCategoryList = ref([])
const websiteCategoryTreeOptions = ref([])
const websiteCategoryQueryOptions = ref([])
const open = ref(false)
const wizardOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 翻译管理相关
const translationDialogOpen = ref(false)
const currentTranslationSiteId = ref(0)
const currentTranslationSiteName = ref('')
const currentTranslationSiteData = ref({})

// 导出管理相关
const exportDialogOpen = ref(false)
const exportFormat = ref('excel')
const exportLoading = ref(false)

// 导入管理相关
const importDialogOpen = ref(false)
const importLoading = ref(false)
const importPreviewData = ref([])
const importFile = ref(null)
const siteTranslationFields = [
  { name: 'name', label: '网站名称', type: 'text' },
  { name: 'description', label: '网站描述', type: 'textarea' },
  { name: 'seoTitle', label: 'SEO标题', type: 'text' },
  { name: 'seoKeywords', label: 'SEO关键词', type: 'text' },
  { name: 'seoDescription', label: 'SEO描述', type: 'textarea' },
  { name: 'remark', label: '备注', type: 'textarea' }
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: undefined,
    code: undefined,
    categoryId: undefined,
    siteFunctionType: undefined,
    status: undefined
  },
  rules: {
    name: [{ required: true, message: "站点名称不能为空", trigger: "blur" }],
    code: [{ required: true, message: "站点编码不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listSite(queryParams.value).then(response => {
    siteList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  const userStore = useUserStore()
  form.value = {
    id: undefined,
    userId: userStore.id,
    name: undefined,
    code: undefined,
    domain: undefined,
    description: undefined,
    seoTitle: undefined,
    seoKeywords: undefined,
    seoDescription: undefined,
    categoryId: undefined,
    templateId: undefined,
    siteFunctionType: undefined,
    defaultLocale: "zh-CN",
    supportedLocales: [],
    sortOrder: 0,
    status: "1",
    remark: undefined
  }
  proxy.resetForm("siteRef")
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

function loadTemplates() {
  if (templateList.value.length > 0) return
  listSiteTemplates({ pageNum: 1, pageSize: 100 }).then(res => {
    templateList.value = res.rows || []
  })
}

function handleAdd() {
  wizardOpen.value = true
}

function handleWizardSuccess() {
  getList()
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getSite(id).then(response => {
    const data = response.data
    
    // 解析supportedLocales JSON字符串为数组
    if (data.supportedLocales) {
      try {
        data.supportedLocales = JSON.parse(data.supportedLocales)
      } catch (e) {
        console.error('解析supportedLocales失败:', e)
        data.supportedLocales = []
      }
    } else {
      data.supportedLocales = []
    }
    
    // 先设置除了categoryId外的所有字段
    form.value = { ...data, categoryId: undefined }
    loadTemplates()
    
    // 等待分类加载完成后再设置categoryId
    loadWebsiteCategoriesForDialog().then(() => {
      nextTick(() => {
        form.value.categoryId = data.categoryId
      })
    })
    
    open.value = true
    title.value = "修改站点"
  })
}

function submitForm() {
  proxy.$refs["siteRef"].validate(valid => {
    if (valid) {
      // 准备提交的数据，将supportedLocales数组转换为JSON字符串
      const submitData = {
        ...form.value,
        supportedLocales: form.value.supportedLocales && form.value.supportedLocales.length > 0
          ? JSON.stringify(form.value.supportedLocales)
          : null
      }
      
      if (form.value.id != undefined) {
        updateSite(submitData).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSite(submitData).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 代码管理 */
function handleCodeManage(row) {
  router.push({ path: '/gamebox/config/code-manage', query: { siteId: row.id } })
}

/** 重新推送代码（template_cloned 状态时快捷推送） */
const pushingIds = ref(new Set())
async function handleRetryPush(row) {
  if (pushingIds.value.has(row.id)) return
  pushingIds.value = new Set([...pushingIds.value, row.id])
  try {
    const res = await gitPushOnly(row.id)
    if (res.data?.success) {
      proxy.$modal.msgSuccess('推送成功！')
      getList()
    } else {
      proxy.$modal.msgError('推送失败：' + (res.data?.message || '请检查 Git 仓库地址及 Token 是否正确，并确保远程仓库已创建'))
    }
  } catch (e) {
    proxy.$modal.msgError('推送异常：' + (e.message || String(e)))
  } finally {
    const next = new Set(pushingIds.value)
    next.delete(row.id)
    pushingIds.value = next
  }
}

/** 打开 Git 配置弹窗 */
const gitConfigOpen = ref(false)
const savingGitConfig = ref(false)
const gitConfigFormRef = ref(null)
const gitConfigSiteId = ref(null)
const gitConfigOriginalUrl = ref('')
const gitConfigForm = ref({ gitAccountId: null, autoCreate: false, gitRepoUrl: '', repoName: '', repoPrivate: true, gitBranch: 'main' })
const gitConfigRules = {
  gitAccountId: [{ required: true, message: '请选择 Git 账号', trigger: 'change' }]
}

// 平台 Git 账号列表（用于 Git配置 / 重建 对话框）
const gitAccounts = ref([])
const gitAccountsLoading = ref(false)

// 从模板重建
const rebuildOpen = ref(false)
const rebuildLoading = ref(false)
const rebuildFinished = ref(false)
const rebuildSuccess = ref(false)
const rebuildSiteRow = ref(null)
const rebuildLog = ref('')
const rebuildLogPreRef = ref(null)
const rebuildForm = ref({ gitAccountId: null, autoCreate: false, gitRepoUrl: '', repoName: '', repoPrivate: true, gitBranch: 'main' })

// 日志有新内容时自动滚到底部
watch(rebuildLog, () => {
  nextTick(() => {
    if (rebuildLogPreRef.value) {
      rebuildLogPreRef.value.scrollTop = rebuildLogPreRef.value.scrollHeight
    }
  })
})
function loadGitAccounts() {
  if (gitAccounts.value.length > 0) return
  gitAccountsLoading.value = true
  listEnabledGitAccounts().then(res => {
    gitAccounts.value = res.data || []
  }).catch(() => {}).finally(() => { gitAccountsLoading.value = false })
}

async function handleOpenGitConfig(row) {
  loadGitAccounts()
  gitConfigSiteId.value = row.id
  gitConfigForm.value = { gitAccountId: null, autoCreate: false, gitRepoUrl: '', repoName: '', repoPrivate: true, gitBranch: 'main' }
  gitConfigOriginalUrl.value = ''
  try {
    const res = await getCodeManageInfo(row.id)
    if (res.data) {
      gitConfigForm.value = {
        gitAccountId: res.data.gitAccountId || null,
        autoCreate: false,
        gitRepoUrl: res.data.gitRepoUrl || '',
        repoName: '',
        repoPrivate: true,
        gitBranch: res.data.gitBranch || 'main'
      }
      gitConfigOriginalUrl.value = res.data.gitRepoUrl || ''
    }
  } catch {}
  gitConfigOpen.value = true
}

async function handleSaveGitConfig() {
  const valid = await gitConfigFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingGitConfig.value = true
  try {
    const f = gitConfigForm.value
    const isRebinding = !f.autoCreate && gitConfigOriginalUrl.value
      && f.gitRepoUrl && f.gitRepoUrl !== gitConfigOriginalUrl.value
    if (isRebinding) {
      try {
        await proxy.$modal.confirm(
          `您修改了仓库地址，系统将重置站点为「⏳ 代码准备中」状态，请在保存后点击推送按钮。\n\n旧仓库：${gitConfigOriginalUrl.value}\n新仓库：${f.gitRepoUrl}\n\n是否继续？`
        )
      } catch {
        savingGitConfig.value = false
        return
      }
    }
    const payload = {
      gitAccountId: f.gitAccountId,
      gitRepoUrl: f.autoCreate ? '' : f.gitRepoUrl,
      gitBranch: f.gitBranch || 'main',
      gitAutoSync: '0',
      autoCreate: f.autoCreate,
      repoName: f.repoName,
      repoPrivate: f.repoPrivate
    }
    const res = await saveGitConfig(gitConfigSiteId.value, payload)
    if (res.code === 200) {
      if (res.data?.createdRepoUrl) {
        proxy.$modal.msgSuccess(`仓库创建成功：${res.data.createdRepoUrl}`)
      } else {
        proxy.$modal.msgSuccess(res.data?.message || res.data || 'Git 配置已保存')
      }
      gitConfigOpen.value = false
      getList()
    } else {
      proxy.$modal.msgError(res.msg || '保存失败')
    }
  } finally {
    savingGitConfig.value = false
  }
}

/** 从模板重建站点代码 */
async function handleRebuildFromTemplate(row) {
  loadGitAccounts()
  loadTemplates()
  rebuildSiteRow.value = row
  rebuildLog.value = ''
  rebuildFinished.value = false
  rebuildSuccess.value = false
  rebuildForm.value = { gitAccountId: null, autoCreate: false, gitRepoUrl: '', repoName: row.code || '', repoPrivate: true, gitBranch: 'main' }
  // 读取已有 Git 配置，预填账号和仓库地址
  try {
    const res = await getCodeManageInfo(row.id)
    if (res.data) {
      rebuildForm.value.gitAccountId = res.data.gitAccountId || null
      rebuildForm.value.gitRepoUrl = res.data.gitRepoUrl || ''
      rebuildForm.value.gitBranch = res.data.gitBranch || 'main'
    }
  } catch {}
  rebuildOpen.value = true
}

async function handleConfirmRebuild() {
  if (!rebuildForm.value.gitAccountId) {
    proxy.$modal.msgWarning('请选择 Git 账号')
    return
  }
  if (!rebuildForm.value.autoCreate && !rebuildForm.value.gitRepoUrl) {
    proxy.$modal.msgWarning('该站点尚未配置 Git 仓库地址，请先在站点配置中绑定仓库，或切换为「自动新建仓库」模式')
    return
  }
  const row = rebuildSiteRow.value
  try {
    await proxy.$modal.confirm(
      `确认要从模板重建「${row.name}」的站点代码吗？\n\n警告：此操作将重新从模板克隆代码并推送到远程仓库，原有仓库中的代码修改将被覆盖！`
    )
  } catch { return }

  rebuildLoading.value = true
  rebuildLog.value = '正在提交重建任务...\n'
  try {
    const f = rebuildForm.value
    // 1. 提交异步任务，立即获得 taskId
    const startRes = await setupFromTemplate(row.id, {
      templateId: row.templateId,
      domain: row.domain || '',
      defaultLocale: row.defaultLocale || 'zh-CN',
      gitAccountId: f.gitAccountId,
      repoName: f.autoCreate ? (f.repoName || row.code || 'my-site') : '',
      repoPrivate: f.repoPrivate,
      gitRepoUrl: !f.autoCreate ? f.gitRepoUrl : '',
      gitBranch: f.gitBranch || 'main',
      rebuildMode: true
    })
    const taskId = startRes.data?.taskId
    if (!taskId) {
      proxy.$modal.msgError('启动重建任务失败，未获取到 taskId')
      return
    }
    rebuildLog.value = `任务已提交（taskId: ${taskId}），正在通过 WebSocket 接收日志...\n`

    // 2. 通过 WebSocket 接收实时日志（替代轮询）
    await new Promise((resolve) => {
      const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
      const ws = new WebSocket(`${wsProtocol}//${window.location.host}/ws/rebuild-log/${taskId}`)
      ws.onmessage = (event) => {
        try {
          const msg = JSON.parse(event.data)
          if (msg.type === 'log') {
            rebuildLog.value += msg.data
          } else if (msg.type === 'done') {
            rebuildFinished.value = true
            rebuildSuccess.value = !!msg.success
            if (msg.success) {
              getList()
            }
            ws.close()
            resolve()
          }
        } catch (_) { /* 消息解析失败忽略 */ }
      }
      ws.onerror = () => {
        rebuildLog.value += '\n[WebSocket 连接异常，请刷新页面查看结果]\n'
        rebuildFinished.value = true
        rebuildSuccess.value = false
        resolve()
      }
      ws.onclose = () => {
        // 连接关闭时如果还未收到 done 帧（如服务端主动断开），也标记完成
        if (!rebuildFinished.value) {
          rebuildFinished.value = true
          resolve()
        }
      }
    })
  } catch (e) {
    proxy.$modal.msgError('重建失败：' + (e.message || String(e)))
  } finally {
    rebuildLoading.value = false
  }
}

function handleDelete(row) {
  // 个人默认站点不允许删除
  if (row.id && row.isPersonal === 1) {
    proxy.$modal.msgWarning('个人默认站点不可删除')
    return
  }
  const siteIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除站点编号为"' + siteIds + '"的数据项？').then(function() {
    return delSite(siteIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 管理翻译 */
function handleManageTranslations(row) {
  currentTranslationSiteId.value = row.id
  currentTranslationSiteName.value = row.name
  currentTranslationSiteData.value = {
    name: row.name,
    description: row.description,
    seoTitle: row.seoTitle,
    seoKeywords: row.seoKeywords,
    seoDescription: row.seoDescription,
    remark: row.remark
  }
  translationDialogOpen.value = true
}

/** 批量翻译 */
async function handleBatchTranslate() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要翻译的网站')
    return
  }
  
  try {
    await proxy.$modal.confirm(`确认要为选中的 ${selectedIds.length} 个网站生成翻译吗？`)
    
    // 准备要翻译的数据
    const allEntities = siteList.value
      .filter(site => selectedIds.includes(site.id))
      .map(site => {
        // 过滤掉空字段，避免无效翻译
        const fields = {}
        if (site.name) fields.name = site.name
        if (site.description) fields.description = site.description
        if (site.seoTitle) fields.seoTitle = site.seoTitle
        if (site.seoKeywords) fields.seoKeywords = site.seoKeywords
        if (site.seoDescription) fields.seoDescription = site.seoDescription
        if (site.remark) fields.remark = site.remark
        
        return {
          siteId: site.id,  // 每个网站使用自己的ID
          entityId: site.id,
          fields: fields
        }
      })
      .filter(entity => Object.keys(entity.fields).length > 0)  // 过滤掉没有可翻译字段的实体
    
    const loading = proxy.$loading({
      lock: true,
      text: `正在批量生成翻译，请稍候...（共 ${allEntities.length} 个网站）`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      // 批量处理：将所有网站分组，每次最多处理5个避免超时
      const batchSize = 5
      let successCount = 0
      let failCount = 0
      
      for (let i = 0; i < allEntities.length; i += batchSize) {
        const batch = allEntities.slice(i, i + batchSize)
        
        // 并行处理这一批网站
        const promises = batch.map(entity => 
          batchAutoTranslate('site', entity.siteId, [{
            entityId: entity.entityId,
            fields: entity.fields
          }]).then(() => {
            successCount++
            return { success: true, entityId: entity.entityId }
          }).catch(error => {
            console.error(`网站 ${entity.entityId} 翻译失败:`, error)
            failCount++
            return { success: false, entityId: entity.entityId, error }
          })
        )
        
        await Promise.all(promises)
        
        // 更新进度
        const processed = Math.min(i + batchSize, allEntities.length)
        loading.setText(`正在批量生成翻译... ${processed}/${allEntities.length}`)
      }
      
      loading.close()
      
      if (failCount > 0) {
        proxy.$modal.msgWarning(`批量翻译完成：成功${successCount}个，失败${failCount}个`)
      } else {
        proxy.$modal.msgSuccess(`批量翻译完成：共${successCount}个`)
      }
    } catch (error) {
      loading.close()
      if (error !== 'cancel') {
        console.error('批量翻译失败:', error)
        proxy.$modal.msgError('批量翻译失败: ' + (error.message || '未知错误'))
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量翻译操作失败:', error)
      proxy.$modal.msgError('批量翻译操作失败')
    }
  }
}

/** 导出数据 */
function handleExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要导出的网站')
    return
  }
  
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  const selectedIds = ids.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要导出的网站')
    return
  }
  
  exportLoading.value = true
  
  try {
    // 获取选中的网站数据
    const exportData = siteList.value.filter(site => selectedIds.includes(site.id))
    
    // 获取翻译数据 + 代码配置
    const exportDataWithTranslations = await Promise.all(
      exportData.map(async (site) => {
        let translations = {}
        let codeManage = null
        try {
          const translationResponse = await getEntityTranslations('site', site.id)
          if (translationResponse && translationResponse.data) {
            translations = translationResponse.data
          }
        } catch (error) {
          console.warn(`获取网站 ${site.id} 的翻译数据失败:`, error)
        }
        try {
          const codeManageRes = await getCodeManageInfo(site.id)
          if (codeManageRes && codeManageRes.data) {
            codeManage = codeManageRes.data
          }
        } catch (error) {
          console.warn(`获取网站 ${site.id} 的代码配置失败:`, error)
        }
        return { ...site, translations, codeManage }
      })
    )

    // 处理数据，解析JSON字段和格式化
    const processedData = exportDataWithTranslations.map(site => {
      let supportedLocales = []
      if (site.supportedLocales) {
        try {
          supportedLocales = JSON.parse(site.supportedLocales)
        } catch (e) {
          supportedLocales = []
        }
      }
      
      return {
        虚拟ID: 'SITE_' + site.id,
        站点名称: site.name,
        站点编码: site.code,
        站点域名: site.domain,
        网站描述: site.description,
        SEO标题: site.seoTitle,
        SEO关键词: site.seoKeywords,
        SEO描述: site.seoDescription,
        分类: site.categoryName || '未分类',
        默认语言: site.defaultLocale,
        支持的语言: Array.isArray(supportedLocales) ? supportedLocales.join(', ') : '',
        排序: site.sortOrder,
        状态: site.status === '1' ? '启用' : '禁用',
        创建时间: site.createTime,
        更新时间: site.updateTime,
        备注: site.remark
      }
    })

    // 准备翻译数据
    const translationData = []
    exportDataWithTranslations.forEach(site => {
      if (site.translations && Array.isArray(site.translations)) {
        // 翻译数据是数组格式：[{fieldName, locale, fieldValue}, ...]
        site.translations.forEach(translationItem => {
          if (translationItem.fieldName && translationItem.locale && translationItem.fieldValue) {
            const translationRow = {
              虚拟ID: 'SITE_' + site.id,
              字段名: getFieldDisplayName(translationItem.fieldName), // 使用中文显示名称
              语言代码: translationItem.locale,
              翻译值: translationItem.fieldValue
            }
            translationData.push(translationRow)
          }
        })
      } else if (site.translations && typeof site.translations === 'object') {
        // 兼容嵌套对象格式：{locale: {fieldName: value}}
        Object.entries(site.translations).forEach(([locale, fields]) => {
          if (fields && typeof fields === 'object') {
            Object.entries(fields).forEach(([fieldName, value]) => {
              if (value !== null && value !== undefined && value !== '') {
                const translationItem = {
                  虚拟ID: 'SITE_' + site.id,
                  字段名: getFieldDisplayName(fieldName), // 使用中文显示名称
                  语言代码: locale,
                  翻译值: value
                }
                translationData.push(translationItem)
              }
            })
          }
        })
      }
    })

    // 处理代码管理配置数据
    const codeConfigData = exportDataWithTranslations
      .filter(site => site.codeManage)
      .map(site => {
        const cm = site.codeManage
        return {
          虚拟ID: 'SITE_' + site.id,
          Git提供商: cm.gitProvider ?? null,
          仓库地址: cm.gitRepoUrl ?? null,
          分支: cm.gitBranch ?? 'main',
          访问令牌: cm.gitToken ?? null,
          自动同步: cm.gitAutoSync ?? '0',
          部署平台: cm.deployPlatform ?? null,
          CF账户ID: cm.cloudflareAccountId ?? null,
          CF_API令牌: cm.cloudflareApiToken ?? null,
          CF项目名称: cm.cloudflareProjectName ?? null,
          CF项目ID: cm.cloudflareProjectId ?? null,
          自定义域名: cm.customDomains ? JSON.stringify(cm.customDomains) : null,
          部署配置JSON: cm.deployConfig ?? null
        }
      })

    if (exportFormat.value === 'excel') {
      await exportToExcel(processedData, translationData, codeConfigData)
    } else {
      await exportToJSON(processedData, translationData, codeConfigData)
    }
    
    proxy.$modal.msgSuccess(`成功导出 ${exportData.length} 条网站数据（包含翻译和代码配置）`)
    exportDialogOpen.value = false
    
  } catch (error) {
    console.error('导出数据失败:', error)
    proxy.$modal.msgError('导出数据失败: ' + (error.message || '未知错误'))
  } finally {
    exportLoading.value = false
  }
}

/** 导出为Excel */
async function exportToExcel(siteData, translationData, codeConfigData = []) {
  // 动态导入 xlsx 库
  const XLSX = await import('xlsx')
  
  // 创建工作簿
  const wb = XLSX.utils.book_new()
  
  // 创建网站数据工作表
  const siteWs = XLSX.utils.json_to_sheet(siteData)
  
  // 设置网站数据列宽
  const siteColWidths = [
    { wch: 12 },  // 虚拟ID
    { wch: 20 },  // 站点名称
    { wch: 15 },  // 站点编码
    { wch: 30 },  // 站点域名
    { wch: 30 },  // 网站描述
    { wch: 30 },  // SEO标题
    { wch: 30 },  // SEO关键词
    { wch: 30 },  // SEO描述
    { wch: 15 },  // 分类
    { wch: 12 },  // 默认语言
    { wch: 20 },  // 支持的语言
    { wch: 8 },   // 排序
    { wch: 10 },  // 状态
    { wch: 20 },  // 创建时间
    { wch: 20 },  // 更新时间
    { wch: 30 }   // 备注
  ]
  siteWs['!cols'] = siteColWidths
  
  // 添加网站数据工作表
  XLSX.utils.book_append_sheet(wb, siteWs, "网站数据")
  
  // 创建翻译数据工作表（如果有翻译数据）
  if (translationData.length > 0) {
    const translationWs = XLSX.utils.json_to_sheet(translationData)
    
    // 设置翻译数据列宽
    const translationColWidths = [
      { wch: 12 },  // 虚拟ID
      { wch: 15 },  // 字段名
      { wch: 12 },  // 语言代码
      { wch: 40 }   // 翻译值
    ]
    translationWs['!cols'] = translationColWidths
    
    // 添加翻译数据工作表
    XLSX.utils.book_append_sheet(wb, translationWs, "翻译数据")
  }

  // 添加代码配置工作表（如果有）
  if (codeConfigData.length > 0) {
    const codeConfigWs = XLSX.utils.json_to_sheet(codeConfigData)
    const codeConfigColWidths = [
      { wch: 12 },  // 虚拟ID
      { wch: 10 },  // Git提供商
      { wch: 50 },  // 仓库地址
      { wch: 12 },  // 分支
      { wch: 50 },  // 访问令牌
      { wch: 10 },  // 自动同步
      { wch: 20 },  // 部署平台
      { wch: 35 },  // CF账户ID
      { wch: 50 },  // CF_API令牌
      { wch: 20 },  // CF项目名称
      { wch: 20 },  // CF项目ID
      { wch: 30 },  // 自定义域名
      { wch: 80 }   // 部署配置JSON
    ]
    codeConfigWs['!cols'] = codeConfigColWidths
    XLSX.utils.book_append_sheet(wb, codeConfigWs, '代码配置')
  }
  
  // 生成文件名
  const fileName = `网站数据_完整_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  
  // 导出文件
  XLSX.writeFile(wb, fileName)
}

/** 导出为JSON */
function exportToJSON(siteData, translationData, codeConfigData = []) {
  // 使用与Excel相同的数据结构：网站数据 + 翻译数据 + 代码配置
  const exportData = {
    sites: siteData,
    translations: translationData,
    codeConfigs: codeConfigData
  }
  
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  
  const fileName = `网站数据_完整_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
  
  // 创建下载链接
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.display = 'none'
  
  document.body.appendChild(link)
  link.click()
  
  // 清理
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

// 加载网站分类列表（查询表单用，只加载个人站点配置）
function loadWebsiteCategoriesForQuery() {
  const personalSiteId = siteList.value.find(s => s.isPersonal === 1)?.id || null
  listCategory({ categoryType: 'website', siteId: personalSiteId, status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
    let categories = response.rows || []
    // 添加图标和全局标签
    categories = categories.map(cat => ({
      ...cat,
      name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [全局]`
    }))
    websiteCategoryQueryOptions.value = handleTree(categories, 'id', 'parentId')
  }).catch(error => {
    console.error('加载网站分类失败:', error)
    websiteCategoryQueryOptions.value = []
  })
}

// 加载网站分类列表（编辑对话框用，只加载个人站点配置）
function loadWebsiteCategoriesForDialog() {
  const personalSiteId = siteList.value.find(s => s.isPersonal === 1)?.id || null
  return listCategory({ categoryType: 'website', siteId: personalSiteId, status: '1', pageNum: 1, pageSize: 1000 }).then(response => {
    let categories = response.rows || []
    // 添加图标和全局标签
    categories = categories.map(cat => ({
      ...cat,
      name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} [全局]`
    }))
    websiteCategoryTreeOptions.value = handleTree(categories, 'id', 'parentId')
    return Promise.resolve()
  }).catch(error => {
    console.error('加载网站分类失败:', error)
    websiteCategoryTreeOptions.value = []
    return Promise.resolve()
  })
}

loadWebsiteCategoriesForQuery()
getList()

/** 导入数据 */
function handleImport() {
  importDialogOpen.value = true
  importPreviewData.value = []
  importFile.value = null
}

/** 处理文件选择 */
async function handleFileChange(file) {
  importFile.value = file
  
  try {
    // 确保分类数据已加载
    await loadWebsiteCategoriesForDialog()
    
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.name.endsWith('.xlsx')) {
      const excelData = await parseExcelData(fileData)
      parsedData = excelData.sites || []
      
      // 如果有翻译数据，合并到网站数据中
      if (excelData.translations && excelData.translations.length > 0) {
        parsedData = mergeTranslationsToSites(parsedData, excelData.translations)
      }
      // 如果有代码配置，合并到网站数据中
      if (excelData.codeConfigs && excelData.codeConfigs.length > 0) {
        parsedData = mergeCodeConfigToSites(parsedData, excelData.codeConfigs)
      }
    } else {
      const jsonData = parseJSONData(fileData)
      parsedData = jsonData.sites || []
      
      // 如果有翻译数据，合并到网站数据中
      if (jsonData.translations && jsonData.translations.length > 0) {
        parsedData = mergeTranslationsToSites(parsedData, jsonData.translations)
      }
      // 如果有代码配置，合并到网站数据中
      if (jsonData.codeConfigs && jsonData.codeConfigs.length > 0) {
        parsedData = mergeCodeConfigToSites(parsedData, jsonData.codeConfigs)
      }
    }
    
    // 验证和转换数据格式
    importPreviewData.value = validateAndTransformImportData(parsedData)
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + (error.message || '未知错误'))
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
  
  const result = { sites: [], translations: [], codeConfigs: [] }
  
  // 解析网站数据（第一个sheet或名为"网站数据"的sheet）
  const siteSheetName = workbook.SheetNames.find(name => 
    name === '网站数据' || name === 'sites' || name === 'site'
  ) || workbook.SheetNames[0]
  
  if (siteSheetName && workbook.Sheets[siteSheetName]) {
    result.sites = XLSX.utils.sheet_to_json(workbook.Sheets[siteSheetName])
  }
  
  // 解析翻译数据（名为"翻译数据"的sheet）
  const translationSheetName = workbook.SheetNames.find(name => 
    name === '翻译数据' || name === 'translations' || name === 'translation'
  )
  
  if (translationSheetName && workbook.Sheets[translationSheetName]) {
    result.translations = XLSX.utils.sheet_to_json(workbook.Sheets[translationSheetName])
  }

  // 解析代码配置数据（名为"代码配置"的sheet）
  const codeConfigSheetName = workbook.SheetNames.find(name =>
    name === '代码配置' || name === 'codeConfigs' || name === 'code-config'
  )

  if (codeConfigSheetName && workbook.Sheets[codeConfigSheetName]) {
    result.codeConfigs = XLSX.utils.sheet_to_json(workbook.Sheets[codeConfigSheetName])
  }
  
  return result
}

/** 解析JSON数据 */
function parseJSONData(jsonString) {
  try {
    const data = JSON.parse(jsonString)
    
    // JSON导出的数据格式：{ sites: [...], translations: [...], codeConfigs: [...] }
    if (data.sites && Array.isArray(data.sites)) {
      return {
        sites: data.sites,
        translations: data.translations || [],
        codeConfigs: data.codeConfigs || []
      }
    }
    
    // 兼容直接是网站数组的格式
    if (Array.isArray(data)) {
      return {
        sites: data,
        translations: [],
        codeConfigs: []
      }
    }
    
    // 兼容单个网站对象
    return {
      sites: [data],
      translations: [],
      codeConfigs: []
    }
  } catch (error) {
    throw new Error('JSON格式不正确')
  }
}

/** 合并代码配置到网站数据 */
function mergeCodeConfigToSites(sites, codeConfigs) {
  if (!codeConfigs || codeConfigs.length === 0) return sites
  const configByVirtualId = {}
  codeConfigs.forEach(cfg => {
    const vid = cfg['虚拟ID'] || cfg.virtualId
    if (vid) configByVirtualId[String(vid)] = cfg
  })
  return sites.map(site => {
    const vid = String(site['虚拟ID'] || site.virtualId || '')
    const cfg = configByVirtualId[vid]
    if (cfg) return { ...site, codeConfig: cfg }
    return site
  })
}

/** 合并翻译数据到网站数据 */
function mergeTranslationsToSites(sites, translations) {
  // 按虚拟ID分组翻译数据
  const translationsByVirtualId = {}
  
  translations.forEach(trans => {
    const vid = trans['虚拟ID'] || trans.virtualId
    const fieldDisplayName = trans['字段名'] || trans['fieldName'] || trans.fieldName
    const locale = trans['语言代码'] || trans['locale'] || trans.locale
    const value = trans['翻译值'] || trans['value'] || trans.value || trans.fieldValue
    
    // 将中文显示名称转换为英文字段名
    const fieldName = getFieldNameFromDisplay(fieldDisplayName)
    
    if (vid && fieldName && locale && value) {
      if (!translationsByVirtualId[String(vid)]) {
        translationsByVirtualId[String(vid)] = []
      }
      // 使用数组格式存储翻译数据
      translationsByVirtualId[String(vid)].push({
        fieldName: fieldName,
        locale: locale,
        fieldValue: value
      })
    }
  })
  
  // 将翻译数据合并到对应的网站数据中
  return sites.map(site => {
    const vid = String(site['虚拟ID'] || site.virtualId || '')
    const translations = translationsByVirtualId[vid]
    
    if (translations && translations.length > 0) {
      return { ...site, translations }
    }
    return site
  })
}

/** 验证和转换导入数据 */
function validateAndTransformImportData(rawData) {
  if (!Array.isArray(rawData) || rawData.length === 0) {
    throw new Error('文件中没有找到有效数据')
  }
  
  // 获取分类名称到ID的映射
  const categoryNameToId = {}
  websiteCategoryTreeOptions.value.forEach(category => {
    const cleanName = category.name.replace(/\s*\[全局\]$/, '').replace(/^[^\s]*\s/, '') // 去除图标和[全局]标记
    categoryNameToId[cleanName] = category.id
    categoryNameToId[category.name] = category.id // 也支持完整名称匹配
  })
  
  return rawData.map((item, index) => {
    
    // 支持中英文字段名映射
    const fieldMapping = {
      '站点名称': 'name',
      'name': 'name',
      '站点编码': 'code', 
      'code': 'code',
      '站点域名': 'domain',
      'domain': 'domain',
      '网站描述': 'description',
      'description': 'description',
      'SEO标题': 'seoTitle',
      'seoTitle': 'seoTitle',
      'SEO关键词': 'seoKeywords',
      'seoKeywords': 'seoKeywords',
      'SEO描述': 'seoDescription',
      'seoDescription': 'seoDescription',
      '分类': 'categoryName',
      'categoryName': 'categoryName',
      '默认语言': 'defaultLocale',
      'defaultLocale': 'defaultLocale',
      '支持的语言': 'supportedLocales',
      'supportedLocales': 'supportedLocales',
      '排序': 'sortOrder',
      'sortOrder': 'sortOrder',
      '状态': 'status',
      'status': 'status',
      '备注': 'remark',
      'remark': 'remark'
    }
    
    const transformedItem = {}
    
    // 处理基本字段
    Object.entries(item).forEach(([key, value]) => {
      // 跳过translations字段，稍后单独处理
      if (key === 'translations') {
        return
      }
      
      const mappedKey = fieldMapping[key] || key
      if (mappedKey && value !== null && value !== undefined && value !== '') {
        transformedItem[mappedKey] = value
      }
    })
    
    // 验证必填字段
    if (!transformedItem.name || !transformedItem.code) {
      throw new Error(`第${index + 1}行数据缺少必填字段（站点名称、站点编码）`)
    }
    
    // 处理分类映射
    let categoryId = null
    if (transformedItem.categoryName && transformedItem.categoryName !== '未分类') {
      // 尝试多种匹配方式
      const categoryName = transformedItem.categoryName.trim()
      categoryId = categoryNameToId[categoryName] 
        || categoryNameToId[categoryName.replace(/^[^\s]*\s/, '')] // 去除图标
        || categoryNameToId[categoryName.replace(/\s*\[全局\]$/, '')] // 去除[全局]
        || null
    }
    
    // 设置默认值
    const result = {
      name: transformedItem.name,
      code: transformedItem.code,
      domain: transformedItem.domain || '',
      description: transformedItem.description || '',
      seoTitle: transformedItem.seoTitle || '',
      seoKeywords: transformedItem.seoKeywords || '',
      seoDescription: transformedItem.seoDescription || '',
      categoryId: categoryId, // 使用映射后的分类ID
      categoryName: transformedItem.categoryName || '',
      defaultLocale: transformedItem.defaultLocale || 'zh-CN',
      supportedLocales: transformedItem.supportedLocales || '',
      sortOrder: parseInt(transformedItem.sortOrder) || 0,
      status: transformedItem.status === '启用' || transformedItem.status === '1' ? '1' : '0',
      remark: transformedItem.remark || ''
    }
    
    // 添加翻译数据
    if (item.translations && Array.isArray(item.translations) && item.translations.length > 0) {
      result.translations = item.translations
    }
    // 添加代码配置
    if (item.codeConfig) {
      result.codeConfig = item.codeConfig
    }
    return result
  })
}

/** 取消导入 */
function cancelImport() {
  importDialogOpen.value = false
  importPreviewData.value = []
  importFile.value = null
}

/** 确认导入 */
async function confirmImport() {
  if (importPreviewData.value.length === 0) {
    proxy.$modal.msgWarning('没有可导入的数据')
    return
  }
  
  // 检查重复的编码和域名（仅提示，不阻止导入）
  const duplicateCheck = await checkDuplicateData()
  let confirmMessage = `确认要导入 ${importPreviewData.value.length} 条网站数据吗？`
  
  if (duplicateCheck.hasDuplicates) {
    confirmMessage = `检测到以下重复数据（仅提示，将继续导入）：\n${duplicateCheck.duplicates.slice(0, 5).join('\n')}${duplicateCheck.duplicates.length > 5 ? '\n...等其他' + (duplicateCheck.duplicates.length - 5) + '个' : ''}\n\n确认继续导入 ${importPreviewData.value.length} 条数据吗？`
  }
  
  try {
    await proxy.$modal.confirm(confirmMessage)
    
    importLoading.value = true
    
    // 处理支持的语言字段
    const processedData = importPreviewData.value.map(item => {
      const data = { ...item }
      
      // 处理支持的语言字段
      if (data.supportedLocales && typeof data.supportedLocales === 'string') {
        // 如果是逗号分隔的字符串，转换为数组再转JSON
        const localesArray = data.supportedLocales.split(',').map(s => s.trim()).filter(Boolean)
        data.supportedLocales = localesArray.length > 0 ? JSON.stringify(localesArray) : null
      }
      return data
    })
    
    // 批量创建：每次处理3个避免超时
    const batchSize = 3
    let successCount = 0
    let failCount = 0
    const errors = []
    
    for (let i = 0; i < processedData.length; i += batchSize) {
      const batch = processedData.slice(i, i + batchSize)
      
      const promises = batch.map(async (siteData, batchIndex) => {
        const globalIndex = i + batchIndex + 1
        try {
          // 先创建网站（移除categoryName、translations、codeConfig等非网站字段）
          const { translations, categoryName, codeConfig, ...siteInfo } = siteData
          
          const response = await addSite(siteInfo)
          
          // 由于后端只返回操作成功状态，需要查询新创建的网站ID
          let siteId = null
          if (response && response.code === 200) {
            try {
              // 根据站点编码查询网站ID
              const listResponse = await listSite({ code: siteInfo.code, pageNum: 1, pageSize: 1 })
              if (listResponse && listResponse.rows && listResponse.rows.length > 0) {
                siteId = listResponse.rows[0].id
              }
            } catch (queryError) {
              // 忽略查询错误，继续执行
            }
          }
          
          // 如果有翻译数据且获取到网站ID，保存翻译
          if (translations && Array.isArray(translations) && translations.length > 0 && siteId) {
            
            try {
              // 按语言分组翻译数据，使用批量接口减少HTTP请求
              const translationsByLocale = {}
              translations.forEach(trans => {
                if (!translationsByLocale[trans.locale]) {
                  translationsByLocale[trans.locale] = {}
                }
                translationsByLocale[trans.locale][trans.fieldName] = trans.fieldValue
              })
              
              // 使用批量接口，每种语言一次请求（比之前的单个字段一个请求效率更高）
              const translationPromises = Object.entries(translationsByLocale).map(([locale, fields]) => {
                const translationData = {
                  entityType: 'site',
                  entityId: siteId,
                  locale: locale,
                  translations: fields
                }
                
                return batchSaveTranslations(translationData)
              })
              
              await Promise.all(translationPromises)
            } catch (translationError) {
              console.error(`网站 ${siteId} 翻译保存失败:`, translationError)
              // 继续执行，不让翻译保存失败影响网站创建
            }
          }

          // 如果有代码管理配置且获取到网站ID，保存配置
          if (codeConfig && siteId) {
            try {
              const cfg = codeConfig
              await saveGitConfig(siteId, {
                gitProvider: cfg['Git提供商'] || 'github',
                gitRepoUrl: cfg['仓库地址'] || '',
                gitBranch: cfg['分支'] || 'main',
                gitToken: cfg['访问令牌'] || '',
                gitAutoSync: cfg['自动同步'] || '0'
              })
              await saveDeployConfig(siteId, {
                deployPlatform: cfg['部署平台'] || 'local',
                cloudflareAccountId: cfg['CF账户ID'] || '',
                cloudflareApiToken: cfg['CF_API令牌'] || '',
                cloudflareProjectName: cfg['CF项目名称'] || '',
                deployConfig: cfg['部署配置JSON'] || null
              })
            } catch (configError) {
              console.warn(`网站 ${siteId} 代码配置保存失败:`, configError)
            }
          }
          
          successCount++
          return { success: true, index: globalIndex }
        } catch (error) {
          console.error(`第${globalIndex}条数据导入失败:`, error)
          failCount++
          errors.push(`第${globalIndex}条: ${error.message || '未知错误'}`)
          return { success: false, index: globalIndex, error }
        }
      })
      
      await Promise.all(promises)
    }
    
    importLoading.value = false
    
    // 显示结果
    if (failCount > 0) {
      const errorMsg = errors.length > 3 ? 
        errors.slice(0, 3).join('\n') + `\n...等${failCount}个错误` :
        errors.join('\n')
      proxy.$modal.msgWarning(`导入完成：成功${successCount}条，失败${failCount}条\n\n${errorMsg}`)
    } else {
      proxy.$modal.msgSuccess(`导入成功：共${successCount}条数据`)
    }
    
    // 关闭对话框并刷新列表
    importDialogOpen.value = false
    importPreviewData.value = []
    getList()
    
  } catch (error) {
    importLoading.value = false
    if (error !== 'cancel') {
      console.error('导入数据失败:', error)
      proxy.$modal.msgError('导入数据失败: ' + (error.message || '未知错误'))
    }
  }
}

/** 检查重复数据（仅提示，不阻止导入） */
async function checkDuplicateData() {
  const duplicates = []
  const existingCodes = new Set()
  const existingDomains = new Set()
  
  // 获取现有的网站编码和域名
  try {
    const response = await listSite({ pageNum: 1, pageSize: 10000 })
    if (response && response.rows) {
      response.rows.forEach(site => {
        if (site.code) existingCodes.add(site.code)
        if (site.domain) existingDomains.add(site.domain)
      })
    }
  } catch (error) {
    console.warn('获取现有网站数据失败:', error)
  }
  
  // 检查导入数据中的重复项
  importPreviewData.value.forEach((item, index) => {
    if (item.code && existingCodes.has(item.code)) {
      duplicates.push(`第${index + 1}行: 站点编码 "${item.code}" 已存在`)
    }
    if (item.domain && existingDomains.has(item.domain)) {
      duplicates.push(`第${index + 1}行: 域名 "${item.domain}" 已存在`)
    }
  })
  
  return {
    hasDuplicates: duplicates.length > 0,
    duplicates
  }
}
</script>
