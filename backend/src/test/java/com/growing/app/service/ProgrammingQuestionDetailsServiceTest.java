package com.growing.app.service;

import com.growing.app.dto.ProgrammingQuestionDetailsDTO;
import com.growing.app.model.MajorCategory;
import com.growing.app.model.ProgrammingQuestionDetails;
import com.growing.app.model.Question;
import com.growing.app.repository.MajorCategoryRepository;
import com.growing.app.repository.ProgrammingQuestionDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgrammingQuestionDetailsServiceTest {

    @Mock
    private ProgrammingQuestionDetailsRepository programmingQuestionDetailsRepository;

    @Mock
    private MajorCategoryRepository majorCategoryRepository;

    @InjectMocks
    private ProgrammingQuestionDetailsService programmingQuestionDetailsService;

    private Question testQuestion;
    private ProgrammingQuestionDetails testDetails;
    private MajorCategory testCategory;
    private ProgrammingQuestionDetailsDTO testDetailsDTO;

    @BeforeEach
    void setUp() {
        // Create test question
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setQuestionText("Test programming question");

        // Create test major category
        testCategory = new MajorCategory();
        testCategory.setId(1L);
        testCategory.setCategoryName("Data Structures");

        // Create test programming question details
        testDetails = new ProgrammingQuestionDetails();
        testDetails.setId(1L);
        testDetails.setQuestion(testQuestion);
        testDetails.setMajorCategory(testCategory);
        testDetails.setTags(Arrays.asList("array", "two-pointers"));
        testDetails.setSimilarQuestions(Arrays.asList("Q1", "Q2"));
        testDetails.setTimeComplexity("O(n)");
        testDetails.setSpaceComplexity("O(1)");
        testDetails.setDifficulties("理解双指针概念");

        // Create test DTO
        testDetailsDTO = new ProgrammingQuestionDetailsDTO();
        testDetailsDTO.setId(1L);
        testDetailsDTO.setQuestionId(1L);
        testDetailsDTO.setMajorCategoryId(1L);
        testDetailsDTO.setMajorCategoryName("Data Structures");
        testDetailsDTO.setTags(Arrays.asList("array", "two-pointers"));
        testDetailsDTO.setSimilarQuestions(Arrays.asList("Q1", "Q2"));
        testDetailsDTO.setTimeComplexity("O(n)");
        testDetailsDTO.setSpaceComplexity("O(1)");
        testDetailsDTO.setDifficulties("理解双指针概念");
    }

    @Test
    void testGetByQuestionId_Success() {
        // Arrange
        when(programmingQuestionDetailsRepository.findByQuestionId(1L)).thenReturn(Optional.of(testDetails));

        // Act
        ProgrammingQuestionDetailsDTO result = programmingQuestionDetailsService.getByQuestionId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getQuestionId());
        assertEquals("Data Structures", result.getMajorCategoryName());
        assertEquals(2, result.getTags().size());
        assertEquals("array", result.getTags().get(0));
        assertEquals("O(n)", result.getTimeComplexity());
        verify(programmingQuestionDetailsRepository).findByQuestionId(1L);
    }

    @Test
    void testGetByQuestionId_NotFound() {
        // Arrange
        when(programmingQuestionDetailsRepository.findByQuestionId(999L)).thenReturn(Optional.empty());

        // Act
        ProgrammingQuestionDetailsDTO result = programmingQuestionDetailsService.getByQuestionId(999L);

        // Assert
        assertNull(result);
        verify(programmingQuestionDetailsRepository).findByQuestionId(999L);
    }

    @Test
    void testSaveOrUpdate_CreateNew() {
        // Arrange
        when(programmingQuestionDetailsRepository.findByQuestionId(1L)).thenReturn(Optional.empty());
        when(majorCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(programmingQuestionDetailsRepository.save(any(ProgrammingQuestionDetails.class))).thenReturn(testDetails);

        // Act
        ProgrammingQuestionDetailsDTO result = programmingQuestionDetailsService.saveOrUpdate(testQuestion, testDetailsDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Data Structures", result.getMajorCategoryName());
        verify(programmingQuestionDetailsRepository).findByQuestionId(1L);
        verify(majorCategoryRepository).findById(1L);
        verify(programmingQuestionDetailsRepository).save(any(ProgrammingQuestionDetails.class));
    }

    @Test
    void testSaveOrUpdate_UpdateExisting() {
        // Arrange
        when(programmingQuestionDetailsRepository.findByQuestionId(1L)).thenReturn(Optional.of(testDetails));
        when(majorCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(programmingQuestionDetailsRepository.save(any(ProgrammingQuestionDetails.class))).thenReturn(testDetails);

        ProgrammingQuestionDetailsDTO updateDTO = new ProgrammingQuestionDetailsDTO();
        updateDTO.setMajorCategoryId(1L);
        updateDTO.setTags(Arrays.asList("array", "hash-table"));
        updateDTO.setTimeComplexity("O(n)");
        updateDTO.setSpaceComplexity("O(n)");

        // Act
        ProgrammingQuestionDetailsDTO result = programmingQuestionDetailsService.saveOrUpdate(testQuestion, updateDTO);

        // Assert
        assertNotNull(result);
        verify(programmingQuestionDetailsRepository).findByQuestionId(1L);
        verify(majorCategoryRepository).findById(1L);
        verify(programmingQuestionDetailsRepository).save(any(ProgrammingQuestionDetails.class));
    }

    @Test
    void testSaveOrUpdate_MajorCategoryNotFound() {
        // Arrange
        when(programmingQuestionDetailsRepository.findByQuestionId(1L)).thenReturn(Optional.empty());
        when(majorCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        testDetailsDTO.setMajorCategoryId(999L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            programmingQuestionDetailsService.saveOrUpdate(testQuestion, testDetailsDTO);
        });
        verify(majorCategoryRepository).findById(999L);
        verify(programmingQuestionDetailsRepository, never()).save(any(ProgrammingQuestionDetails.class));
    }

    @Test
    void testDelete_Success() {
        // Arrange
        when(programmingQuestionDetailsRepository.findByQuestionId(1L)).thenReturn(Optional.of(testDetails));
        doNothing().when(programmingQuestionDetailsRepository).delete(testDetails);

        // Act
        programmingQuestionDetailsService.delete(testQuestion);

        // Assert
        verify(programmingQuestionDetailsRepository).findByQuestionId(1L);
        verify(programmingQuestionDetailsRepository).delete(testDetails);
    }

    @Test
    void testDelete_NotFound() {
        // Arrange
        when(programmingQuestionDetailsRepository.findByQuestionId(1L)).thenReturn(Optional.empty());

        // Act
        programmingQuestionDetailsService.delete(testQuestion);

        // Assert
        verify(programmingQuestionDetailsRepository).findByQuestionId(1L);
        verify(programmingQuestionDetailsRepository, never()).delete(any(ProgrammingQuestionDetails.class));
    }

    @Test
    void testConvertToDTO() {
        // Act
        ProgrammingQuestionDetailsDTO result = programmingQuestionDetailsService.convertToDTO(testDetails);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getQuestionId());
        assertEquals(1L, result.getMajorCategoryId());
        assertEquals("Data Structures", result.getMajorCategoryName());
        assertEquals(2, result.getTags().size());
        assertEquals("array", result.getTags().get(0));
        assertEquals("two-pointers", result.getTags().get(1));
        assertEquals(2, result.getSimilarQuestions().size());
        assertEquals("O(n)", result.getTimeComplexity());
        assertEquals("O(1)", result.getSpaceComplexity());
        assertEquals("理解双指针概念", result.getDifficulties());
    }

    @Test
    void testConvertToDTO_WithNullMajorCategory() {
        // Arrange
        testDetails.setMajorCategory(null);

        // Act
        ProgrammingQuestionDetailsDTO result = programmingQuestionDetailsService.convertToDTO(testDetails);

        // Assert
        assertNotNull(result);
        assertNull(result.getMajorCategoryId());
        assertNull(result.getMajorCategoryName());
    }
}
