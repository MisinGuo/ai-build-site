package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbArticle;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章管理Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbArticleMapper 
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
     * 更新文章发布状态
     * 
     * @param id 文章ID
     * @param status 发布状态
     * @param storageKey 存储键
     * @param storageUrl 存储URL
     * @param pathRule 路径规则
     * @param publishTime 发布时间
     * @return 结果
     */
    public int updateArticlePublishStatus(@org.apache.ibatis.annotations.Param("id") Long id, 
                                          @org.apache.ibatis.annotations.Param("status") String status, 
                                          @org.apache.ibatis.annotations.Param("storageKey") String storageKey, 
                                          @org.apache.ibatis.annotations.Param("storageUrl") String storageUrl, 
                                          @org.apache.ibatis.annotations.Param("pathRule") String pathRule, 
                                          @org.apache.ibatis.annotations.Param("publishTime") java.util.Date publishTime);

    /**
     * 删除文章
     * 
     * @param id 文章主键
     * @return 结果
     */
    public int deleteGbArticleById(Long id);

    /**
     * 批量删除文章
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbArticleByIds(Long[] ids);

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
    public GbArticle getArticleByMasterIdAndLocale(@org.apache.ibatis.annotations.Param("masterArticleId") Long masterArticleId, 
                                                    @org.apache.ibatis.annotations.Param("locale") String locale);

    /**
     * 根据主文章ID获取所有关联的文章
     * 
     * @param masterArticleId 主文章ID
     * @return 文章集合
     */
    public List<GbArticle> selectArticlesByMasterArticleId(@org.apache.ibatis.annotations.Param("masterArticleId") Long masterArticleId);

    /**
     * 根据主文章ID软删除所有关联的文章
     * 
     * @param masterArticleId 主文章ID
     * @return 影响的行数
     */
    public int softDeleteByMasterArticleId(@org.apache.ibatis.annotations.Param("masterArticleId") Long masterArticleId);

    /**
     * 根据主文章ID查询所有关联的语言版本
     * 
     * @param masterArticleId 主文章ID
     * @return 语言代码列表
     */
    public List<String> selectArticleLocales(@org.apache.ibatis.annotations.Param("masterArticleId") Long masterArticleId);
}
