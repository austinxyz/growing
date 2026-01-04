package com.growing.app.service;

import com.growing.app.dto.ResumeSkillDTO;
import com.growing.app.entity.ResumeSkill;
import com.growing.app.entity.Resume;
import com.growing.app.repository.ResumeSkillRepository;
import com.growing.app.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeSkillService {

    @Autowired
    private ResumeSkillRepository resumeSkillRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    public List<ResumeSkillDTO> getSkillsByResumeId(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此简历的技能");
        }

        return resumeSkillRepository.findByResumeIdOrderBySortOrder(resumeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResumeSkillDTO createSkill(Long userId, ResumeSkillDTO dto) {
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此简历添加技能");
        }

        ResumeSkill skill = new ResumeSkill();
        skill.setResumeId(dto.getResumeId());
        skill.setSkillCategory(dto.getSkillCategory());
        skill.setSkillName(dto.getSkillName());
        skill.setProficiency(dto.getProficiency());
        skill.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        skill = resumeSkillRepository.save(skill);
        return convertToDTO(skill);
    }

    @Transactional
    public ResumeSkillDTO updateSkill(Long id, Long userId, ResumeSkillDTO dto) {
        ResumeSkill skill = resumeSkillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        Resume resume = resumeRepository.findById(skill.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此技能");
        }

        skill.setSkillCategory(dto.getSkillCategory());
        skill.setSkillName(dto.getSkillName());
        skill.setProficiency(dto.getProficiency());
        skill.setSortOrder(dto.getSortOrder());

        skill = resumeSkillRepository.save(skill);
        return convertToDTO(skill);
    }

    @Transactional
    public void deleteSkill(Long id, Long userId) {
        ResumeSkill skill = resumeSkillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "技能不存在"));

        Resume resume = resumeRepository.findById(skill.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此技能");
        }

        resumeSkillRepository.delete(skill);
    }

    private ResumeSkillDTO convertToDTO(ResumeSkill skill) {
        ResumeSkillDTO dto = new ResumeSkillDTO();
        dto.setId(skill.getId());
        dto.setResumeId(skill.getResumeId());
        dto.setSkillCategory(skill.getSkillCategory());
        dto.setSkillName(skill.getSkillName());
        dto.setProficiency(skill.getProficiency());
        dto.setSortOrder(skill.getSortOrder());
        dto.setCreatedAt(skill.getCreatedAt());
        return dto;
    }
}
