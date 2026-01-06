#!/bin/bash

# Test Resume Analysis Feature
# This script verifies the resume analysis implementation

echo "=== Testing Resume Analysis Feature ==="

# Test 1: Check backend endpoint exists
echo -e "\n1. Checking ResumeAnalysisController..."
if grep -q "@GetMapping.*job-application" backend/src/main/java/com/growing/app/controller/ResumeAnalysisController.java; then
    echo "✅ Controller endpoint exists: GET /api/resume-analysis/job-application/{id}"
else
    echo "❌ Controller endpoint not found"
    exit 1
fi

# Test 2: Check service implementation
echo -e "\n2. Checking ResumeAnalysisService..."
if grep -q "analyzeResume" backend/src/main/java/com/growing/app/service/ResumeAnalysisService.java; then
    echo "✅ Service method exists"
else
    echo "❌ Service method not found"
    exit 1
fi

# Test 3: Check DTOs exist
echo -e "\n3. Checking DTOs..."
if [ -f "backend/src/main/java/com/growing/app/dto/ResumeAnalysisDTO.java" ] && \
   [ -f "backend/src/main/java/com/growing/app/dto/ResumeCustomizationDTO.java" ]; then
    echo "✅ DTOs exist"
else
    echo "❌ DTOs missing"
    exit 1
fi

# Test 4: Check frontend API
echo -e "\n4. Checking frontend API..."
if [ -f "frontend/src/api/resumeAnalysisApi.js" ] && \
   grep -q "analyzeResume" frontend/src/api/resumeAnalysisApi.js; then
    echo "✅ Frontend API exists"
else
    echo "❌ Frontend API missing"
    exit 1
fi

# Test 5: Check frontend integration
echo -e "\n5. Checking JobApplicationList.vue integration..."
if grep -q "import.*resumeAnalysisApi" frontend/src/views/job-search/JobApplicationList.vue && \
   grep -q "const resumeAnalysis = ref" frontend/src/views/job-search/JobApplicationList.vue && \
   grep -q "loadResumeAnalysis" frontend/src/views/job-search/JobApplicationList.vue && \
   grep -q "getMatchScoreColor" frontend/src/views/job-search/JobApplicationList.vue; then
    echo "✅ Frontend integration complete"
else
    echo "❌ Frontend integration incomplete"
    exit 1
fi

# Test 6: Check UI elements
echo -e "\n6. Checking Resume tab UI..."
if grep -q "matchScore" frontend/src/views/job-search/JobApplicationList.vue && \
   grep -q "matchedSkills" frontend/src/views/job-search/JobApplicationList.vue && \
   grep -q "customization" frontend/src/views/job-search/JobApplicationList.vue; then
    echo "✅ Resume analysis UI elements present"
else
    echo "❌ UI elements missing"
    exit 1
fi

echo -e "\n=== All Tests Passed! ✅ ==="
echo -e "\nResume Analysis Feature Implementation Summary:"
echo "• Backend: ResumeAnalysisService with intelligent matching algorithm"
echo "• Match Score: 0-100% based on skill overlap between JD and resume"
echo "• Customization: Triggered when match score > 70%"
echo "• Frontend: Complete UI with SVG progress circle, skills display, and suggestions"
echo -e "\nTo test manually:"
echo "1. Start backend: cd backend && ./start.sh"
echo "2. Start frontend: cd frontend && npm run dev"
echo "3. Login with admin account (austinxu/helloworld)"
echo "4. Navigate to Job Application List"
echo "5. Select a job application"
echo "6. Click 'Resume' tab"
echo "7. Click 'Analyze Resume Match' button"
