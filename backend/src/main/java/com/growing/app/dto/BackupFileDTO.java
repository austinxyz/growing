package com.growing.app.dto;

import lombok.Data;

@Data
public class BackupFileDTO {
    private String filename;
    private String filepath;
    private String type;  // daily, weekly, monthly, manual
    private Long size;
    private String timestamp;
    private Double mtime;
}
