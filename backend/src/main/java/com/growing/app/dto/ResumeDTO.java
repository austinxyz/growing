package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// From Resume entity + nested resources
@Data
public class ResumeDTO {
    private Long id;
    private Long userId;
    private String versionName;
    private Boolean isDefault;
    private Long jobApplicationId; // 关联的职位申请ID

    private String about;
    private String careerObjective;

    // Contact & Links
    private String email;
    private String phone;
    private String address;
    private String linkedinUrl;
    private String githubUrl;
    private String websiteUrl;
    private List<LinkDTO> otherLinks;  // From Resume.otherLinks JSON

    // Languages & Hobbies
    private String languages;  // JSON string: ["English", "Mandarin"]
    private String hobbies;

    // Nested collections (DTO Completeness Checklist: all collections populated)
    private List<ResumeExperienceDTO> experiences;
    private List<ResumeSkillDTO> skills;
    private List<ResumeEducationDTO> education;
    private List<ResumeCertificationDTO> certifications;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class LinkDTO {
        private String title;
        private String url;
    }

    @Data
    public static class LanguageDTO {
        private String name;
        private String proficiency;  // Native, Fluent, Professional, Limited
    }
}
