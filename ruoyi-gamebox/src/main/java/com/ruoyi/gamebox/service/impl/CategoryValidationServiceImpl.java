package com.ruoyi.gamebox.service.impl;

import com.ruoyi.gamebox.domain.GbCategory;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.domain.GbSiteCategoryRelation;
import com.ruoyi.gamebox.mapper.GbCategoryMapper;
import com.ruoyi.gamebox.mapper.GbSiteCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.service.ICategoryValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类验证服务实现
 * 用于验证导入数据中的分类ID是否有效
 * 
 * @author ruoyi
 * @date 2026-01-26
 */
@Service
public class CategoryValidationServiceImpl implements ICategoryValidationService {
    
    private static final Logger log = LoggerFactory.getLogger(CategoryValidationServiceImpl.class);
    
    @Autowired
    private GbCategoryMapper categoryMapper;
    
    @Autowired
    private GbSiteCategoryRelationMapper siteCategoryRelationMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;
    
    /**
     * 验证分类ID是否对指定网站可见
     * 
     * @param categoryId 分类ID
     * @param siteId 网站ID
     * @return true-可见/有效, false-不可见/无效
     */
    @Override
    public boolean isCategoryVisibleForSite(Long categoryId, Long siteId) {
        if (categoryId == null) {
            return false;
        }
        
        GbCategory category = categoryMapper.selectGbCategoryById(categoryId);
        if (category == null) {
            log.warn("分类ID {} 不存在", categoryId);
            return false;
        }
        
        // 检查分类状态
        if (!"1".equals(category.getStatus())) {
            log.warn("分类ID {} 已禁用", categoryId);
            return false;
        }
        
        // 1. 个人站分类对所有网站可见
        boolean isGlobalCategory = category.getSiteId() == null;
        if (!isGlobalCategory && category.getSiteId() != null) {
            GbSite categorySite = gbSiteMapper.selectGbSiteById(category.getSiteId());
            if (categorySite != null && Integer.valueOf(1).equals(categorySite.getIsPersonal())) {
                isGlobalCategory = true;
            }
        }
        if (isGlobalCategory) {
            // 检查是否在 gb_site_category_relations 中被明确排除
            GbSiteCategoryRelation exclusion = new GbSiteCategoryRelation();
            exclusion.setSiteId(siteId);
            exclusion.setCategoryId(categoryId);
            exclusion.setRelationType("exclude");
            List<GbSiteCategoryRelation> exclusions = siteCategoryRelationMapper.selectGbSiteCategoryRelationList(exclusion);
            
            // 如果被排除，则不可见
            if (exclusions != null && !exclusions.isEmpty()) {
                log.warn("分类ID {} 被网站 {} 明确排除", categoryId, siteId);
                return false;
            }
            return true;
        }
        
        // 2. 网站自有分类
        if (category.getSiteId().equals(siteId)) {
            return true;
        }
        
        // 3. 其他网站的分类，检查是否在 gb_site_category_relations 中可见
        GbSiteCategoryRelation relation = new GbSiteCategoryRelation();
        relation.setSiteId(siteId);
        relation.setCategoryId(categoryId);
        relation.setRelationType("include");
        List<GbSiteCategoryRelation> relations = siteCategoryRelationMapper.selectGbSiteCategoryRelationList(relation);
        
        if (relations != null && !relations.isEmpty()) {
            GbSiteCategoryRelation rel = relations.get(0);
            // 检查是否可见
            if ("1".equals(rel.getIsVisible())) {
                return true;
            }
        }
        
        log.warn("分类ID {} 对网站 {} 不可见", categoryId, siteId);
        return false;
    }
    
    /**
     * 批量验证分类ID
     * 
     * @param categoryIds 分类ID列表
     * @param siteId 网站ID
     * @return 有效的分类ID集合
     */
    @Override
    public Set<Long> getValidCategoryIds(Collection<Long> categoryIds, Long siteId) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptySet();
        }
        
        return categoryIds.stream()
                .filter(id -> isCategoryVisibleForSite(id, siteId))
                .collect(Collectors.toSet());
    }
    
    /**
     * 获取无效的分类ID
     * 
     * @param categoryIds 分类ID列表
     * @param siteId 网站ID
     * @return 无效的分类ID集合
     */
    @Override
    public Set<Long> getInvalidCategoryIds(Collection<Long> categoryIds, Long siteId) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptySet();
        }
        
        return categoryIds.stream()
                .filter(id -> !isCategoryVisibleForSite(id, siteId))
                .collect(Collectors.toSet());
    }
    
    /**
     * 验证游戏数据列表中的分类ID
     * 
     * @param gameDataList 游戏数据列表
     * @param siteId 网站ID
     * @return 验证结果 {success: true/false, message: "错误信息", invalidGames: [...]}
     */
    @Override
    public Map<String, Object> validateGameCategories(List<Map<String, Object>> gameDataList, Long siteId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> invalidGames = new ArrayList<>();
        
        for (int i = 0; i < gameDataList.size(); i++) {
            Map<String, Object> gameData = gameDataList.get(i);
            Object categoryIdObj = gameData.get("categoryId");
            
            if (categoryIdObj == null) {
                continue; // 没有分类ID，跳过
            }
            
            Long categoryId;
            try {
                categoryId = Long.valueOf(String.valueOf(categoryIdObj));
            } catch (NumberFormatException e) {
                log.warn("分类ID格式错误: {}", categoryIdObj);
                Map<String, Object> invalidGame = new HashMap<>();
                invalidGame.put("index", i);
                invalidGame.put("name", gameData.get("name"));
                invalidGame.put("categoryId", categoryIdObj);
                invalidGame.put("reason", "分类ID格式错误");
                invalidGames.add(invalidGame);
                continue;
            }
            
            // 验证分类ID
            if (!isCategoryVisibleForSite(categoryId, siteId)) {
                Map<String, Object> invalidGame = new HashMap<>();
                invalidGame.put("index", i);
                invalidGame.put("name", gameData.get("name"));
                invalidGame.put("categoryId", categoryId);
                invalidGame.put("reason", "分类ID不存在或对当前网站不可见");
                invalidGames.add(invalidGame);
            }
        }
        
        boolean success = invalidGames.isEmpty();
        result.put("success", success);
        result.put("invalidGames", invalidGames);
        
        if (!success) {
            StringBuilder message = new StringBuilder("以下游戏的分类ID无效:\n");
            for (Map<String, Object> game : invalidGames) {
                message.append(String.format("• 游戏【%s】的分类ID %s (%s)\n",
                        game.get("name"),
                        game.get("categoryId"),
                        game.get("reason")));
            }
            result.put("message", message.toString());
        }
        
        return result;
    }
    
    /**
     * 根据分类名称查找分类ID
     * 
     * @param categoryName 分类名称
     * @param siteId 网站ID
     * @return 分类ID，未找到返回null
     */
    @Override
    public Long findCategoryIdByName(String categoryName, Long siteId) {
        if (categoryName == null || categoryName.isEmpty()) {
            return null;
        }
        
        // 构建查询条件
        GbCategory query = new GbCategory();
        query.setName(categoryName);
        query.setStatus("1");
        
        List<GbCategory> categories = categoryMapper.selectGbCategoryList(query);
        
        if (categories.isEmpty()) {
            log.warn("未找到分类: {}", categoryName);
            return null;
        }
        
        // 优先选择当前网站的分类
        for (GbCategory category : categories) {
            if (category.getSiteId().equals(siteId)) {
                return category.getId();
            }
        }
        
        // 其次选择个人站点的分类
        for (GbCategory category : categories) {
            if (category.getSiteId() != null) {
                GbSite catSite = gbSiteMapper.selectGbSiteById(category.getSiteId());
                if (catSite != null && Integer.valueOf(1).equals(catSite.getIsPersonal())) {
                    return category.getId();
                }
            }
        }
        
        // 最后选择其他网站共享的分类（通过关联表检查）
        for (GbCategory category : categories) {
            if (isCategoryVisibleForSite(category.getId(), siteId)) {
                return category.getId();
            }
        }
        
        return null;
    }
}
