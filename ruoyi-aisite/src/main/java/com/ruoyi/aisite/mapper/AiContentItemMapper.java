package com.ruoyi.aisite.mapper;

import java.util.List;
import com.ruoyi.aisite.domain.AiContentItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 内容项 Mapper 接口
 */
@Mapper
public interface AiContentItemMapper
{
    /**
     * 查询内容项
     */
    AiContentItem selectAiContentItemById(Long id);

    /**
     * 按 slug 查询
     */
    AiContentItem selectAiContentItemBySlug(@Param("siteId") Long siteId, @Param("typeCode") String typeCode, @Param("slug") String slug);

    /**
     * 查询列表
     */
    List<AiContentItem> selectAiContentItemList(AiContentItem aiContentItem);

    /**
     * 新增
     */
    int insertAiContentItem(AiContentItem aiContentItem);

    /**
     * 修改
     */
    int updateAiContentItem(AiContentItem aiContentItem);

    /**
     * 删除（软删除）
     */
    int deleteAiContentItemById(Long id);

    /**
     * 批量删除
     */
    int deleteAiContentItemByIds(Long[] ids);

    /**
     * 增加浏览量
     */
    int incrementViewCount(Long id);
}
