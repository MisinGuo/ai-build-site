package com.ruoyi.gamebox.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.gamebox.mapper.GbDramaMapper;
import com.ruoyi.gamebox.domain.GbDrama;
import com.ruoyi.gamebox.service.IGbDramaService;

/**
 * 短剧管理Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbDramaServiceImpl implements IGbDramaService 
{
    @Autowired
    private GbDramaMapper gbDramaMapper;

    /**
     * 查询短剧
     * 
     * @param id 短剧主键
     * @return 短剧
     */
    @Override
    public GbDrama selectGbDramaById(Long id)
    {
        return gbDramaMapper.selectGbDramaById(id);
    }

    /**
     * 查询短剧列表
     * 
     * @param gbDrama 短剧
     * @return 短剧
     */
    @Override
    public List<GbDrama> selectGbDramaList(GbDrama gbDrama)
    {
        return gbDramaMapper.selectGbDramaList(gbDrama);
    }

    /**
     * 新增短剧
     * 
     * @param gbDrama 短剧
     * @return 结果
     */
    @Override
    public int insertGbDrama(GbDrama gbDrama)
    {
        gbDrama.setCreateTime(DateUtils.getNowDate());
        return gbDramaMapper.insertGbDrama(gbDrama);
    }

    /**
     * 修改短剧
     * 
     * @param gbDrama 短剧
     * @return 结果
     */
    @Override
    public int updateGbDrama(GbDrama gbDrama)
    {
        gbDrama.setUpdateTime(DateUtils.getNowDate());
        return gbDramaMapper.updateGbDrama(gbDrama);
    }

    /**
     * 批量删除短剧
     * 
     * @param ids 需要删除的短剧主键
     * @return 结果
     */
    @Override
    public int deleteGbDramaByIds(Long[] ids)
    {
        return gbDramaMapper.deleteGbDramaByIds(ids);
    }

    /**
     * 删除短剧信息
     * 
     * @param id 短剧主键
     * @return 结果
     */
    @Override
    public int deleteGbDramaById(Long id)
    {
        return gbDramaMapper.deleteGbDramaById(id);
    }
}
