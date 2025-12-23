package com.growing.app.controller;

import com.growing.app.dto.LearningStageDTO;
import com.growing.app.service.LearningStageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // Disable security for tests
class LearningStageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningStageService learningStageService;

    private LearningStageDTO testStageDTO1;
    private LearningStageDTO testStageDTO2;

    @BeforeEach
    void setUp() {
        // Create test learning stage DTOs
        testStageDTO1 = new LearningStageDTO();
        testStageDTO1.setId(1L);
        testStageDTO1.setSkillId(1L);
        testStageDTO1.setSkillName("Test Skill");
        testStageDTO1.setStageName("基础原理");
        testStageDTO1.setStageDescription("学习基础原理和概念");
        testStageDTO1.setSortOrder(1);
        testStageDTO1.setContentCount(5);

        testStageDTO2 = new LearningStageDTO();
        testStageDTO2.setId(2L);
        testStageDTO2.setSkillId(1L);
        testStageDTO2.setSkillName("Test Skill");
        testStageDTO2.setStageName("实现代码");
        testStageDTO2.setStageDescription("编写代码实现");
        testStageDTO2.setSortOrder(2);
        testStageDTO2.setContentCount(3);
    }

    @Test
    void testGetStagesBySkill_Success() throws Exception {
        // Arrange
        List<LearningStageDTO> stages = Arrays.asList(testStageDTO1, testStageDTO2);
        when(learningStageService.getStagesBySkillId(1L)).thenReturn(stages);

        // Act & Assert
        mockMvc.perform(get("/api/learning-stages/skills/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].stageName").value("基础原理"))
                .andExpect(jsonPath("$[0].contentCount").value(5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].stageName").value("实现代码"))
                .andExpect(jsonPath("$[1].contentCount").value(3));
    }

    @Test
    void testGetStagesBySkill_EmptyList() throws Exception {
        // Arrange
        when(learningStageService.getStagesBySkillId(999L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/learning-stages/skills/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetStageById_Success() throws Exception {
        // Arrange
        when(learningStageService.getStageById(1L)).thenReturn(testStageDTO1);

        // Act & Assert
        mockMvc.perform(get("/api/learning-stages/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stageName").value("基础原理"))
                .andExpect(jsonPath("$.stageDescription").value("学习基础原理和概念"))
                .andExpect(jsonPath("$.contentCount").value(5))
                .andExpect(jsonPath("$.sortOrder").value(1));
    }

    @Test
    void testGetStageById_NotFound() throws Exception {
        // Arrange
        when(learningStageService.getStageById(999L))
                .thenThrow(new RuntimeException("Learning stage not found"));

        // Act & Assert
        mockMvc.perform(get("/api/learning-stages/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetStageById_WithContents() throws Exception {
        // Arrange
        testStageDTO1.setContentCount(10);
        when(learningStageService.getStageById(1L)).thenReturn(testStageDTO1);

        // Act & Assert
        mockMvc.perform(get("/api/learning-stages/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentCount").value(10));
    }

    @Test
    void testGetStagesBySkill_OrderedBySortOrder() throws Exception {
        // Arrange
        // Test that stages are returned in sort order
        when(learningStageService.getStagesBySkillId(1L))
                .thenReturn(Arrays.asList(testStageDTO1, testStageDTO2));

        // Act & Assert
        mockMvc.perform(get("/api/learning-stages/skills/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sortOrder").value(1))
                .andExpect(jsonPath("$[1].sortOrder").value(2));
    }
}
