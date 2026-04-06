package com.ruoyi.aisite.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.aisite.domain.AiAtomicTool;

@Mapper
public interface AiAtomicToolMapper
{
    AiAtomicTool selectById(Long id);
    AiAtomicTool selectByCode(String toolCode);
    List<AiAtomicTool> selectList(AiAtomicTool query);
    int insert(AiAtomicTool entity);
    int update(AiAtomicTool entity);
    int deleteById(Long id);
    int deleteByIds(Long[] ids);
}
