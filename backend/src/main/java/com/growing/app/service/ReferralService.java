package com.growing.app.service;

import com.growing.app.dto.ReferralDTO;
import com.growing.app.entity.Referral;
import com.growing.app.repository.ReferralRepository;
import com.growing.app.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReferralService {

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private CompanyRepository companyRepository;

    // 获取用户所有Referral
    public List<ReferralDTO> getAllReferralsByUserId(Long userId) {
        return referralRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 按状态获取
    public List<ReferralDTO> getReferralsByStatus(Long userId, String status) {
        return referralRepository.findByUserIdAndReferralStatusOrderByCreatedAtDesc(userId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 按公司获取
    public List<ReferralDTO> getReferralsByCompany(Long userId, Long companyId) {
        return referralRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .filter(referral -> referral.getUserId().equals(userId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取Referral详情
    public ReferralDTO getReferralById(Long id, Long userId) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral不存在"));

        // 验证所有权
        if (!referral.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此Referral");
        }

        return convertToDTO(referral);
    }

    // 创建Referral
    @Transactional
    public ReferralDTO createReferral(Long userId, ReferralDTO dto) {
        // 验证公司存在
        if (!companyRepository.existsById(dto.getCompanyId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在");
        }

        Referral referral = new Referral();
        referral.setUserId(userId);
        referral.setCompanyId(dto.getCompanyId());
        referral.setReferralName(dto.getReferralName());
        referral.setRelationship(dto.getRelationship());
        referral.setPosition(dto.getPosition());
        referral.setEmail(dto.getEmail());
        referral.setPhone(dto.getPhone());
        referral.setLinkedinUrl(dto.getLinkedinUrl());
        referral.setNotes(dto.getNotes());
        referral.setReferralStatus(dto.getReferralStatus() != null ? dto.getReferralStatus() : "NotRequested");
        referral.setRequestDate(dto.getRequestDate());
        referral.setSubmissionDate(dto.getSubmissionDate());
        referral.setReferralResult(dto.getReferralResult());

        referral = referralRepository.save(referral);
        return convertToDTO(referral);
    }

    // 更新Referral
    @Transactional
    public ReferralDTO updateReferral(Long id, Long userId, ReferralDTO dto) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral不存在"));

        // 验证所有权
        if (!referral.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此Referral");
        }

        // 验证公司存在（如果更换）
        if (!referral.getCompanyId().equals(dto.getCompanyId())) {
            if (!companyRepository.existsById(dto.getCompanyId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在");
            }
            referral.setCompanyId(dto.getCompanyId());
        }

        referral.setReferralName(dto.getReferralName());
        referral.setRelationship(dto.getRelationship());
        referral.setPosition(dto.getPosition());
        referral.setEmail(dto.getEmail());
        referral.setPhone(dto.getPhone());
        referral.setLinkedinUrl(dto.getLinkedinUrl());
        referral.setNotes(dto.getNotes());
        referral.setReferralStatus(dto.getReferralStatus());
        referral.setRequestDate(dto.getRequestDate());
        referral.setSubmissionDate(dto.getSubmissionDate());
        referral.setReferralResult(dto.getReferralResult());

        referral = referralRepository.save(referral);
        return convertToDTO(referral);
    }

    // 删除Referral
    @Transactional
    public void deleteReferral(Long id, Long userId) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral不存在"));

        // 验证所有权
        if (!referral.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此Referral");
        }

        referralRepository.delete(referral);
    }

    // DTO Conversion
    private ReferralDTO convertToDTO(Referral referral) {
        ReferralDTO dto = new ReferralDTO();
        dto.setId(referral.getId());
        dto.setUserId(referral.getUserId());
        dto.setCompanyId(referral.getCompanyId());

        // Populate company name
        companyRepository.findById(referral.getCompanyId())
                .ifPresent(company -> dto.setCompanyName(company.getCompanyName()));

        dto.setReferralName(referral.getReferralName());
        dto.setRelationship(referral.getRelationship());
        dto.setPosition(referral.getPosition());
        dto.setEmail(referral.getEmail());
        dto.setPhone(referral.getPhone());
        dto.setLinkedinUrl(referral.getLinkedinUrl());
        dto.setNotes(referral.getNotes());
        dto.setReferralStatus(referral.getReferralStatus());
        dto.setRequestDate(referral.getRequestDate());
        dto.setSubmissionDate(referral.getSubmissionDate());
        dto.setReferralResult(referral.getReferralResult());
        dto.setCreatedAt(referral.getCreatedAt());
        dto.setUpdatedAt(referral.getUpdatedAt());
        return dto;
    }
}
