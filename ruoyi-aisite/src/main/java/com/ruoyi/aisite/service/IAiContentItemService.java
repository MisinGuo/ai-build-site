package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiContentItem;

/**
 * 内容项 Service 接口
 */
public interface IAiContentItemService
{
    AiContentItem selectAiContentItemById(Long id);

    AiContentItem selectAiContentItemBySlug(Long siteId, String typeCode, String slug);

    List<AiContentItem> selectAiContentItemList(AiContentItem aiContentItem);

    int insertAiContentItem(AiContentItem aiContentItem);

    int updateAiContentItem(AiContentItem aiContentItem);

    int deleteAiContentItemById(Long id);

    int deleteAiContentItemByIds(Long[] ids);

    void incrementViewCount(Long id);
}
