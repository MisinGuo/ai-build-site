package com.ruoyi.web.controller.tool;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.mapper.GbArticleMapper;
import com.ruoyi.gamebox.mapper.GbGameBoxMapper;
import com.ruoyi.gamebox.mapper.GbGameMapper;
import com.ruoyi.gamebox.search.helper.ElasticsearchSyncHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据同步工具Controller
 * 用于手动触发MySQL数据同步到Elasticsearch
 * 
 * @author ruoyi
 */
@Api(tags = "数据同步工具")
@RestController
@RequestMapping("/tool/sync")
public class DataSyncController extends BaseController
{
    @Autowired
    private ElasticsearchSyncHelper syncHelper;
    
    @Autowired
    private GbArticleMapper articleMapper;
    
    @Autowired
    private GbGameMapper gameMapper;
    
    @Autowired
    private GbGameBoxMapper gameBoxMapper;

    /**
     * 同步所有文章到ES
     */
    @ApiOperation("同步所有文章到ES")
    @PreAuthorize("@ss.hasPermi('system:sync:articles')")
    @PostMapping("/articles")
    public AjaxResult syncArticles()
    {
        try
        {
            List<GbArticle> articles = articleMapper.selectGbArticleList(new GbArticle());
            int count = 0;
            for (GbArticle article : articles)
            {
                syncHelper.syncArticle(article);
                count++;
            }
            return AjaxResult.success("成功同步 " + count + " 篇文章到ES");
        }
        catch (Exception e)
        {
            logger.error("同步文章失败", e);
            return AjaxResult.error("同步失败：" + e.getMessage());
        }
    }

    /**
     * 同步所有游戏到ES
     */
    @ApiOperation("同步所有游戏到ES")
    @PreAuthorize("@ss.hasPermi('system:sync:games')")
    @PostMapping("/games")
    public AjaxResult syncGames()
    {
        try
        {
            List<GbGame> games = gameMapper.selectGbGameList(new GbGame());
            int count = 0;
            for (GbGame game : games)
            {
                syncHelper.syncGame(game);
                count++;
            }
            return AjaxResult.success("成功同步 " + count + " 个游戏到ES");
        }
        catch (Exception e)
        {
            logger.error("同步游戏失败", e);
            return AjaxResult.error("同步失败：" + e.getMessage());
        }
    }

    /**
     * 同步所有游戏盒子到ES
     */
    @ApiOperation("同步所有游戏盒子到ES")
    @PreAuthorize("@ss.hasPermi('system:sync:gameboxes')")
    @PostMapping("/gameboxes")
    public AjaxResult syncGameBoxes()
    {
        try
        {
            List<GbGameBox> gameBoxes = gameBoxMapper.selectGbGameBoxList(new GbGameBox());
            int count = 0;
            for (GbGameBox gameBox : gameBoxes)
            {
                syncHelper.syncGameBox(gameBox);
                count++;
            }
            return AjaxResult.success("成功同步 " + count + " 个游戏盒子到ES");
        }
        catch (Exception e)
        {
            logger.error("同步游戏盒子失败", e);
            return AjaxResult.error("同步失败：" + e.getMessage());
        }
    }

    /**
     * 同步所有数据到ES
     */
    @ApiOperation("同步所有数据到ES")
    @PreAuthorize("@ss.hasPermi('system:sync:all')")
    @PostMapping("/all")
    public AjaxResult syncAll()
    {
        try
        {
            int articleCount = 0;
            int gameCount = 0;
            int gameBoxCount = 0;
            
            // 同步文章
            List<GbArticle> articles = articleMapper.selectGbArticleList(new GbArticle());
            for (GbArticle article : articles)
            {
                syncHelper.syncArticle(article);
                articleCount++;
            }
            
            // 同步游戏
            List<GbGame> games = gameMapper.selectGbGameList(new GbGame());
            for (GbGame game : games)
            {
                syncHelper.syncGame(game);
                gameCount++;
            }
            
            // 同步游戏盒子
            List<GbGameBox> gameBoxes = gameBoxMapper.selectGbGameBoxList(new GbGameBox());
            for (GbGameBox gameBox : gameBoxes)
            {
                syncHelper.syncGameBox(gameBox);
                gameBoxCount++;
            }
            
            String message = String.format("同步完成！文章：%d，游戏：%d，游戏盒子：%d", 
                    articleCount, gameCount, gameBoxCount);
            return AjaxResult.success(message);
        }
        catch (Exception e)
        {
            logger.error("批量同步失败", e);
            return AjaxResult.error("同步失败：" + e.getMessage());
        }
    }
}
