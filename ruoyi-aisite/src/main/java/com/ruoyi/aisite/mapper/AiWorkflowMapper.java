package com.ruoyi.aisite.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.aisite.domain.AiWorkflow;

@Mapper
public interface AiWorkflowMapper
{
    AiWorkflow selectById(Long id);
    AiWorkflow selectByCode(String code);
    List<AiWorkflow> selectList(AiWorkflow query);
    int insert(AiWorkflow entity);
    int update(AiWorkflow entity);
    int deleteById(Long id);
    int deleteByIds(Long[] ids);
}
