package com.ruoyi.gamebox.service;

/**
 * 批量文章生成服务接口
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
public interface IArticleBatchGenerationService 
{
    /**
     * 执行批量生成任务
     * 
     * @param taskId 生成任务ID
     * @return 生成结果摘要
     */
    String executeBatchGeneration(Long taskId);

    /**
     * 关联游戏到文章
     * 
     * @param articleId 文章ID
     * @param gameIds 游戏ID列表(逗号分隔)
     * @param gameBoxIds 游戏盒子ID列表(逗号分隔)
     */
    void bindGamesToArticle(Long articleId, String gameIds, String gameBoxIds);

    /**
     * 发布文章
     * 
     * @param articleId 文章ID
     * @param autoPublish 是否自动发布
     * @return 是否成功
     */
    boolean publishArticle(Long articleId, boolean autoPublish);
}
