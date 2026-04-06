package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBox;

/**
 * 主文章-GameBox关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface GbMasterArticleGameBoxMapper 
{
    /**
     * 查询主文章-GameBox关联
     * 
     * @param id 主文章-GameBox关联主键
     * @return 主文章-GameBox关联
     */
    public GbMasterArticleGameBox selectGbMasterArticleGameBoxById(Long id);

    /**
     * 查询主文章-GameBox关联列表
     * 
     * @param gbMasterArticleGameBox 主文章-GameBox关联
     * @return 主文章-GameBox关联集合
     */
    public List<GbMasterArticleGameBox> selectGbMasterArticleGameBoxList(GbMasterArticleGameBox gbMasterArticleGameBox);

    /**
     * 根据主文章ID查询GameBox关联列表
     * 
     * @param masterArticleId 主文章ID
     * @return 主文章-GameBox关联集合
     */
    public List<GbMasterArticleGameBox> selectGbMasterArticleGameBoxByMasterArticleId(Long masterArticleId);

    /**
     * 新增主文章-GameBox关联
     * 
     * @param gbMasterArticleGameBox 主文章-GameBox关联
     * @return 结果
     */
    public int insertGbMasterArticleGameBox(GbMasterArticleGameBox gbMasterArticleGameBox);

    /**
     * 修改主文章-GameBox关联
     * 
     * @param gbMasterArticleGameBox 主文章-GameBox关联
     * @return 结果
     */
    public int updateGbMasterArticleGameBox(GbMasterArticleGameBox gbMasterArticleGameBox);

    /**
     * 删除主文章-GameBox关联
     * 
     * @param id 主文章-GameBox关联主键
     * @return 结果
     */
    public int deleteGbMasterArticleGameBoxById(Long id);

    /**
     * 批量删除主文章-GameBox关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbMasterArticleGameBoxByIds(Long[] ids);

    /**
     * 根据主文章ID删除关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteGbMasterArticleGameBoxByMasterArticleId(Long masterArticleId);

    /**
     * 检查是否已存在关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId GameBoxID
     * @return 是否存在
     */
    public boolean checkAssociationExists(Long masterArticleId, Long gameBoxId);

    /**
     * 批量插入关联
     * 
     * @param associations 关联列表
     * @return 结果
     */
    public int batchInsertGbMasterArticleGameBox(List<GbMasterArticleGameBox> associations);
}