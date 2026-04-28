package com.growing.app.controller;

import com.growing.app.dto.LearningContentDTO;
import com.growing.app.dto.StageContentDTO;
import com.growing.app.service.LearningContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // Disable security for tests
class LearningContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningContentService learningContentService;

    private LearningContentDTO testContentDTO;
    private StageContentDTO testStageContentDTO;

    @BeforeEach
    void setUp() {
        // Create test learning content DTO
        testContentDTO = new LearningContentDTO();
        testContentDTO.setId(1L);
        testContentDTO.setStageId(1L);
        testContentDTO.setStageName("基础原理");
        testContentDTO.setFocusAreaId(1L);
        testContentDTO.setFocusAreaName("Test Focus Area");
        testContentDTO.setSkillId(1L);
        testContentDTO.setSkillName("Test Skill");
        testContentDTO.setTitle("Test Content");
        testContentDTO.setContentType("text");
        testContentDTO.setContentText("Test content text");
        testContentDTO.setSortOrder(1);

        // Create test stage content DTO
        testStageContentDTO = new StageContentDTO();
        testStageContentDTO.setStageId(1L);
        testStageContentDTO.setStageName("基础原理");
        testStageContentDTO.setStageDescription("学习基础原理");
        testStageContentDTO.setSortOrder(1);
        testStageContentDTO.setContents(Arrays.asList(testContentDTO));
    }

    @Test
    void testGetContentsByFocusArea_Success() throws Exception {
        // Arrange
        List<StageContentDTO> stageContents = Arrays.asList(testStageContentDTO);
        when(learningContentService.getContentsByFocusArea(1L)).thenReturn(stageContents);

        // Act & Assert
        mockMvc.perform(get("/api/learning-contents/focus-areas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stageId").value(1))
                .andExpect(jsonPath("$[0].stageName").value("基础原理"))
                .andExpect(jsonPath("$[0].contents[0].title").value("Test Content"));
    }

    @Test
    void testGetContentById_Success() throws Exception {
        // Arrange
        when(learningContentService.getContentById(1L)).thenReturn(testContentDTO);

        // Act & Assert
        mockMvc.perform(get("/api/learning-contents/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Content"))
                .andExpect(jsonPath("$.contentType").value("text"));
    }

    @Test
    void testGetAlgorithmTemplates_Success() throws Exception {
        // Arrange
        LearningContentDTO template1 = new LearningContentDTO();
        template1.setId(1L);
        template1.setTitle("Binary Search Template");
        template1.setContentType("algorithm_template");

        LearningContentDTO template2 = new LearningContentDTO();
        template2.setId(2L);
        template2.setTitle("DFS Template");
        template2.setContentType("algorithm_template");

        Page<LearningContentDTO> page = new PageImpl<>(
                Arrays.asList(template1, template2),
                PageRequest.of(0, 10),
                2
        );

        when(learningContentService.getAlgorithmTemplates(isNull(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/learning-contents/algorithm-templates")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Binary Search Template"))
                .andExpect(jsonPath("$.content[1].title").value("DFS Template"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetAlgorithmTemplates_WithSearch() throws Exception {
        // Arrange
        LearningContentDTO template = new LearningContentDTO();
        template.setId(1L);
        template.setTitle("Binary Search Template");
        template.setContentType("algorithm_template");

        Page<LearningContentDTO> page = new PageImpl<>(
                Arrays.asList(template),
                PageRequest.of(0, 10),
                1
        );

        when(learningContentService.getAlgorithmTemplates(eq("Binary"), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/learning-contents/algorithm-templates")
                        .param("search", "Binary")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Binary Search Template"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAlgorithmTemplateById_Success() throws Exception {
        // Arrange
        LearningContentDTO template = new LearningContentDTO();
        template.setId(1L);
        template.setTitle("Binary Search Template");
        template.setContentType("algorithm_template");
        template.setContentText("```java\ncode here\n```");

        when(learningContentService.getAlgorithmTemplateById(1L)).thenReturn(template);

        // Act & Assert
        mockMvc.perform(get("/api/learning-contents/algorithm-templates/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Binary Search Template"))
                .andExpect(jsonPath("$.contentType").value("algorithm_template"));
    }

    @Test
    void testGetContentsByFocusArea_NotFound() throws Exception {
        // Arrange
        when(learningContentService.getContentsByFocusArea(999L))
                .thenThrow(new RuntimeException("Focus area not found"));

        // Act & Assert
        mockMvc.perform(get("/api/learning-contents/focus-areas/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
