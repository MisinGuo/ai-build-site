package com.ruoyi.gamebox.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.gamebox.domain.GbSiteAtomicToolRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-原子工具关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-01-08
 */
@Mapper
public interface GbSiteAtomicToolRelationMapper 
{
    /**
     * 查询网站的所有原子工具（带工具详情）
     * 
     * @param siteId 网站ID
     * @return 原子工具关联列表
     */
    public List<GbSiteAtomicToolRelation> selectAtomicToolsBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询原子工具在哪些网站可用
     * 
     * @param atomicToolId 原子工具ID
     * @return 网站关联列表
     */
    public List<GbSiteAtomicToolRelation> selectSitesByAtomicToolId(@Param("atomicToolId") Long atomicToolId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("atomicToolId") Long atomicToolId);
    
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("atomicToolId") Long atomicToolId);

    /**
     * 新增网站-原子工具关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int insertSiteAtomicToolRelation(GbSiteAtomicToolRelation relation);

    /**
     * 更新网站-原子工具关联
     * 
     * @param relation 关联对象
     * @return 结果
     */
    public int updateGbSiteAtomicToolRelation(GbSiteAtomicToolRelation relation);

    /**
     * 批量新增网站-原子工具关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertSiteAtomicToolRelations(@Param("relations") List<GbSiteAtomicToolRelation> relations);

    /**
     * 删除网站-原子工具关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param atomicToolId 原子工具ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteGbSiteAtomicToolRelation(@Param("siteId") Long siteId, 
                                              @Param("atomicToolId") Long atomicToolId, 
                                              @Param("relationType") String relationType);
    
    /**
     * 更新原子工具可见性
     * 
     * @param params 包含siteId, atomicToolId, isVisible等参数
     * @return 结果
     */
    public int updateSiteAtomicToolVisibility(Map<String, Object> params);
    
    /**
     * 查询默认原子工具被哪些网站排除（按关联类型）
     * 
     * @param atomicToolId 原子工具ID
     * @param relationType 关联类型
     * @return 被排除的网站ID列表
     */
    public List<Long> selectExcludedSiteIdsByRelationType(@Param("atomicToolId") Long atomicToolId, 
                                                          @Param("relationType") String relationType);

    /**
     * 批量删除：对给定 atomicToolIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    public void deleteByAtomicToolIdsAndTypeExcluding(@Param("atomicToolIds") List<Long> atomicToolIds,
                                                       @Param("relationType") String relationType,
                                                       @Param("keepSiteIds") List<Long> keepSiteIds);
}
