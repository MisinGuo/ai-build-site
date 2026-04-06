package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbWorkflowTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工作流模板 Mapper
 */
public interface GbWorkflowTemplateMapper {

    /**
     * 查询模板列表
     */
    List<GbWorkflowTemplate> selectList(GbWorkflowTemplate query);

    /**
     * 根据 ID 查询
     */
    GbWorkflowTemplate selectById(@Param("id") Long id);

    /**
     * 根据模板编码查询
     */
    GbWorkflowTemplate selectByCode(@Param("templateCode") String templateCode);

    /**
     * 新增模板
     */
    int insertTemplate(GbWorkflowTemplate template);

    /**
     * 更新模板
     */
    int updateTemplate(GbWorkflowTemplate template);

    /**
     * 删除模板
     */
    int deleteById(@Param("id") Long id);

    /**
     * 使用次数 +1
     */
    int incrementUsageCount(@Param("id") Long id);
}
