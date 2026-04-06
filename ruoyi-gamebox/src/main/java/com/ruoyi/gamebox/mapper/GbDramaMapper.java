package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbDrama;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短剧管理Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbDramaMapper 
{
    /**
     * 查询短剧
     * 
     * @param id 短剧主键
     * @return 短剧
     */
    public GbDrama selectGbDramaById(Long id);

    /**
     * 查询短剧列表
     * 
     * @param gbDrama 短剧
     * @return 短剧集合
     */
    public List<GbDrama> selectGbDramaList(GbDrama gbDrama);

    /**
     * 新增短剧
     * 
     * @param gbDrama 短剧
     * @return 结果
     */
    public int insertGbDrama(GbDrama gbDrama);

    /**
     * 修改短剧
     * 
     * @param gbDrama 短剧
     * @return 结果
     */
    public int updateGbDrama(GbDrama gbDrama);

    /**
     * 删除短剧
     * 
     * @param id 短剧主键
     * @return 结果
     */
    public int deleteGbDramaById(Long id);

    /**
     * 批量删除短剧
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbDramaByIds(Long[] ids);
}
