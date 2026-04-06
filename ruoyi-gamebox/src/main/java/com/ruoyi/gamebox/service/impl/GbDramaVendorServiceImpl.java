package com.ruoyi.gamebox.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.gamebox.mapper.GbDramaVendorMapper;
import com.ruoyi.gamebox.domain.GbDramaVendor;
import com.ruoyi.gamebox.service.IGbDramaVendorService;

/**
 * 短剧供应商Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbDramaVendorServiceImpl implements IGbDramaVendorService 
{
    @Autowired
    private GbDramaVendorMapper gbDramaVendorMapper;

    /**
     * 查询短剧供应商
     * 
     * @param id 供应商主键
     * @return 短剧供应商
     */
    @Override
    public GbDramaVendor selectGbDramaVendorById(Long id)
    {
        return gbDramaVendorMapper.selectGbDramaVendorById(id);
    }

    /**
     * 查询短剧供应商列表
     * 
     * @param gbDramaVendor 短剧供应商
     * @return 短剧供应商
     */
    @Override
    public List<GbDramaVendor> selectGbDramaVendorList(GbDramaVendor gbDramaVendor)
    {
        return gbDramaVendorMapper.selectGbDramaVendorList(gbDramaVendor);
    }

    /**
     * 新增短剧供应商
     * 
     * @param gbDramaVendor 短剧供应商
     * @return 结果
     */
    @Override
    public int insertGbDramaVendor(GbDramaVendor gbDramaVendor)
    {
        gbDramaVendor.setCreateTime(DateUtils.getNowDate());
        return gbDramaVendorMapper.insertGbDramaVendor(gbDramaVendor);
    }

    /**
     * 修改短剧供应商
     * 
     * @param gbDramaVendor 短剧供应商
     * @return 结果
     */
    @Override
    public int updateGbDramaVendor(GbDramaVendor gbDramaVendor)
    {
        gbDramaVendor.setUpdateTime(DateUtils.getNowDate());
        return gbDramaVendorMapper.updateGbDramaVendor(gbDramaVendor);
    }

    /**
     * 批量删除短剧供应商
     * 
     * @param ids 需要删除的供应商主键
     * @return 结果
     */
    @Override
    public int deleteGbDramaVendorByIds(Long[] ids)
    {
        return gbDramaVendorMapper.deleteGbDramaVendorByIds(ids);
    }

    /**
     * 删除短剧供应商信息
     * 
     * @param id 供应商主键
     * @return 结果
     */
    @Override
    public int deleteGbDramaVendorById(Long id)
    {
        return gbDramaVendorMapper.deleteGbDramaVendorById(id);
    }
}
