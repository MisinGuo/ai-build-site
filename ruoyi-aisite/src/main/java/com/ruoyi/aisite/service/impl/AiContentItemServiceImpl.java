package com.ruoyi.aisite.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.aisite.domain.AiContentItem;
import com.ruoyi.aisite.mapper.AiContentItemMapper;
import com.ruoyi.aisite.service.IAiContentItemService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 内容项 Service 实现
 */
@Service
public class AiContentItemServiceImpl implements IAiContentItemService
{
    @Autowired
    private AiContentItemMapper aiContentItemMapper;

    @Override
    public AiContentItem selectAiContentItemById(Long id)
    {
        return aiContentItemMapper.selectAiContentItemById(id);
    }

    @Override
    public AiContentItem selectAiContentItemBySlug(Long siteId, String typeCode, String slug)
    {
        return aiContentItemMapper.selectAiContentItemBySlug(siteId, typeCode, slug);
    }

    @Override
    public List<AiContentItem> selectAiContentItemList(AiContentItem aiContentItem)
    {
        return aiContentItemMapper.selectAiContentItemList(aiContentItem);
    }

    @Override
    public int insertAiContentItem(AiContentItem aiContentItem)
    {
        aiContentItem.setCreateTime(DateUtils.getNowDate());
        aiContentItem.setDelFlag("0");
        if (aiContentItem.getPublishStatus() == null) {
            aiContentItem.setPublishStatus("draft");
        }
        if (aiContentItem.getStatus() == null) {
            aiContentItem.setStatus("1");
        }
        if (aiContentItem.getSortOrder() == null) {
            aiContentItem.setSortOrder(0);
        }
        if (aiContentItem.getIsFeatured() == null) {
            aiContentItem.setIsFeatured("0");
        }
        // 自动生成 slug（如未提供）
        if (StringUtils.isEmpty(aiContentItem.getSlug())) {
            aiContentItem.setSlug(generateSlug(aiContentItem.getTitle()));
        }
        return aiContentItemMapper.insertAiContentItem(aiContentItem);
    }

    @Override
    public int updateAiContentItem(AiContentItem aiContentItem)
    {
        aiContentItem.setUpdateTime(DateUtils.getNowDate());
        return aiContentItemMapper.updateAiContentItem(aiContentItem);
    }

    @Override
    public int deleteAiContentItemById(Long id)
    {
        return aiContentItemMapper.deleteAiContentItemById(id);
    }

    @Override
    public int deleteAiContentItemByIds(Long[] ids)
    {
        return aiContentItemMapper.deleteAiContentItemByIds(ids);
    }

    @Override
    public void incrementViewCount(Long id)
    {
        aiContentItemMapper.incrementViewCount(id);
    }

    /**
     * 根据标题生成 slug（简单实现）
     */
    private String generateSlug(String title)
    {
        if (StringUtils.isEmpty(title)) {
            return "item-" + System.currentTimeMillis();
        }
        // 转小写，替换空格为连字符，去除特殊字符
        String slug = title.toLowerCase()
            .replaceAll("[^a-z0-9\\u4e00-\\u9fa5\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-");
        // 截断到合理长度
        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
        }
        // 保证唯一性（简单追加时间戳后缀）
        return slug + "-" + (System.currentTimeMillis() % 100000);
    }
}
