package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbSiteCodeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站代码管理配置 Mapper 接口
 */
@Mapper
public interface GbSiteCodeConfigMapper {

    /**
     * 根据网站ID查询配置（不存在时返回 null）
     */
    GbSiteCodeConfig selectBySiteId(@Param("siteId") Long siteId);

    /**
     * 插入配置（新建站点时初始化用）
     */
    int insert(GbSiteCodeConfig config);

    /**
     * 更新配置（全字段更新，null 不覆盖）
     */
    int updateBySiteId(GbSiteCodeConfig config);

    /**
     * 存在则更新，不存在则插入（upsert）
     */
    int upsert(GbSiteCodeConfig config);

    /**
     * 仅更新部署状态相关字段（避免覆盖 git 配置）
     */
    int updateDeployStatus(@Param("siteId") Long siteId,
                           @Param("deployStatus") String deployStatus,
                           @Param("deployUrl") String deployUrl,
                           @Param("lastDeployTime") java.util.Date lastDeployTime,
                           @Param("lastDeployLog") String lastDeployLog);

    /**
     * 删除（随 gb_sites 级联删除，一般不需单独调用）
     */
    int deleteBySiteId(@Param("siteId") Long siteId);
}
