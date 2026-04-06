package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbDramaVendor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短剧供应商Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbDramaVendorMapper 
{
    /**
     * 查询短剧供应商
     * 
     * @param id 供应商主键
     * @return 短剧供应商
     */
    public GbDramaVendor selectGbDramaVendorById(Long id);

    /**
     * 查询短剧供应商列表
     * 
     * @param gbDramaVendor 短剧供应商
     * @return 短剧供应商集合
     */
    public List<GbDramaVendor> selectGbDramaVendorList(GbDramaVendor gbDramaVendor);

    /**
     * 新增短剧供应商
     * 
     * @param gbDramaVendor 短剧供应商
     * @return 结果
     */
    public int insertGbDramaVendor(GbDramaVendor gbDramaVendor);

    /**
     * 修改短剧供应商
     * 
     * @param gbDramaVendor 短剧供应商
     * @return 结果
     */
    public int updateGbDramaVendor(GbDramaVendor gbDramaVendor);

    /**
     * 删除短剧供应商
     * 
     * @param id 供应商主键
     * @return 结果
     */
    public int deleteGbDramaVendorById(Long id);

    /**
     * 批量删除短剧供应商
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbDramaVendorByIds(Long[] ids);
}
