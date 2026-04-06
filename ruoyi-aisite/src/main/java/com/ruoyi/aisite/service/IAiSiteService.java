package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiSite;

/**
 * 站点 Service 接口
 */
public interface IAiSiteService
{
    AiSite selectAiSiteById(Long id);

    AiSite selectAiSiteByDomain(String domain);

    List<AiSite> selectAiSiteList(AiSite aiSite);

    int insertAiSite(AiSite aiSite);

    int updateAiSite(AiSite aiSite);

    int deleteAiSiteById(Long id);

    int deleteAiSiteByIds(Long[] ids);
}
