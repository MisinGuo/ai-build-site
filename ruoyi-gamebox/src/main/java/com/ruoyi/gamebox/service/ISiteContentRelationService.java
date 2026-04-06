package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.*;

/**
 * 网站内容关联管理Service接口
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
public interface ISiteContentRelationService 
{
    // ==================== 分类关联 ====================
    
    /**
     * 将分类添加到网站
     * 
     * @param siteId 网站ID
     * @param categoryId 分类ID
     * @param isEditable 是否可编辑
     * @return 结果
     */
    int addCategoryToSite(Long siteId, Long categoryId, boolean isEditable);
    
    /**
     * 批量将分类添加到网站
     * 
     * @param siteId 网站ID
     * @param categoryIds 分类ID列表
     * @param isEditable 是否可编辑
     * @return 结果
     */
    int batchAddCategoriesToSite(Long siteId, List<Long> categoryIds, boolean isEditable);
    
    /**
     * 从网站移除分类（支持指定类型删除）
     * 
     * @param siteId 网站ID
     * @param categoryId 分类ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    int removeCategoryFromSite(Long siteId, Long categoryId, String relationType);
    
    /**
     * 查询网站的所有可用分类
     * 
     * @param siteId 网站ID
     * @param categoryType 分类类型（可选）
     * @return 分类列表
     */
    List<GbCategory> getAvailableCategoriesBySite(Long siteId, String categoryType);
    
    // ==================== 游戏盒子关联 ====================
    
    /**
     * 将游戏盒子添加到网站
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @param includeGames 是否同时添加盒子中的游戏
     * @return 结果
     */
    int addBoxToSite(Long siteId, Long boxId, boolean includeGames);
    
    /**
     * 批量将游戏盒子添加到网站
     * 
     * @param siteId 网站ID
     * @param boxIds 盒子ID列表
     * @param includeGames 是否同时添加游戏
     * @return 结果
     */
    int batchAddBoxesToSite(Long siteId, List<Long> boxIds, boolean includeGames);
    
    /**
     * 从网站移除游戏盒子
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @param removeGames 是否同时移除游戏
     * @return 结果
     */
    int removeBoxFromSite(Long siteId, Long boxId, boolean removeGames);
    
    /**
     * 将盒子复制到目标网站（包括游戏和分类）
     * 
     * @param boxId 盒子ID
     * @param targetSiteId 目标网站ID
     * @return 结果
     */
    int copyBoxToSite(Long boxId, Long targetSiteId);
    
    // ==================== 游戏关联 ====================
    
    /**
     * 将游戏添加到网站
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 结果
     */
    int addGameToSite(Long siteId, Long gameId);
    
    /**
     * 批量将游戏添加到网站
     * 
     * @param siteId 网站ID
     * @param gameIds 游戏ID列表
     * @return 结果
     */
    int batchAddGamesToSite(Long siteId, List<Long> gameIds);
    
    /**
     * 从网站移除游戏
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 结果
     */
    int removeGameFromSite(Long siteId, Long gameId);
    
    // ==================== 文章关联 ====================
    
    /**
     * 将文章添加到网站
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 结果
     */
    int addArticleToSite(Long siteId, Long articleId);
    
    /**
     * 批量将文章添加到网站
     * 
     * @param siteId 网站ID
     * @param articleIds 文章ID列表
     * @return 结果
     */
    int batchAddArticlesToSite(Long siteId, List<Long> articleIds);
    
    /**
     * 从网站移除文章
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 结果
     */
    int removeArticleFromSite(Long siteId, Long articleId);
    
    // ==================== 辅助方法 ====================
    
    /**
     * 确保分类在目标网站可用
     * 
     * @param categoryId 分类ID
     * @param targetSiteId 目标网站ID
     * @return 是否需要创建新关联
     */
    boolean ensureCategoryAvailable(Long categoryId, Long targetSiteId);
    
    /**
     * 批量确保分类在目标网站可用（包括父分类）
     * 
     * @param categoryIds 分类ID列表
     * @param targetSiteId 目标网站ID
     */
    void batchEnsureCategoriesAvailable(List<Long> categoryIds, Long targetSiteId);
    
    /**
     * 确保盒子的所有分类在目标网站可用
     * 
     * @param boxId 盒子ID
     * @param targetSiteId 目标网站ID
     * @return 添加的分类关联数量
     */
    int ensureBoxCategoriesAvailable(Long boxId, Long targetSiteId);
    
    /**
     * 确保游戏的所有分类在目标网站可用
     * 
     * @param gameId 游戏ID
     * @param targetSiteId 目标网站ID
     * @return 添加的分类关联数量
     */
    int ensureGameCategoriesAvailable(Long gameId, Long targetSiteId);
        /**
     * 确保文章的分类在目标网站可用
     * 
     * @param articleId 文章ID
     * @param targetSiteId 目标网站ID
     * @return 是否成功
     */
    boolean ensureArticleCategoryAvailable(Long articleId, Long targetSiteId);
        // ==================== 排除管理 ====================
    
    /**
     * 排除默认游戏盒子（针对默认配置的黑名单）
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @return 结果
     */
    int excludeDefaultBox(Long siteId, Long boxId);
    
    /**
     * 恢复默认游戏盒子（移除黑名单）
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @return 结果
     */
    int restoreDefaultBox(Long siteId, Long boxId);
    
    /**
     * 管理默认游戏盒子的排除列表
     * 
     * @param boxId 盒子ID
     * @param excludedSiteIds 排除的网站ID列表
     * @return 结果
     */
    int manageDefaultBoxExclusions(Long boxId, List<Long> excludedSiteIds);
    
    /**
     * 排除默认游戏
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 结果
     */
    int excludeDefaultGame(Long siteId, Long gameId);
    
    /**
     * 恢复默认游戏
     * 
     * @param siteId 网站ID
     * @param gameId 游戏ID
     * @return 结果
     */
    int restoreDefaultGame(Long siteId, Long gameId);
    
    /**
     * 管理默认游戏的排除列表
     * 
     * @param gameId 游戏ID
     * @param excludedSiteIds 排除的网站ID列表
     * @return 结果
     */
    int manageDefaultGameExclusions(Long gameId, List<Long> excludedSiteIds);
    
    /**
     * 排除默认文章
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 结果
     */
    int excludeDefaultArticle(Long siteId, Long articleId);
    
    /**
     * 恢复默认文章
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 结果
     */
    int restoreDefaultArticle(Long siteId, Long articleId);
    
    /**
     * 管理默认文章的排除列表
     * 
     * @param articleId 文章ID
     * @param excludedSiteIds 排除的网站ID列表
     * @return 结果
     */
    int manageDefaultArticleExclusions(Long articleId, List<Long> excludedSiteIds);
    
    /**
     * 更新存储配置在网站的可见性
     * 
     * @param siteId 网站ID
     * @param storageConfigId 存储配置ID
     * @param isVisible 可见性
     * @return 结果
     */
    int updateStorageConfigVisibility(Long siteId, Long storageConfigId, String isVisible);
    
    // ==================== AI平台配置关联 ====================
    
    /**
     * 排除默认AI平台配置
     * 
     * @param siteId 网站ID
     * @param aiPlatformId AI平台配置ID
     * @return 结果
     */
    int excludeDefaultAiPlatform(Long siteId, Long aiPlatformId);
    
    /**
     * 恢复默认AI平台配置
     * 
     * @param siteId 网站ID
     * @param aiPlatformId AI平台配置ID
     * @return 结果
     */
    int restoreDefaultAiPlatform(Long siteId, Long aiPlatformId);
    
    /**
     * 更新AI平台配置的可见性
     * 
     * @param siteId 网站ID
     * @param aiPlatformId AI平台配置ID
     * @param isVisible 可见性
     * @return 结果
     */
    int updateAiPlatformVisibility(Long siteId, Long aiPlatformId, String isVisible);
    
    /**
     * 管理默认AI平台配置的排除列表
     * 
     * @param aiPlatformId AI平台配置ID
     * @param excludedSiteIds 排除的网站ID列表
     * @return 结果
     */
    int manageDefaultAiPlatformExclusions(Long aiPlatformId, List<Long> excludedSiteIds);
    
    // ==================== 标题池关联 ====================
    
    
    // ==================== 标题池批次关联 ====================
    
    /**
     * 排除默认标题池批次
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @return 结果
     */
    int excludeDefaultBatch(Long siteId, Long batchId);
    
    /**
     * 恢复默认标题池批次
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @return 结果
     */
    int restoreDefaultBatch(Long siteId, Long batchId);
    
    /**
     * 管理默认标题池批次的排除列表
     * 
     * @param batchId 批次ID
     * @param excludedSiteIds 排除的网站ID列表
     * @return 结果
     */
    int manageDefaultBatchExclusions(Long batchId, List<Long> excludedSiteIds);
    
    /**
     * 查询默认标题池批次被哪些网站排除（返回完整站点信息）
     * 
     * @param batchId 批次ID
     * @return 网站列表（包含id、name、isExcluded字段）
     */
    List<java.util.Map<String, Object>> getExcludedSitesByBatch(Long batchId);
    
    /**
     * 更新标题池批次在网站的可见性
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @param isVisible 可见性（"1"显示，"0"隐藏）
     * @return 结果
     */
    int updateBatchVisibility(Long siteId, Long batchId, String isVisible);
    
    // ==================== 文章生成任务关联 ====================
    
    /**
     * 排除默认文章生成任务
     * 
     * @param siteId 网站ID
     * @param generationId 文章生成任务ID
     * @return 结果
     */
    int excludeDefaultGeneration(Long siteId, Long generationId);
    
    /**
     * 恢复默认文章生成任务
     * 
     * @param siteId 网站ID
     * @param generationId 文章生成任务ID
     * @return 结果
     */
    int restoreDefaultGeneration(Long siteId, Long generationId);
    
    /**
     * 管理默认文章生成任务的排除列表
     * 
     * @param generationId 文章生成任务ID
     * @param excludedSiteIds 排除的网站ID列表
     * @return 结果
     */
    int manageDefaultGenerationExclusions(Long generationId, List<Long> excludedSiteIds);
    
    /**
     * 查询默认文章生成任务被哪些网站排除
     * 
     * @param generationId 文章生成任务ID
     * @return 网站ID列表
     */
    List<Long> getExcludedSitesByGeneration(Long generationId);
    
    // ==================== 原子工具关联 ====================
    
    /**
     * 将原子工具添加到网站
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @param isVisible 可见性（"1"显示，"0"隐藏）
     * @return 结果
     */
    int addAtomicToolToSite(Long siteId, Long atomicToolId, String isVisible);
    
    /**
     * 从网站移除原子工具
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @return 结果
     */
    int removeAtomicToolFromSite(Long siteId, Long atomicToolId);
    
    /**
     * 更新原子工具在网站的可见性
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @param isVisible 可见性（"1"显示，"0"隐藏）
     * @return 结果
     */
    int updateAtomicToolVisibility(Long siteId, Long atomicToolId, String isVisible);
    
    /**
     * 排除默认原子工具
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @return 结果
     */
    int excludeDefaultAtomicTool(Long siteId, Long atomicToolId);
    
    /**
     * 恢复默认原子工具
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @return 结果
     */
    int restoreDefaultAtomicTool(Long siteId, Long atomicToolId);
    
    /**
     * 管理默认原子工具的排除列表
     * 
     * @param atomicToolId 原子工具ID
     * @param excludedSiteIds 排除的网站ID列表
     * @return 结果
     */
    int manageDefaultAtomicToolExclusions(Long atomicToolId, List<Long> excludedSiteIds);
    
    /**
     * 查询默认原子工具被哪些网站排除
     * 
     * @param atomicToolId 原子工具ID
     * @return 网站ID列表
     */
    List<Long> getExcludedSitesByAtomicTool(Long atomicToolId);
}
