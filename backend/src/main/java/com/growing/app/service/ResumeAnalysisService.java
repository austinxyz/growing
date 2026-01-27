package com.growing.app.service;

import com.growing.app.dto.*;
import com.growing.app.dto.match.*;
import com.growing.app.entity.*;
import com.growing.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 简历分析Service - 多维度匹配算法
 * Phase 7: Job Search Module - Resume Analysis & Customization
 */
@Service
public class ResumeAnalysisService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ProjectExperienceRepository projectExperienceRepository;

    @Autowired
    private ManagementExperienceRepository managementExperienceRepository;

    @Autowired
    private ResumeEducationRepository resumeEducationRepository;

    @Autowired
    private ResumeService resumeService;

    // ========== 技能别名映射 ==========
    private static final Map<String, List<String>> SKILL_ALIASES = new HashMap<>() {{
        put("Kubernetes", Arrays.asList("kubernetes", "k8s", "kube"));
        put("Docker", Arrays.asList("docker"));  // Removed "container" - too broad
        put("Python", Arrays.asList("python", "py"));
        put("JavaScript", Arrays.asList("javascript", "js"));
        put("TypeScript", Arrays.asList("typescript", "ts"));
        put("Go", Arrays.asList("go", "golang"));
        put("Java", Arrays.asList("java"));
        put("C++", Arrays.asList("c++", "cpp"));
        put("React", Arrays.asList("react", "reactjs"));
        put("Vue", Arrays.asList("vue", "vuejs"));
        put("Angular", Arrays.asList("angular"));
        put("Spring Boot", Arrays.asList("spring boot", "springboot"));
        put("Node.js", Arrays.asList("node", "nodejs", "node.js"));
        put("MySQL", Arrays.asList("mysql"));
        put("PostgreSQL", Arrays.asList("postgresql", "postgres"));
        put("MongoDB", Arrays.asList("mongodb", "mongo"));
        put("Redis", Arrays.asList("redis"));
        put("Elasticsearch", Arrays.asList("elasticsearch", "elastic search"));  // Removed "es" - too broad
        put("AWS", Arrays.asList("aws", "amazon web services"));
        put("Azure", Arrays.asList("azure"));
        put("GCP", Arrays.asList("gcp", "google cloud"));
        put("Kafka", Arrays.asList("kafka"));
        put("RabbitMQ", Arrays.asList("rabbitmq", "rabbit"));
        put("Jenkins", Arrays.asList("jenkins"));
        put("Git", Arrays.asList("git", "github", "gitlab"));
        put("CI/CD", Arrays.asList("ci/cd", "cicd", "continuous integration"));
        put("Microservices", Arrays.asList("microservices", "micro-services", "microservice"));
        put("RESTful", Arrays.asList("restful", "rest api", "rest"));
        put("GraphQL", Arrays.asList("graphql"));
        put("gRPC", Arrays.asList("grpc"));
    }};

    // ========== 软技能关键词 ==========
    private static final Map<String, List<String>> SOFT_SKILL_KEYWORDS = new HashMap<>() {{
        put("Leadership", Arrays.asList("lead", "leadership", "manage", "direct", "supervise"));
        put("Coaching", Arrays.asList("coach", "mentor", "train", "develop", "up-level", "grow"));
        put("Communication", Arrays.asList("communicate", "collaborate", "partner", "coordinate"));
        put("Strategy", Arrays.asList("strategy", "strategic", "vision", "roadmap"));
        put("Problem Solving", Arrays.asList("solve", "troubleshoot", "debug", "investigate"));
    }};

    // ========== 职责关键词 ==========
    private static final Map<String, List<String>> RESPONSIBILITY_KEYWORDS = new HashMap<>() {{
        put("Platform Building", Arrays.asList("build platform", "operate platform", "platform engineering", "infrastructure"));
        put("Team Coaching", Arrays.asList("coach team", "mentor", "train developers", "up-level", "grow team"));
        put("Technical Strategy", Arrays.asList("strategy", "roadmap", "vision", "architecture", "technical direction"));
    }};

    /**
     * 分析简历与职位的匹配度 (通过jobApplicationId)
     */
    public ResumeAnalysisDTO analyzeResumeByApplication(Long jobApplicationId, Long userId) {
        JobApplication application = jobApplicationRepository.findById(jobApplicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职位申请不存在"));

        if (!application.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此职位申请");
        }

        return performAnalysis(application.getId(), application.getQualifications(),
                application.getResponsibilities(), userId);
    }

    /**
     * 分析简历与职位的匹配度 (通过jobId - 未申请的职位)
     */
    public ResumeAnalysisDTO analyzeResumeByJob(Long jobId, Long userId) {
        JobApplication job = jobApplicationRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "职位不存在"));

        return performAnalysis(job.getId(), job.getQualifications(),
                job.getResponsibilities(), userId);
    }

    /**
     * 执行简历分析 (核心逻辑 - 多维度匹配)
     */
    private ResumeAnalysisDTO performAnalysis(
            Long jobId,
            String qualifications,
            String responsibilities,
            Long userId
    ) {
        // 获取默认简历
        Resume resume = resumeRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到默认简历"));

        // 获取简历相关数据
        List<ProjectExperience> projects = projectExperienceRepository.findByUserIdOrderByStartDateDesc(userId);
        List<ManagementExperience> managements = managementExperienceRepository.findByUserIdOrderByStartDateDesc(userId);
        List<ResumeEducation> educations = resumeEducationRepository.findByResumeIdOrderBySortOrder(resume.getId());

        // 多维度匹配
        EducationMatchDTO educationMatch = matchEducation(educations, qualifications);
        ExperienceMatchDTO experienceMatch = matchExperience(resume, educations, projects, managements, qualifications);
        SkillMatchDTO skillMatch = matchSkills(projects, qualifications, responsibilities);
        ResponsibilityMatchDTO responsibilityMatch = matchResponsibilities(projects, managements, responsibilities);
        SoftSkillMatchDTO softSkillMatch = matchSoftSkills(managements, resume, qualifications);

        // 计算总匹配度 (加权平均)
        int overallScore = calculateOverallScore(
                educationMatch.getScore(),
                experienceMatch.getScore(),
                skillMatch.getScore(),
                responsibilityMatch.getScore(),
                softSkillMatch.getScore()
        );

        // 构建返回结果
        ResumeAnalysisDTO result = new ResumeAnalysisDTO();
        result.setJobApplicationId(jobId);
        result.setResumeId(resume.getId());
        result.setMatchScore(overallScore);

        // 设置多维度匹配详情
        result.setEducationMatch(educationMatch);
        result.setExperienceMatch(experienceMatch);
        result.setSkillMatch(skillMatch);
        result.setResponsibilityMatch(responsibilityMatch);
        result.setSoftSkillMatch(softSkillMatch);

        // 兼容旧字段
        result.setMatchedSkills(skillMatch.getMatchedSkills());
        result.setMissingSkills(skillMatch.getMissingSkills());

        // 分析优势和改进建议
        result.setStrengths(analyzeStrengths(educationMatch, experienceMatch, skillMatch, projects, managements));
        result.setImprovements(analyzeImprovements(educationMatch, experienceMatch, skillMatch, responsibilityMatch, softSkillMatch));

        // 定制化建议 (>70%)
        if (overallScore > 70) {
            result.setCustomization(generateCustomization(skillMatch, projects, overallScore));
        }

        return result;
    }

    // ========== 1. 教育背景匹配 (15%) ==========
    private EducationMatchDTO matchEducation(List<ResumeEducation> educations, String qualifications) {
        if (educations.isEmpty()) {
            return new EducationMatchDTO(false, 0.0, "无", "无", 0,
                    "未找到教育背景信息");
        }

        // 获取最高学历
        ResumeEducation highest = educations.stream()
                .max(Comparator.comparing(e -> getDegreeLevel(e.getDegree())))
                .orElse(educations.get(0));

        String degree = highest.getDegree();
        String major = highest.getMajor();
        int degreeLevel = getDegreeLevel(degree);

        // JD要求学历检查
        boolean requiresBachelor = qualifications != null &&
                (qualifications.toLowerCase().contains("bachelor") ||
                 qualifications.toLowerCase().contains("ba/bs") ||
                 qualifications.toLowerCase().contains("本科"));

        boolean hasRequiredDegree = degreeLevel >= 2;  // 至少本科

        // 专业相关度计算
        double majorRelevance = calculateMajorRelevance(major, qualifications);

        // 计算分数
        int score = (int) ((hasRequiredDegree ? 50 : 0) + (majorRelevance * 50));

        String explanation = String.format("%s学位，%s专业，学历%s要求",
                degree, major != null ? major : "未知",
                hasRequiredDegree ? "满足" : "不满足");

        return new EducationMatchDTO(hasRequiredDegree, majorRelevance, degree, major, score, explanation);
    }

    private int getDegreeLevel(String degree) {
        if (degree == null) return 0;
        String lower = degree.toLowerCase();
        if (lower.contains("doctor") || lower.contains("phd") || lower.contains("博士")) return 4;
        if (lower.contains("master") || lower.contains("硕士")) return 3;
        if (lower.contains("bachelor") || lower.contains("本科") || lower.contains("学士")) return 2;
        return 1;
    }

    private double calculateMajorRelevance(String major, String qualifications) {
        if (major == null || qualifications == null) return 0.5;

        String majorLower = major.toLowerCase();
        String qualLower = qualifications.toLowerCase();

        // 完全匹配专业领域
        if (qualLower.contains("computer science") && majorLower.contains("computer")) return 1.0;
        if (qualLower.contains("engineering") && (majorLower.contains("engineering") || majorLower.contains("ee"))) return 0.7;
        if (qualLower.contains("technical") && (majorLower.contains("technology") || majorLower.contains("engineering"))) return 0.7;

        return 0.5;  // 默认中等相关
    }

    // ========== 2. 工作经验匹配 (25%) ==========
    private ExperienceMatchDTO matchExperience(
            Resume resume,
            List<ResumeEducation> educations,
            List<ProjectExperience> projects,
            List<ManagementExperience> managements,
            String qualifications
    ) {
        // 提取总工作年限
        int totalYears = extractTotalYearsOfExperience(resume, educations);

        // 计算管理年限
        int managementYears = calculateManagementYears(managements);

        // 从JD提取要求的年限
        int requiredYears = extractRequiredYears(qualifications);

        // 是否满足最低要求
        boolean meetsMinimum = totalYears >= requiredYears;

        // 计算分数
        int score = meetsMinimum ? 100 : (totalYears * 100 / Math.max(1, requiredYears));
        score = Math.min(100, score);  // Cap at 100

        String explanation = String.format("总工作年限 %d 年，管理经验 %d 年，JD要求 %d+ 年，%s要求",
                totalYears, managementYears, requiredYears,
                meetsMinimum ? "满足" : "不满足");

        return new ExperienceMatchDTO(totalYears, managementYears, meetsMinimum,
                requiredYears + "+ years", score, explanation);
    }

    /**
     * 从about字段或毕业年份提取总工作年限
     */
    private int extractTotalYearsOfExperience(Resume resume, List<ResumeEducation> educations) {
        // 1. 优先从about字段提取
        if (resume.getAbout() != null) {
            Pattern pattern = Pattern.compile("(\\d+)\\+?\\s*years?\\s*(of\\s*)?(experience|building|operating)?",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(resume.getAbout());
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }

        // 2. 从最高学历毕业年份推算
        if (!educations.isEmpty()) {
            Optional<LocalDate> latestGraduation = educations.stream()
                    .map(ResumeEducation::getEndDate)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder());

            if (latestGraduation.isPresent()) {
                int yearsSinceGraduation = LocalDate.now().getYear() - latestGraduation.get().getYear();
                return Math.max(0, yearsSinceGraduation);
            }
        }

        return 0;
    }

    private int calculateManagementYears(List<ManagementExperience> managements) {
        if (managements.isEmpty()) return 0;

        int totalMonths = 0;
        for (ManagementExperience mgmt : managements) {
            if (mgmt.getStartDate() != null && mgmt.getEndDate() != null) {
                Period period = Period.between(mgmt.getStartDate(), mgmt.getEndDate());
                totalMonths += period.getYears() * 12 + period.getMonths();
            }
        }

        return totalMonths / 12;
    }

    private int extractRequiredYears(String qualifications) {
        if (qualifications == null) return 0;

        // Match "5+ years", "10 years", etc.
        Pattern pattern = Pattern.compile("(\\d+)\\+?\\s*years?\\s*(of\\s*)?(experience|software|management)?",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(qualifications);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return 5;  // 默认5年
    }

    // ========== 3. 技术技能匹配 (30%) - 支持别名 ==========
    private SkillMatchDTO matchSkills(
            List<ProjectExperience> projects,
            String qualifications,
            String responsibilities
    ) {
        // 从JD提取必需技能 (使用别名匹配)
        Set<String> requiredSkills = extractSkillsFromJD(qualifications, responsibilities);

        // 从简历提取技能 (使用别名匹配)
        Set<String> resumeSkills = extractSkillsFromResume(projects);

        // 计算匹配和缺失
        Set<String> matched = new HashSet<>(requiredSkills);
        matched.retainAll(resumeSkills);

        Set<String> missing = new HashSet<>(requiredSkills);
        missing.removeAll(resumeSkills);

        int score = requiredSkills.isEmpty() ? 100 : (matched.size() * 100) / requiredSkills.size();

        String explanation = String.format("匹配 %d/%d 技能，覆盖率 %d%%",
                matched.size(), requiredSkills.size(), score);

        return new SkillMatchDTO(new ArrayList<>(matched), new ArrayList<>(missing), score, explanation);
    }

    /**
     * 从JD提取技能 (模糊匹配，支持别名)
     */
    private Set<String> extractSkillsFromJD(String qualifications, String responsibilities) {
        Set<String> skills = new HashSet<>();
        String combinedText = (qualifications != null ? qualifications : "") + " " +
                (responsibilities != null ? responsibilities : "");
        String lowerText = combinedText.toLowerCase();

        for (Map.Entry<String, List<String>> entry : SKILL_ALIASES.entrySet()) {
            String skillName = entry.getKey();
            List<String> aliases = entry.getValue();

            for (String alias : aliases) {
                if (lowerText.contains(alias)) {
                    skills.add(skillName);  // 标准化技能名
                    break;
                }
            }
        }

        return skills;
    }

    /**
     * 从简历提取技能 (模糊匹配，支持别名)
     */
    private Set<String> extractSkillsFromResume(List<ProjectExperience> projects) {
        Set<String> skills = new HashSet<>();

        for (ProjectExperience project : projects) {
            if (project.getTechStack() != null) {
                String techStack = project.getTechStack().toLowerCase();

                for (Map.Entry<String, List<String>> entry : SKILL_ALIASES.entrySet()) {
                    String skillName = entry.getKey();
                    List<String> aliases = entry.getValue();

                    for (String alias : aliases) {
                        if (techStack.contains(alias)) {
                            skills.add(skillName);  // 标准化技能名
                            break;
                        }
                    }
                }
            }
        }

        return skills;
    }

    // ========== 4. 职责匹配 (20%) ==========
    private ResponsibilityMatchDTO matchResponsibilities(
            List<ProjectExperience> projects,
            List<ManagementExperience> managements,
            String responsibilities
    ) {
        if (responsibilities == null || responsibilities.isEmpty()) {
            return new ResponsibilityMatchDTO(0, 0, new ArrayList<>(), 100, "JD未指定职责要求");
        }

        List<String> matchedKeywords = new ArrayList<>();
        String responsibilitiesLower = responsibilities.toLowerCase();

        for (Map.Entry<String, List<String>> entry : RESPONSIBILITY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            boolean hasKeyword = keywords.stream().anyMatch(responsibilitiesLower::contains);
            if (!hasKeyword) continue;

            // 检查项目/管理经验是否匹配这个职责
            boolean matchesProject = projects.stream()
                    .anyMatch(p -> matchesAnyKeyword(p.getWhatDescription(), keywords) ||
                            matchesAnyKeyword(p.getMyContribution(), keywords));

            boolean matchesManagement = managements.stream()
                    .anyMatch(m -> matchesAnyKeyword(m.getActionsTaken(), keywords) ||
                            matchesAnyKeyword(m.getResults(), keywords));

            if (matchesProject || matchesManagement) {
                matchedKeywords.add(category);
            }
        }

        Integer totalCount = RESPONSIBILITY_KEYWORDS.size();
        Integer matchedCount = matchedKeywords.size();
        Integer score = (matchedCount * 100) / Math.max(1, totalCount);

        String explanation = String.format("匹配 %d/%d 职责关键词", matchedCount, totalCount);

        ResponsibilityMatchDTO dto = new ResponsibilityMatchDTO();
        dto.setMatchedCount(matchedCount);
        dto.setTotalCount(totalCount);
        dto.setMatchedKeywords(matchedKeywords);
        dto.setScore(score);
        dto.setExplanation(explanation);
        return dto;
    }

    private boolean matchesAnyKeyword(String text, List<String> keywords) {
        if (text == null) return false;
        String lower = text.toLowerCase();
        return keywords.stream().anyMatch(lower::contains);
    }

    // ========== 5. 软技能匹配 (10%) ==========
    private SoftSkillMatchDTO matchSoftSkills(
            List<ManagementExperience> managements,
            Resume resume,
            String qualifications
    ) {
        Set<String> demonstratedSkills = new HashSet<>();

        // 从管理经验中提取
        for (ManagementExperience mgmt : managements) {
            String description = (mgmt.getActionsTaken() != null ? mgmt.getActionsTaken() : "") + " " +
                    (mgmt.getResults() != null ? mgmt.getResults() : "");
            String lowerDesc = description.toLowerCase();

            for (Map.Entry<String, List<String>> entry : SOFT_SKILL_KEYWORDS.entrySet()) {
                String skill = entry.getKey();
                List<String> keywords = entry.getValue();

                if (keywords.stream().anyMatch(lowerDesc::contains)) {
                    demonstratedSkills.add(skill);
                }
            }
        }

        // 从about字段提取
        if (resume.getAbout() != null) {
            String aboutLower = resume.getAbout().toLowerCase();
            for (Map.Entry<String, List<String>> entry : SOFT_SKILL_KEYWORDS.entrySet()) {
                String skill = entry.getKey();
                List<String> keywords = entry.getValue();

                if (keywords.stream().anyMatch(aboutLower::contains)) {
                    demonstratedSkills.add(skill);
                }
            }
        }

        // 从JD提取需要的软技能
        Set<String> requiredSoftSkills = new HashSet<>();
        if (qualifications != null) {
            String qualLower = qualifications.toLowerCase();
            for (Map.Entry<String, List<String>> entry : SOFT_SKILL_KEYWORDS.entrySet()) {
                String skill = entry.getKey();
                List<String> keywords = entry.getValue();

                if (keywords.stream().anyMatch(qualLower::contains)) {
                    requiredSoftSkills.add(skill);
                }
            }
        }

        Set<String> missing = new HashSet<>(requiredSoftSkills);
        missing.removeAll(demonstratedSkills);

        int score = requiredSoftSkills.isEmpty() ? 100 :
                (demonstratedSkills.size() * 100) / requiredSoftSkills.size();

        String explanation = String.format("展示了 %d 项软技能", demonstratedSkills.size());

        return new SoftSkillMatchDTO(new ArrayList<>(demonstratedSkills),
                new ArrayList<>(missing), score, explanation);
    }

    // ========== 计算总匹配度 (加权平均) ==========
    private int calculateOverallScore(
            int educationScore,
            int experienceScore,
            int skillScore,
            int responsibilityScore,
            int softSkillScore
    ) {
        return (int) (
                educationScore * 0.15 +
                        experienceScore * 0.25 +
                        skillScore * 0.30 +
                        responsibilityScore * 0.20 +
                        softSkillScore * 0.10
        );
    }

    // ========== 分析优势 ==========
    private List<String> analyzeStrengths(
            EducationMatchDTO education,
            ExperienceMatchDTO experience,
            SkillMatchDTO skill,
            List<ProjectExperience> projects,
            List<ManagementExperience> managements
    ) {
        List<String> strengths = new ArrayList<>();

        if (education.getHasRequiredDegree()) {
            strengths.add(String.format("教育背景：%s学位满足要求", education.getHighestDegree()));
        }

        if (experience.getMeetsMinimum()) {
            strengths.add(String.format("工作经验：%d年经验超过要求的%s",
                    experience.getTotalYears(), experience.getRequiredYears()));
        }

        if (!skill.getMatchedSkills().isEmpty()) {
            strengths.add(String.format("技术栈匹配：掌握 %d 项职位要求的核心技术",
                    skill.getMatchedSkills().size()));
        }

        if (!projects.isEmpty()) {
            strengths.add(String.format("项目经验丰富：共 %d 个相关项目经验", projects.size()));
        }

        if (!managements.isEmpty()) {
            strengths.add(String.format("管理经验：具备 %d 项管理相关经验", managements.size()));
        }

        return strengths;
    }

    // ========== 分析改进建议 ==========
    private List<String> analyzeImprovements(
            EducationMatchDTO education,
            ExperienceMatchDTO experience,
            SkillMatchDTO skill,
            ResponsibilityMatchDTO responsibility,
            SoftSkillMatchDTO softSkill
    ) {
        List<String> improvements = new ArrayList<>();

        if (!education.getHasRequiredDegree()) {
            improvements.add("教育背景：建议获取相关领域的学士或以上学位");
        }

        if (!experience.getMeetsMinimum()) {
            improvements.add(String.format("工作年限：需要补充 %d 年工作经验",
                    experience.getTotalYears() - experience.getTotalYears()));
        }

        if (!skill.getMissingSkills().isEmpty()) {
            improvements.add(String.format("技能补充：建议学习 %s 等技术",
                    String.join(", ", skill.getMissingSkills())));
        }

        if (responsibility.getScore() < 60) {
            improvements.add("职责经验：建议补充与职位要求相关的项目经验");
        }

        if (!softSkill.getMissingSkills().isEmpty()) {
            improvements.add(String.format("软技能：建议提升 %s",
                    String.join(", ", softSkill.getMissingSkills())));
        }

        return improvements;
    }

    // ========== 定制化建议 ==========
    private ResumeCustomizationDTO generateCustomization(
            SkillMatchDTO skillMatch,
            List<ProjectExperience> projects,
            int matchScore
    ) {
        ResumeCustomizationDTO customization = new ResumeCustomizationDTO();

        // 关键词建议
        List<ResumeCustomizationDTO.KeywordSuggestion> keywords = skillMatch.getMatchedSkills().stream()
                .map(skill -> {
                    ResumeCustomizationDTO.KeywordSuggestion suggestion = new ResumeCustomizationDTO.KeywordSuggestion();
                    suggestion.setKeyword(skill);
                    suggestion.setSection("项目经验或技能列表");
                    suggestion.setReason("职位要求中的核心技能，建议在简历中突出展示");
                    return suggestion;
                })
                .collect(Collectors.toList());
        customization.setKeywordSuggestions(keywords);

        // 项目优化建议
        List<ResumeCustomizationDTO.ProjectOptimization> projectOpts = projects.stream()
                .limit(3)
                .map(project -> {
                    ResumeCustomizationDTO.ProjectOptimization opt = new ResumeCustomizationDTO.ProjectOptimization();
                    opt.setProjectId(project.getId());
                    opt.setProjectName(project.getProjectName());
                    opt.setSuggestions(Arrays.asList(
                            "突出使用的核心技术栈: " + project.getTechStack(),
                            "量化项目成果，添加具体数据（如性能提升百分比、用户增长等）",
                            "强调解决的技术难题与职位要求的匹配点"
                    ));
                    return opt;
                })
                .collect(Collectors.toList());
        customization.setProjectOptimizations(projectOpts);

        // 技能突出建议
        List<ResumeCustomizationDTO.SkillHighlight> skillHighlights = skillMatch.getMatchedSkills().stream()
                .map(skill -> {
                    ResumeCustomizationDTO.SkillHighlight highlight = new ResumeCustomizationDTO.SkillHighlight();
                    highlight.setSkill(skill);
                    highlight.setCurrentMention("在技能列表中提及");
                    highlight.setSuggestedMention("在项目描述中具体说明如何使用" + skill + "解决实际问题，展示熟练度");
                    return highlight;
                })
                .collect(Collectors.toList());
        customization.setSkillHighlights(skillHighlights);

        // 结构建议
        customization.setStructuralSuggestions(Arrays.asList(
                "将最相关的项目经验放在简历前三位",
                "在个人总结中提及" + String.join("、", skillMatch.getMatchedSkills()),
                "针对职位要求，调整项目描述的侧重点"
        ));

        return customization;
    }
}
