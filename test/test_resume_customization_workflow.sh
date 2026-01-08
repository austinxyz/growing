#!/bin/bash

# Test Resume Customization Workflow
# Verifies the complete workflow: Job Search → Resume Analysis → Decision to Apply

echo "=== Testing Resume Customization Workflow ==="

# Test 1: Check backend endpoints exist
echo -e "\n1. Checking backend endpoints..."
if grep -q "@GetMapping.*job-application" /Users/yanzxu/claude/growing//Users/yanzxu/claude/growing/backend/src/main/java/com/growing/app/controller/ResumeAnalysisController.java && \
   grep -q "@GetMapping.*job/{jobId}" /Users/yanzxu/claude/growing//Users/yanzxu/claude/growing/backend/src/main/java/com/growing/app/controller/ResumeAnalysisController.java; then
    echo "✅ Both endpoints exist:"
    echo "   - GET /api/resume-analysis/job-application/{jobApplicationId} (已申请职位)"
    echo "   - GET /api/resume-analysis/job/{jobId} (未申请职位)"
else
    echo "❌ Endpoints missing"
    exit 1
fi

# Test 2: Check service methods
echo -e "\n2. Checking service methods..."
if grep -q "analyzeResumeByApplication" /Users/yanzxu/claude/growing/backend/src/main/java/com/growing/app/service/ResumeAnalysisService.java && \
   grep -q "analyzeResumeByJob" /Users/yanzxu/claude/growing/backend/src/main/java/com/growing/app/service/ResumeAnalysisService.java && \
   grep -q "performAnalysis" /Users/yanzxu/claude/growing/backend/src/main/java/com/growing/app/service/ResumeAnalysisService.java; then
    echo "✅ Service methods exist:"
    echo "   - analyzeResumeByApplication (for applied jobs)"
    echo "   - analyzeResumeByJob (for unapplied jobs)"
    echo "   - performAnalysis (shared core logic)"
else
    echo "❌ Service methods missing"
    exit 1
fi

# Test 3: Check frontend API
echo -e "\n3. Checking frontend API..."
if grep -q "analyzeResumeByApplication" /Users/yanzxu/claude/growing/frontend/src/api/resumeAnalysisApi.js && \
   grep -q "analyzeResumeByJob" /Users/yanzxu/claude/growing/frontend/src/api/resumeAnalysisApi.js; then
    echo "✅ Frontend API has both methods"
else
    echo "❌ Frontend API incomplete"
    exit 1
fi

# Test 4: Check CompanyJobManagement integration
echo -e "\n4. Checking CompanyJobManagement.vue integration..."
if grep -q "import.*resumeAnalysisApi" /Users/yanzxu/claude/growing/frontend/src/views/job-search/CompanyJobManagement.vue && \
   grep -q "const resumeAnalysis = ref" /Users/yanzxu/claude/growing/frontend/src/views/job-search/CompanyJobManagement.vue && \
   grep -q "loadResumeAnalysis" /Users/yanzxu/claude/growing/frontend/src/views/job-search/CompanyJobManagement.vue && \
   grep -q "getMatchScoreColor" /Users/yanzxu/claude/growing/frontend/src/views/job-search/CompanyJobManagement.vue; then
    echo "✅ CompanyJobManagement.vue has resume analysis"
else
    echo "❌ CompanyJobManagement.vue integration incomplete"
    exit 1
fi

# Test 5: Check resume tab exists
echo -e "\n5. Checking 'Resume' tab in job details..."
if grep -q "{ id: 'resume', name: '定制简历' }" /Users/yanzxu/claude/growing/frontend/src/views/job-search/CompanyJobManagement.vue; then
    echo "✅ Resume tab configured in jobDetailTabs"
else
    echo "❌ Resume tab missing"
    exit 1
fi

# Test 6: Check JobApplicationList still works
echo -e "\n6. Checking JobApplicationList.vue (for applied jobs)..."
if grep -q "analyzeResumeByApplication" /Users/yanzxu/claude/growing/frontend/src/views/job-search/JobApplicationList.vue; then
    echo "✅ JobApplicationList.vue uses correct API (analyzeResumeByApplication)"
else
    echo "❌ JobApplicationList.vue not updated"
    exit 1
fi

echo -e "\n=== All Tests Passed! ✅ ==="

echo -e "\n📊 Complete Workflow Summary:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Step 1: User browses jobs in CompanyJobManagement"
echo "        → Clicks on a job to view details"
echo ""
echo "Step 2: User clicks '定制简历' tab (BEFORE applying)"
echo "        → Clicks '分析简历匹配度' button"
echo "        → Backend: GET /api/resume-analysis/job/{jobId}"
echo "        → Service: analyzeResumeByJob(jobId, userId)"
echo ""
echo "Step 3: System shows match analysis:"
echo "        • Match score (0-100%)"
echo "        • Matched skills (green badges)"
echo "        • Missing skills (red badges)"
echo "        • Strengths & improvements"
echo ""
echo "Step 4: If match score > 70%:"
echo "        → Shows customization suggestions"
echo "        → Keyword optimization"
echo "        → Project experience enhancements"
echo "        → Skill highlighting tips"
echo "        → Structural improvements"
echo ""
echo "Step 5: User decides whether to apply based on analysis"
echo "        → If good match: Apply and track in JobApplicationList"
echo "        → If poor match: Skip or improve skills first"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

echo -e "\n🧪 Manual Testing Steps:"
echo "1. Start backend: cd backend && ./start.sh"
echo "2. Start frontend: cd frontend && npm run dev"
echo "3. Login as austinxu/helloworld"
echo "4. Navigate: Job Search → Company & Job Management"
echo "5. Select a company, then select a job"
echo "6. Click '定制简历' tab (3rd tab)"
echo "7. Click '分析简历匹配度' button"
echo "8. Verify:"
echo "   - Match score displayed with color"
echo "   - Matched/missing skills shown"
echo "   - Strengths and improvements listed"
echo "   - If score > 70%: Customization section appears"

echo -e "\n💡 Key Differences:"
echo "• CompanyJobManagement (未申请): uses /job/{jobId}"
echo "• JobApplicationList (已申请): uses /job-application/{jobApplicationId}"
echo "• Both share the same core analysis logic (performAnalysis)"
