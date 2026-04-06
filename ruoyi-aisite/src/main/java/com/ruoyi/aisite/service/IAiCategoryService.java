package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiCategory;

/**
 * 通用分类 Service 接口
 */
public interface IAiCategoryService
{
    AiCategory selectAiCategoryById(Long id);

    AiCategory selectAiCategoryBySlug(Long siteId, String typeCode, String slug);

    List<AiCategory> selectAiCategoryList(AiCategory aiCategory);

    int insertAiCategory(AiCategory aiCategory);

    int updateAiCategory(AiCategory aiCategory);

    int deleteAiCategoryById(Long id);

    int deleteAiCategoryByIds(Long[] ids);
}
