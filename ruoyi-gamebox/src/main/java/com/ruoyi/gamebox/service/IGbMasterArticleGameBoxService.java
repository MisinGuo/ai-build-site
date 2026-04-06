package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBox;

/**
 * 主文章-GameBox关联Service接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface IGbMasterArticleGameBoxService 
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
     * 批量删除主文章-GameBox关联
     * 
     * @param ids 需要删除的主文章-GameBox关联主键集合
     * @return 结果
     */
    public int deleteGbMasterArticleGameBoxByIds(Long[] ids);

    /**
     * 删除主文章-GameBox关联信息
     * 
     * @param id 主文章-GameBox关联主键
     * @return 结果
     */
    public int deleteGbMasterArticleGameBoxById(Long id);

    /**
     * 检查是否已存在关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId GameBoxID
     * @return 是否存在
     */
    public boolean checkAssociationExists(Long masterArticleId, Long gameBoxId);

    /**
     * 批量添加GameBox关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxIds GameBox ID列表
     * @param relationSource 关联来源
     * @param relationType 关联类型
     * @param operatorBy 操作人
     * @return 添加成功的数量
     */
    public int batchAddGameBoxAssociations(Long masterArticleId, List<Long> gameBoxIds, 
                                          String relationSource, String relationType, String operatorBy);

    /**
     * 根据主文章ID删除所有GameBox关联
     * 
     * @param masterArticleId 主文章ID
     * @return 删除数量
     */
    public int deleteByMasterArticleId(Long masterArticleId);

    /**
     * 更新关联的审核状态
     * 
     * @param id 关联ID
     * @param reviewStatus 审核状态
     * @param reviewedBy 审核人
     * @param reviewNotes 审核备注
     * @return 结果
     */
    public int updateReviewStatus(Long id, String reviewStatus, String reviewedBy, String reviewNotes);

    /**
     * 批量审核关联
     * 
     * @param ids 关联ID列表
     * @param reviewStatus 审核状态
     * @param reviewedBy 审核人
     * @param reviewNotes 审核备注
     * @return 更新数量
     */
    public int batchReview(Long[] ids, String reviewStatus, String reviewedBy, String reviewNotes);

    /**
     * 增加关联点击次数
     * 
     * @param id 关联ID
     * @return 结果
     */
    public int incrementClickCount(Long id);

    /**
     * 设置推荐展示状态
     * 
     * @param id 关联ID
     * @param isFeatured 是否推荐
     * @param operatorBy 操作人
     * @return 结果
     */
    public int setFeaturedStatus(Long id, String isFeatured, String operatorBy);
}