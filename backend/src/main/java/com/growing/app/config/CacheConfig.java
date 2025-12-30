package com.growing.app.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 * 使用Spring内置的ConcurrentMapCache（基于ConcurrentHashMap）
 *
 * 适用场景：
 * - 单机应用
 * - 数据量小（几百条职业路径/技能/分类/Focus Area）
 * - 更新不频繁
 *
 * 如果未来需要分布式缓存（多实例），可替换为Redis
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "careerPaths",
                "skills",
                "majorCategories",
                "focusAreas"
        );
    }
}
