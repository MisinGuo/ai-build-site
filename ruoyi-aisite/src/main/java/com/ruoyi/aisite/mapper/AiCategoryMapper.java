package com.ruoyi.aisite.mapper;

import java.util.List;
import com.ruoyi.aisite.domain.AiCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通用分类 Mapper 接口
 */
@Mapper
public interface AiCategoryMapper
{
    AiCategory selectAiCategoryById(Long id);

    AiCategory selectAiCategoryBySlug(@Param("siteId") Long siteId, @Param("typeCode") String typeCode, @Param("slug") String slug);

    List<AiCategory> selectAiCategoryList(AiCategory aiCategory);

    int insertAiCategory(AiCategory aiCategory);

    int updateAiCategory(AiCategory aiCategory);

    int deleteAiCategoryById(Long id);

    int deleteAiCategoryByIds(Long[] ids);
}
