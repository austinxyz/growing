# Phase 2 试题导入 - Focus Area映射说明

## 概览

本阶段处理CSV中剩余的 **14道试题**，这些试题的Focus Area在数据库中不存在，需要映射到已有的Focus Area。

## 映射策略

### 1. System Design (9题) → Systems Design & Architecture

**映射理由**:
- CSV中的 "System Design" 与数据库中的 "Systems Design & Architecture" 本质相同
- 直接映射，无需人工审查

**题目数量**: 9题
**难度分布**: HARD (6题), MEDIUM (3题)

---

### 2. Other (4题) → 根据问题内容分别映射

#### 2.1 "What security trends are you most excited about..." → Continuous Learning

**映射理由**:
- 核心考察点：学习习惯、对行业趋势的关注度
- 期望答案：Shows curiosity, learning habits, awareness of trends
- 最佳匹配：Continuous Learning

**难度**: EASY
**职级**: Early to Peak

---

#### 2.2 "What's a recent technical topic you've taught..." → Clarity

**映射理由**:
- 核心考察点：清晰表达技术概念、教学/指导能力
- 期望答案：Reflects clarity, mentoring approach, adaptability to learning styles
- 最佳匹配：Clarity (沟通清晰度)

**难度**: EASY
**职级**: Early to Mid

---

#### 2.3 "How do you prioritize your tasks when faced with..." → Incident Response

**映射理由**:
- 核心考察点：运维场景下的优先级判断、应急响应
- 期望答案：Describes triage, impact analysis, alert severity
- 最佳匹配：Incident Response

**难度**: MEDIUM
**职级**: Mid

---

#### 2.4 "What would you do in your first 90 days..." → Adaptability

**映射理由**:
- 核心考察点：入职适应能力、规划能力
- 期望答案：Shows planning, ramp-up goals, relationship building
- 最佳匹配：Adaptability (适应新环境)

**难度**: EASY
**职级**: Early to Mid

---

### 3. Collaboration & Communication (1题) → Collaboration

**问题**: "How do you ensure your communication is effective when working on deeply technical..."

**映射理由**:
- 题目同时涉及协作(Collaboration)和沟通(Communication)
- 数据库中两个Focus Area都存在，优先选择 **Collaboration**
- 如果后续需要，可以考虑复制到 Communication 分类

**难度**: MEDIUM
**职级**: Mid

---

## 导入统计

| CSV Focus Area | 映射到 (DB Focus Area) | 题目数量 |
|---------------|----------------------|---------|
| System Design | Systems Design & Architecture | 9 |
| Other | Continuous Learning | 1 |
| Other | Clarity | 1 |
| Other | Incident Response | 1 |
| Other | Adaptability | 1 |
| Collaboration & Communication | Collaboration | 1 |
| **总计** | - | **14** |

## 难度分布

- **EASY**: 3题
- **MEDIUM**: 5题
- **HARD**: 6题

## 执行命令

```bash
# 预览模式（默认）
python3 import/import_phase2.py

# 实际导入
python3 import/import_phase2.py --execute
```

## 注意事项

1. 所有映射的Focus Area都已在数据库中存在，**无需创建新Focus Area**
2. "Other" 分类的映射基于问题内容关键词自动识别
3. 如果未来发现映射不合理，可以通过数据库直接更新 `focus_area_id`
