package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbArticle;

/**
 * 文章管理Service接口
 * 
 * @author ruoyi
 */
public interface IGbArticleService 
{
    /**
     * 查询文章
     * 
     * @param id 文章主键
     * @return 文章
     */
    public GbArticle selectGbArticleById(Long id);

    /**
     * 查询文章列表
     * 
     * @param gbArticle 文章
     * @return 文章集合
     */
    public List<GbArticle> selectGbArticleList(GbArticle gbArticle);

    /**
     * 查询所有文章（用于ES全量重建，不受站点权限过滤）
     */
    public List<GbArticle> selectAllGbArticlesForSync();

    /**
     * 新增文章
     * 
     * @param gbArticle 文章
     * @return 结果
     */
    public int insertGbArticle(GbArticle gbArticle);

    /**
     * 修改文章
     * 
     * @param gbArticle 文章
     * @return 结果
     */
    public int updateGbArticle(GbArticle gbArticle);

    /**
     * 批量删除文章
     * 
     * @param ids 需要删除的文章主键集合
     * @return 结果
     */
    public int deleteGbArticleByIds(Long[] ids);

    /**
     * 删除文章信息
     * 
     * @param id 文章主键
     * @return 结果
     */
    public int deleteGbArticleById(Long id);

    /**
     * 通过游戏ID查询关联的文章列表
     * 
     * @param gameId 游戏ID
     * @param siteId 站点ID
     * @param locale 语言代码（可选）
     * @return 文章集合
     */
    public List<GbArticle> selectArticlesByGameId(Long gameId, Long siteId, String locale);

    /**
     * 通过盒子ID查询关联的文章列表
     * 
     * @param boxId 盒子ID
     * @param siteId 站点ID
     * @param locale 语言代码（可选）
     * @return 文章集合
     */
    public List<GbArticle> selectArticlesByBoxId(Long boxId, Long siteId, String locale);

    /**
     * 根据主文章ID和语言代码获取文章
     * 
     * @param masterArticleId 主文章ID
     * @param locale 语言代码
     * @return 文章
     */
    public GbArticle getArticleByMasterIdAndLocale(Long masterArticleId, String locale);
    
    /**
     * 发布文章到存储
     * 
     * @param id 文章ID
     * @return 操作结果
     */
    public com.ruoyi.common.core.domain.AjaxResult publishArticleToStorage(Long id);
    
    /**
     * 强制发布文章到存储（覆盖已存在文件）
     * 
     * @param id 文章ID
     * @return 操作结果
     */
    public com.ruoyi.common.core.domain.AjaxResult publishArticleToStorageForce(Long id);
    
    /**
     * 取消发布文章
     * 
     * @param id 文章ID
     * @return 操作结果
     */
    public com.ruoyi.common.core.domain.AjaxResult unpublishArticle(Long id);
    
    /**
     * 批量发布语言版本文章
     * 
     * @param articleIds 文章ID列表
     * @return 操作结果
     */
    public com.ruoyi.common.core.domain.AjaxResult batchPublishArticles(List<Long> articleIds);
    
    /**
     * 强制批量发布语言版本文章
     * 
     * @param articleIds 文章ID列表
     * @return 操作结果
     */
    public com.ruoyi.common.core.domain.AjaxResult batchPublishArticlesForce(List<Long> articleIds);
    
    /**
     * 批量撤销发布语言版本文章
     * 
     * @param articleIds 文章ID列表
     * @return 操作结果
     */
    public com.ruoyi.common.core.domain.AjaxResult batchUnpublishArticles(List<Long> articleIds);
    
    /**
     * 批量更新文章状态（只修改数据库状态）
     * 
     * @param articleIds 文章ID列表
     * @param status 状态 0-草稿 1-已发布
     * @return 操作结果
     */
    public com.ruoyi.common.core.domain.AjaxResult batchUpdateArticleStatus(List<Long> articleIds, String status);
    
    /**
     * 检查文章远程文件状态
     * 
     * @param articleId 文章ID
     * @return 检查结果
     */
    public com.ruoyi.common.core.domain.AjaxResult checkRemoteFileStatus(Long articleId);
    
    /**
     * 批量检查文章远程文件状态
     * 
     * @param articleIds 文章ID列表
     * @return 检查结果
     */
    public com.ruoyi.common.core.domain.AjaxResult batchCheckRemoteFileStatus(List<Long> articleIds);

    /**
     * 根据文章ID获取所有可用的语言版本
     * 
     * @param id 文章ID
     * @return 语言代码列表
     */
    public List<String> getArticleLocales(Long id);
}
