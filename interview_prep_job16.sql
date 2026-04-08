-- Interview Preparation Todos for job_id=16 (Tech Lead at AI fintech company)
-- Stages: 14=Recruiter Screen, 15=Technical Interview, 16=Leadership & Behavioral, 17=CTO Final Round

-- Stage 1 (Recruiter Screen, stage_id=14)
INSERT INTO interview_preparation_todos (interview_stage_id, user_id, title, description, todo_type, source, priority, order_index, is_completed) VALUES

(14, NULL, 'Career Narrative: Platform Engineering to Tech Lead',
'Craft a compelling 2-minute story:\n- 20+ years building eBay cloud infra (1M+ pods, 5000+ apps)\n- Why now: excited to work at smaller scale with higher ownership, get back to hands-on coding\n- AI fintech angle: built 6 Claude-based AI tools, spec-driven development, MCP server\n- Full-stack credibility: growing app (Vue3 + Spring Boot + MySQL) as personal project\n- Player-coach appeal: mentored engineers while staying technical',
'Skill', 'AI', 1, 1, 0),

(14, NULL, 'Project: Cloud Native Migration (Overview for Recruiter)',
'30-second elevator pitch for recruiter:\n- Led migration of 5000+ eBay apps from VMs to Kubernetes\n- Highlight: architectural thinking, cross-team coordination, measurable outcomes (75% deployment time reduction)\n- Connection to this role: same first-principles architecture approach for fintech product\n- Preparation: practice 30-sec, 2-min, and 10-min versions',
'Project', 'AI', 2, 2, 0),

(14, NULL, 'Management: Yiran Growing Story (Coaching for Recruiter)',
'Brief version of coaching success for recruiter:\n- Senior engineer to Staff promotion\n- Your approach: identify gaps early, create opportunities, regular feedback\n- Connection to raise the engineering bar through mentorship in JD\n- Prep: 1-minute version for recruiter screen',
'Management', 'AI', 2, 3, 0),

(14, NULL, 'Skill: Adaptability - Why startup vs eBay?',
'Prepare honest, compelling answer:\n- After 20+ years at eBay, ready for high-ownership environment\n- Startup appeal: build process from scratch (not inherit legacy), see direct product impact\n- In-office culture: comfortable with 5-day in-office, prefer in-person collaboration\n- Address concern: can someone from large company thrive in startup?\n- Yes - because you have always operated with startup mindset within eBay (innovation programs, building from scratch)',
'Skill', 'AI', 2, 4, 0),

(14, NULL, 'Research: Company and Product Deep Dive',
'Before recruiter screen:\n- Understand what the company builds (AI fintech product)\n- What is their tech stack beyond TypeScript/Node/Next.js/React/Prisma?\n- What stage: seed/Series A/B? Team size?\n- Recent news, funding, product direction\n- Prepare 2-3 smart questions for recruiter about role scope and team',
'Skill', 'AI', 1, 5, 0);

-- Stage 2 (Technical Interview, stage_id=15)
INSERT INTO interview_preparation_todos (interview_stage_id, user_id, title, description, todo_type, source, priority, order_index, is_completed) VALUES

(15, NULL, 'TypeScript and Node.js Hands-on Practice (Critical Gap)',
'CRITICAL GAP: JD requires TypeScript/Node/Next.js/React/Prisma but your experience is primarily K8s/platform.\nPractice plan:\n- TypeScript: type system, generics, utility types, decorators\n- Node.js: async/await patterns, event loop, streams, error handling\n- Prisma: schema definition, migrations, query patterns, transactions\n- Build a small CRUD API in Node+Prisma to demonstrate comfort\n- Leverage the growing app for examples of full-stack thinking',
'Skill', 'AI', 1, 1, 0),

(15, NULL, 'Project: Cloud Native Migration (Technical Architecture Deep Dive)',
'Architecture walk-through preparation:\n- K8s operator pattern: custom controllers, admission webhooks - explain the why\n- Migration workflow: preparation, workload creation, traffic switch, baking, decommission\n- Observability: metrics tracked, dashboard design, alerting strategy\n- Key technical decisions: why operator pattern? how handled rollback?\n- Prepare whiteboard-ready diagram of the migration architecture\n- Metrics to cite: DORA improvement 75% deployment time reduction, 2000+ apps migrated',
'Project', 'AI', 1, 2, 0),

(15, NULL, 'Project: Automated Cluster Lifecycle (Automation from First Principles)',
'Technical depth applicable to startup context:\n- How you designed automation contracts between teams\n- Phased approach: decommission first, then build, then tech refresh - and why this order\n- API design for capacity team self-service capability\n- Connection to role: build process not inherit it - same thinking applies to fintech CI/CD and deployment pipelines\n- Metrics: cluster build time from weeks to several days',
'Project', 'AI', 2, 3, 0),

(15, NULL, 'System Design: Fintech Platform from First Principles',
'Prepare to architect a fintech core system:\n- Payments/ledger system: double-entry accounting, idempotency, audit trails\n- Auth: JWT, OAuth2, fintech compliance considerations\n- API design: REST patterns for fintech, rate limiting, versioning strategy\n- Observability: what to instrument first in a startup context\n- Data modeling: Prisma schema for common fintech patterns\n- Scale consideration: design for growth but do not over-engineer at startup stage',
'Skill', 'AI', 1, 4, 0),

(15, NULL, 'Project: API Server Reliability (SRE Practices to Product Quality)',
'Translate K8s SRE experience to product engineering:\n- Reliability improvement from under 90% to 99% in 2-3 months\n- SLO definition, observability dashboard design, runbook creation\n- For startup: how you would establish reliability culture from day one\n- Metrics: MTTD/MTTR improvement, incident count reduction from daily to near-zero\n- Connection: raise engineering bar through PR reviews includes reliability standards',
'Project', 'AI', 2, 5, 0),

(15, NULL, 'Coding Exercise Prep: TypeScript Patterns',
'Practice common interview coding patterns in TypeScript:\n- Async/await with proper error handling patterns\n- Promise.all, Promise.race, Promise.allSettled use cases\n- TypeScript generics for reusable utilities\n- REST API endpoint implementation (Express/Fastify style)\n- Database query optimization thinking with Prisma and PostgreSQL\n- If asked for React: hooks, state management, async data fetching patterns',
'Skill', 'AI', 2, 6, 0);

-- Stage 3 (Leadership & Behavioral, stage_id=16)
INSERT INTO interview_preparation_todos (interview_stage_id, user_id, title, description, todo_type, source, priority, order_index, is_completed) VALUES

(16, NULL, 'Management: Yiran Growing (Full STARL - Primary Coaching Story)',
'Full STARL narrative for raise the engineering bar through mentorship:\n- S: Senior engineer Yiran had potential for Staff but had gaps in cross-team influence and strategic thinking\n- T: As engineering manager, needed to develop him to Staff level while keeping K8s upgrade project on track\n- A: Identified specific gaps early, adjusted team assignments to expose him to cross-team work, gave architecture committee responsibility, provided regular structured feedback\n- R: Successfully promoted to Staff; K8s upgrade delivered on time; now coaches junior team members\n- L: Identify gaps early, create real opportunities not just feedback, adjust team structure to enable growth\n- Metrics: Promotion achieved within target timeline, project delivery success rate maintained',
'Management', 'AI', 1, 1, 0),

(16, NULL, 'Management: Conflict with Manager (CTO Right-Hand Partner Story)',
'JD says partner directly with the CTO as a right-hand decision-maker:\n- S: Manager focused on resource efficiency, team concerned about site reliability for public L7 traffic apps\n- T: Had to resolve disagreement while finding solution achieving both goals\n- A: Acknowledged manager valid priorities, investigated alternatives, collaborated with network team, developed two-phase workflow compromise\n- R: Both goals achieved - AZ efficiency maintained and public L7 app support within one month\n- L: When in conflict with leadership, acknowledge their POV first, then bring solution options not just problems\n- For this role: how you handle disagreements with CTO on technical direction',
'Management', 'AI', 1, 2, 0),

(16, NULL, 'Management: Hire Dublin Engineers (Team Building Story)',
'For build process not inherit it and future team scaling:\n- Prepare end-to-end hiring process story\n- How you structured interviews, what you look for in engineers\n- Onboarding process you designed including documentation and training\n- Connection: At a startup Tech Lead, you may help the CTO build the engineering team\n- Also highlight: developed interviewer library and structured documentation at eBay',
'Management', 'AI', 2, 3, 0),

(16, NULL, 'Project: Isolated Environment Setup (High-Pressure Ambiguous Situation)',
'For behavioral questions about working with unknowns and hard deadlines:\n- S: DoJ compliance deadline - prevent covered persons from accessing PII in staging and production\n- T: Three workstreams simultaneously: new environments, ownership transfer, business continuity\n- A: War room coordination with daily syncs, AI-generated analysis dashboards, on-site travel, targeted hiring\n- R: SDDZ/DCPX clusters delivered within 3 months, thousands of ownership transfers automated, zero site incidents\n- L: Break ambiguous goals into parallel workstreams, data-driven prioritization, AI augmentation\n- Startup relevance: high-stakes deadline execution, operating with unknowns and changing requirements',
'Project', 'AI', 2, 4, 0),

(16, NULL, 'Skill: Process Building from Scratch Examples',
'Prepare examples for build process not inherit it:\n1. SRE practices: established SLOs, runbooks, incident retrospectives for team with no manager for 6 months\n2. K8s upgrade cadence: created standard process across 100+ clusters\n3. CI/CD pipeline standards: established Tekton pipeline patterns adopted across 5000+ apps\n4. Interview and hiring process: created interviewer library and structured onboarding documentation\n- For each: describe the before state, what you built, and measurable improvement',
'Skill', 'AI', 2, 5, 0),

(16, NULL, 'Skill: Collaboration and Stakeholder Management',
'JD: high-ownership environment - prepare multi-stakeholder examples:\n- Cloud Velocity Program: coordinated 18+ teams across cloud, platform, CD, security, and application domains\n- Mediation example: between CD pipeline team and security team on conflicting requirements\n- Communication channels: regular syncs, on-demand Slack, formal design documents\n- For fintech startup: working directly with product, CTO, and potentially customers\n- Key message: you bring structure without bureaucracy',
'Skill', 'AI', 2, 6, 0);

-- Stage 4 (CTO Final Round, stage_id=17)
INSERT INTO interview_preparation_todos (interview_stage_id, user_id, title, description, todo_type, source, priority, order_index, is_completed) VALUES

(17, NULL, 'Skill: Vision and First 90 Days Plan',
'CTO wants to see strategic thinking and self-awareness:\n- Days 1-30: Deep listen mode - understand product, codebase, team dynamics, biggest pain points\n- Days 31-60: Assess and propose - technical roadmap priorities, quick wins, structural improvements\n- Days 61-90: Execute with team - first architectural decisions, establish engineering norms\n- Key message: Spend time understanding before prescribing. Context matters before solutions.\n- Show humility and confidence: you have patterns from scale, but this is a different context',
'Skill', 'AI', 1, 1, 0),

(17, NULL, 'Skill: Values Alignment - What makes this role the right fit',
'Authentic answer for CTO on why this role:\n- Genuine desire to get back to hands-on coding (50% in codebase is exciting not scary)\n- Build from scratch: greenfield architectural decisions are the most intellectually stimulating work\n- AI and fintech intersection: directly relevant to AI tooling work at eBay\n- Scale of impact: at startup, architectural decisions have visible immediate impact\n- In-office culture: prefer in-person collaboration for high-stakes technical decisions\n- Prepare question: What does the engineering team look like today, and where do you see it in 18 months?',
'Skill', 'AI', 1, 2, 0),

(17, NULL, 'Skill: Technical Strategy - How to architect a fintech product',
'CTO will want to assess your technical judgment:\n- What you would assess in first weeks: current architecture pain points, tech debt, scaling bottlenecks\n- Architectural principles for fintech: security-first, audit trails, idempotency, eventual consistency tradeoffs\n- Platform vs. features: when to invest in platform vs. shipping features at startup stage\n- Tech stack opinion: TypeScript/Node/Next.js/Prisma - what you value, what to watch out for at scale\n- Demonstrate: you have thought deeply about trade-offs, not just pattern-matching from eBay scale',
'Skill', 'AI', 1, 3, 0),

(17, NULL, 'Project: Cloud Velocity Program (Process Building at Scale)',
'Show process-building instincts relevant to startup:\n- Before: developers took up to 1 week to complete CI/CD pipeline\n- Process established: DORA metrics framework, weekly sync cadence, cross-team accountability\n- Result: 95th percentile deployment under 75 minutes, infrastructure reliability maintained above 99%\n- For startup: Establish metrics early, before it becomes painful. The right time to set engineering culture is at 5 engineers not 50.\n- Connection: build process not inherit it - you have done this at scale, now want to do it from scratch',
'Project', 'AI', 2, 4, 0),

(17, NULL, 'Skill: Smart Questions to Ask the CTO',
'Come prepared with thoughtful questions:\n1. What does the current engineering team look like - how many engineers, what seniority levels?\n2. What are the biggest technical challenges you are facing right now?\n3. How do you envision the Tech Lead working with you day-to-day?\n4. What does the product roadmap look like for the next 6-12 months?\n5. What does success look like for this role in the first 3 months?\n6. What process do you most want to establish but have not had bandwidth to do yet?\n7. How does AI factor into your product vision and engineering workflow?\n- Avoid: compensation, hours, vacation - those come after offer',
'Skill', 'AI', 2, 5, 0);
