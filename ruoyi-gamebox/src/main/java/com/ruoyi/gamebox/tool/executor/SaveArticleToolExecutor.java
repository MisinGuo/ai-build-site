package com.ruoyi.gamebox.tool.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbMasterArticle;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IGbMasterArticleService;
import com.ruoyi.gamebox.service.IGbMasterArticleGamesService;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxesService;
import com.ruoyi.gamebox.tool.SystemTool;
import com.ruoyi.gamebox.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保存文章工具执行器
 * 用于将生成的文章保存为草稿到指定网站
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
@Component
@SystemTool(
    code = "save_article",
    name = "保存文章工具",
    description = "将生成的文章内容保存到数据库，支持设置网站、分类、状态等信息，并可关联游戏、盒子、短剧",
    inputs = "[{\"name\":\"title\",\"type\":\"text\",\"label\":\"文章标题\",\"required\":true,\"description\":\"文章的标题\"},{\"name\":\"content\",\"type\":\"textarea\",\"label\":\"文章内容\",\"required\":true,\"description\":\"文章的正文内容（支持Markdown格式）\",\"rows\":10},{\"name\":\"siteId\",\"type\":\"number\",\"label\":\"目标网站ID\",\"required\":true,\"description\":\"文章所属网站的ID\"},{\"name\":\"categoryId\",\"type\":\"number\",\"label\":\"文章分类ID\",\"required\":false,\"description\":\"文章分类ID（可选）\"},{\"name\":\"description\",\"type\":\"textarea\",\"label\":\"文章摘要\",\"required\":false,\"description\":\"文章的简短描述或摘要\",\"rows\":3},{\"name\":\"keywords\",\"type\":\"text\",\"label\":\"关键词\",\"required\":false,\"description\":\"文章的SEO关键词（逗号分隔）\"},{\"name\":\"author\",\"type\":\"text\",\"label\":\"作者\",\"required\":false,\"description\":\"文章作者\"},{\"name\":\"coverUrl\",\"type\":\"text\",\"label\":\"封面图URL\",\"required\":false,\"description\":\"文章封面图片URL\"},{\"name\":\"status\",\"type\":\"text\",\"label\":\"发布状态\",\"required\":false,\"default\":\"0\",\"description\":\"文章状态：0-草稿，1-已发布，2-已删除\"},{\"name\":\"gameIds\",\"type\":\"text\",\"label\":\"关联游戏ID\",\"required\":false,\"description\":\"关联的游戏ID，多个用逗号分隔\"},{\"name\":\"gameBoxIds\",\"type\":\"text\",\"label\":\"关联游戏盒子ID\",\"required\":false,\"description\":\"关联的游戏盒子ID，多个用逗号分隔\"},{\"name\":\"dramaIds\",\"type\":\"text\",\"label\":\"关联短剧ID\",\"required\":false,\"description\":\"关联的短剧ID，多个用逗号分隔\"}]",
    outputs = "[{\"name\":\"articleId\",\"type\":\"number\",\"label\":\"文章ID\",\"description\":\"保存成功后的文章ID\"},{\"name\":\"title\",\"type\":\"text\",\"label\":\"文章标题\",\"description\":\"保存的文章标题\"},{\"name\":\"status\",\"type\":\"text\",\"label\":\"文章状态\",\"description\":\"文章的发布状态\"},{\"name\":\"siteId\",\"type\":\"number\",\"label\":\"网站ID\",\"description\":\"文章所属网站ID\"},{\"name\":\"categoryId\",\"type\":\"number\",\"label\":\"分类ID\",\"description\":\"文章所属分类ID\"},{\"name\":\"success\",\"type\":\"boolean\",\"label\":\"是否成功\",\"description\":\"保存是否成功\"},{\"name\":\"message\",\"type\":\"text\",\"label\":\"返回消息\",\"description\":\"操作结果消息\"}]",
    order = 100
)
public class SaveArticleToolExecutor implements ToolExecutor {
    
    private static final Logger log = LoggerFactory.getLogger(SaveArticleToolExecutor.class);
    
    @Autowired
    private IGbArticleService articleService;
    
    @Autowired
    private IGbMasterArticleService masterArticleService;
    
    @Autowired
    private IGbMasterArticleGamesService masterArticleGamesService;
    
    @Autowired
    private IGbMasterArticleGameBoxesService masterArticleGameBoxesService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public String getType() {
        return "save_article";
    }
    
    @Override
    @Transactional
    public Map<String, Object> execute(Map<String, Object> params) throws Exception {
        log.info("执行保存文章工具，参数: {}", params);
        
        // 从params中获取文章数据
        String title = (String) params.get("title");
        // 支持从 content 或 generatedText 字段获取内容
        String content = (String) params.get("content");
        if (content == null || content.trim().isEmpty()) {
            content = (String) params.get("generatedText");
        }
        Long siteId = getLongParam(params, "siteId");
        Long categoryId = getLongParam(params, "categoryId");
        String description = (String) params.get("description");
        String keywords = (String) params.get("keywords");
        String author = (String) params.get("author");
        String coverUrl = (String) params.get("coverUrl");
        String status = (String) params.getOrDefault("status", "0"); // 默认草稿状态
        String locale = (String) params.getOrDefault("locale", "zh-CN"); // 默认中文
        
        // 验证必填字段
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }
        if (siteId == null) {
            throw new IllegalArgumentException("网站ID不能为空");
        }
        
        // 1. 创建主文章
        GbMasterArticle masterArticle = new GbMasterArticle();
        masterArticle.setSiteId(siteId);
        masterArticle.setCategoryId(categoryId);
        masterArticle.setDefaultLocale(locale);
        // status 和 isPublished 字段已从主文章表删除，状态由各语言版本独立管理
        masterArticle.setDelFlag("0");
        masterArticle.setCreateTime(DateUtils.getNowDate());
        
        int masterResult = masterArticleService.insertGbMasterArticle(masterArticle);
        if (masterResult <= 0) {
            throw new RuntimeException("创建主文章失败");
        }
        
        log.info("主文章创建成功，ID: {}", masterArticle.getId());
        
        // 2. 创建语言版本文章
        GbArticle article = new GbArticle();
        article.setMasterArticleId(masterArticle.getId());
        article.setLocale(locale);
        article.setTitle(title);
        article.setContent(content);
        article.setSiteId(siteId);
        article.setCategoryId(categoryId);
        article.setDescription(description);
        article.setKeywords(keywords);
        article.setAuthor(author);
        article.setCoverUrl(coverUrl);
        article.setStatus(status);
        article.setDelFlag("0");
        article.setCreateTime(DateUtils.getNowDate());
        
        int articleResult = articleService.insertGbArticle(article);
        if (articleResult <= 0) {
            throw new RuntimeException("创建语言版本文章失败");
        }
        
        log.info("语言版本文章创建成功，ID: {}", article.getId());
        
        // 3. 处理关联关系（保存到主文章关联表）
        List<Long> gameIds = parseIdList(params.get("gameIds"));
        List<Long> gameBoxIds = parseIdList(params.get("gameBoxIds"));
        
        if (!gameIds.isEmpty()) {
            log.info("保存主文章游戏关联: gameIds={}", gameIds);
            masterArticleGamesService.saveGamesForMasterArticle(
                masterArticle.getId(), gameIds, "tool", "related"
            );
        }
        
        if (!gameBoxIds.isEmpty()) {
            log.info("保存主文章游戏盒子关联: gameBoxIds={}", gameBoxIds);
            masterArticleGameBoxesService.saveGameBoxesForMasterArticle(
                masterArticle.getId(), gameBoxIds, "tool", "related"
            );
        }
        
        log.info("文章保存成功，主文章ID: {}, 语言版本ID: {}, 标题: {}", 
                masterArticle.getId(), article.getId(), article.getTitle());
        
        // 构建返回结果
        Map<String, Object> output = new HashMap<>();
        output.put("masterArticleId", masterArticle.getId());
        output.put("articleId", article.getId());
        output.put("title", article.getTitle());
        output.put("status", article.getStatus());
        output.put("siteId", article.getSiteId());
        output.put("categoryId", article.getCategoryId());
        output.put("success", true);
        output.put("message", "文章保存成功");
        
        return output;
    }
    
    /**
     * 获取Long类型参数
     */
    private Long getLongParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                log.warn("无法解析参数 {} 为Long: {}", key, value);
                return null;
            }
        }
        return null;
    }
    
    /**
     * 解析ID列表
     */
    private List<Long> parseIdList(Object value) {
        List<Long> ids = new ArrayList<>();
        if (value == null) {
            return ids;
        }
        
        if (value instanceof List) {
            for (Object item : (List<?>) value) {
                if (item instanceof Number) {
                    ids.add(((Number) item).longValue());
                } else if (item instanceof String) {
                    try {
                        ids.add(Long.parseLong((String) item));
                    } catch (NumberFormatException e) {
                        log.warn("无法解析ID: {}", item);
                    }
                }
            }
        } else if (value instanceof String) {
            String str = (String) value;
            if (!str.trim().isEmpty()) {
                // 支持逗号分隔的ID字符串
                for (String idStr : str.split(",")) {
                    try {
                        ids.add(Long.parseLong(idStr.trim()));
                    } catch (NumberFormatException e) {
                        log.warn("无法解析ID: {}", idStr);
                    }
                }
            }
        }
        
        return ids;
    }
    
    @Override
    public void validateParams(Map<String, Object> params, String inputDefinitions) throws Exception {
        // 验证必填字段
        if (!params.containsKey("title") || params.get("title") == null || 
            params.get("title").toString().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (!params.containsKey("content") || params.get("content") == null ||
            params.get("content").toString().trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }
        if (!params.containsKey("siteId") || params.get("siteId") == null) {
            throw new IllegalArgumentException("网站ID不能为空");
        }
    }
}
