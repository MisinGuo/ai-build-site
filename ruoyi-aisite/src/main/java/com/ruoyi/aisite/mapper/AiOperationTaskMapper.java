package com.ruoyi.aisite.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.aisite.domain.AiOperationTask;

@Mapper
public interface AiOperationTaskMapper
{
    AiOperationTask selectById(Long id);
    List<AiOperationTask> selectList(AiOperationTask query);
    int insert(AiOperationTask entity);
    int update(AiOperationTask entity);
    int deleteById(Long id);
    int deleteByIds(Long[] ids);
}
