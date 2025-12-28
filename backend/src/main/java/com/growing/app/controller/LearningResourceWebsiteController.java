package com.growing.app.controller;

import com.growing.app.model.LearningResourceWebsite;
import com.growing.app.repository.LearningResourceWebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习资源网站控制器
 * 提供网站配置信息的查询，包括iframe支持情况
 */
@RestController
@RequestMapping("/api/learning-resource-websites")
public class LearningResourceWebsiteController {

    @Autowired
    private LearningResourceWebsiteRepository websiteRepository;

    /**
     * 获取所有网站信息
     */
    @GetMapping
    public List<LearningResourceWebsite> getAllWebsites() {
        return websiteRepository.findAll();
    }

    /**
     * 获取所有网站的iframe支持情况（简化版，只返回baseUrl和supportsIframe）
     * 返回格式: { "https://www.hellointerview.com": false, "https://labuladong.online": true }
     */
    @GetMapping("/iframe-support")
    public Map<String, Boolean> getIframeSupportMap() {
        return websiteRepository.findAll().stream()
                .collect(Collectors.toMap(
                        LearningResourceWebsite::getBaseUrl,
                        LearningResourceWebsite::getSupportsIframe
                ));
    }

    /**
     * 根据名称获取网站信息
     */
    @GetMapping("/{name}")
    public LearningResourceWebsite getWebsiteByName(@PathVariable String name) {
        return websiteRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Website not found: " + name));
    }
}
