<template>
  <div class="field-mapping-config">
    <!-- 映射列表 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" size="small">新增映射</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="MagicStick" @click="openQuickConfig" size="small">快速配置</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Refresh" @click="refreshTableFields" :loading="loadingFields" size="small">
          刷新字段
        </el-button>
      </el-col>
    </el-row>

    <el-table :data="mappingList" border stripe size="small" max-height="400">
      <el-table-column label="源字段路径" align="center" prop="sourceField" min-width="150">
        <template #default="scope">
          <div v-if="isMultiSourceField(scope.row.sourceField)">
            <el-tag size="small" type="warning" style="margin-bottom:2px;">多字段合并</el-tag>
            <div style="color: #606266; font-size: 11px; line-height: 1.4;">
              {{ parseMultiSourceDisplay(scope.row.sourceField) }}
            </div>
          </div>
          <el-tooltip v-else-if="scope.row.transformExpression && scope.row.transformExpression.startsWith('regex:')"
            :content="'正则提取: ' + scope.row.transformExpression.substring(6)" placement="top">
            <div>
              <el-tag size="small" type="warning">正则提取</el-tag>
              <div style="color: #606266; font-size: 11px;">{{ scope.row.sourceField }}</div>
            </div>
          </el-tooltip>
          <el-tooltip v-else-if="scope.row.transformExpression === 'remove_bracket_content' || scope.row.transformExpression === 'strip_bracket_content'"
            content="去除中英文括号及内部内容，保留前后文本" placement="top">
            <div>
              <el-tag size="small" type="info">去括号内容</el-tag>
              <div style="color: #606266; font-size: 11px;">{{ scope.row.sourceField }}</div>
            </div>
          </el-tooltip>
          <el-tooltip v-else-if="scope.row.transformExpression && scope.row.transformExpression.startsWith('regex_replace:')"
            :content="'正则替换: ' + scope.row.transformExpression.substring('regex_replace:'.length)" placement="top">
            <div>
              <el-tag size="small" type="danger">正则替换</el-tag>
              <div style="color: #606266; font-size: 11px;">{{ scope.row.sourceField }}</div>
            </div>
          </el-tooltip>
          <span v-else style="word-break:break-all;">{{ scope.row.sourceField }}</span>
        </template>
      </el-table-column>
      <el-table-column label="目标位置" align="center" prop="targetLocation" width="140">
        <template #default="scope">
          <div v-if="scope.row.targetLocation === 'main'" style="display: flex; flex-direction: column; align-items: center; gap: 2px;">
            <el-tag type="success" size="small">主表</el-tag>
            <el-tag
              v-if="scope.row.conflictStrategy && scope.row.conflictStrategy !== 'fill_empty'"
              :type="scope.row.conflictStrategy === 'overwrite' ? 'warning' : 'info'"
              size="small"
              style="font-size: 10px;"
            >
              {{ scope.row.conflictStrategy === 'overwrite' ? '覆盖' : '锁定' }}
            </el-tag>
          </div>
          <div v-else-if="scope.row.targetLocation === 'category_relation'" style="display: flex; flex-direction: column; align-items: center; gap: 2px;">
            <el-tag type="danger" size="small">分类关联</el-tag>
            <el-tag
              v-if="scope.row.conflictStrategy"
              :type="scope.row.conflictStrategy === 'overwrite' ? 'warning' : 'success'"
              size="small"
              style="font-size: 10px;"
            >
              {{ scope.row.conflictStrategy === 'overwrite' ? '覆盖' : '合并' }}
            </el-tag>
          </div>
          <div v-else-if="scope.row.targetLocation === 'relation'" style="display: flex; flex-direction: column; align-items: center; gap: 2px;">
            <el-tag type="info" size="small">关联表</el-tag>
            <el-tag
              v-if="scope.row.conflictStrategy && scope.row.conflictStrategy !== 'overwrite'"
              :type="scope.row.conflictStrategy === 'fill_empty' ? 'success' : 'info'"
              size="small"
              style="font-size: 10px;"
            >
              {{ scope.row.conflictStrategy === 'fill_empty' ? '补全' : '锁定' }}
            </el-tag>
          </div>
          <div v-else-if="scope.row.targetLocation === 'promotion_link'" style="display: flex; flex-direction: column; align-items: center; gap: 2px;">
            <el-tag type="primary" size="small">扩展表</el-tag>
            <span style="color: #409eff; font-size: 11px;">推广链接</span>
            <el-tag
              v-if="scope.row.conflictStrategy && scope.row.conflictStrategy !== 'overwrite'"
              :type="scope.row.conflictStrategy === 'fill_empty' ? 'success' : 'info'"
              size="small"
              style="font-size: 10px;"
            >
              {{ scope.row.conflictStrategy === 'fill_empty' ? '补全' : '锁定' }}
            </el-tag>
          </div>
          <div v-else-if="scope.row.targetLocation === 'platform_data'" style="display: flex; flex-direction: column; align-items: center; gap: 2px;">
            <el-tag type="warning" size="small">扩展表</el-tag>
            <span style="color: #E6A23C; font-size: 11px;">平台数据</span>
            <el-tag
              v-if="scope.row.conflictStrategy && scope.row.conflictStrategy !== 'overwrite'"
              :type="scope.row.conflictStrategy === 'fill_empty' ? 'success' : 'info'"
              size="small"
              style="font-size: 10px;"
            >
              {{ scope.row.conflictStrategy === 'fill_empty' ? '补全' : '锁定' }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="目标字段" align="center" prop="targetField" width="150">
        <template #default="scope">
          <div style="display: flex; flex-direction: column; align-items: center;">
            <span>{{ scope.row.targetField }}</span>
            <span v-if="scope.row.jsonKeyDescription" style="color: #909399; font-size: 11px;">
              {{ scope.row.jsonKeyDescription }}
            </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="字段描述" align="center" prop="remark" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          <span v-if="scope.row.remark" style="color: #606266;">{{ scope.row.remark }}</span>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="值映射" align="center" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.valueMapping" type="success" size="small">
            {{ getValueMappingCount(scope.row.valueMapping) }}条
          </el-tag>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.$index)" size="small">修改</el-button>
          <el-button link type="success" icon="Setting" @click="handleValueMapping(scope.$index)" size="small">值映射</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.$index)" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 字段映射编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogOpen" width="800px" append-to-body>
      <el-form ref="mappingFormRef" :model="currentMapping" :rules="rules" label-width="120px">
        <el-form-item label="映射模式">
          <div class="source-mode-cards">
            <div
              class="source-mode-card"
              :class="{ active: sourceMappingMode === 'one_to_one' }"
              @click="handleSourceModeChange('one_to_one'); sourceMappingMode = 'one_to_one'"
            >
              <el-icon class="mode-icon"><Connection /></el-icon>
              <div class="mode-title">单字段映射</div>
              <div class="mode-desc">一对一 / 一对多</div>
            </div>
            <div
              class="source-mode-card"
              :class="{ active: sourceMappingMode === 'many_to_one' }"
              @click="handleSourceModeChange('many_to_one'); sourceMappingMode = 'many_to_one'"
            >
              <el-icon class="mode-icon"><Finished /></el-icon>
              <div class="mode-title">多字段合并</div>
              <div class="mode-desc">多字段 → 数组</div>
            </div>
          </div>
          <div class="mode-hint">
            <template v-if="sourceMappingMode === 'one_to_one'">
              <el-icon style="color: #409eff; vertical-align: middle;"><InfoFilled /></el-icon>
              为同一源字段配置多条规则映射到不同目标字段；配合下方「分割提取」可将逗号分隔值按索引分发到多个字段
            </template>
            <template v-else>
              <el-icon style="color: #e6a23c; vertical-align: middle;"><InfoFilled /></el-icon>
              将多个源字段的非空值按顺序合并为 JSON 数组，存入同一目标字段（如 screenshot1~5 → screenshotList）
            </template>
          </div>
        </el-form-item>

        <!-- 单字段模式 -->
        <el-form-item v-if="sourceMappingMode === 'one_to_one'" label="源字段路径" prop="sourceField">
          <el-input v-model="currentMapping.sourceField" placeholder="如：gamename">
            <template #append>
              <el-button icon="QuestionFilled" @click="showSourceFieldHelp" />
            </template>
          </el-input>
          <div class="field-hint">支持嵌套路径 <code>data.list.0.name</code>、数组索引 <code>photos[0].url</code>、数组遍历 <code>photos[].url</code></div>
        </el-form-item>

        <!-- 多字段合并模式 -->
        <el-form-item v-if="sourceMappingMode === 'many_to_one'" label="合并源字段" prop="sourceField">
          <div class="multi-source-panel">
            <div class="multi-source-input-row">
              <el-input
                v-model="newMultiSourceField"
                placeholder="输入源字段路径，如 screenshot1，按回车快速添加"
                @keyup.enter="addMultiSourceField"
              >
                <template #prefix><el-icon><Plus /></el-icon></template>
              </el-input>
              <el-button type="primary" @click="addMultiSourceField" :disabled="!newMultiSourceField.trim()">添加</el-button>
            </div>
            <div v-if="multiSourceFieldsList.length > 0" class="multi-source-tag-area">
              <div
                v-for="(field, idx) in multiSourceFieldsList"
                :key="idx"
                class="multi-source-tag-item"
              >
                <span class="field-index">{{ idx + 1 }}</span>
                <span class="field-name">{{ field }}</span>
                <el-icon class="field-remove" @click="removeMultiSourceField(idx)"><Close /></el-icon>
              </div>
            </div>
            <div v-else class="multi-source-empty">
              <el-icon><ArrowUpBold /></el-icon> 在上方输入源字段路径后点击「添加」
            </div>
            <div class="multi-source-footer">
              <el-icon style="color: #e6a23c;"><Warning /></el-icon>
              目标字段类型建议选「<strong>JSON数组</strong>」，空值字段自动跳过
            </div>
          </div>
        </el-form-item>

        <!-- 转换规则（一对一辅助） -->
        <el-form-item v-if="sourceMappingMode === 'one_to_one'" label="转换规则">
          <el-select v-model="transformRuleType" @change="handleTransformRuleTypeChange" placeholder="无" style="width: 100%">
            <el-option label="无" value="" />
            <el-option label="正则 — 用正则表达式提取内容" value="regex" />
            <el-option label="正则替换 — 按正则批量替换文本" value="regex_replace" />
          </el-select>
          <!-- 正则配置面板 -->
          <div v-if="transformRuleType === 'regex'" class="split-config-panel">
            <el-input v-model="regexPattern" placeholder="例如：^([^（(]+)" @input="updateRegexExpression" />
            <div style="margin-top: 8px; display: flex; align-items: center; gap: 8px;">
              <el-checkbox v-model="regexMatchAll" @change="updateRegexExpression">提取所有匹配项（用于多分类关联）</el-checkbox>
            </div>
            <div class="split-expression-preview" style="margin-top: 8px;">
              <span class="preview-label">生成表达式</span>
              <code class="preview-code">{{ regexMatchAll ? 'regex_all' : 'regex' }}:{{ regexPattern || '...' }}</code>
            </div>
            <div class="split-example">
              有捕获组 <code>( )</code> 时取第一组，否则取整个匹配。常用预设：
              <el-button size="small" link type="primary" @click="applyRegexPresetBracket">去括号前内容</el-button>
              <el-button size="small" link type="primary" @click="applyRegexPresetBracketContent">提取括号内容</el-button>
              <el-button size="small" link type="primary" @click="applyRegexPresetDigit">提取数字</el-button>
              <el-button size="small" link type="primary" @click="applyRegexAllPresetPipe">按竖线拆分 [^|]+</el-button>
              <el-button size="small" link type="primary" @click="applyRegexAllPresetDotOrPipe">按竖线或点拆分 [^|.]+</el-button>
              <div style="margin-top: 6px; color: #909399;">
                单次：<code>战影破穹（国战）</code> + <code>^([^（(]+)</code> → <code>战影破穹</code><br />
                全部：<code>策略|国战</code> + <code>[^|]+</code> → ["策略", "国战"] → 多条分类关联
              </div>
            </div>
          </div>
          <!-- 正则替换配置面板 -->
          <div v-if="transformRuleType === 'regex_replace'" class="split-config-panel">
            <el-input v-model="regexReplacePattern" placeholder="匹配表达式，例如：[（(][^）)]*[）)]" @input="updateRegexReplaceExpression" />
            <el-input v-model="regexReplaceReplacement" style="margin-top: 8px;" placeholder="替换内容（留空表示删除匹配内容）" @input="updateRegexReplaceExpression" />
            <div class="split-expression-preview" style="margin-top: 8px;">
              <span class="preview-label">生成表达式</span>
              <code class="preview-code">regex_replace:{{ regexReplacePattern || '...' }}=&gt;{{ regexReplaceReplacement }}</code>
            </div>
            <div class="split-example" style="margin-top: 8px;">
              示例：<code>regex_replace:[（(][^）)]*[）)]=&gt;</code>（删除括号内容）
            </div>
          </div>
        </el-form-item>

        <el-form-item label="目标位置" prop="targetLocation">
          <el-select v-model="currentMapping.targetLocation" placeholder="请选择目标位置" @change="handleLocationChange" style="width: 100%">
            <el-option label="主表(gb_games)" value="main">
              <div style="display: flex; flex-direction: column;">
                <span style="font-weight: 500;">主表(gb_games)</span>
                <span style="color: #909399; font-size: 12px;">游戏基本信息，包括名称、图标、描述等</span>
              </div>
            </el-option>
            <el-option label="分类关联(gb_game_category_relation)" value="category_relation">
              <div style="display: flex; flex-direction: column;">
                <span style="font-weight: 500;">分类关联(gb_game_category_relation)</span>
                <span style="color: #909399; font-size: 12px;">游戏与分类的关联关系，支持多分类</span>
              </div>
            </el-option>
            <el-option label="关联表(gb_box_game_relations)" value="relation">
              <div style="display: flex; flex-direction: column;">
                <span style="font-weight: 500;">关联表(gb_box_game_relations)</span>
                <span style="color: #909399; font-size: 12px;">盒子与游戏的关联信息，包括折扣、扶持等</span>
              </div>
            </el-option>
            <el-option label="关联表JSON字段(gb_box_game_relations)" value="ext">
              <div style="display: flex; flex-direction: column;">
                <span style="font-weight: 500;">关联表JSON字段(gb_box_game_relations)</span>
                <span style="color: #909399; font-size: 12px;">推广链接或平台数据，存储在关联表的JSON字段中，可自定义key</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 扩展表JSON字段选择 -->
        <el-form-item v-if="currentMapping.targetLocation === 'ext'" label="JSON字段位置" prop="jsonFieldType">
          <el-select v-model="currentMapping.jsonFieldType" placeholder="请选择JSON字段位置" @change="handleJsonFieldTypeChange" style="width: 100%">
            <el-option label="推广链接(gb_box_game_relations.promotion_links)" value="promotion_link">
              <div style="display: flex; flex-direction: column;">
                <span style="font-weight: 500;">推广链接</span>
                <span style="color: #909399; font-size: 12px;">关联表的promotion_links JSON字段</span>
              </div>
            </el-option>
            <el-option label="平台数据(gb_box_game_relations.platform_data)" value="platform_data">
              <div style="display: flex; flex-direction: column;">
                <span style="font-weight: 500;">平台数据</span>
                <span style="color: #909399; font-size: 12px;">关联表的platform_data JSON字段</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item :label="getTargetFieldLabel()" prop="targetField">
          <el-select 
            v-model="currentMapping.targetField" 
            :placeholder="getTargetFieldPlaceholder()" 
            filterable
            :allow-create="isJsonField()"
            @change="handleTargetFieldChange"
            style="width: 100%"
          >
            <el-option-group
              v-for="group in currentTableFieldOptions"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="field in group.options"
                :key="field.value"
                :label="field.label"
                :value="field.value"
              >
                <div style="display: flex; justify-content: space-between;">
                  <span>{{ field.label }}</span>
                  <span v-if="field.comment" style="color: #8492a6; font-size: 12px;">{{ field.comment }}</span>
                </div>
              </el-option>
            </el-option-group>
          </el-select>
          <div v-if="isJsonField()" style="color: #E6A23C; font-size: 12px; margin-top: 4px;">
            💡 请输入新的key名称（下拉列表中的key已被占用，仅供参考）
          </div>
        </el-form-item>

        <!-- JSON key描述 -->
        <el-form-item v-if="isJsonField()" label="JSON Key描述" prop="jsonKeyDescription">
          <el-input 
            v-model="currentMapping.jsonKeyDescription" 
            placeholder="请输入这个JSON key的用途描述，便于后续识别和复用（如：平台下载链接、游戏评分等）" 
            maxlength="100"
            show-word-limit
          />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            💡 填写清晰的描述可以帮助您在配置其他字段映射时快速找到已有的key，避免重复创建
          </div>
        </el-form-item>

        <el-row>
          <el-col :span="12">
            <el-form-item label="字段类型" prop="fieldType">
              <el-input 
                v-if="isCurrentFieldTypeDisabled"
                v-model="currentMapping.fieldType" 
                disabled
                style="width: 100%"
              >
                <template #suffix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
              <el-select 
                v-else
                v-model="currentMapping.fieldType" 
                placeholder="请选择字段类型" 
                style="width: 100%"
              >
                <el-option label="字符串" value="string" />
                <el-option label="整数" value="integer" />
                <el-option label="小数" value="decimal" />
                <el-option label="日期时间" value="datetime" />
                <el-option label="JSON对象" value="json" />
                <el-option label="JSON数组" value="array" />
              </el-select>
              <div v-if="isCurrentFieldTypeDisabled" style="color: #909399; font-size: 12px; margin-top: 4px;">
                字段类型由数据库schema自动确定
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否必填" prop="isRequired">
              <el-radio-group v-model="currentMapping.isRequired">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 冲突策略：对所有位置均有效 -->
        <el-form-item
          label="冲突策略"
          prop="conflictStrategy"
        >
          <!-- 分类关联特有：merge / overwrite -->
          <template v-if="currentMapping.targetLocation === 'category_relation'">
            <el-select v-model="currentMapping.conflictStrategy" style="width: 100%">
              <el-option label="合并 —— 保留现有分类，追加新分类（默认）" value="merge" />
              <el-option label="覆盖 —— 清除现有分类后替换为新分类" value="overwrite" />
            </el-select>
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">
              决定当同名游戏已存在时，分类关联数据的更新方式
            </div>
          </template>
          <!-- 其他位置：fill_empty / overwrite / skip -->
          <template v-else>
            <el-select v-model="currentMapping.conflictStrategy" style="width: 100%">
              <el-option label="始终覆盖 —— 每次导入都用新数据覆盖（默认）" value="overwrite" />
              <el-option label="补全空字段 —— 仅当目标字段为空时写入" value="fill_empty" />
              <el-option label="保持不变 —— 首次导入后不再更新（跳过）" value="skip" />
            </el-select>
            <el-alert
              v-if="currentMapping.targetLocation === 'main'"
              type="info"
              :closable="false"
              show-icon
              style="margin-top: 8px; font-size: 12px;"
            >
              <template #title>
                <span style="font-size: 12px;">与盒子优先级的关系</span>
              </template>
              <div style="font-size: 12px; line-height: 1.6;">
                <div>• <b>始终覆盖</b>：受优先级影响。高优先级盒子覆盖低优先级数据；低优先级盒子不触碰高优先级数据。</div>
                <div>• <b>补全空字段</b>与<b>保持不变</b>：不受优先级影响，按字面逻辑执行。</div>
              </div>
            </el-alert>
            <div v-else style="color: #909399; font-size: 12px; margin-top: 4px;">
              决定当同名游戏已存在时，此字段的更新方式
            </div>
          </template>
        </el-form-item>

        <el-form-item label="默认值" prop="defaultValue">
          <el-input v-model="currentMapping.defaultValue" placeholder="当源字段为空时使用的默认值" />
        </el-form-item>

        <el-form-item label="字段描述" prop="remark">
          <el-input 
            v-model="currentMapping.remark" 
            type="textarea" 
            :rows="2"
            placeholder="请输入字段描述，说明此映射的用途（如：导入平台游戏名称、平台分类编号等）" 
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button type="primary" @click="submitMapping">确定</el-button>
      </template>
    </el-dialog>

    <!-- 值映射编辑对话框 -->
    <el-dialog title="值映射配置" v-model="valueMappingDialogOpen" width="900px" append-to-body>
      <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
        <template #title>
          <span>值映射说明</span>
        </template>
        <div>值映射用于将源数据的值转换为目标系统的值。例如：分类名 → 分类ID</div>
        <div style="margin-top: 8px;">
          <strong>当前站点：{{ currentSiteName }}</strong>
          <span style="margin-left: 8px; color: #E6A23C;">⚠️ 不同站点的分类ID不同，请确保映射正确</span>
        </div>
      </el-alert>

      <el-form ref="valueMappingFormRef" :model="valueMappingForm" label-width="120px">
        <el-form-item label="映射类型">
          <el-radio-group v-model="valueMappingForm.mappingType" @change="handleMappingTypeChange">
            <el-radio label="direct">直接映射</el-radio>
            <el-radio label="regex">正则替换</el-radio>
            <el-radio label="function">函数转换</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 直接映射 -->
        <div v-if="valueMappingForm.mappingType === 'direct'">
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="Plus" @click="addValueMapping" size="small">添加映射</el-button>
            </el-col>
            <el-col :span="1.5" v-if="getFieldSelectorType().type === 'category'">
              <el-button type="success" plain icon="MagicStick" @click="quickAddAllCategories" size="small" :loading="loadingCategories">
                快速添加所有分类
              </el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="warning" plain icon="Refresh" @click="clearAllMappings" size="small">清空映射</el-button>
            </el-col>
          </el-row>

          <el-table :data="valueMappingForm.mappings" border stripe size="small" max-height="300">
            <el-table-column label="源值" prop="sourceValue" width="200">
              <template #default="scope">
                <el-input v-model="scope.row.sourceValue" placeholder="原始值" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="目标值" prop="targetValue" width="200">
              <template #default="scope">
                <el-input
                  v-model="scope.row.targetValue"
                  placeholder="请输入希望映射的值"
                  size="small"
                  clearable
                />
                <div v-if="loadingTargetValueSuggestions" class="mapping-suggestion-hint">
                  正在加载后端可用值...
                </div>
                <div v-else-if="targetValueSuggestions.length > 0" class="mapping-suggestion-wrap">
                  <div class="mapping-suggestion-hint">后端可用值（点击可填入）：</div>
                  <div class="mapping-suggestion-list">
                    <el-tag
                      v-for="item in targetValueSuggestions"
                      :key="`${item.value}-${item.label}`"
                      size="small"
                      effect="plain"
                      class="mapping-suggestion-tag"
                      @click="scope.row.targetValue = item.value"
                    >
                      {{ item.label }}
                    </el-tag>
                  </div>
                  <div v-if="targetValueSuggestionTotal > targetValueSuggestions.length" class="mapping-suggestion-hint">
                    仅展示前 {{ targetValueSuggestions.length }} 项（共 {{ targetValueSuggestionTotal }} 项）
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="说明" prop="description">
              <template #default="scope">
                <el-input v-model="scope.row.description" placeholder="备注说明" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="scope">
                <el-button link type="danger" icon="Delete" @click="removeValueMapping(scope.$index)" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 正则替换 -->
        <div v-else-if="valueMappingForm.mappingType === 'regex'">
          <el-form-item label="正则表达式">
            <el-input v-model="valueMappingForm.regexPattern" placeholder="如：^(.+?)\\s*\\(.*\\)$" />
          </el-form-item>
          <el-form-item label="替换为">
            <el-input v-model="valueMappingForm.regexReplacement" placeholder="如：$1" />
          </el-form-item>
          <el-form-item label="测试">
            <el-input v-model="regexTestInput" placeholder="输入测试文本">
              <template #append>
                <el-button @click="testRegex">测试</el-button>
              </template>
            </el-input>
            <div v-if="regexTestResult" style="margin-top: 8px; color: #67c23a;">
              结果: {{ regexTestResult }}
            </div>
          </el-form-item>
        </div>

        <!-- 函数转换 -->
        <div v-else-if="valueMappingForm.mappingType === 'function'">
          <el-form-item label="转换函数">
            <el-input 
              type="textarea" 
              v-model="valueMappingForm.functionCode" 
              :rows="10"
              placeholder="function transform(value) {&#10;  // 转换逻辑&#10;  return value;&#10;}"
            />
            <div style="color: #909399; font-size: 12px; margin-top: 4px;">
              函数必须返回转换后的值，输入参数为 value
            </div>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="valueMappingDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="saveValueMapping">保存</el-button>
      </template>
    </el-dialog>

    <!-- 源字段路径帮助对话框 -->
    <el-dialog title="源字段路径说明" v-model="helpDialogOpen" width="600px" append-to-body>
      <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
        <template #title>基本语法</template>
        <div style="line-height: 1.8;">
          <p style="margin: 5px 0;"><code style="background: #f0f2f5; padding: 2px 6px; border-radius: 3px;">gamename</code> - 直接访问字段</p>
          <p style="margin: 5px 0;"><code style="background: #f0f2f5; padding: 2px 6px; border-radius: 3px;">photo.0.url</code> - 访问数组第一个元素的url字段</p>
          <p style="margin: 5px 0;"><code style="background: #f0f2f5; padding: 2px 6px; border-radius: 3px;">user.address.city</code> - 访问多级嵌套对象</p>
          <p style="margin: 5px 0;"><code style="background: #f0f2f5; padding: 2px 6px; border-radius: 3px;">tags.0</code> - 访问数组元素</p>
        </div>
      </el-alert>
      
      <el-alert type="success" :closable="false">
        <template #title>示例说明</template>
        <div style="line-height: 1.6;">
          <p style="margin: 5px 0; font-weight: 500;">JSON数据结构：</p>
          <pre style="background: #f5f7fa; padding: 12px; border-radius: 4px; overflow-x: auto; font-size: 12px;">{
  "gamename": "仙宗大掌门",
  "photo": [
    { "url": "https://example.com/1.jpg" },
    { "url": "https://example.com/2.jpg" }
  ],
  "user": {
    "address": {
      "city": "北京"
    }
  }
}</pre>
          <p style="margin: 8px 0 5px 0; font-weight: 500;">字段路径示例：</p>
          <ul style="margin: 5px 0; padding-left: 20px;">
            <li><code>gamename</code> → "仙宗大掌门"</li>
            <li><code>photo.0.url</code> → "https://example.com/1.jpg"</li>
            <li><code>user.address.city</code> → "北京"</li>
          </ul>
        </div>
      </el-alert>
      <template #footer>
        <el-button type="primary" @click="helpDialogOpen = false">知道了</el-button>
      </template>
    </el-dialog>

    <!-- 快速配置对话框 -->
    <el-dialog title="快速配置字段映射" v-model="quickConfigDialogOpen" width="90%" append-to-body :close-on-click-modal="false">
      <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
        <template #title>快速配置说明</template>
        <div style="line-height: 1.6;">
          <p style="margin: 5px 0;">📤 上传 <strong>Excel</strong> 或 <strong>JSON</strong> 样例文件，系统将自动解析字段并提供可视化配置界面</p>
          <p style="margin: 5px 0; color: #67c23a;">✅ 支持嵌套字段路径（如：<code>photo.0.url</code>、<code>data.list.name</code>）</p>
          <p style="margin: 5px 0; color: #409eff;">🎯 智能匹配常见字段名，自动推荐目标字段和位置</p>
        </div>
      </el-alert>

      <el-steps :active="quickConfigStep" finish-status="success" style="margin-bottom: 20px;">
        <el-step title="上传文件" />
        <el-step title="配置映射" />
        <el-step title="完成" />
      </el-steps>

      <!-- 步骤1: 上传文件 -->
      <div v-if="quickConfigStep === 0">
        <el-upload
          class="upload-demo"
          drag
          :auto-upload="false"
          :on-change="handleFileUpload"
          :limit="1"
          accept=".json,.xls,.xlsx"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 JSON 或 Excel 文件，用于解析字段结构
            </div>
          </template>
        </el-upload>

        <div v-if="parsedFields.length > 0" style="margin-top: 20px;">
          <el-divider>解析结果</el-divider>
          <el-alert type="success" :closable="false" style="margin-bottom: 12px;">
            <template #title>成功解析到 {{ parsedFields.length }} 个字段</template>
          </el-alert>
          
          <div style="max-height: 300px; overflow-y: auto; border: 1px solid #dcdfe6; border-radius: 4px; padding: 12px;">
            <el-tag v-for="field in parsedFields" :key="field" style="margin: 4px;">{{ field }}</el-tag>
          </div>

          <div style="margin-top: 16px; text-align: right;">
            <el-button type="primary" @click="quickConfigStep = 1">下一步：配置映射</el-button>
          </div>
        </div>
      </div>

      <!-- 步骤2: 配置映射 -->
      <div v-if="quickConfigStep === 1">
        <div style="display: flex; gap: 20px; height: 500px;">
          <!-- 左侧：源字段列表 -->
          <div style="flex: 1; border: 1px solid #dcdfe6; border-radius: 4px; padding: 12px; overflow-y: auto;">
            <h4 style="margin-top: 0;">源字段列表</h4>
            <el-input 
              v-model="sourceFieldFilter" 
              placeholder="搜索字段..." 
              size="small" 
              clearable
              style="margin-bottom: 12px;"
            />
            <div v-for="field in filteredSourceFields" :key="field" style="margin-bottom: 8px;">
              <el-card 
                :body-style="{ padding: '10px' }"
                shadow="hover"
                @click="selectSourceField(field)"
                style="cursor: pointer;"
                :class="{ 'selected-field': selectedSourceField === field }"
              >
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <span><el-icon><Document /></el-icon> {{ field }}</span>
                  <el-tag 
                    v-if="quickMappings.some(m => m.sourceField === field)" 
                    type="success" 
                    size="small"
                  >
                    已配置
                  </el-tag>
                </div>
              </el-card>
            </div>
          </div>

          <!-- 中间：配置区域 -->
          <div style="flex: 1; border: 1px solid #dcdfe6; border-radius: 4px; padding: 12px; overflow-y: auto;">
            <h4 style="margin-top: 0;">映射配置</h4>
            
            <div v-if="selectedSourceField">
              <el-form label-width="100px" size="small">
                <el-form-item label="源字段">
                  <el-tag type="primary">{{ selectedSourceField }}</el-tag>
                </el-form-item>

                <el-form-item label="目标位置">
                  <el-select v-model="quickMappingForm.targetLocation" placeholder="请选择" style="width: 100%;">
                    <el-option label="主表(gb_games)" value="main" />
                    <el-option label="分类关联(gb_game_category_relation)" value="category_relation" />
                    <el-option label="关联表(gb_box_game_relations)" value="relation" />
                    <el-option label="关联表-推广链接" value="promotion_link" />
                    <el-option label="关联表-平台数据" value="platform_data" />
                  </el-select>
                </el-form-item>

                <el-form-item label="目标字段">
                  <el-select 
                    v-model="quickMappingForm.targetField" 
                    placeholder="请选择"
                    filterable
                    allow-create
                    style="width: 100%;"
                  >
                    <el-option-group
                      v-for="group in getQuickConfigFieldOptions()"
                      :key="group.label"
                      :label="group.label"
                    >
                      <el-option
                        v-for="field in group.options"
                        :key="field.value"
                        :label="field.comment ? `${field.label} (${field.comment})` : field.label"
                        :value="field.value"
                      >
                        <div style="display: flex; justify-content: space-between;">
                          <span>{{ field.label }}</span>
                          <span style="color: #8492a6; font-size: 12px;">{{ field.comment }}</span>
                        </div>
                      </el-option>
                    </el-option-group>
                  </el-select>
                </el-form-item>

                <el-form-item label="字段类型">
                  <el-input 
                    v-if="isQuickFieldTypeDisabled"
                    v-model="quickMappingForm.fieldType" 
                    disabled
                    style="width: 100%;"
                  >
                    <template #suffix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                  <el-select 
                    v-else
                    v-model="quickMappingForm.fieldType" 
                    placeholder="请选择" 
                    style="width: 100%;"
                  >
                    <el-option label="字符串" value="string" />
                    <el-option label="整数" value="integer" />
                    <el-option label="小数" value="decimal" />
                    <el-option label="日期时间" value="datetime" />
                    <el-option label="JSON对象" value="json" />
                    <el-option label="JSON数组" value="array" />
                  </el-select>
                  <div v-if="isQuickFieldTypeDisabled" style="color: #909399; font-size: 12px; margin-top: 4px;">
                    字段类型由数据库schema自动确定
                  </div>
                </el-form-item>

                <el-form-item label="字段描述">
                  <el-input v-model="quickMappingForm.remark" placeholder="选填" />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" @click="addQuickMapping" size="small">添加映射</el-button>
                  <el-button @click="selectedSourceField = null" size="small">取消</el-button>
                </el-form-item>
              </el-form>
            </div>
            <div v-else style="text-align: center; color: #909399; padding: 50px 0;">
              请从左侧选择一个源字段开始配置
            </div>
          </div>

          <!-- 右侧：已配置映射列表 -->
          <div style="flex: 1; border: 1px solid #dcdfe6; border-radius: 4px; padding: 12px; overflow-y: auto;">
            <h4 style="margin-top: 0;">已配置映射 ({{ quickMappings.length }})</h4>
            
            <div v-for="(mapping, index) in quickMappings" :key="index" style="margin-bottom: 8px;">
              <el-card :body-style="{ padding: '10px' }" shadow="hover">
                <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                  <div style="flex: 1;">
                    <div style="margin-bottom: 4px;">
                      <el-tag size="small">{{ mapping.sourceField }}</el-tag>
                      <el-icon style="margin: 0 8px;"><Right /></el-icon>
                      <el-tag size="small" type="success">{{ mapping.targetField }}</el-tag>
                    </div>
                    <div style="font-size: 12px; color: #909399; margin-bottom: 2px;">
                      {{ getLocationLabel(mapping.targetLocation) }} | {{ mapping.fieldType }}
                    </div>
                    <div v-if="getQuickConfigFieldComment(mapping.targetField, mapping.targetLocation)" style="font-size: 12px; color: #67c23a;">
                      {{ getQuickConfigFieldComment(mapping.targetField, mapping.targetLocation) }}
                    </div>
                    <div v-if="mapping.remark" style="font-size: 12px; color: #409eff;">
                      备注: {{ mapping.remark }}
                    </div>
                  </div>
                  <el-button 
                    link 
                    type="danger" 
                    icon="Delete" 
                    @click="removeQuickMapping(index)"
                    size="small"
                  />
                </div>
              </el-card>
            </div>

            <div v-if="quickMappings.length === 0" style="text-align: center; color: #909399; padding: 50px 0;">
              暂无配置
            </div>
          </div>
        </div>

        <div style="margin-top: 16px; text-align: right;">
          <el-button @click="quickConfigStep = 0">上一步</el-button>
          <el-button type="primary" @click="applyQuickMappings" :disabled="quickMappings.length === 0">
            应用配置 ({{ quickMappings.length }})
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, getCurrentInstance } from 'vue'
import { InfoFilled, UploadFilled, Document, Right, Lock, Connection, Finished, Plus, Close, ArrowUpBold, Warning, Scissor, Odometer } from '@element-plus/icons-vue'
import { getAllTableFields, getFieldDistinctValues, getFieldSchemas, addFieldMapping, updateFieldMapping, delFieldMapping, listFieldMappingByBoxId, batchSaveOrUpdateFieldMappings } from '@/api/gamebox/fieldMapping'
import { listVisibleCategory } from '@/api/gamebox/category'
import * as XLSX from 'xlsx'

const props = defineProps({
  // 盒子ID
  boxId: {
    type: Number,
    default: null
  },
  // 站点ID
  siteId: {
    type: Number,
    default: null
  },
  // 站点名称
  siteName: {
    type: String,
    default: '未知站点'
  },
  // 平台标识 (u2game, milu等)
  platform: {
    type: String,
    default: ''
  },
  // 初始映射列表
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const { proxy } = getCurrentInstance()

// 映射列表
const mappingList = ref([...props.modelValue])

// 表字段选项
const tableFieldOptions = ref({
  main: [],
  promotion_link: [],
  platform_data: [],
  relation: [], // 关联表字段
  category_relation: [] // 分类关联字段
})
const loadingFields = ref(false)

// 当前正在编辑的映射
const currentMapping = ref({})
const currentMappingIndex = ref(-1)
const dialogOpen = ref(false)
const dialogTitle = computed(() => currentMappingIndex.value === -1 ? '新增字段映射' : '修改字段映射')

// 多源字段 / 分割提取 UI 状态
const sourceMappingMode = ref('one_to_one') // 'one_to_one' | 'many_to_one'
const multiSourceFieldsList = ref([])
const newMultiSourceField = ref('')
const transformRuleType = ref('') // '' | 'regex' | 'regex_replace'
const regexPattern = ref('')
const regexMatchAll = ref(false)
const regexReplacePattern = ref('')
const regexReplaceReplacement = ref('')

// 判断 sourceField 是否为多字段合并格式
function isMultiSourceField(sourceField) {
  return !!(sourceField && sourceField.trim().startsWith('['))
}

// 解析多字段合并的显示文本
function parseMultiSourceDisplay(sourceField) {
  try {
    const arr = JSON.parse(sourceField)
    return Array.isArray(arr) ? arr.join(' + ') : sourceField
  } catch {
    return sourceField
  }
}

// 切换映射模式时重置状态
function handleSourceModeChange(mode) {
  if (mode === 'many_to_one') {
    // 把当前单字段迁移进列表
    const cur = currentMapping.value.sourceField
    if (cur && !cur.trim().startsWith('[')) {
      multiSourceFieldsList.value = [cur]
    } else if (cur && cur.trim().startsWith('[')) {
      try { multiSourceFieldsList.value = JSON.parse(cur) } catch { multiSourceFieldsList.value = [] }
    }
    currentMapping.value.sourceField = JSON.stringify(multiSourceFieldsList.value)
    // 关闭转换规则
    transformRuleType.value = ''
    regexPattern.value = ''
    regexMatchAll.value = false
    regexReplacePattern.value = ''
    regexReplaceReplacement.value = ''
  } else {
    // 恢复为单字段
    currentMapping.value.sourceField = multiSourceFieldsList.value[0] || ''
    multiSourceFieldsList.value = []
    // 清除转换规则
    transformRuleType.value = ''
    regexPattern.value = ''
    regexMatchAll.value = false
    regexReplacePattern.value = ''
    regexReplaceReplacement.value = ''
    if (currentMapping.value.transformExpression) {
      currentMapping.value.transformExpression = ''
    }
  }
}

// 切换转换规则类型
function handleTransformRuleTypeChange(type) {
  regexPattern.value = ''
  regexMatchAll.value = false
  regexReplacePattern.value = ''
  regexReplaceReplacement.value = ''
  currentMapping.value.transformExpression = ''
}

// 正则输入或匹配模式变化时同步到 transformExpression
function updateRegexExpression() {
  if (transformRuleType.value === 'regex') {
    const prefix = regexMatchAll.value ? 'regex_all' : 'regex'
    currentMapping.value.transformExpression = regexPattern.value ? `${prefix}:${regexPattern.value}` : ''
  }
}

function updateRegexReplaceExpression() {
  if (transformRuleType.value !== 'regex_replace') return
  if (!regexReplacePattern.value) {
    currentMapping.value.transformExpression = ''
    return
  }
  currentMapping.value.transformExpression = `regex_replace:${regexReplacePattern.value}=>${regexReplaceReplacement.value || ''}`
}
function applyRegexAllPresetPipe() { regexPattern.value = '[^|]+'; regexMatchAll.value = true; updateRegexExpression() }
function applyRegexAllPresetDotOrPipe() { regexPattern.value = '[^|.]+'; regexMatchAll.value = true; updateRegexExpression() }
function applyRegexAllPresetComma() { regexPattern.value = '[^,]+'; regexMatchAll.value = true; updateRegexExpression() }

// 应用正则预设
function applyRegexPreset(pattern) {
  regexPattern.value = pattern
  updateRegexExpression()
}
function applyRegexPresetBracket() {
  applyRegexPreset('^([^\uff08(]+)')
}
function applyRegexPresetBracketContent() {
  applyRegexPreset('[（(]([^）)]+)[）)]')
}
function applyRegexPresetDigit() {
  applyRegexPreset('(\\d+)')
}

// 添加一个源字段到多字段列表
function addMultiSourceField() {
  const f = newMultiSourceField.value.trim()
  if (f) {
    multiSourceFieldsList.value.push(f)
    currentMapping.value.sourceField = JSON.stringify(multiSourceFieldsList.value)
    newMultiSourceField.value = ''
  }
}

// 移除多字段列表中的某个字段
function removeMultiSourceField(index) {
  multiSourceFieldsList.value.splice(index, 1)
  currentMapping.value.sourceField = multiSourceFieldsList.value.length > 0
    ? JSON.stringify(multiSourceFieldsList.value)
    : ''
}

// 分割模式输入变化时同步到 transformExpression
// 根据现有 mapping 初始化多源/转换规则 UI 状态
function syncSourceUIState(mapping) {
  const sf = mapping.sourceField || ''
  const te = mapping.transformExpression || ''
  if (sf.trim().startsWith('[')) {
    sourceMappingMode.value = 'many_to_one'
    try { multiSourceFieldsList.value = JSON.parse(sf) } catch { multiSourceFieldsList.value = [] }
    transformRuleType.value = ''
    regexPattern.value = ''
  } else {
    sourceMappingMode.value = 'one_to_one'
    multiSourceFieldsList.value = []
    if (te === 'trim_bracket' || te.startsWith('split:')) {
      transformRuleType.value = 'regex'
      regexPattern.value = '^([^\uff08(]+)'
      regexMatchAll.value = false
      regexReplacePattern.value = ''
      regexReplaceReplacement.value = ''
    } else if (te.startsWith('regex_all:')) {
      transformRuleType.value = 'regex'
      regexMatchAll.value = true
      regexPattern.value = te.substring('regex_all:'.length)
      regexReplacePattern.value = ''
      regexReplaceReplacement.value = ''
    } else if (te.startsWith('split_all:')) {
      transformRuleType.value = 'regex'
      regexMatchAll.value = true
      const sep = te.substring('split_all:'.length) || '|'
      regexPattern.value = `[^${sep}]+`
      regexReplacePattern.value = ''
      regexReplaceReplacement.value = ''
    } else if (te.startsWith('regex:')) {
      transformRuleType.value = 'regex'
      regexMatchAll.value = false
      regexPattern.value = te.substring(6)
      regexReplacePattern.value = ''
      regexReplaceReplacement.value = ''
    } else if (te === 'remove_bracket_content' || te === 'strip_bracket_content') {
      // 兼容旧配置：自动回显为“正则替换”
      transformRuleType.value = 'regex_replace'
      regexPattern.value = ''
      regexMatchAll.value = false
      regexReplacePattern.value = '[（(][^）)]*[）)]'
      regexReplaceReplacement.value = ''
      updateRegexReplaceExpression()
    } else if (te.startsWith('regex_replace:')) {
      transformRuleType.value = 'regex_replace'
      regexPattern.value = ''
      regexMatchAll.value = false
      const expression = te.substring('regex_replace:'.length)
      const splitIndex = expression.indexOf('=>')
      if (splitIndex >= 0) {
        regexReplacePattern.value = expression.substring(0, splitIndex)
        regexReplaceReplacement.value = expression.substring(splitIndex + 2)
      } else {
        regexReplacePattern.value = expression
        regexReplaceReplacement.value = ''
      }
    } else {
      transformRuleType.value = ''
      regexPattern.value = ''
      regexMatchAll.value = false
      regexReplacePattern.value = ''
      regexReplaceReplacement.value = ''
    }
  }
  newMultiSourceField.value = ''
}

// 已使用的JSON keys（用于显示已有的key列表）
const usedJsonKeys = ref({
  promotion_link: [], // {value: 'download_url', label: 'download_url', description: '下载链接'}
  platform_data: []
})

// 值映射对话框
const valueMappingDialogOpen = ref(false)
const valueMappingForm = ref({
  mappingType: 'direct',
  mappings: [],
  regexPattern: '',
  regexReplacement: '',
  functionCode: ''
})
const regexTestInput = ref('')
const regexTestResult = ref('')

// 分类列表（用于快速添加所有分类）
const categoryList = ref([])
const loadingCategories = ref(false)

// 字段值选项（从目标表查询）
const fieldValueOptions = ref([])
const loadingFieldValues = ref(false)

// 帮助对话框
const helpDialogOpen = ref(false)

// 快速配置相关
const quickConfigDialogOpen = ref(false)
const quickConfigStep = ref(0)
const parsedFields = ref([])
const sourceFieldFilter = ref('')
const selectedSourceField = ref(null)
const quickMappings = ref([])
const quickMappingForm = reactive({
  targetLocation: 'main',
  targetField: '',
  fieldType: 'string',
  remark: ''
})

// 数据库字段schema（从后端动态获取）
const dbFieldSchemas = ref({})

// 从后端获取字段schema定义
async function loadFieldSchemas() {
  try {
    const response = await getFieldSchemas()
    if (response.code === 200) {
      dbFieldSchemas.value = response.data || {}
    }
  } catch (error) {
    console.error('加载字段schema失败:', error)
  }
}

// 根据目标位置和目标字段获取标准字段类型
function getStandardFieldType(location, field) {
  if (!location || !field) return null
  
  // JSON字段位置允许用户自定义类型
  if (location === 'promotion_link' || location === 'platform_data' || location === 'ext') {
    return null
  }
  
  const locationSchemas = dbFieldSchemas.value[location]
  if (!locationSchemas) return null
  
  const fieldSchema = locationSchemas[field]
  if (!fieldSchema) return null
  
  return fieldSchema.type || null
}

// 快速配置：判断字段类型是否可编辑
const isQuickFieldTypeDisabled = computed(() => {
  const standardType = getStandardFieldType(
    quickMappingForm.targetLocation, 
    quickMappingForm.targetField
  )
  return standardType !== null  // 有标准类型则禁用
})

// 主对话框：判断字段类型是否可编辑
const isCurrentFieldTypeDisabled = computed(() => {
  const location = currentMapping.value.targetLocation
  const field = currentMapping.value.targetField
  
  // JSON字段类型的特殊处理
  if (location === 'promotion_link' || location === 'platform_data' || location === 'ext') {
    return false  // JSON字段允许用户选择
  }
  
  const standardType = getStandardFieldType(location, field)
  return standardType !== null
})

// 监听目标位置和目标字段的变化，自动设置必需的字段类型（快速配置）
watch(() => [quickMappingForm.targetLocation, quickMappingForm.targetField], ([location, field]) => {
  const standardType = getStandardFieldType(location, field)
  if (standardType) {
    quickMappingForm.fieldType = standardType
  }
})

// 监听目标位置和目标字段的变化，自动设置必需的字段类型（主对话框）
watch(() => [currentMapping.value.targetLocation, currentMapping.value.targetField], ([location, field]) => {
  // JSON字段位置允许用户选择类型
  if (location === 'promotion_link' || location === 'platform_data' || location === 'ext') {
    return
  }
  
  const standardType = getStandardFieldType(location, field)
  if (standardType) {
    currentMapping.value.fieldType = standardType
  }
})

// 过滤后的源字段
const filteredSourceFields = computed(() => {
  if (!sourceFieldFilter.value) return parsedFields.value
  return parsedFields.value.filter(field => 
    field.toLowerCase().includes(sourceFieldFilter.value.toLowerCase())
  )
})

// 当前站点名称
const currentSiteName = computed(() => props.siteName)

const MAX_TARGET_VALUE_SUGGESTIONS = 30

const targetValueSuggestions = computed(() => {
  const selectorType = getFieldSelectorType()
  if (selectorType.type === 'category') {
    return categoryList.value.slice(0, MAX_TARGET_VALUE_SUGGESTIONS).map(cat => ({
      value: String(cat.id),
      label: `${cat.name} (${cat.id})`
    }))
  }
  if (selectorType.type === 'enum') {
    return (selectorType.options || []).slice(0, MAX_TARGET_VALUE_SUGGESTIONS).map(item => ({
      value: item.value,
      label: `${item.label} (${item.value})`
    }))
  }
  if (selectorType.type === 'fieldValue') {
    return (fieldValueOptions.value || []).slice(0, MAX_TARGET_VALUE_SUGGESTIONS).map(item => ({
      value: String(item),
      label: String(item)
    }))
  }
  return []
})

const targetValueSuggestionTotal = computed(() => {
  const selectorType = getFieldSelectorType()
  if (selectorType.type === 'category') return categoryList.value.length
  if (selectorType.type === 'enum') return (selectorType.options || []).length
  if (selectorType.type === 'fieldValue') return (fieldValueOptions.value || []).length
  return 0
})

const loadingTargetValueSuggestions = computed(() => {
  const selectorType = getFieldSelectorType()
  if (selectorType.type === 'category') return loadingCategories.value
  if (selectorType.type === 'fieldValue') return loadingFieldValues.value
  return false
})

// 当前字段注释
const currentFieldComment = ref('')

// 获取当前字段的选择器类型
function getFieldSelectorType() {
  if (currentMappingIndex.value === -1) return { type: 'text', tableName: null, fieldName: null }
  const mapping = mappingList.value[currentMappingIndex.value]
  const targetField = mapping?.targetField
  const targetLocation = mapping?.targetLocation
  
  // 分类关联表的字段 - 使用分类选择器
  if (targetLocation === 'category_relation') {
    return { type: 'category', tableName: null, fieldName: null }
  }
  
  // 特定枚举字段 - 使用预定义选项
  const enumFields = {
    'device_support': [
      { label: '安卓', value: 'android' },
      { label: 'iOS', value: 'ios' },
      { label: '双端', value: 'both' }
    ],
    'game_type': [
      { label: '官方', value: 'official' },
      { label: '折扣', value: 'discount' },
      { label: 'BT版', value: 'bt' },
      { label: 'H5游戏', value: 'h5' }
    ]
  }
  
  if (targetField && enumFields[targetField]) {
    return { type: 'enum', options: enumFields[targetField] }
  }
  
  // 其他字段 - 从目标表查询字段的所有不同值
  let tableName = 'gb_games' // 默认主表
  if (targetLocation === 'promotion_link') {
    // JSON字段，不支持查询
    return { type: 'text', tableName: null, fieldName: null }
  } else if (targetLocation === 'platform_data') {
    // JSON字段，不支持查询
    return { type: 'text', tableName: null, fieldName: null }
  } else if (targetLocation === 'relation') {
    // 关联表字段 - 支持查询不同值
    tableName = 'gb_box_game_relations'
  }
  
  // 主表或关联表字段 - 支持查询不同值
  if (targetField && targetField !== '') {
    return { type: 'fieldValue', tableName, fieldName: targetField }
  }
  
  return { type: 'text', tableName: null, fieldName: null }
}

// 判断是否应该显示选择器
function shouldShowSelector() {
  return getFieldSelectorType().type !== 'text'
}

// 表单校验规则
const rules = {
  sourceField: [{ required: true, message: '请输入源字段路径', trigger: 'blur' }],
  targetField: [{ required: true, message: '请输入目标字段名', trigger: 'blur' }],
  targetLocation: [{ required: true, message: '请选择目标位置', trigger: 'change' }],
  fieldType: [{ required: true, message: '请选择字段类型', trigger: 'change' }]
}

// 计算当前目标位置的字段选项
const currentTableFieldOptions = computed(() => {
  const location = currentMapping.value.targetLocation || 'main'
  
  // 如果是扩展表JSON字段，返回已使用的keys
  if (location === 'ext') {
    const jsonType = currentMapping.value.jsonFieldType
    if (jsonType === 'promotion_link') {
      return [{
        label: '已使用的推广链接Keys',
        options: usedJsonKeys.value.promotion_link
      }]
    } else if (jsonType === 'platform_data') {
      return [{
        label: '已使用的平台数据Keys',
        options: usedJsonKeys.value.platform_data
      }]
    }
    return []
  }
  
  // 其他位置返回表字段
  const fields = tableFieldOptions.value[location] || []
  const labels = {
    'main': '主表字段',
    'relation': '关联表字段',
    'category_relation': '分类关联字段'
  }
  return [{
    label: labels[location] || '字段',
    options: fields
  }]
})

// 监听映射列表变化，同步到父组件
watch(mappingList, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })

// 监听父组件传入的值变化
watch(() => props.modelValue, (newVal) => {
  if (JSON.stringify(newVal) !== JSON.stringify(mappingList.value)) {
    mappingList.value = [...newVal]
    // 更新已使用的JSON keys
    updateUsedJsonKeys()
  }
}, { deep: true, immediate: true })

// 初始化时加载字段schema
watch(() => props.modelValue, async () => {
  // 只在第一次加载时获取schema
  if (Object.keys(dbFieldSchemas.value).length === 0) {
    await loadFieldSchemas()
  }
}, { immediate: true, once: true })

// 获取字段注释
function getFieldComment(row) {
  const location = row.targetLocation || 'main'
  const fields = tableFieldOptions.value[location] || []
  const field = fields.find(f => f.value === row.targetField)
  return field?.comment || ''
}

// 获取值映射数量
function getValueMappingCount(valueMapping) {
  if (!valueMapping) return 0
  try {
    const mapping = JSON.parse(valueMapping)
    if (mapping.type === 'direct' && mapping.mappings) {
      return Object.keys(mapping.mappings).length
    }
  } catch (e) {
    console.error('解析值映射失败:', e)
  }
  return 0
}

// 判断当前选择的是否为JSON字段
function isJsonField() {
  return currentMapping.value.targetLocation === 'ext'
}

// 获取目标字段标签
function getTargetFieldLabel() {
  if (isJsonField()) {
    return 'JSON Key名称'
  }
  return '目标字段'
}

// 获取目标字段输入框的占位符
function getTargetFieldPlaceholder() {
  const location = currentMapping.value.targetLocation
  if (location === 'ext') {
    const jsonType = currentMapping.value.jsonFieldType
    if (jsonType === 'promotion_link') {
      return '请选择或输入key（如：download_url, web_url）'
    } else if (jsonType === 'platform_data') {
      return '请选择或输入key（如：custom_field_1, rating）'
    }
    return '请先选择JSON字段位置'
  } else if (location === 'category_relation') {
    return '请选择字段（通常为category_id）'
  } else if (location === 'relation') {
    return '请选择关联表字段（如：discount_label）'
  }
  return '请选择目标字段'
}

// 新增映射
function handleAdd() {
  currentMapping.value = {
    sourceField: '',
    targetField: '',
    targetLocation: 'main',
    jsonFieldType: '', // JSON字段类型：promotion_link 或 platform_data
    jsonKeyDescription: '', // JSON key的描述
    fieldType: 'string',
    isRequired: '0',
    defaultValue: '',
    valueMapping: null,
    conflictStrategy: 'fill_empty'  // main 的默认值
  }
  currentMappingIndex.value = -1
  syncSourceUIState(currentMapping.value)
  updateUsedJsonKeys() // 更新已使用的JSON keys
  dialogOpen.value = true
}

// 修改映射
function handleUpdate(index) {
  const mapping = mappingList.value[index]
  currentMapping.value = { 
    ...mapping,
    // 兼容旧数据：如果targetLocation是promotion_link或platform_data，转换为ext
    targetLocation: (mapping.targetLocation === 'promotion_link' || mapping.targetLocation === 'platform_data') 
      ? 'ext' 
      : mapping.targetLocation,
    jsonFieldType: mapping.targetLocation === 'promotion_link' || mapping.targetLocation === 'platform_data'
      ? mapping.targetLocation
      : mapping.jsonFieldType || '',
    jsonKeyDescription: mapping.jsonKeyDescription || '',
    conflictStrategy: mapping.conflictStrategy || 'fill_empty'
  }
  currentMappingIndex.value = index
  syncSourceUIState(currentMapping.value)
  updateUsedJsonKeys() // 更新已使用的JSON keys
  dialogOpen.value = true
}

// 删除映射
function handleDelete(index) {
  const mapping = mappingList.value[index]
  proxy.$modal.confirm('确定删除此映射配置吗？').then(async () => {
    try {
      if (mapping.id) {
        await delFieldMapping(mapping.id)
      }
      mappingList.value.splice(index, 1)
      emit('update:modelValue', mappingList.value)
      proxy.$modal.msgSuccess('删除成功')
    } catch (error) {
      console.error('删除字段映射失败:', error)
      proxy.$modal.msgError('删除失败')
    }
  }).catch(() => {})
}

// 目标位置改变
function handleLocationChange() {
  currentMapping.value.targetField = ''
  currentMapping.value.jsonFieldType = ''
  currentMapping.value.jsonKeyDescription = ''
  currentFieldComment.value = ''
  // 根据位置重置冲突策略默认值：main=补全空字段，category_relation=合并，其他=始终覆盖
  const loc = currentMapping.value.targetLocation
  currentMapping.value.conflictStrategy = loc === 'main' ? 'fill_empty' : loc === 'category_relation' ? 'merge' : 'overwrite'
  updateUsedJsonKeys()
}

// JSON字段类型改变
function handleJsonFieldTypeChange() {
  currentMapping.value.targetField = ''
  currentMapping.value.jsonKeyDescription = ''
  updateUsedJsonKeys()
}

// 更新已使用的JSON keys列表
function updateUsedJsonKeys() {
  const promotionKeys = new Map()
  const platformKeys = new Map()
  
  mappingList.value.forEach((mapping, index) => {
    // 跳过当前正在编辑的映射
    if (index === currentMappingIndex.value) return
    
    // 兼容旧数据格式和新格式
    const isPromotion = mapping.targetLocation === 'promotion_link' || 
                       (mapping.targetLocation === 'ext' && mapping.jsonFieldType === 'promotion_link')
    const isPlatform = mapping.targetLocation === 'platform_data' || 
                      (mapping.targetLocation === 'ext' && mapping.jsonFieldType === 'platform_data')
    
    if (isPromotion && mapping.targetField) {
      promotionKeys.set(mapping.targetField, {
        value: mapping.targetField,
        label: mapping.targetField,
        comment: mapping.jsonKeyDescription || mapping.remark || ''
      })
    } else if (isPlatform && mapping.targetField) {
      platformKeys.set(mapping.targetField, {
        value: mapping.targetField,
        label: mapping.targetField,
        comment: mapping.jsonKeyDescription || mapping.remark || ''
      })
    }
  })
  
  usedJsonKeys.value.promotion_link = Array.from(promotionKeys.values())
  usedJsonKeys.value.platform_data = Array.from(platformKeys.values())
}

// 目标字段改变
function handleTargetFieldChange() {
  const location = currentMapping.value.targetLocation || 'main'
  
  // 如果是JSON字段，从已使用的keys中查找描述
  if (location === 'ext') {
    const jsonType = currentMapping.value.jsonFieldType
    const key = currentMapping.value.targetField
    if (jsonType && key) {
      const usedKeys = usedJsonKeys.value[jsonType] || []
      const existingKey = usedKeys.find(k => k.value === key)
      if (existingKey && existingKey.comment && !currentMapping.value.jsonKeyDescription) {
        currentMapping.value.jsonKeyDescription = existingKey.comment
      }
    }
    return
  }
  
  // 其他位置从表字段中查找注释
  const fields = tableFieldOptions.value[location] || []
  const field = fields.find(f => f.value === currentMapping.value.targetField)
  currentFieldComment.value = field?.comment || ''
}

// 提交映射
async function submitMapping() {
  // 多字段合并模式：提交前同步 sourceField 并做简单校验
  if (sourceMappingMode.value === 'many_to_one') {
    if (multiSourceFieldsList.value.length === 0) {
      proxy.$modal.msgWarning('多字段合并模式下，请至少添加一个源字段')
      return
    }
    currentMapping.value.sourceField = JSON.stringify(multiSourceFieldsList.value)
  }

  const valid = await proxy.$refs.mappingFormRef.validate().catch(() => false)
  if (!valid) return
  
  try {
    // 准备提交的数据
    const mappingData = { ...currentMapping.value }
    mappingData.boxId = props.boxId
    
    // 如果是扩展表JSON字段，需要转换targetLocation为实际的字段类型
    if (mappingData.targetLocation === 'ext') {
      if (!mappingData.jsonFieldType) {
        proxy.$modal.msgWarning('请选择JSON字段位置')
        return
      }
      // 保存实际的targetLocation（兼容后端）
      mappingData.targetLocation = mappingData.jsonFieldType
    }
    
    // 通用重复检查：不允许多个源字段映射到同一个目标字段
    const targetLocation = mappingData.targetLocation
    const targetField = mappingData.targetField
    
    const duplicateIndex = mappingList.value.findIndex((mapping, index) => {
      // 跳过当前正在编辑的映射
      if (index === currentMappingIndex.value) return false
      
      // 获取映射的实际目标位置（兼容新旧格式）
      let mappingLocation = mapping.targetLocation
      if (mappingLocation === 'ext' && mapping.jsonFieldType) {
        mappingLocation = mapping.jsonFieldType
      }
      
      // 检查是否映射到相同的目标位置和目标字段
      return mappingLocation === targetLocation && mapping.targetField === targetField
    })
    
    if (duplicateIndex !== -1) {
      const locationNames = {
        'main': '主表',
        'promotion_link': '推广链接',
        'platform_data': '平台数据',
        'relation': '关联表',
        'category_relation': '分类关联'
      }
      const locationName = locationNames[targetLocation] || targetLocation
      const duplicateMapping = mappingList.value[duplicateIndex]
      
      proxy.$modal.msgError(
        `目标字段 "${targetField}" 在${locationName}中已被源字段 "${duplicateMapping.sourceField}" 映射，` +
        `不允许多个源字段映射到同一个目标字段（会导致数据覆盖和值映射冲突）`
      )
      return
    }
    
    // 调用API保存
    if (currentMappingIndex.value === -1) {
      // 新增
      const response = await addFieldMapping(mappingData)
      // 添加返回的ID到列表
      if (response.data) {
        mappingData.id = response.data
      }
      mappingList.value.push(mappingData)
      proxy.$modal.msgSuccess('新增成功')
    } else {
      // 修改
      await updateFieldMapping(mappingData)
      mappingList.value[currentMappingIndex.value] = mappingData
      proxy.$modal.msgSuccess('修改成功')
    }
    
    dialogOpen.value = false
    emit('update:modelValue', mappingList.value)
  } catch (error) {
    console.error('保存字段映射失败:', error)
    proxy.$modal.msgError('保存失败')
  }
}

// 显示源字段帮助
function showSourceFieldHelp() {
  helpDialogOpen.value = true
}

// 处理值映射
function handleValueMapping(index) {
  currentMappingIndex.value = index
  const mapping = mappingList.value[index]
  
  // 解析现有的值映射配置
  if (mapping.valueMapping) {
    try {
      const parsed = JSON.parse(mapping.valueMapping)
      valueMappingForm.value.mappingType = parsed.type || 'direct'
      
      if (parsed.type === 'direct' && parsed.mappings) {
        // 将对象转换为数组格式
        valueMappingForm.value.mappings = Object.entries(parsed.mappings).map(([key, val]) => ({
          sourceValue: key,
          targetValue: typeof val === 'object' ? val.value : val,
          description: typeof val === 'object' ? val.description : ''
        }))
      } else if (parsed.type === 'regex') {
        valueMappingForm.value.regexPattern = parsed.pattern || ''
        valueMappingForm.value.regexReplacement = parsed.replacement || ''
      } else if (parsed.type === 'function') {
        valueMappingForm.value.functionCode = parsed.code || ''
      }
    } catch (e) {
      console.error('解析值映射配置失败:', e)
      valueMappingForm.value = {
        mappingType: 'direct',
        mappings: [],
        regexPattern: '',
        regexReplacement: '',
        functionCode: ''
      }
    }
  } else {
    // 新建值映射
    valueMappingForm.value = {
      mappingType: 'direct',
      mappings: [],
      regexPattern: '',
      regexReplacement: '',
      functionCode: ''
    }
  }
  
  valueMappingDialogOpen.value = true
  
  // 打开对话框后立即加载后端可用值（仅作参考展示）
  const selectorType = getFieldSelectorType()
  if (selectorType.type === 'category') {
    ensureCategoriesLoaded()
  } else if (selectorType.type === 'fieldValue') {
    ensureFieldValuesLoaded()
  }
}

// 添加值映射
function addValueMapping() {
  valueMappingForm.value.mappings.push({
    sourceValue: '',
    targetValue: '',
    description: ''
  })
}

// 删除值映射
function removeValueMapping(index) {
  valueMappingForm.value.mappings.splice(index, 1)
}

// 快速添加所有分类
async function quickAddAllCategories() {
  if (!props.siteId) {
    proxy.$modal.msgWarning('站点ID未设置')
    return
  }
  
  await ensureCategoriesLoaded()
  
  if (categoryList.value.length === 0) {
    proxy.$modal.msgWarning('当前网站没有可用分类')
    return
  }
  
  // 询问用户是否要清空现有映射
  let shouldClear = false
  if (valueMappingForm.value.mappings.length > 0) {
    try {
      await proxy.$modal.confirm('检测到已有映射配置，是否先清空再添加所有分类？', '确认操作', {
        confirmButtonText: '清空并添加',
        cancelButtonText: '追加添加',
        type: 'warning',
        distinguishCancelAndClose: true
      })
      shouldClear = true
    } catch (action) {
      if (action === 'cancel') {
        // 用户选择追加添加
        shouldClear = false
      } else {
        // 用户关闭对话框，取消操作
        return
      }
    }
  }
  
  // 清空现有映射（如果选择了清空）
  if (shouldClear) {
    valueMappingForm.value.mappings = []
  }
  
  // 为每个分类添加映射
  const existingSourceValues = new Set(valueMappingForm.value.mappings.map(m => m.sourceValue))
  let addedCount = 0
  
  categoryList.value.forEach(cat => {
    // 避免重复添加
    if (!existingSourceValues.has(cat.name)) {
      valueMappingForm.value.mappings.push({
        sourceValue: cat.name,
        targetValue: String(cat.id),
        description: `${cat.description || cat.slug || '游戏分类'}`
      })
      addedCount++
    }
  })
  
  proxy.$modal.msgSuccess(`已添加 ${addedCount} 条分类映射`)
}

// 清空所有映射
function clearAllMappings() {
  if (valueMappingForm.value.mappings.length === 0) {
    proxy.$modal.msgInfo('当前没有映射配置')
    return
  }
  
  proxy.$modal.confirm(`确认清空所有 ${valueMappingForm.value.mappings.length} 条映射配置吗？`, '确认清空', {
    type: 'warning'
  }).then(() => {
    valueMappingForm.value.mappings = []
    proxy.$modal.msgSuccess('已清空所有映射配置')
  }).catch(() => {})
}

// 确保分类列表已加载
async function ensureCategoriesLoaded() {
  // 如果已经加载过且有数据，直接返回
  if (categoryList.value.length > 0) {
    return
  }
  
  if (!props.siteId) {
    proxy.$modal.msgWarning('站点ID未设置')
    return
  }
  
  loadingCategories.value = true
  
  try {
    const response = await listVisibleCategory({
      status: '1',
      categoryType: 'game',
      siteId: props.siteId,
      pageNum: 1,
      pageSize: 1000
    })
    
    categoryList.value = response.rows || []
  } catch (error) {
    console.error('加载分类列表失败:', error)
    proxy.$modal.msgError('加载分类列表失败')
  } finally {
    loadingCategories.value = false
  }
}

// 加载字段值选项
async function loadFieldValues(tableName, fieldName) {
  if (!tableName || !fieldName) return
  
  loadingFieldValues.value = true
  fieldValueOptions.value = []
  
  try {
    const response = await getFieldDistinctValues(tableName, fieldName)
    fieldValueOptions.value = response.data || []
    console.log(`已加载字段 ${tableName}.${fieldName} 的所有值:`, fieldValueOptions.value)
  } catch (error) {
    console.error(`加载字段值失败:`, error)
    proxy.$modal.msgError(`加载字段值失败`)
  } finally {
    loadingFieldValues.value = false
  }
}

// 确保字段值已加载
async function ensureFieldValuesLoaded() {
  const selectorType = getFieldSelectorType()
  if (selectorType.type === 'fieldValue' && selectorType.tableName && selectorType.fieldName) {
    await loadFieldValues(selectorType.tableName, selectorType.fieldName)
  }
}

// 映射类型改变
function handleMappingTypeChange() {
  regexTestResult.value = ''
}

// 测试正则表达式
function testRegex() {
  try {
    const pattern = new RegExp(valueMappingForm.value.regexPattern)
    const result = regexTestInput.value.replace(pattern, valueMappingForm.value.regexReplacement)
    regexTestResult.value = result
  } catch (e) {
    proxy.$modal.msgError('正则表达式错误: ' + e.message)
  }
}

// 保存值映射
async function saveValueMapping() {
  const mapping = {
    type: valueMappingForm.value.mappingType
  }
  
  if (mapping.type === 'direct') {
    mapping.mappings = {}
    valueMappingForm.value.mappings.forEach(m => {
      if (m.sourceValue && m.targetValue) {
        mapping.mappings[m.sourceValue] = m.description ? 
          { value: m.targetValue, description: m.description } : 
          m.targetValue
      }
    })
    
    if (Object.keys(mapping.mappings).length === 0) {
      proxy.$modal.msgWarning('请至少添加一条映射规则')
      return
    }
  } else if (mapping.type === 'regex') {
    if (!valueMappingForm.value.regexPattern) {
      proxy.$modal.msgWarning('请输入正则表达式')
      return
    }
    mapping.pattern = valueMappingForm.value.regexPattern
    mapping.replacement = valueMappingForm.value.regexReplacement
  } else if (mapping.type === 'function') {
    if (!valueMappingForm.value.functionCode) {
      proxy.$modal.msgWarning('请输入转换函数代码')
      return
    }
    mapping.code = valueMappingForm.value.functionCode
  }
  
  const updatedMapping = mappingList.value[currentMappingIndex.value]
  updatedMapping.valueMapping = JSON.stringify(mapping)
  
  // 立即保存到后端
  try {
    if (updatedMapping.id) {
      await updateFieldMapping(updatedMapping)
      emit('update:modelValue', mappingList.value)
      valueMappingDialogOpen.value = false
      proxy.$modal.msgSuccess('值映射配置已保存')
    } else {
      valueMappingDialogOpen.value = false
      proxy.$modal.msgWarning('请先保存字段映射，再配置值映射')
    }
  } catch (error) {
    console.error('保存值映射失败:', error)
    proxy.$modal.msgError('保存值映射失败')
  }
}

// 加载表字段
async function loadTableFields() {
  loadingFields.value = true
  
  try {
    const response = await getAllTableFields()
    const data = response.data || {}
    
    // 后端返回的数据格式：{ main: { label, table, description, fields: [...] }, ... }
    // 需要提取 fields 字段，同时保留其他元数据供将来使用
    tableFieldOptions.value = {
      main: data.main?.fields || [],
      promotion_link: data.promotion_link?.fields || [],
      platform_data: data.platform_data?.fields || [],
      relation: data.relation?.fields || [],
      category_relation: data.category_relation?.fields || []
    }
    
    // 打印加载的字段数量，便于调试
    console.log('已加载字段:', {
      main: tableFieldOptions.value.main.length,
      promotion_link: tableFieldOptions.value.promotion_link.length,
      platform_data: tableFieldOptions.value.platform_data.length,
      relation: tableFieldOptions.value.relation.length,
      category_relation: tableFieldOptions.value.category_relation.length
    })
    
  } catch (error) {
    console.error('加载表字段失败:', error)
    // 使用fallback数据
    tableFieldOptions.value = {
      main: [
        { value: 'name', label: 'name', comment: '游戏名称', type: 'string' },
        { value: 'icon_url', label: 'icon_url', comment: '游戏图标URL', type: 'string' },
        { value: 'description', label: 'description', comment: '游戏描述', type: 'string' }
      ],
      promotion_link: [],
      platform_data: [],
      relation: [],
      category_relation: [
        { value: 'category_id', label: 'category_id', comment: '分类ID（需配置值映射）', type: 'int' }
      ]
    }
  } finally {
    loadingFields.value = false
  }
}

// 刷新表字段和映射列表
async function refreshTableFields() {
  await loadTableFields()
  
  // 如果有boxId，重新加载已保存的映射配置
  if (props.boxId) {
    try {
      const response = await listFieldMappingByBoxId(props.boxId)
      if (response && response.data) {
        mappingList.value = response.data
        emit('update:modelValue', mappingList.value)
        proxy.$modal.msgSuccess('刷新成功')
      }
    } catch (error) {
      console.error('刷新字段映射失败:', error)
      proxy.$modal.msgError('刷新字段映射失败')
    }
  } else {
    proxy.$modal.msgSuccess('字段选项刷新成功')
  }
}

// ==================== 快速配置相关方法 ====================

// 打开快速配置对话框
async function openQuickConfig() {
  quickConfigStep.value = 0
  parsedFields.value = []
  selectedSourceField.value = null
  
  // 加载字段schema定义
  await loadFieldSchemas()
  
  // 加载已有的字段映射作为初始配置
  quickMappings.value = mappingList.value.map(mapping => ({
    sourceField: mapping.sourceField,
    targetLocation: mapping.targetLocation,
    targetField: mapping.targetField,
    fieldType: mapping.fieldType || 'string',
    remark: mapping.remark || '',
    id: mapping.id // 保留ID，更新时使用
  }))
  
  quickConfigDialogOpen.value = true
}

// 处理文件上传
async function handleFileUpload(file) {
  const fileName = file.name
  const fileExtension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase()
  
  try {
    if (fileExtension === '.json') {
      // 解析 JSON 文件
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const jsonData = JSON.parse(e.target.result)
          parseJsonFields(jsonData)
        } catch (error) {
          proxy.$modal.msgError('JSON 解析失败：' + error.message)
        }
      }
      reader.readAsText(file.raw)
    } else if (fileExtension === '.xls' || fileExtension === '.xlsx') {
      // 解析 Excel 文件
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const data = new Uint8Array(e.target.result)
          const workbook = XLSX.read(data, { type: 'array' })
          const firstSheetName = workbook.SheetNames[0]
          const worksheet = workbook.Sheets[firstSheetName]
          const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
          
          // 第一行作为字段名
          if (jsonData.length > 0) {
            const fields = jsonData[0].filter(field => field && field.toString().trim())
            parsedFields.value = fields.map(f => f.toString().trim())
            proxy.$modal.msgSuccess(`成功解析 ${parsedFields.value.length} 个字段`)
          } else {
            proxy.$modal.msgWarning('Excel 文件为空')
          }
        } catch (error) {
          proxy.$modal.msgError('Excel 解析失败：' + error.message)
        }
      }
      reader.readAsArrayBuffer(file.raw)
    } else {
      proxy.$modal.msgError('不支持的文件格式，请上传 JSON 或 Excel 文件')
    }
  } catch (error) {
    proxy.$modal.msgError('文件解析失败：' + error.message)
  }
}

// 解析 JSON 字段
function parseJsonFields(data, prefix = '') {
  const fields = new Set()
  
  function traverse(obj, path) {
    if (obj === null || obj === undefined) return
    
    if (Array.isArray(obj)) {
      // 数组：取第一个元素的结构，并为子字段添加 [] 标记
      if (obj.length > 0) {
        const firstElement = obj[0]
        if (typeof firstElement === 'object' && !Array.isArray(firstElement)) {
          // 如果数组元素是对象，为每个字段添加 [] 标记
          Object.keys(firstElement).forEach(key => {
            const arrayFieldPath = path ? `${path}[].${key}` : `${key}`
            fields.add(arrayFieldPath)
            traverse(firstElement[key], arrayFieldPath)
          })
        } else {
          // 如果是简单数组，添加 [] 标记
          if (path) {
            fields.add(`${path}[]`)
          }
        }
      }
    } else if (typeof obj === 'object') {
      // 对象：遍历所有键
      Object.keys(obj).forEach(key => {
        const newPath = path ? `${path}.${key}` : key
        fields.add(newPath)
        traverse(obj[key], newPath)
      })
    }
  }
  
  // 自动跳过常见的包装字段（items、data、list）
  let actualData = data
  if (!Array.isArray(data) && typeof data === 'object') {
    // 检查是否有包装字段
    if (data.items && Array.isArray(data.items)) {
      actualData = data.items
      console.log('检测到 items 包装层，将从实际数据中解析字段')
    } else if (data.data && Array.isArray(data.data)) {
      actualData = data.data
      console.log('检测到 data 包装层，将从实际数据中解析字段')
    } else if (data.list && Array.isArray(data.list)) {
      actualData = data.list
      console.log('检测到 list 包装层，将从实际数据中解析字段')
    }
  }
  
  // 如果是数组，取第一个元素
  if (Array.isArray(actualData) && actualData.length > 0) {
    traverse(actualData[0], '')
  } else {
    traverse(actualData, '')
  }
  
  parsedFields.value = Array.from(fields).sort()
  proxy.$modal.msgSuccess(`成功解析 ${parsedFields.value.length} 个字段`)
}

// 选择源字段
function selectSourceField(field) {
  selectedSourceField.value = field
  // 智能推荐目标字段
  autoMatchTargetField(field)
}

// 智能匹配目标字段
function autoMatchTargetField(sourceField) {
  const fieldName = sourceField.toLowerCase()
  
  // 字段名映射规则
  const mappingRules = {
    'gamename': { targetField: 'name', targetLocation: 'main', fieldType: 'string' },
    'name': { targetField: 'name', targetLocation: 'main', fieldType: 'string' },
    'icon': { targetField: 'icon_url', targetLocation: 'main', fieldType: 'string' },
    'pic': { targetField: 'icon_url', targetLocation: 'main', fieldType: 'string' },
    'pic1': { targetField: 'icon_url', targetLocation: 'main', fieldType: 'string' },
    'photo[].url': { targetField: 'screenshots', targetLocation: 'main', fieldType: 'json' },
    'screenshots': { targetField: 'screenshots', targetLocation: 'main', fieldType: 'json' },
    'gametype': { targetField: 'game_type', targetLocation: 'main', fieldType: 'string' },
    'type': { targetField: 'game_type', targetLocation: 'main', fieldType: 'string' },
    'description': { targetField: 'description', targetLocation: 'main', fieldType: 'string' },
    'desc': { targetField: 'description', targetLocation: 'main', fieldType: 'string' },
    'discount': { targetField: 'discount_label', targetLocation: 'relation', fieldType: 'string' },
    'download': { targetField: 'download_url', targetLocation: 'relation', fieldType: 'string' },
    'url': { targetField: 'download_url', targetLocation: 'relation', fieldType: 'string' }
  }
  
  // 检测数组字段并自动设置为 json 类型
  let defaultFieldType = 'string'
  if (sourceField.includes('[]')) {
    defaultFieldType = 'json'
  }
  
  // 查找匹配规则
  for (const [key, value] of Object.entries(mappingRules)) {
    if (fieldName.includes(key)) {
      quickMappingForm.targetLocation = value.targetLocation
      quickMappingForm.targetField = value.targetField
      quickMappingForm.fieldType = value.fieldType
      quickMappingForm.remark = `${sourceField} 映射到 ${value.targetField}`
      return
    }
  }
  
  // 默认推荐
  quickMappingForm.targetLocation = 'main'
  quickMappingForm.targetField = sourceField.replace(/\[\]/g, '').replace(/\./g, '_')
  quickMappingForm.fieldType = defaultFieldType
  quickMappingForm.remark = ''
}

// 获取快速配置的字段选项
function getQuickConfigFieldOptions() {
  const location = quickMappingForm.targetLocation
  if (location === 'main') {
    return [{
      label: '主表字段',
      options: tableFieldOptions.value.main
    }]
  } else if (location === 'category_relation') {
    return [{
      label: '分类关联字段',
      options: tableFieldOptions.value.category_relation
    }]
  } else if (location === 'relation') {
    return [{
      label: '关联表字段',
      options: tableFieldOptions.value.relation
    }]
  } else {
    return [{
      label: 'JSON 字段（可自定义）',
      options: []
    }]
  }
}

// 获取位置标签
function getLocationLabel(location) {
  const labels = {
    'main': '主表',
    'category_relation': '分类关联',
    'relation': '关联表',
    'promotion_link': '扩展-推广链接',
    'platform_data': '扩展-平台数据'
  }
  return labels[location] || location
}

// 根据字段值和位置获取字段注释（用于快速配置）
function getQuickConfigFieldComment(fieldValue, location) {
  const fields = tableFieldOptions.value[location] || []
  const field = fields.find(f => f.value === fieldValue)
  return field?.comment || ''
}

// 添加快速映射
function addQuickMapping() {
  if (!selectedSourceField.value) {
    proxy.$modal.msgWarning('请选择源字段')
    return
  }
  if (!quickMappingForm.targetField) {
    proxy.$modal.msgWarning('请输入目标字段')
    return
  }
  
  // 检查目标字段是否重复（同一目标位置 + 同一目标字段）
  const duplicate = quickMappings.value.find(m => 
    m.targetLocation === quickMappingForm.targetLocation && 
    m.targetField === quickMappingForm.targetField
  )
  if (duplicate) {
    proxy.$modal.msgWarning(
      `目标字段 "${quickMappingForm.targetField}" 已被源字段 "${duplicate.sourceField}" 映射，` +
      `会导致数据覆盖！请使用不同的目标字段或删除已有映射。`
    )
    return
  }
  
  quickMappings.value.push({
    sourceField: selectedSourceField.value,
    targetLocation: quickMappingForm.targetLocation,
    targetField: quickMappingForm.targetField,
    fieldType: quickMappingForm.fieldType,
    remark: quickMappingForm.remark
  })
  
  // 清空选择
  selectedSourceField.value = null
  quickMappingForm.targetLocation = 'main'
  quickMappingForm.targetField = ''
  quickMappingForm.fieldType = 'string'
  quickMappingForm.remark = ''
  
  proxy.$modal.msgSuccess('添加成功')
}

// 移除快速映射
function removeQuickMapping(index) {
  quickMappings.value.splice(index, 1)
}

// 应用快速映射配置
async function applyQuickMappings() {
  if (quickMappings.value.length === 0) {
    proxy.$modal.msgWarning('请至少配置一个字段映射')
    return
  }
  
  try {
    console.log('开始批量应用快速映射，共 ' + quickMappings.value.length + ' 个字段')
    
    // 准备批量保存的数据
    const mappingsToSave = quickMappings.value.map(mapping => ({
      id: mapping.id || null,
      boxId: props.boxId,
      resourceType: 'game',
      sourceField: mapping.sourceField,
      targetField: mapping.targetField,
      targetLocation: mapping.targetLocation,
      fieldType: mapping.fieldType,
      remark: mapping.remark,
      status: '1'
    }))
    
    // 调用批量接口
    const result = await batchSaveOrUpdateFieldMappings(mappingsToSave)
    
    if (result && result.code === 200) {
      const { successCount, updateCount, failedCount, errors } = result.data
      
      const messages = []
      if (successCount > 0) messages.push(`新增 ${successCount} 个`)
      if (updateCount > 0) messages.push(`更新 ${updateCount} 个`)
      if (failedCount > 0) messages.push(`失败 ${failedCount} 个`)
      
      if (failedCount > 0 && errors.length > 0) {
        proxy.$modal.msgWarning(`字段映射处理完成：${messages.join('，')}。\n${errors.join('\n')}`)
      } else {
        proxy.$modal.msgSuccess(`字段映射处理成功：${messages.join('，')}`)
      }
      
      quickConfigDialogOpen.value = false
      
      // 重新加载完整的字段映射数据（包括值映射信息）
      if (props.boxId) {
        try {
          const response = await listFieldMappingByBoxId(props.boxId)
          if (response && response.data) {
            mappingList.value = response.data
            emit('update:modelValue', mappingList.value)
          }
        } catch (error) {
          console.error('重新加载字段映射失败:', error)
        }
      }
    } else {
      proxy.$modal.msgError('批量保存失败：' + (result.msg || '未知错误'))
    }
  } catch (error) {
    console.error('应用快速配置失败:', error)
    proxy.$modal.msgError('应用配置失败: ' + error.message)
  }
}

// 初始化
loadTableFields()
</script>

<style scoped>
.field-mapping-config {
  width: 100%;
}

/* ===== 映射模式卡片 ===== */
.source-mode-cards {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.source-mode-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 14px 12px 12px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafafa;
  user-select: none;
}

.source-mode-card:hover {
  border-color: #a0cfff;
  background: #f0f7ff;
}

.source-mode-card.active {
  border-color: #409eff;
  background: linear-gradient(135deg, #ecf5ff 0%, #f0f7ff 100%);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.source-mode-card .mode-icon {
  font-size: 22px;
  color: #c0c4cc;
  margin-bottom: 6px;
  transition: color 0.2s;
}

.source-mode-card.active .mode-icon {
  color: #409eff;
}

.source-mode-card .mode-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 3px;
}

.source-mode-card .mode-desc {
  font-size: 11px;
  color: #909399;
}

.source-mode-card.active .mode-title {
  color: #409eff;
}

.mode-hint {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
  display: flex;
  align-items: flex-start;
  gap: 4px;
}

/* ===== 单字段提示 ===== */
.field-hint {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
  line-height: 1.5;
}

.field-hint code {
  background: #f0f2f5;
  padding: 1px 4px;
  border-radius: 3px;
  color: #e6573e;
  font-family: 'Courier New', monospace;
  font-size: 11px;
}

/* ===== 多字段合并面板 ===== */
.multi-source-panel {
  width: 100%;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.multi-source-input-row {
  display: flex;
  gap: 0;
  border-bottom: 1px solid #e4e7ed;
}

.multi-source-input-row .el-input {
  flex: 1;
  border-right: none;
}

.multi-source-input-row .el-input :deep(.el-input__wrapper) {
  border-radius: 0;
  box-shadow: none !important;
  border: none;
  background: #fafafa;
}

.multi-source-input-row .el-button {
  border-radius: 0;
  border: none;
  border-left: 1px solid #e4e7ed;
  height: 36px;
  padding: 0 16px;
}

.multi-source-tag-area {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: 4px 0;
  background: #fff;
  max-height: 160px;
  overflow-y: auto;
}

.multi-source-tag-item {
  display: flex;
  align-items: center;
  padding: 7px 12px;
  border-bottom: 1px solid #f5f7fa;
  transition: background 0.15s;
}

.multi-source-tag-item:last-child {
  border-bottom: none;
}

.multi-source-tag-item:hover {
  background: #f5f7fa;
}

.field-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  background: #409eff;
  color: #fff;
  border-radius: 50%;
  font-size: 11px;
  font-weight: 600;
  flex-shrink: 0;
  margin-right: 10px;
}

.field-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
  font-family: 'Courier New', monospace;
}

.field-remove {
  color: #c0c4cc;
  cursor: pointer;
  padding: 2px;
  border-radius: 3px;
  transition: all 0.15s;
}

.field-remove:hover {
  color: #f56c6c;
  background: #fef0f0;
}

.multi-source-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 20px;
  color: #c0c4cc;
  font-size: 13px;
  background: #fafafa;
}

.multi-source-footer {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  background: #fdf6ec;
  border-top: 1px solid #fde2ae;
  font-size: 12px;
  color: #906030;
}

/* ===== 分割提取面板 ===== */
.split-mode-toggle {
  display: flex;
  align-items: center;
}

.split-config-panel {
  margin-top: 12px;
  padding: 14px 16px;
  background: linear-gradient(135deg, #f0f7ff 0%, #f5f8ff 100%);
  border: 1px solid #c6e2ff;
  border-radius: 8px;
}

.config-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 6px;
  font-weight: 500;
}

.split-expression-preview {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
  padding: 8px 12px;
  background: #fff;
  border: 1px solid #d9ecff;
  border-radius: 6px;
}

.preview-label {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}

.preview-code {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #409eff;
  font-weight: 600;
  background: #ecf5ff;
  padding: 2px 8px;
  border-radius: 4px;
}

.split-example {
  margin-top: 8px;
  font-size: 11.5px;
  color: #909399;
  line-height: 1.6;
}

.split-example code {
  background: #f0f2f5;
  padding: 1px 4px;
  border-radius: 3px;
  color: #e6573e;
  font-family: 'Courier New', monospace;
  font-size: 11px;
}

.mapping-suggestion-wrap {
  margin-top: 6px;
}

.mapping-suggestion-hint {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.mapping-suggestion-list {
  margin-top: 4px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.mapping-suggestion-tag {
  cursor: pointer;
}
</style>
