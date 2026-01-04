package com.growing.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

// From CompanyLink entity
@Data
public class CompanyLinkDTO {
    private Long id;
    private Long companyId;
    private String linkTitle;
    private String linkUrl;
    private String notes;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
