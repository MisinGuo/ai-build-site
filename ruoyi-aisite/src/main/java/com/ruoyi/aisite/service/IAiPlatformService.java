package com.ruoyi.aisite.service;

import java.util.List;
import com.ruoyi.aisite.domain.AiPlatform;

public interface IAiPlatformService
{
    AiPlatform selectAiPlatformById(Long id);
    List<AiPlatform> selectAiPlatformList(AiPlatform query);
    int insertAiPlatform(AiPlatform entity);
    int updateAiPlatform(AiPlatform entity);
    int deleteAiPlatformById(Long id);
    int deleteAiPlatformByIds(Long[] ids);
}
