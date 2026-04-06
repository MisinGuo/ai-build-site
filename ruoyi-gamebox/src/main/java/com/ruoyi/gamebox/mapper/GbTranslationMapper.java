package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbTranslation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 多语言翻译Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-10
 */
@Mapper
public interface GbTranslationMapper
{
    /**
     * 查询翻译记录
     * 
     * @param gbTranslation 翻译对象
     * @return 翻译记录
     */
    public GbTranslation selectTranslation(GbTranslation gbTranslation);
    
    /**
     * 查询翻译列表
     * 
     * @param gbTranslation 翻译对象
     * @return 翻译集合
     */
    public List<GbTranslation> selectTranslationList(GbTranslation gbTranslation);
    
    /**
     * 新增翻译记录
     * 
     * @param gbTranslation 翻译对象
     * @return 结果
     */
    public int insertTranslation(GbTranslation gbTranslation);
    
    /**
     * 修改翻译记录
     * 
     * @param gbTranslation 翻译对象
     * @return 结果
     */
    public int updateTranslation(GbTranslation gbTranslation);
    
    /**
     * 删除翻译记录
     * 
     * @param id 翻译记录ID
     * @return 结果
     */
    public int deleteTranslationById(Long id);
    
    /**
     * 批量删除翻译记录
     * 
     * @param ids 需要删除的记录ID
     * @return 结果
     */
    public int deleteTranslationByIds(Long[] ids);

    /**
     * 批量获取实体翻译
     * 
     * @param entityType 实体类型
     * @param entityIds 实体ID列表
     * @param locale 语言代码
     * @return 翻译数据
     */
    List<GbTranslation> selectBatchTranslations(@Param("entityType") String entityType, 
                                               @Param("entityIds") List<Long> entityIds, 
                                               @Param("locale") String locale);
    
    /**
     * 获取单个实体的所有翻译字段
     * 
     * @param entityType 实体类型
     * @param entityId 实体ID
     * @param locale 语言代码
     * @return 翻译数据映射 field_name -> field_value
     */
    Map<String, String> selectEntityTranslations(@Param("entityType") String entityType, 
                                                @Param("entityId") Long entityId, 
                                                @Param("locale") String locale);

    /**
     * 删除单个字段的翻译
     */
    int deleteFieldTranslation(@Param("entityType") String entityType,
                              @Param("entityId") Long entityId,
                              @Param("locale") String locale,
                              @Param("fieldName") String fieldName);
}