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
        <el-col :xl="4" :lg="8" :md="12" :sm="24">
          <el-form-item label="游戏名称" prop="name">
            <el-input 
              v-model="queryParams.name" 
              placeholder="请输入游戏名称" 
              clearable 
              @keyup.enter="handleQuery"
              prefix-icon="Search"
            />
          </el-form-item>
        </el-col>
        <el-col :xl="4" :lg="8" :md="12" :sm="24">
          <el-form-item label="副标题" prop="subtitle">
            <el-input
              v-model="queryParams.subtitle"
              placeholder="请输入副标题"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :xl="3" :lg="8" :md="12" :sm="24">
          <el-form-item label="游戏类型" prop="gameType">
            <el-select v-model="queryParams.gameType" placeholder="全部类型" clearable>
              <el-option label="未赋值" value="__EMPTY__" />
              <el-option
                v-for="item in gameTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :xl="4" :lg="8" :md="12" :sm="24">
          <el-form-item label="备注" prop="remark">
            <el-input 
              v-model="queryParams.remark" 
              placeholder="可搜索冲突标记" 
              clearable 
              @keyup.enter="handleQuery"
              prefix-icon="Edit"
            />
          </el-form-item>
        </el-col>
        <el-col :xl="5" :lg="8" :md="12" :sm="24">
          <el-form-item label="分类" prop="categoryIds">
            <div style="display: flex; align-items: center; gap: 8px; width: 100%;">
              <el-tree-select
                v-model="queryParams.categoryIds"
                :data="gameCategoryQueryTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children', disabled: 'disabled' }"
                value-key="id"
                placeholder="全部分类（含子类）"
                multiple
                show-checkbox
                collapse-tags
                collapse-tags-tooltip
                check-strictly
                :render-after-expand="false"
                clearable
                filterable
                :disabled="queryParams.uncategorizedOnly"
                style="flex: 1; min-width: 0;"
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
              <el-checkbox
                v-model="queryParams.uncategorizedOnly"
                @change="handleUncategorizedChange"
                style="white-space: nowrap; flex-shrink: 0;"
              >仅无分类</el-checkbox>
            </div>
          </el-form-item>
        </el-col>
        <el-col :xl="4" :lg="8" :md="12" :sm="24">
          <el-form-item label="所属盒子" prop="boxIds">
            <el-select v-model="queryParams.boxIds" placeholder="不选则不过滤" clearable filterable multiple collapse-tags collapse-tags-tooltip>
              <el-option v-for="box in allBoxes" :key="box.id" :label="box.name" :value="box.id">
                <div style="display: flex; align-items: center; gap: 8px;">
                  <img 
                    v-if="box.logoUrl" 
                    :src="box.logoUrl" 
                    :alt="box.name" 
                    style="width: 24px; height: 24px; border-radius: 4px; object-fit: cover;"
                  />
                  <span>{{ box.name }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="10" style="margin-top: 2px;">
        <el-col :xl="4" :lg="8" :md="12" :sm="24">
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
        <el-col :xl="6" :lg="12" :md="12" :sm="24">
          <el-form-item label="更新时间" prop="updateTimeRange">
            <el-date-picker
              v-model="queryParams.updateTimeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              clearable
              @change="handleUpdateTimeChange"
            />
          </el-form-item>
        </el-col>
        <el-col :xl="14" :lg="24" :md="24" :sm="24">
          <el-form-item label=" " style="margin-left: 0; display: flex; justify-content: flex-end;">
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="viewMode === 'creator'" :gutter="10" style="margin-top: 8px;">
        <el-col :span="24">
          <el-form-item label=" ">
            <el-button type="success" plain icon="Download" @click="handleFullExport" v-hasPermi="['gamebox:game:export']">全站导出</el-button>
            <el-button type="warning" plain icon="Upload" @click="handleFullImport" v-hasPermi="['gamebox:game:import']">全站导入</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['gamebox:game:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['gamebox:game:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="effectiveMultiple" @click="handleDelete" v-hasPermi="['gamebox:game:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="MagicStick" :disabled="effectiveMultiple" @click="handleBatchTranslate" v-hasPermi="['gamebox:translation:edit']">批量翻译</el-button>
      </el-col>
      <el-col v-if="isPersonalSiteCheck(queryParams.siteId)" :span="1.5">
        <el-button type="danger" plain icon="CircleClose" :disabled="effectiveMultiple" @click="handleBatchExclude" v-hasPermi="['gamebox:game:edit']">批量排除管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Link" :disabled="effectiveMultiple" @click="handleBatchRelation" v-hasPermi="['gamebox:game:edit']">批量关联管理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleImport" v-hasPermi="['gamebox:game:add']">盒子游戏导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Download" :disabled="effectiveMultiple" @click="handleExport" v-hasPermi="['gamebox:game:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleSystemImport" v-hasPermi="['gamebox:game:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Tickets" @click="openImportHistory" v-hasPermi="['gamebox:importBatch:list']">数据修改历史</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 跨页全选提示栏 -->
    <el-alert
      v-if="!crossPageSelectActive && ids.length > 0 && total > queryParams.pageSize"
      type="info"
      :closable="false"
      style="margin-bottom: 8px;"
    >
      <template #default>
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <span>已选择当前页 <strong>{{ ids.length }}</strong> 条。共 <strong>{{ total }}</strong> 条筛选结果。</span>
          <el-button
            type="primary"
            size="small"
            :loading="crossPageLoading"
            @click="handleSelectAllFiltered"
            style="margin-left: 16px;"
          >全选全部 {{ total }} 条筛选结果</el-button>
        </div>
      </template>
    </el-alert>
    <el-alert
      v-if="crossPageSelectActive"
      type="warning"
      :closable="false"
      style="margin-bottom: 8px;"
    >
      <template #default>
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <span>
            <el-icon style="margin-right: 4px; color: #e6a23c;"><WarningFilled /></el-icon>
            已跨页全选 <strong>{{ crossPageIds.length }}</strong> 条筛选结果，以下批量操作将作用于全部选中数据
          </span>
          <el-button size="small" @click="clearCrossPageSelect" style="margin-left: 16px;">取消全选</el-button>
        </div>
      </template>
    </el-alert>

    <el-table ref="gameTableRef" v-loading="loading" :data="gameList" row-key="id" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" fixed />
      <el-table-column label="ID" align="center" prop="id" width="70" fixed />
      <el-table-column label="游戏图标" align="center" prop="iconUrl" width="90">
        <template #default="scope">
          <div style="width: 50px; height: 50px; border-radius: 8px; overflow: hidden; margin: 0 auto; background: #f5f7fa; display: flex; align-items: center; justify-content: center;">
            <img
              v-if="scope.row.iconUrl && !iconErrorIds.has(scope.row.id)"
              :src="scope.row.iconUrl"
              loading="lazy"
              style="width: 100%; height: 100%; object-fit: cover; display: block;"
              @error="iconErrorIds.add(scope.row.id)"
            />
            <el-icon v-else style="font-size: 24px; color: #c0c4cc;"><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="游戏名称" align="left" prop="name" min-width="150" :show-overflow-tooltip="true">
        <template #default="scope">
          <div style="display: flex; align-items: center;">
            <span style="font-weight: 500;">{{ scope.row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="副标题/版本说明" align="left" prop="subtitle" min-width="130" :show-overflow-tooltip="true">
        <template #default="scope">
          <span v-if="scope.row.subtitle" style="font-size: 12px; color: #606266;">{{ scope.row.subtitle }}</span>
          <span v-else style="color: #c0c4cc;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="游戏类型" align="center" prop="gameType" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.gameType === 'official'" type="primary" size="small">官服</el-tag>
          <el-tag v-else-if="scope.row.gameType === 'discount'" type="success" size="small">折扣</el-tag>
          <el-tag v-else-if="scope.row.gameType === 'bt'" type="warning" size="small">BT</el-tag>
          <el-tag v-else-if="['h5', 'H5'].includes(scope.row.gameType)" size="small">H5</el-tag>
          <el-tag v-else-if="scope.row.gameType === 'coming'" type="info" size="small">即将上线</el-tag>
          <el-tag v-else-if="scope.row.gameType" type="info" size="small">{{ scope.row.gameType }}</el-tag>
          <span v-else style="color: #c0c4cc;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建者网站" align="center" prop="siteId" width="150">
        <template #default="scope">
          <el-tag type="success" effect="plain">
            <el-icon style="margin-right: 4px;"><User /></el-icon>
            {{ getSiteNameFast(scope.row.siteId) }}
          </el-tag>
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
      <el-table-column label="分类" align="center" prop="categoryId" width="150">
        <template #default="scope">
          <div v-if="scope.row.categories && scope.row.categories.length > 0" style="display: flex; flex-wrap: wrap; gap: 4px; justify-content: center;">
            <CategoryTag 
              v-for="cat in scope.row.categories" 
              :key="cat.categoryId"
              :category="{ id: cat.categoryId, name: cat.categoryName, categoryType: 'game', icon: cat.categoryIcon }" 
              size="small"
            >
              <template v-if="cat.isPrimary === '1'">
                <el-icon style="margin-left: 2px;"><StarFilled /></el-icon>
              </template>
            </CategoryTag>
          </div>
          <div v-else>
            <CategoryTag v-if="scope.row.categoryName" :category="{ id: scope.row.categoryId, name: scope.row.categoryName, categoryType: 'game', icon: scope.row.categoryIcon }" size="small" />
            <CategoryTag v-else-if="getCategoryFromCache(scope.row.categoryId)" :category="{ id: scope.row.categoryId, name: getCategoryFromCache(scope.row.categoryId).name, categoryType: 'game', icon: getCategoryFromCache(scope.row.categoryId).icon }" size="small" />
            <el-tag v-else-if="scope.row.categoryId" type="warning" size="small">ID: {{ scope.row.categoryId }}</el-tag>
            <el-tag v-else type="info" size="small">未分类</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="游戏厂商" align="center" prop="developer" width="120" />
      <el-table-column label="所属盒子" align="center" prop="boxCount" width="220">
        <template #default="scope">
          <div style="display: flex; align-items: center; justify-content: center; gap: 8px;">
            <el-tooltip
              v-if="getGameRelatedBoxes(scope.row).length > 0"
              placement="top"
              :show-after="200"
            >
              <template #content>
                <div style="max-width: 360px; display: flex; flex-wrap: wrap; gap: 8px;">
                  <div
                    v-for="box in getGameRelatedBoxes(scope.row)"
                    :key="`full-${scope.row.id}-${box.id}`"
                    style="display: inline-flex; align-items: center; gap: 6px; padding: 4px 6px; border-radius: 6px; background: rgba(255,255,255,0.08);"
                  >
                    <img
                      v-if="box.logoUrl"
                      :src="box.logoUrl"
                      :alt="box.name"
                      style="width: 20px; height: 20px; border-radius: 4px; object-fit: cover;"
                    />
                    <el-icon v-else style="font-size: 16px; color: #c0c4cc;"><Picture /></el-icon>
                    <span>{{ box.name }}</span>
                  </div>
                </div>
              </template>
              <div style="display: flex; align-items: center; justify-content: center; min-width: 120px;">
                <el-avatar
                  v-for="(box, index) in getGameRelatedBoxes(scope.row).slice(0, 3)"
                  :key="`${scope.row.id}-${box.id}`"
                  shape="square"
                  :size="24"
                  :src="box.logoUrl || undefined"
                  style="margin-left: -6px; border: 1px solid #fff; background: #f5f7fa;"
                >
                  <el-icon v-if="!box.logoUrl"><Picture /></el-icon>
                </el-avatar>
                <span
                  v-if="getGameRelatedBoxes(scope.row).length > 3"
                  style="margin-left: 6px; font-size: 12px; color: #606266;"
                >+{{ getGameRelatedBoxes(scope.row).length - 3 }}</span>
              </div>
            </el-tooltip>
            <span v-else style="color: #c0c4cc;">-</span>
            <el-tag type="info" size="small" effect="plain">
              {{ scope.row.boxCount || 0 }} 个
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="评分" align="center" prop="rating" width="80" />
      <el-table-column label="下载量" align="center" prop="downloadCount" width="100">
        <template #default="scope">
          <span style="color: #409eff;">↓ {{ scope.row.downloadCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="推荐" align="center" prop="isRecommend" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isRecommend === '1' ? 'success' : 'info'" size="small" effect="plain">
            {{ scope.row.isRecommend === '1' ? '★ 是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" width="200" :show-overflow-tooltip="true">
        <template #default="scope">
          <el-tag v-if="scope.row.remark && scope.row.remark.includes('导入冲突')" type="warning" size="small" effect="plain">冲突</el-tag>
          <span v-else style="font-size: 12px; color: #909399;">{{ scope.row.remark || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80" v-if="viewMode !== 'related'">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" effect="plain">
            {{ scope.row.status === '1' ? '✔ 启用' : '✖ 禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="155" :formatter="(row) => dayjs(row.updateTime).format('YYYY-MM-DD HH:mm')" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220" fixed="right">
        <template #default="scope">
          <!-- 关联模式 -->
          <template v-if="viewMode === 'related'">
            <template v-if="scope.row.relationSource === 'own'">
              <el-button link type="primary" icon="Edit" title="修改" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:game:edit']" />
              <el-button link type="success" icon="View" title="主页预览" @click="handlePreviewHomepages(scope.row)" />
              <el-button link type="warning" icon="Box" title="盒子管理" @click="handleManageBoxes(scope.row)" v-hasPermi="['gamebox:game:edit']" />
              <el-button link type="warning" icon="Link" title="网站关联" @click="handleManageSites(scope.row)" v-hasPermi="['gamebox:game:edit']" />
              <el-button link type="danger" icon="Delete" title="删除" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:game:remove']" />
              <el-button link type="info" icon="Tickets" title="变更历史" @click="handleShowGameHistory(scope.row)" />
            </template>
            <template v-else-if="scope.row.relationSource === 'default'">
              <el-button link type="success" icon="View" title="主页预览" @click="handlePreviewHomepages(scope.row)" />
              <el-button link type="warning" icon="Box" title="盒子管理" @click="handleManageBoxes(scope.row)" v-hasPermi="['gamebox:game:edit']" />
              <el-tag v-if="scope.row.isExcluded" type="info" size="small">已排除</el-tag>
            </template>
            <template v-else-if="scope.row.relationSource === 'shared'">
              <el-button link type="success" icon="View" title="主页预览" @click="handlePreviewHomepages(scope.row)" />
              <el-button link type="warning" icon="Box" title="盒子管理" @click="handleManageBoxes(scope.row)" v-hasPermi="['gamebox:game:edit']" />
            </template>
          </template>
          <!-- 创建者模式 -->
          <template v-else>
            <el-button link type="primary" icon="Edit" title="修改" @click="handleUpdate(scope.row)" v-hasPermi="['gamebox:game:edit']" />
            <el-button link type="success" icon="View" title="主页预览" @click="handlePreviewHomepages(scope.row)" />
            <el-button link type="warning" icon="Box" title="盒子管理" @click="handleManageBoxes(scope.row)" v-hasPermi="['gamebox:game:edit']" />
            <el-button link type="warning" icon="Document" title="翻译管理" @click="handleManageTranslations(scope.row)" v-hasPermi="['gamebox:game:edit']" />
            <el-button v-if="!isPersonalSiteCheck(scope.row.siteId)" link type="warning" icon="Link" title="网站关联" @click="handleManageSites(scope.row)" v-hasPermi="['gamebox:game:edit']" />
            <el-button v-if="isPersonalSiteCheck(scope.row.siteId)" link type="danger" icon="CircleClose" title="排除管理" @click="handleManageSites(scope.row)" v-hasPermi="['gamebox:game:edit']" />
            <el-button link type="danger" icon="Delete" title="删除" @click="handleDelete(scope.row)" v-hasPermi="['gamebox:game:remove']" />
            <el-button link type="info" icon="Tickets" title="变更历史" @click="handleShowGameHistory(scope.row)" />
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 网站关联管理对话框 -->
    <SiteRelationManager
      v-model="siteRelationDialogOpen"
      entity-type="game"
      :entity-id="currentGameIdForSites"
      :entity-name="currentGameNameForSites"
      :creator-site-id="currentGameCreatorSiteId"
      @refresh="getList"
    />

    <!-- 盒子关联管理对话框 -->
    <BoxRelationManager
      v-model="boxRelationDialogOpen"
      :game-id="currentGameIdForBoxes"
      :game-name="currentGameNameForBoxes"
      :readonly="boxRelationReadonly"
      :site-id="boxRelationSiteId"
      @refresh="getList"
    />

    <!-- 翻译管理对话框 -->
    <TranslationManager
      v-model="translationDialogOpen"
      entity-type="game"
      :entity-id="currentTranslationGameId"
      :entity-name="currentTranslationGameName"
      :site-id="queryParams.siteId || 0"
      :translation-fields="gameTranslationFields"
      :original-data="currentTranslationGameData"
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
            该游戏暂未绑定主页文章
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
    <el-dialog :title="`主页绑定 - ${currentGameDisplayName}`" v-model="bindHomepageDialogOpen" width="600px" append-to-body :close-on-click-modal="false">
      <!-- 提示信息 -->
      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      >
        <template #title>
          <div style="font-size: 14px; line-height: 1.6;">
            为该游戏选择一个主文章作为主页<br/>
            <span style="color: #909399; font-size: 12px;">所有语言版本将统一使用该主文章的对应语言内容</span>
          </div>
        </template>
      </el-alert>

      <!-- 游戏信息卡片 -->
      <el-card shadow="never" style="margin-bottom: 20px; background-color: #f5f7fa;">
        <div style="display: flex; align-items: center; gap: 12px;">
          <el-icon :size="18" color="#409eff"><Trophy /></el-icon>
          <div style="flex: 1;">
            <div style="font-weight: 500; color: #303133;">{{ (currentGameForBind && currentGameForBind.name) || '未知游戏' }}</div>
            <div style="font-size: 12px; color: #909399; margin-top: 4px;">游戏 ID: {{ (currentGameForBind && currentGameForBind.id) || '' }}</div>
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

    <!-- 导入游戏对话框 -->
    <el-dialog title="多平台游戏导入" v-model="importOpen" width="800px" append-to-body>
      <el-alert
        title="导入说明"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 15px;"
      >
        <template #default>
          <div style="line-height: 1.8;">
            <p style="margin: 0 0 8px 0; font-weight: 500;">📋 导入流程：</p>
            <div style="padding-left: 16px; margin-bottom: 12px;">
              <p style="margin: 5px 0;">1. 选择数据来源盒子和网站</p>
              <p style="margin: 5px 0;">2. 粘贴平台原始JSON数据</p>
              <p style="margin: 5px 0;">3. 后端自动应用字段映射配置完成导入</p>
            </div>
            <p style="margin: 8px 0 0 0; font-size: 12px; color: #606266;">
              <el-icon style="color: #67c23a;"><InfoFilled /></el-icon>
              <strong>快速配置：</strong>在【盒子管理 → 字段映射】中可直接上传样本数据（Excel/JSON）快速生成字段映射
            </p>
            <p style="margin: 8px 0 0 0; font-size: 12px; color: #909399;">
              💡 支持嵌套字段路径（如 photo.0.url）、值映射、类型转换等高级功能
            </p>
          </div>
        </template>
      </el-alert>
      <el-form :model="importForm" ref="importFormRef" label-width="100px">
        <el-form-item label="创建者网站" prop="siteId" :rules="[{ required: true, message: '请选择创建者网站', trigger: 'change' }]">
          <el-select v-model="importForm.siteId" placeholder="请选择创建者网站" style="width: 100%" filterable @change="handleImportSiteChange">
            <el-option
              v-for="site in siteList"
              :key="site.id"
              :label="site.displayLabel"
              :value="site.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="importForm.siteId" label="游戏盒子" prop="boxId" :rules="[{ required: true, message: '请选择游戏盒子', trigger: 'change' }]">
          <el-select v-model="importForm.boxId" placeholder="请选择游戏盒子" style="width: 100%" @change="handleBoxChange">
            <el-option 
              v-for="box in importBoxList" 
              :key="box.id" 
              :label="box.name" 
              :value="box.id"
            >
              <span>{{ box.name }}</span>
            </el-option>
          </el-select>
          <div style="margin-top: 8px;">
            <el-link type="primary" @click="showFieldMapping = !showFieldMapping" :underline="false" style="font-size: 13px;">
              <el-icon><Document /></el-icon>
              {{ showFieldMapping ? '收起' : '查看' }}字段映射说明 ({{ currentPlatformMappings.length }} 个字段)
            </el-link>
          </div>
          <el-collapse-transition>
            <div v-show="showFieldMapping" style="margin-top: 12px; border: 1px solid #ebeef5; border-radius: 4px; padding: 12px; background: #f9fafc;">
              <div style="margin-bottom: 8px; color: #606266; font-weight: 500;">
                <el-icon style="color: #409eff;"><InfoFilled /></el-icon>
                {{ importBoxList.find(b => b.id === importForm.boxId)?.name || '盒子' }} 字段映射配置
              </div>
              <div v-if="loadingFieldMappings" style="text-align: center; padding: 20px; color: #909399;">
                <el-icon class="is-loading"><Loading /></el-icon> 加载中...
              </div>
              <div v-else-if="currentPlatformMappings.length === 0" style="text-align: center; padding: 20px;">
                <div style="color: #909399; margin-bottom: 10px;">
                  暂无字段映射配置
                </div>
                <div style="font-size: 13px; color: #606266; line-height: 1.6;">
                  <p style="margin: 5px 0;">💡 <strong>快速配置：</strong>在【盒子管理 → 字段映射】中点击<el-tag size="small" type="success" effect="plain">快速配置</el-tag>按钮</p>
                  <p style="margin: 5px 0;">上传样本数据文件（Excel/JSON），可视化配置字段映射</p>
                  <p style="margin: 5px 0; color: #909399;">支持嵌套字段（如 photo.0.url）和智能字段匹配</p>
                </div>
              </div>
              <div v-else style="max-height: 400px; overflow-y: auto;">
                <el-table :data="currentPlatformMappings" size="small" border stripe style="font-size: 12px;">
                  <el-table-column label="源字段 (JSON中的字段名)" prop="sourceField" min-width="180" show-overflow-tooltip>
                    <template #default="scope">
                      <el-tag v-if="scope.row.isRequired === '1'" size="small" type="danger" effect="plain">必填</el-tag>
                      <code style="color: #e6a23c; background: #fef0e0; padding: 2px 6px; border-radius: 3px; font-family: Consolas, monospace;">
                        {{ scope.row.sourceField }}
                      </code>
                    </template>
                  </el-table-column>
                  <el-table-column label="说明" prop="remark" min-width="150" show-overflow-tooltip />
                  <el-table-column label="数据类型" prop="fieldType" width="90" align="center">
                    <template #default="scope">
                      <el-tag size="small" :type="scope.row.fieldType === 'int' ? 'warning' : 'info'">
                        {{ scope.row.fieldType }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="存储位置" prop="targetLocation" width="100" align="center">
                    <template #default="scope">
                      <el-tag 
                        size="small" 
                        :type="scope.row.targetLocation === 'main' ? 'success' 
                          : scope.row.targetLocation === 'promotion_link' ? 'warning' 
                          : scope.row.targetLocation === 'relation' ? 'info'
                          : scope.row.targetLocation === 'category_relation' ? 'danger'
                          : 'primary'"
                      >
                        {{ 
                          scope.row.targetLocation === 'main' ? '主表' 
                          : scope.row.targetLocation === 'promotion_link' ? '推广链接' 
                          : scope.row.targetLocation === 'relation' ? '关联表'
                          : scope.row.targetLocation === 'category_relation' ? '分类关联'
                          : '平台数据'
                        }}
                      </el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <div style="margin-top: 12px; padding: 8px; background: #fff; border-radius: 4px; font-size: 12px; color: #606266;">
                <div style="margin-bottom: 6px; font-weight: 500;">💡 JSON示例格式：</div>
                <pre style="background: #f5f7fa; padding: 8px; border-radius: 4px; margin: 0; overflow-x: auto; font-size: 11px; line-height: 1.5;">{{
                  currentPlatformMappings.length > 0 
                    ? `[
  {
    "${currentPlatformMappings[0]?.sourceField || 'gamename'}": "游戏名称",
    "${currentPlatformMappings[1]?.sourceField || 'icon'}": "https://...",
    "${currentPlatformMappings[2]?.sourceField || 'photo'}": ["https://...", "https://..."],
    // ... 其他字段
  }
]` 
                    : '暂无示例'
                }}</pre>
              </div>
            </div>
          </el-collapse-transition>
        </el-form-item>
        <el-form-item label="JSON数据" prop="jsonData" :rules="[{ required: true, message: '请输入JSON数据', trigger: 'blur' }]">
          <el-input
            v-model="importForm.jsonData"
            type="textarea"
            :rows="18"
            :placeholder="`请粘贴平台原始JSON数据...
支持格式：
1. 对象数组：[{...}, {...}]
2. 包含items字段：{ &quot;items&quot;: [{...}] }
3. 单个对象：{...}`"
          />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            直接粘贴平台原始数据，无需手动转换字段
          </div>
        </el-form-item>
        <el-form-item v-if="importPreview && importPreview.length > 0" label="预览数据">
          <el-alert type="success" :closable="false" style="margin-bottom: 10px;">
            <template #title>
              <div style="display: flex; align-items: center; justify-content: space-between;">
                <span>成功解析 {{ importPreview.length }} 个游戏，请确认后导入</span>
                <el-button size="small" type="primary" plain @click="showPreviewDetail = !showPreviewDetail">
                  {{ showPreviewDetail ? '简化视图' : '详细视图' }}
                </el-button>
              </div>
            </template>
          </el-alert>
          <div style="max-height: 350px; overflow-y: auto; border: 1px solid #ebeef5; border-radius: 4px;">
            <el-table :data="importPreview" size="small" border stripe>
              <el-table-column type="index" label="#" width="50" />
              <el-table-column prop="name" label="游戏名称" width="180" show-overflow-tooltip>
                <template #default="scope">
                  <div>{{ scope.row.name }}</div>
                  <div v-if="scope.row._originalName && scope.row._originalName !== scope.row.name" style="font-size: 11px; color: #909399;">
                    原名: {{ scope.row._originalName }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="图标" width="80" align="center">
                <template #default="scope">
                  <el-image 
                    v-if="scope.row.icon_url || scope.row.iconUrl"
                    :src="scope.row.icon_url || scope.row.iconUrl" 
                    style="width: 40px; height: 40px;"
                    fit="cover"
                    :preview-src-list="[scope.row.icon_url || scope.row.iconUrl]"
                  />
                  <span v-else style="color: #ccc;">无</span>
                </template>
              </el-table-column>
              <el-table-column v-if="showPreviewDetail" prop="short_name" label="简称" width="120" show-overflow-tooltip />
              <el-table-column prop="category_id" label="游戏分类" width="120" align="center">
                <template #default="scope">
                  <template v-if="scope.row.category_id && scope.row.category_id.length > 0">
                    <el-tag
                      v-for="(cid, idx) in scope.row.category_id"
                      :key="idx"
                      size="small"
                      type="primary"
                      style="margin: 1px;"
                    >{{ getCategoryNameById(cid) }}</el-tag>
                  </template>
                  <span v-else style="color: #ccc;">未分类</span>
                </template>
              </el-table-column>
              <el-table-column prop="game_type" label="游戏类型" width="100" align="center">
                <template #default="scope">
                  <el-tag v-if="scope.row.game_type === 'official'" type="primary" size="small">官服</el-tag>
                  <el-tag v-else-if="scope.row.game_type === 'discount'" type="success" size="small">折扣</el-tag>
                  <el-tag v-else-if="scope.row.game_type === 'bt'" type="warning" size="small">BT</el-tag>
                  <el-tag v-else-if="['h5', 'H5'].includes(scope.row.game_type)" type="" size="small">H5</el-tag>
                  <el-tag v-else-if="scope.row.game_type === 'coming'" type="info" size="small">即将上线</el-tag>
                  <el-tag v-else-if="scope.row.game_type" type="info" size="small">{{ scope.row.game_type }}</el-tag>
                  <span v-else style="color: #c0c4cc;">-</span>
                </template>
              </el-table-column>
              <el-table-column v-if="!showPreviewDetail" prop="description" label="描述" min-width="200" show-overflow-tooltip />
              <el-table-column v-if="showPreviewDetail" prop="discount_label" label="折扣标签" width="150" show-overflow-tooltip />
              <el-table-column v-if="showPreviewDetail" prop="device_support" label="设备" width="80" align="center">
                <template #default="scope">
                  <el-tag v-if="scope.row.device_support === 'android' || scope.row.device_support === '0'" size="small" type="success">安卓</el-tag>
                  <el-tag v-else-if="scope.row.device_support === 'ios' || scope.row.device_support === '1'" size="small" type="info">iOS</el-tag>
                  <el-tag v-else size="small" type="primary">双端</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelImport">取 消</el-button>
          <el-button type="warning" icon="View" @click="previewImportData" :loading="importing">解析预览</el-button>
          <el-button type="primary" icon="Upload" @click="submitImportDirect" :disabled="!importPreview || importPreview.length === 0" :loading="importing">确认导入</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 重复游戏处理策略选择对话框 -->
    <el-dialog 
      title="检测到重复游戏" 
      v-model="strategyDialogVisible" 
      width="600px"
      top="8vh"
      append-to-body
      :close-on-click-modal="false"
    >
      <div style="max-height: 72vh; overflow-y: auto; padding: 0 4px;">
        <el-alert type="warning" :closable="false" style="margin-bottom: 20px;">
        <template #default>
          <div style="line-height: 1.8;">
            <div style="font-size: 14px; margin-bottom: 8px;">
              数据解析成功，找到 <strong style="color: #303133;">{{ importPreview?.length || 0 }}</strong> 个游戏，
              其中 <strong style="color: #E6A23C;">{{ duplicateGamesList.length }}</strong> 个与已有游戏重复：
            </div>
            <div style="font-size: 13px; color: #E6A23C; font-weight: 500;">
              {{ duplicateGamesList.map(g => g.name).join('、') }}
            </div>
          </div>
        </template>
      </el-alert>
      
      <div>
        <div style="font-weight: 600; margin-bottom: 16px; font-size: 15px; color: #303133;">
          <i class="el-icon-setting" style="margin-right: 6px;"></i>请选择处理方式：
        </div>
        <el-radio-group v-model="duplicateHandleStrategy" style="width: 100%;">
          <el-radio 
            value="merge" 
            style="width: 100%; height: auto; margin: 0 0 12px 0;"
          >
            <div style="padding: 14px 16px; border: 2px solid #DCDFE6; border-radius: 6px; transition: all 0.3s; cursor: pointer; min-height: 76px;"
                 :style="duplicateHandleStrategy === 'merge' ? 'border-color: #409EFF; background-color: #ecf5ff;' : ''"
                 @click="duplicateHandleStrategy = 'merge'"
                 @mouseenter="$event.currentTarget.style.borderColor = '#409EFF'"
                 @mouseleave="$event.currentTarget.style.borderColor = duplicateHandleStrategy === 'merge' ? '#409EFF' : '#DCDFE6'">
                  <el-tag type="success" size="small" style="position: absolute; top: 10px; right: 10px;">推荐</el-tag>

                 <div style="display: flex; align-items: center; margin-bottom: 6px;">
                <i class="el-icon-connection" style="font-size: 18px; color: #409EFF; margin-right: 8px;"></i>
                <span style="font-weight: 600; font-size: 14px; color: #303133;">合并到旧游戏</span>
              </div>
              <div style="font-size: 12px; color: #606266; line-height: 1.6; padding-left: 26px;">
                不创建重复游戏，为旧游戏追加分类并添加盒子关联
              </div>
            </div>
          </el-radio>
          
          <el-radio 
            value="keep" 
            style="width: 100%; height: auto; margin: 0 0 12px 0;"
          >
            <div style="padding: 14px 16px; border: 2px solid #DCDFE6; border-radius: 6px; transition: all 0.3s; cursor: pointer; min-height: 76px;"
                 :style="duplicateHandleStrategy === 'keep' ? 'border-color: #409EFF; background-color: #ecf5ff;' : ''"
                 @click="duplicateHandleStrategy = 'keep'"
                 @mouseenter="$event.currentTarget.style.borderColor = '#409EFF'"
                 @mouseleave="$event.currentTarget.style.borderColor = duplicateHandleStrategy === 'keep' ? '#409EFF' : '#DCDFE6'">
              <div style="display: flex; align-items: center; margin-bottom: 6px;">
                <i class="el-icon-document-add" style="font-size: 18px; color: #E6A23C; margin-right: 8px;"></i>
                <span style="font-weight: 600; font-size: 14px; color: #303133;">仅处理新游戏</span>
              </div>
              <div style="font-size: 12px; color: #606266; line-height: 1.6; padding-left: 26px;">
                新游戏作为主数据，迁移旧游戏的所有关联（分类、盒子）
              </div>
            </div>
          </el-radio>
          
          <el-radio 
            value="both" 
            style="width: 100%; height: auto; margin: 0;"
          >
            <div style="padding: 14px 16px; border: 2px solid #DCDFE6; border-radius: 6px; transition: all 0.3s; cursor: pointer; position: relative; min-height: 76px;"
                 :style="duplicateHandleStrategy === 'both' ? 'border-color: #67C23A; background-color: #f0f9ff;' : ''"
                 @click="duplicateHandleStrategy = 'both'"
                 @mouseenter="$event.currentTarget.style.borderColor = '#67C23A'"
                 @mouseleave="$event.currentTarget.style.borderColor = duplicateHandleStrategy === 'both' ? '#67C23A' : '#DCDFE6'">
              <div style="display: flex; align-items: center; margin-bottom: 6px;">
                <i class="el-icon-collection-tag" style="font-size: 18px; color: #67C23A; margin-right: 8px;"></i>
                <span style="font-weight: 600; font-size: 14px; color: #303133;">新旧都处理</span>
              </div>
              <div style="font-size: 12px; color: #606266; line-height: 1.6; padding-left: 26px;">
                创建独立的重复游戏，像首次导入一样关联盒子和分类
              </div>
            </div>
          </el-radio>
        </el-radio-group>
      </div>
    </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="strategyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmStrategy">
            <i class="el-icon-check" style="margin-right: 4px;"></i>确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加或修改游戏对话框 -->
    <el-dialog :title="title" v-model="open" width="80%" append-to-body top="3vh">
      <el-form ref="gameRef" :model="form" :rules="rules" label-width="140px" class="game-edit-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="创建者网站" prop="siteId">
              <SiteSelect v-model="form.siteId" :site-list="siteList" show-default clearable filterable width="100%" placeholder="请选择创建者网站" :disabled="!!form.id">
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
        </el-row>        <el-row v-if="form.siteId && !isPersonalSiteCheck(form.siteId)">
          <el-col :span="24">
            <el-alert
              title="提示：游戏可以选择本网站的分类或默认配置分类，默认配置分类适用于所有网站"
              type="info"
              :closable="false"
              style="margin-bottom: 10px"
              show-icon>
            </el-alert>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="fieldLabels.name" prop="name">
              <el-input v-model="form.name" placeholder="请输入游戏名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="fieldLabels.subtitle" prop="subtitle">
              <el-input v-model="form.subtitle" placeholder="请输入游戏副标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabels.shortName" prop="shortName">
              <el-input v-model="form.shortName" placeholder="请输入游戏简称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="游戏分类" prop="categoryIds">
              <el-tree-select
                v-model="form.categoryIds"
                :data="gameCategoryTreeOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="请选择分类（可多选）"
                check-strictly
                :render-after-expand="false"
                multiple
                clearable
                filterable
                collapse-tags
                collapse-tags-tooltip
                style="width: 100%"
                @change="handleCategoryChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.categoryIds && form.categoryIds.length > 1">
          <el-col :span="24">
            <el-form-item label="主分类" prop="primaryCategoryId">
              <el-select v-model="form.primaryCategoryId" placeholder="请选择主分类" style="width: 100%">
                <el-option
                  v-for="catId in form.categoryIds"
                  :key="catId"
                  :label="getCategoryNameById(catId)"
                  :value="catId"
                >
                  <span v-if="getCategoryIconById(catId)" style="margin-right: 4px">{{ getCategoryIconById(catId) }}</span>
                  <span>{{ getCategoryNameById(catId) }}</span>
                </el-option>
              </el-select>
              <div style="color: #909399; font-size: 12px; margin-top: 4px;">
                <el-icon><InfoFilled /></el-icon> 主分类将在列表中优先展示
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="fieldLabels.gameType" prop="gameType">
              <el-select v-model="form.gameType" placeholder="请选择游戏类型" style="width: 100%" clearable>
                <el-option
                  v-for="item in gameTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabels.deviceSupport" prop="deviceSupport">
              <el-select v-model="form.deviceSupport" placeholder="请选择设备支持" style="width: 100%" clearable>
                <el-option label="双端" value="both" />
                <el-option label="安卓" value="android" />
                <el-option label="iOS" value="ios" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="fieldLabels.developer" prop="developer">
              <el-input v-model="form.developer" placeholder="请输入游戏厂商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabels.publisher" prop="publisher">
              <el-input v-model="form.publisher" placeholder="请输入发行商" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item :label="fieldLabels.rating" prop="rating">
              <el-input-number v-model="form.rating" :min="0" :max="5" :precision="1" :step="0.1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="fieldLabels.downloadCount" prop="downloadCount">
              <el-input-number v-model="form.downloadCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item :label="fieldLabels.packageName" prop="packageName">
              <el-input v-model="form.packageName" placeholder="请输入包名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="fieldLabels.version" prop="version">
              <el-input v-model="form.version" placeholder="如: 1.0.0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="fieldLabels.size" prop="size">
              <el-input v-model="form.size" placeholder="如: 500MB" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="fieldLabels.iconUrl" prop="iconUrl">
              <el-input v-model="form.iconUrl" placeholder="请输入游戏图标URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="fieldLabels.coverUrl" prop="coverUrl">
              <el-input v-model="form.coverUrl" placeholder="请输入游戏封面URL" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabels.videoUrl" prop="videoUrl">
              <el-input v-model="form.videoUrl" placeholder="请输入视频URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="fieldLabels.screenshots" prop="screenshots">
              <el-input v-model="form.screenshots" type="textarea" :rows="2" placeholder="请输入截图URL数组（JSON格式）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="fieldLabels.description" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入游戏简介" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="fieldLabels.promotionDesc" prop="promotionDesc">
              <el-input v-model="form.promotionDesc" type="textarea" :rows="2" placeholder="请输入推广说明" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item :label="fieldLabels.tags" prop="tags">
              <el-input v-model="form.tags" placeholder="请输入标签，逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="游戏特性" prop="features">
              <el-input v-model="form.features" placeholder="游戏特性（JSON格式）" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上架时间" prop="launchTime">
              <el-date-picker
                v-model="form.launchTime"
                type="datetime"
                placeholder="选择上架时间"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="是否新游" prop="isNew">
              <el-switch v-model="form.isNew" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="是否热门" prop="isHot">
              <el-switch v-model="form.isHot" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="是否推荐" prop="isRecommend">
              <el-switch v-model="form.isRecommend" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="1">上架</el-radio>
                <el-radio value="0">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 下载链接区域 -->
        <el-divider content-position="left">
          <el-icon><Link /></el-icon>
          <span style="margin-left: 5px;">下载链接配置</span>
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item :label="fieldLabels.downloadUrl" prop="downloadUrl">
              <el-input v-model="form.downloadUrl" placeholder="请输入主下载链接" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="安卓下载" prop="androidUrl">
              <el-input v-model="form.androidUrl" placeholder="Android下载链接" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="iOS下载" prop="iosUrl">
              <el-input v-model="form.iosUrl" placeholder="iOS下载链接" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="APK下载" prop="apkUrl">
              <el-input v-model="form.apkUrl" placeholder="APK直链" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 折扣信息区域 -->
        <el-divider content-position="left">
          <el-icon><Discount /></el-icon>
          <span style="margin-left: 5px;">折扣信息</span>
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item :label="fieldLabels.discountLabel" prop="discountLabel">
              <el-input v-model="form.discountLabel" placeholder="如: 3折起充" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="首充国内" prop="firstChargeDomestic">
              <el-input-number v-model="form.firstChargeDomestic" :min="0" :max="10" :precision="2" :step="0.01" placeholder="折扣" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="首充海外" prop="firstChargeOverseas">
              <el-input-number v-model="form.firstChargeOverseas" :min="0" :max="10" :precision="2" :step="0.01" placeholder="折扣" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="低扣券" prop="hasLowDeductCoupon">
              <el-switch v-model="form.hasLowDeductCoupon" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="续充国内" prop="rechargeDomestic">
              <el-input-number v-model="form.rechargeDomestic" :min="0" :max="10" :precision="2" :step="0.01" placeholder="折扣" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="续充海外" prop="rechargeOverseas">
              <el-input-number v-model="form.rechargeOverseas" :min="0" :max="10" :precision="2" :step="0.01" placeholder="折扣" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.hasLowDeductCoupon === '1'">
          <el-col :span="24">
            <el-form-item label="低扣券链接" prop="lowDeductCouponUrl">
              <el-input v-model="form.lowDeductCouponUrl" placeholder="请输入低扣券链接" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 扶持信息区域 -->
        <el-divider content-position="left">
          <el-icon><Star /></el-icon>
          <span style="margin-left: 5px;">扶持信息</span>
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item :label="fieldLabels.hasSupport" prop="hasSupport">
              <el-switch v-model="form.hasSupport" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.hasSupport === '1'">
          <el-col :span="24">
            <el-form-item :label="fieldLabels.supportDesc" prop="supportDesc">
              <el-input v-model="form.supportDesc" type="textarea" :rows="2" placeholder="请输入扶持说明" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
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

    <!-- 盒子配置管理对话框 -->
    <el-dialog title="盒子配置管理" v-model="boxConfigDialogOpen" width="1000px" append-to-body top="5vh">
      <div v-loading="loadingBoxConfigs">
        <el-alert 
          v-if="gameBoxConfigs.length === 0 && !loadingBoxConfigs" 
          type="info" 
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        >
          该游戏暂未关联任何盒子
        </el-alert>
        
        <el-table 
          :data="gameBoxConfigs" 
          border 
          v-if="gameBoxConfigs.length > 0"
          max-height="500"
        >
          <el-table-column label="盒子名称" prop="boxName" width="150" />
          <el-table-column label="下载链接" prop="downloadUrl" min-width="200" show-overflow-tooltip />
          <el-table-column label="推广链接" prop="promoteUrl" min-width="200" show-overflow-tooltip />
          <el-table-column label="折扣标签" prop="discountLabel" width="100" align="center" />
          <el-table-column label="首充国内" prop="firstChargeDomestic" width="90" align="center" />
          <el-table-column label="首充海外" prop="firstChargeOverseas" width="90" align="center" />
          <el-table-column label="续充国内" prop="rechargeDomestic" width="90" align="center" />
          <el-table-column label="续充海外" prop="rechargeOverseas" width="90" align="center" />
          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button link type="primary" @click="handleEditBoxConfig(scope.row)">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="boxConfigDialogOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑盒子配置对话框 -->
    <el-dialog title="编辑盒子配置" v-model="editBoxConfigDialogOpen" width="600px" append-to-body>
      <el-form :model="boxConfigForm" label-width="120px">
        <el-form-item label="盒子名称">
          <el-input :value="boxConfigForm.boxName" disabled />
        </el-form-item>
        <el-form-item label="下载链接">
          <el-input v-model="boxConfigForm.downloadUrl" placeholder="请输入下载链接" />
        </el-form-item>
        <el-form-item label="推广链接">
          <el-input v-model="boxConfigForm.promoteUrl" placeholder="请输入推广链接" />
        </el-form-item>
        <el-form-item label="折扣标签">
          <el-input v-model="boxConfigForm.discountLabel" placeholder="如: 5折" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="首充国内">
              <el-input-number v-model="boxConfigForm.firstChargeDomestic" :min="0" :max="10" :precision="2" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="首充海外">
              <el-input-number v-model="boxConfigForm.firstChargeOverseas" :min="0" :max="10" :precision="2" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="续充国内">
              <el-input-number v-model="boxConfigForm.rechargeDomestic" :min="0" :max="10" :precision="2" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="续充海外">
              <el-input-number v-model="boxConfigForm.rechargeOverseas" :min="0" :max="10" :precision="2" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="有扶持">
              <el-switch v-model="boxConfigForm.hasSupport" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="扶持说明">
          <el-input v-model="boxConfigForm.supportDesc" type="textarea" :rows="2" placeholder="请输入扶持说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editBoxConfigDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="submitBoxConfig">保存</el-button>
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
          <p>• 导出选中游戏的数据（包含分类关联）</p>
          <p>• 游戏数据不包含翻译和网站关联</p>
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
          <el-tag type="info">{{ ids.length }} 个游戏</el-tag>
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
          <p>• 全站导出将包含所有游戏数据（默认配置 + 所有站点）</p>
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
    <!-- 数据导入对话框 -->
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
        <p>• 将游戏数据导入到当前选择的创建者网站</p>
        <p>• 导入时会自动匹配分类</p>
        <p>• 必填字段：游戏名称</p>
        <el-form :model="systemImportForm" label-width="100px" style="margin-top: 15px;">
          <el-form-item label="创建者网站" prop="siteId">
            <el-select v-model="systemImportForm.siteId" placeholder="请选择创建者网站" style="width: 100%" filterable>
              <el-option
                v-for="site in siteList"
                :key="site.id"
                :label="site.displayLabel"
                :value="site.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
      <template #previewColumns>
        <el-table-column prop="name" label="游戏名称" width="150" show-overflow-tooltip />
        <el-table-column label="图标" width="80" align="center">
          <template #default="scope">
            <el-image 
              v-if="scope.row.iconUrl"
              :src="scope.row.iconUrl" 
              style="width: 40px; height: 40px;"
              fit="cover"
            />
            <span v-else style="color: #ccc;">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="shortName" label="简称" width="100" />
        <el-table-column prop="developer" label="厂商" width="100" />
        <el-table-column prop="gameType" label="类型" width="100" />
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
      :importData="fullImportGames"
      v-model:importTranslations="fullImportTranslations"
      v-model:importRelations="fullImportRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      :dataLabel="'游戏'"
      @confirm="confirmFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    >
      <template #importTips>
        <p style="margin: 0;">从其他系统导入完整的游戏数据，包括：</p>
        <ul style="margin: 5px 0; padding-left: 20px;">
          <li>游戏基础数据</li>
          <li>网站关联关系</li>
          <li>默认配置排除关系</li>
          <li>多语言翻译数据（如果有）</li>
        </ul>
      </template>
      <template #dataPreviewColumns>
        <el-table-column prop="游戏名称" label="游戏名称" width="150" show-overflow-tooltip />
        <el-table-column prop="游戏简称" label="简称" width="100" />
        <el-table-column prop="游戏类型" label="类型" width="100" />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 exclude 排除（不显示所选游戏）</p>
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
          <strong>已选游戏：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedGamesForBatchExclude.length }} 个</el-tag>
        </div>
        <!-- 跨页全选模式：显示数量提示 -->
        <div v-if="batchExcludeCrossPage" style="padding: 8px 12px; background: #fff8e6; border: 1px solid #f5c842; border-radius: 4px; color: #7d5a00; font-size: 13px;">
          <el-icon style="margin-right: 4px; vertical-align: middle;"><WarningFilled /></el-icon>
          跨页全选模式，将对以上 <strong>{{ selectedGamesForBatchExclude.length }}</strong> 个游戏统一设置排除关系（不显示逐条详情）
        </div>
        <!-- 普通模式：显示游戏名标签 -->
        <div v-else style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="game in selectedGamesForBatchExclude" 
            :key="game.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeGameFromBatchExclude(game.id)"
            size="small"
          >
            {{ game.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各游戏的当前排除/关联状态（非跨页模式）-->
      <el-collapse v-if="!batchExcludeCrossPage && gameExclusionDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各游戏当前排除状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="gameExclusionDetails" size="small" stripe>
              <el-table-column label="游戏名称" prop="gameName" width="150" show-overflow-tooltip />
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
          <p style="margin: 0 0 8px 0;">• <strong>勾选的网站</strong> = 建立 include 关联（所选游戏对该网站可见）</p>
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
          <strong>已选游戏：</strong>
          <el-tag type="info" size="large" style="margin-left: 8px;">{{ selectedGamesForBatchRelation.length }} 个</el-tag>
        </div>
        <!-- 跨页全选模式：显示数量提示 -->
        <div v-if="batchRelationCrossPage" style="padding: 8px 12px; background: #fff8e6; border: 1px solid #f5c842; border-radius: 4px; color: #7d5a00; font-size: 13px;">
          <el-icon style="margin-right: 4px; vertical-align: middle;"><WarningFilled /></el-icon>
          跨页全选模式，将对以上 <strong>{{ selectedGamesForBatchRelation.length }}</strong> 个游戏统一设置关联关系（不显示逐条详情）
        </div>
        <!-- 普通模式：显示游戏名标签 -->
        <div v-else style="max-height: 100px; overflow-y: auto; padding: 8px; background: #f5f7fa; border-radius: 4px;">
          <el-tag 
            v-for="game in selectedGamesForBatchRelation" 
            :key="game.id" 
            style="margin: 4px 4px 4px 0;"
            closable
            @close="removeGameFromBatchRelation(game.id)"
            size="small"
          >
            {{ game.name }}
          </el-tag>
        </div>
      </div>
      
      <!-- 显示各游戏的当前关联/排除状态（非跨页模式）-->
      <el-collapse v-if="!batchRelationCrossPage && gameRelationDetails.length > 0" style="margin-bottom: 15px;">
        <el-collapse-item title="查看各游戏当前关联状态" name="1">
          <div style="max-height: 200px; overflow-y: auto;">
            <el-table :data="gameRelationDetails" size="small" stripe>
              <el-table-column label="游戏" prop="gameName" width="150" show-overflow-tooltip />
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

    <!-- ===== 游戏变更历史对话框（按批次分组）===== -->
    <el-dialog :title="`变更历史 - ${gameHistoryTitle}`" v-model="gameHistoryOpen" width="760px" top="5vh" append-to-body destroy-on-close>
      <div v-loading="gameHistoryLoading" style="max-height:65vh;overflow-y:auto;padding-right:4px;">
        <el-empty v-if="!gameHistoryLoading && gameHistoryByBatch.length === 0" description="暂无变更记录" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="batch in gameHistoryByBatch"
            :key="batch.batchNo"
            :timestamp="batch.createTime + '　操作人：' + (batch.createBy || '—')"
            placement="top"
          >
            <el-card shadow="never" style="border:1px solid #ebeef5;">
              <!-- 批次信息头 -->
              <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:10px;">
                <span style="font-size:12px;color:#909399;word-break:break-all;">
                  批次：{{ batch.batchNo }}
                </span>
                <div style="display:flex;gap:6px;align-items:center;">
                  <el-button link type="primary" size="small" @click="openImportHistoryDialog(batch.batchNo)">在数据修改历史中查看</el-button>
                  <el-button
                    v-if="batch.changes.some(c => c.reverted !== 1)"
                    link type="danger" size="small"
                    v-hasPermi="['gamebox:importBatch:revert']"
                    @click="handleRevertGameBatch(batch)">撤回此批次变更</el-button>
                  <el-button
                    v-if="batch.changes.every(c => c.reverted === 1)"
                    link type="success" size="small"
                    v-hasPermi="['gamebox:importBatch:revert']"
                    @click="handleReApplyGameBatch(batch)">重新应用</el-button>
                </div>
              </div>
              <!-- 每条变更 -->
              <div
                v-for="c in batch.changes"
                :key="c.id"
                style="display:flex;align-items:center;gap:8px;padding:6px 8px;border-radius:4px;background:#f9f9f9;margin-bottom:6px;flex-wrap:wrap;"
              >
                <el-tag :type="c.changeType === 'INSERT' ? 'success' : 'warning'" size="small">
                  {{ c.changeType === 'INSERT' ? '新增' : '更新' }}
                </el-tag>
                <el-tag type="info" size="small">
                  {{ c.targetTable === 'gb_games' ? '游戏主表' : (c.targetTable === 'gb_box_game_relations' ? '盒子关联' : '分类关联') }}
                </el-tag>
                <el-tag v-if="c.reverted === 1" type="danger" size="small">已撤回</el-tag>
                <div style="margin-left:auto;display:flex;gap:6px;">
                  <el-button link type="primary" size="small" @click="showHistoryDiff(c)">
                    {{ c.changeType === 'INSERT' ? '查看新增内容' : '查看修改对比' }}
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
      <template #footer>
        <el-button @click="gameHistoryOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 历史变更详情对话框 -->
    <el-dialog v-model="historyDiffOpen" title="变更详情" width="820px" top="5vh" append-to-body destroy-on-close>
      <div v-if="historyDiffTarget">
        <!-- 基本信息 -->
        <el-descriptions :column="4" border size="small" style="margin-bottom:16px;">
          <el-descriptions-item label="操作">
            <el-tag :type="historyDiffTarget.changeType === 'INSERT' ? 'success' : 'warning'" size="small">
              {{ historyDiffTarget.changeType === 'INSERT' ? '新增' : '更新' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="数据表">
            {{ historyDiffTarget.targetTable === 'gb_games' ? '游戏主表' : (historyDiffTarget.targetTable === 'gb_box_game_relations' ? '盒子关联表' : '分类关联表') }}
          </el-descriptions-item>
          <el-descriptions-item label="时间">{{ historyDiffTarget.createTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="historyDiffTarget.reverted === 1 ? 'danger' : 'success'" size="small">
              {{ historyDiffTarget.reverted === 1 ? '已撤回' : '生效中' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- INSERT：以表单预览样式展示新增内容 -->
        <template v-if="historyDiffTarget.changeType === 'INSERT'">
          <div style="font-weight:600;color:#409EFF;margin-bottom:10px;font-size:13px;">新增内容</div>
          <el-row :gutter="16">
            <el-col
              v-for="row in historyDiffRows.filter(r => r.afterVal !== null && r.afterVal !== undefined && r.afterVal !== '')"
              :key="row.field"
              :span="12"
              style="margin-bottom:10px;"
            >
              <div style="font-size:11px;color:#909399;margin-bottom:3px;">{{ row.label }}</div>
              <div style="padding:5px 10px;background:#f0f9eb;border:1px solid #e1f3d8;border-radius:4px;font-size:13px;word-break:break-all;min-height:28px;">
                {{ historyFormatVal(row.afterVal) }}
              </div>
            </el-col>
          </el-row>
        </template>

        <!-- UPDATE：变更字段 + 可展开的未变更字段 -->
        <template v-else>
          <!-- 有变更的字段（主要展示区） -->
          <div v-if="historyChangedRows.length > 0">
            <div style="font-weight:600;color:#E6A23C;margin-bottom:8px;font-size:13px;">
              已变更字段（{{ historyChangedRows.length }} 个）
            </div>
            <el-table :data="historyChangedRows" size="small" border style="margin-bottom:16px;">
              <el-table-column prop="label" label="字段" width="140" />
              <el-table-column label="修改前" min-width="220">
                <template #default="{ row }">
                  <span v-if="row.beforeVal !== undefined && row.beforeVal !== null && row.beforeVal !== ''" class="diff-before">
                    {{ historyFormatVal(row.beforeVal) }}
                  </span>
                  <span v-else style="color:#c0c4cc;">（空）</span>
                </template>
              </el-table-column>
              <el-table-column label="修改后" min-width="220">
                <template #default="{ row }">
                  <span v-if="row.afterVal !== null && row.afterVal !== undefined && row.afterVal !== ''" class="diff-after">
                    {{ historyFormatVal(row.afterVal) }}
                  </span>
                  <span v-else style="color:#c0c4cc;">（空）</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-else style="color:#67c23a;margin-bottom:12px;font-size:13px;">
            <el-icon><CircleCheck /></el-icon> 无字段实际变更（值完全相同）
          </div>

          <!-- 未变更字段（可收起） -->
          <div v-if="historyUnchangedRows.length > 0">
            <div
              style="font-size:12px;color:#909399;cursor:pointer;user-select:none;display:flex;align-items:center;gap:4px;margin-bottom:6px;"
              @click="showUnchangedFields = !showUnchangedFields"
            >
              <el-icon :style="showUnchangedFields ? 'transform:rotate(90deg)' : ''"><ArrowRight /></el-icon>
              未变更字段（{{ historyUnchangedRows.length }} 个）
            </div>
            <el-collapse-transition>
              <el-row v-if="showUnchangedFields" :gutter="12">
                <el-col
                  v-for="row in historyUnchangedRows.filter(r => r.afterVal !== null && r.afterVal !== undefined && r.afterVal !== '')"
                  :key="row.field"
                  :span="12"
                  style="margin-bottom:8px;"
                >
                  <div style="font-size:11px;color:#c0c4cc;margin-bottom:2px;">{{ row.label }}</div>
                  <div style="padding:4px 8px;background:#fafafa;border:1px solid #f0f0f0;border-radius:3px;font-size:12px;color:#909399;word-break:break-all;">
                    {{ historyFormatVal(row.afterVal) }}
                  </div>
                </el-col>
              </el-row>
            </el-collapse-transition>
          </div>
        </template>
      </div>
      <template #footer>
        <el-button @click="historyDiffOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ===== 数据修改历史对话框 ===== -->
    <el-dialog title="游戏数据修改历史" v-model="importHistoryOpen" width="1100px" top="3vh" append-to-body destroy-on-close>
      <!-- 筛选条件 -->
      <el-form :model="importHistoryParams" :inline="true" size="small" style="margin-bottom:8px;">
        <el-form-item label="游戏名称">
          <el-input
            v-model="importHistoryParams.gameName"
            placeholder="请输入游戏名称"
            clearable
            style="width:180px;"
            @keyup.enter="loadImportHistory"
          />
        </el-form-item>
        <el-form-item label="批次号列表">
          <el-select
            v-model="importHistoryParams.batchNoList"
            multiple
            filterable
            clearable
            collapse-tags
            collapse-tags-tooltip
            placeholder="可搜索并选择一个或多个批次"
            style="width:360px;"
            :loading="importHistoryBatchOptionsLoading"
            @visible-change="handleBatchSelectVisible"
          >
            <el-option
              v-for="item in importHistoryBatchOptions"
              :key="item.batchNo"
              :label="item.label"
              :value="item.batchNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="importHistoryParams.status" placeholder="全部" clearable style="width:110px;">
            <el-option label="成功" value="completed" />
            <el-option label="部分失败" value="partial_failed" />
            <el-option label="手工修改" value="manual" />
          </el-select>
        </el-form-item>
        <el-form-item label="已撤回">
          <el-select v-model="importHistoryParams.reverted" placeholder="全部" clearable style="width:100px;">
            <el-option label="未撤回" :value="0" />
            <el-option label="已撤回" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="loadImportHistory">查询</el-button>
          <el-button icon="Refresh" @click="resetImportHistoryFilters">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 批次表格 -->
      <el-table v-loading="importHistoryLoading" :data="importHistoryList" size="small" border
                row-key="id"
                :expand-row-keys="importHistoryExpandedKeys"
                @expand-change="handleImportHistoryExpand">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div style="padding:0 20px 12px;">
              <div v-if="row._changesLoaded || row._changes" style="display:flex;align-items:flex-start;justify-content:space-between;gap:12px;margin:0 0 10px;">
                <div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap;">
                  <el-input
                    v-model="row._detailKeyword"
                    clearable
                    size="small"
                    style="width:280px;"
                    placeholder="搜索游戏名称，快速定位撤回目标"
                    :disabled="!row._changesLoaded"
                    @input="onImportHistoryDetailFilterChange(row)"
                  />
                  <el-radio-group v-model="row._detailStatus" size="small" :disabled="!row._changesLoaded" @change="onImportHistoryDetailFilterChange(row)">
                    <el-radio-button label="all">全部</el-radio-button>
                    <el-radio-button label="active">可撤回</el-radio-button>
                    <el-radio-button label="reverted">已撤回</el-radio-button>
                  </el-radio-group>
                  <el-select
                    v-model="row._detailChangeType"
                    size="small"
                    style="width:130px;"
                    :disabled="!row._changesLoaded"
                    @change="onImportHistoryDetailFilterChange(row)"
                  >
                    <el-option label="全部类型" value="all" />
                    <el-option label="含新增" value="insert" />
                    <el-option label="含更新" value="update" />
                    <el-option label="仅跳过" value="skip" />
                  </el-select>
                  <el-button size="small" link :disabled="!row._changesLoaded" @click="resetImportHistoryDetailFilters(row)">清空明细筛选</el-button>
                </div>
                <div style="display:flex;align-items:center;gap:6px;flex-wrap:wrap;justify-content:flex-end;">
                  <el-tag size="small" type="info">总数 {{ getImportHistoryGameGroupStats(row).total }}</el-tag>
                  <el-tag size="small" type="success">可撤回 {{ getImportHistoryGameGroupStats(row).active }}</el-tag>
                  <el-tag size="small" type="danger">已撤回 {{ getImportHistoryGameGroupStats(row).reverted }}</el-tag>
                  <el-tag size="small" type="warning">跳过 {{ getImportHistorySkippedRows(row).length }}</el-tag>
                  <el-tag v-if="row._changesLoaded" size="small" type="warning">匹配 {{ getFilteredImportHistoryGameGroups(row).length }}</el-tag>
                  <el-tag v-else size="small" type="info">加载中...</el-tag>
                </div>
              </div>
              <el-table
                v-if="row._changes && row._detailChangeType !== 'skip' && getFilteredImportHistoryGameGroups(row).length > 0"
                :data="getPagedImportHistoryGameGroups(row)"
                size="small"
                border
              >
                <el-table-column prop="gameName" label="游戏名称" min-width="160" show-overflow-tooltip />
                <el-table-column label="变更内容" min-width="260">
                  <template #default="{ row: g }">
                    <div
                      v-for="c in getVisibleChangesForGroup(g, row)" :key="c.id"
                      style="display:flex;gap:4px;align-items:center;margin-bottom:3px;"
                    >
                      <el-tag :type="c.changeType === 'INSERT' ? 'success' : 'warning'" size="small">
                        {{ c.changeType === 'INSERT' ? '新增' : '更新' }}
                      </el-tag>
                      <el-tag type="info" size="small">
                        {{ c.targetTable === 'gb_games' ? '游戏主表' : (c.targetTable === 'gb_box_game_relations' ? '盒子关联' : '分类关联') }}
                      </el-tag>
                      <el-button link type="primary" size="small" @click="showHistoryDiff(c)">
                        {{ c.changeType === 'INSERT' ? '查看内容' : '查看对比' }}
                      </el-button>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="75">
                  <template #default="{ row: g }">
                    <el-tag :type="g.allReverted ? 'danger' : 'success'" size="small">
                      {{ g.allReverted ? '已撤回' : '生效中' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="110" fixed="right">
                  <template #default="{ row: g }">
                    <el-button link type="danger" size="small"
                               v-if="!g.allReverted"
                               v-hasPermi="['gamebox:importBatch:revert']"
                               @click="handleRevertImportGameGroup(g, row)">撤回</el-button>
                    <el-button link type="warning" size="small"
                               v-if="g.allReverted"
                               v-hasPermi="['gamebox:importBatch:revert']"
                               @click="handleReApplyImportGameGroup(g, row)">重新应用</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-table
                v-if="row._changes && row._detailChangeType === 'skip' && getFilteredImportHistorySkippedRows(row).length > 0"
                :data="getPagedImportHistorySkippedRows(row)"
                size="small"
                border
              >
                <el-table-column type="index" label="#" width="60" />
                <el-table-column prop="gameName" label="游戏名称" min-width="180" show-overflow-tooltip />
                <el-table-column prop="reason" label="跳过原因" min-width="320" show-overflow-tooltip />
                <el-table-column label="状态" width="90">
                  <template #default>
                    <el-tag type="info" size="small">已跳过</el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <div
                v-if="row._changes && getImportHistoryDetailTotal(row) > 0"
                style="display:flex;justify-content:flex-end;margin-top:8px;"
              >
                <el-pagination
                  small
                  background
                  :total="getImportHistoryDetailTotal(row)"
                  :current-page="row._detailPageNum"
                  :page-size="row._detailPageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  layout="total, sizes, prev, pager, next"
                  @current-change="(val) => handleImportHistoryDetailPageChange(row, val)"
                  @size-change="(val) => handleImportHistoryDetailSizeChange(row, val)"
                />
              </div>
              <el-empty
                v-if="row._changes && row._changes.length > 0 && getImportHistoryDetailTotal(row) === 0"
                description="未匹配到游戏，请调整关键词或状态筛选"
                :image-size="40"
              />
              <el-empty
                v-else-if="row._changesLoaded && (!row._changes || row._changes.length === 0)"
                description="暂无变更记录"
                :image-size="40"
              />
              <div v-else-if="!row._changesLoaded && !row._changes" style="display:flex;flex-direction:column;align-items:center;gap:8px;padding:16px;color:#666;">
                <el-icon style="font-size:24px;animation:spin 2s linear infinite;display:inline-block;">
                  <Loading />
                </el-icon>
                <div style="font-size:12px;">正在加载变更记录...</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批次号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="boxName" label="盒子" width="110" show-overflow-tooltip />
        <el-table-column label="变更统计" min-width="190">
          <template #default="{ row }">
            <el-space>
              <el-tag type="success" size="small">新增 {{ row.newCount }}</el-tag>
              <el-tag type="warning" size="small">更新 {{ row.updatedCount }}</el-tag>
              <el-tag type="info"    size="small">跳过 {{ row.skippedCount }}</el-tag>
              <el-tag type="danger"  size="small" v-if="row.failedCount > 0">失败 {{ row.failedCount }}</el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : (row.status === 'manual' ? 'info' : 'warning')" size="small">
              {{ row.status === 'completed' ? '成功' : (row.status === 'manual' ? '手工' : '部分失败') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reverted" label="撤回" width="70">
          <template #default="{ row }">
            <el-tag :type="row.reverted === 1 ? 'danger' : 'info'" size="small">
              {{ row.reverted === 1 ? '已撤回' : '未撤回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy"   label="操作人" width="80" />
        <el-table-column prop="createTime" label="操作时间" width="148" />
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button link type="danger" size="small"
                       v-if="row.reverted !== 1"
                       v-hasPermi="['gamebox:importBatch:revert']"
                       @click="handleRevertImportBatch(row)">撤回整批</el-button>
            <el-button link type="warning" size="small"
                       v-if="row.reverted === 1"
                       v-hasPermi="['gamebox:importBatch:revert']"
                       @click="handleReApplyImportBatch(row)">重新应用</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-show="importHistoryTotal > 0"
        :total="importHistoryTotal"
        v-model:current-page="importHistoryParams.pageNum"
        v-model:page-size="importHistoryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top:12px;justify-content:flex-end;"
        @size-change="loadImportHistory"
        @current-change="loadImportHistory"
      />
      <template #footer>
        <el-button @click="importHistoryOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Game">
import { listGame, listGameIds, getGame, delGame, addGame, updateGame, updateGameStatus, getGameHomepage, bindGameHomepage, unbindGameHomepage, getGameCategories, saveGameCategories, appendGameCategories, batchSaveGameCategories, batchAppendGameCategories, batchAddGames as batchCreateGames, importFromPlatform, getSupportedPlatforms } from "@/api/gamebox/game"
import { listMasterArticle } from "@/api/gamebox/masterArticle"
import { listSite } from "@/api/gamebox/site"
import { enrichSiteList, getSiteDisplayName, getPersonalSiteId, isPersonalSite, restoreViewModeSiteSelection, resolveSiteIdByViewMode, saveViewModeSiteSelection } from "@/composables/useSiteSelection"
import { getGameSites, getBatchGameSites, batchSaveGameSiteRelations, updateGameVisibility } from "@/api/gamebox/siteRelation"
import { listFieldMapping, getAllTableFields, getFieldDistinctValues } from "@/api/gamebox/fieldMapping"
import { listBox } from "@/api/gamebox/box"
import { getBoxesByGameId, batchAddGames, batchRemoveGames, addBoxGame, updateBoxGame, batchAddGamesWithRelations } from "@/api/gamebox/boxgame"
import { listCategory, listVisibleCategory } from "@/api/gamebox/category"
import { useSiteSelection } from "@/composables/useSiteSelection"
import SiteSelect from "@/components/SiteSelect/index.vue"
import CategoryTag from "@/components/CategoryTag/index.vue"
import SiteRelationManager from "@/components/SiteRelationManager/index.vue"
import BoxRelationManager from "@/components/BoxRelationManager/index.vue"
import TranslationManager from "@/components/TranslationManager/index.vue"
import ImportDialog from "@/components/ImportExportDialogs/ImportDialog.vue"
import FullImportDialog from "@/components/ImportExportDialogs/FullImportDialog.vue"
import { batchAutoTranslate } from "@/api/gamebox/translation"
import { getGameHistory, revertChange as revertGameHistoryChange, listImportBatch, getBatchChanges, getBatchChangesByNo, revertImportBatch, reApplyImportBatch, revertImportBatchByNo, reApplyImportBatchByNo, revertByBatchGame, reApplyByBatchGame, revertByBatchNoGame, reApplyByBatchNoGame } from "@/api/gamebox/importBatch"
import { handleTree } from "@/utils/ruoyi"
import { StarFilled, InfoFilled, User, Link, Star, Plus, Search, Edit, CircleCheck, CircleClose, Picture, Tickets, Download, Trophy, Document, QuestionFilled, View, Hide, ArrowDown, ArrowRight, Warning, WarningFilled, Loading } from '@element-plus/icons-vue'
import { h, markRaw } from 'vue'
import { ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const { proxy } = getCurrentInstance()

// 使用网站选择组合式函数
const { siteList, currentSiteId, loadSiteList: loadSites, getSiteName } = useSiteSelection()

const personalSiteId = computed(() => getPersonalSiteId(siteList.value))
const isPersonalSiteCheck = (siteId) => isPersonalSite(siteId, siteList.value)

// O(1)网站名称查找，避免每行渲染时 O(n) find
const siteMap = computed(() => {
  const map = new Map()
  siteList.value.forEach(s => map.set(s.id, s.name))
  return map
})
function getSiteNameFast(siteId) {
  if (isPersonalSite(siteId, siteList.value) || siteId === null || siteId === undefined) {
    return getSiteDisplayName(siteId, siteList.value)
  }
  return siteMap.value.get(siteId) || `网站ID: ${siteId}`
}

// 数据库字段信息
const tableFieldsInfo = ref({})

const gameList = ref([])
const gameCategoryList = ref([])
const gameCategoryTreeOptions = ref([]) // 树形结构，用于对话框
const gameCategoryQueryTreeOptions = ref([]) // 树形结构，用于查询表单
const categoryCache = shallowRef(new Map()) // 分类缓存，用于表格显示（shallowRef避免Map深度响应）
const categoryCacheLoaded = ref(false) // 分类缓存是否已加载过
const iconErrorIds = reactive(new Set()) // 图标加载失败的游戏ID（独立于行数据，避免markRaw行无法触发重渲染）
const includeDefaultConfig = ref(false) // 是否包含默认配置
const viewMode = ref('creator') // 查看模式：creator-创建者, related-关联网站
const open = ref(false)

// 网站关联管理相关
const siteRelationDialogOpen = ref(false)
const currentGameIdForSites = ref(0)
const currentGameNameForSites = ref('')
const currentGameCreatorSiteId = ref(0)

// 盒子关联管理相关
const boxRelationDialogOpen = ref(false)
const currentGameIdForBoxes = ref(0)
const currentGameNameForBoxes = ref('')
const boxRelationReadonly = ref(false)
const boxRelationSiteId = ref(null)

// 排除网站管理相关
const exclusionDialogOpen = ref(false)
const currentExclusionGameId = ref(null)
const currentExclusionGameName = ref('')
const selectedExcludedSiteIds = ref([])

// 字段映射配置缓存
const fieldMappingCache = ref(new Map()) // key: platform, value: {promotion_link: Map, platform_data: Map}

// 推广策略对话框
const strategyDialogVisible = ref(false)

// 翻译管理相关
const translationDialogOpen = ref(false)
const currentTranslationGameId = ref(0)
const currentTranslationGameName = ref('')
const currentTranslationGameData = ref({})
const gameTranslationFields = [
  { name: 'name', label: '游戏名称', type: 'text' },
  { name: 'subtitle', label: '游戏副标题', type: 'text' },
  { name: 'shortName', label: '游戏简称', type: 'text' },
  { name: 'description', label: '游戏描述', type: 'textarea' },
  { name: 'promotionDesc', label: '推广说明', type: 'textarea' },
  { name: 'discountLabel', label: '折扣标签', type: 'text' }
]

// 批量排除管理相关
const batchExclusionDialogOpen = ref(false)
const selectedGamesForBatchExclude = ref([])
const gameExclusionDetails = ref([])
const batchExcludedSiteIds = ref([])
const batchExclusionLoading = ref(false)
const batchExcludeCrossPage = ref(false)   // 是否从跨页全选模式打开
const gameTableRef = ref(null)

// 冲突网站 ID 集合（存在 include 关联的网站，exclude 对其无效）
const batchExclusionConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  gameExclusionDetails.value.forEach(detail => {
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
const selectedGamesForBatchRelation = ref([])
const gameRelationDetails = ref([]) // 各游戏的关联详情
const batchRelationCrossPage = ref(false)  // 是否从跨页全选模式打开

// 是否处于默认配置的批量关联管理
const batchRelationIsDefaultConfig = computed(() => isPersonalSite(queryParams.value.siteId, siteList.value))

// 冲突网站 ID 集合（存在 exclude 关系的网站）
const batchRelationConflictSiteIds = computed(() => {
  const conflictSet = new Set()
  gameRelationDetails.value.forEach(detail => {
    if (detail.excludedSiteIds) {
      detail.excludedSiteIds.forEach(siteId => conflictSet.add(siteId))
    }
  })
  return conflictSet
})

const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 跨页全选相关状态
const crossPageSelectActive = ref(false)   // 是否已激活跨页全选模式
const crossPageIds = ref([])               // 跨页全选时所有筛选结果的ID
const crossPageLoading = ref(false)        // 正在加载全量ID

// 当前有效的选中ID（跨页模式用crossPageIds，否则用当前页选中的ids）
const effectiveIds = computed(() => crossPageSelectActive.value ? crossPageIds.value : ids.value)
const effectiveMultiple = computed(() => crossPageSelectActive.value ? crossPageIds.value.length === 0 : multiple.value)

const gameTypeOptions = ref([])

async function loadGameTypeOptions() {
  try {
    const response = await getFieldDistinctValues('gb_games', 'game_type')
    const values = Array.isArray(response?.data) ? response.data : []
    const unique = [...new Set(values.map(v => String(v || '').trim()).filter(Boolean))]
    gameTypeOptions.value = unique.map(v => ({ value: v, label: v }))
  } catch (error) {
    console.error('加载游戏类型选项失败:', error)
    gameTypeOptions.value = []
  }
}

// 字段标签计算属性
const fieldLabels = computed(() => ({
  name: getMainFieldLabel('name', '游戏名称'),
  subtitle: getMainFieldLabel('subtitle', '游戏副标题'),
  shortName: getMainFieldLabel('short_name', '游戏简称'),
  iconUrl: getMainFieldLabel('icon_url', '游戏图标'),
  coverUrl: getMainFieldLabel('cover_url', '游戏封面'),
  screenshots: getMainFieldLabel('screenshots', '游戏截图'),
  videoUrl: getMainFieldLabel('video_url', '视频URL'),
  description: getMainFieldLabel('description', '游戏描述'),
  gameType: getMainFieldLabel('game_type', '游戏类型'),
  deviceSupport: getMainFieldLabel('device_support', '设备支持'),
  developer: getMainFieldLabel('developer', '游戏厂商'),
  publisher: getMainFieldLabel('publisher', '发行商'),
  rating: getMainFieldLabel('rating', '游戏评分'),
  downloadCount: getMainFieldLabel('download_count', '下载次数'),
  packageName: getMainFieldLabel('package_name', '包名'),
  version: getMainFieldLabel('version', '版本号'),
  size: getMainFieldLabel('size', '安装包大小'),
  tags: getMainFieldLabel('tags', '游戏标签'),
  promotionDesc: getMainFieldLabel('promotion_desc', '推广说明'),
  downloadUrl: getMainFieldLabel('download_url', '下载链接'),
  officialWebsite: getMainFieldLabel('official_website', '官方网站'),
  discountLabel: getMainFieldLabel('discount_label', '折扣标签'),
  hasSupport: getMainFieldLabel('has_support', '是否有扶持'),
  supportDesc: getMainFieldLabel('support_desc', '扶持说明'),
  status: getMainFieldLabel('status', '状态'),
  remark: getMainFieldLabel('remark', '备注')
}))

// 导入相关
const importOpen = ref(false)
const importing = ref(false)
const importPreview = ref(null) // 预览数据
const showPreviewDetail = ref(false) // 是否显示详细视图
const showFieldMapping = ref(false) // 是否显示字段映射说明
const currentPlatformMappings = ref([]) // 当前平台的字段映射列表
const loadingFieldMappings = ref(false) // 加载字段映射中
const importBoxList = ref([]) // 导入盒子列表
const importCategoryTreeOptions = ref([]) // 导入分类树选项
const importForm = ref({
  boxId: undefined, // 盒子ID（替代原来的platformCode）
  siteId: undefined,
  jsonData: ''
})

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
const fullImportGames = ref([]) // 导入的游戏数据
const fullImportRelations = ref([]) // 导入的关联关系
const fullImportTranslations = ref([]) // 导入的翻译数据
const fullImportFile = ref(null)
const siteMapping = ref({}) // 网站ID映射 {源网站ID: 目标网站ID}
const hasDefaultConfig = ref(false) // 是否包含默认配置(siteid=0)
const createDefaultAsNewSite = ref(true) // 是否将默认配置导入为新网站的配置（并创建关联）


// 主页面预览相关
const homepageDialogOpen = ref(false)
const loadingHomepages = ref(false)
const homepageBinding = ref(null)
const currentGameForBind = ref(null)

// 绑定主页相关
const bindHomepageDialogOpen = ref(false)
const loadingBindArticles = ref(false)
const bindArticleList = ref([])
const currentBindType = ref('none')
const currentBindTargetId = ref(undefined)
const binding = ref(false)

// 计算属性：当前游戏的显示名称
const currentGameDisplayName = computed(() => {
  return currentGameForBind.value?.name || '游戏'
})

// 盒子管理相关
const boxesOpen = ref(false)
const currentGameId = ref(null)
const currentGameName = ref("")
const allBoxes = ref([])
const selectedBoxIds = ref([])
const originalBoxIds = ref([])
const boxLookupMap = computed(() => {
  const map = new Map()
  allBoxes.value.forEach(box => map.set(Number(box.id), box))
  return map
})
const transferBoxData = computed(() => {
  return allBoxes.value.map(box => ({
    value: box.id,
    label: box.status === '0' ? `${box.name} [禁用]` : box.name,
    disabled: false
  }))
})

// 盒子配置管理相关
const boxConfigDialogOpen = ref(false)
const editBoxConfigDialogOpen = ref(false)
const loadingBoxConfigs = ref(false)
const gameBoxConfigs = ref([])
const boxConfigForm = ref({
  id: undefined,
  boxId: undefined,
  boxName: undefined,
  gameId: undefined,
  downloadUrl: undefined,
  promoteUrl: undefined,
  discountLabel: undefined,
  firstChargeDomestic: undefined,
  firstChargeOverseas: undefined,
  rechargeDomestic: undefined,
  rechargeOverseas: undefined,
  hasSupport: '0',
  supportDesc: undefined
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    siteId: undefined,
    name: undefined,
    subtitle: undefined,
    gameType: undefined,
    categoryIds: [],
    uncategorizedOnly: false,
    status: undefined,
    remark: undefined,
    boxIds: [],
    updateTimeRange: [],
    updateTimeStart: undefined,
    updateTimeEnd: undefined
  },
  rules: {
    siteId: [{ required: true, message: "请选择创建者网站", trigger: "change" }],
    name: [{ required: true, message: "游戏名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 网站切换事件 */
function handleSiteChange() {
  // 切换网站时重置“含默认配置”选项
  if (!queryParams.value.siteId || isPersonalSite(queryParams.value.siteId, siteList.value)) {
    includeDefaultConfig.value = false
  }
  queryParams.value.boxIds = []
  queryParams.value.pageNum = 1
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  loadGameCategoriesForQuery(queryParams.value.siteId)
  loadBoxOptions(queryParams.value.siteId)
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
  // 重置盒子选择
  queryParams.value.boxIds = []
  loadBoxOptions(queryParams.value.siteId)
  getList()
}

// 预加载分类信息到缓存
function preloadCategories(gameRecords) {
  // 提取所有唯一的categoryId
  // 已加载过则跳过，避免每次刷新列表都重复请求全量分类
  if (categoryCacheLoaded.value) return

  const categoryIds = [...new Set(gameRecords.map(g => g.categoryId).filter(id => id))]
  
  if (categoryIds.length === 0) return
  
  // 批量查询所有分类（不限制siteId，这样可以查到全局和各站点的分类）
  listCategory({ 
    categoryType: 'game', 
    pageNum: 1, 
    pageSize: 9999,
    status: '1'
  }).then(response => {
    const newMap = new Map(categoryCache.value)
    ;(response.rows || []).forEach(cat => newMap.set(cat.id, cat))
    categoryCache.value = newMap // 一次性替换，触发一次响应式更新
    categoryCacheLoaded.value = true
  })
}

// 从缓存获取分类名称
function getCategoryFromCache(categoryId) {
  if (!categoryId) return null
  return categoryCache.value.get(categoryId)
}

function getList() {
  loading.value = true
  // 查询条件变化时重置跨页全选状态
  crossPageSelectActive.value = false
  crossPageIds.value = []
  
  // 构建查询参数
  const { boxIds, categoryIds, updateTimeRange, ...baseQueryParams } = queryParams.value
  const selectedBoxIds = Array.isArray(boxIds)
    ? boxIds.filter(id => id !== null && id !== undefined)
    : []
  const selectedCategoryIds = Array.isArray(categoryIds)
    ? categoryIds.filter(id => id !== null && id !== undefined)
    : []
  const params = { 
    ...baseQueryParams,
    queryMode: viewMode.value,
    includeDefault: includeDefaultConfig.value,
    boxIdsFilter: selectedBoxIds.length > 0 ? selectedBoxIds.join(',') : undefined,
    categoryIdsFilter: selectedCategoryIds.length > 0 ? selectedCategoryIds.join(',') : undefined
  }
  
  // 使用统一的查询接口
  if (params.siteId !== undefined) {
    listGame(params).then(response => {
      iconErrorIds.clear()
      // markRaw：阻止Vue对每个行对象建立深度响应式proxy（每行~40字段 × 410行 = 约16000个watcher），大幅提升渲染速度
      gameList.value = response.rows.map(row => markRaw({
        ...row,
        relationType: row.relationType || 'include'
      }))
      total.value = response.total
      // 预加载所有分类信息到缓存
      preloadCategories(gameList.value)
      loading.value = false
    }).catch(error => {
      console.error('查询游戏失败:', error)
      proxy.$modal.msgError('查询失败')
      loading.value = false
    })
  } else {
    // 未选择网站：默认显示默认配置
    listGame({ ...params, siteId: personalSiteId.value, queryMode: 'creator' }).then(response => {
      iconErrorIds.clear()
      gameList.value = response.rows.map(row => markRaw({
        ...row,
        relationType: row.relationType || 'include'
      }))
      total.value = response.total
      // 预加载所有分类信息到缓存
      preloadCategories(gameList.value)
      loading.value = false
    }).catch(error => {
      console.error('查询游戏失败:', error)
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
    subtitle: undefined,
    shortName: undefined,
    category: undefined,
    categoryId: undefined,
    categoryIds: [],
    primaryCategoryId: undefined,
    gameType: undefined,
    iconUrl: undefined,
    coverUrl: undefined,
    screenshots: undefined,
    videoUrl: undefined,
    description: undefined,
    promotionDesc: undefined,
    developer: undefined,
    publisher: undefined,
    packageName: undefined,
    version: undefined,
    size: undefined,
    deviceSupport: 'both',
    downloadCount: 0,
    rating: 5.0,
    features: undefined,
    tags: undefined,
    launchTime: undefined,
    isNew: '0',
    isHot: '0',
    isRecommend: '0',
    sortOrder: 0,
    status: '1',
    downloadUrl: undefined,
    androidUrl: undefined,
    iosUrl: undefined,
    apkUrl: undefined,
    discountLabel: undefined,
    firstChargeDomestic: undefined,
    firstChargeOverseas: undefined,
    rechargeDomestic: undefined,
    rechargeOverseas: undefined,
    hasSupport: '0',
    supportDesc: undefined,
    hasLowDeductCoupon: '0',
    lowDeductCouponUrl: undefined,
    remark: undefined
  }
  proxy.resetForm("gameRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 勾选「仅无分类」时自动清空已选分类 */
function handleUncategorizedChange(val) {
  if (val) {
    queryParams.value.categoryIds = []
  }
  handleQuery()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  queryParams.value.updateTimeRange = []
  queryParams.value.updateTimeStart = undefined
  queryParams.value.updateTimeEnd = undefined
  handleQuery()
}

/** 更新时间范围改变处理 */
function handleUpdateTimeChange(dates) {
  if (dates && dates.length === 2) {
    // 转换为时间戳（毫秒）
    queryParams.value.updateTimeStart = dates[0] ? dates[0].getTime() : undefined
    queryParams.value.updateTimeEnd = dates[1] ? dates[1].getTime() : undefined
  } else {
    queryParams.value.updateTimeStart = undefined
    queryParams.value.updateTimeEnd = undefined
  }
  // 重新加载数据
  queryParams.value.pageNum = 1
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
  // 手动取消勾选时退出跨页全选模式
  if (crossPageSelectActive.value && selection.length < gameList.value.length) {
    crossPageSelectActive.value = false
    crossPageIds.value = []
  }
}

/** 加载当前筛选条件下的所有游戏ID，激活跨页全选模式 */
async function handleSelectAllFiltered() {
  crossPageLoading.value = true
  try {
    const { boxIds, categoryIds, updateTimeRange, ...baseQueryParams } = queryParams.value
    const selectedBoxIds = Array.isArray(boxIds) ? boxIds.filter(id => id != null) : []
    const selectedCategoryIds = Array.isArray(categoryIds) ? categoryIds.filter(id => id != null) : []
    const params = {
      ...baseQueryParams,
      queryMode: viewMode.value,
      includeDefault: includeDefaultConfig.value,
      boxIdsFilter: selectedBoxIds.length > 0 ? selectedBoxIds.join(',') : undefined,
      categoryIdsFilter: selectedCategoryIds.length > 0 ? selectedCategoryIds.join(',') : undefined
    }
    if (params.siteId === undefined) {
      params.siteId = personalSiteId.value
      params.queryMode = 'creator'
    }
    const res = await listGameIds(params)
    crossPageIds.value = res.data || []
    crossPageSelectActive.value = true
    proxy.$modal.msgSuccess(`已全选 ${crossPageIds.value.length} 条筛选结果`)
  } catch (e) {
    proxy.$modal.msgError('全选失败：' + e.message)
  } finally {
    crossPageLoading.value = false
  }
}

/** 清除跨页全选模式 */
function clearCrossPageSelect() {
  crossPageSelectActive.value = false
  crossPageIds.value = []
  // 同时取消表格内当前页的选中状态
  gameTableRef.value?.clearSelection()
  ids.value = []
  single.value = true
  multiple.value = true
}

function handleAdd() {
  reset()
  // 默认使用当前查询的站点ID（包含 siteId=0 的默认配置）
  if (queryParams.value.siteId != null) {
    form.value.siteId = queryParams.value.siteId
  } else if (siteList.value.length > 0) {
    // 如果查询没有选择站点，默认选择第一个站点
    form.value.siteId = siteList.value[0].id
  }
  loadGameCategoriesForDialog(form.value.siteId)
  open.value = true
  title.value = "添加游戏"
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getGame(id).then(response => {
    const data = response.data
    
    // 先打开对话框
    open.value = true
    title.value = "修改游戏"
    
    // 先加载分类列表（始终包含全局分类）
    loadGameCategoriesForDialog(data.siteId)
    
    // 加载游戏的分类关联
    getGameCategories(id).then(catResponse => {
      const categories = catResponse.data || []
      form.value.categoryIds = categories.map(cat => cat.categoryId)
      // 找到主分类
      const primaryCat = categories.find(cat => cat.isPrimary === '1')
      if (primaryCat) {
        form.value.primaryCategoryId = primaryCat.categoryId
      } else if (categories.length > 0) {
        // 如果没有主分类但有分类，默认第一个为主分类
        form.value.primaryCategoryId = categories[0].categoryId
      }
      
      // 最后设置表单数据
      nextTick(() => {
        form.value = { ...form.value, ...data }
      })
    })
  })
}

function submitForm() {
  proxy.$refs["gameRef"].validate(valid => {
    if (valid) {
      // 验证：如果选择了多个分类，必须指定主分类
      if (form.value.categoryIds && form.value.categoryIds.length > 1 && !form.value.primaryCategoryId) {
        proxy.$modal.msgWarning("请选择主分类")
        return
      }
      
      // 如果只有一个分类，自动设为主分类
      if (form.value.categoryIds && form.value.categoryIds.length === 1) {
        form.value.primaryCategoryId = form.value.categoryIds[0]
      }
      
      // 为了兼容性，设置 categoryId 为主分类ID
      if (form.value.primaryCategoryId) {
        form.value.categoryId = form.value.primaryCategoryId
      }
      
      if (form.value.id != undefined) {
        updateGame(form.value).then(response => {
          // 保存游戏分类关联（包括清空场景）
          const categoryRelations = []
          if (form.value.categoryIds && form.value.categoryIds.length > 0) {
            form.value.categoryIds.forEach(catId => {
              categoryRelations.push({
                categoryId: catId,
                isPrimary: catId === form.value.primaryCategoryId ? '1' : '0'
              })
            })
          }
          // 始终调用saveGameCategories，即使是空数组也要调用（用于清空关联）
          saveGameCategories(form.value.id, categoryRelations).then(() => {
            proxy.$modal.msgSuccess("修改成功")
            open.value = false
            getList()
          })
        })
      } else {
        addGame(form.value).then(response => {
          const gameId = response.data
          
          // 验证返回的ID是否有效
          if (!gameId || typeof gameId !== 'number') {
            proxy.$modal.msgWarning('游戏创建成功，但无法获取新游戏ID，请刷新后手动配置分类')
            open.value = false
            getList()
            return
          }
          
          // 保存游戏分类关联
          if (form.value.categoryIds && form.value.categoryIds.length > 0) {
            const categoryRelations = form.value.categoryIds.map(catId => ({
              categoryId: catId,
              isPrimary: catId === form.value.primaryCategoryId ? '1' : '0'
            }))
            saveGameCategories(gameId, categoryRelations).then(() => {
              proxy.$modal.msgSuccess("新增成功")
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
  })
}

function handleDelete(row) {
  const gameIds = row.id || effectiveIds.value
  proxy.$modal.confirm('是否确认删除游戏编号为"' + gameIds + '"的数据项？').then(function() {
    return delGame(gameIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

// 加载游戏分类列表（对话框使用）
// 使用可见分类接口，只展示对该网站可见的分类（默认配置未排除 + 跨站共享可见 + 自有启用）
function loadGameCategoriesForDialog(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点或选择了全局，只查询全局分类
    return listVisibleCategory({ 
      categoryType: 'game', 
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
      gameCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
    })
  }
  
  // 选择了具体站点：使用可见分类接口，只返回可见的分类
  // 包括：1.默认配置未排除 2.其他网站跨站共享可见 3.自有网站启用
  return listVisibleCategory({ 
    categoryType: 'game', 
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
    gameCategoryTreeOptions.value = handleTree(categories, "id", "parentId")
  })
}

// 加载游戏分类列表（查询表单使用）
// 始终使用关联模式，显示所有分类并标记不可用的
function loadGameCategoriesForQuery(siteId) {
  if (!siteId || isPersonalSite(siteId, siteList.value)) {
    // 没有选择站点，只查询默认配置分类（创建者模式）
    listCategory({ 
      categoryType: 'game', 
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
      gameCategoryList.value = categories
      gameCategoryQueryTreeOptions.value = handleTree(categories, "id", "parentId")
    })
    return
  }
  
  // 选择了具体站点：始终使用关联模式（related）查询所有分类
  // 关联模式会返回：1.默认配置（包括被排除的） 2.跨站共享（包括不可见的） 3.自有分类
  // 并且会在 is_visible 字段中标记每个分类的可见性状态
  listCategory({ 
    categoryType: 'game',
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
    gameCategoryList.value = categories
    gameCategoryQueryTreeOptions.value = handleTree(categories, "id", "parentId")
  })
}

// 加载站点列表
function loadSiteList() {
  listSite({ pageNum: 1, pageSize: 1000, status: '1' }).then(response => {
    siteList.value = enrichSiteList(response.rows || [])
    if (siteList.value.length === 0) {
      proxy.$modal.msgWarning("当前没有可用的站点，请先添加站点后再添加游戏")
    }
  }).catch(error => {
    console.error('加载站点列表失败:', error)
    proxy.$modal.msgError("加载站点列表失败: " + (error.message || '未知错误'))
    siteList.value = []
  })
}

// 预览主页面
function handlePreviewHomepages(row) {
  currentGameForBind.value = row
  homepageDialogOpen.value = true
  loadingHomepages.value = true
  homepageBinding.value = null
  
  getGameHomepage(row.id).then(response => {
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
  if (!currentGameForBind.value) {
    return
  }
  
  // 查询当前绑定信息
  if (homepageBinding.value && homepageBinding.value.masterArticleId) {
    currentBindType.value = 'article'
    currentBindTargetId.value = homepageBinding.value.masterArticleId
  } else {
    currentBindType.value = 'none'
    currentBindTargetId.value = undefined
  }
  
  // 先加载主文章列表，然后再打开对话框
  loadBindArticleList().then(() => {
    bindHomepageDialogOpen.value = true
  })
}

// 当下拉框显示时加载主文章列表
function handleArticleSelectVisible(visible) {
  if (visible && bindArticleList.value.length === 0) {
    loadBindArticleList()
  }
}

// 加载可绑定的主文章列表（使用关联模式）
function loadBindArticleList() {
  return new Promise((resolve, reject) => {
    loadingBindArticles.value = true
    
    const query = {
      pageNum: 1,
      pageSize: 1000,
      siteId: queryParams.value.siteId || currentSiteId.value,
      viewMode: 'related' // 使用关联模式获取所有可用主文章
    }
    
    listMasterArticle(query).then(response => {
      bindArticleList.value = response.rows || []
      resolve(bindArticleList.value)
    }).catch(error => {
      console.error('加载主文章列表失败:', error)
      proxy.$modal.msgError('加载主文章列表失败')
      reject(error)
    }).finally(() => {
      loadingBindArticles.value = false
    })
  })
}

// 提交主页绑定
function handleSubmitHomepageBinding() {
  if (!currentGameForBind.value) return
  
  const bindType = currentBindType.value
  const masterArticleId = currentBindTargetId.value
  
  // 如果选择解绑
  if (bindType === 'none') {
    if (!homepageBinding.value) {
      proxy.$modal.msgWarning('该游戏未绑定主页')
      return
    }
    
    proxy.$modal.confirm('确认要解除该游戏的主页绑定吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      binding.value = true
      const params = {
        masterArticleId: homepageBinding.value.masterArticleId
      }
      
      unbindGameHomepage(currentGameForBind.value.id, params).then(() => {
        proxy.$modal.msgSuccess('解绑成功')
        bindHomepageDialogOpen.value = false
        handlePreviewHomepages(currentGameForBind.value)
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
    
    bindGameHomepage(currentGameForBind.value.id, params).then(response => {
      // 检查是否需要确认
      if (response.needConfirm) {
        proxy.$modal.confirm(
          response.message || '该文章已绑定到其他游戏，是否要强制覆盖？',
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
          bindGameHomepage(currentGameForBind.value.id, forceParams).then(() => {
            proxy.$modal.msgSuccess('绑定成功')
            bindHomepageDialogOpen.value = false
            handlePreviewHomepages(currentGameForBind.value)
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
        handlePreviewHomepages(currentGameForBind.value)
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
  if (!currentGameForBind.value || !homepageBinding.value) return
  
  proxy.$modal.confirm(
    `确认解除游戏 "${currentGameForBind.value.name}" 的主页绑定吗？`,
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
    
    unbindGameHomepage(currentGameForBind.value.id, params).then(() => {
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

// 管理盒子关联
function handleManageBoxes(row) {
  currentGameIdForBoxes.value = row.id
  currentGameNameForBoxes.value = row.name
  boxRelationReadonly.value = viewMode.value === 'related'
  boxRelationSiteId.value = queryParams.value.siteId || null
  boxRelationDialogOpen.value = true
}

function loadBoxOptions(siteId) {
  // 加载该站点下的所有盒子（显示所有状态的盒子，包括正常和停用）
  const boxQuery = {
    pageNum: 1,
    pageSize: 10000
  }
  // 如果有 siteId，则过滤该站点的盒子
  if (siteId) {
    boxQuery.siteId = siteId
  }
  // 关联模式下需使用 related 查询，以包含关联盒子（共享/默认配置盒子），否则只能查到自建盒子
  if (viewMode.value === 'related' && siteId) {
    boxQuery.queryMode = 'related'
  }
  
  listBox(boxQuery).then(response => {
    allBoxes.value = response.rows || []
  })
}

function loadBoxesData(siteId) {
  loadBoxOptions(siteId)

  // 加载该游戏已关联的盒子
  if (!currentGameId.value) {
    selectedBoxIds.value = []
    originalBoxIds.value = []
    return
  }
  getBoxesByGameId(currentGameId.value).then(response => {
    const gameBoxes = response.data || []
    // API返回的是GbBoxGameRelation对象，使用boxId字段
    selectedBoxIds.value = gameBoxes.map(relation => relation.boxId)
    originalBoxIds.value = [...selectedBoxIds.value]
  })
}

function getGameRelatedBoxes(row) {
  if (!row?.boxIds) return []
  return row.boxIds
    .split(',')
    .map(id => Number(id))
    .filter(id => !Number.isNaN(id))
    .map(id => boxLookupMap.value.get(id))
    .filter(Boolean)
}

function filterBox(query, item) {
  return item.label.toLowerCase().includes(query.toLowerCase())
}

function cancelBoxes() {
  boxesOpen.value = false
  selectedBoxIds.value = []
  originalBoxIds.value = []
  currentGameId.value = null
  currentGameName.value = ""
}

// 管理网站关联
function handleManageSites(row) {
  currentGameIdForSites.value = row.id
  currentGameNameForSites.value = row.name
  currentGameCreatorSiteId.value = row.siteId || 0
  siteRelationDialogOpen.value = true
}

// 管理翻译
function handleManageTranslations(row) {
  currentTranslationGameId.value = row.id
  currentTranslationGameName.value = row.name
  currentTranslationGameData.value = {
    name: row.name,
    subtitle: row.subtitle,
    shortName: row.shortName,
    description: row.description,
    promotionDesc: row.promotionDesc,
    discountLabel: row.discountLabel
  }
  translationDialogOpen.value = true
}

// 批量翻译
async function handleBatchTranslate() {
  const selectedIds = effectiveIds.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgWarning('请先选择需要翻译的游戏')
    return
  }
  
  try {
    await proxy.$modal.confirm(`确认要为选中的 ${selectedIds.length} 个游戏生成翻译吗？`)
    
    // 准备要翻译的数据（跨页模式下只传entityId，不传fields，让后端自行查询）
    const allEntities = crossPageSelectActive.value
      ? selectedIds.map(id => ({ entityId: id, fields: {} }))
      : gameList.value
          .filter(game => selectedIds.includes(game.id))
          .map(game => ({
            entityId: game.id,
            fields: {
              name: game.name,
              subtitle: game.subtitle || '',
              shortName: game.shortName || '',
              description: game.description || '',
              promotionDesc: game.promotionDesc || '',
              discountLabel: game.discountLabel || ''
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
        await batchAutoTranslate('game', queryParams.siteId || 0, batches[i])
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
  if (crossPageSelectActive.value) {
    // 跨页全选模式：只用 ID，不加载逐条详情
    batchExcludeCrossPage.value = true
    selectedGamesForBatchExclude.value = crossPageIds.value.map(id => ({ id, name: '' }))
    gameExclusionDetails.value = []
    batchExcludedSiteIds.value = []
    batchExclusionDialogOpen.value = true
    return
  }
  batchExcludeCrossPage.value = false
  const selectedRows = gameList.value.filter(game => ids.value.includes(game.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理排除的游戏')
    return
  }
  
  // 只允许默认配置的游戏进行批量排除
  const invalidGames = selectedRows.filter(game => !isPersonalSite(game.siteId, siteList.value))
  if (invalidGames.length > 0) {
    proxy.$modal.msgWarning('只能对默认配置的游戏进行批量排除管理')
    return
  }
  
  selectedGamesForBatchExclude.value = selectedRows.map(game => ({
    id: game.id,
    name: game.name
  }))
  
  // 加载当前排除状态
  try {
    // 批量一次加载所有选中游戏的网站关系
    const batchRes = await getBatchGameSites(selectedGamesForBatchExclude.value.map(game => game.id))
    const batchMap = batchRes.data || {}
    const results = selectedGamesForBatchExclude.value.map(game => {
      const sites = batchMap[game.id] || []
      return {
        gameId: game.id,
        gameName: game.name,
        excludedSiteIds: sites.filter(site => site.relationType === 'exclude').map(site => site.siteId),
        includedSiteIds: sites.filter(site => site.relationType === 'include').map(site => site.siteId)
      }
    })
    
    // 存储详情供显示
    gameExclusionDetails.value = results
    
    // 统计每个网站被排除的次数
    const siteExcludeCount = new Map()
    results.forEach(result => {
      result.excludedSiteIds.forEach(siteId => {
        siteExcludeCount.set(siteId, (siteExcludeCount.get(siteId) || 0) + 1)
      })
    })
    
    // 找出被所有游戏共同排除的网站（交集），作为初始勾选状态
    const commonExcludedSites = []
    siteExcludeCount.forEach((count, siteId) => {
      if (count === selectedGamesForBatchExclude.value.length) {
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

function removeGameFromBatchExclude(gameId) {
  selectedGamesForBatchExclude.value = selectedGamesForBatchExclude.value.filter(
    game => game.id !== gameId
  )
  
  // 同时从外部表格的选中列表中移除
  ids.value = ids.value.filter(id => id !== gameId)
  
  // 更新 single 和 multiple 的状态
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  // 直接操作表格取消该行的选中状态
  if (gameTableRef.value) {
    const row = gameList.value.find(game => game.id === gameId)
    if (row) {
      gameTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  // 同时从排除详情中移除
  gameExclusionDetails.value = gameExclusionDetails.value.filter(
    detail => detail.gameId !== gameId
  )
  
  if (selectedGamesForBatchExclude.value.length === 0) {
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
  if (selectedGamesForBatchExclude.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何游戏')
    return
  }
  
  batchExclusionLoading.value = true
  
  try {
    // 一条请求处理所有选中游戏的排除关系
    await batchSaveGameSiteRelations({
      gameIds: selectedGamesForBatchExclude.value.map(g => g.id),
      excludeSiteIds: batchExcludedSiteIds.value
    })
    
    const excludeCount = batchExcludedSiteIds.value.length
    const msg = excludeCount > 0 
      ? `成功设置 ${selectedGamesForBatchExclude.value.length} 个游戏排除 ${excludeCount} 个网站`
      : `成功取消 ${selectedGamesForBatchExclude.value.length} 个游戏的所有排除设置`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载排除详情，刷新对话框内表格
    const refreshRes = await getBatchGameSites(selectedGamesForBatchExclude.value.map(game => game.id))
    const refreshMap = refreshRes.data || {}
    gameExclusionDetails.value = selectedGamesForBatchExclude.value.map(game => {
      const sites = refreshMap[game.id] || []
      return {
        gameId: game.id,
        gameName: game.name,
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
  if (crossPageSelectActive.value) {
    // 跨页全选模式：只用 ID，不加载逐条详情
    batchRelationCrossPage.value = true
    selectedGamesForBatchRelation.value = crossPageIds.value.map(id => ({ id, name: '' }))
    gameRelationDetails.value = []
    batchRelatedSiteIds.value = []
    batchRelationDialogOpen.value = true
    return
  }
  batchRelationCrossPage.value = false
  const selectedRows = gameList.value.filter(game => ids.value.includes(game.id))
  
  if (selectedRows.length === 0) {
    proxy.$modal.msgWarning('请先选择需要管理关联的游戏')
    return
  }
  
  const isDefaultConfig = isPersonalSite(queryParams.value.siteId, siteList.value)
  
  selectedGamesForBatchRelation.value = selectedRows.map(game => ({
    id: game.id,
    name: game.name,
    siteId: game.siteId
  }))
  
  // 加载当前关联状态
  try {
    const batchRes2 = await getBatchGameSites(selectedGamesForBatchRelation.value.map(game => game.id))
    const batchMap2 = batchRes2.data || {}
    const results = selectedGamesForBatchRelation.value.map(game => {
      const sites = batchMap2[game.id] || []
      return {
        gameId: game.id,
        gameName: game.name,
        relatedSiteIds: sites.filter(site => site.relationType === 'include' && site.siteId !== game.siteId).map(site => site.siteId),
        excludedSiteIds: isDefaultConfig ? sites.filter(site => site.relationType === 'exclude').map(site => site.siteId) : []
      }
    })
    gameRelationDetails.value = results
    
    // 找出被所有游戏共同关联的网站（交集），作为初始勾选状态
    const siteRelateCount = new Map()
    results.forEach(result => {
      result.relatedSiteIds.forEach(siteId => {
        siteRelateCount.set(siteId, (siteRelateCount.get(siteId) || 0) + 1)
      })
    })
    const commonRelatedSites = []
    siteRelateCount.forEach((count, siteId) => {
      if (count === selectedGamesForBatchRelation.value.length) {
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

/** 从批量关联中移除某个游戏 */
function removeGameFromBatchRelation(gameId) {
  selectedGamesForBatchRelation.value = selectedGamesForBatchRelation.value.filter(
    game => game.id !== gameId
  )
  
  ids.value = ids.value.filter(id => id !== gameId)
  single.value = ids.value.length != 1
  multiple.value = !ids.value.length
  
  if (gameTableRef.value) {
    const row = gameList.value.find(game => game.id === gameId)
    if (row) {
      gameTableRef.value.toggleRowSelection(row, false)
    }
  }
  
  gameRelationDetails.value = gameRelationDetails.value.filter(
    detail => detail.gameId !== gameId
  )
  
  if (selectedGamesForBatchRelation.value.length === 0) {
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
  if (selectedGamesForBatchRelation.value.length === 0) {
    proxy.$modal.msgWarning('没有选择任何游戏')
    return
  }
  
  batchRelationLoading.value = true
  
  try {
    // 一条请求处理所有选中游戏的关联关系
    await batchSaveGameSiteRelations({
      gameIds: selectedGamesForBatchRelation.value.map(g => g.id),
      includeSiteIds: batchRelatedSiteIds.value
    })
    
    const relateCount = batchRelatedSiteIds.value.length
    const msg = relateCount > 0
      ? `成功设置 ${selectedGamesForBatchRelation.value.length} 个游戏关联 ${relateCount} 个网站`
      : `成功取消 ${selectedGamesForBatchRelation.value.length} 个游戏的所有共享关联`
    
    proxy.$modal.msgSuccess(msg)
    getList()
    // 重新批量加载关联详情，刷新对话框内表格
    const refreshRes2 = await getBatchGameSites(selectedGamesForBatchRelation.value.map(game => game.id))
    const refreshMap2 = refreshRes2.data || {}
    gameRelationDetails.value = selectedGamesForBatchRelation.value.map(game => {
      const sites = refreshMap2[game.id] || []
      return {
        gameId: game.id,
        gameName: game.name,
        relatedSiteIds: sites.filter(s => s.relationType === 'include' && s.siteId !== game.siteId).map(s => s.siteId),
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


function submitBoxes() {
  // 找出需要添加游戏的盒子ID（新选中的盒子）
  const boxIdsToAdd = selectedBoxIds.value.filter(id => !originalBoxIds.value.includes(id))
  // 找出需要移除游戏的盒子ID（取消选中的盒子）
  const boxIdsToRemove = originalBoxIds.value.filter(id => !selectedBoxIds.value.includes(id))
  
  const promises = []
  
  // 为每个新增的盒子添加该游戏
  boxIdsToAdd.forEach(boxId => {
    promises.push(batchAddGames(boxId, [currentGameId.value]))
  })
  
  // 从每个移除的盒子中删除该游戏
  boxIdsToRemove.forEach(boxId => {
    promises.push(batchRemoveGames(boxId, [currentGameId.value]))
  })
  
  if (promises.length === 0) {
    proxy.$modal.msgInfo("没有需要更新的盒子")
    cancelBoxes()
    return
  }
  
  Promise.all(promises).then(() => {
    proxy.$modal.msgSuccess("盒子管理成功")
    cancelBoxes()
    getList()
  }).catch(err => {
    proxy.$modal.msgError("盒子管理失败：" + err.message)
  })
}

// 监听表单中的siteId变化，自动重新加载分类（对话框）
watch(() => form.value.siteId, (newSiteId, oldSiteId) => {
  // 只有在对话框已打开且是用户主动切换站点时才处理
  if (!open.value) return
  
  loadGameCategoriesForDialog(newSiteId)
  // 只有在用户主动切换站点时（不是初始化时）才清空分类
  if (oldSiteId !== undefined) {
    form.value.categoryId = undefined
    form.value.categoryIds = []
    form.value.primaryCategoryId = undefined
  }
})

// 监听查询表单的includeDefaultConfig变化，重新加载分类
watch(() => includeDefaultConfig.value, () => {
  loadGameCategoriesForQuery(queryParams.value.siteId)
})

// 分类选择变化处理
function handleCategoryChange(value) {
  // 如果取消选择了主分类，自动选择第一个分类为主分类
  if (form.value.primaryCategoryId && !value.includes(form.value.primaryCategoryId)) {
    form.value.primaryCategoryId = value.length > 0 ? value[0] : undefined
  }
  // 如果只有一个分类，自动设为主分类
  if (value.length === 1) {
    form.value.primaryCategoryId = value[0]
  }
}

// 根据分类ID获取分类名称
function getCategoryNameById(categoryId) {
  if (!categoryId) return '未分类'
  
  // 先从缓存中查找
  const cached = getCategoryFromCache(categoryId)
  if (cached) return cached.name
  
  // 从游戏分类树中查找
  const findInTree = (nodes) => {
    for (const node of nodes) {
      if (node.id === categoryId) {
        return node.name
      }
      if (node.children && node.children.length > 0) {
        const found = findInTree(node.children)
        if (found) return found
      }
    }
    return null
  }
  
  // 尝试从游戏分类树查找
  let result = findInTree(gameCategoryTreeOptions.value)
  if (result) return result
  
  // 尝试从导入分类树查找
  result = findInTree(importCategoryTreeOptions.value)
  return result || `分类 ${categoryId}`
}

// 根据分类ID获取分类图标
function getCategoryIconById(categoryId) {
  const findInTree = (nodes) => {
    for (const node of nodes) {
      if (node.id === categoryId) {
        return node.icon
      }
      if (node.children && node.children.length > 0) {
        const found = findInTree(node.children)
        if (found !== null) return found
      }
    }
    return null
  }
  return findInTree(gameCategoryTreeOptions.value)
}

// 导入功能相关
function handleImport() {
  importOpen.value = true
  importForm.value = {
    siteId: queryParams.value.siteId || currentSiteId.value,
    boxId: undefined,
    categoryIds: [],
    jsonData: ''
  }
  importPreview.value = null
  showFieldMapping.value = false
  currentPlatformMappings.value = []
  // 不再加载默认平台的字段映射，改为选择盒子后加载
  // 加载盒子列表和分类列表
  if (importForm.value.siteId !== undefined) {
    loadImportBoxList(importForm.value.siteId)
    loadImportCategoryList(importForm.value.siteId)
  }
}

// 加载指定盒子的字段映射说明
async function loadBoxFieldMappings(boxId) {
  if (!boxId) {
    currentPlatformMappings.value = []
    return
  }
  
  loadingFieldMappings.value = true
  try {
    // 调用新的基于盒子的API
    const { listFieldMappingByBoxId } = await import('@/api/gamebox/fieldMapping')
    const response = await listFieldMappingByBoxId(boxId)
    
    // 按 targetLocation 分组排序：主表 > 平台扩展 > 推广链接，组内按 sortOrder 排序
    currentPlatformMappings.value = (response.data || [])
      .sort((a, b) => {
        // 先按 targetLocation 分组排序
        const locationOrder = { 'main': 0, 'platform_data': 1, 'promotion_link': 2 }
        const orderA = locationOrder[a.targetLocation] !== undefined ? locationOrder[a.targetLocation] : 999
        const orderB = locationOrder[b.targetLocation] !== undefined ? locationOrder[b.targetLocation] : 999
        if (orderA !== orderB) return orderA - orderB
        // 同一组内按 sortOrder 排序
        return (a.sortOrder || 999) - (b.sortOrder || 999)
      })
      .map(m => ({
        sourceField: m.sourceField,
        targetField: m.targetField,
        targetLocation: m.targetLocation,
        locationLabel: m.targetLocation === 'main' ? '主表' : m.targetLocation === 'promotion_link' ? '推广链接' : '平台扩展',
        remark: m.remark || m.targetField,
        fieldType: m.fieldType,
        isRequired: m.isRequired === '1'
      }))
  } catch (error) {
    console.error('加载字段映射失败:', error)
    currentPlatformMappings.value = []
  } finally {
    loadingFieldMappings.value = false
  }
}

// 监听盒子切换，加载对应的字段映射
function handleBoxChange(boxId) {
  if (boxId) {
    loadBoxFieldMappings(boxId)
  }
}

/** 打开盒子配置管理对话框 */
function handleBoxConfig(row) {
  boxConfigDialogOpen.value = true
  loadingBoxConfigs.value = true
  gameBoxConfigs.value = []
  
  // 调用API获取游戏的所有盒子关联配置
  import('@/api/gamebox/box-game-relation').then(module => {
    module.getGameBoxRelations(row.id).then(response => {
      gameBoxConfigs.value = response.data || []
      loadingBoxConfigs.value = false
    }).catch(() => {
      proxy.$modal.msgError('获取盒子配置失败')
      loadingBoxConfigs.value = false
    })
  })
}

/** 编辑盒子配置 */
function handleEditBoxConfig(row) {
  boxConfigForm.value = {
    id: row.id,
    boxId: row.boxId,
    boxName: row.boxName,
    gameId: row.gameId,
    downloadUrl: row.downloadUrl,
    promoteUrl: row.promoteUrl,
    discountLabel: row.discountLabel,
    firstChargeDomestic: row.firstChargeDomestic,
    firstChargeOverseas: row.firstChargeOverseas,
    rechargeDomestic: row.rechargeDomestic,
    rechargeOverseas: row.rechargeOverseas,
    hasSupport: row.hasSupport || '0',
    supportDesc: row.supportDesc
  }
  editBoxConfigDialogOpen.value = true
}

/** 提交盒子配置 */
function submitBoxConfig() {
  import('@/api/gamebox/box-game-relation').then(module => {
    module.updateBoxGameRelation(boxConfigForm.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      editBoxConfigDialogOpen.value = false
      // 刷新盒子配置列表
      handleBoxConfig({ id: boxConfigForm.value.gameId })
    }).catch(() => {
      proxy.$modal.msgError('保存失败')
    })
  })
}

function handleImportSiteChange(siteId) {
  importForm.value.boxId = undefined
  importForm.value.categoryIds = []
  currentPlatformMappings.value = []
  if (siteId !== undefined && siteId !== null) {
    loadImportBoxList(siteId)
    loadImportCategoryList(siteId)
  } else {
    importBoxList.value = []
    importCategoryTreeOptions.value = []
  }
}

function loadImportBoxList(siteId) {
  // 使用关联模式查询，获取该网站可见的所有盒子（包括默认配置、共享盒子、自有盒子）
  const params = { siteId: siteId, status: '1', pageNum: 1, pageSize: 1000, queryMode: 'related' }
  listBox(params).then(response => {
    importBoxList.value = response.rows || []
  }).catch(() => {
    importBoxList.value = []
  })
}

function loadImportCategoryList(siteId) {
  if (!siteId) {
    importCategoryTreeOptions.value = []
    return
  }
  
  // 使用关联模式加载分类（包含自有+默认+共享）
  listCategory({ 
    categoryType: 'game', 
    siteId: siteId, 
    status: '1', 
    queryMode: 'related',
    pageNum: 1, 
    pageSize: 1000 
  }).then(response => {
    const categories = response.rows || []
    
    // 为分类添加站点来源标识和图标
    const processedCategories = categories.map(cat => {
      let sourceLabel = ''
      if (cat.relationSource === 'own') {
        sourceLabel = `[${getSiteName(siteId)}]`
      } else if (cat.relationSource === 'default') {
        sourceLabel = '[默认配置]'
      } else if (cat.relationSource === 'shared') {
        sourceLabel = '[共享]'
      }
      
      return {
        ...cat,
        name: `${cat.icon ? cat.icon + ' ' : ''}${cat.name} ${sourceLabel}`
      }
    })
    
    // 转换为树形结构
    importCategoryTreeOptions.value = handleTree(processedCategories, "id", "parentId")
  }).catch(() => {
    importCategoryTreeOptions.value = []
  })
}

function cancelImport() {
  importOpen.value = false
  importForm.value = {
    boxId: undefined,
    siteId: undefined,
    jsonData: ''
  }
  importPreview.value = null
  importing.value = false
}

// 解析预览：调用后端API获取转换后的数据，但不保存
function previewImportData() {
  if (!importForm.value.boxId) {
    proxy.$modal.msgWarning('请选择游戏盒子')
    return
  }
  
  if (!importForm.value.siteId) {
    proxy.$modal.msgWarning('请选择创建者网站')
    return
  }
  
  if (!importForm.value.jsonData) {
    proxy.$modal.msgWarning('请输入JSON数据')
    return
  }
  
  importing.value = true
  
  // 调用后端字段映射服务，后端负责 JSON 解析和字段映射，获取转换后的预览数据
  // 使用特殊参数 preview=true 让后端只返回转换结果，不实际保存
  importFromPlatform({
    boxId: importForm.value.boxId,
    siteId: importForm.value.siteId,
    jsonData: importForm.value.jsonData, // 传递原始 JSON 字符串，由后端解析
    preview: true // 预览模式
  }).then(response => {
    const result = response.data || {}
    const previewData = result.previewData || []
    const errorList = result.errorList || []
    const successCount = result.successCount || 0
    const errorCount = result.errorCount || 0
    
    if (previewData.length === 0) {
      if (errorList.length > 0) {
        const errorMsg = '数据解析失败：\n' + errorList.slice(0, 5).join('\n')
        proxy.$modal.msgError(errorMsg)
      } else {
        proxy.$modal.msgWarning('未能解析出有效的游戏数据，请检查字段映射配置')
      }
      importPreview.value = null
      return
    }
    
    // 处理预览数据
    importPreview.value = previewData.map(item => {
      return {
        ...item,
        promotionLinksCount: item.promotionLinksCount || 0
      }
    })
    
    let message = `成功解析 ${successCount} 个游戏`
    if (errorCount > 0) {
      message += `，${errorCount} 个失败`
      if (errorList.length > 0) {
        message += '\n\n失败详情：\n' + errorList.slice(0, 3).join('\n')
        if (errorList.length > 3) {
          message += `\n...还有 ${errorList.length - 3} 条`
        }
      }
    }
    message += '\n\n请确认数据后导入'
    
    proxy.$modal.msgSuccess(message)
  }).catch(error => {
    proxy.$modal.msgError('数据解析失败：' + (error.message || '未知错误'))
    importPreview.value = null
  }).finally(() => {
    importing.value = false
  })
}

// 直接导入：调用后端API，后端根据字段映射配置自动转换
function submitImportDirect() {
  if (!importPreview.value || importPreview.value.length === 0) {
    proxy.$modal.msgWarning('请先点击"解析预览"查看数据')
    return
  }
  
  if (!importForm.value.boxId) {
    proxy.$modal.msgWarning('请选择导入盒子')
    return
  }
  
  if (!importForm.value.siteId) {
    proxy.$modal.msgWarning('请选择创建者网站')
    return
  }
  
  if (!importForm.value.jsonData) {
    proxy.$modal.msgWarning('请输入JSON数据')
    return
  }
  
  // 确认导入
  const siteName = siteList.value.find(s => s.id === importForm.value.siteId)?.name || '未知网站'
  proxy.$modal.confirm(`确认导入 ${importPreview.value.length} 个游戏到【${siteName}】？`, '确认导入', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    importing.value = true
    
    // 调用后端API，传递原始 JSON 字符串，由后端负责解析和字段映射
    importFromPlatform({
      boxId: importForm.value.boxId,
      siteId: importForm.value.siteId,
      categoryIds: importForm.value.categoryIds || [],
      jsonData: importForm.value.jsonData,
      preview: false
    }).then(response => {
      const result = response.data || {}
      const successCount = result.successCount || 0
      const errorCount = result.errorCount || 0
      const totalCount = result.totalCount || importPreview.value.length || 0
      const skippedCount = result.skippedCount || 0
      const newCount = result.newCount || 0
      const reusedCount = result.reusedCount || 0
      const relationUpdatedCount = result.relationUpdatedCount || 0
      const successList = result.successList || []
      const reusedList = result.reusedList || []
      const skippedList = result.skippedList || []
      const errorList = result.errorList || []
      
      // 检查是否有失败的游戏
      if (errorCount > 0) {
        // 有失败，显示详细错误信息
        const failureItems = []
        errorList.forEach((msg, index) => {
          failureItems.push(h('div', { style: 'margin: 5px 0;' }, `${index + 1}. ${msg}`))
        })
        
        // 使用 h 函数创建 VNode
        const message = h('div', { style: 'text-align: left; line-height: 1.8;' }, [
          h('p', { style: 'margin-bottom: 10px; font-weight: bold;' }, '游戏导入部分失败！'),
          h('p', { style: 'margin-bottom: 10px;' }, `总计：${totalCount} 个    成功：${successCount} 个    跳过：${skippedCount} 个    失败：${errorCount} 个`),
          h('p', { style: 'margin-bottom: 10px; color: #606266;' }, `其中：新增游戏 ${newCount} 个，复用已有游戏并新增盒子关联 ${reusedCount} 个，更新已有关联 ${relationUpdatedCount} 个`),
          reusedList.length > 0 ? h('p', { style: 'margin-bottom: 8px; color: #409EFF;' }, `复用已有游戏 ${reusedList.length} 个（这部分不会增加游戏总条数）`) : null,
          failureItems.length > 0 ? h('p', { style: 'margin-bottom: 8px; font-weight: bold;' }, '失败详情：') : null,
          failureItems.length > 0 ? h('div', { style: 'margin-left: 10px; max-height: 300px; overflow-y: auto;' }, failureItems) : null,
          h('p', { style: 'margin-top: 10px; color: #E6A23C;' }, '成功导入的游戏已保存，请检查失败原因后重试。')
        ])
        
        // 使用 ElMessageBox 直接调用
        ElMessageBox.confirm(message, '导入结果', {
          confirmButtonText: '我知道了',
          showCancelButton: false,
          type: 'warning'
        }).catch(() => {})
      } else if (skippedCount > 0) {
        // 无失败但有跳过，展示跳过原因
        const skippedItems = []
        skippedList.slice(0, 100).forEach((msg, index) => {
          skippedItems.push(h('div', { style: 'margin: 5px 0;' }, `${index + 1}. ${msg}`))
        })

        const message = h('div', { style: 'text-align: left; line-height: 1.8;' }, [
          h('p', { style: 'margin-bottom: 10px; font-weight: bold;' }, '导入完成（含跳过项）'),
          h('p', { style: 'margin-bottom: 10px;' }, `总计：${totalCount} 个    成功：${successCount} 个    跳过：${skippedCount} 个    失败：${errorCount} 个`),
          h('p', { style: 'margin-bottom: 10px; color: #606266;' }, `其中：新增游戏 ${newCount} 个，复用已有游戏并新增盒子关联 ${reusedCount} 个，更新已有关联 ${relationUpdatedCount} 个`),
          h('p', { style: 'margin-bottom: 8px; font-weight: bold;' }, '跳过原因：'),
          h('div', { style: 'margin-left: 10px; max-height: 300px; overflow-y: auto;' }, skippedItems),
          skippedList.length > 100 ? h('p', { style: 'margin-top: 8px; color: #909399;' }, `仅展示前100条，剩余 ${skippedList.length - 100} 条请查看数据修改历史`) : null
        ])

        ElMessageBox.confirm(message, '导入结果', {
          confirmButtonText: '我知道了',
          showCancelButton: false,
          type: 'info'
        }).catch(() => {})
      } else {
        // 全部导入成功
        proxy.$modal.msgSuccess(`导入完成！总计 ${totalCount} 个，新增游戏 ${newCount} 个，复用已有游戏并新增盒子关联 ${reusedCount} 个，更新已有关联 ${relationUpdatedCount} 个，跳过 ${skippedCount} 个，失败 ${errorCount} 个`)
      }
      
      // 关闭对话框并刷新列表
      importOpen.value = false
      cancelImport()
      getList()
    }).catch(error => {
      proxy.$modal.msgError('导入失败：' + (error.message || '未知错误'))
    }).finally(() => {
      importing.value = false
    })
  }).catch(() => {})
}

// 保留旧的parseImportData函数（暂时不删除，以防有其他地方调用）
function parseImportData() {
  if (!importForm.value.jsonData) {
    proxy.$modal.msgWarning('请输入JSON数据')
    return
  }
  
  try {
    const jsonData = JSON.parse(importForm.value.jsonData)
    
    // 支持两种格式：1. 直接是游戏对象数组 2. items字段包含游戏数组 3. 单个游戏对象
    let gameItems = []
    if (Array.isArray(jsonData)) {
      gameItems = jsonData
    } else if (jsonData.items && Array.isArray(jsonData.items)) {
      gameItems = jsonData.items
    } else if (jsonData.gamename) {
      gameItems = [jsonData]
    } else {
      proxy.$modal.msgError('JSON数据格式错误：无法识别游戏数据')
      return
    }
    
    if (gameItems.length === 0) {
      proxy.$modal.msgError('未找到可导入的游戏数据')
      return
    }
    
    // 将每个游戏对象映射到系统字段
    importPreview.value = gameItems.map(item => {
      // 处理设备类型
      let deviceSupport = 'both'
      if (item.device_type === 0 || item.device_type === '0') {
        deviceSupport = 'android'
      } else if (item.device_type === 1 || item.device_type === '1') {
        deviceSupport = 'ios'
      } else if (item.device_type === 2 || item.device_type === '2') {
        deviceSupport = 'both'
      }
      
      // 处理截图数组
      let screenshots = ''
      if (item.photo && Array.isArray(item.photo)) {
        const urls = item.photo.map(p => p.url).filter(url => url)
        screenshots = JSON.stringify(urls)
      }
      
      // 处理标签
      let tags = ''
      if (item.tags) {
        if (Array.isArray(item.tags)) {
          tags = item.tags.join(',')
        } else if (typeof item.tags === 'string') {
          tags = item.tags
        }
      }
      
      // 处理游戏类型：根据 edition 字段判断
      let gameType = 'official' // 默认官方
      
      if (item.edition === 0 || item.edition === '0') {
        gameType = 'official'
      } else if (item.edition === 1 || item.edition === '1') {
        gameType = 'discount'
      } else if (item.edition === 2 || item.edition === '2') {
        gameType = 'bt'
      }
      // 判断是否即将上线
      else if (item.yuyue === 1 || item.yuyue === '1') {
        gameType = 'coming'
      }
      
      // 从copy字段提取安卓和iOS下载链接
      let androidDownloadUrl = ''
      let iosDownloadUrl = ''
      if (item.copy && typeof item.copy === 'string') {
        const androidMatch = item.copy.match(/安卓下载地址[:：]\s*(https?:\/\/[^\s\n]+)/i)
        const iosMatch = item.copy.match(/苹果下载地址[:：]\s*(https?:\/\/[^\s\n]+)/i)
        if (androidMatch) androidDownloadUrl = androidMatch[1]
        if (iosMatch) iosDownloadUrl = iosMatch[1]
      }
      
      const result = {
        name: item.gamename || '',
        subtitle: '',
        shortName: '',
        // category 字段不应该存在于游戏表中，分类关系通过关联表维护
        // item.gametype 保留在 rawData 中用于分类匹配
        gameType: gameType,
        iconUrl: item.pic1 || '',
        coverUrl: item.pic2 || item.pic3 || '',
        screenshots: screenshots,
        videoUrl: item.video || '',
        description: item.excerpt || '',
        promotionDesc: item.text_cps || item.promotion || '',
        developer: item.game_source || item.developer || '',
        publisher: item.publisher || '',
        packageName: item.package_name || item.package || '',
        version: item.version || '',
        size: item.size || item.file_size || '',
        deviceSupport: deviceSupport,
        downloadCount: item.download_count || item.downloads || 0,
        rating: item.rating || item.score || null,
        features: item.features ? (Array.isArray(item.features) ? JSON.stringify(item.features) : item.features) : '',
        tags: tags,
        launchTime: item.launch_time || item.release_time || item.addtime || null,
        isNew: item.new === 1 || item.new === '1' || item.is_new === 1 || item.is_new === '1' ? '1' : '0',
        isHot: item.hot === 1 || item.hot === '1' || item.is_hot === 1 || item.is_hot === '1' ? '1' : '0',
        isRecommend: item.recommend === 1 || item.recommend === '1' || item.is_recommend === 1 || item.is_recommend === '1' ? '1' : '0',
        status: '1',
        sortOrder: item.sort || item.sort_order || 0,
        // 下载链接（关联数据）
        downloadUrl: item.downurl || item.download_url || item.downloadUrl || '',
        androidUrl: androidDownloadUrl || item.android_url || item.androidUrl || '',
        iosUrl: iosDownloadUrl || item.ios_url || item.iosUrl || '',
        promoteUrl: item.dwzdownurl || item.dwzdownurlmash || item.promote_url || item.promoteUrl || '',
        rawData: item
      }
      
      return result
    })
    
    // 构建分类匹配缓存
    if (importForm.value.autoMatchCategory && importCategoryTreeOptions.value.length > 0) {
      categoryMatchCache.value.clear()
      
      // 递归遍历分类树，建立字段值到分类ID的映射
      const buildCategoryCache = (categories, isTopLevel = true) => {
        for (const cat of categories) {
          // 跳过板块（顶级分类），只匹配板块下的具体分类
          // isSection='1' 表示是板块，或者顶级且有子分类的也视为板块
          const isSection = cat.isSection === '1' || cat.isSection === 1 || 
                           (isTopLevel && cat.children && cat.children.length > 0)
          
          if (!isSection) {
            // 只有非板块的分类才加入匹配缓存
            // 清理分类名：去除空格、图标、站点标识等
            let cleanName = cat.name
              .replace(/\[[^\]]+\]/g, '') // 移除 [默认配置] [网站名] 等标识
              .replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '') // 只保留中文、字母、数字
              .toLowerCase()
            
            if (cleanName) {
              if (!categoryMatchCache.value.has(cleanName)) {
                categoryMatchCache.value.set(cleanName, [])
              }
              categoryMatchCache.value.get(cleanName).push(cat.id)
            }
          }
          
          // 递归处理子分类
          if (cat.children && cat.children.length > 0) {
            buildCategoryCache(cat.children, false)
          }
        }
      }
      
      buildCategoryCache(importCategoryTreeOptions.value, true)
    }
    
    // 为每个游戏单独匹配分类
    if (importForm.value.autoMatchCategory) {
      const matchField = importForm.value.categoryMatchField
      let totalMatched = 0
      
      importPreview.value = importPreview.value.map(game => {
        const fieldValue = game.rawData[matchField]
        
        if (fieldValue) {
          // 清理游戏类型字段值：去除特殊字符，只保留中文、字母、数字
          const valueClean = String(fieldValue)
            .replace(/[^\u4e00-\u9fa5a-zA-Z0-9]/g, '')
            .toLowerCase()
          
          if (!valueClean) {
            return game
          }
          
          // 在缓存中查找匹配的分类
          const matchedCategoryIds = []
          for (const [key, ids] of categoryMatchCache.value.entries()) {
            // 精确匹配或包含匹配
            if (key === valueClean || key.includes(valueClean) || valueClean.includes(key)) {
              matchedCategoryIds.push(...ids)
            }
          }
          
          if (matchedCategoryIds.length > 0) {
            totalMatched++
            return {
              ...game,
              autoMatchedCategoryIds: [...new Set(matchedCategoryIds)] // 去重
            }
          }
        }
        return game
      })
      
      if (totalMatched > 0) {
        proxy.$modal.msgSuccess(`已为 ${totalMatched} 个游戏自动匹配分类`)
      } else {
        proxy.$modal.msgWarning('未能自动匹配任何游戏的分类，请检查分类名称是否与数据字段匹配')
      }
    }
    
    // 检测重复游戏（同站点下同名游戏）
    if (importForm.value.siteId !== undefined && importForm.value.siteId !== null) {
      const params = {
        siteId: importForm.value.siteId,
        pageNum: 1,
        pageSize: 10000
      }
      
      listGame(params).then(response => {
        const existingGames = response.rows || []
        const normalizeCompareValue = (val) => {
          if (val === null || val === undefined) return ''
          return String(val).trim().toLowerCase()
        }

        const buildDuplicateKey = (game) => {
          const name = normalizeCompareValue(game?.name)
          const subtitle = normalizeCompareValue(game?.subtitle)
          const gameType = normalizeCompareValue(game?.gameType)
          return `${name}__${subtitle}__${gameType}`
        }

        const existingGameMap = new Map()
        existingGames.forEach(g => {
          const key = buildDuplicateKey(g)
          if (!existingGameMap.has(key)) {
            existingGameMap.set(key, g)
          }
        })
        
        // 标记重复的游戏
        let duplicateCount = 0
        importPreview.value = importPreview.value.map(game => {
          const key = buildDuplicateKey(game)
          const existingGame = existingGameMap.get(key)
          const isDuplicate = !!existingGame
          if (isDuplicate) {
            duplicateCount++
            return {
              ...game,
              isDuplicate: true,
              existingGameId: existingGame?.id,
              duplicateWarning: `同站点下已存在相同游戏（名称+副标题+类型）(ID: ${existingGame?.id})`
            }
          }
          return { ...game, isDuplicate: false }
        })
        
        if (duplicateCount > 0) {
          duplicateGamesList.value = importPreview.value.filter(g => g.isDuplicate)
          strategyDialogVisible.value = true
        } else {
          proxy.$modal.msgSuccess(`数据解析成功，找到 ${importPreview.value.length} 个游戏`)
        }
      }).catch(() => {
        // 如果检测失败，仍然显示解析成功
        proxy.$modal.msgSuccess(`数据解析成功，找到 ${importPreview.value.length} 个游戏`)
      })
    } else {
      proxy.$modal.msgSuccess(`数据解析成功，找到 ${importPreview.value.length} 个游戏`)
    }
  } catch (error) {
    proxy.$modal.msgError('JSON解析失败：' + error.message)
    importPreview.value = null
  }
}

// 确认策略选择
function confirmStrategy() {
  strategyDialogVisible.value = false
  const strategyNames = {
    merge: '合并到旧游戏（不创建重复游戏）',
    keep: '仅处理新游戏（新游戏仅获得新分类）',
    both: '新旧都处理（新游戏继承旧分类+新分类）'
  }
  proxy.$modal.msgSuccess(`已选择：${strategyNames[duplicateHandleStrategy.value]}\n\n数据解析成功，找到 ${importPreview.value.length} 个游戏`)
}

function submitImport() {
  if (!importPreview.value || importPreview.value.length === 0) {
    proxy.$modal.msgWarning('请先解析数据')
    return
  }
  
  if (!importForm.value.siteId) {
    proxy.$modal.msgWarning('请选择创建者网站')
    return
  }
  
  const duplicateGames = importPreview.value.filter(g => g.isDuplicate)
  const newGames = importPreview.value.filter(g => !g.isDuplicate)
  
  // 根据策略处理
  if (duplicateHandleStrategy.value === 'merge') {
    // 策略一：合并到旧游戏（不创建重复游戏，只为已存在的旧游戏追加分类）
    handleMergeStrategy(newGames, duplicateGames)
  } else if (duplicateHandleStrategy.value === 'keep') {
    // 策略二：新游戏作为主数据，迁移旧游戏的关联数据
    handleReplaceStrategy(newGames, duplicateGames)
  } else {
    // 策略三：创建独立的重复游戏，不关联盒子不追加分类
    handleBothStrategy(importPreview.value, duplicateGames)
  }
}

// 辅助函数：创建盒子-游戏关联（包含下载链接等关联数据）
async function addBoxGameRelation(boxId, gameId, gameData) {
  // 先检查该游戏是否已关联到此盒子
  try {
    const existingRelations = await getBoxesByGameId(gameId)
    const existingRelation = existingRelations.data?.find(rel => rel.boxId === boxId)
    
    const relationData = {
      boxId: boxId,
      gameId: gameId,
      sortOrder: 0
    }
    
    // 添加关联数据（下载链接等）
    if (gameData.downloadUrl) relationData.downloadUrl = gameData.downloadUrl
    if (gameData.androidUrl) relationData.androidUrl = gameData.androidUrl
    if (gameData.iosUrl) relationData.iosUrl = gameData.iosUrl
    if (gameData.promoteUrl) relationData.promoteUrl = gameData.promoteUrl
    
    if (existingRelation) {
      // 如果已存在，更新关联数据
      relationData.id = existingRelation.id
      return await updateBoxGame(relationData)
    } else {
      // 如果不存在，创建新关联
      return await addBoxGame(relationData)
    }
  } catch (error) {
    // 如果查询失败，直接尝试创建（可能是第一次关联）
    const relationData = {
      boxId: boxId,
      gameId: gameId,
      sortOrder: 0
    }
    
    if (gameData.downloadUrl) relationData.downloadUrl = gameData.downloadUrl
    if (gameData.androidUrl) relationData.androidUrl = gameData.androidUrl
    if (gameData.iosUrl) relationData.iosUrl = gameData.iosUrl
    if (gameData.promoteUrl) relationData.promoteUrl = gameData.promoteUrl
    
    return await addBoxGame(relationData)
  }
}

// 策略一：合并到旧游戏
async function handleMergeStrategy(newGames, duplicateGames) {
  try {
    const hasBoxRelation = !!importForm.value.boxId
    
    // 显示进度
    const loading = proxy.$loading({
      lock: true,
      text: `正在导入游戏，请稍候...（共 ${newGames.length + duplicateGames.length} 个）`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      // 1. 批量创建非重复的新游戏
      let newGameIds = []
      if (newGames.length > 0) {
        loading.setText('正在批量创建新游戏...')
        const batchData = newGames.map(gameData => buildGameSubmitData(gameData, false, hasBoxRelation))
        
        try {
          const response = await batchCreateGames(batchData)
          // 批量接口返回的是游戏ID数组
          newGameIds = response.data || []
          console.log(`成功创建 ${newGameIds.length} 个新游戏`)
        } catch (error) {
          console.error('批量创建游戏失败:', error)
          proxy.$modal.msgError('批量创建游戏失败，请重试')
          loading.close()
          return
        }
      }
      
      loading.setText('正在设置分类关联...')
      
      // 2. 汇总所有需要保存分类的数据
      const baseCategoryRelations = importForm.value.categoryIds && importForm.value.categoryIds.length > 0 
        ? importForm.value.categoryIds.map((catId, index) => ({
            categoryId: catId,
            isPrimary: index === 0 ? '1' : '0'
          }))
        : []
      
      // 准备批量保存的数据结构：[{gameId, categories: []}]
      const batchSaveData = []
      const batchAppendData = []
      
      // 新游戏：保存分类（覆盖）
      for (let i = 0; i < newGames.length; i++) {
        const game = newGames[i]
        const gameId = newGameIds[i]
        if (!gameId) continue
        
        // 合并基础分类和自动匹配的分类
        const gameCategoryIds = [...importForm.value.categoryIds]
        if (game.autoMatchedCategoryIds && game.autoMatchedCategoryIds.length > 0) {
          gameCategoryIds.push(...game.autoMatchedCategoryIds)
        }
        
        // 去重
        const uniqueCategoryIds = [...new Set(gameCategoryIds)]
        
        if (uniqueCategoryIds.length > 0) {
          const categoryRelations = uniqueCategoryIds.map((catId, index) => ({
            categoryId: catId,
            isPrimary: index === 0 ? '1' : '0'
          }))
          
          batchSaveData.push({
            gameId: gameId,
            categories: categoryRelations
          })
        }
      }
      
      // 旧游戏：追加分类（合并）
      if (baseCategoryRelations.length > 0) {
        const oldGameIds = duplicateGames.map(g => g.existingGameId).filter(id => id)
        for (const gameId of oldGameIds) {
          batchAppendData.push({
            gameId: gameId,
            categories: baseCategoryRelations
          })
        }
      }
      
      // 批量提交到后端（一次性执行）
      const categoryPromises = []
      if (batchSaveData.length > 0) {
        categoryPromises.push(batchSaveGameCategories(batchSaveData))
      }
      if (batchAppendData.length > 0) {
        categoryPromises.push(batchAppendGameCategories(batchAppendData))
      }
      
      if (categoryPromises.length > 0) {
        await Promise.all(categoryPromises).catch(error => {
          console.error('批量保存分类失败:', error)
        })
      }
      
      loading.setText('正在关联游戏盒子...')
      
      // 3. 汇总所有需要关联盒子的数据
      if (importForm.value.boxId) {
        const gameRelations = []
        
        // 新游戏关联盒子
        for (let i = 0; i < newGames.length; i++) {
          if (!newGameIds[i]) continue
          const game = newGames[i]
          gameRelations.push({
            gameId: newGameIds[i],
            downloadUrl: game.downloadUrl || '',
            androidUrl: game.androidUrl || '',
            iosUrl: game.iosUrl || '',
            promoteUrl: game.promoteUrl || '',
            displayOrder: 0
          })
        }
        
        // 旧游戏关联盒子
        for (const duplicateGame of duplicateGames) {
          gameRelations.push({
            gameId: duplicateGame.existingGameId,
            downloadUrl: duplicateGame.downloadUrl || '',
            androidUrl: duplicateGame.androidUrl || '',
            iosUrl: duplicateGame.iosUrl || '',
            promoteUrl: duplicateGame.promoteUrl || '',
            displayOrder: 0
          })
        }
        
        // 批量提交到后端（一次性执行）
        if (gameRelations.length > 0) {
          try {
            await batchAddGamesWithRelations(importForm.value.boxId, gameRelations)
            console.log(`成功关联 ${gameRelations.length} 个游戏到盒子`)
          } catch (error) {
            console.error('批量关联盒子失败:', error)
          }
        }
      }
      
      loading.close()
      
      // 5. 显示结果
      const messageLines = [
        `成功导入 ${newGameIds.length} 个新游戏，${duplicateGames.length} 个重复游戏已合并到旧游戏。`
      ]
      const totalBaseCats = importForm.value.categoryIds?.length || 0
      const matchedCount = newGames.filter(g => g.autoMatchedCategoryIds && g.autoMatchedCategoryIds.length > 0).length
      if (totalBaseCats > 0) {
        messageLines.push(`✓ 已为游戏添加 ${totalBaseCats} 个基础分类`)
      }
      if (matchedCount > 0) {
        messageLines.push(`✓ 为 ${matchedCount} 个游戏自动匹配了分类`)
      }
      if (importForm.value.boxId) {
        messageLines.push(`✓ 已关联到盒子`)
      }
      proxy.$modal.msgSuccess(messageLines.join('\n'))
      cancelImport()
      getList()
    } catch (error) {
      loading.close()
      throw error
    }
  } catch (error) {
    proxy.$modal.msgError('导入失败：' + (error.message || '未知错误'))
  }
}

// 策略二：新游戏作为主数据，迁移旧游戏的关联数据
async function handleReplaceStrategy(newGames, duplicateGames) {
  const loading = proxy.$loading({
    lock: true,
    text: `正在导入游戏，请稍候...（共 ${newGames.length + duplicateGames.length} 个）`,
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    const hasBoxRelation = !!importForm.value.boxId
    
    // 1. 批量创建所有新游戏（非重复的）
    let newGameIds = []
    if (newGames.length > 0) {
      loading.setText('正在批量创建新游戏...')
      const batchData = newGames.map(gameData => buildGameSubmitData(gameData, false, hasBoxRelation))
      
      try {
        const response = await batchCreateGames(batchData)
        newGameIds = response.data || []
        console.log(`成功创建 ${newGameIds.length} 个新游戏`)
      } catch (error) {
        console.error('批量创建游戏失败:', error)
        proxy.$modal.msgError('批量创建游戏失败，请重试')
        loading.close()
        return
      }
    }
    
    loading.setText('正在处理重复游戏...')
    
    // 2. 创建重复游戏对应的新游戏（用于替换旧游戏），使用旧游戏的关联数据作为游戏主数据
    const duplicateNewGames = []
    for (const duplicateGame of duplicateGames) {
      const oldGameId = duplicateGame.existingGameId
      
      try {
        // 获取旧游戏的盒子关联数据（下载链接等）
        const oldBoxes = await getBoxesByGameId(oldGameId)
        let relationData = null
        
        if (oldBoxes.data && oldBoxes.data.length > 0) {
          // 使用第一个盒子的关联数据作为游戏主数据
          relationData = oldBoxes.data[0]
        }
        
        // 创建新游戏，将旧的关联数据合并为游戏主数据
        const newGameData = buildGameSubmitData(duplicateGame, false, hasBoxRelation)
        
        // 如果从关联表中获取到了数据，且当前没有盒子关联，则使用关联表的数据
        if (relationData && !hasBoxRelation) {
          if (relationData.downloadUrl) newGameData.downloadUrl = relationData.downloadUrl
          if (relationData.androidUrl) newGameData.androidUrl = relationData.androidUrl
          if (relationData.iosUrl) newGameData.iosUrl = relationData.iosUrl
          if (relationData.promoteUrl) newGameData.promoteUrl = relationData.promoteUrl
        }
        
        const response = await addGame(newGameData)
        const newGameId = response.data?.id || response.data
        
        duplicateNewGames.push({
          newGameId,
          oldGameId,
          gameData: duplicateGame,
          oldBoxRelations: oldBoxes.data || []
        })
      } catch (error) {
        console.error(`创建替换游戏失败:`, error)
      }
    }
    
    // 3. 准备基础分类关联数据
    const baseCategoryRelations = importForm.value.categoryIds && importForm.value.categoryIds.length > 0 
      ? importForm.value.categoryIds.map((catId, index) => ({
          categoryId: catId,
          isPrimary: index === 0 ? '1' : '0'
        }))
      : []
    
    // 4. 为非重复的新游戏添加分类（基础分类 + 自动匹配的分类）
    for (let i = 0; i < newGames.length; i++) {
      const game = newGames[i]
      const gameId = newGameIds[i]
      
      // 合并基础分类和自动匹配的分类
      const gameCategoryIds = [...importForm.value.categoryIds]
      if (game.autoMatchedCategoryIds && game.autoMatchedCategoryIds.length > 0) {
        gameCategoryIds.push(...game.autoMatchedCategoryIds)
      }
      
      // 去重
      const uniqueCategoryIds = [...new Set(gameCategoryIds)]
      
      if (uniqueCategoryIds.length > 0) {
        const categoryRelations = uniqueCategoryIds.map((catId, index) => ({
          categoryId: catId,
          isPrimary: index === 0 ? '1' : '0'
        }))
        
        try {
          await saveGameCategories(gameId, categoryRelations)
        } catch (error) {
          console.error(`保存游戏 ${gameId} 的分类关联失败:`, error)
        }
      }
    }
    
    // 5. 处理重复游戏：为新游戏添加分类、迁移旧游戏的盒子关联，然后删除旧游戏
    for (const item of duplicateNewGames) {
      const { newGameId, oldGameId, gameData, oldBoxRelations } = item
      
      if (!newGameId || !oldGameId) continue
      
      try {
        // 先为新游戏添加基础分类和自动匹配的分类
        const gameCategoryIds = [...importForm.value.categoryIds]
        if (gameData.autoMatchedCategoryIds && gameData.autoMatchedCategoryIds.length > 0) {
          gameCategoryIds.push(...gameData.autoMatchedCategoryIds)
        }
        
        // 获取旧游戏的分类并追加到新游戏
        const oldCategories = await getGameCategories(oldGameId)
        if (oldCategories.data && oldCategories.data.length > 0) {
          oldCategories.data.forEach(cat => {
            if (!gameCategoryIds.includes(cat.categoryId)) {
              gameCategoryIds.push(cat.categoryId)
            }
          })
        }
        
        // 去重并保存分类
        const uniqueCategoryIds = [...new Set(gameCategoryIds)]
        if (uniqueCategoryIds.length > 0) {
          const categoryRelations = uniqueCategoryIds.map((catId, index) => ({
            categoryId: catId,
            isPrimary: index === 0 ? '1' : '0'
          }))
          await saveGameCategories(newGameId, categoryRelations)
        }
        
        // 迁移旧游戏的所有盒子关联到新游戏
        for (const boxRelation of oldBoxRelations) {
          try {
            await addBoxGameRelation(boxRelation.boxId, newGameId, gameData)
          } catch (error) {
            console.error(`迁移盒子关联失败:`, error)
          }
        }
        
        // 删除旧游戏
        await delGame(oldGameId)
      } catch (error) {
        console.error(`迁移游戏 ${oldGameId} 的数据失败:`, error)
      }
    }
    
    // 6. 如果选择了盒子，将所有新游戏关联到盒子
    if (importForm.value.boxId) {
      // 非重复的新游戏
      for (let i = 0; i < newGames.length; i++) {
        try {
          await addBoxGameRelation(importForm.value.boxId, newGameIds[i], newGames[i])
        } catch (error) {
          console.error(`关联游戏到盒子失败:`, error)
        }
      }
      
      // 替换后的新游戏
      for (const item of duplicateNewGames) {
        try {
          await addBoxGameRelation(importForm.value.boxId, item.newGameId, item.gameData)
        } catch (error) {
          console.error(`关联游戏到盒子失败:`, error)
        }
      }
    }
    
    // 7. 显示结果
    const allNewGameCount = newGames.length + duplicateNewGames.length
    const messageLines = [
      `成功导入 ${allNewGameCount} 个游戏${duplicateNewGames.length > 0 ? `，替换了 ${duplicateNewGames.length} 个旧游戏` : ''}。`
    ]
    const totalBaseCats = importForm.value.categoryIds?.length || 0
    const matchedCount = [...newGames, ...duplicateGames].filter(g => g.autoMatchedCategoryIds && g.autoMatchedCategoryIds.length > 0).length
    
    if (totalBaseCats > 0) {
      messageLines.push(`✓ 已为新游戏添加 ${totalBaseCats} 个基础分类`)
    }
    if (matchedCount > 0) {
      messageLines.push(`✓ 为 ${matchedCount} 个游戏自动匹配了分类`)
    }
    if (duplicateNewGames.length > 0) {
      messageLines.push(`✓ 已将 ${duplicateNewGames.length} 个旧游戏的关联数据迁移到新游戏`)
    }
    if (importForm.value.boxId) {
      messageLines.push(`✓ 已关联 ${allNewGameCount} 个游戏到盒子`)
    }
    
    loading.close()
    proxy.$modal.msgSuccess(messageLines.join('\n'))
    cancelImport()
    getList()
  } catch (error) {
    loading.close()
    proxy.$modal.msgError('导入失败：' + (error.message || '未知错误'))
  }
}

// 策略三：创建独立的重复游戏（像首次导入一样处理）
async function handleBothStrategy(allGames, duplicateGames) {
  const loading = proxy.$loading({
    lock: true,
    text: `正在导入游戏，请稍候...（共 ${allGames.length} 个）`,
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    const hasBoxRelation = !!importForm.value.boxId
    
    // 1. 批量创建所有游戏（包括重复的），重复的标记为冲突
    let gameIds = []
    if (allGames.length > 0) {
      loading.setText('正在批量创建游戏...')
      const batchData = allGames.map(gameData => buildGameSubmitData(gameData, gameData.isDuplicate, hasBoxRelation))
      
      try {
        const response = await batchCreateGames(batchData)
        gameIds = response.data || []
        console.log(`成功创建 ${gameIds.length} 个游戏`)
      } catch (error) {
        console.error('批量创建游戏失败:', error)
        proxy.$modal.msgError('批量创建游戏失败，请重试')
        loading.close()
        return
      }
    }
    
    loading.setText('正在设置分类关联...')
    
    // 2. 汇总所有需要保存分类的数据
    const batchSaveData = []
    
    for (let i = 0; i < allGames.length; i++) {
      const game = allGames[i]
      const gameId = gameIds[i]
      if (!gameId) continue
      
      // 合并基础分类和自动匹配的分类
      const gameCategoryIds = [...importForm.value.categoryIds]
      if (game.autoMatchedCategoryIds && game.autoMatchedCategoryIds.length > 0) {
        gameCategoryIds.push(...game.autoMatchedCategoryIds)
      }
      
      // 去重
      const uniqueCategoryIds = [...new Set(gameCategoryIds)]
      
      if (uniqueCategoryIds.length > 0) {
        const categoryRelations = uniqueCategoryIds.map((catId, index) => ({
          categoryId: catId,
          isPrimary: index === 0 ? '1' : '0'
        }))
        
        batchSaveData.push({
          gameId: gameId,
          categories: categoryRelations
        })
      }
    }
    
    // 3. 批量保存分类关联
    if (batchSaveData.length > 0) {
      try {
        await batchSaveGameCategories(batchSaveData)
        console.log(`成功保存 ${batchSaveData.length} 个游戏的分类关联`)
      } catch (error) {
        console.error('批量保存分类关联失败:', error)
      }
    }
    
    loading.setText('正在关联游戏盒子...')
    
    // 4. 批量关联盒子：所有游戏都关联，就像首次导入一样
    if (importForm.value.boxId) {
      const batchRelationData = []
      
      for (let i = 0; i < allGames.length; i++) {
        if (!gameIds[i]) continue
        
        const game = allGames[i]
        const relationData = {
          gameId: gameIds[i],
          downloadUrl: game.downloadUrl || '',
          androidUrl: game.androidUrl || '',
          iosUrl: game.iosUrl || '',
          promoteUrl: game.promoteUrl || '',
          displayOrder: 0
        }
        
        batchRelationData.push(relationData)
      }
      
      if (batchRelationData.length > 0) {
        try {
          await batchAddGamesWithRelations(importForm.value.boxId, batchRelationData)
          console.log(`成功关联 ${batchRelationData.length} 个游戏到盒子`)
        } catch (error) {
          console.error('批量关联游戏到盒子失败:', error)
        }
      }
    }
    
    loading.close()
    
    // 5. 显示结果
    const newGamesCount = allGames.filter(g => !g.isDuplicate).length
    const messageLines = [
      `成功导入 ${gameIds.length} 个游戏${duplicateGames.length > 0 ? `（含 ${duplicateGames.length} 个重复游戏）` : ''}。`
    ]
    const totalBaseCats = importForm.value.categoryIds?.length || 0
    const matchedCount = allGames.filter(g => g.autoMatchedCategoryIds && g.autoMatchedCategoryIds.length > 0).length
    
    if (totalBaseCats > 0) {
      messageLines.push(`✓ 已为所有游戏添加 ${totalBaseCats} 个基础分类`)
    }
    if (matchedCount > 0) {
      messageLines.push(`✓ 为 ${matchedCount} 个游戏自动匹配了分类`)
    }
    if (duplicateGames.length > 0) {
      messageLines.push(`⚠️ ${duplicateGames.length} 个重复游戏已独立创建并标记为冲突`)
    }
    if (importForm.value.boxId) {
      messageLines.push(`✓ 已关联 ${gameIds.length} 个游戏到盒子`)
    }
    if (duplicateGames.length > 0) {
      messageLines.push('')
      messageLines.push('提示：可在搜索框的备注字段输入"导入冲突"来查找重复游戏，然后手动处理。')
    }
    
    proxy.$modal.msgSuccess(messageLines.join('\n'))
    cancelImport()
    getList()
  } catch (error) {
    loading.close()
    proxy.$modal.msgError('导入失败：' + (error.message || '未知错误'))
  }
}

// 构建游戏提交数据
function buildGameSubmitData(gameData, isDuplicate, hasBoxRelation = false) {
  const submitData = {
    siteId: importForm.value.siteId,
    name: gameData.name,
    status: gameData.status,
    sortOrder: gameData.sortOrder,
    isNew: gameData.isNew,
    delFlag: '0'  // 明确设置删除标志为0（存在）
  }
  
  // 添加可选字段
  if (gameData.subtitle) submitData.subtitle = gameData.subtitle
  if (gameData.shortName) submitData.shortName = gameData.shortName
  if (gameData.gameType) submitData.gameType = gameData.gameType
  if (gameData.iconUrl) submitData.iconUrl = gameData.iconUrl
  if (gameData.coverUrl) submitData.coverUrl = gameData.coverUrl
  if (gameData.screenshots) submitData.screenshots = gameData.screenshots
  if (gameData.videoUrl) submitData.videoUrl = gameData.videoUrl
  if (gameData.description) submitData.description = gameData.description
  if (gameData.promotionDesc) submitData.promotionDesc = gameData.promotionDesc
  if (gameData.developer) submitData.developer = gameData.developer
  if (gameData.publisher) submitData.publisher = gameData.publisher
  if (gameData.packageName) submitData.packageName = gameData.packageName
  if (gameData.version) submitData.version = gameData.version
  if (gameData.size) submitData.size = gameData.size
  if (gameData.deviceSupport) submitData.deviceSupport = gameData.deviceSupport
  if (gameData.downloadCount !== null && gameData.downloadCount !== undefined) submitData.downloadCount = gameData.downloadCount
  if (gameData.rating !== null && gameData.rating !== undefined) submitData.rating = gameData.rating
  if (gameData.features) submitData.features = gameData.features
  if (gameData.tags) submitData.tags = gameData.tags
  if (gameData.launchTime) submitData.launchTime = gameData.launchTime
  if (gameData.isHot) submitData.isHot = gameData.isHot
  if (gameData.isRecommend) submitData.isRecommend = gameData.isRecommend
  
  // 下载链接始终存储到游戏表（作为游戏的基础数据）
  // 如果有盒子关联，也会同时存储到关联表中
  if (gameData.downloadUrl) submitData.downloadUrl = gameData.downloadUrl
  if (gameData.androidUrl) submitData.androidUrl = gameData.androidUrl
  if (gameData.iosUrl) submitData.iosUrl = gameData.iosUrl
  if (gameData.promoteUrl) submitData.promoteUrl = gameData.promoteUrl
  
  // 保存原始数据到备注（过滤掉 base64 等大数据字段）
  if (gameData.rawData) {
    // 创建一个副本并过滤掉大数据字段
    const cleanData = { ...gameData.rawData }
    
    // 移除 base64 图片数据字段
    if (cleanData.qrcode) cleanData.qrcode = '[base64已省略]'
    if (cleanData.weburl_qr) cleanData.weburl_qr = '[base64已省略]'
    
    // 移除 copy 字段（通常很长）
    if (cleanData.copy) delete cleanData.copy
    
    // 移除 photo 数组（已保存到 screenshots）
    if (cleanData.photo) delete cleanData.photo
    
    let remarkText = `[导入源数据] ${JSON.stringify(cleanData)}`
    if (isDuplicate) {
      remarkText = `[导入冲突:同站点已存在同名游戏ID=${gameData.existingGameId}] ` + remarkText
    }
    submitData.remark = remarkText
  }
  
  return submitData
}

/** 排除默认配置（关联模式） */
async function handleExcludeDefault(row) {
  const currentQuerySiteId = queryParams.value.siteId
  
  try {
    await proxy.$modal.confirm(`确认排除"${row.name}"吗？排除后该游戏将不在当前网站显示。`)
    
    const res = await getGameSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId)
    if (!excluded.includes(currentQuerySiteId)) excluded.push(currentQuerySiteId)
    await batchSaveGameSiteRelations({ gameIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
    
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
    await proxy.$modal.confirm(`确认恢复"${row.name}"吗？恢复后该游戏将重新在当前网站显示。`)
    
    const res = await getGameSites(row.id)
    const sites = res.data || []
    const included = sites.filter(s => s.relationType !== 'exclude').map(s => s.siteId)
    const excluded = sites.filter(s => s.relationType === 'exclude').map(s => s.siteId).filter(id => id !== currentQuerySiteId)
    await batchSaveGameSiteRelations({ gameIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
    
    proxy.$modal.msgSuccess('恢复成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('恢复失败:', error)
      proxy.$modal.msgError('恢复失败')
    }
  }
}

// JSON 格式化和解析辅助函数
function parseJson(jsonString) {
  if (!jsonString) return {}
  try {
    return typeof jsonString === 'string' ? JSON.parse(jsonString) : jsonString
  } catch (e) {
    return {}
  }
}

function formatJson(jsonString) {
  if (!jsonString) return ''
  try {
    const obj = typeof jsonString === 'string' ? JSON.parse(jsonString) : jsonString
    return JSON.stringify(obj, null, 2)
  } catch (e) {
    return jsonString
  }
}

// 递归对对象的键排序，保证 JSON 序列化结果稳定，便于 diff 对比
function sortObjectKeys(obj) {
  if (Array.isArray(obj)) return obj.map(sortObjectKeys)
  if (obj !== null && typeof obj === 'object') {
    return Object.keys(obj).sort().reduce((acc, key) => {
      acc[key] = sortObjectKeys(obj[key])
      return acc
    }, {})
  }
  return obj
}
function sortedJsonStringify(obj, indent) {
  return JSON.stringify(sortObjectKeys(obj), null, indent)
}

// 将任意值规范化保用于比较：
//   - 对象/数组 → 键排序序列化
//   - JSON 字符串 → 先解析再排序序列化（字段本身就是 JSON 类型的情况）
//   - 其他原始值 → 转字符串
function normalizeForCompare(val) {
  if (val === null || val === undefined) return val
  if (typeof val === 'string') {
    const trimmed = val.trim()
    if (trimmed.startsWith('{') || trimmed.startsWith('[')) {
      try {
        return sortedJsonStringify(JSON.parse(trimmed))
      } catch (e) { /* 不是 JSON */ }
    }
    return val
  }
  if (typeof val === 'object') return sortedJsonStringify(val)
  return String(val)
}
async function loadFieldMappingsForPlatform(platform) {
  // 检查缓存
  if (fieldMappingCache.value.has(platform)) {
    return
  }
  
  try {
    // 查询该平台的所有字段映射配置
    const response = await listFieldMapping({
      platform: platform,
      resourceType: 'game',
      status: '1',
      pageNum: 1,
      pageSize: 1000
    })
    
    const mappings = response.rows || []
    
    // 按 targetLocation 分类存储
    const promotionLinkMap = new Map()
    const platformDataMap = new Map()
    
    mappings.forEach(mapping => {
      const key = mapping.targetField
      const label = mapping.remark || mapping.targetField
      
      if (mapping.targetLocation === 'promotion_link') {
        promotionLinkMap.set(key, label)
      } else if (mapping.targetLocation === 'platform_data') {
        platformDataMap.set(key, label)
      }
    })
    
    // 缓存结果
    fieldMappingCache.value.set(platform, {
      promotion_link: promotionLinkMap,
      platform_data: platformDataMap
    })
  } catch (error) {
    console.error('加载字段映射配置失败:', error)
  }
}

// 字段友好名称映射（从字段映射配置中获取）
function getFieldLabel(fieldKey, fieldType = 'platform_data') {
  // 如果有平台信息，从缓存中获取
  if (form.value.platform && fieldMappingCache.value.has(form.value.platform)) {
    const platformMappings = fieldMappingCache.value.get(form.value.platform)
    const fieldMap = platformMappings[fieldType]
    
    if (fieldMap && fieldMap.has(fieldKey)) {
      return fieldMap.get(fieldKey)
    }
  }
  
  // 如果没有配置，返回字段名本身
  return fieldKey
}

// 更新平台数据字段
function updatePlatformDataField(key, value, isJson = false) {
  try {
    // 解析当前的platformData
    let platformData = parseJson(form.value.platformData)
    
    // 如果是JSON字符串，尝试解析
    if (isJson && value) {
      try {
        platformData[key] = JSON.parse(value)
      } catch (e) {
        // 如果解析失败，保持为字符串
        platformData[key] = value
      }
    } else {
      platformData[key] = value
    }
    
    // 更新form.platformData（键排序保证序列化稳定）
    form.value.platformData = sortedJsonStringify(platformData)
  } catch (e) {
    console.error('更新平台数据字段失败:', e)
  }
}

// 更新推广链接字段
function updatePromotionLinkField(key, value) {
  try {
    // 解析当前的promotionLinks
    let promotionLinks = parseJson(form.value.promotionLinks)
    promotionLinks[key] = value
    
    // 更新form.promotionLinks（键排序保证序列化稳定）
    form.value.promotionLinks = sortedJsonStringify(promotionLinks)
    console.log('推广链接已更新:', key, '=', value)
    console.log('完整推广链接数据:', form.value.promotionLinks)
  } catch (e) {
    console.error('更新推广链接字段失败:', e)
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
      const relationResponse = await getGameSites(row.id)
      const relations = relationResponse.data || []
      const includeRelation = relations.find(r => r.siteId === currentQuerySiteId && r.relationType === 'include')
      
      if (includeRelation) {
        // 有关联记录：切换关联表的 is_visible 字段
        await updateGameVisibility(currentQuerySiteId, row.id, newValue)
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
          proxy.$modal.msgSuccess('已排除该游戏')
        } else {
          excluded = excluded.filter(id => id !== currentQuerySiteId)
          proxy.$modal.msgSuccess('已恢复该游戏')
        }
        await batchSaveGameSiteRelations({ gameIds: [row.id], includeSiteIds: included, excludeSiteIds: excluded })
        // 重新加载列表以更新排除网站数量
        getList()
      }
    } else if (row.relationSource === 'own') {
      // 自有数据：切换启用状态
      const text = newValue === '1' ? '启用' : '禁用'
      await updateGame({ id: row.id, status: newValue })
      row.status = newValue
      proxy.$modal.msgSuccess(`${text}成功`)
    } else if (row.relationSource === 'shared') {
      // 共享数据：切换关联可见性
      await updateGameVisibility(currentQuerySiteId, row.id, newValue)
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
  if (effectiveIds.value.length === 0) {
    proxy.$modal.msgError('请先选择要导出的游戏')
    return
  }
  exportDialogOpen.value = true
}

/** 确认导出 */
async function confirmExport() {
  const selectedIds = effectiveIds.value
  if (!selectedIds || selectedIds.length === 0) {
    proxy.$modal.msgError('请选择要导出的游戏')
    return
  }
  
  exportLoading.value = true
  
  try {
    // 获取选中的游戏详细数据
    const gamePromises = selectedIds.map(id => getGame(id))
    const gameResponses = await Promise.all(gamePromises)
    const gameData = gameResponses.map(response => response.data)
    
    // 转换数据格式
    const formattedData = gameData.map(game => ({
      游戏名称: game.name,
      游戏简称: game.shortName || '',
      游戏类型: game.gameType || '',
      网站编码: getSiteCode(game.siteId),
      游戏厂商: game.developer || '',
      图标URL: game.iconUrl || '',
      截图URL: game.screenshotUrls || '',
      游戏描述: game.description || '',
      设备支持: game.deviceSupport || 'both',
      安卓链接: game.androidUrl || '',
      iOS链接: game.iosUrl || '',
      APK链接: game.apkUrl || '',
      下载链接: game.downloadUrl || '',
      官方网站: game.officialWebsite || '',
      折扣标签: game.discountLabel || '',
      评分: game.rating || '',
      下载量: game.downloadCount || 0,
      是否推荐: game.isRecommend || '0',
      状态: game.status || '1',
      备注: game.remark || ''
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
async function exportToExcel(gameData) {
  const XLSX = await import('xlsx')
  const wb = XLSX.utils.book_new()
  const ws = XLSX.utils.json_to_sheet(gameData)
  XLSX.utils.book_append_sheet(wb, ws, "游戏数据")
  const fileName = `游戏数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出为JSON */
function exportToJSON(gameData) {
  const exportData = {
    games: gameData,
    exportTime: new Date().toISOString()
  }
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  const fileName = `游戏数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
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
    
    // 1. 获取全站游戏数据（默认配置 + 所有真实站点）
    const baseQuery = {
      queryMode: 'creator',
      pageNum: 1,
      pageSize: 9999
    }
    const gameMap = new Map()
    const appendGames = (rows = []) => {
      rows.forEach(game => {
        if (game && game.id != null && !gameMap.has(game.id)) {
          gameMap.set(game.id, game)
        }
      })
    }

    // 先拉默认配置（显式使用个人默认站点ID，兼容默认站点ID已变更场景）
    const defaultSiteId = personalSiteId.value
    const defaultGamesResponse = await listGame(
      defaultSiteId !== null && defaultSiteId !== undefined
        ? {
            ...baseQuery,
            siteId: defaultSiteId,
            includeDefault: false
          }
        : baseQuery
    )
    appendGames(defaultGamesResponse.rows)

    // 再拉所有真实站点的自有数据
    const realSites = (siteList.value || []).filter(site => !isPersonalSite(site.id, siteList.value))
    const siteGameResponses = await Promise.all(
      realSites.map(site =>
        listGame({
          ...baseQuery,
          siteId: site.id,
          includeDefault: false
        })
      )
    )
    siteGameResponses.forEach(resp => appendGames(resp.rows))

    const gameData = Array.from(gameMap.values())
    
    // 2. 收集所有网站ID
    const siteIds = new Set()
    gameData.forEach(game => {
      if (game.siteId !== null && game.siteId !== undefined) {
        siteIds.add(game.siteId)
      }
    })
    
    // 3. 获取所有关联关系
    const relationDataRaw = []
    
    for (let index = 0; index < gameData.length; index++) {
      const game = gameData[index]
      const virtualId = index + 1
      
      if (game.siteId && !isPersonalSite(game.siteId, siteList.value)) {
        // 非默认配置：获取所有关联关系
        try {
          const response = await getGameSites(game.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId)
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              const relationData = {
                游戏虚拟ID: virtualId,
                游戏名称: game.name,
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
          console.warn('获取游戏关联失败:', game.id, error)
        }
      } else {
        // 默认配置：获取所有网站关系（包括关联和排除）
        try {
          const response = await getGameSites(game.id)
          const relations = response.data || []
          if (relations.length > 0) {
            relations.forEach(rel => {
              siteIds.add(rel.siteId)
              const relationType = rel.relationType === 'exclude' ? '排除' : '关联'
              const relationData = {
                游戏虚拟ID: virtualId,
                游戏名称: game.name,
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
          console.warn('获取默认配置关系失败:', game.id, error)
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
      游戏虚拟ID: rel.游戏虚拟ID,
      游戏名称: rel.游戏名称,
      关联类型: rel.关联类型,
      网站虚拟ID: siteIdToVirtualIdMap.get(rel.网站ID_原始) ?? 0,
      网站编码: rel.网站编码,
      可见性: rel.可见性
    }))
    
    // 6. 转换游戏数据格式
    const formattedGameData = gameData.map((game, index) => ({
      游戏虚拟ID: index + 1,
      游戏名称: game.name,
      游戏简称: game.shortName || '',
      游戏类型: game.gameType || '',
      网站虚拟ID: siteIdToVirtualIdMap.get(game.siteId) ?? 0,
      网站编码: getSiteCode(game.siteId),
      游戏厂商: game.developer || '',
      图标URL: game.iconUrl || '',
      截图URL: game.screenshotUrls || '',
      游戏描述: game.description || '',
      设备支持: game.deviceSupport || 'both',
      安卓链接: game.androidUrl || '',
      iOS链接: game.iosUrl || '',
      APK链接: game.apkUrl || '',
      下载链接: game.downloadUrl || '',
      官方网站: game.officialWebsite || '',
      折扣标签: game.discountLabel || '',
      评分: game.rating || '',
      下载量: game.downloadCount || 0,
      是否推荐: game.isRecommend || '0',
      状态: game.status || '1',
      备注: game.remark || ''
    }))
    
    if (fullExportFormat.value === 'excel') {
      await exportFullDataToExcel(sitesData, formattedGameData, relationData)
    } else {
      exportFullDataToJSON(sitesData, formattedGameData, relationData)
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
async function exportFullDataToExcel(sitesData, gameData, relationData) {
  const XLSX = await import('xlsx')
  const wb = XLSX.utils.book_new()
  
  if (sitesData.length > 0) {
    const sitesWs = XLSX.utils.json_to_sheet(sitesData)
    XLSX.utils.book_append_sheet(wb, sitesWs, "网站列表")
  }
  
  const gameWs = XLSX.utils.json_to_sheet(gameData)
  XLSX.utils.book_append_sheet(wb, gameWs, "游戏数据")
  
  if (relationData.length > 0) {
    const relationWs = XLSX.utils.json_to_sheet(relationData)
    XLSX.utils.book_append_sheet(wb, relationWs, "网站关联")
  }
  
  const fileName = `游戏全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.xlsx`
  XLSX.writeFile(wb, fileName)
}

/** 导出全站数据为JSON */
function exportFullDataToJSON(sitesData, gameData, relationData) {
  const exportData = {
    sites: sitesData,
    games: gameData,
    relations: relationData,
    exportTime: new Date().toISOString()
  }
  const jsonStr = JSON.stringify(exportData, null, 2)
  const blob = new Blob([jsonStr], { type: 'application/json;charset=utf-8' })
  const fileName = `游戏全站数据_${new Date().toISOString().slice(0, 19).replace(/[:.]/g, '-')}.json`
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
  systemImportFile.value = file
  
  try {
    const fileData = await readFileData(file.raw)
    let parsedData
    
    if (file.raw.type.includes('json')) {
      parsedData = parseSystemImportJSONData(fileData)
    } else {
      parsedData = await parseSystemImportExcelData(fileData)
    }
    
    systemImportPreviewData.value = validateAndTransformSystemImportData(parsedData.games)
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + error.message)
    systemImportPreviewData.value = []
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

/** 解析系统导入Excel数据 */
async function parseSystemImportExcelData(arrayBuffer) {
  const XLSX = await import('xlsx')
  const workbook = XLSX.read(arrayBuffer, { type: 'array' })
  
  const result = { games: [] }
  
  const gameSheetName = workbook.SheetNames.find(name => 
    name === '游戏数据' || name === 'games'
  ) || workbook.SheetNames[0]
  if (gameSheetName && workbook.Sheets[gameSheetName]) {
    result.games = XLSX.utils.sheet_to_json(workbook.Sheets[gameSheetName])
  }
  
  return result
}

/** 解析系统导入JSON数据 */
function parseSystemImportJSONData(jsonString) {
  try {
    const data = JSON.parse(jsonString)
    return {
      games: data.games || (Array.isArray(data) ? data : [])
    }
  } catch (error) {
    throw new Error('JSON格式错误')
  }
}

/** 验证和转换系统导入数据 */
function validateAndTransformSystemImportData(rawData) {
  if (!Array.isArray(rawData) || rawData.length === 0) {
    return []
  }
  
  return rawData.map((item, index) => {
    const transformedItem = {
      name: item['游戏名称'] || `导入游戏${index + 1}`,
      shortName: item['游戏简称'] || '',
      gameType: item['游戏类型'] || '',
      developer: item['游戏厂商'] || '',
      iconUrl: item['图标URL'] || '',
      screenshotUrls: item['截图URL'] || '',
      description: item['游戏描述'] || '',
      deviceSupport: item['设备支持'] || 'both',
      androidUrl: item['安卓链接'] || '',
      iosUrl: item['iOS链接'] || '',
      apkUrl: item['APK链接'] || '',
      downloadUrl: item['下载链接'] || '',
      officialWebsite: item['官方网站'] || '',
      discountLabel: item['折扣标签'] || '',
      rating: item['评分'] || '',
      downloadCount: parseInt(item['下载量']) || 0,
      isRecommend: normalizeStatus(item['是否推荐']),
      status: normalizeStatus(item['状态']),
      remark: item['备注'] || ''
    }
    
    return transformedItem
  })
}

/** 确认系统导入 */
async function confirmSystemImport() {
  if (systemImportPreviewData.value.length === 0) {
    proxy.$modal.msgError('没有可导入的数据')
    return
  }
  
  if (!systemImportForm.value.siteId) {
    proxy.$modal.msgError('请选择创建者网站')
    return
  }
  
  try {
    systemImportLoading.value = true
    
    // 批量导入游戏
    for (const gameData of systemImportPreviewData.value) {
      await addGame({ ...gameData, siteId: systemImportForm.value.siteId })
    }
    
    proxy.$modal.msgSuccess(`成功导入 ${systemImportPreviewData.value.length} 个游戏`)
    systemImportDialogOpen.value = false
    systemImportPreviewData.value = []
    systemImportFile.value = null
    getList()
    
  } catch (error) {
    console.error('导入失败:', error)
    proxy.$modal.msgError('导入失败: ' + (error.message || '未知错误'))
  } finally {
    systemImportLoading.value = false
  }
}

/** 全站导入 */
function handleFullImport() {
  fullImportDialogOpen.value = true
  fullImportSites.value = []
  fullImportGames.value = []
  fullImportRelations.value = []
  fullImportFile.value = null
  siteMapping.value = {}
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
    fullImportGames.value = parsedData.games || []
    fullImportRelations.value = parsedData.relations || []
    fullImportTranslations.value = parsedData.translations || []
    
    // 检查是否包含默认配置
    hasDefaultConfig.value = fullImportGames.value.some(g => g['网站虚拟ID'] === 0 || g['网站编码'] === 'default')
    
    // 初始化网站映射
    initializeSiteMapping()
    
  } catch (error) {
    console.error('文件解析失败:', error)
    proxy.$modal.msgError('文件解析失败: ' + error.message)
  }
}

/** 处理全站导入文件移除 */
function handleFullImportFileRemove() {
  fullImportFile.value = null
  fullImportSites.value = []
  fullImportGames.value = []
  fullImportRelations.value = []
  fullImportTranslations.value = []
  hasDefaultConfig.value = false
  return true
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
    games: [],
    relations: []
  }
  
  if (workbook.SheetNames.includes('网站列表')) {
    const siteSheet = workbook.Sheets['网站列表']
    result.sites = XLSX.utils.sheet_to_json(siteSheet)
  }
  
  if (workbook.SheetNames.includes('游戏数据')) {
    const gameSheet = workbook.Sheets['游戏数据']
    result.games = XLSX.utils.sheet_to_json(gameSheet)
  }
  
  if (workbook.SheetNames.includes('网站关联')) {
    const relationSheet = workbook.Sheets['网站关联']
    result.relations = XLSX.utils.sheet_to_json(relationSheet)
  }
  
  return result
}

/** 解析全站导入JSON数据 */
function parseFullImportJSONData(jsonString) {
  try {
    return JSON.parse(jsonString)
  } catch (error) {
    throw new Error('JSON格式错误')
  }
}

/** 确认全站导入 */
async function confirmFullImport() {
  if (fullImportGames.value.length === 0) {
    proxy.$modal.msgError('没有可导入的数据')
    return
  }
  
  // 检查网站映射
  const unmappedSites = fullImportSites.value.filter(s => 
    s['网站虚拟ID'] > 0 && !siteMapping.value[s['网站虚拟ID']]
  )
  
  if (unmappedSites.length > 0) {
    proxy.$modal.msgError('请先完成所有网站的映射')
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
  
  // 1. 处理游戏数据导入（按网站）
  const gamesBySite = {}
  
  fullImportGames.value.forEach(game => {
    const siteVirtualId = game['网站虚拟ID'] || 0
    const virtualId = game['游戏虚拟ID']
    
    // 获取目标网站ID（包括默认配置和真实网站）
    const targetSiteId = siteMapping.value[siteVirtualId]
    
    if (targetSiteId !== undefined && virtualId) {
      if (!gamesBySite[targetSiteId]) {
        gamesBySite[targetSiteId] = []
      }
      gamesBySite[targetSiteId].push({
        ...game,
        targetSiteId,
        virtualId
      })
    }
  })
  
  // 2. 按目标网站分别导入游戏
  for (const [targetSiteId, games] of Object.entries(gamesBySite)) {
    await importGamesForSite(targetSiteId, games, virtualIdToNewIdMap[targetSiteId] || {})
  }
  
  // 3. 先处理默认配置关联（如果需要）
  if (hasDefaultConfig.value && createDefaultAsNewSite.value) {
    await createRelationsForConvertedDefault(virtualIdToNewIdMap)
  }
  
  // 4. 处理剩余的关联关系
  if (fullImportRelations.value.length > 0) {
    await importRelations(virtualIdToNewIdMap)
  }
  
  // 5. 处理翻译数据
  if (fullImportTranslations.value.length > 0) {
    await importTranslations(virtualIdToNewIdMap)
  }
}

/** 为指定网站导入游戏 */
async function importGamesForSite(targetSiteId, games, virtualIdMap) {
  console.log(`开始为网站 ${targetSiteId} 导入游戏...`)
  
  if (!games || games.length === 0) {
    console.log('没有游戏需要导入')
    return
  }

  // 转换数据格式
  const transformedData = games.map(game => ({
    name: game['游戏名称'],
    shortName: game['游戏简称'],
    iconUrl: game['游戏图标URL'],
    developer: game['开发商'],
    gameType: game['游戏类型'],
    status: normalizeStatus(game['状态']),
    siteId: parseInt(targetSiteId),
    virtualId: game.virtualId
  }))

  // 导入游戏
  for (const gameItem of transformedData) {
    try {
      console.log('导入游戏:', gameItem.name)
      
      const virtualId = gameItem.virtualId
      delete gameItem.virtualId
      
      const response = await addGame(gameItem)
      const newGameId = response.data || response.msg
      
      // 记录虚拟ID到新ID的映射
      if (virtualId) {
        virtualIdMap[virtualId] = newGameId
      }
      
      console.log(`游戏导入成功: ${gameItem.name} (虚拟ID: ${virtualId}) -> ID: ${newGameId}`)
    } catch (error) {
      console.error(`游戏导入失败: ${gameItem.name}`, error)
      throw new Error(`游戏 "${gameItem.name}" 导入失败: ${error.message}`)
    }
  }

  console.log(`网站 ${targetSiteId} 游戏导入完成`)
}

/** 导入关联关系 */
async function importRelations(virtualIdToNewIdMap) {
  console.log(`开始导入 ${fullImportRelations.value.length} 条关联关系...`)
  
  for (const relation of fullImportRelations.value) {
    try {
      const virtualId = relation['游戏虚拟ID']
      const relationTypeFromData = relation['关联类型'] || '关联'
      const siteVirtualId = relation['网站虚拟ID'] || 0
      
      // 从游戏数据中获取创建者网站虚拟ID
      const gameData = fullImportGames.value.find(g => 
        g['游戏虚拟ID'] === virtualId
      )
      const creatorSiteVirtualId = gameData ? (gameData['网站虚拟ID'] || 0) : 0
      
      // 如果默认配置被转换为新网站配置，跳过这些关联关系（已在createRelationsForConvertedDefault中处理）
      if (hasDefaultConfig.value && createDefaultAsNewSite.value && creatorSiteVirtualId === 0) {
        console.log(`跳过默认配置关联关系（已由createRelationsForConvertedDefault处理）: 虚拟ID=${virtualId}, 目标网站虚拟ID=${siteVirtualId}`)
        continue
      }
      
      if (!virtualId) {
        console.warn('跳过关联关系，缺少游戏虚拟ID:', relation)
        continue
      }
      
      const targetSiteId = siteMapping.value[siteVirtualId]
      if (!targetSiteId) {
        console.warn(`跳过关联关系，找不到网站虚拟ID ${siteVirtualId} 的映射目标:`, relation)
        continue
      }
      
      let newGameId = null
      
      // 从所有网站的映射中查找该虚拟ID对应的新ID
      for (const siteId in virtualIdToNewIdMap) {
        if (virtualIdToNewIdMap[siteId][virtualId]) {
          newGameId = virtualIdToNewIdMap[siteId][virtualId]
          break
        }
      }
      
      if (!newGameId) {
        console.warn(`跳过关联关系，找不到虚拟ID ${virtualId} 对应的游戏:`, relation)
        continue
      }
      
      // 根据关联类型处理
      if (relationTypeFromData === '排除') {
        await batchSaveGameSiteRelations({ gameIds: [newGameId], excludeSiteIds: [targetSiteId] })
        console.log(`排除关系导入成功`)
      } else {
        await batchSaveGameSiteRelations({ gameIds: [newGameId], includeSiteIds: [targetSiteId] })
        console.log(`关联关系导入成功: 游戏 ${newGameId} (虚拟ID: ${virtualId}) -> 网站 ${targetSiteId}`)
      }
      
    } catch (error) {
      console.error('关联关系导入失败:', relation, error)
    }
  }
  
  console.log('关联关系导入完成')
}

/** 导入翻译数据 */
async function importTranslations(virtualIdToNewIdMap) {
  if (!fullImportTranslations.value || fullImportTranslations.value.length === 0) {
    return
  }
  
  console.log(`开始导入 ${fullImportTranslations.value.length} 条翻译数据...`)
  
  // TODO: 实现翻译数据导入逻辑
  
  console.log('翻译数据导入完成')
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
    
    // 为每个转换后的游戏检查原来的关联和排除关系
    for (const [virtualId, newGameId] of Object.entries(defaultVirtualIdMap)) {
      // 查找原来的关联关系数据（包括排除和关联）
      const originalRelations = fullImportRelations.value.filter(rel => {
        const relVirtualId = rel['游戏虚拟ID']
        
        // 从游戏数据中获取创建者网站虚拟ID
        const gameData = fullImportGames.value.find(g => 
          g['游戏虚拟ID'] === relVirtualId
        )
        const creatorSiteVirtualId = gameData ? (gameData['网站虚拟ID'] || 0) : 0
        
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
      
      console.log(`游戏 ${newGameId} (虚拟ID: ${virtualId}): 有关联的网站虚拟ID:`, Array.from(includedSiteVirtualIds), '仅排除的网站虚拟ID:', excludedSiteVirtualIds)
      
      // 第二步：优先处理关联逻辑
      // 处理显式关联关系
      for (const includedVId of includedSiteVirtualIds) {
        const targetSiteId = siteMapping.value[includedVId]
        if (!targetSiteId) {
          console.warn(`关联的网站虚拟ID ${includedVId} 未映射，跳过`)
          continue
        }
        
        try {
          await batchSaveGameSiteRelations({ gameIds: [newGameId], includeSiteIds: [targetSiteId] })
          console.log(`创建关联成功: game=${newGameId}, site=${targetSiteId}`)
        } catch (error) {
          console.warn(`创建关联失败: game=${newGameId}, site=${targetSiteId}`, error.message || '未知错误')
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
            await batchSaveGameSiteRelations({ gameIds: [newGameId], includeSiteIds: [targetSiteId] })
            console.log(`创建逆向关联成功: game=${newGameId}, site=${targetSiteId}`)
          } catch (error) {
            console.warn(`创建逆向关联失败: game=${newGameId}, site=${targetSiteId}`, error.message || '未知错误')
          }
        } else if (isExcludedMapped) {
          console.log(`跳过网站 ${targetSiteId}，因为它是被排除网站的映射目标`)
        } else {
          console.log(`跳过网站 ${targetSiteId}，因为已在显式关联中处理`)
        }
      }
      
      // 记录已处理的关联关系（所有涉及该游戏和默认配置的关联）
      originalRelations.forEach(rel => {
        processedRelations.push(rel)
      })
    }
    
    // 从关联关系列表中移除已处理的关联
    processedRelations.forEach(processedRel => {
      const index = fullImportRelations.value.findIndex(rel => {
        return rel['游戏虚拟ID'] === processedRel['游戏虚拟ID'] && 
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

// 页面再次激活时刷新数据
onActivated(() => {
  if (siteList.value.length > 0) {
    loadGameCategoriesForQuery(queryParams.value.siteId)
    loadBoxOptions(queryParams.value.siteId)
    getList()
  }
})

// 初始化：先加载网站列表，然后由 loadSites 自动加载数据
loadSites(() => {
  const restored = restoreViewModeSiteSelection(siteList.value, {
    creatorFallbackSiteId: personalSiteId.value
  })
  viewMode.value = restored.viewMode
  queryParams.value.siteId = restored.siteId
  saveViewModeSiteSelection(viewMode.value, queryParams.value.siteId)
  loadGameCategoriesForQuery(queryParams.value.siteId)
  loadBoxOptions(queryParams.value.siteId)
  getList()
})

// 加载表字段信息
async function loadTableFields() {
  try {
    const response = await getAllTableFields()
    if (response && response.data) {
      tableFieldsInfo.value = response.data
    }
  } catch (error) {
    console.error('加载表字段信息失败:', error)
  }
}

// 获取主表字段标签（优先使用数据库comment，否则使用默认label）
function getMainFieldLabel(fieldName, defaultLabel) {
  const mainFields = tableFieldsInfo.value.main?.fields || []
  const field = mainFields.find(f => f.value === fieldName)
  if (!field?.comment) return defaultLabel
  
  // 清理comment，去掉括号中的枚举值说明
  let comment = field.comment
  // 去掉括号及其内容，如 "游戏类型(official-官方/discount-折扣)" -> "游戏类型"
  comment = comment.replace(/[（(].*?[）)]/g, '').trim()
  // 去掉冒号后的内容，如 "游戏类型: official-官方" -> "游戏类型"
  comment = comment.split(/[：:]/)[0].trim()
  
  return comment || defaultLabel
}

// 初始化时加载字段信息
loadTableFields()
loadGameTypeOptions()

// ===== 游戏变更历史 =====
const gameHistoryOpen    = ref(false)
const gameHistoryLoading = ref(false)
const gameHistoryTitle   = ref('')
const gameHistoryList    = ref([])
const historyDiffOpen    = ref(false)
const historyDiffTarget  = ref(null)
const showUnchangedFields = ref(false)

// 关联表字段中文标签
const RELATION_FIELD_LABELS = {
  boxId: '盒子ID', boxName: '盒子名称', gameId: '游戏ID',
  downloadUrl: '下载链接', promoteUrl: '推广链接', discountLabel: '折扣标签',
  firstChargeDomestic: '首充(国内)', firstChargeOverseas: '首充(海外)',
  rechargeDomestic: '充值(国内)', rechargeOverseas: '充值(海外)',
  hasSupport: '是否有扶持', supportDesc: '扶持说明',
  status: '状态', siteId: '网站ID', remark: '备注'
}

const CATEGORY_RELATION_FIELD_LABELS = {
  categories: '分类列表'
}

// 跳过不展示的内部字段
const HISTORY_SKIP_FIELDS = new Set(['serialVersionUID', 'id', 'createTime', 'updateTime', 'createBy', 'updateBy', 'delFlag', 'params'])

function getHistoryFieldLabel(fieldName, targetTable) {
  if (targetTable === 'gb_box_game_relations') {
    return RELATION_FIELD_LABELS[fieldName] || fieldName
  }
  if (targetTable === 'gb_game_category_relations') {
    return CATEGORY_RELATION_FIELD_LABELS[fieldName] || fieldName
  }
  // gb_games：使用已有的 fieldLabels computed
  return fieldLabels.value[fieldName] || fieldName
}

// 按批次分组（一个批次 = 一个时间轴节点）
const gameHistoryByBatch = computed(() => {
  const batchMap = new Map()
  gameHistoryList.value.forEach(item => {
    const key = item.batchNo || String(item.batchId)
    if (!batchMap.has(key)) {
      batchMap.set(key, {
        batchNo: item.batchNo,
        batchId: item.batchId,
        createTime: item.createTime,
        createBy: item.createBy,
        changes: []
      })
    }
    batchMap.get(key).changes.push(item)
  })
  return Array.from(batchMap.values())
})

// diff 行数据（含 label 字段）
const historyDiffRows = computed(() => {
  if (!historyDiffTarget.value) return []
  const after  = safeParseHistoryJson(historyDiffTarget.value.afterSnapshot)
  const before = safeParseHistoryJson(historyDiffTarget.value.beforeSnapshot)
  if (!after) return []
  const table = historyDiffTarget.value.targetTable
  const keys = new Set([...Object.keys(after), ...(before ? Object.keys(before) : [])])
  return Array.from(keys)
    .filter(k => !HISTORY_SKIP_FIELDS.has(k))
    .map(k => ({
      field: k,
      label: getHistoryFieldLabel(k, table),
      beforeVal: before ? before[k] : undefined,
      afterVal: after[k],
      changed: normalizeForCompare(before ? before[k] : undefined) !== normalizeForCompare(after[k])
    }))
    .sort((a, b) => (b.changed ? 1 : 0) - (a.changed ? 1 : 0))
})

const historyChangedRows   = computed(() => historyDiffRows.value.filter(r => r.changed))
const historyUnchangedRows = computed(() => historyDiffRows.value.filter(r => !r.changed))

function safeParseHistoryJson(str) {
  if (!str) return null
  try { return JSON.parse(str) } catch { return null }
}

function historyFormatVal(val) {
  if (val === undefined || val === null) return '—'
  if (typeof val === 'string') {
    const trimmed = val.trim()
    if (trimmed.startsWith('{') || trimmed.startsWith('[')) {
      try {
        return JSON.stringify(sortObjectKeys(JSON.parse(trimmed)), null, 2)
      } catch (e) { /* 不是 JSON */ }
    }
    return val
  }
  if (typeof val === 'object') return sortedJsonStringify(val, 2)
  return String(val)
}

async function handleShowGameHistory(row) {
  gameHistoryTitle.value   = row.name || row.shortName || String(row.id)
  gameHistoryOpen.value    = true
  gameHistoryLoading.value = true
  gameHistoryList.value    = []
  try {
    const res = await getGameHistory(row.id)
    gameHistoryList.value = res.data || []
  } finally {
    gameHistoryLoading.value = false
  }
}

function showHistoryDiff(item) {
  showUnchangedFields.value = false
  historyDiffTarget.value = item
  historyDiffOpen.value   = true
}

function openImportHistoryDialog(batchNo) {
  // 打开数据修改历史弹窗并定位到该批次
  gameHistoryOpen.value = false
  importHistoryOpen.value = true
  importHistoryParams.pageNum = 1
  importHistoryParams.gameName = undefined
  importHistoryParams.batchNoList = batchNo ? [batchNo] : []
  ensureImportHistoryBatchOption(batchNo)
  loadImportHistory()
}

async function handleRevertHistoryChange(item) {
  if (!item || item.reverted === 1) return
  try {
    await proxy.$modal.confirm(`确定撤回游戏「${item.gameName}」的此条${item.changeType === 'INSERT' ? '新增' : '更新'}变更？`)
    const res = await revertGameHistoryChange(item.id)
    proxy.$modal.msgSuccess(res.msg || '撤回成功')
    item.reverted = 1
    historyDiffOpen.value = false
  } catch (e) {
    if (e !== 'cancel') proxy.$modal.msgError('撤回失败')
  }
}

// 撤回当前游戏在指定批次内的全部变更（主表 + 关联表视为一条数据）
async function handleRevertGameBatch(batch) {
  if (!batch) return
  const gameName = batch.changes[0]?.gameName || ''
  try {
    await proxy.$modal.confirm(`确定撤回游戏「${gameName}」在批次「${batch.batchNo}」中的全部变更？`)
    const gameId = batch.changes[0]?.gameId
    const res = batch.batchId
      ? await revertByBatchGame(batch.batchId, gameId)
      : await revertByBatchNoGame(batch.batchNo, gameId)
    proxy.$modal.msgSuccess(res.msg || '撤回成功')
    // 刷新历史列表以更新撤回状态
    const reloadRes = await getGameHistory(batch.changes[0]?.gameId)
    gameHistoryList.value = reloadRes.data || []
  } catch (e) {
    if (e !== 'cancel') proxy.$modal.msgError('撤回失败')
  }
}

// 重新应用当前游戏在指定批次内的全部已撤回变更
async function handleReApplyGameBatch(batch) {
  if (!batch) return
  const gameName = batch.changes[0]?.gameName || ''
  try {
    await proxy.$modal.confirm(`确定重新应用游戏「${gameName}」在批次「${batch.batchNo}」中的全部变更？`)
    const gameId = batch.changes[0]?.gameId
    const res = batch.batchId
      ? await reApplyByBatchGame(batch.batchId, gameId)
      : await reApplyByBatchNoGame(batch.batchNo, gameId)
    proxy.$modal.msgSuccess(res.msg || '重新应用成功')
    const reloadRes = await getGameHistory(batch.changes[0]?.gameId)
    gameHistoryList.value = reloadRes.data || []
  } catch (e) {
    if (e !== 'cancel') proxy.$modal.msgError('重新应用失败')
  }
}

// ===== 数据修改历史对话框（嵌入游戏页面）=====
const importHistoryOpen    = ref(false)
const importHistoryLoading = ref(false)
const importHistoryList    = ref([])
const importHistoryTotal   = ref(0)
const importHistoryParams  = reactive({
  pageNum: 1,
  pageSize: 10,
  status: undefined,
  reverted: undefined,
  gameName: undefined,
  batchNoList: []
})
const importHistoryExpandedKeys = ref([])
const importHistoryBatchOptions = ref([])
const importHistoryBatchOptionsLoading = ref(false)

function normalizeBatchNos(batchNoList) {
  if (!Array.isArray(batchNoList) || batchNoList.length === 0) return undefined
  const values = batchNoList.map(v => String(v || '').trim()).filter(Boolean)
  if (values.length === 0) return undefined
  return Array.from(new Set(values)).join(',')
}

function ensureImportHistoryBatchOption(batchNo) {
  if (!batchNo) return
  const exists = importHistoryBatchOptions.value.some(item => item.batchNo === batchNo)
  if (exists) return
  importHistoryBatchOptions.value.unshift({
    batchNo,
    label: `${batchNo}（当前定位批次）`
  })
}

async function loadImportHistoryBatchOptions() {
  importHistoryBatchOptionsLoading.value = true
  try {
    const res = await listImportBatch({ pageNum: 1, pageSize: 200 })
    const rows = res.rows || []
    importHistoryBatchOptions.value = rows
      .filter(item => item && item.batchNo)
      .map(item => ({
        batchNo: item.batchNo,
        label: `${item.batchNo} ｜ ${item.createTime || '无时间'} ｜ ${item.boxName || '未知盒子'}`
      }))
  } finally {
    importHistoryBatchOptionsLoading.value = false
  }
}

function handleBatchSelectVisible(visible) {
  if (!visible) return
  if (importHistoryBatchOptions.value.length > 0) return
  loadImportHistoryBatchOptions()
}

function resetImportHistoryFilters() {
  importHistoryParams.pageNum = 1
  importHistoryParams.status = undefined
  importHistoryParams.reverted = undefined
  importHistoryParams.gameName = undefined
  importHistoryParams.batchNoList = []
  loadImportHistory()
}

async function openImportHistory() {
  importHistoryOpen.value = true
  if (importHistoryBatchOptions.value.length === 0) {
    await loadImportHistoryBatchOptions()
  }
  await loadImportHistory()
}

async function loadImportHistory() {
  importHistoryLoading.value = true
  try {
    const req = {
      ...importHistoryParams,
      gameName: importHistoryParams.gameName ? String(importHistoryParams.gameName).trim() : undefined,
      batchNos: normalizeBatchNos(importHistoryParams.batchNoList)
    }
    const res = await listImportBatch(req)
    importHistoryList.value = res.rows || []
    importHistoryTotal.value = res.total || 0
    // 清除旧的展开数据及展开状态
    importHistoryExpandedKeys.value = []
    importHistoryList.value.forEach(row => {
      row._changes = undefined
      row._groupedChanges = []
      row._changesLoaded = false
      row._detailKeyword = ''
      row._detailStatus = 'all'
      row._detailChangeType = 'all'
      row._detailPageNum = 1
      row._detailPageSize = 10
    })
  } finally {
    importHistoryLoading.value = false
  }
}

// el-table expand 事件：懒加载该批次的变更
async function handleImportHistoryExpand(row, expandedRows) {
  const isExpanding = expandedRows.some(r => r.id === row.id)
  if (!isExpanding) {
    // 用户主动收起——从受控列表移除
    importHistoryExpandedKeys.value = importHistoryExpandedKeys.value.filter(k => k !== row.id)
    return
  }
  // 展开时先把 key 加入，确保异步加载期间不会被重渲染收起
  if (!importHistoryExpandedKeys.value.includes(row.id)) {
    importHistoryExpandedKeys.value = [...importHistoryExpandedKeys.value, row.id]
  }
  if (row._changesLoaded) return
  try {
    const res = row.id > 0 ? await getBatchChanges(row.id) : await getBatchChangesByNo(row.batchNo)
    row._changes = res.data || []
    row._groupedChanges = groupChangesByGame(row._changes)
    row._detailKeyword = ''
    row._detailStatus = 'all'
    row._detailChangeType = 'all'
    row._detailPageNum = 1
    row._detailPageSize = 10
  } catch {
    row._changes = []
    row._groupedChanges = []
    row._detailKeyword = ''
    row._detailStatus = 'all'
    row._detailChangeType = 'all'
    row._detailPageNum = 1
    row._detailPageSize = 10
  } finally {
    row._changesLoaded = true
  }
}

// 按游戏 ID 将批次变更分组（主表+关联表视为同一条数据）
function groupChangesByGame(changes) {
  if (!changes) return []
  const map = new Map()
  changes.forEach(c => {
    if (!map.has(c.gameId)) {
      map.set(c.gameId, { gameId: c.gameId, gameName: c.gameName || '', changes: [] })
    } else if (!map.get(c.gameId).gameName && c.gameName) {
      // 关联表记录可能没有 gameName，主表记录有，优先取非空值
      map.get(c.gameId).gameName = c.gameName
    }
    map.get(c.gameId).changes.push(c)
  })
  return Array.from(map.values()).map(g => ({
    ...g,
    allReverted: g.changes.every(c => c.reverted === 1)
  }))
}

function getImportHistoryGroupedChanges(batchRow) {
  if (Array.isArray(batchRow?._groupedChanges)) return batchRow._groupedChanges
  return groupChangesByGame(batchRow?._changes || [])
}

function parseImportHistoryBatchSummary(batchRow) {
  const raw = batchRow?.summary
  if (!raw) return { skippedList: [], errorList: [] }
  try {
    const parsed = JSON.parse(raw)
    if (Array.isArray(parsed)) {
      // 兼容历史数据：旧版 summary 仅保存 errorList
      return { skippedList: [], errorList: parsed }
    }
    return {
      skippedList: Array.isArray(parsed?.skippedList) ? parsed.skippedList : [],
      errorList: Array.isArray(parsed?.errorList) ? parsed.errorList : []
    }
  } catch {
    return { skippedList: [], errorList: [] }
  }
}

function getImportHistorySkippedRows(batchRow) {
  const { skippedList } = parseImportHistoryBatchSummary(batchRow)
  return skippedList.map((msg, idx) => {
    const text = String(msg || '').trim()
    const firstParen = text.indexOf('（')
    const gameName = firstParen > 0 ? text.slice(0, firstParen) : text
    const reason = firstParen > 0 ? text.slice(firstParen) : '导入策略跳过'
    return { id: idx + 1, gameName, reason, raw: text }
  })
}

function getFilteredImportHistorySkippedRows(batchRow) {
  const keyword = String(batchRow?._detailKeyword || '').trim().toLowerCase()
  if (!keyword) return getImportHistorySkippedRows(batchRow)
  return getImportHistorySkippedRows(batchRow).filter(item => {
    return String(item.raw || '').toLowerCase().includes(keyword)
      || String(item.gameName || '').toLowerCase().includes(keyword)
      || String(item.reason || '').toLowerCase().includes(keyword)
  })
}

function getFilteredImportHistoryGameGroups(batchRow) {
  if ((batchRow?._detailChangeType || 'all') === 'skip') return []
  const grouped = getImportHistoryGroupedChanges(batchRow)
  const keyword = String(batchRow?._detailKeyword || '').trim().toLowerCase()
  const status = batchRow?._detailStatus || 'all'
  const changeType = batchRow?._detailChangeType || 'all'
  return grouped.filter(item => {
    const matchedKeyword = !keyword || String(item.gameName || '').toLowerCase().includes(keyword)
    if (!matchedKeyword) return false
    if (changeType === 'insert' && !item.changes.some(c => c.changeType === 'INSERT')) return false
    if (changeType === 'update' && !item.changes.some(c => c.changeType === 'UPDATE')) return false
    if (status === 'active') return !item.allReverted
    if (status === 'reverted') return item.allReverted
    return true
  })
}

function getPagedImportHistoryGameGroups(batchRow) {
  const filtered = getFilteredImportHistoryGameGroups(batchRow)
  const pageNum = batchRow?._detailPageNum || 1
  const pageSize = batchRow?._detailPageSize || 10
  const start = (pageNum - 1) * pageSize
  return filtered.slice(start, start + pageSize)
}

function getPagedImportHistorySkippedRows(batchRow) {
  const filtered = getFilteredImportHistorySkippedRows(batchRow)
  const pageNum = batchRow?._detailPageNum || 1
  const pageSize = batchRow?._detailPageSize || 10
  const start = (pageNum - 1) * pageSize
  return filtered.slice(start, start + pageSize)
}

function getImportHistoryDetailTotal(batchRow) {
  if ((batchRow?._detailChangeType || 'all') === 'skip') {
    return getFilteredImportHistorySkippedRows(batchRow).length
  }
  return getFilteredImportHistoryGameGroups(batchRow).length
}

function getVisibleChangesForGroup(gameGroup, batchRow) {
  // 「含新增」/「含更新」只是对游戏分组的过滤条件（该组是否包含对应类型），
  // 组内变更应全部显示，不做二次过滤
  return gameGroup.changes
}

function getImportHistoryGameGroupStats(batchRow) {
  const grouped = getImportHistoryGroupedChanges(batchRow)
  const reverted = grouped.filter(item => item.allReverted).length
  return {
    total: grouped.length,
    reverted,
    active: grouped.length - reverted
  }
}

function onImportHistoryDetailFilterChange(batchRow) {
  batchRow._detailPageNum = 1
}

function resetImportHistoryDetailFilters(batchRow) {
  batchRow._detailKeyword = ''
  batchRow._detailStatus = 'all'
  batchRow._detailChangeType = 'all'
  batchRow._detailPageNum = 1
}

function handleImportHistoryDetailPageChange(batchRow, pageNum) {
  batchRow._detailPageNum = pageNum
}

function handleImportHistoryDetailSizeChange(batchRow, pageSize) {
  batchRow._detailPageSize = pageSize
  batchRow._detailPageNum = 1
}

// 撤回导入历史弹窗中某游戏的全部变更
async function handleRevertImportGameGroup(gameGroup, batchRow) {
  let loading
  try {
    await proxy.$modal.confirm(`确定撤回游戏「${gameGroup.gameName}」的全部变更（主表及关联表）？`)
    loading = proxy.$loading({
      lock: true,
      text: `正在撤回游戏「${gameGroup.gameName}」的变更，请稍候...`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    const res = batchRow.id > 0
      ? await revertByBatchGame(batchRow.id, gameGroup.gameId)
      : await revertByBatchNoGame(batchRow.batchNo, gameGroup.gameId)
    proxy.$modal.msgSuccess(res.msg || '撤回成功')
    const changesRes = batchRow.id > 0 ? await getBatchChanges(batchRow.id) : await getBatchChangesByNo(batchRow.batchNo)
    batchRow._changes = changesRes.data || []
    batchRow._groupedChanges = groupChangesByGame(batchRow._changes)
    if (batchRow._changes.every(c => c.reverted === 1)) batchRow.reverted = 1
  } catch (e) {
    if (e !== 'cancel') proxy.$modal.msgError('撤回失败')
  } finally {
    if (loading) loading.close()
  }
}

// 重新应用导入历史弹窗中某游戏的全部已撤回变更
async function handleReApplyImportGameGroup(gameGroup, batchRow) {
  let loading
  try {
    await proxy.$modal.confirm(`确定重新应用游戏「${gameGroup.gameName}」的全部变更（主表及关联表）？`)
    loading = proxy.$loading({
      lock: true,
      text: `正在重新应用游戏「${gameGroup.gameName}」的变更，请稍候...`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    const res = batchRow.id > 0
      ? await reApplyByBatchGame(batchRow.id, gameGroup.gameId)
      : await reApplyByBatchNoGame(batchRow.batchNo, gameGroup.gameId)
    proxy.$modal.msgSuccess(res.msg || '重新应用成功')
    const changesRes = batchRow.id > 0 ? await getBatchChanges(batchRow.id) : await getBatchChangesByNo(batchRow.batchNo)
    batchRow._changes = changesRes.data || []
    batchRow._groupedChanges = groupChangesByGame(batchRow._changes)
    if (batchRow._changes.some(c => c.reverted !== 1)) batchRow.reverted = 0
  } catch (e) {
    if (e !== 'cancel') proxy.$modal.msgError('重新应用失败')
  } finally {
    if (loading) loading.close()
  }
}

// 撤回整批
async function handleRevertImportBatch(row) {
  let loading
  try {
    await proxy.$modal.confirm(`确定撤回批次「${row.batchNo}」的全部变更？`)
    loading = proxy.$loading({
      lock: true,
      text: `正在撤回批次「${row.batchNo}」的变更，请稍候...`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    const res = row.id > 0 ? await revertImportBatch(row.id) : await revertImportBatchByNo(row.batchNo)
    proxy.$modal.msgSuccess(res.msg || '撤回成功')
    await loadImportHistory()
  } catch (e) {
    if (e !== 'cancel') proxy.$modal.msgError('撤回失败')
  } finally {
    if (loading) loading.close()
  }
}

// 重新应用整批
async function handleReApplyImportBatch(row) {
  let loading
  try {
    await proxy.$modal.confirm(`确定重新应用批次「${row.batchNo}」的全部变更？`)
    loading = proxy.$loading({
      lock: true,
      text: `正在重新应用批次「${row.batchNo}」的变更，请稍候...`,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    const res = row.id > 0 ? await reApplyImportBatch(row.id) : await reApplyImportBatchByNo(row.batchNo)
    proxy.$modal.msgSuccess(res.msg || '重新应用成功')
    await loadImportHistory()
  } catch (e) {
    if (e !== 'cancel') proxy.$modal.msgError('重新应用失败')
  } finally {
    if (loading) loading.close()
  }
}
</script>

<style scoped lang="scss">
.diff-before {
  background: #fef0f0;
  color: #f56c6c;
  padding: 1px 4px;
  border-radius: 3px;
  text-decoration: line-through;
}
.diff-after {
  background: #f0f9eb;
  color: #67c23a;
  padding: 1px 4px;
  border-radius: 3px;
}
.game-edit-form {
  :deep(.el-form-item) {
    margin-bottom: 22px;
  }
  
  :deep(.el-row) {
    margin-bottom: 0;
  }
  
  :deep(.el-divider) {
    margin: 28px 0;
  }
  
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
