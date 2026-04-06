package com.ruoyi.aisite.mapper;

import java.util.List;
import com.ruoyi.aisite.domain.AiContentType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 内容类型 Mapper 接口
 */
@Mapper
public interface AiContentTypeMapper
{
    /**
     * 查询内容类型
     */
    AiContentType selectAiContentTypeById(Long id);

    /**
     * 按 code 查询
     */
    AiContentType selectAiContentTypeByCode(@Param("siteId") Long siteId, @Param("code") String code);

    /**
     * 查询内容类型列表
     */
    List<AiContentType> selectAiContentTypeList(AiContentType aiContentType);

    /**
     * 新增内容类型
     */
    int insertAiContentType(AiContentType aiContentType);

    /**
     * 修改内容类型
     */
    int updateAiContentType(AiContentType aiContentType);

    /**
     * 删除内容类型（软删除）
     */
    int deleteAiContentTypeById(Long id);

    /**
     * 批量删除
     */
    int deleteAiContentTypeByIds(Long[] ids);
}
