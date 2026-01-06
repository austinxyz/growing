package com.growing.app.service;

import com.growing.app.dto.CompanyDTO;
import com.growing.app.dto.CompanyLinkDTO;
import com.growing.app.entity.Company;
import com.growing.app.entity.CompanyLink;
import com.growing.app.repository.CompanyRepository;
import com.growing.app.repository.CompanyLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyLinkRepository companyLinkRepository;

    // 获取用户所有公司
    public List<CompanyDTO> getAllCompaniesByUserId(Long userId) {
        return companyRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 按行业获取
    public List<CompanyDTO> getCompaniesByIndustry(Long userId, String industry) {
        return companyRepository.findByUserIdAndIndustryOrderByCreatedAtDesc(userId, industry).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 获取公司详情（包含links）
    public CompanyDTO getCompanyById(Long id, Long userId) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // 验证所有权
        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此公司");
        }

        CompanyDTO dto = convertToDTO(company);

        // DTO Completeness Checklist: all collections populated
        dto.setLinks(companyLinkRepository.findByCompanyIdOrderBySortOrder(id).stream()
                .map(this::convertToLinkDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    // 创建公司
    @Transactional
    public CompanyDTO createCompany(Long userId, CompanyDTO dto) {
        Company company = new Company();
        company.setUserId(userId);
        company.setCompanyName(dto.getCompanyName());
        company.setCompanyDescription(dto.getCompanyDescription());
        company.setCompanyCulture(dto.getCompanyCulture());
        company.setLocation(dto.getLocation());
        company.setCompanySize(dto.getCompanySize());
        company.setIndustry(dto.getIndustry());

        company = companyRepository.save(company);
        return convertToDTO(company);
    }

    // 更新公司
    @Transactional
    public CompanyDTO updateCompany(Long id, Long userId, CompanyDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // 验证所有权
        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此公司");
        }

        company.setCompanyName(dto.getCompanyName());
        company.setCompanyDescription(dto.getCompanyDescription());
        company.setCompanyCulture(dto.getCompanyCulture());
        company.setLocation(dto.getLocation());
        company.setCompanySize(dto.getCompanySize());
        company.setIndustry(dto.getIndustry());

        company = companyRepository.save(company);
        return convertToDTO(company);
    }

    // 删除公司（cascade delete所有关联资源）
    @Transactional
    public void deleteCompany(Long id, Long userId) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // 验证所有权
        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此公司");
        }

        companyRepository.delete(company);
    }

    // --- Company Links ---

    // 获取公司所有链接
    public List<CompanyLinkDTO> getCompanyLinks(Long companyId, Long userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // 验证所有权
        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权访问此公司");
        }

        return companyLinkRepository.findByCompanyIdOrderBySortOrder(companyId).stream()
                .map(this::convertToLinkDTO)
                .collect(Collectors.toList());
    }

    // 创建公司链接
    @Transactional
    public CompanyLinkDTO createCompanyLink(Long companyId, Long userId, CompanyLinkDTO dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // 验证所有权
        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此公司");
        }

        CompanyLink link = new CompanyLink();
        link.setCompanyId(companyId);
        link.setLinkTitle(dto.getLinkTitle());
        link.setLinkUrl(dto.getLinkUrl());
        link.setNotes(dto.getNotes());
        link.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        link = companyLinkRepository.save(link);
        return convertToLinkDTO(link);
    }

    // 更新公司链接
    @Transactional
    public CompanyLinkDTO updateCompanyLink(Long companyId, Long linkId, Long userId, CompanyLinkDTO dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // 验证所有权
        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此公司");
        }

        CompanyLink link = companyLinkRepository.findById(linkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "链接不存在"));

        // 验证链接属于该公司
        if (!link.getCompanyId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "链接不属于该公司");
        }

        link.setLinkTitle(dto.getLinkTitle());
        link.setLinkUrl(dto.getLinkUrl());
        link.setNotes(dto.getNotes());
        link.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : link.getSortOrder());

        link = companyLinkRepository.save(link);
        return convertToLinkDTO(link);
    }

    // 删除公司链接
    @Transactional
    public void deleteCompanyLink(Long companyId, Long linkId, Long userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公司不存在"));

        // 验证所有权
        if (!company.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改此公司");
        }

        CompanyLink link = companyLinkRepository.findById(linkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "链接不存在"));

        // 验证链接属于该公司
        if (!link.getCompanyId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "链接不属于该公司");
        }

        companyLinkRepository.delete(link);
    }

    // DTO Conversion
    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setUserId(company.getUserId());
        dto.setCompanyName(company.getCompanyName());
        dto.setCompanyDescription(company.getCompanyDescription());
        dto.setCompanyCulture(company.getCompanyCulture());
        dto.setLocation(company.getLocation());
        dto.setCompanySize(company.getCompanySize());
        dto.setIndustry(company.getIndustry());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        return dto;
    }

    private CompanyLinkDTO convertToLinkDTO(CompanyLink link) {
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
