package com.growing.app.service;

import com.growing.app.dto.CompanyLinkDTO;
import com.growing.app.entity.CompanyLink;
import com.growing.app.entity.Company;
import com.growing.app.repository.CompanyLinkRepository;
import com.growing.app.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyLinkService {

    @Autowired
    private CompanyLinkRepository companyLinkRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanyLinkDTO> getLinksByCompanyId(Long companyId, Long userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此公司的链接");
        }

        return companyLinkRepository.findByCompanyIdOrderBySortOrder(companyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CompanyLinkDTO createLink(Long userId, CompanyLinkDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为此公司添加链接");
        }

        CompanyLink link = new CompanyLink();
        link.setCompanyId(dto.getCompanyId());
        link.setLinkTitle(dto.getLinkTitle());
        link.setLinkUrl(dto.getLinkUrl());
        link.setNotes(dto.getNotes());
        link.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        link = companyLinkRepository.save(link);
        return convertToDTO(link);
    }

    @Transactional
    public CompanyLinkDTO updateLink(Long id, Long userId, CompanyLinkDTO dto) {
        CompanyLink link = companyLinkRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "链接不存在"));

        Company company = companyRepository.findById(link.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此链接");
        }

        link.setLinkTitle(dto.getLinkTitle());
        link.setLinkUrl(dto.getLinkUrl());
        link.setNotes(dto.getNotes());
        link.setSortOrder(dto.getSortOrder());

        link = companyLinkRepository.save(link);
        return convertToDTO(link);
    }

    @Transactional
    public void deleteLink(Long id, Long userId) {
        CompanyLink link = companyLinkRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "链接不存在"));

        Company company = companyRepository.findById(link.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此链接");
        }

        companyLinkRepository.delete(link);
    }

    private CompanyLinkDTO convertToDTO(CompanyLink link) {
        CompanyLinkDTO dto = new CompanyLinkDTO();
        dto.setId(link.getId());
        dto.setCompanyId(link.getCompanyId());
        dto.setLinkTitle(link.getLinkTitle());
        dto.setLinkUrl(link.getLinkUrl());
        dto.setNotes(link.getNotes());
        dto.setSortOrder(link.getSortOrder());
        dto.setCreatedAt(link.getCreatedAt());
        return dto;
    }
}
