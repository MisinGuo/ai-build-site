package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiAtomicTool;

public interface IAiAtomicToolService
{
    AiAtomicTool selectAiAtomicToolById(Long id);
    AiAtomicTool selectAiAtomicToolByCode(String toolCode);
    List<AiAtomicTool> selectAiAtomicToolList(AiAtomicTool query);
    int insertAiAtomicTool(AiAtomicTool entity);
    int updateAiAtomicTool(AiAtomicTool entity);
    int deleteAiAtomicToolById(Long id);
    int deleteAiAtomicToolByIds(Long[] ids);
}
