package com.ruoyi.web.controller.gamebox;

import java.util.Set;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;

/**
 * 统计数据及可见 ID 集合缓存失效切面
 * <p>
 * 拦截游戏、盒子、文章的增删改操作（通过 {@link Log} 注解识别），
 * 在操作成功返回后自动清除：
 * <ul>
 *   <li>统计缓存：{@code public:statistics:*}</li>
 *   <li>可见游戏 ID 缓存：{@code public:visible_game_ids:*}</li>
 *   <li>可见文章 ID 缓存：{@code public:visible_article_ids:*}</li>
 * </ul>
 * </p>
 */
@Aspect
@Component
public class StatisticsCacheEvictAspect
{
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCacheEvictAspect.class);

    private static final String STATISTICS_KEY_PATTERN      = "public:statistics:*";
    private static final String VISIBLE_GAME_KEY_PATTERN    = "public:visible_game_ids:*";
    private static final String VISIBLE_ARTICLE_KEY_PATTERN = "public:visible_article_ids:*";
    private static final String VISIBLE_BOX_KEY_PATTERN     = "public:visible_box_ids:*";
    private static final String HOME_KEY_PATTERN            = "public:home:*";

    @Autowired(required = false)
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 在 gamebox 包下任意带 @Log 注解的方法正常返回后触发。
     * 仅当 businessType 为 INSERT / UPDATE / DELETE 时才清除缓存。
     */
    @AfterReturning("@annotation(controllerLog) && within(com.ruoyi.web.controller.gamebox..*)")
    public void evictPublicCaches(Log controllerLog)
    {
        BusinessType type = controllerLog.businessType();
        if (type != BusinessType.INSERT
                && type != BusinessType.UPDATE
                && type != BusinessType.DELETE)
        {
            return;
        }
        if (redisTemplate == null)
        {
            return;
        }
        try
        {
            for (String pattern : new String[]{
                    STATISTICS_KEY_PATTERN,
                    VISIBLE_GAME_KEY_PATTERN,
                    VISIBLE_ARTICLE_KEY_PATTERN,
                    VISIBLE_BOX_KEY_PATTERN,
                    HOME_KEY_PATTERN})
            {
                Set<Object> keys = redisTemplate.keys(pattern);
                if (keys != null && !keys.isEmpty())
                {
                    redisTemplate.delete(keys);
                }
            }
            logger.debug("公开接口缓存已清除，触发操作: {} ({})", controllerLog.title(), type);
        }
        catch (Exception e)
        {
            logger.warn("清除公开接口缓存失败", e);
        }
    }
}
