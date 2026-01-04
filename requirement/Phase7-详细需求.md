# Phase 7 详细需求 - 求职管理模块

> **需求来源**: 用户需求 (2026-01-03)
> **参考模块**: Phase 2-6 学习模块
> **目标用户**: 正在求职或准备求职的工程师和职业经理人
> **核心价值**: 将学习成果转化为求职竞争力，提供从简历准备到面试复盘的全流程管理
> **状态**: 📋 需求阶段 → 设计阶段
> **计划版本**: v1.4
> **预计完成时间**: TBD

---

## 1. 背景与目标

### 1.1 背景

Phase 1-6 已完成学习体系构建:
- **Phase 1**: 用户管理和认证系统
- **Phase 2**: 技能体系框架（Skill + Focus Area + Learning Resources）
- **Phase 3**: 试题库基础功能
- **Phase 4**: 算法学习模块（三层结构 + 学习阶段）
- **Phase 5**: 系统设计模块（基础知识 + 典型案例）
- **Phase 6**: 通用技能学习（云计算、Behavioral 等）

**现状**: 用户已完成大量学习和练习，积累了丰富的知识和经验，但缺少将这些成果系统化整理并应用到求职过程的工具。

**痛点**:
1. 简历信息分散，难以针对不同公司/职位快速定制
2. 项目经验和人员管理经验未结构化整理，面试时难以快速回忆和表达
3. 公司和职位信息管理混乱，面试准备效率低
4. 面试问题和反馈未记录，无法有效复盘和改进
5. 学习内容（Phase 2-6）与求职实战脱节

### 1.2 核心目标

**Phase 7 目标**: 构建求职管理系统，实现"学习 → 实战 → 求职 → 反馈 → 改进"的完整闭环。

**关键能力**:
1. **简历管理** - 多版本简历，快速定制，一键导出
2. **经验库** - 结构化项目经验和人员管理经验，支持 Behavioral 面试
3. **公司档案** - 系统化管理目标公司、职位、面试流程、人脉
4. **面试跟踪** - 记录面试全过程，关联试题库，支持复盘分析

### 1.3 设计原则

**原则 1: 与现有模块深度集成**
- 简历 Skills 可参考 Phase 2-6 技能树
- 项目经验可作为 Behavioral 试题答案素材
- 面试问题可关联 Phase 3-6 试题库
- 定制简历根据 JD 自动匹配技能和经验

**原则 2: 结构化数据管理**
- 项目经验采用 What/Problem/How/Result 框架
- 人员管理经验采用 Manage Up/Cross-team/Team Growth 分类
- 面试流程分阶段管理，每个阶段关联技能准备清单

**原则 3: 多版本和定制化支持**
- 一份基础简历 → 多份定制简历
- 根据 JD 自动匹配和推荐内容调整
- 支持 PDF/Word/纯文本多种导出格式

**原则 4: 隐私和安全**
- 简历数据加密存储
- 敏感信息（联系方式、薪资期望）访问控制
- 支持简历匿名化（用于公开分享）

---

## 2. 功能需求

### 2.1 简历管理模块

#### F1 基本信息管理

**用户故事**:
> 作为求职者，我希望能够系统化管理我的简历基本信息，包括个人简介、联系方式、技能、教育背景、语言能力和兴趣爱好，以便快速创建针对不同职位的定制简历。

**功能详情**:

**F1.1 简历版本管理**
- 创建基础简历（Base Resume）
- 从基础简历复制创建定制简历
- 简历版本命名（如 "Google SRE 2026"，"Amazon SDE L6"）
- 设置默认简历（用于快速导出）
- 删除非默认简历
- 简历列表展示：版本名称、创建时间、最后修改时间、是否为默认

**F1.2 About 部分**
- 个人简介（Markdown 格式，支持富文本编辑）
- 职业目标（针对不同职位可调整）
- 字数限制：500 字

**F1.3 Contact & Links 部分**
- 联系方式：
  - 邮箱（必填）
  - 电话（可选）
  - 地址（可选，支持城市级别模糊）
- 社交链接：
  - LinkedIn URL
  - GitHub URL
  - 个人网站 URL
  - 其他链接（可自定义标题和 URL）

**F1.4 Skills 部分**
- 技能分类：
  - 技术技能（Programming Languages, Frameworks, Databases, Cloud Platforms）
  - 软技能（Leadership, Communication, Project Management）
  - 工具技能（Git, Docker, CI/CD, Kubernetes）
- 技能等级（可选）：Beginner, Intermediate, Advanced, Expert
- 从 Phase 2-6 技能树导入功能（可选）：
  - 点击"从学习技能导入"按钮
  - 显示用户已学习的 Skills 和 Focus Areas
  - 选择后自动添加到简历 Skills 列表
  - 可手动编辑或删除导入的技能

**F1.5 Education & Training 部分**
- 学历：
  - 学校名称
  - 学位（Bachelor, Master, PhD, Other）
  - 专业
  - 开始时间 - 结束时间
  - GPA（可选）
  - 主要课程（可选）
- 培训和证书：
  - 证书名称
  - 颁发机构
  - 获得时间
  - 有效期（可选）
  - 证书链接（可选）

**F1.6 Languages 部分**
- 语言名称
- 熟练程度：Native, Fluent, Professional, Limited

**F1.7 Hobbies 部分**
- 自由文本（最多 200 字）

**验收标准**:
- ✅ 可创建至少 10 个定制简历版本
- ✅ 技能从学习模块导入成功，可编辑和删除
- ✅ 所有字段支持 Markdown 格式（除数字和选项）
- ✅ 表单验证：必填字段不能为空，邮箱格式正确，URL 格式正确

---

#### F2 工作经历管理 (Experience)

**用户故事**:
> 作为求职者，我希望能够详细记录我的工作经历，包括公司、职位、职责、项目和成就，并能够关联到项目经验库，以便在简历中展示我的工作成果。

**功能详情**:

**F2.1 工作经历 CRUD**
- 添加工作经历：
  - 公司名称
  - 职位名称
  - 开始时间 - 结束时间（支持"至今"）
  - 所在地（城市、国家）
  - 职责描述（Markdown 格式，支持列表）
  - 主要成就（Markdown 格式，支持量化数据）
- 编辑工作经历
- 删除工作经历
- 工作经历排序（按时间倒序）

**F2.2 关联项目经验**
- 在工作经历中选择"添加项目"
- 从项目经验库中选择项目（下拉列表）
- 支持多个项目关联到同一工作经历
- 显示项目名称和简短描述
- 点击项目名称可跳转到项目经验详情

**F2.3 成就量化提示**
- 输入成就时，系统提示添加量化数据：
  - "提升性能 X%"
  - "节省成本 $X"
  - "支持 X 万用户"
  - "管理 X 人团队"
- 使用占位符或模板帮助用户结构化输入

**验收标准**:
- ✅ 可添加至少 10 条工作经历
- ✅ 每条工作经历可关联至少 5 个项目
- ✅ 职责和成就支持 Markdown 列表和粗体格式
- ✅ 时间验证：结束时间不能早于开始时间

---

#### F3 简历导出

**用户故事**:
> 作为求职者，我希望能够将简历导出为 PDF、Word 或纯文本格式，以便提交给不同的公司或平台。

**功能详情**:

**F3.1 导出格式**
- PDF（推荐格式）
  - 使用专业简历模板
  - 支持单栏或双栏布局
  - 字体：清晰易读（如 Arial, Helvetica）
  - 页边距：标准（1 inch）
- Word (.docx)
  - 可编辑格式
  - 保留 Markdown 格式（粗体、列表等）
- 纯文本 (.txt)
  - 用于 ATS（Applicant Tracking System）
  - 去除所有格式，保留纯文本内容

**F3.2 导出选项**
- 选择导出的版本（基础简历或定制简历）
- 选择导出格式
- 点击"下载"按钮生成文件
- 文件名格式：`{姓名}_{职位}_{日期}.{格式}` (如 `AustinXu_SRE_20260103.pdf`)

**F3.3 简历模板**
- 提供 2-3 个预设模板（可选）：
  - Modern（现代风格，简洁）
  - Classic（传统风格，正式）
  - Creative（创意风格，适合设计/产品岗位）
- 用户可选择模板并预览

**验收标准**:
- ✅ PDF 导出成功，格式专业美观
- ✅ Word 导出成功，可编辑
- ✅ 纯文本导出成功，去除所有格式
- ✅ 文件名自动生成，包含姓名、职位和日期

---

### 2.2 项目经验库模块

#### F4 技术项目经验管理

**用户故事**:
> 作为求职者，我希望能够结构化地总结我的技术项目经验，采用 What/Problem/How/Result 框架，以便在 Behavioral 面试中快速回忆和清晰表达。

**功能详情**:

**F4.1 项目经验 CRUD**
- 添加项目经验：
  - 项目名称（必填）
  - 项目类型（可选）：Feature Development, Performance Optimization, System Migration, Bug Fix, Infrastructure, Other
  - What/When/Who/Why（项目背景）：
    - 项目是什么
    - 时间范围（开始 - 结束）
    - 团队规模和角色
    - 项目背景和目标
  - Problem Statement & Challenges（问题定义和挑战）：
    - 遇到的核心问题
    - 主要挑战和难点
    - 约束条件（时间、资源、技术限制）
  - How（解决方案）：
    - 技术选型和理由
    - 架构设计
    - 创新点/差异化做法
    - 个人贡献（我的角色和具体工作）
  - Result（成果）：
    - 量化数据（性能提升、成本节约、用户增长等）
    - 业务影响
    - 团队/个人成长
    - 经验教训 (Lessons Learned)
- 编辑项目经验
- 删除项目经验
- 项目列表：按时间倒序排列

**F4.2 标签和分类**
- 技术标签（多选）：Java, Python, Kubernetes, AWS, Microservices, Database, etc.
- 技能关联（可选）：关联到 Phase 2-6 技能树中的 Skills
- 难度级别：Low, Medium, High, Critical

**F4.3 关联到 Behavioral 试题**
- 在项目详情页，显示"可用于回答的 Behavioral 问题"
- 推荐相关的 Behavioral 试题（从 Phase 6 试题库）
- 点击试题可跳转到试题答题页面，自动引用项目经验

**验收标准**:
- ✅ 可添加至少 20 个项目经验
- ✅ 每个项目的 What/Problem/How/Result 部分支持 Markdown 格式
- ✅ 项目可关联到工作经历（Experience）
- ✅ 项目可推荐相关 Behavioral 试题

---

#### F5 人员管理经验库

**用户故事**:
> 作为职业经理人或 Team Lead，我希望能够系统化地总结我的人员管理经验，包括向上管理、跨团队协作和团队建设，以便在 People Management 面试中展示我的领导力。

**功能详情**:

**F5.1 人员管理经验 CRUD**
- 添加人员管理经验：
  - 经验名称（必填）
  - 经验类型（必选）：Manage Up, Cross-team Management, Team Growth
  - 时间范围（开始 - 结束）
  - 背景和挑战（Markdown 格式）
  - 采取的行动（Markdown 格式）
  - 结果和影响（Markdown 格式，支持量化数据）
  - 经验教训（Lessons Learned）
- 编辑经验
- 删除经验
- 经验列表：按类型分组，按时间倒序

**F5.2 Team Growth 细分**
- Hiring（招聘经验）：
  - 招聘人数
  - 面试流程设计
  - 关键招聘决策
  - 招聘结果
- High Performer（培养高绩效员工）：
  - 识别高潜力员工的方法
  - 培养计划
  - 成长成果
- Low Performer（处理低绩效员工）：
  - 问题识别
  - 改进计划（PIP）
  - 结果（改进成功或离职）

**F5.3 关联到 People Management 试题**
- 在经验详情页，显示"可用于回答的 People Management 问题"
- 推荐相关的试题（从 Phase 6 试题库）
- 点击试题可跳转到试题答题页面，自动引用管理经验

**验收标准**:
- ✅ 可添加至少 15 条人员管理经验
- ✅ 每条经验的背景/行动/结果部分支持 Markdown 格式
- ✅ Team Growth 细分为 Hiring/High Performer/Low Performer
- ✅ 经验可关联到 People Management 试题

---

### 2.3 公司与职位管理模块

#### F6 公司档案管理

**用户故事**:
> 作为求职者，我希望能够为每个目标公司建立档案，记录公司信息、企业文化、有用链接，以便在面试准备时快速了解公司背景。

**功能详情**:

**F6.1 公司信息 CRUD**
- 添加公司：
  - 公司名称（必填）
  - 公司简介（业务领域、规模、发展历史）
  - 企业文化（价值观、工作氛围）
    - 可关联到 Behavioral 问题准备
    - 例如：Amazon 的 Leadership Principles
  - 所在地（总部城市、国家）
  - 公司规模（员工数）
  - 行业分类（Technology, Finance, Healthcare, etc.）
- 编辑公司信息
- 删除公司（级联删除关联的职位、面试记录等）

**F6.2 有用链接管理**
- 添加链接：
  - 链接标题（如 "技术博客"，"工程博客"，"Glassdoor 评价"）
  - URL
  - 备注（可选）
- 链接列表显示：标题、URL（可点击跳转）
- 编辑链接
- 删除链接

**F6.3 公司列表**
- 显示所有目标公司
- 排序：按创建时间倒序
- 筛选：按行业分类、所在地
- 搜索：按公司名称搜索

**验收标准**:
- ✅ 可添加至少 20 个公司档案
- ✅ 公司信息支持 Markdown 格式
- ✅ 每个公司可添加至少 10 个有用链接
- ✅ 公司列表支持搜索和筛选

---

#### F7 职位管理

**用户故事**:
> 作为求职者，我希望能够为每个目标公司管理多个职位，记录 JD、面试流程、Recruiter 信息，以便系统化地准备每个职位的面试。

**功能详情**:

**F7.1 职位信息 CRUD**
- 添加职位：
  - 职位名称（必填，如 "Senior SRE"，"Staff Software Engineer"）
  - 职位级别（可选）：Junior, Mid, Senior, Staff, Principal, etc.
  - 发布时间
  - 职位链接（JD URL）
  - 申请状态：Not Applied, Applied, Phone Screen, Onsite, Offer, Rejected
  - JD 核心内容：
    - **Qualifications**（技能要求，Markdown 列表）
    - **Responsibilities**（岗位职责，Markdown 列表）
  - 备注（可选）
- 编辑职位信息
- 删除职位（级联删除面试记录）

**F7.2 面试流程管理**
- 定义面试流程（每个职位可自定义）：
  - 阶段 1：Phone Screen
    - 关联技能（从 Phase 2-6 选择）：如 Behavioral, Coding
    - 准备重点（Markdown）
  - 阶段 2：Technical Interview
    - 关联技能：如 Algorithm, System Design
    - 准备重点
  - 阶段 3：Onsite Rounds
    - Round 1: Coding
    - Round 2: System Design
    - Round 3: Behavioral
    - Round 4: Leadership (可选)
  - 每个阶段可添加/删除/编辑
- 面试流程模板（可选）：
  - 提供常见面试流程模板（SRE, SDE, EM, PM）
  - 用户可从模板创建并自定义

**F7.3 JD 技能匹配（P1 智能化功能，Phase 7.1 暂不实现）**
- 分析 JD Qualifications
- 对比简历 Skills
- 给出匹配度评分
- 推荐需要补充的技能

**验收标准**:
- ✅ 每个公司可添加至少 5 个职位
- ✅ 每个职位的 JD 支持 Markdown 格式
- ✅ 面试流程可自定义，至少支持 5 个阶段
- ✅ 面试流程可关联到 Phase 2-6 技能树

---

#### F8 Recruiter 管理

**用户故事**:
> 作为求职者，我希望能够记录招聘人员的联系方式和沟通记录，以便在求职过程中保持良好的沟通。

**功能详情**:

**F8.1 Recruiter 信息 CRUD**
- 添加 Recruiter：
  - 姓名（必填）
  - 所属公司（从公司列表选择）
  - 邮箱
  - 电话（可选）
  - LinkedIn URL（可选）
  - 备注（可选）
- 编辑 Recruiter 信息
- 删除 Recruiter

**F8.2 沟通记录**
- 添加沟通记录：
  - 关联到 Recruiter 和职位
  - 沟通时间
  - 沟通方式（Email, Phone, LinkedIn Message, In-person）
  - 沟通内容（Markdown）
  - 下一步行动（可选）
- 沟通记录列表：按时间倒序
- 编辑沟通记录
- 删除沟通记录

**F8.3 Recruiter 列表**
- 按公司分组显示
- 显示最近沟通时间
- 搜索：按姓名或公司搜索

**验收标准**:
- ✅ 可添加至少 10 个 Recruiter
- ✅ 每个 Recruiter 可添加至少 5 条沟通记录
- ✅ 沟通记录支持 Markdown 格式
- ✅ Recruiter 列表支持按公司分组和搜索

---

#### F9 人脉推荐管理

**用户故事**:
> 作为求职者，我希望能够管理在目标公司工作的熟人（前同事、同学、朋友），并跟踪推荐状态，以便获得内部推荐机会。

**功能详情**:

**F9.1 推荐人信息 CRUD**
- 添加推荐人：
  - 姓名（必填）
  - 关系（Former Colleague, Classmate, Friend, Mentor, Other）
  - 所属公司（从公司列表选择）
  - 职位（可选）
  - 联系方式（邮箱、电话、LinkedIn）
  - 备注（如何认识、关系亲密度）
- 编辑推荐人信息
- 删除推荐人

**F9.2 推荐状态跟踪**
- 推荐状态：
  - Not Requested（未请求）
  - Requested（已请求）
  - Agreed（同意推荐）
  - Declined（拒绝推荐）
  - Submitted（已提交推荐）
- 推荐请求时间
- 推荐提交时间
- 推荐结果（可选）：Interview Scheduled, No Response, Rejected

**F9.3 推荐人列表**
- 按公司分组显示
- 按推荐状态筛选
- 显示关系和联系方式
- 搜索：按姓名或公司搜索

**验收标准**:
- ✅ 可添加至少 20 个推荐人
- ✅ 推荐状态可追踪和更新
- ✅ 推荐人列表支持按公司分组和状态筛选
- ✅ 推荐人列表支持搜索

---

### 2.4 定制简历生成模块

#### F10 定制简历生成

**用户故事**:
> 作为求职者，我希望能够根据职位的 JD 自动生成定制简历，系统能够自动匹配我的技能和项目经验，并推荐需要调整的内容，以便快速准备针对性的简历。

**功能详情**:

**F10.1 简历生成流程**
- 选择目标公司和职位（从公司职位列表）
- 选择基础简历版本（作为模板）
- 点击"生成定制简历"按钮
- 系统执行：
  1. 分析 JD Qualifications（提取关键技能）
  2. 匹配简历 Skills（高亮匹配的技能）
  3. 匹配项目经验（推荐相关项目）
  4. 生成新的简历版本（自动命名：`{公司名}_{职位名}_{日期}`）
- 显示生成结果：
  - 匹配度评分（P1 智能化功能，Phase 7.1 暂不实现）
  - 推荐调整的内容（P1 智能化功能，Phase 7.1 暂不实现）

**F10.2 手动调整**
- 生成的定制简历可编辑
- 调整 About 部分（突出与职位相关的目标）
- 调整 Skills 顺序（将匹配 JD 的技能前置）
- 调整 Experience 部分：
  - 选择展示哪些工作经历
  - 选择展示哪些项目
  - 调整项目描述（突出与 JD 相关的技术和成果）
- 保存调整后的简历

**F10.3 对比视图**
- 显示基础简历 vs 定制简历的对比
- 高亮差异部分
- 支持切换视图（并排对比 or 单独查看）

**验收标准**:
- ✅ 可为每个职位生成定制简历
- ✅ 定制简历自动命名
- ✅ 定制简历可手动编辑和调整
- ✅ 对比视图清晰展示差异

---

### 2.5 面试记录跟踪模块

#### F11 面试准备管理

**用户故事**:
> 作为求职者，我希望能够查看面试流程，并根据每个阶段的准备重点生成技能准备清单，以便系统化地准备面试。

**功能详情**:

**F11.1 准备清单生成**
- 选择公司和职位
- 显示面试流程（从 F7.2 定义的流程）
- 对于每个阶段：
  - 显示关联的技能（从 Phase 2-6 技能树）
  - 显示准备重点（Markdown 内容）
  - 生成准备清单：
    - 技能复习（链接到 Phase 2-6 学习内容）
    - 试题练习（链接到 Phase 3-6 试题库）
    - 项目经验回顾（链接到项目经验库）
- 标记清单项为"已完成"

**F11.2 面试资料整理**
- 自动整理面试相关资料：
  - JD（Qualifications + Responsibilities）
  - 定制简历
  - 公司信息和企业文化
  - 相关试题列表
  - 相关项目经验列表
- 一键导出为 PDF（面试资料包）

**验收标准**:
- ✅ 准备清单根据面试流程自动生成
- ✅ 清单项可链接到学习内容和试题库
- ✅ 清单项可标记为完成
- ✅ 面试资料包可导出为 PDF

---

#### F12 面试记录管理

**用户故事**:
> 作为求职者，我希望能够详细记录每一轮面试的信息，包括面试官、问题列表、自我评估和反馈，以便面试后复盘和改进。

**功能详情**:

**F12.1 面试记录 CRUD**
- 添加面试记录：
  - 关联公司和职位
  - 面试阶段（从面试流程选择，如 Phone Screen, Onsite Round 1）
  - 面试时间
  - 面试官信息：
    - 姓名
    - 职位
    - LinkedIn URL（可选）
  - 面试形式（Video Call, In-person, Phone）
  - 面试时长（分钟）
- 编辑面试记录
- 删除面试记录

**F12.2 问题列表记录**
- 在面试记录中添加问题：
  - 问题描述
  - 问题类型（Coding, System Design, Behavioral, Technical, Other）
  - 我的回答（Markdown）
  - 关联试题（可选）：
    - 从 Phase 3-6 试题库选择
    - 如果题库中没有，可创建新试题并添加到题库
  - 回答质量自我评估（1-5 分）
- 问题列表显示：按提问顺序排列
- 编辑问题
- 删除问题

**F12.3 自我评估和反馈**
- 自我评估：
  - 整体表现（1-5 分）
  - 技术深度（1-5 分）
  - 沟通表达（1-5 分）
  - 问题回答（1-5 分）
  - 总结（Markdown）：做得好的地方、需要改进的地方
- 面试官反馈（可选）：
  - 反馈时间
  - 反馈内容（Markdown）
  - 反馈来源（Recruiter, Hiring Manager, etc.）

**F12.4 面试记录列表**
- 按公司和职位分组
- 显示面试时间、阶段、面试官
- 按时间倒序排列
- 筛选：按公司、职位、面试阶段
- 搜索：按面试官姓名或问题关键词

**验收标准**:
- ✅ 可为每个职位添加至少 5 条面试记录
- ✅ 每条面试记录可添加至少 10 个问题
- ✅ 问题可关联到 Phase 3-6 试题库
- ✅ 自我评估和反馈支持 Markdown 格式
- ✅ 面试记录列表支持筛选和搜索

---

#### F13 面试结果跟踪

**用户故事**:
> 作为求职者，我希望能够跟踪每个职位的面试结果，包括 Offer、Reject 或 Pending 状态，并分析拒绝原因，以便改进后续面试表现。

**功能详情**:

**F13.1 结果状态管理**
- 职位申请状态（从 F7.1）：
  - Not Applied
  - Applied
  - Phone Screen
  - Onsite
  - Offer
  - Rejected
- 状态更新时间（自动记录）
- 状态变更历史（时间线）

**F13.2 Offer 详情（如果状态为 Offer）**
- Offer 接收时间
- 薪资信息（可选，加密存储）：
  - Base Salary
  - Bonus
  - Stock/RSU
  - Total Compensation
- Offer 截止时间
- Offer 决策：Accepted, Declined, Pending
- 决策备注（Markdown）

**F13.3 拒绝原因分析（如果状态为 Rejected）**
- 拒绝时间
- 拒绝阶段（Phone Screen, Onsite, After Onsite, etc.）
- 拒绝原因（可选）：
  - 技术深度不足
  - 系统设计薄弱
  - Behavioral 表现不佳
  - 文化匹配度低
  - 其他（自定义）
- 改进计划（Markdown）：
  - 需要加强的技能
  - 需要复习的知识点
  - 需要练习的试题类型
  - 关联到 Phase 2-6 学习内容

**F13.4 面试复盘报告（自动生成）**
- 统计数据：
  - 申请职位总数
  - Phone Screen 通过率
  - Onsite 通过率
  - Offer 数量
  - 拒绝数量
- 拒绝原因分析（饼图/柱状图）
- 薄弱技能识别（根据拒绝原因和面试评估）
- 推荐学习内容（链接到 Phase 2-6）

**验收标准**:
- ✅ 职位申请状态可跟踪和更新
- ✅ Offer 详情可记录（薪资加密存储）
- ✅ 拒绝原因可分析，改进计划可制定
- ✅ 面试复盘报告自动生成，包含统计数据和推荐

---

## 3. 数据模型设计

### 3.1 核心实体

**简历管理**:
1. `resumes` - 简历主表
2. `resume_experiences` - 工作经历
3. `resume_skills` - 技能列表
4. `resume_education` - 教育背景
5. `resume_certifications` - 培训和证书
6. `resume_languages` - 语言能力

**项目经验库**:
7. `project_experiences` - 技术项目经验
8. `management_experiences` - 人员管理经验

**公司与职位**:
9. `companies` - 公司档案
10. `company_links` - 公司有用链接
11. `job_applications` - 职位申请
12. `interview_stages` - 面试流程阶段
13. `recruiters` - 招聘人员
14. `recruiter_communications` - 招聘沟通记录
15. `referrals` - 内推人脉

**面试记录**:
16. `interview_records` - 面试记录
17. `interview_questions` - 面试问题
18. `interview_feedback` - 面试反馈

### 3.2 表结构详细设计

#### 3.2.1 resumes (简历主表)

```sql
CREATE TABLE resumes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  version_name VARCHAR(100) NOT NULL COMMENT '简历版本名称',
  is_default BOOLEAN DEFAULT FALSE COMMENT '是否为默认简历',
  about TEXT COMMENT '个人简介（Markdown）',
  career_objective TEXT COMMENT '职业目标（Markdown）',

  -- Contact & Links
  email VARCHAR(255) NOT NULL COMMENT '邮箱',
  phone VARCHAR(50) COMMENT '电话',
  address VARCHAR(255) COMMENT '地址',
  linkedin_url VARCHAR(500) COMMENT 'LinkedIn URL',
  github_url VARCHAR(500) COMMENT 'GitHub URL',
  website_url VARCHAR(500) COMMENT '个人网站',
  other_links JSON COMMENT '其他链接（JSON数组）',

  -- Languages & Hobbies
  languages JSON COMMENT '语言能力（JSON数组）',
  hobbies TEXT COMMENT '兴趣爱好',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uk_user_version (user_id, version_name),
  INDEX idx_user (user_id),
  INDEX idx_default (user_id, is_default)
) COMMENT '简历主表';
```

**字段说明**:
- `version_name`: 简历版本名称，如 "Google SRE 2026"
- `is_default`: 每个用户只能有一个默认简历
- `other_links`: JSON 格式，`[{"title": "博客", "url": "https://..."}]`
- `languages`: JSON 格式，`[{"name": "English", "proficiency": "Fluent"}]`

---

#### 3.2.2 resume_experiences (工作经历)

```sql
CREATE TABLE resume_experiences (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  resume_id BIGINT NOT NULL COMMENT '简历ID',
  company_name VARCHAR(255) NOT NULL COMMENT '公司名称',
  position VARCHAR(255) NOT NULL COMMENT '职位名称',
  location VARCHAR(255) COMMENT '所在地（城市、国家）',
  start_date DATE NOT NULL COMMENT '开始时间',
  end_date DATE COMMENT '结束时间（NULL表示至今）',
  is_current BOOLEAN DEFAULT FALSE COMMENT '是否为当前工作',
  responsibilities TEXT COMMENT '职责描述（Markdown）',
  achievements TEXT COMMENT '主要成就（Markdown）',

  -- 关联项目经验
  project_ids JSON COMMENT '关联的项目经验ID列表（JSON数组）',

  sort_order INT DEFAULT 0 COMMENT '排序（按时间倒序）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
  INDEX idx_resume (resume_id),
  INDEX idx_dates (start_date, end_date)
) COMMENT '工作经历';
```

**字段说明**:
- `is_current`: 当前工作，end_date 为 NULL
- `project_ids`: JSON 数组，`[1, 3, 5]`，关联到 `project_experiences` 表

---

#### 3.2.3 resume_skills (技能列表)

```sql
CREATE TABLE resume_skills (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  resume_id BIGINT NOT NULL COMMENT '简历ID',
  skill_category VARCHAR(50) NOT NULL COMMENT '技能分类：Technical, Soft, Tools',
  skill_name VARCHAR(255) NOT NULL COMMENT '技能名称',
  proficiency VARCHAR(50) COMMENT '熟练程度：Beginner, Intermediate, Advanced, Expert',
  sort_order INT DEFAULT 0 COMMENT '排序',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
  INDEX idx_resume (resume_id),
  INDEX idx_category (resume_id, skill_category)
) COMMENT '简历技能列表';
```

---

#### 3.2.4 resume_education (教育背景)

```sql
CREATE TABLE resume_education (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  resume_id BIGINT NOT NULL COMMENT '简历ID',
  school_name VARCHAR(255) NOT NULL COMMENT '学校名称',
  degree VARCHAR(100) NOT NULL COMMENT '学位：Bachelor, Master, PhD, Other',
  major VARCHAR(255) COMMENT '专业',
  start_date DATE COMMENT '开始时间',
  end_date DATE COMMENT '结束时间',
  gpa DECIMAL(3,2) COMMENT 'GPA（如3.8）',
  courses TEXT COMMENT '主要课程（Markdown）',
  sort_order INT DEFAULT 0 COMMENT '排序',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
  INDEX idx_resume (resume_id)
) COMMENT '教育背景';
```

---

#### 3.2.5 resume_certifications (培训和证书)

```sql
CREATE TABLE resume_certifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  resume_id BIGINT NOT NULL COMMENT '简历ID',
  cert_name VARCHAR(255) NOT NULL COMMENT '证书名称',
  issuer VARCHAR(255) NOT NULL COMMENT '颁发机构',
  issue_date DATE COMMENT '获得时间',
  expiry_date DATE COMMENT '有效期（NULL表示永久有效）',
  cert_url VARCHAR(500) COMMENT '证书链接',
  sort_order INT DEFAULT 0 COMMENT '排序',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
  INDEX idx_resume (resume_id)
) COMMENT '培训和证书';
```

---

#### 3.2.6 project_experiences (技术项目经验)

```sql
CREATE TABLE project_experiences (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  project_name VARCHAR(255) NOT NULL COMMENT '项目名称',
  project_type VARCHAR(50) COMMENT '项目类型：Feature, Optimization, Migration, Bug Fix, Infrastructure, Other',

  -- What/When/Who/Why
  what_description TEXT COMMENT '项目是什么（Markdown）',
  start_date DATE COMMENT '开始时间',
  end_date DATE COMMENT '结束时间',
  team_size INT COMMENT '团队规模',
  my_role VARCHAR(255) COMMENT '我的角色',
  background TEXT COMMENT '项目背景和目标（Markdown）',

  -- Problem Statement & Challenges
  problem_statement TEXT COMMENT '核心问题（Markdown）',
  challenges TEXT COMMENT '主要挑战和难点（Markdown）',
  constraints TEXT COMMENT '约束条件（Markdown）',

  -- How
  tech_stack TEXT COMMENT '技术选型和理由（Markdown）',
  architecture TEXT COMMENT '架构设计（Markdown）',
  innovation TEXT COMMENT '创新点/差异化做法（Markdown）',
  my_contribution TEXT COMMENT '个人贡献（Markdown）',

  -- Result
  quantitative_results TEXT COMMENT '量化数据（Markdown）',
  business_impact TEXT COMMENT '业务影响（Markdown）',
  personal_growth TEXT COMMENT '团队/个人成长（Markdown）',
  lessons_learned TEXT COMMENT '经验教训（Markdown）',

  -- 标签和分类
  tech_tags JSON COMMENT '技术标签（JSON数组）',
  skill_ids JSON COMMENT '关联的技能ID（JSON数组，关联到skills表）',
  difficulty VARCHAR(50) COMMENT '难度级别：Low, Medium, High, Critical',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user (user_id),
  INDEX idx_dates (start_date, end_date)
) COMMENT '技术项目经验';
```

**字段说明**:
- `tech_tags`: JSON 数组，`["Java", "Kubernetes", "AWS"]`
- `skill_ids`: JSON 数组，`[1, 3, 5]`，关联到 `skills` 表

---

#### 3.2.7 management_experiences (人员管理经验)

```sql
CREATE TABLE management_experiences (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  experience_name VARCHAR(255) NOT NULL COMMENT '经验名称',
  experience_type VARCHAR(50) NOT NULL COMMENT '经验类型：ManageUp, CrossTeam, TeamGrowth',

  -- Team Growth 细分（仅当 experience_type = 'TeamGrowth' 时使用）
  team_growth_subtype VARCHAR(50) COMMENT 'Hiring, HighPerformer, LowPerformer',

  start_date DATE COMMENT '开始时间',
  end_date DATE COMMENT '结束时间',

  background TEXT COMMENT '背景和挑战（Markdown）',
  actions_taken TEXT COMMENT '采取的行动（Markdown）',
  results TEXT COMMENT '结果和影响（Markdown）',
  lessons_learned TEXT COMMENT '经验教训（Markdown）',

  -- Team Growth 特定字段
  hiring_count INT COMMENT '招聘人数（仅 Hiring）',
  improvement_result VARCHAR(50) COMMENT '改进结果（仅 LowPerformer）：Improved, Terminated',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user (user_id),
  INDEX idx_type (user_id, experience_type)
) COMMENT '人员管理经验';
```

---

#### 3.2.8 companies (公司档案)

```sql
CREATE TABLE companies (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  company_name VARCHAR(255) NOT NULL COMMENT '公司名称',
  company_description TEXT COMMENT '公司简介（Markdown）',
  company_culture TEXT COMMENT '企业文化（Markdown）',
  location VARCHAR(255) COMMENT '所在地（总部城市、国家）',
  company_size VARCHAR(50) COMMENT '公司规模：<50, 50-200, 200-1000, 1000-5000, >5000',
  industry VARCHAR(100) COMMENT '行业分类：Technology, Finance, Healthcare, etc.',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uk_user_company (user_id, company_name),
  INDEX idx_user (user_id),
  INDEX idx_industry (industry)
) COMMENT '公司档案';
```

---

#### 3.2.9 company_links (公司有用链接)

```sql
CREATE TABLE company_links (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT NOT NULL COMMENT '公司ID',
  link_title VARCHAR(255) NOT NULL COMMENT '链接标题',
  link_url VARCHAR(500) NOT NULL COMMENT 'URL',
  notes TEXT COMMENT '备注',
  sort_order INT DEFAULT 0 COMMENT '排序',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
  INDEX idx_company (company_id)
) COMMENT '公司有用链接';
```

---

#### 3.2.10 job_applications (职位申请)

```sql
CREATE TABLE job_applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  company_id BIGINT NOT NULL COMMENT '公司ID',
  position_name VARCHAR(255) NOT NULL COMMENT '职位名称',
  position_level VARCHAR(50) COMMENT '职位级别：Junior, Mid, Senior, Staff, Principal',
  posted_date DATE COMMENT '发布时间',
  job_url VARCHAR(500) COMMENT '职位链接',

  -- JD 核心内容
  qualifications TEXT COMMENT '技能要求（Markdown）',
  responsibilities TEXT COMMENT '岗位职责（Markdown）',

  -- 申请状态
  application_status VARCHAR(50) DEFAULT 'NotApplied' COMMENT '申请状态：NotApplied, Applied, PhoneScreen, Onsite, Offer, Rejected',
  status_updated_at TIMESTAMP COMMENT '状态更新时间',
  status_history JSON COMMENT '状态变更历史（JSON数组）',

  -- Offer 详情（如果状态为 Offer）
  offer_received_at TIMESTAMP COMMENT 'Offer 接收时间',
  base_salary DECIMAL(12,2) COMMENT 'Base Salary（加密存储）',
  bonus DECIMAL(12,2) COMMENT 'Bonus',
  stock_value DECIMAL(12,2) COMMENT 'Stock/RSU',
  total_compensation DECIMAL(12,2) COMMENT 'Total Compensation',
  offer_deadline DATE COMMENT 'Offer 截止时间',
  offer_decision VARCHAR(50) COMMENT 'Offer 决策：Accepted, Declined, Pending',
  offer_notes TEXT COMMENT '决策备注（Markdown）',

  -- 拒绝详情（如果状态为 Rejected）
  rejected_at TIMESTAMP COMMENT '拒绝时间',
  rejected_stage VARCHAR(50) COMMENT '拒绝阶段：PhoneScreen, Onsite, AfterOnsite',
  rejection_reasons JSON COMMENT '拒绝原因（JSON数组）',
  improvement_plan TEXT COMMENT '改进计划（Markdown）',

  notes TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
  UNIQUE KEY uk_user_company_position (user_id, company_id, position_name),
  INDEX idx_user (user_id),
  INDEX idx_company (company_id),
  INDEX idx_status (application_status)
) COMMENT '职位申请';
```

**字段说明**:
- `status_history`: JSON 数组，`[{"status": "Applied", "timestamp": "2026-01-01T10:00:00Z"}]`
- `rejection_reasons`: JSON 数组，`["技术深度不足", "系统设计薄弱"]`

---

#### 3.2.11 interview_stages (面试流程阶段)

```sql
CREATE TABLE interview_stages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_application_id BIGINT NOT NULL COMMENT '职位申请ID',
  stage_name VARCHAR(255) NOT NULL COMMENT '阶段名称：Phone Screen, Technical Interview, Onsite Round 1, etc.',
  stage_order INT NOT NULL COMMENT '阶段顺序',

  -- 关联技能
  skill_ids JSON COMMENT '关联的技能ID（JSON数组，关联到skills表）',
  preparation_notes TEXT COMMENT '准备重点（Markdown）',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE CASCADE,
  INDEX idx_job (job_application_id),
  INDEX idx_order (job_application_id, stage_order)
) COMMENT '面试流程阶段';
```

---

#### 3.2.12 recruiters (招聘人员)

```sql
CREATE TABLE recruiters (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  company_id BIGINT NOT NULL COMMENT '所属公司ID',
  recruiter_name VARCHAR(255) NOT NULL COMMENT '姓名',
  email VARCHAR(255) COMMENT '邮箱',
  phone VARCHAR(50) COMMENT '电话',
  linkedin_url VARCHAR(500) COMMENT 'LinkedIn URL',
  notes TEXT COMMENT '备注',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
  INDEX idx_user (user_id),
  INDEX idx_company (company_id)
) COMMENT '招聘人员';
```

---

#### 3.2.13 recruiter_communications (招聘沟通记录)

```sql
CREATE TABLE recruiter_communications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  recruiter_id BIGINT NOT NULL COMMENT 'Recruiter ID',
  job_application_id BIGINT COMMENT '关联的职位申请ID（可选）',

  communication_date TIMESTAMP NOT NULL COMMENT '沟通时间',
  communication_method VARCHAR(50) NOT NULL COMMENT '沟通方式：Email, Phone, LinkedIn, InPerson',
  communication_content TEXT COMMENT '沟通内容（Markdown）',
  next_action TEXT COMMENT '下一步行动',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (recruiter_id) REFERENCES recruiters(id) ON DELETE CASCADE,
  FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE SET NULL,
  INDEX idx_recruiter (recruiter_id),
  INDEX idx_date (communication_date)
) COMMENT '招聘沟通记录';
```

---

#### 3.2.14 referrals (内推人脉)

```sql
CREATE TABLE referrals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  company_id BIGINT NOT NULL COMMENT '所属公司ID',
  referral_name VARCHAR(255) NOT NULL COMMENT '推荐人姓名',
  relationship VARCHAR(50) NOT NULL COMMENT '关系：FormerColleague, Classmate, Friend, Mentor, Other',
  position VARCHAR(255) COMMENT '职位',

  email VARCHAR(255) COMMENT '邮箱',
  phone VARCHAR(50) COMMENT '电话',
  linkedin_url VARCHAR(500) COMMENT 'LinkedIn URL',
  notes TEXT COMMENT '备注（如何认识、关系亲密度）',

  -- 推荐状态
  referral_status VARCHAR(50) DEFAULT 'NotRequested' COMMENT '推荐状态：NotRequested, Requested, Agreed, Declined, Submitted',
  request_date DATE COMMENT '推荐请求时间',
  submission_date DATE COMMENT '推荐提交时间',
  referral_result VARCHAR(50) COMMENT '推荐结果：InterviewScheduled, NoResponse, Rejected',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
  INDEX idx_user (user_id),
  INDEX idx_company (company_id),
  INDEX idx_status (referral_status)
) COMMENT '内推人脉';
```

---

#### 3.2.15 interview_records (面试记录)

```sql
CREATE TABLE interview_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_application_id BIGINT NOT NULL COMMENT '职位申请ID',
  interview_stage_id BIGINT COMMENT '面试阶段ID（可选，关联到interview_stages）',

  interview_date TIMESTAMP NOT NULL COMMENT '面试时间',
  interviewer_name VARCHAR(255) COMMENT '面试官姓名',
  interviewer_position VARCHAR(255) COMMENT '面试官职位',
  interviewer_linkedin VARCHAR(500) COMMENT '面试官LinkedIn',

  interview_format VARCHAR(50) NOT NULL COMMENT '面试形式：VideoCall, InPerson, Phone',
  interview_duration INT COMMENT '面试时长（分钟）',

  -- 自我评估
  overall_performance INT COMMENT '整体表现（1-5）',
  technical_depth INT COMMENT '技术深度（1-5）',
  communication INT COMMENT '沟通表达（1-5）',
  problem_solving INT COMMENT '问题回答（1-5）',
  self_summary TEXT COMMENT '自我总结（Markdown）',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (job_application_id) REFERENCES job_applications(id) ON DELETE CASCADE,
  FOREIGN KEY (interview_stage_id) REFERENCES interview_stages(id) ON DELETE SET NULL,
  INDEX idx_job (job_application_id),
  INDEX idx_date (interview_date)
) COMMENT '面试记录';
```

---

#### 3.2.16 interview_questions (面试问题)

```sql
CREATE TABLE interview_questions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  interview_record_id BIGINT NOT NULL COMMENT '面试记录ID',
  question_order INT NOT NULL COMMENT '问题顺序',

  question_description TEXT NOT NULL COMMENT '问题描述',
  question_type VARCHAR(50) NOT NULL COMMENT '问题类型：Coding, SystemDesign, Behavioral, Technical, Other',

  my_answer TEXT COMMENT '我的回答（Markdown）',
  related_question_id BIGINT COMMENT '关联的试题ID（关联到questions表）',

  answer_quality INT COMMENT '回答质量自我评估（1-5）',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (interview_record_id) REFERENCES interview_records(id) ON DELETE CASCADE,
  FOREIGN KEY (related_question_id) REFERENCES questions(id) ON DELETE SET NULL,
  INDEX idx_record (interview_record_id),
  INDEX idx_type (question_type)
) COMMENT '面试问题';
```

---

#### 3.2.17 interview_feedback (面试反馈)

```sql
CREATE TABLE interview_feedback (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  interview_record_id BIGINT NOT NULL COMMENT '面试记录ID',

  feedback_date TIMESTAMP NOT NULL COMMENT '反馈时间',
  feedback_source VARCHAR(100) COMMENT '反馈来源：Recruiter, HiringManager, etc.',
  feedback_content TEXT COMMENT '反馈内容（Markdown）',

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (interview_record_id) REFERENCES interview_records(id) ON DELETE CASCADE,
  INDEX idx_record (interview_record_id)
) COMMENT '面试反馈';
```

---

### 3.3 数据关系图

```
users (用户)
  ├── resumes (简历)
  │     ├── resume_experiences (工作经历)
  │     ├── resume_skills (技能列表)
  │     ├── resume_education (教育背景)
  │     └── resume_certifications (培训证书)
  ├── project_experiences (项目经验)
  ├── management_experiences (人员管理经验)
  ├── companies (公司档案)
  │     ├── company_links (公司链接)
  │     ├── job_applications (职位申请)
  │     │     ├── interview_stages (面试流程)
  │     │     └── interview_records (面试记录)
  │     │           ├── interview_questions (面试问题)
  │     │           └── interview_feedback (面试反馈)
  │     ├── recruiters (招聘人员)
  │     │     └── recruiter_communications (沟通记录)
  │     └── referrals (内推人脉)
  └── skills (Phase 2-6 技能树) [已存在]
        └── questions (Phase 3-6 试题库) [已存在]
```

---

## 4. 业务规则

### 4.1 简历管理规则

**R1: 简历版本管理**
- 每个用户可创建多个简历版本，版本名称必须唯一
- 每个用户只能有一个默认简历（is_default=true）
- 删除简历时，如果是默认简历，提示用户先设置其他简历为默认
- 复制简历时，新版本自动命名为 "{原版本名}_Copy_{日期}"

**R2: 技能导入**
- 从 Phase 2-6 技能树导入时，只导入用户已学习的技能（有学习记录或答题记录）
- 导入后的技能可编辑和删除，不影响原技能树
- 技能分类（Technical/Soft/Tools）由用户手动选择，不自动映射

**R3: 简历导出**
- PDF 导出时，检查必填字段是否完整（About, Email,至少1个技能，至少1个教育背景）
- 导出文件名自动生成，格式：`{姓名}_{职位}_{日期}.{格式}`
- 薪资信息在导出时可选择隐藏（用于公开分享）

### 4.2 项目经验管理规则

**R4: 项目经验关联**
- 项目经验可关联到多个工作经历（Experience）
- 项目经验可关联到多个 Behavioral 试题（从 Phase 6 试题库）
- 删除项目经验时，检查是否有工作经历关联，提示用户确认

**R5: 标签和分类**
- 技术标签（tech_tags）为自由文本，支持自动补全（基于已有标签）
- 难度级别（Low/Medium/High/Critical）影响项目在简历中的展示顺序

### 4.3 公司与职位管理规则

**R6: 公司和职位关系**
- 一个公司可有多个职位申请
- 删除公司时，级联删除关联的职位、面试记录、Recruiter、人脉
- 职位申请状态变更时，自动记录到 status_history（JSON 数组）

**R7: 面试流程管理**
- 面试流程阶段（interview_stages）按 stage_order 排序
- 面试流程可从模板创建（SRE/SDE/EM/PM 常见流程）
- 面试记录（interview_records）可关联到面试流程阶段

**R8: Recruiter 和人脉管理**
- Recruiter 和人脉（referrals）都关联到公司（company_id）
- 一个公司可有多个 Recruiter 和多个推荐人
- 推荐状态（referral_status）变更时，自动更新时间戳

### 4.4 面试记录管理规则

**R9: 面试问题关联**
- 面试问题（interview_questions）可关联到试题库（questions表）
- 如果试题库中没有该问题，用户可创建新试题并添加到试题库
- 面试问题的 my_answer 字段可为空（面试后补充）

**R10: 自我评估和反馈**
- 自我评估（overall_performance, technical_depth 等）为 1-5 分
- 面试反馈（interview_feedback）为可选字段，Recruiter 提供时填写
- 面试记录创建后，提醒用户及时填写自我评估

**R11: 面试结果跟踪**
- 职位申请状态（application_status）变更为 Offer 时，必须填写 offer_received_at
- 职位申请状态变更为 Rejected 时，必须填写 rejected_at 和 rejected_stage
- 拒绝原因（rejection_reasons）可多选，存储为 JSON 数组

### 4.5 权限和可见性规则

**R12: 数据隐私**
- 用户只能查看和编辑自己的简历、项目经验、公司档案、面试记录
- 薪资信息（base_salary, bonus, stock_value）加密存储
- 简历导出时，可选择匿名化（隐藏联系方式、薪资信息）

**R13: 级联删除规则**
- 删除用户 → 级联删除所有简历、项目经验、公司档案
- 删除公司 → 级联删除职位、面试记录、Recruiter、人脉
- 删除职位 → 级联删除面试流程、面试记录
- 删除面试记录 → 级联删除面试问题、面试反馈

---

## 5. API 设计概要

### 5.1 简历管理 API

**基础路径**: `/api/resumes`

| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/resumes` | 获取用户所有简历版本 | User |
| GET | `/api/resumes/{id}` | 获取简历详情（包含所有关联数据） | User |
| POST | `/api/resumes` | 创建新简历版本 | User |
| PUT | `/api/resumes/{id}` | 更新简历基本信息 | User |
| DELETE | `/api/resumes/{id}` | 删除简历版本 | User |
| POST | `/api/resumes/{id}/copy` | 复制简历版本 | User |
| PUT | `/api/resumes/{id}/set-default` | 设置为默认简历 | User |
| POST | `/api/resumes/{id}/export` | 导出简历（PDF/Word/Text） | User |

**子资源 API**:
- `/api/resumes/{id}/experiences` - 工作经历 CRUD
- `/api/resumes/{id}/skills` - 技能列表 CRUD
- `/api/resumes/{id}/education` - 教育背景 CRUD
- `/api/resumes/{id}/certifications` - 培训证书 CRUD

### 5.2 项目经验库 API

**基础路径**: `/api/projects`

| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/projects` | 获取用户所有项目经验 | User |
| GET | `/api/projects/{id}` | 获取项目详情 | User |
| POST | `/api/projects` | 创建项目经验 | User |
| PUT | `/api/projects/{id}` | 更新项目经验 | User |
| DELETE | `/api/projects/{id}` | 删除项目经验 | User |
| GET | `/api/projects/{id}/related-questions` | 获取相关 Behavioral 试题 | User |

### 5.3 人员管理经验库 API

**基础路径**: `/api/management-experiences`

| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/management-experiences` | 获取用户所有管理经验 | User |
| GET | `/api/management-experiences/{id}` | 获取管理经验详情 | User |
| POST | `/api/management-experiences` | 创建管理经验 | User |
| PUT | `/api/management-experiences/{id}` | 更新管理经验 | User |
| DELETE | `/api/management-experiences/{id}` | 删除管理经验 | User |
| GET | `/api/management-experiences/{id}/related-questions` | 获取相关 People Management 试题 | User |

### 5.4 公司与职位管理 API

**基础路径**: `/api/companies`, `/api/job-applications`

**公司 API**:
| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/companies` | 获取用户所有公司 | User |
| GET | `/api/companies/{id}` | 获取公司详情 | User |
| POST | `/api/companies` | 创建公司档案 | User |
| PUT | `/api/companies/{id}` | 更新公司信息 | User |
| DELETE | `/api/companies/{id}` | 删除公司（级联删除） | User |

**职位 API**:
| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/companies/{companyId}/jobs` | 获取公司所有职位 | User |
| GET | `/api/job-applications/{id}` | 获取职位详情 | User |
| POST | `/api/companies/{companyId}/jobs` | 创建职位申请 | User |
| PUT | `/api/job-applications/{id}` | 更新职位信息 | User |
| DELETE | `/api/job-applications/{id}` | 删除职位 | User |
| PUT | `/api/job-applications/{id}/status` | 更新申请状态 | User |

**面试流程 API**:
| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/job-applications/{jobId}/stages` | 获取面试流程 | User |
| POST | `/api/job-applications/{jobId}/stages` | 创建面试阶段 | User |
| PUT | `/api/interview-stages/{id}` | 更新面试阶段 | User |
| DELETE | `/api/interview-stages/{id}` | 删除面试阶段 | User |

**Recruiter API**:
| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/companies/{companyId}/recruiters` | 获取公司所有 Recruiter | User |
| POST | `/api/companies/{companyId}/recruiters` | 创建 Recruiter | User |
| PUT | `/api/recruiters/{id}` | 更新 Recruiter | User |
| DELETE | `/api/recruiters/{id}` | 删除 Recruiter | User |
| GET | `/api/recruiters/{id}/communications` | 获取沟通记录 | User |
| POST | `/api/recruiters/{id}/communications` | 创建沟通记录 | User |

**人脉 API**:
| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/companies/{companyId}/referrals` | 获取公司所有推荐人 | User |
| POST | `/api/companies/{companyId}/referrals` | 创建推荐人 | User |
| PUT | `/api/referrals/{id}` | 更新推荐人 | User |
| DELETE | `/api/referrals/{id}` | 删除推荐人 | User |
| PUT | `/api/referrals/{id}/status` | 更新推荐状态 | User |

### 5.5 定制简历生成 API

**基础路径**: `/api/customized-resumes`

| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| POST | `/api/customized-resumes/generate` | 根据 JD 生成定制简历 | User |
| GET | `/api/customized-resumes/compare` | 对比基础简历 vs 定制简历 | User |

**请求体示例**:
```json
{
  "baseResumeId": 1,
  "jobApplicationId": 5
}
```

### 5.6 面试记录跟踪 API

**基础路径**: `/api/interview-records`

| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/job-applications/{jobId}/interviews` | 获取职位所有面试记录 | User |
| GET | `/api/interview-records/{id}` | 获取面试记录详情 | User |
| POST | `/api/job-applications/{jobId}/interviews` | 创建面试记录 | User |
| PUT | `/api/interview-records/{id}` | 更新面试记录 | User |
| DELETE | `/api/interview-records/{id}` | 删除面试记录 | User |

**面试问题 API**:
| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/interview-records/{recordId}/questions` | 获取面试问题列表 | User |
| POST | `/api/interview-records/{recordId}/questions` | 创建面试问题 | User |
| PUT | `/api/interview-questions/{id}` | 更新面试问题 | User |
| DELETE | `/api/interview-questions/{id}` | 删除面试问题 | User |

**面试反馈 API**:
| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| POST | `/api/interview-records/{recordId}/feedback` | 添加面试反馈 | User |
| PUT | `/api/interview-feedback/{id}` | 更新面试反馈 | User |

### 5.7 面试复盘报告 API

**基础路径**: `/api/interview-analytics`

| Method | Endpoint | 功能 | 权限 |
|--------|---------|------|------|
| GET | `/api/interview-analytics/report` | 生成面试复盘报告 | User |
| GET | `/api/interview-analytics/weak-skills` | 识别薄弱技能 | User |
| GET | `/api/interview-analytics/recommendations` | 推荐学习内容 | User |

---

## 6. UI/UX 设计要点

### 6.1 页面布局原则

**原则 1: 参考 Phase 4-6 的双 Tab 模式**
- 左侧导航树 + 右侧内容区（双 Tab）
- Tab 1: 主要内容（如学习资料、公司信息）
- Tab 2: 辅助内容（如试题库、面试记录）

**原则 2: 响应式设计**
- 桌面端：两栏或三栏布局
- 移动端：单栏布局，可折叠侧边栏

**原则 3: Markdown 支持**
- 所有文本字段支持 Markdown 格式（About, 职责描述, 项目经验等）
- 提供 Markdown 编辑器（带预览功能）

### 6.2 页面设计概要

#### 6.2.1 求职 → 简历 → 基本信息管理

**布局**: 单页表单
- 顶部：简历版本选择器（下拉列表 + "创建新版本"按钮）
- 左侧：导航锚点（About, Contact, Skills, Education, Languages, Hobbies）
- 右侧：表单区域
  - About 部分（Markdown 编辑器）
  - Contact & Links 部分（表单字段）
  - Skills 部分（列表 + "从学习模块导入"按钮）
  - Education 部分（可添加多条）
  - Languages 部分（可添加多条）
  - Hobbies 部分（文本框）
- 底部：保存按钮 + 导出按钮（PDF/Word/Text）

**交互**:
- 点击导航锚点滚动到对应部分
- 点击"从学习模块导入"弹出技能选择 Modal
- 点击"导出"显示格式选择 Modal

---

#### 6.2.2 求职 → 简历 → 项目经验库

**布局**: 列表 + 详情
- 左侧（30%）：项目列表
  - 显示项目名称、时间范围、项目类型
  - 按时间倒序排列
  - "添加新项目"按钮
- 右侧（70%）：项目详情
  - What/When/Who/Why（可折叠）
  - Problem Statement & Challenges（可折叠）
  - How（可折叠）
  - Result（可折叠）
  - 标签和分类
  - 关联的 Behavioral 试题（推荐列表）
  - 编辑按钮 + 删除按钮

**交互**:
- 点击左侧项目，右侧显示详情
- 点击"添加新项目"弹出创建 Modal（分步表单）
- 点击"编辑"弹出编辑 Modal
- 点击"关联的 Behavioral 试题"跳转到试题库

---

#### 6.2.3 求职 → 简历 → 人员管理经验库

**布局**: 列表 + 详情
- 左侧（30%）：管理经验列表
  - 按类型分组（Manage Up, Cross-team, Team Growth）
  - 显示经验名称、时间范围
  - "添加新经验"按钮
- 右侧（70%）：经验详情
  - 背景和挑战
  - 采取的行动
  - 结果和影响
  - 经验教训
  - 关联的 People Management 试题
  - 编辑按钮 + 删除按钮

**交互**:
- 点击左侧经验，右侧显示详情
- 点击"添加新经验"弹出创建 Modal（选择类型 → 填写表单）
- Team Growth 类型显示细分选项（Hiring/High Performer/Low Performer）

---

#### 6.2.4 求职 → 面试 → 公司与职位

**布局**: 三栏
- 左侧（25%）：公司列表
  - 显示公司名称、行业分类
  - 搜索框
  - "添加新公司"按钮
- 中间（40%）：公司详情 + 职位列表
  - 公司信息卡片（简介、文化、链接）
  - Tab 1: 职位列表
    - 显示职位名称、申请状态、发布时间
    - "添加新职位"按钮
  - Tab 2: Recruiter 列表
    - 显示姓名、联系方式
    - "添加 Recruiter"按钮
  - Tab 3: 人脉列表
    - 显示姓名、关系、推荐状态
    - "添加推荐人"按钮
- 右侧（35%）：职位详情 / Recruiter 详情 / 人脉详情
  - 根据中间选中的 Tab 和项目显示对应详情
  - JD（Qualifications + Responsibilities）
  - 面试流程
  - 沟通记录（Recruiter）
  - 推荐状态跟踪（人脉）

**交互**:
- 点击左侧公司，中间显示公司详情和职位列表
- 点击中间职位，右侧显示职位详情
- 点击"添加新职位"弹出 Modal（JD 输入 + 面试流程定义）

---

#### 6.2.5 求职 → 面试 → 定制简历

**布局**: 两栏 + 对比视图
- 左侧（50%）：基础简历
  - 只读显示
- 右侧（50%）：定制简历
  - 可编辑
  - 高亮与基础简历的差异
- 顶部：
  - 选择公司和职位（下拉列表）
  - "生成定制简历"按钮
  - "导出"按钮

**交互**:
- 选择公司和职位后，点击"生成定制简历"
- 系统自动匹配 Skills 和项目，生成新版本
- 用户可在右侧编辑调整
- 点击"导出"选择格式并下载

---

#### 6.2.6 求职 → 面试 → 面试记录

**布局**: 三栏
- 左侧（25%）：职位列表
  - 按公司分组
  - 显示职位名称、申请状态
  - 筛选：按状态筛选
- 中间（40%）：面试记录列表
  - 显示面试时间、阶段、面试官
  - 按时间倒序
  - "添加新面试记录"按钮
- 右侧（35%）：面试详情
  - 面试官信息
  - 问题列表（可展开显示我的回答）
  - 自我评估
  - 面试官反馈
  - 编辑按钮

**交互**:
- 点击左侧职位，中间显示该职位的所有面试记录
- 点击中间面试记录，右侧显示详情
- 点击"添加新面试记录"弹出 Modal（面试官信息 + 问题列表）
- 点击问题可展开，显示我的回答和评分

---

### 6.3 UI 组件复用

**从 Phase 4-6 复用的组件**:
- Markdown 编辑器（`MarkdownEditor.vue`）
- 技能树选择器（`SkillTreeSelector.vue`）
- 日期选择器（`DatePicker.vue`）
- Modal 对话框（`BaseModal.vue`）

**新增组件**:
- `ResumeVersionSelector.vue` - 简历版本选择器
- `SkillImporter.vue` - 从学习模块导入技能
- `ProjectExperienceCard.vue` - 项目经验卡片
- `InterviewStageList.vue` - 面试流程阶段列表
- `InterviewQuestionList.vue` - 面试问题列表
- `CompanyCard.vue` - 公司信息卡片
- `ResumeExportModal.vue` - 简历导出 Modal

---

## 7. 测试场景

### 7.1 简历管理测试

**场景 1: 创建和管理多个简历版本**
- 创建基础简历
- 从基础简历复制创建定制简历
- 设置默认简历
- 删除非默认简历
- 尝试删除默认简历（应提示错误）

**场景 2: 技能导入**
- 点击"从学习模块导入"
- 选择已学习的技能（Phase 2-6）
- 验证技能正确添加到简历
- 编辑导入的技能
- 删除导入的技能

**场景 3: 简历导出**
- 选择简历版本
- 选择导出格式（PDF/Word/Text）
- 下载文件并验证格式正确
- 验证文件名格式正确

### 7.2 项目经验管理测试

**场景 4: 创建技术项目经验**
- 填写 What/When/Who/Why 部分
- 填写 Problem/How/Result 部分
- 添加技术标签
- 关联到工作经历
- 验证项目保存成功

**场景 5: 关联到 Behavioral 试题**
- 查看项目详情
- 验证"相关 Behavioral 试题"推荐列表
- 点击试题跳转到试题库
- 在试题答题页面引用项目经验

### 7.3 公司与职位管理测试

**场景 6: 创建公司和职位**
- 创建公司档案
- 添加公司有用链接
- 创建职位申请
- 填写 JD（Qualifications + Responsibilities）
- 定义面试流程（3-5 个阶段）
- 验证数据保存成功

**场景 7: Recruiter 和人脉管理**
- 添加 Recruiter
- 记录沟通记录
- 添加推荐人
- 更新推荐状态
- 验证数据关联正确

### 7.4 定制简历生成测试

**场景 8: 生成定制简历**
- 选择公司和职位
- 点击"生成定制简历"
- 验证系统自动匹配 Skills 和项目
- 手动调整定制简历
- 对比基础简历 vs 定制简历
- 导出定制简历

### 7.5 面试记录跟踪测试

**场景 9: 记录面试过程**
- 创建面试记录
- 添加面试问题
- 关联到试题库
- 填写自我评估
- 添加面试官反馈
- 验证数据保存成功

**场景 10: 面试结果跟踪**
- 更新职位申请状态为 Offer
- 填写 Offer 详情（薪资信息）
- 更新职位申请状态为 Rejected
- 填写拒绝原因和改进计划
- 查看面试复盘报告
- 验证统计数据正确

### 7.6 集成测试

**场景 11: 完整求职流程**
1. 创建基础简历
2. 总结项目经验
3. 添加目标公司
4. 创建职位申请
5. 生成定制简历
6. 导出定制简历
7. 记录面试过程
8. 更新面试结果
9. 查看复盘报告
10. 制定改进计划

---

## 8. 实施状态

### 8.1 Phase 7.1 实施计划

**阶段 1: 数据库设计与实现** (预计 2-3 小时)
- ✅ 设计 17 张表的表结构
- ✅ 创建 Migration 脚本（V20_create_job_search_tables.sql）
- ✅ 执行迁移并验证

**阶段 2: 后端开发** (预计 6-8 小时)
- Entity 实体类创建（17 个实体）
- Repository 接口创建
- Service 服务层实现（简历、项目、公司、面试）
- Controller API 实现（6 组 API）
- DTO 转换逻辑

**阶段 3: 前端开发** (预计 8-10 小时)
- API 调用方法（6 组 API）
- 6 个主要页面开发：
  1. 基本信息管理（ResumeManagement.vue）
  2. 项目经验库（ProjectExperiences.vue）
  3. 人员管理经验库（ManagementExperiences.vue）
  4. 公司与职位（CompanyJobManagement.vue）
  5. 定制简历（CustomizedResume.vue）
  6. 面试记录（InterviewTracking.vue）
- 可复用组件（8 个新组件）

**阶段 4: 测试与优化** (预计 2-3 小时)
- 单元测试（后端 Service 层）
- 集成测试（完整求职流程）
- UI/UX 优化
- 性能优化（数据库索引、前端缓存）

**阶段 5: 文档更新** (预计 1 小时)
- 更新 CLAUDE.md
- 更新 ARCHITECTURE.md
- 更新 database/schema.sql

**预计总时间**: 19-25 小时

---

## 9. 后续优化方向

### P1 智能化功能（未来扩展）

**F14: JD 技能匹配度分析**
- 使用 NLP 分析 JD Qualifications
- 对比简历 Skills，给出匹配度评分（0-100%）
- 推荐需要补充的技能
- 生成技能差距报告

**F15: 简历优化建议**
- 分析 JD 关键词（高频词汇）
- 推荐调整简历重点（突出关键词）
- 推荐项目经验（与 JD 相关度高）
- 推荐成就量化数据（基于行业标准）

**F16: 高频面试题推荐**
- 基于公司和职位类型推荐试题
- 关联 Phase 3-6 试题库
- 推荐试题难度（Easy/Medium/Hard）
- 生成刷题计划

**F17: 面试准备清单自动生成**
- 根据面试流程自动生成准备清单
- 关联学习内容（Phase 2-6）
- 关联试题库（Phase 3-6）
- 关联项目经验
- 估算准备时间

### P2 高级功能（待定）

**F18: 简历模板管理**
- 提供 5+ 专业简历模板
- 支持自定义模板（CSS/HTML）
- 模板预览和切换

**F19: 面试模拟**
- 基于面试流程生成模拟面试题
- 录制模拟面试视频
- AI 评估面试表现

**F20: 求职进度仪表盘**
- 可视化求职进度（申请数、面试数、Offer 数）
- 求职漏斗分析
- 时间线视图
- 目标达成率

---

## 10. 总结

Phase 7 求职管理模块是 Growing 应用的最后一个核心模块，实现了"学习 → 实战 → 求职 → 反馈 → 改进"的完整闭环。

**核心价值**:
1. **简历管理** - 多版本、定制化、快速导出
2. **经验库** - 结构化项目和人员管理经验，支持 Behavioral 面试
3. **公司档案** - 系统化管理目标公司、职位、Recruiter、人脉
4. **面试跟踪** - 详细记录面试过程，关联试题库，支持复盘分析
5. **深度集成** - 与 Phase 2-6 学习模块深度集成，实现数据复用

**关键特性**:
- 17 张表，覆盖求职全流程
- 6 个页面，UI/UX 参考 Phase 4-6 最佳实践
- 6 组 API，RESTful 设计
- Markdown 支持，所有文本字段支持富文本编辑
- 隐私安全，薪资信息加密存储，支持简历匿名化

**下一步**: 确认需求无误后，开始数据库设计和后端开发。
