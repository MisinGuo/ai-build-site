package com.ruoyi.gamebox.service;

/**
 * 模板变量Service接口
 * 
 * @author ruoyi
 */
public interface ITemplateVariableService
{
    /**
     * 替换文章内容中的模板变量（从关联表查询）
     * 
     * @param content 原始内容
     * @param siteId 网站ID（必需，一篇文章只能对应一个网站）
     * @param articleId 文章ID（用于从关联表查询游戏和游戏盒子）
     * @return 替换后的内容
     */
    String replaceVariables(String content, Long siteId, Long articleId);
    
    /**
     * 替换文章内容中的模板变量（直接使用ID列表）
     * 
     * @param content 原始内容
     * @param siteId 网站ID（必需）
     * @param gameBoxIds 游戏盒子ID列表
     * @param gameIds 游戏ID列表
     * @return 替换后的内容
     */
    String replaceVariables(String content, Long siteId, java.util.List<Long> gameBoxIds, java.util.List<Long> gameIds);
}
