package com.growing.app.service;

import com.growing.app.dto.RecruiterDTO;
import com.growing.app.entity.Recruiter;
import com.growing.app.repository.RecruiterRepository;
import com.growing.app.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruiterService {

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private CompanyRepository companyRepository;

    // 获取用户所有Recruiter
    public List<RecruiterDTO> getAllRecruitersByUserId(Long userId) {
        return recruiterRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 按公司获取
    public List<RecruiterDTO> getRecruitersByCompany(Long userId, Long companyId) {
        return recruiterRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .filter(recruiter -> recruiter.getUserId().equals(userId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取Recruiter详情
    public RecruiterDTO getRecruiterById(Long id, Long userId) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter不存在"));

        // 验证所有权
        if (!recruiter.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此Recruiter");
        }

        return convertToDTO(recruiter);
    }

    // 创建Recruiter
    @Transactional
    public RecruiterDTO createRecruiter(Long userId, RecruiterDTO dto) {
        // 验证公司存在（如果提供）
        if (dto.getCompanyId() != null && !companyRepository.existsById(dto.getCompanyId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在");
        }

        Recruiter recruiter = new Recruiter();
        recruiter.setUserId(userId);
        recruiter.setCompanyId(dto.getCompanyId());
        recruiter.setRecruiterName(dto.getRecruiterName());
        recruiter.setEmail(dto.getEmail());
        recruiter.setPhone(dto.getPhone());
        recruiter.setLinkedinUrl(dto.getLinkedinUrl());
        recruiter.setNotes(dto.getNotes());

        recruiter = recruiterRepository.save(recruiter);
        return convertToDTO(recruiter);
    }

    // 更新Recruiter
    @Transactional
    public RecruiterDTO updateRecruiter(Long id, Long userId, RecruiterDTO dto) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter不存在"));

        // 验证所有权
        if (!recruiter.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此Recruiter");
        }

        // 验证公司存在（如果更换）
        if (dto.getCompanyId() != null && !dto.getCompanyId().equals(recruiter.getCompanyId())) {
            if (!companyRepository.existsById(dto.getCompanyId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在");
            }
            recruiter.setCompanyId(dto.getCompanyId());
        }

        recruiter.setRecruiterName(dto.getRecruiterName());
        recruiter.setEmail(dto.getEmail());
        recruiter.setPhone(dto.getPhone());
        recruiter.setLinkedinUrl(dto.getLinkedinUrl());
        recruiter.setNotes(dto.getNotes());

        recruiter = recruiterRepository.save(recruiter);
        return convertToDTO(recruiter);
    }

    // 删除Recruiter
    @Transactional
    public void deleteRecruiter(Long id, Long userId) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter不存在"));

        // 验证所有权
        if (!recruiter.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此Recruiter");
        }

        recruiterRepository.delete(recruiter);
    }

    // DTO Conversion
    private RecruiterDTO convertToDTO(Recruiter recruiter) {
        RecruiterDTO dto = new RecruiterDTO();
        dto.setId(recruiter.getId());
        dto.setUserId(recruiter.getUserId());
        dto.setCompanyId(recruiter.getCompanyId());

        // Populate company name
        if (recruiter.getCompanyId() != null) {
            companyRepository.findById(recruiter.getCompanyId())
                    .ifPresent(company -> dto.setCompanyName(company.getCompanyName()));
        }

        dto.setRecruiterName(recruiter.getRecruiterName());
        dto.setEmail(recruiter.getEmail());
        dto.setPhone(recruiter.getPhone());
        dto.setLinkedinUrl(recruiter.getLinkedinUrl());
        dto.setNotes(recruiter.getNotes());
        dto.setCreatedAt(recruiter.getCreatedAt());
        dto.setUpdatedAt(recruiter.getUpdatedAt());
        return dto;
    }
}
