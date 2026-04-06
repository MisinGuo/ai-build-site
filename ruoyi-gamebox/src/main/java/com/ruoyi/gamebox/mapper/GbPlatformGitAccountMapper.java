package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbPlatformGitAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 平台Git账号 Mapper 接口
 */
@Mapper
public interface GbPlatformGitAccountMapper {

    List<GbPlatformGitAccount> selectList(GbPlatformGitAccount query);

    GbPlatformGitAccount selectById(Long id);

    /** 查询所有启用的账号（供下拉框使用，token脱敏） */
    List<GbPlatformGitAccount> selectEnabledList();

    int insert(GbPlatformGitAccount account);

    int update(GbPlatformGitAccount account);

    int deleteById(Long id);

    int deleteByIds(Long[] ids);

    /** 清除所有默认标记（用于设置新默认前重置） */
    int clearDefault();
}
