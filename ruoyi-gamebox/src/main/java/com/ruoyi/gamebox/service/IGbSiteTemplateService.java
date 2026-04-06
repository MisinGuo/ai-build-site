package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbSiteTemplate;
import java.util.List;

/**
 * 站点模板 Service 接口
 */
public interface IGbSiteTemplateService {

    /**
     * 查询模板列表（可按 siteFunctionType / industry / status 过滤）
     */
    List<GbSiteTemplate> selectTemplateList(GbSiteTemplate query);

    /**
     * 查询单个模板
     */
    GbSiteTemplate selectTemplateById(Long id);

    /**
     * 新增模板
     */
    int insertTemplate(GbSiteTemplate template);

    /**
     * 修改模板
     */
    int updateTemplate(GbSiteTemplate template);

    /**
     * 删除模板
     */
    int deleteTemplateByIds(Long[] ids);
}
