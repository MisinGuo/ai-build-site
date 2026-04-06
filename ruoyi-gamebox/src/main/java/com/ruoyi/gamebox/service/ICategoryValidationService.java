package com.ruoyi.gamebox.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分类验证服务接口
 * 
 * @author ruoyi
 * @date 2026-01-26
 */
public interface ICategoryValidationService {
    
    /**
     * 验证分类ID是否对指定网站可见
     * 
     * @param categoryId 分类ID
     * @param siteId 网站ID
     * @return true-可见/有效, false-不可见/无效
     */
    boolean isCategoryVisibleForSite(Long categoryId, Long siteId);
    
    /**
     * 批量验证分类ID
     * 
     * @param categoryIds 分类ID列表
     * @param siteId 网站ID
     * @return 有效的分类ID集合
     */
    Set<Long> getValidCategoryIds(Collection<Long> categoryIds, Long siteId);
    
    /**
     * 获取无效的分类ID
     * 
     * @param categoryIds 分类ID列表
     * @param siteId 网站ID
     * @return 无效的分类ID集合
     */
    Set<Long> getInvalidCategoryIds(Collection<Long> categoryIds, Long siteId);
    
    /**
     * 验证游戏数据列表中的分类ID
     * 
     * @param gameDataList 游戏数据列表
     * @param siteId 网站ID
     * @return 验证结果 {success: true/false, message: "错误信息", invalidGames: [...]}
     */
    Map<String, Object> validateGameCategories(List<Map<String, Object>> gameDataList, Long siteId);
    
    /**
     * 根据分类名称查找分类ID
     * 
     * @param categoryName 分类名称
     * @param siteId 网站ID
     * @return 分类ID，未找到返回null
     */
    Long findCategoryIdByName(String categoryName, Long siteId);
}
