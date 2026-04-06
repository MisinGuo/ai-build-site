package com.ruoyi.gamebox.service.impl;

import com.ruoyi.gamebox.domain.*;
import com.ruoyi.gamebox.mapper.GbMasterArticleGameBoxesMapper;
import com.ruoyi.gamebox.mapper.GbMasterArticleGamesMapper;
import com.ruoyi.gamebox.mapper.GbArticleMapper;
import com.ruoyi.gamebox.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板变量Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class TemplateVariableServiceImpl implements ITemplateVariableService
{
    @Autowired
    private IGbSiteService siteService;
    
    @Autowired
    private IGbGameBoxService gameBoxService;
    
    @Autowired
    private IGbGameService gameService;
    
    @Autowired
    private GbMasterArticleGameBoxesMapper masterArticleGameBoxesMapper;
    
    @Autowired
    private GbMasterArticleGamesMapper masterArticleGamesMapper;
    
    @Autowired
    private GbArticleMapper articleMapper;
    
    // 匹配 {{XXX.yyy}} 或 {{XXX-n.yyy}} 格式的变量
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([\\w.-]+)\\}\\}");
    
    @Override
    public String replaceVariables(String content, Long siteId, Long articleId)
    {
        if (content == null || content.isEmpty())
        {
            return content;
        }
        
        // 构建变量上下文
        Map<String, Object> context = buildContext(siteId, articleId);
        
        // 替换所有变量
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find())
        {
            String variablePath = matcher.group(1); // 例如：siteConfig.jumpDomain
            String value = resolveVariable(variablePath, context);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    @Override
    public String replaceVariables(String content, Long siteId, List<Long> gameBoxIds, List<Long> gameIds)
    {
        if (content == null || content.isEmpty())
        {
            return content;
        }
        
        // 构建变量上下文（使用 ID 列表）
        Map<String, Object> context = buildContextFromIds(siteId, gameBoxIds, gameIds);
        
        // 替换所有变量
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find())
        {
            String variablePath = matcher.group(1);
            String value = resolveVariable(variablePath, context);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * 构建变量上下文（从关联表查询）
     */
    private Map<String, Object> buildContext(Long siteId, Long articleId)
    {
        Map<String, Object> context = new HashMap<>();
        
        // 加载网站配置（一篇文章只能对应一个网站）
        if (siteId != null)
        {
            GbSite site = siteService.selectGbSiteById(siteId);
            if (site != null)
            {
                Map<String, String> siteData = new HashMap<>();
                siteData.put("name", site.getName());
                siteData.put("code", site.getCode());
                siteData.put("domain", site.getDomain());
                siteData.put("jumpDomain", site.getDomain()); // 跳转域名
                siteData.put("defaultLocale", site.getDefaultLocale());
                context.put("SITE", siteData);
            }
        }
        
        // 加载游戏盒子数据（从主文章关联表查询，支持多个）
        if (articleId != null)
        {
            // 先查询article获取masterArticleId
            GbArticle article = articleMapper.selectGbArticleById(articleId);
            if (article != null && article.getMasterArticleId() != null)
            {
                Long masterArticleId = article.getMasterArticleId();
                
                // 查询主文章关联的游戏盒子（按 display_order 排序）
                List<GbMasterArticleGameBoxes> masterArticleGameBoxes = masterArticleGameBoxesMapper.selectByMasterArticleId(masterArticleId);
                if (!masterArticleGameBoxes.isEmpty())
                {
                    int index = 1;
                    for (GbMasterArticleGameBoxes masterArticleGameBox : masterArticleGameBoxes)
                    {
                        GbGameBox gameBox = gameBoxService.selectGbGameBoxById(masterArticleGameBox.getGameBoxId());
                        if (gameBox != null)
                        {
                            Map<String, String> gameBoxData = new HashMap<>();
                            gameBoxData.put("name", gameBox.getName());
                            gameBoxData.put("logoUrl", gameBox.getLogoUrl());
                            gameBoxData.put("downloadUrl", gameBox.getDownloadUrl());
                            gameBoxData.put("description", gameBox.getDescription());
                            gameBoxData.put("discountRate", String.valueOf(gameBox.getDiscountRate()));
                            
                            // 支持 {{GAMEBOX-1.name}}, {{GAMEBOX-2.name}} 等
                            context.put("GAMEBOX-" + index, gameBoxData);
                            
                            index++;
                        }
                    }
                }
                
                // 查询主文章关联的游戏（按 display_order 排序）
                List<GbMasterArticleGames> masterArticleGames = masterArticleGamesMapper.selectByMasterArticleId(masterArticleId);
                if (!masterArticleGames.isEmpty())
                {
                    int index = 1;
                    for (GbMasterArticleGames masterArticleGame : masterArticleGames)
                    {
                        GbGame game = gameService.selectGbGameById(masterArticleGame.getGameId());
                        if (game != null)
                        {
                            Map<String, String> gameData = new HashMap<>();
                            gameData.put("name", game.getName());
                            gameData.put("shortName", game.getShortName());
                            gameData.put("subtitle", game.getSubtitle());
                            gameData.put("coverUrl", game.getCoverUrl());
                            gameData.put("iconUrl", game.getIconUrl());
                            // gameData.put("downloadUrl", game.getDownloadUrl());
                            // gameData.put("androidUrl", game.getAndroidUrl());
                            // gameData.put("iosUrl", game.getIosUrl());
                            gameData.put("description", game.getDescription());
                            gameData.put("tags", game.getTags());
                            gameData.put("rating", String.valueOf(game.getRating()));
                            
                            // 支持 {{GAME-1.name}}, {{GAME-2.name}} 等
                            context.put("GAME-" + index, gameData);
                            
                            index++;
                        }
                    }
                }
            }
        }
        
        return context;
    }
    
    /**
     * 构建变量上下文（直接使用 ID 列表）
     * 用于发布时，直接从前端传来的 ID 列表构建上下文
     */
    private Map<String, Object> buildContextFromIds(Long siteId, List<Long> gameBoxIds, List<Long> gameIds)
    {
        Map<String, Object> context = new HashMap<>();
        
        // 加载网站配置
        if (siteId != null)
        {
            GbSite site = siteService.selectGbSiteById(siteId);
            if (site != null)
            {
                Map<String, String> siteData = new HashMap<>();
                siteData.put("name", site.getName());
                siteData.put("code", site.getCode());
                siteData.put("domain", site.getDomain());
                siteData.put("jumpDomain", site.getDomain());
                siteData.put("defaultLocale", site.getDefaultLocale());
                context.put("SITE", siteData);
            }
        }
        
        // 加载游戏盒子数据
        if (gameBoxIds != null && !gameBoxIds.isEmpty())
        {
            int index = 1;
            for (Long gameBoxId : gameBoxIds)
            {
                GbGameBox gameBox = gameBoxService.selectGbGameBoxById(gameBoxId);
                if (gameBox != null)
                {
                    Map<String, String> gameBoxData = new HashMap<>();
                    gameBoxData.put("name", gameBox.getName());
                    gameBoxData.put("logoUrl", gameBox.getLogoUrl());
                    gameBoxData.put("downloadUrl", gameBox.getDownloadUrl());
                    gameBoxData.put("description", gameBox.getDescription());
                    gameBoxData.put("discountRate", String.valueOf(gameBox.getDiscountRate()));
                    
                    context.put("GAMEBOX-" + index, gameBoxData);
                    
                    index++;
                }
            }
        }
        
        // 加载游戏数据
        if (gameIds != null && !gameIds.isEmpty())
        {
            int index = 1;
            for (Long gameId : gameIds)
            {
                GbGame game = gameService.selectGbGameById(gameId);
                if (game != null)
                {
                    Map<String, String> gameData = new HashMap<>();
                    gameData.put("name", game.getName());
                    gameData.put("shortName", game.getShortName());
                    gameData.put("subtitle", game.getSubtitle());
                    gameData.put("coverUrl", game.getCoverUrl());
                    gameData.put("iconUrl", game.getIconUrl());
                    // gameData.put("downloadUrl", game.getDownloadUrl());
                    // gameData.put("androidUrl", game.getAndroidUrl());
                    // gameData.put("iosUrl", game.getIosUrl());
                    gameData.put("description", game.getDescription());
                    gameData.put("tags", game.getTags());
                    gameData.put("rating", String.valueOf(game.getRating()));
                    
                    context.put("GAME-" + index, gameData);
                    
                    index++;
                }
            }
        }
        
        return context;
    }
    
    /**
     * 解析变量路径
     */
    private String resolveVariable(String variablePath, Map<String, Object> context)
    {
        String[] parts = variablePath.split("\\.");
        if (parts.length == 0)
        {
            return "{{" + variablePath + "}}"; // 保持原样
        }
        
        Object current = context.get(parts[0]);
        
        // 遍历路径
        for (int i = 1; i < parts.length; i++)
        {
            if (current instanceof Map)
            {
                current = ((Map<?, ?>) current).get(parts[i]);
            }
            else
            {
                return "{{" + variablePath + "}}"; // 路径不存在，保持原样
            }
        }
        
        return current != null ? String.valueOf(current) : "";
    }
}
