package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.mapper.GbMasterArticleGameBoxesMapper;
import com.ruoyi.gamebox.mapper.GbMasterArticleGamesMapper;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes;
import com.ruoyi.gamebox.domain.GbMasterArticleGames;
import com.ruoyi.web.controller.gamebox.dto.GbArticleDTO;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IStorageFileService;
import com.ruoyi.gamebox.service.IGbStorageConfigService;
import com.ruoyi.gamebox.domain.GbStorageConfig;


/**
 * 文章管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/article")
public class GbArticleController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(GbArticleController.class);
    
    @Autowired
    private IGbArticleService gbArticleService;
    
    @Autowired
    private GbMasterArticleGameBoxesMapper masterArticleGameBoxesMapper;
    
    @Autowired
    private GbMasterArticleGamesMapper masterArticleGamesMapper;
    
    @Autowired
    private IStorageFileService storageFileService;
    
    @Autowired
    private IGbStorageConfigService storageConfigService;

    /**
     * 查询文章列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbArticle gbArticle)
    {
        startPage();
        List<GbArticle> list = gbArticleService.selectGbArticleList(gbArticle);
        return getDataTable(list);
    }

    /**
     * 导出文章列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:export')")
    @Log(title = "文章管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbArticle gbArticle)
    {
        List<GbArticle> list = gbArticleService.selectGbArticleList(gbArticle);
        ExcelUtil<GbArticle> util = new ExcelUtil<GbArticle>(GbArticle.class);
        util.exportExcel(list, "文章数据");
    }

    /**
     * 获取文章详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        GbArticle article = gbArticleService.selectGbArticleById(id);
        if (article == null) {
            return error("文章不存在");
        }
        
        // 构建 DTO
        GbArticleDTO dto = new GbArticleDTO();
        BeanUtils.copyProperties(article, dto);
        
        // 如果 content 为空（已被清理），尝试从对象存储加载
        if ((dto.getContent() == null || dto.getContent().trim().isEmpty()) 
            && article.getStorageKey() != null 
            && article.getStorageConfigId() != null) {
            try {
                String contentFromStorage = storageFileService.readTextFile(
                    article.getStorageConfigId(),
                    article.getStorageKey()
                );
                dto.setContent(contentFromStorage);
                dto.setContentLoadedFromStorage(true); // 标记从对象存储加载
                logger.info("文章 {} 的 content 从对象存储加载成功", id);
            } catch (Exception e) {
                logger.warn("从对象存储加载文章 {} 内容失败: {}", id, e.getMessage());
                dto.setContentLoadedFromStorage(false);
            }
        } else {
            dto.setContentLoadedFromStorage(false);
        }
        
        // 查询关联的游戏盒子（从主文章关联表查询）
        if (article.getMasterArticleId() != null) {
            List<GbMasterArticleGameBoxes> masterArticleGameBoxes = masterArticleGameBoxesMapper.selectByMasterArticleId(article.getMasterArticleId());
            dto.setGameBoxIds(masterArticleGameBoxes.stream()
                .map(GbMasterArticleGameBoxes::getGameBoxId)
                .collect(Collectors.toList()));
            
            // 查询关联的游戏（从主文章关联表查询）
            List<GbMasterArticleGames> masterArticleGames = masterArticleGamesMapper.selectByMasterArticleId(article.getMasterArticleId());
            dto.setGameIds(masterArticleGames.stream()
                .map(GbMasterArticleGames::getGameId)
                .collect(Collectors.toList()));
        }
        
        return success(dto);
    }
    
    /**
     * 新增文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:add')")
    @Log(title = "文章管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbArticleDTO articleDTO)
    {
        GbArticle article = new GbArticle();
        BeanUtils.copyProperties(articleDTO, article);
        
        // 处理游戏和游戏盒子关联（通过主文章ID）
        int result = gbArticleService.insertGbArticle(article);
        if (result > 0 && article.getMasterArticleId() != null) {
            updateMasterArticleAssociations(article.getMasterArticleId(), articleDTO.getGameIds(), articleDTO.getGameBoxIds());
        }
        
        return toAjax(result);
    }

    /**
     * 修改文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbArticleDTO articleDTO)
    {
        GbArticle article = new GbArticle();
        BeanUtils.copyProperties(articleDTO, article);
        
        // 处理游戏和游戏盒子关联（通过主文章ID）
        int result = gbArticleService.updateGbArticle(article);
        if (result > 0 && article.getMasterArticleId() != null) {
            updateMasterArticleAssociations(article.getMasterArticleId(), articleDTO.getGameIds(), articleDTO.getGameBoxIds());
        }
        
        return toAjax(result);
    }

    /**
     * 取消发布文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:publish')")
    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PostMapping("/unpublish/{id}")
    public AjaxResult unpublish(@PathVariable Long id)
    {
        return gbArticleService.unpublishArticle(id);
    }
    
    /**
     * 批量发布语言版本文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:publish')")
    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PostMapping("/batchPublish")
    public AjaxResult batchPublish(@RequestBody Map<String, Object> request)
    {
        @SuppressWarnings("unchecked")
        List<Object> rawIds = (List<Object>) request.get("articleIds");
        
        if (rawIds == null || rawIds.isEmpty()) {
            return error("请选择要发布的文章");
        }
        
        // 转换为Long列表（JSON反序列化时数字会被解析为Integer）
        List<Long> articleIds = rawIds.stream()
            .map(id -> id instanceof Integer ? ((Integer) id).longValue() : (Long) id)
            .collect(java.util.stream.Collectors.toList());
        
        return gbArticleService.batchPublishArticles(articleIds);
    }
    
    /**
     * 强制批量发布语言版本文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:publish')")
    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PostMapping("/batchPublishForce")
    public AjaxResult batchPublishForce(@RequestBody Map<String, Object> request)
    {
        @SuppressWarnings("unchecked")
        List<Object> rawIds = (List<Object>) request.get("articleIds");
        
        if (rawIds == null || rawIds.isEmpty()) {
            return error("请选择要发布的文章");
        }
        
        // 转换为Long列表（JSON反序列化时数字会被解析为Integer）
        List<Long> articleIds = rawIds.stream()
            .map(id -> id instanceof Integer ? ((Integer) id).longValue() : (Long) id)
            .collect(java.util.stream.Collectors.toList());
        
        return gbArticleService.batchPublishArticlesForce(articleIds);
    }
    
    /**
     * 批量撤销发布语言版本文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:publish')")
    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PostMapping("/batchUnpublish")
    public AjaxResult batchUnpublish(@RequestBody Map<String, Object> request)
    {
        @SuppressWarnings("unchecked")
        List<Long> articleIds = (List<Long>) request.get("articleIds");
        
        if (articleIds == null || articleIds.isEmpty()) {
            return error("请选择要撤销发布的文章");
        }
        
        return gbArticleService.batchUnpublishArticles(articleIds);
    }
    
    /**
     * 批量更新文章状态（只修改数据库状态）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:publish')")
    @Log(title = "文章管理", businessType = BusinessType.UPDATE)
    @PostMapping("/batchUpdateStatus")
    public AjaxResult batchUpdateStatus(@RequestBody Map<String, Object> request)
    {
        @SuppressWarnings("unchecked")
        List<Integer> articleIdsInt = (List<Integer>) request.get("articleIds");
        String status = (String) request.get("status");
        
        if (articleIdsInt == null || articleIdsInt.isEmpty()) {
            return error("请选择要更新的文章");
        }
        
        // 将Integer转换为Long
        List<Long> articleIds = articleIdsInt.stream()
            .map(Integer::longValue)
            .collect(java.util.stream.Collectors.toList());
        
        return gbArticleService.batchUpdateArticleStatus(articleIds, status);
    }
    
    /**
     * 检查文章远程文件同步状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @Log(title = "文章管理", businessType = BusinessType.OTHER)
    @PostMapping("/checkRemoteStatus/{id}")
    public AjaxResult checkRemoteStatus(@PathVariable Long id)
    {
        return gbArticleService.checkRemoteFileStatus(id);
    }
    
    /**
     * 批量检查远程文件状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:query')")
    @Log(title = "文章管理", businessType = BusinessType.OTHER)
    @PostMapping("/batchCheckRemoteStatus")
    public AjaxResult batchCheckRemoteStatus(@RequestBody Map<String, Object> request)
    {
        @SuppressWarnings("unchecked")
        List<Integer> articleIdsInt = (List<Integer>) request.get("articleIds");
        
        if (articleIdsInt == null || articleIdsInt.isEmpty()) {
            return error("请选择要检查的文章");
        }
        
        // 将Integer转换为Long
        List<Long> articleIds = articleIdsInt.stream()
            .map(Integer::longValue)
            .collect(java.util.stream.Collectors.toList());
        
        return gbArticleService.batchCheckRemoteFileStatus(articleIds);
    }

    /**
     * 删除文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:remove')")
    @Log(title = "文章管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbArticleService.deleteGbArticleByIds(ids));
    }
    
    /**
     * 发布单个语言版本文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "发布文章", businessType = BusinessType.UPDATE)
    @PostMapping("/publish/{id}")
    public AjaxResult publish(@PathVariable Long id)
    {
        try {
            return gbArticleService.publishArticleToStorage(id);
        } catch (Exception e) {
            logger.error("发布文章失败: ", e);
            return error("发布文章失败: " + e.getMessage());
        }
    }

    /**
     * 强制发布单个语言版本文章
     */
    @PreAuthorize("@ss.hasPermi('gamebox:article:edit')")
    @Log(title = "强制发布文章", businessType = BusinessType.UPDATE)
    @PostMapping("/publishForce/{id}")
    public AjaxResult publishForce(@PathVariable Long id)
    {
        try {
            return gbArticleService.publishArticleToStorageForce(id);
        } catch (Exception e) {
            logger.error("强制发布文章失败: ", e);
            return error("强制发布文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新主文章的游戏和游戏盒子关联
     */
    private void updateMasterArticleAssociations(Long masterArticleId, List<Long> gameIds, List<Long> gameBoxIds) {
        // 删除现有关联
        masterArticleGamesMapper.deleteByMasterArticleId(masterArticleId);
        masterArticleGameBoxesMapper.deleteByMasterArticleId(masterArticleId);
        
        // 添加新的游戏关联
        if (gameIds != null && !gameIds.isEmpty()) {
            for (Long gameId : gameIds) {
                if (gameId != null) {
                    GbMasterArticleGames relation = new GbMasterArticleGames();
                    relation.setMasterArticleId(masterArticleId);
                    relation.setGameId(gameId);
                    masterArticleGamesMapper.insertGbMasterArticleGames(relation);
                }
            }
        }
        
        // 添加新的游戏盒子关联
        if (gameBoxIds != null && !gameBoxIds.isEmpty()) {
            for (Long gameBoxId : gameBoxIds) {
                if (gameBoxId != null) {
                    GbMasterArticleGameBoxes relation = new GbMasterArticleGameBoxes();
                    relation.setMasterArticleId(masterArticleId);
                    relation.setGameBoxId(gameBoxId);
                    masterArticleGameBoxesMapper.insertGbMasterArticleGameBoxes(relation);
                }
            }
        }
    }
    
}
