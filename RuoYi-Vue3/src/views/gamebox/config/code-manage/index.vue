<template>
  <div class="code-editor-layout" @keydown.ctrl.s.prevent="saveCurrentFile">
    <!-- ===== 顶部工具栏 ===== -->
    <div class="editor-toolbar">
      <div class="toolbar-left">
        <span class="toolbar-logo"><el-icon><Monitor /></el-icon> 代码管理</span>
        <el-divider direction="vertical" />
        <SiteSelect
          v-model="currentSiteId"
          :site-list="siteList"
          placeholder="选择网站"
          size="small"
          width="180px"
          :loading="siteListLoading"
          @change="handleSiteChange"
        />
      </div>
      <!-- 操作组（居中） -->
      <div class="toolbar-center" v-if="currentSiteId">
        <!-- 安装（固定） -->
        <div class="tb-op-group" v-if="hasRepo">
          <span class="tb-op-label">安装</span>
          <el-button size="small" :loading="installing" :disabled="!hasRepo" @click="handleInstall">
            <el-icon v-if="!installing"><Box /></el-icon>
            {{ installing ? '安装中...' : (deployForm.installCommand || 'pnpm install') }}
          </el-button>
          <el-popover placement="bottom" :width="300" trigger="click">
            <template #reference>
              <el-icon class="tb-op-edit"><EditPen /></el-icon>
            </template>
            <div style="font-size:12px;color:var(--el-text-color-secondary);margin-bottom:6px">安装命令<span v-if="deployForm.deployMode === 'github-actions' || deployForm.deployMode === 'github-actions-push'" style="color:#e6a23c;margin-left:6px;font-size:11px">⚡ 同时写入 Actions 工作流</span></div>
            <el-input v-model="deployForm.installCommand" placeholder="pnpm install" size="small" @keydown.enter.prevent="handleSaveDeploy"><template #prepend>$</template></el-input>
            <div style="margin-top:8px;display:flex;justify-content:flex-end"><el-button size="small" type="primary" :loading="savingDeploy" @click="handleSaveDeploy">保存</el-button></div>
          </el-popover>
        </div>
        <!-- 本地构建（固定） -->
        <div class="tb-op-group" v-if="hasRepo">
          <span class="tb-op-label">本地构建</span>
          <el-button size="small" :loading="building" :disabled="!hasRepo" @click="handleBuild">
            <el-icon v-if="!building"><Cpu /></el-icon>
            {{ building ? '构建中...' : (deployForm.buildCommand || 'pnpm run build') }}
          </el-button>
          <el-popover placement="bottom" :width="300" trigger="click">
            <template #reference>
              <el-icon class="tb-op-edit"><EditPen /></el-icon>
            </template>
            <div style="font-size:12px;color:var(--el-text-color-secondary);margin-bottom:6px">构建命令<span v-if="deployForm.deployMode === 'github-actions' || deployForm.deployMode === 'github-actions-push'" style="color:#e6a23c;margin-left:6px;font-size:11px">⚡ 同时写入 Actions 工作流</span></div>
            <el-input v-model="deployForm.buildCommand" placeholder="pnpm run build" size="small" @keydown.enter.prevent="handleSaveDeploy"><template #prepend>$</template></el-input>
            <div style="margin-top:8px;display:flex;justify-content:flex-end"><el-button size="small" type="primary" :loading="savingDeploy" @click="handleSaveDeploy">保存</el-button></div>
          </el-popover>
        </div>
        <!-- 本地部署/预览（固定） -->
        <div class="tb-op-group" v-if="hasRepo">
          <span class="tb-op-label">本地部署</span>
          <el-button-group size="small">
            <el-button :type="previewStatus.running ? 'danger' : 'success'" :loading="previewLoading"
              :disabled="!hasRepo" @click="togglePreviewServer">
              <el-icon v-if="!previewLoading"><component :is="previewStatus.running ? 'VideoPause' : 'VideoPlay'" /></el-icon>
              {{ previewStatus.running ? '停止' : (deployForm.previewCommand || 'pnpm run dev') }}
            </el-button>
            <el-button v-if="previewStatus.running" size="small" type="success" @click="openPreviewTab">
              <el-icon><TopRight /></el-icon>
            </el-button>
          </el-button-group>
          <el-popover placement="bottom" :width="300" trigger="click">
            <template #reference>
              <el-icon class="tb-op-edit"><EditPen /></el-icon>
            </template>
            <div style="font-size:12px;color:var(--el-text-color-secondary);margin-bottom:6px">本地部署命令（启动开发预览服务器）</div>
            <el-input v-model="deployForm.previewCommand" placeholder="pnpm run dev" size="small" @keydown.enter.prevent="handleSaveDeploy"><template #prepend>$</template></el-input>
            <div style="margin-top:8px;display:flex;justify-content:flex-end"><el-button size="small" type="primary" :loading="savingDeploy" @click="handleSaveDeploy">保存</el-button></div>
          </el-popover>
        </div>
        <!-- 远程构建（GA / CF 直连，条件显示，固定在本地操作之后） -->
        <template v-if="deployForm.deployPlatform !== 'local' && currentSiteId && (deployForm.deployMode === 'github-actions' || deployForm.deployMode === 'github-actions-push' || deployForm.deployMode === 'cf-direct')">
          <div class="tb-op-group">
            <span class="tb-op-label">远程构建</span>
            <el-tooltip v-if="deployForm.deployMode === 'github-actions'" content="触发 GitHub Actions，平台自动拉取代码并执行构建命令">
              <el-button size="small" :loading="gaTriggering" :disabled="!hasRepo" @click="handleDeployGithubAction">
                <el-icon v-if="!gaTriggering"><Promotion /></el-icon>
                {{ gaTriggering ? '触发中...' : 'GitHub Actions' }}
              </el-button>
            </el-tooltip>
            <el-tooltip v-else-if="deployForm.deployMode === 'github-actions-push'" content="推送代码后自动触发，无需手动触发">
              <el-button size="small" disabled><el-icon><CircleCheck /></el-icon> 推送自动触发</el-button>
            </el-tooltip>
            <el-tooltip v-else-if="deployForm.deployMode === 'cf-direct'" content="推送代码后 CF 平台自动构建，无需手动触发">
              <el-button size="small" disabled><el-icon><CircleCheck /></el-icon> CF 自动</el-button>
            </el-tooltip>
            <a v-if="deployForm.deployMode === 'github-actions' && gaRunUrl" :href="gaRunUrl" target="_blank" rel="noopener" class="tb-op-link">
              <el-icon><TopRight /></el-icon> 查看
            </a>
            <a v-else-if="deployForm.deployMode === 'github-actions' && deployForm.actionRepo" :href="deployForm.actionRepo.replace(/\.git$/, '') + '/actions'" target="_blank" rel="noopener" class="tb-op-link">
              <el-icon><TopRight /></el-icon> 查看 Actions
            </a>
            <a v-if="deployForm.deployMode === 'github-actions-push' && currentSite.gitRepoUrl" :href="'https://github.com/' + (currentSite.gitRepoUrl.replace(/\.git$/, '').split('github.com/').pop()) + '/actions'" target="_blank" rel="noopener" class="tb-op-link">
              <el-icon><TopRight /></el-icon> 查看 Actions
            </a>
          </div>
        </template>
        <!-- 远程部署（server 模式，条件显示，固定在最后） -->
        <template v-if="deployForm.deployPlatform !== 'local' && deployForm.deployMode === 'server' && currentSiteId">
          <div class="tb-op-group">
            <span class="tb-op-label">远程部署</span>
            <el-tooltip content="将本地构建产物上传部署到 Cloudflare">
              <el-button size="small" type="warning" :loading="deploying" :disabled="!hasRepo"
                @click="handleDeployWorkers">
                <el-icon v-if="!deploying"><Upload /></el-icon>
                {{ deploying ? '部署中...' : (deployForm.deployCommand || 'npx wrangler deploy') }}
              </el-button>
            </el-tooltip>
            <el-popover placement="bottom" :width="360" trigger="click">
              <template #reference>
                <el-icon class="tb-op-edit"><EditPen /></el-icon>
              </template>
              <div style="font-size:12px;color:var(--el-text-color-secondary);margin-bottom:6px">远程部署命令（由后端执行）</div>
              <el-input v-model="deployForm.deployCommand" placeholder="npx wrangler deploy" size="small" @keydown.enter.prevent="handleSaveDeploy"><template #prepend>$</template></el-input>
              <div style="margin-top:8px;display:flex;justify-content:flex-end"><el-button size="small" type="primary" :loading="savingDeploy" @click="handleSaveDeploy">保存</el-button></div>
            </el-popover>
          </div>
        </template>
      </div>
      <div class="toolbar-right">
        <el-tooltip content="Git / 部署配置">
          <el-button size="small" :disabled="!currentSiteId" @click="showGitDialog = true">
            <el-icon><Setting /></el-icon> 配置
          </el-button>
        </el-tooltip>
        <el-tooltip content="提交历史 (git log)">
          <el-button size="small" :disabled="!hasRepo" @click="loadGitLog">
            <el-icon><List /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip :content="showSidebar ? '隐藏文件树' : '显示文件树'">
          <el-button size="small" :type="showSidebar ? 'primary' : ''" @click="showSidebar = !showSidebar">
            <el-icon><Files /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip :content="showPreview ? '隐藏预览' : '显示预览'">
          <el-button size="small" :type="showPreview ? 'primary' : ''" @click="showPreview = !showPreview"
            :disabled="!previewStatus.running">
            <el-icon><View /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </div>

    <!-- ===== 主体区域 ===== -->
    <div class="editor-main">
      <!-- 文件侧边栏 -->
      <div class="editor-sidebar" v-show="showSidebar" :style="{ width: sidebarWidth + 'px' }">

        <!-- Sidebar tab 标题栏 -->
        <div class="sidebar-tabs">
          <span class="sidebar-tab-btn" :class="{ active: sidebarTab === 'files' }" @click="sidebarTab = 'files'">
            <el-icon><Files /></el-icon> 文件
          </span>
          <span class="sidebar-tab-btn" :class="{ active: sidebarTab === 'git' }" @click="sidebarTab = 'git'">
            <el-icon><Connection /></el-icon> 源代码
            <span v-if="gitTotalCount" class="git-badge">{{ gitTotalCount }}</span>
          </span>
          <span class="sidebar-tab-btn" :class="{ active: sidebarTab === 'config' }" @click="sidebarTab = 'config'">
            <el-icon><Setting /></el-icon> 配置
          </span>
          <div style="flex:1"></div>
          <!-- 文件面板操作按钮 -->
          <template v-if="sidebarTab === 'files' && hasRepo">
            <el-tooltip content="新建文件">
              <el-icon class="sidebar-action" @click="ctxNewFile(false)"><DocumentAdd /></el-icon>
            </el-tooltip>
            <el-tooltip content="新建文件夹">
              <el-icon class="sidebar-action" @click="ctxNewFile(true)"><FolderAdd /></el-icon>
            </el-tooltip>
            <el-tooltip content="上传文件（支持多选）">
              <el-icon class="sidebar-action" :class="{ spin: uploading }" @click="triggerUpload">
                <component :is="uploading ? 'Loading' : 'Upload'" />
              </el-icon>
            </el-tooltip>
            <el-tooltip content="刷新文件树">
              <el-icon class="sidebar-action" @click="refreshFileTree"><Refresh /></el-icon>
            </el-tooltip>
          </template>
          <!-- Git 面板操作按钮 -->
          <template v-if="sidebarTab === 'git'">
            <el-tooltip content="拉取代码 (git pull / clone)">
              <el-icon class="sidebar-action" :class="{ spin: pulling }" @click="handlePull"><Download /></el-icon>
            </el-tooltip>
            <template v-if="hasRepo">
              <el-tooltip content="推送代码 (git push)">
                <el-icon class="sidebar-action" :class="{ spin: gitPushing }" @click="doGitPush"><Upload /></el-icon>
              </el-tooltip>
              <el-tooltip content="刷新状态">
                <el-icon class="sidebar-action" :class="{ spin: gitStatusLoading }" @click="loadGitStatus">
                  <Refresh />
                </el-icon>
              </el-tooltip>
              <el-tooltip content="查看全部 Diff">
                <el-icon class="sidebar-action" @click="viewGitDiff('')"><Document /></el-icon>
              </el-tooltip>
              <el-tooltip content="提交历史">
                <el-icon class="sidebar-action" @click="loadGitLog"><List /></el-icon>
              </el-tooltip>
            </template>
          </template>
        </div>

        <!-- ===== 文件树面板 ===== -->
        <div v-show="sidebarTab === 'files'">
          <div class="sidebar-content" v-if="hasRepo"
            @contextmenu.prevent="handleSidebarContextmenu">
            <el-tree
              ref="fileTreeRef"
              :key="treeKey"
              :props="{ label: 'name', children: 'children', isLeaf: 'isLeaf' }"
              lazy
              :load="loadTreeNode"
              :indent="14"
              node-key="path"
              highlight-current
              :current-node-key="activeFilePath"
              @node-click="handleTreeNodeClick"
              @node-contextmenu="handleTreeNodeContextmenu"
              :expand-on-click-node="false"
              class="file-tree"
            >
              <template #default="{ node, data }">
                <span class="tree-node">
                  <el-icon class="tree-icon" :class="data.type === 'directory' ? 'icon-folder' : 'icon-file'">
                    <component :is="data.type === 'directory' ? (node.expanded ? 'FolderOpened' : 'Folder') : 'Document'" />
                  </el-icon>
                  <span class="tree-label" :class="{ 'is-dirty': openedFilesMap[data.path]?.dirty }">{{ data.name }}</span>
                </span>
              </template>
            </el-tree>
          </div>
          <div class="sidebar-empty" v-else>
            <el-icon style="font-size:32px;color:#555"><FolderOpened /></el-icon>
            <p>仓库未拉取，请先在「站点管理」页面配置 Git 仓库地址</p>
          </div>
        </div>

        <!-- ===== Git 源代码管理面板 ===== -->
        <div v-show="sidebarTab === 'git'" class="git-status-panel">
          <div v-if="!hasRepo" class="sidebar-empty">
            <el-icon style="font-size:32px;color:#555"><Connection /></el-icon>
            <p>仓库未拉取</p>
            <el-button size="small" type="primary" :loading="pulling" @click="handlePull">
              <el-icon v-if="!pulling"><Download /></el-icon> 拉取代码
            </el-button>
          </div>
          <template v-else>
            <!-- 合并中警告横幅 -->
            <div v-if="isMerging" class="git-merging-banner">
              <el-icon style="flex-shrink:0"><Warning /></el-icon>
              <div style="flex:1;min-width:0">
                <div style="font-weight:600;margin-bottom:2px">正在合并中 (MERGE)</div>
                <div v-if="statusConflictFiles.length > 0" style="font-size:11px;opacity:.85">还有 {{ statusConflictFiles.length }} 个冲突需要解决，解决后暂存再提交</div>
                <div v-else style="font-size:11px;opacity:.85">冲突已全部解决，可直接点「提交」完成合并</div>
              </div>
              <el-button size="small" plain type="warning" @click="showMergeConflictDialog = true; mergeConflictFiles = statusConflictFiles">查看冲突</el-button>
            </div>

            <!-- 提交消息输入框 + 分裂提交按钮 -->
            <div class="git-commit-area">
              <textarea v-model="commitMessage" class="git-commit-input"
                :placeholder="isMerging ? `合并提交信息（可留空自动生成）` : `消息（Ctrl+Enter 在 ${repoInfo.branch || 'main'} 提交）`"
                rows="3"
                @keydown.ctrl.enter.prevent="doGitCommit"
                @keydown.meta.enter.prevent="doGitCommit"
              ></textarea>
              <div class="git-commit-actions">
                <!-- 分裂按钮：提交 | ▼(提交并推送) -->
                <el-button size="small" type="primary" :loading="gitCommitting"
                  :disabled="!isMerging && gitStagedList.length === 0"
                  @click="doGitCommit" style="flex:1;justify-content:center">
                  <el-icon><Check /></el-icon>{{ isMerging ? '合并提交' : '提交' }}
                </el-button>
                <el-dropdown trigger="click" :disabled="!isMerging && gitStagedList.length === 0" @command="onCommitDropdown">
                  <el-button size="small" type="primary" :disabled="!isMerging && gitStagedList.length === 0" class="git-commit-dropdown-btn">
                    <el-icon><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="commitAndPush">
                        <el-icon><Upload /></el-icon> 提交并推送
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>

            <!-- 待推送横幅 -->
            <div v-if="pendingPushCount > 0 || needsForcePush" class="git-pending-push"
              :class="needsForcePush ? 'git-pending-push--force' : ''">
              <el-icon><Top /></el-icon>
              <span v-if="needsForcePush">本地已回退，需要强制推送到 origin/{{ needsForcePush }}</span>
              <span v-else>{{ pendingPushCount }} 个提交待推送</span>
              <el-button size="small" :type="needsForcePush ? 'danger' : 'primary'" plain :loading="gitPushing" @click="doGitPush">
                {{ needsForcePush ? '强制推送' : '推送' }}
              </el-button>
            </div>

            <div v-if="gitStatusLoading" style="padding:12px;text-align:center;color:#888;font-size:12px">
              <el-icon class="spin"><Loading /></el-icon> 刷新中...
            </div>
            <template v-else>
              <!-- 冲突待处理区 -->
              <div v-if="gitConflictList.length" class="git-section">
                <div class="git-section-header" style="color:#f14c4c">
                  <el-icon style="margin-right:4px;flex-shrink:0"><Warning /></el-icon>
                  <span>冲突待处理 ({{ gitConflictList.length }})</span>
                </div>
                <div v-for="item in gitConflictList" :key="'c-'+item.path"
                  class="git-change-item git-conflict-item"
                  :title="item.path"
                  @click="openConflictResolver(item.path)">
                  <span class="git-status-badge gs-conflict">!</span>
                  <span class="git-change-path">{{ item.path.split('/').pop() }}</span>
                  <span class="git-change-dir">{{ item.path.includes('/') ? item.path.substring(0, item.path.lastIndexOf('/')) : '' }}</span>
                  <el-icon class="git-item-action git-item-action--always" title="解决冲突" style="color:#f14c4c" @click.stop="openConflictResolver(item.path)"><Edit /></el-icon>
                </div>
              </div>

              <!-- 暂存区 -->
              <div v-if="gitStagedList.length" class="git-section">
                <div class="git-section-header">
                  <span>已暂存的更改 ({{ gitStagedList.length }})</span>
                  <el-tooltip content="取消全部暂存">
                    <el-icon class="git-section-action" @click="doUnstageAll"><Minus /></el-icon>
                  </el-tooltip>
                </div>
                <div v-for="item in gitStagedList" :key="'s-'+item.path"
                  class="git-change-item"
                  :title="item.path"
                  @click="viewGitDiff(item.path)">
                  <span class="git-status-badge" :class="'gs-' + item.statusType">{{ statusChar(item.statusType) }}</span>
                  <span class="git-change-path">{{ item.path.split('/').pop() }}</span>
                  <span class="git-change-dir">{{ item.path.includes('/') ? item.path.substring(0, item.path.lastIndexOf('/')) : '' }}</span>
                  <!-- 取消暂存按钮：始终可见 -->
                  <el-icon class="git-item-action git-item-action--always" title="取消暂存" @click.stop="doUnstage(item.path)"><Minus /></el-icon>
                </div>
              </div>

              <!-- 未暂存区（过滤掉冲突文件） -->
              <div v-if="gitCleanUnstagedList.length" class="git-section">
                <div class="git-section-header">
                  <span>更改 ({{ gitCleanUnstagedList.length }})</span>
                  <el-tooltip content="全部暂存">
                    <el-icon class="git-section-action" @click="doStageAll"><Plus /></el-icon>
                  </el-tooltip>
                </div>
                <div v-for="item in gitCleanUnstagedList" :key="'u-'+item.path"
                  class="git-change-item"
                  :title="item.path"
                  @click="viewGitDiff(item.path)">
                  <span class="git-status-badge" :class="'gs-' + item.statusType">{{ statusChar(item.statusType) }}</span>
                  <span class="git-change-path">{{ item.path.split('/').pop() }}</span>
                  <span class="git-change-dir">{{ item.path.includes('/') ? item.path.substring(0, item.path.lastIndexOf('/')) : '' }}</span>
                  <!-- 放弃更改 + 暂存 按钮（hover 显示） -->
                  <el-icon class="git-item-action" title="放弃更改" @click.stop="doDiscardFile(item)"><RefreshLeft /></el-icon>
                  <el-icon class="git-item-action git-item-action--always" title="暂存" @click.stop="doStage(item.path)"><Plus /></el-icon>
                </div>
              </div>

              <div v-if="!gitStagedList.length && !gitCleanUnstagedList.length && !gitConflictList.length && pendingPushCount === 0"
                style="padding:20px;text-align:center;color:#555;font-size:12px">
                工作区无变更
              </div>
            </template>
          </template>
        </div>

        <!-- ===== 配置编辑器侧边导航面板 ===== -->
        <div v-show="sidebarTab === 'config'" class="config-nav-panel">
          <div v-if="cfgLoading" style="padding:16px;text-align:center;color:#888;font-size:12px">
            <el-icon class="spin"><Loading /></el-icon> 加载中...
          </div>
          <div v-else-if="cfgMappings.length === 0" class="sidebar-empty">
            <el-icon style="font-size:28px;color:#555"><Setting /></el-icon>
            <p style="font-size:12px;color:#666;text-align:center;margin-top:6px">
              该模板未定义可编辑配置项<br/>请管理员在模板中配置「配置文件映射」
            </p>
          </div>
          <template v-else>
            <div
              v-for="group in cfgGroups"
              :key="group.name"
              class="cfgnav-group"
            >
              <div class="cfgnav-group-title">{{ group.name }}</div>
              <div
                v-for="item in group.items"
                :key="item.key"
                class="cfgnav-item"
                :class="{ active: cfgActiveKey === item.key }"
                @click="cfgScrollTo(item.key)"
              >
                <el-icon class="cfgnav-icon"><component :is="cfgTypeIcon(item.type)" /></el-icon>
                <span class="cfgnav-label">{{ item.label }}</span>
                <span v-if="cfgDirtyKeys.has(item.key)" class="cfgnav-dirty">●</span>
              </div>
            </div>
            <div style="padding:10px 8px;border-top:1px solid #333">
              <el-button
                size="small"
                type="primary"
                style="width:100%"
                :loading="cfgSaving"
                :disabled="cfgDirtyKeys.size === 0"
                @click="cfgSaveAll"
              >
                <el-icon><Check /></el-icon>
                保存到文件{{ cfgDirtyKeys.size > 0 ? ` (${cfgDirtyKeys.size})` : '' }}
              </el-button>
              <div v-if="cfgDirtyKeys.size > 0" style="font-size:11px;color:#888;margin-top:6px;text-align:center">
                保存后可通过「源代码」Tab 提交推送
              </div>
            </div>
          </template>
        </div>

        <div class="sidebar-resize-handle" @mousedown="startSidebarResize"></div>
      </div>

      <!-- ===== 右键浮动菜单 ===== -->
      <teleport to="body">
        <div v-if="ctxMenu.visible" class="ctx-menu"
          :style="{ top: ctxMenu.y + 'px', left: ctxMenu.x + 'px' }"
          @click.stop>
          <div class="ctx-item" @click="ctxNewFile(false)">
            <el-icon><DocumentAdd /></el-icon> 新建文件
          </div>
          <div class="ctx-item" @click="ctxNewFile(true)">
            <el-icon><FolderAdd /></el-icon> 新建文件夹
          </div>
          <div class="ctx-item" @click="ctxUpload()">
            <el-icon><Upload /></el-icon> 上传文件到此处（支持多选）
          </div>
          <div class="ctx-divider"></div>
          <div class="ctx-item" @click="ctxRename()" v-if="ctxMenu.data">
            <el-icon><Edit /></el-icon> 重命名
          </div>
          <div class="ctx-item" @click="ctxCopy()" v-if="ctxMenu.data">
            <el-icon><CopyDocument /></el-icon> 复制
          </div>
          <div class="ctx-divider"></div>
          <div class="ctx-item ctx-danger" @click="ctxDelete()" v-if="ctxMenu.data">
            <el-icon><Delete /></el-icon> 删除
          </div>
        </div>
      </teleport>

      <!-- 编辑器主区 -->
      <div class="editor-center">
        <!-- ===== 配置编辑器表单区域 ===== -->
        <div v-show="sidebarTab === 'config'" class="cfg-form-panel">
          <div class="cfg-warning-bar">
            <el-icon><Warning /></el-icon>
            <span>请勿手动修改 <code>src/config/customize/</code> 目录的文件结构与路径，否则系统无法解析写入</span>
          </div>
          <div v-if="cfgLoading" style="flex:1;display:flex;align-items:center;justify-content:center;flex-direction:column;gap:10px;color:#888">
            <el-icon class="spin" style="font-size:28px"><Loading /></el-icon>
            <span style="font-size:13px">正在加载配置...</span>
          </div>
          <div v-else-if="cfgMappings.length === 0" style="flex:1;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:10px">
            <el-icon style="font-size:40px;color:#555"><Setting /></el-icon>
            <p style="font-size:13px;color:#888;text-align:center;margin:0">该模板未定义可编辑配置项<br/>请管理员在「站点模板」中配置「配置文件映射」</p>
          </div>
          <div v-else ref="cfgFormPanelRef" class="cfg-form-scroll">
            <div v-for="group in cfgGroups" :key="group.name" class="cfg-group">
              <div class="cfg-group-title">{{ group.name }}</div>
              <el-row :gutter="16">
                <el-col
                  v-for="item in group.items"
                  :key="item.key"
                  :span="item.type === 'textarea' ? 24 : 12"
                >
                  <div
                    :data-cfg-key="item.key"
                    class="cfg-field-wrap"
                    :class="{ 'is-dirty': cfgDirtyKeys.has(item.key), 'is-active': cfgActiveKey === item.key }"
                  >
                    <div class="cfg-field-label">
                      <span>{{ item.label }}</span>
                      <span v-if="item.required" class="cfg-required">*</span>
                      <el-tag v-if="cfgDirtyKeys.has(item.key)" size="small" type="warning" style="margin-left:4px;padding:0 4px;height:16px;line-height:16px">已修改</el-tag>
                    </div>
                    <!-- text -->
                    <el-input
                      v-if="!item.type || item.type === 'text'"
                      v-model="cfgValues[item.key]"
                      size="small"
                      :placeholder="item.description || item.label"
                      clearable
                      class="cfg-input"
                    />
                    <!-- textarea -->
                    <el-input
                      v-else-if="item.type === 'textarea'"
                      v-model="cfgValues[item.key]"
                      type="textarea"
                      :rows="3"
                      size="small"
                      :placeholder="item.description || item.label"
                      class="cfg-input"
                    />
                    <!-- number -->
                    <el-input-number
                      v-else-if="item.type === 'number'"
                      v-model="cfgValues[item.key]"
                      size="small"
                      class="cfg-input"
                      style="width:100%"
                    />
                    <!-- color -->
                    <div v-else-if="item.type === 'color'" style="display:flex;align-items:center;gap:8px">
                      <el-color-picker v-model="cfgValues[item.key]" size="small" />
                      <el-input v-model="cfgValues[item.key]" size="small" clearable style="flex:1" :placeholder="item.description || '#RRGGBB'" class="cfg-input" />
                    </div>
                    <!-- switch -->
                    <el-switch
                      v-else-if="item.type === 'switch'"
                      v-model="cfgValues[item.key]"
                      size="small"
                    />
                    <!-- fallback -->
                    <el-input v-else v-model="cfgValues[item.key]" size="small" class="cfg-input" clearable />
                    <!-- description + file path hint -->
                    <div v-if="item.description || item.filePath" class="cfg-field-desc">
                      <span v-if="item.description">{{ item.description }}</span>
                      <span v-if="item.filePath" class="cfg-field-path">📄 {{ item.filePath }}</span>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>

        <!-- 文件标签页 -->
        <div class="editor-tabs" v-if="sidebarTab !== 'config' && (openedFiles.length > 0 || diffFilePath)"
          @wheel.prevent="e => e.currentTarget.scrollLeft += e.deltaY || e.deltaX"
          ref="editorTabsRef">
          <div v-for="f in openedFiles" :key="f.path"
            class="editor-tab" :class="{ active: f.path === activeFilePath && !isDiffMode, dirty: f.dirty }"
            @click="switchToFile(f.path)"
            @mousedown.middle.prevent="closeFile(f.path)">
            <el-icon class="tab-icon"><Document /></el-icon>
            <span class="tab-name">{{ f.name }}<span v-if="f.dirty" class="tab-dirty">●</span></span>
            <el-icon class="tab-close" @click.stop="closeFile(f.path)"><Close /></el-icon>
          </div>
          <!-- Diff 模式标签（冲突模式下隐藏，避免重复） -->
          <div v-if="diffFilePath && !isConflictMode" class="editor-tab diff-mode-tab" :class="{ active: isDiffMode }"
            @click="isDiffMode = true; isConflictMode = false"
            @mousedown.middle.prevent="exitDiffMode">
            <el-icon class="tab-icon" style="color:#9cdcfe"><DocumentCopy /></el-icon>
            <span class="tab-name">{{ diffFilePath.split('/').pop() || diffFilePath }} (对比)</span>
            <el-icon class="tab-close" @click.stop="exitDiffMode"><Close /></el-icon>
          </div>
          <!-- 冲突解决标签 -->
          <div v-if="isConflictMode" class="editor-tab conflict-mode-tab active"
            @mousedown.middle.prevent="exitConflictMode">
            <el-icon class="tab-icon" style="color:#f14c4c"><Warning /></el-icon>
            <span class="tab-name" style="color:#f14c4c">{{ conflictResolverFileName }} (解决冲突)</span>
            <el-icon class="tab-close" @click.stop="exitConflictMode"><Close /></el-icon>
          </div>
        </div>

        <!-- 远端有新提交提示横幅 -->
        <div v-if="sidebarTab !== 'config' && hasRepo && repoInfo.behindCount > 0"
          style="display:flex;align-items:center;gap:8px;padding:5px 12px;background:#fff1f0;border-bottom:1px solid #ffccc7;font-size:12px;color:#cf1322;flex-shrink:0">
          <el-icon><RefreshRight /></el-icon>
          <span>远端有 <b>{{ repoInfo.behindCount }}</b> 个新提交，建议执行拉取代码</span>
          <el-button size="small" type="danger" plain style="height:20px;padding:0 8px;font-size:11px;min-height:0" :loading="pulling" @click="handlePull">立即拉取</el-button>
        </div>

        <!-- Monaco 编辑器容器 -->
        <div v-show="sidebarTab !== 'config' && !isDiffMode" class="monaco-editor-wrap">
          <div ref="monacoContainer" class="monaco-container"></div>
        </div>
        <!-- Monaco Diff 编辑器（对比模式 / 冲突解决模式）-->
        <div v-show="sidebarTab !== 'config' && isDiffMode" class="monaco-diff-wrapper">
          <div class="diff-editor-bar">
            <!-- 普通对比模式 -->
            <template v-if="!isConflictMode">
              <span style="font-size:11px;color:#9cdcfe">◀ 原始版本（HEAD）&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;修改版本（工作区）▶</span>
              <div style="flex:1"></div>
              <el-button size="small" type="primary" @click="saveDiffChanges" :loading="saving">保存修改</el-button>
              <el-button size="small" plain @click="exitDiffMode">退出对比</el-button>
            </template>
            <!-- 冲突解决模式 -->
            <template v-else>
              <el-icon style="color:#f14c4c;flex-shrink:0"><Warning /></el-icon>
              <span class="cr-float-filename">{{ conflictResolverFileName }}</span>
              <span v-if="conflictTotalCount > 0" class="cr-float-pos">冲突 {{ conflictCurrentIdx + 1 }}/{{ conflictTotalCount }}</span>
              <span v-else class="cr-float-done">✓ 冲突已全部解决</span>
              <el-button v-if="conflictTotalCount > 0" size="small" plain :disabled="conflictCurrentIdx <= 0" @click="navigateConflict(conflictCurrentIdx - 1)">↑ 上一个</el-button>
              <el-button v-if="conflictTotalCount > 0" size="small" plain :disabled="conflictCurrentIdx >= conflictTotalCount - 1" @click="navigateConflict(conflictCurrentIdx + 1)">↓ 下一个</el-button>
              <span class="cr-float-sep"></span>
              <el-button size="small" type="primary"
                :disabled="conflictTotalCount === 0 && !conflictCurrentRange"
                @click="applyConflictResolution('current')">采用当前 (HEAD)</el-button>
              <el-button size="small" type="success"
                :disabled="conflictTotalCount === 0 && !conflictCurrentRange"
                @click="applyConflictResolution('incoming')">采用传入</el-button>
              <el-button size="small" plain
                :disabled="conflictTotalCount === 0 && !conflictCurrentRange"
                @click="applyConflictResolution('both')">两者都保留</el-button>
              <div style="flex:1"></div>
              <el-button size="small" plain @click="exitConflictMode">关闭</el-button>
              <el-button size="small" type="primary" :disabled="conflictTotalCount > 0" :loading="conflictSaving" @click="saveConflictResolved">完成并暂存</el-button>
            </template>
          </div>
          <div ref="diffContainer" class="diff-editor-canvas"></div>
        </div>

        <!-- 无文件时的欢迎界面 -->
        <div class="editor-welcome" v-if="sidebarTab !== 'config' && openedFiles.length === 0 && !fileLoading && !isDiffMode">
          <el-icon style="font-size:56px;color:#3c3c3c"><Monitor /></el-icon>
          <h3 style="color:#666;margin:8px 0">代码编辑器</h3>
          <p style="color:#555;margin:0;font-size:13px">从左侧文件树选择文件开始编辑</p>
          <p v-if="!hasRepo" style="color:#f56c6c;margin-top:6px;font-size:12px">请先配置 Git 仓库并拉取代码</p>
          <div style="margin-top:16px;display:flex;gap:6px">
            <el-tag size="small" effect="dark">Ctrl+S 保存</el-tag>
            <el-tag size="small" effect="dark">中键关闭标签</el-tag>
          </div>
        </div>
        <div class="monaco-loading" v-if="sidebarTab !== 'config' && fileLoading && !isDiffMode">
          <el-icon class="spin" style="font-size:24px;color:#569cd6"><Loading /></el-icon>
          <span style="color:#888;font-size:13px">正在加载 {{ fileLoadingName }}...</span>
        </div>

        <!-- 底部文件信息栏 -->
        <div class="editor-infobar" v-if="sidebarTab !== 'config' && activeFilePath">
          <span style="color:#888;font-size:11px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;max-width:50%">{{ activeFilePath }}</span>
          <span class="sep">|</span>
          <span style="color:#888;font-size:11px">{{ currentLang }}</span>
          <span class="sep">|</span>
          <span style="color:#888;font-size:11px">UTF-8</span>
          <div style="flex:1"></div>
          <el-button v-if="openedFilesMap[activeFilePath]?.dirty" size="small" type="primary"
            style="height:20px;padding:0 8px;font-size:11px;min-height:0" @click="saveCurrentFile" :loading="saving">
            保存
          </el-button>
          <span v-else style="color:#4ec94e;font-size:11px">✓ 已保存</span>
        </div>
      </div>

      <!-- 预览面板 -->
      <div class="editor-preview" v-show="showPreview && previewStatus.running"
        :style="{ width: previewWidth + 'px' }">
        <div class="preview-resize-handle" @mousedown="startPreviewResize"></div>
        <div class="preview-toolbar">
          <el-icon style="color:#4ec94e;flex-shrink:0"><CircleCheck /></el-icon>
          <el-input v-model="previewAddressBar" size="small"
            style="flex:1;cursor:default" placeholder="当前页面路径" readonly />
          <el-tooltip content="刷新">
            <el-icon style="cursor:pointer;color:#aaa;flex-shrink:0" @click="refreshPreview"><Refresh /></el-icon>
          </el-tooltip>
          <el-tooltip content="新标签页">
            <el-icon style="cursor:pointer;color:#aaa;flex-shrink:0" @click="openPreviewTab"><TopRight /></el-icon>
          </el-tooltip>
        </div>
        <iframe ref="previewFrame" :src="iframeSrc" class="preview-iframe"
          sandbox="allow-scripts allow-forms allow-popups allow-modals"
          @load="syncPreviewAddress"></iframe>
      </div>
    </div>

    <!-- ===== 底部状态栏 ===== -->
    <div class="editor-statusbar">
      <span class="status-item branch" style="cursor:pointer" @click="openBranchDialog">
        <el-icon><Connection /></el-icon>&nbsp;{{ repoInfo.branch || 'main' }}
        <el-icon style="margin-left:2px;font-size:10px"><ArrowDown /></el-icon>
      </span>
      <span class="status-item" :class="deployStatusClass">
        <el-icon><Box /></el-icon>&nbsp;{{ deployStatusText }}
      </span>
      <span v-if="previewStatus.running" class="status-item pv-running" @click="showPreview = !showPreview">
        <el-icon><VideoPlay /></el-icon>&nbsp;预览 :{{ previewStatus.port }}
      </span>
      <span v-if="lastDeployTime" class="status-item" style="cursor:pointer"
        @click="isViewingLastLog = true; logDialogTitle = '上次操作日志'; showLogDialog = true">
        <el-icon><Document /></el-icon>&nbsp;上次: {{ lastDeployTime }}
      </span>
      <div style="flex:1"></div>
      <span v-if="building" class="status-item">
        <el-icon class="spin"><Loading /></el-icon>&nbsp;构建中...
      </span>
      <span v-if="installing" class="status-item">
        <el-icon class="spin"><Loading /></el-icon>&nbsp;安装依赖中...
      </span>
      <span v-if="pulling" class="status-item">
        <el-icon class="spin"><Loading /></el-icon>&nbsp;拉取代码...
      </span>
      <span v-if="pushing" class="status-item">
        <el-icon class="spin"><Loading /></el-icon>&nbsp;推送中...
      </span>
    </div>

    <!-- ===== 分支管理 Dialog ===== -->
    <el-dialog
      v-model="showBranchDialog"
      title="分支管理"
      width="480px"
      :close-on-click-modal="true"
      class="branch-dialog"
    >
      <!-- 新建分支 -->
      <div class="branch-create-row">
        <el-input
          v-model="newBranchName"
          placeholder="输入新分支名称"
          size="small"
          class="branch-create-input"
          @keydown.enter.prevent="doCreateBranch"
        >
          <template #prefix><el-icon><Plus /></el-icon></template>
        </el-input>
        <el-button size="small" type="primary" :loading="creatingBranch" :disabled="!newBranchName.trim()" @click="doCreateBranch">
          建立分支
        </el-button>
      </div>
      <!-- 同步远端 -->
      <div class="branch-sync-row">
        <el-button size="small" :loading="fetchingRemote" @click="doFetchRemote">
          <el-icon><Refresh /></el-icon>&nbsp;同步远端
        </el-button>
        <span style="font-size:11px;color:var(--el-text-color-secondary);margin-left:6px">更新远端分支列表</span>
      </div>
      <el-divider style="margin:10px 0" />
      <!-- 分支列表 -->
      <div v-if="branchLoading" class="branch-loading">
        <el-icon class="spin"><Loading /></el-icon>&nbsp;加载中...
      </div>
      <div v-else-if="!branchList.length" class="branch-empty">无分支信息</div>
      <ul v-else class="branch-list">
        <li
          v-for="b in branchList"
          :key="b.name"
          class="branch-item"
          :class="{ 'branch-item--current': b.current }"
        >
          <el-icon v-if="b.current" style="color:#67c23a;flex-shrink:0"><Check /></el-icon>
          <el-icon v-else style="visibility:hidden;flex-shrink:0"><Check /></el-icon>
          <span class="branch-name" :title="b.name">{{ b.name }}</span>
          <el-tag v-if="b.current" size="small" type="success" effect="plain">当前</el-tag>
          <!-- 操作按鈕组 -->
          <div class="branch-actions">
            <!-- 切换 -->
            <el-tooltip content="切换到此分支" placement="top" :show-after="400">
              <el-button
                v-if="!b.current"
                size="small" circle plain
                :loading="checkingOutBranch === b.name"
                @click.stop="doCheckoutBranch(b)"
              ><el-icon><Switch /></el-icon></el-button>
            </el-tooltip>
            <!-- 合并到当前分支 -->
            <el-tooltip :content="`合并「${b.name}」到当前分支`" placement="top" :show-after="400">
              <el-button
                v-if="!b.current"
                size="small" circle plain type="success"
                :loading="mergingBranchName === b.name"
                @click.stop="doMergeBranch(b)"
              ><el-icon><Connection /></el-icon></el-button>
            </el-tooltip>
            <!-- 推送到远端 -->
            <el-tooltip content="推送到远端" placement="top" :show-after="400">
              <el-button
                size="small" circle plain type="primary"
                :loading="pushingBranchName === b.name"
                @click.stop="doPushBranchToRemote(b)"
              ><el-icon><Upload /></el-icon></el-button>
            </el-tooltip>
            <!-- 删除远端分支 -->
            <el-tooltip content="删除远端分支" placement="top" :show-after="400">
              <el-button
                v-if="!b.current"
                size="small" circle plain type="warning"
                :loading="deletingRemoteBranch === b.name"
                @click.stop="doDeleteRemoteBranch(b.name)"
              ><el-icon><Minus /></el-icon></el-button>
            </el-tooltip>
            <!-- 删除本地分支 -->
            <el-tooltip content="删除本地分支" placement="top" :show-after="400">
              <el-button
                v-if="!b.current"
                size="small" circle plain type="danger"
                :loading="deletingBranch === b.name"
                @click.stop="doDeleteBranch(b)"
              ><el-icon><Delete /></el-icon></el-button>
            </el-tooltip>
          </div>
        </li>
      </ul>
      <!-- 远端分支 -->
      <template v-if="remoteOnlyBranches.length">
        <el-divider style="margin:10px 0">
          <span style="font-size:12px;color:var(--el-text-color-secondary)">远端分支 (origin)</span>
        </el-divider>
        <ul class="branch-list">
          <li v-for="rb in remoteOnlyBranches" :key="rb.name" class="branch-item">
            <el-icon style="visibility:hidden;flex-shrink:0"><Check /></el-icon>
            <span class="branch-name" :title="rb.name">{{ rb.name }}</span>
            <el-tag v-if="rb.hasLocal" size="small" type="success" effect="plain">已有本地</el-tag>
            <el-tag v-else size="small" type="info" effect="plain">仅远端</el-tag>
            <div class="branch-actions">
              <el-tooltip v-if="!rb.hasLocal" content="检出到本地" placement="top" :show-after="400">
                <el-button
                  size="small" circle plain type="success"
                  :loading="checkingOutBranch === rb.name"
                  @click.stop="doCheckoutRemoteBranch(rb.name)"
                ><el-icon><Download /></el-icon></el-button>
              </el-tooltip>
              <el-tooltip content="删除远端分支" placement="top" :show-after="400">
                <el-button
                  size="small" circle plain type="danger"
                  :loading="deletingRemoteBranch === rb.name"
                  @click.stop="doDeleteRemoteBranch(rb.name)"
                ><el-icon><Delete /></el-icon></el-button>
              </el-tooltip>
            </div>
          </li>
        </ul>
      </template>
    </el-dialog>

    <!-- ===== 合并冲突处理对话框 ===== -->
    <el-dialog
      v-model="showMergeConflictDialog"
      :title="mergeConflictSourceBranch ? `合并冲突 — ${mergeConflictSourceBranch}` : '合并冲突 (MERGE 状态)'"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div style="margin-bottom:12px">
        <el-icon style="color:#e6a23c;font-size:22px;vertical-align:middle"><Warning /></el-icon>
        <span style="margin-left:8px;font-size:14px;vertical-align:middle">
          <template v-if="mergeConflictSourceBranch">
            合并「<b>{{ mergeConflictSourceBranch }}</b>」到当前分支时发生冲突，请处理后手动提交。
          </template>
          <template v-else>
            仓库正处于合并中状态 (MERGE_HEAD)，存在冲突文件需处理后提交。
          </template>
        </span>
      </div>
      <!-- 优先显示 mergeConflictFiles，若为空则显示 statusConflictFiles -->
      <template v-if="(mergeConflictFiles.length || statusConflictFiles.length)">
        <div style="font-size:12px;color:var(--el-text-color-secondary);margin-bottom:6px">冲突文件 ({{ (mergeConflictFiles.length || statusConflictFiles.length) }} 个)：</div>
        <ul style="margin:0;padding:0 0 0 16px;max-height:180px;overflow-y:auto">
          <li v-for="f in (mergeConflictFiles.length ? mergeConflictFiles : statusConflictFiles)" :key="f"
            style="font-size:12px;color:#f14c4c;font-family:monospace;margin-bottom:4px;cursor:pointer"
            @click="openConflictFile(f)"
          >{{ f }}</li>
        </ul>
        <div style="font-size:11px;color:var(--el-text-color-secondary);margin-top:8px">
          点击文件名可打开可视化冲突解决器，选择保留哪边代码后一键保存并暂存。
        </div>
      </template>
      <template v-else-if="isMerging">
        <div style="color:#67c23a;font-size:13px;padding:8px 0">
          <el-icon><Check /></el-icon>&nbsp;所有冲突已解决！点击「完成合并提交」完成本次合并。
        </div>
      </template>
      <template #footer>
        <el-button type="danger" :loading="abortingMerge" @click="doAbortMerge">
          <el-icon><RefreshLeft /></el-icon>&nbsp;放弃合并 (abort)
        </el-button>
        <el-button
          v-if="(mergeConflictFiles.length || statusConflictFiles.length) > 0"
          type="primary"
          @click="openConflictFile((mergeConflictFiles.length ? mergeConflictFiles : statusConflictFiles)[0])"
        >解决冲突</el-button>
        <el-button
          v-else
          type="success"
          :loading="gitCommitting"
          @click="showMergeConflictDialog = false; doGitCommit()"
        ><el-icon><Check /></el-icon>&nbsp;完成合并提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showLogDialog" :title="logDialogTitle" width="820px"
      :close-on-click-modal="false">
      <div class="log-console" ref="logConsoleRef">
        <pre>{{ isViewingLastLog ? lastDeployLog : operationLog }}</pre>
      </div>
      <template #footer>
        <el-button @click="copyLog">复制日志</el-button>
        <el-button @click="showLogDialog = false">关闭</el-button>
        <el-button v-if="building || pulling || installing" @click="showLogDialog = false">后台继续</el-button>
      </template>
    </el-dialog>

    <!-- ===== 推送代码对话框 ===== -->
    <el-dialog v-model="showPushDialog" title="推送到远程仓库" width="480px"
      :close-on-click-modal="false">
      <el-form label-width="90px">
        <el-form-item label="提交信息">
          <el-input
            v-model="pushCommitMessage"
            type="textarea"
            :rows="3"
            placeholder="描述本次更改的内容（默认为 Update via Code Manager）"
            @keyup.ctrl.enter="handlePush"
          />
        </el-form-item>
        <el-form-item label="当前分支">
          <span style="color:#88c0d0;font-size:13px">{{ repoInfo.branch || repoInfo.currentBranch || currentSite.gitBranch || 'main' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPushDialog = false">取消</el-button>
        <el-button type="primary" :loading="pushing" @click="handlePush">
          <el-icon><TopRight /></el-icon>推送 (git push)
        </el-button>
      </template>
    </el-dialog>

    <!-- ===== 部署配置弹窗 ===== -->
    <el-dialog v-model="showGitDialog" title="部署配置" width="600px"
      :close-on-click-modal="false">
      <el-tabs v-model="gitDialogTab">
        <el-tab-pane label="Git 仓库" name="git">
          <el-alert type="info" :closable="false" style="margin-bottom:14px;font-size:12px">
            Git 仓库配置请前往「站点管理」页面修改。Token 通过「Platform 账号」统一管理，此处不会显示。
          </el-alert>
          <el-form label-width="90px" size="small">
            <el-form-item label="Git 平台">
              <el-tag v-if="currentSite?.gitProvider === 'github'" type="info">
                <el-icon style="vertical-align:middle"><svg viewBox="0 0 16 16" width="14" height="14" fill="currentColor"><path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/></svg></el-icon>
                GitHub
              </el-tag>
              <el-tag v-else-if="currentSite?.gitProvider === 'gitlab'" type="warning">GitLab</el-tag>
              <span v-else style="color:#999">未设置</span>
            </el-form-item>
            <el-form-item label="仓库地址">
              <span style="word-break:break-all;color:#303133">{{ currentSite?.gitRepoUrl || '未配置' }}</span>
              <a v-if="currentSite?.gitRepoUrl" :href="currentSite.gitRepoUrl.replace(/\.git$/, '')" target="_blank" rel="noopener" style="margin-left:8px;font-size:12px;color:#1677ff">
                <el-icon style="vertical-align:middle"><TopRight /></el-icon>
              </a>
            </el-form-item>
            <el-form-item label="分支">
              <el-tag type="success" size="small">{{ currentSite?.gitBranch || 'main' }}</el-tag>
            </el-form-item>
            <el-form-item label="关联账号" v-if="currentSite?.gitAccountName || currentSite?.gitAccountId">
              <el-tag size="small">{{ resolveGitAccountName(currentSite?.gitAccountId) || currentSite?.gitAccountName || ('ID: ' + currentSite?.gitAccountId) }}</el-tag>
            </el-form-item>
            <el-form-item label="仓库状态" v-if="currentSite?.gitRepoUrl">
              <template v-if="hasRepo">
                <el-tag v-if="repoInfo.behindCount === null" type="info" size="small">
                  <el-icon class="spin" style="vertical-align:-2px"><Loading /></el-icon>
                  检查中...
                </el-tag>
                <el-tag v-else-if="repoInfo.behindCount > 0" type="danger" size="small">
                  <el-icon style="vertical-align:-2px"><RefreshRight /></el-icon>
                  远端有 {{ repoInfo.behindCount }} 个新提交，建议执行拉取代码
                </el-tag>
                <el-tag v-else type="success" size="small">
                  <el-icon style="vertical-align:-2px"><CircleCheck /></el-icon>
                  已是最新（{{ repoInfo.branch || 'main' }}）
                </el-tag>
              </template>
              <el-tag v-else type="warning" size="small">
                <el-icon style="vertical-align:-2px"><Warning /></el-icon>
                未克隆到本地，请先执行拉取代码
              </el-tag>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="部署配置" name="deploy">
          <!-- ===== 部署操作流程说明（Workers 平台） ===== -->
          <template v-if="deployForm.deployPlatform === 'cloudflare-workers'">
            <div style="display:flex;align-items:center;margin-bottom:16px;padding:10px 14px;background:#f0f9ff;border:1px solid #bae0ff;border-radius:6px;font-size:12px;flex-wrap:wrap;gap:6px">
              <div style="display:flex;align-items:center;gap:5px">
                <span style="width:20px;height:20px;border-radius:50%;background:#1677ff;color:#fff;display:inline-flex;align-items:center;justify-content:center;font-size:11px;flex-shrink:0">1</span>
                <span style="color:#555">填写配置并保存</span>
              </div>
              <span style="color:#bbb">→</span>
              <div style="display:flex;align-items:center;gap:5px">
                <span style="width:20px;height:20px;border-radius:50%;background:#1677ff;color:#fff;display:inline-flex;align-items:center;justify-content:center;font-size:11px;flex-shrink:0">2</span>
                <span style="color:#555">启动预览确认效果</span>
              </div>
              <span style="color:#bbb">→</span>
              <div style="display:flex;align-items:center;gap:5px">
                <span :style="{width:'20px',height:'20px',borderRadius:'50%',background:cfProjectExists?'#52c41a':'#1677ff',color:'#fff',display:'inline-flex',alignItems:'center',justifyContent:'center',fontSize:'11px',flexShrink:'0'}">3</span>
                <span style="color:#555">创建 CF Workers 项目</span>
                <span v-if="cfProjectExists === true" style="color:#52c41a">✓ 已创建</span>
                <span v-else-if="cfProjectExists === false" style="color:#ff7875">未创建</span>
              </div>
              <span style="color:#bbb">→</span>
              <div style="display:flex;align-items:center;gap:5px">
                <span style="width:20px;height:20px;border-radius:50%;background:#1677ff;color:#fff;display:inline-flex;align-items:center;justify-content:center;font-size:11px;flex-shrink:0">4</span>
                <span style="color:#555">构建 + 部署上线</span>
              </div>
            </div>
          </template>

          <!-- CF 配置指引 -->
          <el-alert
            v-if="deployForm.deployPlatform !== 'local'"
            type="info" :closable="false"
            style="margin-bottom:14px;font-size:12px"
          >
            <template #default>
              <div style="font-size:12px;line-height:2;color:#909399">
                <div>
                  <strong>① 账户 ID</strong>：登录
                  <a href="https://dash.cloudflare.com" target="_blank" rel="noopener" style="color:#409eff">控制台</a>
                  ，右下角展开账户菜单 → “Account ID”
                </div>
                <div>
                  <strong>② API Token</strong>：点击“创建 Token”，选择模板
                  <a href="https://dash.cloudflare.com/profile/api-tokens" target="_blank" rel="noopener" style="color:#409eff"
                  >「Edit Cloudflare Workers」</a>
                  ，复制生成的 Token 填入下方
                </div>
                <div v-if="deployForm.deployPlatform === 'cloudflare-workers'">
                  <strong>③ Worker 名称</strong>：部署后默认地址为 <code style="color:#88c0d0">&lt;name&gt;.&lt;子域&gt;.workers.dev</code>
                </div>
              </div>
            </template>
          </el-alert>

          <el-form :model="deployForm" label-width="110px">
            <el-form-item label="部署平台">
              <el-select v-model="deployForm.deployPlatform" style="width:100%" @change="handleDeployPlatformChange">
                <el-option value="local" label="本地部署" />
                <el-option value="cloudflare-pages" label="Cloudflare Pages" />
                <el-option value="cloudflare-workers" label="Cloudflare Workers" />
              </el-select>
            </el-form-item>
            <template v-if="deployForm.deployPlatform !== 'local'">
              <el-form-item label="CF 账号">
                <el-select
                  v-model="deployForm.cfAccountId"
                  placeholder="选择 Cloudflare 账号"
                  style="width:100%"
                  :loading="cfAccountsLoading"
                  clearable>
                  <el-option
                    v-for="acc in cfAccounts"
                    :key="acc.id"
                    :label="acc.name + (acc.accountId ? ' (' + acc.accountId + ')' : '')"
                    :value="acc.id" />
                </el-select>
                <div style="margin-top:4px;font-size:11px;color:#909399">
                  如需添加账号，请前往「平台账号 → CF账号」管理
                </div>
              </el-form-item>
              <el-form-item :label="deployForm.deployPlatform === 'cloudflare-workers' ? 'Worker 名称' : '项目名称'">
                <el-input v-model="deployForm.cloudflareProjectName"
                  :placeholder="deployForm.deployPlatform === 'cloudflare-workers' ? '例如: my-site-worker（作为 workers.dev 子域名）' : ''" />
                <div v-if="deployForm.deployPlatform === 'cloudflare-workers' && deployForm.cloudflareProjectName"
                  style="font-size:11px;color:#909399;margin-top:3px">
                  默认访问地址：<code style="color:#88c0d0">{{ deployForm.cloudflareProjectName }}.&lt;子域&gt;.workers.dev</code>
                </div>
                <!-- Worker 名称生效方式提示 -->
                <div v-if="deployForm.deployPlatform === 'cloudflare-workers'" style="font-size:11px;margin-top:5px;padding:5px 8px;border-radius:4px;line-height:1.7;background:#1a1a2e;color:#909399;border:1px solid #2a2a40">
                  <code>wrangler.toml</code> 中的 <code>name</code> 字段决定部署到 Cloudflare 上的项目名称。<br/>
                  <template v-if="deployForm.deployMode === 'github-actions-push' || deployForm.deployMode === 'cf-direct'">
                    <span style="color:#e6a23c">⚠️ 自动部署模式：必须将修改后的 <code>wrangler.toml</code> <b>提交并推送</b>，下次自动构建才会使用新名称部署。</span>
                  </template>
                  <template v-else>
                    <span style="color:#67c23a">✓ 当前模式支持构建时通过参数指定名称，保存后立即生效，无需手动修改文件。</span>
                  </template>
                </div>
              </el-form-item>
            </template>
            <el-form-item label="部署方式">
              <el-radio-group v-model="deployForm.deployMode">
                <el-radio value="server">服务器构建部署</el-radio>
                <el-radio value="github-actions">GitHub Actions（开放仓库，手动触发）</el-radio>
                <el-radio value="github-actions-push">GitHub Actions（当前仓库，推送自动触发）</el-radio>
                <el-radio value="cf-direct">CF 直连部署（推送自动部署）</el-radio>
              </el-radio-group>
            </el-form-item>
            <!-- CF 直连部署说明 -->
            <template v-if="deployForm.deployMode === 'cf-direct'">
              <el-alert type="success" :closable="false" style="margin-bottom:12px">
                <template #title>
                  <span style="font-weight:600">自动部署 — 绑定 GitHub 仓库到 Cloudflare Workers</span>
                </template>
                <div style="font-size:12px;line-height:2;margin-top:6px">
                  <div>每次 <code>git push</code> 自动触发 Cloudflare 构建并部署，<b>不消耗服务器资源，不计 GitHub Actions 分钟数</b>。构建会消耗 CF 免费额度（每月 500 次构建），<b>个人/小团队日常使用完全够用</b>。</div>
                  <div style="margin-top:8px;font-weight:600;color:#529b2e">操作步骤（需在 Cloudflare 控制台手动完成）：</div>
                  <div>① 点击下方链接前往 Cloudflare 控制台，进入 <b>Workers &amp; Pages → Create</b></div>
                  <div>② 选择 <b>Workers</b> 标签 → 点击 <b>Connect to Git</b></div>
                  <div>③ 点击 <b>Continue with GitHub</b>，授权并选择需要绑定的仓库 &amp; 分支</div>
                  <div>④ 填写构建命令（如 <code>pnpm run build</code>）和输出目录（如 <code>.open-next</code>）</div>
                  <div>⑤ 点击 <b>Save and Deploy</b> → 此后每次 <code>git push</code> 将自动重新部署</div>
                  <el-divider style="margin:8px 0" />
                  <div>
                    <a href="https://dash.cloudflare.com/0a223df7a80ba3b519ed601835c0edbd/workers-and-pages/create"
                      target="_blank" rel="noopener" style="color:#409eff;font-weight:600">
                      🔗 前往 Cloudflare 控制台 → 开始绑定 GitHub 仓库
                    </a>
                  </div>
                </div>
              </el-alert>
            </template>

            <!-- GitHub Actions 配置 -->
            <template v-if="deployForm.deployMode === 'github-actions'">
              <el-form-item>
                <template #label>
                  Action 仓库
                  <el-tooltip placement="top">
                    <template #content>
                      <div style="font-size:12px;line-height:1.9;max-width:300px">
                        <div style="font-weight:600;margin-bottom:4px">填写完整仓库 URL</div>
                        <div>支持 HTTPS / SSH 格式，如：</div>
                        <div><code>https://github.com/owner/repo.git</code></div>
                        <div><code>git@github.com:owner/repo.git</code></div>
                        <div style="color:#67c23a;margin-top:4px">• <b>独立公开仓库（推荐）</b>：单独创建 PUBLIC 仓库存放工作流，公开仓库 Actions 完全免费</div>
                        <div>• <b>同代码仓库</b>：与代码在同一仓库，私有仓库会消耗 Actions 分钟数</div>
                      </div>
                    </template>
                    <el-icon style="cursor:help;color:#909399;vertical-align:middle;margin-left:2px"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </template>
                <el-input v-model="deployForm.actionRepo" placeholder="例如: https://github.com/your-name/cf-deploy-relay.git" />
                <!-- 白嫖方案说明 -->
                <div style="margin-top:6px;padding:8px 10px;background:#1a2a1a;border-left:3px solid #67c23a;border-radius:3px;font-size:12px;line-height:1.8">
                  <div style="font-weight:600;color:#67c23a;margin-bottom:4px">💡 完全免费方案：公开中继仓库</div>
                  <div style="color:#aaa">① 在 GitHub 创建一个 <b style="color:#e0e0e0">Public（公开）</b> 仓库，如 <code>https://github.com/your-name/cf-deploy-relay.git</code></div>
                  <div style="color:#aaa">② 系统会通过「一键配置」自动将工作流文件写入该仓库的 <code>.github/workflows/deploy.yml</code></div>
                  <div style="color:#aaa">③ 同时自动设置 <code>CLOUDFLARE_API_TOKEN</code>、<code>CLOUDFLARE_ACCOUNT_ID</code> 两个 Secret</div>
                  <div style="color:#aaa">④ 中继仓库为公开仓库时 Actions <b style="color:#e0e0e0">完全免费，不计分钟数</b></div>
                  <div style="color:#e6a23c;margin-top:4px">⚠️ 若 Action 仓库与代码仓库为<b>同一私有仓库</b>，每次触发将消耗 GitHub Actions 付费分钟数（私有仓库每月 2000 分钟免费额度）</div>
                  <div style="margin-top:4px">
                    <a style="color:#67c23a;cursor:pointer" @click="showGaTemplateDialog = true">📋 查看工作流模板</a>
                  </div>
                </div>
              </el-form-item>
              <el-form-item>
                <template #label>
                  GitHub Token
                  <el-tooltip placement="top">
                    <template #content>
                      <div style="font-size:12px;line-height:1.9">
                        <div style="font-weight:600;margin-bottom:4px">所需权限：</div>
                        <div>• repo（读写代码）</div>
                        <div>• workflow（触发 Actions）</div>
                        <div style="margin-top:4px;color:#e6a23c">在 GitHub → Settings → Developer settings → Tokens 创建 PAT</div>
                      </div>
                    </template>
                    <el-icon style="cursor:help;color:#909399;vertical-align:middle;margin-left:2px"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </template>
                <el-input v-model="deployForm.actionToken" type="password" show-password
                  placeholder="ghp_xxxx（需要 workflow 权限）" />
              </el-form-item>
              <el-form-item label="工作流文件">
                <el-input v-model="deployForm.actionWorkflow" placeholder="deploy.yml" style="margin-bottom:4px" />
                <div style="font-size:12px;color:#909399">
                  相对于仓库根目录的 workflow 文件名，默认为 <code>.github/workflows/deploy.yml</code>
                  <br/>
                  <a style="color:#67c23a;cursor:pointer" @click="showGaTemplateDialog = true">
                    🆓 查看中继仓库工作流模板
                  </a>
                </div>
              </el-form-item>
              <el-form-item label="触发分支">
                <el-input v-model="deployForm.actionRef" placeholder="main" />
              </el-form-item>
              <el-form-item label="安装命令">
                <el-input v-model="deployForm.installCommand" placeholder="pnpm install"><template #prepend>$</template></el-input>
                <div style="font-size:12px;color:#909399;margin-top:4px">Actions 中执行的依赖安装命令</div>
              </el-form-item>
              <el-form-item label="构建命令">
                <el-input v-model="deployForm.buildCommand" placeholder="pnpm run build"><template #prepend>$</template></el-input>
                <div style="font-size:12px;color:#909399;margin-top:4px">Actions 中执行的构建命令（同时也是工具栏「本地构建」按钮使用的命令）</div>
              </el-form-item>

              <!-- 当前配置状态 -->
              <el-form-item v-if="dispatchSetupStatus || dispatchSetupStatusLoading" label="当前状态">
                <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap">
                  <el-tooltip content="检查工作流文件是否已存在于远端 Action 仓库" placement="top">
                    <el-tag :type="dispatchSetupStatus && dispatchSetupStatus.workflowExists ? 'success' : 'info'" size="small">
                      {{ dispatchSetupStatus && dispatchSetupStatus.workflowExists ? '✓ 工作流文件已存在' : '○ 工作流文件未注入' }}
                    </el-tag>
                  </el-tooltip>
                  <!-- 内容是否与当前配置匹配 -->
                  <el-tooltip v-if="dispatchSetupStatus && dispatchSetupStatus.workflowExists" content="工作流文件内容是否与当前 branch/build 配置一致" placement="top">
                    <el-tag :type="dispatchSetupStatus.workflowContentMatch ? 'success' : 'warning'" size="small">
                      {{ dispatchSetupStatus.workflowContentMatch ? '✓ 内容与配置一致' : '⚠ 内容已变更，需重新注入' }}
                    </el-tag>
                  </el-tooltip>
                  <el-tooltip content="使用填入的 GitHub Token 验证是否有效" placement="top">
                    <el-tag :type="dispatchSetupStatus && dispatchSetupStatus.tokenValid === null ? 'info' : (dispatchSetupStatus && dispatchSetupStatus.tokenValid ? 'success' : 'danger')" size="small">
                      {{ dispatchSetupStatus && dispatchSetupStatus.tokenValid === null ? '○ Token 未填' : (dispatchSetupStatus && dispatchSetupStatus.tokenValid ? '✓ Token 有效' : '✕ Token 无效') }}
                    </el-tag>
                  </el-tooltip>
                  <el-tooltip content="CLOUDFLARE_API_TOKEN Secret 是否存在于 Action 仓库" placement="top">
                    <el-tag :type="dispatchSetupStatus && dispatchSetupStatus.secretApiToken ? 'success' : 'info'" size="small">
                      {{ dispatchSetupStatus && dispatchSetupStatus.secretApiToken ? '✓ CF_API_TOKEN 已设置' : '○ CF_API_TOKEN 未设置' }}
                    </el-tag>
                  </el-tooltip>
                  <el-tooltip content="CLOUDFLARE_ACCOUNT_ID Secret 是否存在于 Action 仓库" placement="top">
                    <el-tag :type="dispatchSetupStatus && dispatchSetupStatus.secretAccountId ? 'success' : 'info'" size="small">
                      {{ dispatchSetupStatus && dispatchSetupStatus.secretAccountId ? '✓ CF_ACCOUNT_ID 已设置' : '○ CF_ACCOUNT_ID 未设置' }}
                    </el-tag>
                  </el-tooltip>
                  <el-tooltip content="GIT_ACCESS_TOKEN Secret 是否存在于 Action 仓库（用于拉取代码仓库）" placement="top">
                    <el-tag :type="dispatchSetupStatus && dispatchSetupStatus.secretGitAccessToken ? 'success' : 'info'" size="small">
                      {{ dispatchSetupStatus && dispatchSetupStatus.secretGitAccessToken ? '✓ GIT_ACCESS_TOKEN 已设置' : '○ GIT_ACCESS_TOKEN 未设置' }}
                    </el-tag>
                  </el-tooltip>
                  <el-button link size="small" :loading="dispatchSetupStatusLoading" @click="checkDispatchSetupStatus">刷新状态</el-button>
                </div>
                <div v-if="dispatchSetupAllDone" style="font-size:12px;color:#67c23a;margin-top:6px">✅ 已完整配置，直接点击工具栏 [GitHub Actions] 按钮即可触发构建</div>
                <div v-else-if="dispatchSetupStatus && dispatchSetupStatus.workflowExists && !dispatchSetupStatus.workflowContentMatch" style="font-size:12px;color:#e6a23c;margin-top:6px">⚠ 工作流文件内容与当前配置不一致（如构建命令/分支已变更），请重新注入工作流</div>
                <div v-else-if="dispatchSetupStatus && dispatchSetupStatus.tokenValid === false" style="font-size:12px;color:#f56c6c;margin-top:6px">⚠ GitHub Token 无效，请检查是否过期或权限不足（需要 repo + workflow）</div>
              </el-form-item>

              <!-- 一键配置按钮 -->
              <el-form-item label="一键配置">
                <el-button
                  :type="dispatchSetupAllDone ? 'default' : 'primary'"
                  :loading="dispatchSetupLoading"
                  :disabled="!deployForm.actionRepo || !deployForm.actionToken"
                  @click="handleSetupGithubActionsDispatch"
                >
                  <el-icon v-if="!dispatchSetupLoading"><Setting /></el-icon>
                  {{ dispatchSetupLoading ? '配置中...' : (dispatchSetupAllDone ? '重新注入工作流' : '注入工作流 + 设置 Secrets') }}
                </el-button>
                <div style="font-size:12px;color:#909399;margin-top:6px">
                  将 <code>workflow_dispatch</code> 工作流 YAML 通过 GitHub API 直接写入远端 Action 仓库，同时设置 Cloudflare Secrets。
                  配置完成后每次点击 [GitHub Actions] 按钮即可触发一次构建。
                </div>
              </el-form-item>

              <!-- 配置日志 -->
              <el-form-item v-if="dispatchSetupLog" label="配置日志">
                <el-input type="textarea" :rows="6" readonly :value="dispatchSetupLog"
                  style="font-family:monospace;font-size:12px" />
              </el-form-item>
            </template>

            <!-- ===== github-actions-push: 当前仓库 Actions，推送自动触发 ===== -->
            <template v-if="deployForm.deployMode === 'github-actions-push'">
              <el-alert type="info" :closable="false" style="margin-bottom:12px">
                <template #title>
                  <span style="font-weight:600">当前仓库 GitHub Actions — 推送自动触发</span>
                </template>
                <div style="font-size:12px;line-height:2;margin-top:6px">
                  <div>模板项目中已内置 <code>.github/workflows/deploy.yml</code> 工作流，随代码一并推送到仓库后即可自动触发。</div>
                  <div>每次 <code>git push</code> 到指定分支，GitHub 会自动执行构建 + 部署，<b>无需手动点击触发</b>。</div>
                  <div style="margin-top:4px">点击「一键配置」可自动设置 <code>CF_API_TOKEN</code>、<code>CF_ACCOUNT_ID</code> 两个 GitHub Secret（使用已绑定 Git 账号的 Token 操作）。</div>
                  <div style="color:#e6a23c;margin-top:4px">⚠️ 若仓库为<b>私有仓库</b>，每次 Actions 运行将消耗分钟数（每月 2000 分钟免费额度）</div>
                </div>
              </el-alert>
              <el-form-item label="触发分支">
                <el-input v-model="deployForm.actionPushBranch" placeholder="main" />
                <div style="font-size:12px;color:#909399;margin-top:4px">push 到此分支时自动触发 Actions</div>
              </el-form-item>
              <el-form-item label="工作流文件路径">
                <el-input v-model="deployForm.actionPushWorkflow" placeholder=".github/workflows/deploy.yml" />
              </el-form-item>
              <el-form-item label="安装命令">
                <el-input v-model="deployForm.installCommand" placeholder="pnpm install"><template #prepend>$</template></el-input>
                <div style="font-size:12px;color:#909399;margin-top:4px">Actions 中执行的依赖安装命令</div>
              </el-form-item>
              <el-form-item label="构建命令">
                <el-input v-model="deployForm.buildCommand" placeholder="pnpm run build"><template #prepend>$</template></el-input>
                <div style="font-size:12px;color:#909399;margin-top:4px">Actions 中执行的构建命令（同时也是工具栏「本地构建」按钮使用的命令）</div>
              </el-form-item>
              <!-- 当前配置状态 -->
              <el-form-item v-if="pushSetupStatus || pushSetupStatusLoading" label="当前状态">
                <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap">
                  <!-- 工作流文件 -->
                  <el-tooltip content="检查本地仓库目录中工作流文件是否存在" placement="top">
                    <el-tag :type="pushSetupStatus && pushSetupStatus.workflowExists ? 'success' : 'info'" size="small">
                      {{ pushSetupStatus && pushSetupStatus.workflowExists ? '✓ 工作流文件存在' : '○ 工作流文件未生成' }}
                    </el-tag>
                  </el-tooltip>
                  <!-- Token 有效性 -->
                  <el-tooltip content="使用填入的 GitHub Token 调用 /user 接口验证是否有效" placement="top">
                    <el-tag :type="pushSetupStatus && pushSetupStatus.tokenValid === null ? 'info' : (pushSetupStatus && pushSetupStatus.tokenValid ? 'success' : 'danger')" size="small">
                      {{ pushSetupStatus && pushSetupStatus.tokenValid === null ? '○ Token 未填' : (pushSetupStatus && pushSetupStatus.tokenValid ? '✓ Token 有效' : '✕ Token 无效') }}
                    </el-tag>
                  </el-tooltip>
                  <!-- Secrets（GitHub API 不返回值，只能确认存在） -->
                  <el-tooltip content="GitHub API 不返回 Secret 实际值，仅能确认该 Secret 是否已存在于仓库" placement="top">
                    <el-tag :type="pushSetupStatus && pushSetupStatus.secretApiToken ? 'success' : (pushSetupStatus && pushSetupStatus.secretApiToken === null ? 'warning' : 'info')" size="small">
                      {{ pushSetupStatus && pushSetupStatus.secretApiToken ? '✓ CF_API_TOKEN 已存在' : (pushSetupStatus && pushSetupStatus.secretApiToken === null ? '○ CF_API_TOKEN（Token未填）': '○ CF_API_TOKEN 未设置') }}
                    </el-tag>
                  </el-tooltip>
                  <el-tooltip content="GitHub API 不返回 Secret 实际值，仅能确认该 Secret 是否已存在于仓库" placement="top">
                    <el-tag :type="pushSetupStatus && pushSetupStatus.secretAccountId ? 'success' : (pushSetupStatus && pushSetupStatus.secretAccountId === null ? 'warning' : 'info')" size="small">
                      {{ pushSetupStatus && pushSetupStatus.secretAccountId ? '✓ CF_ACCOUNT_ID 已存在' : (pushSetupStatus && pushSetupStatus.secretAccountId === null ? '○ CF_ACCOUNT_ID（Token未填）' : '○ CF_ACCOUNT_ID 未设置') }}
                    </el-tag>
                  </el-tooltip>
                  <el-button link size="small" :loading="pushSetupStatusLoading" @click="checkPushSetupStatus">刷新状态</el-button>
                </div>
                <div v-if="pushSetupAllDone" style="font-size:12px;color:#67c23a;margin-top:6px">✅ 已完整配置，推送代码到触发分支即可自动部署</div>
                <div v-else-if="pushSetupStatus && pushSetupStatus.tokenValid === false" style="font-size:12px;color:#f56c6c;margin-top:6px">⚠ GitHub Token 无效，请检查 Token 是否过期或权限不足（需要 repo + secrets write）</div>
              </el-form-item>
              <el-form-item label="一键配置">
                <el-button
                  :type="pushSetupAllDone ? 'default' : 'primary'"
                  :loading="pushSetupLoading"
                  @click="handleSetupGithubActionsPush"
                >
                  <el-icon v-if="!pushSetupLoading"><Setting /></el-icon>
                  {{ pushSetupLoading ? '配置中...' : (pushSetupAllDone ? '重新配置' : '注入工作流 + 设置 Secrets') }}
                </el-button>
                <span v-if="!pushSetupAllDone" style="font-size:12px;color:#909399;margin-left:8px">点击后将自动保存配置，再执行注入</span>
                <span v-else style="font-size:12px;color:#67c23a;margin-left:8px">已完整配置，如需更新可重新执行</span>
              </el-form-item>
              <!-- 配置结果日志 -->
              <template v-if="pushSetupLog">
                <el-form-item label="配置日志">
                  <pre style="background:#161616;color:#d4d4d4;padding:10px;border-radius:4px;font-size:12px;max-height:200px;overflow-y:auto;width:100%;margin:0;white-space:pre-wrap">{{ pushSetupLog }}</pre>
                </el-form-item>
                <el-form-item v-if="pushSetupActionsUrl" label="Actions 页面">
                  <a :href="pushSetupActionsUrl" target="_blank" rel="noopener" style="color:#409eff;font-size:13px">
                    <el-icon><TopRight /></el-icon> {{ pushSetupActionsUrl }}
                  </a>
                </el-form-item>
              </template>
            </template>
          </el-form>

          <!-- 操作按钮区域 -->
          <div style="display:flex;gap:8px;flex-wrap:wrap;align-items:center;margin-top:4px">
            <el-button type="primary" :loading="savingDeploy" @click="handleSaveDeploy">保存部署配置</el-button>

            <!-- 创建 CF Workers 项目按钮（无需去 CF 控制台手动创建） -->
            <template v-if="deployForm.deployPlatform === 'cloudflare-workers' && deployForm.deployMode !== 'cf-direct' && deployForm.cfAccountId && deployForm.cloudflareProjectName">
              <el-divider direction="vertical" />
              <el-tooltip :content="deployFormDirty ? '请先保存配置' : (cfProjectExists ? '项目已存在，再次点击可重置占位脚本' : '在 Cloudflare 中创建 Workers 项目（无需去 CF 控制台手动操作）')">
                <el-button
                  :type="cfProjectExists ? 'success' : 'primary'"
                  :loading="creatingProject || checkingProject"
                  :disabled="deployFormDirty"
                  @click="handleCreateCfProject"
                >
                  <el-icon><component :is="cfProjectExists ? 'CircleCheck' : 'Plus'" /></el-icon>
                  {{ cfProjectExists ? '已创建 Workers 项目' : '创建 CF Workers 项目' }}
                </el-button>
              </el-tooltip>
              <el-tooltip v-if="cfProjectExists === true" :content="deployFormDirty ? '请先保存配置' : '删除该 Workers 项目后可重新创建不同名称的项目'">
                <el-button
                  type="danger"
                  plain
                  :loading="deletingProject"
                  :disabled="deployFormDirty"
                  @click="handleDeleteCfProject"
                >
                  <el-icon><Delete /></el-icon>
                  删除项目
                </el-button>
              </el-tooltip>
              <span v-if="deployFormDirty" style="font-size:11px;color:#e6a23c">⚠ 配置已修改，请先保存</span>
              <a v-if="cfProjectUrl" :href="cfProjectUrl" target="_blank" rel="noopener"
                style="font-size:12px;color:#409eff;text-decoration:none;display:flex;align-items:center;gap:3px">
                <el-icon style="font-size:12px"><TopRight /></el-icon>{{ cfProjectUrl }}
              </a>
            </template>

            <!-- CF 直连模式：跳转到 CF 控制台手动绑定 GitHub -->
            <template v-if="deployForm.deployPlatform === 'cloudflare-workers' && deployForm.deployMode === 'cf-direct'">
              <el-divider direction="vertical" />
              <el-button
                type="success"
                tag="a"
                href="https://dash.cloudflare.com/0a223df7a80ba3b519ed601835c0edbd/workers-and-pages/create"
                target="_blank"
                rel="noopener"
              >
                <el-icon><TopRight /></el-icon>
                前往 CF 控制台绑定 GitHub 仓库
              </el-button>
            </template>
          </div>

          <!-- 域名绑定（Workers 已部署后显示） -->
          <template v-if="deployForm.deployPlatform === 'cloudflare-workers' && deployForm.cfAccountId">
            <el-divider>自定义域名绑定</el-divider>
            <div class="worker-domain-section">

              <!-- 操作前置说明 -->
              <el-collapse style="margin-bottom:10px;border:none">
                <el-collapse-item name="guide" style="border:1px solid #e4e7ed;border-radius:6px;overflow:hidden">
                  <template #title>
                    <span style="font-size:12px;color:#606266;padding-left:4px">
                      <el-icon style="vertical-align:-2px;margin-right:4px"><QuestionFilled /></el-icon>
                      如何绑定自定义域名？（点击展开配置指南）
                    </span>
                  </template>
                  <div style="padding:4px 8px 8px;font-size:12px;line-height:2;color:#606266">
                    <div style="font-weight:600;margin-bottom:4px;color:#303133">前提条件</div>
                    <div>① 你的域名必须已托管到 Cloudflare（即 DNS 命名服务器已更改为 Cloudflare 的 NS）。</div>
                    <div>
                      还没托管？前往
                      <a href="https://dash.cloudflare.com/" target="_blank" rel="noopener" style="color:#409eff">Cloudflare 控制台</a>
                      → <b>添加网站</b> → 输入你的根域名 → 按提示将域名注册商的 NS 改为 Cloudflare 提供的值，等待生效（通常几分钟~48小时）。
                    </div>
                    <div style="font-weight:600;margin:8px 0 4px;color:#303133">绑定步骤</div>
                    <div>
                      <el-tag size="small" type="info" style="margin-right:4px">步骤 1</el-tag>
                      域名托管到 CF 后，在下方<b>「根域名」下拉框</b>中应能看到该域名。若列表为空，请先点击右侧刷新按钮，或检查 API Token 是否具备 <code>Zone:Read</code> 权限。
                    </div>
                    <div style="margin-top:6px">
                      <el-tag size="small" type="info" style="margin-right:4px">步骤 2</el-tag>
                      在下方输入框填写<b>子域名前缀</b>（如填 <code>app</code> 将绑定 <code>app.example.com</code>），留空或填 <code>@</code> 表示直接绑定根域名本身。
                    </div>
                    <div style="margin-top:6px">
                      <el-tag size="small" type="info" style="margin-right:4px">步骤 3</el-tag>
                      在<b>「根域名」下拉框</b>选择对应的根域名（用于系统自动查询 Zone ID），然后点击<b>「绑定」</b>。
                    </div>
                    <div style="margin-top:6px">
                      <el-tag size="small" type="success" style="margin-right:4px">完成</el-tag>
                      绑定成功后 CF 会自动添加对应 DNS 记录，通常几秒内生效，域名即可访问你部署的 Worker。
                    </div>
                    <div style="margin-top:8px;padding:6px 8px;background:#fdf6ec;border-radius:4px;color:#e6a23c;font-size:11px">
                      ⚠ API Token 需具备以下权限：<code>Workers Routes:Edit</code>、<code>Zone:Read</code>、<code>DNS:Edit</code>。
                      可在 <a href="https://dash.cloudflare.com/profile/api-tokens" target="_blank" rel="noopener" style="color:#409eff">CF → 我的个人资料 → API 令牌</a> 中创建或编辑。
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>

              <!-- 域名列表 -->
              <div v-if="workerDomainLoading" style="color:#888;font-size:13px">加载中...</div>
              <template v-else>
                <el-alert v-if="workerDomainError" :title="workerDomainError" type="warning" :closable="false"
                  show-icon style="margin-bottom:8px;font-size:12px" />
                <el-table v-if="workerDomains.length > 0" :data="workerDomains" size="small" style="width:100%;margin-bottom:12px">
                  <el-table-column prop="hostname" label="域名" />
                  <el-table-column prop="environment" label="环境" width="90" />
                  <el-table-column label="操作" width="80" align="center">
                    <template #default="{ row }">
                      <el-popconfirm title="确认解绑此域名？" @confirm="handleUnbindDomain(row.id)">
                        <template #reference>
                          <el-button type="danger" size="small" plain>解绑</el-button>
                        </template>
                      </el-popconfirm>
                    </template>
                  </el-table-column>
                </el-table>
                <p v-else-if="!workerDomainError" style="color:#888;font-size:13px;margin:0 0 8px">暂无绑定域名</p>
              </template>

              <!-- 添加新域名 -->
              <div style="display:flex;gap:6px;align-items:center;flex-wrap:wrap">
                <el-input v-model="newDomainHostname" size="small"
                  placeholder="子域名，如 blog（留空或填 @ 表示根域名）"
                  style="flex:1;min-width:140px" @input="bindDomainError = ''">
                  <template #prepend style="padding:0 8px">https://</template>
                  <template #append>
                    <el-select v-model="newDomainZone" style="width:170px" size="small"
                      placeholder=".root-domain.com"
                      :loading="cfZonesLoading" clearable filterable
                      loading-text="加载中..."
                      no-data-text="无可用 Zone，请先验证 Token">
                      <template #prefix><span style="color:#909399;font-size:12px">.</span></template>
                      <el-option v-for="z in cfZones" :key="z.id" :label="'.' + z.name" :value="z.name" />
                    </el-select>
                  </template>
                </el-input>
                <el-button type="primary" size="small" :loading="bindingDomain" @click="handleBindDomain"
                  :disabled="!newDomainZone">
                  <el-icon><Plus /></el-icon> 绑定
                </el-button>
                <el-tooltip content="刷新域名列表和 Zone 列表">
                  <el-button size="small" :loading="cfZonesLoading || workerDomainLoading"
                    @click="() => { loadWorkerDomains(); loadCfZones() }">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
              <div v-if="newDomainZone" style="font-size:11px;color:#67c23a;margin-top:4px;padding-left:2px">
                将绑定域名：<b>{{ newDomainHostname && newDomainHostname !== '@' ? newDomainHostname + '.' + newDomainZone : newDomainZone }}</b>
              </div>
              <el-alert v-if="bindDomainError" :title="bindDomainError" type="error" :closable="true"
                show-icon style="margin-top:6px;font-size:12px" @close="bindDomainError = ''" />
            </div>
          </template>
        </el-tab-pane>

        <!-- 可视化配置 Tab -->
        <el-tab-pane label="环境配置" name="visual">
          <div v-if="visualConfigLoading" style="text-align:center;padding:40px">
            <el-icon class="is-loading" size="28"><Loading /></el-icon>
          </div>
          <template v-else>
            <el-form :model="envFormValues" label-position="top">
              <el-form-item label="站点 ID">
                <el-input :value="currentSiteId" disabled />
              </el-form-item>

              <!-- 模板定义的变量 -->
              <template v-if="envSchema.length">
                <el-divider content-position="left">模板定义的变量</el-divider>
                <el-form-item
                  v-for="field in envSchema"
                  :key="field.key"
                  :required="!!field.required"
                >
                  <template #label>
                    <span>{{ field.label || field.key }}</span>
                    <span v-if="field.label" style="font-size:12px;color:#909399;margin-left:6px">{{ field.key }}</span>
                  </template>
                  <el-input
                    v-model="envFormValues[field.key]"
                    :placeholder="field.description || field.defaultValue || ''"
                  />
                  <div v-if="field.description" style="font-size:12px;color:#909399;line-height:1.4;margin-top:2px">
                    {{ field.description }}
                  </div>
                </el-form-item>
              </template>
              <el-alert
                v-else
                type="info"
                :closable="false"
                style="margin-bottom:12px"
                title="当前站点未关联模板或模板未定义环境变量，可直接在下方添加自定义变量。"
              />

              <!-- 文件中存在但模板未定义的额外变量（来自 .env.production） -->
              <template v-if="extraEnvKeys.length">
                <el-divider content-position="left">.env.production 中已有变量</el-divider>
                <el-form-item
                  v-for="key in extraEnvKeys"
                  :key="key"
                  :label="key"
                >
                  <div style="display:flex;gap:8px;width:100%">
                    <el-input v-model="envFormValues[key]" style="flex:1" />
                    <el-button type="danger" plain @click="removeExtraKey(key)">删除</el-button>
                  </div>
                </el-form-item>
              </template>

              <!-- 自定义变量（用户自由添加） -->
              <el-divider content-position="left">自定义变量</el-divider>
              <el-form-item
                v-for="(row, idx) in customEnvRows"
                :key="idx"
              >
                <div style="display:flex;gap:8px;width:100%">
                  <el-input v-model="row.key" placeholder="变量名（如 NEXT_PUBLIC_API_URL）" style="flex:1" />
                  <el-input v-model="row.value" placeholder="值" style="flex:1" />
                  <el-button type="danger" plain @click="customEnvRows.splice(idx, 1)">删除</el-button>
                </div>
              </el-form-item>
              <el-button style="margin-bottom:16px" @click="customEnvRows.push({ key: '', value: '' })">+ 添加自定义变量</el-button>

              <el-alert
                v-if="!envFileExists"
                type="warning"
                :closable="false"
                style="margin-bottom:12px"
                title="未检测到本地 .env.production 文件，应用配置将生成该文件并推送到 Git 仓库。请确保后端代码已拉取到本地。"
              />
              <div style="display:flex;gap:8px">
                <el-button type="primary" :loading="savingVisualConfig" @click="handleSaveVisualConfig">
                  应用并推送
                </el-button>
                <el-button @click="handleLoadVisualConfig">刷新</el-button>
              </div>
            </el-form>
            <el-collapse v-if="visualConfigLog" style="margin-top:12px">
              <el-collapse-item title="操作日志">
                <pre style="font-size:12px;max-height:200px;overflow:auto;white-space:pre-wrap">{{ visualConfigLog }}</pre>
              </el-collapse-item>
            </el-collapse>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <!-- ===== GitHub Actions 工作流模板弹窗 ===== -->
    <el-dialog v-model="showGaTemplateDialog" title="GitHub Actions 工作流模板（公开中继仓库）" width="720px">
      <el-alert type="success" :closable="false" style="margin-bottom:8px">
        <div style="font-size:12px;line-height:1.9">
          <div>将此工作流保存至一个 <b>Public 公开仓库</b> 的 <code>.github/workflows/deploy.yml</code>，在该仓库的 <b>Settings → Secrets</b> 添加：</div>
          <div>• <code>CLOUDFLARE_API_TOKEN</code> &nbsp;• <code>CLOUDFLARE_ACCOUNT_ID</code> &nbsp;• <code>GIT_ACCESS_TOKEN</code>（读取私有代码仓库的 GitHub PAT）</div>
          <div style="margin-top:4px;color:#529b2e;font-weight:600">公开仓库 GitHub Actions 完全免费，无分钟限制。代码仓库可保持私有。也可通过「一键配置」自动注入此文件。</div>
        </div>
      </el-alert>
      <el-input type="textarea" :rows="26" readonly :value="gaRelayTemplate"
        style="font-family:monospace;font-size:12px" />
      <template #footer>
        <el-button type="primary" @click="copyGaTemplate">复制模板</el-button>
        <el-button @click="showGaTemplateDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ===== 新建文件/文件夹弹窗 ===== -->
    <el-dialog v-model="showNewFileDialog" :title="newFileForm.isDirectory ? '新建文件夹' : '新建文件'" width="400px"
      :close-on-click-modal="false" @keydown.enter.prevent="doCreateFile">
      <el-form label-width="80px">
        <el-form-item label="位置">
          <span style="color:#888;font-size:12px">{{ newFileForm.parentPath || '/ (根目录)' }}</span>
        </el-form-item>
        <el-form-item :label="newFileForm.isDirectory ? '文件夹名' : '文件名'">
          <el-input v-model="newFileForm.name" :placeholder="newFileForm.isDirectory ? '例如: components' : '例如: index.ts'"
            autofocus clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewFileDialog = false">取消</el-button>
        <el-button type="primary" @click="doCreateFile">创建</el-button>
      </template>
    </el-dialog>

    <!-- ===== 重命名弹窗 ===== -->
    <el-dialog v-model="showRenameDialog" title="重命名" width="400px"
      :close-on-click-modal="false" @keydown.enter.prevent="doRename">
      <el-form label-width="80px">
        <el-form-item label="原名称">
          <span style="color:#888;font-size:12px">{{ renameForm.oldName }}</span>
        </el-form-item>
        <el-form-item label="新名称">
          <el-input v-model="renameForm.newName" autofocus clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRenameDialog = false">取消</el-button>
        <el-button type="primary" @click="doRename">确认</el-button>
      </template>
    </el-dialog>

    <!-- ===== 复制弹窗 ===== -->
    <el-dialog v-model="showCopyDialog" title="复制文件" width="440px"
      :close-on-click-modal="false" @keydown.enter.prevent="doCopy">
      <el-form label-width="90px">
        <el-form-item label="源文件">
          <span style="color:#888;font-size:12px">{{ copyForm.sourcePath }}</span>
        </el-form-item>
        <el-form-item label="目标路径">
          <el-input v-model="copyForm.destPath" placeholder="相对仓库根目录的路径" autofocus clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCopyDialog = false">取消</el-button>
        <el-button type="primary" @click="doCopy">复制</el-button>
      </template>
    </el-dialog>

    <!-- ===== Git Diff 弹窗 ===== -->
    <el-dialog v-model="showGitDiffDialog" :title="gitDiffTitle" width="1000px"
      :close-on-click-modal="false">
      <div class="diff-viewer-wrap">
        <div v-if="gitDiffLoading" style="padding:32px;color:#888;text-align:center">
          <el-icon class="spin" style="font-size:24px"><Loading /></el-icon>
        </div>
        <template v-else>
          <div v-for="(file, fi) in parsedDiff" :key="fi" class="diff-file-block">
            <div class="diff-file-header">
              <span class="diff-file-old">{{ file.from }}</span>
              <span style="margin:0 6px;opacity:.5">→</span>
              <span class="diff-file-new">{{ file.to }}</span>
            </div>
            <div v-for="(hunk, hi) in file.hunks" :key="hi">
              <div class="diff-hunk-header">{{ hunk.header }}</div>
              <table class="diff-table">
                <tbody>
                  <tr v-for="(ln, li) in hunk.lines" :key="li" :class="'diff-line diff-' + ln.type">
                    <td class="diff-lno">{{ ln.oldNo }}</td>
                    <td class="diff-lno">{{ ln.newNo }}</td>
                    <td class="diff-sign">{{ ln.sign }}</td>
                    <td class="diff-code"><span v-html="ln.html"></span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div v-if="!parsedDiff.length" style="padding:24px;color:#888;text-align:center">无变更内容</div>
        </template>
      </div>
      <template #footer>
        <el-button @click="showGitDiffDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ===== Git Log 弹窗 ===== -->
    <el-dialog v-model="showGitLogDialog" title="提交历史 (git log)" width="960px"
      :close-on-click-modal="false" class="git-log-dialog">
      <!-- 工具栏 -->
      <div class="git-log-toolbar">
        <div class="git-log-toolbar-left">
          <el-icon style="color:#888;font-size:13px"><Filter /></el-icon>
          <span class="gl-label">分支</span>
          <el-select
            v-model="gitLogBranchFilter"
            size="small"
            placeholder="所有分支 (--all)"
            clearable
            style="width:220px"
            popper-class="git-log-branch-select"
            @change="() => loadGitLog()"
          >
            <el-option v-for="b in gitLogAllBranches" :key="b" :label="b" :value="b" />
          </el-select>
          <el-tooltip content="同步远端（git fetch --prune）并刷新全部历史" placement="top" :show-after="300">
            <el-button size="small" :loading="gitLogFetchLoading" @click="doFetchThenReload" class="gl-sync-btn">
              <el-icon><Refresh /></el-icon>
              <span>同步远端</span>
            </el-button>
          </el-tooltip>
        </div>
        <span class="gl-count">共 <b>{{ gitLogList.length }}</b> 条提交</span>
      </div>

      <!-- 加载中 -->
      <div v-if="gitLogLoading" class="gl-loading">
        <el-icon class="spin" style="font-size:22px"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      <!-- 空状态 -->
      <div v-else-if="gitLogList.length === 0" class="gl-empty">暂无提交记录</div>

      <!-- 提交列表 -->
      <div v-else class="git-log-list">
        <div
          v-for="row in gitGraphRows"
          :key="row.commit.hash"
          class="git-log-item"
          :class="{ 'gl-is-head': row.commit.refs?.some(r => r.type === 'head') }"
        >
          <!-- Git 分支图 -->
          <div class="gl-graph" :style="{ width: row.svgW + 'px', minWidth: row.svgW + 'px' }">
            <svg :width="row.svgW" :height="36" style="display:block;overflow:visible">
              <template v-for="seg in row.segments" :key="seg.key">
                <path v-if="seg.type === 'path'" :d="seg.d" :stroke="seg.color"
                  stroke-width="1.5" fill="none" stroke-linecap="round" />
                <circle v-else-if="seg.type === 'circle'" :cx="seg.cx" :cy="seg.cy" :r="seg.r"
                  :fill="seg.color" :stroke="seg.isBold ? '#1e1e1e' : 'rgba(0,0,0,0.5)'"
                  :stroke-width="seg.isBold ? 2 : 1"
                  :style="seg.isBold ? 'filter:drop-shadow(0 0 3px ' + seg.color + ')' : ''" />
              </template>
            </svg>
          </div>

          <!-- 主体内容 -->
          <div class="gl-body">
            <!-- 第一行：hash + refs + 消息 -->
            <div class="gl-row1">
              <span class="git-log-hash">{{ row.commit.shortHash }}</span>
              <span v-if="row.commit.refs?.length" class="git-log-refs">
                <span
                  v-for="ref in row.commit.refs"
                  :key="ref.name"
                  class="git-log-ref-chip"
                  :class="'ref-' + ref.type"
                >{{ ref.short }}</span>
              </span>
              <span class="git-log-msg" :title="row.commit.message">{{ row.commit.message }}</span>
            </div>
            <!-- 第二行：作者 + 日期 -->
            <div class="gl-row2">
              <el-icon><Avatar /></el-icon>
              <span>{{ row.commit.author }}</span>
              <span class="gl-sep">·</span>
              <el-icon><Clock /></el-icon>
              <span>{{ row.commit.date }}</span>
            </div>
          </div>

          <!-- 操作按钮（hover 显示） -->
          <div class="git-log-actions">
            <el-tooltip content="检出到此提交 (Detached HEAD)" placement="top" :show-after="300">
              <el-button size="small" plain circle
                :loading="commitActionLoading === row.commit.hash + '_co'"
                @click.stop="doCheckoutCommit(row.commit)"
              ><el-icon><Aim /></el-icon></el-button>
            </el-tooltip>
            <el-tooltip content="基于此提交创建分支" placement="top" :show-after="300">
              <el-button size="small" plain circle type="success"
                :loading="commitActionLoading === row.commit.hash + '_br'"
                @click.stop="doBranchFromCommit(row.commit)"
              ><el-icon><Share /></el-icon></el-button>
            </el-tooltip>
            <el-tooltip content="合并此提交到当前分支 (git merge)" placement="top" :show-after="300">
              <el-button size="small" plain circle type="primary"
                :loading="commitActionLoading === row.commit.hash + '_mg'"
                @click.stop="doMergeCommit(row.commit)"
              ><el-icon><Connection /></el-icon></el-button>
            </el-tooltip>
            <el-tooltip content="Revert 此提交（生成反向提交）" placement="top" :show-after="300">
              <el-button size="small" plain circle type="warning"
                :loading="commitActionLoading === row.commit.hash + '_rv'"
                @click.stop="doRevertCommit(row.commit)"
              ><el-icon><RefreshLeft /></el-icon></el-button>
            </el-tooltip>
            <el-tooltip content="⚠️ 将对当前工作区分支执行 Reset，不可恢复" placement="top" :show-after="300">
              <el-button size="small" plain circle type="danger"
                :loading="commitActionLoading === row.commit.hash + '_rf'"
                @click.stop="doResetForcePush(row.commit)"
              ><el-icon><Scissor /></el-icon></el-button>
            </el-tooltip>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showGitLogDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ===== 上传文件对话框 ===== -->
    <!-- 隐藏文件多选 input -->
    <input ref="uploadFileInputRef" type="file" multiple style="display:none" @change="handleFileInputChange" />
    <!-- 隐藏文件夹 input -->
    <input ref="uploadFolderInputRef" type="file" webkitdirectory style="display:none" @change="handleFolderInputChange" />

    <el-dialog v-model="showUploadDialog" title="上传文件 / 文件夹" width="560px" :close-on-click-modal="false"
      @close="cancelUpload">
      <div style="margin-bottom:12px;color:#888;font-size:13px">
        目标目录：<strong style="color:#e6a23c">{{ uploadDirPath || '/ (根目录)' }}</strong>
      </div>

      <!-- 拖拽区域 -->
      <div
        v-if="!uploadProgressList.length"
        class="upload-drop-zone"
        :class="{ 'drop-active': uploadDragActive, 'is-reading': uploadReading }"
        @dragover.prevent="uploadDragActive = true"
        @dragleave.prevent="uploadDragActive = false"
        @drop.prevent="handleDropUpload"
      >
        <div v-if="uploadReading" style="color:#409eff">
          <el-icon class="spin" style="font-size:28px;display:block;margin:0 auto 8px"><Loading /></el-icon>
          正在读取文件夹...
        </div>
        <template v-else-if="!pendingUploadFiles.length">
          <el-icon style="font-size:36px;display:block;margin:0 auto 8px;color:#555"><Upload /></el-icon>
          <div style="color:#ccc;font-size:14px">将文件或文件夹拖到此处</div>
          <div style="color:#666;font-size:12px;margin-top:4px">支持同时拖入文件和文件夹、保留目录结构</div>
        </template>
        <template v-else>
          <div style="max-height:160px;overflow-y:auto;width:100%">
            <div v-for="(item, i) in pendingUploadFiles" :key="item.uid"
              style="display:flex;align-items:center;gap:6px;padding:3px 0;font-size:12px;border-bottom:1px solid #2a2a2a;text-align:left">
              <el-icon style="flex-shrink:0;color:#909399"><Document /></el-icon>
              <span style="flex:1;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:#ccc"
                :title="item.relativePath || item.name">{{ item.relativePath || item.name }}</span>
              <span style="color:#666;flex-shrink:0;font-size:11px">{{ formatBytes(item.raw.size) }}</span>
              <el-icon style="cursor:pointer;color:#f56c6c;flex-shrink:0" @click.stop="removeUploadFile(i)"><Close /></el-icon>
            </div>
          </div>
        </template>
      </div>

      <!-- 上传进度列表 -->
      <div v-if="uploadProgressList.length"
        style="max-height:220px;overflow-y:auto;border:1px solid #3a3a3a;border-radius:4px;padding:6px 8px">
        <div v-for="(item, i) in uploadProgressList" :key="i"
          style="display:flex;align-items:center;gap:8px;padding:3px 0;font-size:12px;border-bottom:1px solid #2a2a2a">
          <el-icon v-if="item.status === 'success'" style="color:#67c23a;flex-shrink:0"><CircleCheck /></el-icon>
          <el-icon v-else-if="item.status === 'fail'" style="color:#f56c6c;flex-shrink:0"><CircleClose /></el-icon>
          <el-icon v-else class="spin" style="color:#409eff;flex-shrink:0"><Loading /></el-icon>
          <span style="flex:1;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;color:#ccc"
            :title="item.name">{{ item.name }}</span>
          <span style="flex-shrink:0;font-size:11px" :style="{color: item.status==='success'?'#67c23a':item.status==='fail'?'#f56c6c':'#909399'}">
            {{ item.status === 'success' ? '成功' : item.status === 'fail' ? '失败' : '上传中...' }}
          </span>
        </div>
      </div>

      <!-- 操作按钮行 -->
      <div v-if="!uploadProgressList.length" style="display:flex;align-items:center;gap:8px;margin-top:10px">
        <el-button size="small" :disabled="uploading || uploadReading" @click="uploadFileInputRef && uploadFileInputRef.click()">
          <el-icon><Document /></el-icon> 选文件
        </el-button>
        <el-button size="small" :disabled="uploading || uploadReading" @click="uploadFolderInputRef && uploadFolderInputRef.click()">
          <el-icon><Folder /></el-icon> 选文件夹
        </el-button>
        <el-button v-if="pendingUploadFiles.length" size="small" type="danger" plain
          style="margin-left:auto" @click="pendingUploadFiles = []; uploadProgressList = []">
          清空
        </el-button>
      </div>

      <template #footer>
        <span style="font-size:12px;color:#888;margin-right:auto">
          {{ pendingUploadFiles.length ? `共 ${pendingUploadFiles.length} 个文件` : '' }}
        </span>
        <el-button @click="cancelUpload" :disabled="uploading">取消</el-button>
        <el-button type="primary" :loading="uploading"
          :disabled="pendingUploadFiles.length === 0 || uploadReading"
          @click="doUploadFiles">
          开始上传
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onUnmounted, nextTick, markRaw } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as monacoLib from 'monaco-editor'
import { listSite } from '@/api/gamebox/site'
import { getSiteTemplate } from '@/api/gamebox/siteTemplate'
import { listEnabledCfAccounts } from '@/api/gamebox/cfAccount'
import { listEnabledGitAccounts } from '@/api/gamebox/gitAccount'
import SiteSelect from '@/components/SiteSelect/index.vue'
import {
  getCodeManageInfo, getRepoInfo, getRepoSyncStatus,
  saveDeployConfig,
  pullCode, buildProject, installDependencies, getPullStatus, getInstallStatus, startPreview, stopPreview, getPreviewStatus,
  listFiles, readFile, saveFile, pushCode, getPushStatus,
  deleteFile, renameFile, copyFile, createFile, uploadFile,
  getGitStatus, getGitDiff, getGitLog, getGitOriginal,
  gitStage, gitUnstage, gitCommit, gitPushOnly, gitDiscard,
  getNodeTypes, gitListBranches, gitCheckout, gitCreateBranch, gitDeleteBranch, gitDeleteRemoteBranch, gitFetch, gitPushBranch,
  gitCheckoutCommit, gitBranchFromCommit, gitMergeCommit, gitRevertCommit, gitResetForcePush, gitForcePush,
  gitMergeBranch, gitMergeAbort,
  deployToWorkers, listWorkerDomains, bindWorkerDomain, unbindWorkerDomain, listCfZones,
  verifyCfCredentials, createCfProject, checkCfProject, deleteCfProject, getBuildStatus,
  triggerGithubAction, getGithubActionStatus, setupGithubActionsPush, checkGithubActionsPushStatus,
  setupGithubActionsDispatch, checkGithubActionsDispatchStatus,
  getVisualConfig, saveVisualConfig as saveVisualConfigApi
} from '@/api/gamebox/codeManage'

const route = useRoute()

// ===== 基础状态 =====
const siteList = ref([])
const siteListLoading = ref(false)
const currentSiteId = ref(null)
const currentSite = ref(null)
const hasRepo = ref(false)
const repoInfo = ref({ branch: 'main' })

// ===== 布局 =====
const showSidebar = ref(true)
const showPreview = ref(false)
const sidebarWidth = ref(300)
const previewWidth = ref(420)

// ===== 文件树 =====
const fileTreeRef = ref(null)
const treeKey = ref(0)

// ===== 编辑器 =====
const monacoContainer = ref(null)
const diffContainer = ref(null)
const isDiffMode = ref(false)
const diffFilePath = ref('')
let diffEditorInstance = null
const fileLoading = ref(false)
const fileLoadingName = ref('')
let monacoInstance = null
let isSettingModel = false  // 防止 setModel 时触发 onDidChangeModelContent 造成循环

// 已打开文件：tab 列表
const openedFiles = ref([])  // [{ path, name, ext, dirty }]
// 已打开文件内容映射：path -> { content, originalContent, dirty, model }
const openedFilesMap = reactive({})
const activeFilePath = ref('')
const saving = ref(false)

// ===== 预览 =====
const previewFrame = ref(null)
const previewPath = ref('/')
const previewAddressBar = ref('')
// iframe 的 src 应由显式导航控制，不直接绑定响应式计算属性，
// 避免 syncPreviewAddress 更新 previewPath 后 Vue 被动重设 src 属性导致强制刷新
const iframeSrc = ref('about:blank')
const previewLoading = ref(false)
const previewStatus = ref({ running: false, port: null })
let previewPollingTimer = null

// ===== Git 操作 =====
const pulling = ref(false)
const building = ref(false)
const installing = ref(false)
const pushing = ref(false)
const showPushDialog = ref(false)
const pushCommitMessage = ref('')
const showLogDialog = ref(false)
const logDialogTitle = ref('')
const operationLog = ref('')
const logConsoleRef = ref(null)
const lastDeployTime = ref('')
const lastDeployLog = ref('')
const isViewingLastLog = ref(false)

// ===== 配置弹窗 =====
const showGitDialog = ref(false)
const gitDialogTab = ref('git')

// 可视化配置（动态环境变量）
const envSchema = ref([])          // 模板定义的变量列表 [{key, label, defaultValue, type, required, description}]
const envFormValues = ref({})      // KEY → VALUE 当前值
const envFileExists = ref(true)
const visualConfigLoading = ref(false)
const savingVisualConfig = ref(false)
const visualConfigLog = ref('')

const extraEnvKeys = computed(() => {
  const schemaKeys = new Set(envSchema.value.map(f => f.key))
  return Object.keys(envFormValues.value).filter(k => !schemaKeys.has(k))
})

const customEnvRows = ref([])

function removeExtraKey(key) {
  const vals = { ...envFormValues.value }
  delete vals[key]
  envFormValues.value = vals
}

watch(gitDialogTab, (tab) => {
  if (tab === 'visual' && currentSiteId.value) handleLoadVisualConfig()
})

// 弹窗打开时，如果已经在 visual tab，立即加载
watch(showGitDialog, (visible) => {
  if (visible && gitDialogTab.value === 'visual' && currentSiteId.value) handleLoadVisualConfig()
})

// 站点切换时，如果弹窗已打开且在 visual tab，重新加载
watch(currentSiteId, () => {
  if (showGitDialog.value && gitDialogTab.value === 'visual') {
    envSchema.value = []
    envFormValues.value = {}
    handleLoadVisualConfig()
  }
})

function handleLoadVisualConfig() {
  if (!currentSiteId.value) return
  visualConfigLoading.value = true
  visualConfigLog.value = ''
  customEnvRows.value = []
  getVisualConfig(currentSiteId.value).then(res => {
    const d = res.data || {}
    envSchema.value = d.envSchema || []
    envFileExists.value = d.envFileExists !== false
    // 合并：以 envValues 为主，缺失字段用 schema defaultValue 补
    const vals = { ...(d.envValues || {}) }
    for (const field of envSchema.value) {
      if (!(field.key in vals) && field.defaultValue) {
        vals[field.key] = field.defaultValue
      }
    }
    envFormValues.value = vals
  }).catch(e => {
    ElMessage.error('读取配置失败')
  }).finally(() => {
    visualConfigLoading.value = false
  })
}

function handleSaveVisualConfig() {
  if (!currentSiteId.value) return
  // 校验必填
  for (const field of envSchema.value) {
    if (field.required && !envFormValues.value[field.key]) {
      ElMessage.warning(`请填写「${field.label || field.key}」`)
      return
    }
  }
  savingVisualConfig.value = true
  visualConfigLog.value = ''
  // 合并自定义变量行
  const merged = { ...envFormValues.value }
  for (const row of customEnvRows.value) {
    if (row.key && row.key.trim()) merged[row.key.trim()] = row.value
  }
  saveVisualConfigApi(currentSiteId.value, {
    envVars: merged
  }).then(res => {
    const d = res.data || {}
    visualConfigLog.value = d.log || ''
    if (d.success) {
      ElMessage.success(d.message || '配置已应用并推送')
    } else {
      ElMessage.warning(d.message || '应用失败')
    }
  }).catch(e => {
    ElMessage.error('应用配置失败')
  }).finally(() => {
    savingVisualConfig.value = false
  })
}

const savingDeploy = ref(false)
const cfAccounts = ref([])
const cfAccountsLoading = ref(false)
async function loadCfAccounts() {
  cfAccountsLoading.value = true
  try {
    const res = await listEnabledCfAccounts()
    cfAccounts.value = (res.data || [])
  } catch { cfAccounts.value = [] }
  finally { cfAccountsLoading.value = false }
}
const gitAccounts = ref([])
const gitAccountsLoading = ref(false)
async function loadGitAccounts() {
  gitAccountsLoading.value = true
  try {
    const res = await listEnabledGitAccounts()
    gitAccounts.value = (res.data || [])
  } catch { gitAccounts.value = [] }
  finally { gitAccountsLoading.value = false }
}
function resolveGitAccountName(id) {
  if (!id) return null
  const acc = gitAccounts.value.find(a => a.id === id || String(a.id) === String(id))
  return acc ? (acc.name || acc.accountName) : null
}

const deployForm = ref({
  deployPlatform: 'local',
  cfAccountId: null,
  cloudflareProjectName: '',
  installCommand: 'pnpm install',
  buildCommand: 'pnpm run build',
  previewCommand: 'pnpm run dev',
  deployCommand: 'npx wrangler deploy',
  // GitHub Actions 部署配置
  deployMode: 'server',      // 'server' | 'github-actions' | 'github-actions-push' | 'cf-direct'
  // Mode 1: 开放 Action 仓库（手动触发）
  actionRepo: '',            // owner/repo（推荐使用公开仓库以节省分钟数）
  actionToken: '',           // GitHub PAT（需要 workflow 权限）
  actionWorkflow: 'deploy.yml',
  actionRef: 'main',
  // Mode 2: 当前仓库 Action（推送自动触发）
  actionPushToken: '',       // GitHub PAT（需要 repo + secrets write）
  actionPushBranch: 'main',  // 触发分支
  actionPushWorkflow: '.github/workflows/deploy.yml'
})

// ===== GitHub Actions Push 模式 =====
const pushSetupLoading = ref(false)
const pushSetupLog = ref('')
const pushSetupActionsUrl = ref('')
const pushSetupStatus = ref(null)   // null | { workflowExists, workflowContentMatch, tokenValid, secretApiToken, secretAccountId }
const pushSetupStatusLoading = ref(false)
const pushSetupAllDone = computed(() => {
  const s = pushSetupStatus.value
  return !!(s && s.workflowExists && s.workflowContentMatch && s.tokenValid && s.secretApiToken && s.secretAccountId)
})

// ===== Cloudflare Workers 部署 =====
const deploying = ref(false)
const workerDomains = ref([])
const workerDomainLoading = ref(false)
const workerDomainError = ref('')
const bindDomainError = ref('')
const cfZones = ref([])          // [{id, name}]
const cfZonesLoading = ref(false)
const bindingDomain = ref(false)
const newDomainHostname = ref('')
const newDomainZone = ref('')
const cfVerifying = ref(false)
const cfVerifyResult = ref(null)  // { success, message, accountName }
const creatingProject = ref(false)
const cfProjectExists = ref(null)   // null=未检查, true=已存在, false=不存在
const cfProjectUrl = ref('')        // workers.dev 地址
const checkingProject = ref(false)
const deletingProject = ref(false)
// 追踪部署表单是否有未保存的修改（名称/账号/平台相关字段）
const deployFormSaved = ref({ deployPlatform: '', cfAccountId: null, cloudflareProjectName: '' })
const deployFormDirty = computed(() => {
  const f = deployForm.value, s = deployFormSaved.value
  return f.deployPlatform !== s.deployPlatform || f.cfAccountId !== s.cfAccountId || f.cloudflareProjectName !== s.cloudflareProjectName
})

// ===== GitHub Actions 部署 =====
const gaTriggering = ref(false)     // 是否正在触发/运行
const gaRunUrl = ref('')            // GitHub Actions 运行页面链接
let gaPollStopped = true
let gaPollTimer = null
const showGaTemplateDialog = ref(false)
const gaTemplateTab = ref('relay')

// ===== GitHub Actions 手动触发（dispatch）Setup =====
const dispatchSetupLoading = ref(false)
const dispatchSetupLog = ref('')
const dispatchSetupStatus = ref(null) // null | { workflowExists, workflowContentMatch, tokenValid, secretApiToken, secretAccountId, secretGitAccessToken }
const dispatchSetupStatusLoading = ref(false)
const dispatchSetupAllDone = computed(() => {
  const s = dispatchSetupStatus.value
  return !!(s && s.workflowExists && s.workflowContentMatch && s.tokenValid && s.secretApiToken && s.secretAccountId && s.secretGitAccessToken)
})

// ===== 文件操作 =====
// 右键菜单
const ctxMenu = reactive({ visible: false, x: 0, y: 0, node: null, data: null })
// 新建文件/文件夹弹窗
const showNewFileDialog = ref(false)
const newFileForm = reactive({ name: '', isDirectory: false, parentPath: '' })
// 重命名弹窗
const showRenameDialog = ref(false)
const renameForm = reactive({ oldPath: '', newName: '', oldName: '' })
// 复制弹窗
const showCopyDialog = ref(false)
const copyForm = reactive({ sourcePath: '', destPath: '', sourceName: '' })
// 文件上传
const uploadDirPath = ref('')
const uploading = ref(false)
const showUploadDialog = ref(false)
const pendingUploadFiles = ref([])   // [{ uid, name, raw, relativePath }]
const uploadProgressList = ref([])   // [{ name, status }]
const uploadFileInputRef = ref(null)
const uploadFolderInputRef = ref(null)
const uploadDragActive = ref(false)
const uploadReading = ref(false)

// ===== Git 面板 =====
const sidebarTab = ref('files')  // 'files' | 'git' | 'config'

// ===== 配置编辑器 =====
const cfgMappings = ref([])
const cfgValues = ref({})
const cfgOriginalValues = ref({})
const cfgLoading = ref(false)
const cfgSaving = ref(false)
const cfgActiveKey = ref('')
const cfgFormPanelRef = ref(null)
const cfgDirtyKeys = computed(() => {
  const s = new Set()
  for (const item of cfgMappings.value) {
    if (String(cfgValues.value[item.key] ?? '') !== String(cfgOriginalValues.value[item.key] ?? '')) {
      s.add(item.key)
    }
  }
  return s
})
const cfgGroups = computed(() => {
  const groups = {}
  for (const item of cfgMappings.value) {
    const g = item.group || '基本配置'
    if (!groups[g]) groups[g] = { name: g, items: [] }
    groups[g].items.push(item)
  }
  return Object.values(groups)
})
const gitStagedList = ref([])      // 已暂存
const gitUnstagedList = ref([])    // 未暂存/未跟踪
const gitStatusLoading = ref(false)
const commitMessage = ref('')
const gitCommitting = ref(false)
const gitPushing = ref(false)
const pendingPushCount = ref(0) // 已提交但未推送的次数
const needsForcePush = ref('')  // 非空时表示 reset 后需要强制推送，存储分支名
const gitTotalCount = computed(() => gitStagedList.value.length + gitUnstagedList.value.length)
const gitConflictList = computed(() => gitUnstagedList.value.filter(item => statusConflictFiles.value.includes(item.path)))
const gitCleanUnstagedList = computed(() => gitUnstagedList.value.filter(item => !statusConflictFiles.value.includes(item.path)))
// 兼容旧引用（徽标数量用） - 旧模板中如有依赖此属性
const gitStatusList = computed(() => [...gitStagedList.value, ...gitUnstagedList.value])
const showGitDiffDialog = ref(false)
const gitDiffContent = ref('')
const gitDiffTitle = ref('')

// ===== 分支管理 =====
const showBranchDialog = ref(false)
const branchList = ref([])          // [{ name, current }]
const remoteOnlyBranches = ref([])  // 所有远端分支 [{ name, hasLocal }]
const branchLoading = ref(false)
const fetchingRemote = ref(false)
const checkingOutBranch = ref('')
const pushingBranchName = ref('')
const deletingBranch = ref('')
const deletingRemoteBranch = ref('')
const newBranchName = ref('')
const creatingBranch = ref(false)
const mergingBranchName = ref('')   // 正在合并的分支名
// ===== 合并冲突对话框 =====
const showMergeConflictDialog = ref(false)
const mergeConflictSourceBranch = ref('')
const mergeConflictFiles = ref([])
const abortingMerge = ref(false)
// ===== 合并中状态（来自 loadGitStatus） =====
const isMerging = ref(false)         // 当前仓库处于 MERGE_HEAD 状态
const statusConflictFiles = ref([])  // 来自 git status 的冲突文件
// ===== 冲突解决器 =====
const isConflictMode = ref(false)
const conflictResolverFilePath = ref('')
const conflictResolverFileName = ref('')
const conflictSaving = ref(false)
const conflictCurrentIdx = ref(0)   // 当前聚焦的冲突块序号（0起）
const conflictTotalCount = ref(0)   // 当前文件中剩余冲突块数量
const conflictDecorationIds = ref([]) // Monaco 装饰 ID 列表
const conflictCurrentHead = ref([])     // 当前/最近冲突块的 HEAD 内容行
const conflictCurrentIncoming = ref([]) // 当前/最近冲突块的传入内容行
const conflictCurrentRange = ref(null)  // 最近一次解决后的行范围 {startLine,endLine}，用于重新选择
const gitDiffLoading = ref(false)

/** 解析 unified diff 文本为结构化对象 */
const parsedDiff = computed(() => {
  const text = gitDiffContent.value
  if (!text) return []
  const files = []
  let cur = null
  let curHunk = null
  let oldNo = 0
  let newNo = 0
  const escHtml = s => s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  for (const raw of text.split('\n')) {
    if (raw.startsWith('diff --git')) {
      cur = { from: '', to: '', hunks: [] }
      files.push(cur)
      curHunk = null
    } else if (raw.startsWith('--- ') && cur) {
      cur.from = raw.slice(4).replace(/^a\//, '')
    } else if (raw.startsWith('+++ ') && cur) {
      cur.to = raw.slice(4).replace(/^b\//, '')
    } else if (raw.startsWith('@@ ') && cur) {
      const m = raw.match(/@@ -([\d,]+) \+([\d,]+) @@(.*)/)
      if (m) {
        oldNo = parseInt(m[1].split(',')[0])
        newNo = parseInt(m[2].split(',')[0])
      }
      curHunk = { header: raw, lines: [] }
      cur.hunks.push(curHunk)
    } else if (curHunk) {
      if (raw.startsWith('+')) {
        curHunk.lines.push({ type: 'add', sign: '+', oldNo: '', newNo: newNo++, html: escHtml(raw.slice(1)) })
      } else if (raw.startsWith('-')) {
        curHunk.lines.push({ type: 'del', sign: '-', oldNo: oldNo++, newNo: '', html: escHtml(raw.slice(1)) })
      } else if (raw.startsWith(' ') || raw === '') {
        curHunk.lines.push({ type: 'ctx', sign: '', oldNo: oldNo++, newNo: newNo++, html: escHtml(raw.slice(1)) })
      }
    }
  }
  return files
})
const showGitLogDialog = ref(false)
const gitLogList = ref([])
const gitLogLoading = ref(false)
const gitLogFetchLoading = ref(false)
const commitActionLoading = ref('')  // '<hash>_<action>' 标识哪个提交正在操作
const gitLogBranchFilter = ref('')   // 分支筛选：空 = 所有分支
const gitLogAllBranches = ref([])    // 分支下拉选项

// ===== Git 分支图计算 =====
const GL_LANE_W = 14  // 每条泳道宽度
const GL_ROW_H = 36   // 每行高度
const GL_MID_Y = 18   // commit 点 Y 坐标
const GL_DOT_R = 4    // 普通点半径
const GL_DOT_R_HEAD = 5 // HEAD 点半径
const GL_COLORS = ['#4a9eff', '#c586ff', '#4ec9b0', '#ce9178', '#f44747', '#b5cea8', '#dcdcaa', '#9cdcfe', '#ff79c6', '#e9b96e']

function glLaneX(lane) { return lane * GL_LANE_W + GL_LANE_W / 2 }

const gitGraphRows = computed(() => {
  const commits = gitLogList.value
  if (!commits.length) return []

  const lanes = []      // lanes[i] = hash | null
  const laneColors = [] // laneColors[i] = color

  const rows = []

  for (const commit of commits) {
    const hash = commit.hash
    const parents = (commit.parents || []).filter(Boolean)
    const isHead = commit.refs?.some(r => r.type === 'head')

    // 找到该 commit 所在 lane（由上一行的 parentSlots 预先分配）
    let myLane = lanes.indexOf(hash)
    const isNewLane = myLane === -1
    if (isNewLane) {
      // 新分支起点：找空槽或追加
      myLane = lanes.indexOf(null)
      if (myLane === -1) myLane = lanes.length
    }

    // 确保数组够长
    while (lanes.length <= myLane) { lanes.push(null); laneColors.push(null) }
    if (!laneColors[myLane]) laneColors[myLane] = GL_COLORS[myLane % GL_COLORS.length]

    const myColor = laneColors[myLane]
    const hadIncoming = !isNewLane && lanes[myLane] === hash

    const prevLanes = lanes.slice()
    const prevColors = laneColors.slice()

    // 克隆新状态
    const newLanes = lanes.slice()
    const newColors = laneColors.slice()
    newLanes[myLane] = null
    newColors[myLane] = null

    // 计算父 commit 的 lane 分配
    const parentSlots = []

    if (parents.length > 0) {
      const existingP0 = newLanes.indexOf(parents[0])
      if (existingP0 !== -1) {
        // 第一父已被其他 lane 追踪：myLane 收束进去
        parentSlots.push({ lane: existingP0, color: newColors[existingP0], hash: parents[0] })
      } else {
        // 第一父接续 myLane
        newLanes[myLane] = parents[0]
        newColors[myLane] = myColor
        parentSlots.push({ lane: myLane, color: myColor, hash: parents[0] })
      }
      // 额外父（合并提交）
      for (let p = 1; p < parents.length; p++) {
        const ph = parents[p]
        const existingLane = newLanes.indexOf(ph)
        if (existingLane !== -1) {
          parentSlots.push({ lane: existingLane, color: newColors[existingLane], hash: ph })
        } else {
          let ni = newLanes.indexOf(null)
          if (ni === -1) ni = newLanes.length
          while (newLanes.length <= ni) { newLanes.push(null); newColors.push(null) }
          newColors[ni] = GL_COLORS[ni % GL_COLORS.length]
          newLanes[ni] = ph
          parentSlots.push({ lane: ni, color: newColors[ni], hash: ph })
        }
      }
    }

    // 计算 SVG 宽度
    const activeIdxs = [...prevLanes.map((h, i) => h ? i : -1), ...newLanes.map((h, i) => h ? i : -1), myLane]
    const maxLane = Math.max(...activeIdxs.filter(i => i >= 0))
    const svgW = Math.max((maxLane + 1) * GL_LANE_W, GL_LANE_W)

    // 构造 SVG 线段
    const segments = []

    // 1. 直通泳道（不是 myLane，且 prevLanes 中非空）
    for (let j = 0; j < prevLanes.length; j++) {
      if (prevLanes[j] === null || j === myLane) continue
      const x = glLaneX(j)
      const color = prevColors[j]
      segments.push({ type: 'path', d: `M${x},0 L${x},${GL_ROW_H}`, color, key: `pt-${j}` })
    }

    // 2. myLane 上半段（从上方来的连线）
    if (hadIncoming) {
      const mx = glLaneX(myLane)
      segments.push({ type: 'path', d: `M${mx},0 L${mx},${GL_MID_Y}`, color: myColor, key: 'in' })
    }

    // 3. 到父 commit 的连线（下半段）
    for (const ps of parentSlots) {
      const mx = glLaneX(myLane)
      const px = glLaneX(ps.lane)
      if (ps.lane === myLane) {
        segments.push({ type: 'path', d: `M${mx},${GL_MID_Y} L${mx},${GL_ROW_H}`, color: ps.color, key: `pc-${ps.lane}` })
      } else {
        const cpY = GL_MID_Y + (GL_ROW_H - GL_MID_Y) * 0.6
        const d = `M${mx},${GL_MID_Y} C${mx},${cpY} ${px},${cpY} ${px},${GL_ROW_H}`
        segments.push({ type: 'path', d, color: ps.color, key: `pc-${ps.lane}` })
      }
    }

    // 4. commit 点（最后绘制，覆盖在线上）
    const dotR = isHead ? GL_DOT_R_HEAD : GL_DOT_R
    segments.push({ type: 'circle', cx: glLaneX(myLane), cy: GL_MID_Y, r: dotR, color: myColor, isBold: isHead, key: 'dot' })

    rows.push({ commit, myLane, myColor, svgW, segments })

    // 更新 lanes 状态，裁剪末尾空槽
    lanes.length = 0; newLanes.forEach(l => lanes.push(l))
    laneColors.length = 0; newColors.forEach(c => laneColors.push(c))
    while (lanes.length > 0 && lanes[lanes.length - 1] === null) { lanes.pop(); laneColors.pop() }
  }

  return rows
})

// ===== 计算属性 =====
// 通过后端代理路径访问预览
// 开发环境（/dev-api）：Vite 为 /gamebox/code-manage 路径配置了单独代理规则，直接用相对路径。
// 生产环境（http://host:port）：VITE_APP_BASE_API 是绝对 URL，前端与后端不同源，
//   必须拼上后端 origin，否则相对路径会解析到前端服务器而找不到代理路由。
const previewProxyBase = computed(() => {
  const apiBase = import.meta.env.VITE_APP_BASE_API || ''
  // 优先使用后端 status 接口返回的 previewUrl（已是正确代理路径），
  // 如 siteId 未加载则回退到用 currentSiteId 构造。
  const proxyPath = previewStatus.value.previewUrl
    || `/gamebox/code-manage/${currentSiteId.value}/preview/proxy`
  if (apiBase.startsWith('http://') || apiBase.startsWith('https://')) {
    // 绝对 URL（如 http://local.zeusai.top:8890）：拼接 origin，直接访问后端
    try {
      const { origin } = new URL(apiBase)
      return origin + proxyPath
    } catch {
      // URL 解析失败，降级为相对路径
    }
  }
  // 相对前缀（如 /prod-api）：说明 nginx 同源统一代理，预览路径可直接用相对路径
  // nginx 需要有对应的 /gamebox/code-manage/*/preview/ 代理规则才能正确转发
  return proxyPath
})

const previewIframeUrl = computed(() => {
  if (!previewStatus.value.running || !previewStatus.value.port) return 'about:blank'
  const base = previewProxyBase.value
  const path = previewPath.value || '/'
  return `${base}${path}`
})

// 地址栏同步：仅在 previewIframeUrl 因 previewStatus 变化（启动/切换站点）时更新地址栏
// 已移除，改由 watch(running) 统一处理

// 仅当 running 状态变化时才更新 iframeSrc 和地址栏：
// - 变为 true ：加载预览，地址栏显示初始 URL
// - 变为 false：重置空白
watch(() => previewStatus.value.running, (running) => {
  if (running) {
    iframeSrc.value = previewIframeUrl.value
    previewAddressBar.value = previewIframeUrl.value  // 显示完整代理路径
  } else {
    iframeSrc.value = 'about:blank'
  }
})

const currentLang = computed(() => {
  if (!activeFilePath.value) return ''
  const ext = activeFilePath.value.split('.').pop() || ''
  return getMonacoLanguage(ext)
})

const deployStatusText = computed(() => {
  const s = currentSite.value?.deployStatus
  return { not_deployed: '未部署', deploying: '部署中', deployed: '已部署', failed: '部署失败' }[s] || '未部署'
})
const deployStatusClass = computed(() => ({
  'ds-ok': currentSite.value?.deployStatus === 'deployed',
  'ds-warn': currentSite.value?.deployStatus === 'deploying',
  'ds-err': currentSite.value?.deployStatus === 'failed'
}))

// ===== Monaco 初始化 =====

// 加载项目 node_modules 类型到 Monaco 语言服务
let monacoTypesLoaded = false
async function loadMonacoTypes(siteId) {
  if (!monacoLib || monacoTypesLoaded) return
  try {
    const res = await getNodeTypes(siteId)
    const files = res.data?.files || []
    if (!files.length) return
    const monaco = monacoLib
    for (const f of files) {
      const uri = `file:///node_modules/${f.path}`
      monaco.languages.typescript.typescriptDefaults.addExtraLib(f.content, uri)
      monaco.languages.typescript.javascriptDefaults.addExtraLib(f.content, uri)
    }
    // 类型加载完成后开启语义诊断
    const diag = { noSemanticValidation: false, noSyntaxValidation: false, noSuggestionDiagnostics: false }
    monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions(diag)
    monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions(diag)
    monacoTypesLoaded = true
  } catch (e) {
    // 加载失败不影响编辑器正常使用
    console.warn('[Monaco] 类型加载失败:', e.message)
  }
}

function initMonaco() {
  if (!monacoContainer.value || monacoInstance) return
  try {
    const monaco = monacoLib

    // 初始时关闭语义诊断，加载类型后再开启
    const tsDiag = { noSemanticValidation: true, noSyntaxValidation: false, noSuggestionDiagnostics: true }
    monaco.languages.typescript.typescriptDefaults.setDiagnosticsOptions(tsDiag)
    monaco.languages.typescript.javascriptDefaults.setDiagnosticsOptions(tsDiag)
    // 使用 NodeJs 模块解析，配合后续注入的 node_modules 虚拟路径
    const tsCompiler = {
      allowNonTsExtensions: true,
      allowJs: true,
      checkJs: false,
      moduleResolution: monaco.languages.typescript.ModuleResolutionKind.NodeJs,
      target: monaco.languages.typescript.ScriptTarget.ESNext,
      jsx: monaco.languages.typescript.JsxEmit.Preserve,
      allowSyntheticDefaultImports: true,
      esModuleInterop: true,
    }
    monaco.languages.typescript.typescriptDefaults.setCompilerOptions(tsCompiler)
    monaco.languages.typescript.javascriptDefaults.setCompilerOptions(tsCompiler)

    monacoInstance = monaco.editor.create(monacoContainer.value, {
      value: '',
      language: 'plaintext',
      theme: 'vs-dark',
      fontSize: 14,
      tabSize: 2,
      automaticLayout: true,
      minimap: { enabled: true },
      scrollBeyondLastLine: false,
      wordWrap: 'off',
      renderLineHighlight: 'all',
      smoothScrolling: true,
      cursorBlinking: 'smooth',
      bracketPairColorization: { enabled: true },
      fontFamily: "'JetBrains Mono', 'Fira Code', Consolas, monospace"
    })
    // 监听内容变化，标记 dirty
    monacoInstance.onDidChangeModelContent(() => {
      if (isSettingModel) return  // 切换模型时忽略
      if (!activeFilePath.value || !openedFilesMap[activeFilePath.value]) return
      const cur = openedFilesMap[activeFilePath.value]
      const content = monacoInstance.getValue()
      const dirty = content !== cur.originalContent
      cur.content = content
      if (cur.dirty !== dirty) {
        cur.dirty = dirty
        const tab = openedFiles.value.find(f => f.path === activeFilePath.value)
        if (tab) tab.dirty = dirty
      }
    })
    // Ctrl+S 保存
    monacoInstance.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, saveCurrentFile)
    // 如果有待显示的文件（在Monaco初始化前已打开），现在创建model并显示
    if (activeFilePath.value && openedFilesMap[activeFilePath.value]) {
      const fd = openedFilesMap[activeFilePath.value]
      if (!fd.model) {
        const lang = getMonacoLanguage(fd.ext || activeFilePath.value.split('.').pop())
        const uri = monaco.Uri.parse('inmemory:///' + activeFilePath.value.replace(/\\/g, '/'))
        fd.model = markRaw(monaco.editor.getModel(uri) || monaco.editor.createModel(fd.content, lang, uri))
      }
      isSettingModel = true
      monacoInstance.setModel(fd.model)
      isSettingModel = false
    }
  } catch (err) {
    ElMessage.error('编辑器加载失败: ' + err.message)
  }
}

function getMonacoLanguage(ext) {
  const m = {
    js: 'javascript', jsx: 'javascript', ts: 'typescript', tsx: 'typescript',
    vue: 'html', html: 'html', htm: 'html', css: 'css', scss: 'scss', less: 'less',
    json: 'json', json5: 'json', md: 'markdown', mdx: 'markdown',
    yaml: 'yaml', yml: 'yaml', toml: 'ini', env: 'ini', sh: 'shell', bat: 'bat',
    py: 'python', go: 'go', rs: 'rust', java: 'java', xml: 'xml', svg: 'xml',
    graphql: 'graphql', gql: 'graphql', sql: 'sql', txt: 'plaintext', prisma: 'plaintext'
  }
  return m[ext?.toLowerCase()] || 'plaintext'
}

// ===== 文件树 =====
async function loadTreeNode(node, resolve) {
  if (!currentSiteId.value) return resolve([])
  const path = node.level === 0 ? '' : node.data.path
  try {
    const res = await listFiles(currentSiteId.value, path)
    resolve((res.data?.files || []).map(f => ({ ...f, isLeaf: f.type === 'file' })))
  } catch {
    resolve([])
  }
}

function refreshFileTree() {
  treeKey.value++
}

async function handleTreeNodeClick(data, node) {
  if (data.type === 'directory') {
    node.expanded ? node.collapse() : node.expand()
    return
  }
  // 如果点击的是冲突文件，直接进入冲突解决器
  if (statusConflictFiles.value.includes(data.path)) {
    await openConflictResolver(data.path)
    return
  }
  await openFileInEditor(data.path, data.name, data.ext || '')
}

async function openFileInEditor(path, name, ext) {
  if (openedFilesMap[path]) {
    await switchToFile(path)
    return
  }
  fileLoading.value = true
  fileLoadingName.value = name
  try {
    const res = await readFile(currentSiteId.value, path)
    if (!res.data?.success) {
      ElMessage.error(res.data?.message || '读取文件失败')
      return
    }
    const content = res.data.content || ''
    let model = null
    if (monacoLib) {
      const lang = getMonacoLanguage(ext || path.split('.').pop())
      const uri = monacoLib.Uri.parse('inmemory:///' + path.replace(/\\/g, '/'))
      model = monacoLib.editor.getModel(uri) || monacoLib.editor.createModel(content, lang, uri)
      model = markRaw(model)
    }
    openedFilesMap[path] = { content, originalContent: content, dirty: false, model, ext }
    openedFiles.value.push({ path, name, ext, dirty: false })
    // 先隐藏 spinner，让 Monaco 容器完全可见，再切换 model
    // 否则 position:absolute 的遮罩覆盖编辑器导致 automaticLayout 尺寸计算异常
    fileLoading.value = false
    fileLoadingName.value = ''
    await nextTick()
    await switchToFile(path)
  } catch (e) {
    ElMessage.error('打开文件失败: ' + e.message)
  } finally {
    fileLoading.value = false
    fileLoadingName.value = ''
  }
}

async function switchToFile(path) {
  // 如果当前在 diff 模式，仅隐藏（挂起）而不销毁
  if (isDiffMode.value) hideDiffMode()
  // 如果切换到不同的文件，退出冲突模式
  if (isConflictMode.value && path !== conflictResolverFilePath.value) exitConflictMode()
  activeFilePath.value = path
  if (!monacoInstance) return
  const fd = openedFilesMap[path]
  if (!fd) return
  try {
    if (fd.model) {
      isSettingModel = true
      monacoInstance.setModel(fd.model)
      isSettingModel = false
    } else {
      const lang = getMonacoLanguage(fd.ext || path.split('.').pop())
      isSettingModel = true
      monacoInstance.setValue(fd.content)
      isSettingModel = false
      if (monacoLib) monacoLib.editor.setModelLanguage(monacoInstance.getModel(), lang)
    }
    await nextTick()
    monacoInstance.layout()
    monacoInstance.focus()
  } catch (err) {
    isSettingModel = false
  }
}

async function closeFile(path, force = false) {
  const fd = openedFilesMap[path]
  if (!force && fd?.dirty) {
    try {
      await ElMessageBox.confirm(
        `"${openedFiles.value.find(f => f.path === path)?.name}" 有未保存的更改，是否放弃？`,
        '关闭确认', { confirmButtonText: '放弃', cancelButtonText: '取消', type: 'warning' }
      )
    } catch { return }
  }
  if (fd?.model) fd.model.dispose()
  delete openedFilesMap[path]
  const idx = openedFiles.value.findIndex(f => f.path === path)
  openedFiles.value.splice(idx, 1)
  if (activeFilePath.value === path) {
    const next = openedFiles.value[idx] || openedFiles.value[idx - 1]
    if (next) await switchToFile(next.path)
    else {
      activeFilePath.value = ''
      if (monacoInstance) monacoInstance.setValue('')
    }
  }
}

async function saveCurrentFile() {
  const path = activeFilePath.value
  if (!path || !openedFilesMap[path]?.dirty) return
  saving.value = true
  try {
    const content = monacoInstance ? monacoInstance.getValue() : openedFilesMap[path].content
    const res = await saveFile(currentSiteId.value, path, content)
    if (res.code === 200) {
      openedFilesMap[path].originalContent = content
      openedFilesMap[path].dirty = false
      const tab = openedFiles.value.find(f => f.path === path)
      if (tab) tab.dirty = false
      ElMessage.success({ message: '已保存', duration: 1000, plain: true })
    } else {
      ElMessage.error('保存失败: ' + (res.msg || ''))
    }
  } catch (e) {
    ElMessage.error('保存失败: ' + e.message)
  } finally {
    saving.value = false
  }
}

// ===== 网站初始化 =====
async function loadSiteList() {
  siteListLoading.value = true
  try {
    const res = await listSite({ pageNum: 1, pageSize: 100, status: '1' })
    siteList.value = res.rows || []
  } finally {
    siteListLoading.value = false
  }
}

function getFirstRealSite() {
  return siteList.value.find(site => site.isPersonal !== 1) || null
}

function normalizeCodeManageSiteId(siteId) {
  const numericSiteId = Number(siteId)
  if (!numericSiteId) {
    return getFirstRealSite()?.id || null
  }
  const matchedSite = siteList.value.find(site => site.id === numericSiteId)
  if (!matchedSite || matchedSite.isPersonal === 1) {
    return getFirstRealSite()?.id || null
  }
  return numericSiteId
}

async function handleSiteChange(siteId) {
  if (!siteId) return
  // 关闭已打开文件
  for (const p of Object.keys(openedFilesMap)) {
    if (openedFilesMap[p]?.model) openedFilesMap[p].model.dispose()
    delete openedFilesMap[p]
  }
  openedFiles.value = []
  activeFilePath.value = ''
  if (monacoInstance) monacoInstance.setValue('')
  operationLog.value = ''
  cfVerifyResult.value = null
  lastDeployTime.value = ''
  lastDeployLog.value = ''
  pushSetupStatus.value = null
  pushSetupLog.value = ''
  pushSetupActionsUrl.value = ''
  previewStatus.value = { running: false, port: null }
  showPreview.value = false
  stopPreviewPolling()

  try {
    const res = await getCodeManageInfo(siteId)
    currentSite.value = res.data
    cfgMappings.value = []
    cfgValues.value = {}
    cfgOriginalValues.value = {}
    lastDeployLog.value = res.data?.lastDeployLog || ''
    if (res.data?.lastDeployTime) {
      const d = new Date(res.data.lastDeployTime)
      lastDeployTime.value = `${d.getMonth()+1}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    } else {
      lastDeployTime.value = ''
    }
    if (res.data) {
      let installCommand = 'pnpm install'
      let buildCommand = 'pnpm run build'
      let previewCommand = 'pnpm run dev'
      let deployMode = 'server', actionRepo = '', actionToken = '', actionWorkflow = 'deploy.yml', actionRef = 'main'
      let actionPushToken = '', actionPushBranch = 'main', actionPushWorkflow = '.github/workflows/deploy.yml'
      try {
        const dc = res.data.deployConfig ? JSON.parse(res.data.deployConfig) : null
        if (dc?.installCommand) installCommand = dc.installCommand
        if (dc?.buildCommand) buildCommand = dc.buildCommand
        if (dc?.previewCommand) previewCommand = dc.previewCommand
        if (dc?.deployMode) deployMode = dc.deployMode
        if (dc?.actionRepo) actionRepo = dc.actionRepo
        if (dc?.actionToken) actionToken = dc.actionToken
        if (dc?.actionWorkflow) actionWorkflow = dc.actionWorkflow
        if (dc?.actionRef) actionRef = dc.actionRef
        if (dc?.actionPushToken) actionPushToken = dc.actionPushToken
        if (dc?.actionPushBranch) actionPushBranch = dc.actionPushBranch
        if (dc?.actionPushWorkflow) actionPushWorkflow = dc.actionPushWorkflow
      } catch {}
      const deployCommand = (() => { try { const dc = res.data.deployConfig ? JSON.parse(res.data.deployConfig) : null; return dc?.deployCommand || 'npx wrangler deploy' } catch { return 'npx wrangler deploy' } })()
      deployForm.value = {
        deployPlatform: res.data.deployPlatform || 'local',
        cfAccountId: res.data.cfAccountId || null,
        cloudflareProjectName: res.data.cloudflareProjectName || '',
        installCommand, buildCommand, previewCommand, deployCommand, deployMode,
        actionRepo, actionToken, actionWorkflow, actionRef,
        actionPushToken, actionPushBranch, actionPushWorkflow
      }
      deployFormSaved.value = {
        deployPlatform: deployForm.value.deployPlatform,
        cfAccountId: deployForm.value.cfAccountId,
        cloudflareProjectName: deployForm.value.cloudflareProjectName
      }
    }
    const repoRes = await getRepoInfo(siteId)
    if (repoRes.data?.success) {
      hasRepo.value = true
      // 将后端的 currentBranch 映射到前端统一使用的 branch 字段
      repoInfo.value = {
        ...repoRes.data,
        branch: repoRes.data.currentBranch || repoRes.data.branch || 'main',
        behindCount: null  // null 表示检查中
      }
      // 异步 fetch + 计数，不阻塞主流程
      getRepoSyncStatus(siteId).then(syncRes => {
        if (syncRes.data?.success) {
          repoInfo.value = { ...repoInfo.value, behindCount: syncRes.data.behindCount ?? -1, aheadCount: syncRes.data.aheadCount ?? -1 }
        }
      }).catch(() => {})
    } else {
      hasRepo.value = false
    }
    const pvRes = await getPreviewStatus(siteId)
    if (pvRes.data?.running) {
      previewStatus.value = pvRes.data
      showPreview.value = true
      startPreviewPolling()
    }
    // 若已选 cloudflare-workers 平台，加载域名列表并检查项目状态
    if (deployForm.value.deployPlatform === 'cloudflare-workers') {
      loadWorkerDomains()
      loadCfZones()
      handleCheckCfProject()
    }
    // 若已选 github-actions-push 模式，自动检查配置状态
    if (deployForm.value.deployMode === 'github-actions-push') {
      checkPushSetupStatus()
    }
    // 若已选 github-actions 手动触发模式，自动检查配置状态
    if (deployForm.value.deployMode === 'github-actions') {
      checkDispatchSetupStatus()
    }
  } catch (e) {
    ElMessage.error('加载网站信息失败')
  }
}

/** 校验 Cloudflare 账户 ID 格式（32 位十六进制） */
function isValidCfAccountId(id) {
  return /^[0-9a-fA-F]{32}$/.test(id)
}

async function handleSaveDeploy() {
  const platform = deployForm.value.deployPlatform
  const f = deployForm.value
  savingDeploy.value = true
  try {
    const res = await saveDeployConfig(currentSiteId.value, {
      deployPlatform: platform,
      cfAccountId: f.cfAccountId,
      cloudflareProjectName: f.cloudflareProjectName,
      oldCloudflareProjectName: deployFormSaved.value.cloudflareProjectName || '',
      deployConfig: JSON.stringify({
        installCommand: f.installCommand || 'pnpm install',
        buildCommand: f.buildCommand || 'pnpm run build',
        previewCommand: f.previewCommand || 'pnpm run dev',
        deployCommand: f.deployCommand || 'npx wrangler deploy',
        deployMode: f.deployMode || 'server',
        actionRepo: f.actionRepo || '',
        actionToken: f.actionToken || '',
        actionWorkflow: f.actionWorkflow || 'deploy.yml',
        actionRef: f.actionRef || 'main',
        actionPushToken: f.actionPushToken || '',
        actionPushBranch: f.actionPushBranch || 'main',
        actionPushWorkflow: f.actionPushWorkflow || '.github/workflows/deploy.yml'
      })
    })
    if (res.code === 200) {
      const workerNameChanged = platform === 'cloudflare-workers'
        && f.cloudflareProjectName
        && deployFormSaved.value.cloudflareProjectName
        && f.cloudflareProjectName !== deployFormSaved.value.cloudflareProjectName
      ElMessage.success(workerNameChanged
        ? '部署配置已保存，wrangler.toml 的 Worker 名称已同步更新——提交并推送后生效'
        : '部署配置已保存'
      )
      // 更新已保存快照
      deployFormSaved.value = {
        deployPlatform: f.deployPlatform,
        cfAccountId: f.cfAccountId,
        cloudflareProjectName: f.cloudflareProjectName
      }
      // 名称变更后重新检查项目状态
      if (platform === 'cloudflare-workers') {
        cfProjectExists.value = null
        handleCheckCfProject()
      }
      // Worker 名称变更后刷新 Git 状态，让用户在文件面板看到 wrangler.toml 已被修改
      if (workerNameChanged && hasRepo.value) {
        loadGitStatus()
      }
      return true
    } else {
      ElMessage.error(res.msg || '保存失败')
      return false
    }
  } catch (e) {
    ElMessage.error('保存请求异常：' + (e.message || e))
    return false
  } finally {
    savingDeploy.value = false
  }
}

// 切换平台时清空验证结果
function handleDeployPlatformChange(val) {
  cfVerifyResult.value = null
  cfProjectExists.value = null
  cfProjectUrl.value = ''
  if (val === 'cloudflare-workers' && currentSiteId.value) {
    loadWorkerDomains()
    handleCheckCfProject()
  }
}

// 加载 Cloudflare Zone 列表
async function loadCfZones() {
  if (!currentSiteId.value) return
  cfZonesLoading.value = true
  try {
    const res = await listCfZones(currentSiteId.value)
    cfZones.value = Array.isArray(res?.data) ? res.data : []
  } catch {
    cfZones.value = []
  } finally {
    cfZonesLoading.value = false
  }
}

// 加载 Worker 域名列表
async function loadWorkerDomains() {
  if (!currentSiteId.value) return
  workerDomainLoading.value = true
  workerDomainError.value = ''
  try {
    const res = await listWorkerDomains(currentSiteId.value)
    const data = res?.data
    if (data?.success) {
      workerDomains.value = data.domains || []
    } else {
      workerDomainError.value = data?.message || '获取域名列表失败'
      workerDomains.value = []
    }
  } catch (e) {
    workerDomainError.value = '加载域名列表异常: ' + (e?.message || e)
    workerDomains.value = []
  } finally {
    workerDomainLoading.value = false
  }
}

// 验证 Cloudflare API Token 与账户 ID
async function handleVerifyCf() {
  if (!currentSiteId.value) {
    ElMessage.warning('请先选择网站')
    return
  }
  if (!deployForm.value.cfAccountId) {
    ElMessage.warning('请先在部署配置中选择 CF 账号并保存')
    return
  }
  cfVerifying.value = true
  cfVerifyResult.value = null
  try {
    const res = await verifyCfCredentials(currentSiteId.value)
    const data = res.data || {}
    cfVerifyResult.value = {
      success: !!data.success,
      message: data.message || (data.success ? '验证通过' : '验证失败'),
      accountName: data.accountName || ''
    }
    if (data.success) {
      ElMessage.success(data.message || '验证通过')
    } else {
      ElMessage.error(data.message || '验证失败')
    }
  } catch (e) {
    cfVerifyResult.value = { success: false, message: '请求异常: ' + e.message }
    ElMessage.error('验证请求失败: ' + e.message)
  } finally {
    cfVerifying.value = false
  }
}

// 在 Cloudflare 中直接创建 Workers 项目（无需去 CF 控制台）
async function handleCreateCfProject() {
  if (!currentSiteId.value) return
  if (!deployForm.value.cfAccountId) {
    ElMessage.warning('请先选择 CF 账号并保存')
    return
  }
  if (!deployForm.value.cloudflareProjectName) {
    ElMessage.warning('请先填写 Worker 名称')
    return
  }
  creatingProject.value = true
  try {
    const res = await createCfProject(currentSiteId.value)
    const data = res.data || {}
    if (data.success) {
      cfProjectExists.value = true
      cfProjectUrl.value = data.workerUrl || ''
      ElMessage.success(data.message || 'Worker 项目创建成功')
    } else {
      ElMessage.error(data.message || '项目创建失败')
    }
  } catch (e) {
    ElMessage.error('创建失败: ' + e.message)
  } finally {
    creatingProject.value = false
  }
}

// 检查 Cloudflare Workers 项目是否已存在
async function handleCheckCfProject() {
  if (!currentSiteId.value) return
  checkingProject.value = true
  try {
    const res = await checkCfProject(currentSiteId.value)
    const data = res.data || {}
    if (data.success) {
      cfProjectExists.value = !!data.exists
      cfProjectUrl.value = data.workerUrl || ''
    }
  } catch {
    // 静默失败
  } finally {
    checkingProject.value = false
  }
}

// 删除 Cloudflare Workers 项目
async function handleDeleteCfProject() {
  if (!currentSiteId.value) return
  const projectName = deployForm.value.cloudflareProjectName || 'Worker 项目'
  try {
    await ElMessageBox.confirm(
      `确定删除 Cloudflare Workers 项目「${projectName}」？此操作不可恢复，删除后可重新创建不同名称的项目。`,
      '删除确认',
      { type: 'warning', confirmButtonText: '确认删除', confirmButtonClass: 'el-button--danger', cancelButtonText: '取消' }
    )
  } catch { return }
  deletingProject.value = true
  try {
    const res = await deleteCfProject(currentSiteId.value)
    const data = res.data || {}
    if (data.success) {
      cfProjectExists.value = false
      cfProjectUrl.value = ''
      ElMessage.success(data.message || 'Worker 项目已删除')
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败: ' + e.message)
  } finally {
    deletingProject.value = false
  }
}

// 部署到 Cloudflare Workers
async function handleDeployWorkers() {
  if (!currentSiteId.value) return
  if (!deployForm.value.cfAccountId) {
    ElMessage.warning('请先在部署配置中选择 CF 账号并保存')
    showGitDialog.value = true
    return
  }
  if (!deployForm.value.cloudflareProjectName) {
    ElMessage.warning('请先在部署配置中填写 Worker 名称')
    showGitDialog.value = true
    return
  }
  deploying.value = true
  isViewingLastLog.value = false
  operationLog.value = '> 正在部署到 Cloudflare Workers...\n'
  logDialogTitle.value = 'CF Workers 部署日志'
  showLogDialog.value = true
  try {
    const res = await deployToWorkers(currentSiteId.value)
    const log = res.data?.log || ''
    const ok = res.data?.success === true
    operationLog.value += log + (log ? '\n' : '') + (ok ? '✓ 部署成功!' : '✗ ' + (res.data?.message || '部署失败')) + '\n'
    if (ok && res.data?.deployUrl) {
      operationLog.value += '访问地址: ' + res.data.deployUrl + '\n'
    }
    lastDeployLog.value = operationLog.value
    const now = new Date()
    lastDeployTime.value = `${now.getMonth()+1}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
    if (ok) ElMessage.success('部署成功')
    else ElMessage.error('部署失败，请查看日志')
  } catch (e) {
    operationLog.value += '\n错误: ' + e.message
    ElMessage.error('部署异常: ' + e.message)
  } finally {
    deploying.value = false
    scrollLog()
  }
}

// 查询 GitHub Actions Push 配置状态
async function checkPushSetupStatus() {
  if (!currentSiteId.value) return
  pushSetupStatusLoading.value = true
  try {
    const res = await checkGithubActionsPushStatus(currentSiteId.value)
    const data = res.data || {}
    if (data.success !== false) {
      pushSetupStatus.value = {
        workflowExists: !!data.workflowExists,
        workflowContentMatch: !!data.workflowContentMatch,
        tokenValid: data.tokenValid === null ? null : !!data.tokenValid,
        secretApiToken: data.secretApiToken === null ? null : !!data.secretApiToken,
        secretAccountId: data.secretAccountId === null ? null : !!data.secretAccountId,

      }
    }
  } catch (e) { /* 静默失败 */ }
  finally { pushSetupStatusLoading.value = false }
}

// GitHub Actions Push 模式：一键配置工作流 + Secrets
async function handleSetupGithubActionsPush() {
  if (!currentSiteId.value) return
  // 执行前先自动保存，确保后端读取最新配置
  const saveOk = await handleSaveDeploy()
  if (!saveOk) return
  pushSetupLoading.value = true
  pushSetupLog.value = ''
  pushSetupActionsUrl.value = ''
  try {
    const res = await setupGithubActionsPush(currentSiteId.value)
    const data = res.data || {}
    pushSetupLog.value = data.log || (data.success ? '配置成功' : (data.message || '未知错误'))
    pushSetupActionsUrl.value = data.actionsUrl || ''
    if (data.success) {
      ElMessage.success('GitHub Actions 工作流配置完成')
      checkPushSetupStatus() // 刷新状态标签
    } else {
      ElMessage.error(data.message || '配置失败，请查看日志')
    }
  } catch (e) {
    pushSetupLog.value = '请求异常: ' + (e.message || e)
    ElMessage.error('请求异常')
  } finally {
    pushSetupLoading.value = false
  }
}

// GitHub Actions 手动触发（dispatch）模式：检查配置状态
async function checkDispatchSetupStatus() {
  if (!currentSiteId.value || !deployForm.value.actionToken) return
  dispatchSetupStatusLoading.value = true
  try {
    const res = await checkGithubActionsDispatchStatus(currentSiteId.value)
    const data = res.data || {}
    if (data.success !== false) {
      dispatchSetupStatus.value = {
        workflowExists: !!data.workflowExists,
        workflowContentMatch: !!data.workflowContentMatch,
        tokenValid: data.tokenValid === null ? null : !!data.tokenValid,
        secretApiToken: data.secretApiToken === null ? null : !!data.secretApiToken,
        secretAccountId: data.secretAccountId === null ? null : !!data.secretAccountId,
        secretGitAccessToken: data.secretGitAccessToken === null ? null : !!data.secretGitAccessToken
      }
    }
  } catch (e) { /* 静默失败 */ }
  finally { dispatchSetupStatusLoading.value = false }
}

// GitHub Actions 手动触发（dispatch）模式：一键注入 workflow 文件到远端 actionRepo
async function handleSetupGithubActionsDispatch() {
  if (!currentSiteId.value) return
  if (!deployForm.value.actionRepo || !deployForm.value.actionRepo.includes('/')) {
    ElMessage.warning('请先填写 Action 仓库（格式: owner/repo）并保存配置')
    return
  }
  if (!deployForm.value.actionToken) {
    ElMessage.warning('请先填写 GitHub Token 并保存配置')
    return
  }
  const saveOk = await handleSaveDeploy()
  if (!saveOk) return
  dispatchSetupLoading.value = true
  dispatchSetupLog.value = ''
  try {
    const res = await setupGithubActionsDispatch(currentSiteId.value)
    const data = res.data || {}
    dispatchSetupLog.value = data.log || (data.success ? '配置成功' : (data.message || '未知错误'))
    if (data.success) {
      ElMessage.success('工作流文件已推送到远端仓库，现在可以点击触发了')
      checkDispatchSetupStatus()
    } else {
      ElMessage.error(data.message || '配置失败，请查看日志')
    }
  } catch (e) {
    dispatchSetupLog.value = '请求异常: ' + (e.message || e)
    ElMessage.error('请求异常: ' + e.message)
  } finally {
    dispatchSetupLoading.value = false
  }
}

// GitHub Actions 部署（开放仓库手动触发）
async function handleDeployGithubAction() {
  if (!currentSiteId.value) return
  const f = deployForm.value
  if (!f.actionRepo) {
    ElMessage.warning('请先填写 Action 仓库地址（如 https://github.com/owner/repo.git）并保存配置')
    return
  }
  if (!f.actionToken) {
    ElMessage.warning('请先填写 GitHub Token 并保存配置')
    return
  }
  if (!currentSite.value?.gitRepoUrl) {
    ElMessage.warning('中继模式需要先在「站点管理」中配置 Git 仓库地址')
    return
  }

  gaTriggering.value = true
  gaPollStopped = true
  if (gaPollTimer) { clearTimeout(gaPollTimer); gaPollTimer = null }
  gaRunUrl.value = ''
  isViewingLastLog.value = false
  operationLog.value = '> 正在触发 GitHub Actions 工作流...\n'
  logDialogTitle.value = 'GitHub Actions 部署日志'
  showLogDialog.value = true

  try {
    const res = await triggerGithubAction(currentSiteId.value)
    if (!res.data?.success) {
      operationLog.value += (res.data?.message || '触发失败') + '\n'
      gaTriggering.value = false
      ElMessage.error(res.data?.message || '触发失败')
      return
    }
  } catch (e) {
    operationLog.value += '\n错误: ' + e.message
    gaTriggering.value = false
    ElMessage.error('触发失败: ' + e.message)
    return
  }

  // 开始轮询 GitHub Actions 状态
  let lastLogLen = 0
  const siteId = currentSiteId.value
  gaPollStopped = false

  const pollOnce = async () => {
    if (gaPollStopped) return
    try {
      const statusRes = await getGithubActionStatus(siteId)
      const data = statusRes.data || {}
      const fullLog = data.log || ''

      // 追加新增部分（在修改 DOM 前先快照位置）
      if (fullLog.length > lastLogLen) {
        const el = logConsoleRef.value
        const wasAtBottom = el ? (el.scrollHeight - el.scrollTop - el.clientHeight) <= 30 : true
        operationLog.value += fullLog.substring(lastLogLen)
        lastLogLen = fullLog.length
        if (wasAtBottom) nextTick(() => { if (logConsoleRef.value) logConsoleRef.value.scrollTop = logConsoleRef.value.scrollHeight })
      }

      // 更新运行链接
      if (data.runId && !gaRunUrl.value) {
        gaRunUrl.value = 'https://github.com/' + f.actionRepo + '/actions/runs/' + data.runId
      }

      // 构建结束
      if (data.done) {
        gaPollStopped = true
        gaTriggering.value = false
        lastDeployLog.value = operationLog.value
        const now = new Date()
        lastDeployTime.value = `${now.getMonth()+1}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
        if (data.success) ElMessage.success('GitHub Actions 部署成功')
        else ElMessage.error('GitHub Actions 部署失败，请查看日志')
        return
      }
    } catch (e) {
      console.warn('轮询 GA 状态失败:', e)
    }
    if (!gaPollStopped) gaPollTimer = setTimeout(pollOnce, 3000)
  }

  gaPollTimer = setTimeout(pollOnce, 2000)
}

// 绑定域名到 Worker
async function handleBindDomain() {
  if (!newDomainZone.value) {
    ElMessage.warning('请选择根域名（Zone）')
    return
  }
  const prefix = newDomainHostname.value.trim()
  // 留空或 @ 表示根域名本身
  const fullHostname = (!prefix || prefix === '@')
    ? newDomainZone.value
    : prefix + '.' + newDomainZone.value
  bindingDomain.value = true
  bindDomainError.value = ''
  try {
    const res = await bindWorkerDomain(currentSiteId.value, fullHostname, newDomainZone.value)
    const data = res?.data
    if (data?.success) {
      ElMessage.success(data.message || '域名绑定成功')
      newDomainHostname.value = ''
      newDomainZone.value = ''
      await loadWorkerDomains()
    } else {
      bindDomainError.value = data?.message || '绑定失败'
    }
  } catch (e) {
    bindDomainError.value = '绑定域名失败: ' + (e?.message || e)
  } finally {
    bindingDomain.value = false
  }
}

// 解绑域名
async function handleUnbindDomain(domainId) {
  try {
    const res = await unbindWorkerDomain(currentSiteId.value, domainId)
    const data = res?.data
    if (data?.success) {
      ElMessage.success('域名已解绑')
      await loadWorkerDomains()
    } else {
      ElMessage.error(data?.message || '解绑失败')
    }
  } catch (e) {
    ElMessage.error('解绑失败: ' + (e?.message || e))
  }
}

async function handlePull() {
  if (!currentSiteId.value) return
  pulling.value = true
  isViewingLastLog.value = false
  operationLog.value = '> git pull...\n'
  logDialogTitle.value = 'Git 拉取日志'
  showLogDialog.value = true

  // 关闭可能残留的旧连接
  if (pullWs) { pullWs.close(); pullWs = null }

  // 发起异步拉取（后端立即返回，拉取在后台运行）
  try {
    await pullCode(currentSiteId.value)
  } catch (e) {
    pulling.value = false
    operationLog.value += '\n错误: ' + e.message
    ElMessage.error('启动拉取失败')
    return
  }

  const siteId = currentSiteId.value
  const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  pullWs = new WebSocket(`${wsProtocol}//${window.location.host}/ws/op-log/${siteId}/pull`)
  pullWs.onmessage = async (event) => {
    try {
      const msg = JSON.parse(event.data)
      if (msg.type === 'log') {
        const el = logConsoleRef.value
        const wasAtBottom = el ? (el.scrollHeight - el.scrollTop - el.clientHeight) <= 30 : true
        operationLog.value += msg.data
        if (wasAtBottom) nextTick(() => { if (logConsoleRef.value) logConsoleRef.value.scrollTop = logConsoleRef.value.scrollHeight })
      } else if (msg.type === 'done') {
        pullWs = null
        pulling.value = false
        lastDeployLog.value = operationLog.value
        const now = new Date()
        lastDeployTime.value = `${now.getMonth()+1}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
        if (msg.success) {
          hasRepo.value = true
          refreshFileTree()
          const rr = await getRepoInfo(currentSiteId.value)
          if (rr.data?.success) {
            repoInfo.value = {
              ...rr.data,
              branch: rr.data.currentBranch || rr.data.branch || 'main'
            }
          }
          await loadGitStatus()
          if (showBranchDialog.value) await loadBranches()
          ElMessage.success('拉取成功')
        } else {
          ElMessage.error('拉取失败，请查看日志')
        }
      }
    } catch {}
  }
  pullWs.onerror = () => {
    operationLog.value += '\n[WebSocket 连接异常]\n'
    pulling.value = false
    pullWs = null
  }
}

// 运维操作的 WebSocket 实例（pull / build / install / push 各一个，提前关闭旧连接用）
let pullWs = null
let buildWs = null
let installWs = null
let pushWs = null

async function handleBuild() {
  if (!currentSiteId.value) return
  building.value = true
  isViewingLastLog.value = false
  operationLog.value = '> ' + (deployForm.value.buildCommand || 'pnpm run build') + '...\n'
  logDialogTitle.value = '构建日志'
  showLogDialog.value = true

  // 关闭可能残留的旧连接
  if (buildWs) { buildWs.close(); buildWs = null }

  // 发起异步构建（后端立即返回，构建在后台运行）
  try {
    await buildProject(currentSiteId.value)
  } catch (e) {
    building.value = false
    operationLog.value += '\n错误: ' + e.message
    ElMessage.error('启动构建失败')
    return
  }

  const siteId = currentSiteId.value
  const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  buildWs = new WebSocket(`${wsProtocol}//${window.location.host}/ws/op-log/${siteId}/build`)
  buildWs.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      if (msg.type === 'log') {
        const el = logConsoleRef.value
        const wasAtBottom = el ? (el.scrollHeight - el.scrollTop - el.clientHeight) <= 30 : true
        operationLog.value += msg.data
        if (wasAtBottom) nextTick(() => { if (logConsoleRef.value) logConsoleRef.value.scrollTop = logConsoleRef.value.scrollHeight })
      } else if (msg.type === 'done') {
        buildWs = null
        building.value = false
        lastDeployLog.value = operationLog.value
        const now = new Date()
        lastDeployTime.value = `${now.getMonth()+1}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
        if (msg.success) {
          ElMessage.success('构建成功')
        } else {
          ElMessage.error('构建失败，请查看日志')
        }
      }
    } catch {}
  }
  buildWs.onerror = () => {
    operationLog.value += '\n[WebSocket 连接异常]\n'
    building.value = false
    buildWs = null
  }
}

async function handleInstall() {
  if (!currentSiteId.value) return
  installing.value = true
  isViewingLastLog.value = false
  operationLog.value = '> ' + (deployForm.value.installCommand || 'pnpm install') + '...\n'
  logDialogTitle.value = '安装依赖'
  showLogDialog.value = true

  // 关闭可能残留的旧连接
  if (installWs) { installWs.close(); installWs = null }

  // 发起异步安装（后端立即返回，安装在后台运行）
  try {
    await installDependencies(currentSiteId.value)
  } catch (e) {
    installing.value = false
    operationLog.value += '\n错误: ' + e.message
    ElMessage.error('启动安装失败')
    return
  }

  const siteId = currentSiteId.value
  const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  installWs = new WebSocket(`${wsProtocol}//${window.location.host}/ws/op-log/${siteId}/install`)
  installWs.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      if (msg.type === 'log') {
        const el = logConsoleRef.value
        const wasAtBottom = el ? (el.scrollHeight - el.scrollTop - el.clientHeight) <= 30 : true
        operationLog.value += msg.data
        if (wasAtBottom) nextTick(() => { if (logConsoleRef.value) logConsoleRef.value.scrollTop = logConsoleRef.value.scrollHeight })
      } else if (msg.type === 'done') {
        installWs = null
        installing.value = false
        lastDeployLog.value = operationLog.value
        const now = new Date()
        lastDeployTime.value = `${now.getMonth()+1}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
        if (msg.success) {
          ElMessage.success('依赖安装完成')
        } else {
          ElMessage.error('安装失败，请查看日志')
        }
      }
    } catch {}
  }
  installWs.onerror = () => {
    operationLog.value += '\n[WebSocket 连接异常]\n'
    installing.value = false
    installWs = null
  }

}

async function handlePush() {
  if (!currentSiteId.value) return
  showPushDialog.value = false
  pushing.value = true
  isViewingLastLog.value = false
  operationLog.value = '> git add . && git commit && git push...\n'
  logDialogTitle.value = 'Git 推送日志'
  showLogDialog.value = true

  // 关闭可能残留的旧连接
  if (pushWs) { pushWs.close(); pushWs = null }

  // 发起异步推送（后端立即返回，推送在后台运行）
  try {
    await pushCode(currentSiteId.value, pushCommitMessage.value || '')
  } catch (e) {
    pushing.value = false
    operationLog.value += '\n错误: ' + e.message
    ElMessage.error('启动推送失败')
    return
  }

  const siteId = currentSiteId.value
  const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  pushWs = new WebSocket(`${wsProtocol}//${window.location.host}/ws/op-log/${siteId}/push`)
  pushWs.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      if (msg.type === 'log') {
        const el = logConsoleRef.value
        const wasAtBottom = el ? (el.scrollHeight - el.scrollTop - el.clientHeight) <= 30 : true
        operationLog.value += msg.data
        if (wasAtBottom) nextTick(() => { if (logConsoleRef.value) logConsoleRef.value.scrollTop = logConsoleRef.value.scrollHeight })
      } else if (msg.type === 'done') {
        pushWs = null
        pushing.value = false
        lastDeployLog.value = operationLog.value
        const now = new Date()
        lastDeployTime.value = `${now.getMonth()+1}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
        if (msg.success) {
          ElMessage.success('代码已成功推送到远程仓库')
          pushCommitMessage.value = ''
        } else {
          ElMessage.error('推送失败，请查看日志')
        }
      }
    } catch {}
  }
  pushWs.onerror = () => {
    operationLog.value += '\n[WebSocket 连接异常]\n'
    pushing.value = false
    pushWs = null
  }
}

/**
 * 滚动日志到底部
 * @param {boolean} force true=始终滚动（打开对话框/操作结束时）
 *                        false=智能模式：仅当用户已在底部附近（100px以内）时才滚动，
 *                              否则保持用户当前阅读位置不变
 */
function scrollLog(force = true) {
  nextTick(() => {
    const el = logConsoleRef.value
    if (!el) return
    if (force) {
      el.scrollTop = el.scrollHeight
    } else {
      // 距离底部 30px 以内才跟随，否则用户正在往上看，不要打断
      const distToBottom = el.scrollHeight - el.scrollTop - el.clientHeight
      if (distToBottom <= 30) {
        el.scrollTop = el.scrollHeight
      }
    }
  })
}

// ===== 右键菜单 =====
function handleTreeNodeContextmenu(event, data, node) {
  event.preventDefault()
  event.stopPropagation()
  ctxMenu.x = event.clientX
  ctxMenu.y = event.clientY
  ctxMenu.node = node
  ctxMenu.data = data
  ctxMenu.visible = true
}
function handleSidebarContextmenu(event) {
  if (!hasRepo.value) return
  ctxMenu.x = event.clientX
  ctxMenu.y = event.clientY
  ctxMenu.node = null
  ctxMenu.data = null
  ctxMenu.visible = true
}
function closeCtxMenu() {
  ctxMenu.visible = false
}

// 新建文件/文件夹
function ctxNewFile(isDirectory) {
  const data = ctxMenu.data
  const parentPath = data ? (data.type === 'directory' ? data.path : getParentPath(data.path)) : ''
  newFileForm.name = ''
  newFileForm.isDirectory = isDirectory
  newFileForm.parentPath = parentPath
  showNewFileDialog.value = true
  ctxMenu.visible = false
}
function getParentPath(p) {
  const idx = p.lastIndexOf('/')
  return idx > 0 ? p.substring(0, idx) : ''
}
async function doCreateFile() {
  if (!newFileForm.name.trim()) { ElMessage.warning('请输入名称'); return }
  const fullPath = newFileForm.parentPath
    ? newFileForm.parentPath + '/' + newFileForm.name.trim()
    : newFileForm.name.trim()
  try {
    const res = await createFile(currentSiteId.value, fullPath, newFileForm.isDirectory)
    if (res.code === 200) {
      ElMessage.success(newFileForm.isDirectory ? '目录创建成功' : '文件创建成功')
      showNewFileDialog.value = false
      refreshFileTree()
      if (!newFileForm.isDirectory) {
        // 打开新文件
        const name = newFileForm.name.trim()
        const ext = name.includes('.') ? name.split('.').pop() : ''
        await openFileInEditor(fullPath, name, ext)
      }
    } else {
      ElMessage.error(res.msg || '创建失败')
    }
  } catch (e) {
    ElMessage.error('创建失败: ' + e.message)
  }
}

// 重命名
function ctxRename() {
  const data = ctxMenu.data
  if (!data) return
  renameForm.oldPath = data.path
  renameForm.oldName = data.name
  renameForm.newName = data.name
  showRenameDialog.value = true
  ctxMenu.visible = false
}
async function doRename() {
  if (!renameForm.newName.trim()) { ElMessage.warning('请输入新名称'); return }
  const parentPath = getParentPath(renameForm.oldPath)
  const newPath = parentPath ? parentPath + '/' + renameForm.newName.trim() : renameForm.newName.trim()
  try {
    const res = await renameFile(currentSiteId.value, renameForm.oldPath, newPath)
    if (res.code === 200) {
      ElMessage.success('重命名成功')
      showRenameDialog.value = false
      // 如果重命名的文件已打开，关闭再重新打开
      if (openedFilesMap[renameForm.oldPath]) {
        closeFile(renameForm.oldPath)
      }
      refreshFileTree()
    } else {
      ElMessage.error(res.msg || '重命名失败')
    }
  } catch (e) {
    ElMessage.error('重命名失败: ' + e.message)
  }
}

// 删除
async function ctxDelete() {
  const data = ctxMenu.data
  if (!data) return
  ctxMenu.visible = false
  try {
    await ElMessageBox.confirm(
      `确定要删除 "${data.name}"${data.type === 'directory' ? ' 及其所有子文件' : ''}？此操作不可撤销。`,
      '删除确认', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning', confirmButtonClass: 'el-button--danger' }
    )
    const res = await deleteFile(currentSiteId.value, data.path)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      if (openedFilesMap[data.path]) closeFile(data.path)
      refreshFileTree()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败: ' + e.message)
  }
}

// 复制
function ctxCopy() {
  const data = ctxMenu.data
  if (!data) return
  copyForm.sourcePath = data.path
  copyForm.sourceName = data.name
  const parentPath = getParentPath(data.path)
  const nameNoExt = data.name.includes('.') ? data.name.substring(0, data.name.lastIndexOf('.')) : data.name
  const ext = data.name.includes('.') ? data.name.substring(data.name.lastIndexOf('.')) : ''
  copyForm.destPath = parentPath ? parentPath + '/' + nameNoExt + '_copy' + ext : nameNoExt + '_copy' + ext
  showCopyDialog.value = true
  ctxMenu.visible = false
}
async function doCopy() {
  if (!copyForm.destPath.trim()) { ElMessage.warning('请输入目标路径'); return }
  try {
    const res = await copyFile(currentSiteId.value, copyForm.sourcePath, copyForm.destPath.trim())
    if (res.code === 200) {
      ElMessage.success('复制成功')
      showCopyDialog.value = false
      refreshFileTree()
    } else {
      ElMessage.error(res.msg || '复制失败')
    }
  } catch (e) {
    ElMessage.error('复制失败: ' + e.message)
  }
}

// 文件上传
function ctxUpload() {
  const data = ctxMenu.data
  uploadDirPath.value = data ? (data.type === 'directory' ? data.path : getParentPath(data.path)) : ''
  ctxMenu.visible = false
  pendingUploadFiles.value = []
  uploadProgressList.value = []
  uploadDragActive.value = false
  showUploadDialog.value = true
}
function triggerUpload() {
  uploadDirPath.value = ''
  pendingUploadFiles.value = []
  uploadProgressList.value = []
  uploadDragActive.value = false
  showUploadDialog.value = true
}
function handleFileInputChange(e) {
  Array.from(e.target.files || []).forEach(f => {
    pendingUploadFiles.value.push({ uid: Date.now() + Math.random(), name: f.name, raw: f, relativePath: '' })
  })
  e.target.value = ''
}
function handleFolderInputChange(e) {
  Array.from(e.target.files || []).forEach(f => {
    pendingUploadFiles.value.push({
      uid: Date.now() + Math.random(),
      name: f.name,
      raw: f,
      relativePath: f.webkitRelativePath || f.name
    })
  })
  e.target.value = ''
}
// 递归读取 FileSystemEntry
function readEntryRecursive(entry, basePath) {
  return new Promise(resolve => {
    if (entry.isFile) {
      entry.file(file => {
        const rel = basePath ? basePath + '/' + file.name : file.name
        resolve([{ uid: Date.now() + Math.random(), name: file.name, raw: file, relativePath: rel }])
      }, () => resolve([]))
    } else if (entry.isDirectory) {
      const reader = entry.createReader()
      const allEntries = []
      function readBatch() {
        reader.readEntries(async entries => {
          if (!entries.length) {
            const dirBase = basePath ? basePath + '/' + entry.name : entry.name
            const nested = await Promise.all(allEntries.map(e2 => readEntryRecursive(e2, dirBase)))
            resolve(nested.flat())
            return
          }
          allEntries.push(...entries)
          readBatch()
        }, () => resolve([]))
      }
      readBatch()
    } else {
      resolve([])
    }
  })
}
async function handleDropUpload(e) {
  uploadDragActive.value = false
  if (uploading.value || uploadReading.value) return
  const items = Array.from(e.dataTransfer.items || [])
  if (!items.length) return
  uploadReading.value = true
  try {
    const entries = items.map(i => i.webkitGetAsEntry ? i.webkitGetAsEntry() : null).filter(Boolean)
    const results = await Promise.all(entries.map(entry => readEntryRecursive(entry, '')))
    results.flat().forEach(item => pendingUploadFiles.value.push(item))
  } finally {
    uploadReading.value = false
  }
}
function removeUploadFile(index) {
  pendingUploadFiles.value.splice(index, 1)
}
function formatBytes(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}
function cancelUpload() {
  if (uploading.value) return
  showUploadDialog.value = false
  pendingUploadFiles.value = []
  uploadProgressList.value = []
}
async function doUploadFiles() {
  const files = pendingUploadFiles.value
  if (!files.length) return
  uploading.value = true
  uploadProgressList.value = files.map(f => ({ name: f.relativePath || f.name, status: 'pending' }))
  let successCount = 0
  let failCount = 0
  try {
    for (let i = 0; i < files.length; i++) {
      uploadProgressList.value[i].status = 'uploading'
      const file = files[i]
      // 计算实际目标目录：将相对路径中的目录部分与 uploadDirPath 拼接
      let finalDirPath = uploadDirPath.value || ''
      if (file.relativePath) {
        const parts = file.relativePath.replace(/\\/g, '/').split('/')
        if (parts.length > 1) {
          const subDir = parts.slice(0, -1).join('/')
          finalDirPath = finalDirPath ? finalDirPath + '/' + subDir : subDir
        }
      }
      try {
        const res = await uploadFile(currentSiteId.value, finalDirPath, file.raw)
        if (res.code === 200) {
          uploadProgressList.value[i].status = 'success'
          successCount++
        } else {
          uploadProgressList.value[i].status = 'fail'
          failCount++
        }
      } catch {
        uploadProgressList.value[i].status = 'fail'
        failCount++
      }
    }
    if (successCount > 0) {
      ElMessage.success(`成功上传 ${successCount} 个文件${failCount > 0 ? `，${failCount} 个失败` : ''}`)
      refreshFileTree()
      if (failCount === 0) {
        showUploadDialog.value = false
        pendingUploadFiles.value = []
        uploadProgressList.value = []
      }
    } else {
      ElMessage.error('所有文件上传失败')
    }
  } finally {
    uploading.value = false
  }
}

// ===== Git 状态面板 =====
function statusChar(t) {
  return { modified:'M', added:'A', deleted:'D', untracked:'U', renamed:'R', conflict:'!', copied:'C' }[t] || '?'
}

async function loadGitStatus() {
  if (!currentSiteId.value || !hasRepo.value) return
  gitStatusLoading.value = true
  try {
    const res = await getGitStatus(currentSiteId.value)
    gitStagedList.value   = res.data?.staged   || []
    gitUnstagedList.value = res.data?.unstaged || []
    // 检测合并中状态
    const wasMerging = isMerging.value
    isMerging.value = !!res.data?.merging
    statusConflictFiles.value = res.data?.conflictFiles || []
    // 仅在「首次」检测到合并状态时自动弹对话框（含页面加载时已处于 MERGE 状态）
    // !wasMerging 保证：false→true 时触发，true→true 时不重复弹
    if (isMerging.value && !wasMerging && !showMergeConflictDialog.value) {
      mergeConflictFiles.value = statusConflictFiles.value
      showMergeConflictDialog.value = true
    }
  } catch {
    gitStagedList.value = []
    gitUnstagedList.value = []
  } finally {
    gitStatusLoading.value = false
  }
}

async function doStage(path) {
  try {
    await gitStage(currentSiteId.value, path)
    await loadGitStatus()
  } catch (e) { ElMessage.error('暂存失败: ' + e.message) }
}
async function doUnstage(path) {
  try {
    await gitUnstage(currentSiteId.value, path)
    await loadGitStatus()
  } catch (e) { ElMessage.error('取消暂存失败: ' + e.message) }
}
async function doStageAll() {
  try {
    await gitStage(currentSiteId.value, '')
    await loadGitStatus()
  } catch (e) { ElMessage.error('暂存失败: ' + e.message) }
}
async function doUnstageAll() {
  try {
    await gitUnstage(currentSiteId.value, '')
    await loadGitStatus()
  } catch (e) { ElMessage.error('取消暂存失败: ' + e.message) }
}
async function doGitCommit() {
  if (!commitMessage.value.trim()) {
    ElMessage.warning('请输入提交消息')
    return
  }
  if (gitStagedList.value.length === 0) {
    // 合并状态下：如果还有冲突文件未解决，提示用户先解决
    if (isMerging.value && statusConflictFiles.value.length > 0) {
      ElMessage.warning('还有 ' + statusConflictFiles.value.length + ' 个冲突文件未解决，请先解决冲突再暂存提交')
      showMergeConflictDialog.value = true
      return
    }
    // 合并状态且没有冲突（段已解决但尚未暂存）：直接允许提交合并
    if (!isMerging.value) {
      ElMessage.warning('暂存区为空，请先暂存文件')
      return
    }
  }
  gitCommitting.value = true
  try {
    const res = await gitCommit(currentSiteId.value, commitMessage.value)
    if (res.data?.hasConflict) {
      ElMessage.warning(res.data.message || '仍有冲突未解决')
      await loadGitStatus()
      if (statusConflictFiles.value.length > 0) {
        mergeConflictFiles.value = statusConflictFiles.value
        showMergeConflictDialog.value = true
      }
      return
    }
    if (res.data?.success === false) throw new Error(res.data?.message)
    if (res.data?.nothingToCommit) {
      ElMessage.info('暂存区为空，无需提交')
    } else {
      ElMessage.success(res.data?.message || '提交成功')
      commitMessage.value = ''
      pendingPushCount.value++  // 提交成功，待推送计数 +1
      if (isMerging.value) {
        // 合并提交成功后清空合并状态
        isMerging.value = false
        statusConflictFiles.value = []
        mergeConflictFiles.value = []
        mergeConflictSourceBranch.value = ''
        showMergeConflictDialog.value = false
      }
    }
    await loadGitStatus()
  } catch (e) { ElMessage.error('提交失败: ' + e.message) }
  finally { gitCommitting.value = false }
}
async function doGitPush() {
  gitPushing.value = true
  try {
    let res
    if (needsForcePush.value) {
      // reset 后需要强制推送
      res = await gitForcePush(currentSiteId.value, needsForcePush.value)
      // gitPushBranch 内部使用 --force
    } else {
      res = await gitPushOnly(currentSiteId.value)
    }
    if (res.data?.success === false) throw new Error(res.data?.message)
    ElMessage.success(res.data?.message || '推送成功')
    pendingPushCount.value = 0
    needsForcePush.value = ''
    await loadGitStatus()
  } catch (e) { ElMessage.error('推送失败: ' + e.message) }
  finally { gitPushing.value = false }
}
async function onCommitDropdown(command) {
  if (command === 'commitAndPush') {
    await doGitCommit()
    if (pendingPushCount.value > 0) {
      await doGitPush()
    }
  }
}
async function doDiscardFile(item) {
  try {
    await ElMessageBox.confirm(
      `确定放弃对 "${item.path.split('/').pop()}" 的更改？此操作不可恢复。`,
      '放弃更改', { type: 'warning', confirmButtonText: '放弃更改', cancelButtonText: '取消' }
    )
  } catch { return }
  try {
    await gitDiscard(currentSiteId.value, item.path, item.statusType === 'untracked')
    // 如果该文件当前正在 diff 模式中，真正销毁 diff
    if (diffFilePath.value === item.path) exitDiffMode()
    // 无论已跟踪还是未跟踪，只要编辑器中有打开的标签，强制关闭
    if (openedFilesMap[item.path]) {
      await closeFile(item.path, true)
    }
    await loadGitStatus()
  } catch (e) { ElMessage.error('放弃更改失败: ' + e.message) }
}

// ===== 分支切换 =====
async function loadBranches(includeRemote = false) {
  if (!currentSiteId.value) return
  branchLoading.value = true
  try {
    const res = await gitListBranches(currentSiteId.value, includeRemote)
    branchList.value = res.data?.branches || []
    remoteOnlyBranches.value = res.data?.remoteBranches || []  // [{ name, hasLocal }]
  } catch (e) {
    ElMessage.error('获取分支列表失败: ' + e.message)
  } finally {
    branchLoading.value = false
  }
}

function openBranchDialog() {
  if (!hasRepo.value) return
  showBranchDialog.value = true
  newBranchName.value = ''
  loadBranches()
}

async function doCheckoutBranch(b) {
  if (b.current || checkingOutBranch.value) return
  // 有未保存文件时提示
  const dirtyFiles = openedFiles.value.filter(f => f.dirty)
  if (dirtyFiles.length > 0) {
    try {
      await ElMessageBox.confirm(
        `您有 ${dirtyFiles.length} 个文件未保存，切换分支后未保存的内容将丢失，是否继续？`,
        '切换分支', { type: 'warning', confirmButtonText: '仍然切换', cancelButtonText: '取消' }
      )
    } catch { return }
  }
  checkingOutBranch.value = b.name
  try {
    await gitCheckout(currentSiteId.value, b.name)
    showBranchDialog.value = false
    ElMessage.success('已切换到分支: ' + b.name)
    // 更新状态栏分支显示
    repoInfo.value = { ...repoInfo.value, branch: b.name }
    pendingPushCount.value = 0  // 切换分支后重置待推送计数
    // 关闭所有编辑器标签（内容已过期）
    openedFiles.value.slice().forEach(f => closeFile(f.path, true))
    // 刷新文件树 + git 状态
    treeKey.value++
    await loadGitStatus()
  } catch (e) {
    ElMessage.error('切换分支失败: ' + e.message)
  } finally {
    checkingOutBranch.value = ''
  }
}

async function doCreateBranch() {
  const name = newBranchName.value.trim()
  if (!name) return
  creatingBranch.value = true
  try {
    const res = await gitCreateBranch(currentSiteId.value, name)
    if (res.data?.success === false) throw new Error(res.data?.message)
    repoInfo.value = { ...repoInfo.value, branch: name }
    pendingPushCount.value = 0
    newBranchName.value = ''
    openedFiles.value.slice().forEach(f => closeFile(f.path, true))
    treeKey.value++
    await loadGitStatus()
    await loadBranches()  // 刷新分支列表（不关闭 dialog，让用户可以继续操作）
    ElMessage.success('分支「' + name + '」已在本地建立，点击 ↑ 可推送到远端')
  } catch (e) {
    ElMessage.error('创建分支失败: ' + e.message)
  } finally {
    creatingBranch.value = false
  }
}

async function doPushBranchToRemote(b) {
  pushingBranchName.value = b.name
  try {
    const res = await gitPushBranch(currentSiteId.value, b.name)
    if (res.data?.success === false) throw new Error(res.data?.message)
    ElMessage.success('分支「' + b.name + '」已推送到远端')
    await loadBranches()
  } catch (e) {
    ElMessage.error('推送失败: ' + e.message)
  } finally {
    pushingBranchName.value = ''
  }
}

async function doFetchRemote() {
  fetchingRemote.value = true
  try {
    const res = await gitFetch(currentSiteId.value)
    if (res.data?.success === false) throw new Error(res.data?.message)
    const created = res.data?.created || []
    if (created.length > 0) {
      ElMessage.success('已同步，新建本地跟踪分支: ' + created.join(', '))
    } else {
      ElMessage.success('已同步，本地分支与远端一致')
    }
    await loadBranches(true)  // 用户主动同步，用 ls-remote 获取远端完整列表
  } catch (e) {
    ElMessage.error('同步失败: ' + e.message)
  } finally {
    fetchingRemote.value = false
  }
}

async function doCheckoutRemoteBranch(branchName) {
  checkingOutBranch.value = branchName
  try {
    const res = await gitCheckout(currentSiteId.value, branchName)
    if (!res.data?.success && res.data?.success !== undefined) throw new Error(res.data?.message)
    repoInfo.value = { ...repoInfo.value, branch: branchName }
    pendingPushCount.value = 0
    openedFiles.value.slice().forEach(f => closeFile(f.path, true))
    treeKey.value++
    await loadGitStatus()
    await loadBranches()
    ElMessage.success('已检出远端分支: ' + branchName)
  } catch (e) {
    ElMessage.error('检出失败: ' + e.message)
  } finally {
    checkingOutBranch.value = ''
  }
}

async function doDeleteRemoteBranch(branchName) {
  try {
    await ElMessageBox.confirm(
      `确定删除远端分支「origin/${branchName}」？该操作不影响本地分支。`,
      '删除远端分支', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
  } catch { return }
  deletingRemoteBranch.value = branchName
  try {
    const res = await gitDeleteRemoteBranch(currentSiteId.value, branchName)
    if (res.data?.success) {
      ElMessage.success('远端分支 origin/' + branchName + ' 已删除')
    } else {
      const msg = res.data?.message || '未知错误'
      if (msg.includes('不存在')) {
        ElMessage.warning(msg)
      } else {
        ElMessage.error('删除远端分支失败: ' + msg)
      }
    }
    await loadBranches()
  } catch (e) {
    ElMessage.error('删除远端分支失败: ' + e.message)
  } finally {
    deletingRemoteBranch.value = ''
  }
}

async function doDeleteBranch(b) {
  if (b.current) return
  try {
    await ElMessageBox.confirm(
      `确定删除本地分支「${b.name}」？`,
      '删除分支', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
    )
  } catch { return }
  deletingBranch.value = b.name
  try {
    const res = await gitDeleteBranch(currentSiteId.value, b.name)
    if (res.data?.notFullyMerged) {
      // 分支有未合并提交，弹中文二次确认
      try {
        await ElMessageBox.confirm(
          res.data?.message || `分支「${b.name}」存在未合并的提交，是否强制删除？`,
          '强制删除', { type: 'warning', confirmButtonText: '强制删除', cancelButtonText: '取消' }
        )
      } catch { deletingBranch.value = ''; return }
      const res2 = await gitDeleteBranch(currentSiteId.value, b.name, true)
      if (res2.data?.success === false) throw new Error(res2.data?.message)
    } else if (res.data?.success === false) {
      throw new Error(res.data?.message || '删除失败')
    }
    ElMessage.success('本地分支「' + b.name + '」已删除')
    await loadBranches()
  } catch (e) {
    ElMessage.error('删除分支失败: ' + e.message)
  } finally {
    deletingBranch.value = ''
  }
}

// ===== 合并分支 =====
async function doMergeBranch(b) {
  const currentBranch = repoInfo.value?.branch || 'main'
  try {
    await ElMessageBox.confirm(
      `将分支「${b.name}」合并到当前分支「${currentBranch}」？`,
      '合并分支', { type: 'info', confirmButtonText: '合并', cancelButtonText: '取消' }
    )
  } catch { return }
  mergingBranchName.value = b.name
  try {
    const res = await gitMergeBranch(currentSiteId.value, b.name)
    if (res.data?.conflict) {
      // 有冲突：弹冲突处理对话框
      mergeConflictSourceBranch.value = b.name
      mergeConflictFiles.value = res.data?.conflictFiles || []
      showMergeConflictDialog.value = true
    } else if (res.data?.success) {
      ElMessage.success(res.data.message || '合并成功')
      pendingPushCount.value++
      treeKey.value++
      await loadGitStatus()
      await loadGitLog()
    } else {
      ElMessage.error('合并失败: ' + (res.data?.message || '未知错误'))
    }
  } catch (e) {
    ElMessage.error('合并失败: ' + e.message)
  } finally {
    mergingBranchName.value = ''
  }
}

async function doAbortMerge() {
  abortingMerge.value = true
  try {
    const res = await gitMergeAbort(currentSiteId.value)
    if (res.data?.success) {
      ElMessage.success('已放弃合并，工作区已恢复')
      showMergeConflictDialog.value = false
      mergeConflictFiles.value = []
      mergeConflictSourceBranch.value = ''
      isMerging.value = false
      statusConflictFiles.value = []
      treeKey.value++
      await loadGitStatus()
    } else {
      ElMessage.error('放弃合并失败: ' + (res.data?.message || '未知错误'))
    }
  } catch (e) {
    ElMessage.error('放弃合并失败: ' + e.message)
  } finally {
    abortingMerge.value = false
  }
}

/** 打开冲突解决器：在 Diff 对比编辑器中打开，工具栏升级为冲突处理模式 */
async function openConflictResolver(filePath) {
  showMergeConflictDialog.value = false
  conflictResolverFilePath.value = filePath
  conflictResolverFileName.value = filePath.replace(/\\/g, '/').split('/').pop()
  // 用 Diff 编辑器打开（左=HEAD原始，右=带冲突标记的工作区）
  await viewGitDiff(filePath)
  isConflictMode.value = true
  await nextTick()
  _refreshConflictState(0)
}

function exitConflictMode() {
  // 清除冲突高亮装饰
  const modEditor = diffEditorInstance?.getModifiedEditor()
  if (modEditor && conflictDecorationIds.value.length) {
    conflictDecorationIds.value = modEditor.deltaDecorations(conflictDecorationIds.value, [])
  }
  conflictDecorationIds.value = []
  isConflictMode.value = false
  conflictTotalCount.value = 0
  conflictCurrentIdx.value = 0
  conflictCurrentHead.value = []
  conflictCurrentIncoming.value = []
  conflictCurrentRange.value = null
  conflictResolverFilePath.value = ''
  conflictResolverFileName.value = ''
  // 同时退出 Diff 模式（关闭对比窗口）
  exitDiffMode()
}

/** 兼容旧调用 */
function openConflictFile(filePath) { openConflictResolver(filePath) }

/** 扫描 Diff 工作区中所有冲突块的行范围（1-based）*/
function findConflictRanges() {
  const editor = diffEditorInstance?.getModifiedEditor()
  if (!editor) return []
  const lines = editor.getValue().split('\n')
  const ranges = []
  let state = 'text', startLine = -1
  for (let i = 0; i < lines.length; i++) {
    const l = lines[i]
    if (state === 'text' && l.startsWith('<<<<<<<')) { startLine = i + 1; state = 'conflict' }
    else if (state === 'conflict' && l.startsWith('>>>>>>>')) { ranges.push({ startLine, endLine: i + 1 }); state = 'text' }
  }
  return ranges
}

/** 刷新冲突块数量并在 Diff 修改工作区滚动到指定冲突块 */
function _refreshConflictState(idx) {
  const modEditor = diffEditorInstance?.getModifiedEditor()
  if (!modEditor) return
  const ranges = findConflictRanges()
  conflictTotalCount.value = ranges.length
  if (ranges.length === 0) {
    if (conflictDecorationIds.value.length) {
      conflictDecorationIds.value = modEditor.deltaDecorations(conflictDecorationIds.value, [])
    }
    return
  }
  const safeIdx = Math.max(0, Math.min(idx, ranges.length - 1))
  conflictCurrentIdx.value = safeIdx
  const r = ranges[safeIdx]
  modEditor.revealLinesInCenter(r.startLine, r.endLine)
  // 解析并存储当前冲突块的 head/incoming 内容，为重新选择提供支持
  const allLines = modEditor.getValue().split('\n')
  let hBuf = [], iBuf = [], st = 'head'
  for (let i = r.startLine - 1; i <= r.endLine - 1; i++) {
    const l = allLines[i]
    if (l.startsWith('<<<<<<<')) { st = 'head'; continue }
    if (l.startsWith('|||||||')) { st = 'base'; continue }
    if (l.startsWith('=======') && st !== 'base') { st = 'incoming'; continue }
    if (l.startsWith('=======') && st === 'base') { st = 'incoming2'; continue }
    if (l.startsWith('>>>>>>>')) break
    if (st === 'head') hBuf.push(l)
    else if (st === 'incoming' || st === 'incoming2') iBuf.push(l)
  }
  conflictCurrentHead.value = hBuf
  conflictCurrentIncoming.value = iBuf
  conflictCurrentRange.value = null  // 导航切换时重置，重新由股冲突标记定位
  // 装饰：高亮所有冲突块
  const decorations = []
  ranges.forEach((cr, i) => {
    decorations.push({
      range: new monacoLib.Range(cr.startLine, 1, cr.endLine, 1),
      options: {
        isWholeLine: true,
        className: i === safeIdx ? 'cr-line-active' : 'cr-line-other',
        overviewRuler: { color: i === safeIdx ? '#f14c4c' : '#888', position: monacoLib.editor.OverviewRulerLane.Left }
      }
    })
  })
  conflictDecorationIds.value = modEditor.deltaDecorations(conflictDecorationIds.value, decorations)
}

/** 跳转到第 idx 个冲突块 */
function navigateConflict(idx) {
  _refreshConflictState(idx)
}

/** 在 Diff 修改工作区里直接替换当前冲突块。已解决的块也可重新选择。20 */
function applyConflictResolution(choice) {
  const modEditor = diffEditorInstance?.getModifiedEditor()
  if (!modEditor) return
  let startLine, endLine, headBuf, incomingBuf
  const ranges = findConflictRanges()
  if (ranges.length > 0) {
    // 最常规情况：当前块含有冲突标记
    const r = ranges[Math.min(conflictCurrentIdx.value, ranges.length - 1)]
    const lines = modEditor.getValue().split('\n')
    headBuf = []; incomingBuf = []; let st = 'head'
    for (let i = r.startLine - 1; i <= r.endLine - 1; i++) {
      const l = lines[i]
      if (l.startsWith('<<<<<<<')) { st = 'head'; continue }
      if (l.startsWith('|||||||')) { st = 'base'; continue }
      if (l.startsWith('=======') && st !== 'base') { st = 'incoming'; continue }
      if (l.startsWith('=======') && st === 'base') { st = 'incoming2'; continue }
      if (l.startsWith('>>>>>>>')) break
      if (st === 'head') headBuf.push(l)
      else if (st === 'incoming' || st === 'incoming2') incomingBuf.push(l)
    }
    startLine = r.startLine; endLine = r.endLine
    // 存储本次解决的原始内容，为后续重新选择提供数据
    conflictCurrentHead.value = headBuf
    conflictCurrentIncoming.value = incomingBuf
  } else if (conflictCurrentRange.value && (conflictCurrentHead.value.length || conflictCurrentIncoming.value.length)) {
    // 冲突已解决标记被移除，但用户想切换选择：对上次解决的范围重新应用
    startLine = conflictCurrentRange.value.startLine
    endLine = conflictCurrentRange.value.endLine
    headBuf = conflictCurrentHead.value
    incomingBuf = conflictCurrentIncoming.value
  } else {
    return
  }
  let replacementLines
  if (choice === 'current') replacementLines = headBuf
  else if (choice === 'incoming') replacementLines = incomingBuf
  else replacementLines = [...headBuf, ...incomingBuf]
  const lines = modEditor.getValue().split('\n')
  const endCol = (lines[endLine - 1] || '').length + 1
  modEditor.executeEdits('conflict-resolve', [{
    range: new monacoLib.Range(startLine, 1, endLine, endCol),
    text: replacementLines.join('\n')
  }])
  // 记录解决后的行范围，下次再次选择时使用
  conflictCurrentRange.value = { startLine, endLine: startLine + replacementLines.length - 1 }
  const newRanges = findConflictRanges()
  _refreshConflictState(Math.min(conflictCurrentIdx.value, Math.max(0, newRanges.length - 1)))
}

/** 保存已解决的文件（从 Diff 修改工作区读取）并暂存 */
async function saveConflictResolved() {
  const modEditor = diffEditorInstance?.getModifiedEditor()
  if (!modEditor) return
  conflictSaving.value = true
  try {
    const content = modEditor.getValue()
    await saveFile(currentSiteId.value, conflictResolverFilePath.value, content)
    await gitStage(currentSiteId.value, conflictResolverFilePath.value)
    ElMessage.success('冲突已解决并暂存：' + conflictResolverFileName.value)
    statusConflictFiles.value = statusConflictFiles.value.filter(f => f !== conflictResolverFilePath.value)
    mergeConflictFiles.value = mergeConflictFiles.value.filter(f => f !== conflictResolverFilePath.value)
    exitConflictMode()
    await loadGitStatus()
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || e))
  } finally {
    conflictSaving.value = false
  }
}


async function viewGitDiff(filePath) {
  if (!filePath) {
    // 无具体文件：对话框显示全部 diff
    showGitDiffDialog.value = true
    gitDiffTitle.value = 'Git Diff: 全部变更'
    gitDiffContent.value = ''
    gitDiffLoading.value = true
    try {
      const res = await getGitDiff(currentSiteId.value, '')
      gitDiffContent.value = res.data?.diff || '（无变更）'
    } catch (e) {
      gitDiffContent.value = '获取 diff 失败: ' + e.message
    } finally {
      gitDiffLoading.value = false
    }
    return
  }

  // 有具体文件：内联 Monaco Diff Editor
  diffFilePath.value = filePath
  isDiffMode.value = true
  await nextTick()
  if (!diffContainer.value) return

  const monaco = monacoLib
  if (diffEditorInstance) {
    const _dm = diffEditorInstance.getModel()
    try { diffEditorInstance.setModel(null) } catch (_) {}
    _dm?.original?.dispose()
    _dm?.modified?.dispose()
    diffEditorInstance.dispose()
    diffEditorInstance = null
  }

  // 并发获取原始内容 + 当前内容
  const [origRes, modifiedText] = await Promise.all([
    getGitOriginal(currentSiteId.value, filePath).catch(() => ({ data: { content: '' } })),
    (async () => {
      if (openedFilesMap[filePath]) return openedFilesMap[filePath].content
      try {
        const r = await readFile(currentSiteId.value, filePath)
        return r.data?.content ?? ''
      } catch { return '' }
    })()
  ])

  const originalText = origRes.data?.content ?? ''
  const ext = filePath.split('.').pop()
  const lang = getMonacoLanguage(ext)

  const origUri = monaco.Uri.parse('diff-orig:///' + filePath.replace(/\\/g, '/'))
  const modUri  = monaco.Uri.parse('diff-mod:///'  + filePath.replace(/\\/g, '/'))
  monaco.editor.getModel(origUri)?.dispose()
  monaco.editor.getModel(modUri)?.dispose()

  const origModel = monaco.editor.createModel(originalText, lang, origUri)
  const modModel  = monaco.editor.createModel(modifiedText, lang, modUri)

  diffEditorInstance = monaco.editor.createDiffEditor(diffContainer.value, {
    theme: 'vs-dark',
    fontSize: 14,
    automaticLayout: true,
    renderSideBySide: true,
    ignoreTrimWhitespace: false,
    originalEditable: false,
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    smoothScrolling: true,
    fontFamily: "'JetBrains Mono', 'Fira Code', Consolas, monospace"
  })
  diffEditorInstance.setModel({ original: origModel, modified: modModel })
}

function exitDiffMode() {
  isDiffMode.value = false
  if (diffEditorInstance) {
    const _dm = diffEditorInstance.getModel()
    try { diffEditorInstance.setModel(null) } catch (_) {}
    _dm?.original?.dispose()
    _dm?.modified?.dispose()
    diffEditorInstance.dispose()
    diffEditorInstance = null
  }
  diffFilePath.value = ''
}

// 仅隐藏 diff 面板，保留 editor 实例（可点标签恢复）
function hideDiffMode() {
  isDiffMode.value = false
}

async function saveDiffChanges() {
  if (!diffEditorInstance || !diffFilePath.value) return
  const modifiedContent = diffEditorInstance.getModifiedEditor().getValue()
  saving.value = true
  try {
    await saveFile(currentSiteId.value, diffFilePath.value, modifiedContent)
    ElMessage.success('保存成功')
    const fd = openedFilesMap[diffFilePath.value]
    if (fd) {
      fd.content = modifiedContent
      fd.originalContent = modifiedContent
      fd.dirty = false
      fd.model?.setValue(modifiedContent)
      const tab = openedFiles.value.find(f => f.path === diffFilePath.value)
      if (tab) tab.dirty = false
    }
    loadGitStatus()
  } catch (e) {
    ElMessage.error('保存失败: ' + e.message)
  } finally {
    saving.value = false
  }
}

async function loadGitLog(includeRemote = false) {
  if (!currentSiteId.value || !hasRepo.value) return
  showGitLogDialog.value = true
  gitLogLoading.value = true
  try {
    const res = await getGitLog(currentSiteId.value, 100, gitLogBranchFilter.value, includeRemote)
    gitLogList.value = res.data?.commits || []
    // 始终更新分支列表（同步远端时 includeRemote=true，确保拿到最新远端分支）
    if (res.data?.allBranches?.length) {
      gitLogAllBranches.value = res.data.allBranches
    }
  } catch { gitLogList.value = [] }
  finally { gitLogLoading.value = false }
}

// 同步远端 (fetch --prune) 再刷新 log
async function doFetchThenReload() {
  if (!currentSiteId.value) return
  gitLogFetchLoading.value = true
  try {
    const res = await gitFetch(currentSiteId.value)
    if (res.data?.success === false) throw new Error(res.data?.message)
    ElMessage.success(res.data?.message || '同步完成')
    // 清空筛选，用 ls-remote 重新加载，确保分支列表与远端完全一致
    gitLogAllBranches.value = []
    gitLogBranchFilter.value = ''
    await loadGitLog(true)
  } catch (e) {
    ElMessage.warning('远端同步失败：' + (e.message || '网络错误'))
  } finally {
    gitLogFetchLoading.value = false
  }
}

async function doCheckoutCommit(commit) {
  try {
    await ElMessageBox.confirm(
      `将切换到提交 ${commit.shortHash}(“${commit.message}”)，进入 Detached HEAD 状态。\n如需保留变更请先创建分支。`,
      '检出提交', { type: 'warning', confirmButtonText: '检出', cancelButtonText: '取消' }
    )
  } catch { return }
  commitActionLoading.value = commit.hash + '_co'
  try {
    const res = await gitCheckoutCommit(currentSiteId.value, commit.hash)
    if (!res.data?.success) throw new Error(res.data?.message)
    ElMessage.success(res.data.message)
    repoInfo.value = { ...repoInfo.value, branch: '(分离 HEAD)' }
    openedFiles.value.slice().forEach(f => closeFile(f.path, true))
    treeKey.value++
    await loadGitStatus()
  } catch (e) { ElMessage.error('检出失败: ' + e.message) }
  finally { commitActionLoading.value = '' }
}

async function doBranchFromCommit(commit) {
  let branchName
  try {
    const { value } = await ElMessageBox.prompt(
      `基于提交 ${commit.shortHash} 创建新分支：`,
      '创建分支', { inputPlaceholder: '输入分支名称', confirmButtonText: '创建', cancelButtonText: '取消',
        inputValidator: v => v && v.trim() ? true : '分支名不能为空' }
    )
    branchName = value.trim()
  } catch { return }
  commitActionLoading.value = commit.hash + '_br'
  try {
    const res = await gitBranchFromCommit(currentSiteId.value, commit.hash, branchName)
    if (!res.data?.success) throw new Error(res.data?.message)
    ElMessage.success(res.data.message)
    repoInfo.value = { ...repoInfo.value, branch: branchName }
    pendingPushCount.value = 0
    openedFiles.value.slice().forEach(f => closeFile(f.path, true))
    treeKey.value++
    await loadGitStatus()
    await loadGitLog()
  } catch (e) { ElMessage.error('创建分支失败: ' + e.message) }
  finally { commitActionLoading.value = '' }
}

async function doMergeCommit(commit) {
  try {
    await ElMessageBox.confirm(
      `将提交 ${commit.shortHash}(“${commit.message}”) 合并到当前分支？`,
      '合并提交', { type: 'info', confirmButtonText: '合并', cancelButtonText: '取消' }
    )
  } catch { return }
  commitActionLoading.value = commit.hash + '_mg'
  try {
    const res = await gitMergeCommit(currentSiteId.value, commit.hash)
    if (res.data?.conflict) {
      ElMessage.warning('合并冲突，请手动解冲突后提交')
    } else if (!res.data?.success) {
      throw new Error(res.data?.message)
    } else {
      ElMessage.success(res.data.message)
      pendingPushCount.value++
    }
    treeKey.value++
    await loadGitStatus()
    await loadGitLog()
  } catch (e) { ElMessage.error('合并失败: ' + e.message) }
  finally { commitActionLoading.value = '' }
}

async function doRevertCommit(commit) {
  try {
    await ElMessageBox.confirm(
      `将生成一个反向提交，撤销「${commit.message}」的所有更改。这不会删除历史，仅生成新提交。`,
      'Revert 提交', { type: 'warning', confirmButtonText: 'Revert', cancelButtonText: '取消' }
    )
  } catch { return }
  commitActionLoading.value = commit.hash + '_rv'
  try {
    const res = await gitRevertCommit(currentSiteId.value, commit.hash)
    if (res.data?.conflict) {
      ElMessage.warning('Revert 冲突，请手动解冲突后提交')
    } else if (!res.data?.success) {
      throw new Error(res.data?.message)
    } else {
      ElMessage.success(res.data.message)
      pendingPushCount.value++
    }
    treeKey.value++
    await loadGitStatus()
    await loadGitLog()
  } catch (e) { ElMessage.error('Revert 失败: ' + e.message) }
  finally { commitActionLoading.value = '' }
}

async function doResetForcePush(commit) {
  const actualBranch = repoInfo.value?.branch
  const branchMismatch = gitLogBranchFilter.value && actualBranch && gitLogBranchFilter.value !== actualBranch
  // 第一重确认：明确提示将操作的实际分支
  try {
    const mismatchNote = branchMismatch
      ? `\n\n📌 注意：上方正在查看「${gitLogBranchFilter.value}」的历史，但当前工作区分支为「${actualBranch}」，Reset 将在「${actualBranch}」上执行。`
      : ''
    await ElMessageBox.confirm(
      `⚠️ 高危操作！\n将对当前工作区分支「${actualBranch}」执行 Reset 到提交 ${commit.shortHash}("${commit.message}")。${mismatchNote}\n\n此后的所有提交记录将被清除，本地操作不可恢复！`,
      '警告：将删除本地历史', { type: 'error', confirmButtonText: '我知道风险，继续', cancelButtonText: '取消' }
    )
  } catch { return }
  // 第二重确认：要求输入确认文字
  try {
    const { value } = await ElMessageBox.prompt(
      `请在下方输入「确认删除」才能继续：`,
      '最终确认', {
        confirmButtonText: '执行 Reset', cancelButtonText: '放弃',
        inputValidator: v => v === '确认删除' ? true : '请刚好输入「确认删除」'
      }
    )
    if (value !== '确认删除') return
  } catch { return }
  commitActionLoading.value = commit.hash + '_rf'
  try {
    const res = await gitResetForcePush(currentSiteId.value, commit.hash)
    if (!res.data?.success) throw new Error(res.data?.message)
    // reset 成功，等待用户手动推送
    ElMessage.success(`${res.data.message}`)
    needsForcePush.value = res.data.branch || actualBranch || ''  // 始终用后端返回的真实分支
    pendingPushCount.value = 0
    openedFiles.value.slice().forEach(f => closeFile(f.path, true))
    treeKey.value++
    await loadGitStatus()
    await loadGitLog()
  } catch (e) { ElMessage.error('操作失败: ' + e.message) }
  finally { commitActionLoading.value = '' }
}

// ===== 配置编辑器函数 =====
async function loadCfgMappings() {
  if (!currentSiteId.value || !currentSite.value?.templateId) {
    cfgMappings.value = []
    return
  }
  cfgLoading.value = true
  try {
    const res = await getSiteTemplate(currentSite.value.templateId)
    const tmpl = res.data
    if (tmpl?.configMappings) {
      try {
        const parsed = JSON.parse(tmpl.configMappings)
        cfgMappings.value = Array.isArray(parsed) ? parsed : []
      } catch {
        cfgMappings.value = []
      }
    } else {
      cfgMappings.value = []
    }
    // 初始化为默认值
    const defaults = {}
    for (const item of cfgMappings.value) {
      defaults[item.key] = item.defaultValue || ''
    }
    cfgValues.value = { ...defaults }
    cfgOriginalValues.value = { ...defaults }
    await cfgLoadValues()
  } catch (e) {
    ElMessage.error('加载配置映射失败：' + (e.message || String(e)))
    cfgMappings.value = []
  } finally {
    cfgLoading.value = false
  }
}

async function cfgLoadValues() {
  if (cfgMappings.value.length === 0) return
  const fileCache = {}
  const newValues = { ...cfgValues.value }
  for (const item of cfgMappings.value) {
    if (!item.filePath) continue
    if (!fileCache[item.filePath]) {
      try {
        const res = await readFile(currentSiteId.value, item.filePath)
        fileCache[item.filePath] = res.data?.content || ''
      } catch {
        fileCache[item.filePath] = null
      }
    }
    const content = fileCache[item.filePath]
    if (content == null) continue
    const extracted = cfgExtractValue(content, item)
    if (extracted !== null) newValues[item.key] = extracted
  }
  cfgValues.value = { ...newValues }
  cfgOriginalValues.value = { ...newValues }
}

async function cfgSaveAll() {
  if (cfgDirtyKeys.value.size === 0) return
  cfgSaving.value = true
  try {
    const fileCache = {}
    for (const item of cfgMappings.value) {
      if (!cfgDirtyKeys.value.has(item.key)) continue
      if (!item.filePath) continue
      if (!fileCache[item.filePath]) {
        const res = await readFile(currentSiteId.value, item.filePath)
        fileCache[item.filePath] = res.data?.content || ''
      }
      const newContent = cfgReplaceValue(fileCache[item.filePath], item, cfgValues.value[item.key])
      if (newContent === null) {
        ElMessage.warning(`字段「${item.label}」未能匹配文件内容，已跳过。请检查 tsKey 或 searchRegex 配置。`)
        continue
      }
      fileCache[item.filePath] = newContent
    }
    for (const [filePath, content] of Object.entries(fileCache)) {
      await saveFile(currentSiteId.value, filePath, content)
    }
    cfgOriginalValues.value = { ...cfgValues.value }
    ElMessage.success('配置已保存，可通过「源代码」Tab 提交推送')
    if (showPreview.value && previewStatus.value.running) refreshPreview()
  } catch (e) {
    ElMessage.error('保存失败：' + (e.message || String(e)))
  } finally {
    cfgSaving.value = false
  }
}

function cfgScrollTo(key) {
  cfgActiveKey.value = key
  nextTick(() => {
    const el = cfgFormPanelRef.value?.querySelector(`[data-cfg-key="${key}"]`)
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  })
}

function cfgTypeIcon(type) {
  const map = { text: 'Edit', color: 'Brush', textarea: 'Document', number: 'Aim', switch: 'SwitchButton' }
  return map[type] || 'Edit'
}

function cfgExtractValue(content, item) {
  const pattern = cfgBuildRegex(item)
  if (!pattern) return null
  try {
    const regex = new RegExp(pattern, 's')
    const match = content.match(regex)
    if (match && match[2] !== undefined) return match[2]
  } catch {}
  return null
}

function cfgReplaceValue(content, item, newValue) {
  const pattern = cfgBuildRegex(item)
  if (!pattern) return null
  const tpl = item.replaceTpl || '$1$VALUE$3'
  try {
    const regex = new RegExp(pattern, 's')
    if (!regex.test(content)) return null
    return content.replace(regex, (match, g1, g2, g3) => {
      return tpl.replace('$1', g1).replace('$VALUE', cfgEscapeTs(newValue)).replace('$3', g3)
    })
  } catch {
    return null
  }
}

function cfgBuildRegex(item) {
  if (item.searchRegex) return item.searchRegex
  if (item.tsKey) {
    const escaped = item.tsKey.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    return `(${escaped}\\s*:\\s*['"\`])([^'"\`]*?)(['"\`])`
  }
  return null
}

function cfgEscapeTs(value) {
  return String(value).replace(/\\/g, '\\\\').replace(/'/g, "\\'").replace(/`/g, '\\`')
}

// 切换 tab 时自动加载
watch(sidebarTab, (tab) => {
  if (tab === 'git') loadGitStatus()
  if (tab === 'config' && cfgMappings.value.length === 0) loadCfgMappings()
})

// 切换到 github-actions-push 模式时自动检查配置状态
watch(() => deployForm.value.deployMode, (val) => {
  if (val === 'github-actions-push') {
    pushSetupStatus.value = null
    checkPushSetupStatus()
  }
  if (val === 'github-actions') {
    dispatchSetupStatus.value = null
    checkDispatchSetupStatus()
  }
})

// token 填写后自动刷新状态（防抖，避免频繁请求）
let _pushTokenCheckTimer = null
watch(() => deployForm.value.actionPushToken, () => {
  if (deployForm.value.deployMode !== 'github-actions-push') return
  clearTimeout(_pushTokenCheckTimer)
  _pushTokenCheckTimer = setTimeout(() => checkPushSetupStatus(), 800)
})

let _dispatchTokenCheckTimer = null
watch(() => deployForm.value.actionToken, () => {
  if (deployForm.value.deployMode !== 'github-actions') return
  clearTimeout(_dispatchTokenCheckTimer)
  _dispatchTokenCheckTimer = setTimeout(() => checkDispatchSetupStatus(), 800)
})

async function copyLog() {
  try {
    await navigator.clipboard.writeText(isViewingLastLog.value ? lastDeployLog.value : operationLog.value)
    ElMessage.success({ message: '日志已复制', duration: 1500, plain: true })
  } catch {
    // 降级：选中 pre 内容
    const pre = logConsoleRef.value?.querySelector('pre')
    if (pre) {
      const range = document.createRange()
      range.selectNodeContents(pre)
      const sel = window.getSelection()
      sel.removeAllRanges()
      sel.addRange(range)
      document.execCommand('copy')
      sel.removeAllRanges()
      ElMessage.success({ message: '日志已复制', duration: 1500, plain: true })
    }
  }
}

// ===== 预览服务器 =====
async function togglePreviewServer() {
  if (previewStatus.value.running) {
    previewLoading.value = true
    try {
      await stopPreview(currentSiteId.value)
      previewStatus.value = { running: false, port: null }
      showPreview.value = false
      stopPreviewPolling()
    } finally {
      previewLoading.value = false
    }
  } else {
    previewLoading.value = true
    try {
      const res = await startPreview(currentSiteId.value)
      if (res.data?.success && res.data?.port) {
        previewStatus.value = { running: true, port: res.data.port, previewUrl: res.data.previewUrl }
        showPreview.value = true
        startPreviewPolling()
        ElMessage.success('预览已启动: ' + res.data.port)
      } else {
        // 显示日志（如果有的话）
        const logContent = res.data?.log || ''
        if (logContent) {
          operationLog.value = '> 启动预览服务器失败：\n' + logContent
          logDialogTitle.value = '预览启动日志'
          showLogDialog.value = true
        }
        ElMessage.error(res.data?.message || '启动失败')
      }
    } catch (e) {
      ElMessage.error('启动失败: ' + e.message)
    } finally {
      previewLoading.value = false
    }
  }
}

function syncPreviewAddress() {
  try {
    const iframe = previewFrame.value
    if (!iframe) return
    const iframeUrl = iframe.contentWindow?.location?.href
    if (iframeUrl && iframeUrl !== 'about:blank') {
      const base = previewProxyBase.value
      try {
        const url = new URL(iframeUrl)
        // 地址栏直接显示最终落地的完整路径（含代理前缀），不做任何剔除/重拼，消除跳变感
        previewAddressBar.value = url.pathname + url.search + url.hash
        // previewPath 剔除代理前缀，供「新标签页」按钒使用
        const basePath = base.startsWith('http') ? new URL(base).pathname : base
        previewPath.value = url.pathname.startsWith(basePath)
          ? (url.pathname.replace(basePath, '') || '/')
          : url.pathname
      } catch {
        previewPath.value = '/'
      }
    }
  } catch (e) {
    // 跨域时无法访问（理论上代理后同域不会跨域），忽略
  }
}

function navigateToAddress() {
  const input = previewAddressBar.value.trim()
  if (!input) return
  const base = previewProxyBase.value
  if (input.startsWith('http://') || input.startsWith('https://')) {
    try {
      const url = new URL(input)
      // 若地址仍在代理路径内，提取子路径；否则视为直接注入路径
      const basePath = base.startsWith('http') ? new URL(base).pathname : base
      previewPath.value = url.pathname.startsWith(basePath)
        ? (url.pathname.replace(basePath, '') || '/') + url.search + url.hash
        : url.pathname + url.search + url.hash
    } catch { previewPath.value = '/' }
  } else {
    // 如果输入包含代理前缀（地址栏显示的是完整路径），先剥离再赋値
    const stripped = input.startsWith(base) ? (input.slice(base.length) || '/') : (input.startsWith('/') ? input : '/' + input)
    previewPath.value = stripped
  }
  previewAddressBar.value = previewPath.value  // 地址栏只显示子路径，不含代理 base
  iframeSrc.value = previewIframeUrl.value
}

function refreshPreview() {
  iframeSrc.value = 'about:blank'
  nextTick(() => {
    // 加时间戳 cache-bust，防止浏览器从缓存读取旧 HTML（双重保障）
    const base = previewIframeUrl.value
    const sep = base.includes('?') ? '&' : '?'
    iframeSrc.value = base + sep + '_nc=' + Date.now()
  })
}

function openPreviewTab() {
  if (previewStatus.value.running && previewStatus.value.port) {
    const base = previewProxyBase.value
    const path = previewPath.value || '/'
    window.open(`${base}${path}`, '_blank')
  }
}

function startPreviewPolling() {
  stopPreviewPolling()
  previewPollingTimer = setInterval(async () => {
    if (!currentSiteId.value) return
    try {
      const res = await getPreviewStatus(currentSiteId.value)
      if (res.data) previewStatus.value = res.data
      if (!res.data?.running) stopPreviewPolling()
    } catch { stopPreviewPolling() }
  }, 5000)
}
function stopPreviewPolling() {
  if (previewPollingTimer) { clearInterval(previewPollingTimer); previewPollingTimer = null }
}

// ===== 侧边栏/预览面板拖拽 =====
let _dragging = null, _startX = 0, _startW = 0
function startSidebarResize(e) {
  _dragging = 'sidebar'; _startX = e.clientX; _startW = sidebarWidth.value
  document.addEventListener('mousemove', _onDrag); document.addEventListener('mouseup', _stopDrag)
}
function startPreviewResize(e) {
  _dragging = 'preview'; _startX = e.clientX; _startW = previewWidth.value
  document.addEventListener('mousemove', _onDrag); document.addEventListener('mouseup', _stopDrag)
}
function _onDrag(e) {
  if (_dragging === 'sidebar') sidebarWidth.value = Math.max(160, Math.min(500, _startW + e.clientX - _startX))
  if (_dragging === 'preview') previewWidth.value = Math.max(200, Math.min(900, _startW - (e.clientX - _startX)))
}
function _stopDrag() {
  _dragging = null
  document.removeEventListener('mousemove', _onDrag)
  document.removeEventListener('mouseup', _stopDrag)
}

// ===== 生命周期 =====
function onPreviewMessage(event) {
  if (event.data?.type === 'PREVIEW_NAVIGATION') {
    const { path, href } = event.data
    if (path) previewPath.value = path
    if (href) {
      try {
        // href 是完整 URL（含 http://localhost），只取 pathname 避免显示主机名造成跳变
        const url = new URL(href)
        previewAddressBar.value = url.pathname + url.search + url.hash
      } catch {
        previewAddressBar.value = href
      }
    }
  }
}

onMounted(async () => {
  window.addEventListener('click', closeCtxMenu)
  window.addEventListener('message', onPreviewMessage)
  await loadSiteList()
  await loadCfAccounts()
  await loadGitAccounts()
  const idFromQuery = route.query.siteId ? Number(route.query.siteId) : null
  const initialSiteId = normalizeCodeManageSiteId(idFromQuery)
  if (initialSiteId) {
    currentSiteId.value = initialSiteId
    await handleSiteChange(initialSiteId)
  }
  await nextTick()
  await initMonaco()
  // 异步加载项目类型，不阻塞主流程
  if (currentSiteId.value) loadMonacoTypes(currentSiteId.value)
})

// ===== GitHub Actions 工作流模板（公开中继仓库，完全免费）=====
// 公开中继仓库模板（Action 放在 Public 仓库，代码仓库可私有，完全免费）
const gaRelayTemplate = `name: 中继部署到 Cloudflare Workers

# ====================================================================
# 使用说明：
# 1. 将本文件放入一个【公开（Public）GitHub 仓库】的
#    .github/workflows/deploy.yml
# 2. 在该公开仓库 Settings → Secrets and variables → Actions 中添加：
#    - CLOUDFLARE_API_TOKEN  (Cloudflare API Token)
#    - CLOUDFLARE_ACCOUNT_ID (Cloudflare Account ID)
#    - GIT_ACCESS_TOKEN      (GitHub PAT，Fine-grained token 给目标
#                             仓库 Contents: Read 权限即可)
# 3. 公开仓库 Actions 完全免费，代码仓库保持私有也不消耗分钟数
# ====================================================================

on:
  workflow_dispatch:
    inputs:
      build_command:
        description: '构建命令'
        required: false
        default: 'pnpm run build'
      worker_name:
        description: 'Worker 名称（Cloudflare Workers 项目名）'
        required: false
      git_repo_url:
        description: '代码仓库 URL（如 https://github.com/owner/my-site）'
        required: true
      git_branch:
        description: '代码分支'
        required: false
        default: 'main'

jobs:
  deploy:
    name: 拉取代码并部署到 Cloudflare Workers
    runs-on: ubuntu-latest

    steps:
      - name: 拉取代码仓库
        run: |
          REPO_URL=\${{ inputs.git_repo_url }}
          # 将 https://github.com/... 转为带 Token 的认证 URL
          AUTH_URL=\${REPO_URL/https:\\/\\//https:\\/\\/x-token:\${{ secrets.GIT_ACCESS_TOKEN }}@}
          git clone --depth 1 --branch "\${{ inputs.git_branch || 'main' }}" "\$AUTH_URL" code-repo

      - name: 安装 pnpm
        uses: pnpm/action-setup@v4
        with:
          version: 10

      - name: 安装 Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'pnpm'
          cache-dependency-path: code-repo/pnpm-lock.yaml

      - name: 安装依赖
        working-directory: code-repo
        run: pnpm install

      - name: 构建项目
        working-directory: code-repo
        run: \${{ inputs.build_command || 'pnpm run build' }}

      - name: 部署到 Cloudflare
        uses: cloudflare/wrangler-action@v3
        with:
          apiToken: \${{ secrets.CLOUDFLARE_API_TOKEN }}
          accountId: \${{ secrets.CLOUDFLARE_ACCOUNT_ID }}
          workingDirectory: code-repo
`

function copyGaTemplate() {
  navigator.clipboard.writeText(gaRelayTemplate)
    .then(() => ElMessage.success('已复制到剪贴板'))
    .catch(() => ElMessage.error('复制失败，请手动选择复制'))
}

onUnmounted(() => {
  if (pullWs) { pullWs.close(); pullWs = null }
  if (buildWs) { buildWs.close(); buildWs = null }
  if (installWs) { installWs.close(); installWs = null }
  if (pushWs) { pushWs.close(); pushWs = null }
  gaPollStopped = true
  if (gaPollTimer) { clearTimeout(gaPollTimer); gaPollTimer = null }
  window.removeEventListener('click', closeCtxMenu)
  window.removeEventListener('message', onPreviewMessage)
  stopPreviewPolling()
  document.removeEventListener('mousemove', _onDrag)
  document.removeEventListener('mouseup', _stopDrag)
  if (monacoInstance) { monacoInstance.dispose(); monacoInstance = null }
  if (diffEditorInstance) {
    const _dm = diffEditorInstance.getModel()
    try { diffEditorInstance.setModel(null) } catch (_) {}
    _dm?.original?.dispose()
    _dm?.modified?.dispose()
    diffEditorInstance.dispose()
    diffEditorInstance = null
  }
  for (const f of Object.values(openedFilesMap)) { if (f.model) f.model.dispose() }
})
</script>

<style scoped>
/* ===== 全局布局 ===== */
.code-editor-layout {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 84px);
  background: #1e1e1e;
  color: #d4d4d4;
  overflow: hidden;
  user-select: none;
}

/* ===== 顶部工具栏 ===== */
.editor-toolbar {
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 46px;
  background: #2d2d2d;
  border-bottom: 1px solid #3c3c3c;
  padding: 0 10px;
  gap: 6px;
  flex-shrink: 0;
}
.toolbar-logo {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  color: #569cd6;
  white-space: nowrap;
}
.toolbar-center {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
  &::-webkit-scrollbar { height: 3px; }
  &::-webkit-scrollbar-thumb { background: #555; border-radius: 2px; }
}
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}
/* 每个操作卡片：分类标签 + 按钮 + 编辑图标横排 */
.tb-op-group {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 0 8px 0 6px;
  height: 30px;
  border-radius: 5px;
  background: #2a2d2e;
  border: 1px solid #3e3e3e;
  transition: border-color .15s;
  flex-shrink: 0;
  &:hover { border-color: #555; }
}
.tb-op-label {
  font-size: 10px;
  color: #5a5a5a;
  white-space: nowrap;
  padding-right: 5px;
  border-right: 1px solid #3c3c3c;
  line-height: 1;
  user-select: none;
}
.tb-op-edit {
  color: #4a4a4a;
  cursor: pointer;
  font-size: 12px;
  flex-shrink: 0;
  transition: color .15s;
  &:hover { color: #9cdcfe; }
}
.tb-op-link {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 11px;
  color: #569cd6;
  text-decoration: none;
  white-space: nowrap;
  &:hover { color: #9cdcfe; }
}
/* 按钮统一暗色风格 */
.editor-toolbar :deep(.el-button) {
  background: transparent !important;
  border-color: transparent !important;
  color: #bbb !important;
  padding: 3px 7px !important;
  height: 22px !important;
  font-size: 12px !important;
  font-family: 'Consolas', 'Courier New', monospace;
}
.editor-toolbar :deep(.el-button:hover) {
  background: #3a3a3a !important;
  color: #fff !important;
  border-color: transparent !important;
}
.editor-toolbar :deep(.el-button--primary) {
  background: #0e639c !important;
  border-color: #1177bb !important;
  color: #fff !important;
}
.editor-toolbar :deep(.el-button--success) {
  background: rgba(46,160,67,0.12) !important;
  border-color: transparent !important;
  color: #3fb950 !important;
}
.editor-toolbar :deep(.el-button--success:hover) {
  background: #2ea043 !important;
  color: #fff !important;
}
.editor-toolbar :deep(.el-button--danger) {
  background: rgba(185,28,28,0.12) !important;
  border-color: transparent !important;
  color: #f87171 !important;
}
.editor-toolbar :deep(.el-button--danger:hover) {
  background: #b91c1c !important;
  color: #fff !important;
}
.editor-toolbar :deep(.el-button--warning) {
  background: rgba(217,119,6,0.12) !important;
  border-color: transparent !important;
  color: #fbbf24 !important;
}
.editor-toolbar :deep(.el-button--warning:hover) {
  background: #d97706 !important;
  color: #fff !important;
}
.editor-toolbar :deep(.el-divider--vertical) { border-color: #555; }
.editor-toolbar :deep(.el-select .el-input__wrapper) {
  background: #3c3c3c !important;
  box-shadow: 0 0 0 1px #555 !important;
}
.editor-toolbar :deep(.el-select .el-input__inner) { color: #ccc; }
.editor-toolbar :deep(.el-button-group .el-button) {
  border-color: transparent !important;
}

/* ===== 主体 ===== */
.editor-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* ===== 文件侧边栏 ===== */
.editor-sidebar {
  position: relative;
  display: flex;
  flex-direction: column;
  background: #252526;
  border-right: 1px solid #3c3c3c;
  min-width: 160px;
  max-width: 500px;
  flex-shrink: 0;
}
.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 12px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #aaa;
  border-bottom: 1px solid #3c3c3c;
  flex-shrink: 0;
}
.sidebar-action {
  cursor: pointer;
  color: #777;
  transition: color .15s;
}
.sidebar-action:hover { color: #d4d4d4; }
.sidebar-content { flex: 1; overflow: auto; }
.sidebar-content::-webkit-scrollbar { width: 6px; }
.sidebar-content::-webkit-scrollbar-track { background: transparent; }
.sidebar-content::-webkit-scrollbar-thumb { background: #444; border-radius: 3px; }
.sidebar-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  color: #666;
  font-size: 12px;
  text-align: center;
  line-height: 1.6;
}
.sidebar-resize-handle {
  position: absolute;
  right: 0; top: 0; bottom: 0;
  width: 3px;
  cursor: col-resize;
  background: transparent;
  z-index: 10;
  transition: background .15s;
}
.sidebar-resize-handle:hover { background: #569cd6; }

/* ===== 文件树 ===== */
.file-tree {
  background: transparent !important;
  font-size: 13px;
}
.file-tree :deep(.el-tree-node__content) {
  height: 22px;
}
.file-tree :deep(.el-tree-node__content:hover) { background: #2a2d2e; }
.file-tree :deep(.el-tree-node.is-current > .el-tree-node__content) { background: #04395e; }
.file-tree :deep(.el-tree-node__expand-icon) { color: #777; font-size: 13px; }
.file-tree :deep(.el-tree-node__expand-icon.is-leaf) { color: transparent; }
.tree-node { display: flex; align-items: center; gap: 4px; overflow: hidden; }
.tree-icon { font-size: 14px; flex-shrink: 0; }
.icon-folder { color: #e8c17a; }
.icon-file { color: #c5c5c5; }
.tree-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #ccc;
}
.tree-label.is-dirty { color: #e8c33e; }

/* ===== 编辑器中心 ===== */
.editor-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  position: relative;
}

/* ===== 文件标签 ===== */
.editor-tabs {
  display: flex;
  height: 40px;
  background: #2d2d2d;
  border-bottom: 1px solid #3c3c3c;
  overflow-x: auto;
  overflow-y: hidden;
  flex-shrink: 0;
  align-items: flex-start;
}
.editor-tabs::-webkit-scrollbar { height: 6px; }
.editor-tabs::-webkit-scrollbar-track { background: #1e1e1e; }
.editor-tabs::-webkit-scrollbar-thumb { background: #666; border-radius: 3px; }
.editor-tabs::-webkit-scrollbar-thumb:hover { background: #999; }
.editor-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 8px;
  min-width: 100px;
  max-width: 180px;
  height: 34px;
  cursor: pointer;
  border-right: 1px solid #3c3c3c;
  color: #888;
  font-size: 12px;
  flex-shrink: 0;
  user-select: none;
}
.editor-tab:hover { background: #1e1e1e; color: #ccc; }
.editor-tab.active { background: #1e1e1e; color: #d4d4d4; border-top: 1px solid #569cd6; }
.tab-icon { font-size: 13px; flex-shrink: 0; color: #888; }
.tab-name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.tab-dirty { color: #e8c33e; margin-left: 3px; }
.tab-close {
  font-size: 14px;
  opacity: 0;
  flex-shrink: 0;
  border-radius: 3px;
  padding: 2px;
  color: #888;
  transition: opacity .15s;
}
.editor-tab:hover .tab-close,
.editor-tab.active .tab-close { opacity: 1; }
.tab-close:hover { background: #555; color: #fff; }

/* ===== Monaco 容器 ===== */
.monaco-container {
  flex: 1;
  overflow: hidden;
  display: block;
}
.editor-welcome, .monaco-loading {
  position: absolute;
  top: 34px;
  left: 0; right: 0; bottom: 22px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  pointer-events: none;
  background: #1e1e1e;
}
.editor-welcome { pointer-events: auto; }

/* ===== 底部信息栏 ===== */
.editor-infobar {
  display: flex;
  align-items: center;
  height: 22px;
  background: #2d2d2d;
  border-top: 1px solid #3c3c3c;
  padding: 0 8px;
  flex-shrink: 0;
  gap: 0;
}
.sep { margin: 0 6px; color: #555; font-size: 11px; }

/* ===== 预览面板 ===== */
.editor-preview {
  position: relative;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-left: 1px solid #3c3c3c;
  min-width: 200px;
  max-width: 900px;
  flex-shrink: 0;
}
.preview-resize-handle {
  position: absolute;
  left: 0; top: 0; bottom: 0;
  width: 3px;
  cursor: col-resize;
  background: transparent;
  z-index: 10;
  transition: background .15s;
}
.preview-resize-handle:hover { background: #569cd6; }
.preview-toolbar {
  display: flex;
  align-items: center;
  height: 34px;
  background: #333;
  border-bottom: 1px solid #3c3c3c;
  padding: 0 8px;
  gap: 6px;
  flex-shrink: 0;
}
.preview-toolbar :deep(.el-input__wrapper) {
  background: #1e1e1e !important;
  box-shadow: 0 0 0 1px #555 !important;
}
.preview-toolbar :deep(.el-input__inner) { color: #ccc; font-size: 12px; }
.preview-iframe {
  flex: 1;
  border: none;
  width: 100%;
  background: #fff;
}

/* ===== 状态栏 ===== */
.editor-statusbar {
  display: flex;
  align-items: center;
  height: 22px;
  background: #007acc;
  color: rgba(255,255,255,0.9);
  font-size: 11px;
  flex-shrink: 0;
}
.status-item {
  display: flex;
  align-items: center;
  padding: 0 8px;
  height: 100%;
  white-space: nowrap;
}
.status-item:hover { background: rgba(255,255,255,0.15); cursor: default; }
.status-item.branch { background: rgba(0,0,0,0.2); cursor: pointer !important; }
.status-item.ds-ok { color: #a0f0a0; }
.status-item.ds-warn { color: #ffe066; }
.status-item.ds-err { color: #ff8080; }
.status-item.pv-running { background: #16825d; cursor: pointer; }

/* ===== 分支管理 Dialog ===== */
.branch-create-row { display: flex; gap: 8px; margin-bottom: 4px; }
.branch-create-input { flex: 1; }
.branch-sync-row { display: flex; align-items: center; margin-bottom: 4px; }
.branch-loading, .branch-empty { text-align: center; padding: 20px 0; color: #888; font-size: 13px; }
.branch-list { list-style: none; margin: 0; padding: 0; max-height: 360px; overflow-y: auto; }
.branch-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 8px;
  border-radius: 4px;
  font-size: 13px;
  transition: background .15s;
}
.branch-item:hover { background: var(--el-fill-color-light); }
.branch-name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.branch-item--current { font-weight: 600; }
.branch-actions { display: flex; gap: 4px; flex-shrink: 0; opacity: 0; transition: opacity .15s; }
.branch-item:hover .branch-actions { opacity: 1; }

/* ===== 日志控制台 ===== */
.log-console {
  height: 420px;
  overflow: auto;
  background: #1e1e1e;
  padding: 12px 14px;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: 12px;
  color: #d4d4d4;
  user-select: text;
}
.log-console pre { margin: 0; white-space: pre-wrap; word-break: break-all; line-height: 1.6; user-select: text; }
.log-console::-webkit-scrollbar { width: 6px; }
.log-console::-webkit-scrollbar-thumb { background: #444; border-radius: 3px; }

/* ===== 旋转动画 ===== */
.spin {
  animation: spin 1s linear infinite;
  display: inline-flex;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ===== Sidebar Tabs ===== */
.sidebar-tabs {
  display: flex;
  align-items: center;
  height: 32px;
  background: #252526;
  border-bottom: 1px solid #3c3c3c;
  padding: 0 4px;
  gap: 2px;
  flex-shrink: 0;
}
.sidebar-tab-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  font-size: 11px;
  color: #888;
  cursor: pointer;
  border-radius: 3px;
  white-space: nowrap;
  user-select: none;
  transition: background .1s, color .1s;
  position: relative;
}
.sidebar-tab-btn:hover { color: #ccc; background: #2d2d2d; }
.sidebar-tab-btn.active { color: #d4d4d4; }
.git-badge {
  background: #e8a03a;
  color: #1e1e1e;
  font-size: 10px;
  font-weight: 700;
  padding: 0 4px;
  border-radius: 10px;
  margin-left: 2px;
  min-width: 16px;
  text-align: center;
}

/* ===== Git 状态面板 ===== */
.git-status-panel { flex: 1; overflow: auto; display: flex; flex-direction: column; min-height: 0; }
.git-commit-area { padding: 8px; border-bottom: 1px solid #3c3c3c; flex-shrink: 0; }
.git-commit-input {
  width: 100%;
  background: #2d2d2d;
  border: 1px solid #3c3c3c;
  border-radius: 4px;
  color: #d4d4d4;
  font-size: 12px;
  padding: 6px 8px;
  resize: none;
  outline: none;
  font-family: inherit;
  box-sizing: border-box;
  margin-bottom: 6px;
}
.git-commit-input:focus { border-color: #569cd6; }
.git-commit-actions { display: flex; gap: 4px; }
.git-section { flex-shrink: 0; }
.git-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 8px;
  font-size: 11px;
  font-weight: 600;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  user-select: none;
}
.git-section-action { cursor: pointer; color: #888; font-size: 14px; padding: 2px; border-radius: 3px; }
.git-section-action:hover { color: #d4d4d4; background: #3c3c3c; }
.git-change-item {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 2px 8px 2px 16px;
  cursor: pointer;
  min-height: 22px;
  font-size: 12px;
}
.git-change-item:hover { background: #2a2d2e; }
.git-change-item:hover .git-item-action { opacity: 1; }
.git-status-badge {
  font-size: 11px;
  font-weight: 700;
  width: 14px;
  flex-shrink: 0;
  text-align: center;
}
.git-change-path { color: #ccc; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex-shrink: 0; max-width: 110px; }
.git-change-dir { color: #666; font-size: 11px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; }
.git-item-action { opacity: 0; cursor: pointer; color: #888; font-size: 14px; padding: 2px; border-radius: 3px; flex-shrink: 0; transition: opacity .1s; }
.git-item-action:hover { color: #d4d4d4; background: #3c3c3c; }
.git-item-action--always { opacity: 1 !important; }
.git-pending-push {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 10px; background: #1a3a5c; font-size: 12px; color: #89c4f4;
  border-bottom: 1px solid #2a4a7c;
}
.git-pending-push--force {
  background: #3a1a1a; color: #f97583; border-bottom-color: #6a2a2a;
}
.git-pending-push span { flex: 1; }
.git-merging-banner {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 10px; background: #3a2a00; font-size: 12px; color: #e6a23c;
  border-bottom: 1px solid #5a4010; cursor: default;
}
.git-commit-dropdown-btn { padding: 0 6px !important; border-left: 1px solid rgba(255,255,255,0.2) !important; }
.gs-modified { color: #e2c08d; }
.gs-added { color: #73c991; }
.gs-deleted { color: #f14c4c; }
.gs-untracked { color: #73c991; }
.gs-renamed { color: #3dc9b0; }
.gs-conflict { color: #f14c4c; }
.gs-other { color: #888; }
.git-conflict-item { background: rgba(241,76,76,0.06); }
.git-conflict-item:hover { background: rgba(241,76,76,0.14) !important; }
.git-section-header .el-icon { display: flex; align-items: center; }

/* ===== 冲突解决器浮层工具栏 ===== */
.monaco-editor-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}
.monaco-editor-wrap .monaco-container {
  flex: 1;
  min-height: 0;
}
.cr-float-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 10px;
  height: 38px;
  flex-shrink: 0;
  background: #1e1e2e;
  border-bottom: 1px solid rgba(241, 76, 76, 0.5);
  box-shadow: 0 2px 8px rgba(0,0,0,.4);
}
.cr-float-filename {
  font-size: 13px;
  font-weight: 600;
  color: #f14c4c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}
.cr-float-pos { font-size: 12px; color: #888; white-space: nowrap; }
.cr-float-done { font-size: 12px; color: #67c23a; white-space: nowrap; }
.cr-float-sep {
  width: 1px;
  height: 18px;
  background: #444;
  flex-shrink: 0;
  margin: 0 2px;
}
/* Monaco 行高亮装饰 */
.cr-line-active { background: rgba(241, 76, 76, 0.12) !important; }
.cr-line-other  { background: rgba(241, 76, 76, 0.04) !important; }

/* ===== 右键浮动菜单 ===== */
.ctx-menu {
  position: fixed;
  z-index: 9999;
  background: #2d2d2d;
  border: 1px solid #454545;
  border-radius: 5px;
  padding: 4px 0;
  min-width: 160px;
  box-shadow: 2px 4px 12px rgba(0,0,0,.6);
  font-size: 13px;
  color: #ccc;
  user-select: none;
}
.ctx-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  cursor: pointer;
}
.ctx-item:hover { background: #094771; color: #fff; }
.ctx-danger { color: #f47067; }
.ctx-danger:hover { background: #6b1a1a; color: #ff8a8a; }
.ctx-divider { height: 1px; background: #3c3c3c; margin: 3px 0; }

/* ===== Git Log 列表 ===== */
/* ===== 提交历史弹窗 ===== */
/* git-log-dialog dark theme → 见文件末非 scoped 样式块 */
.git-log-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-bottom: 1px solid #3a3a3a;
  background: #252526;
  gap: 10px;
}
.git-log-toolbar-left { display: flex; align-items: center; gap: 8px; }
.gl-label { color: #999; font-size: 12px; white-space: nowrap; }
.gl-sync-btn { display: flex; align-items: center; gap: 4px; font-size: 12px !important; padding: 4px 10px !important; }
.gl-count { color: #666; font-size: 11px; white-space: nowrap; flex-shrink: 0; }
.gl-count b { color: #aaa; }
.gl-loading { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 32px; color: #888; font-size: 13px; }
.gl-empty { text-align: center; padding: 32px; color: #666; font-size: 13px; }
/* 列表 */
.git-log-list { max-height: 460px; overflow-y: auto; overflow-x: auto; font-family: 'JetBrains Mono', Consolas, monospace; font-size: 12px; padding: 4px 0; }
.git-log-item {
  display: flex;
  align-items: flex-start;
  padding: 0 14px 0 0;
  position: relative;
  transition: background .1s;
  min-width: max-content;
}
.git-log-item:hover { background: #2a2c2e; }
.git-log-item:hover .git-log-actions { opacity: 1; }
/* 分支图列 */
.gl-graph {
  flex-shrink: 0;
  height: 36px;
  position: relative;
  overflow: visible;
}
/* 内容区 */
.gl-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 7px 8px 8px 0;
  border-bottom: 1px solid #2a2a2a;
}
.git-log-item:last-child .gl-body { border-bottom: none; }
/* 第一行 */
.gl-row1 { display: flex; align-items: center; gap: 6px; min-width: 0; }
.git-log-hash {
  color: #569cd6;
  width: 58px;
  flex-shrink: 0;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: 11.5px;
  letter-spacing: .5px;
}
.git-log-refs { display: flex; gap: 3px; flex-shrink: 0; flex-wrap: wrap; align-items: center; }
.git-log-ref-chip {
  display: inline-flex;
  align-items: center;
  padding: 0 6px;
  border-radius: 3px;
  font-size: 10px;
  height: 16px;
  white-space: nowrap;
  font-weight: 600;
  letter-spacing: .2px;
}
.ref-head     { background: #3a286a; color: #c586ff; border: 1px solid #7a4aaa; }
.ref-local    { background: #1a3a5a; color: #4ec9b0; border: 1px solid #2a6a8a; }
.ref-remote   { background: #3a2a1a; color: #ce9178; border: 1px solid #7a5a3a; }
.ref-tag      { background: #2a3a1a; color: #b5cea8; border: 1px solid #4a7a2a; }
.ref-detached { background: #3a1a1a; color: #f44747; border: 1px solid #7a2a2a; }
.git-log-msg { flex: 1; color: #e0e0e0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 13px; font-family: -apple-system, 'Segoe UI', sans-serif; }
/* 第二行 */
.gl-row2 {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #777;
  font-size: 11px;
  font-family: -apple-system, 'Segoe UI', sans-serif;
}
.gl-row2 .el-icon { font-size: 11px; flex-shrink: 0; }
.gl-sep { margin: 0 4px; color: #444; }
/* 操作按钮 */
.git-log-actions {
  display: flex;
  gap: 3px;
  flex-shrink: 0;
  opacity: 0;
  transition: opacity .15s;
  align-self: center;
  padding: 4px 0;
}

/* ===== Diff 高亮 ===== */
/* ===== Monaco Diff 内联对比编辑器 ===== */
.monaco-diff-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}
.diff-editor-bar {
  height: 34px;
  background: #252526;
  border-bottom: 1px solid #3c3c3c;
  display: flex;
  align-items: center;
  padding: 0 12px;
  gap: 8px;
  flex-shrink: 0;
}
.diff-editor-canvas {
  flex: 1;
  overflow: hidden;
  display: block;
}
.diff-mode-tab {
  border-top-color: #9cdcfe !important;
  background: #1a1f2e !important;
}
.diff-mode-tab .tab-name { color: #9cdcfe; }
.diff-viewer-wrap { max-height: 560px; overflow-y: auto; background: #1e1e1e; border-radius: 6px; font-family: 'JetBrains Mono', Consolas, monospace; font-size: 12px; }
.diff-file-block { margin-bottom: 2px; }
.diff-file-header { background: #252526; color: #9cdcfe; padding: 6px 12px; font-size: 12px; font-weight: 600; border-left: 3px solid #007acc; user-select: text; }
.diff-file-old { color: #f48771; }
.diff-file-new { color: #89d185; }
.diff-hunk-header { background: #1a2332; color: #6a9fd8; padding: 2px 10px; font-size: 11px; user-select: text; }
.diff-table { width: 100%; border-collapse: collapse; }
.diff-lno { width: 42px; min-width: 42px; text-align: right; padding: 0 8px; color: #858585; user-select: none; border-right: 1px solid #333; font-size: 11px; line-height: 1.6; white-space: nowrap; }
.diff-sign { width: 14px; text-align: center; font-weight: bold; padding: 0 2px; user-select: none; }
.diff-code { padding: 0 8px; white-space: pre; color: #d4d4d4; line-height: 1.6; user-select: text; }
.diff-line.diff-add { background: #1a2f1a; }
.diff-line.diff-add .diff-lno { background: #153015; }
.diff-line.diff-add .diff-sign { color: #89d185; }
.diff-line.diff-add .diff-code { color: #d4d4d4; }
.diff-line.diff-del { background: #2f1a1a; }
.diff-line.diff-del .diff-lno { background: #301515; }
.diff-line.diff-del .diff-sign { color: #f48771; }
.diff-line.diff-del .diff-code { color: #d4d4d4; }
.diff-line.diff-ctx .diff-sign { color: transparent; }

/* ===== 配置导航面板（侧边栏）===== */
.config-nav-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
.cfgnav-group { padding: 4px 0; }
.cfgnav-group-title {
  font-size: 10px;
  color: #888;
  text-transform: uppercase;
  padding: 4px 10px 2px;
  letter-spacing: 0.05em;
}
.cfgnav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 4px;
  margin: 1px 4px;
  font-size: 12px;
  color: #ccc;
  transition: background 0.15s;
}
.cfgnav-item:hover { background: #2a2d2e; }
.cfgnav-item.active { background: #094771; color: #fff; }
.cfgnav-icon { font-size: 12px; color: #888; flex-shrink: 0; }
.cfgnav-label { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.cfgnav-dirty { color: #e5a50a; font-size: 10px; flex-shrink: 0; }

/* ===== 配置编辑器表单区域（中间）===== */
.cfg-form-panel {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
  background: #1e1e1e;
  min-height: 0;
}
.cfg-warning-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #332200;
  border-bottom: 1px solid #4d3300;
  font-size: 12px;
  color: #e5a50a;
  flex-shrink: 0;
}
.cfg-warning-bar code {
  background: #4d3a00;
  padding: 1px 4px;
  border-radius: 3px;
  font-family: monospace;
}
.cfg-form-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
.cfg-group { margin-bottom: 20px; }
.cfg-group-title {
  font-size: 12px;
  font-weight: 600;
  color: #9cdcfe;
  border-bottom: 1px solid #333;
  padding-bottom: 6px;
  margin-bottom: 12px;
}
.cfg-field-wrap {
  margin-bottom: 14px;
  padding: 10px 12px;
  border-radius: 6px;
  background: #252526;
  border: 1px solid transparent;
  transition: border-color 0.15s;
}
.cfg-field-wrap.is-dirty { border-color: #e5a50a66; background: #2a2500; }
.cfg-field-wrap.is-active { border-color: #569cd6; }
.cfg-field-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #d4d4d4;
  margin-bottom: 6px;
  font-weight: 500;
}
.cfg-required { color: #f48771; font-size: 12px; }
.cfg-input { width: 100%; }
.cfg-field-desc {
  margin-top: 5px;
  font-size: 11px;
  color: #666;
  line-height: 1.4;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.cfg-field-path { color: #555; font-family: monospace; font-size: 10px; }

/* Element Plus dark overrides for cfg fields */
.cfg-form-panel .el-input__wrapper { background: #1a1a1a !important; box-shadow: none !important; border: 1px solid #3c3c3c !important; }
.cfg-form-panel .el-input__wrapper:hover { border-color: #569cd6 !important; }
.cfg-form-panel .el-input__inner { color: #d4d4d4 !important; }
.cfg-form-panel .el-textarea__inner { background: #1a1a1a !important; color: #d4d4d4 !important; border: 1px solid #3c3c3c !important; box-shadow: none !important; }
.cfg-form-panel .el-textarea__inner:hover { border-color: #569cd6 !important; }
</style>

<style>
/* 提交历史弹窗 暗色主题：非 scoped 样式块，teleport 到 body 后 scoped hash 不匹配，必须全局覆盖 */
/* ── 用 CSS 变量从根节点整体覆盖 Element Plus 默认亮色 ── */
/* Element Plus 会把自定义 class 直接加到 .el-dialog 元素上，需要同元素双类选择器 */
.git-log-dialog.el-dialog,
.git-log-dialog .el-dialog {
  --el-bg-color: #1e1e1e;
  --el-bg-color-overlay: #1e1e1e;
  --el-border-color: #3a3a3a;
  --el-border-color-light: #3a3a3a;
  --el-border-color-lighter: #333;
  --el-text-color-primary: #d4d4d4;
  --el-text-color-regular: #bbb;
  --el-text-color-secondary: #888;
  --el-dialog-bg-color: #1e1e1e;
  background: #1e1e1e;
  border: 1px solid #3a3a3a;
  box-shadow: 0 8px 40px rgba(0,0,0,.8);
  /* 防止 border-radius 裁切后露出白色 */
  overflow: hidden;
}
.git-log-dialog .el-dialog__header {
  background: #1e1e1e;
  border-bottom: 1px solid #333;
  padding: 14px 20px 12px;
  margin-right: 0;
}
.git-log-dialog .el-dialog__title { color: #d4d4d4; font-size: 14px; font-weight: 500; }
.git-log-dialog .el-dialog__headerbtn .el-dialog__close { color: #777; }
.git-log-dialog .el-dialog__headerbtn:hover .el-dialog__close { color: #ccc; }
.git-log-dialog .el-dialog__body { padding: 0; background: #1e1e1e; color: #d4d4d4; }
.git-log-dialog .el-dialog__footer { background: #1e1e1e; border-top: 1px solid #333; padding: 10px 20px; }
/* 关闭按钮 */
.git-log-dialog .el-dialog__footer .el-button {
  --el-button-bg-color: #2d2d2d;
  --el-button-border-color: #4a4a4a;
  --el-button-text-color: #ccc;
  --el-button-hover-bg-color: #3a3a3a;
  --el-button-hover-border-color: #666;
  --el-button-hover-text-color: #fff;
}
/* 工具栏 el-select 输入框 */
.git-log-dialog .el-input__wrapper {
  --el-input-bg-color: #2a2a2a;
  background: #2a2a2a;
  box-shadow: 0 0 0 1px #4a4a4a inset;
}
.git-log-dialog .el-input__wrapper:hover { box-shadow: 0 0 0 1px #666 inset; }
.git-log-dialog .el-input__wrapper.is-focus { box-shadow: 0 0 0 1px #4a9eff inset !important; }
.git-log-dialog .el-input__inner { color: #ccc; background: transparent; }
.git-log-dialog .el-input__inner::placeholder { color: #555; }
.git-log-dialog .el-input__suffix .el-icon,
.git-log-dialog .el-input__prefix .el-icon { color: #666; }
/* 工具栏同步按钮 */
.git-log-dialog .gl-sync-btn {
  --el-button-bg-color: #2a2a2a;
  --el-button-border-color: #4a4a4a;
  --el-button-text-color: #bbb;
  --el-button-hover-bg-color: #383838;
  --el-button-hover-border-color: #666;
  --el-button-hover-text-color: #fff;
}
/* select 下拉面板（popper-class="git-log-branch-select"） */
.el-select-dropdown.git-log-branch-select {
  --el-bg-color-overlay: #252526;
  --el-border-color-light: #3a3a3a;
  background: #252526;
  border-color: #3a3a3a;
}
.el-select-dropdown.git-log-branch-select .el-select-dropdown__item { color: #ccc; }
.el-select-dropdown.git-log-branch-select .el-select-dropdown__item.hover,
.el-select-dropdown.git-log-branch-select .el-select-dropdown__item:hover { background: #094771; color: #fff; }
.el-select-dropdown.git-log-branch-select .el-select-dropdown__item.selected { color: #4a9eff; }

/* 上传拖拽区 */
.upload-drop-zone {
  min-height: 140px;
  border: 2px dashed #3a3a3a;
  border-radius: 6px;
  padding: 16px;
  text-align: center;
  transition: border-color 0.2s, background 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: default;
}
.upload-drop-zone.drop-active {
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.06);
}
.upload-drop-zone.is-reading {
  border-color: #e6a23c;
}

/* Cloudflare Workers 部署 */
.worker-domain-section {
  padding: 0 0 4px;
}
</style>
