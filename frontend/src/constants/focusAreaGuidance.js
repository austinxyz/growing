/**
 * Focus Area guidance templates for project experience editing
 * Each Focus Area has contextual tips for different tabs (4W, Problem, Solution, Result)
 */

export const FOCUS_AREA_GUIDANCE = {
  // Leadership Focus Area
  'Leadership': {
    icon: '🎯',
    what: '你在项目中担任什么领导角色？管理多少人？',
    problem: '团队面临什么困难？你如何发现并定义问题？',
    solution: '你如何制定计划并推动执行？如何激励团队？',
    result: '团队效率提升了多少？项目提前/按时交付了吗？'
  },

  // Conflict Resolution
  'Conflict Resolution': {
    icon: '🤝',
    what: '项目背景中是否涉及不同团队的利益冲突？',
    problem: '遇到了哪些分歧和冲突？涉及哪些利益方？',
    solution: '你用什么方法化解冲突？如何平衡各方诉求？',
    result: '冲突解决后，团队合作改善了多少？'
  },

  // Cross-team Communication
  'Cross-team Communication': {
    icon: '🌐',
    what: '项目涉及几个团队？跨部门协作情况？',
    problem: '跨团队协作中遇到什么沟通障碍？',
    solution: '你建立了什么沟通机制？如何确保信息同步？',
    result: '沟通效率提升？减少了多少返工和延迟？'
  },

  // Technical Mentoring
  'Technical Mentoring': {
    icon: '👨‍🏫',
    what: '你指导了多少初级工程师？他们的成长如何？',
    problem: '团队技能差距是什么？新人遇到什么困难？',
    solution: '你提供了什么培训和指导？建立了什么机制？',
    result: '新人成长如何？晋升/独立承担工作的比例？'
  },

  // Stakeholder Management
  'Stakeholder Management': {
    icon: '💼',
    what: '项目涉及哪些利益相关方？他们的诉求是什么？',
    problem: '不同利益方的期望如何冲突？优先级如何权衡？',
    solution: '你如何管理各方期望？用什么方法达成共识？',
    result: '利益相关方满意度如何？是否达成了各方目标？'
  },

  // Negotiation & Persuasion
  'Negotiation & Persuasion': {
    icon: '💬',
    what: '项目需要争取哪些资源或支持？',
    problem: '遇到了什么阻力或反对意见？',
    solution: '你如何说服他人接受你的方案？用了哪些谈判技巧？',
    result: '最终争取到了哪些资源？达成了什么妥协？'
  },

  // Decision Making Under Uncertainty
  'Decision Making Under Uncertainty': {
    icon: '⚖️',
    what: '项目中有哪些关键决策点？当时的不确定性是什么？',
    problem: '面对不完整信息时，如何评估风险？',
    solution: '你用什么框架做决策？如何在不确定性下行动？',
    result: '决策的结果如何？是否达到了预期目标？'
  },

  // Project Planning & Execution
  'Project Planning & Execution': {
    icon: '📋',
    what: '项目范围和里程碑是什么？如何拆解任务？',
    problem: '计划执行中遇到了什么偏差？',
    solution: '你如何调整计划应对变化？如何跟踪进度？',
    result: '项目按时交付了吗？成本控制如何？'
  },

  // Risk Management
  'Risk Management': {
    icon: '⚠️',
    what: '项目启动时识别了哪些潜在风险？',
    problem: '实际发生了哪些风险？影响有多大？',
    solution: '你提前制定了什么风险缓解措施？如何应对突发问题？',
    result: '风险管理的效果如何？避免了哪些潜在损失？'
  },

  // Process Improvement
  'Process Improvement': {
    icon: '🔄',
    what: '项目开始前，现有流程有什么问题？',
    problem: '流程效率低下的根本原因是什么？',
    solution: '你如何重新设计流程？如何推动团队采纳？',
    result: '流程改进后，效率提升了多少？质量如何变化？'
  },

  // Coaching & Feedback
  'Coaching & Feedback': {
    icon: '📣',
    what: '你为团队成员提供了什么指导？',
    problem: '团队成员在哪些方面需要改进？',
    solution: '你如何给出建设性反馈？如何跟进改进？',
    result: '被指导的成员成长如何？工作质量提升了吗？'
  },

  // Cultural Awareness
  'Cultural Awareness': {
    icon: '🌍',
    what: '项目涉及哪些不同文化背景的团队？',
    problem: '文化差异导致了什么沟通或协作问题？',
    solution: '你如何适应和尊重不同文化？建立了什么共同语言？',
    result: '跨文化协作的效果如何？团队凝聚力提升了吗？'
  },

  // Time Management
  'Time Management': {
    icon: '⏰',
    what: '项目的关键时间节点是什么？',
    problem: '时间压力下，如何平衡质量和速度？',
    solution: '你如何优化时间分配？用了什么时间管理技巧？',
    result: '项目按时交付了吗？工作与生活平衡如何？'
  },

  // Presentation Skills
  'Presentation Skills': {
    icon: '🎤',
    what: '项目中需要做哪些重要汇报？受众是谁？',
    problem: '如何让非技术人员理解技术方案？',
    solution: '你如何准备和优化演示？用了什么可视化方法？',
    result: '汇报的效果如何？获得了哪些支持和认可？'
  }
}

/**
 * Get guidance text for a specific Focus Area and tab
 * @param {string} focusAreaName - Focus Area name
 * @param {string} tab - Tab name: 'what', 'problem', 'solution', 'result'
 * @returns {string} Guidance text
 */
export function getGuidanceText(focusAreaName, tab) {
  const guidance = FOCUS_AREA_GUIDANCE[focusAreaName]
  return guidance ? guidance[tab] : '如何在此部分体现这个技能点？'
}

/**
 * Get icon for a specific Focus Area
 * @param {string} focusAreaName - Focus Area name
 * @returns {string} Icon emoji (defaults to 🎯 if not found)
 */
export function getFocusAreaIcon(focusAreaName) {
  const guidance = FOCUS_AREA_GUIDANCE[focusAreaName]
  return guidance?.icon || '🎯'
}
