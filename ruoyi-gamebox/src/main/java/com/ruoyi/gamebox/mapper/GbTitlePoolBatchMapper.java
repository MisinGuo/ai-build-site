package com.ruoyi.gamebox.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.gamebox.domain.GbTitlePoolBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 标题池批次Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-01
 */
@Mapper
public interface GbTitlePoolBatchMapper 
{
    /**
     * 查询标题池批次列表
     * 
     * @param batch 标题池批次
     * @return 标题池批次集合
     */
    public List<GbTitlePoolBatch> selectGbTitlePoolBatchList(GbTitlePoolBatch batch);

    /**
     * 查询标题池批次
     * 
     * @param id 标题池批次主键
     * @return 标题池批次
     */
    public GbTitlePoolBatch selectGbTitlePoolBatchById(Long id);

    /**
     * 根据批次号查询批次
     * 
     * @param batchCode 批次号
     * @return 标题池批次
     */
    public GbTitlePoolBatch selectGbTitlePoolBatchByCode(@Param("batchCode") String batchCode);

    /**
     * 新增标题池批次
     * 
     * @param batch 标题池批次
     * @return 结果
     */
    public int insertGbTitlePoolBatch(GbTitlePoolBatch batch);

    /**
     * 修改标题池批次
     * 
     * @param batch 标题池批次
     * @return 结果
     */
    public int updateGbTitlePoolBatch(GbTitlePoolBatch batch);

    /**
     * 删除标题池批次
     * 
     * @param id 标题池批次主键
     * @return 结果
     */
    public int deleteGbTitlePoolBatchById(Long id);

    /**
     * 批量删除标题池批次
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbTitlePoolBatchByIds(Long[] ids);

    /**
     * 逻辑删除批次关联的标题
     * 
     * @param id 批次ID
     * @return 结果
     */
    public int markTitlesAsDeletedByBatchId(Long id);

    /**
     * 批量逻辑删除批次关联的标题
     * 
     * @param ids 批次ID集合
     * @return 结果
     */
    public int markTitlesAsDeletedByBatchIds(Long[] ids);

    /**
     * 生成新的批次号
     * 
     * @param siteId 网站ID
     * @param importDate 导入日期
     * @return 批次号
     */
    public String generateBatchCode(@Param("siteId") Long siteId, @Param("importDate") String importDate);

    /**
     * 更新批次的标题数量
     * 
     * @param batchCode 批次号
     * @return 结果
     */
    public int updateBatchTitleCount(@Param("batchCode") String batchCode);

    /**
     * 获取批次关联的网站列表
     * 
     * @param batchId 批次ID
     * @return 网站列表
     */
    public List<Map<String, Object>> getBatchSites(@Param("batchId") Long batchId);

    /**
     * 删除批次的所有网站关联关系
     * 
     * @param batchId 批次ID
     * @return 结果
     */
    public int deleteBatchRelations(@Param("batchId") Long batchId);

    /**
     * 批量插入批次与网站的关联关系
     * 
     * @param batchId 批次ID
     * @param siteIds 网站ID列表
     * @param isVisible 是否可见
     * @return 结果
     */
    public int batchInsertBatchRelations(@Param("batchId") Long batchId, 
                                         @Param("siteIds") List<Long> siteIds, 
                                         @Param("isVisible") Integer isVisible);

    /**
     * 删除批次与指定网站的关联关系
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @return 结果
     */
    public int deleteBatchSiteRelation(@Param("siteId") Long siteId, @Param("batchId") Long batchId);

    /**
     * 更新批次与网站关联的可见性
     * 
     * @param siteId 网站ID
     * @param batchId 批次ID
     * @param isVisible 是否可见
     * @return 结果
     */
    public int updateBatchRelationVisibility(@Param("siteId") Long siteId, 
                                             @Param("batchId") Long batchId, 
                                             @Param("isVisible") Integer isVisible);
}
