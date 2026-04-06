package com.ruoyi.gamebox.service.impl;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.domain.GbBoxCategoryRelation;
import com.ruoyi.gamebox.domain.GbCategory;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.dto.CategoryValidationResult;
import com.ruoyi.gamebox.mapper.GbBoxCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.service.IGbCategoryService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbGameCategoryRelationService;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.gamebox.mapper.GbGameBoxMapper;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.service.IGbGameBoxService;
import com.ruoyi.gamebox.search.helper.ElasticsearchSyncHelper;

/**
 * 游戏盒子Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbGameBoxServiceImpl implements IGbGameBoxService
{
    private static final Logger logger = LoggerFactory.getLogger(GbGameBoxServiceImpl.class);
    
    @Autowired
    private GbGameBoxMapper gbGameBoxMapper;

    @Autowired
    private GbBoxCategoryRelationMapper boxCategoryRelationMapper;
    
    @Autowired
    private IGbCategoryService categoryService;
    
    @Autowired
    private IGbGameService gameService;
    
    @Autowired
    private IGbGameCategoryRelationService gameCategoryRelationService;

    @Autowired(required = false)
    private ElasticsearchSyncHelper elasticsearchSyncHelper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    private void injectPersonalSiteId(BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
    }

    /**
     * 查询游戏盒子
     * 
     * @param id 游戏盒子主键
     * @return 游戏盒子
     */
    @Override
    public GbGameBox selectGbGameBoxById(Long id)
    {
        return gbGameBoxMapper.selectGbGameBoxById(id);
    }

    /**
     * 查询游戏盒子列表
     * 
     * @param gbGameBox 游戏盒子
     * @return 游戏盒子
     */
    @Override
    public List<GbGameBox> selectGbGameBoxList(GbGameBox gbGameBox)
    {
        relatedModeSiteValidator.validate(gbGameBox.getQueryMode(), gbGameBox.getSiteId());
        injectPersonalSiteId(gbGameBox);
        List<GbGameBox> boxes = gbGameBoxMapper.selectGbGameBoxList(gbGameBox);
        // 为每个盒子加载分类关联
        for (GbGameBox box : boxes) {
            List<GbBoxCategoryRelation> categories = boxCategoryRelationMapper.selectCategoriesByBoxId(box.getId());
            box.setCategories(categories);
        }
        return boxes;
    }

    @Override
    public List<GbGameBox> selectAllGbGameBoxesForSync()
    {
        return gbGameBoxMapper.selectAllGbGameBoxesForSync();
    }

    /**
     * 新增游戏盒子
     * 
     * @param gbGameBox 游戏盒子
     * @return 结果
     */
    @Override
    public int insertGbGameBox(GbGameBox gbGameBox)
    {
        gbGameBox.setCreateTime(DateUtils.getNowDate());
        int result = gbGameBoxMapper.insertGbGameBox(gbGameBox);
        
        // 同步到Elasticsearch
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.syncGameBox(gbGameBox);
        }
        
        return result;
    }

    /**
     * 修改游戏盒子
     * 
     * @param gbGameBox 游戏盒子
     * @return 结果
     */
    @Override
    public int updateGbGameBox(GbGameBox gbGameBox)
    {
        // 检查siteId是否改变以及是否需要同步
        GbGameBox oldBox = gbGameBoxMapper.selectGbGameBoxById(gbGameBox.getId());
        boolean siteIdChanged = oldBox != null && 
                                gbGameBox.getSiteId() != null && 
                                !gbGameBox.getSiteId().equals(oldBox.getSiteId());
        boolean needSyncGameSiteId = siteIdChanged && 
                                     gbGameBox.getSyncGameSiteId() != null && 
                                     gbGameBox.getSyncGameSiteId();
        
        gbGameBox.setUpdateTime(DateUtils.getNowDate());
        int result = gbGameBoxMapper.updateGbGameBox(gbGameBox);
        
        // 如果需要同步更新游戏的siteId
        if (result > 0 && needSyncGameSiteId)
        {
            int updatedCount = cascadeUpdateGameSiteId(gbGameBox.getId(), gbGameBox.getSiteId());
            logger.info("盒子{}的网站从{}更改为{}，同步更新了{}个游戏", 
                       gbGameBox.getId(), oldBox.getSiteId(), gbGameBox.getSiteId(), updatedCount);
        }
        
        // 同步到Elasticsearch
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.syncGameBox(gbGameBox);
        }
        
        return result;
    }
    
    /**
     * 级联更新盒子下游戏的siteId
     * 更新该盒子下所有游戏的所属网站
     * 
     * @param boxId 盒子ID
     * @param newSiteId 新的网站ID
     * @return 更新的游戏数量
     */
    private int cascadeUpdateGameSiteId(Long boxId, Long newSiteId)
    {
        try {
            // 调用mapper方法来更新游戏的siteId
            // 但只更新那些仅属于该盒子的游戏
            int updatedCount = gbGameBoxMapper.updateGamesSiteIdByBoxId(boxId, newSiteId);
            logger.info("级联更新游戏siteId成功: boxId={}, newSiteId={}, 更新了{}个游戏", boxId, newSiteId, updatedCount);
            return updatedCount;
        } catch (Exception e) {
            // 记录日志但不影响主流程
            logger.error("级联更新游戏siteId失败: boxId={}, newSiteId={}", boxId, newSiteId, e);
            return 0;
        }
    }

    /**
     * 批量删除游戏盒子
     * 
     * @param ids 需要删除的游戏盒子主键
     * @return 结果
     */
    @Override
    public int deleteGbGameBoxByIds(Long[] ids)
    {
        int result = gbGameBoxMapper.deleteGbGameBoxByIds(ids);
        
        // 从Elasticsearch删除
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.deleteGameBoxes(ids);
        }
        
        return result;
    }

    /**
     * 删除游戏盒子信息
     * 
     * @param id 游戏盒子主键
     * @return 结果
     */
    @Override
    public int deleteGbGameBoxById(Long id)
    {
        int result = gbGameBoxMapper.deleteGbGameBoxById(id);
        
        // 从Elasticsearch删除
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.deleteGameBox(id);
        }
        
        return result;
    }

    /**
     * 通过分类ID查询盒子列表（支持siteId过滤和排序）
     * 
     * @param categoryId 分类ID
     * @param siteId 站点ID
     * @param orderBy 排序方式：hot-热门, new-最新, rating-评分
     * @return 盒子集合
     */
    @Override
    public List<GbGameBox> selectBoxesByCategoryId(Long categoryId, Long siteId, String orderBy)
    {
        return gbGameBoxMapper.selectBoxesByCategoryId(categoryId, siteId, orderBy);
    }

    /**
     * 通过站点ID查询盒子列表（包含siteId=0的全局数据）
     * 
     * @param siteId 站点ID
     * @param categoryId 分类ID（可选）
     * @return 盒子集合
     */
    @Override
    public List<GbGameBox> selectBoxesBySiteId(Long siteId, Long categoryId)
    {
        // 构造查询对象以支持注入 personalSiteId（用于匿名公开接口）并使用关联模式的完整逻辑
        GbGameBox query = new GbGameBox();
        query.setSiteId(siteId);
        query.setCategoryId(categoryId);
        query.setQueryMode("related");
        // 注入个人默认站点ID到 params（如果存在）
        injectPersonalSiteId(query);

        List<GbGameBox> boxes = gbGameBoxMapper.selectGbGameBoxList(query);
        // 为每个盒子加载分类关联
        for (GbGameBox box : boxes) {
            List<GbBoxCategoryRelation> categories = boxCategoryRelationMapper.selectCategoriesByBoxId(box.getId());
            box.setCategories(categories);
        }
        return boxes;
    }

    /**
     * 通过游戏ID查询关联的盒子列表
     * 
     * @param gameId 游戏ID
     * @param siteId 站点ID
     * @return 盒子集合
     */
    @Override
    public List<GbGameBox> selectBoxesByGameId(Long gameId, Long siteId)
    {
        return gbGameBoxMapper.selectBoxesByGameId(gameId, siteId);
    }

    /**
     * 通过文章ID查询关联的盒子列表
     * 
     * @param articleId 文章ID
     * @param siteId 站点ID
     * @return 盒子集合
     */
    @Override
    public List<GbGameBox> selectBoxesByArticleId(Long articleId, Long siteId)
    {
        return gbGameBoxMapper.selectBoxesByArticleId(articleId, siteId);
    }
    
    /**
     * 验证盒子的siteId变更后，游戏的分类是否仍然有效
     * 只要分类不属于目标网站且不是全局分类，就会"找不到"
     * 
     * @param boxId 盒子ID
     * @param newSiteId 新的网站ID
     * @return 验证结果
     */
    @Override
    public CategoryValidationResult validateCategoriesForSiteChange(Long boxId, Long newSiteId)
    {
        CategoryValidationResult result = new CategoryValidationResult();
        Set<String> invalidCategoryNames = new HashSet<>();
        
        try {
            // 1. 查询盒子下的所有游戏
            List<GbGame> games = gameService.selectGamesByBoxId(boxId, null);
            
            if (games == null || games.isEmpty()) {
                return result; // 没有游戏，直接通过
            }
            
            // 2. 检查每个游戏的分类（主分类 + 关联分类）
            int invalidGameCount = 0;
            Set<String> processedGames = new HashSet<>();
            
            for (GbGame game : games) {
                boolean gameHasInvalidCategory = false;
                
                // 检查游戏所有关联分类（包括主分类）
                List<Long> gameCategoryIds = gameCategoryRelationService.selectCategoryIdsByGameId(game.getId());
                for (Long categoryId : gameCategoryIds) {
                    GbCategory category = categoryService.selectGbCategoryById(categoryId);
                    if (category != null) {
                        // 分类既不属于目标网站，也不是全局分类，就会"找不到"
                        if (!category.getSiteId().equals(newSiteId) && !category.getSiteId().equals(0L)) {
                            invalidCategoryNames.add(category.getName());
                            gameHasInvalidCategory = true;
                        }
                    }
                }
                
                // 统计有问题的游戏数量
                if (gameHasInvalidCategory && !processedGames.contains(game.getId().toString())) {
                    invalidGameCount++;
                    processedGames.add(game.getId().toString());
                }
            }
            
            // 3. 如果有无效分类，添加错误信息
            if (!invalidCategoryNames.isEmpty()) {
                String categoryNamesStr = String.join("、", invalidCategoryNames);
                result.setInvalidGameCount(invalidGameCount);
                result.setInvalidGameCategoryNames(new java.util.ArrayList<>(invalidCategoryNames));
                result.addError("该盒子下有 " + invalidGameCount + " 个游戏使用了以下分类：【" + categoryNamesStr + "】");
                result.addError("这些分类不属于目标网站，修改后会找不到对应分类");
                result.addError("请先将这些分类的所属网站修改为目标网站，或修改为全局分类");
            }
            
        } catch (Exception e) {
            logger.error("验证分类失败: boxId={}, newSiteId={}", boxId, newSiteId, e);
            result.addError("验证失败: " + e.getMessage());
        }
        
        return result;
    }
}
