package com.ruoyi.gamebox.support;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelatedModeSiteValidator
{
    @Autowired
    private GbSiteMapper gbSiteMapper;

    public void validate(String queryMode, Long siteId)
    {
        if (!"related".equalsIgnoreCase(queryMode) || siteId == null)
        {
            return;
        }

        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        if (site != null && Integer.valueOf(1).equals(site.getIsPersonal()))
        {
            throw new ServiceException("站点ID " + siteId + " 在关联模式下不可用，请选择真实网站ID后重试");
        }
    }
}