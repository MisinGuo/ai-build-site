package com.ruoyi.gamebox.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbBoxCategoryRelation;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.domain.GbCategory;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.domain.GbSiteAiPlatformRelation;
import com.ruoyi.gamebox.domain.GbSiteArticleRelation;
import com.ruoyi.gamebox.domain.GbSiteBoxRelation;
import com.ruoyi.gamebox.domain.GbSiteCategoryRelation;
import com.ruoyi.gamebox.domain.GbSiteGameRelation;
import com.ruoyi.gamebox.domain.GbSiteRelation;
import com.ruoyi.gamebox.domain.GbSiteStorageConfigRelation;
import com.ruoyi.gamebox.domain.GbSiteTemplateRelation;
import com.ruoyi.gamebox.domain.GbSiteTitleBatchRelation;
import com.ruoyi.gamebox.domain.GbSiteAtomicToolRelation;
import com.ruoyi.gamebox.domain.GbStorageConfig;
import com.ruoyi.gamebox.mapper.GbArticleMapper;
import com.ruoyi.gamebox.mapper.GbBoxCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbBoxGameRelationMapper;
import com.ruoyi.gamebox.mapper.GbCategoryMapper;
import com.ruoyi.gamebox.mapper.GbGameBoxMapper;
import com.ruoyi.gamebox.mapper.GbGameCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbGameMapper;
import com.ruoyi.gamebox.mapper.GbSiteAiPlatformRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.mapper.GbSiteArticleRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteBoxRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteGameRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteGenerationRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteTitleBatchRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteStorageConfigRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteTemplateRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteAtomicToolRelationMapper;
import com.ruoyi.gamebox.mapper.GbStorageConfigMapper;
import com.ruoyi.gamebox.service.AsyncRelationService;
import com.ruoyi.gamebox.service.ISiteContentRelationService;

/**
 * 网站内容关联管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
@Service
public class SiteContentRelationServiceImpl implements ISiteContentRelationService 
{
    private static final Logger logger = LoggerFactory.getLogger(SiteContentRelationServiceImpl.class);
    
    @Autowired
    private GbSiteCategoryRelationMapper siteCategoryRelationMapper;
    
    @Autowired
    private GbSiteBoxRelationMapper siteBoxRelationMapper;
    
    @Autowired
    private GbSiteGameRelationMapper siteGameRelationMapper;
    
    @Autowired
    private GbSiteArticleRelationMapper siteArticleRelationMapper;
    
    @Autowired
    private GbSiteStorageConfigRelationMapper siteStorageConfigRelationMapper;
    
    @Autowired
    private GbSiteGenerationRelationMapper siteGenerationRelationMapper;
    
    @Autowired
    private GbSiteTitleBatchRelationMapper siteTitleBatchRelationMapper;
    
    @Autowired
    private GbSiteAiPlatformRelationMapper siteAiPlatformRelationMapper;
    
    @Autowired
    private GbSiteTemplateRelationMapper siteTemplateRelationMapper;
    
    @Autowired
    private GbSiteAtomicToolRelationMapper siteAtomicToolRelationMapper;
    
    @Autowired
    private GbCategoryMapper categoryMapper;
    
    @Autowired
    private GbGameBoxMapper gameBoxMapper;
    
    @Autowired
    private GbGameMapper gameMapper;
    
    @Autowired
    private GbBoxCategoryRelationMapper boxCategoryRelationMapper;
    
    @Autowired
    private GbGameCategoryRelationMapper gameCategoryRelationMapper;
    
    @Autowired
    private GbBoxGameRelationMapper boxGameRelationMapper;
    
    @Autowired
    private GbArticleMapper articleMapper;
    
    @Autowired
    private GbSiteMapper siteMapper;
    
    @Autowired
    private AsyncRelationService asyncRelationService;

    /**
     * 判断站点是否为个人站点（即原来的"默认配置"）
     */
    private boolean isPersonalSite(Long siteId) {
        if (siteId == null) return false;
        GbSite site = siteMapper.selectGbSiteById(siteId);
        return site != null && Integer.valueOf(1).equals(site.getIsPersonal());
    }

    // ==================== 分类关联 ====================
    
    /**
     * 递归获取所有父级分类ID
     * @param categoryId 分类ID
     * @return 父级分类ID列表（从根到直接父级）
     */
    private List<Long> getAllParentCategoryIds(Long categoryId) {
        List<Long> parentIds = new ArrayList<>();
        GbCategory category = categoryMapper.selectGbCategoryById(categoryId);
        
        while (category != null && category.getParentId() != null && category.getParentId() != 0) {
            parentIds.add(0, category.getParentId()); // 添加到列表开头，保证从根到叶的顺序
            category = categoryMapper.selectGbCategoryById(category.getParentId());
        }
        
        return parentIds;
    }
    
    @Override
    @Transactional
    public int addCategoryToSite(Long siteId, Long categoryId, boolean isEditable) {
        // 1. 先关联所有父级分类（不可见）
        List<Long> parentIds = getAllParentCategoryIds(categoryId);
        String username = SecurityUtils.getUsername();
        Date now = new Date();
        int count = 0;
        
        for (Long parentId : parentIds) {
            // 检查父级关联是否已存在（只检查 include 类型）
            if (siteCategoryRelationMapper.checkRelationExists(siteId, parentId) == 0) {
                GbSiteCategoryRelation parentRelation = new GbSiteCategoryRelation();
                parentRelation.setSiteId(siteId);
                parentRelation.setCategoryId(parentId);
                parentRelation.setRelationType("include");
                parentRelation.setIsVisible("0"); // 父级分类默认不可见
                parentRelation.setIsEditable("0"); // 自动添加的父级不可编辑
                parentRelation.setCreateBy(username);
                parentRelation.setCreateTime(now);
                count += siteCategoryRelationMapper.insertGbSiteCategoryRelation(parentRelation);
                logger.info("自动关联父级分类{}到网站{}", parentId, siteId);
            }
        }
        
        // 2. 检查目标分类的 include 关联是否已存在
        if (siteCategoryRelationMapper.checkRelationExists(siteId, categoryId) > 0) {
            logger.info("分类{}已关联到网站{}", categoryId, siteId);
            return count;
        }
        
        // 3. 添加目标分类关联
        GbSiteCategoryRelation relation = new GbSiteCategoryRelation();
        relation.setSiteId(siteId);
        relation.setCategoryId(categoryId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        relation.setIsEditable(isEditable ? "1" : "0");
        relation.setCreateBy(username);
        relation.setCreateTime(now);
        
        return count + siteCategoryRelationMapper.insertGbSiteCategoryRelation(relation);
    }
    
    @Override
    @Transactional
    public int batchAddCategoriesToSite(Long siteId, List<Long> categoryIds, boolean isEditable) {
        List<GbSiteCategoryRelation> relations = new ArrayList<>();
        Set<Long> processedCategoryIds = new HashSet<>(); // 避免重复处理
        String username = SecurityUtils.getUsername();
        Date now = new Date();
        
        for (Long categoryId : categoryIds) {
            // 1. 收集所有父级分类ID
            List<Long> parentIds = getAllParentCategoryIds(categoryId);
            
            // 2. 先添加父级分类关联
            for (Long parentId : parentIds) {
                if (!processedCategoryIds.contains(parentId) && 
                    siteCategoryRelationMapper.checkRelationExists(siteId, parentId) == 0) {
                    GbSiteCategoryRelation parentRelation = new GbSiteCategoryRelation();
                    parentRelation.setSiteId(siteId);
                    parentRelation.setCategoryId(parentId);
                    parentRelation.setRelationType("include");
                    parentRelation.setIsVisible("0"); // 父级分类默认不可见
                    parentRelation.setIsEditable("0"); // 自动添加的父级不可编辑
                    parentRelation.setCreateBy(username);
                    parentRelation.setCreateTime(now);
                    relations.add(parentRelation);
                    processedCategoryIds.add(parentId);
                }
            }
            
            // 3. 添加目标分类关联
            if (!processedCategoryIds.contains(categoryId) &&
                siteCategoryRelationMapper.checkRelationExists(siteId, categoryId) == 0) {
                GbSiteCategoryRelation relation = new GbSiteCategoryRelation();
                relation.setSiteId(siteId);
                relation.setCategoryId(categoryId);
                relation.setRelationType("include");
                relation.setIsVisible("1");
                relation.setIsEditable(isEditable ? "1" : "0");
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
                processedCategoryIds.add(categoryId);
            }
        }
        
        if (relations.isEmpty()) {
            return 0;
        }
        
        int count = siteCategoryRelationMapper.batchInsertGbSiteCategoryRelation(relations);
        logger.info("批量关联分类到网站{}，共添加{}条关联（包含父级）", siteId, count);
        return count;
    }
    
    @Override
    public int removeCategoryFromSite(Long siteId, Long categoryId, String relationType) {
        return siteCategoryRelationMapper.deleteGbSiteCategoryRelation(siteId, categoryId, relationType);
    }
    
    @Override
    public List<GbCategory> getAvailableCategoriesBySite(Long siteId, String categoryType) {
        // 通过关联表查询网站可用的分类
        GbCategory query = new GbCategory();
        query.setSiteId(siteId);
        query.setCategoryType(categoryType);
        return categoryMapper.selectCategoriesBySiteId(query);
    }
    
    // ==================== 游戏盒子关联 ====================
    
    @Override
    @Transactional
    public int addBoxToSite(Long siteId, Long boxId, boolean includeGames) {
        // 1. 检查相同类型的盒子关联是否已存在
        if (siteBoxRelationMapper.checkRelationExistsByType(siteId, boxId, "include") > 0) {
            logger.info("盒子{}已关联到网站{}（include类型）", boxId, siteId);
            return 0;
        }
        
        // 2. 确保盒子的分类在目标网站可用
        ensureBoxCategoriesAvailable(boxId, siteId);
        
        // 3. 创建盒子关联
        GbSiteBoxRelation relation = new GbSiteBoxRelation();
        relation.setSiteId(siteId);
        relation.setBoxId(boxId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        int result = siteBoxRelationMapper.insertGbSiteBoxRelation(relation);
        
        // 4. 如果需要，异步批量添加盒子中的游戏
        if (includeGames) {
            String username = SecurityUtils.getUsername();
            // 通过独立的异步服务处理游戏关联，避免超时
            asyncRelationService.asyncAddBoxGamesToSite(siteId, boxId, username);
            logger.info("已启动异步任务：将盒子{}的游戏添加到网站{}", boxId, siteId);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public int batchAddBoxesToSite(Long siteId, List<Long> boxIds, boolean includeGames) {
        int count = 0;
        for (Long boxId : boxIds) {
            count += addBoxToSite(siteId, boxId, includeGames);
        }
        return count;
    }
    
    @Override
    @Transactional
    public int removeBoxFromSite(Long siteId, Long boxId, boolean removeGames) {
        // 1. 删除盒子关联
        int result = siteBoxRelationMapper.deleteGbSiteBoxRelation(siteId, boxId, null);
        
        // 2. 如果需要，同时移除游戏
        if (removeGames) {
            List<GbBoxGameRelation> gameRelations = boxGameRelationMapper.selectGamesByBoxId(boxId);
            if (!gameRelations.isEmpty()) {
                // 收集所有游戏ID
                List<Long> gameIds = gameRelations.stream()
                    .map(GbBoxGameRelation::getGameId)
                    .collect(Collectors.toList());
                
                // 批量删除游戏关联
                siteGameRelationMapper.batchDeleteGbSiteGameRelations(siteId, gameIds);
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public int copyBoxToSite(Long boxId, Long targetSiteId) {
        // 1. 添加盒子到目标网站（包括游戏）
        return addBoxToSite(targetSiteId, boxId, true);
    }
    
    // ==================== 游戏关联 ====================
    
    @Override
    @Transactional
    public int addGameToSite(Long siteId, Long gameId) {
        // 1. 检查游戏关联是否已存在
        if (siteGameRelationMapper.checkRelationExists(siteId, gameId) > 0) {
            logger.info("游戏{}已关联到网站{}", gameId, siteId);
            return 0;
        }
        
        // 2. 确保游戏的分类在目标网站可用
        ensureGameCategoriesAvailable(gameId, siteId);
        
        // 3. 创建游戏关联
        GbSiteGameRelation relation = new GbSiteGameRelation();
        relation.setSiteId(siteId);
        relation.setGameId(gameId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteGameRelationMapper.insertGbSiteGameRelation(relation);
    }
    
    @Override
    @Transactional
    public int batchAddGamesToSite(Long siteId, List<Long> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return 0;
        }
        
        logger.info("[批量游戏关联] 开始处理，游戏数量: {}, 目标网站: {}", gameIds.size(), siteId);
        
        // 1. 先批量收集所有游戏的分类ID（优化性能）
        Set<Long> allCategoryIds = new HashSet<>();
        List<Long> validGameIds = new ArrayList<>();
        
        for (Long gameId : gameIds) {
            // 检查关联是否已存在
            if (siteGameRelationMapper.checkRelationExists(siteId, gameId) > 0) {
                logger.debug("游戏{}已关联到网站{}，跳过", gameId, siteId);
                continue;
            }
            
            // 获取游戏信息并收集分类
            GbGame game = gameMapper.selectGbGameById(gameId);
            if (game != null && "0".equals(game.getDelFlag())) {
                validGameIds.add(gameId);
                
                // 从关联表收集所有分类（包括主分类）
                List<GbGameCategoryRelation> categoryRelations = gameCategoryRelationMapper.selectCategoriesByGameId(gameId);
                for (GbGameCategoryRelation rel : categoryRelations) {
                    allCategoryIds.add(rel.getCategoryId());
                    logger.debug("收集游戏{}的多分类: {}", gameId, rel.getCategoryId());
                }
            } else {
                if (game == null) {
                    logger.warn("游戏{}不存在，跳过关联", gameId);
                } else {
                    logger.warn("游戏{}已被删除，跳过关联", gameId);
                }
            }
        }
        
        if (validGameIds.isEmpty()) {
            logger.info("[批量游戏关联] 没有有效的游戏需要关联");
            return 0;
        }
        
        logger.info("[批量游戏关联] 有效游戏数: {}, 收集到的分类数: {}", validGameIds.size(), allCategoryIds.size());
        
        // 2. 批量关联所有分类到目标网站（包括父分类）
        if (!allCategoryIds.isEmpty()) {
            logger.info("[批量游戏关联] 开始批量关联{}个分类", allCategoryIds.size());
            batchEnsureCategoriesAvailable(new ArrayList<>(allCategoryIds), siteId);
            logger.info("[批量游戏关联] 分类关联完成");
        }
        
        // 3. 批量插入游戏关联
        String username = SecurityUtils.getUsername();
        Date now = new Date();
        List<GbSiteGameRelation> gamesToAdd = new ArrayList<>();
        
        for (Long gameId : validGameIds) {
            GbSiteGameRelation relation = new GbSiteGameRelation();
            relation.setSiteId(siteId);
            relation.setGameId(gameId);
            relation.setRelationType("include");
            relation.setIsVisible("1");
            relation.setCreateBy(username);
            relation.setCreateTime(now);
            gamesToAdd.add(relation);
        }
        
        // 批量插入游戏关联
        if (!gamesToAdd.isEmpty()) {
            logger.info("[批量游戏关联] 开始批量插入{}个游戏关联", gamesToAdd.size());
            int result = siteGameRelationMapper.batchInsertGbSiteGameRelation(gamesToAdd);
            logger.info("[批量游戏关联] ✓ 成功添加{}个游戏到网站{}", result, siteId);
            return result;
        }
        
        return 0;
    }
    
    @Override
    public int removeGameFromSite(Long siteId, Long gameId) {
        return siteGameRelationMapper.deleteGbSiteGameRelation(siteId, gameId, null);
    }
    
    // ==================== 文章关联 ====================
    
    @Override
    @Transactional
    public int addArticleToSite(Long siteId, Long articleId) {
        // 检查关联是否已存在
        if (siteArticleRelationMapper.checkRelationExists(siteId, articleId) > 0) {
            logger.info("文章{}已关联到网站{}", articleId, siteId);
            return 0;
        }
        
        // 确保文章的分类在目标网站可用
        ensureArticleCategoryAvailable(articleId, siteId);
        
        GbSiteArticleRelation relation = new GbSiteArticleRelation();
        relation.setSiteId(siteId);
        relation.setArticleId(articleId);
        relation.setRelationType("include");
        relation.setIsVisible("1");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteArticleRelationMapper.insertGbSiteArticleRelation(relation);
    }
    
    @Override
    @Transactional
    public int batchAddArticlesToSite(Long siteId, List<Long> articleIds) {
        List<GbSiteArticleRelation> relations = new ArrayList<>();
        String username = SecurityUtils.getUsername();
        Date now = new Date();
        
        for (Long articleId : articleIds) {
            if (siteArticleRelationMapper.checkRelationExists(siteId, articleId) > 0) {
                continue;
            }
            
            // 确保文章的分类在目标网站可用
            ensureArticleCategoryAvailable(articleId, siteId);
            
            GbSiteArticleRelation relation = new GbSiteArticleRelation();
            relation.setSiteId(siteId);
            relation.setArticleId(articleId);
            relation.setRelationType("include");
            relation.setIsVisible("1");
            relation.setCreateBy(username);
            relation.setCreateTime(now);
            relations.add(relation);
        }
        
        if (relations.isEmpty()) {
            return 0;
        }
        
        return siteArticleRelationMapper.batchInsertGbSiteArticleRelation(relations);
    }
    
    @Override
    public int removeArticleFromSite(Long siteId, Long articleId) {
        return siteArticleRelationMapper.deleteGbSiteArticleRelation(siteId, articleId, null);
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 批量确保分类在目标网站可用（包括父分类）
     * 优化：先收集所有需要关联的分类（包括父分类），去重后批量处理
     */
    @Transactional
    public void batchEnsureCategoriesAvailable(List<Long> categoryIds, Long targetSiteId) {
        logger.info("[批量分类关联] 开始处理，初始分类ID数量: {}, 目标网站: {}", categoryIds != null ? categoryIds.size() : 0, targetSiteId);
        
        if (categoryIds == null || categoryIds.isEmpty()) {
            logger.warn("[批量分类关联] 分类ID列表为空，跳过处理");
            return;
        }
        
        // 收集所有需要关联的分类ID（包括父分类）
        Set<Long> allCategoryIds = new HashSet<>();
        for (Long categoryId : categoryIds) {
            collectCategoryWithParents(categoryId, allCategoryIds);
        }
        
        if (allCategoryIds.isEmpty()) {
            logger.warn("[批量分类关联] 收集父分类后仍为空，跳过处理");
            return;
        }
        
        logger.info("[批量分类关联] 需要关联{}个分类（含父分类）到网站{}: {}", allCategoryIds.size(), targetSiteId, allCategoryIds);
        
        // 批量查询已存在的关联
        List<Long> existingRelations = siteCategoryRelationMapper.selectExistingRelations(
            targetSiteId, new ArrayList<>(allCategoryIds));
        Set<Long> existingSet = new HashSet<>(existingRelations);
        
        // 过滤出需要新建的关联
        List<Long> needToAdd = allCategoryIds.stream()
            .filter(id -> !existingSet.contains(id))
            .collect(Collectors.toList());
        
        if (needToAdd.isEmpty()) {
            logger.info("[批量分类关联] 所有分类已存在关联，无需添加");
            return;
        }
        
        logger.info("[批量分类关联] 需要新增{}个分类关联: {}", needToAdd.size(), needToAdd);
        
        // 批量查询分类信息
        List<GbCategory> categories = categoryMapper.selectGbCategoryByIds(needToAdd);
        
        // 准备批量插入的数据
        List<GbSiteCategoryRelation> relationsToAdd = new ArrayList<>();
        // 注意：在异步线程中无法获取SecurityContext，使用默认系统用户
        String username = "system";
        try {
            username = SecurityUtils.getUsername();
        } catch (Exception e) {
            logger.debug("无法获取当前用户，使用system作为创建者");
        }
        Date now = new Date();
        
        for (GbCategory category : categories) {
            GbSiteCategoryRelation relation = new GbSiteCategoryRelation();
            relation.setSiteId(targetSiteId);
            relation.setCategoryId(category.getId());
            relation.setRelationType("include");
            relation.setIsVisible("1");
            // 如果是目标网站自己创建的分类，设置为可编辑；否则不可编辑
            relation.setIsEditable(
                category.getSiteId() != null && category.getSiteId().equals(targetSiteId) ? "1" : "0"
            );
            relation.setCreateBy(username);
            relation.setCreateTime(now);
            relationsToAdd.add(relation);
        }
        
        // 批量插入
        if (!relationsToAdd.isEmpty()) {
            logger.info("[批量分类关联] 准备批量插入{}个分类关联", relationsToAdd.size());
            siteCategoryRelationMapper.batchInsertGbSiteCategoryRelation(relationsToAdd);
            logger.info("[批量分类关联] ✓ 成功批量添加{}个分类关联到网站{}", relationsToAdd.size(), targetSiteId);
        } else {
            logger.warn("[批量分类关联] 没有需要插入的关联数据");
        }
    }
    
    /**
     * 递归收集分类及其所有父分类
     */
    private void collectCategoryWithParents(Long categoryId, Set<Long> result) {
        if (categoryId == null || categoryId == 0) {
            logger.debug("[收集父分类] categoryId为null或0，跳过");
            return;
        }
        
        if (result.contains(categoryId)) {
            logger.debug("[收集父分类] 分类{}已存在，跳过", categoryId);
            return;
        }
        
        GbCategory category = categoryMapper.selectGbCategoryById(categoryId);
        if (category == null) {
            logger.warn("[收集父分类] 分类{}不存在", categoryId);
            return;
        }
        
        // 添加当前分类
        result.add(categoryId);
        logger.debug("[收集父分类] ✓ 添加分类{}: {}", categoryId, category.getName());
        
        // 递归处理父分类
        if (category.getParentId() != null && category.getParentId() != 0) {
            logger.debug("[收集父分类] 分类{}有父分类{}，继续收集", categoryId, category.getParentId());
            collectCategoryWithParents(category.getParentId(), result);
        }
    }
    
    @Override
    @Transactional
    public boolean ensureCategoryAvailable(Long categoryId, Long targetSiteId) {
        GbCategory category = categoryMapper.selectGbCategoryById(categoryId);
        if (category == null) {
            logger.warn("分类{}不存在", categoryId);
            return false;
        }
        
        // 首先递归确保所有父分类都已关联（从顶级向下）
        if (category.getParentId() != null && category.getParentId() != 0) {
            // 递归处理父分类
            boolean parentEnsured = ensureCategoryAvailable(category.getParentId(), targetSiteId);
            if (!parentEnsured) {
                logger.warn("父分类{}无法关联到网站{}", category.getParentId(), targetSiteId);
                // 即使父分类关联失败，仍然尝试关联当前分类
            }
        }
        
        // 检查当前分类是否已有关联
        if (siteCategoryRelationMapper.checkRelationExists(targetSiteId, categoryId) > 0) {
            logger.info("分类{}已关联到网站{}", categoryId, targetSiteId);
            return true;
        }
        
        // 自动创建关联（无论分类是哪个网站创建的，都自动关联）
        GbSiteCategoryRelation relation = new GbSiteCategoryRelation();
        relation.setSiteId(targetSiteId);
        relation.setCategoryId(categoryId);
        relation.setIsVisible("1");
        // 如果是目标网站自己创建的分类，设置为可编辑；否则不可编辑
        relation.setIsEditable(
            category.getSiteId() != null && category.getSiteId().equals(targetSiteId) ? "1" : "0"
        );
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        int result = siteCategoryRelationMapper.insertGbSiteCategoryRelation(relation);
        if (result > 0) {
            logger.info("成功将分类{}关联到网站{}", categoryId, targetSiteId);
            return true;
        } else {
            logger.error("将分类{}关联到网站{}失败", categoryId, targetSiteId);
            return false;
        }
    }
    
    @Override
    @Transactional
    public int ensureBoxCategoriesAvailable(Long boxId, Long targetSiteId) {
        GbGameBox box = gameBoxMapper.selectGbGameBoxById(boxId);
        if (box == null) {
            return 0;
        }
        
        // 收集所有需要关联的分类ID
        Set<Long> categoryIds = new HashSet<>();
        
        // 添加主分类
        if (box.getCategoryId() != null) {
            categoryIds.add(box.getCategoryId());
        }
        
        // 添加多分类
        List<GbBoxCategoryRelation> categoryRelations = boxCategoryRelationMapper.selectCategoriesByBoxId(boxId);
        for (GbBoxCategoryRelation rel : categoryRelations) {
            categoryIds.add(rel.getCategoryId());
        }
        
        // 批量关联所有分类（包括父分类）
        if (!categoryIds.isEmpty()) {
            batchEnsureCategoriesAvailable(new ArrayList<>(categoryIds), targetSiteId);
        }
        
        return categoryIds.size();
    }
    
    @Override
    @Transactional
    public int ensureGameCategoriesAvailable(Long gameId, Long targetSiteId) {
        int count = 0;
        GbGame game = gameMapper.selectGbGameById(gameId);
        if (game == null) {
            return 0;
        }
        
        // 处理所有分类关联（包括主分类）
        List<GbGameCategoryRelation> categoryRelations = gameCategoryRelationMapper.selectCategoriesByGameId(gameId);
        for (GbGameCategoryRelation rel : categoryRelations) {
            if (ensureCategoryAvailable(rel.getCategoryId(), targetSiteId)) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    @Transactional
    public boolean ensureArticleCategoryAvailable(Long articleId, Long targetSiteId) {
        GbArticle article = articleMapper.selectGbArticleById(articleId);
        if (article == null || article.getCategoryId() == null) {
            return false;
        }
        
        // 确保文章的分类在目标网站可用
        return ensureCategoryAvailable(article.getCategoryId(), targetSiteId);
    }
    
    // ==================== 游戏盒子排除管理 ====================
    
    @Override
    @Transactional
    public int excludeDefaultBox(Long siteId, Long boxId) {
        // 1. 检查是否为个人站点的默认配置
        GbGameBox box = gameBoxMapper.selectGbGameBoxById(boxId);
        if (box == null || !isPersonalSite(box.getSiteId())) {
            logger.warn("游戏盒子{}不是默认配置，无法排除", boxId);
            return 0;
        }
        
        // 2. 检查是否已存在排除记录
        if (siteBoxRelationMapper.checkExcludeRelationExists(siteId, boxId) > 0) {
            logger.info("游戏盒子{}已被网站{}排除", boxId, siteId);
            return 0;
        }
        
        // 3. 创建排除关联
        GbSiteBoxRelation relation = new GbSiteBoxRelation();
        relation.setSiteId(siteId);
        relation.setBoxId(boxId);
        relation.setRelationType("exclude");
        relation.setIsVisible("0");
        relation.setIsEditable("0");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteBoxRelationMapper.insertGbSiteBoxRelation(relation);
    }
    
    @Override
    public int restoreDefaultBox(Long siteId, Long boxId) {
        return siteBoxRelationMapper.deleteExcludeRelation(siteId, boxId);
    }
    
    @Override
    @Transactional
    public int manageDefaultBoxExclusions(Long boxId, List<Long> excludedSiteIds) {
        // 1. 查询当前已排除的网站
        List<Long> currentExcluded = siteBoxRelationMapper.selectExcludedSiteIds(boxId);
        
        // 2. 计算需要新增的排除关联
        List<Long> toAdd = excludedSiteIds.stream()
            .filter(id -> !currentExcluded.contains(id))
            .collect(Collectors.toList());
        
        // 3. 计算需要删除的排除关联
        List<Long> toRemove = currentExcluded.stream()
            .filter(id -> !excludedSiteIds.contains(id))
            .collect(Collectors.toList());
        
        // 4. 批量新增排除关联
        if (!toAdd.isEmpty()) {
            List<GbSiteBoxRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            Date now = new Date();
            
            for (Long siteId : toAdd) {
                GbSiteBoxRelation relation = new GbSiteBoxRelation();
                relation.setSiteId(siteId);
                relation.setBoxId(boxId);
                relation.setRelationType("exclude");
                relation.setIsVisible("0");
                relation.setIsEditable("0");
                relation.setSortOrder(0);
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
            }
            
            siteBoxRelationMapper.batchInsertGbSiteBoxRelation(relations);
        }
        
        // 5. 批量删除排除关联
        if (!toRemove.isEmpty()) {
            siteBoxRelationMapper.batchDeleteExcludeRelations(boxId, toRemove);
        }
        
        return toAdd.size() + toRemove.size();
    }
    
    // ==================== 游戏排除管理 ====================
    
    @Override
    @Transactional
    public int excludeDefaultGame(Long siteId, Long gameId) {
        // 1. 检查是否为个人站点的默认配置
        GbGame game = gameMapper.selectGbGameById(gameId);
        if (game == null || !isPersonalSite(game.getSiteId())) {
            logger.warn("游戏{}不是默认配置，无法排除", gameId);
            return 0;
        }
        
        // 2. 检查是否已存在排除记录
        if (siteGameRelationMapper.checkExcludeRelationExists(siteId, gameId) > 0) {
            logger.info("游戏{}已被网站{}排除", gameId, siteId);
            return 0;
        }
        
        // 3. 创建排除关联
        GbSiteGameRelation relation = new GbSiteGameRelation();
        relation.setSiteId(siteId);
        relation.setGameId(gameId);
        relation.setRelationType("exclude");
        relation.setIsVisible("0");
        relation.setIsEditable("0");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteGameRelationMapper.insertGbSiteGameRelation(relation);
    }
    
    @Override
    public int restoreDefaultGame(Long siteId, Long gameId) {
        return siteGameRelationMapper.deleteExcludeRelation(siteId, gameId);
    }
    
    @Override
    @Transactional
    public int manageDefaultGameExclusions(Long gameId, List<Long> excludedSiteIds) {
        // 1. 查询当前已排除的网站
        List<Long> currentExcluded = siteGameRelationMapper.selectExcludedSiteIds(gameId);
        
        // 2. 计算需要新增的排除关联
        List<Long> toAdd = excludedSiteIds.stream()
            .filter(id -> !currentExcluded.contains(id))
            .collect(Collectors.toList());
        
        // 3. 计算需要删除的排除关联
        List<Long> toRemove = currentExcluded.stream()
            .filter(id -> !excludedSiteIds.contains(id))
            .collect(Collectors.toList());
        
        // 4. 批量新增排除关联
        if (!toAdd.isEmpty()) {
            List<GbSiteGameRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            Date now = new Date();
            
            for (Long siteId : toAdd) {
                GbSiteGameRelation relation = new GbSiteGameRelation();
                relation.setSiteId(siteId);
                relation.setGameId(gameId);
                relation.setRelationType("exclude");
                relation.setIsVisible("0");
                relation.setIsEditable("0");
                relation.setSortOrder(0);
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
            }
            
            siteGameRelationMapper.batchInsertGbSiteGameRelation(relations);
        }
        
        // 5. 批量删除排除关联
        if (!toRemove.isEmpty()) {
            siteGameRelationMapper.batchDeleteExcludeRelations(gameId, toRemove);
        }
        
        return toAdd.size() + toRemove.size();
    }
    
    // ==================== 文章排除管理 ====================
    
    @Override
    @Transactional
    public int excludeDefaultArticle(Long siteId, Long articleId) {
        // 1. 检查是否为个人站点的默认配置
        GbArticle article = articleMapper.selectGbArticleById(articleId);
        if (article == null || !isPersonalSite(article.getSiteId())) {
            logger.warn("文章{}不是默认配置，无法排除", articleId);
            return 0;
        }
        
        // 2. 检查是否已存在排除记录
        if (siteArticleRelationMapper.checkExcludeRelationExists(siteId, articleId) > 0) {
            logger.info("文章{}已被网站{}排除", articleId, siteId);
            return 0;
        }
        
        // 3. 创建排除关联
        GbSiteArticleRelation relation = new GbSiteArticleRelation();
        relation.setSiteId(siteId);
        relation.setArticleId(articleId);
        relation.setRelationType("exclude");
        relation.setIsVisible("0");
        relation.setIsEditable("0");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteArticleRelationMapper.insertGbSiteArticleRelation(relation);
    }
    
    @Override
    public int restoreDefaultArticle(Long siteId, Long articleId) {
        return siteArticleRelationMapper.deleteExcludeRelation(siteId, articleId);
    }
    
    @Override
    @Transactional
    public int manageDefaultArticleExclusions(Long articleId, List<Long> excludedSiteIds) {
        // 1. 查询当前已排除的网站
        List<Long> currentExcluded = siteArticleRelationMapper.selectExcludedSiteIds(articleId);
        
        // 2. 计算需要新增的排除关联
        List<Long> toAdd = excludedSiteIds.stream()
            .filter(id -> !currentExcluded.contains(id))
            .collect(Collectors.toList());
        
        // 3. 计算需要删除的排除关联
        List<Long> toRemove = currentExcluded.stream()
            .filter(id -> !excludedSiteIds.contains(id))
            .collect(Collectors.toList());
        
        // 4. 批量新增排除关联
        if (!toAdd.isEmpty()) {
            List<GbSiteArticleRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            Date now = new Date();
            
            for (Long siteId : toAdd) {
                GbSiteArticleRelation relation = new GbSiteArticleRelation();
                relation.setSiteId(siteId);
                relation.setArticleId(articleId);
                relation.setRelationType("exclude");
                relation.setIsVisible("0");
                relation.setIsEditable("0");
                relation.setSortOrder(0);
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
            }
            
            siteArticleRelationMapper.batchInsertGbSiteArticleRelation(relations);
        }
        
        // 5. 批量删除排除关联
        if (!toRemove.isEmpty()) {
            siteArticleRelationMapper.batchDeleteExcludeRelations(articleId, toRemove);
        }
        
        return toAdd.size() + toRemove.size();
    }
    
    @Override
    @Transactional
    public int updateStorageConfigVisibility(Long siteId, Long storageConfigId, String isVisible) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        params.put("storageConfigId", storageConfigId);
        params.put("isVisible", isVisible);
        params.put("updateBy", SecurityUtils.getUsername());
        params.put("updateTime", DateUtils.getNowDate());
        return siteStorageConfigRelationMapper.updateSiteStorageConfigVisibility(params);
    }
    
    // ==================== AI平台配置关联 ====================
    
    @Override
    @Transactional
    public int excludeDefaultAiPlatform(Long siteId, Long aiPlatformId) {
        // 检查是否已存在排除关联
        if (siteAiPlatformRelationMapper.checkRelationExists(siteId, aiPlatformId) > 0) {
            return 0;
        }
        
        GbSiteAiPlatformRelation relation = new GbSiteAiPlatformRelation();
        relation.setSiteId(siteId);
        relation.setAiPlatformId(aiPlatformId);
        relation.setRelationType("exclude");
        relation.setIsVisible("0");
        relation.setIsEditable("0");
        relation.setSortOrder(0);
        return siteAiPlatformRelationMapper.insertGbSiteAiPlatformRelation(relation);
    }
    
    @Override
    @Transactional
    public int restoreDefaultAiPlatform(Long siteId, Long aiPlatformId) {
        return siteAiPlatformRelationMapper.deleteGbSiteAiPlatformRelation(siteId, aiPlatformId, "exclude");
    }
    
    @Override
    @Transactional
    public int updateAiPlatformVisibility(Long siteId, Long aiPlatformId, String isVisible) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        params.put("aiPlatformId", aiPlatformId);
        params.put("isVisible", isVisible);
        params.put("updateBy", SecurityUtils.getUsername());
        params.put("updateTime", DateUtils.getNowDate());
        return siteAiPlatformRelationMapper.updateSiteAiPlatformVisibility(params);
    }
    
    @Override
    @Transactional
    public int manageDefaultAiPlatformExclusions(Long aiPlatformId, List<Long> excludedSiteIds) {
        // 1. 查询当前已排除的网站
        List<Long> currentExcluded = siteAiPlatformRelationMapper.selectExcludedSiteIds(aiPlatformId);
        
        // 2. 计算需要新增的排除关联
        List<Long> toAdd = excludedSiteIds.stream()
            .filter(id -> !currentExcluded.contains(id))
            .collect(Collectors.toList());
        
        // 3. 计算需要删除的排除关联
        List<Long> toRemove = currentExcluded.stream()
            .filter(id -> !excludedSiteIds.contains(id))
            .collect(Collectors.toList());
        
        // 4. 批量新增排除关联
        if (!toAdd.isEmpty()) {
            List<GbSiteAiPlatformRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            Date now = new Date();
            
            for (Long siteId : toAdd) {
                // 先删除可能存在的include关联，避免唯一约束冲突
                siteAiPlatformRelationMapper.deleteGbSiteAiPlatformRelation(siteId, aiPlatformId, null);
                
                GbSiteAiPlatformRelation relation = new GbSiteAiPlatformRelation();
                relation.setSiteId(siteId);
                relation.setAiPlatformId(aiPlatformId);
                relation.setRelationType("exclude");
                relation.setIsVisible("0");
                relation.setIsEditable("0");
                relation.setSortOrder(0);
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
            }
            
            siteAiPlatformRelationMapper.batchInsertGbSiteAiPlatformRelation(relations);
        }
        
        // 5. 批量删除排除关联
        if (!toRemove.isEmpty()) {
            siteAiPlatformRelationMapper.batchDeleteExcludeRelations(aiPlatformId, toRemove);
        }
        
        return toAdd.size() + toRemove.size();
    }
    
    // ==================== 标题池批次排除管理 ====================
    
    @Override
    @Transactional
    public int excludeDefaultBatch(Long siteId, Long batchId) {
        // 1. 检查是否已存在排除记录
        if (siteTitleBatchRelationMapper.checkExcludeRelationExists(siteId, batchId) > 0) {
            logger.info("标题池批次{}已被网站{}排除", batchId, siteId);
            return 0;
        }
        
        // 2. 创建排除关联
        GbSiteRelation relation = new GbSiteRelation();
        relation.setSiteId(siteId);
        relation.setRelatedId(batchId);
        relation.setRelationType("exclude");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteTitleBatchRelationMapper.insertSiteRelation(relation);
    }
    
    @Override
    @Transactional
    public int restoreDefaultBatch(Long siteId, Long batchId) {
        return siteTitleBatchRelationMapper.deleteSiteRelation(siteId, batchId, "exclude");
    }
    
    @Override
    @Transactional
    public int manageDefaultBatchExclusions(Long batchId, List<Long> excludedSiteIds) {
        // 1. 查询当前已排除的网站
        List<Long> currentExcluded = siteTitleBatchRelationMapper.selectExcludedSiteIdsByRelationType(batchId, "exclude");
        
        // 2. 计算需要新增的排除关联
        List<Long> toAdd = excludedSiteIds.stream()
            .filter(id -> !currentExcluded.contains(id))
            .collect(Collectors.toList());
        
        // 3. 计算需要删除的排除关联
        List<Long> toRemove = currentExcluded.stream()
            .filter(id -> !excludedSiteIds.contains(id))
            .collect(Collectors.toList());
        
        // 4. 批量新增排除关联
        if (!toAdd.isEmpty()) {
            List<GbSiteRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            Date now = new Date();
            
            for (Long siteId : toAdd) {
                GbSiteRelation relation = new GbSiteRelation();
                relation.setSiteId(siteId);
                relation.setRelatedId(batchId);
                relation.setRelationType("exclude");
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
            }
            
            siteTitleBatchRelationMapper.batchInsertSiteRelations(relations);
        }
        
        // 5. 批量删除排除关联
        if (!toRemove.isEmpty()) {
            for (Long siteId : toRemove) {
                siteTitleBatchRelationMapper.deleteSiteRelation(siteId, batchId, "exclude");
            }
        }
        
        return toAdd.size() + toRemove.size();
    }
    
    @Override
    public List<Map<String, Object>> getExcludedSitesByBatch(Long batchId) {
        // 查询所有非默认配置的网站
        List<GbSite> allSites = siteMapper.selectGbSiteList(new GbSite());
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询当前已排除的网站ID列表
        List<Long> excludedSiteIds = siteTitleBatchRelationMapper.selectExcludedSiteIdsByRelationType(batchId, "exclude");
        
        // 构造返回结果
        for (GbSite site : allSites) {
            if (site.getId() > 0) { // 排除默认配置
                Map<String, Object> siteInfo = new HashMap<>();
                siteInfo.put("id", site.getId());
                siteInfo.put("name", site.getName());
                siteInfo.put("isExcluded", excludedSiteIds.contains(site.getId()) ? 1 : 0);
                result.add(siteInfo);
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public int updateBatchVisibility(Long siteId, Long batchId, String isVisible) {
        // 查询关联记录
        GbSiteTitleBatchRelation query = new GbSiteTitleBatchRelation();
        query.setSiteId(siteId);
        query.setTitleBatchId(batchId);
        query.setRelationType("include"); // 使用 include 类型（共享关联存储为 include）
        
        List<GbSiteTitleBatchRelation> relations = siteTitleBatchRelationMapper.selectGbSiteTitleBatchRelationList(query);
        if (relations.isEmpty()) {
            logger.warn("未找到网站{}和批次{}的关联记录，无法更新可见性", siteId, batchId);
            return 0;
        }
        
        // 更新可见性
        GbSiteTitleBatchRelation relation = relations.get(0);
        relation.setIsVisible(isVisible);
        
        return siteTitleBatchRelationMapper.updateGbSiteTitleBatchRelation(relation);
    }
    
    // ==================== 文章生成任务排除管理 ====================
    
    @Override
    @Transactional
    public int excludeDefaultGeneration(Long siteId, Long generationId) {
        // 1. 检查是否已存在排除记录
        if (siteGenerationRelationMapper.checkExcludeRelationExists(siteId, generationId) > 0) {
            logger.info("文章生成任务{}已被网站{}排除", generationId, siteId);
            return 0;
        }
        
        // 2. 创建排除关联
        GbSiteRelation relation = new GbSiteRelation();
        relation.setSiteId(siteId);
        relation.setRelatedId(generationId);
        relation.setRelationType("exclude");
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteGenerationRelationMapper.insertSiteRelation(relation);
    }
    
    @Override
    @Transactional
    public int restoreDefaultGeneration(Long siteId, Long generationId) {
        return siteGenerationRelationMapper.deleteSiteRelation(siteId, generationId, "exclude");
    }
    
    @Override
    @Transactional
    public int manageDefaultGenerationExclusions(Long generationId, List<Long> excludedSiteIds) {
        // 1. 查询当前已排除的网站
        List<Long> currentExcluded = siteGenerationRelationMapper.selectExcludedSiteIdsByRelationType(generationId, "exclude");
        
        // 2. 计算需要新增的排除关联
        List<Long> toAdd = excludedSiteIds.stream()
            .filter(id -> !currentExcluded.contains(id))
            .collect(Collectors.toList());
        
        // 3. 计算需要删除的排除关联
        List<Long> toRemove = currentExcluded.stream()
            .filter(id -> !excludedSiteIds.contains(id))
            .collect(Collectors.toList());
        
        // 4. 批量新增排除关联
        if (!toAdd.isEmpty()) {
            List<GbSiteRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            Date now = new Date();
            
            for (Long siteId : toAdd) {
                GbSiteRelation relation = new GbSiteRelation();
                relation.setSiteId(siteId);
                relation.setRelatedId(generationId);
                relation.setRelationType("exclude");
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
            }
            
            siteGenerationRelationMapper.batchInsertSiteRelations(relations);
        }
        
        // 5. 批量删除排除关联
        if (!toRemove.isEmpty()) {
            for (Long siteId : toRemove) {
                siteGenerationRelationMapper.deleteSiteRelation(siteId, generationId, "exclude");
            }
        }
        
        return toAdd.size() + toRemove.size();
    }
    
    @Override
    public List<Long> getExcludedSitesByGeneration(Long generationId) {
        return siteGenerationRelationMapper.selectExcludedSiteIdsByRelationType(generationId, "exclude");
    }
    
    // ==================== 原子工具关联实现 ====================
    
    @Override
    @Transactional
    public int addAtomicToolToSite(Long siteId, Long atomicToolId, String isVisible) {
        try {
            GbSiteAtomicToolRelation relation = new GbSiteAtomicToolRelation();
            relation.setSiteId(siteId);
            relation.setAtomicToolId(atomicToolId);
            relation.setIsVisible(isVisible);
            relation.setRelationType("include");
            relation.setCreateBy(SecurityUtils.getUsername());
            relation.setCreateTime(new Date());
            
            return siteAtomicToolRelationMapper.insertSiteAtomicToolRelation(relation);
        } catch (Exception e) {
            logger.error("添加原子工具到网站失败", e);
            throw new RuntimeException("添加原子工具到网站失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public int removeAtomicToolFromSite(Long siteId, Long atomicToolId) {
        try {
            return siteAtomicToolRelationMapper.deleteGbSiteAtomicToolRelation(siteId, atomicToolId, "include");
        } catch (Exception e) {
            logger.error("从网站移除原子工具失败", e);
            throw new RuntimeException("从网站移除原子工具失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public int updateAtomicToolVisibility(Long siteId, Long atomicToolId, String isVisible) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("siteId", siteId);
            params.put("atomicToolId", atomicToolId);
            params.put("isVisible", isVisible);
            params.put("updateBy", SecurityUtils.getUsername());
            params.put("updateTime", new Date());
            
            return siteAtomicToolRelationMapper.updateSiteAtomicToolVisibility(params);
        } catch (Exception e) {
            logger.error("更新原子工具可见性失败", e);
            throw new RuntimeException("更新原子工具可见性失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public int excludeDefaultAtomicTool(Long siteId, Long atomicToolId) {
        try {
            GbSiteAtomicToolRelation relation = new GbSiteAtomicToolRelation();
            relation.setSiteId(siteId);
            relation.setAtomicToolId(atomicToolId);
            relation.setRelationType("exclude");
            relation.setIsVisible("0");
            relation.setCreateBy(SecurityUtils.getUsername());
            relation.setCreateTime(new Date());
            
            return siteAtomicToolRelationMapper.insertSiteAtomicToolRelation(relation);
        } catch (Exception e) {
            logger.error("排除默认原子工具失败", e);
            throw new RuntimeException("排除默认原子工具失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public int restoreDefaultAtomicTool(Long siteId, Long atomicToolId) {
        try {
            return siteAtomicToolRelationMapper.deleteGbSiteAtomicToolRelation(siteId, atomicToolId, "exclude");
        } catch (Exception e) {
            logger.error("恢复默认原子工具失败", e);
            throw new RuntimeException("恢复默认原子工具失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public int manageDefaultAtomicToolExclusions(Long atomicToolId, List<Long> excludedSiteIds) {
        try {
            // 1. 获取当前排除列表
            List<Long> currentExcludedSites = siteAtomicToolRelationMapper
                .selectExcludedSiteIdsByRelationType(atomicToolId, "exclude");
            
            // 2. 计算需要新增和删除的网站
            Set<Long> currentSet = new HashSet<>(currentExcludedSites);
            Set<Long> newSet = new HashSet<>(excludedSiteIds);
            
            // 需要添加的
            Set<Long> toAdd = new HashSet<>(newSet);
            toAdd.removeAll(currentSet);
            
            // 需要移除的
            Set<Long> toRemove = new HashSet<>(currentSet);
            toRemove.removeAll(newSet);
            
            // 3. 批量新增排除关联
            if (!toAdd.isEmpty()) {
                List<GbSiteAtomicToolRelation> relations = new ArrayList<>();
                String username = SecurityUtils.getUsername();
                Date now = new Date();
                
                for (Long siteId : toAdd) {
                    GbSiteAtomicToolRelation relation = new GbSiteAtomicToolRelation();
                    relation.setSiteId(siteId);
                    relation.setAtomicToolId(atomicToolId);
                    relation.setRelationType("exclude");
                    relation.setIsVisible("0");
                    relation.setCreateBy(username);
                    relation.setCreateTime(now);
                    relations.add(relation);
                }
                
                siteAtomicToolRelationMapper.batchInsertSiteAtomicToolRelations(relations);
            }
            
            // 4. 批量删除排除关联
            if (!toRemove.isEmpty()) {
                for (Long siteId : toRemove) {
                    siteAtomicToolRelationMapper.deleteGbSiteAtomicToolRelation(siteId, atomicToolId, "exclude");
                }
            }
            
            return toAdd.size() + toRemove.size();
        } catch (Exception e) {
            logger.error("管理默认原子工具排除列表失败", e);
            throw new RuntimeException("管理默认原子工具排除列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Long> getExcludedSitesByAtomicTool(Long atomicToolId) {
        return siteAtomicToolRelationMapper.selectExcludedSiteIdsByRelationType(atomicToolId, "exclude");
    }
}
