package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbSiteTemplate;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 站点模板 Mapper 接口
 */
@Mapper
public interface GbSiteTemplateMapper {

    /**
     * 查询所有启用的模板（可按 siteFunctionType 过滤）
     */
    List<GbSiteTemplate> selectTemplateList(GbSiteTemplate query);

    /**
     * 根据 ID 查询
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
     * 删除模板（物理删除）
     */
    int deleteTemplateById(Long id);

    /**
     * 批量删除
     */
    int deleteTemplateByIds(Long[] ids);
}
