package com.growing.app.service;

import com.growing.app.dto.ResumeCertificationDTO;
import com.growing.app.entity.ResumeCertification;
import com.growing.app.entity.Resume;
import com.growing.app.repository.ResumeCertificationRepository;
import com.growing.app.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeCertificationService {

    @Autowired
    private ResumeCertificationRepository resumeCertificationRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    public List<ResumeCertificationDTO> getCertificationsByResumeId(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此简历的证书");
        }

        return resumeCertificationRepository.findByResumeIdOrderBySortOrder(resumeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResumeCertificationDTO createCertification(Long userId, ResumeCertificationDTO dto) {
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此简历添加证书");
        }

        ResumeCertification certification = new ResumeCertification();
        certification.setResumeId(dto.getResumeId());
        certification.setCertName(dto.getCertName());
        certification.setIssuer(dto.getIssuer());
        certification.setIssueDate(dto.getIssueDate());
        certification.setExpiryDate(dto.getExpiryDate());
        certification.setCertUrl(dto.getCertUrl());
        certification.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        certification = resumeCertificationRepository.save(certification);
        return convertToDTO(certification);
    }

    @Transactional
    public ResumeCertificationDTO updateCertification(Long id, Long userId, ResumeCertificationDTO dto) {
        ResumeCertification certification = resumeCertificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "证书不存在"));

        Resume resume = resumeRepository.findById(certification.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此证书");
        }

        certification.setCertName(dto.getCertName());
        certification.setIssuer(dto.getIssuer());
        certification.setIssueDate(dto.getIssueDate());
        certification.setExpiryDate(dto.getExpiryDate());
        certification.setCertUrl(dto.getCertUrl());
        certification.setSortOrder(dto.getSortOrder());

        certification = resumeCertificationRepository.save(certification);
        return convertToDTO(certification);
    }

    @Transactional
    public void deleteCertification(Long id, Long userId) {
        ResumeCertification certification = resumeCertificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "证书不存在"));

        Resume resume = resumeRepository.findById(certification.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));

        if (!resume.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此证书");
        }

        resumeCertificationRepository.delete(certification);
    }

    private ResumeCertificationDTO convertToDTO(ResumeCertification certification) {
        ResumeCertificationDTO dto = new ResumeCertificationDTO();
        dto.setId(certification.getId());
        dto.setResumeId(certification.getResumeId());
        dto.setCertName(certification.getCertName());
        dto.setIssuer(certification.getIssuer());
        dto.setIssueDate(certification.getIssueDate());
        dto.setExpiryDate(certification.getExpiryDate());
        dto.setCertUrl(certification.getCertUrl());
        dto.setSortOrder(certification.getSortOrder());
        dto.setCreatedAt(certification.getCreatedAt());
        return dto;
    }
}
