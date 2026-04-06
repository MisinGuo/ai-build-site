package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.gamebox.search.service.ISearchService;
import com.ruoyi.gamebox.search.vo.SearchResultVO;
import com.ruoyi.gamebox.search.helper.ElasticsearchSyncHelper;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbGameBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;
import java.util.Map;

/**
 * 搜索Controller
 * 
 * @author ruoyi
 */
@Api(tags = "搜索管理")
@RestController
@RequestMapping("/gamebox/search")
public class SearchController extends BaseController
{
    @Autowired
    private ISearchService searchService;

    @Autowired
    private ElasticsearchSyncHelper syncHelper;

    @Autowired
    private IGbArticleService articleService;

    @Autowired
    private IGbGameService gameService;

    @Autowired
    private IGbGameBoxService gameBoxService;

    /**
     * 全局搜索
     */
    @ApiOperation("全局搜索")
    @GetMapping("/global")
    public AjaxResult globalSearch(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int pageSize)
    {
        SearchResultVO result = searchService.globalSearch(keyword, pageNum, pageSize);
        return AjaxResult.success(result);
    }

    /**
     * 搜索文章
     */
    @ApiOperation("搜索文章")
    @GetMapping("/articles")
    public TableDataInfo searchArticles(
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int pageSize)
    {
        Page<?> page = searchService.searchArticles(keyword, categoryId, pageNum, pageSize);
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(page.getContent());
        dataInfo.setTotal(page.getTotalElements());
        return dataInfo;
    }

    /**
     * 搜索游戏
     */
    @ApiOperation("搜索游戏")
    @GetMapping("/games")
    public TableDataInfo searchGames(
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("游戏分类") @RequestParam(required = false) String category,
            @ApiParam("游戏类型") @RequestParam(required = false) String gameType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int pageSize)
    {
        Page<?> page = searchService.searchGames(keyword, category, gameType, pageNum, pageSize);
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(page.getContent());
        dataInfo.setTotal(page.getTotalElements());
        return dataInfo;
    }

    /**
     * 搜索游戏盒子
     */
    @ApiOperation("搜索游戏盒子")
    @GetMapping("/gameboxes")
    public TableDataInfo searchGameBoxes(
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int pageSize)
    {
        Page<?> page = searchService.searchGameBoxes(keyword, categoryId, pageNum, pageSize);
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(page.getContent());
        dataInfo.setTotal(page.getTotalElements());
        return dataInfo;
    }

    /**
     * 获取搜索建议
     */
    @ApiOperation("获取搜索建议")
    @GetMapping("/suggestions")
    public AjaxResult getSuggestions(
            @ApiParam("输入前缀") @RequestParam String prefix,
            @ApiParam("返回数量") @RequestParam(defaultValue = "10") int limit)
    {
        List<String> suggestions = searchService.getSuggestions(prefix, limit);
        return AjaxResult.success(suggestions);
    }

    /**
     * 获取热门搜索词
     */
    @ApiOperation("获取热门搜索词")
    @GetMapping("/hot-keywords")
    public AjaxResult getHotKeywords(
            @ApiParam("返回数量") @RequestParam(defaultValue = "10") int limit)
    {
        List<String> keywords = searchService.getHotSearchKeywords(limit);
        return AjaxResult.success(keywords);
    }

    /**
     * 获取搜索统计信息
     */
    @ApiOperation("获取搜索统计信息")
    @PreAuthorize("@ss.hasPermi('gamebox:search:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        Map<String, Object> statistics = searchService.getSearchStatistics();
        return AjaxResult.success(statistics);
    }

    /**
     * 重建ES索引（清空并重新同步所有数据）
     */
    @ApiOperation("重建ES索引")
    @PreAuthorize("@ss.hasPermi('gamebox:search:rebuild')")
    @PostMapping("/rebuild-index")
    public AjaxResult rebuildIndex()
    {
        try
        {
            logger.info("开始重建ES索引...");
            
            // 1. 清空所有索引
            syncHelper.clearAllIndexes();
            logger.info("清空ES索引完成");
            
            // 2. 重新同步所有数据
            syncHelper.syncArticles(articleService.selectAllGbArticlesForSync());
            logger.info("同步文章数据完成");
            
            syncHelper.syncGames(gameService.selectAllGbGamesForSync());
            logger.info("同步游戏数据完成");
            
            syncHelper.syncGameBoxes(gameBoxService.selectAllGbGameBoxesForSync());
            logger.info("同步游戏盒子数据完成");
            
            logger.info("ES索引重建完成");
            return AjaxResult.success("ES索引重建成功");
        }
        catch (Exception e)
        {
            logger.error("重建ES索引失败", e);
            return AjaxResult.error("重建ES索引失败: " + e.getMessage());
        }
    }

    /**
     * 重建ES索引 - 公开接口（临时使用，生产环境应移除）
     */
    @ApiOperation("重建ES索引-公开接口")
    @PostMapping("/public/rebuild-index")
    public AjaxResult publicRebuildIndex(@RequestParam(defaultValue = "rebuild-secret-key-2026") String key)
    {
        // 简单的密钥验证
        if (!"rebuild-secret-key-2026".equals(key))
        {
            return AjaxResult.error("密钥错误");
        }
        return rebuildIndex();
    }
}
