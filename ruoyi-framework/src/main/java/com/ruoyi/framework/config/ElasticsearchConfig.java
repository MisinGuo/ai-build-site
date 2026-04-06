package com.ruoyi.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch配置类
 * 
 * @author ruoyi
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.ruoyi.gamebox.search.repository")
public class ElasticsearchConfig
{
    // Spring Boot 2.5.x 的 spring-boot-starter-data-elasticsearch 会自动配置
    // ElasticsearchRestTemplate 和 RestHighLevelClient
    // 只需要在 application.yml 中配置 spring.elasticsearch.uris 即可
}
