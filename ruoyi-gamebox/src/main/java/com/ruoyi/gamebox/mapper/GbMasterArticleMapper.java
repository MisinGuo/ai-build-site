package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticle;

/**
 * 主文章内容Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface GbMasterArticleMapper 
{
    /**
     * 查询主文章内容
     * 
     * @param id 主文章内容主键
     * @return 主文章内容
     */
    public GbMasterArticle selectGbMasterArticleById(Long id);

    /**
     * 查询主文章内容列表
     * 
     * @param gbMasterArticle 主文章内容
     * @return 主文章内容集合
     */
    public List<GbMasterArticle> selectGbMasterArticleList(GbMasterArticle gbMasterArticle);

    /**
     * 新增主文章内容
     * 
     * @param gbMasterArticle 主文章内容
     * @return 结果
     */
    public int insertGbMasterArticle(GbMasterArticle gbMasterArticle);

    /**
     * 修改主文章内容
     * 
     * @param gbMasterArticle 主文章内容
     * @return 结果
     */
    public int updateGbMasterArticle(GbMasterArticle gbMasterArticle);

    /**
     * 删除主文章内容
     * 
     * @param id 主文章内容主键
     * @return 结果
     */
    public int deleteGbMasterArticleById(Long id);

    /**
     * 批量删除主文章内容
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbMasterArticleByIds(Long[] ids);

    // 注释：content_key字段已从表中移除
    /**
     * 根据内容标识查询主文章
     * 
     * @param contentKey 内容标识
     * @return 主文章内容
     */
    // public GbMasterArticle selectGbMasterArticleByContentKey(String contentKey);

    /**
     * 查询主文章详情（包含所有关联信息）
     * 
     * @param id 主文章内容主键
     * @return 主文章内容
     */
    public GbMasterArticle selectGbMasterArticleWithAssociations(Long id);

    /**
     * 根据主文章ID查询语言版本文章数量
     * 
     * @param masterArticleId 主文章ID
     * @return 语言版本数量
     */
    public int selectLanguageVersionCount(Long masterArticleId);

    // 注释：content_key字段已从表中移除
    /**
     * 检查内容标识是否已存在
     * 
     * @param contentKey 内容标识
     * @param excludeId 排除的ID(用于更新时)
     * @return 是否存在
     */
    // public boolean checkContentKeyExists(String contentKey, Long excludeId);
}