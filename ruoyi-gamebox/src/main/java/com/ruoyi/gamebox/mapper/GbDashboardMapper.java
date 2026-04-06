package com.ruoyi.gamebox.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 首页仪表盘统计 Mapper
 */
@Mapper
public interface GbDashboardMapper {

    /** 当前用户有权限的非个人站点下的游戏总数 */
    @Select("SELECT COUNT(*) FROM gb_games g " +
            "INNER JOIN gb_user_site_relation r ON r.site_id = g.site_id AND r.user_id = #{userId} " +
            "INNER JOIN gb_sites s ON s.id = g.site_id AND s.del_flag = '0' AND s.is_personal = 0 " +
            "WHERE g.del_flag = '0'")
    long countGames(@Param("userId") Long userId);

    /** 当前用户有权限的非个人站点下的游戏盒子总数 */
    @Select("SELECT COUNT(*) FROM gb_game_boxes b " +
            "INNER JOIN gb_user_site_relation r ON r.site_id = b.site_id AND r.user_id = #{userId} " +
            "INNER JOIN gb_sites s ON s.id = b.site_id AND s.del_flag = '0' AND s.is_personal = 0 " +
            "WHERE b.del_flag = '0'")
    long countGameBoxes(@Param("userId") Long userId);

    /** 当前用户有权限的非个人站点下的文章总数（articles 通过 master_articles 关联站点） */
    @Select("SELECT COUNT(*) FROM gb_articles a " +
            "INNER JOIN gb_master_articles m ON m.id = a.master_article_id AND m.del_flag = '0' " +
            "INNER JOIN gb_user_site_relation r ON r.site_id = m.site_id AND r.user_id = #{userId} " +
            "INNER JOIN gb_sites s ON s.id = m.site_id AND s.del_flag = '0' AND s.is_personal = 0 " +
            "WHERE a.del_flag = '0'")
    long countArticles(@Param("userId") Long userId);

    /** 当前用户有权限的非个人站点下的主文章总数 */
    @Select("SELECT COUNT(*) FROM gb_master_articles m " +
            "INNER JOIN gb_user_site_relation r ON r.site_id = m.site_id AND r.user_id = #{userId} " +
            "INNER JOIN gb_sites s ON s.id = m.site_id AND s.del_flag = '0' AND s.is_personal = 0 " +
            "WHERE m.del_flag = '0'")
    long countMasterArticles(@Param("userId") Long userId);

    /** 当前用户有权限的非个人站点数 */
    @Select("SELECT COUNT(*) FROM gb_sites s " +
            "INNER JOIN gb_user_site_relation r ON r.site_id = s.id AND r.user_id = #{userId} " +
            "WHERE s.del_flag = '0' AND s.is_personal = 0")
    long countSites(@Param("userId") Long userId);
}
