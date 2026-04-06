package com.ruoyi.framework.config;

import java.util.TimeZone;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 *
 * @author ruoyi
 */
@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.ruoyi.**.mapper")
public class ApplicationConfig
{
    /**
     * Jackson 全局时区配置
     *
     * <p>此 Bean 的优先级高于 application.yml 中的 spring.jackson.time-zone，
     * 因此时区必须在此处显式指定，yml 中的配置会被覆盖而不生效。
     *
     * <p>原始代码使用 TimeZone.getDefault()，在服务器 JVM 默认时区为 UTC 的环境下，
     * 所有带 @JsonFormat 的 Date 字段会被序列化为 UTC 时间字符串（比北京时间少 8 小时），
     * 导致前端执行历史等页面显示的时间不正确。
     *
     * <p>注意：此配置仅影响 Jackson JSON 序列化输出，不影响 Quartz 调度时区。
     * Quartz 的时区需在 ScheduleUtils.createScheduleJob 中单独指定。
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
