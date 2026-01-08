#!/bin/bash
echo "=== Testing Resume Customization Workflow ==="

# Test 1
echo -e "\n1. Backend endpoints..."
grep -q "@GetMapping.*job/{jobId}" backend/src/main/java/com/growing/app/controller/ResumeAnalysisController.java && echo "✅ Job endpoint exists" || exit 1

# Test 2
echo -e "\n2. Service methods..."
grep -q "analyzeResumeByJob" backend/src/main/java/com/growing/app/service/ResumeAnalysisService.java && echo "✅ Service method exists" || exit 1

# Test 3
echo -e "\n3. Frontend API..."
grep -q "analyzeResumeByJob" frontend/src/api/resumeAnalysisApi.js && echo "✅ Frontend API exists" || exit 1

# Test 4
echo -e "\n4. CompanyJobManagement..."
grep -q "loadResumeAnalysis" frontend/src/views/job-search/CompanyJobManagement.vue && echo "✅ Integration complete" || exit 1

echo -e "\n✅ All tests passed!"
echo -e "\n📊 Workflow:"
echo "1. Browse jobs in CompanyJobManagement"
echo "2. Click '定制简历' tab (BEFORE applying)"
echo "3. Click '分析简历匹配度'"
echo "4. View match score and customization suggestions"
echo "5. Decide whether to apply based on analysis"
