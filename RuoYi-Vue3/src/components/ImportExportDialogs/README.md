# 导入导出组件

这是一组可复用的导入导出对话框组件，用于在不同页面中实现数据的导入导出功能。

## 组件列表

### 1. ExportDialog（普通导出对话框）
用于导出选中的数据，支持 Excel 和 JSON 格式。

**Props：**
- `modelValue`: Boolean - 对话框显示状态
- `selectedCount`: Number - 选中的数据数量
- `availableLanguages`: Array - 可用的语言列表
- `exportFormat`: String - 导出格式（'excel' | 'json'）
- `includeTranslations`: Boolean - 是否包含翻译数据
- `selectedLanguages`: Array - 选中要导出的语言
- `loading`: Boolean - 加载状态

**Events：**
- `update:modelValue` - 更新对话框状态
- `confirm` - 确认导出
- `update:exportFormat` - 更新导出格式
- `update:includeTranslations` - 更新是否包含翻译
- `update:selectedLanguages` - 更新选中的语言

**Slots：**
- `exportTips` - 自定义导出说明

### 2. ImportDialog（普通导入对话框）
用于导入数据文件，支持 Excel 和 JSON 格式。

**Props：**
- `modelValue`: Boolean - 对话框显示状态
- `loading`: Boolean - 加载状态
- `previewData`: Array - 预览数据
- `translationsData`: Array - 翻译数据

**Events：**
- `update:modelValue` - 更新对话框状态
- `confirm` - 确认导入
- `fileChange` - 文件变化事件
- `fileRemove` - 文件移除事件
- `update:previewData` - 更新预览数据
- `update:translationsData` - 更新翻译数据

**Slots：**
- `importTips` - 自定义导入说明
- `previewColumns` - 自定义预览表格列

### 3. FullExportDialog（全站导出对话框）
用于导出所有站点的完整数据。

**Props：**
- `modelValue`: Boolean - 对话框显示状态
- `exportFormat`: String - 导出格式（'excel' | 'json'）
- `loading`: Boolean - 加载状态

**Events：**
- `update:modelValue` - 更新对话框状态
- `confirm` - 确认导出
- `update:exportFormat` - 更新导出格式

**Slots：**
- `exportTips` - 自定义导出说明

### 4. FullImportDialog（全站导入对话框）
用于导入完整的多站点数据，支持网站映射配置。

**Props：**
- `modelValue`: Boolean - 对话框显示状态
- `loading`: Boolean - 加载状态
- `siteList`: Array - 网站列表
- `importSites`: Array - 导入文件中的网站数据
- `importData`: Array - 导入的数据
- `importTranslations`: Array - 导入的翻译数据
- `importRelations`: Array - 导入的关联关系
- `siteMapping`: Object - 网站映射关系
- `createDefaultAsNewSite`: Boolean - 是否将默认配置创建为新网站
- `dataLabel`: String - 数据标签（如"分类数量"、"文章数量"）

**Events：**
- `update:modelValue` - 更新对话框状态
- `confirm` - 确认导入
- `fileChange` - 文件变化事件
- `fileRemove` - 文件移除事件
- `update:importSites` - 更新网站数据
- `update:importData` - 更新导入数据
- `update:importTranslations` - 更新翻译数据
- `update:importRelations` - 更新关联关系
- `update:siteMapping` - 更新网站映射
- `update:createDefaultAsNewSite` - 更新创建选项

**Slots：**
- `importTips` - 自定义导入说明
- `defaultConfigTips` - 自定义默认配置说明

## 使用示例

```vue
<template>
  <div>
    <!-- 普通导出 -->
    <ExportDialog
      v-model="exportDialogOpen"
      :selectedCount="selectedIds.length"
      :availableLanguages="languages"
      v-model:exportFormat="exportFormat"
      v-model:includeTranslations="includeTranslations"
      v-model:selectedLanguages="selectedLanguages"
      :loading="exportLoading"
      @confirm="handleExport"
    >
      <template #exportTips>
        <p>• 自定义导出说明</p>
      </template>
    </ExportDialog>

    <!-- 普通导入 -->
    <ImportDialog
      v-model="importDialogOpen"
      :loading="importLoading"
      :previewData="previewData"
      :translationsData="translationsData"
      @confirm="handleImport"
      @fileChange="handleFileChange"
      @fileRemove="handleFileRemove"
    >
      <template #importTips>
        <p>• 自定义导入说明</p>
      </template>
      <template #previewColumns>
        <el-table-column prop="name" label="名称" />
      </template>
    </ImportDialog>

    <!-- 全站导出 -->
    <FullExportDialog
      v-model="fullExportDialogOpen"
      v-model:exportFormat="fullExportFormat"
      :loading="fullExportLoading"
      @confirm="handleFullExport"
    />

    <!-- 全站导入 -->
    <FullImportDialog
      v-model="fullImportDialogOpen"
      :loading="fullImportLoading"
      :siteList="siteList"
      :importSites="importSites"
      :importData="importData"
      :importTranslations="importTranslations"
      :importRelations="importRelations"
      v-model:siteMapping="siteMapping"
      v-model:createDefaultAsNewSite="createDefaultAsNewSite"
      dataLabel="数据数量"
      @confirm="handleFullImport"
      @fileChange="handleFullImportFileChange"
      @fileRemove="handleFullImportFileRemove"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  ExportDialog, 
  ImportDialog, 
  FullExportDialog, 
  FullImportDialog 
} from '@/components/ImportExportDialogs'

// 状态定义
const exportDialogOpen = ref(false)
const importDialogOpen = ref(false)
const fullExportDialogOpen = ref(false)
const fullImportDialogOpen = ref(false)

// ... 其他状态和方法
</script>
```

## 注意事项

1. 所有组件都使用 `v-model` 来控制显示状态
2. 文件上传处理需要在父组件中实现具体的解析逻辑
3. 导出功能需要在父组件中实现具体的数据处理和文件生成逻辑
4. 全站导入组件的网站映射功能需要配合具体业务逻辑使用
5. Slots 提供了灵活的自定义能力，可以根据不同页面需求调整说明文本和表格列

## 目录结构

```
src/components/ImportExportDialogs/
├── ExportDialog.vue          # 普通导出组件
├── ImportDialog.vue          # 普通导入组件
├── FullExportDialog.vue      # 全站导出组件
├── FullImportDialog.vue      # 全站导入组件
└── index.js                  # 组件导出文件
```
