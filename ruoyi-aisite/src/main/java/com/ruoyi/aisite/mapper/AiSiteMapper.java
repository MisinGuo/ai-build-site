package com.ruoyi.aisite.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.aisite.domain.AiSite;

@Mapper
public interface AiSiteMapper
{
    AiSite selectById(Long id);

    AiSite selectByDomain(String domain);

    List<AiSite> selectList(AiSite query);

    int insert(AiSite aiSite);

    int update(AiSite aiSite);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);
}
