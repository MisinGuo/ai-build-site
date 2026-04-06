package com.ruoyi.gamebox.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.gamebox.domain.GbSiteTemplate;
import com.ruoyi.gamebox.mapper.GbSiteTemplateMapper;
import com.ruoyi.gamebox.service.IGbSiteTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 站点模板 Service 业务层实现
 */
@Service
public class GbSiteTemplateServiceImpl implements IGbSiteTemplateService {

    @Autowired
    private GbSiteTemplateMapper templateMapper;

    @Override
    public List<GbSiteTemplate> selectTemplateList(GbSiteTemplate query) {
        return templateMapper.selectTemplateList(query);
    }

    @Override
    public GbSiteTemplate selectTemplateById(Long id) {
        return templateMapper.selectTemplateById(id);
    }

    @Override
    public int insertTemplate(GbSiteTemplate template) {
        template.setCreateTime(DateUtils.getNowDate());
        return templateMapper.insertTemplate(template);
    }

    @Override
    public int updateTemplate(GbSiteTemplate template) {
        template.setUpdateTime(DateUtils.getNowDate());
        return templateMapper.updateTemplate(template);
    }

    @Override
    public int deleteTemplateByIds(Long[] ids) {
        return templateMapper.deleteTemplateByIds(ids);
    }
}
