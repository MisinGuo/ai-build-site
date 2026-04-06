package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbAtomicTool;
import java.util.List;

/**
 * 原子工具Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-04
 */
public interface GbAtomicToolMapper
{
    /**
     * 查询原子工具
     * 
     * @param id 原子工具主键
     * @return 原子工具
     */
    public GbAtomicTool selectGbAtomicToolById(Long id);

    /**
     * 查询原子工具列表
     * 
     * @param gbAtomicTool 原子工具
     * @return 原子工具集合
     */
    public List<GbAtomicTool> selectGbAtomicToolList(GbAtomicTool gbAtomicTool);

    /**
     * 新增原子工具
     * 
     * @param gbAtomicTool 原子工具
     * @return 结果
     */
    public int insertGbAtomicTool(GbAtomicTool gbAtomicTool);

    /**
     * 修改原子工具
     * 
     * @param gbAtomicTool 原子工具
     * @return 结果
     */
    public int updateGbAtomicTool(GbAtomicTool gbAtomicTool);

    /**
     * 删除原子工具
     * 
     * @param id 原子工具主键
     * @return 结果
     */
    public int deleteGbAtomicToolById(Long id);

    /**
     * 批量删除原子工具
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbAtomicToolByIds(Long[] ids);
}
