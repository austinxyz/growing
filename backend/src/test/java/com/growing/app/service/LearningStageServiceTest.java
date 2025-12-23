package com.growing.app.service;

import com.growing.app.dto.LearningStageDTO;
import com.growing.app.model.LearningStage;
import com.growing.app.model.Skill;
import com.growing.app.repository.LearningContentRepository;
import com.growing.app.repository.LearningStageRepository;
import com.growing.app.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningStageServiceTest {

    @Mock
    private LearningStageRepository learningStageRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private LearningContentRepository learningContentRepository;

    @InjectMocks
    private LearningStageService learningStageService;

    private Skill testSkill;
    private LearningStage testStage;
    private LearningStageDTO testStageDTO;

    @BeforeEach
    void setUp() {
        // Create test skill
        testSkill = new Skill();
        testSkill.setId(1L);
        testSkill.setName("Test Skill");

        // Create test learning stage
        testStage = new LearningStage();
        testStage.setId(1L);
        testStage.setSkill(testSkill);
        testStage.setStageName("基础原理");
        testStage.setStageDescription("学习基础原理");
        testStage.setSortOrder(1);

        // Create test DTO
        testStageDTO = new LearningStageDTO();
        testStageDTO.setId(1L);
        testStageDTO.setSkillId(1L);
        testStageDTO.setSkillName("Test Skill");
        testStageDTO.setStageName("基础原理");
        testStageDTO.setStageDescription("学习基础原理");
        testStageDTO.setSortOrder(1);
    }

    @Test
    void testGetStagesBySkillId_Success() {
        // Arrange
        List<LearningStage> stages = Arrays.asList(testStage);
        when(learningStageRepository.findBySkillIdOrderBySortOrder(1L)).thenReturn(stages);
        when(learningContentRepository.countByStageId(1L)).thenReturn(5);

        // Act
        List<LearningStageDTO> result = learningStageService.getStagesBySkillId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("基础原理", result.get(0).getStageName());
        assertEquals(5, result.get(0).getContentCount());
        verify(learningStageRepository).findBySkillIdOrderBySortOrder(1L);
        verify(learningContentRepository).countByStageId(1L);
    }

    @Test
    void testGetStageById_Success() {
        // Arrange
        when(learningStageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(learningContentRepository.countByStageId(1L)).thenReturn(3);

        // Act
        LearningStageDTO result = learningStageService.getStageById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("基础原理", result.getStageName());
        assertEquals(3, result.getContentCount());
        verify(learningStageRepository).findById(1L);
    }

    @Test
    void testGetStageById_NotFound() {
        // Arrange
        when(learningStageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            learningStageService.getStageById(999L);
        });
        verify(learningStageRepository).findById(999L);
    }

    @Test
    void testCreateStage_Success() {
        // Arrange
        when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));
        when(learningStageRepository.existsBySkillIdAndStageName(1L, "基础原理")).thenReturn(false);
        when(learningStageRepository.save(any(LearningStage.class))).thenReturn(testStage);
        when(learningContentRepository.countByStageId(1L)).thenReturn(0);

        // Act
        LearningStageDTO result = learningStageService.createStage(testStageDTO);

        // Assert
        assertNotNull(result);
        assertEquals("基础原理", result.getStageName());
        verify(skillRepository).findById(1L);
        verify(learningStageRepository).existsBySkillIdAndStageName(1L, "基础原理");
        verify(learningStageRepository).save(any(LearningStage.class));
    }

    @Test
    void testCreateStage_DuplicateName() {
        // Arrange
        when(skillRepository.findById(1L)).thenReturn(Optional.of(testSkill));
        when(learningStageRepository.existsBySkillIdAndStageName(1L, "基础原理")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            learningStageService.createStage(testStageDTO);
        });
        verify(learningStageRepository).existsBySkillIdAndStageName(1L, "基础原理");
        verify(learningStageRepository, never()).save(any(LearningStage.class));
    }

    @Test
    void testCreateStage_SkillNotFound() {
        // Arrange
        when(skillRepository.findById(999L)).thenReturn(Optional.empty());
        testStageDTO.setSkillId(999L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            learningStageService.createStage(testStageDTO);
        });
        verify(skillRepository).findById(999L);
        verify(learningStageRepository, never()).save(any(LearningStage.class));
    }

    @Test
    void testUpdateStage_Success() {
        // Arrange
        LearningStageDTO updateDTO = new LearningStageDTO();
        updateDTO.setStageName("Updated Stage");
        updateDTO.setStageDescription("Updated Description");
        updateDTO.setSortOrder(2);

        when(learningStageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(learningStageRepository.existsBySkillIdAndStageNameAndIdNot(1L, "Updated Stage", 1L)).thenReturn(false);
        when(learningStageRepository.save(any(LearningStage.class))).thenReturn(testStage);
        when(learningContentRepository.countByStageId(1L)).thenReturn(2);

        // Act
        LearningStageDTO result = learningStageService.updateStage(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(learningStageRepository).findById(1L);
        verify(learningStageRepository).existsBySkillIdAndStageNameAndIdNot(1L, "Updated Stage", 1L);
        verify(learningStageRepository).save(any(LearningStage.class));
    }

    @Test
    void testUpdateStage_DuplicateName() {
        // Arrange
        LearningStageDTO updateDTO = new LearningStageDTO();
        updateDTO.setStageName("Duplicate Stage");

        when(learningStageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        when(learningStageRepository.existsBySkillIdAndStageNameAndIdNot(1L, "Duplicate Stage", 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            learningStageService.updateStage(1L, updateDTO);
        });
        verify(learningStageRepository).existsBySkillIdAndStageNameAndIdNot(1L, "Duplicate Stage", 1L);
        verify(learningStageRepository, never()).save(any(LearningStage.class));
    }

    @Test
    void testDeleteStage_Success() {
        // Arrange
        when(learningStageRepository.findById(1L)).thenReturn(Optional.of(testStage));
        doNothing().when(learningStageRepository).deleteById(1L);

        // Act
        learningStageService.deleteStage(1L);

        // Assert
        verify(learningStageRepository).findById(1L);
        verify(learningStageRepository).deleteById(1L);
    }

    @Test
    void testDeleteStage_NotFound() {
        // Arrange
        when(learningStageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            learningStageService.deleteStage(999L);
        });
        verify(learningStageRepository).findById(999L);
        verify(learningStageRepository, never()).deleteById(anyLong());
    }
}
