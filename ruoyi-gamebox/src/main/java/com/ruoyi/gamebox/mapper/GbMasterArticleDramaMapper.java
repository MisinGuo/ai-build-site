package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleDrama;

/**
 * 主文章-剧集关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface GbMasterArticleDramaMapper 
{
    /**
     * 查询主文章-剧集关联
     * 
     * @param id 主文章-剧集关联主键
     * @return 主文章-剧集关联
     */
    public GbMasterArticleDrama selectGbMasterArticleDramaById(Long id);

    /**
     * 查询主文章-剧集关联列表
     * 
     * @param gbMasterArticleDrama 主文章-剧集关联
     * @return 主文章-剧集关联集合
     */
    public List<GbMasterArticleDrama> selectGbMasterArticleDramaList(GbMasterArticleDrama gbMasterArticleDrama);

    /**
     * 根据主文章ID查询剧集关联列表
     * 
     * @param masterArticleId 主文章ID
     * @return 主文章-剧集关联集合
     */
    public List<GbMasterArticleDrama> selectGbMasterArticleDramaByMasterArticleId(Long masterArticleId);

    /**
     * 新增主文章-剧集关联
     * 
     * @param gbMasterArticleDrama 主文章-剧集关联
     * @return 结果
     */
    public int insertGbMasterArticleDrama(GbMasterArticleDrama gbMasterArticleDrama);

    /**
     * 修改主文章-剧集关联
     * 
     * @param gbMasterArticleDrama 主文章-剧集关联
     * @return 结果
     */
    public int updateGbMasterArticleDrama(GbMasterArticleDrama gbMasterArticleDrama);

    /**
     * 删除主文章-剧集关联
     * 
     * @param id 主文章-剧集关联主键
     * @return 结果
     */
    public int deleteGbMasterArticleDramaById(Long id);

    /**
     * 批量删除主文章-剧集关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbMasterArticleDramaByIds(Long[] ids);

    /**
     * 根据主文章ID删除关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteGbMasterArticleDramaByMasterArticleId(Long masterArticleId);

    /**
     * 检查是否已存在关联
     * 
     * @param masterArticleId 主文章ID
     * @param dramaId 剧集ID
     * @return 是否存在
     */
    public boolean checkAssociationExists(Long masterArticleId, Long dramaId);

    /**
     * 批量插入关联
     * 
     * @param associations 关联列表
     * @return 结果
     */
    public int batchInsertGbMasterArticleDrama(List<GbMasterArticleDrama> associations);
}