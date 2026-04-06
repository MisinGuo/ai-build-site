# 多语言API支持说明

## 概述

系统已为所有动态数据添加了多语言支持。后端API会根据请求的`locale`参数自动返回对应语言的翻译数据。

## 支持的语言

- `zh-CN`: 简体中文（默认，存储在主表中）
- `zh-TW`: 繁体中文（存储在gb_translations表）
- `en`: 英语（存储在gb_translations表）

## 支持多语言的实体类型

### 1. 游戏 (game)
**翻译字段：**
- `name` - 游戏名称
- `subtitle` - 副标题
- `shortName` - 短名称
- `description` - 描述
- `promotionDesc` - 推广描述
- `discountLabel` - 折扣标签

**API端点：**
- `GET /api/public/games?locale=zh-TW` - 游戏列表
- `GET /api/public/games/{id}?locale=zh-TW` - 游戏详情
- `GET /api/public/games/search?keyword=xxx&locale=zh-TW` - 搜索游戏
- `GET /api/public/categories/{categoryId}/games?locale=zh-TW` - 分类下的游戏

### 2. 游戏盒子 (box)
**翻译字段：**
- `name` - 盒子名称
- `description` - 描述

**API端点：**
- `GET /api/public/boxes?locale=zh-TW` - 盒子列表
- `GET /api/public/boxes/{id}?locale=zh-TW` - 盒子详情

### 3. 分类 (category)
**翻译字段：**
- `name` - 分类名称
- `description` - 描述

**API端点：**
- `GET /api/public/categories?categoryType=game&locale=zh-TW` - 分类列表

### 4. 短剧 (drama)
**翻译字段：**
- `name` - 短剧名称
- `subtitle` - 副标题
- `description` - 描述
- `content` - 内容
- `director` - 导演
- `actors` - 演员
- `producer` - 制片人

**API端点：**
- 后端已实现翻译逻辑，前端需要时可调用

### 5. 文章 (article)
**说明：** 文章使用`locale`字段区分语言，不使用翻译表。每种语言的文章是独立存储的。

## 后端实现

### 翻译服务方法

```java
// 单个实体翻译
Map<String, String> translations = translationService.getEntityTranslations(entityType, entityId, locale);

// 批量实体翻译
Map<Long, Map<String, String>> batchTranslations = translationService.getBatchEntityTranslations(entityType, entityIds, locale);
```

### 应用翻译的Controller方法

```java
// 单个游戏
applyGameTranslation(game, locale);

// 游戏列表
applyGamesTranslation(games, locale);

// 单个盒子
applyBoxTranslation(box, locale);

// 盒子列表
applyBoxesTranslation(boxes, locale);

// 单个分类
applyCategoryTranslation(category, locale);

// 分类列表
applyCategoriesTranslation(categories, locale);

// 单个短剧
applyDramaTranslation(drama, locale);

// 短剧列表
applyDramasTranslation(dramas, locale);
```

## 前端使用

### 在Next-web中使用ApiClient

```typescript
import ApiClient from '@/lib/api'

// 获取游戏列表（自动使用当前页面locale）
const response = await ApiClient.getGames({
  locale: locale as any,
  pageSize: 20,
  pageNum: 1,
})

// 获取游戏详情
const response = await ApiClient.getGameDetail(gameId, locale)

// 获取分类下的游戏
const response = await ApiClient.getCategoryGames(categoryId, {
  locale: locale as any,
  pageSize: 50,
})

// 获取盒子列表
const response = await ApiClient.getBoxes({
  locale: locale as any,
})

// 获取分类列表
const response = await ApiClient.getCategories({
  locale: locale as any,
  categoryType: 'game',
})
```

### API响应格式

#### 列表接口 (TableDataInfo)
```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    {
      "id": 1,
      "name": "传奇游戏",  // 已自动翻译
      "description": "经典传奇游戏"
    }
  ],
  "total": 100
}
```

#### 详情接口 (AjaxResult)
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "传奇游戏",  // 已自动翻译
    "description": "经典传奇游戏"
  }
}
```

#### 分类列表 (AjaxResult)
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "传奇",  // 已自动翻译
      "description": "传奇类游戏"
    }
  ]
}
```

## 注意事项

1. **默认语言 (zh-CN)**：不需要翻译，直接返回主表数据
2. **其他语言**：自动从gb_translations表读取翻译，如果没有翻译则返回默认值
3. **性能优化**：列表接口使用批量翻译，一次查询获取所有翻译数据
4. **兼容性**：如果不传locale参数，默认返回zh-CN数据
5. **文章特殊处理**：文章不使用翻译表，每种语言独立存储

## 添加新语言

如果需要支持新语言（如日语、韩语）：

1. 在管理后台翻译页面添加对应语言的翻译
2. 或使用批量翻译功能自动生成翻译
3. 前端调用时传入对应的locale参数即可

## 批量翻译

管理后台提供批量翻译功能：

1. 在各管理页面（游戏、盒子、分类、短剧）选择要翻译的项目
2. 点击"批量翻译"按钮
3. 系统自动调用Google Translate API生成zh-TW和en翻译
4. 每批处理5条数据，显示实时进度
5. 自动处理超时问题，分批执行确保稳定性

## 开发者指南

### 添加新的可翻译字段

1. 在对应的Service实现类中更新翻译逻辑：
```java
if (translations.containsKey("newField")) {
    entity.setNewField(translations.get("newField"));
}
```

2. 在批量翻译时包含新字段：
```javascript
fields: {
  name: entity.name,
  newField: entity.newField || '',
  // ... 其他字段
}
```

### 添加新的实体类型

1. 创建翻译方法：`applyXxxTranslation()`和`applyXxxsTranslation()`
2. 在相关API端点中调用翻译方法
3. 在前端ApiClient中添加对应的API方法
4. 支持locale参数传递

## 测试

### 测试URL示例

```bash
# 获取繁体中文游戏列表
curl "http://localhost:8080/api/public/games?siteId=1&locale=zh-TW&pageSize=10"

# 获取英文游戏详情
curl "http://localhost:8080/api/public/games/1?locale=en"

# 获取繁体中文分类列表
curl "http://localhost:8080/api/public/categories?siteId=1&categoryType=game&locale=zh-TW"
```

### 验证翻译

1. 确认返回的name、description等字段是对应语言的翻译
2. 检查是否有翻译缺失（应返回默认中文）
3. 验证批量接口的翻译性能

## 问题排查

### 翻译未生效

1. 检查gb_translations表是否有对应的翻译记录
2. 确认locale参数格式正确（zh-CN, zh-TW, en）
3. 查看后端日志，确认翻译服务被调用
4. 使用批量翻译功能重新生成翻译

### 翻译缺失

1. 在管理后台翻译页面手动添加翻译
2. 使用批量翻译功能自动生成
3. 检查Google Translate API是否正常工作

## 相关文件

### 后端
- `PublicApiController.java` - 公开API控制器
- `ITranslationService.java` - 翻译服务接口
- `TranslationServiceImpl.java` - 翻译服务实现
- `AutoTranslationServiceImpl.java` - 自动翻译实现

### 前端
- `Next-web/src/lib/api.ts` - API客户端
- `Next-web/src/lib/api-types.ts` - API类型定义
- `Next-web/src/config/api/backend.ts` - API配置

### 管理后台
- `RuoYi-Vue3/src/api/gamebox/translation.js` - 翻译API
- `RuoYi-Vue3/src/views/gamebox/**/index.vue` - 各实体管理页面（含批量翻译）
