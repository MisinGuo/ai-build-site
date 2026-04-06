package com.ruoyi.aisite.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.aisite.domain.AiMatrixGroup;

@Mapper
public interface AiMatrixGroupMapper
{
    AiMatrixGroup selectById(Long id);
    List<AiMatrixGroup> selectList(AiMatrixGroup query);
    int insert(AiMatrixGroup entity);
    int update(AiMatrixGroup entity);
    int deleteById(Long id);
    int deleteByIds(Long[] ids);
}
