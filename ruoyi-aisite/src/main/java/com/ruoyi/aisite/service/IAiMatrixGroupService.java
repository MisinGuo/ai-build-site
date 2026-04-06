package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiMatrixGroup;

public interface IAiMatrixGroupService
{
    AiMatrixGroup selectAiMatrixGroupById(Long id);
    List<AiMatrixGroup> selectAiMatrixGroupList(AiMatrixGroup query);
    int insertAiMatrixGroup(AiMatrixGroup entity);
    int updateAiMatrixGroup(AiMatrixGroup entity);
    int deleteAiMatrixGroupById(Long id);
    int deleteAiMatrixGroupByIds(Long[] ids);
}
