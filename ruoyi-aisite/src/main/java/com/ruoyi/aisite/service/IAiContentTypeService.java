package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiContentType;

/**
 * 内容类型 Service 接口
 */
public interface IAiContentTypeService
{
    AiContentType selectAiContentTypeById(Long id);

    AiContentType selectAiContentTypeByCode(Long siteId, String code);

    List<AiContentType> selectAiContentTypeList(AiContentType aiContentType);

    int insertAiContentType(AiContentType aiContentType);

    int updateAiContentType(AiContentType aiContentType);

    int deleteAiContentTypeById(Long id);

    int deleteAiContentTypeByIds(Long[] ids);
}
