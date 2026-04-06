package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbDramaVendor;

/**
 * 短剧供应商Service接口
 * 
 * @author ruoyi
 */
public interface IGbDramaVendorService 
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
     * 批量删除短剧供应商
     * 
     * @param ids 需要删除的供应商主键集合
     * @return 结果
     */
    public int deleteGbDramaVendorByIds(Long[] ids);

    /**
     * 删除短剧供应商信息
     * 
     * @param id 供应商主键
     * @return 结果
     */
    public int deleteGbDramaVendorById(Long id);
}
