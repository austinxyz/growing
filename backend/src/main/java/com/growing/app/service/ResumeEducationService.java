package com.growing.app.service;

import com.growing.app.dto.ResumeEducationDTO;
import com.growing.app.entity.ResumeEducation;
import com.growing.app.entity.Resume;
import com.growing.app.repository.ResumeEducationRepository;
import com.growing.app.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeEducationService {

    @Autowired
    private ResumeEducationRepository resumeEducationRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    public List<ResumeEducationDTO> getEducationByResumeId(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此简历的教育经历");
        }

        return resumeEducationRepository.findByResumeIdOrderBySortOrder(resumeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResumeEducationDTO createEducation(Long userId, ResumeEducationDTO dto) {
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此简历添加教育经历");
        }

        ResumeEducation education = new ResumeEducation();
        education.setResumeId(dto.getResumeId());
        education.setSchoolName(dto.getSchoolName());
        education.setDegree(dto.getDegree());
        education.setMajor(dto.getMajor());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());
        education.setGpa(dto.getGpa());
        education.setCourses(dto.getCourses());
        education.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        education = resumeEducationRepository.save(education);
        return convertToDTO(education);
    }

    @Transactional
    public ResumeEducationDTO updateEducation(Long id, Long userId, ResumeEducationDTO dto) {
        ResumeEducation education = resumeEducationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "教育经历不存在"));

        Resume resume = resumeRepository.findById(education.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此教育经历");
        }

        education.setSchoolName(dto.getSchoolName());
        education.setDegree(dto.getDegree());
        education.setMajor(dto.getMajor());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());
        education.setGpa(dto.getGpa());
        education.setCourses(dto.getCourses());
        education.setSortOrder(dto.getSortOrder());

        education = resumeEducationRepository.save(education);
        return convertToDTO(education);
    }

    @Transactional
    public void deleteEducation(Long id, Long userId) {
        ResumeEducation education = resumeEducationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "教育经历不存在"));

        Resume resume = resumeRepository.findById(education.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此教育经历");
        }

        resumeEducationRepository.delete(education);
    }

    private ResumeEducationDTO convertToDTO(ResumeEducation education) {
        ResumeEducationDTO dto = new ResumeEducationDTO();
        dto.setId(education.getId());
        dto.setResumeId(education.getResumeId());
        dto.setSchoolName(education.getSchoolName());
        dto.setDegree(education.getDegree());
        dto.setMajor(education.getMajor());
        dto.setStartDate(education.getStartDate());
        dto.setEndDate(education.getEndDate());
        dto.setGpa(education.getGpa());
        dto.setCourses(education.getCourses());
        dto.setSortOrder(education.getSortOrder());
        dto.setCreatedAt(education.getCreatedAt());
        return dto;
    }
}
