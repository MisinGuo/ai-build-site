package com.ruoyi.gamebox.service.impl;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBox;
import com.ruoyi.gamebox.mapper.GbMasterArticleGameBoxMapper;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxService;

/**
 * 主文章-GameBox关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
@Service
public class GbMasterArticleGameBoxServiceImpl implements IGbMasterArticleGameBoxService 
{
    @Autowired
    private GbMasterArticleGameBoxMapper gbMasterArticleGameBoxMapper;

    /**
     * 查询主文章-GameBox关联
     * 
     * @param id 主文章-GameBox关联主键
     * @return 主文章-GameBox关联
     */
    @Override
    public GbMasterArticleGameBox selectGbMasterArticleGameBoxById(Long id)
    {
        return gbMasterArticleGameBoxMapper.selectGbMasterArticleGameBoxById(id);
    }

    /**
     * 查询主文章-GameBox关联列表
     * 
     * @param gbMasterArticleGameBox 主文章-GameBox关联
     * @return 主文章-GameBox关联
     */
    @Override
    public List<GbMasterArticleGameBox> selectGbMasterArticleGameBoxList(GbMasterArticleGameBox gbMasterArticleGameBox)
    {
        return gbMasterArticleGameBoxMapper.selectGbMasterArticleGameBoxList(gbMasterArticleGameBox);
    }

    /**
     * 根据主文章ID查询GameBox关联列表
     * 
     * @param masterArticleId 主文章ID
     * @return 主文章-GameBox关联集合
     */
    @Override
    public List<GbMasterArticleGameBox> selectGbMasterArticleGameBoxByMasterArticleId(Long masterArticleId)
    {
        return gbMasterArticleGameBoxMapper.selectGbMasterArticleGameBoxByMasterArticleId(masterArticleId);
    }

    /**
     * 新增主文章-GameBox关联
     * 
     * @param gbMasterArticleGameBox 主文章-GameBox关联
     * @return 结果
     */
    @Override
    @Transactional
    public int insertGbMasterArticleGameBox(GbMasterArticleGameBox gbMasterArticleGameBox)
    {
        // 检查是否已存在关联
        if (checkAssociationExists(gbMasterArticleGameBox.getMasterArticleId(), gbMasterArticleGameBox.getGameBoxId())) {
            return 0; // 已存在，不重复添加
        }

        gbMasterArticleGameBox.setCreateTime(DateUtils.getNowDate());
        gbMasterArticleGameBox.setCreateBy(SecurityUtils.getUsername());
        gbMasterArticleGameBox.setDelFlag("0");
        
        // 设置默认值
        if (StringUtils.isNull(gbMasterArticleGameBox.getReviewStatus())) {
            gbMasterArticleGameBox.setReviewStatus("0"); // 默认待审核
        }
        if (StringUtils.isNull(gbMasterArticleGameBox.getIsFeatured())) {
            gbMasterArticleGameBox.setIsFeatured("0");
        }
        if (StringUtils.isNull(gbMasterArticleGameBox.getClickCount())) {
            gbMasterArticleGameBox.setClickCount(0);
        }
        if (StringUtils.isNull(gbMasterArticleGameBox.getDisplayOrder())) {
            gbMasterArticleGameBox.setDisplayOrder(0);
        }

        return gbMasterArticleGameBoxMapper.insertGbMasterArticleGameBox(gbMasterArticleGameBox);
    }

    /**
     * 修改主文章-GameBox关联
     * 
     * @param gbMasterArticleGameBox 主文章-GameBox关联
     * @return 结果
     */
    @Override
    @Transactional
    public int updateGbMasterArticleGameBox(GbMasterArticleGameBox gbMasterArticleGameBox)
    {
        gbMasterArticleGameBox.setUpdateTime(DateUtils.getNowDate());
        gbMasterArticleGameBox.setUpdateBy(SecurityUtils.getUsername());
        return gbMasterArticleGameBoxMapper.updateGbMasterArticleGameBox(gbMasterArticleGameBox);
    }

    /**
     * 批量删除主文章-GameBox关联
     * 
     * @param ids 需要删除的主文章-GameBox关联主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbMasterArticleGameBoxByIds(Long[] ids)
    {
        return gbMasterArticleGameBoxMapper.deleteGbMasterArticleGameBoxByIds(ids);
    }

    /**
     * 删除主文章-GameBox关联信息
     * 
     * @param id 主文章-GameBox关联主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbMasterArticleGameBoxById(Long id)
    {
        return gbMasterArticleGameBoxMapper.deleteGbMasterArticleGameBoxById(id);
    }

    /**
     * 检查是否已存在关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId GameBoxID
     * @return 是否存在
     */
    @Override
    public boolean checkAssociationExists(Long masterArticleId, Long gameBoxId)
    {
        return gbMasterArticleGameBoxMapper.checkAssociationExists(masterArticleId, gameBoxId);
    }

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
    @Override
    @Transactional
    public int batchAddGameBoxAssociations(Long masterArticleId, List<Long> gameBoxIds, 
                                          String relationSource, String relationType, String operatorBy)
    {
        if (gameBoxIds == null || gameBoxIds.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        Date currentTime = DateUtils.getNowDate();
        
        for (int i = 0; i < gameBoxIds.size(); i++) {
            Long gameBoxId = gameBoxIds.get(i);
            
            // 检查是否已存在
            if (checkAssociationExists(masterArticleId, gameBoxId)) {
                continue; // 跳过已存在的关联
            }

            GbMasterArticleGameBox association = new GbMasterArticleGameBox();
            association.setMasterArticleId(masterArticleId);
            association.setGameBoxId(gameBoxId);
            association.setRelationSource(relationSource);
            association.setRelationType(relationType);
            association.setDisplayOrder(i + 1);
            association.setReviewStatus("0"); // 待审核
            association.setIsFeatured("0");
            association.setClickCount(0);
            association.setCreateBy(operatorBy);
            association.setCreateTime(currentTime);
            association.setDelFlag("0");

            try {
                int result = gbMasterArticleGameBoxMapper.insertGbMasterArticleGameBox(association);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录日志但继续处理其他关联
                continue;
            }
        }

        return successCount;
    }

    /**
     * 根据主文章ID删除所有GameBox关联
     * 
     * @param masterArticleId 主文章ID
     * @return 删除数量
     */
    @Override
    @Transactional
    public int deleteByMasterArticleId(Long masterArticleId)
    {
        return gbMasterArticleGameBoxMapper.deleteGbMasterArticleGameBoxByMasterArticleId(masterArticleId);
    }

    /**
     * 更新关联的审核状态
     * 
     * @param id 关联ID
     * @param reviewStatus 审核状态
     * @param reviewedBy 审核人
     * @param reviewNotes 审核备注
     * @return 结果
     */
    @Override
    @Transactional
    public int updateReviewStatus(Long id, String reviewStatus, String reviewedBy, String reviewNotes)
    {
        GbMasterArticleGameBox association = new GbMasterArticleGameBox();
        association.setId(id);
        association.setReviewStatus(reviewStatus);
        association.setReviewedBy(reviewedBy);
        association.setReviewedAt(DateUtils.getNowDate());
        association.setReviewNotes(reviewNotes);
        association.setUpdateBy(SecurityUtils.getUsername());
        association.setUpdateTime(DateUtils.getNowDate());
        
        return gbMasterArticleGameBoxMapper.updateGbMasterArticleGameBox(association);
    }

    /**
     * 批量审核关联
     * 
     * @param ids 关联ID列表
     * @param reviewStatus 审核状态
     * @param reviewedBy 审核人
     * @param reviewNotes 审核备注
     * @return 更新数量
     */
    @Override
    @Transactional
    public int batchReview(Long[] ids, String reviewStatus, String reviewedBy, String reviewNotes)
    {
        int updateCount = 0;
        Date currentTime = DateUtils.getNowDate();
        String currentUser = SecurityUtils.getUsername();

        for (Long id : ids) {
            GbMasterArticleGameBox association = new GbMasterArticleGameBox();
            association.setId(id);
            association.setReviewStatus(reviewStatus);
            association.setReviewedBy(reviewedBy);
            association.setReviewedAt(currentTime);
            association.setReviewNotes(reviewNotes);
            association.setUpdateBy(currentUser);
            association.setUpdateTime(currentTime);
            
            int result = gbMasterArticleGameBoxMapper.updateGbMasterArticleGameBox(association);
            if (result > 0) {
                updateCount++;
            }
        }

        return updateCount;
    }

    /**
     * 增加关联点击次数
     * 
     * @param id 关联ID
     * @return 结果
     */
    @Override
    @Transactional
    public int incrementClickCount(Long id)
    {
        GbMasterArticleGameBox existing = gbMasterArticleGameBoxMapper.selectGbMasterArticleGameBoxById(id);
        if (existing != null) {
            existing.setClickCount(existing.getClickCount() + 1);
            existing.setLastUsedAt(DateUtils.getNowDate());
            existing.setUpdateBy(SecurityUtils.getUsername());
            existing.setUpdateTime(DateUtils.getNowDate());
            
            return gbMasterArticleGameBoxMapper.updateGbMasterArticleGameBox(existing);
        }
        return 0;
    }

    /**
     * 设置推荐展示状态
     * 
     * @param id 关联ID
     * @param isFeatured 是否推荐
     * @param operatorBy 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int setFeaturedStatus(Long id, String isFeatured, String operatorBy)
    {
        GbMasterArticleGameBox association = new GbMasterArticleGameBox();
        association.setId(id);
        association.setIsFeatured(isFeatured);
        association.setUpdateBy(operatorBy);
        association.setUpdateTime(DateUtils.getNowDate());
        
        return gbMasterArticleGameBoxMapper.updateGbMasterArticleGameBox(association);
    }
}